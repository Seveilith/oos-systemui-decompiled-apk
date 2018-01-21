package com.android.systemui.recents.events.activity;

import com.android.systemui.recents.events.EventBus.Event;

public class HideRecentsEvent
  extends EventBus.Event
{
  public final boolean triggeredFromAltTab;
  public final boolean triggeredFromHomeKey;
  
  public HideRecentsEvent(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.triggeredFromAltTab = paramBoolean1;
    this.triggeredFromHomeKey = paramBoolean2;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\activity\HideRecentsEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */