package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.systemui.qs.QSDetailItems;
import com.android.systemui.qs.QSDetailItems.Callback;
import com.android.systemui.qs.QSDetailItems.Item;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.BooleanState;
import com.android.systemui.qs.QSTile.DetailAdapter;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.ResourceIcon;
import com.android.systemui.statusbar.policy.BluetoothController;
import com.android.systemui.statusbar.policy.BluetoothController.Callback;
import com.android.systemui.util.MdmLogger;
import java.util.ArrayList;
import java.util.Iterator;

public class BluetoothTile
  extends QSTile<QSTile.BooleanState>
{
  private static final Intent BLUETOOTH_SETTINGS = new Intent("android.settings.BLUETOOTH_SETTINGS");
  private final BluetoothController.Callback mCallback = new BluetoothController.Callback()
  {
    public void onBluetoothBatteryChanged() {}
    
    public void onBluetoothDevicesChanged()
    {
      BluetoothTile.-get5(BluetoothTile.this).removeCallbacks(BluetoothTile.this.mUpdateTask);
      BluetoothTile.-get5(BluetoothTile.this).postDelayed(BluetoothTile.this.mUpdateTask, 200L);
      BluetoothTile.this.refreshState();
    }
    
    public void onBluetoothStateChange(boolean paramAnonymousBoolean)
    {
      BluetoothTile.this.refreshState();
    }
  };
  private final BluetoothController mController;
  private final BluetoothDetailAdapter mDetailAdapter;
  Runnable mUpdateTask = new Runnable()
  {
    public void run()
    {
      BluetoothTile.BluetoothDetailAdapter.-wrap0(BluetoothTile.-get4(BluetoothTile.this));
    }
  };
  
  public BluetoothTile(QSTile.Host paramHost)
  {
    super(paramHost);
    this.mController = paramHost.getBluetoothController();
    this.mDetailAdapter = new BluetoothDetailAdapter(null);
  }
  
  protected String composeChangeAnnouncement()
  {
    if (((QSTile.BooleanState)this.mState).value) {
      return this.mContext.getString(2131690214);
    }
    return this.mContext.getString(2131690213);
  }
  
  public QSTile.DetailAdapter getDetailAdapter()
  {
    return this.mDetailAdapter;
  }
  
  public Intent getLongClickIntent()
  {
    return new Intent("android.settings.BLUETOOTH_SETTINGS");
  }
  
  public int getMetricsCategory()
  {
    return 113;
  }
  
  public CharSequence getTileLabel()
  {
    return this.mContext.getString(2131690268);
  }
  
  protected void handleClick()
  {
    if (!this.mController.canConfigBluetooth())
    {
      this.mHost.startActivityDismissingKeyguard(new Intent("android.settings.BLUETOOTH_SETTINGS"));
      return;
    }
    showDetail(true);
    ((QSTile.BooleanState)this.mState).value = this.mController.isBluetoothEnabled();
    if (!((QSTile.BooleanState)this.mState).value)
    {
      ((QSTile.BooleanState)this.mState).value = true;
      this.mController.setBluetoothEnabled(true);
    }
  }
  
  protected void handleSecondaryClick()
  {
    boolean bool2 = false;
    boolean bool3 = this.mController.isBluetoothEnabled();
    Object localObject = this.mContext;
    int i = getMetricsCategory();
    if (bool3)
    {
      bool1 = false;
      MetricsLogger.action((Context)localObject, i, bool1);
      localObject = this.mController;
      if (!bool3) {
        break label65;
      }
    }
    label65:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      ((BluetoothController)localObject).setBluetoothEnabled(bool1);
      return;
      bool1 = true;
      break;
    }
  }
  
  protected void handleUpdateState(QSTile.BooleanState paramBooleanState, Object paramObject)
  {
    boolean bool1 = this.mController.isBluetoothEnabled();
    boolean bool2 = this.mController.isBluetoothConnected();
    boolean bool3 = this.mController.isBluetoothConnecting();
    paramBooleanState.value = bool1;
    paramBooleanState.autoMirrorDrawable = false;
    paramBooleanState.minimalContentDescription = this.mContext.getString(2131690208);
    if (bool1)
    {
      paramBooleanState.label = null;
      if (bool2)
      {
        paramBooleanState.icon = QSTile.ResourceIcon.get(2130837773);
        paramBooleanState.label = this.mController.getLastDeviceName();
        paramBooleanState.contentDescription = this.mContext.getString(2131690121, new Object[] { paramBooleanState.label });
        paramBooleanState.minimalContentDescription = (paramBooleanState.minimalContentDescription + "," + paramBooleanState.contentDescription);
        if (TextUtils.isEmpty(paramBooleanState.label)) {
          paramBooleanState.label = this.mContext.getString(2131690268);
        }
      }
    }
    for (;;)
    {
      paramObject = paramBooleanState.label;
      if (bool2)
      {
        paramObject = this.mContext.getString(2131690121, new Object[] { paramBooleanState.label });
        paramBooleanState.dualLabelContentDescription = ((CharSequence)paramObject);
      }
      paramBooleanState.dualLabelContentDescription = ((CharSequence)paramObject);
      paramBooleanState.contentDescription = (paramBooleanState.contentDescription + "," + this.mContext.getString(2131690637, new Object[] { getTileLabel() }));
      paramBooleanState.expandedAccessibilityClassName = Button.class.getName();
      paramBooleanState.minimalAccessibilityClassName = Switch.class.getName();
      return;
      if (bool3)
      {
        paramBooleanState.icon = QSTile.ResourceIcon.get(2130837774);
        paramBooleanState.contentDescription = this.mContext.getString(2131690211);
        paramBooleanState.label = this.mContext.getString(2131690268);
        paramBooleanState.minimalContentDescription = (paramBooleanState.minimalContentDescription + "," + paramBooleanState.contentDescription);
        break;
      }
      paramBooleanState.icon = QSTile.ResourceIcon.get(2130837777);
      paramBooleanState.contentDescription = (this.mContext.getString(2131690210) + "," + this.mContext.getString(2131690131));
      paramBooleanState.minimalContentDescription = (paramBooleanState.minimalContentDescription + "," + this.mContext.getString(2131690131));
      break;
      paramBooleanState.icon = QSTile.ResourceIcon.get(2130837776);
      paramBooleanState.label = this.mContext.getString(2131690268);
      paramBooleanState.contentDescription = this.mContext.getString(2131690209);
    }
  }
  
  public boolean isAvailable()
  {
    return this.mController.isBluetoothSupported();
  }
  
  public QSTile.BooleanState newTileState()
  {
    return new QSTile.BooleanState();
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mController.addStateChangedCallback(this.mCallback);
      return;
    }
    this.mController.removeStateChangedCallback(this.mCallback);
  }
  
  private final class BluetoothDetailAdapter
    implements QSTile.DetailAdapter, QSDetailItems.Callback
  {
    private QSDetailItems mItems;
    
    private BluetoothDetailAdapter() {}
    
    private void updateItems()
    {
      if (this.mItems == null) {
        return;
      }
      ArrayList localArrayList = new ArrayList();
      Object localObject = BluetoothTile.-get3(BluetoothTile.this).getDevices();
      if (localObject != null)
      {
        localObject = ((Iterable)localObject).iterator();
        while (((Iterator)localObject).hasNext())
        {
          CachedBluetoothDevice localCachedBluetoothDevice = (CachedBluetoothDevice)((Iterator)localObject).next();
          if (localCachedBluetoothDevice.getBondState() != 10)
          {
            QSDetailItems.Item localItem = new QSDetailItems.Item();
            localItem.icon = 2130837777;
            localItem.line1 = localCachedBluetoothDevice.getName();
            int i = localCachedBluetoothDevice.getMaxConnectionState();
            if (i == 2)
            {
              localItem.icon = 2130837773;
              localItem.line2 = BluetoothTile.-get2(BluetoothTile.this).getString(2131690307);
              localItem.canDisconnect = true;
            }
            for (;;)
            {
              localItem.tag = localCachedBluetoothDevice;
              localArrayList.add(localItem);
              break;
              if (i == 1)
              {
                localItem.icon = 2130837774;
                localItem.line2 = BluetoothTile.-get2(BluetoothTile.this).getString(2131690308);
              }
            }
          }
        }
      }
      this.mItems.setItems((QSDetailItems.Item[])localArrayList.toArray(new QSDetailItems.Item[localArrayList.size()]));
    }
    
    public View createDetailView(Context paramContext, View paramView, ViewGroup paramViewGroup)
    {
      this.mItems = QSDetailItems.convertOrInflate(paramContext, paramView, paramViewGroup);
      this.mItems.setTagSuffix("Bluetooth");
      this.mItems.setEmptyState(2130837775, 2131690271);
      this.mItems.setCallback(this);
      updateItems();
      setItemsVisible(true);
      return this.mItems;
    }
    
    public int getMetricsCategory()
    {
      return 150;
    }
    
    public Intent getSettingsIntent()
    {
      return BluetoothTile.-get0();
    }
    
    public CharSequence getTitle()
    {
      return BluetoothTile.-get2(BluetoothTile.this).getString(2131690268);
    }
    
    public Boolean getToggleState()
    {
      return Boolean.valueOf(true);
    }
    
    public void onDetailItemClick(QSDetailItems.Item paramItem)
    {
      if ((paramItem == null) || (paramItem.tag == null)) {
        return;
      }
      paramItem = (CachedBluetoothDevice)paramItem.tag;
      if ((paramItem != null) && (paramItem.getMaxConnectionState() == 0)) {
        BluetoothTile.-get3(BluetoothTile.this).connect(paramItem);
      }
    }
    
    public void onDetailItemDisconnect(QSDetailItems.Item paramItem)
    {
      if ((paramItem == null) || (paramItem.tag == null)) {
        return;
      }
      paramItem = (CachedBluetoothDevice)paramItem.tag;
      if (paramItem != null) {
        BluetoothTile.-get3(BluetoothTile.this).disconnect(paramItem);
      }
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
      MetricsLogger.action(BluetoothTile.-get2(BluetoothTile.this), 154, paramBoolean);
      MdmLogger.logQsTile(BluetoothTile.-get1(BluetoothTile.this), "full_switch", "1");
      BluetoothTile.-get3(BluetoothTile.this).setBluetoothEnabled(paramBoolean);
      BluetoothTile.this.showDetail(false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\BluetoothTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */