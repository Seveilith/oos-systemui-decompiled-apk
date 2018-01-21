package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.BoostFramework;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import com.android.systemui.EventLogTags;
import com.android.systemui.Interpolators;
import com.android.systemui.classifier.FalsingManager;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.plugin.LSState;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.util.MdmLogger;
import com.android.systemui.util.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public abstract class PanelView
  extends FrameLayout
  implements GestureDetector.OnDoubleTapListener
{
  public static final String TAG = PanelView.class.getSimpleName();
  private boolean mAnimateAfterExpanding;
  private boolean mAnimatingOnDown;
  PanelBar mBar;
  private int[] mBoostParamVal;
  private Interpolator mBounceInterpolator;
  private boolean mClosing;
  private boolean mCollapseAfterPeek;
  private boolean mCollapsedAndHeadsUpOnDown;
  private float mExpandedFraction = 0.0F;
  protected float mExpandedHeight = 0.0F;
  protected boolean mExpanding;
  private FalsingManager mFalsingManager;
  private FlingAnimationUtils mFlingAnimationUtils;
  private final Runnable mFlingCollapseRunnable = new Runnable()
  {
    public void run()
    {
      PanelView.this.fling(0.0F, false, PanelView.-get4(PanelView.this), false);
    }
  };
  private GestureDetector mGestureDetector;
  private boolean mGestureWaitForTouchSlop;
  private boolean mHasLayoutedSinceDown;
  protected HeadsUpManager mHeadsUpManager;
  private ValueAnimator mHeightAnimator;
  private boolean mHightHigntIntercepting;
  protected boolean mHintAnimationRunning;
  private float mHintDistance;
  private boolean mIgnoreXTouchSlop;
  private float mInitialOffsetOnTouch;
  private float mInitialTouchX;
  private float mInitialTouchY;
  private boolean mInstantExpanding;
  private boolean mJustPeeked;
  protected KeyguardBottomAreaView mKeyguardBottomArea;
  private boolean mMotionAborted;
  private float mNextCollapseSpeedUpFactor = 1.0F;
  private boolean mOverExpandedBeforeFling;
  private boolean mPanelClosedOnDown;
  private ObjectAnimator mPeekAnimator;
  private float mPeekHeight;
  private boolean mPeekPending;
  private Runnable mPeekRunnable = new Runnable()
  {
    public void run()
    {
      PanelView.-set4(PanelView.this, false);
      PanelView.-wrap1(PanelView.this);
    }
  };
  private boolean mPeekTouching;
  private BoostFramework mPerf = null;
  protected final Runnable mPostCollapseRunnable = new Runnable()
  {
    public void run()
    {
      PanelView.this.collapse(false, 1.0F);
    }
  };
  protected PhoneStatusBar mStatusBar;
  private boolean mTouchAboveFalsingThreshold;
  private boolean mTouchDisabled;
  protected int mTouchSlop;
  private boolean mTouchSlopExceeded;
  private boolean mTouchStartedInEmptyArea;
  protected boolean mTracking;
  private int mTrackingPointer;
  private int mUnlockFalsingThreshold;
  private boolean mUpdateFlingOnLayout;
  private float mUpdateFlingVelocity;
  private boolean mUpwardsWhenTresholdReached;
  private VelocityTrackerInterface mVelocityTracker;
  private String mViewName;
  
  public PanelView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mFlingAnimationUtils = new FlingAnimationUtils(paramContext, 0.6F);
    this.mBounceInterpolator = new BounceInterpolator();
    this.mFalsingManager = FalsingManager.getInstance(paramContext);
    if (paramContext.getResources().getBoolean(17957055))
    {
      this.mBoostParamVal = paramContext.getResources().getIntArray(17236056);
      this.mPerf = new BoostFramework();
    }
    this.mGestureDetector = new GestureDetector(paramContext, new GestureDetector.SimpleOnGestureListener());
    this.mGestureDetector.setOnDoubleTapListener(this);
  }
  
  private void abortAnimations()
  {
    cancelPeek();
    cancelHeightAnimator();
    removeCallbacks(this.mPostCollapseRunnable);
    removeCallbacks(this.mFlingCollapseRunnable);
  }
  
  private ValueAnimator createHeightAnimator(float paramFloat)
  {
    ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { this.mExpandedHeight, paramFloat });
    localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        PanelView.this.setExpandedHeightInternal(((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
      }
    });
    return localValueAnimator;
  }
  
  private void endClosing()
  {
    if (this.mClosing)
    {
      this.mClosing = false;
      onClosingFinished();
    }
  }
  
  private void endMotionEvent(MotionEvent paramMotionEvent, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    this.mTrackingPointer = -1;
    float f1;
    if (((this.mTracking) && (this.mTouchSlopExceeded)) || (Math.abs(paramFloat1 - this.mInitialTouchX) > this.mTouchSlop))
    {
      f1 = 0.0F;
      float f2 = 0.0F;
      if (this.mVelocityTracker != null)
      {
        this.mVelocityTracker.computeCurrentVelocity(1000);
        f1 = this.mVelocityTracker.getYVelocity();
        f2 = (float)Math.hypot(this.mVelocityTracker.getXVelocity(), this.mVelocityTracker.getYVelocity());
      }
      if ((!flingExpands(f1, f2, paramFloat1, paramFloat2)) && (paramMotionEvent.getActionMasked() != 3)) {
        break label405;
      }
      label120:
      if (LSState.getInstance().getFingerprintUnlockControl().getMode() == 5) {
        break label416;
      }
      paramBoolean = true;
      label136:
      DozeLog.traceFling(paramBoolean, this.mTouchAboveFalsingThreshold, this.mStatusBar.isFalsingThresholdNeeded(), this.mStatusBar.isWakeUpComingFromTouch());
      if ((!paramBoolean) && (this.mStatusBar.getBarState() == 1))
      {
        f2 = this.mStatusBar.getDisplayDensity();
        EventLogTags.writeSysuiLockscreenGesture(1, (int)Math.abs((paramFloat2 - this.mInitialTouchY) / f2), (int)Math.abs(f1 / f2));
        if ((this.mStatusBar.mKeyguardMonitor != null) && (!this.mStatusBar.mKeyguardMonitor.isSecure())) {
          break label422;
        }
      }
      label233:
      fling(f1, paramBoolean, isFalseTouch(paramFloat1, paramFloat2));
      if (Utils.DEBUG_ONEPLUS) {
        Log.i(TAG, " isFalseTouch:" + isFalseTouch(paramFloat1, paramFloat2));
      }
      onTrackingStopped(paramBoolean);
      if ((paramBoolean) && (this.mPanelClosedOnDown) && (!this.mHasLayoutedSinceDown)) {
        break label437;
      }
    }
    label405:
    label416:
    label422:
    label437:
    for (paramBoolean = false;; paramBoolean = true)
    {
      this.mUpdateFlingOnLayout = paramBoolean;
      if (this.mUpdateFlingOnLayout) {
        this.mUpdateFlingVelocity = f1;
      }
      for (;;)
      {
        if (this.mVelocityTracker != null)
        {
          this.mVelocityTracker.recycle();
          this.mVelocityTracker = null;
        }
        this.mPeekTouching = false;
        return;
        if ((Math.abs(paramFloat2 - this.mInitialTouchY) > this.mTouchSlop) || (paramMotionEvent.getActionMasked() == 3) || (paramBoolean)) {
          break;
        }
        onTrackingStopped(onEmptySpaceClick(this.mInitialTouchX));
      }
      if (paramBoolean) {
        break label120;
      }
      paramBoolean = false;
      break label136;
      paramBoolean = false;
      break label136;
      MdmLogger.log("lock_unlock_success", "swipe", "1");
      break label233;
    }
  }
  
  private int getFalsingThreshold()
  {
    if (this.mStatusBar.isWakeUpComingFromTouch()) {}
    for (float f = 1.5F;; f = 1.0F) {
      return (int)(this.mUnlockFalsingThreshold * f);
    }
  }
  
  private void initVelocityTracker()
  {
    if (this.mVelocityTracker != null) {
      this.mVelocityTracker.recycle();
    }
    this.mVelocityTracker = VelocityTrackerFactory.obtain(getContext());
  }
  
  private boolean isDirectionUpwards(float paramFloat1, float paramFloat2)
  {
    boolean bool = false;
    float f = this.mInitialTouchX;
    paramFloat2 -= this.mInitialTouchY;
    if (paramFloat2 >= 0.0F) {
      return false;
    }
    if (Math.abs(paramFloat2) >= Math.abs(paramFloat1 - f)) {
      bool = true;
    }
    return bool;
  }
  
  private boolean isFalseTouch(float paramFloat1, float paramFloat2)
  {
    if (!this.mStatusBar.isFalsingThresholdNeeded()) {
      return false;
    }
    if (this.mFalsingManager.isClassiferEnabled())
    {
      if ((Utils.DEBUG_ONEPLUS) && (this.mFalsingManager.isFalseTouch())) {
        Log.i(TAG, "FalsingManager check fail ");
      }
      return this.mFalsingManager.isFalseTouch();
    }
    if (!this.mTouchAboveFalsingThreshold)
    {
      if (Utils.DEBUG_ONEPLUS) {
        Log.i(TAG, "TouchAboveFalsingThreshold  check fail");
      }
      return true;
    }
    if (this.mUpwardsWhenTresholdReached) {
      return false;
    }
    if ((Utils.DEBUG_ONEPLUS) && (!isDirectionUpwards(paramFloat1, paramFloat2))) {
      Log.i(TAG, "isDirectionUpwards  check fail");
    }
    return !isDirectionUpwards(paramFloat1, paramFloat2);
  }
  
  private void notifyExpandingStarted()
  {
    if (!this.mExpanding)
    {
      this.mExpanding = true;
      onExpandingStarted();
    }
  }
  
  private void runPeekAnimation()
  {
    this.mPeekHeight = getPeekHeight();
    if (this.mHeightAnimator != null) {
      return;
    }
    this.mPeekAnimator = ObjectAnimator.ofFloat(this, "expandedHeight", new float[] { this.mPeekHeight }).setDuration(250L);
    this.mPeekAnimator.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
    this.mPeekAnimator.addListener(new AnimatorListenerAdapter()
    {
      private boolean mCancelled;
      
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        this.mCancelled = true;
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        PanelView.-set3(PanelView.this, null);
        if ((!PanelView.-get2(PanelView.this)) || (this.mCancelled)) {}
        for (;;)
        {
          PanelView.-set0(PanelView.this, false);
          return;
          PanelView.this.postOnAnimation(PanelView.this.mPostCollapseRunnable);
        }
      }
    });
    notifyExpandingStarted();
    this.mPeekAnimator.start();
    this.mJustPeeked = true;
  }
  
  private void schedulePeek()
  {
    this.mPeekPending = true;
    long l = ViewConfiguration.getTapTimeout();
    postOnAnimationDelayed(this.mPeekRunnable, l);
    notifyBarPanelExpansionChanged();
  }
  
  private boolean shouldHightHintIntercept(float paramFloat1, float paramFloat2)
  {
    boolean bool = false;
    View localView = this.mStatusBar.getStatusBarView();
    if ((paramFloat1 >= localView.getX()) && (paramFloat1 <= localView.getRight()) && (paramFloat2 >= localView.getTop()))
    {
      if (paramFloat2 <= localView.getBottom()) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  private void startUnlockHintAnimationPhase1(final Runnable paramRunnable)
  {
    ValueAnimator localValueAnimator = createHeightAnimator(Math.max(0.0F, getMaxPanelHeight() - this.mHintDistance));
    localValueAnimator.setDuration(250L);
    localValueAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
    localValueAnimator.addListener(new AnimatorListenerAdapter()
    {
      private boolean mCancelled;
      
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        this.mCancelled = true;
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if (this.mCancelled)
        {
          PanelView.-set1(PanelView.this, null);
          paramRunnable.run();
          return;
        }
        PanelView.-wrap2(PanelView.this, paramRunnable);
      }
    });
    localValueAnimator.start();
    this.mHeightAnimator = localValueAnimator;
    this.mKeyguardBottomArea.getIndicationViewHost().animate().translationY(-this.mHintDistance).setDuration(250L).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).withEndAction(new Runnable()
    {
      public void run()
      {
        PanelView.this.mKeyguardBottomArea.getIndicationViewHost().animate().translationY(0.0F).setDuration(450L).setInterpolator(PanelView.-get1(PanelView.this)).start();
      }
    }).start();
    this.mKeyguardBottomArea.getDashView().animate().translationY(-this.mHintDistance).setDuration(250L).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).withEndAction(new Runnable()
    {
      public void run()
      {
        PanelView.this.mKeyguardBottomArea.getDashView().animate().translationY(0.0F).setDuration(450L).setInterpolator(PanelView.-get1(PanelView.this)).start();
      }
    }).start();
  }
  
  private void startUnlockHintAnimationPhase2(final Runnable paramRunnable)
  {
    ValueAnimator localValueAnimator = createHeightAnimator(getMaxPanelHeight());
    localValueAnimator.setDuration(450L);
    localValueAnimator.setInterpolator(this.mBounceInterpolator);
    localValueAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        PanelView.-set1(PanelView.this, null);
        paramRunnable.run();
        PanelView.this.notifyBarPanelExpansionChanged();
      }
    });
    localValueAnimator.start();
    this.mHeightAnimator = localValueAnimator;
  }
  
  private void trackMovement(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getRawX() - paramMotionEvent.getX();
    float f2 = paramMotionEvent.getRawY() - paramMotionEvent.getY();
    paramMotionEvent.offsetLocation(f1, f2);
    if (this.mVelocityTracker != null) {
      this.mVelocityTracker.addMovement(paramMotionEvent);
    }
    paramMotionEvent.offsetLocation(-f1, -f2);
  }
  
  protected void cancelHeightAnimator()
  {
    if (this.mHeightAnimator != null) {
      this.mHeightAnimator.cancel();
    }
    endClosing();
  }
  
  public void cancelPeek()
  {
    boolean bool = this.mPeekPending;
    if (this.mPeekAnimator != null)
    {
      bool = true;
      this.mPeekAnimator.cancel();
    }
    removeCallbacks(this.mPeekRunnable);
    this.mPeekPending = false;
    if (bool) {
      notifyBarPanelExpansionChanged();
    }
  }
  
  public void collapse(boolean paramBoolean, float paramFloat)
  {
    if ((this.mPeekPending) || (this.mPeekAnimator != null))
    {
      this.mCollapseAfterPeek = true;
      if (this.mPeekPending)
      {
        removeCallbacks(this.mPeekRunnable);
        this.mPeekRunnable.run();
      }
    }
    while ((isFullyCollapsed()) || (this.mTracking) || (this.mClosing)) {
      return;
    }
    cancelHeightAnimator();
    notifyExpandingStarted();
    this.mClosing = true;
    if (paramBoolean)
    {
      this.mNextCollapseSpeedUpFactor = paramFloat;
      postDelayed(this.mFlingCollapseRunnable, 120L);
      return;
    }
    fling(0.0F, false, paramFloat, false);
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    String str5 = getClass().getSimpleName();
    float f = getExpandedHeight();
    int i = getMaxPanelHeight();
    label43:
    String str1;
    label55:
    ObjectAnimator localObjectAnimator;
    String str2;
    label83:
    ValueAnimator localValueAnimator;
    String str3;
    if (this.mClosing)
    {
      paramFileDescriptor = "T";
      if (!this.mTracking) {
        break label210;
      }
      paramArrayOfString = "T";
      if (!this.mJustPeeked) {
        break label217;
      }
      str1 = "T";
      localObjectAnimator = this.mPeekAnimator;
      if ((this.mPeekAnimator == null) || (!this.mPeekAnimator.isStarted())) {
        break label225;
      }
      str2 = " (started)";
      localValueAnimator = this.mHeightAnimator;
      if ((this.mHeightAnimator == null) || (!this.mHeightAnimator.isStarted())) {
        break label233;
      }
      str3 = " (started)";
      label111:
      if (!this.mTouchDisabled) {
        break label241;
      }
    }
    label210:
    label217:
    label225:
    label233:
    label241:
    for (String str4 = "T";; str4 = "f")
    {
      paramPrintWriter.println(String.format("[PanelView(%s): expandedHeight=%f maxPanelHeight=%d closing=%s tracking=%s justPeeked=%s peekAnim=%s%s timeAnim=%s%s touchDisabled=%s]", new Object[] { str5, Float.valueOf(f), Integer.valueOf(i), paramFileDescriptor, paramArrayOfString, str1, localObjectAnimator, str2, localValueAnimator, str3, str4 }));
      return;
      paramFileDescriptor = "f";
      break;
      paramArrayOfString = "f";
      break label43;
      str1 = "f";
      break label55;
      str2 = "";
      break label83;
      str3 = "";
      break label111;
    }
  }
  
  public void expand(boolean paramBoolean, final int paramInt)
  {
    if ((isFullyCollapsed()) || (isCollapsing()))
    {
      this.mInstantExpanding = true;
      this.mAnimateAfterExpanding = paramBoolean;
      this.mUpdateFlingOnLayout = false;
      abortAnimations();
      cancelPeek();
      if (this.mTracking) {
        onTrackingStopped(true);
      }
      if (this.mExpanding) {
        notifyExpandingFinished();
      }
      notifyBarPanelExpansionChanged();
      getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
      {
        public void onGlobalLayout()
        {
          if (!PanelView.-get3(PanelView.this))
          {
            PanelView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            return;
          }
          if (PanelView.this.mStatusBar.getStatusBarWindow().getHeight() != PanelView.this.mStatusBar.getStatusBarHeight())
          {
            PanelView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            if (!PanelView.-get0(PanelView.this)) {
              break label99;
            }
            PanelView.-wrap0(PanelView.this);
            PanelView.this.fling(paramInt, true);
          }
          for (;;)
          {
            PanelView.-set2(PanelView.this, false);
            return;
            label99:
            PanelView.this.setExpandedFraction(1.0F);
          }
        }
      });
      requestLayout();
      return;
    }
  }
  
  protected void fling(float paramFloat, boolean paramBoolean)
  {
    fling(paramFloat, paramBoolean, 1.0F, false);
  }
  
  protected void fling(float paramFloat1, boolean paramBoolean1, float paramFloat2, boolean paramBoolean2)
  {
    cancelPeek();
    if (paramBoolean1) {}
    for (float f = getMaxPanelHeight();; f = 0.0F)
    {
      if (!paramBoolean1) {
        this.mClosing = true;
      }
      flingToHeight(paramFloat1, paramBoolean1, f, paramFloat2, paramBoolean2);
      return;
    }
  }
  
  protected void fling(float paramFloat, boolean paramBoolean1, boolean paramBoolean2)
  {
    fling(paramFloat, paramBoolean1, 1.0F, paramBoolean2);
  }
  
  protected boolean flingExpands(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if (isFalseTouch(paramFloat3, paramFloat4)) {
      return true;
    }
    if (Math.abs(paramFloat2) < this.mFlingAnimationUtils.getMinVelocityPxPerSecond()) {
      return getExpandedFraction() > 0.5F;
    }
    return paramFloat1 > 0.0F;
  }
  
  protected void flingToHeight(float paramFloat1, final boolean paramBoolean1, float paramFloat2, float paramFloat3, boolean paramBoolean2)
  {
    boolean bool2 = true;
    final boolean bool1;
    if ((paramBoolean1) && (fullyExpandedClearAllVisible()) && (this.mExpandedHeight < getMaxPanelHeight() - getClearAllHeight())) {
      if (isClearAllVisible()) {
        bool1 = false;
      }
    }
    for (;;)
    {
      if (bool1) {
        paramFloat2 = getMaxPanelHeight() - getClearAllHeight();
      }
      if ((paramFloat2 != this.mExpandedHeight) && ((getOverExpansionAmount() <= 0.0F) || (!paramBoolean1))) {
        break;
      }
      notifyExpandingFinished();
      return;
      bool1 = true;
      continue;
      bool1 = false;
    }
    ValueAnimator localValueAnimator;
    if (getOverExpansionAmount() > 0.0F)
    {
      this.mOverExpandedBeforeFling = bool2;
      localValueAnimator = createHeightAnimator(paramFloat2);
      if (!paramBoolean1) {
        break label219;
      }
      if (paramBoolean2) {
        paramFloat1 = 0.0F;
      }
      this.mFlingAnimationUtils.apply(localValueAnimator, this.mExpandedHeight, paramFloat2, paramFloat1, getHeight());
      if (paramFloat1 == 0.0F) {
        localValueAnimator.setDuration(350L);
      }
    }
    for (;;)
    {
      if (this.mPerf != null) {
        this.mPerf.perfLockAcquire(0, this.mBoostParamVal);
      }
      localValueAnimator.addListener(new AnimatorListenerAdapter()
      {
        private boolean mCancelled;
        
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          if (PanelView.-get5(PanelView.this) != null) {
            PanelView.-get5(PanelView.this).perfLockRelease();
          }
          this.mCancelled = true;
          if (!paramBoolean1)
          {
            Log.d(PanelView.TAG, "Cancel collapse animation, restore focus");
            PanelView.this.mStatusBar.onCancelCollapsePanels();
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (PanelView.-get5(PanelView.this) != null) {
            PanelView.-get5(PanelView.this).perfLockRelease();
          }
          if ((!bool1) || (this.mCancelled)) {}
          for (;;)
          {
            PanelView.-set1(PanelView.this, null);
            if (!this.mCancelled) {
              PanelView.this.notifyExpandingFinished();
            }
            PanelView.this.notifyBarPanelExpansionChanged();
            return;
            PanelView.this.setExpandedHeightInternal(PanelView.this.getMaxPanelHeight());
          }
        }
      });
      this.mHeightAnimator = localValueAnimator;
      localValueAnimator.start();
      return;
      bool2 = false;
      break;
      label219:
      this.mFlingAnimationUtils.applyDismissing(localValueAnimator, this.mExpandedHeight, paramFloat2, paramFloat1, getHeight());
      if (paramFloat1 == 0.0F) {
        localValueAnimator.setDuration(((float)localValueAnimator.getDuration() * getCannedFlingDurationFactor() / paramFloat3));
      }
    }
  }
  
  protected abstract boolean fullyExpandedClearAllVisible();
  
  protected abstract float getCannedFlingDurationFactor();
  
  protected abstract int getClearAllHeight();
  
  public float getExpandedFraction()
  {
    return this.mExpandedFraction;
  }
  
  public float getExpandedHeight()
  {
    return this.mExpandedHeight;
  }
  
  protected abstract int getMaxPanelHeight();
  
  protected abstract float getOverExpansionAmount();
  
  protected abstract float getOverExpansionPixels();
  
  protected abstract float getPeekHeight();
  
  protected abstract int getRealMaxPanelHeight();
  
  protected abstract boolean hasConflictingGestures();
  
  public void instantCollapse()
  {
    Log.d(TAG, "instantCollapse: mExpanding = " + this.mExpanding + ", mInstantExpanding = " + this.mInstantExpanding);
    abortAnimations();
    setExpandedFraction(0.0F);
    if (this.mExpanding) {
      notifyExpandingFinished();
    }
    if (this.mInstantExpanding)
    {
      this.mInstantExpanding = false;
      notifyBarPanelExpansionChanged();
    }
  }
  
  protected abstract boolean isClearAllVisible();
  
  public boolean isCollapsing()
  {
    return this.mClosing;
  }
  
  public boolean isFullyCollapsed()
  {
    return this.mExpandedHeight <= 0.0F;
  }
  
  public boolean isFullyExpanded()
  {
    return this.mExpandedHeight >= getMaxPanelHeight();
  }
  
  protected abstract boolean isInContentBounds(float paramFloat1, float paramFloat2);
  
  protected abstract boolean isPanelVisibleBecauseOfHeadsUp();
  
  protected boolean isScrolledToBottom()
  {
    return true;
  }
  
  public boolean isTracking()
  {
    return this.mTracking;
  }
  
  protected abstract boolean isTrackingBlocked();
  
  protected void loadDimens()
  {
    Resources localResources = getContext().getResources();
    this.mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    this.mHintDistance = localResources.getDimension(2131755480);
    this.mUnlockFalsingThreshold = localResources.getDimensionPixelSize(2131755466);
  }
  
  protected void notifyBarPanelExpansionChanged()
  {
    boolean bool2 = true;
    PanelBar localPanelBar = this.mBar;
    float f = this.mExpandedFraction;
    boolean bool1 = bool2;
    if (this.mExpandedFraction <= 0.0F)
    {
      bool1 = bool2;
      if (!this.mPeekPending)
      {
        if (this.mPeekAnimator == null) {
          break label50;
        }
        bool1 = bool2;
      }
    }
    for (;;)
    {
      localPanelBar.panelExpansionChanged(f, bool1);
      return;
      label50:
      bool1 = bool2;
      if (!this.mInstantExpanding)
      {
        bool1 = bool2;
        if (!isPanelVisibleBecauseOfHeadsUp())
        {
          bool1 = bool2;
          if (!this.mTracking)
          {
            bool1 = bool2;
            if (this.mHeightAnimator == null) {
              bool1 = false;
            }
          }
        }
      }
    }
  }
  
  protected final void notifyExpandingFinished()
  {
    endClosing();
    if (this.mExpanding)
    {
      this.mExpanding = false;
      onExpandingFinished();
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mViewName = getResources().getResourceName(getId());
  }
  
  protected void onClosingFinished()
  {
    this.mBar.onClosingFinished();
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    loadDimens();
  }
  
  protected boolean onEmptySpaceClick(float paramFloat)
  {
    if (this.mHintAnimationRunning) {
      return true;
    }
    return onMiddleClicked();
  }
  
  protected void onExpandingFinished()
  {
    this.mBar.onExpandingFinished();
  }
  
  protected void onExpandingStarted() {}
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    loadDimens();
  }
  
  protected abstract void onHeightUpdated(float paramFloat);
  
  public boolean onHightlightHintIntercept(MotionEvent paramMotionEvent)
  {
    int j = paramMotionEvent.findPointerIndex(this.mTrackingPointer);
    int i = j;
    if (j < 0)
    {
      i = 0;
      this.mTrackingPointer = paramMotionEvent.getPointerId(0);
    }
    float f1 = paramMotionEvent.getX(i);
    float f2 = paramMotionEvent.getY(i);
    switch (paramMotionEvent.getActionMasked())
    {
    default: 
    case 0: 
    case 2: 
      do
      {
        return false;
        if (shouldHightHintIntercept(f1, f2))
        {
          this.mHightHigntIntercepting = true;
          return false;
        }
        this.mHightHigntIntercepting = false;
        return false;
      } while (shouldHightHintIntercept(f1, f2));
      this.mHightHigntIntercepting = false;
      return false;
    case 1: 
      if ((this.mHightHigntIntercepting) && (shouldHightHintIntercept(f1, f2))) {
        this.mStatusBar.launchHighlightHintAp();
      }
      break;
    }
    this.mHightHigntIntercepting = false;
    return false;
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool2 = true;
    if ((this.mInstantExpanding) || ((this.mMotionAborted) && (paramMotionEvent.getActionMasked() != 0)))
    {
      Log.d(TAG, "onInterceptTouchEvent: mInstantExpanding = " + this.mInstantExpanding + ", mTouchDisabled = " + this.mTouchDisabled + ", mMotionAborted = " + this.mMotionAborted);
      return false;
    }
    int j = paramMotionEvent.findPointerIndex(this.mTrackingPointer);
    int i = j;
    if (j < 0)
    {
      i = 0;
      this.mTrackingPointer = paramMotionEvent.getPointerId(0);
    }
    float f1 = paramMotionEvent.getX(i);
    float f2 = paramMotionEvent.getY(i);
    boolean bool1 = isScrolledToBottom();
    switch (paramMotionEvent.getActionMasked())
    {
    }
    do
    {
      float f3;
      float f4;
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                return false;
                this.mStatusBar.userActivity();
                if (this.mHeightAnimator != null) {}
                for (bool1 = true;; bool1 = false)
                {
                  this.mAnimatingOnDown = bool1;
                  if (((!this.mAnimatingOnDown) || (!this.mClosing) || (this.mHintAnimationRunning)) && (!this.mPeekPending) && (this.mPeekAnimator == null)) {
                    break;
                  }
                  cancelHeightAnimator();
                  cancelPeek();
                  this.mTouchSlopExceeded = true;
                  return true;
                }
                this.mInitialTouchY = f2;
                this.mInitialTouchX = f1;
                bool1 = bool2;
                if (isInContentBounds(f1, f2)) {
                  bool1 = false;
                }
                this.mTouchStartedInEmptyArea = bool1;
                this.mTouchSlopExceeded = false;
                this.mJustPeeked = false;
                this.mMotionAborted = false;
                this.mPanelClosedOnDown = isFullyCollapsed();
                this.mCollapsedAndHeadsUpOnDown = false;
                this.mHasLayoutedSinceDown = false;
                this.mUpdateFlingOnLayout = false;
                this.mTouchAboveFalsingThreshold = false;
                initVelocityTracker();
                trackMovement(paramMotionEvent);
                return false;
                i = paramMotionEvent.getPointerId(paramMotionEvent.getActionIndex());
              } while (this.mTrackingPointer != i);
              if (paramMotionEvent.getPointerId(0) != i) {}
              for (i = 0;; i = 1)
              {
                this.mTrackingPointer = paramMotionEvent.getPointerId(i);
                this.mInitialTouchX = paramMotionEvent.getX(i);
                this.mInitialTouchY = paramMotionEvent.getY(i);
                return false;
              }
            } while (this.mStatusBar.getBarState() != 1);
            this.mMotionAborted = true;
          } while (this.mVelocityTracker == null);
          this.mVelocityTracker.recycle();
          this.mVelocityTracker = null;
          return false;
          f3 = f2 - this.mInitialTouchY;
          trackMovement(paramMotionEvent);
        } while ((!bool1) && (!this.mTouchStartedInEmptyArea) && (!this.mAnimatingOnDown));
        f4 = Math.abs(f3);
      } while (((f3 >= -this.mTouchSlop) && ((!this.mAnimatingOnDown) || (f4 <= this.mTouchSlop))) || (f4 <= Math.abs(f1 - this.mInitialTouchX)));
      cancelHeightAnimator();
      startExpandMotion(f1, f2, true, this.mExpandedHeight);
      return true;
    } while (this.mVelocityTracker == null);
    this.mVelocityTracker.recycle();
    this.mVelocityTracker = null;
    return false;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.mStatusBar.onPanelLaidOut();
    requestPanelHeightUpdate();
    this.mHasLayoutedSinceDown = true;
    if (this.mUpdateFlingOnLayout)
    {
      abortAnimations();
      fling(this.mUpdateFlingVelocity, true);
      this.mUpdateFlingOnLayout = false;
    }
  }
  
  protected abstract boolean onMiddleClicked();
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool2 = true;
    boolean bool3 = false;
    if ((this.mInstantExpanding) || (this.mTouchDisabled) || ((this.mMotionAborted) && (paramMotionEvent.getActionMasked() != 0))) {
      return false;
    }
    if (this.mGestureDetector.onTouchEvent(paramMotionEvent)) {
      return true;
    }
    if ((isFullyCollapsed()) && (paramMotionEvent.isFromSource(8194)))
    {
      if (paramMotionEvent.getAction() == 1) {
        expand(true, 0);
      }
      return true;
    }
    int j = paramMotionEvent.findPointerIndex(this.mTrackingPointer);
    int i = j;
    if (j < 0)
    {
      i = 0;
      this.mTrackingPointer = paramMotionEvent.getPointerId(0);
    }
    float f3 = paramMotionEvent.getX(i);
    float f4 = paramMotionEvent.getY(i);
    boolean bool1;
    if (paramMotionEvent.getActionMasked() == 0)
    {
      if (!isFullyCollapsed())
      {
        bool1 = hasConflictingGestures();
        this.mGestureWaitForTouchSlop = bool1;
        if (isFullyCollapsed()) {
          break label250;
        }
        bool1 = shouldGestureIgnoreXTouchSlop(f3, f4);
        label172:
        this.mIgnoreXTouchSlop = bool1;
      }
    }
    else {
      switch (paramMotionEvent.getActionMasked())
      {
      }
    }
    for (;;)
    {
      bool1 = bool2;
      if (this.mGestureWaitForTouchSlop) {
        bool1 = this.mTracking;
      }
      return bool1;
      bool1 = true;
      break;
      label250:
      bool1 = true;
      break label172;
      startExpandMotion(f3, f4, false, this.mExpandedHeight);
      this.mJustPeeked = false;
      this.mPanelClosedOnDown = isFullyCollapsed();
      this.mHasLayoutedSinceDown = false;
      this.mUpdateFlingOnLayout = false;
      this.mMotionAborted = false;
      this.mPeekTouching = this.mPanelClosedOnDown;
      this.mTouchAboveFalsingThreshold = false;
      if (isFullyCollapsed())
      {
        bool1 = this.mHeadsUpManager.hasPinnedHeadsUp();
        label326:
        this.mCollapsedAndHeadsUpOnDown = bool1;
        if (this.mVelocityTracker == null) {
          initVelocityTracker();
        }
        trackMovement(paramMotionEvent);
        if ((!this.mGestureWaitForTouchSlop) || ((this.mHeightAnimator != null) && (!this.mHintAnimationRunning)) || (this.mPeekPending) || (this.mPeekAnimator != null))
        {
          cancelHeightAnimator();
          cancelPeek();
          if (((this.mHeightAnimator != null) && (!this.mHintAnimationRunning)) || (this.mPeekPending)) {
            break label476;
          }
          bool1 = bool3;
          if (this.mPeekAnimator == null) {}
        }
      }
      label476:
      for (bool1 = true;; bool1 = true)
      {
        this.mTouchSlopExceeded = bool1;
        onTrackingStarted();
        if ((!isFullyCollapsed()) || (this.mHeadsUpManager.hasPinnedHeadsUp()) || (this.mStatusBar.isHighlightHintViewShowing())) {
          break;
        }
        schedulePeek();
        break;
        bool1 = false;
        break label326;
      }
      i = paramMotionEvent.getPointerId(paramMotionEvent.getActionIndex());
      if (this.mTrackingPointer == i)
      {
        if (paramMotionEvent.getPointerId(0) != i) {}
        float f1;
        float f2;
        for (i = 0;; i = 1)
        {
          f1 = paramMotionEvent.getY(i);
          f2 = paramMotionEvent.getX(i);
          this.mTrackingPointer = paramMotionEvent.getPointerId(i);
          startExpandMotion(f2, f1, true, this.mExpandedHeight);
          break;
        }
        if (this.mStatusBar.getBarState() == 1)
        {
          this.mMotionAborted = true;
          endMotionEvent(paramMotionEvent, f3, f4, true);
          return false;
          f2 = f4 - this.mInitialTouchY;
          f1 = f2;
          if (Math.abs(f2) > this.mTouchSlop) {
            if (Math.abs(f2) <= Math.abs(f3 - this.mInitialTouchX))
            {
              f1 = f2;
              if (!this.mIgnoreXTouchSlop) {}
            }
            else
            {
              this.mTouchSlopExceeded = true;
              f1 = f2;
              if (this.mGestureWaitForTouchSlop)
              {
                if (!this.mTracking) {
                  break label762;
                }
                f1 = f2;
              }
            }
          }
          label659:
          f2 = Math.max(0.0F, this.mInitialOffsetOnTouch + f1);
          if (f2 > this.mPeekHeight)
          {
            if (this.mPeekAnimator != null) {
              this.mPeekAnimator.cancel();
            }
            this.mJustPeeked = false;
          }
          if (-f1 >= getFalsingThreshold())
          {
            this.mTouchAboveFalsingThreshold = true;
            this.mUpwardsWhenTresholdReached = isDirectionUpwards(f3, f4);
          }
          if ((this.mJustPeeked) || ((this.mGestureWaitForTouchSlop) && (!this.mTracking)) || (isTrackingBlocked())) {}
          for (;;)
          {
            trackMovement(paramMotionEvent);
            break;
            label762:
            f1 = f2;
            if (this.mCollapsedAndHeadsUpOnDown) {
              break label659;
            }
            f1 = f2;
            if (!this.mJustPeeked)
            {
              f1 = f2;
              if (this.mInitialOffsetOnTouch != 0.0F)
              {
                startExpandMotion(f3, f4, false, this.mExpandedHeight);
                f1 = 0.0F;
              }
            }
            cancelHeightAnimator();
            removeCallbacks(this.mPeekRunnable);
            this.mPeekPending = false;
            onTrackingStarted();
            break label659;
            setExpandedHeightInternal(f2);
          }
          trackMovement(paramMotionEvent);
          endMotionEvent(paramMotionEvent, f3, f4, false);
        }
      }
    }
  }
  
  protected void onTrackingStarted()
  {
    endClosing();
    this.mTracking = true;
    this.mCollapseAfterPeek = false;
    this.mBar.onTrackingStarted();
    notifyExpandingStarted();
    notifyBarPanelExpansionChanged();
  }
  
  protected void onTrackingStopped(boolean paramBoolean)
  {
    this.mTracking = false;
    this.mBar.onTrackingStopped(paramBoolean);
    notifyBarPanelExpansionChanged();
  }
  
  protected void requestPanelHeightUpdate()
  {
    float f = getMaxPanelHeight();
    if (((this.mTracking) && (!isTrackingBlocked())) || (this.mHeightAnimator != null) || (isFullyCollapsed())) {}
    while ((f == this.mExpandedHeight) || (this.mPeekPending) || (this.mPeekAnimator != null) || (this.mPeekTouching)) {
      return;
    }
    setExpandedHeight(f);
  }
  
  public abstract void resetViews();
  
  public void setBar(PanelBar paramPanelBar)
  {
    this.mBar = paramPanelBar;
  }
  
  public void setExpandedFraction(float paramFloat)
  {
    setExpandedHeight(getMaxPanelHeight() * paramFloat);
  }
  
  public void setExpandedHeight(float paramFloat)
  {
    setExpandedHeightInternal(getOverExpansionPixels() + paramFloat);
  }
  
  public void setExpandedHeightInternal(float paramFloat)
  {
    float f1 = 0.0F;
    float f2 = getMaxPanelHeight() - getOverExpansionAmount();
    if (this.mHeightAnimator == null)
    {
      float f3 = Math.max(0.0F, paramFloat - f2);
      if ((getOverExpansionPixels() != f3) && (this.mTracking)) {
        setOverExpansion(f3, true);
      }
      this.mExpandedHeight = (Math.min(paramFloat, f2) + getOverExpansionAmount());
      this.mExpandedHeight = Math.max(0.0F, this.mExpandedHeight);
      paramFloat = f1;
      if (f2 != 0.0F) {
        if (getRealMaxPanelHeight() != 0) {
          break label145;
        }
      }
    }
    label145:
    for (paramFloat = f1;; paramFloat = this.mExpandedHeight / f2)
    {
      this.mExpandedFraction = Math.min(1.0F, paramFloat);
      onHeightUpdated(this.mExpandedHeight);
      notifyBarPanelExpansionChanged();
      return;
      this.mExpandedHeight = paramFloat;
      if (!this.mOverExpandedBeforeFling) {
        break;
      }
      setOverExpansion(Math.max(0.0F, paramFloat - f2), false);
      break;
    }
  }
  
  public void setHeadsUpManager(HeadsUpManager paramHeadsUpManager)
  {
    this.mHeadsUpManager = paramHeadsUpManager;
  }
  
  protected abstract void setOverExpansion(float paramFloat, boolean paramBoolean);
  
  public void setTouchDisabled(boolean paramBoolean)
  {
    this.mTouchDisabled = paramBoolean;
    if ((this.mTouchDisabled) && (this.mTracking)) {
      onTrackingStopped(true);
    }
  }
  
  protected abstract boolean shouldGestureIgnoreXTouchSlop(float paramFloat1, float paramFloat2);
  
  protected void startExpandMotion(float paramFloat1, float paramFloat2, boolean paramBoolean, float paramFloat3)
  {
    this.mInitialOffsetOnTouch = paramFloat3;
    this.mInitialTouchY = paramFloat2;
    this.mInitialTouchX = paramFloat1;
    if (paramBoolean)
    {
      this.mTouchSlopExceeded = true;
      setExpandedHeight(this.mInitialOffsetOnTouch);
      onTrackingStarted();
    }
  }
  
  public void startFacelockFailAnimation()
  {
    this.mKeyguardBottomArea.getIndicationView().animate().translationY(-this.mHintDistance).setDuration(250L).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).withEndAction(new Runnable()
    {
      public void run()
      {
        PanelView.this.mKeyguardBottomArea.getIndicationView().animate().translationY(0.0F).setDuration(450L).setInterpolator(PanelView.-get1(PanelView.this)).start();
      }
    }).start();
  }
  
  protected void startUnlockHintAnimation()
  {
    if ((this.mHeightAnimator != null) || (this.mTracking)) {
      return;
    }
    cancelPeek();
    notifyExpandingStarted();
    startUnlockHintAnimationPhase1(new Runnable()
    {
      public void run()
      {
        PanelView.this.notifyExpandingFinished();
        PanelView.this.mStatusBar.onHintFinished();
        PanelView.this.mHintAnimationRunning = false;
      }
    });
    this.mStatusBar.onUnlockHintStarted();
    this.mHintAnimationRunning = true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\PanelView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */