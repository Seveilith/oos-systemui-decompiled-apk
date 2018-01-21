package android.support.v4.widget;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.annotation.RestrictTo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;

public abstract class CursorAdapter
  extends BaseAdapter
  implements Filterable, CursorFilter.CursorFilterClient
{
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  protected boolean mAutoRequery;
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  protected ChangeObserver mChangeObserver;
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  protected Context mContext;
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  protected Cursor mCursor;
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  protected CursorFilter mCursorFilter;
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  protected DataSetObserver mDataSetObserver;
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  protected boolean mDataValid;
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  protected FilterQueryProvider mFilterQueryProvider;
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  protected int mRowIDColumn;
  
  public abstract void bindView(View paramView, Context paramContext, Cursor paramCursor);
  
  public void changeCursor(Cursor paramCursor)
  {
    paramCursor = swapCursor(paramCursor);
    if (paramCursor != null) {
      paramCursor.close();
    }
  }
  
  public CharSequence convertToString(Cursor paramCursor)
  {
    if (paramCursor == null) {
      return "";
    }
    return paramCursor.toString();
  }
  
  public int getCount()
  {
    if ((this.mDataValid) && (this.mCursor != null)) {
      return this.mCursor.getCount();
    }
    return 0;
  }
  
  public Cursor getCursor()
  {
    return this.mCursor;
  }
  
  public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    if (this.mDataValid)
    {
      this.mCursor.moveToPosition(paramInt);
      if (paramView == null) {
        paramView = newDropDownView(this.mContext, this.mCursor, paramViewGroup);
      }
      for (;;)
      {
        bindView(paramView, this.mContext, this.mCursor);
        return paramView;
      }
    }
    return null;
  }
  
  public Filter getFilter()
  {
    if (this.mCursorFilter == null) {
      this.mCursorFilter = new CursorFilter(this);
    }
    return this.mCursorFilter;
  }
  
  public Object getItem(int paramInt)
  {
    if ((this.mDataValid) && (this.mCursor != null))
    {
      this.mCursor.moveToPosition(paramInt);
      return this.mCursor;
    }
    return null;
  }
  
  public long getItemId(int paramInt)
  {
    if ((this.mDataValid) && (this.mCursor != null))
    {
      if (this.mCursor.moveToPosition(paramInt)) {
        return this.mCursor.getLong(this.mRowIDColumn);
      }
      return 0L;
    }
    return 0L;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    if (!this.mDataValid) {
      throw new IllegalStateException("this should only be called when the cursor is valid");
    }
    if (!this.mCursor.moveToPosition(paramInt)) {
      throw new IllegalStateException("couldn't move cursor to position " + paramInt);
    }
    if (paramView == null) {
      paramView = newView(this.mContext, this.mCursor, paramViewGroup);
    }
    for (;;)
    {
      bindView(paramView, this.mContext, this.mCursor);
      return paramView;
    }
  }
  
  public boolean hasStableIds()
  {
    return true;
  }
  
  public View newDropDownView(Context paramContext, Cursor paramCursor, ViewGroup paramViewGroup)
  {
    return newView(paramContext, paramCursor, paramViewGroup);
  }
  
  public abstract View newView(Context paramContext, Cursor paramCursor, ViewGroup paramViewGroup);
  
  protected void onContentChanged()
  {
    if ((!this.mAutoRequery) || (this.mCursor == null) || (this.mCursor.isClosed())) {
      return;
    }
    this.mDataValid = this.mCursor.requery();
  }
  
  public Cursor runQueryOnBackgroundThread(CharSequence paramCharSequence)
  {
    if (this.mFilterQueryProvider != null) {
      return this.mFilterQueryProvider.runQuery(paramCharSequence);
    }
    return this.mCursor;
  }
  
  public Cursor swapCursor(Cursor paramCursor)
  {
    if (paramCursor == this.mCursor) {
      return null;
    }
    Cursor localCursor = this.mCursor;
    if (localCursor != null)
    {
      if (this.mChangeObserver != null) {
        localCursor.unregisterContentObserver(this.mChangeObserver);
      }
      if (this.mDataSetObserver != null) {
        localCursor.unregisterDataSetObserver(this.mDataSetObserver);
      }
    }
    this.mCursor = paramCursor;
    if (paramCursor != null)
    {
      if (this.mChangeObserver != null) {
        paramCursor.registerContentObserver(this.mChangeObserver);
      }
      if (this.mDataSetObserver != null) {
        paramCursor.registerDataSetObserver(this.mDataSetObserver);
      }
      this.mRowIDColumn = paramCursor.getColumnIndexOrThrow("_id");
      this.mDataValid = true;
      notifyDataSetChanged();
      return localCursor;
    }
    this.mRowIDColumn = -1;
    this.mDataValid = false;
    notifyDataSetInvalidated();
    return localCursor;
  }
  
  private class ChangeObserver
    extends ContentObserver
  {
    public boolean deliverSelfNotifications()
    {
      return true;
    }
    
    public void onChange(boolean paramBoolean)
    {
      this.this$0.onContentChanged();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\widget\CursorAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */