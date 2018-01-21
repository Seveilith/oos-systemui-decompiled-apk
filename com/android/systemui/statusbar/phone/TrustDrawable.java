package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.animation.Interpolator;
import com.android.systemui.Interpolators;

public class TrustDrawable
  extends Drawable
{
  private int mAlpha;
  private final ValueAnimator.AnimatorUpdateListener mAlphaUpdateListener = new ValueAnimator.AnimatorUpdateListener()
  {
    public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
    {
      TrustDrawable.-set0(TrustDrawable.this, ((Integer)paramAnonymousValueAnimator.getAnimatedValue()).intValue());
      TrustDrawable.this.invalidateSelf();
    }
  };
  private boolean mAnimating;
  private int mCurAlpha;
  private Animator mCurAnimator;
  private float mCurInnerRadius;
  private final float mInnerRadiusEnter;
  private final float mInnerRadiusExit;
  private final float mInnerRadiusVisibleMax;
  private final float mInnerRadiusVisibleMin;
  private Paint mPaint;
  private final ValueAnimator.AnimatorUpdateListener mRadiusUpdateListener = new ValueAnimator.AnimatorUpdateListener()
  {
    public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
    {
      TrustDrawable.-set1(TrustDrawable.this, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
      TrustDrawable.this.invalidateSelf();
    }
  };
  private int mState = -1;
  private final float mThickness;
  private boolean mTrustManaged;
  private final Animator mVisibleAnimator;
  
  public TrustDrawable(Context paramContext)
  {
    paramContext = paramContext.getResources();
    this.mInnerRadiusVisibleMin = paramContext.getDimension(2131755525);
    this.mInnerRadiusVisibleMax = paramContext.getDimension(2131755526);
    this.mInnerRadiusExit = paramContext.getDimension(2131755527);
    this.mInnerRadiusEnter = paramContext.getDimension(2131755528);
    this.mThickness = paramContext.getDimension(2131755529);
    this.mCurInnerRadius = this.mInnerRadiusEnter;
    this.mVisibleAnimator = makeVisibleAnimator();
    this.mPaint = new Paint();
    this.mPaint.setStyle(Paint.Style.STROKE);
    this.mPaint.setColor(-1);
    this.mPaint.setAntiAlias(true);
    this.mPaint.setStrokeWidth(this.mThickness);
  }
  
  private ValueAnimator configureAnimator(ValueAnimator paramValueAnimator, long paramLong, ValueAnimator.AnimatorUpdateListener paramAnimatorUpdateListener, Interpolator paramInterpolator, boolean paramBoolean)
  {
    paramValueAnimator.setDuration(paramLong);
    paramValueAnimator.addUpdateListener(paramAnimatorUpdateListener);
    paramValueAnimator.setInterpolator(paramInterpolator);
    if (paramBoolean)
    {
      paramValueAnimator.setRepeatCount(-1);
      paramValueAnimator.setRepeatMode(2);
    }
    return paramValueAnimator;
  }
  
  private Animator makeAnimators(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, long paramLong, Interpolator paramInterpolator, boolean paramBoolean1, boolean paramBoolean2)
  {
    ValueAnimator localValueAnimator = configureAnimator(ValueAnimator.ofInt(new int[] { paramInt1, paramInt2 }), paramLong, this.mAlphaUpdateListener, paramInterpolator, paramBoolean1);
    paramInterpolator = configureAnimator(ValueAnimator.ofFloat(new float[] { paramFloat1, paramFloat2 }), paramLong, this.mRadiusUpdateListener, paramInterpolator, paramBoolean1);
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { localValueAnimator, paramInterpolator });
    if (paramBoolean2) {
      localAnimatorSet.addListener(new StateUpdateAnimatorListener(null));
    }
    return localAnimatorSet;
  }
  
  private Animator makeEnterAnimator(float paramFloat, int paramInt)
  {
    return makeAnimators(paramFloat, this.mInnerRadiusVisibleMax, paramInt, 76, 500L, Interpolators.LINEAR_OUT_SLOW_IN, false, true);
  }
  
  private Animator makeExitAnimator(float paramFloat, int paramInt)
  {
    return makeAnimators(paramFloat, this.mInnerRadiusExit, paramInt, 0, 500L, Interpolators.FAST_OUT_SLOW_IN, false, true);
  }
  
  private Animator makeVisibleAnimator()
  {
    return makeAnimators(this.mInnerRadiusVisibleMax, this.mInnerRadiusVisibleMin, 76, 38, 1000L, Interpolators.ACCELERATE_DECELERATE, true, false);
  }
  
  private void updateState(boolean paramBoolean)
  {
    if (!this.mAnimating) {
      return;
    }
    int j = this.mState;
    int i;
    int k;
    if (this.mState == -1) {
      if (this.mTrustManaged)
      {
        i = 1;
        k = i;
        if (!paramBoolean)
        {
          j = i;
          if (i == 1) {
            j = 2;
          }
          k = j;
          if (j == 3) {
            k = 0;
          }
        }
        if (k != this.mState)
        {
          if (this.mCurAnimator != null)
          {
            this.mCurAnimator.cancel();
            this.mCurAnimator = null;
          }
          if (k != 0) {
            break label222;
          }
          this.mCurAlpha = 0;
          this.mCurInnerRadius = this.mInnerRadiusEnter;
        }
      }
    }
    for (;;)
    {
      this.mState = k;
      if (this.mCurAnimator != null) {
        this.mCurAnimator.start();
      }
      invalidateSelf();
      return;
      i = 0;
      break;
      if (this.mState == 0)
      {
        i = j;
        if (!this.mTrustManaged) {
          break;
        }
        i = 1;
        break;
      }
      if (this.mState == 1)
      {
        i = j;
        if (this.mTrustManaged) {
          break;
        }
        i = 3;
        break;
      }
      if (this.mState == 2)
      {
        i = j;
        if (this.mTrustManaged) {
          break;
        }
        i = 3;
        break;
      }
      i = j;
      if (this.mState != 3) {
        break;
      }
      i = j;
      if (!this.mTrustManaged) {
        break;
      }
      i = 1;
      break;
      label222:
      if (k == 1)
      {
        this.mCurAnimator = makeEnterAnimator(this.mCurInnerRadius, this.mCurAlpha);
        if (this.mState == -1) {
          this.mCurAnimator.setStartDelay(200L);
        }
      }
      else if (k == 2)
      {
        this.mCurAlpha = 76;
        this.mCurInnerRadius = this.mInnerRadiusVisibleMax;
        this.mCurAnimator = this.mVisibleAnimator;
      }
      else if (k == 3)
      {
        this.mCurAnimator = makeExitAnimator(this.mCurInnerRadius, this.mCurAlpha);
      }
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    int i = this.mCurAlpha * this.mAlpha / 256;
    if (i == 0) {
      return;
    }
    Rect localRect = getBounds();
    this.mPaint.setAlpha(i);
    paramCanvas.drawCircle(localRect.exactCenterX(), localRect.exactCenterY(), this.mCurInnerRadius, this.mPaint);
  }
  
  public int getAlpha()
  {
    return this.mAlpha;
  }
  
  public int getOpacity()
  {
    return -3;
  }
  
  public void setAlpha(int paramInt)
  {
    this.mAlpha = paramInt;
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    throw new UnsupportedOperationException("not implemented");
  }
  
  public void setTrustManaged(boolean paramBoolean)
  {
    if ((paramBoolean == this.mTrustManaged) && (this.mState != -1)) {
      return;
    }
    this.mTrustManaged = paramBoolean;
    updateState(true);
  }
  
  public void start()
  {
    if (!this.mAnimating)
    {
      this.mAnimating = true;
      updateState(true);
      invalidateSelf();
    }
  }
  
  public void stop()
  {
    if (this.mAnimating)
    {
      this.mAnimating = false;
      if (this.mCurAnimator != null)
      {
        this.mCurAnimator.cancel();
        this.mCurAnimator = null;
      }
      this.mState = -1;
      this.mCurAlpha = 0;
      this.mCurInnerRadius = this.mInnerRadiusEnter;
      invalidateSelf();
    }
  }
  
  private class StateUpdateAnimatorListener
    extends AnimatorListenerAdapter
  {
    boolean mCancelled;
    
    private StateUpdateAnimatorListener() {}
    
    public void onAnimationCancel(Animator paramAnimator)
    {
      this.mCancelled = true;
    }
    
    public void onAnimationEnd(Animator paramAnimator)
    {
      if (!this.mCancelled) {
        TrustDrawable.-wrap0(TrustDrawable.this, false);
      }
    }
    
    public void onAnimationStart(Animator paramAnimator)
    {
      this.mCancelled = false;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\TrustDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */