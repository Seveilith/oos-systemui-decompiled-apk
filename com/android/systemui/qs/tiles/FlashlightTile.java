package com.android.systemui.qs.tiles;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.qs.QSIconView;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.AnimationIcon;
import com.android.systemui.qs.QSTile.BooleanState;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.Icon;
import com.android.systemui.statusbar.policy.FlashlightController;
import com.android.systemui.statusbar.policy.FlashlightController.FlashlightListener;

public class FlashlightTile
  extends QSTile<QSTile.BooleanState>
  implements FlashlightController.FlashlightListener
{
  private final QSTile<QSTile.BooleanState>.AnimationIcon mDisable = new QSTile.AnimationIcon(this, 2130837872, 2130837873);
  private final QSTile<QSTile.BooleanState>.AnimationIcon mEnable = new QSTile.AnimationIcon(this, 2130837874, 2130837871);
  private final FlashlightController mFlashlightController;
  
  public FlashlightTile(QSTile.Host paramHost)
  {
    super(paramHost);
    this.mFlashlightController = paramHost.getFlashlightController();
    this.mFlashlightController.addListener(this);
  }
  
  protected String composeChangeAnnouncement()
  {
    if (((QSTile.BooleanState)this.mState).value) {
      return this.mContext.getString(2131690227);
    }
    return this.mContext.getString(2131690226);
  }
  
  public Intent getLongClickIntent()
  {
    return new Intent("android.media.action.STILL_IMAGE_CAMERA");
  }
  
  public int getMetricsCategory()
  {
    return 119;
  }
  
  public CharSequence getTileLabel()
  {
    return this.mContext.getString(2131690312);
  }
  
  protected void handleClick()
  {
    if (ActivityManager.isUserAMonkey()) {
      return;
    }
    Context localContext = this.mContext;
    int i = getMetricsCategory();
    if (((QSTile.BooleanState)this.mState).value)
    {
      bool = false;
      MetricsLogger.action(localContext, i, bool);
      if (!((QSTile.BooleanState)this.mState).value) {
        break label75;
      }
    }
    label75:
    for (boolean bool = false;; bool = true)
    {
      refreshState(Boolean.valueOf(bool));
      this.mFlashlightController.setFlashlight(bool);
      return;
      bool = true;
      break;
    }
  }
  
  protected void handleDestroy()
  {
    super.handleDestroy();
    this.mFlashlightController.removeListener(this);
  }
  
  protected void handleLongClick()
  {
    handleClick();
  }
  
  protected void handleUpdateState(QSTile.BooleanState paramBooleanState, Object paramObject)
  {
    paramBooleanState.label = this.mHost.getContext().getString(2131690312);
    if (!this.mFlashlightController.isAvailable()) {
      paramBooleanState.label = new SpannableStringBuilder().append(paramBooleanState.label, new ForegroundColorSpan(QSIconView.sCustomDisableIconColor), 18);
    }
    if ((paramObject instanceof Boolean))
    {
      boolean bool = ((Boolean)paramObject).booleanValue();
      if (bool == paramBooleanState.value) {
        return;
      }
      paramBooleanState.value = bool;
      if (this.mFlashlightController.isAvailable()) {
        break label163;
      }
      paramBooleanState.value = false;
      paramBooleanState.contentDescription = this.mContext.getString(2131690224);
      label115:
      if (!paramBooleanState.value) {
        break label179;
      }
    }
    label163:
    label179:
    for (paramObject = this.mEnable;; paramObject = this.mDisable)
    {
      paramBooleanState.icon = ((QSTile.Icon)paramObject);
      paramObject = Switch.class.getName();
      paramBooleanState.expandedAccessibilityClassName = ((String)paramObject);
      paramBooleanState.minimalAccessibilityClassName = ((String)paramObject);
      return;
      paramBooleanState.value = this.mFlashlightController.isEnabled();
      break;
      paramBooleanState.contentDescription = this.mContext.getString(2131690312);
      break label115;
    }
  }
  
  protected void handleUserSwitch(int paramInt) {}
  
  public boolean isAvailable()
  {
    return this.mFlashlightController.hasFlashlight();
  }
  
  public QSTile.BooleanState newTileState()
  {
    return new QSTile.BooleanState();
  }
  
  public void onFlashlightAvailabilityChanged(boolean paramBoolean)
  {
    refreshState();
  }
  
  public void onFlashlightChanged(boolean paramBoolean)
  {
    refreshState(Boolean.valueOf(paramBoolean));
  }
  
  public void onFlashlightError()
  {
    refreshState(Boolean.valueOf(false));
  }
  
  public void setListening(boolean paramBoolean) {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\FlashlightTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */