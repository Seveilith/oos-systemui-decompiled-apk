package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.AnimationIcon;
import com.android.systemui.qs.QSTile.BooleanState;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.Icon;
import com.android.systemui.qs.SecureSetting;

public class ColorInversionTile
  extends QSTile<QSTile.BooleanState>
{
  private final QSTile<QSTile.BooleanState>.AnimationIcon mDisable = new QSTile.AnimationIcon(this, 2130837735, 2130837736);
  private final QSTile<QSTile.BooleanState>.AnimationIcon mEnable = new QSTile.AnimationIcon(this, 2130837737, 2130837734);
  private final SecureSetting mSetting = new SecureSetting(this.mContext, this.mHandler, "accessibility_display_inversion_enabled")
  {
    protected void handleValueChanged(int paramAnonymousInt, boolean paramAnonymousBoolean)
    {
      ColorInversionTile.-wrap0(ColorInversionTile.this, Integer.valueOf(paramAnonymousInt));
    }
  };
  
  public ColorInversionTile(QSTile.Host paramHost)
  {
    super(paramHost);
  }
  
  protected String composeChangeAnnouncement()
  {
    if (((QSTile.BooleanState)this.mState).value) {
      return this.mContext.getString(2131690229);
    }
    return this.mContext.getString(2131690228);
  }
  
  public Intent getLongClickIntent()
  {
    return new Intent("android.settings.ACCESSIBILITY_SETTINGS");
  }
  
  public int getMetricsCategory()
  {
    return 116;
  }
  
  public CharSequence getTileLabel()
  {
    return this.mContext.getString(2131690303);
  }
  
  protected void handleClick()
  {
    int i = 0;
    Object localObject = this.mContext;
    int j = getMetricsCategory();
    boolean bool;
    if (((QSTile.BooleanState)this.mState).value)
    {
      bool = false;
      MetricsLogger.action((Context)localObject, j, bool);
      localObject = this.mSetting;
      if (!((QSTile.BooleanState)this.mState).value) {
        break label66;
      }
    }
    for (;;)
    {
      ((SecureSetting)localObject).setValue(i);
      return;
      bool = true;
      break;
      label66:
      i = 1;
    }
  }
  
  protected void handleDestroy()
  {
    super.handleDestroy();
    this.mSetting.setListening(false);
  }
  
  protected void handleUpdateState(QSTile.BooleanState paramBooleanState, Object paramObject)
  {
    boolean bool = false;
    int i;
    if ((paramObject instanceof Integer))
    {
      i = ((Integer)paramObject).intValue();
      if (i != 0) {
        bool = true;
      }
      paramBooleanState.value = bool;
      paramBooleanState.label = this.mContext.getString(2131690303);
      if (!bool) {
        break label95;
      }
    }
    label95:
    for (paramObject = this.mEnable;; paramObject = this.mDisable)
    {
      paramBooleanState.icon = ((QSTile.Icon)paramObject);
      paramObject = Switch.class.getName();
      paramBooleanState.expandedAccessibilityClassName = ((String)paramObject);
      paramBooleanState.minimalAccessibilityClassName = ((String)paramObject);
      paramBooleanState.contentDescription = paramBooleanState.label;
      return;
      i = this.mSetting.getValue();
      break;
    }
  }
  
  protected void handleUserSwitch(int paramInt)
  {
    this.mSetting.setUserId(paramInt);
    handleRefreshState(Integer.valueOf(this.mSetting.getValue()));
  }
  
  public QSTile.BooleanState newTileState()
  {
    return new QSTile.BooleanState();
  }
  
  public void setListening(boolean paramBoolean)
  {
    this.mSetting.setListening(paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\ColorInversionTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */