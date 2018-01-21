package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.os.UserManager;
import android.util.Slog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardHostView;
import com.android.keyguard.KeyguardHostView.OnDismissAction;
import com.android.keyguard.KeyguardSecurityModel.SecurityMode;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.DejankUtils;
import com.android.systemui.classifier.FalsingManager;
import com.android.systemui.util.Utils;

public class KeyguardBouncer
{
  private int mBouncerPromptReason;
  protected ViewMediatorCallback mCallback;
  protected ViewGroup mContainer;
  protected Context mContext;
  private FalsingManager mFalsingManager;
  protected KeyguardHostView mKeyguardView;
  protected LockPatternUtils mLockPatternUtils;
  protected ViewGroup mRoot;
  private final Runnable mShowRunnable = new Runnable()
  {
    public void run()
    {
      KeyguardBouncer.this.mRoot.setVisibility(0);
      KeyguardBouncer.this.mKeyguardView.onResume();
      KeyguardBouncer.this.showPromptReason(KeyguardBouncer.-get0(KeyguardBouncer.this));
      if (KeyguardBouncer.this.mKeyguardView.getHeight() != 0) {
        KeyguardBouncer.this.mKeyguardView.startAppearAnimation();
      }
      for (;;)
      {
        KeyguardBouncer.-set1(KeyguardBouncer.this, false);
        KeyguardBouncer.this.mKeyguardView.sendAccessibilityEvent(32);
        return;
        KeyguardBouncer.this.mKeyguardView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
          public boolean onPreDraw()
          {
            KeyguardBouncer.this.mKeyguardView.getViewTreeObserver().removeOnPreDrawListener(this);
            KeyguardBouncer.this.mKeyguardView.startAppearAnimation();
            return true;
          }
        });
        KeyguardBouncer.this.mKeyguardView.requestLayout();
      }
    }
  };
  private boolean mShowingSoon;
  private KeyguardUpdateMonitorCallback mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback()
  {
    public void onFacelockStateChanged(int paramAnonymousInt)
    {
      int i = -1;
      if ((paramAnonymousInt == 3) || (paramAnonymousInt == 2))
      {
        KeyguardBouncer.this.showMessage(" ", -1);
        return;
      }
      int j = KeyguardUpdateMonitor.getInstance(KeyguardBouncer.this.mContext).getFacelockNotifyMsgId(paramAnonymousInt);
      if (j == 0) {
        return;
      }
      if ((paramAnonymousInt == 1) || (paramAnonymousInt == 7)) {}
      for (;;)
      {
        i = -65536;
        do
        {
          KeyguardBouncer.this.showMessage(KeyguardBouncer.this.mContext.getString(j), i);
          return;
          if ((paramAnonymousInt == 6) || (paramAnonymousInt == 8)) {
            break;
          }
        } while (paramAnonymousInt != 9);
      }
    }
    
    public void onScreenTurnedOn()
    {
      if ((!KeyguardBouncer.this.isUserUnlocked()) && (KeyguardBouncer.this.mKeyguardView != null) && (KeyguardBouncer.this.mRoot != null) && (KeyguardBouncer.this.mRoot.getVisibility() == 0)) {
        KeyguardBouncer.this.mKeyguardView.onResume();
      }
    }
    
    public void onStrongAuthStateChanged(int paramAnonymousInt)
    {
      KeyguardBouncer.-set0(KeyguardBouncer.this, KeyguardBouncer.this.mCallback.getBouncerPromptReason());
    }
  };
  private StatusBarWindowManager mWindowManager;
  
  public KeyguardBouncer(Context paramContext, ViewMediatorCallback paramViewMediatorCallback, LockPatternUtils paramLockPatternUtils, StatusBarWindowManager paramStatusBarWindowManager, ViewGroup paramViewGroup)
  {
    this.mContext = paramContext;
    this.mCallback = paramViewMediatorCallback;
    this.mLockPatternUtils = paramLockPatternUtils;
    this.mContainer = paramViewGroup;
    this.mWindowManager = paramStatusBarWindowManager;
    KeyguardUpdateMonitor.getInstance(this.mContext).registerCallback(this.mUpdateMonitorCallback);
    this.mFalsingManager = FalsingManager.getInstance(this.mContext);
  }
  
  private void cancelShowRunnable()
  {
    DejankUtils.removeCallbacks(this.mShowRunnable);
    this.mShowingSoon = false;
  }
  
  private int translatePromptReasonToStringId(int paramInt, KeyguardSecurityModel.SecurityMode paramSecurityMode)
  {
    if ((paramSecurityMode == KeyguardSecurityModel.SecurityMode.None) || (paramSecurityMode == KeyguardSecurityModel.SecurityMode.SimPin)) {}
    while (paramSecurityMode == KeyguardSecurityModel.SecurityMode.SimPuk) {
      return 0;
    }
    int j = 0;
    int i;
    if (paramSecurityMode == KeyguardSecurityModel.SecurityMode.Pattern) {
      switch (paramInt)
      {
      default: 
        i = j;
      }
    }
    for (;;)
    {
      if (i != 0) {
        Slog.d("KeyguardBouncer", "show first unlock notify, reason:" + paramInt + ", mode:" + paramSecurityMode);
      }
      return i;
      i = 2131690757;
      continue;
      i = 2131690760;
      continue;
      i = 2131690766;
      continue;
      i = 2131690767;
      continue;
      if (paramSecurityMode == KeyguardSecurityModel.SecurityMode.PIN)
      {
        switch (paramInt)
        {
        default: 
          i = j;
          break;
        case 1: 
          i = 2131690758;
          break;
        case 2: 
          i = 2131690761;
          break;
        case 3: 
          i = 2131690766;
          break;
        case 4: 
          i = 2131690767;
          break;
        }
      }
      else
      {
        i = j;
        if (paramSecurityMode == KeyguardSecurityModel.SecurityMode.Password) {
          switch (paramInt)
          {
          default: 
            i = j;
            break;
          case 1: 
            i = 2131690759;
            break;
          case 2: 
            i = 2131690762;
            break;
          case 3: 
            i = 2131690766;
            break;
          case 4: 
            i = 2131690767;
          }
        }
      }
    }
  }
  
  protected void ensureView()
  {
    if (this.mRoot == null) {
      inflateView();
    }
  }
  
  public void forceHide()
  {
    if (this.mRoot != null) {
      this.mRoot.setVisibility(4);
    }
  }
  
  public int getSecurityPromptStringId()
  {
    KeyguardSecurityModel.SecurityMode localSecurityMode = KeyguardSecurityModel.SecurityMode.None;
    if (this.mKeyguardView != null) {
      localSecurityMode = this.mKeyguardView.getSecurityMode();
    }
    return translatePromptReasonToStringId(this.mBouncerPromptReason, localSecurityMode);
  }
  
  public void hide(boolean paramBoolean)
  {
    this.mFalsingManager.onBouncerHidden();
    cancelShowRunnable();
    if (this.mKeyguardView != null)
    {
      this.mKeyguardView.cancelDismissAction();
      this.mKeyguardView.cleanUp();
    }
    if (paramBoolean) {
      removeView();
    }
    while (this.mRoot == null) {
      return;
    }
    this.mRoot.setVisibility(4);
  }
  
  protected void inflateView()
  {
    removeView();
    this.mRoot = ((ViewGroup)LayoutInflater.from(this.mContext).inflate(2130968641, null));
    this.mKeyguardView = ((KeyguardHostView)this.mRoot.findViewById(2131951861));
    this.mKeyguardView.setLockPatternUtils(this.mLockPatternUtils);
    this.mKeyguardView.setViewMediatorCallback(this.mCallback);
    this.mContainer.addView(this.mRoot, this.mContainer.getChildCount() - 1);
    this.mRoot.setVisibility(4);
  }
  
  public boolean interceptMediaKey(KeyEvent paramKeyEvent)
  {
    ensureView();
    return this.mKeyguardView.interceptMediaKey(paramKeyEvent);
  }
  
  public boolean isCheckingPassword()
  {
    if (this.mKeyguardView != null) {
      return this.mKeyguardView.isCheckingPassword();
    }
    return false;
  }
  
  public boolean isFullscreenBouncer()
  {
    if (this.mKeyguardView != null)
    {
      KeyguardSecurityModel.SecurityMode localSecurityMode = this.mKeyguardView.getCurrentSecurityMode();
      return (localSecurityMode == KeyguardSecurityModel.SecurityMode.SimPin) || (localSecurityMode == KeyguardSecurityModel.SecurityMode.SimPuk);
    }
    return false;
  }
  
  public boolean isSecure()
  {
    return (this.mKeyguardView == null) || (this.mKeyguardView.getSecurityMode() != KeyguardSecurityModel.SecurityMode.None);
  }
  
  public boolean isShowing()
  {
    return (this.mShowingSoon) || ((this.mRoot != null) && (this.mRoot.getVisibility() == 0));
  }
  
  public boolean isUserUnlocked()
  {
    if ((Utils.is8998Device()) && (this.mLockPatternUtils.isSecure(KeyguardUpdateMonitor.getCurrentUser()))) {
      return KeyguardUpdateMonitor.getInstance(this.mContext).isUserUnlocked();
    }
    return true;
  }
  
  public boolean needsFullscreenBouncer()
  {
    ensureView();
    if (!isUserUnlocked()) {
      return true;
    }
    if (this.mKeyguardView != null)
    {
      KeyguardSecurityModel.SecurityMode localSecurityMode = this.mKeyguardView.getSecurityMode();
      return (localSecurityMode == KeyguardSecurityModel.SecurityMode.SimPin) || (localSecurityMode == KeyguardSecurityModel.SecurityMode.SimPuk);
    }
    return false;
  }
  
  public void notifyKeyguardAuthenticated(boolean paramBoolean)
  {
    ensureView();
    this.mKeyguardView.finish(paramBoolean);
  }
  
  public void onScreenTurnedOff()
  {
    if ((this.mKeyguardView != null) && (this.mRoot != null) && (this.mRoot.getVisibility() == 0)) {
      this.mKeyguardView.onPause();
    }
  }
  
  public void prepare()
  {
    if (this.mRoot != null) {}
    for (int i = 1;; i = 0)
    {
      ensureView();
      if (i != 0) {
        this.mKeyguardView.showPrimarySecurityScreen();
      }
      this.mBouncerPromptReason = this.mCallback.getBouncerPromptReason();
      return;
    }
  }
  
  protected void removeView()
  {
    if ((this.mRoot != null) && (this.mRoot.getParent() == this.mContainer))
    {
      this.mContainer.removeView(this.mRoot);
      this.mRoot = null;
    }
  }
  
  public boolean shouldDismissOnMenuPressed()
  {
    return this.mKeyguardView.shouldEnableMenuKey();
  }
  
  public void show(boolean paramBoolean)
  {
    int k = 0;
    int m = KeyguardUpdateMonitor.getCurrentUser();
    if ((m == 0) && (UserManager.isSplitSystemUser())) {
      return;
    }
    this.mFalsingManager.onBouncerShown();
    ensureView();
    if (paramBoolean) {
      this.mKeyguardView.showPrimarySecurityScreen();
    }
    if ((this.mRoot.getVisibility() == 0) || (this.mShowingSoon)) {
      return;
    }
    int n = KeyguardUpdateMonitor.getCurrentUser();
    if ((UserManager.isSplitSystemUser()) && (n == 0)) {}
    int j;
    for (int i = 1;; i = 0)
    {
      j = k;
      if (i == 0)
      {
        j = k;
        if (n == m) {
          j = 1;
        }
      }
      if ((j == 0) || (!this.mKeyguardView.dismiss())) {
        break;
      }
      return;
    }
    if (j == 0) {
      Slog.w("KeyguardBouncer", "User can't dismiss keyguard: " + n + " != " + m);
    }
    this.mShowingSoon = true;
    DejankUtils.postAfterTraversal(this.mShowRunnable);
  }
  
  public void showMessage(String paramString, int paramInt)
  {
    this.mKeyguardView.showMessage(paramString, paramInt);
  }
  
  public void showPromptReason(int paramInt)
  {
    this.mKeyguardView.showPromptReason(paramInt);
  }
  
  public void showWithDismissAction(KeyguardHostView.OnDismissAction paramOnDismissAction, Runnable paramRunnable)
  {
    ensureView();
    this.mKeyguardView.setOnDismissAction(paramOnDismissAction, paramRunnable);
    show(false);
  }
  
  public void startPreHideAnimation(Runnable paramRunnable)
  {
    if (this.mKeyguardView != null) {
      this.mKeyguardView.startDisappearAnimation(paramRunnable);
    }
    while (paramRunnable == null) {
      return;
    }
    paramRunnable.run();
  }
  
  public void updateBouncerPromptReason()
  {
    if (this.mCallback != null) {
      this.mBouncerPromptReason = this.mCallback.getBouncerPromptReason();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\KeyguardBouncer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */