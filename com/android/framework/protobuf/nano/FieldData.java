package com.android.framework.protobuf.nano;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class FieldData
  implements Cloneable
{
  private Extension<?, ?> cachedExtension;
  private List<UnknownFieldData> unknownFieldData = new ArrayList();
  private Object value;
  
  private byte[] toByteArray()
    throws IOException
  {
    byte[] arrayOfByte = new byte[computeSerializedSize()];
    writeTo(CodedOutputByteBufferNano.newInstance(arrayOfByte));
    return arrayOfByte;
  }
  
  public final FieldData clone()
  {
    FieldData localFieldData = new FieldData();
    try
    {
      localFieldData.cachedExtension = this.cachedExtension;
      if (this.unknownFieldData == null) {
        localFieldData.unknownFieldData = null;
      }
      while (this.value == null)
      {
        return localFieldData;
        localFieldData.unknownFieldData.addAll(this.unknownFieldData);
      }
      if (!(this.value instanceof MessageNano)) {
        break label90;
      }
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError(localCloneNotSupportedException);
    }
    localCloneNotSupportedException.value = ((MessageNano)this.value).clone();
    return localCloneNotSupportedException;
    label90:
    if ((this.value instanceof byte[]))
    {
      localCloneNotSupportedException.value = ((byte[])this.value).clone();
      return localCloneNotSupportedException;
    }
    Object localObject1;
    Object localObject2;
    int i;
    if ((this.value instanceof byte[][]))
    {
      localObject1 = (byte[][])this.value;
      localObject2 = new byte[localObject1.length][];
      localCloneNotSupportedException.value = localObject2;
      i = 0;
      while (i < localObject1.length)
      {
        localObject2[i] = ((byte[])localObject1[i].clone());
        i += 1;
      }
    }
    if ((this.value instanceof boolean[]))
    {
      localCloneNotSupportedException.value = ((boolean[])this.value).clone();
      return localCloneNotSupportedException;
    }
    if ((this.value instanceof int[]))
    {
      localCloneNotSupportedException.value = ((int[])this.value).clone();
      return localCloneNotSupportedException;
    }
    if ((this.value instanceof long[]))
    {
      localCloneNotSupportedException.value = ((long[])this.value).clone();
      return localCloneNotSupportedException;
    }
    if ((this.value instanceof float[]))
    {
      localCloneNotSupportedException.value = ((float[])this.value).clone();
      return localCloneNotSupportedException;
    }
    if ((this.value instanceof double[]))
    {
      localCloneNotSupportedException.value = ((double[])this.value).clone();
      return localCloneNotSupportedException;
    }
    if ((this.value instanceof MessageNano[]))
    {
      localObject1 = (MessageNano[])this.value;
      localObject2 = new MessageNano[localObject1.length];
      localCloneNotSupportedException.value = localObject2;
      i = 0;
      while (i < localObject1.length)
      {
        localObject2[i] = localObject1[i].clone();
        i += 1;
      }
    }
    return localCloneNotSupportedException;
  }
  
  int computeSerializedSize()
  {
    int i = 0;
    int j;
    if (this.value != null)
    {
      j = this.cachedExtension.computeSerializedSize(this.value);
      return j;
    }
    Iterator localIterator = this.unknownFieldData.iterator();
    for (;;)
    {
      j = i;
      if (!localIterator.hasNext()) {
        break;
      }
      i += ((UnknownFieldData)localIterator.next()).computeSerializedSize();
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof FieldData)) {
      return false;
    }
    paramObject = (FieldData)paramObject;
    if ((this.value != null) && (((FieldData)paramObject).value != null))
    {
      if (this.cachedExtension != ((FieldData)paramObject).cachedExtension) {
        return false;
      }
      if (!this.cachedExtension.clazz.isArray()) {
        return this.value.equals(((FieldData)paramObject).value);
      }
      if ((this.value instanceof byte[])) {
        return Arrays.equals((byte[])this.value, (byte[])((FieldData)paramObject).value);
      }
      if ((this.value instanceof int[])) {
        return Arrays.equals((int[])this.value, (int[])((FieldData)paramObject).value);
      }
      if ((this.value instanceof long[])) {
        return Arrays.equals((long[])this.value, (long[])((FieldData)paramObject).value);
      }
      if ((this.value instanceof float[])) {
        return Arrays.equals((float[])this.value, (float[])((FieldData)paramObject).value);
      }
      if ((this.value instanceof double[])) {
        return Arrays.equals((double[])this.value, (double[])((FieldData)paramObject).value);
      }
      if ((this.value instanceof boolean[])) {
        return Arrays.equals((boolean[])this.value, (boolean[])((FieldData)paramObject).value);
      }
      return Arrays.deepEquals((Object[])this.value, (Object[])((FieldData)paramObject).value);
    }
    if ((this.unknownFieldData != null) && (((FieldData)paramObject).unknownFieldData != null)) {
      return this.unknownFieldData.equals(((FieldData)paramObject).unknownFieldData);
    }
    try
    {
      boolean bool = Arrays.equals(toByteArray(), ((FieldData)paramObject).toByteArray());
      return bool;
    }
    catch (IOException paramObject)
    {
      throw new IllegalStateException((Throwable)paramObject);
    }
  }
  
  public int hashCode()
  {
    try
    {
      int i = Arrays.hashCode(toByteArray());
      return i + 527;
    }
    catch (IOException localIOException)
    {
      throw new IllegalStateException(localIOException);
    }
  }
  
  void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
    throws IOException
  {
    if (this.value != null) {
      this.cachedExtension.writeTo(this.value, paramCodedOutputByteBufferNano);
    }
    for (;;)
    {
      return;
      Iterator localIterator = this.unknownFieldData.iterator();
      while (localIterator.hasNext()) {
        ((UnknownFieldData)localIterator.next()).writeTo(paramCodedOutputByteBufferNano);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\framework\protobuf\nano\FieldData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */