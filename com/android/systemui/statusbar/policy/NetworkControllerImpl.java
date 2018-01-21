package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Global;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionManager.OnSubscriptionsChangedListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.MathUtils;
import android.widget.TextView;
import com.android.settingslib.net.DataUsageController;
import com.android.settingslib.net.DataUsageController.Callback;
import com.android.settingslib.net.DataUsageController.NetworkNameProvider;
import com.android.systemui.DemoMode;
import com.android.systemui.statusbar.phone.NetworkSpeedController;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NetworkControllerImpl
  extends BroadcastReceiver
  implements NetworkController, DemoMode, DataUsageController.NetworkNameProvider
{
  static final boolean CHATTY = Log.isLoggable("NetworkControllerChat", 3);
  static final boolean DEBUG = Build.DEBUG_ONEPLUS;
  private final AccessPointControllerImpl mAccessPoints;
  private boolean mAirplaneMode = false;
  private final CallbackHandler mCallbackHandler;
  private Config mConfig;
  private final BitSet mConnectedTransports = new BitSet();
  private final ConnectivityManager mConnectivityManager;
  private final Context mContext;
  private List<SubscriptionInfo> mCurrentSubscriptions = new ArrayList();
  private int mCurrentUserId;
  private final DataSaverController mDataSaverController;
  private final DataUsageController mDataUsageController;
  private MobileSignalController mDefaultSignalController;
  private boolean mDemoInetCondition;
  private boolean mDemoMode;
  private WifiSignalController.WifiState mDemoWifiState;
  private int mEmergencySource;
  final EthernetSignalController mEthernetSignalController;
  private final boolean mHasMobileDataFeature;
  private boolean mHasNoSims;
  private boolean mInetCondition;
  private boolean mIsEmbmsActive;
  private boolean mIsEmergency;
  private boolean[] mLTEstatus = { 0, 0, 0, 0, 0, 0 };
  ServiceState mLastServiceState;
  boolean mListening;
  private Locale mLocale = null;
  final Map<Integer, MobileSignalController> mMobileSignalControllers = new HashMap();
  private TextView mNetWorkNameLabelView;
  private NetworkSpeedController mNetworkSpeedController;
  private final TelephonyManager mPhone;
  private PhoneStatusBar mPhoneStatusBar;
  private final Handler mReceiverHandler;
  private final Runnable mRegisterListeners = new Runnable()
  {
    public void run()
    {
      NetworkControllerImpl.-wrap0(NetworkControllerImpl.this);
    }
  };
  private final SubscriptionDefaults mSubDefaults;
  private SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionListener;
  private final SubscriptionManager mSubscriptionManager;
  private boolean mUserSetup;
  private final BitSet mValidatedTransports = new BitSet();
  private final WifiManager mWifiManager;
  final WifiSignalController mWifiSignalController;
  
  NetworkControllerImpl(Context paramContext, ConnectivityManager paramConnectivityManager, TelephonyManager paramTelephonyManager, WifiManager paramWifiManager, SubscriptionManager paramSubscriptionManager, Config paramConfig, Looper paramLooper, CallbackHandler paramCallbackHandler, AccessPointControllerImpl paramAccessPointControllerImpl, DataUsageController paramDataUsageController, SubscriptionDefaults paramSubscriptionDefaults)
  {
    this.mContext = paramContext;
    this.mConfig = paramConfig;
    this.mReceiverHandler = new Handler(paramLooper);
    this.mCallbackHandler = paramCallbackHandler;
    this.mDataSaverController = new DataSaverController(paramContext);
    this.mSubscriptionManager = paramSubscriptionManager;
    this.mSubDefaults = paramSubscriptionDefaults;
    this.mConnectivityManager = paramConnectivityManager;
    this.mHasMobileDataFeature = this.mConnectivityManager.isNetworkSupported(0);
    this.mPhone = paramTelephonyManager;
    this.mWifiManager = paramWifiManager;
    this.mLocale = this.mContext.getResources().getConfiguration().locale;
    this.mAccessPoints = paramAccessPointControllerImpl;
    this.mDataUsageController = paramDataUsageController;
    this.mDataUsageController.setNetworkController(this);
    this.mDataUsageController.setCallback(new DataUsageController.Callback()
    {
      public void onMobileDataEnabled(boolean paramAnonymousBoolean)
      {
        NetworkControllerImpl.-get0(NetworkControllerImpl.this).setMobileDataEnabled(paramAnonymousBoolean);
      }
    });
    this.mWifiSignalController = new WifiSignalController(this.mContext, this.mHasMobileDataFeature, this.mCallbackHandler, this);
    this.mEthernetSignalController = new EthernetSignalController(this.mContext, this.mCallbackHandler, this);
    updateAirplaneMode(true);
  }
  
  public NetworkControllerImpl(Context paramContext, Looper paramLooper)
  {
    this(paramContext, (ConnectivityManager)paramContext.getSystemService("connectivity"), (TelephonyManager)paramContext.getSystemService("phone"), (WifiManager)paramContext.getSystemService("wifi"), SubscriptionManager.from(paramContext), Config.readConfig(paramContext), paramLooper, new CallbackHandler(), new AccessPointControllerImpl(paramContext, paramLooper), new DataUsageController(paramContext), new SubscriptionDefaults());
    this.mReceiverHandler.post(this.mRegisterListeners);
  }
  
  private SubscriptionInfo addSignalController(int paramInt1, int paramInt2)
  {
    SubscriptionInfo localSubscriptionInfo = new SubscriptionInfo(paramInt1, "", paramInt2, "", "", 0, 0, "", 0, null, 0, 0, "");
    this.mMobileSignalControllers.put(Integer.valueOf(paramInt1), new MobileSignalController(this.mContext, this.mConfig, this.mHasMobileDataFeature, this.mPhone, this.mCallbackHandler, this, localSubscriptionInfo, this.mSubDefaults, this.mReceiverHandler.getLooper()));
    return localSubscriptionInfo;
  }
  
  private static final String emergencyToString(int paramInt)
  {
    if (paramInt > 300) {
      return "NO_SUB(" + (paramInt - 300) + ")";
    }
    if (paramInt > 200) {
      return "VOICE_CONTROLLER(" + (paramInt - 200) + ")";
    }
    if (paramInt > 100) {
      return "FIRST_CONTROLLER(" + (paramInt - 100) + ")";
    }
    if (paramInt == 0) {
      return "NO_CONTROLLERS";
    }
    return "UNKNOWN_SOURCE";
  }
  
  private MobileSignalController getDataController()
  {
    int i = this.mSubDefaults.getDefaultDataSubId();
    if (!SubscriptionManager.isValidSubscriptionId(i))
    {
      if (DEBUG) {
        Log.e("NetworkController", "No data sim selected");
      }
      return this.mDefaultSignalController;
    }
    if (this.mMobileSignalControllers.containsKey(Integer.valueOf(i))) {
      return (MobileSignalController)this.mMobileSignalControllers.get(Integer.valueOf(i));
    }
    if (DEBUG) {
      Log.e("NetworkController", "Cannot find controller for data sub: " + i);
    }
    return this.mDefaultSignalController;
  }
  
  private boolean isLTEStatusChange(boolean[] paramArrayOfBoolean)
  {
    boolean bool = false;
    int i = 0;
    while (i < paramArrayOfBoolean.length)
    {
      if (DEBUG) {
        Log.v("NetworkController", "isLTEStatusChange mLTEstatus:" + this.mLTEstatus[i] + " ltestatus:" + paramArrayOfBoolean[i]);
      }
      if (this.mLTEstatus[i] != paramArrayOfBoolean[i]) {
        bool = true;
      }
      i += 1;
    }
    if (DEBUG) {
      Log.i("NetworkController", "isLTEStatusChange" + bool);
    }
    return bool;
  }
  
  private void notifyAllListeners()
  {
    notifyListeners();
    Iterator localIterator = this.mMobileSignalControllers.values().iterator();
    while (localIterator.hasNext()) {
      ((MobileSignalController)localIterator.next()).notifyListeners();
    }
    this.mWifiSignalController.notifyListeners();
    this.mEthernetSignalController.notifyListeners();
  }
  
  private void notifyListeners()
  {
    this.mCallbackHandler.setIsAirplaneMode(new NetworkController.IconState(this.mAirplaneMode, 2130838389, 2131690162, this.mContext));
    this.mCallbackHandler.setNoSims(this.mHasNoSims);
  }
  
  private void pushConnectivityToSignals()
  {
    Iterator localIterator = this.mMobileSignalControllers.values().iterator();
    while (localIterator.hasNext()) {
      ((MobileSignalController)localIterator.next()).updateConnectivity(this.mConnectedTransports, this.mValidatedTransports);
    }
    this.mWifiSignalController.updateConnectivity(this.mConnectedTransports, this.mValidatedTransports);
    this.mEthernetSignalController.updateConnectivity(this.mConnectedTransports, this.mValidatedTransports);
    if (this.mNetworkSpeedController != null) {
      this.mNetworkSpeedController.updateConnectivity(this.mConnectedTransports, this.mValidatedTransports);
    }
  }
  
  private void refreshLocale()
  {
    Locale localLocale = this.mContext.getResources().getConfiguration().locale;
    if (!localLocale.equals(this.mLocale))
    {
      this.mLocale = localLocale;
      notifyAllListeners();
    }
  }
  
  private void registerListeners()
  {
    Object localObject = this.mMobileSignalControllers.values().iterator();
    while (((Iterator)localObject).hasNext()) {
      ((MobileSignalController)((Iterator)localObject).next()).registerListener();
    }
    if (this.mSubscriptionListener == null) {
      this.mSubscriptionListener = new SubListener(null);
    }
    this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mSubscriptionListener);
    localObject = new IntentFilter();
    ((IntentFilter)localObject).addAction("android.net.wifi.RSSI_CHANGED");
    ((IntentFilter)localObject).addAction("android.net.wifi.WIFI_STATE_CHANGED");
    ((IntentFilter)localObject).addAction("android.net.wifi.STATE_CHANGE");
    ((IntentFilter)localObject).addAction("android.intent.action.SIM_STATE_CHANGED");
    ((IntentFilter)localObject).addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
    ((IntentFilter)localObject).addAction("android.intent.action.ACTION_DEFAULT_VOICE_SUBSCRIPTION_CHANGED");
    ((IntentFilter)localObject).addAction("android.intent.action.SERVICE_STATE");
    ((IntentFilter)localObject).addAction("android.provider.Telephony.SPN_STRINGS_UPDATED");
    ((IntentFilter)localObject).addAction("android.net.conn.CONNECTIVITY_CHANGE");
    ((IntentFilter)localObject).addAction("android.net.conn.INET_CONDITION_ACTION");
    ((IntentFilter)localObject).addAction("android.intent.action.LOCALE_CHANGED");
    ((IntentFilter)localObject).addAction("android.intent.action.AIRPLANE_MODE");
    if (MobileSignalController.isCarrierOneSupported()) {
      ((IntentFilter)localObject).addAction("com.qualcomm.intent.EMBMS_STATUS");
    }
    this.mContext.registerReceiver(this, (IntentFilter)localObject, null, this.mReceiverHandler);
    this.mListening = true;
    updateMobileControllers();
  }
  
  private void setTextViewVisibility(TextView paramTextView)
  {
    String str = getMobileDataNetworkName();
    if ((str.equals(this.mContext.getString(17040033))) || (str.equals(this.mContext.getString(17040058))))
    {
      paramTextView.setVisibility(8);
      return;
    }
    paramTextView.setVisibility(0);
  }
  
  private void unregisterListeners()
  {
    this.mListening = false;
    Iterator localIterator = this.mMobileSignalControllers.values().iterator();
    while (localIterator.hasNext()) {
      ((MobileSignalController)localIterator.next()).unregisterListener();
    }
    this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mSubscriptionListener);
    this.mContext.unregisterReceiver(this);
  }
  
  private void updateAirplaneMode(boolean paramBoolean)
  {
    boolean bool;
    if (Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1) {
      bool = true;
    }
    while ((bool != this.mAirplaneMode) || (paramBoolean))
    {
      this.mAirplaneMode = bool;
      Iterator localIterator = this.mMobileSignalControllers.values().iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          ((MobileSignalController)localIterator.next()).setAirplaneMode(this.mAirplaneMode);
          continue;
          bool = false;
          break;
        }
      }
      notifyListeners();
    }
  }
  
  private void updateConnectivity()
  {
    boolean bool = false;
    this.mConnectedTransports.clear();
    this.mValidatedTransports.clear();
    NetworkCapabilities[] arrayOfNetworkCapabilities = this.mConnectivityManager.getDefaultNetworkCapabilitiesForUser(this.mCurrentUserId);
    int k = arrayOfNetworkCapabilities.length;
    int i = 0;
    while (i < k)
    {
      NetworkCapabilities localNetworkCapabilities = arrayOfNetworkCapabilities[i];
      int[] arrayOfInt = localNetworkCapabilities.getTransportTypes();
      int m = arrayOfInt.length;
      int j = 0;
      while (j < m)
      {
        int n = arrayOfInt[j];
        this.mConnectedTransports.set(n);
        if (localNetworkCapabilities.hasCapability(16)) {
          this.mValidatedTransports.set(n);
        }
        j += 1;
      }
      i += 1;
    }
    Log.d("NetworkController", "updateConnectivity: mConnectedTransports=" + this.mConnectedTransports);
    Log.d("NetworkController", "updateConnectivity: mValidatedTransports=" + this.mValidatedTransports);
    if (this.mValidatedTransports.isEmpty()) {}
    for (;;)
    {
      this.mInetCondition = bool;
      pushConnectivityToSignals();
      return;
      bool = true;
    }
  }
  
  private void updateMobileControllers()
  {
    if (!this.mListening) {
      return;
    }
    doUpdateMobileControllers();
  }
  
  public void addEmergencyListener(NetworkController.EmergencyListener paramEmergencyListener)
  {
    this.mCallbackHandler.setListening(paramEmergencyListener, true);
    this.mCallbackHandler.setEmergencyCallsOnly(isEmergencyOnly());
  }
  
  public void addNetworkLabelView(TextView paramTextView)
  {
    if (paramTextView != null)
    {
      this.mNetWorkNameLabelView = paramTextView;
      this.mNetWorkNameLabelView.setText(getMobileDataNetworkName());
      setTextViewVisibility(this.mNetWorkNameLabelView);
    }
  }
  
  public void addSignalCallback(NetworkController.SignalCallback paramSignalCallback)
  {
    paramSignalCallback.setSubs(this.mCurrentSubscriptions);
    paramSignalCallback.setIsAirplaneMode(new NetworkController.IconState(this.mAirplaneMode, 2130838389, 2131690162, this.mContext));
    paramSignalCallback.setNoSims(this.mHasNoSims);
    this.mWifiSignalController.notifyListeners(paramSignalCallback);
    this.mEthernetSignalController.notifyListeners(paramSignalCallback);
    Iterator localIterator = this.mMobileSignalControllers.values().iterator();
    while (localIterator.hasNext()) {
      ((MobileSignalController)localIterator.next()).notifyListeners(paramSignalCallback);
    }
    this.mCallbackHandler.setListening(paramSignalCallback, true);
  }
  
  public void dispatchDemoCommand(String paramString, Bundle paramBundle)
  {
    if ((!this.mDemoMode) && (paramString.equals("enter")))
    {
      if (DEBUG) {
        Log.d("NetworkController", "Entering demo mode");
      }
      unregisterListeners();
      this.mDemoMode = true;
      this.mDemoInetCondition = this.mInetCondition;
      this.mDemoWifiState = ((WifiSignalController.WifiState)this.mWifiSignalController.getState());
    }
    do
    {
      return;
      if ((this.mDemoMode) && (paramString.equals("exit")))
      {
        if (DEBUG) {
          Log.d("NetworkController", "Exiting demo mode");
        }
        this.mDemoMode = false;
        updateMobileControllers();
        paramString = this.mMobileSignalControllers.values().iterator();
        while (paramString.hasNext()) {
          ((MobileSignalController)paramString.next()).resetLastState();
        }
        this.mWifiSignalController.resetLastState();
        this.mReceiverHandler.post(this.mRegisterListeners);
        notifyAllListeners();
        return;
      }
    } while ((!this.mDemoMode) || (!paramString.equals("network")));
    paramString = paramBundle.getString("airplane");
    if (paramString != null)
    {
      bool1 = paramString.equals("show");
      this.mCallbackHandler.setIsAirplaneMode(new NetworkController.IconState(bool1, 2130838389, 2131690162, this.mContext));
    }
    paramString = paramBundle.getString("fully");
    Object localObject1;
    Object localObject2;
    if (paramString != null)
    {
      this.mDemoInetCondition = Boolean.parseBoolean(paramString);
      paramString = new BitSet();
      if (this.mDemoInetCondition) {
        paramString.set(this.mWifiSignalController.mTransportType);
      }
      this.mWifiSignalController.updateConnectivity(paramString, paramString);
      localObject1 = this.mMobileSignalControllers.values().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (MobileSignalController)((Iterator)localObject1).next();
        if (this.mDemoInetCondition) {
          paramString.set(((MobileSignalController)localObject2).mTransportType);
        }
        ((MobileSignalController)localObject2).updateConnectivity(paramString, paramString);
      }
    }
    paramString = paramBundle.getString("wifi");
    boolean bool2;
    int i;
    if (paramString != null)
    {
      bool2 = paramString.equals("show");
      paramString = paramBundle.getString("level");
      if (paramString != null)
      {
        localObject1 = this.mDemoWifiState;
        if (!paramString.equals("null")) {
          break label546;
        }
        i = -1;
        ((WifiSignalController.WifiState)localObject1).level = i;
        paramString = this.mDemoWifiState;
        if (this.mDemoWifiState.level < 0) {
          break label562;
        }
      }
    }
    int j;
    label546:
    label562:
    for (boolean bool1 = true;; bool1 = false)
    {
      paramString.connected = bool1;
      this.mDemoWifiState.enabled = bool2;
      this.mWifiSignalController.notifyListeners();
      paramString = paramBundle.getString("sims");
      if (paramString == null) {
        break label576;
      }
      int k = MathUtils.constrain(Integer.parseInt(paramString), 1, 8);
      paramString = new ArrayList();
      if (k == this.mMobileSignalControllers.size()) {
        break label576;
      }
      this.mMobileSignalControllers.clear();
      j = this.mSubscriptionManager.getActiveSubscriptionInfoCountMax();
      i = j;
      while (i < j + k)
      {
        paramString.add(addSignalController(i, i));
        i += 1;
      }
      i = Math.min(Integer.parseInt(paramString), WifiIcons.WIFI_LEVEL_COUNT - 1);
      break;
    }
    this.mCallbackHandler.setSubs(paramString);
    label576:
    paramString = paramBundle.getString("nosim");
    if (paramString != null)
    {
      this.mHasNoSims = paramString.equals("show");
      this.mCallbackHandler.setNoSims(this.mHasNoSims);
    }
    paramString = paramBundle.getString("mobile");
    if (paramString != null)
    {
      bool2 = paramString.equals("show");
      paramString = paramBundle.getString("datatype");
      localObject1 = paramBundle.getString("slot");
      if (TextUtils.isEmpty((CharSequence)localObject1)) {}
      for (i = 0;; i = Integer.parseInt((String)localObject1))
      {
        i = MathUtils.constrain(i, 0, 8);
        localObject1 = new ArrayList();
        while (this.mMobileSignalControllers.size() <= i)
        {
          j = this.mMobileSignalControllers.size();
          ((List)localObject1).add(addSignalController(j, j));
        }
      }
      if (!((List)localObject1).isEmpty()) {
        this.mCallbackHandler.setSubs((List)localObject1);
      }
      localObject1 = ((MobileSignalController[])this.mMobileSignalControllers.values().toArray(new MobileSignalController[0]))[i];
      localObject2 = (MobileSignalController.MobileState)((MobileSignalController)localObject1).getState();
      if (paramString == null) {
        break label988;
      }
      bool1 = true;
      label788:
      ((MobileSignalController.MobileState)localObject2).dataSim = bool1;
      if (paramString != null)
      {
        localObject2 = (MobileSignalController.MobileState)((MobileSignalController)localObject1).getState();
        if (!paramString.equals("1x")) {
          break label994;
        }
        paramString = TelephonyIcons.ONE_X;
        label823:
        ((MobileSignalController.MobileState)localObject2).iconGroup = paramString;
      }
      paramString = TelephonyIcons.TELEPHONY_SIGNAL_STRENGTH;
      localObject2 = paramBundle.getString("level");
      if (localObject2 != null)
      {
        MobileSignalController.MobileState localMobileState = (MobileSignalController.MobileState)((MobileSignalController)localObject1).getState();
        if (!((String)localObject2).equals("null")) {
          break label1154;
        }
        i = -1;
        label870:
        localMobileState.level = i;
        paramString = (MobileSignalController.MobileState)((MobileSignalController)localObject1).getState();
        if (((MobileSignalController.MobileState)((MobileSignalController)localObject1).getState()).level < 0) {
          break label1172;
        }
      }
    }
    label988:
    label994:
    label1154:
    label1172:
    for (bool1 = true;; bool1 = false)
    {
      paramString.connected = bool1;
      ((MobileSignalController.MobileState)((MobileSignalController)localObject1).getState()).enabled = bool2;
      ((MobileSignalController)localObject1).notifyListeners();
      paramString = paramBundle.getString("carriernetworkchange");
      if (paramString == null) {
        break;
      }
      bool1 = paramString.equals("show");
      paramString = this.mMobileSignalControllers.values().iterator();
      while (paramString.hasNext()) {
        ((MobileSignalController)paramString.next()).setCarrierNetworkChangeMode(bool1);
      }
      break;
      bool1 = false;
      break label788;
      if (paramString.equals("3g"))
      {
        paramString = TelephonyIcons.THREE_G;
        break label823;
      }
      if (paramString.equals("4g"))
      {
        paramString = TelephonyIcons.FOUR_G;
        break label823;
      }
      if (paramString.equals("4g+"))
      {
        paramString = TelephonyIcons.FOUR_G_PLUS;
        break label823;
      }
      if (paramString.equals("e"))
      {
        paramString = TelephonyIcons.E;
        break label823;
      }
      if (paramString.equals("g"))
      {
        paramString = TelephonyIcons.G;
        break label823;
      }
      if (paramString.equals("h"))
      {
        paramString = TelephonyIcons.H;
        break label823;
      }
      if (paramString.equals("lte"))
      {
        paramString = TelephonyIcons.LTE;
        break label823;
      }
      if (paramString.equals("lte+"))
      {
        paramString = TelephonyIcons.LTE_PLUS;
        break label823;
      }
      if (paramString.equals("roam"))
      {
        paramString = TelephonyIcons.ROAMING;
        break label823;
      }
      paramString = TelephonyIcons.UNKNOWN;
      break label823;
      i = Math.min(Integer.parseInt((String)localObject2), paramString[0].length - 1);
      break label870;
    }
  }
  
  void doUpdateMobileControllers()
  {
    List localList2 = this.mSubscriptionManager.getActiveSubscriptionInfoList();
    List localList1 = localList2;
    if (localList2 == null) {
      localList1 = Collections.emptyList();
    }
    if (hasCorrectMobileControllers(localList1))
    {
      updateNoSims();
      return;
    }
    setCurrentSubscriptions(localList1);
    updateNoSims();
    recalculateEmergency();
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("NetworkController state:");
    paramPrintWriter.println("  - telephony ------");
    paramPrintWriter.print("  hasVoiceCallingFeature()=");
    paramPrintWriter.println(hasVoiceCallingFeature());
    paramPrintWriter.println("  - connectivity ------");
    paramPrintWriter.print("  mConnectedTransports=");
    paramPrintWriter.println(this.mConnectedTransports);
    paramPrintWriter.print("  mValidatedTransports=");
    paramPrintWriter.println(this.mValidatedTransports);
    paramPrintWriter.print("  mInetCondition=");
    paramPrintWriter.println(this.mInetCondition);
    paramPrintWriter.print("  mAirplaneMode=");
    paramPrintWriter.println(this.mAirplaneMode);
    paramPrintWriter.print("  mLocale=");
    paramPrintWriter.println(this.mLocale);
    paramPrintWriter.print("  mLastServiceState=");
    paramPrintWriter.println(this.mLastServiceState);
    paramPrintWriter.print("  mIsEmergency=");
    paramPrintWriter.println(this.mIsEmergency);
    paramPrintWriter.print("  mEmergencySource=");
    paramPrintWriter.println(emergencyToString(this.mEmergencySource));
    paramFileDescriptor = this.mMobileSignalControllers.values().iterator();
    while (paramFileDescriptor.hasNext()) {
      ((MobileSignalController)paramFileDescriptor.next()).dump(paramPrintWriter);
    }
    this.mWifiSignalController.dump(paramPrintWriter);
    this.mEthernetSignalController.dump(paramPrintWriter);
    this.mAccessPoints.dump(paramPrintWriter);
  }
  
  public NetworkController.AccessPointController getAccessPointController()
  {
    return this.mAccessPoints;
  }
  
  public String getCarrierName()
  {
    return getMobileDataNetworkName();
  }
  
  public boolean[] getCurrentLTEStatus()
  {
    return this.mLTEstatus;
  }
  
  public DataSaverController getDataSaverController()
  {
    return this.mDataSaverController;
  }
  
  public DataUsageController getMobileDataController()
  {
    return this.mDataUsageController;
  }
  
  public String getMobileDataNetworkName()
  {
    MobileSignalController localMobileSignalController = getDataController();
    if (localMobileSignalController != null) {
      return ((MobileSignalController.MobileState)localMobileSignalController.getState()).networkNameData;
    }
    return "";
  }
  
  void handleConfigurationChanged()
  {
    Iterator localIterator = this.mMobileSignalControllers.values().iterator();
    while (localIterator.hasNext()) {
      ((MobileSignalController)localIterator.next()).setConfiguration(this.mConfig);
    }
    refreshLocale();
  }
  
  void handleSetUserSetupComplete(boolean paramBoolean)
  {
    this.mUserSetup = paramBoolean;
    Iterator localIterator = this.mMobileSignalControllers.values().iterator();
    while (localIterator.hasNext()) {
      ((MobileSignalController)localIterator.next()).setUserSetupComplete(this.mUserSetup);
    }
  }
  
  boolean hasCorrectMobileControllers(List<SubscriptionInfo> paramList)
  {
    if (paramList.size() != this.mMobileSignalControllers.size()) {
      return false;
    }
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      SubscriptionInfo localSubscriptionInfo = (SubscriptionInfo)paramList.next();
      if (!this.mMobileSignalControllers.containsKey(Integer.valueOf(localSubscriptionInfo.getSubscriptionId()))) {
        return false;
      }
    }
    return true;
  }
  
  public boolean hasEmergencyCryptKeeperText()
  {
    return EncryptionHelper.IS_DATA_ENCRYPTED;
  }
  
  public boolean hasMobileDataFeature()
  {
    return this.mHasMobileDataFeature;
  }
  
  public boolean hasVoiceCallingFeature()
  {
    boolean bool = false;
    if (this.mPhone.getPhoneType() != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isEmbmsActive()
  {
    return this.mIsEmbmsActive;
  }
  
  public boolean isEmergencyOnly()
  {
    if (this.mMobileSignalControllers.size() == 0)
    {
      this.mEmergencySource = 0;
      if (this.mLastServiceState != null) {
        return this.mLastServiceState.isEmergencyOnly();
      }
      return false;
    }
    int i = this.mSubDefaults.getDefaultVoiceSubId();
    if (!SubscriptionManager.isValidSubscriptionId(i))
    {
      Iterator localIterator = this.mMobileSignalControllers.values().iterator();
      while (localIterator.hasNext())
      {
        MobileSignalController localMobileSignalController = (MobileSignalController)localIterator.next();
        if (!((MobileSignalController.MobileState)localMobileSignalController.getState()).isEmergency)
        {
          this.mEmergencySource = (localMobileSignalController.mSubscriptionInfo.getSubscriptionId() + 100);
          if (DEBUG) {
            Log.d("NetworkController", "Found emergency " + localMobileSignalController.mTag);
          }
          return false;
        }
      }
    }
    if (this.mMobileSignalControllers.containsKey(Integer.valueOf(i)))
    {
      this.mEmergencySource = (i + 200);
      if (DEBUG) {
        Log.d("NetworkController", "Getting emergency from " + i);
      }
      return ((MobileSignalController.MobileState)((MobileSignalController)this.mMobileSignalControllers.get(Integer.valueOf(i))).getState()).isEmergency;
    }
    if (DEBUG) {
      Log.e("NetworkController", "Cannot find controller for voice sub: " + i);
    }
    this.mEmergencySource = (i + 300);
    return true;
  }
  
  public boolean isRadioOn()
  {
    return !this.mAirplaneMode;
  }
  
  public void onConfigurationChanged()
  {
    this.mConfig = Config.readConfig(this.mContext);
    this.mReceiverHandler.post(new Runnable()
    {
      public void run()
      {
        NetworkControllerImpl.this.handleConfigurationChanged();
      }
    });
  }
  
  public void onLTEStatusUpdate()
  {
    boolean[] arrayOfBoolean1 = new boolean[6];
    boolean[] tmp6_5 = arrayOfBoolean1;
    tmp6_5[0] = 0;
    boolean[] tmp11_6 = tmp6_5;
    tmp11_6[1] = 0;
    boolean[] tmp16_11 = tmp11_6;
    tmp16_11[2] = 0;
    boolean[] tmp21_16 = tmp16_11;
    tmp21_16[3] = 0;
    boolean[] tmp26_21 = tmp21_16;
    tmp26_21[4] = 0;
    boolean[] tmp31_26 = tmp26_21;
    tmp31_26[5] = 0;
    tmp31_26;
    Iterator localIterator = this.mMobileSignalControllers.values().iterator();
    while (localIterator.hasNext())
    {
      boolean[] arrayOfBoolean2 = ((MobileSignalController)localIterator.next()).getLTEStatus();
      int i = 0;
      while (i < arrayOfBoolean2.length)
      {
        arrayOfBoolean1[i] |= arrayOfBoolean2[i];
        i += 1;
      }
    }
    if (isLTEStatusChange(arrayOfBoolean1))
    {
      this.mLTEstatus = arrayOfBoolean1;
      if (this.mPhoneStatusBar != null) {
        this.mPhoneStatusBar.onLTEStatusUpdate();
      }
    }
    else
    {
      return;
    }
    Log.v("NetworkController", "onLTEStatusUpdate mPhoneStatusBar is null:");
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (CHATTY) {
      Log.d("NetworkController", "onReceive: intent=" + paramIntent);
    }
    paramContext = paramIntent.getAction();
    if ((paramContext.equals("android.net.conn.CONNECTIVITY_CHANGE")) || (paramContext.equals("android.net.conn.INET_CONDITION_ACTION"))) {
      updateConnectivity();
    }
    for (;;)
    {
      return;
      if (paramContext.equals("android.intent.action.AIRPLANE_MODE"))
      {
        refreshLocale();
        updateAirplaneMode(false);
        return;
      }
      if (paramContext.equals("android.intent.action.ACTION_DEFAULT_VOICE_SUBSCRIPTION_CHANGED"))
      {
        recalculateEmergency();
        return;
      }
      if (paramContext.equals("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED"))
      {
        paramContext = this.mMobileSignalControllers.values().iterator();
        while (paramContext.hasNext()) {
          ((MobileSignalController)paramContext.next()).handleBroadcast(paramIntent);
        }
      }
      else
      {
        if (paramContext.equals("android.intent.action.SIM_STATE_CHANGED"))
        {
          updateMobileControllers();
          return;
        }
        if (paramContext.equals("android.intent.action.LOCALE_CHANGED"))
        {
          paramContext = this.mMobileSignalControllers.values().iterator();
          while (paramContext.hasNext()) {
            ((MobileSignalController)paramContext.next()).handleBroadcast(paramIntent);
          }
        }
        else if (paramContext.equals("android.intent.action.SERVICE_STATE"))
        {
          this.mLastServiceState = ServiceState.newFromBundle(paramIntent.getExtras());
          if (this.mMobileSignalControllers.size() == 0) {
            recalculateEmergency();
          }
        }
        else
        {
          if (!paramContext.equals("com.qualcomm.intent.EMBMS_STATUS")) {
            break;
          }
          this.mIsEmbmsActive = paramIntent.getBooleanExtra("ACTIVE", false);
          Log.d("NetworkController", "EMBMS_STATUS On Receive:isEmbmsactive=" + this.mIsEmbmsActive);
          paramContext = this.mMobileSignalControllers.values().iterator();
          while (paramContext.hasNext()) {
            ((MobileSignalController)paramContext.next()).notifyListeners();
          }
        }
      }
    }
    int i = paramIntent.getIntExtra("subscription", -1);
    if (SubscriptionManager.isValidSubscriptionId(i))
    {
      if (this.mMobileSignalControllers.containsKey(Integer.valueOf(i)))
      {
        ((MobileSignalController)this.mMobileSignalControllers.get(Integer.valueOf(i))).handleBroadcast(paramIntent);
        return;
      }
      updateMobileControllers();
      return;
    }
    this.mWifiSignalController.handleBroadcast(paramIntent);
  }
  
  public void onUserSwitched(int paramInt)
  {
    this.mCurrentUserId = paramInt;
    this.mAccessPoints.onUserSwitched(paramInt);
    updateConnectivity();
  }
  
  void recalculateEmergency()
  {
    this.mIsEmergency = isEmergencyOnly();
    this.mCallbackHandler.setEmergencyCallsOnly(this.mIsEmergency);
  }
  
  public void removeEmergencyListener(NetworkController.EmergencyListener paramEmergencyListener)
  {
    this.mCallbackHandler.setListening(paramEmergencyListener, false);
  }
  
  public void removeNetworkLabelView()
  {
    this.mNetWorkNameLabelView = null;
  }
  
  public void removeSignalCallback(NetworkController.SignalCallback paramSignalCallback)
  {
    this.mCallbackHandler.setListening(paramSignalCallback, false);
  }
  
  public void setBar(PhoneStatusBar paramPhoneStatusBar)
  {
    this.mPhoneStatusBar = paramPhoneStatusBar;
  }
  
  void setCurrentSubscriptions(List<SubscriptionInfo> paramList)
  {
    Collections.sort(paramList, new Comparator()
    {
      public int compare(SubscriptionInfo paramAnonymousSubscriptionInfo1, SubscriptionInfo paramAnonymousSubscriptionInfo2)
      {
        if (paramAnonymousSubscriptionInfo1.getSimSlotIndex() == paramAnonymousSubscriptionInfo2.getSimSlotIndex()) {
          return paramAnonymousSubscriptionInfo1.getSubscriptionId() - paramAnonymousSubscriptionInfo2.getSubscriptionId();
        }
        return paramAnonymousSubscriptionInfo1.getSimSlotIndex() - paramAnonymousSubscriptionInfo2.getSimSlotIndex();
      }
    });
    this.mCurrentSubscriptions = paramList;
    HashMap localHashMap = new HashMap(this.mMobileSignalControllers);
    this.mMobileSignalControllers.clear();
    int j = paramList.size();
    int i = 0;
    Object localObject;
    if (i < j)
    {
      int k = ((SubscriptionInfo)paramList.get(i)).getSubscriptionId();
      if (localHashMap.containsKey(Integer.valueOf(k))) {
        this.mMobileSignalControllers.put(Integer.valueOf(k), (MobileSignalController)localHashMap.remove(Integer.valueOf(k)));
      }
      for (;;)
      {
        i += 1;
        break;
        localObject = new MobileSignalController(this.mContext, this.mConfig, this.mHasMobileDataFeature, this.mPhone, this.mCallbackHandler, this, (SubscriptionInfo)paramList.get(i), this.mSubDefaults, this.mReceiverHandler.getLooper());
        ((MobileSignalController)localObject).setUserSetupComplete(this.mUserSetup);
        this.mMobileSignalControllers.put(Integer.valueOf(k), localObject);
        if (((SubscriptionInfo)paramList.get(i)).getSimSlotIndex() == 0) {
          this.mDefaultSignalController = ((MobileSignalController)localObject);
        }
        if (this.mListening) {
          ((MobileSignalController)localObject).registerListener();
        }
      }
    }
    if (this.mListening)
    {
      localObject = localHashMap.keySet().iterator();
      while (((Iterator)localObject).hasNext())
      {
        Integer localInteger = (Integer)((Iterator)localObject).next();
        if (localHashMap.get(localInteger) == this.mDefaultSignalController) {
          this.mDefaultSignalController = null;
        }
        ((MobileSignalController)localHashMap.get(localInteger)).unregisterListener();
      }
    }
    this.mCallbackHandler.setSubs(paramList);
    notifyAllListeners();
    pushConnectivityToSignals();
    updateAirplaneMode(true);
  }
  
  public void setNetworkSpeedController(NetworkSpeedController paramNetworkSpeedController)
  {
    this.mNetworkSpeedController = paramNetworkSpeedController;
    if (this.mNetworkSpeedController != null) {
      this.mNetworkSpeedController.updateConnectivity(this.mConnectedTransports, this.mValidatedTransports);
    }
  }
  
  public void setUserSetupComplete(final boolean paramBoolean)
  {
    this.mReceiverHandler.post(new Runnable()
    {
      public void run()
      {
        NetworkControllerImpl.this.handleSetUserSetupComplete(paramBoolean);
      }
    });
  }
  
  public void setWifiEnabled(final boolean paramBoolean)
  {
    new AsyncTask()
    {
      protected Void doInBackground(Void... paramAnonymousVarArgs)
      {
        int i = NetworkControllerImpl.-get2(NetworkControllerImpl.this).getWifiApState();
        if ((!paramBoolean) || (NetworkControllerImpl.-get2(NetworkControllerImpl.this).getWifiStaSapConcurrency())) {}
        for (;;)
        {
          NetworkControllerImpl.-get2(NetworkControllerImpl.this).setWifiEnabled(paramBoolean);
          return null;
          if ((i == 12) || (i == 13)) {
            NetworkControllerImpl.-get1(NetworkControllerImpl.this).stopTethering(0);
          }
        }
      }
    }.execute(new Void[0]);
  }
  
  public void updateNetworkLabelView()
  {
    if (this.mNetWorkNameLabelView != null)
    {
      this.mNetWorkNameLabelView.setText(getMobileDataNetworkName());
      setTextViewVisibility(this.mNetWorkNameLabelView);
    }
  }
  
  protected void updateNoSims()
  {
    if ((this.mHasMobileDataFeature) && (this.mMobileSignalControllers.size() == 0)) {}
    for (boolean bool = true;; bool = false)
    {
      if (bool != this.mHasNoSims)
      {
        this.mHasNoSims = bool;
        this.mCallbackHandler.setNoSims(this.mHasNoSims);
      }
      return;
    }
  }
  
  static class Config
  {
    boolean alwaysShowCdmaRssi = false;
    boolean hideLtePlus = false;
    boolean hspaDataDistinguishable;
    boolean readIconsFromXml;
    boolean show4gForLte = false;
    boolean showAtLeast3G = false;
    boolean showLocale;
    boolean showRat = false;
    boolean showRsrpSignalLevelforLTE;
    
    static Config readConfig(Context paramContext)
    {
      Config localConfig = new Config();
      paramContext = paramContext.getResources();
      localConfig.showAtLeast3G = paramContext.getBoolean(2131558413);
      localConfig.alwaysShowCdmaRssi = paramContext.getBoolean(17956969);
      localConfig.show4gForLte = paramContext.getBoolean(2131558420);
      localConfig.hspaDataDistinguishable = paramContext.getBoolean(2131558410);
      localConfig.readIconsFromXml = paramContext.getBoolean(2131558425);
      localConfig.showRsrpSignalLevelforLTE = paramContext.getBoolean(2131558421);
      localConfig.showLocale = paramContext.getBoolean(17957059);
      localConfig.hideLtePlus = paramContext.getBoolean(2131558426);
      return localConfig;
    }
  }
  
  private class SubListener
    extends SubscriptionManager.OnSubscriptionsChangedListener
  {
    private SubListener() {}
    
    public void onSubscriptionsChanged()
    {
      NetworkControllerImpl.-wrap1(NetworkControllerImpl.this);
    }
  }
  
  public static class SubscriptionDefaults
  {
    public int getDefaultDataSubId()
    {
      return SubscriptionManager.getDefaultDataSubscriptionId();
    }
    
    public int getDefaultVoiceSubId()
    {
      return SubscriptionManager.getDefaultVoiceSubscriptionId();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\NetworkControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */