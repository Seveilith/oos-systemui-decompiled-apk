package com.android.systemui.recents.model;

import android.app.ActivityManager.TaskDescription;
import android.app.ActivityManager.TaskThumbnailInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.ViewDebug.ExportedProperty;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.misc.Utilities;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;

public class Task
{
  @ViewDebug.ExportedProperty(category="recents")
  public int affiliationColor;
  @ViewDebug.ExportedProperty(category="recents")
  public int affiliationTaskId;
  @ViewDebug.ExportedProperty(category="recents")
  public String appInfoDescription;
  @ViewDebug.ExportedProperty(category="recents")
  public Rect bounds;
  @ViewDebug.ExportedProperty(category="recents")
  public int colorBackground;
  @ViewDebug.ExportedProperty(category="recents")
  public int colorPrimary;
  @ViewDebug.ExportedProperty(category="recents")
  public String dismissDescription;
  @ViewDebug.ExportedProperty(deepExport=true, prefix="group_")
  public TaskGrouping group;
  public Drawable icon;
  @ViewDebug.ExportedProperty(category="recents")
  public boolean isDockable;
  @ViewDebug.ExportedProperty(category="recents")
  public boolean isLaunchTarget;
  @ViewDebug.ExportedProperty(category="recents")
  public boolean isLocked;
  @ViewDebug.ExportedProperty(category="recents")
  public boolean isStackTask;
  @ViewDebug.ExportedProperty(category="recents")
  public boolean isSystemApp;
  @ViewDebug.ExportedProperty(deepExport=true, prefix="key_")
  public TaskKey key;
  private ArrayList<TaskCallbacks> mCallbacks = new ArrayList();
  @ViewDebug.ExportedProperty(category="recents")
  public int resizeMode;
  public ActivityManager.TaskDescription taskDescription;
  public int temporarySortIndexInStack;
  public Bitmap thumbnail;
  @ViewDebug.ExportedProperty(category="recents")
  public String title;
  @ViewDebug.ExportedProperty(category="recents")
  public String titleDescription;
  @ViewDebug.ExportedProperty(category="recents")
  public ComponentName topActivity;
  @ViewDebug.ExportedProperty(category="recents")
  public boolean useLightOnPrimaryColor;
  
  public Task() {}
  
  public Task(TaskKey paramTaskKey, int paramInt1, int paramInt2, Drawable paramDrawable, Bitmap paramBitmap, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, Rect paramRect, ActivityManager.TaskDescription paramTaskDescription, int paramInt5, ComponentName paramComponentName, boolean paramBoolean5)
  {
    int i;
    if (paramInt1 != paramTaskKey.id)
    {
      i = 1;
      if ((i == 0) || (paramInt2 == 0)) {
        break label189;
      }
      i = 1;
      label38:
      this.key = paramTaskKey;
      this.affiliationTaskId = paramInt1;
      this.affiliationColor = paramInt2;
      this.icon = paramDrawable;
      this.thumbnail = paramBitmap;
      this.title = paramString1;
      this.titleDescription = paramString2;
      this.dismissDescription = paramString3;
      this.appInfoDescription = paramString4;
      if (i == 0) {
        break label195;
      }
      label94:
      this.colorPrimary = paramInt2;
      this.colorBackground = paramInt4;
      if (Utilities.computeContrastBetweenColors(this.colorPrimary, -1) <= 3.0F) {
        break label201;
      }
    }
    label189:
    label195:
    label201:
    for (boolean bool = true;; bool = false)
    {
      this.useLightOnPrimaryColor = bool;
      this.bounds = paramRect;
      this.taskDescription = paramTaskDescription;
      this.isLaunchTarget = paramBoolean1;
      this.isStackTask = paramBoolean2;
      this.isSystemApp = paramBoolean3;
      this.isDockable = paramBoolean4;
      this.resizeMode = paramInt5;
      this.topActivity = paramComponentName;
      this.isLocked = paramBoolean5;
      return;
      i = 0;
      break;
      i = 0;
      break label38;
      paramInt2 = paramInt3;
      break label94;
    }
  }
  
  public void addCallback(TaskCallbacks paramTaskCallbacks)
  {
    if (!this.mCallbacks.contains(paramTaskCallbacks)) {
      this.mCallbacks.add(paramTaskCallbacks);
    }
  }
  
  public void copyFrom(Task paramTask)
  {
    this.key = paramTask.key;
    this.group = paramTask.group;
    this.affiliationTaskId = paramTask.affiliationTaskId;
    this.affiliationColor = paramTask.affiliationColor;
    this.icon = paramTask.icon;
    this.thumbnail = paramTask.thumbnail;
    this.title = paramTask.title;
    this.titleDescription = paramTask.titleDescription;
    this.dismissDescription = paramTask.dismissDescription;
    this.appInfoDescription = paramTask.appInfoDescription;
    this.colorPrimary = paramTask.colorPrimary;
    this.colorBackground = paramTask.colorBackground;
    this.useLightOnPrimaryColor = paramTask.useLightOnPrimaryColor;
    this.bounds = paramTask.bounds;
    this.taskDescription = paramTask.taskDescription;
    this.isLaunchTarget = paramTask.isLaunchTarget;
    this.isStackTask = paramTask.isStackTask;
    this.isSystemApp = paramTask.isSystemApp;
    this.isDockable = paramTask.isDockable;
    this.resizeMode = paramTask.resizeMode;
    this.topActivity = paramTask.topActivity;
    this.isLocked = paramTask.isLocked;
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print(this.key);
    if (isAffiliatedTask())
    {
      paramPrintWriter.print(" ");
      paramPrintWriter.print("affTaskId=" + this.affiliationTaskId);
    }
    if (!this.isDockable) {
      paramPrintWriter.print(" dockable=N");
    }
    if (this.isLaunchTarget) {
      paramPrintWriter.print(" launchTarget=Y");
    }
    if (isFreeformTask()) {
      paramPrintWriter.print(" freeform=Y");
    }
    paramPrintWriter.print(" ");
    paramPrintWriter.print(this.title);
    paramPrintWriter.println();
  }
  
  public boolean equals(Object paramObject)
  {
    paramObject = (Task)paramObject;
    return this.key.equals(((Task)paramObject).key);
  }
  
  public ComponentName getTopComponent()
  {
    if (this.topActivity != null) {
      return this.topActivity;
    }
    return this.key.baseIntent.getComponent();
  }
  
  public boolean isAffiliatedTask()
  {
    return this.key.id != this.affiliationTaskId;
  }
  
  public boolean isFreeformTask()
  {
    if (Recents.getSystemServices().hasFreeformWorkspaceSupport()) {
      return SystemServicesProxy.isFreeformStack(this.key.stackId);
    }
    return false;
  }
  
  public void notifyTaskDataLoaded(Bitmap paramBitmap, Drawable paramDrawable, ActivityManager.TaskThumbnailInfo paramTaskThumbnailInfo)
  {
    this.icon = paramDrawable;
    this.thumbnail = paramBitmap;
    int j = this.mCallbacks.size();
    int i = 0;
    while (i < j)
    {
      ((TaskCallbacks)this.mCallbacks.get(i)).onTaskDataLoaded(this, paramTaskThumbnailInfo);
      i += 1;
    }
  }
  
  public void notifyTaskDataUnloaded(Bitmap paramBitmap, Drawable paramDrawable)
  {
    this.icon = paramDrawable;
    this.thumbnail = paramBitmap;
    int i = this.mCallbacks.size() - 1;
    while (i >= 0)
    {
      ((TaskCallbacks)this.mCallbacks.get(i)).onTaskDataUnloaded();
      i -= 1;
    }
  }
  
  public void removeCallback(TaskCallbacks paramTaskCallbacks)
  {
    this.mCallbacks.remove(paramTaskCallbacks);
  }
  
  public void setGroup(TaskGrouping paramTaskGrouping)
  {
    this.group = paramTaskGrouping;
  }
  
  public void setStackId(int paramInt)
  {
    this.key.setStackId(paramInt);
    int i = this.mCallbacks.size();
    paramInt = 0;
    while (paramInt < i)
    {
      ((TaskCallbacks)this.mCallbacks.get(paramInt)).onTaskStackIdChanged();
      paramInt += 1;
    }
  }
  
  public String toString()
  {
    return "[" + this.key.toString() + "] " + this.title;
  }
  
  public static abstract interface TaskCallbacks
  {
    public abstract void onTaskDataLoaded(Task paramTask, ActivityManager.TaskThumbnailInfo paramTaskThumbnailInfo);
    
    public abstract void onTaskDataUnloaded();
    
    public abstract void onTaskStackIdChanged();
  }
  
  public static class TaskKey
  {
    @ViewDebug.ExportedProperty(category="recents")
    public final Intent baseIntent;
    @ViewDebug.ExportedProperty(category="recents")
    public long firstActiveTime;
    @ViewDebug.ExportedProperty(category="recents")
    public final int id;
    public boolean isTopAppLocked;
    @ViewDebug.ExportedProperty(category="recents")
    public long lastActiveTime;
    private int mHashCode;
    @ViewDebug.ExportedProperty(category="recents")
    public int stackId;
    @ViewDebug.ExportedProperty(category="recents")
    public final int userId;
    
    public TaskKey(int paramInt1, int paramInt2, Intent paramIntent, int paramInt3, long paramLong1, long paramLong2, boolean paramBoolean)
    {
      this.id = paramInt1;
      this.stackId = paramInt2;
      this.baseIntent = paramIntent;
      this.userId = paramInt3;
      this.firstActiveTime = paramLong1;
      this.lastActiveTime = paramLong2;
      updateHashCode();
      this.isTopAppLocked = paramBoolean;
    }
    
    private void updateHashCode()
    {
      this.mHashCode = Objects.hash(new Object[] { Integer.valueOf(this.id), Integer.valueOf(this.stackId), Integer.valueOf(this.userId) });
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if (!(paramObject instanceof TaskKey)) {
        return false;
      }
      paramObject = (TaskKey)paramObject;
      boolean bool1 = bool2;
      if (this.id == ((TaskKey)paramObject).id)
      {
        bool1 = bool2;
        if (this.stackId == ((TaskKey)paramObject).stackId)
        {
          bool1 = bool2;
          if (this.userId == ((TaskKey)paramObject).userId) {
            bool1 = true;
          }
        }
      }
      return bool1;
    }
    
    public ComponentName getComponent()
    {
      return this.baseIntent.getComponent();
    }
    
    public int hashCode()
    {
      return this.mHashCode;
    }
    
    public void setStackId(int paramInt)
    {
      this.stackId = paramInt;
      updateHashCode();
    }
    
    public String toString()
    {
      return "id=" + this.id + " stackId=" + this.stackId + " user=" + this.userId + " lastActiveTime=" + this.lastActiveTime;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\model\Task.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */