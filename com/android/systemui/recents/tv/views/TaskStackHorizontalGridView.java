package com.android.systemui.recents.tv.views;

import android.content.Context;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.util.AttributeSet;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.ui.AllTaskViewsDismissedEvent;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.TaskStack;
import com.android.systemui.recents.model.TaskStack.TaskStackCallbacks;
import com.android.systemui.recents.tv.animations.RecentsRowFocusAnimationHolder;
import com.android.systemui.recents.tv.animations.ViewFocusAnimator;
import com.android.systemui.recents.views.AnimationProps;

public class TaskStackHorizontalGridView
  extends HorizontalGridView
  implements TaskStack.TaskStackCallbacks
{
  private Task mFocusedTask;
  private TaskStack mStack;
  
  public TaskStackHorizontalGridView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TaskStackHorizontalGridView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public TaskCardView getChildViewForTask(Task paramTask)
  {
    int i = 0;
    while (i < getChildCount())
    {
      TaskCardView localTaskCardView = (TaskCardView)getChildAt(i);
      if (localTaskCardView.getTask() == paramTask) {
        return localTaskCardView;
      }
      i += 1;
    }
    return null;
  }
  
  public Task getFocusedTask()
  {
    if (findFocus() != null) {
      this.mFocusedTask = ((TaskCardView)findFocus()).getTask();
    }
    return this.mFocusedTask;
  }
  
  public TaskStack getStack()
  {
    return this.mStack;
  }
  
  public void init(TaskStack paramTaskStack)
  {
    this.mStack = paramTaskStack;
    if (this.mStack != null) {
      this.mStack.setCallbacks(this);
    }
  }
  
  protected void onAttachedToWindow()
  {
    EventBus.getDefault().register(this, 3);
    setWindowAlignment(0);
    setImportantForAccessibility(1);
    super.onAttachedToWindow();
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    EventBus.getDefault().unregister(this);
  }
  
  public void onStackTaskAdded(TaskStack paramTaskStack, Task paramTask)
  {
    ((TaskStackHorizontalViewAdapter)getAdapter()).addTaskAt(paramTask, paramTaskStack.indexOfStackTask(paramTask));
  }
  
  public void onStackTaskRemoved(TaskStack paramTaskStack, Task paramTask1, Task paramTask2, AnimationProps paramAnimationProps, boolean paramBoolean)
  {
    int i = 0;
    ((TaskStackHorizontalViewAdapter)getAdapter()).removeTask(paramTask1);
    if (this.mFocusedTask == paramTask1) {
      this.mFocusedTask = null;
    }
    if (this.mStack.getStackTaskCount() == 0)
    {
      if (this.mStack.getStackTaskCount() == 0) {
        i = 1;
      }
      if (i != 0)
      {
        paramTaskStack = EventBus.getDefault();
        if (!paramBoolean) {
          break label82;
        }
      }
    }
    label82:
    for (i = 2131690324;; i = 2131690325)
    {
      paramTaskStack.send(new AllTaskViewsDismissedEvent(i));
      return;
    }
  }
  
  public void onStackTasksRemoved(TaskStack paramTaskStack) {}
  
  public void onStackTasksUpdated(TaskStack paramTaskStack) {}
  
  public void startFocusGainAnimation()
  {
    int i = 0;
    while (i < getChildCount())
    {
      TaskCardView localTaskCardView = (TaskCardView)getChildAt(i);
      if (localTaskCardView.hasFocus()) {
        localTaskCardView.getViewFocusAnimator().changeSize(true);
      }
      localTaskCardView.getRecentsRowFocusAnimationHolder().startFocusGainAnimation();
      i += 1;
    }
  }
  
  public void startFocusLossAnimation()
  {
    int i = 0;
    while (i < getChildCount())
    {
      TaskCardView localTaskCardView = (TaskCardView)getChildAt(i);
      if (localTaskCardView.hasFocus()) {
        localTaskCardView.getViewFocusAnimator().changeSize(false);
      }
      localTaskCardView.getRecentsRowFocusAnimationHolder().startFocusLossAnimation();
      i += 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\tv\views\TaskStackHorizontalGridView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */