package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothDevice;

final class OppProfile
  implements LocalBluetoothProfile
{
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    return false;
  }
  
  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    return false;
  }
  
  public int getConnectionStatus(BluetoothDevice paramBluetoothDevice)
  {
    return 0;
  }
  
  public boolean isAutoConnectable()
  {
    return false;
  }
  
  public boolean isConnectable()
  {
    return false;
  }
  
  public boolean isPreferred(BluetoothDevice paramBluetoothDevice)
  {
    return false;
  }
  
  public void setPreferred(BluetoothDevice paramBluetoothDevice, boolean paramBoolean) {}
  
  public String toString()
  {
    return "OPP";
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\settingslib\bluetooth\OppProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */