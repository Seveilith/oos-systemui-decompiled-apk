package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.AnimationIcon;
import com.android.systemui.qs.QSTile.BooleanState;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.KeyguardMonitor.Callback;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.LocationController.LocationSettingsChangeCallback;

public class LocationTile
  extends QSTile<QSTile.BooleanState>
{
  private final Callback mCallback = new Callback(null);
  private final LocationController mController;
  private final QSTile<QSTile.BooleanState>.AnimationIcon mDisable = new QSTile.AnimationIcon(this, 2130837876, 2130837877);
  private final QSTile<QSTile.BooleanState>.AnimationIcon mEnable = new QSTile.AnimationIcon(this, 2130837878, 2130837875);
  private final KeyguardMonitor mKeyguard;
  
  public LocationTile(QSTile.Host paramHost)
  {
    super(paramHost);
    this.mController = paramHost.getLocationController();
    this.mKeyguard = paramHost.getKeyguardMonitor();
  }
  
  protected String composeChangeAnnouncement()
  {
    if (((QSTile.BooleanState)this.mState).value) {
      return this.mContext.getString(2131690218);
    }
    return this.mContext.getString(2131690217);
  }
  
  public Intent getLongClickIntent()
  {
    return new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
  }
  
  public int getMetricsCategory()
  {
    return 122;
  }
  
  public CharSequence getTileLabel()
  {
    return this.mContext.getString(2131690280);
  }
  
  protected void handleClick()
  {
    boolean bool2 = false;
    if ((this.mKeyguard.isSecure()) && (this.mKeyguard.isShowing()))
    {
      this.mHost.startRunnableDismissingKeyguard(new Runnable()
      {
        public void run()
        {
          boolean bool2 = false;
          boolean bool3 = Boolean.valueOf(((QSTile.BooleanState)LocationTile.-get2(LocationTile.this)).value).booleanValue();
          Object localObject = LocationTile.-get0(LocationTile.this);
          int i = LocationTile.this.getMetricsCategory();
          if (bool3)
          {
            bool1 = false;
            MetricsLogger.action((Context)localObject, i, bool1);
            localObject = LocationTile.-get1(LocationTile.this);
            if (!bool3) {
              break label85;
            }
          }
          label85:
          for (boolean bool1 = bool2;; bool1 = true)
          {
            ((LocationController)localObject).setLocationEnabled(bool1);
            return;
            bool1 = true;
            break;
          }
        }
      });
      return;
    }
    boolean bool3 = Boolean.valueOf(((QSTile.BooleanState)this.mState).value).booleanValue();
    Object localObject = this.mContext;
    int i = getMetricsCategory();
    if (bool3)
    {
      bool1 = false;
      MetricsLogger.action((Context)localObject, i, bool1);
      localObject = this.mController;
      if (!bool3) {
        break label111;
      }
    }
    label111:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      ((LocationController)localObject).setLocationEnabled(bool1);
      return;
      bool1 = true;
      break;
    }
  }
  
  protected void handleUpdateState(QSTile.BooleanState paramBooleanState, Object paramObject)
  {
    boolean bool = this.mController.isLocationEnabled();
    paramBooleanState.value = bool;
    checkIfRestrictionEnforcedByAdminOnly(paramBooleanState, "no_share_location");
    if (bool)
    {
      paramBooleanState.icon = this.mEnable;
      paramBooleanState.label = this.mContext.getString(2131690280);
    }
    for (paramBooleanState.contentDescription = this.mContext.getString(2131690216);; paramBooleanState.contentDescription = this.mContext.getString(2131690215))
    {
      paramObject = Switch.class.getName();
      paramBooleanState.expandedAccessibilityClassName = ((String)paramObject);
      paramBooleanState.minimalAccessibilityClassName = ((String)paramObject);
      return;
      paramBooleanState.icon = this.mDisable;
      paramBooleanState.label = this.mContext.getString(2131690280);
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
      this.mController.addSettingsChangedCallback(this.mCallback);
      this.mKeyguard.addCallback(this.mCallback);
      return;
    }
    this.mController.removeSettingsChangedCallback(this.mCallback);
    this.mKeyguard.removeCallback(this.mCallback);
  }
  
  private final class Callback
    implements LocationController.LocationSettingsChangeCallback, KeyguardMonitor.Callback
  {
    private Callback() {}
    
    public void onKeyguardChanged()
    {
      LocationTile.this.refreshState();
    }
    
    public void onLocationSettingsChanged(boolean paramBoolean)
    {
      LocationTile.this.refreshState();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\LocationTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */