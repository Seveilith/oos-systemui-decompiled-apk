package com.android.systemui.statusbar.car;

import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import com.android.systemui.BatteryMeterView;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.misc.SystemServicesProxy.TaskStackListener;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import com.android.systemui.statusbar.phone.PhoneStatusBarView;
import com.android.systemui.statusbar.phone.StatusBarWindowView;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.UserSwitcherController;

public class CarStatusBar
  extends PhoneStatusBar
  implements CarBatteryController.BatteryViewHandler
{
  private BatteryMeterView mBatteryMeterView;
  private CarBatteryController mCarBatteryController;
  private CarNavigationBarView mCarNavigationBar;
  private CarNavigationBarController mController;
  private FullscreenUserSwitcher mFullscreenUserSwitcher;
  private BroadcastReceiver mPackageChangeReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ((paramAnonymousIntent.getData() == null) || (CarStatusBar.-get0(CarStatusBar.this) == null)) {
        return;
      }
      paramAnonymousContext = paramAnonymousIntent.getData().getSchemeSpecificPart();
      CarStatusBar.-get0(CarStatusBar.this).onPackageChange(paramAnonymousContext);
    }
  };
  private TaskStackListenerImpl mTaskStackListener;
  
  private void registerPackageChangeReceivers()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
    localIntentFilter.addDataScheme("package");
    this.mContext.registerReceiver(this.mPackageChangeReceiver, localIntentFilter);
  }
  
  protected void addNavigationBar()
  {
    WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(-1, -1, 2019, 25428072, -3);
    localLayoutParams.setTitle("CarNavigationBar");
    localLayoutParams.windowAnimations = 0;
    this.mWindowManager.addView(this.mNavigationBarView, localLayoutParams);
  }
  
  protected BatteryController createBatteryController()
  {
    this.mCarBatteryController = new CarBatteryController(this.mContext);
    this.mCarBatteryController.addBatteryViewHandler(this);
    return this.mCarBatteryController;
  }
  
  protected void createNavigationBarView(Context paramContext)
  {
    if (this.mNavigationBarView != null) {
      return;
    }
    this.mCarNavigationBar = ((CarNavigationBarView)View.inflate(paramContext, 2130968609, null));
    this.mController = new CarNavigationBarController(paramContext, this.mCarNavigationBar, this);
    this.mNavigationBarView = this.mCarNavigationBar;
  }
  
  protected void createUserSwitcher()
  {
    if (this.mUserSwitcherController.useFullscreenUserSwitcher())
    {
      this.mFullscreenUserSwitcher = new FullscreenUserSwitcher(this, this.mUserSwitcherController, (ViewStub)this.mStatusBarWindow.findViewById(2131952304));
      return;
    }
    super.createUserSwitcher();
  }
  
  public void destroy()
  {
    this.mCarBatteryController.stopListening();
    super.destroy();
  }
  
  public void hideBatteryView()
  {
    if (Log.isLoggable("CarStatusBar", 3)) {
      Log.d("CarStatusBar", "hideBatteryView(). mBatteryMeterView: " + this.mBatteryMeterView);
    }
    if (this.mBatteryMeterView != null) {
      this.mBatteryMeterView.setVisibility(8);
    }
  }
  
  protected PhoneStatusBarView makeStatusBarView()
  {
    PhoneStatusBarView localPhoneStatusBarView = super.makeStatusBarView();
    this.mBatteryMeterView = ((BatteryMeterView)localPhoneStatusBarView.findViewById(2131952313));
    this.mBatteryMeterView.setVisibility(8);
    if (Log.isLoggable("CarStatusBar", 3)) {
      Log.d("CarStatusBar", "makeStatusBarView(). mBatteryMeterView: " + this.mBatteryMeterView);
    }
    return localPhoneStatusBarView;
  }
  
  protected void repositionNavigationBar() {}
  
  public void showBatteryView()
  {
    if (Log.isLoggable("CarStatusBar", 3)) {
      Log.d("CarStatusBar", "showBatteryView(). mBatteryMeterView: " + this.mBatteryMeterView);
    }
    if (this.mBatteryMeterView != null) {
      this.mBatteryMeterView.setVisibility(0);
    }
  }
  
  public void start()
  {
    super.start();
    this.mTaskStackListener = new TaskStackListenerImpl(null);
    SystemServicesProxy.getInstance(this.mContext).registerTaskStackListener(this.mTaskStackListener);
    registerPackageChangeReceivers();
    this.mCarBatteryController.startListening();
  }
  
  public void updateKeyguardState(boolean paramBoolean1, boolean paramBoolean2)
  {
    super.updateKeyguardState(paramBoolean1, paramBoolean2);
    if (this.mFullscreenUserSwitcher != null)
    {
      if (this.mState == 3) {
        this.mFullscreenUserSwitcher.show();
      }
    }
    else {
      return;
    }
    this.mFullscreenUserSwitcher.hide();
  }
  
  public void userSwitched(int paramInt)
  {
    super.userSwitched(paramInt);
    if (this.mFullscreenUserSwitcher != null) {
      this.mFullscreenUserSwitcher.onUserSwitched(paramInt);
    }
  }
  
  private class TaskStackListenerImpl
    extends SystemServicesProxy.TaskStackListener
  {
    private TaskStackListenerImpl() {}
    
    public void onTaskStackChanged()
    {
      ActivityManager.RunningTaskInfo localRunningTaskInfo = Recents.getSystemServices().getRunningTask();
      CarStatusBar.-get0(CarStatusBar.this).taskChanged(localRunningTaskInfo.baseActivity.getPackageName());
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\car\CarStatusBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */