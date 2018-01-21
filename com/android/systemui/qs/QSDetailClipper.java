package com.android.systemui.qs;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Interpolator;

public class QSDetailClipper
{
  private Animator mAnimator;
  private final TransitionDrawable mBackground;
  private final View mDetail;
  private final AnimatorListenerAdapter mGoneOnEnd = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      QSDetailClipper.-get1(QSDetailClipper.this).setVisibility(8);
      QSDetailClipper.-get0(QSDetailClipper.this).resetTransition();
      QSDetailClipper.-set0(QSDetailClipper.this, null);
    }
  };
  private Interpolator mInterpolator = null;
  private int mMinHeight;
  private final AnimatorListenerAdapter mVisibleOnStart = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      QSDetailClipper.-set0(QSDetailClipper.this, null);
    }
    
    public void onAnimationStart(Animator paramAnonymousAnimator)
    {
      QSDetailClipper.-get1(QSDetailClipper.this).setVisibility(0);
    }
  };
  
  public QSDetailClipper(View paramView)
  {
    this(paramView, 1920);
  }
  
  public QSDetailClipper(View paramView, int paramInt)
  {
    this.mDetail = paramView;
    this.mMinHeight = paramInt;
    this.mBackground = ((TransitionDrawable)paramView.getBackground());
  }
  
  public void animateCircularClip(int paramInt1, int paramInt2, boolean paramBoolean, Animator.AnimatorListener paramAnimatorListener, int paramInt3)
  {
    if (this.mAnimator != null) {
      this.mAnimator.cancel();
    }
    int j = this.mDetail.getWidth() - paramInt1;
    int k = Math.max(this.mDetail.getHeight(), this.mMinHeight) - paramInt2;
    int i = 0;
    if ((paramInt1 < 0) || (j < 0)) {}
    for (;;)
    {
      i = Math.min(Math.min(Math.min(Math.abs(paramInt1), Math.abs(paramInt2)), Math.abs(j)), Math.abs(k));
      label84:
      j = (int)Math.max((int)Math.max((int)Math.max((int)Math.ceil(Math.sqrt(paramInt1 * paramInt1 + paramInt2 * paramInt2)), Math.ceil(Math.sqrt(j * j + paramInt2 * paramInt2))), Math.ceil(Math.sqrt(j * j + k * k))), Math.ceil(Math.sqrt(paramInt1 * paramInt1 + k * k)));
      if (paramBoolean) {}
      try
      {
        for (this.mAnimator = ViewAnimationUtils.createCircularReveal(this.mDetail, paramInt1, paramInt2, i, j);; this.mAnimator = ViewAnimationUtils.createCircularReveal(this.mDetail, paramInt1, paramInt2, j, i))
        {
          this.mAnimator.setDuration((this.mAnimator.getDuration() * 1.5D));
          if (this.mInterpolator != null) {
            this.mAnimator.setInterpolator(this.mInterpolator);
          }
          if (paramAnimatorListener != null) {
            this.mAnimator.addListener(paramAnimatorListener);
          }
          if (!paramBoolean) {
            break label362;
          }
          this.mBackground.startTransition((int)(this.mAnimator.getDuration() * 0.4D));
          this.mAnimator.addListener(this.mVisibleOnStart);
          this.mAnimator.setStartDelay(paramInt3);
          this.mAnimator.start();
          return;
          if (paramInt2 < 0) {
            break;
          }
          if (k >= 0) {
            break label84;
          }
          break;
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          Log.d("QSDetailClipper", "Using dummy animator while cannot using createCircularReveal", localException);
          this.mAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
          continue;
          label362:
          this.mAnimator.addListener(this.mGoneOnEnd);
        }
      }
    }
  }
  
  public void setInterpolator(Interpolator paramInterpolator)
  {
    this.mInterpolator = paramInterpolator;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\QSDetailClipper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */