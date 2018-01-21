package com.android.systemui.screenshot;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenshotServiceErrorReceiver
  extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    GlobalScreenshot.notifyScreenshotError(paramContext, (NotificationManager)paramContext.getSystemService("notification"), 2131690077);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\screenshot\ScreenshotServiceErrorReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */