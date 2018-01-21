package com.android.systemui.recents.tv.views;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsConfiguration;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.CancelEnterRecentsWindowAnimationEvent;
import com.android.systemui.recents.events.activity.DismissRecentsToHomeAnimationStarted;
import com.android.systemui.recents.events.activity.LaunchTvTaskEvent;
import com.android.systemui.recents.events.component.RecentsVisibilityChangedEvent;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.TaskStack;

public class RecentsTvView
  extends FrameLayout
{
  private boolean mAwaitingFirstLayout = true;
  private View mDismissPlaceholder;
  private View mEmptyView;
  private final Handler mHandler = new Handler();
  private RecyclerView.OnScrollListener mScrollListener;
  private TaskStack mStack;
  private Rect mSystemInsets = new Rect();
  private TaskStackHorizontalGridView mTaskStackHorizontalView;
  private RecentsTvTransitionHelper mTransitionHelper;
  
  public RecentsTvView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public RecentsTvView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public RecentsTvView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public RecentsTvView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setWillNotDraw(false);
    this.mEmptyView = LayoutInflater.from(paramContext).inflate(2130968799, this, false);
    addView(this.mEmptyView);
    this.mTransitionHelper = new RecentsTvTransitionHelper(this.mContext, this.mHandler);
  }
  
  private void launchTaskFomRecents(final Task paramTask, boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      Recents.getSystemServices().startActivityFromRecents(getContext(), paramTask.key, paramTask.title, null);
      return;
    }
    this.mTaskStackHorizontalView.requestFocus();
    Task localTask = this.mTaskStackHorizontalView.getFocusedTask();
    if ((localTask != null) && (paramTask != localTask))
    {
      if (this.mScrollListener != null) {
        this.mTaskStackHorizontalView.removeOnScrollListener(this.mScrollListener);
      }
      this.mScrollListener = new RecyclerView.OnScrollListener()
      {
        public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
        {
          super.onScrollStateChanged(paramAnonymousRecyclerView, paramAnonymousInt);
          if (paramAnonymousInt == 0)
          {
            paramAnonymousRecyclerView = RecentsTvView.-get2(RecentsTvView.this).getChildViewForTask(paramTask);
            if (paramAnonymousRecyclerView == null) {
              break label78;
            }
            RecentsTvView.-get3(RecentsTvView.this).launchTaskFromRecents(RecentsTvView.-get1(RecentsTvView.this), paramTask, RecentsTvView.-get2(RecentsTvView.this), paramAnonymousRecyclerView, null, -1);
          }
          for (;;)
          {
            RecentsTvView.-get2(RecentsTvView.this).removeOnScrollListener(RecentsTvView.-get0(RecentsTvView.this));
            return;
            label78:
            Log.e("RecentsTvView", "Card view for task : " + paramTask + ", returned null.");
            Recents.getSystemServices().startActivityFromRecents(RecentsTvView.this.getContext(), paramTask.key, paramTask.title, null);
          }
        }
      };
      this.mTaskStackHorizontalView.addOnScrollListener(this.mScrollListener);
      this.mTaskStackHorizontalView.setSelectedPositionSmooth(((TaskStackHorizontalViewAdapter)this.mTaskStackHorizontalView.getAdapter()).getPositionOfTask(paramTask));
      return;
    }
    this.mTransitionHelper.launchTaskFromRecents(this.mStack, paramTask, this.mTaskStackHorizontalView, this.mTaskStackHorizontalView.getChildViewForTask(paramTask), null, -1);
  }
  
  public void hideEmptyView()
  {
    this.mEmptyView.setVisibility(8);
    this.mTaskStackHorizontalView.setVisibility(0);
    if (Recents.getSystemServices().isTouchExplorationEnabled()) {
      this.mDismissPlaceholder.setVisibility(0);
    }
  }
  
  public void init(TaskStack paramTaskStack)
  {
    Recents.getConfiguration().getLaunchState();
    this.mStack = paramTaskStack;
    this.mTaskStackHorizontalView.init(paramTaskStack);
    if (paramTaskStack.getStackTaskCount() > 0) {
      hideEmptyView();
    }
    for (;;)
    {
      requestLayout();
      return;
      showEmptyView();
    }
  }
  
  public boolean launchFocusedTask()
  {
    if (this.mTaskStackHorizontalView != null)
    {
      Task localTask = this.mTaskStackHorizontalView.getFocusedTask();
      if (localTask != null)
      {
        launchTaskFomRecents(localTask, true);
        return true;
      }
    }
    return false;
  }
  
  public boolean launchPreviousTask(boolean paramBoolean)
  {
    if (this.mTaskStackHorizontalView != null)
    {
      Task localTask = this.mTaskStackHorizontalView.getStack().getLaunchTarget();
      if (localTask != null)
      {
        launchTaskFomRecents(localTask, paramBoolean);
        return true;
      }
    }
    return false;
  }
  
  public WindowInsets onApplyWindowInsets(WindowInsets paramWindowInsets)
  {
    this.mSystemInsets.set(paramWindowInsets.getSystemWindowInsets());
    requestLayout();
    return paramWindowInsets;
  }
  
  protected void onAttachedToWindow()
  {
    EventBus.getDefault().register(this, 3);
    super.onAttachedToWindow();
  }
  
  public final void onBusEvent(DismissRecentsToHomeAnimationStarted paramDismissRecentsToHomeAnimationStarted)
  {
    EventBus.getDefault().send(new CancelEnterRecentsWindowAnimationEvent(null));
  }
  
  public final void onBusEvent(LaunchTvTaskEvent paramLaunchTvTaskEvent)
  {
    this.mTransitionHelper.launchTaskFromRecents(this.mStack, paramLaunchTvTaskEvent.task, this.mTaskStackHorizontalView, paramLaunchTvTaskEvent.taskView, paramLaunchTvTaskEvent.targetTaskBounds, paramLaunchTvTaskEvent.targetTaskStack);
  }
  
  public final void onBusEvent(RecentsVisibilityChangedEvent paramRecentsVisibilityChangedEvent)
  {
    if (!paramRecentsVisibilityChangedEvent.visible) {
      this.mAwaitingFirstLayout = true;
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    EventBus.getDefault().unregister(this);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mDismissPlaceholder = findViewById(2131952188);
    this.mTaskStackHorizontalView = ((TaskStackHorizontalGridView)findViewById(2131952186));
  }
  
  public TaskStackHorizontalGridView setTaskStackViewAdapter(TaskStackHorizontalViewAdapter paramTaskStackHorizontalViewAdapter)
  {
    if (this.mTaskStackHorizontalView != null)
    {
      this.mTaskStackHorizontalView.setAdapter(paramTaskStackHorizontalViewAdapter);
      paramTaskStackHorizontalViewAdapter.setTaskStackHorizontalGridView(this.mTaskStackHorizontalView);
    }
    return this.mTaskStackHorizontalView;
  }
  
  public void showEmptyView()
  {
    this.mEmptyView.setVisibility(0);
    this.mTaskStackHorizontalView.setVisibility(8);
    if (Recents.getSystemServices().isTouchExplorationEnabled()) {
      this.mDismissPlaceholder.setVisibility(8);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\tv\views\RecentsTvView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */