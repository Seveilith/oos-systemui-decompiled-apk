package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.RestrictTo;
import android.support.v17.leanback.R.styleable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.RecyclerListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
abstract class BaseGridView
  extends RecyclerView
{
  private boolean mAnimateChildLayout = true;
  RecyclerView.RecyclerListener mChainedRecyclerListener;
  private boolean mHasOverlappingRendering = true;
  final GridLayoutManager mLayoutManager = new GridLayoutManager(this);
  private OnKeyInterceptListener mOnKeyInterceptListener;
  private OnMotionInterceptListener mOnMotionInterceptListener;
  private OnTouchInterceptListener mOnTouchInterceptListener;
  private OnUnhandledKeyListener mOnUnhandledKeyListener;
  
  public BaseGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setLayoutManager(this.mLayoutManager);
    setDescendantFocusability(262144);
    setHasFixedSize(true);
    setChildrenDrawingOrderEnabled(true);
    setWillNotDraw(true);
    setOverScrollMode(2);
    ((SimpleItemAnimator)getItemAnimator()).setSupportsChangeAnimations(false);
    super.setRecyclerListener(new RecyclerView.RecyclerListener()
    {
      public void onViewRecycled(RecyclerView.ViewHolder paramAnonymousViewHolder)
      {
        BaseGridView.this.mLayoutManager.onChildRecycled(paramAnonymousViewHolder);
        if (BaseGridView.this.mChainedRecyclerListener != null) {
          BaseGridView.this.mChainedRecyclerListener.onViewRecycled(paramAnonymousViewHolder);
        }
      }
    });
  }
  
  public boolean dispatchGenericFocusedEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mOnMotionInterceptListener != null) && (this.mOnMotionInterceptListener.onInterceptMotionEvent(paramMotionEvent))) {
      return true;
    }
    return super.dispatchGenericFocusedEvent(paramMotionEvent);
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if ((this.mOnKeyInterceptListener != null) && (this.mOnKeyInterceptListener.onInterceptKeyEvent(paramKeyEvent))) {
      return true;
    }
    if (super.dispatchKeyEvent(paramKeyEvent)) {
      return true;
    }
    return (this.mOnUnhandledKeyListener != null) && (this.mOnUnhandledKeyListener.onUnhandledKey(paramKeyEvent));
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mOnTouchInterceptListener != null) && (this.mOnTouchInterceptListener.onInterceptTouchEvent(paramMotionEvent))) {
      return true;
    }
    return super.dispatchTouchEvent(paramMotionEvent);
  }
  
  public View focusSearch(int paramInt)
  {
    if (isFocused())
    {
      View localView = this.mLayoutManager.findViewByPosition(this.mLayoutManager.getSelection());
      if (localView != null) {
        return focusSearch(localView, paramInt);
      }
    }
    return super.focusSearch(paramInt);
  }
  
  public int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
    return this.mLayoutManager.getChildDrawingOrder(this, paramInt1, paramInt2);
  }
  
  public int getSelectedPosition()
  {
    return this.mLayoutManager.getSelection();
  }
  
  public boolean hasOverlappingRendering()
  {
    return this.mHasOverlappingRendering;
  }
  
  protected void initBaseGridViewAttributes(Context paramContext, AttributeSet paramAttributeSet)
  {
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.lbBaseGridView);
    boolean bool1 = paramContext.getBoolean(R.styleable.lbBaseGridView_focusOutFront, false);
    boolean bool2 = paramContext.getBoolean(R.styleable.lbBaseGridView_focusOutEnd, false);
    this.mLayoutManager.setFocusOutAllowed(bool1, bool2);
    bool1 = paramContext.getBoolean(R.styleable.lbBaseGridView_focusOutSideStart, true);
    bool2 = paramContext.getBoolean(R.styleable.lbBaseGridView_focusOutSideEnd, true);
    this.mLayoutManager.setFocusOutSideAllowed(bool1, bool2);
    this.mLayoutManager.setVerticalMargin(paramContext.getDimensionPixelSize(R.styleable.lbBaseGridView_verticalMargin, 0));
    this.mLayoutManager.setHorizontalMargin(paramContext.getDimensionPixelSize(R.styleable.lbBaseGridView_horizontalMargin, 0));
    if (paramContext.hasValue(R.styleable.lbBaseGridView_android_gravity)) {
      setGravity(paramContext.getInt(R.styleable.lbBaseGridView_android_gravity, 0));
    }
    paramContext.recycle();
  }
  
  final boolean isChildrenDrawingOrderEnabledInternal()
  {
    return isChildrenDrawingOrderEnabled();
  }
  
  protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
  {
    super.onFocusChanged(paramBoolean, paramInt, paramRect);
    this.mLayoutManager.onFocusChanged(paramBoolean, paramInt, paramRect);
  }
  
  public boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    return this.mLayoutManager.gridOnRequestFocusInDescendants(this, paramInt, paramRect);
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    this.mLayoutManager.onRtlPropertiesChanged(paramInt);
  }
  
  public void setGravity(int paramInt)
  {
    this.mLayoutManager.setGravity(paramInt);
    requestLayout();
  }
  
  public void setOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener paramOnChildViewHolderSelectedListener)
  {
    this.mLayoutManager.setOnChildViewHolderSelectedListener(paramOnChildViewHolderSelectedListener);
  }
  
  public void setRecyclerListener(RecyclerView.RecyclerListener paramRecyclerListener)
  {
    this.mChainedRecyclerListener = paramRecyclerListener;
  }
  
  public void setSelectedPosition(int paramInt)
  {
    this.mLayoutManager.setSelection(paramInt, 0);
  }
  
  public void setSelectedPositionSmooth(int paramInt)
  {
    this.mLayoutManager.setSelectionSmooth(paramInt);
  }
  
  public void setWindowAlignment(int paramInt)
  {
    this.mLayoutManager.setWindowAlignment(paramInt);
    requestLayout();
  }
  
  public static abstract interface OnKeyInterceptListener
  {
    public abstract boolean onInterceptKeyEvent(KeyEvent paramKeyEvent);
  }
  
  public static abstract interface OnMotionInterceptListener
  {
    public abstract boolean onInterceptMotionEvent(MotionEvent paramMotionEvent);
  }
  
  public static abstract interface OnTouchInterceptListener
  {
    public abstract boolean onInterceptTouchEvent(MotionEvent paramMotionEvent);
  }
  
  public static abstract interface OnUnhandledKeyListener
  {
    public abstract boolean onUnhandledKey(KeyEvent paramKeyEvent);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\BaseGridView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */