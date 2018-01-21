package com.android.systemui.statusbar.phone;

import android.util.Pools.SynchronizedPool;
import android.view.MotionEvent;
import java.util.ArrayDeque;
import java.util.Iterator;

public class NoisyVelocityTracker
  implements VelocityTrackerInterface
{
  private static final Pools.SynchronizedPool<NoisyVelocityTracker> sNoisyPool = new Pools.SynchronizedPool(2);
  private final int MAX_EVENTS = 8;
  private ArrayDeque<MotionEventCopy> mEventBuf = new ArrayDeque(8);
  private float mVX;
  private float mVY = 0.0F;
  
  public static NoisyVelocityTracker obtain()
  {
    NoisyVelocityTracker localNoisyVelocityTracker = (NoisyVelocityTracker)sNoisyPool.acquire();
    if (localNoisyVelocityTracker != null) {
      return localNoisyVelocityTracker;
    }
    return new NoisyVelocityTracker();
  }
  
  public void addMovement(MotionEvent paramMotionEvent)
  {
    if (this.mEventBuf.size() == 8) {
      this.mEventBuf.remove();
    }
    this.mEventBuf.add(new MotionEventCopy(paramMotionEvent.getX(), paramMotionEvent.getY(), paramMotionEvent.getEventTime()));
  }
  
  public void computeCurrentVelocity(int paramInt)
  {
    this.mVY = 0.0F;
    this.mVX = 0.0F;
    Object localObject = null;
    int i = 0;
    float f2 = 0.0F;
    float f1 = 10.0F;
    Iterator localIterator = this.mEventBuf.iterator();
    while (localIterator.hasNext())
    {
      MotionEventCopy localMotionEventCopy = (MotionEventCopy)localIterator.next();
      float f4 = f2;
      float f3 = f1;
      if (localObject != null)
      {
        f3 = (float)(localMotionEventCopy.t - ((MotionEventCopy)localObject).t) / paramInt;
        f4 = localMotionEventCopy.x;
        float f5 = ((MotionEventCopy)localObject).x;
        float f6 = localMotionEventCopy.y;
        float f7 = ((MotionEventCopy)localObject).y;
        if (localMotionEventCopy.t != ((MotionEventCopy)localObject).t)
        {
          this.mVX += f1 * (f4 - f5) / f3;
          this.mVY += f1 * (f6 - f7) / f3;
          f4 = f2 + f1;
          f3 = f1 * 0.75F;
        }
      }
      else
      {
        localObject = localMotionEventCopy;
        i += 1;
        f2 = f4;
        f1 = f3;
      }
    }
    if (f2 > 0.0F)
    {
      this.mVX /= f2;
      this.mVY /= f2;
      return;
    }
    this.mVY = 0.0F;
    this.mVX = 0.0F;
  }
  
  public float getXVelocity()
  {
    if ((Float.isNaN(this.mVX)) || (Float.isInfinite(this.mVX))) {
      this.mVX = 0.0F;
    }
    return this.mVX;
  }
  
  public float getYVelocity()
  {
    if ((Float.isNaN(this.mVY)) || (Float.isInfinite(this.mVX))) {
      this.mVY = 0.0F;
    }
    return this.mVY;
  }
  
  public void recycle()
  {
    this.mEventBuf.clear();
    sNoisyPool.release(this);
  }
  
  private static class MotionEventCopy
  {
    long t;
    float x;
    float y;
    
    public MotionEventCopy(float paramFloat1, float paramFloat2, long paramLong)
    {
      this.x = paramFloat1;
      this.y = paramFloat2;
      this.t = paramLong;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\NoisyVelocityTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */