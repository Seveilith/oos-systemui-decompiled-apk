package com.android.systemui.recents.events.ui.dragndrop;

import com.android.systemui.recents.events.EventBus.AnimatedEvent;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.TaskStack;
import com.android.systemui.recents.views.TaskView;

public class DragEndCancelledEvent
  extends EventBus.AnimatedEvent
{
  public final TaskStack stack;
  public final Task task;
  public final TaskView taskView;
  
  public DragEndCancelledEvent(TaskStack paramTaskStack, Task paramTask, TaskView paramTaskView)
  {
    this.stack = paramTaskStack;
    this.task = paramTask;
    this.taskView = paramTaskView;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\ui\dragndrop\DragEndCancelledEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */