package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewStub.OnInflateListener;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.android.systemui.AutoReinflateContainer;
import com.android.systemui.AutoReinflateContainer.InflateListener;
import com.android.systemui.qs.QSContainer;
import com.android.systemui.qs.customize.QSCustomizer;

public class NotificationsQuickSettingsContainer
  extends FrameLayout
  implements ViewStub.OnInflateListener, AutoReinflateContainer.InflateListener
{
  private int mBottomPadding;
  private boolean mCustomizerAnimating;
  private boolean mInflated;
  private View mKeyguardStatusBar;
  private AutoReinflateContainer mQsContainer;
  private boolean mQsExpanded;
  private View mStackScroller;
  private int mStackScrollerMargin;
  private View mUserSwitcher;
  
  public NotificationsQuickSettingsContainer(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void reloadWidth(View paramView)
  {
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)paramView.getLayoutParams();
    localLayoutParams.width = getContext().getResources().getDimensionPixelSize(2131755401);
    paramView.setLayoutParams(localLayoutParams);
  }
  
  private void setBottomMargin(View paramView, int paramInt)
  {
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)paramView.getLayoutParams();
    localLayoutParams.bottomMargin = paramInt;
    paramView.setLayoutParams(localLayoutParams);
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    int m = 0;
    int i;
    int j;
    label36:
    int k;
    label58:
    Object localObject2;
    label69:
    Object localObject1;
    if ((this.mInflated) && (this.mUserSwitcher.getVisibility() == 0))
    {
      i = 1;
      if (this.mKeyguardStatusBar.getVisibility() != 0) {
        break label119;
      }
      j = 1;
      k = m;
      if (this.mQsExpanded)
      {
        if (!this.mCustomizerAnimating) {
          break label125;
        }
        k = m;
      }
      if (k == 0) {
        break label131;
      }
      localObject2 = this.mStackScroller;
      if (k != 0) {
        break label140;
      }
      localObject1 = this.mStackScroller;
      label80:
      if (paramView != this.mQsContainer) {
        break label177;
      }
      if ((i == 0) || (j == 0)) {
        break label149;
      }
      localObject1 = this.mUserSwitcher;
    }
    for (;;)
    {
      return super.drawChild(paramCanvas, (View)localObject1, paramLong);
      i = 0;
      break;
      label119:
      j = 0;
      break label36;
      label125:
      k = 1;
      break label58;
      label131:
      localObject2 = this.mQsContainer;
      break label69;
      label140:
      localObject1 = this.mQsContainer;
      break label80;
      label149:
      if (j != 0) {
        localObject1 = this.mKeyguardStatusBar;
      } else if (i != 0) {
        localObject1 = this.mUserSwitcher;
      }
    }
    label177:
    if (paramView == this.mStackScroller)
    {
      if ((i != 0) && (j != 0)) {}
      for (localObject2 = this.mKeyguardStatusBar;; localObject2 = localObject1) {
        do
        {
          return super.drawChild(paramCanvas, (View)localObject2, paramLong);
        } while ((j == 0) && (i == 0));
      }
    }
    if (paramView == this.mUserSwitcher)
    {
      if ((i != 0) && (j != 0)) {}
      for (;;)
      {
        return super.drawChild(paramCanvas, (View)localObject1, paramLong);
        localObject1 = localObject2;
      }
    }
    if (paramView == this.mKeyguardStatusBar) {
      return super.drawChild(paramCanvas, (View)localObject2, paramLong);
    }
    return super.drawChild(paramCanvas, paramView, paramLong);
  }
  
  public WindowInsets onApplyWindowInsets(WindowInsets paramWindowInsets)
  {
    this.mBottomPadding = paramWindowInsets.getStableInsetBottom();
    setPadding(0, 0, 0, this.mBottomPadding);
    return paramWindowInsets;
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    reloadWidth(this.mQsContainer);
    reloadWidth(this.mStackScroller);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mQsContainer = ((AutoReinflateContainer)findViewById(2131952276));
    this.mQsContainer.addInflateListener(this);
    this.mStackScroller = findViewById(2131952277);
    this.mStackScrollerMargin = ((FrameLayout.LayoutParams)this.mStackScroller.getLayoutParams()).bottomMargin;
    this.mKeyguardStatusBar = findViewById(2131951918);
    ViewStub localViewStub = (ViewStub)findViewById(2131952278);
    localViewStub.setOnInflateListener(this);
    this.mUserSwitcher = localViewStub;
  }
  
  public void onInflate(ViewStub paramViewStub, View paramView)
  {
    if (paramViewStub == this.mUserSwitcher)
    {
      this.mUserSwitcher = paramView;
      this.mInflated = true;
    }
  }
  
  public void onInflated(View paramView)
  {
    ((QSContainer)paramView).getCustomizer().setContainer(this);
  }
  
  public void setCustomizerAnimating(boolean paramBoolean)
  {
    if (this.mCustomizerAnimating != paramBoolean)
    {
      this.mCustomizerAnimating = paramBoolean;
      invalidate();
    }
  }
  
  public void setCustomizerShowing(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      setPadding(0, 0, 0, 0);
      setBottomMargin(this.mStackScroller, 0);
      return;
    }
    setPadding(0, 0, 0, this.mBottomPadding);
    setBottomMargin(this.mStackScroller, this.mStackScrollerMargin);
  }
  
  public void setQsExpanded(boolean paramBoolean)
  {
    if (this.mQsExpanded != paramBoolean)
    {
      this.mQsExpanded = paramBoolean;
      invalidate();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\NotificationsQuickSettingsContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */