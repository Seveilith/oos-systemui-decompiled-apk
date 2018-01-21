package com.android.systemui.statusbar.policy;

import android.app.AlarmManager;
import android.app.AlarmManager.AlarmClockInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

public class NextAlarmController
  extends BroadcastReceiver
{
  private AlarmManager mAlarmManager;
  private final ArrayList<NextAlarmChangeCallback> mChangeCallbacks = new ArrayList();
  private AlarmManager.AlarmClockInfo mNextAlarm;
  
  public NextAlarmController(Context paramContext)
  {
    this.mAlarmManager = ((AlarmManager)paramContext.getSystemService("alarm"));
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.USER_SWITCHED");
    localIntentFilter.addAction("android.app.action.NEXT_ALARM_CLOCK_CHANGED");
    paramContext.registerReceiverAsUser(this, UserHandle.ALL, localIntentFilter, null, null);
    updateNextAlarm();
  }
  
  private void fireNextAlarmChanged()
  {
    int j = this.mChangeCallbacks.size();
    int i = 0;
    while (i < j)
    {
      ((NextAlarmChangeCallback)this.mChangeCallbacks.get(i)).onNextAlarmChanged(this.mNextAlarm);
      i += 1;
    }
  }
  
  private void updateNextAlarm()
  {
    this.mNextAlarm = this.mAlarmManager.getNextAlarmClock(-2);
    fireNextAlarmChanged();
  }
  
  public void addStateChangedCallback(NextAlarmChangeCallback paramNextAlarmChangeCallback)
  {
    this.mChangeCallbacks.add(paramNextAlarmChangeCallback);
    paramNextAlarmChangeCallback.onNextAlarmChanged(this.mNextAlarm);
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("NextAlarmController state:");
    paramPrintWriter.print("  mNextAlarm=");
    paramPrintWriter.println(this.mNextAlarm);
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramContext = paramIntent.getAction();
    if ((paramContext.equals("android.intent.action.USER_SWITCHED")) || (paramContext.equals("android.app.action.NEXT_ALARM_CLOCK_CHANGED"))) {
      updateNextAlarm();
    }
  }
  
  public void removeStateChangedCallback(NextAlarmChangeCallback paramNextAlarmChangeCallback)
  {
    this.mChangeCallbacks.remove(paramNextAlarmChangeCallback);
  }
  
  public static abstract interface NextAlarmChangeCallback
  {
    public abstract void onNextAlarmChanged(AlarmManager.AlarmClockInfo paramAlarmClockInfo);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\NextAlarmController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */