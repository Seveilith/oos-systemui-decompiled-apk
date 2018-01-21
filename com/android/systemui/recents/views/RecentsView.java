package com.android.systemui.recents.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions.OnAnimationStartedListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.view.AppTransitionAnimationSpec;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewPropertyAnimator;
import android.view.WindowInsets;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Interpolators;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsActivityLaunchState;
import com.android.systemui.recents.RecentsConfiguration;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.DismissRecentsToHomeAnimationStarted;
import com.android.systemui.recents.events.activity.DockedFirstAnimationFrameEvent;
import com.android.systemui.recents.events.activity.EnterRecentsWindowAnimationCompletedEvent;
import com.android.systemui.recents.events.activity.HideStackActionButtonEvent;
import com.android.systemui.recents.events.activity.LaunchTaskEvent;
import com.android.systemui.recents.events.activity.MultiWindowStateChangedEvent;
import com.android.systemui.recents.events.activity.ShowStackActionButtonEvent;
import com.android.systemui.recents.events.ui.AllTaskViewsDismissedEvent;
import com.android.systemui.recents.events.ui.DismissAllTaskViewsEvent;
import com.android.systemui.recents.events.ui.DraggingInRecentsEndedEvent;
import com.android.systemui.recents.events.ui.DraggingInRecentsEvent;
import com.android.systemui.recents.events.ui.dragndrop.DragDropTargetChangedEvent;
import com.android.systemui.recents.events.ui.dragndrop.DragEndCancelledEvent;
import com.android.systemui.recents.events.ui.dragndrop.DragEndEvent;
import com.android.systemui.recents.events.ui.dragndrop.DragStartEvent;
import com.android.systemui.recents.misc.ReferenceCountedTrigger;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.misc.Utilities;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.Task.TaskKey;
import com.android.systemui.recents.model.TaskStack;
import com.android.systemui.recents.model.TaskStack.DockState;
import com.android.systemui.recents.model.TaskStack.DockState.ViewState;
import com.android.systemui.stackdivider.WindowManagerProxy;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.util.MdmLogger;
import com.android.systemui.util.Utils;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class RecentsView
  extends FrameLayout
{
  private boolean mAttached = false;
  private boolean mAwaitingFirstLayout = true;
  private Drawable mBackgroundScrim = new ColorDrawable(Color.argb(84, 0, 0, 0)).mutate();
  private Animator mBackgroundScrimAnimator;
  private int mDismissAllBottomMargin;
  ImageButton mDismissAllBtn;
  private int mDividerSize;
  private TextView mEmptyView;
  private final FlingAnimationUtils mFlingAnimationUtils;
  private boolean mLastTaskLaunchedWasFreeform;
  private boolean mNeedToRedoAnimation;
  private TaskStack mStack;
  private TextView mStackActionButton;
  @ViewDebug.ExportedProperty(category="recents")
  Rect mSystemInsets = new Rect();
  private TaskStackView mTaskStackView;
  @ViewDebug.ExportedProperty(deepExport=true, prefix="touch_")
  private RecentsViewTouchHandler mTouchHandler;
  private RecentsTransitionHelper mTransitionHelper;
  
  public RecentsView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public RecentsView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public RecentsView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public RecentsView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setWillNotDraw(false);
    paramAttributeSet = Recents.getSystemServices();
    this.mTransitionHelper = new RecentsTransitionHelper(getContext());
    this.mDividerSize = paramAttributeSet.getDockedDividerSize(paramContext);
    this.mTouchHandler = new RecentsViewTouchHandler(this);
    this.mFlingAnimationUtils = new FlingAnimationUtils(paramContext, 0.3F);
    this.mEmptyView = ((TextView)LayoutInflater.from(paramContext).inflate(2130968789, this, false));
    addView(this.mEmptyView);
    this.mDismissAllBottomMargin = paramContext.getResources().getDimensionPixelSize(2131755697);
  }
  
  private void animateBackgroundScrim(float paramFloat, int paramInt)
  {
    Utilities.cancelAnimationWithoutCallbacks(this.mBackgroundScrimAnimator);
    int i = (int)(this.mBackgroundScrim.getAlpha() / 84.15F * 255.0F);
    int j = (int)(paramFloat * 255.0F);
    this.mBackgroundScrimAnimator = ObjectAnimator.ofInt(this.mBackgroundScrim, Utilities.DRAWABLE_ALPHA, new int[] { i, j });
    this.mBackgroundScrimAnimator.setDuration(paramInt);
    Animator localAnimator = this.mBackgroundScrimAnimator;
    if (j > i) {}
    for (Interpolator localInterpolator = Interpolators.ALPHA_IN;; localInterpolator = Interpolators.ALPHA_OUT)
    {
      localAnimator.setInterpolator(localInterpolator);
      this.mBackgroundScrimAnimator.start();
      return;
    }
  }
  
  private Rect getStackActionButtonBoundsFromStackLayout()
  {
    Rect localRect = new Rect(this.mTaskStackView.mLayoutAlgorithm.mStackActionButtonRect);
    if (isLayoutRtl()) {}
    for (int i = localRect.left - this.mStackActionButton.getPaddingLeft();; i = localRect.right + this.mStackActionButton.getPaddingRight() - this.mStackActionButton.getMeasuredWidth())
    {
      int j = localRect.top + (localRect.height() - this.mStackActionButton.getMeasuredHeight()) / 2;
      localRect.set(i, j, this.mStackActionButton.getMeasuredWidth() + i, this.mStackActionButton.getMeasuredHeight() + j);
      return localRect;
    }
  }
  
  private Rect getTaskRect(TaskView paramTaskView)
  {
    int[] arrayOfInt = paramTaskView.getLocationOnScreen();
    int i = arrayOfInt[0];
    int j = arrayOfInt[1];
    return new Rect(i, j, (int)(i + paramTaskView.getWidth() * paramTaskView.getScaleX()), (int)(j + paramTaskView.getHeight() * paramTaskView.getScaleY()));
  }
  
  private void hideStackActionButton(int paramInt, boolean paramBoolean) {}
  
  private void hideStackActionButton(int paramInt, boolean paramBoolean, ReferenceCountedTrigger paramReferenceCountedTrigger) {}
  
  private void showStackActionButton(int paramInt, boolean paramBoolean) {}
  
  private void updateHeaderDockButton()
  {
    List localList = this.mTaskStackView.getTaskViews();
    int j = localList.size();
    int i = 0;
    while (i < j)
    {
      ((TaskView)localList.get(i)).getHeaderView().updateHeaderDockButton();
      i += 1;
    }
  }
  
  private void updateVisibleDockRegions(TaskStack.DockState[] paramArrayOfDockState, boolean paramBoolean1, int paramInt1, int paramInt2, boolean paramBoolean2, boolean paramBoolean3)
  {
    ArraySet localArraySet = Utilities.arrayToSet(paramArrayOfDockState, new ArraySet());
    ArrayList localArrayList = this.mTouchHandler.getVisibleDockStates();
    int i = localArrayList.size() - 1;
    if (i >= 0)
    {
      Object localObject = (TaskStack.DockState)localArrayList.get(i);
      TaskStack.DockState.ViewState localViewState = ((TaskStack.DockState)localObject).viewState;
      int j;
      label77:
      int k;
      if ((paramArrayOfDockState != null) && (localArraySet.contains(localObject)))
      {
        if (paramInt1 == -1) {
          break label192;
        }
        j = paramInt1;
        if (paramInt2 == -1) {
          break label202;
        }
        k = paramInt2;
        label87:
        if (!paramBoolean1) {
          break label212;
        }
      }
      label192:
      label202:
      label212:
      for (localObject = ((TaskStack.DockState)localObject).getPreDockedBounds(getMeasuredWidth(), getMeasuredHeight(), this.mSystemInsets);; localObject = ((TaskStack.DockState)localObject).getDockedBounds(getMeasuredWidth(), getMeasuredHeight(), this.mDividerSize, this.mSystemInsets, getResources()))
      {
        if (localViewState.dockAreaOverlay.getCallback() != this)
        {
          localViewState.dockAreaOverlay.setCallback(this);
          localViewState.dockAreaOverlay.setBounds((Rect)localObject);
        }
        localViewState.startAnimation((Rect)localObject, j, k, 250, Interpolators.FAST_OUT_SLOW_IN, paramBoolean2, paramBoolean3);
        for (;;)
        {
          i -= 1;
          break;
          localViewState.startAnimation(null, 0, 0, 250, Interpolators.FAST_OUT_SLOW_IN, paramBoolean2, paramBoolean3);
        }
        j = localViewState.dockAreaAlpha;
        break label77;
        k = localViewState.hintTextAlpha;
        break label87;
      }
    }
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    String str1 = paramString + "  ";
    String str2 = Integer.toHexString(System.identityHashCode(this));
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("RecentsView");
    paramPrintWriter.print(" awaitingFirstLayout=");
    if (this.mAwaitingFirstLayout) {}
    for (paramString = "Y";; paramString = "N")
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(" insets=");
      paramPrintWriter.print(Utilities.dumpRect(this.mSystemInsets));
      paramPrintWriter.print(" [0x");
      paramPrintWriter.print(str2);
      paramPrintWriter.print("]");
      paramPrintWriter.println();
      if (this.mStack != null) {
        this.mStack.dump(str1, paramPrintWriter);
      }
      if (this.mTaskStackView != null) {
        this.mTaskStackView.dump(str1, paramPrintWriter);
      }
      return;
    }
  }
  
  public Drawable getBackgroundScrim()
  {
    return this.mBackgroundScrim;
  }
  
  public TaskStack getStack()
  {
    return this.mStack;
  }
  
  public void hideEmptyView()
  {
    this.mEmptyView.setVisibility(4);
    this.mTaskStackView.setVisibility(0);
    this.mTaskStackView.bringToFront();
  }
  
  public boolean isLastTaskLaunchedFreeform()
  {
    return this.mLastTaskLaunchedWasFreeform;
  }
  
  public boolean launchFocusedTask(int paramInt)
  {
    if (this.mTaskStackView != null)
    {
      Task localTask = this.mTaskStackView.getFocusedTask();
      if (localTask != null)
      {
        TaskView localTaskView = this.mTaskStackView.getChildViewForTask(localTask);
        EventBus.getDefault().send(new LaunchTaskEvent(localTaskView, localTask, null, -1, false));
        if (paramInt != 0) {
          MetricsLogger.action(getContext(), paramInt, localTask.key.getComponent().toString());
        }
        return true;
      }
    }
    return false;
  }
  
  public boolean launchPreviousTask()
  {
    if (this.mTaskStackView != null)
    {
      Task localTask = this.mTaskStackView.getStack().getLaunchTarget();
      if (localTask != null)
      {
        TaskView localTaskView = this.mTaskStackView.getChildViewForTask(localTask);
        EventBus.getDefault().send(new LaunchTaskEvent(localTaskView, localTask, null, -1, false));
        return true;
      }
    }
    return false;
  }
  
  public void notifyEnterAnimationComplete()
  {
    if (!this.mAttached)
    {
      this.mNeedToRedoAnimation = true;
      Log.d("RecentsView", "notify to redo complete animation");
    }
  }
  
  public WindowInsets onApplyWindowInsets(WindowInsets paramWindowInsets)
  {
    this.mSystemInsets.set(paramWindowInsets.getSystemWindowInsets());
    this.mTaskStackView.setSystemInsets(this.mSystemInsets);
    if (this.mDismissAllBtn != null)
    {
      Rect localRect = paramWindowInsets.getSystemWindowInsets();
      ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.mDismissAllBtn.getLayoutParams();
      if (localMarginLayoutParams != null)
      {
        localMarginLayoutParams.leftMargin = (localRect.left / 2);
        localMarginLayoutParams.rightMargin = (localRect.right / 2);
        localMarginLayoutParams.bottomMargin = (this.mDismissAllBottomMargin + localRect.bottom);
      }
    }
    requestLayout();
    return paramWindowInsets;
  }
  
  protected void onAttachedToWindow()
  {
    EventBus.getDefault().register(this, 3);
    EventBus.getDefault().register(this.mTouchHandler, 4);
    super.onAttachedToWindow();
    this.mAttached = true;
    if (this.mNeedToRedoAnimation)
    {
      this.mNeedToRedoAnimation = false;
      new Handler().post(new Runnable()
      {
        public void run()
        {
          Log.d("RecentsView", "re send CompletedEvent");
          EventBus.getDefault().send(new EnterRecentsWindowAnimationCompletedEvent());
        }
      });
    }
  }
  
  public final void onBusEvent(DismissRecentsToHomeAnimationStarted paramDismissRecentsToHomeAnimationStarted)
  {
    animateBackgroundScrim(0.0F, 200);
  }
  
  public final void onBusEvent(EnterRecentsWindowAnimationCompletedEvent paramEnterRecentsWindowAnimationCompletedEvent)
  {
    paramEnterRecentsWindowAnimationCompletedEvent = Recents.getConfiguration().getLaunchState();
    if ((paramEnterRecentsWindowAnimationCompletedEvent.launchedViaDockGesture) || (paramEnterRecentsWindowAnimationCompletedEvent.launchedFromApp)) {}
    for (;;)
    {
      updateHeaderDockButton();
      return;
      if (this.mStack.getTaskCount() > 0) {
        animateBackgroundScrim(1.0F, 200);
      }
    }
  }
  
  public final void onBusEvent(HideStackActionButtonEvent paramHideStackActionButtonEvent) {}
  
  public final void onBusEvent(LaunchTaskEvent paramLaunchTaskEvent)
  {
    this.mLastTaskLaunchedWasFreeform = paramLaunchTaskEvent.task.isFreeformTask();
    this.mTransitionHelper.launchTaskFromRecents(this.mStack, paramLaunchTaskEvent.task, this.mTaskStackView, paramLaunchTaskEvent.taskView, paramLaunchTaskEvent.screenPinningRequested, paramLaunchTaskEvent.targetTaskBounds, paramLaunchTaskEvent.targetTaskStack);
  }
  
  public final void onBusEvent(MultiWindowStateChangedEvent paramMultiWindowStateChangedEvent)
  {
    updateStack(paramMultiWindowStateChangedEvent.stack, false);
    updateHeaderDockButton();
  }
  
  public final void onBusEvent(ShowStackActionButtonEvent paramShowStackActionButtonEvent) {}
  
  public final void onBusEvent(AllTaskViewsDismissedEvent paramAllTaskViewsDismissedEvent)
  {
    hideStackActionButton(100, true);
  }
  
  public final void onBusEvent(DismissAllTaskViewsEvent paramDismissAllTaskViewsEvent)
  {
    if (!Recents.getSystemServices().hasDockedTask()) {
      animateBackgroundScrim(0.0F, 200);
    }
  }
  
  public final void onBusEvent(DraggingInRecentsEndedEvent paramDraggingInRecentsEndedEvent)
  {
    ViewPropertyAnimator localViewPropertyAnimator = animate();
    if (paramDraggingInRecentsEndedEvent.velocity > this.mFlingAnimationUtils.getMinVelocityPxPerSecond())
    {
      localViewPropertyAnimator.translationY(getHeight());
      localViewPropertyAnimator.withEndAction(new Runnable()
      {
        public void run()
        {
          WindowManagerProxy.getInstance().maximizeDockedStack();
        }
      });
      this.mFlingAnimationUtils.apply(localViewPropertyAnimator, getTranslationY(), getHeight(), paramDraggingInRecentsEndedEvent.velocity);
    }
    for (;;)
    {
      localViewPropertyAnimator.start();
      return;
      localViewPropertyAnimator.translationY(0.0F);
      localViewPropertyAnimator.setListener(null);
      this.mFlingAnimationUtils.apply(localViewPropertyAnimator, getTranslationY(), 0.0F, paramDraggingInRecentsEndedEvent.velocity);
    }
  }
  
  public final void onBusEvent(DraggingInRecentsEvent paramDraggingInRecentsEvent)
  {
    if (this.mTaskStackView.getTaskViews().size() > 0) {
      setTranslationY(paramDraggingInRecentsEvent.distanceFromTop - ((TaskView)this.mTaskStackView.getTaskViews().get(0)).getY());
    }
  }
  
  public final void onBusEvent(DragDropTargetChangedEvent paramDragDropTargetChangedEvent)
  {
    if ((paramDragDropTargetChangedEvent.dropTarget != null) && ((paramDragDropTargetChangedEvent.dropTarget instanceof TaskStack.DockState))) {
      updateVisibleDockRegions(new TaskStack.DockState[] { (TaskStack.DockState)paramDragDropTargetChangedEvent.dropTarget }, false, -1, -1, true, true);
    }
    for (;;)
    {
      if (this.mStackActionButton != null) {
        paramDragDropTargetChangedEvent.addPostAnimationCallback(new Runnable()
        {
          public void run()
          {
            Rect localRect = RecentsView.-wrap0(RecentsView.this);
            RecentsView.-get0(RecentsView.this).setLeftTopRightBottom(localRect.left, localRect.top, localRect.right, localRect.bottom);
          }
        });
      }
      return;
      updateVisibleDockRegions(this.mTouchHandler.getDockStatesForCurrentOrientation(), true, TaskStack.DockState.NONE.viewState.dockAreaAlpha, TaskStack.DockState.NONE.viewState.hintTextAlpha, true, true);
    }
  }
  
  public final void onBusEvent(DragEndCancelledEvent paramDragEndCancelledEvent)
  {
    updateVisibleDockRegions(null, true, -1, -1, true, false);
  }
  
  public final void onBusEvent(final DragEndEvent paramDragEndEvent)
  {
    if ((paramDragEndEvent.dropTarget instanceof TaskStack.DockState))
    {
      Object localObject = (TaskStack.DockState)paramDragEndEvent.dropTarget;
      updateVisibleDockRegions(null, false, -1, -1, false, false);
      Utilities.setViewFrameFromTranslation(paramDragEndEvent.taskView);
      SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
      if (localSystemServicesProxy.startTaskInDockedMode(paramDragEndEvent.task.key.id, ((TaskStack.DockState)localObject).createMode))
      {
        localObject = new ActivityOptions.OnAnimationStartedListener()
        {
          public void onAnimationStarted()
          {
            EventBus.getDefault().send(new DockedFirstAnimationFrameEvent());
            RecentsView.-get1(RecentsView.this).getStack().removeTask(paramDragEndEvent.task, null, true);
          }
        };
        final Rect localRect = getTaskRect(paramDragEndEvent.taskView);
        localSystemServicesProxy.overridePendingAppTransitionMultiThumbFuture(this.mTransitionHelper.getAppTransitionFuture(new RecentsTransitionHelper.AnimationSpecComposer()
        {
          public List<AppTransitionAnimationSpec> composeSpecs()
          {
            return RecentsView.-get2(RecentsView.this).composeDockAnimationSpec(paramDragEndEvent.taskView, localRect);
          }
        }), this.mTransitionHelper.wrapStartedListener((ActivityOptions.OnAnimationStartedListener)localObject), true);
        MetricsLogger.action(this.mContext, 270, paramDragEndEvent.task.getTopComponent().flattenToShortString());
        MdmLogger.log("recent_split", "", "1");
      }
    }
    for (;;)
    {
      if (this.mStackActionButton != null) {
        this.mStackActionButton.animate().alpha(1.0F).setDuration(134L).setInterpolator(Interpolators.ALPHA_IN).start();
      }
      return;
      EventBus.getDefault().send(new DragEndCancelledEvent(this.mStack, paramDragEndEvent.task, paramDragEndEvent.taskView));
      continue;
      updateVisibleDockRegions(null, true, -1, -1, true, false);
    }
  }
  
  public final void onBusEvent(DragStartEvent paramDragStartEvent)
  {
    updateVisibleDockRegions(this.mTouchHandler.getDockStatesForCurrentOrientation(), true, TaskStack.DockState.NONE.viewState.dockAreaAlpha, TaskStack.DockState.NONE.viewState.hintTextAlpha, true, false);
    if (this.mStackActionButton != null) {
      this.mStackActionButton.animate().alpha(0.0F).setDuration(100L).setInterpolator(Interpolators.ALPHA_OUT).start();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    EventBus.getDefault().unregister(this);
    EventBus.getDefault().unregister(this.mTouchHandler);
    this.mAttached = false;
  }
  
  public void onDrawForeground(Canvas paramCanvas)
  {
    super.onDrawForeground(paramCanvas);
    ArrayList localArrayList = this.mTouchHandler.getVisibleDockStates();
    int i = localArrayList.size() - 1;
    while (i >= 0)
    {
      ((TaskStack.DockState)localArrayList.get(i)).viewState.draw(paramCanvas);
      i -= 1;
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    return this.mTouchHandler.onInterceptTouchEvent(paramMotionEvent);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mTaskStackView.getVisibility() != 8) {
      this.mTaskStackView.layout(paramInt1, paramInt2, getMeasuredWidth() + paramInt1, getMeasuredHeight() + paramInt2);
    }
    if (this.mEmptyView.getVisibility() != 8)
    {
      int n = this.mSystemInsets.left;
      int i1 = this.mSystemInsets.right;
      int k = this.mSystemInsets.top;
      int m = this.mSystemInsets.bottom;
      int i = this.mEmptyView.getMeasuredWidth();
      int j = this.mEmptyView.getMeasuredHeight();
      paramInt1 = this.mSystemInsets.left + paramInt1 + Math.max(0, paramInt3 - paramInt1 - (n + i1) - i) / 2;
      paramInt2 = this.mSystemInsets.top + paramInt2 + Math.max(0, paramInt4 - paramInt2 - (k + m) - j) / 2;
      this.mEmptyView.layout(paramInt1, paramInt2, paramInt1 + i, paramInt2 + j);
    }
    if (this.mAwaitingFirstLayout)
    {
      this.mAwaitingFirstLayout = false;
      if (Recents.getConfiguration().getLaunchState().launchedViaDragGesture) {
        setTranslationY(getMeasuredHeight());
      }
    }
    else
    {
      return;
    }
    setTranslationY(0.0F);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getSize(paramInt1);
    int j = View.MeasureSpec.getSize(paramInt2);
    if (this.mTaskStackView.getVisibility() != 8) {
      this.mTaskStackView.measure(paramInt1, paramInt2);
    }
    if (this.mEmptyView.getVisibility() != 8) {
      measureChild(this.mEmptyView, View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(j, Integer.MIN_VALUE));
    }
    setMeasuredDimension(i, j);
  }
  
  public void onReload(boolean paramBoolean1, boolean paramBoolean2)
  {
    RecentsActivityLaunchState localRecentsActivityLaunchState = Recents.getConfiguration().getLaunchState();
    if (this.mTaskStackView == null)
    {
      paramBoolean1 = false;
      this.mTaskStackView = new TaskStackView(getContext());
      this.mTaskStackView.setSystemInsets(this.mSystemInsets);
      addView(this.mTaskStackView);
      this.mTaskStackView.setDismissAllBtn(this.mDismissAllBtn);
    }
    if (paramBoolean1) {}
    for (boolean bool = false;; bool = true)
    {
      this.mAwaitingFirstLayout = bool;
      this.mLastTaskLaunchedWasFreeform = false;
      this.mTaskStackView.onReload(paramBoolean1);
      if (!paramBoolean1) {
        break;
      }
      animateBackgroundScrim(1.0F, 200);
      return;
    }
    if ((localRecentsActivityLaunchState.launchedViaDockGesture) || (localRecentsActivityLaunchState.launchedFromApp) || (paramBoolean2))
    {
      this.mBackgroundScrim.setAlpha(255);
      return;
    }
    this.mBackgroundScrim.setAlpha(0);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    return this.mTouchHandler.onTouchEvent(paramMotionEvent);
  }
  
  public void setDismissAllBtn(ImageButton paramImageButton)
  {
    this.mDismissAllBtn = paramImageButton;
  }
  
  public void showDismissAllButton()
  {
    if (this.mTaskStackView != null) {
      this.mTaskStackView.showDismissAllButton();
    }
  }
  
  public void showEmptyView(int paramInt)
  {
    this.mTaskStackView.setVisibility(4);
    this.mEmptyView.setText(paramInt);
    this.mEmptyView.setVisibility(0);
    this.mEmptyView.bringToFront();
    if (this.mTaskStackView != null) {
      this.mTaskStackView.hideDismissAllButton();
    }
  }
  
  public void updateStack(TaskStack paramTaskStack, boolean paramBoolean)
  {
    this.mStack = paramTaskStack;
    if (paramBoolean) {
      this.mTaskStackView.setTasks(paramTaskStack, true);
    }
    if (Utils.DEBUG_ONEPLUS) {
      Log.d("RecentsView", "updateStack , size:" + paramTaskStack.getTaskCount());
    }
    if (paramTaskStack.getTaskCount() > 0)
    {
      hideEmptyView();
      showDismissAllButton();
      return;
    }
    showEmptyView(2131690324);
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    ArrayList localArrayList = this.mTouchHandler.getVisibleDockStates();
    int i = localArrayList.size() - 1;
    while (i >= 0)
    {
      if (((TaskStack.DockState)localArrayList.get(i)).viewState.dockAreaOverlay == paramDrawable) {
        return true;
      }
      i -= 1;
    }
    return super.verifyDrawable(paramDrawable);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\RecentsView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */