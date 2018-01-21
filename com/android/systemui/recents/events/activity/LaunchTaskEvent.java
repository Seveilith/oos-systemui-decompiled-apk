package com.android.systemui.recents.events.activity;

import android.graphics.Rect;
import com.android.systemui.recents.events.EventBus.Event;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.views.TaskView;

public class LaunchTaskEvent
  extends EventBus.Event
{
  public final boolean screenPinningRequested;
  public final Rect targetTaskBounds;
  public final int targetTaskStack;
  public final Task task;
  public final TaskView taskView;
  
  public LaunchTaskEvent(TaskView paramTaskView, Task paramTask, Rect paramRect, int paramInt, boolean paramBoolean)
  {
    this.taskView = paramTaskView;
    this.task = paramTask;
    this.targetTaskBounds = paramRect;
    this.targetTaskStack = paramInt;
    this.screenPinningRequested = paramBoolean;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\activity\LaunchTaskEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */