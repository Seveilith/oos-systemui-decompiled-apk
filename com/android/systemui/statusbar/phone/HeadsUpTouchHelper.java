package com.android.systemui.statusbar.phone;

import android.service.notification.StatusBarNotification;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.stack.NotificationStackScrollLayout;

public class HeadsUpTouchHelper
{
  private boolean mCollapseSnoozes;
  private HeadsUpManager mHeadsUpManager;
  private float mInitialTouchX;
  private float mInitialTouchY;
  private NotificationPanelView mPanel;
  private ExpandableNotificationRow mPickedChild;
  private NotificationStackScrollLayout mStackScroller;
  private float mTouchSlop;
  private boolean mTouchingHeadsUpView;
  private boolean mTrackingHeadsUp;
  private int mTrackingPointer;
  
  public HeadsUpTouchHelper(HeadsUpManager paramHeadsUpManager, NotificationStackScrollLayout paramNotificationStackScrollLayout, NotificationPanelView paramNotificationPanelView)
  {
    this.mHeadsUpManager = paramHeadsUpManager;
    this.mStackScroller = paramNotificationStackScrollLayout;
    this.mPanel = paramNotificationPanelView;
    this.mTouchSlop = ViewConfiguration.get(paramNotificationStackScrollLayout.getContext()).getScaledTouchSlop();
  }
  
  private void endMotion()
  {
    this.mTrackingPointer = -1;
    this.mPickedChild = null;
    this.mTouchingHeadsUpView = false;
  }
  
  private void setTrackingHeadsUp(boolean paramBoolean)
  {
    this.mTrackingHeadsUp = paramBoolean;
    this.mHeadsUpManager.setTrackingHeadsUp(paramBoolean);
    this.mPanel.setTrackingHeadsUp(paramBoolean);
  }
  
  public boolean isTrackingHeadsUp()
  {
    return this.mTrackingHeadsUp;
  }
  
  public void notifyFling(boolean paramBoolean)
  {
    if ((paramBoolean) && (this.mCollapseSnoozes)) {
      this.mHeadsUpManager.snooze();
    }
    this.mCollapseSnoozes = false;
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int j = 1;
    boolean bool = false;
    if ((!this.mTouchingHeadsUpView) && (paramMotionEvent.getActionMasked() != 0)) {
      return false;
    }
    int k = paramMotionEvent.findPointerIndex(this.mTrackingPointer);
    int i = k;
    if (k < 0)
    {
      i = 0;
      this.mTrackingPointer = paramMotionEvent.getPointerId(0);
    }
    float f1 = paramMotionEvent.getX(i);
    float f2 = paramMotionEvent.getY(i);
    switch (paramMotionEvent.getActionMasked())
    {
    case 4: 
    case 5: 
    default: 
    case 0: 
    case 6: 
    case 2: 
      float f3;
      do
      {
        do
        {
          do
          {
            return false;
            this.mInitialTouchY = f2;
            this.mInitialTouchX = f1;
            setTrackingHeadsUp(false);
            paramMotionEvent = this.mStackScroller.getChildAtRawPosition(f1, f2);
            this.mTouchingHeadsUpView = false;
          } while (!(paramMotionEvent instanceof ExpandableNotificationRow));
          this.mPickedChild = ((ExpandableNotificationRow)paramMotionEvent);
          if ((!this.mStackScroller.isExpanded()) && (this.mPickedChild.isHeadsUp())) {}
          for (bool = this.mPickedChild.isPinned();; bool = false)
          {
            this.mTouchingHeadsUpView = bool;
            return false;
          }
          k = paramMotionEvent.getPointerId(paramMotionEvent.getActionIndex());
        } while (this.mTrackingPointer != k);
        i = j;
        if (paramMotionEvent.getPointerId(0) != k) {
          i = 0;
        }
        this.mTrackingPointer = paramMotionEvent.getPointerId(i);
        this.mInitialTouchX = paramMotionEvent.getX(i);
        this.mInitialTouchY = paramMotionEvent.getY(i);
        return false;
        f3 = f2 - this.mInitialTouchY;
      } while ((!this.mTouchingHeadsUpView) || (Math.abs(f3) <= this.mTouchSlop) || (Math.abs(f3) <= Math.abs(f1 - this.mInitialTouchX)));
      setTrackingHeadsUp(true);
      if (f3 < 0.0F) {
        bool = true;
      }
      this.mCollapseSnoozes = bool;
      this.mInitialTouchX = f1;
      this.mInitialTouchY = f2;
      i = this.mPickedChild.getActualHeight();
      this.mPanel.setPanelScrimMinFraction(i / this.mPanel.getMaxPanelHeight());
      this.mPanel.startExpandMotion(f1, f2, true, i);
      this.mHeadsUpManager.unpinAll();
      this.mPanel.clearNotificationEffects();
      return true;
    }
    if ((this.mPickedChild != null) && (this.mTouchingHeadsUpView) && (this.mHeadsUpManager.shouldSwallowClick(this.mPickedChild.getStatusBarNotification().getKey())))
    {
      endMotion();
      return true;
    }
    endMotion();
    return false;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!this.mTrackingHeadsUp) {
      return false;
    }
    switch (paramMotionEvent.getActionMasked())
    {
    }
    for (;;)
    {
      return true;
      endMotion();
      setTrackingHeadsUp(false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\HeadsUpTouchHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */