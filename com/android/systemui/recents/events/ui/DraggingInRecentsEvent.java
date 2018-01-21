package com.android.systemui.recents.events.ui;

import com.android.systemui.recents.events.EventBus.Event;

public class DraggingInRecentsEvent
  extends EventBus.Event
{
  public final float distanceFromTop;
  
  public DraggingInRecentsEvent(float paramFloat)
  {
    this.distanceFromTop = paramFloat;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\ui\DraggingInRecentsEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */