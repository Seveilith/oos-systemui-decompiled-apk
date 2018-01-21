package com.android.systemui.recents.events.ui;

import com.android.systemui.recents.events.EventBus.Event;

public class DraggingInRecentsEndedEvent
  extends EventBus.Event
{
  public final float velocity;
  
  public DraggingInRecentsEndedEvent(float paramFloat)
  {
    this.velocity = paramFloat;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\ui\DraggingInRecentsEndedEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */