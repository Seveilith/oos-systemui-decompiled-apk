package com.android.keyguard;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings.System;

public class Utils
{
  public static final boolean DEBUG_ONEPLUS = Build.DEBUG_ONEPLUS;
  
  public static int getThemeAccentColor(Context paramContext, int paramInt)
  {
    if (paramInt == 2) {
      return 0;
    }
    if (paramInt == 1) {}
    for (String str = "oem_black_mode_accent_color";; str = "oem_white_mode_accent_color")
    {
      paramContext = Settings.System.getString(paramContext.getContentResolver(), str);
      if (paramContext == null) {
        break;
      }
      return Color.parseColor(paramContext);
    }
    return 0;
  }
  
  public static int getThemeColor(Context paramContext)
  {
    return Settings.System.getInt(paramContext.getContentResolver(), "oem_black_mode", 0);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */