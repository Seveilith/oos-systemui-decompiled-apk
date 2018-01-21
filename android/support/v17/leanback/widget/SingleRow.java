package android.support.v17.leanback.widget;

import android.support.v4.util.CircularIntArray;

class SingleRow
  extends Grid
{
  private Object[] mTmpItem = new Object[1];
  private final Grid.Location mTmpLocation = new Grid.Location(0);
  
  SingleRow()
  {
    setNumRows(1);
  }
  
  protected final boolean appendVisibleItems(int paramInt, boolean paramBoolean)
  {
    if (this.mProvider.getCount() == 0) {
      return false;
    }
    if ((!paramBoolean) && (checkAppendOverLimit(paramInt))) {
      return false;
    }
    boolean bool1 = false;
    int j = getStartIndexForAppend();
    for (;;)
    {
      int k;
      if (j < this.mProvider.getCount())
      {
        k = this.mProvider.createItem(j, true, this.mTmpItem);
        if ((this.mFirstVisibleIndex >= 0) && (this.mLastVisibleIndex >= 0)) {
          break label161;
        }
        if (!this.mReversedFlow) {
          break label155;
        }
      }
      boolean bool2;
      label155:
      for (int i = Integer.MAX_VALUE;; i = Integer.MIN_VALUE)
      {
        this.mFirstVisibleIndex = j;
        this.mLastVisibleIndex = j;
        this.mProvider.addItem(this.mTmpItem[0], j, k, 0, i);
        boolean bool3 = true;
        bool2 = true;
        bool1 = bool3;
        if (!paramBoolean)
        {
          if (!checkAppendOverLimit(paramInt)) {
            break;
          }
          bool1 = bool3;
        }
        return bool1;
      }
      label161:
      if (this.mReversedFlow) {}
      for (i = this.mProvider.getEdge(j - 1) - this.mProvider.getSize(j - 1) - this.mMargin;; i = this.mProvider.getEdge(j - 1) + this.mProvider.getSize(j - 1) + this.mMargin)
      {
        this.mLastVisibleIndex = j;
        break;
      }
      j += 1;
      bool1 = bool2;
    }
  }
  
  protected final int findRowMax(boolean paramBoolean, int paramInt, int[] paramArrayOfInt)
  {
    if (paramArrayOfInt != null)
    {
      paramArrayOfInt[0] = 0;
      paramArrayOfInt[1] = paramInt;
    }
    if (this.mReversedFlow) {
      return this.mProvider.getEdge(paramInt);
    }
    return this.mProvider.getEdge(paramInt) + this.mProvider.getSize(paramInt);
  }
  
  protected final int findRowMin(boolean paramBoolean, int paramInt, int[] paramArrayOfInt)
  {
    if (paramArrayOfInt != null)
    {
      paramArrayOfInt[0] = 0;
      paramArrayOfInt[1] = paramInt;
    }
    if (this.mReversedFlow) {
      return this.mProvider.getEdge(paramInt) - this.mProvider.getSize(paramInt);
    }
    return this.mProvider.getEdge(paramInt);
  }
  
  public final CircularIntArray[] getItemPositionsInRows(int paramInt1, int paramInt2)
  {
    this.mTmpItemPositionsInRows[0].clear();
    this.mTmpItemPositionsInRows[0].addLast(paramInt1);
    this.mTmpItemPositionsInRows[0].addLast(paramInt2);
    return this.mTmpItemPositionsInRows;
  }
  
  public final Grid.Location getLocation(int paramInt)
  {
    return this.mTmpLocation;
  }
  
  int getStartIndexForAppend()
  {
    if (this.mLastVisibleIndex >= 0) {
      return this.mLastVisibleIndex + 1;
    }
    if (this.mStartIndex != -1) {
      return Math.min(this.mStartIndex, this.mProvider.getCount() - 1);
    }
    return 0;
  }
  
  int getStartIndexForPrepend()
  {
    if (this.mFirstVisibleIndex >= 0) {
      return this.mFirstVisibleIndex - 1;
    }
    if (this.mStartIndex != -1) {
      return Math.min(this.mStartIndex, this.mProvider.getCount() - 1);
    }
    return this.mProvider.getCount() - 1;
  }
  
  protected final boolean prependVisibleItems(int paramInt, boolean paramBoolean)
  {
    if (this.mProvider.getCount() == 0) {
      return false;
    }
    if ((!paramBoolean) && (checkPrependOverLimit(paramInt))) {
      return false;
    }
    boolean bool1 = false;
    int j = getStartIndexForPrepend();
    for (;;)
    {
      int k;
      if (j >= 0)
      {
        k = this.mProvider.createItem(j, false, this.mTmpItem);
        if ((this.mFirstVisibleIndex >= 0) && (this.mLastVisibleIndex >= 0)) {
          break label152;
        }
        if (!this.mReversedFlow) {
          break label146;
        }
      }
      boolean bool2;
      label146:
      for (int i = Integer.MIN_VALUE;; i = Integer.MAX_VALUE)
      {
        this.mFirstVisibleIndex = j;
        this.mLastVisibleIndex = j;
        this.mProvider.addItem(this.mTmpItem[0], j, k, 0, i);
        boolean bool3 = true;
        bool2 = true;
        bool1 = bool3;
        if (!paramBoolean)
        {
          if (!checkPrependOverLimit(paramInt)) {
            break;
          }
          bool1 = bool3;
        }
        return bool1;
      }
      label152:
      if (this.mReversedFlow) {}
      for (i = this.mProvider.getEdge(j + 1) + this.mMargin + k;; i = this.mProvider.getEdge(j + 1) - this.mMargin - k)
      {
        this.mFirstVisibleIndex = j;
        break;
      }
      j -= 1;
      bool1 = bool2;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\SingleRow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */