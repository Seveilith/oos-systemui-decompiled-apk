package com.android.systemui.recents.events.activity;

import com.android.systemui.recents.events.EventBus.Event;

public class ShowStackActionButtonEvent
  extends EventBus.Event
{
  public final boolean translate;
  
  public ShowStackActionButtonEvent(boolean paramBoolean)
  {
    this.translate = paramBoolean;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\activity\ShowStackActionButtonEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */