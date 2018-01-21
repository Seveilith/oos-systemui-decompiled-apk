package com.android.systemui.recents.events.activity;

import com.android.systemui.recents.events.EventBus.AnimatedEvent;
import com.android.systemui.recents.tv.views.TaskCardView;

public class LaunchTvTaskStartedEvent
  extends EventBus.AnimatedEvent
{
  public final TaskCardView taskView;
  
  public LaunchTvTaskStartedEvent(TaskCardView paramTaskCardView)
  {
    this.taskView = paramTaskCardView;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\activity\LaunchTvTaskStartedEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */