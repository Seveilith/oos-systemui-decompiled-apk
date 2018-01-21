package com.google.protobuf.nano;

import java.io.IOException;

public abstract class MessageNano
{
  protected volatile int cachedSize = -1;
  
  public static final <T extends MessageNano> T mergeFrom(T paramT, byte[] paramArrayOfByte)
    throws InvalidProtocolBufferNanoException
  {
    return mergeFrom(paramT, paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static final <T extends MessageNano> T mergeFrom(T paramT, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws InvalidProtocolBufferNanoException
  {
    try
    {
      paramArrayOfByte = CodedInputByteBufferNano.newInstance(paramArrayOfByte, paramInt1, paramInt2);
      paramT.mergeFrom(paramArrayOfByte);
      paramArrayOfByte.checkLastTagWas(0);
      return paramT;
    }
    catch (IOException paramT)
    {
      throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).");
    }
    catch (InvalidProtocolBufferNanoException paramT)
    {
      throw paramT;
    }
  }
  
  public static final void toByteArray(MessageNano paramMessageNano, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      paramArrayOfByte = CodedOutputByteBufferNano.newInstance(paramArrayOfByte, paramInt1, paramInt2);
      paramMessageNano.writeTo(paramArrayOfByte);
      paramArrayOfByte.checkNoSpaceLeft();
      return;
    }
    catch (IOException paramMessageNano)
    {
      throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", paramMessageNano);
    }
  }
  
  public static final byte[] toByteArray(MessageNano paramMessageNano)
  {
    byte[] arrayOfByte = new byte[paramMessageNano.getSerializedSize()];
    toByteArray(paramMessageNano, arrayOfByte, 0, arrayOfByte.length);
    return arrayOfByte;
  }
  
  public MessageNano clone()
    throws CloneNotSupportedException
  {
    return (MessageNano)super.clone();
  }
  
  protected int computeSerializedSize()
  {
    return 0;
  }
  
  public int getCachedSize()
  {
    if (this.cachedSize < 0) {
      getSerializedSize();
    }
    return this.cachedSize;
  }
  
  public int getSerializedSize()
  {
    int i = computeSerializedSize();
    this.cachedSize = i;
    return i;
  }
  
  public abstract MessageNano mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
    throws IOException;
  
  public String toString()
  {
    return MessageNanoPrinter.print(this);
  }
  
  public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
    throws IOException
  {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\google\protobuf\nano\MessageNano.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */