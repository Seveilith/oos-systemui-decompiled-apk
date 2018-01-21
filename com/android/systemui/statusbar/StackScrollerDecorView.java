package com.android.systemui.statusbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import com.android.systemui.Interpolators;

public abstract class StackScrollerDecorView
  extends ExpandableView
{
  private boolean mAnimating;
  protected View mContent;
  private boolean mIsVisible;
  
  public StackScrollerDecorView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void animateText(boolean paramBoolean, final Runnable paramRunnable)
  {
    if (paramBoolean != this.mIsVisible) {
      if (paramBoolean)
      {
        f = 1.0F;
        if (!paramBoolean) {
          break label74;
        }
        localInterpolator = Interpolators.ALPHA_IN;
        this.mAnimating = true;
        this.mContent.animate().alpha(f).setInterpolator(localInterpolator).setDuration(260L).withEndAction(new Runnable()
        {
          public void run()
          {
            StackScrollerDecorView.-set0(StackScrollerDecorView.this, false);
            if (paramRunnable != null) {
              paramRunnable.run();
            }
          }
        });
        this.mIsVisible = paramBoolean;
      }
    }
    label74:
    while (paramRunnable == null) {
      for (;;)
      {
        return;
        float f = 0.0F;
        continue;
        Interpolator localInterpolator = Interpolators.ALPHA_OUT;
      }
    }
    paramRunnable.run();
  }
  
  public void cancelAnimation()
  {
    this.mContent.animate().cancel();
  }
  
  protected abstract View findContentView();
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public boolean isTransparent()
  {
    return true;
  }
  
  public boolean isVisible()
  {
    if (!this.mIsVisible) {
      return this.mAnimating;
    }
    return true;
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mContent = findContentView();
    setInvisible();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    setOutlineProvider(null);
  }
  
  public void performAddAnimation(long paramLong1, long paramLong2)
  {
    performVisibilityAnimation(true);
  }
  
  public void performRemoveAnimation(long paramLong, float paramFloat, Runnable paramRunnable)
  {
    performVisibilityAnimation(false);
  }
  
  public void performVisibilityAnimation(boolean paramBoolean)
  {
    animateText(paramBoolean, null);
  }
  
  public void performVisibilityAnimation(boolean paramBoolean, Runnable paramRunnable)
  {
    animateText(paramBoolean, paramRunnable);
  }
  
  public void setInvisible()
  {
    this.mContent.setAlpha(0.0F);
    this.mIsVisible = false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\StackScrollerDecorView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */