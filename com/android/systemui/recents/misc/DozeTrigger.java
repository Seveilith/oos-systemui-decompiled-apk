package com.android.systemui.recents.misc;

import android.os.Handler;
import android.view.ViewDebug.ExportedProperty;

public class DozeTrigger
{
  @ViewDebug.ExportedProperty(category="recents")
  int mDozeDurationMilliseconds;
  Runnable mDozeRunnable = new Runnable()
  {
    public void run()
    {
      DozeTrigger.this.mIsDozing = false;
      DozeTrigger.this.mIsAsleep = true;
      DozeTrigger.this.mOnSleepRunnable.run();
    }
  };
  Handler mHandler = new Handler();
  @ViewDebug.ExportedProperty(category="recents")
  boolean mIsAsleep;
  @ViewDebug.ExportedProperty(category="recents")
  boolean mIsDozing;
  Runnable mOnSleepRunnable;
  
  public DozeTrigger(int paramInt, Runnable paramRunnable)
  {
    this.mDozeDurationMilliseconds = paramInt;
    this.mOnSleepRunnable = paramRunnable;
  }
  
  void forcePoke()
  {
    this.mHandler.removeCallbacks(this.mDozeRunnable);
    this.mHandler.postDelayed(this.mDozeRunnable, this.mDozeDurationMilliseconds);
    this.mIsDozing = true;
  }
  
  public boolean isAsleep()
  {
    return this.mIsAsleep;
  }
  
  public boolean isDozing()
  {
    return this.mIsDozing;
  }
  
  public void poke()
  {
    if (this.mIsDozing) {
      forcePoke();
    }
  }
  
  public void setDozeDuration(int paramInt)
  {
    this.mDozeDurationMilliseconds = paramInt;
  }
  
  public void startDozing()
  {
    forcePoke();
    this.mIsAsleep = false;
  }
  
  public void stopDozing()
  {
    this.mHandler.removeCallbacks(this.mDozeRunnable);
    this.mIsDozing = false;
    this.mIsAsleep = false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\misc\DozeTrigger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */