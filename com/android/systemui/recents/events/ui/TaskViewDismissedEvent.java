package com.android.systemui.recents.events.ui;

import com.android.systemui.recents.events.EventBus.Event;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.views.AnimationProps;
import com.android.systemui.recents.views.TaskView;

public class TaskViewDismissedEvent
  extends EventBus.Event
{
  public final AnimationProps animation;
  public final Task task;
  public final TaskView taskView;
  
  public TaskViewDismissedEvent(Task paramTask, TaskView paramTaskView, AnimationProps paramAnimationProps)
  {
    this.task = paramTask;
    this.taskView = paramTaskView;
    this.animation = paramAnimationProps;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\ui\TaskViewDismissedEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */