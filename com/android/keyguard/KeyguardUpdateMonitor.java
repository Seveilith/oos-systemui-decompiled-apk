package com.android.keyguard;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AlarmManager;
import android.app.IActivityManager;
import android.app.IUserSwitchObserver.Stub;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.app.trust.TrustManager;
import android.app.trust.TrustManager.TrustListener;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.SystemSensorManager;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager.AuthenticationCallback;
import android.hardware.fingerprint.FingerprintManager.AuthenticationResult;
import android.hardware.fingerprint.FingerprintManager.LockoutResetCallback;
import android.hardware.fingerprint.IFingerprintService;
import android.hardware.fingerprint.IFingerprintService.Stub;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.IRemoteCallback;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionManager.OnSubscriptionsChangedListener;
import android.telephony.TelephonyManager;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.internal.telephony.IccCardConstants.State;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternUtils.StrongAuthTracker;
import com.android.keyguard.plugin.ClockCtrl;
import com.android.keyguard.plugin.ClockCtrl.OnTimeUpdatedListener;
import com.google.android.collect.Lists;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class KeyguardUpdateMonitor
  implements TrustManager.TrustListener
{
  private static final ComponentName FALLBACK_HOME_COMPONENT = new ComponentName("com.android.settings", "com.android.settings.FallbackHome");
  private static int sCurrentUser;
  private static KeyguardUpdateMonitor sInstance;
  private AlarmManager mAlarmManager;
  private FingerprintManager.AuthenticationCallback mAuthenticationCallback = new FingerprintManager.AuthenticationCallback()
  {
    public void onAuthenticationAcquired(int paramAnonymousInt)
    {
      KeyguardUpdateMonitor.-wrap5(KeyguardUpdateMonitor.this, paramAnonymousInt);
    }
    
    public void onAuthenticationError(int paramAnonymousInt, CharSequence paramAnonymousCharSequence)
    {
      if (paramAnonymousInt == 101)
      {
        if ((KeyguardUpdateMonitor.-get6(KeyguardUpdateMonitor.this) != 1) || (KeyguardUpdateMonitor.-get9(KeyguardUpdateMonitor.this))) {
          return;
        }
        Log.d("KeyguardUpdateMonitor", "state stopped when interrupted");
        KeyguardUpdateMonitor.-wrap26(KeyguardUpdateMonitor.this, 0);
        return;
      }
      KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).removeMessages(600);
      if (KeyguardUpdateMonitor.-get3(KeyguardUpdateMonitor.this))
      {
        KeyguardUpdateMonitor.-set1(KeyguardUpdateMonitor.this, false);
        KeyguardUpdateMonitor.this.updateFingerprintListeningState();
      }
      KeyguardUpdateMonitor.-wrap8(KeyguardUpdateMonitor.this, paramAnonymousInt, paramAnonymousCharSequence.toString());
    }
    
    public void onAuthenticationFailed()
    {
      KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).removeMessages(600);
      if (KeyguardUpdateMonitor.-get3(KeyguardUpdateMonitor.this))
      {
        KeyguardUpdateMonitor.-set1(KeyguardUpdateMonitor.this, false);
        KeyguardUpdateMonitor.this.updateFingerprintListeningState();
      }
      KeyguardUpdateMonitor.-wrap6(KeyguardUpdateMonitor.this);
    }
    
    public void onAuthenticationHelp(int paramAnonymousInt, CharSequence paramAnonymousCharSequence)
    {
      KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).removeMessages(600);
      if (KeyguardUpdateMonitor.-get3(KeyguardUpdateMonitor.this))
      {
        KeyguardUpdateMonitor.-set1(KeyguardUpdateMonitor.this, false);
        KeyguardUpdateMonitor.this.updateFingerprintListeningState();
      }
      KeyguardUpdateMonitor.-wrap9(KeyguardUpdateMonitor.this, paramAnonymousInt, paramAnonymousCharSequence.toString());
    }
    
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult paramAnonymousAuthenticationResult)
    {
      Trace.beginSection("KeyguardUpdateMonitor#onAuthenticationSucceeded");
      KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).removeMessages(600);
      if (KeyguardUpdateMonitor.-get3(KeyguardUpdateMonitor.this)) {
        KeyguardUpdateMonitor.-set1(KeyguardUpdateMonitor.this, false);
      }
      KeyguardUpdateMonitor.-wrap7(KeyguardUpdateMonitor.this, paramAnonymousAuthenticationResult.getUserId());
      Trace.endSection();
    }
  };
  private boolean mAutoFacelockEnabled = false;
  private BatteryStatus mBatteryStatus;
  private boolean mBootCompleted;
  private boolean mBouncer;
  private final BroadcastReceiver mBroadcastAllReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if ("android.app.action.NEXT_ALARM_CLOCK_CHANGED".equals(paramAnonymousContext)) {
        KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendEmptyMessage(301);
      }
      do
      {
        return;
        if ("android.intent.action.USER_INFO_CHANGED".equals(paramAnonymousContext))
        {
          KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendMessage(KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).obtainMessage(317, paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", getSendingUserId()), 0));
          return;
        }
        if ("com.android.facelock.FACE_UNLOCK_STARTED".equals(paramAnonymousContext))
        {
          Trace.beginSection("KeyguardUpdateMonitor.mBroadcastAllReceiver#onReceive ACTION_FACE_UNLOCK_STARTED");
          KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendMessage(KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).obtainMessage(327, 1, getSendingUserId()));
          Trace.endSection();
          return;
        }
        if ("com.android.facelock.FACE_UNLOCK_STOPPED".equals(paramAnonymousContext))
        {
          KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendMessage(KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).obtainMessage(327, 0, getSendingUserId()));
          return;
        }
        if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(paramAnonymousContext))
        {
          KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendEmptyMessage(309);
          return;
        }
        if ("android.intent.action.USER_UNLOCKED".equals(paramAnonymousContext))
        {
          KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendEmptyMessage(334);
          return;
        }
      } while (!"android.intent.action.TIME_SET".equals(paramAnonymousContext));
      KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendEmptyMessage(301);
    }
  };
  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if (("android.intent.action.TIME_TICK".equals(paramAnonymousContext)) || ("android.intent.action.TIMEZONE_CHANGED".equals(paramAnonymousContext))) {
        KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendEmptyMessage(301);
      }
      do
      {
        return;
        int j;
        int i;
        if ("android.intent.action.BATTERY_CHANGED".equals(paramAnonymousContext))
        {
          int k = paramAnonymousIntent.getIntExtra("status", 1);
          int m = paramAnonymousIntent.getIntExtra("plugged", 0);
          int n = paramAnonymousIntent.getIntExtra("level", 0);
          int i1 = paramAnonymousIntent.getIntExtra("health", 1);
          int i2 = paramAnonymousIntent.getIntExtra("max_charging_current", -1);
          j = paramAnonymousIntent.getIntExtra("max_charging_voltage", -1);
          i = j;
          if (j <= 0) {
            i = 5000000;
          }
          if (i2 > 0) {}
          for (i = i2 / 1000 * (i / 1000);; i = -1)
          {
            boolean bool = paramAnonymousIntent.getBooleanExtra("fastcharge_status", false);
            paramAnonymousContext = KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).obtainMessage(302, new KeyguardUpdateMonitor.BatteryStatus(k, n, m, i1, i, bool));
            KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendMessage(paramAnonymousContext);
            return;
          }
        }
        Object localObject;
        if ("android.intent.action.SIM_STATE_CHANGED".equals(paramAnonymousContext))
        {
          localObject = KeyguardUpdateMonitor.SimData.fromIntent(paramAnonymousIntent);
          Log.v("KeyguardUpdateMonitor", "action " + paramAnonymousContext + " state: " + paramAnonymousIntent.getStringExtra("ss") + " slotId: " + ((KeyguardUpdateMonitor.SimData)localObject).slotId + " subid: " + ((KeyguardUpdateMonitor.SimData)localObject).subId);
          KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).obtainMessage(304, ((KeyguardUpdateMonitor.SimData)localObject).subId, ((KeyguardUpdateMonitor.SimData)localObject).slotId, ((KeyguardUpdateMonitor.SimData)localObject).simState).sendToTarget();
          return;
        }
        if ("android.media.RINGER_MODE_CHANGED".equals(paramAnonymousContext))
        {
          KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendMessage(KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).obtainMessage(305, paramAnonymousIntent.getIntExtra("android.media.EXTRA_RINGER_MODE", -1), 0));
          return;
        }
        if ("android.intent.action.PHONE_STATE".equals(paramAnonymousContext))
        {
          paramAnonymousContext = paramAnonymousIntent.getStringExtra("state");
          KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendMessage(KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).obtainMessage(306, paramAnonymousContext));
          return;
        }
        if ("android.intent.action.AIRPLANE_MODE".equals(paramAnonymousContext))
        {
          KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendEmptyMessage(329);
          return;
        }
        if ("android.intent.action.BOOT_COMPLETED".equals(paramAnonymousContext))
        {
          KeyguardUpdateMonitor.this.dispatchBootCompleted();
          return;
        }
        if ("android.intent.action.SERVICE_STATE".equals(paramAnonymousContext))
        {
          localObject = ServiceState.newFromBundle(paramAnonymousIntent.getExtras());
          i = paramAnonymousIntent.getIntExtra("subscription", -1);
          j = paramAnonymousIntent.getIntExtra("slot", 0);
          if (Utils.DEBUG_ONEPLUS) {
            Log.v("KeyguardUpdateMonitor", "action " + paramAnonymousContext + " serviceState=" + localObject + " slotId=" + j + " subId=" + i);
          }
          KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendMessage(KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).obtainMessage(330, j, i, localObject));
          return;
        }
      } while (!"android.intent.action.LOCALE_CHANGED".equals(paramAnonymousContext));
      KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendEmptyMessage(500);
    }
  };
  private final ArrayList<WeakReference<KeyguardUpdateMonitorCallback>> mCallbacks = Lists.newArrayList();
  private ClockCtrl mClockCtrl = ClockCtrl.getInstance();
  private final Context mContext;
  private boolean mDeviceInteractive;
  private boolean mDeviceProvisioned;
  private ContentObserver mDeviceProvisionedObserver;
  private DisplayClientState mDisplayClientState = new DisplayClientState();
  private boolean mDuringAcquired = false;
  private boolean mFacelockEnabled = false;
  private boolean mFacelockLightingEnabled = false;
  private int mFacelockRunningType = 0;
  private ContentObserver mFacelockSettingsObserver;
  private boolean mFacelockUnlocking;
  private SparseIntArray mFailedAttempts = new SparseIntArray();
  private boolean mFingerprintAlreadyAuthenticated;
  private CancellationSignal mFingerprintCancelSignal;
  private SparseIntArray mFingerprintFailedAttempts = new SparseIntArray();
  private int mFingerprintRunningState = 0;
  private FingerprintManager mFpm;
  private boolean mGoingToSleep;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      boolean bool2 = true;
      boolean bool1 = true;
      switch (paramAnonymousMessage.what)
      {
      default: 
      case 301: 
      case 302: 
      case 304: 
      case 305: 
      case 306: 
      case 308: 
      case 309: 
      case 310: 
      case 314: 
      case 312: 
      case 322: 
      case 313: 
      case 317: 
      case 318: 
      case 321: 
      case 320: 
      case 319: 
      case 327: 
      case 328: 
      case 329: 
      case 330: 
      case 331: 
      case 332: 
      case 500: 
      case 333: 
      case 334: 
      case 600: 
        do
        {
          return;
          KeyguardUpdateMonitor.-wrap22(KeyguardUpdateMonitor.this);
          return;
          KeyguardUpdateMonitor.-wrap2(KeyguardUpdateMonitor.this, (KeyguardUpdateMonitor.BatteryStatus)paramAnonymousMessage.obj);
          return;
          KeyguardUpdateMonitor.-wrap16(KeyguardUpdateMonitor.this, paramAnonymousMessage.arg2);
          KeyguardUpdateMonitor.-wrap21(KeyguardUpdateMonitor.this, paramAnonymousMessage.arg1, paramAnonymousMessage.arg2, (IccCardConstants.State)paramAnonymousMessage.obj);
          return;
          KeyguardUpdateMonitor.this.handleRingerModeChange(paramAnonymousMessage.arg1);
          return;
          KeyguardUpdateMonitor.this.handlePhoneStateChanged((String)paramAnonymousMessage.obj);
          return;
          KeyguardUpdateMonitor.this.handleDeviceProvisioned();
          return;
          KeyguardUpdateMonitor.this.handleDevicePolicyManagerStateChanged();
          return;
          KeyguardUpdateMonitor.this.handleUserSwitching(paramAnonymousMessage.arg1, (IRemoteCallback)paramAnonymousMessage.obj);
          return;
          KeyguardUpdateMonitor.this.handleUserSwitchComplete(paramAnonymousMessage.arg1);
          return;
          KeyguardUpdateMonitor.-wrap13(KeyguardUpdateMonitor.this);
          return;
          KeyguardUpdateMonitor.-wrap12(KeyguardUpdateMonitor.this, paramAnonymousMessage.arg1);
          return;
          KeyguardUpdateMonitor.this.handleBootCompleted();
          return;
          KeyguardUpdateMonitor.-wrap23(KeyguardUpdateMonitor.this, paramAnonymousMessage.arg1);
          return;
          KeyguardUpdateMonitor.-wrap17(KeyguardUpdateMonitor.this);
          return;
          KeyguardUpdateMonitor.this.handleStartedGoingToSleep(paramAnonymousMessage.arg1);
          return;
          KeyguardUpdateMonitor.this.handleFinishedGoingToSleep(paramAnonymousMessage.arg1);
          return;
          KeyguardUpdateMonitor.this.handleStartedWakingUp();
          return;
          Trace.beginSection("KeyguardUpdateMonitor#handler MSG_FACE_UNLOCK_STATE_CHANGED");
          localKeyguardUpdateMonitor = KeyguardUpdateMonitor.this;
          if (paramAnonymousMessage.arg1 != 0) {}
          for (;;)
          {
            KeyguardUpdateMonitor.-wrap4(localKeyguardUpdateMonitor, bool1, paramAnonymousMessage.arg2);
            Trace.endSection();
            return;
            bool1 = false;
          }
          if ((KeyguardUpdateMonitor.-get12(KeyguardUpdateMonitor.this)) || (KeyguardUpdateMonitor.-get13(KeyguardUpdateMonitor.this)))
          {
            KeyguardUpdateMonitor.-set3(KeyguardUpdateMonitor.this, true);
            Log.d("KeyguardUpdateMonitor", "delay handle subinfo change");
            return;
          }
          KeyguardUpdateMonitor.this.handleSimSubscriptionInfoChanged();
          return;
          KeyguardUpdateMonitor.-wrap1(KeyguardUpdateMonitor.this);
          return;
          KeyguardUpdateMonitor.-wrap20(KeyguardUpdateMonitor.this, paramAnonymousMessage.arg1, paramAnonymousMessage.arg2, (ServiceState)paramAnonymousMessage.obj);
          return;
          KeyguardUpdateMonitor.-wrap19(KeyguardUpdateMonitor.this);
          return;
          Trace.beginSection("KeyguardUpdateMonitor#handler MSG_SCREEN_TURNED_ON");
          KeyguardUpdateMonitor.-wrap18(KeyguardUpdateMonitor.this);
          KeyguardUpdateMonitor.-wrap15(KeyguardUpdateMonitor.this);
          Trace.endSection();
          return;
          KeyguardUpdateMonitor.-wrap3(KeyguardUpdateMonitor.this, paramAnonymousMessage.arg1);
          return;
          KeyguardUpdateMonitor.-wrap24(KeyguardUpdateMonitor.this);
          return;
        } while (!KeyguardUpdateMonitor.-get3(KeyguardUpdateMonitor.this));
        KeyguardUpdateMonitor.-set1(KeyguardUpdateMonitor.this, false);
        KeyguardUpdateMonitor.this.updateFingerprintListeningState();
        KeyguardUpdateMonitor.-wrap11(KeyguardUpdateMonitor.this);
        return;
      case 700: 
        KeyguardUpdateMonitor.this.handleSystemReady();
        return;
      case 701: 
        if ((KeyguardUpdateMonitor.-get12(KeyguardUpdateMonitor.this)) || (KeyguardUpdateMonitor.-get13(KeyguardUpdateMonitor.this)))
        {
          Log.d("KeyguardUpdateMonitor", "timeout delay of slot: " + paramAnonymousMessage.arg1 + ", " + KeyguardUpdateMonitor.-get12(KeyguardUpdateMonitor.this) + ", " + KeyguardUpdateMonitor.-get13(KeyguardUpdateMonitor.this));
          KeyguardUpdateMonitor.-wrap16(KeyguardUpdateMonitor.this, paramAnonymousMessage.arg1);
          return;
        }
        return;
      }
      KeyguardUpdateMonitor localKeyguardUpdateMonitor = KeyguardUpdateMonitor.this;
      if (paramAnonymousMessage.arg1 != 1) {}
      for (bool1 = bool2;; bool1 = false)
      {
        KeyguardUpdateMonitor.-wrap14(localKeyguardUpdateMonitor, bool1);
        return;
      }
    }
  };
  private boolean mHasLockscreenWallpaper;
  private boolean mIsDreaming;
  private boolean mIsFaceAdded = false;
  private boolean mIsKeyguardDone = true;
  private boolean mIsUserUnlocked = true;
  private boolean mKeyguardIsVisible;
  private boolean mLaunchingCamera;
  private boolean mLidOpen = true;
  private LockPatternUtils mLockPatternUtils;
  private final FingerprintManager.LockoutResetCallback mLockoutResetCallback = new FingerprintManager.LockoutResetCallback()
  {
    public void onLockoutReset()
    {
      KeyguardUpdateMonitor.-set2(KeyguardUpdateMonitor.this, false);
      KeyguardUpdateMonitor.-wrap10(KeyguardUpdateMonitor.this);
    }
  };
  private boolean mLockoutState = false;
  private boolean mNeedsSlowUnlockTransition;
  private boolean mPendingSubInfoChange = false;
  private int mPhoneState;
  private int mProximity = 0;
  SensorEventListener mProximityListener = new SensorEventListener()
  {
    public void onAccuracyChanged(Sensor paramAnonymousSensor, int paramAnonymousInt) {}
    
    public void onSensorChanged(SensorEvent paramAnonymousSensorEvent)
    {
      int i = 0;
      if (paramAnonymousSensorEvent.values.length == 0)
      {
        Log.d("KeyguardUpdateMonitor", "Event has no values!");
        i = 0;
      }
      for (;;)
      {
        if (KeyguardUpdateMonitor.-get10(KeyguardUpdateMonitor.this) != i) {
          KeyguardUpdateMonitor.-wrap27(KeyguardUpdateMonitor.this, i, KeyguardUpdateMonitor.-get8(KeyguardUpdateMonitor.this));
        }
        return;
        Log.d("KeyguardUpdateMonitor", "Event: value=" + paramAnonymousSensorEvent.values[0] + " max=" + KeyguardUpdateMonitor.-get11(KeyguardUpdateMonitor.this).getMaximumRange() + ", time:" + paramAnonymousSensorEvent.timestamp);
        if (paramAnonymousSensorEvent.values[0] == 1.0F) {
          i = 1;
        }
        if (i != 0) {
          i = 1;
        } else {
          i = 2;
        }
      }
    }
  };
  private Sensor mProximitySensor;
  private boolean mProximitySensorEnabled;
  private int mRingMode;
  private boolean mScreenOn;
  private SensorManager mSensorManager;
  HashMap<Integer, ServiceState> mServiceStates = new HashMap();
  HashMap<Integer, SimData> mSimDatas = new HashMap();
  private boolean mSimUnlockSlot0 = false;
  private boolean mSimUnlockSlot1 = false;
  private boolean mSkipBouncerByFacelock = false;
  private ArraySet<Integer> mStrongAuthNotTimedOut = new ArraySet();
  private final BroadcastReceiver mStrongAuthTimeoutReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("com.android.systemui.ACTION_STRONG_AUTH_TIMEOUT".equals(paramAnonymousIntent.getAction()))
      {
        int i = paramAnonymousIntent.getIntExtra("com.android.systemui.USER_ID", -1);
        KeyguardUpdateMonitor.-get14(KeyguardUpdateMonitor.this).remove(Integer.valueOf(i));
        KeyguardUpdateMonitor.-wrap25(KeyguardUpdateMonitor.this, i);
      }
    }
  };
  private final StrongAuthTracker mStrongAuthTracker;
  private List<SubscriptionInfo> mSubscriptionInfo;
  private SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionListener = new SubscriptionManager.OnSubscriptionsChangedListener()
  {
    public void onSubscriptionsChanged()
    {
      KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendEmptyMessage(328);
    }
  };
  private SubscriptionManager mSubscriptionManager;
  private boolean mSwitchingUser;
  private ClockCtrl.OnTimeUpdatedListener mTimeTickListener = new ClockCtrl.OnTimeUpdatedListener()
  {
    public void onTimeChanged()
    {
      Log.i("KeyguardUpdateMonitor", "onTimeChanged");
      if (KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this) != null)
      {
        KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).removeMessages(301);
        KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendEmptyMessage(301);
      }
    }
  };
  private TrustManager mTrustManager;
  private SparseBooleanArray mUserFaceUnlockRunning = new SparseBooleanArray();
  private SparseBooleanArray mUserFingerprintAuthenticated = new SparseBooleanArray();
  private SparseBooleanArray mUserHasTrust = new SparseBooleanArray();
  private UserManager mUserManager;
  private SparseBooleanArray mUserTrustIsManaged = new SparseBooleanArray();
  
  private KeyguardUpdateMonitor(Context paramContext)
  {
    this.mContext = paramContext;
    this.mSubscriptionManager = SubscriptionManager.from(paramContext);
    this.mAlarmManager = ((AlarmManager)paramContext.getSystemService(AlarmManager.class));
    this.mDeviceProvisioned = isDeviceProvisionedInSettingsDb();
    this.mStrongAuthTracker = new StrongAuthTracker(paramContext);
    if (!this.mDeviceProvisioned) {
      watchForDeviceProvisioning();
    }
    if (is17801Device()) {
      watchForFacelockSettings();
    }
    this.mBatteryStatus = new BatteryStatus(1, 100, 0, 0, 0, false);
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.BATTERY_CHANGED");
    localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
    localIntentFilter.addAction("android.intent.action.AIRPLANE_MODE");
    localIntentFilter.addAction("android.intent.action.LOCALE_CHANGED");
    localIntentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
    localIntentFilter.addAction("android.intent.action.SERVICE_STATE");
    localIntentFilter.addAction("android.intent.action.PHONE_STATE");
    localIntentFilter.addAction("android.media.RINGER_MODE_CHANGED");
    paramContext.registerReceiver(this.mBroadcastReceiver, localIntentFilter);
    localIntentFilter = new IntentFilter();
    localIntentFilter.setPriority(1000);
    localIntentFilter.addAction("android.intent.action.BOOT_COMPLETED");
    paramContext.registerReceiver(this.mBroadcastReceiver, localIntentFilter);
    localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.TIME_SET");
    localIntentFilter.addAction("android.intent.action.USER_INFO_CHANGED");
    localIntentFilter.addAction("android.app.action.NEXT_ALARM_CLOCK_CHANGED");
    localIntentFilter.addAction("com.android.facelock.FACE_UNLOCK_STARTED");
    localIntentFilter.addAction("com.android.facelock.FACE_UNLOCK_STOPPED");
    localIntentFilter.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
    localIntentFilter.addAction("android.intent.action.USER_UNLOCKED");
    paramContext.registerReceiverAsUser(this.mBroadcastAllReceiver, UserHandle.ALL, localIntentFilter, null, null);
    this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mSubscriptionListener);
    try
    {
      ActivityManagerNative.getDefault().registerUserSwitchObserver(new IUserSwitchObserver.Stub()
      {
        public void onForegroundProfileSwitch(int paramAnonymousInt) {}
        
        public void onUserSwitchComplete(int paramAnonymousInt)
          throws RemoteException
        {
          KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendMessage(KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).obtainMessage(314, paramAnonymousInt, 0));
        }
        
        public void onUserSwitching(int paramAnonymousInt, IRemoteCallback paramAnonymousIRemoteCallback)
        {
          KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendMessage(KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).obtainMessage(310, paramAnonymousInt, 0, paramAnonymousIRemoteCallback));
        }
      }, "KeyguardUpdateMonitor");
      localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("com.android.systemui.ACTION_STRONG_AUTH_TIMEOUT");
      paramContext.registerReceiver(this.mStrongAuthTimeoutReceiver, localIntentFilter, "com.android.systemui.permission.SELF", null);
      this.mTrustManager = ((TrustManager)paramContext.getSystemService("trust"));
      this.mTrustManager.registerTrustListener(this);
      this.mLockPatternUtils = new LockPatternUtils(paramContext);
      this.mLockPatternUtils.registerStrongAuthTracker(this.mStrongAuthTracker);
      this.mSensorManager = new SystemSensorManager(this.mContext, this.mHandler.getLooper());
      this.mProximitySensor = this.mSensorManager.getDefaultSensor(33171025, true);
      this.mFpm = ((FingerprintManager)paramContext.getSystemService("fingerprint"));
      updateFingerprintListeningState();
      if (this.mFpm != null) {
        this.mFpm.addLockoutResetCallback(this.mLockoutResetCallback);
      }
      this.mUserManager = ((UserManager)paramContext.getSystemService(UserManager.class));
      startClockCtrl();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        localRemoteException.rethrowAsRuntimeException();
      }
    }
  }
  
  private boolean canSkipBouncerByFacelock()
  {
    return this.mSkipBouncerByFacelock;
  }
  
  private void clearFailedFacelockAttempts()
  {
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onClearFailedFacelockAttempts();
      }
      i += 1;
    }
    notifyFacelockStateChanged(0);
  }
  
  private void clearFingerprintFailedUnlockAttempts()
  {
    this.mFingerprintFailedAttempts.delete(sCurrentUser);
  }
  
  private String getAuthenticatedPackage()
  {
    String str = "";
    IFingerprintService localIFingerprintService = IFingerprintService.Stub.asInterface(ServiceManager.getService("fingerprint"));
    if (localIFingerprintService != null) {}
    try
    {
      str = localIFingerprintService.getAuthenticatedPackage();
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("KeyguardUpdateMonitor", "getAuthenticatedPackage , " + localRemoteException);
    }
    return "";
  }
  
  public static int getCurrentUser()
  {
    try
    {
      int i = sCurrentUser;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public static KeyguardUpdateMonitor getInstance(Context paramContext)
  {
    if (sInstance == null) {
      sInstance = new KeyguardUpdateMonitor(paramContext);
    }
    return sInstance;
  }
  
  private void handleAirplaneModeChanged()
  {
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onRefreshCarrierInfo();
      }
      i += 1;
    }
  }
  
  private void handleBatteryUpdate(BatteryStatus paramBatteryStatus)
  {
    boolean bool = isBatteryUpdateInteresting(this.mBatteryStatus, paramBatteryStatus);
    this.mBatteryStatus = paramBatteryStatus;
    if (bool)
    {
      int i = 0;
      while (i < this.mCallbacks.size())
      {
        KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
        if (localKeyguardUpdateMonitorCallback != null) {
          localKeyguardUpdateMonitorCallback.onRefreshBatteryInfo(paramBatteryStatus);
        }
        i += 1;
      }
    }
  }
  
  private void handleDreamingStateChanged(int paramInt)
  {
    boolean bool = true;
    int i = this.mCallbacks.size();
    if (paramInt == 1) {}
    for (;;)
    {
      this.mIsDreaming = bool;
      paramInt = 0;
      while (paramInt < i)
      {
        KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(paramInt)).get();
        if (localKeyguardUpdateMonitorCallback != null) {
          localKeyguardUpdateMonitorCallback.onDreamingStateChanged(bool);
        }
        paramInt += 1;
      }
      bool = false;
    }
  }
  
  private void handleFaceUnlockStateChanged(boolean paramBoolean, int paramInt)
  {
    this.mUserFaceUnlockRunning.put(paramInt, paramBoolean);
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onFaceUnlockStateChanged(paramBoolean, paramInt);
      }
      i += 1;
    }
  }
  
  private void handleFingerprintAcquired(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1000)) {
      return;
    }
    this.mDuringAcquired = true;
    this.mHandler.removeMessages(600);
    this.mHandler.sendEmptyMessageDelayed(600, 1000L);
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onFingerprintAcquired(paramInt);
      }
      i += 1;
    }
  }
  
  private void handleFingerprintAuthFailed()
  {
    this.mFingerprintFailedAttempts.put(sCurrentUser, getFingerprintFailedUnlockAttempts() + 1);
    Log.d("KeyguardUpdateMonitor", "fp Auth Failed, failed attempts=" + getFingerprintFailedUnlockAttempts());
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onFingerprintAuthFailed();
      }
      i += 1;
    }
    handleFingerprintHelp(-1, this.mContext.getString(R.string.fingerprint_not_recognized));
  }
  
  private void handleFingerprintAuthenticated(int paramInt)
  {
    Trace.beginSection("KeyGuardUpdateMonitor#handlerFingerPrintAuthenticated");
    try
    {
      int i = getCurrentUser();
      if (i != paramInt)
      {
        Log.d("KeyguardUpdateMonitor", "Fingerprint authenticated for wrong user: " + paramInt);
        handleFingerprintAuthFailed();
        return;
      }
      if (isFingerprintDisabled(i))
      {
        Log.d("KeyguardUpdateMonitor", "Fingerprint disabled by DPM for userId: " + i);
        handleFingerprintAuthFailed();
        return;
      }
      onFingerprintAuthenticated(i);
      setFingerprintRunningState(0);
      Trace.endSection();
      return;
    }
    finally
    {
      setFingerprintRunningState(0);
    }
  }
  
  private void handleFingerprintError(int paramInt, String paramString)
  {
    if (paramInt == 7) {
      this.mLockoutState = true;
    }
    Object localObject = getAuthenticatedPackage();
    Log.d("KeyguardUpdateMonitor", "handle fp Error: msgId = " + paramInt + ", errString = " + paramString + ", state = " + this.mFingerprintRunningState + ", lockout = " + this.mLockoutState + ", auth=" + (String)localObject);
    if ((paramInt == 5) && (this.mFingerprintRunningState == 3))
    {
      setFingerprintRunningState(0);
      startListeningForFingerprint();
    }
    for (;;)
    {
      int i = 0;
      while (i < this.mCallbacks.size())
      {
        localObject = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
        if (localObject != null) {
          ((KeyguardUpdateMonitorCallback)localObject).onFingerprintError(paramInt, paramString);
        }
        i += 1;
      }
      if ((this.mFingerprintRunningState != 1) || (!"com.android.systemui".equals(localObject)) || (this.mLockoutState)) {
        setFingerprintRunningState(0);
      } else {
        Log.d("KeyguardUpdateMonitor", "not handle error when authenticated");
      }
    }
  }
  
  private void handleFingerprintHelp(int paramInt, String paramString)
  {
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onFingerprintHelp(paramInt, paramString);
      }
      i += 1;
    }
  }
  
  private void handleFingerprintLockoutReset()
  {
    updateFingerprintListeningState();
  }
  
  private void handleFingerprintTimeout()
  {
    Log.d("KeyguardUpdateMonitor", "handleFingerprintTimeout");
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onFingerprintTimeout();
      }
      i += 1;
    }
  }
  
  private void handleKeyguardBouncerChanged(int paramInt)
  {
    boolean bool = true;
    if (paramInt == 1) {}
    for (;;)
    {
      this.mBouncer = bool;
      paramInt = 0;
      while (paramInt < this.mCallbacks.size())
      {
        KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(paramInt)).get();
        if (localKeyguardUpdateMonitorCallback != null) {
          localKeyguardUpdateMonitorCallback.onKeyguardBouncerChanged(bool);
        }
        paramInt += 1;
      }
      bool = false;
    }
    updateFingerprintListeningState();
  }
  
  private void handleKeyguardReset()
  {
    updateFingerprintListeningState();
    this.mNeedsSlowUnlockTransition = resolveNeedsSlowUnlockTransition();
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onKeyguardReset();
      }
      i += 1;
    }
  }
  
  private void handleLidSwitchChanged(boolean paramBoolean)
  {
    if (paramBoolean != this.mLidOpen) {
      updateFPStateBySensor(this.mProximity, paramBoolean);
    }
  }
  
  private void handleLocaleChanged()
  {
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onRefreshCarrierInfo();
      }
      i += 1;
    }
  }
  
  private void handlePendingSubInfoChange(int paramInt)
  {
    if (paramInt == 0) {
      this.mSimUnlockSlot0 = false;
    }
    while ((this.mSimUnlockSlot0) || (this.mSimUnlockSlot1))
    {
      return;
      if (paramInt == 1) {
        this.mSimUnlockSlot1 = false;
      }
    }
    if (this.mPendingSubInfoChange)
    {
      Log.d("KeyguardUpdateMonitor", "handle pending subinfo change");
      handleSimSubscriptionInfoChanged();
    }
    this.mPendingSubInfoChange = false;
  }
  
  private void handleReportEmergencyCallAction()
  {
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onEmergencyCallAction();
      }
      i += 1;
    }
  }
  
  private void handleScreenTurnedOff()
  {
    int j = this.mCallbacks.size();
    int i = 0;
    while (i < j)
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onScreenTurnedOff();
      }
      i += 1;
    }
    setProximitySensorEnabled(true);
    onScreenStatusChanged(false);
  }
  
  private void handleScreenTurnedOn()
  {
    int j = this.mCallbacks.size();
    int i = 0;
    while (i < j)
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onScreenTurnedOn();
      }
      i += 1;
    }
    setProximitySensorEnabled(false);
    onScreenStatusChanged(true);
  }
  
  private void handleServiceStateChange(int paramInt1, int paramInt2, ServiceState paramServiceState)
  {
    this.mServiceStates.put(Integer.valueOf(paramInt1), paramServiceState);
    paramInt1 = 0;
    while (paramInt1 < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(paramInt1)).get();
      if (localKeyguardUpdateMonitorCallback != null)
      {
        localKeyguardUpdateMonitorCallback.onRefreshCarrierInfo();
        localKeyguardUpdateMonitorCallback.onServiceStateChanged(paramInt2, paramServiceState);
      }
      paramInt1 += 1;
    }
  }
  
  private void handleSimStateChange(int paramInt1, int paramInt2, IccCardConstants.State paramState)
  {
    Log.d("KeyguardUpdateMonitor", "handleSimStateChange(subId=" + paramInt1 + ", slotId=" + paramInt2 + ", state=" + paramState + ")");
    if (!SubscriptionManager.isValidSubscriptionId(paramInt1))
    {
      Log.w("KeyguardUpdateMonitor", "invalid subId in handleSimStateChange()");
      return;
    }
    Object localObject = (SimData)this.mSimDatas.get(Integer.valueOf(paramInt1));
    int i;
    if (localObject == null)
    {
      localObject = new SimData(paramState, paramInt2, paramInt1);
      this.mSimDatas.put(Integer.valueOf(paramInt1), localObject);
      i = 1;
      if ((i != 0) && (paramState != IccCardConstants.State.UNKNOWN))
      {
        i = 0;
        while (i < this.mCallbacks.size())
        {
          localObject = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
          if (localObject != null) {
            ((KeyguardUpdateMonitorCallback)localObject).onSimStateChanged(paramInt1, paramInt2, paramState);
          }
          i += 1;
        }
      }
    }
    else
    {
      if ((((SimData)localObject).simState != paramState) || (((SimData)localObject).subId != paramInt1)) {}
      label208:
      for (i = 1;; i = 0)
      {
        ((SimData)localObject).simState = paramState;
        ((SimData)localObject).subId = paramInt1;
        ((SimData)localObject).slotId = paramInt2;
        break;
        if (((SimData)localObject).slotId != paramInt2) {
          break label208;
        }
      }
    }
  }
  
  private void handleTimeUpdate()
  {
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onTimeChanged();
      }
      i += 1;
    }
  }
  
  private void handleUserInfoChanged(int paramInt)
  {
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onUserInfoChanged(paramInt);
      }
      i += 1;
    }
  }
  
  private void handleUserUnlocked()
  {
    this.mNeedsSlowUnlockTransition = resolveNeedsSlowUnlockTransition();
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onUserUnlocked();
      }
      i += 1;
    }
  }
  
  private boolean is17801Device()
  {
    return "17801".equals(SystemProperties.get("ro.boot.project_name"));
  }
  
  private boolean is8998Device()
  {
    if (is17801Device()) {
      return true;
    }
    return ("P6859".equals(Build.DEVICE)) || ("OnePlus5".equals(Build.DEVICE));
  }
  
  private static boolean isBatteryUpdateInteresting(BatteryStatus paramBatteryStatus1, BatteryStatus paramBatteryStatus2)
  {
    boolean bool1 = paramBatteryStatus2.isPluggedIn();
    boolean bool2 = paramBatteryStatus1.isPluggedIn();
    int i;
    if ((bool2) && (bool1)) {
      if (paramBatteryStatus1.status != paramBatteryStatus2.status) {
        i = 1;
      }
    }
    while ((bool2 != bool1) || (i != 0))
    {
      return true;
      i = 0;
      continue;
      i = 0;
    }
    if ((bool1) && (paramBatteryStatus1.level != paramBatteryStatus2.level)) {
      return true;
    }
    if ((!bool1) && (paramBatteryStatus2.isBatteryLow()) && (paramBatteryStatus2.level != paramBatteryStatus1.level)) {
      return true;
    }
    if ((bool1) && (paramBatteryStatus2.maxChargingWattage != paramBatteryStatus1.maxChargingWattage)) {
      return true;
    }
    return (bool1) && (paramBatteryStatus2.fastCharge != paramBatteryStatus1.fastCharge);
  }
  
  private boolean isDeviceProvisionedInSettingsDb()
  {
    boolean bool = false;
    if (Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean isFingerprintDisabled(int paramInt)
  {
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)this.mContext.getSystemService("device_policy");
    if ((localDevicePolicyManager != null) && ((localDevicePolicyManager.getKeyguardDisabledFeatures(null, paramInt) & 0x20) != 0)) {
      return true;
    }
    return isSimPinSecure();
  }
  
  private boolean isSensorNear(int paramInt, boolean paramBoolean)
  {
    return (paramInt == 1) || (!paramBoolean);
  }
  
  public static boolean isSimPinSecure(IccCardConstants.State paramState)
  {
    if ((paramState == IccCardConstants.State.PIN_REQUIRED) || (paramState == IccCardConstants.State.PUK_REQUIRED)) {}
    while (paramState == IccCardConstants.State.PERM_DISABLED) {
      return true;
    }
    return false;
  }
  
  private boolean isTrustDisabled(int paramInt)
  {
    return isSimPinSecure();
  }
  
  private void notifyFingerprintRunningStateChanged()
  {
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onFingerprintRunningStateChanged(isFingerprintDetectionRunning());
      }
      i += 1;
    }
  }
  
  private void notifyStrongAuthStateChanged(int paramInt)
  {
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onStrongAuthStateChanged(paramInt);
      }
      i += 1;
    }
  }
  
  private void onFingerprintAuthenticated(int paramInt)
  {
    Trace.beginSection("KeyGuardUpdateMonitor#onFingerPrintAuthenticated");
    this.mUserFingerprintAuthenticated.put(paramInt, true);
    this.mFingerprintAlreadyAuthenticated = isUnlockingWithFingerprintAllowed();
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onFingerprintAuthenticated(paramInt);
      }
      i += 1;
    }
    Trace.endSection();
  }
  
  private void onScreenStatusChanged(boolean paramBoolean)
  {
    if (this.mClockCtrl != null)
    {
      if (paramBoolean) {
        this.mClockCtrl.onScreenTurnedOn();
      }
    }
    else {
      return;
    }
    this.mClockCtrl.onScreenTurnedOff();
  }
  
  private boolean refreshSimState(int paramInt1, int paramInt2)
  {
    int i = TelephonyManager.from(this.mContext).getSimState(paramInt2);
    SimData localSimData;
    Object localObject2;
    try
    {
      Object localObject1 = IccCardConstants.State.intToState(i);
      localSimData = (SimData)this.mSimDatas.get(Integer.valueOf(paramInt1));
      if (localSimData == null)
      {
        localObject1 = new SimData((IccCardConstants.State)localObject1, paramInt2, paramInt1);
        this.mSimDatas.put(Integer.valueOf(paramInt1), localObject1);
        bool = true;
        if (bool) {
          Log.d("KeyguardUpdateMonitor", "refreshSimState , data = " + localObject1);
        }
        return bool;
      }
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;)
      {
        Log.w("KeyguardUpdateMonitor", "Unknown sim state: " + i);
        localObject2 = IccCardConstants.State.UNKNOWN;
      }
    }
    if (localSimData.simState != localObject2) {}
    for (boolean bool = true;; bool = false)
    {
      localSimData.simState = ((IccCardConstants.State)localObject2);
      localObject2 = localSimData;
      break;
    }
  }
  
  private boolean resolveNeedsSlowUnlockTransition()
  {
    boolean bool = this.mUserManager.isUserUnlocked(getCurrentUser());
    setUserUnlocked(bool);
    if (bool) {
      return false;
    }
    Object localObject = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME");
    localObject = this.mContext.getPackageManager().resolveActivity((Intent)localObject, 0);
    return FALLBACK_HOME_COMPONENT.equals(((ResolveInfo)localObject).getComponentInfo().getComponentName());
  }
  
  private void scheduleStrongAuthTimeout()
  {
    long l1 = ((DevicePolicyManager)this.mContext.getSystemService("device_policy")).getRequiredStrongAuthTimeout(null, sCurrentUser);
    Log.d("KeyguardUpdateMonitor", "schedule strong auth: " + l1);
    long l2 = SystemClock.elapsedRealtime();
    Object localObject = new Intent("com.android.systemui.ACTION_STRONG_AUTH_TIMEOUT");
    ((Intent)localObject).putExtra("com.android.systemui.USER_ID", sCurrentUser);
    localObject = PendingIntent.getBroadcast(this.mContext, sCurrentUser, (Intent)localObject, 268435456);
    this.mAlarmManager.set(3, l2 + l1, (PendingIntent)localObject);
    notifyStrongAuthStateChanged(sCurrentUser);
  }
  
  private void sendUpdates(KeyguardUpdateMonitorCallback paramKeyguardUpdateMonitorCallback)
  {
    paramKeyguardUpdateMonitorCallback.onRefreshBatteryInfo(this.mBatteryStatus);
    paramKeyguardUpdateMonitorCallback.onTimeChanged();
    paramKeyguardUpdateMonitorCallback.onRingerModeChanged(this.mRingMode);
    paramKeyguardUpdateMonitorCallback.onPhoneStateChanged(this.mPhoneState);
    paramKeyguardUpdateMonitorCallback.onRefreshCarrierInfo();
    paramKeyguardUpdateMonitorCallback.onClockVisibilityChanged();
    Iterator localIterator = this.mSimDatas.entrySet().iterator();
    while (localIterator.hasNext())
    {
      SimData localSimData = (SimData)((Map.Entry)localIterator.next()).getValue();
      paramKeyguardUpdateMonitorCallback.onSimStateChanged(localSimData.subId, localSimData.slotId, localSimData.simState);
    }
  }
  
  public static void setCurrentUser(int paramInt)
  {
    try
    {
      sCurrentUser = paramInt;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private void setFingerprintRunningState(int paramInt)
  {
    int i;
    if (this.mFingerprintRunningState == 1)
    {
      i = 1;
      if (paramInt != 1) {
        break label72;
      }
    }
    label72:
    for (int j = 1;; j = 0)
    {
      if (this.mFingerprintRunningState != paramInt) {
        Log.d("KeyguardUpdateMonitor", "change fp running state to " + paramInt);
      }
      this.mFingerprintRunningState = paramInt;
      if (i != j) {
        notifyFingerprintRunningStateChanged();
      }
      return;
      i = 0;
      break;
    }
  }
  
  private void setProximitySensorEnabled(boolean paramBoolean)
  {
    if (!isPreventModeEnabled(this.mContext)) {
      return;
    }
    int i = getCurrentUser();
    boolean bool;
    if ((this.mFpm != null) && (this.mFpm.isHardwareDetected()) && (this.mFpm.getEnrolledFingerprints(i).size() > 0))
    {
      bool = true;
      Log.d("KeyguardUpdateMonitor", "listen pocket-sensor: " + paramBoolean + ", current=" + this.mProximitySensorEnabled + ", FP enabled=" + bool);
      if ((!bool) || (!paramBoolean)) {
        break label144;
      }
      if (!this.mProximitySensorEnabled)
      {
        this.mProximitySensorEnabled = true;
        this.mSensorManager.registerListener(this.mProximityListener, this.mProximitySensor, 3);
      }
    }
    label144:
    while (!this.mProximitySensorEnabled)
    {
      return;
      bool = false;
      break;
    }
    this.mProximitySensorEnabled = false;
    if (isSensorNear(this.mProximity, this.mLidOpen))
    {
      this.mProximity = 0;
      this.mLidOpen = true;
      updateFingerprintListeningState();
    }
    this.mProximity = 0;
    this.mLidOpen = true;
    if (this.mFpm != null) {
      this.mFpm.updateStatus(0);
    }
    this.mSensorManager.unregisterListener(this.mProximityListener);
  }
  
  private boolean shouldListenForFingerprint()
  {
    Log.d("KeyguardUpdateMonitor", "shouldListen: isVisible= " + this.mKeyguardIsVisible + ", interactive= " + this.mDeviceInteractive + ", mBouncer= " + this.mBouncer + ", mIsKeyguardDone = " + this.mIsKeyguardDone + ", goingToSleep= " + this.mGoingToSleep + ", switchingUser= " + this.mSwitchingUser + ", alreadyAuthenticated= " + this.mFingerprintAlreadyAuthenticated + ", isDisabled= " + isFingerprintDisabled(getCurrentUser()) + ", camera= " + this.mLaunchingCamera + ", mProximity= " + this.mProximity + ", mLidOpen= " + this.mLidOpen + ", " + this.mProximitySensorEnabled);
    if ((!this.mKeyguardIsVisible) || (this.mGoingToSleep)) {
      this.mLaunchingCamera = false;
    }
    if (((!this.mKeyguardIsVisible) && (this.mDeviceInteractive) && ((!this.mBouncer) || (this.mIsKeyguardDone)) && (!this.mGoingToSleep)) || (this.mSwitchingUser)) {}
    while ((this.mFingerprintAlreadyAuthenticated) || ((isSensorNear(this.mProximity, this.mLidOpen)) && (this.mProximitySensorEnabled)) || (isFingerprintDisabled(getCurrentUser())) || (this.mLaunchingCamera)) {
      return false;
    }
    return true;
  }
  
  private void startClockCtrl()
  {
    if (this.mClockCtrl != null) {
      this.mClockCtrl.onStartCtrl(this.mTimeTickListener, this.mContext);
    }
  }
  
  private void startListeningForFingerprint()
  {
    if (this.mFingerprintRunningState == 2)
    {
      setFingerprintRunningState(3);
      return;
    }
    int i = getCurrentUser();
    Log.v("KeyguardUpdateMonitor", "startListeningFP , enabled=" + isUnlockWithFingerprintPossible(i));
    if (isUnlockWithFingerprintPossible(i))
    {
      if (this.mFingerprintCancelSignal != null) {
        this.mFingerprintCancelSignal.cancel();
      }
      this.mFingerprintCancelSignal = new CancellationSignal();
      this.mFpm.authenticate(null, this.mFingerprintCancelSignal, 0, this.mAuthenticationCallback, null, i);
      setFingerprintRunningState(1);
    }
  }
  
  private void stopListeningForFingerprint()
  {
    Log.v("KeyguardUpdateMonitor", "stopListeningFP , " + this.mFingerprintRunningState);
    if (this.mFingerprintRunningState == 1)
    {
      this.mFingerprintCancelSignal.cancel();
      this.mFingerprintCancelSignal = null;
      setFingerprintRunningState(2);
    }
    if (this.mFingerprintRunningState == 3) {
      setFingerprintRunningState(2);
    }
  }
  
  private void updateFPStateBySensor(int paramInt, boolean paramBoolean)
  {
    int j = this.mProximity;
    boolean bool = this.mLidOpen;
    this.mProximity = paramInt;
    this.mLidOpen = paramBoolean;
    int i = paramInt;
    if (!paramBoolean) {
      i = 1;
    }
    if (this.mFpm != null) {
      this.mFpm.updateStatus(i);
    }
    if ((this.mFingerprintRunningState == 1) && (isSensorNear(paramInt, paramBoolean))) {
      updateFingerprintListeningState();
    }
    while ((!isSensorNear(j, bool)) || (isSensorNear(paramInt, paramBoolean))) {
      return;
    }
    updateFingerprintListeningState();
  }
  
  private void updateFacelockSettings()
  {
    boolean bool2 = true;
    if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "oneplus_face_unlock_enable", 0, 0) == 1)
    {
      bool1 = true;
      this.mFacelockEnabled = bool1;
      if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "oneplus_auto_face_unlock_enable", 0, 0) != 1) {
        break label86;
      }
      bool1 = true;
      label49:
      this.mAutoFacelockEnabled = bool1;
      if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "oneplus_face_unlock_assistive_lighting_enable", 0, 0) != 1) {
        break label91;
      }
    }
    label86:
    label91:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.mFacelockLightingEnabled = bool1;
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label49;
    }
  }
  
  private void updateFacelockTrustState(boolean paramBoolean)
  {
    this.mSkipBouncerByFacelock = paramBoolean;
    Log.d("KeyguardUpdateMonitor", "FacelockTrust," + paramBoolean);
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onTrustChanged(getCurrentUser());
      }
      i += 1;
    }
  }
  
  private void watchForDeviceProvisioning()
  {
    this.mDeviceProvisionedObserver = new ContentObserver(this.mHandler)
    {
      public void onChange(boolean paramAnonymousBoolean)
      {
        super.onChange(paramAnonymousBoolean);
        KeyguardUpdateMonitor.-set0(KeyguardUpdateMonitor.this, KeyguardUpdateMonitor.-wrap0(KeyguardUpdateMonitor.this));
        if (KeyguardUpdateMonitor.-get2(KeyguardUpdateMonitor.this)) {
          KeyguardUpdateMonitor.-get7(KeyguardUpdateMonitor.this).sendEmptyMessage(308);
        }
      }
    };
    this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("device_provisioned"), false, this.mDeviceProvisionedObserver);
    boolean bool = isDeviceProvisionedInSettingsDb();
    if (bool != this.mDeviceProvisioned)
    {
      this.mDeviceProvisioned = bool;
      if (this.mDeviceProvisioned) {
        this.mHandler.sendEmptyMessage(308);
      }
    }
  }
  
  private void watchForFacelockSettings()
  {
    this.mFacelockSettingsObserver = new ContentObserver(this.mHandler)
    {
      public void onChange(boolean paramAnonymousBoolean)
      {
        super.onChange(paramAnonymousBoolean);
        KeyguardUpdateMonitor.-wrap28(KeyguardUpdateMonitor.this);
        Log.d("KeyguardUpdateMonitor", "facelock state = " + KeyguardUpdateMonitor.-get4(KeyguardUpdateMonitor.this) + ", " + KeyguardUpdateMonitor.-get0(KeyguardUpdateMonitor.this) + ", " + KeyguardUpdateMonitor.-get5(KeyguardUpdateMonitor.this));
      }
    };
    this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("oneplus_face_unlock_enable"), false, this.mFacelockSettingsObserver, 0);
    this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("oneplus_auto_face_unlock_enable"), false, this.mFacelockSettingsObserver, 0);
    this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("oneplus_face_unlock_assistive_lighting_enable"), false, this.mFacelockSettingsObserver, 0);
    updateFacelockSettings();
  }
  
  public void clearFailedUnlockAttempts()
  {
    this.mFailedAttempts.delete(sCurrentUser);
    clearFingerprintFailedUnlockAttempts();
    clearFailedFacelockAttempts();
  }
  
  public void clearFingerprintRecognized()
  {
    this.mUserFingerprintAuthenticated.clear();
  }
  
  public void dispatchBootCompleted()
  {
    this.mHandler.sendEmptyMessage(313);
  }
  
  public void dispatchDreamingStarted()
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(333, 1, 0));
  }
  
  public void dispatchDreamingStopped()
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(333, 0, 0));
  }
  
  public void dispatchFinishedGoingToSleep(int paramInt)
  {
    try
    {
      this.mDeviceInteractive = false;
      this.mHandler.sendMessage(this.mHandler.obtainMessage(320, paramInt, 0));
      return;
    }
    finally {}
  }
  
  public void dispatchScreenTurnedOff()
  {
    try
    {
      this.mScreenOn = false;
      this.mHandler.sendEmptyMessage(332);
      return;
    }
    finally {}
  }
  
  public void dispatchScreenTurnedOn()
  {
    try
    {
      this.mScreenOn = true;
      this.mHandler.sendEmptyMessage(331);
      return;
    }
    finally {}
  }
  
  public void dispatchStartedGoingToSleep(int paramInt)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(321, paramInt, 0));
  }
  
  public void dispatchStartedWakingUp()
  {
    try
    {
      this.mDeviceInteractive = true;
      this.mHandler.sendEmptyMessage(319);
      return;
    }
    finally {}
  }
  
  public void dispatchSystemReady()
  {
    this.mHandler.sendEmptyMessage(700);
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("KeyguardUpdateMonitor state:");
    paramPrintWriter.println("  SIM States:");
    paramFileDescriptor = this.mSimDatas.values().iterator();
    while (paramFileDescriptor.hasNext())
    {
      paramArrayOfString = (SimData)paramFileDescriptor.next();
      paramPrintWriter.println("    " + paramArrayOfString.toString());
    }
    paramPrintWriter.println("  Subs:");
    if (this.mSubscriptionInfo != null)
    {
      i = 0;
      while (i < this.mSubscriptionInfo.size())
      {
        paramPrintWriter.println("    " + this.mSubscriptionInfo.get(i));
        i += 1;
      }
    }
    paramPrintWriter.println("  Service states:");
    paramFileDescriptor = this.mServiceStates.keySet().iterator();
    while (paramFileDescriptor.hasNext())
    {
      i = ((Integer)paramFileDescriptor.next()).intValue();
      paramPrintWriter.println("    " + i + "=" + this.mServiceStates.get(Integer.valueOf(i)));
    }
    int i = ActivityManager.getCurrentUser();
    if ((this.mFpm != null) && (this.mFpm.isHardwareDetected()))
    {
      int j = this.mStrongAuthTracker.getStrongAuthForUser(i);
      paramPrintWriter.println("  Fingerprint state (user=" + i + ")");
      paramPrintWriter.println("    allowed=" + isUnlockingWithFingerprintAllowed());
      paramPrintWriter.println("    auth'd=" + this.mUserFingerprintAuthenticated.get(i));
      paramPrintWriter.println("    authSinceBoot=" + getStrongAuthTracker().hasUserAuthenticatedSinceBoot());
      paramPrintWriter.println("    disabled(DPM)=" + isFingerprintDisabled(i));
      paramPrintWriter.println("    possible=" + isUnlockWithFingerprintPossible(i));
      paramPrintWriter.println("    strongAuthFlags=" + Integer.toHexString(j));
      paramPrintWriter.println("    timedout=" + hasFingerprintUnlockTimedOut(i));
      paramPrintWriter.println("    trustManaged=" + getUserTrustIsManaged(i));
      paramPrintWriter.println("    FingerprintFailedAttempts=" + getFingerprintFailedUnlockAttempts());
      paramPrintWriter.println("    mProximitySensorEnabled=" + this.mProximitySensorEnabled);
      paramPrintWriter.println("    mProximity=" + this.mProximity);
      paramPrintWriter.println("    mLaunchingCamera=" + this.mLaunchingCamera);
      paramPrintWriter.println("    mDuringAcquired=" + this.mDuringAcquired);
      paramPrintWriter.println("    mLockoutState=" + this.mLockoutState);
      paramPrintWriter.println("    mFingerprintRunningState=" + this.mFingerprintRunningState);
      paramPrintWriter.println("    mFingerprintAlreadyAuthenticated=" + this.mFingerprintAlreadyAuthenticated);
      paramPrintWriter.println("    EnrollSize=" + this.mFpm.getEnrolledFingerprints(i).size());
    }
    paramPrintWriter.println("    mBatteryStatus=" + this.mBatteryStatus.status + ", level=" + this.mBatteryStatus.level + ", plugged=" + this.mBatteryStatus.plugged + ", health=" + this.mBatteryStatus.health + ", fastCharge=" + this.mBatteryStatus.fastCharge + ", health=" + this.mBatteryStatus.health + ", maxChargingWattage=" + this.mBatteryStatus.maxChargingWattage);
    paramPrintWriter.println("    mKeyguardIsVisible=" + this.mKeyguardIsVisible);
    paramPrintWriter.println("    mBootCompleted=" + this.mBootCompleted);
    paramPrintWriter.println("    mGoingToSleep=" + this.mGoingToSleep);
    paramPrintWriter.println("    isPreventModeEnabled=" + isPreventModeEnabled(this.mContext));
    paramPrintWriter.println("    mDeviceProvisioned=" + this.mDeviceProvisioned);
    paramPrintWriter.println("    getFailedUnlockAttempts=" + getFailedUnlockAttempts(i));
    paramPrintWriter.println("    getUserCanSkipBouncer=" + getUserCanSkipBouncer(i));
    paramPrintWriter.println("    mDeviceInteractive=" + this.mDeviceInteractive);
    paramPrintWriter.println("    mScreenOn=" + this.mScreenOn);
    paramPrintWriter.println("    mSimUnlockSlot0=" + this.mSimUnlockSlot0);
    paramPrintWriter.println("    mSimUnlockSlot1=" + this.mSimUnlockSlot1);
    paramPrintWriter.println("    mPendingSubInfoChange=" + this.mPendingSubInfoChange);
    paramPrintWriter.println("    mIsUserUnlocked=" + isUserUnlocked());
    paramPrintWriter.println("    mIsFaceAdded=" + this.mIsFaceAdded);
    paramPrintWriter.println("    mFacelockRunningType=" + this.mFacelockRunningType);
    paramPrintWriter.println("    isSecure=" + this.mLockPatternUtils.isSecure(getCurrentUser()));
    paramPrintWriter.println("    getCurrentUser=" + getCurrentUser());
    paramPrintWriter.println("    is17801Device=" + is17801Device());
    paramPrintWriter.println("    mSkipBouncerByFacelock=" + this.mSkipBouncerByFacelock);
    paramPrintWriter.println("    mFacelockUnlocking=" + this.mFacelockUnlocking);
  }
  
  public int getFacelockNotifyMsgId(int paramInt)
  {
    switch (paramInt)
    {
    case 2: 
    case 3: 
    case 4: 
    default: 
      return 0;
    case 1: 
      return R.string.face_unlock_timeout;
    case 5: 
      return R.string.face_unlock_tap_to_retry;
    case 6: 
      return R.string.face_unlock_no_face;
    case 7: 
      return R.string.face_unlock_fail;
    case 8: 
      return R.string.face_unlock_camera_error;
    }
    return R.string.face_unlock_no_permission;
  }
  
  public int getFacelockRunningType()
  {
    return this.mFacelockRunningType;
  }
  
  public int getFailedUnlockAttempts(int paramInt)
  {
    return this.mFailedAttempts.get(paramInt, 0);
  }
  
  public int getFingerprintFailedUnlockAttempts()
  {
    return this.mFingerprintFailedAttempts.get(sCurrentUser, 0);
  }
  
  public int getNextSubIdForState(IccCardConstants.State paramState)
  {
    List localList = getSubscriptionInfo(false);
    int j = -1;
    int m = Integer.MAX_VALUE;
    int i = 0;
    while (i < localList.size())
    {
      int i2 = ((SubscriptionInfo)localList.get(i)).getSubscriptionId();
      int i1 = SubscriptionManager.getSlotId(i2);
      int n = m;
      int k = j;
      if (paramState == getSimState(i2))
      {
        n = m;
        k = j;
        if (m > i1)
        {
          k = i2;
          n = i1;
        }
      }
      i += 1;
      m = n;
      j = k;
    }
    return j;
  }
  
  public int getPresentSubId()
  {
    int i = 0;
    while (i < TelephonyManager.getDefault().getPhoneCount())
    {
      Object localObject = this.mSubscriptionManager;
      localObject = SubscriptionManager.getSubId(i);
      if ((localObject != null) && (localObject.length > 0) && (getSimState(localObject[0]) != IccCardConstants.State.ABSENT) && (getSimState(localObject[0]) != IccCardConstants.State.UNKNOWN))
      {
        if (Utils.DEBUG_ONEPLUS) {
          Log.i("KeyguardUpdateMonitor", "getPresentSubId slotId:" + i + " subId:" + localObject[0] + " getSimState:" + getSimState(localObject[0]));
        }
        return localObject[0];
      }
      i += 1;
    }
    return -1;
  }
  
  public int getSimSlotId(int paramInt)
  {
    if (this.mSimDatas.containsKey(Integer.valueOf(paramInt))) {
      return ((SimData)this.mSimDatas.get(Integer.valueOf(paramInt))).slotId;
    }
    Log.w("KeyguardUpdateMonitor", "invalid subid not in keyguard");
    return -1;
  }
  
  public IccCardConstants.State getSimState(int paramInt)
  {
    if (this.mSimDatas.containsKey(Integer.valueOf(paramInt))) {
      return ((SimData)this.mSimDatas.get(Integer.valueOf(paramInt))).simState;
    }
    return IccCardConstants.State.UNKNOWN;
  }
  
  public StrongAuthTracker getStrongAuthTracker()
  {
    return this.mStrongAuthTracker;
  }
  
  public List<SubscriptionInfo> getSubscriptionInfo(boolean paramBoolean)
  {
    List localList = this.mSubscriptionInfo;
    if ((localList == null) || (paramBoolean)) {
      localList = this.mSubscriptionManager.getActiveSubscriptionInfoList();
    }
    if (localList == null) {}
    for (this.mSubscriptionInfo = new ArrayList();; this.mSubscriptionInfo = localList) {
      return this.mSubscriptionInfo;
    }
  }
  
  public SubscriptionInfo getSubscriptionInfoForSubId(int paramInt)
  {
    List localList = getSubscriptionInfo(false);
    int i = 0;
    while (i < localList.size())
    {
      SubscriptionInfo localSubscriptionInfo = (SubscriptionInfo)localList.get(i);
      if (paramInt == localSubscriptionInfo.getSubscriptionId()) {
        return localSubscriptionInfo;
      }
      i += 1;
    }
    return null;
  }
  
  public boolean getUserCanSkipBouncer(int paramInt)
  {
    if (!getUserHasTrust(paramInt))
    {
      if (this.mUserFingerprintAuthenticated.get(paramInt)) {
        return isUnlockingWithFingerprintAllowed();
      }
    }
    else {
      return true;
    }
    return false;
  }
  
  public boolean getUserHasTrust(int paramInt)
  {
    if ((isTrustDisabled(paramInt)) || (!this.mUserHasTrust.get(paramInt))) {
      return canSkipBouncerByFacelock();
    }
    return true;
  }
  
  public boolean getUserTrustIsManaged(int paramInt)
  {
    if ((!this.mUserTrustIsManaged.get(paramInt)) || (isTrustDisabled(paramInt))) {
      return canSkipBouncerByFacelock();
    }
    return true;
  }
  
  protected void handleBootCompleted()
  {
    if (this.mBootCompleted) {
      return;
    }
    this.mBootCompleted = true;
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onBootCompleted();
      }
      i += 1;
    }
  }
  
  protected void handleDevicePolicyManagerStateChanged()
  {
    updateFingerprintListeningState();
    int i = this.mCallbacks.size() - 1;
    while (i >= 0)
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onDevicePolicyManagerStateChanged();
      }
      i -= 1;
    }
  }
  
  protected void handleDeviceProvisioned()
  {
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onDeviceProvisioned();
      }
      i += 1;
    }
    if (this.mDeviceProvisionedObserver != null)
    {
      this.mContext.getContentResolver().unregisterContentObserver(this.mDeviceProvisionedObserver);
      this.mDeviceProvisionedObserver = null;
    }
  }
  
  protected void handleFinishedGoingToSleep(int paramInt)
  {
    this.mGoingToSleep = false;
    int j = this.mCallbacks.size();
    int i = 0;
    while (i < j)
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onFinishedGoingToSleep(paramInt);
      }
      i += 1;
    }
    updateFingerprintListeningState();
  }
  
  protected void handlePhoneStateChanged(String paramString)
  {
    if (TelephonyManager.EXTRA_STATE_IDLE.equals(paramString)) {
      this.mPhoneState = 0;
    }
    for (;;)
    {
      int i = 0;
      while (i < this.mCallbacks.size())
      {
        paramString = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
        if (paramString != null) {
          paramString.onPhoneStateChanged(this.mPhoneState);
        }
        i += 1;
      }
      if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(paramString)) {
        this.mPhoneState = 2;
      } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(paramString)) {
        this.mPhoneState = 1;
      }
    }
  }
  
  protected void handleRingerModeChange(int paramInt)
  {
    this.mRingMode = paramInt;
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onRingerModeChanged(paramInt);
      }
      i += 1;
    }
  }
  
  protected void handleSimSubscriptionInfoChanged()
  {
    if (Utils.DEBUG_ONEPLUS)
    {
      Log.v("KeyguardUpdateMonitor", "onSubscriptionInfoChanged()");
      localObject1 = this.mSubscriptionManager.getActiveSubscriptionInfoList();
      if (localObject1 != null)
      {
        localObject1 = ((Iterable)localObject1).iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (SubscriptionInfo)((Iterator)localObject1).next();
          Log.v("KeyguardUpdateMonitor", "SubInfo:" + localObject2);
        }
      }
      Log.v("KeyguardUpdateMonitor", "onSubscriptionInfoChanged: list is null");
    }
    Object localObject2 = getSubscriptionInfo(true);
    Object localObject1 = new ArrayList();
    int i = 0;
    Object localObject3;
    while (i < ((List)localObject2).size())
    {
      localObject3 = (SubscriptionInfo)((List)localObject2).get(i);
      if (refreshSimState(((SubscriptionInfo)localObject3).getSubscriptionId(), ((SubscriptionInfo)localObject3).getSimSlotIndex())) {
        ((ArrayList)localObject1).add(localObject3);
      }
      i += 1;
    }
    i = 0;
    while (i < ((ArrayList)localObject1).size())
    {
      localObject2 = (SimData)this.mSimDatas.get(Integer.valueOf(((SubscriptionInfo)((ArrayList)localObject1).get(i)).getSubscriptionId()));
      int j = 0;
      while (j < this.mCallbacks.size())
      {
        localObject3 = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(j)).get();
        if (localObject3 != null) {
          ((KeyguardUpdateMonitorCallback)localObject3).onSimStateChanged(((SimData)localObject2).subId, ((SimData)localObject2).slotId, ((SimData)localObject2).simState);
        }
        j += 1;
      }
      i += 1;
    }
    i = 0;
    while (i < this.mCallbacks.size())
    {
      localObject1 = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localObject1 != null) {
        ((KeyguardUpdateMonitorCallback)localObject1).onRefreshCarrierInfo();
      }
      i += 1;
    }
  }
  
  protected void handleStartedGoingToSleep(int paramInt)
  {
    clearFingerprintRecognized();
    int j = this.mCallbacks.size();
    int i = 0;
    while (i < j)
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onStartedGoingToSleep(paramInt);
      }
      i += 1;
    }
    this.mGoingToSleep = true;
    this.mFingerprintAlreadyAuthenticated = false;
    updateFingerprintListeningState();
    onScreenStatusChanged(false);
  }
  
  protected void handleStartedWakingUp()
  {
    Trace.beginSection("KeyguardUpdateMonitor#handleStartedWakingUp");
    updateFingerprintListeningState();
    int j = this.mCallbacks.size();
    int i = 0;
    while (i < j)
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onStartedWakingUp();
      }
      i += 1;
    }
    onScreenStatusChanged(true);
    Trace.endSection();
  }
  
  public void handleSystemReady()
  {
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onSystemReady();
      }
      i += 1;
    }
  }
  
  protected void handleUserSwitchComplete(int paramInt)
  {
    this.mSwitchingUser = false;
    updateFingerprintListeningState();
    setUserUnlocked(this.mUserManager.isUserUnlocked(getCurrentUser()));
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onUserSwitchComplete(paramInt);
      }
      i += 1;
    }
  }
  
  protected void handleUserSwitching(int paramInt, IRemoteCallback paramIRemoteCallback)
  {
    this.mSwitchingUser = true;
    updateFingerprintListeningState();
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onUserSwitching(paramInt);
      }
      i += 1;
    }
    try
    {
      paramIRemoteCallback.sendResult(null);
      return;
    }
    catch (RemoteException paramIRemoteCallback) {}
  }
  
  public boolean hasBootCompleted()
  {
    return this.mBootCompleted;
  }
  
  public boolean hasFingerprintUnlockTimedOut(int paramInt)
  {
    return !this.mStrongAuthNotTimedOut.contains(Integer.valueOf(paramInt));
  }
  
  public boolean hasLockscreenWallpaper()
  {
    return this.mHasLockscreenWallpaper;
  }
  
  public boolean isAutoFacelockEnabled()
  {
    return this.mAutoFacelockEnabled;
  }
  
  public boolean isDeviceInteractive()
  {
    return this.mDeviceInteractive;
  }
  
  public boolean isDeviceProvisioned()
  {
    return this.mDeviceProvisioned;
  }
  
  public boolean isDreaming()
  {
    return this.mIsDreaming;
  }
  
  public boolean isEmergencyOnly()
  {
    boolean bool1 = false;
    int i = 0;
    while (i < TelephonyManager.getDefault().getPhoneCount())
    {
      Object localObject2 = null;
      Object localObject1 = this.mSubscriptionManager;
      int[] arrayOfInt = SubscriptionManager.getSubId(i);
      localObject1 = localObject2;
      if (arrayOfInt != null)
      {
        localObject1 = localObject2;
        if (arrayOfInt.length > 0) {
          localObject1 = (ServiceState)this.mServiceStates.get(Integer.valueOf(arrayOfInt[0]));
        }
      }
      boolean bool2 = bool1;
      if (localObject1 != null)
      {
        if (((ServiceState)localObject1).getVoiceRegState() == 0) {
          return false;
        }
        bool2 = bool1;
        if (((ServiceState)localObject1).isEmergencyOnly()) {
          bool2 = true;
        }
      }
      i += 1;
      bool1 = bool2;
    }
    return bool1;
  }
  
  public boolean isFaceAdded()
  {
    return this.mIsFaceAdded;
  }
  
  public boolean isFaceUnlockRunning(int paramInt)
  {
    return this.mUserFaceUnlockRunning.get(paramInt);
  }
  
  public boolean isFacelockAllowed()
  {
    Log.d("KeyguardUpdateMonitor", "isFacelockAllowed, visible:" + this.mKeyguardIsVisible + ", interActive:" + this.mDeviceInteractive + ", switching:" + this.mSwitchingUser + ", faceEnabled:" + isFacelockEnabled() + ", faceAdded:" + this.mIsFaceAdded + ", simpin:" + isSimPinSecure() + ", user:" + getCurrentUser() + ", fp authenticated:" + this.mFingerprintAlreadyAuthenticated + ", screenon:" + this.mScreenOn);
    if ((this.mKeyguardIsVisible) && (this.mDeviceInteractive) && (!this.mSwitchingUser) && ((!this.mFingerprintAlreadyAuthenticated) || (this.mScreenOn)))
    {
      if (isUnlockWithFacelockPossible()) {
        return true;
      }
    }
    else {
      return false;
    }
    return false;
  }
  
  public boolean isFacelockAvailable()
  {
    if ((this.mFacelockRunningType == 5) || (this.mFacelockRunningType == 6)) {}
    while (this.mFacelockRunningType == 7) {
      return true;
    }
    return false;
  }
  
  public boolean isFacelockEnabled()
  {
    return this.mFacelockEnabled;
  }
  
  public boolean isFacelockLightingEnabled()
  {
    return this.mFacelockLightingEnabled;
  }
  
  public boolean isFacelockRecognizing()
  {
    return this.mFacelockRunningType == 3;
  }
  
  public boolean isFacelockUnlocking()
  {
    return this.mFacelockUnlocking;
  }
  
  public boolean isFingerprintAlreadyAuthenticated()
  {
    return this.mFingerprintAlreadyAuthenticated;
  }
  
  public boolean isFingerprintDetectionRunning()
  {
    return this.mFingerprintRunningState == 1;
  }
  
  public boolean isFingerprintLockout()
  {
    return this.mLockoutState;
  }
  
  public boolean isGoingToSleep()
  {
    return this.mGoingToSleep;
  }
  
  public boolean isKeyguardDone()
  {
    return this.mIsKeyguardDone;
  }
  
  public boolean isKeyguardVisible()
  {
    return this.mKeyguardIsVisible;
  }
  
  public boolean isOOS()
  {
    boolean bool2 = true;
    int j = TelephonyManager.getDefault().getPhoneCount();
    int i = 0;
    while (i < j)
    {
      ServiceState localServiceState = (ServiceState)this.mServiceStates.get(Integer.valueOf(i));
      boolean bool1 = bool2;
      if (localServiceState != null)
      {
        if (localServiceState.isEmergencyOnly()) {
          bool2 = false;
        }
        bool1 = bool2;
        if (localServiceState.getVoiceRegState() != 1)
        {
          bool1 = bool2;
          if (localServiceState.getVoiceRegState() != 3) {
            bool1 = false;
          }
        }
      }
      i += 1;
      bool2 = bool1;
    }
    return bool2;
  }
  
  public boolean isPreventModeEnabled(Context paramContext)
  {
    boolean bool = false;
    try
    {
      int i = Settings.System.getInt(paramContext.getContentResolver(), "oem_acc_anti_misoperation_screen");
      if (i != 0) {
        bool = true;
      }
      return bool;
    }
    catch (Settings.SettingNotFoundException paramContext) {}
    return false;
  }
  
  public boolean isScreenOn()
  {
    return this.mScreenOn;
  }
  
  public boolean isSimPinSecure()
  {
    Iterator localIterator = getSubscriptionInfo(false).iterator();
    while (localIterator.hasNext()) {
      if (isSimPinSecure(getSimState(((SubscriptionInfo)localIterator.next()).getSubscriptionId()))) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isSimPinVoiceSecure()
  {
    return isSimPinSecure();
  }
  
  public boolean isUnlockWithFacelockPossible()
  {
    if ((!isFacelockEnabled()) || (!this.mIsFaceAdded) || (!this.mLockPatternUtils.isSecure(getCurrentUser())) || (isSimPinSecure())) {}
    while (getCurrentUser() != 0) {
      return false;
    }
    return true;
  }
  
  public boolean isUnlockWithFingerprintPossible(int paramInt)
  {
    if ((this.mFpm == null) || (!this.mFpm.isHardwareDetected()) || (isFingerprintDisabled(paramInt))) {}
    while (this.mFpm.getEnrolledFingerprints(paramInt).size() <= 0) {
      return false;
    }
    return true;
  }
  
  public boolean isUnlockingWithFingerprintAllowed()
  {
    return (this.mStrongAuthTracker.isUnlockingWithFingerprintAllowed()) && (!hasFingerprintUnlockTimedOut(sCurrentUser));
  }
  
  public boolean isUserUnlocked()
  {
    if ((is8998Device()) && (this.mLockPatternUtils.isSecure(getCurrentUser()))) {
      return this.mIsUserUnlocked;
    }
    return true;
  }
  
  public boolean needsSlowUnlockTransition()
  {
    return this.mNeedsSlowUnlockTransition;
  }
  
  public void notifyFacelockStateChanged(final int paramInt)
  {
    final int i = this.mFacelockRunningType;
    this.mFacelockRunningType = paramInt;
    if (Utils.DEBUG_ONEPLUS) {
      Log.d("KeyguardUpdateMonitor", "notifyFacelockStateChanged, type:" + paramInt);
    }
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        int i = 0;
        while (i < KeyguardUpdateMonitor.-get1(KeyguardUpdateMonitor.this).size())
        {
          KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)KeyguardUpdateMonitor.-get1(KeyguardUpdateMonitor.this).get(i)).get();
          if (localKeyguardUpdateMonitorCallback != null) {
            localKeyguardUpdateMonitorCallback.onFacelockStateChanged(paramInt);
          }
          i += 1;
        }
        if (i == paramInt) {
          return;
        }
        if (paramInt == 2)
        {
          KeyguardUpdateMonitor.-wrap29(KeyguardUpdateMonitor.this, true);
          return;
        }
        KeyguardUpdateMonitor.-wrap29(KeyguardUpdateMonitor.this, false);
      }
    });
  }
  
  public void notifyLidSwitchChanged(boolean paramBoolean)
  {
    int i = 0;
    if (!paramBoolean) {
      i = 1;
    }
    Log.d("KeyguardUpdateMonitor", "LidOpen: " + paramBoolean);
    this.mHandler.removeMessages(702);
    Message localMessage = this.mHandler.obtainMessage(702, i, 0);
    this.mHandler.sendMessage(localMessage);
  }
  
  public void notifyPasswordLockout()
  {
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onPasswordLockout();
      }
      i += 1;
    }
  }
  
  public void onFacelockUnlocking(boolean paramBoolean)
  {
    this.mFacelockUnlocking = paramBoolean;
  }
  
  public void onKeyguardDone(boolean paramBoolean)
  {
    this.mIsKeyguardDone = paramBoolean;
  }
  
  public void onKeyguardVisibilityChanged(boolean paramBoolean)
  {
    this.mKeyguardIsVisible = paramBoolean;
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onKeyguardVisibilityChangedRaw(paramBoolean);
      }
      i += 1;
    }
    if (!paramBoolean) {
      this.mFingerprintAlreadyAuthenticated = false;
    }
    updateFingerprintListeningState();
  }
  
  public void onTrustChanged(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    Log.d("KeyguardUpdateMonitor", "onTrustChanged, " + paramBoolean);
    this.mUserHasTrust.put(paramInt1, paramBoolean);
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null)
      {
        localKeyguardUpdateMonitorCallback.onTrustChanged(paramInt1);
        if ((paramBoolean) && (paramInt2 != 0)) {
          localKeyguardUpdateMonitorCallback.onTrustGrantedWithFlags(paramInt2, paramInt1);
        }
      }
      i += 1;
    }
  }
  
  public void onTrustManagedChanged(boolean paramBoolean, int paramInt)
  {
    this.mUserTrustIsManaged.put(paramInt, paramBoolean);
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
      if (localKeyguardUpdateMonitorCallback != null) {
        localKeyguardUpdateMonitorCallback.onTrustManagedChanged(paramInt);
      }
      i += 1;
    }
  }
  
  public void registerCallback(KeyguardUpdateMonitorCallback paramKeyguardUpdateMonitorCallback)
  {
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      if (((WeakReference)this.mCallbacks.get(i)).get() == paramKeyguardUpdateMonitorCallback) {
        return;
      }
      i += 1;
    }
    this.mCallbacks.add(new WeakReference(paramKeyguardUpdateMonitorCallback));
    removeCallback(null);
    sendUpdates(paramKeyguardUpdateMonitorCallback);
  }
  
  public void removeCallback(KeyguardUpdateMonitorCallback paramKeyguardUpdateMonitorCallback)
  {
    int i = this.mCallbacks.size() - 1;
    while (i >= 0)
    {
      if (((WeakReference)this.mCallbacks.get(i)).get() == paramKeyguardUpdateMonitorCallback) {
        this.mCallbacks.remove(i);
      }
      i -= 1;
    }
  }
  
  public void reportEmergencyCallAction(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      this.mHandler.obtainMessage(318).sendToTarget();
      return;
    }
    handleReportEmergencyCallAction();
  }
  
  public void reportFailedStrongAuthUnlockAttempt(int paramInt)
  {
    this.mFailedAttempts.put(paramInt, getFailedUnlockAttempts(paramInt) + 1);
    paramInt = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "confirm_lock_password_fragment.key_num_wrong_confirm_attempts", 0, sCurrentUser);
    Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "confirm_lock_password_fragment.key_num_wrong_confirm_attempts", paramInt + 1, sCurrentUser);
  }
  
  public void reportSimUnlocked(int paramInt)
  {
    int i = SubscriptionManager.getSlotId(paramInt);
    if (i == 0) {
      this.mSimUnlockSlot0 = true;
    }
    for (;;)
    {
      this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(701, i, 0), 2000L);
      Log.v("KeyguardUpdateMonitor", "reportSimUnlocked(subId=" + paramInt + ", slotId=" + i + ")");
      handleSimStateChange(paramInt, i, IccCardConstants.State.READY);
      return;
      if (i == 1) {
        this.mSimUnlockSlot1 = true;
      }
    }
  }
  
  public void reportSuccessfulStrongAuthUnlockAttempt()
  {
    this.mStrongAuthNotTimedOut.add(Integer.valueOf(sCurrentUser));
    scheduleStrongAuthTimeout();
    if (this.mFpm != null)
    {
      String str = getAuthenticatedPackage();
      if ((this.mLockoutState) && ("com.android.systemui".equals(str))) {
        setFingerprintRunningState(1);
      }
      this.mFpm.resetTimeout(null);
    }
  }
  
  public void sendKeyguardBouncerChanged(boolean paramBoolean)
  {
    Message localMessage = this.mHandler.obtainMessage(322);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localMessage.arg1 = i;
      localMessage.sendToTarget();
      return;
    }
  }
  
  public void sendKeyguardReset()
  {
    this.mHandler.obtainMessage(312).sendToTarget();
  }
  
  public void setHasLockscreenWallpaper(boolean paramBoolean)
  {
    if (paramBoolean != this.mHasLockscreenWallpaper)
    {
      this.mHasLockscreenWallpaper = paramBoolean;
      int i = this.mCallbacks.size() - 1;
      while (i >= 0)
      {
        KeyguardUpdateMonitorCallback localKeyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback)((WeakReference)this.mCallbacks.get(i)).get();
        if (localKeyguardUpdateMonitorCallback != null) {
          localKeyguardUpdateMonitorCallback.onHasLockscreenWallpaperChanged(paramBoolean);
        }
        i -= 1;
      }
    }
  }
  
  public void setIsFaceAdded(boolean paramBoolean)
  {
    this.mIsFaceAdded = paramBoolean;
  }
  
  public void setUserUnlocked(boolean paramBoolean)
  {
    this.mIsUserUnlocked = paramBoolean;
  }
  
  public boolean shouldPlayFacelockFailAnim()
  {
    if ((this.mFacelockRunningType == 1) || (this.mFacelockRunningType == 6)) {}
    while ((this.mFacelockRunningType == 7) || (this.mFacelockRunningType == 8) || (this.mFacelockRunningType == 9)) {
      return true;
    }
    return false;
  }
  
  public boolean shouldShowFacelockIcon()
  {
    if ((this.mFacelockRunningType == 3) || (this.mFacelockRunningType == 4)) {}
    while ((this.mFacelockRunningType == 5) || (this.mFacelockRunningType == 6) || (this.mFacelockRunningType == 7)) {
      return true;
    }
    return false;
  }
  
  public void updateFingerprintListeningState()
  {
    boolean bool = shouldListenForFingerprint();
    Log.d("KeyguardUpdateMonitor", "updateFPState: shouldListen = " + bool + ", running = " + this.mFingerprintRunningState + ", duringAcquired = " + this.mDuringAcquired + " , lockout = " + this.mLockoutState);
    if (this.mDuringAcquired)
    {
      Log.d("KeyguardUpdateMonitor", "not update fp listen state during acquired");
      return;
    }
    if ((this.mFingerprintRunningState != 1) || (bool))
    {
      if ((this.mFingerprintRunningState != 1) && (bool)) {
        startListeningForFingerprint();
      }
      return;
    }
    stopListeningForFingerprint();
  }
  
  public void updateLaunchingCameraState(boolean paramBoolean)
  {
    if (this.mLaunchingCamera != paramBoolean)
    {
      this.mLaunchingCamera = paramBoolean;
      updateFingerprintListeningState();
    }
  }
  
  public static class BatteryStatus
  {
    public final boolean fastCharge;
    public final int health;
    public final int level;
    public final int maxChargingWattage;
    public final int plugged;
    public final int status;
    
    public BatteryStatus(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
    {
      this.status = paramInt1;
      this.level = paramInt2;
      this.plugged = paramInt3;
      this.health = paramInt4;
      this.maxChargingWattage = paramInt5;
      this.fastCharge = paramBoolean;
    }
    
    public final int getChargingSpeed(int paramInt1, int paramInt2)
    {
      paramInt2 = 0;
      if (this.fastCharge) {
        return 2;
      }
      if (this.maxChargingWattage <= 0) {
        paramInt2 = -1;
      }
      while (this.maxChargingWattage < paramInt1) {
        return paramInt2;
      }
      return 1;
    }
    
    public boolean isBatteryLow()
    {
      return this.level < 20;
    }
    
    public boolean isCharged()
    {
      return (this.status == 5) || (this.level >= 100);
    }
    
    public boolean isPluggedIn()
    {
      if ((this.plugged == 1) || (this.plugged == 2)) {}
      while (this.plugged == 4) {
        return true;
      }
      return false;
    }
  }
  
  static class DisplayClientState {}
  
  private static class SimData
  {
    public IccCardConstants.State simState;
    public int slotId;
    public int subId;
    
    SimData(IccCardConstants.State paramState, int paramInt1, int paramInt2)
    {
      this.simState = paramState;
      this.slotId = paramInt1;
      this.subId = paramInt2;
    }
    
    static SimData fromIntent(Intent paramIntent)
    {
      if (!"android.intent.action.SIM_STATE_CHANGED".equals(paramIntent.getAction())) {
        throw new IllegalArgumentException("only handles intent ACTION_SIM_STATE_CHANGED");
      }
      String str = paramIntent.getStringExtra("ss");
      int i = paramIntent.getIntExtra("slot", 0);
      int j = paramIntent.getIntExtra("subscription", -1);
      if ("ABSENT".equals(str)) {
        if ("PERM_DISABLED".equals(paramIntent.getStringExtra("reason"))) {
          paramIntent = IccCardConstants.State.PERM_DISABLED;
        }
      }
      for (;;)
      {
        return new SimData(paramIntent, i, j);
        paramIntent = IccCardConstants.State.ABSENT;
        continue;
        if ("READY".equals(str))
        {
          paramIntent = IccCardConstants.State.READY;
        }
        else if ("LOCKED".equals(str))
        {
          paramIntent = paramIntent.getStringExtra("reason");
          if ("PIN".equals(paramIntent)) {
            paramIntent = IccCardConstants.State.PIN_REQUIRED;
          } else if ("PUK".equals(paramIntent)) {
            paramIntent = IccCardConstants.State.PUK_REQUIRED;
          } else {
            paramIntent = IccCardConstants.State.UNKNOWN;
          }
        }
        else if ("NETWORK".equals(str))
        {
          paramIntent = IccCardConstants.State.NETWORK_LOCKED;
        }
        else if ("CARD_IO_ERROR".equals(str))
        {
          paramIntent = IccCardConstants.State.CARD_IO_ERROR;
        }
        else if (("LOADED".equals(str)) || ("IMSI".equals(str)))
        {
          paramIntent = IccCardConstants.State.READY;
        }
        else
        {
          paramIntent = IccCardConstants.State.UNKNOWN;
        }
      }
    }
    
    public String toString()
    {
      return "SimData{state=" + this.simState + ",slotId=" + this.slotId + ",subId=" + this.subId + "}";
    }
  }
  
  public class StrongAuthTracker
    extends LockPatternUtils.StrongAuthTracker
  {
    public StrongAuthTracker(Context paramContext)
    {
      super();
    }
    
    public boolean hasUserAuthenticatedSinceBoot()
    {
      boolean bool = false;
      if ((getStrongAuthForUser(KeyguardUpdateMonitor.getCurrentUser()) & 0x1) == 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean isUnlockingWithFingerprintAllowed()
    {
      return isFingerprintAllowedForUser(KeyguardUpdateMonitor.getCurrentUser());
    }
    
    public void onStrongAuthRequiredChanged(int paramInt)
    {
      KeyguardUpdateMonitor.-wrap25(KeyguardUpdateMonitor.this, paramInt);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\KeyguardUpdateMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */