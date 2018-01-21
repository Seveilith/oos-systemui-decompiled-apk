package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import com.android.systemui.DejankUtils;
import com.android.systemui.util.Utils;

public class PhoneStatusBarView
  extends PanelBar
{
  private static int PFLAG_FORCE_LAYOUT = Utils.getIntField(null, "android.view.View", "PFLAG_FORCE_LAYOUT", 0);
  PhoneStatusBar mBar;
  private final PhoneStatusBarTransitions mBarTransitions = new PhoneStatusBarTransitions(this);
  private Runnable mHideExpandedRunnable = new Runnable()
  {
    public void run()
    {
      if (PhoneStatusBarView.-get0(PhoneStatusBarView.this) == 0.0F) {
        PhoneStatusBarView.this.mBar.makeExpandedInvisible();
      }
    }
  };
  boolean mIsFullyOpenedPanel = false;
  private float mMinFraction;
  private float mPanelFraction;
  private ScrimController mScrimController;
  View mTraceView;
  
  public PhoneStatusBarView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void updateScrimFraction()
  {
    float f = Math.max(this.mPanelFraction, this.mMinFraction);
    this.mScrimController.setPanelExpansion(f);
  }
  
  public BarTransitions getBarTransitions()
  {
    return this.mBarTransitions;
  }
  
  public boolean isLayoutReady()
  {
    if (this.mTraceView == null) {
      return false;
    }
    int i = Utils.getIntField(this.mTraceView, "android.view.View", "mPrivateFlags", 0);
    return (PFLAG_FORCE_LAYOUT & i) == 0;
  }
  
  public void onClosingFinished()
  {
    super.onClosingFinished();
    this.mBar.onClosingFinished();
  }
  
  public void onDensityOrFontScaleChanged()
  {
    ViewGroup.LayoutParams localLayoutParams = getLayoutParams();
    localLayoutParams.height = getResources().getDimensionPixelSize(2131755669);
    setLayoutParams(localLayoutParams);
  }
  
  public void onExpandingFinished()
  {
    super.onExpandingFinished();
    this.mScrimController.onExpandingFinished();
  }
  
  public void onFinishInflate()
  {
    this.mBarTransitions.init();
    this.mTraceView = findViewById(2131952268);
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!this.mBar.interceptTouchEvent(paramMotionEvent)) {
      return super.onInterceptTouchEvent(paramMotionEvent);
    }
    return true;
  }
  
  public void onPanelCollapsed()
  {
    super.onPanelCollapsed();
    DejankUtils.postAfterTraversal(this.mHideExpandedRunnable);
    this.mIsFullyOpenedPanel = false;
    this.mBar.hideDismissAnimate(false);
  }
  
  public void onPanelFullyOpened()
  {
    super.onPanelFullyOpened();
    if (!this.mIsFullyOpenedPanel) {
      this.mPanel.sendAccessibilityEvent(32);
    }
    this.mIsFullyOpenedPanel = true;
  }
  
  public void onPanelPeeked()
  {
    super.onPanelPeeked();
    this.mBar.makeExpandedVisible(false);
  }
  
  public boolean onRequestSendAccessibilityEventInternal(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    if (super.onRequestSendAccessibilityEventInternal(paramView, paramAccessibilityEvent))
    {
      paramView = AccessibilityEvent.obtain();
      onInitializeAccessibilityEvent(paramView);
      dispatchPopulateAccessibilityEvent(paramView);
      paramAccessibilityEvent.appendRecord(paramView);
      return true;
    }
    return false;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!this.mBar.interceptTouchEvent(paramMotionEvent)) {
      return super.onTouchEvent(paramMotionEvent);
    }
    return true;
  }
  
  public void onTrackingStarted()
  {
    super.onTrackingStarted();
    this.mBar.onTrackingStarted();
    this.mScrimController.onTrackingStarted();
    removePendingHideExpandedRunnables();
  }
  
  public void onTrackingStopped(boolean paramBoolean)
  {
    super.onTrackingStopped(paramBoolean);
    this.mBar.onTrackingStopped(paramBoolean);
  }
  
  public boolean panelEnabled()
  {
    return this.mBar.panelsEnabled();
  }
  
  public void panelExpansionChanged(float paramFloat, boolean paramBoolean)
  {
    super.panelExpansionChanged(paramFloat, paramBoolean);
    if (paramFloat > this.mPanelFraction) {
      if (this.mBar.haveAnyDismissableNotification()) {
        this.mBar.showDismissAnimate(true);
      }
    }
    for (;;)
    {
      this.mPanelFraction = paramFloat;
      updateScrimFraction();
      if ((Utils.isSupportHideNavBar()) && (this.mBar.getNavigationBarView() != null)) {
        this.mBar.getNavigationBarView().onExpandChanged(paramBoolean);
      }
      return;
      if (paramFloat < this.mPanelFraction) {
        this.mBar.hideDismissAnimate(true);
      }
    }
  }
  
  public void panelScrimMinFractionChanged(float paramFloat)
  {
    if (this.mMinFraction != paramFloat)
    {
      this.mMinFraction = paramFloat;
      if (paramFloat != 0.0F) {
        this.mScrimController.animateNextChange();
      }
      updateScrimFraction();
    }
  }
  
  public void removePendingHideExpandedRunnables()
  {
    DejankUtils.removeCallbacks(this.mHideExpandedRunnable);
  }
  
  public void setBar(PhoneStatusBar paramPhoneStatusBar)
  {
    this.mBar = paramPhoneStatusBar;
  }
  
  public void setScrimController(ScrimController paramScrimController)
  {
    this.mScrimController = paramScrimController;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\PhoneStatusBarView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */