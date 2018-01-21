package com.android.systemui.recents.events;

class EventHandler
{
  EventHandlerMethod method;
  int priority;
  Subscriber subscriber;
  
  EventHandler(Subscriber paramSubscriber, EventHandlerMethod paramEventHandlerMethod, int paramInt)
  {
    this.subscriber = paramSubscriber;
    this.method = paramEventHandlerMethod;
    this.priority = paramInt;
  }
  
  public String toString()
  {
    return this.subscriber.toString(this.priority) + " " + this.method.toString();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\EventHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */