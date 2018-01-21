package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import java.util.ArrayList;

public class ReverseLinearLayout
  extends LinearLayout
{
  private boolean mIsAlternativeOrder;
  private boolean mIsLayoutReverse;
  
  public ReverseLinearLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void reversParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (paramLayoutParams == null) {
      return;
    }
    int i = paramLayoutParams.width;
    paramLayoutParams.width = paramLayoutParams.height;
    paramLayoutParams.height = i;
  }
  
  private void updateOrder()
  {
    if (getLayoutDirection() == 1) {}
    boolean bool;
    int j;
    ArrayList localArrayList;
    for (int i = 1;; i = 0)
    {
      bool = i ^ this.mIsAlternativeOrder;
      if (this.mIsLayoutReverse == bool) {
        return;
      }
      j = getChildCount();
      localArrayList = new ArrayList(j);
      i = 0;
      while (i < j)
      {
        localArrayList.add(getChildAt(i));
        i += 1;
      }
    }
    removeAllViews();
    i = j - 1;
    while (i >= 0)
    {
      super.addView((View)localArrayList.get(i));
      i -= 1;
    }
    this.mIsLayoutReverse = bool;
  }
  
  public void addView(View paramView)
  {
    reversParams(paramView.getLayoutParams());
    if (this.mIsLayoutReverse)
    {
      super.addView(paramView, 0);
      return;
    }
    super.addView(paramView);
  }
  
  public void addView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    reversParams(paramLayoutParams);
    if (this.mIsLayoutReverse)
    {
      super.addView(paramView, 0, paramLayoutParams);
      return;
    }
    super.addView(paramView, paramLayoutParams);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    updateOrder();
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    super.onRtlPropertiesChanged(paramInt);
    updateOrder();
  }
  
  public void setAlternativeOrder(boolean paramBoolean)
  {
    this.mIsAlternativeOrder = paramBoolean;
    updateOrder();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\ReverseLinearLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */