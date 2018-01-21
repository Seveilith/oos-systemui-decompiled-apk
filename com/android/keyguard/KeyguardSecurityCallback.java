package com.android.keyguard;

public abstract interface KeyguardSecurityCallback
{
  public abstract void dismiss(boolean paramBoolean);
  
  public abstract void hideSecurityIcon();
  
  public abstract void reportMDMEvent(String paramString1, String paramString2, String paramString3);
  
  public abstract void reportUnlockAttempt(int paramInt1, boolean paramBoolean, int paramInt2);
  
  public abstract void reset();
  
  public abstract void tryToStartFaceLockFromBouncer();
  
  public abstract void userActivity();
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\KeyguardSecurityCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */