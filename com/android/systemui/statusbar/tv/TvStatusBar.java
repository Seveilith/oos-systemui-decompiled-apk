package com.android.systemui.statusbar.tv;

import android.content.ComponentName;
import android.graphics.Rect;
import android.os.IBinder;
import android.service.notification.NotificationListenerService.RankingMap;
import android.service.notification.StatusBarNotification;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.statusbar.ActivatableNotificationView;
import com.android.systemui.statusbar.BaseStatusBar;
import com.android.systemui.statusbar.NotificationData.Entry;
import com.android.systemui.tv.pip.PipManager;

public class TvStatusBar
  extends BaseStatusBar
{
  public void addNotification(StatusBarNotification paramStatusBarNotification, NotificationListenerService.RankingMap paramRankingMap, NotificationData.Entry paramEntry) {}
  
  public void addQsTile(ComponentName paramComponentName) {}
  
  public void animateCollapsePanels(int paramInt) {}
  
  public void animateExpandNotificationsPanel(int paramInt) {}
  
  public void animateExpandSettingsPanel(String paramString) {}
  
  public void appTransitionCancelled() {}
  
  public void appTransitionFinished() {}
  
  public void appTransitionPending() {}
  
  public void appTransitionStarting(long paramLong1, long paramLong2) {}
  
  public void buzzBeepBlinked() {}
  
  public void clickTile(ComponentName paramComponentName) {}
  
  protected void createAndAddWindows() {}
  
  public void disable(int paramInt1, int paramInt2, boolean paramBoolean) {}
  
  protected int getMaxKeyguardNotifications(boolean paramBoolean)
  {
    return 0;
  }
  
  public void handleSystemNavigationKey(int paramInt) {}
  
  public boolean isPanelFullyCollapsed()
  {
    return false;
  }
  
  protected boolean isSnoozedPackage(StatusBarNotification paramStatusBarNotification)
  {
    return false;
  }
  
  public void maybeEscalateHeadsUp() {}
  
  public void notificationLightOff() {}
  
  public void notificationLightPulse(int paramInt1, int paramInt2, int paramInt3) {}
  
  public void onActivated(ActivatableNotificationView paramActivatableNotificationView) {}
  
  public void onActivationReset(ActivatableNotificationView paramActivatableNotificationView) {}
  
  public void onCameraLaunchGestureDetected(int paramInt) {}
  
  protected void refreshLayout(int paramInt) {}
  
  public void remQsTile(ComponentName paramComponentName) {}
  
  public void removeIcon(String paramString) {}
  
  public void removeNotification(String paramString, NotificationListenerService.RankingMap paramRankingMap) {}
  
  protected void setAreThereNotifications() {}
  
  protected void setHeadsUpUser(int paramInt) {}
  
  public void setIcon(String paramString, StatusBarIcon paramStatusBarIcon) {}
  
  public void setImeWindowStatus(IBinder paramIBinder, int paramInt1, int paramInt2, boolean paramBoolean) {}
  
  public void setSystemUiVisibility(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rect paramRect1, Rect paramRect2) {}
  
  public void setWindowState(int paramInt1, int paramInt2) {}
  
  public void showScreenPinningRequest(int paramInt) {}
  
  public void showTvPictureInPictureMenu()
  {
    PipManager.getInstance().showTvPictureInPictureMenu();
  }
  
  public void start()
  {
    super.start();
    putComponent(TvStatusBar.class, this);
  }
  
  protected void toggleSplitScreenMode(int paramInt1, int paramInt2) {}
  
  public void topAppWindowChanged(boolean paramBoolean) {}
  
  protected void updateHeadsUp(String paramString, NotificationData.Entry paramEntry, boolean paramBoolean1, boolean paramBoolean2) {}
  
  protected void updateNotificationRanking(NotificationListenerService.RankingMap paramRankingMap) {}
  
  protected void updateNotifications() {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\tv\TvStatusBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */