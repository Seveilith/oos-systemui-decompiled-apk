package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import java.util.Iterator;
import java.util.List;

public final class HeadsetProfile
  implements LocalBluetoothProfile
{
  static final ParcelUuid[] UUIDS = { BluetoothUuid.HSP, BluetoothUuid.Handsfree };
  private static boolean V = true;
  private final CachedBluetoothDeviceManager mDeviceManager;
  private boolean mIsProfileReady;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final LocalBluetoothProfileManager mProfileManager;
  private BluetoothHeadset mService;
  
  HeadsetProfile(Context paramContext, LocalBluetoothAdapter paramLocalBluetoothAdapter, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, LocalBluetoothProfileManager paramLocalBluetoothProfileManager)
  {
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDeviceManager = paramCachedBluetoothDeviceManager;
    this.mProfileManager = paramLocalBluetoothProfileManager;
    this.mLocalAdapter.getProfileProxy(paramContext, new HeadsetServiceListener(null), 1);
  }
  
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null) {
      return false;
    }
    Object localObject = this.mService.getConnectedDevices();
    if (localObject != null)
    {
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)((Iterator)localObject).next();
        if (localBluetoothDevice.equals(paramBluetoothDevice))
        {
          Log.d("HeadsetProfile", "Not disconnecting device = " + localBluetoothDevice);
          return true;
        }
      }
    }
    return this.mService.connect(paramBluetoothDevice);
  }
  
  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null) {
      return false;
    }
    Object localObject = this.mService.getConnectedDevices();
    if (!((List)localObject).isEmpty())
    {
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        if (((BluetoothDevice)((Iterator)localObject).next()).equals(paramBluetoothDevice))
        {
          if (V) {
            Log.d("HeadsetProfile", "Downgrade priority as useris disconnecting the headset");
          }
          if (this.mService.getPriority(paramBluetoothDevice) > 100) {
            this.mService.setPriority(paramBluetoothDevice, 100);
          }
          return this.mService.disconnect(paramBluetoothDevice);
        }
      }
    }
    return false;
  }
  
  protected void finalize()
  {
    if (V) {
      Log.d("HeadsetProfile", "finalize()");
    }
    if (this.mService != null) {}
    try
    {
      BluetoothAdapter.getDefaultAdapter().closeProfileProxy(1, this.mService);
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("HeadsetProfile", "Error cleaning up HID proxy", localThrowable);
    }
  }
  
  public int getConnectionStatus(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null) {
      return 0;
    }
    Object localObject = this.mService.getConnectedDevices();
    if (!((List)localObject).isEmpty())
    {
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        if (((BluetoothDevice)((Iterator)localObject).next()).equals(paramBluetoothDevice)) {
          return this.mService.getConnectionState(paramBluetoothDevice);
        }
      }
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
    return "HEADSET";
  }
  
  private final class HeadsetServiceListener
    implements BluetoothProfile.ServiceListener
  {
    private HeadsetServiceListener() {}
    
    public void onServiceConnected(int paramInt, BluetoothProfile paramBluetoothProfile)
    {
      if (HeadsetProfile.-get0()) {
        Log.d("HeadsetProfile", "Bluetooth service connected");
      }
      HeadsetProfile.-set1(HeadsetProfile.this, (BluetoothHeadset)paramBluetoothProfile);
      List localList = HeadsetProfile.-get4(HeadsetProfile.this).getConnectedDevices();
      while (!localList.isEmpty())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)localList.remove(0);
        CachedBluetoothDevice localCachedBluetoothDevice = HeadsetProfile.-get1(HeadsetProfile.this).findDevice(localBluetoothDevice);
        paramBluetoothProfile = localCachedBluetoothDevice;
        if (localCachedBluetoothDevice == null)
        {
          Log.w("HeadsetProfile", "HeadsetProfile found new device: " + localBluetoothDevice);
          paramBluetoothProfile = HeadsetProfile.-get1(HeadsetProfile.this).addDevice(HeadsetProfile.-get2(HeadsetProfile.this), HeadsetProfile.-get3(HeadsetProfile.this), localBluetoothDevice);
        }
        paramBluetoothProfile.onProfileStateChanged(HeadsetProfile.this, 2);
        paramBluetoothProfile.refresh();
      }
      HeadsetProfile.-get3(HeadsetProfile.this).callServiceConnectedListeners();
      HeadsetProfile.-set0(HeadsetProfile.this, true);
    }
    
    public void onServiceDisconnected(int paramInt)
    {
      if (HeadsetProfile.-get0()) {
        Log.d("HeadsetProfile", "Bluetooth service disconnected");
      }
      HeadsetProfile.-get3(HeadsetProfile.this).callServiceDisconnectedListeners();
      HeadsetProfile.-set0(HeadsetProfile.this, false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\settingslib\bluetooth\HeadsetProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */