package com.android.framework.protobuf.nano;

public final class InternalNano
{
  public static final Object LAZY_INIT_LOCK = new Object();
  
  public static void cloneUnknownFieldData(ExtendableMessageNano paramExtendableMessageNano1, ExtendableMessageNano paramExtendableMessageNano2)
  {
    if (paramExtendableMessageNano1.unknownFieldData != null) {
      paramExtendableMessageNano2.unknownFieldData = paramExtendableMessageNano1.unknownFieldData.clone();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\framework\protobuf\nano\InternalNano.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */