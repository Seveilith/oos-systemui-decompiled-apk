package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public abstract class PanelBar
  extends FrameLayout
{
  public static final String TAG = PanelBar.class.getSimpleName();
  PanelView mPanel;
  private int mState = 0;
  private boolean mTracking;
  
  public PanelBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public void collapsePanel(boolean paramBoolean1, boolean paramBoolean2, float paramFloat)
  {
    int i = 0;
    PanelView localPanelView = this.mPanel;
    if ((!paramBoolean1) || (localPanelView.isFullyCollapsed()))
    {
      localPanelView.resetViews();
      localPanelView.setExpandedFraction(0.0F);
      localPanelView.cancelPeek();
    }
    for (;;)
    {
      if ((i == 0) && (this.mState != 0))
      {
        go(0);
        onPanelCollapsed();
      }
      return;
      localPanelView.collapse(paramBoolean2, paramFloat);
      i = 1;
    }
  }
  
  public void go(int paramInt)
  {
    this.mState = paramInt;
  }
  
  public void onClosingFinished() {}
  
  public void onExpandingFinished() {}
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
  }
  
  public void onPanelCollapsed() {}
  
  public void onPanelFullyOpened() {}
  
  public void onPanelPeeked() {}
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool = true;
    if (!panelEnabled())
    {
      if (paramMotionEvent.getAction() == 0) {
        Log.v(TAG, String.format("onTouch: all panels disabled, ignoring touch at (%d,%d)", new Object[] { Integer.valueOf((int)paramMotionEvent.getX()), Integer.valueOf((int)paramMotionEvent.getY()) }));
      }
      return false;
    }
    if (paramMotionEvent.getAction() == 0)
    {
      PanelView localPanelView = this.mPanel;
      if (localPanelView == null)
      {
        Log.v(TAG, String.format("onTouch: no panel for touch at (%d,%d)", new Object[] { Integer.valueOf((int)paramMotionEvent.getX()), Integer.valueOf((int)paramMotionEvent.getY()) }));
        return true;
      }
      if (!localPanelView.isEnabled())
      {
        Log.v(TAG, String.format("onTouch: panel (%s) is disabled, ignoring touch at (%d,%d)", new Object[] { localPanelView, Integer.valueOf((int)paramMotionEvent.getX()), Integer.valueOf((int)paramMotionEvent.getY()) }));
        return true;
      }
    }
    if (this.mPanel != null) {
      bool = this.mPanel.onTouchEvent(paramMotionEvent);
    }
    return bool;
  }
  
  public void onTrackingStarted()
  {
    this.mTracking = true;
  }
  
  public void onTrackingStopped(boolean paramBoolean)
  {
    this.mTracking = false;
  }
  
  public boolean panelEnabled()
  {
    return true;
  }
  
  public void panelExpansionChanged(float paramFloat, boolean paramBoolean)
  {
    int j = 1;
    int k = 0;
    PanelView localPanelView = this.mPanel;
    int i;
    if (paramBoolean)
    {
      i = 0;
      localPanelView.setVisibility(i);
      i = k;
      if (paramBoolean)
      {
        if (this.mState == 0)
        {
          go(1);
          onPanelPeeked();
        }
        j = 0;
        if (localPanelView.getExpandedFraction() < 1.0F) {
          break label91;
        }
        i = 1;
      }
      label62:
      if ((i != 0) && (!this.mTracking)) {
        break label96;
      }
      if ((j != 0) && (!this.mTracking)) {
        break label106;
      }
    }
    label91:
    label96:
    label106:
    while (this.mState == 0)
    {
      return;
      i = 4;
      break;
      i = 0;
      break label62;
      go(2);
      onPanelFullyOpened();
      return;
    }
    go(0);
    onPanelCollapsed();
  }
  
  public abstract void panelScrimMinFractionChanged(float paramFloat);
  
  public void setBouncerShowing(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 4;; i = 0)
    {
      setImportantForAccessibility(i);
      if (this.mPanel != null) {
        this.mPanel.setImportantForAccessibility(i);
      }
      return;
    }
  }
  
  public void setPanel(PanelView paramPanelView)
  {
    this.mPanel = paramPanelView;
    paramPanelView.setBar(this);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\PanelBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */