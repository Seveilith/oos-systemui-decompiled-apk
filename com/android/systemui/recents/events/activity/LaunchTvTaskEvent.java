package com.android.systemui.recents.events.activity;

import android.graphics.Rect;
import com.android.systemui.recents.events.EventBus.Event;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.tv.views.TaskCardView;

public class LaunchTvTaskEvent
  extends EventBus.Event
{
  public final Rect targetTaskBounds;
  public final int targetTaskStack;
  public final Task task;
  public final TaskCardView taskView;
  
  public LaunchTvTaskEvent(TaskCardView paramTaskCardView, Task paramTask, Rect paramRect, int paramInt)
  {
    this.taskView = paramTaskCardView;
    this.task = paramTask;
    this.targetTaskBounds = paramRect;
    this.targetTaskStack = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\activity\LaunchTvTaskEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */