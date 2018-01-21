package com.android.systemui.qs.tiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Prefs;
import com.android.systemui.SysUIToast;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.AnimationIcon;
import com.android.systemui.qs.QSTile.BooleanState;
import com.android.systemui.qs.QSTile.DetailAdapter;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.Icon;
import com.android.systemui.qs.QSTile.ResourceIcon;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.policy.ZenModeController.Callback;
import com.android.systemui.volume.ZenModePanel;
import com.android.systemui.volume.ZenModePanel.Callback;

public class DndTile
  extends QSTile<QSTile.BooleanState>
{
  private static final QSTile.Icon TOTAL_SILENCE = QSTile.ResourceIcon.get(2130837791);
  private static final Intent ZEN_PRIORITY_SETTINGS;
  private static final Intent ZEN_SETTINGS = new Intent("android.settings.ZEN_MODE_SETTINGS");
  private final ZenModeController mController;
  private final DndDetailAdapter mDetailAdapter;
  private final QSTile<QSTile.BooleanState>.AnimationIcon mDisable = new QSTile.AnimationIcon(this, 2130837709, 2130837789);
  private final QSTile<QSTile.BooleanState>.AnimationIcon mDisableTotalSilence = new QSTile.AnimationIcon(this, 2130837712, 2130837789);
  private boolean mListening;
  private final SharedPreferences.OnSharedPreferenceChangeListener mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener()
  {
    public void onSharedPreferenceChanged(SharedPreferences paramAnonymousSharedPreferences, String paramAnonymousString)
    {
      if (("DndTileCombinedIcon".equals(paramAnonymousString)) || ("DndTileVisible".equals(paramAnonymousString))) {
        DndTile.this.refreshState();
      }
    }
  };
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      boolean bool = paramAnonymousIntent.getBooleanExtra("visible", false);
      DndTile.setVisible(DndTile.-get3(DndTile.this), bool);
      DndTile.this.refreshState();
    }
  };
  private boolean mShowingDetail;
  private final ZenModeController.Callback mZenCallback = new ZenModeController.Callback()
  {
    public void onZenChanged(int paramAnonymousInt)
    {
      DndTile.-wrap0(DndTile.this, Integer.valueOf(paramAnonymousInt));
    }
  };
  private final ZenModePanel.Callback mZenModePanelCallback = new ZenModePanel.Callback()
  {
    public void onExpanded(boolean paramAnonymousBoolean) {}
    
    public void onInteraction() {}
    
    public void onPrioritySettings()
    {
      DndTile.-get5(DndTile.this).startActivityDismissingKeyguard(DndTile.-get1());
    }
  };
  
  static
  {
    ZEN_PRIORITY_SETTINGS = new Intent("android.settings.ZEN_MODE_PRIORITY_SETTINGS");
  }
  
  public DndTile(QSTile.Host paramHost)
  {
    super(paramHost);
    this.mController = paramHost.getZenModeController();
    this.mDetailAdapter = new DndDetailAdapter(null);
    this.mContext.registerReceiver(this.mReceiver, new IntentFilter("com.android.systemui.dndtile.SET_VISIBLE"));
  }
  
  public static boolean isCombinedIcon(Context paramContext)
  {
    return Prefs.getBoolean(paramContext, "DndTileCombinedIcon", false);
  }
  
  public static boolean isVisible(Context paramContext)
  {
    return Prefs.getBoolean(paramContext, "DndTileVisible", false);
  }
  
  public static void setCombinedIcon(Context paramContext, boolean paramBoolean)
  {
    Prefs.putBoolean(paramContext, "DndTileCombinedIcon", paramBoolean);
  }
  
  public static void setVisible(Context paramContext, boolean paramBoolean)
  {
    Prefs.putBoolean(paramContext, "DndTileVisible", paramBoolean);
  }
  
  protected String composeChangeAnnouncement()
  {
    if (((QSTile.BooleanState)this.mState).value) {
      return this.mContext.getString(2131690207);
    }
    return this.mContext.getString(2131690206);
  }
  
  public QSTile.DetailAdapter getDetailAdapter()
  {
    return this.mDetailAdapter;
  }
  
  public Intent getLongClickIntent()
  {
    return ZEN_SETTINGS;
  }
  
  public int getMetricsCategory()
  {
    return 118;
  }
  
  public CharSequence getTileLabel()
  {
    return this.mContext.getString(2131690264);
  }
  
  public void handleClick()
  {
    if (this.mController.isVolumeRestricted())
    {
      this.mHost.collapsePanels();
      SysUIToast.makeText(this.mContext, this.mContext.getString(17040715), 1).show();
      return;
    }
    Context localContext = this.mContext;
    int i = getMetricsCategory();
    if (((QSTile.BooleanState)this.mState).value) {}
    for (boolean bool = false;; bool = true)
    {
      MetricsLogger.action(localContext, i, bool);
      if (!((QSTile.BooleanState)this.mState).value) {
        break;
      }
      this.mController.setZen(0, null, this.TAG);
      return;
    }
    showDetail(true);
    i = Prefs.getInt(this.mContext, "DndFavoriteZen", 3);
    this.mController.setZen(i, null, this.TAG);
  }
  
  protected void handleUpdateState(QSTile.BooleanState paramBooleanState, Object paramObject)
  {
    int i;
    boolean bool;
    label22:
    int j;
    if ((paramObject instanceof Integer))
    {
      i = ((Integer)paramObject).intValue();
      if (i == 0) {
        break label184;
      }
      bool = true;
      if (paramBooleanState.value == bool) {
        break label190;
      }
      j = 1;
      label34:
      paramBooleanState.value = bool;
      checkIfRestrictionEnforcedByAdminOnly(paramBooleanState, "no_adjust_volume");
      switch (i)
      {
      default: 
        if (TOTAL_SILENCE.equals(paramBooleanState.icon))
        {
          paramObject = this.mDisableTotalSilence;
          label94:
          paramBooleanState.icon = ((QSTile.Icon)paramObject);
          paramBooleanState.label = this.mContext.getString(2131690264);
          paramBooleanState.contentDescription = this.mContext.getString(2131690204);
          label126:
          if ((this.mShowingDetail) && (!paramBooleanState.value)) {
            break label324;
          }
        }
        break;
      }
    }
    for (;;)
    {
      if (j != 0) {
        fireToggleStateChanged(paramBooleanState.value);
      }
      paramObject = Switch.class.getName();
      paramBooleanState.expandedAccessibilityClassName = ((String)paramObject);
      paramBooleanState.minimalAccessibilityClassName = ((String)paramObject);
      return;
      i = this.mController.getZen();
      break;
      label184:
      bool = false;
      break label22;
      label190:
      j = 0;
      break label34;
      paramBooleanState.icon = QSTile.ResourceIcon.get(2130837790);
      paramBooleanState.label = this.mContext.getString(2131690265);
      paramBooleanState.contentDescription = this.mContext.getString(2131690201);
      break label126;
      paramBooleanState.icon = TOTAL_SILENCE;
      paramBooleanState.label = this.mContext.getString(2131690267);
      paramBooleanState.contentDescription = this.mContext.getString(2131690202);
      break label126;
      paramBooleanState.icon = QSTile.ResourceIcon.get(2130837790);
      paramBooleanState.label = this.mContext.getString(2131690266);
      paramBooleanState.contentDescription = this.mContext.getString(2131690203);
      break label126;
      paramObject = this.mDisable;
      break label94;
      label324:
      showDetail(false);
    }
  }
  
  public boolean isAvailable()
  {
    return isVisible(this.mContext);
  }
  
  public QSTile.BooleanState newTileState()
  {
    return new QSTile.BooleanState();
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (this.mListening == paramBoolean) {
      return;
    }
    this.mListening = paramBoolean;
    if (this.mListening)
    {
      this.mController.addCallback(this.mZenCallback);
      Prefs.registerListener(this.mContext, this.mPrefListener);
      return;
    }
    this.mController.removeCallback(this.mZenCallback);
    Prefs.unregisterListener(this.mContext, this.mPrefListener);
  }
  
  private final class DndDetailAdapter
    implements QSTile.DetailAdapter, View.OnAttachStateChangeListener
  {
    private DndDetailAdapter() {}
    
    public View createDetailView(Context paramContext, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView != null) {}
      for (paramContext = (ZenModePanel)paramView;; paramContext = (ZenModePanel)LayoutInflater.from(paramContext).inflate(2130968849, paramViewGroup, false))
      {
        if (paramView == null)
        {
          paramContext.init(DndTile.-get4(DndTile.this));
          paramContext.addOnAttachStateChangeListener(this);
          paramContext.setCallback(DndTile.-get7(DndTile.this));
        }
        return paramContext;
      }
    }
    
    public int getMetricsCategory()
    {
      return 149;
    }
    
    public Intent getSettingsIntent()
    {
      return DndTile.-get2();
    }
    
    public CharSequence getTitle()
    {
      return DndTile.-get3(DndTile.this).getString(2131690264);
    }
    
    public Boolean getToggleState()
    {
      return Boolean.valueOf(((QSTile.BooleanState)DndTile.-get6(DndTile.this)).value);
    }
    
    public void onViewAttachedToWindow(View paramView)
    {
      DndTile.-set0(DndTile.this, true);
    }
    
    public void onViewDetachedFromWindow(View paramView)
    {
      DndTile.-set0(DndTile.this, false);
    }
    
    public void setToggleState(boolean paramBoolean)
    {
      MetricsLogger.action(DndTile.-get3(DndTile.this), 166, paramBoolean);
      if (!paramBoolean)
      {
        DndTile.-get4(DndTile.this).setZen(0, null, DndTile.-get0(DndTile.this));
        DndTile.this.showDetail(false);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\DndTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */