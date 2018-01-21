package android.support.v4.view.accessibility;

import android.os.Build.VERSION;
import android.view.accessibility.AccessibilityEvent;

public final class AccessibilityEventCompat
{
  private static final AccessibilityEventVersionImpl IMPL = new AccessibilityEventStubImpl();
  
  static
  {
    if (Build.VERSION.SDK_INT >= 19)
    {
      IMPL = new AccessibilityEventKitKatImpl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 16)
    {
      IMPL = new AccessibilityEventJellyBeanImpl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 14)
    {
      IMPL = new AccessibilityEventIcsImpl();
      return;
    }
  }
  
  public static AccessibilityRecordCompat asRecord(AccessibilityEvent paramAccessibilityEvent)
  {
    return new AccessibilityRecordCompat(paramAccessibilityEvent);
  }
  
  public static int getContentChangeTypes(AccessibilityEvent paramAccessibilityEvent)
  {
    return IMPL.getContentChangeTypes(paramAccessibilityEvent);
  }
  
  public static void setContentChangeTypes(AccessibilityEvent paramAccessibilityEvent, int paramInt)
  {
    IMPL.setContentChangeTypes(paramAccessibilityEvent, paramInt);
  }
  
  static class AccessibilityEventIcsImpl
    extends AccessibilityEventCompat.AccessibilityEventStubImpl
  {}
  
  static class AccessibilityEventJellyBeanImpl
    extends AccessibilityEventCompat.AccessibilityEventIcsImpl
  {}
  
  static class AccessibilityEventKitKatImpl
    extends AccessibilityEventCompat.AccessibilityEventJellyBeanImpl
  {
    public int getContentChangeTypes(AccessibilityEvent paramAccessibilityEvent)
    {
      return AccessibilityEventCompatKitKat.getContentChangeTypes(paramAccessibilityEvent);
    }
    
    public void setContentChangeTypes(AccessibilityEvent paramAccessibilityEvent, int paramInt)
    {
      AccessibilityEventCompatKitKat.setContentChangeTypes(paramAccessibilityEvent, paramInt);
    }
  }
  
  static class AccessibilityEventStubImpl
    implements AccessibilityEventCompat.AccessibilityEventVersionImpl
  {
    public int getContentChangeTypes(AccessibilityEvent paramAccessibilityEvent)
    {
      return 0;
    }
    
    public void setContentChangeTypes(AccessibilityEvent paramAccessibilityEvent, int paramInt) {}
  }
  
  static abstract interface AccessibilityEventVersionImpl
  {
    public abstract int getContentChangeTypes(AccessibilityEvent paramAccessibilityEvent);
    
    public abstract void setContentChangeTypes(AccessibilityEvent paramAccessibilityEvent, int paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\view\accessibility\AccessibilityEventCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */