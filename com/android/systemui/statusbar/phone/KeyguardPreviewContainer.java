package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

public class KeyguardPreviewContainer
  extends FrameLayout
{
  private Drawable mBlackBarDrawable = new Drawable()
  {
    public void draw(Canvas paramAnonymousCanvas)
    {
      paramAnonymousCanvas.save();
      paramAnonymousCanvas.clipRect(0, KeyguardPreviewContainer.this.getHeight() - KeyguardPreviewContainer.this.getPaddingBottom(), KeyguardPreviewContainer.this.getWidth(), KeyguardPreviewContainer.this.getHeight());
      paramAnonymousCanvas.drawColor(-16777216);
      paramAnonymousCanvas.restore();
    }
    
    public int getOpacity()
    {
      return -1;
    }
    
    public void setAlpha(int paramAnonymousInt) {}
    
    public void setColorFilter(ColorFilter paramAnonymousColorFilter) {}
  };
  
  public KeyguardPreviewContainer(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setBackground(this.mBlackBarDrawable);
  }
  
  public WindowInsets onApplyWindowInsets(WindowInsets paramWindowInsets)
  {
    setPadding(0, 0, 0, paramWindowInsets.getStableInsetBottom());
    return super.onApplyWindowInsets(paramWindowInsets);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\KeyguardPreviewContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */