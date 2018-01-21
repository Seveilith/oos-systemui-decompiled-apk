package com.android.systemui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.android.systemui.statusbar.phone.StatusBarIconController;

public class BatteryDashChargeView
  extends ImageView
{
  private boolean mDark;
  private float mDarkIntensity;
  private int mIconTint = -1;
  private int mLevel = -1;
  private final Rect mTintArea = new Rect();
  
  public BatteryDashChargeView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public BatteryDashChargeView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public BatteryDashChargeView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  private void applyIconTint()
  {
    setImageTintList(ColorStateList.valueOf(StatusBarIconController.getTint(this.mTintArea, this, this.mIconTint)));
  }
  
  private int getImageResId()
  {
    int j = 0;
    int i;
    if (this.mLevel == 0) {
      if (!this.mDark) {
        i = 2130837883;
      }
    }
    do
    {
      do
      {
        return i;
        return 2130837884;
        if ((this.mLevel > 0) && (this.mLevel <= 11))
        {
          if (!this.mDark) {
            return 2130837885;
          }
          return 2130837900;
        }
        if ((this.mLevel > 11) && (this.mLevel <= 17))
        {
          if (!this.mDark) {
            return 2130837901;
          }
          return 2130837902;
        }
        if ((this.mLevel > 17) && (this.mLevel <= 23))
        {
          if (!this.mDark) {
            return 2130837903;
          }
          return 2130837904;
        }
        if ((this.mLevel > 23) && (this.mLevel <= 29))
        {
          if (!this.mDark) {
            return 2130837905;
          }
          return 2130837906;
        }
        if ((this.mLevel > 29) && (this.mLevel <= 35))
        {
          if (!this.mDark) {
            return 2130837907;
          }
          return 2130837908;
        }
        if ((this.mLevel > 35) && (this.mLevel <= 41))
        {
          if (!this.mDark) {
            return 2130837909;
          }
          return 2130837910;
        }
        if ((this.mLevel > 41) && (this.mLevel <= 47))
        {
          if (!this.mDark) {
            return 2130837911;
          }
          return 2130837912;
        }
        if ((this.mLevel > 47) && (this.mLevel <= 53))
        {
          if (!this.mDark) {
            return 2130837913;
          }
          return 2130837914;
        }
        if ((this.mLevel > 53) && (this.mLevel <= 59))
        {
          if (!this.mDark) {
            return 2130837915;
          }
          return 2130837916;
        }
        if ((this.mLevel > 59) && (this.mLevel <= 65))
        {
          if (!this.mDark) {
            return 2130837886;
          }
          return 2130837887;
        }
        if ((this.mLevel > 65) && (this.mLevel <= 71))
        {
          if (!this.mDark) {
            return 2130837888;
          }
          return 2130837889;
        }
        if ((this.mLevel > 71) && (this.mLevel <= 77))
        {
          if (!this.mDark) {
            return 2130837890;
          }
          return 2130837891;
        }
        if ((this.mLevel > 77) && (this.mLevel <= 83))
        {
          if (!this.mDark) {
            return 2130837892;
          }
          return 2130837893;
        }
        if ((this.mLevel > 83) && (this.mLevel <= 89))
        {
          if (!this.mDark) {
            return 2130837894;
          }
          return 2130837895;
        }
        if ((this.mLevel > 89) && (this.mLevel <= 95))
        {
          if (!this.mDark) {
            return 2130837896;
          }
          return 2130837897;
        }
        i = j;
      } while (this.mLevel <= 95);
      i = j;
    } while (this.mLevel > 100);
    if (!this.mDark) {
      return 2130837898;
    }
    return 2130837899;
  }
  
  public void setIconTint(int paramInt, float paramFloat, Rect paramRect)
  {
    int i;
    if ((paramInt != this.mIconTint) || (paramFloat != this.mDarkIntensity)) {
      i = 1;
    }
    for (;;)
    {
      this.mIconTint = paramInt;
      this.mDarkIntensity = paramFloat;
      this.mTintArea.set(paramRect);
      if (i != 0) {
        applyIconTint();
      }
      return;
      if (this.mTintArea.equals(paramRect)) {
        i = 0;
      } else {
        i = 1;
      }
    }
  }
  
  public void setLevel(int paramInt, boolean paramBoolean)
  {
    if ((paramInt != this.mLevel) || (paramBoolean))
    {
      this.mLevel = paramInt;
      setImageResource(getImageResId());
    }
  }
  
  public void updateDisplayAndTextSize(Context paramContext)
  {
    setLevel(this.mLevel, true);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\BatteryDashChargeView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */