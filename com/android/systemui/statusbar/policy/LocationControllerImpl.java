package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import java.util.ArrayList;
import java.util.Iterator;

public class LocationControllerImpl
  extends BroadcastReceiver
  implements LocationController
{
  private static String TAG = "LocationControllerImpl";
  private static final int[] mHighPowerRequestAppOpArray = { 42 };
  private AppOpsManager mAppOpsManager;
  private Context mContext;
  private final H mHandler = new H(null);
  private final Object mLock = new Object();
  private ArrayList<LocationController.LocationSettingsChangeCallback> mSettingsChangeCallbacks = new ArrayList();
  public final String mSlotLocation;
  private StatusBarManager mStatusBarManager;
  
  public LocationControllerImpl(Context paramContext, Looper paramLooper)
  {
    this.mContext = paramContext;
    this.mSlotLocation = this.mContext.getString(17039394);
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.location.MODE_CHANGED");
    paramContext.registerReceiverAsUser(this, UserHandle.ALL, localIntentFilter, null, new Handler(paramLooper));
    this.mAppOpsManager = ((AppOpsManager)paramContext.getSystemService("appops"));
    this.mStatusBarManager = ((StatusBarManager)paramContext.getSystemService("statusbar"));
  }
  
  private boolean isUserLocationRestricted(int paramInt)
  {
    return ((UserManager)this.mContext.getSystemService("user")).hasUserRestriction("no_share_location", UserHandle.of(paramInt));
  }
  
  public void addSettingsChangedCallback(LocationController.LocationSettingsChangeCallback paramLocationSettingsChangeCallback)
  {
    synchronized (this.mLock)
    {
      this.mSettingsChangeCallbacks.add(paramLocationSettingsChangeCallback);
      this.mHandler.sendEmptyMessage(1);
      return;
    }
  }
  
  public boolean isLocationEnabled()
  {
    boolean bool = false;
    if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "location_mode", 0, ActivityManager.getCurrentUser()) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    this.mHandler.sendEmptyMessage(1);
  }
  
  public void removeSettingsChangedCallback(LocationController.LocationSettingsChangeCallback paramLocationSettingsChangeCallback)
  {
    synchronized (this.mLock)
    {
      this.mSettingsChangeCallbacks.remove(paramLocationSettingsChangeCallback);
      return;
    }
  }
  
  public boolean setLocationEnabled(boolean paramBoolean)
  {
    int j = ActivityManager.getCurrentUser();
    if (isUserLocationRestricted(j)) {
      return false;
    }
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    if (paramBoolean) {}
    for (int i = -1;; i = 0) {
      return Settings.Secure.putIntForUser(localContentResolver, "location_mode", i, j);
    }
  }
  
  private final class H
    extends Handler
  {
    private H() {}
    
    private void locationSettingsChanged()
    {
      synchronized (LocationControllerImpl.-get0(LocationControllerImpl.this))
      {
        boolean bool = LocationControllerImpl.this.isLocationEnabled();
        Iterator localIterator = LocationControllerImpl.-get1(LocationControllerImpl.this).iterator();
        if (localIterator.hasNext()) {
          ((LocationController.LocationSettingsChangeCallback)localIterator.next()).onLocationSettingsChanged(bool);
        }
      }
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      locationSettingsChanged();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\LocationControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */