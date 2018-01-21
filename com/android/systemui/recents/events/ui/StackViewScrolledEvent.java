package com.android.systemui.recents.events.ui;

import android.util.MutableInt;
import com.android.systemui.recents.events.EventBus.ReusableEvent;

public class StackViewScrolledEvent
  extends EventBus.ReusableEvent
{
  public final MutableInt yMovement = new MutableInt(0);
  
  public void updateY(int paramInt)
  {
    this.yMovement.value = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\ui\StackViewScrolledEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */