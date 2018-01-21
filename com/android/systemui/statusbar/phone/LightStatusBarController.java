package com.android.systemui.statusbar.phone;

import android.graphics.Rect;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback;

public class LightStatusBarController
  implements BatteryController.BatteryStateChangeCallback
{
  private final BatteryController mBatteryController;
  private boolean mDockedLight;
  private int mDockedStackVisibility;
  private FingerprintUnlockController mFingerprintUnlockController;
  private boolean mFullscreenLight;
  private int mFullscreenStackVisibility;
  private final StatusBarIconController mIconController;
  private final Rect mLastDockedBounds = new Rect();
  private final Rect mLastFullscreenBounds = new Rect();
  private int mLastStatusBarMode;
  
  public LightStatusBarController(StatusBarIconController paramStatusBarIconController, BatteryController paramBatteryController)
  {
    this.mIconController = paramStatusBarIconController;
    this.mBatteryController = paramBatteryController;
    paramBatteryController.addStateChangedCallback(this);
  }
  
  private boolean animateChange()
  {
    if (this.mFingerprintUnlockController == null) {
      return false;
    }
    int i = this.mFingerprintUnlockController.getMode();
    if (i != 2) {
      return i != 1;
    }
    return false;
  }
  
  private boolean isLight(int paramInt1, int paramInt2)
  {
    int j = 1;
    int i = j;
    if (paramInt2 != 4)
    {
      if (paramInt2 == 6) {
        i = j;
      }
    }
    else {
      if ((paramInt1 & 0x2000) == 0) {
        break label43;
      }
    }
    label43:
    for (boolean bool = true;; bool = false)
    {
      if (i == 0) {
        break label49;
      }
      return bool;
      i = 0;
      break;
    }
    label49:
    return false;
  }
  
  private void update(Rect paramRect1, Rect paramRect2)
  {
    int i;
    if (paramRect2.isEmpty())
    {
      i = 0;
      if (((this.mFullscreenLight) && (this.mDockedLight)) || ((this.mFullscreenLight) && (i == 0))) {
        break label99;
      }
      if (((!this.mFullscreenLight) && (!this.mDockedLight)) || ((!this.mFullscreenLight) && (i == 0))) {
        break label120;
      }
      if (!this.mFullscreenLight) {
        break label133;
      }
      label66:
      if (!paramRect1.isEmpty()) {
        break label138;
      }
      this.mIconController.setIconsDarkArea(null);
    }
    for (;;)
    {
      this.mIconController.setIconsDark(true, animateChange());
      return;
      i = 1;
      break;
      label99:
      this.mIconController.setIconsDarkArea(null);
      this.mIconController.setIconsDark(true, animateChange());
      return;
      label120:
      this.mIconController.setIconsDark(false, animateChange());
      return;
      label133:
      paramRect1 = paramRect2;
      break label66;
      label138:
      this.mIconController.setIconsDarkArea(paramRect1);
    }
  }
  
  public void onBatteryLevelChanged(int paramInt, boolean paramBoolean1, boolean paramBoolean2) {}
  
  public void onBatteryPercentShowChange(boolean paramBoolean) {}
  
  public void onBatteryStyleChanged(int paramInt) {}
  
  public void onFastChargeChanged(boolean paramBoolean) {}
  
  public void onPowerSaveChanged(boolean paramBoolean)
  {
    onSystemUiVisibilityChanged(this.mFullscreenStackVisibility, this.mDockedStackVisibility, 0, this.mLastFullscreenBounds, this.mLastDockedBounds, true, this.mLastStatusBarMode);
  }
  
  public void onSystemUiVisibilityChanged(int paramInt1, int paramInt2, int paramInt3, Rect paramRect1, Rect paramRect2, boolean paramBoolean, int paramInt4)
  {
    int i = this.mFullscreenStackVisibility;
    paramInt1 = paramInt3 & i | paramInt1 & paramInt3;
    int j = this.mDockedStackVisibility;
    paramInt2 = paramInt3 & j | paramInt2 & paramInt3;
    if ((((paramInt1 ^ i) & 0x2000) != 0) || (((paramInt2 ^ j) & 0x2000) != 0))
    {
      this.mFullscreenLight = isLight(paramInt1, paramInt4);
      this.mDockedLight = isLight(paramInt2, paramInt4);
      update(paramRect1, paramRect2);
    }
    for (;;)
    {
      this.mFullscreenStackVisibility = paramInt1;
      this.mDockedStackVisibility = paramInt2;
      this.mLastStatusBarMode = paramInt4;
      this.mLastFullscreenBounds.set(paramRect1);
      this.mLastDockedBounds.set(paramRect2);
      return;
      if ((paramBoolean) || (!this.mLastFullscreenBounds.equals(paramRect1)) || (!this.mLastDockedBounds.equals(paramRect2))) {
        break;
      }
    }
  }
  
  public void setFingerprintUnlockController(FingerprintUnlockController paramFingerprintUnlockController)
  {
    this.mFingerprintUnlockController = paramFingerprintUnlockController;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\LightStatusBarController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */