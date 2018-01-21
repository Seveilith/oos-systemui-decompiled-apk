package com.android.systemui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.ExpandableView;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.statusbar.policy.ScrollAdapter;

public class ExpandHelper
{
  private Callback mCallback;
  private Context mContext;
  private float mCurrentHeight;
  private boolean mEnabled = true;
  private View mEventSource;
  private boolean mExpanding;
  private int mExpansionStyle = 0;
  private FlingAnimationUtils mFlingAnimationUtils;
  private int mGravity;
  private boolean mHasPopped;
  private float mInitialTouchFocusY;
  private float mInitialTouchSpan;
  private float mInitialTouchX;
  private float mInitialTouchY;
  private int mLargeSize;
  private float mLastFocusY;
  private float mLastMotionY;
  private float mLastSpanY;
  private float mMaximumStretch;
  private float mNaturalHeight;
  private float mOldHeight;
  private boolean mOnlyMovements;
  private float mPullGestureMinXSpan;
  private ExpandableView mResizedView;
  private ScaleGestureDetector mSGD;
  private ObjectAnimator mScaleAnimation;
  private ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener()
  {
    public boolean onScale(ScaleGestureDetector paramAnonymousScaleGestureDetector)
    {
      return true;
    }
    
    public boolean onScaleBegin(ScaleGestureDetector paramAnonymousScaleGestureDetector)
    {
      if (!ExpandHelper.-get2(ExpandHelper.this)) {
        ExpandHelper.-wrap0(ExpandHelper.this, ExpandHelper.-get3(ExpandHelper.this), 4);
      }
      return ExpandHelper.-get1(ExpandHelper.this);
    }
    
    public void onScaleEnd(ScaleGestureDetector paramAnonymousScaleGestureDetector) {}
  };
  private ViewScaler mScaler;
  private ScrollAdapter mScrollAdapter;
  private int mSmallSize;
  private int mTouchSlop;
  private VelocityTracker mVelocityTracker;
  private boolean mWatchingForPull;
  
  public ExpandHelper(Context paramContext, Callback paramCallback, int paramInt1, int paramInt2)
  {
    this.mSmallSize = paramInt1;
    this.mMaximumStretch = (this.mSmallSize * 2.0F);
    this.mLargeSize = paramInt2;
    this.mContext = paramContext;
    this.mCallback = paramCallback;
    this.mScaler = new ViewScaler();
    this.mGravity = 48;
    this.mScaleAnimation = ObjectAnimator.ofFloat(this.mScaler, "height", new float[] { 0.0F });
    this.mPullGestureMinXSpan = this.mContext.getResources().getDimension(2131755407);
    this.mTouchSlop = ViewConfiguration.get(this.mContext).getScaledTouchSlop();
    this.mSGD = new ScaleGestureDetector(paramContext, this.mScaleGestureListener);
    this.mSGD.setQuickScaleEnabled(false);
    this.mFlingAnimationUtils = new FlingAnimationUtils(paramContext, 0.3F);
  }
  
  private float clamp(float paramFloat)
  {
    if (paramFloat < this.mSmallSize) {
      paramFloat = this.mSmallSize;
    }
    for (;;)
    {
      float f = paramFloat;
      if (paramFloat > this.mNaturalHeight) {
        f = this.mNaturalHeight;
      }
      return f;
    }
  }
  
  private void clearView()
  {
    this.mResizedView = null;
  }
  
  private ExpandableView findView(float paramFloat1, float paramFloat2)
  {
    if (this.mEventSource != null)
    {
      int[] arrayOfInt = new int[2];
      this.mEventSource.getLocationOnScreen(arrayOfInt);
      float f1 = arrayOfInt[0];
      float f2 = arrayOfInt[1];
      return this.mCallback.getChildAtRawPosition(paramFloat1 + f1, paramFloat2 + f2);
    }
    return this.mCallback.getChildAtPosition(paramFloat1, paramFloat2);
  }
  
  private void finishExpanding(boolean paramBoolean, float paramFloat)
  {
    boolean bool1 = true;
    if (!this.mExpanding) {
      return;
    }
    float f1 = this.mScaler.getHeight();
    this.mScaler.getHeight();
    int i;
    label79:
    int j;
    label95:
    final boolean bool2;
    if (this.mOldHeight == this.mSmallSize)
    {
      i = 1;
      int k = this.mScaler.getNaturalHeight();
      if (i == 0) {
        break label257;
      }
      if ((!paramBoolean) && ((f1 <= this.mOldHeight) || (paramFloat < 0.0F))) {
        break label251;
      }
      i = 1;
      if (this.mNaturalHeight != this.mSmallSize) {
        break label288;
      }
      j = 1;
      bool2 = i | j;
      if (this.mScaleAnimation.isRunning()) {
        this.mScaleAnimation.cancel();
      }
      this.mCallback.expansionStateChanged(false);
      if (!bool2) {
        break label294;
      }
      i = k;
      label138:
      float f2 = i;
      if (f2 == f1) {
        break label313;
      }
      this.mScaleAnimation.setFloatValues(new float[] { f2 });
      this.mScaleAnimation.setupStartValues();
      final ExpandableView localExpandableView = this.mResizedView;
      this.mScaleAnimation.addListener(new AnimatorListenerAdapter()
      {
        public boolean mCancelled;
        
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          this.mCancelled = true;
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (!this.mCancelled) {
            ExpandHelper.-get0(ExpandHelper.this).setUserExpandedChild(localExpandableView, bool2);
          }
          for (;;)
          {
            ExpandHelper.-get0(ExpandHelper.this).setUserLockedChild(localExpandableView, false);
            ExpandHelper.-get4(ExpandHelper.this).removeListener(this);
            return;
            ExpandHelper.-get0(ExpandHelper.this).setExpansionCancelled(localExpandableView);
          }
        }
      });
      if (paramFloat < 0.0F) {
        break label303;
      }
      paramBoolean = bool1;
      label206:
      if (bool2 != paramBoolean) {
        break label308;
      }
      label212:
      this.mFlingAnimationUtils.apply(this.mScaleAnimation, f1, f2, paramFloat);
      this.mScaleAnimation.start();
    }
    for (;;)
    {
      this.mExpanding = false;
      this.mExpansionStyle = 0;
      return;
      i = 0;
      break;
      label251:
      i = 0;
      break label79;
      label257:
      if ((!paramBoolean) && ((f1 >= this.mOldHeight) || (paramFloat > 0.0F)))
      {
        i = 1;
        break label79;
      }
      i = 0;
      break label79;
      label288:
      j = 0;
      break label95;
      label294:
      i = this.mSmallSize;
      break label138;
      label303:
      paramBoolean = false;
      break label206;
      label308:
      paramFloat = 0.0F;
      break label212;
      label313:
      this.mCallback.setUserExpandedChild(this.mResizedView, bool2);
      this.mCallback.setUserLockedChild(this.mResizedView, false);
    }
  }
  
  private float getCurrentVelocity()
  {
    if (this.mVelocityTracker != null)
    {
      this.mVelocityTracker.computeCurrentVelocity(1000);
      return this.mVelocityTracker.getYVelocity();
    }
    return 0.0F;
  }
  
  private boolean isEnabled()
  {
    return this.mEnabled;
  }
  
  private boolean isFullyExpanded(ExpandableView paramExpandableView)
  {
    if (paramExpandableView.getIntrinsicHeight() == paramExpandableView.getMaxContentHeight())
    {
      if (paramExpandableView.isSummaryWithChildren()) {
        return paramExpandableView.areChildrenExpanded();
      }
      return true;
    }
    return false;
  }
  
  private boolean isInside(View paramView, float paramFloat1, float paramFloat2)
  {
    int j = 1;
    if (paramView == null) {
      return false;
    }
    float f2 = paramFloat1;
    float f1 = paramFloat2;
    if (this.mEventSource != null)
    {
      arrayOfInt = new int[2];
      this.mEventSource.getLocationOnScreen(arrayOfInt);
      f2 = paramFloat1 + arrayOfInt[0];
      f1 = paramFloat2 + arrayOfInt[1];
    }
    int[] arrayOfInt = new int[2];
    paramView.getLocationOnScreen(arrayOfInt);
    paramFloat1 = f2 - arrayOfInt[0];
    paramFloat2 = f1 - arrayOfInt[1];
    if ((paramFloat1 > 0.0F) && (paramFloat2 > 0.0F))
    {
      int i;
      if (paramFloat1 < paramView.getWidth())
      {
        i = 1;
        if (paramFloat2 >= paramView.getHeight()) {
          break label130;
        }
      }
      for (;;)
      {
        return i & j;
        i = 0;
        break;
        label130:
        j = 0;
      }
    }
    return false;
  }
  
  private void maybeRecycleVelocityTracker(MotionEvent paramMotionEvent)
  {
    if ((this.mVelocityTracker != null) && ((paramMotionEvent.getActionMasked() == 3) || (paramMotionEvent.getActionMasked() == 1)))
    {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    }
  }
  
  private boolean startExpanding(ExpandableView paramExpandableView, int paramInt)
  {
    if (!(paramExpandableView instanceof ExpandableNotificationRow)) {
      return false;
    }
    this.mExpansionStyle = paramInt;
    if ((this.mExpanding) && (paramExpandableView == this.mResizedView)) {
      return true;
    }
    this.mExpanding = true;
    this.mCallback.expansionStateChanged(true);
    this.mCallback.setUserLockedChild(paramExpandableView, true);
    this.mScaler.setView(paramExpandableView);
    this.mOldHeight = this.mScaler.getHeight();
    this.mCurrentHeight = this.mOldHeight;
    if (this.mCallback.canChildBeExpanded(paramExpandableView))
    {
      this.mNaturalHeight = this.mScaler.getNaturalHeight();
      this.mSmallSize = paramExpandableView.getCollapsedHeight();
      return true;
    }
    this.mNaturalHeight = this.mOldHeight;
    return true;
  }
  
  private void trackVelocity(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getActionMasked())
    {
    case 1: 
    default: 
      return;
    case 0: 
      if (this.mVelocityTracker == null) {
        this.mVelocityTracker = VelocityTracker.obtain();
      }
      for (;;)
      {
        this.mVelocityTracker.addMovement(paramMotionEvent);
        return;
        this.mVelocityTracker.clear();
      }
    }
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
    this.mVelocityTracker.addMovement(paramMotionEvent);
  }
  
  private void updateExpansion()
  {
    float f2 = (this.mSGD.getCurrentSpan() - this.mInitialTouchSpan) * 1.0F;
    float f3 = this.mSGD.getFocusY();
    float f4 = this.mInitialTouchFocusY;
    if (this.mGravity == 80) {}
    for (float f1 = -1.0F;; f1 = 1.0F)
    {
      f1 = (f3 - f4) * 1.0F * f1;
      f3 = Math.abs(f1) + Math.abs(f2) + 1.0F;
      f1 = clamp(Math.abs(f1) * f1 / f3 + Math.abs(f2) * f2 / f3 + this.mOldHeight);
      this.mScaler.setHeight(f1);
      this.mLastFocusY = this.mSGD.getFocusY();
      this.mLastSpanY = this.mSGD.getCurrentSpan();
      return;
    }
  }
  
  public void cancel()
  {
    finishExpanding(true, 0.0F);
    clearView();
    this.mSGD = new ScaleGestureDetector(this.mContext, this.mScaleGestureListener);
    this.mSGD.setQuickScaleEnabled(false);
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!isEnabled()) {
      return false;
    }
    trackVelocity(paramMotionEvent);
    int i = paramMotionEvent.getAction();
    this.mSGD.onTouchEvent(paramMotionEvent);
    int j = (int)this.mSGD.getFocusX();
    int k = (int)this.mSGD.getFocusY();
    this.mInitialTouchFocusY = k;
    this.mInitialTouchSpan = this.mSGD.getCurrentSpan();
    this.mLastFocusY = this.mInitialTouchFocusY;
    this.mLastSpanY = this.mInitialTouchSpan;
    if (this.mExpanding)
    {
      this.mLastMotionY = paramMotionEvent.getRawY();
      maybeRecycleVelocityTracker(paramMotionEvent);
      return true;
    }
    if ((i == 2) && ((this.mExpansionStyle & 0x1) != 0)) {
      return true;
    }
    switch (i & 0xFF)
    {
    }
    for (;;)
    {
      this.mLastMotionY = paramMotionEvent.getRawY();
      maybeRecycleVelocityTracker(paramMotionEvent);
      return this.mExpanding;
      float f1 = this.mSGD.getCurrentSpanX();
      if ((f1 <= this.mPullGestureMinXSpan) || (f1 <= this.mSGD.getCurrentSpanY()) || (this.mExpanding)) {}
      while (this.mWatchingForPull)
      {
        f1 = paramMotionEvent.getRawY() - this.mInitialTouchY;
        float f2 = paramMotionEvent.getRawX();
        float f3 = this.mInitialTouchX;
        if ((f1 <= this.mTouchSlop) || (f1 <= Math.abs(f2 - f3))) {
          break;
        }
        this.mWatchingForPull = false;
        if ((this.mResizedView == null) || (isFullyExpanded(this.mResizedView)) || (!startExpanding(this.mResizedView, 1))) {
          break;
        }
        this.mLastMotionY = paramMotionEvent.getRawY();
        this.mInitialTouchY = paramMotionEvent.getRawY();
        this.mHasPopped = false;
        break;
        startExpanding(this.mResizedView, 2);
        this.mWatchingForPull = false;
      }
      boolean bool;
      if ((this.mScrollAdapter != null) && (isInside(this.mScrollAdapter.getHostView(), j, k)))
      {
        bool = this.mScrollAdapter.isScrolledToTop();
        label381:
        this.mWatchingForPull = bool;
        this.mResizedView = findView(j, k);
        if ((this.mResizedView != null) && (!this.mCallback.canChildBeExpanded(this.mResizedView))) {
          break label449;
        }
      }
      for (;;)
      {
        this.mInitialTouchY = paramMotionEvent.getRawY();
        this.mInitialTouchX = paramMotionEvent.getRawX();
        break;
        bool = false;
        break label381;
        label449:
        this.mResizedView = null;
        this.mWatchingForPull = false;
      }
      finishExpanding(false, getCurrentVelocity());
      clearView();
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool2 = false;
    if (!isEnabled()) {
      return false;
    }
    trackVelocity(paramMotionEvent);
    int i = paramMotionEvent.getActionMasked();
    this.mSGD.onTouchEvent(paramMotionEvent);
    int j = (int)this.mSGD.getFocusX();
    int k = (int)this.mSGD.getFocusY();
    if (this.mOnlyMovements)
    {
      this.mLastMotionY = paramMotionEvent.getRawY();
      return false;
    }
    switch (i)
    {
    }
    for (;;)
    {
      this.mLastMotionY = paramMotionEvent.getRawY();
      maybeRecycleVelocityTracker(paramMotionEvent);
      boolean bool1 = bool2;
      if (this.mResizedView != null) {
        bool1 = true;
      }
      return bool1;
      if (this.mScrollAdapter != null) {}
      for (bool1 = isInside(this.mScrollAdapter.getHostView(), j, k);; bool1 = false)
      {
        this.mWatchingForPull = bool1;
        this.mResizedView = findView(j, k);
        this.mInitialTouchX = paramMotionEvent.getRawX();
        this.mInitialTouchY = paramMotionEvent.getRawY();
        break;
      }
      float f1;
      float f2;
      if (this.mWatchingForPull)
      {
        f1 = paramMotionEvent.getRawY() - this.mInitialTouchY;
        f2 = paramMotionEvent.getRawX();
        float f3 = this.mInitialTouchX;
        if ((f1 > this.mTouchSlop) && (f1 > Math.abs(f2 - f3)))
        {
          this.mWatchingForPull = false;
          if ((this.mResizedView != null) && (!isFullyExpanded(this.mResizedView))) {
            break label414;
          }
        }
      }
      while ((this.mExpanding) && ((this.mExpansionStyle & 0x1) != 0))
      {
        f1 = paramMotionEvent.getRawY() - this.mLastMotionY + this.mCurrentHeight;
        f2 = clamp(f1);
        i = 0;
        if (f1 > this.mNaturalHeight) {
          i = 1;
        }
        if (f1 < this.mSmallSize) {
          i = 1;
        }
        if (!this.mHasPopped)
        {
          if (this.mEventSource != null) {
            this.mEventSource.performHapticFeedback(1);
          }
          this.mHasPopped = true;
        }
        this.mScaler.setHeight(f2);
        this.mLastMotionY = paramMotionEvent.getRawY();
        if (i != 0)
        {
          this.mCallback.expansionStateChanged(false);
          return true;
          label414:
          if (startExpanding(this.mResizedView, 1))
          {
            this.mInitialTouchY = paramMotionEvent.getRawY();
            this.mLastMotionY = paramMotionEvent.getRawY();
            this.mHasPopped = false;
          }
        }
        else
        {
          this.mCallback.expansionStateChanged(true);
          return true;
        }
      }
      if (this.mExpanding)
      {
        updateExpansion();
        this.mLastMotionY = paramMotionEvent.getRawY();
        return true;
        this.mInitialTouchY += this.mSGD.getFocusY() - this.mLastFocusY;
        this.mInitialTouchSpan += this.mSGD.getCurrentSpan() - this.mLastSpanY;
        continue;
        finishExpanding(false, getCurrentVelocity());
        clearView();
      }
    }
  }
  
  public void onlyObserveMovements(boolean paramBoolean)
  {
    this.mOnlyMovements = paramBoolean;
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    this.mEnabled = paramBoolean;
  }
  
  public void setEventSource(View paramView)
  {
    this.mEventSource = paramView;
  }
  
  public void setScrollAdapter(ScrollAdapter paramScrollAdapter)
  {
    this.mScrollAdapter = paramScrollAdapter;
  }
  
  public static abstract interface Callback
  {
    public abstract boolean canChildBeExpanded(View paramView);
    
    public abstract void expansionStateChanged(boolean paramBoolean);
    
    public abstract ExpandableView getChildAtPosition(float paramFloat1, float paramFloat2);
    
    public abstract ExpandableView getChildAtRawPosition(float paramFloat1, float paramFloat2);
    
    public abstract int getMaxExpandHeight(ExpandableView paramExpandableView);
    
    public abstract void setExpansionCancelled(View paramView);
    
    public abstract void setUserExpandedChild(View paramView, boolean paramBoolean);
    
    public abstract void setUserLockedChild(View paramView, boolean paramBoolean);
  }
  
  private class ViewScaler
  {
    ExpandableView mView;
    
    public ViewScaler() {}
    
    public float getHeight()
    {
      return this.mView.getActualHeight();
    }
    
    public int getNaturalHeight()
    {
      return ExpandHelper.-get0(ExpandHelper.this).getMaxExpandHeight(this.mView);
    }
    
    public void setHeight(float paramFloat)
    {
      this.mView.setActualHeight((int)paramFloat);
      ExpandHelper.-set0(ExpandHelper.this, paramFloat);
    }
    
    public void setView(ExpandableView paramExpandableView)
    {
      this.mView = paramExpandableView;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\ExpandHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */