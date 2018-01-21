package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class ObservableScrollView
  extends ScrollView
{
  private boolean mBlockFlinging;
  private boolean mHandlingTouchEvent;
  private int mLastOverscrollAmount;
  private float mLastX;
  private float mLastY;
  private Listener mListener;
  private boolean mTouchCancelled;
  private boolean mTouchEnabled = true;
  
  public ObservableScrollView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private int getMaxScrollY()
  {
    int i = 0;
    if (getChildCount() > 0) {
      i = Math.max(0, getChildAt(0).getHeight() - (getHeight() - this.mPaddingBottom - this.mPaddingTop));
    }
    return i;
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getAction() == 0)
    {
      if (!this.mTouchEnabled)
      {
        this.mTouchCancelled = true;
        return false;
      }
      this.mTouchCancelled = false;
    }
    do
    {
      return super.dispatchTouchEvent(paramMotionEvent);
      if (this.mTouchCancelled) {
        return false;
      }
    } while (this.mTouchEnabled);
    paramMotionEvent = MotionEvent.obtain(paramMotionEvent);
    paramMotionEvent.setAction(3);
    super.dispatchTouchEvent(paramMotionEvent);
    paramMotionEvent.recycle();
    this.mTouchCancelled = true;
    return false;
  }
  
  public void fling(int paramInt)
  {
    if (!this.mBlockFlinging) {
      super.fling(paramInt);
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    this.mHandlingTouchEvent = true;
    this.mLastX = paramMotionEvent.getX();
    this.mLastY = paramMotionEvent.getY();
    boolean bool = super.onInterceptTouchEvent(paramMotionEvent);
    this.mHandlingTouchEvent = false;
    return bool;
  }
  
  protected void onOverScrolled(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    super.onOverScrolled(paramInt1, paramInt2, paramBoolean1, paramBoolean2);
    if ((this.mListener != null) && (this.mLastOverscrollAmount > 0)) {
      this.mListener.onOverscrolled(this.mLastX, this.mLastY, this.mLastOverscrollAmount);
    }
  }
  
  protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.mListener != null) {
      this.mListener.onScrollChanged();
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    this.mHandlingTouchEvent = true;
    this.mLastX = paramMotionEvent.getX();
    this.mLastY = paramMotionEvent.getY();
    boolean bool = super.onTouchEvent(paramMotionEvent);
    this.mHandlingTouchEvent = false;
    return bool;
  }
  
  protected boolean overScrollBy(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, boolean paramBoolean)
  {
    this.mLastOverscrollAmount = Math.max(0, paramInt4 + paramInt2 - getMaxScrollY());
    return super.overScrollBy(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramBoolean);
  }
  
  public static abstract interface Listener
  {
    public abstract void onOverscrolled(float paramFloat1, float paramFloat2, int paramInt);
    
    public abstract void onScrollChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\ObservableScrollView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */