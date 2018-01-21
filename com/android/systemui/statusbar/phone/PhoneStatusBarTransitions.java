package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;

public final class PhoneStatusBarTransitions
  extends BarTransitions
{
  private View mBattery;
  private View mBatteryLevel;
  private View mClock;
  private Animator mCurrentAnimation;
  private View mDashView;
  private final float mIconAlphaWhenOpaque;
  private View mLeftSide;
  private View mSignalCluster;
  private View mSpeedView;
  private View mStatusIcons;
  private final PhoneStatusBarView mView;
  
  public PhoneStatusBarTransitions(PhoneStatusBarView paramPhoneStatusBarView)
  {
    super(paramPhoneStatusBarView, 2130839020);
    this.mView = paramPhoneStatusBarView;
    this.mIconAlphaWhenOpaque = this.mView.getContext().getResources().getFraction(2131755379, 1, 1);
  }
  
  private void applyMode(int paramInt, boolean paramBoolean)
  {
    if (this.mLeftSide == null) {
      return;
    }
    float f1 = getNonBatteryClockAlphaFor(paramInt);
    float f2 = getBatteryClockAlpha(paramInt);
    if (this.mCurrentAnimation != null) {
      this.mCurrentAnimation.cancel();
    }
    if (paramBoolean)
    {
      AnimatorSet localAnimatorSet = new AnimatorSet();
      localAnimatorSet.playTogether(new Animator[] { animateTransitionTo(this.mLeftSide, f1), animateTransitionTo(this.mStatusIcons, f1), animateTransitionTo(this.mSignalCluster, f1), animateTransitionTo(this.mSpeedView, f1), animateTransitionTo(this.mDashView, f2), animateTransitionTo(this.mBatteryLevel, f2), animateTransitionTo(this.mBattery, f2), animateTransitionTo(this.mClock, f2) });
      if (isLightsOut(paramInt)) {
        localAnimatorSet.setDuration(750L);
      }
      localAnimatorSet.start();
      this.mCurrentAnimation = localAnimatorSet;
      return;
    }
    this.mLeftSide.setAlpha(f1);
    this.mStatusIcons.setAlpha(f1);
    this.mSignalCluster.setAlpha(f1);
    this.mSpeedView.setAlpha(f1);
    this.mDashView.setAlpha(f2);
    this.mBatteryLevel.setAlpha(f2);
    this.mBattery.setAlpha(f2);
    this.mClock.setAlpha(f2);
  }
  
  private float getBatteryClockAlpha(int paramInt)
  {
    if (isLightsOut(paramInt)) {
      return 0.5F;
    }
    return getNonBatteryClockAlphaFor(paramInt);
  }
  
  private float getNonBatteryClockAlphaFor(int paramInt)
  {
    if (isLightsOut(paramInt)) {
      return 0.0F;
    }
    if (!isOpaque(paramInt)) {
      return 1.0F;
    }
    return this.mIconAlphaWhenOpaque;
  }
  
  private boolean isOpaque(int paramInt)
  {
    if ((paramInt != 1) && (paramInt != 2) && (paramInt != 4)) {
      return paramInt != 6;
    }
    return false;
  }
  
  public ObjectAnimator animateTransitionTo(View paramView, float paramFloat)
  {
    return ObjectAnimator.ofFloat(paramView, "alpha", new float[] { paramView.getAlpha(), paramFloat });
  }
  
  public void init()
  {
    this.mLeftSide = this.mView.findViewById(2131952266);
    this.mStatusIcons = this.mView.findViewById(2131952311);
    this.mSignalCluster = this.mView.findViewById(2131952234);
    this.mBattery = this.mView.findViewById(2131952313);
    this.mClock = this.mView.findViewById(2131951896);
    this.mDashView = this.mView.findViewById(2131952314);
    this.mSpeedView = this.mView.findViewById(2131952309);
    this.mBatteryLevel = this.mView.findViewById(2131952312);
    applyModeBackground(-1, getMode(), false);
    applyMode(getMode(), false);
  }
  
  protected void onTransition(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super.onTransition(paramInt1, paramInt2, paramBoolean);
    applyMode(paramInt2, paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\PhoneStatusBarTransitions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */