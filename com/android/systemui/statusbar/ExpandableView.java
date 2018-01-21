package com.android.systemui.statusbar;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class ExpandableView
  extends FrameLayout
{
  private static Rect mClipRect = new Rect();
  private int mActualHeight;
  private boolean mChangingPosition = false;
  private boolean mClipToActualHeight = true;
  protected int mClipTopAmount;
  private boolean mDark;
  private ArrayList<View> mMatchParentViews = new ArrayList();
  private int mMinClipTopAmount = 0;
  protected OnHeightChangedListener mOnHeightChangedListener;
  private ViewGroup mTransientContainer;
  private boolean mWillBeGone;
  
  public ExpandableView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void updateClipping()
  {
    if (this.mClipToActualHeight)
    {
      int j = getClipTopAmount();
      int i = j;
      if (j >= getActualHeight()) {
        i = getActualHeight() - 1;
      }
      mClipRect.set(0, i, getWidth(), getActualHeight() + getExtraBottomPadding());
      setClipBounds(mClipRect);
      return;
    }
    setClipBounds(null);
  }
  
  public boolean areChildrenExpanded()
  {
    return false;
  }
  
  public int getActualHeight()
  {
    return this.mActualHeight;
  }
  
  public void getBoundsOnScreen(Rect paramRect, boolean paramBoolean)
  {
    super.getBoundsOnScreen(paramRect, paramBoolean);
    if (getTop() + getTranslationY() < 0.0F) {
      paramRect.top = ((int)(paramRect.top + (getTop() + getTranslationY())));
    }
    paramRect.bottom = (paramRect.top + getActualHeight());
    paramRect.top += getClipTopAmount();
  }
  
  public int getClipTopAmount()
  {
    return this.mClipTopAmount;
  }
  
  public int getCollapsedHeight()
  {
    return getHeight();
  }
  
  public void getDrawingRect(Rect paramRect)
  {
    super.getDrawingRect(paramRect);
    paramRect.left = ((int)(paramRect.left + getTranslationX()));
    paramRect.right = ((int)(paramRect.right + getTranslationX()));
    paramRect.bottom = ((int)(paramRect.top + getTranslationY() + getActualHeight()));
    paramRect.top = ((int)(paramRect.top + (getTranslationY() + getClipTopAmount())));
  }
  
  public int getExtraBottomPadding()
  {
    return 0;
  }
  
  public float getIncreasedPaddingAmount()
  {
    return 0.0F;
  }
  
  public int getIntrinsicHeight()
  {
    return getHeight();
  }
  
  public int getMaxContentHeight()
  {
    return getHeight();
  }
  
  public int getMinHeight()
  {
    return getHeight();
  }
  
  public float getOutlineAlpha()
  {
    return 0.0F;
  }
  
  public int getOutlineTranslation()
  {
    return 0;
  }
  
  public float getShadowAlpha()
  {
    return 0.0F;
  }
  
  public ViewGroup getTransientContainer()
  {
    return this.mTransientContainer;
  }
  
  public float getTranslation()
  {
    return getTranslationX();
  }
  
  public boolean hasOverlappingRendering()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (super.hasOverlappingRendering())
    {
      bool1 = bool2;
      if (getActualHeight() <= getHeight()) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean isChangingPosition()
  {
    return this.mChangingPosition;
  }
  
  public boolean isChildInGroup()
  {
    return false;
  }
  
  public boolean isContentExpandable()
  {
    return false;
  }
  
  public boolean isDark()
  {
    return this.mDark;
  }
  
  public boolean isGroupExpanded()
  {
    return false;
  }
  
  public boolean isGroupExpansionChanging()
  {
    return false;
  }
  
  public boolean isSummaryWithChildren()
  {
    return false;
  }
  
  public boolean isTransparent()
  {
    return false;
  }
  
  public boolean mustStayOnScreen()
  {
    return false;
  }
  
  public void notifyHeightChanged(boolean paramBoolean)
  {
    if (this.mOnHeightChangedListener != null) {
      this.mOnHeightChangedListener.onHeightChanged(this, paramBoolean);
    }
  }
  
  public void onHeightReset()
  {
    if (this.mOnHeightChangedListener != null) {
      this.mOnHeightChangedListener.onReset(this);
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    updateClipping();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int m = View.MeasureSpec.getSize(paramInt2);
    int j = Integer.MAX_VALUE;
    int i1 = View.MeasureSpec.getMode(paramInt2);
    int i = j;
    if (i1 != 0)
    {
      i = j;
      if (m != 0) {
        i = Math.min(m, Integer.MAX_VALUE);
      }
    }
    int n = View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE);
    int k = 0;
    int i2 = getChildCount();
    j = 0;
    Object localObject1;
    Object localObject2;
    if (j < i2)
    {
      localObject1 = getChildAt(j);
      if (((View)localObject1).getVisibility() == 8) {}
      for (;;)
      {
        j += 1;
        break;
        paramInt2 = n;
        localObject2 = ((View)localObject1).getLayoutParams();
        if (((ViewGroup.LayoutParams)localObject2).height != -1)
        {
          if (((ViewGroup.LayoutParams)localObject2).height >= 0) {
            if (((ViewGroup.LayoutParams)localObject2).height <= i) {
              break label168;
            }
          }
          label168:
          for (paramInt2 = View.MeasureSpec.makeMeasureSpec(i, 1073741824);; paramInt2 = View.MeasureSpec.makeMeasureSpec(((ViewGroup.LayoutParams)localObject2).height, 1073741824))
          {
            ((View)localObject1).measure(getChildMeasureSpec(paramInt1, 0, ((ViewGroup.LayoutParams)localObject2).width), paramInt2);
            k = Math.max(k, ((View)localObject1).getMeasuredHeight());
            break;
          }
        }
        this.mMatchParentViews.add(localObject1);
      }
    }
    if (i1 == 1073741824) {}
    for (paramInt2 = m;; paramInt2 = Math.min(i, k))
    {
      i = View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824);
      localObject1 = this.mMatchParentViews.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (View)((Iterator)localObject1).next();
        ((View)localObject2).measure(getChildMeasureSpec(paramInt1, 0, ((View)localObject2).getLayoutParams().width), i);
      }
    }
    this.mMatchParentViews.clear();
    setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), paramInt2);
  }
  
  public abstract void performAddAnimation(long paramLong1, long paramLong2);
  
  public abstract void performRemoveAnimation(long paramLong, float paramFloat, Runnable paramRunnable);
  
  public boolean pointInView(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    boolean bool2 = false;
    float f1 = this.mClipTopAmount;
    float f2 = this.mActualHeight;
    boolean bool1 = bool2;
    if (paramFloat1 >= -paramFloat3)
    {
      bool1 = bool2;
      if (paramFloat2 >= f1 - paramFloat3)
      {
        bool1 = bool2;
        if (paramFloat1 < this.mRight - this.mLeft + paramFloat3)
        {
          bool1 = bool2;
          if (paramFloat2 < f2 + paramFloat3) {
            bool1 = true;
          }
        }
      }
    }
    return bool1;
  }
  
  public void setActualHeight(int paramInt)
  {
    setActualHeight(paramInt, true);
  }
  
  public void setActualHeight(int paramInt, boolean paramBoolean)
  {
    this.mActualHeight = paramInt;
    updateClipping();
    if (paramBoolean) {
      notifyHeightChanged(false);
    }
  }
  
  public void setActualHeightAnimating(boolean paramBoolean) {}
  
  public void setBelowSpeedBump(boolean paramBoolean) {}
  
  public void setChangingPosition(boolean paramBoolean)
  {
    this.mChangingPosition = paramBoolean;
  }
  
  public void setClipToActualHeight(boolean paramBoolean)
  {
    this.mClipToActualHeight = paramBoolean;
    updateClipping();
  }
  
  public void setClipTopAmount(int paramInt)
  {
    this.mClipTopAmount = paramInt;
    updateClipping();
  }
  
  public void setDark(boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    this.mDark = paramBoolean1;
  }
  
  public void setDimmed(boolean paramBoolean1, boolean paramBoolean2) {}
  
  public void setFakeShadowIntensity(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2) {}
  
  public void setHideSensitive(boolean paramBoolean1, boolean paramBoolean2, long paramLong1, long paramLong2) {}
  
  public void setHideSensitiveForIntrinsicHeight(boolean paramBoolean) {}
  
  public void setLayerType(int paramInt, Paint paramPaint)
  {
    if (hasOverlappingRendering()) {
      super.setLayerType(paramInt, paramPaint);
    }
  }
  
  public void setMinClipTopAmount(int paramInt)
  {
    this.mMinClipTopAmount = paramInt;
  }
  
  public void setOnHeightChangedListener(OnHeightChangedListener paramOnHeightChangedListener)
  {
    this.mOnHeightChangedListener = paramOnHeightChangedListener;
  }
  
  public void setShadowAlpha(float paramFloat) {}
  
  public void setTransientContainer(ViewGroup paramViewGroup)
  {
    this.mTransientContainer = paramViewGroup;
  }
  
  public void setTranslation(float paramFloat)
  {
    setTranslationX(paramFloat);
  }
  
  public void setWillBeGone(boolean paramBoolean)
  {
    this.mWillBeGone = paramBoolean;
  }
  
  public boolean willBeGone()
  {
    return this.mWillBeGone;
  }
  
  public static abstract interface OnHeightChangedListener
  {
    public abstract void onHeightChanged(ExpandableView paramExpandableView, boolean paramBoolean);
    
    public abstract void onReset(ExpandableView paramExpandableView);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\ExpandableView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */