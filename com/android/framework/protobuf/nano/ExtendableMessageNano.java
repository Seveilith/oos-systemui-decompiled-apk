package com.android.framework.protobuf.nano;

import java.io.IOException;

public abstract class ExtendableMessageNano<M extends ExtendableMessageNano<M>>
  extends MessageNano
{
  protected FieldArray unknownFieldData;
  
  public M clone()
    throws CloneNotSupportedException
  {
    ExtendableMessageNano localExtendableMessageNano = (ExtendableMessageNano)super.clone();
    InternalNano.cloneUnknownFieldData(this, localExtendableMessageNano);
    return localExtendableMessageNano;
  }
  
  protected int computeSerializedSize()
  {
    int k = 0;
    int i = 0;
    if (this.unknownFieldData != null)
    {
      int j = 0;
      for (;;)
      {
        k = i;
        if (j >= this.unknownFieldData.size()) {
          break;
        }
        i += this.unknownFieldData.dataAt(j).computeSerializedSize();
        j += 1;
      }
    }
    return k;
  }
  
  public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
    throws IOException
  {
    if (this.unknownFieldData == null) {
      return;
    }
    int i = 0;
    while (i < this.unknownFieldData.size())
    {
      this.unknownFieldData.dataAt(i).writeTo(paramCodedOutputByteBufferNano);
      i += 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\framework\protobuf\nano\ExtendableMessageNano.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */