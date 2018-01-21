package android.support.v4.media.session;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.List;

public final class PlaybackStateCompat
  implements Parcelable
{
  public static final Parcelable.Creator<PlaybackStateCompat> CREATOR = new Parcelable.Creator()
  {
    public PlaybackStateCompat createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PlaybackStateCompat(paramAnonymousParcel);
    }
    
    public PlaybackStateCompat[] newArray(int paramAnonymousInt)
    {
      return new PlaybackStateCompat[paramAnonymousInt];
    }
  };
  final long mActions;
  final long mActiveItemId;
  final long mBufferedPosition;
  List<CustomAction> mCustomActions;
  final CharSequence mErrorMessage;
  final Bundle mExtras;
  final long mPosition;
  final float mSpeed;
  final int mState;
  final long mUpdateTime;
  
  PlaybackStateCompat(Parcel paramParcel)
  {
    this.mState = paramParcel.readInt();
    this.mPosition = paramParcel.readLong();
    this.mSpeed = paramParcel.readFloat();
    this.mUpdateTime = paramParcel.readLong();
    this.mBufferedPosition = paramParcel.readLong();
    this.mActions = paramParcel.readLong();
    this.mErrorMessage = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    this.mCustomActions = paramParcel.createTypedArrayList(CustomAction.CREATOR);
    this.mActiveItemId = paramParcel.readLong();
    this.mExtras = paramParcel.readBundle();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("PlaybackState {");
    localStringBuilder.append("state=").append(this.mState);
    localStringBuilder.append(", position=").append(this.mPosition);
    localStringBuilder.append(", buffered position=").append(this.mBufferedPosition);
    localStringBuilder.append(", speed=").append(this.mSpeed);
    localStringBuilder.append(", updated=").append(this.mUpdateTime);
    localStringBuilder.append(", actions=").append(this.mActions);
    localStringBuilder.append(", error=").append(this.mErrorMessage);
    localStringBuilder.append(", custom actions=").append(this.mCustomActions);
    localStringBuilder.append(", active item id=").append(this.mActiveItemId);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mState);
    paramParcel.writeLong(this.mPosition);
    paramParcel.writeFloat(this.mSpeed);
    paramParcel.writeLong(this.mUpdateTime);
    paramParcel.writeLong(this.mBufferedPosition);
    paramParcel.writeLong(this.mActions);
    TextUtils.writeToParcel(this.mErrorMessage, paramParcel, paramInt);
    paramParcel.writeTypedList(this.mCustomActions);
    paramParcel.writeLong(this.mActiveItemId);
    paramParcel.writeBundle(this.mExtras);
  }
  
  public static final class CustomAction
    implements Parcelable
  {
    public static final Parcelable.Creator<CustomAction> CREATOR = new Parcelable.Creator()
    {
      public PlaybackStateCompat.CustomAction createFromParcel(Parcel paramAnonymousParcel)
      {
        return new PlaybackStateCompat.CustomAction(paramAnonymousParcel);
      }
      
      public PlaybackStateCompat.CustomAction[] newArray(int paramAnonymousInt)
      {
        return new PlaybackStateCompat.CustomAction[paramAnonymousInt];
      }
    };
    private final String mAction;
    private final Bundle mExtras;
    private final int mIcon;
    private final CharSequence mName;
    
    CustomAction(Parcel paramParcel)
    {
      this.mAction = paramParcel.readString();
      this.mName = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      this.mIcon = paramParcel.readInt();
      this.mExtras = paramParcel.readBundle();
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public String toString()
    {
      return "Action:mName='" + this.mName + ", mIcon=" + this.mIcon + ", mExtras=" + this.mExtras;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.mAction);
      TextUtils.writeToParcel(this.mName, paramParcel, paramInt);
      paramParcel.writeInt(this.mIcon);
      paramParcel.writeBundle(this.mExtras);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\media\session\PlaybackStateCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */