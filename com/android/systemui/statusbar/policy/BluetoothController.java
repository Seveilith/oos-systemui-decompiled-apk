package com.android.systemui.statusbar.policy;

import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import java.util.Collection;

public abstract interface BluetoothController
{
  public abstract void addStateChangedCallback(Callback paramCallback);
  
  public abstract boolean canConfigBluetooth();
  
  public abstract void connect(CachedBluetoothDevice paramCachedBluetoothDevice);
  
  public abstract void disconnect(CachedBluetoothDevice paramCachedBluetoothDevice);
  
  public abstract int getBluetoothBatteryLevel();
  
  public abstract Collection<CachedBluetoothDevice> getDevices();
  
  public abstract String getLastDeviceName();
  
  public abstract boolean isBluetoothConnected();
  
  public abstract boolean isBluetoothConnecting();
  
  public abstract boolean isBluetoothEnabled();
  
  public abstract boolean isBluetoothSupported();
  
  public abstract void removeStateChangedCallback(Callback paramCallback);
  
  public abstract void setBluetoothEnabled(boolean paramBoolean);
  
  public static abstract interface Callback
  {
    public abstract void onBluetoothBatteryChanged();
    
    public abstract void onBluetoothDevicesChanged();
    
    public abstract void onBluetoothStateChange(boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\BluetoothController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */