package com.android.systemui;

import android.util.EventLog;

public class EventLogTags
{
  public static void writeSysuiLockscreenGesture(int paramInt1, int paramInt2, int paramInt3)
  {
    EventLog.writeEvent(36021, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) });
  }
  
  public static void writeSysuiStatusBarState(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    EventLog.writeEvent(36004, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), Integer.valueOf(paramInt5), Integer.valueOf(paramInt6) });
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\EventLogTags.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */