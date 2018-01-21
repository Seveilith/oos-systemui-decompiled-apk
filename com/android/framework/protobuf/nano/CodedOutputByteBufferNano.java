package com.android.framework.protobuf.nano;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class CodedOutputByteBufferNano
{
  private final ByteBuffer buffer;
  
  private CodedOutputByteBufferNano(ByteBuffer paramByteBuffer)
  {
    this.buffer = paramByteBuffer;
    this.buffer.order(ByteOrder.LITTLE_ENDIAN);
  }
  
  private CodedOutputByteBufferNano(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this(ByteBuffer.wrap(paramArrayOfByte, paramInt1, paramInt2));
  }
  
  public static int computeGroupSize(int paramInt, MessageNano paramMessageNano)
  {
    return computeTagSize(paramInt) * 2 + computeGroupSizeNoTag(paramMessageNano);
  }
  
  public static int computeGroupSizeNoTag(MessageNano paramMessageNano)
  {
    return paramMessageNano.getSerializedSize();
  }
  
  public static int computeMessageSize(int paramInt, MessageNano paramMessageNano)
  {
    return computeTagSize(paramInt) + computeMessageSizeNoTag(paramMessageNano);
  }
  
  public static int computeMessageSizeNoTag(MessageNano paramMessageNano)
  {
    int i = paramMessageNano.getSerializedSize();
    return computeRawVarint32Size(i) + i;
  }
  
  public static int computeRawVarint32Size(int paramInt)
  {
    if ((paramInt & 0xFFFFFF80) == 0) {
      return 1;
    }
    if ((paramInt & 0xC000) == 0) {
      return 2;
    }
    if ((0xFFE00000 & paramInt) == 0) {
      return 3;
    }
    if ((0xF0000000 & paramInt) == 0) {
      return 4;
    }
    return 5;
  }
  
  public static int computeTagSize(int paramInt)
  {
    return computeRawVarint32Size(WireFormatNano.makeTag(paramInt, 0));
  }
  
  public static CodedOutputByteBufferNano newInstance(byte[] paramArrayOfByte)
  {
    return newInstance(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static CodedOutputByteBufferNano newInstance(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new CodedOutputByteBufferNano(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void checkNoSpaceLeft()
  {
    if (spaceLeft() != 0) {
      throw new IllegalStateException("Did not write as much data as expected.");
    }
  }
  
  public int spaceLeft()
  {
    return this.buffer.remaining();
  }
  
  public void writeGroupNoTag(MessageNano paramMessageNano)
    throws IOException
  {
    paramMessageNano.writeTo(this);
  }
  
  public void writeMessageNoTag(MessageNano paramMessageNano)
    throws IOException
  {
    writeRawVarint32(paramMessageNano.getCachedSize());
    paramMessageNano.writeTo(this);
  }
  
  public void writeRawByte(byte paramByte)
    throws IOException
  {
    if (!this.buffer.hasRemaining()) {
      throw new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
    }
    this.buffer.put(paramByte);
  }
  
  public void writeRawByte(int paramInt)
    throws IOException
  {
    writeRawByte((byte)paramInt);
  }
  
  public void writeRawBytes(byte[] paramArrayOfByte)
    throws IOException
  {
    writeRawBytes(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void writeRawBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.buffer.remaining() >= paramInt2)
    {
      this.buffer.put(paramArrayOfByte, paramInt1, paramInt2);
      return;
    }
    throw new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
  }
  
  public void writeRawVarint32(int paramInt)
    throws IOException
  {
    for (;;)
    {
      if ((paramInt & 0xFFFFFF80) == 0)
      {
        writeRawByte(paramInt);
        return;
      }
      writeRawByte(paramInt & 0x7F | 0x80);
      paramInt >>>= 7;
    }
  }
  
  public void writeTag(int paramInt1, int paramInt2)
    throws IOException
  {
    writeRawVarint32(WireFormatNano.makeTag(paramInt1, paramInt2));
  }
  
  public static class OutOfSpaceException
    extends IOException
  {
    private static final long serialVersionUID = -6947486886997889499L;
    
    OutOfSpaceException(int paramInt1, int paramInt2)
    {
      super();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\framework\protobuf\nano\CodedOutputByteBufferNano.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */