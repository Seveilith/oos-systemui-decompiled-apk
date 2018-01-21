package com.android.systemui.recents.events.component;

import com.android.systemui.recents.events.EventBus.Event;

public class ShowUserToastEvent
  extends EventBus.Event
{
  public final int msgLength;
  public final int msgResId;
  
  public ShowUserToastEvent(int paramInt1, int paramInt2)
  {
    this.msgResId = paramInt1;
    this.msgLength = paramInt2;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\component\ShowUserToastEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */