package com.android.systemui;

import android.R.styleable;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;

public class ResizingSpace
  extends View
{
  private final int mHeight;
  private final int mWidth;
  
  public ResizingSpace(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    if (getVisibility() == 0) {
      setVisibility(4);
    }
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ViewGroup_Layout);
    this.mWidth = paramContext.getResourceId(0, 0);
    this.mHeight = paramContext.getResourceId(1, 0);
  }
  
  private static int getDefaultSize2(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getMode(paramInt2);
    paramInt2 = View.MeasureSpec.getSize(paramInt2);
    switch (i)
    {
    default: 
      return paramInt1;
    case 0: 
      return paramInt1;
    case -2147483648: 
      return Math.min(paramInt1, paramInt2);
    }
    return paramInt2;
  }
  
  public void draw(Canvas paramCanvas) {}
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    paramConfiguration = getLayoutParams();
    int j = 0;
    int i = j;
    int k;
    if (this.mWidth > 0)
    {
      k = getContext().getResources().getDimensionPixelOffset(this.mWidth);
      i = j;
      if (k != paramConfiguration.width)
      {
        paramConfiguration.width = k;
        i = 1;
      }
    }
    j = i;
    if (this.mHeight > 0)
    {
      k = getContext().getResources().getDimensionPixelOffset(this.mHeight);
      j = i;
      if (k != paramConfiguration.height)
      {
        paramConfiguration.height = k;
        j = 1;
      }
    }
    if (j != 0) {
      setLayoutParams(paramConfiguration);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(getDefaultSize2(getSuggestedMinimumWidth(), paramInt1), getDefaultSize2(getSuggestedMinimumHeight(), paramInt2));
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\ResizingSpace.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */