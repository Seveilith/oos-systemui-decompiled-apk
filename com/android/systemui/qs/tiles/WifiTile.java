package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.wifi.AccessPoint;
import com.android.systemui.qs.QSDetailItems;
import com.android.systemui.qs.QSDetailItems.Callback;
import com.android.systemui.qs.QSDetailItems.Item;
import com.android.systemui.qs.QSIconView;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.DetailAdapter;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.ResourceIcon;
import com.android.systemui.qs.QSTile.SignalState;
import com.android.systemui.qs.SignalTileView;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.NetworkController.AccessPointController;
import com.android.systemui.statusbar.policy.NetworkController.AccessPointController.AccessPointCallback;
import com.android.systemui.statusbar.policy.NetworkController.IconState;
import com.android.systemui.statusbar.policy.SignalCallbackAdapter;
import com.android.systemui.util.MdmLogger;
import java.util.List;

public class WifiTile
  extends QSTile<QSTile.SignalState>
{
  private static final Intent WIFI_SETTINGS = new Intent("android.settings.WIFI_SETTINGS");
  private final NetworkController mController;
  private final WifiDetailAdapter mDetailAdapter;
  protected final WifiSignalCallback mSignalCallback = new WifiSignalCallback();
  private final QSTile.SignalState mStateBeforeClick = newTileState();
  private final NetworkController.AccessPointController mWifiController;
  
  public WifiTile(QSTile.Host paramHost)
  {
    super(paramHost);
    this.mController = paramHost.getNetworkController();
    this.mWifiController = this.mController.getAccessPointController();
    this.mDetailAdapter = new WifiDetailAdapter(null);
  }
  
  private static String removeDoubleQuotes(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = paramString.length();
    if ((i > 1) && (paramString.charAt(0) == '"') && (paramString.charAt(i - 1) == '"')) {
      return paramString.substring(1, i - 1);
    }
    return paramString;
  }
  
  protected String composeChangeAnnouncement()
  {
    if (((QSTile.SignalState)this.mState).value) {
      return this.mContext.getString(2131690194);
    }
    return this.mContext.getString(2131690193);
  }
  
  public QSIconView createTileView(Context paramContext)
  {
    return new SignalTileView(paramContext);
  }
  
  public QSTile.DetailAdapter getDetailAdapter()
  {
    return this.mDetailAdapter;
  }
  
  public Intent getLongClickIntent()
  {
    return WIFI_SETTINGS;
  }
  
  public int getMetricsCategory()
  {
    return 126;
  }
  
  public CharSequence getTileLabel()
  {
    return this.mContext.getString(2131690290);
  }
  
  protected void handleClick()
  {
    if (!this.mWifiController.canConfigWifi())
    {
      this.mHost.startActivityDismissingKeyguard(new Intent("android.settings.WIFI_SETTINGS"));
      return;
    }
    showDetail(true);
    if (!((QSTile.SignalState)this.mState).value)
    {
      this.mController.setWifiEnabled(true);
      ((QSTile.SignalState)this.mState).value = true;
    }
  }
  
  protected void handleSecondaryClick()
  {
    boolean bool2 = false;
    ((QSTile.SignalState)this.mState).copyTo(this.mStateBeforeClick);
    Object localObject = this.mContext;
    int i = getMetricsCategory();
    if (((QSTile.SignalState)this.mState).value)
    {
      bool1 = false;
      MetricsLogger.action((Context)localObject, i, bool1);
      localObject = this.mController;
      if (!((QSTile.SignalState)this.mState).value) {
        break label85;
      }
    }
    label85:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      ((NetworkController)localObject).setWifiEnabled(bool1);
      return;
      bool1 = true;
      break;
    }
  }
  
  protected void handleUpdateState(QSTile.SignalState paramSignalState, Object paramObject)
  {
    if (DEBUG) {
      Log.d(this.TAG, "handleUpdateState arg=" + paramObject);
    }
    Object localObject = (CallbackInfo)paramObject;
    paramObject = localObject;
    if (localObject == null) {
      paramObject = this.mSignalCallback.mInfo;
    }
    boolean bool1;
    int i;
    label95:
    int j;
    label109:
    boolean bool2;
    label168:
    label187:
    StringBuffer localStringBuffer1;
    StringBuffer localStringBuffer2;
    if ((((CallbackInfo)paramObject).enabled) && (((CallbackInfo)paramObject).wifiSignalIconId > 0) && (((CallbackInfo)paramObject).enabledDesc != null))
    {
      bool1 = true;
      if ((((CallbackInfo)paramObject).wifiSignalIconId <= 0) || (((CallbackInfo)paramObject).enabledDesc != null)) {
        break label471;
      }
      i = 1;
      if (paramSignalState.value == ((CallbackInfo)paramObject).enabled) {
        break label476;
      }
      j = 1;
      if (j != 0)
      {
        this.mDetailAdapter.setItemsVisible(((CallbackInfo)paramObject).enabled);
        fireToggleStateChanged(((CallbackInfo)paramObject).enabled);
      }
      paramSignalState.value = ((CallbackInfo)paramObject).enabled;
      paramSignalState.colored = paramSignalState.value;
      paramSignalState.connected = bool1;
      if (!((CallbackInfo)paramObject).enabled) {
        break label482;
      }
      bool2 = ((CallbackInfo)paramObject).activityIn;
      paramSignalState.activityIn = bool2;
      if (!((CallbackInfo)paramObject).enabled) {
        break label488;
      }
      bool2 = ((CallbackInfo)paramObject).activityOut;
      paramSignalState.activityOut = bool2;
      paramSignalState.filter = true;
      localStringBuffer1 = new StringBuffer();
      localStringBuffer2 = new StringBuffer();
      localObject = this.mContext.getResources();
      if (paramSignalState.value) {
        break label494;
      }
      paramSignalState.icon = QSTile.ResourceIcon.get(2130837853);
      paramSignalState.label = ((Resources)localObject).getString(2131690290);
      label253:
      localStringBuffer1.append(this.mContext.getString(2131690290)).append(",");
      if (!paramSignalState.value) {
        break label576;
      }
      localStringBuffer2.append(((Resources)localObject).getString(2131690294)).append(",");
      if (bool1)
      {
        localStringBuffer1.append(((CallbackInfo)paramObject).wifiSignalContentDescription).append(",");
        localStringBuffer1.append(removeDoubleQuotes(((CallbackInfo)paramObject).enabledDesc));
        localStringBuffer2.append(((CallbackInfo)paramObject).wifiSignalContentDescription).append(",");
        localStringBuffer2.append(removeDoubleQuotes(((CallbackInfo)paramObject).enabledDesc));
      }
    }
    for (;;)
    {
      paramSignalState.minimalContentDescription = localStringBuffer1;
      localStringBuffer2.append(",").append(((Resources)localObject).getString(2131690637, new Object[] { getTileLabel() }));
      paramSignalState.contentDescription = localStringBuffer2;
      paramObject = paramSignalState.label;
      if (paramSignalState.connected) {
        paramObject = ((Resources)localObject).getString(2131690120, new Object[] { paramSignalState.label });
      }
      paramSignalState.dualLabelContentDescription = ((CharSequence)paramObject);
      paramSignalState.expandedAccessibilityClassName = Button.class.getName();
      paramSignalState.minimalAccessibilityClassName = Switch.class.getName();
      return;
      bool1 = false;
      break;
      label471:
      i = 0;
      break label95;
      label476:
      j = 0;
      break label109;
      label482:
      bool2 = false;
      break label168;
      label488:
      bool2 = false;
      break label187;
      label494:
      if (bool1)
      {
        paramSignalState.icon = QSTile.ResourceIcon.get(((CallbackInfo)paramObject).wifiSignalIconId);
        paramSignalState.label = removeDoubleQuotes(((CallbackInfo)paramObject).enabledDesc);
        break label253;
      }
      if (i != 0)
      {
        paramSignalState.icon = QSTile.ResourceIcon.get(2130837854);
        paramSignalState.label = ((Resources)localObject).getString(2131690290);
        break label253;
      }
      paramSignalState.icon = QSTile.ResourceIcon.get(2130837860);
      paramSignalState.label = ((Resources)localObject).getString(2131690290);
      break label253;
      label576:
      localStringBuffer2.append(((Resources)localObject).getString(2131690293));
    }
  }
  
  public boolean isAvailable()
  {
    return this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi");
  }
  
  public QSTile.SignalState newTileState()
  {
    return new QSTile.SignalState();
  }
  
  public void setDetailListening(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mWifiController.addAccessPointCallback(this.mDetailAdapter);
      return;
    }
    this.mWifiController.removeAccessPointCallback(this.mDetailAdapter);
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mController.addSignalCallback(this.mSignalCallback);
      return;
    }
    this.mController.removeSignalCallback(this.mSignalCallback);
  }
  
  protected boolean shouldAnnouncementBeDelayed()
  {
    return this.mStateBeforeClick.value == ((QSTile.SignalState)this.mState).value;
  }
  
  protected static final class CallbackInfo
  {
    boolean activityIn;
    boolean activityOut;
    boolean connected;
    boolean enabled;
    String enabledDesc;
    String wifiSignalContentDescription;
    int wifiSignalIconId;
    
    public String toString()
    {
      return "CallbackInfo[" + "enabled=" + this.enabled + ",connected=" + this.connected + ",wifiSignalIconId=" + this.wifiSignalIconId + ",enabledDesc=" + this.enabledDesc + ",activityIn=" + this.activityIn + ",activityOut=" + this.activityOut + ",wifiSignalContentDescription=" + this.wifiSignalContentDescription + ']';
    }
  }
  
  private final class WifiDetailAdapter
    implements QSTile.DetailAdapter, NetworkController.AccessPointController.AccessPointCallback, QSDetailItems.Callback
  {
    private AccessPoint[] mAccessPoints;
    private QSDetailItems mItems;
    
    private WifiDetailAdapter() {}
    
    private void updateItems()
    {
      if (this.mItems == null) {
        return;
      }
      Object localObject = null;
      if (this.mAccessPoints != null)
      {
        QSDetailItems.Item[] arrayOfItem = new QSDetailItems.Item[this.mAccessPoints.length];
        int i = 0;
        localObject = arrayOfItem;
        if (i < this.mAccessPoints.length)
        {
          AccessPoint localAccessPoint = this.mAccessPoints[i];
          QSDetailItems.Item localItem = new QSDetailItems.Item();
          localItem.tag = localAccessPoint;
          localItem.icon = WifiTile.-get7(WifiTile.this).getIcon(localAccessPoint);
          localItem.line1 = localAccessPoint.getSsid();
          if (localAccessPoint.isActive())
          {
            localObject = localAccessPoint.getSummary();
            label106:
            localItem.line2 = ((CharSequence)localObject);
            if (localAccessPoint.getSecurity() == 0) {
              break label175;
            }
          }
          label175:
          for (localObject = WifiTile.-get3(WifiTile.this).getDrawable(2130838096);; localObject = null)
          {
            localItem.overlay = ((Drawable)localObject);
            arrayOfItem[i] = localItem;
            if (localItem.overlay != null) {
              localItem.overlay.setTint(QSIconView.sIconColor);
            }
            i += 1;
            break;
            localObject = null;
            break label106;
          }
        }
      }
      this.mItems.setItems((QSDetailItems.Item[])localObject);
    }
    
    public View createDetailView(Context paramContext, View paramView, ViewGroup paramViewGroup)
    {
      String str;
      StringBuilder localStringBuilder;
      if (WifiTile.-get0())
      {
        str = WifiTile.-get1(WifiTile.this);
        localStringBuilder = new StringBuilder().append("createDetailView convertView=");
        if (paramView == null) {
          break label141;
        }
      }
      label141:
      for (boolean bool = true;; bool = false)
      {
        Log.d(str, bool);
        this.mAccessPoints = null;
        WifiTile.-get7(WifiTile.this).scanForAccessPoints();
        WifiTile.this.fireScanStateChanged(true);
        this.mItems = QSDetailItems.convertOrInflate(paramContext, paramView, paramViewGroup);
        this.mItems.setTagSuffix("Wifi");
        this.mItems.setCallback(this);
        this.mItems.setEmptyState(2130837852, 2131690295);
        updateItems();
        setItemsVisible(((QSTile.SignalState)WifiTile.-get6(WifiTile.this)).value);
        return this.mItems;
      }
    }
    
    public int getMetricsCategory()
    {
      return 152;
    }
    
    public Intent getSettingsIntent()
    {
      return WifiTile.-get2();
    }
    
    public CharSequence getTitle()
    {
      return WifiTile.-get3(WifiTile.this).getString(2131690290);
    }
    
    public Boolean getToggleState()
    {
      return Boolean.valueOf(((QSTile.SignalState)WifiTile.-get6(WifiTile.this)).value);
    }
    
    public void onAccessPointsChanged(List<AccessPoint> paramList)
    {
      this.mAccessPoints = ((AccessPoint[])paramList.toArray(new AccessPoint[paramList.size()]));
      updateItems();
      if ((paramList != null) && (paramList.size() > 0)) {
        WifiTile.this.fireScanStateChanged(false);
      }
    }
    
    public void onDetailItemClick(QSDetailItems.Item paramItem)
    {
      if ((paramItem == null) || (paramItem.tag == null)) {
        return;
      }
      paramItem = (AccessPoint)paramItem.tag;
      if ((!paramItem.isActive()) && (WifiTile.-get7(WifiTile.this).connect(paramItem))) {
        WifiTile.-get5(WifiTile.this).collapsePanels();
      }
      WifiTile.this.showDetail(false);
    }
    
    public void onDetailItemDisconnect(QSDetailItems.Item paramItem) {}
    
    public void onSettingsActivityTriggered(Intent paramIntent)
    {
      WifiTile.-get5(WifiTile.this).startActivityDismissingKeyguard(paramIntent);
    }
    
    public void setItemsVisible(boolean paramBoolean)
    {
      if (this.mItems == null) {
        return;
      }
      this.mItems.setItemsVisible(paramBoolean);
    }
    
    public void setToggleState(boolean paramBoolean)
    {
      if (WifiTile.-get0()) {
        Log.d(WifiTile.-get1(WifiTile.this), "setToggleState " + paramBoolean);
      }
      MetricsLogger.action(WifiTile.-get3(WifiTile.this), 153, paramBoolean);
      MdmLogger.logQsTile(WifiTile.-get1(WifiTile.this), "full_switch", "1");
      WifiTile.-get4(WifiTile.this).setWifiEnabled(paramBoolean);
      WifiTile.this.showDetail(false);
    }
  }
  
  protected final class WifiSignalCallback
    extends SignalCallbackAdapter
  {
    final WifiTile.CallbackInfo mInfo = new WifiTile.CallbackInfo();
    
    protected WifiSignalCallback() {}
    
    public void setWifiIndicators(boolean paramBoolean1, NetworkController.IconState paramIconState1, NetworkController.IconState paramIconState2, boolean paramBoolean2, boolean paramBoolean3, String paramString)
    {
      if (WifiTile.-get0()) {
        Log.d(WifiTile.-get1(WifiTile.this), "onWifiSignalChanged enabled=" + paramBoolean1);
      }
      this.mInfo.enabled = paramBoolean1;
      this.mInfo.connected = paramIconState2.visible;
      this.mInfo.wifiSignalIconId = paramIconState2.icon;
      this.mInfo.enabledDesc = paramString;
      this.mInfo.activityIn = paramBoolean2;
      this.mInfo.activityOut = paramBoolean3;
      this.mInfo.wifiSignalContentDescription = paramIconState2.contentDescription;
      WifiTile.-wrap0(WifiTile.this, this.mInfo);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\WifiTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */