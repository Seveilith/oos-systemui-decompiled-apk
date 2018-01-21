package com.android.systemui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import java.util.Map;

public final class Prefs
{
  private static SharedPreferences get(Context paramContext)
  {
    return paramContext.getSharedPreferences(paramContext.getPackageName(), 0);
  }
  
  public static Map<String, ?> getAll(Context paramContext)
  {
    return get(paramContext).getAll();
  }
  
  public static boolean getBoolean(Context paramContext, String paramString, boolean paramBoolean)
  {
    return get(paramContext).getBoolean(paramString, paramBoolean);
  }
  
  public static int getInt(Context paramContext, String paramString, int paramInt)
  {
    return get(paramContext).getInt(paramString, paramInt);
  }
  
  public static long getLong(Context paramContext, String paramString, long paramLong)
  {
    return get(paramContext).getLong(paramString, paramLong);
  }
  
  public static String getString(Context paramContext, String paramString1, String paramString2)
  {
    return get(paramContext).getString(paramString1, paramString2);
  }
  
  public static void putBoolean(Context paramContext, String paramString, boolean paramBoolean)
  {
    get(paramContext).edit().putBoolean(paramString, paramBoolean).apply();
  }
  
  public static void putInt(Context paramContext, String paramString, int paramInt)
  {
    get(paramContext).edit().putInt(paramString, paramInt).apply();
  }
  
  public static void putLong(Context paramContext, String paramString, long paramLong)
  {
    get(paramContext).edit().putLong(paramString, paramLong).apply();
  }
  
  public static void putString(Context paramContext, String paramString1, String paramString2)
  {
    get(paramContext).edit().putString(paramString1, paramString2).apply();
  }
  
  public static void registerListener(Context paramContext, SharedPreferences.OnSharedPreferenceChangeListener paramOnSharedPreferenceChangeListener)
  {
    get(paramContext).registerOnSharedPreferenceChangeListener(paramOnSharedPreferenceChangeListener);
  }
  
  public static void remove(Context paramContext, String paramString)
  {
    get(paramContext).edit().remove(paramString).apply();
  }
  
  public static void unregisterListener(Context paramContext, SharedPreferences.OnSharedPreferenceChangeListener paramOnSharedPreferenceChangeListener)
  {
    get(paramContext).unregisterOnSharedPreferenceChangeListener(paramOnSharedPreferenceChangeListener);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\Prefs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */