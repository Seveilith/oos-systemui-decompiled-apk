package com.android.systemui;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

public class FontSizeUtils
{
  public static void updateFontSize(View paramView, int paramInt1, int paramInt2)
  {
    updateFontSize((TextView)paramView.findViewById(paramInt1), paramInt2);
  }
  
  public static void updateFontSize(TextView paramTextView, int paramInt)
  {
    if (paramTextView != null) {
      paramTextView.setTextSize(0, paramTextView.getResources().getDimensionPixelSize(paramInt));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\FontSizeUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */