package com.android.systemui;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.IBinder;
import android.os.SystemProperties;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class SystemUIService
  extends Service
{
  private final String TAG = "SystemUIService";
  private int mFontConfig = 0;
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    int i = 0;
    SystemUI[] arrayOfSystemUI = ((SystemUIApplication)getApplication()).getServices();
    int j;
    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
      j = arrayOfSystemUI.length;
    }
    while (i < j)
    {
      Object localObject = arrayOfSystemUI[i];
      paramPrintWriter.println("dumping service: " + ((SystemUI)localObject).getClass().getName());
      ((SystemUI)localObject).dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      i += 1;
      continue;
      localObject = paramArrayOfString[0];
      j = arrayOfSystemUI.length;
      i = 0;
      while (i < j)
      {
        SystemUI localSystemUI = arrayOfSystemUI[i];
        if (localSystemUI.getClass().getName().endsWith((String)localObject)) {
          localSystemUI.dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        }
        i += 1;
      }
    }
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return null;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    if (paramConfiguration.oneplusfont != this.mFontConfig)
    {
      Typeface.changeFont(paramConfiguration.oneplusfont);
      this.mFontConfig = paramConfiguration.oneplusfont;
    }
  }
  
  public void onCreate()
  {
    super.onCreate();
    ((SystemUIApplication)getApplication()).startServicesIfNeeded();
    int i = 1;
    try
    {
      int j = SystemProperties.getInt("persist.sys.font", 1);
      i = j;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.e("SystemUIService", "[Font]" + localException.getMessage());
      }
    }
    if (this.mFontConfig != i)
    {
      Typeface.changeFont(i);
      this.mFontConfig = i;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\SystemUIService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */