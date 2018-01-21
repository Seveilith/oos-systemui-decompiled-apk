package com.android.systemui.qs.tiles;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.qs.GlobalSetting;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.AirplaneBooleanState;
import com.android.systemui.qs.QSTile.AnimationIcon;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.Icon;
import com.android.systemui.qs.QSTile.ResourceIcon;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.HotspotController.Callback;

public class HotspotTile
  extends QSTile<QSTile.AirplaneBooleanState>
{
  static final Intent HOTSPOT_SETTINGS = new Intent().setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$TetherSettingsActivity"));
  private final GlobalSetting mAirplaneMode;
  private final Callback mCallback = new Callback(null);
  private final HotspotController mController;
  private final QSTile<QSTile.AirplaneBooleanState>.AnimationIcon mDisable = new QSTile.AnimationIcon(this, 2130837728, 2130837729);
  private final QSTile.Icon mDisableNoAnimation = QSTile.ResourceIcon.get(2130837729);
  private final QSTile<QSTile.AirplaneBooleanState>.AnimationIcon mEnable = new QSTile.AnimationIcon(this, 2130837730, 2130837727);
  private boolean mListening;
  private final QSTile.Icon mUnavailable = QSTile.ResourceIcon.get(2130837729);
  
  public HotspotTile(QSTile.Host paramHost)
  {
    super(paramHost);
    this.mController = paramHost.getHotspotController();
    this.mAirplaneMode = new GlobalSetting(this.mContext, this.mHandler, "airplane_mode_on")
    {
      protected void handleValueChanged(int paramAnonymousInt)
      {
        HotspotTile.this.refreshState();
      }
    };
  }
  
  protected String composeChangeAnnouncement()
  {
    if (((QSTile.AirplaneBooleanState)this.mState).value) {
      return this.mContext.getString(2131690231);
    }
    return this.mContext.getString(2131690230);
  }
  
  public Intent getLongClickIntent()
  {
    return HOTSPOT_SETTINGS;
  }
  
  public int getMetricsCategory()
  {
    return 120;
  }
  
  public CharSequence getTileLabel()
  {
    return this.mContext.getString(2131690310);
  }
  
  protected void handleClick()
  {
    boolean bool2 = false;
    boolean bool3 = Boolean.valueOf(((QSTile.AirplaneBooleanState)this.mState).value).booleanValue();
    if ((!bool3) && (this.mAirplaneMode.getValue() != 0)) {
      return;
    }
    Object localObject = this.mContext;
    int i = getMetricsCategory();
    if (bool3)
    {
      bool1 = false;
      MetricsLogger.action((Context)localObject, i, bool1);
      localObject = this.mController;
      if (!bool3) {
        break label88;
      }
    }
    label88:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      ((HotspotController)localObject).setHotspotEnabled(bool1);
      return;
      bool1 = true;
      break;
    }
  }
  
  protected void handleDestroy()
  {
    super.handleDestroy();
  }
  
  protected void handleUpdateState(QSTile.AirplaneBooleanState paramAirplaneBooleanState, Object paramObject)
  {
    paramAirplaneBooleanState.label = this.mContext.getString(2131690310);
    checkIfRestrictionEnforcedByAdminOnly(paramAirplaneBooleanState, "no_config_tethering");
    label50:
    boolean bool2;
    boolean bool1;
    if ((paramObject instanceof Boolean))
    {
      paramAirplaneBooleanState.value = ((Boolean)paramObject).booleanValue();
      if (!paramAirplaneBooleanState.value) {
        break label134;
      }
      paramObject = this.mEnable;
      paramAirplaneBooleanState.icon = ((QSTile.Icon)paramObject);
      bool2 = paramAirplaneBooleanState.isAirplaneMode;
      if (this.mAirplaneMode.getValue() == 0) {
        break label142;
      }
      bool1 = true;
      label73:
      paramAirplaneBooleanState.isAirplaneMode = bool1;
      if (!paramAirplaneBooleanState.isAirplaneMode) {
        break label147;
      }
      paramAirplaneBooleanState.icon = this.mUnavailable;
    }
    for (;;)
    {
      paramObject = Switch.class.getName();
      paramAirplaneBooleanState.expandedAccessibilityClassName = ((String)paramObject);
      paramAirplaneBooleanState.minimalAccessibilityClassName = ((String)paramObject);
      paramAirplaneBooleanState.contentDescription = paramAirplaneBooleanState.label;
      return;
      paramAirplaneBooleanState.value = this.mController.isHotspotEnabled();
      break;
      label134:
      paramObject = this.mDisable;
      break label50;
      label142:
      bool1 = false;
      break label73;
      label147:
      if (bool2) {
        paramAirplaneBooleanState.icon = this.mDisableNoAnimation;
      }
    }
  }
  
  public boolean isAvailable()
  {
    return this.mController.isHotspotSupported();
  }
  
  public QSTile.AirplaneBooleanState newTileState()
  {
    return new QSTile.AirplaneBooleanState();
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (this.mListening == paramBoolean) {
      return;
    }
    this.mListening = paramBoolean;
    if (paramBoolean)
    {
      this.mController.addCallback(this.mCallback);
      new IntentFilter().addAction("android.intent.action.AIRPLANE_MODE");
      refreshState();
    }
    for (;;)
    {
      this.mAirplaneMode.setListening(paramBoolean);
      return;
      this.mController.removeCallback(this.mCallback);
    }
  }
  
  private final class Callback
    implements HotspotController.Callback
  {
    private Callback() {}
    
    public void onHotspotChanged(boolean paramBoolean)
    {
      HotspotTile.-wrap0(HotspotTile.this, Boolean.valueOf(paramBoolean));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\HotspotTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */