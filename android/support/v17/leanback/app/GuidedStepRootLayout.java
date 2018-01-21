package android.support.v17.leanback.app;

import android.content.Context;
import android.support.v17.leanback.widget.Util;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

class GuidedStepRootLayout
  extends LinearLayout
{
  private boolean mFocusOutEnd = false;
  private boolean mFocusOutStart = false;
  
  public GuidedStepRootLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public GuidedStepRootLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public View focusSearch(View paramView, int paramInt)
  {
    View localView = super.focusSearch(paramView, paramInt);
    if ((paramInt == 17) || (paramInt == 66))
    {
      if (Util.isDescendant(this, localView)) {
        return localView;
      }
      if (getLayoutDirection() == 0)
      {
        if (paramInt != 17) {}
      }
      else {
        while (paramInt == 66)
        {
          if (this.mFocusOutStart) {
            break;
          }
          return paramView;
        }
      }
      if (!this.mFocusOutEnd) {
        return paramView;
      }
    }
    return localView;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\app\GuidedStepRootLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */