package com.android.systemui.recents.model;

import java.util.concurrent.ConcurrentLinkedQueue;

class TaskResourceLoadQueue
{
  ConcurrentLinkedQueue<Task> mQueue = new ConcurrentLinkedQueue();
  
  void addTask(Task paramTask)
  {
    if (!this.mQueue.contains(paramTask)) {
      this.mQueue.add(paramTask);
    }
    try
    {
      notifyAll();
      return;
    }
    finally
    {
      paramTask = finally;
      throw paramTask;
    }
  }
  
  void clearTasks()
  {
    this.mQueue.clear();
  }
  
  boolean isEmpty()
  {
    return this.mQueue.isEmpty();
  }
  
  Task nextTask()
  {
    return (Task)this.mQueue.poll();
  }
  
  void removeTask(Task paramTask)
  {
    this.mQueue.remove(paramTask);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\model\TaskResourceLoadQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */