package com.android.systemui.statusbar.policy;

import java.util.Set;

public abstract interface CastController
{
  public abstract void addCallback(Callback paramCallback);
  
  public abstract Set<CastDevice> getCastDevices();
  
  public abstract void removeCallback(Callback paramCallback);
  
  public abstract void setCurrentUserId(int paramInt);
  
  public abstract void setDiscovering(boolean paramBoolean);
  
  public abstract void startCasting(CastDevice paramCastDevice);
  
  public abstract void stopCasting(CastDevice paramCastDevice);
  
  public static abstract interface Callback
  {
    public abstract void onCastDevicesChanged();
  }
  
  public static final class CastDevice
  {
    public String description;
    public String id;
    public String name;
    public int state = 0;
    public Object tag;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\CastController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */