package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.CanvasProperty;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DisplayListCanvas;
import android.view.RenderNodeAnimator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import com.android.systemui.Interpolators;

public class KeyguardAffordanceView
  extends ImageView
{
  private ValueAnimator mAlphaAnimator;
  private AnimatorListenerAdapter mAlphaEndListener = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      KeyguardAffordanceView.-set0(KeyguardAffordanceView.this, null);
    }
  };
  private int mCenterX;
  private int mCenterY;
  private ValueAnimator mCircleAnimator;
  private int mCircleColor;
  private AnimatorListenerAdapter mCircleEndListener = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      KeyguardAffordanceView.-set1(KeyguardAffordanceView.this, null);
    }
  };
  private final Paint mCirclePaint = new Paint();
  private float mCircleRadius;
  private float mCircleStartRadius;
  private float mCircleStartValue;
  private boolean mCircleWillBeHidden;
  private AnimatorListenerAdapter mClipEndListener = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      KeyguardAffordanceView.-set5(KeyguardAffordanceView.this, null);
    }
  };
  private final ArgbEvaluator mColorInterpolator;
  private boolean mFinishing;
  private final FlingAnimationUtils mFlingAnimationUtils;
  private CanvasProperty<Float> mHwCenterX;
  private CanvasProperty<Float> mHwCenterY;
  private CanvasProperty<Paint> mHwCirclePaint;
  private CanvasProperty<Float> mHwCircleRadius;
  private float mImageScale = 1.0F;
  private final int mInverseColor;
  private boolean mLaunchingAffordance;
  private float mMaxCircleSize;
  private final int mMinBackgroundRadius;
  private final int mNormalColor;
  private Animator mPreviewClipper;
  private View mPreviewView;
  private float mRestingAlpha = 0.5F;
  private ValueAnimator mScaleAnimator;
  private AnimatorListenerAdapter mScaleEndListener = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      KeyguardAffordanceView.-set6(KeyguardAffordanceView.this, null);
    }
  };
  private boolean mSupportHardware;
  private int[] mTempPoint = new int[2];
  
  public KeyguardAffordanceView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public KeyguardAffordanceView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public KeyguardAffordanceView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public KeyguardAffordanceView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mCirclePaint.setAntiAlias(true);
    this.mCircleColor = -1;
    this.mCirclePaint.setColor(this.mCircleColor);
    this.mNormalColor = -1;
    this.mInverseColor = -16777216;
    this.mMinBackgroundRadius = this.mContext.getResources().getDimensionPixelSize(2131755474);
    this.mColorInterpolator = new ArgbEvaluator();
    this.mFlingAnimationUtils = new FlingAnimationUtils(this.mContext, 0.3F);
  }
  
  private void cancelAnimator(Animator paramAnimator)
  {
    if (paramAnimator != null) {
      paramAnimator.cancel();
    }
  }
  
  private void drawBackgroundCircle(Canvas paramCanvas)
  {
    if ((this.mCircleRadius > 0.0F) || (this.mFinishing))
    {
      if ((this.mFinishing) && (this.mSupportHardware) && (this.mHwCenterX != null)) {
        ((DisplayListCanvas)paramCanvas).drawCircle(this.mHwCenterX, this.mHwCenterY, this.mHwCircleRadius, this.mHwCirclePaint);
      }
    }
    else {
      return;
    }
    if ((this.mCircleColor != this.mNormalColor) && (this.mCircleRadius != this.mMaxCircleSize)) {
      this.mCircleColor = this.mNormalColor;
    }
    updateCircleColor();
    paramCanvas.drawCircle(this.mCenterX, this.mCenterY, this.mCircleRadius, this.mCirclePaint);
  }
  
  private ValueAnimator getAnimatorToRadius(float paramFloat)
  {
    boolean bool = true;
    ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { this.mCircleRadius, paramFloat });
    this.mCircleAnimator = localValueAnimator;
    this.mCircleStartValue = this.mCircleRadius;
    if (paramFloat == 0.0F) {}
    for (;;)
    {
      this.mCircleWillBeHidden = bool;
      localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          KeyguardAffordanceView.-set2(KeyguardAffordanceView.this, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
          KeyguardAffordanceView.-wrap0(KeyguardAffordanceView.this);
          KeyguardAffordanceView.this.invalidate();
        }
      });
      localValueAnimator.addListener(this.mCircleEndListener);
      return localValueAnimator;
      bool = false;
    }
  }
  
  private Animator.AnimatorListener getEndListener(final Runnable paramRunnable)
  {
    new AnimatorListenerAdapter()
    {
      boolean mCancelled;
      
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        this.mCancelled = true;
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if (!this.mCancelled) {
          paramRunnable.run();
        }
      }
    };
  }
  
  private float getMaxCircleSize()
  {
    getLocationInWindow(this.mTempPoint);
    float f1 = getRootView().getWidth();
    float f2 = this.mTempPoint[0] + this.mCenterX;
    f1 = Math.max(f1 - f2, f2);
    f2 = this.mTempPoint[1] + this.mCenterY;
    return (float)Math.hypot(f1, f2);
  }
  
  private Animator getRtAnimatorToRadius(float paramFloat)
  {
    RenderNodeAnimator localRenderNodeAnimator = new RenderNodeAnimator(this.mHwCircleRadius, paramFloat);
    localRenderNodeAnimator.setTarget(this);
    return localRenderNodeAnimator;
  }
  
  private void initHwProperties()
  {
    this.mHwCenterX = CanvasProperty.createFloat(this.mCenterX);
    this.mHwCenterY = CanvasProperty.createFloat(this.mCenterY);
    this.mHwCirclePaint = CanvasProperty.createPaint(this.mCirclePaint);
    this.mHwCircleRadius = CanvasProperty.createFloat(this.mCircleRadius);
  }
  
  private void setCircleRadius(float paramFloat, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i;
    int j;
    if ((this.mCircleAnimator == null) || (!this.mCircleWillBeHidden))
    {
      if ((this.mCircleAnimator != null) || (this.mCircleRadius != 0.0F)) {
        break label108;
      }
      i = 1;
      if (paramFloat != 0.0F) {
        break label114;
      }
      j = 1;
      label42:
      if ((i != j) && (!paramBoolean2)) {
        break label120;
      }
      i = 0;
      label56:
      if (i != 0) {
        break label186;
      }
      if (this.mCircleAnimator != null) {
        break label126;
      }
      this.mCircleRadius = paramFloat;
      updateIconColor();
      invalidate();
      if ((j != 0) && (this.mPreviewView != null)) {
        this.mPreviewView.setVisibility(4);
      }
    }
    label108:
    label114:
    label120:
    label126:
    while (this.mCircleWillBeHidden)
    {
      return;
      i = 1;
      break;
      i = 0;
      break;
      j = 0;
      break label42;
      i = 1;
      break label56;
    }
    float f = this.mMinBackgroundRadius;
    this.mCircleAnimator.getValues()[0].setFloatValues(new float[] { this.mCircleStartValue + (paramFloat - f), paramFloat });
    this.mCircleAnimator.setCurrentPlayTime(this.mCircleAnimator.getCurrentPlayTime());
    return;
    label186:
    cancelAnimator(this.mCircleAnimator);
    cancelAnimator(this.mPreviewClipper);
    ValueAnimator localValueAnimator = getAnimatorToRadius(paramFloat);
    if (paramFloat == 0.0F) {}
    for (Interpolator localInterpolator = Interpolators.FAST_OUT_LINEAR_IN;; localInterpolator = Interpolators.LINEAR_OUT_SLOW_IN)
    {
      localValueAnimator.setInterpolator(localInterpolator);
      long l = 250L;
      if (!paramBoolean1) {
        l = Math.min((80.0F * (Math.abs(this.mCircleRadius - paramFloat) / this.mMinBackgroundRadius)), 200L);
      }
      localValueAnimator.setDuration(l);
      localValueAnimator.start();
      if ((this.mPreviewView == null) || (this.mPreviewView.getVisibility() != 0)) {
        break;
      }
      this.mPreviewView.setVisibility(0);
      this.mPreviewClipper = ViewAnimationUtils.createCircularReveal(this.mPreviewView, getLeft() + this.mCenterX, getTop() + this.mCenterY, this.mCircleRadius, paramFloat);
      this.mPreviewClipper.setInterpolator(localInterpolator);
      this.mPreviewClipper.setDuration(l);
      this.mPreviewClipper.addListener(this.mClipEndListener);
      this.mPreviewClipper.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          KeyguardAffordanceView.-get0(KeyguardAffordanceView.this).setVisibility(4);
        }
      });
      this.mPreviewClipper.start();
      return;
    }
  }
  
  private void startRtAlphaFadeIn()
  {
    if ((this.mCircleRadius == 0.0F) && (this.mPreviewView == null))
    {
      Object localObject = new Paint(this.mCirclePaint);
      ((Paint)localObject).setColor(this.mCircleColor);
      ((Paint)localObject).setAlpha(0);
      this.mHwCirclePaint = CanvasProperty.createPaint((Paint)localObject);
      localObject = new RenderNodeAnimator(this.mHwCirclePaint, 1, 255.0F);
      ((RenderNodeAnimator)localObject).setTarget(this);
      ((RenderNodeAnimator)localObject).setInterpolator(Interpolators.ALPHA_IN);
      ((RenderNodeAnimator)localObject).setDuration(250L);
      ((RenderNodeAnimator)localObject).start();
    }
  }
  
  private void startRtCircleFadeOut(long paramLong)
  {
    RenderNodeAnimator localRenderNodeAnimator = new RenderNodeAnimator(this.mHwCirclePaint, 1, 0.0F);
    localRenderNodeAnimator.setDuration(paramLong);
    localRenderNodeAnimator.setInterpolator(Interpolators.ALPHA_OUT);
    localRenderNodeAnimator.setTarget(this);
    localRenderNodeAnimator.start();
  }
  
  private void updateCircleColor()
  {
    float f2 = 0.5F + Math.max(0.0F, Math.min(1.0F, (this.mCircleRadius - this.mMinBackgroundRadius) / (this.mMinBackgroundRadius * 0.5F))) * 0.5F;
    float f1 = f2;
    if (this.mPreviewView != null)
    {
      f1 = f2;
      if (this.mPreviewView.getVisibility() == 0) {
        f1 = f2 * (1.0F - Math.max(0.0F, this.mCircleRadius - this.mCircleStartRadius) / (this.mMaxCircleSize - this.mCircleStartRadius));
      }
    }
    int i = Color.argb((int)(Color.alpha(this.mCircleColor) * f1), Color.red(this.mCircleColor), Color.green(this.mCircleColor), Color.blue(this.mCircleColor));
    this.mCirclePaint.setColor(i);
  }
  
  private void updateIconColor()
  {
    Drawable localDrawable = getDrawable().mutate();
    float f = Math.min(1.0F, this.mCircleRadius / this.mMinBackgroundRadius);
    localDrawable.setColorFilter(((Integer)this.mColorInterpolator.evaluate(f, Integer.valueOf(this.mNormalColor), Integer.valueOf(this.mInverseColor))).intValue(), PorterDuff.Mode.SRC_ATOP);
  }
  
  public void finishAnimation(float paramFloat, final Runnable paramRunnable)
  {
    cancelAnimator(this.mCircleAnimator);
    cancelAnimator(this.mPreviewClipper);
    this.mFinishing = true;
    this.mCircleStartRadius = this.mCircleRadius;
    final float f = getMaxCircleSize();
    Object localObject;
    if (this.mSupportHardware)
    {
      initHwProperties();
      localObject = getRtAnimatorToRadius(f);
      startRtAlphaFadeIn();
    }
    for (;;)
    {
      this.mFlingAnimationUtils.applyDismissing((Animator)localObject, this.mCircleRadius, f, paramFloat, f);
      ((Animator)localObject).addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          paramRunnable.run();
          KeyguardAffordanceView.-set3(KeyguardAffordanceView.this, false);
          KeyguardAffordanceView.-set2(KeyguardAffordanceView.this, f);
          KeyguardAffordanceView.this.invalidate();
        }
      });
      ((Animator)localObject).start();
      setImageAlpha(0.0F, true);
      if (this.mPreviewView != null)
      {
        this.mPreviewView.setVisibility(0);
        this.mPreviewClipper = ViewAnimationUtils.createCircularReveal(this.mPreviewView, getLeft() + this.mCenterX, getTop() + this.mCenterY, this.mCircleRadius, f);
        this.mFlingAnimationUtils.applyDismissing(this.mPreviewClipper, this.mCircleRadius, f, paramFloat, f);
        this.mPreviewClipper.addListener(this.mClipEndListener);
        this.mPreviewClipper.start();
        if (this.mSupportHardware) {
          startRtCircleFadeOut(((Animator)localObject).getDuration());
        }
      }
      return;
      localObject = getAnimatorToRadius(f);
    }
  }
  
  public float getCircleRadius()
  {
    return this.mCircleRadius;
  }
  
  public float getRestingAlpha()
  {
    return this.mRestingAlpha;
  }
  
  public void instantFinishAnimation()
  {
    cancelAnimator(this.mPreviewClipper);
    if (this.mPreviewView != null)
    {
      this.mPreviewView.setClipBounds(null);
      this.mPreviewView.setVisibility(0);
    }
    this.mCircleRadius = getMaxCircleSize();
    setImageAlpha(0.0F, false);
    invalidate();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    this.mSupportHardware = paramCanvas.isHardwareAccelerated();
    drawBackgroundCircle(paramCanvas);
    paramCanvas.save();
    paramCanvas.scale(this.mImageScale, this.mImageScale, getWidth() / 2, getHeight() / 2);
    super.onDraw(paramCanvas);
    paramCanvas.restore();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.mCenterX = (getWidth() / 2);
    this.mCenterY = (getHeight() / 2);
    this.mMaxCircleSize = getMaxCircleSize();
  }
  
  protected void onVisibilityChanged(View paramView, int paramInt)
  {
    super.onVisibilityChanged(paramView, paramInt);
    if ((paramInt != 0) && (this.mCircleColor != this.mNormalColor)) {
      setCircleColorToInverse(false);
    }
  }
  
  public boolean performClick()
  {
    if (isClickable()) {
      return super.performClick();
    }
    return false;
  }
  
  public void setCircleColorToInverse(boolean paramBoolean)
  {
    if (paramBoolean) {
      Log.d("KeyguardAffordanceView", "setCircleColorToInverse");
    }
    for (this.mCircleColor = this.mInverseColor;; this.mCircleColor = this.mNormalColor)
    {
      updateCircleColor();
      return;
    }
  }
  
  public void setCircleRadius(float paramFloat, boolean paramBoolean)
  {
    setCircleRadius(paramFloat, paramBoolean, false);
  }
  
  public void setCircleRadiusWithoutAnimation(float paramFloat)
  {
    cancelAnimator(this.mCircleAnimator);
    setCircleRadius(paramFloat, false, true);
  }
  
  public void setImageAlpha(float paramFloat, boolean paramBoolean)
  {
    setImageAlpha(paramFloat, paramBoolean, -1L, null, null);
  }
  
  public void setImageAlpha(float paramFloat, boolean paramBoolean, long paramLong, Interpolator paramInterpolator, Runnable paramRunnable)
  {
    cancelAnimator(this.mAlphaAnimator);
    if (this.mLaunchingAffordance) {
      paramFloat = 0.0F;
    }
    int i = (int)(paramFloat * 255.0F);
    final Object localObject = getBackground();
    if (!paramBoolean)
    {
      if (localObject != null) {
        ((Drawable)localObject).mutate().setAlpha(i);
      }
      setImageAlpha(i);
      return;
    }
    int j = getImageAlpha();
    ValueAnimator localValueAnimator = ValueAnimator.ofInt(new int[] { j, i });
    this.mAlphaAnimator = localValueAnimator;
    localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        int i = ((Integer)paramAnonymousValueAnimator.getAnimatedValue()).intValue();
        if (localObject != null) {
          localObject.mutate().setAlpha(i);
        }
        KeyguardAffordanceView.this.setImageAlpha(i);
      }
    });
    localValueAnimator.addListener(this.mAlphaEndListener);
    localObject = paramInterpolator;
    if (paramInterpolator == null) {
      if (paramFloat != 0.0F) {
        break label203;
      }
    }
    label203:
    for (localObject = Interpolators.FAST_OUT_LINEAR_IN;; localObject = Interpolators.LINEAR_OUT_SLOW_IN)
    {
      localValueAnimator.setInterpolator((TimeInterpolator)localObject);
      long l = paramLong;
      if (paramLong == -1L) {
        l = (200.0F * Math.min(1.0F, Math.abs(j - i) / 255.0F));
      }
      localValueAnimator.setDuration(l);
      if (paramRunnable != null) {
        localValueAnimator.addListener(getEndListener(paramRunnable));
      }
      localValueAnimator.start();
      return;
    }
  }
  
  public void setImageScale(float paramFloat, boolean paramBoolean)
  {
    setImageScale(paramFloat, paramBoolean, -1L, null);
  }
  
  public void setImageScale(float paramFloat, boolean paramBoolean, long paramLong, Interpolator paramInterpolator)
  {
    cancelAnimator(this.mScaleAnimator);
    if (!paramBoolean)
    {
      this.mImageScale = paramFloat;
      invalidate();
      return;
    }
    ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { this.mImageScale, paramFloat });
    this.mScaleAnimator = localValueAnimator;
    localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        KeyguardAffordanceView.-set4(KeyguardAffordanceView.this, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
        KeyguardAffordanceView.this.invalidate();
      }
    });
    localValueAnimator.addListener(this.mScaleEndListener);
    Interpolator localInterpolator = paramInterpolator;
    if (paramInterpolator == null) {
      if (paramFloat != 0.0F) {
        break label145;
      }
    }
    label145:
    for (localInterpolator = Interpolators.FAST_OUT_LINEAR_IN;; localInterpolator = Interpolators.LINEAR_OUT_SLOW_IN)
    {
      localValueAnimator.setInterpolator(localInterpolator);
      long l = paramLong;
      if (paramLong == -1L) {
        l = (200.0F * Math.min(1.0F, Math.abs(this.mImageScale - paramFloat) / 0.19999999F));
      }
      localValueAnimator.setDuration(l);
      localValueAnimator.start();
      return;
    }
  }
  
  public void setLaunchingAffordance(boolean paramBoolean)
  {
    this.mLaunchingAffordance = paramBoolean;
  }
  
  public void setPreviewView(View paramView)
  {
    View localView = this.mPreviewView;
    this.mPreviewView = paramView;
    if (this.mPreviewView != null)
    {
      paramView = this.mPreviewView;
      if (!this.mLaunchingAffordance) {
        break label40;
      }
    }
    label40:
    for (int i = localView.getVisibility();; i = 4)
    {
      paramView.setVisibility(i);
      return;
    }
  }
  
  public void setRestingAlpha(float paramFloat)
  {
    this.mRestingAlpha = paramFloat;
    setImageAlpha(paramFloat, false);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\KeyguardAffordanceView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */