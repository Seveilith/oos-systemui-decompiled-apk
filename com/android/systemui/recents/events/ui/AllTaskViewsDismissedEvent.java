package com.android.systemui.recents.events.ui;

import com.android.systemui.recents.events.EventBus.Event;

public class AllTaskViewsDismissedEvent
  extends EventBus.Event
{
  public final int msgResId;
  
  public AllTaskViewsDismissedEvent(int paramInt)
  {
    this.msgResId = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\ui\AllTaskViewsDismissedEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */