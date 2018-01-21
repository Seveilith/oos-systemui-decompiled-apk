package com.android.framework.protobuf.nano;

import java.io.IOException;
import java.lang.reflect.Array;

public class Extension<M extends ExtendableMessageNano<M>, T>
{
  protected final Class<T> clazz;
  protected final boolean repeated;
  public final int tag;
  protected final int type;
  
  protected int computeRepeatedSerializedSize(Object paramObject)
  {
    int j = 0;
    int m = Array.getLength(paramObject);
    int i = 0;
    while (i < m)
    {
      int k = j;
      if (Array.get(paramObject, i) != null) {
        k = j + computeSingularSerializedSize(Array.get(paramObject, i));
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  int computeSerializedSize(Object paramObject)
  {
    if (this.repeated) {
      return computeRepeatedSerializedSize(paramObject);
    }
    return computeSingularSerializedSize(paramObject);
  }
  
  protected int computeSingularSerializedSize(Object paramObject)
  {
    int i = WireFormatNano.getTagFieldNumber(this.tag);
    switch (this.type)
    {
    default: 
      throw new IllegalArgumentException("Unknown type " + this.type);
    case 10: 
      return CodedOutputByteBufferNano.computeGroupSize(i, (MessageNano)paramObject);
    }
    return CodedOutputByteBufferNano.computeMessageSize(i, (MessageNano)paramObject);
  }
  
  protected void writeRepeatedData(Object paramObject, CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
  {
    int j = Array.getLength(paramObject);
    int i = 0;
    while (i < j)
    {
      Object localObject = Array.get(paramObject, i);
      if (localObject != null) {
        writeSingularData(localObject, paramCodedOutputByteBufferNano);
      }
      i += 1;
    }
  }
  
  protected void writeSingularData(Object paramObject, CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
  {
    for (;;)
    {
      try
      {
        paramCodedOutputByteBufferNano.writeRawVarint32(this.tag);
        switch (this.type)
        {
        case 10: 
          throw new IllegalArgumentException("Unknown type " + this.type);
        }
      }
      catch (IOException paramObject)
      {
        throw new IllegalStateException((Throwable)paramObject);
      }
      paramObject = (MessageNano)paramObject;
      int i = WireFormatNano.getTagFieldNumber(this.tag);
      paramCodedOutputByteBufferNano.writeGroupNoTag((MessageNano)paramObject);
      paramCodedOutputByteBufferNano.writeTag(i, 4);
      return;
      paramCodedOutputByteBufferNano.writeMessageNoTag((MessageNano)paramObject);
      return;
    }
  }
  
  void writeTo(Object paramObject, CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
    throws IOException
  {
    if (this.repeated)
    {
      writeRepeatedData(paramObject, paramCodedOutputByteBufferNano);
      return;
    }
    writeSingularData(paramObject, paramCodedOutputByteBufferNano);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\framework\protobuf\nano\Extension.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */