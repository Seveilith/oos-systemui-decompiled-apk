package com.android.systemui.recents.model;

import android.util.SparseArray;

abstract interface TaskFilter
{
  public abstract boolean acceptTask(SparseArray<Task> paramSparseArray, Task paramTask, int paramInt);
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\model\TaskFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */