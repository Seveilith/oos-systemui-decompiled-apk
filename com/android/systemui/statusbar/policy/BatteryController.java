package com.android.systemui.statusbar.policy;

import com.android.systemui.DemoMode;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public abstract interface BatteryController
  extends DemoMode
{
  public abstract void addStateChangedCallback(BatteryStateChangeCallback paramBatteryStateChangeCallback);
  
  public abstract void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString);
  
  public abstract boolean isPowerSave();
  
  public abstract void removeStateChangedCallback(BatteryStateChangeCallback paramBatteryStateChangeCallback);
  
  public abstract void setPowerSaveMode(boolean paramBoolean);
  
  public static abstract interface BatteryStateChangeCallback
  {
    public abstract void onBatteryLevelChanged(int paramInt, boolean paramBoolean1, boolean paramBoolean2);
    
    public abstract void onBatteryPercentShowChange(boolean paramBoolean);
    
    public abstract void onBatteryStyleChanged(int paramInt);
    
    public abstract void onFastChargeChanged(boolean paramBoolean);
    
    public abstract void onPowerSaveChanged(boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\BatteryController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */