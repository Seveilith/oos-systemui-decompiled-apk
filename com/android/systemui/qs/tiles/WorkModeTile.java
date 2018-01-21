package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.AnimationIcon;
import com.android.systemui.qs.QSTile.BooleanState;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.statusbar.phone.ManagedProfileController;
import com.android.systemui.statusbar.phone.ManagedProfileController.Callback;

public class WorkModeTile
  extends QSTile<QSTile.BooleanState>
  implements ManagedProfileController.Callback
{
  private final QSTile<QSTile.BooleanState>.AnimationIcon mDisable = new QSTile.AnimationIcon(this, 2130837880, 2130837881);
  private final QSTile<QSTile.BooleanState>.AnimationIcon mEnable = new QSTile.AnimationIcon(this, 2130837882, 2130837879);
  private final ManagedProfileController mProfileController;
  
  public WorkModeTile(QSTile.Host paramHost)
  {
    super(paramHost);
    this.mProfileController = paramHost.getManagedProfileController();
  }
  
  protected String composeChangeAnnouncement()
  {
    if (((QSTile.BooleanState)this.mState).value) {
      return this.mContext.getString(2131690236);
    }
    return this.mContext.getString(2131690235);
  }
  
  public Intent getLongClickIntent()
  {
    return new Intent("android.settings.SYNC_SETTINGS");
  }
  
  public int getMetricsCategory()
  {
    return 257;
  }
  
  public CharSequence getTileLabel()
  {
    return this.mContext.getString(2131690320);
  }
  
  public void handleClick()
  {
    boolean bool2 = false;
    Object localObject = this.mContext;
    int i = getMetricsCategory();
    if (((QSTile.BooleanState)this.mState).value)
    {
      bool1 = false;
      MetricsLogger.action((Context)localObject, i, bool1);
      localObject = this.mProfileController;
      if (!((QSTile.BooleanState)this.mState).value) {
        break label68;
      }
    }
    label68:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      ((ManagedProfileController)localObject).setWorkModeEnabled(bool1);
      return;
      bool1 = true;
      break;
    }
  }
  
  protected void handleUpdateState(QSTile.BooleanState paramBooleanState, Object paramObject)
  {
    if ((paramObject instanceof Boolean))
    {
      paramBooleanState.value = ((Boolean)paramObject).booleanValue();
      paramBooleanState.label = this.mContext.getString(2131690320);
      if (!paramBooleanState.value) {
        break label90;
      }
      paramBooleanState.icon = this.mEnable;
    }
    for (paramBooleanState.contentDescription = this.mContext.getString(2131690234);; paramBooleanState.contentDescription = this.mContext.getString(2131690233))
    {
      paramObject = Switch.class.getName();
      paramBooleanState.expandedAccessibilityClassName = ((String)paramObject);
      paramBooleanState.minimalAccessibilityClassName = ((String)paramObject);
      return;
      paramBooleanState.value = this.mProfileController.isWorkModeEnabled();
      break;
      label90:
      paramBooleanState.icon = this.mDisable;
    }
  }
  
  public boolean isAvailable()
  {
    return this.mProfileController.hasActiveProfile();
  }
  
  public QSTile.BooleanState newTileState()
  {
    return new QSTile.BooleanState();
  }
  
  public void onManagedProfileChanged()
  {
    refreshState(Boolean.valueOf(this.mProfileController.isWorkModeEnabled()));
  }
  
  public void onManagedProfileRemoved()
  {
    this.mHost.removeTile(getTileSpec());
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mProfileController.addCallback(this);
      return;
    }
    this.mProfileController.removeCallback(this);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\WorkModeTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */