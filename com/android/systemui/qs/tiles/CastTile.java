package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.qs.QSDetailItems;
import com.android.systemui.qs.QSDetailItems.Callback;
import com.android.systemui.qs.QSDetailItems.Item;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.BooleanState;
import com.android.systemui.qs.QSTile.DetailAdapter;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.ResourceIcon;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.CastController.Callback;
import com.android.systemui.statusbar.policy.CastController.CastDevice;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.KeyguardMonitor.Callback;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

public class CastTile
  extends QSTile<QSTile.BooleanState>
{
  private static final Intent CAST_SETTINGS = new Intent("android.settings.CAST_SETTINGS");
  private final Callback mCallback = new Callback(null);
  private final CastController mController;
  private final CastDetailAdapter mDetailAdapter;
  private final KeyguardMonitor mKeyguard;
  
  public CastTile(QSTile.Host paramHost)
  {
    super(paramHost);
    this.mController = paramHost.getCastController();
    this.mDetailAdapter = new CastDetailAdapter(null);
    this.mKeyguard = paramHost.getKeyguardMonitor();
  }
  
  private String getDeviceName(CastController.CastDevice paramCastDevice)
  {
    if (paramCastDevice.name != null) {
      return paramCastDevice.name;
    }
    return this.mContext.getString(2131690298);
  }
  
  protected String composeChangeAnnouncement()
  {
    if (!((QSTile.BooleanState)this.mState).value) {
      return this.mContext.getString(2131690232);
    }
    return null;
  }
  
  public QSTile.DetailAdapter getDetailAdapter()
  {
    return this.mDetailAdapter;
  }
  
  public Intent getLongClickIntent()
  {
    return new Intent("android.settings.CAST_SETTINGS");
  }
  
  public int getMetricsCategory()
  {
    return 114;
  }
  
  public CharSequence getTileLabel()
  {
    return this.mContext.getString(2131690296);
  }
  
  protected void handleClick()
  {
    if ((!this.mKeyguard.isSecure()) || (this.mKeyguard.canSkipBouncer()))
    {
      MetricsLogger.action(this.mContext, getMetricsCategory());
      showDetail(true);
      return;
    }
    this.mHost.startRunnableDismissingKeyguard(new Runnable()
    {
      public void run()
      {
        MetricsLogger.action(CastTile.-get3(CastTile.this), CastTile.this.getMetricsCategory());
        CastTile.this.showDetail(true);
      }
    });
  }
  
  protected void handleUpdateState(QSTile.BooleanState paramBooleanState, Object paramObject)
  {
    paramBooleanState.label = this.mContext.getString(2131690296);
    paramBooleanState.contentDescription = paramBooleanState.label;
    paramBooleanState.value = false;
    paramBooleanState.autoMirrorDrawable = false;
    paramObject = this.mController.getCastDevices();
    int i = 0;
    Iterator localIterator = ((Iterable)paramObject).iterator();
    while (localIterator.hasNext())
    {
      CastController.CastDevice localCastDevice = (CastController.CastDevice)localIterator.next();
      if (localCastDevice.state == 2)
      {
        paramBooleanState.value = true;
        paramBooleanState.label = getDeviceName(localCastDevice);
        paramBooleanState.contentDescription = (paramBooleanState.contentDescription + "," + this.mContext.getString(2131690122, new Object[] { paramBooleanState.label }));
      }
      else if (localCastDevice.state == 1)
      {
        i = 1;
      }
    }
    if ((!paramBooleanState.value) && (i != 0)) {
      paramBooleanState.label = this.mContext.getString(2131690308);
    }
    if (paramBooleanState.value) {}
    for (i = 2130837786;; i = 2130837785)
    {
      paramBooleanState.icon = QSTile.ResourceIcon.get(i);
      CastDetailAdapter.-wrap0(this.mDetailAdapter, (Set)paramObject);
      paramObject = Button.class.getName();
      paramBooleanState.expandedAccessibilityClassName = ((String)paramObject);
      paramBooleanState.minimalAccessibilityClassName = ((String)paramObject);
      paramBooleanState.contentDescription = (paramBooleanState.contentDescription + "," + this.mContext.getString(2131690636));
      return;
    }
  }
  
  protected void handleUserSwitch(int paramInt)
  {
    super.handleUserSwitch(paramInt);
    if (this.mController == null) {
      return;
    }
    this.mController.setCurrentUserId(paramInt);
  }
  
  public QSTile.BooleanState newTileState()
  {
    return new QSTile.BooleanState();
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (this.mController == null) {
      return;
    }
    if (DEBUG) {
      Log.d(this.TAG, "setListening " + paramBoolean);
    }
    if (paramBoolean)
    {
      this.mController.addCallback(this.mCallback);
      this.mKeyguard.addCallback(this.mCallback);
      return;
    }
    this.mController.setDiscovering(false);
    this.mController.removeCallback(this.mCallback);
    this.mKeyguard.removeCallback(this.mCallback);
  }
  
  private final class Callback
    implements CastController.Callback, KeyguardMonitor.Callback
  {
    private Callback() {}
    
    public void onCastDevicesChanged()
    {
      CastTile.this.refreshState();
    }
    
    public void onKeyguardChanged()
    {
      CastTile.this.refreshState();
    }
  }
  
  private final class CastDetailAdapter
    implements QSTile.DetailAdapter, QSDetailItems.Callback
  {
    private QSDetailItems mItems;
    private final LinkedHashMap<String, CastController.CastDevice> mVisibleOrder = new LinkedHashMap();
    
    private CastDetailAdapter() {}
    
    private void updateItems(Set<CastController.CastDevice> paramSet)
    {
      if (this.mItems == null) {
        return;
      }
      Iterator localIterator = null;
      Object localObject2 = null;
      Object localObject1 = localObject2;
      if (paramSet != null)
      {
        if (!paramSet.isEmpty()) {
          break label42;
        }
        localObject1 = localObject2;
      }
      label42:
      do
      {
        this.mItems.setItems((QSDetailItems.Item[])localObject1);
        return;
        localObject1 = paramSet.iterator();
        do
        {
          localObject2 = localIterator;
          if (!((Iterator)localObject1).hasNext()) {
            break;
          }
          localObject2 = (CastController.CastDevice)((Iterator)localObject1).next();
        } while (((CastController.CastDevice)localObject2).state != 2);
        localObject1 = new QSDetailItems.Item();
        ((QSDetailItems.Item)localObject1).icon = 2130837786;
        ((QSDetailItems.Item)localObject1).line1 = CastTile.-wrap0(CastTile.this, (CastController.CastDevice)localObject2);
        ((QSDetailItems.Item)localObject1).line2 = CastTile.-get3(CastTile.this).getString(2131690307);
        ((QSDetailItems.Item)localObject1).tag = localObject2;
        ((QSDetailItems.Item)localObject1).canDisconnect = true;
        localObject2 = new QSDetailItems.Item[1];
        localObject2[0] = localObject1;
        localObject1 = localObject2;
      } while (localObject2 != null);
      localObject1 = paramSet.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (CastController.CastDevice)((Iterator)localObject1).next();
        this.mVisibleOrder.put(((CastController.CastDevice)localObject2).id, localObject2);
      }
      localObject2 = new QSDetailItems.Item[paramSet.size()];
      int i = 0;
      localIterator = this.mVisibleOrder.keySet().iterator();
      for (;;)
      {
        localObject1 = localObject2;
        if (!localIterator.hasNext()) {
          break;
        }
        localObject1 = (String)localIterator.next();
        localObject1 = (CastController.CastDevice)this.mVisibleOrder.get(localObject1);
        if (paramSet.contains(localObject1))
        {
          QSDetailItems.Item localItem = new QSDetailItems.Item();
          localItem.icon = 2130837785;
          localItem.line1 = CastTile.-wrap0(CastTile.this, (CastController.CastDevice)localObject1);
          if (((CastController.CastDevice)localObject1).state == 1) {
            localItem.line2 = CastTile.-get3(CastTile.this).getString(2131690308);
          }
          localItem.tag = localObject1;
          localObject2[i] = localItem;
          i += 1;
        }
      }
    }
    
    public View createDetailView(Context paramContext, View paramView, ViewGroup paramViewGroup)
    {
      this.mItems = QSDetailItems.convertOrInflate(paramContext, paramView, paramViewGroup);
      this.mItems.setTagSuffix("Cast");
      if (paramView == null)
      {
        if (CastTile.-get1()) {
          Log.d(CastTile.-get2(CastTile.this), "addOnAttachStateChangeListener");
        }
        this.mItems.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener()
        {
          public void onViewAttachedToWindow(View paramAnonymousView)
          {
            if (CastTile.-get1()) {
              Log.d(CastTile.-get2(CastTile.this), "onViewAttachedToWindow");
            }
          }
          
          public void onViewDetachedFromWindow(View paramAnonymousView)
          {
            if (CastTile.-get1()) {
              Log.d(CastTile.-get2(CastTile.this), "onViewDetachedFromWindow");
            }
            CastTile.CastDetailAdapter.-get0(CastTile.CastDetailAdapter.this).clear();
          }
        });
      }
      this.mItems.setEmptyState(2130837784, 2131690300);
      this.mItems.setCallback(this);
      updateItems(CastTile.-get4(CastTile.this).getCastDevices());
      CastTile.-get4(CastTile.this).setDiscovering(true);
      return this.mItems;
    }
    
    public int getMetricsCategory()
    {
      return 151;
    }
    
    public Intent getSettingsIntent()
    {
      return CastTile.-get0();
    }
    
    public CharSequence getTitle()
    {
      return CastTile.-get3(CastTile.this).getString(2131690296);
    }
    
    public Boolean getToggleState()
    {
      return null;
    }
    
    public void onDetailItemClick(QSDetailItems.Item paramItem)
    {
      if ((paramItem == null) || (paramItem.tag == null)) {
        return;
      }
      MetricsLogger.action(CastTile.-get3(CastTile.this), 157);
      paramItem = (CastController.CastDevice)paramItem.tag;
      CastTile.-get4(CastTile.this).startCasting(paramItem);
    }
    
    public void onDetailItemDisconnect(QSDetailItems.Item paramItem)
    {
      if ((paramItem == null) || (paramItem.tag == null)) {
        return;
      }
      MetricsLogger.action(CastTile.-get3(CastTile.this), 158);
      paramItem = (CastController.CastDevice)paramItem.tag;
      CastTile.-get4(CastTile.this).stopCasting(paramItem);
    }
    
    public void setToggleState(boolean paramBoolean) {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\CastTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */