package com.android.systemui.recents.events.activity;

import com.android.systemui.recents.events.EventBus.AnimatedEvent;

public class DismissRecentsToHomeAnimationStarted
  extends EventBus.AnimatedEvent
{
  public final boolean animated;
  
  public DismissRecentsToHomeAnimationStarted(boolean paramBoolean)
  {
    this.animated = paramBoolean;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\activity\DismissRecentsToHomeAnimationStarted.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */