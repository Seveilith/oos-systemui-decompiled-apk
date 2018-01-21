package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings.Global;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.util.NativeTextHelper;
import android.util.SparseArray;
import com.android.systemui.plugin.LSState;
import com.android.systemui.statusbar.phone.FingerprintUnlockController;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Objects;
import org.codeaurora.internal.IExtTelephony;
import org.codeaurora.internal.IExtTelephony.Stub;

public class MobileSignalController
  extends SignalController<MobileState, MobileIconGroup>
{
  private static String[] SHOW_LTE_OPERATORS = { "310004", "310005", "310012", "311480", "311481-9", "310026", "310160", "310170", "310200", "310210", "310220", "310230", "310240", "310250", "310260", "310270", "310280", "310290", "310310", "310330", "310490", "310580", "310660", "310800", "310090", "310150", "310380", "310410", "310560", "310680", "310980", "310990", "310120", "316010", "310020" };
  private final boolean[] LTE_DEFAULT_STATUS = { 0, 0, 0, 0, 0, 0 };
  private final int MSG_RECOVER_DATA = 100;
  private final int MSG_UPDATE_TELEPHONY_DELAY = 101;
  private final int STATUS_BAR_STYLE_ANDROID_DEFAULT = 0;
  private final int STATUS_BAR_STYLE_CDMA_1X_COMBINED = 1;
  private final int STATUS_BAR_STYLE_DATA_VOICE = 3;
  private final int STATUS_BAR_STYLE_DEFAULT_DATA = 2;
  private final int STATUS_BAR_STYLE_EXTENDED = 4;
  int mBackupDataNetType = 0;
  private CallbackHandler mCallbackHandler;
  private int[] mCarrierOneThresholdValues = null;
  private String[] mCarrieroneMccMncs = null;
  private NetworkControllerImpl.Config mConfig;
  private DataEnabledSettingObserver mDataEnabledSettingObserver;
  private int mDataNetType = 0;
  private int mDataState = 0;
  private MobileIconGroup mDefaultIcons;
  private final NetworkControllerImpl.SubscriptionDefaults mDefaults;
  private IExtTelephony mExtTelephony;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 100: 
        MobileSignalController.-wrap4(MobileSignalController.this);
        return;
      }
      MobileSignalController.-wrap6(MobileSignalController.this);
    }
  };
  private boolean mIsCarrierOneNetwork = false;
  private boolean mIsDataSignalControlEnabled;
  boolean mIsRemainCa = false;
  private boolean[] mLTEStatus = this.LTE_DEFAULT_STATUS;
  private String mLastDataSpn;
  private String mLastPlmn;
  private boolean mLastShowPlmn;
  private boolean mLastShowSpn;
  private String mLastSpn;
  private final String mNetworkNameDefault;
  private final String mNetworkNameSeparator;
  final SparseArray<MobileIconGroup> mNetworkToIconLookup;
  private int mNewCellIdentity = Integer.MAX_VALUE;
  BroadcastReceiver mOPMoblileReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      boolean bool = false;
      paramAnonymousContext = paramAnonymousIntent.getAction();
      int i;
      int j;
      if ("android.intent.action.SIM_STATE_CHANGED".equals(paramAnonymousContext))
      {
        if (MobileSignalController.this.mSubscriptionInfo == null) {
          return;
        }
        i = paramAnonymousIntent.getIntExtra("slot", 0);
        j = paramAnonymousIntent.getIntExtra("subscription", -1);
        paramAnonymousContext = paramAnonymousIntent.getStringExtra("ss");
        if (MobileSignalController.DEBUG) {
          Log.v(MobileSignalController.this.mTag, "onSIMstateChange state: " + paramAnonymousContext + " slotId: " + i + " subId " + j + " getSimSlotIndex: " + MobileSignalController.-wrap1(MobileSignalController.this));
        }
        if ((MobileSignalController.-wrap1(MobileSignalController.this) == i) || (MobileSignalController.this.mSubscriptionInfo.getSubscriptionId() == j))
        {
          MobileSignalController.-wrap5(MobileSignalController.this);
          ((MobileSignalController.MobileState)MobileSignalController.this.mCurrentState).simstate = paramAnonymousContext;
          MobileSignalController.-wrap6(MobileSignalController.this);
        }
      }
      do
      {
        do
        {
          return;
          if (!"org.codeaurora.intent.action.ACTION_UICC_MANUAL_PROVISION_STATUS_CHANGED".equals(paramAnonymousContext)) {
            break;
          }
          if (MobileSignalController.this.mSubscriptionInfo == null) {
            return;
          }
          i = paramAnonymousIntent.getIntExtra("phone", -1);
          j = paramAnonymousIntent.getIntExtra("newProvisionState", 0);
          Log.v(MobileSignalController.this.mTag, "onProvisionChange provisionedState: " + j + " slotId: " + i + " getSimSlotIndex: " + MobileSignalController.-wrap1(MobileSignalController.this));
        } while (MobileSignalController.-wrap1(MobileSignalController.this) != i);
        ((MobileSignalController.MobileState)MobileSignalController.this.mCurrentState).provision = j;
        MobileSignalController.-wrap6(MobileSignalController.this);
        return;
        if ("android.intent.action.PHONE_STATE".equals(paramAnonymousContext))
        {
          paramAnonymousContext = paramAnonymousIntent.getStringExtra("state");
          MobileSignalController.-wrap3(MobileSignalController.this, paramAnonymousContext);
          return;
        }
      } while (!"android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED".equals(paramAnonymousContext));
      paramAnonymousContext = (MobileSignalController.MobileState)MobileSignalController.this.mCurrentState;
      if (MobileSignalController.this.getDefaultDataSubId() == MobileSignalController.this.getSubId()) {
        bool = true;
      }
      paramAnonymousContext.isDefaultDataSubId = bool;
      MobileSignalController.-wrap6(MobileSignalController.this);
    }
  };
  private final TelephonyManager mPhone;
  private int mPhoneState;
  final PhoneStateListener mPhoneStateListener;
  private ServiceState mServiceState;
  private SignalStrength mSignalStrength;
  private int mStyle = 0;
  final SubscriptionInfo mSubscriptionInfo;
  
  public MobileSignalController(Context paramContext, NetworkControllerImpl.Config paramConfig, boolean paramBoolean, TelephonyManager paramTelephonyManager, CallbackHandler paramCallbackHandler, NetworkControllerImpl paramNetworkControllerImpl, SubscriptionInfo paramSubscriptionInfo, NetworkControllerImpl.SubscriptionDefaults paramSubscriptionDefaults, Looper paramLooper)
  {
    super("MobileSignalController(" + paramSubscriptionInfo.getSubscriptionId() + ")", paramContext, 0, paramCallbackHandler, paramNetworkControllerImpl);
    this.mCallbackHandler = paramCallbackHandler;
    this.mNetworkToIconLookup = new SparseArray();
    this.mConfig = paramConfig;
    this.mPhone = paramTelephonyManager;
    this.mDefaults = paramSubscriptionDefaults;
    this.mSubscriptionInfo = paramSubscriptionInfo;
    this.mPhoneStateListener = new MobilePhoneStateListener(paramSubscriptionInfo.getSubscriptionId(), paramLooper);
    this.mNetworkNameSeparator = getStringIfExists(2131690051);
    this.mNetworkNameDefault = getStringIfExists(17040033);
    this.mIsDataSignalControlEnabled = this.mContext.getResources().getBoolean(2131558448);
    boolean bool;
    if (this.mIsDataSignalControlEnabled)
    {
      this.mDataEnabledSettingObserver = new DataEnabledSettingObserver(new Handler(), paramContext);
      paramTelephonyManager = (MobileState)this.mLastState;
      if (isMobileDataEnabled(this.mSubscriptionInfo.getSubscriptionId()))
      {
        bool = false;
        ((MobileState)this.mCurrentState).isForbidden = bool;
        paramTelephonyManager.isForbidden = bool;
      }
    }
    else
    {
      if (!paramConfig.readIconsFromXml) {
        break label615;
      }
      TelephonyIcons.readIconsFromXml(paramContext);
      if (this.mConfig.showAtLeast3G) {
        break label608;
      }
      paramConfig = TelephonyIcons.G;
      label395:
      this.mDefaultIcons = paramConfig;
      label400:
      this.mStyle = paramContext.getResources().getInteger(2131623986);
      if (isCarrierOneSupported()) {
        this.mStyle = 4;
      }
      if (paramSubscriptionInfo.getCarrierName() == null) {
        break label622;
      }
    }
    label608:
    label615:
    label622:
    for (paramContext = paramSubscriptionInfo.getCarrierName().toString();; paramContext = this.mNetworkNameDefault)
    {
      paramConfig = (MobileState)this.mLastState;
      ((MobileState)this.mCurrentState).networkName = paramContext;
      paramConfig.networkName = paramContext;
      paramConfig = (MobileState)this.mLastState;
      ((MobileState)this.mCurrentState).networkNameData = paramContext;
      paramConfig.networkNameData = paramContext;
      paramContext = (MobileState)this.mLastState;
      ((MobileState)this.mCurrentState).enabled = paramBoolean;
      paramContext.enabled = paramBoolean;
      paramContext = (MobileState)this.mLastState;
      paramConfig = this.mDefaultIcons;
      ((MobileState)this.mCurrentState).iconGroup = paramConfig;
      paramContext.iconGroup = paramConfig;
      updateDataSim();
      this.mCarrieroneMccMncs = this.mContext.getResources().getStringArray(2131427601);
      this.mCarrierOneThresholdValues = this.mContext.getResources().getIntArray(2131427602);
      ((MobileState)this.mCurrentState).provision = getSlotProvisionStatus(getSubId());
      return;
      bool = true;
      break;
      paramConfig = TelephonyIcons.THREE_G;
      break label395;
      mapIconSets();
      break label400;
    }
  }
  
  private void cleanLTEStatus()
  {
    this.mLTEStatus = this.LTE_DEFAULT_STATUS;
    Log.i(this.mTag, "cleanLTEStatus");
  }
  
  private void generateIconGroup()
  {
    int i2 = ((MobileState)this.mCurrentState).level;
    int i4 = ((MobileState)this.mCurrentState).voiceLevel;
    int i7 = ((MobileState)this.mCurrentState).inetCondition;
    boolean bool1 = ((MobileState)this.mCurrentState).dataConnected;
    boolean bool2 = isRoaming();
    int i = getVoiceNetworkType();
    int i5 = getDataNetworkType();
    int[][] arrayOfInt1 = TelephonyIcons.TELEPHONY_SIGNAL_STRENGTH;
    int[][] arrayOfInt2 = TelephonyIcons.QS_TELEPHONY_SIGNAL_STRENGTH;
    int[] arrayOfInt = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
    int i6 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0];
    int j = 0;
    int i1 = 0;
    int i3 = getSimSlotIndex();
    if ((i3 < 0) || (i3 > this.mPhone.getPhoneCount()))
    {
      Log.e(this.mTag, "generateIconGroup invalid slotId:" + i3);
      return;
    }
    if (DEBUG) {
      Log.d(this.mTag, "generateIconGroup slot:" + i3 + " style:" + this.mStyle + " connected:" + ((MobileState)this.mCurrentState).connected + " inetCondition:" + i7 + " roaming:" + bool2 + " level:" + i2 + " voiceLevel:" + i4 + " dataConnected:" + bool1 + " dataActivity:" + ((MobileState)this.mCurrentState).dataActivity + " CS:" + i + "/" + TelephonyManager.getNetworkTypeName(i) + ", PS:" + i5 + "/" + TelephonyManager.getNetworkTypeName(i5));
    }
    int m;
    label461:
    int n;
    int k;
    if (i5 == 0)
    {
      TelephonyIcons.updateDataType(i3, i, this.mConfig.showAtLeast3G, this.mConfig.show4gForLte, this.mConfig.hspaDataDistinguishable, i7);
      m = TelephonyIcons.getSignalStrengthIcon(i3, i7, i2, bool2);
      if (DEBUG) {
        Log.d(this.mTag, "singleSignalIcon:" + getResourceName(m));
      }
      if (!this.mIsDataSignalControlEnabled) {
        break label907;
      }
      if ((!((MobileState)this.mCurrentState).dataConnected) || (i3 < 0)) {
        break label897;
      }
      i = TelephonyIcons.getDataActivity(i3, ((MobileState)this.mCurrentState).dataActivity);
      n = TelephonyIcons.convertMobileStrengthIcon(m);
      if (DEBUG) {
        Log.d(this.mTag, "unstackedSignalIcon:" + getResourceName(n));
      }
      k = m;
      if (m != n)
      {
        j = m;
        k = n;
      }
      m = k;
      n = i1;
      if (this.mStyle == 1)
      {
        if ((bool2) || (!showDataAndVoice())) {
          break label949;
        }
        n = TelephonyIcons.getStackedVoiceIcon(i4, isRoaming());
        m = k;
      }
      label564:
      i1 = j;
      if (n == 0) {
        i1 = 0;
      }
      arrayOfInt = TelephonyIcons.getSignalStrengthDes(i3);
      i7 = TelephonyIcons.getSignalNullIcon(i3);
      if (DEBUG) {
        Log.d(this.mTag, "singleSignalIcon=" + getResourceName(m) + " dataActivityId=" + getResourceName(i) + " stackedDataIcon=" + getResourceName(i1) + " stackedVoiceIcon=" + getResourceName(n));
      }
      if ((i5 != 18) || (!this.mContext.getResources().getBoolean(2131558444))) {
        break label988;
      }
      j = 2130838419;
      k = 2130837821;
      i2 = 2131690148;
      label708:
      i3 = j;
      i4 = k;
      if (bool2)
      {
        if (!this.mContext.getResources().getBoolean(2131558440)) {
          break label1010;
        }
        i4 = k;
        i3 = j;
      }
      label741:
      if (DEBUG) {
        Log.d(this.mTag, "updateDataNetType, dataTypeIcon=" + getResourceName(i3) + " qsDataTypeIcon=" + getResourceName(i4) + " dataContentDesc=" + i2);
      }
      if (!bool2) {
        break label1071;
      }
      if (this.mContext.getResources().getBoolean(2131558440)) {
        break label1059;
      }
      if (this.mStyle != 4) {
        break label1065;
      }
      bool1 = true;
    }
    for (;;)
    {
      ((MobileState)this.mCurrentState).iconGroup = new MobileIconGroup(TelephonyManager.getNetworkTypeName(i5), arrayOfInt1, arrayOfInt2, arrayOfInt, 0, 0, i7, 2130837842, i6, i2, i3, bool1, i4, m, i1, n, i);
      return;
      i = i5;
      break;
      label897:
      i = getCustomStatusBarIcon(i3);
      break label461;
      label907:
      if ((((MobileState)this.mCurrentState).dataConnected) && (i3 >= 0))
      {
        i = TelephonyIcons.getDataActivity(i3, ((MobileState)this.mCurrentState).dataActivity);
        break label461;
      }
      i = 0;
      break label461;
      label949:
      m = k;
      n = i1;
      if (!bool2) {
        break label564;
      }
      m = k;
      n = i1;
      if (i == 0) {
        break label564;
      }
      m = TelephonyIcons.getRoamingSignalIconId(i2, i7);
      n = i1;
      break label564;
      label988:
      j = TelephonyIcons.getDataTypeIcon(i3);
      i2 = TelephonyIcons.getDataTypeDesc(i3);
      k = TelephonyIcons.getQSDataTypeIcon(i3);
      break label708;
      label1010:
      i3 = j;
      i4 = k;
      if (this.mStyle == 4) {
        break label741;
      }
      i3 = j;
      i4 = k;
      if (this.mContext.getResources().getBoolean(2131558449)) {
        break label741;
      }
      i3 = 2130838431;
      i4 = 2130837844;
      break label741;
      label1059:
      bool1 = true;
      continue;
      label1065:
      bool1 = false;
      continue;
      label1071:
      bool1 = false;
    }
  }
  
  private int getAlternateLteLevel(SignalStrength paramSignalStrength)
  {
    int j = paramSignalStrength.getLteDbm();
    int i = 0;
    if (j > -44) {
      i = 0;
    }
    for (;;)
    {
      if (DEBUG) {
        Log.d(this.mTag, "getAlternateLteLevel lteRsrp:" + j + " rsrpLevel = " + i);
      }
      return i;
      if (j >= -97) {
        i = 4;
      } else if (j >= -105) {
        i = 3;
      } else if (j >= -113) {
        i = 2;
      } else if (j >= -120) {
        i = 1;
      } else if (j >= 65396) {
        i = 0;
      }
    }
  }
  
  private int getCustomStatusBarIcon(int paramInt)
  {
    if ((!((MobileState)this.mCurrentState).dataConnected) && (((MobileState)this.mCurrentState).isForbidden)) {
      return TelephonyIcons.getForbiddenDataIcon(paramInt);
    }
    return TelephonyIcons.getDataDisconnectedIcon(paramInt);
  }
  
  private int getDataNetworkType()
  {
    if (this.mServiceState == null) {
      return 0;
    }
    return this.mServiceState.getDataNetworkType();
  }
  
  private int getEmbmsIconId()
  {
    if ((this.mStyle == 4) && (isEmbmsActiveOnDataSim())) {
      return 2130838028;
    }
    return 0;
  }
  
  private int getImsIconId()
  {
    if ((this.mStyle != 4) || (this.mServiceState == null)) {}
    while (this.mServiceState.getVoiceRegState() != 0) {
      return 0;
    }
    if (((MobileState)this.mCurrentState).imsRadioTechnology == 14) {
      return 2130839033;
    }
    return 0;
  }
  
  private int getImsRadioTechnology()
  {
    if ((this.mServiceState == null) || (this.mServiceState.getVoiceRegState() != 0)) {
      return 0;
    }
    return this.mServiceState.getRilImsRadioTechnology();
  }
  
  private String getLocalString(String paramString)
  {
    str1 = "";
    try
    {
      String str2 = NativeTextHelper.getLocalString(this.mContext, paramString, 17236059, 17236060);
      paramString = str2;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        localException.printStackTrace();
        Log.e(this.mTag, "getLocalString error String:" + paramString);
        paramString = str1;
      }
    }
    Log.i(this.mTag, " getLocalString result:" + paramString);
    return paramString;
  }
  
  private String getNetworkClassString(ServiceState paramServiceState)
  {
    if ((paramServiceState != null) && ((paramServiceState.getDataRegState() == 0) || (paramServiceState.getVoiceRegState() == 0)))
    {
      int i = paramServiceState.getVoiceNetworkType();
      int j = paramServiceState.getDataNetworkType();
      if (j == 0) {}
      for (;;)
      {
        return networkClassToString(TelephonyManager.getNetworkClass(i));
        i = j;
      }
    }
    return "";
  }
  
  private int getSimSlotIndex()
  {
    int i = -1;
    if (this.mSubscriptionInfo != null) {
      i = this.mSubscriptionInfo.getSimSlotIndex();
    }
    if (DEBUG) {
      Log.d(this.mTag, "getSimSlotIndex, slotId: " + i);
    }
    return i;
  }
  
  private int getSlotProvisionStatus(int paramInt)
  {
    if (this.mExtTelephony == null) {
      this.mExtTelephony = IExtTelephony.Stub.asInterface(ServiceManager.getService("extphone"));
    }
    int i = SubscriptionManager.getPhoneId(paramInt);
    try
    {
      paramInt = this.mExtTelephony.getCurrentUiccCardProvisioningStatus(i);
      Log.d(this.mTag, "getSlotProvisionStatus slotId: " + i + ", status = " + paramInt);
      return paramInt;
    }
    catch (NullPointerException localNullPointerException)
    {
      for (;;)
      {
        paramInt = -1;
        this.mExtTelephony = null;
        Log.e(this.mTag, "Failed to get pref, slotId: " + i + " Exception: " + localNullPointerException);
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        paramInt = -1;
        this.mExtTelephony = null;
        Log.e(this.mTag, "Failed to get pref, slotId: " + i + " Exception: " + localRemoteException);
      }
    }
  }
  
  private int getVoiceNetworkType()
  {
    if (this.mServiceState == null) {
      return 0;
    }
    return this.mServiceState.getVoiceNetworkType();
  }
  
  private int getVoiceSignalLevel()
  {
    if (this.mSignalStrength == null) {
      return 0;
    }
    if (showStacked(this.mDataNetType)) {
      return this.mSignalStrength.getCheatingSignalLevelAll()[0];
    }
    return this.mSignalStrength.getLevel();
  }
  
  private boolean hasService()
  {
    boolean bool2 = false;
    if (this.mServiceState != null)
    {
      switch (this.mServiceState.getVoiceRegState())
      {
      default: 
        return true;
      case 3: 
        return false;
      }
      if (this.mContext.getResources().getBoolean(2131558445)) {
        return this.mServiceState.getDataRegState() == 0;
      }
      boolean bool1 = bool2;
      if (this.mServiceState.getDataRegState() == 0)
      {
        bool1 = bool2;
        if (this.mServiceState.getDataNetworkType() != 18) {
          bool1 = true;
        }
      }
      return bool1;
    }
    return false;
  }
  
  private boolean isCarrierNetworkChangeActive()
  {
    return ((MobileState)this.mCurrentState).carrierNetworkChangeMode;
  }
  
  public static boolean isCarrierOneSupported()
  {
    return "405854".equals(SystemProperties.get("persist.radio.atel.carrier"));
  }
  
  private boolean isCdma()
  {
    return (this.mSignalStrength != null) && (!this.mSignalStrength.isGsm());
  }
  
  private boolean isDataDisabled()
  {
    return !this.mPhone.getDataEnabled(this.mSubscriptionInfo.getSubscriptionId());
  }
  
  private boolean isEmbmsActiveOnDataSim()
  {
    if (this.mNetworkController.isEmbmsActive()) {
      return ((MobileState)this.mCurrentState).dataSim;
    }
    return false;
  }
  
  private boolean isImsRegisteredInWifi()
  {
    if (this.mStyle != 4) {
      return false;
    }
    Object localObject = SubscriptionManager.from(this.mContext).getActiveSubscriptionInfoList();
    if (localObject != null)
    {
      localObject = ((Iterable)localObject).iterator();
      int i;
      do
      {
        if (!((Iterator)localObject).hasNext()) {
          break;
        }
        i = ((SubscriptionInfo)((Iterator)localObject).next()).getSubscriptionId();
      } while ((this.mPhone == null) || ((!this.mPhone.isVoWifiCallingAvailableForSubscriber(i)) && (!this.mPhone.isVideoTelephonyWifiCallingAvailableForSubscriber(i))));
      return true;
    }
    Log.e(this.mTag, "Invalid SubscriptionInfo");
    return false;
  }
  
  private boolean isMobileDataEnabled(int paramInt)
  {
    return TelephonyManager.getDefault().getDataEnabled(paramInt);
  }
  
  private boolean isRoaming()
  {
    boolean bool3 = false;
    boolean bool2 = false;
    if (this.mServiceState == null) {
      return false;
    }
    String str;
    StringBuilder localStringBuilder;
    if (DEBUG)
    {
      str = this.mTag;
      localStringBuilder = new StringBuilder().append(" isRoaming iconMode:").append(this.mServiceState.getCdmaEriIconMode()).append(" EriIconIndex:").append(this.mServiceState.getCdmaEriIconIndex()).append(" isRoaming:");
      if (this.mServiceState == null) {
        break label155;
      }
    }
    label155:
    for (boolean bool1 = this.mServiceState.getRoaming();; bool1 = false)
    {
      Log.d(str, bool1);
      if (!isCdma()) {
        break label162;
      }
      int i = this.mServiceState.getCdmaEriIconMode();
      bool1 = bool2;
      if (hasService())
      {
        bool1 = bool2;
        if (this.mServiceState.getCdmaEriIconIndex() != 1)
        {
          if (i == 0) {
            break;
          }
          bool1 = bool2;
          if (i == 1) {
            bool1 = true;
          }
        }
      }
      return bool1;
    }
    return true;
    label162:
    bool1 = bool3;
    if (this.mServiceState != null) {
      bool1 = this.mServiceState.getRoaming();
    }
    return bool1;
  }
  
  private void makeDataNetTypeStable()
  {
    this.mBackupDataNetType = this.mDataNetType;
    if (this.mDataNetType == 19)
    {
      Log.d("CADebug", "Starting to make CA stable");
      this.mIsRemainCa = true;
      this.mHandler.removeMessages(100);
      this.mHandler.sendEmptyMessageDelayed(100, 10000L);
    }
    do
    {
      return;
      if ((this.mDataNetType != 19) && (this.mDataNetType != 13)) {}
      while (this.mPhoneState != 0)
      {
        this.mHandler.removeMessages(100);
        this.mIsRemainCa = false;
        return;
      }
    } while (!this.mIsRemainCa);
    Log.d("CADebug", "mDataNetType changed, force it to display CA");
    this.mDataNetType = 19;
  }
  
  private void mapIconSets()
  {
    this.mNetworkToIconLookup.clear();
    this.mNetworkToIconLookup.put(5, TelephonyIcons.THREE_G);
    this.mNetworkToIconLookup.put(6, TelephonyIcons.THREE_G);
    this.mNetworkToIconLookup.put(12, TelephonyIcons.THREE_G);
    this.mNetworkToIconLookup.put(14, TelephonyIcons.THREE_G);
    this.mNetworkToIconLookup.put(3, TelephonyIcons.THREE_G);
    this.mNetworkToIconLookup.put(17, TelephonyIcons.THREE_G);
    if (!this.mConfig.showAtLeast3G)
    {
      this.mNetworkToIconLookup.put(0, TelephonyIcons.UNKNOWN);
      this.mNetworkToIconLookup.put(2, TelephonyIcons.E);
      this.mNetworkToIconLookup.put(4, TelephonyIcons.ONE_X);
      this.mNetworkToIconLookup.put(7, TelephonyIcons.ONE_X);
      this.mDefaultIcons = TelephonyIcons.G;
      if (this.mContext.getResources().getBoolean(2131558439)) {
        this.mNetworkToIconLookup.put(2, TelephonyIcons.E);
      }
      MobileIconGroup localMobileIconGroup1 = TelephonyIcons.THREE_G;
      if (this.mConfig.hspaDataDistinguishable) {
        localMobileIconGroup1 = TelephonyIcons.H;
      }
      MobileIconGroup localMobileIconGroup2 = TelephonyIcons.THREE_G;
      if (this.mConfig.hspaDataDistinguishable) {
        localMobileIconGroup2 = TelephonyIcons.H_plus;
      }
      this.mNetworkToIconLookup.put(8, localMobileIconGroup1);
      this.mNetworkToIconLookup.put(9, localMobileIconGroup1);
      this.mNetworkToIconLookup.put(10, localMobileIconGroup2);
      this.mNetworkToIconLookup.put(15, localMobileIconGroup2);
      if (showLTE()) {
        break label341;
      }
      this.mNetworkToIconLookup.put(13, TelephonyIcons.FOUR_G);
      this.mNetworkToIconLookup.put(19, TelephonyIcons.FOUR_G_PLUS);
    }
    for (;;)
    {
      this.mNetworkToIconLookup.put(18, TelephonyIcons.WFC);
      return;
      this.mNetworkToIconLookup.put(0, TelephonyIcons.THREE_G);
      this.mNetworkToIconLookup.put(2, TelephonyIcons.THREE_G);
      this.mNetworkToIconLookup.put(4, TelephonyIcons.THREE_G);
      this.mNetworkToIconLookup.put(7, TelephonyIcons.THREE_G);
      this.mDefaultIcons = TelephonyIcons.THREE_G;
      break;
      label341:
      this.mNetworkToIconLookup.put(13, TelephonyIcons.LTE);
      this.mNetworkToIconLookup.put(19, TelephonyIcons.LTE_PLUS);
    }
  }
  
  private String networkClassToString(int paramInt)
  {
    Object localObject = new int[4];
    localObject[0] = 17039477;
    localObject[1] = 17039478;
    localObject[2] = 17039479;
    localObject[3] = 17039480;
    String str = null;
    if (paramInt < localObject.length) {
      str = this.mContext.getResources().getString(localObject[paramInt]);
    }
    localObject = str;
    if (str == null) {
      localObject = "";
    }
    return (String)localObject;
  }
  
  private void onPhoneStateChange(String paramString)
  {
    if (TelephonyManager.EXTRA_STATE_IDLE.equals(paramString)) {
      this.mPhoneState = 0;
    }
    for (;;)
    {
      if (DEBUG) {
        Log.d(this.mTag, "handlePhoneStateChanged(" + paramString + ")");
      }
      if ((this.mPhoneState != 0) && (this.mIsRemainCa))
      {
        this.mHandler.removeMessages(100);
        recoverDataNetTypeStable();
      }
      return;
      if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(paramString)) {
        this.mPhoneState = 2;
      } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(paramString)) {
        this.mPhoneState = 1;
      }
    }
  }
  
  private void recoverDataNetTypeStable()
  {
    this.mIsRemainCa = false;
    if (this.mBackupDataNetType != 19)
    {
      Log.d("CADebug", "Restore mDataNetType to mBackupDataNetType:" + TelephonyManager.getNetworkTypeName(this.mBackupDataNetType));
      this.mDataNetType = this.mBackupDataNetType;
      updateTelephony();
    }
  }
  
  private boolean showDataAndVoice()
  {
    if (this.mStyle != 1) {
      return false;
    }
    int i = getDataNetworkType();
    int j = getVoiceNetworkType();
    if ((i == 5) || (i == 5)) {}
    while ((i == 6) || (i == 12) || (i == 14) || (i == 13) || (i == 19))
    {
      if ((j != 16) && (j != 7)) {
        break;
      }
      return true;
    }
    while (j != 4) {
      return false;
    }
    return true;
  }
  
  private boolean showLongOperatorName()
  {
    return (this.mContext.getResources().getBoolean(2131558446)) || ((this.mContext.getResources().getBoolean(2131558447)) && (isRoaming()));
  }
  
  private boolean showMobileActivity()
  {
    if ((this.mStyle == 2) || (this.mStyle == 0)) {}
    while (this.mStyle == 4) {
      return true;
    }
    return false;
  }
  
  private boolean showStacked(int paramInt)
  {
    return (paramInt != 0) && (isCdma()) && (!isDataDisabled());
  }
  
  private void updateDataSim()
  {
    boolean bool = true;
    int i = this.mDefaults.getDefaultDataSubId();
    if (SubscriptionManager.isValidSubscriptionId(i))
    {
      MobileState localMobileState = (MobileState)this.mCurrentState;
      if (i == this.mSubscriptionInfo.getSubscriptionId()) {}
      for (;;)
      {
        localMobileState.dataSim = bool;
        return;
        bool = false;
      }
    }
    ((MobileState)this.mCurrentState).dataSim = true;
  }
  
  private void updateSignalIcons()
  {
    if (showLTE())
    {
      this.mNetworkToIconLookup.put(13, TelephonyIcons.LTE);
      this.mNetworkToIconLookup.put(19, TelephonyIcons.LTE_PLUS);
      return;
    }
    this.mNetworkToIconLookup.put(13, TelephonyIcons.FOUR_G);
    this.mNetworkToIconLookup.put(19, TelephonyIcons.FOUR_G_PLUS);
  }
  
  private final void updateTelephony()
  {
    boolean bool2 = false;
    this.mHandler.removeMessages(101);
    Object localObject = LSState.getInstance().getFingerprintUnlockControl();
    if ((localObject != null) && (((FingerprintUnlockController)localObject).getMode() == 1))
    {
      Log.d(this.mTag, "updateTelephony: during fp authenticating, update later");
      this.mHandler.sendEmptyMessageDelayed(101, 500L);
    }
    if (DEBUG) {
      Log.d(this.mTag, "updateTelephony: hasService=" + hasService() + " ss=" + this.mSignalStrength);
    }
    localObject = (MobileState)this.mCurrentState;
    boolean bool1;
    if ((hasService()) && (this.mSignalStrength != null))
    {
      bool1 = true;
      ((MobileState)localObject).connected = bool1;
      ((MobileState)this.mCurrentState).isroaming = isRoaming();
      if (((MobileState)this.mCurrentState).connected)
      {
        if ((this.mSignalStrength.isGsm()) || (!this.mConfig.alwaysShowCdmaRssi)) {
          break label657;
        }
        ((MobileState)this.mCurrentState).level = this.mSignalStrength.getCdmaLevel();
      }
      if ((this.mNetworkToIconLookup.indexOfKey(this.mDataNetType) < 0) || (this.mNetworkToIconLookup.get(this.mDataNetType) == null)) {
        break label782;
      }
      if (!showStacked(this.mDataNetType)) {
        break label755;
      }
      ((MobileState)this.mCurrentState).iconGroup = new MobileIconGroup((MobileIconGroup)this.mNetworkToIconLookup.get(this.mDataNetType), this.mDataNetType, getVoiceSignalLevel(), ((MobileState)this.mCurrentState).level, isRoaming(), showLTE());
      if (DEBUG) {
        Log.d(this.mTag, " showStacked dataType:" + this.mDataNetType + " getCurrentPhoneType:" + TelephonyManager.getDefault().getCurrentPhoneType(this.mSubscriptionInfo.getSubscriptionId()) + " SubscriptionId:" + this.mSubscriptionInfo.getSubscriptionId());
      }
      label373:
      localObject = (MobileState)this.mCurrentState;
      bool1 = bool2;
      if (((MobileState)this.mCurrentState).connected)
      {
        bool1 = bool2;
        if (this.mDataState == 2) {
          bool1 = true;
        }
      }
      ((MobileState)localObject).dataConnected = bool1;
      if (!isDataDisabled()) {
        break label799;
      }
      bool1 = ((MobileState)this.mCurrentState).isDefaultDataSubId;
      label433:
      Log.i(this.mTag, " showDisableIcon:" + bool1);
      if (!isCarrierNetworkChangeActive()) {
        break label804;
      }
      ((MobileState)this.mCurrentState).iconGroup = TelephonyIcons.CARRIER_NETWORK_CHANGE;
      label481:
      if (isEmergencyOnly() != ((MobileState)this.mCurrentState).isEmergency)
      {
        ((MobileState)this.mCurrentState).isEmergency = isEmergencyOnly();
        this.mNetworkController.recalculateEmergency();
      }
      if ((((MobileState)this.mCurrentState).networkName == this.mNetworkNameDefault) && (this.mServiceState != null) && (!TextUtils.isEmpty(this.mServiceState.getOperatorAlphaShort()))) {
        break label824;
      }
      label556:
      if ((!showLongOperatorName()) && (this.mServiceState != null) && (!TextUtils.isEmpty(this.mServiceState.getOperatorAlphaShort()))) {
        break label844;
      }
    }
    for (;;)
    {
      if (this.mConfig.readIconsFromXml) {
        ((MobileState)this.mCurrentState).voiceLevel = getVoiceSignalLevel();
      }
      if (this.mStyle == 4) {
        ((MobileState)this.mCurrentState).imsRadioTechnology = getImsRadioTechnology();
      }
      ((MobileState)this.mCurrentState).provision = getSlotProvisionStatus(getSubId());
      notifyListenersIfNecessary();
      return;
      bool1 = false;
      break;
      label657:
      localObject = (MobileState)this.mCurrentState;
      if (showStacked(this.mDataNetType)) {}
      for (int i = this.mSignalStrength.getCheatingSignalLevelAll()[1];; i = this.mSignalStrength.getCheatingSignalLevel())
      {
        ((MobileState)localObject).level = i;
        if (!this.mConfig.showRsrpSignalLevelforLTE) {
          break;
        }
        i = this.mServiceState.getDataNetworkType();
        if ((i != 13) && (i != 19)) {
          break;
        }
        ((MobileState)this.mCurrentState).level = getAlternateLteLevel(this.mSignalStrength);
        break;
      }
      label755:
      ((MobileState)this.mCurrentState).iconGroup = ((SignalController.IconGroup)this.mNetworkToIconLookup.get(this.mDataNetType));
      break label373;
      label782:
      ((MobileState)this.mCurrentState).iconGroup = this.mDefaultIcons;
      break label373;
      label799:
      bool1 = false;
      break label433;
      label804:
      if (!bool1) {
        break label481;
      }
      ((MobileState)this.mCurrentState).iconGroup = TelephonyIcons.DATA_DISABLED;
      break label481;
      label824:
      ((MobileState)this.mCurrentState).networkName = this.mServiceState.getOperatorAlphaShort();
      break label556;
      label844:
      ((MobileState)this.mCurrentState).networkNameData = (this.mServiceState.getOperatorAlphaShort() + " " + getNetworkClassString(this.mServiceState));
    }
  }
  
  protected MobileState cleanState()
  {
    return new MobileState();
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    super.dump(paramPrintWriter);
    paramPrintWriter.println("  mSubscription=" + this.mSubscriptionInfo + ",");
    paramPrintWriter.println("  mServiceState=" + this.mServiceState + ",");
    paramPrintWriter.println("  mSignalStrength=" + this.mSignalStrength + ",");
    paramPrintWriter.println("  mDataState=" + this.mDataState + ",");
    paramPrintWriter.println("  mDataNetType=" + this.mDataNetType + ",");
  }
  
  public int getCurrentIconId()
  {
    if ((this.mConfig.readIconsFromXml) && (((MobileState)this.mCurrentState).connected)) {
      return ((MobileIconGroup)getIcons()).mSingleSignalIcon;
    }
    if ((!isRoaming()) || (showStacked(this.mDataNetType))) {
      return super.getCurrentIconId();
    }
    return TelephonyIcons.getOneplusRoamingSignalIconId(((MobileState)this.mCurrentState).level);
  }
  
  public int getDefaultDataSubId()
  {
    int j = this.mDefaults.getDefaultDataSubId();
    int i = j;
    if (!SubscriptionManager.isValidSubscriptionId(j)) {
      i = Integer.MAX_VALUE;
    }
    return i;
  }
  
  public boolean[] getLTEStatus()
  {
    return this.mLTEStatus;
  }
  
  protected String getResourceName(int paramInt)
  {
    if (paramInt != 0)
    {
      Object localObject = this.mContext.getResources();
      try
      {
        localObject = ((Resources)localObject).getResourceName(paramInt);
        return (String)localObject;
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        return "(unknown)";
      }
    }
    return "(null)";
  }
  
  public int getSubId()
  {
    int i = -1;
    if (this.mSubscriptionInfo != null) {
      i = this.mSubscriptionInfo.getSubscriptionId();
    }
    if (DEBUG) {
      Log.d(this.mTag, "getSubId, subId: " + i);
    }
    return i;
  }
  
  public void handleBroadcast(Intent paramIntent)
  {
    String str = paramIntent.getAction();
    if (str.equals("android.provider.Telephony.SPN_STRINGS_UPDATED"))
    {
      updateNetworkName(paramIntent.getBooleanExtra("showSpn", false), paramIntent.getStringExtra("spn"), paramIntent.getStringExtra("spnData"), paramIntent.getBooleanExtra("showPlmn", false), paramIntent.getStringExtra("plmn"));
      notifyListenersIfNecessary();
    }
    do
    {
      return;
      if (str.equals("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED"))
      {
        updateDataSim();
        notifyListenersIfNecessary();
        return;
      }
    } while ((!str.equals("android.intent.action.LOCALE_CHANGED")) || (!this.mConfig.showLocale));
    this.mCallbackHandler.post(new Runnable()
    {
      public void run()
      {
        MobileSignalController.this.updateNetworkName(MobileSignalController.-get7(MobileSignalController.this), MobileSignalController.-get8(MobileSignalController.this), MobileSignalController.-get4(MobileSignalController.this), MobileSignalController.-get6(MobileSignalController.this), MobileSignalController.-get5(MobileSignalController.this));
        MobileSignalController.this.notifyListenersIfNecessary();
      }
    });
  }
  
  public boolean isEmergencyOnly()
  {
    if (this.mServiceState != null) {
      return this.mServiceState.isEmergencyOnly();
    }
    return false;
  }
  
  public void notifyListeners(NetworkController.SignalCallback paramSignalCallback)
  {
    if (this.mConfig.readIconsFromXml) {
      generateIconGroup();
    }
    MobileIconGroup localMobileIconGroup = (MobileIconGroup)getIcons();
    String str3 = getStringIfExists(getContentDescription());
    String str2 = getStringIfExists(localMobileIconGroup.mDataContentDescription);
    boolean bool1;
    boolean bool2;
    label100:
    boolean bool3;
    label129:
    NetworkController.IconState localIconState2;
    int i;
    label195:
    label228:
    int j;
    NetworkController.IconState localIconState1;
    String str1;
    label259:
    label288:
    label321:
    label350:
    label379:
    label389:
    int k;
    label399:
    int m;
    label413:
    boolean bool4;
    label433:
    int n;
    int i1;
    int i2;
    if (((MobileState)this.mCurrentState).iconGroup == TelephonyIcons.DATA_DISABLED)
    {
      bool1 = ((MobileState)this.mCurrentState).userSetup;
      if (!this.mContext.getResources().getBoolean(2131558440)) {
        break label604;
      }
      bool2 = ((MobileState)this.mCurrentState).dataConnected;
      if ((((MobileState)this.mCurrentState).enabled) && (!((MobileState)this.mCurrentState).airplaneMode)) {
        break label653;
      }
      bool3 = false;
      localIconState2 = new NetworkController.IconState(bool3, getCurrentIconId(), str3);
      if ((((MobileState)this.mCurrentState).isDefault) || (((MobileState)this.mCurrentState).iconGroup == TelephonyIcons.ROAMING)) {
        bool1 = true;
      }
      if (this.mStyle == 0) {
        break label659;
      }
      if (this.mStyle != 4) {
        break label664;
      }
      i = 1;
      i = bool2 & bool1 & i;
      if (!((MobileState)this.mCurrentState).dataConnected) {
        break label669;
      }
      bool2 = ((MobileState)this.mCurrentState).isDefault;
      j = 0;
      localIconState1 = null;
      str1 = null;
      if (((MobileState)this.mCurrentState).dataSim)
      {
        if (i == 0) {
          break label675;
        }
        j = localMobileIconGroup.mQsDataType;
        if (!((MobileState)this.mCurrentState).enabled) {
          break label686;
        }
        if (!((MobileState)this.mCurrentState).isEmergency) {
          break label680;
        }
        bool1 = false;
        localIconState1 = new NetworkController.IconState(bool1, getQsCurrentIconId(), str3);
        if (!((MobileState)this.mCurrentState).isEmergency) {
          break label692;
        }
        str1 = null;
      }
      if ((((MobileState)this.mCurrentState).dataConnected) && (!((MobileState)this.mCurrentState).carrierNetworkChangeMode)) {
        break label707;
      }
      bool1 = false;
      if ((((MobileState)this.mCurrentState).dataConnected) && (!((MobileState)this.mCurrentState).carrierNetworkChangeMode)) {
        break label722;
      }
      bool3 = false;
      if (i == 0) {
        break label737;
      }
      i = localMobileIconGroup.mDataType;
      if (!showMobileActivity()) {
        break label742;
      }
      k = 0;
      if (!showMobileActivity()) {
        break label752;
      }
      m = localMobileIconGroup.mActivityId;
      int i3 = 0;
      if (((MobileState)this.mCurrentState).provision != 1) {
        break label758;
      }
      bool4 = true;
      n = i;
      i1 = j;
      i2 = i3;
      if (this.mStyle == 4)
      {
        n = i;
        i1 = j;
        i2 = i3;
        if (isRoaming())
        {
          if (!((MobileState)this.mCurrentState).dataConnected) {
            break label764;
          }
          label481:
          n = 2130838431;
          if (!((MobileState)this.mCurrentState).dataConnected) {
            break label769;
          }
          i2 = i;
          i1 = j;
        }
      }
      label505:
      if (!(paramSignalCallback instanceof NetworkController.SignalCallbackExtended)) {
        break label778;
      }
      ((NetworkController.SignalCallbackExtended)paramSignalCallback).setMobileDataIndicators(localIconState2, localIconState1, n, i1, bool1, bool3, k, m, localMobileIconGroup.mStackedDataIcon, localMobileIconGroup.mStackedVoiceIcon, str2, str1, localMobileIconGroup.mIsWide, this.mSubscriptionInfo.getSubscriptionId(), i2, getEmbmsIconId(), getImsIconId(), isImsRegisteredInWifi(), bool2, bool4);
    }
    for (;;)
    {
      this.mCallbackHandler.post(new Runnable()
      {
        public void run()
        {
          MobileSignalController.this.mNetworkController.updateNetworkLabelView();
        }
      });
      return;
      bool1 = false;
      break;
      label604:
      if ((((MobileState)this.mCurrentState).dataConnected) || (((MobileState)this.mCurrentState).iconGroup == TelephonyIcons.ROAMING)) {}
      while (isRoaming())
      {
        bool2 = true;
        break;
      }
      bool2 = bool1;
      break label100;
      label653:
      bool3 = true;
      break label129;
      label659:
      i = 1;
      break label195;
      label664:
      i = 0;
      break label195;
      label669:
      bool2 = false;
      break label228;
      label675:
      j = 0;
      break label259;
      label680:
      bool1 = true;
      break label288;
      label686:
      bool1 = false;
      break label288;
      label692:
      str1 = ((MobileState)this.mCurrentState).networkName;
      break label321;
      label707:
      bool1 = ((MobileState)this.mCurrentState).activityIn;
      break label350;
      label722:
      bool3 = ((MobileState)this.mCurrentState).activityOut;
      break label379;
      label737:
      i = 0;
      break label389;
      label742:
      k = localMobileIconGroup.mActivityId;
      break label399;
      label752:
      m = 0;
      break label413;
      label758:
      bool4 = false;
      break label433;
      label764:
      i = 0;
      break label481;
      label769:
      i1 = 0;
      i2 = i;
      break label505;
      label778:
      paramSignalCallback.setMobileDataIndicators(localIconState2, localIconState1, n, i1, bool1, bool3, k, m, localMobileIconGroup.mStackedDataIcon, localMobileIconGroup.mStackedVoiceIcon, str2, str1, localMobileIconGroup.mIsWide, this.mSubscriptionInfo.getSubscriptionId(), bool2, bool4);
    }
  }
  
  public void registerListener()
  {
    this.mPhone.listen(this.mPhoneStateListener, 197089);
    if (this.mContext != null)
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
      localIntentFilter.addAction("org.codeaurora.intent.action.ACTION_UICC_MANUAL_PROVISION_STATUS_CHANGED");
      localIntentFilter.addAction("android.intent.action.PHONE_STATE");
      localIntentFilter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
      this.mContext.registerReceiver(this.mOPMoblileReceiver, localIntentFilter);
    }
    if (this.mIsDataSignalControlEnabled) {
      this.mDataEnabledSettingObserver.register();
    }
  }
  
  void setActivity(int paramInt)
  {
    boolean bool2 = true;
    MobileState localMobileState = (MobileState)this.mCurrentState;
    if (paramInt != 3)
    {
      if (paramInt != 1) {
        break label89;
      }
      bool1 = true;
      localMobileState.activityIn = bool1;
      localMobileState = (MobileState)this.mCurrentState;
      bool1 = bool2;
      if (paramInt != 3) {
        if (paramInt != 2) {
          break label94;
        }
      }
    }
    label89:
    label94:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      localMobileState.activityOut = bool1;
      if (this.mConfig.readIconsFromXml) {
        ((MobileState)this.mCurrentState).dataActivity = paramInt;
      }
      notifyListenersIfNecessary();
      return;
      bool1 = true;
      break;
      bool1 = false;
      break;
    }
  }
  
  public void setAirplaneMode(boolean paramBoolean)
  {
    ((MobileState)this.mCurrentState).airplaneMode = paramBoolean;
    notifyListenersIfNecessary();
  }
  
  public void setCarrierNetworkChangeMode(boolean paramBoolean)
  {
    ((MobileState)this.mCurrentState).carrierNetworkChangeMode = paramBoolean;
    updateTelephony();
  }
  
  public void setConfiguration(NetworkControllerImpl.Config paramConfig)
  {
    this.mConfig = paramConfig;
    if (!paramConfig.readIconsFromXml) {
      mapIconSets();
    }
    updateTelephony();
  }
  
  public void setForbiddenState(boolean paramBoolean)
  {
    ((MobileState)this.mCurrentState).isForbidden = paramBoolean;
    Log.i(this.mTag, "setForbiddenState:" + ((MobileState)this.mCurrentState).isForbidden);
    updateTelephony();
  }
  
  public void setUserSetupComplete(boolean paramBoolean)
  {
    ((MobileState)this.mCurrentState).userSetup = paramBoolean;
    notifyListenersIfNecessary();
  }
  
  public boolean showLTE()
  {
    if (this.mPhone == null) {
      return false;
    }
    String str = this.mPhone.getSimOperatorNumericForPhone(getSimSlotIndex());
    Log.i(this.mTag, "showLTE:" + str);
    int i = 0;
    while (i < SHOW_LTE_OPERATORS.length)
    {
      if (str.equals(SHOW_LTE_OPERATORS[i])) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public void unregisterListener()
  {
    this.mPhone.listen(this.mPhoneStateListener, 0);
    if (this.mContext != null) {
      this.mContext.unregisterReceiver(this.mOPMoblileReceiver);
    }
    cleanLTEStatus();
    if (this.mNetworkController != null) {
      this.mNetworkController.onLTEStatusUpdate();
    }
    for (;;)
    {
      if (this.mIsDataSignalControlEnabled) {
        this.mDataEnabledSettingObserver.unregister();
      }
      return;
      Log.w(this.mTag, "unregisterListener mNetworkController is null");
    }
  }
  
  public void updateConnectivity(BitSet paramBitSet1, BitSet paramBitSet2)
  {
    int i = 0;
    boolean bool2 = paramBitSet2.get(this.mTransportType);
    ((MobileState)this.mCurrentState).isDefault = paramBitSet1.get(this.mTransportType);
    paramBitSet1 = (MobileState)this.mCurrentState;
    boolean bool1;
    if (getDefaultDataSubId() == getSubId())
    {
      bool1 = true;
      paramBitSet1.isDefaultDataSubId = bool1;
      if (!((MobileState)this.mCurrentState).isDefaultDataSubId) {
        break label118;
      }
      paramBitSet1 = (MobileState)this.mCurrentState;
      if ((bool2) || (!((MobileState)this.mCurrentState).isDefault)) {
        break label113;
      }
    }
    label97:
    label113:
    label118:
    for (paramBitSet1.inetCondition = i;; ((MobileState)this.mCurrentState).inetCondition = 1)
    {
      notifyListenersIfNecessary();
      return;
      bool1 = false;
      break;
      i = 1;
      break label97;
    }
  }
  
  void updateNetworkName(boolean paramBoolean1, String paramString1, String paramString2, boolean paramBoolean2, String paramString3)
  {
    this.mLastShowSpn = paramBoolean1;
    this.mLastSpn = paramString1;
    this.mLastDataSpn = paramString2;
    this.mLastShowPlmn = paramBoolean2;
    this.mLastPlmn = paramString3;
    if (CHATTY) {
      Log.d("CarrierLabel", "updateNetworkName showSpn=" + paramBoolean1 + " spn=" + paramString1 + " dataSpn=" + paramString2 + " showPlmn=" + paramBoolean2 + " plmn=" + paramString3);
    }
    Object localObject = paramString1;
    String str2 = paramString2;
    String str3 = paramString3;
    String str1;
    if (this.mConfig.showLocale)
    {
      str1 = paramString1;
      if (paramBoolean1)
      {
        if (!TextUtils.isEmpty(paramString1)) {
          break label491;
        }
        str1 = paramString1;
      }
      paramString1 = paramString2;
      if (paramBoolean1)
      {
        if (!TextUtils.isEmpty(paramString2)) {
          break label501;
        }
        paramString1 = paramString2;
      }
      label154:
      localObject = str1;
      str2 = paramString1;
      str3 = paramString3;
      if (paramBoolean2)
      {
        if (!TextUtils.isEmpty(paramString3)) {
          break label510;
        }
        str3 = paramString3;
        str2 = paramString1;
        localObject = str1;
      }
    }
    label189:
    boolean bool = paramBoolean1;
    if (paramBoolean2)
    {
      bool = paramBoolean1;
      if (paramBoolean1)
      {
        if (!TextUtils.isEmpty((CharSequence)localObject)) {
          break label528;
        }
        bool = paramBoolean1;
      }
    }
    label215:
    paramBoolean1 = this.mConfig.showRat;
    SubscriptionManager.getSubId(getSimSlotIndex());
    paramString1 = getNetworkClassString(this.mServiceState);
    Log.d(this.mTag, "networkClass=" + paramString1 + " showRat=" + paramBoolean1 + " slot=" + getSimSlotIndex());
    paramString2 = new StringBuilder();
    paramString3 = new StringBuilder();
    if ((paramBoolean2) && (str3 != null))
    {
      paramString2.append(str3);
      paramString3.append(str3);
      if (paramBoolean1)
      {
        paramString2.append(" ").append(paramString1);
        paramString3.append(" ").append(paramString1);
      }
    }
    if ((bool) && (localObject != null))
    {
      if (paramString2.length() != 0)
      {
        paramString2.append("(");
        paramString2.append((String)localObject);
        paramString2.append(")");
      }
    }
    else
    {
      label402:
      if (paramString2.length() == 0) {
        break label568;
      }
      ((MobileState)this.mCurrentState).networkName = paramString2.toString();
      label423:
      if ((bool) && (str2 != null))
      {
        if (paramString3.length() == 0) {
          break label585;
        }
        paramString3.append("(");
        paramString3.append(str2);
        paramString3.append(")");
      }
    }
    for (;;)
    {
      if (paramString3.length() == 0) {
        break label596;
      }
      ((MobileState)this.mCurrentState).networkNameData = paramString3.toString();
      return;
      label491:
      str1 = getLocalString(paramString1);
      break;
      label501:
      paramString1 = getLocalString(paramString2);
      break label154;
      label510:
      str3 = getLocalString(paramString3);
      localObject = str1;
      str2 = paramString1;
      break label189;
      label528:
      bool = paramBoolean1;
      if (TextUtils.isEmpty(str3)) {
        break label215;
      }
      bool = paramBoolean1;
      if (!str3.equals(localObject)) {
        break label215;
      }
      bool = false;
      break label215;
      paramString2.append((String)localObject);
      break label402;
      label568:
      ((MobileState)this.mCurrentState).networkName = this.mNetworkNameDefault;
      break label423;
      label585:
      paramString3.append(str2);
    }
    label596:
    ((MobileState)this.mCurrentState).networkNameData = this.mNetworkNameDefault;
  }
  
  private class DataEnabledSettingObserver
    extends ContentObserver
  {
    ContentResolver mResolver;
    
    public DataEnabledSettingObserver(Handler paramHandler, Context paramContext)
    {
      super();
      this.mResolver = paramContext.getContentResolver();
    }
    
    public void onChange(boolean paramBoolean)
    {
      MobileSignalController localMobileSignalController = MobileSignalController.this;
      if (MobileSignalController.-wrap0(MobileSignalController.this, MobileSignalController.this.mSubscriptionInfo.getSubscriptionId())) {}
      for (paramBoolean = false;; paramBoolean = true)
      {
        localMobileSignalController.setForbiddenState(paramBoolean);
        return;
      }
    }
    
    public void register()
    {
      String str = "mobile_data" + MobileSignalController.this.mSubscriptionInfo.getSubscriptionId();
      this.mResolver.registerContentObserver(Settings.Global.getUriFor(str), false, this);
      this.mResolver.registerContentObserver(Settings.Global.getUriFor("mobile_data"), false, this);
    }
    
    public void unregister()
    {
      this.mResolver.unregisterContentObserver(this);
    }
  }
  
  static class MobileIconGroup
    extends SignalController.IconGroup
  {
    final int mActivityId;
    final int mDataContentDescription;
    final int mDataType;
    final boolean mIsWide;
    final int mQsDataType;
    final int mSingleSignalIcon;
    final int mStackedDataIcon;
    final int mStackedVoiceIcon;
    
    public MobileIconGroup(MobileIconGroup paramMobileIconGroup, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2)
    {
      this(paramMobileIconGroup.mName, paramMobileIconGroup.mSbIcons, paramMobileIconGroup.mQsIcons, paramMobileIconGroup.mContentDesc, paramMobileIconGroup.mSbNullState, paramMobileIconGroup.mQsNullState, paramMobileIconGroup.mSbDiscState, paramMobileIconGroup.mQsDiscState, paramMobileIconGroup.mDiscContentDesc, paramMobileIconGroup.mDataContentDescription, paramMobileIconGroup.mDataType, paramMobileIconGroup.mIsWide, paramMobileIconGroup.mQsDataType, 0, TelephonyIcons.getStackedDataIcon(paramInt1, paramInt3, paramBoolean2), TelephonyIcons.getStackedVoiceIcon(paramInt2, paramBoolean1), 0);
    }
    
    public MobileIconGroup(String paramString, int[][] paramArrayOfInt1, int[][] paramArrayOfInt2, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean, int paramInt8)
    {
      this(paramString, paramArrayOfInt1, paramArrayOfInt2, paramArrayOfInt, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramBoolean, paramInt8, 0, 0, 0, 0);
    }
    
    public MobileIconGroup(String paramString, int[][] paramArrayOfInt1, int[][] paramArrayOfInt2, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12)
    {
      super(paramArrayOfInt1, paramArrayOfInt2, paramArrayOfInt, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      this.mDataContentDescription = paramInt6;
      this.mDataType = paramInt7;
      this.mIsWide = paramBoolean;
      this.mQsDataType = paramInt8;
      this.mSingleSignalIcon = paramInt9;
      this.mStackedDataIcon = paramInt10;
      this.mStackedVoiceIcon = paramInt11;
      this.mActivityId = paramInt12;
    }
  }
  
  class MobilePhoneStateListener
    extends PhoneStateListener
  {
    public MobilePhoneStateListener(int paramInt, Looper paramLooper)
    {
      super(paramLooper);
    }
    
    private boolean isCarrierOneOperatorRegistered(ServiceState paramServiceState)
    {
      paramServiceState = paramServiceState.getOperatorNumeric();
      if ((MobileSignalController.-get1(MobileSignalController.this) == null) || (MobileSignalController.-get1(MobileSignalController.this).length == 0)) {}
      while (TextUtils.isEmpty(paramServiceState)) {
        return false;
      }
      String[] arrayOfString = MobileSignalController.-get1(MobileSignalController.this);
      int j = arrayOfString.length;
      int i = 0;
      while (i < j)
      {
        if (paramServiceState.equals(arrayOfString[i])) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    public void onCarrierNetworkChange(boolean paramBoolean)
    {
      if (MobileSignalController.DEBUG) {
        Log.d(MobileSignalController.this.mTag, "onCarrierNetworkChange: active=" + paramBoolean);
      }
      ((MobileSignalController.MobileState)MobileSignalController.this.mCurrentState).carrierNetworkChangeMode = paramBoolean;
      MobileSignalController.-wrap6(MobileSignalController.this);
    }
    
    public void onDataActivity(int paramInt)
    {
      if (MobileSignalController.DEBUG) {
        Log.d(MobileSignalController.this.mTag, "onDataActivity: direction=" + paramInt);
      }
      MobileSignalController.this.setActivity(paramInt);
    }
    
    public void onDataConnectionStateChanged(int paramInt1, int paramInt2)
    {
      Object localObject;
      boolean bool;
      if (MobileSignalController.DEBUG)
      {
        localObject = MobileSignalController.this.mTag;
        StringBuilder localStringBuilder = new StringBuilder().append("onDataConnectionStateChanged: state=").append(paramInt1).append(" type=").append(paramInt2).append(" isUsingCarrierAggregation:");
        if (MobileSignalController.-get11(MobileSignalController.this) != null)
        {
          bool = MobileSignalController.-get11(MobileSignalController.this).isUsingCarrierAggregation();
          Log.d((String)localObject, bool);
        }
      }
      else
      {
        if (!MobileSignalController.this.mContext.getResources().getBoolean(2131558439)) {
          break label303;
        }
        localObject = MobileSignalController.-get10(MobileSignalController.this).getCellLocation();
        if ((localObject instanceof GsmCellLocation))
        {
          localObject = (GsmCellLocation)localObject;
          MobileSignalController.-set4(MobileSignalController.this, ((GsmCellLocation)localObject).getCid());
          Log.d(MobileSignalController.this.mTag, "onDataConnectionStateChanged, mNewCellIdentity = " + MobileSignalController.-get9(MobileSignalController.this));
        }
        Log.d(MobileSignalController.this.mTag, "onDataConnectionStateChanged , mNewCellIdentity = " + MobileSignalController.-get9(MobileSignalController.this) + ", mDataNetType = " + MobileSignalController.-get2(MobileSignalController.this) + ", networkType = " + paramInt2);
        if (Integer.MAX_VALUE == MobileSignalController.-get9(MobileSignalController.this)) {
          break label280;
        }
        MobileSignalController.-set0(MobileSignalController.this, paramInt2);
      }
      for (;;)
      {
        MobileSignalController.-set1(MobileSignalController.this, paramInt1);
        MobileSignalController.-wrap6(MobileSignalController.this);
        return;
        bool = false;
        break;
        label280:
        if (paramInt2 > MobileSignalController.-get2(MobileSignalController.this)) {
          MobileSignalController.-set0(MobileSignalController.this, paramInt2);
        }
      }
      label303:
      MobileSignalController.-set1(MobileSignalController.this, paramInt1);
      MobileSignalController.-set0(MobileSignalController.this, paramInt2);
      if ((MobileSignalController.-get2(MobileSignalController.this) == 13) && (MobileSignalController.-get11(MobileSignalController.this) != null) && (MobileSignalController.-get11(MobileSignalController.this).isUsingCarrierAggregation())) {
        MobileSignalController.-set0(MobileSignalController.this, 19);
      }
      MobileSignalController.-wrap2(MobileSignalController.this);
      MobileSignalController.-wrap6(MobileSignalController.this);
    }
    
    public void onImsCapabilityStatusChange(boolean[] paramArrayOfBoolean)
    {
      if (MobileSignalController.DEBUG)
      {
        int i = 0;
        while (i < paramArrayOfBoolean.length)
        {
          Log.v(MobileSignalController.this.mTag, "onImsCapabilityStatusChange: status:" + paramArrayOfBoolean[i]);
          i += 1;
        }
      }
      MobileSignalController.-set3(MobileSignalController.this, paramArrayOfBoolean);
      if (MobileSignalController.this.mNetworkController != null)
      {
        MobileSignalController.this.mNetworkController.onLTEStatusUpdate();
        return;
      }
      Log.i(MobileSignalController.this.mTag, "onImsCapabilityStatusChange mNetworkController is null");
    }
    
    public void onServiceStateChanged(ServiceState paramServiceState)
    {
      String str = MobileSignalController.this.mTag;
      StringBuilder localStringBuilder = new StringBuilder().append("onServiceStateChanged voiceState=").append(paramServiceState.getVoiceRegState()).append(" dataState=").append(paramServiceState.getDataRegState()).append(" isUsingCarrierAggregation:");
      if (MobileSignalController.-get11(MobileSignalController.this) != null) {}
      for (boolean bool = MobileSignalController.-get11(MobileSignalController.this).isUsingCarrierAggregation();; bool = false)
      {
        Log.d(str, bool);
        MobileSignalController.-set5(MobileSignalController.this, paramServiceState);
        MobileSignalController.-set0(MobileSignalController.this, paramServiceState.getDataNetworkType());
        MobileSignalController.-set2(MobileSignalController.this, isCarrierOneOperatorRegistered(MobileSignalController.-get11(MobileSignalController.this)));
        Log.d(MobileSignalController.this.mTag, "onServiceStateChanged mIsCarrierOneNetwork =" + MobileSignalController.-get3(MobileSignalController.this));
        MobileSignalController.this.updateNetworkName(MobileSignalController.-get7(MobileSignalController.this), MobileSignalController.-get8(MobileSignalController.this), MobileSignalController.-get4(MobileSignalController.this), MobileSignalController.-get6(MobileSignalController.this), MobileSignalController.-get5(MobileSignalController.this));
        if ((MobileSignalController.-get2(MobileSignalController.this) == 13) && (MobileSignalController.-get11(MobileSignalController.this) != null) && (MobileSignalController.-get11(MobileSignalController.this).isUsingCarrierAggregation())) {
          MobileSignalController.-set0(MobileSignalController.this, 19);
        }
        MobileSignalController.-wrap2(MobileSignalController.this);
        MobileSignalController.-wrap6(MobileSignalController.this);
        return;
      }
    }
    
    public void onSignalStrengthsChanged(SignalStrength paramSignalStrength)
    {
      String str2 = MobileSignalController.this.mTag;
      StringBuilder localStringBuilder = new StringBuilder().append("onSignalStrengthsChanged signalStrength=").append(paramSignalStrength);
      if (paramSignalStrength == null)
      {
        str1 = "";
        localStringBuilder = localStringBuilder.append(str1);
        if (paramSignalStrength != null) {
          break label155;
        }
      }
      label155:
      for (String str1 = "";; str1 = " voicelevel=" + paramSignalStrength.getCheatingSignalLevelAll()[0] + " datalevel=" + paramSignalStrength.getCheatingSignalLevelAll()[1])
      {
        Log.d(str2, str1);
        MobileSignalController.-set6(MobileSignalController.this, paramSignalStrength);
        if ((MobileSignalController.-get3(MobileSignalController.this)) && (MobileSignalController.-get12(MobileSignalController.this) != null) && (MobileSignalController.-get0(MobileSignalController.this) != null)) {
          MobileSignalController.-get12(MobileSignalController.this).setThreshRsrp(MobileSignalController.-get0(MobileSignalController.this));
        }
        MobileSignalController.-wrap6(MobileSignalController.this);
        return;
        str1 = " level=" + paramSignalStrength.getCheatingSignalLevel();
        break;
      }
    }
  }
  
  static class MobileState
    extends SignalController.State
  {
    boolean airplaneMode;
    boolean carrierNetworkChangeMode;
    int dataActivity;
    boolean dataConnected;
    boolean dataSim;
    int imsRadioTechnology;
    boolean isDefault;
    boolean isDefaultDataSubId;
    boolean isEmergency;
    boolean isForbidden;
    boolean isroaming;
    String networkName;
    String networkNameData;
    int provision = -1;
    String simstate;
    boolean userSetup;
    int voiceLevel;
    
    public void copyFrom(SignalController.State paramState)
    {
      super.copyFrom(paramState);
      paramState = (MobileState)paramState;
      this.dataSim = paramState.dataSim;
      this.networkName = paramState.networkName;
      this.networkNameData = paramState.networkNameData;
      this.dataConnected = paramState.dataConnected;
      this.isDefault = paramState.isDefault;
      this.isEmergency = paramState.isEmergency;
      this.isForbidden = paramState.isForbidden;
      this.airplaneMode = paramState.airplaneMode;
      this.carrierNetworkChangeMode = paramState.carrierNetworkChangeMode;
      this.userSetup = paramState.userSetup;
      this.dataActivity = paramState.dataActivity;
      this.voiceLevel = paramState.voiceLevel;
      this.imsRadioTechnology = paramState.imsRadioTechnology;
      this.simstate = paramState.simstate;
      this.provision = paramState.provision;
      this.isroaming = paramState.isroaming;
      this.isDefaultDataSubId = paramState.isDefaultDataSubId;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((super.equals(paramObject)) && (Objects.equals(((MobileState)paramObject).networkName, this.networkName)) && (Objects.equals(((MobileState)paramObject).networkNameData, this.networkNameData)) && (((MobileState)paramObject).dataSim == this.dataSim) && (((MobileState)paramObject).dataConnected == this.dataConnected) && (((MobileState)paramObject).isEmergency == this.isEmergency) && (((MobileState)paramObject).isForbidden == this.isForbidden) && (((MobileState)paramObject).airplaneMode == this.airplaneMode) && (((MobileState)paramObject).carrierNetworkChangeMode == this.carrierNetworkChangeMode) && (((MobileState)paramObject).userSetup == this.userSetup) && (((MobileState)paramObject).voiceLevel == this.voiceLevel) && (((MobileState)paramObject).isDefault == this.isDefault) && (((MobileState)paramObject).imsRadioTechnology == this.imsRadioTechnology) && (((MobileState)paramObject).simstate == this.simstate) && (((MobileState)paramObject).provision == this.provision) && (((MobileState)paramObject).isroaming == this.isroaming)) {
        return ((MobileState)paramObject).isDefaultDataSubId == this.isDefaultDataSubId;
      }
      return false;
    }
    
    protected void toString(StringBuilder paramStringBuilder)
    {
      super.toString(paramStringBuilder);
      paramStringBuilder.append(',');
      paramStringBuilder.append("dataSim=").append(this.dataSim).append(',');
      paramStringBuilder.append("networkName=").append(this.networkName).append(',');
      paramStringBuilder.append("networkNameData=").append(this.networkNameData).append(',');
      paramStringBuilder.append("dataConnected=").append(this.dataConnected).append(',');
      paramStringBuilder.append("isDefault=").append(this.isDefault).append(',');
      paramStringBuilder.append("isEmergency=").append(this.isEmergency).append(',');
      paramStringBuilder.append("isForbidden= ").append(this.isForbidden).append(',');
      paramStringBuilder.append("airplaneMode=").append(this.airplaneMode).append(',');
      paramStringBuilder.append("carrierNetworkChangeMode=").append(this.carrierNetworkChangeMode).append(',');
      paramStringBuilder.append("userSetup=").append(this.userSetup);
      paramStringBuilder.append("voiceLevel=").append(this.voiceLevel).append(',');
      paramStringBuilder.append("carrierNetworkChangeMode=").append(this.carrierNetworkChangeMode);
      paramStringBuilder.append("imsRadioTechnology=").append(this.imsRadioTechnology);
      paramStringBuilder.append("simstate=").append(this.simstate).append(',');
      paramStringBuilder.append("provision=").append(this.provision).append(',');
      paramStringBuilder.append("isroaming=").append(this.isroaming).append(',');
      paramStringBuilder.append("isDefaultDataSubId=").append(this.isDefaultDataSubId);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\MobileSignalController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */