package com.android.systemui;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.statusbar.BaseStatusBar;
import com.android.systemui.statusbar.ScrimView;
import com.android.systemui.statusbar.phone.KeyguardBouncer;
import com.android.systemui.statusbar.phone.LockscreenWallpaper;
import com.android.systemui.statusbar.phone.NotificationIconAreaController;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import com.android.systemui.statusbar.phone.QSTileHost;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.phone.StatusBarWindowManager;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BluetoothController;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.FlashlightController;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.statusbar.policy.SecurityController;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.policy.ZenModeController;

public class SystemUIFactory
{
  static SystemUIFactory mFactory;
  
  public static void createFromConfig(Context paramContext)
  {
    String str = paramContext.getString(2131689913);
    if ((str == null) || (str.length() == 0)) {
      throw new RuntimeException("No SystemUIFactory component configured");
    }
    try
    {
      mFactory = (SystemUIFactory)paramContext.getClassLoader().loadClass(str).newInstance();
      return;
    }
    catch (Throwable paramContext)
    {
      Log.w("SystemUIFactory", "Error creating SystemUIFactory component: " + str, paramContext);
      throw new RuntimeException(paramContext);
    }
  }
  
  public static SystemUIFactory getInstance()
  {
    return mFactory;
  }
  
  public AssistManager createAssistManager(BaseStatusBar paramBaseStatusBar, Context paramContext)
  {
    return new AssistManager(paramBaseStatusBar, paramContext);
  }
  
  public <T> T createInstance(Class<T> paramClass)
  {
    return null;
  }
  
  public KeyguardBouncer createKeyguardBouncer(Context paramContext, ViewMediatorCallback paramViewMediatorCallback, LockPatternUtils paramLockPatternUtils, StatusBarWindowManager paramStatusBarWindowManager, ViewGroup paramViewGroup)
  {
    return new KeyguardBouncer(paramContext, paramViewMediatorCallback, paramLockPatternUtils, paramStatusBarWindowManager, paramViewGroup);
  }
  
  public NotificationIconAreaController createNotificationIconAreaController(Context paramContext, PhoneStatusBar paramPhoneStatusBar)
  {
    return new NotificationIconAreaController(paramContext, paramPhoneStatusBar);
  }
  
  public QSTileHost createQSTileHost(Context paramContext, PhoneStatusBar paramPhoneStatusBar, BluetoothController paramBluetoothController, LocationController paramLocationController, RotationLockController paramRotationLockController, NetworkController paramNetworkController, ZenModeController paramZenModeController, HotspotController paramHotspotController, CastController paramCastController, FlashlightController paramFlashlightController, UserSwitcherController paramUserSwitcherController, UserInfoController paramUserInfoController, KeyguardMonitor paramKeyguardMonitor, SecurityController paramSecurityController, BatteryController paramBatteryController, StatusBarIconController paramStatusBarIconController, NextAlarmController paramNextAlarmController)
  {
    return new QSTileHost(paramContext, paramPhoneStatusBar, paramBluetoothController, paramLocationController, paramRotationLockController, paramNetworkController, paramZenModeController, paramHotspotController, paramCastController, paramFlashlightController, paramUserSwitcherController, paramUserInfoController, paramKeyguardMonitor, paramSecurityController, paramBatteryController, paramStatusBarIconController, paramNextAlarmController);
  }
  
  public ScrimController createScrimController(ScrimView paramScrimView1, ScrimView paramScrimView2, View paramView, LockscreenWallpaper paramLockscreenWallpaper)
  {
    return new ScrimController(paramScrimView1, paramScrimView2, paramView);
  }
  
  public StatusBarKeyguardViewManager createStatusBarKeyguardViewManager(Context paramContext, ViewMediatorCallback paramViewMediatorCallback, LockPatternUtils paramLockPatternUtils)
  {
    return new StatusBarKeyguardViewManager(paramContext, paramViewMediatorCallback, paramLockPatternUtils);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\SystemUIFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */