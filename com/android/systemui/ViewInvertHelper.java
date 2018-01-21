package com.android.systemui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.View;
import java.util.ArrayList;

public class ViewInvertHelper
{
  private final Paint mDarkPaint = new Paint();
  private final long mFadeDuration;
  private final ColorMatrix mGrayscaleMatrix = new ColorMatrix();
  private final ColorMatrix mMatrix = new ColorMatrix();
  private final ArrayList<View> mTargets = new ArrayList();
  
  public ViewInvertHelper(Context paramContext, long paramLong)
  {
    this.mFadeDuration = paramLong;
  }
  
  public ViewInvertHelper(View paramView, long paramLong)
  {
    this(paramView.getContext(), paramLong);
    addTarget(paramView);
  }
  
  private void updateInvertPaint(float paramFloat)
  {
    float f = 1.0F - 2.0F * paramFloat;
    this.mMatrix.set(new float[] { f, 0.0F, 0.0F, 0.0F, 255.0F * paramFloat, 0.0F, f, 0.0F, 0.0F, 255.0F * paramFloat, 0.0F, 0.0F, f, 0.0F, 255.0F * paramFloat, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F });
    this.mGrayscaleMatrix.setSaturation(1.0F - paramFloat);
    this.mMatrix.preConcat(this.mGrayscaleMatrix);
    this.mDarkPaint.setColorFilter(new ColorMatrixColorFilter(this.mMatrix));
  }
  
  public void addTarget(View paramView)
  {
    this.mTargets.add(paramView);
  }
  
  public void clearTargets()
  {
    this.mTargets.clear();
  }
  
  public void fade(final boolean paramBoolean, long paramLong)
  {
    float f1;
    if (paramBoolean)
    {
      f1 = 0.0F;
      if (!paramBoolean) {
        break label95;
      }
    }
    label95:
    for (float f2 = 1.0F;; f2 = 0.0F)
    {
      ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { f1, f2 });
      localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          ViewInvertHelper.-wrap0(ViewInvertHelper.this, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
          int i = 0;
          while (i < ViewInvertHelper.-get1(ViewInvertHelper.this).size())
          {
            ((View)ViewInvertHelper.-get1(ViewInvertHelper.this).get(i)).setLayerType(2, ViewInvertHelper.-get0(ViewInvertHelper.this));
            i += 1;
          }
        }
      });
      localValueAnimator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (!paramBoolean)
          {
            int i = 0;
            while (i < ViewInvertHelper.-get1(ViewInvertHelper.this).size())
            {
              ((View)ViewInvertHelper.-get1(ViewInvertHelper.this).get(i)).setLayerType(0, null);
              i += 1;
            }
          }
        }
      });
      localValueAnimator.setDuration(this.mFadeDuration);
      localValueAnimator.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
      localValueAnimator.setStartDelay(paramLong);
      localValueAnimator.start();
      return;
      f1 = 1.0F;
      break;
    }
  }
  
  public void setInverted(boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    if (paramBoolean2)
    {
      fade(paramBoolean1, paramLong);
      return;
    }
    update(paramBoolean1);
  }
  
  public void update(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      updateInvertPaint(1.0F);
      i = 0;
      while (i < this.mTargets.size())
      {
        ((View)this.mTargets.get(i)).setLayerType(2, this.mDarkPaint);
        i += 1;
      }
    }
    int i = 0;
    while (i < this.mTargets.size())
    {
      ((View)this.mTargets.get(i)).setLayerType(0, null);
      i += 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\ViewInvertHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */