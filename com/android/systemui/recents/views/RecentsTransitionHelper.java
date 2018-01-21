package com.android.systemui.recents.views;

import android.app.ActivityManager.StackId;
import android.app.ActivityOptions;
import android.app.ActivityOptions.OnAnimationStartedListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.IRemoteCallback;
import android.os.IRemoteCallback.Stub;
import android.os.RemoteException;
import android.util.Log;
import android.view.AppTransitionAnimationSpec;
import android.view.IAppTransitionAnimationSpecsFuture;
import android.view.IAppTransitionAnimationSpecsFuture.Stub;
import com.android.internal.annotations.GuardedBy;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.EventBus.Event;
import com.android.systemui.recents.events.activity.CancelEnterRecentsWindowAnimationEvent;
import com.android.systemui.recents.events.activity.ExitRecentsWindowFirstAnimationFrameEvent;
import com.android.systemui.recents.events.activity.LaunchTaskFailedEvent;
import com.android.systemui.recents.events.activity.LaunchTaskStartedEvent;
import com.android.systemui.recents.events.activity.LaunchTaskSucceededEvent;
import com.android.systemui.recents.events.component.ScreenPinningRequestEvent;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.Task.TaskKey;
import com.android.systemui.recents.model.TaskGrouping;
import com.android.systemui.recents.model.TaskStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecentsTransitionHelper
{
  private static final List<AppTransitionAnimationSpec> SPECS_WAITING = new ArrayList();
  @GuardedBy("this")
  private List<AppTransitionAnimationSpec> mAppTransitionAnimationSpecs = SPECS_WAITING;
  private Context mContext;
  private Handler mHandler;
  private StartScreenPinningRunnableRunnable mStartScreenPinningRunnable = new StartScreenPinningRunnableRunnable(null);
  private TaskViewTransform mTmpTransform = new TaskViewTransform();
  
  public RecentsTransitionHelper(Context paramContext)
  {
    this.mContext = paramContext;
    this.mHandler = new Handler();
  }
  
  private static AppTransitionAnimationSpec composeAnimationSpec(TaskStackView paramTaskStackView, TaskView paramTaskView, TaskViewTransform paramTaskViewTransform, boolean paramBoolean)
  {
    Object localObject1 = null;
    if (paramBoolean)
    {
      localObject2 = composeHeaderBitmap(paramTaskView, paramTaskViewTransform);
      localObject1 = localObject2;
      if (localObject2 == null) {
        return null;
      }
    }
    Object localObject2 = new Rect();
    paramTaskViewTransform.rect.round((Rect)localObject2);
    if (paramTaskStackView.getStack().getStackFrontMostTask(false) != paramTaskView.getTask()) {
      ((Rect)localObject2).bottom = (((Rect)localObject2).top + paramTaskStackView.getMeasuredHeight());
    }
    return new AppTransitionAnimationSpec(paramTaskView.getTask().key.id, (Bitmap)localObject1, (Rect)localObject2);
  }
  
  private List<AppTransitionAnimationSpec> composeAnimationSpecs(Task paramTask, TaskStackView paramTaskStackView, int paramInt)
  {
    if (paramInt != -1) {}
    while (!ActivityManager.StackId.useAnimationSpecForAppTransition(paramInt))
    {
      return null;
      paramInt = paramTask.key.stackId;
    }
    TaskView localTaskView1 = paramTaskStackView.getChildViewForTask(paramTask);
    TaskStackLayoutAlgorithm localTaskStackLayoutAlgorithm = paramTaskStackView.getStackAlgorithm();
    Rect localRect = new Rect();
    localTaskStackLayoutAlgorithm.getFrontOfStackTransform().rect.round(localRect);
    ArrayList localArrayList = new ArrayList();
    if ((paramInt == 1) || (paramInt == 3)) {}
    int i;
    label122:
    Object localObject;
    TaskView localTaskView2;
    for (;;)
    {
      if (localTaskView1 == null)
      {
        localArrayList.add(composeOffscreenAnimationSpec(paramTask, localRect));
        return localArrayList;
        if (paramInt != -1)
        {
          paramTask = paramTaskStackView.getStack().getStackTasks();
          i = paramTask.size() - 1;
          if (i < 0) {
            break label287;
          }
          localObject = (Task)paramTask.get(i);
          if ((((Task)localObject).isFreeformTask()) || (paramInt == 2))
          {
            localTaskView2 = paramTaskStackView.getChildViewForTask((Task)localObject);
            if (localTaskView2 != null) {
              break label236;
            }
            localArrayList.add(composeOffscreenAnimationSpec((Task)localObject, localRect));
          }
        }
      }
    }
    for (;;)
    {
      i -= 1;
      break label122;
      this.mTmpTransform.fillIn(localTaskView1);
      localTaskStackLayoutAlgorithm.transformToScreenCoordinates(this.mTmpTransform, null);
      paramTask = composeAnimationSpec(paramTaskStackView, localTaskView1, this.mTmpTransform, true);
      if (paramTask == null) {
        break;
      }
      localArrayList.add(paramTask);
      return localArrayList;
      label236:
      this.mTmpTransform.fillIn(localTaskView1);
      localTaskStackLayoutAlgorithm.transformToScreenCoordinates(this.mTmpTransform, null);
      localObject = composeAnimationSpec(paramTaskStackView, localTaskView2, this.mTmpTransform, true);
      if (localObject != null) {
        localArrayList.add(localObject);
      }
    }
    label287:
    return localArrayList;
  }
  
  private static Bitmap composeHeaderBitmap(TaskView paramTaskView, TaskViewTransform paramTaskViewTransform)
  {
    float f = paramTaskViewTransform.scale;
    int i = (int)paramTaskViewTransform.rect.width();
    int j = (int)(paramTaskView.mHeaderView.getMeasuredHeight() * f);
    if ((i == 0) || (j == 0)) {
      return null;
    }
    paramTaskViewTransform = Bitmap.createBitmap(i, j, Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(paramTaskViewTransform);
    localCanvas.scale(f, f);
    paramTaskView.mHeaderView.draw(localCanvas);
    localCanvas.setBitmap(null);
    return paramTaskViewTransform.createAshmemBitmap();
  }
  
  private static AppTransitionAnimationSpec composeOffscreenAnimationSpec(Task paramTask, Rect paramRect)
  {
    return new AppTransitionAnimationSpec(paramTask.key.id, null, paramRect);
  }
  
  public static Bitmap composeTaskBitmap(TaskView paramTaskView, TaskViewTransform paramTaskViewTransform)
  {
    float f = paramTaskViewTransform.scale;
    int i = (int)(paramTaskViewTransform.rect.width() * f);
    int j = (int)(paramTaskViewTransform.rect.height() * f);
    if ((i == 0) || (j == 0))
    {
      Log.e("RecentsTransitionHelper", "Could not compose thumbnail for task: " + paramTaskView.getTask() + " at transform: " + paramTaskViewTransform);
      paramTaskView = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
      paramTaskView.eraseColor(0);
      return paramTaskView;
    }
    paramTaskViewTransform = Bitmap.createBitmap(i, j, Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(paramTaskViewTransform);
    localCanvas.scale(f, f);
    paramTaskView.draw(localCanvas);
    localCanvas.setBitmap(null);
    return paramTaskViewTransform.createAshmemBitmap();
  }
  
  private void startTaskActivity(TaskStack paramTaskStack, Task paramTask, TaskView paramTaskView, ActivityOptions paramActivityOptions, IAppTransitionAnimationSpecsFuture paramIAppTransitionAnimationSpecsFuture, ActivityOptions.OnAnimationStartedListener paramOnAnimationStartedListener)
  {
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    if (localSystemServicesProxy.startActivityFromRecents(this.mContext, paramTask.key, paramTask.title, paramActivityOptions))
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
      if (paramIAppTransitionAnimationSpecsFuture != null) {
        localSystemServicesProxy.overridePendingAppTransitionMultiThumbFuture(paramIAppTransitionAnimationSpecsFuture, wrapStartedListener(paramOnAnimationStartedListener), true);
      }
      return;
      if (paramTaskView != null) {
        paramTaskView.dismissTask();
      }
      EventBus.getDefault().send(new LaunchTaskFailedEvent());
    }
  }
  
  public List<AppTransitionAnimationSpec> composeDockAnimationSpec(TaskView paramTaskView, Rect paramRect)
  {
    this.mTmpTransform.fillIn(paramTaskView);
    Task localTask = paramTaskView.getTask();
    paramTaskView = composeTaskBitmap(paramTaskView, this.mTmpTransform);
    return Collections.singletonList(new AppTransitionAnimationSpec(localTask.key.id, paramTaskView, paramRect));
  }
  
  public IAppTransitionAnimationSpecsFuture getAppTransitionFuture(final AnimationSpecComposer paramAnimationSpecComposer)
  {
    try
    {
      this.mAppTransitionAnimationSpecs = SPECS_WAITING;
      new IAppTransitionAnimationSpecsFuture.Stub()
      {
        public AppTransitionAnimationSpec[] get()
          throws RemoteException
        {
          RecentsTransitionHelper.-get3(RecentsTransitionHelper.this).post(new Runnable()
          {
            public void run()
            {
              synchronized (RecentsTransitionHelper.this)
              {
                RecentsTransitionHelper.-set0(RecentsTransitionHelper.this, this.val$composer.composeSpecs());
                RecentsTransitionHelper.this.notifyAll();
                return;
              }
            }
          });
          synchronized (RecentsTransitionHelper.this)
          {
            for (;;)
            {
              List localList1 = RecentsTransitionHelper.-get1(RecentsTransitionHelper.this);
              List localList2 = RecentsTransitionHelper.-get0();
              if (localList1 != localList2) {
                break;
              }
              try
              {
                RecentsTransitionHelper.this.wait();
              }
              catch (InterruptedException localInterruptedException) {}
            }
            Object localObject1 = RecentsTransitionHelper.-get1(RecentsTransitionHelper.this);
            if (localObject1 == null) {
              return null;
            }
            localObject1 = new AppTransitionAnimationSpec[RecentsTransitionHelper.-get1(RecentsTransitionHelper.this).size()];
            RecentsTransitionHelper.-get1(RecentsTransitionHelper.this).toArray((Object[])localObject1);
            RecentsTransitionHelper.-set0(RecentsTransitionHelper.this, RecentsTransitionHelper.-get0());
            return (AppTransitionAnimationSpec[])localObject1;
          }
        }
      };
    }
    finally
    {
      paramAnimationSpecComposer = finally;
      throw paramAnimationSpecComposer;
    }
  }
  
  public void launchTaskFromRecents(final TaskStack paramTaskStack, final Task paramTask, final TaskStackView paramTaskStackView, final TaskView paramTaskView, final boolean paramBoolean, final Rect paramRect, final int paramInt)
  {
    final ActivityOptions localActivityOptions = ActivityOptions.makeBasic();
    Object localObject;
    if (paramRect != null)
    {
      localObject = paramRect;
      if (paramRect.isEmpty()) {
        localObject = null;
      }
      localActivityOptions.setLaunchBounds((Rect)localObject);
    }
    if (paramTaskView != null)
    {
      localObject = getAppTransitionFuture(new AnimationSpecComposer()
      {
        public List<AppTransitionAnimationSpec> composeSpecs()
        {
          return RecentsTransitionHelper.-wrap0(RecentsTransitionHelper.this, paramTask, paramTaskStackView, paramInt);
        }
      });
      paramRect = new ActivityOptions.OnAnimationStartedListener()
      {
        public void onAnimationStarted()
        {
          EventBus.getDefault().send(new CancelEnterRecentsWindowAnimationEvent(paramTask));
          EventBus.getDefault().send(new ExitRecentsWindowFirstAnimationFrameEvent());
          paramTaskStackView.cancelAllTaskViewAnimations();
          if (paramBoolean)
          {
            RecentsTransitionHelper.StartScreenPinningRunnableRunnable.-set0(RecentsTransitionHelper.-get4(RecentsTransitionHelper.this), paramTask.key.id);
            RecentsTransitionHelper.-get3(RecentsTransitionHelper.this).postDelayed(RecentsTransitionHelper.-get4(RecentsTransitionHelper.this), 350L);
          }
        }
      };
      paramTaskStackView = (TaskStackView)localObject;
      if (paramTaskView != null) {
        break label126;
      }
      startTaskActivity(paramTaskStack, paramTask, paramTaskView, localActivityOptions, paramTaskStackView, paramRect);
    }
    for (;;)
    {
      Recents.getSystemServices().sendCloseSystemWindows("homekey");
      return;
      paramRect = null;
      localObject = new ActivityOptions.OnAnimationStartedListener()
      {
        public void onAnimationStarted()
        {
          EventBus.getDefault().send(new CancelEnterRecentsWindowAnimationEvent(paramTask));
          EventBus.getDefault().send(new ExitRecentsWindowFirstAnimationFrameEvent());
          paramTaskStackView.cancelAllTaskViewAnimations();
        }
      };
      paramTaskStackView = paramRect;
      paramRect = (Rect)localObject;
      break;
      label126:
      localObject = new LaunchTaskStartedEvent(paramTaskView, paramBoolean);
      if ((paramTask.group == null) || (paramTask.group.isFrontMostTask(paramTask)))
      {
        EventBus.getDefault().send((EventBus.Event)localObject);
        startTaskActivity(paramTaskStack, paramTask, paramTaskView, localActivityOptions, paramTaskStackView, paramRect);
      }
      else
      {
        ((LaunchTaskStartedEvent)localObject).addPostAnimationCallback(new Runnable()
        {
          public void run()
          {
            RecentsTransitionHelper.-wrap1(RecentsTransitionHelper.this, paramTaskStack, paramTask, paramTaskView, localActivityOptions, paramTaskStackView, paramRect);
          }
        });
        EventBus.getDefault().send((EventBus.Event)localObject);
      }
    }
  }
  
  public IRemoteCallback wrapStartedListener(final ActivityOptions.OnAnimationStartedListener paramOnAnimationStartedListener)
  {
    if (paramOnAnimationStartedListener == null) {
      return null;
    }
    new IRemoteCallback.Stub()
    {
      public void sendResult(Bundle paramAnonymousBundle)
        throws RemoteException
      {
        RecentsTransitionHelper.-get3(RecentsTransitionHelper.this).post(new Runnable()
        {
          public void run()
          {
            this.val$listener.onAnimationStarted();
          }
        });
      }
    };
  }
  
  public static abstract interface AnimationSpecComposer
  {
    public abstract List<AppTransitionAnimationSpec> composeSpecs();
  }
  
  private class StartScreenPinningRunnableRunnable
    implements Runnable
  {
    private int taskId = -1;
    
    private StartScreenPinningRunnableRunnable() {}
    
    public void run()
    {
      EventBus.getDefault().send(new ScreenPinningRequestEvent(RecentsTransitionHelper.-get2(RecentsTransitionHelper.this), this.taskId));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\RecentsTransitionHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */