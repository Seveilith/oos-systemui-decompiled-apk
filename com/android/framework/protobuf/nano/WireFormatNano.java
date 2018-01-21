package com.android.framework.protobuf.nano;

public final class WireFormatNano
{
  public static final boolean[] EMPTY_BOOLEAN_ARRAY;
  public static final byte[] EMPTY_BYTES = new byte[0];
  public static final byte[][] EMPTY_BYTES_ARRAY;
  public static final double[] EMPTY_DOUBLE_ARRAY;
  public static final float[] EMPTY_FLOAT_ARRAY;
  public static final int[] EMPTY_INT_ARRAY = new int[0];
  public static final long[] EMPTY_LONG_ARRAY = new long[0];
  public static final String[] EMPTY_STRING_ARRAY;
  
  static
  {
    EMPTY_FLOAT_ARRAY = new float[0];
    EMPTY_DOUBLE_ARRAY = new double[0];
    EMPTY_BOOLEAN_ARRAY = new boolean[0];
    EMPTY_STRING_ARRAY = new String[0];
    EMPTY_BYTES_ARRAY = new byte[0][];
  }
  
  public static int getTagFieldNumber(int paramInt)
  {
    return paramInt >>> 3;
  }
  
  static int makeTag(int paramInt1, int paramInt2)
  {
    return paramInt1 << 3 | paramInt2;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\framework\protobuf\nano\WireFormatNano.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */