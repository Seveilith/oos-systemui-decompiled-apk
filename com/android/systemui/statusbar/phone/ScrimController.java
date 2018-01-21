package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.NotificationData.Entry;
import com.android.systemui.statusbar.ScrimView;
import com.android.systemui.statusbar.policy.HeadsUpManager.OnHeadsUpChangedListener;
import com.android.systemui.statusbar.stack.StackStateAnimator;
import com.android.systemui.util.Utils;

public class ScrimController
  implements ViewTreeObserver.OnPreDrawListener, HeadsUpManager.OnHeadsUpChangedListener
{
  public static final Interpolator KEYGUARD_FADE_OUT_INTERPOLATOR = new PathInterpolator(0.0F, 0.0F, 0.7F, 1.0F);
  public static final Interpolator KEYGUARD_FADE_OUT_INTERPOLATOR_LOCKED = new PathInterpolator(0.3F, 0.0F, 0.8F, 1.0F);
  private boolean mAnimateChange;
  private boolean mAnimateKeyguardFadingOut;
  private long mAnimationDelay;
  protected boolean mBouncerShowing;
  private float mCurrentBehindAlpha;
  private float mCurrentHeadsUpAlpha = 1.0F;
  private float mCurrentInFrontAlpha;
  private boolean mDarkenWhileDragging;
  private boolean mDontAnimateBouncerChanges;
  private float mDozeBehindAlpha;
  private float mDozeInFrontAlpha;
  private boolean mDozing;
  private View mDraggedHeadsUpView;
  private long mDurationOverride = -1L;
  private boolean mExpanding;
  private boolean mForceHideScrims;
  private float mFraction;
  private final View mHeadsUpScrim;
  private final Interpolator mInterpolator = new DecelerateInterpolator();
  private ValueAnimator mKeyguardFadeoutAnimation;
  private boolean mKeyguardFadingOutInProgress;
  protected boolean mKeyguardShowing;
  private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
  private Runnable mOnAnimationFinished;
  private int mPinnedHeadsUpCount;
  protected final ScrimView mScrimBehind;
  private float mScrimBehindAlpha = 0.62F;
  private float mScrimBehindAlphaKeyguard = 0.3F;
  private float mScrimBehindAlphaUnlocking = 0.2F;
  private final ScrimView mScrimInFront;
  private boolean mSkipFirstFrame;
  private float mTopHeadsUpDragAmount;
  private final UnlockMethodCache mUnlockMethodCache;
  private boolean mUpdatePending;
  private boolean mWakeAndUnlocking;
  private boolean mWakeAndUnlockingPulsing;
  
  public ScrimController(ScrimView paramScrimView1, ScrimView paramScrimView2, View paramView)
  {
    this.mScrimBehind = paramScrimView1;
    this.mScrimInFront = paramScrimView2;
    this.mHeadsUpScrim = paramView;
    paramScrimView1 = paramScrimView1.getContext();
    this.mUnlockMethodCache = UnlockMethodCache.getInstance(paramScrimView1);
    this.mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(paramScrimView1);
    updateHeadsUpScrim(false);
  }
  
  private float calculateHeadsUpAlpha()
  {
    float f;
    if (this.mPinnedHeadsUpCount >= 2) {
      f = 1.0F;
    }
    for (;;)
    {
      return f * Math.max(1.0F - this.mFraction, 0.0F);
      if (this.mPinnedHeadsUpCount == 0) {
        f = 0.0F;
      } else {
        f = 1.0F - this.mTopHeadsUpDragAmount;
      }
    }
  }
  
  private void endAnimateKeyguardFadingOut(boolean paramBoolean)
  {
    this.mAnimateKeyguardFadingOut = false;
    if ((!paramBoolean) && ((isAnimating(this.mScrimInFront)) || (isAnimating(this.mScrimBehind)))) {
      return;
    }
    if (this.mOnAnimationFinished != null)
    {
      this.mOnAnimationFinished.run();
      this.mOnAnimationFinished = null;
    }
    this.mKeyguardFadingOutInProgress = false;
  }
  
  private float getCurrentScrimAlpha(View paramView)
  {
    if (paramView == this.mScrimBehind) {
      return this.mCurrentBehindAlpha;
    }
    if (paramView == this.mScrimInFront) {
      return this.mCurrentInFrontAlpha;
    }
    return this.mCurrentHeadsUpAlpha;
  }
  
  private float getDozeAlpha(View paramView)
  {
    if (this.mForceHideScrims) {
      return 0.0F;
    }
    if (paramView == this.mScrimBehind) {
      return this.mDozeBehindAlpha;
    }
    return this.mDozeInFrontAlpha;
  }
  
  private Interpolator getInterpolator()
  {
    if ((this.mAnimateKeyguardFadingOut) && (this.mKeyguardUpdateMonitor.needsSlowUnlockTransition())) {
      return KEYGUARD_FADE_OUT_INTERPOLATOR_LOCKED;
    }
    if (this.mAnimateKeyguardFadingOut) {
      return KEYGUARD_FADE_OUT_INTERPOLATOR;
    }
    return this.mInterpolator;
  }
  
  private float getScrimInFrontAlpha()
  {
    if (this.mKeyguardUpdateMonitor.needsSlowUnlockTransition()) {}
    return 0.75F;
  }
  
  private boolean isAnimating(View paramView)
  {
    return paramView.getTag(2131951664) != null;
  }
  
  private void scheduleUpdate()
  {
    if (this.mUpdatePending) {
      return;
    }
    this.mScrimBehind.invalidate();
    this.mScrimBehind.getViewTreeObserver().addOnPreDrawListener(this);
    this.mUpdatePending = true;
  }
  
  private void setCurrentScrimAlpha(View paramView, float paramFloat)
  {
    if (paramView == this.mScrimBehind)
    {
      this.mCurrentBehindAlpha = paramFloat;
      return;
    }
    if (paramView == this.mScrimInFront)
    {
      this.mCurrentInFrontAlpha = paramFloat;
      return;
    }
    this.mCurrentHeadsUpAlpha = Math.max(0.0F, Math.min(1.0F, paramFloat));
  }
  
  private void setScrimBehindColor(float paramFloat)
  {
    setScrimColor(this.mScrimBehind, paramFloat);
  }
  
  private void setScrimColor(View paramView, float paramFloat)
  {
    updateScrim(this.mAnimateChange, paramView, paramFloat, getCurrentScrimAlpha(paramView));
  }
  
  private void setScrimInFrontColor(float paramFloat)
  {
    boolean bool = false;
    if ((Utils.DEBUG_ONEPLUS) && (this.mKeyguardUpdateMonitor.isSimPinSecure())) {
      Log.d("ScrimController", "set infront to " + paramFloat);
    }
    setScrimColor(this.mScrimInFront, paramFloat);
    if (paramFloat == 0.0F)
    {
      this.mScrimInFront.setClickable(false);
      return;
    }
    ScrimView localScrimView = this.mScrimInFront;
    if (this.mDozing) {}
    for (;;)
    {
      localScrimView.setClickable(bool);
      return;
      bool = true;
    }
  }
  
  private void startScrimAnimation(final View paramView, float paramFloat)
  {
    ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { getCurrentScrimAlpha(paramView), paramFloat });
    localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        float f = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        ScrimController.-wrap0(ScrimController.this, paramView, f);
        ScrimController.-wrap1(ScrimController.this, paramView);
      }
    });
    localValueAnimator.setInterpolator(getInterpolator());
    localValueAnimator.setStartDelay(this.mAnimationDelay);
    if (this.mDurationOverride != -1L) {}
    for (long l = this.mDurationOverride;; l = 0L)
    {
      localValueAnimator.setDuration(l);
      localValueAnimator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (ScrimController.-get1(ScrimController.this) != null)
          {
            ScrimController.-get1(ScrimController.this).run();
            ScrimController.-set2(ScrimController.this, null);
          }
          if (ScrimController.-get0(ScrimController.this))
          {
            ScrimController.-set0(ScrimController.this, null);
            ScrimController.-set1(ScrimController.this, false);
          }
          paramView.setTag(2131951664, null);
          paramView.setTag(2131951665, null);
        }
      });
      localValueAnimator.start();
      if (this.mAnimateKeyguardFadingOut)
      {
        this.mKeyguardFadingOutInProgress = true;
        this.mKeyguardFadeoutAnimation = localValueAnimator;
      }
      if (this.mSkipFirstFrame) {
        localValueAnimator.setCurrentPlayTime(16L);
      }
      paramView.setTag(2131951664, localValueAnimator);
      paramView.setTag(2131951665, Float.valueOf(paramFloat));
      return;
    }
  }
  
  private void updateHeadsUpScrim(boolean paramBoolean)
  {
    updateScrim(paramBoolean, this.mHeadsUpScrim, calculateHeadsUpAlpha(), this.mCurrentHeadsUpAlpha);
  }
  
  private void updateScrim(boolean paramBoolean, View paramView, float paramFloat1, float paramFloat2)
  {
    if (this.mKeyguardFadingOutInProgress) {
      return;
    }
    ValueAnimator localValueAnimator = (ValueAnimator)StackStateAnimator.getChildTag(paramView, 2131951664);
    float f2 = -1.0F;
    float f1 = f2;
    if (localValueAnimator != null)
    {
      if ((!paramBoolean) && (paramFloat1 != paramFloat2)) {
        break label101;
      }
      localValueAnimator.cancel();
    }
    label101:
    for (f1 = f2;; f1 = ((Float)StackStateAnimator.getChildTag(paramView, 2131951667)).floatValue())
    {
      if ((paramFloat1 != paramFloat2) && (paramFloat1 != f1))
      {
        if (!paramBoolean) {
          break;
        }
        startScrimAnimation(paramView, paramFloat1);
        paramView.setTag(2131951666, Float.valueOf(paramFloat2));
        paramView.setTag(2131951667, Float.valueOf(paramFloat1));
      }
      return;
    }
    if (localValueAnimator != null)
    {
      paramFloat2 = ((Float)StackStateAnimator.getChildTag(paramView, 2131951666)).floatValue();
      f1 = ((Float)StackStateAnimator.getChildTag(paramView, 2131951667)).floatValue();
      PropertyValuesHolder[] arrayOfPropertyValuesHolder = localValueAnimator.getValues();
      paramFloat2 = Math.max(0.0F, Math.min(1.0F, paramFloat2 + (paramFloat1 - f1)));
      arrayOfPropertyValuesHolder[0].setFloatValues(new float[] { paramFloat2, paramFloat1 });
      paramView.setTag(2131951666, Float.valueOf(paramFloat2));
      paramView.setTag(2131951667, Float.valueOf(paramFloat1));
      localValueAnimator.setCurrentPlayTime(localValueAnimator.getCurrentPlayTime());
      return;
    }
    setCurrentScrimAlpha(paramView, paramFloat1);
    updateScrimColor(paramView);
  }
  
  private void updateScrimColor(View paramView)
  {
    float f = getCurrentScrimAlpha(paramView);
    if ((paramView instanceof ScrimView))
    {
      f = Math.max(0.0F, Math.min(1.0F, 1.0F - (1.0F - f) * (1.0F - getDozeAlpha(paramView))));
      ((ScrimView)paramView).setScrimColor(Color.argb((int)(255.0F * f), 0, 0, 0));
      return;
    }
    paramView.setAlpha(f);
  }
  
  private void updateScrimKeyguard()
  {
    if ((this.mExpanding) && (this.mDarkenWhileDragging))
    {
      float f2 = Math.max(0.0F, Math.min(this.mFraction, 1.0F));
      f1 = (float)Math.pow(1.0F - f2, 0.800000011920929D);
      f2 = (float)Math.pow(f2, 0.800000011920929D);
      setScrimInFrontColor(getScrimInFrontAlpha() * f1);
      setScrimBehindColor(this.mScrimBehindAlphaKeyguard * f2);
      return;
    }
    if (this.mBouncerShowing)
    {
      setScrimInFrontColor(getScrimInFrontAlpha());
      setScrimBehindColor(0.0F);
      return;
    }
    float f1 = Math.max(0.0F, Math.min(this.mFraction, 1.0F));
    setScrimInFrontColor(0.0F);
    setScrimBehindColor((this.mScrimBehindAlphaKeyguard - this.mScrimBehindAlphaUnlocking) * f1 + this.mScrimBehindAlphaUnlocking);
  }
  
  private void updateScrimNormal()
  {
    float f = 1.2F * this.mFraction - 0.2F;
    if (f <= 0.0F)
    {
      setScrimBehindColor(0.0F);
      return;
    }
    f = (float)(1.0D - (1.0D - Math.cos(Math.pow(1.0F - f, 2.0D) * 3.141590118408203D)) * 0.5D);
    setScrimBehindColor(this.mScrimBehindAlpha * f);
  }
  
  public void abortKeyguardFadingOut()
  {
    if (this.mAnimateKeyguardFadingOut) {
      endAnimateKeyguardFadingOut(true);
    }
  }
  
  public void animateGoingToFullShade(long paramLong1, long paramLong2)
  {
    this.mDurationOverride = paramLong2;
    this.mAnimationDelay = paramLong1;
    this.mAnimateChange = true;
    scheduleUpdate();
  }
  
  public void animateKeyguardFadingOut(long paramLong1, long paramLong2, Runnable paramRunnable, boolean paramBoolean)
  {
    this.mWakeAndUnlocking = false;
    this.mWakeAndUnlockingPulsing = false;
    this.mAnimateKeyguardFadingOut = true;
    this.mDurationOverride = paramLong2;
    this.mAnimationDelay = paramLong1;
    this.mAnimateChange = true;
    this.mSkipFirstFrame = paramBoolean;
    this.mOnAnimationFinished = paramRunnable;
    if (!this.mKeyguardUpdateMonitor.needsSlowUnlockTransition())
    {
      scheduleUpdate();
      onPreDraw();
      return;
    }
    this.mScrimInFront.postOnAnimationDelayed(new -void_animateKeyguardFadingOut_long_delay_long_duration_java_lang_Runnable_onAnimationFinished_boolean_skipFirstFrame_LambdaImpl0(), 16L);
  }
  
  public void animateKeyguardUnoccluding(long paramLong)
  {
    this.mAnimateChange = false;
    setScrimBehindColor(0.0F);
    this.mAnimateChange = true;
    scheduleUpdate();
    this.mDurationOverride = paramLong;
  }
  
  public void animateNextChange()
  {
    this.mAnimateChange = true;
  }
  
  public void dontAnimateBouncerChangesUntilNextFrame()
  {
    this.mDontAnimateBouncerChanges = true;
  }
  
  public void forceHideScrims(boolean paramBoolean)
  {
    this.mForceHideScrims = paramBoolean;
    this.mAnimateChange = false;
    scheduleUpdate();
  }
  
  public float getDozeBehindAlpha()
  {
    return this.mDozeBehindAlpha;
  }
  
  public float getDozeInFrontAlpha()
  {
    return this.mDozeInFrontAlpha;
  }
  
  public int getScrimBehindColor()
  {
    return this.mScrimBehind.getScrimColorWithAlpha();
  }
  
  public int getScrimInFrontColor()
  {
    return this.mScrimInFront.getScrimColorWithAlpha();
  }
  
  public void onDensityOrFontScaleChanged()
  {
    ViewGroup.LayoutParams localLayoutParams = this.mHeadsUpScrim.getLayoutParams();
    localLayoutParams.height = this.mHeadsUpScrim.getResources().getDimensionPixelSize(2131755472);
    this.mHeadsUpScrim.setLayoutParams(localLayoutParams);
  }
  
  public void onExpandingFinished()
  {
    if ((Utils.DEBUG_ONEPLUS) && (this.mKeyguardUpdateMonitor.isSimPinSecure())) {
      Log.d("ScrimController", "onExpandingFinished");
    }
    this.mExpanding = false;
  }
  
  public void onHeadsUpPinned(ExpandableNotificationRow paramExpandableNotificationRow)
  {
    this.mPinnedHeadsUpCount += 1;
    updateHeadsUpScrim(true);
  }
  
  public void onHeadsUpPinnedModeChanged(boolean paramBoolean) {}
  
  public void onHeadsUpStateChanged(NotificationData.Entry paramEntry, boolean paramBoolean) {}
  
  public void onHeadsUpUnPinned(ExpandableNotificationRow paramExpandableNotificationRow)
  {
    this.mPinnedHeadsUpCount -= 1;
    if (paramExpandableNotificationRow == this.mDraggedHeadsUpView)
    {
      this.mDraggedHeadsUpView = null;
      this.mTopHeadsUpDragAmount = 0.0F;
    }
    updateHeadsUpScrim(true);
  }
  
  public boolean onPreDraw()
  {
    this.mScrimBehind.getViewTreeObserver().removeOnPreDrawListener(this);
    this.mUpdatePending = false;
    if (this.mDontAnimateBouncerChanges) {
      this.mDontAnimateBouncerChanges = false;
    }
    updateScrims();
    this.mDurationOverride = -1L;
    this.mAnimationDelay = 0L;
    this.mSkipFirstFrame = false;
    endAnimateKeyguardFadingOut(false);
    return true;
  }
  
  public void onTrackingStarted()
  {
    boolean bool = true;
    this.mExpanding = true;
    if (this.mUnlockMethodCache.canSkipBouncer()) {
      bool = false;
    }
    this.mDarkenWhileDragging = bool;
  }
  
  public void setBouncerShowing(boolean paramBoolean)
  {
    boolean bool = false;
    this.mBouncerShowing = paramBoolean;
    paramBoolean = bool;
    if (!this.mExpanding) {
      if (!this.mDontAnimateBouncerChanges) {
        break label35;
      }
    }
    label35:
    for (paramBoolean = bool;; paramBoolean = true)
    {
      this.mAnimateChange = paramBoolean;
      scheduleUpdate();
      return;
    }
  }
  
  public void setCurrentUser(int paramInt) {}
  
  public void setDozeBehindAlpha(float paramFloat)
  {
    this.mDozeBehindAlpha = paramFloat;
    updateScrimColor(this.mScrimBehind);
  }
  
  public void setDozeInFrontAlpha(float paramFloat)
  {
    this.mDozeInFrontAlpha = paramFloat;
    updateScrimColor(this.mScrimInFront);
  }
  
  public void setDozing(boolean paramBoolean)
  {
    if (this.mDozing != paramBoolean)
    {
      this.mDozing = paramBoolean;
      scheduleUpdate();
    }
  }
  
  public void setDrawBehindAsSrc(boolean paramBoolean)
  {
    this.mScrimBehind.setDrawAsSrc(paramBoolean);
  }
  
  public void setExcludedBackgroundArea(Rect paramRect)
  {
    this.mScrimBehind.setExcludedArea(paramRect);
  }
  
  public void setKeyguardShowing(boolean paramBoolean)
  {
    this.mKeyguardShowing = paramBoolean;
    scheduleUpdate();
  }
  
  public void setLeftInset(int paramInt)
  {
    this.mScrimBehind.setLeftInset(paramInt);
  }
  
  public void setPanelExpansion(float paramFloat)
  {
    if (this.mFraction != paramFloat)
    {
      this.mFraction = paramFloat;
      scheduleUpdate();
      if (this.mPinnedHeadsUpCount != 0) {
        updateHeadsUpScrim(false);
      }
      if (this.mKeyguardFadeoutAnimation != null) {
        this.mKeyguardFadeoutAnimation.cancel();
      }
    }
  }
  
  public void setScrimBehindChangeRunnable(Runnable paramRunnable)
  {
    this.mScrimBehind.setChangeRunnable(paramRunnable);
  }
  
  public void setTopHeadsUpDragAmount(View paramView, float paramFloat)
  {
    this.mTopHeadsUpDragAmount = paramFloat;
    this.mDraggedHeadsUpView = paramView;
    updateHeadsUpScrim(false);
  }
  
  public void setWakeAndUnlocking()
  {
    this.mWakeAndUnlocking = true;
    scheduleUpdate();
  }
  
  protected void updateScrims()
  {
    Log.d("ScrimController", "updateScrims:   mAnimateKeyguardFadingOut = " + this.mAnimateKeyguardFadingOut + ", mForceHideScrims = " + this.mForceHideScrims + ", mWakeAndUnlocking = " + this.mWakeAndUnlocking + ", mWakeAndUnlockingPulsing = " + this.mWakeAndUnlockingPulsing + ", mKeyguardShowing = " + this.mKeyguardShowing + ", mExpanding = " + this.mExpanding + ", mDarkenWhileDragging = " + this.mDarkenWhileDragging + ", mBouncerShowing = " + this.mBouncerShowing);
    if ((this.mAnimateKeyguardFadingOut) || (this.mForceHideScrims))
    {
      setScrimInFrontColor(0.0F);
      setScrimBehindColor(0.0F);
    }
    for (;;)
    {
      this.mAnimateChange = false;
      return;
      if (this.mWakeAndUnlocking)
      {
        setScrimInFrontColor(0.0F);
        setScrimBehindColor(0.0F);
      }
      else if (this.mWakeAndUnlockingPulsing)
      {
        if (this.mDozing)
        {
          setScrimInFrontColor(0.0F);
          setScrimBehindColor(1.0F);
        }
        else
        {
          setScrimInFrontColor(1.0F);
          setScrimBehindColor(0.0F);
        }
      }
      else if ((this.mKeyguardShowing) || (this.mBouncerShowing))
      {
        updateScrimKeyguard();
      }
      else
      {
        updateScrimNormal();
        setScrimInFrontColor(0.0F);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\ScrimController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */