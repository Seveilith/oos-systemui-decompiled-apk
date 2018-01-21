package com.android.systemui.recents.events.activity;

import com.android.systemui.recents.events.EventBus.AnimatedEvent;
import com.android.systemui.recents.views.TaskView;

public class LaunchTaskStartedEvent
  extends EventBus.AnimatedEvent
{
  public final boolean screenPinningRequested;
  public final TaskView taskView;
  
  public LaunchTaskStartedEvent(TaskView paramTaskView, boolean paramBoolean)
  {
    this.taskView = paramTaskView;
    this.screenPinningRequested = paramBoolean;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\activity\LaunchTaskStartedEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */