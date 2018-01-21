package android.support.v4.view;

import android.os.Build.VERSION;

public class WindowInsetsCompat
{
  private static final WindowInsetsCompatImpl IMPL = new WindowInsetsCompatBaseImpl();
  private final Object mInsets;
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    if (i >= 21)
    {
      IMPL = new WindowInsetsCompatApi21Impl();
      return;
    }
    if (i >= 20)
    {
      IMPL = new WindowInsetsCompatApi20Impl();
      return;
    }
  }
  
  WindowInsetsCompat(Object paramObject)
  {
    this.mInsets = paramObject;
  }
  
  static Object unwrap(WindowInsetsCompat paramWindowInsetsCompat)
  {
    if (paramWindowInsetsCompat == null) {
      return null;
    }
    return paramWindowInsetsCompat.mInsets;
  }
  
  static WindowInsetsCompat wrap(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    return new WindowInsetsCompat(paramObject);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    paramObject = (WindowInsetsCompat)paramObject;
    if (this.mInsets == null) {
      return ((WindowInsetsCompat)paramObject).mInsets == null;
    }
    return this.mInsets.equals(((WindowInsetsCompat)paramObject).mInsets);
  }
  
  public int getSystemWindowInsetBottom()
  {
    return IMPL.getSystemWindowInsetBottom(this.mInsets);
  }
  
  public int getSystemWindowInsetLeft()
  {
    return IMPL.getSystemWindowInsetLeft(this.mInsets);
  }
  
  public int getSystemWindowInsetRight()
  {
    return IMPL.getSystemWindowInsetRight(this.mInsets);
  }
  
  public int getSystemWindowInsetTop()
  {
    return IMPL.getSystemWindowInsetTop(this.mInsets);
  }
  
  public int hashCode()
  {
    if (this.mInsets == null) {
      return 0;
    }
    return this.mInsets.hashCode();
  }
  
  public boolean isConsumed()
  {
    return IMPL.isConsumed(this.mInsets);
  }
  
  public WindowInsetsCompat replaceSystemWindowInsets(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return IMPL.replaceSystemWindowInsets(this.mInsets, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  private static class WindowInsetsCompatApi20Impl
    extends WindowInsetsCompat.WindowInsetsCompatBaseImpl
  {
    public int getSystemWindowInsetBottom(Object paramObject)
    {
      return WindowInsetsCompatApi20.getSystemWindowInsetBottom(paramObject);
    }
    
    public int getSystemWindowInsetLeft(Object paramObject)
    {
      return WindowInsetsCompatApi20.getSystemWindowInsetLeft(paramObject);
    }
    
    public int getSystemWindowInsetRight(Object paramObject)
    {
      return WindowInsetsCompatApi20.getSystemWindowInsetRight(paramObject);
    }
    
    public int getSystemWindowInsetTop(Object paramObject)
    {
      return WindowInsetsCompatApi20.getSystemWindowInsetTop(paramObject);
    }
    
    public WindowInsetsCompat replaceSystemWindowInsets(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return new WindowInsetsCompat(WindowInsetsCompatApi20.replaceSystemWindowInsets(paramObject, paramInt1, paramInt2, paramInt3, paramInt4));
    }
  }
  
  private static class WindowInsetsCompatApi21Impl
    extends WindowInsetsCompat.WindowInsetsCompatApi20Impl
  {
    public boolean isConsumed(Object paramObject)
    {
      return WindowInsetsCompatApi21.isConsumed(paramObject);
    }
  }
  
  private static class WindowInsetsCompatBaseImpl
    implements WindowInsetsCompat.WindowInsetsCompatImpl
  {
    public int getSystemWindowInsetBottom(Object paramObject)
    {
      return 0;
    }
    
    public int getSystemWindowInsetLeft(Object paramObject)
    {
      return 0;
    }
    
    public int getSystemWindowInsetRight(Object paramObject)
    {
      return 0;
    }
    
    public int getSystemWindowInsetTop(Object paramObject)
    {
      return 0;
    }
    
    public boolean isConsumed(Object paramObject)
    {
      return false;
    }
    
    public WindowInsetsCompat replaceSystemWindowInsets(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return null;
    }
  }
  
  private static abstract interface WindowInsetsCompatImpl
  {
    public abstract int getSystemWindowInsetBottom(Object paramObject);
    
    public abstract int getSystemWindowInsetLeft(Object paramObject);
    
    public abstract int getSystemWindowInsetRight(Object paramObject);
    
    public abstract int getSystemWindowInsetTop(Object paramObject);
    
    public abstract boolean isConsumed(Object paramObject);
    
    public abstract WindowInsetsCompat replaceSystemWindowInsets(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\view\WindowInsetsCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */