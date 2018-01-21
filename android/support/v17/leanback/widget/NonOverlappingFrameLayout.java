package android.support.v17.leanback.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

class NonOverlappingFrameLayout
  extends FrameLayout
{
  public NonOverlappingFrameLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public NonOverlappingFrameLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet, 0);
  }
  
  public NonOverlappingFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\NonOverlappingFrameLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */