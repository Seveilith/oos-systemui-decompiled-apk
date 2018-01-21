package com.android.systemui.recents.misc;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import java.util.ArrayList;

public class ReferenceCountedTrigger
{
  int mCount;
  Runnable mDecrementRunnable = new Runnable()
  {
    public void run()
    {
      ReferenceCountedTrigger.this.decrement();
    }
  };
  Runnable mErrorRunnable;
  ArrayList<Runnable> mFirstIncRunnables = new ArrayList();
  Runnable mIncrementRunnable = new Runnable()
  {
    public void run()
    {
      ReferenceCountedTrigger.this.increment();
    }
  };
  ArrayList<Runnable> mLastDecRunnables = new ArrayList();
  
  public ReferenceCountedTrigger()
  {
    this(null, null, null);
  }
  
  public ReferenceCountedTrigger(Runnable paramRunnable1, Runnable paramRunnable2, Runnable paramRunnable3)
  {
    if (paramRunnable1 != null) {
      this.mFirstIncRunnables.add(paramRunnable1);
    }
    if (paramRunnable2 != null) {
      this.mLastDecRunnables.add(paramRunnable2);
    }
    this.mErrorRunnable = paramRunnable3;
  }
  
  public void addLastDecrementRunnable(Runnable paramRunnable)
  {
    this.mLastDecRunnables.add(paramRunnable);
  }
  
  public void decrement()
  {
    this.mCount -= 1;
    if (this.mCount == 0) {
      flushLastDecrementRunnables();
    }
    while (this.mCount >= 0) {
      return;
    }
    if (this.mErrorRunnable != null)
    {
      this.mErrorRunnable.run();
      return;
    }
    throw new RuntimeException("Invalid ref count");
  }
  
  public Animator.AnimatorListener decrementOnAnimationEnd()
  {
    new AnimatorListenerAdapter()
    {
      private boolean hasEnded;
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if (this.hasEnded) {
          return;
        }
        ReferenceCountedTrigger.this.decrement();
        this.hasEnded = true;
      }
    };
  }
  
  public void flushLastDecrementRunnables()
  {
    if (!this.mLastDecRunnables.isEmpty())
    {
      int j = this.mLastDecRunnables.size();
      int i = 0;
      while (i < j)
      {
        ((Runnable)this.mLastDecRunnables.get(i)).run();
        i += 1;
      }
    }
    this.mLastDecRunnables.clear();
  }
  
  public void increment()
  {
    if ((this.mCount != 0) || (this.mFirstIncRunnables.isEmpty())) {}
    for (;;)
    {
      this.mCount += 1;
      return;
      int j = this.mFirstIncRunnables.size();
      int i = 0;
      while (i < j)
      {
        ((Runnable)this.mFirstIncRunnables.get(i)).run();
        i += 1;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\misc\ReferenceCountedTrigger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */