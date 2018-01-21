package com.android.systemui.recents.events.activity;

import com.android.systemui.recents.events.EventBus.Event;
import com.android.systemui.recents.model.Task;

public class CancelEnterRecentsWindowAnimationEvent
  extends EventBus.Event
{
  public final Task launchTask;
  
  public CancelEnterRecentsWindowAnimationEvent(Task paramTask)
  {
    this.launchTask = paramTask;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\activity\CancelEnterRecentsWindowAnimationEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */