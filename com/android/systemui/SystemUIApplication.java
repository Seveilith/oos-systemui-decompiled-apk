package com.android.systemui;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Process;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;
import com.android.systemui.keyboard.KeyboardUI;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.media.RingtonePlayer;
import com.android.systemui.power.PowerUI;
import com.android.systemui.recents.Recents;
import com.android.systemui.shortcut.ShortcutKeyDispatcher;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.statusbar.SystemBars;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tv.pip.PipUI;
import com.android.systemui.usb.StorageNotification;
import com.android.systemui.util.MdmLogger;
import com.android.systemui.volume.VolumeUI;
import java.util.HashMap;
import java.util.Map;

public class SystemUIApplication
  extends Application
{
  private final Class<?>[] SERVICES = { TunerService.class, KeyguardViewMediator.class, Recents.class, VolumeUI.class, Divider.class, SystemBars.class, StorageNotification.class, PowerUI.class, RingtonePlayer.class, KeyboardUI.class, PipUI.class, ShortcutKeyDispatcher.class, VendorServices.class };
  private final Class<?>[] SERVICES_PER_USER = { Recents.class, PipUI.class };
  private boolean mBootCompleted;
  private final Map<Class<?>, Object> mComponents = new HashMap();
  private final SystemUI[] mServices = new SystemUI[this.SERVICES.length];
  private boolean mServicesStarted;
  
  private void startServicesIfNeeded(Class<?>[] paramArrayOfClass)
  {
    if (this.mServicesStarted) {
      return;
    }
    if ((!this.mBootCompleted) && ("1".equals(SystemProperties.get("sys.boot_completed")))) {
      this.mBootCompleted = true;
    }
    Log.v("SystemUIService", "Starting SystemUI services for user " + Process.myUserHandle().getIdentifier() + ".");
    MdmLogger.init(this);
    int j = paramArrayOfClass.length;
    int i = 0;
    while (i < j)
    {
      Class<?> localClass = paramArrayOfClass[i];
      try
      {
        Object localObject = SystemUIFactory.getInstance().createInstance(localClass);
        SystemUI[] arrayOfSystemUI = this.mServices;
        if (localObject == null) {
          localObject = localClass.newInstance();
        }
        for (;;)
        {
          arrayOfSystemUI[i] = ((SystemUI)localObject);
          this.mServices[i].mContext = this;
          this.mServices[i].mComponents = this.mComponents;
          this.mServices[i].start();
          if (this.mBootCompleted) {
            this.mServices[i].onBootCompleted();
          }
          i += 1;
          break;
        }
        this.mServicesStarted = true;
      }
      catch (InstantiationException paramArrayOfClass)
      {
        throw new RuntimeException(paramArrayOfClass);
      }
      catch (IllegalAccessException paramArrayOfClass)
      {
        throw new RuntimeException(paramArrayOfClass);
      }
    }
  }
  
  public <T> T getComponent(Class<T> paramClass)
  {
    return (T)this.mComponents.get(paramClass);
  }
  
  public SystemUI[] getServices()
  {
    return this.mServices;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    if (this.mServicesStarted)
    {
      int j = this.mServices.length;
      int i = 0;
      while (i < j)
      {
        if (this.mServices[i] != null) {
          this.mServices[i].onConfigurationChanged(paramConfiguration);
        }
        i += 1;
      }
    }
  }
  
  public void onCreate()
  {
    super.onCreate();
    setTheme(2131821081);
    SystemUIFactory.createFromConfig(this);
    if (Process.myUserHandle().equals(UserHandle.SYSTEM))
    {
      IntentFilter localIntentFilter = new IntentFilter("android.intent.action.BOOT_COMPLETED");
      localIntentFilter.setPriority(1000);
      registerReceiver(new BroadcastReceiver()
      {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
          if (SystemUIApplication.-get0(SystemUIApplication.this)) {
            return;
          }
          SystemUIApplication.this.unregisterReceiver(this);
          SystemUIApplication.-set0(SystemUIApplication.this, true);
          if (SystemUIApplication.-get2(SystemUIApplication.this))
          {
            int j = SystemUIApplication.-get1(SystemUIApplication.this).length;
            int i = 0;
            while (i < j)
            {
              SystemUIApplication.-get1(SystemUIApplication.this)[i].onBootCompleted();
              i += 1;
            }
          }
        }
      }, localIntentFilter);
      return;
    }
    startServicesIfNeeded(this.SERVICES_PER_USER);
  }
  
  void startSecondaryUserServicesIfNeeded()
  {
    startServicesIfNeeded(this.SERVICES_PER_USER);
  }
  
  public void startServicesIfNeeded()
  {
    startServicesIfNeeded(this.SERVICES);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\SystemUIApplication.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */