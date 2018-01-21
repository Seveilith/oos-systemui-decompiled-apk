package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothPbap;
import android.bluetooth.BluetoothPbap.ServiceListener;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

public final class PbapServerProfile
  implements LocalBluetoothProfile
{
  static final ParcelUuid[] PBAB_CLIENT_UUIDS = { BluetoothUuid.HSP, BluetoothUuid.Handsfree, BluetoothUuid.PBAP_PCE };
  private static boolean V = true;
  private boolean mIsProfileReady;
  private BluetoothPbap mService;
  
  PbapServerProfile(Context paramContext)
  {
    new BluetoothPbap(paramContext, new PbapServiceListener(null));
  }
  
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    return false;
  }
  
  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    if ((this.mService == null) || (paramBluetoothDevice == null)) {
      return false;
    }
    if (getConnectionStatus(paramBluetoothDevice) == 2) {
      return this.mService.disconnect();
    }
    Log.d("PbapServerProfile", "pbap server not connected to " + paramBluetoothDevice.getAddress());
    return false;
  }
  
  protected void finalize()
  {
    if (V) {
      Log.d("PbapServerProfile", "finalize()");
    }
    if (this.mService != null) {}
    try
    {
      this.mService.close();
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("PbapServerProfile", "Error cleaning up PBAP proxy", localThrowable);
    }
  }
  
  public int getConnectionStatus(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null) {
      return 0;
    }
    if (this.mService.isConnected(paramBluetoothDevice)) {
      return 2;
    }
    return 0;
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
    return false;
  }
  
  public void setPreferred(BluetoothDevice paramBluetoothDevice, boolean paramBoolean) {}
  
  public String toString()
  {
    return "PBAP Server";
  }
  
  private final class PbapServiceListener
    implements BluetoothPbap.ServiceListener
  {
    private PbapServiceListener() {}
    
    public void onServiceConnected(BluetoothPbap paramBluetoothPbap)
    {
      if (PbapServerProfile.-get0()) {
        Log.d("PbapServerProfile", "Bluetooth service connected");
      }
      PbapServerProfile.-set1(PbapServerProfile.this, paramBluetoothPbap);
      PbapServerProfile.-set0(PbapServerProfile.this, true);
    }
    
    public void onServiceDisconnected()
    {
      if (PbapServerProfile.-get0()) {
        Log.d("PbapServerProfile", "Bluetooth service disconnected");
      }
      PbapServerProfile.-set0(PbapServerProfile.this, false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\settingslib\bluetooth\PbapServerProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */