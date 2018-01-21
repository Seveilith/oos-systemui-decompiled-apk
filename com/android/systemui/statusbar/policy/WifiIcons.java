package com.android.systemui.statusbar.policy;

public class WifiIcons
{
  public static final int[][] QS_WIFI_SIGNAL_STRENGTH;
  static final int WIFI_LEVEL_COUNT = WIFI_SIGNAL_STRENGTH[0].length;
  static final int[][] WIFI_SIGNAL_STRENGTH = { { 2130839007, 2130839009, 2130839011, 2130839013, 2130839015 }, { 2130839008, 2130839010, 2130839012, 2130839014, 2130839016 } };
  
  static
  {
    int[] arrayOfInt = { 2130837855, 2130837856, 2130837857, 2130837858, 2130837859 };
    QS_WIFI_SIGNAL_STRENGTH = new int[][] { { 2130837847, 2130837848, 2130837849, 2130837850, 2130837851 }, arrayOfInt };
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\WifiIcons.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */