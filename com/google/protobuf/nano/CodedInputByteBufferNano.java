package com.google.protobuf.nano;

import java.io.IOException;

public final class CodedInputByteBufferNano
{
  private final byte[] buffer;
  private int bufferPos;
  private int bufferSize;
  private int bufferSizeAfterLimit;
  private int bufferStart;
  private int currentLimit = Integer.MAX_VALUE;
  private int lastTag;
  private int recursionDepth;
  private int recursionLimit = 64;
  private int sizeLimit = 67108864;
  
  private CodedInputByteBufferNano(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.buffer = paramArrayOfByte;
    this.bufferStart = paramInt1;
    this.bufferSize = (paramInt1 + paramInt2);
    this.bufferPos = paramInt1;
  }
  
  public static CodedInputByteBufferNano newInstance(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new CodedInputByteBufferNano(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  private void recomputeBufferSizeAfterLimit()
  {
    this.bufferSize += this.bufferSizeAfterLimit;
    int i = this.bufferSize;
    if (i > this.currentLimit)
    {
      this.bufferSizeAfterLimit = (i - this.currentLimit);
      this.bufferSize -= this.bufferSizeAfterLimit;
      return;
    }
    this.bufferSizeAfterLimit = 0;
  }
  
  public void checkLastTagWas(int paramInt)
    throws InvalidProtocolBufferNanoException
  {
    if (this.lastTag != paramInt) {
      throw InvalidProtocolBufferNanoException.invalidEndTag();
    }
  }
  
  public int getPosition()
  {
    return this.bufferPos - this.bufferStart;
  }
  
  public boolean isAtEnd()
  {
    return this.bufferPos == this.bufferSize;
  }
  
  public void popLimit(int paramInt)
  {
    this.currentLimit = paramInt;
    recomputeBufferSizeAfterLimit();
  }
  
  public int pushLimit(int paramInt)
    throws InvalidProtocolBufferNanoException
  {
    if (paramInt < 0) {
      throw InvalidProtocolBufferNanoException.negativeSize();
    }
    paramInt += this.bufferPos;
    int i = this.currentLimit;
    if (paramInt > i) {
      throw InvalidProtocolBufferNanoException.truncatedMessage();
    }
    this.currentLimit = paramInt;
    recomputeBufferSizeAfterLimit();
    return i;
  }
  
  public boolean readBool()
    throws IOException
  {
    boolean bool = false;
    if (readRawVarint32() != 0) {
      bool = true;
    }
    return bool;
  }
  
  public float readFloat()
    throws IOException
  {
    return Float.intBitsToFloat(readRawLittleEndian32());
  }
  
  public int readInt32()
    throws IOException
  {
    return readRawVarint32();
  }
  
  public void readMessage(MessageNano paramMessageNano)
    throws IOException
  {
    int i = readRawVarint32();
    if (this.recursionDepth >= this.recursionLimit) {
      throw InvalidProtocolBufferNanoException.recursionLimitExceeded();
    }
    i = pushLimit(i);
    this.recursionDepth += 1;
    paramMessageNano.mergeFrom(this);
    checkLastTagWas(0);
    this.recursionDepth -= 1;
    popLimit(i);
  }
  
  public byte readRawByte()
    throws IOException
  {
    if (this.bufferPos == this.bufferSize) {
      throw InvalidProtocolBufferNanoException.truncatedMessage();
    }
    byte[] arrayOfByte = this.buffer;
    int i = this.bufferPos;
    this.bufferPos = (i + 1);
    return arrayOfByte[i];
  }
  
  public int readRawLittleEndian32()
    throws IOException
  {
    return readRawByte() & 0xFF | (readRawByte() & 0xFF) << 8 | (readRawByte() & 0xFF) << 16 | (readRawByte() & 0xFF) << 24;
  }
  
  public long readRawLittleEndian64()
    throws IOException
  {
    int i = readRawByte();
    int j = readRawByte();
    int k = readRawByte();
    int m = readRawByte();
    int n = readRawByte();
    int i1 = readRawByte();
    int i2 = readRawByte();
    int i3 = readRawByte();
    return i & 0xFF | (j & 0xFF) << 8 | (k & 0xFF) << 16 | (m & 0xFF) << 24 | (n & 0xFF) << 32 | (i1 & 0xFF) << 40 | (i2 & 0xFF) << 48 | (i3 & 0xFF) << 56;
  }
  
  public int readRawVarint32()
    throws IOException
  {
    int i = readRawByte();
    if (i >= 0) {
      return i;
    }
    i &= 0x7F;
    int j = readRawByte();
    if (j >= 0) {
      i |= j << 7;
    }
    int k;
    do
    {
      return i;
      i |= (j & 0x7F) << 7;
      j = readRawByte();
      if (j >= 0) {
        return i | j << 14;
      }
      i |= (j & 0x7F) << 14;
      j = readRawByte();
      if (j >= 0) {
        return i | j << 21;
      }
      k = readRawByte();
      j = i | (j & 0x7F) << 21 | k << 28;
      i = j;
    } while (k >= 0);
    i = 0;
    while (i < 5)
    {
      if (readRawByte() >= 0) {
        return j;
      }
      i += 1;
    }
    throw InvalidProtocolBufferNanoException.malformedVarint();
  }
  
  public long readRawVarint64()
    throws IOException
  {
    int i = 0;
    long l = 0L;
    while (i < 64)
    {
      int j = readRawByte();
      l |= (j & 0x7F) << i;
      if ((j & 0x80) == 0) {
        return l;
      }
      i += 7;
    }
    throw InvalidProtocolBufferNanoException.malformedVarint();
  }
  
  public String readString()
    throws IOException
  {
    int i = readRawVarint32();
    if (i < 0) {
      throw InvalidProtocolBufferNanoException.negativeSize();
    }
    if (i > this.bufferSize - this.bufferPos) {
      throw InvalidProtocolBufferNanoException.truncatedMessage();
    }
    String str = new String(this.buffer, this.bufferPos, i, "UTF-8");
    this.bufferPos += i;
    return str;
  }
  
  public int readTag()
    throws IOException
  {
    if (isAtEnd())
    {
      this.lastTag = 0;
      return 0;
    }
    this.lastTag = readRawVarint32();
    if (this.lastTag == 0) {
      throw InvalidProtocolBufferNanoException.invalidTag();
    }
    return this.lastTag;
  }
  
  public long readUInt64()
    throws IOException
  {
    return readRawVarint64();
  }
  
  public void rewindToPosition(int paramInt)
  {
    if (paramInt > this.bufferPos - this.bufferStart) {
      throw new IllegalArgumentException("Position " + paramInt + " is beyond current " + (this.bufferPos - this.bufferStart));
    }
    if (paramInt < 0) {
      throw new IllegalArgumentException("Bad position " + paramInt);
    }
    this.bufferPos = (this.bufferStart + paramInt);
  }
  
  public boolean skipField(int paramInt)
    throws IOException
  {
    switch (WireFormatNano.getTagWireType(paramInt))
    {
    default: 
      throw InvalidProtocolBufferNanoException.invalidWireType();
    case 0: 
      readInt32();
      return true;
    case 1: 
      readRawLittleEndian64();
      return true;
    case 2: 
      skipRawBytes(readRawVarint32());
      return true;
    case 3: 
      skipMessage();
      checkLastTagWas(WireFormatNano.makeTag(WireFormatNano.getTagFieldNumber(paramInt), 4));
      return true;
    case 4: 
      return false;
    }
    readRawLittleEndian32();
    return true;
  }
  
  public void skipMessage()
    throws IOException
  {
    int i;
    do
    {
      i = readTag();
    } while ((i != 0) && (skipField(i)));
  }
  
  public void skipRawBytes(int paramInt)
    throws IOException
  {
    if (paramInt < 0) {
      throw InvalidProtocolBufferNanoException.negativeSize();
    }
    if (this.bufferPos + paramInt > this.currentLimit)
    {
      skipRawBytes(this.currentLimit - this.bufferPos);
      throw InvalidProtocolBufferNanoException.truncatedMessage();
    }
    if (paramInt <= this.bufferSize - this.bufferPos)
    {
      this.bufferPos += paramInt;
      return;
    }
    throw InvalidProtocolBufferNanoException.truncatedMessage();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\google\protobuf\nano\CodedInputByteBufferNano.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */