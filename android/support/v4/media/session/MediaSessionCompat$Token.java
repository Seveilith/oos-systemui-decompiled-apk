package android.support.v4.media.session;

import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class MediaSessionCompat$Token
  implements Parcelable
{
  public static final Parcelable.Creator<Token> CREATOR = new Parcelable.Creator()
  {
    public MediaSessionCompat.Token createFromParcel(Parcel paramAnonymousParcel)
    {
      if (Build.VERSION.SDK_INT >= 21) {}
      for (paramAnonymousParcel = paramAnonymousParcel.readParcelable(null);; paramAnonymousParcel = paramAnonymousParcel.readStrongBinder()) {
        return new MediaSessionCompat.Token(paramAnonymousParcel);
      }
    }
    
    public MediaSessionCompat.Token[] newArray(int paramAnonymousInt)
    {
      return new MediaSessionCompat.Token[paramAnonymousInt];
    }
  };
  private final Object mInner;
  
  MediaSessionCompat$Token(Object paramObject)
  {
    this.mInner = paramObject;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Token)) {
      return false;
    }
    paramObject = (Token)paramObject;
    if (this.mInner == null) {
      return ((Token)paramObject).mInner == null;
    }
    if (((Token)paramObject).mInner == null) {
      return false;
    }
    return this.mInner.equals(((Token)paramObject).mInner);
  }
  
  public int hashCode()
  {
    if (this.mInner == null) {
      return 0;
    }
    return this.mInner.hashCode();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramParcel.writeParcelable((Parcelable)this.mInner, paramInt);
      return;
    }
    paramParcel.writeStrongBinder((IBinder)this.mInner);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\media\session\MediaSessionCompat$Token.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */