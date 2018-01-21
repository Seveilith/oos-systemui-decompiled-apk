package com.android.systemui.statusbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AlphaOptimizedImageView
  extends ImageView
{
  public AlphaOptimizedImageView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AlphaOptimizedImageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public AlphaOptimizedImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public AlphaOptimizedImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\AlphaOptimizedImageView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */