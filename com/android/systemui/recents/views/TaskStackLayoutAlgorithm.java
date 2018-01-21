package com.android.systemui.recents.views;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.ArraySet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.ViewDebug.ExportedProperty;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsActivityLaunchState;
import com.android.systemui.recents.RecentsConfiguration;
import com.android.systemui.recents.RecentsDebugFlags;
import com.android.systemui.recents.misc.FreePathInterpolator;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.misc.Utilities;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.Task.TaskKey;
import com.android.systemui.recents.model.TaskGrouping;
import com.android.systemui.recents.model.TaskStack;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TaskStackLayoutAlgorithm
{
  TaskViewTransform mBackOfStackTransform = new TaskViewTransform();
  @ViewDebug.ExportedProperty(category="recents")
  private int mBaseBottomMargin;
  private int mBaseInitialBottomOffset;
  private int mBaseInitialTopOffset;
  @ViewDebug.ExportedProperty(category="recents")
  private int mBaseSideMargin;
  @ViewDebug.ExportedProperty(category="recents")
  private int mBaseTopMargin;
  private TaskStackLayoutAlgorithmCallbacks mCb;
  Context mContext;
  @ViewDebug.ExportedProperty(category="recents")
  private int mFocusState;
  @ViewDebug.ExportedProperty(category="recents")
  private int mFocusedBottomPeekHeight;
  private Path mFocusedCurve;
  private FreePathInterpolator mFocusedCurveInterpolator;
  private Path mFocusedDimCurve;
  private FreePathInterpolator mFocusedDimCurveInterpolator;
  private Range mFocusedRange;
  @ViewDebug.ExportedProperty(category="recents")
  private int mFocusedTopPeekHeight;
  FreeformWorkspaceLayoutAlgorithm mFreeformLayoutAlgorithm;
  @ViewDebug.ExportedProperty(category="recents")
  public Rect mFreeformRect = new Rect();
  @ViewDebug.ExportedProperty(category="recents")
  private int mFreeformStackGap;
  @ViewDebug.ExportedProperty(category="recents")
  float mFrontMostTaskP;
  TaskViewTransform mFrontOfStackTransform = new TaskViewTransform();
  @ViewDebug.ExportedProperty(category="recents")
  private int mInitialBottomOffset;
  @ViewDebug.ExportedProperty(category="recents")
  float mInitialScrollP;
  @ViewDebug.ExportedProperty(category="recents")
  private int mInitialTopOffset;
  @ViewDebug.ExportedProperty(category="recents")
  float mMaxScrollP;
  @ViewDebug.ExportedProperty(category="recents")
  int mMaxTranslationZ;
  private int mMinMargin;
  @ViewDebug.ExportedProperty(category="recents")
  float mMinScrollP;
  @ViewDebug.ExportedProperty(category="recents")
  int mMinTranslationZ;
  @ViewDebug.ExportedProperty(category="recents")
  int mNumFreeformTasks;
  @ViewDebug.ExportedProperty(category="recents")
  int mNumStackTasks;
  @ViewDebug.ExportedProperty(category="recents")
  public Rect mStackActionButtonRect = new Rect();
  @ViewDebug.ExportedProperty(category="recents")
  private int mStackBottomOffset;
  @ViewDebug.ExportedProperty(category="recents")
  public Rect mStackRect = new Rect();
  private StackState mState = StackState.SPLIT;
  @ViewDebug.ExportedProperty(category="recents")
  public Rect mSystemInsets = new Rect();
  private SparseIntArray mTaskIndexMap = new SparseIntArray();
  private SparseArray<Float> mTaskIndexOverrideMap = new SparseArray();
  @ViewDebug.ExportedProperty(category="recents")
  public Rect mTaskRect = new Rect();
  private Path mUnfocusedCurve;
  private FreePathInterpolator mUnfocusedCurveInterpolator;
  private Path mUnfocusedDimCurve;
  private FreePathInterpolator mUnfocusedDimCurveInterpolator;
  private Range mUnfocusedRange;
  
  public TaskStackLayoutAlgorithm(Context paramContext, TaskStackLayoutAlgorithmCallbacks paramTaskStackLayoutAlgorithmCallbacks)
  {
    Resources localResources = paramContext.getResources();
    this.mContext = paramContext;
    this.mCb = paramTaskStackLayoutAlgorithmCallbacks;
    this.mFreeformLayoutAlgorithm = new FreeformWorkspaceLayoutAlgorithm(paramContext);
    this.mMinMargin = localResources.getDimensionPixelSize(2131755588);
    this.mBaseTopMargin = getDimensionForDevice(paramContext, 2131755589, 2131755590, 2131755591);
    this.mBaseSideMargin = getDimensionForDevice(paramContext, 2131755593, 2131755594, 2131755596);
    this.mBaseBottomMargin = localResources.getDimensionPixelSize(2131755592);
    this.mFreeformStackGap = localResources.getDimensionPixelSize(2131755608);
    reloadOnConfigurationChange(paramContext);
  }
  
  private Path constructFocusedCurve()
  {
    float f1 = this.mFocusedTopPeekHeight / this.mStackRect.height();
    float f2 = (this.mStackBottomOffset + this.mFocusedBottomPeekHeight) / this.mStackRect.height();
    float f3 = (this.mFocusedTopPeekHeight + this.mTaskRect.height() - this.mMinMargin) / this.mStackRect.height();
    Path localPath = new Path();
    localPath.moveTo(0.0F, 1.0F);
    localPath.lineTo(0.5F, 1.0F - f1);
    localPath.lineTo(1.0F - 0.5F / this.mFocusedRange.relativeMax, Math.max(1.0F - f3, f2));
    localPath.lineTo(1.0F, 0.0F);
    return localPath;
  }
  
  private Path constructFocusedDimCurve()
  {
    Path localPath = new Path();
    localPath.moveTo(0.0F, 0.25F);
    localPath.lineTo(0.5F, 0.0F);
    localPath.lineTo(0.5F / this.mFocusedRange.relativeMax + 0.5F, 0.25F);
    localPath.lineTo(1.0F, 0.25F);
    return localPath;
  }
  
  private Path constructUnfocusedCurve()
  {
    float f1 = this.mFocusedTopPeekHeight / this.mStackRect.height();
    float f2 = (1.0F - f1 - 0.975F) / 0.099999994F;
    Path localPath = new Path();
    localPath.moveTo(0.0F, 1.0F);
    localPath.cubicTo(0.0F, 1.0F, 0.4F, 0.975F, 0.5F, 1.0F - f1);
    localPath.cubicTo(0.5F, 1.0F - f1, 0.65F, 0.65F * f2 + (1.0F - 0.4F * f2), 1.0F, 0.0F);
    return localPath;
  }
  
  private Path constructUnfocusedDimCurve()
  {
    float f1 = getNormalizedXFromUnfocusedY(this.mInitialTopOffset, 0);
    float f2 = f1 + (1.0F - f1) / 2.0F;
    Path localPath = new Path();
    localPath.moveTo(0.0F, 0.25F);
    localPath.cubicTo(0.5F * f1, 0.25F, 0.75F * f1, 0.1875F, f1, 0.0F);
    localPath.cubicTo(f2, 0.0F, f2, 0.15F, 1.0F, 0.15F);
    return localPath;
  }
  
  public static int getDimensionForDevice(Context paramContext, int paramInt1, int paramInt2, int paramInt3)
  {
    return getDimensionForDevice(paramContext, paramInt1, paramInt1, paramInt2, paramInt2, paramInt3, paramInt3);
  }
  
  public static int getDimensionForDevice(Context paramContext, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    RecentsConfiguration localRecentsConfiguration = Recents.getConfiguration();
    Resources localResources = paramContext.getResources();
    int i;
    if (Utilities.getAppConfiguration(paramContext).orientation == 2)
    {
      i = 1;
      if (!localRecentsConfiguration.isXLargeScreen) {
        break label59;
      }
      if (i == 0) {
        break label52;
      }
    }
    for (;;)
    {
      return localResources.getDimensionPixelSize(paramInt6);
      i = 0;
      break;
      label52:
      paramInt6 = paramInt5;
    }
    label59:
    if (localRecentsConfiguration.isLargeScreen)
    {
      if (i != 0) {}
      for (;;)
      {
        return localResources.getDimensionPixelSize(paramInt4);
        paramInt4 = paramInt3;
      }
    }
    if (i != 0) {}
    for (;;)
    {
      return localResources.getDimensionPixelSize(paramInt2);
      paramInt2 = paramInt1;
    }
  }
  
  private float getNormalizedXFromFocusedY(float paramFloat, int paramInt)
  {
    if (paramInt == 0) {
      paramFloat = this.mStackRect.height() - paramFloat;
    }
    for (;;)
    {
      paramFloat /= this.mStackRect.height();
      return this.mFocusedCurveInterpolator.getX(paramFloat);
    }
  }
  
  private float getNormalizedXFromUnfocusedY(float paramFloat, int paramInt)
  {
    if (paramInt == 0) {
      paramFloat = this.mStackRect.height() - paramFloat;
    }
    for (;;)
    {
      paramFloat /= this.mStackRect.height();
      return this.mUnfocusedCurveInterpolator.getX(paramFloat);
    }
  }
  
  private int getScaleForExtent(Rect paramRect1, Rect paramRect2, int paramInt1, int paramInt2, int paramInt3)
  {
    float f;
    if (paramInt3 == 0)
    {
      f = Utilities.clamp01(paramRect1.width() / paramRect2.width());
      return Math.max(paramInt2, (int)(paramInt1 * f));
    }
    if (paramInt3 == 1)
    {
      f = Utilities.clamp01(paramRect1.height() / paramRect2.height());
      return Math.max(paramInt2, (int)(paramInt1 * f));
    }
    return paramInt1;
  }
  
  private boolean isInvalidOverrideX(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    int i;
    if (this.mUnfocusedRange.getNormalizedX(paramFloat3) >= 0.0F)
    {
      if (this.mUnfocusedRange.getNormalizedX(paramFloat3) <= 1.0F) {
        break label54;
      }
      i = 1;
      if ((i == 0) && ((paramFloat2 < paramFloat1) || (paramFloat1 < paramFloat3))) {
        break label60;
      }
    }
    label54:
    label60:
    while ((paramFloat2 <= paramFloat1) && (paramFloat1 <= paramFloat3))
    {
      return true;
      i = 1;
      break;
      i = 0;
      break;
    }
    return false;
  }
  
  private void updateFrontBackTransforms()
  {
    if (this.mStackRect.isEmpty()) {
      return;
    }
    float f1 = Utilities.mapRange(this.mFocusState, this.mUnfocusedRange.relativeMin, this.mFocusedRange.relativeMin);
    float f2 = Utilities.mapRange(this.mFocusState, this.mUnfocusedRange.relativeMax, this.mFocusedRange.relativeMax);
    getStackTransform(f1, f1, 0.0F, this.mFocusState, this.mBackOfStackTransform, null, true, true);
    getStackTransform(f2, f2, 0.0F, this.mFocusState, this.mFrontOfStackTransform, null, true, true);
    this.mBackOfStackTransform.visible = true;
    this.mFrontOfStackTransform.visible = true;
  }
  
  public void addUnfocusedTaskOverride(Task paramTask, float paramFloat)
  {
    if (this.mFocusState != 0)
    {
      this.mFocusedRange.offset(paramFloat);
      this.mUnfocusedRange.offset(paramFloat);
      float f1 = this.mFocusedRange.getNormalizedX(this.mTaskIndexMap.get(paramTask.key.id));
      float f2 = this.mFocusedCurveInterpolator.getInterpolation(f1);
      f2 = this.mUnfocusedCurveInterpolator.getX(f2);
      float f3 = this.mUnfocusedRange.getAbsoluteX(f2);
      if (Float.compare(f1, f2) != 0) {
        this.mTaskIndexOverrideMap.put(paramTask.key.id, Float.valueOf(paramFloat + f3));
      }
    }
  }
  
  public void addUnfocusedTaskOverride(TaskView paramTaskView, float paramFloat)
  {
    this.mFocusedRange.offset(paramFloat);
    this.mUnfocusedRange.offset(paramFloat);
    Task localTask = paramTaskView.getTask();
    int i = paramTaskView.getTop() - this.mTaskRect.top;
    float f1 = getNormalizedXFromFocusedY(i, 0);
    float f2 = getNormalizedXFromUnfocusedY(i, 0);
    float f3 = this.mUnfocusedRange.getAbsoluteX(f2);
    if (Float.compare(f1, f2) != 0) {
      this.mTaskIndexOverrideMap.put(localTask.key.id, Float.valueOf(paramFloat + f3));
    }
  }
  
  public void clearUnfocusedTaskOverrides()
  {
    this.mTaskIndexOverrideMap.clear();
  }
  
  public VisibilityReport computeStackVisibilityReport(ArrayList<Task> paramArrayList)
  {
    if (paramArrayList.size() <= 1) {
      return new VisibilityReport(1, 1);
    }
    if (this.mNumStackTasks == 0) {
      return new VisibilityReport(Math.max(this.mNumFreeformTasks, 1), Math.max(this.mNumFreeformTasks, 1));
    }
    TaskViewTransform localTaskViewTransform = new TaskViewTransform();
    Range localRange;
    int i1;
    int i;
    int m;
    float f1;
    int j;
    label132:
    int k;
    Task localTask;
    float f2;
    int n;
    if (getInitialFocusState() > 0.0F)
    {
      localRange = this.mFocusedRange;
      localRange.offset(this.mInitialScrollP);
      i1 = this.mContext.getResources().getDimensionPixelSize(2131755610);
      i = Math.max(this.mNumFreeformTasks, 1);
      m = Math.max(this.mNumFreeformTasks, 1);
      f1 = 2.14748365E9F;
      j = paramArrayList.size() - 1;
      k = i;
      if (j < 0) {
        break label401;
      }
      localTask = (Task)paramArrayList.get(j);
      if (!localTask.isFreeformTask()) {
        break label198;
      }
      f2 = f1;
      n = m;
      k = i;
    }
    for (;;)
    {
      j -= 1;
      i = k;
      m = n;
      f1 = f2;
      break label132;
      localRange = this.mUnfocusedRange;
      break;
      label198:
      float f3 = getStackScrollForTask(localTask);
      k = i;
      n = m;
      f2 = f1;
      if (localRange.isInRange(f3))
      {
        boolean bool;
        if (localTask.group != null)
        {
          bool = localTask.group.isFrontMostTask(localTask);
          label246:
          if (!bool) {
            break label371;
          }
          getStackTransform(f3, f3, this.mInitialScrollP, this.mFocusState, localTaskViewTransform, null, false, false);
          f2 = localTaskViewTransform.rect.top;
          if (f1 - f2 <= i1) {
            break label320;
          }
        }
        label320:
        for (k = 1;; k = 0)
        {
          if (k == 0) {
            break label326;
          }
          n = m + 1;
          k = i + 1;
          break;
          bool = true;
          break label246;
        }
        for (;;)
        {
          label326:
          k = i;
          if (j < 0) {
            break;
          }
          i += 1;
          if (!localRange.isInRange(getStackScrollForTask((Task)paramArrayList.get(j)))) {}
          j -= 1;
        }
        label371:
        k = i;
        n = m;
        f2 = f1;
        if (!bool)
        {
          k = i + 1;
          n = m;
          f2 = f1;
        }
      }
    }
    label401:
    return new VisibilityReport(k, m);
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    String str = paramString + "  ";
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("TaskStackLayoutAlgorithm");
    paramPrintWriter.write(" numStackTasks=");
    paramPrintWriter.print(this.mNumStackTasks);
    paramPrintWriter.println();
    paramPrintWriter.print(str);
    paramPrintWriter.print("insets=");
    paramPrintWriter.print(Utilities.dumpRect(this.mSystemInsets));
    paramPrintWriter.print(" stack=");
    paramPrintWriter.print(Utilities.dumpRect(this.mStackRect));
    paramPrintWriter.print(" task=");
    paramPrintWriter.print(Utilities.dumpRect(this.mTaskRect));
    paramPrintWriter.print(" freeform=");
    paramPrintWriter.print(Utilities.dumpRect(this.mFreeformRect));
    paramPrintWriter.print(" actionButton=");
    paramPrintWriter.print(Utilities.dumpRect(this.mStackActionButtonRect));
    paramPrintWriter.println();
    paramPrintWriter.print(str);
    paramPrintWriter.print("minScroll=");
    paramPrintWriter.print(this.mMinScrollP);
    paramPrintWriter.print(" maxScroll=");
    paramPrintWriter.print(this.mMaxScrollP);
    paramPrintWriter.print(" initialScroll=");
    paramPrintWriter.print(this.mInitialScrollP);
    paramPrintWriter.println();
    paramPrintWriter.print(str);
    paramPrintWriter.print("focusState=");
    paramPrintWriter.print(this.mFocusState);
    paramPrintWriter.println();
    if (this.mTaskIndexOverrideMap.size() > 0)
    {
      int i = this.mTaskIndexOverrideMap.size() - 1;
      while (i >= 0)
      {
        int j = this.mTaskIndexOverrideMap.keyAt(i);
        float f1 = this.mTaskIndexMap.get(j);
        float f2 = ((Float)this.mTaskIndexOverrideMap.get(j, Float.valueOf(0.0F))).floatValue();
        paramPrintWriter.print(str);
        paramPrintWriter.print("taskId= ");
        paramPrintWriter.print(j);
        paramPrintWriter.print(" x= ");
        paramPrintWriter.print(f1);
        paramPrintWriter.print(" overrideX= ");
        paramPrintWriter.print(f2);
        paramPrintWriter.println();
        i -= 1;
      }
    }
  }
  
  public TaskViewTransform getBackOfStackTransform()
  {
    return this.mBackOfStackTransform;
  }
  
  public float getDeltaPForY(int paramInt1, int paramInt2)
  {
    return -((paramInt2 - paramInt1) / this.mStackRect.height() * this.mUnfocusedCurveInterpolator.getArcLength());
  }
  
  public int getFocusState()
  {
    return this.mFocusState;
  }
  
  public TaskViewTransform getFrontOfStackTransform()
  {
    return this.mFrontOfStackTransform;
  }
  
  public int getInitialFocusState()
  {
    RecentsActivityLaunchState localRecentsActivityLaunchState = Recents.getConfiguration().getLaunchState();
    if ((Recents.getDebugFlags().isPagingEnabled()) || (localRecentsActivityLaunchState.launchedWithAltTab)) {
      return 1;
    }
    return 0;
  }
  
  float getStackScrollForTask(Task paramTask)
  {
    Float localFloat = (Float)this.mTaskIndexOverrideMap.get(paramTask.key.id, null);
    if (localFloat == null) {
      return this.mTaskIndexMap.get(paramTask.key.id, 0);
    }
    return localFloat.floatValue();
  }
  
  float getStackScrollForTaskAtInitialOffset(Task paramTask)
  {
    float f = getNormalizedXFromUnfocusedY(this.mInitialTopOffset, 0);
    this.mUnfocusedRange.offset(0.0F);
    return Utilities.clamp(this.mTaskIndexMap.get(paramTask.key.id, 0) - Math.max(0.0F, this.mUnfocusedRange.getAbsoluteX(f)), this.mMinScrollP, this.mMaxScrollP);
  }
  
  float getStackScrollForTaskIgnoreOverrides(Task paramTask)
  {
    return this.mTaskIndexMap.get(paramTask.key.id, 0);
  }
  
  public StackState getStackState()
  {
    return this.mState;
  }
  
  public TaskViewTransform getStackTransform(Task paramTask, float paramFloat, int paramInt, TaskViewTransform paramTaskViewTransform1, TaskViewTransform paramTaskViewTransform2, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mFreeformLayoutAlgorithm.isTransformAvailable(paramTask, this))
    {
      this.mFreeformLayoutAlgorithm.getTransform(paramTask, paramTaskViewTransform1, this);
      return paramTaskViewTransform1;
    }
    int i = this.mTaskIndexMap.get(paramTask.key.id, -1);
    if ((paramTask == null) || (i == -1))
    {
      paramTaskViewTransform1.reset();
      return paramTaskViewTransform1;
    }
    if (paramBoolean2) {}
    for (float f = i;; f = getStackScrollForTask(paramTask))
    {
      getStackTransform(f, i, paramFloat, paramInt, paramTaskViewTransform1, paramTaskViewTransform2, false, paramBoolean1);
      return paramTaskViewTransform1;
    }
  }
  
  public TaskViewTransform getStackTransform(Task paramTask, float paramFloat, TaskViewTransform paramTaskViewTransform1, TaskViewTransform paramTaskViewTransform2)
  {
    return getStackTransform(paramTask, paramFloat, this.mFocusState, paramTaskViewTransform1, paramTaskViewTransform2, false, false);
  }
  
  public TaskViewTransform getStackTransform(Task paramTask, float paramFloat, TaskViewTransform paramTaskViewTransform1, TaskViewTransform paramTaskViewTransform2, boolean paramBoolean)
  {
    return getStackTransform(paramTask, paramFloat, this.mFocusState, paramTaskViewTransform1, paramTaskViewTransform2, false, paramBoolean);
  }
  
  public void getStackTransform(float paramFloat1, float paramFloat2, float paramFloat3, int paramInt, TaskViewTransform paramTaskViewTransform1, TaskViewTransform paramTaskViewTransform2, boolean paramBoolean1, boolean paramBoolean2)
  {
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    this.mUnfocusedRange.offset(paramFloat3);
    this.mFocusedRange.offset(paramFloat3);
    boolean bool1 = this.mUnfocusedRange.isInRange(paramFloat1);
    boolean bool2 = this.mFocusedRange.isInRange(paramFloat1);
    if ((paramBoolean2) || (bool1))
    {
      this.mUnfocusedRange.offset(paramFloat3);
      this.mFocusedRange.offset(paramFloat3);
      float f3 = this.mUnfocusedRange.getNormalizedX(paramFloat1);
      float f4 = this.mFocusedRange.getNormalizedX(paramFloat1);
      float f1 = Utilities.clamp(paramFloat3, this.mMinScrollP, this.mMaxScrollP);
      this.mUnfocusedRange.offset(f1);
      this.mFocusedRange.offset(f1);
      f1 = this.mUnfocusedRange.getNormalizedX(paramFloat1);
      float f2 = this.mUnfocusedRange.getNormalizedX(paramFloat2);
      float f5 = Utilities.clamp(paramFloat3, -3.4028235E38F, this.mMaxScrollP);
      this.mUnfocusedRange.offset(f5);
      this.mFocusedRange.offset(f5);
      f5 = this.mUnfocusedRange.getNormalizedX(paramFloat1);
      paramFloat1 = this.mFocusedRange.getNormalizedX(paramFloat1);
      int j = (this.mStackRect.width() - this.mTaskRect.width()) / 2;
      if ((!localSystemServicesProxy.hasFreeformWorkspaceSupport()) && (this.mNumStackTasks == 1) && (!paramBoolean1)) {
        break label534;
      }
      int i = (int)((1.0F - this.mUnfocusedCurveInterpolator.getInterpolation(f3)) * this.mStackRect.height());
      int k = (int)((1.0F - this.mFocusedCurveInterpolator.getInterpolation(f4)) * this.mStackRect.height());
      paramFloat3 = this.mUnfocusedDimCurveInterpolator.getInterpolation(f5);
      f3 = this.mFocusedDimCurveInterpolator.getInterpolation(paramFloat1);
      paramFloat1 = paramFloat3;
      if (this.mNumStackTasks <= 2)
      {
        paramFloat1 = paramFloat3;
        if (paramFloat2 == 0.0F)
        {
          if (f1 < 0.5F) {
            break label610;
          }
          paramFloat1 = 0.0F;
        }
      }
      label323:
      i = this.mStackRect.top - this.mTaskRect.top + (int)Utilities.mapRange(paramInt, i, k);
      paramFloat3 = Utilities.mapRange(Utilities.clamp01(f2), this.mMinTranslationZ, this.mMaxTranslationZ);
      paramFloat1 = Utilities.mapRange(paramInt, paramFloat1, f3);
      paramFloat2 = Utilities.mapRange(Utilities.clamp01(f1), 0.0F, 2.0F);
      paramInt = i;
      label398:
      paramTaskViewTransform1.scale = 1.0F;
      paramTaskViewTransform1.alpha = 1.0F;
      paramTaskViewTransform1.translationZ = paramFloat3;
      paramTaskViewTransform1.dimAlpha = paramFloat1;
      paramTaskViewTransform1.viewOutlineAlpha = paramFloat2;
      paramTaskViewTransform1.rect.set(this.mTaskRect);
      paramTaskViewTransform1.rect.offset(j, paramInt);
      Utilities.scaleRectAboutCenter(paramTaskViewTransform1.rect, paramTaskViewTransform1.scale);
      if (paramTaskViewTransform1.rect.top >= this.mStackRect.bottom) {
        break label641;
      }
      if ((paramTaskViewTransform2 != null) && (paramTaskViewTransform1.rect.top == paramTaskViewTransform2.rect.top)) {
        break label635;
      }
      paramBoolean1 = true;
    }
    for (;;)
    {
      paramTaskViewTransform1.visible = paramBoolean1;
      return;
      if (bool2) {
        break;
      }
      paramTaskViewTransform1.reset();
      return;
      label534:
      paramFloat1 = (this.mMinScrollP - paramFloat3) / this.mNumStackTasks;
      paramInt = this.mStackRect.top - this.mTaskRect.top + (this.mStackRect.height() - this.mSystemInsets.bottom - this.mTaskRect.height()) / 2 + getYForDeltaP(paramFloat1, 0.0F);
      paramFloat3 = this.mMaxTranslationZ;
      paramFloat1 = 0.0F;
      paramFloat2 = 1.0F;
      break label398;
      label610:
      paramFloat1 = this.mUnfocusedDimCurveInterpolator.getInterpolation(0.5F);
      paramFloat1 = (paramFloat3 - paramFloat1) * (0.25F / (0.25F - paramFloat1));
      break label323;
      label635:
      paramBoolean1 = false;
      continue;
      label641:
      paramBoolean1 = false;
    }
  }
  
  public TaskViewTransform getStackTransformScreenCoordinates(Task paramTask, float paramFloat, TaskViewTransform paramTaskViewTransform1, TaskViewTransform paramTaskViewTransform2, Rect paramRect)
  {
    return transformToScreenCoordinates(getStackTransform(paramTask, paramFloat, this.mFocusState, paramTaskViewTransform1, paramTaskViewTransform2, true, false), paramRect);
  }
  
  public void getTaskStackBounds(Rect paramRect1, Rect paramRect2, int paramInt1, int paramInt2, int paramInt3, Rect paramRect3)
  {
    paramRect3.set(paramRect2.left + paramInt2, paramRect2.top + paramInt1, paramRect2.right - paramInt3, paramRect2.bottom);
    paramInt1 = getScaleForExtent(paramRect2, paramRect1, this.mBaseSideMargin, this.mMinMargin, 0);
    paramInt2 = paramRect3.width() - paramInt1 * 2;
    paramInt1 = paramInt2;
    if (Utilities.getAppConfiguration(this.mContext).orientation == 2)
    {
      paramRect1 = new Rect(0, 0, Math.min(paramRect1.width(), paramRect1.height()), Math.max(paramRect1.width(), paramRect1.height()));
      paramInt1 = getScaleForExtent(paramRect1, paramRect1, this.mBaseSideMargin, this.mMinMargin, 0);
      paramInt1 = Math.min(paramInt2, paramRect1.width() - paramInt1 * 2);
    }
    paramRect3.inset((paramRect3.width() - paramInt1) / 2, 0);
  }
  
  public Rect getUntransformedTaskViewBounds()
  {
    return new Rect(this.mTaskRect);
  }
  
  public int getYForDeltaP(float paramFloat1, float paramFloat2)
  {
    return -(int)((paramFloat2 - paramFloat1) * this.mStackRect.height() * (1.0F / this.mUnfocusedCurveInterpolator.getArcLength()));
  }
  
  public void initialize(Rect paramRect1, Rect paramRect2, Rect paramRect3, StackState paramStackState)
  {
    Rect localRect = new Rect(this.mStackRect);
    int i = getScaleForExtent(paramRect2, paramRect1, this.mBaseTopMargin, this.mMinMargin, 1);
    int j = getScaleForExtent(paramRect2, paramRect1, this.mBaseBottomMargin, this.mMinMargin, 1);
    this.mInitialTopOffset = getScaleForExtent(paramRect2, paramRect1, this.mBaseInitialTopOffset, this.mMinMargin, 1);
    this.mInitialBottomOffset = this.mBaseInitialBottomOffset;
    this.mState = paramStackState;
    this.mStackBottomOffset = (this.mSystemInsets.bottom + j);
    paramStackState.computeRects(this.mFreeformRect, this.mStackRect, paramRect3, i, this.mFreeformStackGap, this.mStackBottomOffset);
    this.mStackActionButtonRect.set(this.mStackRect.left, this.mStackRect.top - i, this.mStackRect.right, this.mStackRect.top + this.mFocusedTopPeekHeight);
    i = this.mStackRect.height();
    j = this.mInitialTopOffset;
    int k = this.mStackBottomOffset;
    this.mTaskRect.set(this.mStackRect.left, this.mStackRect.top, this.mStackRect.right, this.mStackRect.top + (i - j - k));
    if (!localRect.equals(this.mStackRect))
    {
      this.mUnfocusedCurve = constructUnfocusedCurve();
      this.mUnfocusedCurveInterpolator = new FreePathInterpolator(this.mUnfocusedCurve);
      this.mFocusedCurve = constructFocusedCurve();
      this.mFocusedCurveInterpolator = new FreePathInterpolator(this.mFocusedCurve);
      this.mUnfocusedDimCurve = constructUnfocusedDimCurve();
      this.mUnfocusedDimCurveInterpolator = new FreePathInterpolator(this.mUnfocusedDimCurve);
      this.mFocusedDimCurve = constructFocusedDimCurve();
      this.mFocusedDimCurveInterpolator = new FreePathInterpolator(this.mFocusedDimCurve);
      updateFrontBackTransforms();
    }
  }
  
  public boolean isInitialized()
  {
    return !this.mStackRect.isEmpty();
  }
  
  public void reloadOnConfigurationChange(Context paramContext)
  {
    Resources localResources = paramContext.getResources();
    this.mFocusedRange = new Range(localResources.getFloat(2131624002), localResources.getFloat(2131624003));
    this.mUnfocusedRange = new Range(localResources.getFloat(2131624004), localResources.getFloat(2131624005));
    this.mFocusState = getInitialFocusState();
    this.mFocusedTopPeekHeight = localResources.getDimensionPixelSize(2131755598);
    this.mFocusedBottomPeekHeight = localResources.getDimensionPixelSize(2131755599);
    this.mMinTranslationZ = localResources.getDimensionPixelSize(2131755606);
    this.mMaxTranslationZ = localResources.getDimensionPixelSize(2131755607);
    this.mBaseInitialTopOffset = getDimensionForDevice(paramContext, 2131755600, 2131755602, 2131755604, 2131755604, 2131755604, 2131755604);
    this.mBaseInitialBottomOffset = getDimensionForDevice(paramContext, 2131755601, 2131755603, 2131755605, 2131755605, 2131755605, 2131755605);
    this.mFreeformLayoutAlgorithm.reloadOnConfigurationChange(paramContext);
  }
  
  public void reset()
  {
    this.mTaskIndexOverrideMap.clear();
    setFocusState(getInitialFocusState());
  }
  
  public void setFocusState(int paramInt)
  {
    int i = this.mFocusState;
    this.mFocusState = paramInt;
    updateFrontBackTransforms();
    if (this.mCb != null) {
      this.mCb.onFocusStateChanged(i, paramInt);
    }
  }
  
  public boolean setSystemInsets(Rect paramRect)
  {
    if (this.mSystemInsets.equals(paramRect)) {}
    for (boolean bool = false;; bool = true)
    {
      this.mSystemInsets.set(paramRect);
      return bool;
    }
  }
  
  public void setTaskOverridesForInitialState(TaskStack paramTaskStack, boolean paramBoolean)
  {
    Object localObject = Recents.getConfiguration().getLaunchState();
    this.mTaskIndexOverrideMap.clear();
    if ((!((RecentsActivityLaunchState)localObject).launchedFromHome) && (!((RecentsActivityLaunchState)localObject).launchedFromBlacklistedApp)) {}
    for (boolean bool = ((RecentsActivityLaunchState)localObject).launchedViaDockGesture; (getInitialFocusState() != 0) || (this.mNumStackTasks <= 1) || ((!paramBoolean) && ((((RecentsActivityLaunchState)localObject).launchedWithAltTab) || (bool))); bool = true) {
      return;
    }
    float f1 = getNormalizedXFromUnfocusedY(this.mSystemInsets.bottom + this.mInitialBottomOffset, 1);
    float f2 = getNormalizedXFromUnfocusedY(this.mFocusedTopPeekHeight + this.mTaskRect.height() - this.mMinMargin, 0);
    if (this.mNumStackTasks <= 2)
    {
      localObject = new float[2];
      localObject[0] = Math.min(f2, f1);
      localObject[1] = getNormalizedXFromUnfocusedY(this.mFocusedTopPeekHeight, 0);
    }
    for (;;)
    {
      this.mUnfocusedRange.offset(0.0F);
      paramTaskStack = paramTaskStack.getStackTasks();
      int j = paramTaskStack.size();
      int i = j - 1;
      for (;;)
      {
        if (i < 0) {
          break label264;
        }
        int k = j - i - 1;
        if (k >= localObject.length) {
          break;
        }
        f1 = this.mInitialScrollP;
        f2 = this.mUnfocusedRange.getAbsoluteX(localObject[k]);
        this.mTaskIndexOverrideMap.put(((Task)paramTaskStack.get(i)).key.id, Float.valueOf(f1 + f2));
        i -= 1;
      }
      label264:
      break;
      localObject = new float[2];
      localObject[0] = f1;
      localObject[1] = getNormalizedXFromUnfocusedY(this.mInitialTopOffset, 0);
    }
  }
  
  public TaskViewTransform transformToScreenCoordinates(TaskViewTransform paramTaskViewTransform, Rect paramRect)
  {
    if (paramRect != null) {}
    for (;;)
    {
      paramTaskViewTransform.rect.offset(paramRect.left, paramRect.top);
      return paramTaskViewTransform;
      paramRect = Recents.getSystemServices().getWindowRect();
    }
  }
  
  void update(TaskStack paramTaskStack, ArraySet<Task.TaskKey> paramArraySet)
  {
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    RecentsActivityLaunchState localRecentsActivityLaunchState = Recents.getConfiguration().getLaunchState();
    this.mTaskIndexMap.clear();
    ArrayList localArrayList3 = paramTaskStack.getStackTasks();
    if (localArrayList3.isEmpty())
    {
      this.mFrontMostTaskP = 0.0F;
      this.mInitialScrollP = 0.0F;
      this.mMaxScrollP = 0.0F;
      this.mMinScrollP = 0.0F;
      this.mNumFreeformTasks = 0;
      this.mNumStackTasks = 0;
      return;
    }
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    int i = 0;
    if (i < localArrayList3.size())
    {
      Task localTask = (Task)localArrayList3.get(i);
      if (paramArraySet.contains(localTask.key)) {}
      for (;;)
      {
        i += 1;
        break;
        if (localTask.isFreeformTask()) {
          localArrayList1.add(localTask);
        } else {
          localArrayList2.add(localTask);
        }
      }
    }
    this.mNumStackTasks = localArrayList2.size();
    this.mNumFreeformTasks = localArrayList1.size();
    int j = localArrayList2.size();
    i = 0;
    while (i < j)
    {
      paramArraySet = (Task)localArrayList2.get(i);
      this.mTaskIndexMap.put(paramArraySet.key.id, i);
      i += 1;
    }
    if (!localArrayList1.isEmpty()) {
      this.mFreeformLayoutAlgorithm.update(localArrayList1, this);
    }
    paramArraySet = paramTaskStack.getLaunchTarget();
    if (paramArraySet != null) {
      i = paramTaskStack.indexOfStackTask(paramArraySet);
    }
    while (getInitialFocusState() == 1)
    {
      f = getNormalizedXFromFocusedY(this.mStackBottomOffset + this.mTaskRect.height(), 1);
      this.mFocusedRange.offset(0.0F);
      this.mMinScrollP = 0.0F;
      this.mMaxScrollP = Math.max(this.mMinScrollP, this.mNumStackTasks - 1 - Math.max(0.0F, this.mFocusedRange.getAbsoluteX(f)));
      if (localRecentsActivityLaunchState.launchedFromHome)
      {
        this.mInitialScrollP = Utilities.clamp(i, this.mMinScrollP, this.mMaxScrollP);
        return;
        i = this.mNumStackTasks - 1;
      }
      else
      {
        this.mInitialScrollP = Utilities.clamp(i - 1, this.mMinScrollP, this.mMaxScrollP);
        return;
      }
    }
    if ((!localSystemServicesProxy.hasFreeformWorkspaceSupport()) && (this.mNumStackTasks == 1))
    {
      this.mMinScrollP = 0.0F;
      this.mMaxScrollP = 0.0F;
      this.mInitialScrollP = 0.0F;
      return;
    }
    float f = getNormalizedXFromUnfocusedY(this.mStackBottomOffset + this.mTaskRect.height(), 1);
    this.mUnfocusedRange.offset(0.0F);
    this.mMinScrollP = 0.0F;
    this.mMaxScrollP = Math.max(this.mMinScrollP, this.mNumStackTasks - 1 - Math.max(0.0F, this.mUnfocusedRange.getAbsoluteX(f)));
    if (!localRecentsActivityLaunchState.launchedFromHome) {}
    for (boolean bool = localRecentsActivityLaunchState.launchedViaDockGesture; localRecentsActivityLaunchState.launchedFromBlacklistedApp; bool = true)
    {
      this.mInitialScrollP = this.mMaxScrollP;
      return;
    }
    if (localRecentsActivityLaunchState.launchedWithAltTab)
    {
      this.mInitialScrollP = Utilities.clamp(i, this.mMinScrollP, this.mMaxScrollP);
      return;
    }
    if (bool)
    {
      this.mInitialScrollP = Utilities.clamp(i, this.mMinScrollP, this.mMaxScrollP);
      return;
    }
    f = getNormalizedXFromUnfocusedY(this.mInitialTopOffset, 0);
    this.mInitialScrollP = Math.max(this.mMinScrollP, Math.min(this.mMaxScrollP, this.mNumStackTasks - 2) - Math.max(0.0F, this.mUnfocusedRange.getAbsoluteX(f)));
  }
  
  public float updateFocusStateOnScroll(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramFloat2 == paramFloat3) {
      return paramFloat2;
    }
    float f2 = paramFloat2 - paramFloat3;
    float f1 = paramFloat2;
    this.mUnfocusedRange.offset(paramFloat2);
    int i = this.mTaskIndexOverrideMap.size() - 1;
    if (i >= 0)
    {
      int j = this.mTaskIndexOverrideMap.keyAt(i);
      float f3 = this.mTaskIndexMap.get(j);
      float f4 = ((Float)this.mTaskIndexOverrideMap.get(j, Float.valueOf(0.0F))).floatValue();
      float f5 = f4 + f2;
      if (isInvalidOverrideX(f3, f4, f5)) {
        this.mTaskIndexOverrideMap.removeAt(i);
      }
      for (;;)
      {
        i -= 1;
        break;
        if ((f4 >= f3) && (f2 <= 0.0F)) {}
        while ((f4 <= f3) && (f2 >= 0.0F))
        {
          this.mTaskIndexOverrideMap.put(j, Float.valueOf(f5));
          break;
        }
        f1 = paramFloat3;
        f5 = f4 - (paramFloat2 - paramFloat1);
        if (isInvalidOverrideX(f3, f4, f5)) {
          this.mTaskIndexOverrideMap.removeAt(i);
        } else {
          this.mTaskIndexOverrideMap.put(j, Float.valueOf(f5));
        }
      }
    }
    return f1;
  }
  
  public static class StackState
  {
    public static final StackState FREEFORM_ONLY = new StackState(1.0F, 255);
    public static final StackState SPLIT = new StackState(0.5F, 255);
    public static final StackState STACK_ONLY = new StackState(0.0F, 0);
    public final int freeformBackgroundAlpha;
    public final float freeformHeightPct;
    
    private StackState(float paramFloat, int paramInt)
    {
      this.freeformHeightPct = paramFloat;
      this.freeformBackgroundAlpha = paramInt;
    }
    
    public static StackState getStackStateForStack(TaskStack paramTaskStack)
    {
      boolean bool = Recents.getSystemServices().hasFreeformWorkspaceSupport();
      int i = paramTaskStack.getFreeformTaskCount();
      int j = paramTaskStack.getStackTaskCount();
      if ((bool) && (j > 0) && (i > 0)) {
        return SPLIT;
      }
      if ((bool) && (i > 0)) {
        return FREEFORM_ONLY;
      }
      return STACK_ONLY;
    }
    
    public void computeRects(Rect paramRect1, Rect paramRect2, Rect paramRect3, int paramInt1, int paramInt2, int paramInt3)
    {
      paramInt3 = (int)((paramRect3.height() - paramInt1 - paramInt3) * this.freeformHeightPct);
      paramInt2 = Math.max(0, paramInt3 - paramInt2);
      paramRect1.set(paramRect3.left, paramRect3.top + paramInt1, paramRect3.right, paramRect3.top + paramInt1 + paramInt2);
      paramRect2.set(paramRect3.left, paramRect3.top, paramRect3.right, paramRect3.bottom);
      if (paramInt3 > 0)
      {
        paramRect2.top += paramInt3;
        return;
      }
      paramRect2.top += paramInt1;
    }
  }
  
  public static abstract interface TaskStackLayoutAlgorithmCallbacks
  {
    public abstract void onFocusStateChanged(int paramInt1, int paramInt2);
  }
  
  public class VisibilityReport
  {
    public int numVisibleTasks;
    public int numVisibleThumbnails;
    
    VisibilityReport(int paramInt1, int paramInt2)
    {
      this.numVisibleTasks = paramInt1;
      this.numVisibleThumbnails = paramInt2;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\TaskStackLayoutAlgorithm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */