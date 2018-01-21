package com.android.systemui.power;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Prefs;
import com.android.systemui.SystemUI;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;

public class PowerUI
  extends SystemUI
{
  static final boolean DEBUG = Log.isLoggable("PowerUI", 3);
  private int mBatteryLevel = 100;
  private int mBatteryStatus = 1;
  private boolean mEnteredPowerSave = false;
  private final Handler mHandler = new Handler();
  private int mInvalidCharger = 0;
  private int mLowBatteryAlertCloseLevel;
  private final int[] mLowBatteryReminderLevels = new int[2];
  private int mPlugType = 0;
  private PowerManager mPowerManager;
  private final Receiver mReceiver = new Receiver(null);
  private long mScreenOffTime = -1L;
  private boolean mSelfChange = false;
  private boolean mSelfChangeRestore = false;
  ScreenTimeoutMap mTimeoutMap = new ScreenTimeoutMap();
  private int mUser = 0;
  private WarningsUI mWarnings;
  
  private int findBatteryLevelBucket(int paramInt)
  {
    if (paramInt >= this.mLowBatteryAlertCloseLevel) {
      return 1;
    }
    if (paramInt > this.mLowBatteryReminderLevels[0]) {
      return 0;
    }
    int i = this.mLowBatteryReminderLevels.length - 1;
    while (i >= 0)
    {
      if (paramInt <= this.mLowBatteryReminderLevels[i]) {
        return -1 - i;
      }
      i -= 1;
    }
    throw new RuntimeException("not possible!");
  }
  
  private void setSaverMode(boolean paramBoolean)
  {
    this.mWarnings.showSaverMode(paramBoolean);
  }
  
  private void updatePowerSavingSettings()
  {
    this.mUser = KeyguardUpdateMonitor.getCurrentUser();
    this.mSelfChange = true;
    if (this.mPowerManager.isPowerSaveMode())
    {
      if (this.mEnteredPowerSave)
      {
        Log.d("PowerUI", "updatePowerSavingSettings:Already in PowerSaving Mode:ignore it");
        return;
      }
      this.mEnteredPowerSave = true;
      i = -1;
      if (!this.mTimeoutMap.contains(this.mUser))
      {
        i = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_off_timeout", 30000, this.mUser);
        this.mTimeoutMap.put(this.mUser, i);
      }
      Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_off_timeout", 30000, this.mUser);
      Log.d("PowerUI", "updatePowerSavingSettings:Enter PowerSaving Mode: timeout=" + i + ", user=" + this.mUser);
      return;
    }
    this.mEnteredPowerSave = false;
    int i = this.mTimeoutMap.remove(this.mUser);
    if (i != -1) {
      Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_off_timeout", i, this.mUser);
    }
    Log.d("PowerUI", "updatePowerSavingSettings:Leave PowerSaving Mode: timeout=" + i + ", user=" + this.mUser);
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.print("mLowBatteryAlertCloseLevel=");
    paramPrintWriter.println(this.mLowBatteryAlertCloseLevel);
    paramPrintWriter.print("mLowBatteryReminderLevels=");
    paramPrintWriter.println(Arrays.toString(this.mLowBatteryReminderLevels));
    paramPrintWriter.print("mBatteryLevel=");
    paramPrintWriter.println(Integer.toString(this.mBatteryLevel));
    paramPrintWriter.print("mBatteryStatus=");
    paramPrintWriter.println(Integer.toString(this.mBatteryStatus));
    paramPrintWriter.print("mPlugType=");
    paramPrintWriter.println(Integer.toString(this.mPlugType));
    paramPrintWriter.print("mInvalidCharger=");
    paramPrintWriter.println(Integer.toString(this.mInvalidCharger));
    paramPrintWriter.print("mScreenOffTime=");
    paramPrintWriter.print(this.mScreenOffTime);
    if (this.mScreenOffTime >= 0L)
    {
      paramPrintWriter.print(" (");
      paramPrintWriter.print(SystemClock.elapsedRealtime() - this.mScreenOffTime);
      paramPrintWriter.print(" ago)");
    }
    paramPrintWriter.println();
    paramPrintWriter.print("soundTimeout=");
    paramPrintWriter.println(Settings.Global.getInt(this.mContext.getContentResolver(), "low_battery_sound_timeout", 0));
    paramPrintWriter.print("bucket: ");
    paramPrintWriter.println(Integer.toString(findBatteryLevelBucket(this.mBatteryLevel)));
    this.mWarnings.dump(paramPrintWriter);
  }
  
  public void start()
  {
    this.mPowerManager = ((PowerManager)this.mContext.getSystemService("power"));
    if (this.mPowerManager.isScreenOn()) {}
    for (long l = -1L;; l = SystemClock.elapsedRealtime())
    {
      this.mScreenOffTime = l;
      this.mWarnings = new PowerNotificationWarnings(this.mContext, (PhoneStatusBar)getComponent(PhoneStatusBar.class));
      ContentObserver local1 = new ContentObserver(this.mHandler)
      {
        public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
        {
          if (Settings.Global.getUriFor("low_power_trigger_level").equals(paramAnonymousUri)) {
            PowerUI.this.updateBatteryWarningLevels();
          }
          int i;
          do
          {
            do
            {
              do
              {
                return;
              } while (!Settings.System.getUriFor("screen_off_timeout").equals(paramAnonymousUri));
              Log.d("PowerUI", "onChange:SCREEN_OFF_TIMEOUT:mSelfChange=" + PowerUI.-get9(PowerUI.this) + ", mSelfChangeRestore=" + PowerUI.-get10(PowerUI.this));
            } while (!PowerUI.-get7(PowerUI.this).isPowerSaveMode());
            if (PowerUI.-get9(PowerUI.this))
            {
              PowerUI.-set6(PowerUI.this, false);
              return;
            }
            if (PowerUI.-get10(PowerUI.this))
            {
              PowerUI.-set7(PowerUI.this, false);
              return;
            }
            i = PowerUI.this.mTimeoutMap.remove(PowerUI.-get11(PowerUI.this));
          } while (i == -1);
          Log.d("PowerUI", "SettingsObserver:onChange:User changed the timeout during power saving mode: timeout=" + i);
        }
      };
      ContentResolver localContentResolver = this.mContext.getContentResolver();
      localContentResolver.registerContentObserver(Settings.Global.getUriFor("low_power_trigger_level"), false, local1, -1);
      localContentResolver.registerContentObserver(Settings.System.getUriFor("screen_off_timeout"), false, local1, -1);
      this.mUser = KeyguardUpdateMonitor.getCurrentUser();
      this.mTimeoutMap.parseData(Prefs.getString(this.mContext, "PowerSavingTimeoutBackupAll", ""));
      updatePowerSavingSettings();
      updateBatteryWarningLevels();
      this.mReceiver.init();
      return;
    }
  }
  
  void updateBatteryWarningLevels()
  {
    int k = this.mContext.getResources().getInteger(17694806);
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    int j = this.mContext.getResources().getInteger(17694808);
    int m = Settings.Global.getInt(localContentResolver, "low_power_trigger_level", j);
    int i = m;
    if (m == 0) {
      i = j;
    }
    j = i;
    if (i < k) {
      j = k;
    }
    this.mLowBatteryReminderLevels[0] = j;
    this.mLowBatteryReminderLevels[1] = k;
    this.mLowBatteryAlertCloseLevel = (this.mLowBatteryReminderLevels[0] + this.mContext.getResources().getInteger(17694809));
  }
  
  private final class Receiver
    extends BroadcastReceiver
  {
    private Receiver() {}
    
    private void updateSaverMode()
    {
      PowerUI.-wrap1(PowerUI.this, PowerUI.-get7(PowerUI.this).isPowerSaveMode());
    }
    
    public void init()
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.BATTERY_CHANGED");
      localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
      localIntentFilter.addAction("android.intent.action.SCREEN_ON");
      localIntentFilter.addAction("android.intent.action.USER_SWITCHED");
      localIntentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGING");
      localIntentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
      PowerUI.this.mContext.registerReceiver(this, localIntentFilter, null, PowerUI.-get2(PowerUI.this));
      updateSaverMode();
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = paramIntent.getAction();
      boolean bool2;
      boolean bool1;
      int n;
      int i1;
      if (paramContext.equals("android.intent.action.BATTERY_CHANGED"))
      {
        int i = PowerUI.-get0(PowerUI.this);
        PowerUI.-set0(PowerUI.this, paramIntent.getIntExtra("level", 100));
        int j = PowerUI.-get1(PowerUI.this);
        PowerUI.-set1(PowerUI.this, paramIntent.getIntExtra("status", 1));
        int k = PowerUI.-get6(PowerUI.this);
        PowerUI.-set4(PowerUI.this, paramIntent.getIntExtra("plugged", 1));
        int m = PowerUI.-get3(PowerUI.this);
        PowerUI.-set3(PowerUI.this, paramIntent.getIntExtra("invalid_charger", 0));
        if (PowerUI.-get6(PowerUI.this) != 0)
        {
          bool2 = true;
          if (k == 0) {
            break label533;
          }
        }
        label533:
        for (bool1 = true;; bool1 = false)
        {
          n = PowerUI.-wrap0(PowerUI.this, i);
          i1 = PowerUI.-wrap0(PowerUI.this, PowerUI.-get0(PowerUI.this));
          if (PowerUI.DEBUG)
          {
            Slog.d("PowerUI", "buckets   ....." + PowerUI.-get4(PowerUI.this) + " .. " + PowerUI.-get5(PowerUI.this)[0] + " .. " + PowerUI.-get5(PowerUI.this)[1]);
            Slog.d("PowerUI", "level          " + i + " --> " + PowerUI.-get0(PowerUI.this));
            Slog.d("PowerUI", "status         " + j + " --> " + PowerUI.-get1(PowerUI.this));
            Slog.d("PowerUI", "plugType       " + k + " --> " + PowerUI.-get6(PowerUI.this));
            Slog.d("PowerUI", "invalidCharger " + m + " --> " + PowerUI.-get3(PowerUI.this));
            Slog.d("PowerUI", "bucket         " + n + " --> " + i1);
            Slog.d("PowerUI", "plugged        " + bool1 + " --> " + bool2);
          }
          PowerUI.-get12(PowerUI.this).update(PowerUI.-get0(PowerUI.this), i1, PowerUI.-get8(PowerUI.this));
          if ((m != 0) || (PowerUI.-get3(PowerUI.this) == 0)) {
            break label539;
          }
          Slog.d("PowerUI", "showing invalid charger warning");
          PowerUI.-get12(PowerUI.this).showInvalidChargerWarning();
          return;
          bool2 = false;
          break;
        }
        label539:
        if ((m != 0) && (PowerUI.-get3(PowerUI.this) == 0)) {
          PowerUI.-get12(PowerUI.this).dismissInvalidChargerWarning();
        }
      }
      label622:
      do
      {
        break label622;
        boolean bool3 = PowerUI.-get7(PowerUI.this).isPowerSaveMode();
        if ((bool2) || (bool3)) {}
        for (;;)
        {
          if ((bool3) || (bool2) || ((i1 > n) && (i1 > 0)))
          {
            PowerUI.-get12(PowerUI.this).dismissLowBatteryWarning();
            return;
            if (!PowerUI.-get12(PowerUI.this).isInvalidChargerWarningShowing()) {
              break;
            }
            return;
            if (((i1 < n) || (bool1)) && (PowerUI.-get1(PowerUI.this) != 1) && (i1 < 0))
            {
              if (i1 == n) {}
              for (;;)
              {
                PowerUI.-get12(PowerUI.this).showLowBatteryWarning(bool1);
                return;
                bool1 = true;
              }
            }
          }
        }
        PowerUI.-get12(PowerUI.this).updateLowBatteryWarning();
        return;
        if ("android.intent.action.SCREEN_OFF".equals(paramContext))
        {
          PowerUI.-set5(PowerUI.this, SystemClock.elapsedRealtime());
          return;
        }
        if ("android.intent.action.SCREEN_ON".equals(paramContext))
        {
          PowerUI.-set5(PowerUI.this, -1L);
          return;
        }
        if ("android.intent.action.USER_SWITCHED".equals(paramContext))
        {
          PowerUI.-get12(PowerUI.this).userSwitched();
          PowerUI.-set2(PowerUI.this, false);
          PowerUI.-wrap2(PowerUI.this);
          return;
        }
        if ("android.os.action.POWER_SAVE_MODE_CHANGED".equals(paramContext))
        {
          updateSaverMode();
          return;
        }
        if (!"android.os.action.POWER_SAVE_MODE_CHANGING".equals(paramContext)) {
          break;
        }
        PowerUI.-wrap1(PowerUI.this, paramIntent.getBooleanExtra("mode", false));
        PowerUI.-wrap2(PowerUI.this);
      } while (!PowerUI.-get7(PowerUI.this).isPowerSaveMode());
      PowerUI.-get12(PowerUI.this).dismissLowBatteryWarning();
      return;
      Slog.w("PowerUI", "unknown intent: " + paramIntent);
    }
  }
  
  public class ScreenTimeoutMap
  {
    private ArrayMap<Integer, Integer> mMap = new ArrayMap();
    
    public ScreenTimeoutMap() {}
    
    private void saveToPrefs()
    {
      Prefs.putString(PowerUI.this.mContext, "PowerSavingTimeoutBackupAll", toString());
    }
    
    public boolean contains(int paramInt)
    {
      return this.mMap.containsKey(Integer.valueOf(paramInt));
    }
    
    public void parseData(String paramString)
    {
      Log.d("PowerUI", "parseData=" + paramString);
      this.mMap.clear();
      if (paramString.equals(""))
      {
        Log.d("PowerUI", "no data");
        return;
      }
      try
      {
        paramString = paramString.split("[;]");
        int i = 0;
        int j = paramString.length;
        while (i < j)
        {
          String[] arrayOfString = paramString[i].split("[,]");
          Log.d("PowerUI", "parseData:load userID=" + arrayOfString[0] + ", timeout=" + arrayOfString[1]);
          this.mMap.put(Integer.valueOf(Integer.parseInt(arrayOfString[0])), Integer.valueOf(Integer.parseInt(arrayOfString[1])));
          i += 1;
        }
        return;
      }
      catch (Exception paramString)
      {
        Log.e("PowerUI", "Error while parsing the data");
      }
    }
    
    public void put(int paramInt1, int paramInt2)
    {
      if (!this.mMap.containsKey(Integer.valueOf(paramInt1)))
      {
        this.mMap.put(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
        saveToPrefs();
        return;
      }
      Log.d("PowerUI", "Already has backup timeout, skip it");
    }
    
    public int remove(int paramInt)
    {
      if (this.mMap.containsKey(Integer.valueOf(paramInt)))
      {
        paramInt = ((Integer)this.mMap.remove(Integer.valueOf(paramInt))).intValue();
        saveToPrefs();
        return paramInt;
      }
      Log.d("PowerUI", "No key in remove");
      return -1;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      int i = 0;
      int j = this.mMap.size();
      while (i < j)
      {
        localStringBuilder.append(this.mMap.keyAt(i));
        localStringBuilder.append(',');
        localStringBuilder.append(this.mMap.valueAt(i));
        if (i < j - 1) {
          localStringBuilder.append(';');
        }
        i += 1;
      }
      return localStringBuilder.toString();
    }
  }
  
  public static abstract interface WarningsUI
  {
    public abstract void dismissInvalidChargerWarning();
    
    public abstract void dismissLowBatteryWarning();
    
    public abstract void dump(PrintWriter paramPrintWriter);
    
    public abstract boolean isInvalidChargerWarningShowing();
    
    public abstract void showInvalidChargerWarning();
    
    public abstract void showLowBatteryWarning(boolean paramBoolean);
    
    public abstract void showSaverMode(boolean paramBoolean);
    
    public abstract void update(int paramInt1, int paramInt2, long paramLong);
    
    public abstract void updateLowBatteryWarning();
    
    public abstract void userSwitched();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\power\PowerUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */