package com.android.systemui.statusbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;
import android.view.View;

public class NotificationBackgroundView
  extends View
{
  private int mActualHeight;
  private Drawable mBackground;
  private int mClipTopAmount;
  
  public NotificationBackgroundView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void draw(Canvas paramCanvas, Drawable paramDrawable)
  {
    if ((paramDrawable != null) && (this.mActualHeight > this.mClipTopAmount))
    {
      paramDrawable.setBounds(0, this.mClipTopAmount, getWidth(), this.mActualHeight);
      paramDrawable.draw(paramCanvas);
    }
  }
  
  private void drawableStateChanged(Drawable paramDrawable)
  {
    if ((paramDrawable != null) && (paramDrawable.isStateful())) {
      paramDrawable.setState(getDrawableState());
    }
  }
  
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2)
  {
    if (this.mBackground != null) {
      this.mBackground.setHotspot(paramFloat1, paramFloat2);
    }
  }
  
  protected void drawableStateChanged()
  {
    drawableStateChanged(this.mBackground);
  }
  
  public int getActualHeight()
  {
    return this.mActualHeight;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    draw(paramCanvas, this.mBackground);
  }
  
  public void setActualHeight(int paramInt)
  {
    this.mActualHeight = paramInt;
    invalidate();
  }
  
  public void setClipTopAmount(int paramInt)
  {
    this.mClipTopAmount = paramInt;
    invalidate();
  }
  
  public void setCustomBackground(int paramInt)
  {
    setCustomBackground(this.mContext.getDrawable(paramInt));
  }
  
  public void setCustomBackground(Drawable paramDrawable)
  {
    if (this.mBackground != null)
    {
      this.mBackground.setCallback(null);
      unscheduleDrawable(this.mBackground);
    }
    this.mBackground = paramDrawable;
    if (this.mBackground != null) {
      this.mBackground.setCallback(this);
    }
    if ((this.mBackground instanceof RippleDrawable)) {
      ((RippleDrawable)this.mBackground).setForceSoftware(true);
    }
    invalidate();
  }
  
  public void setRippleColor(int paramInt)
  {
    if ((this.mBackground instanceof RippleDrawable)) {
      ((RippleDrawable)this.mBackground).setColor(ColorStateList.valueOf(paramInt));
    }
  }
  
  public void setState(int[] paramArrayOfInt)
  {
    this.mBackground.setState(paramArrayOfInt);
  }
  
  public void setTint(int paramInt)
  {
    if (paramInt != 0) {
      this.mBackground.setColorFilter(paramInt, PorterDuff.Mode.SRC_ATOP);
    }
    for (;;)
    {
      invalidate();
      return;
      this.mBackground.clearColorFilter();
    }
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    return (super.verifyDrawable(paramDrawable)) || (paramDrawable == this.mBackground);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\NotificationBackgroundView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */