package com.android.systemui.recents;

import android.content.Context;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.DebugFlagsChangedEvent;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.tuner.TunerService.Tunable;

public class RecentsDebugFlags
  implements TunerService.Tunable
{
  public RecentsDebugFlags(Context paramContext) {}
  
  public boolean isFastToggleRecentsEnabled()
  {
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    return (!localSystemServicesProxy.hasFreeformWorkspaceSupport()) && (!localSystemServicesProxy.isTouchExplorationEnabled());
  }
  
  public boolean isPagingEnabled()
  {
    return false;
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    EventBus.getDefault().send(new DebugFlagsChangedEvent());
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\RecentsDebugFlags.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */