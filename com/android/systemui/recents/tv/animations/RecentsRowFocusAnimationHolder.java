package com.android.systemui.recents.tv.animations;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.view.View;
import com.android.systemui.Interpolators;

public class RecentsRowFocusAnimationHolder
{
  private AnimatorSet mFocusGainAnimatorSet;
  private AnimatorSet mFocusLossAnimatorSet;
  private final View mTitleView;
  private final View mView;
  
  public RecentsRowFocusAnimationHolder(View paramView1, View paramView2)
  {
    this.mView = paramView1;
    this.mTitleView = paramView2;
    paramView1 = paramView1.getResources();
    int i = paramView1.getInteger(2131624027);
    float f = paramView1.getFloat(2131755625);
    this.mFocusGainAnimatorSet = new AnimatorSet();
    this.mFocusGainAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.mView, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.mTitleView, "alpha", new float[] { 1.0F }) });
    this.mFocusGainAnimatorSet.setDuration(i);
    this.mFocusGainAnimatorSet.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
    this.mFocusLossAnimatorSet = new AnimatorSet();
    this.mFocusLossAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.mView, "alpha", new float[] { 1.0F, f }), ObjectAnimator.ofFloat(this.mTitleView, "alpha", new float[] { 0.0F }) });
    this.mFocusLossAnimatorSet.setDuration(i);
    this.mFocusLossAnimatorSet.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
  }
  
  private static void cancelAnimator(Animator paramAnimator)
  {
    if (paramAnimator.isStarted()) {
      paramAnimator.cancel();
    }
  }
  
  public void reset()
  {
    cancelAnimator(this.mFocusLossAnimatorSet);
    cancelAnimator(this.mFocusGainAnimatorSet);
    this.mView.setAlpha(1.0F);
    this.mTitleView.setAlpha(1.0F);
  }
  
  public void startFocusGainAnimation()
  {
    cancelAnimator(this.mFocusLossAnimatorSet);
    this.mFocusGainAnimatorSet.start();
  }
  
  public void startFocusLossAnimation()
  {
    cancelAnimator(this.mFocusGainAnimatorSet);
    this.mFocusLossAnimatorSet.start();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\tv\animations\RecentsRowFocusAnimationHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */