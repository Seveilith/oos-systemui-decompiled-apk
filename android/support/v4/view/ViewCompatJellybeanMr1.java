package android.support.v4.view;

import android.graphics.Paint;
import android.view.Display;
import android.view.View;

class ViewCompatJellybeanMr1
{
  public static Display getDisplay(View paramView)
  {
    return paramView.getDisplay();
  }
  
  public static int getLayoutDirection(View paramView)
  {
    return paramView.getLayoutDirection();
  }
  
  public static int getWindowSystemUiVisibility(View paramView)
  {
    return paramView.getWindowSystemUiVisibility();
  }
  
  public static void setLayerPaint(View paramView, Paint paramPaint)
  {
    paramView.setLayerPaint(paramPaint);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\view\ViewCompatJellybeanMr1.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */