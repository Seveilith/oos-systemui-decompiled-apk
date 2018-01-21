package android.support.v4.view;

import android.view.WindowInsets;

class WindowInsetsCompatApi20
{
  public static int getSystemWindowInsetBottom(Object paramObject)
  {
    return ((WindowInsets)paramObject).getSystemWindowInsetBottom();
  }
  
  public static int getSystemWindowInsetLeft(Object paramObject)
  {
    return ((WindowInsets)paramObject).getSystemWindowInsetLeft();
  }
  
  public static int getSystemWindowInsetRight(Object paramObject)
  {
    return ((WindowInsets)paramObject).getSystemWindowInsetRight();
  }
  
  public static int getSystemWindowInsetTop(Object paramObject)
  {
    return ((WindowInsets)paramObject).getSystemWindowInsetTop();
  }
  
  public static Object replaceSystemWindowInsets(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return ((WindowInsets)paramObject).replaceSystemWindowInsets(paramInt1, paramInt2, paramInt3, paramInt4);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\view\WindowInsetsCompatApi20.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */