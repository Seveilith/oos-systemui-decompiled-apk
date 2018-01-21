package com.android.keyguard;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnHoverListener;
import android.view.accessibility.AccessibilityManager;

class LiftToActivateListener
  implements View.OnHoverListener
{
  private final AccessibilityManager mAccessibilityManager;
  private boolean mCachedClickableState;
  
  public LiftToActivateListener(Context paramContext)
  {
    this.mAccessibilityManager = ((AccessibilityManager)paramContext.getSystemService("accessibility"));
  }
  
  public boolean onHover(View paramView, MotionEvent paramMotionEvent)
  {
    if ((this.mAccessibilityManager.isEnabled()) && (this.mAccessibilityManager.isTouchExplorationEnabled())) {
      switch (paramMotionEvent.getActionMasked())
      {
      }
    }
    for (;;)
    {
      paramView.onHoverEvent(paramMotionEvent);
      return true;
      this.mCachedClickableState = paramView.isClickable();
      paramView.setClickable(false);
      continue;
      int i = (int)paramMotionEvent.getX();
      int j = (int)paramMotionEvent.getY();
      if ((i > paramView.getPaddingLeft()) && (j > paramView.getPaddingTop()) && (i < paramView.getWidth() - paramView.getPaddingRight()) && (j < paramView.getHeight() - paramView.getPaddingBottom())) {
        paramView.performClick();
      }
      paramView.setClickable(this.mCachedClickableState);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\LiftToActivateListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */