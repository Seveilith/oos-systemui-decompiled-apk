package com.android.systemui.statusbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AlphaImageView
  extends ImageView
{
  public AlphaImageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\AlphaImageView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */