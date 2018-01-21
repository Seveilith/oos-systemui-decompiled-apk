package com.android.systemui.volume;

import android.content.res.Configuration;
import com.android.systemui.DemoMode;
import com.android.systemui.statusbar.policy.ZenModeController;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public abstract interface VolumeComponent
  extends DemoMode
{
  public abstract void dismissNow();
  
  public abstract void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString);
  
  public abstract ZenModeController getZenController();
  
  public abstract void onConfigurationChanged(Configuration paramConfiguration);
  
  public abstract void register();
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\volume\VolumeComponent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */