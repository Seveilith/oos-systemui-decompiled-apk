package com.android.systemui.recents;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import com.android.internal.os.SomeArgs;

public class RecentsImplProxy
  extends IRecentsNonSystemUserCallbacks.Stub
{
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      boolean bool2 = true;
      boolean bool5 = true;
      switch (paramAnonymousMessage.what)
      {
      default: 
        super.handleMessage(paramAnonymousMessage);
      }
      for (;;)
      {
        super.handleMessage(paramAnonymousMessage);
        return;
        RecentsImplProxy.-get0(RecentsImplProxy.this).preloadRecents();
        continue;
        RecentsImplProxy.-get0(RecentsImplProxy.this).cancelPreloadingRecents();
        continue;
        Object localObject = (SomeArgs)paramAnonymousMessage.obj;
        RecentsImpl localRecentsImpl = RecentsImplProxy.-get0(RecentsImplProxy.this);
        boolean bool1;
        label130:
        label141:
        boolean bool3;
        label152:
        boolean bool4;
        if (((SomeArgs)localObject).argi1 != 0)
        {
          bool1 = true;
          if (((SomeArgs)localObject).argi2 == 0) {
            break label200;
          }
          bool2 = true;
          if (((SomeArgs)localObject).argi3 == 0) {
            break label206;
          }
          bool3 = true;
          if (((SomeArgs)localObject).argi4 == 0) {
            break label212;
          }
          bool4 = true;
          label163:
          if (((SomeArgs)localObject).argi5 == 0) {
            break label218;
          }
        }
        for (;;)
        {
          localRecentsImpl.showRecents(bool1, bool2, bool3, bool4, bool5, ((SomeArgs)localObject).argi6);
          break;
          bool1 = false;
          break label130;
          label200:
          bool2 = false;
          break label141;
          label206:
          bool3 = false;
          break label152;
          label212:
          bool4 = false;
          break label163;
          label218:
          bool5 = false;
        }
        localObject = RecentsImplProxy.-get0(RecentsImplProxy.this);
        if (paramAnonymousMessage.arg1 != 0)
        {
          bool1 = true;
          label243:
          if (paramAnonymousMessage.arg2 == 0) {
            break label268;
          }
        }
        for (;;)
        {
          ((RecentsImpl)localObject).hideRecents(bool1, bool2);
          break;
          bool1 = false;
          break label243;
          label268:
          bool2 = false;
        }
        localObject = (SomeArgs)paramAnonymousMessage.obj;
        RecentsImplProxy.-get0(RecentsImplProxy.this).toggleRecents(((SomeArgs)localObject).argi1);
        continue;
        RecentsImplProxy.-get0(RecentsImplProxy.this).onConfigurationChanged();
        continue;
        localObject = (SomeArgs)paramAnonymousMessage.obj;
        localRecentsImpl = RecentsImplProxy.-get0(RecentsImplProxy.this);
        int i = ((SomeArgs)localObject).argi1;
        int j = ((SomeArgs)localObject).argi2;
        ((SomeArgs)localObject).argi3 = 0;
        localRecentsImpl.dockTopTask(i, j, 0, (Rect)((SomeArgs)localObject).arg1);
        continue;
        RecentsImplProxy.-get0(RecentsImplProxy.this).onDraggingInRecents(((Float)paramAnonymousMessage.obj).floatValue());
        continue;
        RecentsImplProxy.-get0(RecentsImplProxy.this).onDraggingInRecentsEnded(((Float)paramAnonymousMessage.obj).floatValue());
        continue;
        RecentsImplProxy.-get0(RecentsImplProxy.this).onShowCurrentUserToast(paramAnonymousMessage.arg1, paramAnonymousMessage.arg2);
      }
    }
  };
  private RecentsImpl mImpl;
  
  public RecentsImplProxy(RecentsImpl paramRecentsImpl)
  {
    this.mImpl = paramRecentsImpl;
  }
  
  public void cancelPreloadingRecents()
    throws RemoteException
  {
    this.mHandler.sendEmptyMessage(2);
  }
  
  public void dockTopTask(int paramInt1, int paramInt2, int paramInt3, Rect paramRect)
    throws RemoteException
  {
    SomeArgs localSomeArgs = SomeArgs.obtain();
    localSomeArgs.argi1 = paramInt1;
    localSomeArgs.argi2 = paramInt2;
    localSomeArgs.argi3 = paramInt3;
    localSomeArgs.arg1 = paramRect;
    this.mHandler.sendMessage(this.mHandler.obtainMessage(7, localSomeArgs));
  }
  
  public void hideRecents(boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException
  {
    int j = 1;
    Handler localHandler1 = this.mHandler;
    Handler localHandler2 = this.mHandler;
    int i;
    if (paramBoolean1)
    {
      i = 1;
      if (!paramBoolean2) {
        break label46;
      }
    }
    for (;;)
    {
      localHandler1.sendMessage(localHandler2.obtainMessage(4, i, j));
      return;
      i = 0;
      break;
      label46:
      j = 0;
    }
  }
  
  public void onConfigurationChanged()
    throws RemoteException
  {
    this.mHandler.sendEmptyMessage(6);
  }
  
  public void onDraggingInRecents(float paramFloat)
    throws RemoteException
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(8, Float.valueOf(paramFloat)));
  }
  
  public void onDraggingInRecentsEnded(float paramFloat)
    throws RemoteException
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(9, Float.valueOf(paramFloat)));
  }
  
  public void preloadRecents()
    throws RemoteException
  {
    this.mHandler.sendEmptyMessage(1);
  }
  
  public void showCurrentUserToast(int paramInt1, int paramInt2)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(10, paramInt1, paramInt2));
  }
  
  public void showRecents(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, int paramInt)
    throws RemoteException
  {
    int j = 1;
    SomeArgs localSomeArgs = SomeArgs.obtain();
    if (paramBoolean1)
    {
      i = 1;
      localSomeArgs.argi1 = i;
      if (!paramBoolean2) {
        break label113;
      }
      i = 1;
      label29:
      localSomeArgs.argi2 = i;
      if (!paramBoolean3) {
        break label119;
      }
      i = 1;
      label43:
      localSomeArgs.argi3 = i;
      if (!paramBoolean4) {
        break label125;
      }
      i = 1;
      label58:
      localSomeArgs.argi4 = i;
      if (!paramBoolean5) {
        break label131;
      }
    }
    label113:
    label119:
    label125:
    label131:
    for (int i = j;; i = 0)
    {
      localSomeArgs.argi5 = i;
      localSomeArgs.argi6 = paramInt;
      this.mHandler.sendMessage(this.mHandler.obtainMessage(3, localSomeArgs));
      return;
      i = 0;
      break;
      i = 0;
      break label29;
      i = 0;
      break label43;
      i = 0;
      break label58;
    }
  }
  
  public void toggleRecents(int paramInt)
    throws RemoteException
  {
    SomeArgs localSomeArgs = SomeArgs.obtain();
    localSomeArgs.argi1 = paramInt;
    this.mHandler.sendMessage(this.mHandler.obtainMessage(5, localSomeArgs));
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\RecentsImplProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */