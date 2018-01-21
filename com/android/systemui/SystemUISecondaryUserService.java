package com.android.systemui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class SystemUISecondaryUserService
  extends Service
{
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
      if (localObject != null)
      {
        paramPrintWriter.println("dumping service: " + ((SystemUI)localObject).getClass().getName());
        ((SystemUI)localObject).dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      }
      i += 1;
      continue;
      localObject = paramArrayOfString[0];
      j = arrayOfSystemUI.length;
      i = 0;
      while (i < j)
      {
        SystemUI localSystemUI = arrayOfSystemUI[i];
        if ((localSystemUI != null) && (localSystemUI.getClass().getName().endsWith((String)localObject))) {
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
  
  public void onCreate()
  {
    super.onCreate();
    ((SystemUIApplication)getApplication()).startSecondaryUserServicesIfNeeded();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\SystemUISecondaryUserService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */