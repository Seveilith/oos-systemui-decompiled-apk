package com.google.protobuf.nano;

import java.io.IOException;
import java.util.Arrays;

final class UnknownFieldData
{
  final byte[] bytes;
  final int tag;
  
  int computeSerializedSize()
  {
    return CodedOutputByteBufferNano.computeRawVarint32Size(this.tag) + 0 + this.bytes.length;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof UnknownFieldData)) {
      return false;
    }
    paramObject = (UnknownFieldData)paramObject;
    if (this.tag == ((UnknownFieldData)paramObject).tag) {
      bool = Arrays.equals(this.bytes, ((UnknownFieldData)paramObject).bytes);
    }
    return bool;
  }
  
  public int hashCode()
  {
    return (this.tag + 527) * 31 + Arrays.hashCode(this.bytes);
  }
  
  void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
    throws IOException
  {
    paramCodedOutputByteBufferNano.writeRawVarint32(this.tag);
    paramCodedOutputByteBufferNano.writeRawBytes(this.bytes);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\google\protobuf\nano\UnknownFieldData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */