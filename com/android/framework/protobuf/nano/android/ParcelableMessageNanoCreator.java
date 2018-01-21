package com.android.framework.protobuf.nano.android;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Log;
import com.android.framework.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.framework.protobuf.nano.MessageNano;
import java.lang.reflect.Array;

public final class ParcelableMessageNanoCreator<T extends MessageNano>
  implements Parcelable.Creator<T>
{
  private final Class<T> mClazz;
  
  static <T extends MessageNano> void writeToParcel(Class<T> paramClass, MessageNano paramMessageNano, Parcel paramParcel)
  {
    paramParcel.writeString(paramClass.getName());
    paramParcel.writeByteArray(MessageNano.toByteArray(paramMessageNano));
  }
  
  public T createFromParcel(Parcel paramParcel)
  {
    String str = paramParcel.readString();
    byte[] arrayOfByte = paramParcel.createByteArray();
    Parcel localParcel2 = null;
    Parcel localParcel3 = null;
    Parcel localParcel4 = null;
    Parcel localParcel1 = null;
    try
    {
      paramParcel = (MessageNano)Class.forName(str).newInstance();
      localParcel1 = paramParcel;
      localParcel2 = paramParcel;
      localParcel3 = paramParcel;
      localParcel4 = paramParcel;
      MessageNano.mergeFrom(paramParcel, arrayOfByte);
      return paramParcel;
    }
    catch (InvalidProtocolBufferNanoException paramParcel)
    {
      Log.e("PMNCreator", "Exception trying to create proto from parcel", paramParcel);
      return localParcel1;
    }
    catch (InstantiationException paramParcel)
    {
      Log.e("PMNCreator", "Exception trying to create proto from parcel", paramParcel);
      return localParcel2;
    }
    catch (IllegalAccessException paramParcel)
    {
      Log.e("PMNCreator", "Exception trying to create proto from parcel", paramParcel);
      return localParcel3;
    }
    catch (ClassNotFoundException paramParcel)
    {
      Log.e("PMNCreator", "Exception trying to create proto from parcel", paramParcel);
    }
    return localParcel4;
  }
  
  public T[] newArray(int paramInt)
  {
    return (MessageNano[])Array.newInstance(this.mClazz, paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\framework\protobuf\nano\android\ParcelableMessageNanoCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */