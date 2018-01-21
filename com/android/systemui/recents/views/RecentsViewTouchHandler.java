package com.android.systemui.recents.views;

import android.app.ActivityManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewParent;
import com.android.internal.policy.DividerSnapAlgorithm;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsConfiguration;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.ConfigurationChangedEvent;
import com.android.systemui.recents.events.ui.HideIncompatibleAppOverlayEvent;
import com.android.systemui.recents.events.ui.ShowIncompatibleAppOverlayEvent;
import com.android.systemui.recents.events.ui.dragndrop.DragDropTargetChangedEvent;
import com.android.systemui.recents.events.ui.dragndrop.DragEndEvent;
import com.android.systemui.recents.events.ui.dragndrop.DragStartEvent;
import com.android.systemui.recents.events.ui.dragndrop.DragStartInitializeDropTargetsEvent;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.TaskStack.DockState;
import java.util.ArrayList;
import java.util.Iterator;

public class RecentsViewTouchHandler
{
  private DividerSnapAlgorithm mDividerSnapAlgorithm;
  @ViewDebug.ExportedProperty(category="recents")
  private Point mDownPos = new Point();
  @ViewDebug.ExportedProperty(category="recents")
  private boolean mDragRequested;
  private float mDragSlop;
  @ViewDebug.ExportedProperty(deepExport=true, prefix="drag_task")
  private Task mDragTask;
  private ArrayList<DropTarget> mDropTargets = new ArrayList();
  @ViewDebug.ExportedProperty(category="recents")
  private boolean mIsDragging;
  private DropTarget mLastDropTarget;
  private RecentsView mRv;
  @ViewDebug.ExportedProperty(deepExport=true, prefix="drag_task_view_")
  private TaskView mTaskView;
  @ViewDebug.ExportedProperty(category="recents")
  private Point mTaskViewOffset = new Point();
  private ArrayList<TaskStack.DockState> mVisibleDockStates = new ArrayList();
  
  public RecentsViewTouchHandler(RecentsView paramRecentsView)
  {
    this.mRv = paramRecentsView;
    this.mDragSlop = ViewConfiguration.get(paramRecentsView.getContext()).getScaledTouchSlop();
    updateSnapAlgorithm();
  }
  
  private void handleTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getActionMasked();
    switch (i)
    {
    }
    Object localObject1;
    Object localObject2;
    label346:
    do
    {
      float f1;
      float f2;
      float f3;
      float f4;
      do
      {
        return;
        this.mDownPos.set((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
        return;
        f1 = paramMotionEvent.getX();
        f2 = paramMotionEvent.getY();
        f3 = this.mTaskViewOffset.x;
        f4 = this.mTaskViewOffset.y;
      } while (!this.mDragRequested);
      if (!this.mIsDragging) {
        if (Math.hypot(f1 - this.mDownPos.x, f2 - this.mDownPos.y) <= this.mDragSlop) {
          break label346;
        }
      }
      for (boolean bool = true;; bool = false)
      {
        this.mIsDragging = bool;
        if (this.mIsDragging)
        {
          i = this.mRv.getMeasuredWidth();
          int j = this.mRv.getMeasuredHeight();
          localObject1 = null;
          paramMotionEvent = (MotionEvent)localObject1;
          if (this.mLastDropTarget != null)
          {
            paramMotionEvent = (MotionEvent)localObject1;
            if (this.mLastDropTarget.acceptsDrop((int)f1, (int)f2, i, j, this.mRv.mSystemInsets, true)) {
              paramMotionEvent = this.mLastDropTarget;
            }
          }
          localObject1 = paramMotionEvent;
          if (paramMotionEvent == null)
          {
            localObject2 = this.mDropTargets.iterator();
            do
            {
              localObject1 = paramMotionEvent;
              if (!((Iterator)localObject2).hasNext()) {
                break;
              }
              localObject1 = (DropTarget)((Iterator)localObject2).next();
            } while (!((DropTarget)localObject1).acceptsDrop((int)f1, (int)f2, i, j, this.mRv.mSystemInsets, false));
          }
          if (this.mLastDropTarget != localObject1)
          {
            this.mLastDropTarget = ((DropTarget)localObject1);
            EventBus.getDefault().send(new DragDropTargetChangedEvent(this.mDragTask, (DropTarget)localObject1));
          }
        }
        this.mTaskView.setTranslationX(f1 - f3);
        this.mTaskView.setTranslationY(f2 - f4);
        return;
      }
    } while (!this.mDragRequested);
    TaskView localTaskView;
    if (i == 3)
    {
      i = 1;
      if (i != 0) {
        EventBus.getDefault().send(new DragDropTargetChangedEvent(this.mDragTask, null));
      }
      localObject1 = EventBus.getDefault();
      localObject2 = this.mDragTask;
      localTaskView = this.mTaskView;
      if (i != 0) {
        break label442;
      }
    }
    label442:
    for (paramMotionEvent = this.mLastDropTarget;; paramMotionEvent = null)
    {
      ((EventBus)localObject1).send(new DragEndEvent((Task)localObject2, localTaskView, paramMotionEvent));
      return;
      i = 0;
      break;
    }
  }
  
  private void updateSnapAlgorithm()
  {
    Rect localRect = new Rect();
    SystemServicesProxy.getInstance(this.mRv.getContext()).getStableInsets(localRect);
    this.mDividerSnapAlgorithm = DividerSnapAlgorithm.create(this.mRv.getContext(), localRect);
  }
  
  public TaskStack.DockState[] getDockStatesForCurrentOrientation()
  {
    int i;
    if (this.mRv.getResources().getConfiguration().orientation == 2) {
      i = 1;
    }
    while (Recents.getConfiguration().isLargeScreen) {
      if (i != 0)
      {
        return DockRegion.TABLET_LANDSCAPE;
        i = 0;
      }
      else
      {
        return DockRegion.TABLET_PORTRAIT;
      }
    }
    if (i != 0) {
      return DockRegion.PHONE_LANDSCAPE;
    }
    return DockRegion.PHONE_PORTRAIT;
  }
  
  public ArrayList<TaskStack.DockState> getVisibleDockStates()
  {
    return this.mVisibleDockStates;
  }
  
  public final void onBusEvent(ConfigurationChangedEvent paramConfigurationChangedEvent)
  {
    if ((paramConfigurationChangedEvent.fromDisplayDensityChange) || (paramConfigurationChangedEvent.fromDeviceOrientationChange)) {
      updateSnapAlgorithm();
    }
  }
  
  public final void onBusEvent(DragEndEvent paramDragEndEvent)
  {
    if (!this.mDragTask.isDockable) {
      EventBus.getDefault().send(new HideIncompatibleAppOverlayEvent());
    }
    this.mDragRequested = false;
    this.mDragTask = null;
    this.mTaskView = null;
    this.mLastDropTarget = null;
  }
  
  public final void onBusEvent(DragStartEvent paramDragStartEvent)
  {
    int i = 0;
    Object localObject = Recents.getSystemServices();
    this.mRv.getParent().requestDisallowInterceptTouchEvent(true);
    this.mDragRequested = true;
    this.mIsDragging = false;
    this.mDragTask = paramDragStartEvent.task;
    this.mTaskView = paramDragStartEvent.taskView;
    this.mDropTargets.clear();
    int[] arrayOfInt = new int[2];
    this.mRv.getLocationInWindow(arrayOfInt);
    this.mTaskViewOffset.set(this.mTaskView.getLeft() - arrayOfInt[0] + paramDragStartEvent.tlOffset.x, this.mTaskView.getTop() - arrayOfInt[1] + paramDragStartEvent.tlOffset.y);
    float f1 = this.mDownPos.x - this.mTaskViewOffset.x;
    float f2 = this.mDownPos.y - this.mTaskViewOffset.y;
    this.mTaskView.setTranslationX(f1);
    this.mTaskView.setTranslationY(f2);
    this.mVisibleDockStates.clear();
    if ((!ActivityManager.supportsMultiWindow()) || (((SystemServicesProxy)localObject).hasDockedTask())) {}
    for (;;)
    {
      EventBus.getDefault().send(new DragStartInitializeDropTargetsEvent(paramDragStartEvent.task, paramDragStartEvent.taskView, this));
      return;
      if (this.mDividerSnapAlgorithm.isSplitScreenFeasible())
      {
        Recents.logDockAttempt(this.mRv.getContext(), paramDragStartEvent.task.getTopComponent(), paramDragStartEvent.task.resizeMode);
        if (!paramDragStartEvent.task.isDockable)
        {
          EventBus.getDefault().send(new ShowIncompatibleAppOverlayEvent());
        }
        else
        {
          localObject = getDockStatesForCurrentOrientation();
          int j = localObject.length;
          while (i < j)
          {
            arrayOfInt = localObject[i];
            registerDropTargetForCurrentDrag(arrayOfInt);
            arrayOfInt.update(this.mRv.getContext());
            this.mVisibleDockStates.add(arrayOfInt);
            i += 1;
          }
        }
      }
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    handleTouchEvent(paramMotionEvent);
    return this.mDragRequested;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    handleTouchEvent(paramMotionEvent);
    return this.mDragRequested;
  }
  
  public void registerDropTargetForCurrentDrag(DropTarget paramDropTarget)
  {
    this.mDropTargets.add(paramDropTarget);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\RecentsViewTouchHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */