package com.android.systemui.statusbar.phone;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.IDeviceIdleController;
import android.os.IDeviceIdleController.Stub;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.Log;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.plugin.LSState;
import com.android.systemui.plugin.PreventModeCtrl;
import com.android.systemui.util.MdmLogger;
import com.android.systemui.util.Utils;
import java.util.List;

public class FingerprintUnlockController
  extends KeyguardUpdateMonitorCallback
{
  private Context mContext;
  private IDeviceIdleController mDeviceIdleService = null;
  private DozeScrimController mDozeScrimController;
  private boolean mDreaming;
  private Handler mHandler = new Handler();
  private boolean mIsFingerprintAuthenticating = false;
  private boolean mIsScreenOffUnlock;
  private KeyguardViewMediator mKeyguardViewMediator;
  private int mMode;
  private boolean mPendingAuthFailed = false;
  private int mPendingAuthenticatedUserId = -1;
  private PhoneStatusBar mPhoneStatusBar;
  private PowerManager mPowerManager;
  private final Runnable mReleaseFingerprintWakeLockRunnable = new Runnable()
  {
    public void run()
    {
      Log.i("FingerprintController", "fp wakelock: TIMEOUT!!");
      FingerprintUnlockController.-wrap0(FingerprintUnlockController.this);
    }
  };
  private ScrimController mScrimController;
  private StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
  private StatusBarWindowManager mStatusBarWindowManager;
  private boolean mTempUnlock = false;
  private String mTopApp;
  private KeyguardUpdateMonitor mUpdateMonitor;
  private PowerManager.WakeLock mWakeLock;
  
  public FingerprintUnlockController(Context paramContext, StatusBarWindowManager paramStatusBarWindowManager, DozeScrimController paramDozeScrimController, KeyguardViewMediator paramKeyguardViewMediator, ScrimController paramScrimController, PhoneStatusBar paramPhoneStatusBar)
  {
    this.mContext = paramContext;
    this.mPowerManager = ((PowerManager)paramContext.getSystemService(PowerManager.class));
    this.mUpdateMonitor = KeyguardUpdateMonitor.getInstance(paramContext);
    this.mUpdateMonitor.registerCallback(this);
    this.mStatusBarWindowManager = paramStatusBarWindowManager;
    this.mDozeScrimController = paramDozeScrimController;
    this.mKeyguardViewMediator = paramKeyguardViewMediator;
    this.mScrimController = paramScrimController;
    this.mPhoneStatusBar = paramPhoneStatusBar;
    this.mDeviceIdleService = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));
  }
  
  private int calculateMode()
  {
    boolean bool = this.mUpdateMonitor.isUnlockingWithFingerprintAllowed();
    Log.d("FingerprintController", "mIsScreenOffUnlock: " + this.mIsScreenOffUnlock + ", isDeviceInteractive = " + this.mUpdateMonitor.isDeviceInteractive());
    if ((!this.mIsScreenOffUnlock) && ((this.mIsScreenOffUnlock) || (!this.mDreaming)) && ((!this.mStatusBarWindowManager.isShowingLiveWallpaper()) || (this.mUpdateMonitor.isDeviceInteractive())))
    {
      if (!this.mStatusBarKeyguardViewManager.isShowing()) {
        break label157;
      }
      if ((this.mStatusBarKeyguardViewManager.isBouncerShowing()) && (bool)) {
        return 6;
      }
    }
    else
    {
      if (!this.mStatusBarKeyguardViewManager.isShowing()) {
        return 4;
      }
      if (bool) {
        return 1;
      }
      return 3;
    }
    if (bool) {
      return 5;
    }
    if (!this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
      return 3;
    }
    label157:
    return 0;
  }
  
  private void changePanelVisibilityByAlpha(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mKeyguardViewMediator.changePanelAlpha(paramInt, KeyguardViewMediator.AUTHENTICATE_IGNORE);
      return;
    }
    this.mKeyguardViewMediator.changePanelAlpha(paramInt, KeyguardViewMediator.AUTHENTICATE_FINGERPRINT);
  }
  
  private void cleanup()
  {
    this.mMode = 0;
    releaseFingerprintWakeLock();
    this.mStatusBarWindowManager.setForceDozeBrightness(false);
    this.mPhoneStatusBar.notifyFpAuthModeChanged();
    this.mIsScreenOffUnlock = false;
    if (this.mTempUnlock)
    {
      this.mKeyguardViewMediator.onEarlyFingerprintUnlockFail();
      this.mTempUnlock = false;
    }
  }
  
  private String getTopApp()
  {
    List localList = ((ActivityManager)this.mContext.getSystemService(ActivityManager.class)).getRunningTasks(1);
    if ((localList == null) || (localList.isEmpty())) {}
    while ((localList.get(0) == null) || (((ActivityManager.RunningTaskInfo)localList.get(0)).topActivity == null)) {
      return null;
    }
    return ((ActivityManager.RunningTaskInfo)localList.get(0)).topActivity.getPackageName();
  }
  
  private void onFingerprintUnlockCancel(int paramInt)
  {
    KeyguardUpdateMonitor localKeyguardUpdateMonitor1 = this.mUpdateMonitor;
    KeyguardUpdateMonitor localKeyguardUpdateMonitor2 = this.mUpdateMonitor;
    boolean bool1 = localKeyguardUpdateMonitor1.getUserCanSkipBouncer(KeyguardUpdateMonitor.getCurrentUser());
    Log.d("FingerprintController", "onFingerprintUnlockCancel: cancelReason = " + paramInt + ", mIsScreenOffUnlock = " + this.mIsScreenOffUnlock + ", failAttemps = " + this.mUpdateMonitor.getFingerprintFailedUnlockAttempts() + ", Authenticating = " + isFingerprintAuthenticating() + ", prevent=" + LSState.getInstance().getPreventModeCtrl().isPreventModeActive() + ", skipBouncer=" + bool1);
    if (isFingerprintAuthenticating())
    {
      changePanelVisibilityByAlpha(1, false);
      boolean bool2 = "1".equals(SystemProperties.get("fingerprint_test"));
      if (((paramInt != 0) || (this.mUpdateMonitor.getFingerprintFailedUnlockAttempts() < 3) || (bool2)) && (!this.mIsScreenOffUnlock)) {}
    }
    try
    {
      this.mDeviceIdleService.setFingerprintResult(false, false);
      this.mPowerManager.goToSleep(SystemClock.uptimeMillis(), 11, 0);
      this.mPhoneStatusBar.getFacelockController().onPreStartedGoingToSleep();
      LSState.getInstance().onFingerprintStartedGoingToSleep();
      for (;;)
      {
        setFingerprintState(false);
        setFingerprintState(false);
        return;
        label231:
        if (((!bool1) && (!this.mIsScreenOffUnlock)) || (this.mIsScreenOffUnlock)) {}
        try
        {
          this.mDeviceIdleService.setFingerprintResult(false, true);
          if (this.mIsScreenOffUnlock) {
            this.mPowerManager.goToSleep(SystemClock.uptimeMillis(), 11, 0);
          }
          this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), "com.android.systemui:FailedAttempts");
          if (this.mIsScreenOffUnlock) {
            this.mPhoneStatusBar.getFacelockController().tryToStartFaceLockAfterScreenOn();
          }
          if ((bool1) || (!this.mIsScreenOffUnlock) || (LSState.getInstance().getPreventModeCtrl().isPreventModeActive())) {
            continue;
          }
          this.mHandler.postDelayed(new Runnable()
          {
            public void run()
            {
              FingerprintUnlockController.-get1(FingerprintUnlockController.this).animateCollapsePanels(2.0F);
            }
          }, 300L);
          continue;
          if (LSState.getInstance().getPreventModeCtrl().isPreventModeActive()) {
            break label231;
          }
          this.mStatusBarKeyguardViewManager.animateCollapsePanels(2.0F);
        }
        catch (Exception localException1)
        {
          for (;;)
          {
            Log.d("FingerprintController", "setFingerprintResult , " + localException1);
          }
        }
      }
    }
    catch (Exception localException2)
    {
      for (;;)
      {
        Log.d("FingerprintController", "setFingerprintResult , " + localException2);
      }
    }
  }
  
  private void onFingerprintUnlockStart()
  {
    boolean bool1 = LSState.getInstance().isFinishedScreenTuredOn();
    this.mDozeScrimController.isPulsing();
    boolean bool2 = this.mStatusBarWindowManager.isShowingLiveWallpaper();
    Log.d("FingerprintController", "onFingerprintUnlockStart, " + bool1 + " , " + this.mDreaming + " , " + bool2 + ", " + this.mTempUnlock + ", top:" + this.mTopApp);
    if ((bool1) || (bool2))
    {
      if ((bool1) && (this.mDreaming) && (this.mKeyguardViewMediator.isShowingAndNotOccluded())) {
        changePanelVisibilityByAlpha(0, false);
      }
      return;
    }
    if (this.mKeyguardViewMediator.isShowingAndNotOccluded())
    {
      changePanelVisibilityByAlpha(0, false);
      if (!Utils.isInFPNotResumeList(this.mTopApp))
      {
        this.mKeyguardViewMediator.onEarlyFingerprintUnlockStart();
        this.mTempUnlock = true;
      }
    }
    try
    {
      this.mDeviceIdleService.setFingerprintResult(true, false);
      this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), "com.android.systemui:UnlockStart");
      this.mIsScreenOffUnlock = true;
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.d("FingerprintController", "setFingerprintResult , " + localException);
      }
    }
  }
  
  private void releaseFingerprintWakeLock()
  {
    if (this.mWakeLock != null)
    {
      this.mHandler.removeCallbacks(this.mReleaseFingerprintWakeLockRunnable);
      Log.i("FingerprintController", "releasing fp wakelock");
      this.mWakeLock.release();
      this.mWakeLock = null;
    }
  }
  
  private void setFingerprintState(boolean paramBoolean)
  {
    if (this.mIsFingerprintAuthenticating != paramBoolean) {
      Log.d("FingerprintController", "setFingerprintState: " + paramBoolean);
    }
    this.mIsFingerprintAuthenticating = paramBoolean;
    this.mKeyguardViewMediator.notifyScreenOffAuthenticate(this.mIsFingerprintAuthenticating, KeyguardViewMediator.AUTHENTICATE_FINGERPRINT);
  }
  
  public void finishKeyguardFadingAway()
  {
    this.mMode = 0;
    if (this.mPhoneStatusBar.getNavigationBarView() != null) {
      this.mPhoneStatusBar.getNavigationBarView().setWakeAndUnlocking(false);
    }
    this.mPhoneStatusBar.notifyFpAuthModeChanged();
  }
  
  public int getMode()
  {
    return this.mMode;
  }
  
  public boolean isFingerprintAuthenticating()
  {
    return this.mIsFingerprintAuthenticating;
  }
  
  public void onDreamingStateChanged(boolean paramBoolean)
  {
    super.onDreamingStateChanged(paramBoolean);
    Log.d("FingerprintController", "onDreamingStateChanged: " + paramBoolean);
    this.mDreaming = paramBoolean;
  }
  
  public void onFingerprintAcquired(int paramInt)
  {
    Trace.beginSection("FingerprintUnlockController#onFingerprintAcquired");
    Log.d("FingerprintController", "onFingerprintAcquired: accquireInfo=" + paramInt + " isPulsing=" + this.mDozeScrimController.isPulsing() + ", " + this.mTempUnlock);
    releaseFingerprintWakeLock();
    if (paramInt == 1000)
    {
      if (!this.mUpdateMonitor.isUnlockingWithFingerprintAllowed())
      {
        Log.d("FingerprintController", "not allow unlock with fingerprint");
        this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), "com.android.systemui:onAcquired");
        return;
      }
      setFingerprintState(true);
      onFingerprintUnlockStart();
      return;
    }
    if ((paramInt == 0) && (this.mTempUnlock)) {
      this.mKeyguardViewMediator.onEarlyNotifyDraw();
    }
    if ((!this.mUpdateMonitor.isDeviceInteractive()) && (this.mDozeScrimController.isPulsing()))
    {
      this.mWakeLock = this.mPowerManager.newWakeLock(1, "wake-and-unlock wakelock");
      Trace.beginSection("acquiring wake-and-unlock");
      this.mWakeLock.acquire();
      Trace.endSection();
      Log.i("FingerprintController", "fingerprint acquired, grabbing fp wakelock");
      this.mHandler.postDelayed(this.mReleaseFingerprintWakeLockRunnable, 15000L);
      this.mStatusBarWindowManager.setForceDozeBrightness(true);
    }
    Trace.endSection();
  }
  
  public void onFingerprintAuthFailed()
  {
    Log.d("FingerprintController", "onFingerprintAuthFailed: " + this.mUpdateMonitor.getFingerprintFailedUnlockAttempts() + ", " + isFingerprintAuthenticating() + ", " + LSState.getInstance().isFinishedScreenTuredOn());
    MdmLogger.log("lock_unlock_failed", "finger", "1");
    if (this.mUpdateMonitor.isGoingToSleep())
    {
      this.mPendingAuthFailed = true;
      return;
    }
    onFingerprintUnlockCancel(0);
    cleanup();
  }
  
  public void onFingerprintAuthenticated(int paramInt)
  {
    Trace.beginSection("FingerprintUnlockController#onFingerprintAuthenticated");
    if (this.mUpdateMonitor.isGoingToSleep())
    {
      this.mPendingAuthenticatedUserId = paramInt;
      Trace.endSection();
      return;
    }
    setFingerprintState(false);
    this.mTempUnlock = false;
    boolean bool = this.mUpdateMonitor.isDeviceInteractive();
    this.mMode = calculateMode();
    if (!bool)
    {
      Log.i("FingerprintController", "fp wakelock: Authenticated, waking up...");
      this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), "android.policy:FINGERPRINT");
    }
    Trace.beginSection("release wake-and-unlock");
    releaseFingerprintWakeLock();
    this.mIsScreenOffUnlock = false;
    try
    {
      this.mDeviceIdleService.setFingerprintResult(false, true);
      if ((this.mMode != 4) || (this.mUpdateMonitor.isKeyguardDone()))
      {
        Log.d("FingerprintController", "onFingerprintAuthenticated: mMode = " + this.mMode);
        if ((this.mMode != 5) && (this.mMode != 2)) {
          break label304;
        }
        MdmLogger.log("lock_unlock_success", "finger", "1");
        Trace.endSection();
        switch (this.mMode)
        {
        case 0: 
        default: 
          if (this.mMode != 2) {
            this.mStatusBarWindowManager.setForceDozeBrightness(false);
          }
          this.mPhoneStatusBar.notifyFpAuthModeChanged();
          Trace.endSection();
          return;
        }
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.d("FingerprintController", "setFingerprintResult , " + localException);
        continue;
        Log.d("FingerprintController", "ignore handleShow");
        this.mKeyguardViewMediator.ignorePendingHandleShow();
        continue;
        label304:
        if (this.mMode != 1) {
          if (this.mMode == 4)
          {
            continue;
            Trace.beginSection("MODE_DISMISS");
            this.mStatusBarKeyguardViewManager.notifyKeyguardAuthenticated(false);
            Trace.endSection();
            continue;
            Trace.beginSection("MODE_UNLOCK or MODE_SHOW_BOUNCER");
            if (!bool) {
              this.mStatusBarKeyguardViewManager.notifyDeviceWakeUpRequested();
            }
            if ((this.mMode == 5) && (bool) && (this.mStatusBarKeyguardViewManager.isOccluded()))
            {
              Log.d("FingerprintController", "keyguardDone when MODE_UNLOCK and Occluded");
              this.mKeyguardViewMediator.keyguardDone(true);
            }
            for (;;)
            {
              Trace.endSection();
              break;
              this.mStatusBarKeyguardViewManager.animateCollapsePanels(1.3F);
            }
            Trace.beginSection("MODE_WAKE_AND_UNLOCK_PULSING");
            this.mPhoneStatusBar.updateMediaMetaData(false, true);
            Trace.endSection();
            Trace.beginSection("MODE_WAKE_AND_UNLOCK");
            this.mStatusBarWindowManager.setStatusBarFocusable(false);
            this.mDozeScrimController.abortPulsing();
            this.mKeyguardViewMediator.onWakeAndUnlocking();
            this.mScrimController.setWakeAndUnlocking();
            if (this.mPhoneStatusBar.getNavigationBarView() != null) {
              this.mPhoneStatusBar.getNavigationBarView().setWakeAndUnlocking(true);
            }
            Trace.endSection();
            continue;
            this.mUpdateMonitor.clearFingerprintRecognized();
          }
        }
      }
    }
  }
  
  public void onFingerprintError(int paramInt, String paramString)
  {
    Log.d("FingerprintController", "onFingerprintError: " + paramString);
    onFingerprintUnlockCancel(2);
    cleanup();
  }
  
  public void onFingerprintHelp(int paramInt, String paramString)
  {
    Log.d("FingerprintController", "onFingerprintHelp: msgId = " + paramInt + ", helpString = " + paramString);
    if (paramInt != -1) {
      onFingerprintUnlockCancel(1);
    }
    cleanup();
  }
  
  public void onFingerprintTimeout()
  {
    onFingerprintUnlockCancel(3);
    cleanup();
  }
  
  public void onFinishedGoingToSleep(int paramInt)
  {
    Trace.beginSection("FingerprintUnlockController#onFinishedGoingToSleep");
    if (this.mPendingAuthenticatedUserId != -1) {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          FingerprintUnlockController.this.onFingerprintAuthenticated(FingerprintUnlockController.-get0(FingerprintUnlockController.this));
        }
      });
    }
    this.mPendingAuthenticatedUserId = -1;
    if (this.mPendingAuthFailed) {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          FingerprintUnlockController.this.onFingerprintAuthFailed();
        }
      });
    }
    this.mPendingAuthFailed = false;
    Trace.endSection();
  }
  
  public void onKeyguardVisibilityChanged(boolean paramBoolean)
  {
    Log.d("FingerprintController", "onKeyguardVisibilityChanged: " + paramBoolean + " , " + this.mIsFingerprintAuthenticating);
    if ((paramBoolean) && (this.mIsFingerprintAuthenticating))
    {
      changePanelVisibilityByAlpha(0, false);
      return;
    }
    resetMode();
  }
  
  public void onScreenTurnedOn()
  {
    this.mIsScreenOffUnlock = false;
  }
  
  public void onStartedGoingToSleep(int paramInt)
  {
    this.mPendingAuthenticatedUserId = -1;
    this.mPendingAuthFailed = false;
    this.mTopApp = getTopApp();
  }
  
  public void resetMode()
  {
    this.mMode = 0;
    changePanelVisibilityByAlpha(1, true);
  }
  
  public void setStatusBarKeyguardViewManager(StatusBarKeyguardViewManager paramStatusBarKeyguardViewManager)
  {
    this.mStatusBarKeyguardViewManager = paramStatusBarKeyguardViewManager;
  }
  
  public void startKeyguardFadingAway()
  {
    this.mHandler.postDelayed(new Runnable()
    {
      public void run()
      {
        FingerprintUnlockController.-get2(FingerprintUnlockController.this).setForceDozeBrightness(false);
      }
    }, 96L);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\FingerprintUnlockController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */