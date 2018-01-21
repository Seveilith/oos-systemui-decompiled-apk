package android.support.v17.leanback.widget;

import android.support.v4.util.CircularArray;

final class StaggeredGridDefault
  extends StaggeredGrid
{
  private int findRowEdgeLimitSearchIndex(boolean paramBoolean)
  {
    int k = 0;
    int j = 0;
    int i;
    int m;
    if (paramBoolean)
    {
      i = this.mLastVisibleIndex;
      if (i >= this.mFirstVisibleIndex)
      {
        m = getLocation(i).row;
        if (m == 0) {
          k = 1;
        }
        do
        {
          do
          {
            i -= 1;
            j = k;
            break;
            k = j;
          } while (j == 0);
          k = j;
        } while (m != this.mNumRows - 1);
        return i;
      }
    }
    else
    {
      i = this.mFirstVisibleIndex;
      j = k;
      if (i <= this.mLastVisibleIndex)
      {
        m = getLocation(i).row;
        if (m == this.mNumRows - 1) {
          k = 1;
        }
        do
        {
          do
          {
            i += 1;
            j = k;
            break;
            k = j;
          } while (j == 0);
          k = j;
        } while (m != 0);
        return i;
      }
    }
    return -1;
  }
  
  protected boolean appendVisibleItemsWithoutCache(int paramInt, boolean paramBoolean)
  {
    int i5 = this.mProvider.getCount();
    int m;
    int n;
    int i;
    label91:
    label97:
    label121:
    int i1;
    boolean bool;
    if (this.mLastVisibleIndex >= 0)
    {
      if (this.mLastVisibleIndex < getLastIndex()) {
        return false;
      }
      m = this.mLastVisibleIndex + 1;
      n = getLocation(this.mLastVisibleIndex).row;
      i = findRowEdgeLimitSearchIndex(true);
      if (i < 0)
      {
        i = Integer.MIN_VALUE;
        j = 0;
        if (j < this.mNumRows)
        {
          if (this.mReversedFlow)
          {
            i = getRowMin(j);
            if (i == Integer.MIN_VALUE) {
              break label234;
            }
          }
        }
        else
        {
          if (!this.mReversedFlow) {
            break label272;
          }
          j = i;
          k = n;
          if (getRowMin(n) <= i)
          {
            n += 1;
            j = i;
            k = n;
            if (n == this.mNumRows)
            {
              k = 0;
              if (!this.mReversedFlow) {
                break label292;
              }
              j = findRowMin(false, null);
            }
          }
        }
      }
      for (;;)
      {
        i1 = 1;
        n = k;
        i = m;
        m = i1;
        k = j;
        bool = false;
        j = n;
        i1 = i;
        n = m;
        if (j >= this.mNumRows) {
          break label775;
        }
        if ((i1 != i5) && ((paramBoolean) || (!checkAppendOverLimit(paramInt)))) {
          break label370;
        }
        return bool;
        i = getRowMax(j);
        break label91;
        label234:
        j += 1;
        break;
        if (this.mReversedFlow)
        {
          i = findRowMin(false, i, null);
          break label97;
        }
        i = findRowMax(true, i, null);
        break label97;
        label272:
        j = i;
        k = n;
        if (getRowMax(n) >= i)
        {
          break label121;
          label292:
          j = findRowMax(true, null);
        }
      }
    }
    if (this.mStartIndex != -1)
    {
      i = this.mStartIndex;
      label316:
      if (this.mLocations.size() <= 0) {
        break label364;
      }
    }
    label364:
    for (int j = getLocation(getLastIndex()).row + 1;; j = i)
    {
      n = j % this.mNumRows;
      k = 0;
      m = 0;
      break;
      i = 0;
      break label316;
    }
    label370:
    label420:
    label453:
    label458:
    int i3;
    int i2;
    int i4;
    if (this.mReversedFlow)
    {
      i = getRowMin(j);
      if ((i != Integer.MAX_VALUE) && (i != Integer.MIN_VALUE)) {
        break label603;
      }
      if (j != 0) {
        break label572;
      }
      if (!this.mReversedFlow) {
        break label549;
      }
      m = getRowMin(this.mNumRows - 1);
      i = m;
      if (m != Integer.MAX_VALUE)
      {
        i = m;
        if (m != Integer.MIN_VALUE)
        {
          if (!this.mReversedFlow) {
            break label564;
          }
          i = -this.mMargin;
          i = m + i;
        }
      }
      m = i1 + 1;
      i3 = appendVisibleItemToRow(i1, j, i);
      bool = true;
      if (n == 0) {
        break label729;
      }
      i1 = i;
      i2 = m;
      if (!this.mReversedFlow) {
        break label634;
      }
      m = k;
      i4 = n;
      i = i2;
      if (i1 - i3 <= k) {
        break label655;
      }
    }
    for (;;)
    {
      if ((i2 == i5) || ((!paramBoolean) && (checkAppendOverLimit(paramInt))))
      {
        return true;
        i = getRowMax(j);
        break;
        label549:
        m = getRowMax(this.mNumRows - 1);
        break label420;
        label564:
        i = this.mMargin;
        break label453;
        label572:
        if (this.mReversedFlow)
        {
          i = getRowMax(j - 1);
          break label458;
        }
        i = getRowMin(j - 1);
        break label458;
        label603:
        if (this.mReversedFlow) {}
        for (m = -this.mMargin;; m = this.mMargin)
        {
          i += m;
          break;
        }
        label634:
        if (i1 + i3 >= k)
        {
          i = i2;
          i4 = n;
          m = k;
        }
      }
    }
    for (;;)
    {
      label655:
      j += 1;
      k = m;
      n = i4;
      i1 = i;
      break;
      if (this.mReversedFlow) {}
      for (i = -i3 - this.mMargin;; i = this.mMargin + i3)
      {
        i1 += i;
        m = i2 + 1;
        i3 = appendVisibleItemToRow(i2, j, i1);
        break;
      }
      label729:
      i4 = 1;
      if (this.mReversedFlow)
      {
        k = getRowMin(j);
        i = m;
        m = k;
      }
      else
      {
        k = getRowMax(j);
        i = m;
        m = k;
      }
    }
    label775:
    if (paramBoolean) {
      return bool;
    }
    if (this.mReversedFlow) {}
    for (int k = findRowMin(false, null);; k = findRowMax(true, null))
    {
      j = 0;
      break;
    }
  }
  
  public int findRowMax(boolean paramBoolean, int paramInt, int[] paramArrayOfInt)
  {
    int i = this.mProvider.getEdge(paramInt);
    StaggeredGrid.Location localLocation = getLocation(paramInt);
    int k = localLocation.row;
    int m = paramInt;
    int i1 = 1;
    int n = 1;
    int j = k;
    int i2;
    int i6;
    int i3;
    int i7;
    int i5;
    int i4;
    if (this.mReversedFlow)
    {
      i2 = i;
      paramInt += 1;
      i1 = j;
      j = i2;
      i6 = i;
      i = m;
      i2 = k;
      i3 = j;
      if (n < this.mNumRows)
      {
        i = m;
        i2 = k;
        i3 = j;
        if (paramInt <= this.mLastVisibleIndex)
        {
          localLocation = getLocation(paramInt);
          i3 = i6 + localLocation.offset;
          i7 = m;
          i5 = k;
          i4 = j;
          i = i1;
          i2 = n;
          if (localLocation.row != i1)
          {
            i1 = localLocation.row;
            n += 1;
            if (!paramBoolean) {
              break label248;
            }
            i7 = m;
            i5 = k;
            i4 = j;
            i = i1;
            i2 = n;
            if (i3 <= j) {}
          }
          for (;;)
          {
            i5 = i1;
            i4 = i3;
            i7 = paramInt;
            i2 = n;
            i = i1;
            label248:
            do
            {
              paramInt += 1;
              i6 = i3;
              m = i7;
              k = i5;
              j = i4;
              i1 = i;
              n = i2;
              break;
              i7 = m;
              i5 = k;
              i4 = j;
              i = i1;
              i2 = n;
            } while (i3 >= j);
          }
        }
      }
    }
    else
    {
      i2 = i + this.mProvider.getSize(paramInt);
      paramInt -= 1;
      n = i1;
      i1 = j;
      j = i2;
      i6 = i;
      i = m;
      i2 = k;
      i3 = j;
      if (n < this.mNumRows)
      {
        i = m;
        i2 = k;
        i3 = j;
        if (paramInt >= this.mFirstVisibleIndex)
        {
          i6 -= localLocation.offset;
          localLocation = getLocation(paramInt);
          i5 = m;
          i4 = k;
          i3 = j;
          i = i1;
          i2 = n;
          if (localLocation.row != i1)
          {
            i1 = localLocation.row;
            n += 1;
            i7 = i6 + this.mProvider.getSize(paramInt);
            if (!paramBoolean) {
              break label506;
            }
            i5 = m;
            i4 = k;
            i3 = j;
            i = i1;
            i2 = n;
            if (i7 <= j) {}
          }
          for (;;)
          {
            i4 = i1;
            i3 = i7;
            i5 = paramInt;
            i2 = n;
            i = i1;
            label506:
            do
            {
              paramInt -= 1;
              m = i5;
              k = i4;
              j = i3;
              i1 = i;
              n = i2;
              break;
              i5 = m;
              i4 = k;
              i3 = j;
              i = i1;
              i2 = n;
            } while (i7 >= j);
          }
        }
      }
    }
    if (paramArrayOfInt != null)
    {
      paramArrayOfInt[0] = i2;
      paramArrayOfInt[1] = i;
    }
    return i3;
  }
  
  public int findRowMin(boolean paramBoolean, int paramInt, int[] paramArrayOfInt)
  {
    int i = this.mProvider.getEdge(paramInt);
    StaggeredGrid.Location localLocation = getLocation(paramInt);
    int k = localLocation.row;
    int m = paramInt;
    int i1 = 1;
    int n = 1;
    int j = k;
    int i2;
    int i6;
    int i3;
    int i5;
    int i4;
    int i7;
    if (this.mReversedFlow)
    {
      i2 = i - this.mProvider.getSize(paramInt);
      paramInt -= 1;
      i1 = j;
      j = i2;
      i6 = i;
      i = m;
      i2 = k;
      i3 = j;
      if (n < this.mNumRows)
      {
        i = m;
        i2 = k;
        i3 = j;
        if (paramInt >= this.mFirstVisibleIndex)
        {
          i6 -= localLocation.offset;
          localLocation = getLocation(paramInt);
          i5 = m;
          i4 = k;
          i3 = j;
          i = i1;
          i2 = n;
          if (localLocation.row != i1)
          {
            i1 = localLocation.row;
            n += 1;
            i7 = i6 - this.mProvider.getSize(paramInt);
            if (!paramBoolean) {
              break label270;
            }
            i5 = m;
            i4 = k;
            i3 = j;
            i = i1;
            i2 = n;
            if (i7 <= j) {}
          }
          for (;;)
          {
            i3 = i7;
            i4 = i1;
            i5 = paramInt;
            i2 = n;
            i = i1;
            label270:
            do
            {
              paramInt -= 1;
              m = i5;
              k = i4;
              j = i3;
              i1 = i;
              n = i2;
              break;
              i5 = m;
              i4 = k;
              i3 = j;
              i = i1;
              i2 = n;
            } while (i7 >= j);
          }
        }
      }
    }
    else
    {
      i2 = i;
      paramInt += 1;
      n = i1;
      i1 = j;
      j = i2;
      i6 = i;
      i = m;
      i2 = k;
      i3 = j;
      if (n < this.mNumRows)
      {
        i = m;
        i2 = k;
        i3 = j;
        if (paramInt <= this.mLastVisibleIndex)
        {
          localLocation = getLocation(paramInt);
          i3 = i6 + localLocation.offset;
          i7 = m;
          i5 = k;
          i4 = j;
          i = i1;
          i2 = n;
          if (localLocation.row != i1)
          {
            i1 = localLocation.row;
            n += 1;
            if (!paramBoolean) {
              break label506;
            }
            i7 = m;
            i5 = k;
            i4 = j;
            i = i1;
            i2 = n;
            if (i3 <= j) {}
          }
          for (;;)
          {
            i4 = i3;
            i5 = i1;
            i7 = paramInt;
            i2 = n;
            i = i1;
            label506:
            do
            {
              paramInt += 1;
              i6 = i3;
              m = i7;
              k = i5;
              j = i4;
              i1 = i;
              n = i2;
              break;
              i7 = m;
              i5 = k;
              i4 = j;
              i = i1;
              i2 = n;
            } while (i3 >= j);
          }
        }
      }
    }
    if (paramArrayOfInt != null)
    {
      paramArrayOfInt[0] = i2;
      paramArrayOfInt[1] = i;
    }
    return i3;
  }
  
  int getRowMax(int paramInt)
  {
    if (this.mFirstVisibleIndex < 0) {
      return Integer.MIN_VALUE;
    }
    if (this.mReversedFlow)
    {
      j = this.mProvider.getEdge(this.mFirstVisibleIndex);
      if (getLocation(this.mFirstVisibleIndex).row == paramInt) {
        return j;
      }
      i = this.mFirstVisibleIndex + 1;
      while (i <= getLastIndex())
      {
        localLocation = getLocation(i);
        j += localLocation.offset;
        if (localLocation.row == paramInt) {
          return j;
        }
        i += 1;
      }
    }
    int j = this.mProvider.getEdge(this.mLastVisibleIndex);
    StaggeredGrid.Location localLocation = getLocation(this.mLastVisibleIndex);
    if (localLocation.row == paramInt) {
      return localLocation.size + j;
    }
    int i = this.mLastVisibleIndex - 1;
    while (i >= getFirstIndex())
    {
      j -= localLocation.offset;
      localLocation = getLocation(i);
      if (localLocation.row == paramInt) {
        return localLocation.size + j;
      }
      i -= 1;
    }
    return Integer.MIN_VALUE;
  }
  
  int getRowMin(int paramInt)
  {
    if (this.mFirstVisibleIndex < 0) {
      return Integer.MAX_VALUE;
    }
    StaggeredGrid.Location localLocation;
    if (this.mReversedFlow)
    {
      j = this.mProvider.getEdge(this.mLastVisibleIndex);
      localLocation = getLocation(this.mLastVisibleIndex);
      if (localLocation.row == paramInt) {
        return j - localLocation.size;
      }
      i = this.mLastVisibleIndex - 1;
      while (i >= getFirstIndex())
      {
        j -= localLocation.offset;
        localLocation = getLocation(i);
        if (localLocation.row == paramInt) {
          return j - localLocation.size;
        }
        i -= 1;
      }
    }
    int j = this.mProvider.getEdge(this.mFirstVisibleIndex);
    if (getLocation(this.mFirstVisibleIndex).row == paramInt) {
      return j;
    }
    int i = this.mFirstVisibleIndex + 1;
    while (i <= getLastIndex())
    {
      localLocation = getLocation(i);
      j += localLocation.offset;
      if (localLocation.row == paramInt) {
        return j;
      }
      i += 1;
    }
    return Integer.MAX_VALUE;
  }
  
  protected boolean prependVisibleItemsWithoutCache(int paramInt, boolean paramBoolean)
  {
    int n;
    int m;
    int i;
    label91:
    label101:
    label125:
    boolean bool;
    int i1;
    if (this.mFirstVisibleIndex >= 0)
    {
      if (this.mFirstVisibleIndex > getFirstIndex()) {
        return false;
      }
      n = this.mFirstVisibleIndex - 1;
      m = getLocation(this.mFirstVisibleIndex).row;
      i = findRowEdgeLimitSearchIndex(false);
      if (i < 0)
      {
        k = m - 1;
        i = Integer.MAX_VALUE;
        j = this.mNumRows - 1;
        m = k;
        if (j >= 0)
        {
          if (this.mReversedFlow)
          {
            i = getRowMax(j);
            if (i == Integer.MAX_VALUE) {
              break label221;
            }
            m = k;
          }
        }
        else
        {
          if (!this.mReversedFlow) {
            break label259;
          }
          j = i;
          k = m;
          if (getRowMax(m) >= i)
          {
            m -= 1;
            j = i;
            k = m;
            if (m < 0)
            {
              k = this.mNumRows - 1;
              if (!this.mReversedFlow) {
                break label279;
              }
              j = findRowMax(true, null);
            }
          }
        }
      }
      for (;;)
      {
        m = 1;
        i = k;
        k = j;
        bool = false;
        j = i;
        i1 = m;
        if (j < 0) {
          break label756;
        }
        if ((n >= 0) && ((paramBoolean) || (!checkPrependOverLimit(paramInt)))) {
          break label368;
        }
        return bool;
        i = getRowMin(j);
        break label91;
        label221:
        j -= 1;
        break;
        if (this.mReversedFlow)
        {
          i = findRowMax(true, i, null);
          break label101;
        }
        i = findRowMin(false, i, null);
        break label101;
        label259:
        j = i;
        k = m;
        if (getRowMin(m) <= i)
        {
          break label125;
          label279:
          j = findRowMin(false, null);
        }
      }
    }
    if (this.mStartIndex != -1)
    {
      i = this.mStartIndex;
      label303:
      if (this.mLocations.size() < 0) {
        break label362;
      }
    }
    label362:
    for (int j = getLocation(getFirstIndex()).row + this.mNumRows - 1;; j = i)
    {
      j %= this.mNumRows;
      k = 0;
      m = 0;
      n = i;
      i = j;
      break;
      i = 0;
      break label303;
    }
    label368:
    label419:
    label451:
    label456:
    int i2;
    int i5;
    int i3;
    int i4;
    if (this.mReversedFlow)
    {
      i = getRowMax(j);
      if ((i != Integer.MAX_VALUE) && (i != Integer.MIN_VALUE)) {
        break label593;
      }
      if (j != this.mNumRows - 1) {
        break label562;
      }
      if (!this.mReversedFlow) {
        break label543;
      }
      m = getRowMax(0);
      i = m;
      if (m != Integer.MAX_VALUE)
      {
        i = m;
        if (m != Integer.MIN_VALUE)
        {
          if (!this.mReversedFlow) {
            break label553;
          }
          i = this.mMargin;
          i = m + i;
        }
      }
      i2 = n - 1;
      i5 = prependVisibleItemToRow(n, j, i);
      bool = true;
      if (i1 == 0) {
        break label716;
      }
      i3 = i;
      i = i2;
      if (!this.mReversedFlow) {
        break label624;
      }
      m = k;
      i4 = i1;
      n = i;
      if (i3 + i5 >= k) {
        break label645;
      }
    }
    for (;;)
    {
      if ((i < 0) || ((!paramBoolean) && (checkPrependOverLimit(paramInt))))
      {
        return true;
        i = getRowMin(j);
        break;
        label543:
        m = getRowMin(0);
        break label419;
        label553:
        i = -this.mMargin;
        break label451;
        label562:
        if (this.mReversedFlow)
        {
          i = getRowMin(j + 1);
          break label456;
        }
        i = getRowMax(j + 1);
        break label456;
        label593:
        if (this.mReversedFlow) {}
        for (m = this.mMargin;; m = -this.mMargin)
        {
          i += m;
          break;
        }
        label624:
        if (i3 - i5 <= k)
        {
          n = i;
          i4 = i1;
          m = k;
        }
      }
    }
    for (;;)
    {
      label645:
      j -= 1;
      k = m;
      i1 = i4;
      break;
      if (this.mReversedFlow) {}
      for (m = this.mMargin + i5;; m = -i5 - this.mMargin)
      {
        i3 += m;
        i5 = prependVisibleItemToRow(i, j, i3);
        i -= 1;
        break;
      }
      label716:
      i4 = 1;
      if (this.mReversedFlow)
      {
        m = getRowMax(j);
        n = i2;
      }
      else
      {
        m = getRowMin(j);
        n = i2;
      }
    }
    label756:
    if (paramBoolean) {
      return bool;
    }
    if (this.mReversedFlow) {}
    for (int k = findRowMax(true, null);; k = findRowMin(false, null))
    {
      j = this.mNumRows - 1;
      break;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\StaggeredGridDefault.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */