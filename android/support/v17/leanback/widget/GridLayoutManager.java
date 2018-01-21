package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.util.CircularIntArray;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.RecyclerView.Recycler;
import android.support.v7.widget.RecyclerView.SmoothScroller.Action;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import java.util.ArrayList;

final class GridLayoutManager
  extends RecyclerView.LayoutManager
{
  private static final Rect sTempRect = new Rect();
  static int[] sTwoInts = new int[2];
  final BaseGridView mBaseGridView;
  OnChildLaidOutListener mChildLaidOutListener = null;
  private OnChildSelectedListener mChildSelectedListener = null;
  private ArrayList<OnChildViewHolderSelectedListener> mChildViewHolderSelectedListeners = null;
  int mChildVisibility = -1;
  final ViewsStateBundle mChildrenStates = new ViewsStateBundle();
  private int mExtraLayoutSpace;
  private FacetProviderAdapter mFacetProviderAdapter;
  private int mFixedRowSizeSecondary;
  private boolean mFocusOutEnd;
  private boolean mFocusOutFront;
  private boolean mFocusOutSideEnd = true;
  private boolean mFocusOutSideStart = true;
  int mFocusPosition = -1;
  private int mFocusPositionOffset = 0;
  private int mFocusScrollStrategy = 0;
  private boolean mFocusSearchDisabled;
  private boolean mForceFullLayout;
  private int mGravity = 8388659;
  Grid mGrid;
  private Grid.Provider mGridProvider = new Grid.Provider()
  {
    public void addItem(Object paramAnonymousObject, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
    {
      paramAnonymousObject = (View)paramAnonymousObject;
      int i;
      if (paramAnonymousInt4 != Integer.MIN_VALUE)
      {
        i = paramAnonymousInt4;
        if (paramAnonymousInt4 != Integer.MAX_VALUE) {}
      }
      else
      {
        if (GridLayoutManager.this.mGrid.isReversedFlow()) {
          break label294;
        }
        i = GridLayoutManager.this.mWindowAlignment.mainAxis().getPaddingLow();
      }
      label67:
      label81:
      label165:
      label199:
      RecyclerView.ViewHolder localViewHolder;
      OnChildLaidOutListener localOnChildLaidOutListener;
      BaseGridView localBaseGridView;
      if (GridLayoutManager.this.mGrid.isReversedFlow())
      {
        paramAnonymousInt4 = 0;
        if (paramAnonymousInt4 == 0) {
          break label332;
        }
        paramAnonymousInt4 = i;
        paramAnonymousInt2 = i + paramAnonymousInt2;
        i = GridLayoutManager.this.getRowStartSecondary(paramAnonymousInt3);
        int j = GridLayoutManager.this.mScrollOffsetSecondary;
        GridLayoutManager.this.mChildrenStates.loadView((View)paramAnonymousObject, paramAnonymousInt1);
        GridLayoutManager.this.layoutChild(paramAnonymousInt3, (View)paramAnonymousObject, paramAnonymousInt4, paramAnonymousInt2, i - j);
        if (paramAnonymousInt1 == GridLayoutManager.this.mGrid.getFirstVisibleIndex())
        {
          if (GridLayoutManager.this.mGrid.isReversedFlow()) {
            break label344;
          }
          GridLayoutManager.this.updateScrollMin();
        }
        if (paramAnonymousInt1 == GridLayoutManager.this.mGrid.getLastVisibleIndex())
        {
          if (GridLayoutManager.this.mGrid.isReversedFlow()) {
            break label354;
          }
          GridLayoutManager.this.updateScrollMax();
        }
        if ((!GridLayoutManager.this.mInLayout) && (GridLayoutManager.this.mPendingMoveSmoothScroller != null)) {
          GridLayoutManager.this.mPendingMoveSmoothScroller.consumePendingMovesAfterLayout();
        }
        if (GridLayoutManager.this.mChildLaidOutListener != null)
        {
          localViewHolder = GridLayoutManager.this.mBaseGridView.getChildViewHolder((View)paramAnonymousObject);
          localOnChildLaidOutListener = GridLayoutManager.this.mChildLaidOutListener;
          localBaseGridView = GridLayoutManager.this.mBaseGridView;
          if (localViewHolder != null) {
            break label364;
          }
        }
      }
      label294:
      label332:
      label344:
      label354:
      label364:
      for (long l = -1L;; l = localViewHolder.getItemId())
      {
        localOnChildLaidOutListener.onChildLaidOut(localBaseGridView, (View)paramAnonymousObject, paramAnonymousInt1, l);
        return;
        i = GridLayoutManager.this.mWindowAlignment.mainAxis().getSize() - GridLayoutManager.this.mWindowAlignment.mainAxis().getPaddingHigh();
        break;
        paramAnonymousInt4 = 1;
        break label67;
        paramAnonymousInt4 = i - paramAnonymousInt2;
        paramAnonymousInt2 = i;
        break label81;
        GridLayoutManager.this.updateScrollMax();
        break label165;
        GridLayoutManager.this.updateScrollMin();
        break label199;
      }
    }
    
    public int createItem(int paramAnonymousInt, boolean paramAnonymousBoolean, Object[] paramAnonymousArrayOfObject)
    {
      View localView = GridLayoutManager.this.getViewForPosition(paramAnonymousInt);
      GridLayoutManager.LayoutParams localLayoutParams = (GridLayoutManager.LayoutParams)localView.getLayoutParams();
      RecyclerView.ViewHolder localViewHolder = GridLayoutManager.this.mBaseGridView.getChildViewHolder(localView);
      localLayoutParams.setItemAlignmentFacet((ItemAlignmentFacet)GridLayoutManager.this.getFacet(localViewHolder, ItemAlignmentFacet.class));
      int i;
      if (!localLayoutParams.isItemRemoved())
      {
        if (!paramAnonymousBoolean) {
          break label217;
        }
        GridLayoutManager.this.addView(localView);
        if (GridLayoutManager.this.mChildVisibility != -1) {
          localView.setVisibility(GridLayoutManager.this.mChildVisibility);
        }
        if (GridLayoutManager.this.mPendingMoveSmoothScroller != null) {
          GridLayoutManager.this.mPendingMoveSmoothScroller.consumePendingMovesBeforeLayout();
        }
        i = GridLayoutManager.this.getSubPositionByView(localView, localView.findFocus());
        if (GridLayoutManager.this.mInLayout) {
          break label230;
        }
        if ((paramAnonymousInt == GridLayoutManager.this.mFocusPosition) && (i == GridLayoutManager.this.mSubFocusPosition) && (GridLayoutManager.this.mPendingMoveSmoothScroller == null)) {
          GridLayoutManager.this.dispatchChildSelected();
        }
      }
      for (;;)
      {
        GridLayoutManager.this.measureChild(localView);
        paramAnonymousArrayOfObject[0] = localView;
        if (GridLayoutManager.this.mOrientation != 0) {
          break label347;
        }
        return GridLayoutManager.this.getDecoratedMeasuredWidthWithMargin(localView);
        label217:
        GridLayoutManager.this.addView(localView, 0);
        break;
        label230:
        if (!GridLayoutManager.this.mInFastRelayout) {
          if ((!GridLayoutManager.this.mInLayoutSearchFocus) && (paramAnonymousInt == GridLayoutManager.this.mFocusPosition) && (i == GridLayoutManager.this.mSubFocusPosition))
          {
            GridLayoutManager.this.dispatchChildSelected();
          }
          else if ((GridLayoutManager.this.mInLayoutSearchFocus) && (paramAnonymousInt >= GridLayoutManager.this.mFocusPosition) && (localView.hasFocusable()))
          {
            GridLayoutManager.this.mFocusPosition = paramAnonymousInt;
            GridLayoutManager.this.mSubFocusPosition = i;
            GridLayoutManager.this.mInLayoutSearchFocus = false;
            GridLayoutManager.this.dispatchChildSelected();
          }
        }
      }
      label347:
      return GridLayoutManager.this.getDecoratedMeasuredHeightWithMargin(localView);
    }
    
    public int getCount()
    {
      return GridLayoutManager.this.mState.getItemCount();
    }
    
    public int getEdge(int paramAnonymousInt)
    {
      if (GridLayoutManager.this.mReverseFlowPrimary) {
        return GridLayoutManager.this.getViewMax(GridLayoutManager.this.findViewByPosition(paramAnonymousInt));
      }
      return GridLayoutManager.this.getViewMin(GridLayoutManager.this.findViewByPosition(paramAnonymousInt));
    }
    
    public int getSize(int paramAnonymousInt)
    {
      return GridLayoutManager.this.getViewPrimarySize(GridLayoutManager.this.findViewByPosition(paramAnonymousInt));
    }
    
    public void removeItem(int paramAnonymousInt)
    {
      View localView = GridLayoutManager.this.findViewByPosition(paramAnonymousInt);
      if (GridLayoutManager.this.mInLayout)
      {
        GridLayoutManager.this.detachAndScrapView(localView, GridLayoutManager.this.mRecycler);
        return;
      }
      GridLayoutManager.this.removeAndRecycleView(localView, GridLayoutManager.this.mRecycler);
    }
  };
  private int mHorizontalMargin;
  boolean mInFastRelayout;
  boolean mInLayout;
  boolean mInLayoutSearchFocus;
  private boolean mInScroll;
  boolean mInSelection = false;
  private final ItemAlignment mItemAlignment = new ItemAlignment();
  private boolean mLayoutEnabled = true;
  private int mMarginPrimary;
  private int mMarginSecondary;
  private int mMaxSizeSecondary;
  private int[] mMeasuredDimension = new int[2];
  int mNumRows;
  private int mNumRowsRequested = 1;
  int mOrientation = 0;
  private OrientationHelper mOrientationHelper = OrientationHelper.createHorizontalHelper(this);
  PendingMoveSmoothScroller mPendingMoveSmoothScroller;
  private int mPrimaryScrollExtra;
  private boolean mPruneChild = true;
  RecyclerView.Recycler mRecycler;
  private final Runnable mRequestLayoutRunnable = new Runnable()
  {
    public void run()
    {
      GridLayoutManager.this.requestLayout();
    }
  };
  boolean mReverseFlowPrimary = false;
  private boolean mReverseFlowSecondary = false;
  private boolean mRowSecondarySizeRefresh;
  private int[] mRowSizeSecondary;
  private int mRowSizeSecondaryRequested;
  private boolean mScrollEnabled = true;
  private int mScrollOffsetPrimary;
  int mScrollOffsetSecondary;
  private int mSizePrimary;
  RecyclerView.State mState;
  int mSubFocusPosition = 0;
  private int mVerticalMargin;
  final WindowAlignment mWindowAlignment = new WindowAlignment();
  
  public GridLayoutManager(BaseGridView paramBaseGridView)
  {
    this.mBaseGridView = paramBaseGridView;
  }
  
  private boolean appendOneColumnVisibleItems()
  {
    return this.mGrid.appendOneColumnVisibleItems();
  }
  
  private void appendVisibleItems()
  {
    Grid localGrid = this.mGrid;
    if (this.mReverseFlowPrimary) {}
    for (int i = -this.mExtraLayoutSpace;; i = this.mSizePrimary + this.mExtraLayoutSpace)
    {
      localGrid.appendVisibleItems(i);
      return;
    }
  }
  
  private void discardLayoutInfo()
  {
    this.mGrid = null;
    this.mRowSizeSecondary = null;
    this.mRowSecondarySizeRefresh = false;
  }
  
  private void fastRelayout()
  {
    int n = 0;
    int i1 = getChildCount();
    int j = -1;
    int k = 0;
    int i;
    for (;;)
    {
      i = n;
      View localView2;
      Grid.Location localLocation;
      if (k < i1)
      {
        localView2 = getChildAt(k);
        j = getPositionByIndex(k);
        localLocation = this.mGrid.getLocation(j);
        if (localLocation == null) {
          i = 1;
        }
      }
      else
      {
        if (i == 0) {
          break label311;
        }
        i = this.mGrid.getLastVisibleIndex();
        this.mGrid.invalidateItemsAfter(j);
        if (!this.mPruneChild) {
          break;
        }
        appendVisibleItems();
        if ((this.mFocusPosition < 0) || (this.mFocusPosition > i)) {
          break label311;
        }
        while (this.mGrid.getLastVisibleIndex() < this.mFocusPosition) {
          this.mGrid.appendOneColumnVisibleItems();
        }
      }
      int i2 = getRowStartSecondary(localLocation.row);
      int i3 = this.mScrollOffsetSecondary;
      int i4 = getViewMin(localView2);
      int i5 = getViewPrimarySize(localView2);
      View localView1 = localView2;
      if (((LayoutParams)localView2.getLayoutParams()).viewNeedsUpdate())
      {
        i = this.mBaseGridView.indexOfChild(localView2);
        detachAndScrapView(localView2, this.mRecycler);
        localView1 = getViewForPosition(j);
        addView(localView1, i);
      }
      measureChild(localView1);
      int m;
      if (this.mOrientation == 0) {
        m = getDecoratedMeasuredWidthWithMargin(localView1);
      }
      for (i = i4 + m;; i = i4 + m)
      {
        layoutChild(localLocation.row, localView1, i4, i, i2 - i3);
        if (i5 == m) {
          break label283;
        }
        i = 1;
        break;
        m = getDecoratedMeasuredHeightWithMargin(localView1);
      }
      label283:
      k += 1;
    }
    while ((this.mGrid.appendOneColumnVisibleItems()) && (this.mGrid.getLastVisibleIndex() < i)) {}
    label311:
    updateScrollMin();
    updateScrollMax();
    updateScrollSecondAxis();
  }
  
  private int findImmediateChildIndex(View paramView)
  {
    if ((this.mBaseGridView != null) && (paramView != this.mBaseGridView))
    {
      paramView = findContainingItemView(paramView);
      if (paramView != null)
      {
        int i = 0;
        int j = getChildCount();
        while (i < j)
        {
          if (getChildAt(i) == paramView) {
            return i;
          }
          i += 1;
        }
      }
    }
    return -1;
  }
  
  private void forceRequestLayout()
  {
    ViewCompat.postOnAnimation(this.mBaseGridView, this.mRequestLayoutRunnable);
  }
  
  private int getAdjustedPrimaryScrollPosition(int paramInt, View paramView1, View paramView2)
  {
    int j = getSubPositionByView(paramView1, paramView2);
    int i = paramInt;
    if (j != 0)
    {
      paramView1 = (LayoutParams)paramView1.getLayoutParams();
      i = paramInt + (paramView1.getAlignMultiple()[j] - paramView1.getAlignMultiple()[0]);
    }
    return i;
  }
  
  private boolean getAlignedPosition(View paramView1, View paramView2, int[] paramArrayOfInt)
  {
    int j = getPrimarySystemScrollPosition(paramView1);
    int i = j;
    if (paramView2 != null) {
      i = getAdjustedPrimaryScrollPosition(j, paramView1, paramView2);
    }
    int k = getSecondarySystemScrollPosition(paramView1);
    j = this.mScrollOffsetPrimary;
    k -= this.mScrollOffsetSecondary;
    i = i - j + this.mPrimaryScrollExtra;
    if ((i != 0) || (k != 0))
    {
      paramArrayOfInt[0] = i;
      paramArrayOfInt[1] = k;
      return true;
    }
    return false;
  }
  
  private int getMovement(int paramInt)
  {
    if (this.mOrientation == 0) {
      switch (paramInt)
      {
      }
    }
    while (this.mOrientation != 1)
    {
      return 17;
      if (!this.mReverseFlowPrimary) {
        return 0;
      }
      return 1;
      if (!this.mReverseFlowPrimary) {
        return 1;
      }
      return 0;
      return 2;
      return 3;
    }
    switch (paramInt)
    {
    default: 
      return 17;
    case 17: 
      if (!this.mReverseFlowSecondary) {
        return 2;
      }
      return 3;
    case 66: 
      if (!this.mReverseFlowSecondary) {
        return 3;
      }
      return 2;
    case 33: 
      return 0;
    }
    return 1;
  }
  
  private boolean getNoneAlignedPosition(View paramView, int[] paramArrayOfInt)
  {
    int i = getPositionByView(paramView);
    int m = getViewMin(paramView);
    int n = getViewMax(paramView);
    Object localObject3 = null;
    Object localObject4 = null;
    int j = this.mWindowAlignment.mainAxis().getPaddingLow();
    int k = this.mWindowAlignment.mainAxis().getClientSize();
    int i1 = this.mGrid.getRowIndex(i);
    Object localObject1;
    Object localObject2;
    if (m < j)
    {
      localObject3 = paramView;
      localObject1 = localObject3;
      localObject2 = localObject4;
      if (this.mFocusScrollStrategy == 2)
      {
        localObject1 = localObject3;
        do
        {
          localObject2 = localObject4;
          if (!prependOneColumnVisibleItems()) {
            break;
          }
          localObject3 = this.mGrid.getItemPositionsInRows(this.mGrid.getFirstVisibleIndex(), i)[i1];
          localObject2 = findViewByPosition(((CircularIntArray)localObject3).get(0));
          localObject1 = localObject2;
        } while (n - getViewMin((View)localObject2) <= k);
        localObject1 = localObject2;
        localObject2 = localObject4;
        if (((CircularIntArray)localObject3).size() > 2)
        {
          localObject1 = findViewByPosition(((CircularIntArray)localObject3).get(2));
          localObject2 = localObject4;
        }
      }
      i = 0;
      if (localObject1 == null) {
        break label364;
      }
      i = getViewMin((View)localObject1) - j;
      label201:
      if (localObject1 == null) {
        break label385;
      }
      paramView = (View)localObject1;
    }
    for (;;)
    {
      j = getSecondarySystemScrollPosition(paramView) - this.mScrollOffsetSecondary;
      if ((i == 0) && (j == 0)) {
        break label399;
      }
      paramArrayOfInt[0] = i;
      paramArrayOfInt[1] = j;
      return true;
      localObject1 = localObject3;
      localObject2 = localObject4;
      if (n <= k + j) {
        break;
      }
      if (this.mFocusScrollStrategy == 2)
      {
        localObject1 = paramView;
        for (;;)
        {
          localObject2 = this.mGrid.getItemPositionsInRows(i, this.mGrid.getLastVisibleIndex())[i1];
          localObject3 = findViewByPosition(((CircularIntArray)localObject2).get(((CircularIntArray)localObject2).size() - 1));
          if (getViewMax((View)localObject3) - m > k) {
            localObject3 = null;
          }
          while (!appendOneColumnVisibleItems())
          {
            localObject2 = localObject3;
            if (localObject3 == null) {
              break;
            }
            localObject1 = null;
            localObject2 = localObject3;
            break;
          }
        }
      }
      localObject2 = paramView;
      localObject1 = localObject3;
      break;
      label364:
      if (localObject2 == null) {
        break label201;
      }
      i = getViewMax((View)localObject2) - (j + k);
      break label201;
      label385:
      if (localObject2 != null) {
        paramView = (View)localObject2;
      }
    }
    label399:
    return false;
  }
  
  private int getPositionByIndex(int paramInt)
  {
    return getPositionByView(getChildAt(paramInt));
  }
  
  private int getPositionByView(View paramView)
  {
    if (paramView == null) {
      return -1;
    }
    paramView = (LayoutParams)paramView.getLayoutParams();
    if ((paramView == null) || (paramView.isItemRemoved())) {
      return -1;
    }
    return paramView.getViewPosition();
  }
  
  private int getPrimarySystemScrollPosition(View paramView)
  {
    int j = this.mScrollOffsetPrimary;
    int k = getViewCenter(paramView);
    int m = getViewMin(paramView);
    int n = getViewMax(paramView);
    boolean bool2;
    int i1;
    int i;
    label67:
    boolean bool1;
    label78:
    boolean bool3;
    label89:
    View localView;
    boolean bool4;
    boolean bool5;
    if (!this.mReverseFlowPrimary) {
      if (this.mGrid.getFirstVisibleIndex() == 0)
      {
        bool2 = true;
        i1 = this.mGrid.getLastVisibleIndex();
        if (this.mState != null) {
          break label158;
        }
        i = getItemCount();
        if (i1 != i - 1) {
          break label169;
        }
        bool1 = true;
        i = getChildCount() - 1;
        bool3 = bool1;
        if (((!bool2) && (!bool3)) || (i < 0)) {
          break label315;
        }
        localView = getChildAt(i);
        bool4 = bool3;
        bool5 = bool2;
        if (localView != paramView)
        {
          if (localView != null) {
            break label246;
          }
          bool5 = bool2;
          bool4 = bool3;
        }
      }
    }
    for (;;)
    {
      i -= 1;
      bool3 = bool4;
      bool2 = bool5;
      break label89;
      bool2 = false;
      break;
      label158:
      i = this.mState.getItemCount();
      break label67;
      label169:
      bool1 = false;
      break label78;
      if (this.mGrid.getFirstVisibleIndex() == 0)
      {
        bool1 = true;
        label188:
        i1 = this.mGrid.getLastVisibleIndex();
        if (this.mState != null) {
          break label229;
        }
      }
      label229:
      for (i = getItemCount();; i = this.mState.getItemCount())
      {
        if (i1 != i - 1) {
          break label240;
        }
        bool2 = true;
        break;
        bool1 = false;
        break label188;
      }
      label240:
      bool2 = false;
      break label78;
      label246:
      bool1 = bool2;
      if (bool2)
      {
        bool1 = bool2;
        if (getViewMin(localView) < m) {
          bool1 = false;
        }
      }
      bool4 = bool3;
      bool5 = bool1;
      if (bool3)
      {
        bool4 = bool3;
        bool5 = bool1;
        if (getViewMax(localView) > n)
        {
          bool4 = false;
          bool5 = bool1;
        }
      }
    }
    label315:
    return this.mWindowAlignment.mainAxis().getSystemScrollPos(j + k, bool2, bool3);
  }
  
  private int getPrimarySystemScrollPositionOfChildMax(View paramView)
  {
    int j = getPrimarySystemScrollPosition(paramView);
    paramView = ((LayoutParams)paramView.getLayoutParams()).getAlignMultiple();
    int i = j;
    if (paramView != null)
    {
      i = j;
      if (paramView.length > 0) {
        i = j + (paramView[(paramView.length - 1)] - paramView[0]);
      }
    }
    return i;
  }
  
  private int getRowSizeSecondary(int paramInt)
  {
    if (this.mFixedRowSizeSecondary != 0) {
      return this.mFixedRowSizeSecondary;
    }
    if (this.mRowSizeSecondary == null) {
      return 0;
    }
    return this.mRowSizeSecondary[paramInt];
  }
  
  private int getSecondarySystemScrollPosition(View paramView)
  {
    int i = this.mScrollOffsetSecondary;
    int j = getViewCenterSecondary(paramView);
    int k = getPositionByView(paramView);
    k = this.mGrid.getLocation(k).row;
    boolean bool2;
    boolean bool1;
    if (!this.mReverseFlowSecondary) {
      if (k == 0)
      {
        bool2 = true;
        if (k != this.mGrid.getNumRows() - 1) {
          break label88;
        }
        bool1 = true;
      }
    }
    for (;;)
    {
      return this.mWindowAlignment.secondAxis().getSystemScrollPos(i + j, bool2, bool1);
      bool2 = false;
      break;
      label88:
      bool1 = false;
      continue;
      if (k == 0) {}
      for (bool1 = true;; bool1 = false)
      {
        if (k != this.mGrid.getNumRows() - 1) {
          break label128;
        }
        bool2 = true;
        break;
      }
      label128:
      bool2 = false;
    }
  }
  
  private int getSizeSecondary()
  {
    if (this.mReverseFlowSecondary) {}
    for (int i = 0;; i = this.mNumRows - 1) {
      return getRowStartSecondary(i) + getRowSizeSecondary(i);
    }
  }
  
  private int getViewCenter(View paramView)
  {
    if (this.mOrientation == 0) {
      return getViewCenterX(paramView);
    }
    return getViewCenterY(paramView);
  }
  
  private int getViewCenterSecondary(View paramView)
  {
    if (this.mOrientation == 0) {
      return getViewCenterY(paramView);
    }
    return getViewCenterX(paramView);
  }
  
  private int getViewCenterX(View paramView)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    return localLayoutParams.getOpticalLeft(paramView) + localLayoutParams.getAlignX();
  }
  
  private int getViewCenterY(View paramView)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    return localLayoutParams.getOpticalTop(paramView) + localLayoutParams.getAlignY();
  }
  
  private boolean gridOnRequestFocusInDescendantsAligned(RecyclerView paramRecyclerView, int paramInt, Rect paramRect)
  {
    paramRecyclerView = findViewByPosition(this.mFocusPosition);
    if (paramRecyclerView != null)
    {
      boolean bool = paramRecyclerView.requestFocus(paramInt, paramRect);
      if (!bool) {}
      return bool;
    }
    return false;
  }
  
  private boolean gridOnRequestFocusInDescendantsUnaligned(RecyclerView paramRecyclerView, int paramInt, Rect paramRect)
  {
    int j = getChildCount();
    int i;
    int k;
    int m;
    int n;
    if ((paramInt & 0x2) != 0)
    {
      i = 0;
      k = 1;
      m = this.mWindowAlignment.mainAxis().getPaddingLow();
      n = this.mWindowAlignment.mainAxis().getClientSize();
    }
    for (;;)
    {
      if (i == j) {
        break label122;
      }
      paramRecyclerView = getChildAt(i);
      if ((paramRecyclerView.getVisibility() == 0) && (getViewMin(paramRecyclerView) >= m) && (getViewMax(paramRecyclerView) <= n + m) && (paramRecyclerView.requestFocus(paramInt, paramRect)))
      {
        return true;
        i = j - 1;
        k = -1;
        j = -1;
        break;
      }
      i += k;
    }
    label122:
    return false;
  }
  
  private void initScrollController()
  {
    this.mWindowAlignment.reset();
    this.mWindowAlignment.horizontal.setSize(getWidth());
    this.mWindowAlignment.vertical.setSize(getHeight());
    this.mWindowAlignment.horizontal.setPadding(getPaddingLeft(), getPaddingRight());
    this.mWindowAlignment.vertical.setPadding(getPaddingTop(), getPaddingBottom());
    this.mSizePrimary = this.mWindowAlignment.mainAxis().getSize();
    this.mScrollOffsetPrimary = (-this.mWindowAlignment.mainAxis().getPaddingLow());
    this.mScrollOffsetSecondary = (-this.mWindowAlignment.secondAxis().getPaddingLow());
  }
  
  private boolean layoutInit()
  {
    int i;
    int j;
    if ((this.mGrid != null) && (this.mFocusPosition >= 0) && (this.mFocusPosition >= this.mGrid.getFirstVisibleIndex())) {
      if (this.mFocusPosition <= this.mGrid.getLastVisibleIndex())
      {
        i = 1;
        j = this.mState.getItemCount();
        if (j != 0) {
          break label265;
        }
        this.mFocusPosition = -1;
        this.mSubFocusPosition = 0;
        label66:
        if ((!this.mState.didStructureChange()) && (this.mGrid.getFirstVisibleIndex() >= 0) && (!this.mForceFullLayout)) {
          break label313;
        }
        label93:
        this.mForceFullLayout = false;
        if (i == 0) {
          break label378;
        }
        j = this.mGrid.getFirstVisibleIndex();
        label110:
        if ((this.mGrid != null) && (this.mNumRows == this.mGrid.getNumRows())) {
          break label383;
        }
      }
    }
    for (;;)
    {
      this.mGrid = Grid.createGrid(this.mNumRows);
      this.mGrid.setProvider(this.mGridProvider);
      this.mGrid.setReversedFlow(this.mReverseFlowPrimary);
      label265:
      label313:
      label378:
      label383:
      do
      {
        initScrollController();
        updateScrollSecondAxis();
        this.mGrid.setMargin(this.mMarginPrimary);
        detachAndScrapAttachedViews(this.mRecycler);
        this.mGrid.resetVisibleIndex();
        if (this.mFocusPosition == -1) {
          this.mBaseGridView.clearFocus();
        }
        this.mWindowAlignment.mainAxis().invalidateScrollMin();
        this.mWindowAlignment.mainAxis().invalidateScrollMax();
        if ((i == 0) || (j > this.mFocusPosition)) {
          break label400;
        }
        this.mGrid.setStart(j);
        return false;
        i = 0;
        break;
        i = 0;
        break;
        if (this.mFocusPosition >= j)
        {
          this.mFocusPosition = (j - 1);
          this.mSubFocusPosition = 0;
          break label66;
        }
        if ((this.mFocusPosition != -1) || (j <= 0)) {
          break label66;
        }
        this.mFocusPosition = 0;
        this.mSubFocusPosition = 0;
        break label66;
        if ((this.mGrid == null) || (this.mGrid.getNumRows() != this.mNumRows)) {
          break label93;
        }
        updateScrollController();
        updateScrollSecondAxis();
        this.mGrid.setMargin(this.mMarginPrimary);
        if ((i == 0) && (this.mFocusPosition != -1)) {
          this.mGrid.setStart(this.mFocusPosition);
        }
        return true;
        j = 0;
        break label110;
      } while (this.mReverseFlowPrimary == this.mGrid.isReversedFlow());
    }
    label400:
    this.mGrid.setStart(this.mFocusPosition);
    return false;
  }
  
  private void leaveContext()
  {
    this.mRecycler = null;
    this.mState = null;
  }
  
  private void measureScrapChild(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt)
  {
    View localView = this.mRecycler.getViewForPosition(paramInt1);
    if (localView != null)
    {
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      calculateItemDecorationsForChild(localView, sTempRect);
      paramInt1 = localLayoutParams.leftMargin;
      int i = localLayoutParams.rightMargin;
      int j = sTempRect.left;
      int k = sTempRect.right;
      int m = localLayoutParams.topMargin;
      int n = localLayoutParams.bottomMargin;
      int i1 = sTempRect.top;
      int i2 = sTempRect.bottom;
      localView.measure(ViewGroup.getChildMeasureSpec(paramInt2, getPaddingLeft() + getPaddingRight() + (paramInt1 + i + j + k), localLayoutParams.width), ViewGroup.getChildMeasureSpec(paramInt3, getPaddingTop() + getPaddingBottom() + (m + n + i1 + i2), localLayoutParams.height));
      paramArrayOfInt[0] = getDecoratedMeasuredWidthWithMargin(localView);
      paramArrayOfInt[1] = getDecoratedMeasuredHeightWithMargin(localView);
      this.mRecycler.recycleView(localView);
    }
  }
  
  private void offsetChildrenPrimary(int paramInt)
  {
    int j = getChildCount();
    if (this.mOrientation == 1)
    {
      i = 0;
      while (i < j)
      {
        getChildAt(i).offsetTopAndBottom(paramInt);
        i += 1;
      }
    }
    int i = 0;
    while (i < j)
    {
      getChildAt(i).offsetLeftAndRight(paramInt);
      i += 1;
    }
  }
  
  private void offsetChildrenSecondary(int paramInt)
  {
    int j = getChildCount();
    if (this.mOrientation == 0)
    {
      i = 0;
      while (i < j)
      {
        getChildAt(i).offsetTopAndBottom(paramInt);
        i += 1;
      }
    }
    int i = 0;
    while (i < j)
    {
      getChildAt(i).offsetLeftAndRight(paramInt);
      i += 1;
    }
  }
  
  private boolean prependOneColumnVisibleItems()
  {
    return this.mGrid.prependOneColumnVisibleItems();
  }
  
  private void prependVisibleItems()
  {
    Grid localGrid = this.mGrid;
    if (this.mReverseFlowPrimary) {}
    for (int i = this.mSizePrimary + this.mExtraLayoutSpace;; i = -this.mExtraLayoutSpace)
    {
      localGrid.prependVisibleItems(i);
      return;
    }
  }
  
  private void processPendingMovement(boolean paramBoolean)
  {
    boolean bool2 = true;
    if (paramBoolean) {}
    for (boolean bool1 = hasCreatedLastItem(); bool1; bool1 = hasCreatedFirstItem()) {
      return;
    }
    if (this.mPendingMoveSmoothScroller == null)
    {
      this.mBaseGridView.stopScroll();
      int i;
      if (paramBoolean)
      {
        i = 1;
        if (this.mNumRows <= 1) {
          break label99;
        }
      }
      label99:
      for (paramBoolean = bool2;; paramBoolean = false)
      {
        PendingMoveSmoothScroller localPendingMoveSmoothScroller = new PendingMoveSmoothScroller(i, paramBoolean);
        this.mFocusPositionOffset = 0;
        startSmoothScroll(localPendingMoveSmoothScroller);
        if (localPendingMoveSmoothScroller.isRunning()) {
          this.mPendingMoveSmoothScroller = localPendingMoveSmoothScroller;
        }
        return;
        i = -1;
        break;
      }
    }
    if (paramBoolean)
    {
      this.mPendingMoveSmoothScroller.increasePendingMoves();
      return;
    }
    this.mPendingMoveSmoothScroller.decreasePendingMoves();
  }
  
  private boolean processRowSizeSecondary(boolean paramBoolean)
  {
    if ((this.mFixedRowSizeSecondary != 0) || (this.mRowSizeSecondary == null)) {
      return false;
    }
    CircularIntArray[] arrayOfCircularIntArray;
    boolean bool;
    int m;
    int n;
    int i1;
    CircularIntArray localCircularIntArray;
    label55:
    label62:
    int i;
    if (this.mGrid == null)
    {
      arrayOfCircularIntArray = null;
      bool = false;
      m = -1;
      n = -1;
      i1 = 0;
      if (i1 >= this.mNumRows) {
        break label459;
      }
      if (arrayOfCircularIntArray != null) {
        break label145;
      }
      localCircularIntArray = null;
      if (localCircularIntArray != null) {
        break label155;
      }
      j = 0;
      i = -1;
      k = 0;
    }
    for (;;)
    {
      if (k >= j) {
        break label226;
      }
      i3 = localCircularIntArray.get(k);
      int i5 = localCircularIntArray.get(k + 1);
      i2 = i;
      for (;;)
      {
        if (i3 <= i5)
        {
          View localView = findViewByPosition(i3);
          int i4;
          if (localView == null)
          {
            i4 = i2;
            i3 += 1;
            i2 = i4;
            continue;
            arrayOfCircularIntArray = this.mGrid.getItemPositionsInRows();
            break;
            label145:
            localCircularIntArray = arrayOfCircularIntArray[i1];
            break label55;
            label155:
            j = localCircularIntArray.size();
            break label62;
          }
          if (paramBoolean) {
            measureChild(localView);
          }
          if (this.mOrientation == 0) {}
          for (i = getDecoratedMeasuredHeightWithMargin(localView);; i = getDecoratedMeasuredWidthWithMargin(localView))
          {
            i4 = i2;
            if (i <= i2) {
              break;
            }
            i4 = i;
            break;
          }
        }
      }
      k += 2;
      i = i2;
    }
    label226:
    int i3 = this.mState.getItemCount();
    int j = i;
    int k = n;
    int i2 = m;
    if (!this.mBaseGridView.hasFixedSize())
    {
      j = i;
      k = n;
      i2 = m;
      if (paramBoolean)
      {
        j = i;
        k = n;
        i2 = m;
        if (i < 0)
        {
          j = i;
          k = n;
          i2 = m;
          if (i3 > 0)
          {
            k = n;
            i = m;
            if (m < 0)
            {
              k = n;
              i = m;
              if (n < 0)
              {
                if (this.mFocusPosition != -1) {
                  break label426;
                }
                i = 0;
                label332:
                measureScrapChild(i, View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0), this.mMeasuredDimension);
                i = this.mMeasuredDimension[0];
                k = this.mMeasuredDimension[1];
              }
            }
            if (this.mOrientation != 0) {
              break label451;
            }
            j = k;
          }
        }
      }
    }
    for (i2 = i;; i2 = i)
    {
      i = j;
      if (j < 0) {
        i = 0;
      }
      if (this.mRowSizeSecondary[i1] != i)
      {
        this.mRowSizeSecondary[i1] = i;
        bool = true;
      }
      i1 += 1;
      n = k;
      m = i2;
      break;
      label426:
      if (this.mFocusPosition >= i3)
      {
        i = i3 - 1;
        break label332;
      }
      i = this.mFocusPosition;
      break label332;
      label451:
      j = i;
    }
    label459:
    return bool;
  }
  
  private void removeInvisibleViewsAtEnd()
  {
    Grid localGrid;
    int j;
    if (this.mPruneChild)
    {
      localGrid = this.mGrid;
      j = this.mFocusPosition;
      if (!this.mReverseFlowPrimary) {
        break label37;
      }
    }
    label37:
    for (int i = -this.mExtraLayoutSpace;; i = this.mSizePrimary + this.mExtraLayoutSpace)
    {
      localGrid.removeInvisibleItemsAtEnd(j, i);
      return;
    }
  }
  
  private void removeInvisibleViewsAtFront()
  {
    Grid localGrid;
    int j;
    if (this.mPruneChild)
    {
      localGrid = this.mGrid;
      j = this.mFocusPosition;
      if (!this.mReverseFlowPrimary) {
        break label41;
      }
    }
    label41:
    for (int i = this.mSizePrimary + this.mExtraLayoutSpace;; i = -this.mExtraLayoutSpace)
    {
      localGrid.removeInvisibleItemsAtFront(j, i);
      return;
    }
  }
  
  private void saveContext(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if ((this.mRecycler != null) || (this.mState != null)) {
      Log.e("GridLayoutManager", "Recycler information was not released, bug!");
    }
    this.mRecycler = paramRecycler;
    this.mState = paramState;
  }
  
  private int scrollDirectionPrimary(int paramInt)
  {
    int j = 0;
    int i;
    int k;
    if (paramInt > 0)
    {
      i = paramInt;
      if (!this.mWindowAlignment.mainAxis().isMaxUnknown())
      {
        k = this.mWindowAlignment.mainAxis().getMaxScroll();
        i = paramInt;
        if (this.mScrollOffsetPrimary + paramInt > k) {
          i = k - this.mScrollOffsetPrimary;
        }
      }
    }
    while (i == 0)
    {
      return 0;
      i = paramInt;
      if (paramInt < 0)
      {
        i = paramInt;
        if (!this.mWindowAlignment.mainAxis().isMinUnknown())
        {
          k = this.mWindowAlignment.mainAxis().getMinScroll();
          i = paramInt;
          if (this.mScrollOffsetPrimary + paramInt < k) {
            i = k - this.mScrollOffsetPrimary;
          }
        }
      }
    }
    offsetChildrenPrimary(-i);
    this.mScrollOffsetPrimary += i;
    if (this.mInLayout) {
      return i;
    }
    paramInt = getChildCount();
    if (this.mReverseFlowPrimary)
    {
      if (i <= 0) {
        break label227;
      }
      prependVisibleItems();
      label162:
      if (getChildCount() <= paramInt) {
        break label234;
      }
      paramInt = 1;
      label172:
      k = getChildCount();
      if (!this.mReverseFlowPrimary) {
        break label239;
      }
      if (i <= 0) {
        break label243;
      }
      label189:
      removeInvisibleViewsAtEnd();
    }
    for (;;)
    {
      if (getChildCount() < k) {
        j = 1;
      }
      if ((paramInt | j) != 0) {
        updateRowSecondarySizeRefresh();
      }
      this.mBaseGridView.invalidate();
      return i;
      if (i < 0) {
        break;
      }
      label227:
      appendVisibleItems();
      break label162;
      label234:
      paramInt = 0;
      break label172;
      label239:
      if (i < 0) {
        break label189;
      }
      label243:
      removeInvisibleViewsAtFront();
    }
  }
  
  private int scrollDirectionSecondary(int paramInt)
  {
    if (paramInt == 0) {
      return 0;
    }
    offsetChildrenSecondary(-paramInt);
    this.mScrollOffsetSecondary += paramInt;
    this.mBaseGridView.invalidate();
    return paramInt;
  }
  
  private void scrollGrid(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (this.mInLayout)
    {
      scrollDirectionPrimary(paramInt1);
      scrollDirectionSecondary(paramInt2);
      return;
    }
    if (this.mOrientation == 0) {}
    while (paramBoolean)
    {
      this.mBaseGridView.smoothScrollBy(paramInt1, paramInt2);
      return;
      int i = paramInt1;
      paramInt1 = paramInt2;
      paramInt2 = i;
    }
    this.mBaseGridView.scrollBy(paramInt1, paramInt2);
  }
  
  private void scrollToFocusViewInLayout(boolean paramBoolean1, boolean paramBoolean2)
  {
    View localView = findViewByPosition(this.mFocusPosition);
    if ((localView != null) && (paramBoolean2)) {
      scrollToView(localView, false);
    }
    if ((localView == null) || (!paramBoolean1) || (localView.hasFocus()))
    {
      if ((!paramBoolean1) && (!this.mBaseGridView.hasFocus())) {}
    }
    else
    {
      localView.requestFocus();
      return;
    }
    if ((localView != null) && (localView.hasFocusable()))
    {
      this.mBaseGridView.focusableViewAvailable(localView);
      return;
    }
    int i = 0;
    int j = getChildCount();
    for (;;)
    {
      if (i < j)
      {
        localView = getChildAt(i);
        if ((localView != null) && (localView.hasFocusable())) {
          this.mBaseGridView.focusableViewAvailable(localView);
        }
      }
      else
      {
        if ((!paramBoolean2) || (localView == null) || (!localView.hasFocus())) {
          break;
        }
        scrollToView(localView, false);
        return;
      }
      i += 1;
    }
  }
  
  private void scrollToView(View paramView1, View paramView2, boolean paramBoolean)
  {
    int i = getPositionByView(paramView1);
    int j = getSubPositionByView(paramView1, paramView2);
    if ((i != this.mFocusPosition) || (j != this.mSubFocusPosition))
    {
      this.mFocusPosition = i;
      this.mSubFocusPosition = j;
      this.mFocusPositionOffset = 0;
      if (!this.mInLayout) {
        dispatchChildSelected();
      }
      if (this.mBaseGridView.isChildrenDrawingOrderEnabledInternal()) {
        this.mBaseGridView.invalidate();
      }
    }
    if (paramView1 == null) {
      return;
    }
    if ((!paramView1.hasFocus()) && (this.mBaseGridView.hasFocus())) {
      paramView1.requestFocus();
    }
    if ((!this.mScrollEnabled) && (paramBoolean)) {
      return;
    }
    if (getScrollPosition(paramView1, paramView2, sTwoInts)) {
      scrollGrid(sTwoInts[0], sTwoInts[1], paramBoolean);
    }
  }
  
  private void updateChildAlignments(View paramView)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (localLayoutParams.getItemAlignmentFacet() == null)
    {
      localLayoutParams.setAlignX(this.mItemAlignment.horizontal.getAlignmentPosition(paramView));
      localLayoutParams.setAlignY(this.mItemAlignment.vertical.getAlignmentPosition(paramView));
      return;
    }
    localLayoutParams.calculateItemAlignments(this.mOrientation, paramView);
    if (this.mOrientation == 0)
    {
      localLayoutParams.setAlignY(this.mItemAlignment.vertical.getAlignmentPosition(paramView));
      return;
    }
    localLayoutParams.setAlignX(this.mItemAlignment.horizontal.getAlignmentPosition(paramView));
  }
  
  private void updateRowSecondarySizeRefresh()
  {
    this.mRowSecondarySizeRefresh = processRowSizeSecondary(false);
    if (this.mRowSecondarySizeRefresh) {
      forceRequestLayout();
    }
  }
  
  private void updateScrollController()
  {
    int i;
    if (this.mOrientation == 0) {
      i = getPaddingLeft() - this.mWindowAlignment.horizontal.getPaddingLow();
    }
    for (int j = getPaddingTop() - this.mWindowAlignment.vertical.getPaddingLow();; j = getPaddingLeft() - this.mWindowAlignment.horizontal.getPaddingLow())
    {
      this.mScrollOffsetPrimary -= i;
      this.mScrollOffsetSecondary -= j;
      this.mWindowAlignment.horizontal.setSize(getWidth());
      this.mWindowAlignment.vertical.setSize(getHeight());
      this.mWindowAlignment.horizontal.setPadding(getPaddingLeft(), getPaddingRight());
      this.mWindowAlignment.vertical.setPadding(getPaddingTop(), getPaddingBottom());
      this.mSizePrimary = this.mWindowAlignment.mainAxis().getSize();
      return;
      i = getPaddingTop() - this.mWindowAlignment.vertical.getPaddingLow();
    }
  }
  
  private void updateScrollSecondAxis()
  {
    this.mWindowAlignment.secondAxis().setMinEdge(0);
    this.mWindowAlignment.secondAxis().setMaxEdge(getSizeSecondary());
  }
  
  public boolean canScrollHorizontally()
  {
    return (this.mOrientation == 0) || (this.mNumRows > 1);
  }
  
  boolean canScrollTo(View paramView)
  {
    boolean bool = false;
    if (paramView.getVisibility() == 0)
    {
      if (hasFocus()) {
        bool = paramView.hasFocusable();
      }
    }
    else {
      return bool;
    }
    return true;
  }
  
  public boolean canScrollVertically()
  {
    return (this.mOrientation == 1) || (this.mNumRows > 1);
  }
  
  void dispatchChildSelected()
  {
    View localView;
    RecyclerView.ViewHolder localViewHolder;
    int i;
    long l;
    if ((this.mChildSelectedListener != null) || (hasOnChildViewHolderSelectedListener()))
    {
      if (this.mFocusPosition != -1) {
        break label124;
      }
      localView = null;
      if (localView == null) {
        break label146;
      }
      localViewHolder = this.mBaseGridView.getChildViewHolder(localView);
      if (this.mChildSelectedListener != null)
      {
        OnChildSelectedListener localOnChildSelectedListener = this.mChildSelectedListener;
        BaseGridView localBaseGridView = this.mBaseGridView;
        i = this.mFocusPosition;
        if (localViewHolder != null) {
          break label137;
        }
        l = -1L;
        label74:
        localOnChildSelectedListener.onChildSelected(localBaseGridView, localView, i, l);
      }
      fireOnChildViewHolderSelected(this.mBaseGridView, localViewHolder, this.mFocusPosition, this.mSubFocusPosition);
      label105:
      if ((!this.mInLayout) && (!this.mBaseGridView.isLayoutRequested())) {
        break label185;
      }
    }
    for (;;)
    {
      return;
      return;
      label124:
      localView = findViewByPosition(this.mFocusPosition);
      break;
      label137:
      l = localViewHolder.getItemId();
      break label74;
      label146:
      if (this.mChildSelectedListener != null) {
        this.mChildSelectedListener.onChildSelected(this.mBaseGridView, null, -1, -1L);
      }
      fireOnChildViewHolderSelected(this.mBaseGridView, null, -1, 0);
      break label105;
      label185:
      int j = getChildCount();
      i = 0;
      while (i < j)
      {
        if (getChildAt(i).isLayoutRequested())
        {
          forceRequestLayout();
          return;
        }
        i += 1;
      }
    }
  }
  
  void fireOnChildViewHolderSelected(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2)
  {
    if (this.mChildViewHolderSelectedListeners == null) {
      return;
    }
    int i = this.mChildViewHolderSelectedListeners.size() - 1;
    while (i >= 0)
    {
      ((OnChildViewHolderSelectedListener)this.mChildViewHolderSelectedListeners.get(i)).onChildViewHolderSelected(paramRecyclerView, paramViewHolder, paramInt1, paramInt2);
      i -= 1;
    }
  }
  
  public RecyclerView.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-2, -2);
  }
  
  public RecyclerView.LayoutParams generateLayoutParams(Context paramContext, AttributeSet paramAttributeSet)
  {
    return new LayoutParams(paramContext, paramAttributeSet);
  }
  
  public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof LayoutParams)) {
      return new LayoutParams((LayoutParams)paramLayoutParams);
    }
    if ((paramLayoutParams instanceof RecyclerView.LayoutParams)) {
      return new LayoutParams((RecyclerView.LayoutParams)paramLayoutParams);
    }
    if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
      return new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
    }
    return new LayoutParams(paramLayoutParams);
  }
  
  int getChildDrawingOrder(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    View localView = findViewByPosition(this.mFocusPosition);
    if (localView == null) {
      return paramInt2;
    }
    int i = paramRecyclerView.indexOfChild(localView);
    if (paramInt2 < i) {
      return paramInt2;
    }
    if (paramInt2 < paramInt1 - 1) {
      return i + paramInt1 - 1 - paramInt2;
    }
    return i;
  }
  
  public int getColumnCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if ((this.mOrientation == 1) && (this.mGrid != null)) {
      return this.mGrid.getNumRows();
    }
    return super.getColumnCountForAccessibility(paramRecycler, paramState);
  }
  
  public int getDecoratedBottom(View paramView)
  {
    return super.getDecoratedBottom(paramView) - ((LayoutParams)paramView.getLayoutParams()).mBottomInset;
  }
  
  public void getDecoratedBoundsWithMargins(View paramView, Rect paramRect)
  {
    super.getDecoratedBoundsWithMargins(paramView, paramRect);
    paramView = (LayoutParams)paramView.getLayoutParams();
    paramRect.left += paramView.mLeftInset;
    paramRect.top += paramView.mTopInset;
    paramRect.right -= paramView.mRightInset;
    paramRect.bottom -= paramView.mBottomInset;
  }
  
  public int getDecoratedLeft(View paramView)
  {
    int i = super.getDecoratedLeft(paramView);
    return ((LayoutParams)paramView.getLayoutParams()).mLeftInset + i;
  }
  
  int getDecoratedMeasuredHeightWithMargin(View paramView)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    return getDecoratedMeasuredHeight(paramView) + localLayoutParams.topMargin + localLayoutParams.bottomMargin;
  }
  
  int getDecoratedMeasuredWidthWithMargin(View paramView)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    return getDecoratedMeasuredWidth(paramView) + localLayoutParams.leftMargin + localLayoutParams.rightMargin;
  }
  
  public int getDecoratedRight(View paramView)
  {
    return super.getDecoratedRight(paramView) - ((LayoutParams)paramView.getLayoutParams()).mRightInset;
  }
  
  public int getDecoratedTop(View paramView)
  {
    int i = super.getDecoratedTop(paramView);
    return ((LayoutParams)paramView.getLayoutParams()).mTopInset + i;
  }
  
  <E> E getFacet(RecyclerView.ViewHolder paramViewHolder, Class<? extends E> paramClass)
  {
    Object localObject1 = null;
    if ((paramViewHolder instanceof FacetProvider)) {
      localObject1 = ((FacetProvider)paramViewHolder).getFacet(paramClass);
    }
    Object localObject2 = localObject1;
    if (localObject1 == null)
    {
      localObject2 = localObject1;
      if (this.mFacetProviderAdapter != null)
      {
        paramViewHolder = this.mFacetProviderAdapter.getFacetProvider(paramViewHolder.getItemViewType());
        localObject2 = localObject1;
        if (paramViewHolder != null) {
          localObject2 = paramViewHolder.getFacet(paramClass);
        }
      }
    }
    return (E)localObject2;
  }
  
  final int getOpticalLeft(View paramView)
  {
    return ((LayoutParams)paramView.getLayoutParams()).getOpticalLeft(paramView);
  }
  
  final int getOpticalRight(View paramView)
  {
    return ((LayoutParams)paramView.getLayoutParams()).getOpticalRight(paramView);
  }
  
  public int getRowCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if ((this.mOrientation == 0) && (this.mGrid != null)) {
      return this.mGrid.getNumRows();
    }
    return super.getRowCountForAccessibility(paramRecycler, paramState);
  }
  
  int getRowStartSecondary(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (this.mReverseFlowSecondary)
    {
      k = this.mNumRows - 1;
      for (;;)
      {
        j = i;
        if (k <= paramInt) {
          break;
        }
        i += getRowSizeSecondary(k) + this.mMarginSecondary;
        k -= 1;
      }
    }
    int k = 0;
    i = j;
    for (;;)
    {
      j = i;
      if (k >= paramInt) {
        break;
      }
      i += getRowSizeSecondary(k) + this.mMarginSecondary;
      k += 1;
    }
    return j;
  }
  
  boolean getScrollPosition(View paramView1, View paramView2, int[] paramArrayOfInt)
  {
    switch (this.mFocusScrollStrategy)
    {
    default: 
      return getAlignedPosition(paramView1, paramView2, paramArrayOfInt);
    }
    return getNoneAlignedPosition(paramView1, paramArrayOfInt);
  }
  
  public int getSelection()
  {
    return this.mFocusPosition;
  }
  
  int getSubPositionByView(View paramView1, View paramView2)
  {
    if ((paramView1 == null) || (paramView2 == null)) {
      return 0;
    }
    Object localObject = ((LayoutParams)paramView1.getLayoutParams()).getItemAlignmentFacet();
    if (localObject != null)
    {
      localObject = ((ItemAlignmentFacet)localObject).getAlignmentDefs();
      if (localObject.length > 1) {
        while (paramView2 != paramView1)
        {
          int j = paramView2.getId();
          if (j != -1)
          {
            int i = 1;
            while (i < localObject.length)
            {
              if (localObject[i].getItemAlignmentFocusViewId() == j) {
                return i;
              }
              i += 1;
            }
          }
          paramView2 = (View)paramView2.getParent();
        }
      }
    }
    return 0;
  }
  
  String getTag()
  {
    return "GridLayoutManager:" + this.mBaseGridView.getId();
  }
  
  protected View getViewForPosition(int paramInt)
  {
    return this.mRecycler.getViewForPosition(paramInt);
  }
  
  int getViewMax(View paramView)
  {
    return this.mOrientationHelper.getDecoratedEnd(paramView);
  }
  
  int getViewMin(View paramView)
  {
    return this.mOrientationHelper.getDecoratedStart(paramView);
  }
  
  int getViewPrimarySize(View paramView)
  {
    getDecoratedBoundsWithMargins(paramView, sTempRect);
    if (this.mOrientation == 0) {
      return sTempRect.width();
    }
    return sTempRect.height();
  }
  
  boolean gridOnRequestFocusInDescendants(RecyclerView paramRecyclerView, int paramInt, Rect paramRect)
  {
    switch (this.mFocusScrollStrategy)
    {
    default: 
      return gridOnRequestFocusInDescendantsAligned(paramRecyclerView, paramInt, paramRect);
    }
    return gridOnRequestFocusInDescendantsUnaligned(paramRecyclerView, paramInt, paramRect);
  }
  
  boolean hasCreatedFirstItem()
  {
    return (getItemCount() == 0) || (this.mBaseGridView.findViewHolderForAdapterPosition(0) != null);
  }
  
  boolean hasCreatedLastItem()
  {
    int i = getItemCount();
    return (i == 0) || (this.mBaseGridView.findViewHolderForAdapterPosition(i - 1) != null);
  }
  
  protected boolean hasDoneFirstLayout()
  {
    return this.mGrid != null;
  }
  
  boolean hasOnChildViewHolderSelectedListener()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mChildViewHolderSelectedListeners != null)
    {
      bool1 = bool2;
      if (this.mChildViewHolderSelectedListeners.size() > 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  void layoutChild(int paramInt1, View paramView, int paramInt2, int paramInt3, int paramInt4)
  {
    int i;
    int j;
    int m;
    int k;
    if (this.mOrientation == 0)
    {
      i = getDecoratedMeasuredHeightWithMargin(paramView);
      j = i;
      if (this.mFixedRowSizeSecondary > 0) {
        j = Math.min(i, this.mFixedRowSizeSecondary);
      }
      m = this.mGravity & 0x70;
      if ((!this.mReverseFlowPrimary) && (!this.mReverseFlowSecondary)) {
        break label202;
      }
      k = Gravity.getAbsoluteGravity(this.mGravity & 0x800007, 1);
      label73:
      if ((this.mOrientation != 0) || (m != 48)) {
        break label214;
      }
      i = paramInt4;
      label91:
      if (this.mOrientation != 0) {
        break label333;
      }
      paramInt1 = i;
      j = i + j;
      i = paramInt3;
      paramInt4 = paramInt1;
      paramInt1 = paramInt2;
    }
    for (;;)
    {
      LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
      layoutDecoratedWithMargins(paramView, paramInt1, paramInt4, i, j);
      super.getDecoratedBoundsWithMargins(paramView, sTempRect);
      localLayoutParams.setOpticalInsets(paramInt1 - sTempRect.left, paramInt4 - sTempRect.top, sTempRect.right - i, sTempRect.bottom - j);
      updateChildAlignments(paramView);
      return;
      i = getDecoratedMeasuredWidthWithMargin(paramView);
      break;
      label202:
      k = this.mGravity & 0x7;
      break label73;
      label214:
      if (this.mOrientation == 1)
      {
        i = paramInt4;
        if (k == 3) {
          break label91;
        }
      }
      if ((this.mOrientation == 0) && (m == 80)) {}
      while ((this.mOrientation == 1) && (k == 5))
      {
        i = paramInt4 + (getRowSizeSecondary(paramInt1) - j);
        break;
      }
      if ((this.mOrientation == 0) && (m == 16)) {}
      for (;;)
      {
        i = paramInt4 + (getRowSizeSecondary(paramInt1) - j) / 2;
        break;
        i = paramInt4;
        if (this.mOrientation != 1) {
          break;
        }
        i = paramInt4;
        if (k != 1) {
          break;
        }
      }
      label333:
      paramInt1 = i;
      i += j;
      paramInt4 = paramInt2;
      j = paramInt3;
    }
  }
  
  void measureChild(View paramView)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    calculateItemDecorationsForChild(paramView, sTempRect);
    int k = localLayoutParams.leftMargin + localLayoutParams.rightMargin + sTempRect.left + sTempRect.right;
    int m = localLayoutParams.topMargin + localLayoutParams.bottomMargin + sTempRect.top + sTempRect.bottom;
    int i;
    int j;
    if (this.mRowSizeSecondaryRequested == -2)
    {
      i = View.MeasureSpec.makeMeasureSpec(0, 0);
      if (this.mOrientation != 0) {
        break label142;
      }
      j = ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(0, 0), k, localLayoutParams.width);
      i = ViewGroup.getChildMeasureSpec(i, m, localLayoutParams.height);
    }
    for (;;)
    {
      paramView.measure(j, i);
      return;
      i = View.MeasureSpec.makeMeasureSpec(this.mFixedRowSizeSecondary, 1073741824);
      break;
      label142:
      j = ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(0, 0), m, localLayoutParams.height);
      k = ViewGroup.getChildMeasureSpec(i, k, localLayoutParams.width);
      i = j;
      j = k;
    }
  }
  
  public void onAdapterChanged(RecyclerView.Adapter paramAdapter1, RecyclerView.Adapter paramAdapter2)
  {
    if (paramAdapter1 != null)
    {
      discardLayoutInfo();
      this.mFocusPosition = -1;
      this.mFocusPositionOffset = 0;
      this.mChildrenStates.clear();
    }
    if ((paramAdapter2 instanceof FacetProviderAdapter)) {}
    for (this.mFacetProviderAdapter = ((FacetProviderAdapter)paramAdapter2);; this.mFacetProviderAdapter = null)
    {
      super.onAdapterChanged(paramAdapter1, paramAdapter2);
      return;
    }
  }
  
  public boolean onAddFocusables(RecyclerView paramRecyclerView, ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
  {
    if (this.mFocusSearchDisabled) {
      return true;
    }
    int n;
    int i;
    int i1;
    int j;
    int i2;
    int k;
    label158:
    int m;
    if (paramRecyclerView.hasFocus())
    {
      if (this.mPendingMoveSmoothScroller != null) {
        return true;
      }
      n = getMovement(paramInt1);
      i = findImmediateChildIndex(paramRecyclerView.findFocus());
      i1 = getPositionByIndex(i);
      if (i1 != -1) {
        findViewByPosition(i1).addFocusables(paramArrayList, paramInt1, paramInt2);
      }
      if ((this.mGrid == null) || (getChildCount() == 0)) {
        return true;
      }
      if (((n == 3) || (n == 2)) && (this.mGrid.getNumRows() <= 1)) {
        return true;
      }
      if ((this.mGrid != null) && (i1 != -1))
      {
        j = this.mGrid.getLocation(i1).row;
        i2 = paramArrayList.size();
        if ((n != 1) && (n != 3)) {
          break label249;
        }
        k = 1;
        if (k <= 0) {
          break label255;
        }
        m = getChildCount() - 1;
        label171:
        if (i != -1) {
          break label272;
        }
        if (k <= 0) {
          break label261;
        }
        i = 0;
        label185:
        if (k <= 0) {
          break label282;
        }
        if (i <= m)
        {
          label197:
          paramRecyclerView = getChildAt(i);
          if ((paramRecyclerView.getVisibility() != 0) || (!paramRecyclerView.hasFocusable())) {
            break label316;
          }
          if (i1 != -1) {
            break label292;
          }
          paramRecyclerView.addFocusables(paramArrayList, paramInt1, paramInt2);
          if (paramArrayList.size() <= i2) {
            break label316;
          }
        }
      }
    }
    for (;;)
    {
      label241:
      return true;
      j = -1;
      break;
      label249:
      k = -1;
      break label158;
      label255:
      m = 0;
      break label171;
      label261:
      i = getChildCount() - 1;
      break label185;
      label272:
      i += k;
      break label185;
      label282:
      if (i >= m)
      {
        break label197;
        label292:
        int i3 = getPositionByIndex(i);
        Object localObject = this.mGrid.getLocation(i3);
        if (localObject == null) {}
        for (;;)
        {
          label316:
          i += k;
          break;
          if (n == 1)
          {
            if ((((Grid.Location)localObject).row != j) || (i3 <= i1)) {
              continue;
            }
            paramRecyclerView.addFocusables(paramArrayList, paramInt1, paramInt2);
            if (paramArrayList.size() <= i2) {
              continue;
            }
            break label241;
          }
          if (n == 0)
          {
            if ((((Grid.Location)localObject).row != j) || (i3 >= i1)) {
              continue;
            }
            paramRecyclerView.addFocusables(paramArrayList, paramInt1, paramInt2);
            if (paramArrayList.size() <= i2) {
              continue;
            }
            break label241;
          }
          if (n == 3)
          {
            if (((Grid.Location)localObject).row == j) {
              continue;
            }
            if (((Grid.Location)localObject).row < j) {
              break label241;
            }
            paramRecyclerView.addFocusables(paramArrayList, paramInt1, paramInt2);
            continue;
          }
          if ((n == 2) && (((Grid.Location)localObject).row != j))
          {
            if (((Grid.Location)localObject).row > j) {
              break label241;
            }
            paramRecyclerView.addFocusables(paramArrayList, paramInt1, paramInt2);
          }
        }
        j = paramArrayList.size();
        if (this.mFocusScrollStrategy != 0)
        {
          k = this.mWindowAlignment.mainAxis().getPaddingLow();
          m = this.mWindowAlignment.mainAxis().getClientSize();
          i = 0;
          n = getChildCount();
          while (i < n)
          {
            localObject = getChildAt(i);
            if ((((View)localObject).getVisibility() == 0) && (getViewMin((View)localObject) >= k) && (getViewMax((View)localObject) <= m + k)) {
              ((View)localObject).addFocusables(paramArrayList, paramInt1, paramInt2);
            }
            i += 1;
          }
          if (paramArrayList.size() == j)
          {
            i = 0;
            k = getChildCount();
            while (i < k)
            {
              localObject = getChildAt(i);
              if (((View)localObject).getVisibility() == 0) {
                ((View)localObject).addFocusables(paramArrayList, paramInt1, paramInt2);
              }
              i += 1;
            }
          }
        }
        else
        {
          localObject = findViewByPosition(this.mFocusPosition);
          if (localObject != null) {
            ((View)localObject).addFocusables(paramArrayList, paramInt1, paramInt2);
          }
        }
        if (paramArrayList.size() != j) {
          return true;
        }
        if (paramRecyclerView.isFocusable()) {
          paramArrayList.add(paramRecyclerView);
        }
      }
    }
  }
  
  void onChildRecycled(RecyclerView.ViewHolder paramViewHolder)
  {
    int i = paramViewHolder.getAdapterPosition();
    if (i != -1) {
      this.mChildrenStates.saveOffscreenView(paramViewHolder.itemView, i);
    }
  }
  
  void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
  {
    if (paramBoolean) {
      paramInt = this.mFocusPosition;
    }
    for (;;)
    {
      paramRect = findViewByPosition(paramInt);
      if (paramRect == null) {
        return;
      }
      if ((paramRect.getVisibility() == 0) && (paramRect.hasFocusable()))
      {
        paramRect.requestFocus();
        return;
      }
      paramInt += 1;
    }
  }
  
  public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    saveContext(paramRecycler, paramState);
    if ((!this.mScrollEnabled) || (hasCreatedFirstItem())) {
      if ((this.mScrollEnabled) && (!hasCreatedLastItem())) {
        break label85;
      }
    }
    for (;;)
    {
      paramAccessibilityNodeInfoCompat.setCollectionInfo(AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(getRowCountForAccessibility(paramRecycler, paramState), getColumnCountForAccessibility(paramRecycler, paramState), isLayoutHierarchical(paramRecycler, paramState), getSelectionModeForAccessibility(paramRecycler, paramState)));
      leaveContext();
      return;
      paramAccessibilityNodeInfoCompat.addAction(8192);
      paramAccessibilityNodeInfoCompat.setScrollable(true);
      break;
      label85:
      paramAccessibilityNodeInfoCompat.addAction(4096);
      paramAccessibilityNodeInfoCompat.setScrollable(true);
    }
  }
  
  public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
    int j;
    int i;
    if ((this.mGrid != null) && ((localLayoutParams instanceof LayoutParams)))
    {
      j = ((LayoutParams)localLayoutParams).getViewLayoutPosition();
      i = this.mGrid.getRowIndex(j);
      j /= this.mGrid.getNumRows();
      if (this.mOrientation == 0) {
        paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(i, 1, j, 1, false, false));
      }
    }
    else
    {
      super.onInitializeAccessibilityNodeInfoForItem(paramRecycler, paramState, paramView, paramAccessibilityNodeInfoCompat);
      return;
    }
    paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(j, 1, i, 1, false, false));
  }
  
  public View onInterceptFocusSearch(View paramView, int paramInt)
  {
    if (this.mFocusSearchDisabled) {
      return paramView;
    }
    Object localObject = FocusFinder.getInstance();
    View localView = null;
    int i;
    if ((paramInt == 2) || (paramInt == 1)) {
      if (canScrollVertically())
      {
        if (paramInt == 2)
        {
          i = 130;
          localView = ((FocusFinder)localObject).findNextFocus(this.mBaseGridView, paramView, i);
        }
      }
      else if (canScrollHorizontally())
      {
        if (getLayoutDirection() != 1) {
          break label118;
        }
        i = 1;
        label73:
        if (paramInt != 2) {
          break label123;
        }
        j = 1;
        label81:
        if ((j ^ i) == 0) {
          break label129;
        }
        i = 66;
      }
    }
    label91:
    for (localView = ((FocusFinder)localObject).findNextFocus(this.mBaseGridView, paramView, i);; localView = ((FocusFinder)localObject).findNextFocus(this.mBaseGridView, paramView, paramInt))
    {
      if (localView == null) {
        break label151;
      }
      return localView;
      i = 33;
      break;
      label118:
      i = 0;
      break label73;
      label123:
      j = 0;
      break label81;
      label129:
      i = 17;
      break label91;
    }
    label151:
    int j = getMovement(paramInt);
    if (this.mBaseGridView.getScrollState() != 0)
    {
      i = 1;
      if (j != 1) {
        break label239;
      }
      if ((i != 0) || (!this.mFocusOutEnd)) {
        break label222;
      }
      label187:
      localObject = localView;
      if (this.mScrollEnabled)
      {
        if (!hasCreatedLastItem()) {
          break label228;
        }
        localObject = localView;
      }
    }
    for (;;)
    {
      if (localObject == null) {
        break label352;
      }
      return (View)localObject;
      i = 0;
      break;
      label222:
      localView = paramView;
      break label187;
      label228:
      processPendingMovement(true);
      localObject = paramView;
      continue;
      label239:
      if (j == 0)
      {
        if ((i == 0) && (this.mFocusOutFront)) {}
        for (;;)
        {
          localObject = localView;
          if (!this.mScrollEnabled) {
            break;
          }
          localObject = localView;
          if (hasCreatedFirstItem()) {
            break;
          }
          processPendingMovement(false);
          localObject = paramView;
          break;
          localView = paramView;
        }
      }
      if (j == 3)
      {
        if (i == 0)
        {
          localObject = localView;
          if (this.mFocusOutSideEnd) {}
        }
        else
        {
          localObject = paramView;
        }
      }
      else
      {
        localObject = localView;
        if (j == 2) {
          if (i == 0)
          {
            localObject = localView;
            if (this.mFocusOutSideStart) {}
          }
          else
          {
            localObject = paramView;
          }
        }
      }
    }
    label352:
    localObject = this.mBaseGridView.getParent().focusSearch(paramView, paramInt);
    if (localObject != null) {
      return (View)localObject;
    }
    if (paramView != null) {
      return paramView;
    }
    return this.mBaseGridView;
  }
  
  public void onItemsAdded(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    if ((this.mFocusPosition != -1) && (this.mGrid != null) && (this.mGrid.getFirstVisibleIndex() >= 0) && (this.mFocusPositionOffset != Integer.MIN_VALUE) && (paramInt1 <= this.mFocusPosition + this.mFocusPositionOffset)) {
      this.mFocusPositionOffset += paramInt2;
    }
    this.mChildrenStates.clear();
  }
  
  public void onItemsChanged(RecyclerView paramRecyclerView)
  {
    this.mFocusPositionOffset = 0;
    this.mChildrenStates.clear();
  }
  
  public void onItemsMoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3)
  {
    int i;
    if ((this.mFocusPosition != -1) && (this.mFocusPositionOffset != Integer.MIN_VALUE))
    {
      i = this.mFocusPosition + this.mFocusPositionOffset;
      if ((paramInt1 > i) || (i >= paramInt1 + paramInt3)) {
        break label64;
      }
      this.mFocusPositionOffset += paramInt2 - paramInt1;
    }
    for (;;)
    {
      this.mChildrenStates.clear();
      return;
      label64:
      if ((paramInt1 < i) && (paramInt2 > i - paramInt3)) {
        this.mFocusPositionOffset -= paramInt3;
      } else if ((paramInt1 > i) && (paramInt2 < i)) {
        this.mFocusPositionOffset += paramInt3;
      }
    }
  }
  
  public void onItemsRemoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    if ((this.mFocusPosition != -1) && (this.mGrid != null) && (this.mGrid.getFirstVisibleIndex() >= 0) && (this.mFocusPositionOffset != Integer.MIN_VALUE))
    {
      int i = this.mFocusPosition + this.mFocusPositionOffset;
      if (paramInt1 <= i) {
        if (paramInt1 + paramInt2 <= i) {
          break label75;
        }
      }
    }
    label75:
    for (this.mFocusPositionOffset = Integer.MIN_VALUE;; this.mFocusPositionOffset -= paramInt2)
    {
      this.mChildrenStates.clear();
      return;
    }
  }
  
  public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    while (i < paramInt1 + paramInt2)
    {
      this.mChildrenStates.remove(i);
      i += 1;
    }
  }
  
  public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mNumRows == 0) {
      return;
    }
    if (paramState.getItemCount() < 0) {
      return;
    }
    if (!this.mLayoutEnabled)
    {
      discardLayoutInfo();
      removeAndRecycleAllViews(paramRecycler);
      return;
    }
    this.mInLayout = true;
    if (paramState.didStructureChange()) {
      this.mBaseGridView.stopScroll();
    }
    boolean bool1;
    boolean bool2;
    int k;
    int m;
    if (!isSmoothScrolling()) {
      if (this.mFocusScrollStrategy == 0)
      {
        bool1 = true;
        if ((this.mFocusPosition != -1) && (this.mFocusPositionOffset != Integer.MIN_VALUE))
        {
          this.mFocusPosition += this.mFocusPositionOffset;
          this.mSubFocusPosition = 0;
        }
        this.mFocusPositionOffset = 0;
        saveContext(paramRecycler, paramState);
        paramRecycler = findViewByPosition(this.mFocusPosition);
        int n = this.mFocusPosition;
        int i1 = this.mSubFocusPosition;
        bool2 = this.mBaseGridView.hasFocus();
        k = 0;
        m = 0;
        int j = k;
        int i = m;
        if (this.mFocusPosition != -1)
        {
          j = k;
          i = m;
          if (bool1)
          {
            j = k;
            i = m;
            if (this.mBaseGridView.getScrollState() != 0)
            {
              j = k;
              i = m;
              if (paramRecycler != null)
              {
                j = k;
                i = m;
                if (getScrollPosition(paramRecycler, paramRecycler.findFocus(), sTwoInts))
                {
                  j = sTwoInts[0];
                  i = sTwoInts[1];
                }
              }
            }
          }
        }
        boolean bool3 = layoutInit();
        this.mInFastRelayout = bool3;
        if (!bool3) {
          break label378;
        }
        fastRelayout();
        if (this.mFocusPosition != -1) {
          scrollToFocusViewInLayout(bool2, bool1);
        }
        if (bool1)
        {
          scrollDirectionPrimary(-j);
          scrollDirectionSecondary(-i);
        }
        appendVisibleItems();
        prependVisibleItems();
        removeInvisibleViewsAtFront();
        removeInvisibleViewsAtEnd();
        if (!this.mRowSecondarySizeRefresh) {
          break label486;
        }
        this.mRowSecondarySizeRefresh = false;
        label327:
        if (!this.mInFastRelayout) {
          break label505;
        }
        if ((this.mFocusPosition == n) && (this.mSubFocusPosition == i1)) {
          break label493;
        }
        label352:
        dispatchChildSelected();
      }
    }
    for (;;)
    {
      this.mInLayout = false;
      leaveContext();
      return;
      bool1 = false;
      break;
      bool1 = false;
      break;
      label378:
      this.mInLayoutSearchFocus = bool2;
      while ((this.mFocusPosition != -1) && (appendOneColumnVisibleItems()) && (findViewByPosition(this.mFocusPosition) == null)) {}
      for (;;)
      {
        updateScrollMin();
        updateScrollMax();
        k = this.mGrid.getFirstVisibleIndex();
        m = this.mGrid.getLastVisibleIndex();
        scrollToFocusViewInLayout(bool2, true);
        appendVisibleItems();
        prependVisibleItems();
        removeInvisibleViewsAtFront();
        removeInvisibleViewsAtEnd();
        if (this.mGrid.getFirstVisibleIndex() == k) {
          if (this.mGrid.getLastVisibleIndex() == m) {
            break;
          }
        }
      }
      label486:
      updateRowSecondarySizeRefresh();
      break label327;
      label493:
      if (findViewByPosition(this.mFocusPosition) != paramRecycler) {
        break label352;
      }
      label505:
      if ((!this.mInFastRelayout) && (this.mInLayoutSearchFocus)) {
        dispatchChildSelected();
      }
    }
  }
  
  public void onMeasure(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2)
  {
    int k = 1;
    saveContext(paramRecycler, paramState);
    int i;
    int j;
    int m;
    if (this.mOrientation == 0)
    {
      i = View.MeasureSpec.getSize(paramInt1);
      paramInt1 = View.MeasureSpec.getSize(paramInt2);
      j = View.MeasureSpec.getMode(paramInt2);
      m = getPaddingTop() + getPaddingBottom();
      paramInt2 = i;
      i = m;
      label54:
      this.mMaxSizeSecondary = paramInt1;
      if (this.mRowSizeSecondaryRequested != -2) {
        break label268;
      }
      if (this.mNumRowsRequested != 0) {
        break label207;
      }
    }
    label207:
    for (paramInt1 = 1;; paramInt1 = this.mNumRowsRequested)
    {
      this.mNumRows = paramInt1;
      this.mFixedRowSizeSecondary = 0;
      if ((this.mRowSizeSecondary == null) || (this.mRowSizeSecondary.length != this.mNumRows)) {
        this.mRowSizeSecondary = new int[this.mNumRows];
      }
      processRowSizeSecondary(true);
      switch (j)
      {
      default: 
        throw new IllegalStateException("wrong spec");
        m = View.MeasureSpec.getSize(paramInt1);
        paramInt2 = View.MeasureSpec.getSize(paramInt2);
        j = View.MeasureSpec.getMode(paramInt1);
        i = getPaddingLeft() + getPaddingRight();
        paramInt1 = m;
        break label54;
      }
    }
    paramInt1 = getSizeSecondary() + i;
    if (this.mOrientation == 0) {
      setMeasuredDimension(paramInt2, paramInt1);
    }
    for (;;)
    {
      leaveContext();
      return;
      paramInt1 = Math.min(getSizeSecondary() + i, this.mMaxSizeSecondary);
      break;
      paramInt1 = this.mMaxSizeSecondary;
      break;
      switch (j)
      {
      default: 
        throw new IllegalStateException("wrong spec");
      case 0: 
        label268:
        if (this.mRowSizeSecondaryRequested == 0)
        {
          paramInt1 -= i;
          label327:
          this.mFixedRowSizeSecondary = paramInt1;
          if (this.mNumRowsRequested != 0) {
            break label383;
          }
        }
        label383:
        for (paramInt1 = k;; paramInt1 = this.mNumRowsRequested)
        {
          this.mNumRows = paramInt1;
          paramInt1 = this.mFixedRowSizeSecondary * this.mNumRows + this.mMarginSecondary * (this.mNumRows - 1) + i;
          break;
          paramInt1 = this.mRowSizeSecondaryRequested;
          break label327;
        }
      }
      if ((this.mNumRowsRequested == 0) && (this.mRowSizeSecondaryRequested == 0))
      {
        this.mNumRows = 1;
        this.mFixedRowSizeSecondary = (paramInt1 - i);
      }
      for (;;)
      {
        k = paramInt1;
        paramInt1 = k;
        if (j != Integer.MIN_VALUE) {
          break;
        }
        i = this.mFixedRowSizeSecondary * this.mNumRows + this.mMarginSecondary * (this.mNumRows - 1) + i;
        paramInt1 = k;
        if (i >= k) {
          break;
        }
        paramInt1 = i;
        break;
        if (this.mNumRowsRequested == 0)
        {
          this.mFixedRowSizeSecondary = this.mRowSizeSecondaryRequested;
          this.mNumRows = ((this.mMarginSecondary + paramInt1) / (this.mRowSizeSecondaryRequested + this.mMarginSecondary));
        }
        else if (this.mRowSizeSecondaryRequested == 0)
        {
          this.mNumRows = this.mNumRowsRequested;
          this.mFixedRowSizeSecondary = ((paramInt1 - i - this.mMarginSecondary * (this.mNumRows - 1)) / this.mNumRows);
        }
        else
        {
          this.mNumRows = this.mNumRowsRequested;
          this.mFixedRowSizeSecondary = this.mRowSizeSecondaryRequested;
        }
      }
      setMeasuredDimension(paramInt1, paramInt2);
    }
  }
  
  public boolean onRequestChildFocus(RecyclerView paramRecyclerView, View paramView1, View paramView2)
  {
    if (this.mFocusSearchDisabled) {
      return true;
    }
    if (getPositionByView(paramView1) == -1) {
      return true;
    }
    if ((this.mInLayout) || (this.mInSelection)) {}
    while (this.mInScroll) {
      return true;
    }
    scrollToView(paramView1, paramView2, true);
    return true;
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState)) {
      return;
    }
    paramParcelable = (SavedState)paramParcelable;
    this.mFocusPosition = paramParcelable.index;
    this.mFocusPositionOffset = 0;
    this.mChildrenStates.loadFromBundle(paramParcelable.childStates);
    this.mForceFullLayout = true;
    requestLayout();
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    boolean bool2 = true;
    WindowAlignment.Axis localAxis;
    if (this.mOrientation == 0) {
      if (paramInt == 1)
      {
        bool1 = true;
        this.mReverseFlowPrimary = bool1;
        this.mReverseFlowSecondary = false;
        localAxis = this.mWindowAlignment.horizontal;
        if (paramInt != 1) {
          break label79;
        }
      }
    }
    label79:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      localAxis.setReversedFlow(bool1);
      return;
      bool1 = false;
      break;
      if (paramInt == 1) {}
      for (bool1 = true;; bool1 = false)
      {
        this.mReverseFlowSecondary = bool1;
        this.mReverseFlowPrimary = false;
        break;
      }
    }
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState();
    localSavedState.index = getSelection();
    Object localObject1 = this.mChildrenStates.saveAsBundle();
    int i = 0;
    int j = getChildCount();
    while (i < j)
    {
      View localView = getChildAt(i);
      int k = getPositionByView(localView);
      Object localObject2 = localObject1;
      if (k != -1) {
        localObject2 = this.mChildrenStates.saveOnScreenView((Bundle)localObject1, localView, k);
      }
      i += 1;
      localObject1 = localObject2;
    }
    localSavedState.childStates = ((Bundle)localObject1);
    return localSavedState;
  }
  
  public boolean performAccessibilityAction(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt, Bundle paramBundle)
  {
    saveContext(paramRecycler, paramState);
    switch (paramInt)
    {
    }
    for (;;)
    {
      leaveContext();
      return true;
      processSelectionMoves(false, -this.mState.getItemCount());
      continue;
      processSelectionMoves(false, this.mState.getItemCount());
    }
  }
  
  int processSelectionMoves(boolean paramBoolean, int paramInt)
  {
    if (this.mGrid == null) {
      return paramInt;
    }
    int n = this.mFocusPosition;
    int i;
    Object localObject2;
    int j;
    int i4;
    int k;
    int m;
    label52:
    label70:
    View localView;
    Object localObject1;
    int i1;
    if (n != -1)
    {
      i = this.mGrid.getRowIndex(n);
      localObject2 = null;
      j = 0;
      i4 = getChildCount();
      k = paramInt;
      paramInt = j;
      m = i;
      if ((paramInt >= i4) || (k == 0)) {
        break label297;
      }
      if (k <= 0) {
        break label128;
      }
      i = paramInt;
      localView = getChildAt(i);
      if (canScrollTo(localView)) {
        break label138;
      }
      j = k;
      localObject1 = localObject2;
      i1 = m;
      i = n;
    }
    for (;;)
    {
      paramInt += 1;
      n = i;
      m = i1;
      localObject2 = localObject1;
      k = j;
      break label52;
      i = -1;
      break;
      label128:
      i = i4 - 1 - paramInt;
      break label70;
      label138:
      int i2 = getPositionByIndex(i);
      int i3 = this.mGrid.getRowIndex(i2);
      if (m == -1)
      {
        i = i2;
        localObject1 = localView;
        i1 = i3;
        j = k;
      }
      else
      {
        i = n;
        i1 = m;
        localObject1 = localObject2;
        j = k;
        if (i3 == m)
        {
          if ((k > 0) && (i2 > n)) {}
          for (;;)
          {
            i = i2;
            localObject1 = localView;
            if (k <= 0) {
              break label284;
            }
            j = k - 1;
            i1 = m;
            break;
            i = n;
            i1 = m;
            localObject1 = localObject2;
            j = k;
            if (k >= 0) {
              break;
            }
            i = n;
            i1 = m;
            localObject1 = localObject2;
            j = k;
            if (i2 >= n) {
              break;
            }
          }
          label284:
          j = k + 1;
          i1 = m;
        }
      }
    }
    label297:
    if (localObject2 != null)
    {
      if (paramBoolean)
      {
        if (hasFocus())
        {
          this.mInSelection = true;
          ((View)localObject2).requestFocus();
          this.mInSelection = false;
        }
        this.mFocusPosition = n;
        this.mSubFocusPosition = 0;
      }
    }
    else {
      return k;
    }
    scrollToView((View)localObject2, true);
    return k;
  }
  
  public void removeAndRecycleAllViews(RecyclerView.Recycler paramRecycler)
  {
    int i = getChildCount() - 1;
    while (i >= 0)
    {
      removeAndRecycleViewAt(i, paramRecycler);
      i -= 1;
    }
  }
  
  public boolean requestChildRectangleOnScreen(RecyclerView paramRecyclerView, View paramView, Rect paramRect, boolean paramBoolean)
  {
    return false;
  }
  
  public int scrollHorizontallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if ((this.mLayoutEnabled) && (hasDoneFirstLayout()))
    {
      saveContext(paramRecycler, paramState);
      this.mInScroll = true;
      if (this.mOrientation != 0) {
        break label51;
      }
    }
    label51:
    for (paramInt = scrollDirectionPrimary(paramInt);; paramInt = scrollDirectionSecondary(paramInt))
    {
      leaveContext();
      this.mInScroll = false;
      return paramInt;
      return 0;
    }
  }
  
  public void scrollToPosition(int paramInt)
  {
    setSelection(paramInt, 0, false, 0);
  }
  
  void scrollToSelection(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
  {
    this.mPrimaryScrollExtra = paramInt3;
    View localView = findViewByPosition(paramInt1);
    if (localView != null)
    {
      this.mInSelection = true;
      scrollToView(localView, paramBoolean);
      this.mInSelection = false;
      return;
    }
    this.mFocusPosition = paramInt1;
    this.mSubFocusPosition = paramInt2;
    this.mFocusPositionOffset = Integer.MIN_VALUE;
    if (!this.mLayoutEnabled) {
      return;
    }
    if (paramBoolean)
    {
      if (!hasDoneFirstLayout())
      {
        Log.w(getTag(), "setSelectionSmooth should not be called before first layout pass");
        return;
      }
      startPositionSmoothScroller(paramInt1);
      return;
    }
    this.mForceFullLayout = true;
    requestLayout();
  }
  
  void scrollToView(View paramView, boolean paramBoolean)
  {
    View localView = null;
    if (paramView == null) {}
    for (;;)
    {
      scrollToView(paramView, localView, paramBoolean);
      return;
      localView = paramView.findFocus();
    }
  }
  
  public int scrollVerticallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if ((this.mLayoutEnabled) && (hasDoneFirstLayout()))
    {
      this.mInScroll = true;
      saveContext(paramRecycler, paramState);
      if (this.mOrientation != 1) {
        break label52;
      }
    }
    label52:
    for (paramInt = scrollDirectionPrimary(paramInt);; paramInt = scrollDirectionSecondary(paramInt))
    {
      leaveContext();
      this.mInScroll = false;
      return paramInt;
      return 0;
    }
  }
  
  public void setFocusOutAllowed(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mFocusOutFront = paramBoolean1;
    this.mFocusOutEnd = paramBoolean2;
  }
  
  public void setFocusOutSideAllowed(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mFocusOutSideStart = paramBoolean1;
    this.mFocusOutSideEnd = paramBoolean2;
  }
  
  public void setGravity(int paramInt)
  {
    this.mGravity = paramInt;
  }
  
  public void setHorizontalMargin(int paramInt)
  {
    if (this.mOrientation == 0)
    {
      this.mHorizontalMargin = paramInt;
      this.mMarginPrimary = paramInt;
      return;
    }
    this.mHorizontalMargin = paramInt;
    this.mMarginSecondary = paramInt;
  }
  
  public void setNumRows(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    this.mNumRowsRequested = paramInt;
  }
  
  public void setOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener paramOnChildViewHolderSelectedListener)
  {
    if (paramOnChildViewHolderSelectedListener == null)
    {
      this.mChildViewHolderSelectedListeners = null;
      return;
    }
    if (this.mChildViewHolderSelectedListeners == null) {
      this.mChildViewHolderSelectedListeners = new ArrayList();
    }
    for (;;)
    {
      this.mChildViewHolderSelectedListeners.add(paramOnChildViewHolderSelectedListener);
      return;
      this.mChildViewHolderSelectedListeners.clear();
    }
  }
  
  public void setOrientation(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      return;
    }
    this.mOrientation = paramInt;
    this.mOrientationHelper = OrientationHelper.createOrientationHelper(this, this.mOrientation);
    this.mWindowAlignment.setOrientation(paramInt);
    this.mItemAlignment.setOrientation(paramInt);
    this.mForceFullLayout = true;
  }
  
  public void setRowHeight(int paramInt)
  {
    if ((paramInt >= 0) || (paramInt == -2))
    {
      this.mRowSizeSecondaryRequested = paramInt;
      return;
    }
    throw new IllegalArgumentException("Invalid row height: " + paramInt);
  }
  
  public void setSelection(int paramInt1, int paramInt2)
  {
    setSelection(paramInt1, 0, false, paramInt2);
  }
  
  public void setSelection(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
  {
    if ((this.mFocusPosition != paramInt1) && (paramInt1 != -1)) {
      break label22;
    }
    for (;;)
    {
      scrollToSelection(paramInt1, paramInt2, paramBoolean, paramInt3);
      label22:
      return;
      if (paramInt2 == this.mSubFocusPosition) {
        if (paramInt3 == this.mPrimaryScrollExtra) {
          break;
        }
      }
    }
  }
  
  public void setSelectionSmooth(int paramInt)
  {
    setSelection(paramInt, 0, true, 0);
  }
  
  public void setVerticalMargin(int paramInt)
  {
    if (this.mOrientation == 0)
    {
      this.mVerticalMargin = paramInt;
      this.mMarginSecondary = paramInt;
      return;
    }
    this.mVerticalMargin = paramInt;
    this.mMarginPrimary = paramInt;
  }
  
  public void setWindowAlignment(int paramInt)
  {
    this.mWindowAlignment.mainAxis().setWindowAlignment(paramInt);
  }
  
  void startPositionSmoothScroller(int paramInt)
  {
    GridLinearSmoothScroller local3 = new GridLinearSmoothScroller(this)
    {
      public PointF computeScrollVectorForPosition(int paramAnonymousInt)
      {
        int i = 1;
        if (getChildCount() == 0) {
          return null;
        }
        int j = jdField_this.getPosition(jdField_this.getChildAt(0));
        if (jdField_this.mReverseFlowPrimary) {
          if (paramAnonymousInt > j) {
            if (i == 0) {
              break label84;
            }
          }
        }
        label84:
        for (paramAnonymousInt = -1;; paramAnonymousInt = 1)
        {
          if (jdField_this.mOrientation != 0) {
            break label89;
          }
          return new PointF(paramAnonymousInt, 0.0F);
          i = 0;
          break;
          if (paramAnonymousInt < j) {
            break;
          }
          i = 0;
          break;
        }
        label89:
        return new PointF(0.0F, paramAnonymousInt);
      }
    };
    local3.setTargetPosition(paramInt);
    startSmoothScroll(local3);
  }
  
  void updateScrollMax()
  {
    if (!this.mReverseFlowPrimary)
    {
      i = this.mGrid.getLastVisibleIndex();
      if (this.mReverseFlowPrimary) {
        break label48;
      }
    }
    label48:
    for (int j = this.mState.getItemCount() - 1;; j = 0)
    {
      if (i >= 0) {
        break label53;
      }
      return;
      i = this.mGrid.getFirstVisibleIndex();
      break;
    }
    label53:
    if (i == j) {}
    for (int i = 1;; i = 0)
    {
      boolean bool = this.mWindowAlignment.mainAxis().isMaxUnknown();
      if ((i != 0) || (!bool)) {
        break;
      }
      return;
    }
    j = this.mGrid.findRowMax(true, sTwoInts) + this.mScrollOffsetPrimary;
    int k = sTwoInts[0];
    int m = sTwoInts[1];
    k = this.mWindowAlignment.mainAxis().getMaxEdge();
    this.mWindowAlignment.mainAxis().setMaxEdge(j);
    m = getPrimarySystemScrollPositionOfChildMax(findViewByPosition(m));
    this.mWindowAlignment.mainAxis().setMaxEdge(k);
    if (i != 0)
    {
      this.mWindowAlignment.mainAxis().setMaxEdge(j);
      this.mWindowAlignment.mainAxis().setMaxScroll(m);
      return;
    }
    this.mWindowAlignment.mainAxis().invalidateScrollMax();
  }
  
  void updateScrollMin()
  {
    if (!this.mReverseFlowPrimary)
    {
      i = this.mGrid.getFirstVisibleIndex();
      if (this.mReverseFlowPrimary) {
        break label40;
      }
    }
    label40:
    for (int j = 0;; j = this.mState.getItemCount() - 1)
    {
      if (i >= 0) {
        break label53;
      }
      return;
      i = this.mGrid.getLastVisibleIndex();
      break;
    }
    label53:
    if (i == j) {}
    for (int i = 1;; i = 0)
    {
      boolean bool = this.mWindowAlignment.mainAxis().isMinUnknown();
      if ((i != 0) || (!bool)) {
        break;
      }
      return;
    }
    j = this.mGrid.findRowMin(false, sTwoInts) + this.mScrollOffsetPrimary;
    int k = sTwoInts[0];
    int m = sTwoInts[1];
    k = this.mWindowAlignment.mainAxis().getMinEdge();
    this.mWindowAlignment.mainAxis().setMinEdge(j);
    m = getPrimarySystemScrollPosition(findViewByPosition(m));
    this.mWindowAlignment.mainAxis().setMinEdge(k);
    if (i != 0)
    {
      this.mWindowAlignment.mainAxis().setMinEdge(j);
      this.mWindowAlignment.mainAxis().setMinScroll(m);
      return;
    }
    this.mWindowAlignment.mainAxis().invalidateScrollMin();
  }
  
  abstract class GridLinearSmoothScroller
    extends LinearSmoothScroller
  {
    GridLinearSmoothScroller()
    {
      super();
    }
    
    protected int calculateTimeForScrolling(int paramInt)
    {
      int j = super.calculateTimeForScrolling(paramInt);
      int i = j;
      if (GridLayoutManager.this.mWindowAlignment.mainAxis().getSize() > 0)
      {
        float f = 30.0F / GridLayoutManager.this.mWindowAlignment.mainAxis().getSize() * paramInt;
        i = j;
        if (j < f) {
          i = (int)f;
        }
      }
      return i;
    }
    
    protected void onStop()
    {
      View localView = findViewByPosition(getTargetPosition());
      if (localView == null)
      {
        if (getTargetPosition() >= 0) {
          GridLayoutManager.this.scrollToSelection(getTargetPosition(), 0, false, 0);
        }
        super.onStop();
        return;
      }
      if (GridLayoutManager.this.hasFocus())
      {
        GridLayoutManager.this.mInSelection = true;
        localView.requestFocus();
        GridLayoutManager.this.mInSelection = false;
      }
      GridLayoutManager.this.dispatchChildSelected();
      super.onStop();
    }
    
    protected void onTargetFound(View paramView, RecyclerView.State paramState, RecyclerView.SmoothScroller.Action paramAction)
    {
      int i;
      if (GridLayoutManager.this.getScrollPosition(paramView, null, GridLayoutManager.sTwoInts))
      {
        if (GridLayoutManager.this.mOrientation != 0) {
          break label72;
        }
        i = GridLayoutManager.sTwoInts[0];
      }
      for (int j = GridLayoutManager.sTwoInts[1];; j = GridLayoutManager.sTwoInts[0])
      {
        paramAction.update(i, j, calculateTimeForDeceleration((int)Math.sqrt(i * i + j * j)), this.mDecelerateInterpolator);
        return;
        label72:
        i = GridLayoutManager.sTwoInts[1];
      }
    }
  }
  
  static final class LayoutParams
    extends RecyclerView.LayoutParams
  {
    private int[] mAlignMultiple;
    private int mAlignX;
    private int mAlignY;
    private ItemAlignmentFacet mAlignmentFacet;
    int mBottomInset;
    int mLeftInset;
    int mRightInset;
    int mTopInset;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(RecyclerView.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }
    
    void calculateItemAlignments(int paramInt, View paramView)
    {
      ItemAlignmentFacet.ItemAlignmentDef[] arrayOfItemAlignmentDef = this.mAlignmentFacet.getAlignmentDefs();
      if ((this.mAlignMultiple == null) || (this.mAlignMultiple.length != arrayOfItemAlignmentDef.length)) {
        this.mAlignMultiple = new int[arrayOfItemAlignmentDef.length];
      }
      int i = 0;
      while (i < arrayOfItemAlignmentDef.length)
      {
        this.mAlignMultiple[i] = ItemAlignmentFacetHelper.getAlignmentPosition(paramView, arrayOfItemAlignmentDef[i], paramInt);
        i += 1;
      }
      if (paramInt == 0)
      {
        this.mAlignX = this.mAlignMultiple[0];
        return;
      }
      this.mAlignY = this.mAlignMultiple[0];
    }
    
    int[] getAlignMultiple()
    {
      return this.mAlignMultiple;
    }
    
    int getAlignX()
    {
      return this.mAlignX;
    }
    
    int getAlignY()
    {
      return this.mAlignY;
    }
    
    ItemAlignmentFacet getItemAlignmentFacet()
    {
      return this.mAlignmentFacet;
    }
    
    int getOpticalHeight(View paramView)
    {
      return paramView.getHeight() - this.mTopInset - this.mBottomInset;
    }
    
    int getOpticalLeft(View paramView)
    {
      return paramView.getLeft() + this.mLeftInset;
    }
    
    int getOpticalLeftInset()
    {
      return this.mLeftInset;
    }
    
    int getOpticalRight(View paramView)
    {
      return paramView.getRight() - this.mRightInset;
    }
    
    int getOpticalTop(View paramView)
    {
      return paramView.getTop() + this.mTopInset;
    }
    
    int getOpticalTopInset()
    {
      return this.mTopInset;
    }
    
    int getOpticalWidth(View paramView)
    {
      return paramView.getWidth() - this.mLeftInset - this.mRightInset;
    }
    
    void setAlignX(int paramInt)
    {
      this.mAlignX = paramInt;
    }
    
    void setAlignY(int paramInt)
    {
      this.mAlignY = paramInt;
    }
    
    void setItemAlignmentFacet(ItemAlignmentFacet paramItemAlignmentFacet)
    {
      this.mAlignmentFacet = paramItemAlignmentFacet;
    }
    
    void setOpticalInsets(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.mLeftInset = paramInt1;
      this.mTopInset = paramInt2;
      this.mRightInset = paramInt3;
      this.mBottomInset = paramInt4;
    }
  }
  
  final class PendingMoveSmoothScroller
    extends GridLayoutManager.GridLinearSmoothScroller
  {
    private int mPendingMoves;
    private final boolean mStaggeredGrid;
    
    PendingMoveSmoothScroller(int paramInt, boolean paramBoolean)
    {
      super();
      this.mPendingMoves = paramInt;
      this.mStaggeredGrid = paramBoolean;
      setTargetPosition(-2);
    }
    
    public PointF computeScrollVectorForPosition(int paramInt)
    {
      if (this.mPendingMoves == 0) {
        return null;
      }
      if (GridLayoutManager.this.mReverseFlowPrimary) {
        if (this.mPendingMoves <= 0) {
          break label56;
        }
      }
      label56:
      for (paramInt = -1;; paramInt = 1)
      {
        if (GridLayoutManager.this.mOrientation != 0) {
          break label61;
        }
        return new PointF(paramInt, 0.0F);
        if (this.mPendingMoves < 0) {
          break;
        }
      }
      label61:
      return new PointF(0.0F, paramInt);
    }
    
    void consumePendingMovesAfterLayout()
    {
      if ((this.mStaggeredGrid) && (this.mPendingMoves != 0)) {
        this.mPendingMoves = GridLayoutManager.this.processSelectionMoves(true, this.mPendingMoves);
      }
      if ((this.mPendingMoves == 0) || ((this.mPendingMoves > 0) && (GridLayoutManager.this.hasCreatedLastItem())) || ((this.mPendingMoves < 0) && (GridLayoutManager.this.hasCreatedFirstItem())))
      {
        setTargetPosition(GridLayoutManager.this.mFocusPosition);
        stop();
      }
    }
    
    void consumePendingMovesBeforeLayout()
    {
      if ((this.mStaggeredGrid) || (this.mPendingMoves == 0)) {
        return;
      }
      Object localObject = null;
      int i;
      if (this.mPendingMoves > 0) {
        i = GridLayoutManager.this.mFocusPosition + GridLayoutManager.this.mNumRows;
      }
      for (;;)
      {
        View localView;
        if (this.mPendingMoves != 0)
        {
          localView = findViewByPosition(i);
          if (localView != null) {}
        }
        else
        {
          if ((localObject != null) && (GridLayoutManager.this.hasFocus()))
          {
            GridLayoutManager.this.mInSelection = true;
            ((View)localObject).requestFocus();
            GridLayoutManager.this.mInSelection = false;
          }
          return;
          i = GridLayoutManager.this.mFocusPosition - GridLayoutManager.this.mNumRows;
          continue;
        }
        if (!GridLayoutManager.this.canScrollTo(localView)) {}
        for (;;)
        {
          if (this.mPendingMoves <= 0) {
            break label194;
          }
          i += GridLayoutManager.this.mNumRows;
          break;
          localObject = localView;
          GridLayoutManager.this.mFocusPosition = i;
          GridLayoutManager.this.mSubFocusPosition = 0;
          if (this.mPendingMoves > 0) {
            this.mPendingMoves -= 1;
          } else {
            this.mPendingMoves += 1;
          }
        }
        label194:
        i -= GridLayoutManager.this.mNumRows;
      }
    }
    
    void decreasePendingMoves()
    {
      if (this.mPendingMoves > -10) {
        this.mPendingMoves -= 1;
      }
    }
    
    void increasePendingMoves()
    {
      if (this.mPendingMoves < 10) {
        this.mPendingMoves += 1;
      }
    }
    
    protected void onStop()
    {
      super.onStop();
      this.mPendingMoves = 0;
      GridLayoutManager.this.mPendingMoveSmoothScroller = null;
      View localView = findViewByPosition(getTargetPosition());
      if (localView != null) {
        GridLayoutManager.this.scrollToView(localView, true);
      }
    }
    
    protected void updateActionForInterimTarget(RecyclerView.SmoothScroller.Action paramAction)
    {
      if (this.mPendingMoves == 0) {
        return;
      }
      super.updateActionForInterimTarget(paramAction);
    }
  }
  
  static final class SavedState
    implements Parcelable
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public GridLayoutManager.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new GridLayoutManager.SavedState(paramAnonymousParcel);
      }
      
      public GridLayoutManager.SavedState[] newArray(int paramAnonymousInt)
      {
        return new GridLayoutManager.SavedState[paramAnonymousInt];
      }
    };
    Bundle childStates = Bundle.EMPTY;
    int index;
    
    SavedState() {}
    
    SavedState(Parcel paramParcel)
    {
      this.index = paramParcel.readInt();
      this.childStates = paramParcel.readBundle(GridLayoutManager.class.getClassLoader());
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.index);
      paramParcel.writeBundle(this.childStates);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\GridLayoutManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */