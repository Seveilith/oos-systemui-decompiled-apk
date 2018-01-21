package android.support.v7.widget;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.RestrictTo;
import java.util.List;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class StaggeredGridLayoutManager$SavedState
  implements Parcelable
{
  public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
  {
    public StaggeredGridLayoutManager.SavedState createFromParcel(Parcel paramAnonymousParcel)
    {
      return new StaggeredGridLayoutManager.SavedState(paramAnonymousParcel);
    }
    
    public StaggeredGridLayoutManager.SavedState[] newArray(int paramAnonymousInt)
    {
      return new StaggeredGridLayoutManager.SavedState[paramAnonymousInt];
    }
  };
  boolean mAnchorLayoutFromEnd;
  int mAnchorPosition;
  List<StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem> mFullSpanItems;
  boolean mLastLayoutRTL;
  boolean mReverseLayout;
  int[] mSpanLookup;
  int mSpanLookupSize;
  int[] mSpanOffsets;
  int mSpanOffsetsSize;
  int mVisibleAnchorPosition;
  
  public StaggeredGridLayoutManager$SavedState() {}
  
  StaggeredGridLayoutManager$SavedState(Parcel paramParcel)
  {
    this.mAnchorPosition = paramParcel.readInt();
    this.mVisibleAnchorPosition = paramParcel.readInt();
    this.mSpanOffsetsSize = paramParcel.readInt();
    if (this.mSpanOffsetsSize > 0)
    {
      this.mSpanOffsets = new int[this.mSpanOffsetsSize];
      paramParcel.readIntArray(this.mSpanOffsets);
    }
    this.mSpanLookupSize = paramParcel.readInt();
    if (this.mSpanLookupSize > 0)
    {
      this.mSpanLookup = new int[this.mSpanLookupSize];
      paramParcel.readIntArray(this.mSpanLookup);
    }
    if (paramParcel.readInt() == 1)
    {
      bool1 = true;
      this.mReverseLayout = bool1;
      if (paramParcel.readInt() != 1) {
        break label152;
      }
      bool1 = true;
      label113:
      this.mAnchorLayoutFromEnd = bool1;
      if (paramParcel.readInt() != 1) {
        break label157;
      }
    }
    label152:
    label157:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.mLastLayoutRTL = bool1;
      this.mFullSpanItems = paramParcel.readArrayList(StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem.class.getClassLoader());
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label113;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeInt(this.mAnchorPosition);
    paramParcel.writeInt(this.mVisibleAnchorPosition);
    paramParcel.writeInt(this.mSpanOffsetsSize);
    if (this.mSpanOffsetsSize > 0) {
      paramParcel.writeIntArray(this.mSpanOffsets);
    }
    paramParcel.writeInt(this.mSpanLookupSize);
    if (this.mSpanLookupSize > 0) {
      paramParcel.writeIntArray(this.mSpanLookup);
    }
    if (this.mReverseLayout)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (!this.mAnchorLayoutFromEnd) {
        break label120;
      }
      paramInt = 1;
      label87:
      paramParcel.writeInt(paramInt);
      if (!this.mLastLayoutRTL) {
        break label125;
      }
    }
    label120:
    label125:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeList(this.mFullSpanItems);
      return;
      paramInt = 0;
      break;
      paramInt = 0;
      break label87;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\StaggeredGridLayoutManager$SavedState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */