package android.support.v4.view;

import android.os.Build.VERSION;
import android.view.MotionEvent;

public final class MotionEventCompat
{
  static final MotionEventVersionImpl IMPL = new BaseMotionEventVersionImpl();
  
  static
  {
    if (Build.VERSION.SDK_INT >= 14)
    {
      IMPL = new ICSMotionEventVersionImpl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 12)
    {
      IMPL = new HoneycombMr1MotionEventVersionImpl();
      return;
    }
  }
  
  public static int getActionIndex(MotionEvent paramMotionEvent)
  {
    return (paramMotionEvent.getAction() & 0xFF00) >> 8;
  }
  
  public static int getActionMasked(MotionEvent paramMotionEvent)
  {
    return paramMotionEvent.getAction() & 0xFF;
  }
  
  public static float getAxisValue(MotionEvent paramMotionEvent, int paramInt)
  {
    return IMPL.getAxisValue(paramMotionEvent, paramInt);
  }
  
  static class BaseMotionEventVersionImpl
    implements MotionEventCompat.MotionEventVersionImpl
  {
    public float getAxisValue(MotionEvent paramMotionEvent, int paramInt)
    {
      return 0.0F;
    }
  }
  
  static class HoneycombMr1MotionEventVersionImpl
    extends MotionEventCompat.BaseMotionEventVersionImpl
  {
    public float getAxisValue(MotionEvent paramMotionEvent, int paramInt)
    {
      return MotionEventCompatHoneycombMr1.getAxisValue(paramMotionEvent, paramInt);
    }
  }
  
  private static class ICSMotionEventVersionImpl
    extends MotionEventCompat.HoneycombMr1MotionEventVersionImpl
  {}
  
  static abstract interface MotionEventVersionImpl
  {
    public abstract float getAxisValue(MotionEvent paramMotionEvent, int paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\view\MotionEventCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */