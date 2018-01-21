package com.android.systemui.statusbar;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;

class ScalingDrawableWrapper
  extends DrawableWrapper
{
  private float mScaleFactor;
  
  public ScalingDrawableWrapper(Drawable paramDrawable, float paramFloat)
  {
    super(paramDrawable);
    this.mScaleFactor = paramFloat;
  }
  
  public int getIntrinsicHeight()
  {
    return (int)(super.getIntrinsicHeight() * this.mScaleFactor);
  }
  
  public int getIntrinsicWidth()
  {
    return (int)(super.getIntrinsicWidth() * this.mScaleFactor);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\ScalingDrawableWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */