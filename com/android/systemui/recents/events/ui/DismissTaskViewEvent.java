package com.android.systemui.recents.events.ui;

import com.android.systemui.recents.events.EventBus.AnimatedEvent;
import com.android.systemui.recents.views.TaskView;

public class DismissTaskViewEvent
  extends EventBus.AnimatedEvent
{
  public final TaskView taskView;
  
  public DismissTaskViewEvent(TaskView paramTaskView)
  {
    this.taskView = paramTaskView;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\ui\DismissTaskViewEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */