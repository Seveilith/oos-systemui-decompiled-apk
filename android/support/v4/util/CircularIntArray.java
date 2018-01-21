package android.support.v4.util;

public final class CircularIntArray
{
  private int mCapacityBitmask;
  private int[] mElements;
  private int mHead;
  private int mTail;
  
  public CircularIntArray()
  {
    this(8);
  }
  
  public CircularIntArray(int paramInt)
  {
    if (paramInt < 1) {
      throw new IllegalArgumentException("capacity must be >= 1");
    }
    if (paramInt > 1073741824) {
      throw new IllegalArgumentException("capacity must be <= 2^30");
    }
    if (Integer.bitCount(paramInt) != 1) {
      paramInt = Integer.highestOneBit(paramInt - 1) << 1;
    }
    for (;;)
    {
      this.mCapacityBitmask = (paramInt - 1);
      this.mElements = new int[paramInt];
      return;
    }
  }
  
  private void doubleCapacity()
  {
    int i = this.mElements.length;
    int j = i - this.mHead;
    int k = i << 1;
    if (k < 0) {
      throw new RuntimeException("Max array capacity exceeded");
    }
    int[] arrayOfInt = new int[k];
    System.arraycopy(this.mElements, this.mHead, arrayOfInt, 0, j);
    System.arraycopy(this.mElements, 0, arrayOfInt, j, this.mHead);
    this.mElements = arrayOfInt;
    this.mHead = 0;
    this.mTail = i;
    this.mCapacityBitmask = (k - 1);
  }
  
  public void addLast(int paramInt)
  {
    this.mElements[this.mTail] = paramInt;
    this.mTail = (this.mTail + 1 & this.mCapacityBitmask);
    if (this.mTail == this.mHead) {
      doubleCapacity();
    }
  }
  
  public void clear()
  {
    this.mTail = this.mHead;
  }
  
  public int get(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= size())) {
      throw new ArrayIndexOutOfBoundsException();
    }
    return this.mElements[(this.mHead + paramInt & this.mCapacityBitmask)];
  }
  
  public int getLast()
  {
    if (this.mHead == this.mTail) {
      throw new ArrayIndexOutOfBoundsException();
    }
    return this.mElements[(this.mTail - 1 & this.mCapacityBitmask)];
  }
  
  public int popLast()
  {
    if (this.mHead == this.mTail) {
      throw new ArrayIndexOutOfBoundsException();
    }
    int i = this.mTail - 1 & this.mCapacityBitmask;
    int j = this.mElements[i];
    this.mTail = i;
    return j;
  }
  
  public int size()
  {
    return this.mTail - this.mHead & this.mCapacityBitmask;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\util\CircularIntArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */