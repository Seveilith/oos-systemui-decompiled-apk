package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.ActionListener;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.WifiTracker;
import com.android.settingslib.wifi.WifiTracker.WifiListener;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AccessPointControllerImpl
  implements NetworkController.AccessPointController, WifiTracker.WifiListener
{
  private static final boolean DEBUG = Log.isLoggable("AccessPointController", 3);
  private static final int[] ICONS = { 2130837855, 2130837856, 2130837857, 2130837858, 2130837859 };
  private final ArrayList<NetworkController.AccessPointController.AccessPointCallback> mCallbacks = new ArrayList();
  private final WifiManager.ActionListener mConnectListener = new WifiManager.ActionListener()
  {
    public void onFailure(int paramAnonymousInt)
    {
      if (AccessPointControllerImpl.-get0()) {
        Log.d("AccessPointController", "connect failure reason=" + paramAnonymousInt);
      }
    }
    
    public void onSuccess()
    {
      if (AccessPointControllerImpl.-get0()) {
        Log.d("AccessPointController", "connect success");
      }
    }
  };
  private final Context mContext;
  private int mCurrentUser;
  private final UserManager mUserManager;
  private final WifiTracker mWifiTracker;
  
  public AccessPointControllerImpl(Context paramContext, Looper paramLooper)
  {
    this.mContext = paramContext;
    this.mUserManager = ((UserManager)this.mContext.getSystemService("user"));
    this.mWifiTracker = new WifiTracker(paramContext, this, paramLooper, false, true);
    this.mCurrentUser = ActivityManager.getCurrentUser();
  }
  
  private void fireAcccessPointsCallback(List<AccessPoint> paramList)
  {
    Iterator localIterator = this.mCallbacks.iterator();
    while (localIterator.hasNext()) {
      ((NetworkController.AccessPointController.AccessPointCallback)localIterator.next()).onAccessPointsChanged(paramList);
    }
  }
  
  private void fireSettingsIntentCallback(Intent paramIntent)
  {
    Iterator localIterator = this.mCallbacks.iterator();
    while (localIterator.hasNext()) {
      ((NetworkController.AccessPointController.AccessPointCallback)localIterator.next()).onSettingsActivityTriggered(paramIntent);
    }
  }
  
  public void addAccessPointCallback(NetworkController.AccessPointController.AccessPointCallback paramAccessPointCallback)
  {
    if ((paramAccessPointCallback == null) || (this.mCallbacks.contains(paramAccessPointCallback))) {
      return;
    }
    if (DEBUG) {
      Log.d("AccessPointController", "addCallback " + paramAccessPointCallback);
    }
    this.mCallbacks.add(paramAccessPointCallback);
    if (this.mCallbacks.size() == 1) {
      this.mWifiTracker.startTracking();
    }
  }
  
  public boolean canConfigWifi()
  {
    return !this.mUserManager.hasUserRestriction("no_config_wifi", new UserHandle(this.mCurrentUser));
  }
  
  public boolean connect(AccessPoint paramAccessPoint)
  {
    if (paramAccessPoint == null) {
      return false;
    }
    if ((DEBUG) && (paramAccessPoint.getConfig() != null)) {
      Log.d("AccessPointController", "connect networkId=" + paramAccessPoint.getConfig().networkId);
    }
    if (paramAccessPoint.isSaved())
    {
      if (paramAccessPoint.getConfig() != null)
      {
        this.mWifiTracker.getManager().connect(paramAccessPoint.getConfig().networkId, this.mConnectListener);
        return false;
      }
      Log.d("AccessPointController", "Cannot connect to saved AP because the config is null");
      return false;
    }
    if (paramAccessPoint.getSecurity() != 0)
    {
      Intent localIntent = new Intent("android.settings.WIFI_SETTINGS");
      localIntent.putExtra("wifi_start_connect_ssid", paramAccessPoint.getSsidStr());
      localIntent.addFlags(268435456);
      fireSettingsIntentCallback(localIntent);
      return true;
    }
    paramAccessPoint.generateOpenNetworkConfig();
    if (paramAccessPoint.getConfig() != null)
    {
      this.mWifiTracker.getManager().connect(paramAccessPoint.getConfig(), this.mConnectListener);
      return false;
    }
    Log.d("AccessPointController", "Cannot connect to AP because the generated config is null");
    return false;
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    this.mWifiTracker.dump(paramPrintWriter);
  }
  
  public int getIcon(AccessPoint paramAccessPoint)
  {
    int i = Math.min(paramAccessPoint.getLevel() + 1, 4);
    paramAccessPoint = ICONS;
    if (i >= 0) {}
    for (;;)
    {
      return paramAccessPoint[i];
      i = 0;
    }
  }
  
  public void onAccessPointsChanged()
  {
    fireAcccessPointsCallback(this.mWifiTracker.getAccessPoints());
  }
  
  public void onConnectedChanged()
  {
    fireAcccessPointsCallback(this.mWifiTracker.getAccessPoints());
  }
  
  public void onUserSwitched(int paramInt)
  {
    this.mCurrentUser = paramInt;
  }
  
  public void onWifiStateChanged(int paramInt) {}
  
  public void removeAccessPointCallback(NetworkController.AccessPointController.AccessPointCallback paramAccessPointCallback)
  {
    if (paramAccessPointCallback == null) {
      return;
    }
    if (DEBUG) {
      Log.d("AccessPointController", "removeCallback " + paramAccessPointCallback);
    }
    this.mCallbacks.remove(paramAccessPointCallback);
    if (this.mCallbacks.isEmpty()) {
      this.mWifiTracker.stopTracking();
    }
  }
  
  public void scanForAccessPoints()
  {
    if (DEBUG) {
      Log.d("AccessPointController", "scan!");
    }
    this.mWifiTracker.forceScan();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\AccessPointControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */