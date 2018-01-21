package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class BrowseFrameLayout
  extends FrameLayout
{
  private OnFocusSearchListener mListener;
  private OnChildFocusListener mOnChildFocusListener;
  
  public BrowseFrameLayout(Context paramContext)
  {
    this(paramContext, null, 0);
  }
  
  public BrowseFrameLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public BrowseFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public View focusSearch(View paramView, int paramInt)
  {
    if (this.mListener != null)
    {
      View localView = this.mListener.onFocusSearch(paramView, paramInt);
      if (localView != null) {
        return localView;
      }
    }
    return super.focusSearch(paramView, paramInt);
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    if (this.mOnChildFocusListener != null) {
      return this.mOnChildFocusListener.onRequestFocusInDescendants(paramInt, paramRect);
    }
    return super.onRequestFocusInDescendants(paramInt, paramRect);
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    super.requestChildFocus(paramView1, paramView2);
    if (this.mOnChildFocusListener != null) {
      this.mOnChildFocusListener.onRequestChildFocus(paramView1, paramView2);
    }
  }
  
  public static abstract interface OnChildFocusListener
  {
    public abstract void onRequestChildFocus(View paramView1, View paramView2);
    
    public abstract boolean onRequestFocusInDescendants(int paramInt, Rect paramRect);
  }
  
  public static abstract interface OnFocusSearchListener
  {
    public abstract View onFocusSearch(View paramView, int paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\BrowseFrameLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */