package com.android.systemui.recents.views;

import com.android.systemui.recents.model.TaskStack.DockState;

class DockRegion
{
  public static TaskStack.DockState[] PHONE_LANDSCAPE = { TaskStack.DockState.LEFT };
  public static TaskStack.DockState[] PHONE_PORTRAIT = { TaskStack.DockState.TOP };
  public static TaskStack.DockState[] TABLET_LANDSCAPE = { TaskStack.DockState.LEFT, TaskStack.DockState.RIGHT };
  public static TaskStack.DockState[] TABLET_PORTRAIT = PHONE_PORTRAIT;
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\DockRegion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */