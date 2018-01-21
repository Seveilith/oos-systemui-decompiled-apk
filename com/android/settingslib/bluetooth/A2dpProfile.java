package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class A2dpProfile
  implements LocalBluetoothProfile
{
  static final ParcelUuid[] SINK_UUIDS = { BluetoothUuid.AudioSink, BluetoothUuid.AdvAudioDist };
  private static boolean V = false;
  private final CachedBluetoothDeviceManager mDeviceManager;
  private boolean mIsProfileReady;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final LocalBluetoothProfileManager mProfileManager;
  private BluetoothA2dp mService;
  
  A2dpProfile(Context paramContext, LocalBluetoothAdapter paramLocalBluetoothAdapter, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, LocalBluetoothProfileManager paramLocalBluetoothProfileManager)
  {
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDeviceManager = paramCachedBluetoothDeviceManager;
    this.mProfileManager = paramLocalBluetoothProfileManager;
    this.mLocalAdapter.getProfileProxy(paramContext, new A2dpServiceListener(null), 2);
  }
  
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null) {
      return false;
    }
    Object localObject = getConnectedDevices();
    if (localObject != null)
    {
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)((Iterator)localObject).next();
        if (localBluetoothDevice.equals(paramBluetoothDevice))
        {
          Log.d("A2dpProfile", "Not disconnecting device = " + localBluetoothDevice);
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
            Log.d("A2dpProfile", "Downgrade priority as useris disconnecting the headset");
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
      Log.d("A2dpProfile", "finalize()");
    }
    if (this.mService != null) {}
    try
    {
      BluetoothAdapter.getDefaultAdapter().closeProfileProxy(2, this.mService);
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("A2dpProfile", "Error cleaning up A2DP proxy", localThrowable);
    }
  }
  
  public List<BluetoothDevice> getConnectedDevices()
  {
    if (this.mService == null) {
      return new ArrayList(0);
    }
    return this.mService.getDevicesMatchingConnectionStates(new int[] { 2, 1, 3 });
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
    return "A2DP";
  }
  
  private final class A2dpServiceListener
    implements BluetoothProfile.ServiceListener
  {
    private A2dpServiceListener() {}
    
    public void onServiceConnected(int paramInt, BluetoothProfile paramBluetoothProfile)
    {
      if (A2dpProfile.-get0()) {
        Log.d("A2dpProfile", "Bluetooth service connected");
      }
      A2dpProfile.-set1(A2dpProfile.this, (BluetoothA2dp)paramBluetoothProfile);
      List localList = A2dpProfile.-get4(A2dpProfile.this).getConnectedDevices();
      while (!localList.isEmpty())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)localList.remove(0);
        CachedBluetoothDevice localCachedBluetoothDevice = A2dpProfile.-get1(A2dpProfile.this).findDevice(localBluetoothDevice);
        paramBluetoothProfile = localCachedBluetoothDevice;
        if (localCachedBluetoothDevice == null)
        {
          Log.w("A2dpProfile", "A2dpProfile found new device: " + localBluetoothDevice);
          paramBluetoothProfile = A2dpProfile.-get1(A2dpProfile.this).addDevice(A2dpProfile.-get2(A2dpProfile.this), A2dpProfile.-get3(A2dpProfile.this), localBluetoothDevice);
        }
        paramBluetoothProfile.onProfileStateChanged(A2dpProfile.this, 2);
        paramBluetoothProfile.refresh();
      }
      A2dpProfile.-set0(A2dpProfile.this, true);
    }
    
    public void onServiceDisconnected(int paramInt)
    {
      if (A2dpProfile.-get0()) {
        Log.d("A2dpProfile", "Bluetooth service disconnected");
      }
      A2dpProfile.-set0(A2dpProfile.this, false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\settingslib\bluetooth\A2dpProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */