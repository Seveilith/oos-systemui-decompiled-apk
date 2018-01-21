package com.android.systemui.recents.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import android.util.MutableBoolean;
import com.android.systemui.recents.misc.ReferenceCountedTrigger;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class EventBus
  extends BroadcastReceiver
{
  private static final Comparator<EventHandler> EVENT_HANDLER_COMPARATOR = new Comparator()
  {
    public int compare(EventHandler paramAnonymousEventHandler1, EventHandler paramAnonymousEventHandler2)
    {
      if (paramAnonymousEventHandler1.priority != paramAnonymousEventHandler2.priority) {
        return paramAnonymousEventHandler2.priority - paramAnonymousEventHandler1.priority;
      }
      return Long.compare(paramAnonymousEventHandler2.subscriber.registrationTime, paramAnonymousEventHandler1.subscriber.registrationTime);
    }
  };
  private static EventBus sDefaultBus;
  private static final Object sLock = new Object();
  private HashMap<Class<? extends Event>, ArrayList<EventHandler>> mEventTypeMap = new HashMap();
  private Handler mHandler;
  private HashMap<String, Class<? extends InterprocessEvent>> mInterprocessEventNameMap = new HashMap();
  private HashMap<Class<? extends Object>, ArrayList<EventHandlerMethod>> mSubscriberTypeMap = new HashMap();
  private ArrayList<Subscriber> mSubscribers = new ArrayList();
  
  private EventBus(Looper paramLooper)
  {
    this.mHandler = new Handler(paramLooper);
  }
  
  private boolean findRegisteredSubscriber(Object paramObject, boolean paramBoolean)
  {
    int i = this.mSubscribers.size() - 1;
    while (i >= 0)
    {
      if (((Subscriber)this.mSubscribers.get(i)).getReference() == paramObject)
      {
        if (paramBoolean) {
          this.mSubscribers.remove(i);
        }
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  public static EventBus getDefault()
  {
    if (sDefaultBus == null) {}
    synchronized (sLock)
    {
      if (sDefaultBus == null) {
        sDefaultBus = new EventBus(Looper.getMainLooper());
      }
      return sDefaultBus;
    }
  }
  
  private boolean isValidEventBusHandlerMethod(Method paramMethod, Class<?>[] paramArrayOfClass, MutableBoolean paramMutableBoolean)
  {
    int i = paramMethod.getModifiers();
    if ((Modifier.isPublic(i)) && (Modifier.isFinal(i)) && (paramMethod.getReturnType().equals(Void.TYPE)) && (paramArrayOfClass.length == 1))
    {
      if ((InterprocessEvent.class.isAssignableFrom(paramArrayOfClass[0])) && (paramMethod.getName().startsWith("onInterprocessBusEvent")))
      {
        paramMutableBoolean.value = true;
        return true;
      }
      if ((Event.class.isAssignableFrom(paramArrayOfClass[0])) && (paramMethod.getName().startsWith("onBusEvent")))
      {
        paramMutableBoolean.value = false;
        return true;
      }
    }
    return false;
  }
  
  private static void logWithPid(String paramString)
  {
    Log.d("EventBus", "[" + Process.myPid() + ", u" + UserHandle.myUserId() + "] " + paramString);
  }
  
  /* Error */
  private void processEvent(EventHandler paramEventHandler, Event paramEvent)
  {
    // Byte code:
    //   0: aload_2
    //   1: getfield 220	com/android/systemui/recents/events/EventBus$Event:cancelled	Z
    //   4: ifeq +17 -> 21
    //   7: aload_2
    //   8: getfield 223	com/android/systemui/recents/events/EventBus$Event:trace	Z
    //   11: ifne +4 -> 15
    //   14: return
    //   15: ldc -31
    //   17: invokestatic 227	com/android/systemui/recents/events/EventBus:logWithPid	(Ljava/lang/String;)V
    //   20: return
    //   21: aload_2
    //   22: getfield 223	com/android/systemui/recents/events/EventBus$Event:trace	Z
    //   25: ifne +25 -> 50
    //   28: aload_1
    //   29: getfield 233	com/android/systemui/recents/events/EventHandler:subscriber	Lcom/android/systemui/recents/events/Subscriber;
    //   32: invokevirtual 102	com/android/systemui/recents/events/Subscriber:getReference	()Ljava/lang/Object;
    //   35: astore_3
    //   36: aload_3
    //   37: ifnull +55 -> 92
    //   40: aload_1
    //   41: getfield 237	com/android/systemui/recents/events/EventHandler:method	Lcom/android/systemui/recents/events/EventHandlerMethod;
    //   44: aload_3
    //   45: aload_2
    //   46: invokevirtual 243	com/android/systemui/recents/events/EventHandlerMethod:invoke	(Ljava/lang/Object;Lcom/android/systemui/recents/events/EventBus$Event;)V
    //   49: return
    //   50: new 180	java/lang/StringBuilder
    //   53: dup
    //   54: invokespecial 181	java/lang/StringBuilder:<init>	()V
    //   57: ldc -11
    //   59: invokevirtual 187	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   62: aload_1
    //   63: invokevirtual 246	com/android/systemui/recents/events/EventHandler:toString	()Ljava/lang/String;
    //   66: invokevirtual 187	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   69: invokevirtual 207	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   72: invokestatic 227	com/android/systemui/recents/events/EventBus:logWithPid	(Ljava/lang/String;)V
    //   75: goto -47 -> 28
    //   78: astore_1
    //   79: ldc -78
    //   81: ldc -8
    //   83: aload_1
    //   84: invokevirtual 252	java/lang/IllegalAccessException:getCause	()Ljava/lang/Throwable;
    //   87: invokestatic 256	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   90: pop
    //   91: return
    //   92: ldc -78
    //   94: ldc_w 258
    //   97: invokestatic 260	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   100: pop
    //   101: return
    //   102: astore_1
    //   103: new 262	java/lang/RuntimeException
    //   106: dup
    //   107: aload_1
    //   108: invokevirtual 263	java/lang/reflect/InvocationTargetException:getCause	()Ljava/lang/Throwable;
    //   111: invokespecial 266	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   114: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	115	0	this	EventBus
    //   0	115	1	paramEventHandler	EventHandler
    //   0	115	2	paramEvent	Event
    //   35	10	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   21	28	78	java/lang/IllegalAccessException
    //   28	36	78	java/lang/IllegalAccessException
    //   40	49	78	java/lang/IllegalAccessException
    //   50	75	78	java/lang/IllegalAccessException
    //   92	101	78	java/lang/IllegalAccessException
    //   21	28	102	java/lang/reflect/InvocationTargetException
    //   28	36	102	java/lang/reflect/InvocationTargetException
    //   40	49	102	java/lang/reflect/InvocationTargetException
    //   50	75	102	java/lang/reflect/InvocationTargetException
    //   92	101	102	java/lang/reflect/InvocationTargetException
  }
  
  private void queueEvent(final Event paramEvent)
  {
    ArrayList localArrayList = (ArrayList)this.mEventTypeMap.get(paramEvent.getClass());
    if (localArrayList == null) {
      return;
    }
    int j = 0;
    paramEvent.onPreDispatch();
    localArrayList = (ArrayList)localArrayList.clone();
    int m = localArrayList.size();
    int i = 0;
    if (i < m)
    {
      final EventHandler localEventHandler = (EventHandler)localArrayList.get(i);
      int k = j;
      if (localEventHandler.subscriber.getReference() != null)
      {
        if (!paramEvent.requiresPost) {
          break label117;
        }
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            EventBus.-wrap0(EventBus.this, localEventHandler, paramEvent);
          }
        });
      }
      for (k = 1;; k = j)
      {
        i += 1;
        j = k;
        break;
        label117:
        processEvent(localEventHandler, paramEvent);
      }
    }
    if (j != 0)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          paramEvent.onPostDispatch();
        }
      });
      return;
    }
    paramEvent.onPostDispatch();
  }
  
  private void registerSubscriber(Object paramObject, int paramInt, MutableBoolean paramMutableBoolean)
  {
    if (Thread.currentThread().getId() != this.mHandler.getLooper().getThread().getId()) {
      throw new RuntimeException("Can not register() a subscriber from a non-main thread.");
    }
    if (findRegisteredSubscriber(paramObject, false)) {
      return;
    }
    Subscriber localSubscriber = new Subscriber(paramObject, SystemClock.uptimeMillis());
    paramObject = paramObject.getClass();
    Object localObject1 = (ArrayList)this.mSubscriberTypeMap.get(paramObject);
    if (localObject1 != null)
    {
      paramObject = ((Iterable)localObject1).iterator();
      while (((Iterator)paramObject).hasNext())
      {
        paramMutableBoolean = (EventHandlerMethod)((Iterator)paramObject).next();
        localObject1 = (ArrayList)this.mEventTypeMap.get(paramMutableBoolean.eventType);
        ((ArrayList)localObject1).add(new EventHandler(localSubscriber, paramMutableBoolean, paramInt));
        sortEventHandlersByPriority((List)localObject1);
      }
      this.mSubscribers.add(localSubscriber);
      return;
    }
    ArrayList localArrayList = new ArrayList();
    this.mSubscriberTypeMap.put(paramObject, localArrayList);
    this.mSubscribers.add(localSubscriber);
    MutableBoolean localMutableBoolean = new MutableBoolean(false);
    Method[] arrayOfMethod = ((Class)paramObject).getDeclaredMethods();
    int i = 0;
    int j = arrayOfMethod.length;
    while (i < j)
    {
      Method localMethod = arrayOfMethod[i];
      paramObject = localMethod.getParameterTypes();
      localMutableBoolean.value = false;
      Object localObject2;
      if (isValidEventBusHandlerMethod(localMethod, (Class[])paramObject, localMutableBoolean))
      {
        localObject2 = paramObject[0];
        localObject1 = (ArrayList)this.mEventTypeMap.get(localObject2);
        paramObject = localObject1;
        if (localObject1 == null)
        {
          paramObject = new ArrayList();
          this.mEventTypeMap.put(localObject2, paramObject);
        }
        if (!localMutableBoolean.value) {}
      }
      try
      {
        ((Class)localObject2).getConstructor(new Class[] { Bundle.class });
        this.mInterprocessEventNameMap.put(((Class)localObject2).getName(), localObject2);
        if (paramMutableBoolean != null) {
          paramMutableBoolean.value = true;
        }
        localObject1 = new EventHandlerMethod(localMethod, (Class)localObject2);
        ((ArrayList)paramObject).add(new EventHandler(localSubscriber, (EventHandlerMethod)localObject1, paramInt));
        localArrayList.add(localObject1);
        sortEventHandlersByPriority((List)paramObject);
        i += 1;
      }
      catch (NoSuchMethodException paramObject)
      {
        throw new RuntimeException("Expected InterprocessEvent to have a Bundle constructor");
      }
    }
  }
  
  private void sortEventHandlersByPriority(List<EventHandler> paramList)
  {
    Collections.sort(paramList, EVENT_HANDLER_COMPARATOR);
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println(dumpInternal(paramString));
  }
  
  public String dumpInternal(String paramString)
  {
    String str1 = paramString + "  ";
    String str2 = str1 + "  ";
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append("Registered class types:");
    localStringBuilder.append("\n");
    Object localObject1 = new ArrayList(this.mSubscriberTypeMap.keySet());
    Collections.sort((List)localObject1, new Comparator()
    {
      public int compare(Class<?> paramAnonymousClass1, Class<?> paramAnonymousClass2)
      {
        return paramAnonymousClass1.getSimpleName().compareTo(paramAnonymousClass2.getSimpleName());
      }
    });
    int i = 0;
    Object localObject2;
    while (i < ((ArrayList)localObject1).size())
    {
      localObject2 = (Class)((ArrayList)localObject1).get(i);
      localStringBuilder.append(str1);
      localStringBuilder.append(((Class)localObject2).getSimpleName());
      localStringBuilder.append("\n");
      i += 1;
    }
    localStringBuilder.append(paramString);
    localStringBuilder.append("Event map:");
    localStringBuilder.append("\n");
    paramString = new ArrayList(this.mEventTypeMap.keySet());
    Collections.sort(paramString, new Comparator()
    {
      public int compare(Class<?> paramAnonymousClass1, Class<?> paramAnonymousClass2)
      {
        return paramAnonymousClass1.getSimpleName().compareTo(paramAnonymousClass2.getSimpleName());
      }
    });
    i = 0;
    while (i < paramString.size())
    {
      localObject1 = (Class)paramString.get(i);
      localStringBuilder.append(str1);
      localStringBuilder.append(((Class)localObject1).getSimpleName());
      localStringBuilder.append(" -> ");
      localStringBuilder.append("\n");
      localObject1 = ((ArrayList)this.mEventTypeMap.get(localObject1)).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (EventHandler)((Iterator)localObject1).next();
        Object localObject3 = ((EventHandler)localObject2).subscriber.getReference();
        if (localObject3 != null)
        {
          String str3 = Integer.toHexString(System.identityHashCode(localObject3));
          localStringBuilder.append(str2);
          localStringBuilder.append(localObject3.getClass().getSimpleName());
          localStringBuilder.append(" [0x").append(str3).append(", #").append(((EventHandler)localObject2).priority).append("]");
          localStringBuilder.append("\n");
        }
      }
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramContext = paramIntent.getBundleExtra("interprocess_event_bundle");
    paramIntent = (Class)this.mInterprocessEventNameMap.get(paramIntent.getAction());
    try
    {
      send((Event)paramIntent.getConstructor(new Class[] { Bundle.class }).newInstance(new Object[] { paramContext }));
      return;
    }
    catch (NoSuchMethodException|InvocationTargetException|InstantiationException|IllegalAccessException paramContext)
    {
      Log.e("EventBus", "Failed to create InterprocessEvent", paramContext.getCause());
    }
  }
  
  public void post(Event paramEvent)
  {
    paramEvent.requiresPost = true;
    paramEvent.cancelled = false;
    queueEvent(paramEvent);
  }
  
  public void register(Object paramObject)
  {
    registerSubscriber(paramObject, 1, null);
  }
  
  public void register(Object paramObject, int paramInt)
  {
    registerSubscriber(paramObject, paramInt, null);
  }
  
  public void send(Event paramEvent)
  {
    if (Thread.currentThread().getId() != this.mHandler.getLooper().getThread().getId()) {
      throw new RuntimeException("Can not send() a message from a non-main thread.");
    }
    paramEvent.requiresPost = false;
    paramEvent.cancelled = false;
    queueEvent(paramEvent);
  }
  
  public void sendOntoMainThread(Event paramEvent)
  {
    if (Thread.currentThread().getId() != this.mHandler.getLooper().getThread().getId())
    {
      post(paramEvent);
      return;
    }
    send(paramEvent);
  }
  
  public void unregister(Object paramObject)
  {
    if (Thread.currentThread().getId() != this.mHandler.getLooper().getThread().getId()) {
      throw new RuntimeException("Can not unregister() a subscriber from a non-main thread.");
    }
    if (!findRegisteredSubscriber(paramObject, true)) {
      return;
    }
    Object localObject1 = paramObject.getClass();
    localObject1 = (ArrayList)this.mSubscriberTypeMap.get(localObject1);
    if (localObject1 != null)
    {
      localObject1 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        Object localObject2 = (EventHandlerMethod)((Iterator)localObject1).next();
        localObject2 = (ArrayList)this.mEventTypeMap.get(((EventHandlerMethod)localObject2).eventType);
        int i = ((ArrayList)localObject2).size() - 1;
        while (i >= 0)
        {
          if (((EventHandler)((ArrayList)localObject2).get(i)).subscriber.getReference() == paramObject) {
            ((ArrayList)localObject2).remove(i);
          }
          i -= 1;
        }
      }
    }
  }
  
  public static class AnimatedEvent
    extends EventBus.Event
  {
    private final ReferenceCountedTrigger mTrigger = new ReferenceCountedTrigger();
    
    public void addPostAnimationCallback(Runnable paramRunnable)
    {
      this.mTrigger.addLastDecrementRunnable(paramRunnable);
    }
    
    protected Object clone()
      throws CloneNotSupportedException
    {
      throw new CloneNotSupportedException();
    }
    
    public ReferenceCountedTrigger getAnimationTrigger()
    {
      return this.mTrigger;
    }
    
    void onPostDispatch()
    {
      this.mTrigger.decrement();
    }
    
    void onPreDispatch()
    {
      this.mTrigger.increment();
    }
  }
  
  public static class Event
    implements Cloneable
  {
    boolean cancelled;
    boolean requiresPost;
    boolean trace;
    
    protected Object clone()
      throws CloneNotSupportedException
    {
      Event localEvent = (Event)super.clone();
      localEvent.cancelled = false;
      return localEvent;
    }
    
    void onPostDispatch() {}
    
    void onPreDispatch() {}
  }
  
  public static class InterprocessEvent
    extends EventBus.Event
  {}
  
  public static class ReusableEvent
    extends EventBus.Event
  {
    private int mDispatchCount;
    
    protected Object clone()
      throws CloneNotSupportedException
    {
      throw new CloneNotSupportedException();
    }
    
    void onPostDispatch()
    {
      super.onPostDispatch();
      this.mDispatchCount += 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\EventBus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */