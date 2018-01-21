package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ExpandableIndicator
  extends ImageView
{
  private boolean mExpanded;
  private boolean mIsDefaultDirection = true;
  
  public ExpandableIndicator(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private String getContentDescription(boolean paramBoolean)
  {
    if (paramBoolean) {
      return this.mContext.getString(2131690633);
    }
    return this.mContext.getString(2131690632);
  }
  
  private int getDrawableResourceId(boolean paramBoolean)
  {
    if (this.mIsDefaultDirection)
    {
      if (paramBoolean) {
        return 2130837940;
      }
      return 2130837942;
    }
    if (paramBoolean) {
      return 2130837942;
    }
    return 2130837940;
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    setImageResource(getDrawableResourceId(this.mExpanded));
    setContentDescription(getContentDescription(this.mExpanded));
  }
  
  public void setExpanded(boolean paramBoolean)
  {
    if (paramBoolean == this.mExpanded) {
      return;
    }
    this.mExpanded = paramBoolean;
    if (this.mExpanded) {}
    for (boolean bool = false;; bool = true)
    {
      int i = getDrawableResourceId(bool);
      AnimatedVectorDrawable localAnimatedVectorDrawable = (AnimatedVectorDrawable)getContext().getDrawable(i).getConstantState().newDrawable();
      setImageDrawable(localAnimatedVectorDrawable);
      localAnimatedVectorDrawable.forceAnimationOnUI();
      localAnimatedVectorDrawable.start();
      setContentDescription(getContentDescription(paramBoolean));
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\ExpandableIndicator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */