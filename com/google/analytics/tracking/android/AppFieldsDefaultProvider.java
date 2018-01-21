package com.google.analytics.tracking.android;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import com.google.android.gms.common.util.VisibleForTesting;

class AppFieldsDefaultProvider
  implements DefaultProvider
{
  private static AppFieldsDefaultProvider sInstance;
  private static Object sInstanceLock = new Object();
  protected String mAppId;
  protected String mAppInstallerId;
  protected String mAppName;
  protected String mAppVersion;
  
  @VisibleForTesting
  protected AppFieldsDefaultProvider() {}
  
  private AppFieldsDefaultProvider(Context paramContext)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    this.mAppId = paramContext.getPackageName();
    this.mAppInstallerId = localPackageManager.getInstallerPackageName(this.mAppId);
    String str = this.mAppId;
    localObject2 = null;
    localObject1 = str;
    for (;;)
    {
      try
      {
        localPackageInfo = localPackageManager.getPackageInfo(paramContext.getPackageName(), 0);
        if (localPackageInfo != null) {
          continue;
        }
        paramContext = (Context)localObject2;
        localObject1 = str;
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        PackageInfo localPackageInfo;
        Log.e("Error retrieving package info: appName set to " + (String)localObject1);
        paramContext = (Context)localObject2;
        continue;
      }
      this.mAppName = ((String)localObject1);
      this.mAppVersion = paramContext;
      return;
      localObject1 = str;
      paramContext = localPackageManager.getApplicationLabel(localPackageInfo.applicationInfo).toString();
      localObject1 = paramContext;
      str = localPackageInfo.versionName;
      localObject1 = paramContext;
      paramContext = str;
    }
  }
  
  @VisibleForTesting
  static void dropInstance()
  {
    synchronized (sInstanceLock)
    {
      sInstance = null;
      return;
    }
  }
  
  public static AppFieldsDefaultProvider getProvider()
  {
    return sInstance;
  }
  
  public static void initializeProvider(Context paramContext)
  {
    synchronized (sInstanceLock)
    {
      if (sInstance != null) {
        return;
      }
      sInstance = new AppFieldsDefaultProvider(paramContext);
    }
  }
  
  public String getValue(String paramString)
  {
    if (paramString != null)
    {
      if (!paramString.equals("&an"))
      {
        if (paramString.equals("&av")) {
          break label49;
        }
        if (paramString.equals("&aid")) {
          break label54;
        }
        if (paramString.equals("&aiid")) {
          break label59;
        }
        return null;
      }
    }
    else {
      return null;
    }
    return this.mAppName;
    label49:
    return this.mAppVersion;
    label54:
    return this.mAppId;
    label59:
    return this.mAppInstallerId;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\google\analytics\tracking\android\AppFieldsDefaultProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */