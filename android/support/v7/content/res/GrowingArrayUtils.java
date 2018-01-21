package android.support.v7.content.res;

import java.lang.reflect.Array;

final class GrowingArrayUtils
{
  static
  {
    if (GrowingArrayUtils.class.desiredAssertionStatus()) {}
    for (boolean bool = false;; bool = true)
    {
      -assertionsDisabled = bool;
      return;
    }
  }
  
  public static int[] append(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    if (!-assertionsDisabled)
    {
      if (paramInt1 <= paramArrayOfInt.length) {}
      for (int i = 1; i == 0; i = 0) {
        throw new AssertionError();
      }
    }
    int[] arrayOfInt = paramArrayOfInt;
    if (paramInt1 + 1 > paramArrayOfInt.length)
    {
      arrayOfInt = new int[growSize(paramInt1)];
      System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, paramInt1);
    }
    arrayOfInt[paramInt1] = paramInt2;
    return arrayOfInt;
  }
  
  public static <T> T[] append(T[] paramArrayOfT, int paramInt, T paramT)
  {
    if (!-assertionsDisabled)
    {
      if (paramInt <= paramArrayOfT.length) {}
      for (int i = 1; i == 0; i = 0) {
        throw new AssertionError();
      }
    }
    Object localObject = paramArrayOfT;
    if (paramInt + 1 > paramArrayOfT.length)
    {
      localObject = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), growSize(paramInt));
      System.arraycopy(paramArrayOfT, 0, localObject, 0, paramInt);
    }
    localObject[paramInt] = paramT;
    return (T[])localObject;
  }
  
  public static int growSize(int paramInt)
  {
    if (paramInt <= 4) {
      return 8;
    }
    return paramInt * 2;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\content\res\GrowingArrayUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */