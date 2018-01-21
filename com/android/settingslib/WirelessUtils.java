package com.android.settingslib;

import android.content.Context;
import android.provider.Settings.Global;

public class WirelessUtils
{
  public static boolean isAirplaneModeOn(Context paramContext)
  {
    boolean bool = false;
    if (Settings.Global.getInt(paramContext.getContentResolver(), "airplane_mode_on", 0) != 0) {
      bool = true;
    }
    return bool;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\settingslib\WirelessUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */