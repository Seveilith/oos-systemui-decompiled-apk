package com.android.systemui.recents.events.activity;

import com.android.systemui.recents.events.EventBus.Event;
import com.android.systemui.recents.model.RecentsPackageMonitor;

public class PackagesChangedEvent
  extends EventBus.Event
{
  public final RecentsPackageMonitor monitor;
  public final String packageName;
  public final int userId;
  
  public PackagesChangedEvent(RecentsPackageMonitor paramRecentsPackageMonitor, String paramString, int paramInt)
  {
    this.monitor = paramRecentsPackageMonitor;
    this.packageName = paramString;
    this.userId = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\activity\PackagesChangedEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */