package com.android.systemui.recents.events.component;

import android.content.Context;
import com.android.systemui.recents.events.EventBus.Event;

public class ScreenPinningRequestEvent
  extends EventBus.Event
{
  public final Context applicationContext;
  public final int taskId;
  
  public ScreenPinningRequestEvent(Context paramContext, int paramInt)
  {
    this.applicationContext = paramContext.getApplicationContext();
    this.taskId = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\component\ScreenPinningRequestEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */