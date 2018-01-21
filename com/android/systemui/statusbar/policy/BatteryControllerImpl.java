package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings.System;
import android.util.Log;
import com.android.keyguard.KeyguardUpdateMonitor;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

public class BatteryControllerImpl
  extends BroadcastReceiver
  implements BatteryController
{
  private static final boolean DEBUG = Log.isLoggable("BatteryController", 3);
  private int mBatteryStyle = 0;
  private final ArrayList<BatteryController.BatteryStateChangeCallback> mChangeCallbacks = new ArrayList();
  protected boolean mCharged;
  protected boolean mCharging;
  private final Context mContext;
  private boolean mDemoMode;
  private boolean mFastcharge = false;
  private final Handler mHandler;
  private boolean mHasReceivedBattery = false;
  protected int mLevel;
  protected boolean mPluggedIn;
  private final PowerManager mPowerManager;
  protected boolean mPowerSave;
  private final SettingObserver mSettingObserver = new SettingObserver();
  private boolean mShowPercent = false;
  private boolean mTestmode = false;
  
  public BatteryControllerImpl(Context paramContext)
  {
    this.mContext = paramContext;
    this.mHandler = new Handler();
    this.mPowerManager = ((PowerManager)paramContext.getSystemService("power"));
    registerReceiver();
    updatePowerSave();
  }
  
  private void fireBatteryStylechange()
  {
    int j = this.mChangeCallbacks.size();
    Log.i("BatteryController", " fireBatteryStylechange mShowPercent:" + this.mShowPercent + " mBatteryStyle:" + this.mBatteryStyle);
    int i = 0;
    for (;;)
    {
      if (i < j) {
        try
        {
          ((BatteryController.BatteryStateChangeCallback)this.mChangeCallbacks.get(i)).onBatteryPercentShowChange(this.mShowPercent);
          ((BatteryController.BatteryStateChangeCallback)this.mChangeCallbacks.get(i)).onBatteryStyleChanged(this.mBatteryStyle);
          i += 1;
        }
        catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
        {
          for (;;)
          {
            Log.i("BatteryController", " fireBatteryStylechange:" + localIndexOutOfBoundsException.getMessage());
          }
        }
      }
    }
  }
  
  private void firePowerSaveChanged()
  {
    Log.i("BatteryController", " firePowerSaveChanged mPowerSave:" + this.mPowerSave);
    synchronized (this.mChangeCallbacks)
    {
      int j = this.mChangeCallbacks.size();
      int i = 0;
      while (i < j)
      {
        ((BatteryController.BatteryStateChangeCallback)this.mChangeCallbacks.get(i)).onPowerSaveChanged(this.mPowerSave);
        i += 1;
      }
      return;
    }
  }
  
  private void registerReceiver()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.BATTERY_CHANGED");
    localIntentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
    localIntentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGING");
    localIntentFilter.addAction("com.android.systemui.BATTERY_LEVEL_TEST");
    localIntentFilter.addAction("android.intent.action.BOOT_COMPLETED");
    this.mContext.registerReceiver(this, localIntentFilter);
    this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("status_bar_show_battery_percent"), false, this.mSettingObserver, -1);
    this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("status_bar_battery_style"), false, this.mSettingObserver, -1);
    updateBatteryStyle();
  }
  
  private void setPowerSave(boolean paramBoolean)
  {
    if (paramBoolean == this.mPowerSave) {
      return;
    }
    this.mPowerSave = paramBoolean;
    StringBuilder localStringBuilder = new StringBuilder().append("Power save is ");
    if (this.mPowerSave) {}
    for (String str = "on";; str = "off")
    {
      Log.d("BatteryController", str);
      firePowerSaveChanged();
      return;
    }
  }
  
  private void updateBatteryStyle()
  {
    if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "status_bar_show_battery_percent", 0, KeyguardUpdateMonitor.getCurrentUser()) != 0) {}
    for (boolean bool = true;; bool = false)
    {
      this.mShowPercent = bool;
      this.mBatteryStyle = Settings.System.getIntForUser(this.mContext.getContentResolver(), "status_bar_battery_style", 0, KeyguardUpdateMonitor.getCurrentUser());
      fireBatteryStylechange();
      return;
    }
  }
  
  private void updatePowerSave()
  {
    setPowerSave(this.mPowerManager.isPowerSaveMode());
  }
  
  public void addStateChangedCallback(BatteryController.BatteryStateChangeCallback paramBatteryStateChangeCallback)
  {
    synchronized (this.mChangeCallbacks)
    {
      this.mChangeCallbacks.add(paramBatteryStateChangeCallback);
      if (!this.mHasReceivedBattery) {
        return;
      }
    }
    paramBatteryStateChangeCallback.onBatteryLevelChanged(this.mLevel, this.mPluggedIn, this.mCharging);
    paramBatteryStateChangeCallback.onPowerSaveChanged(this.mPowerSave);
    paramBatteryStateChangeCallback.onFastChargeChanged(this.mFastcharge);
    paramBatteryStateChangeCallback.onBatteryStyleChanged(this.mBatteryStyle);
    paramBatteryStateChangeCallback.onBatteryPercentShowChange(this.mShowPercent);
  }
  
  public void dispatchDemoCommand(String paramString, Bundle paramBundle)
  {
    if ((!this.mDemoMode) && (paramString.equals("enter")))
    {
      this.mDemoMode = true;
      this.mContext.unregisterReceiver(this);
    }
    do
    {
      return;
      if ((this.mDemoMode) && (paramString.equals("exit")))
      {
        this.mDemoMode = false;
        registerReceiver();
        updatePowerSave();
        return;
      }
    } while ((!this.mDemoMode) || (!paramString.equals("battery")));
    paramString = paramBundle.getString("level");
    paramBundle = paramBundle.getString("plugged");
    if (paramString != null) {
      this.mLevel = Math.min(Math.max(Integer.parseInt(paramString), 0), 100);
    }
    if (paramBundle != null) {
      this.mPluggedIn = Boolean.parseBoolean(paramBundle);
    }
    fireBatteryLevelChanged();
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("BatteryController state:");
    paramPrintWriter.print("  mLevel=");
    paramPrintWriter.println(this.mLevel);
    paramPrintWriter.print("  mPluggedIn=");
    paramPrintWriter.println(this.mPluggedIn);
    paramPrintWriter.print("  mCharging=");
    paramPrintWriter.println(this.mCharging);
    paramPrintWriter.print("  mCharged=");
    paramPrintWriter.println(this.mCharged);
    paramPrintWriter.print("  mPowerSave=");
    paramPrintWriter.println(this.mPowerSave);
    paramPrintWriter.print("  mShowPercent=");
    paramPrintWriter.println(this.mShowPercent);
    paramPrintWriter.print("  mBatteryStyle=");
    paramPrintWriter.println(this.mBatteryStyle);
  }
  
  protected void fireBatteryLevelChanged()
  {
    Log.i("BatteryController", " fireBatteryLevelChanged mLevel:" + this.mLevel + " PluggedIn:" + this.mPluggedIn + " Charging:" + this.mCharging + " Fastcharge:" + this.mFastcharge + " show:" + this.mShowPercent + " style:" + this.mBatteryStyle);
    synchronized (this.mChangeCallbacks)
    {
      int j = this.mChangeCallbacks.size();
      int i = 0;
      while (i < j)
      {
        ((BatteryController.BatteryStateChangeCallback)this.mChangeCallbacks.get(i)).onBatteryLevelChanged(this.mLevel, this.mPluggedIn, this.mCharging);
        ((BatteryController.BatteryStateChangeCallback)this.mChangeCallbacks.get(i)).onFastChargeChanged(this.mFastcharge);
        i += 1;
      }
      return;
    }
  }
  
  public boolean isPowerSave()
  {
    return this.mPowerSave;
  }
  
  public void onReceive(final Context paramContext, Intent paramIntent)
  {
    boolean bool2 = true;
    String str = paramIntent.getAction();
    boolean bool1;
    if (str.equals("android.intent.action.BATTERY_CHANGED")) {
      if ((!this.mTestmode) || (paramIntent.getBooleanExtra("testmode", false)))
      {
        int i = 0;
        if (!this.mHasReceivedBattery) {
          i = 1;
        }
        this.mHasReceivedBattery = true;
        this.mLevel = ((int)(paramIntent.getIntExtra("level", 0) * 100.0F / paramIntent.getIntExtra("scale", 100)));
        if (paramIntent.getIntExtra("plugged", 0) == 0) {
          break label180;
        }
        bool1 = true;
        this.mPluggedIn = bool1;
        int j = paramIntent.getIntExtra("status", 1);
        if (j != 5) {
          break label186;
        }
        bool1 = true;
        label121:
        this.mCharged = bool1;
        bool1 = bool2;
        if (!this.mCharged)
        {
          if (j != 2) {
            break label192;
          }
          bool1 = bool2;
        }
        label148:
        this.mCharging = bool1;
        this.mFastcharge = paramIntent.getBooleanExtra("fastcharge_status", false);
        fireBatteryLevelChanged();
        if (i != 0) {
          fireBatteryStylechange();
        }
      }
    }
    label180:
    label186:
    label192:
    do
    {
      return;
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label121;
      bool1 = false;
      break label148;
      if (str.equals("android.os.action.POWER_SAVE_MODE_CHANGED"))
      {
        updatePowerSave();
        return;
      }
      if (str.equals("android.os.action.POWER_SAVE_MODE_CHANGING"))
      {
        setPowerSave(paramIntent.getBooleanExtra("mode", false));
        return;
      }
      if (str.equals("android.intent.action.BOOT_COMPLETED"))
      {
        updateBatteryStyle();
        return;
      }
    } while (!str.equals("com.android.systemui.BATTERY_LEVEL_TEST"));
    this.mTestmode = true;
    this.mHandler.post(new Runnable()
    {
      int curLevel = 0;
      Intent dummy = new Intent("android.intent.action.BATTERY_CHANGED");
      int incr = 1;
      int saveLevel = BatteryControllerImpl.this.mLevel;
      boolean savePlugged = BatteryControllerImpl.this.mPluggedIn;
      
      public void run()
      {
        int i = 0;
        if (this.curLevel < 0)
        {
          BatteryControllerImpl.-set0(BatteryControllerImpl.this, false);
          this.dummy.putExtra("level", this.saveLevel);
          this.dummy.putExtra("plugged", this.savePlugged);
          this.dummy.putExtra("testmode", false);
        }
        for (;;)
        {
          paramContext.sendBroadcast(this.dummy);
          if (BatteryControllerImpl.-get1(BatteryControllerImpl.this)) {
            break;
          }
          return;
          this.dummy.putExtra("level", this.curLevel);
          Intent localIntent = this.dummy;
          if (this.incr > 0) {
            i = 1;
          }
          localIntent.putExtra("plugged", i);
          this.dummy.putExtra("testmode", true);
        }
        this.curLevel += this.incr;
        if (this.curLevel == 100) {
          this.incr *= -1;
        }
        BatteryControllerImpl.-get0(BatteryControllerImpl.this).postDelayed(this, 200L);
      }
    });
  }
  
  public void removeStateChangedCallback(BatteryController.BatteryStateChangeCallback paramBatteryStateChangeCallback)
  {
    synchronized (this.mChangeCallbacks)
    {
      this.mChangeCallbacks.remove(paramBatteryStateChangeCallback);
      return;
    }
  }
  
  public void setPowerSaveMode(boolean paramBoolean)
  {
    this.mPowerManager.setPowerSaveMode(paramBoolean);
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
      BatteryControllerImpl.-wrap0(BatteryControllerImpl.this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\BatteryControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */