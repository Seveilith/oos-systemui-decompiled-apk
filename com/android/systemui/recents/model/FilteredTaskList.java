package com.android.systemui.recents.model;

import android.util.ArrayMap;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.List;

class FilteredTaskList
{
  TaskFilter mFilter;
  ArrayList<Task> mFilteredTasks = new ArrayList();
  ArrayMap<Task.TaskKey, Integer> mTaskIndices = new ArrayMap();
  ArrayList<Task> mTasks = new ArrayList();
  
  private void updateFilteredTaskIndices()
  {
    int j = this.mFilteredTasks.size();
    this.mTaskIndices.clear();
    int i = 0;
    while (i < j)
    {
      Task localTask = (Task)this.mFilteredTasks.get(i);
      this.mTaskIndices.put(localTask.key, Integer.valueOf(i));
      i += 1;
    }
  }
  
  private void updateFilteredTasks()
  {
    this.mFilteredTasks.clear();
    if (this.mFilter != null)
    {
      SparseArray localSparseArray = new SparseArray();
      int j = this.mTasks.size();
      int i = 0;
      Task localTask;
      while (i < j)
      {
        localTask = (Task)this.mTasks.get(i);
        localSparseArray.put(localTask.key.id, localTask);
        i += 1;
      }
      i = 0;
      while (i < j)
      {
        localTask = (Task)this.mTasks.get(i);
        if (this.mFilter.acceptTask(localSparseArray, localTask, i)) {
          this.mFilteredTasks.add(localTask);
        }
        i += 1;
      }
    }
    this.mFilteredTasks.addAll(this.mTasks);
    updateFilteredTaskIndices();
  }
  
  boolean contains(Task paramTask)
  {
    return this.mTaskIndices.containsKey(paramTask.key);
  }
  
  ArrayList<Task> getTasks()
  {
    return this.mFilteredTasks;
  }
  
  int indexOf(Task paramTask)
  {
    if ((paramTask != null) && (this.mTaskIndices.containsKey(paramTask.key))) {
      return ((Integer)this.mTaskIndices.get(paramTask.key)).intValue();
    }
    return -1;
  }
  
  public void moveTaskToStack(Task paramTask, int paramInt1, int paramInt2)
  {
    int j = indexOf(paramTask);
    if (j != paramInt1)
    {
      this.mTasks.remove(j);
      int i = paramInt1;
      if (j < paramInt1) {
        i = paramInt1 - 1;
      }
      this.mTasks.add(i, paramTask);
    }
    paramTask.setStackId(paramInt2);
    updateFilteredTasks();
  }
  
  boolean remove(Task paramTask)
  {
    if (this.mFilteredTasks.contains(paramTask))
    {
      boolean bool = this.mTasks.remove(paramTask);
      updateFilteredTasks();
      return bool;
    }
    return false;
  }
  
  void set(List<Task> paramList)
  {
    this.mTasks.clear();
    this.mTasks.addAll(paramList);
    updateFilteredTasks();
  }
  
  boolean setFilter(TaskFilter paramTaskFilter)
  {
    ArrayList localArrayList = new ArrayList(this.mFilteredTasks);
    this.mFilter = paramTaskFilter;
    updateFilteredTasks();
    return !localArrayList.equals(this.mFilteredTasks);
  }
  
  int size()
  {
    return this.mFilteredTasks.size();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\model\FilteredTaskList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */