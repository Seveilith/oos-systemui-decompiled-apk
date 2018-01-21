package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

public class ScrimView
  extends View
{
  private ValueAnimator mAlphaAnimator;
  private ValueAnimator.AnimatorUpdateListener mAlphaUpdateListener = new ValueAnimator.AnimatorUpdateListener()
  {
    public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
    {
      ScrimView.-set1(ScrimView.this, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
      ScrimView.this.invalidate();
    }
  };
  private Runnable mChangeRunnable;
  private AnimatorListenerAdapter mClearAnimatorListener = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      ScrimView.-set0(ScrimView.this, null);
    }
  };
  private boolean mDrawAsSrc;
  private Rect mExcludedRect = new Rect();
  private boolean mHasExcludedArea;
  private boolean mIsEmpty = true;
  private int mLeftInset = 0;
  private final Paint mPaint = new Paint();
  private int mScrimColor;
  private float mViewAlpha = 1.0F;
  
  public ScrimView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ScrimView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ScrimView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ScrimView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public void animateViewAlpha(float paramFloat, long paramLong, Interpolator paramInterpolator)
  {
    if (this.mAlphaAnimator != null) {
      this.mAlphaAnimator.cancel();
    }
    this.mAlphaAnimator = ValueAnimator.ofFloat(new float[] { this.mViewAlpha, paramFloat });
    this.mAlphaAnimator.addUpdateListener(this.mAlphaUpdateListener);
    this.mAlphaAnimator.addListener(this.mClearAnimatorListener);
    this.mAlphaAnimator.setInterpolator(paramInterpolator);
    this.mAlphaAnimator.setDuration(paramLong);
    this.mAlphaAnimator.start();
  }
  
  public int getScrimColorWithAlpha()
  {
    int i = this.mScrimColor;
    return Color.argb((int)(Color.alpha(i) * this.mViewAlpha), Color.red(i), Color.green(i), Color.blue(i));
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    PorterDuff.Mode localMode;
    int i;
    if ((this.mDrawAsSrc) || ((!this.mIsEmpty) && (this.mViewAlpha > 0.0F)))
    {
      if (!this.mDrawAsSrc) {
        break label53;
      }
      localMode = PorterDuff.Mode.SRC;
      i = getScrimColorWithAlpha();
      if (this.mHasExcludedArea) {
        break label60;
      }
      paramCanvas.drawColor(i, localMode);
    }
    label53:
    label60:
    do
    {
      return;
      localMode = PorterDuff.Mode.SRC_OVER;
      break;
      this.mPaint.setColor(i);
      if (this.mExcludedRect.top > 0) {
        paramCanvas.drawRect(0.0F, 0.0F, getWidth(), this.mExcludedRect.top, this.mPaint);
      }
      if (this.mExcludedRect.left + this.mLeftInset > 0) {
        paramCanvas.drawRect(0.0F, this.mExcludedRect.top, this.mExcludedRect.left + this.mLeftInset, this.mExcludedRect.bottom, this.mPaint);
      }
      if (this.mExcludedRect.right + this.mLeftInset < getWidth()) {
        paramCanvas.drawRect(this.mExcludedRect.right + this.mLeftInset, this.mExcludedRect.top, getWidth(), this.mExcludedRect.bottom, this.mPaint);
      }
    } while (this.mExcludedRect.bottom >= getHeight());
    paramCanvas.drawRect(0.0F, this.mExcludedRect.bottom, getWidth(), getHeight(), this.mPaint);
  }
  
  public void setChangeRunnable(Runnable paramRunnable)
  {
    this.mChangeRunnable = paramRunnable;
  }
  
  public void setDrawAsSrc(boolean paramBoolean)
  {
    this.mDrawAsSrc = paramBoolean;
    Paint localPaint = this.mPaint;
    if (this.mDrawAsSrc) {}
    for (PorterDuff.Mode localMode = PorterDuff.Mode.SRC;; localMode = PorterDuff.Mode.SRC_OVER)
    {
      localPaint.setXfermode(new PorterDuffXfermode(localMode));
      invalidate();
      return;
    }
  }
  
  public void setExcludedArea(Rect paramRect)
  {
    boolean bool2 = false;
    if (paramRect == null)
    {
      this.mHasExcludedArea = false;
      invalidate();
      return;
    }
    int i = Math.max(paramRect.left, 0);
    int j = Math.max(paramRect.top, 0);
    int k = Math.min(paramRect.right, getWidth());
    int m = Math.min(paramRect.bottom, getHeight());
    this.mExcludedRect.set(i, j, k, m);
    boolean bool1 = bool2;
    if (i < k)
    {
      bool1 = bool2;
      if (j < m) {
        bool1 = true;
      }
    }
    this.mHasExcludedArea = bool1;
    invalidate();
  }
  
  public void setLeftInset(int paramInt)
  {
    if (this.mLeftInset != paramInt)
    {
      this.mLeftInset = paramInt;
      if (this.mHasExcludedArea) {
        invalidate();
      }
    }
  }
  
  public void setScrimColor(int paramInt)
  {
    boolean bool = false;
    if (paramInt != this.mScrimColor)
    {
      if (Color.alpha(paramInt) == 0) {
        bool = true;
      }
      this.mIsEmpty = bool;
      this.mScrimColor = paramInt;
      invalidate();
      if (this.mChangeRunnable != null) {
        this.mChangeRunnable.run();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\ScrimView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */