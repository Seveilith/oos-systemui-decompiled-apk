package com.android.systemui.statusbar.notification;

import android.util.Pools.SimplePool;
import android.view.View;

public class ActionListTransformState
  extends TransformState
{
  private static Pools.SimplePool<ActionListTransformState> sInstancePool = new Pools.SimplePool(40);
  
  public static ActionListTransformState obtain()
  {
    ActionListTransformState localActionListTransformState = (ActionListTransformState)sInstancePool.acquire();
    if (localActionListTransformState != null) {
      return localActionListTransformState;
    }
    return new ActionListTransformState();
  }
  
  public void recycle()
  {
    super.recycle();
    sInstancePool.release(this);
  }
  
  protected void resetTransformedView()
  {
    float f = getTransformedView().getTranslationY();
    super.resetTransformedView();
    getTransformedView().setTranslationY(f);
  }
  
  protected boolean sameAs(TransformState paramTransformState)
  {
    return paramTransformState instanceof ActionListTransformState;
  }
  
  public void transformViewFullyFrom(TransformState paramTransformState, float paramFloat) {}
  
  public void transformViewFullyTo(TransformState paramTransformState, float paramFloat) {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\notification\ActionListTransformState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */