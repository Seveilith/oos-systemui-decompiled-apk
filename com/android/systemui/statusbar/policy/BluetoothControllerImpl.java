package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.BluetoothEventManager;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class BluetoothControllerImpl
  extends BroadcastReceiver
  implements BluetoothController, BluetoothCallback, CachedBluetoothDevice.Callback
{
  private static final boolean DEBUG = Log.isLoggable("BluetoothController", 3);
  private int mBTHeadsetState = 0;
  private int mBatteryLevel = -1;
  private int mConnectionState = 0;
  private Context mContext = null;
  private final int mCurrentUser;
  private boolean mEnabled;
  private final H mHandler = new H(null);
  private CachedBluetoothDevice mLastDevice;
  private final LocalBluetoothManager mLocalBluetoothManager;
  private final Handler mReceiverHandler;
  private final Runnable mRegisterListeners = new Runnable()
  {
    public void run()
    {
      BluetoothControllerImpl.-wrap0(BluetoothControllerImpl.this);
    }
  };
  private int mState;
  private final Runnable mUnRegisterListeners = new Runnable()
  {
    public void run()
    {
      BluetoothControllerImpl.-wrap1(BluetoothControllerImpl.this);
    }
  };
  private final UserManager mUserManager;
  
  public BluetoothControllerImpl(Context paramContext, Looper paramLooper)
  {
    this.mLocalBluetoothManager = LocalBluetoothManager.getInstance(paramContext, null);
    if (this.mLocalBluetoothManager != null)
    {
      this.mLocalBluetoothManager.getEventManager().setReceiverHandler(new Handler(paramLooper));
      this.mLocalBluetoothManager.getEventManager().registerCallback(this);
      onBluetoothStateChanged(this.mLocalBluetoothManager.getBluetoothAdapter().getBluetoothState());
    }
    this.mUserManager = ((UserManager)paramContext.getSystemService("user"));
    this.mCurrentUser = ActivityManager.getCurrentUser();
    this.mContext = paramContext;
    this.mReceiverHandler = new Handler(paramLooper);
    this.mReceiverHandler.post(this.mRegisterListeners);
  }
  
  private String getDeviceString(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    return paramCachedBluetoothDevice.getName() + " " + paramCachedBluetoothDevice.getBondState() + " " + paramCachedBluetoothDevice.isConnected();
  }
  
  private void registerListeners()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.bluetooth.headset.action.ACTION_HF_BATTERY_LEVEL_CHANGED");
    localIntentFilter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
    if (this.mContext != null) {
      this.mContext.registerReceiver(this, localIntentFilter, null, this.mReceiverHandler);
    }
  }
  
  private static String stateToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN(" + paramInt + ")";
    case 2: 
      return "CONNECTED";
    case 1: 
      return "CONNECTING";
    case 0: 
      return "DISCONNECTED";
    }
    return "DISCONNECTING";
  }
  
  private void unregisterListeners()
  {
    if (this.mContext != null) {
      this.mContext.unregisterReceiver(this);
    }
  }
  
  private void updateConnected()
  {
    int i = this.mLocalBluetoothManager.getBluetoothAdapter().getConnectionState();
    if (i != this.mConnectionState)
    {
      this.mConnectionState = i;
      this.mHandler.sendEmptyMessage(2);
    }
    if ((this.mLastDevice != null) && (this.mLastDevice.isConnected())) {
      return;
    }
    this.mLastDevice = null;
    Iterator localIterator = getDevices().iterator();
    while (localIterator.hasNext())
    {
      CachedBluetoothDevice localCachedBluetoothDevice = (CachedBluetoothDevice)localIterator.next();
      if (localCachedBluetoothDevice.isConnected()) {
        this.mLastDevice = localCachedBluetoothDevice;
      }
    }
    if ((this.mLastDevice == null) && (this.mConnectionState == 2))
    {
      this.mConnectionState = 0;
      this.mHandler.sendEmptyMessage(2);
    }
  }
  
  public void addStateChangedCallback(BluetoothController.Callback paramCallback)
  {
    this.mHandler.obtainMessage(3, paramCallback).sendToTarget();
    this.mHandler.sendEmptyMessage(2);
  }
  
  public boolean canConfigBluetooth()
  {
    return !this.mUserManager.hasUserRestriction("no_config_bluetooth", UserHandle.of(this.mCurrentUser));
  }
  
  public void connect(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    if ((this.mLocalBluetoothManager == null) || (paramCachedBluetoothDevice == null)) {
      return;
    }
    paramCachedBluetoothDevice.connect(true);
  }
  
  public void disconnect(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    if ((this.mLocalBluetoothManager == null) || (paramCachedBluetoothDevice == null)) {
      return;
    }
    paramCachedBluetoothDevice.disconnect();
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("BluetoothController state:");
    paramPrintWriter.print("  mLocalBluetoothManager=");
    paramPrintWriter.println(this.mLocalBluetoothManager);
    if (this.mLocalBluetoothManager == null) {
      return;
    }
    paramPrintWriter.print("  mEnabled=");
    paramPrintWriter.println(this.mEnabled);
    paramPrintWriter.print("  mConnectionState=");
    paramPrintWriter.println(stateToString(this.mConnectionState));
    paramPrintWriter.print("  mLastDevice=");
    paramPrintWriter.println(this.mLastDevice);
    paramPrintWriter.print("  mCallbacks.size=");
    paramPrintWriter.println(H.-get0(this.mHandler).size());
    paramPrintWriter.println("  Bluetooth Devices:");
    paramFileDescriptor = this.mLocalBluetoothManager.getCachedDeviceManager().getCachedDevicesCopy().iterator();
    while (paramFileDescriptor.hasNext())
    {
      paramArrayOfString = (CachedBluetoothDevice)paramFileDescriptor.next();
      paramPrintWriter.println("    " + getDeviceString(paramArrayOfString));
    }
  }
  
  public int getBluetoothBatteryLevel()
  {
    return this.mBatteryLevel;
  }
  
  public Collection<CachedBluetoothDevice> getDevices()
  {
    Collection localCollection = null;
    if (this.mLocalBluetoothManager != null) {
      localCollection = this.mLocalBluetoothManager.getCachedDeviceManager().getCachedDevicesCopy();
    }
    return localCollection;
  }
  
  public String getLastDeviceName()
  {
    String str = null;
    if (this.mLastDevice != null) {
      str = this.mLastDevice.getName();
    }
    return str;
  }
  
  public boolean isBluetoothConnected()
  {
    return this.mConnectionState == 2;
  }
  
  public boolean isBluetoothConnecting()
  {
    return this.mConnectionState == 1;
  }
  
  public boolean isBluetoothEnabled()
  {
    return this.mEnabled;
  }
  
  public boolean isBluetoothSupported()
  {
    return this.mLocalBluetoothManager != null;
  }
  
  public void onBluetoothStateChanged(int paramInt)
  {
    boolean bool2 = true;
    Log.i("BluetoothController", "onBluetoothStateChanged:" + paramInt);
    boolean bool1 = bool2;
    if (paramInt != 12) {
      if (paramInt != 11) {
        break label64;
      }
    }
    label64:
    for (bool1 = bool2;; bool1 = false)
    {
      this.mEnabled = bool1;
      this.mState = paramInt;
      this.mHandler.sendEmptyMessage(2);
      return;
    }
  }
  
  public void onConnectionStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt)
  {
    this.mLastDevice = paramCachedBluetoothDevice;
    updateConnected();
    this.mConnectionState = paramInt;
    this.mHandler.sendEmptyMessage(2);
  }
  
  public void onDeviceAdded(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    paramCachedBluetoothDevice.registerCallback(this);
    updateConnected();
    this.mHandler.sendEmptyMessage(1);
  }
  
  public void onDeviceAttributesChanged()
  {
    updateConnected();
    this.mHandler.sendEmptyMessage(1);
  }
  
  public void onDeviceBondStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt)
  {
    updateConnected();
    this.mHandler.sendEmptyMessage(1);
  }
  
  public void onDeviceDeleted(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    updateConnected();
    this.mHandler.sendEmptyMessage(1);
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramContext = paramIntent.getAction();
    if (paramContext.equals("android.bluetooth.headset.action.ACTION_HF_BATTERY_LEVEL_CHANGED"))
    {
      this.mBatteryLevel = paramIntent.getIntExtra("android.bluetooth.headset.extra.EXTRA_HF_BATTERY_LEVEL", -1);
      Log.d("BluetoothController", "got battery level frome intent = " + this.mBatteryLevel);
    }
    do
    {
      this.mHandler.sendEmptyMessage(5);
      do
      {
        return;
      } while (!paramContext.equals("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED"));
      this.mBTHeadsetState = paramIntent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
      Log.d("BluetoothController", "BT headset state changed:" + this.mBTHeadsetState);
    } while (this.mBTHeadsetState != 0);
    this.mBatteryLevel = -1;
  }
  
  public void onScanningStateChanged(boolean paramBoolean) {}
  
  public void removeStateChangedCallback(BluetoothController.Callback paramCallback)
  {
    this.mHandler.obtainMessage(4, paramCallback).sendToTarget();
  }
  
  public void setBluetoothEnabled(boolean paramBoolean)
  {
    if (this.mLocalBluetoothManager != null) {
      this.mLocalBluetoothManager.getBluetoothAdapter().setBluetoothEnabled(paramBoolean);
    }
  }
  
  private final class H
    extends Handler
  {
    private final ArrayList<BluetoothController.Callback> mCallbacks = new ArrayList();
    
    private H() {}
    
    private void fireBatteryLevelChange()
    {
      Log.d("BluetoothController", "mEnabled = " + BluetoothControllerImpl.-get2(BluetoothControllerImpl.this) + " mConnectionState = " + BluetoothControllerImpl.-get1(BluetoothControllerImpl.this) + " mBatteryLevel = " + BluetoothControllerImpl.-get0(BluetoothControllerImpl.this));
      if ((BluetoothControllerImpl.-get2(BluetoothControllerImpl.this)) && (BluetoothControllerImpl.-get1(BluetoothControllerImpl.this) == 2) && (BluetoothControllerImpl.-get0(BluetoothControllerImpl.this) > -1))
      {
        Iterator localIterator = this.mCallbacks.iterator();
        while (localIterator.hasNext()) {
          ((BluetoothController.Callback)localIterator.next()).onBluetoothBatteryChanged();
        }
      }
    }
    
    private void firePairedDevicesChanged()
    {
      Iterator localIterator = this.mCallbacks.iterator();
      while (localIterator.hasNext()) {
        ((BluetoothController.Callback)localIterator.next()).onBluetoothDevicesChanged();
      }
    }
    
    private void fireStateChange()
    {
      Iterator localIterator = this.mCallbacks.iterator();
      while (localIterator.hasNext()) {
        fireStateChange((BluetoothController.Callback)localIterator.next());
      }
    }
    
    private void fireStateChange(BluetoothController.Callback paramCallback)
    {
      paramCallback.onBluetoothStateChange(BluetoothControllerImpl.-get2(BluetoothControllerImpl.this));
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        firePairedDevicesChanged();
        return;
      case 2: 
        fireStateChange();
        return;
      case 3: 
        this.mCallbacks.add((BluetoothController.Callback)paramMessage.obj);
        return;
      case 4: 
        this.mCallbacks.remove((BluetoothController.Callback)paramMessage.obj);
        return;
      }
      fireBatteryLevelChange();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\BluetoothControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */