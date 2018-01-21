package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.os.ParcelUuid;
import java.util.Set;

public final class LocalBluetoothAdapter
{
  private static LocalBluetoothAdapter sInstance;
  private final BluetoothAdapter mAdapter;
  private LocalBluetoothProfileManager mProfileManager;
  private int mState = Integer.MIN_VALUE;
  
  private LocalBluetoothAdapter(BluetoothAdapter paramBluetoothAdapter)
  {
    this.mAdapter = paramBluetoothAdapter;
  }
  
  static LocalBluetoothAdapter getInstance()
  {
    try
    {
      if (sInstance == null)
      {
        localObject1 = BluetoothAdapter.getDefaultAdapter();
        if (localObject1 != null) {
          sInstance = new LocalBluetoothAdapter((BluetoothAdapter)localObject1);
        }
      }
      Object localObject1 = sInstance;
      return (LocalBluetoothAdapter)localObject1;
    }
    finally {}
  }
  
  public void cancelDiscovery()
  {
    this.mAdapter.cancelDiscovery();
  }
  
  public boolean enable()
  {
    return this.mAdapter.enable();
  }
  
  public BluetoothLeScanner getBluetoothLeScanner()
  {
    return this.mAdapter.getBluetoothLeScanner();
  }
  
  public int getBluetoothState()
  {
    try
    {
      syncBluetoothState();
      int i = this.mState;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public Set<BluetoothDevice> getBondedDevices()
  {
    return this.mAdapter.getBondedDevices();
  }
  
  public int getConnectionState()
  {
    return this.mAdapter.getConnectionState();
  }
  
  void getProfileProxy(Context paramContext, BluetoothProfile.ServiceListener paramServiceListener, int paramInt)
  {
    this.mAdapter.getProfileProxy(paramContext, paramServiceListener, paramInt);
  }
  
  public int getState()
  {
    return this.mAdapter.getState();
  }
  
  public ParcelUuid[] getUuids()
  {
    return this.mAdapter.getUuids();
  }
  
  public boolean isDiscovering()
  {
    return this.mAdapter.isDiscovering();
  }
  
  public boolean setBluetoothEnabled(boolean paramBoolean)
  {
    boolean bool;
    if (paramBoolean)
    {
      bool = this.mAdapter.enable();
      if (!bool) {
        break label47;
      }
      if (!paramBoolean) {
        break label41;
      }
    }
    label41:
    for (int i = 11;; i = 13)
    {
      setBluetoothStateInt(i);
      return bool;
      bool = this.mAdapter.disable();
      break;
    }
    label47:
    syncBluetoothState();
    return bool;
  }
  
  void setBluetoothStateInt(int paramInt)
  {
    try
    {
      int i = this.mState;
      if (i == paramInt) {
        return;
      }
      this.mState = paramInt;
      if ((paramInt == 12) && (this.mProfileManager != null)) {
        this.mProfileManager.setBluetoothStateOn();
      }
      return;
    }
    finally {}
  }
  
  public void setName(String paramString)
  {
    this.mAdapter.setName(paramString);
  }
  
  void setProfileManager(LocalBluetoothProfileManager paramLocalBluetoothProfileManager)
  {
    this.mProfileManager = paramLocalBluetoothProfileManager;
  }
  
  boolean syncBluetoothState()
  {
    if (this.mAdapter.getState() != this.mState)
    {
      setBluetoothStateInt(this.mAdapter.getState());
      return true;
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\settingslib\bluetooth\LocalBluetoothAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */