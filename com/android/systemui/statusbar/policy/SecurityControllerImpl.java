package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.IConnectivityManager;
import android.net.IConnectivityManager.Stub;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.NetworkRequest.Builder;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserManager;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class SecurityControllerImpl
  implements SecurityController
{
  private static final boolean DEBUG = Log.isLoggable("SecurityController", 3);
  private static final NetworkRequest REQUEST = new NetworkRequest.Builder().removeCapability(15).removeCapability(13).removeCapability(14).build();
  @GuardedBy("mCallbacks")
  private final ArrayList<SecurityController.SecurityControllerCallback> mCallbacks = new ArrayList();
  private final ConnectivityManager mConnectivityManager;
  private final IConnectivityManager mConnectivityManagerService;
  private final Context mContext;
  private int mCurrentUserId;
  private SparseArray<VpnConfig> mCurrentVpns = new SparseArray();
  private final DevicePolicyManager mDevicePolicyManager;
  private final ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback()
  {
    public void onAvailable(Network paramAnonymousNetwork)
    {
      if (SecurityControllerImpl.-get0()) {
        Log.d("SecurityController", "onAvailable " + paramAnonymousNetwork.netId);
      }
      SecurityControllerImpl.-wrap1(SecurityControllerImpl.this);
      SecurityControllerImpl.-wrap0(SecurityControllerImpl.this);
    }
    
    public void onLost(Network paramAnonymousNetwork)
    {
      if (SecurityControllerImpl.-get0()) {
        Log.d("SecurityController", "onLost " + paramAnonymousNetwork.netId);
      }
      SecurityControllerImpl.-wrap1(SecurityControllerImpl.this);
      SecurityControllerImpl.-wrap0(SecurityControllerImpl.this);
    }
  };
  private final PackageManager mPackageManager;
  private final UserManager mUserManager;
  private int mVpnUserId;
  
  public SecurityControllerImpl(Context paramContext)
  {
    this.mContext = paramContext;
    this.mDevicePolicyManager = ((DevicePolicyManager)paramContext.getSystemService("device_policy"));
    this.mConnectivityManager = ((ConnectivityManager)paramContext.getSystemService("connectivity"));
    this.mConnectivityManagerService = IConnectivityManager.Stub.asInterface(ServiceManager.getService("connectivity"));
    this.mPackageManager = paramContext.getPackageManager();
    this.mUserManager = ((UserManager)paramContext.getSystemService("user"));
    this.mConnectivityManager.registerNetworkCallback(REQUEST, this.mNetworkCallback);
    onUserSwitched(ActivityManager.getCurrentUser());
  }
  
  private void fireCallbacks()
  {
    synchronized (this.mCallbacks)
    {
      Iterator localIterator = this.mCallbacks.iterator();
      if (localIterator.hasNext()) {
        ((SecurityController.SecurityControllerCallback)localIterator.next()).onStateChanged();
      }
    }
  }
  
  private String getPackageNameForVpnConfig(VpnConfig paramVpnConfig)
  {
    if (paramVpnConfig.legacy) {
      return null;
    }
    return paramVpnConfig.user;
  }
  
  private boolean isVpnPackageBranded(String paramString)
  {
    try
    {
      paramString = this.mPackageManager.getApplicationInfo(paramString, 128);
      if (paramString != null)
      {
        if (paramString.metaData == null) {
          return false;
        }
        if (paramString.isSystemApp())
        {
          boolean bool = paramString.metaData.getBoolean("com.android.systemui.IS_BRANDED", false);
          return bool;
        }
      }
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      return false;
    }
    return false;
  }
  
  private void updateState()
  {
    SparseArray localSparseArray = new SparseArray();
    try
    {
      Iterator localIterator = this.mUserManager.getUsers().iterator();
      while (localIterator.hasNext())
      {
        UserInfo localUserInfo = (UserInfo)localIterator.next();
        VpnConfig localVpnConfig = this.mConnectivityManagerService.getVpnConfig(localUserInfo.id);
        if (localVpnConfig != null) {
          if (localVpnConfig.legacy)
          {
            LegacyVpnInfo localLegacyVpnInfo = this.mConnectivityManagerService.getLegacyVpnInfo(localUserInfo.id);
            if ((localLegacyVpnInfo == null) || (localLegacyVpnInfo.state != 3)) {}
          }
          else
          {
            localSparseArray.put(localUserInfo.id, localVpnConfig);
          }
        }
      }
      this.mCurrentVpns = localRemoteException;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("SecurityController", "Unable to list active VPNs", localRemoteException);
      return;
    }
  }
  
  public void addCallback(SecurityController.SecurityControllerCallback paramSecurityControllerCallback)
  {
    ArrayList localArrayList = this.mCallbacks;
    if (paramSecurityControllerCallback != null) {}
    try
    {
      boolean bool = this.mCallbacks.contains(paramSecurityControllerCallback);
      if (bool) {
        return;
      }
      if (DEBUG) {
        Log.d("SecurityController", "addCallback " + paramSecurityControllerCallback);
      }
      this.mCallbacks.add(paramSecurityControllerCallback);
      return;
    }
    finally {}
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("SecurityController state:");
    paramPrintWriter.print("  mCurrentVpns={");
    int i = 0;
    while (i < this.mCurrentVpns.size())
    {
      if (i > 0) {
        paramPrintWriter.print(", ");
      }
      paramPrintWriter.print(this.mCurrentVpns.keyAt(i));
      paramPrintWriter.print('=');
      paramPrintWriter.print(((VpnConfig)this.mCurrentVpns.valueAt(i)).user);
      i += 1;
    }
    paramPrintWriter.println("}");
  }
  
  public boolean isVpnBranded()
  {
    Object localObject = (VpnConfig)this.mCurrentVpns.get(this.mVpnUserId);
    if (localObject == null) {
      return false;
    }
    localObject = getPackageNameForVpnConfig((VpnConfig)localObject);
    if (localObject == null) {
      return false;
    }
    return isVpnPackageBranded((String)localObject);
  }
  
  public boolean isVpnEnabled()
  {
    int[] arrayOfInt = this.mUserManager.getProfileIdsWithDisabled(this.mVpnUserId);
    int j = arrayOfInt.length;
    int i = 0;
    while (i < j)
    {
      int k = arrayOfInt[i];
      if (this.mCurrentVpns.get(k) != null) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public void onUserSwitched(int paramInt)
  {
    this.mCurrentUserId = paramInt;
    UserInfo localUserInfo = this.mUserManager.getUserInfo(paramInt);
    if (localUserInfo.isRestricted()) {}
    for (this.mVpnUserId = localUserInfo.restrictedProfileParentId;; this.mVpnUserId = this.mCurrentUserId)
    {
      fireCallbacks();
      return;
    }
  }
  
  public void removeCallback(SecurityController.SecurityControllerCallback paramSecurityControllerCallback)
  {
    ArrayList localArrayList = this.mCallbacks;
    if (paramSecurityControllerCallback == null) {
      return;
    }
    try
    {
      if (DEBUG) {
        Log.d("SecurityController", "removeCallback " + paramSecurityControllerCallback);
      }
      this.mCallbacks.remove(paramSecurityControllerCallback);
      return;
    }
    finally {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\SecurityControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */