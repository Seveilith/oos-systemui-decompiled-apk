package com.android.systemui.volume;

import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.Log;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Prefs;
import com.android.systemui.SystemUI;
import com.android.systemui.qs.tiles.DndTile;
import com.android.systemui.statusbar.ServiceMonitor;
import com.android.systemui.statusbar.ServiceMonitor.Callbacks;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.ZenModeControllerImpl;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class VolumeUI
  extends SystemUI
{
  private static boolean LOGD = Log.isLoggable("VolumeUI", 3);
  private static int[] sZenModeMap = { 3, 2, 1, 1 };
  private AudioManager mAudioManager;
  private boolean mBootCompleted;
  private boolean mEnabled;
  private final Handler mHandler = new Handler();
  private int mLastThreeKeyStatus = 0;
  private MediaSessionManager mMediaSessionManager;
  private NotificationManager mNotificationManager;
  private final Receiver mReceiver = new Receiver(null);
  private final RestorationNotification mRestorationNotification = new RestorationNotification(null);
  private final SettingObserver mSettingObserver = new SettingObserver();
  private int mVibrateWhenMute = 0;
  private VolumeDialogComponent mVolumeComponent;
  private ServiceMonitor mVolumeControllerService;
  private final ContentObserver mZenModeObserver = new ContentObserver(this.mHandler)
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      int i;
      if (Settings.Global.getInt(VolumeUI.this.mContext.getContentResolver(), "device_provisioned", 0) != 0)
      {
        i = 1;
        if ((i != 0) && (VolumeUI.-get1(VolumeUI.this)))
        {
          i = Util.getThreeKeyStatus(VolumeUI.this.mContext);
          int j = Settings.Global.getInt(VolumeUI.this.mContext.getContentResolver(), "zen_mode", 0);
          j = VolumeUI.-wrap1(VolumeUI.this, Util.getCorrectZenMode(j, i, VolumeUI.-get6(VolumeUI.this)));
          Log.d("VolumeUI", "mZenModeObserver:zenMode=" + j + ", threeKeyStatus=" + i + ", mLastThreeKeyStatus=" + VolumeUI.-get3(VolumeUI.this));
          Settings.Global.putInt(VolumeUI.this.mContext.getContentResolver(), "correct_zen_mode_aod", j);
          if (j != i) {
            VolumeUI.-set0(VolumeUI.this, i);
          }
          if ((j == i) && (i != VolumeUI.-get3(VolumeUI.this)))
          {
            if (VolumeUI.-get3(VolumeUI.this) <= 0) {
              break label214;
            }
            VolumeUI.-get7(VolumeUI.this).showVolumeDialogForTriKey();
          }
        }
      }
      for (;;)
      {
        VolumeUI.-set0(VolumeUI.this, i);
        return;
        i = 0;
        break;
        label214:
        Log.i("VolumeUI", " Don't show dialog when threekey first change");
      }
    }
  };
  
  private String getAppLabel(ComponentName paramComponentName)
  {
    paramComponentName = paramComponentName.getPackageName();
    try
    {
      Object localObject = this.mContext.getPackageManager().getApplicationInfo(paramComponentName, 0);
      localObject = this.mContext.getPackageManager().getApplicationLabel((ApplicationInfo)localObject).toString();
      boolean bool = TextUtils.isEmpty((CharSequence)localObject);
      if (!bool) {
        return (String)localObject;
      }
    }
    catch (Exception localException)
    {
      Log.w("VolumeUI", "Error loading app label", localException);
    }
    return paramComponentName;
  }
  
  private VolumeComponent getVolumeComponent()
  {
    return this.mVolumeComponent;
  }
  
  private void setDefaultVolumeController(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      DndTile.setVisible(this.mContext, true);
      if (LOGD) {
        Log.d("VolumeUI", "Registering default volume controller");
      }
      getVolumeComponent().register();
      return;
    }
    if (LOGD) {
      Log.d("VolumeUI", "Unregistering default volume controller");
    }
    this.mAudioManager.setVolumeController(null);
    this.mMediaSessionManager.setRemoteVolumeController(null);
  }
  
  private void showServiceActivationDialog(final ComponentName paramComponentName)
  {
    SystemUIDialog localSystemUIDialog = new SystemUIDialog(this.mContext);
    localSystemUIDialog.setMessage(this.mContext.getString(2131690438, new Object[] { getAppLabel(paramComponentName) }));
    localSystemUIDialog.setPositiveButton(2131690439, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        VolumeUI.-get8(VolumeUI.this).setComponent(paramComponentName);
      }
    });
    localSystemUIDialog.setNegativeButton(2131690440, null);
    localSystemUIDialog.show();
  }
  
  private int zenModeToThreeKey(int paramInt)
  {
    if (Settings.Global.isValidZenMode(paramInt)) {
      return sZenModeMap[paramInt];
    }
    return sZenModeMap[0];
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.print("mEnabled=");
    paramPrintWriter.println(this.mEnabled);
    if (!this.mEnabled) {
      return;
    }
    paramPrintWriter.print("mVolumeControllerService=");
    paramPrintWriter.println(this.mVolumeControllerService.getComponent());
    getVolumeComponent().dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
  }
  
  public void onBootCompleted()
  {
    this.mBootCompleted = true;
    this.mLastThreeKeyStatus = Util.getThreeKeyStatus(this.mContext);
    Log.d("VolumeUI", "onBootCompleted mLastThreeKeyStatus:" + this.mLastThreeKeyStatus);
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (!this.mEnabled) {
      return;
    }
    getVolumeComponent().onConfigurationChanged(paramConfiguration);
  }
  
  public void start()
  {
    this.mEnabled = this.mContext.getResources().getBoolean(2131558435);
    if (!this.mEnabled) {
      return;
    }
    this.mAudioManager = ((AudioManager)this.mContext.getSystemService("audio"));
    this.mNotificationManager = ((NotificationManager)this.mContext.getSystemService("notification"));
    this.mMediaSessionManager = ((MediaSessionManager)this.mContext.getSystemService("media_session"));
    ZenModeControllerImpl localZenModeControllerImpl = new ZenModeControllerImpl(this.mContext, this.mHandler);
    this.mVolumeComponent = new VolumeDialogComponent(this, this.mContext, null, localZenModeControllerImpl);
    putComponent(VolumeComponent.class, getVolumeComponent());
    this.mReceiver.start();
    this.mVolumeControllerService = new ServiceMonitor("VolumeUI", LOGD, this.mContext, "volume_controller_service_component", new ServiceMonitorCallbacks(null));
    this.mVolumeControllerService.start();
    this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("zen_mode"), false, this.mZenModeObserver);
    this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("three_Key_mode"), false, this.mZenModeObserver);
    this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("oem_vibrate_under_silent"), false, this.mSettingObserver, -1);
    this.mVibrateWhenMute = Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_vibrate_under_silent", 0, KeyguardUpdateMonitor.getCurrentUser());
  }
  
  private final class Receiver
    extends BroadcastReceiver
  {
    private Receiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = paramIntent.getAction();
      if ("com.android.systemui.PREF".equals(paramContext))
      {
        paramContext = paramIntent.getStringExtra("key");
        if ((paramContext != null) && (paramIntent.getExtras() != null))
        {
          paramIntent = paramIntent.getExtras().get("value");
          if (paramIntent != null) {
            break label58;
          }
          Prefs.remove(VolumeUI.this.mContext, paramContext);
        }
        label58:
        do
        {
          return;
          if ((paramIntent instanceof Boolean))
          {
            Prefs.putBoolean(VolumeUI.this.mContext, paramContext, ((Boolean)paramIntent).booleanValue());
            return;
          }
          if ((paramIntent instanceof Integer))
          {
            Prefs.putInt(VolumeUI.this.mContext, paramContext, ((Integer)paramIntent).intValue());
            return;
          }
        } while (!(paramIntent instanceof Long));
        Prefs.putLong(VolumeUI.this.mContext, paramContext, ((Long)paramIntent).longValue());
        return;
      }
      paramIntent = (ComponentName)paramIntent.getParcelableExtra("component");
      if (paramIntent != null) {}
      for (boolean bool = paramIntent.equals(VolumeUI.-get8(VolumeUI.this).getComponent());; bool = false)
      {
        if (("com.android.systemui.vui.ENABLE".equals(paramContext)) && (paramIntent != null) && (!bool)) {
          VolumeUI.-wrap4(VolumeUI.this, paramIntent);
        }
        if (("com.android.systemui.vui.DISABLE".equals(paramContext)) && (paramIntent != null) && (bool)) {
          VolumeUI.-get8(VolumeUI.this).setComponent(null);
        }
        return;
      }
    }
    
    public void start()
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("com.android.systemui.vui.ENABLE");
      localIntentFilter.addAction("com.android.systemui.vui.DISABLE");
      localIntentFilter.addAction("com.android.systemui.PREF");
      VolumeUI.this.mContext.registerReceiver(this, localIntentFilter, null, VolumeUI.-get2(VolumeUI.this));
    }
  }
  
  private final class RestorationNotification
  {
    private RestorationNotification() {}
    
    public void hide()
    {
      VolumeUI.-get4(VolumeUI.this).cancel(2131951671);
    }
    
    public void show()
    {
      Object localObject = VolumeUI.-get8(VolumeUI.this).getComponent();
      if (localObject == null)
      {
        Log.w("VolumeUI", "Not showing restoration notification, component not active");
        return;
      }
      Intent localIntent = new Intent("com.android.systemui.vui.DISABLE").putExtra("component", (Parcelable)localObject);
      localObject = new Notification.Builder(VolumeUI.this.mContext).setSmallIcon(2130837947).setWhen(0L).setShowWhen(false).setOngoing(true).setContentTitle(VolumeUI.this.mContext.getString(2131690441, new Object[] { VolumeUI.-wrap2(VolumeUI.this, (ComponentName)localObject) })).setContentText(VolumeUI.this.mContext.getString(2131690442)).setContentIntent(PendingIntent.getBroadcast(VolumeUI.this.mContext, 0, localIntent, 134217728)).setPriority(-2).setVisibility(1).setColor(VolumeUI.this.mContext.getColor(17170523));
      VolumeUI.overrideNotificationAppName(VolumeUI.this.mContext, (Notification.Builder)localObject);
      VolumeUI.-get4(VolumeUI.this).notify(2131951671, ((Notification.Builder)localObject).build());
    }
  }
  
  private final class ServiceMonitorCallbacks
    implements ServiceMonitor.Callbacks
  {
    private ServiceMonitorCallbacks() {}
    
    public void onNoService()
    {
      if (VolumeUI.-get0()) {
        Log.d("VolumeUI", "onNoService");
      }
      VolumeUI.-wrap3(VolumeUI.this, true);
      VolumeUI.-get5(VolumeUI.this).hide();
      if (!VolumeUI.-get8(VolumeUI.this).isPackageAvailable()) {
        VolumeUI.-get8(VolumeUI.this).setComponent(null);
      }
    }
    
    public long onServiceStartAttempt()
    {
      if (VolumeUI.-get0()) {
        Log.d("VolumeUI", "onServiceStartAttempt");
      }
      VolumeUI.-get8(VolumeUI.this).setComponent(VolumeUI.-get8(VolumeUI.this).getComponent());
      VolumeUI.-wrap3(VolumeUI.this, false);
      VolumeUI.-wrap0(VolumeUI.this).dismissNow();
      VolumeUI.-get5(VolumeUI.this).show();
      return 0L;
    }
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
      VolumeUI.-set1(VolumeUI.this, Settings.System.getIntForUser(VolumeUI.this.mContext.getContentResolver(), "oem_vibrate_under_silent", 0, KeyguardUpdateMonitor.getCurrentUser()));
      Log.i("VolumeUI", " SettingObserver mVibrateWhenMute:" + VolumeUI.-get6(VolumeUI.this));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\volume\VolumeUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */