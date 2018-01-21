package com.android.systemui.statusbar;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.util.Log;
import java.util.Arrays;
import java.util.Iterator;

public class ServiceMonitor
{
  private boolean mBound;
  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getData().getSchemeSpecificPart();
      if ((ServiceMonitor.-get3(ServiceMonitor.this) != null) && (ServiceMonitor.-get3(ServiceMonitor.this).getPackageName().equals(paramAnonymousContext))) {
        ServiceMonitor.-get2(ServiceMonitor.this).sendMessage(ServiceMonitor.-get2(ServiceMonitor.this).obtainMessage(4, paramAnonymousIntent));
      }
    }
  };
  private final Callbacks mCallbacks;
  private final Context mContext;
  private final boolean mDebug;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 1: 
        ServiceMonitor.-wrap5(ServiceMonitor.this);
        return;
      case 2: 
        ServiceMonitor.-wrap2(ServiceMonitor.this);
        return;
      case 3: 
        ServiceMonitor.-wrap6(ServiceMonitor.this);
        return;
      case 4: 
        ServiceMonitor.-wrap3(ServiceMonitor.this, (Intent)paramAnonymousMessage.obj);
        return;
      case 5: 
        ServiceMonitor.-wrap1(ServiceMonitor.this);
        return;
      }
      ServiceMonitor.-wrap4(ServiceMonitor.this, (ComponentName)paramAnonymousMessage.obj);
    }
  };
  private SC mServiceConnection;
  private ComponentName mServiceName;
  private final String mSettingKey;
  private final ContentObserver mSettingObserver = new ContentObserver(this.mHandler)
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      onChange(paramAnonymousBoolean, null);
    }
    
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      if (ServiceMonitor.-get1(ServiceMonitor.this)) {
        Log.d(ServiceMonitor.-get4(ServiceMonitor.this), "onChange selfChange=" + paramAnonymousBoolean + " uri=" + paramAnonymousUri);
      }
      paramAnonymousUri = ServiceMonitor.-wrap0(ServiceMonitor.this);
      if ((paramAnonymousUri == null) && (ServiceMonitor.-get3(ServiceMonitor.this) == null)) {}
      while ((paramAnonymousUri != null) && (paramAnonymousUri.equals(ServiceMonitor.-get3(ServiceMonitor.this))))
      {
        if (ServiceMonitor.-get1(ServiceMonitor.this)) {
          Log.d(ServiceMonitor.-get4(ServiceMonitor.this), "skipping no-op restart");
        }
        return;
      }
      if (ServiceMonitor.-get0(ServiceMonitor.this)) {
        ServiceMonitor.-get2(ServiceMonitor.this).sendEmptyMessage(3);
      }
      ServiceMonitor.-get2(ServiceMonitor.this).sendEmptyMessageDelayed(1, 500L);
    }
  };
  private final String mTag;
  
  public ServiceMonitor(String paramString1, boolean paramBoolean, Context paramContext, String paramString2, Callbacks paramCallbacks)
  {
    this.mTag = (paramString1 + ".ServiceMonitor");
    this.mDebug = paramBoolean;
    this.mContext = paramContext;
    this.mSettingKey = paramString2;
    this.mCallbacks = paramCallbacks;
  }
  
  private static String bundleToString(Bundle paramBundle)
  {
    if (paramBundle == null) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder(123);
    Iterator localIterator = paramBundle.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (localStringBuilder.length() > 1) {
        localStringBuilder.append(',');
      }
      Object localObject2 = paramBundle.get(str);
      Object localObject1 = localObject2;
      if ((localObject2 instanceof String[])) {
        localObject1 = Arrays.asList((String[])localObject2);
      }
      localStringBuilder.append(str).append('=').append(localObject1);
    }
    return '}';
  }
  
  private void checkBound()
  {
    if (this.mDebug) {
      Log.d(this.mTag, "checkBound mBound=" + this.mBound);
    }
    if (!this.mBound) {
      startService();
    }
  }
  
  private void continueStartService()
  {
    if (this.mDebug) {
      Log.d(this.mTag, "continueStartService");
    }
    Intent localIntent = new Intent().setComponent(this.mServiceName);
    try
    {
      this.mServiceConnection = new SC(null);
      this.mBound = this.mContext.bindService(localIntent, this.mServiceConnection, 1);
      if (this.mDebug) {
        Log.d(this.mTag, "mBound: " + this.mBound);
      }
      if (!this.mBound) {
        this.mCallbacks.onNoService();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.w(this.mTag, "Error binding to service: " + this.mServiceName, localThrowable);
      }
    }
  }
  
  private ComponentName getComponentNameFromSetting()
  {
    String str = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), this.mSettingKey, -2);
    if (str == null) {
      return null;
    }
    return ComponentName.unflattenFromString(str);
  }
  
  private void packageIntent(Intent paramIntent)
  {
    if (this.mDebug) {
      Log.d(this.mTag, "packageIntent intent=" + paramIntent + " extras=" + bundleToString(paramIntent.getExtras()));
    }
    if ("android.intent.action.PACKAGE_ADDED".equals(paramIntent.getAction())) {
      this.mHandler.sendEmptyMessage(1);
    }
    while ((!"android.intent.action.PACKAGE_CHANGED".equals(paramIntent.getAction())) && (!"android.intent.action.PACKAGE_REMOVED".equals(paramIntent.getAction()))) {
      return;
    }
    paramIntent = this.mContext.getPackageManager();
    int i;
    if ((isPackageAvailable()) && (paramIntent.getApplicationEnabledSetting(this.mServiceName.getPackageName()) != 2)) {
      if (paramIntent.getComponentEnabledSetting(this.mServiceName) != 2) {
        i = 1;
      }
    }
    for (;;)
    {
      if ((this.mBound) && (i == 0)) {
        break label178;
      }
      if ((this.mBound) || (i == 0)) {
        break;
      }
      startService();
      return;
      i = 0;
      continue;
      i = 0;
    }
    label178:
    stopService();
    scheduleCheckBound();
  }
  
  private void scheduleCheckBound()
  {
    this.mHandler.removeMessages(5);
    this.mHandler.sendEmptyMessageDelayed(5, 2000L);
  }
  
  private void serviceDisconnected(ComponentName paramComponentName)
  {
    if (this.mDebug) {
      Log.d(this.mTag, "serviceDisconnected serviceName=" + paramComponentName + " mServiceName=" + this.mServiceName);
    }
    if (paramComponentName.equals(this.mServiceName))
    {
      this.mBound = false;
      scheduleCheckBound();
    }
  }
  
  private void startService()
  {
    this.mServiceName = getComponentNameFromSetting();
    if (this.mDebug) {
      Log.d(this.mTag, "startService mServiceName=" + this.mServiceName);
    }
    if (this.mServiceName == null)
    {
      this.mBound = false;
      this.mCallbacks.onNoService();
      return;
    }
    long l = this.mCallbacks.onServiceStartAttempt();
    this.mHandler.sendEmptyMessageDelayed(2, l);
  }
  
  private void stopService()
  {
    if (this.mDebug) {
      Log.d(this.mTag, "stopService");
    }
    boolean bool = this.mContext.stopService(new Intent().setComponent(this.mServiceName));
    if (this.mDebug) {
      Log.d(this.mTag, "  stopped=" + bool);
    }
    this.mContext.unbindService(this.mServiceConnection);
    this.mBound = false;
  }
  
  public ComponentName getComponent()
  {
    return getComponentNameFromSetting();
  }
  
  public boolean isPackageAvailable()
  {
    ComponentName localComponentName = getComponent();
    if (localComponentName == null) {
      return false;
    }
    try
    {
      boolean bool = this.mContext.getPackageManager().isPackageAvailable(localComponentName.getPackageName());
      return bool;
    }
    catch (RuntimeException localRuntimeException)
    {
      Log.w(this.mTag, "Error checking package availability", localRuntimeException);
    }
    return false;
  }
  
  public void setComponent(ComponentName paramComponentName)
  {
    if (paramComponentName == null) {}
    for (paramComponentName = null;; paramComponentName = paramComponentName.flattenToShortString())
    {
      Settings.Secure.putStringForUser(this.mContext.getContentResolver(), this.mSettingKey, paramComponentName, -2);
      return;
    }
  }
  
  public void start()
  {
    this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(this.mSettingKey), false, this.mSettingObserver, -1);
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
    localIntentFilter.addDataScheme("package");
    this.mContext.registerReceiver(this.mBroadcastReceiver, localIntentFilter);
    this.mHandler.sendEmptyMessage(1);
  }
  
  public static abstract interface Callbacks
  {
    public abstract void onNoService();
    
    public abstract long onServiceStartAttempt();
  }
  
  private final class SC
    implements ServiceConnection, IBinder.DeathRecipient
  {
    private ComponentName mName;
    private IBinder mService;
    
    private SC() {}
    
    public void binderDied()
    {
      if (ServiceMonitor.-get1(ServiceMonitor.this)) {
        Log.d(ServiceMonitor.-get4(ServiceMonitor.this), "binderDied");
      }
      ServiceMonitor.-get2(ServiceMonitor.this).sendMessage(ServiceMonitor.-get2(ServiceMonitor.this).obtainMessage(6, this.mName));
    }
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      if (ServiceMonitor.-get1(ServiceMonitor.this)) {
        Log.d(ServiceMonitor.-get4(ServiceMonitor.this), "onServiceConnected name=" + paramComponentName + " service=" + paramIBinder);
      }
      this.mName = paramComponentName;
      this.mService = paramIBinder;
      try
      {
        paramIBinder.linkToDeath(this, 0);
        return;
      }
      catch (RemoteException paramComponentName)
      {
        Log.w(ServiceMonitor.-get4(ServiceMonitor.this), "Error linking to death", paramComponentName);
      }
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      if (ServiceMonitor.-get1(ServiceMonitor.this)) {
        Log.d(ServiceMonitor.-get4(ServiceMonitor.this), "onServiceDisconnected name=" + paramComponentName);
      }
      boolean bool = this.mService.unlinkToDeath(this, 0);
      if (ServiceMonitor.-get1(ServiceMonitor.this)) {
        Log.d(ServiceMonitor.-get4(ServiceMonitor.this), "  unlinked=" + bool);
      }
      ServiceMonitor.-get2(ServiceMonitor.this).sendMessage(ServiceMonitor.-get2(ServiceMonitor.this).obtainMessage(6, this.mName));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\ServiceMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */