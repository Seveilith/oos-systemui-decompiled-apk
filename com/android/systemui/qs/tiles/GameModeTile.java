package com.android.systemui.qs.tiles;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings.System;
import android.util.Log;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.BooleanState;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.ResourceIcon;
import com.android.systemui.qs.SystemSetting;

public class GameModeTile
  extends QSTile<QSTile.BooleanState>
{
  private boolean mIsListening;
  private SystemSetting mModeSetting = new SystemSetting(this.mContext, this.mHandler, "game_mode_status", true)
  {
    protected void handleValueChanged(int paramAnonymousInt, boolean paramAnonymousBoolean)
    {
      Log.d(GameModeTile.-get0(GameModeTile.this), "handleValueChanged:GAME_MODE_STATUS=" + paramAnonymousInt);
      GameModeTile.this.refreshState();
    }
  };
  
  public GameModeTile(QSTile.Host paramHost)
  {
    super(paramHost);
  }
  
  private boolean isEnabled()
  {
    boolean bool = false;
    if (this.mModeSetting.getValue() != 0) {
      bool = true;
    }
    return bool;
  }
  
  private void setEnabled(boolean paramBoolean)
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    if (paramBoolean) {}
    for (String str = "force-on";; str = "force-off")
    {
      Settings.System.putStringForUser(localContentResolver, "game_mode_status_manual", str, -2);
      return;
    }
  }
  
  public Intent getLongClickIntent()
  {
    return new Intent("android.settings.OP_GAMING_MODE_SETTINGS");
  }
  
  public int getMetricsCategory()
  {
    return 491;
  }
  
  public CharSequence getTileLabel()
  {
    return this.mContext.getString(2131690005);
  }
  
  protected void handleClick()
  {
    if (((QSTile.BooleanState)this.mState).value) {}
    for (boolean bool = false;; bool = true)
    {
      MetricsLogger.action(this.mContext, getMetricsCategory(), bool);
      setEnabled(bool);
      return;
    }
  }
  
  protected void handleUpdateState(QSTile.BooleanState paramBooleanState, Object paramObject)
  {
    paramBooleanState.value = isEnabled();
    Log.d(this.TAG, "handleUpdateState:state.value=" + paramBooleanState.value);
    paramBooleanState.label = this.mContext.getString(2131690005);
    if (paramBooleanState.value)
    {
      i = 2130837795;
      paramBooleanState.icon = QSTile.ResourceIcon.get(i);
      paramObject = this.mContext;
      if (!paramBooleanState.value) {
        break label116;
      }
    }
    label116:
    for (int i = 2131690006;; i = 2131690007)
    {
      paramBooleanState.contentDescription = ((Context)paramObject).getString(i);
      paramObject = Switch.class.getName();
      paramBooleanState.expandedAccessibilityClassName = ((String)paramObject);
      paramBooleanState.minimalAccessibilityClassName = ((String)paramObject);
      return;
      i = 2130837794;
      break;
    }
  }
  
  public boolean isAvailable()
  {
    return true;
  }
  
  public QSTile.BooleanState newTileState()
  {
    return new QSTile.BooleanState();
  }
  
  protected void setListening(boolean paramBoolean)
  {
    this.mIsListening = paramBoolean;
    this.mModeSetting.setListening(paramBoolean);
    if (paramBoolean) {
      refreshState();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\GameModeTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */