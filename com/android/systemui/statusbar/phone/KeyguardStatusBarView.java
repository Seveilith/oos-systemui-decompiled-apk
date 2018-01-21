package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewGroupOverlay;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.android.systemui.BatteryMeterView;
import com.android.systemui.Interpolators;
import com.android.systemui.qs.QSPanel;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback;
import com.android.systemui.statusbar.policy.KeyguardUserSwitcher;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.UserInfoController.OnUserInfoChangedListener;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import java.text.NumberFormat;

public class KeyguardStatusBarView
  extends RelativeLayout
  implements BatteryController.BatteryStateChangeCallback
{
  private boolean mBatteryCharging;
  private BatteryController mBatteryController;
  private TextView mBatteryLevel;
  private boolean mBatteryListening;
  private TextView mCarrierLabel;
  private KeyguardUserSwitcher mKeyguardUserSwitcher;
  private boolean mKeyguardUserSwitcherShowing;
  private ImageView mMultiUserAvatar;
  private MultiUserSwitch mMultiUserSwitch;
  private int mSystemIconsBaseMargin;
  private View mSystemIconsContainer;
  private View mSystemIconsSuperContainer;
  private int mSystemIconsSwitcherHiddenExpandedMargin;
  private UserSwitcherController mUserSwitcherController;
  
  public KeyguardStatusBarView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void animateNextLayoutChange()
  {
    final int i = this.mSystemIconsSuperContainer.getLeft();
    if (this.mMultiUserSwitch.getParent() == this) {}
    for (final boolean bool = true;; bool = false)
    {
      getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          KeyguardStatusBarView.this.getViewTreeObserver().removeOnPreDrawListener(this);
          int i;
          int j;
          if (bool) {
            if (KeyguardStatusBarView.-get1(KeyguardStatusBarView.this).getParent() != KeyguardStatusBarView.this)
            {
              i = 1;
              KeyguardStatusBarView.-get2(KeyguardStatusBarView.this).setX(i);
              ViewPropertyAnimator localViewPropertyAnimator = KeyguardStatusBarView.-get2(KeyguardStatusBarView.this).animate().translationX(0.0F).setDuration(400L);
              if (i == 0) {
                break label173;
              }
              j = 300;
              label81:
              localViewPropertyAnimator.setStartDelay(j).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).start();
              if (i == 0) {
                break label178;
              }
              KeyguardStatusBarView.this.getOverlay().add(KeyguardStatusBarView.-get1(KeyguardStatusBarView.this));
              KeyguardStatusBarView.-get1(KeyguardStatusBarView.this).animate().alpha(0.0F).setDuration(300L).setStartDelay(0L).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(new Runnable()
              {
                public void run()
                {
                  KeyguardStatusBarView.-get1(KeyguardStatusBarView.this).setAlpha(1.0F);
                  KeyguardStatusBarView.this.getOverlay().remove(KeyguardStatusBarView.-get1(KeyguardStatusBarView.this));
                }
              }).start();
            }
          }
          for (;;)
          {
            return true;
            i = 0;
            break;
            i = 0;
            break;
            label173:
            j = 0;
            break label81;
            label178:
            KeyguardStatusBarView.-get1(KeyguardStatusBarView.this).setAlpha(0.0F);
            KeyguardStatusBarView.-get1(KeyguardStatusBarView.this).animate().alpha(1.0F).setDuration(300L).setStartDelay(200L).setInterpolator(Interpolators.ALPHA_IN);
          }
        }
      });
      return;
    }
  }
  
  private void loadDimens()
  {
    Resources localResources = getResources();
    this.mSystemIconsSwitcherHiddenExpandedMargin = localResources.getDimensionPixelSize(2131755487);
    this.mSystemIconsBaseMargin = localResources.getDimensionPixelSize(2131755398);
  }
  
  private void updateSystemIconsLayoutParams()
  {
    RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams)this.mSystemIconsSuperContainer.getLayoutParams();
    int i;
    if (this.mMultiUserSwitch.getVisibility() == 8)
    {
      i = this.mSystemIconsBaseMargin;
      if (!this.mKeyguardUserSwitcherShowing) {
        break label67;
      }
      i = this.mSystemIconsSwitcherHiddenExpandedMargin;
    }
    label67:
    for (;;)
    {
      if (i != localLayoutParams.getMarginEnd())
      {
        localLayoutParams.setMarginEnd(i);
        this.mSystemIconsSuperContainer.setLayoutParams(localLayoutParams);
      }
      return;
      i = 0;
      break;
    }
  }
  
  private void updateUserSwitcher()
  {
    if (this.mKeyguardUserSwitcher != null) {}
    for (boolean bool = true;; bool = false)
    {
      this.mMultiUserSwitch.setClickable(bool);
      this.mMultiUserSwitch.setFocusable(bool);
      this.mMultiUserSwitch.setKeyguardMode(bool);
      return;
    }
  }
  
  private void updateVisibilities()
  {
    if ((this.mMultiUserSwitch.getParent() == this) || (this.mKeyguardUserSwitcherShowing)) {
      if ((this.mMultiUserSwitch.getParent() == this) && (this.mKeyguardUserSwitcherShowing)) {
        removeView(this.mMultiUserSwitch);
      }
    }
    while ((this.mKeyguardUserSwitcher != null) || ((this.mUserSwitcherController != null) && (this.mUserSwitcherController.getSwitchableUserCount() > 1)))
    {
      return;
      if (this.mMultiUserSwitch.getParent() != null) {
        getOverlay().remove(this.mMultiUserSwitch);
      }
      addView(this.mMultiUserSwitch, 0);
    }
    this.mMultiUserSwitch.setVisibility(8);
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public void onBatteryLevelChanged(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    String str = NumberFormat.getPercentInstance().format(paramInt / 100.0D);
    this.mBatteryLevel.setText(str);
    if (this.mBatteryCharging != paramBoolean2) {}
    for (paramInt = 1;; paramInt = 0)
    {
      this.mBatteryCharging = paramBoolean2;
      if (paramInt != 0) {
        updateVisibilities();
      }
      return;
    }
  }
  
  public void onBatteryPercentShowChange(boolean paramBoolean) {}
  
  public void onBatteryStyleChanged(int paramInt) {}
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    paramConfiguration = (ViewGroup.MarginLayoutParams)this.mMultiUserAvatar.getLayoutParams();
    int i = getResources().getDimensionPixelSize(2131755497);
    paramConfiguration.height = i;
    paramConfiguration.width = i;
    this.mMultiUserAvatar.setLayoutParams(paramConfiguration);
    paramConfiguration = (ViewGroup.MarginLayoutParams)this.mMultiUserSwitch.getLayoutParams();
    paramConfiguration.width = getResources().getDimensionPixelSize(2131755495);
    paramConfiguration.setMarginEnd(getResources().getDimensionPixelSize(2131755486));
    this.mMultiUserSwitch.setLayoutParams(paramConfiguration);
    paramConfiguration = (ViewGroup.MarginLayoutParams)this.mSystemIconsSuperContainer.getLayoutParams();
    paramConfiguration.height = getResources().getDimensionPixelSize(2131755394);
    paramConfiguration.setMarginStart(getResources().getDimensionPixelSize(2131755397));
    this.mSystemIconsSuperContainer.setLayoutParams(paramConfiguration);
    this.mSystemIconsSuperContainer.setPaddingRelative(this.mSystemIconsSuperContainer.getPaddingStart(), this.mSystemIconsSuperContainer.getPaddingTop(), getResources().getDimensionPixelSize(2131755511), this.mSystemIconsSuperContainer.getPaddingBottom());
    paramConfiguration = (ViewGroup.MarginLayoutParams)this.mSystemIconsContainer.getLayoutParams();
    paramConfiguration.height = getResources().getDimensionPixelSize(2131755669);
    this.mSystemIconsContainer.setLayoutParams(paramConfiguration);
    paramConfiguration = (ViewGroup.MarginLayoutParams)this.mBatteryLevel.getLayoutParams();
    this.mBatteryLevel.setLayoutParams(paramConfiguration);
    this.mCarrierLabel.setTextSize(0, getResources().getDimensionPixelSize(17105173));
    paramConfiguration = (ViewGroup.MarginLayoutParams)this.mCarrierLabel.getLayoutParams();
    paramConfiguration.setMarginStart(getResources().getDimensionPixelSize(2131755508));
    this.mCarrierLabel.setLayoutParams(paramConfiguration);
    paramConfiguration = (ViewGroup.MarginLayoutParams)getLayoutParams();
    paramConfiguration.height = getResources().getDimensionPixelSize(2131755396);
    setLayoutParams(paramConfiguration);
  }
  
  public void onFastChargeChanged(boolean paramBoolean) {}
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mSystemIconsSuperContainer = findViewById(2131951921);
    this.mSystemIconsContainer = findViewById(2131951922);
    this.mMultiUserSwitch = ((MultiUserSwitch)findViewById(2131951919));
    this.mMultiUserAvatar = ((ImageView)findViewById(2131951920));
    this.mBatteryLevel = ((TextView)findViewById(2131952312));
    this.mCarrierLabel = ((TextView)findViewById(2131951923));
    loadDimens();
    updateUserSwitcher();
  }
  
  public void onPowerSaveChanged(boolean paramBoolean) {}
  
  public void setBatteryController(BatteryController paramBatteryController)
  {
    this.mBatteryController = paramBatteryController;
    ((BatteryMeterView)findViewById(2131952313)).setBatteryController(paramBatteryController);
  }
  
  public void setCerrierLabVisibility(boolean paramBoolean)
  {
    TextView localTextView;
    if (this.mCarrierLabel != null)
    {
      localTextView = this.mCarrierLabel;
      if (!paramBoolean) {
        break label24;
      }
    }
    label24:
    for (int i = 0;; i = 8)
    {
      localTextView.setVisibility(i);
      return;
    }
  }
  
  public void setKeyguardUserSwitcher(KeyguardUserSwitcher paramKeyguardUserSwitcher)
  {
    this.mKeyguardUserSwitcher = paramKeyguardUserSwitcher;
    this.mMultiUserSwitch.setKeyguardUserSwitcher(paramKeyguardUserSwitcher);
    updateUserSwitcher();
  }
  
  public void setKeyguardUserSwitcherShowing(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mKeyguardUserSwitcherShowing = paramBoolean1;
    if (paramBoolean2) {
      animateNextLayoutChange();
    }
    updateVisibilities();
    updateSystemIconsLayoutParams();
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (paramBoolean == this.mBatteryListening) {
      return;
    }
    this.mBatteryListening = paramBoolean;
    if (this.mBatteryListening)
    {
      this.mBatteryController.addStateChangedCallback(this);
      return;
    }
    this.mBatteryController.removeStateChangedCallback(this);
  }
  
  public void setQSPanel(QSPanel paramQSPanel)
  {
    this.mMultiUserSwitch.setQsPanel(paramQSPanel);
  }
  
  public void setUserInfoController(UserInfoController paramUserInfoController)
  {
    paramUserInfoController.addListener(new UserInfoController.OnUserInfoChangedListener()
    {
      public void onUserInfoChanged(String paramAnonymousString, Drawable paramAnonymousDrawable)
      {
        KeyguardStatusBarView.-get0(KeyguardStatusBarView.this).setImageDrawable(paramAnonymousDrawable);
      }
    });
  }
  
  public void setUserSwitcherController(UserSwitcherController paramUserSwitcherController)
  {
    this.mUserSwitcherController = paramUserSwitcherController;
    this.mMultiUserSwitch.setUserSwitcherController(paramUserSwitcherController);
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    if (paramInt != 0)
    {
      this.mSystemIconsSuperContainer.animate().cancel();
      this.mSystemIconsSuperContainer.setTranslationX(0.0F);
      this.mMultiUserSwitch.animate().cancel();
      this.mMultiUserSwitch.setAlpha(1.0F);
      return;
    }
    updateVisibilities();
    updateSystemIconsLayoutParams();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\KeyguardStatusBarView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */