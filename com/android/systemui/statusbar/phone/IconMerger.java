package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class IconMerger
  extends LinearLayout
{
  private int mIconHPadding;
  private int mIconSize;
  private View mMoreView;
  
  public IconMerger(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    reloadDimens();
  }
  
  private void checkOverflow(int paramInt)
  {
    final boolean bool2 = false;
    if (this.mMoreView == null) {
      return;
    }
    int m = getChildCount();
    int j = 0;
    int i = 0;
    while (i < m)
    {
      int k = j;
      if (getChildAt(i).getVisibility() != 8) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    if (this.mMoreView.getVisibility() == 0) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      if (getFullIconWidth() * j > paramInt) {
        bool2 = true;
      }
      if (bool2 != bool1) {
        post(new Runnable()
        {
          public void run()
          {
            View localView = IconMerger.-get0(IconMerger.this);
            if (bool2) {}
            for (int i = 0;; i = 8)
            {
              localView.setVisibility(i);
              return;
            }
          }
        });
      }
      return;
    }
  }
  
  private int getFullIconWidth()
  {
    return this.mIconSize + this.mIconHPadding * 2;
  }
  
  private void reloadDimens()
  {
    Resources localResources = this.mContext.getResources();
    this.mIconSize = localResources.getDimensionPixelSize(2131755359);
    this.mIconHPadding = localResources.getDimensionPixelSize(2131755380);
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    reloadDimens();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    checkOverflow(paramInt3 - paramInt1);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    paramInt1 = getMeasuredWidth();
    setMeasuredDimension(paramInt1 - paramInt1 % getFullIconWidth(), getMeasuredHeight());
  }
  
  public void setOverflowIndicator(View paramView)
  {
    this.mMoreView = paramView;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\IconMerger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */