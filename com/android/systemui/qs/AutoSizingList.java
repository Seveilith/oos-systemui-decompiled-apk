package com.android.systemui.qs;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import com.android.systemui.R.styleable;

public class AutoSizingList
  extends LinearLayout
{
  private ListAdapter mAdapter;
  private final Runnable mBindChildren = new Runnable()
  {
    public void run()
    {
      AutoSizingList.-wrap2(AutoSizingList.this);
    }
  };
  private int mCount;
  private final DataSetObserver mDataObserver = new DataSetObserver()
  {
    public void onChanged()
    {
      if (AutoSizingList.-get0(AutoSizingList.this) > AutoSizingList.-wrap0(AutoSizingList.this)) {
        AutoSizingList.-set0(AutoSizingList.this, AutoSizingList.-wrap0(AutoSizingList.this));
      }
      AutoSizingList.-wrap1(AutoSizingList.this);
    }
    
    public void onInvalidated()
    {
      AutoSizingList.-wrap1(AutoSizingList.this);
    }
  };
  private final Handler mHandler = new Handler();
  private final int mItemSize;
  
  public AutoSizingList(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mItemSize = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.AutoSizingList).getDimensionPixelSize(0, 0);
  }
  
  private int getDesiredCount()
  {
    if (this.mAdapter != null) {
      return this.mAdapter.getCount();
    }
    return 0;
  }
  
  private void postRebindChildren()
  {
    this.mHandler.post(this.mBindChildren);
  }
  
  private void rebindChildren()
  {
    if (this.mAdapter == null) {
      return;
    }
    int i = 0;
    if (i < this.mCount)
    {
      if (i < getChildCount()) {}
      for (View localView1 = getChildAt(i);; localView1 = null)
      {
        View localView2 = this.mAdapter.getView(i, localView1, this);
        if (localView2 != localView1)
        {
          if (localView1 != null) {
            removeView(localView1);
          }
          addView(localView2, i);
        }
        i += 1;
        break;
      }
    }
    while (getChildCount() > this.mCount) {
      removeViewAt(getChildCount() - 1);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getSize(paramInt2);
    if (i != 0)
    {
      i = Math.min(i / this.mItemSize, getDesiredCount());
      if (this.mCount != i)
      {
        postRebindChildren();
        this.mCount = i;
      }
    }
    super.onMeasure(paramInt1, paramInt2);
  }
  
  public void setAdapter(ListAdapter paramListAdapter)
  {
    if (this.mAdapter != null) {
      this.mAdapter.unregisterDataSetObserver(this.mDataObserver);
    }
    this.mAdapter = paramListAdapter;
    if (paramListAdapter != null) {
      paramListAdapter.registerDataSetObserver(this.mDataObserver);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\AutoSizingList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */