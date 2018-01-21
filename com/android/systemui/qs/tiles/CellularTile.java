package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.telephony.SubscriptionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.net.DataUsageController;
import com.android.settingslib.net.DataUsageController.DataUsageInfo;
import com.android.systemui.qs.QSIconView;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.DetailAdapter;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.ResourceIcon;
import com.android.systemui.qs.QSTile.SignalState;
import com.android.systemui.qs.SignalTileView;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.NetworkController.IconState;
import com.android.systemui.statusbar.policy.SignalCallbackAdapter;
import com.android.systemui.util.MdmLogger;

public class CellularTile
  extends QSTile<QSTile.SignalState>
{
  static final Intent CELLULAR_SETTINGS = new Intent("com.oneplus.security.action.USAGE_DATA_SUMMARY");
  private DataUsageDetailView.DataUsageBuilder mBuilder;
  private final NetworkController mController;
  private final DataUsageController mDataController;
  private final CellularDetailAdapter mDetailAdapter;
  private DataUsageController.DataUsageInfo mInfo = null;
  private final CellSignalCallback mSignalCallback = new CellSignalCallback(null);
  private boolean mStartToPreload;
  private SubscriptionManager mSubscriptionManager = null;
  
  public CellularTile(QSTile.Host paramHost)
  {
    super(paramHost);
    this.mController = paramHost.getNetworkController();
    this.mDataController = this.mController.getMobileDataController();
    this.mDetailAdapter = new CellularDetailAdapter(null);
    this.mBuilder = new DataUsageDetailView.DataUsageBuilder(this.mContext, this.mController);
    CELLULAR_SETTINGS.putExtra("tracker_event", 2);
  }
  
  private int getDefaultDataSimIndex()
  {
    if (this.mSubscriptionManager == null) {
      this.mSubscriptionManager = SubscriptionManager.from(this.mContext);
    }
    return this.mSubscriptionManager.getDefaultDataPhoneId();
  }
  
  public QSIconView createTileView(Context paramContext)
  {
    return new SignalTileView(paramContext);
  }
  
  public QSTile.DetailAdapter getDetailAdapter()
  {
    return this.mDetailAdapter;
  }
  
  /* Error */
  public Intent getLongClickIntent()
  {
    // Byte code:
    //   0: ldc -97
    //   2: invokestatic 165	android/os/SystemProperties:get	(Ljava/lang/String;)Ljava/lang/String;
    //   5: astore 6
    //   7: iconst_0
    //   8: istore_2
    //   9: iconst_0
    //   10: istore_1
    //   11: aload 6
    //   13: ldc -89
    //   15: invokestatic 173	android/text/TextUtils:split	(Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;
    //   18: astore 6
    //   20: iconst_0
    //   21: istore_3
    //   22: iload_1
    //   23: istore_2
    //   24: iload_1
    //   25: istore 4
    //   27: iload_3
    //   28: aload 6
    //   30: arraylength
    //   31: if_icmpge +89 -> 120
    //   34: iload_1
    //   35: istore 4
    //   37: iload_1
    //   38: istore_2
    //   39: aload 6
    //   41: iload_3
    //   42: aaload
    //   43: invokevirtual 179	java/lang/String:isEmpty	()Z
    //   46: ifne +41 -> 87
    //   49: iload_1
    //   50: istore 4
    //   52: iload_1
    //   53: istore_2
    //   54: aload 6
    //   56: iload_3
    //   57: aaload
    //   58: ldc -75
    //   60: invokevirtual 185	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   63: ifne +24 -> 87
    //   66: iload_1
    //   67: istore_2
    //   68: aload 6
    //   70: iload_3
    //   71: aaload
    //   72: ldc -69
    //   74: invokevirtual 185	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   77: istore 5
    //   79: iload 5
    //   81: ifeq +16 -> 97
    //   84: iload_1
    //   85: istore 4
    //   87: iload_3
    //   88: iconst_1
    //   89: iadd
    //   90: istore_3
    //   91: iload 4
    //   93: istore_1
    //   94: goto -72 -> 22
    //   97: iload_1
    //   98: iconst_1
    //   99: iadd
    //   100: istore 4
    //   102: goto -15 -> 87
    //   105: astore 6
    //   107: aload_0
    //   108: getfield 40	com/android/systemui/qs/tiles/CellularTile:TAG	Ljava/lang/String;
    //   111: ldc -67
    //   113: invokestatic 195	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   116: pop
    //   117: iload_2
    //   118: istore 4
    //   120: getstatic 101	com/android/systemui/qs/tiles/CellularTile:CELLULAR_SETTINGS	Landroid/content/Intent;
    //   123: astore 6
    //   125: iload 4
    //   127: iconst_1
    //   128: if_icmple +48 -> 176
    //   131: aload 6
    //   133: ldc -59
    //   135: aload_0
    //   136: invokespecial 83	com/android/systemui/qs/tiles/CellularTile:getDefaultDataSimIndex	()I
    //   139: invokevirtual 135	android/content/Intent:putExtra	(Ljava/lang/String;I)Landroid/content/Intent;
    //   142: pop
    //   143: aload_0
    //   144: getfield 40	com/android/systemui/qs/tiles/CellularTile:TAG	Ljava/lang/String;
    //   147: new 199	java/lang/StringBuilder
    //   150: dup
    //   151: invokespecial 201	java/lang/StringBuilder:<init>	()V
    //   154: ldc -53
    //   156: invokevirtual 207	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   159: aload_0
    //   160: invokespecial 83	com/android/systemui/qs/tiles/CellularTile:getDefaultDataSimIndex	()I
    //   163: invokevirtual 210	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   166: invokevirtual 214	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   169: invokestatic 217	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   172: pop
    //   173: aload 6
    //   175: areturn
    //   176: aload 6
    //   178: ldc -59
    //   180: iconst_0
    //   181: invokevirtual 135	android/content/Intent:putExtra	(Ljava/lang/String;I)Landroid/content/Intent;
    //   184: pop
    //   185: aload_0
    //   186: getfield 40	com/android/systemui/qs/tiles/CellularTile:TAG	Ljava/lang/String;
    //   189: ldc -37
    //   191: invokestatic 217	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   194: pop
    //   195: aload 6
    //   197: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	198	0	this	CellularTile
    //   10	90	1	i	int
    //   8	110	2	j	int
    //   21	70	3	k	int
    //   25	104	4	m	int
    //   77	3	5	bool	boolean
    //   5	64	6	localObject	Object
    //   105	1	6	localException	Exception
    //   123	73	6	localIntent	Intent
    // Exception table:
    //   from	to	target	type
    //   11	20	105	java/lang/Exception
    //   27	34	105	java/lang/Exception
    //   39	49	105	java/lang/Exception
    //   54	66	105	java/lang/Exception
    //   68	79	105	java/lang/Exception
  }
  
  public int getMetricsCategory()
  {
    return 115;
  }
  
  public CharSequence getTileLabel()
  {
    return this.mContext.getString(2131690313);
  }
  
  protected void handleClick()
  {
    MetricsLogger.action(this.mContext, getMetricsCategory());
    if ((this.mDataController.isMobileDataSupported()) && (getDefaultDataSimIndex() >= 0))
    {
      showDetail(true);
      return;
    }
    this.mHost.startActivityDismissingKeyguard(CELLULAR_SETTINGS);
  }
  
  protected void handleUpdateState(QSTile.SignalState paramSignalState, Object paramObject)
  {
    boolean bool2 = false;
    Object localObject = (CallbackInfo)paramObject;
    paramObject = localObject;
    if (localObject == null) {
      paramObject = CellSignalCallback.-get0(this.mSignalCallback);
    }
    Resources localResources = this.mContext.getResources();
    int i;
    label81:
    label107:
    label131:
    int j;
    if (((CallbackInfo)paramObject).noSim)
    {
      i = 2130837803;
      paramSignalState.icon = QSTile.ResourceIcon.get(i);
      paramSignalState.isOverlayIconWide = ((CallbackInfo)paramObject).isDataTypeIconWide;
      if ((((CallbackInfo)paramObject).noSim) || (!((CallbackInfo)paramObject).enabled)) {
        break label482;
      }
      bool1 = ((CallbackInfo)paramObject).airplaneModeEnabled;
      if ((bool1) || (((CallbackInfo)paramObject).mobileSignalIconId <= 0)) {
        break label494;
      }
      if (!localResources.getBoolean(2131558449)) {
        break label488;
      }
      bool1 = false;
      paramSignalState.isShowRoaming = bool1;
      paramSignalState.subId = ((CallbackInfo)paramObject).subId;
      if (!((CallbackInfo)paramObject).noSim) {
        break label500;
      }
      bool1 = false;
      paramSignalState.autoMirrorDrawable = bool1;
      if ((((CallbackInfo)paramObject).enabled) && (((CallbackInfo)paramObject).dataTypeIconId > 0) && (this.mDataController.isMobileDataEnabled()) && (!((CallbackInfo)paramObject).airplaneModeEnabled)) {
        break label506;
      }
      j = 0;
      label171:
      paramSignalState.overlayIconId = j;
      if ((!this.mDataController.isMobileDataEnabled()) && (((CallbackInfo)paramObject).enabled) && (!((CallbackInfo)paramObject).noSim)) {
        break label515;
      }
      label201:
      if (i == 2130837803) {
        break label551;
      }
      bool1 = true;
      label211:
      paramSignalState.filter = bool1;
      if ((((CallbackInfo)paramObject).enabled) && (!((CallbackInfo)paramObject).noSim)) {
        break label557;
      }
      bool1 = false;
      label234:
      paramSignalState.activityIn = bool1;
      if ((((CallbackInfo)paramObject).enabled) && (!((CallbackInfo)paramObject).noSim)) {
        break label566;
      }
      bool1 = false;
      label257:
      paramSignalState.activityOut = bool1;
      if (!((CallbackInfo)paramObject).enabled) {
        break label575;
      }
      localObject = ((CallbackInfo)paramObject).enabledDesc;
      label276:
      paramSignalState.label = ((CharSequence)localObject);
      if ((!((CallbackInfo)paramObject).enabled) || (((CallbackInfo)paramObject).mobileSignalIconId <= 0)) {
        break label588;
      }
      localObject = ((CallbackInfo)paramObject).signalContentDescription;
      label302:
      if (!((CallbackInfo)paramObject).noSim) {
        break label601;
      }
      paramSignalState.contentDescription = paramSignalState.label;
      paramSignalState.contentDescription = (paramSignalState.contentDescription + "," + localResources.getString(2131690637, new Object[] { getTileLabel() }));
      localObject = Button.class.getName();
      paramSignalState.expandedAccessibilityClassName = ((String)localObject);
      paramSignalState.minimalAccessibilityClassName = ((String)localObject);
      if (!this.mDataController.isMobileDataSupported()) {
        break label706;
      }
      bool1 = this.mDataController.isMobileDataEnabled();
      label404:
      paramSignalState.value = bool1;
      bool1 = bool2;
      if (!((CallbackInfo)paramObject).noSim) {
        if (!((CallbackInfo)paramObject).airplaneModeEnabled) {
          break label712;
        }
      }
    }
    label482:
    label488:
    label494:
    label500:
    label506:
    label515:
    label551:
    label557:
    label566:
    label575:
    label588:
    label601:
    label706:
    label712:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      paramSignalState.colored = bool1;
      return;
      if ((!((CallbackInfo)paramObject).enabled) || (((CallbackInfo)paramObject).airplaneModeEnabled))
      {
        i = 2130837826;
        break;
      }
      if (((CallbackInfo)paramObject).mobileSignalIconId > 0)
      {
        i = ((CallbackInfo)paramObject).mobileSignalIconId;
        break;
      }
      i = 2130837842;
      break;
      bool1 = true;
      break label81;
      bool1 = true;
      break label107;
      bool1 = false;
      break label107;
      bool1 = true;
      break label131;
      j = ((CallbackInfo)paramObject).dataTypeIconId;
      break label171;
      if ((((CallbackInfo)paramObject).airplaneModeEnabled) || (i == 2130837842) || (i == 2130837826)) {
        break label201;
      }
      paramSignalState.overlayIconId = 2130837788;
      paramSignalState.isOverlayIconWide = false;
      break label201;
      bool1 = false;
      break label211;
      bool1 = ((CallbackInfo)paramObject).activityIn;
      break label234;
      bool1 = ((CallbackInfo)paramObject).activityOut;
      break label257;
      localObject = localResources.getString(2131690284);
      break label276;
      localObject = localResources.getString(2131690130);
      break label302;
      if (((CallbackInfo)paramObject).enabled) {}
      for (String str = localResources.getString(2131690159);; str = localResources.getString(2131690160))
      {
        paramSignalState.contentDescription = localResources.getString(2131690195, new Object[] { str, localObject, paramSignalState.label });
        paramSignalState.minimalContentDescription = localResources.getString(2131690195, new Object[] { localResources.getString(2131690158), localObject, paramSignalState.label });
        break;
      }
      bool1 = false;
      break label404;
    }
  }
  
  public boolean isAvailable()
  {
    return this.mController.hasMobileDataFeature();
  }
  
  public QSTile.SignalState newTileState()
  {
    return new QSTile.SignalState();
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mController.addSignalCallback(this.mSignalCallback);
      if (!this.mStartToPreload)
      {
        this.mStartToPreload = true;
        new Thread()
        {
          public void run()
          {
            CellularTile.-set0(CellularTile.this, CellularTile.-get1(CellularTile.this).buildOneplusDataUsage(CellularTile.-wrap0(CellularTile.this), true));
            CellularTile.-set1(CellularTile.this, false);
          }
        }.start();
      }
      return;
    }
    this.mController.removeSignalCallback(this.mSignalCallback);
  }
  
  private static final class CallbackInfo
  {
    boolean activityIn;
    boolean activityOut;
    boolean airplaneModeEnabled;
    String dataContentDescription;
    int dataTypeIconId;
    boolean enabled;
    String enabledDesc;
    boolean isDataTypeIconWide;
    int mobileSignalIconId;
    boolean noSim;
    String signalContentDescription;
    int subId;
    boolean wifiEnabled;
  }
  
  private final class CellSignalCallback
    extends SignalCallbackAdapter
  {
    private final CellularTile.CallbackInfo mInfo = new CellularTile.CallbackInfo(null);
    
    private CellSignalCallback() {}
    
    public void setIsAirplaneMode(NetworkController.IconState paramIconState)
    {
      this.mInfo.airplaneModeEnabled = paramIconState.visible;
      CellularTile.-wrap1(CellularTile.this, this.mInfo);
    }
    
    public void setMobileDataEnabled(boolean paramBoolean)
    {
      CellularTile.-get5(CellularTile.this).setMobileDataEnabled(paramBoolean);
    }
    
    public void setMobileDataIndicators(NetworkController.IconState paramIconState1, NetworkController.IconState paramIconState2, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, String paramString1, String paramString2, boolean paramBoolean3, int paramInt7, boolean paramBoolean4, boolean paramBoolean5)
    {
      if (paramIconState2 == null) {
        return;
      }
      this.mInfo.enabled = paramIconState2.visible;
      this.mInfo.mobileSignalIconId = paramIconState2.icon;
      this.mInfo.signalContentDescription = paramIconState2.contentDescription;
      this.mInfo.dataTypeIconId = paramInt2;
      this.mInfo.dataContentDescription = paramString1;
      paramIconState1 = this.mInfo;
      if (paramBoolean4)
      {
        paramIconState1.activityIn = paramBoolean1;
        paramIconState1 = this.mInfo;
        if (!paramBoolean4) {
          break label140;
        }
        label82:
        paramIconState1.activityOut = paramBoolean2;
        this.mInfo.enabledDesc = paramString2;
        paramIconState1 = this.mInfo;
        if (paramInt2 == 0) {
          break label146;
        }
      }
      for (;;)
      {
        paramIconState1.isDataTypeIconWide = paramBoolean3;
        this.mInfo.subId = paramInt7;
        CellularTile.-wrap1(CellularTile.this, this.mInfo);
        return;
        paramBoolean1 = false;
        break;
        label140:
        paramBoolean2 = false;
        break label82;
        label146:
        paramBoolean3 = false;
      }
    }
    
    public void setNoSims(boolean paramBoolean)
    {
      this.mInfo.noSim = paramBoolean;
      if (this.mInfo.noSim)
      {
        this.mInfo.mobileSignalIconId = 0;
        this.mInfo.dataTypeIconId = 0;
        this.mInfo.enabled = true;
        this.mInfo.enabledDesc = CellularTile.-get2(CellularTile.this).getString(2131690702);
        this.mInfo.signalContentDescription = this.mInfo.enabledDesc;
      }
      CellularTile.-wrap1(CellularTile.this, this.mInfo);
    }
    
    public void setWifiIndicators(boolean paramBoolean1, NetworkController.IconState paramIconState1, NetworkController.IconState paramIconState2, boolean paramBoolean2, boolean paramBoolean3, String paramString)
    {
      this.mInfo.wifiEnabled = paramBoolean1;
      CellularTile.-wrap1(CellularTile.this, this.mInfo);
    }
  }
  
  private final class CellularDetailAdapter
    implements QSTile.DetailAdapter
  {
    private CellularDetailAdapter() {}
    
    public View createDetailView(Context paramContext, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView != null) {}
      for (;;)
      {
        paramContext = (DataUsageDetailView)paramView;
        paramContext.setNetworkController(CellularTile.-get3(CellularTile.this));
        paramContext.bind(CellularTile.-get6(CellularTile.this));
        return paramContext;
        paramView = LayoutInflater.from(CellularTile.-get2(CellularTile.this)).inflate(2130968613, paramViewGroup, false);
      }
    }
    
    public int getMetricsCategory()
    {
      return 117;
    }
    
    public Intent getSettingsIntent()
    {
      return CellularTile.CELLULAR_SETTINGS;
    }
    
    public CharSequence getTitle()
    {
      return CellularTile.-get2(CellularTile.this).getString(2131690313);
    }
    
    public boolean getToggleEnabled()
    {
      return !CellularTile.CellSignalCallback.-get0(CellularTile.-get7(CellularTile.this)).airplaneModeEnabled;
    }
    
    public Boolean getToggleState()
    {
      if (CellularTile.-get4(CellularTile.this).isMobileDataSupported()) {
        return Boolean.valueOf(CellularTile.-get4(CellularTile.this).isMobileDataEnabled());
      }
      return null;
    }
    
    public void setMobileDataEnabled(boolean paramBoolean)
    {
      CellularTile.this.fireToggleStateChanged(paramBoolean);
    }
    
    public void setToggleState(boolean paramBoolean)
    {
      MetricsLogger.action(CellularTile.-get2(CellularTile.this), 155, paramBoolean);
      MdmLogger.logQsTile(CellularTile.-get0(CellularTile.this), "full_switch", "1");
      CellularTile.-get4(CellularTile.this).setMobileDataEnabled(paramBoolean);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\CellularTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */