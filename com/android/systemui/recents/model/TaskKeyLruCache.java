package com.android.systemui.recents.model;

import android.util.Log;
import android.util.LruCache;
import android.util.SparseArray;
import java.io.PrintWriter;

public class TaskKeyLruCache<V>
{
  private final LruCache<Integer, V> mCache;
  private final EvictionCallback mEvictionCallback;
  private final SparseArray<Task.TaskKey> mKeys = new SparseArray();
  
  public TaskKeyLruCache(int paramInt)
  {
    this(paramInt, null);
  }
  
  public TaskKeyLruCache(int paramInt, EvictionCallback paramEvictionCallback)
  {
    this.mEvictionCallback = paramEvictionCallback;
    this.mCache = new LruCache(paramInt)
    {
      protected void entryRemoved(boolean paramAnonymousBoolean, Integer paramAnonymousInteger, V paramAnonymousV1, V paramAnonymousV2)
      {
        if (TaskKeyLruCache.-get0(TaskKeyLruCache.this) != null) {
          TaskKeyLruCache.-get0(TaskKeyLruCache.this).onEntryEvicted((Task.TaskKey)TaskKeyLruCache.-get1(TaskKeyLruCache.this).get(paramAnonymousInteger.intValue()));
        }
        TaskKeyLruCache.-get1(TaskKeyLruCache.this).remove(paramAnonymousInteger.intValue());
      }
    };
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    String str = paramString + "  ";
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("TaskKeyLruCache");
    paramPrintWriter.print(" numEntries=");
    paramPrintWriter.print(this.mKeys.size());
    paramPrintWriter.println();
    int j = this.mKeys.size();
    int i = 0;
    while (i < j)
    {
      paramPrintWriter.print(str);
      paramPrintWriter.println(this.mKeys.get(this.mKeys.keyAt(i)));
      i += 1;
    }
  }
  
  final void evictAll()
  {
    this.mCache.evictAll();
    this.mKeys.clear();
  }
  
  final V get(Task.TaskKey paramTaskKey)
  {
    return (V)this.mCache.get(Integer.valueOf(paramTaskKey.id));
  }
  
  final V getAndInvalidateIfModified(Task.TaskKey paramTaskKey)
  {
    try
    {
      Task.TaskKey localTaskKey = (Task.TaskKey)this.mKeys.get(paramTaskKey.id);
      if ((localTaskKey != null) && ((localTaskKey.stackId != paramTaskKey.stackId) || (localTaskKey.lastActiveTime != paramTaskKey.lastActiveTime)))
      {
        remove(paramTaskKey);
        return null;
      }
    }
    catch (ArrayIndexOutOfBoundsException paramTaskKey)
    {
      Log.e("TaskKeyLruCache", "getAndInvalidateIfModified, " + paramTaskKey);
      return null;
    }
    return (V)this.mCache.get(Integer.valueOf(paramTaskKey.id));
  }
  
  final void put(Task.TaskKey paramTaskKey, V paramV)
  {
    if ((paramTaskKey == null) || (paramV == null))
    {
      Log.e("TaskKeyLruCache", "Unexpected null key or value: " + paramTaskKey + ", " + paramV);
      return;
    }
    try
    {
      this.mKeys.put(paramTaskKey.id, paramTaskKey);
      this.mCache.put(Integer.valueOf(paramTaskKey.id), paramV);
      return;
    }
    catch (Exception paramTaskKey)
    {
      Log.e("TaskKeyLruCache", "Unexpected exception when put");
      paramTaskKey.printStackTrace();
    }
  }
  
  final void remove(Task.TaskKey paramTaskKey)
  {
    this.mCache.remove(Integer.valueOf(paramTaskKey.id));
    this.mKeys.remove(paramTaskKey.id);
  }
  
  final void trimToSize(int paramInt)
  {
    this.mCache.trimToSize(paramInt);
  }
  
  public static abstract interface EvictionCallback
  {
    public abstract void onEntryEvicted(Task.TaskKey paramTaskKey);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\model\TaskKeyLruCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */