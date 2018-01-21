package com.android.systemui.recents.tv;

import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.SystemClock;
import android.os.UserHandle;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsActivityLaunchState;
import com.android.systemui.recents.RecentsConfiguration;
import com.android.systemui.recents.RecentsImpl;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.RecentsActivityStartingEvent;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.model.RecentsTaskLoadPlan;
import com.android.systemui.recents.model.RecentsTaskLoader;
import com.android.systemui.recents.model.TaskStack;
import com.android.systemui.recents.model.ThumbnailData;
import com.android.systemui.recents.tv.views.TaskCardView;
import com.android.systemui.tv.pip.PipManager;

public class RecentsTvImpl
  extends RecentsImpl
{
  private static final PipManager mPipManager = ;
  
  public RecentsTvImpl(Context paramContext)
  {
    super(paramContext);
  }
  
  private ActivityOptions getThumbnailTransitionActivityOptionsForTV(ActivityManager.RunningTaskInfo paramRunningTaskInfo, int paramInt)
  {
    Object localObject = this.mContext;
    if (mPipManager.isPipShown()) {}
    for (boolean bool = false;; bool = true)
    {
      localObject = TaskCardView.getStartingCardThumbnailRect((Context)localObject, bool, paramInt);
      paramRunningTaskInfo = Recents.getSystemServices().getTaskThumbnail(paramRunningTaskInfo.id);
      if (paramRunningTaskInfo.thumbnail == null) {
        break;
      }
      paramRunningTaskInfo = Bitmap.createScaledBitmap(paramRunningTaskInfo.thumbnail, ((Rect)localObject).width(), ((Rect)localObject).height(), false);
      return ActivityOptions.makeThumbnailAspectScaleDownAnimation(this.mDummyStackView, paramRunningTaskInfo, ((Rect)localObject).left, ((Rect)localObject).top, ((Rect)localObject).width(), ((Rect)localObject).height(), this.mHandler, null);
    }
    return getUnknownTransitionActivityOptions();
  }
  
  public void onVisibilityChanged(Context paramContext, boolean paramBoolean)
  {
    Recents.getSystemServices().setRecentsVisibility(paramBoolean);
  }
  
  protected void startRecentsActivity(ActivityManager.RunningTaskInfo paramRunningTaskInfo, ActivityOptions paramActivityOptions, boolean paramBoolean1, boolean paramBoolean2)
  {
    RecentsActivityLaunchState localRecentsActivityLaunchState = Recents.getConfiguration().getLaunchState();
    localRecentsActivityLaunchState.launchedFromHome = paramBoolean1;
    localRecentsActivityLaunchState.launchedFromApp = paramBoolean2;
    int i;
    if (paramRunningTaskInfo != null)
    {
      i = paramRunningTaskInfo.id;
      localRecentsActivityLaunchState.launchedToTaskId = i;
      localRecentsActivityLaunchState.launchedWithAltTab = this.mTriggeredFromAltTab;
      paramRunningTaskInfo = new Intent();
      paramRunningTaskInfo.setClassName("com.android.systemui", "com.android.systemui.recents.tv.RecentsTvActivity");
      paramRunningTaskInfo.setFlags(276840448);
      if (paramActivityOptions == null) {
        break label110;
      }
      this.mContext.startActivityAsUser(paramRunningTaskInfo, paramActivityOptions.toBundle(), UserHandle.CURRENT);
    }
    for (;;)
    {
      EventBus.getDefault().send(new RecentsActivityStartingEvent());
      return;
      i = -1;
      break;
      label110:
      this.mContext.startActivityAsUser(paramRunningTaskInfo, UserHandle.CURRENT);
    }
  }
  
  protected void startRecentsActivity(ActivityManager.RunningTaskInfo paramRunningTaskInfo, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    Object localObject = Recents.getTaskLoader();
    if ((this.mTriggeredFromAltTab) || (sInstanceLoadPlan == null)) {
      sInstanceLoadPlan = ((RecentsTaskLoader)localObject).createLoadPlan(this.mContext);
    }
    if ((!this.mTriggeredFromAltTab) && (sInstanceLoadPlan.hasTasks()))
    {
      localObject = sInstanceLoadPlan.getTaskStack();
      if (!paramBoolean2) {
        startRecentsActivity(paramRunningTaskInfo, ActivityOptions.makeCustomAnimation(this.mContext, -1, -1), false, false);
      }
    }
    else
    {
      RecentsTaskLoadPlan localRecentsTaskLoadPlan = sInstanceLoadPlan;
      paramInt = paramRunningTaskInfo.id;
      if (paramBoolean1) {}
      for (boolean bool = false;; bool = true)
      {
        ((RecentsTaskLoader)localObject).preloadTasks(localRecentsTaskLoadPlan, paramInt, bool);
        break;
      }
    }
    if (((TaskStack)localObject).getTaskCount() > 0)
    {
      paramInt = 1;
      if ((paramRunningTaskInfo != null) && (!paramBoolean1)) {
        break label201;
      }
      paramInt = 0;
      label135:
      i = paramInt;
      if (paramInt != 0)
      {
        localObject = getThumbnailTransitionActivityOptionsForTV(paramRunningTaskInfo, ((TaskStack)localObject).getTaskCount());
        if (localObject == null) {
          break label204;
        }
        startRecentsActivity(paramRunningTaskInfo, (ActivityOptions)localObject, false, true);
      }
    }
    label201:
    label204:
    for (int i = paramInt;; i = 0)
    {
      if (i == 0) {
        startRecentsActivity(paramRunningTaskInfo, null, true, false);
      }
      this.mLastToggleTime = SystemClock.elapsedRealtime();
      return;
      paramInt = 0;
      break;
      break label135;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\tv\RecentsTvImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */