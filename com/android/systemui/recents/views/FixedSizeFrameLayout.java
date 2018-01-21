package com.android.systemui.recents.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;

public class FixedSizeFrameLayout
  extends FrameLayout
{
  private final Rect mLayoutBounds = new Rect();
  
  public FixedSizeFrameLayout(Context paramContext)
  {
    super(paramContext);
  }
  
  public FixedSizeFrameLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public FixedSizeFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public FixedSizeFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  protected void layoutContents(Rect paramRect, boolean paramBoolean)
  {
    super.onLayout(paramBoolean, paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
    int i = getMeasuredWidth();
    int j = getMeasuredHeight();
    onSizeChanged(i, j, i, j);
  }
  
  protected void measureContents(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(paramInt1, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE));
  }
  
  protected final void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mLayoutBounds.set(paramInt1, paramInt2, paramInt3, paramInt4);
    layoutContents(this.mLayoutBounds, paramBoolean);
  }
  
  protected final void onMeasure(int paramInt1, int paramInt2)
  {
    measureContents(View.MeasureSpec.getSize(paramInt1), View.MeasureSpec.getSize(paramInt2));
  }
  
  public final void requestLayout()
  {
    if ((this.mLayoutBounds == null) || (this.mLayoutBounds.isEmpty()))
    {
      super.requestLayout();
      return;
    }
    measureContents(getMeasuredWidth(), getMeasuredHeight());
    layoutContents(this.mLayoutBounds, false);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\FixedSizeFrameLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */