package com.android.systemui.stackdivider;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.InternalInsetsInfo;
import android.view.ViewTreeObserver.OnComputeInternalInsetsListener;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.policy.DividerSnapAlgorithm;
import com.android.internal.policy.DividerSnapAlgorithm.SnapTarget;
import com.android.internal.policy.DockedDividerUtils;
import com.android.systemui.Interpolators;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.DockedTopTaskEvent;
import com.android.systemui.recents.events.activity.RecentsActivityStartingEvent;
import com.android.systemui.recents.events.activity.UndockingTaskEvent;
import com.android.systemui.recents.events.ui.RecentsDrawnEvent;
import com.android.systemui.recents.events.ui.RecentsGrowingEvent;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.stackdivider.events.StartedDragingEvent;
import com.android.systemui.statusbar.FlingAnimationUtils;

public class DividerView
  extends FrameLayout
  implements View.OnTouchListener, ViewTreeObserver.OnComputeInternalInsetsListener
{
  private static final PathInterpolator DIM_INTERPOLATOR = new PathInterpolator(0.23F, 0.87F, 0.52F, -0.11F);
  private static final Interpolator IME_ADJUST_INTERPOLATOR = new PathInterpolator(0.2F, 0.0F, 0.1F, 1.0F);
  private static final PathInterpolator SLOWDOWN_INTERPOLATOR = new PathInterpolator(0.5F, 1.0F, 0.5F, 1.0F);
  private boolean mAdjustedForIme;
  private View mBackground;
  private boolean mBackgroundLifted;
  private ValueAnimator mCurrentAnimator;
  private int mDisplayHeight;
  private final Rect mDisplayRect = new Rect();
  private int mDisplayWidth;
  private int mDividerInsets;
  private int mDividerSize;
  private int mDividerWindowWidth;
  private int mDockSide;
  private final Rect mDockedInsetRect = new Rect();
  private final Rect mDockedRect = new Rect();
  private boolean mDockedStackMinimized;
  private final Rect mDockedTaskRect = new Rect();
  private boolean mEntranceAnimationRunning;
  private boolean mExitAnimationRunning;
  private int mExitStartPosition;
  private FlingAnimationUtils mFlingAnimationUtils;
  private GestureDetector mGestureDetector;
  private boolean mGrowRecents;
  private DividerHandleView mHandle;
  private final View.AccessibilityDelegate mHandleDelegate = new View.AccessibilityDelegate()
  {
    public void onInitializeAccessibilityNodeInfo(View paramAnonymousView, AccessibilityNodeInfo paramAnonymousAccessibilityNodeInfo)
    {
      super.onInitializeAccessibilityNodeInfo(paramAnonymousView, paramAnonymousAccessibilityNodeInfo);
      if (DividerView.this.isHorizontalDivision())
      {
        paramAnonymousAccessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(2131951685, DividerView.-get0(DividerView.this).getString(2131690613)));
        if (DividerView.-get2(DividerView.this).isFirstSplitTargetAvailable()) {
          paramAnonymousAccessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(2131951686, DividerView.-get0(DividerView.this).getString(2131690614)));
        }
        paramAnonymousAccessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(2131951687, DividerView.-get0(DividerView.this).getString(2131690615)));
        if (DividerView.-get2(DividerView.this).isLastSplitTargetAvailable()) {
          paramAnonymousAccessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(2131951688, DividerView.-get0(DividerView.this).getString(2131690616)));
        }
        paramAnonymousAccessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(2131951689, DividerView.-get0(DividerView.this).getString(2131690617)));
        return;
      }
      paramAnonymousAccessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(2131951685, DividerView.-get0(DividerView.this).getString(2131690608)));
      if (DividerView.-get2(DividerView.this).isFirstSplitTargetAvailable()) {
        paramAnonymousAccessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(2131951686, DividerView.-get0(DividerView.this).getString(2131690609)));
      }
      paramAnonymousAccessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(2131951687, DividerView.-get0(DividerView.this).getString(2131690610)));
      if (DividerView.-get2(DividerView.this).isLastSplitTargetAvailable()) {
        paramAnonymousAccessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(2131951688, DividerView.-get0(DividerView.this).getString(2131690611)));
      }
      paramAnonymousAccessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(2131951689, DividerView.-get0(DividerView.this).getString(2131690612)));
    }
    
    public boolean performAccessibilityAction(View paramAnonymousView, int paramAnonymousInt, Bundle paramAnonymousBundle)
    {
      int i = DividerView.this.getCurrentPosition();
      DividerSnapAlgorithm.SnapTarget localSnapTarget = null;
      switch (paramAnonymousInt)
      {
      }
      while (localSnapTarget != null)
      {
        DividerView.this.startDragging(true, false);
        DividerView.this.stopDragging(i, localSnapTarget, 250L, Interpolators.FAST_OUT_SLOW_IN);
        return true;
        localSnapTarget = DividerView.-get2(DividerView.this).getDismissEndTarget();
        continue;
        localSnapTarget = DividerView.-get2(DividerView.this).getLastSplitTarget();
        continue;
        localSnapTarget = DividerView.-get2(DividerView.this).getMiddleTarget();
        continue;
        localSnapTarget = DividerView.-get2(DividerView.this).getFirstSplitTarget();
        continue;
        localSnapTarget = DividerView.-get2(DividerView.this).getDismissStartTarget();
      }
      return super.performAccessibilityAction(paramAnonymousView, paramAnonymousInt, paramAnonymousBundle);
    }
  };
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        super.handleMessage(paramAnonymousMessage);
        return;
      }
      DividerView.this.resizeStack(paramAnonymousMessage.arg1, paramAnonymousMessage.arg2, (DividerSnapAlgorithm.SnapTarget)paramAnonymousMessage.obj);
    }
  };
  private final Rect mLastResizeRect = new Rect();
  private int mLongPressEntraceAnimDuration;
  private MinimizedDockShadow mMinimizedShadow;
  private boolean mMoving;
  private final Rect mOtherInsetRect = new Rect();
  private final Rect mOtherRect = new Rect();
  private final Rect mOtherTaskRect = new Rect();
  private final Runnable mResetBackgroundRunnable = new Runnable()
  {
    public void run()
    {
      DividerView.-wrap0(DividerView.this);
    }
  };
  private DividerSnapAlgorithm mSnapAlgorithm;
  private final Rect mStableInsets = new Rect();
  private int mStartPosition;
  private int mStartX;
  private int mStartY;
  private DividerState mState;
  private long mSurfaceFlingerOffsetMs;
  private final int[] mTempInt2 = new int[2];
  private int mTouchElevation;
  private int mTouchSlop;
  private VelocityTracker mVelocityTracker;
  private DividerWindowManager mWindowManager;
  private final WindowManagerProxy mWindowManagerProxy = WindowManagerProxy.getInstance();
  
  public DividerView(Context paramContext)
  {
    super(paramContext);
  }
  
  public DividerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public DividerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public DividerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  private void alignBottomRight(Rect paramRect1, Rect paramRect2)
  {
    int i = paramRect2.width();
    int j = paramRect2.height();
    paramRect2.set(paramRect1.right - i, paramRect1.bottom - j, paramRect1.right, paramRect1.bottom);
  }
  
  private void alignTopLeft(Rect paramRect1, Rect paramRect2)
  {
    int i = paramRect2.width();
    int j = paramRect2.height();
    paramRect2.set(paramRect1.left, paramRect1.top, paramRect1.left + i, paramRect1.top + j);
  }
  
  private void applyDismissingParallax(Rect paramRect, int paramInt1, DividerSnapAlgorithm.SnapTarget paramSnapTarget, int paramInt2, int paramInt3)
  {
    float f = Math.min(1.0F, Math.max(0.0F, this.mSnapAlgorithm.calculateDismissingFraction(paramInt2)));
    Object localObject2 = null;
    Object localObject3 = null;
    int i = 0;
    Object localObject1;
    if ((paramInt2 <= this.mSnapAlgorithm.getLastSplitTarget().position) && (dockSideTopLeft(paramInt1)))
    {
      paramSnapTarget = this.mSnapAlgorithm.getDismissStartTarget();
      localObject1 = this.mSnapAlgorithm.getFirstSplitTarget();
    }
    for (;;)
    {
      if ((paramSnapTarget != null) && (f > 0.0F) && (isDismissing((DividerSnapAlgorithm.SnapTarget)localObject1, paramInt2, paramInt1)))
      {
        f = calculateParallaxDismissingFraction(f, paramInt1);
        paramInt2 = (int)(paramInt3 + (paramSnapTarget.position - ((DividerSnapAlgorithm.SnapTarget)localObject1).position) * f);
        paramInt3 = paramRect.width();
        i = paramRect.height();
      }
      switch (paramInt1)
      {
      default: 
        return;
        paramSnapTarget = (DividerSnapAlgorithm.SnapTarget)localObject2;
        localObject1 = localObject3;
        paramInt3 = i;
        if (paramInt2 >= this.mSnapAlgorithm.getLastSplitTarget().position)
        {
          paramSnapTarget = (DividerSnapAlgorithm.SnapTarget)localObject2;
          localObject1 = localObject3;
          paramInt3 = i;
          if (dockSideBottomRight(paramInt1))
          {
            paramSnapTarget = this.mSnapAlgorithm.getDismissEndTarget();
            localObject1 = this.mSnapAlgorithm.getLastSplitTarget();
            paramInt3 = ((DividerSnapAlgorithm.SnapTarget)localObject1).position;
          }
        }
        break;
      }
    }
    paramRect.left = (paramInt2 - paramInt3);
    paramRect.right = paramInt2;
    return;
    paramRect.left = (this.mDividerSize + paramInt2);
    paramRect.right = (paramInt2 + paramInt3 + this.mDividerSize);
    return;
    paramRect.top = (paramInt2 - i);
    paramRect.bottom = paramInt2;
    return;
    paramRect.top = (this.mDividerSize + paramInt2);
    paramRect.bottom = (paramInt2 + i + this.mDividerSize);
  }
  
  private void applyExitAnimationParallax(Rect paramRect, int paramInt)
  {
    if (this.mDockSide == 2) {
      paramRect.offset(0, (int)((paramInt - this.mExitStartPosition) * 0.25F));
    }
    do
    {
      return;
      if (this.mDockSide == 1)
      {
        paramRect.offset((int)((paramInt - this.mExitStartPosition) * 0.25F), 0);
        return;
      }
    } while (this.mDockSide != 3);
    paramRect.offset((int)((this.mExitStartPosition - paramInt) * 0.25F), 0);
  }
  
  private long calculateAppSurfaceFlingerVsyncOffsetMs()
  {
    Display localDisplay = getDisplay();
    return Math.max(0L, ((1.0E9F / localDisplay.getRefreshRate()) - (localDisplay.getPresentationDeadlineNanos() - 1000000L) - localDisplay.getAppVsyncOffsetNanos()) / 1000000L);
  }
  
  private static float calculateParallaxDismissingFraction(float paramFloat, int paramInt)
  {
    float f = SLOWDOWN_INTERPOLATOR.getInterpolation(paramFloat) / 3.5F;
    paramFloat = f;
    if (paramInt == 2) {
      paramFloat = f / 2.0F;
    }
    return paramFloat;
  }
  
  private int calculatePosition(int paramInt1, int paramInt2)
  {
    if (isHorizontalDivision()) {
      return calculateYPosition(paramInt2);
    }
    return calculateXPosition(paramInt1);
  }
  
  private int calculateXPosition(int paramInt)
  {
    return this.mStartPosition + paramInt - this.mStartX;
  }
  
  private int calculateYPosition(int paramInt)
  {
    return this.mStartPosition + paramInt - this.mStartY;
  }
  
  private void cancelFlingAnimation()
  {
    if (this.mCurrentAnimator != null) {
      this.mCurrentAnimator.cancel();
    }
  }
  
  private void commitSnapFlags(DividerSnapAlgorithm.SnapTarget paramSnapTarget)
  {
    if (paramSnapTarget.flag == 0) {
      return;
    }
    int i;
    if (paramSnapTarget.flag == 1) {
      if (this.mDockSide != 1)
      {
        if (this.mDockSide != 2) {
          break label61;
        }
        i = 1;
        if (i == 0) {
          break label97;
        }
        this.mWindowManagerProxy.dismissDockedStack();
      }
    }
    for (;;)
    {
      this.mWindowManagerProxy.setResizeDimLayer(false, -1, 0.0F);
      return;
      i = 1;
      break;
      label61:
      i = 0;
      break;
      if (this.mDockSide != 3)
      {
        if (this.mDockSide != 4) {
          break label92;
        }
        i = 1;
        break;
      }
      i = 1;
      break;
      label92:
      i = 0;
      break;
      label97:
      this.mWindowManagerProxy.maximizeDockedStack();
    }
  }
  
  private void convertToScreenCoordinates(MotionEvent paramMotionEvent)
  {
    paramMotionEvent.setLocation(paramMotionEvent.getRawX(), paramMotionEvent.getRawY());
  }
  
  private static boolean dockSideBottomRight(int paramInt)
  {
    return (paramInt == 4) || (paramInt == 3);
  }
  
  private static boolean dockSideTopLeft(int paramInt)
  {
    return (paramInt == 2) || (paramInt == 1);
  }
  
  private void fling(int paramInt, float paramFloat, boolean paramBoolean1, boolean paramBoolean2)
  {
    Object localObject2 = this.mSnapAlgorithm.calculateSnapTarget(paramInt, paramFloat);
    Object localObject1 = localObject2;
    if (paramBoolean1)
    {
      localObject1 = localObject2;
      if (localObject2 == this.mSnapAlgorithm.getDismissStartTarget()) {
        localObject1 = this.mSnapAlgorithm.getFirstSplitTarget();
      }
    }
    if (paramBoolean2) {
      logResizeEvent((DividerSnapAlgorithm.SnapTarget)localObject1);
    }
    localObject2 = getFlingAnimator(paramInt, (DividerSnapAlgorithm.SnapTarget)localObject1, 0L);
    this.mFlingAnimationUtils.apply((Animator)localObject2, paramInt, ((DividerSnapAlgorithm.SnapTarget)localObject1).position, paramFloat);
    ((ValueAnimator)localObject2).start();
  }
  
  private void flingTo(int paramInt, DividerSnapAlgorithm.SnapTarget paramSnapTarget, long paramLong1, long paramLong2, long paramLong3, Interpolator paramInterpolator)
  {
    paramSnapTarget = getFlingAnimator(paramInt, paramSnapTarget, paramLong3);
    paramSnapTarget.setDuration(paramLong1);
    paramSnapTarget.setStartDelay(paramLong2);
    paramSnapTarget.setInterpolator(paramInterpolator);
    paramSnapTarget.start();
  }
  
  private float getDimFraction(int paramInt, DividerSnapAlgorithm.SnapTarget paramSnapTarget)
  {
    if (this.mEntranceAnimationRunning) {
      return 0.0F;
    }
    float f1 = Math.max(0.0F, Math.min(this.mSnapAlgorithm.calculateDismissingFraction(paramInt), 1.0F));
    float f2 = DIM_INTERPOLATOR.getInterpolation(f1);
    f1 = f2;
    if (hasInsetsAtDismissTarget(paramSnapTarget)) {
      f1 = f2 * 0.8F;
    }
    return f1;
  }
  
  private ValueAnimator getFlingAnimator(int paramInt, DividerSnapAlgorithm.SnapTarget paramSnapTarget, final long paramLong)
  {
    if (paramSnapTarget.flag == 0) {}
    for (boolean bool = true;; bool = false)
    {
      ValueAnimator localValueAnimator = ValueAnimator.ofInt(new int[] { paramInt, paramSnapTarget.position });
      localValueAnimator.addUpdateListener(new -android_animation_ValueAnimator_getFlingAnimator_int_position_com_android_internal_policy_DividerSnapAlgorithm.SnapTarget_snapTarget_long_endDelay_LambdaImpl0(bool, paramSnapTarget));
      localValueAnimator.addListener(new AnimatorListenerAdapter()
      {
        private boolean mCancelled;
        
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          DividerView.-get1(DividerView.this).removeMessages(0);
          this.mCancelled = true;
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          long l = 0L;
          if (paramLong != 0L) {
            l = paramLong;
          }
          while (l == 0L)
          {
            this.val$endAction.run();
            return;
            if (this.mCancelled) {
              l = 0L;
            } else if (DividerView.-get3(DividerView.this) != 0L) {
              l = DividerView.-get3(DividerView.this);
            }
          }
          DividerView.-get1(DividerView.this).postDelayed(this.val$endAction, l);
        }
      });
      this.mCurrentAnimator = localValueAnimator;
      return localValueAnimator;
    }
  }
  
  private int getStackIdForDismissTarget(DividerSnapAlgorithm.SnapTarget paramSnapTarget)
  {
    if (((paramSnapTarget.flag == 1) && (dockSideTopLeft(this.mDockSide))) || ((paramSnapTarget.flag == 2) && (dockSideBottomRight(this.mDockSide)))) {
      return 3;
    }
    return 0;
  }
  
  private boolean hasInsetsAtDismissTarget(DividerSnapAlgorithm.SnapTarget paramSnapTarget)
  {
    if (isHorizontalDivision())
    {
      if (paramSnapTarget == this.mSnapAlgorithm.getDismissStartTarget()) {
        return this.mStableInsets.top != 0;
      }
      return this.mStableInsets.bottom != 0;
    }
    if (paramSnapTarget == this.mSnapAlgorithm.getDismissStartTarget()) {
      return this.mStableInsets.left != 0;
    }
    return this.mStableInsets.right != 0;
  }
  
  private void initializeSnapAlgorithm()
  {
    if (this.mSnapAlgorithm == null) {
      this.mSnapAlgorithm = new DividerSnapAlgorithm(getContext().getResources(), this.mDisplayWidth, this.mDisplayHeight, this.mDividerSize, isHorizontalDivision(), this.mStableInsets);
    }
  }
  
  private static boolean isDismissing(DividerSnapAlgorithm.SnapTarget paramSnapTarget, int paramInt1, int paramInt2)
  {
    if ((paramInt2 == 2) || (paramInt2 == 1)) {
      return paramInt1 < paramSnapTarget.position;
    }
    return paramInt1 > paramSnapTarget.position;
  }
  
  private void liftBackground()
  {
    if (this.mBackgroundLifted) {
      return;
    }
    if (isHorizontalDivision()) {
      this.mBackground.animate().scaleY(1.4F);
    }
    for (;;)
    {
      this.mBackground.animate().setInterpolator(Interpolators.TOUCH_RESPONSE).setDuration(150L).translationZ(this.mTouchElevation).start();
      this.mHandle.animate().setInterpolator(Interpolators.TOUCH_RESPONSE).setDuration(150L).translationZ(this.mTouchElevation).start();
      this.mBackgroundLifted = true;
      return;
      this.mBackground.animate().scaleX(1.4F);
    }
  }
  
  private void logResizeEvent(DividerSnapAlgorithm.SnapTarget paramSnapTarget)
  {
    int i = 2;
    int k = 1;
    int m = 1;
    int j = 1;
    if (paramSnapTarget == this.mSnapAlgorithm.getDismissStartTarget())
    {
      paramSnapTarget = this.mContext;
      if (dockSideTopLeft(this.mDockSide))
      {
        i = j;
        MetricsLogger.action(paramSnapTarget, 390, i);
      }
    }
    do
    {
      return;
      i = 0;
      break;
      if (paramSnapTarget == this.mSnapAlgorithm.getDismissEndTarget())
      {
        paramSnapTarget = this.mContext;
        if (dockSideBottomRight(this.mDockSide)) {}
        for (i = k;; i = 0)
        {
          MetricsLogger.action(paramSnapTarget, 390, i);
          return;
        }
      }
      if (paramSnapTarget == this.mSnapAlgorithm.getMiddleTarget())
      {
        MetricsLogger.action(this.mContext, 389, 0);
        return;
      }
      if (paramSnapTarget == this.mSnapAlgorithm.getFirstSplitTarget())
      {
        paramSnapTarget = this.mContext;
        if (dockSideTopLeft(this.mDockSide)) {}
        for (i = m;; i = 2)
        {
          MetricsLogger.action(paramSnapTarget, 389, i);
          return;
        }
      }
    } while (paramSnapTarget != this.mSnapAlgorithm.getLastSplitTarget());
    paramSnapTarget = this.mContext;
    if (dockSideTopLeft(this.mDockSide)) {}
    for (;;)
    {
      MetricsLogger.action(paramSnapTarget, 389, i);
      return;
      i = 1;
    }
  }
  
  private void releaseBackground()
  {
    if (!this.mBackgroundLifted) {
      return;
    }
    this.mBackground.animate().setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setDuration(200L).translationZ(0.0F).scaleX(1.0F).scaleY(1.0F).start();
    this.mHandle.animate().setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setDuration(200L).translationZ(0.0F).start();
    this.mBackgroundLifted = false;
  }
  
  private void resetBackground()
  {
    this.mBackground.setPivotX(this.mBackground.getWidth() / 2);
    this.mBackground.setPivotY(this.mBackground.getHeight() / 2);
    this.mBackground.setScaleX(1.0F);
    this.mBackground.setScaleY(1.0F);
    this.mMinimizedShadow.setAlpha(0.0F);
  }
  
  private int restrictDismissingTaskPosition(int paramInt1, int paramInt2, DividerSnapAlgorithm.SnapTarget paramSnapTarget)
  {
    if ((paramSnapTarget.flag == 1) && (dockSideTopLeft(paramInt2))) {
      return Math.max(this.mSnapAlgorithm.getFirstSplitTarget().position, this.mStartPosition);
    }
    if ((paramSnapTarget.flag == 2) && (dockSideBottomRight(paramInt2))) {
      return Math.min(this.mSnapAlgorithm.getLastSplitTarget().position, this.mStartPosition);
    }
    return paramInt1;
  }
  
  private void stopDragging()
  {
    this.mHandle.setTouching(false, true);
    this.mWindowManager.setSlippery(true);
    releaseBackground();
  }
  
  private void updateDisplayInfo()
  {
    Display localDisplay = ((DisplayManager)this.mContext.getSystemService("display")).getDisplay(0);
    DisplayInfo localDisplayInfo = new DisplayInfo();
    localDisplay.getDisplayInfo(localDisplayInfo);
    this.mDisplayWidth = localDisplayInfo.logicalWidth;
    this.mDisplayHeight = localDisplayInfo.logicalHeight;
    this.mSnapAlgorithm = null;
    initializeSnapAlgorithm();
  }
  
  private void updateDockSide()
  {
    this.mDockSide = this.mWindowManagerProxy.getDockSide();
    this.mMinimizedShadow.setDockSide(this.mDockSide);
  }
  
  public void calculateBoundsForPosition(int paramInt1, int paramInt2, Rect paramRect)
  {
    DockedDividerUtils.calculateBoundsForPosition(paramInt1, paramInt2, paramRect, this.mDisplayWidth, this.mDisplayHeight, this.mDividerSize);
  }
  
  public int getCurrentPosition()
  {
    getLocationOnScreen(this.mTempInt2);
    if (isHorizontalDivision()) {
      return this.mTempInt2[1] + this.mDividerInsets;
    }
    return this.mTempInt2[0] + this.mDividerInsets;
  }
  
  public DividerSnapAlgorithm getSnapAlgorithm()
  {
    initializeSnapAlgorithm();
    return this.mSnapAlgorithm;
  }
  
  public WindowManagerProxy getWindowManagerProxy()
  {
    return this.mWindowManagerProxy;
  }
  
  public int growsRecents()
  {
    int i = 0;
    if ((this.mGrowRecents) && (this.mWindowManagerProxy.getDockSide() == 2)) {
      if (getCurrentPosition() != getSnapAlgorithm().getLastSplitTarget().position) {}
    }
    for (i = 1; i != 0; i = 0) {
      return getSnapAlgorithm().getMiddleTarget().position;
    }
    return -1;
  }
  
  public void injectDependencies(DividerWindowManager paramDividerWindowManager, DividerState paramDividerState)
  {
    this.mWindowManager = paramDividerWindowManager;
    this.mState = paramDividerState;
  }
  
  public boolean isHorizontalDivision()
  {
    return getResources().getConfiguration().orientation == 1;
  }
  
  public void notifyDockSideChanged(int paramInt)
  {
    this.mDockSide = paramInt;
    this.mMinimizedShadow.setDockSide(this.mDockSide);
    requestLayout();
  }
  
  public WindowInsets onApplyWindowInsets(WindowInsets paramWindowInsets)
  {
    if ((this.mStableInsets.left != paramWindowInsets.getStableInsetLeft()) || (this.mStableInsets.top != paramWindowInsets.getStableInsetTop())) {}
    for (;;)
    {
      this.mStableInsets.set(paramWindowInsets.getStableInsetLeft(), paramWindowInsets.getStableInsetTop(), paramWindowInsets.getStableInsetRight(), paramWindowInsets.getStableInsetBottom());
      if (this.mSnapAlgorithm != null)
      {
        this.mSnapAlgorithm = null;
        initializeSnapAlgorithm();
      }
      do
      {
        return super.onApplyWindowInsets(paramWindowInsets);
        if (this.mStableInsets.right != paramWindowInsets.getStableInsetRight()) {
          break;
        }
      } while (this.mStableInsets.bottom == paramWindowInsets.getStableInsetBottom());
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    EventBus.getDefault().register(this);
    this.mSurfaceFlingerOffsetMs = calculateAppSurfaceFlingerVsyncOffsetMs();
  }
  
  public final void onBusEvent(DockedTopTaskEvent paramDockedTopTaskEvent)
  {
    if (paramDockedTopTaskEvent.dragMode == -1)
    {
      this.mState.growAfterRecentsDrawn = false;
      this.mState.animateAfterRecentsDrawn = true;
      startDragging(false, false);
    }
    updateDockSide();
    int i = DockedDividerUtils.calculatePositionForBounds(paramDockedTopTaskEvent.initialRect, this.mDockSide, this.mDividerSize);
    this.mEntranceAnimationRunning = true;
    if (this.mStableInsets.isEmpty())
    {
      SystemServicesProxy.getInstance(this.mContext).getStableInsets(this.mStableInsets);
      this.mSnapAlgorithm = null;
      initializeSnapAlgorithm();
    }
    resizeStack(i, this.mSnapAlgorithm.getMiddleTarget().position, this.mSnapAlgorithm.getMiddleTarget());
  }
  
  public final void onBusEvent(RecentsActivityStartingEvent paramRecentsActivityStartingEvent)
  {
    if ((this.mGrowRecents) && (getWindowManagerProxy().getDockSide() == 2) && (getCurrentPosition() == getSnapAlgorithm().getLastSplitTarget().position))
    {
      this.mState.growAfterRecentsDrawn = true;
      startDragging(false, false);
    }
  }
  
  public final void onBusEvent(UndockingTaskEvent paramUndockingTaskEvent)
  {
    int i = this.mWindowManagerProxy.getDockSide();
    if ((i == -1) || (this.mDockedStackMinimized)) {
      return;
    }
    startDragging(false, false);
    if (dockSideTopLeft(i)) {}
    for (paramUndockingTaskEvent = this.mSnapAlgorithm.getDismissEndTarget();; paramUndockingTaskEvent = this.mSnapAlgorithm.getDismissStartTarget())
    {
      this.mExitAnimationRunning = true;
      this.mExitStartPosition = getCurrentPosition();
      stopDragging(this.mExitStartPosition, paramUndockingTaskEvent, 336L, 100L, 0L, Interpolators.FAST_OUT_SLOW_IN);
      return;
    }
  }
  
  public final void onBusEvent(RecentsDrawnEvent paramRecentsDrawnEvent)
  {
    if (this.mState.animateAfterRecentsDrawn)
    {
      this.mState.animateAfterRecentsDrawn = false;
      updateDockSide();
      this.mHandler.post(new -void_onBusEvent_com_android_systemui_recents_events_ui_RecentsDrawnEvent_drawnEvent_LambdaImpl0());
    }
    if (this.mState.growAfterRecentsDrawn)
    {
      this.mState.growAfterRecentsDrawn = false;
      updateDockSide();
      EventBus.getDefault().send(new RecentsGrowingEvent());
      stopDragging(getCurrentPosition(), this.mSnapAlgorithm.getMiddleTarget(), 336L, Interpolators.FAST_OUT_SLOW_IN);
    }
  }
  
  public void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo paramInternalInsetsInfo)
  {
    paramInternalInsetsInfo.setTouchableInsets(3);
    paramInternalInsetsInfo.touchableRegion.set(this.mHandle.getLeft(), this.mHandle.getTop(), this.mHandle.getRight(), this.mHandle.getBottom());
    paramInternalInsetsInfo.touchableRegion.op(this.mBackground.getLeft(), this.mBackground.getTop(), this.mBackground.getRight(), this.mBackground.getBottom(), Region.Op.UNION);
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    updateDisplayInfo();
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    EventBus.getDefault().unregister(this);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mHandle = ((DividerHandleView)findViewById(2131951828));
    this.mBackground = findViewById(2131951826);
    this.mMinimizedShadow = ((MinimizedDockShadow)findViewById(2131951827));
    this.mHandle.setOnTouchListener(this);
    this.mDividerWindowWidth = getResources().getDimensionPixelSize(17104931);
    this.mDividerInsets = getResources().getDimensionPixelSize(17104932);
    this.mDividerSize = (this.mDividerWindowWidth - this.mDividerInsets * 2);
    this.mTouchElevation = getResources().getDimensionPixelSize(2131755577);
    this.mLongPressEntraceAnimDuration = getResources().getInteger(2131624001);
    this.mGrowRecents = getResources().getBoolean(2131558427);
    this.mTouchSlop = ViewConfiguration.get(this.mContext).getScaledTouchSlop();
    this.mFlingAnimationUtils = new FlingAnimationUtils(getContext(), 0.3F);
    updateDisplayInfo();
    DividerHandleView localDividerHandleView;
    Context localContext;
    if (getResources().getConfiguration().orientation == 2)
    {
      i = 1;
      localDividerHandleView = this.mHandle;
      localContext = getContext();
      if (i == 0) {
        break label263;
      }
    }
    label263:
    for (int i = 1014;; i = 1015)
    {
      localDividerHandleView.setPointerIcon(PointerIcon.getSystemIcon(localContext, i));
      getViewTreeObserver().addOnComputeInternalInsetsListener(this);
      this.mHandle.setAccessibilityDelegate(this.mHandleDelegate);
      this.mGestureDetector = new GestureDetector(this.mContext, new GestureDetector.SimpleOnGestureListener()
      {
        public boolean onSingleTapUp(MotionEvent paramAnonymousMotionEvent)
        {
          return false;
        }
      });
      return;
      i = 0;
      break;
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    paramInt1 = 0;
    paramInt3 = 0;
    if (this.mDockSide == 2) {
      paramInt2 = this.mBackground.getTop();
    }
    for (;;)
    {
      this.mMinimizedShadow.layout(paramInt1, paramInt2, this.mMinimizedShadow.getMeasuredWidth() + paramInt1, this.mMinimizedShadow.getMeasuredHeight() + paramInt2);
      if (paramBoolean) {
        this.mWindowManagerProxy.setTouchRegion(new Rect(this.mHandle.getLeft(), this.mHandle.getTop(), this.mHandle.getRight(), this.mHandle.getBottom()));
      }
      return;
      if (this.mDockSide == 1)
      {
        paramInt1 = this.mBackground.getLeft();
        paramInt2 = paramInt3;
      }
      else
      {
        paramInt2 = paramInt3;
        if (this.mDockSide == 3)
        {
          paramInt1 = this.mBackground.getRight() - this.mMinimizedShadow.getWidth();
          paramInt2 = paramInt3;
        }
      }
    }
  }
  
  public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
  {
    convertToScreenCoordinates(paramMotionEvent);
    this.mGestureDetector.onTouchEvent(paramMotionEvent);
    switch (paramMotionEvent.getAction() & 0xFF)
    {
    default: 
      return true;
    case 0: 
      this.mVelocityTracker = VelocityTracker.obtain();
      this.mVelocityTracker.addMovement(paramMotionEvent);
      this.mStartX = ((int)paramMotionEvent.getX());
      this.mStartY = ((int)paramMotionEvent.getY());
      boolean bool = startDragging(true, true);
      if (!bool) {
        stopDragging();
      }
      this.mStartPosition = getCurrentPosition();
      this.mMoving = false;
      return bool;
    case 2: 
      this.mVelocityTracker.addMovement(paramMotionEvent);
      j = (int)paramMotionEvent.getX();
      int k = (int)paramMotionEvent.getY();
      if ((isHorizontalDivision()) && (Math.abs(k - this.mStartY) > this.mTouchSlop)) {
        i = 1;
      }
      for (;;)
      {
        if ((!this.mMoving) && (i != 0))
        {
          this.mStartX = j;
          this.mStartY = k;
          this.mMoving = true;
        }
        if ((!this.mMoving) || (this.mDockSide == -1)) {
          break;
        }
        paramView = this.mSnapAlgorithm.calculateSnapTarget(this.mStartPosition, 0.0F, false);
        resizeStackDelayed(calculatePosition(j, k), this.mStartPosition, paramView);
        return true;
        if ((!isHorizontalDivision()) && (Math.abs(j - this.mStartX) > this.mTouchSlop)) {
          i = 1;
        } else {
          i = 0;
        }
      }
    }
    this.mVelocityTracker.addMovement(paramMotionEvent);
    int i = (int)paramMotionEvent.getRawX();
    int j = (int)paramMotionEvent.getRawY();
    this.mVelocityTracker.computeCurrentVelocity(1000);
    i = calculatePosition(i, j);
    if (isHorizontalDivision()) {}
    for (float f = this.mVelocityTracker.getYVelocity();; f = this.mVelocityTracker.getXVelocity())
    {
      stopDragging(i, f, false, true);
      this.mMoving = false;
      return true;
    }
  }
  
  public void resizeStack(int paramInt1, int paramInt2, DividerSnapAlgorithm.SnapTarget paramSnapTarget)
  {
    calculateBoundsForPosition(paramInt1, this.mDockSide, this.mDockedRect);
    label135:
    float f;
    WindowManagerProxy localWindowManagerProxy;
    if ((!this.mDockedRect.equals(this.mLastResizeRect)) || (this.mEntranceAnimationRunning))
    {
      if (this.mBackground.getZ() > 0.0F) {
        this.mBackground.invalidate();
      }
      this.mLastResizeRect.set(this.mDockedRect);
      if ((!this.mEntranceAnimationRunning) || (paramInt2 == Integer.MAX_VALUE)) {
        break label223;
      }
      if (this.mCurrentAnimator == null) {
        break label184;
      }
      calculateBoundsForPosition(paramInt2, this.mDockSide, this.mDockedTaskRect);
      calculateBoundsForPosition(paramInt2, DockedDividerUtils.invertDockSide(this.mDockSide), this.mOtherTaskRect);
      this.mWindowManagerProxy.resizeDockedStack(this.mDockedRect, this.mDockedTaskRect, null, this.mOtherTaskRect, null);
      paramSnapTarget = this.mSnapAlgorithm.getClosestDismissTarget(paramInt1);
      f = getDimFraction(paramInt1, paramSnapTarget);
      localWindowManagerProxy = this.mWindowManagerProxy;
      if (f == 0.0F) {
        break label595;
      }
    }
    label184:
    label223:
    label595:
    for (boolean bool = true;; bool = false)
    {
      localWindowManagerProxy.setResizeDimLayer(bool, getStackIdForDismissTarget(paramSnapTarget), f);
      return;
      return;
      if (isHorizontalDivision()) {}
      for (int i = this.mDisplayHeight;; i = this.mDisplayWidth)
      {
        calculateBoundsForPosition(i, this.mDockSide, this.mDockedTaskRect);
        break;
      }
      if ((this.mExitAnimationRunning) && (paramInt2 != Integer.MAX_VALUE))
      {
        calculateBoundsForPosition(paramInt2, this.mDockSide, this.mDockedTaskRect);
        calculateBoundsForPosition(this.mExitStartPosition, DockedDividerUtils.invertDockSide(this.mDockSide), this.mOtherTaskRect);
        this.mOtherInsetRect.set(this.mOtherTaskRect);
        applyExitAnimationParallax(this.mOtherTaskRect, paramInt1);
        this.mWindowManagerProxy.resizeDockedStack(this.mDockedRect, this.mDockedTaskRect, null, this.mOtherTaskRect, this.mOtherInsetRect);
        break label135;
      }
      if (paramInt2 != Integer.MAX_VALUE)
      {
        calculateBoundsForPosition(paramInt1, DockedDividerUtils.invertDockSide(this.mDockSide), this.mOtherRect);
        i = DockedDividerUtils.invertDockSide(this.mDockSide);
        int j = restrictDismissingTaskPosition(paramInt2, this.mDockSide, paramSnapTarget);
        paramInt2 = restrictDismissingTaskPosition(paramInt2, i, paramSnapTarget);
        calculateBoundsForPosition(j, this.mDockSide, this.mDockedTaskRect);
        calculateBoundsForPosition(paramInt2, i, this.mOtherTaskRect);
        this.mDisplayRect.set(0, 0, this.mDisplayWidth, this.mDisplayHeight);
        alignTopLeft(this.mDockedRect, this.mDockedTaskRect);
        alignTopLeft(this.mOtherRect, this.mOtherTaskRect);
        this.mDockedInsetRect.set(this.mDockedTaskRect);
        this.mOtherInsetRect.set(this.mOtherTaskRect);
        if (dockSideTopLeft(this.mDockSide))
        {
          alignTopLeft(this.mDisplayRect, this.mDockedInsetRect);
          alignBottomRight(this.mDisplayRect, this.mOtherInsetRect);
        }
        for (;;)
        {
          applyDismissingParallax(this.mDockedTaskRect, this.mDockSide, paramSnapTarget, paramInt1, j);
          applyDismissingParallax(this.mOtherTaskRect, i, paramSnapTarget, paramInt1, paramInt2);
          this.mWindowManagerProxy.resizeDockedStack(this.mDockedRect, this.mDockedTaskRect, this.mDockedInsetRect, this.mOtherTaskRect, this.mOtherInsetRect);
          break;
          alignBottomRight(this.mDisplayRect, this.mDockedInsetRect);
          alignTopLeft(this.mDisplayRect, this.mOtherInsetRect);
        }
      }
      this.mWindowManagerProxy.resizeDockedStack(this.mDockedRect, null, null, null, null);
      break label135;
    }
  }
  
  public void resizeStackDelayed(int paramInt1, int paramInt2, DividerSnapAlgorithm.SnapTarget paramSnapTarget)
  {
    if (this.mSurfaceFlingerOffsetMs != 0L)
    {
      paramSnapTarget = this.mHandler.obtainMessage(0, paramInt1, paramInt2, paramSnapTarget);
      paramSnapTarget.setAsynchronous(true);
      this.mHandler.sendMessageDelayed(paramSnapTarget, this.mSurfaceFlingerOffsetMs);
      return;
    }
    resizeStack(paramInt1, paramInt2, paramSnapTarget);
  }
  
  public void setAdjustedForIme(boolean paramBoolean)
  {
    updateDockSide();
    DividerHandleView localDividerHandleView = this.mHandle;
    float f;
    if (paramBoolean)
    {
      f = 0.0F;
      localDividerHandleView.setAlpha(f);
      if (paramBoolean) {
        break label39;
      }
      resetBackground();
    }
    for (;;)
    {
      this.mAdjustedForIme = paramBoolean;
      return;
      f = 1.0F;
      break;
      label39:
      if (this.mDockSide == 2)
      {
        this.mBackground.setPivotY(0.0F);
        this.mBackground.setScaleY(0.5F);
      }
    }
  }
  
  public void setAdjustedForIme(boolean paramBoolean, long paramLong)
  {
    float f2 = 1.0F;
    updateDockSide();
    ViewPropertyAnimator localViewPropertyAnimator = this.mHandle.animate().setInterpolator(IME_ADJUST_INTERPOLATOR).setDuration(paramLong);
    if (paramBoolean) {}
    for (float f1 = 0.0F;; f1 = 1.0F)
    {
      localViewPropertyAnimator.alpha(f1).start();
      if (this.mDockSide == 2)
      {
        this.mBackground.setPivotY(0.0F);
        localViewPropertyAnimator = this.mBackground.animate();
        f1 = f2;
        if (paramBoolean) {
          f1 = 0.5F;
        }
        localViewPropertyAnimator.scaleY(f1);
      }
      if (!paramBoolean) {
        this.mBackground.animate().withEndAction(this.mResetBackgroundRunnable);
      }
      this.mBackground.animate().setInterpolator(IME_ADJUST_INTERPOLATOR).setDuration(paramLong).start();
      this.mAdjustedForIme = paramBoolean;
      return;
    }
  }
  
  public void setMinimizedDockStack(boolean paramBoolean)
  {
    float f2 = 1.0F;
    updateDockSide();
    Object localObject = this.mHandle;
    if (paramBoolean)
    {
      f1 = 0.0F;
      ((DividerHandleView)localObject).setAlpha(f1);
      if (paramBoolean) {
        break label61;
      }
      resetBackground();
      label32:
      localObject = this.mMinimizedShadow;
      if (!paramBoolean) {
        break label152;
      }
    }
    label61:
    label152:
    for (float f1 = f2;; f1 = 0.0F)
    {
      ((MinimizedDockShadow)localObject).setAlpha(f1);
      this.mDockedStackMinimized = paramBoolean;
      return;
      f1 = 1.0F;
      break;
      if (this.mDockSide == 2)
      {
        this.mBackground.setPivotY(0.0F);
        this.mBackground.setScaleY(0.0F);
        break label32;
      }
      if ((this.mDockSide != 1) && (this.mDockSide != 3)) {
        break label32;
      }
      localObject = this.mBackground;
      if (this.mDockSide == 1) {}
      for (int i = 0;; i = this.mBackground.getWidth())
      {
        ((View)localObject).setPivotX(i);
        this.mBackground.setScaleX(0.0F);
        break;
      }
    }
  }
  
  public void setMinimizedDockStack(boolean paramBoolean, long paramLong)
  {
    float f2 = 1.0F;
    updateDockSide();
    Object localObject = this.mHandle.animate().setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setDuration(paramLong);
    if (paramBoolean)
    {
      f1 = 0.0F;
      ((ViewPropertyAnimator)localObject).alpha(f1).start();
      if (this.mDockSide != 2) {
        break label177;
      }
      this.mBackground.setPivotY(0.0F);
      localObject = this.mBackground.animate();
      if (!paramBoolean) {
        break label171;
      }
      f1 = 0.0F;
      label75:
      ((ViewPropertyAnimator)localObject).scaleY(f1);
      label83:
      if (!paramBoolean) {
        this.mBackground.animate().withEndAction(this.mResetBackgroundRunnable);
      }
      localObject = this.mMinimizedShadow.animate();
      if (!paramBoolean) {
        break label263;
      }
    }
    label171:
    label177:
    label210:
    label257:
    label263:
    for (float f1 = f2;; f1 = 0.0F)
    {
      ((ViewPropertyAnimator)localObject).alpha(f1).setInterpolator(Interpolators.ALPHA_IN).setDuration(paramLong).start();
      this.mBackground.animate().setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setDuration(paramLong).start();
      this.mDockedStackMinimized = paramBoolean;
      return;
      f1 = 1.0F;
      break;
      f1 = 1.0F;
      break label75;
      if ((this.mDockSide != 1) && (this.mDockSide != 3)) {
        break label83;
      }
      localObject = this.mBackground;
      int i;
      if (this.mDockSide == 1)
      {
        i = 0;
        ((View)localObject).setPivotX(i);
        localObject = this.mBackground.animate();
        if (!paramBoolean) {
          break label257;
        }
      }
      for (f1 = 0.0F;; f1 = 1.0F)
      {
        ((ViewPropertyAnimator)localObject).scaleX(f1);
        break;
        i = this.mBackground.getWidth();
        break label210;
      }
    }
  }
  
  public boolean startDragging(boolean paramBoolean1, boolean paramBoolean2)
  {
    cancelFlingAnimation();
    if (paramBoolean2) {
      this.mHandle.setTouching(true, paramBoolean1);
    }
    this.mDockSide = this.mWindowManagerProxy.getDockSide();
    initializeSnapAlgorithm();
    this.mWindowManagerProxy.setResizing(true);
    if (paramBoolean2)
    {
      this.mWindowManager.setSlippery(false);
      liftBackground();
    }
    EventBus.getDefault().send(new StartedDragingEvent());
    return this.mDockSide != -1;
  }
  
  public void stopDragging(int paramInt, float paramFloat, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mHandle.setTouching(false, true);
    fling(paramInt, paramFloat, paramBoolean1, paramBoolean2);
    this.mWindowManager.setSlippery(true);
    releaseBackground();
  }
  
  public void stopDragging(int paramInt, DividerSnapAlgorithm.SnapTarget paramSnapTarget, long paramLong1, long paramLong2, long paramLong3, Interpolator paramInterpolator)
  {
    this.mHandle.setTouching(false, true);
    flingTo(paramInt, paramSnapTarget, paramLong1, paramLong2, paramLong3, paramInterpolator);
    this.mWindowManager.setSlippery(true);
    releaseBackground();
  }
  
  public void stopDragging(int paramInt, DividerSnapAlgorithm.SnapTarget paramSnapTarget, long paramLong, Interpolator paramInterpolator)
  {
    stopDragging(paramInt, paramSnapTarget, paramLong, 0L, 0L, paramInterpolator);
  }
  
  public void stopDragging(int paramInt, DividerSnapAlgorithm.SnapTarget paramSnapTarget, long paramLong1, Interpolator paramInterpolator, long paramLong2)
  {
    stopDragging(paramInt, paramSnapTarget, paramLong1, 0L, paramLong2, paramInterpolator);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\stackdivider\DividerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */