package com.android.settingslib.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.INetworkStatsService;
import android.net.INetworkStatsService.Stub;
import android.net.NetworkPolicyManager;
import android.os.ServiceManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.util.Formatter;
import java.util.Locale;

public class DataUsageController
{
  private static final boolean DEBUG = Log.isLoggable("DataUsageController", 3);
  private static final StringBuilder PERIOD_BUILDER = new StringBuilder(50);
  private static final Formatter PERIOD_FORMATTER = new Formatter(PERIOD_BUILDER, Locale.getDefault());
  private Callback mCallback;
  private final ConnectivityManager mConnectivityManager;
  private final Context mContext;
  private NetworkNameProvider mNetworkController;
  private final NetworkPolicyManager mPolicyManager;
  private final INetworkStatsService mStatsService;
  private SubscriptionManager mSubscriptionManager;
  private final TelephonyManager mTelephonyManager;
  
  public DataUsageController(Context paramContext)
  {
    this.mContext = paramContext;
    this.mTelephonyManager = TelephonyManager.from(paramContext);
    this.mConnectivityManager = ConnectivityManager.from(paramContext);
    this.mStatsService = INetworkStatsService.Stub.asInterface(ServiceManager.getService("netstats"));
    this.mPolicyManager = NetworkPolicyManager.from(this.mContext);
    this.mSubscriptionManager = SubscriptionManager.from(this.mContext);
  }
  
  public boolean isMobileDataEnabled()
  {
    return this.mTelephonyManager.getDataEnabled();
  }
  
  public boolean isMobileDataSupported()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mConnectivityManager.isNetworkSupported(0))
    {
      bool1 = bool2;
      if (this.mTelephonyManager.getSimState() == 5) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public void setCallback(Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  public void setMobileDataEnabled(boolean paramBoolean)
  {
    Log.d("DataUsageController", "setMobileDataEnabled: enabled=" + paramBoolean);
    int[] arrayOfInt = this.mSubscriptionManager.getActiveSubscriptionIdList();
    if (arrayOfInt.length == 0)
    {
      this.mTelephonyManager.setDataEnabled(paramBoolean);
      if (this.mCallback != null) {
        this.mCallback.onMobileDataEnabled(paramBoolean);
      }
      return;
    }
    int i = 0;
    label66:
    if (i < arrayOfInt.length)
    {
      if (arrayOfInt[i] < 0) {
        break label96;
      }
      this.mTelephonyManager.setDataEnabled(arrayOfInt[i], paramBoolean);
    }
    for (;;)
    {
      i += 1;
      break label66;
      break;
      label96:
      Log.d("DataUsageController", "setMobileDataEnabled: negative subId[i]=" + arrayOfInt[i]);
    }
  }
  
  public void setNetworkController(NetworkNameProvider paramNetworkNameProvider)
  {
    this.mNetworkController = paramNetworkNameProvider;
  }
  
  public static abstract interface Callback
  {
    public abstract void onMobileDataEnabled(boolean paramBoolean);
  }
  
  public static class DataUsageInfo
  {
    public String carrier;
    public long limitLevel;
    public String period;
    public long startDate;
    public long usageLevel;
    public long warningLevel;
  }
  
  public static abstract interface NetworkNameProvider {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\settingslib\net\DataUsageController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */