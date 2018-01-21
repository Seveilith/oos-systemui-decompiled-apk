package com.android.keyguard.plugin;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

public class ClockCtrl
{
  private static ClockCtrl mInstance;
  private BGHandler mBGHandler;
  private OnTimeUpdatedListener mListener;
  private Looper mNonUiLooper;
  private boolean mScreenON;
  
  private void dispatchTimeChanged()
  {
    if (this.mListener != null) {
      this.mListener.onTimeChanged();
    }
  }
  
  public static ClockCtrl getInstance()
  {
    try
    {
      if (mInstance == null) {
        mInstance = new ClockCtrl();
      }
      ClockCtrl localClockCtrl = mInstance;
      return localClockCtrl;
    }
    finally {}
  }
  
  private void handleNotifySchedule(boolean paramBoolean)
  {
    long l = System.currentTimeMillis();
    l = (l / 60000L + 1L) * 60000L - l;
    Log.i("ClockCtrl", " schedule next: " + l);
    if (this.mBGHandler != null)
    {
      Message localMessage = Message.obtain();
      localMessage.what = 131072;
      localMessage.arg1 = 1;
      this.mBGHandler.sendMessageDelayed(localMessage, l);
    }
    if (paramBoolean) {
      dispatchTimeChanged();
    }
  }
  
  private void startUpdate(String paramString)
  {
    Log.i("ClockCtrl", "startUpdate: " + paramString + ", " + this.mScreenON);
    if (this.mScreenON)
    {
      if (this.mBGHandler != null)
      {
        this.mBGHandler.removeMessages(131072);
        this.mBGHandler.sendEmptyMessage(131072);
      }
      dispatchTimeChanged();
    }
  }
  
  private void stopUpdate(String paramString)
  {
    Log.i("ClockCtrl", "stopUpdate: " + paramString);
    if (this.mBGHandler != null) {
      this.mBGHandler.removeMessages(131072);
    }
  }
  
  public Looper getNonUILooper()
  {
    try
    {
      if (this.mNonUiLooper == null)
      {
        localObject1 = new HandlerThread("ClockCtrl thread");
        ((HandlerThread)localObject1).start();
        this.mNonUiLooper = ((HandlerThread)localObject1).getLooper();
      }
      Object localObject1 = this.mNonUiLooper;
      return (Looper)localObject1;
    }
    finally {}
  }
  
  public void onScreenTurnedOff()
  {
    this.mScreenON = false;
    stopUpdate("ScreenOFF");
  }
  
  public void onScreenTurnedOn()
  {
    this.mScreenON = true;
    startUpdate("ScreenON");
  }
  
  public void onStartCtrl(OnTimeUpdatedListener paramOnTimeUpdatedListener, Context paramContext)
  {
    this.mListener = paramOnTimeUpdatedListener;
    if (this.mBGHandler == null)
    {
      this.mBGHandler = new BGHandler(getNonUILooper());
      if (paramContext != null) {
        this.mScreenON = ((PowerManager)paramContext.getSystemService("power")).isScreenOn();
      }
    }
    startUpdate("startCtrl");
  }
  
  private class BGHandler
    extends Handler
  {
    public BGHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool = true;
      if (paramMessage == null)
      {
        Log.w("ClockCtrl", "BGHandler: msg null");
        return;
      }
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      ClockCtrl localClockCtrl = ClockCtrl.this;
      if (paramMessage.arg1 == 1) {}
      for (;;)
      {
        ClockCtrl.-wrap0(localClockCtrl, bool);
        return;
        bool = false;
      }
    }
  }
  
  public static abstract interface OnTimeUpdatedListener
  {
    public abstract void onTimeChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\plugin\ClockCtrl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */