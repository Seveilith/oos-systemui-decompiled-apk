package com.android.systemui.statusbar.policy;

public abstract interface SecurityController
{
  public abstract void addCallback(SecurityControllerCallback paramSecurityControllerCallback);
  
  public abstract boolean isVpnBranded();
  
  public abstract boolean isVpnEnabled();
  
  public abstract void removeCallback(SecurityControllerCallback paramSecurityControllerCallback);
  
  public static abstract interface SecurityControllerCallback
  {
    public abstract void onStateChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\SecurityController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */