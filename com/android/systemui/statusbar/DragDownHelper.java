package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.systemui.ExpandHelper.Callback;
import com.android.systemui.Interpolators;
import com.android.systemui.classifier.FalsingManager;

public class DragDownHelper
{
  private ExpandHelper.Callback mCallback;
  private DragDownCallback mDragDownCallback;
  private boolean mDraggedFarEnough;
  private boolean mDraggingDown;
  private FalsingManager mFalsingManager;
  private View mHost;
  private float mInitialTouchX;
  private float mInitialTouchY;
  private float mLastHeight;
  private int mMinDragDistance;
  private ExpandableView mStartingChild;
  private final int[] mTemp2 = new int[2];
  private float mTouchSlop;
  
  public DragDownHelper(Context paramContext, View paramView, ExpandHelper.Callback paramCallback, DragDownCallback paramDragDownCallback)
  {
    this.mMinDragDistance = paramContext.getResources().getDimensionPixelSize(2131755469);
    this.mTouchSlop = ViewConfiguration.get(paramContext).getScaledTouchSlop();
    this.mCallback = paramCallback;
    this.mDragDownCallback = paramDragDownCallback;
    this.mHost = paramView;
    this.mFalsingManager = FalsingManager.getInstance(paramContext);
  }
  
  private void cancelExpansion()
  {
    ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { this.mLastHeight, 0.0F });
    localValueAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
    localValueAnimator.setDuration(375L);
    localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        DragDownHelper.-get1(DragDownHelper.this).setEmptyDragAmount(((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
      }
    });
    localValueAnimator.start();
  }
  
  private void cancelExpansion(final ExpandableView paramExpandableView)
  {
    if (paramExpandableView.getActualHeight() == paramExpandableView.getCollapsedHeight())
    {
      this.mCallback.setUserLockedChild(paramExpandableView, false);
      return;
    }
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofInt(paramExpandableView, "actualHeight", new int[] { paramExpandableView.getActualHeight(), paramExpandableView.getCollapsedHeight() });
    localObjectAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
    localObjectAnimator.setDuration(375L);
    localObjectAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        DragDownHelper.-get0(DragDownHelper.this).setUserLockedChild(paramExpandableView, false);
      }
    });
    localObjectAnimator.start();
  }
  
  private void captureStartingChild(float paramFloat1, float paramFloat2)
  {
    if (this.mStartingChild == null)
    {
      this.mStartingChild = findView(paramFloat1, paramFloat2);
      if (this.mStartingChild != null) {
        this.mCallback.setUserLockedChild(this.mStartingChild, true);
      }
    }
  }
  
  private ExpandableView findView(float paramFloat1, float paramFloat2)
  {
    this.mHost.getLocationOnScreen(this.mTemp2);
    float f1 = this.mTemp2[0];
    float f2 = this.mTemp2[1];
    return this.mCallback.getChildAtRawPosition(paramFloat1 + f1, paramFloat2 + f2);
  }
  
  private void handleExpansion(float paramFloat, ExpandableView paramExpandableView)
  {
    float f = paramFloat;
    if (paramFloat < 0.0F) {
      f = 0.0F;
    }
    boolean bool = paramExpandableView.isContentExpandable();
    if (bool) {}
    for (paramFloat = 0.5F;; paramFloat = 0.15F)
    {
      f *= paramFloat;
      paramFloat = f;
      if (bool)
      {
        paramFloat = f;
        if (paramExpandableView.getCollapsedHeight() + f > paramExpandableView.getMaxContentHeight()) {
          paramFloat = f - (paramExpandableView.getCollapsedHeight() + f - paramExpandableView.getMaxContentHeight()) * 0.85F;
        }
      }
      paramExpandableView.setActualHeight((int)(paramExpandableView.getCollapsedHeight() + paramFloat));
      return;
    }
  }
  
  private boolean isFalseTouch()
  {
    boolean bool2 = true;
    boolean bool1 = bool2;
    if (!this.mFalsingManager.isFalseTouch())
    {
      bool1 = bool2;
      if (this.mDraggedFarEnough) {
        bool1 = false;
      }
    }
    return bool1;
  }
  
  private void stopDragging()
  {
    this.mFalsingManager.onNotificatonStopDraggingDown();
    if (this.mStartingChild != null) {
      cancelExpansion(this.mStartingChild);
    }
    for (;;)
    {
      this.mDragDownCallback.onDragDownReset();
      return;
      cancelExpansion();
    }
  }
  
  public boolean isInChild(float paramFloat1, float paramFloat2)
  {
    return findView(paramFloat1, paramFloat2) != null;
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    switch (paramMotionEvent.getActionMasked())
    {
    }
    do
    {
      float f3;
      while ((f3 <= this.mTouchSlop) || (f3 <= Math.abs(f1 - this.mInitialTouchX)) || (this.mDraggingDown) || (isInChild(this.mInitialTouchX, this.mInitialTouchY)))
      {
        return false;
        this.mDraggedFarEnough = false;
        this.mDraggingDown = false;
        this.mStartingChild = null;
        this.mInitialTouchY = f2;
        this.mInitialTouchX = f1;
        return false;
        f3 = f2 - this.mInitialTouchY;
        if ((f3 > this.mTouchSlop) && (f3 > Math.abs(f1 - this.mInitialTouchX)) && (!this.mDraggingDown)) {
          break;
        }
      }
      return true;
    } while (!isInChild(this.mInitialTouchX, this.mInitialTouchY));
    this.mFalsingManager.onNotificatonStartDraggingDown();
    this.mDraggingDown = true;
    captureStartingChild(this.mInitialTouchX, this.mInitialTouchY);
    this.mInitialTouchY = f2;
    this.mInitialTouchX = f1;
    this.mDragDownCallback.onTouchSlopExceeded();
    return true;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!this.mDraggingDown) {
      return false;
    }
    paramMotionEvent.getX();
    float f = paramMotionEvent.getY();
    switch (paramMotionEvent.getActionMasked())
    {
    default: 
      return false;
    case 2: 
      this.mLastHeight = (f - this.mInitialTouchY);
      captureStartingChild(this.mInitialTouchX, this.mInitialTouchY);
      if (this.mStartingChild != null)
      {
        handleExpansion(this.mLastHeight, this.mStartingChild);
        if (this.mLastHeight <= this.mMinDragDistance) {
          break label144;
        }
        if (!this.mDraggedFarEnough)
        {
          this.mDraggedFarEnough = true;
          this.mDragDownCallback.onCrossedThreshold(true);
        }
      }
      while (!this.mDraggedFarEnough)
      {
        return true;
        this.mDragDownCallback.setEmptyDragAmount(this.mLastHeight);
        break;
      }
      this.mDraggedFarEnough = false;
      this.mDragDownCallback.onCrossedThreshold(false);
      return true;
    case 1: 
      label144:
      if ((!isFalseTouch()) && (this.mDragDownCallback.onDraggedDown(this.mStartingChild, (int)(f - this.mInitialTouchY))))
      {
        if (this.mStartingChild == null)
        {
          this.mDragDownCallback.setEmptyDragAmount(0.0F);
          return false;
        }
        this.mCallback.setUserLockedChild(this.mStartingChild, false);
        return false;
      }
      stopDragging();
      return false;
    }
    stopDragging();
    return false;
  }
  
  public static abstract interface DragDownCallback
  {
    public abstract void onCrossedThreshold(boolean paramBoolean);
    
    public abstract void onDragDownReset();
    
    public abstract boolean onDraggedDown(View paramView, int paramInt);
    
    public abstract void onTouchSlopExceeded();
    
    public abstract void setEmptyDragAmount(float paramFloat);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\DragDownHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */