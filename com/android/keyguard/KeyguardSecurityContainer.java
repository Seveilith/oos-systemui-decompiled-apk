package com.android.keyguard;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.provider.Settings.Secure;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.android.internal.widget.LockPatternUtils;
import java.util.List;

public class KeyguardSecurityContainer
  extends FrameLayout
  implements KeyguardSecurityView
{
  private KeyguardSecurityCallback mCallback = new KeyguardSecurityCallback()
  {
    public void dismiss(boolean paramAnonymousBoolean)
    {
      KeyguardSecurityContainer.-get6(KeyguardSecurityContainer.this).dismiss(paramAnonymousBoolean);
    }
    
    public void hideSecurityIcon()
    {
      KeyguardSecurityContainer.-wrap0(KeyguardSecurityContainer.this);
    }
    
    public void reportMDMEvent(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
    {
      String str = paramAnonymousString2;
      if ("pass".equals(paramAnonymousString2))
      {
        if (KeyguardSecurityContainer.-get2(KeyguardSecurityContainer.this) != KeyguardSecurityModel.SecurityMode.Password) {
          break label46;
        }
        str = "password";
      }
      for (;;)
      {
        KeyguardSecurityContainer.-get6(KeyguardSecurityContainer.this).reportMDMEvent(paramAnonymousString1, str, paramAnonymousString3);
        return;
        label46:
        str = paramAnonymousString2;
        if (KeyguardSecurityContainer.-get2(KeyguardSecurityContainer.this) == KeyguardSecurityModel.SecurityMode.PIN) {
          str = "pin";
        }
      }
    }
    
    public void reportUnlockAttempt(int paramAnonymousInt1, boolean paramAnonymousBoolean, int paramAnonymousInt2)
    {
      KeyguardUpdateMonitor localKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(KeyguardSecurityContainer.-get1(KeyguardSecurityContainer.this));
      if (paramAnonymousBoolean)
      {
        localKeyguardUpdateMonitor.clearFailedUnlockAttempts();
        Settings.Secure.putIntForUser(KeyguardSecurityContainer.-get1(KeyguardSecurityContainer.this).getContentResolver(), "confirm_lock_password_fragment.key_num_wrong_confirm_attempts", 0, paramAnonymousInt1);
        KeyguardSecurityContainer.-get5(KeyguardSecurityContainer.this).reportSuccessfulPasswordAttempt(paramAnonymousInt1);
        return;
      }
      KeyguardSecurityContainer.-wrap1(KeyguardSecurityContainer.this, paramAnonymousInt1, paramAnonymousInt2);
    }
    
    public void reset()
    {
      KeyguardSecurityContainer.-get6(KeyguardSecurityContainer.this).reset();
    }
    
    public void tryToStartFaceLockFromBouncer()
    {
      KeyguardSecurityContainer.-get6(KeyguardSecurityContainer.this).tryToStartFaceLockFromBouncer();
    }
    
    public void userActivity()
    {
      if (KeyguardSecurityContainer.-get6(KeyguardSecurityContainer.this) != null) {
        KeyguardSecurityContainer.-get6(KeyguardSecurityContainer.this).userActivity();
      }
    }
  };
  private KeyguardSecurityModel.SecurityMode mCurrentSecuritySelection = KeyguardSecurityModel.SecurityMode.Invalid;
  private Animation mFacelockAnimationSet;
  private LockPatternUtils mLockPatternUtils;
  private KeyguardSecurityCallback mNullCallback = new KeyguardSecurityCallback()
  {
    public void dismiss(boolean paramAnonymousBoolean) {}
    
    public void hideSecurityIcon() {}
    
    public void reportMDMEvent(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3) {}
    
    public void reportUnlockAttempt(int paramAnonymousInt1, boolean paramAnonymousBoolean, int paramAnonymousInt2) {}
    
    public void reset() {}
    
    public void tryToStartFaceLockFromBouncer() {}
    
    public void userActivity() {}
  };
  private SecurityCallback mSecurityCallback;
  private View mSecurityIcon;
  private View mSecurityIconSwap;
  private KeyguardSecurityModel mSecurityModel;
  private KeyguardSecurityViewFlipper mSecurityViewFlipper;
  private final KeyguardUpdateMonitor mUpdateMonitor;
  private WipeConfirmListener mWipeConfirmListener = null;
  
  public KeyguardSecurityContainer(Context paramContext)
  {
    this(paramContext, null, 0);
  }
  
  public KeyguardSecurityContainer(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public KeyguardSecurityContainer(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mSecurityModel = new KeyguardSecurityModel(paramContext);
    this.mLockPatternUtils = new LockPatternUtils(paramContext);
    this.mUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
    this.mFacelockAnimationSet = AnimationUtils.loadAnimation(this.mContext, R.anim.facelock_lock_blink);
  }
  
  private KeyguardSecurityView getSecurityView(KeyguardSecurityModel.SecurityMode paramSecurityMode)
  {
    int j = getSecurityViewIdForMode(paramSecurityMode);
    Object localObject2 = null;
    int k = this.mSecurityViewFlipper.getChildCount();
    int i = 0;
    for (;;)
    {
      Object localObject1 = localObject2;
      if (i < k)
      {
        if (this.mSecurityViewFlipper.getChildAt(i).getId() == j) {
          localObject1 = (KeyguardSecurityView)this.mSecurityViewFlipper.getChildAt(i);
        }
      }
      else
      {
        i = getLayoutIdFor(paramSecurityMode);
        paramSecurityMode = (KeyguardSecurityModel.SecurityMode)localObject1;
        if (localObject1 == null)
        {
          paramSecurityMode = (KeyguardSecurityModel.SecurityMode)localObject1;
          if (i != 0)
          {
            paramSecurityMode = LayoutInflater.from(this.mContext).inflate(i, this.mSecurityViewFlipper, false);
            this.mSecurityViewFlipper.addView(paramSecurityMode);
            updateSecurityView(paramSecurityMode);
            paramSecurityMode = (KeyguardSecurityView)paramSecurityMode;
          }
        }
        updateSecurityIcon(paramSecurityMode);
        return paramSecurityMode;
      }
      i += 1;
    }
  }
  
  private int getSecurityViewIdForMode(KeyguardSecurityModel.SecurityMode paramSecurityMode)
  {
    switch (-getcom-android-keyguard-KeyguardSecurityModel$SecurityModeSwitchesValues()[paramSecurityMode.ordinal()])
    {
    default: 
      return 0;
    case 5: 
      return R.id.keyguard_pattern_view;
    case 3: 
      return R.id.keyguard_pin_view;
    case 4: 
      return R.id.keyguard_password_view;
    case 6: 
      return R.id.keyguard_sim_pin_view;
    }
    return R.id.keyguard_sim_puk_view;
  }
  
  private void hideSecurityIcon()
  {
    if (this.mSecurityIcon != null)
    {
      this.mSecurityIcon.setClickable(false);
      this.mSecurityIcon.setVisibility(4);
      this.mSecurityIcon.clearAnimation();
    }
    if (this.mSecurityIconSwap != null) {
      this.mSecurityIconSwap.setVisibility(0);
    }
    if (this.mFacelockAnimationSet != null) {
      this.mFacelockAnimationSet.setAnimationListener(null);
    }
  }
  
  private void reportFailedUnlockAttempt(int paramInt1, int paramInt2)
  {
    KeyguardUpdateMonitor localKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
    int n = localKeyguardUpdateMonitor.getFailedUnlockAttempts(paramInt1) + 1;
    Log.d("KeyguardSecurityView", "reportFailedPatternAttempt: #" + n);
    KeyguardSecurityModel.SecurityMode localSecurityMode = this.mSecurityModel.getSecurityMode();
    DevicePolicyManager localDevicePolicyManager = this.mLockPatternUtils.getDevicePolicyManager();
    int i = localDevicePolicyManager.getMaximumFailedPasswordsForWipe(null, paramInt1);
    int k;
    int j;
    label94:
    int m;
    if (i > 0)
    {
      k = i - n;
      if (localSecurityMode != KeyguardSecurityModel.SecurityMode.Pattern) {
        break label198;
      }
      j = 1;
      if (localSecurityMode != KeyguardSecurityModel.SecurityMode.PIN) {
        break label204;
      }
      m = 1;
      label105:
      if (localSecurityMode != KeyguardSecurityModel.SecurityMode.Password) {
        break label210;
      }
      i = 1;
      label115:
      int i1 = this.mContext.getResources().getInteger(R.integer.config_max_unlock_countdown_times);
      if (i1 <= 0) {
        break label220;
      }
      if ((j != 0) || (m != 0)) {
        break label215;
      }
      label145:
      if ((i == 0) || (n < i1)) {
        break label225;
      }
      showCountdownWipeDialog(n);
    }
    for (;;)
    {
      localKeyguardUpdateMonitor.reportFailedStrongAuthUnlockAttempt(paramInt1);
      this.mLockPatternUtils.reportFailedPasswordAttempt(paramInt1);
      if ((i == 0) && (paramInt2 > 0)) {
        showTimeoutDialog(paramInt2);
      }
      return;
      k = Integer.MAX_VALUE;
      break;
      label198:
      j = 0;
      break label94;
      label204:
      m = 0;
      break label105;
      label210:
      i = 0;
      break label115;
      label215:
      i = 1;
      break label145;
      label220:
      i = 0;
      break label145;
      label225:
      if (k < 5)
      {
        m = localDevicePolicyManager.getProfileWithMinimumFailedPasswordsForWipe(paramInt1);
        j = 1;
        if (m == paramInt1) {
          if (m != 0) {
            j = 3;
          }
        }
        for (;;)
        {
          if (k <= 0) {
            break label288;
          }
          showAlmostAtWipeDialog(n, k, j);
          break;
          if (m != 55536) {
            j = 2;
          }
        }
        label288:
        Slog.i("KeyguardSecurityView", "Too many unlock attempts; user " + m + " will be wiped!");
        showWipeDialog(n, j);
      }
    }
  }
  
  private void showAlmostAtWipeDialog(int paramInt1, int paramInt2, int paramInt3)
  {
    String str = null;
    switch (paramInt3)
    {
    }
    for (;;)
    {
      showDialog(null, str);
      return;
      str = this.mContext.getString(R.string.kg_failed_attempts_almost_at_wipe, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
      continue;
      str = this.mContext.getString(R.string.kg_failed_attempts_almost_at_erase_user, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
      continue;
      str = this.mContext.getString(R.string.kg_failed_attempts_almost_at_erase_profile, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
    }
  }
  
  private void showCountdownWipeDialog(int paramInt)
  {
    int i = R.string.kg_failed_attempts_now_wiping;
    switch (-getcom-android-keyguard-KeyguardSecurityModel$SecurityModeSwitchesValues()[this.mSecurityModel.getSecurityMode().ordinal()])
    {
    }
    for (;;)
    {
      if (this.mWipeConfirmListener == null) {
        this.mWipeConfirmListener = new WipeConfirmListener(null);
      }
      AlertDialog localAlertDialog = new AlertDialog.Builder(this.mContext).setMessage(this.mContext.getString(i, new Object[] { Integer.valueOf(paramInt) })).setNegativeButton(17040557, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          KeyguardSecurityContainer.-wrap2(KeyguardSecurityContainer.this);
        }
      }).setPositiveButton(17040558, this.mWipeConfirmListener).setCancelable(false).create();
      if (!(this.mContext instanceof Activity)) {
        localAlertDialog.getWindow().setType(2009);
      }
      localAlertDialog.show();
      return;
      i = R.string.kg_failed_pin_attempts_now_wiping;
      continue;
      i = R.string.kg_failed_password_attempts_now_wiping;
      continue;
      i = R.string.kg_failed_pattern_attempts_now_wiping;
    }
  }
  
  private void showDialog(String paramString1, String paramString2)
  {
    int j = Utils.getThemeColor(this.mContext);
    if (j == 1) {}
    for (int i = 16974374;; i = 16974394)
    {
      paramString1 = new AlertDialog.Builder(this.mContext, i).setTitle(paramString1).setMessage(paramString2).setNeutralButton(R.string.ok, null).create();
      if (!(this.mContext instanceof Activity)) {
        paramString1.getWindow().setType(2009);
      }
      paramString1.show();
      if (j != 2) {
        paramString1.getButton(-3).setTextColor(Utils.getThemeAccentColor(this.mContext, j));
      }
      return;
    }
  }
  
  private void showSecurityScreen(KeyguardSecurityModel.SecurityMode paramSecurityMode)
  {
    if (paramSecurityMode == this.mCurrentSecuritySelection) {
      return;
    }
    Object localObject = getSecurityView(this.mCurrentSecuritySelection);
    KeyguardSecurityView localKeyguardSecurityView = getSecurityView(paramSecurityMode);
    if (localObject != null)
    {
      ((KeyguardSecurityView)localObject).onPause();
      ((KeyguardSecurityView)localObject).setKeyguardCallback(this.mNullCallback);
    }
    if (paramSecurityMode != KeyguardSecurityModel.SecurityMode.None)
    {
      localKeyguardSecurityView.onResume(2);
      localKeyguardSecurityView.setKeyguardCallback(this.mCallback);
    }
    int j = this.mSecurityViewFlipper.getChildCount();
    int k = getSecurityViewIdForMode(paramSecurityMode);
    int i = 0;
    if (i < j)
    {
      if (this.mSecurityViewFlipper.getChildAt(i).getId() == k) {
        this.mSecurityViewFlipper.setDisplayedChild(i);
      }
    }
    else
    {
      this.mCurrentSecuritySelection = paramSecurityMode;
      localObject = this.mSecurityCallback;
      if (paramSecurityMode == KeyguardSecurityModel.SecurityMode.None) {
        break label166;
      }
    }
    label166:
    for (boolean bool = localKeyguardSecurityView.needsInput();; bool = false)
    {
      ((SecurityCallback)localObject).onSecurityModeChanged(paramSecurityMode, bool);
      return;
      i += 1;
      break;
    }
  }
  
  private void showTimeoutDialog(int paramInt)
  {
    int j = paramInt / 1000;
    int i = 0;
    paramInt = i;
    switch (-getcom-android-keyguard-KeyguardSecurityModel$SecurityModeSwitchesValues()[this.mSecurityModel.getSecurityMode().ordinal()])
    {
    default: 
      paramInt = i;
    }
    for (;;)
    {
      if (paramInt != 0) {
        showDialog(null, this.mContext.getString(paramInt, new Object[] { Integer.valueOf(KeyguardUpdateMonitor.getInstance(this.mContext).getFailedUnlockAttempts(KeyguardUpdateMonitor.getCurrentUser())), Integer.valueOf(j) }));
      }
      return;
      paramInt = R.string.kg_too_many_failed_pattern_attempts_dialog_message;
      continue;
      paramInt = R.string.kg_too_many_failed_pin_attempts_dialog_message;
      continue;
      paramInt = R.string.kg_too_many_failed_password_attempts_dialog_message;
    }
  }
  
  private void showWipeConfirmDialog()
  {
    AlertDialog localAlertDialog = new AlertDialog.Builder(this.mContext).setMessage(R.string.kg_failed_attempts_now_wiping_confirm).setNegativeButton(17040557, this.mWipeConfirmListener).setPositiveButton(17040558, this.mWipeConfirmListener).setCancelable(false).create();
    if (!(this.mContext instanceof Activity)) {
      localAlertDialog.getWindow().setType(2009);
    }
    localAlertDialog.show();
  }
  
  private void showWipeDialog(int paramInt1, int paramInt2)
  {
    String str = null;
    switch (paramInt2)
    {
    }
    for (;;)
    {
      showDialog(null, str);
      return;
      str = this.mContext.getString(R.string.kg_failed_attempts_now_wiping, new Object[] { Integer.valueOf(paramInt1) });
      continue;
      str = this.mContext.getString(R.string.kg_failed_attempts_now_erasing_user, new Object[] { Integer.valueOf(paramInt1) });
      continue;
      str = this.mContext.getString(R.string.kg_failed_attempts_now_erasing_profile, new Object[] { Integer.valueOf(paramInt1) });
    }
  }
  
  private void updateIconAnimation()
  {
    if ((this.mSecurityIcon == null) || (this.mFacelockAnimationSet == null)) {
      return;
    }
    if (!this.mUpdateMonitor.isFacelockRecognizing())
    {
      if (Utils.DEBUG_ONEPLUS) {
        Log.d("KeyguardSecurityView", "stop anim");
      }
      this.mSecurityIcon.clearAnimation();
      this.mFacelockAnimationSet.setAnimationListener(null);
      return;
    }
    this.mFacelockAnimationSet.setAnimationListener(new Animation.AnimationListener()
    {
      public void onAnimationEnd(Animation paramAnonymousAnimation)
      {
        if ((!KeyguardSecurityContainer.-get8(KeyguardSecurityContainer.this).isFacelockRecognizing()) || (KeyguardSecurityContainer.-get3(KeyguardSecurityContainer.this) == null)) {}
        while (KeyguardSecurityContainer.-get7(KeyguardSecurityContainer.this) == null) {
          return;
        }
        if (Utils.DEBUG_ONEPLUS) {
          Log.d("KeyguardSecurityView", "start again");
        }
        KeyguardSecurityContainer.-get3(KeyguardSecurityContainer.this).setAnimationListener(this);
        KeyguardSecurityContainer.-get7(KeyguardSecurityContainer.this).startAnimation(KeyguardSecurityContainer.-get3(KeyguardSecurityContainer.this));
      }
      
      public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
      
      public void onAnimationStart(Animation paramAnonymousAnimation) {}
    });
    if (Utils.DEBUG_ONEPLUS) {
      Log.d("KeyguardSecurityView", "start anim");
    }
    this.mSecurityIcon.startAnimation(this.mFacelockAnimationSet);
  }
  
  private void updateSecurityView(View paramView)
  {
    if ((paramView instanceof KeyguardSecurityView))
    {
      paramView = (KeyguardSecurityView)paramView;
      paramView.setKeyguardCallback(this.mCallback);
      paramView.setLockPatternUtils(this.mLockPatternUtils);
      return;
    }
    Log.w("KeyguardSecurityView", "View " + paramView + " is not a KeyguardSecurityView");
  }
  
  public KeyguardSecurityModel.SecurityMode getCurrentSecurityMode()
  {
    return this.mCurrentSecuritySelection;
  }
  
  public CharSequence getCurrentSecurityModeContentDescription()
  {
    View localView = (View)getSecurityView(this.mCurrentSecuritySelection);
    if (localView != null) {
      return localView.getContentDescription();
    }
    return "";
  }
  
  protected int getLayoutIdFor(KeyguardSecurityModel.SecurityMode paramSecurityMode)
  {
    switch (-getcom-android-keyguard-KeyguardSecurityModel$SecurityModeSwitchesValues()[paramSecurityMode.ordinal()])
    {
    default: 
      return 0;
    case 5: 
      return R.layout.keyguard_pattern_view;
    case 3: 
      return R.layout.keyguard_pin_view;
    case 4: 
      return R.layout.keyguard_password_view;
    case 6: 
      return R.layout.keyguard_sim_pin_view;
    }
    return R.layout.keyguard_sim_puk_view;
  }
  
  public KeyguardSecurityModel.SecurityMode getSecurityMode()
  {
    return this.mSecurityModel.getSecurityMode();
  }
  
  public boolean isCheckingPassword()
  {
    if (this.mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
      return getSecurityView(this.mCurrentSecuritySelection).isCheckingPassword();
    }
    return false;
  }
  
  public boolean needsInput()
  {
    return this.mSecurityViewFlipper.needsInput();
  }
  
  protected void onFinishInflate()
  {
    this.mSecurityViewFlipper = ((KeyguardSecurityViewFlipper)findViewById(R.id.view_flipper));
    this.mSecurityViewFlipper.setLockPatternUtils(this.mLockPatternUtils);
  }
  
  public void onPause()
  {
    if (this.mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
      getSecurityView(this.mCurrentSecuritySelection).onPause();
    }
  }
  
  public void onResume(int paramInt)
  {
    if (this.mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
      getSecurityView(this.mCurrentSecuritySelection).onResume(paramInt);
    }
  }
  
  public void setKeyguardCallback(KeyguardSecurityCallback paramKeyguardSecurityCallback)
  {
    this.mSecurityViewFlipper.setKeyguardCallback(paramKeyguardSecurityCallback);
  }
  
  public void setLockPatternUtils(LockPatternUtils paramLockPatternUtils)
  {
    this.mLockPatternUtils = paramLockPatternUtils;
    this.mSecurityModel.setLockPatternUtils(paramLockPatternUtils);
    this.mSecurityViewFlipper.setLockPatternUtils(this.mLockPatternUtils);
  }
  
  public void setSecurityCallback(SecurityCallback paramSecurityCallback)
  {
    this.mSecurityCallback = paramSecurityCallback;
  }
  
  public void showMessage(String paramString, int paramInt)
  {
    if (this.mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
      getSecurityView(this.mCurrentSecuritySelection).showMessage(paramString, paramInt);
    }
  }
  
  boolean showNextSecurityScreenOrFinish(boolean paramBoolean)
  {
    Log.d("KeyguardSecurityView", "showNextSecurityScreenOrFinish(" + paramBoolean + ")" + ", " + this.mCurrentSecuritySelection);
    boolean bool4 = false;
    boolean bool3 = false;
    boolean bool1;
    boolean bool2;
    if (this.mUpdateMonitor.getUserCanSkipBouncer(KeyguardUpdateMonitor.getCurrentUser()))
    {
      Log.d("KeyguardSecurityView", "user can skip bouncer");
      bool1 = true;
      bool2 = bool3;
    }
    for (;;)
    {
      if (bool1) {
        this.mSecurityCallback.finish(bool2);
      }
      return bool1;
      KeyguardSecurityModel.SecurityMode localSecurityMode;
      if (KeyguardSecurityModel.SecurityMode.None == this.mCurrentSecuritySelection)
      {
        localSecurityMode = this.mSecurityModel.getSecurityMode();
        if (KeyguardSecurityModel.SecurityMode.None == localSecurityMode)
        {
          bool1 = true;
          bool2 = bool3;
        }
        else
        {
          showSecurityScreen(localSecurityMode);
          bool1 = bool4;
          bool2 = bool3;
        }
      }
      else
      {
        bool1 = bool4;
        bool2 = bool3;
        if (paramBoolean) {
          switch (-getcom-android-keyguard-KeyguardSecurityModel$SecurityModeSwitchesValues()[this.mCurrentSecuritySelection.ordinal()])
          {
          default: 
            Log.v("KeyguardSecurityView", "Bad security screen " + this.mCurrentSecuritySelection + ", fail safe");
            showPrimarySecurityScreen(false);
            bool1 = bool4;
            bool2 = bool3;
            break;
          case 3: 
          case 4: 
          case 5: 
            bool2 = true;
            bool1 = true;
            break;
          case 6: 
          case 7: 
            localSecurityMode = this.mSecurityModel.getSecurityMode();
            if ((localSecurityMode == KeyguardSecurityModel.SecurityMode.None) && (this.mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser())))
            {
              bool1 = true;
              bool2 = bool3;
            }
            else
            {
              showSecurityScreen(localSecurityMode);
              bool1 = bool4;
              bool2 = bool3;
            }
            break;
          }
        }
      }
    }
  }
  
  void showPrimarySecurityScreen(boolean paramBoolean)
  {
    showSecurityScreen(this.mSecurityModel.getSecurityMode());
  }
  
  public void showPromptReason(int paramInt)
  {
    if (this.mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None)
    {
      if (paramInt != 0) {
        Log.i("KeyguardSecurityView", "Strong auth required, reason: " + paramInt);
      }
      getSecurityView(this.mCurrentSecuritySelection).showPromptReason(paramInt);
    }
  }
  
  public void startAppearAnimation()
  {
    if (this.mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
      getSecurityView(this.mCurrentSecuritySelection).startAppearAnimation();
    }
  }
  
  public boolean startDisappearAnimation(Runnable paramRunnable)
  {
    if (this.mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
      return getSecurityView(this.mCurrentSecuritySelection).startDisappearAnimation(paramRunnable);
    }
    return false;
  }
  
  public void updateSecurityIcon(KeyguardSecurityView paramKeyguardSecurityView)
  {
    boolean bool1;
    boolean bool2;
    int j;
    label103:
    int k;
    int i;
    if (paramKeyguardSecurityView != null)
    {
      this.mSecurityIcon = ((ViewGroup)paramKeyguardSecurityView).findViewById(R.id.fingerprint_icon);
      this.mSecurityIconSwap = ((ViewGroup)paramKeyguardSecurityView).findViewById(R.id.fingerprint_icon_swap);
      if (this.mSecurityIcon != null)
      {
        paramKeyguardSecurityView = (ImageView)this.mSecurityIcon.findViewById(R.id.security_image);
        if (this.mUpdateMonitor.isFacelockAvailable()) {
          break label214;
        }
        bool1 = this.mUpdateMonitor.isFacelockRecognizing();
        bool2 = this.mUpdateMonitor.isFingerprintDetectionRunning();
        if ((this.mUpdateMonitor.isUnlockingWithFingerprintAllowed()) && (!this.mUpdateMonitor.isFingerprintLockout())) {
          break label220;
        }
        j = 0;
        k = 0;
        if (!this.mUpdateMonitor.isKeyguardDone()) {
          break label225;
        }
        i = 0;
        label118:
        paramKeyguardSecurityView = this.mSecurityIcon;
        if (i == 0) {
          break label326;
        }
        j = 0;
        label129:
        paramKeyguardSecurityView.setVisibility(j);
        if (this.mSecurityIconSwap != null)
        {
          paramKeyguardSecurityView = this.mSecurityIconSwap;
          if (i != 0) {
            break label331;
          }
          j = 0;
          label152:
          paramKeyguardSecurityView.setVisibility(j);
        }
        if ((i == 0) || (!bool1) || (!this.mUpdateMonitor.isFacelockAvailable())) {
          break label337;
        }
        this.mSecurityIcon.setClickable(true);
        this.mSecurityIcon.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            Log.d("KeyguardSecurityView", "restart facelock from bouncer");
            if (KeyguardSecurityContainer.-get0(KeyguardSecurityContainer.this) != null)
            {
              KeyguardSecurityContainer.-get0(KeyguardSecurityContainer.this).userActivity();
              KeyguardSecurityContainer.-get0(KeyguardSecurityContainer.this).tryToStartFaceLockFromBouncer();
            }
          }
        });
        Log.d("KeyguardSecurityView", "show bouncer face icon");
      }
    }
    for (;;)
    {
      updateIconAnimation();
      return;
      label214:
      bool1 = true;
      break;
      label220:
      j = 1;
      break label103;
      label225:
      if (bool1)
      {
        i = 1;
        paramKeyguardSecurityView.setImageResource(R.drawable.facelock_bouncer_icon);
        break label118;
      }
      i = k;
      if (!bool2) {
        break label118;
      }
      i = k;
      if (j == 0) {
        break label118;
      }
      FingerprintManager localFingerprintManager = (FingerprintManager)getContext().getSystemService("fingerprint");
      List localList = localFingerprintManager.getEnrolledFingerprints();
      if (localList != null)
      {
        i = localList.size();
        label292:
        if ((!localFingerprintManager.isHardwareDetected()) || (i <= 0)) {
          break label321;
        }
      }
      label321:
      for (i = 1;; i = 0)
      {
        paramKeyguardSecurityView.setImageResource(R.drawable.ic_fingerprint_lockscreen_blow);
        break;
        i = 0;
        break label292;
      }
      label326:
      j = 4;
      break label129;
      label331:
      j = 8;
      break label152;
      label337:
      Log.d("KeyguardSecurityView", "hide bouncer face icon");
      this.mSecurityIcon.setClickable(false);
    }
  }
  
  public static abstract interface SecurityCallback
  {
    public abstract boolean dismiss(boolean paramBoolean);
    
    public abstract void finish(boolean paramBoolean);
    
    public abstract void onSecurityModeChanged(KeyguardSecurityModel.SecurityMode paramSecurityMode, boolean paramBoolean);
    
    public abstract void reportMDMEvent(String paramString1, String paramString2, String paramString3);
    
    public abstract void reset();
    
    public abstract void tryToStartFaceLockFromBouncer();
    
    public abstract void userActivity();
  }
  
  private class WipeConfirmListener
    implements DialogInterface.OnClickListener
  {
    private WipeConfirmListener() {}
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (-1 == paramInt)
      {
        KeyguardUpdateMonitor.getInstance(KeyguardSecurityContainer.-get1(KeyguardSecurityContainer.this)).clearFailedUnlockAttempts();
        return;
      }
      if (ActivityManager.isUserAMonkey()) {
        return;
      }
      paramDialogInterface = new Intent("android.intent.action.MASTER_CLEAR");
      KeyguardSecurityContainer.-get1(KeyguardSecurityContainer.this).sendBroadcast(paramDialogInterface);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\KeyguardSecurityContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */