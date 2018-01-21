package com.android.systemui.recents.views;

import android.content.Context;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ViewPool<V, T>
{
  Context mContext;
  LinkedList<V> mPool = new LinkedList();
  ViewPoolConsumer<V, T> mViewCreator;
  
  public ViewPool(Context paramContext, ViewPoolConsumer<V, T> paramViewPoolConsumer)
  {
    this.mContext = paramContext;
    this.mViewCreator = paramViewPoolConsumer;
  }
  
  List<V> getViews()
  {
    return this.mPool;
  }
  
  V pickUpViewFromPool(T paramT1, T paramT2)
  {
    Object localObject2 = null;
    boolean bool2 = false;
    boolean bool1;
    if (this.mPool.isEmpty())
    {
      paramT1 = this.mViewCreator.createView(this.mContext);
      bool1 = true;
    }
    for (;;)
    {
      this.mViewCreator.onPickUpViewFromPool(paramT1, paramT2, bool1);
      return paramT1;
      Iterator localIterator = this.mPool.iterator();
      Object localObject1;
      do
      {
        localObject1 = localObject2;
        if (!localIterator.hasNext()) {
          break;
        }
        localObject1 = localIterator.next();
      } while (!this.mViewCreator.hasPreferredData(localObject1, paramT1));
      localIterator.remove();
      bool1 = bool2;
      paramT1 = (T)localObject1;
      if (localObject1 == null)
      {
        paramT1 = this.mPool.pop();
        bool1 = bool2;
      }
    }
  }
  
  void returnViewToPool(V paramV)
  {
    this.mViewCreator.onReturnViewToPool(paramV);
    this.mPool.push(paramV);
  }
  
  public static abstract interface ViewPoolConsumer<V, T>
  {
    public abstract V createView(Context paramContext);
    
    public abstract boolean hasPreferredData(V paramV, T paramT);
    
    public abstract void onPickUpViewFromPool(V paramV, T paramT, boolean paramBoolean);
    
    public abstract void onReturnViewToPool(V paramV);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\ViewPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */