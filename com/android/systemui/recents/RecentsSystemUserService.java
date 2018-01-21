package com.android.systemui.recents;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.android.systemui.SystemUIApplication;

public class RecentsSystemUserService
  extends Service
{
  public IBinder onBind(Intent paramIntent)
  {
    paramIntent = (Recents)((SystemUIApplication)getApplication()).getComponent(Recents.class);
    Log.d("RecentsSystemUserService", "onBind: " + paramIntent);
    if (paramIntent != null) {
      return paramIntent.getSystemUserCallbacks();
    }
    return null;
  }
  
  public void onCreate()
  {
    super.onCreate();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\RecentsSystemUserService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */