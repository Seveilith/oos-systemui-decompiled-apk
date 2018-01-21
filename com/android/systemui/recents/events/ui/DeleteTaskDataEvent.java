package com.android.systemui.recents.events.ui;

import com.android.systemui.recents.events.EventBus.Event;
import com.android.systemui.recents.model.Task;

public class DeleteTaskDataEvent
  extends EventBus.Event
{
  public final Task task;
  
  public DeleteTaskDataEvent(Task paramTask)
  {
    this.task = paramTask;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\ui\DeleteTaskDataEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */