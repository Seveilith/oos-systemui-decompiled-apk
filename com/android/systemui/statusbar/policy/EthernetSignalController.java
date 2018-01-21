package com.android.systemui.statusbar.policy;

import android.content.Context;
import java.util.BitSet;

public class EthernetSignalController
  extends SignalController<SignalController.State, SignalController.IconGroup>
{
  public EthernetSignalController(Context paramContext, CallbackHandler paramCallbackHandler, NetworkControllerImpl paramNetworkControllerImpl)
  {
    super("EthernetSignalController", paramContext, 3, paramCallbackHandler, paramNetworkControllerImpl);
    paramContext = this.mCurrentState;
    paramCallbackHandler = new SignalController.IconGroup("Ethernet Icons", EthernetIcons.ETHERNET_ICONS, null, AccessibilityContentDescriptions.ETHERNET_CONNECTION_VALUES, 0, 0, 0, 0, AccessibilityContentDescriptions.ETHERNET_CONNECTION_VALUES[0]);
    this.mLastState.iconGroup = paramCallbackHandler;
    paramContext.iconGroup = paramCallbackHandler;
  }
  
  public SignalController.State cleanState()
  {
    return new SignalController.State();
  }
  
  public void notifyListeners(NetworkController.SignalCallback paramSignalCallback)
  {
    boolean bool = this.mCurrentState.connected;
    String str = getStringIfExists(getContentDescription());
    paramSignalCallback.setEthernetIndicators(new NetworkController.IconState(bool, getCurrentIconId(), str));
  }
  
  public void updateConnectivity(BitSet paramBitSet1, BitSet paramBitSet2)
  {
    this.mCurrentState.connected = paramBitSet1.get(this.mTransportType);
    super.updateConnectivity(paramBitSet1, paramBitSet2);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\EthernetSignalController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */