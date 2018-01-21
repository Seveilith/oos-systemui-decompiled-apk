package com.android.keyguard;

public abstract interface ViewMediatorCallback
{
  public abstract int getBouncerPromptReason();
  
  public abstract boolean isScreenOn();
  
  public abstract void keyguardDone(boolean paramBoolean);
  
  public abstract void keyguardDoneDrawing();
  
  public abstract void keyguardDonePending(boolean paramBoolean);
  
  public abstract void keyguardGone();
  
  public abstract void playTrustedSound();
  
  public abstract void readyForKeyguardDone();
  
  public abstract void reportMDMEvent(String paramString1, String paramString2, String paramString3);
  
  public abstract void resetKeyguard();
  
  public abstract void setNeedsInput(boolean paramBoolean);
  
  public abstract void startPowerKeyLaunchCamera();
  
  public abstract void tryToStartFaceLockFromBouncer();
  
  public abstract void userActivity();
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\ViewMediatorCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */