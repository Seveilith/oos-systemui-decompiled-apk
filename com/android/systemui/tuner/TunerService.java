package com.android.systemui.tuner;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.systemui.SystemUI;
import com.android.systemui.SystemUIApplication;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class TunerService
  extends SystemUI
{
  private static final String TAG = TunerService.class.getSimpleName();
  private static TunerService sInstance;
  private static boolean sIsOPLabExist = true;
  private ContentResolver mContentResolver;
  private int mCurrentUser;
  private final ArrayMap<Uri, String> mListeningUris = new ArrayMap();
  private final Observer mObserver = new Observer();
  private final HashMap<String, Set<Tunable>> mTunableLookup = new HashMap();
  private CurrentUserTracker mUserTracker;
  
  private void addTunable(Tunable paramTunable, String paramString)
  {
    if (!this.mTunableLookup.containsKey(paramString)) {
      this.mTunableLookup.put(paramString, new ArraySet());
    }
    ((Set)this.mTunableLookup.get(paramString)).add(paramTunable);
    Uri localUri = Settings.Secure.getUriFor(paramString);
    if (!this.mListeningUris.containsKey(localUri))
    {
      this.mListeningUris.put(localUri, paramString);
      this.mContentResolver.registerContentObserver(localUri, false, this.mObserver, this.mCurrentUser);
    }
    paramTunable.onTuningChanged(paramString, Settings.Secure.getStringForUser(this.mContentResolver, paramString, this.mCurrentUser));
  }
  
  public static TunerService get(Context paramContext)
  {
    TunerService localTunerService = null;
    if ((paramContext.getApplicationContext() instanceof SystemUIApplication)) {
      localTunerService = (TunerService)((SystemUIApplication)paramContext.getApplicationContext()).getComponent(TunerService.class);
    }
    if (localTunerService == null) {
      return getStaticService(paramContext);
    }
    return localTunerService;
  }
  
  private static TunerService getStaticService(Context paramContext)
  {
    if (sInstance == null)
    {
      sInstance = new TunerService();
      sInstance.mContext = paramContext.getApplicationContext();
      sInstance.mComponents = new HashMap();
      sInstance.start();
    }
    return sInstance;
  }
  
  public static boolean isOPLabExist()
  {
    return sIsOPLabExist;
  }
  
  public static final boolean isTunerEnabled(Context paramContext)
  {
    if (sIsOPLabExist) {}
    for (ComponentName localComponentName = new ComponentName("com.android.settings", "com.oneplus.settings.laboratory.OPLabFeatureActivity"); userContext(paramContext).getPackageManager().getComponentEnabledSetting(localComponentName) == 1; localComponentName = new ComponentName(paramContext, TunerActivity.class)) {
      return true;
    }
    return false;
  }
  
  public static final void setTunerEnabled(Context paramContext, boolean paramBoolean)
  {
    int j = 2;
    try
    {
      PackageManager localPackageManager1 = userContext(paramContext).getPackageManager();
      ComponentName localComponentName = new ComponentName("com.android.settings", "com.oneplus.settings.laboratory.OPLabFeatureActivity");
      if (paramBoolean) {}
      for (int i = 1;; i = 2)
      {
        localPackageManager1.setComponentEnabledSetting(localComponentName, i, 1);
        sIsOPLabExist = true;
        return;
      }
      PackageManager localPackageManager2;
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      Log.w(TAG, "OPLabFeatureActivity does not exist.");
      localPackageManager2 = userContext(paramContext).getPackageManager();
      paramContext = new ComponentName(paramContext, TunerActivity.class);
      i = j;
      if (paramBoolean) {
        i = 1;
      }
      localPackageManager2.setComponentEnabledSetting(paramContext, i, 1);
      sIsOPLabExist = false;
    }
  }
  
  public static final void showResetRequest(Context paramContext, final Runnable paramRunnable)
  {
    SystemUIDialog localSystemUIDialog = new SystemUIDialog(paramContext);
    localSystemUIDialog.setShowForAllUsers(true);
    if (sIsOPLabExist) {}
    for (int i = 2131690014;; i = 2131690484)
    {
      localSystemUIDialog.setMessage(i);
      localSystemUIDialog.setButton(-2, paramContext.getString(2131690100), (DialogInterface.OnClickListener)null);
      localSystemUIDialog.setButton(-1, paramContext.getString(2131690378), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          TunerService.setTunerEnabled(this.val$context, false);
          Settings.Secure.putInt(this.val$context.getContentResolver(), "seen_tuner_warning", 0);
          if (paramRunnable != null) {
            paramRunnable.run();
          }
        }
      });
      localSystemUIDialog.show();
      return;
    }
  }
  
  private void upgradeTuner(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 1)
    {
      Object localObject = getValue("icon_blacklist");
      if (localObject != null)
      {
        localObject = StatusBarIconController.getIconBlacklist((String)localObject);
        ((ArraySet)localObject).add("rotate");
        ((ArraySet)localObject).add("headset");
        ((ArraySet)localObject).add("networkspeed");
        Settings.Secure.putStringForUser(this.mContentResolver, "icon_blacklist", TextUtils.join(",", (Iterable)localObject), this.mCurrentUser);
      }
    }
    setValue("sysui_tuner_version", paramInt2);
  }
  
  private static Context userContext(Context paramContext)
  {
    try
    {
      Context localContext = paramContext.createPackageContextAsUser(paramContext.getPackageName(), 0, new UserHandle(ActivityManager.getCurrentUser()));
      return localContext;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
    return paramContext;
  }
  
  public void addTunable(Tunable paramTunable, String... paramVarArgs)
  {
    int i = 0;
    int j = paramVarArgs.length;
    while (i < j)
    {
      addTunable(paramTunable, paramVarArgs[i]);
      i += 1;
    }
  }
  
  public void clearAll()
  {
    Settings.Global.putString(this.mContentResolver, "sysui_demo_allowed", null);
    Settings.System.putString(this.mContentResolver, "status_bar_show_battery_percent", null);
    Object localObject = new Intent("com.android.systemui.demo");
    ((Intent)localObject).putExtra("command", "exit");
    this.mContext.sendBroadcast((Intent)localObject);
    localObject = this.mTunableLookup.keySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      String str = (String)((Iterator)localObject).next();
      Settings.Secure.putString(this.mContentResolver, str, null);
    }
  }
  
  public int getValue(String paramString, int paramInt)
  {
    return Settings.Secure.getIntForUser(this.mContentResolver, paramString, paramInt, this.mCurrentUser);
  }
  
  public String getValue(String paramString)
  {
    return Settings.Secure.getStringForUser(this.mContentResolver, paramString, this.mCurrentUser);
  }
  
  public void reloadAll()
  {
    Iterator localIterator1 = this.mTunableLookup.keySet().iterator();
    while (localIterator1.hasNext())
    {
      String str1 = (String)localIterator1.next();
      String str2 = Settings.Secure.getStringForUser(this.mContentResolver, str1, this.mCurrentUser);
      Iterator localIterator2 = ((Set)this.mTunableLookup.get(str1)).iterator();
      while (localIterator2.hasNext()) {
        ((Tunable)localIterator2.next()).onTuningChanged(str1, str2);
      }
    }
  }
  
  public void reloadSetting(Uri paramUri)
  {
    paramUri = (String)this.mListeningUris.get(paramUri);
    Object localObject = (Set)this.mTunableLookup.get(paramUri);
    if (localObject == null) {
      return;
    }
    String str = Settings.Secure.getStringForUser(this.mContentResolver, paramUri, this.mCurrentUser);
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext()) {
      ((Tunable)((Iterator)localObject).next()).onTuningChanged(paramUri, str);
    }
  }
  
  public void removeTunable(Tunable paramTunable)
  {
    Iterator localIterator = this.mTunableLookup.values().iterator();
    while (localIterator.hasNext()) {
      ((Set)localIterator.next()).remove(paramTunable);
    }
  }
  
  protected void reregisterAll()
  {
    if (this.mListeningUris.size() == 0) {
      return;
    }
    this.mContentResolver.unregisterContentObserver(this.mObserver);
    Iterator localIterator = this.mListeningUris.keySet().iterator();
    while (localIterator.hasNext())
    {
      Uri localUri = (Uri)localIterator.next();
      this.mContentResolver.registerContentObserver(localUri, false, this.mObserver, this.mCurrentUser);
    }
  }
  
  public void setValue(String paramString, int paramInt)
  {
    Settings.Secure.putIntForUser(this.mContentResolver, paramString, paramInt, this.mCurrentUser);
  }
  
  public void setValue(String paramString1, String paramString2)
  {
    Settings.Secure.putStringForUser(this.mContentResolver, paramString1, paramString2, this.mCurrentUser);
  }
  
  public void start()
  {
    this.mContentResolver = this.mContext.getContentResolver();
    Iterator localIterator = UserManager.get(this.mContext).getUsers().iterator();
    while (localIterator.hasNext())
    {
      this.mCurrentUser = ((UserInfo)localIterator.next()).getUserHandle().getIdentifier();
      if (getValue("sysui_tuner_version", 0) != 1) {
        upgradeTuner(getValue("sysui_tuner_version", 0), 1);
      }
    }
    putComponent(TunerService.class, this);
    this.mCurrentUser = ActivityManager.getCurrentUser();
    this.mUserTracker = new CurrentUserTracker(this.mContext)
    {
      public void onUserSwitched(int paramAnonymousInt)
      {
        TunerService.-set0(TunerService.this, paramAnonymousInt);
        TunerService.this.reloadAll();
        TunerService.this.reregisterAll();
      }
    };
    this.mUserTracker.startTracking();
  }
  
  public static class ClearReceiver
    extends BroadcastReceiver
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if ("com.android.systemui.action.CLEAR_TUNER".equals(paramIntent.getAction())) {
        TunerService.get(paramContext).clearAll();
      }
    }
  }
  
  private class Observer
    extends ContentObserver
  {
    public Observer()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri, int paramInt)
    {
      if (paramInt == ActivityManager.getCurrentUser()) {
        TunerService.this.reloadSetting(paramUri);
      }
      Log.d(TunerService.-get0(), "onChange:uri=" + paramUri);
    }
  }
  
  public static abstract interface Tunable
  {
    public abstract void onTuningChanged(String paramString1, String paramString2);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tuner\TunerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */