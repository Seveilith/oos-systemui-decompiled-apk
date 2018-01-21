package com.google.tagmanager;

import com.google.android.gms.common.util.VisibleForTesting;

final class Log
{
  @VisibleForTesting
  static Logger sLogger = new DefaultLogger();
  
  public static void e(String paramString)
  {
    sLogger.e(paramString);
  }
  
  public static void w(String paramString)
  {
    sLogger.w(paramString);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\google\tagmanager\Log.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */