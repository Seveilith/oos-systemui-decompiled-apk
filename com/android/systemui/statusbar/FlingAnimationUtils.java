package com.android.systemui.statusbar;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.android.systemui.Interpolators;

public class FlingAnimationUtils
{
  private AnimatorProperties mAnimatorProperties = new AnimatorProperties(null);
  private float mHighVelocityPxPerSecond;
  private Interpolator mLinearOutSlowIn;
  private float mMaxLengthSeconds;
  private float mMinVelocityPxPerSecond;
  
  public FlingAnimationUtils(Context paramContext, float paramFloat)
  {
    this.mMaxLengthSeconds = paramFloat;
    this.mLinearOutSlowIn = new PathInterpolator(0.0F, 0.0F, 0.35F, 1.0F);
    this.mMinVelocityPxPerSecond = (paramContext.getResources().getDisplayMetrics().density * 250.0F);
    this.mHighVelocityPxPerSecond = (paramContext.getResources().getDisplayMetrics().density * 3000.0F);
  }
  
  private float calculateLinearOutFasterInY2(float paramFloat)
  {
    paramFloat = Math.max(0.0F, Math.min(1.0F, (paramFloat - this.mMinVelocityPxPerSecond) / (this.mHighVelocityPxPerSecond - this.mMinVelocityPxPerSecond)));
    return (1.0F - paramFloat) * 0.4F + 0.5F * paramFloat;
  }
  
  private AnimatorProperties getDismissingProperties(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    paramFloat4 = (float)(this.mMaxLengthSeconds * Math.pow(Math.abs(paramFloat2 - paramFloat1) / paramFloat4, 0.5D));
    paramFloat2 = Math.abs(paramFloat2 - paramFloat1);
    paramFloat3 = Math.abs(paramFloat3);
    paramFloat1 = calculateLinearOutFasterInY2(paramFloat3);
    float f = paramFloat1 / 0.5F;
    Object localObject = new PathInterpolator(0.0F, 0.0F, 0.5F, paramFloat1);
    paramFloat1 = f * paramFloat2 / paramFloat3;
    if (paramFloat1 <= paramFloat4) {
      this.mAnimatorProperties.interpolator = ((Interpolator)localObject);
    }
    for (;;)
    {
      this.mAnimatorProperties.duration = ((1000.0F * paramFloat1));
      return this.mAnimatorProperties;
      if (paramFloat3 >= this.mMinVelocityPxPerSecond)
      {
        paramFloat1 = paramFloat4;
        localObject = new InterpolatorInterpolator(new VelocityInterpolator(paramFloat4, paramFloat3, paramFloat2, null), (Interpolator)localObject, this.mLinearOutSlowIn);
        this.mAnimatorProperties.interpolator = ((Interpolator)localObject);
      }
      else
      {
        paramFloat1 = paramFloat4;
        this.mAnimatorProperties.interpolator = Interpolators.FAST_OUT_LINEAR_IN;
      }
    }
  }
  
  private AnimatorProperties getProperties(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    paramFloat4 = (float)(this.mMaxLengthSeconds * Math.sqrt(Math.abs(paramFloat2 - paramFloat1) / paramFloat4));
    paramFloat2 = Math.abs(paramFloat2 - paramFloat1);
    paramFloat3 = Math.abs(paramFloat3);
    paramFloat1 = 2.857143F * paramFloat2 / paramFloat3;
    if (paramFloat1 <= paramFloat4) {
      this.mAnimatorProperties.interpolator = this.mLinearOutSlowIn;
    }
    for (;;)
    {
      this.mAnimatorProperties.duration = ((1000.0F * paramFloat1));
      return this.mAnimatorProperties;
      if (paramFloat3 >= this.mMinVelocityPxPerSecond)
      {
        paramFloat1 = paramFloat4;
        InterpolatorInterpolator localInterpolatorInterpolator = new InterpolatorInterpolator(new VelocityInterpolator(paramFloat4, paramFloat3, paramFloat2, null), this.mLinearOutSlowIn, this.mLinearOutSlowIn);
        this.mAnimatorProperties.interpolator = localInterpolatorInterpolator;
      }
      else
      {
        paramFloat1 = paramFloat4;
        this.mAnimatorProperties.interpolator = Interpolators.FAST_OUT_SLOW_IN;
      }
    }
  }
  
  public void apply(Animator paramAnimator, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    apply(paramAnimator, paramFloat1, paramFloat2, paramFloat3, Math.abs(paramFloat2 - paramFloat1));
  }
  
  public void apply(Animator paramAnimator, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    AnimatorProperties localAnimatorProperties = getProperties(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    paramAnimator.setDuration(localAnimatorProperties.duration);
    paramAnimator.setInterpolator(localAnimatorProperties.interpolator);
  }
  
  public void apply(ViewPropertyAnimator paramViewPropertyAnimator, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    apply(paramViewPropertyAnimator, paramFloat1, paramFloat2, paramFloat3, Math.abs(paramFloat2 - paramFloat1));
  }
  
  public void apply(ViewPropertyAnimator paramViewPropertyAnimator, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    AnimatorProperties localAnimatorProperties = getProperties(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    paramViewPropertyAnimator.setDuration(localAnimatorProperties.duration);
    paramViewPropertyAnimator.setInterpolator(localAnimatorProperties.interpolator);
  }
  
  public void applyDismissing(Animator paramAnimator, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    AnimatorProperties localAnimatorProperties = getDismissingProperties(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    paramAnimator.setDuration(localAnimatorProperties.duration);
    paramAnimator.setInterpolator(localAnimatorProperties.interpolator);
  }
  
  public float getMinVelocityPxPerSecond()
  {
    return this.mMinVelocityPxPerSecond;
  }
  
  private static class AnimatorProperties
  {
    long duration;
    Interpolator interpolator;
  }
  
  private static final class InterpolatorInterpolator
    implements Interpolator
  {
    private Interpolator mCrossfader;
    private Interpolator mInterpolator1;
    private Interpolator mInterpolator2;
    
    InterpolatorInterpolator(Interpolator paramInterpolator1, Interpolator paramInterpolator2, Interpolator paramInterpolator3)
    {
      this.mInterpolator1 = paramInterpolator1;
      this.mInterpolator2 = paramInterpolator2;
      this.mCrossfader = paramInterpolator3;
    }
    
    public float getInterpolation(float paramFloat)
    {
      float f = this.mCrossfader.getInterpolation(paramFloat);
      return (1.0F - f) * this.mInterpolator1.getInterpolation(paramFloat) + this.mInterpolator2.getInterpolation(paramFloat) * f;
    }
  }
  
  private static final class VelocityInterpolator
    implements Interpolator
  {
    private float mDiff;
    private float mDurationSeconds;
    private float mVelocity;
    
    private VelocityInterpolator(float paramFloat1, float paramFloat2, float paramFloat3)
    {
      this.mDurationSeconds = paramFloat1;
      this.mVelocity = paramFloat2;
      this.mDiff = paramFloat3;
    }
    
    public float getInterpolation(float paramFloat)
    {
      float f = this.mDurationSeconds;
      return this.mVelocity * (paramFloat * f) / this.mDiff;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\FlingAnimationUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */