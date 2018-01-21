package com.google.tagmanager;

import android.util.Log;

class DefaultLogger
  implements Logger
{
  private Logger.LogLevel mLogLevel = Logger.LogLevel.WARNING;
  
  public void e(String paramString)
  {
    if (this.mLogLevel.ordinal() > Logger.LogLevel.ERROR.ordinal()) {
      return;
    }
    Log.e("GoogleTagManager", paramString);
  }
  
  public void w(String paramString)
  {
    if (this.mLogLevel.ordinal() > Logger.LogLevel.WARNING.ordinal()) {
      return;
    }
    Log.w("GoogleTagManager", paramString);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\google\tagmanager\DefaultLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */