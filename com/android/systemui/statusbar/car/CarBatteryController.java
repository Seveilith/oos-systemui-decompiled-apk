package com.android.systemui.statusbar.car;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CarBatteryController
  extends BroadcastReceiver
  implements BatteryController
{
  private final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
  private BatteryViewHandler mBatteryViewHandler;
  private BluetoothHeadsetClient mBluetoothHeadsetClient;
  private final ArrayList<BatteryController.BatteryStateChangeCallback> mChangeCallbacks = new ArrayList();
  private final Context mContext;
  private final BluetoothProfile.ServiceListener mHfpServiceListener = new BluetoothProfile.ServiceListener()
  {
    public void onServiceConnected(int paramAnonymousInt, BluetoothProfile paramAnonymousBluetoothProfile)
    {
      if (paramAnonymousInt == 16) {
        CarBatteryController.-set0(CarBatteryController.this, (BluetoothHeadsetClient)paramAnonymousBluetoothProfile);
      }
    }
    
    public void onServiceDisconnected(int paramAnonymousInt)
    {
      if (paramAnonymousInt == 16) {
        CarBatteryController.-set0(CarBatteryController.this, null);
      }
    }
  };
  private int mLevel;
  
  public CarBatteryController(Context paramContext)
  {
    this.mContext = paramContext;
    this.mAdapter.getProfileProxy(paramContext.getApplicationContext(), this.mHfpServiceListener, 16);
  }
  
  private void notifyBatteryLevelChanged()
  {
    int i = 0;
    int j = this.mChangeCallbacks.size();
    while (i < j)
    {
      ((BatteryController.BatteryStateChangeCallback)this.mChangeCallbacks.get(i)).onBatteryLevelChanged(this.mLevel, false, false);
      i += 1;
    }
  }
  
  private void updateBatteryIcon(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    if (paramInt == 2)
    {
      if (Log.isLoggable("CarBatteryController", 3)) {
        Log.d("CarBatteryController", "Device connected");
      }
      if (this.mBatteryViewHandler != null) {
        this.mBatteryViewHandler.showBatteryView();
      }
      if ((this.mBluetoothHeadsetClient == null) || (paramBluetoothDevice == null)) {
        return;
      }
      paramBluetoothDevice = this.mBluetoothHeadsetClient.getCurrentAgEvents(paramBluetoothDevice);
      if (paramBluetoothDevice == null) {
        return;
      }
      updateBatteryLevel(paramBluetoothDevice.getInt("android.bluetooth.headsetclient.extra.BATTERY_LEVEL", -1));
    }
    do
    {
      do
      {
        return;
      } while (paramInt != 0);
      if (Log.isLoggable("CarBatteryController", 3)) {
        Log.d("CarBatteryController", "Device disconnected");
      }
    } while (this.mBatteryViewHandler == null);
    this.mBatteryViewHandler.hideBatteryView();
  }
  
  private void updateBatteryLevel(int paramInt)
  {
    if (paramInt == -1)
    {
      if (Log.isLoggable("CarBatteryController", 3)) {
        Log.d("CarBatteryController", "Battery level invalid. Ignoring.");
      }
      return;
    }
    switch (paramInt)
    {
    default: 
      this.mLevel = 0;
    }
    for (;;)
    {
      if (Log.isLoggable("CarBatteryController", 3)) {
        Log.d("CarBatteryController", "Battery level: " + paramInt + "; setting mLevel as: " + this.mLevel);
      }
      notifyBatteryLevelChanged();
      return;
      this.mLevel = 100;
      continue;
      this.mLevel = 87;
      continue;
      this.mLevel = 63;
      continue;
      this.mLevel = 28;
      continue;
      this.mLevel = 12;
    }
  }
  
  public void addBatteryViewHandler(BatteryViewHandler paramBatteryViewHandler)
  {
    this.mBatteryViewHandler = paramBatteryViewHandler;
  }
  
  public void addStateChangedCallback(BatteryController.BatteryStateChangeCallback paramBatteryStateChangeCallback)
  {
    this.mChangeCallbacks.add(paramBatteryStateChangeCallback);
    paramBatteryStateChangeCallback.onBatteryLevelChanged(this.mLevel, false, false);
    paramBatteryStateChangeCallback.onPowerSaveChanged(false);
  }
  
  public void dispatchDemoCommand(String paramString, Bundle paramBundle) {}
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("CarBatteryController state:");
    paramPrintWriter.print("    mLevel=");
    paramPrintWriter.println(this.mLevel);
  }
  
  public boolean isPowerSave()
  {
    return false;
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramContext = paramIntent.getAction();
    if (Log.isLoggable("CarBatteryController", 3)) {
      Log.d("CarBatteryController", "onReceive(). action: " + paramContext);
    }
    if ("android.bluetooth.headsetclient.profile.action.AG_EVENT".equals(paramContext))
    {
      if (Log.isLoggable("CarBatteryController", 3)) {
        Log.d("CarBatteryController", "Received ACTION_AG_EVENT");
      }
      i = paramIntent.getIntExtra("android.bluetooth.headsetclient.extra.BATTERY_LEVEL", -1);
      updateBatteryLevel(i);
      if ((i != -1) && (this.mBatteryViewHandler != null)) {
        this.mBatteryViewHandler.showBatteryView();
      }
    }
    while (!"android.bluetooth.headsetclient.profile.action.CONNECTION_STATE_CHANGED".equals(paramContext)) {
      return;
    }
    int i = paramIntent.getIntExtra("android.bluetooth.profile.extra.STATE", -1);
    if (Log.isLoggable("CarBatteryController", 3))
    {
      int j = paramIntent.getIntExtra("android.bluetooth.profile.extra.PREVIOUS_STATE", -1);
      Log.d("CarBatteryController", "ACTION_CONNECTION_STATE_CHANGED event: " + j + " -> " + i);
    }
    updateBatteryIcon((BluetoothDevice)paramIntent.getExtra("android.bluetooth.device.extra.DEVICE"), i);
  }
  
  public void removeStateChangedCallback(BatteryController.BatteryStateChangeCallback paramBatteryStateChangeCallback)
  {
    this.mChangeCallbacks.remove(paramBatteryStateChangeCallback);
  }
  
  public void setPowerSaveMode(boolean paramBoolean) {}
  
  public void startListening()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.bluetooth.headsetclient.profile.action.CONNECTION_STATE_CHANGED");
    localIntentFilter.addAction("android.bluetooth.headsetclient.profile.action.AG_EVENT");
    this.mContext.registerReceiver(this, localIntentFilter);
  }
  
  public void stopListening()
  {
    this.mContext.unregisterReceiver(this);
  }
  
  public static abstract interface BatteryViewHandler
  {
    public abstract void hideBatteryView();
    
    public abstract void showBatteryView();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\car\CarBatteryController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */