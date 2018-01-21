package com.android.systemui.qs;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;

public class TileLayout
  extends ViewGroup
  implements QSPanel.QSTileLayout
{
  protected int mCellHeight;
  protected int mCellItemMargin;
  protected int mCellMargin;
  private int mCellMarginTop;
  protected int mCellWidth;
  protected int mColumns;
  private boolean mListening;
  protected final ArrayList<QSPanel.TileRecord> mRecords = new ArrayList();
  
  public TileLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TileLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setFocusableInTouchMode(true);
    updateResources();
  }
  
  private static int exactly(int paramInt)
  {
    return View.MeasureSpec.makeMeasureSpec(paramInt, 1073741824);
  }
  
  private int getColumnStart(int paramInt)
  {
    return (this.mCellWidth + this.mCellMargin) * paramInt + this.mCellMargin;
  }
  
  private int getRowTop(int paramInt)
  {
    return (this.mCellHeight + this.mCellItemMargin) * paramInt + this.mCellMarginTop;
  }
  
  public void addTile(QSPanel.TileRecord paramTileRecord)
  {
    this.mRecords.add(paramTileRecord);
    paramTileRecord.tile.setListening(this, this.mListening);
    addView(paramTileRecord.tileView);
  }
  
  public int getOffsetTop(QSPanel.TileRecord paramTileRecord)
  {
    return getTop();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int k = getWidth();
    int j;
    label24:
    int i;
    QSPanel.TileRecord localTileRecord;
    int m;
    if (getLayoutDirection() == 1)
    {
      paramInt2 = 1;
      j = 0;
      paramInt1 = 0;
      paramInt3 = 0;
      if (paramInt3 >= this.mRecords.size()) {
        return;
      }
      i = paramInt1;
      paramInt4 = j;
      if (paramInt1 == this.mColumns)
      {
        paramInt4 = j + 1;
        i = paramInt1 - this.mColumns;
      }
      localTileRecord = (QSPanel.TileRecord)this.mRecords.get(paramInt3);
      paramInt1 = getColumnStart(i);
      m = getRowTop(paramInt4);
      if (paramInt2 == 0) {
        break label159;
      }
      j = k - paramInt1;
      paramInt1 = j - this.mCellWidth;
    }
    for (;;)
    {
      localTileRecord.tileView.layout(paramInt1, m, j, localTileRecord.tileView.getMeasuredHeight() + m);
      paramInt3 += 1;
      paramInt1 = i + 1;
      j = paramInt4;
      break label24;
      paramInt2 = 0;
      break;
      label159:
      j = paramInt1 + this.mCellWidth;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = this.mRecords.size();
    paramInt1 = View.MeasureSpec.getSize(paramInt1);
    paramInt2 = (this.mColumns + paramInt2 - 1) / this.mColumns;
    this.mCellWidth = ((paramInt1 - this.mCellMargin * (this.mColumns + 1)) / this.mColumns);
    Object localObject = this;
    Iterator localIterator = this.mRecords.iterator();
    while (localIterator.hasNext())
    {
      QSPanel.TileRecord localTileRecord = (QSPanel.TileRecord)localIterator.next();
      if (localTileRecord.tileView.getVisibility() != 8)
      {
        localTileRecord.tileView.measure(exactly(this.mCellWidth), exactly(this.mCellHeight));
        localObject = localTileRecord.tileView.updateAccessibilityOrder((View)localObject);
      }
    }
    setMeasuredDimension(paramInt1, Math.max((this.mCellHeight + this.mCellItemMargin) * paramInt2 + (this.mCellMarginTop - this.mCellItemMargin) + this.mCellMarginTop, 0));
  }
  
  public void removeAllViews()
  {
    Iterator localIterator = this.mRecords.iterator();
    while (localIterator.hasNext()) {
      ((QSPanel.TileRecord)localIterator.next()).tile.setListening(this, false);
    }
    this.mRecords.clear();
    super.removeAllViews();
  }
  
  public void removeTile(QSPanel.TileRecord paramTileRecord)
  {
    this.mRecords.remove(paramTileRecord);
    paramTileRecord.tile.setListening(this, false);
    removeView(paramTileRecord.tileView);
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (this.mListening == paramBoolean) {
      return;
    }
    this.mListening = paramBoolean;
    Iterator localIterator = this.mRecords.iterator();
    while (localIterator.hasNext()) {
      ((QSPanel.TileRecord)localIterator.next()).tile.setListening(this, this.mListening);
    }
  }
  
  public boolean updateResources()
  {
    Resources localResources = this.mContext.getResources();
    int i = Math.max(1, localResources.getInteger(2131623981));
    this.mCellHeight = this.mContext.getResources().getDimensionPixelSize(2131755408);
    this.mCellMargin = localResources.getDimensionPixelSize(2131755409);
    this.mCellItemMargin = localResources.getDimensionPixelSize(2131755714);
    this.mCellMarginTop = localResources.getDimensionPixelSize(2131755410);
    if (this.mColumns != i)
    {
      this.mColumns = i;
      requestLayout();
      return true;
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\TileLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */