package com.android.framework.protobuf.nano;

public final class CodedInputByteBufferNano
{
  private final byte[] buffer;
  private int bufferPos;
  private int bufferSize;
  private int bufferStart;
  private int currentLimit = Integer.MAX_VALUE;
  private int lastTag;
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
  
  public void checkLastTagWas(int paramInt)
    throws InvalidProtocolBufferNanoException
  {
    if (this.lastTag != paramInt) {
      throw InvalidProtocolBufferNanoException.invalidEndTag();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\framework\protobuf\nano\CodedInputByteBufferNano.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */