package com.android.systemui.settings;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public abstract class CurrentUserTracker
  extends BroadcastReceiver
{
  private Context mContext;
  private int mCurrentUserId;
  
  public CurrentUserTracker(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public int getCurrentUserId()
  {
    return this.mCurrentUserId;
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if ("android.intent.action.USER_SWITCHED".equals(paramIntent.getAction()))
    {
      int i = this.mCurrentUserId;
      this.mCurrentUserId = paramIntent.getIntExtra("android.intent.extra.user_handle", 0);
      if (i != this.mCurrentUserId) {
        onUserSwitched(this.mCurrentUserId);
      }
    }
  }
  
  public abstract void onUserSwitched(int paramInt);
  
  public void startTracking()
  {
    this.mCurrentUserId = ActivityManager.getCurrentUser();
    IntentFilter localIntentFilter = new IntentFilter("android.intent.action.USER_SWITCHED");
    this.mContext.registerReceiver(this, localIntentFilter);
  }
  
  public void stopTracking()
  {
    this.mContext.unregisterReceiver(this);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\settings\CurrentUserTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */