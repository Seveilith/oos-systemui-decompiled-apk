package com.android.systemui.recents.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.ArrayMap;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.Task.TaskKey;
import java.util.Collections;
import java.util.List;

public class FreeformWorkspaceLayoutAlgorithm
{
  private int mTaskPadding;
  private ArrayMap<Task.TaskKey, RectF> mTaskRectMap = new ArrayMap();
  
  public FreeformWorkspaceLayoutAlgorithm(Context paramContext)
  {
    reloadOnConfigurationChange(paramContext);
  }
  
  public TaskViewTransform getTransform(Task paramTask, TaskViewTransform paramTaskViewTransform, TaskStackLayoutAlgorithm paramTaskStackLayoutAlgorithm)
  {
    if (this.mTaskRectMap.containsKey(paramTask.key))
    {
      paramTask = (RectF)this.mTaskRectMap.get(paramTask.key);
      paramTaskViewTransform.scale = 1.0F;
      paramTaskViewTransform.alpha = 1.0F;
      paramTaskViewTransform.translationZ = paramTaskStackLayoutAlgorithm.mMaxTranslationZ;
      paramTaskViewTransform.dimAlpha = 0.0F;
      paramTaskViewTransform.viewOutlineAlpha = 2.0F;
      paramTaskViewTransform.rect.set(paramTask);
      paramTaskViewTransform.rect.offset(paramTaskStackLayoutAlgorithm.mFreeformRect.left, paramTaskStackLayoutAlgorithm.mFreeformRect.top);
      paramTaskViewTransform.visible = true;
      return paramTaskViewTransform;
    }
    return null;
  }
  
  public boolean isTransformAvailable(Task paramTask, TaskStackLayoutAlgorithm paramTaskStackLayoutAlgorithm)
  {
    if ((paramTaskStackLayoutAlgorithm.mNumFreeformTasks == 0) || (paramTask == null)) {
      return false;
    }
    return this.mTaskRectMap.containsKey(paramTask.key);
  }
  
  public void reloadOnConfigurationChange(Context paramContext)
  {
    this.mTaskPadding = (paramContext.getResources().getDimensionPixelSize(2131755609) / 2);
  }
  
  public void update(List<Task> paramList, TaskStackLayoutAlgorithm paramTaskStackLayoutAlgorithm)
  {
    Collections.reverse(paramList);
    this.mTaskRectMap.clear();
    int k = paramTaskStackLayoutAlgorithm.mNumFreeformTasks;
    if (!paramList.isEmpty())
    {
      int m = paramTaskStackLayoutAlgorithm.mFreeformRect.width();
      int n = paramTaskStackLayoutAlgorithm.mFreeformRect.height();
      float f4 = m / n;
      paramTaskStackLayoutAlgorithm = new float[k];
      int i = 0;
      Task localTask;
      if (i < k)
      {
        localTask = (Task)paramList.get(i);
        if (localTask.bounds != null) {}
        for (f1 = localTask.bounds.width() / localTask.bounds.height();; f1 = f4)
        {
          paramTaskStackLayoutAlgorithm[i] = Math.min(f1, f4);
          i += 1;
          break;
        }
      }
      float f3 = 0.85F;
      float f1 = 0.0F;
      float f2 = 0.0F;
      int j = 1;
      i = 0;
      float f5;
      if (i < k)
      {
        f5 = paramTaskStackLayoutAlgorithm[i] * f3;
        if (f1 + f5 > f4) {
          if ((j + 1) * f3 > 1.0F)
          {
            f3 = Math.min(f4 / (f1 + f5), 1.0F / (j + 1));
            j = 1;
            f1 = 0.0F;
            i = 0;
          }
        }
        for (;;)
        {
          f2 = Math.max(f1, f2);
          break;
          f1 = f5;
          j += 1;
          i += 1;
          continue;
          f1 += f5;
          i += 1;
        }
      }
      f2 = (1.0F - f2 / f4) * m / 2.0F;
      f1 = f2;
      f4 = (1.0F - j * f3) * n / 2.0F;
      float f6 = f3 * n;
      i = 0;
      while (i < k)
      {
        localTask = (Task)paramList.get(i);
        float f7 = f6 * paramTaskStackLayoutAlgorithm[i];
        f5 = f1;
        f3 = f4;
        if (f1 + f7 > m)
        {
          f3 = f4 + f6;
          f5 = f2;
        }
        RectF localRectF = new RectF(f5, f3, f5 + f7, f3 + f6);
        localRectF.inset(this.mTaskPadding, this.mTaskPadding);
        f1 = f5 + f7;
        this.mTaskRectMap.put(localTask.key, localRectF);
        i += 1;
        f4 = f3;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\FreeformWorkspaceLayoutAlgorithm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */