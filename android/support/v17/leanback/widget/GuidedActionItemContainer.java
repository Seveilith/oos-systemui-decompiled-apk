package android.support.v17.leanback.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

class GuidedActionItemContainer
  extends NonOverlappingLinearLayoutWithForeground
{
  private boolean mFocusOutAllowed = true;
  
  public GuidedActionItemContainer(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public GuidedActionItemContainer(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public GuidedActionItemContainer(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public View focusSearch(View paramView, int paramInt)
  {
    if ((!this.mFocusOutAllowed) && (Util.isDescendant(this, paramView)))
    {
      paramView = super.focusSearch(paramView, paramInt);
      if (Util.isDescendant(this, paramView)) {
        return paramView;
      }
    }
    else
    {
      return super.focusSearch(paramView, paramInt);
    }
    return null;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\GuidedActionItemContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */