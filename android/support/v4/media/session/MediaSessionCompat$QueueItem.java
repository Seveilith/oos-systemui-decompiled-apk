package android.support.v4.media.session;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.media.MediaDescriptionCompat;

public final class MediaSessionCompat$QueueItem
  implements Parcelable
{
  public static final Parcelable.Creator<QueueItem> CREATOR = new Parcelable.Creator()
  {
    public MediaSessionCompat.QueueItem createFromParcel(Parcel paramAnonymousParcel)
    {
      return new MediaSessionCompat.QueueItem(paramAnonymousParcel);
    }
    
    public MediaSessionCompat.QueueItem[] newArray(int paramAnonymousInt)
    {
      return new MediaSessionCompat.QueueItem[paramAnonymousInt];
    }
  };
  private final MediaDescriptionCompat mDescription;
  private final long mId;
  
  MediaSessionCompat$QueueItem(Parcel paramParcel)
  {
    this.mDescription = ((MediaDescriptionCompat)MediaDescriptionCompat.CREATOR.createFromParcel(paramParcel));
    this.mId = paramParcel.readLong();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    return "MediaSession.QueueItem {Description=" + this.mDescription + ", Id=" + this.mId + " }";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    this.mDescription.writeToParcel(paramParcel, paramInt);
    paramParcel.writeLong(this.mId);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\media\session\MediaSessionCompat$QueueItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */