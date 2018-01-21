package com.android.systemui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Property;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.systemui.classifier.FalsingManager;
import com.android.systemui.statusbar.FlingAnimationUtils;
import java.util.HashMap;

public class SwipeHelper
{
  private int DEFAULT_ESCAPE_ANIMATION_DURATION = 200;
  private int MAX_DISMISS_VELOCITY = 4000;
  private int MAX_ESCAPE_ANIMATION_DURATION = 400;
  private float SWIPE_ESCAPE_VELOCITY = 100.0F;
  private Callback mCallback;
  private boolean mCanCurrViewBeDimissed;
  private View mCurrView;
  private float mDensityScale;
  private boolean mDisableHwLayers;
  private HashMap<View, Animator> mDismissPendingMap = new HashMap();
  private boolean mDragging;
  private FalsingManager mFalsingManager;
  private int mFalsingThreshold;
  private FlingAnimationUtils mFlingAnimationUtils;
  private Handler mHandler;
  private float mInitialTouchPos;
  private LongPressListener mLongPressListener;
  private boolean mLongPressSent;
  private long mLongPressTimeout;
  private float mMaxSwipeProgress = 1.0F;
  private float mMinSwipeProgress = 0.0F;
  private float mPagingTouchSlop;
  private float mPerpendicularInitialTouchPos;
  private boolean mSnappingChild;
  private int mSwipeDirection;
  private final int[] mTmpPos = new int[2];
  private boolean mTouchAboveFalsingThreshold;
  private float mTranslation = 0.0F;
  private VelocityTracker mVelocityTracker;
  private Runnable mWatchLongPress;
  
  public SwipeHelper(int paramInt, Callback paramCallback, Context paramContext)
  {
    this.mCallback = paramCallback;
    this.mHandler = new Handler();
    this.mSwipeDirection = paramInt;
    this.mVelocityTracker = VelocityTracker.obtain();
    this.mDensityScale = paramContext.getResources().getDisplayMetrics().density;
    this.mPagingTouchSlop = ViewConfiguration.get(paramContext).getScaledPagingTouchSlop();
    this.mLongPressTimeout = ((ViewConfiguration.getLongPressTimeout() * 1.5F));
    this.mFalsingThreshold = paramContext.getResources().getDimensionPixelSize(2131755468);
    this.mFalsingManager = FalsingManager.getInstance(paramContext);
    this.mFlingAnimationUtils = new FlingAnimationUtils(paramContext, (float)getMaxEscapeAnimDuration() / 1000.0F);
  }
  
  private int getFalsingThreshold()
  {
    float f = this.mCallback.getFalsingThresholdFactor();
    return (int)(this.mFalsingThreshold * f);
  }
  
  private float getMaxVelocity()
  {
    return this.MAX_DISMISS_VELOCITY * this.mDensityScale;
  }
  
  private float getPerpendicularPos(MotionEvent paramMotionEvent)
  {
    if (this.mSwipeDirection == 0) {
      return paramMotionEvent.getY();
    }
    return paramMotionEvent.getX();
  }
  
  private float getPos(MotionEvent paramMotionEvent)
  {
    if (this.mSwipeDirection == 0) {
      return paramMotionEvent.getX();
    }
    return paramMotionEvent.getY();
  }
  
  private float getSwipeAlpha(float paramFloat)
  {
    return Math.min(0.0F, Math.max(1.0F, paramFloat / 0.5F));
  }
  
  private float getSwipeProgressForOffset(View paramView, float paramFloat)
  {
    paramFloat = Math.abs(paramFloat / getSize(paramView));
    return Math.min(Math.max(this.mMinSwipeProgress, paramFloat), this.mMaxSwipeProgress);
  }
  
  private float getVelocity(VelocityTracker paramVelocityTracker)
  {
    if (this.mSwipeDirection == 0) {
      return paramVelocityTracker.getXVelocity();
    }
    return paramVelocityTracker.getYVelocity();
  }
  
  public static void invalidateGlobalRegion(View paramView)
  {
    invalidateGlobalRegion(paramView, new RectF(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom()));
  }
  
  public static void invalidateGlobalRegion(View paramView, RectF paramRectF)
  {
    while ((paramView.getParent() != null) && ((paramView.getParent() instanceof View)))
    {
      paramView = (View)paramView.getParent();
      paramView.getMatrix().mapRect(paramRectF);
      paramView.invalidate((int)Math.floor(paramRectF.left), (int)Math.floor(paramRectF.top), (int)Math.ceil(paramRectF.right), (int)Math.ceil(paramRectF.bottom));
    }
  }
  
  private void snapChildInstantly(View paramView)
  {
    boolean bool = this.mCallback.canChildBeDismissed(paramView);
    setTranslation(paramView, 0.0F);
    updateSwipeProgressFromOffset(paramView, bool);
  }
  
  private void updateSwipeProgressFromOffset(View paramView, boolean paramBoolean)
  {
    updateSwipeProgressFromOffset(paramView, paramBoolean, getTranslation(paramView));
  }
  
  private void updateSwipeProgressFromOffset(View paramView, boolean paramBoolean, float paramFloat)
  {
    paramFloat = getSwipeProgressForOffset(paramView, paramFloat);
    if ((!this.mCallback.updateSwipeProgress(paramView, paramBoolean, paramFloat)) && (paramBoolean)) {
      if (!this.mDisableHwLayers)
      {
        if ((paramFloat == 0.0F) || (paramFloat == 1.0F)) {
          break label65;
        }
        paramView.setLayerType(2, null);
      }
    }
    for (;;)
    {
      paramView.setAlpha(getSwipeAlpha(paramFloat));
      invalidateGlobalRegion(paramView);
      return;
      label65:
      paramView.setLayerType(0, null);
    }
  }
  
  protected ObjectAnimator createTranslationAnimation(View paramView, float paramFloat)
  {
    if (this.mSwipeDirection == 0) {}
    for (Property localProperty = View.TRANSLATION_X;; localProperty = View.TRANSLATION_Y) {
      return ObjectAnimator.ofFloat(paramView, localProperty, new float[] { paramFloat });
    }
  }
  
  public void dismissChild(final View paramView, float paramFloat, final Runnable paramRunnable, long paramLong1, boolean paramBoolean1, long paramLong2, boolean paramBoolean2)
  {
    final boolean bool = this.mCallback.canChildBeDismissed(paramView);
    int j;
    int i;
    label55:
    int k;
    label80:
    label110:
    float f;
    if (paramView.getLayoutDirection() == 1)
    {
      j = 1;
      if ((paramFloat != 0.0F) || ((getTranslation(paramView) != 0.0F) && (!paramBoolean2))) {
        break label232;
      }
      if (this.mSwipeDirection != 1) {
        break label226;
      }
      i = 1;
      if ((paramFloat != 0.0F) || ((getTranslation(paramView) != 0.0F) && (!paramBoolean2))) {
        break label238;
      }
      k = j;
      if (paramFloat < 0.0F) {
        break label244;
      }
      if ((paramFloat != 0.0F) || (getTranslation(paramView) >= 0.0F)) {
        break label256;
      }
      if (!paramBoolean2) {
        break label250;
      }
      j = 0;
      if ((j == 0) && (k == 0) && (i == 0)) {
        break label262;
      }
      f = -getSize(paramView);
      label133:
      if (paramLong2 != 0L) {
        break label282;
      }
      paramLong2 = this.MAX_ESCAPE_ANIMATION_DURATION;
      if (paramFloat == 0.0F) {
        break label272;
      }
      paramLong2 = Math.min(paramLong2, (int)(Math.abs(f - getTranslation(paramView)) * 1000.0F / Math.abs(paramFloat)));
    }
    Animator localAnimator;
    label226:
    label232:
    label238:
    label244:
    label250:
    label256:
    label262:
    label272:
    label282:
    for (;;)
    {
      if (!this.mDisableHwLayers) {
        paramView.setLayerType(2, null);
      }
      localAnimator = getViewTranslationAnimator(paramView, f, new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          SwipeHelper.this.onTranslationUpdate(paramView, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue(), bool);
        }
      });
      if (localAnimator != null) {
        break label285;
      }
      return;
      j = 0;
      break;
      i = 0;
      break label55;
      i = 0;
      break label55;
      k = 0;
      break label80;
      j = 1;
      break label110;
      j = 1;
      break label110;
      j = 0;
      break label110;
      f = getSize(paramView);
      break label133;
      paramLong2 = this.DEFAULT_ESCAPE_ANIMATION_DURATION;
    }
    label285:
    if (paramBoolean1)
    {
      localAnimator.setInterpolator(Interpolators.FAST_OUT_LINEAR_IN);
      localAnimator.setDuration(paramLong2);
    }
    for (;;)
    {
      if (paramLong1 > 0L) {
        localAnimator.setStartDelay(paramLong1);
      }
      localAnimator.addListener(new AnimatorListenerAdapter()
      {
        private boolean mCancelled;
        
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          this.mCancelled = true;
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          SwipeHelper.-wrap0(SwipeHelper.this, paramView, bool);
          SwipeHelper.-get3(SwipeHelper.this).remove(paramView);
          if (!this.mCancelled) {
            SwipeHelper.-get0(SwipeHelper.this).onChildDismissed(paramView);
          }
          if (paramRunnable != null) {
            paramRunnable.run();
          }
          if (!SwipeHelper.-get2(SwipeHelper.this)) {
            paramView.setLayerType(0, null);
          }
        }
      });
      prepareDismissAnimation(paramView, localAnimator);
      this.mDismissPendingMap.put(paramView, localAnimator);
      localAnimator.start();
      return;
      this.mFlingAnimationUtils.applyDismissing(localAnimator, getTranslation(paramView), f, paramFloat, getSize(paramView));
    }
  }
  
  public void dismissChild(View paramView, float paramFloat, boolean paramBoolean)
  {
    dismissChild(paramView, paramFloat, null, 0L, paramBoolean, 0L, false);
  }
  
  protected float getEscapeVelocity()
  {
    return getUnscaledEscapeVelocity() * this.mDensityScale;
  }
  
  protected long getMaxEscapeAnimDuration()
  {
    return this.MAX_ESCAPE_ANIMATION_DURATION;
  }
  
  protected float getSize(View paramView)
  {
    if (this.mSwipeDirection == 0) {}
    for (int i = paramView.getMeasuredWidth();; i = paramView.getMeasuredHeight()) {
      return i;
    }
  }
  
  protected float getTranslation(View paramView)
  {
    if (this.mSwipeDirection == 0) {
      return paramView.getTranslationX();
    }
    return paramView.getTranslationY();
  }
  
  protected float getUnscaledEscapeVelocity()
  {
    return this.SWIPE_ESCAPE_VELOCITY;
  }
  
  protected Animator getViewTranslationAnimator(View paramView, float paramFloat, ValueAnimator.AnimatorUpdateListener paramAnimatorUpdateListener)
  {
    paramView = createTranslationAnimation(paramView, paramFloat);
    if (paramAnimatorUpdateListener != null) {
      paramView.addUpdateListener(paramAnimatorUpdateListener);
    }
    return paramView;
  }
  
  protected boolean handleUpEvent(MotionEvent paramMotionEvent, View paramView, float paramFloat1, float paramFloat2)
  {
    return false;
  }
  
  protected boolean isDismissGesture(MotionEvent paramMotionEvent)
  {
    boolean bool = this.mCallback.isAntiFalsingNeeded();
    if (this.mFalsingManager.isClassiferEnabled()) {
      if (bool) {
        bool = this.mFalsingManager.isFalseTouch();
      }
    }
    while ((!bool) && ((swipedFastEnough()) || (swipedFarEnough())) && (paramMotionEvent.getActionMasked() == 1))
    {
      return this.mCallback.canChildBeDismissed(this.mCurrView);
      bool = false;
      continue;
      if ((!bool) || (this.mTouchAboveFalsingThreshold)) {
        bool = false;
      } else {
        bool = true;
      }
    }
    return false;
  }
  
  public void onDownUpdate(View paramView) {}
  
  public boolean onInterceptTouchEvent(final MotionEvent paramMotionEvent)
  {
    boolean bool2 = true;
    switch (paramMotionEvent.getAction())
    {
    default: 
    case 0: 
    case 2: 
      for (;;)
      {
        bool1 = bool2;
        if (!this.mDragging) {
          bool1 = this.mLongPressSent;
        }
        return bool1;
        this.mTouchAboveFalsingThreshold = false;
        this.mDragging = false;
        this.mSnappingChild = false;
        this.mLongPressSent = false;
        this.mVelocityTracker.clear();
        this.mCurrView = this.mCallback.getChildAtPosition(paramMotionEvent);
        if (this.mCurrView != null)
        {
          onDownUpdate(this.mCurrView);
          this.mCanCurrViewBeDimissed = this.mCallback.canChildBeDismissed(this.mCurrView);
          this.mVelocityTracker.addMovement(paramMotionEvent);
          this.mInitialTouchPos = getPos(paramMotionEvent);
          this.mPerpendicularInitialTouchPos = getPerpendicularPos(paramMotionEvent);
          this.mTranslation = getTranslation(this.mCurrView);
          if (this.mLongPressListener != null)
          {
            if (this.mWatchLongPress == null) {
              this.mWatchLongPress = new Runnable()
              {
                public void run()
                {
                  if ((SwipeHelper.-get1(SwipeHelper.this) == null) || (SwipeHelper.-get5(SwipeHelper.this))) {
                    return;
                  }
                  SwipeHelper.-set0(SwipeHelper.this, true);
                  SwipeHelper.-get1(SwipeHelper.this).sendAccessibilityEvent(2);
                  SwipeHelper.-get1(SwipeHelper.this).getLocationOnScreen(SwipeHelper.-get6(SwipeHelper.this));
                  int i = (int)paramMotionEvent.getRawX();
                  int j = SwipeHelper.-get6(SwipeHelper.this)[0];
                  int k = (int)paramMotionEvent.getRawY();
                  int m = SwipeHelper.-get6(SwipeHelper.this)[1];
                  SwipeHelper.-get4(SwipeHelper.this).onLongPress(SwipeHelper.-get1(SwipeHelper.this), i - j, k - m);
                }
              };
            }
            this.mHandler.postDelayed(this.mWatchLongPress, this.mLongPressTimeout);
            continue;
            if ((this.mCurrView != null) && (!this.mLongPressSent))
            {
              this.mVelocityTracker.addMovement(paramMotionEvent);
              float f2 = getPos(paramMotionEvent);
              float f1 = getPerpendicularPos(paramMotionEvent);
              f2 -= this.mInitialTouchPos;
              float f3 = this.mPerpendicularInitialTouchPos;
              if ((Math.abs(f2) > this.mPagingTouchSlop) && (Math.abs(f2) > Math.abs(f1 - f3)))
              {
                this.mCallback.onBeginDrag(this.mCurrView);
                this.mDragging = true;
                this.mInitialTouchPos = getPos(paramMotionEvent);
                this.mTranslation = getTranslation(this.mCurrView);
                removeLongPressCallback();
              }
            }
          }
        }
      }
    }
    if (!this.mDragging) {}
    for (boolean bool1 = this.mLongPressSent;; bool1 = true)
    {
      this.mDragging = false;
      this.mCurrView = null;
      this.mLongPressSent = false;
      removeLongPressCallback();
      if (!bool1) {
        break;
      }
      return true;
    }
  }
  
  protected void onMoveUpdate(View paramView, float paramFloat1, float paramFloat2) {}
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mLongPressSent) {
      return true;
    }
    if (!this.mDragging)
    {
      if (this.mCallback.getChildAtPosition(paramMotionEvent) != null)
      {
        onInterceptTouchEvent(paramMotionEvent);
        return true;
      }
      removeLongPressCallback();
      return false;
    }
    this.mVelocityTracker.addMovement(paramMotionEvent);
    switch (paramMotionEvent.getAction())
    {
    }
    do
    {
      do
      {
        return true;
      } while (this.mCurrView == null);
      f1 = getPos(paramMotionEvent) - this.mInitialTouchPos;
      float f3 = Math.abs(f1);
      if (f3 >= getFalsingThreshold()) {
        this.mTouchAboveFalsingThreshold = true;
      }
      if (this.mCallback.canChildBeDismissed(this.mCurrView)) {}
      for (;;)
      {
        setTranslation(this.mCurrView, this.mTranslation + f1);
        updateSwipeProgressFromOffset(this.mCurrView, this.mCanCurrViewBeDimissed);
        onMoveUpdate(this.mCurrView, this.mTranslation + f1, f1);
        return true;
        float f4 = getSize(this.mCurrView);
        float f2 = 0.25F * f4;
        if (f3 >= f4)
        {
          if (f1 > 0.0F) {
            f1 = f2;
          } else {
            f1 = -f2;
          }
        }
        else {
          f1 = f2 * (float)Math.sin(f1 / f4 * 1.5707963267948966D);
        }
      }
    } while (this.mCurrView == null);
    this.mVelocityTracker.computeCurrentVelocity(1000, getMaxVelocity());
    float f1 = getVelocity(this.mVelocityTracker);
    boolean bool;
    if (!handleUpEvent(paramMotionEvent, this.mCurrView, f1, getTranslation(this.mCurrView)))
    {
      if (!isDismissGesture(paramMotionEvent)) {
        break label346;
      }
      paramMotionEvent = this.mCurrView;
      if (!swipedFastEnough()) {
        break label340;
      }
      bool = false;
      dismissChild(paramMotionEvent, f1, bool);
    }
    for (;;)
    {
      this.mCurrView = null;
      this.mDragging = false;
      return true;
      label340:
      bool = true;
      break;
      label346:
      this.mCallback.onDragCancelled(this.mCurrView);
      snapChild(this.mCurrView, 0.0F, f1);
    }
  }
  
  public void onTranslationUpdate(View paramView, float paramFloat, boolean paramBoolean)
  {
    updateSwipeProgressFromOffset(paramView, paramBoolean, paramFloat);
  }
  
  protected void prepareDismissAnimation(View paramView, Animator paramAnimator) {}
  
  protected void prepareSnapBackAnimation(View paramView, Animator paramAnimator) {}
  
  public void removeLongPressCallback()
  {
    if (this.mWatchLongPress != null)
    {
      this.mHandler.removeCallbacks(this.mWatchLongPress);
      this.mWatchLongPress = null;
    }
  }
  
  public void setDensityScale(float paramFloat)
  {
    this.mDensityScale = paramFloat;
  }
  
  public void setDisableHardwareLayers(boolean paramBoolean)
  {
    this.mDisableHwLayers = paramBoolean;
  }
  
  public void setLongPressListener(LongPressListener paramLongPressListener)
  {
    this.mLongPressListener = paramLongPressListener;
  }
  
  public void setPagingTouchSlop(float paramFloat)
  {
    this.mPagingTouchSlop = paramFloat;
  }
  
  protected void setTranslation(View paramView, float paramFloat)
  {
    if (paramView == null) {
      return;
    }
    if (this.mSwipeDirection == 0)
    {
      paramView.setTranslationX(paramFloat);
      return;
    }
    paramView.setTranslationY(paramFloat);
  }
  
  public void snapChild(final View paramView, final float paramFloat1, float paramFloat2)
  {
    final boolean bool = this.mCallback.canChildBeDismissed(paramView);
    Animator localAnimator = getViewTranslationAnimator(paramView, paramFloat1, new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        SwipeHelper.this.onTranslationUpdate(paramView, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue(), bool);
      }
    });
    if (localAnimator == null) {
      return;
    }
    localAnimator.setDuration(150L);
    localAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        SwipeHelper.-set1(SwipeHelper.this, false);
        SwipeHelper.-wrap0(SwipeHelper.this, paramView, bool);
        SwipeHelper.-get0(SwipeHelper.this).onChildSnappedBack(paramView, paramFloat1);
      }
    });
    prepareSnapBackAnimation(paramView, localAnimator);
    this.mSnappingChild = true;
    localAnimator.start();
  }
  
  public void snapChildIfNeeded(View paramView, boolean paramBoolean, float paramFloat)
  {
    if ((this.mDragging) && (this.mCurrView == paramView)) {}
    while (this.mSnappingChild) {
      return;
    }
    int i = 0;
    Animator localAnimator = (Animator)this.mDismissPendingMap.get(paramView);
    if (localAnimator != null)
    {
      i = 1;
      localAnimator.cancel();
    }
    for (;;)
    {
      if (i != 0)
      {
        if (!paramBoolean) {
          break;
        }
        snapChild(paramView, paramFloat, 0.0F);
      }
      return;
      if (getTranslation(paramView) != 0.0F) {
        i = 1;
      }
    }
    snapChildInstantly(paramView);
  }
  
  protected boolean swipedFarEnough()
  {
    return Math.abs(getTranslation(this.mCurrView)) > getSize(this.mCurrView) * 0.4D;
  }
  
  protected boolean swipedFastEnough()
  {
    float f1 = getVelocity(this.mVelocityTracker);
    float f2 = getTranslation(this.mCurrView);
    if (Math.abs(f1) > getEscapeVelocity())
    {
      int i;
      if (f1 > 0.0F)
      {
        i = 1;
        if (f2 <= 0.0F) {
          break label60;
        }
      }
      label60:
      for (int j = 1;; j = 0)
      {
        if (i != j) {
          break label66;
        }
        return true;
        i = 0;
        break;
      }
      label66:
      return false;
    }
    return false;
  }
  
  public static abstract interface Callback
  {
    public abstract boolean canChildBeDismissed(View paramView);
    
    public abstract View getChildAtPosition(MotionEvent paramMotionEvent);
    
    public abstract float getFalsingThresholdFactor();
    
    public abstract boolean isAntiFalsingNeeded();
    
    public abstract void onBeginDrag(View paramView);
    
    public abstract void onChildDismissed(View paramView);
    
    public abstract void onChildSnappedBack(View paramView, float paramFloat);
    
    public abstract void onDragCancelled(View paramView);
    
    public abstract boolean updateSwipeProgress(View paramView, boolean paramBoolean, float paramFloat);
  }
  
  public static abstract interface LongPressListener
  {
    public abstract boolean onLongPress(View paramView, int paramInt1, int paramInt2);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\SwipeHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */