package com.android.keyguard;

import com.android.internal.widget.LockPatternUtils;

public abstract interface KeyguardSecurityView
{
  public abstract boolean isCheckingPassword();
  
  public abstract boolean needsInput();
  
  public abstract void onPause();
  
  public abstract void onResume(int paramInt);
  
  public abstract void setKeyguardCallback(KeyguardSecurityCallback paramKeyguardSecurityCallback);
  
  public abstract void setLockPatternUtils(LockPatternUtils paramLockPatternUtils);
  
  public abstract void showMessage(String paramString, int paramInt);
  
  public abstract void showPromptReason(int paramInt);
  
  public abstract void startAppearAnimation();
  
  public abstract boolean startDisappearAnimation(Runnable paramRunnable);
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\KeyguardSecurityView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */