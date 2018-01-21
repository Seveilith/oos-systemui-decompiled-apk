package android.support.v4.view;

import android.os.Build.VERSION;

public final class GravityCompat
{
  static final GravityCompatImpl IMPL = new GravityCompatImplBase();
  
  static
  {
    if (Build.VERSION.SDK_INT >= 17)
    {
      IMPL = new GravityCompatImplJellybeanMr1();
      return;
    }
  }
  
  public static int getAbsoluteGravity(int paramInt1, int paramInt2)
  {
    return IMPL.getAbsoluteGravity(paramInt1, paramInt2);
  }
  
  static abstract interface GravityCompatImpl
  {
    public abstract int getAbsoluteGravity(int paramInt1, int paramInt2);
  }
  
  static class GravityCompatImplBase
    implements GravityCompat.GravityCompatImpl
  {
    public int getAbsoluteGravity(int paramInt1, int paramInt2)
    {
      return 0xFF7FFFFF & paramInt1;
    }
  }
  
  static class GravityCompatImplJellybeanMr1
    implements GravityCompat.GravityCompatImpl
  {
    public int getAbsoluteGravity(int paramInt1, int paramInt2)
    {
      return GravityCompatJellybeanMr1.getAbsoluteGravity(paramInt1, paramInt2);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\view\GravityCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */