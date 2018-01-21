package com.android.systemui.stackdivider;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.UserHandle;
import android.util.ArraySet;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.AppTransitionFinishedEvent;
import com.android.systemui.recents.events.component.ShowUserToastEvent;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.misc.SystemServicesProxy.TaskStackListener;
import com.android.systemui.stackdivider.events.StartedDragingEvent;
import com.android.systemui.stackdivider.events.StoppedDragingEvent;

public class ForcedResizableInfoActivityController
{
  private final Context mContext;
  private boolean mDividerDraging;
  private final Handler mHandler = new Handler();
  private final ArraySet<String> mPackagesShownInSession = new ArraySet();
  private final ArraySet<Integer> mPendingTaskIds = new ArraySet();
  private final Runnable mTimeoutRunnable = new Runnable()
  {
    public void run()
    {
      ForcedResizableInfoActivityController.-wrap2(ForcedResizableInfoActivityController.this);
    }
  };
  
  public ForcedResizableInfoActivityController(Context paramContext)
  {
    this.mContext = paramContext;
    EventBus.getDefault().register(this);
    SystemServicesProxy.getInstance(paramContext).registerTaskStackListener(new SystemServicesProxy.TaskStackListener()
    {
      public void onActivityDismissingDockedStack()
      {
        ForcedResizableInfoActivityController.-wrap0(ForcedResizableInfoActivityController.this);
      }
      
      public void onActivityForcedResizable(String paramAnonymousString, int paramAnonymousInt)
      {
        ForcedResizableInfoActivityController.-wrap1(ForcedResizableInfoActivityController.this, paramAnonymousString, paramAnonymousInt);
      }
    });
  }
  
  private void activityDismissingDockedStack()
  {
    EventBus.getDefault().send(new ShowUserToastEvent(2131690630, 0));
  }
  
  private void activityForcedResizable(String paramString, int paramInt)
  {
    if (debounce(paramString)) {
      return;
    }
    this.mPendingTaskIds.add(Integer.valueOf(paramInt));
    postTimeout();
  }
  
  private boolean debounce(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    if ("com.android.systemui".equals(paramString)) {
      return true;
    }
    boolean bool = this.mPackagesShownInSession.contains(paramString);
    this.mPackagesShownInSession.add(paramString);
    return bool;
  }
  
  private void postTimeout()
  {
    this.mHandler.removeCallbacks(this.mTimeoutRunnable);
    this.mHandler.postDelayed(this.mTimeoutRunnable, 1000L);
  }
  
  private void showPending()
  {
    this.mHandler.removeCallbacks(this.mTimeoutRunnable);
    int i = this.mPendingTaskIds.size() - 1;
    while (i >= 0)
    {
      Intent localIntent = new Intent(this.mContext, ForcedResizableInfoActivity.class);
      ActivityOptions localActivityOptions = ActivityOptions.makeBasic();
      localActivityOptions.setLaunchTaskId(((Integer)this.mPendingTaskIds.valueAt(i)).intValue());
      localActivityOptions.setTaskOverlay(true);
      this.mContext.startActivityAsUser(localIntent, localActivityOptions.toBundle(), UserHandle.CURRENT);
      i -= 1;
    }
    this.mPendingTaskIds.clear();
  }
  
  public void notifyDockedStackExistsChanged(boolean paramBoolean)
  {
    if (!paramBoolean) {
      this.mPackagesShownInSession.clear();
    }
  }
  
  public final void onBusEvent(AppTransitionFinishedEvent paramAppTransitionFinishedEvent)
  {
    if (!this.mDividerDraging) {
      showPending();
    }
  }
  
  public final void onBusEvent(StartedDragingEvent paramStartedDragingEvent)
  {
    this.mDividerDraging = true;
    this.mHandler.removeCallbacks(this.mTimeoutRunnable);
  }
  
  public final void onBusEvent(StoppedDragingEvent paramStoppedDragingEvent)
  {
    this.mDividerDraging = false;
    showPending();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\stackdivider\ForcedResizableInfoActivityController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */