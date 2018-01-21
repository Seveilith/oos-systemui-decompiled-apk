package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

class PlaybackControlsRowView
  extends LinearLayout
{
  private OnUnhandledKeyListener mOnUnhandledKeyListener;
  
  public PlaybackControlsRowView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public PlaybackControlsRowView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if (super.dispatchKeyEvent(paramKeyEvent)) {
      return true;
    }
    return (this.mOnUnhandledKeyListener != null) && (this.mOnUnhandledKeyListener.onUnhandledKey(paramKeyEvent));
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    View localView = findFocus();
    if ((localView != null) && (localView.requestFocus(paramInt, paramRect))) {
      return true;
    }
    return super.onRequestFocusInDescendants(paramInt, paramRect);
  }
  
  public static abstract interface OnUnhandledKeyListener
  {
    public abstract boolean onUnhandledKey(KeyEvent paramKeyEvent);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\PlaybackControlsRowView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */