package android.support.v4.view.accessibility;

import android.os.Build.VERSION;

public class AccessibilityRecordCompat
{
  private static final AccessibilityRecordImpl IMPL = new AccessibilityRecordStubImpl();
  private final Object mRecord;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      IMPL = new AccessibilityRecordJellyBeanImpl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 15)
    {
      IMPL = new AccessibilityRecordIcsMr1Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 14)
    {
      IMPL = new AccessibilityRecordIcsImpl();
      return;
    }
  }
  
  @Deprecated
  public AccessibilityRecordCompat(Object paramObject)
  {
    this.mRecord = paramObject;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (AccessibilityRecordCompat)paramObject;
    if (this.mRecord == null)
    {
      if (((AccessibilityRecordCompat)paramObject).mRecord != null) {
        return false;
      }
    }
    else if (!this.mRecord.equals(((AccessibilityRecordCompat)paramObject).mRecord)) {
      return false;
    }
    return true;
  }
  
  public int hashCode()
  {
    if (this.mRecord == null) {
      return 0;
    }
    return this.mRecord.hashCode();
  }
  
  public void setFromIndex(int paramInt)
  {
    IMPL.setFromIndex(this.mRecord, paramInt);
  }
  
  public void setItemCount(int paramInt)
  {
    IMPL.setItemCount(this.mRecord, paramInt);
  }
  
  public void setMaxScrollX(int paramInt)
  {
    IMPL.setMaxScrollX(this.mRecord, paramInt);
  }
  
  public void setMaxScrollY(int paramInt)
  {
    IMPL.setMaxScrollY(this.mRecord, paramInt);
  }
  
  public void setScrollX(int paramInt)
  {
    IMPL.setScrollX(this.mRecord, paramInt);
  }
  
  public void setScrollY(int paramInt)
  {
    IMPL.setScrollY(this.mRecord, paramInt);
  }
  
  public void setScrollable(boolean paramBoolean)
  {
    IMPL.setScrollable(this.mRecord, paramBoolean);
  }
  
  public void setToIndex(int paramInt)
  {
    IMPL.setToIndex(this.mRecord, paramInt);
  }
  
  static class AccessibilityRecordIcsImpl
    extends AccessibilityRecordCompat.AccessibilityRecordStubImpl
  {
    public void setFromIndex(Object paramObject, int paramInt)
    {
      AccessibilityRecordCompatIcs.setFromIndex(paramObject, paramInt);
    }
    
    public void setItemCount(Object paramObject, int paramInt)
    {
      AccessibilityRecordCompatIcs.setItemCount(paramObject, paramInt);
    }
    
    public void setScrollX(Object paramObject, int paramInt)
    {
      AccessibilityRecordCompatIcs.setScrollX(paramObject, paramInt);
    }
    
    public void setScrollY(Object paramObject, int paramInt)
    {
      AccessibilityRecordCompatIcs.setScrollY(paramObject, paramInt);
    }
    
    public void setScrollable(Object paramObject, boolean paramBoolean)
    {
      AccessibilityRecordCompatIcs.setScrollable(paramObject, paramBoolean);
    }
    
    public void setToIndex(Object paramObject, int paramInt)
    {
      AccessibilityRecordCompatIcs.setToIndex(paramObject, paramInt);
    }
  }
  
  static class AccessibilityRecordIcsMr1Impl
    extends AccessibilityRecordCompat.AccessibilityRecordIcsImpl
  {
    public void setMaxScrollX(Object paramObject, int paramInt)
    {
      AccessibilityRecordCompatIcsMr1.setMaxScrollX(paramObject, paramInt);
    }
    
    public void setMaxScrollY(Object paramObject, int paramInt)
    {
      AccessibilityRecordCompatIcsMr1.setMaxScrollY(paramObject, paramInt);
    }
  }
  
  static abstract interface AccessibilityRecordImpl
  {
    public abstract void setFromIndex(Object paramObject, int paramInt);
    
    public abstract void setItemCount(Object paramObject, int paramInt);
    
    public abstract void setMaxScrollX(Object paramObject, int paramInt);
    
    public abstract void setMaxScrollY(Object paramObject, int paramInt);
    
    public abstract void setScrollX(Object paramObject, int paramInt);
    
    public abstract void setScrollY(Object paramObject, int paramInt);
    
    public abstract void setScrollable(Object paramObject, boolean paramBoolean);
    
    public abstract void setToIndex(Object paramObject, int paramInt);
  }
  
  static class AccessibilityRecordJellyBeanImpl
    extends AccessibilityRecordCompat.AccessibilityRecordIcsMr1Impl
  {}
  
  static class AccessibilityRecordStubImpl
    implements AccessibilityRecordCompat.AccessibilityRecordImpl
  {
    public void setFromIndex(Object paramObject, int paramInt) {}
    
    public void setItemCount(Object paramObject, int paramInt) {}
    
    public void setMaxScrollX(Object paramObject, int paramInt) {}
    
    public void setMaxScrollY(Object paramObject, int paramInt) {}
    
    public void setScrollX(Object paramObject, int paramInt) {}
    
    public void setScrollY(Object paramObject, int paramInt) {}
    
    public void setScrollable(Object paramObject, boolean paramBoolean) {}
    
    public void setToIndex(Object paramObject, int paramInt) {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\view\accessibility\AccessibilityRecordCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */