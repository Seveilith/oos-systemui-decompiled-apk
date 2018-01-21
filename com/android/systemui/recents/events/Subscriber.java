package com.android.systemui.recents.events;

import java.lang.ref.WeakReference;

class Subscriber
{
  private WeakReference<Object> mSubscriber;
  long registrationTime;
  
  Subscriber(Object paramObject, long paramLong)
  {
    this.mSubscriber = new WeakReference(paramObject);
    this.registrationTime = paramLong;
  }
  
  public Object getReference()
  {
    return this.mSubscriber.get();
  }
  
  public String toString(int paramInt)
  {
    Object localObject = this.mSubscriber.get();
    String str = Integer.toHexString(System.identityHashCode(localObject));
    return localObject.getClass().getSimpleName() + " [0x" + str + ", P" + paramInt + "]";
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\Subscriber.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */