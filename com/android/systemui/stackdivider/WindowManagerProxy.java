package com.android.systemui.stackdivider;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.android.internal.annotations.GuardedBy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WindowManagerProxy
{
  private static final WindowManagerProxy sInstance = new WindowManagerProxy();
  private float mDimLayerAlpha;
  private final Runnable mDimLayerRunnable = new Runnable()
  {
    public void run()
    {
      try
      {
        WindowManagerGlobal.getWindowManagerService().setResizeDimLayer(WindowManagerProxy.-get2(WindowManagerProxy.this), WindowManagerProxy.-get1(WindowManagerProxy.this), WindowManagerProxy.-get0(WindowManagerProxy.this));
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("WindowManagerProxy", "Failed to resize stack: " + localRemoteException);
      }
    }
  };
  private int mDimLayerTargetStack;
  private boolean mDimLayerVisible;
  private final Runnable mDismissRunnable = new Runnable()
  {
    public void run()
    {
      try
      {
        ActivityManagerNative.getDefault().moveTasksToFullscreenStack(3, false);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("WindowManagerProxy", "Failed to remove stack: " + localRemoteException);
      }
    }
  };
  @GuardedBy("mDockedRect")
  private final Rect mDockedRect = new Rect();
  private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
  private final Runnable mMaximizeRunnable = new Runnable()
  {
    public void run()
    {
      try
      {
        ActivityManagerNative.getDefault().resizeStack(3, null, true, true, false, -1);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("WindowManagerProxy", "Failed to resize stack: " + localRemoteException);
      }
    }
  };
  private final Runnable mResizeRunnable = new Runnable()
  {
    public void run()
    {
      Rect localRect5 = null;
      synchronized (WindowManagerProxy.-get3(WindowManagerProxy.this))
      {
        WindowManagerProxy.-get8(WindowManagerProxy.this).set(WindowManagerProxy.-get3(WindowManagerProxy.this));
        WindowManagerProxy.-get9(WindowManagerProxy.this).set(WindowManagerProxy.-get5(WindowManagerProxy.this));
        WindowManagerProxy.-get10(WindowManagerProxy.this).set(WindowManagerProxy.-get4(WindowManagerProxy.this));
        WindowManagerProxy.-get11(WindowManagerProxy.this).set(WindowManagerProxy.-get7(WindowManagerProxy.this));
        WindowManagerProxy.-get12(WindowManagerProxy.this).set(WindowManagerProxy.-get6(WindowManagerProxy.this));
      }
      try
      {
        if (Build.DEBUG_ONEPLUS) {
          Log.i("WindowManagerProxy", "resizeDockedStack start");
        }
        IActivityManager localIActivityManager = ActivityManagerNative.getDefault();
        Rect localRect6 = WindowManagerProxy.-get8(WindowManagerProxy.this);
        Rect localRect2;
        label158:
        Rect localRect4;
        if (WindowManagerProxy.-get9(WindowManagerProxy.this).isEmpty())
        {
          ??? = null;
          if (!WindowManagerProxy.-get10(WindowManagerProxy.this).isEmpty()) {
            break label231;
          }
          localRect2 = null;
          if (!WindowManagerProxy.-get11(WindowManagerProxy.this).isEmpty()) {
            break label242;
          }
          localRect4 = null;
          label173:
          if (!WindowManagerProxy.-get12(WindowManagerProxy.this).isEmpty()) {
            break label253;
          }
        }
        for (;;)
        {
          localIActivityManager.resizeDockedStack(localRect6, ???, localRect2, localRect4, localRect5);
          if (Build.DEBUG_ONEPLUS) {
            Log.i("WindowManagerProxy", "resizeDockedStack end");
          }
          return;
          localObject = finally;
          throw ((Throwable)localObject);
          ??? = WindowManagerProxy.-get9(WindowManagerProxy.this);
          break;
          label231:
          Rect localRect3 = WindowManagerProxy.-get10(WindowManagerProxy.this);
          break label158;
          label242:
          localRect4 = WindowManagerProxy.-get11(WindowManagerProxy.this);
          break label173;
          label253:
          localRect5 = WindowManagerProxy.-get12(WindowManagerProxy.this);
        }
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("WindowManagerProxy", "Failed to resize stack: " + localRemoteException);
      }
    }
  };
  private final Runnable mSetTouchableRegionRunnable = new Runnable()
  {
    public void run()
    {
      try
      {
        synchronized (WindowManagerProxy.-get3(WindowManagerProxy.this))
        {
          WindowManagerProxy.-get8(WindowManagerProxy.this).set(WindowManagerProxy.-get13(WindowManagerProxy.this));
          WindowManagerGlobal.getWindowManagerService().setDockedStackDividerTouchRegion(WindowManagerProxy.-get8(WindowManagerProxy.this));
          return;
        }
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("WindowManagerProxy", "Failed to set touchable region: " + localRemoteException);
      }
    }
  };
  private final Runnable mSwapRunnable = new Runnable()
  {
    public void run()
    {
      try
      {
        ActivityManagerNative.getDefault().swapDockedAndFullscreenStack();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("WindowManagerProxy", "Failed to resize stack: " + localRemoteException);
      }
    }
  };
  private final Rect mTempDockedInsetRect = new Rect();
  private final Rect mTempDockedTaskRect = new Rect();
  private final Rect mTempOtherInsetRect = new Rect();
  private final Rect mTempOtherTaskRect = new Rect();
  private final Rect mTmpRect1 = new Rect();
  private final Rect mTmpRect2 = new Rect();
  private final Rect mTmpRect3 = new Rect();
  private final Rect mTmpRect4 = new Rect();
  private final Rect mTmpRect5 = new Rect();
  @GuardedBy("mDockedRect")
  private final Rect mTouchableRegion = new Rect();
  
  public static WindowManagerProxy getInstance()
  {
    return sInstance;
  }
  
  public void dismissDockedStack()
  {
    this.mExecutor.execute(this.mDismissRunnable);
  }
  
  public int getDockSide()
  {
    try
    {
      int i = WindowManagerGlobal.getWindowManagerService().getDockedStackSide();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("WindowManagerProxy", "Failed to get dock side: " + localRemoteException);
    }
    return -1;
  }
  
  public void maximizeDockedStack()
  {
    this.mExecutor.execute(this.mMaximizeRunnable);
  }
  
  public void resizeDockedStack(Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5)
  {
    for (;;)
    {
      synchronized (this.mDockedRect)
      {
        this.mDockedRect.set(paramRect1);
        if (paramRect2 != null)
        {
          this.mTempDockedTaskRect.set(paramRect2);
          if (paramRect3 != null)
          {
            this.mTempDockedInsetRect.set(paramRect3);
            if (paramRect4 == null) {
              break label112;
            }
            this.mTempOtherTaskRect.set(paramRect4);
            if (paramRect5 == null) {
              break label122;
            }
            this.mTempOtherInsetRect.set(paramRect5);
            this.mExecutor.execute(this.mResizeRunnable);
          }
        }
        else
        {
          this.mTempDockedTaskRect.setEmpty();
        }
      }
      this.mTempDockedInsetRect.setEmpty();
      continue;
      label112:
      this.mTempOtherTaskRect.setEmpty();
      continue;
      label122:
      this.mTempOtherInsetRect.setEmpty();
    }
  }
  
  public void setResizeDimLayer(boolean paramBoolean, int paramInt, float paramFloat)
  {
    this.mDimLayerVisible = paramBoolean;
    this.mDimLayerTargetStack = paramInt;
    this.mDimLayerAlpha = paramFloat;
    this.mExecutor.execute(this.mDimLayerRunnable);
  }
  
  public void setResizing(final boolean paramBoolean)
  {
    this.mExecutor.execute(new Runnable()
    {
      public void run()
      {
        try
        {
          WindowManagerGlobal.getWindowManagerService().setDockedStackResizing(paramBoolean);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          Log.w("WindowManagerProxy", "Error calling setDockedStackResizing: " + localRemoteException);
        }
      }
    });
  }
  
  public void setTouchRegion(Rect paramRect)
  {
    synchronized (this.mDockedRect)
    {
      this.mTouchableRegion.set(paramRect);
      this.mExecutor.execute(this.mSetTouchableRegionRunnable);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\stackdivider\WindowManagerProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */