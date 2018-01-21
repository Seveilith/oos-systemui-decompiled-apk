package com.android.systemui.recents.views;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.ArrayMap;
import android.util.Log;
import android.util.MutableBoolean;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Interpolators;
import com.android.systemui.SwipeHelper;
import com.android.systemui.SwipeHelper.Callback;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.HideRecentsEvent;
import com.android.systemui.recents.events.ui.StackViewScrolledEvent;
import com.android.systemui.recents.events.ui.TaskViewDismissedEvent;
import com.android.systemui.recents.misc.FreePathInterpolator;
import com.android.systemui.recents.misc.RectFEvaluator;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.misc.Utilities;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.TaskStack;
import com.android.systemui.statusbar.FlingAnimationUtils;
import java.util.ArrayList;
import java.util.List;

class TaskStackViewTouchHandler
  implements SwipeHelper.Callback
{
  private static final Interpolator OVERSCROLL_INTERP;
  int mActivePointerId = -1;
  TaskView mActiveTaskView = null;
  Context mContext;
  private ArrayList<TaskViewTransform> mCurrentTaskTransforms = new ArrayList();
  private ArrayList<Task> mCurrentTasks = new ArrayList();
  float mDownScrollP;
  int mDownX;
  int mDownY;
  private ArrayList<TaskViewTransform> mFinalTaskTransforms = new ArrayList();
  FlingAnimationUtils mFlingAnimUtils;
  boolean mInterceptedBySwipeHelper;
  @ViewDebug.ExportedProperty(category="recents")
  boolean mIsScrolling;
  int mLastY;
  int mMaximumVelocity;
  int mMinimumVelocity;
  int mOverscrollSize;
  ValueAnimator mScrollFlingAnimator;
  int mScrollTouchSlop;
  TaskStackViewScroller mScroller;
  private final StackViewScrolledEvent mStackViewScrolledEvent = new StackViewScrolledEvent();
  TaskStackView mSv;
  SwipeHelper mSwipeHelper;
  private ArrayMap<View, Animator> mSwipeHelperAnimations = new ArrayMap();
  private float mTargetStackScroll;
  private TaskViewTransform mTmpTransform = new TaskViewTransform();
  VelocityTracker mVelocityTracker;
  final int mWindowTouchSlop;
  
  static
  {
    Path localPath = new Path();
    localPath.moveTo(0.0F, 0.0F);
    localPath.cubicTo(0.2F, 0.175F, 0.25F, 0.3F, 1.0F, 0.3F);
    OVERSCROLL_INTERP = new FreePathInterpolator(localPath);
  }
  
  public TaskStackViewTouchHandler(Context paramContext, TaskStackView paramTaskStackView, TaskStackViewScroller paramTaskStackViewScroller)
  {
    Resources localResources = paramContext.getResources();
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(paramContext);
    this.mContext = paramContext;
    this.mSv = paramTaskStackView;
    this.mScroller = paramTaskStackViewScroller;
    this.mMinimumVelocity = localViewConfiguration.getScaledMinimumFlingVelocity();
    this.mMaximumVelocity = localViewConfiguration.getScaledMaximumFlingVelocity();
    this.mScrollTouchSlop = localViewConfiguration.getScaledTouchSlop();
    this.mWindowTouchSlop = localViewConfiguration.getScaledWindowTouchSlop();
    this.mFlingAnimUtils = new FlingAnimationUtils(paramContext, 0.2F);
    this.mOverscrollSize = localResources.getDimensionPixelSize(2131755619);
    this.mSwipeHelper = new SwipeHelper(0, this, paramContext)
    {
      protected long getMaxEscapeAnimDuration()
      {
        return 700L;
      }
      
      protected float getSize(View paramAnonymousView)
      {
        return TaskStackViewTouchHandler.this.getScaledDismissSize();
      }
      
      protected float getUnscaledEscapeVelocity()
      {
        return 800.0F;
      }
      
      protected void prepareDismissAnimation(View paramAnonymousView, Animator paramAnonymousAnimator)
      {
        TaskStackViewTouchHandler.-get0(TaskStackViewTouchHandler.this).put(paramAnonymousView, paramAnonymousAnimator);
      }
      
      protected void prepareSnapBackAnimation(View paramAnonymousView, Animator paramAnonymousAnimator)
      {
        paramAnonymousAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        TaskStackViewTouchHandler.-get0(TaskStackViewTouchHandler.this).put(paramAnonymousView, paramAnonymousAnimator);
      }
    };
    this.mSwipeHelper.setDisableHardwareLayers(true);
  }
  
  private TaskView findViewAtPoint(int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = this.mSv.getStack().getStackTasks();
    int i = localArrayList.size() - 1;
    while (i >= 0)
    {
      TaskView localTaskView = this.mSv.getChildViewForTask((Task)localArrayList.get(i));
      if ((localTaskView != null) && (localTaskView.getVisibility() == 0) && (this.mSv.isTouchPointInView(paramInt1, paramInt2, localTaskView))) {
        return localTaskView;
      }
      i -= 1;
    }
    return null;
  }
  
  private boolean handleTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mSv.getTaskViews().size() == 0) {
      return false;
    }
    TaskStackLayoutAlgorithm localTaskStackLayoutAlgorithm = this.mSv.mLayoutAlgorithm;
    switch (paramMotionEvent.getAction() & 0xFF)
    {
    }
    for (;;)
    {
      return this.mIsScrolling;
      this.mScroller.stopScroller();
      this.mScroller.stopBoundScrollAnimation();
      this.mScroller.resetDeltaScroll();
      cancelNonDismissTaskAnimations();
      this.mSv.cancelDeferredTaskViewLayoutAnimation();
      this.mDownX = ((int)paramMotionEvent.getX());
      this.mDownY = ((int)paramMotionEvent.getY());
      this.mLastY = this.mDownY;
      this.mDownScrollP = this.mScroller.getStackScroll();
      this.mActivePointerId = paramMotionEvent.getPointerId(0);
      this.mActiveTaskView = findViewAtPoint(this.mDownX, this.mDownY);
      initOrResetVelocityTracker();
      this.mVelocityTracker.addMovement(paramMotionEvent);
      continue;
      if (this.mVelocityTracker == null)
      {
        Log.w("TaskStackViewTouchHandler", "mVelocityTracker is null when pointer down");
      }
      else
      {
        int i = paramMotionEvent.getActionIndex();
        this.mActivePointerId = paramMotionEvent.getPointerId(i);
        try
        {
          this.mDownX = ((int)paramMotionEvent.getX(i));
          this.mDownY = ((int)paramMotionEvent.getY(i));
          this.mLastY = this.mDownY;
          this.mDownScrollP = this.mScroller.getStackScroll();
          this.mScroller.resetDeltaScroll();
          this.mVelocityTracker.addMovement(paramMotionEvent);
        }
        catch (IllegalArgumentException localIllegalArgumentException1)
        {
          for (;;)
          {
            this.mDownX = ((int)paramMotionEvent.getX());
            this.mDownY = ((int)paramMotionEvent.getY());
            Log.w("TaskStackViewTouchHandler", localIllegalArgumentException1);
          }
        }
        if (this.mVelocityTracker == null)
        {
          Log.w("TaskStackViewTouchHandler", "mVelocityTracker is null when move");
        }
        else
        {
          int j = paramMotionEvent.findPointerIndex(this.mActivePointerId);
          float f1;
          label546:
          float f2;
          try
          {
            i = (int)paramMotionEvent.getY(j);
            f1 = paramMotionEvent.getX(j);
            j = (int)f1;
          }
          catch (IllegalArgumentException localIllegalArgumentException3)
          {
            for (;;)
            {
              int k;
              List localList;
              i = (int)paramMotionEvent.getY();
              j = (int)paramMotionEvent.getX();
              Log.w("TaskStackViewTouchHandler", localIllegalArgumentException3);
            }
            localIllegalArgumentException1.setFocusState(0);
            ViewParent localViewParent = this.mSv.getParent();
            if (localViewParent == null) {
              break label546;
            }
            localViewParent.requestDisallowInterceptTouchEvent(true);
            MetricsLogger.action(this.mSv.getContext(), 287);
            if (!this.mIsScrolling) {
              break label714;
            }
            f1 = localIllegalArgumentException1.getDeltaPForY(this.mDownY, i);
            float f3 = localIllegalArgumentException1.mMinScrollP;
            float f4 = localIllegalArgumentException1.mMaxScrollP;
            f2 = this.mDownScrollP + f1;
            if (f2 < f3) {
              break label615;
            }
            f1 = f2;
            if (f2 <= f4) {
              break label664;
            }
            label615:
            f1 = Utilities.clamp(f2, f3, f4);
            f2 -= f1;
            f3 = Math.abs(f2) / 2.3333333F;
            f3 = OVERSCROLL_INTERP.getInterpolation(f3);
            f1 += Math.signum(f2) * (2.3333333F * f3);
            label664:
            this.mDownScrollP += this.mScroller.setDeltaStackScroll(this.mDownScrollP, f1 - this.mDownScrollP);
            this.mStackViewScrolledEvent.updateY(i - this.mLastY);
            EventBus.getDefault().send(this.mStackViewScrolledEvent);
            label714:
            this.mLastY = i;
            this.mVelocityTracker.addMovement(paramMotionEvent);
          }
          if (!this.mIsScrolling)
          {
            k = Math.abs(i - this.mDownY);
            j = Math.abs(j - this.mDownX);
            if ((Math.abs(i - this.mDownY) > this.mScrollTouchSlop) && (k > j))
            {
              this.mIsScrolling = true;
              f1 = this.mScroller.getStackScroll();
              localList = this.mSv.getTaskViews();
              j = localList.size() - 1;
              while (j >= 0)
              {
                localIllegalArgumentException1.addUnfocusedTaskOverride(((TaskView)localList.get(j)).getTask(), f1);
                j -= 1;
              }
            }
          }
          continue;
          if (this.mVelocityTracker == null)
          {
            Log.w("TaskStackViewTouchHandler", "mVelocityTracker is null when pointer up");
          }
          else
          {
            j = paramMotionEvent.getActionIndex();
            if (paramMotionEvent.getPointerId(j) == this.mActivePointerId)
            {
              if (j != 0) {
                break label840;
              }
              i = 1;
              this.mActivePointerId = paramMotionEvent.getPointerId(i);
            }
            try
            {
              this.mDownX = ((int)paramMotionEvent.getX(j));
              this.mDownY = ((int)paramMotionEvent.getY(j));
              this.mLastY = this.mDownY;
              this.mDownScrollP = this.mScroller.getStackScroll();
              this.mVelocityTracker.addMovement(paramMotionEvent);
              continue;
              label840:
              i = 0;
            }
            catch (IllegalArgumentException localIllegalArgumentException2)
            {
              for (;;)
              {
                this.mDownX = ((int)paramMotionEvent.getX());
                this.mDownY = ((int)paramMotionEvent.getY());
                Log.w("TaskStackViewTouchHandler", localIllegalArgumentException2);
              }
            }
            if (this.mVelocityTracker == null)
            {
              Log.w("TaskStackViewTouchHandler", "mVelocityTracker is null when up");
            }
            else
            {
              this.mVelocityTracker.addMovement(paramMotionEvent);
              this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
              i = paramMotionEvent.findPointerIndex(this.mActivePointerId);
              try
              {
                f1 = paramMotionEvent.getY(i);
                i = (int)f1;
              }
              catch (IllegalArgumentException localIllegalArgumentException4)
              {
                for (;;)
                {
                  i = (int)paramMotionEvent.getY();
                  Log.w("TaskStackViewTouchHandler", localIllegalArgumentException4);
                  continue;
                  if (Math.abs(j) > this.mMinimumVelocity)
                  {
                    f1 = this.mDownY + localIllegalArgumentException2.getYForDeltaP(this.mDownScrollP, localIllegalArgumentException2.mMaxScrollP);
                    f2 = this.mDownY + localIllegalArgumentException2.getYForDeltaP(this.mDownScrollP, localIllegalArgumentException2.mMinScrollP);
                    this.mScroller.fling(this.mDownScrollP, this.mDownY, i, j, (int)f1, (int)f2, this.mOverscrollSize);
                    this.mSv.invalidate();
                    continue;
                    if (this.mActiveTaskView == null) {
                      maybeHideRecentsFromBackgroundTap((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
                    }
                  }
                }
              }
              j = (int)this.mVelocityTracker.getYVelocity(this.mActivePointerId);
              if (this.mIsScrolling) {
                if (this.mScroller.isScrollOutOfBounds())
                {
                  this.mScroller.animateBoundScroll();
                  if (!this.mSv.mTouchExplorationEnabled) {
                    this.mSv.resetFocusedTask(this.mSv.getFocusedTask());
                  }
                  this.mActivePointerId = -1;
                  this.mIsScrolling = false;
                  recycleVelocityTracker();
                  continue;
                }
              }
              this.mActivePointerId = -1;
              this.mIsScrolling = false;
              recycleVelocityTracker();
            }
          }
        }
      }
    }
  }
  
  private void updateTaskViewTransforms(float paramFloat)
  {
    List localList = this.mSv.getTaskViews();
    int j = localList.size();
    int i = 0;
    if (i < j)
    {
      TaskView localTaskView = (TaskView)localList.get(i);
      Object localObject = localTaskView.getTask();
      if (this.mSv.isIgnoredTask((Task)localObject)) {}
      for (;;)
      {
        i += 1;
        break;
        int k = this.mCurrentTasks.indexOf(localObject);
        if (k != -1)
        {
          localObject = (TaskViewTransform)this.mCurrentTaskTransforms.get(k);
          TaskViewTransform localTaskViewTransform = (TaskViewTransform)this.mFinalTaskTransforms.get(k);
          this.mTmpTransform.copyFrom((TaskViewTransform)localObject);
          this.mTmpTransform.rect.set(Utilities.RECTF_EVALUATOR.evaluate(paramFloat, ((TaskViewTransform)localObject).rect, localTaskViewTransform.rect));
          this.mTmpTransform.dimAlpha = (((TaskViewTransform)localObject).dimAlpha + (localTaskViewTransform.dimAlpha - ((TaskViewTransform)localObject).dimAlpha) * paramFloat);
          this.mTmpTransform.viewOutlineAlpha = (((TaskViewTransform)localObject).viewOutlineAlpha + (localTaskViewTransform.viewOutlineAlpha - ((TaskViewTransform)localObject).viewOutlineAlpha) * paramFloat);
          this.mTmpTransform.translationZ = (((TaskViewTransform)localObject).translationZ + (localTaskViewTransform.translationZ - ((TaskViewTransform)localObject).translationZ) * paramFloat);
          this.mSv.updateTaskViewToTransform(localTaskView, this.mTmpTransform, AnimationProps.IMMEDIATE);
        }
      }
    }
  }
  
  public boolean canChildBeDismissed(View paramView)
  {
    Task localTask = ((TaskView)paramView).getTask();
    return (!this.mSwipeHelperAnimations.containsKey(paramView)) && (this.mSv.getStack().indexOfStackTask(localTask) != -1) && (!localTask.isLocked);
  }
  
  public void cancelNonDismissTaskAnimations()
  {
    Utilities.cancelAnimationWithoutCallbacks(this.mScrollFlingAnimator);
    if (!this.mSwipeHelperAnimations.isEmpty())
    {
      List localList = this.mSv.getTaskViews();
      int i = localList.size() - 1;
      if (i >= 0)
      {
        TaskView localTaskView = (TaskView)localList.get(i);
        if (this.mSv.isIgnoredTask(localTaskView.getTask())) {}
        for (;;)
        {
          i -= 1;
          break;
          localTaskView.cancelTransformAnimation();
          this.mSv.getStackAlgorithm().addUnfocusedTaskOverride(localTaskView, this.mTargetStackScroll);
        }
      }
      this.mSv.getStackAlgorithm().setFocusState(0);
      this.mSv.getScroller().setStackScroll(this.mTargetStackScroll, null);
      this.mSwipeHelperAnimations.clear();
    }
    this.mActiveTaskView = null;
  }
  
  public View getChildAtPosition(MotionEvent paramMotionEvent)
  {
    int j = 0;
    paramMotionEvent = findViewAtPoint((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
    if (paramMotionEvent != null)
    {
      int i = j;
      if (!this.mSwipeHelperAnimations.containsKey(paramMotionEvent))
      {
        i = j;
        if (this.mSv.getStack().indexOfStackTask(paramMotionEvent.getTask()) != -1) {
          i = 1;
        }
      }
      if (i != 0) {
        return paramMotionEvent;
      }
      return null;
    }
    return null;
  }
  
  public float getFalsingThresholdFactor()
  {
    return 0.0F;
  }
  
  public float getScaledDismissSize()
  {
    return Math.max(this.mSv.getWidth(), this.mSv.getHeight()) * 1.5F;
  }
  
  void initOrResetVelocityTracker()
  {
    if (this.mVelocityTracker == null)
    {
      this.mVelocityTracker = VelocityTracker.obtain();
      return;
    }
    this.mVelocityTracker.clear();
  }
  
  public boolean isAntiFalsingNeeded()
  {
    return false;
  }
  
  void maybeHideRecentsFromBackgroundTap(int paramInt1, int paramInt2)
  {
    int i = Math.abs(this.mDownX - paramInt1);
    int j = Math.abs(this.mDownY - paramInt2);
    if ((i > this.mScrollTouchSlop) || (j > this.mScrollTouchSlop)) {
      return;
    }
    if (paramInt1 > (this.mSv.getRight() - this.mSv.getLeft()) / 2) {}
    for (i = paramInt1 - this.mWindowTouchSlop; findViewAtPoint(i, paramInt2) != null; i = paramInt1 + this.mWindowTouchSlop) {
      return;
    }
    if ((paramInt1 > this.mSv.mLayoutAlgorithm.mStackRect.left) && (paramInt1 < this.mSv.mLayoutAlgorithm.mStackRect.right)) {
      return;
    }
    if (Recents.getSystemServices().hasFreeformWorkspaceSupport())
    {
      Rect localRect = this.mSv.mLayoutAlgorithm.mFreeformRect;
      if ((localRect.top <= paramInt2) && (paramInt2 <= localRect.bottom) && (this.mSv.launchFreeformTasks())) {
        return;
      }
    }
    EventBus.getDefault().send(new HideRecentsEvent(false, true));
  }
  
  public void onBeginDrag(View paramView)
  {
    paramView = (TaskView)paramView;
    paramView.setClipViewInStack(false);
    paramView.setTouchEnabled(false);
    Object localObject = this.mSv.getParent();
    if (localObject != null) {
      ((ViewParent)localObject).requestDisallowInterceptTouchEvent(true);
    }
    this.mSv.addIgnoreTask(paramView.getTask());
    this.mCurrentTasks = new ArrayList(this.mSv.getStack().getStackTasks());
    paramView = new MutableBoolean(false);
    localObject = this.mSv.findAnchorTask(this.mCurrentTasks, paramView);
    TaskStackLayoutAlgorithm localTaskStackLayoutAlgorithm = this.mSv.getStackAlgorithm();
    TaskStackViewScroller localTaskStackViewScroller = this.mSv.getScroller();
    float f2;
    int i;
    float f1;
    if (localObject != null)
    {
      this.mSv.getCurrentTaskTransforms(this.mCurrentTasks, this.mCurrentTaskTransforms);
      f2 = 0.0F;
      if (this.mCurrentTasks.size() <= 0) {
        break label219;
      }
      i = 1;
      if (i != 0) {
        f2 = localTaskStackLayoutAlgorithm.getStackScrollForTask((Task)localObject);
      }
      this.mSv.updateLayoutAlgorithm(false);
      f1 = localTaskStackViewScroller.getStackScroll();
      if (!paramView.value) {
        break label225;
      }
      f1 = localTaskStackViewScroller.getBoundedStackScroll(f1);
    }
    for (;;)
    {
      this.mSv.bindVisibleTaskViews(f1, true);
      this.mSv.getLayoutTaskTransforms(f1, 0, this.mCurrentTasks, true, this.mFinalTaskTransforms);
      this.mTargetStackScroll = f1;
      return;
      label219:
      i = 0;
      break;
      label225:
      if (i != 0)
      {
        f2 = localTaskStackLayoutAlgorithm.getStackScrollForTaskIgnoreOverrides((Task)localObject) - f2;
        f1 = f2;
        if (localTaskStackLayoutAlgorithm.getFocusState() != 1) {
          f1 = f2 * 0.75F;
        }
        f1 = localTaskStackViewScroller.getBoundedStackScroll(localTaskStackViewScroller.getStackScroll() + f1);
      }
    }
  }
  
  public void onBeginManualDrag(TaskView paramTaskView)
  {
    this.mActiveTaskView = paramTaskView;
    this.mSwipeHelperAnimations.put(paramTaskView, null);
    onBeginDrag(paramTaskView);
  }
  
  public void onChildDismissed(View paramView)
  {
    TaskView localTaskView = (TaskView)paramView;
    localTaskView.setClipViewInStack(true);
    localTaskView.setTouchEnabled(true);
    EventBus localEventBus = EventBus.getDefault();
    Task localTask = localTaskView.getTask();
    if (this.mSwipeHelperAnimations.containsKey(paramView)) {}
    for (AnimationProps localAnimationProps = new AnimationProps(200, Interpolators.FAST_OUT_SLOW_IN);; localAnimationProps = null)
    {
      localEventBus.send(new TaskViewDismissedEvent(localTask, localTaskView, localAnimationProps));
      if (this.mSwipeHelperAnimations.containsKey(paramView))
      {
        this.mSv.getScroller().setStackScroll(this.mTargetStackScroll, null);
        this.mSv.getStackAlgorithm().setFocusState(0);
        this.mSv.getStackAlgorithm().clearUnfocusedTaskOverrides();
        this.mSwipeHelperAnimations.remove(paramView);
      }
      MetricsLogger.histogram(localTaskView.getContext(), "overview_task_dismissed_source", 1);
      return;
    }
  }
  
  public void onChildSnappedBack(View paramView, float paramFloat)
  {
    TaskView localTaskView = (TaskView)paramView;
    localTaskView.setClipViewInStack(true);
    localTaskView.setTouchEnabled(true);
    this.mSv.removeIgnoreTask(localTaskView.getTask());
    this.mSv.updateLayoutAlgorithm(false);
    this.mSv.relayoutTaskViews(AnimationProps.IMMEDIATE);
    this.mSwipeHelperAnimations.remove(paramView);
  }
  
  public void onDragCancelled(View paramView) {}
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    if ((paramMotionEvent.getSource() & 0x2) == 2) {}
    switch (paramMotionEvent.getAction() & 0xFF)
    {
    default: 
      return false;
    }
    if (paramMotionEvent.getAxisValue(9) > 0.0F)
    {
      this.mSv.setRelativeFocusedTask(true, true, false);
      return true;
    }
    this.mSv.setRelativeFocusedTask(false, true, false);
    return true;
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    this.mInterceptedBySwipeHelper = this.mSwipeHelper.onInterceptTouchEvent(paramMotionEvent);
    if (this.mInterceptedBySwipeHelper) {
      return true;
    }
    return handleTouchEvent(paramMotionEvent);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mInterceptedBySwipeHelper) && (this.mSwipeHelper.onTouchEvent(paramMotionEvent))) {
      return true;
    }
    handleTouchEvent(paramMotionEvent);
    return true;
  }
  
  void recycleVelocityTracker()
  {
    if (this.mVelocityTracker != null)
    {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    }
  }
  
  public boolean updateSwipeProgress(View paramView, boolean paramBoolean, float paramFloat)
  {
    if ((this.mActiveTaskView == paramView) || (this.mSwipeHelperAnimations.containsKey(paramView))) {
      updateTaskViewTransforms(Interpolators.FAST_OUT_SLOW_IN.getInterpolation(paramFloat));
    }
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\TaskStackViewTouchHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */