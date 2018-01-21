package com.android.systemui.recents.events.ui;

import com.android.systemui.recents.events.EventBus.Event;

public class UpdateFreeformTaskViewVisibilityEvent
  extends EventBus.Event
{
  public final boolean visible;
  
  public UpdateFreeformTaskViewVisibilityEvent(boolean paramBoolean)
  {
    this.visible = paramBoolean;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\ui\UpdateFreeformTaskViewVisibilityEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */