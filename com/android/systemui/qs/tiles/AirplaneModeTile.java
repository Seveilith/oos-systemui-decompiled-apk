package com.android.systemui.qs.tiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.qs.GlobalSetting;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.AnimationIcon;
import com.android.systemui.qs.QSTile.BooleanState;
import com.android.systemui.qs.QSTile.Host;

public class AirplaneModeTile
  extends QSTile<QSTile.BooleanState>
{
  private final QSTile<QSTile.BooleanState>.AnimationIcon mDisable = new QSTile.AnimationIcon(this, 2130837868, 2130837869);
  private final QSTile<QSTile.BooleanState>.AnimationIcon mEnable = new QSTile.AnimationIcon(this, 2130837870, 2130837867);
  private boolean mListening;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.intent.action.AIRPLANE_MODE".equals(paramAnonymousIntent.getAction())) {
        AirplaneModeTile.this.refreshState();
      }
    }
  };
  private final GlobalSetting mSetting = new GlobalSetting(this.mContext, this.mHandler, "airplane_mode_on")
  {
    protected void handleValueChanged(int paramAnonymousInt)
    {
      AirplaneModeTile.-wrap0(AirplaneModeTile.this, Integer.valueOf(paramAnonymousInt));
    }
  };
  
  public AirplaneModeTile(QSTile.Host paramHost)
  {
    super(paramHost);
  }
  
  private void setEnabled(boolean paramBoolean)
  {
    ((ConnectivityManager)this.mContext.getSystemService("connectivity")).setAirplaneMode(paramBoolean);
  }
  
  protected String composeChangeAnnouncement()
  {
    if (((QSTile.BooleanState)this.mState).value) {
      return this.mContext.getString(2131690200);
    }
    return this.mContext.getString(2131690199);
  }
  
  public Intent getLongClickIntent()
  {
    return new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
  }
  
  public int getMetricsCategory()
  {
    return 112;
  }
  
  public CharSequence getTileLabel()
  {
    return this.mContext.getString(2131690756);
  }
  
  public void handleClick()
  {
    boolean bool2 = false;
    Context localContext = this.mContext;
    int i = getMetricsCategory();
    if (((QSTile.BooleanState)this.mState).value)
    {
      bool1 = false;
      MetricsLogger.action(localContext, i, bool1);
      if (!((QSTile.BooleanState)this.mState).value) {
        break label61;
      }
    }
    label61:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      setEnabled(bool1);
      return;
      bool1 = true;
      break;
    }
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
      paramBooleanState.label = this.mContext.getString(2131690756);
      if (!bool) {
        break label93;
      }
    }
    label93:
    for (paramBooleanState.icon = this.mEnable;; paramBooleanState.icon = this.mDisable)
    {
      paramBooleanState.contentDescription = paramBooleanState.label;
      paramObject = Switch.class.getName();
      paramBooleanState.expandedAccessibilityClassName = ((String)paramObject);
      paramBooleanState.minimalAccessibilityClassName = ((String)paramObject);
      return;
      i = this.mSetting.getValue();
      break;
    }
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
    if (paramBoolean)
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.AIRPLANE_MODE");
      this.mContext.registerReceiver(this.mReceiver, localIntentFilter);
    }
    for (;;)
    {
      this.mSetting.setListening(paramBoolean);
      return;
      this.mContext.unregisterReceiver(this.mReceiver);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\AirplaneModeTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */