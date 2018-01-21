package android.support.v4.util;

public class Pools$SimplePool<T>
  implements Pools.Pool<T>
{
  private final Object[] mPool;
  private int mPoolSize;
  
  public Pools$SimplePool(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("The max pool size must be > 0");
    }
    this.mPool = new Object[paramInt];
  }
  
  private boolean isInPool(T paramT)
  {
    int i = 0;
    while (i < this.mPoolSize)
    {
      if (this.mPool[i] == paramT) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public T acquire()
  {
    if (this.mPoolSize > 0)
    {
      int i = this.mPoolSize - 1;
      Object localObject = this.mPool[i];
      this.mPool[i] = null;
      this.mPoolSize -= 1;
      return (T)localObject;
    }
    return null;
  }
  
  public boolean release(T paramT)
  {
    if (isInPool(paramT)) {
      throw new IllegalStateException("Already in the pool!");
    }
    if (this.mPoolSize < this.mPool.length)
    {
      this.mPool[this.mPoolSize] = paramT;
      this.mPoolSize += 1;
      return true;
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\util\Pools$SimplePool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */