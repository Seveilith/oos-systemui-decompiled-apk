package com.android.systemui.recents;

import android.app.Activity;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityOptions;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Interpolators;
import com.android.systemui.Prefs;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.CancelEnterRecentsWindowAnimationEvent;
import com.android.systemui.recents.events.activity.ConfigurationChangedEvent;
import com.android.systemui.recents.events.activity.DebugFlagsChangedEvent;
import com.android.systemui.recents.events.activity.DismissRecentsToHomeAnimationStarted;
import com.android.systemui.recents.events.activity.DockedFirstAnimationFrameEvent;
import com.android.systemui.recents.events.activity.DockedTopTaskEvent;
import com.android.systemui.recents.events.activity.EnterRecentsWindowAnimationCompletedEvent;
import com.android.systemui.recents.events.activity.EnterRecentsWindowLastAnimationFrameEvent;
import com.android.systemui.recents.events.activity.ExitRecentsWindowFirstAnimationFrameEvent;
import com.android.systemui.recents.events.activity.HideRecentsEvent;
import com.android.systemui.recents.events.activity.IterateRecentsEvent;
import com.android.systemui.recents.events.activity.LaunchTaskFailedEvent;
import com.android.systemui.recents.events.activity.LaunchTaskSucceededEvent;
import com.android.systemui.recents.events.activity.MultiWindowStateChangedEvent;
import com.android.systemui.recents.events.activity.ToggleRecentsEvent;
import com.android.systemui.recents.events.component.RecentsVisibilityChangedEvent;
import com.android.systemui.recents.events.component.ScreenPinningRequestEvent;
import com.android.systemui.recents.events.ui.AllTaskViewsDismissedEvent;
import com.android.systemui.recents.events.ui.DeleteTaskDataEvent;
import com.android.systemui.recents.events.ui.HideIncompatibleAppOverlayEvent;
import com.android.systemui.recents.events.ui.RecentsDrawnEvent;
import com.android.systemui.recents.events.ui.ShowApplicationInfoEvent;
import com.android.systemui.recents.events.ui.ShowIncompatibleAppOverlayEvent;
import com.android.systemui.recents.events.ui.StackViewScrolledEvent;
import com.android.systemui.recents.events.ui.UpdateFreeformTaskViewVisibilityEvent;
import com.android.systemui.recents.events.ui.UserInteractionEvent;
import com.android.systemui.recents.events.ui.focus.DismissFocusedTaskViewEvent;
import com.android.systemui.recents.events.ui.focus.FocusNextTaskViewEvent;
import com.android.systemui.recents.events.ui.focus.FocusPreviousTaskViewEvent;
import com.android.systemui.recents.misc.DozeTrigger;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.misc.Utilities;
import com.android.systemui.recents.model.RecentsPackageMonitor;
import com.android.systemui.recents.model.RecentsTaskLoadPlan;
import com.android.systemui.recents.model.RecentsTaskLoadPlan.Options;
import com.android.systemui.recents.model.RecentsTaskLoader;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.Task.TaskKey;
import com.android.systemui.recents.model.TaskStack;
import com.android.systemui.recents.views.RecentsView;
import com.android.systemui.recents.views.SystemBarScrimViews;
import com.android.systemui.util.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

public class RecentsActivity
  extends Activity
  implements ViewTreeObserver.OnPreDrawListener
{
  ImageButton mDismissAllBtn;
  private boolean mFinishedOnStartup;
  private int mFocusTimerDuration;
  private Handler mHandler = new Handler();
  private Intent mHomeIntent;
  private boolean mIgnoreAltTabRelease;
  private View mIncompatibleAppOverlay;
  private boolean mIsVisible;
  private DozeTrigger mIterateTrigger;
  private int mLastDeviceOrientation = 0;
  private int mLastDisplayDensity;
  private long mLastTabKeyEventTime;
  private RecentsPackageMonitor mPackageMonitor;
  private boolean mReceivedNewIntent;
  private final ViewTreeObserver.OnPreDrawListener mRecentsDrawnEventListener = new ViewTreeObserver.OnPreDrawListener()
  {
    public boolean onPreDraw()
    {
      RecentsActivity.-get1(RecentsActivity.this).getViewTreeObserver().removeOnPreDrawListener(this);
      EventBus.getDefault().post(new RecentsDrawnEvent());
      return true;
    }
  };
  private RecentsView mRecentsView;
  private SystemBarScrimViews mScrimViews;
  private final Runnable mSendEnterWindowAnimationCompleteRunnable = new -void__init___LambdaImpl0();
  final BroadcastReceiver mSystemBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      String str = paramAnonymousIntent.getAction();
      if (str.equals("android.intent.action.SCREEN_OFF")) {
        if (paramAnonymousIntent.getIntExtra("screenoff_reason", 0) == 7) {
          return;
        }
      }
      long l1;
      long l2;
      do
      {
        do
        {
          RecentsActivity.this.dismissRecentsToHomeIfVisible(false);
          do
          {
            return;
          } while (!str.equals("android.intent.action.TIME_SET"));
          l1 = Prefs.getLong(RecentsActivity.this, "OverviewLastStackTaskActiveTime", -1L);
        } while (l1 == -1L);
        l2 = System.currentTimeMillis();
        if (Utils.DEBUG_ONEPLUS) {
          Log.d("RecentsActivity", "TIME_SET, " + l2 + ", " + l1);
        }
      } while (l2 >= l1);
      paramAnonymousContext = Recents.getTaskLoader().createLoadPlan(paramAnonymousContext);
      paramAnonymousContext.preloadRawTasks(false);
      paramAnonymousContext = paramAnonymousContext.getRawTasks();
      int i = paramAnonymousContext.size() - 1;
      while (i >= 0)
      {
        paramAnonymousIntent = (ActivityManager.RecentTaskInfo)paramAnonymousContext.get(i);
        if ((l2 <= paramAnonymousIntent.lastActiveTime) && (paramAnonymousIntent.lastActiveTime < l1))
        {
          if (Utils.DEBUG_ONEPLUS) {
            Log.d("RecentsActivity", "remove, " + l2 + ", " + paramAnonymousIntent.lastActiveTime + ", " + l1);
          }
          str = paramAnonymousIntent.baseIntent.getComponent().getPackageName();
          Recents.getSystemServices().removeTask(paramAnonymousIntent.persistentId, str, paramAnonymousIntent.userId);
        }
        i -= 1;
      }
      Prefs.putLong(RecentsActivity.this, "OverviewLastStackTaskActiveTime", l2);
    }
  };
  private final UserInteractionEvent mUserInteractionEvent = new UserInteractionEvent();
  
  private void reloadStackView()
  {
    Object localObject3 = Recents.getTaskLoader();
    Object localObject2 = RecentsImpl.consumeInstanceLoadPlan();
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = ((RecentsTaskLoader)localObject3).createLoadPlan(this);
    }
    localObject2 = Recents.getConfiguration().getLaunchState();
    int i;
    boolean bool1;
    boolean bool2;
    if (!((RecentsTaskLoadPlan)localObject1).hasTasks())
    {
      i = ((RecentsActivityLaunchState)localObject2).launchedToTaskId;
      if (((RecentsActivityLaunchState)localObject2).launchedFromHome)
      {
        bool1 = false;
        ((RecentsTaskLoader)localObject3).preloadTasks((RecentsTaskLoadPlan)localObject1, i, bool1);
      }
    }
    else
    {
      RecentsTaskLoadPlan.Options localOptions = new RecentsTaskLoadPlan.Options();
      localOptions.runningTaskId = ((RecentsActivityLaunchState)localObject2).launchedToTaskId;
      localOptions.numVisibleTasks = ((RecentsActivityLaunchState)localObject2).launchedNumVisibleTasks;
      localOptions.numVisibleTaskThumbnails = ((RecentsActivityLaunchState)localObject2).launchedNumVisibleThumbnails;
      ((RecentsTaskLoader)localObject3).loadTasks(this, (RecentsTaskLoadPlan)localObject1, localOptions);
      localObject1 = ((RecentsTaskLoadPlan)localObject1).getTaskStack();
      localObject3 = this.mRecentsView;
      bool2 = this.mIsVisible;
      if (((TaskStack)localObject1).getTaskCount() != 0) {
        break label315;
      }
      bool1 = true;
      label145:
      ((RecentsView)localObject3).onReload(bool2, bool1);
      this.mRecentsView.updateStack((TaskStack)localObject1, true);
      if (!((RecentsActivityLaunchState)localObject2).launchedViaDockGesture) {
        break label320;
      }
      bool1 = false;
      label172:
      localObject3 = this.mScrimViews;
      if (((TaskStack)localObject1).getTaskCount() <= 0) {
        break label325;
      }
      bool2 = true;
      label188:
      ((SystemBarScrimViews)localObject3).updateNavBarScrim(bool1, bool2, null);
      if (((RecentsActivityLaunchState)localObject2).launchedFromHome) {
        break label335;
      }
      if (!((RecentsActivityLaunchState)localObject2).launchedFromApp) {
        break label330;
      }
      i = 0;
      label214:
      if (i != 0) {
        EventBus.getDefault().send(new EnterRecentsWindowAnimationCompletedEvent());
      }
      if (!((RecentsActivityLaunchState)localObject2).launchedWithAltTab) {
        break label340;
      }
      MetricsLogger.count(this, "overview_trigger_alttab", 1);
      label246:
      if (!((RecentsActivityLaunchState)localObject2).launchedFromApp) {
        break label355;
      }
      localObject2 = ((TaskStack)localObject1).getLaunchTarget();
      if (localObject2 == null) {
        break label350;
      }
      i = ((TaskStack)localObject1).indexOfStackTask((Task)localObject2);
      label274:
      MetricsLogger.count(this, "overview_source_app", 1);
      MetricsLogger.histogram(this, "overview_source_app_index", i);
    }
    for (;;)
    {
      MetricsLogger.histogram(this, "overview_task_count", this.mRecentsView.getStack().getTaskCount());
      this.mIsVisible = true;
      return;
      bool1 = true;
      break;
      label315:
      bool1 = false;
      break label145;
      label320:
      bool1 = true;
      break label172;
      label325:
      bool2 = false;
      break label188;
      label330:
      i = 1;
      break label214;
      label335:
      i = 0;
      break label214;
      label340:
      MetricsLogger.count(this, "overview_trigger_nav_btn", 1);
      break label246;
      label350:
      i = 0;
      break label274;
      label355:
      MetricsLogger.count(this, "overview_source_home", 1);
    }
  }
  
  boolean dismissRecentsToFocusedTask(int paramInt)
  {
    return (Recents.getSystemServices().isRecentsActivityVisible()) && (this.mRecentsView.launchFocusedTask(paramInt));
  }
  
  boolean dismissRecentsToFocusedTaskOrHome()
  {
    if (Recents.getSystemServices().isRecentsActivityVisible())
    {
      if (this.mRecentsView.launchFocusedTask(0)) {
        return true;
      }
      dismissRecentsToHome(true);
      return true;
    }
    return false;
  }
  
  void dismissRecentsToHome(boolean paramBoolean)
  {
    dismissRecentsToHome(paramBoolean, null);
  }
  
  void dismissRecentsToHome(boolean paramBoolean, ActivityOptions paramActivityOptions)
  {
    DismissRecentsToHomeAnimationStarted localDismissRecentsToHomeAnimationStarted = new DismissRecentsToHomeAnimationStarted(paramBoolean);
    localDismissRecentsToHomeAnimationStarted.addPostAnimationCallback(new LaunchHomeRunnable(this.mHomeIntent, paramActivityOptions));
    Recents.getSystemServices().sendCloseSystemWindows("homekey");
    EventBus.getDefault().send(localDismissRecentsToHomeAnimationStarted);
  }
  
  boolean dismissRecentsToHomeIfVisible(boolean paramBoolean)
  {
    if (Recents.getSystemServices().isRecentsActivityVisible())
    {
      dismissRecentsToHome(paramBoolean);
      return true;
    }
    return false;
  }
  
  boolean dismissRecentsToLaunchTargetTaskOrHome()
  {
    if (Recents.getSystemServices().isRecentsActivityVisible())
    {
      if (this.mRecentsView.launchPreviousTask()) {
        return true;
      }
      dismissRecentsToHome(true);
    }
    return false;
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    EventBus.getDefault().dump(paramString, paramPrintWriter);
    Recents.getTaskLoader().dump(paramString, paramPrintWriter);
    paramArrayOfString = Integer.toHexString(System.identityHashCode(this));
    long l = Prefs.getLong(this, "OverviewLastStackTaskActiveTime", -1L);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("RecentsActivity");
    paramPrintWriter.print(" visible=");
    if (this.mIsVisible) {}
    for (paramFileDescriptor = "Y";; paramFileDescriptor = "N")
    {
      paramPrintWriter.print(paramFileDescriptor);
      paramPrintWriter.print(" lastStackTaskActiveTime=");
      paramPrintWriter.print(l);
      paramPrintWriter.print(" currentTime=");
      paramPrintWriter.print(System.currentTimeMillis());
      paramPrintWriter.print(" [0x");
      paramPrintWriter.print(paramArrayOfString);
      paramPrintWriter.print("]");
      paramPrintWriter.println();
      if (this.mRecentsView != null) {
        this.mRecentsView.dump(paramString, paramPrintWriter);
      }
      Recents.getSystemServices().dump("RecentsActivity", paramPrintWriter);
      paramPrintWriter.println();
      return;
    }
  }
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    EventBus.getDefault().register(this.mScrimViews, 2);
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
  
  public final void onBusEvent(DebugFlagsChangedEvent paramDebugFlagsChangedEvent)
  {
    finish();
  }
  
  public final void onBusEvent(DockedFirstAnimationFrameEvent paramDockedFirstAnimationFrameEvent)
  {
    this.mRecentsView.getViewTreeObserver().addOnPreDrawListener(this);
    this.mRecentsView.invalidate();
  }
  
  public final void onBusEvent(DockedTopTaskEvent paramDockedTopTaskEvent)
  {
    this.mRecentsView.getViewTreeObserver().addOnPreDrawListener(this.mRecentsDrawnEventListener);
    this.mRecentsView.invalidate();
  }
  
  public final void onBusEvent(EnterRecentsWindowLastAnimationFrameEvent paramEnterRecentsWindowLastAnimationFrameEvent)
  {
    EventBus.getDefault().send(new UpdateFreeformTaskViewVisibilityEvent(true));
    this.mRecentsView.getViewTreeObserver().addOnPreDrawListener(this);
    this.mRecentsView.invalidate();
  }
  
  public final void onBusEvent(ExitRecentsWindowFirstAnimationFrameEvent paramExitRecentsWindowFirstAnimationFrameEvent)
  {
    if (this.mRecentsView.isLastTaskLaunchedFreeform()) {
      EventBus.getDefault().send(new UpdateFreeformTaskViewVisibilityEvent(false));
    }
    this.mRecentsView.getViewTreeObserver().addOnPreDrawListener(this);
    this.mRecentsView.invalidate();
  }
  
  public final void onBusEvent(HideRecentsEvent paramHideRecentsEvent)
  {
    if (this.mIsVisible) {
      Log.d("RecentsActivity", "HideRecentsEvent, " + paramHideRecentsEvent.triggeredFromAltTab + ", " + paramHideRecentsEvent.triggeredFromHomeKey);
    }
    if (paramHideRecentsEvent.triggeredFromAltTab)
    {
      if (!this.mIgnoreAltTabRelease) {
        dismissRecentsToFocusedTaskOrHome();
      }
      return;
    }
    if (paramHideRecentsEvent.triggeredFromHomeKey)
    {
      dismissRecentsToHome(true);
      EventBus.getDefault().send(this.mUserInteractionEvent);
      return;
    }
    this.mRecentsView.showDismissAllButton();
  }
  
  public final void onBusEvent(IterateRecentsEvent paramIterateRecentsEvent)
  {
    paramIterateRecentsEvent = Recents.getDebugFlags();
    int i = 0;
    if (paramIterateRecentsEvent.isFastToggleRecentsEnabled())
    {
      i = getResources().getInteger(2131623998);
      this.mIterateTrigger.setDozeDuration(i);
      if (this.mIterateTrigger.isDozing()) {
        break label71;
      }
      this.mIterateTrigger.startDozing();
    }
    for (;;)
    {
      EventBus.getDefault().send(new FocusNextTaskViewEvent(i));
      MetricsLogger.action(this, 276);
      return;
      label71:
      this.mIterateTrigger.poke();
    }
  }
  
  public final void onBusEvent(LaunchTaskFailedEvent paramLaunchTaskFailedEvent)
  {
    dismissRecentsToHome(true);
    MetricsLogger.count(this, "overview_task_launch_failed", 1);
  }
  
  public final void onBusEvent(LaunchTaskSucceededEvent paramLaunchTaskSucceededEvent)
  {
    MetricsLogger.histogram(this, "overview_task_launch_index", paramLaunchTaskSucceededEvent.taskIndexFromStackFront);
  }
  
  public final void onBusEvent(ToggleRecentsEvent paramToggleRecentsEvent)
  {
    if (Recents.getConfiguration().getLaunchState().launchedFromHome)
    {
      dismissRecentsToHome(true);
      return;
    }
    dismissRecentsToLaunchTargetTaskOrHome();
  }
  
  public final void onBusEvent(ScreenPinningRequestEvent paramScreenPinningRequestEvent)
  {
    MetricsLogger.count(this, "overview_screen_pinned", 1);
  }
  
  public final void onBusEvent(AllTaskViewsDismissedEvent paramAllTaskViewsDismissedEvent)
  {
    if (Recents.getSystemServices().hasDockedTask()) {
      this.mRecentsView.showEmptyView(paramAllTaskViewsDismissedEvent.msgResId);
    }
    for (;;)
    {
      MetricsLogger.count(this, "overview_task_all_dismissed", 1);
      return;
      dismissRecentsToHome(false);
    }
  }
  
  public final void onBusEvent(DeleteTaskDataEvent paramDeleteTaskDataEvent)
  {
    if (paramDeleteTaskDataEvent.task.isLocked)
    {
      Log.d("RecentsActivity", "Task should not be removed: id " + paramDeleteTaskDataEvent.task.key.id + " pkgName " + paramDeleteTaskDataEvent.task.key.baseIntent.getComponent().getPackageName() + " isLocked " + paramDeleteTaskDataEvent.task.isLocked);
      return;
    }
    Recents.getTaskLoader().deleteTaskData(paramDeleteTaskDataEvent.task, false);
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    String str = paramDeleteTaskDataEvent.task.key.baseIntent.getComponent().getPackageName();
    localSystemServicesProxy.removeTask(paramDeleteTaskDataEvent.task.key.id, str, paramDeleteTaskDataEvent.task.key.userId);
  }
  
  public final void onBusEvent(HideIncompatibleAppOverlayEvent paramHideIncompatibleAppOverlayEvent)
  {
    if (this.mIncompatibleAppOverlay != null) {
      this.mIncompatibleAppOverlay.animate().alpha(0.0F).setDuration(150L).setInterpolator(Interpolators.ALPHA_OUT).start();
    }
  }
  
  public final void onBusEvent(ShowApplicationInfoEvent paramShowApplicationInfoEvent)
  {
    Intent localIntent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", paramShowApplicationInfoEvent.task.key.getComponent().getPackageName(), null));
    localIntent.setComponent(localIntent.resolveActivity(getPackageManager()));
    TaskStackBuilder.create(this).addNextIntentWithParentStack(localIntent).startActivities(null, new UserHandle(paramShowApplicationInfoEvent.task.key.userId));
    MetricsLogger.count(this, "overview_app_info", 1);
  }
  
  public final void onBusEvent(ShowIncompatibleAppOverlayEvent paramShowIncompatibleAppOverlayEvent)
  {
    if (this.mIncompatibleAppOverlay == null)
    {
      this.mIncompatibleAppOverlay = Utilities.findViewStubById(this, 2131952183).inflate();
      this.mIncompatibleAppOverlay.setWillNotDraw(false);
      this.mIncompatibleAppOverlay.setVisibility(0);
    }
    this.mIncompatibleAppOverlay.animate().alpha(1.0F).setDuration(150L).setInterpolator(Interpolators.ALPHA_IN).start();
  }
  
  public final void onBusEvent(StackViewScrolledEvent paramStackViewScrolledEvent)
  {
    this.mIgnoreAltTabRelease = true;
  }
  
  public final void onBusEvent(UserInteractionEvent paramUserInteractionEvent)
  {
    this.mIterateTrigger.stopDozing();
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    boolean bool3 = true;
    super.onConfigurationChanged(paramConfiguration);
    paramConfiguration = Utilities.getAppConfiguration(this);
    int i = this.mRecentsView.getStack().getStackTaskCount();
    EventBus localEventBus = EventBus.getDefault();
    boolean bool1;
    boolean bool2;
    if (this.mLastDeviceOrientation != paramConfiguration.orientation)
    {
      bool1 = true;
      if (this.mLastDisplayDensity == paramConfiguration.densityDpi) {
        break label100;
      }
      bool2 = true;
      label56:
      if (i <= 0) {
        break label106;
      }
    }
    for (;;)
    {
      localEventBus.send(new ConfigurationChangedEvent(false, bool1, bool2, bool3));
      this.mLastDeviceOrientation = paramConfiguration.orientation;
      this.mLastDisplayDensity = paramConfiguration.densityDpi;
      return;
      bool1 = false;
      break;
      label100:
      bool2 = false;
      break label56;
      label106:
      bool3 = false;
    }
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
    EventBus.getDefault().register(this, 2);
    this.mPackageMonitor = new RecentsPackageMonitor();
    this.mPackageMonitor.register(this);
    setContentView(2130968788);
    takeKeyEvents(true);
    this.mRecentsView = ((RecentsView)findViewById(2131952181));
    this.mRecentsView.setSystemUiVisibility(1792);
    this.mDismissAllBtn = ((ImageButton)findViewById(2131952182));
    this.mRecentsView.setDismissAllBtn(this.mDismissAllBtn);
    this.mScrimViews = new SystemBarScrimViews(this);
    paramBundle = getWindow().getAttributes();
    paramBundle.privateFlags |= 0x4000;
    paramBundle = Utilities.getAppConfiguration(this);
    this.mLastDeviceOrientation = paramBundle.orientation;
    this.mLastDisplayDensity = paramBundle.densityDpi;
    this.mFocusTimerDuration = getResources().getInteger(2131623997);
    this.mIterateTrigger = new DozeTrigger(this.mFocusTimerDuration, new Runnable()
    {
      public void run()
      {
        RecentsActivity.this.dismissRecentsToFocusedTask(288);
      }
    });
    getWindow().setBackgroundDrawable(this.mRecentsView.getBackgroundScrim());
    this.mHomeIntent = new Intent("android.intent.action.MAIN", null);
    this.mHomeIntent.addCategory("android.intent.category.HOME");
    this.mHomeIntent.addFlags(270532608);
    paramBundle = new IntentFilter();
    paramBundle.addAction("android.intent.action.SCREEN_OFF");
    paramBundle.addAction("android.intent.action.TIME_SET");
    registerReceiver(this.mSystemBroadcastReceiver, paramBundle);
    getWindow().addPrivateFlags(64);
    reloadStackView();
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    if (this.mFinishedOnStartup) {
      return;
    }
    Recents.getSystemServices().setDeepCleaning(false);
    unregisterReceiver(this.mSystemBroadcastReceiver);
    this.mPackageMonitor.unregister();
    EventBus.getDefault().unregister(this);
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    EventBus.getDefault().unregister(this.mScrimViews);
  }
  
  public void onEnterAnimationComplete()
  {
    super.onEnterAnimationComplete();
    this.mHandler.removeCallbacks(this.mSendEnterWindowAnimationCompleteRunnable);
    if (this.mRecentsView != null) {
      this.mRecentsView.notifyEnterAnimationComplete();
    }
    if (!this.mReceivedNewIntent)
    {
      this.mHandler.post(this.mSendEnterWindowAnimationCompleteRunnable);
      return;
    }
    this.mSendEnterWindowAnimationCompleteRunnable.run();
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    switch (paramInt)
    {
    }
    label143:
    do
    {
      return super.onKeyDown(paramInt, paramKeyEvent);
      paramInt = getResources().getInteger(2131623999);
      if (SystemClock.elapsedRealtime() - this.mLastTabKeyEventTime > paramInt)
      {
        paramInt = 1;
        if ((paramKeyEvent.getRepeatCount() <= 0) || (paramInt != 0))
        {
          if (!paramKeyEvent.isShiftPressed()) {
            break label143;
          }
          EventBus.getDefault().send(new FocusPreviousTaskViewEvent());
        }
      }
      for (;;)
      {
        this.mLastTabKeyEventTime = SystemClock.elapsedRealtime();
        if (paramKeyEvent.isAltPressed()) {
          this.mIgnoreAltTabRelease = false;
        }
        return true;
        paramInt = 0;
        break;
        EventBus.getDefault().send(new FocusNextTaskViewEvent(0));
      }
      EventBus.getDefault().send(new FocusNextTaskViewEvent(0));
      return true;
      EventBus.getDefault().send(new FocusPreviousTaskViewEvent());
      return true;
    } while (paramKeyEvent.getRepeatCount() > 0);
    EventBus.getDefault().send(new DismissFocusedTaskViewEvent());
    MetricsLogger.histogram(this, "overview_task_dismissed_source", 0);
    return true;
  }
  
  public void onMultiWindowModeChanged(boolean paramBoolean)
  {
    super.onMultiWindowModeChanged(paramBoolean);
    Object localObject1 = Recents.getConfiguration().getLaunchState();
    Object localObject2 = Recents.getTaskLoader();
    RecentsTaskLoadPlan localRecentsTaskLoadPlan = ((RecentsTaskLoader)localObject2).createLoadPlan(this);
    ((RecentsTaskLoader)localObject2).preloadTasks(localRecentsTaskLoadPlan, -1, false);
    RecentsTaskLoadPlan.Options localOptions = new RecentsTaskLoadPlan.Options();
    localOptions.numVisibleTasks = ((RecentsActivityLaunchState)localObject1).launchedNumVisibleTasks;
    localOptions.numVisibleTaskThumbnails = ((RecentsActivityLaunchState)localObject1).launchedNumVisibleThumbnails;
    ((RecentsTaskLoader)localObject2).loadTasks(this, localRecentsTaskLoadPlan, localOptions);
    localObject1 = localRecentsTaskLoadPlan.getTaskStack();
    int i = ((TaskStack)localObject1).getStackTaskCount();
    boolean bool1;
    if (i > 0)
    {
      bool1 = true;
      localObject2 = EventBus.getDefault();
      if (i <= 0) {
        break label145;
      }
    }
    label145:
    for (boolean bool2 = true;; bool2 = false)
    {
      ((EventBus)localObject2).send(new ConfigurationChangedEvent(true, false, false, bool2));
      EventBus.getDefault().send(new MultiWindowStateChangedEvent(paramBoolean, bool1, (TaskStack)localObject1));
      return;
      bool1 = false;
      break;
    }
  }
  
  protected void onNewIntent(Intent paramIntent)
  {
    super.onNewIntent(paramIntent);
    this.mReceivedNewIntent = true;
    reloadStackView();
  }
  
  protected void onPause()
  {
    super.onPause();
    this.mIgnoreAltTabRelease = false;
    this.mIterateTrigger.stopDozing();
  }
  
  public boolean onPreDraw()
  {
    this.mRecentsView.getViewTreeObserver().removeOnPreDrawListener(this);
    this.mRecentsView.post(new Runnable()
    {
      public void run()
      {
        Recents.getSystemServices().endProlongedAnimations();
      }
    });
    return true;
  }
  
  protected void onStart()
  {
    super.onStart();
    if ((!this.mReceivedNewIntent) && (this.mRecentsView != null) && (this.mRecentsView.getStack() != null) && (this.mRecentsView.getStack().getTaskCount() > 0))
    {
      if (Utils.DEBUG_ONEPLUS) {
        Log.d("RecentsActivity", "reload again when started without toggle");
      }
      reloadStackView();
    }
    Recents.getSystemServices().setDeepCleaning(false);
    EventBus.getDefault().send(new RecentsVisibilityChangedEvent(this, true));
    MetricsLogger.visible(this, 224);
    this.mRecentsView.getViewTreeObserver().addOnPreDrawListener(this.mRecentsDrawnEventListener);
  }
  
  protected void onStop()
  {
    super.onStop();
    this.mIsVisible = false;
    this.mReceivedNewIntent = false;
    EventBus.getDefault().send(new RecentsVisibilityChangedEvent(this, false));
    MetricsLogger.hidden(this, 224);
    Recents.getConfiguration().getLaunchState().reset();
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
    EventBus.getDefault().send(this.mUserInteractionEvent);
  }
  
  class LaunchHomeRunnable
    implements Runnable
  {
    Intent mLaunchIntent;
    ActivityOptions mOpts;
    
    public LaunchHomeRunnable(Intent paramIntent, ActivityOptions paramActivityOptions)
    {
      this.mLaunchIntent = paramIntent;
      this.mOpts = paramActivityOptions;
    }
    
    public void run()
    {
      try
      {
        RecentsActivity.-get0(RecentsActivity.this).post(new -void_run__LambdaImpl0());
        return;
      }
      catch (Exception localException)
      {
        Log.e("RecentsActivity", RecentsActivity.this.getString(2131690329, new Object[] { "Home" }), localException);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\RecentsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */