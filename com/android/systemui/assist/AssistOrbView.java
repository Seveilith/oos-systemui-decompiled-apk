package com.android.systemui.assist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.android.systemui.Interpolators;

public class AssistOrbView
  extends FrameLayout
{
  private final Paint mBackgroundPaint = new Paint();
  private final int mBaseMargin;
  private float mCircleAnimationEndValue;
  private ValueAnimator mCircleAnimator;
  private final int mCircleMinSize;
  private final Rect mCircleRect = new Rect();
  private float mCircleSize;
  private ValueAnimator.AnimatorUpdateListener mCircleUpdateListener = new ValueAnimator.AnimatorUpdateListener()
  {
    public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
    {
      AssistOrbView.-wrap1(AssistOrbView.this, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
      AssistOrbView.-wrap2(AssistOrbView.this);
    }
  };
  private AnimatorListenerAdapter mClearAnimatorListener = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      AssistOrbView.-set0(AssistOrbView.this, null);
    }
  };
  private boolean mClipToOutline;
  private ImageView mLogo;
  private final int mMaxElevation;
  private float mOffset;
  private ValueAnimator mOffsetAnimator;
  private ValueAnimator.AnimatorUpdateListener mOffsetUpdateListener = new ValueAnimator.AnimatorUpdateListener()
  {
    public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
    {
      AssistOrbView.-set1(AssistOrbView.this, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
      AssistOrbView.-wrap3(AssistOrbView.this);
    }
  };
  private float mOutlineAlpha;
  private final Interpolator mOvershootInterpolator = new OvershootInterpolator();
  private final int mStaticOffset;
  private final Rect mStaticRect = new Rect();
  
  public AssistOrbView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AssistOrbView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public AssistOrbView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public AssistOrbView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setOutlineProvider(new ViewOutlineProvider()
    {
      public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
      {
        if (AssistOrbView.-get2(AssistOrbView.this) > 0.0F) {
          paramAnonymousOutline.setOval(AssistOrbView.-get1(AssistOrbView.this));
        }
        for (;;)
        {
          paramAnonymousOutline.setAlpha(AssistOrbView.-get3(AssistOrbView.this));
          return;
          paramAnonymousOutline.setEmpty();
        }
      }
    });
    setWillNotDraw(false);
    this.mCircleMinSize = paramContext.getResources().getDimensionPixelSize(2131755513);
    this.mBaseMargin = paramContext.getResources().getDimensionPixelSize(2131755514);
    this.mStaticOffset = paramContext.getResources().getDimensionPixelSize(2131755515);
    this.mMaxElevation = paramContext.getResources().getDimensionPixelSize(2131755516);
    this.mBackgroundPaint.setAntiAlias(true);
    this.mBackgroundPaint.setColor(getResources().getColor(2131493036));
  }
  
  private void animateOffset(float paramFloat, long paramLong1, long paramLong2, Interpolator paramInterpolator)
  {
    if (this.mOffsetAnimator != null)
    {
      this.mOffsetAnimator.removeAllListeners();
      this.mOffsetAnimator.cancel();
    }
    this.mOffsetAnimator = ValueAnimator.ofFloat(new float[] { this.mOffset, paramFloat });
    this.mOffsetAnimator.addUpdateListener(this.mOffsetUpdateListener);
    this.mOffsetAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        AssistOrbView.-set2(AssistOrbView.this, null);
      }
    });
    this.mOffsetAnimator.setInterpolator(paramInterpolator);
    this.mOffsetAnimator.setStartDelay(paramLong2);
    this.mOffsetAnimator.setDuration(paramLong1);
    this.mOffsetAnimator.start();
  }
  
  private void applyCircleSize(float paramFloat)
  {
    this.mCircleSize = paramFloat;
    updateLayout();
  }
  
  private void drawBackground(Canvas paramCanvas)
  {
    paramCanvas.drawCircle(this.mCircleRect.centerX(), this.mCircleRect.centerY(), this.mCircleSize / 2.0F, this.mBackgroundPaint);
  }
  
  private void updateCircleRect()
  {
    updateCircleRect(this.mCircleRect, this.mOffset, false);
  }
  
  private void updateCircleRect(Rect paramRect, float paramFloat, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (float f = this.mCircleMinSize;; f = this.mCircleSize)
    {
      int i = (int)(getWidth() - f) / 2;
      int j = (int)(getHeight() - f / 2.0F - this.mBaseMargin - paramFloat);
      paramRect.set(i, j, (int)(i + f), (int)(j + f));
      return;
    }
  }
  
  private void updateClipping()
  {
    if (this.mCircleSize < this.mCircleMinSize) {}
    for (boolean bool = true;; bool = false)
    {
      if (bool != this.mClipToOutline)
      {
        setClipToOutline(bool);
        this.mClipToOutline = bool;
      }
      return;
    }
  }
  
  private void updateElevation()
  {
    setElevation((1.0F - Math.max((this.mStaticOffset - this.mOffset) / this.mStaticOffset, 0.0F)) * this.mMaxElevation);
  }
  
  private void updateLayout()
  {
    updateCircleRect();
    updateLogo();
    invalidateOutline();
    invalidate();
    updateClipping();
  }
  
  private void updateLogo()
  {
    float f1 = (this.mCircleRect.left + this.mCircleRect.right) / 2.0F;
    float f2 = this.mLogo.getWidth() / 2.0F;
    float f3 = (this.mCircleRect.top + this.mCircleRect.bottom) / 2.0F;
    float f4 = this.mLogo.getHeight() / 2.0F;
    float f5 = this.mCircleMinSize / 7.0F;
    float f6 = (this.mStaticOffset - this.mOffset) / this.mStaticOffset;
    float f7 = this.mStaticOffset;
    float f8 = Math.max((1.0F - f6 - 0.5F) * 2.0F, 0.0F);
    this.mLogo.setImageAlpha((int)(255.0F * f8));
    this.mLogo.setTranslationX(f1 - f2);
    this.mLogo.setTranslationY(f3 - f4 - f5 + f7 * f6 * 0.1F);
  }
  
  public void animateCircleSize(float paramFloat, long paramLong1, long paramLong2, Interpolator paramInterpolator)
  {
    if (paramFloat == this.mCircleAnimationEndValue) {
      return;
    }
    if (this.mCircleAnimator != null) {
      this.mCircleAnimator.cancel();
    }
    this.mCircleAnimator = ValueAnimator.ofFloat(new float[] { this.mCircleSize, paramFloat });
    this.mCircleAnimator.addUpdateListener(this.mCircleUpdateListener);
    this.mCircleAnimator.addListener(this.mClearAnimatorListener);
    this.mCircleAnimator.setInterpolator(paramInterpolator);
    this.mCircleAnimator.setDuration(paramLong1);
    this.mCircleAnimator.setStartDelay(paramLong2);
    this.mCircleAnimator.start();
    this.mCircleAnimationEndValue = paramFloat;
  }
  
  public ImageView getLogo()
  {
    return this.mLogo;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    drawBackground(paramCanvas);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mLogo = ((ImageView)findViewById(2131951791));
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mLogo.layout(0, 0, this.mLogo.getMeasuredWidth(), this.mLogo.getMeasuredHeight());
    if (paramBoolean) {
      updateCircleRect(this.mStaticRect, this.mStaticOffset, true);
    }
  }
  
  public void reset()
  {
    this.mClipToOutline = false;
    this.mBackgroundPaint.setAlpha(255);
    this.mOutlineAlpha = 1.0F;
  }
  
  public void startEnterAnimation()
  {
    applyCircleSize(0.0F);
    post(new Runnable()
    {
      public void run()
      {
        AssistOrbView.this.animateCircleSize(AssistOrbView.-get0(AssistOrbView.this), 300L, 0L, AssistOrbView.-get4(AssistOrbView.this));
        AssistOrbView.-wrap0(AssistOrbView.this, AssistOrbView.-get5(AssistOrbView.this), 400L, 0L, Interpolators.LINEAR_OUT_SLOW_IN);
      }
    });
  }
  
  public void startExitAnimation(long paramLong)
  {
    animateCircleSize(0.0F, 200L, paramLong, Interpolators.FAST_OUT_LINEAR_IN);
    animateOffset(0.0F, 200L, paramLong, Interpolators.FAST_OUT_LINEAR_IN);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\assist\AssistOrbView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */