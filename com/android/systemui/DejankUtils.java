package com.android.systemui;

import android.os.Handler;
import android.os.Looper;
import android.view.Choreographer;
import java.util.ArrayList;

public class DejankUtils
{
  private static final Runnable sAnimationCallbackRunnable = new Runnable()
  {
    public void run()
    {
      int i = 0;
      while (i < DejankUtils.-get1().size())
      {
        DejankUtils.-get0().post((Runnable)DejankUtils.-get1().get(i));
        i += 1;
      }
      DejankUtils.-get1().clear();
    }
  };
  private static final Choreographer sChoreographer = ;
  private static final Handler sHandler = new Handler();
  private static final ArrayList<Runnable> sPendingRunnables = new ArrayList();
  
  public static void postAfterTraversal(Runnable paramRunnable)
  {
    throwIfNotCalledOnMainThread();
    sPendingRunnables.add(paramRunnable);
    postAnimationCallback();
  }
  
  private static void postAnimationCallback()
  {
    sChoreographer.postCallback(1, sAnimationCallbackRunnable, null);
  }
  
  public static void removeCallbacks(Runnable paramRunnable)
  {
    throwIfNotCalledOnMainThread();
    sPendingRunnables.remove(paramRunnable);
    sHandler.removeCallbacks(paramRunnable);
  }
  
  private static void throwIfNotCalledOnMainThread()
  {
    if (!Looper.getMainLooper().isCurrentThread()) {
      throw new IllegalStateException("should be called from the main thread.");
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\DejankUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */