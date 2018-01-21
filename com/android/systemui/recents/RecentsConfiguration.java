package com.android.systemui.recents;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import com.android.systemui.recents.misc.SystemServicesProxy;

public class RecentsConfiguration
{
  public boolean fakeShadows;
  public final boolean isLargeScreen;
  public final boolean isXLargeScreen;
  public RecentsActivityLaunchState mLaunchState = new RecentsActivityLaunchState();
  public final int smallestWidth;
  public int svelteLevel;
  
  public RecentsConfiguration(Context paramContext)
  {
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    Resources localResources = paramContext.getApplicationContext().getResources();
    this.fakeShadows = localResources.getBoolean(2131558408);
    this.svelteLevel = localResources.getInteger(2131624000);
    float f = paramContext.getResources().getDisplayMetrics().density;
    this.smallestWidth = localSystemServicesProxy.getDeviceSmallestWidth();
    if (this.smallestWidth >= (int)(600.0F * f))
    {
      bool1 = true;
      this.isLargeScreen = bool1;
      if (this.smallestWidth < (int)(720.0F * f)) {
        break label119;
      }
    }
    label119:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.isXLargeScreen = bool1;
      return;
      bool1 = false;
      break;
    }
  }
  
  public RecentsActivityLaunchState getLaunchState()
  {
    return this.mLaunchState;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\RecentsConfiguration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */