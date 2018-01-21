package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothDun;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Context;
import android.util.Log;

final class DunServerProfile
  implements LocalBluetoothProfile
{
  private static boolean V = true;
  private boolean mIsProfileReady;
  private BluetoothDun mService;
  
  DunServerProfile(Context paramContext)
  {
    BluetoothAdapter.getDefaultAdapter().getProfileProxy(paramContext, new DunServiceListener(null), 21);
  }
  
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    return false;
  }
  
  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null) {
      return false;
    }
    return this.mService.disconnect(paramBluetoothDevice);
  }
  
  protected void finalize()
  {
    if (V) {
      Log.d("DunServerProfile", "finalize()");
    }
    if (this.mService != null) {}
    try
    {
      BluetoothAdapter.getDefaultAdapter().closeProfileProxy(21, this.mService);
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("DunServerProfile", "Error cleaning up DUN proxy", localThrowable);
    }
  }
  
  public int getConnectionStatus(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null) {
      return 0;
    }
    return this.mService.getConnectionState(paramBluetoothDevice);
  }
  
  public boolean isAutoConnectable()
  {
    return false;
  }
  
  public boolean isConnectable()
  {
    return true;
  }
  
  public boolean isPreferred(BluetoothDevice paramBluetoothDevice)
  {
    return true;
  }
  
  public void setPreferred(BluetoothDevice paramBluetoothDevice, boolean paramBoolean) {}
  
  public String toString()
  {
    return "DUN Server";
  }
  
  private final class DunServiceListener
    implements BluetoothProfile.ServiceListener
  {
    private DunServiceListener() {}
    
    public void onServiceConnected(int paramInt, BluetoothProfile paramBluetoothProfile)
    {
      if (DunServerProfile.-get0()) {
        Log.d("DunServerProfile", "Bluetooth service connected");
      }
      DunServerProfile.-set1(DunServerProfile.this, (BluetoothDun)paramBluetoothProfile);
      DunServerProfile.-set0(DunServerProfile.this, true);
    }
    
    public void onServiceDisconnected(int paramInt)
    {
      if (DunServerProfile.-get0()) {
        Log.d("DunServerProfile", "Bluetooth service disconnected");
      }
      DunServerProfile.-set0(DunServerProfile.this, false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\settingslib\bluetooth\DunServerProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */