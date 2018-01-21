package com.android.systemui.statusbar.policy;

import android.os.SystemProperties;

public class EncryptionHelper
{
  public static final boolean IS_DATA_ENCRYPTED = ;
  
  private static boolean isDataEncrypted()
  {
    String str = SystemProperties.get("vold.decrypt");
    if (!"1".equals(str)) {
      return "trigger_restart_min_framework".equals(str);
    }
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\EncryptionHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */