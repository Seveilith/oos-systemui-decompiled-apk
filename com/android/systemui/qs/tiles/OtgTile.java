package com.android.systemui.qs.tiles;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.SystemProperties;
import android.provider.Settings.Global;
import android.util.Log;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.BooleanState;
import com.android.systemui.qs.QSTile.ResourceIcon;
import com.android.systemui.statusbar.phone.QSTileHost;
import java.lang.reflect.Method;

public class OtgTile
  extends QSTile<QSTile.BooleanState>
{
  private static final Intent OTG_SETTINGS = new Intent("oneplus.intent.action.OTG_SETTINGS");
  private BroadcastReceiver mOTGBReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("oneplus.intent.action.otg_auto_shutdown".equals(paramAnonymousIntent.getAction())) {
        OtgTile.this.refreshState();
      }
    }
  };
  private boolean mRegistered = false;
  UsbManager mUsbManager = (UsbManager)this.mContext.getSystemService("usb");
  
  public OtgTile(QSTileHost paramQSTileHost)
  {
    super(paramQSTileHost);
  }
  
  private boolean isOtgEnabled()
  {
    return SystemProperties.getBoolean("persist.sys.oem.otg_support", false);
  }
  
  private void setOtgEnabled(boolean paramBoolean)
  {
    int i = 1;
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    if (paramBoolean) {}
    for (;;)
    {
      Settings.Global.putInt(localContentResolver, "oneplus_otg_auto_disable", i);
      try
      {
        UsbManager.class.getMethod("setOtgEnabled", new Class[] { Boolean.TYPE }).invoke(this.mUsbManager, new Object[] { Boolean.valueOf(paramBoolean) });
        return;
      }
      catch (Exception localException)
      {
        Log.e(this.TAG, "Cannot setOtgEnabled", localException);
      }
      i = 0;
    }
  }
  
  public Intent getLongClickIntent()
  {
    return OTG_SETTINGS;
  }
  
  public int getMetricsCategory()
  {
    return 415;
  }
  
  public CharSequence getTileLabel()
  {
    return this.mContext.getString(2131689929);
  }
  
  protected void handleClick()
  {
    if (isOtgEnabled()) {}
    for (boolean bool = false;; bool = true)
    {
      setOtgEnabled(bool);
      refreshState();
      return;
    }
  }
  
  protected void handleUpdateState(QSTile.BooleanState paramBooleanState, Object paramObject)
  {
    paramBooleanState.value = isOtgEnabled();
    if (paramBooleanState.value) {}
    for (paramBooleanState.icon = QSTile.ResourceIcon.get(2130837805);; paramBooleanState.icon = QSTile.ResourceIcon.get(2130837804))
    {
      paramBooleanState.label = this.mContext.getString(2131689929);
      paramBooleanState.contentDescription = this.mContext.getString(2131689929);
      return;
    }
  }
  
  public QSTile.BooleanState newTileState()
  {
    return new QSTile.BooleanState();
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (paramBoolean) {
      if (!this.mRegistered)
      {
        localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("oneplus.intent.action.otg_auto_shutdown");
        this.mContext.registerReceiver(this.mOTGBReceiver, localIntentFilter);
        this.mRegistered = true;
      }
    }
    while (!this.mRegistered)
    {
      IntentFilter localIntentFilter;
      return;
    }
    this.mContext.unregisterReceiver(this.mOTGBReceiver);
    this.mRegistered = false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\OtgTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */