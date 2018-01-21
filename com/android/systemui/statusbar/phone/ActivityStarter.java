package com.android.systemui.statusbar.phone;

import android.app.PendingIntent;
import android.content.Intent;

public abstract interface ActivityStarter
{
  public abstract void preventNextAnimation();
  
  public abstract void startActivity(Intent paramIntent, boolean paramBoolean);
  
  public abstract void startActivity(Intent paramIntent, boolean paramBoolean, Callback paramCallback);
  
  public abstract void startPendingIntentDismissingKeyguard(PendingIntent paramPendingIntent);
  
  public static abstract interface Callback
  {
    public abstract void onActivityStarted(int paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\ActivityStarter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */