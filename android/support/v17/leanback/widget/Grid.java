package android.support.v17.leanback.widget;

import android.support.v4.util.CircularIntArray;

abstract class Grid
{
  protected int mFirstVisibleIndex = -1;
  protected int mLastVisibleIndex = -1;
  protected int mMargin;
  protected int mNumRows;
  protected Provider mProvider;
  protected boolean mReversedFlow;
  protected int mStartIndex = -1;
  protected CircularIntArray[] mTmpItemPositionsInRows;
  
  public static Grid createGrid(int paramInt)
  {
    if (paramInt == 1) {
      return new SingleRow();
    }
    StaggeredGridDefault localStaggeredGridDefault = new StaggeredGridDefault();
    localStaggeredGridDefault.setNumRows(paramInt);
    return localStaggeredGridDefault;
  }
  
  private void resetVisibleIndexIfEmpty()
  {
    if (this.mLastVisibleIndex < this.mFirstVisibleIndex) {
      resetVisibleIndex();
    }
  }
  
  public boolean appendOneColumnVisibleItems()
  {
    if (this.mReversedFlow) {}
    for (int i = Integer.MAX_VALUE;; i = Integer.MIN_VALUE) {
      return appendVisibleItems(i, true);
    }
  }
  
  public final void appendVisibleItems(int paramInt)
  {
    appendVisibleItems(paramInt, false);
  }
  
  protected abstract boolean appendVisibleItems(int paramInt, boolean paramBoolean);
  
  protected final boolean checkAppendOverLimit(int paramInt)
  {
    if (this.mLastVisibleIndex < 0) {
      return false;
    }
    if (this.mReversedFlow) {
      if (findRowMin(true, null) > this.mMargin + paramInt) {}
    }
    while (findRowMax(false, null) >= paramInt - this.mMargin)
    {
      return true;
      return false;
    }
    return false;
  }
  
  protected final boolean checkPrependOverLimit(int paramInt)
  {
    if (this.mLastVisibleIndex < 0) {
      return false;
    }
    if (this.mReversedFlow) {
      if (findRowMax(false, null) < paramInt - this.mMargin) {}
    }
    while (findRowMin(true, null) <= this.mMargin + paramInt)
    {
      return true;
      return false;
    }
    return false;
  }
  
  protected abstract int findRowMax(boolean paramBoolean, int paramInt, int[] paramArrayOfInt);
  
  public final int findRowMax(boolean paramBoolean, int[] paramArrayOfInt)
  {
    if (this.mReversedFlow) {}
    for (int i = this.mFirstVisibleIndex;; i = this.mLastVisibleIndex) {
      return findRowMax(paramBoolean, i, paramArrayOfInt);
    }
  }
  
  protected abstract int findRowMin(boolean paramBoolean, int paramInt, int[] paramArrayOfInt);
  
  public final int findRowMin(boolean paramBoolean, int[] paramArrayOfInt)
  {
    if (this.mReversedFlow) {}
    for (int i = this.mLastVisibleIndex;; i = this.mFirstVisibleIndex) {
      return findRowMin(paramBoolean, i, paramArrayOfInt);
    }
  }
  
  public final int getFirstVisibleIndex()
  {
    return this.mFirstVisibleIndex;
  }
  
  public final CircularIntArray[] getItemPositionsInRows()
  {
    return getItemPositionsInRows(getFirstVisibleIndex(), getLastVisibleIndex());
  }
  
  public abstract CircularIntArray[] getItemPositionsInRows(int paramInt1, int paramInt2);
  
  public final int getLastVisibleIndex()
  {
    return this.mLastVisibleIndex;
  }
  
  public abstract Location getLocation(int paramInt);
  
  public int getNumRows()
  {
    return this.mNumRows;
  }
  
  public final int getRowIndex(int paramInt)
  {
    return getLocation(paramInt).row;
  }
  
  public void invalidateItemsAfter(int paramInt)
  {
    if (paramInt < 0) {
      return;
    }
    if (this.mLastVisibleIndex < 0) {
      return;
    }
    while (this.mLastVisibleIndex >= paramInt)
    {
      this.mProvider.removeItem(this.mLastVisibleIndex);
      this.mLastVisibleIndex -= 1;
    }
    resetVisibleIndexIfEmpty();
    if (getFirstVisibleIndex() < 0) {
      setStart(paramInt);
    }
  }
  
  public boolean isReversedFlow()
  {
    return this.mReversedFlow;
  }
  
  public final boolean prependOneColumnVisibleItems()
  {
    if (this.mReversedFlow) {}
    for (int i = Integer.MIN_VALUE;; i = Integer.MAX_VALUE) {
      return prependVisibleItems(i, true);
    }
  }
  
  public final void prependVisibleItems(int paramInt)
  {
    prependVisibleItems(paramInt, false);
  }
  
  protected abstract boolean prependVisibleItems(int paramInt, boolean paramBoolean);
  
  public void removeInvisibleItemsAtEnd(int paramInt1, int paramInt2)
  {
    if ((this.mLastVisibleIndex >= this.mFirstVisibleIndex) && (this.mLastVisibleIndex > paramInt1))
    {
      int i;
      if (!this.mReversedFlow) {
        if (this.mProvider.getEdge(this.mLastVisibleIndex) >= paramInt2) {
          i = 1;
        }
      }
      for (;;)
      {
        if (i == 0) {
          break label107;
        }
        this.mProvider.removeItem(this.mLastVisibleIndex);
        this.mLastVisibleIndex -= 1;
        break;
        i = 0;
        continue;
        if (this.mProvider.getEdge(this.mLastVisibleIndex) <= paramInt2) {
          i = 1;
        } else {
          i = 0;
        }
      }
    }
    label107:
    resetVisibleIndexIfEmpty();
  }
  
  public void removeInvisibleItemsAtFront(int paramInt1, int paramInt2)
  {
    if ((this.mLastVisibleIndex >= this.mFirstVisibleIndex) && (this.mFirstVisibleIndex < paramInt1))
    {
      int i;
      if (!this.mReversedFlow) {
        if (this.mProvider.getEdge(this.mFirstVisibleIndex) + this.mProvider.getSize(this.mFirstVisibleIndex) <= paramInt2) {
          i = 1;
        }
      }
      for (;;)
      {
        if (i == 0) {
          break label135;
        }
        this.mProvider.removeItem(this.mFirstVisibleIndex);
        this.mFirstVisibleIndex += 1;
        break;
        i = 0;
        continue;
        if (this.mProvider.getEdge(this.mFirstVisibleIndex) - this.mProvider.getSize(this.mFirstVisibleIndex) >= paramInt2) {
          i = 1;
        } else {
          i = 0;
        }
      }
    }
    label135:
    resetVisibleIndexIfEmpty();
  }
  
  public void resetVisibleIndex()
  {
    this.mLastVisibleIndex = -1;
    this.mFirstVisibleIndex = -1;
  }
  
  public final void setMargin(int paramInt)
  {
    this.mMargin = paramInt;
  }
  
  void setNumRows(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException();
    }
    if (this.mNumRows == paramInt) {
      return;
    }
    this.mNumRows = paramInt;
    this.mTmpItemPositionsInRows = new CircularIntArray[this.mNumRows];
    paramInt = 0;
    while (paramInt < this.mNumRows)
    {
      this.mTmpItemPositionsInRows[paramInt] = new CircularIntArray();
      paramInt += 1;
    }
  }
  
  public void setProvider(Provider paramProvider)
  {
    this.mProvider = paramProvider;
  }
  
  public final void setReversedFlow(boolean paramBoolean)
  {
    this.mReversedFlow = paramBoolean;
  }
  
  public void setStart(int paramInt)
  {
    this.mStartIndex = paramInt;
  }
  
  public static class Location
  {
    public int row;
    
    public Location(int paramInt)
    {
      this.row = paramInt;
    }
  }
  
  public static abstract interface Provider
  {
    public abstract void addItem(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
    
    public abstract int createItem(int paramInt, boolean paramBoolean, Object[] paramArrayOfObject);
    
    public abstract int getCount();
    
    public abstract int getEdge(int paramInt);
    
    public abstract int getSize(int paramInt);
    
    public abstract void removeItem(int paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\Grid.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */