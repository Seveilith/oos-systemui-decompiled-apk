package com.android.systemui.recents.events.component;

import android.content.Context;
import com.android.systemui.recents.events.EventBus.Event;

public class RecentsVisibilityChangedEvent
  extends EventBus.Event
{
  public final Context applicationContext;
  public final boolean visible;
  
  public RecentsVisibilityChangedEvent(Context paramContext, boolean paramBoolean)
  {
    this.applicationContext = paramContext.getApplicationContext();
    this.visible = paramBoolean;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\component\RecentsVisibilityChangedEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */