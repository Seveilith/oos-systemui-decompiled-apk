package com.android.systemui.statusbar.stack;

import android.view.View;

public abstract interface ScrollContainer
{
  public abstract void lockScrollTo(View paramView);
  
  public abstract void requestDisallowDismiss();
  
  public abstract void requestDisallowLongPress();
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\stack\ScrollContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */