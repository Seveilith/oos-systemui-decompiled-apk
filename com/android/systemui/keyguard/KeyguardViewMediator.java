package com.android.systemui.keyguard;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AlarmManager;
import android.app.IActivityManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.StatusBarManager;
import android.app.admin.DevicePolicyManager;
import android.app.trust.TrustManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.telephony.SubscriptionManager;
import android.util.EventLog;
import android.util.Log;
import android.util.LogPrinter;
import android.util.Slog;
import android.view.IWindowManager;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import android.view.WindowManagerGlobal;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.android.internal.policy.IKeyguardDrawnCallback;
import com.android.internal.policy.IKeyguardExitCallback;
import com.android.internal.policy.IKeyguardStateCallback;
import com.android.internal.telephony.IccCardConstants.State;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardDisplayManager;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitor.StrongAuthTracker;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.SystemUI;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.classifier.FalsingManager;
import com.android.systemui.plugin.LSState;
import com.android.systemui.statusbar.phone.FingerprintUnlockController;
import com.android.systemui.statusbar.phone.OPFacelockController;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.phone.StatusBarWindowManager;
import com.android.systemui.util.MdmLogger;
import com.android.systemui.util.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

public class KeyguardViewMediator
  extends SystemUI
{
  public static int AUTHENTICATE_FACEUNLOCK = 2;
  public static int AUTHENTICATE_FINGERPRINT;
  public static int AUTHENTICATE_IGNORE;
  private static boolean DEBUG_MESSAGE = false;
  private static boolean DEBUG_SCREEN_ON = false;
  private static final Intent USER_PRESENT_INTENT = new Intent("android.intent.action.USER_PRESENT").addFlags(603979776);
  private AlarmManager mAlarmManager;
  private AudioManager mAudioManager;
  private int mAuthenticatingType = AUTHENTICATE_IGNORE;
  private PhoneStatusBar mBar;
  private boolean mBootCompleted;
  private boolean mBootSendUserPresent;
  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      int i;
      if ("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_KEYGUARD".equals(paramAnonymousIntent.getAction()))
      {
        i = paramAnonymousIntent.getIntExtra("seq", 0);
        Log.d("KeyguardViewMediator", "received DELAYED_KEYGUARD_ACTION with seq = " + i + ", mDelayedShowingSequence = " + KeyguardViewMediator.-get2(KeyguardViewMediator.this));
        paramAnonymousIntent = KeyguardViewMediator.this;
        paramAnonymousContext = paramAnonymousIntent;
      }
      for (;;)
      {
        try
        {
          if (KeyguardViewMediator.-get2(KeyguardViewMediator.this) == i)
          {
            KeyguardViewMediator.-wrap2(KeyguardViewMediator.this, null);
            paramAnonymousContext = paramAnonymousIntent;
          }
          return;
        }
        finally
        {
          paramAnonymousContext = finally;
          throw paramAnonymousContext;
        }
        if (!"com.android.internal.policy.impl.PhoneWindowManager.DELAYED_LOCK".equals(paramAnonymousIntent.getAction())) {
          continue;
        }
        i = paramAnonymousIntent.getIntExtra("seq", 0);
        int j = paramAnonymousIntent.getIntExtra("android.intent.extra.USER_ID", 0);
        if (j == 0) {
          continue;
        }
        paramAnonymousIntent = KeyguardViewMediator.this;
        paramAnonymousContext = paramAnonymousIntent;
        try
        {
          if (KeyguardViewMediator.-get1(KeyguardViewMediator.this) != i) {
            continue;
          }
          KeyguardViewMediator.-wrap21(KeyguardViewMediator.this, j);
          paramAnonymousContext = paramAnonymousIntent;
        }
        finally {}
      }
    }
  };
  private int mDelayedProfileShowingSequence;
  private int mDelayedShowingSequence;
  private boolean mDeviceInteractive;
  private IKeyguardDrawnCallback mDrawnCallback;
  private IKeyguardExitCallback mExitSecureCallback;
  private boolean mExternallyEnabled = true;
  private boolean mGoingToSleep;
  private PowerManager.WakeLock mHandleKeyguardMessageWakeLock;
  private Handler mHandler = new Handler(Looper.myLooper(), null, true)
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      boolean bool2 = true;
      boolean bool3 = true;
      boolean bool4 = true;
      boolean bool1 = true;
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 2: 
        KeyguardViewMediator.-wrap18(KeyguardViewMediator.this, (Bundle)paramAnonymousMessage.obj);
        return;
      case 3: 
        KeyguardViewMediator.-wrap6(KeyguardViewMediator.this);
        return;
      case 4: 
        KeyguardViewMediator.-wrap16(KeyguardViewMediator.this);
        return;
      case 5: 
        Trace.beginSection("KeyguardViewMediator#handleMessage VERIFY_UNLOCK");
        KeyguardViewMediator.-wrap20(KeyguardViewMediator.this);
        Trace.endSection();
        return;
      case 24: 
        KeyguardViewMediator.-wrap13(KeyguardViewMediator.this);
        return;
      case 6: 
        KeyguardViewMediator.-wrap9(KeyguardViewMediator.this);
        return;
      case 7: 
        Trace.beginSection("KeyguardViewMediator#handleMessage NOTIFY_SCREEN_TURNING_ON");
        KeyguardViewMediator.-wrap12(KeyguardViewMediator.this, (IKeyguardDrawnCallback)paramAnonymousMessage.obj);
        Trace.endSection();
        return;
      case 22: 
        Trace.beginSection("KeyguardViewMediator#handleMessage NOTIFY_SCREEN_TURNED_ON");
        KeyguardViewMediator.-wrap11(KeyguardViewMediator.this);
        Trace.endSection();
        return;
      case 23: 
        KeyguardViewMediator.-wrap10(KeyguardViewMediator.this);
        return;
      case 21: 
        Trace.beginSection("KeyguardViewMediator#handleMessage NOTIFY_STARTED_WAKING_UP");
        KeyguardViewMediator.-wrap14(KeyguardViewMediator.this);
        Trace.endSection();
        return;
      case 9: 
        Trace.beginSection("KeyguardViewMediator#handleMessage KEYGUARD_DONE");
        ??? = KeyguardViewMediator.this;
        if (paramAnonymousMessage.arg1 != 0) {}
        for (;;)
        {
          KeyguardViewMediator.-wrap8(???, bool1);
          Trace.endSection();
          return;
          bool1 = false;
        }
      case 10: 
        Trace.beginSection("KeyguardViewMediator#handleMessage KEYGUARD_DONE_DRAWING");
        KeyguardViewMediator.-wrap7(KeyguardViewMediator.this);
        Trace.endSection();
        return;
      case 12: 
        Trace.beginSection("KeyguardViewMediator#handleMessage SET_OCCLUDED");
        ??? = KeyguardViewMediator.this;
        if (paramAnonymousMessage.arg1 != 0)
        {
          bool1 = true;
          if (paramAnonymousMessage.arg2 == 0) {
            break label427;
          }
        }
        for (;;)
        {
          KeyguardViewMediator.-wrap17(???, bool1, bool2);
          Trace.endSection();
          return;
          bool1 = false;
          break;
          bool2 = false;
        }
      case 13: 
        synchronized (KeyguardViewMediator.this)
        {
          KeyguardViewMediator.-wrap2(KeyguardViewMediator.this, (Bundle)paramAnonymousMessage.obj);
          return;
        }
      case 17: 
        ??? = KeyguardViewMediator.this;
        if (paramAnonymousMessage.arg1 == 1) {}
        for (bool1 = bool3;; bool1 = false)
        {
          ???.handleDismiss(bool1);
          return;
        }
      case 104: 
        ??? = KeyguardViewMediator.this;
        if (paramAnonymousMessage.arg1 == 1) {}
        for (bool1 = bool4;; bool1 = false)
        {
          ???.handleForceDismiss(bool1);
          return;
        }
      case 25: 
        Log.d("KeyguardViewMediator", "START_KEYGUARD_EXIT_ANIM_TIMEOUT");
      case 18: 
        Trace.beginSection("KeyguardViewMediator#handleMessage START_KEYGUARD_EXIT_ANIM");
        paramAnonymousMessage = (KeyguardViewMediator.StartKeyguardExitAnimParams)paramAnonymousMessage.obj;
        KeyguardViewMediator.-wrap19(KeyguardViewMediator.this, paramAnonymousMessage.startTime, paramAnonymousMessage.fadeoutDuration);
        FalsingManager.getInstance(KeyguardViewMediator.this.mContext).onSucccessfulUnlock();
        Trace.endSection();
        return;
      case 20: 
        Trace.beginSection("KeyguardViewMediator#handleMessage KEYGUARD_DONE_PENDING_TIMEOUT");
        Log.w("KeyguardViewMediator", "Timeout while waiting for activity drawn!");
        Trace.endSection();
      case 19: 
        KeyguardViewMediator.-wrap15(KeyguardViewMediator.this);
        return;
      case 101: 
        KeyguardViewMediator.-wrap5(KeyguardViewMediator.this);
        return;
      case 102: 
        label427:
        KeyguardViewMediator.-wrap3(KeyguardViewMediator.this);
        return;
      }
      KeyguardViewMediator.-wrap4(KeyguardViewMediator.this);
    }
  };
  private Animation mHideAnimation;
  private boolean mHideAnimationRun = false;
  private boolean mHiding;
  private boolean mIgnoreHandleShow = false;
  private boolean mInputRestricted;
  private boolean mIsPerUserLock;
  private KeyguardDisplayManager mKeyguardDisplayManager;
  private boolean mKeyguardDonePending = false;
  private final Runnable mKeyguardGoingAwayRunnable = new Runnable()
  {
    public void run()
    {
      Trace.beginSection("KeyguardViewMediator.mKeyGuardGoingAwayRunnable");
      try
      {
        KeyguardViewMediator.-get14(KeyguardViewMediator.this).keyguardGoingAway();
        j = 0;
        if ((KeyguardViewMediator.-get14(KeyguardViewMediator.this).shouldDisableWindowAnimationsForUnlock()) || (KeyguardViewMediator.-get17(KeyguardViewMediator.this))) {
          break label136;
        }
        if (!KeyguardViewMediator.-get16(KeyguardViewMediator.this).isFacelockUnlocking()) {}
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          int i;
          Log.e("KeyguardViewMediator", "Error while calling WindowManager", localRemoteException);
          continue;
          int j = 2;
        }
      }
      i = j;
      if (KeyguardViewMediator.-get14(KeyguardViewMediator.this).isGoingToNotificationShade()) {
        i = j | 0x1;
      }
      j = i;
      if (KeyguardViewMediator.-get14(KeyguardViewMediator.this).isUnlockWithWallpaper()) {
        j = i | 0x4;
      }
      Log.d("KeyguardViewMediator", "keyguardGoingAway begin");
      ActivityManagerNative.getDefault().keyguardGoingAway(j);
      Log.d("KeyguardViewMediator", "keyguardGoingAway end");
      Trace.endSection();
    }
  };
  private final ArrayList<IKeyguardStateCallback> mKeyguardStateCallbacks = new ArrayList();
  private IccCardConstants.State[] mLastSimStateSlot = new IccCardConstants.State[2];
  private boolean mLockLater;
  private LockPatternUtils mLockPatternUtils;
  private int mLockSoundId;
  private int mLockSoundStreamId;
  private float mLockSoundVolume;
  private SoundPool mLockSounds;
  private boolean mNeedToReshowWhenReenabled = false;
  private boolean mOccluded = false;
  private PowerManager mPM;
  private boolean mPendingLock;
  private boolean mPendingReset;
  private int mPhoneState = 0;
  private boolean mPowerKeyCameraLaunching = false;
  private ScrimController mScrimController;
  private SearchManager mSearchManager;
  private PowerManager.WakeLock mShowKeyguardWakeLock;
  private boolean mShowing;
  private StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
  private StatusBarManager mStatusBarManager;
  private StatusBarWindowManager mStatusBarWindowManager;
  private boolean mSwitchingUser;
  private boolean mSystemReady;
  private boolean mTempUnlock = false;
  private TrustManager mTrustManager;
  private int mTrustedSoundId;
  private int mUiSoundsStreamType;
  private int mUnlockSoundId;
  private boolean mUnlockSoundPlayed = false;
  KeyguardUpdateMonitorCallback mUpdateCallback = new KeyguardUpdateMonitorCallback()
  {
    public void onClockVisibilityChanged()
    {
      KeyguardViewMediator.-wrap1(KeyguardViewMediator.this);
    }
    
    public void onDeviceProvisioned()
    {
      KeyguardViewMediator.-wrap27(KeyguardViewMediator.this);
      synchronized (KeyguardViewMediator.this)
      {
        if (KeyguardViewMediator.this.mustNotUnlockCurrentUser()) {
          KeyguardViewMediator.-wrap2(KeyguardViewMediator.this, null);
        }
        return;
      }
    }
    
    public void onFingerprintAuthFailed()
    {
      int i = KeyguardUpdateMonitor.getCurrentUser();
      if (KeyguardViewMediator.-get12(KeyguardViewMediator.this).isSecure(i)) {
        KeyguardViewMediator.-get12(KeyguardViewMediator.this).getDevicePolicyManager().reportFailedFingerprintAttempt(i);
      }
    }
    
    public void onFingerprintAuthenticated(int paramAnonymousInt)
    {
      if (KeyguardViewMediator.-get12(KeyguardViewMediator.this).isSecure(paramAnonymousInt)) {
        KeyguardViewMediator.-get12(KeyguardViewMediator.this).getDevicePolicyManager().reportSuccessfulFingerprintAttempt(paramAnonymousInt);
      }
    }
    
    public void onHasLockscreenWallpaperChanged(boolean paramAnonymousBoolean)
    {
      synchronized (KeyguardViewMediator.this)
      {
        KeyguardViewMediator.-wrap22(KeyguardViewMediator.this, paramAnonymousBoolean);
        return;
      }
    }
    
    public void onPhoneStateChanged(int paramAnonymousInt)
    {
      synchronized (KeyguardViewMediator.this)
      {
        Log.d("KeyguardViewMediator", "onPhoneStateChanged to " + paramAnonymousInt);
        KeyguardViewMediator.-set2(KeyguardViewMediator.this, paramAnonymousInt);
        if (paramAnonymousInt == 0)
        {
          boolean bool = KeyguardViewMediator.-get3(KeyguardViewMediator.this);
          if (!bool) {
            break label60;
          }
        }
        label60:
        while (!KeyguardViewMediator.-get4(KeyguardViewMediator.this)) {
          return;
        }
        Log.d("KeyguardViewMediator", "screen is off and call ended, let's make sure the keyguard is showing");
      }
    }
    
    public void onSimStateChanged(int paramAnonymousInt1, int paramAnonymousInt2, IccCardConstants.State paramAnonymousState)
    {
      Log.d("KeyguardViewMediator", "onSimStateChanged(subId=" + paramAnonymousInt1 + ", slotId=" + paramAnonymousInt2 + ",state=" + paramAnonymousState + ")");
      paramAnonymousInt1 = KeyguardViewMediator.-get10(KeyguardViewMediator.this).size();
      boolean bool3 = KeyguardViewMediator.-get16(KeyguardViewMediator.this).isSimPinSecure();
      paramAnonymousInt1 -= 1;
      for (;;)
      {
        if (paramAnonymousInt1 >= 0) {
          try
          {
            ((IKeyguardStateCallback)KeyguardViewMediator.-get10(KeyguardViewMediator.this).get(paramAnonymousInt1)).onSimSecureStateChanged(bool3);
            paramAnonymousInt1 -= 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;)
            {
              Slog.w("KeyguardViewMediator", "Failed to call onSimSecureStateChanged", localRemoteException);
              if ((localRemoteException instanceof DeadObjectException)) {
                KeyguardViewMediator.-get10(KeyguardViewMediator.this).remove(paramAnonymousInt1);
              }
            }
          }
        }
      }
      boolean bool1 = true;
      boolean bool2 = true;
      if ((paramAnonymousInt2 == 0) || (paramAnonymousInt2 == 1))
      {
        bool1 = bool2;
        if (KeyguardViewMediator.-get11(KeyguardViewMediator.this)[paramAnonymousInt2] == paramAnonymousState) {
          bool1 = false;
        }
        KeyguardViewMediator.-get11(KeyguardViewMediator.this)[paramAnonymousInt2] = paramAnonymousState;
      }
      switch (-getcom-android-internal-telephony-IccCardConstants$StateSwitchesValues()[paramAnonymousState.ordinal()])
      {
      default: 
        Log.v("KeyguardViewMediator", "Ignoring state: " + paramAnonymousState);
        return;
      case 1: 
      case 2: 
        
      case 4: 
      case 5: 
      case 3: 
        for (;;)
        {
          try
          {
            if (KeyguardViewMediator.-wrap0(KeyguardViewMediator.this))
            {
              if (!KeyguardViewMediator.-get13(KeyguardViewMediator.this))
              {
                Log.d("KeyguardViewMediator", "ICC_ABSENT isn't showing, we need to show the keyguard since the device isn't provisioned yet.");
                KeyguardViewMediator.-wrap2(KeyguardViewMediator.this, null);
              }
            }
            else {
              return;
            }
            KeyguardViewMediator.-wrap26(KeyguardViewMediator.this);
            continue;
          }
          finally {}
          try
          {
            if (!KeyguardViewMediator.-get13(KeyguardViewMediator.this))
            {
              Log.d("KeyguardViewMediator", "INTENT_VALUE_ICC_LOCKED and keygaurd isn't showing; need to show keyguard so user can enter sim pin");
              KeyguardViewMediator.-wrap2(KeyguardViewMediator.this, null);
              continue;
            }
          }
          finally {}
          KeyguardViewMediator.-wrap26(KeyguardViewMediator.this);
          continue;
          try
          {
            if (!KeyguardViewMediator.-get13(KeyguardViewMediator.this))
            {
              Log.d("KeyguardViewMediator", "PERM_DISABLED and keygaurd isn't showing.");
              KeyguardViewMediator.-wrap2(KeyguardViewMediator.this, null);
              continue;
            }
          }
          finally {}
          Log.d("KeyguardViewMediator", "PERM_DISABLED, resetStateLocked toshow permanently disabled message in lockscreen.");
          KeyguardViewMediator.-wrap26(KeyguardViewMediator.this);
        }
      }
      bool2 = KeyguardViewMediator.-get14(KeyguardViewMediator.this).isBouncerShowing();
      boolean bool4 = KeyguardViewMediator.-get12(KeyguardViewMediator.this).isSecure(KeyguardUpdateMonitor.getCurrentUser());
      do
      {
        try
        {
          if (!KeyguardViewMediator.-get13(KeyguardViewMediator.this)) {
            break;
          }
          Log.d("KeyguardViewMediator", "skip ready check isB:" + bool2 + " isS:" + bool4 + " simPinS:" + bool3 + " needReset:" + bool1 + "  isChecking:" + KeyguardViewMediator.-get14(KeyguardViewMediator.this).isCheckingPassword());
          if ((!bool2) || (!bool4) || (bool3))
          {
            KeyguardViewMediator.-wrap26(KeyguardViewMediator.this);
            break;
          }
        }
        finally {}
      } while ((bool1) && (!KeyguardViewMediator.-get14(KeyguardViewMediator.this).isCheckingPassword()));
      Log.d("KeyguardViewMediator", "not resetState");
    }
    
    public void onTrustChanged(int paramAnonymousInt)
    {
      if (paramAnonymousInt == KeyguardUpdateMonitor.getCurrentUser()) {}
      synchronized (KeyguardViewMediator.this)
      {
        KeyguardViewMediator.-wrap23(KeyguardViewMediator.this, KeyguardViewMediator.-get16(KeyguardViewMediator.this).getUserHasTrust(paramAnonymousInt));
        return;
      }
    }
    
    public void onUserInfoChanged(int paramAnonymousInt) {}
    
    public void onUserSwitchComplete(int paramAnonymousInt)
    {
      KeyguardViewMediator.-set4(KeyguardViewMediator.this, false);
      if (paramAnonymousInt != 0)
      {
        UserInfo localUserInfo = UserManager.get(KeyguardViewMediator.this.mContext).getUserInfo(paramAnonymousInt);
        if ((localUserInfo == null) || (localUserInfo.isGuest()) || (!localUserInfo.isDemo())) {}
      }
    }
    
    public void onUserSwitching(int paramAnonymousInt)
    {
      synchronized (KeyguardViewMediator.this)
      {
        KeyguardViewMediator.-set4(KeyguardViewMediator.this, true);
        KeyguardViewMediator.-wrap25(KeyguardViewMediator.this);
        KeyguardViewMediator.-wrap26(KeyguardViewMediator.this);
        KeyguardViewMediator.-wrap1(KeyguardViewMediator.this);
        return;
      }
    }
  };
  private KeyguardUpdateMonitor mUpdateMonitor;
  ViewMediatorCallback mViewMediatorCallback = new ViewMediatorCallback()
  {
    public int getBouncerPromptReason()
    {
      int i = ActivityManager.getCurrentUser();
      boolean bool3 = KeyguardViewMediator.-get15(KeyguardViewMediator.this).isTrustUsuallyManaged(i);
      boolean bool1;
      if (!KeyguardViewMediator.-get16(KeyguardViewMediator.this).isUnlockWithFingerprintPossible(i))
      {
        bool1 = KeyguardViewMediator.-get16(KeyguardViewMediator.this).isUnlockWithFacelockPossible();
        if (bool3) {
          break label107;
        }
      }
      int j;
      label107:
      for (boolean bool2 = bool1;; bool2 = true)
      {
        KeyguardUpdateMonitor.StrongAuthTracker localStrongAuthTracker = KeyguardViewMediator.-get16(KeyguardViewMediator.this).getStrongAuthTracker();
        j = localStrongAuthTracker.getStrongAuthForUser(i);
        if ((bool2) && (!localStrongAuthTracker.hasUserAuthenticatedSinceBoot())) {
          break label113;
        }
        if ((!bool1) || (!KeyguardViewMediator.-get16(KeyguardViewMediator.this).hasFingerprintUnlockTimedOut(i))) {
          break label115;
        }
        return 2;
        bool1 = true;
        break;
      }
      label113:
      return 1;
      label115:
      if ((bool2) && ((j & 0x2) != 0)) {
        return 3;
      }
      if ((bool3) && ((j & 0x4) != 0)) {
        return 4;
      }
      if ((bool2) && ((j & 0x8) != 0)) {
        return 5;
      }
      return 0;
    }
    
    public boolean isScreenOn()
    {
      return KeyguardViewMediator.-get3(KeyguardViewMediator.this);
    }
    
    public void keyguardDone(boolean paramAnonymousBoolean)
    {
      if (!KeyguardViewMediator.-get9(KeyguardViewMediator.this)) {
        KeyguardViewMediator.this.keyguardDone(true);
      }
      if (paramAnonymousBoolean) {
        KeyguardViewMediator.-get16(KeyguardViewMediator.this).reportSuccessfulStrongAuthUnlockAttempt();
      }
    }
    
    public void keyguardDoneDrawing()
    {
      Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#keyguardDoneDrawing");
      KeyguardViewMediator.-get6(KeyguardViewMediator.this).sendEmptyMessage(10);
      Trace.endSection();
    }
    
    public void keyguardDonePending(boolean paramAnonymousBoolean)
    {
      Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#keyguardDonePending");
      if (KeyguardViewMediator.-get16(KeyguardViewMediator.this).isFacelockUnlocking()) {
        KeyguardViewMediator.this.changePanelAlpha(1, KeyguardViewMediator.AUTHENTICATE_FACEUNLOCK);
      }
      KeyguardViewMediator.-set1(KeyguardViewMediator.this, true);
      KeyguardViewMediator.-set0(KeyguardViewMediator.this, true);
      KeyguardViewMediator.-get14(KeyguardViewMediator.this).startPreHideAnimation(null);
      KeyguardViewMediator.-get6(KeyguardViewMediator.this).sendEmptyMessageDelayed(20, 3000L);
      if (paramAnonymousBoolean) {
        KeyguardViewMediator.-get16(KeyguardViewMediator.this).reportSuccessfulStrongAuthUnlockAttempt();
      }
      Trace.endSection();
    }
    
    public void keyguardGone()
    {
      Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#keyguardGone");
      KeyguardViewMediator.-get8(KeyguardViewMediator.this).hide();
      Trace.endSection();
    }
    
    public void playTrustedSound()
    {
      KeyguardViewMediator.-wrap24(KeyguardViewMediator.this);
    }
    
    public void readyForKeyguardDone()
    {
      Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#readyForKeyguardDone");
      if (KeyguardViewMediator.-get9(KeyguardViewMediator.this)) {
        KeyguardViewMediator.this.keyguardDone(true);
      }
      Trace.endSection();
    }
    
    public void reportMDMEvent(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
    {
      MdmLogger.log(paramAnonymousString1, paramAnonymousString2, paramAnonymousString3);
    }
    
    public void resetKeyguard()
    {
      KeyguardViewMediator.-wrap26(KeyguardViewMediator.this);
    }
    
    public void setNeedsInput(boolean paramAnonymousBoolean)
    {
      KeyguardViewMediator.-get14(KeyguardViewMediator.this).setNeedsInput(paramAnonymousBoolean);
    }
    
    public void startPowerKeyLaunchCamera()
    {
      Log.d("KeyguardViewMediator", "startPowerKeyLaunchCamera");
      KeyguardViewMediator.-set3(KeyguardViewMediator.this, true);
      Message localMessage = KeyguardViewMediator.-get6(KeyguardViewMediator.this).obtainMessage(25, new KeyguardViewMediator.StartKeyguardExitAnimParams(SystemClock.uptimeMillis() + KeyguardViewMediator.-get7(KeyguardViewMediator.this).getStartOffset(), KeyguardViewMediator.-get7(KeyguardViewMediator.this).getDuration(), null));
      KeyguardViewMediator.-get6(KeyguardViewMediator.this).sendMessageDelayed(localMessage, 1000L);
    }
    
    public void tryToStartFaceLockFromBouncer()
    {
      if ((KeyguardViewMediator.-get0(KeyguardViewMediator.this) != null) && (KeyguardViewMediator.-get0(KeyguardViewMediator.this).getFacelockController() != null)) {
        KeyguardViewMediator.-get0(KeyguardViewMediator.this).getFacelockController().tryToStartFaceLock();
      }
    }
    
    public void userActivity()
    {
      KeyguardViewMediator.this.userActivity();
    }
  };
  private IWindowManager mWM;
  private boolean mWaitingUntilKeyguardVisible = false;
  private boolean mWakeAndUnlocking;
  
  static
  {
    AUTHENTICATE_IGNORE = 0;
    AUTHENTICATE_FINGERPRINT = 1;
  }
  
  private void adjustStatusBarLocked()
  {
    if (this.mStatusBarManager == null) {
      this.mStatusBarManager = ((StatusBarManager)this.mContext.getSystemService("statusbar"));
    }
    if (this.mStatusBarManager == null) {
      Log.w("KeyguardViewMediator", "Could not get status bar manager");
    }
    int j;
    do
    {
      return;
      int i = 0;
      if (this.mShowing) {
        i = 0x1000000 | 0x2000000;
      }
      j = i;
      if (isShowingAndNotOccluded()) {
        j = i | 0x200000;
      }
    } while ((this.mContext instanceof Activity));
    this.mStatusBarManager.disable(j);
  }
  
  private void cancelDoKeyguardForChildProfilesLocked()
  {
    this.mDelayedProfileShowingSequence += 1;
  }
  
  private void cancelDoKeyguardLaterLocked()
  {
    this.mDelayedShowingSequence += 1;
  }
  
  private void doKeyguardForChildProfilesLocked()
  {
    int[] arrayOfInt = UserManager.get(this.mContext).getEnabledProfileIds(UserHandle.myUserId());
    int i = 0;
    int j = arrayOfInt.length;
    while (i < j)
    {
      int k = arrayOfInt[i];
      if (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(k)) {
        lockProfile(k);
      }
      i += 1;
    }
  }
  
  private void doKeyguardLaterForChildProfilesLocked()
  {
    int[] arrayOfInt = UserManager.get(this.mContext).getEnabledProfileIds(UserHandle.myUserId());
    int j = arrayOfInt.length;
    int i = 0;
    if (i < j)
    {
      int k = arrayOfInt[i];
      long l1;
      if (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(k))
      {
        l1 = getLockTimeout(k);
        if (l1 != 0L) {
          break label67;
        }
        doKeyguardForChildProfilesLocked();
      }
      for (;;)
      {
        i += 1;
        break;
        label67:
        long l2 = SystemClock.elapsedRealtime();
        Object localObject = new Intent("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_LOCK");
        ((Intent)localObject).putExtra("seq", this.mDelayedProfileShowingSequence);
        ((Intent)localObject).putExtra("android.intent.extra.USER_ID", k);
        ((Intent)localObject).addFlags(268435456);
        localObject = PendingIntent.getBroadcast(this.mContext, 0, (Intent)localObject, 268435456);
        this.mAlarmManager.setExactAndAllowWhileIdle(2, l2 + l1, (PendingIntent)localObject);
      }
    }
  }
  
  private void doKeyguardLaterLocked()
  {
    long l = getLockTimeout(KeyguardUpdateMonitor.getCurrentUser());
    if (l == 0L)
    {
      doKeyguardLocked(null);
      return;
    }
    doKeyguardLaterLocked(l);
  }
  
  private void doKeyguardLaterLocked(long paramLong)
  {
    long l = SystemClock.elapsedRealtime();
    Object localObject = new Intent("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_KEYGUARD");
    ((Intent)localObject).putExtra("seq", this.mDelayedShowingSequence);
    ((Intent)localObject).addFlags(268435456);
    localObject = PendingIntent.getBroadcast(this.mContext, 0, (Intent)localObject, 268435456);
    this.mAlarmManager.setExactAndAllowWhileIdle(2, l + paramLong, (PendingIntent)localObject);
    Log.d("KeyguardViewMediator", "setting alarm to turn off keyguard, seq = " + this.mDelayedShowingSequence);
    doKeyguardLaterForChildProfilesLocked();
  }
  
  private void doKeyguardLocked(Bundle paramBundle)
  {
    if (!this.mExternallyEnabled)
    {
      Log.d("KeyguardViewMediator", "doKeyguard: not showing because externally disabled");
      return;
    }
    int i;
    if (this.mStatusBarKeyguardViewManager.isShowing())
    {
      i = 0;
      if (paramBundle != null) {
        i = paramBundle.getInt("sleep_reason", 0);
      }
      if (i != 11) {
        resetStateLocked();
      }
      Log.d("KeyguardViewMediator", "doKeyguard: not showing because it is already showing , " + i);
      return;
    }
    if ((mustNotUnlockCurrentUser()) && (this.mUpdateMonitor.isDeviceProvisioned())) {}
    try
    {
      label218:
      label223:
      label228:
      do
      {
        ActivityManagerNative.getDefault().setKeyguardDone(false);
        this.mUpdateMonitor.onKeyguardDone(false);
        Log.d("KeyguardViewMediator", "doKeyguard: showing the lock screen");
        showLocked(paramBundle);
        return;
        if (SystemProperties.getBoolean("keyguard.no_require_sim", false))
        {
          i = 0;
          boolean bool1 = SubscriptionManager.isValidSubscriptionId(this.mUpdateMonitor.getNextSubIdForState(IccCardConstants.State.ABSENT));
          boolean bool2 = SubscriptionManager.isValidSubscriptionId(this.mUpdateMonitor.getNextSubIdForState(IccCardConstants.State.PERM_DISABLED));
          if (this.mUpdateMonitor.isSimPinSecure()) {
            break label218;
          }
          if ((!bool1) && (!bool2)) {
            break label223;
          }
        }
        for (;;)
        {
          if ((i != 0) || (!shouldWaitForProvisioning())) {
            break label228;
          }
          Log.d("KeyguardViewMediator", "doKeyguard: not showing because device isn't provisioned and the sim is not locked or missing");
          return;
          i = 1;
          break;
          i = 1;
          continue;
          i = 0;
        }
        if ((this.mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser())) && (i == 0)) {
          break;
        }
      } while (!this.mLockPatternUtils.checkVoldPassword(KeyguardUpdateMonitor.getCurrentUser()));
      setShowingLocked(false);
      hideLocked();
      this.mUpdateMonitor.reportSuccessfulStrongAuthUnlockAttempt();
      return;
      Log.d("KeyguardViewMediator", "doKeyguard: not showing because lockscreen is off");
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.w("KeyguardViewMediator", "Exception e = " + localException.toString());
      }
    }
  }
  
  private long getLockTimeout(int paramInt)
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    long l1 = Settings.Secure.getInt(localContentResolver, "lock_screen_lock_after_timeout", 5000);
    long l2 = this.mLockPatternUtils.getDevicePolicyManager().getMaximumTimeToLockForUserAndProfiles(paramInt);
    if (l2 <= 0L) {
      return l1;
    }
    return Math.max(Math.min(l2 - Math.max(Settings.System.getInt(localContentResolver, "screen_off_timeout", 30000), 0L), l1), 0L);
  }
  
  private void handleEarlyNotfiyDraw()
  {
    if ((this.mWakeAndUnlocking) && (this.mDrawnCallback != null))
    {
      this.mStatusBarKeyguardViewManager.getViewRootImpl().setReportNextDraw();
      notifyDrawn(this.mDrawnCallback);
    }
  }
  
  private void handleEarlyUnlockFail()
  {
    Log.d("KeyguardViewMediator", "handleEarlyUnlockFail, " + this.mTempUnlock);
    if (this.mTempUnlock)
    {
      this.mWakeAndUnlocking = false;
      this.mTempUnlock = false;
      setShowingLocked(true);
    }
    updateActivityLockScreenState();
  }
  
  private void handleEarlyUnlockStart()
  {
    Log.d("KeyguardViewMediator", "handleEarlyUnlockStart, " + this.mTempUnlock);
    this.mWakeAndUnlocking = true;
    this.mTempUnlock = true;
    setShowingLocked(false);
    this.mKeyguardGoingAwayRunnable.run();
  }
  
  private void handleHide()
  {
    Trace.beginSection("KeyguardViewMediator#handleHide");
    for (;;)
    {
      try
      {
        if (mustNotUnlockCurrentUser())
        {
          Log.d("KeyguardViewMediator", "Split system user, quit unlocking.");
          return;
        }
        this.mHiding = true;
        Log.d("KeyguardViewMediator", "handleHide, mShowing=" + this.mShowing + ", mOccluded=" + this.mOccluded + ", mHideAnimationRun=" + this.mHideAnimationRun + ", mWakeAndUnlocking=" + this.mWakeAndUnlocking + " , mTempUnlock=" + this.mTempUnlock + ", isFacelockUnlocking=" + this.mUpdateMonitor.isFacelockUnlocking());
        if (((!this.mShowing) || (this.mOccluded)) && (!this.mTempUnlock)) {
          break label211;
        }
        if (this.mWakeAndUnlocking)
        {
          handleStartKeyguardExitAnimation(SystemClock.uptimeMillis(), 0L);
          Trace.endSection();
          return;
        }
        if (!this.mHideAnimationRun)
        {
          this.mStatusBarKeyguardViewManager.startPreHideAnimation(this.mKeyguardGoingAwayRunnable);
          continue;
        }
        this.mKeyguardGoingAwayRunnable.run();
      }
      finally {}
      continue;
      label211:
      handleStartKeyguardExitAnimation(SystemClock.uptimeMillis() + this.mHideAnimation.getStartOffset(), this.mHideAnimation.getDuration());
    }
  }
  
  private void handleKeyguardDone(boolean paramBoolean)
  {
    Trace.beginSection("KeyguardViewMediator#handleKeyguardDone");
    int i = KeyguardUpdateMonitor.getCurrentUser();
    if (this.mLockPatternUtils.isSecure(i)) {
      this.mLockPatternUtils.getDevicePolicyManager().reportKeyguardDismissed(i);
    }
    Log.d("KeyguardViewMediator", "handleKeyguardDone(" + paramBoolean + ")");
    try
    {
      resetKeyguardDonePendingLocked();
      if (paramBoolean) {
        this.mUpdateMonitor.clearFailedUnlockAttempts();
      }
      this.mUpdateMonitor.clearFingerprintRecognized();
      if (this.mGoingToSleep)
      {
        Log.i("KeyguardViewMediator", "Device is going to sleep, aborting keyguardDone");
        FingerprintUnlockController localFingerprintUnlockController = LSState.getInstance().getFingerprintUnlockControl();
        if ((localFingerprintUnlockController != null) && (localFingerprintUnlockController.getMode() != 1) && (localFingerprintUnlockController.getMode() != 6) && (localFingerprintUnlockController.getMode() != 5)) {
          localFingerprintUnlockController.resetMode();
        }
        return;
      }
    }
    finally {}
    if (this.mExitSecureCallback != null) {}
    try
    {
      this.mExitSecureCallback.onKeyguardExitResult(paramBoolean);
      this.mExitSecureCallback = null;
      if (paramBoolean)
      {
        this.mExternallyEnabled = true;
        this.mNeedToReshowWhenReenabled = false;
        updateInputRestricted();
      }
    }
    catch (RemoteException localRemoteException)
    {
      try
      {
        ActivityManagerNative.getDefault().setKeyguardDone(true);
        this.mUpdateMonitor.onKeyguardDone(true);
        this.mUpdateMonitor.setUserUnlocked(true);
        LSState.getInstance().onKeyguardDone(paramBoolean);
        handleHide();
        Trace.endSection();
        return;
        localRemoteException = localRemoteException;
        Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(" + paramBoolean + ")", localRemoteException);
      }
      catch (Exception localException)
      {
        for (;;)
        {
          Log.w("KeyguardViewMediator", "Exception e = " + localException.toString());
        }
      }
    }
  }
  
  private void handleKeyguardDoneDrawing()
  {
    Trace.beginSection("KeyguardViewMediator#handleKeyguardDoneDrawing");
    try
    {
      if (this.mWaitingUntilKeyguardVisible)
      {
        this.mWaitingUntilKeyguardVisible = false;
        notifyAll();
        this.mHandler.removeMessages(10);
      }
      Trace.endSection();
      return;
    }
    finally {}
  }
  
  private void handleNotifyFinishedGoingToSleep()
  {
    try
    {
      this.mStatusBarKeyguardViewManager.onFinishedGoingToSleep();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  private void handleNotifyScreenTurnedOff()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: getstatic 812	android/os/Build:DEBUG_ONEPLUS	Z
    //   5: ifeq +18 -> 23
    //   8: iconst_1
    //   9: putstatic 332	com/android/systemui/keyguard/KeyguardViewMediator:DEBUG_SCREEN_ON	Z
    //   12: aload_0
    //   13: getfield 174	com/android/systemui/keyguard/KeyguardViewMediator:mHandler	Landroid/os/Handler;
    //   16: invokevirtual 815	android/os/Handler:getLooper	()Landroid/os/Looper;
    //   19: aconst_null
    //   20: invokevirtual 819	android/os/Looper:setMessageLogging	(Landroid/util/Printer;)V
    //   23: aload_0
    //   24: getfield 146	com/android/systemui/keyguard/KeyguardViewMediator:mStatusBarKeyguardViewManager	Lcom/android/systemui/statusbar/phone/StatusBarKeyguardViewManager;
    //   27: invokevirtual 822	com/android/systemui/statusbar/phone/StatusBarKeyguardViewManager:onScreenTurnedOff	()V
    //   30: aload_0
    //   31: getfield 157	com/android/systemui/keyguard/KeyguardViewMediator:mWakeAndUnlocking	Z
    //   34: ifeq +25 -> 59
    //   37: aload_0
    //   38: getfield 154	com/android/systemui/keyguard/KeyguardViewMediator:mUpdateMonitor	Lcom/android/keyguard/KeyguardUpdateMonitor;
    //   41: invokevirtual 825	com/android/keyguard/KeyguardUpdateMonitor:isFingerprintAlreadyAuthenticated	()Z
    //   44: ifeq +15 -> 59
    //   47: aload_0
    //   48: getfield 163	com/android/systemui/keyguard/KeyguardViewMediator:mDeviceInteractive	Z
    //   51: istore_1
    //   52: iload_1
    //   53: ifeq +6 -> 59
    //   56: aload_0
    //   57: monitorexit
    //   58: return
    //   59: aload_0
    //   60: iconst_0
    //   61: putfield 157	com/android/systemui/keyguard/KeyguardViewMediator:mWakeAndUnlocking	Z
    //   64: aload_0
    //   65: getfield 154	com/android/systemui/keyguard/KeyguardViewMediator:mUpdateMonitor	Lcom/android/keyguard/KeyguardUpdateMonitor;
    //   68: iconst_0
    //   69: invokevirtual 828	com/android/keyguard/KeyguardUpdateMonitor:onFacelockUnlocking	(Z)V
    //   72: goto -16 -> 56
    //   75: astore_2
    //   76: aload_0
    //   77: monitorexit
    //   78: aload_2
    //   79: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	80	0	this	KeyguardViewMediator
    //   51	2	1	bool	boolean
    //   75	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	23	75	finally
    //   23	52	75	finally
    //   59	72	75	finally
  }
  
  private void handleNotifyScreenTurnedOn()
  {
    if ((!DEBUG_MESSAGE) && (DEBUG_SCREEN_ON))
    {
      this.mHandler.getLooper().setMessageLogging(null);
      DEBUG_SCREEN_ON = false;
    }
    try
    {
      Log.d("KeyguardViewMediator", "handleNotifyScreenTurnedOn");
      LSState.getInstance().onScreenTurnedOn();
      this.mStatusBarKeyguardViewManager.onScreenTurnedOn();
      Trace.endSection();
      return;
    }
    finally {}
  }
  
  /* Error */
  private void handleNotifyScreenTurningOn(IKeyguardDrawnCallback paramIKeyguardDrawnCallback)
  {
    // Byte code:
    //   0: ldc_w 835
    //   3: invokestatic 691	android/os/Trace:beginSection	(Ljava/lang/String;)V
    //   6: aload_0
    //   7: monitorenter
    //   8: ldc_w 414
    //   11: ldc_w 836
    //   14: invokestatic 526	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   17: pop
    //   18: aload_0
    //   19: getfield 146	com/android/systemui/keyguard/KeyguardViewMediator:mStatusBarKeyguardViewManager	Lcom/android/systemui/statusbar/phone/StatusBarKeyguardViewManager;
    //   22: invokevirtual 839	com/android/systemui/statusbar/phone/StatusBarKeyguardViewManager:onScreenTurningOn	()V
    //   25: aload_1
    //   26: ifnull +29 -> 55
    //   29: aload_0
    //   30: getfield 157	com/android/systemui/keyguard/KeyguardViewMediator:mWakeAndUnlocking	Z
    //   33: ifeq +17 -> 50
    //   36: aload_0
    //   37: getfield 142	com/android/systemui/keyguard/KeyguardViewMediator:mShowing	Z
    //   40: ifeq +10 -> 50
    //   43: aload_0
    //   44: getfield 695	com/android/systemui/keyguard/KeyguardViewMediator:mHiding	Z
    //   47: ifeq +14 -> 61
    //   50: aload_0
    //   51: aload_1
    //   52: invokespecial 669	com/android/systemui/keyguard/KeyguardViewMediator:notifyDrawn	(Lcom/android/internal/policy/IKeyguardDrawnCallback;)V
    //   55: aload_0
    //   56: monitorexit
    //   57: invokestatic 716	android/os/Trace:endSection	()V
    //   60: return
    //   61: aload_0
    //   62: aload_1
    //   63: putfield 657	com/android/systemui/keyguard/KeyguardViewMediator:mDrawnCallback	Lcom/android/internal/policy/IKeyguardDrawnCallback;
    //   66: goto -11 -> 55
    //   69: astore_1
    //   70: aload_0
    //   71: monitorexit
    //   72: aload_1
    //   73: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	74	0	this	KeyguardViewMediator
    //   0	74	1	paramIKeyguardDrawnCallback	IKeyguardDrawnCallback
    // Exception table:
    //   from	to	target	type
    //   8	25	69	finally
    //   29	50	69	finally
    //   50	55	69	finally
    //   61	66	69	finally
  }
  
  private void handleNotifyStartedGoingToSleep()
  {
    try
    {
      this.mStatusBarKeyguardViewManager.onStartedGoingToSleep();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private void handleNotifyStartedWakingUp()
  {
    Trace.beginSection("KeyguardViewMediator#handleMotifyStartedWakingUp");
    try
    {
      this.mStatusBarKeyguardViewManager.onStartedWakingUp();
      Trace.endSection();
      return;
    }
    finally {}
  }
  
  private void handleOnActivityDrawn()
  {
    if (this.mKeyguardDonePending) {
      this.mStatusBarKeyguardViewManager.onActivityDrawn();
    }
  }
  
  private void handleReset()
  {
    try
    {
      this.mStatusBarKeyguardViewManager.reset();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private void handleSetOccluded(boolean paramBoolean1, boolean paramBoolean2)
  {
    Trace.beginSection("KeyguardViewMediator#handleSetOccluded");
    try
    {
      if ((this.mHiding) && (paramBoolean1)) {
        startKeyguardExitAnimation(0L, 0L);
      }
      if (this.mOccluded != paramBoolean1)
      {
        Log.d("KeyguardViewMediator", "handleSetOccluded " + paramBoolean1);
        this.mOccluded = paramBoolean1;
        this.mStatusBarKeyguardViewManager.setOccluded(paramBoolean1, paramBoolean2);
        updateActivityLockScreenState();
        adjustStatusBarLocked();
      }
      Trace.endSection();
      return;
    }
    finally {}
  }
  
  private void handleShow(Bundle paramBundle)
  {
    Trace.beginSection("KeyguardViewMediator#handleShow");
    int i = KeyguardUpdateMonitor.getCurrentUser();
    if (this.mLockPatternUtils.isSecure(i)) {
      this.mLockPatternUtils.getDevicePolicyManager().reportKeyguardSecured(i);
    }
    try
    {
      if ((!this.mSystemReady) || (this.mIgnoreHandleShow))
      {
        this.mIgnoreHandleShow = false;
        Log.d("KeyguardViewMediator", "ignoring handleShow because system is not ready.");
        this.mUpdateMonitor.onKeyguardDone(true);
        try
        {
          ActivityManagerNative.getDefault().setKeyguardDone(true);
          return;
        }
        catch (Exception paramBundle)
        {
          for (;;)
          {
            Log.w("KeyguardViewMediator", "Exception e = " + paramBundle.toString());
          }
        }
      }
      Log.d("KeyguardViewMediator", "handleShow");
    }
    finally {}
    this.mIgnoreHandleShow = false;
    setShowingLocked(true);
    this.mStatusBarKeyguardViewManager.show(paramBundle);
    Log.d("KeyguardViewMediator", "show keyguard");
    this.mHiding = false;
    this.mWakeAndUnlocking = false;
    this.mUpdateMonitor.onFacelockUnlocking(false);
    resetKeyguardDonePendingLocked();
    this.mHideAnimationRun = false;
    updateActivityLockScreenState();
    adjustStatusBarLocked();
    userActivity();
    this.mHandleKeyguardMessageWakeLock.acquire();
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        if (KeyguardViewMediator.-get5(KeyguardViewMediator.this).isHeld()) {
          KeyguardViewMediator.-get5(KeyguardViewMediator.this).release();
        }
      }
    });
    this.mShowKeyguardWakeLock.release();
    this.mKeyguardDisplayManager.show();
    this.mUnlockSoundPlayed = false;
    Log.d("KeyguardViewMediator", "finish handleshow");
    Trace.endSection();
  }
  
  /* Error */
  private void handleStartKeyguardExitAnimation(long paramLong1, long paramLong2)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 154	com/android/systemui/keyguard/KeyguardViewMediator:mUpdateMonitor	Lcom/android/keyguard/KeyguardUpdateMonitor;
    //   4: invokevirtual 710	com/android/keyguard/KeyguardUpdateMonitor:isFacelockUnlocking	()Z
    //   7: ifeq +7 -> 14
    //   10: ldc2_w 903
    //   13: lstore_3
    //   14: ldc_w 906
    //   17: invokestatic 691	android/os/Trace:beginSection	(Ljava/lang/String;)V
    //   20: ldc_w 414
    //   23: new 509	java/lang/StringBuilder
    //   26: dup
    //   27: invokespecial 510	java/lang/StringBuilder:<init>	()V
    //   30: ldc_w 908
    //   33: invokevirtual 516	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   36: lload_1
    //   37: invokevirtual 911	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   40: ldc_w 913
    //   43: invokevirtual 516	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   46: lload_3
    //   47: invokevirtual 911	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   50: ldc_w 915
    //   53: invokevirtual 516	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   56: aload_0
    //   57: getfield 695	com/android/systemui/keyguard/KeyguardViewMediator:mHiding	Z
    //   60: invokevirtual 674	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   63: invokevirtual 523	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   66: invokestatic 526	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   69: pop
    //   70: aload_0
    //   71: monitorenter
    //   72: aload_0
    //   73: getfield 695	com/android/systemui/keyguard/KeyguardViewMediator:mHiding	Z
    //   76: istore 5
    //   78: iload 5
    //   80: ifne +6 -> 86
    //   83: aload_0
    //   84: monitorexit
    //   85: return
    //   86: aload_0
    //   87: iconst_0
    //   88: putfield 695	com/android/systemui/keyguard/KeyguardViewMediator:mHiding	Z
    //   91: aload_0
    //   92: iconst_0
    //   93: putfield 366	com/android/systemui/keyguard/KeyguardViewMediator:mTempUnlock	Z
    //   96: aload_0
    //   97: getfield 157	com/android/systemui/keyguard/KeyguardViewMediator:mWakeAndUnlocking	Z
    //   100: ifeq +33 -> 133
    //   103: aload_0
    //   104: getfield 657	com/android/systemui/keyguard/KeyguardViewMediator:mDrawnCallback	Lcom/android/internal/policy/IKeyguardDrawnCallback;
    //   107: ifnull +26 -> 133
    //   110: aload_0
    //   111: getfield 146	com/android/systemui/keyguard/KeyguardViewMediator:mStatusBarKeyguardViewManager	Lcom/android/systemui/statusbar/phone/StatusBarKeyguardViewManager;
    //   114: invokevirtual 661	com/android/systemui/statusbar/phone/StatusBarKeyguardViewManager:getViewRootImpl	()Landroid/view/ViewRootImpl;
    //   117: invokevirtual 666	android/view/ViewRootImpl:setReportNextDraw	()V
    //   120: aload_0
    //   121: aload_0
    //   122: getfield 657	com/android/systemui/keyguard/KeyguardViewMediator:mDrawnCallback	Lcom/android/internal/policy/IKeyguardDrawnCallback;
    //   125: invokespecial 669	com/android/systemui/keyguard/KeyguardViewMediator:notifyDrawn	(Lcom/android/internal/policy/IKeyguardDrawnCallback;)V
    //   128: aload_0
    //   129: aconst_null
    //   130: putfield 657	com/android/systemui/keyguard/KeyguardViewMediator:mDrawnCallback	Lcom/android/internal/policy/IKeyguardDrawnCallback;
    //   133: aload_0
    //   134: iconst_0
    //   135: invokespecial 610	com/android/systemui/keyguard/KeyguardViewMediator:setShowingLocked	(Z)V
    //   138: aload_0
    //   139: getfield 194	com/android/systemui/keyguard/KeyguardViewMediator:mPhoneState	I
    //   142: ifne +10 -> 152
    //   145: aload_0
    //   146: getfield 370	com/android/systemui/keyguard/KeyguardViewMediator:mUnlockSoundPlayed	Z
    //   149: ifeq +98 -> 247
    //   152: aload_0
    //   153: iconst_0
    //   154: putfield 157	com/android/systemui/keyguard/KeyguardViewMediator:mWakeAndUnlocking	Z
    //   157: aload_0
    //   158: getfield 146	com/android/systemui/keyguard/KeyguardViewMediator:mStatusBarKeyguardViewManager	Lcom/android/systemui/statusbar/phone/StatusBarKeyguardViewManager;
    //   161: lload_1
    //   162: lload_3
    //   163: invokevirtual 918	com/android/systemui/statusbar/phone/StatusBarKeyguardViewManager:hide	(JJ)V
    //   166: aload_0
    //   167: getfield 154	com/android/systemui/keyguard/KeyguardViewMediator:mUpdateMonitor	Lcom/android/keyguard/KeyguardUpdateMonitor;
    //   170: iconst_0
    //   171: invokevirtual 828	com/android/keyguard/KeyguardUpdateMonitor:onFacelockUnlocking	(Z)V
    //   174: aload_0
    //   175: invokespecial 291	com/android/systemui/keyguard/KeyguardViewMediator:resetKeyguardDonePendingLocked	()V
    //   178: aload_0
    //   179: iconst_0
    //   180: putfield 189	com/android/systemui/keyguard/KeyguardViewMediator:mHideAnimationRun	Z
    //   183: aload_0
    //   184: invokespecial 677	com/android/systemui/keyguard/KeyguardViewMediator:updateActivityLockScreenState	()V
    //   187: aload_0
    //   188: invokespecial 211	com/android/systemui/keyguard/KeyguardViewMediator:adjustStatusBarLocked	()V
    //   191: aload_0
    //   192: invokespecial 299	com/android/systemui/keyguard/KeyguardViewMediator:sendUserPresentBroadcast	()V
    //   195: aload_0
    //   196: monitorexit
    //   197: aload_0
    //   198: getfield 402	com/android/systemui/keyguard/KeyguardViewMediator:mContext	Landroid/content/Context;
    //   201: invokestatic 921	com/android/keyguard/KeyguardUpdateMonitor:getInstance	(Landroid/content/Context;)Lcom/android/keyguard/KeyguardUpdateMonitor;
    //   204: invokevirtual 599	com/android/keyguard/KeyguardUpdateMonitor:isSimPinSecure	()Z
    //   207: ifeq +18 -> 225
    //   210: ldc_w 414
    //   213: ldc_w 923
    //   216: invokestatic 526	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   219: pop
    //   220: aload_0
    //   221: aconst_null
    //   222: invokespecial 263	com/android/systemui/keyguard/KeyguardViewMediator:doKeyguardLocked	(Landroid/os/Bundle;)V
    //   225: ldc_w 414
    //   228: ldc_w 925
    //   231: invokestatic 526	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   234: pop
    //   235: aload_0
    //   236: iconst_1
    //   237: getstatic 349	com/android/systemui/keyguard/KeyguardViewMediator:AUTHENTICATE_IGNORE	I
    //   240: invokevirtual 929	com/android/systemui/keyguard/KeyguardViewMediator:changePanelAlpha	(II)V
    //   243: invokestatic 716	android/os/Trace:endSection	()V
    //   246: return
    //   247: aload_0
    //   248: iconst_1
    //   249: putfield 370	com/android/systemui/keyguard/KeyguardViewMediator:mUnlockSoundPlayed	Z
    //   252: aload_0
    //   253: iconst_0
    //   254: invokespecial 932	com/android/systemui/keyguard/KeyguardViewMediator:playSounds	(Z)V
    //   257: goto -105 -> 152
    //   260: astore 6
    //   262: aload_0
    //   263: monitorexit
    //   264: aload 6
    //   266: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	267	0	this	KeyguardViewMediator
    //   0	267	1	paramLong1	long
    //   0	267	3	paramLong2	long
    //   76	3	5	bool	boolean
    //   260	5	6	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   72	78	260	finally
    //   86	133	260	finally
    //   133	152	260	finally
    //   152	195	260	finally
    //   247	257	260	finally
  }
  
  private void handleVerifyUnlock()
  {
    Trace.beginSection("KeyguardViewMediator#handleVerifyUnlock");
    try
    {
      setShowingLocked(true);
      this.mStatusBarKeyguardViewManager.verifyUnlock();
      updateActivityLockScreenState();
      Trace.endSection();
      return;
    }
    finally {}
  }
  
  private void hideLocked()
  {
    Trace.beginSection("KeyguardViewMediator#hideLocked");
    Message localMessage = this.mHandler.obtainMessage(3);
    this.mHandler.sendMessage(localMessage);
    Trace.endSection();
  }
  
  private void lockProfile(int paramInt)
  {
    this.mTrustManager.setDeviceLockedForUser(paramInt, true);
  }
  
  private void maybeSendUserPresentBroadcast()
  {
    if ((this.mSystemReady) && (this.mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser()))) {
      sendUserPresentBroadcast();
    }
    while ((!this.mSystemReady) || (!shouldWaitForProvisioning())) {
      return;
    }
    getLockPatternUtils().userPresent(KeyguardUpdateMonitor.getCurrentUser());
  }
  
  private void notifyDrawn(IKeyguardDrawnCallback paramIKeyguardDrawnCallback)
  {
    Trace.beginSection("KeyguardViewMediator#notifyDrawn");
    try
    {
      Log.d("KeyguardViewMediator", "notifyDrawn");
      paramIKeyguardDrawnCallback.onDrawn();
      Trace.endSection();
      return;
    }
    catch (RemoteException paramIKeyguardDrawnCallback)
    {
      for (;;)
      {
        Slog.w("KeyguardViewMediator", "Exception calling onDrawn():", paramIKeyguardDrawnCallback);
      }
    }
  }
  
  private void notifyFinishedGoingToSleep()
  {
    this.mHandler.sendEmptyMessage(6);
  }
  
  private void notifyHasLockscreenWallpaperChanged(boolean paramBoolean)
  {
    int i = this.mKeyguardStateCallbacks.size() - 1;
    for (;;)
    {
      if (i >= 0) {
        try
        {
          ((IKeyguardStateCallback)this.mKeyguardStateCallbacks.get(i)).onHasLockscreenWallpaperChanged(paramBoolean);
          i -= 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.w("KeyguardViewMediator", "Failed to call onHasLockscreenWallpaperChanged", localRemoteException);
            if ((localRemoteException instanceof DeadObjectException)) {
              this.mKeyguardStateCallbacks.remove(i);
            }
          }
        }
      }
    }
  }
  
  private void notifyScreenOn(IKeyguardDrawnCallback paramIKeyguardDrawnCallback)
  {
    paramIKeyguardDrawnCallback = this.mHandler.obtainMessage(7, paramIKeyguardDrawnCallback);
    this.mHandler.sendMessage(paramIKeyguardDrawnCallback);
  }
  
  private void notifyScreenTurnedOff()
  {
    Message localMessage = this.mHandler.obtainMessage(23);
    this.mHandler.sendMessage(localMessage);
  }
  
  private void notifyScreenTurnedOn()
  {
    Message localMessage = this.mHandler.obtainMessage(22);
    this.mHandler.sendMessage(localMessage);
  }
  
  private void notifyStartedGoingToSleep()
  {
    this.mHandler.sendEmptyMessage(24);
  }
  
  private void notifyStartedWakingUp()
  {
    Log.d("KeyguardViewMediator", "notifyStartedWakingUp");
    this.mHandler.sendEmptyMessage(21);
  }
  
  private void notifyTrustedChangedLocked(boolean paramBoolean)
  {
    int i = this.mKeyguardStateCallbacks.size() - 1;
    for (;;)
    {
      if (i >= 0) {
        try
        {
          ((IKeyguardStateCallback)this.mKeyguardStateCallbacks.get(i)).onTrustedChanged(paramBoolean);
          i -= 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.w("KeyguardViewMediator", "Failed to call notifyTrustedChangedLocked", localRemoteException);
            if ((localRemoteException instanceof DeadObjectException)) {
              this.mKeyguardStateCallbacks.remove(i);
            }
          }
        }
      }
    }
  }
  
  private void playSound(int paramInt)
  {
    if (paramInt == 0) {
      return;
    }
    if (Settings.System.getInt(this.mContext.getContentResolver(), "lockscreen_sounds_enabled", 1) == 1)
    {
      this.mLockSounds.stop(this.mLockSoundStreamId);
      if (this.mAudioManager == null)
      {
        this.mAudioManager = ((AudioManager)this.mContext.getSystemService("audio"));
        if (this.mAudioManager == null) {
          return;
        }
        this.mUiSoundsStreamType = this.mAudioManager.getUiSoundsStreamType();
      }
      if (this.mAudioManager.isStreamMute(this.mUiSoundsStreamType)) {
        return;
      }
      this.mLockSoundStreamId = this.mLockSounds.play(paramInt, this.mLockSoundVolume, this.mLockSoundVolume, 1, 0, 1.0F);
    }
  }
  
  private void playSounds(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = this.mLockSoundId;; i = this.mUnlockSoundId)
    {
      playSound(i);
      return;
    }
  }
  
  private void playTrustedSound()
  {
    playSound(this.mTrustedSoundId);
  }
  
  private void resetKeyguardDonePendingLocked()
  {
    this.mKeyguardDonePending = false;
    this.mHandler.removeMessages(20);
  }
  
  private void resetStateLocked()
  {
    if (!KeyguardUpdateMonitor.getInstance(this.mContext).isFingerprintAlreadyAuthenticated())
    {
      Message localMessage = this.mHandler.obtainMessage(4);
      this.mHandler.sendMessage(localMessage);
      return;
    }
    Log.d("KeyguardViewMediator", "skip reset when auth");
  }
  
  /* Error */
  private void sendUserPresentBroadcast()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 1051	com/android/systemui/keyguard/KeyguardViewMediator:mBootCompleted	Z
    //   6: ifeq +90 -> 96
    //   9: invokestatic 502	com/android/keyguard/KeyguardUpdateMonitor:getCurrentUser	()I
    //   12: istore_2
    //   13: new 444	android/os/UserHandle
    //   16: dup
    //   17: iload_2
    //   18: invokespecial 1053	android/os/UserHandle:<init>	(I)V
    //   21: astore 5
    //   23: aload_0
    //   24: getfield 402	com/android/systemui/keyguard/KeyguardViewMediator:mContext	Landroid/content/Context;
    //   27: ldc_w 1055
    //   30: invokevirtual 410	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   33: checkcast 438	android/os/UserManager
    //   36: aload 5
    //   38: invokevirtual 1058	android/os/UserHandle:getIdentifier	()I
    //   41: invokevirtual 1061	android/os/UserManager:getProfileIdsWithDisabled	(I)[I
    //   44: astore 5
    //   46: iconst_0
    //   47: istore_1
    //   48: aload 5
    //   50: arraylength
    //   51: istore_3
    //   52: iload_1
    //   53: iload_3
    //   54: if_icmpge +31 -> 85
    //   57: aload 5
    //   59: iload_1
    //   60: iaload
    //   61: istore 4
    //   63: aload_0
    //   64: getfield 402	com/android/systemui/keyguard/KeyguardViewMediator:mContext	Landroid/content/Context;
    //   67: getstatic 347	com/android/systemui/keyguard/KeyguardViewMediator:USER_PRESENT_INTENT	Landroid/content/Intent;
    //   70: iload 4
    //   72: invokestatic 1065	android/os/UserHandle:of	(I)Landroid/os/UserHandle;
    //   75: invokevirtual 1069	android/content/Context:sendBroadcastAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;)V
    //   78: iload_1
    //   79: iconst_1
    //   80: iadd
    //   81: istore_1
    //   82: goto -30 -> 52
    //   85: aload_0
    //   86: invokevirtual 958	com/android/systemui/keyguard/KeyguardViewMediator:getLockPatternUtils	()Lcom/android/internal/widget/LockPatternUtils;
    //   89: iload_2
    //   90: invokevirtual 961	com/android/internal/widget/LockPatternUtils:userPresent	(I)V
    //   93: aload_0
    //   94: monitorexit
    //   95: return
    //   96: aload_0
    //   97: iconst_1
    //   98: putfield 1071	com/android/systemui/keyguard/KeyguardViewMediator:mBootSendUserPresent	Z
    //   101: goto -8 -> 93
    //   104: astore 5
    //   106: aload_0
    //   107: monitorexit
    //   108: aload 5
    //   110: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	111	0	this	KeyguardViewMediator
    //   47	35	1	i	int
    //   12	78	2	j	int
    //   51	4	3	k	int
    //   61	10	4	m	int
    //   21	37	5	localObject1	Object
    //   104	5	5	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   2	46	104	finally
    //   48	52	104	finally
    //   63	78	104	finally
    //   85	93	104	finally
    //   96	101	104	finally
  }
  
  private void setShowingLocked(boolean paramBoolean)
  {
    if (paramBoolean != this.mShowing)
    {
      this.mShowing = paramBoolean;
      int i = this.mKeyguardStateCallbacks.size() - 1;
      for (;;)
      {
        if (i >= 0) {
          try
          {
            ((IKeyguardStateCallback)this.mKeyguardStateCallbacks.get(i)).onShowingStateChanged(paramBoolean);
            i -= 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;)
            {
              Slog.w("KeyguardViewMediator", "Failed to call onShowingStateChanged", localRemoteException);
              if ((localRemoteException instanceof DeadObjectException)) {
                this.mKeyguardStateCallbacks.remove(i);
              }
            }
          }
        }
      }
      updateInputRestrictedLocked();
      this.mTrustManager.reportKeyguardShowingChanged();
    }
  }
  
  private void setupLocked()
  {
    this.mPM = ((PowerManager)this.mContext.getSystemService("power"));
    this.mWM = WindowManagerGlobal.getWindowManagerService();
    this.mTrustManager = ((TrustManager)this.mContext.getSystemService("trust"));
    this.mShowKeyguardWakeLock = this.mPM.newWakeLock(1, "show keyguard");
    this.mShowKeyguardWakeLock.setReferenceCounted(false);
    this.mHandleKeyguardMessageWakeLock = this.mPM.newWakeLock(1, "keyguard handler");
    this.mHandleKeyguardMessageWakeLock.setReferenceCounted(false);
    this.mContext.registerReceiver(this.mBroadcastReceiver, new IntentFilter("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_KEYGUARD"));
    this.mContext.registerReceiver(this.mBroadcastReceiver, new IntentFilter("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_LOCK"));
    this.mKeyguardDisplayManager = new KeyguardDisplayManager(this.mContext);
    this.mAlarmManager = ((AlarmManager)this.mContext.getSystemService("alarm"));
    this.mUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
    this.mLockPatternUtils = new LockPatternUtils(this.mContext);
    KeyguardUpdateMonitor.setCurrentUser(ActivityManager.getCurrentUser());
    if ((shouldWaitForProvisioning()) || (this.mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser()))) {}
    for (boolean bool = false;; bool = true)
    {
      setShowingLocked(bool);
      updateInputRestrictedLocked();
      this.mTrustManager.reportKeyguardShowingChanged();
      this.mStatusBarKeyguardViewManager = SystemUIFactory.getInstance().createStatusBarKeyguardViewManager(this.mContext, this.mViewMediatorCallback, this.mLockPatternUtils);
      Object localObject = this.mContext.getContentResolver();
      this.mDeviceInteractive = this.mPM.isInteractive();
      this.mLockSounds = new SoundPool(1, 1, 0);
      String str = Settings.Global.getString((ContentResolver)localObject, "lock_sound");
      if (str != null) {
        this.mLockSoundId = this.mLockSounds.load(str, 1);
      }
      if ((str == null) || (this.mLockSoundId == 0)) {
        Log.w("KeyguardViewMediator", "failed to load lock sound from " + str);
      }
      str = Settings.Global.getString((ContentResolver)localObject, "unlock_sound");
      if (str != null) {
        this.mUnlockSoundId = this.mLockSounds.load(str, 1);
      }
      if ((str == null) || (this.mUnlockSoundId == 0)) {
        Log.w("KeyguardViewMediator", "failed to load unlock sound from " + str);
      }
      localObject = Settings.Global.getString((ContentResolver)localObject, "trusted_sound");
      if (localObject != null) {
        this.mTrustedSoundId = this.mLockSounds.load((String)localObject, 1);
      }
      if ((localObject == null) || (this.mTrustedSoundId == 0)) {
        Log.w("KeyguardViewMediator", "failed to load trusted sound from " + (String)localObject);
      }
      this.mLockSoundVolume = ((float)Math.pow(10.0D, this.mContext.getResources().getInteger(17694725) / 20.0F));
      this.mHideAnimation = AnimationUtils.loadAnimation(this.mContext, 17432660);
      return;
    }
  }
  
  private boolean shouldWaitForProvisioning()
  {
    return (!this.mUpdateMonitor.isDeviceProvisioned()) && (!isSecure());
  }
  
  private void showLocked(Bundle paramBundle)
  {
    Trace.beginSection("KeyguardViewMediator#showLocked aqcuiring mShowKeyguardWakeLock");
    this.mIgnoreHandleShow = false;
    this.mShowKeyguardWakeLock.acquire();
    paramBundle = this.mHandler.obtainMessage(2, paramBundle);
    this.mHandler.sendMessage(paramBundle);
    Trace.endSection();
  }
  
  private void updateActivityLockScreenState()
  {
    Trace.beginSection("KeyguardViewMediator#updateActivityLockScreenState");
    try
    {
      ActivityManagerNative.getDefault().setLockScreenShown(this.mShowing, this.mOccluded);
      Trace.endSection();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  private void updateInputRestricted()
  {
    try
    {
      updateInputRestrictedLocked();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private void updateInputRestrictedLocked()
  {
    boolean bool = isInputRestricted();
    if (this.mInputRestricted != bool)
    {
      this.mInputRestricted = bool;
      int i = this.mKeyguardStateCallbacks.size() - 1;
      for (;;)
      {
        if (i >= 0) {
          try
          {
            ((IKeyguardStateCallback)this.mKeyguardStateCallbacks.get(i)).onInputRestrictedStateChanged(bool);
            i -= 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;)
            {
              Slog.w("KeyguardViewMediator", "Failed to call onDeviceProvisioned", localRemoteException);
              if ((localRemoteException instanceof DeadObjectException)) {
                this.mKeyguardStateCallbacks.remove(i);
              }
            }
          }
        }
      }
    }
  }
  
  /* Error */
  public void addStateMonitorCallback(IKeyguardStateCallback paramIKeyguardStateCallback)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 130	com/android/systemui/keyguard/KeyguardViewMediator:mKeyguardStateCallbacks	Ljava/util/ArrayList;
    //   6: aload_1
    //   7: invokevirtual 1213	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   10: pop
    //   11: aload_1
    //   12: aload_0
    //   13: getfield 154	com/android/systemui/keyguard/KeyguardViewMediator:mUpdateMonitor	Lcom/android/keyguard/KeyguardUpdateMonitor;
    //   16: invokevirtual 599	com/android/keyguard/KeyguardUpdateMonitor:isSimPinSecure	()Z
    //   19: invokeinterface 1216 2 0
    //   24: aload_1
    //   25: aload_0
    //   26: getfield 142	com/android/systemui/keyguard/KeyguardViewMediator:mShowing	Z
    //   29: invokeinterface 1074 2 0
    //   34: aload_1
    //   35: aload_0
    //   36: getfield 1202	com/android/systemui/keyguard/KeyguardViewMediator:mInputRestricted	Z
    //   39: invokeinterface 1205 2 0
    //   44: aload_1
    //   45: aload_0
    //   46: getfield 154	com/android/systemui/keyguard/KeyguardViewMediator:mUpdateMonitor	Lcom/android/keyguard/KeyguardUpdateMonitor;
    //   49: invokestatic 502	com/android/keyguard/KeyguardUpdateMonitor:getCurrentUser	()I
    //   52: invokevirtual 1219	com/android/keyguard/KeyguardUpdateMonitor:getUserHasTrust	(I)Z
    //   55: invokeinterface 1005 2 0
    //   60: aload_1
    //   61: aload_0
    //   62: getfield 154	com/android/systemui/keyguard/KeyguardViewMediator:mUpdateMonitor	Lcom/android/keyguard/KeyguardUpdateMonitor;
    //   65: invokevirtual 1222	com/android/keyguard/KeyguardUpdateMonitor:hasLockscreenWallpaper	()Z
    //   68: invokeinterface 986 2 0
    //   73: aload_0
    //   74: monitorexit
    //   75: return
    //   76: astore_1
    //   77: ldc_w 414
    //   80: ldc_w 1224
    //   83: aload_1
    //   84: invokestatic 794	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   87: pop
    //   88: goto -15 -> 73
    //   91: astore_1
    //   92: aload_0
    //   93: monitorexit
    //   94: aload_1
    //   95: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	96	0	this	KeyguardViewMediator
    //   0	96	1	paramIKeyguardStateCallback	IKeyguardStateCallback
    // Exception table:
    //   from	to	target	type
    //   11	73	76	android/os/RemoteException
    //   2	11	91	finally
    //   11	73	91	finally
    //   77	88	91	finally
  }
  
  public void changePanelAlpha(int paramInt1, int paramInt2)
  {
    if ((paramInt1 <= 0) || (!this.mStatusBarWindowManager.isShowingWallpaper()) || (isScreenOffAuthenticating())) {}
    while ((paramInt2 != AUTHENTICATE_IGNORE) && (isScreenOffAuthenticating()) && (paramInt2 != this.mAuthenticatingType))
    {
      Log.d("KeyguardViewMediator", "return set alpha");
      return;
      if (this.mUpdateMonitor.needsSlowUnlockTransition())
      {
        Log.d("KeyguardViewMediator", "not set backdrop alpha");
        return;
      }
    }
    Log.d("KeyguardViewMediator", "set panel alpha to " + paramInt1 + ", type:" + paramInt2 + ", current:" + this.mAuthenticatingType);
    if (paramInt1 > 0)
    {
      this.mBar.setWallpaperAlpha(paramInt1);
      this.mBar.setPanelViewAlpha(paramInt1, false);
      this.mScrimController.forceHideScrims(false);
      return;
    }
    this.mBar.setWallpaperAlpha(paramInt1);
    this.mBar.setPanelViewAlpha(paramInt1, false);
    this.mScrimController.forceHideScrims(true);
  }
  
  public void dismiss(boolean paramBoolean)
  {
    Handler localHandler = this.mHandler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localHandler.obtainMessage(17, i, 0).sendToTarget();
      return;
    }
  }
  
  public void doKeyguardTimeout(Bundle paramBundle)
  {
    this.mHandler.removeMessages(13);
    paramBundle = this.mHandler.obtainMessage(13, paramBundle);
    this.mHandler.sendMessage(paramBundle);
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.print("  mSystemReady: ");
    paramPrintWriter.println(this.mSystemReady);
    paramPrintWriter.print("  mBootCompleted: ");
    paramPrintWriter.println(this.mBootCompleted);
    paramPrintWriter.print("  mBootSendUserPresent: ");
    paramPrintWriter.println(this.mBootSendUserPresent);
    paramPrintWriter.print("  mExternallyEnabled: ");
    paramPrintWriter.println(this.mExternallyEnabled);
    paramPrintWriter.print("  mNeedToReshowWhenReenabled: ");
    paramPrintWriter.println(this.mNeedToReshowWhenReenabled);
    paramPrintWriter.print("  mShowing: ");
    paramPrintWriter.println(this.mShowing);
    paramPrintWriter.print("  mInputRestricted: ");
    paramPrintWriter.println(this.mInputRestricted);
    paramPrintWriter.print("  mOccluded: ");
    paramPrintWriter.println(this.mOccluded);
    paramPrintWriter.print("  mDelayedShowingSequence: ");
    paramPrintWriter.println(this.mDelayedShowingSequence);
    paramPrintWriter.print("  mExitSecureCallback: ");
    paramPrintWriter.println(this.mExitSecureCallback);
    paramPrintWriter.print("  mDeviceInteractive: ");
    paramPrintWriter.println(this.mDeviceInteractive);
    paramPrintWriter.print("  mGoingToSleep: ");
    paramPrintWriter.println(this.mGoingToSleep);
    paramPrintWriter.print("  mHiding: ");
    paramPrintWriter.println(this.mHiding);
    paramPrintWriter.print("  mWaitingUntilKeyguardVisible: ");
    paramPrintWriter.println(this.mWaitingUntilKeyguardVisible);
    paramPrintWriter.print("  mKeyguardDonePending: ");
    paramPrintWriter.println(this.mKeyguardDonePending);
    paramPrintWriter.print("  mHideAnimationRun: ");
    paramPrintWriter.println(this.mHideAnimationRun);
    paramPrintWriter.print("  mPendingReset: ");
    paramPrintWriter.println(this.mPendingReset);
    paramPrintWriter.print("  mPendingLock: ");
    paramPrintWriter.println(this.mPendingLock);
    paramPrintWriter.print("  mWakeAndUnlocking: ");
    paramPrintWriter.println(this.mWakeAndUnlocking);
    paramPrintWriter.print("  mDrawnCallback: ");
    paramPrintWriter.println(this.mDrawnCallback);
    paramPrintWriter.print("  mPowerKeyCameraLaunching: ");
    paramPrintWriter.println(this.mPowerKeyCameraLaunching);
    paramPrintWriter.print("  mUnlockSoundPlayed: ");
    paramPrintWriter.println(this.mUnlockSoundPlayed);
    paramPrintWriter.print("  mPhoneState: ");
    paramPrintWriter.println(this.mPhoneState);
    paramPrintWriter.print("  DEBUG_MESSAGE: ");
    paramPrintWriter.println(DEBUG_MESSAGE);
    paramPrintWriter.print("  mTempUnlock: ");
    paramPrintWriter.println(this.mTempUnlock);
    paramPrintWriter.print("  mAuthenticatingType: ");
    paramPrintWriter.println(this.mAuthenticatingType);
  }
  
  public void forceDismiss(boolean paramBoolean)
  {
    Handler localHandler = this.mHandler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localHandler.obtainMessage(104, i, 0).sendToTarget();
      return;
    }
  }
  
  public LockPatternUtils getLockPatternUtils()
  {
    return this.mLockPatternUtils;
  }
  
  public ViewMediatorCallback getViewMediatorCallback()
  {
    return this.mViewMediatorCallback;
  }
  
  public void handleDismiss(boolean paramBoolean)
  {
    if ((!this.mShowing) || ((!paramBoolean) && (this.mOccluded))) {
      return;
    }
    this.mStatusBarKeyguardViewManager.dismiss();
  }
  
  public void handleForceDismiss(boolean paramBoolean)
  {
    if ((!this.mShowing) || ((!paramBoolean) && (this.mOccluded))) {
      return;
    }
    this.mStatusBarKeyguardViewManager.forceDismiss();
  }
  
  public void ignorePendingHandleShow()
  {
    try
    {
      this.mIgnoreHandleShow = true;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean isInputRestricted()
  {
    if (!this.mShowing) {
      return this.mNeedToReshowWhenReenabled;
    }
    return true;
  }
  
  public boolean isScreenOffAuthenticating()
  {
    boolean bool = false;
    if (this.mAuthenticatingType != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isSecure()
  {
    if (!this.mLockPatternUtils.isSecure(KeyguardUpdateMonitor.getCurrentUser())) {
      return KeyguardUpdateMonitor.getInstance(this.mContext).isSimPinSecure();
    }
    return true;
  }
  
  public boolean isShowingAndNotOccluded()
  {
    return (this.mShowing) && (!this.mOccluded);
  }
  
  public void keyguardDone(boolean paramBoolean)
  {
    Trace.beginSection("KeyguardViewMediator#keyguardDone");
    Log.d("KeyguardViewMediator", "keyguardDone(" + paramBoolean + ")");
    userActivity();
    EventLog.writeEvent(70000, 2);
    Object localObject = this.mHandler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localObject = ((Handler)localObject).obtainMessage(9, i, 0);
      this.mHandler.sendMessage((Message)localObject);
      if ((this.mBar != null) && (this.mBar.getFacelockController() != null)) {
        this.mBar.getFacelockController().resetFacelockPending();
      }
      Trace.endSection();
      return;
    }
  }
  
  boolean mustNotUnlockCurrentUser()
  {
    boolean bool2 = false;
    boolean bool1;
    if (!UserManager.isSplitSystemUser())
    {
      bool1 = bool2;
      if (!UserManager.isDeviceInDemoMode(this.mContext)) {}
    }
    else
    {
      bool1 = bool2;
      if (KeyguardUpdateMonitor.getCurrentUser() == 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public void notifyLidSwitchChanged(boolean paramBoolean)
  {
    if (this.mUpdateMonitor != null) {
      this.mUpdateMonitor.notifyLidSwitchChanged(paramBoolean);
    }
  }
  
  public void notifyScreenOffAuthenticate(boolean paramBoolean, int paramInt)
  {
    Log.d("KeyguardViewMediator", "notifyAuthenticate Change: " + paramBoolean + ", type:" + paramInt + ", current:" + this.mAuthenticatingType);
    if (paramBoolean)
    {
      if (this.mAuthenticatingType != AUTHENTICATE_IGNORE) {
        return;
      }
      this.mAuthenticatingType = paramInt;
    }
    for (;;)
    {
      paramInt = this.mKeyguardStateCallbacks.size() - 1;
      label80:
      if (paramInt >= 0) {
        try
        {
          ((IKeyguardStateCallback)this.mKeyguardStateCallbacks.get(paramInt)).onFingerprintStateChange(paramBoolean);
          paramInt -= 1;
          break label80;
          this.mAuthenticatingType = AUTHENTICATE_IGNORE;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.w("KeyguardViewMediator", "Failed to call onFingerprintStateChange", localRemoteException);
            if ((localRemoteException instanceof DeadObjectException)) {
              this.mKeyguardStateCallbacks.remove(paramInt);
            }
          }
        }
      }
    }
  }
  
  public void onActivityDrawn()
  {
    this.mHandler.sendEmptyMessage(19);
  }
  
  public void onBootCompleted()
  {
    this.mUpdateMonitor.dispatchBootCompleted();
    try
    {
      this.mBootCompleted = true;
      if (this.mBootSendUserPresent) {
        sendUserPresentBroadcast();
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void onDreamingStarted()
  {
    KeyguardUpdateMonitor.getInstance(this.mContext).dispatchDreamingStarted();
    LSState.getInstance().onDreamingStarted();
    try
    {
      if ((this.mDeviceInteractive) && (this.mLockPatternUtils.isSecure(KeyguardUpdateMonitor.getCurrentUser()))) {
        doKeyguardLaterLocked();
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void onDreamingStopped()
  {
    KeyguardUpdateMonitor.getInstance(this.mContext).dispatchDreamingStopped();
    LSState.getInstance().onDreamingStopped();
    try
    {
      if (this.mDeviceInteractive) {
        cancelDoKeyguardLaterLocked();
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void onEarlyFingerprintUnlockFail()
  {
    this.mHandler.removeMessages(103);
    Message localMessage = this.mHandler.obtainMessage(103);
    this.mHandler.sendMessage(localMessage);
  }
  
  public void onEarlyFingerprintUnlockStart()
  {
    this.mHandler.removeMessages(101);
    Message localMessage = this.mHandler.obtainMessage(101);
    this.mHandler.sendMessage(localMessage);
  }
  
  public void onEarlyNotifyDraw()
  {
    this.mHandler.removeMessages(102);
    Message localMessage = this.mHandler.obtainMessage(102);
    this.mHandler.sendMessage(localMessage);
  }
  
  /* Error */
  public void onFinishedGoingToSleep(int paramInt, boolean paramBoolean)
  {
    // Byte code:
    //   0: ldc_w 414
    //   3: new 509	java/lang/StringBuilder
    //   6: dup
    //   7: invokespecial 510	java/lang/StringBuilder:<init>	()V
    //   10: ldc_w 1420
    //   13: invokevirtual 516	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   16: iload_1
    //   17: invokevirtual 519	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   20: ldc_w 1422
    //   23: invokevirtual 516	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   26: iload_2
    //   27: invokevirtual 674	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   30: ldc_w 742
    //   33: invokevirtual 516	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   36: invokevirtual 523	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   39: invokestatic 526	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   42: pop
    //   43: aload_0
    //   44: monitorenter
    //   45: aload_0
    //   46: iconst_0
    //   47: putfield 163	com/android/systemui/keyguard/KeyguardViewMediator:mDeviceInteractive	Z
    //   50: aload_0
    //   51: iconst_0
    //   52: putfield 750	com/android/systemui/keyguard/KeyguardViewMediator:mGoingToSleep	Z
    //   55: aload_0
    //   56: invokespecial 291	com/android/systemui/keyguard/KeyguardViewMediator:resetKeyguardDonePendingLocked	()V
    //   59: aload_0
    //   60: iconst_0
    //   61: putfield 189	com/android/systemui/keyguard/KeyguardViewMediator:mHideAnimationRun	Z
    //   64: aload_0
    //   65: invokespecial 1424	com/android/systemui/keyguard/KeyguardViewMediator:notifyFinishedGoingToSleep	()V
    //   68: iload_2
    //   69: ifeq +45 -> 114
    //   72: ldc_w 414
    //   75: ldc_w 1426
    //   78: invokestatic 755	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   81: pop
    //   82: aload_0
    //   83: getfield 402	com/android/systemui/keyguard/KeyguardViewMediator:mContext	Landroid/content/Context;
    //   86: ldc_w 1087
    //   89: invokevirtual 1429	android/content/Context:getSystemService	(Ljava/lang/Class;)Ljava/lang/Object;
    //   92: checkcast 1087	android/os/PowerManager
    //   95: invokestatic 713	android/os/SystemClock:uptimeMillis	()J
    //   98: ldc_w 1431
    //   101: invokevirtual 1435	android/os/PowerManager:wakeUp	(JLjava/lang/String;)V
    //   104: aload_0
    //   105: iconst_0
    //   106: putfield 1329	com/android/systemui/keyguard/KeyguardViewMediator:mPendingLock	Z
    //   109: aload_0
    //   110: iconst_0
    //   111: putfield 1325	com/android/systemui/keyguard/KeyguardViewMediator:mPendingReset	Z
    //   114: aload_0
    //   115: getfield 1325	com/android/systemui/keyguard/KeyguardViewMediator:mPendingReset	Z
    //   118: ifeq +18 -> 136
    //   121: iload_1
    //   122: bipush 11
    //   124: if_icmpeq +12 -> 136
    //   127: aload_0
    //   128: invokespecial 295	com/android/systemui/keyguard/KeyguardViewMediator:resetStateLocked	()V
    //   131: aload_0
    //   132: iconst_0
    //   133: putfield 1325	com/android/systemui/keyguard/KeyguardViewMediator:mPendingReset	Z
    //   136: aload_0
    //   137: getfield 1329	com/android/systemui/keyguard/KeyguardViewMediator:mPendingLock	Z
    //   140: ifeq +32 -> 172
    //   143: new 541	android/os/Bundle
    //   146: dup
    //   147: invokespecial 1436	android/os/Bundle:<init>	()V
    //   150: astore 4
    //   152: aload 4
    //   154: ldc_w 539
    //   157: iload_1
    //   158: invokevirtual 1440	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   161: aload_0
    //   162: aload 4
    //   164: invokespecial 263	com/android/systemui/keyguard/KeyguardViewMediator:doKeyguardLocked	(Landroid/os/Bundle;)V
    //   167: aload_0
    //   168: iconst_0
    //   169: putfield 1329	com/android/systemui/keyguard/KeyguardViewMediator:mPendingLock	Z
    //   172: aload_0
    //   173: getfield 1442	com/android/systemui/keyguard/KeyguardViewMediator:mLockLater	Z
    //   176: istore_3
    //   177: iload_3
    //   178: ifne +7 -> 185
    //   181: iload_2
    //   182: ifeq +17 -> 199
    //   185: aload_0
    //   186: monitorexit
    //   187: aload_0
    //   188: getfield 402	com/android/systemui/keyguard/KeyguardViewMediator:mContext	Landroid/content/Context;
    //   191: invokestatic 921	com/android/keyguard/KeyguardUpdateMonitor:getInstance	(Landroid/content/Context;)Lcom/android/keyguard/KeyguardUpdateMonitor;
    //   194: iload_1
    //   195: invokevirtual 1445	com/android/keyguard/KeyguardUpdateMonitor:dispatchFinishedGoingToSleep	(I)V
    //   198: return
    //   199: aload_0
    //   200: invokespecial 465	com/android/systemui/keyguard/KeyguardViewMediator:doKeyguardForChildProfilesLocked	()V
    //   203: goto -18 -> 185
    //   206: astore 4
    //   208: aload_0
    //   209: monitorexit
    //   210: aload 4
    //   212: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	213	0	this	KeyguardViewMediator
    //   0	213	1	paramInt	int
    //   0	213	2	paramBoolean	boolean
    //   176	2	3	bool	boolean
    //   150	13	4	localBundle	Bundle
    //   206	5	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   45	68	206	finally
    //   72	114	206	finally
    //   114	121	206	finally
    //   127	136	206	finally
    //   136	172	206	finally
    //   172	177	206	finally
    //   199	203	206	finally
  }
  
  public void onScreenTurnedOff()
  {
    notifyScreenTurnedOff();
    this.mUpdateMonitor.dispatchScreenTurnedOff();
  }
  
  public void onScreenTurnedOn()
  {
    Log.d("KeyguardViewMediator", "onScreenTurnedOn");
    Trace.beginSection("KeyguardViewMediator#onScreenTurnedOn");
    notifyScreenTurnedOn();
    this.mUpdateMonitor.dispatchScreenTurnedOn();
    Trace.endSection();
  }
  
  public void onScreenTurningOn(IKeyguardDrawnCallback paramIKeyguardDrawnCallback)
  {
    Log.d("KeyguardViewMediator", "onScreenTurningOn");
    Trace.beginSection("KeyguardViewMediator#onScreenTurningOn");
    notifyScreenOn(paramIKeyguardDrawnCallback);
    Trace.endSection();
  }
  
  /* Error */
  public void onStartedGoingToSleep(int paramInt)
  {
    // Byte code:
    //   0: ldc_w 1465
    //   3: iconst_0
    //   4: invokestatic 1466	android/os/SystemProperties:getInt	(Ljava/lang/String;I)I
    //   7: ifeq +148 -> 155
    //   10: iconst_1
    //   11: putstatic 330	com/android/systemui/keyguard/KeyguardViewMediator:DEBUG_MESSAGE	Z
    //   14: ldc_w 414
    //   17: new 509	java/lang/StringBuilder
    //   20: dup
    //   21: invokespecial 510	java/lang/StringBuilder:<init>	()V
    //   24: ldc_w 1468
    //   27: invokevirtual 516	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   30: iload_1
    //   31: invokevirtual 519	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   34: ldc_w 742
    //   37: invokevirtual 516	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   40: getstatic 330	com/android/systemui/keyguard/KeyguardViewMediator:DEBUG_MESSAGE	Z
    //   43: invokevirtual 674	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   46: invokevirtual 523	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   49: invokestatic 526	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   52: pop
    //   53: aload_0
    //   54: monitorenter
    //   55: aload_0
    //   56: iconst_0
    //   57: putfield 163	com/android/systemui/keyguard/KeyguardViewMediator:mDeviceInteractive	Z
    //   60: aload_0
    //   61: iconst_1
    //   62: putfield 750	com/android/systemui/keyguard/KeyguardViewMediator:mGoingToSleep	Z
    //   65: invokestatic 502	com/android/keyguard/KeyguardUpdateMonitor:getCurrentUser	()I
    //   68: istore_3
    //   69: aload_0
    //   70: getfield 138	com/android/systemui/keyguard/KeyguardViewMediator:mLockPatternUtils	Lcom/android/internal/widget/LockPatternUtils;
    //   73: iload_3
    //   74: invokevirtual 1471	com/android/internal/widget/LockPatternUtils:getPowerButtonInstantlyLocks	(I)Z
    //   77: ifne +85 -> 162
    //   80: aload_0
    //   81: getfield 138	com/android/systemui/keyguard/KeyguardViewMediator:mLockPatternUtils	Lcom/android/internal/widget/LockPatternUtils;
    //   84: iload_3
    //   85: invokevirtual 735	com/android/internal/widget/LockPatternUtils:isSecure	(I)Z
    //   88: ifeq +79 -> 167
    //   91: iconst_0
    //   92: istore_2
    //   93: aload_0
    //   94: invokestatic 502	com/android/keyguard/KeyguardUpdateMonitor:getCurrentUser	()I
    //   97: invokespecial 463	com/android/systemui/keyguard/KeyguardViewMediator:getLockTimeout	(I)J
    //   100: lstore 4
    //   102: aload_0
    //   103: iconst_0
    //   104: putfield 1442	com/android/systemui/keyguard/KeyguardViewMediator:mLockLater	Z
    //   107: iload_1
    //   108: iconst_4
    //   109: if_icmpne +63 -> 172
    //   112: aload_0
    //   113: getfield 1329	com/android/systemui/keyguard/KeyguardViewMediator:mPendingLock	Z
    //   116: ifeq +21 -> 137
    //   119: iload_1
    //   120: bipush 11
    //   122: if_icmpeq +15 -> 137
    //   125: aload_0
    //   126: getfield 194	com/android/systemui/keyguard/KeyguardViewMediator:mPhoneState	I
    //   129: ifne +8 -> 137
    //   132: aload_0
    //   133: iconst_1
    //   134: invokespecial 932	com/android/systemui/keyguard/KeyguardViewMediator:playSounds	(Z)V
    //   137: aload_0
    //   138: monitorexit
    //   139: aload_0
    //   140: getfield 402	com/android/systemui/keyguard/KeyguardViewMediator:mContext	Landroid/content/Context;
    //   143: invokestatic 921	com/android/keyguard/KeyguardUpdateMonitor:getInstance	(Landroid/content/Context;)Lcom/android/keyguard/KeyguardUpdateMonitor;
    //   146: iload_1
    //   147: invokevirtual 1474	com/android/keyguard/KeyguardUpdateMonitor:dispatchStartedGoingToSleep	(I)V
    //   150: aload_0
    //   151: invokespecial 1476	com/android/systemui/keyguard/KeyguardViewMediator:notifyStartedGoingToSleep	()V
    //   154: return
    //   155: iconst_0
    //   156: putstatic 330	com/android/systemui/keyguard/KeyguardViewMediator:DEBUG_MESSAGE	Z
    //   159: goto -145 -> 14
    //   162: iconst_1
    //   163: istore_2
    //   164: goto -71 -> 93
    //   167: iconst_1
    //   168: istore_2
    //   169: goto -76 -> 93
    //   172: aload_0
    //   173: getfield 775	com/android/systemui/keyguard/KeyguardViewMediator:mExitSecureCallback	Lcom/android/internal/policy/IKeyguardExitCallback;
    //   176: ifnull +66 -> 242
    //   179: ldc_w 414
    //   182: ldc_w 1478
    //   185: invokestatic 526	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   188: pop
    //   189: aload_0
    //   190: getfield 775	com/android/systemui/keyguard/KeyguardViewMediator:mExitSecureCallback	Lcom/android/internal/policy/IKeyguardExitCallback;
    //   193: iconst_0
    //   194: invokeinterface 780 2 0
    //   199: aload_0
    //   200: aconst_null
    //   201: putfield 775	com/android/systemui/keyguard/KeyguardViewMediator:mExitSecureCallback	Lcom/android/internal/policy/IKeyguardExitCallback;
    //   204: aload_0
    //   205: getfield 166	com/android/systemui/keyguard/KeyguardViewMediator:mExternallyEnabled	Z
    //   208: ifne -96 -> 112
    //   211: aload_0
    //   212: invokespecial 613	com/android/systemui/keyguard/KeyguardViewMediator:hideLocked	()V
    //   215: goto -103 -> 112
    //   218: astore 6
    //   220: aload_0
    //   221: monitorexit
    //   222: aload 6
    //   224: athrow
    //   225: astore 6
    //   227: ldc_w 414
    //   230: ldc_w 1480
    //   233: aload 6
    //   235: invokestatic 794	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   238: pop
    //   239: goto -40 -> 199
    //   242: aload_0
    //   243: getfield 142	com/android/systemui/keyguard/KeyguardViewMediator:mShowing	Z
    //   246: ifeq +44 -> 290
    //   249: aload_0
    //   250: iconst_1
    //   251: putfield 1325	com/android/systemui/keyguard/KeyguardViewMediator:mPendingReset	Z
    //   254: goto -142 -> 112
    //   257: aload_0
    //   258: lload 4
    //   260: invokespecial 505	com/android/systemui/keyguard/KeyguardViewMediator:doKeyguardLaterLocked	(J)V
    //   263: aload_0
    //   264: iconst_1
    //   265: putfield 1442	com/android/systemui/keyguard/KeyguardViewMediator:mLockLater	Z
    //   268: goto -156 -> 112
    //   271: aload_0
    //   272: getfield 138	com/android/systemui/keyguard/KeyguardViewMediator:mLockPatternUtils	Lcom/android/internal/widget/LockPatternUtils;
    //   275: iload_3
    //   276: invokevirtual 604	com/android/internal/widget/LockPatternUtils:isLockScreenDisabled	(I)Z
    //   279: ifne -167 -> 112
    //   282: aload_0
    //   283: iconst_1
    //   284: putfield 1329	com/android/systemui/keyguard/KeyguardViewMediator:mPendingLock	Z
    //   287: goto -175 -> 112
    //   290: iload_1
    //   291: iconst_3
    //   292: if_icmpne +13 -> 305
    //   295: lload 4
    //   297: lconst_0
    //   298: lcmp
    //   299: ifle +6 -> 305
    //   302: goto -45 -> 257
    //   305: iload_1
    //   306: iconst_2
    //   307: if_icmpne -36 -> 271
    //   310: iload_2
    //   311: ifeq -54 -> 257
    //   314: goto -43 -> 271
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	317	0	this	KeyguardViewMediator
    //   0	317	1	paramInt	int
    //   92	219	2	i	int
    //   68	208	3	j	int
    //   100	196	4	l	long
    //   218	5	6	localObject	Object
    //   225	9	6	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   55	91	218	finally
    //   93	107	218	finally
    //   112	119	218	finally
    //   125	137	218	finally
    //   172	189	218	finally
    //   189	199	218	finally
    //   199	215	218	finally
    //   227	239	218	finally
    //   242	254	218	finally
    //   257	268	218	finally
    //   271	287	218	finally
    //   189	199	225	android/os/RemoteException
  }
  
  public void onStartedWakingUp()
  {
    Trace.beginSection("KeyguardViewMediator#onStartedWakingUp");
    if ((DEBUG_MESSAGE) || (DEBUG_SCREEN_ON)) {
      this.mHandler.getLooper().setMessageLogging(new LogPrinter(3, "SystemUI"));
    }
    try
    {
      this.mDeviceInteractive = true;
      cancelDoKeyguardLaterLocked();
      cancelDoKeyguardForChildProfilesLocked();
      notifyStartedWakingUp();
      KeyguardUpdateMonitor.getInstance(this.mContext).dispatchStartedWakingUp();
      if ((this.mBar != null) && (this.mBar.getFacelockController() != null)) {
        this.mBar.getFacelockController().onPreStartedWakingUp();
      }
      maybeSendUserPresentBroadcast();
      Trace.endSection();
      return;
    }
    finally {}
  }
  
  public void onSystemReady()
  {
    this.mSearchManager = ((SearchManager)this.mContext.getSystemService("search"));
    try
    {
      Log.d("KeyguardViewMediator", "onSystemReady");
      this.mSystemReady = true;
      doKeyguardLocked(null);
      this.mUpdateMonitor.registerCallback(this.mUpdateCallback);
      Utils.setSystemReady();
      this.mUpdateMonitor.dispatchSystemReady();
      this.mIsPerUserLock = StorageManager.isFileEncryptedNativeOrEmulated();
      maybeSendUserPresentBroadcast();
      this.mUpdateMonitor.setUserUnlocked(UserManager.get(this.mContext).isUserUnlocked(KeyguardUpdateMonitor.getCurrentUser()));
      return;
    }
    finally {}
  }
  
  public void onWakeAndUnlocking()
  {
    Trace.beginSection("KeyguardViewMediator#onWakeAndUnlocking");
    this.mWakeAndUnlocking = true;
    keyguardDone(true);
    Trace.endSection();
  }
  
  public StatusBarKeyguardViewManager registerStatusBar(PhoneStatusBar paramPhoneStatusBar, ViewGroup paramViewGroup, StatusBarWindowManager paramStatusBarWindowManager, ScrimController paramScrimController, FingerprintUnlockController paramFingerprintUnlockController)
  {
    this.mScrimController = paramScrimController;
    this.mBar = paramPhoneStatusBar;
    this.mStatusBarWindowManager = paramStatusBarWindowManager;
    this.mStatusBarKeyguardViewManager.registerStatusBar(paramPhoneStatusBar, paramViewGroup, paramStatusBarWindowManager, paramScrimController, paramFingerprintUnlockController);
    return this.mStatusBarKeyguardViewManager;
  }
  
  public void setCurrentUser(int paramInt)
  {
    KeyguardUpdateMonitor.setCurrentUser(paramInt);
    try
    {
      notifyTrustedChangedLocked(this.mUpdateMonitor.getUserHasTrust(paramInt));
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public void setKeyguardEnabled(boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: ldc_w 414
    //   5: new 509	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 510	java/lang/StringBuilder:<init>	()V
    //   12: ldc_w 1546
    //   15: invokevirtual 516	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   18: iload_1
    //   19: invokevirtual 674	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   22: ldc_w 742
    //   25: invokevirtual 516	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   28: invokevirtual 523	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   31: invokestatic 526	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   34: pop
    //   35: aload_0
    //   36: iload_1
    //   37: putfield 166	com/android/systemui/keyguard/KeyguardViewMediator:mExternallyEnabled	Z
    //   40: iload_1
    //   41: ifne +56 -> 97
    //   44: aload_0
    //   45: getfield 142	com/android/systemui/keyguard/KeyguardViewMediator:mShowing	Z
    //   48: ifeq +49 -> 97
    //   51: aload_0
    //   52: getfield 775	com/android/systemui/keyguard/KeyguardViewMediator:mExitSecureCallback	Lcom/android/internal/policy/IKeyguardExitCallback;
    //   55: ifnull +16 -> 71
    //   58: ldc_w 414
    //   61: ldc_w 1548
    //   64: invokestatic 526	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   67: pop
    //   68: aload_0
    //   69: monitorexit
    //   70: return
    //   71: ldc_w 414
    //   74: ldc_w 1550
    //   77: invokestatic 526	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   80: pop
    //   81: aload_0
    //   82: iconst_1
    //   83: putfield 357	com/android/systemui/keyguard/KeyguardViewMediator:mNeedToReshowWhenReenabled	Z
    //   86: aload_0
    //   87: invokespecial 1079	com/android/systemui/keyguard/KeyguardViewMediator:updateInputRestrictedLocked	()V
    //   90: aload_0
    //   91: invokespecial 613	com/android/systemui/keyguard/KeyguardViewMediator:hideLocked	()V
    //   94: aload_0
    //   95: monitorexit
    //   96: return
    //   97: iload_1
    //   98: ifeq -4 -> 94
    //   101: aload_0
    //   102: getfield 357	com/android/systemui/keyguard/KeyguardViewMediator:mNeedToReshowWhenReenabled	Z
    //   105: ifeq -11 -> 94
    //   108: ldc_w 414
    //   111: ldc_w 1552
    //   114: invokestatic 526	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   117: pop
    //   118: aload_0
    //   119: iconst_0
    //   120: putfield 357	com/android/systemui/keyguard/KeyguardViewMediator:mNeedToReshowWhenReenabled	Z
    //   123: aload_0
    //   124: invokespecial 1079	com/android/systemui/keyguard/KeyguardViewMediator:updateInputRestrictedLocked	()V
    //   127: aload_0
    //   128: getfield 775	com/android/systemui/keyguard/KeyguardViewMediator:mExitSecureCallback	Lcom/android/internal/policy/IKeyguardExitCallback;
    //   131: ifnull +55 -> 186
    //   134: ldc_w 414
    //   137: ldc_w 1554
    //   140: invokestatic 526	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   143: pop
    //   144: aload_0
    //   145: getfield 775	com/android/systemui/keyguard/KeyguardViewMediator:mExitSecureCallback	Lcom/android/internal/policy/IKeyguardExitCallback;
    //   148: iconst_0
    //   149: invokeinterface 780 2 0
    //   154: aload_0
    //   155: aconst_null
    //   156: putfield 775	com/android/systemui/keyguard/KeyguardViewMediator:mExitSecureCallback	Lcom/android/internal/policy/IKeyguardExitCallback;
    //   159: aload_0
    //   160: invokespecial 295	com/android/systemui/keyguard/KeyguardViewMediator:resetStateLocked	()V
    //   163: goto -69 -> 94
    //   166: astore_2
    //   167: aload_0
    //   168: monitorexit
    //   169: aload_2
    //   170: athrow
    //   171: astore_2
    //   172: ldc_w 414
    //   175: ldc_w 1480
    //   178: aload_2
    //   179: invokestatic 794	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   182: pop
    //   183: goto -29 -> 154
    //   186: aload_0
    //   187: aconst_null
    //   188: invokespecial 572	com/android/systemui/keyguard/KeyguardViewMediator:showLocked	(Landroid/os/Bundle;)V
    //   191: aload_0
    //   192: iconst_1
    //   193: putfield 361	com/android/systemui/keyguard/KeyguardViewMediator:mWaitingUntilKeyguardVisible	Z
    //   196: aload_0
    //   197: getfield 174	com/android/systemui/keyguard/KeyguardViewMediator:mHandler	Landroid/os/Handler;
    //   200: bipush 10
    //   202: ldc2_w 1555
    //   205: invokevirtual 1560	android/os/Handler:sendEmptyMessageDelayed	(IJ)Z
    //   208: pop
    //   209: ldc_w 414
    //   212: ldc_w 1562
    //   215: invokestatic 526	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   218: pop
    //   219: aload_0
    //   220: getfield 361	com/android/systemui/keyguard/KeyguardViewMediator:mWaitingUntilKeyguardVisible	Z
    //   223: istore_1
    //   224: iload_1
    //   225: ifeq +20 -> 245
    //   228: aload_0
    //   229: invokevirtual 1565	com/android/systemui/keyguard/KeyguardViewMediator:wait	()V
    //   232: goto -13 -> 219
    //   235: astore_2
    //   236: invokestatic 1571	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   239: invokevirtual 1574	java/lang/Thread:interrupt	()V
    //   242: goto -23 -> 219
    //   245: ldc_w 414
    //   248: ldc_w 1576
    //   251: invokestatic 526	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   254: pop
    //   255: goto -161 -> 94
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	258	0	this	KeyguardViewMediator
    //   0	258	1	paramBoolean	boolean
    //   166	4	2	localObject	Object
    //   171	8	2	localRemoteException	RemoteException
    //   235	1	2	localInterruptedException	InterruptedException
    // Exception table:
    //   from	to	target	type
    //   2	40	166	finally
    //   44	68	166	finally
    //   71	94	166	finally
    //   101	144	166	finally
    //   144	154	166	finally
    //   154	163	166	finally
    //   172	183	166	finally
    //   186	219	166	finally
    //   219	224	166	finally
    //   228	232	166	finally
    //   236	242	166	finally
    //   245	255	166	finally
    //   144	154	171	android/os/RemoteException
    //   228	232	235	java/lang/InterruptedException
  }
  
  public void setOccluded(boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = 1;
    Trace.beginSection("KeyguardViewMediator#setOccluded");
    this.mHandler.removeMessages(12);
    Object localObject = this.mHandler;
    int i;
    if (paramBoolean1)
    {
      i = 1;
      if (!paramBoolean2) {
        break label65;
      }
    }
    for (;;)
    {
      localObject = ((Handler)localObject).obtainMessage(12, i, j);
      this.mHandler.sendMessage((Message)localObject);
      Trace.endSection();
      return;
      i = 0;
      break;
      label65:
      j = 0;
    }
  }
  
  public void start()
  {
    try
    {
      setupLocked();
      putComponent(KeyguardViewMediator.class, this);
      return;
    }
    finally {}
  }
  
  public void startKeyguardExitAnimation(long paramLong1, long paramLong2)
  {
    Trace.beginSection("KeyguardViewMediator#startKeyguardExitAnimation");
    if (this.mPowerKeyCameraLaunching)
    {
      this.mPowerKeyCameraLaunching = false;
      this.mHandler.removeMessages(25);
      Log.d("KeyguardViewMediator", "handleStartKeyguardExitAnimation: callback receive from wm, remove time out message");
    }
    Message localMessage = this.mHandler.obtainMessage(18, new StartKeyguardExitAnimParams(paramLong1, paramLong2, null));
    this.mHandler.sendMessage(localMessage);
    Trace.endSection();
  }
  
  public void userActivity()
  {
    this.mPM.userActivity(SystemClock.uptimeMillis(), false);
  }
  
  public void verifyUnlock(IKeyguardExitCallback paramIKeyguardExitCallback)
  {
    Trace.beginSection("KeyguardViewMediator#verifyUnlock");
    for (;;)
    {
      try
      {
        Log.d("KeyguardViewMediator", "verifyUnlock");
        if (shouldWaitForProvisioning())
        {
          Log.d("KeyguardViewMediator", "ignoring because device isn't provisioned");
          try
          {
            paramIKeyguardExitCallback.onKeyguardExitResult(false);
            Trace.endSection();
            return;
          }
          catch (RemoteException paramIKeyguardExitCallback)
          {
            Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", paramIKeyguardExitCallback);
            continue;
          }
        }
        if (!this.mExternallyEnabled) {
          break label110;
        }
      }
      finally {}
      Log.w("KeyguardViewMediator", "verifyUnlock called when not externally disabled");
      try
      {
        paramIKeyguardExitCallback.onKeyguardExitResult(false);
      }
      catch (RemoteException paramIKeyguardExitCallback)
      {
        Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", paramIKeyguardExitCallback);
      }
      continue;
      label110:
      IKeyguardExitCallback localIKeyguardExitCallback = this.mExitSecureCallback;
      if (localIKeyguardExitCallback != null)
      {
        try
        {
          paramIKeyguardExitCallback.onKeyguardExitResult(false);
        }
        catch (RemoteException paramIKeyguardExitCallback)
        {
          Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", paramIKeyguardExitCallback);
        }
      }
      else if (!isSecure())
      {
        this.mExternallyEnabled = true;
        this.mNeedToReshowWhenReenabled = false;
        updateInputRestricted();
        try
        {
          paramIKeyguardExitCallback.onKeyguardExitResult(true);
        }
        catch (RemoteException paramIKeyguardExitCallback)
        {
          Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", paramIKeyguardExitCallback);
        }
      }
      else
      {
        if (this.mNeedToReshowWhenReenabled)
        {
          Slog.d("KeyguardViewMediator", "reshow when security and home key");
          setKeyguardEnabled(true);
        }
        try
        {
          paramIKeyguardExitCallback.onKeyguardExitResult(false);
        }
        catch (RemoteException paramIKeyguardExitCallback)
        {
          Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", paramIKeyguardExitCallback);
        }
      }
    }
  }
  
  private static class StartKeyguardExitAnimParams
  {
    long fadeoutDuration;
    long startTime;
    
    private StartKeyguardExitAnimParams(long paramLong1, long paramLong2)
    {
      this.startTime = paramLong1;
      this.fadeoutDuration = paramLong2;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\keyguard\KeyguardViewMediator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */