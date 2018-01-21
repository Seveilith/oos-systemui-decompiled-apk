package com.android.systemui.statusbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class BackDropView
  extends FrameLayout
{
  private Runnable mOnVisibilityChangedRunnable;
  
  public BackDropView(Context paramContext)
  {
    super(paramContext);
  }
  
  public BackDropView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public BackDropView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public BackDropView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onVisibilityChanged(View paramView, int paramInt)
  {
    super.onVisibilityChanged(paramView, paramInt);
    if ((paramView == this) && (this.mOnVisibilityChangedRunnable != null)) {
      this.mOnVisibilityChangedRunnable.run();
    }
  }
  
  public void setOnVisibilityChangedRunnable(Runnable paramRunnable)
  {
    this.mOnVisibilityChangedRunnable = paramRunnable;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\BackDropView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */