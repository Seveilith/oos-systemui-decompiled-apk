package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.Intent;
import android.telephony.SubscriptionInfo;
import com.android.settingslib.net.DataUsageController;
import com.android.settingslib.wifi.AccessPoint;
import java.util.List;

public abstract interface NetworkController
{
  public abstract void addEmergencyListener(EmergencyListener paramEmergencyListener);
  
  public abstract void addSignalCallback(SignalCallback paramSignalCallback);
  
  public abstract AccessPointController getAccessPointController();
  
  public abstract String getCarrierName();
  
  public abstract DataSaverController getDataSaverController();
  
  public abstract DataUsageController getMobileDataController();
  
  public abstract boolean hasMobileDataFeature();
  
  public abstract boolean hasVoiceCallingFeature();
  
  public abstract void removeEmergencyListener(EmergencyListener paramEmergencyListener);
  
  public abstract void removeSignalCallback(SignalCallback paramSignalCallback);
  
  public abstract void setWifiEnabled(boolean paramBoolean);
  
  public static abstract interface AccessPointController
  {
    public abstract void addAccessPointCallback(AccessPointCallback paramAccessPointCallback);
    
    public abstract boolean canConfigWifi();
    
    public abstract boolean connect(AccessPoint paramAccessPoint);
    
    public abstract int getIcon(AccessPoint paramAccessPoint);
    
    public abstract void removeAccessPointCallback(AccessPointCallback paramAccessPointCallback);
    
    public abstract void scanForAccessPoints();
    
    public static abstract interface AccessPointCallback
    {
      public abstract void onAccessPointsChanged(List<AccessPoint> paramList);
      
      public abstract void onSettingsActivityTriggered(Intent paramIntent);
    }
  }
  
  public static abstract interface EmergencyListener
  {
    public abstract void setEmergencyCallsOnly(boolean paramBoolean);
  }
  
  public static class IconState
  {
    public final String contentDescription;
    public final int icon;
    public final boolean visible;
    
    public IconState(boolean paramBoolean, int paramInt1, int paramInt2, Context paramContext)
    {
      this(paramBoolean, paramInt1, paramContext.getString(paramInt2));
    }
    
    public IconState(boolean paramBoolean, int paramInt, String paramString)
    {
      this.visible = paramBoolean;
      this.icon = paramInt;
      this.contentDescription = paramString;
    }
  }
  
  public static abstract interface SignalCallback
  {
    public void setEthernetIndicators(NetworkController.IconState paramIconState) {}
    
    public void setIsAirplaneMode(NetworkController.IconState paramIconState) {}
    
    public void setMobileDataEnabled(boolean paramBoolean) {}
    
    public void setMobileDataIndicators(NetworkController.IconState paramIconState1, NetworkController.IconState paramIconState2, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, String paramString1, String paramString2, boolean paramBoolean3, int paramInt7, boolean paramBoolean4, boolean paramBoolean5) {}
    
    public void setNoSims(boolean paramBoolean) {}
    
    public void setSubs(List<SubscriptionInfo> paramList) {}
    
    public void setWifiIndicators(boolean paramBoolean1, NetworkController.IconState paramIconState1, NetworkController.IconState paramIconState2, boolean paramBoolean2, boolean paramBoolean3, String paramString) {}
  }
  
  public static abstract interface SignalCallbackExtended
    extends NetworkController.SignalCallback
  {
    public abstract void setMobileDataIndicators(NetworkController.IconState paramIconState1, NetworkController.IconState paramIconState2, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, String paramString1, String paramString2, boolean paramBoolean3, int paramInt7, int paramInt8, int paramInt9, int paramInt10, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\NetworkController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */