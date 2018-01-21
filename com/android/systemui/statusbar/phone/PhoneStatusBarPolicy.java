package com.android.systemui.statusbar.phone;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AlarmManager;
import android.app.AlarmManager.AlarmClockInfo;
import android.app.IActivityManager;
import android.app.SynchronousUserSwitchObserver;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.System;
import android.util.Log;
import com.android.internal.telephony.IccCardConstants.State;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.qs.tiles.DndTile;
import com.android.systemui.qs.tiles.RotationLockTile;
import com.android.systemui.statusbar.policy.BluetoothController;
import com.android.systemui.statusbar.policy.BluetoothController.Callback;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.CastController.Callback;
import com.android.systemui.statusbar.policy.CastController.CastDevice;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.DataSaverController.Listener;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.HotspotController.Callback;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.statusbar.policy.RotationLockController.RotationLockControllerCallback;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.volume.Events;
import java.util.Iterator;

public class PhoneStatusBarPolicy
  implements BluetoothController.Callback, RotationLockController.RotationLockControllerCallback, DataSaverController.Listener
{
  private static final boolean DEBUG = Log.isLoggable("PhoneStatusBarPolicy", 3);
  private final AlarmManager mAlarmManager;
  private BluetoothController mBluetooth;
  private final CastController mCast;
  private final CastController.Callback mCastCallback = new CastController.Callback()
  {
    public void onCastDevicesChanged()
    {
      PhoneStatusBarPolicy.-wrap2(PhoneStatusBarPolicy.this);
    }
  };
  private final Context mContext;
  private boolean mCurrentUserSetup;
  private final DataSaverController mDataSaver;
  private final Handler mHandler = new Handler();
  private final HotspotController mHotspot;
  private final HotspotController.Callback mHotspotCallback = new HotspotController.Callback()
  {
    public void onHotspotChanged(boolean paramAnonymousBoolean)
    {
      PhoneStatusBarPolicy.-get3(PhoneStatusBarPolicy.this).setIcon(PhoneStatusBarPolicy.-get5(PhoneStatusBarPolicy.this), 2130838465, PhoneStatusBarPolicy.-get1(PhoneStatusBarPolicy.this).getString(2131690476));
      PhoneStatusBarPolicy.-get3(PhoneStatusBarPolicy.this).setIconVisibility(PhoneStatusBarPolicy.-get5(PhoneStatusBarPolicy.this), paramAnonymousBoolean);
    }
  };
  private final StatusBarIconController mIconController;
  private BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if (paramAnonymousContext.equals("android.app.action.NEXT_ALARM_CLOCK_CHANGED")) {
        PhoneStatusBarPolicy.-wrap1(PhoneStatusBarPolicy.this);
      }
      do
      {
        return;
        if ((paramAnonymousContext.equals("android.media.RINGER_MODE_CHANGED")) || (paramAnonymousContext.equals("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION")))
        {
          PhoneStatusBarPolicy.-wrap8(PhoneStatusBarPolicy.this);
          return;
        }
        if (paramAnonymousContext.equals("android.intent.action.SIM_STATE_CHANGED"))
        {
          PhoneStatusBarPolicy.-wrap6(PhoneStatusBarPolicy.this, paramAnonymousIntent);
          return;
        }
        if (paramAnonymousContext.equals("android.telecom.action.CURRENT_TTY_MODE_CHANGED"))
        {
          PhoneStatusBarPolicy.-wrap7(PhoneStatusBarPolicy.this, paramAnonymousIntent);
          return;
        }
        if ((paramAnonymousContext.equals("android.intent.action.MANAGED_PROFILE_AVAILABLE")) || (paramAnonymousContext.equals("android.intent.action.MANAGED_PROFILE_UNAVAILABLE")) || (paramAnonymousContext.equals("android.intent.action.MANAGED_PROFILE_REMOVED")))
        {
          PhoneStatusBarPolicy.-wrap5(PhoneStatusBarPolicy.this);
          PhoneStatusBarPolicy.-wrap4(PhoneStatusBarPolicy.this);
          return;
        }
      } while (!paramAnonymousContext.equals("android.intent.action.HEADSET_PLUG"));
      PhoneStatusBarPolicy.-wrap3(PhoneStatusBarPolicy.this, paramAnonymousIntent);
    }
  };
  private boolean mManagedProfileFocused = false;
  private boolean mManagedProfileIconVisible = false;
  private boolean mManagedProfileInQuietMode = false;
  private final BroadcastReceiver mNfcReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousIntent.getAction();
      int i = paramAnonymousIntent.getIntExtra("android.nfc.extra.ADAPTER_STATE", 0);
      if ((i == 3) || (i == 2))
      {
        PhoneStatusBarPolicy.-get3(PhoneStatusBarPolicy.this).setIconVisibility("nfc", true);
        return;
      }
      PhoneStatusBarPolicy.-get3(PhoneStatusBarPolicy.this).setIconVisibility("nfc", false);
    }
  };
  private Runnable mRemoveCastIconRunnable = new Runnable()
  {
    public void run()
    {
      if (PhoneStatusBarPolicy.-get0()) {
        Log.v("PhoneStatusBarPolicy", "updateCast: hiding icon NOW");
      }
      PhoneStatusBarPolicy.-get3(PhoneStatusBarPolicy.this).setIconVisibility(PhoneStatusBarPolicy.-get4(PhoneStatusBarPolicy.this), false);
    }
  };
  private final RotationLockController mRotationLockController;
  private final SettingObserver mSettingObserver = new SettingObserver();
  IccCardConstants.State mSimState = IccCardConstants.State.READY;
  private final String mSlotAlarmClock;
  private final String mSlotBluetooth;
  private final String mSlotCast;
  private final String mSlotDataSaver;
  private final String mSlotHeadset;
  private final String mSlotHotspot;
  private final String mSlotManagedProfile;
  private final String mSlotRotate;
  private final String mSlotTty;
  private final String mSlotVolume;
  private final String mSlotZen;
  private StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
  private final UserInfoController mUserInfoController;
  private final UserManager mUserManager;
  private final SynchronousUserSwitchObserver mUserSwitchListener = new SynchronousUserSwitchObserver()
  {
    public void onForegroundProfileSwitch(final int paramAnonymousInt)
    {
      PhoneStatusBarPolicy.-get2(PhoneStatusBarPolicy.this).post(new Runnable()
      {
        public void run()
        {
          PhoneStatusBarPolicy.-wrap0(PhoneStatusBarPolicy.this, paramAnonymousInt);
        }
      });
    }
    
    public void onUserSwitchComplete(final int paramAnonymousInt)
      throws RemoteException
    {
      PhoneStatusBarPolicy.-get2(PhoneStatusBarPolicy.this).post(new Runnable()
      {
        public void run()
        {
          PhoneStatusBarPolicy.-wrap1(PhoneStatusBarPolicy.this);
          PhoneStatusBarPolicy.-wrap0(PhoneStatusBarPolicy.this, paramAnonymousInt);
          PhoneStatusBarPolicy.-wrap5(PhoneStatusBarPolicy.this);
          PhoneStatusBarPolicy.-wrap4(PhoneStatusBarPolicy.this);
        }
      });
    }
    
    public void onUserSwitching(int paramAnonymousInt)
      throws RemoteException
    {
      PhoneStatusBarPolicy.-get2(PhoneStatusBarPolicy.this).post(new Runnable()
      {
        public void run()
        {
          PhoneStatusBarPolicy.-get6(PhoneStatusBarPolicy.this).reloadUserInfo();
        }
      });
    }
  };
  private int mVibrateWhenMute = 0;
  private boolean mVolumeVisible;
  private int mZen;
  private boolean mZenVisible;
  
  public PhoneStatusBarPolicy(Context paramContext, StatusBarIconController paramStatusBarIconController, CastController paramCastController, HotspotController paramHotspotController, UserInfoController paramUserInfoController, BluetoothController paramBluetoothController, RotationLockController paramRotationLockController, DataSaverController paramDataSaverController)
  {
    this.mContext = paramContext;
    this.mIconController = paramStatusBarIconController;
    this.mCast = paramCastController;
    this.mHotspot = paramHotspotController;
    this.mBluetooth = paramBluetoothController;
    this.mBluetooth.addStateChangedCallback(this);
    this.mAlarmManager = ((AlarmManager)paramContext.getSystemService("alarm"));
    this.mUserInfoController = paramUserInfoController;
    this.mUserManager = ((UserManager)this.mContext.getSystemService("user"));
    this.mRotationLockController = paramRotationLockController;
    this.mDataSaver = paramDataSaverController;
    this.mSlotCast = paramContext.getString(17039392);
    this.mSlotHotspot = paramContext.getString(17039393);
    this.mSlotBluetooth = paramContext.getString(17039395);
    this.mSlotTty = paramContext.getString(17039397);
    this.mSlotZen = paramContext.getString(17039399);
    this.mSlotVolume = paramContext.getString(17039401);
    this.mSlotAlarmClock = paramContext.getString(17039408);
    this.mSlotManagedProfile = paramContext.getString(17039388);
    this.mSlotRotate = paramContext.getString(17039385);
    this.mSlotHeadset = paramContext.getString(17039386);
    this.mSlotDataSaver = paramContext.getString(17039387);
    this.mRotationLockController.addRotationLockControllerCallback(this);
    paramStatusBarIconController = new IntentFilter();
    paramStatusBarIconController.addAction("android.app.action.NEXT_ALARM_CLOCK_CHANGED");
    paramStatusBarIconController.addAction("android.media.RINGER_MODE_CHANGED");
    paramStatusBarIconController.addAction("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION");
    paramStatusBarIconController.addAction("android.intent.action.HEADSET_PLUG");
    paramStatusBarIconController.addAction("android.intent.action.SIM_STATE_CHANGED");
    paramStatusBarIconController.addAction("android.telecom.action.CURRENT_TTY_MODE_CHANGED");
    paramStatusBarIconController.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
    paramStatusBarIconController.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
    paramStatusBarIconController.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
    this.mContext.registerReceiver(this.mIntentReceiver, paramStatusBarIconController, null, this.mHandler);
    this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("oem_vibrate_under_silent"), false, this.mSettingObserver, -1);
    try
    {
      ActivityManagerNative.getDefault().registerUserSwitchObserver(this.mUserSwitchListener, "PhoneStatusBarPolicy");
      this.mIconController.setIcon(this.mSlotTty, 2130839000, null);
      this.mIconController.setIconVisibility(this.mSlotTty, false);
      updateBluetooth();
      this.mIconController.setIcon(this.mSlotAlarmClock, 2130838390, null);
      this.mIconController.setIconVisibility(this.mSlotAlarmClock, false);
      this.mIconController.setIcon(this.mSlotZen, 2130839018, null);
      this.mIconController.setIconVisibility(this.mSlotZen, false);
      this.mIconController.setIcon(this.mSlotVolume, 2130838577, null);
      this.mIconController.setIconVisibility(this.mSlotVolume, false);
      updateVolumeZen();
      this.mIconController.setIcon(this.mSlotCast, 2130838405, null);
      this.mIconController.setIconVisibility(this.mSlotCast, false);
      this.mCast.addCallback(this.mCastCallback);
      if (!this.mContext.getResources().getBoolean(17957071))
      {
        this.mIconController.setIcon(this.mSlotHotspot, 2130838465, this.mContext.getString(2131690476));
        this.mIconController.setIconVisibility(this.mSlotHotspot, this.mHotspot.isHotspotEnabled());
        this.mHotspot.addCallback(this.mHotspotCallback);
      }
      this.mIconController.setIcon(this.mSlotManagedProfile, 2130838469, this.mContext.getString(2131690477));
      this.mIconController.setIconVisibility(this.mSlotManagedProfile, this.mManagedProfileIconVisible);
      this.mIconController.setIcon(this.mSlotDataSaver, 2130838460, paramContext.getString(2131690580));
      this.mIconController.setIconVisibility(this.mSlotDataSaver, false);
      this.mDataSaver.addListener(this);
      try
      {
        this.mIconController.setIcon("volte", 2130839001, null);
        this.mIconController.setIconVisibility("volte", false);
      }
      catch (SecurityException paramContext)
      {
        try
        {
          for (;;)
          {
            this.mIconController.setIcon("vowifi", 2130839002, "");
            this.mIconController.setIconVisibility("vowifi", false);
            this.mIconController.setIcon("nfc", 2130838471, null);
            this.mIconController.setIconVisibility("nfc", false);
            this.mContext.registerReceiverAsUser(this.mNfcReceiver, UserHandle.ALL, new IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED"), null, null);
            return;
            paramContext = paramContext;
            Log.e("PhoneStatusBarPolicy", "The framework doesn't support VoLTE icon");
          }
        }
        catch (SecurityException paramContext)
        {
          for (;;)
          {
            Log.e("PhoneStatusBarPolicy", "setIcon The framework doesn't support SLOT_VOWIFI icon");
          }
        }
      }
    }
    catch (RemoteException paramStatusBarIconController)
    {
      for (;;) {}
    }
  }
  
  private int getBluetoothBatteryIcon(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 0: 
      return 2130838395;
    case 1: 
      return 2130838396;
    case 2: 
      return 2130838397;
    case 3: 
      return 2130838398;
    case 4: 
      return 2130838399;
    case 5: 
      return 2130838400;
    case 6: 
      return 2130838401;
    case 7: 
      return 2130838402;
    case 8: 
      return 2130838403;
    }
    return 2130838404;
  }
  
  private void profileChanged(int paramInt)
  {
    boolean bool2 = false;
    Object localObject = null;
    if (paramInt == -2) {}
    for (;;)
    {
      try
      {
        UserInfo localUserInfo = ActivityManagerNative.getDefault().getCurrentUser();
        localObject = localUserInfo;
      }
      catch (RemoteException localRemoteException)
      {
        boolean bool1;
        continue;
      }
      bool1 = bool2;
      if (localObject != null)
      {
        bool1 = bool2;
        if (((UserInfo)localObject).isManagedProfile())
        {
          bool1 = bool2;
          if (paramInt != 999) {
            bool1 = true;
          }
        }
      }
      this.mManagedProfileFocused = bool1;
      if (DEBUG) {
        Log.v("PhoneStatusBarPolicy", "profileChanged: mManagedProfileFocused: " + this.mManagedProfileFocused);
      }
      return;
      localObject = this.mUserManager.getUserInfo(paramInt);
    }
  }
  
  private void updateAlarm()
  {
    Object localObject = this.mAlarmManager.getNextAlarmClock(-2);
    boolean bool;
    int i;
    label35:
    String str;
    if ((localObject != null) && (((AlarmManager.AlarmClockInfo)localObject).getTriggerTime() > 0L))
    {
      bool = true;
      if (this.mZen != 2) {
        break label93;
      }
      i = 1;
      localObject = this.mIconController;
      str = this.mSlotAlarmClock;
      if (i == 0) {
        break label98;
      }
      i = 2130838391;
      label54:
      ((StatusBarIconController)localObject).setIcon(str, i, null);
      localObject = this.mIconController;
      str = this.mSlotAlarmClock;
      if (!this.mCurrentUserSetup) {
        break label105;
      }
    }
    for (;;)
    {
      ((StatusBarIconController)localObject).setIconVisibility(str, bool);
      return;
      bool = false;
      break;
      label93:
      i = 0;
      break label35;
      label98:
      i = 2130838390;
      break label54;
      label105:
      bool = false;
    }
  }
  
  private final void updateBluetooth()
  {
    int j = 2130838406;
    String str2 = this.mContext.getString(2131690210);
    boolean bool1 = false;
    String str1 = str2;
    int i = j;
    boolean bool2;
    if (this.mBluetooth != null)
    {
      bool2 = this.mBluetooth.isBluetoothEnabled();
      bool1 = bool2;
      str1 = str2;
      i = j;
      if (this.mBluetooth.isBluetoothConnected())
      {
        i = this.mBluetooth.getBluetoothBatteryLevel();
        if ((i <= -1) || (getBluetoothBatteryIcon(i) == 0)) {
          break label126;
        }
        i = getBluetoothBatteryIcon(i);
        str1 = str2;
      }
    }
    for (bool1 = bool2;; bool1 = bool2)
    {
      this.mIconController.setIcon(this.mSlotBluetooth, i, str1);
      this.mIconController.setIconVisibility(this.mSlotBluetooth, bool1);
      return;
      label126:
      i = 2130838407;
      str1 = this.mContext.getString(2131690103);
    }
  }
  
  private void updateCast()
  {
    boolean bool2 = false;
    Iterator localIterator = this.mCast.getCastDevices().iterator();
    CastController.CastDevice localCastDevice;
    do
    {
      bool1 = bool2;
      if (!localIterator.hasNext()) {
        break;
      }
      localCastDevice = (CastController.CastDevice)localIterator.next();
    } while ((localCastDevice.state != 1) && (localCastDevice.state != 2));
    boolean bool1 = true;
    if (DEBUG) {
      Log.v("PhoneStatusBarPolicy", "updateCast: isCasting: " + bool1);
    }
    this.mHandler.removeCallbacks(this.mRemoveCastIconRunnable);
    if (bool1)
    {
      this.mIconController.setIcon(this.mSlotCast, 2130838405, this.mContext.getString(2131690176));
      this.mIconController.setIconVisibility(this.mSlotCast, true);
      return;
    }
    if (DEBUG) {
      Log.v("PhoneStatusBarPolicy", "updateCast: hiding icon in 3 sec...");
    }
    this.mHandler.postDelayed(this.mRemoveCastIconRunnable, 3000L);
  }
  
  private void updateHeadsetPlug(Intent paramIntent)
  {
    boolean bool1;
    boolean bool2;
    label27:
    label82:
    StatusBarIconController localStatusBarIconController;
    String str;
    if (paramIntent.getIntExtra("state", 0) != 0)
    {
      bool1 = true;
      if (paramIntent.getIntExtra("microphone", 0) == 0) {
        break label136;
      }
      bool2 = true;
      Log.i("PhoneStatusBarPolicy", "updateHeadsetPlug connected:" + bool1 + " hasMic:" + bool2);
      if (!bool1) {
        break label156;
      }
      paramIntent = this.mContext;
      if (!bool2) {
        break label142;
      }
      i = 2131690578;
      paramIntent = paramIntent.getString(i);
      localStatusBarIconController = this.mIconController;
      str = this.mSlotHeadset;
      if (!bool2) {
        break label149;
      }
    }
    label136:
    label142:
    label149:
    for (int i = 2130837725;; i = 2130837724)
    {
      localStatusBarIconController.setIcon(str, i, paramIntent);
      this.mIconController.setIconVisibility(this.mSlotHeadset, true);
      return;
      bool1 = false;
      break;
      bool2 = false;
      break label27;
      i = 2131690577;
      break label82;
    }
    label156:
    this.mIconController.setIconVisibility(this.mSlotHeadset, false);
  }
  
  private void updateManagedProfile()
  {
    if (DEBUG) {
      Log.v("PhoneStatusBarPolicy", "updateManagedProfile: mManagedProfileFocused: " + this.mManagedProfileFocused);
    }
    boolean bool;
    if ((!this.mManagedProfileFocused) || (this.mStatusBarKeyguardViewManager.isShowing()))
    {
      if (!this.mManagedProfileInQuietMode) {
        break label140;
      }
      bool = true;
      this.mIconController.setIcon(this.mSlotManagedProfile, 2130838470, this.mContext.getString(2131690477));
    }
    for (;;)
    {
      if (this.mManagedProfileIconVisible != bool)
      {
        this.mIconController.setIconVisibility(this.mSlotManagedProfile, bool);
        this.mManagedProfileIconVisible = bool;
      }
      return;
      bool = true;
      this.mIconController.setIcon(this.mSlotManagedProfile, 2130838469, this.mContext.getString(2131690477));
      continue;
      label140:
      bool = false;
    }
  }
  
  private void updateQuietState()
  {
    this.mManagedProfileInQuietMode = false;
    int i = ActivityManager.getCurrentUser();
    Iterator localIterator = this.mUserManager.getEnabledProfiles(i).iterator();
    while (localIterator.hasNext())
    {
      UserInfo localUserInfo = (UserInfo)localIterator.next();
      if ((localUserInfo.isManagedProfile()) && (localUserInfo.isQuietModeEnabled()))
      {
        this.mManagedProfileInQuietMode = true;
        return;
      }
    }
  }
  
  private final void updateSimState(Intent paramIntent)
  {
    String str = paramIntent.getStringExtra("ss");
    if ("ABSENT".equals(str))
    {
      this.mSimState = IccCardConstants.State.ABSENT;
      return;
    }
    if ("CARD_IO_ERROR".equals(str))
    {
      this.mSimState = IccCardConstants.State.CARD_IO_ERROR;
      return;
    }
    if ("CARD_RESTRICTED".equals(str))
    {
      this.mSimState = IccCardConstants.State.CARD_RESTRICTED;
      return;
    }
    if ("READY".equals(str))
    {
      this.mSimState = IccCardConstants.State.READY;
      return;
    }
    if ("LOCKED".equals(str))
    {
      paramIntent = paramIntent.getStringExtra("reason");
      if ("PIN".equals(paramIntent))
      {
        this.mSimState = IccCardConstants.State.PIN_REQUIRED;
        return;
      }
      if ("PUK".equals(paramIntent))
      {
        this.mSimState = IccCardConstants.State.PUK_REQUIRED;
        return;
      }
      this.mSimState = IccCardConstants.State.NETWORK_LOCKED;
      return;
    }
    this.mSimState = IccCardConstants.State.UNKNOWN;
  }
  
  private final void updateTTY(Intent paramIntent)
  {
    if (paramIntent.getIntExtra("android.telecom.intent.extra.CURRENT_TTY_MODE", 0) != 0) {}
    for (boolean bool = true;; bool = false)
    {
      if (DEBUG) {
        Log.v("PhoneStatusBarPolicy", "updateTTY: enabled: " + bool);
      }
      if (!bool) {
        break;
      }
      if (DEBUG) {
        Log.v("PhoneStatusBarPolicy", "updateTTY: set TTY on");
      }
      this.mIconController.setIcon(this.mSlotTty, 2130839000, this.mContext.getString(2131690173));
      this.mIconController.setIconVisibility(this.mSlotTty, true);
      return;
    }
    if (DEBUG) {
      Log.v("PhoneStatusBarPolicy", "updateTTY: set TTY off");
    }
    this.mIconController.setIconVisibility(this.mSlotTty, false);
  }
  
  private final void updateVolumeZen()
  {
    AudioManager localAudioManager = (AudioManager)this.mContext.getSystemService("audio");
    boolean bool3 = false;
    int k = 0;
    Object localObject2 = null;
    this.mVibrateWhenMute = Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_vibrate_under_silent", 0, KeyguardUpdateMonitor.getCurrentUser());
    boolean bool1;
    int i;
    String str;
    Object localObject1;
    int j;
    boolean bool2;
    switch (this.mZen)
    {
    case 2: 
    default: 
      bool1 = false;
      i = 2130838998;
      str = this.mContext.getString(2131690264);
      Events.writeEvent(this.mContext, 17, new Object[] { "three key in normal state, no icon shows on status bar" });
      localObject1 = localObject2;
      j = k;
      bool2 = bool3;
      if (DndTile.isVisible(this.mContext))
      {
        if (!DndTile.isCombinedIcon(this.mContext)) {
          break label350;
        }
        bool2 = bool3;
        j = k;
        localObject1 = localObject2;
      }
      break;
    }
    for (;;)
    {
      if (bool1) {
        this.mIconController.setIcon(this.mSlotZen, i, str);
      }
      if (bool1 != this.mZenVisible)
      {
        this.mIconController.setIconVisibility(this.mSlotZen, bool1);
        this.mZenVisible = bool1;
      }
      if (bool2) {
        this.mIconController.setIcon(this.mSlotVolume, j, (CharSequence)localObject1);
      }
      if (bool2 != this.mVolumeVisible)
      {
        this.mIconController.setIconVisibility(this.mSlotVolume, bool2);
        this.mVolumeVisible = bool2;
      }
      updateAlarm();
      return;
      bool1 = true;
      i = 2130838997;
      str = this.mContext.getString(2131690358);
      Events.writeEvent(this.mContext, 17, new Object[] { "show no disturb icon on status bar" });
      break;
      bool1 = true;
      if (this.mVibrateWhenMute == 1) {}
      for (i = 2130838577;; i = 2130838999)
      {
        str = this.mContext.getString(2131690359);
        Events.writeEvent(this.mContext, 17, new Object[] { "show silent icon on status bar" });
        break;
      }
      label350:
      localObject1 = localObject2;
      j = k;
      bool2 = bool3;
      if (localAudioManager.getRingerModeInternal() == 0)
      {
        bool2 = true;
        j = 2130838576;
        localObject1 = this.mContext.getString(2131690175);
      }
    }
  }
  
  public void appTransitionStarting(long paramLong1, long paramLong2)
  {
    updateManagedProfile();
  }
  
  public void notifyKeyguardShowingChanged()
  {
    updateManagedProfile();
  }
  
  public void onBluetoothBatteryChanged()
  {
    if (DEBUG) {
      Log.d("PhoneStatusBarPolicy", "onBluetoothBatteryChanged");
    }
    updateBluetooth();
  }
  
  public void onBluetoothDevicesChanged()
  {
    updateBluetooth();
  }
  
  public void onBluetoothStateChange(boolean paramBoolean)
  {
    updateBluetooth();
  }
  
  public void onDataSaverChanged(boolean paramBoolean)
  {
    this.mIconController.setIconVisibility(this.mSlotDataSaver, paramBoolean);
  }
  
  public void onLTEStatusUpdate(boolean[] paramArrayOfBoolean)
  {
    final int i = paramArrayOfBoolean[0];
    final int j = paramArrayOfBoolean[2];
    try
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          Log.v("PhoneStatusBarPolicy", "onLTEStatusUpdate: volteEnable:" + i);
          PhoneStatusBarPolicy.-get3(PhoneStatusBarPolicy.this).setIconVisibility("volte", i);
        }
      });
    }
    catch (Exception paramArrayOfBoolean)
    {
      for (;;)
      {
        try
        {
          this.mHandler.post(new Runnable()
          {
            public void run()
            {
              Log.v("PhoneStatusBarPolicy", "onLTEStatusUpdate: vowifiEnable:" + j);
              PhoneStatusBarPolicy.-get3(PhoneStatusBarPolicy.this).setIconVisibility("vowifi", j);
            }
          });
          return;
        }
        catch (Exception paramArrayOfBoolean)
        {
          Log.e("PhoneStatusBarPolicy", "The framework doesn't support VoWIFI icon");
        }
        paramArrayOfBoolean = paramArrayOfBoolean;
        Log.e("PhoneStatusBarPolicy", "The framework doesn't support VoLTE icon");
      }
    }
  }
  
  public void onRotationLockStateChanged(boolean paramBoolean1, boolean paramBoolean2)
  {
    paramBoolean2 = RotationLockTile.isCurrentOrientationLockPortrait(this.mRotationLockController, this.mContext);
    if (paramBoolean1)
    {
      if (paramBoolean2) {
        this.mIconController.setIcon(this.mSlotRotate, 2130838579, this.mContext.getString(2131690257));
      }
      for (;;)
      {
        this.mIconController.setIconVisibility(this.mSlotRotate, true);
        return;
        this.mIconController.setIcon(this.mSlotRotate, 2130838578, this.mContext.getString(2131690256));
      }
    }
    this.mIconController.setIconVisibility(this.mSlotRotate, false);
  }
  
  public void setCurrentUserSetup(boolean paramBoolean)
  {
    if (this.mCurrentUserSetup == paramBoolean) {
      return;
    }
    this.mCurrentUserSetup = paramBoolean;
    updateAlarm();
    updateQuietState();
  }
  
  public void setStatusBarKeyguardViewManager(StatusBarKeyguardViewManager paramStatusBarKeyguardViewManager)
  {
    this.mStatusBarKeyguardViewManager = paramStatusBarKeyguardViewManager;
  }
  
  public void setZenMode(int paramInt)
  {
    this.mZen = paramInt;
    updateVolumeZen();
  }
  
  private final class SettingObserver
    extends ContentObserver
  {
    public SettingObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      super.onChange(paramBoolean, paramUri);
      PhoneStatusBarPolicy.-wrap8(PhoneStatusBarPolicy.this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\PhoneStatusBarPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */