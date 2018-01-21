package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.animation.AnimationUtils;
import com.android.keyguard.AlphaOptimizedImageButton;
import com.android.systemui.Interpolators;

public class SettingsButton
  extends AlphaOptimizedImageButton
{
  private ObjectAnimator mAnimator;
  private final Runnable mLongPressCallback = new Runnable()
  {
    public void run()
    {
      SettingsButton.this.startAccelSpin();
    }
  };
  private float mSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
  private boolean mUpToSpeed;
  
  public SettingsButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void cancelAnimation()
  {
    if (this.mAnimator != null)
    {
      this.mAnimator.removeAllListeners();
      this.mAnimator.cancel();
      this.mAnimator = null;
    }
  }
  
  private void cancelLongClick()
  {
    cancelAnimation();
    this.mUpToSpeed = false;
    removeCallbacks(this.mLongPressCallback);
  }
  
  private void startExitAnimation()
  {
    animate().translationX(((View)getParent().getParent()).getWidth() - getX()).alpha(0.0F).setDuration(350L).setInterpolator(AnimationUtils.loadInterpolator(this.mContext, 17563650)).setListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator) {}
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        SettingsButton.this.setAlpha(1.0F);
        SettingsButton.this.setTranslationX(0.0F);
        SettingsButton.-wrap0(SettingsButton.this);
      }
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator) {}
    }).start();
  }
  
  public boolean isTunerClick()
  {
    return this.mUpToSpeed;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getActionMasked())
    {
    default: 
    case 0: 
    case 1: 
    case 3: 
      for (;;)
      {
        return super.onTouchEvent(paramMotionEvent);
        postDelayed(this.mLongPressCallback, 1000L);
        continue;
        if (this.mUpToSpeed)
        {
          startExitAnimation();
        }
        else
        {
          cancelLongClick();
          continue;
          cancelLongClick();
        }
      }
    }
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    if ((f1 < -this.mSlop) || (f2 < -this.mSlop)) {}
    for (;;)
    {
      cancelLongClick();
      break;
      if (f1 <= getWidth() + this.mSlop) {
        if (f2 <= getHeight() + this.mSlop) {
          break;
        }
      }
    }
  }
  
  protected void startAccelSpin()
  {
    cancelAnimation();
    this.mAnimator = ObjectAnimator.ofFloat(this, View.ROTATION, new float[] { 0.0F, 360.0F });
    this.mAnimator.setInterpolator(AnimationUtils.loadInterpolator(this.mContext, 17563648));
    this.mAnimator.setDuration(750L);
    this.mAnimator.addListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator) {}
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        SettingsButton.this.startContinuousSpin();
      }
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator) {}
    });
    this.mAnimator.start();
  }
  
  protected void startContinuousSpin()
  {
    cancelAnimation();
    performHapticFeedback(0);
    this.mUpToSpeed = true;
    this.mAnimator = ObjectAnimator.ofFloat(this, View.ROTATION, new float[] { 0.0F, 360.0F });
    this.mAnimator.setInterpolator(Interpolators.LINEAR);
    this.mAnimator.setDuration(375L);
    this.mAnimator.setRepeatCount(-1);
    this.mAnimator.start();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\SettingsButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */