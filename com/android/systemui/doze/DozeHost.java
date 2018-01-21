package com.android.systemui.doze;

public abstract interface DozeHost
{
  public abstract void addCallback(Callback paramCallback);
  
  public abstract boolean isPowerSaveActive();
  
  public abstract boolean isPulsingBlocked();
  
  public abstract void pulseWhileDozing(PulseCallback paramPulseCallback, int paramInt);
  
  public abstract void removeCallback(Callback paramCallback);
  
  public abstract void startDozing(Runnable paramRunnable);
  
  public abstract void stopDozing();
  
  public static abstract interface Callback
  {
    public abstract void onBuzzBeepBlinked();
    
    public abstract void onNewNotifications();
    
    public abstract void onNotificationLight(boolean paramBoolean);
    
    public abstract void onPowerSaveChanged(boolean paramBoolean);
  }
  
  public static abstract interface PulseCallback
  {
    public abstract void onPulseFinished();
    
    public abstract void onPulseStarted();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\doze\DozeHost.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */