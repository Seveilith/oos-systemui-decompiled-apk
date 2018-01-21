package com.android.systemui.recents.misc;

import android.os.Handler;
import android.os.HandlerThread;

public final class ForegroundThread
  extends HandlerThread
{
  private static Handler sHandler;
  private static ForegroundThread sInstance;
  
  private ForegroundThread()
  {
    super("recents.fg");
  }
  
  private static void ensureThreadLocked()
  {
    if (sInstance == null)
    {
      sInstance = new ForegroundThread();
      sInstance.start();
      sHandler = new Handler(sInstance.getLooper());
    }
  }
  
  public static ForegroundThread get()
  {
    try
    {
      ensureThreadLocked();
      ForegroundThread localForegroundThread = sInstance;
      return localForegroundThread;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public static Handler getHandler()
  {
    try
    {
      ensureThreadLocked();
      Handler localHandler = sHandler;
      return localHandler;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\misc\ForegroundThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */