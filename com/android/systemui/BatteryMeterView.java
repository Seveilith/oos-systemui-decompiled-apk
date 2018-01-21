package com.android.systemui;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;

public class BatteryMeterView
  extends ImageView
  implements BatteryController.BatteryStateChangeCallback, TunerService.Tunable
{
  private boolean isIconDisableByTuner = false;
  private BatteryController mBatteryController;
  private int mBatteryStyle = 0;
  private final BatteryMeterDrawable mDrawable;
  private boolean mFastCharge = false;
  private final String mSlotBattery;
  
  public BatteryMeterView(Context paramContext)
  {
    this(paramContext, null, 0);
  }
  
  public BatteryMeterView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public BatteryMeterView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.BatteryMeterView, paramInt, 0);
    paramInt = paramAttributeSet.getColor(0, paramContext.getColor(2131492990));
    this.mDrawable = new BatteryMeterDrawable(paramContext, new Handler(), paramInt);
    this.mDrawable.setIconPlace(true);
    paramAttributeSet.recycle();
    this.mSlotBattery = paramContext.getString(17039407);
    setImageDrawable(this.mDrawable);
  }
  
  private void updateBatteryMeterVisibility()
  {
    if ((this.mBatteryStyle == 2) || (this.mFastCharge) || (this.isIconDisableByTuner))
    {
      setVisibility(8);
      return;
    }
    setVisibility(0);
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mBatteryController.addStateChangedCallback(this);
    this.mDrawable.startListening();
    TunerService.get(getContext()).addTunable(this, new String[] { "icon_blacklist" });
    updateBatteryMeterVisibility();
  }
  
  public void onBatteryLevelChanged(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    Context localContext = getContext();
    if (paramBoolean2) {}
    for (int i = 2131690167;; i = 2131690166)
    {
      setContentDescription(localContext.getString(i, new Object[] { Integer.valueOf(paramInt) }));
      return;
    }
  }
  
  public void onBatteryPercentShowChange(boolean paramBoolean) {}
  
  public void onBatteryStyleChanged(int paramInt)
  {
    if (this.mBatteryStyle != paramInt)
    {
      this.mBatteryStyle = paramInt;
      updateBatteryMeterVisibility();
      requestLayout();
    }
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.mBatteryController.removeStateChangedCallback(this);
    this.mDrawable.stopListening();
    TunerService.get(getContext()).removeTunable(this);
  }
  
  public void onFastChargeChanged(boolean paramBoolean)
  {
    if (this.mFastCharge != paramBoolean)
    {
      this.mFastCharge = paramBoolean;
      updateBatteryMeterVisibility();
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getSize(paramInt1);
    paramInt1 = View.MeasureSpec.getSize(paramInt2);
    if ((this.mBatteryStyle == 1) && (i != paramInt1)) {
      paramInt1 += 3;
    }
    for (paramInt2 = paramInt1;; paramInt2 = i)
    {
      setMeasuredDimension(paramInt2, paramInt1);
      return;
      onSizeChanged(i, paramInt1, 0, 0);
    }
  }
  
  public void onPowerSaveChanged(boolean paramBoolean) {}
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    if ("icon_blacklist".equals(paramString1))
    {
      this.isIconDisableByTuner = StatusBarIconController.getIconBlacklist(paramString2).contains(this.mSlotBattery);
      updateBatteryMeterVisibility();
    }
  }
  
  public void setBatteryController(BatteryController paramBatteryController)
  {
    this.mBatteryController = paramBatteryController;
    this.mDrawable.setBatteryController(paramBatteryController);
  }
  
  public void setDarkIntensity(float paramFloat)
  {
    this.mDrawable.setDarkIntensity(paramFloat);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\BatteryMeterView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */