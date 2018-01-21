package com.android.settingslib;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.SparseBooleanArray;

public class AppItem
  implements Comparable<AppItem>, Parcelable
{
  public static final Parcelable.Creator<AppItem> CREATOR = new Parcelable.Creator()
  {
    public AppItem createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AppItem(paramAnonymousParcel);
    }
    
    public AppItem[] newArray(int paramAnonymousInt)
    {
      return new AppItem[paramAnonymousInt];
    }
  };
  public int category;
  public final int key;
  public long total;
  public SparseBooleanArray uids = new SparseBooleanArray();
  
  public AppItem()
  {
    this.key = 0;
  }
  
  public AppItem(Parcel paramParcel)
  {
    this.key = paramParcel.readInt();
    this.uids = paramParcel.readSparseBooleanArray();
    this.total = paramParcel.readLong();
  }
  
  public int compareTo(AppItem paramAppItem)
  {
    int j = Integer.compare(this.category, paramAppItem.category);
    int i = j;
    if (j == 0) {
      i = Long.compare(paramAppItem.total, this.total);
    }
    return i;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.key);
    paramParcel.writeSparseBooleanArray(this.uids);
    paramParcel.writeLong(this.total);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\settingslib\AppItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */