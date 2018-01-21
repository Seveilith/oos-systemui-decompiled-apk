package com.android.systemui.qs;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

public class PagedTileLayout
  extends ViewPager
  implements QSPanel.QSTileLayout
{
  private final PagerAdapter mAdapter = new PagerAdapter()
  {
    public void destroyItem(ViewGroup paramAnonymousViewGroup, int paramAnonymousInt, Object paramAnonymousObject)
    {
      paramAnonymousViewGroup.removeView((View)paramAnonymousObject);
    }
    
    public int getCount()
    {
      return PagedTileLayout.-get0(PagedTileLayout.this);
    }
    
    public Object instantiateItem(ViewGroup paramAnonymousViewGroup, int paramAnonymousInt)
    {
      int i = paramAnonymousInt;
      if (PagedTileLayout.this.isLayoutRtl()) {
        i = PagedTileLayout.-get3(PagedTileLayout.this).size() - 1 - paramAnonymousInt;
      }
      ViewGroup localViewGroup = (ViewGroup)PagedTileLayout.-get3(PagedTileLayout.this).get(i);
      paramAnonymousViewGroup.addView(localViewGroup);
      return localViewGroup;
    }
    
    public boolean isViewFromObject(View paramAnonymousView, Object paramAnonymousObject)
    {
      return paramAnonymousView == paramAnonymousObject;
    }
  };
  private View mDecorGroup;
  private final Runnable mDistribute = new Runnable()
  {
    public void run()
    {
      PagedTileLayout.-wrap0(PagedTileLayout.this);
    }
  };
  private boolean mListening;
  private int mNumPages;
  private boolean mOffPage;
  private PageIndicator mPageIndicator;
  private PageListener mPageListener;
  private final ArrayList<TilePage> mPages = new ArrayList();
  private int mPosition;
  private final ArrayList<QSPanel.TileRecord> mTiles = new ArrayList();
  
  public PagedTileLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setAdapter(this.mAdapter);
    setOnPageChangeListener(new ViewPager.OnPageChangeListener()
    {
      public void onPageScrollStateChanged(int paramAnonymousInt) {}
      
      public void onPageScrolled(int paramAnonymousInt1, float paramAnonymousFloat, int paramAnonymousInt2)
      {
        boolean bool2 = false;
        if (PagedTileLayout.-get1(PagedTileLayout.this) == null) {
          return;
        }
        Object localObject = PagedTileLayout.this;
        boolean bool1;
        if (paramAnonymousFloat != 0.0F)
        {
          bool1 = true;
          PagedTileLayout.-wrap1((PagedTileLayout)localObject, paramAnonymousInt1, bool1);
          PagedTileLayout.-get1(PagedTileLayout.this).setLocation(paramAnonymousInt1 + paramAnonymousFloat);
          if (PagedTileLayout.-get2(PagedTileLayout.this) != null)
          {
            localObject = PagedTileLayout.-get2(PagedTileLayout.this);
            bool1 = bool2;
            if (paramAnonymousInt2 == 0)
            {
              if (!PagedTileLayout.this.isLayoutRtl()) {
                break label127;
              }
              bool1 = bool2;
              if (paramAnonymousInt1 != PagedTileLayout.-get3(PagedTileLayout.this).size() - 1) {}
            }
          }
        }
        for (;;)
        {
          bool1 = true;
          label127:
          do
          {
            ((PagedTileLayout.PageListener)localObject).onPageChanged(bool1);
            return;
            bool1 = false;
            break;
            bool1 = bool2;
          } while (paramAnonymousInt1 != 0);
        }
      }
      
      public void onPageSelected(int paramAnonymousInt)
      {
        boolean bool = true;
        if (PagedTileLayout.-get1(PagedTileLayout.this) == null) {
          return;
        }
        PagedTileLayout.PageListener localPageListener;
        if (PagedTileLayout.-get2(PagedTileLayout.this) != null)
        {
          localPageListener = PagedTileLayout.-get2(PagedTileLayout.this);
          if (!PagedTileLayout.this.isLayoutRtl()) {
            break label70;
          }
          if (paramAnonymousInt != PagedTileLayout.-get3(PagedTileLayout.this).size() - 1) {
            break label65;
          }
        }
        for (;;)
        {
          localPageListener.onPageChanged(bool);
          return;
          label65:
          bool = false;
          continue;
          label70:
          if (paramAnonymousInt != 0) {
            bool = false;
          }
        }
      }
    });
    setCurrentItem(0);
  }
  
  private void distributeTiles()
  {
    int j = this.mPages.size();
    int i = 0;
    while (i < j)
    {
      ((TilePage)this.mPages.get(i)).removeAllViews();
      i += 1;
    }
    int k = 0;
    int m = this.mTiles.size();
    j = 0;
    Object localObject;
    while (j < m)
    {
      localObject = (QSPanel.TileRecord)this.mTiles.get(j);
      i = k;
      if (((TilePage)this.mPages.get(k)).isFull())
      {
        k += 1;
        i = k;
        if (k == this.mPages.size())
        {
          this.mPages.add((TilePage)LayoutInflater.from(this.mContext).inflate(2130968777, this, false));
          i = k;
        }
      }
      ((TilePage)this.mPages.get(i)).addTile((QSPanel.TileRecord)localObject);
      j += 1;
      k = i;
    }
    if (this.mNumPages != k + 1)
    {
      this.mNumPages = (k + 1);
      while (this.mPages.size() > this.mNumPages) {
        this.mPages.remove(this.mPages.size() - 1);
      }
      this.mPageIndicator.setNumPages(this.mNumPages);
      localObject = this.mDecorGroup;
      if (this.mNumPages <= 1) {
        break label262;
      }
    }
    label262:
    for (i = 0;; i = 8)
    {
      ((View)localObject).setVisibility(i);
      setAdapter(this.mAdapter);
      this.mAdapter.notifyDataSetChanged();
      setCurrentItem(0, false);
      return;
    }
  }
  
  private void postDistributeTiles()
  {
    removeCallbacks(this.mDistribute);
    post(this.mDistribute);
  }
  
  private void setCurrentPage(int paramInt, boolean paramBoolean)
  {
    if ((this.mPosition == paramInt) && (this.mOffPage == paramBoolean)) {
      return;
    }
    if (this.mListening)
    {
      if (this.mPosition == paramInt) {
        break label88;
      }
      setPageListening(this.mPosition, false);
      if (this.mOffPage) {
        setPageListening(this.mPosition + 1, false);
      }
      setPageListening(paramInt, true);
      if (paramBoolean) {
        setPageListening(paramInt + 1, true);
      }
    }
    for (;;)
    {
      this.mPosition = paramInt;
      this.mOffPage = paramBoolean;
      return;
      label88:
      if (this.mOffPage != paramBoolean) {
        setPageListening(this.mPosition + 1, paramBoolean);
      }
    }
  }
  
  private void setPageListening(int paramInt, boolean paramBoolean)
  {
    if (paramInt >= this.mPages.size()) {
      return;
    }
    int i = paramInt;
    if (isLayoutRtl()) {
      i = this.mPages.size() - 1 - paramInt;
    }
    ((TilePage)this.mPages.get(i)).setListening(paramBoolean);
  }
  
  public void addTile(QSPanel.TileRecord paramTileRecord)
  {
    this.mTiles.add(paramTileRecord);
    postDistributeTiles();
  }
  
  public int getColumnCount()
  {
    if (this.mPages.size() == 0) {
      return 0;
    }
    return ((TilePage)this.mPages.get(0)).mColumns;
  }
  
  public int getOffsetTop(QSPanel.TileRecord paramTileRecord)
  {
    paramTileRecord = (ViewGroup)paramTileRecord.tileView.getParent();
    if (paramTileRecord == null) {
      return 0;
    }
    return paramTileRecord.getTop() + getTop();
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mPageIndicator = ((PageIndicator)findViewById(2131952005));
    this.mDecorGroup = findViewById(2131952155);
    ((ViewPager.LayoutParams)this.mDecorGroup.getLayoutParams()).isDecor = true;
    this.mPages.add((TilePage)LayoutInflater.from(this.mContext).inflate(2130968777, this, false));
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    paramInt1 = 0;
    int k = getChildCount();
    paramInt2 = 0;
    while (paramInt2 < k)
    {
      int j = getChildAt(paramInt2).getMeasuredHeight();
      i = paramInt1;
      if (j > paramInt1) {
        i = j;
      }
      paramInt2 += 1;
      paramInt1 = i;
    }
    int i = getMeasuredWidth();
    if (this.mDecorGroup.getVisibility() != 8) {}
    for (paramInt2 = this.mDecorGroup.getMeasuredHeight();; paramInt2 = 0)
    {
      setMeasuredDimension(i, paramInt2 + paramInt1);
      return;
    }
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    super.onRtlPropertiesChanged(paramInt);
    setAdapter(this.mAdapter);
    setCurrentItem(0, false);
  }
  
  public void removeTile(QSPanel.TileRecord paramTileRecord)
  {
    if (this.mTiles.remove(paramTileRecord)) {
      postDistributeTiles();
    }
  }
  
  public void setCurrentItem(int paramInt, boolean paramBoolean)
  {
    int i = paramInt;
    if (isLayoutRtl()) {
      i = this.mPages.size() - 1 - paramInt;
    }
    super.setCurrentItem(i, paramBoolean);
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (this.mListening == paramBoolean) {
      return;
    }
    this.mListening = paramBoolean;
    if (this.mListening)
    {
      setPageListening(this.mPosition, true);
      if (this.mOffPage) {
        setPageListening(this.mPosition + 1, true);
      }
    }
    for (;;)
    {
      return;
      int i = 0;
      while (i < this.mPages.size())
      {
        ((TilePage)this.mPages.get(i)).setListening(false);
        i += 1;
      }
    }
  }
  
  public void setPageListener(PageListener paramPageListener)
  {
    this.mPageListener = paramPageListener;
  }
  
  public boolean updateResources()
  {
    boolean bool = false;
    int i = 0;
    while (i < this.mPages.size())
    {
      bool |= ((TilePage)this.mPages.get(i)).updateResources();
      i += 1;
    }
    if (bool) {
      distributeTiles();
    }
    return bool;
  }
  
  public static abstract interface PageListener
  {
    public abstract void onPageChanged(boolean paramBoolean);
  }
  
  public static class TilePage
    extends TileLayout
  {
    private int mMaxRows = 3;
    
    public TilePage(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      updateResources();
      setContentDescription(this.mContext.getString(2131690186));
    }
    
    private int getRows()
    {
      Resources localResources = getContext().getResources();
      if (localResources.getConfiguration().orientation == 1) {
        return 3;
      }
      return Math.max(1, localResources.getInteger(2131623982));
    }
    
    public boolean isFull()
    {
      return this.mRecords.size() >= this.mColumns * this.mMaxRows;
    }
    
    public boolean updateResources()
    {
      int i = getRows();
      if (i != this.mMaxRows) {}
      for (boolean bool = true;; bool = false)
      {
        if (bool)
        {
          this.mMaxRows = i;
          requestLayout();
        }
        if (super.updateResources()) {
          break;
        }
        return bool;
      }
      return true;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\PagedTileLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */