package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.systemui.Interpolators;
import com.android.systemui.classifier.FalsingManager;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.statusbar.KeyguardAffordanceView;

public class KeyguardAffordanceHelper
{
  private Runnable mAnimationEndRunnable = new Runnable()
  {
    public void run()
    {
      KeyguardAffordanceHelper.-get0(KeyguardAffordanceHelper.this).onAnimationToSideEnded();
    }
  };
  private final Callback mCallback;
  private KeyguardAffordanceView mCenterIcon;
  private final Context mContext;
  private FalsingManager mFalsingManager;
  private FlingAnimationUtils mFlingAnimationUtils;
  private AnimatorListenerAdapter mFlingEndListener = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      KeyguardAffordanceHelper.-set0(KeyguardAffordanceHelper.this, null);
      KeyguardAffordanceHelper.-set1(KeyguardAffordanceHelper.this, false);
      KeyguardAffordanceHelper.-set2(KeyguardAffordanceHelper.this, null);
    }
  };
  private int mHintGrowAmount;
  private float mInitialTouchX;
  private float mInitialTouchY;
  private KeyguardAffordanceView mLeftIcon;
  private int mMinBackgroundRadius;
  private int mMinFlingVelocity;
  private int mMinTranslationAmount;
  private boolean mMotionCancelled;
  private KeyguardAffordanceView mRightIcon;
  private Animator mSwipeAnimator;
  private boolean mSwipingInProgress;
  private View mTargetedView;
  private int mTouchSlop;
  private boolean mTouchSlopExeeded;
  private int mTouchTargetSize;
  private float mTranslation;
  private float mTranslationOnDown;
  private VelocityTracker mVelocityTracker;
  
  KeyguardAffordanceHelper(Callback paramCallback, Context paramContext)
  {
    this.mContext = paramContext;
    this.mCallback = paramCallback;
    initIcons();
    updateIcon(this.mLeftIcon, 0.0F, this.mLeftIcon.getRestingAlpha(), false, false, true, false);
    updateIcon(this.mCenterIcon, 0.0F, this.mCenterIcon.getRestingAlpha(), false, false, true, false);
    updateIcon(this.mRightIcon, 0.0F, this.mRightIcon.getRestingAlpha(), false, false, true, false);
    initDimens();
  }
  
  private void cancelAnimation()
  {
    if (this.mSwipeAnimator != null) {
      this.mSwipeAnimator.cancel();
    }
  }
  
  private void endMotion(boolean paramBoolean, float paramFloat1, float paramFloat2)
  {
    if (this.mSwipingInProgress) {
      flingWithCurrentVelocity(paramBoolean, paramFloat1, paramFloat2);
    }
    for (;;)
    {
      if (this.mVelocityTracker != null)
      {
        this.mVelocityTracker.recycle();
        this.mVelocityTracker = null;
      }
      return;
      this.mTargetedView = null;
    }
  }
  
  private void fling(float paramFloat, boolean paramBoolean1, boolean paramBoolean2)
  {
    float f;
    ValueAnimator localValueAnimator;
    if (paramBoolean2)
    {
      f = -this.mCallback.getMaxTranslationDistance();
      if (paramBoolean1) {
        f = 0.0F;
      }
      localValueAnimator = ValueAnimator.ofFloat(new float[] { this.mTranslation, f });
      this.mFlingAnimationUtils.apply(localValueAnimator, this.mTranslation, f, paramFloat);
      localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          KeyguardAffordanceHelper.-set3(KeyguardAffordanceHelper.this, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
        }
      });
      localValueAnimator.addListener(this.mFlingEndListener);
      if (paramBoolean1) {
        break label152;
      }
      startFinishingCircleAnimation(0.375F * paramFloat, this.mAnimationEndRunnable, paramBoolean2);
      this.mCallback.onAnimationToSideStarted(paramBoolean2, this.mTranslation, paramFloat);
    }
    for (;;)
    {
      localValueAnimator.start();
      this.mSwipeAnimator = localValueAnimator;
      if (paramBoolean1) {
        this.mCallback.onSwipingAborted();
      }
      return;
      f = this.mCallback.getMaxTranslationDistance();
      break;
      label152:
      reset(true);
    }
  }
  
  private void flingWithCurrentVelocity(boolean paramBoolean, float paramFloat1, float paramFloat2)
  {
    boolean bool4 = true;
    paramFloat1 = getCurrentVelocity(paramFloat1, paramFloat2);
    boolean bool3 = false;
    label49:
    boolean bool1;
    label63:
    boolean bool2;
    if (this.mCallback.needsAntiFalsing())
    {
      if (0 == 0) {
        bool3 = this.mFalsingManager.isFalseTouch();
      }
    }
    else
    {
      if (bool3) {
        break label130;
      }
      bool3 = isBelowFalsingThreshold();
      if (this.mTranslation * paramFloat1 >= 0.0F) {
        break label136;
      }
      bool1 = true;
      if (Math.abs(paramFloat1) <= this.mMinFlingVelocity) {
        break label142;
      }
      bool2 = bool1;
      label80:
      bool2 = bool3 | bool2;
      if ((bool2 ^ bool1)) {
        paramFloat1 = 0.0F;
      }
      if (bool2) {
        break label148;
      }
      label102:
      if (this.mTranslation >= 0.0F) {
        break label153;
      }
    }
    label130:
    label136:
    label142:
    label148:
    label153:
    for (bool3 = bool4;; bool3 = false)
    {
      fling(paramFloat1, paramBoolean, bool3);
      return;
      bool3 = true;
      break;
      bool3 = true;
      break label49;
      bool1 = false;
      break label63;
      bool2 = false;
      break label80;
      paramBoolean = true;
      break label102;
    }
  }
  
  private ValueAnimator getAnimatorToRadius(final boolean paramBoolean, int paramInt)
  {
    if (paramBoolean) {}
    for (final KeyguardAffordanceView localKeyguardAffordanceView = this.mRightIcon;; localKeyguardAffordanceView = this.mLeftIcon)
    {
      ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { localKeyguardAffordanceView.getCircleRadius(), paramInt });
      localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          float f1 = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
          localKeyguardAffordanceView.setCircleRadiusWithoutAnimation(f1);
          float f2 = KeyguardAffordanceHelper.-wrap0(KeyguardAffordanceHelper.this, f1);
          paramAnonymousValueAnimator = KeyguardAffordanceHelper.this;
          f1 = f2;
          if (paramBoolean) {
            f1 = -f2;
          }
          KeyguardAffordanceHelper.-set3(paramAnonymousValueAnimator, f1);
          KeyguardAffordanceHelper.-wrap2(KeyguardAffordanceHelper.this, localKeyguardAffordanceView);
        }
      });
      return localValueAnimator;
    }
  }
  
  private float getCurrentVelocity(float paramFloat1, float paramFloat2)
  {
    if (this.mVelocityTracker == null) {
      return 0.0F;
    }
    this.mVelocityTracker.computeCurrentVelocity(1000);
    float f1 = this.mVelocityTracker.getXVelocity();
    float f2 = this.mVelocityTracker.getYVelocity();
    paramFloat1 -= this.mInitialTouchX;
    paramFloat2 -= this.mInitialTouchY;
    paramFloat2 = (f1 * paramFloat1 + f2 * paramFloat2) / (float)Math.hypot(paramFloat1, paramFloat2);
    paramFloat1 = paramFloat2;
    if (this.mTargetedView == this.mRightIcon) {
      paramFloat1 = -paramFloat2;
    }
    return paramFloat1;
  }
  
  private View getIconAtPosition(float paramFloat1, float paramFloat2)
  {
    if ((leftSwipePossible()) && (isOnIcon(this.mLeftIcon, paramFloat1, paramFloat2))) {
      return this.mLeftIcon;
    }
    if ((rightSwipePossible()) && (isOnIcon(this.mRightIcon, paramFloat1, paramFloat2))) {
      return this.mRightIcon;
    }
    return null;
  }
  
  private int getMinTranslationAmount()
  {
    float f = this.mCallback.getAffordanceFalsingFactor();
    return (int)(this.mMinTranslationAmount * f);
  }
  
  private float getRadiusFromTranslation(float paramFloat)
  {
    if (paramFloat <= this.mTouchSlop) {
      return 0.0F;
    }
    return (paramFloat - this.mTouchSlop) * 0.25F + this.mMinBackgroundRadius;
  }
  
  private float getScale(float paramFloat, KeyguardAffordanceView paramKeyguardAffordanceView)
  {
    return Math.min(paramFloat / paramKeyguardAffordanceView.getRestingAlpha() * 0.2F + 0.8F, 1.5F);
  }
  
  private float getTranslationFromRadius(float paramFloat)
  {
    float f1 = 0.0F;
    float f2 = (paramFloat - this.mMinBackgroundRadius) / 0.25F;
    paramFloat = f1;
    if (f2 > 0.0F) {
      paramFloat = this.mTouchSlop + f2;
    }
    return paramFloat;
  }
  
  private void initDimens()
  {
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(this.mContext);
    this.mTouchSlop = localViewConfiguration.getScaledPagingTouchSlop();
    this.mMinFlingVelocity = localViewConfiguration.getScaledMinimumFlingVelocity();
    this.mMinTranslationAmount = this.mContext.getResources().getDimensionPixelSize(2131755473);
    this.mMinBackgroundRadius = this.mContext.getResources().getDimensionPixelSize(2131755474);
    this.mTouchTargetSize = this.mContext.getResources().getDimensionPixelSize(2131755475);
    this.mHintGrowAmount = this.mContext.getResources().getDimensionPixelSize(2131755476);
    this.mFlingAnimationUtils = new FlingAnimationUtils(this.mContext, 0.4F);
    this.mFalsingManager = FalsingManager.getInstance(this.mContext);
  }
  
  private void initIcons()
  {
    this.mLeftIcon = this.mCallback.getLeftIcon();
    this.mCenterIcon = this.mCallback.getCenterIcon();
    this.mRightIcon = this.mCallback.getRightIcon();
    updatePreviews();
  }
  
  private void initVelocityTracker()
  {
    if (this.mVelocityTracker != null) {
      this.mVelocityTracker.recycle();
    }
    this.mVelocityTracker = VelocityTracker.obtain();
  }
  
  private boolean isBelowFalsingThreshold()
  {
    return Math.abs(this.mTranslation) < Math.abs(this.mTranslationOnDown) + getMinTranslationAmount();
  }
  
  private boolean isOnIcon(View paramView, float paramFloat1, float paramFloat2)
  {
    float f1 = paramView.getX();
    float f2 = paramView.getWidth() / 2.0F;
    float f3 = paramView.getY();
    float f4 = paramView.getHeight() / 2.0F;
    return Math.hypot(paramFloat1 - (f1 + f2), paramFloat2 - (f3 + f4)) <= this.mTouchTargetSize / 2;
  }
  
  private boolean leftSwipePossible()
  {
    boolean bool = false;
    if (this.mLeftIcon.getVisibility() == 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean rightSwipePossible()
  {
    boolean bool = false;
    if (this.mRightIcon.getVisibility() == 0) {
      bool = true;
    }
    return bool;
  }
  
  private void setTranslation(float paramFloat, boolean paramBoolean1, boolean paramBoolean2)
  {
    label14:
    KeyguardAffordanceView localKeyguardAffordanceView1;
    label45:
    KeyguardAffordanceView localKeyguardAffordanceView2;
    label57:
    float f2;
    boolean bool1;
    label84:
    label94:
    boolean bool2;
    if (rightSwipePossible())
    {
      if (!leftSwipePossible()) {
        break label200;
      }
      float f3 = Math.abs(paramFloat);
      if ((paramFloat != this.mTranslation) || (paramBoolean1))
      {
        if (paramFloat <= 0.0F) {
          break label209;
        }
        localKeyguardAffordanceView1 = this.mLeftIcon;
        if (paramFloat <= 0.0F) {
          break label218;
        }
        localKeyguardAffordanceView2 = this.mRightIcon;
        float f1 = f3 / getMinTranslationAmount();
        f2 = Math.max(1.0F - f1, 0.0F);
        if (!paramBoolean1) {
          break label227;
        }
        bool1 = paramBoolean2;
        if ((paramBoolean1) && (!paramBoolean2)) {
          break label233;
        }
        paramBoolean2 = false;
        f3 = getRadiusFromTranslation(f3);
        if (!paramBoolean1) {
          break label238;
        }
        bool2 = isBelowFalsingThreshold();
        label112:
        if (paramBoolean1) {
          break label244;
        }
        updateIcon(localKeyguardAffordanceView1, f3, f1 + localKeyguardAffordanceView1.getRestingAlpha() * f2, false, false, false, false);
      }
    }
    for (;;)
    {
      updateIcon(localKeyguardAffordanceView2, 0.0F, f2 * localKeyguardAffordanceView2.getRestingAlpha(), bool1, bool2, false, paramBoolean2);
      updateIcon(this.mCenterIcon, 0.0F, f2 * this.mCenterIcon.getRestingAlpha(), bool1, bool2, false, paramBoolean2);
      this.mTranslation = paramFloat;
      return;
      paramFloat = Math.max(0.0F, paramFloat);
      break;
      label200:
      paramFloat = Math.min(0.0F, paramFloat);
      break label14;
      label209:
      localKeyguardAffordanceView1 = this.mRightIcon;
      break label45;
      label218:
      localKeyguardAffordanceView2 = this.mLeftIcon;
      break label57;
      label227:
      bool1 = false;
      break label84;
      label233:
      paramBoolean2 = true;
      break label94;
      label238:
      bool2 = false;
      break label112;
      label244:
      updateIcon(localKeyguardAffordanceView1, 0.0F, f2 * localKeyguardAffordanceView1.getRestingAlpha(), bool1, bool2, false, paramBoolean2);
    }
  }
  
  private void startFinishingCircleAnimation(float paramFloat, Runnable paramRunnable, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (KeyguardAffordanceView localKeyguardAffordanceView = this.mRightIcon;; localKeyguardAffordanceView = this.mLeftIcon)
    {
      localKeyguardAffordanceView.finishAnimation(paramFloat, paramRunnable);
      return;
    }
  }
  
  private void startHintAnimationPhase1(final boolean paramBoolean, final Runnable paramRunnable)
  {
    if (paramBoolean) {}
    for (KeyguardAffordanceView localKeyguardAffordanceView = this.mRightIcon;; localKeyguardAffordanceView = this.mLeftIcon)
    {
      ValueAnimator localValueAnimator = getAnimatorToRadius(paramBoolean, this.mHintGrowAmount);
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
            KeyguardAffordanceHelper.-set0(KeyguardAffordanceHelper.this, null);
            KeyguardAffordanceHelper.-set2(KeyguardAffordanceHelper.this, null);
            paramRunnable.run();
            return;
          }
          KeyguardAffordanceHelper.-wrap1(KeyguardAffordanceHelper.this, paramBoolean, paramRunnable);
        }
      });
      localValueAnimator.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
      localValueAnimator.setDuration(200L);
      localValueAnimator.start();
      this.mSwipeAnimator = localValueAnimator;
      this.mTargetedView = localKeyguardAffordanceView;
      return;
    }
  }
  
  private void startSwiping(View paramView)
  {
    Callback localCallback = this.mCallback;
    if (paramView == this.mRightIcon) {}
    for (boolean bool = true;; bool = false)
    {
      localCallback.onSwipingStarted(bool);
      this.mSwipingInProgress = true;
      this.mTargetedView = paramView;
      return;
    }
  }
  
  private void startUnlockHintAnimationPhase2(boolean paramBoolean, final Runnable paramRunnable)
  {
    ValueAnimator localValueAnimator = getAnimatorToRadius(paramBoolean, 0);
    localValueAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        KeyguardAffordanceHelper.-set0(KeyguardAffordanceHelper.this, null);
        KeyguardAffordanceHelper.-set2(KeyguardAffordanceHelper.this, null);
        paramRunnable.run();
      }
    });
    localValueAnimator.setInterpolator(Interpolators.FAST_OUT_LINEAR_IN);
    localValueAnimator.setDuration(350L);
    localValueAnimator.setStartDelay(500L);
    localValueAnimator.start();
    this.mSwipeAnimator = localValueAnimator;
  }
  
  private void trackMovement(MotionEvent paramMotionEvent)
  {
    if (this.mVelocityTracker != null) {
      this.mVelocityTracker.addMovement(paramMotionEvent);
    }
  }
  
  private void updateIcon(KeyguardAffordanceView paramKeyguardAffordanceView, float paramFloat1, float paramFloat2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    if ((paramKeyguardAffordanceView.getVisibility() == 0) || (paramBoolean3))
    {
      if (!paramBoolean4) {
        break label32;
      }
      paramKeyguardAffordanceView.setCircleRadiusWithoutAnimation(paramFloat1);
    }
    for (;;)
    {
      updateIconAlpha(paramKeyguardAffordanceView, paramFloat2, paramBoolean1);
      return;
      return;
      label32:
      paramKeyguardAffordanceView.setCircleRadius(paramFloat1, paramBoolean2);
    }
  }
  
  private void updateIconAlpha(KeyguardAffordanceView paramKeyguardAffordanceView, float paramFloat, boolean paramBoolean)
  {
    float f = getScale(paramFloat, paramKeyguardAffordanceView);
    paramKeyguardAffordanceView.setImageAlpha(Math.min(1.0F, paramFloat), paramBoolean);
    paramKeyguardAffordanceView.setImageScale(f, paramBoolean);
  }
  
  private void updateIconsFromTranslation(KeyguardAffordanceView paramKeyguardAffordanceView)
  {
    float f1 = Math.abs(this.mTranslation) / getMinTranslationAmount();
    float f2 = Math.max(0.0F, 1.0F - f1);
    if (paramKeyguardAffordanceView == this.mRightIcon) {}
    for (KeyguardAffordanceView localKeyguardAffordanceView = this.mLeftIcon;; localKeyguardAffordanceView = this.mRightIcon)
    {
      updateIconAlpha(paramKeyguardAffordanceView, paramKeyguardAffordanceView.getRestingAlpha() * f2 + f1, false);
      updateIconAlpha(localKeyguardAffordanceView, localKeyguardAffordanceView.getRestingAlpha() * f2, false);
      updateIconAlpha(this.mCenterIcon, this.mCenterIcon.getRestingAlpha() * f2, false);
      return;
    }
  }
  
  public void animateHideLeftRightIcon()
  {
    cancelAnimation();
    updateIcon(this.mRightIcon, 0.0F, 0.0F, true, false, false, false);
    updateIcon(this.mLeftIcon, 0.0F, 0.0F, true, false, false, false);
  }
  
  public boolean isOnAffordanceIcon(float paramFloat1, float paramFloat2)
  {
    if (!isOnIcon(this.mLeftIcon, paramFloat1, paramFloat2)) {
      return isOnIcon(this.mRightIcon, paramFloat1, paramFloat2);
    }
    return true;
  }
  
  public boolean isSwipingInProgress()
  {
    return this.mSwipingInProgress;
  }
  
  public void launchAffordance(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mSwipingInProgress) {
      return;
    }
    KeyguardAffordanceView localKeyguardAffordanceView1;
    KeyguardAffordanceView localKeyguardAffordanceView2;
    if (paramBoolean2)
    {
      localKeyguardAffordanceView1 = this.mLeftIcon;
      if (!paramBoolean2) {
        break label87;
      }
      localKeyguardAffordanceView2 = this.mRightIcon;
      label28:
      startSwiping(localKeyguardAffordanceView1);
      if (!paramBoolean1) {
        break label101;
      }
      if (!paramBoolean2) {
        break label96;
      }
    }
    label87:
    label96:
    for (paramBoolean1 = false;; paramBoolean1 = true)
    {
      fling(0.0F, false, paramBoolean1);
      updateIcon(localKeyguardAffordanceView2, 0.0F, 0.0F, true, false, true, false);
      updateIcon(this.mCenterIcon, 0.0F, 0.0F, true, false, true, false);
      return;
      localKeyguardAffordanceView1 = this.mRightIcon;
      break;
      localKeyguardAffordanceView2 = this.mLeftIcon;
      break label28;
    }
    label101:
    if (!paramBoolean2) {
      this.mRightIcon.setCircleColorToInverse(true);
    }
    Callback localCallback = this.mCallback;
    if (paramBoolean2)
    {
      paramBoolean1 = false;
      localCallback.onAnimationToSideStarted(paramBoolean1, this.mTranslation, 0.0F);
      if (!paramBoolean2) {
        break label211;
      }
    }
    label211:
    for (float f = this.mCallback.getMaxTranslationDistance();; f = this.mCallback.getMaxTranslationDistance())
    {
      this.mTranslation = f;
      updateIcon(this.mCenterIcon, 0.0F, 0.0F, false, false, true, false);
      updateIcon(localKeyguardAffordanceView2, 0.0F, 0.0F, false, false, true, false);
      localKeyguardAffordanceView1.instantFinishAnimation();
      this.mFlingEndListener.onAnimationEnd(null);
      this.mAnimationEndRunnable.run();
      return;
      paramBoolean1 = true;
      break;
    }
  }
  
  public void onConfigurationChanged()
  {
    initDimens();
    initIcons();
  }
  
  public void onRtlPropertiesChanged()
  {
    initIcons();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int j = paramMotionEvent.getActionMasked();
    if ((this.mMotionCancelled) && (j != 0)) {
      return false;
    }
    float f1 = paramMotionEvent.getY();
    float f2 = paramMotionEvent.getX();
    int i = 0;
    switch (j)
    {
    case 4: 
    default: 
    case 0: 
    case 5: 
    case 2: 
      do
      {
        for (;;)
        {
          return true;
          View localView = getIconAtPosition(f2, f1);
          if ((localView == null) || ((this.mTargetedView != null) && (this.mTargetedView != localView)))
          {
            this.mMotionCancelled = true;
            return false;
          }
          if (this.mTargetedView != null) {
            cancelAnimation();
          }
          for (;;)
          {
            startSwiping(localView);
            this.mInitialTouchX = f2;
            this.mInitialTouchY = f1;
            this.mTranslationOnDown = this.mTranslation;
            initVelocityTracker();
            trackMovement(paramMotionEvent);
            this.mMotionCancelled = false;
            break;
            this.mTouchSlopExeeded = false;
          }
          this.mMotionCancelled = true;
          endMotion(true, f2, f1);
        }
        trackMovement(paramMotionEvent);
        float f3 = this.mInitialTouchX;
        float f4 = this.mInitialTouchY;
        f1 = (float)Math.hypot(f2 - f3, f1 - f4);
        if ((!this.mTouchSlopExeeded) && (f1 > this.mTouchSlop)) {
          this.mTouchSlopExeeded = true;
        }
      } while (!this.mSwipingInProgress);
      if (this.mTargetedView == this.mRightIcon) {}
      for (f1 = Math.min(0.0F, this.mTranslationOnDown - f1);; f1 = Math.max(0.0F, f1 + this.mTranslationOnDown))
      {
        setTranslation(f1, false, false);
        break;
      }
    case 1: 
      i = 1;
    }
    boolean bool1;
    if (this.mTargetedView == this.mRightIcon)
    {
      bool1 = true;
      label309:
      trackMovement(paramMotionEvent);
      if (i == 0) {
        break label362;
      }
    }
    label362:
    for (boolean bool2 = false;; bool2 = true)
    {
      endMotion(bool2, f2, f1);
      if ((this.mTouchSlopExeeded) || (i == 0)) {
        break;
      }
      this.mCallback.onIconClicked(bool1);
      break;
      bool1 = false;
      break label309;
    }
  }
  
  public void reset(boolean paramBoolean)
  {
    cancelAnimation();
    setTranslation(0.0F, true, paramBoolean);
    this.mMotionCancelled = true;
    if (this.mSwipingInProgress)
    {
      this.mCallback.onSwipingAborted();
      this.mSwipingInProgress = false;
    }
  }
  
  public void startHintAnimation(boolean paramBoolean, Runnable paramRunnable)
  {
    cancelAnimation();
    startHintAnimationPhase1(paramBoolean, paramRunnable);
  }
  
  public void updatePreviews()
  {
    this.mLeftIcon.setPreviewView(this.mCallback.getLeftPreview());
    this.mRightIcon.setPreviewView(this.mCallback.getRightPreview());
  }
  
  public static abstract interface Callback
  {
    public abstract float getAffordanceFalsingFactor();
    
    public abstract KeyguardAffordanceView getCenterIcon();
    
    public abstract KeyguardAffordanceView getLeftIcon();
    
    public abstract View getLeftPreview();
    
    public abstract float getMaxTranslationDistance();
    
    public abstract KeyguardAffordanceView getRightIcon();
    
    public abstract View getRightPreview();
    
    public abstract boolean needsAntiFalsing();
    
    public abstract void onAnimationToSideEnded();
    
    public abstract void onAnimationToSideStarted(boolean paramBoolean, float paramFloat1, float paramFloat2);
    
    public abstract void onIconClicked(boolean paramBoolean);
    
    public abstract void onSwipingAborted();
    
    public abstract void onSwipingStarted(boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\KeyguardAffordanceHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */