package com.android.systemui.recents.events.activity;

import com.android.systemui.recents.events.EventBus.AnimatedEvent;

public class ConfigurationChangedEvent
  extends EventBus.AnimatedEvent
{
  public final boolean fromDeviceOrientationChange;
  public final boolean fromDisplayDensityChange;
  public final boolean fromMultiWindow;
  public final boolean hasStackTasks;
  
  public ConfigurationChangedEvent(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    this.fromMultiWindow = paramBoolean1;
    this.fromDeviceOrientationChange = paramBoolean2;
    this.fromDisplayDensityChange = paramBoolean3;
    this.hasStackTasks = paramBoolean4;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\activity\ConfigurationChangedEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */