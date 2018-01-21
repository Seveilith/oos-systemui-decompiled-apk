package com.android.keyguard;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import com.android.internal.widget.LockPatternUtils;
import java.io.File;
import java.util.List;

public class KeyguardHostView
  extends FrameLayout
  implements KeyguardSecurityContainer.SecurityCallback
{
  private AudioManager mAudioManager;
  private Runnable mCancelAction;
  private OnDismissAction mDismissAction;
  protected LockPatternUtils mLockPatternUtils;
  private KeyguardSecurityContainer mSecurityContainer;
  private TelephonyManager mTelephonyManager = null;
  private final KeyguardUpdateMonitorCallback mUpdateCallback = new KeyguardUpdateMonitorCallback()
  {
    public void onTrustGrantedWithFlags(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if (paramAnonymousInt2 != KeyguardUpdateMonitor.getCurrentUser()) {
        return;
      }
      if (!KeyguardHostView.this.isAttachedToWindow()) {
        return;
      }
      boolean bool = KeyguardHostView.-wrap0(KeyguardHostView.this);
      if ((paramAnonymousInt1 & 0x1) != 0)
      {
        paramAnonymousInt2 = 1;
        if ((paramAnonymousInt1 & 0x2) == 0) {
          break label101;
        }
      }
      label101:
      for (paramAnonymousInt1 = 1;; paramAnonymousInt1 = 0)
      {
        if ((paramAnonymousInt2 != 0) || (paramAnonymousInt1 != 0))
        {
          if ((!KeyguardHostView.this.mViewMediatorCallback.isScreenOn()) || ((!bool) && (paramAnonymousInt1 == 0))) {
            break label106;
          }
          if (!bool) {
            Log.i("KeyguardViewBase", "TrustAgent dismissed Keyguard.");
          }
          KeyguardHostView.this.dismiss(false);
        }
        return;
        paramAnonymousInt2 = 0;
        break;
      }
      label106:
      KeyguardHostView.this.mViewMediatorCallback.playTrustedSound();
    }
    
    public void onUserSwitchComplete(int paramAnonymousInt)
    {
      KeyguardHostView.this.getSecurityContainer().showPrimarySecurityScreen(false);
    }
  };
  protected ViewMediatorCallback mViewMediatorCallback;
  
  public KeyguardHostView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public KeyguardHostView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    KeyguardUpdateMonitor.getInstance(paramContext).registerCallback(this.mUpdateCallback);
  }
  
  private void handleMediaKeyEvent(KeyEvent paramKeyEvent)
  {
    try
    {
      if (this.mAudioManager == null) {
        this.mAudioManager = ((AudioManager)getContext().getSystemService("audio"));
      }
      this.mAudioManager.dispatchMediaKeyEvent(paramKeyEvent);
      return;
    }
    finally {}
  }
  
  public void cancelDismissAction()
  {
    setOnDismissAction(null, null);
  }
  
  public void cleanUp()
  {
    getSecurityContainer().onPause();
  }
  
  public boolean dismiss()
  {
    return dismiss(false);
  }
  
  public boolean dismiss(boolean paramBoolean)
  {
    return this.mSecurityContainer.showNextSecurityScreenOrFinish(paramBoolean);
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    super.dispatchDraw(paramCanvas);
    if (this.mViewMediatorCallback != null) {
      this.mViewMediatorCallback.keyguardDoneDrawing();
    }
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if (interceptMediaKey(paramKeyEvent)) {
      return true;
    }
    return super.dispatchKeyEvent(paramKeyEvent);
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    if (paramAccessibilityEvent.getEventType() == 32)
    {
      paramAccessibilityEvent.getText().add(this.mSecurityContainer.getCurrentSecurityModeContentDescription());
      return true;
    }
    return super.dispatchPopulateAccessibilityEvent(paramAccessibilityEvent);
  }
  
  public void dispatchSystemUiVisibilityChanged(int paramInt)
  {
    super.dispatchSystemUiVisibilityChanged(paramInt);
    if (!(this.mContext instanceof Activity)) {
      setSystemUiVisibility(4194304);
    }
  }
  
  public void finish(boolean paramBoolean)
  {
    boolean bool = false;
    if (this.mDismissAction != null)
    {
      bool = this.mDismissAction.onDismiss();
      this.mDismissAction = null;
      this.mCancelAction = null;
    }
    if (this.mViewMediatorCallback != null)
    {
      if (bool) {
        this.mViewMediatorCallback.keyguardDonePending(paramBoolean);
      }
    }
    else {
      return;
    }
    this.mViewMediatorCallback.keyguardDone(paramBoolean);
  }
  
  public KeyguardSecurityModel.SecurityMode getCurrentSecurityMode()
  {
    return this.mSecurityContainer.getCurrentSecurityMode();
  }
  
  protected KeyguardSecurityContainer getSecurityContainer()
  {
    return this.mSecurityContainer;
  }
  
  public KeyguardSecurityModel.SecurityMode getSecurityMode()
  {
    return this.mSecurityContainer.getSecurityMode();
  }
  
  public boolean interceptMediaKey(KeyEvent paramKeyEvent)
  {
    int i = paramKeyEvent.getKeyCode();
    if (paramKeyEvent.getAction() == 0) {
      switch (i)
      {
      }
    }
    while (paramKeyEvent.getAction() != 1)
    {
      return false;
      if (this.mTelephonyManager == null) {
        this.mTelephonyManager = ((TelephonyManager)getContext().getSystemService("phone"));
      }
      if ((this.mTelephonyManager != null) && (this.mTelephonyManager.getCallState() != 0)) {
        return true;
      }
      handleMediaKeyEvent(paramKeyEvent);
      return true;
      return false;
    }
    switch (i)
    {
    default: 
      return false;
    }
    handleMediaKeyEvent(paramKeyEvent);
    return true;
  }
  
  public boolean isCheckingPassword()
  {
    return this.mSecurityContainer.isCheckingPassword();
  }
  
  protected void onFinishInflate()
  {
    this.mSecurityContainer = ((KeyguardSecurityContainer)findViewById(R.id.keyguard_security_container));
    this.mLockPatternUtils = new LockPatternUtils(this.mContext);
    this.mSecurityContainer.setLockPatternUtils(this.mLockPatternUtils);
    this.mSecurityContainer.setSecurityCallback(this);
    this.mSecurityContainer.showPrimarySecurityScreen(false);
  }
  
  public void onPause()
  {
    this.mSecurityContainer.showPrimarySecurityScreen(true);
    this.mSecurityContainer.onPause();
    clearFocus();
  }
  
  public void onResume()
  {
    this.mSecurityContainer.onResume(1);
    requestFocus();
  }
  
  public void onSecurityModeChanged(KeyguardSecurityModel.SecurityMode paramSecurityMode, boolean paramBoolean)
  {
    if (this.mViewMediatorCallback != null) {
      this.mViewMediatorCallback.setNeedsInput(paramBoolean);
    }
  }
  
  public void reportMDMEvent(String paramString1, String paramString2, String paramString3)
  {
    this.mViewMediatorCallback.reportMDMEvent(paramString1, paramString2, paramString3);
  }
  
  public void reset()
  {
    this.mViewMediatorCallback.resetKeyguard();
  }
  
  public void setLockPatternUtils(LockPatternUtils paramLockPatternUtils)
  {
    this.mLockPatternUtils = paramLockPatternUtils;
    this.mSecurityContainer.setLockPatternUtils(paramLockPatternUtils);
  }
  
  public void setOnDismissAction(OnDismissAction paramOnDismissAction, Runnable paramRunnable)
  {
    if (this.mCancelAction != null)
    {
      this.mCancelAction.run();
      this.mCancelAction = null;
    }
    this.mDismissAction = paramOnDismissAction;
    this.mCancelAction = paramRunnable;
  }
  
  public void setViewMediatorCallback(ViewMediatorCallback paramViewMediatorCallback)
  {
    this.mViewMediatorCallback = paramViewMediatorCallback;
    this.mViewMediatorCallback.setNeedsInput(this.mSecurityContainer.needsInput());
  }
  
  public boolean shouldEnableMenuKey()
  {
    boolean bool1 = getResources().getBoolean(R.bool.config_disableMenuKeyInLockScreen);
    boolean bool2 = ActivityManager.isRunningInTestHarness();
    boolean bool3 = new File("/data/local/enable_menu_key").exists();
    if ((bool1) && (!bool2)) {
      return bool3;
    }
    return true;
  }
  
  public void showMessage(String paramString, int paramInt)
  {
    this.mSecurityContainer.showMessage(paramString, paramInt);
  }
  
  public void showPrimarySecurityScreen()
  {
    this.mSecurityContainer.showPrimarySecurityScreen(false);
  }
  
  public void showPromptReason(int paramInt)
  {
    this.mSecurityContainer.showPromptReason(paramInt);
  }
  
  public void startAppearAnimation()
  {
    this.mSecurityContainer.startAppearAnimation();
  }
  
  public void startDisappearAnimation(Runnable paramRunnable)
  {
    if ((!this.mSecurityContainer.startDisappearAnimation(paramRunnable)) && (paramRunnable != null)) {
      paramRunnable.run();
    }
  }
  
  public void tryToStartFaceLockFromBouncer()
  {
    this.mViewMediatorCallback.tryToStartFaceLockFromBouncer();
  }
  
  public void userActivity()
  {
    if (this.mViewMediatorCallback != null) {
      this.mViewMediatorCallback.userActivity();
    }
  }
  
  public static abstract interface OnDismissAction
  {
    public abstract boolean onDismiss();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\KeyguardHostView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */