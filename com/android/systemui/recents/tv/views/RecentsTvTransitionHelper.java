package com.android.systemui.recents.tv.views;

import android.app.ActivityOptions;
import android.app.ActivityOptions.OnAnimationStartedListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IRemoteCallback.Stub;
import android.os.RemoteException;
import android.util.Log;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.CancelEnterRecentsWindowAnimationEvent;
import com.android.systemui.recents.events.activity.ExitRecentsWindowFirstAnimationFrameEvent;
import com.android.systemui.recents.events.activity.LaunchTaskFailedEvent;
import com.android.systemui.recents.events.activity.LaunchTaskSucceededEvent;
import com.android.systemui.recents.events.activity.LaunchTvTaskStartedEvent;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.TaskStack;

public class RecentsTvTransitionHelper
{
  private Context mContext;
  private Handler mHandler;
  
  public RecentsTvTransitionHelper(Context paramContext, Handler paramHandler)
  {
    this.mContext = paramContext;
    this.mHandler = paramHandler;
  }
  
  private void startTaskActivity(TaskStack paramTaskStack, Task paramTask, TaskCardView paramTaskCardView, ActivityOptions paramActivityOptions, final ActivityOptions.OnAnimationStartedListener paramOnAnimationStartedListener)
  {
    if (Recents.getSystemServices().startActivityFromRecents(this.mContext, paramTask.key, paramTask.title, paramActivityOptions))
    {
      int i = 0;
      int j = paramTaskStack.indexOfStackTask(paramTask);
      if (j > -1) {
        i = paramTaskStack.getTaskCount() - j - 1;
      }
      EventBus.getDefault().send(new LaunchTaskSucceededEvent(i));
    }
    for (;;)
    {
      paramTaskCardView = paramTaskCardView.getFocusedThumbnailRect();
      if ((paramTaskCardView != null) && (paramTask.thumbnail != null)) {
        break;
      }
      return;
      EventBus.getDefault().send(new LaunchTaskFailedEvent());
    }
    paramTaskStack = null;
    if (paramOnAnimationStartedListener != null) {
      paramTaskStack = new IRemoteCallback.Stub()
      {
        public void sendResult(Bundle paramAnonymousBundle)
          throws RemoteException
        {
          RecentsTvTransitionHelper.-get0(RecentsTvTransitionHelper.this).post(new Runnable()
          {
            public void run()
            {
              if (this.val$animStartedListener != null) {
                this.val$animStartedListener.onAnimationStarted();
              }
            }
          });
        }
      };
    }
    try
    {
      paramTask = Bitmap.createScaledBitmap(paramTask.thumbnail, paramTaskCardView.width(), paramTaskCardView.height(), false);
      WindowManagerGlobal.getWindowManagerService().overridePendingAppTransitionAspectScaledThumb(paramTask, paramTaskCardView.left, paramTaskCardView.top, paramTaskCardView.width(), paramTaskCardView.height(), paramTaskStack, true);
      return;
    }
    catch (RemoteException paramTaskStack)
    {
      Log.w("RecentsTvTransitionHelper", "Failed to override transition: " + paramTaskStack);
    }
  }
  
  public void launchTaskFromRecents(TaskStack paramTaskStack, final Task paramTask, TaskStackHorizontalGridView paramTaskStackHorizontalGridView, TaskCardView paramTaskCardView, Rect paramRect, int paramInt)
  {
    ActivityOptions localActivityOptions = ActivityOptions.makeBasic();
    if (paramRect != null)
    {
      paramTaskStackHorizontalGridView = paramRect;
      if (paramRect.isEmpty()) {
        paramTaskStackHorizontalGridView = null;
      }
      localActivityOptions.setLaunchBounds(paramTaskStackHorizontalGridView);
    }
    if ((paramTask.thumbnail != null) && (paramTask.thumbnail.getWidth() > 0) && (paramTask.thumbnail.getHeight() > 0)) {}
    for (paramTaskStackHorizontalGridView = new ActivityOptions.OnAnimationStartedListener()
        {
          public void onAnimationStarted()
          {
            EventBus.getDefault().send(new CancelEnterRecentsWindowAnimationEvent(paramTask));
            EventBus.getDefault().send(new ExitRecentsWindowFirstAnimationFrameEvent());
          }
        }; paramTaskCardView == null; paramTaskStackHorizontalGridView = new ActivityOptions.OnAnimationStartedListener()
        {
          public void onAnimationStarted()
          {
            EventBus.getDefault().send(new ExitRecentsWindowFirstAnimationFrameEvent());
          }
        })
    {
      startTaskActivity(paramTaskStack, paramTask, paramTaskCardView, localActivityOptions, paramTaskStackHorizontalGridView);
      return;
    }
    paramRect = new LaunchTvTaskStartedEvent(paramTaskCardView);
    EventBus.getDefault().send(paramRect);
    startTaskActivity(paramTaskStack, paramTask, paramTaskCardView, localActivityOptions, paramTaskStackHorizontalGridView);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\tv\views\RecentsTvTransitionHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */