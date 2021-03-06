package android.support.v7.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.RestrictTo;
import android.util.AttributeSet;
import android.widget.FrameLayout;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class FitWindowsFrameLayout
  extends FrameLayout
{
  private FitWindowsViewGroup.OnFitSystemWindowsListener mListener;
  
  public FitWindowsFrameLayout(Context paramContext)
  {
    super(paramContext);
  }
  
  public FitWindowsFrameLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  protected boolean fitSystemWindows(Rect paramRect)
  {
    if (this.mListener != null) {
      this.mListener.onFitSystemWindows(paramRect);
    }
    return super.fitSystemWindows(paramRect);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\FitWindowsFrameLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */