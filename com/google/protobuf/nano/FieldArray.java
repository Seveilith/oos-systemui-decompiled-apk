package com.google.protobuf.nano;

public final class FieldArray
  implements Cloneable
{
  private static final FieldData DELETED = new FieldData();
  private FieldData[] mData;
  private int[] mFieldNumbers;
  private boolean mGarbage = false;
  private int mSize;
  
  FieldArray()
  {
    this(10);
  }
  
  FieldArray(int paramInt)
  {
    paramInt = idealIntArraySize(paramInt);
    this.mFieldNumbers = new int[paramInt];
    this.mData = new FieldData[paramInt];
    this.mSize = 0;
  }
  
  private boolean arrayEquals(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    int i = 0;
    while (i < paramInt)
    {
      if (paramArrayOfInt1[i] != paramArrayOfInt2[i]) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private boolean arrayEquals(FieldData[] paramArrayOfFieldData1, FieldData[] paramArrayOfFieldData2, int paramInt)
  {
    int i = 0;
    while (i < paramInt)
    {
      if (!paramArrayOfFieldData1[i].equals(paramArrayOfFieldData2[i])) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private void gc()
  {
    int m = this.mSize;
    int j = 0;
    int[] arrayOfInt = this.mFieldNumbers;
    FieldData[] arrayOfFieldData = this.mData;
    int i = 0;
    while (i < m)
    {
      FieldData localFieldData = arrayOfFieldData[i];
      int k = j;
      if (localFieldData != DELETED)
      {
        if (i != j)
        {
          arrayOfInt[j] = arrayOfInt[i];
          arrayOfFieldData[j] = localFieldData;
          arrayOfFieldData[i] = null;
        }
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    this.mGarbage = false;
    this.mSize = j;
  }
  
  private int idealByteArraySize(int paramInt)
  {
    int i = 4;
    while (i < 32)
    {
      if (paramInt <= (1 << i) - 12) {
        return (1 << i) - 12;
      }
      i += 1;
    }
    return paramInt;
  }
  
  private int idealIntArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 4) / 4;
  }
  
  public final FieldArray clone()
  {
    int j = size();
    FieldArray localFieldArray = new FieldArray(j);
    System.arraycopy(this.mFieldNumbers, 0, localFieldArray.mFieldNumbers, 0, j);
    int i = 0;
    while (i < j)
    {
      if (this.mData[i] != null) {
        localFieldArray.mData[i] = this.mData[i].clone();
      }
      i += 1;
    }
    localFieldArray.mSize = j;
    return localFieldArray;
  }
  
  FieldData dataAt(int paramInt)
  {
    if (this.mGarbage) {
      gc();
    }
    return this.mData[paramInt];
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof FieldArray)) {
      return false;
    }
    paramObject = (FieldArray)paramObject;
    if (size() != ((FieldArray)paramObject).size()) {
      return false;
    }
    if (arrayEquals(this.mFieldNumbers, ((FieldArray)paramObject).mFieldNumbers, this.mSize)) {
      bool = arrayEquals(this.mData, ((FieldArray)paramObject).mData, this.mSize);
    }
    return bool;
  }
  
  public int hashCode()
  {
    if (this.mGarbage) {
      gc();
    }
    int j = 17;
    int i = 0;
    while (i < this.mSize)
    {
      j = (j * 31 + this.mFieldNumbers[i]) * 31 + this.mData[i].hashCode();
      i += 1;
    }
    return j;
  }
  
  int size()
  {
    if (this.mGarbage) {
      gc();
    }
    return this.mSize;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\google\protobuf\nano\FieldArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */