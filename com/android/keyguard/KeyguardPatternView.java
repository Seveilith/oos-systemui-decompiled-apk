package com.android.keyguard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Debug;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternChecker.OnCheckCallback;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternView;
import com.android.internal.widget.LockPatternView.Cell;
import com.android.internal.widget.LockPatternView.CellState;
import com.android.internal.widget.LockPatternView.DisplayMode;
import com.android.internal.widget.LockPatternView.OnPatternListener;
import com.android.settingslib.animation.AppearAnimationCreator;
import com.android.settingslib.animation.AppearAnimationUtils;
import com.android.settingslib.animation.DisappearAnimationUtils;
import java.util.List;

public class KeyguardPatternView
  extends LinearLayout
  implements KeyguardSecurityView, AppearAnimationCreator<LockPatternView.CellState>, EmergencyButton.EmergencyButtonCallback
{
  private final AppearAnimationUtils mAppearAnimationUtils = new AppearAnimationUtils(paramContext, 220L, 1.5F, 2.0F, AnimationUtils.loadInterpolator(this.mContext, 17563662));
  private KeyguardSecurityCallback mCallback;
  private Runnable mCancelPatternRunnable = new Runnable()
  {
    public void run()
    {
      KeyguardPatternView.-get4(KeyguardPatternView.this).clearPattern();
    }
  };
  private ViewGroup mContainer;
  private CountDownTimer mCountdownTimer = null;
  private final DisappearAnimationUtils mDisappearAnimationUtils = new DisappearAnimationUtils(paramContext, 125L, 1.2F, 0.6F, AnimationUtils.loadInterpolator(this.mContext, 17563663));
  private final DisappearAnimationUtils mDisappearAnimationUtilsLocked = new DisappearAnimationUtils(paramContext, 187L, 1.2F, 0.6F, AnimationUtils.loadInterpolator(this.mContext, 17563663));
  private int mDisappearYTranslation = getResources().getDimensionPixelSize(R.dimen.disappear_y_translation);
  private View mEcaView;
  private View mFingerprintIcon;
  private final KeyguardUpdateMonitor mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
  private long mLastPokeTime = -7000L;
  private boolean mLockOut = false;
  private LockPatternUtils mLockPatternUtils;
  private LockPatternView mLockPatternView;
  private int mMaxCountdownTimes;
  private AsyncTask<?, ?, ?> mPendingLockCheck;
  private KeyguardMessageArea mSecurityMessageDisplay;
  private Rect mTempRect = new Rect();
  
  public KeyguardPatternView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public KeyguardPatternView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void displayDefaultSecurityMessage()
  {
    this.mSecurityMessageDisplay.setTimeout(0);
    this.mSecurityMessageDisplay.setMessage(getMessageWithCount(R.string.kg_pattern_instructions), true);
  }
  
  private void enableClipping(boolean paramBoolean)
  {
    setClipChildren(paramBoolean);
    this.mContainer.setClipToPadding(paramBoolean);
    this.mContainer.setClipChildren(paramBoolean);
  }
  
  private String getMessageWithCount(int paramInt)
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
  
  private void handleAttemptLockout(long paramLong)
  {
    this.mLockPatternView.clearPattern();
    this.mLockPatternView.setEnabled(false);
    this.mKeyguardUpdateMonitor.updateFingerprintListeningState();
    this.mKeyguardUpdateMonitor.notifyPasswordLockout();
    if (this.mCallback != null) {
      this.mCallback.hideSecurityIcon();
    }
    long l = SystemClock.elapsedRealtime();
    this.mLockOut = true;
    this.mCountdownTimer = new CountDownTimer(paramLong - l, 1000L)
    {
      public void onFinish()
      {
        KeyguardPatternView.-get4(KeyguardPatternView.this).setEnabled(true);
        KeyguardPatternView.-wrap1(KeyguardPatternView.this);
        KeyguardPatternView.-get2(KeyguardPatternView.this).clearFailedUnlockAttempts();
        KeyguardPatternView.-set0(KeyguardPatternView.this, false);
        KeyguardPatternView.-get2(KeyguardPatternView.this).updateFingerprintListeningState();
      }
      
      public void onTick(long paramAnonymousLong)
      {
        int i = (int)(paramAnonymousLong / 1000L);
        KeyguardPatternView.-get7(KeyguardPatternView.this).setMessage(R.string.kg_too_many_failed_attempts_countdown, true, new Object[] { Integer.valueOf(i) });
      }
    }.start();
  }
  
  public void createAnimation(LockPatternView.CellState paramCellState, long paramLong1, long paramLong2, float paramFloat, boolean paramBoolean, Interpolator paramInterpolator, Runnable paramRunnable)
  {
    LockPatternView localLockPatternView = this.mLockPatternView;
    float f1;
    float f2;
    label23:
    float f3;
    if (paramBoolean)
    {
      f1 = 1.0F;
      if (!paramBoolean) {
        break label95;
      }
      f2 = paramFloat;
      if (!paramBoolean) {
        break label101;
      }
      f3 = 0.0F;
      label31:
      if (!paramBoolean) {
        break label108;
      }
    }
    label95:
    label101:
    label108:
    for (float f4 = 0.0F;; f4 = 1.0F)
    {
      localLockPatternView.startCellStateAnimation(paramCellState, 1.0F, f1, f2, f3, f4, 1.0F, paramLong1, paramLong2, paramInterpolator, paramRunnable);
      if (paramRunnable != null) {
        this.mAppearAnimationUtils.createAnimation(this.mEcaView, paramLong1, paramLong2, paramFloat, paramBoolean, paramInterpolator, null);
      }
      return;
      f1 = 0.0F;
      break;
      f2 = 0.0F;
      break label23;
      f3 = paramFloat;
      break label31;
    }
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
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
    super.onFinishInflate();
    this.mMaxCountdownTimes = this.mContext.getResources().getInteger(R.integer.config_max_unlock_countdown_times);
    if (this.mLockPatternUtils == null) {}
    for (Object localObject = new LockPatternUtils(this.mContext);; localObject = this.mLockPatternUtils)
    {
      this.mLockPatternUtils = ((LockPatternUtils)localObject);
      this.mLockPatternView = ((LockPatternView)findViewById(R.id.lockPatternView));
      this.mLockPatternView.setSaveEnabled(false);
      this.mLockPatternView.setOnPatternListener(new UnlockPatternListener(null));
      this.mLockPatternView.setTactileFeedbackEnabled(this.mLockPatternUtils.isTactileFeedbackEnabled());
      this.mSecurityMessageDisplay = ((KeyguardMessageArea)KeyguardMessageArea.findSecurityMessageDisplay(this));
      this.mEcaView = findViewById(R.id.keyguard_selector_fade_container);
      this.mContainer = ((ViewGroup)findViewById(R.id.container));
      this.mFingerprintIcon = findViewById(R.id.fingerprint_icon);
      localObject = (EmergencyButton)findViewById(R.id.emergency_call_button);
      if (localObject != null) {
        ((EmergencyButton)localObject).setCallback(this);
      }
      displayDefaultSecurityMessage();
      return;
    }
  }
  
  public void onPause()
  {
    if (this.mCountdownTimer != null)
    {
      this.mCountdownTimer.cancel();
      this.mCountdownTimer = null;
    }
    if (this.mPendingLockCheck != null)
    {
      Log.d("SecurityPatternView", "onPause to cancel, " + Debug.getCallers(7));
      this.mPendingLockCheck.cancel(false);
      this.mPendingLockCheck = null;
    }
  }
  
  public void onResume(int paramInt)
  {
    reset();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool = super.onTouchEvent(paramMotionEvent);
    long l1 = SystemClock.elapsedRealtime();
    long l2 = this.mLastPokeTime;
    if ((bool) && (l1 - l2 > 6900L)) {
      this.mLastPokeTime = SystemClock.elapsedRealtime();
    }
    this.mTempRect.set(0, 0, 0, 0);
    offsetRectIntoDescendantCoords(this.mLockPatternView, this.mTempRect);
    paramMotionEvent.offsetLocation(this.mTempRect.left, this.mTempRect.top);
    if (!this.mLockPatternView.dispatchTouchEvent(paramMotionEvent)) {}
    for (;;)
    {
      paramMotionEvent.offsetLocation(-this.mTempRect.left, -this.mTempRect.top);
      return bool;
      bool = true;
    }
  }
  
  public void reset()
  {
    LockPatternView localLockPatternView = this.mLockPatternView;
    if (this.mLockPatternUtils.isVisiblePatternEnabled(KeyguardUpdateMonitor.getCurrentUser())) {}
    for (boolean bool = false;; bool = true)
    {
      localLockPatternView.setInStealthMode(bool);
      this.mLockPatternView.enableInput();
      this.mLockPatternView.setEnabled(true);
      this.mLockPatternView.clearPattern();
      long l = this.mLockPatternUtils.getLockoutAttemptDeadline(KeyguardUpdateMonitor.getCurrentUser());
      if (l == 0L) {
        break;
      }
      handleAttemptLockout(l);
      return;
    }
    if (this.mLockOut)
    {
      this.mKeyguardUpdateMonitor.clearFailedUnlockAttempts();
      this.mLockOut = false;
    }
    displayDefaultSecurityMessage();
  }
  
  public void setKeyguardCallback(KeyguardSecurityCallback paramKeyguardSecurityCallback)
  {
    this.mCallback = paramKeyguardSecurityCallback;
  }
  
  public void setLockPatternUtils(LockPatternUtils paramLockPatternUtils)
  {
    this.mLockPatternUtils = paramLockPatternUtils;
  }
  
  public void showMessage(String paramString, int paramInt)
  {
    this.mSecurityMessageDisplay.setNextMessageColor(paramInt);
    this.mSecurityMessageDisplay.setMessage(paramString, true);
  }
  
  public void showPromptReason(int paramInt)
  {
    if (!this.mKeyguardUpdateMonitor.isUserUnlocked())
    {
      this.mSecurityMessageDisplay.setMessage(getMessageWithCount(17040043), true);
      return;
    }
    if (this.mKeyguardUpdateMonitor.isFacelockAvailable())
    {
      this.mSecurityMessageDisplay.setMessage(R.string.face_unlock_notify_pattern, true);
      return;
    }
    switch (paramInt)
    {
    default: 
      this.mSecurityMessageDisplay.setMessage(R.string.kg_prompt_reason_timeout_pattern, true);
    case 0: 
      return;
    case 1: 
      this.mSecurityMessageDisplay.setMessage(R.string.kg_prompt_reason_restart_pattern, true);
      return;
    case 2: 
      this.mSecurityMessageDisplay.setMessage(R.string.kg_prompt_reason_timeout_pattern, true);
      return;
    case 3: 
      this.mSecurityMessageDisplay.setMessage(R.string.kg_prompt_reason_device_admin, true);
      return;
    }
    this.mSecurityMessageDisplay.setMessage(R.string.kg_prompt_reason_user_request, true);
  }
  
  public void startAppearAnimation()
  {
    enableClipping(false);
    setAlpha(1.0F);
    setTranslationY(this.mAppearAnimationUtils.getStartTranslation());
    AppearAnimationUtils.startTranslationYAnimation(this, 0L, 500L, 0.0F, this.mAppearAnimationUtils.getInterpolator());
    this.mAppearAnimationUtils.startAnimation2d(this.mLockPatternView.getCellStates(), new Runnable()
    {
      public void run()
      {
        KeyguardPatternView.-wrap2(KeyguardPatternView.this, true);
      }
    }, this);
    if (!TextUtils.isEmpty(this.mSecurityMessageDisplay.getText())) {
      this.mAppearAnimationUtils.createAnimation(this.mSecurityMessageDisplay, 0L, 220L, this.mAppearAnimationUtils.getStartTranslation(), true, this.mAppearAnimationUtils.getInterpolator(), null);
    }
    if (this.mFingerprintIcon.getVisibility() == 0) {
      this.mAppearAnimationUtils.createAnimation(this.mFingerprintIcon, 0L, 220L, this.mAppearAnimationUtils.getStartTranslation(), true, this.mAppearAnimationUtils.getInterpolator(), null);
    }
  }
  
  public boolean startDisappearAnimation(Runnable paramRunnable)
  {
    float f;
    if (this.mKeyguardUpdateMonitor.needsSlowUnlockTransition())
    {
      f = 1.5F;
      this.mLockPatternView.clearPattern();
      enableClipping(false);
      setTranslationY(0.0F);
      AppearAnimationUtils.startTranslationYAnimation(this, 0L, (300.0F * f), -this.mDisappearAnimationUtils.getStartTranslation(), this.mDisappearAnimationUtils.getInterpolator());
      if (!this.mKeyguardUpdateMonitor.needsSlowUnlockTransition()) {
        break label197;
      }
    }
    label197:
    for (DisappearAnimationUtils localDisappearAnimationUtils = this.mDisappearAnimationUtilsLocked;; localDisappearAnimationUtils = this.mDisappearAnimationUtils)
    {
      localDisappearAnimationUtils.startAnimation2d(this.mLockPatternView.getCellStates(), new -boolean_startDisappearAnimation_java_lang_Runnable_finishRunnable_LambdaImpl0(paramRunnable), this);
      if (!TextUtils.isEmpty(this.mSecurityMessageDisplay.getText())) {
        this.mDisappearAnimationUtils.createAnimation(this.mSecurityMessageDisplay, 0L, (200.0F * f), -this.mDisappearAnimationUtils.getStartTranslation() * 3.0F, false, this.mDisappearAnimationUtils.getInterpolator(), null);
      }
      if (this.mFingerprintIcon.getVisibility() == 0) {
        this.mDisappearAnimationUtils.createAnimation(this.mFingerprintIcon, 0L, 200L, -this.mDisappearAnimationUtils.getStartTranslation() * 3.0F, false, this.mDisappearAnimationUtils.getInterpolator(), null);
      }
      return true;
      f = 1.0F;
      break;
    }
  }
  
  private class UnlockPatternListener
    implements LockPatternView.OnPatternListener
  {
    private UnlockPatternListener() {}
    
    private void onPatternChecked(int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2)
    {
      int i;
      if (KeyguardUpdateMonitor.getCurrentUser() == paramInt1)
      {
        i = 1;
        if (!paramBoolean1) {
          break label77;
        }
        KeyguardPatternView.-get3(KeyguardPatternView.this).sanitizePassword();
        KeyguardPatternView.-get0(KeyguardPatternView.this).reportUnlockAttempt(paramInt1, true, 0);
        if (i != 0)
        {
          KeyguardPatternView.-get4(KeyguardPatternView.this).setDisplayMode(LockPatternView.DisplayMode.Correct);
          KeyguardPatternView.-get0(KeyguardPatternView.this).dismiss(true);
        }
      }
      label77:
      do
      {
        return;
        i = 0;
        break;
        KeyguardPatternView.-get4(KeyguardPatternView.this).setDisplayMode(LockPatternView.DisplayMode.Wrong);
        if (paramBoolean2)
        {
          KeyguardPatternView.-get0(KeyguardPatternView.this).reportUnlockAttempt(paramInt1, false, paramInt2);
          if ((KeyguardPatternView.-get5(KeyguardPatternView.this) <= 0) && (paramInt2 > 0))
          {
            long l = KeyguardPatternView.-get3(KeyguardPatternView.this).setLockoutAttemptDeadline(paramInt1, paramInt2);
            KeyguardPatternView.-wrap3(KeyguardPatternView.this, l);
          }
        }
      } while (paramInt2 != 0);
      paramInt1 = KeyguardPatternView.-get2(KeyguardPatternView.this).getFailedUnlockAttempts(paramInt1);
      if (paramInt1 % 5 == 3) {
        KeyguardPatternView.-get7(KeyguardPatternView.this).setMessage(R.string.kg_wrong_pattern_warning, true);
      }
      for (;;)
      {
        KeyguardPatternView.-get4(KeyguardPatternView.this).postDelayed(KeyguardPatternView.-get1(KeyguardPatternView.this), 2000L);
        return;
        if (paramInt1 % 5 == 4) {
          KeyguardPatternView.-get7(KeyguardPatternView.this).setMessage(R.string.kg_wrong_pattern_warning_one, true);
        } else {
          KeyguardPatternView.-get7(KeyguardPatternView.this).setMessage(KeyguardPatternView.-wrap0(KeyguardPatternView.this, R.string.kg_wrong_pattern), true);
        }
      }
    }
    
    public void onPatternCellAdded(List<LockPatternView.Cell> paramList)
    {
      KeyguardPatternView.-get0(KeyguardPatternView.this).userActivity();
    }
    
    public void onPatternCleared() {}
    
    public void onPatternDetected(List<LockPatternView.Cell> paramList)
    {
      KeyguardPatternView.-get4(KeyguardPatternView.this).disableInput();
      if (KeyguardPatternView.-get6(KeyguardPatternView.this) != null)
      {
        Log.d("SecurityPatternView", "onPatternDetected to cancel");
        KeyguardPatternView.-get6(KeyguardPatternView.this).cancel(false);
      }
      final int i = KeyguardUpdateMonitor.getCurrentUser();
      if (paramList.size() < 4)
      {
        KeyguardPatternView.-get4(KeyguardPatternView.this).enableInput();
        KeyguardPatternView.-get7(KeyguardPatternView.this).setMessage(R.string.kg_at_least_four_points, true);
        KeyguardPatternView.-get4(KeyguardPatternView.this).setDisplayMode(LockPatternView.DisplayMode.Wrong);
        onPatternChecked(i, false, 0, false);
        return;
      }
      Log.d("SecurityPatternView", "checkPattern begin");
      KeyguardPatternView.-set1(KeyguardPatternView.this, LockPatternChecker.checkPattern(KeyguardPatternView.-get3(KeyguardPatternView.this), paramList, i, new LockPatternChecker.OnCheckCallback()
      {
        public void onChecked(boolean paramAnonymousBoolean, int paramAnonymousInt)
        {
          Log.d("SecurityPatternView", "onChecked," + paramAnonymousBoolean + "," + paramAnonymousInt);
          if (paramAnonymousBoolean) {
            KeyguardPatternView.-get0(KeyguardPatternView.this).reportMDMEvent("lock_unlock_success", "pattern", "1");
          }
          for (;;)
          {
            KeyguardPatternView.-get4(KeyguardPatternView.this).enableInput();
            KeyguardPatternView.-set1(KeyguardPatternView.this, null);
            if (!paramAnonymousBoolean) {
              KeyguardPatternView.UnlockPatternListener.-wrap0(KeyguardPatternView.UnlockPatternListener.this, i, false, paramAnonymousInt, true);
            }
            return;
            KeyguardPatternView.-get0(KeyguardPatternView.this).reportMDMEvent("lock_unlock_failed", "pattern", "1");
          }
        }
        
        public void onEarlyMatched()
        {
          Log.d("SecurityPatternView", "onEarlyMatched: " + i);
          KeyguardPatternView.UnlockPatternListener.-wrap0(KeyguardPatternView.UnlockPatternListener.this, i, true, 0, true);
        }
      }));
      if (paramList.size() > 2) {
        KeyguardPatternView.-get0(KeyguardPatternView.this).userActivity();
      }
    }
    
    public void onPatternStart()
    {
      KeyguardPatternView.-get4(KeyguardPatternView.this).removeCallbacks(KeyguardPatternView.-get1(KeyguardPatternView.this));
      KeyguardPatternView.-get7(KeyguardPatternView.this).setMessage("", false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\KeyguardPatternView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */