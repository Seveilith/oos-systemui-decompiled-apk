package android.support.v17.leanback.widget;

import android.support.annotation.RestrictTo;
import android.view.View;
import android.view.ViewGroup;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class Util
{
  public static boolean isDescendant(ViewGroup paramViewGroup, View paramView)
  {
    while (paramView != null)
    {
      if (paramView == paramViewGroup) {
        return true;
      }
      paramView = paramView.getParent();
      if (!(paramView instanceof View)) {
        return false;
      }
      paramView = (View)paramView;
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */