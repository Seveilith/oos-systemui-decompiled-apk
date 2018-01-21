package com.android.systemui.volume;

import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.content.Context;
import android.provider.Settings.System;
import android.service.notification.ZenModeConfig;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.SystemUI;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.policy.ZenModeController.Callback;
import java.util.Objects;

public class ZenFooter
  extends LinearLayout
{
  private static final String TAG = Util.logTag(ZenFooter.class);
  private ZenModeConfig mConfig;
  private final Context mContext;
  private ZenModeController mController;
  private ImageView mIcon;
  private ImageView mSettingIcon;
  private final SpTexts mSpTexts;
  private TextView mSummaryLine1;
  private SystemUI mSysui;
  private VolumeDialog mVolumeDialog;
  private int mZen = -1;
  private final ZenModeController.Callback mZenCallback = new ZenModeController.Callback()
  {
    public void onConfigChanged(ZenModeConfig paramAnonymousZenModeConfig)
    {
      ZenFooter.-wrap2(ZenFooter.this, paramAnonymousZenModeConfig);
    }
    
    public void onZenChanged(int paramAnonymousInt)
    {
      ZenFooter.-wrap3(ZenFooter.this, paramAnonymousInt);
    }
  };
  
  public ZenFooter(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    this.mSpTexts = new SpTexts(this.mContext);
    paramContext = new LayoutTransition();
    paramContext.setDuration(new ValueAnimator().getDuration() / 2L);
    setLayoutTransition(paramContext);
  }
  
  private boolean isZenAlarms()
  {
    return this.mZen == 3;
  }
  
  private boolean isZenPriority()
  {
    return this.mZen == 1;
  }
  
  private void setConfig(ZenModeConfig paramZenModeConfig)
  {
    if (Objects.equals(this.mConfig, paramZenModeConfig)) {
      return;
    }
    this.mConfig = paramZenModeConfig;
    update();
  }
  
  private void setZen(int paramInt)
  {
    if (this.mZen == paramInt) {
      return;
    }
    this.mZen = paramInt;
    update();
  }
  
  public void cleanup()
  {
    this.mController.removeCallback(this.mZenCallback);
  }
  
  public void init(SystemUI paramSystemUI, VolumeDialog paramVolumeDialog, ZenModeController paramZenModeController)
  {
    this.mZen = paramZenModeController.getZen();
    this.mConfig = paramZenModeController.getConfig();
    this.mController = paramZenModeController;
    this.mSysui = paramSystemUI;
    this.mVolumeDialog = paramVolumeDialog;
    this.mSettingIcon.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (ZenFooter.-wrap1(ZenFooter.this)) {
          paramAnonymousView = ZenModePanel.ZEN_PRIORITY_SETTINGS;
        }
        for (;;)
        {
          ((PhoneStatusBar)ZenFooter.-get0(ZenFooter.this).getComponent(PhoneStatusBar.class)).startActivityDismissingKeyguard(paramAnonymousView, true, true);
          ZenFooter.-get1(ZenFooter.this).dismissWaitForRipple(5);
          return;
          if (ZenFooter.-wrap0(ZenFooter.this)) {
            paramAnonymousView = ZenModePanel.ZEN_SILENT_MODE_SETTINGS;
          } else {
            paramAnonymousView = ZenModePanel.ZEN_RING_MODE_SETTINGS;
          }
        }
      }
    });
    this.mController.addCallback(this.mZenCallback);
    update();
  }
  
  protected void onAttachedToWindow()
  {
    if (!KeyguardUpdateMonitor.getInstance(this.mContext).isDeviceProvisioned())
    {
      Log.i("ZenFooter", "disable setting button in ZenFooter because device is not provisioned!");
      this.mSettingIcon.setVisibility(4);
    }
    for (;;)
    {
      super.onAttachedToWindow();
      return;
      this.mSettingIcon.setVisibility(0);
    }
  }
  
  public void onConfigurationChanged()
  {
    this.mSpTexts.update();
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mIcon = ((ImageView)findViewById(2131952350));
    this.mSettingIcon = ((ImageView)findViewById(2131952352));
    this.mSummaryLine1 = ((TextView)findViewById(2131952351));
    this.mSpTexts.add(this.mSummaryLine1);
  }
  
  public void update()
  {
    Object localObject = this.mIcon;
    int i;
    if (isZenPriority())
    {
      i = 2130837943;
      ((ImageView)localObject).setImageResource(i);
      i = 0;
      if (isZenAlarms())
      {
        if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "oem_vibrate_under_silent", 0, KeyguardUpdateMonitor.getCurrentUser()) <= 0) {
          break label96;
        }
        i = 1;
      }
      label50:
      if (!isZenPriority()) {
        break label101;
      }
      localObject = this.mContext.getString(2131689987);
    }
    for (;;)
    {
      Util.setText(this.mSummaryLine1, (CharSequence)localObject);
      return;
      if (isZenAlarms())
      {
        i = 2130837946;
        break;
      }
      i = 2130837944;
      break;
      label96:
      i = 0;
      break label50;
      label101:
      if (isZenAlarms())
      {
        if (i != 0) {
          localObject = this.mContext.getString(2131690012);
        } else {
          localObject = this.mContext.getString(2131689986);
        }
      }
      else {
        localObject = this.mContext.getString(2131689988);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\volume\ZenFooter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */