package com.android.systemui.statusbar.policy;

import android.app.AlarmManager;
import android.app.AlarmManager.AlarmClockInfo;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.service.notification.Condition;
import android.service.notification.IConditionListener;
import android.service.notification.IConditionListener.Stub;
import android.service.notification.ZenModeConfig;
import android.service.notification.ZenModeConfig.ZenRule;
import android.util.Log;
import android.util.Slog;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.qs.GlobalSetting;
import com.android.systemui.volume.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;

public class ZenModeControllerImpl
  implements ZenModeController
{
  private static final boolean DEBUG = Log.isLoggable("ZenModeController", 3);
  private final AlarmManager mAlarmManager;
  private final ArrayList<ZenModeController.Callback> mCallbacks = new ArrayList();
  private final LinkedHashMap<Uri, Condition> mConditions = new LinkedHashMap();
  private ZenModeConfig mConfig;
  private final GlobalSetting mConfigSetting;
  private final Context mContext;
  private final IConditionListener mListener = new IConditionListener.Stub()
  {
    public void onConditionsReceived(Condition[] paramAnonymousArrayOfCondition)
    {
      StringBuilder localStringBuilder;
      if (ZenModeControllerImpl.-get0())
      {
        localStringBuilder = new StringBuilder().append("onConditionsReceived ");
        if (paramAnonymousArrayOfCondition != null) {
          break label65;
        }
      }
      label65:
      for (int i = 0;; i = paramAnonymousArrayOfCondition.length)
      {
        Slog.d("ZenModeController", i + " mRequesting=" + ZenModeControllerImpl.-get3(ZenModeControllerImpl.this));
        if (ZenModeControllerImpl.-get3(ZenModeControllerImpl.this)) {
          break;
        }
        return;
      }
      ZenModeControllerImpl.-wrap4(ZenModeControllerImpl.this, paramAnonymousArrayOfCondition);
    }
  };
  private final GlobalSetting mModeSetting;
  private KeyguardUpdateMonitorCallback mMonitorCallback = new KeyguardUpdateMonitorCallback()
  {
    public void onSystemReady()
    {
      ZenModeControllerImpl.-set0(ZenModeControllerImpl.this, Settings.System.getIntForUser(ZenModeControllerImpl.-get1(ZenModeControllerImpl.this).getContentResolver(), "oem_vibrate_under_silent", 0, KeyguardUpdateMonitor.getCurrentUser()));
      int i = Settings.Global.getInt(ZenModeControllerImpl.-get1(ZenModeControllerImpl.this).getContentResolver(), "zen_mode", 0);
      ZenModeControllerImpl.-wrap3(ZenModeControllerImpl.this, i);
    }
  };
  private final NotificationManager mNoMan;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.app.action.NEXT_ALARM_CLOCK_CHANGED".equals(paramAnonymousIntent.getAction())) {
        ZenModeControllerImpl.-wrap1(ZenModeControllerImpl.this);
      }
      if ("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED".equals(paramAnonymousIntent.getAction())) {
        ZenModeControllerImpl.-wrap0(ZenModeControllerImpl.this);
      }
    }
  };
  private boolean mRegistered;
  private boolean mRequesting;
  private final SettingObserver mSettingObserver = new SettingObserver();
  private final SetupObserver mSetupObserver;
  private final GlobalSetting mThreekeySetting;
  private int mUserId;
  private final UserManager mUserManager;
  private int mVibrateWhenMute = 0;
  private int mZenMode = 0;
  
  public ZenModeControllerImpl(Context paramContext, Handler paramHandler)
  {
    this.mContext = paramContext;
    this.mModeSetting = new GlobalSetting(this.mContext, paramHandler, "zen_mode")
    {
      protected void handleValueChanged(int paramAnonymousInt)
      {
        ZenModeControllerImpl.-wrap3(ZenModeControllerImpl.this, paramAnonymousInt);
      }
    };
    this.mThreekeySetting = new GlobalSetting(this.mContext, paramHandler, "three_Key_mode")
    {
      protected void handleValueChanged(int paramAnonymousInt)
      {
        ZenModeControllerImpl.-wrap3(ZenModeControllerImpl.this, ZenModeControllerImpl.-get2(ZenModeControllerImpl.this).getValue());
      }
    };
    this.mConfigSetting = new GlobalSetting(this.mContext, paramHandler, "zen_mode_config_etag")
    {
      protected void handleValueChanged(int paramAnonymousInt)
      {
        ZenModeControllerImpl.-wrap5(ZenModeControllerImpl.this);
      }
    };
    this.mNoMan = ((NotificationManager)paramContext.getSystemService("notification"));
    this.mConfig = this.mNoMan.getZenModeConfig();
    this.mModeSetting.setListening(true);
    this.mThreekeySetting.setListening(true);
    this.mConfigSetting.setListening(true);
    this.mAlarmManager = ((AlarmManager)paramContext.getSystemService("alarm"));
    this.mSetupObserver = new SetupObserver(paramHandler);
    this.mSetupObserver.register();
    this.mUserManager = ((UserManager)paramContext.getSystemService(UserManager.class));
    this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("oem_vibrate_under_silent"), false, this.mSettingObserver, -1);
    this.mVibrateWhenMute = Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_vibrate_under_silent", 0, KeyguardUpdateMonitor.getCurrentUser());
  }
  
  private void fireConditionsChanged(Condition[] paramArrayOfCondition)
  {
    Iterator localIterator = this.mCallbacks.iterator();
    while (localIterator.hasNext()) {
      ((ZenModeController.Callback)localIterator.next()).onConditionsChanged(paramArrayOfCondition);
    }
  }
  
  private void fireConfigChanged(ZenModeConfig paramZenModeConfig)
  {
    Iterator localIterator = this.mCallbacks.iterator();
    while (localIterator.hasNext()) {
      ((ZenModeController.Callback)localIterator.next()).onConfigChanged(paramZenModeConfig);
    }
  }
  
  private void fireEffectsSuppressorChanged()
  {
    Iterator localIterator = this.mCallbacks.iterator();
    while (localIterator.hasNext()) {
      ((ZenModeController.Callback)localIterator.next()).onEffectsSupressorChanged();
    }
  }
  
  private void fireManualRuleChanged(ZenModeConfig.ZenRule paramZenRule)
  {
    Iterator localIterator = this.mCallbacks.iterator();
    while (localIterator.hasNext()) {
      ((ZenModeController.Callback)localIterator.next()).onManualRuleChanged(paramZenRule);
    }
  }
  
  private void fireNextAlarmChanged()
  {
    Iterator localIterator = this.mCallbacks.iterator();
    while (localIterator.hasNext()) {
      ((ZenModeController.Callback)localIterator.next()).onNextAlarmChanged();
    }
  }
  
  private void fireZenAvailableChanged(boolean paramBoolean)
  {
    Iterator localIterator = this.mCallbacks.iterator();
    while (localIterator.hasNext()) {
      ((ZenModeController.Callback)localIterator.next()).onZenAvailableChanged(paramBoolean);
    }
  }
  
  private void fireZenChanged(int paramInt)
  {
    paramInt = Util.getCorrectZenMode(paramInt, Util.getThreeKeyStatus(this.mContext), this.mVibrateWhenMute);
    this.mZenMode = paramInt;
    Log.i("ZenModeController", " fireZenChanged zenMode:" + paramInt);
    Iterator localIterator = this.mCallbacks.iterator();
    while (localIterator.hasNext()) {
      ((ZenModeController.Callback)localIterator.next()).onZenChanged(paramInt);
    }
  }
  
  private void updateConditions(Condition[] paramArrayOfCondition)
  {
    int i = 0;
    if ((paramArrayOfCondition == null) || (paramArrayOfCondition.length == 0)) {
      return;
    }
    int j = paramArrayOfCondition.length;
    if (i < j)
    {
      Condition localCondition = paramArrayOfCondition[i];
      if ((localCondition.flags & 0x1) == 0) {}
      for (;;)
      {
        i += 1;
        break;
        this.mConditions.put(localCondition.id, localCondition);
      }
    }
    fireConditionsChanged((Condition[])this.mConditions.values().toArray(new Condition[this.mConditions.values().size()]));
  }
  
  private void updateZenModeConfig()
  {
    ZenModeConfig.ZenRule localZenRule2 = null;
    ZenModeConfig localZenModeConfig = this.mNoMan.getZenModeConfig();
    if (Objects.equals(localZenModeConfig, this.mConfig)) {
      return;
    }
    if (this.mConfig != null) {}
    for (ZenModeConfig.ZenRule localZenRule1 = this.mConfig.manualRule;; localZenRule1 = null)
    {
      this.mConfig = localZenModeConfig;
      fireConfigChanged(localZenModeConfig);
      if (localZenModeConfig != null) {
        localZenRule2 = localZenModeConfig.manualRule;
      }
      if (!Objects.equals(localZenRule1, localZenRule2)) {
        break;
      }
      return;
    }
    fireManualRuleChanged(localZenRule2);
  }
  
  public void addCallback(ZenModeController.Callback paramCallback)
  {
    this.mCallbacks.add(paramCallback);
    if (paramCallback != null) {
      paramCallback.onZenChanged(this.mZenMode);
    }
  }
  
  public ZenModeConfig getConfig()
  {
    return this.mConfig;
  }
  
  public ZenModeConfig.ZenRule getManualRule()
  {
    if (this.mConfig == null) {
      return null;
    }
    return this.mConfig.manualRule;
  }
  
  public long getNextAlarm()
  {
    AlarmManager.AlarmClockInfo localAlarmClockInfo = this.mAlarmManager.getNextAlarmClock(this.mUserId);
    if (localAlarmClockInfo != null) {
      return localAlarmClockInfo.getTriggerTime();
    }
    return 0L;
  }
  
  public int getZen()
  {
    return this.mModeSetting.getValue();
  }
  
  public boolean isCountdownConditionSupported()
  {
    return NotificationManager.from(this.mContext).isSystemConditionProviderEnabled("countdown");
  }
  
  public boolean isVolumeRestricted()
  {
    return this.mUserManager.hasUserRestriction("no_adjust_volume", new UserHandle(this.mUserId));
  }
  
  public boolean isZenAvailable()
  {
    if (this.mSetupObserver.isDeviceProvisioned()) {
      return this.mSetupObserver.isUserSetup();
    }
    return false;
  }
  
  public void removeCallback(ZenModeController.Callback paramCallback)
  {
    this.mCallbacks.remove(paramCallback);
  }
  
  public void setUserId(int paramInt)
  {
    this.mUserId = paramInt;
    if (this.mRegistered) {
      this.mContext.unregisterReceiver(this.mReceiver);
    }
    IntentFilter localIntentFilter = new IntentFilter("android.app.action.NEXT_ALARM_CLOCK_CHANGED");
    localIntentFilter.addAction("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED");
    this.mContext.registerReceiverAsUser(this.mReceiver, new UserHandle(this.mUserId), localIntentFilter, null, null);
    this.mRegistered = true;
    this.mSetupObserver.register();
    KeyguardUpdateMonitor.getInstance(this.mContext).registerCallback(this.mMonitorCallback);
  }
  
  public void setZen(int paramInt, Uri paramUri, String paramString)
  {
    this.mNoMan.setZenMode(paramInt, paramUri, paramString);
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
      ZenModeControllerImpl.-set0(ZenModeControllerImpl.this, Settings.System.getIntForUser(ZenModeControllerImpl.-get1(ZenModeControllerImpl.this).getContentResolver(), "oem_vibrate_under_silent", 0, KeyguardUpdateMonitor.getCurrentUser()));
      Log.i("ZenModeController", " SettingObserver mVibrateWhenMute:" + ZenModeControllerImpl.-get5(ZenModeControllerImpl.this));
    }
  }
  
  private final class SetupObserver
    extends ContentObserver
  {
    private boolean mRegistered;
    private final ContentResolver mResolver = ZenModeControllerImpl.-get1(ZenModeControllerImpl.this).getContentResolver();
    
    public SetupObserver(Handler paramHandler)
    {
      super();
    }
    
    public boolean isDeviceProvisioned()
    {
      boolean bool = false;
      if (Settings.Global.getInt(this.mResolver, "device_provisioned", 0) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean isUserSetup()
    {
      boolean bool = false;
      if (Settings.Secure.getIntForUser(this.mResolver, "user_setup_complete", 0, ZenModeControllerImpl.-get4(ZenModeControllerImpl.this)) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      if ((Settings.Global.getUriFor("device_provisioned").equals(paramUri)) || (Settings.Secure.getUriFor("user_setup_complete").equals(paramUri))) {
        ZenModeControllerImpl.-wrap2(ZenModeControllerImpl.this, ZenModeControllerImpl.this.isZenAvailable());
      }
    }
    
    public void register()
    {
      if (this.mRegistered) {
        this.mResolver.unregisterContentObserver(this);
      }
      this.mResolver.registerContentObserver(Settings.Global.getUriFor("device_provisioned"), false, this);
      this.mResolver.registerContentObserver(Settings.Secure.getUriFor("user_setup_complete"), false, this, ZenModeControllerImpl.-get4(ZenModeControllerImpl.this));
      ZenModeControllerImpl.-wrap2(ZenModeControllerImpl.this, ZenModeControllerImpl.this.isZenAvailable());
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\ZenModeControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */