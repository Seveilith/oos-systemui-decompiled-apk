package com.android.systemui.qs;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import com.android.systemui.util.ThemeColorUtils;
import java.util.ArrayList;

public class PageIndicator
  extends ViewGroup
{
  private boolean mAnimating;
  private final Runnable mAnimationDone = new Runnable()
  {
    public void run()
    {
      PageIndicator.-set0(PageIndicator.this, false);
      if (PageIndicator.-get0(PageIndicator.this).size() != 0) {
        PageIndicator.-wrap0(PageIndicator.this, ((Integer)PageIndicator.-get0(PageIndicator.this).remove(0)).intValue());
      }
    }
  };
  private final int mColor = ThemeColorUtils.getColor(ThemeColorUtils.QS_PRIMARY_TEXT);
  private final int mPageDotWidth = (int)(this.mPageIndicatorWidth * 0.4F);
  private final int mPageIndicatorHeight = (int)this.mContext.getResources().getDimension(2131755419);
  private final int mPageIndicatorWidth = (int)this.mContext.getResources().getDimension(2131755418);
  private int mPosition = -1;
  private final ArrayList<Integer> mQueuedPositions = new ArrayList();
  
  public PageIndicator(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void animate(int paramInt1, int paramInt2)
  {
    int j = paramInt1 >> 1;
    int k = paramInt2 >> 1;
    setIndex(j);
    boolean bool1;
    if ((paramInt1 & 0x1) != 0)
    {
      bool1 = true;
      if (!bool1) {
        break label108;
      }
      if (paramInt1 <= paramInt2) {
        break label102;
      }
    }
    boolean bool2;
    ImageView localImageView1;
    ImageView localImageView2;
    for (;;)
    {
      bool2 = true;
      int i = Math.min(j, k);
      paramInt2 = Math.max(j, k);
      paramInt1 = paramInt2;
      if (paramInt2 == i) {
        paramInt1 = paramInt2 + 1;
      }
      localImageView1 = (ImageView)getChildAt(i);
      localImageView2 = (ImageView)getChildAt(paramInt1);
      if ((localImageView1 != null) && (localImageView2 != null)) {
        break label116;
      }
      return;
      bool1 = false;
      break;
      label102:
      label108:
      do
      {
        bool2 = false;
        break;
      } while (paramInt1 >= paramInt2);
    }
    label116:
    localImageView2.setTranslationX(localImageView1.getX() - localImageView2.getX());
    playAnimation(localImageView1, getTransition(bool1, bool2, false));
    localImageView1.setAlpha(getAlpha(false));
    playAnimation(localImageView2, getTransition(bool1, bool2, true));
    localImageView2.setAlpha(getAlpha(true));
    this.mAnimating = true;
  }
  
  private float getAlpha(boolean paramBoolean)
  {
    if (paramBoolean) {
      return 1.0F;
    }
    return 0.3F;
  }
  
  private int getTransition(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (paramBoolean3)
    {
      if (paramBoolean1)
      {
        if (paramBoolean2) {
          return 2130838032;
        }
        return 2130838034;
      }
      if (paramBoolean2) {
        return 2130838030;
      }
      return 2130838036;
    }
    if (paramBoolean1)
    {
      if (paramBoolean2) {
        return 2130838042;
      }
      return 2130838040;
    }
    if (paramBoolean2) {
      return 2130838044;
    }
    return 2130838038;
  }
  
  private void playAnimation(ImageView paramImageView, int paramInt)
  {
    AnimatedVectorDrawable localAnimatedVectorDrawable = (AnimatedVectorDrawable)getContext().getDrawable(paramInt);
    paramImageView.setImageDrawable(localAnimatedVectorDrawable);
    localAnimatedVectorDrawable.forceAnimationOnUI();
    localAnimatedVectorDrawable.start();
    postDelayed(this.mAnimationDone, 250L);
  }
  
  private void setIndex(int paramInt)
  {
    int j = getChildCount();
    int i = 0;
    if (i < j)
    {
      ImageView localImageView = (ImageView)getChildAt(i);
      localImageView.setTranslationX(0.0F);
      localImageView.setImageResource(2130838029);
      localImageView.setColorFilter(this.mColor);
      if (i == paramInt) {}
      for (boolean bool = true;; bool = false)
      {
        localImageView.setAlpha(getAlpha(bool));
        i += 1;
        break;
      }
    }
  }
  
  private void setPosition(int paramInt)
  {
    if ((isVisibleToUser()) && (Math.abs(this.mPosition - paramInt) == 1)) {
      animate(this.mPosition, paramInt);
    }
    for (;;)
    {
      this.mPosition = paramInt;
      return;
      setIndex(paramInt >> 1);
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt2 = getChildCount();
    if (paramInt2 == 0) {
      return;
    }
    paramInt1 = 0;
    while (paramInt1 < paramInt2)
    {
      paramInt3 = (this.mPageIndicatorWidth - this.mPageDotWidth) * paramInt1;
      getChildAt(paramInt1).layout(paramInt3, 0, this.mPageIndicatorWidth + paramInt3, this.mPageIndicatorHeight);
      paramInt1 += 1;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = getChildCount();
    if (i == 0)
    {
      super.onMeasure(paramInt1, paramInt2);
      return;
    }
    paramInt2 = View.MeasureSpec.makeMeasureSpec(this.mPageIndicatorWidth, 1073741824);
    int j = View.MeasureSpec.makeMeasureSpec(this.mPageIndicatorHeight, 1073741824);
    paramInt1 = 0;
    while (paramInt1 < i)
    {
      getChildAt(paramInt1).measure(paramInt2, j);
      paramInt1 += 1;
    }
    setMeasuredDimension((this.mPageIndicatorWidth - this.mPageDotWidth) * i + this.mPageDotWidth, this.mPageIndicatorHeight);
  }
  
  public void setLocation(float paramFloat)
  {
    int i = 1;
    int j = (int)paramFloat;
    setContentDescription(getContext().getString(2131690639, new Object[] { Integer.valueOf(j + 1), Integer.valueOf(getChildCount()) }));
    if (paramFloat != j) {}
    for (;;)
    {
      j = j << 1 | i;
      i = this.mPosition;
      if (this.mQueuedPositions.size() != 0) {
        i = ((Integer)this.mQueuedPositions.get(this.mQueuedPositions.size() - 1)).intValue();
      }
      if (j != i) {
        break;
      }
      return;
      i = 0;
    }
    if (this.mAnimating)
    {
      this.mQueuedPositions.add(Integer.valueOf(j));
      return;
    }
    setPosition(j);
  }
  
  public void setNumPages(int paramInt)
  {
    if (paramInt > 1) {}
    for (int i = 0;; i = 4)
    {
      setVisibility(i);
      if (this.mAnimating) {
        Log.w("PageIndicator", "setNumPages during animation");
      }
      while (paramInt < getChildCount()) {
        removeViewAt(getChildCount() - 1);
      }
    }
    while (paramInt > getChildCount())
    {
      ImageView localImageView = new ImageView(this.mContext);
      localImageView.setImageResource(2130838037);
      addView(localImageView, new ViewGroup.LayoutParams(this.mPageIndicatorWidth, this.mPageIndicatorHeight));
    }
    setIndex(this.mPosition >> 1);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\PageIndicator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */