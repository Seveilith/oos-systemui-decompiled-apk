package com.android.systemui.statusbar.policy;

public abstract interface HotspotController
{
  public abstract void addCallback(Callback paramCallback);
  
  public abstract boolean isHotspotEnabled();
  
  public abstract boolean isHotspotSupported();
  
  public abstract void removeCallback(Callback paramCallback);
  
  public abstract void setHotspotEnabled(boolean paramBoolean);
  
  public static abstract interface Callback
  {
    public abstract void onHotspotChanged(boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\HotspotController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */