package com.android.framework.protobuf.nano.android;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.framework.protobuf.nano.ExtendableMessageNano;

public abstract class ParcelableExtendableMessageNano<M extends ExtendableMessageNano<M>>
  extends ExtendableMessageNano<M>
  implements Parcelable
{
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    ParcelableMessageNanoCreator.writeToParcel(getClass(), this, paramParcel);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\framework\protobuf\nano\android\ParcelableExtendableMessageNano.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */