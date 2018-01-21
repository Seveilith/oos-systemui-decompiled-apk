package com.android.systemui.recents.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsActivityLaunchState;
import com.android.systemui.recents.RecentsConfiguration;
import com.android.systemui.recents.misc.ReferenceCountedTrigger;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.TaskGrouping;
import com.android.systemui.recents.model.TaskStack;
import java.util.ArrayList;
import java.util.List;

public class TaskStackAnimationHelper
{
  private static final Interpolator DISMISS_ALL_TRANSLATION_INTERPOLATOR;
  private static final Interpolator ENTER_FROM_HOME_ALPHA_INTERPOLATOR;
  private static final Interpolator ENTER_FROM_HOME_TRANSLATION_INTERPOLATOR = Interpolators.LINEAR_OUT_SLOW_IN;
  private static final Interpolator ENTER_WHILE_DOCKING_INTERPOLATOR = Interpolators.LINEAR_OUT_SLOW_IN;
  private static final Interpolator EXIT_TO_HOME_TRANSLATION_INTERPOLATOR;
  private static final Interpolator FOCUS_BEHIND_NEXT_TASK_INTERPOLATOR;
  private static final Interpolator FOCUS_IN_FRONT_NEXT_TASK_INTERPOLATOR;
  private static final Interpolator FOCUS_NEXT_TASK_INTERPOLATOR;
  private TaskStackView mStackView;
  private ArrayList<TaskViewTransform> mTmpCurrentTaskTransforms = new ArrayList();
  private ArrayList<TaskViewTransform> mTmpFinalTaskTransforms = new ArrayList();
  private TaskViewTransform mTmpTransform = new TaskViewTransform();
  
  static
  {
    ENTER_FROM_HOME_ALPHA_INTERPOLATOR = Interpolators.LINEAR;
    EXIT_TO_HOME_TRANSLATION_INTERPOLATOR = new PathInterpolator(0.4F, 0.0F, 0.6F, 1.0F);
    DISMISS_ALL_TRANSLATION_INTERPOLATOR = new PathInterpolator(0.4F, 0.0F, 1.0F, 1.0F);
    FOCUS_NEXT_TASK_INTERPOLATOR = new PathInterpolator(0.4F, 0.0F, 0.0F, 1.0F);
    FOCUS_IN_FRONT_NEXT_TASK_INTERPOLATOR = new PathInterpolator(0.0F, 0.0F, 0.0F, 1.0F);
    FOCUS_BEHIND_NEXT_TASK_INTERPOLATOR = Interpolators.LINEAR_OUT_SLOW_IN;
  }
  
  public TaskStackAnimationHelper(Context paramContext, TaskStackView paramTaskStackView)
  {
    this.mStackView = paramTaskStackView;
  }
  
  private int calculateStaggeredAnimDuration(int paramInt)
  {
    return Math.max(100, (paramInt - 1) * 50 + 100);
  }
  
  public void prepareForEnterAnimation()
  {
    RecentsActivityLaunchState localRecentsActivityLaunchState = Recents.getConfiguration().getLaunchState();
    Object localObject1 = this.mStackView.getResources();
    Object localObject2 = this.mStackView.getContext().getApplicationContext().getResources();
    TaskStackLayoutAlgorithm localTaskStackLayoutAlgorithm = this.mStackView.getStackAlgorithm();
    TaskStackViewScroller localTaskStackViewScroller = this.mStackView.getScroller();
    Object localObject3 = this.mStackView.getStack();
    Task localTask = ((TaskStack)localObject3).getLaunchTarget();
    if (((TaskStack)localObject3).getTaskCount() == 0) {
      return;
    }
    int n = localTaskStackLayoutAlgorithm.mStackRect.height();
    int i1 = ((Resources)localObject1).getDimensionPixelSize(2131755622);
    int m = ((Resources)localObject1).getDimensionPixelSize(2131755623);
    int i;
    int j;
    label136:
    boolean bool1;
    label187:
    boolean bool2;
    if (((Resources)localObject2).getConfiguration().orientation == 2)
    {
      i = 1;
      localObject1 = this.mStackView.getTaskViews();
      j = ((List)localObject1).size() - 1;
      if (j < 0) {
        return;
      }
      localObject2 = (TaskView)((List)localObject1).get(j);
      localObject3 = ((TaskView)localObject2).getTask();
      if ((localTask == null) || (localTask.group == null)) {
        break label248;
      }
      bool1 = localTask.group.isTaskAboveTask((Task)localObject3, localTask);
      if ((localTask == null) || (!localTask.isFreeformTask())) {
        break label254;
      }
      bool2 = ((Task)localObject3).isFreeformTask();
      label207:
      localTaskStackLayoutAlgorithm.getStackTransform((Task)localObject3, localTaskStackViewScroller.getStackScroll(), this.mTmpTransform, null);
      if (!bool2) {
        break label260;
      }
      ((TaskView)localObject2).setVisibility(4);
    }
    label248:
    label254:
    label260:
    label393:
    do
    {
      for (;;)
      {
        j -= 1;
        break label136;
        i = 0;
        break;
        bool1 = false;
        break label187;
        bool2 = false;
        break label207;
        if ((!localRecentsActivityLaunchState.launchedFromApp) || (localRecentsActivityLaunchState.launchedViaDockGesture))
        {
          if (!localRecentsActivityLaunchState.launchedFromHome) {
            break label393;
          }
          this.mTmpTransform.rect.offset(0.0F, n);
          this.mTmpTransform.alpha = 0.0F;
          this.mStackView.updateTaskViewToTransform((TaskView)localObject2, this.mTmpTransform, AnimationProps.IMMEDIATE);
        }
        else if (((Task)localObject3).isLaunchTarget)
        {
          ((TaskView)localObject2).onPrepareLaunchTargetForEnterAnimation();
        }
        else if (bool1)
        {
          this.mTmpTransform.rect.offset(0.0F, i1);
          this.mTmpTransform.alpha = 0.0F;
          this.mStackView.updateTaskViewToTransform((TaskView)localObject2, this.mTmpTransform, AnimationProps.IMMEDIATE);
          ((TaskView)localObject2).setClipViewInStack(false);
        }
      }
    } while (!localRecentsActivityLaunchState.launchedViaDockGesture);
    if (i != 0) {}
    for (int k = m;; k = (int)(n * 0.9F))
    {
      this.mTmpTransform.rect.offset(0.0F, k);
      this.mTmpTransform.alpha = 0.0F;
      this.mStackView.updateTaskViewToTransform((TaskView)localObject2, this.mTmpTransform, AnimationProps.IMMEDIATE);
      break;
    }
  }
  
  public void startDeleteAllTasksAnimation(final List<TaskView> paramList, final ReferenceCountedTrigger paramReferenceCountedTrigger)
  {
    Object localObject1 = this.mStackView.getStackAlgorithm();
    int j = this.mStackView.getMeasuredWidth();
    int k = ((TaskStackLayoutAlgorithm)localObject1).mTaskRect.left;
    localObject1 = new ArrayList();
    int i = 0;
    Object localObject2;
    while (i < paramList.size())
    {
      localObject2 = (TaskView)paramList.get(i);
      if (!((TaskView)localObject2).isLocked()) {
        ((List)localObject1).add(localObject2);
      }
      i += 1;
    }
    int m = ((List)localObject1).size();
    i = m - 1;
    while (i >= 0)
    {
      paramList = (TaskView)((List)localObject1).get(i);
      paramList.setClipViewInStack(false);
      localObject2 = new AnimationProps((m - i - 1) * 33, 200, DISMISS_ALL_TRANSLATION_INTERPOLATOR, new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          paramReferenceCountedTrigger.decrement();
          paramList.setClipViewInStack(true);
        }
      });
      paramReferenceCountedTrigger.increment();
      this.mTmpTransform.fillIn(paramList);
      this.mTmpTransform.rect.offset(j - k, 0.0F);
      this.mStackView.updateTaskViewToTransform(paramList, this.mTmpTransform, (AnimationProps)localObject2);
      i -= 1;
    }
  }
  
  public void startDeleteTaskAnimation(TaskView paramTaskView, final ReferenceCountedTrigger paramReferenceCountedTrigger)
  {
    TaskStackViewTouchHandler localTaskStackViewTouchHandler = this.mStackView.getTouchHandler();
    localTaskStackViewTouchHandler.onBeginManualDrag(paramTaskView);
    paramReferenceCountedTrigger.increment();
    paramReferenceCountedTrigger.addLastDecrementRunnable(new -void_startDeleteTaskAnimation_com_android_systemui_recents_views_TaskView_deleteTaskView_com_android_systemui_recents_misc_ReferenceCountedTrigger_postAnimationTrigger_LambdaImpl0(localTaskStackViewTouchHandler, paramTaskView));
    float f = localTaskStackViewTouchHandler.getScaledDismissSize();
    ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
    localValueAnimator.setDuration(400L);
    localValueAnimator.addUpdateListener(new -void_startDeleteTaskAnimation_com_android_systemui_recents_views_TaskView_deleteTaskView_com_android_systemui_recents_misc_ReferenceCountedTrigger_postAnimationTrigger_LambdaImpl1(paramTaskView, f, localTaskStackViewTouchHandler));
    localValueAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        paramReferenceCountedTrigger.decrement();
      }
    });
    localValueAnimator.start();
  }
  
  public void startEnterAnimation(final ReferenceCountedTrigger paramReferenceCountedTrigger)
  {
    RecentsActivityLaunchState localRecentsActivityLaunchState = Recents.getConfiguration().getLaunchState();
    Object localObject1 = this.mStackView.getResources();
    final Object localObject2 = this.mStackView.getContext().getApplicationContext().getResources();
    TaskStackLayoutAlgorithm localTaskStackLayoutAlgorithm = this.mStackView.getStackAlgorithm();
    TaskStackViewScroller localTaskStackViewScroller = this.mStackView.getScroller();
    Object localObject3 = this.mStackView.getStack();
    Task localTask = ((TaskStack)localObject3).getLaunchTarget();
    if (((TaskStack)localObject3).getTaskCount() == 0) {
      return;
    }
    int j = ((Resources)localObject1).getInteger(2131623992);
    int k = ((Resources)localObject1).getInteger(2131623993);
    int m = ((Resources)localObject2).getInteger(2131624001);
    localObject1 = this.mStackView.getTaskViews();
    int n = ((List)localObject1).size();
    int i = n - 1;
    if (i >= 0)
    {
      localObject2 = (TaskView)((List)localObject1).get(i);
      localObject3 = ((TaskView)localObject2).getTask();
      boolean bool;
      if ((localTask != null) && (localTask.group != null))
      {
        bool = localTask.group.isTaskAboveTask((Task)localObject3, localTask);
        label178:
        localTaskStackLayoutAlgorithm.getStackTransform((Task)localObject3, localTaskStackViewScroller.getStackScroll(), this.mTmpTransform, null);
        if ((localRecentsActivityLaunchState.launchedFromApp) && (!localRecentsActivityLaunchState.launchedViaDockGesture)) {
          break label339;
        }
        if (!localRecentsActivityLaunchState.launchedFromHome) {
          break label420;
        }
        localObject3 = new AnimationProps().setInitialPlayTime(6, Math.min(5, n - i - 1) * 33).setStartDelay(4, 0).setDuration(6, 200).setDuration(4, 0).setInterpolator(6, ENTER_FROM_HOME_TRANSLATION_INTERPOLATOR).setInterpolator(4, ENTER_FROM_HOME_ALPHA_INTERPOLATOR).setListener(paramReferenceCountedTrigger.decrementOnAnimationEnd());
        paramReferenceCountedTrigger.increment();
        this.mStackView.updateTaskViewToTransform((TaskView)localObject2, this.mTmpTransform, (AnimationProps)localObject3);
        if (i == n - 1) {
          ((TaskView)localObject2).onStartFrontTaskEnterAnimation(this.mStackView.mScreenPinningEnabled);
        }
      }
      for (;;)
      {
        i -= 1;
        break;
        bool = false;
        break label178;
        label339:
        if (((Task)localObject3).isLaunchTarget)
        {
          ((TaskView)localObject2).onStartLaunchTargetEnterAnimation(this.mTmpTransform, j, this.mStackView.mScreenPinningEnabled, paramReferenceCountedTrigger);
        }
        else if (bool)
        {
          localObject3 = new AnimationProps(k, Interpolators.ALPHA_IN, new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              paramReferenceCountedTrigger.decrement();
              localObject2.setClipViewInStack(true);
            }
          });
          paramReferenceCountedTrigger.increment();
          this.mStackView.updateTaskViewToTransform((TaskView)localObject2, this.mTmpTransform, (AnimationProps)localObject3);
          continue;
          label420:
          if (localRecentsActivityLaunchState.launchedViaDockGesture)
          {
            localObject3 = new AnimationProps().setDuration(6, i * 33 + m).setInterpolator(6, ENTER_WHILE_DOCKING_INTERPOLATOR).setStartDelay(6, 48).setListener(paramReferenceCountedTrigger.decrementOnAnimationEnd());
            paramReferenceCountedTrigger.increment();
            this.mStackView.updateTaskViewToTransform((TaskView)localObject2, this.mTmpTransform, (AnimationProps)localObject3);
          }
        }
      }
    }
  }
  
  public void startExitToHomeAnimation(boolean paramBoolean, ReferenceCountedTrigger paramReferenceCountedTrigger)
  {
    Object localObject = this.mStackView.getStackAlgorithm();
    if (this.mStackView.getStack().getTaskCount() == 0) {
      return;
    }
    int j = ((TaskStackLayoutAlgorithm)localObject).mStackRect.height();
    List localList = this.mStackView.getTaskViews();
    int k = localList.size();
    int i = 0;
    while (i < k)
    {
      TaskView localTaskView = (TaskView)localList.get(i);
      localObject = localTaskView.getTask();
      if (this.mStackView.isIgnoredTask((Task)localObject))
      {
        i += 1;
      }
      else
      {
        if (paramBoolean)
        {
          Math.min(5, k - i - 1);
          localObject = new AnimationProps().setStartDelay(6, 0).setDuration(6, 200).setInterpolator(6, EXIT_TO_HOME_TRANSLATION_INTERPOLATOR).setListener(paramReferenceCountedTrigger.decrementOnAnimationEnd());
          paramReferenceCountedTrigger.increment();
        }
        for (;;)
        {
          this.mTmpTransform.fillIn(localTaskView);
          this.mTmpTransform.rect.offset(0.0F, j);
          this.mStackView.updateTaskViewToTransform(localTaskView, this.mTmpTransform, (AnimationProps)localObject);
          break;
          localObject = AnimationProps.IMMEDIATE;
        }
      }
    }
  }
  
  public void startLaunchTaskAnimation(TaskView paramTaskView, boolean paramBoolean, ReferenceCountedTrigger paramReferenceCountedTrigger)
  {
    Object localObject1 = this.mStackView.getResources();
    int j = ((Resources)localObject1).getInteger(2131623994);
    int k = ((Resources)localObject1).getDimensionPixelSize(2131755622);
    localObject1 = paramTaskView.getTask();
    List localList = this.mStackView.getTaskViews();
    int m = localList.size();
    int i = 0;
    if (i < m)
    {
      final TaskView localTaskView = (TaskView)localList.get(i);
      Object localObject2 = localTaskView.getTask();
      boolean bool;
      if ((localObject1 != null) && (((Task)localObject1).group != null))
      {
        bool = ((Task)localObject1).group.isTaskAboveTask((Task)localObject2, (Task)localObject1);
        label110:
        if (localTaskView != paramTaskView) {
          break label160;
        }
        localTaskView.setClipViewInStack(false);
        paramReferenceCountedTrigger.addLastDecrementRunnable(new Runnable()
        {
          public void run()
          {
            localTaskView.setClipViewInStack(true);
          }
        });
        localTaskView.onStartLaunchTargetLaunchAnimation(j, paramBoolean, paramReferenceCountedTrigger);
      }
      for (;;)
      {
        i += 1;
        break;
        bool = false;
        break label110;
        label160:
        if (bool)
        {
          localObject2 = new AnimationProps(j, Interpolators.ALPHA_OUT, paramReferenceCountedTrigger.decrementOnAnimationEnd());
          paramReferenceCountedTrigger.increment();
          this.mTmpTransform.fillIn(localTaskView);
          this.mTmpTransform.alpha = 0.0F;
          this.mTmpTransform.rect.offset(0.0F, k);
          this.mStackView.updateTaskViewToTransform(localTaskView, this.mTmpTransform, (AnimationProps)localObject2);
        }
      }
    }
  }
  
  public void startNewStackScrollAnimation(TaskStack paramTaskStack, ReferenceCountedTrigger paramReferenceCountedTrigger)
  {
    Object localObject2 = this.mStackView.getStackAlgorithm();
    final Object localObject1 = this.mStackView.getScroller();
    ArrayList localArrayList = paramTaskStack.getStackTasks();
    this.mStackView.getCurrentTaskTransforms(localArrayList, this.mTmpCurrentTaskTransforms);
    this.mStackView.setTasks(paramTaskStack, false);
    this.mStackView.updateLayoutAlgorithm(false);
    final float f = ((TaskStackLayoutAlgorithm)localObject2).mInitialScrollP;
    this.mStackView.bindVisibleTaskViews(f);
    ((TaskStackLayoutAlgorithm)localObject2).setFocusState(0);
    ((TaskStackLayoutAlgorithm)localObject2).setTaskOverridesForInitialState(paramTaskStack, true);
    ((TaskStackViewScroller)localObject1).setStackScroll(f);
    this.mStackView.cancelDeferredTaskViewLayoutAnimation();
    this.mStackView.getLayoutTaskTransforms(f, ((TaskStackLayoutAlgorithm)localObject2).getFocusState(), localArrayList, false, this.mTmpFinalTaskTransforms);
    paramTaskStack = paramTaskStack.getStackFrontMostTask(false);
    localObject1 = this.mStackView.getChildViewForTask(paramTaskStack);
    final Object localObject3 = (TaskViewTransform)this.mTmpFinalTaskTransforms.get(localArrayList.indexOf(paramTaskStack));
    if (localObject1 != null) {
      this.mStackView.updateTaskViewToTransform((TaskView)localObject1, ((TaskStackLayoutAlgorithm)localObject2).getFrontOfStackTransform(), AnimationProps.IMMEDIATE);
    }
    paramReferenceCountedTrigger.addLastDecrementRunnable(new Runnable()
    {
      public void run()
      {
        TaskStackAnimationHelper.-get1(TaskStackAnimationHelper.this).bindVisibleTaskViews(f);
        if (localObject1 != null) {
          TaskStackAnimationHelper.-get1(TaskStackAnimationHelper.this).updateTaskViewToTransform(localObject1, localObject3, new AnimationProps(75, 250, TaskStackAnimationHelper.-get0()));
        }
      }
    });
    localObject2 = this.mStackView.getTaskViews();
    int j = ((List)localObject2).size();
    int i = 0;
    if (i < j)
    {
      localObject3 = (TaskView)((List)localObject2).get(i);
      Object localObject4 = ((TaskView)localObject3).getTask();
      if (this.mStackView.isIgnoredTask((Task)localObject4)) {}
      for (;;)
      {
        i += 1;
        break;
        if ((localObject4 != paramTaskStack) || (localObject1 == null))
        {
          int k = localArrayList.indexOf(localObject4);
          Object localObject5 = (TaskViewTransform)this.mTmpCurrentTaskTransforms.get(k);
          localObject4 = (TaskViewTransform)this.mTmpFinalTaskTransforms.get(k);
          this.mStackView.updateTaskViewToTransform((TaskView)localObject3, (TaskViewTransform)localObject5, AnimationProps.IMMEDIATE);
          k = calculateStaggeredAnimDuration(i);
          localObject5 = FOCUS_BEHIND_NEXT_TASK_INTERPOLATOR;
          localObject5 = new AnimationProps().setDuration(6, k).setInterpolator(6, (Interpolator)localObject5).setListener(paramReferenceCountedTrigger.decrementOnAnimationEnd());
          paramReferenceCountedTrigger.increment();
          this.mStackView.updateTaskViewToTransform((TaskView)localObject3, (TaskViewTransform)localObject4, (AnimationProps)localObject5);
        }
      }
    }
  }
  
  public boolean startScrollToFocusedTaskAnimation(Task paramTask, boolean paramBoolean)
  {
    Object localObject2 = this.mStackView.getStackAlgorithm();
    Object localObject3 = this.mStackView.getScroller();
    Object localObject1 = this.mStackView.getStack();
    float f1 = ((TaskStackViewScroller)localObject3).getStackScroll();
    final float f2 = ((TaskStackViewScroller)localObject3).getBoundedStackScroll(((TaskStackLayoutAlgorithm)localObject2).getStackScrollForTask(paramTask));
    int j;
    if (f2 > f1)
    {
      j = 1;
      if (Float.compare(f2, f1) == 0) {
        break label244;
      }
    }
    int i;
    label244:
    for (boolean bool = true;; bool = false)
    {
      i = this.mStackView.getTaskViews().size();
      localObject1 = ((TaskStack)localObject1).getStackTasks();
      this.mStackView.getCurrentTaskTransforms((ArrayList)localObject1, this.mTmpCurrentTaskTransforms);
      this.mStackView.bindVisibleTaskViews(f2);
      ((TaskStackLayoutAlgorithm)localObject2).setFocusState(1);
      ((TaskStackViewScroller)localObject3).setStackScroll(f2, null);
      this.mStackView.cancelDeferredTaskViewLayoutAnimation();
      this.mStackView.getLayoutTaskTransforms(f2, ((TaskStackLayoutAlgorithm)localObject2).getFocusState(), (ArrayList)localObject1, true, this.mTmpFinalTaskTransforms);
      paramTask = this.mStackView.getChildViewForTask(paramTask);
      if (paramTask != null) {
        break label250;
      }
      Log.e("TaskStackAnimationHelper", "b/27389156 null-task-view prebind:" + i + " postbind:" + this.mStackView.getTaskViews().size() + " prescroll:" + f1 + " postscroll: " + f2);
      return false;
      j = 0;
      break;
    }
    label250:
    paramTask.setFocusedState(true, paramBoolean);
    localObject2 = new ReferenceCountedTrigger();
    ((ReferenceCountedTrigger)localObject2).addLastDecrementRunnable(new Runnable()
    {
      public void run()
      {
        TaskStackAnimationHelper.-get1(TaskStackAnimationHelper.this).bindVisibleTaskViews(f2);
      }
    });
    localObject3 = this.mStackView.getTaskViews();
    int m = ((List)localObject3).size();
    int n = ((List)localObject3).indexOf(paramTask);
    int k = 0;
    while (k < m)
    {
      TaskView localTaskView = (TaskView)((List)localObject3).get(k);
      paramTask = localTaskView.getTask();
      if (this.mStackView.isIgnoredTask(paramTask))
      {
        k += 1;
      }
      else
      {
        i = ((ArrayList)localObject1).indexOf(paramTask);
        paramTask = (TaskViewTransform)this.mTmpCurrentTaskTransforms.get(i);
        TaskViewTransform localTaskViewTransform = (TaskViewTransform)this.mTmpFinalTaskTransforms.get(i);
        this.mStackView.updateTaskViewToTransform(localTaskView, paramTask, AnimationProps.IMMEDIATE);
        if (j != 0)
        {
          i = calculateStaggeredAnimDuration(k);
          paramTask = FOCUS_BEHIND_NEXT_TASK_INTERPOLATOR;
        }
        for (;;)
        {
          paramTask = new AnimationProps().setDuration(6, i).setInterpolator(6, paramTask).setListener(((ReferenceCountedTrigger)localObject2).decrementOnAnimationEnd());
          ((ReferenceCountedTrigger)localObject2).increment();
          this.mStackView.updateTaskViewToTransform(localTaskView, localTaskViewTransform, paramTask);
          break;
          if (k < n)
          {
            i = (n - k - 1) * 50 + 150;
            paramTask = FOCUS_BEHIND_NEXT_TASK_INTERPOLATOR;
          }
          else if (k > n)
          {
            i = Math.max(100, 150 - (k - n - 1) * 50);
            paramTask = FOCUS_IN_FRONT_NEXT_TASK_INTERPOLATOR;
          }
          else
          {
            i = 200;
            paramTask = FOCUS_NEXT_TASK_INTERPOLATOR;
          }
        }
      }
    }
    return bool;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\TaskStackAnimationHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */