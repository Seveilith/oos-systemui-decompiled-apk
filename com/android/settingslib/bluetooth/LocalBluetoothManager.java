package com.android.settingslib.bluetooth;

import android.content.Context;

public final class LocalBluetoothManager
{
  private static LocalBluetoothManager sInstance;
  private final CachedBluetoothDeviceManager mCachedDeviceManager;
  private final Context mContext;
  private final BluetoothEventManager mEventManager;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final LocalBluetoothProfileManager mProfileManager;
  
  private LocalBluetoothManager(LocalBluetoothAdapter paramLocalBluetoothAdapter, Context paramContext)
  {
    this.mContext = paramContext;
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mCachedDeviceManager = new CachedBluetoothDeviceManager(paramContext, this);
    this.mEventManager = new BluetoothEventManager(this.mLocalAdapter, this.mCachedDeviceManager, paramContext);
    this.mProfileManager = new LocalBluetoothProfileManager(paramContext, this.mLocalAdapter, this.mCachedDeviceManager, this.mEventManager);
  }
  
  public static LocalBluetoothManager getInstance(Context paramContext, BluetoothManagerCallback paramBluetoothManagerCallback)
  {
    try
    {
      if (sInstance == null)
      {
        LocalBluetoothAdapter localLocalBluetoothAdapter = LocalBluetoothAdapter.getInstance();
        if (localLocalBluetoothAdapter == null) {
          return null;
        }
        paramContext = paramContext.getApplicationContext();
        sInstance = new LocalBluetoothManager(localLocalBluetoothAdapter, paramContext);
        if (paramBluetoothManagerCallback != null) {
          paramBluetoothManagerCallback.onBluetoothManagerInitialized(paramContext, sInstance);
        }
      }
      paramContext = sInstance;
      return paramContext;
    }
    finally {}
  }
  
  public LocalBluetoothAdapter getBluetoothAdapter()
  {
    return this.mLocalAdapter;
  }
  
  public CachedBluetoothDeviceManager getCachedDeviceManager()
  {
    return this.mCachedDeviceManager;
  }
  
  public BluetoothEventManager getEventManager()
  {
    return this.mEventManager;
  }
  
  public LocalBluetoothProfileManager getProfileManager()
  {
    return this.mProfileManager;
  }
  
  public static abstract interface BluetoothManagerCallback
  {
    public abstract void onBluetoothManagerInitialized(Context paramContext, LocalBluetoothManager paramLocalBluetoothManager);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\settingslib\bluetooth\LocalBluetoothManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */