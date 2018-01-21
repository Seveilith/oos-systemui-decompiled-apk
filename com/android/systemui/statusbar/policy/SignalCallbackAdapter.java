package com.android.systemui.statusbar.policy;

import android.telephony.SubscriptionInfo;
import java.util.List;

public class SignalCallbackAdapter
  implements NetworkController.SignalCallback
{
  public void setEthernetIndicators(NetworkController.IconState paramIconState) {}
  
  public void setIsAirplaneMode(NetworkController.IconState paramIconState) {}
  
  public void setMobileDataEnabled(boolean paramBoolean) {}
  
  public void setMobileDataIndicators(NetworkController.IconState paramIconState1, NetworkController.IconState paramIconState2, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, String paramString1, String paramString2, boolean paramBoolean3, int paramInt7, boolean paramBoolean4, boolean paramBoolean5) {}
  
  public void setNoSims(boolean paramBoolean) {}
  
  public void setSubs(List<SubscriptionInfo> paramList) {}
  
  public void setWifiIndicators(boolean paramBoolean1, NetworkController.IconState paramIconState1, NetworkController.IconState paramIconState2, boolean paramBoolean2, boolean paramBoolean3, String paramString) {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\SignalCallbackAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */