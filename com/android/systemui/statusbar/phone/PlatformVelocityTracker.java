package com.android.systemui.statusbar.phone;

import android.util.Pools.SynchronizedPool;
import android.view.MotionEvent;
import android.view.VelocityTracker;

public class PlatformVelocityTracker
  implements VelocityTrackerInterface
{
  private static final Pools.SynchronizedPool<PlatformVelocityTracker> sPool = new Pools.SynchronizedPool(2);
  private VelocityTracker mTracker;
  
  public static PlatformVelocityTracker obtain()
  {
    PlatformVelocityTracker localPlatformVelocityTracker2 = (PlatformVelocityTracker)sPool.acquire();
    PlatformVelocityTracker localPlatformVelocityTracker1 = localPlatformVelocityTracker2;
    if (localPlatformVelocityTracker2 == null) {
      localPlatformVelocityTracker1 = new PlatformVelocityTracker();
    }
    localPlatformVelocityTracker1.setTracker(VelocityTracker.obtain());
    return localPlatformVelocityTracker1;
  }
  
  public void addMovement(MotionEvent paramMotionEvent)
  {
    this.mTracker.addMovement(paramMotionEvent);
  }
  
  public void computeCurrentVelocity(int paramInt)
  {
    this.mTracker.computeCurrentVelocity(paramInt);
  }
  
  public float getXVelocity()
  {
    return this.mTracker.getXVelocity();
  }
  
  public float getYVelocity()
  {
    return this.mTracker.getYVelocity();
  }
  
  public void recycle()
  {
    this.mTracker.recycle();
    sPool.release(this);
  }
  
  public void setTracker(VelocityTracker paramVelocityTracker)
  {
    this.mTracker = paramVelocityTracker;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\PlatformVelocityTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */