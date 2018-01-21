package com.android.systemui.statusbar.notification;

import android.util.Pools.SimplePool;

public class ProgressTransformState
  extends TransformState
{
  private static Pools.SimplePool<ProgressTransformState> sInstancePool = new Pools.SimplePool(40);
  
  public static ProgressTransformState obtain()
  {
    ProgressTransformState localProgressTransformState = (ProgressTransformState)sInstancePool.acquire();
    if (localProgressTransformState != null) {
      return localProgressTransformState;
    }
    return new ProgressTransformState();
  }
  
  public void recycle()
  {
    super.recycle();
    sInstancePool.release(this);
  }
  
  protected boolean sameAs(TransformState paramTransformState)
  {
    if ((paramTransformState instanceof ProgressTransformState)) {
      return true;
    }
    return super.sameAs(paramTransformState);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\notification\ProgressTransformState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */