package android.support.v4.media;

import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

public final class MediaBrowserCompat
{
  static final boolean DEBUG = Log.isLoggable("MediaBrowserCompat", 3);
  
  public static abstract class ItemCallback
  {
    final Object mItemCallbackObj;
    
    public ItemCallback()
    {
      if (Build.VERSION.SDK_INT >= 23)
      {
        this.mItemCallbackObj = MediaBrowserCompatApi23.createItemCallback(new StubApi23());
        return;
      }
      this.mItemCallbackObj = null;
    }
    
    public void onError(@NonNull String paramString) {}
    
    public void onItemLoaded(MediaBrowserCompat.MediaItem paramMediaItem) {}
    
    private class StubApi23
      implements MediaBrowserCompatApi23.ItemCallback
    {
      StubApi23() {}
      
      public void onError(@NonNull String paramString)
      {
        MediaBrowserCompat.ItemCallback.this.onError(paramString);
      }
      
      public void onItemLoaded(Parcel paramParcel)
      {
        paramParcel.setDataPosition(0);
        MediaBrowserCompat.MediaItem localMediaItem = (MediaBrowserCompat.MediaItem)MediaBrowserCompat.MediaItem.CREATOR.createFromParcel(paramParcel);
        paramParcel.recycle();
        MediaBrowserCompat.ItemCallback.this.onItemLoaded(localMediaItem);
      }
    }
  }
  
  private static class ItemReceiver
    extends ResultReceiver
  {
    private final MediaBrowserCompat.ItemCallback mCallback;
    private final String mMediaId;
    
    protected void onReceiveResult(int paramInt, Bundle paramBundle)
    {
      paramBundle.setClassLoader(MediaBrowserCompat.class.getClassLoader());
      if ((paramInt != 0) || (paramBundle == null)) {}
      while (!paramBundle.containsKey("media_item"))
      {
        this.mCallback.onError(this.mMediaId);
        return;
      }
      paramBundle = paramBundle.getParcelable("media_item");
      if ((paramBundle == null) || ((paramBundle instanceof MediaBrowserCompat.MediaItem)))
      {
        this.mCallback.onItemLoaded((MediaBrowserCompat.MediaItem)paramBundle);
        return;
      }
      this.mCallback.onError(this.mMediaId);
    }
  }
  
  public static class MediaItem
    implements Parcelable
  {
    public static final Parcelable.Creator<MediaItem> CREATOR = new Parcelable.Creator()
    {
      public MediaBrowserCompat.MediaItem createFromParcel(Parcel paramAnonymousParcel)
      {
        return new MediaBrowserCompat.MediaItem(paramAnonymousParcel);
      }
      
      public MediaBrowserCompat.MediaItem[] newArray(int paramAnonymousInt)
      {
        return new MediaBrowserCompat.MediaItem[paramAnonymousInt];
      }
    };
    private final MediaDescriptionCompat mDescription;
    private final int mFlags;
    
    MediaItem(Parcel paramParcel)
    {
      this.mFlags = paramParcel.readInt();
      this.mDescription = ((MediaDescriptionCompat)MediaDescriptionCompat.CREATOR.createFromParcel(paramParcel));
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("MediaItem{");
      localStringBuilder.append("mFlags=").append(this.mFlags);
      localStringBuilder.append(", mDescription=").append(this.mDescription);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mFlags);
      this.mDescription.writeToParcel(paramParcel, paramInt);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\media\MediaBrowserCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */