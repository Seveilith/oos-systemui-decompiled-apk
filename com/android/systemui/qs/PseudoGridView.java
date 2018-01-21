package com.android.systemui.qs;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.android.systemui.R.styleable;
import java.lang.ref.WeakReference;

public class PseudoGridView
  extends ViewGroup
{
  private int mHorizontalSpacing;
  private int mNumColumns = 3;
  private int mVerticalSpacing;
  
  public PseudoGridView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.PseudoGridView);
    int j = paramContext.getIndexCount();
    int i = 0;
    if (i < j)
    {
      int k = paramContext.getIndex(i);
      switch (k)
      {
      }
      for (;;)
      {
        i += 1;
        break;
        this.mNumColumns = paramContext.getInt(k, 3);
        continue;
        this.mVerticalSpacing = paramContext.getDimensionPixelSize(k, 0);
        continue;
        this.mHorizontalSpacing = paramContext.getDimensionPixelSize(k, 0);
      }
    }
    paramContext.recycle();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramBoolean = isLayoutRtl();
    int k = getChildCount();
    int m = (this.mNumColumns + k - 1) / this.mNumColumns;
    paramInt3 = 0;
    paramInt2 = 0;
    while (paramInt2 < m)
    {
      int i;
      label72:
      int i1;
      int j;
      if (paramBoolean)
      {
        paramInt1 = getWidth();
        i = 0;
        paramInt4 = paramInt2 * this.mNumColumns;
        int n = Math.min(this.mNumColumns + paramInt4, k);
        if (paramInt4 >= n) {
          break label182;
        }
        View localView = getChildAt(paramInt4);
        i1 = localView.getMeasuredWidth();
        int i2 = localView.getMeasuredHeight();
        j = paramInt1;
        if (paramBoolean) {
          j = paramInt1 - i1;
        }
        localView.layout(j, paramInt3, j + i1, paramInt3 + i2);
        i = Math.max(i, i2);
        if (!paramBoolean) {
          break label168;
        }
      }
      label168:
      for (paramInt1 = j - this.mHorizontalSpacing;; paramInt1 = j + (this.mHorizontalSpacing + i1))
      {
        paramInt4 += 1;
        break label72;
        paramInt1 = 0;
        break;
      }
      label182:
      paramInt3 += i;
      paramInt1 = paramInt3;
      if (paramInt2 > 0) {
        paramInt1 = paramInt3 + this.mVerticalSpacing;
      }
      paramInt2 += 1;
      paramInt3 = paramInt1;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (View.MeasureSpec.getMode(paramInt1) == 0) {
      throw new UnsupportedOperationException("Needs a maximum width");
    }
    int n = View.MeasureSpec.getSize(paramInt1);
    int i1 = View.MeasureSpec.makeMeasureSpec((n - (this.mNumColumns - 1) * this.mHorizontalSpacing) / this.mNumColumns, 1073741824);
    paramInt1 = 0;
    int i2 = getChildCount();
    int i3 = (this.mNumColumns + i2 - 1) / this.mNumColumns;
    int i = 0;
    while (i < i3)
    {
      int j = i * this.mNumColumns;
      int i4 = Math.min(this.mNumColumns + j, i2);
      int k = 0;
      int m = j;
      View localView;
      while (m < i4)
      {
        localView = getChildAt(m);
        localView.measure(i1, 0);
        k = Math.max(k, localView.getMeasuredHeight());
        m += 1;
      }
      m = View.MeasureSpec.makeMeasureSpec(k, 1073741824);
      while (j < i4)
      {
        localView = getChildAt(j);
        if (localView.getMeasuredHeight() != k) {
          localView.measure(i1, m);
        }
        j += 1;
      }
      j = paramInt1 + k;
      paramInt1 = j;
      if (i > 0) {
        paramInt1 = j + this.mVerticalSpacing;
      }
      i += 1;
    }
    setMeasuredDimension(n, resolveSizeAndState(paramInt1, paramInt2, 0));
  }
  
  public static class ViewGroupAdapterBridge
    extends DataSetObserver
  {
    private final BaseAdapter mAdapter;
    private boolean mReleased;
    private final WeakReference<ViewGroup> mViewGroup;
    
    private ViewGroupAdapterBridge(ViewGroup paramViewGroup, BaseAdapter paramBaseAdapter)
    {
      this.mViewGroup = new WeakReference(paramViewGroup);
      this.mAdapter = paramBaseAdapter;
      this.mReleased = false;
      this.mAdapter.registerDataSetObserver(this);
      refresh();
    }
    
    public static void link(ViewGroup paramViewGroup, BaseAdapter paramBaseAdapter)
    {
      new ViewGroupAdapterBridge(paramViewGroup, paramBaseAdapter);
    }
    
    private void refresh()
    {
      if (this.mReleased) {
        return;
      }
      ViewGroup localViewGroup = (ViewGroup)this.mViewGroup.get();
      if (localViewGroup == null)
      {
        release();
        return;
      }
      int j = localViewGroup.getChildCount();
      int k = this.mAdapter.getCount();
      int m = Math.max(j, k);
      int i = 0;
      if (i < m)
      {
        View localView1;
        View localView2;
        if (i < k)
        {
          localView1 = null;
          if (i < j) {
            localView1 = localViewGroup.getChildAt(i);
          }
          localView2 = this.mAdapter.getView(i, localView1, localViewGroup);
          if (localView1 == null) {
            localViewGroup.addView(localView2);
          }
        }
        for (;;)
        {
          i += 1;
          break;
          if (localView1 != localView2)
          {
            localViewGroup.removeViewAt(i);
            localViewGroup.addView(localView2, i);
            continue;
            localViewGroup.removeViewAt(localViewGroup.getChildCount() - 1);
          }
        }
      }
    }
    
    private void release()
    {
      if (!this.mReleased)
      {
        this.mReleased = true;
        this.mAdapter.unregisterDataSetObserver(this);
      }
    }
    
    public void onChanged()
    {
      refresh();
    }
    
    public void onInvalidated()
    {
      release();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\PseudoGridView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */