package com.android.systemui.recents.events.ui.dragndrop;

import com.android.systemui.recents.events.EventBus.AnimatedEvent;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.views.DropTarget;
import com.android.systemui.recents.views.TaskView;

public class DragEndEvent
  extends EventBus.AnimatedEvent
{
  public final DropTarget dropTarget;
  public final Task task;
  public final TaskView taskView;
  
  public DragEndEvent(Task paramTask, TaskView paramTaskView, DropTarget paramDropTarget)
  {
    this.task = paramTask;
    this.taskView = paramTaskView;
    this.dropTarget = paramDropTarget;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\ui\dragndrop\DragEndEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */