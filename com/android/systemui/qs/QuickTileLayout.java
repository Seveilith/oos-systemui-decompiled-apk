package com.android.systemui.qs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class QuickTileLayout
  extends LinearLayout
{
  public QuickTileLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setGravity(17);
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    paramLayoutParams = new LinearLayout.LayoutParams(paramLayoutParams.height, paramLayoutParams.height);
    ((LinearLayout.LayoutParams)paramLayoutParams).weight = 1.0F;
    super.addView(paramView, paramInt, paramLayoutParams);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\QuickTileLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */