package com.android.keyguard;

import android.os.SystemClock;
import android.telephony.ServiceState;
import com.android.internal.telephony.IccCardConstants.State;

public class KeyguardUpdateMonitorCallback
{
  private boolean mShowing;
  private long mVisibilityChangedCalled;
  
  public void onBootCompleted() {}
  
  public void onClearFailedFacelockAttempts() {}
  
  public void onClockVisibilityChanged() {}
  
  public void onDevicePolicyManagerStateChanged() {}
  
  public void onDeviceProvisioned() {}
  
  public void onDreamingStateChanged(boolean paramBoolean) {}
  
  public void onEmergencyCallAction() {}
  
  public void onFaceUnlockStateChanged(boolean paramBoolean, int paramInt) {}
  
  public void onFacelockStateChanged(int paramInt) {}
  
  public void onFingerprintAcquired(int paramInt) {}
  
  public void onFingerprintAuthFailed() {}
  
  public void onFingerprintAuthenticated(int paramInt) {}
  
  public void onFingerprintError(int paramInt, String paramString) {}
  
  public void onFingerprintHelp(int paramInt, String paramString) {}
  
  public void onFingerprintRunningStateChanged(boolean paramBoolean) {}
  
  public void onFingerprintTimeout() {}
  
  public void onFinishedGoingToSleep(int paramInt) {}
  
  public void onHasLockscreenWallpaperChanged(boolean paramBoolean) {}
  
  public void onKeyguardBouncerChanged(boolean paramBoolean) {}
  
  public void onKeyguardReset() {}
  
  public void onKeyguardVisibilityChanged(boolean paramBoolean) {}
  
  public void onKeyguardVisibilityChangedRaw(boolean paramBoolean)
  {
    long l = SystemClock.elapsedRealtime();
    if ((paramBoolean == this.mShowing) && (l - this.mVisibilityChangedCalled < 1000L)) {
      return;
    }
    onKeyguardVisibilityChanged(paramBoolean);
    this.mVisibilityChangedCalled = l;
    this.mShowing = paramBoolean;
  }
  
  public void onPasswordLockout() {}
  
  public void onPhoneStateChanged(int paramInt) {}
  
  public void onRefreshBatteryInfo(KeyguardUpdateMonitor.BatteryStatus paramBatteryStatus) {}
  
  public void onRefreshCarrierInfo() {}
  
  public void onRingerModeChanged(int paramInt) {}
  
  public void onScreenTurnedOff() {}
  
  public void onScreenTurnedOn() {}
  
  public void onServiceStateChanged(int paramInt, ServiceState paramServiceState) {}
  
  public void onSimStateChanged(int paramInt1, int paramInt2, IccCardConstants.State paramState) {}
  
  public void onStartedGoingToSleep(int paramInt) {}
  
  public void onStartedWakingUp() {}
  
  public void onStrongAuthStateChanged(int paramInt) {}
  
  public void onSystemReady() {}
  
  public void onTimeChanged() {}
  
  public void onTrustChanged(int paramInt) {}
  
  public void onTrustGrantedWithFlags(int paramInt1, int paramInt2) {}
  
  public void onTrustManagedChanged(int paramInt) {}
  
  public void onUserInfoChanged(int paramInt) {}
  
  public void onUserSwitchComplete(int paramInt) {}
  
  public void onUserSwitching(int paramInt) {}
  
  public void onUserUnlocked() {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\KeyguardUpdateMonitorCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */