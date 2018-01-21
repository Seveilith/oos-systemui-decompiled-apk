package com.android.systemui.power;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.util.Slog;
import com.android.systemui.SystemUI;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import java.io.PrintWriter;
import java.text.NumberFormat;

public class PowerNotificationWarnings
  implements PowerUI.WarningsUI
{
  private static final AudioAttributes AUDIO_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
  private static final boolean DEBUG = PowerUI.DEBUG;
  private static final String[] SHOWING_STRINGS = { "SHOWING_NOTHING", "SHOWING_WARNING", "SHOWING_SAVER", "SHOWING_INVALID_CHARGER" };
  private int mBatteryLevel;
  private int mBucket;
  private long mBucketDroppedNegativeTimeMs;
  private final Context mContext;
  private final Handler mHandler = new Handler();
  private boolean mInvalidCharger;
  private final NotificationManager mNoMan;
  private final Intent mOpenBatterySettings = settings("android.intent.action.POWER_USAGE_SUMMARY");
  private final Intent mOpenSaverSettings = settings("android.settings.BATTERY_SAVER_SETTINGS");
  private boolean mPlaySound;
  private final PowerManager mPowerMan;
  private final Receiver mReceiver = new Receiver(null);
  private boolean mSaver;
  private SystemUIDialog mSaverConfirmation;
  private long mScreenOffTime;
  private int mShowing;
  private final DialogInterface.OnClickListener mStartSaverMode = new DialogInterface.OnClickListener()
  {
    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
    {
      AsyncTask.execute(new Runnable()
      {
        public void run()
        {
          PowerNotificationWarnings.-wrap2(PowerNotificationWarnings.this, true);
        }
      });
    }
  };
  private boolean mWarning;
  
  public PowerNotificationWarnings(Context paramContext, PhoneStatusBar paramPhoneStatusBar)
  {
    this.mContext = paramContext;
    this.mNoMan = ((NotificationManager)paramContext.getSystemService("notification"));
    this.mPowerMan = ((PowerManager)paramContext.getSystemService("power"));
    this.mReceiver.init();
  }
  
  private void addStopSaverAction(Notification.Builder paramBuilder)
  {
    paramBuilder.addAction(0, this.mContext.getString(2131690396), pendingBroadcast("PNW.stopSaver"));
  }
  
  private void attachLowBatterySound(Notification.Builder paramBuilder)
  {
    Object localObject = this.mContext.getContentResolver();
    int i = Settings.Global.getInt((ContentResolver)localObject, "low_battery_sound_timeout", 0);
    long l = SystemClock.elapsedRealtime() - this.mScreenOffTime;
    if ((i > 0) && (this.mScreenOffTime > 0L) && (l > i))
    {
      Slog.i("PowerUI.Notification", "screen off too long (" + l + "ms, limit " + i + "ms): not waking up the user with low battery sound");
      return;
    }
    if (DEBUG) {
      Slog.d("PowerUI.Notification", "playing low battery sound. pick-a-doop!");
    }
    if (Settings.Global.getInt((ContentResolver)localObject, "power_sounds_enabled", 1) == 1)
    {
      localObject = Settings.Global.getString((ContentResolver)localObject, "low_battery_sound");
      if (localObject != null)
      {
        localObject = Uri.parse("file://" + (String)localObject);
        if (localObject != null)
        {
          paramBuilder.setSound((Uri)localObject, AUDIO_ATTRIBUTES);
          if (DEBUG) {
            Slog.d("PowerUI.Notification", "playing sound " + localObject);
          }
        }
      }
    }
  }
  
  private void dismissInvalidChargerNotification()
  {
    if (this.mInvalidCharger) {
      Slog.i("PowerUI.Notification", "dismissing invalid charger notification");
    }
    this.mInvalidCharger = false;
    updateNotification();
  }
  
  private void dismissLowBatteryNotification()
  {
    if (this.mWarning) {
      Slog.i("PowerUI.Notification", "dismissing low battery notification");
    }
    this.mWarning = false;
    updateNotification();
  }
  
  private void dismissSaverNotification()
  {
    if (this.mSaver) {
      Slog.i("PowerUI.Notification", "dismissing saver notification");
    }
    this.mSaver = false;
    updateNotification();
  }
  
  private boolean hasBatterySettings()
  {
    return this.mOpenBatterySettings.resolveActivity(this.mContext.getPackageManager()) != null;
  }
  
  private boolean hasSaverSettings()
  {
    return this.mOpenSaverSettings.resolveActivity(this.mContext.getPackageManager()) != null;
  }
  
  private PendingIntent pendingActivity(Intent paramIntent)
  {
    return PendingIntent.getActivityAsUser(this.mContext, 0, paramIntent, 0, null, UserHandle.CURRENT);
  }
  
  private PendingIntent pendingBroadcast(String paramString)
  {
    return PendingIntent.getBroadcastAsUser(this.mContext, 0, new Intent(paramString), 0, UserHandle.CURRENT);
  }
  
  private void setSaverMode(boolean paramBoolean)
  {
    this.mPowerMan.setPowerSaveMode(paramBoolean);
  }
  
  private static Intent settings(String paramString)
  {
    return new Intent(paramString).setFlags(1551892480);
  }
  
  private void showInvalidChargerNotification()
  {
    Object localObject = new Notification.Builder(this.mContext).setSmallIcon(2130837770).setWhen(0L).setShowWhen(false).setOngoing(true).setContentTitle(this.mContext.getString(2131690039)).setContentText(this.mContext.getString(2131690040)).setPriority(2).setVisibility(1).setColor(this.mContext.getColor(17170523));
    SystemUI.overrideNotificationAppName(this.mContext, (Notification.Builder)localObject);
    localObject = ((Notification.Builder)localObject).build();
    this.mNoMan.notifyAsUser("low_battery", 2131951668, (Notification)localObject, UserHandle.ALL);
  }
  
  private void showSaverNotification()
  {
    Notification.Builder localBuilder = new Notification.Builder(this.mContext).setSmallIcon(2130837771).setContentTitle(this.mContext.getString(2131690394)).setContentText(this.mContext.getString(2131690395)).setOngoing(true).setShowWhen(false).setVisibility(1).setColor(this.mContext.getColor(17170524));
    addStopSaverAction(localBuilder);
    if (hasSaverSettings()) {
      localBuilder.setContentIntent(pendingActivity(this.mOpenSaverSettings));
    }
    this.mNoMan.notifyAsUser("low_battery", 2131951668, localBuilder.build(), UserHandle.ALL);
  }
  
  private void showStartSaverConfirmation()
  {
    if (this.mSaverConfirmation != null) {
      return;
    }
    SystemUIDialog localSystemUIDialog = new SystemUIDialog(this.mContext);
    localSystemUIDialog.setTitle(2131690042);
    localSystemUIDialog.setMessage(17040845);
    localSystemUIDialog.setNegativeButton(17039360, null);
    localSystemUIDialog.setPositiveButton(2131690043, this.mStartSaverMode);
    localSystemUIDialog.setShowForAllUsers(true);
    localSystemUIDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
    {
      public void onDismiss(DialogInterface paramAnonymousDialogInterface)
      {
        PowerNotificationWarnings.-set0(PowerNotificationWarnings.this, null);
      }
    });
    localSystemUIDialog.show();
    this.mSaverConfirmation = localSystemUIDialog;
  }
  
  private void showWarningNotification()
  {
    int i;
    Object localObject;
    if (this.mSaver)
    {
      i = 2131690037;
      localObject = NumberFormat.getPercentInstance().format(this.mBatteryLevel / 100.0D);
      Bundle localBundle = new Bundle();
      localBundle.putBoolean("oneplus.shouldPeekInGameMode", true);
      localObject = new Notification.Builder(this.mContext).setSmallIcon(2130837770).setWhen(this.mBucketDroppedNegativeTimeMs).setShowWhen(false).setContentTitle(this.mContext.getString(2131690035)).setContentText(this.mContext.getString(i, new Object[] { localObject })).setOnlyAlertOnce(true).setDeleteIntent(pendingBroadcast("PNW.dismissedWarning")).setPriority(2).setVisibility(1).setColor(this.mContext.getColor(17170524)).setExtras(localBundle);
      if (hasBatterySettings()) {
        ((Notification.Builder)localObject).setContentIntent(pendingBroadcast("PNW.batterySettings"));
      }
      if (this.mSaver) {
        break label245;
      }
      ((Notification.Builder)localObject).addAction(0, this.mContext.getString(2131690044), pendingBroadcast("PNW.startSaver"));
    }
    for (;;)
    {
      if (this.mPlaySound)
      {
        attachLowBatterySound((Notification.Builder)localObject);
        this.mPlaySound = false;
      }
      SystemUI.overrideNotificationAppName(this.mContext, (Notification.Builder)localObject);
      this.mNoMan.notifyAsUser("low_battery", 2131951668, ((Notification.Builder)localObject).build(), UserHandle.ALL);
      return;
      i = 2131690036;
      break;
      label245:
      addStopSaverAction((Notification.Builder)localObject);
    }
  }
  
  private void updateNotification()
  {
    if (DEBUG) {
      Slog.d("PowerUI.Notification", "updateNotification mWarning=" + this.mWarning + " mPlaySound=" + this.mPlaySound + " mSaver=" + this.mSaver + " mInvalidCharger=" + this.mInvalidCharger);
    }
    if (this.mInvalidCharger)
    {
      showInvalidChargerNotification();
      this.mShowing = 3;
      return;
    }
    if (this.mWarning)
    {
      showWarningNotification();
      this.mShowing = 1;
      return;
    }
    if (this.mSaver)
    {
      showSaverNotification();
      this.mShowing = 2;
      return;
    }
    this.mNoMan.cancelAsUser("low_battery", 2131951668, UserHandle.ALL);
    this.mShowing = 0;
  }
  
  public void dismissInvalidChargerWarning()
  {
    dismissInvalidChargerNotification();
  }
  
  public void dismissLowBatteryWarning()
  {
    if (DEBUG) {
      Slog.d("PowerUI.Notification", "dismissing low battery warning: level=" + this.mBatteryLevel);
    }
    dismissLowBatteryNotification();
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    String str = null;
    paramPrintWriter.print("mSaver=");
    paramPrintWriter.println(this.mSaver);
    paramPrintWriter.print("mWarning=");
    paramPrintWriter.println(this.mWarning);
    paramPrintWriter.print("mPlaySound=");
    paramPrintWriter.println(this.mPlaySound);
    paramPrintWriter.print("mInvalidCharger=");
    paramPrintWriter.println(this.mInvalidCharger);
    paramPrintWriter.print("mShowing=");
    paramPrintWriter.println(SHOWING_STRINGS[this.mShowing]);
    paramPrintWriter.print("mSaverConfirmation=");
    if (this.mSaverConfirmation != null) {
      str = "not null";
    }
    paramPrintWriter.println(str);
  }
  
  public boolean isInvalidChargerWarningShowing()
  {
    return this.mInvalidCharger;
  }
  
  public void showInvalidChargerWarning()
  {
    this.mInvalidCharger = true;
    updateNotification();
  }
  
  public void showLowBatteryWarning(boolean paramBoolean)
  {
    Slog.i("PowerUI.Notification", "show low battery warning: level=" + this.mBatteryLevel + " [" + this.mBucket + "] playSound=" + paramBoolean);
    this.mPlaySound = paramBoolean;
    this.mWarning = true;
    updateNotification();
  }
  
  public void showSaverMode(boolean paramBoolean)
  {
    this.mSaver = paramBoolean;
    if ((this.mSaver) && (this.mSaverConfirmation != null)) {
      this.mSaverConfirmation.dismiss();
    }
    updateNotification();
  }
  
  public void update(int paramInt1, int paramInt2, long paramLong)
  {
    this.mBatteryLevel = paramInt1;
    if (paramInt2 >= 0) {
      this.mBucketDroppedNegativeTimeMs = 0L;
    }
    for (;;)
    {
      this.mBucket = paramInt2;
      this.mScreenOffTime = paramLong;
      return;
      if (paramInt2 < this.mBucket) {
        this.mBucketDroppedNegativeTimeMs = System.currentTimeMillis();
      }
    }
  }
  
  public void updateLowBatteryWarning()
  {
    updateNotification();
  }
  
  public void userSwitched()
  {
    updateNotification();
  }
  
  private final class Receiver
    extends BroadcastReceiver
  {
    private Receiver() {}
    
    public void init()
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("PNW.batterySettings");
      localIntentFilter.addAction("PNW.startSaver");
      localIntentFilter.addAction("PNW.stopSaver");
      localIntentFilter.addAction("PNW.dismissedWarning");
      PowerNotificationWarnings.-get0(PowerNotificationWarnings.this).registerReceiverAsUser(this, UserHandle.ALL, localIntentFilter, "android.permission.STATUS_BAR_SERVICE", PowerNotificationWarnings.-get1(PowerNotificationWarnings.this));
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = paramIntent.getAction();
      Slog.i("PowerUI.Notification", "Received " + paramContext);
      if (paramContext.equals("PNW.batterySettings"))
      {
        PowerNotificationWarnings.-wrap0(PowerNotificationWarnings.this);
        PowerNotificationWarnings.-get0(PowerNotificationWarnings.this).startActivityAsUser(PowerNotificationWarnings.-get2(PowerNotificationWarnings.this), UserHandle.CURRENT);
      }
      do
      {
        return;
        if (paramContext.equals("PNW.startSaver"))
        {
          PowerNotificationWarnings.-wrap0(PowerNotificationWarnings.this);
          PowerNotificationWarnings.-wrap3(PowerNotificationWarnings.this);
          return;
        }
        if (paramContext.equals("PNW.stopSaver"))
        {
          PowerNotificationWarnings.-wrap1(PowerNotificationWarnings.this);
          PowerNotificationWarnings.-wrap0(PowerNotificationWarnings.this);
          PowerNotificationWarnings.-wrap2(PowerNotificationWarnings.this, false);
          return;
        }
      } while (!paramContext.equals("PNW.dismissedWarning"));
      PowerNotificationWarnings.this.dismissLowBatteryWarning();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\power\PowerNotificationWarnings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */