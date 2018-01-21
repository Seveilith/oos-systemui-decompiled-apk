package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.android.internal.util.AsyncChannel;
import com.android.settingslib.wifi.WifiStatusTracker;
import java.util.Objects;

public class WifiSignalController
  extends SignalController<WifiState, SignalController.IconGroup>
{
  private final boolean mHasMobileData;
  private final AsyncChannel mWifiChannel;
  private final WifiManager mWifiManager;
  private final WifiStatusTracker mWifiTracker;
  
  public WifiSignalController(Context paramContext, boolean paramBoolean, CallbackHandler paramCallbackHandler, NetworkControllerImpl paramNetworkControllerImpl)
  {
    super("WifiSignalController", paramContext, 1, paramCallbackHandler, paramNetworkControllerImpl);
    this.mWifiManager = ((WifiManager)paramContext.getSystemService("wifi"));
    this.mWifiTracker = new WifiStatusTracker(this.mWifiManager);
    this.mHasMobileData = paramBoolean;
    paramCallbackHandler = new WifiHandler(null);
    this.mWifiChannel = new AsyncChannel();
    paramNetworkControllerImpl = this.mWifiManager.getWifiServiceMessenger();
    if (paramNetworkControllerImpl != null) {
      this.mWifiChannel.connect(paramContext, paramCallbackHandler, paramNetworkControllerImpl);
    }
    paramContext = (WifiState)this.mCurrentState;
    paramCallbackHandler = new SignalController.IconGroup("Wi-Fi Icons", WifiIcons.WIFI_SIGNAL_STRENGTH, WifiIcons.QS_WIFI_SIGNAL_STRENGTH, AccessibilityContentDescriptions.WIFI_CONNECTION_STRENGTH, 2130839017, 2130837860, 2130839017, 2130837860, 2131689576);
    ((WifiState)this.mLastState).iconGroup = paramCallbackHandler;
    paramContext.iconGroup = paramCallbackHandler;
  }
  
  protected WifiState cleanState()
  {
    return new WifiState();
  }
  
  public void handleBroadcast(Intent paramIntent)
  {
    String str = paramIntent.getAction();
    this.mWifiTracker.handleBroadcast(paramIntent);
    ((WifiState)this.mCurrentState).enabled = this.mWifiManager.isWifiEnabled();
    ((WifiState)this.mCurrentState).connected = this.mWifiTracker.connected;
    ((WifiState)this.mCurrentState).ssid = this.mWifiTracker.ssid;
    ((WifiState)this.mCurrentState).rssi = this.mWifiTracker.rssi;
    ((WifiState)this.mCurrentState).level = this.mWifiTracker.level;
    if ("android.net.wifi.WIFI_STATE_CHANGED".equals(str)) {
      Log.d(this.mTag, "handleBroadcast:action=" + str + ", WifiManager.EXTRA_WIFI_STATE=" + paramIntent.getIntExtra("wifi_state", 4) + ", mWifiTracker.enabled=" + this.mWifiTracker.enabled);
    }
    notifyListenersIfNecessary();
  }
  
  public void notifyListeners(NetworkController.SignalCallback paramSignalCallback)
  {
    boolean bool2 = false;
    boolean bool1 = this.mContext.getResources().getBoolean(2131558424);
    String str;
    label65:
    int i;
    label84:
    Object localObject2;
    Object localObject1;
    boolean bool3;
    if (((WifiState)this.mCurrentState).enabled) {
      if ((!((WifiState)this.mCurrentState).connected) && (this.mHasMobileData))
      {
        if (!bool1) {
          break label257;
        }
        str = ((WifiState)this.mCurrentState).ssid;
        if ((!bool1) || (((WifiState)this.mCurrentState).ssid == null)) {
          break label263;
        }
        i = 1;
        localObject2 = getStringIfExists(getContentDescription());
        localObject1 = localObject2;
        if (((WifiState)this.mCurrentState).inetCondition == 0) {
          localObject1 = (String)localObject2 + "," + this.mContext.getString(2131690635);
        }
        localObject2 = new NetworkController.IconState(bool1, getCurrentIconId(), (String)localObject1);
        localObject1 = new NetworkController.IconState(((WifiState)this.mCurrentState).connected, getQsCurrentIconId(), (String)localObject1);
        bool3 = ((WifiState)this.mCurrentState).enabled;
        if (i == 0) {
          break label268;
        }
      }
    }
    label257:
    label263:
    label268:
    for (bool1 = ((WifiState)this.mCurrentState).activityIn;; bool1 = false)
    {
      if (i != 0) {
        bool2 = ((WifiState)this.mCurrentState).activityOut;
      }
      paramSignalCallback.setWifiIndicators(bool3, (NetworkController.IconState)localObject2, (NetworkController.IconState)localObject1, bool1, bool2, str);
      return;
      bool1 = true;
      break;
      bool1 = false;
      break;
      str = null;
      break label65;
      i = 0;
      break label84;
    }
  }
  
  void setActivity(int paramInt)
  {
    boolean bool2 = true;
    WifiState localWifiState = (WifiState)this.mCurrentState;
    if (paramInt != 3)
    {
      if (paramInt != 1) {
        break label68;
      }
      bool1 = true;
      localWifiState.activityIn = bool1;
      localWifiState = (WifiState)this.mCurrentState;
      bool1 = bool2;
      if (paramInt != 3) {
        if (paramInt != 2) {
          break label73;
        }
      }
    }
    label68:
    label73:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      localWifiState.activityOut = bool1;
      notifyListenersIfNecessary();
      return;
      bool1 = true;
      break;
      bool1 = false;
      break;
    }
  }
  
  private class WifiHandler
    extends Handler
  {
    private WifiHandler() {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 69632: 
        if (paramMessage.arg1 == 0)
        {
          WifiSignalController.-get0(WifiSignalController.this).sendMessage(Message.obtain(this, 69633));
          return;
        }
        Log.e(WifiSignalController.this.mTag, "Failed to connect to wifi");
        return;
      }
      WifiSignalController.this.setActivity(paramMessage.arg1);
    }
  }
  
  static class WifiState
    extends SignalController.State
  {
    String ssid;
    
    public void copyFrom(SignalController.State paramState)
    {
      super.copyFrom(paramState);
      this.ssid = ((WifiState)paramState).ssid;
    }
    
    public boolean equals(Object paramObject)
    {
      if (super.equals(paramObject)) {
        return Objects.equals(((WifiState)paramObject).ssid, this.ssid);
      }
      return false;
    }
    
    protected void toString(StringBuilder paramStringBuilder)
    {
      super.toString(paramStringBuilder);
      paramStringBuilder.append(',').append("ssid=").append(this.ssid);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\WifiSignalController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */