package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

class ControlBar
  extends LinearLayout
{
  private int mChildMarginFromCenter;
  private OnChildFocusedListener mOnChildFocusedListener;
  
  public ControlBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public ControlBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if (this.mChildMarginFromCenter <= 0) {
      return;
    }
    paramInt2 = 0;
    paramInt1 = 0;
    while (paramInt1 < getChildCount() - 1)
    {
      Object localObject = getChildAt(paramInt1);
      View localView = getChildAt(paramInt1 + 1);
      int i = ((View)localObject).getMeasuredWidth();
      int j = localView.getMeasuredWidth();
      i = this.mChildMarginFromCenter - (i + j) / 2;
      localObject = (LinearLayout.LayoutParams)localView.getLayoutParams();
      j = ((LinearLayout.LayoutParams)localObject).getMarginStart();
      ((LinearLayout.LayoutParams)localObject).setMarginStart(i);
      localView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      paramInt2 += i - j;
      paramInt1 += 1;
    }
    setMeasuredDimension(getMeasuredWidth() + paramInt2, getMeasuredHeight());
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    super.requestChildFocus(paramView1, paramView2);
    if (this.mOnChildFocusedListener != null) {
      this.mOnChildFocusedListener.onChildFocusedListener(paramView1, paramView2);
    }
  }
  
  public boolean requestFocus(int paramInt, Rect paramRect)
  {
    if ((getChildCount() > 0) && (getChildAt(getChildCount() / 2).requestFocus(paramInt, paramRect))) {
      return true;
    }
    return super.requestFocus(paramInt, paramRect);
  }
  
  public static abstract interface OnChildFocusedListener
  {
    public abstract void onChildFocusedListener(View paramView1, View paramView2);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\ControlBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */