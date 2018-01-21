package android.support.v7.widget;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;

class StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem
  implements Parcelable
{
  public static final Parcelable.Creator<FullSpanItem> CREATOR = new Parcelable.Creator()
  {
    public StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem createFromParcel(Parcel paramAnonymousParcel)
    {
      return new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem(paramAnonymousParcel);
    }
    
    public StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem[] newArray(int paramAnonymousInt)
    {
      return new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem[paramAnonymousInt];
    }
  };
  int mGapDir;
  int[] mGapPerSpan;
  boolean mHasUnwantedGapAfter;
  int mPosition;
  
  public StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem() {}
  
  public StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem(Parcel paramParcel)
  {
    this.mPosition = paramParcel.readInt();
    this.mGapDir = paramParcel.readInt();
    if (paramParcel.readInt() == 1) {}
    for (;;)
    {
      this.mHasUnwantedGapAfter = bool;
      int i = paramParcel.readInt();
      if (i > 0)
      {
        this.mGapPerSpan = new int[i];
        paramParcel.readIntArray(this.mGapPerSpan);
      }
      return;
      bool = false;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    return "FullSpanItem{mPosition=" + this.mPosition + ", mGapDir=" + this.mGapDir + ", mHasUnwantedGapAfter=" + this.mHasUnwantedGapAfter + ", mGapPerSpan=" + Arrays.toString(this.mGapPerSpan) + '}';
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mPosition);
    paramParcel.writeInt(this.mGapDir);
    if (this.mHasUnwantedGapAfter) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      if ((this.mGapPerSpan == null) || (this.mGapPerSpan.length <= 0)) {
        break;
      }
      paramParcel.writeInt(this.mGapPerSpan.length);
      paramParcel.writeIntArray(this.mGapPerSpan);
      return;
    }
    paramParcel.writeInt(0);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */