package com.android.systemui.qs.tiles;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Switch;
import com.android.internal.app.NightDisplayController;
import com.android.internal.app.NightDisplayController.Callback;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.BooleanState;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.ResourceIcon;

public class NightDisplayTile
  extends QSTile<QSTile.BooleanState>
  implements NightDisplayController.Callback
{
  private NightDisplayController mController = new NightDisplayController(this.mContext, ActivityManager.getCurrentUser());
  private boolean mIsListening;
  
  public NightDisplayTile(QSTile.Host paramHost)
  {
    super(paramHost);
  }
  
  public Intent getLongClickIntent()
  {
    return new Intent("android.settings.NIGHT_DISPLAY_SETTINGS");
  }
  
  public int getMetricsCategory()
  {
    return 491;
  }
  
  public CharSequence getTileLabel()
  {
    return this.mContext.getString(2131690018);
  }
  
  protected void handleClick()
  {
    if (((QSTile.BooleanState)this.mState).value) {}
    for (boolean bool = false;; bool = true)
    {
      MetricsLogger.action(this.mContext, getMetricsCategory(), bool);
      this.mController.setActivated(bool);
      return;
    }
  }
  
  protected void handleUpdateState(QSTile.BooleanState paramBooleanState, Object paramObject)
  {
    boolean bool = this.mController.isActivated();
    paramBooleanState.value = bool;
    paramBooleanState.label = this.mContext.getString(2131690018);
    if (bool)
    {
      i = 2130837759;
      paramBooleanState.icon = QSTile.ResourceIcon.get(i);
      paramObject = this.mContext;
      if (!bool) {
        break label89;
      }
    }
    label89:
    for (int i = 2131690019;; i = 2131690020)
    {
      paramBooleanState.contentDescription = ((Context)paramObject).getString(i);
      paramObject = Switch.class.getName();
      paramBooleanState.expandedAccessibilityClassName = ((String)paramObject);
      paramBooleanState.minimalAccessibilityClassName = ((String)paramObject);
      return;
      i = 2130837758;
      break;
    }
  }
  
  protected void handleUserSwitch(int paramInt)
  {
    if (this.mIsListening) {
      this.mController.setListener(null);
    }
    this.mController = new NightDisplayController(this.mContext, paramInt);
    if (this.mIsListening) {
      this.mController.setListener(this);
    }
    super.handleUserSwitch(paramInt);
  }
  
  public QSTile.BooleanState newTileState()
  {
    return new QSTile.BooleanState();
  }
  
  public void onActivated(boolean paramBoolean)
  {
    refreshState();
  }
  
  protected void setListening(boolean paramBoolean)
  {
    this.mIsListening = paramBoolean;
    if (paramBoolean)
    {
      this.mController.setListener(this);
      refreshState();
      return;
    }
    this.mController.setListener(null);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\NightDisplayTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */