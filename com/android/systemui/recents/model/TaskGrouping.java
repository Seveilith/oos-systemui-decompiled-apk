package com.android.systemui.recents.model;

import android.util.ArrayMap;
import java.util.ArrayList;

public class TaskGrouping
{
  int affiliation;
  long latestActiveTimeInGroup;
  Task.TaskKey mFrontMostTaskKey;
  ArrayMap<Task.TaskKey, Integer> mTaskKeyIndices = new ArrayMap();
  ArrayList<Task.TaskKey> mTaskKeys = new ArrayList();
  
  public TaskGrouping(int paramInt)
  {
    this.affiliation = paramInt;
  }
  
  private void updateTaskIndices()
  {
    if (this.mTaskKeys.isEmpty())
    {
      this.mFrontMostTaskKey = null;
      this.mTaskKeyIndices.clear();
      return;
    }
    int j = this.mTaskKeys.size();
    this.mFrontMostTaskKey = ((Task.TaskKey)this.mTaskKeys.get(this.mTaskKeys.size() - 1));
    this.mTaskKeyIndices.clear();
    int i = 0;
    while (i < j)
    {
      Task.TaskKey localTaskKey = (Task.TaskKey)this.mTaskKeys.get(i);
      this.mTaskKeyIndices.put(localTaskKey, Integer.valueOf(i));
      i += 1;
    }
  }
  
  void addTask(Task paramTask)
  {
    this.mTaskKeys.add(paramTask.key);
    if (paramTask.key.lastActiveTime > this.latestActiveTimeInGroup) {
      this.latestActiveTimeInGroup = paramTask.key.lastActiveTime;
    }
    paramTask.setGroup(this);
    updateTaskIndices();
  }
  
  public Task.TaskKey getNextTaskInGroup(Task paramTask)
  {
    int i = indexOf(paramTask);
    if (i + 1 < getTaskCount()) {
      return (Task.TaskKey)this.mTaskKeys.get(i + 1);
    }
    return null;
  }
  
  public Task.TaskKey getPrevTaskInGroup(Task paramTask)
  {
    int i = indexOf(paramTask);
    if (i - 1 >= 0) {
      return (Task.TaskKey)this.mTaskKeys.get(i - 1);
    }
    return null;
  }
  
  public int getTaskCount()
  {
    return this.mTaskKeys.size();
  }
  
  public int indexOf(Task paramTask)
  {
    return ((Integer)this.mTaskKeyIndices.get(paramTask.key)).intValue();
  }
  
  public boolean isFrontMostTask(Task paramTask)
  {
    return paramTask.key == this.mFrontMostTaskKey;
  }
  
  public boolean isTaskAboveTask(Task paramTask1, Task paramTask2)
  {
    if ((this.mTaskKeyIndices.containsKey(paramTask1.key)) && (this.mTaskKeyIndices.containsKey(paramTask2.key))) {
      return ((Integer)this.mTaskKeyIndices.get(paramTask1.key)).intValue() > ((Integer)this.mTaskKeyIndices.get(paramTask2.key)).intValue();
    }
    return false;
  }
  
  void removeTask(Task paramTask)
  {
    this.mTaskKeys.remove(paramTask.key);
    this.latestActiveTimeInGroup = 0L;
    int j = this.mTaskKeys.size();
    int i = 0;
    while (i < j)
    {
      long l = ((Task.TaskKey)this.mTaskKeys.get(i)).lastActiveTime;
      if (l > this.latestActiveTimeInGroup) {
        this.latestActiveTimeInGroup = l;
      }
      i += 1;
    }
    paramTask.setGroup(null);
    updateTaskIndices();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\model\TaskGrouping.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */