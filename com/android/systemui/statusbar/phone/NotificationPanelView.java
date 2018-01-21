package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Debug;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.EventLog;
import android.util.Log;
import android.util.MathUtils;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.keyguard.KeyguardStatusView;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.AutoReinflateContainer;
import com.android.systemui.AutoReinflateContainer.InflateListener;
import com.android.systemui.DejankUtils;
import com.android.systemui.EventLogTags;
import com.android.systemui.Interpolators;
import com.android.systemui.classifier.FalsingManager;
import com.android.systemui.plugin.LSState;
import com.android.systemui.plugin.PreventModeCtrl;
import com.android.systemui.qs.QSContainer;
import com.android.systemui.qs.QSPanel;
import com.android.systemui.qs.customize.QSCustomizer;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.ExpandableView;
import com.android.systemui.statusbar.ExpandableView.OnHeightChangedListener;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.statusbar.GestureRecorder;
import com.android.systemui.statusbar.KeyguardAffordanceView;
import com.android.systemui.statusbar.NotificationData.Entry;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.HeadsUpManager.OnHeadsUpChangedListener;
import com.android.systemui.statusbar.policy.KeyguardUserSwitcher;
import com.android.systemui.statusbar.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.stack.NotificationStackScrollLayout.OnEmptySpaceClickListener;
import com.android.systemui.statusbar.stack.NotificationStackScrollLayout.OnOverscrollTopChangedListener;
import com.android.systemui.util.MdmLogger;
import com.android.systemui.util.Utils;
import java.io.PrintWriter;
import java.util.List;

public class NotificationPanelView
  extends PanelView
  implements ExpandableView.OnHeightChangedListener, View.OnClickListener, NotificationStackScrollLayout.OnOverscrollTopChangedListener, KeyguardAffordanceHelper.Callback, NotificationStackScrollLayout.OnEmptySpaceClickListener, HeadsUpManager.OnHeadsUpChangedListener
{
  private static final Rect mDummyDirtyRect = new Rect(0, 0, 1, 1);
  public static int mLastCameraGestureLaunchSource = 0;
  private KeyguardAffordanceHelper mAfforanceHelper;
  private final Runnable mAnimateKeyguardBottomAreaInvisibleEndRunnable = new Runnable()
  {
    public void run()
    {
      NotificationPanelView.this.mKeyguardBottomArea.setVisibility(8);
    }
  };
  private final Runnable mAnimateKeyguardStatusBarInvisibleEndRunnable = new Runnable()
  {
    public void run()
    {
      NotificationPanelView.-get5(NotificationPanelView.this).setVisibility(4);
      NotificationPanelView.-get5(NotificationPanelView.this).setAlpha(1.0F);
      NotificationPanelView.-set3(NotificationPanelView.this, 1.0F);
    }
  };
  private final Runnable mAnimateKeyguardStatusViewInvisibleEndRunnable = new Runnable()
  {
    public void run()
    {
      NotificationPanelView.-set4(NotificationPanelView.this, false);
      NotificationPanelView.-get6(NotificationPanelView.this).setVisibility(8);
    }
  };
  private final Runnable mAnimateKeyguardStatusViewVisibleEndRunnable = new Runnable()
  {
    public void run()
    {
      NotificationPanelView.-set4(NotificationPanelView.this, false);
    }
  };
  private boolean mAnimateNextTopPaddingChange;
  private boolean mBlockTouches;
  private int mClockAnimationTarget = -1;
  private ObjectAnimator mClockAnimator;
  private KeyguardClockPositionAlgorithm mClockPositionAlgorithm = new KeyguardClockPositionAlgorithm();
  private KeyguardClockPositionAlgorithm.Result mClockPositionResult = new KeyguardClockPositionAlgorithm.Result();
  private TextView mClockView;
  private boolean mClosingWithAlphaFadeOut;
  private boolean mCollapsedOnDown;
  private boolean mConflictingQsExpansionGesture;
  private boolean mDozing;
  private boolean mDozingOnDown;
  private float mEmptyDragAmount;
  private boolean mExpandingFromHeadsUp;
  private FalsingManager mFalsingManager;
  private FlingAnimationUtils mFlingAnimationUtils;
  private NotificationGroupManager mGroupManager;
  private Handler mHandler;
  private boolean mHeadsUpAnimatingAway;
  private Runnable mHeadsUpExistenceChangedRunnable = new Runnable()
  {
    public void run()
    {
      NotificationPanelView.-set2(NotificationPanelView.this, false);
      NotificationPanelView.this.notifyBarPanelExpansionChanged();
    }
  };
  private HeadsUpTouchHelper mHeadsUpTouchHelper;
  private float mInitialHeightOnTouch;
  private float mInitialTouchX;
  private float mInitialTouchY;
  private boolean mIntercepting;
  private boolean mIsExpanding;
  private boolean mIsExpansionFromHeadsUp;
  private boolean mIsLaunchTransitionFinished;
  private boolean mIsLaunchTransitionRunning;
  private boolean mKeyguardShowing;
  private KeyguardStatusBarView mKeyguardStatusBar;
  private float mKeyguardStatusBarAnimateAlpha = 1.0F;
  private KeyguardStatusView mKeyguardStatusView;
  private boolean mKeyguardStatusViewAnimating;
  private KeyguardUserSwitcher mKeyguardUserSwitcher;
  private boolean mLastAnnouncementWasQuickSettings;
  private String mLastCameraLaunchSource = "lockscreen_affordance";
  private int mLastOrientation = -1;
  private float mLastOverscroll;
  private float mLastTouchX;
  private float mLastTouchY;
  private Runnable mLaunchAnimationEndRunnable;
  private boolean mLaunchingAffordance;
  private boolean mListenForHeadsUp;
  private int mMaxFadeoutHeight;
  private int mNavigationBarBottomHeight;
  boolean mNeedCloseQs = false;
  protected NotificationsQuickSettingsContainer mNotificationContainerParent;
  private int mNotificationScrimWaitDistance;
  protected NotificationStackScrollLayout mNotificationStackScroller;
  private int mNotificationsHeaderCollideDistance;
  private int mOldLayoutDirection;
  private boolean mOnlyAffordanceInThisMotion;
  private boolean mPanelExpanded;
  private int mPositionMinSideMargin;
  private boolean mQsAnimatorExpand;
  private AutoReinflateContainer mQsAutoReinflateContainer;
  protected QSContainer mQsContainer;
  private boolean mQsExpandImmediate;
  private boolean mQsExpanded;
  private boolean mQsExpandedWhenExpandingStarted;
  private ValueAnimator mQsExpansionAnimator;
  protected boolean mQsExpansionEnabled = true;
  private boolean mQsExpansionFromOverscroll;
  protected float mQsExpansionHeight;
  private int mQsFalsingThreshold;
  private boolean mQsFullyExpanded;
  protected int mQsMaxExpansionHeight;
  protected int mQsMinExpansionHeight;
  private int mQsPeekHeight;
  private boolean mQsScrimEnabled = true;
  private ValueAnimator mQsSizeChangeAnimator;
  private boolean mQsTouchAboveFalsingThreshold;
  private boolean mQsTracking;
  private boolean mShadeEmpty;
  private boolean mStackScrollerOverscrolling;
  private final ValueAnimator.AnimatorUpdateListener mStatusBarAnimateAlphaListener = new ValueAnimator.AnimatorUpdateListener()
  {
    public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
    {
      NotificationPanelView.-set3(NotificationPanelView.this, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
      NotificationPanelView.-wrap3(NotificationPanelView.this);
    }
  };
  private int mStatusBarMinHeight;
  private int mStatusBarState;
  private int mTopPaddingAdjustment;
  private int mTrackingPointer;
  private boolean mTwoFingerQsExpandPossible;
  private boolean mUnlockIconActive;
  private int mUnlockMoveDistance;
  final Runnable mUpdateCameraStateTimeout = new Runnable()
  {
    public void run()
    {
      KeyguardUpdateMonitor.getInstance(NotificationPanelView.-get3(NotificationPanelView.this)).updateLaunchingCameraState(false);
    }
  };
  private final Runnable mUpdateHeader = new Runnable()
  {
    public void run()
    {
      NotificationPanelView.this.mQsContainer.getHeader().updateEverything();
    }
  };
  private VelocityTracker mVelocityTracker;
  private StringBuffer setAlphaTrace = new StringBuffer();
  
  public NotificationPanelView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setWillNotDraw(true);
    this.mFalsingManager = FalsingManager.getInstance(paramContext);
  }
  
  private void animateKeyguardStatusBarIn(long paramLong)
  {
    this.mKeyguardStatusBar.setVisibility(0);
    this.mKeyguardStatusBar.setAlpha(0.0F);
    ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
    localValueAnimator.addUpdateListener(this.mStatusBarAnimateAlphaListener);
    localValueAnimator.setDuration(paramLong);
    localValueAnimator.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
    localValueAnimator.start();
  }
  
  private void animateKeyguardStatusBarOut()
  {
    ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { this.mKeyguardStatusBar.getAlpha(), 0.0F });
    localValueAnimator.addUpdateListener(this.mStatusBarAnimateAlphaListener);
    if (this.mStatusBar.isKeyguardFadingAway())
    {
      l = this.mStatusBar.getKeyguardFadingAwayDelay();
      localValueAnimator.setStartDelay(l);
      if (!this.mStatusBar.isKeyguardFadingAway()) {
        break label109;
      }
    }
    label109:
    for (long l = this.mStatusBar.getKeyguardFadingAwayDuration() / 2L;; l = 360L)
    {
      localValueAnimator.setDuration(l);
      localValueAnimator.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
      localValueAnimator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          NotificationPanelView.-get0(NotificationPanelView.this).run();
        }
      });
      localValueAnimator.start();
      return;
      l = 0L;
      break;
    }
  }
  
  private int calculatePanelHeightQsExpanded()
  {
    float f2 = this.mNotificationStackScroller.getHeight() - this.mNotificationStackScroller.getEmptyBottomMargin() - this.mNotificationStackScroller.getTopPadding();
    float f1 = f2;
    if (this.mNotificationStackScroller.getNotGoneChildCount() == 0)
    {
      f1 = f2;
      if (this.mShadeEmpty) {
        f1 = this.mNotificationStackScroller.getEmptyShadeViewHeight() + this.mNotificationStackScroller.getBottomStackPeekSize() + this.mNotificationStackScroller.getBottomStackSlowDownHeight();
      }
    }
    int i = this.mQsMaxExpansionHeight;
    if (this.mQsSizeChangeAnimator != null) {
      i = ((Integer)this.mQsSizeChangeAnimator.getAnimatedValue()).intValue();
    }
    if (this.mStatusBarState == 1) {}
    for (int j = this.mClockPositionResult.stackScrollerPadding - this.mTopPaddingAdjustment;; j = 0)
    {
      f2 = Math.max(i, j) + f1 + this.mNotificationStackScroller.getTopPaddingOverflow();
      f1 = f2;
      if (f2 > this.mNotificationStackScroller.getHeight()) {
        f1 = Math.max(this.mNotificationStackScroller.getLayoutMinHeight() + i, this.mNotificationStackScroller.getHeight());
      }
      return (int)f1;
    }
  }
  
  private int calculatePanelHeightShade()
  {
    int i = this.mNotificationStackScroller.getEmptyBottomMargin();
    return (int)(this.mNotificationStackScroller.getHeight() - i - this.mTopPaddingAdjustment + this.mNotificationStackScroller.getTopPaddingOverflow());
  }
  
  private float calculateQsTopPadding()
  {
    if ((this.mKeyguardShowing) && ((this.mQsExpandImmediate) || ((this.mIsExpanding) && (this.mQsExpandedWhenExpandingStarted))))
    {
      int j = this.mClockPositionResult.stackScrollerPadding;
      int k = this.mClockPositionResult.stackScrollerPaddingAdjustment;
      int i = getTempQsMaxExpansion();
      if (this.mStatusBarState == 1) {
        i = Math.max(j - k, i);
      }
      for (;;)
      {
        return (int)interpolate(getExpandedFraction(), this.mQsMinExpansionHeight, i);
      }
    }
    if (this.mQsSizeChangeAnimator != null) {
      return ((Integer)this.mQsSizeChangeAnimator.getAnimatedValue()).intValue();
    }
    if (this.mKeyguardShowing) {
      return interpolate(getQsExpansionFraction(), this.mNotificationStackScroller.getIntrinsicPadding(), this.mQsMaxExpansionHeight);
    }
    return this.mQsExpansionHeight;
  }
  
  private void cancelQsAnimation()
  {
    if (this.mQsExpansionAnimator != null) {
      this.mQsExpansionAnimator.cancel();
    }
  }
  
  private boolean flingExpandsQs(float paramFloat)
  {
    if (isFalseTouch()) {
      return false;
    }
    if (Math.abs(paramFloat) < this.mFlingAnimationUtils.getMinVelocityPxPerSecond()) {
      return getQsExpansionFraction() > 0.5F;
    }
    return paramFloat > 0.0F;
  }
  
  private void flingQsWithCurrentVelocity(float paramFloat, boolean paramBoolean)
  {
    boolean bool2 = false;
    float f = getCurrentVelocity();
    boolean bool3 = flingExpandsQs(f);
    if (bool3) {
      logQsSwipeDown(paramFloat);
    }
    boolean bool1 = bool2;
    if (bool3) {
      if (!paramBoolean) {
        break label50;
      }
    }
    label50:
    for (bool1 = bool2;; bool1 = true)
    {
      flingSettings(f, bool1);
      return;
    }
  }
  
  private void flingSettings(float paramFloat, final boolean paramBoolean1, final Runnable paramRunnable, boolean paramBoolean2)
  {
    if (paramBoolean1) {}
    float f1;
    final float f2;
    for (int i = this.mQsMaxExpansionHeight;; i = this.mQsMinExpansionHeight)
    {
      f1 = i;
      f2 = this.mQsExpansionHeight;
      if (f1 != this.mQsExpansionHeight) {
        break;
      }
      if (paramRunnable != null) {
        paramRunnable.run();
      }
      return;
    }
    boolean bool = isFalseTouch();
    if (bool) {
      paramFloat = 0.0F;
    }
    this.mNeedCloseQs = false;
    ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { this.mQsExpansionHeight, f1 });
    if (paramBoolean2)
    {
      localValueAnimator.setInterpolator(Interpolators.TOUCH_RESPONSE);
      localValueAnimator.setDuration(368L);
    }
    for (;;)
    {
      if (bool) {
        localValueAnimator.setDuration(350L);
      }
      localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          NotificationPanelView.-wrap2(NotificationPanelView.this, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
          NotificationPanelView localNotificationPanelView = NotificationPanelView.this;
          if (((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue() == f2) {}
          for (boolean bool = true;; bool = false)
          {
            localNotificationPanelView.mNeedCloseQs = bool;
            return;
          }
        }
      });
      localValueAnimator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          if ((paramBoolean1) && (NotificationPanelView.this.mNeedCloseQs))
          {
            Log.d(NotificationPanelView.TAG, "The expand animation is canceled, close QS Detail");
            NotificationPanelView.this.closeQsDetail();
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          NotificationPanelView.-set5(NotificationPanelView.this, null);
          if (paramRunnable != null) {
            paramRunnable.run();
          }
        }
      });
      localValueAnimator.start();
      this.mQsExpansionAnimator = localValueAnimator;
      this.mQsAnimatorExpand = paramBoolean1;
      return;
      this.mFlingAnimationUtils.apply(localValueAnimator, this.mQsExpansionHeight, f1, paramFloat);
    }
  }
  
  private float getCurrentVelocity()
  {
    if (this.mVelocityTracker == null) {
      return 0.0F;
    }
    this.mVelocityTracker.computeCurrentVelocity(1000);
    return this.mVelocityTracker.getYVelocity();
  }
  
  private float getFadeoutAlpha()
  {
    return (float)Math.pow(Math.max(0.0F, Math.min((getNotificationsTopY() + this.mNotificationStackScroller.getFirstItemMinHeight()) / (this.mQsMinExpansionHeight + this.mNotificationStackScroller.getBottomStackPeekSize() - this.mNotificationStackScroller.getBottomStackSlowDownHeight()), 1.0F)), 0.75D);
  }
  
  private int getFalsingThreshold()
  {
    if (this.mStatusBar.isWakeUpComingFromTouch()) {}
    for (float f = 1.5F;; f = 1.0F) {
      return (int)(this.mQsFalsingThreshold * f);
    }
  }
  
  private float getKeyguardContentsAlpha()
  {
    if (this.mStatusBar.getBarState() == 1) {}
    for (float f = getNotificationsTopY() / (this.mKeyguardStatusBar.getHeight() + this.mNotificationsHeaderCollideDistance);; f = getNotificationsTopY() / this.mKeyguardStatusBar.getHeight()) {
      return (float)Math.pow(MathUtils.constrain(f, 0.0F, 1.0F), 0.75D);
    }
  }
  
  private String getKeyguardOrLockScreenString()
  {
    if (this.mQsContainer.isCustomizing()) {
      return getContext().getString(2131690626);
    }
    if (this.mStatusBarState == 1) {
      return getContext().getString(2131690187);
    }
    return getContext().getString(2131690185);
  }
  
  private float getNotificationsTopY()
  {
    if (this.mNotificationStackScroller.getNotGoneChildCount() == 0) {
      return getExpandedHeight();
    }
    return this.mNotificationStackScroller.getNotificationsTopY();
  }
  
  private float getQsExpansionFraction()
  {
    return Math.min(1.0F, (this.mQsExpansionHeight - this.mQsMinExpansionHeight) / (getTempQsMaxExpansion() - this.mQsMinExpansionHeight));
  }
  
  private int getTempQsMaxExpansion()
  {
    return this.mQsMaxExpansionHeight;
  }
  
  private void handleQsDown(MotionEvent paramMotionEvent)
  {
    if ((paramMotionEvent.getActionMasked() == 0) && (shouldQuickSettingsIntercept(paramMotionEvent.getX(), paramMotionEvent.getY(), -1.0F)))
    {
      this.mFalsingManager.onQsDown();
      this.mQsTracking = true;
      onQsExpansionStarted();
      this.mInitialHeightOnTouch = this.mQsExpansionHeight;
      this.mInitialTouchY = paramMotionEvent.getX();
      this.mInitialTouchX = paramMotionEvent.getY();
      notifyExpandingFinished();
    }
  }
  
  private boolean handleQsTouch(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getActionMasked();
    if ((i != 0) || (getExpandedFraction() != 1.0F) || (this.mStatusBar.getBarState() == 1) || (this.mQsExpanded)) {}
    for (;;)
    {
      if (!isFullyCollapsed()) {
        handleQsDown(paramMotionEvent);
      }
      if ((this.mQsExpandImmediate) || (!this.mQsTracking)) {
        break;
      }
      onQsTouch(paramMotionEvent);
      if (this.mConflictingQsExpansionGesture) {
        break;
      }
      return true;
      if (this.mQsExpansionEnabled)
      {
        this.mQsTracking = true;
        this.mConflictingQsExpansionGesture = true;
        onQsExpansionStarted();
        this.mInitialHeightOnTouch = this.mQsExpansionHeight;
        this.mInitialTouchY = paramMotionEvent.getX();
        this.mInitialTouchX = paramMotionEvent.getY();
      }
    }
    if ((i == 3) || (i == 1)) {
      this.mConflictingQsExpansionGesture = false;
    }
    if ((i == 0) && (isFullyCollapsed()) && (this.mQsExpansionEnabled)) {
      this.mTwoFingerQsExpandPossible = true;
    }
    if ((this.mTwoFingerQsExpandPossible) && (isOpenQsEvent(paramMotionEvent)) && (paramMotionEvent.getY(paramMotionEvent.getActionIndex()) < this.mStatusBarMinHeight))
    {
      MetricsLogger.count(this.mContext, "panel_open_qs", 1);
      this.mQsExpandImmediate = true;
      requestPanelHeightUpdate();
      setListening(true);
    }
    return false;
  }
  
  private void initDownStates(MotionEvent paramMotionEvent)
  {
    boolean bool = false;
    if (paramMotionEvent.getActionMasked() == 0)
    {
      this.mOnlyAffordanceInThisMotion = false;
      this.mQsTouchAboveFalsingThreshold = this.mQsFullyExpanded;
      this.mDozingOnDown = isDozing();
      this.mCollapsedOnDown = isFullyCollapsed();
      if (this.mCollapsedOnDown) {
        bool = this.mHeadsUpManager.hasPinnedHeadsUp();
      }
      this.mListenForHeadsUp = bool;
    }
  }
  
  private void initVelocityTracker()
  {
    if (this.mVelocityTracker != null) {
      this.mVelocityTracker.recycle();
    }
    this.mVelocityTracker = VelocityTracker.obtain();
  }
  
  private static float interpolate(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return (1.0F - paramFloat1) * paramFloat2 + paramFloat1 * paramFloat3;
  }
  
  private boolean isFalseTouch()
  {
    if (!needsAntiFalsing()) {
      return false;
    }
    if (this.mFalsingManager.isClassiferEnabled()) {
      return this.mFalsingManager.isFalseTouch();
    }
    return !this.mQsTouchAboveFalsingThreshold;
  }
  
  private boolean isForegroundApp(String paramString)
  {
    boolean bool = false;
    if (paramString == null) {
      return false;
    }
    List localList = ((ActivityManager)getContext().getSystemService(ActivityManager.class)).getRunningTasks(1);
    if (!localList.isEmpty()) {
      bool = paramString.equals(((ActivityManager.RunningTaskInfo)localList.get(0)).topActivity.getPackageName());
    }
    return bool;
  }
  
  private boolean isInQsArea(float paramFloat1, float paramFloat2)
  {
    if ((paramFloat1 >= this.mQsAutoReinflateContainer.getX()) && (paramFloat1 <= this.mQsAutoReinflateContainer.getX() + this.mQsAutoReinflateContainer.getWidth())) {
      return (paramFloat2 <= this.mNotificationStackScroller.getBottomMostNotificationBottom()) || (paramFloat2 <= this.mQsContainer.getY() + this.mQsContainer.getHeight());
    }
    return false;
  }
  
  private boolean isOpenQsEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getPointerCount();
    int j = paramMotionEvent.getActionMasked();
    boolean bool1;
    label43:
    boolean bool2;
    if (j == 5) {
      if (i == 2)
      {
        i = 1;
        if (j != 0) {
          break label90;
        }
        if (paramMotionEvent.isButtonPressed(32)) {
          break label84;
        }
        bool1 = paramMotionEvent.isButtonPressed(64);
        if (j != 0) {
          break label102;
        }
        if (paramMotionEvent.isButtonPressed(2)) {
          break label96;
        }
        bool2 = paramMotionEvent.isButtonPressed(4);
      }
    }
    for (;;)
    {
      if ((i != 0) || (bool1)) {
        break label108;
      }
      return bool2;
      i = 0;
      break;
      i = 0;
      break;
      label84:
      bool1 = true;
      break label43;
      label90:
      bool1 = false;
      break label43;
      label96:
      bool2 = true;
      continue;
      label102:
      bool2 = false;
    }
    label108:
    return true;
  }
  
  private void logQsSwipeDown(float paramFloat)
  {
    float f = getCurrentVelocity();
    if (this.mStatusBarState == 1) {}
    for (int i = 8;; i = 9)
    {
      EventLogTags.writeSysuiLockscreenGesture(i, (int)((paramFloat - this.mInitialTouchY) / this.mStatusBar.getDisplayDensity()), (int)(f / this.mStatusBar.getDisplayDensity()));
      return;
    }
  }
  
  private void onQsExpansionStarted()
  {
    onQsExpansionStarted(0);
  }
  
  private void onQsExpansionStarted(int paramInt)
  {
    cancelQsAnimation();
    cancelHeightAnimator();
    setQsExpansion(this.mQsExpansionHeight - paramInt);
    requestPanelHeightUpdate();
  }
  
  private boolean onQsIntercept(MotionEvent paramMotionEvent)
  {
    boolean bool = true;
    int j = 1;
    int k = paramMotionEvent.findPointerIndex(this.mTrackingPointer);
    int i = k;
    if (k < 0)
    {
      i = 0;
      this.mTrackingPointer = paramMotionEvent.getPointerId(0);
    }
    float f1 = paramMotionEvent.getX(i);
    float f2 = paramMotionEvent.getY(i);
    switch (paramMotionEvent.getActionMasked())
    {
    case 4: 
    case 5: 
    default: 
    case 0: 
    case 6: 
    case 2: 
      float f3;
      do
      {
        do
        {
          do
          {
            return false;
            if (isOverScrollRunning()) {
              cancelOverScroll();
            }
            this.mIntercepting = true;
            this.mInitialTouchY = f2;
            this.mInitialTouchX = f1;
            initVelocityTracker();
            trackMovement(paramMotionEvent);
            if (shouldQuickSettingsIntercept(this.mInitialTouchX, this.mInitialTouchY, 0.0F)) {
              getParent().requestDisallowInterceptTouchEvent(true);
            }
          } while (this.mQsExpansionAnimator == null);
          onQsExpansionStarted();
          this.mInitialHeightOnTouch = this.mQsExpansionHeight;
          this.mQsTracking = true;
          this.mIntercepting = false;
          this.mNotificationStackScroller.removeLongPressCallback();
          return false;
          k = paramMotionEvent.getPointerId(paramMotionEvent.getActionIndex());
        } while (this.mTrackingPointer != k);
        i = j;
        if (paramMotionEvent.getPointerId(0) != k) {
          i = 0;
        }
        this.mTrackingPointer = paramMotionEvent.getPointerId(i);
        this.mInitialTouchX = paramMotionEvent.getX(i);
        this.mInitialTouchY = paramMotionEvent.getY(i);
        return false;
        f3 = f2 - this.mInitialTouchY;
        trackMovement(paramMotionEvent);
        if (this.mQsTracking)
        {
          setQsExpansion(this.mInitialHeightOnTouch + f3);
          trackMovement(paramMotionEvent);
          this.mIntercepting = false;
          return true;
        }
      } while ((Math.abs(f3) <= this.mTouchSlop) || (Math.abs(f3) <= Math.abs(f1 - this.mInitialTouchX)) || (!shouldQuickSettingsIntercept(this.mInitialTouchX, this.mInitialTouchY, f3)));
      this.mQsTracking = true;
      onQsExpansionStarted();
      notifyExpandingFinished();
      this.mInitialHeightOnTouch = this.mQsExpansionHeight;
      this.mInitialTouchY = f2;
      this.mInitialTouchX = f1;
      this.mIntercepting = false;
      this.mNotificationStackScroller.removeLongPressCallback();
      return true;
    }
    trackMovement(paramMotionEvent);
    if (this.mQsTracking) {
      if (paramMotionEvent.getActionMasked() != 3) {
        break label441;
      }
    }
    for (;;)
    {
      flingQsWithCurrentVelocity(f2, bool);
      this.mQsTracking = false;
      this.mIntercepting = false;
      return false;
      label441:
      bool = false;
    }
  }
  
  private void onQsTouch(MotionEvent paramMotionEvent)
  {
    boolean bool = true;
    int j = 0;
    int k = paramMotionEvent.findPointerIndex(this.mTrackingPointer);
    int i = k;
    if (k < 0)
    {
      i = 0;
      this.mTrackingPointer = paramMotionEvent.getPointerId(0);
    }
    float f1 = paramMotionEvent.getY(i);
    float f2 = paramMotionEvent.getX(i);
    float f3 = f1 - this.mInitialTouchY;
    switch (paramMotionEvent.getActionMasked())
    {
    case 4: 
    case 5: 
    default: 
    case 0: 
    case 6: 
      do
      {
        return;
        this.mQsTracking = true;
        this.mInitialTouchY = f1;
        this.mInitialTouchX = f2;
        if (!this.mStatusBar.isHighlightHintViewShowing()) {
          onQsExpansionStarted();
        }
        this.mInitialHeightOnTouch = this.mQsExpansionHeight;
        initVelocityTracker();
        trackMovement(paramMotionEvent);
        return;
        i = paramMotionEvent.getPointerId(paramMotionEvent.getActionIndex());
      } while (this.mTrackingPointer != i);
      if (paramMotionEvent.getPointerId(0) != i) {}
      for (i = j;; i = 1)
      {
        f1 = paramMotionEvent.getY(i);
        f2 = paramMotionEvent.getX(i);
        this.mTrackingPointer = paramMotionEvent.getPointerId(i);
        this.mInitialHeightOnTouch = this.mQsExpansionHeight;
        this.mInitialTouchY = f1;
        this.mInitialTouchX = f2;
        return;
      }
    case 2: 
      if (!this.mQsContainer.getCustomizer().isShown()) {
        setQsExpansion(this.mInitialHeightOnTouch + f3);
      }
      if (f3 >= getFalsingThreshold()) {
        this.mQsTouchAboveFalsingThreshold = true;
      }
      trackMovement(paramMotionEvent);
      return;
    }
    this.mQsTracking = false;
    this.mTrackingPointer = -1;
    trackMovement(paramMotionEvent);
    if ((getQsExpansionFraction() != 0.0F) || (f1 >= this.mInitialTouchY)) {
      if (paramMotionEvent.getActionMasked() != 3) {
        break label348;
      }
    }
    for (;;)
    {
      flingQsWithCurrentVelocity(f1, bool);
      if (this.mVelocityTracker == null) {
        break;
      }
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
      return;
      label348:
      bool = false;
    }
  }
  
  private void positionClockAndNotifications()
  {
    boolean bool = this.mNotificationStackScroller.isAddOrRemoveAnimationPending();
    int i;
    if (this.mStatusBarState != 1)
    {
      i = this.mQsContainer.getHeader().getHeight() + this.mQsPeekHeight;
      this.mTopPaddingAdjustment = 0;
      this.mNotificationStackScroller.setIntrinsicPadding(i);
      requestScrollerTopPaddingUpdate(bool);
      return;
    }
    this.mClockPositionAlgorithm.setup(this.mStatusBar.getMaxKeyguardNotifications(), getMaxPanelHeight(), getExpandedHeight(), this.mNotificationStackScroller.getNotGoneChildCount(), getHeight(), this.mKeyguardStatusView.getHeight(), this.mEmptyDragAmount);
    this.mClockPositionAlgorithm.run(this.mClockPositionResult);
    if ((bool) || (this.mClockAnimator != null)) {
      startClockAnimation(this.mClockPositionResult.clockY);
    }
    for (;;)
    {
      this.mStatusBar.getFacelockController().setClockY(this.mClockPositionResult.clockY);
      updateClock(this.mClockPositionResult.clockAlpha, this.mClockPositionResult.clockScale);
      i = this.mClockPositionResult.stackScrollerPadding;
      this.mTopPaddingAdjustment = this.mClockPositionResult.stackScrollerPaddingAdjustment;
      break;
      this.mKeyguardStatusView.setY(this.mClockPositionResult.clockY);
    }
  }
  
  private void resetVerticalPanelPosition()
  {
    setVerticalPanelTranslation(0.0F);
  }
  
  private void setClosingWithAlphaFadeout(boolean paramBoolean)
  {
    this.mClosingWithAlphaFadeOut = paramBoolean;
    this.mNotificationStackScroller.forceNoOverlappingRendering(paramBoolean);
  }
  
  private void setKeyguardBottomAreaVisibility(int paramInt, boolean paramBoolean)
  {
    this.mKeyguardBottomArea.animate().cancel();
    if (paramBoolean)
    {
      this.mKeyguardBottomArea.animate().alpha(0.0F).setStartDelay(this.mStatusBar.getKeyguardFadingAwayDelay()).setDuration(this.mStatusBar.getKeyguardFadingAwayDuration() / 2L).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(this.mAnimateKeyguardBottomAreaInvisibleEndRunnable).start();
      return;
    }
    if ((paramInt == 1) || (paramInt == 2))
    {
      if (!this.mDozing) {
        this.mKeyguardBottomArea.setVisibility(0);
      }
      this.mKeyguardBottomArea.setAlpha(1.0F);
      return;
    }
    this.mKeyguardBottomArea.setVisibility(8);
    this.mKeyguardBottomArea.setAlpha(1.0F);
  }
  
  private void setKeyguardStatusViewVisibility(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((!paramBoolean1) && (this.mStatusBarState == 1) && (paramInt != 1)) {}
    while (paramBoolean2)
    {
      this.mKeyguardStatusView.animate().cancel();
      this.mKeyguardStatusViewAnimating = true;
      this.mKeyguardStatusView.animate().alpha(0.0F).setStartDelay(0L).setDuration(160L).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(this.mAnimateKeyguardStatusViewInvisibleEndRunnable);
      if (paramBoolean1) {
        this.mKeyguardStatusView.animate().setStartDelay(this.mStatusBar.getKeyguardFadingAwayDelay()).setDuration(this.mStatusBar.getKeyguardFadingAwayDuration() / 2L).start();
      }
      return;
    }
    if ((this.mStatusBarState == 2) && (paramInt == 1))
    {
      this.mKeyguardStatusView.animate().cancel();
      this.mKeyguardStatusView.setVisibility(0);
      this.mKeyguardStatusViewAnimating = true;
      this.mKeyguardStatusView.setAlpha(0.0F);
      this.mKeyguardStatusView.animate().alpha(1.0F).setStartDelay(0L).setDuration(320L).setInterpolator(Interpolators.ALPHA_IN).withEndAction(this.mAnimateKeyguardStatusViewVisibleEndRunnable);
      return;
    }
    if (paramInt == 1)
    {
      this.mKeyguardStatusView.animate().cancel();
      this.mKeyguardStatusViewAnimating = false;
      this.mKeyguardStatusView.setVisibility(0);
      this.mKeyguardStatusView.setAlpha(1.0F);
      return;
    }
    this.mKeyguardStatusView.animate().cancel();
    this.mKeyguardStatusViewAnimating = false;
    this.mKeyguardStatusView.setVisibility(8);
    this.mKeyguardStatusView.setAlpha(1.0F);
  }
  
  private void setLaunchingAffordance(boolean paramBoolean)
  {
    getLeftIcon().setLaunchingAffordance(paramBoolean);
    getRightIcon().setLaunchingAffordance(paramBoolean);
    getCenterIcon().setLaunchingAffordance(paramBoolean);
  }
  
  private void setListening(boolean paramBoolean)
  {
    this.mQsContainer.setListening(paramBoolean);
    this.mKeyguardStatusBar.setListening(paramBoolean);
  }
  
  private void setOverScrolling(boolean paramBoolean)
  {
    this.mStackScrollerOverscrolling = paramBoolean;
    this.mQsContainer.setOverscrolling(paramBoolean);
  }
  
  private void setQsExpanded(boolean paramBoolean)
  {
    int i = 0;
    if (this.mQsExpanded != paramBoolean) {
      i = 1;
    }
    if (i != 0)
    {
      this.mQsExpanded = paramBoolean;
      if ((this.mQsExpanded) && (this.mStatusBar.getBarState() != 0)) {
        MdmLogger.log("lock_quicksetting", "", "1");
      }
      updateQsState();
      requestPanelHeightUpdate();
      this.mFalsingManager.setQsExpanded(paramBoolean);
      this.mStatusBar.setQsExpanded(paramBoolean);
      this.mNotificationContainerParent.setQsExpanded(paramBoolean);
    }
  }
  
  private void setQsExpansion(float paramFloat)
  {
    paramFloat = Math.min(Math.max(paramFloat, this.mQsMinExpansionHeight), this.mQsMaxExpansionHeight);
    boolean bool;
    if ((paramFloat == this.mQsMaxExpansionHeight) && (this.mQsMaxExpansionHeight != 0))
    {
      bool = true;
      this.mQsFullyExpanded = bool;
      if ((paramFloat > this.mQsMinExpansionHeight) && (!this.mQsExpanded)) {
        break label195;
      }
      label59:
      if ((paramFloat <= this.mQsMinExpansionHeight) && (this.mQsExpanded))
      {
        setQsExpanded(false);
        if ((this.mLastAnnouncementWasQuickSettings) && (!this.mTracking)) {
          break label210;
        }
      }
      label95:
      this.mQsExpansionHeight = paramFloat;
      updateQsExpansion();
      requestScrollerTopPaddingUpdate(false);
      if (this.mKeyguardShowing) {
        updateHeaderKeyguardAlpha();
      }
      if ((this.mStatusBarState == 2) || (this.mStatusBarState == 1)) {
        updateKeyguardBottomAreaAlpha();
      }
      if ((paramFloat != 0.0F) && (this.mQsFullyExpanded) && (!this.mLastAnnouncementWasQuickSettings)) {
        break label233;
      }
    }
    for (;;)
    {
      if ((this.mQsFullyExpanded) && (this.mFalsingManager.shouldEnforceBouncer())) {
        this.mStatusBar.executeRunnableDismissingKeyguard(null, null, false, true, false);
      }
      return;
      bool = false;
      break;
      label195:
      if (this.mStackScrollerOverscrolling) {
        break label59;
      }
      setQsExpanded(true);
      break label95;
      label210:
      if (isCollapsing()) {
        break label95;
      }
      announceForAccessibility(getKeyguardOrLockScreenString());
      this.mLastAnnouncementWasQuickSettings = false;
      break label95;
      label233:
      announceForAccessibility(getContext().getString(2131690186));
      this.mLastAnnouncementWasQuickSettings = true;
    }
  }
  
  private boolean shouldQuickSettingsIntercept(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    boolean bool2 = false;
    if ((!this.mQsExpansionEnabled) || (this.mCollapsedOnDown)) {
      return false;
    }
    Object localObject;
    boolean bool1;
    if (this.mKeyguardShowing)
    {
      localObject = this.mKeyguardStatusBar;
      if ((paramFloat1 < this.mQsAutoReinflateContainer.getX()) || (paramFloat1 > this.mQsAutoReinflateContainer.getX() + this.mQsAutoReinflateContainer.getWidth()) || (paramFloat2 < ((View)localObject).getTop())) {
        break label141;
      }
      if (paramFloat2 > ((View)localObject).getBottom()) {
        break label135;
      }
      bool1 = true;
    }
    for (;;)
    {
      if (this.mQsExpanded)
      {
        if (!bool1)
        {
          bool1 = bool2;
          if (paramFloat3 < 0.0F) {
            bool1 = isInQsArea(paramFloat1, paramFloat2);
          }
          return bool1;
          localObject = this.mQsContainer.getHeader();
          break;
          label135:
          bool1 = false;
          continue;
          label141:
          bool1 = false;
          continue;
        }
        return true;
      }
    }
    return bool1;
  }
  
  private void startClockAnimation(int paramInt)
  {
    if (this.mClockAnimationTarget == paramInt) {
      return;
    }
    this.mClockAnimationTarget = paramInt;
    getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        NotificationPanelView.this.getViewTreeObserver().removeOnPreDrawListener(this);
        if (NotificationPanelView.-get2(NotificationPanelView.this) != null)
        {
          NotificationPanelView.-get2(NotificationPanelView.this).removeAllListeners();
          NotificationPanelView.-get2(NotificationPanelView.this).cancel();
        }
        NotificationPanelView.-set1(NotificationPanelView.this, ObjectAnimator.ofFloat(NotificationPanelView.-get6(NotificationPanelView.this), View.Y, new float[] { NotificationPanelView.-get1(NotificationPanelView.this) }));
        NotificationPanelView.-get2(NotificationPanelView.this).setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        NotificationPanelView.-get2(NotificationPanelView.this).setDuration(360L);
        NotificationPanelView.-get2(NotificationPanelView.this).addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymous2Animator)
          {
            NotificationPanelView.-set1(NotificationPanelView.this, null);
            NotificationPanelView.-set0(NotificationPanelView.this, -1);
          }
        });
        NotificationPanelView.-get2(NotificationPanelView.this).start();
        return true;
      }
    });
  }
  
  private void startHighlightIconAnimation(final KeyguardAffordanceView paramKeyguardAffordanceView)
  {
    paramKeyguardAffordanceView.setImageAlpha(1.0F, true, 200L, Interpolators.FAST_OUT_SLOW_IN, new Runnable()
    {
      public void run()
      {
        paramKeyguardAffordanceView.setImageAlpha(paramKeyguardAffordanceView.getRestingAlpha(), true, 200L, Interpolators.FAST_OUT_SLOW_IN, null);
      }
    });
  }
  
  private void startQsSizeChangeAnimation(int paramInt1, int paramInt2)
  {
    if (this.mQsSizeChangeAnimator != null)
    {
      paramInt1 = ((Integer)this.mQsSizeChangeAnimator.getAnimatedValue()).intValue();
      this.mQsSizeChangeAnimator.cancel();
    }
    this.mQsSizeChangeAnimator = ValueAnimator.ofInt(new int[] { paramInt1, paramInt2 });
    this.mQsSizeChangeAnimator.setDuration(300L);
    this.mQsSizeChangeAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
    this.mQsSizeChangeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        NotificationPanelView.this.requestScrollerTopPaddingUpdate(false);
        NotificationPanelView.this.requestPanelHeightUpdate();
        int i = ((Integer)NotificationPanelView.-get8(NotificationPanelView.this).getAnimatedValue()).intValue();
        NotificationPanelView.this.mQsContainer.setHeightOverride(i);
      }
    });
    this.mQsSizeChangeAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        NotificationPanelView.-set6(NotificationPanelView.this, null);
      }
    });
    this.mQsSizeChangeAnimator.start();
  }
  
  private void trackMovement(MotionEvent paramMotionEvent)
  {
    if (this.mVelocityTracker != null) {
      this.mVelocityTracker.addMovement(paramMotionEvent);
    }
    this.mLastTouchX = paramMotionEvent.getX();
    this.mLastTouchY = paramMotionEvent.getY();
  }
  
  private void updateClock(float paramFloat1, float paramFloat2)
  {
    if (!this.mKeyguardStatusViewAnimating) {
      this.mKeyguardStatusView.setAlpha(paramFloat1);
    }
    this.mKeyguardStatusView.setScaleX(paramFloat2);
    this.mKeyguardStatusView.setScaleY(paramFloat2);
  }
  
  private void updateDozingVisibilities(boolean paramBoolean)
  {
    if (this.mDozing)
    {
      this.mKeyguardStatusBar.setVisibility(4);
      this.mKeyguardBottomArea.setVisibility(4);
    }
    do
    {
      return;
      this.mKeyguardBottomArea.setVisibility(0);
      this.mKeyguardStatusBar.setVisibility(0);
    } while (!paramBoolean);
    animateKeyguardStatusBarIn(700L);
    this.mKeyguardBottomArea.startFinishDozeAnimation();
  }
  
  private void updateEmptyShadeView()
  {
    boolean bool2 = false;
    NotificationStackScrollLayout localNotificationStackScrollLayout = this.mNotificationStackScroller;
    boolean bool1 = bool2;
    if (this.mShadeEmpty) {
      if (!this.mQsExpanded) {
        break label31;
      }
    }
    label31:
    for (bool1 = bool2;; bool1 = true)
    {
      localNotificationStackScrollLayout.updateEmptyShadeView(bool1);
      return;
    }
  }
  
  private void updateHeader()
  {
    if (this.mStatusBar.getBarState() == 1) {
      updateHeaderKeyguardAlpha();
    }
    updateQsExpansion();
  }
  
  private void updateHeaderKeyguardAlpha()
  {
    float f = Math.min(1.0F, getQsExpansionFraction() * 2.0F);
    this.mKeyguardStatusBar.setAlpha(Math.min(getKeyguardContentsAlpha(), 1.0F - f) * this.mKeyguardStatusBarAnimateAlpha);
    KeyguardStatusBarView localKeyguardStatusBarView = this.mKeyguardStatusBar;
    if ((this.mKeyguardStatusBar.getAlpha() == 0.0F) || (this.mDozing)) {}
    for (int i = 4;; i = 0)
    {
      localKeyguardStatusBarView.setVisibility(i);
      return;
    }
  }
  
  private void updateKeyguardBottomAreaAlpha()
  {
    float f = Math.min(getKeyguardContentsAlpha(), 1.0F - getQsExpansionFraction());
    this.mKeyguardBottomArea.setAlpha(f);
    KeyguardBottomAreaView localKeyguardBottomAreaView = this.mKeyguardBottomArea;
    if (f == 0.0F) {}
    for (int i = 4;; i = 0)
    {
      localKeyguardBottomAreaView.setImportantForAccessibility(i);
      return;
    }
  }
  
  private void updateMaxHeadsUpTranslation()
  {
    this.mNotificationStackScroller.setHeadsUpBoundaries(getHeight(), this.mNavigationBarBottomHeight);
  }
  
  private void updateNotificationTranslucency()
  {
    if ((!this.mClosingWithAlphaFadeOut) || (this.mExpandingFromHeadsUp)) {}
    for (;;)
    {
      this.mNotificationStackScroller.setAlpha(1.0F);
      return;
      if (!this.mHeadsUpManager.hasPinnedHeadsUp()) {
        getFadeoutAlpha();
      }
    }
  }
  
  private void updatePanelExpanded()
  {
    if (isFullyCollapsed()) {}
    for (boolean bool = false;; bool = true)
    {
      if (this.mPanelExpanded != bool)
      {
        this.mHeadsUpManager.setIsExpanded(bool);
        this.mStatusBar.setPanelExpanded(bool);
        this.mPanelExpanded = bool;
      }
      return;
    }
  }
  
  private void updateQsState()
  {
    this.mQsContainer.setExpanded(this.mQsExpanded);
    this.mNotificationStackScroller.setQsExpanded(this.mQsExpanded);
    NotificationStackScrollLayout localNotificationStackScrollLayout = this.mNotificationStackScroller;
    boolean bool;
    if (this.mStatusBarState != 1) {
      if (this.mQsExpanded) {
        bool = this.mQsExpansionFromOverscroll;
      }
    }
    for (;;)
    {
      localNotificationStackScrollLayout.setScrollingEnabled(bool);
      updateEmptyShadeView();
      if ((this.mKeyguardUserSwitcher != null) && (this.mQsExpanded) && (!this.mStackScrollerOverscrolling)) {
        break;
      }
      return;
      bool = true;
      continue;
      bool = false;
    }
    this.mKeyguardUserSwitcher.hideIfNotSimple(true);
  }
  
  private void updateUnlockIcon()
  {
    boolean bool;
    LockIcon localLockIcon;
    if ((this.mStatusBar.getBarState() == 1) || (this.mStatusBar.getBarState() == 2))
    {
      if (getMaxPanelHeight() - getExpandedHeight() <= this.mUnlockMoveDistance) {
        break label114;
      }
      bool = true;
      localLockIcon = this.mKeyguardBottomArea.getLockIcon();
      if ((bool) && (!this.mUnlockIconActive)) {
        break label119;
      }
      label62:
      if ((!bool) && (this.mUnlockIconActive) && (this.mTracking))
      {
        localLockIcon.setImageAlpha(localLockIcon.getRestingAlpha(), true, 150L, Interpolators.FAST_OUT_LINEAR_IN, null);
        localLockIcon.setImageScale(1.0F, true, 150L, Interpolators.FAST_OUT_LINEAR_IN);
      }
    }
    for (;;)
    {
      this.mUnlockIconActive = bool;
      return;
      label114:
      bool = false;
      break;
      label119:
      if (!this.mTracking) {
        break label62;
      }
      localLockIcon.setImageAlpha(1.0F, true, 150L, Interpolators.FAST_OUT_LINEAR_IN, null);
      localLockIcon.setImageScale(1.2F, true, 150L, Interpolators.FAST_OUT_LINEAR_IN);
    }
  }
  
  public void animateCloseQs()
  {
    if (this.mQsExpansionAnimator != null)
    {
      if (!this.mQsAnimatorExpand) {
        return;
      }
      float f = this.mQsExpansionHeight;
      this.mQsExpansionAnimator.cancel();
      setQsExpansion(f);
    }
    flingSettings(0.0F, false);
  }
  
  public void animateToFullShade(long paramLong)
  {
    this.mAnimateNextTopPaddingChange = true;
    this.mNotificationStackScroller.goToFullShade(paramLong);
    requestLayout();
  }
  
  public boolean canCameraGestureBeLaunched(boolean paramBoolean)
  {
    if (!this.mStatusBar.isCameraAllowedByAdmin())
    {
      EventLog.writeEvent(1397638484, new Object[] { "63787722", Integer.valueOf(-1), "" });
      return false;
    }
    Object localObject = this.mKeyguardBottomArea.resolveCameraIntent();
    if ((localObject == null) || (((ResolveInfo)localObject).activityInfo == null))
    {
      localObject = null;
      Log.d(TAG, "canCameraGestureBeLaunched: packageToLaucn = " + (String)localObject + ", keyguardIsShowing = " + paramBoolean + ", isForegroundApp = " + isForegroundApp((String)localObject) + ", isSwipingInProgress = " + this.mAfforanceHelper.isSwipingInProgress());
      if ((localObject != null) && ((paramBoolean) || (!isForegroundApp((String)localObject)))) {
        break label159;
      }
    }
    label159:
    while (this.mAfforanceHelper.isSwipingInProgress())
    {
      return false;
      localObject = ((ResolveInfo)localObject).activityInfo.packageName;
      break;
    }
    return true;
  }
  
  protected void cancelOverScroll()
  {
    this.mNotificationStackScroller.cancelOverScroll(true);
  }
  
  public void clearNotificationEffects()
  {
    this.mStatusBar.clearNotificationEffects();
  }
  
  public void closeQs()
  {
    cancelQsAnimation();
    setQsExpansion(this.mQsMinExpansionHeight);
  }
  
  public void closeQsDetail()
  {
    this.mQsContainer.getQsPanel().closeDetail();
  }
  
  public int computeMaxKeyguardNotifications(int paramInt)
  {
    float f1 = this.mClockPositionAlgorithm.getMinStackScrollerPadding(getHeight(), this.mKeyguardStatusView.getHeight());
    int m = Math.max(1, getResources().getDimensionPixelSize(2131755461));
    int n = getResources().getDimensionPixelSize(2131755374);
    int i = 0;
    int k = 0;
    int j = 0;
    if (this.mKeyguardBottomArea.getDashView().getVisibility() == 0)
    {
      i = k;
      if ((this.mKeyguardBottomArea.getDashView().getParent() instanceof FrameLayout)) {
        i = ((FrameLayout.LayoutParams)this.mKeyguardBottomArea.getDashView().getLayoutParams()).bottomMargin;
      }
      j = this.mKeyguardBottomArea.getDashView().getMeasuredHeight();
    }
    float f2 = Math.max(this.mKeyguardBottomArea.getIndicationViewHost().getMeasuredHeight(), j + i);
    f1 = this.mNotificationContainerParent.getHeight() - f1 - n - f2;
    j = 0;
    i = 0;
    if (i < this.mNotificationStackScroller.getChildCount())
    {
      ExpandableView localExpandableView = (ExpandableView)this.mNotificationStackScroller.getChildAt(i);
      if (!(localExpandableView instanceof ExpandableNotificationRow))
      {
        k = j;
        f2 = f1;
      }
      for (;;)
      {
        i += 1;
        f1 = f2;
        j = k;
        break;
        ExpandableNotificationRow localExpandableNotificationRow = (ExpandableNotificationRow)localExpandableView;
        f2 = f1;
        k = j;
        if (!this.mGroupManager.isSummaryOfSuppressedGroup(localExpandableNotificationRow.getStatusBarNotification()))
        {
          f2 = f1;
          k = j;
          if (this.mStatusBar.shouldShowOnKeyguard(localExpandableNotificationRow.getStatusBarNotification()))
          {
            f2 = f1;
            k = j;
            if (!localExpandableNotificationRow.isRemoved())
            {
              f2 = f1 - (localExpandableView.getMinHeight() + m);
              if ((f2 < 0.0F) || (j >= paramInt)) {
                break label313;
              }
              k = j + 1;
            }
          }
        }
      }
      label313:
      return j;
    }
    return j;
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    super.dispatchDraw(paramCanvas);
  }
  
  public boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    if (paramAccessibilityEvent.getEventType() == 32)
    {
      paramAccessibilityEvent.getText().add(getKeyguardOrLockScreenString());
      this.mLastAnnouncementWasQuickSettings = false;
      return true;
    }
    return super.dispatchPopulateAccessibilityEventInternal(paramAccessibilityEvent);
  }
  
  public void dumpViewTree(PrintWriter paramPrintWriter)
  {
    String[] arrayOfString = this.setAlphaTrace.toString().split("\\)");
    paramPrintWriter.println(" dump setAlphaTrace start");
    int i = 0;
    while (i < arrayOfString.length)
    {
      paramPrintWriter.println(arrayOfString[i]);
      i += 1;
    }
    paramPrintWriter.println(" dump setAlphaTrace end");
    Log.i(TAG, " dump view tree start");
    debug(1);
    Log.i(TAG, " dump view tree end");
  }
  
  public void expand(boolean paramBoolean)
  {
    expand(paramBoolean, 0);
  }
  
  public void expand(boolean paramBoolean, int paramInt)
  {
    super.expand(paramBoolean, paramInt);
    setListening(true);
  }
  
  public void expandWithQs()
  {
    if (this.mQsExpansionEnabled) {
      this.mQsExpandImmediate = true;
    }
    expand(true);
  }
  
  public void fling(float paramFloat, boolean paramBoolean)
  {
    GestureRecorder localGestureRecorder = ((PhoneStatusBarView)this.mBar).mBar.getGestureRecorder();
    StringBuilder localStringBuilder;
    if (localGestureRecorder != null)
    {
      localStringBuilder = new StringBuilder().append("fling ");
      if (paramFloat <= 0.0F) {
        break label86;
      }
    }
    label86:
    for (String str = "open";; str = "closed")
    {
      localGestureRecorder.tag(str, "notifications,v=" + paramFloat);
      super.fling(paramFloat, paramBoolean);
      return;
    }
  }
  
  protected boolean flingExpands(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    boolean bool = super.flingExpands(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    if (this.mQsExpansionAnimator != null) {
      bool = true;
    }
    return bool;
  }
  
  public void flingSettings(float paramFloat, boolean paramBoolean)
  {
    flingSettings(paramFloat, paramBoolean, null, false);
  }
  
  protected void flingToHeight(float paramFloat1, boolean paramBoolean1, float paramFloat2, float paramFloat3, boolean paramBoolean2)
  {
    boolean bool2 = true;
    HeadsUpTouchHelper localHeadsUpTouchHelper = this.mHeadsUpTouchHelper;
    boolean bool1;
    if (paramBoolean1)
    {
      bool1 = false;
      localHeadsUpTouchHelper.notifyFling(bool1);
      if ((paramBoolean1) || (this.mNotificationStackScroller.getFirstChildIntrinsicHeight() > this.mMaxFadeoutHeight)) {
        break label84;
      }
      if (getFadeoutAlpha() != 1.0F) {
        break label78;
      }
      bool1 = bool2;
    }
    for (;;)
    {
      setClosingWithAlphaFadeout(bool1);
      super.flingToHeight(paramFloat1, paramBoolean1, paramFloat2, paramFloat3, paramBoolean2);
      return;
      bool1 = true;
      break;
      label78:
      bool1 = false;
      continue;
      label84:
      bool1 = false;
    }
  }
  
  public void flingTopOverscroll(float paramFloat, boolean paramBoolean)
  {
    this.mLastOverscroll = 0.0F;
    this.mQsExpansionFromOverscroll = false;
    setQsExpansion(this.mQsExpansionHeight);
    float f = paramFloat;
    if (!this.mQsExpansionEnabled)
    {
      f = paramFloat;
      if (paramBoolean) {
        f = 0.0F;
      }
    }
    if (paramBoolean) {}
    for (paramBoolean = this.mQsExpansionEnabled;; paramBoolean = false)
    {
      flingSettings(f, paramBoolean, new Runnable()
      {
        public void run()
        {
          NotificationPanelView.-set7(NotificationPanelView.this, false);
          NotificationPanelView.-wrap1(NotificationPanelView.this, false);
          NotificationPanelView.-wrap4(NotificationPanelView.this);
        }
      }, false);
      return;
    }
  }
  
  protected boolean fullyExpandedClearAllVisible()
  {
    return (this.mNotificationStackScroller.isDismissViewNotGone()) && (this.mNotificationStackScroller.isScrolledToBottom()) && (!this.mQsExpandImmediate);
  }
  
  public float getAffordanceFalsingFactor()
  {
    if (this.mStatusBar.isWakeUpComingFromTouch()) {
      return 1.5F;
    }
    return 1.0F;
  }
  
  protected float getCannedFlingDurationFactor()
  {
    if (this.mQsExpanded) {
      return 0.7F;
    }
    return 0.6F;
  }
  
  public KeyguardAffordanceView getCenterIcon()
  {
    return this.mKeyguardBottomArea.getLockIcon();
  }
  
  protected int getClearAllHeight()
  {
    return this.mNotificationStackScroller.getDismissViewHeight();
  }
  
  protected float getHeaderTranslation()
  {
    if (this.mStatusBar.getBarState() == 1) {
      return 0.0F;
    }
    return Math.min(0.0F, NotificationUtils.interpolate(-this.mQsMinExpansionHeight, 0.0F, this.mNotificationStackScroller.getAppearFraction(this.mExpandedHeight)));
  }
  
  public KeyguardAffordanceView getLeftIcon()
  {
    if (getLayoutDirection() == 1) {
      return this.mKeyguardBottomArea.getRightView();
    }
    return this.mKeyguardBottomArea.getLeftView();
  }
  
  public View getLeftPreview()
  {
    if (getLayoutDirection() == 1) {
      return this.mKeyguardBottomArea.getRightPreview();
    }
    return this.mKeyguardBottomArea.getLeftPreview();
  }
  
  protected int getMaxPanelHeight()
  {
    int j = this.mStatusBarMinHeight;
    int i = j;
    if (this.mStatusBar.getBarState() != 1)
    {
      i = j;
      if (this.mNotificationStackScroller.getNotGoneChildCount() == 0) {
        i = Math.max(j, (int)(this.mQsMinExpansionHeight + getOverExpansionAmount()));
      }
    }
    if ((this.mQsExpandImmediate) || (this.mQsExpanded) || ((this.mIsExpanding) && (this.mQsExpandedWhenExpandingStarted))) {}
    for (j = calculatePanelHeightQsExpanded();; j = calculatePanelHeightShade()) {
      return Math.max(j, i);
    }
  }
  
  public float getMaxTranslationDistance()
  {
    return (float)Math.hypot(getWidth(), getHeight());
  }
  
  protected float getOverExpansionAmount()
  {
    return this.mNotificationStackScroller.getCurrentOverScrollAmount(true);
  }
  
  protected float getOverExpansionPixels()
  {
    return this.mNotificationStackScroller.getCurrentOverScrolledPixels(true);
  }
  
  protected float getPeekHeight()
  {
    if (this.mNotificationStackScroller.getNotGoneChildCount() > 0) {
      return this.mNotificationStackScroller.getPeekHeight();
    }
    return this.mQsMinExpansionHeight;
  }
  
  protected int getRealMaxPanelHeight()
  {
    if ((this.mQsExpandImmediate) || (this.mQsExpanded) || ((this.mIsExpanding) && (this.mQsExpandedWhenExpandingStarted))) {
      return calculatePanelHeightQsExpanded();
    }
    return calculatePanelHeightShade();
  }
  
  public KeyguardAffordanceView getRightIcon()
  {
    if (getLayoutDirection() == 1) {
      return this.mKeyguardBottomArea.getLeftView();
    }
    return this.mKeyguardBottomArea.getRightView();
  }
  
  public View getRightPreview()
  {
    if (getLayoutDirection() == 1) {
      return this.mKeyguardBottomArea.getLeftPreview();
    }
    return this.mKeyguardBottomArea.getRightPreview();
  }
  
  protected boolean hasConflictingGestures()
  {
    boolean bool = false;
    if (this.mStatusBar.getBarState() != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasNotification()
  {
    boolean bool = false;
    if (this.mNotificationStackScroller.getNotGoneChildCount() > 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasOverlappingRendering()
  {
    return !this.mDozing;
  }
  
  protected boolean isClearAllVisible()
  {
    return this.mNotificationStackScroller.isDismissViewVisible();
  }
  
  public boolean isClosingWithAlphaFadeOut()
  {
    return this.mClosingWithAlphaFadeOut;
  }
  
  public boolean isDozing()
  {
    return this.mDozing;
  }
  
  public boolean isExpanding()
  {
    return this.mIsExpanding;
  }
  
  protected boolean isInContentBounds(float paramFloat1, float paramFloat2)
  {
    boolean bool2 = false;
    float f = this.mNotificationStackScroller.getX();
    boolean bool1 = bool2;
    if (!this.mNotificationStackScroller.isBelowLastNotification(paramFloat1 - f, paramFloat2))
    {
      bool1 = bool2;
      if (f < paramFloat1)
      {
        bool1 = bool2;
        if (paramFloat1 < this.mNotificationStackScroller.getWidth() + f) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public boolean isInSettings()
  {
    return this.mQsExpanded;
  }
  
  public boolean isLaunchTransitionFinished()
  {
    return this.mIsLaunchTransitionFinished;
  }
  
  public boolean isLaunchTransitionRunning()
  {
    return this.mIsLaunchTransitionRunning;
  }
  
  protected boolean isOverScrollRunning()
  {
    return this.mNotificationStackScroller.isOverScrollRunning(true);
  }
  
  protected boolean isPanelVisibleBecauseOfHeadsUp()
  {
    if (!this.mHeadsUpManager.hasPinnedHeadsUp()) {
      return this.mHeadsUpAnimatingAway;
    }
    return true;
  }
  
  public boolean isQsDetailShowing()
  {
    return this.mQsContainer.isShowingDetail();
  }
  
  public boolean isQsExpanded()
  {
    return this.mQsExpanded;
  }
  
  protected boolean isScrolledToBottom()
  {
    boolean bool = true;
    if (!isInSettings())
    {
      if (this.mStatusBar.getBarState() != 1) {
        bool = this.mNotificationStackScroller.isScrolledToBottom();
      }
      return bool;
    }
    return true;
  }
  
  protected boolean isTrackingBlocked()
  {
    if (this.mConflictingQsExpansionGesture) {
      return this.mQsExpanded;
    }
    return false;
  }
  
  public void launchCamera(boolean paramBoolean, int paramInt)
  {
    boolean bool = true;
    Log.d(TAG, "launchCamera, " + paramBoolean + ", " + paramInt + ", " + Debug.getCallers(3));
    mLastCameraGestureLaunchSource = paramInt;
    label85:
    KeyguardAffordanceHelper localKeyguardAffordanceHelper;
    if (paramInt == 1)
    {
      this.mLastCameraLaunchSource = "power_double_tap";
      if (isFullyCollapsed()) {
        break label183;
      }
      this.mLaunchingAffordance = true;
      setLaunchingAffordance(true);
      this.mHandler.removeCallbacks(this.mUpdateCameraStateTimeout);
      KeyguardUpdateMonitor.getInstance(this.mContext).updateLaunchingCameraState(true);
      this.mHandler.postDelayed(this.mUpdateCameraStateTimeout, 2000L);
      this.mStatusBar.forceHideBouncer();
      this.mStatusBar.notifyCameraLaunching(null);
      localKeyguardAffordanceHelper = this.mAfforanceHelper;
      if (getLayoutDirection() != 1) {
        break label188;
      }
    }
    for (;;)
    {
      localKeyguardAffordanceHelper.launchAffordance(paramBoolean, bool);
      return;
      if (paramInt == 0)
      {
        this.mLastCameraLaunchSource = "wiggle_gesture";
        break;
      }
      this.mLastCameraLaunchSource = "lockscreen_affordance";
      break;
      label183:
      paramBoolean = false;
      break label85;
      label188:
      bool = false;
    }
  }
  
  protected void loadDimens()
  {
    super.loadDimens();
    this.mFlingAnimationUtils = new FlingAnimationUtils(getContext(), 0.4F);
    this.mStatusBarMinHeight = getResources().getDimensionPixelSize(17104921);
    this.mQsPeekHeight = getResources().getDimensionPixelSize(2131755449);
    this.mNotificationsHeaderCollideDistance = getResources().getDimensionPixelSize(2131755477);
    this.mUnlockMoveDistance = getResources().getDimensionPixelOffset(2131755478);
    this.mClockPositionAlgorithm.loadDimens(getResources());
    this.mNotificationScrimWaitDistance = getResources().getDimensionPixelSize(2131755479);
    this.mQsFalsingThreshold = getResources().getDimensionPixelSize(2131755467);
    this.mPositionMinSideMargin = getResources().getDimensionPixelSize(2131755553);
    this.mMaxFadeoutHeight = getResources().getDimensionPixelSize(2131755368);
  }
  
  public boolean needsAntiFalsing()
  {
    return this.mStatusBarState == 1;
  }
  
  public void onAffordanceLaunchEnded()
  {
    this.mLaunchingAffordance = false;
    setLaunchingAffordance(false);
  }
  
  public void onAnimationToSideEnded()
  {
    if (Utils.DEBUG_ONEPLUS) {
      Log.d(TAG, "onAnimationToSideEnded");
    }
    this.mIsLaunchTransitionRunning = false;
    this.mIsLaunchTransitionFinished = true;
    if (this.mLaunchAnimationEndRunnable != null)
    {
      this.mLaunchAnimationEndRunnable.run();
      this.mLaunchAnimationEndRunnable = null;
    }
  }
  
  public void onAnimationToSideStarted(boolean paramBoolean, float paramFloat1, float paramFloat2)
  {
    int i;
    int j;
    if (getLayoutDirection() == 1)
    {
      this.mIsLaunchTransitionRunning = true;
      this.mLaunchAnimationEndRunnable = null;
      float f = this.mStatusBar.getDisplayDensity();
      i = Math.abs((int)(paramFloat1 / f));
      j = Math.abs((int)(paramFloat2 / f));
      if (!paramBoolean) {
        break label132;
      }
      EventLogTags.writeSysuiLockscreenGesture(5, i, j);
      this.mFalsingManager.onLeftAffordanceOn();
      if (!this.mFalsingManager.shouldEnforceBouncer()) {
        break label122;
      }
      this.mStatusBar.executeRunnableDismissingKeyguard(new Runnable()
      {
        public void run()
        {
          NotificationPanelView.this.mKeyguardBottomArea.launchLeftAffordance();
        }
      }, null, true, false, true);
    }
    for (;;)
    {
      this.mStatusBar.startLaunchTransitionTimeout();
      this.mBlockTouches = true;
      return;
      if (paramBoolean)
      {
        paramBoolean = false;
        break;
      }
      paramBoolean = true;
      break;
      label122:
      this.mKeyguardBottomArea.launchLeftAffordance();
      continue;
      label132:
      if ("lockscreen_affordance".equals(this.mLastCameraLaunchSource))
      {
        this.mHandler.removeCallbacks(this.mUpdateCameraStateTimeout);
        KeyguardUpdateMonitor.getInstance(this.mContext).updateLaunchingCameraState(true);
        this.mHandler.postDelayed(this.mUpdateCameraStateTimeout, 2000L);
        EventLogTags.writeSysuiLockscreenGesture(4, i, j);
        MdmLogger.log("lock_camera", "", "1");
      }
      this.mFalsingManager.onCameraOn();
      this.mStatusBar.notifyCameraLaunching(null);
      if (this.mFalsingManager.shouldEnforceBouncer()) {
        this.mStatusBar.executeRunnableDismissingKeyguard(new Runnable()
        {
          public void run()
          {
            NotificationPanelView.this.mKeyguardBottomArea.launchCamera(NotificationPanelView.-get7(NotificationPanelView.this));
          }
        }, null, true, false, true);
      } else {
        this.mKeyguardBottomArea.launchCamera(this.mLastCameraLaunchSource);
      }
    }
  }
  
  public WindowInsets onApplyWindowInsets(WindowInsets paramWindowInsets)
  {
    this.mNavigationBarBottomHeight = paramWindowInsets.getStableInsetBottom();
    updateMaxHeadsUpTranslation();
    return paramWindowInsets;
  }
  
  public void onClick(View paramView)
  {
    if (paramView.getId() == 2131952174)
    {
      onQsExpansionStarted();
      if (!this.mQsExpanded) {
        break label30;
      }
      flingSettings(0.0F, false, null, true);
    }
    label30:
    while (!this.mQsExpansionEnabled) {
      return;
    }
    EventLogTags.writeSysuiLockscreenGesture(10, 0, 0);
    flingSettings(0.0F, true, null, true);
  }
  
  protected void onClosingFinished()
  {
    super.onClosingFinished();
    resetVerticalPanelPosition();
    setClosingWithAlphaFadeout(false);
    this.mQsContainer.getCustomizer().hide(0, 0);
    this.mQsContainer.setShadow(true);
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    this.mAfforanceHelper.onConfigurationChanged();
    if (paramConfiguration.orientation != this.mLastOrientation) {
      resetVerticalPanelPosition();
    }
    this.mLastOrientation = paramConfiguration.orientation;
  }
  
  public boolean onDoubleTap(MotionEvent paramMotionEvent)
  {
    if ((this.mNotificationStackScroller.getVisibility() != 0) || (this.mStatusBar.getBarState() != 1) || (this.mStatusBar.isBouncerShowing())) {}
    while ((LSState.getInstance().getFingerprintUnlockControl().isFingerprintAuthenticating()) || (LSState.getInstance().getPreventModeCtrl().isPreventModeActive())) {
      return false;
    }
    Log.d(TAG, "onDoubleTap to sleep");
    ((PowerManager)getContext().getSystemService("power")).goToSleep(SystemClock.uptimeMillis());
    return true;
  }
  
  public boolean onDoubleTapEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  public void onEmptySpaceClicked(float paramFloat1, float paramFloat2)
  {
    onEmptySpaceClick(paramFloat1);
  }
  
  protected void onExpandingFinished()
  {
    super.onExpandingFinished();
    this.mNotificationStackScroller.onExpansionStopped();
    this.mHeadsUpManager.onExpandingFinished();
    this.mIsExpanding = false;
    if (isFullyCollapsed())
    {
      DejankUtils.postAfterTraversal(new Runnable()
      {
        public void run()
        {
          NotificationPanelView.-wrap0(NotificationPanelView.this, false);
        }
      });
      postOnAnimation(new Runnable()
      {
        public void run()
        {
          NotificationPanelView.this.getParent().invalidateChild(NotificationPanelView.this, NotificationPanelView.-get4());
        }
      });
    }
    for (;;)
    {
      this.mQsExpandImmediate = false;
      this.mTwoFingerQsExpandPossible = false;
      this.mIsExpansionFromHeadsUp = false;
      this.mNotificationStackScroller.setTrackingHeadsUp(false);
      this.mExpandingFromHeadsUp = false;
      setPanelScrimMinFraction(0.0F);
      return;
      setListening(true);
    }
  }
  
  protected void onExpandingStarted()
  {
    super.onExpandingStarted();
    this.mNotificationStackScroller.onExpansionStarted();
    this.mIsExpanding = true;
    this.mQsExpandedWhenExpandingStarted = this.mQsFullyExpanded;
    if (this.mQsExpanded) {
      onQsExpansionStarted();
    }
    this.mQsContainer.setHeaderListening(true);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mKeyguardStatusBar = ((KeyguardStatusBarView)findViewById(2131951918));
    this.mKeyguardStatusView = ((KeyguardStatusView)findViewById(2131951924));
    this.mClockView = ((TextView)findViewById(2131951787));
    this.mNotificationContainerParent = ((NotificationsQuickSettingsContainer)findViewById(2131952275));
    this.mNotificationStackScroller = ((NotificationStackScrollLayout)findViewById(2131952277));
    this.mNotificationStackScroller.setOnHeightChangedListener(this);
    this.mNotificationStackScroller.setOverscrollTopChangedListener(this);
    this.mNotificationStackScroller.setOnEmptySpaceClickListener(this);
    this.mKeyguardBottomArea = ((KeyguardBottomAreaView)findViewById(2131951849));
    this.mAfforanceHelper = new KeyguardAffordanceHelper(this, getContext());
    this.mKeyguardBottomArea.setAffordanceHelper(this.mAfforanceHelper);
    this.mLastOrientation = getResources().getConfiguration().orientation;
    this.mQsAutoReinflateContainer = ((AutoReinflateContainer)findViewById(2131952276));
    this.mQsAutoReinflateContainer.addInflateListener(new AutoReinflateContainer.InflateListener()
    {
      public void onInflated(View paramAnonymousView)
      {
        NotificationPanelView.this.mQsContainer = ((QSContainer)paramAnonymousView.findViewById(2131952156));
        NotificationPanelView.this.mQsContainer.setPanelView(NotificationPanelView.this);
        NotificationPanelView.this.mQsContainer.getHeader().findViewById(2131952174).setOnClickListener(NotificationPanelView.this);
        NotificationPanelView.this.mQsContainer.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
        {
          public void onLayoutChange(View paramAnonymous2View, int paramAnonymous2Int1, int paramAnonymous2Int2, int paramAnonymous2Int3, int paramAnonymous2Int4, int paramAnonymous2Int5, int paramAnonymous2Int6, int paramAnonymous2Int7, int paramAnonymous2Int8)
          {
            if (paramAnonymous2Int4 - paramAnonymous2Int2 != paramAnonymous2Int8 - paramAnonymous2Int6) {
              NotificationPanelView.this.onQsHeightChanged();
            }
          }
        });
        NotificationPanelView.this.mNotificationStackScroller.setQsContainer(NotificationPanelView.this.mQsContainer);
      }
    });
    this.mHandler = new Handler();
  }
  
  public void onHeadsUpPinned(ExpandableNotificationRow paramExpandableNotificationRow)
  {
    this.mNotificationStackScroller.generateHeadsUpAnimation(paramExpandableNotificationRow, true);
  }
  
  public void onHeadsUpPinnedModeChanged(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mHeadsUpExistenceChangedRunnable.run();
      updateNotificationTranslucency();
      return;
    }
    this.mHeadsUpAnimatingAway = true;
    this.mNotificationStackScroller.runAfterAnimationFinished(this.mHeadsUpExistenceChangedRunnable);
  }
  
  public void onHeadsUpStateChanged(NotificationData.Entry paramEntry, boolean paramBoolean)
  {
    this.mNotificationStackScroller.generateHeadsUpAnimation(paramEntry.row, paramBoolean);
  }
  
  public void onHeadsUpUnPinned(ExpandableNotificationRow paramExpandableNotificationRow) {}
  
  public void onHeightChanged(ExpandableView paramExpandableView, boolean paramBoolean)
  {
    ExpandableNotificationRow localExpandableNotificationRow = null;
    if ((paramExpandableView == null) && (this.mQsExpanded)) {
      return;
    }
    ExpandableView localExpandableView = this.mNotificationStackScroller.getFirstChildNotGone();
    if ((localExpandableView instanceof ExpandableNotificationRow)) {
      localExpandableNotificationRow = (ExpandableNotificationRow)localExpandableView;
    }
    if ((localExpandableNotificationRow != null) && ((paramExpandableView == localExpandableNotificationRow) || (localExpandableNotificationRow.getNotificationParent() == localExpandableNotificationRow))) {
      requestScrollerTopPaddingUpdate(false);
    }
    requestPanelHeightUpdate();
  }
  
  protected void onHeightUpdated(float paramFloat)
  {
    if ((!this.mQsExpanded) || (this.mQsExpandImmediate) || ((this.mIsExpanding) && (this.mQsExpandedWhenExpandingStarted))) {
      positionClockAndNotifications();
    }
    NotificationStackScrollLayout localNotificationStackScrollLayout;
    if (!this.mQsExpandImmediate) {
      if ((!this.mQsExpanded) || (this.mQsTracking))
      {
        updateExpandedHeight(paramFloat);
        updateHeader();
        updateUnlockIcon();
        updateNotificationTranslucency();
        updatePanelExpanded();
        localNotificationStackScrollLayout = this.mNotificationStackScroller;
        if (!isFullyCollapsed()) {
          break label182;
        }
      }
    }
    label182:
    for (boolean bool = false;; bool = true)
    {
      localNotificationStackScrollLayout.setShadeExpanded(bool);
      return;
      if ((this.mQsExpansionAnimator != null) || (this.mQsExpansionFromOverscroll)) {
        break;
      }
      if (this.mKeyguardShowing) {}
      for (float f = paramFloat / getMaxPanelHeight();; f = (paramFloat - f) / (calculatePanelHeightQsExpanded() - f))
      {
        setQsExpansion(this.mQsMinExpansionHeight + (getTempQsMaxExpansion() - this.mQsMinExpansionHeight) * f);
        break;
        f = this.mNotificationStackScroller.getIntrinsicPadding() + this.mNotificationStackScroller.getLayoutMinHeight();
      }
    }
  }
  
  public void onIconClicked(boolean paramBoolean)
  {
    if (this.mHintAnimationRunning) {
      return;
    }
    this.mHintAnimationRunning = true;
    this.mAfforanceHelper.startHintAnimation(paramBoolean, new Runnable()
    {
      public void run()
      {
        NotificationPanelView.this.mHintAnimationRunning = false;
        NotificationPanelView.this.mStatusBar.onHintFinished();
      }
    });
    boolean bool = paramBoolean;
    if (getLayoutDirection() == 1) {
      if (!paramBoolean) {
        break label57;
      }
    }
    label57:
    for (bool = false; bool; bool = true)
    {
      this.mStatusBar.onCameraHintStarted();
      return;
    }
    if (this.mKeyguardBottomArea.isLeftVoiceAssist())
    {
      this.mStatusBar.onVoiceAssistHintStarted();
      return;
    }
    this.mStatusBar.onPhoneHintStarted();
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mBlockTouches) || (this.mQsContainer.isCustomizing())) {
      return false;
    }
    initDownStates(paramMotionEvent);
    if (this.mHeadsUpTouchHelper.onInterceptTouchEvent(paramMotionEvent))
    {
      this.mIsExpansionFromHeadsUp = true;
      MetricsLogger.count(this.mContext, "panel_open", 1);
      MetricsLogger.count(this.mContext, "panel_open_peek", 1);
      return true;
    }
    if ((!isFullyCollapsed()) && (onQsIntercept(paramMotionEvent))) {
      return true;
    }
    return super.onInterceptTouchEvent(paramMotionEvent);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.mKeyguardStatusView.setPivotX(getWidth() / 2);
    this.mKeyguardStatusView.setPivotY(this.mClockView.getTextSize() * 0.34521484F);
    paramInt2 = this.mQsMaxExpansionHeight;
    if (this.mKeyguardShowing)
    {
      paramInt1 = 0;
      this.mQsMinExpansionHeight = paramInt1;
      this.mQsMaxExpansionHeight = this.mQsContainer.getDesiredHeight();
      positionClockAndNotifications();
      if ((!this.mQsExpanded) || (!this.mQsFullyExpanded)) {
        break label175;
      }
      this.mQsExpansionHeight = this.mQsMaxExpansionHeight;
      requestScrollerTopPaddingUpdate(false);
      requestPanelHeightUpdate();
      if (this.mQsMaxExpansionHeight != paramInt2) {
        startQsSizeChangeAnimation(paramInt2, this.mQsMaxExpansionHeight);
      }
    }
    for (;;)
    {
      updateExpandedHeight(getExpandedHeight());
      updateHeader();
      if (this.mQsSizeChangeAnimator == null) {
        this.mQsContainer.setHeightOverride(this.mQsContainer.getDesiredHeight());
      }
      updateMaxHeadsUpTranslation();
      return;
      paramInt1 = this.mQsContainer.getQsMinExpansionHeight();
      break;
      label175:
      if (!this.mQsExpanded) {
        setQsExpansion(this.mQsMinExpansionHeight + this.mLastOverscroll);
      }
    }
  }
  
  protected boolean onMiddleClicked()
  {
    switch (this.mStatusBar.getBarState())
    {
    default: 
      return true;
    case 1: 
      if (!this.mDozingOnDown)
      {
        EventLogTags.writeSysuiLockscreenGesture(3, 0, 0);
        startUnlockHintAnimation();
      }
      return true;
    case 2: 
      if (!this.mQsExpanded) {
        this.mStatusBar.goToKeyguard();
      }
      return true;
    }
    post(this.mPostCollapseRunnable);
    return false;
  }
  
  public void onOverscrollTopChanged(float paramFloat, boolean paramBoolean)
  {
    boolean bool = false;
    cancelQsAnimation();
    if (!this.mQsExpansionEnabled) {
      paramFloat = 0.0F;
    }
    if (paramFloat >= 1.0F) {
      if (paramFloat == 0.0F) {
        break label73;
      }
    }
    for (;;)
    {
      setOverScrolling(paramBoolean);
      paramBoolean = bool;
      if (paramFloat != 0.0F) {
        paramBoolean = true;
      }
      this.mQsExpansionFromOverscroll = paramBoolean;
      this.mLastOverscroll = paramFloat;
      updateQsState();
      setQsExpansion(this.mQsMinExpansionHeight + paramFloat);
      return;
      paramFloat = 0.0F;
      break;
      label73:
      paramBoolean = false;
    }
  }
  
  public void onQsHeightChanged()
  {
    this.mQsMaxExpansionHeight = this.mQsContainer.getDesiredHeight();
    if ((this.mQsExpanded) && (this.mQsFullyExpanded))
    {
      this.mQsExpansionHeight = this.mQsMaxExpansionHeight;
      requestScrollerTopPaddingUpdate(false);
      requestPanelHeightUpdate();
    }
  }
  
  public void onReset(ExpandableView paramExpandableView) {}
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    if (paramInt != this.mOldLayoutDirection)
    {
      this.mAfforanceHelper.onRtlPropertiesChanged();
      this.mOldLayoutDirection = paramInt;
    }
  }
  
  public void onScreenTurningOn()
  {
    this.mKeyguardStatusView.refreshTime();
  }
  
  public boolean onSingleTapConfirmed(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  public void onSwipingAborted()
  {
    this.mFalsingManager.onAffordanceSwipingAborted();
    this.mKeyguardBottomArea.unbindCameraPrewarmService(false);
  }
  
  public void onSwipingStarted(boolean paramBoolean)
  {
    this.mFalsingManager.onAffordanceSwipingStarted(paramBoolean);
    if (getLayoutDirection() == 1) {
      if (paramBoolean) {
        paramBoolean = false;
      }
    }
    for (;;)
    {
      if (paramBoolean) {
        this.mKeyguardBottomArea.bindCameraPrewarmService();
      }
      requestDisallowInterceptTouchEvent(true);
      this.mOnlyAffordanceInThisMotion = true;
      this.mQsTracking = false;
      return;
      paramBoolean = true;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mBlockTouches) || (this.mQsContainer.isCustomizing()))
    {
      Log.d(TAG, "onTouchEvent, block touch, set mTracking to false:mBlockTouches=" + this.mBlockTouches + ", mQsContainer.isCustomizing()=" + this.mQsContainer.isCustomizing());
      this.mTracking = false;
      return false;
    }
    initDownStates(paramMotionEvent);
    if ((!this.mListenForHeadsUp) || (this.mHeadsUpTouchHelper.isTrackingHeadsUp())) {
      if (((!this.mIsExpanding) || (this.mHintAnimationRunning)) && (!this.mQsExpanded)) {
        break label152;
      }
    }
    for (;;)
    {
      if (!this.mOnlyAffordanceInThisMotion) {
        break label174;
      }
      return true;
      if (!this.mHeadsUpTouchHelper.onInterceptTouchEvent(paramMotionEvent)) {
        break;
      }
      this.mIsExpansionFromHeadsUp = true;
      MetricsLogger.count(this.mContext, "panel_open_peek", 1);
      break;
      label152:
      if (this.mStatusBar.getBarState() != 0) {
        this.mAfforanceHelper.onTouchEvent(paramMotionEvent);
      }
    }
    label174:
    this.mHeadsUpTouchHelper.onTouchEvent(paramMotionEvent);
    if (this.mStatusBar.isHighlightHintViewShowing()) {
      onHightlightHintIntercept(paramMotionEvent);
    }
    if ((!this.mHeadsUpTouchHelper.isTrackingHeadsUp()) && (handleQsTouch(paramMotionEvent))) {
      return true;
    }
    if ((paramMotionEvent.getActionMasked() == 0) && (isFullyCollapsed()))
    {
      MetricsLogger.count(this.mContext, "panel_open", 1);
      updateVerticalPanelPosition(paramMotionEvent.getX());
    }
    super.onTouchEvent(paramMotionEvent);
    return true;
  }
  
  protected void onTrackingStarted()
  {
    this.mFalsingManager.onTrackingStarted();
    super.onTrackingStarted();
    if (this.mQsFullyExpanded) {
      this.mQsExpandImmediate = true;
    }
    if ((this.mStatusBar.getBarState() == 1) || (this.mStatusBar.getBarState() == 2)) {
      this.mAfforanceHelper.animateHideLeftRightIcon();
    }
    this.mNotificationStackScroller.onPanelTrackingStarted();
  }
  
  protected void onTrackingStopped(boolean paramBoolean)
  {
    this.mFalsingManager.onTrackingStopped();
    super.onTrackingStopped(paramBoolean);
    if (paramBoolean) {
      this.mNotificationStackScroller.setOverScrolledPixels(0.0F, true, true);
    }
    this.mNotificationStackScroller.onPanelTrackingStopped();
    if ((paramBoolean) && ((this.mStatusBar.getBarState() == 1) || (this.mStatusBar.getBarState() == 2)) && (!this.mHintAnimationRunning)) {
      this.mAfforanceHelper.reset(true);
    }
    if ((!paramBoolean) && ((this.mStatusBar.getBarState() == 1) || (this.mStatusBar.getBarState() == 2)))
    {
      LockIcon localLockIcon = this.mKeyguardBottomArea.getLockIcon();
      localLockIcon.setImageAlpha(0.0F, true, 100L, Interpolators.FAST_OUT_LINEAR_IN, null);
      localLockIcon.setImageScale(2.0F, true, 100L, Interpolators.FAST_OUT_LINEAR_IN);
    }
  }
  
  protected void requestScrollerTopPaddingUpdate(boolean paramBoolean)
  {
    boolean bool2 = true;
    NotificationStackScrollLayout localNotificationStackScrollLayout = this.mNotificationStackScroller;
    float f = calculateQsTopPadding();
    boolean bool1;
    if (!this.mAnimateNextTopPaddingChange)
    {
      bool1 = paramBoolean;
      if (!this.mKeyguardShowing) {
        break label76;
      }
      paramBoolean = bool2;
      if (!this.mQsExpandImmediate)
      {
        if (!this.mIsExpanding) {
          break label71;
        }
        paramBoolean = this.mQsExpandedWhenExpandingStarted;
      }
    }
    for (;;)
    {
      localNotificationStackScrollLayout.updateTopPadding(f, bool1, paramBoolean);
      this.mAnimateNextTopPaddingChange = false;
      return;
      bool1 = true;
      break;
      label71:
      paramBoolean = false;
      continue;
      label76:
      paramBoolean = false;
    }
  }
  
  public void resetViews()
  {
    this.mIsLaunchTransitionFinished = false;
    this.mBlockTouches = false;
    this.mUnlockIconActive = false;
    if (!this.mLaunchingAffordance)
    {
      this.mAfforanceHelper.reset(false);
      this.mLastCameraLaunchSource = "lockscreen_affordance";
    }
    closeQs();
    this.mStatusBar.dismissPopups();
    this.mNotificationStackScroller.setOverScrollAmount(0.0F, true, false, true);
    this.mNotificationStackScroller.resetScrollPosition();
  }
  
  public void setAlpha(float paramFloat)
  {
    boolean bool = false;
    super.setAlpha(paramFloat);
    this.setAlphaTrace.delete(0, this.setAlphaTrace.length());
    Object localObject1 = Thread.currentThread().getStackTrace();
    int j = localObject1.length;
    int i = 0;
    while (i < j)
    {
      Object localObject2 = localObject1[i];
      this.setAlphaTrace.append(((StackTraceElement)localObject2).toString());
      i += 1;
    }
    localObject1 = this.mNotificationStackScroller;
    if (paramFloat != 1.0F) {
      bool = true;
    }
    ((NotificationStackScrollLayout)localObject1).setParentFadingOut(bool);
  }
  
  public void setBarState(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = this.mStatusBarState;
    boolean bool;
    long l;
    if (paramInt == 1)
    {
      bool = true;
      setKeyguardStatusViewVisibility(paramInt, paramBoolean1, paramBoolean2);
      setKeyguardBottomAreaVisibility(paramInt, paramBoolean2);
      this.mStatusBarState = paramInt;
      this.mKeyguardShowing = bool;
      this.mQsContainer.setKeyguardShowing(this.mKeyguardShowing);
      if ((i != 1) || ((!paramBoolean2) && (paramInt != 2))) {
        break label125;
      }
      animateKeyguardStatusBarOut();
      if (this.mStatusBarState != 2) {
        break label113;
      }
      l = 0L;
      label79:
      this.mQsContainer.animateHeaderSlidingIn(l);
    }
    for (;;)
    {
      if (bool) {
        updateDozingVisibilities(false);
      }
      resetVerticalPanelPosition();
      updateQsState();
      return;
      bool = false;
      break;
      label113:
      l = this.mStatusBar.calculateGoingToFullShadeDelay();
      break label79;
      label125:
      if ((i != 2) || (paramInt != 1)) {
        break label153;
      }
      animateKeyguardStatusBarIn(360L);
      this.mQsContainer.animateHeaderSlidingOut();
    }
    label153:
    this.mKeyguardStatusBar.setAlpha(1.0F);
    KeyguardStatusBarView localKeyguardStatusBarView = this.mKeyguardStatusBar;
    if (bool) {}
    for (paramInt = 0;; paramInt = 4)
    {
      localKeyguardStatusBarView.setVisibility(paramInt);
      if ((!bool) || (i == this.mStatusBarState)) {
        break;
      }
      this.mKeyguardBottomArea.onKeyguardShowingChanged();
      this.mQsContainer.hideImmediately();
      break;
    }
  }
  
  public void setDozing(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1 == this.mDozing) {
      return;
    }
    this.mDozing = paramBoolean1;
    if (this.mStatusBarState == 1) {
      updateDozingVisibilities(paramBoolean2);
    }
  }
  
  public void setEmptyDragAmount(float paramFloat)
  {
    float f = 0.8F;
    if (this.mNotificationStackScroller.getNotGoneChildCount() > 0) {
      f = 0.4F;
    }
    for (;;)
    {
      this.mEmptyDragAmount = (paramFloat * f);
      positionClockAndNotifications();
      return;
      if (!this.mStatusBar.hasActiveNotifications()) {
        f = 0.4F;
      }
    }
  }
  
  public void setGroupManager(NotificationGroupManager paramNotificationGroupManager)
  {
    this.mGroupManager = paramNotificationGroupManager;
  }
  
  public void setHeadsUpManager(HeadsUpManager paramHeadsUpManager)
  {
    super.setHeadsUpManager(paramHeadsUpManager);
    this.mHeadsUpTouchHelper = new HeadsUpTouchHelper(paramHeadsUpManager, this.mNotificationStackScroller, this);
  }
  
  public void setKeyguardUserSwitcher(KeyguardUserSwitcher paramKeyguardUserSwitcher)
  {
    this.mKeyguardUserSwitcher = paramKeyguardUserSwitcher;
  }
  
  public void setLaunchTransitionEndRunnable(Runnable paramRunnable)
  {
    this.mLaunchAnimationEndRunnable = paramRunnable;
  }
  
  protected void setOverExpansion(float paramFloat, boolean paramBoolean)
  {
    if ((this.mConflictingQsExpansionGesture) || (this.mQsExpandImmediate)) {
      return;
    }
    if (this.mStatusBar.getBarState() != 1)
    {
      this.mNotificationStackScroller.setOnHeightChangedListener(null);
      if (!paramBoolean) {
        break label57;
      }
      this.mNotificationStackScroller.setOverScrolledPixels(paramFloat, true, false);
    }
    for (;;)
    {
      this.mNotificationStackScroller.setOnHeightChangedListener(this);
      return;
      label57:
      this.mNotificationStackScroller.setOverScrollAmount(paramFloat, true, false);
    }
  }
  
  public void setPanelScrimMinFraction(float paramFloat)
  {
    this.mBar.panelScrimMinFractionChanged(paramFloat);
  }
  
  public void setQsExpansionEnabled(boolean paramBoolean)
  {
    this.mQsExpansionEnabled = paramBoolean;
    this.mQsContainer.setHeaderClickable(paramBoolean);
  }
  
  public void setQsScrimEnabled(boolean paramBoolean)
  {
    if (this.mQsScrimEnabled != paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      this.mQsScrimEnabled = paramBoolean;
      if (i != 0) {
        updateQsState();
      }
      return;
    }
  }
  
  public void setShadeEmpty(boolean paramBoolean)
  {
    this.mShadeEmpty = paramBoolean;
    updateEmptyShadeView();
  }
  
  public void setStatusBar(PhoneStatusBar paramPhoneStatusBar)
  {
    this.mStatusBar = paramPhoneStatusBar;
  }
  
  public void setTrackingHeadsUp(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mNotificationStackScroller.setTrackingHeadsUp(true);
      this.mExpandingFromHeadsUp = true;
    }
  }
  
  protected void setVerticalPanelTranslation(float paramFloat)
  {
    this.mNotificationStackScroller.setTranslationX(paramFloat);
    this.mQsAutoReinflateContainer.setTranslationX(paramFloat);
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return true;
  }
  
  protected boolean shouldGestureIgnoreXTouchSlop(float paramFloat1, float paramFloat2)
  {
    return !this.mAfforanceHelper.isOnAffordanceIcon(paramFloat1, paramFloat2);
  }
  
  protected void startUnlockHintAnimation()
  {
    super.startUnlockHintAnimation();
    startHighlightIconAnimation(getCenterIcon());
  }
  
  protected void updateExpandedHeight(float paramFloat)
  {
    this.mNotificationStackScroller.setExpandedHeight(paramFloat);
    updateKeyguardBottomAreaAlpha();
  }
  
  protected void updateQsExpansion()
  {
    this.mQsContainer.setQsExpansion(getQsExpansionFraction(), getHeaderTranslation());
  }
  
  public void updateResources()
  {
    int i = getResources().getDimensionPixelSize(2131755401);
    int j = getResources().getInteger(2131624023);
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.mQsAutoReinflateContainer.getLayoutParams();
    if (localLayoutParams.width != i)
    {
      localLayoutParams.width = i;
      localLayoutParams.gravity = j;
      this.mQsAutoReinflateContainer.setLayoutParams(localLayoutParams);
      this.mQsContainer.post(this.mUpdateHeader);
    }
    localLayoutParams = (FrameLayout.LayoutParams)this.mNotificationStackScroller.getLayoutParams();
    if (localLayoutParams.width != i)
    {
      localLayoutParams.width = i;
      localLayoutParams.gravity = j;
      this.mNotificationStackScroller.setLayoutParams(localLayoutParams);
    }
  }
  
  protected void updateVerticalPanelPosition(float paramFloat)
  {
    resetVerticalPanelPosition();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\NotificationPanelView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */