package com.android.systemui.screenshot;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.UserManager;
import android.util.Log;

public class TakeScreenshotService
  extends Service
{
  private static GlobalScreenshot mScreenshot;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      boolean bool3 = true;
      boolean bool2 = true;
      Runnable local1 = new Runnable()
      {
        public void run()
        {
          Message localMessage = Message.obtain(null, 1);
          try
          {
            this.val$callback.send(localMessage);
            return;
          }
          catch (RemoteException localRemoteException) {}
        }
      };
      if (!((UserManager)TakeScreenshotService.this.getSystemService(UserManager.class)).isUserUnlocked())
      {
        Log.w("TakeScreenshotService", "Skipping screenshot because storage is locked!");
        post(local1);
        return;
      }
      if (TakeScreenshotService.-get0() == null) {
        TakeScreenshotService.-set0(new GlobalScreenshot(TakeScreenshotService.this));
      }
      boolean bool1;
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 1: 
        localGlobalScreenshot = TakeScreenshotService.-get0();
        if (paramAnonymousMessage.arg1 > 0)
        {
          bool1 = true;
          if (paramAnonymousMessage.arg2 <= 0) {
            break label137;
          }
        }
        for (;;)
        {
          localGlobalScreenshot.takeScreenshot(local1, bool1, bool2);
          return;
          bool1 = false;
          break;
          label137:
          bool2 = false;
        }
      }
      GlobalScreenshot localGlobalScreenshot = TakeScreenshotService.-get0();
      if (paramAnonymousMessage.arg1 > 0)
      {
        bool1 = true;
        if (paramAnonymousMessage.arg2 <= 0) {
          break label181;
        }
      }
      label181:
      for (bool2 = bool3;; bool2 = false)
      {
        localGlobalScreenshot.takeScreenshotPartial(local1, bool1, bool2);
        return;
        bool1 = false;
        break;
      }
    }
  };
  
  public IBinder onBind(Intent paramIntent)
  {
    return new Messenger(this.mHandler).getBinder();
  }
  
  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    if (mScreenshot == null) {
      mScreenshot = new GlobalScreenshot(this);
    }
    mScreenshot.saveScreenshot();
    return 3;
  }
  
  public boolean onUnbind(Intent paramIntent)
  {
    if (mScreenshot != null) {
      mScreenshot.stopScreenshot();
    }
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\screenshot\TakeScreenshotService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */