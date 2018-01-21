package com.android.settingslib.wifi;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkInfo.State;
import android.net.wifi.IWifiManager;
import android.net.wifi.IWifiManager.Stub;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.NetworkSelectionStatus;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TtsSpan.VerbatimBuilder;
import android.util.Log;
import android.util.LruCache;
import android.util.OpFeatures;
import com.android.settingslib.R.array;
import com.android.settingslib.R.string;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;

public class AccessPoint
  implements Comparable<AccessPoint>
{
  private String bssid;
  public boolean foundInScanResult = false;
  private boolean isCurrentConnected = false;
  private AccessPointListener mAccessPointListener;
  private WifiConfiguration mConfig;
  private final Context mContext;
  private WifiInfo mInfo;
  private NetworkInfo mNetworkInfo;
  private int mRssi = Integer.MAX_VALUE;
  public LruCache<String, ScanResult> mScanResultCache = new LruCache(32);
  private long mSeen = 0L;
  private int networkId = -1;
  private int pskType = 0;
  private int security;
  private String ssid;
  private int wapiPskType;
  
  AccessPoint(Context paramContext, ScanResult paramScanResult)
  {
    this.mContext = paramContext;
    initWithScanResult(paramScanResult);
  }
  
  AccessPoint(Context paramContext, WifiConfiguration paramWifiConfiguration)
  {
    this.mContext = paramContext;
    loadConfig(paramWifiConfiguration);
  }
  
  public static String convertToQuotedString(String paramString)
  {
    return "\"" + paramString + "\"";
  }
  
  private int getOemLevel(int paramInt)
  {
    int i = WifiManager.calculateSignalLevel(paramInt, 5);
    paramInt = i;
    if (i > 0) {
      paramInt = i - 1;
    }
    return paramInt;
  }
  
  private static int getPskType(ScanResult paramScanResult)
  {
    boolean bool1 = paramScanResult.capabilities.contains("WPA-PSK");
    boolean bool2 = paramScanResult.capabilities.contains("WPA2-PSK");
    if ((bool2) && (bool1)) {
      return 3;
    }
    if (bool2) {
      return 2;
    }
    if (bool1) {
      return 1;
    }
    Log.w("SettingsLib.AccessPoint", "Received abnormal flag string: " + paramScanResult.capabilities);
    return 0;
  }
  
  private static int getSecurity(ScanResult paramScanResult)
  {
    if (paramScanResult.capabilities.contains("WEP")) {
      return 1;
    }
    if (paramScanResult.capabilities.contains("PSK")) {
      return 2;
    }
    if (paramScanResult.capabilities.contains("EAP")) {
      return 3;
    }
    if (paramScanResult.capabilities.contains("WAPI-KEY")) {
      return 4;
    }
    if (paramScanResult.capabilities.contains("WAPI-CERT")) {
      return 5;
    }
    return 0;
  }
  
  static int getSecurity(WifiConfiguration paramWifiConfiguration)
  {
    if (paramWifiConfiguration.allowedKeyManagement.get(1)) {
      return 2;
    }
    if ((paramWifiConfiguration.allowedKeyManagement.get(2)) || (paramWifiConfiguration.allowedKeyManagement.get(3))) {
      return 3;
    }
    if (paramWifiConfiguration.allowedKeyManagement.get(6)) {
      return 4;
    }
    if (paramWifiConfiguration.allowedKeyManagement.get(7)) {
      return 5;
    }
    if (paramWifiConfiguration.wepKeys[0] != null) {
      return 1;
    }
    return 0;
  }
  
  private String getSettingsSummary(WifiConfiguration paramWifiConfiguration)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((isActive()) && (paramWifiConfiguration != null) && (paramWifiConfiguration.isPasspoint()))
    {
      localStringBuilder.append(getSummary(this.mContext, getDetailedState(), false, paramWifiConfiguration.providerFriendlyName));
      if (WifiTracker.sVerboseLogging <= 0) {
        break label654;
      }
      if ((this.mInfo != null) && (this.mNetworkInfo != null) && (this.isCurrentConnected)) {
        localStringBuilder.append(" f=").append(Integer.toString(this.mInfo.getFrequency()));
      }
      localStringBuilder.append(" ").append(getVisibilityStatus());
      if ((paramWifiConfiguration != null) && (!paramWifiConfiguration.getNetworkSelectionStatus().isNetworkEnabled())) {
        break label499;
      }
    }
    for (;;)
    {
      if (paramWifiConfiguration == null) {
        break label654;
      }
      paramWifiConfiguration = paramWifiConfiguration.getNetworkSelectionStatus();
      int i = 0;
      while (i < 10)
      {
        if (paramWifiConfiguration.getDisableReasonCounter(i) != 0) {
          localStringBuilder.append(" ").append(WifiConfiguration.NetworkSelectionStatus.getNetworkDisableReasonString(i)).append("=").append(paramWifiConfiguration.getDisableReasonCounter(i));
        }
        i += 1;
      }
      if (isActive())
      {
        Context localContext = this.mContext;
        NetworkInfo.DetailedState localDetailedState = getDetailedState();
        if (this.mInfo != null) {}
        for (boolean bool = this.mInfo.isEphemeral();; bool = false)
        {
          localStringBuilder.append(getSummary(localContext, localDetailedState, bool));
          break;
        }
      }
      if ((paramWifiConfiguration != null) && (paramWifiConfiguration.isPasspoint()))
      {
        localStringBuilder.append(String.format(this.mContext.getString(R.string.available_via_passpoint), new Object[] { paramWifiConfiguration.providerFriendlyName }));
        break;
      }
      if ((paramWifiConfiguration != null) && (paramWifiConfiguration.hasNoInternetAccess()))
      {
        if (paramWifiConfiguration.getNetworkSelectionStatus().isNetworkPermanentlyDisabled()) {}
        for (i = R.string.wifi_no_internet_no_reconnect;; i = R.string.wifi_no_internet)
        {
          localStringBuilder.append(this.mContext.getString(i));
          break;
        }
      }
      if ((paramWifiConfiguration == null) || (paramWifiConfiguration.getNetworkSelectionStatus().isNetworkEnabled()))
      {
        if (this.mRssi != Integer.MAX_VALUE) {
          break label476;
        }
        localStringBuilder.append(this.mContext.getString(R.string.wifi_not_in_range));
        break;
      }
      switch (paramWifiConfiguration.getNetworkSelectionStatus().getNetworkSelectionDisableReason())
      {
      default: 
        break;
      case 2: 
        localStringBuilder.append(this.mContext.getString(R.string.wifi_disabled_generic));
        break;
      case 3: 
        localStringBuilder.append(this.mContext.getString(R.string.wifi_disabled_password_failure));
        break;
      case 4: 
      case 5: 
        localStringBuilder.append(this.mContext.getString(R.string.wifi_disabled_network_failure));
        break;
      }
      label476:
      if (paramWifiConfiguration == null) {
        break;
      }
      localStringBuilder.append(this.mContext.getString(R.string.wifi_remembered));
      break;
      label499:
      localStringBuilder.append(" (").append(paramWifiConfiguration.getNetworkSelectionStatus().getNetworkStatusString());
      if (paramWifiConfiguration.getNetworkSelectionStatus().getDisableTime() > 0L)
      {
        long l1 = (System.currentTimeMillis() - paramWifiConfiguration.getNetworkSelectionStatus().getDisableTime()) / 1000L;
        long l2 = l1 / 60L % 60L;
        long l3 = l2 / 60L % 60L;
        localStringBuilder.append(", ");
        if (l3 > 0L) {
          localStringBuilder.append(Long.toString(l3)).append("h ");
        }
        localStringBuilder.append(Long.toString(l2)).append("m ");
        localStringBuilder.append(Long.toString(l1 % 60L)).append("s ");
      }
      localStringBuilder.append(")");
    }
    label654:
    return localStringBuilder.toString();
  }
  
  public static String getSummary(Context paramContext, NetworkInfo.DetailedState paramDetailedState, boolean paramBoolean)
  {
    return getSummary(paramContext, null, paramDetailedState, paramBoolean, null);
  }
  
  public static String getSummary(Context paramContext, NetworkInfo.DetailedState paramDetailedState, boolean paramBoolean, String paramString)
  {
    return getSummary(paramContext, null, paramDetailedState, paramBoolean, paramString);
  }
  
  public static String getSummary(Context paramContext, String paramString1, NetworkInfo.DetailedState paramDetailedState, boolean paramBoolean, String paramString2)
  {
    if ((paramDetailedState == NetworkInfo.DetailedState.CONNECTED) && (paramString1 == null))
    {
      if (!TextUtils.isEmpty(paramString2)) {
        return String.format(paramContext.getString(R.string.connected_via_passpoint), new Object[] { paramString2 });
      }
      if (paramBoolean) {
        return paramContext.getString(R.string.connected_via_wfa);
      }
    }
    ConnectivityManager localConnectivityManager = (ConnectivityManager)paramContext.getSystemService("connectivity");
    if (paramDetailedState == NetworkInfo.DetailedState.CONNECTED) {
      paramString2 = IWifiManager.Stub.asInterface(ServiceManager.getService("wifi"));
    }
    try
    {
      paramString2 = paramString2.getCurrentNetwork();
      paramString2 = localConnectivityManager.getNetworkCapabilities(paramString2);
      if ((!OpFeatures.isSupport(new int[] { 1 })) || (paramString2 == null) || (paramString2.hasCapability(16)))
      {
        if (paramDetailedState != null) {
          break label160;
        }
        Log.w("SettingsLib.AccessPoint", "state is null, returning empty summary");
        return "";
      }
    }
    catch (RemoteException paramString2)
    {
      for (;;)
      {
        paramString2 = null;
      }
    }
    return paramContext.getString(R.string.wifi_connected_no_internet);
    label160:
    paramContext = paramContext.getResources();
    if (paramString1 == null) {}
    for (int i = R.array.wifi_status;; i = R.array.wifi_status_with_ssid)
    {
      paramContext = paramContext.getStringArray(i);
      i = paramDetailedState.ordinal();
      if ((i < paramContext.length) && (paramContext[i].length() != 0)) {
        break;
      }
      return "";
    }
    return String.format(paramContext[i], new Object[] { paramString1 });
  }
  
  private String getVisibilityStatus()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Object localObject2 = null;
    Object localObject1 = null;
    String str = null;
    System.currentTimeMillis();
    if (this.mInfo != null)
    {
      str = this.mInfo.getBSSID();
      if (str != null) {
        localStringBuilder.append(" ").append(str);
      }
      localStringBuilder.append(" rssi=").append(this.mInfo.getRssi());
      localStringBuilder.append(" ");
      localStringBuilder.append(" score=").append(this.mInfo.score);
      localStringBuilder.append(String.format(" tx=%.1f,", new Object[] { Double.valueOf(this.mInfo.txSuccessRate) }));
      localStringBuilder.append(String.format("%.1f,", new Object[] { Double.valueOf(this.mInfo.txRetriesRate) }));
      localStringBuilder.append(String.format("%.1f ", new Object[] { Double.valueOf(this.mInfo.txBadRate) }));
      localStringBuilder.append(String.format("rx=%.1f", new Object[] { Double.valueOf(this.mInfo.rxSuccessRate) }));
    }
    int i = WifiConfiguration.INVALID_RSSI;
    int j = WifiConfiguration.INVALID_RSSI;
    int n = 0;
    int i3 = 0;
    int m = 0;
    int k = 0;
    Iterator localIterator = this.mScanResultCache.snapshot().values().iterator();
    while (localIterator.hasNext())
    {
      ScanResult localScanResult = (ScanResult)localIterator.next();
      int i2;
      int i1;
      if ((localScanResult.frequency >= 4900) && (localScanResult.frequency <= 5900))
      {
        i2 = n + 1;
        i1 = i3;
      }
      int i4;
      Object localObject3;
      for (;;)
      {
        if ((localScanResult.frequency < 4900) || (localScanResult.frequency > 5900)) {
          break label544;
        }
        i4 = i;
        if (localScanResult.level > i) {
          i4 = localScanResult.level;
        }
        i3 = i1;
        n = i2;
        i = i4;
        if (k >= 4) {
          break;
        }
        localObject3 = localObject1;
        if (localObject1 == null) {
          localObject3 = new StringBuilder();
        }
        ((StringBuilder)localObject3).append(" \n{").append(localScanResult.BSSID);
        if ((str != null) && (localScanResult.BSSID.equals(str))) {
          ((StringBuilder)localObject3).append("*");
        }
        ((StringBuilder)localObject3).append("=").append(localScanResult.frequency);
        ((StringBuilder)localObject3).append(",").append(localScanResult.level);
        ((StringBuilder)localObject3).append("}");
        k += 1;
        i3 = i1;
        n = i2;
        i = i4;
        localObject1 = localObject3;
        break;
        i1 = i3;
        i2 = n;
        if (localScanResult.frequency >= 2400)
        {
          i1 = i3;
          i2 = n;
          if (localScanResult.frequency <= 2500)
          {
            i1 = i3 + 1;
            i2 = n;
          }
        }
      }
      label544:
      i3 = i1;
      n = i2;
      if (localScanResult.frequency >= 2400)
      {
        i3 = i1;
        n = i2;
        if (localScanResult.frequency <= 2500)
        {
          i4 = j;
          if (localScanResult.level > j) {
            i4 = localScanResult.level;
          }
          i3 = i1;
          n = i2;
          j = i4;
          if (m < 4)
          {
            localObject3 = localObject2;
            if (localObject2 == null) {
              localObject3 = new StringBuilder();
            }
            ((StringBuilder)localObject3).append(" \n{").append(localScanResult.BSSID);
            if ((str != null) && (localScanResult.BSSID.equals(str))) {
              ((StringBuilder)localObject3).append("*");
            }
            ((StringBuilder)localObject3).append("=").append(localScanResult.frequency);
            ((StringBuilder)localObject3).append(",").append(localScanResult.level);
            ((StringBuilder)localObject3).append("}");
            m += 1;
            i3 = i1;
            n = i2;
            j = i4;
            localObject2 = localObject3;
          }
        }
      }
    }
    localStringBuilder.append(" [");
    if (i3 > 0)
    {
      localStringBuilder.append("(").append(i3).append(")");
      if (m > 4) {
        break label872;
      }
      if (localObject2 != null) {
        localStringBuilder.append(((StringBuilder)localObject2).toString());
      }
    }
    localStringBuilder.append(";");
    if (n > 0)
    {
      localStringBuilder.append("(").append(n).append(")");
      if (k > 4) {
        break label910;
      }
      if (localObject1 != null) {
        localStringBuilder.append(((StringBuilder)localObject1).toString());
      }
    }
    for (;;)
    {
      localStringBuilder.append("]");
      return localStringBuilder.toString();
      label872:
      localStringBuilder.append("max=").append(j);
      if (localObject2 == null) {
        break;
      }
      localStringBuilder.append(",").append(((StringBuilder)localObject2).toString());
      break;
      label910:
      localStringBuilder.append("max=").append(i);
      if (localObject1 != null) {
        localStringBuilder.append(",").append(((StringBuilder)localObject1).toString());
      }
    }
  }
  
  private void initWithScanResult(ScanResult paramScanResult)
  {
    this.ssid = paramScanResult.SSID;
    this.bssid = paramScanResult.BSSID;
    this.security = getSecurity(paramScanResult);
    if (this.security == 2) {
      this.pskType = getPskType(paramScanResult);
    }
    this.mRssi = paramScanResult.level;
    this.mSeen = paramScanResult.timestamp;
  }
  
  private boolean isInfoForThisAccessPoint(WifiConfiguration paramWifiConfiguration, WifiInfo paramWifiInfo)
  {
    if ((!isPasspoint()) && (this.networkId != -1)) {
      return this.networkId == paramWifiInfo.getNetworkId();
    }
    if (paramWifiConfiguration != null) {
      return matches(paramWifiConfiguration);
    }
    return this.ssid.equals(removeDoubleQuotes(paramWifiInfo.getSSID()));
  }
  
  private boolean isValiableConnectedBssid()
  {
    return (this.isCurrentConnected) && (this.bssid != null) && (!this.bssid.equals("00:00:00:00:00:00"));
  }
  
  static String removeDoubleQuotes(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return "";
    }
    int i = paramString.length();
    if ((i > 1) && (paramString.charAt(0) == '"') && (paramString.charAt(i - 1) == '"')) {
      return paramString.substring(1, i - 1);
    }
    return paramString;
  }
  
  public static String securityToString(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 1) {
      return "WEP";
    }
    if (paramInt1 == 2)
    {
      if (paramInt2 == 1) {
        return "WPA";
      }
      if (paramInt2 == 2) {
        return "WPA2";
      }
      if (paramInt2 == 3) {
        return "WPA_WPA2";
      }
      return "PSK";
    }
    if (paramInt1 == 3) {
      return "EAP";
    }
    return "NONE";
  }
  
  public void clearConfig()
  {
    this.mConfig = null;
    this.networkId = -1;
  }
  
  public int compareTo(@NonNull AccessPoint paramAccessPoint)
  {
    if ((!isActive()) || (paramAccessPoint.isActive()))
    {
      if ((!isActive()) && (paramAccessPoint.isActive())) {
        return 1;
      }
    }
    else {
      return -1;
    }
    if ((this.mRssi != Integer.MAX_VALUE) && (paramAccessPoint.mRssi == Integer.MAX_VALUE)) {
      return -1;
    }
    if ((this.mRssi == Integer.MAX_VALUE) && (paramAccessPoint.mRssi != Integer.MAX_VALUE)) {
      return 1;
    }
    if ((this.networkId != -1) && (paramAccessPoint.networkId == -1)) {
      return -1;
    }
    if ((this.networkId == -1) && (paramAccessPoint.networkId != -1)) {
      return 1;
    }
    int i = getOemLevel(paramAccessPoint.mRssi) - getOemLevel(this.mRssi);
    if (i != 0) {
      return i;
    }
    return this.ssid.compareToIgnoreCase(paramAccessPoint.ssid);
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if (!(paramObject instanceof AccessPoint)) {
      return false;
    }
    if (compareTo((AccessPoint)paramObject) == 0) {
      bool = true;
    }
    return bool;
  }
  
  public void generateOpenNetworkConfig()
  {
    if (this.security != 0) {
      throw new IllegalStateException();
    }
    if (this.mConfig != null) {
      return;
    }
    this.mConfig = new WifiConfiguration();
    this.mConfig.SSID = convertToQuotedString(this.ssid);
    this.mConfig.allowedKeyManagement.set(0);
  }
  
  public WifiConfiguration getConfig()
  {
    return this.mConfig;
  }
  
  public NetworkInfo.DetailedState getDetailedState()
  {
    if ((this.mNetworkInfo != null) && (this.isCurrentConnected)) {
      return this.mNetworkInfo.getDetailedState();
    }
    Log.w("SettingsLib.AccessPoint", "NetworkInfo is null, cannot return detailed state");
    return null;
  }
  
  public int getLevel()
  {
    if (this.mRssi == Integer.MAX_VALUE) {
      return -1;
    }
    return getOemLevel(this.mRssi);
  }
  
  public int getRssi()
  {
    int i = Integer.MIN_VALUE;
    Iterator localIterator = this.mScanResultCache.snapshot().values().iterator();
    while (localIterator.hasNext())
    {
      ScanResult localScanResult = (ScanResult)localIterator.next();
      if (isValiableConnectedBssid())
      {
        if ((this.bssid.equals(localScanResult.BSSID)) && (localScanResult.level > i)) {
          i = localScanResult.level;
        }
      }
      else if (localScanResult.level > i) {
        i = localScanResult.level;
      }
    }
    return i;
  }
  
  public int getSecurity()
  {
    return this.security;
  }
  
  public long getSeen()
  {
    long l = 0L;
    Iterator localIterator = this.mScanResultCache.snapshot().values().iterator();
    while (localIterator.hasNext())
    {
      ScanResult localScanResult = (ScanResult)localIterator.next();
      if (localScanResult.timestamp > l) {
        l = localScanResult.timestamp;
      }
    }
    return l;
  }
  
  public CharSequence getSsid()
  {
    SpannableString localSpannableString = new SpannableString(this.ssid);
    localSpannableString.setSpan(new TtsSpan.VerbatimBuilder(this.ssid).build(), 0, this.ssid.length(), 18);
    return localSpannableString;
  }
  
  public String getSsidStr()
  {
    return this.ssid;
  }
  
  public String getSummary()
  {
    return getSettingsSummary(this.mConfig);
  }
  
  public int hashCode()
  {
    int i = 0;
    if (this.mInfo != null) {
      i = this.mInfo.hashCode() * 13 + 0;
    }
    return i + this.mRssi * 19 + this.networkId * 23 + this.ssid.hashCode() * 29;
  }
  
  public boolean isActive()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mNetworkInfo != null) {
      if (this.networkId == -1)
      {
        bool1 = bool2;
        if (this.mNetworkInfo.getState() == NetworkInfo.State.DISCONNECTED) {}
      }
      else
      {
        bool1 = this.isCurrentConnected;
      }
    }
    return bool1;
  }
  
  public boolean isConnectable()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (getLevel() != -1)
    {
      bool1 = bool2;
      if (getDetailedState() == null) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean isEphemeral()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mInfo != null)
    {
      bool1 = bool2;
      if (this.mInfo.isEphemeral())
      {
        bool1 = bool2;
        if (this.mNetworkInfo != null)
        {
          bool1 = bool2;
          if (this.isCurrentConnected)
          {
            bool1 = bool2;
            if (this.mNetworkInfo.getState() != NetworkInfo.State.DISCONNECTED) {
              bool1 = true;
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public boolean isPasspoint()
  {
    if (this.mConfig != null) {
      return this.mConfig.isPasspoint();
    }
    return false;
  }
  
  public boolean isSaved()
  {
    return (this.networkId != -1) || ((this.mConfig != null) && (this.mConfig.networkId != -1));
  }
  
  void loadConfig(WifiConfiguration paramWifiConfiguration)
  {
    if (paramWifiConfiguration.isPasspoint())
    {
      this.ssid = paramWifiConfiguration.providerFriendlyName;
      this.bssid = paramWifiConfiguration.BSSID;
      this.security = getSecurity(paramWifiConfiguration);
      this.networkId = paramWifiConfiguration.networkId;
      this.mConfig = paramWifiConfiguration;
      this.wapiPskType = paramWifiConfiguration.wapiPskType;
      Log.e("SettingsLib.AccessPoint", "loadConfig() ssid:" + this.ssid + "  WAPI PSK key type: " + this.wapiPskType + ", networkId: " + this.networkId);
      return;
    }
    if (paramWifiConfiguration.SSID == null) {}
    for (String str = "";; str = removeDoubleQuotes(paramWifiConfiguration.SSID))
    {
      this.ssid = str;
      break;
    }
  }
  
  public boolean matches(ScanResult paramScanResult)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.ssid.equals(paramScanResult.SSID))
    {
      bool1 = bool2;
      if (this.security == getSecurity(paramScanResult)) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean matches(WifiConfiguration paramWifiConfiguration)
  {
    if ((paramWifiConfiguration.isPasspoint()) && (this.mConfig != null) && (this.mConfig.isPasspoint())) {
      return paramWifiConfiguration.FQDN.equals(this.mConfig.FQDN);
    }
    if ((this.ssid.equals(removeDoubleQuotes(paramWifiConfiguration.SSID))) && (this.security == getSecurity(paramWifiConfiguration))) {
      return (this.mConfig == null) || (this.mConfig.shared == paramWifiConfiguration.shared);
    }
    return false;
  }
  
  void setRssi(int paramInt)
  {
    this.mRssi = paramInt;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("AccessPoint(").append(this.ssid);
    if (isSaved()) {
      localStringBuilder.append(',').append("saved");
    }
    if (isActive()) {
      localStringBuilder.append(',').append("active");
    }
    if (isEphemeral()) {
      localStringBuilder.append(',').append("ephemeral");
    }
    if (isConnectable()) {
      localStringBuilder.append(',').append("connectable");
    }
    if (this.security != 0) {
      localStringBuilder.append(',').append(securityToString(this.security, this.pskType));
    }
    return ')';
  }
  
  void update(WifiConfiguration paramWifiConfiguration)
  {
    this.mConfig = paramWifiConfiguration;
    this.networkId = paramWifiConfiguration.networkId;
    if (this.mAccessPointListener != null) {
      this.mAccessPointListener.onAccessPointChanged(this);
    }
  }
  
  boolean update(ScanResult paramScanResult)
  {
    if (matches(paramScanResult))
    {
      this.mScanResultCache.get(paramScanResult.BSSID);
      this.mScanResultCache.put(paramScanResult.BSSID, paramScanResult);
      int k = getLevel();
      int j = getRssi();
      int i;
      if (isValiableConnectedBssid()) {
        if (this.bssid.equals(paramScanResult.BSSID)) {
          i = paramScanResult.level;
        }
      }
      for (;;)
      {
        this.mSeen = getSeen();
        this.mRssi = ((i + j) / 2);
        i = WifiManager.calculateSignalLevel(this.mRssi, 5);
        if ((i > 0) && (i != k) && (this.mAccessPointListener != null)) {
          this.mAccessPointListener.onLevelChanged(this);
        }
        if (this.security == 2) {
          this.pskType = getPskType(paramScanResult);
        }
        if (this.mAccessPointListener != null) {
          this.mAccessPointListener.onAccessPointChanged(this);
        }
        return true;
        i = j;
        continue;
        i = paramScanResult.level;
      }
    }
    return false;
  }
  
  boolean update(WifiConfiguration paramWifiConfiguration, WifiInfo paramWifiInfo, NetworkInfo paramNetworkInfo)
  {
    boolean bool2 = false;
    boolean bool1;
    if ((paramWifiInfo != null) && (isInfoForThisAccessPoint(paramWifiConfiguration, paramWifiInfo))) {
      if (this.isCurrentConnected)
      {
        bool1 = false;
        this.mRssi = paramWifiInfo.getRssi();
        this.mInfo = paramWifiInfo;
        this.mNetworkInfo = paramNetworkInfo;
        this.isCurrentConnected = true;
        this.bssid = paramWifiInfo.getBSSID();
        bool2 = bool1;
        if (this.mAccessPointListener != null)
        {
          this.mAccessPointListener.onAccessPointChanged(this);
          bool2 = bool1;
        }
      }
    }
    do
    {
      do
      {
        return bool2;
        bool1 = true;
        break;
      } while (this.mInfo == null);
      bool2 = true;
      if (this.isCurrentConnected) {
        this.isCurrentConnected = false;
      }
    } while (this.mAccessPointListener == null);
    this.mAccessPointListener.onAccessPointChanged(this);
    return true;
  }
  
  public static abstract interface AccessPointListener
  {
    public abstract void onAccessPointChanged(AccessPoint paramAccessPoint);
    
    public abstract void onLevelChanged(AccessPoint paramAccessPoint);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\settingslib\wifi\AccessPoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */