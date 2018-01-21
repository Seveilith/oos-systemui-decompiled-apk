package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraManager.TorchCallback;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FlashlightController
{
  private static final boolean DEBUG = Log.isLoggable("FlashlightController", 3);
  private String mCameraId;
  private final CameraManager mCameraManager;
  private final Context mContext;
  private boolean mFlashlightEnabled;
  private Handler mHandler;
  private final ArrayList<WeakReference<FlashlightListener>> mListeners = new ArrayList(1);
  private boolean mTorchAvailable;
  private final CameraManager.TorchCallback mTorchCallback = new CameraManager.TorchCallback()
  {
    private void setCameraAvailable(boolean paramAnonymousBoolean)
    {
      synchronized (FlashlightController.this)
      {
        if (FlashlightController.-get3(FlashlightController.this) != paramAnonymousBoolean)
        {
          i = 1;
          FlashlightController.-set1(FlashlightController.this, paramAnonymousBoolean);
          if (i != 0)
          {
            if (FlashlightController.-get0()) {
              Log.d("FlashlightController", "dispatchAvailabilityChanged(" + paramAnonymousBoolean + ")");
            }
            FlashlightController.-wrap0(FlashlightController.this, paramAnonymousBoolean);
          }
          return;
        }
        int i = 0;
      }
    }
    
    private void setTorchMode(boolean paramAnonymousBoolean)
    {
      synchronized (FlashlightController.this)
      {
        if (FlashlightController.-get2(FlashlightController.this) != paramAnonymousBoolean)
        {
          i = 1;
          FlashlightController.-set0(FlashlightController.this, paramAnonymousBoolean);
          if (i != 0)
          {
            if (FlashlightController.-get0()) {
              Log.d("FlashlightController", "dispatchModeChanged(" + paramAnonymousBoolean + ")");
            }
            FlashlightController.-wrap1(FlashlightController.this, paramAnonymousBoolean);
          }
          return;
        }
        int i = 0;
      }
    }
    
    public void onTorchModeChanged(String paramAnonymousString, boolean paramAnonymousBoolean)
    {
      if (TextUtils.equals(paramAnonymousString, FlashlightController.-get1(FlashlightController.this)))
      {
        setCameraAvailable(true);
        setTorchMode(paramAnonymousBoolean);
      }
    }
    
    public void onTorchModeUnavailable(String paramAnonymousString)
    {
      if (TextUtils.equals(paramAnonymousString, FlashlightController.-get1(FlashlightController.this))) {
        setCameraAvailable(false);
      }
    }
  };
  
  public FlashlightController(Context paramContext)
  {
    this.mContext = paramContext;
    this.mCameraManager = ((CameraManager)this.mContext.getSystemService("camera"));
    tryInitCamera();
  }
  
  private void cleanUpListenersLocked(FlashlightListener paramFlashlightListener)
  {
    int i = this.mListeners.size() - 1;
    while (i >= 0)
    {
      FlashlightListener localFlashlightListener = (FlashlightListener)((WeakReference)this.mListeners.get(i)).get();
      if ((localFlashlightListener == null) || (localFlashlightListener == paramFlashlightListener)) {
        this.mListeners.remove(i);
      }
      i -= 1;
    }
  }
  
  private void dispatchAvailabilityChanged(boolean paramBoolean)
  {
    dispatchListeners(2, paramBoolean);
  }
  
  private void dispatchError()
  {
    dispatchListeners(1, false);
  }
  
  private void dispatchListeners(int paramInt, boolean paramBoolean)
  {
    int j;
    int i;
    synchronized (this.mListeners)
    {
      int m = this.mListeners.size();
      j = 0;
      i = 0;
      if (i >= m) {
        break label123;
      }
      FlashlightListener localFlashlightListener = (FlashlightListener)((WeakReference)this.mListeners.get(i)).get();
      if (localFlashlightListener == null) {
        break label148;
      }
      if (paramInt == 0)
      {
        localFlashlightListener.onFlashlightError();
        k = j;
      }
      else if (paramInt == 1)
      {
        localFlashlightListener.onFlashlightChanged(paramBoolean);
        k = j;
      }
    }
    int k = j;
    if (paramInt == 2)
    {
      ((FlashlightListener)localObject).onFlashlightAvailabilityChanged(paramBoolean);
      k = j;
      break label137;
      label123:
      if (j != 0) {
        cleanUpListenersLocked(null);
      }
      return;
    }
    for (;;)
    {
      label137:
      i += 1;
      j = k;
      break;
      label148:
      k = 1;
    }
  }
  
  private void dispatchModeChanged(boolean paramBoolean)
  {
    dispatchListeners(1, paramBoolean);
  }
  
  private void ensureHandler()
  {
    try
    {
      if (this.mHandler == null)
      {
        HandlerThread localHandlerThread = new HandlerThread("FlashlightController", 10);
        localHandlerThread.start();
        this.mHandler = new Handler(localHandlerThread.getLooper());
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private String getCameraId()
    throws CameraAccessException
  {
    String[] arrayOfString = this.mCameraManager.getCameraIdList();
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      String str = arrayOfString[i];
      Object localObject = this.mCameraManager.getCameraCharacteristics(str);
      Boolean localBoolean = (Boolean)((CameraCharacteristics)localObject).get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
      localObject = (Integer)((CameraCharacteristics)localObject).get(CameraCharacteristics.LENS_FACING);
      if ((localBoolean != null) && (localBoolean.booleanValue()) && (localObject != null) && (((Integer)localObject).intValue() == 1)) {
        return str;
      }
      i += 1;
    }
    return null;
  }
  
  private void tryInitCamera()
  {
    try
    {
      this.mCameraId = getCameraId();
      if (this.mCameraId != null)
      {
        ensureHandler();
        this.mCameraManager.registerTorchCallback(this.mTorchCallback, this.mHandler);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.e("FlashlightController", "Couldn't initialize.", localThrowable);
    }
  }
  
  public void addListener(FlashlightListener paramFlashlightListener)
  {
    synchronized (this.mListeners)
    {
      if (this.mCameraId == null) {
        tryInitCamera();
      }
      cleanUpListenersLocked(paramFlashlightListener);
      this.mListeners.add(new WeakReference(paramFlashlightListener));
      return;
    }
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("FlashlightController state:");
    paramPrintWriter.print("  mCameraId=");
    paramPrintWriter.println(this.mCameraId);
    paramPrintWriter.print("  mFlashlightEnabled=");
    paramPrintWriter.println(this.mFlashlightEnabled);
    paramPrintWriter.print("  mTorchAvailable=");
    paramPrintWriter.println(this.mTorchAvailable);
  }
  
  public boolean hasFlashlight()
  {
    return this.mContext.getPackageManager().hasSystemFeature("android.hardware.camera.flash");
  }
  
  public boolean isAvailable()
  {
    try
    {
      boolean bool = this.mTorchAvailable;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean isEnabled()
  {
    try
    {
      boolean bool = this.mFlashlightEnabled;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void removeListener(FlashlightListener paramFlashlightListener)
  {
    synchronized (this.mListeners)
    {
      cleanUpListenersLocked(paramFlashlightListener);
      return;
    }
  }
  
  public void setFlashlight(boolean paramBoolean)
  {
    int j = 0;
    int i;
    try
    {
      String str = this.mCameraId;
      if (str == null) {
        return;
      }
      i = j;
      if (this.mFlashlightEnabled != paramBoolean) {
        this.mFlashlightEnabled = paramBoolean;
      }
      try
      {
        this.mCameraManager.setTorchMode(this.mCameraId, paramBoolean);
        i = j;
      }
      catch (CameraAccessException localCameraAccessException)
      {
        for (;;)
        {
          Log.e("FlashlightController", "Couldn't set torch mode", localCameraAccessException);
          this.mFlashlightEnabled = false;
          i = 1;
        }
      }
      dispatchModeChanged(this.mFlashlightEnabled);
      if (i != 0) {
        dispatchError();
      }
      return;
    }
    finally {}
  }
  
  public static abstract interface FlashlightListener
  {
    public abstract void onFlashlightAvailabilityChanged(boolean paramBoolean);
    
    public abstract void onFlashlightChanged(boolean paramBoolean);
    
    public abstract void onFlashlightError();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\FlashlightController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */