package com.android.systemui.statusbar.policy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.phone.KeyguardPreviewContainer;
import java.util.List;

public class PreviewInflater
{
  private Context mContext;
  private LockPatternUtils mLockPatternUtils;
  
  public PreviewInflater(Context paramContext, LockPatternUtils paramLockPatternUtils)
  {
    this.mContext = paramContext;
    this.mLockPatternUtils = paramLockPatternUtils;
  }
  
  public static ActivityInfo getTargetActivityInfo(Context paramContext, Intent paramIntent, int paramInt, boolean paramBoolean)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    int i = 65536;
    if (!paramBoolean) {
      i = 851968;
    }
    paramContext = localPackageManager.queryIntentActivitiesAsUser(paramIntent, i, paramInt);
    if (paramContext.size() == 0) {
      return null;
    }
    paramIntent = localPackageManager.resolveActivityAsUser(paramIntent, i | 0x80, paramInt);
    if ((paramIntent == null) || (wouldLaunchResolverActivity(paramIntent, paramContext))) {
      return null;
    }
    return paramIntent.activityInfo;
  }
  
  private WidgetInfo getWidgetInfo(Intent paramIntent)
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    List localList = localPackageManager.queryIntentActivitiesAsUser(paramIntent, 851968, KeyguardUpdateMonitor.getCurrentUser());
    if (localList.size() == 0) {
      return null;
    }
    paramIntent = localPackageManager.resolveActivityAsUser(paramIntent, 852096, KeyguardUpdateMonitor.getCurrentUser());
    if (wouldLaunchResolverActivity(paramIntent, localList)) {
      return null;
    }
    if ((paramIntent == null) || (paramIntent.activityInfo == null)) {
      return null;
    }
    return getWidgetInfoFromMetaData(paramIntent.activityInfo.packageName, paramIntent.activityInfo.metaData);
  }
  
  private WidgetInfo getWidgetInfoFromMetaData(String paramString, Bundle paramBundle)
  {
    if (paramBundle == null) {
      return null;
    }
    int i = paramBundle.getInt("com.android.keyguard.layout");
    if (i == 0) {
      return null;
    }
    paramBundle = new WidgetInfo(null);
    paramBundle.contextPackage = paramString;
    paramBundle.layoutId = i;
    return paramBundle;
  }
  
  private WidgetInfo getWidgetInfoFromService(ComponentName paramComponentName)
  {
    Object localObject = this.mContext.getPackageManager();
    try
    {
      localObject = ((PackageManager)localObject).getServiceInfo(paramComponentName, 128).metaData;
      localObject = getWidgetInfoFromMetaData(paramComponentName.getPackageName(), (Bundle)localObject);
      return (WidgetInfo)localObject;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.w("PreviewInflater", "Failed to load preview; " + paramComponentName.flattenToShortString() + " not found", localNameNotFoundException);
    }
    return null;
  }
  
  private KeyguardPreviewContainer inflatePreview(WidgetInfo paramWidgetInfo)
  {
    if (paramWidgetInfo == null) {
      return null;
    }
    paramWidgetInfo = inflateWidgetView(paramWidgetInfo);
    if (paramWidgetInfo == null) {
      return null;
    }
    KeyguardPreviewContainer localKeyguardPreviewContainer = new KeyguardPreviewContainer(this.mContext, null);
    localKeyguardPreviewContainer.addView(paramWidgetInfo);
    return localKeyguardPreviewContainer;
  }
  
  private View inflateWidgetView(WidgetInfo paramWidgetInfo)
  {
    try
    {
      Context localContext = this.mContext.createPackageContext(paramWidgetInfo.contextPackage, 4);
      paramWidgetInfo = ((LayoutInflater)localContext.getSystemService("layout_inflater")).cloneInContext(localContext).inflate(paramWidgetInfo.layoutId, null, false);
      return paramWidgetInfo;
    }
    catch (PackageManager.NameNotFoundException|RuntimeException paramWidgetInfo)
    {
      Log.w("PreviewInflater", "Error creating widget view", paramWidgetInfo);
    }
    return null;
  }
  
  public static boolean wouldLaunchResolverActivity(Context paramContext, Intent paramIntent, int paramInt)
  {
    boolean bool = false;
    if (getTargetActivityInfo(paramContext, paramIntent, paramInt, false) == null) {
      bool = true;
    }
    return bool;
  }
  
  private static boolean wouldLaunchResolverActivity(ResolveInfo paramResolveInfo, List<ResolveInfo> paramList)
  {
    int i = 0;
    while (i < paramList.size())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)paramList.get(i);
      if ((localResolveInfo.activityInfo.name.equals(paramResolveInfo.activityInfo.name)) && (localResolveInfo.activityInfo.packageName.equals(paramResolveInfo.activityInfo.packageName))) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public View inflatePreview(Intent paramIntent)
  {
    return inflatePreview(getWidgetInfo(paramIntent));
  }
  
  public View inflatePreviewFromService(ComponentName paramComponentName)
  {
    if (paramComponentName == null)
    {
      Log.w("PreviewInflater", "voice assist component null");
      return null;
    }
    return inflatePreview(getWidgetInfoFromService(paramComponentName));
  }
  
  private static class WidgetInfo
  {
    String contextPackage;
    int layoutId;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\PreviewInflater.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */