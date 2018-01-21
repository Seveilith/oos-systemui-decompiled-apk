package android.support.v4.media.session;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.ResultReceiver;

final class MediaSessionCompat$ResultReceiverWrapper
  implements Parcelable
{
  public static final Parcelable.Creator<ResultReceiverWrapper> CREATOR = new Parcelable.Creator()
  {
    public MediaSessionCompat.ResultReceiverWrapper createFromParcel(Parcel paramAnonymousParcel)
    {
      return new MediaSessionCompat.ResultReceiverWrapper(paramAnonymousParcel);
    }
    
    public MediaSessionCompat.ResultReceiverWrapper[] newArray(int paramAnonymousInt)
    {
      return new MediaSessionCompat.ResultReceiverWrapper[paramAnonymousInt];
    }
  };
  private ResultReceiver mResultReceiver;
  
  MediaSessionCompat$ResultReceiverWrapper(Parcel paramParcel)
  {
    this.mResultReceiver = ((ResultReceiver)ResultReceiver.CREATOR.createFromParcel(paramParcel));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    this.mResultReceiver.writeToParcel(paramParcel, paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\media\session\MediaSessionCompat$ResultReceiverWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */