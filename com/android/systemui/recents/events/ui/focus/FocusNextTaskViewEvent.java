package com.android.systemui.recents.events.ui.focus;

import com.android.systemui.recents.events.EventBus.Event;

public class FocusNextTaskViewEvent
  extends EventBus.Event
{
  public final int timerIndicatorDuration;
  
  public FocusNextTaskViewEvent(int paramInt)
  {
    this.timerIndicatorDuration = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\ui\focus\FocusNextTaskViewEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */