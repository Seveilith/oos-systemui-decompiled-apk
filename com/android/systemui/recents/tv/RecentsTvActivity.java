package com.android.systemui.recents.tv;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout.LayoutParams;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsActivityLaunchState;
import com.android.systemui.recents.RecentsConfiguration;
import com.android.systemui.recents.RecentsImpl;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.CancelEnterRecentsWindowAnimationEvent;
import com.android.systemui.recents.events.activity.DismissRecentsToHomeAnimationStarted;
import com.android.systemui.recents.events.activity.EnterRecentsWindowAnimationCompletedEvent;
import com.android.systemui.recents.events.activity.HideRecentsEvent;
import com.android.systemui.recents.events.activity.LaunchTaskFailedEvent;
import com.android.systemui.recents.events.activity.ToggleRecentsEvent;
import com.android.systemui.recents.events.component.RecentsVisibilityChangedEvent;
import com.android.systemui.recents.events.ui.AllTaskViewsDismissedEvent;
import com.android.systemui.recents.events.ui.DeleteTaskDataEvent;
import com.android.systemui.recents.events.ui.UserInteractionEvent;
import com.android.systemui.recents.events.ui.focus.DismissFocusedTaskViewEvent;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.model.RecentsPackageMonitor;
import com.android.systemui.recents.model.RecentsTaskLoadPlan;
import com.android.systemui.recents.model.RecentsTaskLoadPlan.Options;
import com.android.systemui.recents.model.RecentsTaskLoader;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.Task.TaskKey;
import com.android.systemui.recents.model.TaskStack;
import com.android.systemui.recents.tv.animations.HomeRecentsEnterExitAnimationHolder;
import com.android.systemui.recents.tv.views.RecentsTvView;
import com.android.systemui.recents.tv.views.TaskCardView;
import com.android.systemui.recents.tv.views.TaskStackHorizontalGridView;
import com.android.systemui.recents.tv.views.TaskStackHorizontalViewAdapter;
import com.android.systemui.tv.pip.PipManager;
import com.android.systemui.tv.pip.PipManager.Listener;
import com.android.systemui.tv.pip.PipRecentsOverlayManager;
import com.android.systemui.tv.pip.PipRecentsOverlayManager.Callback;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecentsTvActivity
  extends Activity
  implements ViewTreeObserver.OnPreDrawListener
{
  private FinishRecentsRunnable mFinishLaunchHomeRunnable;
  private boolean mFinishedOnStartup;
  private HomeRecentsEnterExitAnimationHolder mHomeRecentsEnterExitAnimationHolder;
  private boolean mIgnoreAltTabRelease;
  private boolean mLaunchedFromHome;
  private RecentsPackageMonitor mPackageMonitor;
  private final PipManager.Listener mPipListener = new PipManager.Listener()
  {
    public void onMoveToFullscreen()
    {
      RecentsTvActivity.this.dismissRecentsToLaunchTargetTaskOrHome(false);
    }
    
    public void onPipActivityClosed()
    {
      RecentsTvActivity.-wrap0(RecentsTvActivity.this);
    }
    
    public void onPipEntered()
    {
      RecentsTvActivity.-wrap0(RecentsTvActivity.this);
    }
    
    public void onPipResizeAboutToStart() {}
    
    public void onShowPipMenu()
    {
      RecentsTvActivity.-wrap0(RecentsTvActivity.this);
    }
  };
  private final PipManager mPipManager = PipManager.getInstance();
  private PipRecentsOverlayManager mPipRecentsOverlayManager;
  private final PipRecentsOverlayManager.Callback mPipRecentsOverlayManagerCallback = new PipRecentsOverlayManager.Callback()
  {
    public void onBackPressed()
    {
      RecentsTvActivity.this.onBackPressed();
    }
    
    public void onClosed()
    {
      RecentsTvActivity.this.dismissRecentsToLaunchTargetTaskOrHome(true);
    }
    
    public void onRecentsFocused()
    {
      if (RecentsTvActivity.-get0(RecentsTvActivity.this))
      {
        RecentsTvActivity.-get1(RecentsTvActivity.this).requestFocus();
        RecentsTvActivity.-get1(RecentsTvActivity.this).sendAccessibilityEvent(8);
      }
      RecentsTvActivity.-get1(RecentsTvActivity.this).startFocusGainAnimation();
    }
  };
  private View mPipView;
  private final View.OnFocusChangeListener mPipViewFocusChangeListener = new View.OnFocusChangeListener()
  {
    public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
    {
      if (paramAnonymousBoolean) {
        RecentsTvActivity.this.requestPipControlsFocus();
      }
    }
  };
  private RecentsTvView mRecentsView;
  private boolean mTalkBackEnabled;
  private TaskStackHorizontalGridView mTaskStackHorizontalGridView;
  private TaskStackHorizontalViewAdapter mTaskStackViewAdapter;
  
  private void updatePipUI()
  {
    if (!this.mPipManager.isPipShown())
    {
      this.mPipRecentsOverlayManager.removePipRecentsOverlayView();
      this.mTaskStackHorizontalGridView.startFocusLossAnimation();
      return;
    }
    Log.w("RecentsTvActivity", "An activity entered PIP mode while Recents is shown");
  }
  
  private void updateRecentsTasks()
  {
    Object localObject3 = Recents.getTaskLoader();
    Object localObject2 = RecentsImpl.consumeInstanceLoadPlan();
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = ((RecentsTaskLoader)localObject3).createLoadPlan(this);
    }
    localObject2 = Recents.getConfiguration().getLaunchState();
    boolean bool;
    int i;
    label193:
    int j;
    if (!((RecentsTaskLoadPlan)localObject1).hasTasks())
    {
      if (((RecentsActivityLaunchState)localObject2).launchedFromHome)
      {
        bool = false;
        ((RecentsTaskLoader)localObject3).preloadTasks((RecentsTaskLoadPlan)localObject1, -1, bool);
      }
    }
    else
    {
      i = TaskCardView.getNumberOfVisibleTasks(getApplicationContext());
      this.mLaunchedFromHome = ((RecentsActivityLaunchState)localObject2).launchedFromHome;
      TaskStack localTaskStack = ((RecentsTaskLoadPlan)localObject1).getTaskStack();
      RecentsTaskLoadPlan.Options localOptions = new RecentsTaskLoadPlan.Options();
      localOptions.runningTaskId = ((RecentsActivityLaunchState)localObject2).launchedToTaskId;
      localOptions.numVisibleTasks = i;
      localOptions.numVisibleTaskThumbnails = i;
      ((RecentsTaskLoader)localObject3).loadTasks(this, (RecentsTaskLoadPlan)localObject1, localOptions);
      localObject1 = localTaskStack.getStackTasks();
      Collections.reverse((List)localObject1);
      if (this.mTaskStackViewAdapter != null) {
        break label270;
      }
      this.mTaskStackViewAdapter = new TaskStackHorizontalViewAdapter((List)localObject1);
      this.mTaskStackHorizontalGridView = this.mRecentsView.setTaskStackViewAdapter(this.mTaskStackViewAdapter);
      this.mHomeRecentsEnterExitAnimationHolder = new HomeRecentsEnterExitAnimationHolder(getApplicationContext(), this.mTaskStackHorizontalGridView);
      this.mRecentsView.init(localTaskStack);
      if (((RecentsActivityLaunchState)localObject2).launchedToTaskId != -1)
      {
        localObject1 = localTaskStack.getStackTasks();
        j = ((ArrayList)localObject1).size();
        i = 0;
      }
    }
    for (;;)
    {
      if (i < j)
      {
        localObject3 = (Task)((ArrayList)localObject1).get(i);
        if (((Task)localObject3).key.id == ((RecentsActivityLaunchState)localObject2).launchedToTaskId) {
          ((Task)localObject3).isLaunchTarget = true;
        }
      }
      else
      {
        return;
        bool = true;
        break;
        label270:
        this.mTaskStackViewAdapter.setNewStackTasks((List)localObject1);
        break label193;
      }
      i += 1;
    }
  }
  
  boolean dismissRecentsToFocusedTaskOrHome()
  {
    if (Recents.getSystemServices().isRecentsActivityVisible())
    {
      if (this.mRecentsView.launchFocusedTask()) {
        return true;
      }
      dismissRecentsToHome(true);
      return true;
    }
    return false;
  }
  
  void dismissRecentsToHome(boolean paramBoolean)
  {
    Runnable local4 = new Runnable()
    {
      public void run()
      {
        Recents.getSystemServices().sendCloseSystemWindows("homekey");
      }
    };
    DismissRecentsToHomeAnimationStarted localDismissRecentsToHomeAnimationStarted = new DismissRecentsToHomeAnimationStarted(paramBoolean);
    localDismissRecentsToHomeAnimationStarted.addPostAnimationCallback(this.mFinishLaunchHomeRunnable);
    localDismissRecentsToHomeAnimationStarted.addPostAnimationCallback(local4);
    if ((this.mTaskStackHorizontalGridView.getChildCount() > 0) && (paramBoolean))
    {
      this.mHomeRecentsEnterExitAnimationHolder.startExitAnimation(localDismissRecentsToHomeAnimationStarted);
      return;
    }
    local4.run();
    this.mFinishLaunchHomeRunnable.run();
  }
  
  boolean dismissRecentsToLaunchTargetTaskOrHome(boolean paramBoolean)
  {
    if (Recents.getSystemServices().isRecentsActivityVisible())
    {
      if (this.mRecentsView.launchPreviousTask(paramBoolean)) {
        return true;
      }
      dismissRecentsToHome(paramBoolean);
    }
    return false;
  }
  
  public void onBackPressed()
  {
    EventBus.getDefault().send(new ToggleRecentsEvent());
  }
  
  public final void onBusEvent(CancelEnterRecentsWindowAnimationEvent paramCancelEnterRecentsWindowAnimationEvent)
  {
    RecentsActivityLaunchState localRecentsActivityLaunchState = Recents.getConfiguration().getLaunchState();
    int i = localRecentsActivityLaunchState.launchedToTaskId;
    if ((i != -1) && ((paramCancelEnterRecentsWindowAnimationEvent.launchTask == null) || (i != paramCancelEnterRecentsWindowAnimationEvent.launchTask.key.id)))
    {
      paramCancelEnterRecentsWindowAnimationEvent = Recents.getSystemServices();
      paramCancelEnterRecentsWindowAnimationEvent.cancelWindowTransition(localRecentsActivityLaunchState.launchedToTaskId);
      paramCancelEnterRecentsWindowAnimationEvent.cancelThumbnailTransition(getTaskId());
    }
  }
  
  public final void onBusEvent(HideRecentsEvent paramHideRecentsEvent)
  {
    if (paramHideRecentsEvent.triggeredFromAltTab) {
      if (!this.mIgnoreAltTabRelease) {
        dismissRecentsToFocusedTaskOrHome();
      }
    }
    while (!paramHideRecentsEvent.triggeredFromHomeKey) {
      return;
    }
    dismissRecentsToHome(true);
  }
  
  public final void onBusEvent(LaunchTaskFailedEvent paramLaunchTaskFailedEvent)
  {
    dismissRecentsToHome(true);
  }
  
  public final void onBusEvent(ToggleRecentsEvent paramToggleRecentsEvent)
  {
    if (Recents.getConfiguration().getLaunchState().launchedFromHome)
    {
      dismissRecentsToHome(true);
      return;
    }
    dismissRecentsToLaunchTargetTaskOrHome(true);
  }
  
  public final void onBusEvent(AllTaskViewsDismissedEvent paramAllTaskViewsDismissedEvent)
  {
    if (this.mPipManager.isPipShown())
    {
      this.mRecentsView.showEmptyView();
      this.mPipRecentsOverlayManager.requestFocus(false);
      return;
    }
    dismissRecentsToHome(false);
  }
  
  public final void onBusEvent(DeleteTaskDataEvent paramDeleteTaskDataEvent)
  {
    Recents.getTaskLoader().deleteTaskData(paramDeleteTaskDataEvent.task, false);
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    String str = paramDeleteTaskDataEvent.task.key.baseIntent.getComponent().getPackageName();
    localSystemServicesProxy.removeTask(paramDeleteTaskDataEvent.task.key.id, str, paramDeleteTaskDataEvent.task.key.userId);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mFinishedOnStartup = false;
    if (Recents.getSystemServices() == null)
    {
      this.mFinishedOnStartup = true;
      finish();
      return;
    }
    this.mPipRecentsOverlayManager = PipManager.getInstance().getPipRecentsOverlayManager();
    EventBus.getDefault().register(this, 2);
    this.mPackageMonitor = new RecentsPackageMonitor();
    this.mPackageMonitor.register(this);
    setContentView(2130968791);
    this.mRecentsView = ((RecentsTvView)findViewById(2131952181));
    this.mRecentsView.setSystemUiVisibility(1792);
    this.mPipView = findViewById(2131952187);
    this.mPipView.setOnFocusChangeListener(this.mPipViewFocusChangeListener);
    paramBundle = this.mPipManager.getRecentsFocusedPipBounds();
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.mPipView.getLayoutParams();
    localLayoutParams.width = paramBundle.width();
    localLayoutParams.height = paramBundle.height();
    localLayoutParams.leftMargin = paramBundle.left;
    localLayoutParams.topMargin = paramBundle.top;
    this.mPipView.setLayoutParams(localLayoutParams);
    this.mPipRecentsOverlayManager.setCallback(this.mPipRecentsOverlayManagerCallback);
    paramBundle = getWindow().getAttributes();
    paramBundle.privateFlags |= 0x4000;
    paramBundle = new Intent("android.intent.action.MAIN", null);
    paramBundle.addCategory("android.intent.category.HOME");
    paramBundle.addFlags(270532608);
    paramBundle.putExtra("com.android.systemui.recents.tv.RecentsTvActivity.RECENTS_HOME_INTENT_EXTRA", true);
    this.mFinishLaunchHomeRunnable = new FinishRecentsRunnable(paramBundle);
    this.mPipManager.addListener(this.mPipListener);
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    this.mPipManager.removeListener(this.mPipListener);
    if (this.mFinishedOnStartup) {
      return;
    }
    this.mPackageMonitor.unregister();
    EventBus.getDefault().unregister(this);
  }
  
  public void onEnterAnimationComplete()
  {
    super.onEnterAnimationComplete();
    if (this.mLaunchedFromHome) {
      this.mHomeRecentsEnterExitAnimationHolder.startEnterAnimation(this.mPipManager.isPipShown());
    }
    EventBus.getDefault().send(new EnterRecentsWindowAnimationCompletedEvent());
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    switch (paramInt)
    {
    default: 
      return super.onKeyDown(paramInt, paramKeyEvent);
    }
    EventBus.getDefault().send(new DismissFocusedTaskViewEvent());
    return true;
  }
  
  protected void onNewIntent(Intent paramIntent)
  {
    super.onNewIntent(paramIntent);
    setIntent(paramIntent);
  }
  
  public void onPause()
  {
    super.onPause();
    this.mPipRecentsOverlayManager.onRecentsPaused();
  }
  
  public boolean onPreDraw()
  {
    this.mRecentsView.getViewTreeObserver().removeOnPreDrawListener(this);
    if (this.mLaunchedFromHome) {
      this.mHomeRecentsEnterExitAnimationHolder.setEnterFromHomeStartingAnimationValues(this.mPipManager.isPipShown());
    }
    for (;;)
    {
      this.mRecentsView.post(new Runnable()
      {
        public void run()
        {
          Recents.getSystemServices().endProlongedAnimations();
        }
      });
      return true;
      this.mHomeRecentsEnterExitAnimationHolder.setEnterFromAppStartingAnimationValues(this.mPipManager.isPipShown());
    }
  }
  
  public void onResume()
  {
    boolean bool = true;
    super.onResume();
    this.mPipRecentsOverlayManager.onRecentsResumed();
    updateRecentsTasks();
    Object localObject = Recents.getConfiguration().getLaunchState();
    int i;
    if (!((RecentsActivityLaunchState)localObject).launchedFromHome) {
      if (((RecentsActivityLaunchState)localObject).launchedFromApp)
      {
        i = 0;
        if (i != 0) {
          EventBus.getDefault().send(new EnterRecentsWindowAnimationCompletedEvent());
        }
        localObject = Recents.getSystemServices();
        EventBus.getDefault().send(new RecentsVisibilityChangedEvent(this, true));
        if ((this.mTaskStackHorizontalGridView.getStack().getTaskCount() > 1) && (!this.mLaunchedFromHome)) {
          break label245;
        }
        this.mTaskStackHorizontalGridView.setSelectedPosition(0);
        label105:
        this.mRecentsView.getViewTreeObserver().addOnPreDrawListener(this);
        View localView = findViewById(2131952188);
        this.mTalkBackEnabled = ((SystemServicesProxy)localObject).isTouchExplorationEnabled();
        if (this.mTalkBackEnabled)
        {
          localView.setAccessibilityTraversalBefore(2131952186);
          localView.setAccessibilityTraversalAfter(2131952188);
          this.mTaskStackHorizontalGridView.setAccessibilityTraversalAfter(2131952188);
          this.mTaskStackHorizontalGridView.setAccessibilityTraversalBefore(2131952187);
          localView.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              RecentsTvActivity.-get1(RecentsTvActivity.this).requestFocus();
              RecentsTvActivity.-get1(RecentsTvActivity.this).sendAccessibilityEvent(8);
              paramAnonymousView = RecentsTvActivity.-get1(RecentsTvActivity.this).getFocusedTask();
              if ((paramAnonymousView == null) || (paramAnonymousView.isLocked)) {
                return;
              }
              RecentsTvActivity.-get2(RecentsTvActivity.this).removeTask(paramAnonymousView);
              EventBus.getDefault().send(new DeleteTaskDataEvent(paramAnonymousView));
            }
          });
        }
        if (!this.mPipManager.isPipShown()) {
          break label273;
        }
        if (!this.mTalkBackEnabled) {
          break label256;
        }
        this.mPipView.setVisibility(0);
        label214:
        localObject = this.mPipRecentsOverlayManager;
        if (this.mTaskStackViewAdapter.getItemCount() <= 0) {
          break label268;
        }
      }
    }
    for (;;)
    {
      ((PipRecentsOverlayManager)localObject).requestFocus(bool);
      return;
      i = 1;
      break;
      i = 0;
      break;
      label245:
      this.mTaskStackHorizontalGridView.setSelectedPosition(1);
      break label105;
      label256:
      this.mPipView.setVisibility(8);
      break label214;
      label268:
      bool = false;
    }
    label273:
    this.mPipView.setVisibility(8);
    this.mPipRecentsOverlayManager.removePipRecentsOverlayView();
  }
  
  protected void onStop()
  {
    super.onStop();
    this.mIgnoreAltTabRelease = false;
    EventBus.getDefault().send(new RecentsVisibilityChangedEvent(this, false));
    Recents.getConfiguration().getLaunchState().reset();
    finish();
  }
  
  public void onTrimMemory(int paramInt)
  {
    RecentsTaskLoader localRecentsTaskLoader = Recents.getTaskLoader();
    if (localRecentsTaskLoader != null) {
      localRecentsTaskLoader.onTrimMemory(paramInt);
    }
  }
  
  public void onUserInteraction()
  {
    EventBus.getDefault().send(new UserInteractionEvent());
  }
  
  public void requestPipControlsFocus()
  {
    boolean bool = false;
    if (!this.mPipManager.isPipShown()) {
      return;
    }
    this.mTaskStackHorizontalGridView.startFocusLossAnimation();
    PipRecentsOverlayManager localPipRecentsOverlayManager = this.mPipRecentsOverlayManager;
    if (this.mTaskStackViewAdapter.getItemCount() > 0) {
      bool = true;
    }
    localPipRecentsOverlayManager.requestFocus(bool);
  }
  
  class FinishRecentsRunnable
    implements Runnable
  {
    Intent mLaunchIntent;
    
    public FinishRecentsRunnable(Intent paramIntent)
    {
      this.mLaunchIntent = paramIntent;
    }
    
    public void run()
    {
      try
      {
        ActivityOptions localActivityOptions = ActivityOptions.makeCustomAnimation(RecentsTvActivity.this, 2131034294, 2131034295);
        RecentsTvActivity.this.startActivityAsUser(this.mLaunchIntent, localActivityOptions.toBundle(), UserHandle.CURRENT);
        return;
      }
      catch (Exception localException)
      {
        Log.e("RecentsTvActivity", RecentsTvActivity.this.getString(2131690329, new Object[] { "Home" }), localException);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\tv\RecentsTvActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */