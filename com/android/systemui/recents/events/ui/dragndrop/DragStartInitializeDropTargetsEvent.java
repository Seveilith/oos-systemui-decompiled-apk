package com.android.systemui.recents.events.ui.dragndrop;

import com.android.systemui.recents.events.EventBus.Event;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.views.RecentsViewTouchHandler;
import com.android.systemui.recents.views.TaskView;

public class DragStartInitializeDropTargetsEvent
  extends EventBus.Event
{
  public final RecentsViewTouchHandler handler;
  public final Task task;
  public final TaskView taskView;
  
  public DragStartInitializeDropTargetsEvent(Task paramTask, TaskView paramTaskView, RecentsViewTouchHandler paramRecentsViewTouchHandler)
  {
    this.task = paramTask;
    this.taskView = paramTaskView;
    this.handler = paramRecentsViewTouchHandler;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\ui\dragndrop\DragStartInitializeDropTargetsEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */