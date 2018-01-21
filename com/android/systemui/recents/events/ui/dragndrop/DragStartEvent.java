package com.android.systemui.recents.events.ui.dragndrop;

import android.graphics.Point;
import com.android.systemui.recents.events.EventBus.Event;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.views.TaskView;

public class DragStartEvent
  extends EventBus.Event
{
  public final Task task;
  public final TaskView taskView;
  public final Point tlOffset;
  
  public DragStartEvent(Task paramTask, TaskView paramTaskView, Point paramPoint)
  {
    this.task = paramTask;
    this.taskView = paramTaskView;
    this.tlOffset = paramPoint;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\ui\dragndrop\DragStartEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */