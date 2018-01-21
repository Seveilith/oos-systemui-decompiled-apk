package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.internal.policy.DividerSnapAlgorithm;
import com.android.internal.policy.DividerSnapAlgorithm.SnapTarget;
import com.android.systemui.RecentsComponent;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.stackdivider.DividerView;
import com.android.systemui.stackdivider.WindowManagerProxy;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;

public class NavigationBarGestureHelper
  extends GestureDetector.SimpleOnGestureListener
  implements TunerService.Tunable
{
  private Context mContext;
  private Divider mDivider;
  private boolean mDockWindowEnabled;
  private boolean mDockWindowTouchSlopExceeded;
  private boolean mDownOnRecents;
  private int mDragMode;
  private boolean mIsRTL;
  private boolean mIsVertical;
  private final int mMinFlingVelocity;
  private NavigationBarView mNavigationBarView;
  private RecentsComponent mRecentsComponent;
  private final int mScrollTouchSlop;
  private final GestureDetector mTaskSwitcherDetector;
  private int mTouchDownX;
  private int mTouchDownY;
  private VelocityTracker mVelocityTracker;
  
  public NavigationBarGestureHelper(Context paramContext)
  {
    this.mContext = paramContext;
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(paramContext);
    this.mScrollTouchSlop = paramContext.getResources().getDimensionPixelSize(2131755356);
    this.mMinFlingVelocity = localViewConfiguration.getScaledMinimumFlingVelocity();
    this.mTaskSwitcherDetector = new GestureDetector(paramContext, this);
    TunerService.get(paramContext).addTunable(this, new String[] { "overview_nav_bar_gesture" });
  }
  
  private int calculateDragMode()
  {
    if ((!this.mIsVertical) || (this.mDivider.getView().isHorizontalDivision()))
    {
      if ((!this.mIsVertical) && (this.mDivider.getView().isHorizontalDivision())) {
        return 1;
      }
    }
    else {
      return 1;
    }
    return 0;
  }
  
  private boolean handleDockWindowEvent(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getActionMasked())
    {
    }
    for (;;)
    {
      return true;
      handleDragActionDownEvent(paramMotionEvent);
      continue;
      handleDragActionMoveEvent(paramMotionEvent);
      continue;
      handleDragActionUpEvent(paramMotionEvent);
    }
  }
  
  private void handleDragActionDownEvent(MotionEvent paramMotionEvent)
  {
    boolean bool2 = false;
    this.mVelocityTracker = VelocityTracker.obtain();
    this.mVelocityTracker.addMovement(paramMotionEvent);
    this.mDockWindowTouchSlopExceeded = false;
    this.mTouchDownX = ((int)paramMotionEvent.getX());
    this.mTouchDownY = ((int)paramMotionEvent.getY());
    if (this.mNavigationBarView != null)
    {
      paramMotionEvent = this.mNavigationBarView.getRecentsButton().getCurrentView();
      if (paramMotionEvent != null)
      {
        boolean bool1 = bool2;
        if (this.mTouchDownX >= paramMotionEvent.getLeft())
        {
          bool1 = bool2;
          if (this.mTouchDownX <= paramMotionEvent.getRight())
          {
            bool1 = bool2;
            if (this.mTouchDownY >= paramMotionEvent.getTop())
            {
              bool1 = bool2;
              if (this.mTouchDownY <= paramMotionEvent.getBottom()) {
                bool1 = true;
              }
            }
          }
        }
        this.mDownOnRecents = bool1;
      }
    }
    else
    {
      return;
    }
    this.mDownOnRecents = false;
  }
  
  private boolean handleDragActionMoveEvent(MotionEvent paramMotionEvent)
  {
    if (this.mVelocityTracker == null) {
      return false;
    }
    int j = (int)paramMotionEvent.getX();
    int i = (int)paramMotionEvent.getY();
    j = Math.abs(j - this.mTouchDownX);
    i = Math.abs(i - this.mTouchDownY);
    if ((this.mDivider == null) || (this.mRecentsComponent == null)) {
      return false;
    }
    label167:
    label183:
    label282:
    label291:
    label297:
    float f;
    if (!this.mDockWindowTouchSlopExceeded)
    {
      Rect localRect;
      int m;
      int k;
      if (!this.mIsVertical) {
        if ((i > this.mScrollTouchSlop) && (i > j))
        {
          i = 1;
          if ((!this.mDownOnRecents) || (i == 0) || (this.mDivider.getView().getWindowManagerProxy().getDockSide() != -1)) {
            break label400;
          }
          localRect = null;
          m = calculateDragMode();
          k = 0;
          if (m != 1) {
            break label297;
          }
          localRect = new Rect();
          DividerView localDividerView = this.mDivider.getView();
          if (!this.mIsVertical) {
            break label282;
          }
          i = (int)paramMotionEvent.getRawX();
          if (!this.mDivider.getView().isHorizontalDivision()) {
            break label291;
          }
          j = 2;
          localDividerView.calculateBoundsForPosition(i, j, localRect);
          paramMotionEvent = localRect;
          i = k;
        }
      }
      for (;;)
      {
        if (!this.mRecentsComponent.dockTopTask(m, i, paramMotionEvent, 272)) {
          break label400;
        }
        this.mDragMode = m;
        if (this.mDragMode == 1) {
          this.mDivider.getView().startDragging(false, true);
        }
        this.mDockWindowTouchSlopExceeded = true;
        return true;
        i = 0;
        break;
        if ((j > this.mScrollTouchSlop) && (j > i))
        {
          i = 1;
          break;
        }
        i = 0;
        break;
        i = (int)paramMotionEvent.getRawY();
        break label167;
        j = 1;
        break label183;
        i = k;
        paramMotionEvent = localRect;
        if (m == 0)
        {
          i = k;
          paramMotionEvent = localRect;
          if (this.mTouchDownX < this.mContext.getResources().getDisplayMetrics().widthPixels / 2)
          {
            i = 1;
            paramMotionEvent = localRect;
          }
        }
      }
    }
    else
    {
      if (this.mDragMode != 1) {
        break label410;
      }
      if (this.mIsVertical) {
        break label402;
      }
      f = paramMotionEvent.getRawY();
      i = (int)f;
      paramMotionEvent = this.mDivider.getView().getSnapAlgorithm().calculateSnapTarget(i, 0.0F, false);
      this.mDivider.getView().resizeStack(i, paramMotionEvent.position, paramMotionEvent);
    }
    for (;;)
    {
      label400:
      return false;
      label402:
      f = paramMotionEvent.getRawX();
      break;
      label410:
      if (this.mDragMode == 0) {
        this.mRecentsComponent.onDraggingInRecents(paramMotionEvent.getRawY());
      }
    }
  }
  
  private void handleDragActionUpEvent(MotionEvent paramMotionEvent)
  {
    this.mVelocityTracker.addMovement(paramMotionEvent);
    this.mVelocityTracker.computeCurrentVelocity(1000);
    int i;
    float f;
    if ((this.mDockWindowTouchSlopExceeded) && (this.mDivider != null) && (this.mRecentsComponent != null))
    {
      if (this.mDragMode != 1) {
        break label126;
      }
      DividerView localDividerView = this.mDivider.getView();
      if (!this.mIsVertical) {
        break label106;
      }
      i = (int)paramMotionEvent.getRawX();
      if (!this.mIsVertical) {
        break label115;
      }
      f = this.mVelocityTracker.getXVelocity();
      label84:
      localDividerView.stopDragging(i, f, true, false);
    }
    for (;;)
    {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
      return;
      label106:
      i = (int)paramMotionEvent.getRawY();
      break;
      label115:
      f = this.mVelocityTracker.getYVelocity();
      break label84;
      label126:
      if (this.mDragMode == 0) {
        this.mRecentsComponent.onDraggingInRecentsEnded(this.mVelocityTracker.getYVelocity());
      }
    }
  }
  
  private boolean interceptDockWindowEvent(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getActionMasked())
    {
    }
    for (;;)
    {
      return false;
      handleDragActionDownEvent(paramMotionEvent);
      continue;
      return handleDragActionMoveEvent(paramMotionEvent);
      handleDragActionUpEvent(paramMotionEvent);
    }
  }
  
  public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
  {
    int i = 0;
    float f1 = Math.abs(paramFloat1);
    float f2 = Math.abs(paramFloat2);
    if ((f1 > this.mMinFlingVelocity) && (this.mIsVertical))
    {
      if (f2 > f1) {
        i = 1;
      }
      label45:
      if ((i != 0) && (this.mRecentsComponent != null))
      {
        if (this.mIsRTL) {
          break label123;
        }
        if (!this.mIsVertical) {
          break label114;
        }
        if (paramFloat2 >= 0.0F) {
          break label108;
        }
      }
    }
    for (;;)
    {
      i = 1;
      if (i == 0) {
        break label158;
      }
      this.mRecentsComponent.showNextAffiliatedTask();
      return true;
      if (f1 <= f2) {
        break label45;
      }
      break;
      label108:
      label114:
      do
      {
        i = 0;
        break;
      } while (paramFloat1 >= 0.0F);
    }
    label123:
    if (this.mIsVertical) {
      if (paramFloat2 >= 0.0F) {}
    }
    for (;;)
    {
      i = 1;
      break;
      do
      {
        i = 0;
        break;
      } while (paramFloat1 <= 0.0F);
    }
    label158:
    this.mRecentsComponent.showPrevAffiliatedTask();
    return true;
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool = false;
    this.mTaskSwitcherDetector.onTouchEvent(paramMotionEvent);
    switch (paramMotionEvent.getAction() & 0xFF)
    {
    }
    for (;;)
    {
      if (this.mDockWindowEnabled) {
        bool = interceptDockWindowEvent(paramMotionEvent);
      }
      return bool;
      this.mTouchDownX = ((int)paramMotionEvent.getX());
      this.mTouchDownY = ((int)paramMotionEvent.getY());
      continue;
      int j = (int)paramMotionEvent.getX();
      int i = (int)paramMotionEvent.getY();
      j = Math.abs(j - this.mTouchDownX);
      i = Math.abs(i - this.mTouchDownY);
      if (!this.mIsVertical) {
        if ((j > this.mScrollTouchSlop) && (j > i)) {
          i = 1;
        }
      }
      while (i != 0)
      {
        return true;
        i = 0;
        continue;
        if ((i > this.mScrollTouchSlop) && (i > j)) {
          i = 1;
        } else {
          i = 0;
        }
      }
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool2 = this.mTaskSwitcherDetector.onTouchEvent(paramMotionEvent);
    boolean bool1 = bool2;
    if (this.mDockWindowEnabled) {
      bool1 = bool2 | handleDockWindowEvent(paramMotionEvent);
    }
    return bool1;
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    boolean bool2 = false;
    if (paramString1.equals("overview_nav_bar_gesture"))
    {
      boolean bool1 = bool2;
      if (paramString2 != null)
      {
        bool1 = bool2;
        if (Integer.parseInt(paramString2) != 0) {
          bool1 = true;
        }
      }
      this.mDockWindowEnabled = bool1;
    }
  }
  
  public void setBarState(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mIsVertical = paramBoolean1;
    this.mIsRTL = paramBoolean2;
  }
  
  public void setComponents(RecentsComponent paramRecentsComponent, Divider paramDivider, NavigationBarView paramNavigationBarView)
  {
    this.mRecentsComponent = paramRecentsComponent;
    this.mDivider = paramDivider;
    this.mNavigationBarView = paramNavigationBarView;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\NavigationBarGestureHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */