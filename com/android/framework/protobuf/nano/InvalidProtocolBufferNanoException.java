package com.android.framework.protobuf.nano;

import java.io.IOException;

public class InvalidProtocolBufferNanoException
  extends IOException
{
  private static final long serialVersionUID = -1616151763072450476L;
  
  public InvalidProtocolBufferNanoException(String paramString)
  {
    super(paramString);
  }
  
  static InvalidProtocolBufferNanoException invalidEndTag()
  {
    return new InvalidProtocolBufferNanoException("Protocol message end-group tag did not match expected tag.");
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\framework\protobuf\nano\InvalidProtocolBufferNanoException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */