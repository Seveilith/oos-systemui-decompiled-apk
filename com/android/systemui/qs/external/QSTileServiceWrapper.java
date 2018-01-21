package com.android.systemui.qs.external;

import android.os.IBinder;
import android.service.quicksettings.IQSTileService;
import android.util.Log;

public class QSTileServiceWrapper
{
  private final IQSTileService mService;
  
  public QSTileServiceWrapper(IQSTileService paramIQSTileService)
  {
    this.mService = paramIQSTileService;
  }
  
  public IBinder asBinder()
  {
    return this.mService.asBinder();
  }
  
  public boolean onClick(IBinder paramIBinder)
  {
    try
    {
      this.mService.onClick(paramIBinder);
      return true;
    }
    catch (Exception paramIBinder)
    {
      Log.d("IQSTileServiceWrapper", "Caught exception from TileService", paramIBinder);
    }
    return false;
  }
  
  public boolean onStartListening()
  {
    try
    {
      this.mService.onStartListening();
      return true;
    }
    catch (Exception localException)
    {
      Log.d("IQSTileServiceWrapper", "Caught exception from TileService", localException);
    }
    return false;
  }
  
  public boolean onStopListening()
  {
    try
    {
      this.mService.onStopListening();
      return true;
    }
    catch (Exception localException)
    {
      Log.d("IQSTileServiceWrapper", "Caught exception from TileService", localException);
    }
    return false;
  }
  
  public boolean onTileAdded()
  {
    try
    {
      this.mService.onTileAdded();
      return true;
    }
    catch (Exception localException)
    {
      Log.d("IQSTileServiceWrapper", "Caught exception from TileService", localException);
    }
    return false;
  }
  
  public boolean onTileRemoved()
  {
    try
    {
      this.mService.onTileRemoved();
      return true;
    }
    catch (Exception localException)
    {
      Log.d("IQSTileServiceWrapper", "Caught exception from TileService", localException);
    }
    return false;
  }
  
  public boolean onUnlockComplete()
  {
    try
    {
      this.mService.onUnlockComplete();
      return true;
    }
    catch (Exception localException)
    {
      Log.d("IQSTileServiceWrapper", "Caught exception from TileService", localException);
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\external\QSTileServiceWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */