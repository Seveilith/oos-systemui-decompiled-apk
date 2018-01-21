package com.android.settingslib.bluetooth;

import android.content.Context;

public class Utils
{
  private static ErrorListener sErrorListener;
  
  public static void setErrorListener(ErrorListener paramErrorListener)
  {
    sErrorListener = paramErrorListener;
  }
  
  static void showError(Context paramContext, String paramString, int paramInt)
  {
    if (sErrorListener != null) {
      sErrorListener.onShowError(paramContext, paramString, paramInt);
    }
  }
  
  public static abstract interface ErrorListener
  {
    public abstract void onShowError(Context paramContext, String paramString, int paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\settingslib\bluetooth\Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */