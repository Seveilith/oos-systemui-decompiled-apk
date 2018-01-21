package com.android.systemui.statusbar.car;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.android.keyguard.AlphaOptimizedImageButton;

public class CarNavigationButton
  extends RelativeLayout
{
  private AlphaOptimizedImageButton mIcon;
  private AlphaOptimizedImageButton mMoreIcon;
  
  public CarNavigationButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public void onFinishInflate()
  {
    super.onFinishInflate();
    this.mIcon = ((AlphaOptimizedImageButton)findViewById(2131951804));
    this.mIcon.setClickable(false);
    this.mIcon.setBackgroundColor(17170445);
    this.mIcon.setAlpha(0.7F);
    this.mMoreIcon = ((AlphaOptimizedImageButton)findViewById(2131951805));
    this.mMoreIcon.setClickable(false);
    this.mMoreIcon.setBackgroundColor(17170445);
    this.mMoreIcon.setVisibility(4);
    this.mMoreIcon.setImageDrawable(getContext().getDrawable(2130837595));
    this.mMoreIcon.setAlpha(0.7F);
  }
  
  public void setResources(Drawable paramDrawable)
  {
    this.mIcon.setImageDrawable(paramDrawable);
  }
  
  public void setSelected(boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = 4;
    if (paramBoolean1)
    {
      AlphaOptimizedImageButton localAlphaOptimizedImageButton = this.mMoreIcon;
      if (paramBoolean2) {
        i = 0;
      }
      localAlphaOptimizedImageButton.setVisibility(i);
      this.mMoreIcon.setAlpha(1.0F);
      this.mIcon.setAlpha(1.0F);
      return;
    }
    this.mMoreIcon.setVisibility(4);
    this.mIcon.setAlpha(0.7F);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\car\CarNavigationButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */