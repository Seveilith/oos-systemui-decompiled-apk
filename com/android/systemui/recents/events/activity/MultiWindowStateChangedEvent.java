package com.android.systemui.recents.events.activity;

import com.android.systemui.recents.events.EventBus.AnimatedEvent;
import com.android.systemui.recents.model.TaskStack;

public class MultiWindowStateChangedEvent
  extends EventBus.AnimatedEvent
{
  public final boolean inMultiWindow;
  public final boolean showDeferredAnimation;
  public final TaskStack stack;
  
  public MultiWindowStateChangedEvent(boolean paramBoolean1, boolean paramBoolean2, TaskStack paramTaskStack)
  {
    this.inMultiWindow = paramBoolean1;
    this.showDeferredAnimation = paramBoolean2;
    this.stack = paramTaskStack;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\activity\MultiWindowStateChangedEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */