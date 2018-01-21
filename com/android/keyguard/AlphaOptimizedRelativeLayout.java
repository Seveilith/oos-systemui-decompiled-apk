package com.android.keyguard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class AlphaOptimizedRelativeLayout
  extends RelativeLayout
{
  public AlphaOptimizedRelativeLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\AlphaOptimizedRelativeLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */