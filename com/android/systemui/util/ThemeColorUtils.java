package com.android.systemui.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.provider.Settings.System;

public class ThemeColorUtils
{
  public static int PROGRESS;
  public static int PROGRESS_BACKGROUND;
  public static int QS_ACCENT = 100;
  public static int QS_BUTTON_ACTIVE;
  public static int QS_DIALOG_BACKGROUND;
  public static int QS_DIALOG_TEXT;
  public static int QS_DISABLE_TEXT;
  public static int QS_EDIT_BOTTOM;
  public static int QS_EDIT_TEXT_BOTTOM;
  public static int QS_EDIT_TILE_BACKGROUND;
  public static int QS_ICON_ACTIVE;
  public static int QS_ICON_INACTIVE;
  public static int QS_POPUP_BACKGROUND;
  public static int QS_PRIMARY_TEXT;
  public static int QS_SECONDARY_TEXT;
  public static int QS_SWITCH_THUMB_DISABLED;
  public static int QS_SWITCH_THUMB_OFF;
  public static int QS_SWITCH_TRACK_DISABLED;
  public static int QS_SWITCH_TRACK_OFF;
  public static int QS_SYSTEM_PRIMARY;
  public static int QS_TILE_ICON;
  public static int QS_TILE_ICON_DISABLE;
  public static int QS_TILE_LABEL;
  public static int QS_TILE_LABEL_LIGHT;
  private static int sAccentColor;
  private static int[] sColors;
  private static int sCurrentTheme = -1;
  private static boolean sSpecialTheme = false;
  private static String[] sThemeName;
  
  static
  {
    QS_PRIMARY_TEXT = 0;
    QS_SECONDARY_TEXT = 1;
    QS_DISABLE_TEXT = 2;
    QS_TILE_ICON_DISABLE = 3;
    QS_SYSTEM_PRIMARY = 4;
    QS_EDIT_BOTTOM = 5;
    PROGRESS_BACKGROUND = 6;
    QS_EDIT_TILE_BACKGROUND = 7;
    QS_EDIT_TEXT_BOTTOM = 8;
    QS_TILE_ICON = 9;
    PROGRESS = 10;
    QS_POPUP_BACKGROUND = 11;
    QS_DIALOG_BACKGROUND = 12;
    QS_DIALOG_TEXT = 13;
    QS_SWITCH_THUMB_DISABLED = 14;
    QS_SWITCH_TRACK_DISABLED = 15;
    QS_SWITCH_THUMB_OFF = 16;
    QS_SWITCH_TRACK_OFF = 17;
    QS_TILE_LABEL = 18;
    QS_TILE_LABEL_LIGHT = 19;
    QS_ICON_ACTIVE = 20;
    QS_ICON_INACTIVE = 21;
    QS_BUTTON_ACTIVE = 22;
  }
  
  public static int getColor(int paramInt)
  {
    if ((paramInt == QS_TILE_ICON) || (paramInt == PROGRESS))
    {
      if (sCurrentTheme == 2) {
        return sColors[paramInt];
      }
      return sAccentColor;
    }
    if (paramInt == QS_ACCENT) {
      return sAccentColor & 0x89FFFFFF;
    }
    return sColors[paramInt];
  }
  
  public static int getPopTheme()
  {
    switch (sCurrentTheme)
    {
    default: 
      return 2131821036;
    case 0: 
      return 2131821034;
    case 1: 
      return 2131821035;
    }
    return 2131821036;
  }
  
  public static int getSpinnerLayout()
  {
    switch (sCurrentTheme)
    {
    default: 
      return 0;
    case 0: 
      return 2130968624;
    case 1: 
      return 2130968623;
    }
    return 2130968622;
  }
  
  public static void init(Context paramContext)
  {
    Resources localResources = paramContext.getResources();
    if (sCurrentTheme == -1) {
      sThemeName = localResources.getStringArray(2131427604);
    }
    boolean bool = Utils.isSpecialTheme(paramContext);
    int i = Utils.getThemeColor(paramContext);
    if ((sCurrentTheme != i) || (sSpecialTheme != bool))
    {
      sCurrentTheme = i;
      sSpecialTheme = bool;
      sColors = localResources.getIntArray(localResources.getIdentifier(sThemeName[sCurrentTheme], null, "com.android.systemui"));
      updateAccentColor(paramContext);
    }
  }
  
  public static void updateAccentColor(Context paramContext)
  {
    Object localObject = "#FF2196F3";
    if (sCurrentTheme == 0)
    {
      paramContext = Settings.System.getString(paramContext.getContentResolver(), "oem_white_mode_accent_color");
      localObject = paramContext;
      if (paramContext == null) {
        localObject = "#FF2196F3";
      }
    }
    while (sCurrentTheme != 1)
    {
      sAccentColor = Color.parseColor((String)localObject);
      return;
    }
    if (sSpecialTheme) {}
    for (paramContext = "#FFFF2837";; paramContext = Settings.System.getString(paramContext.getContentResolver(), "oem_black_mode_accent_color"))
    {
      localObject = paramContext;
      if (paramContext != null) {
        break;
      }
      localObject = "#FF42A5F5";
      break;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\util\ThemeColorUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */