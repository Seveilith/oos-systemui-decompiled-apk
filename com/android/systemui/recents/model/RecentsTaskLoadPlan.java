package com.android.systemui.recents.model;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.os.UserManager;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.systemui.Prefs;
import com.android.systemui.recents.LockStateController;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsConfiguration;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.util.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecentsTaskLoadPlan
{
  private static int MIN_NUM_TASKS = 5;
  private static int SESSION_BEGIN_TIME = 21600000;
  Context mContext;
  ArraySet<Integer> mCurrentQuietProfiles = new ArraySet();
  List<ActivityManager.RecentTaskInfo> mRawTasks;
  TaskStack mStack;
  
  RecentsTaskLoadPlan(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private boolean isHistoricalTask(ActivityManager.RecentTaskInfo paramRecentTaskInfo)
  {
    return paramRecentTaskInfo.lastActiveTime < System.currentTimeMillis() - SESSION_BEGIN_TIME;
  }
  
  private void updateCurrentQuietProfilesCache(int paramInt)
  {
    this.mCurrentQuietProfiles.clear();
    int i = paramInt;
    if (paramInt == -2) {
      i = ActivityManager.getCurrentUser();
    }
    List localList = ((UserManager)this.mContext.getSystemService("user")).getProfiles(i);
    if (localList != null)
    {
      paramInt = 0;
      while (paramInt < localList.size())
      {
        UserInfo localUserInfo = (UserInfo)localList.get(paramInt);
        if ((localUserInfo.isManagedProfile()) && (localUserInfo.isQuietModeEnabled())) {
          this.mCurrentQuietProfiles.add(Integer.valueOf(localUserInfo.id));
        }
        paramInt += 1;
      }
    }
  }
  
  public void executePlan(Options paramOptions, RecentsTaskLoader paramRecentsTaskLoader, TaskResourceLoadQueue paramTaskResourceLoadQueue)
  {
    for (;;)
    {
      RecentsConfiguration localRecentsConfiguration;
      int i;
      Task localTask;
      try
      {
        localRecentsConfiguration = Recents.getConfiguration();
        Resources localResources = this.mContext.getResources();
        ArrayList localArrayList = this.mStack.getStackTasks();
        int n = localArrayList.size();
        i = 0;
        if (i >= n) {
          break label245;
        }
        localTask = (Task)localArrayList.get(i);
        Task.TaskKey localTaskKey = localTask.key;
        if (localTask.key.id != paramOptions.runningTaskId) {
          break label257;
        }
        j = 1;
        if (i < n - paramOptions.numVisibleTasks) {
          break label263;
        }
        k = 1;
        if (i < n - paramOptions.numVisibleTaskThumbnails) {
          break label269;
        }
        m = 1;
        if ((paramOptions.onlyLoadPausedActivities) && (j != 0)) {
          break label248;
        }
        if ((paramOptions.loadIcons) && ((j != 0) || (k != 0)) && (localTask.icon == null)) {
          localTask.icon = paramRecentsTaskLoader.getAndUpdateActivityIcon(localTaskKey, localTask.taskDescription, localResources, true);
        }
        if ((!paramOptions.loadThumbnails) || ((j == 0) && (m == 0)) || ((localTask.thumbnail != null) && (j == 0))) {
          break label248;
        }
        if (localRecentsConfiguration.svelteLevel <= 1) {
          localTask.thumbnail = paramRecentsTaskLoader.getAndUpdateThumbnail(localTaskKey, true);
        }
      }
      finally {}
      if (localRecentsConfiguration.svelteLevel == 2)
      {
        paramTaskResourceLoadQueue.addTask(localTask);
        break label248;
        label245:
        return;
      }
      label248:
      i += 1;
      continue;
      label257:
      int j = 0;
      continue;
      label263:
      int k = 0;
      continue;
      label269:
      int m = 0;
    }
  }
  
  public List<ActivityManager.RecentTaskInfo> getRawTasks()
  {
    return this.mRawTasks;
  }
  
  public TaskStack getTaskStack()
  {
    return this.mStack;
  }
  
  public boolean hasTasks()
  {
    boolean bool = false;
    if (this.mStack != null)
    {
      if (this.mStack.getTaskCount() > 0) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  public void preloadPlan(RecentsTaskLoader paramRecentsTaskLoader, int paramInt, boolean paramBoolean)
  {
    for (;;)
    {
      try
      {
        Resources localResources = this.mContext.getResources();
        ArrayList localArrayList = new ArrayList();
        if (Utils.DEBUG_ONEPLUS)
        {
          localObject = new StringBuilder().append("preloadPlan, ");
          if (this.mRawTasks == null)
          {
            bool1 = true;
            Log.d("RecentsTaskLoadPlan", bool1);
          }
        }
        else
        {
          if (this.mRawTasks == null) {
            preloadRawTasks(paramBoolean);
          }
          SparseArray localSparseArray = new SparseArray();
          SparseIntArray localSparseIntArray = new SparseIntArray();
          String str1 = this.mContext.getString(2131690178);
          String str2 = this.mContext.getString(2131690181);
          long l3 = Prefs.getLong(this.mContext, "OverviewLastStackTaskActiveTime", 0L);
          long l2 = -1L;
          int k = this.mRawTasks.size();
          int j = 0;
          if (j < k)
          {
            ActivityManager.RecentTaskInfo localRecentTaskInfo = (ActivityManager.RecentTaskInfo)this.mRawTasks.get(j);
            Task.TaskKey localTaskKey = new Task.TaskKey(localRecentTaskInfo.persistentId, localRecentTaskInfo.stackId, localRecentTaskInfo.baseIntent, localRecentTaskInfo.userId, localRecentTaskInfo.firstActiveTime, localRecentTaskInfo.lastActiveTime, localRecentTaskInfo.isTopAppLocked);
            if ((SystemServicesProxy.isFreeformStack(localRecentTaskInfo.stackId)) || (!isHistoricalTask(localRecentTaskInfo))) {
              break label731;
            }
            if ((localRecentTaskInfo.lastActiveTime < l3) || (j < k - MIN_NUM_TASKS)) {
              break label737;
            }
            i = 1;
            if (localTaskKey.id != paramInt) {
              break label743;
            }
            bool1 = true;
            long l1 = l2;
            if (i != 0)
            {
              l1 = l2;
              if (l2 < 0L)
              {
                l2 = localRecentTaskInfo.lastActiveTime;
                l1 = l2;
                if (Utils.DEBUG_ONEPLUS)
                {
                  Log.d("RecentsTaskLoadPlan", "last active:" + localTaskKey.id + ", " + l2);
                  l1 = l2;
                }
              }
            }
            ActivityInfo localActivityInfo = paramRecentsTaskLoader.getAndUpdateActivityInfo(localTaskKey);
            String str3 = paramRecentsTaskLoader.getAndUpdateActivityTitle(localTaskKey, localRecentTaskInfo.taskDescription);
            String str4 = paramRecentsTaskLoader.getAndUpdateContentDescription(localTaskKey, localResources);
            String str5 = String.format(str1, new Object[] { str4 });
            String str6 = String.format(str2, new Object[] { str4 });
            if (1 == 0) {
              break label749;
            }
            localObject = paramRecentsTaskLoader.getAndUpdateActivityIcon(localTaskKey, localRecentTaskInfo.taskDescription, localResources, false);
            Bitmap localBitmap = paramRecentsTaskLoader.getAndUpdateThumbnail(localTaskKey, false);
            i = paramRecentsTaskLoader.getActivityPrimaryColor(localRecentTaskInfo.taskDescription);
            int m = paramRecentsTaskLoader.getActivityBackgroundColor(localRecentTaskInfo.taskDescription);
            if (localActivityInfo == null) {
              break label760;
            }
            if ((localActivityInfo.applicationInfo.flags & 0x1) == 0) {
              break label755;
            }
            paramBoolean = true;
            boolean bool2 = LockStateController.getInstance(this.mContext).getLockState(localRecentTaskInfo.baseIntent.getComponent().toShortString(), localTaskKey.userId);
            localArrayList.add(new Task(localTaskKey, localRecentTaskInfo.affiliatedTaskId, localRecentTaskInfo.affiliatedTaskColor, (Drawable)localObject, localBitmap, str3, str4, str5, str6, i, m, bool1, true, paramBoolean, localRecentTaskInfo.isDockable, localRecentTaskInfo.bounds, localRecentTaskInfo.taskDescription, localRecentTaskInfo.resizeMode, localRecentTaskInfo.topActivity, bool2));
            localSparseIntArray.put(localTaskKey.id, localSparseIntArray.get(localTaskKey.id, 0) + 1);
            localSparseArray.put(localTaskKey.id, localTaskKey);
            j += 1;
            l2 = l1;
            continue;
          }
          if (l2 != -1L)
          {
            if (Utils.DEBUG_ONEPLUS) {
              Log.d("RecentsTaskLoadPlan", "set last active:" + l2);
            }
            Prefs.putLong(this.mContext, "OverviewLastStackTaskActiveTime", l2);
          }
          this.mStack = new TaskStack();
          this.mStack.setTasks(this.mContext, localArrayList, false);
          return;
        }
      }
      finally {}
      boolean bool1 = false;
      continue;
      label731:
      int i = 1;
      continue;
      label737:
      i = 0;
      continue;
      label743:
      bool1 = false;
      continue;
      label749:
      Object localObject = null;
      continue;
      label755:
      paramBoolean = false;
      continue;
      label760:
      paramBoolean = false;
    }
  }
  
  public void preloadRawTasks(boolean paramBoolean)
  {
    try
    {
      if (Utils.DEBUG_ONEPLUS) {
        Log.d("RecentsTaskLoadPlan", "preloadRawTasks, " + Debug.getCallers(5));
      }
      updateCurrentQuietProfilesCache(-2);
      this.mRawTasks = Recents.getSystemServices().getRecentTasks(ActivityManager.getMaxRecentTasksStatic(), -2, paramBoolean, this.mCurrentQuietProfiles);
      Collections.reverse(this.mRawTasks);
      return;
    }
    finally {}
  }
  
  public static class Options
  {
    public boolean loadIcons = true;
    public boolean loadThumbnails = true;
    public int numVisibleTaskThumbnails = 0;
    public int numVisibleTasks = 0;
    public boolean onlyLoadForCache = false;
    public boolean onlyLoadPausedActivities = false;
    public int runningTaskId = -1;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\model\RecentsTaskLoadPlan.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */