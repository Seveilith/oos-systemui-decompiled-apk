package com.android.systemui.statusbar.phone;

import android.view.MotionEvent;

public abstract interface VelocityTrackerInterface
{
  public abstract void addMovement(MotionEvent paramMotionEvent);
  
  public abstract void computeCurrentVelocity(int paramInt);
  
  public abstract float getXVelocity();
  
  public abstract float getYVelocity();
  
  public abstract void recycle();
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\VelocityTrackerInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */