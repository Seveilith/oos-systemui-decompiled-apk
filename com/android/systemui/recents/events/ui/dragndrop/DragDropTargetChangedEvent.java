package com.android.systemui.recents.events.ui.dragndrop;

import com.android.systemui.recents.events.EventBus.AnimatedEvent;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.views.DropTarget;

public class DragDropTargetChangedEvent
  extends EventBus.AnimatedEvent
{
  public final DropTarget dropTarget;
  public final Task task;
  
  public DragDropTargetChangedEvent(Task paramTask, DropTarget paramDropTarget)
  {
    this.task = paramTask;
    this.dropTarget = paramDropTarget;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\ui\dragndrop\DragDropTargetChangedEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */