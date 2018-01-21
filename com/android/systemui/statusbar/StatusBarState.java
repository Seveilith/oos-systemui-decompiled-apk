package com.android.systemui.statusbar;

public class StatusBarState
{
  public static String toShortString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "bad_value_" + paramInt;
    case 0: 
      return "SHD";
    case 2: 
      return "SHD_LCK";
    case 1: 
      return "KGRD";
    }
    return "FS_USRSW";
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\StatusBarState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */