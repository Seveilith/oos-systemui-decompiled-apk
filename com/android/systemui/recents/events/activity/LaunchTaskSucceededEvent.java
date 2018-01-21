package com.android.systemui.recents.events.activity;

import com.android.systemui.recents.events.EventBus.Event;

public class LaunchTaskSucceededEvent
  extends EventBus.Event
{
  public final int taskIndexFromStackFront;
  
  public LaunchTaskSucceededEvent(int paramInt)
  {
    this.taskIndexFromStackFront = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\activity\LaunchTaskSucceededEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */