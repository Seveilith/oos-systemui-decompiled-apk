package com.android.systemui.recents;

import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityOptions;
import android.app.ActivityOptions.OnAnimationFinishedListener;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import android.util.MutableBoolean;
import android.view.AppTransitionAnimationSpec;
import android.view.LayoutInflater;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.policy.DockedDividerUtils;
import com.android.systemui.SystemUIApplication;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.DockedTopTaskEvent;
import com.android.systemui.recents.events.activity.EnterRecentsWindowLastAnimationFrameEvent;
import com.android.systemui.recents.events.activity.HideRecentsEvent;
import com.android.systemui.recents.events.activity.IterateRecentsEvent;
import com.android.systemui.recents.events.activity.LaunchNextTaskRequestEvent;
import com.android.systemui.recents.events.activity.RecentsActivityStartingEvent;
import com.android.systemui.recents.events.activity.ToggleRecentsEvent;
import com.android.systemui.recents.events.ui.DraggingInRecentsEndedEvent;
import com.android.systemui.recents.events.ui.DraggingInRecentsEvent;
import com.android.systemui.recents.misc.DozeTrigger;
import com.android.systemui.recents.misc.ForegroundThread;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.misc.SystemServicesProxy.TaskStackListener;
import com.android.systemui.recents.model.RecentsTaskLoadPlan;
import com.android.systemui.recents.model.RecentsTaskLoadPlan.Options;
import com.android.systemui.recents.model.RecentsTaskLoader;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.Task.TaskKey;
import com.android.systemui.recents.model.TaskGrouping;
import com.android.systemui.recents.model.TaskStack;
import com.android.systemui.recents.views.TaskStackLayoutAlgorithm;
import com.android.systemui.recents.views.TaskStackLayoutAlgorithm.StackState;
import com.android.systemui.recents.views.TaskStackLayoutAlgorithm.VisibilityReport;
import com.android.systemui.recents.views.TaskStackView;
import com.android.systemui.recents.views.TaskStackViewScroller;
import com.android.systemui.recents.views.TaskViewHeader;
import com.android.systemui.recents.views.TaskViewTransform;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import com.android.systemui.util.Utils;
import java.util.ArrayList;

public class RecentsImpl
  implements ActivityOptions.OnAnimationFinishedListener
{
  protected static RecentsTaskLoadPlan sInstanceLoadPlan;
  protected Context mContext;
  boolean mDraggingInRecents;
  protected TaskStackView mDummyStackView;
  DozeTrigger mFastAltTabTrigger = new DozeTrigger(225, new Runnable()
  {
    public void run()
    {
      RecentsImpl.this.showRecents(RecentsImpl.this.mTriggeredFromAltTab, false, true, false, false, -1);
    }
  });
  protected Handler mHandler;
  TaskViewHeader mHeaderBar;
  final Object mHeaderBarLock = new Object();
  protected long mLastToggleTime;
  boolean mLaunchedWhileDocking;
  int mNavBarHeight;
  int mNavBarWidth;
  int mStatusBarHeight;
  int mTaskBarHeight;
  Rect mTaskStackBounds = new Rect();
  TaskStackListenerImpl mTaskStackListener;
  protected Bitmap mThumbTransitionBitmapCache;
  TaskViewTransform mTmpTransform = new TaskViewTransform();
  protected boolean mTriggeredFromAltTab;
  
  public RecentsImpl(Context paramContext)
  {
    this.mContext = paramContext;
    this.mHandler = new Handler();
    ForegroundThread.get();
    this.mTaskStackListener = new TaskStackListenerImpl();
    Recents.getSystemServices().registerTaskStackListener(this.mTaskStackListener);
    paramContext = LayoutInflater.from(this.mContext);
    this.mDummyStackView = new TaskStackView(this.mContext);
    this.mHeaderBar = ((TaskViewHeader)paramContext.inflate(2130968795, null, false));
    reloadResources();
  }
  
  private void calculateWindowStableInsets(Rect paramRect1, Rect paramRect2)
  {
    Rect localRect1 = new Rect(Recents.getSystemServices().getDisplayRect());
    localRect1.inset(paramRect1);
    Rect localRect2 = new Rect(paramRect2);
    localRect2.intersect(localRect1);
    localRect2.left -= paramRect2.left;
    localRect2.top -= paramRect2.top;
    paramRect2.right -= localRect2.right;
    paramRect2.bottom -= localRect2.bottom;
  }
  
  public static RecentsTaskLoadPlan consumeInstanceLoadPlan()
  {
    RecentsTaskLoadPlan localRecentsTaskLoadPlan = sInstanceLoadPlan;
    sInstanceLoadPlan = null;
    return localRecentsTaskLoadPlan;
  }
  
  private Bitmap drawThumbnailTransitionBitmap(Task paramTask, TaskViewTransform paramTaskViewTransform, Bitmap paramBitmap)
  {
    Object localObject2 = Recents.getSystemServices();
    if ((paramTaskViewTransform != null) && (paramTask.key != null)) {
      synchronized (this.mHeaderBarLock)
      {
        if (!paramTask.isSystemApp)
        {
          bool = ((SystemServicesProxy)localObject2).isInSafeMode();
          this.mHeaderBar.onTaskViewSizeChanged((int)paramTaskViewTransform.rect.width(), (int)paramTaskViewTransform.rect.height());
          paramBitmap.eraseColor(0);
          localObject2 = new Canvas(paramBitmap);
          Drawable localDrawable = this.mHeaderBar.getIconView().getDrawable();
          if (localDrawable != null) {
            localDrawable.setCallback(null);
          }
          this.mHeaderBar.bindToTask(paramTask, false, bool);
          this.mHeaderBar.onTaskDataLoaded();
          this.mHeaderBar.setDimAlpha(paramTaskViewTransform.dimAlpha);
          this.mHeaderBar.draw((Canvas)localObject2);
          ((Canvas)localObject2).setBitmap(null);
          return paramBitmap.createAshmemBitmap();
        }
        boolean bool = false;
      }
    }
    return null;
  }
  
  private ActivityOptions getThumbnailTransitionActivityOptions(ActivityManager.RunningTaskInfo paramRunningTaskInfo, TaskStackView paramTaskStackView, Rect paramRect)
  {
    if ((paramRunningTaskInfo != null) && (paramRunningTaskInfo.stackId == 2))
    {
      paramRunningTaskInfo = new ArrayList();
      ArrayList localArrayList = paramTaskStackView.getStack().getStackTasks();
      TaskStackLayoutAlgorithm localTaskStackLayoutAlgorithm = paramTaskStackView.getStackAlgorithm();
      TaskStackViewScroller localTaskStackViewScroller = paramTaskStackView.getScroller();
      paramTaskStackView.updateLayoutAlgorithm(true);
      paramTaskStackView.updateToInitialState();
      int i = localArrayList.size() - 1;
      while (i >= 0)
      {
        paramTaskStackView = (Task)localArrayList.get(i);
        if (paramTaskStackView.isFreeformTask())
        {
          this.mTmpTransform = localTaskStackLayoutAlgorithm.getStackTransformScreenCoordinates(paramTaskStackView, localTaskStackViewScroller.getStackScroll(), this.mTmpTransform, null, paramRect);
          Bitmap localBitmap = drawThumbnailTransitionBitmap(paramTaskStackView, this.mTmpTransform, this.mThumbTransitionBitmapCache);
          Rect localRect = new Rect();
          this.mTmpTransform.rect.round(localRect);
          paramRunningTaskInfo.add(new AppTransitionAnimationSpec(paramTaskStackView.key.id, localBitmap, localRect));
        }
        i -= 1;
      }
      paramTaskStackView = new AppTransitionAnimationSpec[paramRunningTaskInfo.size()];
      paramRunningTaskInfo.toArray(paramTaskStackView);
      return ActivityOptions.makeThumbnailAspectScaleDownAnimation(this.mDummyStackView, paramTaskStackView, this.mHandler, null, this);
    }
    paramRunningTaskInfo = new Task();
    paramTaskStackView = getThumbnailTransitionTransform(paramTaskStackView, paramRunningTaskInfo, paramRect);
    paramRunningTaskInfo = drawThumbnailTransitionBitmap(paramRunningTaskInfo, paramTaskStackView, this.mThumbTransitionBitmapCache);
    if (paramRunningTaskInfo != null)
    {
      paramTaskStackView = paramTaskStackView.rect;
      return ActivityOptions.makeThumbnailAspectScaleDownAnimation(this.mDummyStackView, paramRunningTaskInfo, (int)paramTaskStackView.left, (int)paramTaskStackView.top, (int)paramTaskStackView.width(), (int)paramTaskStackView.height(), this.mHandler, null);
    }
    return getUnknownTransitionActivityOptions();
  }
  
  private TaskViewTransform getThumbnailTransitionTransform(TaskStackView paramTaskStackView, Task paramTask, Rect paramRect)
  {
    TaskStack localTaskStack = paramTaskStackView.getStack();
    Task localTask = localTaskStack.getLaunchTarget();
    if (localTask != null) {
      paramTask.copyFrom(localTask);
    }
    for (paramTask = localTask;; paramTask = localTask)
    {
      paramTaskStackView.updateLayoutAlgorithm(true);
      paramTaskStackView.updateToInitialState();
      paramTaskStackView.getStackAlgorithm().getStackTransformScreenCoordinates(paramTask, paramTaskStackView.getScroller().getStackScroll(), this.mTmpTransform, null, paramRect);
      return this.mTmpTransform;
      localTask = localTaskStack.getStackFrontMostTask(true);
      paramTask.copyFrom(localTask);
    }
  }
  
  private Rect getWindowRectOverride(int paramInt)
  {
    if (paramInt == -1) {
      return null;
    }
    Rect localRect1 = new Rect();
    Rect localRect2 = Recents.getSystemServices().getDisplayRect();
    DockedDividerUtils.calculateBoundsForPosition(paramInt, 4, localRect1, localRect2.width(), localRect2.height(), Recents.getSystemServices().getDockedDividerSize(this.mContext));
    return localRect1;
  }
  
  private void preloadIcon(int paramInt)
  {
    RecentsTaskLoadPlan.Options localOptions = new RecentsTaskLoadPlan.Options();
    localOptions.runningTaskId = paramInt;
    localOptions.loadThumbnails = false;
    localOptions.onlyLoadForCache = true;
    Recents.getTaskLoader().loadTasks(this.mContext, sInstanceLoadPlan, localOptions);
  }
  
  private void reloadResources()
  {
    Resources localResources = this.mContext.getResources();
    this.mStatusBarHeight = localResources.getDimensionPixelSize(17104921);
    this.mNavBarHeight = localResources.getDimensionPixelSize(17104922);
    this.mNavBarWidth = localResources.getDimensionPixelSize(17104924);
    this.mTaskBarHeight = TaskStackLayoutAlgorithm.getDimensionForDevice(this.mContext, 2131755610, 2131755610, 2131755610, 2131755611, 2131755610, 2131755611);
  }
  
  private void startRecentsActivity(ActivityOptions paramActivityOptions)
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.systemui", "com.android.systemui.recents.RecentsActivity");
    localIntent.setFlags(276840448);
    if (paramActivityOptions != null) {
      this.mContext.startActivityAsUser(localIntent, paramActivityOptions.toBundle(), UserHandle.CURRENT);
    }
    for (;;)
    {
      EventBus.getDefault().send(new RecentsActivityStartingEvent());
      return;
      this.mContext.startActivityAsUser(localIntent, UserHandle.CURRENT);
    }
  }
  
  private void updateHeaderBarLayout(TaskStack arg1, Rect paramRect)
  {
    Object localObject = Recents.getSystemServices();
    Rect localRect1 = ((SystemServicesProxy)localObject).getDisplayRect();
    Rect localRect2 = new Rect();
    ((SystemServicesProxy)localObject).getStableInsets(localRect2);
    int i;
    if (paramRect != null)
    {
      paramRect = new Rect(paramRect);
      if (((SystemServicesProxy)localObject).hasDockedTask())
      {
        paramRect.bottom -= localRect2.bottom;
        localRect2.bottom = 0;
      }
      calculateWindowStableInsets(localRect2, paramRect);
      paramRect.offsetTo(0, 0);
      localObject = this.mDummyStackView.getStackAlgorithm();
      ((TaskStackLayoutAlgorithm)localObject).setSystemInsets(localRect2);
      if (??? != null)
      {
        ((TaskStackLayoutAlgorithm)localObject).getTaskStackBounds(localRect1, paramRect, localRect2.top, localRect2.left, localRect2.right, this.mTaskStackBounds);
        ((TaskStackLayoutAlgorithm)localObject).reset();
        ((TaskStackLayoutAlgorithm)localObject).initialize(localRect1, paramRect, this.mTaskStackBounds, TaskStackLayoutAlgorithm.StackState.getStackStateForStack(???));
        this.mDummyStackView.setTasks(???, false);
        ??? = ((TaskStackLayoutAlgorithm)localObject).getUntransformedTaskViewBounds();
        if (!???.isEmpty()) {
          i = ???.width();
        }
      }
    }
    for (;;)
    {
      synchronized (this.mHeaderBarLock)
      {
        if ((this.mHeaderBar.getMeasuredWidth() != i) || (this.mHeaderBar.getMeasuredHeight() != this.mTaskBarHeight))
        {
          this.mHeaderBar.forceLayout();
          this.mHeaderBar.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(this.mTaskBarHeight, 1073741824));
        }
        this.mHeaderBar.layout(0, 0, i, this.mTaskBarHeight);
        if ((this.mThumbTransitionBitmapCache == null) || (this.mThumbTransitionBitmapCache.getWidth() != i))
        {
          this.mThumbTransitionBitmapCache = Bitmap.createBitmap(i, this.mTaskBarHeight, Bitmap.Config.ARGB_8888);
          return;
          paramRect = ((SystemServicesProxy)localObject).getWindowRect();
        }
      }
      if (this.mThumbTransitionBitmapCache.getHeight() == this.mTaskBarHeight) {}
    }
  }
  
  public void cancelPreloadingRecents() {}
  
  public void dockTopTask(int paramInt1, int paramInt2, int paramInt3, Rect paramRect)
  {
    if (Recents.getSystemServices().moveTaskToDockedStack(paramInt1, paramInt3, paramRect))
    {
      EventBus.getDefault().send(new DockedTopTaskEvent(paramInt2, paramRect));
      if (paramInt2 != 0) {
        break label48;
      }
    }
    label48:
    for (boolean bool = true;; bool = false)
    {
      showRecents(false, bool, false, true, false, -1);
      return;
    }
  }
  
  protected ActivityOptions getHomeTransitionActivityOptions()
  {
    return ActivityOptions.makeCustomAnimation(this.mContext, 2131034284, 2131034285, this.mHandler, null);
  }
  
  protected ActivityOptions getUnknownTransitionActivityOptions()
  {
    return ActivityOptions.makeCustomAnimation(this.mContext, 2131034286, 2131034287, this.mHandler, null);
  }
  
  public void hideRecents(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (this.mFastAltTabTrigger.isDozing()))
    {
      showNextTask();
      this.mFastAltTabTrigger.stopDozing();
      return;
    }
    EventBus.getDefault().post(new HideRecentsEvent(paramBoolean1, paramBoolean2));
  }
  
  public void onAnimationFinished()
  {
    EventBus.getDefault().post(new EnterRecentsWindowLastAnimationFrameEvent());
  }
  
  public void onBootCompleted()
  {
    RecentsTaskLoader localRecentsTaskLoader = Recents.getTaskLoader();
    RecentsTaskLoadPlan localRecentsTaskLoadPlan = localRecentsTaskLoader.createLoadPlan(this.mContext);
    localRecentsTaskLoader.preloadTasks(localRecentsTaskLoadPlan, -1, false);
    RecentsTaskLoadPlan.Options localOptions = new RecentsTaskLoadPlan.Options();
    localOptions.numVisibleTasks = localRecentsTaskLoader.getIconCacheSize();
    localOptions.numVisibleTaskThumbnails = localRecentsTaskLoader.getThumbnailCacheSize();
    localOptions.onlyLoadForCache = true;
    localRecentsTaskLoader.loadTasks(this.mContext, localRecentsTaskLoadPlan, localOptions);
  }
  
  public void onConfigurationChanged()
  {
    Resources localResources = this.mContext.getResources();
    reloadResources();
    this.mDummyStackView.reloadOnConfigurationChange();
    this.mHeaderBar.setLayoutDirection(localResources.getConfiguration().getLayoutDirection());
    this.mHeaderBar.onConfigurationChanged();
    this.mHeaderBar.forceLayout();
    this.mHeaderBar.measure(View.MeasureSpec.makeMeasureSpec(this.mHeaderBar.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(this.mHeaderBar.getMeasuredHeight(), 1073741824));
  }
  
  public void onDraggingInRecents(float paramFloat)
  {
    EventBus.getDefault().sendOntoMainThread(new DraggingInRecentsEvent(paramFloat));
  }
  
  public void onDraggingInRecentsEnded(float paramFloat)
  {
    EventBus.getDefault().sendOntoMainThread(new DraggingInRecentsEndedEvent(paramFloat));
  }
  
  public void onShowCurrentUserToast(int paramInt1, int paramInt2)
  {
    Toast.makeText(this.mContext, paramInt1, paramInt2).show();
  }
  
  public void onStartScreenPinning(Context paramContext, int paramInt)
  {
    paramContext = (PhoneStatusBar)((SystemUIApplication)paramContext).getComponent(PhoneStatusBar.class);
    if (paramContext != null) {
      paramContext.showScreenPinningRequest(paramInt, false);
    }
  }
  
  public void onVisibilityChanged(Context paramContext, boolean paramBoolean)
  {
    Recents.getSystemServices().setRecentsVisibility(paramBoolean);
  }
  
  public void preloadRecents()
  {
    boolean bool2 = false;
    Object localObject2 = Recents.getSystemServices();
    Object localObject1 = new MutableBoolean(true);
    RecentsTaskLoader localRecentsTaskLoader;
    RecentsTaskLoadPlan localRecentsTaskLoadPlan;
    int i;
    if (!((SystemServicesProxy)localObject2).isRecentsActivityVisible((MutableBoolean)localObject1))
    {
      localObject2 = ((SystemServicesProxy)localObject2).getRunningTask();
      localRecentsTaskLoader = Recents.getTaskLoader();
      sInstanceLoadPlan = localRecentsTaskLoader.createLoadPlan(this.mContext);
      localRecentsTaskLoadPlan = sInstanceLoadPlan;
      if (!((MutableBoolean)localObject1).value) {
        break label135;
      }
      bool1 = false;
      localRecentsTaskLoadPlan.preloadRawTasks(bool1);
      localRecentsTaskLoadPlan = sInstanceLoadPlan;
      i = ((ActivityManager.RunningTaskInfo)localObject2).id;
      if (!((MutableBoolean)localObject1).value) {
        break label140;
      }
    }
    label135:
    label140:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      localRecentsTaskLoader.preloadTasks(localRecentsTaskLoadPlan, i, bool1);
      localObject1 = sInstanceLoadPlan.getTaskStack();
      if (((TaskStack)localObject1).getTaskCount() > 0)
      {
        preloadIcon(((ActivityManager.RunningTaskInfo)localObject2).id);
        updateHeaderBarLayout((TaskStack)localObject1, null);
      }
      return;
      bool1 = true;
      break;
    }
  }
  
  public void showNextAffiliatedTask()
  {
    MetricsLogger.count(this.mContext, "overview_affiliated_task_next", 1);
    showRelativeAffiliatedTask(true);
  }
  
  public void showNextTask()
  {
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    Object localObject1 = Recents.getTaskLoader();
    Object localObject2 = ((RecentsTaskLoader)localObject1).createLoadPlan(this.mContext);
    ((RecentsTaskLoader)localObject1).preloadTasks((RecentsTaskLoadPlan)localObject2, -1, false);
    localObject1 = ((RecentsTaskLoadPlan)localObject2).getTaskStack();
    if ((localObject1 == null) || (((TaskStack)localObject1).getTaskCount() == 0)) {
      return;
    }
    ActivityManager.RunningTaskInfo localRunningTaskInfo = localSystemServicesProxy.getRunningTask();
    if (localRunningTaskInfo == null) {
      return;
    }
    boolean bool = SystemServicesProxy.isHomeStack(localRunningTaskInfo.stackId);
    ArrayList localArrayList = ((TaskStack)localObject1).getStackTasks();
    Object localObject3 = null;
    Object localObject4 = null;
    int i = localArrayList.size() - 1;
    for (;;)
    {
      localObject1 = localObject4;
      localObject2 = localObject3;
      if (i >= 1)
      {
        localObject1 = (Task)localArrayList.get(i);
        if (!bool) {
          break label161;
        }
        localObject2 = (Task)localArrayList.get(i - 1);
      }
      for (localObject1 = ActivityOptions.makeCustomAnimation(this.mContext, 2131034290, 2131034283);; localObject1 = ActivityOptions.makeCustomAnimation(this.mContext, 2131034293, 2131034292))
      {
        if (localObject2 != null) {
          break label213;
        }
        localSystemServicesProxy.startInPlaceAnimationOnFrontMostApplication(ActivityOptions.makeCustomInPlaceAnimation(this.mContext, 2131034291));
        return;
        label161:
        if (((Task)localObject1).key.id != localRunningTaskInfo.id) {
          break;
        }
        localObject2 = (Task)localArrayList.get(i - 1);
      }
      i -= 1;
    }
    label213:
    localSystemServicesProxy.startActivityFromRecents(this.mContext, ((Task)localObject2).key, ((Task)localObject2).title, (ActivityOptions)localObject1);
  }
  
  public void showPrevAffiliatedTask()
  {
    MetricsLogger.count(this.mContext, "overview_affiliated_task_prev", 1);
    showRelativeAffiliatedTask(false);
  }
  
  public void showRecents(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, int paramInt)
  {
    this.mTriggeredFromAltTab = paramBoolean1;
    this.mDraggingInRecents = paramBoolean2;
    this.mLaunchedWhileDocking = paramBoolean4;
    if (this.mFastAltTabTrigger.isAsleep()) {
      this.mFastAltTabTrigger.stopDozing();
    }
    for (;;)
    {
      try
      {
        Object localObject = Recents.getSystemServices();
        MutableBoolean localMutableBoolean;
        if (!paramBoolean4)
        {
          localMutableBoolean = new MutableBoolean(paramBoolean2);
          if (!paramBoolean2)
          {
            paramBoolean1 = ((SystemServicesProxy)localObject).isRecentsActivityVisible(localMutableBoolean);
            if (paramBoolean1)
            {
              return;
              if (this.mFastAltTabTrigger.isDozing())
              {
                if (!paramBoolean1) {
                  return;
                }
                this.mFastAltTabTrigger.stopDozing();
                continue;
              }
              if (!paramBoolean1) {
                continue;
              }
              this.mFastAltTabTrigger.startDozing();
            }
          }
        }
        else
        {
          paramBoolean2 = true;
          continue;
        }
        localObject = ((SystemServicesProxy)localObject).getRunningTask();
        if (!localMutableBoolean.value)
        {
          startRecentsActivity((ActivityManager.RunningTaskInfo)localObject, paramBoolean5, paramBoolean3, paramInt);
          return;
        }
      }
      catch (ActivityNotFoundException localActivityNotFoundException)
      {
        Log.e("RecentsImpl", "Failed to launch RecentsActivity", localActivityNotFoundException);
        return;
      }
      paramBoolean5 = true;
    }
  }
  
  public void showRelativeAffiliatedTask(boolean paramBoolean)
  {
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    Object localObject1 = Recents.getTaskLoader();
    Object localObject2 = ((RecentsTaskLoader)localObject1).createLoadPlan(this.mContext);
    ((RecentsTaskLoader)localObject1).preloadTasks((RecentsTaskLoadPlan)localObject2, -1, false);
    TaskStack localTaskStack = ((RecentsTaskLoadPlan)localObject2).getTaskStack();
    if ((localTaskStack == null) || (localTaskStack.getTaskCount() == 0)) {
      return;
    }
    ActivityManager.RunningTaskInfo localRunningTaskInfo = localSystemServicesProxy.getRunningTask();
    if (localRunningTaskInfo == null) {
      return;
    }
    if (SystemServicesProxy.isHomeStack(localRunningTaskInfo.stackId)) {
      return;
    }
    ArrayList localArrayList = localTaskStack.getStackTasks();
    TaskGrouping localTaskGrouping = null;
    Object localObject3 = null;
    Task.TaskKey localTaskKey = null;
    int m = localArrayList.size();
    int k = 0;
    int i = 0;
    for (;;)
    {
      localObject1 = localTaskKey;
      int j = k;
      localObject2 = localTaskGrouping;
      if (i < m)
      {
        localObject1 = (Task)localArrayList.get(i);
        if (((Task)localObject1).key.id != localRunningTaskInfo.id) {
          break label267;
        }
        localTaskGrouping = ((Task)localObject1).group;
        if (!paramBoolean) {
          break label240;
        }
        localTaskKey = localTaskGrouping.getNextTaskInGroup((Task)localObject1);
      }
      for (localObject1 = ActivityOptions.makeCustomAnimation(this.mContext, 2131034290, 2131034289);; localObject1 = ActivityOptions.makeCustomAnimation(this.mContext, 2131034293, 2131034292))
      {
        localObject2 = localObject3;
        if (localTaskKey != null) {
          localObject2 = localTaskStack.findTaskWithId(localTaskKey.id);
        }
        j = localTaskGrouping.getTaskCount();
        if (localObject2 != null) {
          break label290;
        }
        if (j > 1)
        {
          if (!paramBoolean) {
            break;
          }
          localSystemServicesProxy.startInPlaceAnimationOnFrontMostApplication(ActivityOptions.makeCustomInPlaceAnimation(this.mContext, 2131034288));
        }
        return;
        label240:
        localTaskKey = localTaskGrouping.getPrevTaskInGroup((Task)localObject1);
      }
      label267:
      i += 1;
    }
    localSystemServicesProxy.startInPlaceAnimationOnFrontMostApplication(ActivityOptions.makeCustomInPlaceAnimation(this.mContext, 2131034291));
    return;
    label290:
    MetricsLogger.count(this.mContext, "overview_affiliated_task_launch", 1);
    localSystemServicesProxy.startActivityFromRecents(this.mContext, ((Task)localObject2).key, ((Task)localObject2).title, (ActivityOptions)localObject1);
  }
  
  protected void startRecentsActivity(ActivityManager.RunningTaskInfo paramRunningTaskInfo, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    Object localObject1 = Recents.getTaskLoader();
    RecentsActivityLaunchState localRecentsActivityLaunchState = Recents.getConfiguration().getLaunchState();
    Object localObject2 = Recents.getSystemServices();
    boolean bool1;
    label48:
    int j;
    label51:
    int i;
    label209:
    int k;
    if (paramRunningTaskInfo != null)
    {
      bool1 = ((SystemServicesProxy)localObject2).isBlackListedActivity(paramRunningTaskInfo.baseActivity.getClassName());
      if ((!this.mLaunchedWhileDocking) && (!bool1)) {
        break label382;
      }
      j = -1;
      if ((this.mLaunchedWhileDocking) || (this.mTriggeredFromAltTab) || (sInstanceLoadPlan == null)) {
        sInstanceLoadPlan = ((RecentsTaskLoader)localObject1).createLoadPlan(this.mContext);
      }
      Log.d("RecentsImpl", "startRecentsActivity, " + paramBoolean2 + ", " + this.mLaunchedWhileDocking + ", " + this.mTriggeredFromAltTab + ", " + bool1 + ", " + sInstanceLoadPlan.hasTasks());
      ((SystemServicesProxy)localObject2).initWidgetPkgList();
      if ((this.mLaunchedWhileDocking) || (this.mTriggeredFromAltTab) || (!sInstanceLoadPlan.hasTasks())) {
        break label395;
      }
      localObject2 = sInstanceLoadPlan.getTaskStack();
      if (((TaskStack)localObject2).getTaskCount() <= 0) {
        break label427;
      }
      i = 1;
      if ((paramRunningTaskInfo != null) && (!paramBoolean1)) {
        break label433;
      }
      k = 0;
      label220:
      if ((k == 0) && (!this.mLaunchedWhileDocking)) {
        break label440;
      }
      paramBoolean1 = false;
      label234:
      localRecentsActivityLaunchState.launchedFromHome = paramBoolean1;
      if (k != 0) {
        break label445;
      }
      paramBoolean1 = this.mLaunchedWhileDocking;
      label250:
      localRecentsActivityLaunchState.launchedFromApp = paramBoolean1;
      if (!localRecentsActivityLaunchState.launchedFromApp) {
        break label450;
      }
    }
    label382:
    label395:
    label427:
    label433:
    label440:
    label445:
    label450:
    for (paramBoolean1 = bool1;; paramBoolean1 = false)
    {
      localRecentsActivityLaunchState.launchedFromBlacklistedApp = paramBoolean1;
      localRecentsActivityLaunchState.launchedViaDockGesture = this.mLaunchedWhileDocking;
      localRecentsActivityLaunchState.launchedViaDragGesture = this.mDraggingInRecents;
      localRecentsActivityLaunchState.launchedToTaskId = j;
      localRecentsActivityLaunchState.launchedWithAltTab = this.mTriggeredFromAltTab;
      preloadIcon(j);
      localObject1 = getWindowRectOverride(paramInt);
      updateHeaderBarLayout((TaskStack)localObject2, (Rect)localObject1);
      localObject2 = this.mDummyStackView.computeStackVisibilityReport();
      localRecentsActivityLaunchState.launchedNumVisibleTasks = ((TaskStackLayoutAlgorithm.VisibilityReport)localObject2).numVisibleTasks;
      localRecentsActivityLaunchState.launchedNumVisibleThumbnails = ((TaskStackLayoutAlgorithm.VisibilityReport)localObject2).numVisibleThumbnails;
      if (paramBoolean2) {
        break label455;
      }
      startRecentsActivity(ActivityOptions.makeCustomAnimation(this.mContext, -1, -1));
      return;
      bool1 = false;
      break;
      if (paramRunningTaskInfo == null) {
        break label48;
      }
      j = paramRunningTaskInfo.id;
      break label51;
      localObject2 = sInstanceLoadPlan;
      if (paramBoolean1) {}
      for (boolean bool2 = false;; bool2 = true)
      {
        ((RecentsTaskLoader)localObject1).preloadTasks((RecentsTaskLoadPlan)localObject2, j, bool2);
        break;
      }
      i = 0;
      break label209;
      k = i;
      break label220;
      paramBoolean1 = true;
      break label234;
      paramBoolean1 = true;
      break label250;
    }
    label455:
    if (bool1) {
      paramRunningTaskInfo = getUnknownTransitionActivityOptions();
    }
    for (;;)
    {
      startRecentsActivity(paramRunningTaskInfo);
      this.mLastToggleTime = SystemClock.elapsedRealtime();
      return;
      if (k != 0) {
        paramRunningTaskInfo = getThumbnailTransitionActivityOptions(paramRunningTaskInfo, this.mDummyStackView, (Rect)localObject1);
      } else if (i != 0) {
        paramRunningTaskInfo = getHomeTransitionActivityOptions();
      } else {
        paramRunningTaskInfo = getUnknownTransitionActivityOptions();
      }
    }
  }
  
  public void toggleRecents(int paramInt)
  {
    if (this.mFastAltTabTrigger.isDozing()) {
      return;
    }
    this.mDraggingInRecents = false;
    this.mLaunchedWhileDocking = false;
    this.mTriggeredFromAltTab = false;
    MutableBoolean localMutableBoolean;
    long l;
    try
    {
      Object localObject = Recents.getSystemServices();
      localMutableBoolean = new MutableBoolean(true);
      l = SystemClock.elapsedRealtime() - this.mLastToggleTime;
      if (!((SystemServicesProxy)localObject).isRecentsActivityVisible(localMutableBoolean)) {
        break label227;
      }
      if (((SystemServicesProxy)localObject).isDeepCleaning())
      {
        Log.d("RecentsImpl", "not handle toggle when cleaning");
        return;
      }
      localObject = Recents.getDebugFlags();
      if (!Recents.getConfiguration().getLaunchState().launchedWithAltTab)
      {
        if ((!((RecentsDebugFlags)localObject).isPagingEnabled()) || ((ViewConfiguration.getDoubleTapMinTime() < l) && (l < ViewConfiguration.getDoubleTapTimeout())))
        {
          EventBus.getDefault().post(new LaunchNextTaskRequestEvent());
          return;
        }
        EventBus.getDefault().post(new IterateRecentsEvent());
        return;
      }
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.e("RecentsImpl", "Failed to launch RecentsActivity", localActivityNotFoundException);
      return;
    }
    if (l < 350L) {
      return;
    }
    EventBus.getDefault().post(new ToggleRecentsEvent());
    this.mLastToggleTime = SystemClock.elapsedRealtime();
    return;
    label227:
    do
    {
      startRecentsActivity(localActivityNotFoundException.getRunningTask(), localMutableBoolean.value, true, paramInt);
      localActivityNotFoundException.sendCloseSystemWindows("recentapps");
      this.mLastToggleTime = SystemClock.elapsedRealtime();
      return;
    } while (l >= 350L);
  }
  
  class TaskStackListenerImpl
    extends SystemServicesProxy.TaskStackListener
  {
    TaskStackListenerImpl() {}
    
    public void onTaskStackChanged()
    {
      if (Recents.getConfiguration().svelteLevel == 0)
      {
        RecentsTaskLoader localRecentsTaskLoader = Recents.getTaskLoader();
        ActivityManager.RunningTaskInfo localRunningTaskInfo = Recents.getSystemServices().getRunningTask();
        RecentsTaskLoadPlan localRecentsTaskLoadPlan = localRecentsTaskLoader.createLoadPlan(RecentsImpl.this.mContext);
        if (Utils.DEBUG_ONEPLUS) {
          Log.d("RecentsImpl", "onTaskStackChanged");
        }
        localRecentsTaskLoader.preloadTasks(localRecentsTaskLoadPlan, -1, false);
        RecentsTaskLoadPlan.Options localOptions = new RecentsTaskLoadPlan.Options();
        if (localRunningTaskInfo != null) {
          localOptions.runningTaskId = localRunningTaskInfo.id;
        }
        localOptions.numVisibleTasks = 2;
        localOptions.numVisibleTaskThumbnails = 2;
        localOptions.onlyLoadForCache = true;
        localOptions.onlyLoadPausedActivities = true;
        localRecentsTaskLoader.loadTasks(RecentsImpl.this.mContext, localRecentsTaskLoadPlan, localOptions);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\RecentsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */