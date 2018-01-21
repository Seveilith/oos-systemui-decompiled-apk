package com.android.systemui.shortcut;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import com.android.internal.policy.IShortcutService.Stub;

public class ShortcutKeyServiceProxy
  extends IShortcutService.Stub
{
  private Callbacks mCallbacks;
  private final Handler mHandler = new H(null);
  private final Object mLock = new Object();
  
  public ShortcutKeyServiceProxy(Callbacks paramCallbacks)
  {
    this.mCallbacks = paramCallbacks;
  }
  
  public void notifyShortcutKeyPressed(long paramLong)
    throws RemoteException
  {
    synchronized (this.mLock)
    {
      this.mHandler.obtainMessage(1, Long.valueOf(paramLong)).sendToTarget();
      return;
    }
  }
  
  public static abstract interface Callbacks
  {
    public abstract void onShortcutKeyPressed(long paramLong);
  }
  
  private final class H
    extends Handler
  {
    private H() {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      ShortcutKeyServiceProxy.-get0(ShortcutKeyServiceProxy.this).onShortcutKeyPressed(((Long)paramMessage.obj).longValue());
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\shortcut\ShortcutKeyServiceProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */