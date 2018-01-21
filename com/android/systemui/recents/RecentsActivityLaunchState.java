package com.android.systemui.recents;

import com.android.systemui.recents.misc.SystemServicesProxy;

public class RecentsActivityLaunchState
{
  public boolean launchedFromApp;
  public boolean launchedFromBlacklistedApp;
  public boolean launchedFromHome;
  public int launchedNumVisibleTasks;
  public int launchedNumVisibleThumbnails;
  public int launchedToTaskId;
  public boolean launchedViaDockGesture;
  public boolean launchedViaDragGesture;
  public boolean launchedWithAltTab;
  
  public int getInitialFocusTaskIndex(int paramInt)
  {
    RecentsDebugFlags localRecentsDebugFlags = Recents.getDebugFlags();
    RecentsActivityLaunchState localRecentsActivityLaunchState = Recents.getConfiguration().getLaunchState();
    if (this.launchedFromApp)
    {
      if ((!localRecentsActivityLaunchState.launchedWithAltTab) && (localRecentsDebugFlags.isFastToggleRecentsEnabled())) {
        return paramInt - 1;
      }
      if (localRecentsActivityLaunchState.launchedFromBlacklistedApp) {
        return paramInt - 1;
      }
      return Math.max(0, paramInt - 2);
    }
    if ((!localRecentsActivityLaunchState.launchedWithAltTab) && (localRecentsDebugFlags.isFastToggleRecentsEnabled())) {
      return -1;
    }
    return paramInt - 1;
  }
  
  public void reset()
  {
    this.launchedFromHome = false;
    this.launchedFromApp = false;
    this.launchedFromBlacklistedApp = false;
    this.launchedToTaskId = -1;
    this.launchedWithAltTab = false;
    this.launchedViaDragGesture = false;
    this.launchedViaDockGesture = false;
    Recents.getSystemServices().setDeepCleaning(false);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\RecentsActivityLaunchState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */