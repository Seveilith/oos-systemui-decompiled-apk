package com.android.keyguard;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Debug;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternChecker.OnCheckCallback;
import com.android.internal.widget.LockPatternUtils;

public abstract class KeyguardAbsKeyInputView
  extends LinearLayout
  implements KeyguardSecurityView, EmergencyButton.EmergencyButtonCallback
{
  protected KeyguardSecurityCallback mCallback;
  private boolean mDismissing;
  protected View mEcaView;
  protected boolean mEnableHaptics;
  private boolean mLockOut = false;
  protected LockPatternUtils mLockPatternUtils;
  private int mMaxCountdownTimes = 0;
  protected AsyncTask<?, ?, ?> mPendingLockCheck;
  protected SecurityMessageDisplay mSecurityMessageDisplay;
  
  public KeyguardAbsKeyInputView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public KeyguardAbsKeyInputView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void onPasswordChecked(int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2)
  {
    boolean bool = false;
    int i;
    if (KeyguardUpdateMonitor.getCurrentUser() == paramInt1)
    {
      i = 1;
      if (!paramBoolean1) {
        break label76;
      }
      this.mLockPatternUtils.sanitizePassword();
      this.mCallback.reportUnlockAttempt(paramInt1, true, 0);
      if (i != 0)
      {
        this.mDismissing = true;
        this.mCallback.dismiss(true);
      }
      label56:
      if (!paramBoolean1) {
        break label164;
      }
    }
    label76:
    label164:
    for (paramBoolean1 = bool;; paramBoolean1 = true)
    {
      resetPasswordText(true, paramBoolean1);
      return;
      i = 0;
      break;
      if (paramBoolean2)
      {
        this.mCallback.reportUnlockAttempt(paramInt1, false, paramInt2);
        if ((this.mMaxCountdownTimes <= 0) && (paramInt2 > 0)) {
          handleAttemptLockout(this.mLockPatternUtils.setLockoutAttemptDeadline(paramInt1, paramInt2));
        }
      }
      if (paramInt2 == 0)
      {
        String str = getMessageWithCount(getWrongPasswordStringId());
        this.mSecurityMessageDisplay.setMessage(str, true);
      }
      ((Vibrator)getContext().getSystemService("vibrator")).vibrate(1000L);
      break label56;
    }
  }
  
  public void doHapticKeyClick()
  {
    if (this.mEnableHaptics) {
      performHapticFeedback(1, 3);
    }
  }
  
  protected String getMessageWithCount(int paramInt)
  {
    String str2 = getContext().getString(paramInt);
    paramInt = this.mMaxCountdownTimes - KeyguardUpdateMonitor.getInstance(this.mContext).getFailedUnlockAttempts(KeyguardUpdateMonitor.getCurrentUser());
    String str1 = str2;
    if (this.mMaxCountdownTimes > 0)
    {
      str1 = str2;
      if (paramInt > 0) {
        str1 = str2 + " - " + getContext().getResources().getString(R.string.kg_remaining_attempts, new Object[] { Integer.valueOf(paramInt) });
      }
    }
    return str1;
  }
  
  protected abstract String getPasswordText();
  
  protected abstract int getPasswordTextViewId();
  
  protected abstract int getPromtReasonStringRes(int paramInt);
  
  protected int getWrongPasswordStringId()
  {
    return R.string.kg_wrong_password;
  }
  
  protected void handleAttemptLockout(long paramLong)
  {
    setPasswordEntryEnabled(false);
    long l = SystemClock.elapsedRealtime();
    KeyguardUpdateMonitor.getInstance(this.mContext).updateFingerprintListeningState();
    KeyguardUpdateMonitor.getInstance(this.mContext).notifyPasswordLockout();
    if (this.mCallback != null) {
      this.mCallback.hideSecurityIcon();
    }
    this.mLockOut = true;
    new CountDownTimer(paramLong - l, 1000L)
    {
      public void onFinish()
      {
        KeyguardAbsKeyInputView.this.mSecurityMessageDisplay.setMessage("", false);
        KeyguardAbsKeyInputView.this.resetState();
        KeyguardUpdateMonitor.getInstance(KeyguardAbsKeyInputView.-get0(KeyguardAbsKeyInputView.this)).clearFailedUnlockAttempts();
        KeyguardAbsKeyInputView.-set0(KeyguardAbsKeyInputView.this, false);
        KeyguardUpdateMonitor.getInstance(KeyguardAbsKeyInputView.-get0(KeyguardAbsKeyInputView.this)).updateFingerprintListeningState();
      }
      
      public void onTick(long paramAnonymousLong)
      {
        int i = (int)(paramAnonymousLong / 1000L);
        KeyguardAbsKeyInputView.this.mSecurityMessageDisplay.setMessage(R.string.kg_too_many_failed_attempts_countdown, true, new Object[] { Integer.valueOf(i) });
      }
    }.start();
  }
  
  public boolean isCheckingPassword()
  {
    return this.mPendingLockCheck != null;
  }
  
  public boolean needsInput()
  {
    return false;
  }
  
  public void onEmergencyButtonClickedWhenInCall()
  {
    this.mCallback.reset();
  }
  
  protected void onFinishInflate()
  {
    this.mLockPatternUtils = new LockPatternUtils(this.mContext);
    this.mSecurityMessageDisplay = KeyguardMessageArea.findSecurityMessageDisplay(this);
    this.mEcaView = findViewById(R.id.keyguard_selector_fade_container);
    this.mMaxCountdownTimes = this.mContext.getResources().getInteger(R.integer.config_max_unlock_countdown_times);
    EmergencyButton localEmergencyButton = (EmergencyButton)findViewById(R.id.emergency_call_button);
    if (localEmergencyButton != null) {
      localEmergencyButton.setCallback(this);
    }
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramInt == 3) {
      if (this.mCallback != null) {
        this.mCallback.userActivity();
      }
    }
    for (;;)
    {
      return false;
      onUserInput();
    }
  }
  
  public void onPause()
  {
    if (this.mPendingLockCheck != null)
    {
      Log.d("KeyguardAbsKeyInputView", "onPause to cancel, " + Debug.getCallers(7));
      this.mPendingLockCheck.cancel(false);
      this.mPendingLockCheck = null;
    }
  }
  
  public void onResume(int paramInt)
  {
    reset();
  }
  
  protected void onUserInput()
  {
    if (this.mCallback != null) {
      this.mCallback.userActivity();
    }
    this.mSecurityMessageDisplay.setMessage("", false);
  }
  
  public void reset()
  {
    this.mDismissing = false;
    resetPasswordText(false, false);
    long l = this.mLockPatternUtils.getLockoutAttemptDeadline(KeyguardUpdateMonitor.getCurrentUser());
    if (shouldLockout(l))
    {
      handleAttemptLockout(l);
      return;
    }
    if (this.mLockOut)
    {
      KeyguardUpdateMonitor.getInstance(this.mContext).clearFailedUnlockAttempts();
      this.mLockOut = false;
    }
    resetState();
  }
  
  protected abstract void resetPasswordText(boolean paramBoolean1, boolean paramBoolean2);
  
  protected abstract void resetState();
  
  public void setKeyguardCallback(KeyguardSecurityCallback paramKeyguardSecurityCallback)
  {
    this.mCallback = paramKeyguardSecurityCallback;
  }
  
  public void setLockPatternUtils(LockPatternUtils paramLockPatternUtils)
  {
    this.mLockPatternUtils = paramLockPatternUtils;
    this.mEnableHaptics = this.mLockPatternUtils.isTactileFeedbackEnabled();
  }
  
  protected abstract void setPasswordEntryEnabled(boolean paramBoolean);
  
  protected abstract void setPasswordEntryInputEnabled(boolean paramBoolean);
  
  protected boolean shouldLockout(long paramLong)
  {
    return paramLong != 0L;
  }
  
  public void showMessage(String paramString, int paramInt)
  {
    this.mSecurityMessageDisplay.setNextMessageColor(paramInt);
    this.mSecurityMessageDisplay.setMessage(paramString, true);
  }
  
  public void showPromptReason(int paramInt)
  {
    if (!KeyguardUpdateMonitor.getInstance(this.mContext).isUserUnlocked())
    {
      this.mSecurityMessageDisplay.setMessage(17040043, true);
      return;
    }
    if (KeyguardUpdateMonitor.getInstance(this.mContext).isFacelockAvailable())
    {
      this.mSecurityMessageDisplay.setMessage(R.string.face_unlock_notify_password, true);
      return;
    }
    if (paramInt != 0)
    {
      paramInt = getPromtReasonStringRes(paramInt);
      if (paramInt != 0) {
        this.mSecurityMessageDisplay.setMessage(paramInt, true);
      }
    }
  }
  
  public boolean startDisappearAnimation(Runnable paramRunnable)
  {
    return false;
  }
  
  protected void verifyPasswordAndUnlock()
  {
    if (this.mDismissing) {
      return;
    }
    String str = getPasswordText();
    setPasswordEntryInputEnabled(false);
    if (this.mPendingLockCheck != null)
    {
      Log.d("KeyguardAbsKeyInputView", "verifyPasswordAndUnlock to cancel");
      this.mPendingLockCheck.cancel(false);
    }
    final int i = KeyguardUpdateMonitor.getCurrentUser();
    if (str.length() <= 3)
    {
      setPasswordEntryInputEnabled(true);
      onPasswordChecked(i, false, 0, false);
      return;
    }
    Log.d("KeyguardAbsKeyInputView", "checkPassword begin");
    this.mPendingLockCheck = LockPatternChecker.checkPassword(this.mLockPatternUtils, str, i, new LockPatternChecker.OnCheckCallback()
    {
      public void onChecked(boolean paramAnonymousBoolean, int paramAnonymousInt)
      {
        Log.d("KeyguardAbsKeyInputView", "onChecked, " + paramAnonymousBoolean + ", " + paramAnonymousInt);
        if (paramAnonymousBoolean) {
          KeyguardAbsKeyInputView.this.mCallback.reportMDMEvent("lock_unlock_success", "pass", "1");
        }
        for (;;)
        {
          KeyguardAbsKeyInputView.this.setPasswordEntryInputEnabled(true);
          KeyguardAbsKeyInputView.this.mPendingLockCheck = null;
          if (!paramAnonymousBoolean) {
            KeyguardAbsKeyInputView.-wrap0(KeyguardAbsKeyInputView.this, i, false, paramAnonymousInt, true);
          }
          return;
          KeyguardAbsKeyInputView.this.mCallback.reportMDMEvent("lock_unlock_failed", "pass", "1");
        }
      }
      
      public void onEarlyMatched()
      {
        Log.d("KeyguardAbsKeyInputView", "onEarlyMatched: " + i);
        KeyguardAbsKeyInputView.-wrap0(KeyguardAbsKeyInputView.this, i, true, 0, true);
      }
    });
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\KeyguardAbsKeyInputView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */