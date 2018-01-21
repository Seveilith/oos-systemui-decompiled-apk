package com.android.systemui.recents.views;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.IntDef;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.MutableBoolean;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Interpolators;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsActivityLaunchState;
import com.android.systemui.recents.RecentsConfiguration;
import com.android.systemui.recents.RecentsDebugFlags;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.CancelEnterRecentsWindowAnimationEvent;
import com.android.systemui.recents.events.activity.ConfigurationChangedEvent;
import com.android.systemui.recents.events.activity.DismissRecentsToHomeAnimationStarted;
import com.android.systemui.recents.events.activity.EnterRecentsTaskStackAnimationCompletedEvent;
import com.android.systemui.recents.events.activity.EnterRecentsWindowAnimationCompletedEvent;
import com.android.systemui.recents.events.activity.HideRecentsEvent;
import com.android.systemui.recents.events.activity.HideStackActionButtonEvent;
import com.android.systemui.recents.events.activity.IterateRecentsEvent;
import com.android.systemui.recents.events.activity.LaunchNextTaskRequestEvent;
import com.android.systemui.recents.events.activity.LaunchTaskEvent;
import com.android.systemui.recents.events.activity.LaunchTaskStartedEvent;
import com.android.systemui.recents.events.activity.MultiWindowStateChangedEvent;
import com.android.systemui.recents.events.activity.PackagesChangedEvent;
import com.android.systemui.recents.events.activity.ShowStackActionButtonEvent;
import com.android.systemui.recents.events.ui.AllTaskViewsDismissedEvent;
import com.android.systemui.recents.events.ui.DeleteTaskDataEvent;
import com.android.systemui.recents.events.ui.DismissAllTaskViewsEvent;
import com.android.systemui.recents.events.ui.DismissTaskViewEvent;
import com.android.systemui.recents.events.ui.RecentsGrowingEvent;
import com.android.systemui.recents.events.ui.TaskViewDismissedEvent;
import com.android.systemui.recents.events.ui.UpdateFreeformTaskViewVisibilityEvent;
import com.android.systemui.recents.events.ui.UserInteractionEvent;
import com.android.systemui.recents.events.ui.dragndrop.DragDropTargetChangedEvent;
import com.android.systemui.recents.events.ui.dragndrop.DragEndCancelledEvent;
import com.android.systemui.recents.events.ui.dragndrop.DragEndEvent;
import com.android.systemui.recents.events.ui.dragndrop.DragStartEvent;
import com.android.systemui.recents.events.ui.dragndrop.DragStartInitializeDropTargetsEvent;
import com.android.systemui.recents.events.ui.focus.DismissFocusedTaskViewEvent;
import com.android.systemui.recents.events.ui.focus.FocusNextTaskViewEvent;
import com.android.systemui.recents.events.ui.focus.FocusPreviousTaskViewEvent;
import com.android.systemui.recents.misc.DozeTrigger;
import com.android.systemui.recents.misc.ReferenceCountedTrigger;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.misc.Utilities;
import com.android.systemui.recents.model.RecentsTaskLoader;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.Task.TaskKey;
import com.android.systemui.recents.model.TaskStack;
import com.android.systemui.recents.model.TaskStack.DockState;
import com.android.systemui.recents.model.TaskStack.TaskStackCallbacks;
import com.android.systemui.util.MdmLogger;
import com.android.systemui.util.Utils;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class TaskStackView
  extends FrameLayout
  implements TaskStack.TaskStackCallbacks, TaskView.TaskViewCallbacks, TaskStackViewScroller.TaskStackViewScrollerCallbacks, TaskStackLayoutAlgorithm.TaskStackLayoutAlgorithmCallbacks, ViewPool.ViewPoolConsumer<TaskView, Task>
{
  private TaskStackAnimationHelper mAnimationHelper;
  @ViewDebug.ExportedProperty(category="recents")
  private boolean mAwaitingFirstLayout = true;
  private ArrayList<TaskViewTransform> mCurrentTaskTransforms = new ArrayList();
  private AnimationProps mDeferredTaskViewLayoutAnimation = null;
  private ImageButton mDismissAllBtn;
  private boolean mDismissAllButtonAnimating;
  @ViewDebug.ExportedProperty(category="recents")
  private int mDisplayOrientation = 0;
  @ViewDebug.ExportedProperty(category="recents")
  private Rect mDisplayRect = new Rect();
  private int mDividerSize;
  @ViewDebug.ExportedProperty(category="recents")
  private boolean mEnterAnimationComplete = false;
  @ViewDebug.ExportedProperty(deepExport=true, prefix="focused_task_")
  private Task mFocusedTask;
  private GradientDrawable mFreeformWorkspaceBackground;
  private ObjectAnimator mFreeformWorkspaceBackgroundAnimator;
  private DropTarget mFreeformWorkspaceDropTarget = new DropTarget()
  {
    public boolean acceptsDrop(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, Rect paramAnonymousRect, boolean paramAnonymousBoolean)
    {
      if (!paramAnonymousBoolean) {
        return TaskStackView.this.mLayoutAlgorithm.mFreeformRect.contains(paramAnonymousInt1, paramAnonymousInt2);
      }
      return false;
    }
  };
  private ArraySet<Task.TaskKey> mIgnoreTasks = new ArraySet();
  @ViewDebug.ExportedProperty(category="recents")
  private boolean mInMeasureLayout = false;
  private LayoutInflater mInflater;
  @ViewDebug.ExportedProperty(category="recents")
  private int mInitialState = 1;
  private int mLastHeight;
  private int mLastWidth;
  @ViewDebug.ExportedProperty(category="recents")
  private boolean mLaunchNextAfterFirstMeasure = false;
  @ViewDebug.ExportedProperty(deepExport=true, prefix="layout_")
  TaskStackLayoutAlgorithm mLayoutAlgorithm;
  private ValueAnimator.AnimatorUpdateListener mRequestUpdateClippingListener = new ValueAnimator.AnimatorUpdateListener()
  {
    public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
    {
      if (!TaskStackView.-get6(TaskStackView.this))
      {
        TaskStackView.-set1(TaskStackView.this, true);
        TaskStackView.this.invalidate();
      }
    }
  };
  private boolean mResetToInitialStateWhenResized;
  @ViewDebug.ExportedProperty(category="recents")
  boolean mScreenPinningEnabled;
  private TaskStackLayoutAlgorithm mStableLayoutAlgorithm;
  @ViewDebug.ExportedProperty(category="recents")
  private Rect mStableStackBounds = new Rect();
  @ViewDebug.ExportedProperty(category="recents")
  private Rect mStableWindowRect = new Rect();
  private TaskStack mStack = new TaskStack();
  @ViewDebug.ExportedProperty(category="recents")
  private Rect mStackBounds = new Rect();
  private DropTarget mStackDropTarget = new DropTarget()
  {
    public boolean acceptsDrop(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, Rect paramAnonymousRect, boolean paramAnonymousBoolean)
    {
      if (!paramAnonymousBoolean) {
        return TaskStackView.this.mLayoutAlgorithm.mStackRect.contains(paramAnonymousInt1, paramAnonymousInt2);
      }
      return false;
    }
  };
  @ViewDebug.ExportedProperty(deepExport=true, prefix="scroller_")
  private TaskStackViewScroller mStackScroller;
  private int mStartTimerIndicatorDuration;
  private int mTaskCornerRadiusPx;
  private ArrayList<TaskView> mTaskViews = new ArrayList();
  @ViewDebug.ExportedProperty(category="recents")
  private boolean mTaskViewsClipDirty = true;
  private int[] mTmpIntPair = new int[2];
  private Rect mTmpRect = new Rect();
  private ArrayMap<Task.TaskKey, TaskView> mTmpTaskViewMap = new ArrayMap();
  private List<TaskView> mTmpTaskViews = new ArrayList();
  private TaskViewTransform mTmpTransform = new TaskViewTransform();
  @ViewDebug.ExportedProperty(category="recents")
  boolean mTouchExplorationEnabled;
  @ViewDebug.ExportedProperty(deepExport=true, prefix="touch_")
  private TaskStackViewTouchHandler mTouchHandler;
  @ViewDebug.ExportedProperty(deepExport=true, prefix="doze_")
  private DozeTrigger mUIDozeTrigger;
  private ViewPool<TaskView, Task> mViewPool;
  @ViewDebug.ExportedProperty(category="recents")
  private Rect mWindowRect = new Rect();
  
  public TaskStackView(Context paramContext)
  {
    super(paramContext);
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    Resources localResources = paramContext.getResources();
    this.mStack.setCallbacks(this);
    this.mViewPool = new ViewPool(paramContext, this);
    this.mInflater = LayoutInflater.from(paramContext);
    this.mLayoutAlgorithm = new TaskStackLayoutAlgorithm(paramContext, this);
    this.mStableLayoutAlgorithm = new TaskStackLayoutAlgorithm(paramContext, null);
    this.mStackScroller = new TaskStackViewScroller(paramContext, this, this.mLayoutAlgorithm);
    this.mTouchHandler = new TaskStackViewTouchHandler(paramContext, this, this.mStackScroller);
    this.mAnimationHelper = new TaskStackAnimationHelper(paramContext, this);
    this.mTaskCornerRadiusPx = localResources.getDimensionPixelSize(2131755614);
    this.mDividerSize = localSystemServicesProxy.getDockedDividerSize(paramContext);
    this.mDisplayOrientation = Utilities.getAppConfiguration(this.mContext).orientation;
    this.mDisplayRect = localSystemServicesProxy.getDisplayRect();
    this.mUIDozeTrigger = new DozeTrigger(getResources().getInteger(2131623991), new Runnable()
    {
      public void run()
      {
        List localList = TaskStackView.this.getTaskViews();
        int j = localList.size();
        int i = 0;
        while (i < j)
        {
          ((TaskView)localList.get(i)).startNoUserInteractionAnimation();
          i += 1;
        }
      }
    });
    setImportantForAccessibility(1);
    this.mFreeformWorkspaceBackground = ((GradientDrawable)getContext().getDrawable(2130838109));
    this.mFreeformWorkspaceBackground.setCallback(this);
    if (localSystemServicesProxy.hasFreeformWorkspaceSupport()) {
      this.mFreeformWorkspaceBackground.setColor(getContext().getColor(2131493020));
    }
  }
  
  private void animateFreeformWorkspaceBackgroundAlpha(int paramInt, AnimationProps paramAnimationProps)
  {
    if (this.mFreeformWorkspaceBackground.getAlpha() == paramInt) {
      return;
    }
    Utilities.cancelAnimationWithoutCallbacks(this.mFreeformWorkspaceBackgroundAnimator);
    this.mFreeformWorkspaceBackgroundAnimator = ObjectAnimator.ofInt(this.mFreeformWorkspaceBackground, Utilities.DRAWABLE_ALPHA, new int[] { this.mFreeformWorkspaceBackground.getAlpha(), paramInt });
    this.mFreeformWorkspaceBackgroundAnimator.setStartDelay(paramAnimationProps.getDuration(4));
    this.mFreeformWorkspaceBackgroundAnimator.setDuration(paramAnimationProps.getDuration(4));
    this.mFreeformWorkspaceBackgroundAnimator.setInterpolator(paramAnimationProps.getInterpolator(4));
    this.mFreeformWorkspaceBackgroundAnimator.start();
  }
  
  private void bindTaskView(TaskView paramTaskView, Task paramTask)
  {
    paramTaskView.onTaskBound(paramTask, this.mTouchExplorationEnabled, this.mDisplayOrientation, this.mDisplayRect);
    Recents.getTaskLoader().loadTaskData(paramTask);
  }
  
  private void clipTaskViews()
  {
    List localList = getTaskViews();
    Object localObject1 = null;
    int m = localList.size();
    int i = 0;
    if (i < m)
    {
      TaskView localTaskView = (TaskView)localList.get(i);
      Object localObject2 = null;
      int k = 0;
      if ((isIgnoredTask(localTaskView.getTask())) && (localObject1 != null)) {
        localTaskView.setTranslationZ(Math.max(localTaskView.getTranslationZ(), ((TaskView)localObject1).getTranslationZ() + 0.1F));
      }
      int j = k;
      if (i < m - 1)
      {
        j = k;
        if (localTaskView.shouldClipViewInStack()) {
          j = i + 1;
        }
      }
      for (;;)
      {
        localObject1 = localObject2;
        if (j < m)
        {
          localObject1 = (TaskView)localList.get(j);
          if (!((TaskView)localObject1).shouldClipViewInStack()) {}
        }
        else
        {
          j = k;
          if (localObject1 != null)
          {
            float f1 = localTaskView.getBottom();
            float f2 = ((TaskView)localObject1).getTop();
            j = k;
            if (f2 < f1) {
              j = (int)(f1 - f2) - this.mTaskCornerRadiusPx;
            }
          }
          localTaskView.getViewBounds().setClipBottom(j);
          localTaskView.mThumbnailView.updateThumbnailVisibility(j - localTaskView.getPaddingBottom());
          localObject1 = localTaskView;
          i += 1;
          break;
        }
        j += 1;
      }
    }
    this.mTaskViewsClipDirty = false;
  }
  
  private int findTaskViewInsertIndex(Task paramTask, int paramInt)
  {
    if (paramInt != -1)
    {
      List localList = getTaskViews();
      int j = 0;
      int m = localList.size();
      int i = 0;
      if (i < m)
      {
        Task localTask = ((TaskView)localList.get(i)).getTask();
        int k;
        if (localTask == paramTask) {
          k = 1;
        }
        do
        {
          i += 1;
          j = k;
          break;
          k = j;
        } while (paramInt >= this.mStack.indexOfStackTask(localTask));
        if (j != 0) {
          return i - 1;
        }
        return i;
      }
    }
    return -1;
  }
  
  private TaskView getFrontMostTaskView(boolean paramBoolean)
  {
    List localList = getTaskViews();
    int i = localList.size() - 1;
    while (i >= 0)
    {
      TaskView localTaskView = (TaskView)localList.get(i);
      Task localTask = localTaskView.getTask();
      if ((paramBoolean) && (localTask.isFreeformTask())) {
        i -= 1;
      } else {
        return localTaskView;
      }
    }
    return null;
  }
  
  private void layoutTaskView(boolean paramBoolean, TaskView paramTaskView)
  {
    if (paramBoolean)
    {
      Rect localRect = new Rect();
      if (paramTaskView.getBackground() != null) {
        paramTaskView.getBackground().getPadding(localRect);
      }
      this.mTmpRect.set(this.mStableLayoutAlgorithm.mTaskRect);
      this.mTmpRect.union(this.mLayoutAlgorithm.mTaskRect);
      paramTaskView.cancelTransformAnimation();
      paramTaskView.layout(this.mTmpRect.left - localRect.left, this.mTmpRect.top - localRect.top, this.mTmpRect.right + localRect.right, this.mTmpRect.bottom + localRect.bottom);
      return;
    }
    paramTaskView.layout(paramTaskView.getLeft(), paramTaskView.getTop(), paramTaskView.getRight(), paramTaskView.getBottom());
  }
  
  private void measureTaskView(TaskView paramTaskView)
  {
    Rect localRect = new Rect();
    if (paramTaskView.getBackground() != null) {
      paramTaskView.getBackground().getPadding(localRect);
    }
    this.mTmpRect.set(this.mStableLayoutAlgorithm.mTaskRect);
    this.mTmpRect.union(this.mLayoutAlgorithm.mTaskRect);
    paramTaskView.measure(View.MeasureSpec.makeMeasureSpec(this.mTmpRect.width() + localRect.left + localRect.right, 1073741824), View.MeasureSpec.makeMeasureSpec(this.mTmpRect.height() + localRect.top + localRect.bottom, 1073741824));
  }
  
  private void readSystemFlags()
  {
    boolean bool = false;
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    this.mTouchExplorationEnabled = localSystemServicesProxy.isTouchExplorationEnabled();
    if (localSystemServicesProxy.getSystemSetting(getContext(), "lock_to_app_enabled") != 0) {
      bool = true;
    }
    this.mScreenPinningEnabled = bool;
  }
  
  private void relayoutTaskViews(AnimationProps paramAnimationProps, ArrayMap<Task, AnimationProps> paramArrayMap, boolean paramBoolean)
  {
    cancelDeferredTaskViewLayoutAnimation();
    bindVisibleTaskViews(this.mStackScroller.getStackScroll(), paramBoolean);
    List localList = getTaskViews();
    int j = localList.size();
    int i = 0;
    if (i < j)
    {
      TaskView localTaskView = (TaskView)localList.get(i);
      Task localTask = localTaskView.getTask();
      if (this.mIgnoreTasks.contains(localTask.key)) {}
      for (;;)
      {
        i += 1;
        break;
        int k = this.mStack.indexOfStackTask(localTask);
        TaskViewTransform localTaskViewTransform = (TaskViewTransform)this.mCurrentTaskTransforms.get(k);
        AnimationProps localAnimationProps = paramAnimationProps;
        if (paramArrayMap != null)
        {
          localAnimationProps = paramAnimationProps;
          if (paramArrayMap.containsKey(localTask)) {
            localAnimationProps = (AnimationProps)paramArrayMap.get(localTask);
          }
        }
        updateTaskViewToTransform(localTaskView, localTaskViewTransform, localAnimationProps);
        paramAnimationProps = localAnimationProps;
      }
    }
  }
  
  private boolean setFocusedTask(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    return setFocusedTask(paramInt, paramBoolean1, paramBoolean2, 0);
  }
  
  private boolean setFocusedTask(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2)
  {
    Object localObject;
    label46:
    boolean bool2;
    boolean bool1;
    if (this.mStack.getTaskCount() > 0)
    {
      paramInt1 = Utilities.clamp(paramInt1, 0, this.mStack.getTaskCount() - 1);
      if (paramInt1 == -1) {
        break label179;
      }
      localObject = (Task)this.mStack.getStackTasks().get(paramInt1);
      TaskView localTaskView;
      if (this.mFocusedTask != null)
      {
        if (paramInt2 > 0)
        {
          localTaskView = getChildViewForTask(this.mFocusedTask);
          if (localTaskView != null) {
            localTaskView.getHeaderView().cancelFocusTimerIndicator();
          }
        }
        resetFocusedTask(this.mFocusedTask);
      }
      bool2 = false;
      this.mFocusedTask = ((Task)localObject);
      bool1 = bool2;
      if (localObject != null)
      {
        if (paramInt2 > 0)
        {
          localTaskView = getChildViewForTask(this.mFocusedTask);
          if (localTaskView == null) {
            break label185;
          }
          localTaskView.getHeaderView().startFocusTimerIndicator(paramInt2);
        }
        label137:
        if (!paramBoolean1) {
          break label194;
        }
        if (!this.mEnterAnimationComplete) {
          cancelAllTaskViewAnimations();
        }
        this.mLayoutAlgorithm.clearUnfocusedTaskOverrides();
        bool1 = this.mAnimationHelper.startScrollToFocusedTaskAnimation((Task)localObject, paramBoolean2);
      }
    }
    label179:
    label185:
    label194:
    do
    {
      return bool1;
      paramInt1 = -1;
      break;
      localObject = null;
      break label46;
      this.mStartTimerIndicatorDuration = paramInt2;
      break label137;
      localObject = getChildViewForTask((Task)localObject);
      bool1 = bool2;
    } while (localObject == null);
    ((TaskView)localObject).setFocusedState(true, paramBoolean2);
    return false;
  }
  
  private void unbindTaskView(TaskView paramTaskView, Task paramTask)
  {
    Recents.getTaskLoader().unloadTaskData(paramTask);
  }
  
  private void updateLayoutToStableBounds()
  {
    this.mWindowRect.set(this.mStableWindowRect);
    this.mStackBounds.set(this.mStableStackBounds);
    this.mLayoutAlgorithm.setSystemInsets(this.mStableLayoutAlgorithm.mSystemInsets);
    this.mLayoutAlgorithm.initialize(this.mDisplayRect, this.mWindowRect, this.mStackBounds, TaskStackLayoutAlgorithm.StackState.getStackStateForStack(this.mStack));
    updateLayoutAlgorithm(true);
  }
  
  void addIgnoreTask(Task paramTask)
  {
    this.mIgnoreTasks.add(paramTask.key);
  }
  
  void bindVisibleTaskViews(float paramFloat)
  {
    bindVisibleTaskViews(paramFloat, false);
  }
  
  void bindVisibleTaskViews(float paramFloat, boolean paramBoolean)
  {
    ArrayList localArrayList = this.mStack.getStackTasks();
    int[] arrayOfInt = computeVisibleTaskTransforms(this.mCurrentTaskTransforms, localArrayList, this.mStackScroller.getStackScroll(), paramFloat, this.mIgnoreTasks, paramBoolean);
    this.mTmpTaskViewMap.clear();
    Object localObject2 = getTaskViews();
    int i = -1;
    int k = ((List)localObject2).size() - 1;
    TaskView localTaskView;
    Object localObject1;
    if (k >= 0)
    {
      localTaskView = (TaskView)((List)localObject2).get(k);
      Task localTask = localTaskView.getTask();
      if (this.mIgnoreTasks.contains(localTask.key)) {}
      for (;;)
      {
        k -= 1;
        break;
        int m = this.mStack.indexOfStackTask(localTask);
        localObject1 = null;
        if (m != -1) {
          localObject1 = (TaskViewTransform)this.mCurrentTaskTransforms.get(m);
        }
        if ((localTask.isFreeformTask()) || ((localObject1 != null) && (((TaskViewTransform)localObject1).visible)))
        {
          this.mTmpTaskViewMap.put(localTask.key, localTaskView);
        }
        else
        {
          j = i;
          if (this.mTouchExplorationEnabled)
          {
            j = i;
            if (Utilities.isDescendentAccessibilityFocused(localTaskView))
            {
              j = m;
              resetFocusedTask(localTask);
            }
          }
          this.mViewPool.returnViewToPool(localTaskView);
          i = j;
        }
      }
    }
    int j = localArrayList.size() - 1;
    if (j >= 0)
    {
      localObject1 = (Task)localArrayList.get(j);
      localObject2 = (TaskViewTransform)this.mCurrentTaskTransforms.get(j);
      if (this.mIgnoreTasks.contains(((Task)localObject1).key)) {}
      for (;;)
      {
        j -= 1;
        break;
        if ((((Task)localObject1).isFreeformTask()) || (((TaskViewTransform)localObject2).visible))
        {
          localTaskView = (TaskView)this.mTmpTaskViewMap.get(((Task)localObject1).key);
          if (localTaskView == null)
          {
            localTaskView = (TaskView)this.mViewPool.pickUpViewFromPool(localObject1, localObject1);
            if (((Task)localObject1).isFreeformTask()) {
              updateTaskViewToTransform(localTaskView, (TaskViewTransform)localObject2, AnimationProps.IMMEDIATE);
            } else if (((TaskViewTransform)localObject2).rect.top <= this.mLayoutAlgorithm.mStackRect.top) {
              updateTaskViewToTransform(localTaskView, this.mLayoutAlgorithm.getBackOfStackTransform(), AnimationProps.IMMEDIATE);
            } else {
              updateTaskViewToTransform(localTaskView, this.mLayoutAlgorithm.getFrontOfStackTransform(), AnimationProps.IMMEDIATE);
            }
          }
          else
          {
            k = findTaskViewInsertIndex((Task)localObject1, this.mStack.indexOfStackTask((Task)localObject1));
            if (k != getTaskViews().indexOf(localTaskView))
            {
              detachViewFromParent(localTaskView);
              attachViewToParent(localTaskView, k, localTaskView.getLayoutParams());
              updateTaskViewsList();
            }
          }
        }
      }
    }
    if (i != -1) {
      if (i >= arrayOfInt[1]) {
        break label537;
      }
    }
    label537:
    for (i = arrayOfInt[1];; i = arrayOfInt[0])
    {
      setFocusedTask(i, false, true);
      localObject1 = getChildViewForTask(this.mFocusedTask);
      if (localObject1 != null) {
        ((TaskView)localObject1).requestAccessibilityFocus();
      }
      return;
    }
  }
  
  void cancelAllTaskViewAnimations()
  {
    Recents.getSystemServices().setDeepCleaning(false);
    List localList = getTaskViews();
    int i = localList.size() - 1;
    while (i >= 0)
    {
      TaskView localTaskView = (TaskView)localList.get(i);
      if (!this.mIgnoreTasks.contains(localTaskView.getTask().key)) {
        localTaskView.cancelTransformAnimation();
      }
      i -= 1;
    }
  }
  
  void cancelDeferredTaskViewLayoutAnimation()
  {
    this.mDeferredTaskViewLayoutAnimation = null;
  }
  
  public void computeScroll()
  {
    if (this.mStackScroller.computeScroll()) {
      sendAccessibilityEvent(4096);
    }
    if (this.mDeferredTaskViewLayoutAnimation != null)
    {
      relayoutTaskViews(this.mDeferredTaskViewLayoutAnimation);
      this.mTaskViewsClipDirty = true;
      this.mDeferredTaskViewLayoutAnimation = null;
    }
    if (this.mTaskViewsClipDirty) {
      clipTaskViews();
    }
  }
  
  public TaskStackLayoutAlgorithm.VisibilityReport computeStackVisibilityReport()
  {
    return this.mLayoutAlgorithm.computeStackVisibilityReport(this.mStack.getStackTasks());
  }
  
  int[] computeVisibleTaskTransforms(ArrayList<TaskViewTransform> paramArrayList, ArrayList<Task> paramArrayList1, float paramFloat1, float paramFloat2, ArraySet<Task.TaskKey> paramArraySet, boolean paramBoolean)
  {
    int j = paramArrayList1.size();
    int[] arrayOfInt = this.mTmpIntPair;
    arrayOfInt[0] = -1;
    arrayOfInt[1] = -1;
    int i;
    Object localObject3;
    Object localObject2;
    Object localObject4;
    label54:
    Object localObject5;
    TaskViewTransform localTaskViewTransform;
    Object localObject1;
    if (Float.compare(paramFloat1, paramFloat2) != 0)
    {
      i = 1;
      Utilities.matchTaskListSize(paramArrayList1, paramArrayList);
      localObject3 = null;
      localObject2 = null;
      localObject4 = null;
      j -= 1;
      if (j < 0) {
        break label258;
      }
      localObject5 = (Task)paramArrayList1.get(j);
      localTaskViewTransform = this.mLayoutAlgorithm.getStackTransform((Task)localObject5, paramFloat1, (TaskViewTransform)paramArrayList.get(j), (TaskViewTransform)localObject3, paramBoolean);
      localObject1 = localObject4;
      if (i != 0)
      {
        if (!localTaskViewTransform.visible) {
          break label148;
        }
        localObject1 = localObject4;
      }
      label116:
      if (!paramArraySet.contains(((Task)localObject5).key)) {
        break label196;
      }
    }
    for (;;)
    {
      j -= 1;
      localObject4 = localObject1;
      break label54;
      i = 0;
      break;
      label148:
      localObject4 = this.mLayoutAlgorithm.getStackTransform((Task)localObject5, paramFloat2, new TaskViewTransform(), (TaskViewTransform)localObject2);
      localObject1 = localObject4;
      if (!((TaskViewTransform)localObject4).visible) {
        break label116;
      }
      localTaskViewTransform.copyFrom((TaskViewTransform)localObject4);
      localObject1 = localObject4;
      break label116;
      label196:
      if (!((Task)localObject5).isFreeformTask())
      {
        localObject4 = localTaskViewTransform;
        localObject5 = localObject1;
        localObject3 = localObject4;
        localObject2 = localObject5;
        if (localTaskViewTransform.visible)
        {
          if (arrayOfInt[0] < 0) {
            arrayOfInt[0] = j;
          }
          arrayOfInt[1] = j;
          localObject3 = localObject4;
          localObject2 = localObject5;
        }
      }
    }
    label258:
    return arrayOfInt;
  }
  
  public TaskView createView(Context paramContext)
  {
    return (TaskView)this.mInflater.inflate(2130968794, this, false);
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    String str1 = paramString + "  ";
    String str2 = Integer.toHexString(System.identityHashCode(this));
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("TaskStackView");
    paramPrintWriter.print(" hasDefRelayout=");
    if (this.mDeferredTaskViewLayoutAnimation != null)
    {
      paramString = "Y";
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(" clipDirty=");
      if (!this.mTaskViewsClipDirty) {
        break label444;
      }
      paramString = "Y";
      label83:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(" awaitingFirstLayout=");
      if (!this.mAwaitingFirstLayout) {
        break label451;
      }
      paramString = "Y";
      label106:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(" initialState=");
      paramPrintWriter.print(this.mInitialState);
      paramPrintWriter.print(" inMeasureLayout=");
      if (!this.mInMeasureLayout) {
        break label458;
      }
      paramString = "Y";
      label144:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(" enterAnimCompleted=");
      if (!this.mEnterAnimationComplete) {
        break label465;
      }
      paramString = "Y";
      label167:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(" touchExplorationOn=");
      if (!this.mTouchExplorationEnabled) {
        break label472;
      }
      paramString = "Y";
      label190:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(" screenPinningOn=");
      if (!this.mScreenPinningEnabled) {
        break label479;
      }
    }
    label444:
    label451:
    label458:
    label465:
    label472:
    label479:
    for (paramString = "Y";; paramString = "N")
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(" numIgnoreTasks=");
      paramPrintWriter.print(this.mIgnoreTasks.size());
      paramPrintWriter.print(" numViewPool=");
      paramPrintWriter.print(this.mViewPool.getViews().size());
      paramPrintWriter.print(" stableStackBounds=");
      paramPrintWriter.print(Utilities.dumpRect(this.mStableStackBounds));
      paramPrintWriter.print(" stackBounds=");
      paramPrintWriter.print(Utilities.dumpRect(this.mStackBounds));
      paramPrintWriter.print(" stableWindow=");
      paramPrintWriter.print(Utilities.dumpRect(this.mStableWindowRect));
      paramPrintWriter.print(" window=");
      paramPrintWriter.print(Utilities.dumpRect(this.mWindowRect));
      paramPrintWriter.print(" display=");
      paramPrintWriter.print(Utilities.dumpRect(this.mDisplayRect));
      paramPrintWriter.print(" orientation=");
      paramPrintWriter.print(this.mDisplayOrientation);
      paramPrintWriter.print(" [0x");
      paramPrintWriter.print(str2);
      paramPrintWriter.print("]");
      paramPrintWriter.println();
      if (this.mFocusedTask != null)
      {
        paramPrintWriter.print(str1);
        paramPrintWriter.print("Focused task: ");
        this.mFocusedTask.dump("", paramPrintWriter);
      }
      this.mLayoutAlgorithm.dump(str1, paramPrintWriter);
      this.mStackScroller.dump(str1, paramPrintWriter);
      return;
      paramString = "N";
      break;
      paramString = "N";
      break label83;
      paramString = "N";
      break label106;
      paramString = "N";
      break label144;
      paramString = "N";
      break label167;
      paramString = "N";
      break label190;
    }
  }
  
  public Task findAnchorTask(List<Task> paramList, MutableBoolean paramMutableBoolean)
  {
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      Task localTask = (Task)paramList.get(i);
      if (isIgnoredTask(localTask))
      {
        if (i == paramList.size() - 1) {
          paramMutableBoolean.value = true;
        }
        i -= 1;
      }
      else
      {
        return localTask;
      }
    }
    return null;
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return ScrollView.class.getName();
  }
  
  Task getAccessibilityFocusedTask()
  {
    Object localObject = getTaskViews();
    int j = ((List)localObject).size();
    int i = 0;
    while (i < j)
    {
      TaskView localTaskView = (TaskView)((List)localObject).get(i);
      if (Utilities.isDescendentAccessibilityFocused(localTaskView)) {
        return localTaskView.getTask();
      }
      i += 1;
    }
    localObject = getFrontMostTaskView(true);
    if (localObject != null) {
      return ((TaskView)localObject).getTask();
    }
    return null;
  }
  
  public TaskView getChildViewForTask(Task paramTask)
  {
    List localList = getTaskViews();
    int j = localList.size();
    int i = 0;
    while (i < j)
    {
      TaskView localTaskView = (TaskView)localList.get(i);
      if (localTaskView.getTask() == paramTask) {
        return localTaskView;
      }
      i += 1;
    }
    return null;
  }
  
  public void getCurrentTaskTransforms(ArrayList<Task> paramArrayList, ArrayList<TaskViewTransform> paramArrayList1)
  {
    Utilities.matchTaskListSize(paramArrayList, paramArrayList1);
    int j = this.mLayoutAlgorithm.getFocusState();
    int i = paramArrayList.size() - 1;
    if (i >= 0)
    {
      Task localTask = (Task)paramArrayList.get(i);
      TaskViewTransform localTaskViewTransform = (TaskViewTransform)paramArrayList1.get(i);
      TaskView localTaskView = getChildViewForTask(localTask);
      if (localTaskView != null) {
        localTaskViewTransform.fillIn(localTaskView);
      }
      for (;;)
      {
        localTaskViewTransform.visible = true;
        i -= 1;
        break;
        this.mLayoutAlgorithm.getStackTransform(localTask, this.mStackScroller.getStackScroll(), j, localTaskViewTransform, null, true, false);
      }
    }
  }
  
  Task getFocusedTask()
  {
    return this.mFocusedTask;
  }
  
  public void getLayoutTaskTransforms(float paramFloat, int paramInt, ArrayList<Task> paramArrayList, boolean paramBoolean, ArrayList<TaskViewTransform> paramArrayList1)
  {
    Utilities.matchTaskListSize(paramArrayList, paramArrayList1);
    int i = paramArrayList.size() - 1;
    while (i >= 0)
    {
      Task localTask = (Task)paramArrayList.get(i);
      TaskViewTransform localTaskViewTransform = (TaskViewTransform)paramArrayList1.get(i);
      this.mLayoutAlgorithm.getStackTransform(localTask, paramFloat, paramInt, localTaskViewTransform, null, true, paramBoolean);
      localTaskViewTransform.visible = true;
      i -= 1;
    }
  }
  
  public TaskStackViewScroller getScroller()
  {
    return this.mStackScroller;
  }
  
  public TaskStack getStack()
  {
    return this.mStack;
  }
  
  public TaskStackLayoutAlgorithm getStackAlgorithm()
  {
    return this.mLayoutAlgorithm;
  }
  
  List<TaskView> getTaskViews()
  {
    return this.mTaskViews;
  }
  
  public TaskStackViewTouchHandler getTouchHandler()
  {
    return this.mTouchHandler;
  }
  
  public boolean hasPreferredData(TaskView paramTaskView, Task paramTask)
  {
    return paramTaskView.getTask() == paramTask;
  }
  
  public void hideDismissAllButton()
  {
    if (this.mDismissAllBtn == null) {
      return;
    }
    this.mDismissAllButtonAnimating = true;
    this.mDismissAllBtn.animate().alpha(0.0F).setDuration(200L).withEndAction(new Runnable()
    {
      public void run()
      {
        TaskStackView.-set0(TaskStackView.this, false);
        TaskStackView.-get2(TaskStackView.this).setVisibility(8);
      }
    }).start();
  }
  
  boolean isIgnoredTask(Task paramTask)
  {
    return this.mIgnoreTasks.contains(paramTask.key);
  }
  
  public boolean isTouchPointInView(float paramFloat1, float paramFloat2, TaskView paramTaskView)
  {
    this.mTmpRect.set(paramTaskView.getLeft(), paramTaskView.getTop(), paramTaskView.getRight(), paramTaskView.getBottom());
    this.mTmpRect.offset((int)paramTaskView.getTranslationX(), (int)paramTaskView.getTranslationY());
    return this.mTmpRect.contains((int)paramFloat1, (int)paramFloat2);
  }
  
  public boolean launchFreeformTasks()
  {
    Object localObject = this.mStack.getFreeformTasks();
    if (!((ArrayList)localObject).isEmpty())
    {
      localObject = (Task)((ArrayList)localObject).get(((ArrayList)localObject).size() - 1);
      if ((localObject != null) && (((Task)localObject).isFreeformTask()))
      {
        EventBus.getDefault().send(new LaunchTaskEvent(getChildViewForTask((Task)localObject), (Task)localObject, null, -1, false));
        return true;
      }
    }
    return false;
  }
  
  protected void onAttachedToWindow()
  {
    EventBus.getDefault().register(this, 3);
    super.onAttachedToWindow();
    readSystemFlags();
  }
  
  public final void onBusEvent(ConfigurationChangedEvent paramConfigurationChangedEvent)
  {
    if (paramConfigurationChangedEvent.fromDeviceOrientationChange)
    {
      this.mDisplayOrientation = Utilities.getAppConfiguration(this.mContext).orientation;
      this.mDisplayRect = Recents.getSystemServices().getDisplayRect();
      this.mStackScroller.stopScroller();
    }
    reloadOnConfigurationChange();
    if (!paramConfigurationChangedEvent.fromMultiWindow)
    {
      this.mTmpTaskViews.clear();
      this.mTmpTaskViews.addAll(getTaskViews());
      this.mTmpTaskViews.addAll(this.mViewPool.getViews());
      int j = this.mTmpTaskViews.size();
      int i = 0;
      while (i < j)
      {
        ((TaskView)this.mTmpTaskViews.get(i)).onConfigurationChanged();
        i += 1;
      }
    }
    if (paramConfigurationChangedEvent.fromMultiWindow)
    {
      this.mInitialState = 2;
      requestLayout();
    }
    while (!paramConfigurationChangedEvent.fromDeviceOrientationChange) {
      return;
    }
    this.mInitialState = 1;
    requestLayout();
  }
  
  public final void onBusEvent(DismissRecentsToHomeAnimationStarted paramDismissRecentsToHomeAnimationStarted)
  {
    this.mTouchHandler.cancelNonDismissTaskAnimations();
    this.mStackScroller.stopScroller();
    this.mStackScroller.stopBoundScrollAnimation();
    cancelDeferredTaskViewLayoutAnimation();
    this.mAnimationHelper.startExitToHomeAnimation(paramDismissRecentsToHomeAnimationStarted.animated, paramDismissRecentsToHomeAnimationStarted.getAnimationTrigger());
    hideDismissAllButton();
    animateFreeformWorkspaceBackgroundAlpha(0, new AnimationProps(200, Interpolators.FAST_OUT_SLOW_IN));
  }
  
  public final void onBusEvent(EnterRecentsWindowAnimationCompletedEvent paramEnterRecentsWindowAnimationCompletedEvent)
  {
    this.mEnterAnimationComplete = true;
    if (this.mStack.getTaskCount() > 0)
    {
      this.mAnimationHelper.startEnterAnimation(paramEnterRecentsWindowAnimationCompletedEvent.getAnimationTrigger());
      paramEnterRecentsWindowAnimationCompletedEvent.addPostAnimationCallback(new Runnable()
      {
        public void run()
        {
          if (TaskStackView.-get3(TaskStackView.this) != null)
          {
            Object localObject = Recents.getConfiguration().getLaunchState();
            TaskStackView.-wrap0(TaskStackView.this, TaskStackView.-get4(TaskStackView.this).indexOfStackTask(TaskStackView.-get3(TaskStackView.this)), false, ((RecentsActivityLaunchState)localObject).launchedWithAltTab);
            localObject = TaskStackView.this.getChildViewForTask(TaskStackView.-get3(TaskStackView.this));
            if ((TaskStackView.this.mTouchExplorationEnabled) && (localObject != null)) {
              ((TaskView)localObject).requestAccessibilityFocus();
            }
          }
          EventBus.getDefault().send(new EnterRecentsTaskStackAnimationCompletedEvent());
        }
      });
    }
  }
  
  public final void onBusEvent(IterateRecentsEvent paramIterateRecentsEvent)
  {
    if (!this.mEnterAnimationComplete) {
      EventBus.getDefault().send(new CancelEnterRecentsWindowAnimationEvent(null));
    }
  }
  
  public final void onBusEvent(final LaunchNextTaskRequestEvent paramLaunchNextTaskRequestEvent)
  {
    if (this.mAwaitingFirstLayout)
    {
      this.mLaunchNextAfterFirstMeasure = true;
      return;
    }
    int i = this.mStack.indexOfStackTask(this.mStack.getLaunchTarget());
    if (i != -1)
    {
      i = Math.max(0, i - 1);
      if (i == -1) {
        break label202;
      }
      cancelAllTaskViewAnimations();
      paramLaunchNextTaskRequestEvent = (Task)this.mStack.getStackTasks().get(i);
      f2 = this.mStackScroller.getStackScroll();
      f1 = this.mLayoutAlgorithm.getStackScrollForTaskAtInitialOffset(paramLaunchNextTaskRequestEvent);
      f2 = Math.abs(f1 - f2);
      if ((getChildViewForTask(paramLaunchNextTaskRequestEvent) != null) && (f2 <= 0.35F)) {
        break label177;
      }
      i = (int)(32.0F * f2 + 216.0F);
      this.mStackScroller.animateScroll(f1, i, new Runnable()
      {
        public void run()
        {
          EventBus.getDefault().send(new LaunchTaskEvent(TaskStackView.this.getChildViewForTask(paramLaunchNextTaskRequestEvent), paramLaunchNextTaskRequestEvent, null, -1, false));
        }
      });
      MetricsLogger.action(getContext(), 318, paramLaunchNextTaskRequestEvent.key.getComponent().toString());
    }
    label177:
    label202:
    while (this.mStack.getTaskCount() != 0) {
      for (;;)
      {
        float f2;
        float f1;
        return;
        i = this.mStack.getTaskCount() - 1;
        break;
        EventBus.getDefault().send(new LaunchTaskEvent(getChildViewForTask(paramLaunchNextTaskRequestEvent), paramLaunchNextTaskRequestEvent, null, -1, false));
      }
    }
    EventBus.getDefault().send(new HideRecentsEvent(false, true));
  }
  
  public final void onBusEvent(LaunchTaskEvent paramLaunchTaskEvent) {}
  
  public final void onBusEvent(LaunchTaskStartedEvent paramLaunchTaskStartedEvent)
  {
    this.mAnimationHelper.startLaunchTaskAnimation(paramLaunchTaskStartedEvent.taskView, paramLaunchTaskStartedEvent.screenPinningRequested, paramLaunchTaskStartedEvent.getAnimationTrigger());
  }
  
  public final void onBusEvent(final MultiWindowStateChangedEvent paramMultiWindowStateChangedEvent)
  {
    if ((!paramMultiWindowStateChangedEvent.inMultiWindow) && (paramMultiWindowStateChangedEvent.showDeferredAnimation))
    {
      Recents.getConfiguration().getLaunchState().reset();
      paramMultiWindowStateChangedEvent.getAnimationTrigger().increment();
      post(new Runnable()
      {
        public void run()
        {
          TaskStackView.-get0(TaskStackView.this).startNewStackScrollAnimation(paramMultiWindowStateChangedEvent.stack, paramMultiWindowStateChangedEvent.getAnimationTrigger());
          paramMultiWindowStateChangedEvent.getAnimationTrigger().decrement();
          TaskStackView.this.showDismissAllButton();
        }
      });
      return;
    }
    hideDismissAllButton();
    setTasks(paramMultiWindowStateChangedEvent.stack, true);
  }
  
  public final void onBusEvent(PackagesChangedEvent paramPackagesChangedEvent)
  {
    paramPackagesChangedEvent = this.mStack.computeComponentsRemoved(paramPackagesChangedEvent.packageName, paramPackagesChangedEvent.userId);
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = this.mStack.getStackTasks();
    int j = ((ArrayList)localObject1).size();
    int i = 0;
    Object localObject2;
    while (i < j)
    {
      localObject2 = (Task)((ArrayList)localObject1).get(i);
      if (!((Task)localObject2).isLocked) {
        localArrayList.add(localObject2);
      }
      i += 1;
    }
    i = localArrayList.size() - 1;
    if (i >= 0)
    {
      localObject1 = (Task)localArrayList.get(i);
      if (paramPackagesChangedEvent.contains(((Task)localObject1).key.getComponent()))
      {
        localObject2 = getChildViewForTask((Task)localObject1);
        if (localObject2 == null) {
          break label144;
        }
        ((TaskView)localObject2).dismissTask();
      }
      for (;;)
      {
        i -= 1;
        break;
        label144:
        this.mStack.removeTask((Task)localObject1, AnimationProps.IMMEDIATE, false);
      }
    }
  }
  
  public final void onBusEvent(DismissAllTaskViewsEvent paramDismissAllTaskViewsEvent)
  {
    Log.d("TaskStackView", "DismissAllTaskViewsEvent");
    final ArrayList localArrayList = new ArrayList(this.mStack.getStackTasks());
    this.mAnimationHelper.startDeleteAllTasksAnimation(getTaskViews(), paramDismissAllTaskViewsEvent.getAnimationTrigger());
    Recents.getSystemServices().setDeepCleaning(true);
    paramDismissAllTaskViewsEvent.addPostAnimationCallback(new Runnable()
    {
      public void run()
      {
        Log.d("TaskStackView", "handle removeAllTasks");
        if (Utils.isDeepCleanEnable(TaskStackView.-get1(TaskStackView.this))) {
          Recents.getSystemServices().killAllRunningProcess();
        }
        TaskStackView.this.announceForAccessibility(TaskStackView.this.getContext().getString(2131690180));
        TaskStackView.-get4(TaskStackView.this).removeAllTasks();
        int i = localArrayList.size() - 1;
        while (i >= 0)
        {
          Task localTask = (Task)localArrayList.get(i);
          if (!localTask.isLocked) {
            EventBus.getDefault().send(new DeleteTaskDataEvent(localTask));
          }
          i -= 1;
        }
        MetricsLogger.action(TaskStackView.this.getContext(), 357);
      }
    });
  }
  
  public final void onBusEvent(DismissTaskViewEvent paramDismissTaskViewEvent)
  {
    this.mAnimationHelper.startDeleteTaskAnimation(paramDismissTaskViewEvent.taskView, paramDismissTaskViewEvent.getAnimationTrigger());
  }
  
  public final void onBusEvent(RecentsGrowingEvent paramRecentsGrowingEvent)
  {
    this.mResetToInitialStateWhenResized = true;
  }
  
  public final void onBusEvent(TaskViewDismissedEvent paramTaskViewDismissedEvent)
  {
    announceForAccessibility(getContext().getString(2131690179, new Object[] { paramTaskViewDismissedEvent.task.title }));
    this.mStack.removeTask(paramTaskViewDismissedEvent.task, paramTaskViewDismissedEvent.animation, false);
    EventBus.getDefault().send(new DeleteTaskDataEvent(paramTaskViewDismissedEvent.task));
    MetricsLogger.action(getContext(), 289, paramTaskViewDismissedEvent.task.key.getComponent().toString());
  }
  
  public final void onBusEvent(UpdateFreeformTaskViewVisibilityEvent paramUpdateFreeformTaskViewVisibilityEvent)
  {
    List localList = getTaskViews();
    int k = localList.size();
    int i = 0;
    if (i < k)
    {
      TaskView localTaskView = (TaskView)localList.get(i);
      if (localTaskView.getTask().isFreeformTask()) {
        if (!paramUpdateFreeformTaskViewVisibilityEvent.visible) {
          break label69;
        }
      }
      label69:
      for (int j = 0;; j = 4)
      {
        localTaskView.setVisibility(j);
        i += 1;
        break;
      }
    }
  }
  
  public final void onBusEvent(UserInteractionEvent paramUserInteractionEvent)
  {
    if ((Recents.getDebugFlags().isFastToggleRecentsEnabled()) && (this.mFocusedTask != null))
    {
      paramUserInteractionEvent = getChildViewForTask(this.mFocusedTask);
      if (paramUserInteractionEvent != null) {
        paramUserInteractionEvent.getHeaderView().cancelFocusTimerIndicator();
      }
    }
  }
  
  public final void onBusEvent(DragDropTargetChangedEvent paramDragDropTargetChangedEvent)
  {
    AnimationProps localAnimationProps = new AnimationProps(250, Interpolators.FAST_OUT_SLOW_IN);
    boolean bool = false;
    if ((paramDragDropTargetChangedEvent.dropTarget instanceof TaskStack.DockState))
    {
      paramDragDropTargetChangedEvent = (TaskStack.DockState)paramDragDropTargetChangedEvent.dropTarget;
      Rect localRect = new Rect(this.mStableLayoutAlgorithm.mSystemInsets);
      int i = getMeasuredHeight();
      int j = localRect.bottom;
      localRect.bottom = 0;
      this.mStackBounds.set(paramDragDropTargetChangedEvent.getDockedTaskStackBounds(this.mDisplayRect, getMeasuredWidth(), i - j, this.mDividerSize, localRect, this.mLayoutAlgorithm, getResources(), this.mWindowRect));
      this.mLayoutAlgorithm.setSystemInsets(localRect);
      this.mLayoutAlgorithm.initialize(this.mDisplayRect, this.mWindowRect, this.mStackBounds, TaskStackLayoutAlgorithm.StackState.getStackStateForStack(this.mStack));
      updateLayoutAlgorithm(true);
      bool = true;
    }
    for (;;)
    {
      relayoutTaskViews(localAnimationProps, null, bool);
      return;
      removeIgnoreTask(paramDragDropTargetChangedEvent.task);
      updateLayoutToStableBounds();
      addIgnoreTask(paramDragDropTargetChangedEvent.task);
    }
  }
  
  public final void onBusEvent(DragEndCancelledEvent paramDragEndCancelledEvent)
  {
    removeIgnoreTask(paramDragEndCancelledEvent.task);
    updateLayoutToStableBounds();
    Utilities.setViewFrameFromTranslation(paramDragEndCancelledEvent.taskView);
    new ArrayMap().put(paramDragEndCancelledEvent.task, new AnimationProps(250, Interpolators.FAST_OUT_SLOW_IN, paramDragEndCancelledEvent.getAnimationTrigger().decrementOnAnimationEnd()));
    relayoutTaskViews(new AnimationProps(250, Interpolators.FAST_OUT_SLOW_IN));
    paramDragEndCancelledEvent.getAnimationTrigger().increment();
  }
  
  public final void onBusEvent(final DragEndEvent paramDragEndEvent)
  {
    int j = 0;
    if ((paramDragEndEvent.dropTarget instanceof TaskStack.DockState))
    {
      this.mLayoutAlgorithm.clearUnfocusedTaskOverrides();
      return;
    }
    showDismissAllButton();
    boolean bool = paramDragEndEvent.task.isFreeformTask();
    int i;
    if ((!bool) && (paramDragEndEvent.dropTarget == this.mFreeformWorkspaceDropTarget))
    {
      i = 1;
      if (i != 0)
      {
        if (paramDragEndEvent.dropTarget != this.mFreeformWorkspaceDropTarget) {
          break label196;
        }
        this.mStack.moveTaskToStack(paramDragEndEvent.task, 2);
      }
    }
    for (;;)
    {
      updateLayoutAlgorithm(true);
      paramDragEndEvent.addPostAnimationCallback(new Runnable()
      {
        public void run()
        {
          Recents.getSystemServices().moveTaskToStack(paramDragEndEvent.task.key.id, paramDragEndEvent.task.key.stackId);
        }
      });
      removeIgnoreTask(paramDragEndEvent.task);
      Utilities.setViewFrameFromTranslation(paramDragEndEvent.taskView);
      new ArrayMap().put(paramDragEndEvent.task, new AnimationProps(250, Interpolators.FAST_OUT_SLOW_IN, paramDragEndEvent.getAnimationTrigger().decrementOnAnimationEnd()));
      relayoutTaskViews(new AnimationProps(250, Interpolators.FAST_OUT_SLOW_IN));
      paramDragEndEvent.getAnimationTrigger().increment();
      return;
      i = j;
      if (!bool) {
        break;
      }
      i = j;
      if (paramDragEndEvent.dropTarget != this.mStackDropTarget) {
        break;
      }
      i = 1;
      break;
      label196:
      if (paramDragEndEvent.dropTarget == this.mStackDropTarget) {
        this.mStack.moveTaskToStack(paramDragEndEvent.task, 1);
      }
    }
  }
  
  public final void onBusEvent(DragStartEvent paramDragStartEvent)
  {
    hideDismissAllButton();
    addIgnoreTask(paramDragStartEvent.task);
    if (paramDragStartEvent.task.isFreeformTask()) {
      this.mStackScroller.animateScroll(this.mLayoutAlgorithm.mInitialScrollP, null);
    }
    float f = paramDragStartEvent.taskView.getScaleX();
    this.mLayoutAlgorithm.getStackTransform(paramDragStartEvent.task, getScroller().getStackScroll(), this.mTmpTransform, null);
    this.mTmpTransform.scale = (f * 1.05F);
    this.mTmpTransform.translationZ = (this.mLayoutAlgorithm.mMaxTranslationZ + 1);
    this.mTmpTransform.dimAlpha = 0.0F;
    updateTaskViewToTransform(paramDragStartEvent.taskView, this.mTmpTransform, new AnimationProps(175, Interpolators.FAST_OUT_SLOW_IN));
  }
  
  public final void onBusEvent(DragStartInitializeDropTargetsEvent paramDragStartInitializeDropTargetsEvent)
  {
    if (Recents.getSystemServices().hasFreeformWorkspaceSupport())
    {
      paramDragStartInitializeDropTargetsEvent.handler.registerDropTargetForCurrentDrag(this.mStackDropTarget);
      paramDragStartInitializeDropTargetsEvent.handler.registerDropTargetForCurrentDrag(this.mFreeformWorkspaceDropTarget);
    }
  }
  
  public final void onBusEvent(DismissFocusedTaskViewEvent paramDismissFocusedTaskViewEvent)
  {
    if (this.mFocusedTask != null)
    {
      paramDismissFocusedTaskViewEvent = getChildViewForTask(this.mFocusedTask);
      if (paramDismissFocusedTaskViewEvent != null) {
        paramDismissFocusedTaskViewEvent.dismissTask();
      }
      resetFocusedTask(this.mFocusedTask);
    }
  }
  
  public final void onBusEvent(FocusNextTaskViewEvent paramFocusNextTaskViewEvent)
  {
    this.mStackScroller.stopScroller();
    this.mStackScroller.stopBoundScrollAnimation();
    setRelativeFocusedTask(true, false, true, false, paramFocusNextTaskViewEvent.timerIndicatorDuration);
  }
  
  public final void onBusEvent(FocusPreviousTaskViewEvent paramFocusPreviousTaskViewEvent)
  {
    this.mStackScroller.stopScroller();
    this.mStackScroller.stopBoundScrollAnimation();
    setRelativeFocusedTask(false, false, true);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    EventBus.getDefault().unregister(this);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if ((Recents.getSystemServices().hasFreeformWorkspaceSupport()) && (this.mFreeformWorkspaceBackground.getAlpha() > 0)) {
      this.mFreeformWorkspaceBackground.draw(paramCanvas);
    }
  }
  
  void onFirstLayout()
  {
    this.mAnimationHelper.prepareForEnterAnimation();
    animateFreeformWorkspaceBackgroundAlpha(this.mLayoutAlgorithm.getStackState().freeformBackgroundAlpha, new AnimationProps(150, Interpolators.FAST_OUT_SLOW_IN));
    int i = Recents.getConfiguration().getLaunchState().getInitialFocusTaskIndex(this.mStack.getTaskCount());
    if (i != -1) {
      setFocusedTask(i, false, false);
    }
    if ((this.mStackScroller.getStackScroll() < 0.3F) && (this.mStack.getTaskCount() > 0))
    {
      EventBus.getDefault().send(new ShowStackActionButtonEvent(false));
      return;
    }
    EventBus.getDefault().send(new HideStackActionButtonEvent());
  }
  
  public void onFocusStateChanged(int paramInt1, int paramInt2)
  {
    if (this.mDeferredTaskViewLayoutAnimation == null) {
      relayoutTaskViewsOnNextFrame(AnimationProps.IMMEDIATE);
    }
  }
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    if (Recents.getSystemServices().isDeepCleaning())
    {
      Log.d("TaskStackView", "return onGenericMotion");
      return true;
    }
    return this.mTouchHandler.onGenericMotionEvent(paramMotionEvent);
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    Object localObject = getTaskViews();
    int i = ((List)localObject).size();
    if (i > 0)
    {
      TaskView localTaskView = (TaskView)((List)localObject).get(0);
      localObject = (TaskView)((List)localObject).get(i - 1);
      paramAccessibilityEvent.setFromIndex(this.mStack.indexOfStackTask(localTaskView.getTask()));
      paramAccessibilityEvent.setToIndex(this.mStack.indexOfStackTask(((TaskView)localObject).getTask()));
      paramAccessibilityEvent.setContentDescription(((TaskView)localObject).getTask().title);
    }
    paramAccessibilityEvent.setItemCount(this.mStack.getTaskCount());
    i = this.mLayoutAlgorithm.mStackRect.height();
    paramAccessibilityEvent.setScrollY((int)(this.mStackScroller.getStackScroll() * i));
    paramAccessibilityEvent.setMaxScrollY((int)(this.mLayoutAlgorithm.mMaxScrollP * i));
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
    if (getTaskViews().size() > 1)
    {
      Task localTask = getAccessibilityFocusedTask();
      paramAccessibilityNodeInfo.setScrollable(true);
      int i = this.mStack.indexOfStackTask(localTask);
      if (i > 0) {
        paramAccessibilityNodeInfo.addAction(8192);
      }
      if ((i >= 0) && (i < this.mStack.getTaskCount() - 1)) {
        paramAccessibilityNodeInfo.addAction(4096);
      }
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if (Recents.getSystemServices().isDeepCleaning())
    {
      Log.d("TaskStackView", "return onIntercept");
      return true;
    }
    return this.mTouchHandler.onInterceptTouchEvent(paramMotionEvent);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mTmpTaskViews.clear();
    this.mTmpTaskViews.addAll(getTaskViews());
    this.mTmpTaskViews.addAll(this.mViewPool.getViews());
    paramInt2 = this.mTmpTaskViews.size();
    paramInt1 = 0;
    while (paramInt1 < paramInt2)
    {
      layoutTaskView(paramBoolean, (TaskView)this.mTmpTaskViews.get(paramInt1));
      paramInt1 += 1;
    }
    if ((paramBoolean) && (this.mStackScroller.isScrollOutOfBounds())) {
      this.mStackScroller.boundScroll();
    }
    relayoutTaskViews(AnimationProps.IMMEDIATE);
    clipTaskViews();
    if ((!this.mAwaitingFirstLayout) && (this.mEnterAnimationComplete)) {}
    do
    {
      return;
      this.mAwaitingFirstLayout = false;
      this.mInitialState = 0;
      onFirstLayout();
    } while (!this.mEnterAnimationComplete);
    Log.d("TaskStackView", "animation is complete before first layout");
    post(new Runnable()
    {
      public void run()
      {
        EventBus.getDefault().send(new EnterRecentsWindowAnimationCompletedEvent());
      }
    });
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    this.mInMeasureLayout = true;
    int i = View.MeasureSpec.getSize(paramInt1);
    paramInt2 = View.MeasureSpec.getSize(paramInt2);
    this.mLayoutAlgorithm.getTaskStackBounds(this.mDisplayRect, new Rect(0, 0, i, paramInt2), this.mLayoutAlgorithm.mSystemInsets.top, this.mLayoutAlgorithm.mSystemInsets.left, this.mLayoutAlgorithm.mSystemInsets.right, this.mTmpRect);
    if (!this.mTmpRect.equals(this.mStableStackBounds))
    {
      this.mStableStackBounds.set(this.mTmpRect);
      this.mStackBounds.set(this.mTmpRect);
      this.mStableWindowRect.set(0, 0, i, paramInt2);
      this.mWindowRect.set(0, 0, i, paramInt2);
    }
    this.mStableLayoutAlgorithm.initialize(this.mDisplayRect, this.mStableWindowRect, this.mStableStackBounds, TaskStackLayoutAlgorithm.StackState.getStackStateForStack(this.mStack));
    this.mLayoutAlgorithm.initialize(this.mDisplayRect, this.mWindowRect, this.mStackBounds, TaskStackLayoutAlgorithm.StackState.getStackStateForStack(this.mStack));
    updateLayoutAlgorithm(false);
    boolean bool;
    if ((i != this.mLastWidth) || (paramInt2 != this.mLastHeight))
    {
      bool = this.mResetToInitialStateWhenResized;
      if ((!this.mAwaitingFirstLayout) && (this.mInitialState == 0)) {
        break label382;
      }
    }
    for (;;)
    {
      if ((this.mInitialState != 2) || (bool))
      {
        updateToInitialState();
        this.mResetToInitialStateWhenResized = false;
      }
      if (!this.mAwaitingFirstLayout) {
        this.mInitialState = 0;
      }
      label382:
      do
      {
        if (this.mLaunchNextAfterFirstMeasure)
        {
          this.mLaunchNextAfterFirstMeasure = false;
          EventBus.getDefault().post(new LaunchNextTaskRequestEvent());
        }
        bindVisibleTaskViews(this.mStackScroller.getStackScroll(), false);
        this.mTmpTaskViews.clear();
        this.mTmpTaskViews.addAll(getTaskViews());
        this.mTmpTaskViews.addAll(this.mViewPool.getViews());
        int j = this.mTmpTaskViews.size();
        paramInt1 = 0;
        while (paramInt1 < j)
        {
          measureTaskView((TaskView)this.mTmpTaskViews.get(paramInt1));
          paramInt1 += 1;
        }
        bool = false;
        break;
      } while (!bool);
    }
    setMeasuredDimension(i, paramInt2);
    this.mLastWidth = i;
    this.mLastHeight = paramInt2;
    this.mInMeasureLayout = false;
  }
  
  public void onPickUpViewFromPool(TaskView paramTaskView, Task paramTask, boolean paramBoolean)
  {
    int i = findTaskViewInsertIndex(paramTask, this.mStack.indexOfStackTask(paramTask));
    if (paramBoolean) {
      if (this.mInMeasureLayout) {
        addView(paramTaskView, i);
      }
    }
    for (;;)
    {
      updateTaskViewsList();
      bindTaskView(paramTaskView, paramTask);
      paramTaskView.setCallbacks(this);
      paramTaskView.setTouchEnabled(true);
      paramTaskView.setClipViewInStack(true);
      if (this.mFocusedTask == paramTask)
      {
        paramTaskView.setFocusedState(true, false);
        if (this.mStartTimerIndicatorDuration > 0)
        {
          paramTaskView.getHeaderView().startFocusTimerIndicator(this.mStartTimerIndicatorDuration);
          this.mStartTimerIndicatorDuration = 0;
        }
      }
      if ((this.mScreenPinningEnabled) && (paramTaskView.getTask() == this.mStack.getStackFrontMostTask(false))) {
        paramTaskView.showActionButton(false, 0);
      }
      return;
      ViewGroup.LayoutParams localLayoutParams = paramTaskView.getLayoutParams();
      Object localObject = localLayoutParams;
      if (localLayoutParams == null) {
        localObject = generateDefaultLayoutParams();
      }
      addViewInLayout(paramTaskView, i, (ViewGroup.LayoutParams)localObject, true);
      measureTaskView(paramTaskView);
      layoutTaskView(true, paramTaskView);
      continue;
      attachViewToParent(paramTaskView, i, paramTaskView.getLayoutParams());
    }
  }
  
  void onReload(boolean paramBoolean)
  {
    if (!paramBoolean) {
      resetFocusedTask(getFocusedTask());
    }
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(getTaskViews());
    localArrayList.addAll(this.mViewPool.getViews());
    int i = localArrayList.size() - 1;
    while (i >= 0)
    {
      ((TaskView)localArrayList.get(i)).onReload(paramBoolean);
      i -= 1;
    }
    readSystemFlags();
    this.mTaskViewsClipDirty = true;
    this.mEnterAnimationComplete = false;
    if (paramBoolean) {
      animateFreeformWorkspaceBackgroundAlpha(this.mLayoutAlgorithm.getStackState().freeformBackgroundAlpha, new AnimationProps(150, Interpolators.FAST_OUT_SLOW_IN));
    }
    for (;;)
    {
      this.mAwaitingFirstLayout = true;
      this.mLaunchNextAfterFirstMeasure = false;
      this.mInitialState = 1;
      requestLayout();
      return;
      this.mStackScroller.reset();
      this.mStableLayoutAlgorithm.reset();
      this.mLayoutAlgorithm.reset();
    }
  }
  
  public void onReturnViewToPool(TaskView paramTaskView)
  {
    unbindTaskView(paramTaskView, paramTaskView.getTask());
    paramTaskView.clearAccessibilityFocus();
    paramTaskView.resetViewProperties();
    paramTaskView.setFocusedState(false, false);
    paramTaskView.setClipViewInStack(false);
    if (this.mScreenPinningEnabled) {
      paramTaskView.hideActionButton(false, 0, false, null);
    }
    detachViewFromParent(paramTaskView);
    updateTaskViewsList();
  }
  
  public void onStackScrollChanged(float paramFloat1, float paramFloat2, AnimationProps paramAnimationProps)
  {
    if (paramAnimationProps != null) {
      relayoutTaskViewsOnNextFrame(paramAnimationProps);
    }
    if (this.mEnterAnimationComplete)
    {
      if ((paramFloat1 <= 0.3F) || (paramFloat2 > 0.3F) || (this.mStack.getTaskCount() <= 0)) {
        break label57;
      }
      EventBus.getDefault().send(new ShowStackActionButtonEvent(true));
    }
    label57:
    while ((paramFloat1 >= 0.3F) || (paramFloat2 < 0.3F)) {
      return;
    }
    EventBus.getDefault().send(new HideStackActionButtonEvent());
  }
  
  public void onStackTaskAdded(TaskStack paramTaskStack, Task paramTask)
  {
    updateLayoutAlgorithm(true);
    if (this.mAwaitingFirstLayout) {}
    for (paramTaskStack = AnimationProps.IMMEDIATE;; paramTaskStack = new AnimationProps(200, Interpolators.FAST_OUT_SLOW_IN))
    {
      relayoutTaskViews(paramTaskStack);
      return;
    }
  }
  
  public void onStackTaskRemoved(TaskStack paramTaskStack, Task paramTask1, Task paramTask2, AnimationProps paramAnimationProps, boolean paramBoolean)
  {
    if (this.mFocusedTask == paramTask1) {
      resetFocusedTask(paramTask1);
    }
    paramTaskStack = getChildViewForTask(paramTask1);
    if (paramTaskStack != null) {
      this.mViewPool.returnViewToPool(paramTaskStack);
    }
    removeIgnoreTask(paramTask1);
    if (paramAnimationProps != null)
    {
      updateLayoutAlgorithm(true);
      relayoutTaskViews(paramAnimationProps);
    }
    if ((this.mScreenPinningEnabled) && (paramTask2 != null))
    {
      paramTaskStack = getChildViewForTask(paramTask2);
      if (paramTaskStack != null) {
        paramTaskStack.showActionButton(true, 200);
      }
    }
    if (this.mStack.getTaskCount() == 0)
    {
      paramTaskStack = EventBus.getDefault();
      if (!paramBoolean) {
        break label119;
      }
    }
    label119:
    for (int i = 2131690324;; i = 2131690325)
    {
      paramTaskStack.send(new AllTaskViewsDismissedEvent(i));
      return;
    }
  }
  
  public void onStackTasksRemoved(TaskStack paramTaskStack)
  {
    resetFocusedTask(getFocusedTask());
    paramTaskStack = new ArrayList();
    paramTaskStack.addAll(getTaskViews());
    int i = paramTaskStack.size() - 1;
    while (i >= 0)
    {
      this.mViewPool.returnViewToPool((TaskView)paramTaskStack.get(i));
      i -= 1;
    }
    this.mIgnoreTasks.clear();
    EventBus.getDefault().send(new AllTaskViewsDismissedEvent(2131690325));
  }
  
  public void onStackTasksUpdated(TaskStack paramTaskStack)
  {
    updateLayoutAlgorithm(false);
    relayoutTaskViews(AnimationProps.IMMEDIATE);
    paramTaskStack = getTaskViews();
    int j = paramTaskStack.size();
    int i = 0;
    while (i < j)
    {
      TaskView localTaskView = (TaskView)paramTaskStack.get(i);
      bindTaskView(localTaskView, localTaskView.getTask());
      i += 1;
    }
  }
  
  public void onTaskViewClipStateChanged(TaskView paramTaskView)
  {
    if (!this.mTaskViewsClipDirty)
    {
      this.mTaskViewsClipDirty = true;
      invalidate();
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (Recents.getSystemServices().isDeepCleaning())
    {
      Log.d("TaskStackView", "return onTouch");
      return true;
    }
    return this.mTouchHandler.onTouchEvent(paramMotionEvent);
  }
  
  public boolean performAccessibilityAction(int paramInt, Bundle paramBundle)
  {
    if (super.performAccessibilityAction(paramInt, paramBundle)) {
      return true;
    }
    paramBundle = getAccessibilityFocusedTask();
    int i = this.mStack.indexOfStackTask(paramBundle);
    if ((i >= 0) && (i < this.mStack.getTaskCount())) {}
    switch (paramInt)
    {
    default: 
      return false;
    case 4096: 
      setFocusedTask(i + 1, true, true, 0);
      return true;
    }
    setFocusedTask(i - 1, true, true, 0);
    return true;
  }
  
  public void relayoutTaskViews(AnimationProps paramAnimationProps)
  {
    relayoutTaskViews(paramAnimationProps, null, false);
  }
  
  void relayoutTaskViewsOnNextFrame(AnimationProps paramAnimationProps)
  {
    this.mDeferredTaskViewLayoutAnimation = paramAnimationProps;
    invalidate();
  }
  
  public void reloadOnConfigurationChange()
  {
    this.mStableLayoutAlgorithm.reloadOnConfigurationChange(getContext());
    this.mLayoutAlgorithm.reloadOnConfigurationChange(getContext());
  }
  
  void removeIgnoreTask(Task paramTask)
  {
    this.mIgnoreTasks.remove(paramTask.key);
  }
  
  void resetFocusedTask(Task paramTask)
  {
    if (paramTask != null)
    {
      paramTask = getChildViewForTask(paramTask);
      if (paramTask != null) {
        paramTask.setFocusedState(false, false);
      }
    }
    this.mFocusedTask = null;
  }
  
  public void setDismissAllBtn(ImageButton paramImageButton)
  {
    this.mDismissAllBtn = paramImageButton;
    if (this.mDismissAllBtn != null) {
      this.mDismissAllBtn.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          Log.d("TaskStackView", "click dismiss all btn");
          TaskStackView.-get5(TaskStackView.this).stopScroller();
          TaskStackView.this.hideDismissAllButton();
          EventBus.getDefault().send(new DismissAllTaskViewsEvent());
          MdmLogger.log("recent_clear", "", "1");
        }
      });
    }
  }
  
  public void setRelativeFocusedTask(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    setRelativeFocusedTask(paramBoolean1, paramBoolean2, paramBoolean3, false, 0);
  }
  
  public void setRelativeFocusedTask(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, int paramInt)
  {
    Object localObject = getFocusedTask();
    int j = this.mStack.indexOfStackTask((Task)localObject);
    int i;
    if (localObject != null)
    {
      if (paramBoolean2)
      {
        ArrayList localArrayList = this.mStack.getStackTasks();
        if (((Task)localObject).isFreeformTask())
        {
          localObject = getFrontMostTaskView(paramBoolean2);
          i = j;
          if (localObject != null) {
            i = this.mStack.indexOfStackTask(((TaskView)localObject).getTask());
          }
          if ((i != -1) && (setFocusedTask(i, true, true, paramInt)) && (paramBoolean4)) {
            EventBus.getDefault().send(new CancelEnterRecentsWindowAnimationEvent(null));
          }
          return;
        }
        if (paramBoolean1) {}
        for (i = -1;; i = 1)
        {
          k = j + i;
          i = j;
          if (k < 0) {
            break;
          }
          i = j;
          if (k >= localArrayList.size()) {
            break;
          }
          i = j;
          if (((Task)localArrayList.get(k)).isFreeformTask()) {
            break;
          }
          i = k;
          break;
        }
      }
      k = this.mStack.getTaskCount();
      if (paramBoolean1) {}
      for (i = -1;; i = 1)
      {
        i = (i + j + k) % k;
        break;
      }
    }
    float f = this.mStackScroller.getStackScroll();
    localObject = this.mStack.getStackTasks();
    int k = ((ArrayList)localObject).size();
    if (paramBoolean1)
    {
      j = k - 1;
      for (;;)
      {
        i = j;
        if (j < 0) {
          break;
        }
        i = j;
        if (Float.compare(this.mLayoutAlgorithm.getStackScrollForTask((Task)((ArrayList)localObject).get(j)), f) <= 0) {
          break;
        }
        j -= 1;
      }
    }
    j = 0;
    for (;;)
    {
      i = j;
      if (j >= k) {
        break;
      }
      i = j;
      if (Float.compare(this.mLayoutAlgorithm.getStackScrollForTask((Task)((ArrayList)localObject).get(j)), f) >= 0) {
        break;
      }
      j += 1;
    }
  }
  
  public void setSystemInsets(Rect paramRect)
  {
    if ((this.mStableLayoutAlgorithm.setSystemInsets(paramRect) | this.mLayoutAlgorithm.setSystemInsets(paramRect))) {
      requestLayout();
    }
  }
  
  public void setTasks(TaskStack paramTaskStack, boolean paramBoolean)
  {
    boolean bool = this.mLayoutAlgorithm.isInitialized();
    TaskStack localTaskStack = this.mStack;
    Context localContext = getContext();
    paramTaskStack = paramTaskStack.computeAllTasksList();
    if (paramBoolean) {}
    for (paramBoolean = bool;; paramBoolean = false)
    {
      localTaskStack.setTasks(localContext, paramTaskStack, paramBoolean);
      return;
    }
  }
  
  public void showDismissAllButton()
  {
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    if ((this.mDismissAllBtn == null) || (this.mStack.getTaskCount() <= 0)) {}
    while ((localSystemServicesProxy.hasDockedTask()) || (!localSystemServicesProxy.isRecentsActivityVisible())) {
      return;
    }
    if ((this.mDismissAllButtonAnimating) || (this.mDismissAllBtn.getVisibility() != 0)) {}
    for (;;)
    {
      this.mDismissAllButtonAnimating = true;
      this.mDismissAllBtn.setVisibility(0);
      this.mDismissAllBtn.setAlpha(0.0F);
      this.mDismissAllBtn.animate().alpha(1.0F).setDuration(250L).withEndAction(new Runnable()
      {
        public void run()
        {
          TaskStackView.-set0(TaskStackView.this, false);
        }
      }).start();
      do
      {
        return;
      } while (Float.compare(this.mDismissAllBtn.getAlpha(), 0.0F) != 0);
    }
  }
  
  public void updateLayoutAlgorithm(boolean paramBoolean)
  {
    this.mLayoutAlgorithm.update(this.mStack, this.mIgnoreTasks);
    if (Recents.getSystemServices().hasFreeformWorkspaceSupport())
    {
      this.mTmpRect.set(this.mLayoutAlgorithm.mFreeformRect);
      this.mFreeformWorkspaceBackground.setBounds(this.mTmpRect);
    }
    if (paramBoolean) {
      this.mStackScroller.boundScroll();
    }
  }
  
  public void updateTaskViewToTransform(TaskView paramTaskView, TaskViewTransform paramTaskViewTransform, AnimationProps paramAnimationProps)
  {
    if (paramTaskView.isAnimatingTo(paramTaskViewTransform))
    {
      paramTaskView = Recents.getSystemServices();
      if (paramTaskView.isDeepCleaning())
      {
        Log.d("TaskStackView", "return isAnimatingTo");
        paramTaskView.setDeepCleaning(false);
        showDismissAllButton();
      }
      return;
    }
    paramTaskView.cancelTransformAnimation();
    paramTaskView.updateViewPropertiesToTaskTransform(paramTaskViewTransform, paramAnimationProps, this.mRequestUpdateClippingListener);
  }
  
  void updateTaskViewsList()
  {
    this.mTaskViews.clear();
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      View localView = getChildAt(i);
      if ((localView instanceof TaskView)) {
        this.mTaskViews.add((TaskView)localView);
      }
      i += 1;
    }
  }
  
  public void updateToInitialState()
  {
    this.mStackScroller.setStackScrollToInitialState();
    this.mLayoutAlgorithm.setTaskOverridesForInitialState(this.mStack, false);
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    if (paramDrawable == this.mFreeformWorkspaceBackground) {
      return true;
    }
    return super.verifyDrawable(paramDrawable);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L, 2L})
  public static @interface InitialStateAction {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\TaskStackView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */