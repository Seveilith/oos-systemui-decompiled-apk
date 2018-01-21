package com.android.systemui.recents.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.util.FloatProperty;
import android.util.Property;
import android.view.ViewDebug.ExportedProperty;
import android.widget.OverScroller;
import com.android.systemui.Interpolators;
import com.android.systemui.recents.misc.Utilities;
import java.io.PrintWriter;

public class TaskStackViewScroller
{
  private static final Property<TaskStackViewScroller, Float> STACK_SCROLL = new FloatProperty("stackScroll")
  {
    public Float get(TaskStackViewScroller paramAnonymousTaskStackViewScroller)
    {
      return Float.valueOf(paramAnonymousTaskStackViewScroller.getStackScroll());
    }
    
    public void setValue(TaskStackViewScroller paramAnonymousTaskStackViewScroller, float paramAnonymousFloat)
    {
      paramAnonymousTaskStackViewScroller.setStackScroll(paramAnonymousFloat);
    }
  };
  TaskStackViewScrollerCallbacks mCb;
  Context mContext;
  float mFinalAnimatedScroll;
  float mFlingDownScrollP;
  int mFlingDownY;
  @ViewDebug.ExportedProperty(category="recents")
  float mLastDeltaP = 0.0F;
  TaskStackLayoutAlgorithm mLayoutAlgorithm;
  ObjectAnimator mScrollAnimator;
  OverScroller mScroller;
  @ViewDebug.ExportedProperty(category="recents")
  float mStackScrollP;
  
  public TaskStackViewScroller(Context paramContext, TaskStackViewScrollerCallbacks paramTaskStackViewScrollerCallbacks, TaskStackLayoutAlgorithm paramTaskStackLayoutAlgorithm)
  {
    this.mContext = paramContext;
    this.mCb = paramTaskStackViewScrollerCallbacks;
    this.mScroller = new OverScroller(paramContext);
    this.mLayoutAlgorithm = paramTaskStackLayoutAlgorithm;
  }
  
  ObjectAnimator animateBoundScroll()
  {
    float f1 = getStackScroll();
    float f2 = getBoundedStackScroll(f1);
    if (Float.compare(f2, f1) != 0) {
      animateScroll(f2, null);
    }
    return this.mScrollAnimator;
  }
  
  void animateScroll(float paramFloat, int paramInt, final Runnable paramRunnable)
  {
    if ((this.mScrollAnimator != null) && (this.mScrollAnimator.isRunning()))
    {
      setStackScroll(this.mFinalAnimatedScroll);
      this.mScroller.forceFinished(true);
    }
    stopScroller();
    stopBoundScrollAnimation();
    if (Float.compare(this.mStackScrollP, paramFloat) != 0)
    {
      this.mFinalAnimatedScroll = paramFloat;
      this.mScrollAnimator = ObjectAnimator.ofFloat(this, STACK_SCROLL, new float[] { getStackScroll(), paramFloat });
      this.mScrollAnimator.setDuration(paramInt);
      this.mScrollAnimator.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
      this.mScrollAnimator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (paramRunnable != null) {
            paramRunnable.run();
          }
          TaskStackViewScroller.this.mScrollAnimator.removeAllListeners();
        }
      });
      this.mScrollAnimator.start();
    }
    while (paramRunnable == null) {
      return;
    }
    paramRunnable.run();
  }
  
  void animateScroll(float paramFloat, Runnable paramRunnable)
  {
    animateScroll(paramFloat, this.mContext.getResources().getInteger(2131623996), paramRunnable);
  }
  
  public boolean boundScroll()
  {
    float f1 = getStackScroll();
    float f2 = getBoundedStackScroll(f1);
    if (Float.compare(f2, f1) != 0)
    {
      setStackScroll(f2);
      return true;
    }
    return false;
  }
  
  boolean computeScroll()
  {
    if (this.mScroller.computeScrollOffset())
    {
      float f = this.mLayoutAlgorithm.getDeltaPForY(this.mFlingDownY, this.mScroller.getCurrY());
      this.mFlingDownScrollP += setDeltaStackScroll(this.mFlingDownScrollP, f);
      return true;
    }
    return false;
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("TaskStackViewScroller");
    paramPrintWriter.print(" stackScroll:");
    paramPrintWriter.print(this.mStackScrollP);
    paramPrintWriter.println();
  }
  
  public void fling(float paramFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    this.mFlingDownScrollP = paramFloat;
    this.mFlingDownY = paramInt1;
    this.mScroller.fling(0, paramInt2, 0, paramInt3, 0, 0, paramInt4, paramInt5, 0, paramInt6);
  }
  
  float getBoundedStackScroll(float paramFloat)
  {
    return Utilities.clamp(paramFloat, this.mLayoutAlgorithm.mMinScrollP, this.mLayoutAlgorithm.mMaxScrollP);
  }
  
  float getScrollAmountOutOfBounds(float paramFloat)
  {
    if (paramFloat < this.mLayoutAlgorithm.mMinScrollP) {
      return Math.abs(paramFloat - this.mLayoutAlgorithm.mMinScrollP);
    }
    if (paramFloat > this.mLayoutAlgorithm.mMaxScrollP) {
      return Math.abs(paramFloat - this.mLayoutAlgorithm.mMaxScrollP);
    }
    return 0.0F;
  }
  
  public float getStackScroll()
  {
    return this.mStackScrollP;
  }
  
  boolean isScrollOutOfBounds()
  {
    boolean bool = false;
    if (Float.compare(getScrollAmountOutOfBounds(this.mStackScrollP), 0.0F) != 0) {
      bool = true;
    }
    return bool;
  }
  
  void reset()
  {
    this.mStackScrollP = 0.0F;
    this.mLastDeltaP = 0.0F;
  }
  
  void resetDeltaScroll()
  {
    this.mLastDeltaP = 0.0F;
  }
  
  public float setDeltaStackScroll(float paramFloat1, float paramFloat2)
  {
    float f = paramFloat1 + paramFloat2;
    paramFloat1 = this.mLayoutAlgorithm.updateFocusStateOnScroll(this.mLastDeltaP + paramFloat1, f, this.mStackScrollP);
    setStackScroll(paramFloat1, AnimationProps.IMMEDIATE);
    this.mLastDeltaP = paramFloat2;
    return paramFloat1 - f;
  }
  
  public void setStackScroll(float paramFloat)
  {
    setStackScroll(paramFloat, AnimationProps.IMMEDIATE);
  }
  
  public void setStackScroll(float paramFloat, AnimationProps paramAnimationProps)
  {
    float f = this.mStackScrollP;
    this.mStackScrollP = paramFloat;
    if (this.mCb != null) {
      this.mCb.onStackScrollChanged(f, this.mStackScrollP, paramAnimationProps);
    }
  }
  
  public boolean setStackScrollToInitialState()
  {
    boolean bool = false;
    float f = this.mStackScrollP;
    setStackScroll(this.mLayoutAlgorithm.mInitialScrollP);
    if (Float.compare(f, this.mStackScrollP) != 0) {
      bool = true;
    }
    return bool;
  }
  
  void stopBoundScrollAnimation()
  {
    Utilities.cancelAnimationWithoutCallbacks(this.mScrollAnimator);
  }
  
  void stopScroller()
  {
    if (!this.mScroller.isFinished()) {
      this.mScroller.abortAnimation();
    }
  }
  
  public static abstract interface TaskStackViewScrollerCallbacks
  {
    public abstract void onStackScrollChanged(float paramFloat1, float paramFloat2, AnimationProps paramAnimationProps);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\TaskStackViewScroller.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */