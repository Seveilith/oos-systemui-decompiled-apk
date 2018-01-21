package com.android.systemui.recents.events.ui;

import com.android.systemui.recents.events.EventBus.Event;
import com.android.systemui.recents.model.Task;

public class ShowApplicationInfoEvent
  extends EventBus.Event
{
  public final Task task;
  
  public ShowApplicationInfoEvent(Task paramTask)
  {
    this.task = paramTask;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\ui\ShowApplicationInfoEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */