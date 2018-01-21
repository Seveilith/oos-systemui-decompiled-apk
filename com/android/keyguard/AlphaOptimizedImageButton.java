package com.android.keyguard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class AlphaOptimizedImageButton
  extends ImageButton
{
  public AlphaOptimizedImageButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\AlphaOptimizedImageButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */