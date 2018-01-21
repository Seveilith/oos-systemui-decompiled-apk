package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothInputDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Context;
import android.util.Log;
import java.util.List;

public final class HidProfile
  implements LocalBluetoothProfile
{
  private static boolean V = true;
  private final CachedBluetoothDeviceManager mDeviceManager;
  private boolean mIsProfileReady;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final LocalBluetoothProfileManager mProfileManager;
  private BluetoothInputDevice mService;
  
  HidProfile(Context paramContext, LocalBluetoothAdapter paramLocalBluetoothAdapter, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, LocalBluetoothProfileManager paramLocalBluetoothProfileManager)
  {
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDeviceManager = paramCachedBluetoothDeviceManager;
    this.mProfileManager = paramLocalBluetoothProfileManager;
    paramLocalBluetoothAdapter.getProfileProxy(paramContext, new InputDeviceServiceListener(null), 4);
  }
  
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null) {
      return false;
    }
    return this.mService.connect(paramBluetoothDevice);
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
      Log.d("HidProfile", "finalize()");
    }
    if (this.mService != null) {}
    try
    {
      BluetoothAdapter.getDefaultAdapter().closeProfileProxy(4, this.mService);
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("HidProfile", "Error cleaning up HID proxy", localThrowable);
    }
  }
  
  public int getConnectionStatus(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null) {
      return 0;
    }
    List localList = this.mService.getConnectedDevices();
    if ((!localList.isEmpty()) && (((BluetoothDevice)localList.get(0)).equals(paramBluetoothDevice))) {
      return this.mService.getConnectionState(paramBluetoothDevice);
    }
    return 0;
  }
  
  public boolean isAutoConnectable()
  {
    return true;
  }
  
  public boolean isConnectable()
  {
    return true;
  }
  
  public boolean isPreferred(BluetoothDevice paramBluetoothDevice)
  {
    boolean bool = false;
    if (this.mService == null) {
      return false;
    }
    if (this.mService.getPriority(paramBluetoothDevice) > 0) {
      bool = true;
    }
    return bool;
  }
  
  public void setPreferred(BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
  {
    if (this.mService == null) {
      return;
    }
    if (paramBoolean)
    {
      if (this.mService.getPriority(paramBluetoothDevice) < 100) {
        this.mService.setPriority(paramBluetoothDevice, 100);
      }
      return;
    }
    this.mService.setPriority(paramBluetoothDevice, 0);
  }
  
  public String toString()
  {
    return "HID";
  }
  
  private final class InputDeviceServiceListener
    implements BluetoothProfile.ServiceListener
  {
    private InputDeviceServiceListener() {}
    
    public void onServiceConnected(int paramInt, BluetoothProfile paramBluetoothProfile)
    {
      if (HidProfile.-get0()) {
        Log.d("HidProfile", "Bluetooth service connected");
      }
      HidProfile.-set1(HidProfile.this, (BluetoothInputDevice)paramBluetoothProfile);
      List localList = HidProfile.-get4(HidProfile.this).getConnectedDevices();
      while (!localList.isEmpty())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)localList.remove(0);
        CachedBluetoothDevice localCachedBluetoothDevice = HidProfile.-get1(HidProfile.this).findDevice(localBluetoothDevice);
        paramBluetoothProfile = localCachedBluetoothDevice;
        if (localCachedBluetoothDevice == null)
        {
          Log.w("HidProfile", "HidProfile found new device: " + localBluetoothDevice);
          paramBluetoothProfile = HidProfile.-get1(HidProfile.this).addDevice(HidProfile.-get2(HidProfile.this), HidProfile.-get3(HidProfile.this), localBluetoothDevice);
        }
        paramBluetoothProfile.onProfileStateChanged(HidProfile.this, 2);
        paramBluetoothProfile.refresh();
      }
      HidProfile.-set0(HidProfile.this, true);
    }
    
    public void onServiceDisconnected(int paramInt)
    {
      if (HidProfile.-get0()) {
        Log.d("HidProfile", "Bluetooth service disconnected");
      }
      HidProfile.-set0(HidProfile.this, false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\settingslib\bluetooth\HidProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */