package com.android.systemui.recents.model;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IntProperty;
import android.util.SparseArray;
import android.view.animation.Interpolator;
import com.android.internal.policy.DockedDividerUtils;
import com.android.systemui.Interpolators;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.misc.Utilities;
import com.android.systemui.recents.views.AnimationProps;
import com.android.systemui.recents.views.DropTarget;
import com.android.systemui.recents.views.TaskStackLayoutAlgorithm;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TaskStack
{
  private Comparator<Task> FREEFORM_COMPARATOR = new Comparator()
  {
    public int compare(Task paramAnonymousTask1, Task paramAnonymousTask2)
    {
      if ((!paramAnonymousTask1.isFreeformTask()) || (paramAnonymousTask2.isFreeformTask()))
      {
        if ((!paramAnonymousTask2.isFreeformTask()) || (paramAnonymousTask1.isFreeformTask())) {
          return Long.compare(paramAnonymousTask1.temporarySortIndexInStack, paramAnonymousTask2.temporarySortIndexInStack);
        }
      }
      else {
        return 1;
      }
      return -1;
    }
  };
  ArrayMap<Integer, TaskGrouping> mAffinitiesGroups = new ArrayMap();
  TaskStackCallbacks mCb;
  ArrayList<TaskGrouping> mGroups = new ArrayList();
  ArrayList<Task> mRawTaskList = new ArrayList();
  FilteredTaskList mStackTaskList = new FilteredTaskList();
  
  public TaskStack()
  {
    this.mStackTaskList.setFilter(new TaskFilter()
    {
      public boolean acceptTask(SparseArray<Task> paramAnonymousSparseArray, Task paramAnonymousTask, int paramAnonymousInt)
      {
        return paramAnonymousTask.isStackTask;
      }
    });
  }
  
  private ArrayMap<Task.TaskKey, Task> createTaskKeyMapFromList(List<Task> paramList)
  {
    ArrayMap localArrayMap = new ArrayMap(paramList.size());
    int j = paramList.size();
    int i = 0;
    while (i < j)
    {
      Task localTask = (Task)paramList.get(i);
      localArrayMap.put(localTask.key, localTask);
      i += 1;
    }
    return localArrayMap;
  }
  
  public void addGroup(TaskGrouping paramTaskGrouping)
  {
    this.mGroups.add(paramTaskGrouping);
    this.mAffinitiesGroups.put(Integer.valueOf(paramTaskGrouping.affiliation), paramTaskGrouping);
  }
  
  public ArrayList<Task> computeAllTasksList()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(this.mStackTaskList.getTasks());
    return localArrayList;
  }
  
  public ArraySet<ComponentName> computeComponentsRemoved(String paramString, int paramInt)
  {
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    ArraySet localArraySet1 = new ArraySet();
    ArraySet localArraySet2 = new ArraySet();
    ArrayList localArrayList = getTaskKeys();
    int j = localArrayList.size();
    int i = 0;
    if (i < j)
    {
      Object localObject = (Task.TaskKey)localArrayList.get(i);
      if (((Task.TaskKey)localObject).userId != paramInt) {}
      for (;;)
      {
        i += 1;
        break;
        localObject = ((Task.TaskKey)localObject).getComponent();
        if ((((ComponentName)localObject).getPackageName().equals(paramString)) && (!localArraySet1.contains(localObject))) {
          if (localSystemServicesProxy.getActivityInfo((ComponentName)localObject, paramInt) != null) {
            localArraySet1.add(localObject);
          } else {
            localArraySet2.add(localObject);
          }
        }
      }
    }
    return localArraySet2;
  }
  
  void createAffiliatedGroupings(Context paramContext)
  {
    this.mGroups.clear();
    this.mAffinitiesGroups.clear();
    ArrayMap localArrayMap = new ArrayMap();
    ArrayList localArrayList = this.mStackTaskList.getTasks();
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      Task localTask = (Task)localArrayList.get(i);
      TaskGrouping localTaskGrouping = new TaskGrouping(localTask.key.id);
      addGroup(localTaskGrouping);
      localTaskGrouping.addTask(localTask);
      localArrayMap.put(localTask.key, localTask);
      i += 1;
    }
    float f2 = paramContext.getResources().getFloat(2131755621);
    int k = this.mGroups.size();
    i = 0;
    if (i < k)
    {
      paramContext = (TaskGrouping)this.mGroups.get(i);
      int m = paramContext.getTaskCount();
      if (m <= 1) {}
      for (;;)
      {
        i += 1;
        break;
        int n = ((Task)localArrayMap.get(paramContext.mTaskKeys.get(0))).affiliationColor;
        float f3 = (1.0F - f2) / m;
        float f1 = 1.0F;
        j = 0;
        while (j < m)
        {
          ((Task)localArrayMap.get(paramContext.mTaskKeys.get(j))).colorPrimary = Utilities.getColorWithOverlay(n, -1, f1);
          f1 -= f3;
          j += 1;
        }
      }
    }
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    String str = paramString + "  ";
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("TaskStack");
    paramPrintWriter.print(" numStackTasks=");
    paramPrintWriter.print(this.mStackTaskList.size());
    paramPrintWriter.println();
    paramString = this.mStackTaskList.getTasks();
    int j = paramString.size();
    int i = 0;
    while (i < j)
    {
      ((Task)paramString.get(i)).dump(str, paramPrintWriter);
      i += 1;
    }
  }
  
  public Task findTaskWithId(int paramInt)
  {
    ArrayList localArrayList = computeAllTasksList();
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      Task localTask = (Task)localArrayList.get(i);
      if (localTask.key.id == paramInt) {
        return localTask;
      }
      i += 1;
    }
    return null;
  }
  
  public int getFreeformTaskCount()
  {
    ArrayList localArrayList = this.mStackTaskList.getTasks();
    int j = 0;
    int m = localArrayList.size();
    int i = 0;
    while (i < m)
    {
      int k = j;
      if (((Task)localArrayList.get(i)).isFreeformTask()) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  public ArrayList<Task> getFreeformTasks()
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = this.mStackTaskList.getTasks();
    int j = localArrayList2.size();
    int i = 0;
    while (i < j)
    {
      Task localTask = (Task)localArrayList2.get(i);
      if (localTask.isFreeformTask()) {
        localArrayList1.add(localTask);
      }
      i += 1;
    }
    return localArrayList1;
  }
  
  public Task getLaunchTarget()
  {
    ArrayList localArrayList = this.mStackTaskList.getTasks();
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      Task localTask = (Task)localArrayList.get(i);
      if (localTask.isLaunchTarget) {
        return localTask;
      }
      i += 1;
    }
    return null;
  }
  
  public Task getStackFrontMostTask(boolean paramBoolean)
  {
    ArrayList localArrayList = this.mStackTaskList.getTasks();
    if (localArrayList.isEmpty()) {
      return null;
    }
    int i = localArrayList.size() - 1;
    while (i >= 0)
    {
      Task localTask = (Task)localArrayList.get(i);
      if ((!localTask.isFreeformTask()) || (paramBoolean)) {
        return localTask;
      }
      i -= 1;
    }
    return null;
  }
  
  public int getStackTaskCount()
  {
    ArrayList localArrayList = this.mStackTaskList.getTasks();
    int j = 0;
    int m = localArrayList.size();
    int i = 0;
    while (i < m)
    {
      int k = j;
      if (!((Task)localArrayList.get(i)).isFreeformTask()) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  public ArrayList<Task> getStackTasks()
  {
    return this.mStackTaskList.getTasks();
  }
  
  public int getTaskCount()
  {
    return this.mStackTaskList.size();
  }
  
  public ArrayList<Task.TaskKey> getTaskKeys()
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = computeAllTasksList();
    int j = localArrayList2.size();
    int i = 0;
    while (i < j)
    {
      localArrayList1.add(((Task)localArrayList2.get(i)).key);
      i += 1;
    }
    return localArrayList1;
  }
  
  public int indexOfStackTask(Task paramTask)
  {
    return this.mStackTaskList.indexOf(paramTask);
  }
  
  public void moveTaskToStack(Task paramTask, int paramInt)
  {
    ArrayList localArrayList = this.mStackTaskList.getTasks();
    int i = localArrayList.size();
    if ((!paramTask.isFreeformTask()) && (paramInt == 2)) {
      this.mStackTaskList.moveTaskToStack(paramTask, i, paramInt);
    }
    while ((!paramTask.isFreeformTask()) || (paramInt != 1)) {
      return;
    }
    int k = 0;
    i -= 1;
    for (;;)
    {
      int j = k;
      if (i >= 0)
      {
        if (!((Task)localArrayList.get(i)).isFreeformTask()) {
          j = i + 1;
        }
      }
      else
      {
        this.mStackTaskList.moveTaskToStack(paramTask, j, paramInt);
        return;
      }
      i -= 1;
    }
  }
  
  public void removeAllTasks()
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject = this.mStackTaskList.getTasks();
    int j = ((ArrayList)localObject).size();
    int i = 0;
    while (i < j)
    {
      Task localTask = (Task)((ArrayList)localObject).get(i);
      if (!localTask.isLocked) {
        localArrayList.add(localTask);
      }
      i += 1;
    }
    i = localArrayList.size() - 1;
    while (i >= 0)
    {
      localObject = (Task)localArrayList.get(i);
      removeTaskImpl(this.mStackTaskList, (Task)localObject);
      this.mRawTaskList.remove(localObject);
      i -= 1;
    }
    if (this.mCb != null) {
      this.mCb.onStackTasksRemoved(this);
    }
  }
  
  public void removeGroup(TaskGrouping paramTaskGrouping)
  {
    this.mGroups.remove(paramTaskGrouping);
    this.mAffinitiesGroups.remove(Integer.valueOf(paramTaskGrouping.affiliation));
  }
  
  public void removeTask(Task paramTask, AnimationProps paramAnimationProps, boolean paramBoolean)
  {
    if (this.mStackTaskList.contains(paramTask))
    {
      removeTaskImpl(this.mStackTaskList, paramTask);
      Task localTask = getStackFrontMostTask(false);
      if (this.mCb != null) {
        this.mCb.onStackTaskRemoved(this, paramTask, localTask, paramAnimationProps, paramBoolean);
      }
    }
    this.mRawTaskList.remove(paramTask);
  }
  
  void removeTaskImpl(FilteredTaskList paramFilteredTaskList, Task paramTask)
  {
    paramFilteredTaskList.remove(paramTask);
    paramFilteredTaskList = paramTask.group;
    if (paramFilteredTaskList != null)
    {
      paramFilteredTaskList.removeTask(paramTask);
      if (paramFilteredTaskList.getTaskCount() == 0) {
        removeGroup(paramFilteredTaskList);
      }
    }
  }
  
  public void setCallbacks(TaskStackCallbacks paramTaskStackCallbacks)
  {
    this.mCb = paramTaskStackCallbacks;
  }
  
  public void setTasks(Context paramContext, List<Task> paramList, boolean paramBoolean)
  {
    ArrayMap localArrayMap = createTaskKeyMapFromList(this.mRawTaskList);
    Object localObject = createTaskKeyMapFromList(paramList);
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    if (this.mCb == null) {
      paramBoolean = false;
    }
    int i = this.mRawTaskList.size() - 1;
    Task localTask1;
    while (i >= 0)
    {
      localTask1 = (Task)this.mRawTaskList.get(i);
      if ((!((ArrayMap)localObject).containsKey(localTask1.key)) && (paramBoolean)) {
        localArrayList2.add(localTask1);
      }
      localTask1.setGroup(null);
      i -= 1;
    }
    int j = paramList.size();
    i = 0;
    if (i < j)
    {
      localTask1 = (Task)paramList.get(i);
      Task localTask2 = (Task)localArrayMap.get(localTask1.key);
      if ((localTask2 == null) && (paramBoolean))
      {
        localArrayList1.add(localTask1);
        localObject = localTask1;
      }
      for (;;)
      {
        localArrayList3.add(localObject);
        i += 1;
        break;
        localObject = localTask1;
        if (localTask2 != null)
        {
          localTask2.copyFrom(localTask1);
          localObject = localTask2;
        }
      }
    }
    i = localArrayList3.size() - 1;
    while (i >= 0)
    {
      ((Task)localArrayList3.get(i)).temporarySortIndexInStack = i;
      i -= 1;
    }
    Collections.sort(localArrayList3, this.FREEFORM_COMPARATOR);
    this.mStackTaskList.set(localArrayList3);
    this.mRawTaskList = localArrayList3;
    createAffiliatedGroupings(paramContext);
    j = localArrayList2.size();
    paramContext = getStackFrontMostTask(false);
    i = 0;
    while (i < j)
    {
      this.mCb.onStackTaskRemoved(this, (Task)localArrayList2.get(i), paramContext, AnimationProps.IMMEDIATE, false);
      i += 1;
    }
    j = localArrayList1.size();
    i = 0;
    while (i < j)
    {
      this.mCb.onStackTaskAdded(this, (Task)localArrayList1.get(i));
      i += 1;
    }
    if (paramBoolean) {
      this.mCb.onStackTasksUpdated(this);
    }
  }
  
  public String toString()
  {
    String str = "Stack Tasks (" + this.mStackTaskList.size() + "):\n";
    ArrayList localArrayList = this.mStackTaskList.getTasks();
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      str = str + "    " + ((Task)localArrayList.get(i)).toString() + "\n";
      i += 1;
    }
    return str;
  }
  
  public static class DockState
    implements DropTarget
  {
    public static final DockState BOTTOM = new DockState(4, 1, 80, 0, 0, new RectF(0.0F, 0.875F, 1.0F, 1.0F), new RectF(0.0F, 0.875F, 1.0F, 1.0F), new RectF(0.0F, 0.5F, 1.0F, 1.0F));
    public static final DockState LEFT;
    public static final DockState NONE = new DockState(-1, -1, 80, 255, 0, null, null, null);
    public static final DockState RIGHT;
    public static final DockState TOP;
    private static final Rect mTmpRect = new Rect();
    public final int createMode;
    private final RectF dockArea;
    public final int dockSide;
    private final RectF expandedTouchDockArea;
    private final RectF touchArea;
    public final ViewState viewState;
    
    static
    {
      LEFT = new DockState(1, 0, 80, 0, 1, new RectF(0.0F, 0.0F, 0.125F, 1.0F), new RectF(0.0F, 0.0F, 0.125F, 1.0F), new RectF(0.0F, 0.0F, 0.5F, 1.0F));
      TOP = new DockState(2, 0, 80, 0, 0, new RectF(0.0F, 0.0F, 1.0F, 0.125F), new RectF(0.0F, 0.0F, 1.0F, 0.125F), new RectF(0.0F, 0.0F, 1.0F, 0.5F));
      RIGHT = new DockState(3, 1, 80, 0, 1, new RectF(0.875F, 0.0F, 1.0F, 1.0F), new RectF(0.875F, 0.0F, 1.0F, 1.0F), new RectF(0.5F, 0.0F, 1.0F, 1.0F));
    }
    
    DockState(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, RectF paramRectF1, RectF paramRectF2, RectF paramRectF3)
    {
      this.dockSide = paramInt1;
      this.createMode = paramInt2;
      this.viewState = new ViewState(paramInt3, paramInt4, paramInt5, 2131690333, null);
      this.dockArea = paramRectF2;
      this.touchArea = paramRectF1;
      this.expandedTouchDockArea = paramRectF3;
    }
    
    private void getMappedRect(RectF paramRectF, int paramInt1, int paramInt2, Rect paramRect)
    {
      paramRect.set((int)(paramRectF.left * paramInt1), (int)(paramRectF.top * paramInt2), (int)(paramRectF.right * paramInt1), (int)(paramRectF.bottom * paramInt2));
    }
    
    private Rect updateBoundsWithSystemInsets(Rect paramRect1, Rect paramRect2)
    {
      if (this.dockSide == 1) {
        paramRect1.right += paramRect2.left;
      }
      while (this.dockSide != 3) {
        return paramRect1;
      }
      paramRect1.left -= paramRect2.right;
      return paramRect1;
    }
    
    public boolean acceptsDrop(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rect paramRect, boolean paramBoolean)
    {
      if (paramBoolean)
      {
        getMappedRect(this.expandedTouchDockArea, paramInt3, paramInt4, mTmpRect);
        return mTmpRect.contains(paramInt1, paramInt2);
      }
      getMappedRect(this.touchArea, paramInt3, paramInt4, mTmpRect);
      updateBoundsWithSystemInsets(mTmpRect, paramRect);
      return mTmpRect.contains(paramInt1, paramInt2);
    }
    
    public Rect getDockedBounds(int paramInt1, int paramInt2, int paramInt3, Rect paramRect, Resources paramResources)
    {
      boolean bool = true;
      if (paramResources.getConfiguration().orientation == 1) {}
      for (;;)
      {
        int i = DockedDividerUtils.calculateMiddlePosition(bool, paramRect, paramInt1, paramInt2, paramInt3);
        paramRect = new Rect();
        DockedDividerUtils.calculateBoundsForPosition(i, this.dockSide, paramRect, paramInt1, paramInt2, paramInt3);
        return paramRect;
        bool = false;
      }
    }
    
    public Rect getDockedTaskStackBounds(Rect paramRect1, int paramInt1, int paramInt2, int paramInt3, Rect paramRect2, TaskStackLayoutAlgorithm paramTaskStackLayoutAlgorithm, Resources paramResources, Rect paramRect3)
    {
      boolean bool;
      if (paramResources.getConfiguration().orientation == 1)
      {
        bool = true;
        DockedDividerUtils.calculateBoundsForPosition(DockedDividerUtils.calculateMiddlePosition(bool, paramRect2, paramInt1, paramInt2, paramInt3), DockedDividerUtils.invertDockSide(this.dockSide), paramRect3, paramInt1, paramInt2, paramInt3);
        paramResources = new Rect();
        if (this.dockArea.bottom >= 1.0F) {
          break label91;
        }
      }
      label91:
      for (paramInt1 = 0;; paramInt1 = paramRect2.top)
      {
        paramTaskStackLayoutAlgorithm.getTaskStackBounds(paramRect1, paramRect3, paramInt1, 0, paramRect2.right, paramResources);
        return paramResources;
        bool = false;
        break;
      }
    }
    
    public Rect getPreDockedBounds(int paramInt1, int paramInt2, Rect paramRect)
    {
      getMappedRect(this.dockArea, paramInt1, paramInt2, mTmpRect);
      return updateBoundsWithSystemInsets(mTmpRect, paramRect);
    }
    
    public void update(Context paramContext)
    {
      this.viewState.update(paramContext);
    }
    
    public static class ViewState
    {
      private static final IntProperty<ViewState> HINT_ALPHA = new IntProperty("drawableAlpha")
      {
        public Integer get(TaskStack.DockState.ViewState paramAnonymousViewState)
        {
          return Integer.valueOf(TaskStack.DockState.ViewState.-get0(paramAnonymousViewState));
        }
        
        public void setValue(TaskStack.DockState.ViewState paramAnonymousViewState, int paramAnonymousInt)
        {
          TaskStack.DockState.ViewState.-set0(paramAnonymousViewState, paramAnonymousInt);
          paramAnonymousViewState.dockAreaOverlay.invalidateSelf();
        }
      };
      public final int dockAreaAlpha;
      public final ColorDrawable dockAreaOverlay;
      public final int hintTextAlpha;
      public final int hintTextOrientation;
      private AnimatorSet mDockAreaOverlayAnimator;
      private String mHintText;
      private int mHintTextAlpha = 255;
      private Point mHintTextBounds = new Point();
      private Paint mHintTextPaint;
      private final int mHintTextResId;
      private Rect mTmpRect = new Rect();
      
      private ViewState(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        this.dockAreaAlpha = paramInt1;
        this.dockAreaOverlay = new ColorDrawable(-1);
        this.dockAreaOverlay.setAlpha(0);
        this.hintTextAlpha = paramInt2;
        this.hintTextOrientation = paramInt3;
        this.mHintTextResId = paramInt4;
        this.mHintTextPaint = new Paint(1);
        this.mHintTextPaint.setColor(-1);
      }
      
      public void draw(Canvas paramCanvas)
      {
        if (this.dockAreaOverlay.getAlpha() > 0) {
          this.dockAreaOverlay.draw(paramCanvas);
        }
        if (this.mHintTextAlpha > 0)
        {
          Rect localRect = this.dockAreaOverlay.getBounds();
          int i = localRect.left;
          int j = (localRect.width() - this.mHintTextBounds.x) / 2;
          int k = localRect.top;
          int m = (localRect.height() + this.mHintTextBounds.y) / 2;
          this.mHintTextPaint.setAlpha(this.mHintTextAlpha);
          if (this.hintTextOrientation == 1)
          {
            paramCanvas.save();
            paramCanvas.rotate(-90.0F, localRect.centerX(), localRect.centerY());
          }
          paramCanvas.drawText(this.mHintText, i + j, k + m, this.mHintTextPaint);
          if (this.hintTextOrientation == 1) {
            paramCanvas.restore();
          }
        }
      }
      
      public void startAnimation(Rect paramRect, int paramInt1, int paramInt2, int paramInt3, Interpolator paramInterpolator, boolean paramBoolean1, boolean paramBoolean2)
      {
        if (this.mDockAreaOverlayAnimator != null) {
          this.mDockAreaOverlayAnimator.cancel();
        }
        ArrayList localArrayList = new ArrayList();
        Object localObject;
        if (this.dockAreaOverlay.getAlpha() != paramInt1)
        {
          if (paramBoolean1)
          {
            localObject = ObjectAnimator.ofInt(this.dockAreaOverlay, Utilities.DRAWABLE_ALPHA, new int[] { this.dockAreaOverlay.getAlpha(), paramInt1 });
            ((ObjectAnimator)localObject).setDuration(paramInt3);
            ((ObjectAnimator)localObject).setInterpolator(paramInterpolator);
            localArrayList.add(localObject);
          }
        }
        else
        {
          if (this.mHintTextAlpha != paramInt2)
          {
            if (!paramBoolean1) {
              break label238;
            }
            ObjectAnimator localObjectAnimator = ObjectAnimator.ofInt(this, HINT_ALPHA, new int[] { this.mHintTextAlpha, paramInt2 });
            localObjectAnimator.setDuration(150L);
            if (paramInt2 <= this.mHintTextAlpha) {
              break label230;
            }
            localObject = Interpolators.ALPHA_IN;
            label150:
            localObjectAnimator.setInterpolator((TimeInterpolator)localObject);
            localArrayList.add(localObjectAnimator);
          }
          label165:
          if ((paramRect != null) && (!this.dockAreaOverlay.getBounds().equals(paramRect))) {
            break label253;
          }
        }
        for (;;)
        {
          if (!localArrayList.isEmpty())
          {
            this.mDockAreaOverlayAnimator = new AnimatorSet();
            this.mDockAreaOverlayAnimator.playTogether(localArrayList);
            this.mDockAreaOverlayAnimator.start();
          }
          return;
          this.dockAreaOverlay.setAlpha(paramInt1);
          break;
          label230:
          localObject = Interpolators.ALPHA_OUT;
          break label150;
          label238:
          this.mHintTextAlpha = paramInt2;
          this.dockAreaOverlay.invalidateSelf();
          break label165;
          label253:
          if (paramBoolean2)
          {
            paramRect = PropertyValuesHolder.ofObject(Utilities.DRAWABLE_RECT, Utilities.RECT_EVALUATOR, new Rect[] { new Rect(this.dockAreaOverlay.getBounds()), paramRect });
            paramRect = ObjectAnimator.ofPropertyValuesHolder(this.dockAreaOverlay, new PropertyValuesHolder[] { paramRect });
            paramRect.setDuration(paramInt3);
            paramRect.setInterpolator(paramInterpolator);
            localArrayList.add(paramRect);
          }
          else
          {
            this.dockAreaOverlay.setBounds(paramRect);
          }
        }
      }
      
      public void update(Context paramContext)
      {
        Resources localResources = paramContext.getResources();
        this.mHintText = paramContext.getString(this.mHintTextResId);
        this.mHintTextPaint.setTextSize(localResources.getDimensionPixelSize(2131755620));
        this.mHintTextPaint.getTextBounds(this.mHintText, 0, this.mHintText.length(), this.mTmpRect);
        this.mHintTextBounds.set((int)this.mHintTextPaint.measureText(this.mHintText), this.mTmpRect.height());
      }
    }
  }
  
  public static abstract interface TaskStackCallbacks
  {
    public abstract void onStackTaskAdded(TaskStack paramTaskStack, Task paramTask);
    
    public abstract void onStackTaskRemoved(TaskStack paramTaskStack, Task paramTask1, Task paramTask2, AnimationProps paramAnimationProps, boolean paramBoolean);
    
    public abstract void onStackTasksRemoved(TaskStack paramTaskStack);
    
    public abstract void onStackTasksUpdated(TaskStack paramTaskStack);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\model\TaskStack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */