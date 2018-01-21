package com.android.systemui.stackdivider;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import com.android.systemui.Interpolators;

public class DividerHandleView
  extends View
{
  private static final Property<DividerHandleView, Integer> HEIGHT_PROPERTY = new Property(Integer.class, "height")
  {
    public Integer get(DividerHandleView paramAnonymousDividerHandleView)
    {
      return Integer.valueOf(DividerHandleView.-get0(paramAnonymousDividerHandleView));
    }
    
    public void set(DividerHandleView paramAnonymousDividerHandleView, Integer paramAnonymousInteger)
    {
      DividerHandleView.-set1(paramAnonymousDividerHandleView, paramAnonymousInteger.intValue());
      paramAnonymousDividerHandleView.invalidate();
    }
  };
  private static final Property<DividerHandleView, Integer> WIDTH_PROPERTY = new Property(Integer.class, "width")
  {
    public Integer get(DividerHandleView paramAnonymousDividerHandleView)
    {
      return Integer.valueOf(DividerHandleView.-get1(paramAnonymousDividerHandleView));
    }
    
    public void set(DividerHandleView paramAnonymousDividerHandleView, Integer paramAnonymousInteger)
    {
      DividerHandleView.-set2(paramAnonymousDividerHandleView, paramAnonymousInteger.intValue());
      paramAnonymousDividerHandleView.invalidate();
    }
  };
  private AnimatorSet mAnimator;
  private final int mCircleDiameter;
  private int mCurrentHeight;
  private int mCurrentWidth;
  private final int mHeight;
  private final Paint mPaint = new Paint();
  private boolean mTouching;
  private final int mWidth;
  
  public DividerHandleView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mPaint.setColor(getResources().getColor(2131493055, null));
    this.mPaint.setAntiAlias(true);
    this.mWidth = getResources().getDimensionPixelSize(2131755578);
    this.mHeight = getResources().getDimensionPixelSize(2131755579);
    this.mCurrentWidth = this.mWidth;
    this.mCurrentHeight = this.mHeight;
    this.mCircleDiameter = ((this.mWidth + this.mHeight) / 3);
  }
  
  private void animateToTarget(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    Object localObject1 = ObjectAnimator.ofInt(this, WIDTH_PROPERTY, new int[] { this.mCurrentWidth, paramInt1 });
    Object localObject2 = ObjectAnimator.ofInt(this, HEIGHT_PROPERTY, new int[] { this.mCurrentHeight, paramInt2 });
    this.mAnimator = new AnimatorSet();
    this.mAnimator.playTogether(new Animator[] { localObject1, localObject2 });
    localObject1 = this.mAnimator;
    long l;
    if (paramBoolean)
    {
      l = 150L;
      ((AnimatorSet)localObject1).setDuration(l);
      localObject2 = this.mAnimator;
      if (!paramBoolean) {
        break label154;
      }
    }
    label154:
    for (localObject1 = Interpolators.TOUCH_RESPONSE;; localObject1 = Interpolators.FAST_OUT_SLOW_IN)
    {
      ((AnimatorSet)localObject2).setInterpolator((TimeInterpolator)localObject1);
      this.mAnimator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          DividerHandleView.-set0(DividerHandleView.this, null);
        }
      });
      this.mAnimator.start();
      return;
      l = 200L;
      break;
    }
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int i = getWidth() / 2 - this.mCurrentWidth / 2;
    int j = getHeight() / 2 - this.mCurrentHeight / 2;
    int k = Math.min(this.mCurrentWidth, this.mCurrentHeight) / 2;
    paramCanvas.drawRoundRect(i, j, this.mCurrentWidth + i, this.mCurrentHeight + j, k, k, this.mPaint);
  }
  
  public void setTouching(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1 == this.mTouching) {
      return;
    }
    if (this.mAnimator != null)
    {
      this.mAnimator.cancel();
      this.mAnimator = null;
    }
    if (!paramBoolean2)
    {
      if (paramBoolean1) {
        this.mCurrentWidth = this.mCircleDiameter;
      }
      for (this.mCurrentHeight = this.mCircleDiameter;; this.mCurrentHeight = this.mHeight)
      {
        invalidate();
        this.mTouching = paramBoolean1;
        return;
        this.mCurrentWidth = this.mWidth;
      }
    }
    int i;
    if (paramBoolean1)
    {
      i = this.mCircleDiameter;
      label90:
      if (!paramBoolean1) {
        break label119;
      }
    }
    label119:
    for (int j = this.mCircleDiameter;; j = this.mHeight)
    {
      animateToTarget(i, j, paramBoolean1);
      break;
      i = this.mWidth;
      break label90;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\stackdivider\DividerHandleView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */