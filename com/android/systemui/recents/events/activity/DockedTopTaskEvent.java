package com.android.systemui.recents.events.activity;

import android.graphics.Rect;
import com.android.systemui.recents.events.EventBus.Event;

public class DockedTopTaskEvent
  extends EventBus.Event
{
  public int dragMode;
  public Rect initialRect;
  
  public DockedTopTaskEvent(int paramInt, Rect paramRect)
  {
    this.dragMode = paramInt;
    this.initialRect = paramRect;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\events\activity\DockedTopTaskEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */