package android.support.v4.view;

import android.os.Build.VERSION;
import android.view.ViewGroup.MarginLayoutParams;

public final class MarginLayoutParamsCompat
{
  static final MarginLayoutParamsCompatImpl IMPL = new MarginLayoutParamsCompatImplBase();
  
  static
  {
    if (Build.VERSION.SDK_INT >= 17)
    {
      IMPL = new MarginLayoutParamsCompatImplJbMr1();
      return;
    }
  }
  
  public static int getMarginEnd(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
  {
    return IMPL.getMarginEnd(paramMarginLayoutParams);
  }
  
  public static int getMarginStart(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
  {
    return IMPL.getMarginStart(paramMarginLayoutParams);
  }
  
  static abstract interface MarginLayoutParamsCompatImpl
  {
    public abstract int getMarginEnd(ViewGroup.MarginLayoutParams paramMarginLayoutParams);
    
    public abstract int getMarginStart(ViewGroup.MarginLayoutParams paramMarginLayoutParams);
  }
  
  static class MarginLayoutParamsCompatImplBase
    implements MarginLayoutParamsCompat.MarginLayoutParamsCompatImpl
  {
    public int getMarginEnd(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      return paramMarginLayoutParams.rightMargin;
    }
    
    public int getMarginStart(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      return paramMarginLayoutParams.leftMargin;
    }
  }
  
  static class MarginLayoutParamsCompatImplJbMr1
    implements MarginLayoutParamsCompat.MarginLayoutParamsCompatImpl
  {
    public int getMarginEnd(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      return MarginLayoutParamsCompatJellybeanMr1.getMarginEnd(paramMarginLayoutParams);
    }
    
    public int getMarginStart(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      return MarginLayoutParamsCompatJellybeanMr1.getMarginStart(paramMarginLayoutParams);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\view\MarginLayoutParamsCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */