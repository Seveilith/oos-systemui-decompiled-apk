package com.android.systemui.statusbar.car;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.android.systemui.statusbar.phone.NavigationBarView;

class CarNavigationBarView
  extends NavigationBarView
{
  private LinearLayout mLightsOutButtons;
  private LinearLayout mNavButtons;
  
  public CarNavigationBarView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public void addButton(CarNavigationButton paramCarNavigationButton1, CarNavigationButton paramCarNavigationButton2)
  {
    this.mNavButtons.addView(paramCarNavigationButton1);
    this.mLightsOutButtons.addView(paramCarNavigationButton2);
  }
  
  public View getCurrentView()
  {
    return this;
  }
  
  public void onFinishInflate()
  {
    this.mNavButtons = ((LinearLayout)findViewById(2131951802));
    this.mLightsOutButtons = ((LinearLayout)findViewById(2131951803));
  }
  
  public void reorient() {}
  
  public void setDisabledFlags(int paramInt, boolean paramBoolean) {}
  
  public void setNavigationIconHints(int paramInt, boolean paramBoolean) {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\car\CarNavigationBarView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */