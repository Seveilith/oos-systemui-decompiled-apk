package com.android.systemui.recents.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class EventHandlerMethod
{
  Class<? extends EventBus.Event> eventType;
  private Method mMethod;
  
  EventHandlerMethod(Method paramMethod, Class<? extends EventBus.Event> paramClass)
  {
    this.mMethod = paramMethod;
    this.mMethod.setAccessible(true);
    this.eventType = paramClass;
  }
  
  public void invoke(Object paramObject, EventBus.Event paramEvent)
    throws InvocationTargetException, IllegalAccessException
  {
    this.mMethod.invoke(paramObject, new Object[] { paramEvent });
  }
  
  public String toString()
  {
    return this.mMethod.getName() + "(" + this.eventType.getSimpleName() + ")";
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\EventHandlerMethod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */