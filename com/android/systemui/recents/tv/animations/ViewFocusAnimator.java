package com.android.systemui.recents.tv.animations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import com.android.systemui.recents.tv.views.TaskCardView;

public class ViewFocusAnimator
  implements View.OnFocusChangeListener
{
  private final int mAnimDuration;
  private final float mDismissIconAlpha;
  ObjectAnimator mFocusAnimation;
  private final Interpolator mFocusInterpolator;
  private float mFocusProgress;
  private final float mSelectedScale;
  private final float mSelectedScaleDelta;
  private final float mSelectedSpacingDelta;
  private final float mSelectedZDelta;
  protected TaskCardView mTargetView;
  private final float mUnselectedScale;
  private final float mUnselectedSpacing;
  private final float mUnselectedZ;
  
  public ViewFocusAnimator(TaskCardView paramTaskCardView)
  {
    this.mTargetView = paramTaskCardView;
    paramTaskCardView = paramTaskCardView.getResources();
    this.mTargetView.setOnFocusChangeListener(this);
    TypedValue localTypedValue = new TypedValue();
    paramTaskCardView.getValue(2131624035, localTypedValue, true);
    this.mUnselectedScale = localTypedValue.getFloat();
    paramTaskCardView.getValue(2131624036, localTypedValue, true);
    this.mSelectedScale = localTypedValue.getFloat();
    this.mSelectedScaleDelta = (this.mSelectedScale - this.mUnselectedScale);
    this.mUnselectedZ = paramTaskCardView.getDimensionPixelOffset(2131755658);
    this.mSelectedZDelta = paramTaskCardView.getDimensionPixelOffset(2131755659);
    this.mUnselectedSpacing = paramTaskCardView.getDimensionPixelOffset(2131755656);
    this.mSelectedSpacingDelta = paramTaskCardView.getDimensionPixelOffset(2131755657);
    this.mAnimDuration = paramTaskCardView.getInteger(2131624024);
    this.mFocusInterpolator = new AccelerateDecelerateInterpolator();
    this.mFocusAnimation = ObjectAnimator.ofFloat(this, "focusProgress", new float[] { 0.0F });
    this.mFocusAnimation.setDuration(this.mAnimDuration);
    this.mFocusAnimation.setInterpolator(this.mFocusInterpolator);
    this.mDismissIconAlpha = paramTaskCardView.getFloat(2131624037);
    setFocusProgress(0.0F);
    this.mFocusAnimation.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        ViewFocusAnimator.this.mTargetView.setHasTransientState(false);
      }
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        ViewFocusAnimator.this.mTargetView.setHasTransientState(true);
      }
    });
  }
  
  private void animateFocus(boolean paramBoolean)
  {
    if (this.mFocusAnimation.isStarted()) {
      this.mFocusAnimation.cancel();
    }
    if (paramBoolean) {}
    for (float f = 1.0F;; f = 0.0F)
    {
      if (this.mFocusProgress != f)
      {
        this.mFocusAnimation.setFloatValues(new float[] { this.mFocusProgress, f });
        this.mFocusAnimation.start();
      }
      return;
    }
  }
  
  private void setFocusProgress(float paramFloat)
  {
    this.mFocusProgress = paramFloat;
    float f1 = this.mUnselectedScale + this.mSelectedScaleDelta * paramFloat;
    float f2 = this.mUnselectedZ + this.mSelectedZDelta * paramFloat;
    float f3 = this.mUnselectedSpacing + this.mSelectedSpacingDelta * paramFloat;
    this.mTargetView.setScaleX(f1);
    this.mTargetView.setScaleY(f1);
    this.mTargetView.setPadding((int)f3, this.mTargetView.getPaddingTop(), (int)f3, this.mTargetView.getPaddingBottom());
    this.mTargetView.getDismissIconView().setAlpha(this.mDismissIconAlpha * paramFloat);
    this.mTargetView.getThumbnailView().setZ(f2);
    this.mTargetView.getDismissIconView().setZ(f2);
  }
  
  public void changeSize(boolean paramBoolean)
  {
    ViewGroup.LayoutParams localLayoutParams = this.mTargetView.getLayoutParams();
    int i = localLayoutParams.width;
    int j = localLayoutParams.height;
    if ((i < 0) && (j < 0)) {
      this.mTargetView.measure(0, 0);
    }
    if ((this.mTargetView.isAttachedToWindow()) && (this.mTargetView.hasWindowFocus()) && (this.mTargetView.getVisibility() == 0))
    {
      animateFocus(paramBoolean);
      return;
    }
    if (this.mFocusAnimation.isStarted()) {
      this.mFocusAnimation.cancel();
    }
    if (paramBoolean) {}
    for (float f = 1.0F;; f = 0.0F)
    {
      setFocusProgress(f);
      return;
    }
  }
  
  public void onFocusChange(View paramView, boolean paramBoolean)
  {
    if (paramView != this.mTargetView) {
      return;
    }
    changeSize(paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\tv\animations\ViewFocusAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */