package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.BooleanState;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.ResourceIcon;
import com.android.systemui.statusbar.phone.QSTileHost;
import com.android.systemui.statusbar.policy.SecurityController;
import com.android.systemui.statusbar.policy.SecurityController.SecurityControllerCallback;

public class VPNTile
  extends QSTile<QSTile.BooleanState>
{
  private static final Intent VPN_SETTINGS = new Intent("android.net.vpn.SETTINGS");
  private final Callback mCallback = new Callback(null);
  private SecurityController mSecurityController;
  
  public VPNTile(QSTileHost paramQSTileHost)
  {
    super(paramQSTileHost);
    this.mSecurityController = paramQSTileHost.getSecurityController();
  }
  
  public Intent getLongClickIntent()
  {
    return VPN_SETTINGS;
  }
  
  public int getMetricsCategory()
  {
    return 415;
  }
  
  public CharSequence getTileLabel()
  {
    return this.mContext.getString(2131690415);
  }
  
  protected void handleClick()
  {
    this.mHost.startActivityDismissingKeyguard(new Intent(VPN_SETTINGS));
  }
  
  protected void handleUpdateState(QSTile.BooleanState paramBooleanState, Object paramObject)
  {
    paramBooleanState.value = this.mSecurityController.isVpnEnabled();
    paramBooleanState.icon = QSTile.ResourceIcon.get(2130837845);
    if (paramBooleanState.value) {}
    for (paramBooleanState.icon = QSTile.ResourceIcon.get(2130837845);; paramBooleanState.icon = QSTile.ResourceIcon.get(2130837846))
    {
      paramBooleanState.label = this.mContext.getString(2131690415);
      paramBooleanState.contentDescription = this.mContext.getString(2131690415);
      return;
    }
  }
  
  public QSTile.BooleanState newTileState()
  {
    return new QSTile.BooleanState();
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mSecurityController.addCallback(this.mCallback);
      return;
    }
    this.mSecurityController.removeCallback(this.mCallback);
  }
  
  private class Callback
    implements SecurityController.SecurityControllerCallback
  {
    private Callback() {}
    
    public void onStateChanged()
    {
      VPNTile.this.refreshState();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\VPNTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */