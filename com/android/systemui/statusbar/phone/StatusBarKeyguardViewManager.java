package com.android.systemui.statusbar.phone;

import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.Trace;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import android.view.WindowManagerGlobal;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardHostView.OnDismissAction;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.statusbar.RemoteInputController.Callback;
import com.android.systemui.util.Utils;

public class StatusBarKeyguardViewManager
  implements RemoteInputController.Callback
{
  private static String TAG = "StatusBarKeyguardViewManager";
  private KeyguardHostView.OnDismissAction mAfterKeyguardGoneAction;
  protected KeyguardBouncer mBouncer;
  private ViewGroup mContainer;
  protected final Context mContext;
  private boolean mDeferScrimFadeOut;
  private boolean mDeviceInteractive = false;
  private boolean mDeviceWillWakeUp;
  private FingerprintUnlockController mFingerprintUnlockController;
  protected boolean mFirstUpdate = true;
  private INotificationManager mINotificationManager;
  private boolean mLastBouncerDismissible;
  private boolean mLastBouncerShowing;
  private boolean mLastLightViewEnabled;
  protected boolean mLastOccluded;
  protected boolean mLastRemoteInputActive;
  protected boolean mLastShowing;
  protected LockPatternUtils mLockPatternUtils;
  private Runnable mMakeNavigationBarVisibleRunnable = new Runnable()
  {
    public void run()
    {
      if (StatusBarKeyguardViewManager.this.mPhoneStatusBar.getNavigationBarView() == null)
      {
        Log.w(StatusBarKeyguardViewManager.-get0(), "Navigationbar is null");
        return;
      }
      StatusBarKeyguardViewManager.this.mPhoneStatusBar.getNavigationBarView().setVisibility(0);
    }
  };
  protected boolean mOccluded;
  protected PhoneStatusBar mPhoneStatusBar;
  protected boolean mRemoteInputActive;
  private boolean mScreenTurnedOn;
  private ScrimController mScrimController;
  protected boolean mShowing;
  private StatusBarWindowManager mStatusBarWindowManager;
  protected ViewMediatorCallback mViewMediatorCallback;
  
  public StatusBarKeyguardViewManager(Context paramContext, ViewMediatorCallback paramViewMediatorCallback, LockPatternUtils paramLockPatternUtils)
  {
    this.mContext = paramContext;
    this.mViewMediatorCallback = paramViewMediatorCallback;
    this.mLockPatternUtils = paramLockPatternUtils;
    this.mINotificationManager = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
  }
  
  private void animateScrimControllerKeyguardFadingOut(long paramLong1, long paramLong2, final Runnable paramRunnable, boolean paramBoolean)
  {
    Trace.asyncTraceBegin(8L, "Fading out", 0);
    this.mScrimController.animateKeyguardFadingOut(paramLong1, paramLong2, new Runnable()
    {
      public void run()
      {
        if (paramRunnable != null) {
          paramRunnable.run();
        }
        StatusBarKeyguardViewManager.-get3(StatusBarKeyguardViewManager.this).setKeyguardFadingAway(false);
        StatusBarKeyguardViewManager.this.mPhoneStatusBar.finishKeyguardFadingAway();
        StatusBarKeyguardViewManager.-get1(StatusBarKeyguardViewManager.this).finishKeyguardFadingAway();
        WindowManagerGlobal.getInstance().trimMemory(20);
        Trace.asyncTraceEnd(8L, "Fading out", 0);
      }
    }, paramBoolean);
  }
  
  private void animateScrimControllerKeyguardFadingOut(long paramLong1, long paramLong2, boolean paramBoolean)
  {
    animateScrimControllerKeyguardFadingOut(paramLong1, paramLong2, null, paramBoolean);
  }
  
  private void executeAfterKeyguardGoneAction()
  {
    if (this.mAfterKeyguardGoneAction != null)
    {
      this.mAfterKeyguardGoneAction.onDismiss();
      this.mAfterKeyguardGoneAction = null;
    }
  }
  
  private long getNavBarShowDelay()
  {
    if (KeyguardUpdateMonitor.getInstance(this.mContext).isFacelockUnlocking()) {
      return 0L;
    }
    if (this.mPhoneStatusBar.isKeyguardFadingAway()) {
      return this.mPhoneStatusBar.getKeyguardFadingAwayDelay();
    }
    return 320L;
  }
  
  private void showBouncer()
  {
    if (this.mShowing) {
      this.mBouncer.show(false);
    }
    updateStates();
  }
  
  public void animateCollapsePanels(float paramFloat)
  {
    this.mPhoneStatusBar.animateCollapsePanels(0, true, false, paramFloat);
  }
  
  public boolean canShowNavBar()
  {
    if ((!this.mLastShowing) || (this.mLastOccluded)) {}
    while (this.mLastBouncerShowing) {
      return true;
    }
    return false;
  }
  
  public void dismiss()
  {
    if ((this.mDeviceInteractive) || (this.mDeviceWillWakeUp)) {
      showBouncer();
    }
  }
  
  public void dismissWithAction(KeyguardHostView.OnDismissAction paramOnDismissAction, Runnable paramRunnable, boolean paramBoolean)
  {
    if (this.mShowing)
    {
      if (paramBoolean) {
        break label25;
      }
      this.mBouncer.showWithDismissAction(paramOnDismissAction, paramRunnable);
    }
    for (;;)
    {
      updateStates();
      return;
      label25:
      this.mBouncer.show(false);
      this.mAfterKeyguardGoneAction = paramOnDismissAction;
    }
  }
  
  public void forceDismiss()
  {
    showBouncer();
  }
  
  public void forceHideBouncer()
  {
    if (this.mBouncer.isShowing()) {
      this.mBouncer.forceHide();
    }
  }
  
  protected boolean getLastNavBarVisible()
  {
    if ((this.mLastShowing) && (!this.mLastOccluded) && ((!this.mLastBouncerShowing) || (this.mLastLightViewEnabled))) {
      return this.mLastRemoteInputActive;
    }
    return true;
  }
  
  public int getSecurityPromptStringId()
  {
    if (this.mBouncer != null) {
      return this.mBouncer.getSecurityPromptStringId();
    }
    return 0;
  }
  
  public ViewRootImpl getViewRootImpl()
  {
    return this.mPhoneStatusBar.getStatusBarView().getViewRootImpl();
  }
  
  public void hide(long paramLong1, long paramLong2)
  {
    this.mShowing = false;
    if (KeyguardUpdateMonitor.getInstance(this.mContext).needsSlowUnlockTransition()) {
      paramLong2 = 2000L;
    }
    long l = Math.max(0L, -48L + paramLong1 - SystemClock.uptimeMillis());
    if (this.mPhoneStatusBar.isInLaunchTransition())
    {
      this.mPhoneStatusBar.fadeKeyguardAfterLaunchTransition(new Runnable()new Runnable
      {
        public void run()
        {
          StatusBarKeyguardViewManager.-get3(StatusBarKeyguardViewManager.this).setKeyguardShowing(false);
          StatusBarKeyguardViewManager.-get3(StatusBarKeyguardViewManager.this).setKeyguardFadingAway(true);
          StatusBarKeyguardViewManager.this.mBouncer.hide(true);
          StatusBarKeyguardViewManager.this.updateStates();
          StatusBarKeyguardViewManager.-get2(StatusBarKeyguardViewManager.this).animateKeyguardFadingOut(100L, 300L, null, false);
        }
      }, new Runnable()
      {
        public void run()
        {
          StatusBarKeyguardViewManager.this.mPhoneStatusBar.hideKeyguard();
          StatusBarKeyguardViewManager.-get3(StatusBarKeyguardViewManager.this).setKeyguardFadingAway(false);
          StatusBarKeyguardViewManager.this.mViewMediatorCallback.keyguardGone();
          StatusBarKeyguardViewManager.-wrap0(StatusBarKeyguardViewManager.this);
        }
      });
      return;
    }
    if (this.mFingerprintUnlockController.getMode() == 2)
    {
      this.mFingerprintUnlockController.startKeyguardFadingAway();
      this.mPhoneStatusBar.setKeyguardFadingAway(paramLong1, 0L, 240L);
      this.mStatusBarWindowManager.setKeyguardFadingAway(true);
      this.mPhoneStatusBar.fadeKeyguardWhilePulsing();
      animateScrimControllerKeyguardFadingOut(0L, 240L, new Runnable()
      {
        public void run()
        {
          StatusBarKeyguardViewManager.this.mPhoneStatusBar.hideKeyguard();
        }
      }, false);
    }
    for (;;)
    {
      this.mStatusBarWindowManager.setKeyguardShowing(false);
      this.mBouncer.hide(true);
      this.mViewMediatorCallback.keyguardGone();
      executeAfterKeyguardGoneAction();
      updateStates();
      return;
      this.mFingerprintUnlockController.startKeyguardFadingAway();
      this.mPhoneStatusBar.setKeyguardFadingAway(paramLong1, l, paramLong2);
      if (!this.mPhoneStatusBar.hideKeyguard())
      {
        this.mStatusBarWindowManager.setKeyguardFadingAway(true);
        if (this.mFingerprintUnlockController.getMode() == 1)
        {
          if (!this.mScreenTurnedOn) {
            this.mDeferScrimFadeOut = true;
          } else {
            animateScrimControllerKeyguardFadingOut(0L, 200L, true);
          }
        }
        else {
          animateScrimControllerKeyguardFadingOut(l, paramLong2, false);
        }
      }
      else
      {
        this.mScrimController.animateGoingToFullShade(l, paramLong2);
        this.mPhoneStatusBar.finishKeyguardFadingAway();
      }
    }
  }
  
  public boolean interceptMediaKey(KeyEvent paramKeyEvent)
  {
    return this.mBouncer.interceptMediaKey(paramKeyEvent);
  }
  
  public boolean isBouncerShowing()
  {
    return this.mBouncer.isShowing();
  }
  
  public boolean isCheckingPassword()
  {
    if (this.mBouncer == null) {
      return false;
    }
    return this.mBouncer.isCheckingPassword();
  }
  
  public boolean isGoingToNotificationShade()
  {
    return this.mPhoneStatusBar.isGoingToNotificationShade();
  }
  
  protected boolean isNavBarVisible()
  {
    if ((this.mShowing) && (!this.mOccluded) && ((!this.mBouncer.isShowing()) || (this.mPhoneStatusBar.getFacelockController().isLighModeEnabled()))) {
      return this.mRemoteInputActive;
    }
    return true;
  }
  
  public boolean isOccluded()
  {
    return this.mOccluded;
  }
  
  public boolean isScreenTurnedOn()
  {
    return this.mScreenTurnedOn;
  }
  
  public boolean isSecure()
  {
    return this.mBouncer.isSecure();
  }
  
  public boolean isSecure(int paramInt)
  {
    if (!this.mBouncer.isSecure()) {
      return this.mLockPatternUtils.isSecure(paramInt);
    }
    return true;
  }
  
  public boolean isShowing()
  {
    return this.mShowing;
  }
  
  public boolean isUnlockWithWallpaper()
  {
    return this.mStatusBarWindowManager.isShowingWallpaper();
  }
  
  public void keyguardGoingAway()
  {
    this.mPhoneStatusBar.keyguardGoingAway();
  }
  
  public void notifyDeviceWakeUpRequested()
  {
    if (this.mDeviceInteractive) {}
    for (boolean bool = false;; bool = true)
    {
      this.mDeviceWillWakeUp = bool;
      return;
    }
  }
  
  public void notifyKeyguardAuthenticated(boolean paramBoolean)
  {
    this.mBouncer.notifyKeyguardAuthenticated(paramBoolean);
  }
  
  public void onActivityDrawn()
  {
    if (this.mPhoneStatusBar.isCollapsing())
    {
      this.mPhoneStatusBar.addPostCollapseAction(new Runnable()
      {
        public void run()
        {
          StatusBarKeyguardViewManager.this.mViewMediatorCallback.readyForKeyguardDone();
        }
      });
      return;
    }
    this.mViewMediatorCallback.readyForKeyguardDone();
  }
  
  public boolean onBackPressed()
  {
    if (this.mBouncer.isShowing())
    {
      if (this.mBouncer.isCheckingPassword())
      {
        Log.d(TAG, "onBackPressed when checking pass");
        return true;
      }
      this.mPhoneStatusBar.endAffordanceLaunch();
      if (!this.mBouncer.isUserUnlocked())
      {
        Log.d(TAG, "onBack when not unlocked");
        this.mPhoneStatusBar.showKeyguard();
        this.mBouncer.hide(false);
        this.mBouncer.prepare();
        KeyguardUpdateMonitor.getInstance(this.mContext).sendKeyguardReset();
        updateStates();
        return true;
      }
      reset();
      return true;
    }
    return false;
  }
  
  public void onDensityOrFontScaleChanged()
  {
    this.mBouncer.hide(true);
  }
  
  public void onFinishedGoingToSleep()
  {
    this.mDeviceInteractive = false;
    this.mPhoneStatusBar.onFinishedGoingToSleep();
    this.mBouncer.onScreenTurnedOff();
  }
  
  public void onRemoteInputActive(boolean paramBoolean)
  {
    this.mRemoteInputActive = paramBoolean;
    updateStates();
  }
  
  public void onScreenTurnedOff()
  {
    this.mScreenTurnedOn = false;
    this.mPhoneStatusBar.onScreenTurnedOff();
  }
  
  public void onScreenTurnedOn()
  {
    Trace.beginSection("StatusBarKeyguardViewManager#onScreenTurnedOn");
    this.mScreenTurnedOn = true;
    if (this.mDeferScrimFadeOut)
    {
      this.mDeferScrimFadeOut = false;
      animateScrimControllerKeyguardFadingOut(0L, 200L, true);
      updateStates();
    }
    this.mPhoneStatusBar.onScreenTurnedOn();
    Trace.endSection();
  }
  
  public void onScreenTurningOn()
  {
    Trace.beginSection("StatusBarKeyguardViewManager#onScreenTurningOn");
    this.mPhoneStatusBar.onScreenTurningOn();
    Trace.endSection();
  }
  
  public void onStartedGoingToSleep()
  {
    this.mPhoneStatusBar.onStartedGoingToSleep();
  }
  
  public void onStartedWakingUp()
  {
    Trace.beginSection("StatusBarKeyguardViewManager#onStartedWakingUp");
    this.mDeviceInteractive = true;
    this.mDeviceWillWakeUp = false;
    this.mPhoneStatusBar.onStartedWakingUp();
    Trace.endSection();
  }
  
  public void registerStatusBar(PhoneStatusBar paramPhoneStatusBar, ViewGroup paramViewGroup, StatusBarWindowManager paramStatusBarWindowManager, ScrimController paramScrimController, FingerprintUnlockController paramFingerprintUnlockController)
  {
    this.mPhoneStatusBar = paramPhoneStatusBar;
    this.mContainer = paramViewGroup;
    this.mStatusBarWindowManager = paramStatusBarWindowManager;
    this.mScrimController = paramScrimController;
    this.mFingerprintUnlockController = paramFingerprintUnlockController;
    this.mBouncer = SystemUIFactory.getInstance().createKeyguardBouncer(this.mContext, this.mViewMediatorCallback, this.mLockPatternUtils, this.mStatusBarWindowManager, paramViewGroup);
  }
  
  public void reset()
  {
    if (this.mShowing)
    {
      if (!this.mOccluded) {
        break label52;
      }
      this.mPhoneStatusBar.hideKeyguard();
      this.mPhoneStatusBar.stopWaitingForKeyguardExit();
      this.mBouncer.hide(false);
    }
    for (;;)
    {
      KeyguardUpdateMonitor.getInstance(this.mContext).sendKeyguardReset();
      updateStates();
      return;
      label52:
      showBouncerOrKeyguard();
    }
  }
  
  public void setNeedsInput(boolean paramBoolean)
  {
    this.mStatusBarWindowManager.setKeyguardNeedsInput(paramBoolean);
  }
  
  public void setOccluded(boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = true;
    if ((!paramBoolean1) || (this.mOccluded)) {}
    do
    {
      do
      {
        this.mOccluded = paramBoolean1;
        if (this.mShowing)
        {
          PhoneStatusBar localPhoneStatusBar = this.mPhoneStatusBar;
          if ((!paramBoolean2) || (paramBoolean1)) {
            bool = false;
          }
          localPhoneStatusBar.updateMediaMetaData(false, bool);
        }
        this.mStatusBarWindowManager.setKeyguardOccluded(paramBoolean1);
        reset();
        if ((paramBoolean2) && (!paramBoolean1)) {
          break;
        }
        return;
      } while ((!this.mShowing) || (!this.mPhoneStatusBar.isInLaunchTransition()));
      this.mOccluded = true;
      this.mPhoneStatusBar.fadeKeyguardAfterLaunchTransition(null, new Runnable()
      {
        public void run()
        {
          if (Utils.DEBUG_ONEPLUS) {
            Log.d(StatusBarKeyguardViewManager.-get0(), "transition done");
          }
          StatusBarKeyguardViewManager.-get3(StatusBarKeyguardViewManager.this).setKeyguardOccluded(StatusBarKeyguardViewManager.this.mOccluded);
          StatusBarKeyguardViewManager.this.reset();
        }
      });
      if (Utils.DEBUG_ONEPLUS) {
        Log.d(TAG, "fade after launch transition");
      }
      return;
    } while (!this.mShowing);
    this.mPhoneStatusBar.animateKeyguardUnoccluding();
  }
  
  public boolean shouldDisableWindowAnimationsForUnlock()
  {
    return this.mPhoneStatusBar.isInLaunchTransition();
  }
  
  public boolean shouldDismissOnMenuPressed()
  {
    return this.mBouncer.shouldDismissOnMenuPressed();
  }
  
  public void show(Bundle paramBundle)
  {
    this.mShowing = true;
    this.mStatusBarWindowManager.setKeyguardShowing(true);
    this.mScrimController.abortKeyguardFadingOut();
    reset();
    if (isSecure()) {}
    try
    {
      this.mINotificationManager.setAppLock();
      return;
    }
    catch (RemoteException paramBundle)
    {
      Log.w(TAG, "Talk to NotificationManagerService fail");
    }
  }
  
  public void showBouncerMessage(String paramString, int paramInt)
  {
    this.mBouncer.showMessage(paramString, paramInt);
  }
  
  protected void showBouncerOrKeyguard()
  {
    if (this.mBouncer.needsFullscreenBouncer())
    {
      this.mPhoneStatusBar.hideKeyguard();
      this.mBouncer.show(true);
      return;
    }
    this.mPhoneStatusBar.showKeyguard();
    this.mBouncer.hide(false);
    this.mBouncer.prepare();
  }
  
  public void startPreHideAnimation(Runnable paramRunnable)
  {
    if (this.mBouncer.isShowing()) {
      this.mBouncer.startPreHideAnimation(paramRunnable);
    }
    while (paramRunnable == null) {
      return;
    }
    paramRunnable.run();
  }
  
  protected void updateStates()
  {
    int i = this.mContainer.getSystemUiVisibility();
    boolean bool5 = this.mShowing;
    boolean bool6 = this.mOccluded;
    boolean bool7 = this.mBouncer.isShowing();
    boolean bool1;
    boolean bool4;
    Object localObject;
    label187:
    boolean bool3;
    label207:
    label247:
    long l;
    label306:
    label370:
    int j;
    if (this.mBouncer.isFullscreenBouncer())
    {
      bool1 = false;
      bool4 = this.mRemoteInputActive;
      localObject = new StringBuilder("updateStates, vis:").append(i).append(",Showing:").append(this.mShowing).append(",LastShowing:").append(this.mLastShowing).append(",Occluded:").append(this.mOccluded).append(",LastOccluded:").append(this.mLastOccluded).append(",BouncerShow:").append(bool7).append(",LastBouncerShow:").append(this.mLastBouncerShowing).append(",BouncerDismiss:").append(bool1).append(",LastBouncerDismiss:").append(this.mLastBouncerDismissible);
      Log.d(TAG, ((StringBuilder)localObject).toString());
      if ((bool1) || (!bool5)) {
        break label547;
      }
      bool2 = bool4;
      if ((this.mLastBouncerDismissible) || (!this.mLastShowing)) {
        break label553;
      }
      bool3 = this.mLastRemoteInputActive;
      if ((bool2 != bool3) || (this.mFirstUpdate))
      {
        if ((!bool1) && (bool5) && (!bool4)) {
          break label559;
        }
        this.mContainer.setSystemUiVisibility(0xFFBFFFFF & i);
      }
      bool2 = isNavBarVisible();
      if (((bool2 != getLastNavBarVisible()) || (this.mFirstUpdate)) && (this.mPhoneStatusBar.getNavigationBarView() != null))
      {
        if (!bool2) {
          break label590;
        }
        l = getNavBarShowDelay();
        if (l != 0L) {
          break label574;
        }
        this.mMakeNavigationBarVisibleRunnable.run();
      }
      if ((bool7 != this.mLastBouncerShowing) || (this.mFirstUpdate))
      {
        this.mStatusBarWindowManager.setBouncerShowing(bool7);
        this.mPhoneStatusBar.setBouncerShowing(bool7);
        this.mScrimController.setBouncerShowing(bool7);
      }
      localObject = KeyguardUpdateMonitor.getInstance(this.mContext);
      if ((bool5) && (!bool6)) {
        break label617;
      }
      i = 0;
      if ((this.mLastShowing) && (!this.mLastOccluded)) {
        break label622;
      }
      j = 0;
      label386:
      if ((i != j) || (this.mFirstUpdate))
      {
        if ((Utils.isSupportHideNavBar()) && (this.mPhoneStatusBar.getNavigationBarView() != null))
        {
          NavigationBarView localNavigationBarView = this.mPhoneStatusBar.getNavigationBarView();
          if ((bool5) && (!bool6)) {
            break label627;
          }
          bool2 = false;
          label436:
          localNavigationBarView.onShowKeyguard(bool2);
        }
        if ((bool5) && (!bool6)) {
          break label633;
        }
      }
    }
    label547:
    label553:
    label559:
    label574:
    label590:
    label617:
    label622:
    label627:
    label633:
    for (boolean bool2 = false;; bool2 = true)
    {
      ((KeyguardUpdateMonitor)localObject).onKeyguardVisibilityChanged(bool2);
      if ((bool7 != this.mLastBouncerShowing) || (this.mFirstUpdate)) {
        ((KeyguardUpdateMonitor)localObject).sendKeyguardBouncerChanged(bool7);
      }
      this.mFirstUpdate = false;
      this.mLastShowing = bool5;
      this.mLastOccluded = bool6;
      this.mLastBouncerShowing = bool7;
      this.mLastBouncerDismissible = bool1;
      this.mLastRemoteInputActive = bool4;
      this.mLastLightViewEnabled = this.mPhoneStatusBar.getFacelockController().isLighModeEnabled();
      this.mPhoneStatusBar.onKeyguardViewManagerStatesUpdated();
      return;
      bool1 = true;
      break;
      bool2 = true;
      break label187;
      bool3 = true;
      break label207;
      this.mContainer.setSystemUiVisibility(0x400000 | i);
      break label247;
      this.mContainer.postOnAnimationDelayed(this.mMakeNavigationBarVisibleRunnable, l);
      break label306;
      this.mContainer.removeCallbacks(this.mMakeNavigationBarVisibleRunnable);
      this.mPhoneStatusBar.getNavigationBarView().setVisibility(8);
      break label306;
      i = 1;
      break label370;
      j = 1;
      break label386;
      bool2 = true;
      break label436;
    }
  }
  
  public void verifyUnlock()
  {
    dismiss();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\StatusBarKeyguardViewManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */