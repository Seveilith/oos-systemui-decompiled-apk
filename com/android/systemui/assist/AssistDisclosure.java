package com.android.systemui.assist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.AnimatorSet.Builder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import com.android.systemui.Interpolators;

public class AssistDisclosure
{
  private final Context mContext;
  private final Handler mHandler;
  private Runnable mShowRunnable = new Runnable()
  {
    public void run()
    {
      AssistDisclosure.-wrap1(AssistDisclosure.this);
    }
  };
  private AssistDisclosureView mView;
  private boolean mViewAdded;
  private final WindowManager mWm;
  
  public AssistDisclosure(Context paramContext, Handler paramHandler)
  {
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    this.mWm = ((WindowManager)this.mContext.getSystemService(WindowManager.class));
  }
  
  private void hide()
  {
    if (this.mViewAdded)
    {
      this.mWm.removeView(this.mView);
      this.mViewAdded = false;
    }
  }
  
  private void show()
  {
    if (this.mView == null) {
      this.mView = new AssistDisclosureView(this.mContext);
    }
    if (!this.mViewAdded)
    {
      WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(2015, 17302792, -3);
      localLayoutParams.setTitle("AssistDisclosure");
      this.mWm.addView(this.mView, localLayoutParams);
      this.mViewAdded = true;
    }
  }
  
  public void postShow()
  {
    this.mHandler.removeCallbacks(this.mShowRunnable);
    this.mHandler.post(this.mShowRunnable);
  }
  
  private class AssistDisclosureView
    extends View
    implements ValueAnimator.AnimatorUpdateListener
  {
    private int mAlpha = 0;
    private final ValueAnimator mAlphaInAnimator = ValueAnimator.ofInt(new int[] { 0, 222 }).setDuration(400L);
    private final ValueAnimator mAlphaOutAnimator;
    private final AnimatorSet mAnimator;
    private final Paint mPaint = new Paint();
    private final Paint mShadowPaint = new Paint();
    private float mShadowThickness;
    private float mThickness;
    
    public AssistDisclosureView(Context paramContext)
    {
      super();
      this.mAlphaInAnimator.addUpdateListener(this);
      this.mAlphaInAnimator.setInterpolator(Interpolators.CUSTOM_40_40);
      this.mAlphaOutAnimator = ValueAnimator.ofInt(new int[] { 222, 0 }).setDuration(300L);
      this.mAlphaOutAnimator.addUpdateListener(this);
      this.mAlphaOutAnimator.setInterpolator(Interpolators.CUSTOM_40_40);
      this.mAnimator = new AnimatorSet();
      this.mAnimator.play(this.mAlphaInAnimator).before(this.mAlphaOutAnimator);
      this.mAnimator.addListener(new AnimatorListenerAdapter()
      {
        boolean mCancelled;
        
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          this.mCancelled = true;
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (!this.mCancelled) {
            AssistDisclosure.-wrap0(AssistDisclosure.this);
          }
        }
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          this.mCancelled = false;
        }
      });
      this$1 = new PorterDuffXfermode(PorterDuff.Mode.SRC);
      this.mPaint.setColor(-1);
      this.mPaint.setXfermode(AssistDisclosure.this);
      this.mShadowPaint.setColor(-12303292);
      this.mShadowPaint.setXfermode(AssistDisclosure.this);
      this.mThickness = getResources().getDimension(2131755571);
      this.mShadowThickness = getResources().getDimension(2131755572);
    }
    
    private void drawBeam(Canvas paramCanvas, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, Paint paramPaint, float paramFloat5)
    {
      paramCanvas.drawRect(paramFloat1 - paramFloat5, paramFloat2 - paramFloat5, paramFloat3 + paramFloat5, paramFloat4 + paramFloat5, paramPaint);
    }
    
    private void drawGeometry(Canvas paramCanvas, Paint paramPaint, float paramFloat)
    {
      int i = getWidth();
      int j = getHeight();
      float f = this.mThickness;
      drawBeam(paramCanvas, 0.0F, j - f, i, j, paramPaint, paramFloat);
      drawBeam(paramCanvas, 0.0F, 0.0F, f, j - f, paramPaint, paramFloat);
      drawBeam(paramCanvas, i - f, 0.0F, i, j - f, paramPaint, paramFloat);
      drawBeam(paramCanvas, f, 0.0F, i - f, f, paramPaint, paramFloat);
    }
    
    private void startAnimation()
    {
      this.mAnimator.cancel();
      this.mAnimator.start();
    }
    
    public void onAnimationUpdate(ValueAnimator paramValueAnimator)
    {
      if (paramValueAnimator == this.mAlphaOutAnimator) {
        this.mAlpha = ((Integer)this.mAlphaOutAnimator.getAnimatedValue()).intValue();
      }
      for (;;)
      {
        invalidate();
        return;
        if (paramValueAnimator == this.mAlphaInAnimator) {
          this.mAlpha = ((Integer)this.mAlphaInAnimator.getAnimatedValue()).intValue();
        }
      }
    }
    
    protected void onAttachedToWindow()
    {
      super.onAttachedToWindow();
      startAnimation();
      sendAccessibilityEvent(16777216);
    }
    
    protected void onDetachedFromWindow()
    {
      super.onDetachedFromWindow();
      this.mAnimator.cancel();
      this.mAlpha = 0;
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      this.mPaint.setAlpha(this.mAlpha);
      this.mShadowPaint.setAlpha(this.mAlpha / 4);
      drawGeometry(paramCanvas, this.mShadowPaint, this.mShadowThickness);
      drawGeometry(paramCanvas, this.mPaint, 0.0F);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\assist\AssistDisclosure.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */