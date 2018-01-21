package com.android.systemui.statusbar;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

public class DismissView
  extends StackScrollerDecorView
{
  private DismissViewButton mDismissButton;
  
  public DismissView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  protected View findContentView()
  {
    return findViewById(2131952282);
  }
  
  public boolean isOnEmptySpace(float paramFloat1, float paramFloat2)
  {
    if ((paramFloat1 < this.mContent.getX()) || (paramFloat1 > this.mContent.getX() + this.mContent.getWidth())) {}
    while ((paramFloat2 < this.mContent.getY()) || (paramFloat2 > this.mContent.getY() + this.mContent.getHeight())) {
      return true;
    }
    return false;
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    this.mDismissButton.setText(2131690399);
    this.mDismissButton.setContentDescription(this.mContext.getString(2131690251));
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mDismissButton = ((DismissViewButton)findContentView());
  }
  
  public void setOnButtonClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mContent.setOnClickListener(paramOnClickListener);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\DismissView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */