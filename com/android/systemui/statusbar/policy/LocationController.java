package com.android.systemui.statusbar.policy;

public abstract interface LocationController
{
  public abstract void addSettingsChangedCallback(LocationSettingsChangeCallback paramLocationSettingsChangeCallback);
  
  public abstract boolean isLocationEnabled();
  
  public abstract void removeSettingsChangedCallback(LocationSettingsChangeCallback paramLocationSettingsChangeCallback);
  
  public abstract boolean setLocationEnabled(boolean paramBoolean);
  
  public static abstract interface LocationSettingsChangeCallback
  {
    public abstract void onLocationSettingsChanged(boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\LocationController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */