package com.android.systemui.statusbar;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.app.IBatteryStats;
import com.android.internal.app.IBatteryStats.Stub;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitor.BatteryStatus;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.statusbar.phone.KeyguardBottomAreaView;
import com.android.systemui.statusbar.phone.KeyguardIndicationTextView;
import com.android.systemui.statusbar.phone.LockIcon;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.util.Utils;

public class KeyguardIndicationController
{
  private AudioManager mAudioManager;
  private final IBatteryStats mBatteryInfo;
  private int mBatteryLevel;
  private SoundPool mChargingSound;
  private int mChargingSoundId;
  private int mChargingSpeed;
  private int mChargingWattage;
  private final Context mContext;
  private AnimationDrawable mDashAnimation;
  private ImageView mDashView;
  private final int mFastThreshold;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      if ((paramAnonymousMessage.what == 1) && (KeyguardIndicationController.-get18(KeyguardIndicationController.this) != null))
      {
        KeyguardIndicationController.-set7(KeyguardIndicationController.this, null);
        KeyguardIndicationController.-wrap1(KeyguardIndicationController.this);
      }
      while (paramAnonymousMessage.what != 2) {
        return;
      }
      KeyguardIndicationController.-get12(KeyguardIndicationController.this).setTransientFpError(false);
      KeyguardIndicationController.this.hideTransientIndication();
    }
  };
  private KeyguardBottomAreaView mKeyguardBottomArea;
  private int mLastChargingSpeed;
  private TextView mLevelView;
  private final LockIcon mLockIcon;
  private String mMessageToShowOnScreenOn;
  private boolean mPowerCharged;
  private boolean mPowerPluggedIn;
  private String mRestingIndication;
  private boolean mShowMsgWhenExiting = false;
  private final int mSlowThreshold;
  private StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
  private final KeyguardIndicationTextView mTextView;
  BroadcastReceiver mTickReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (KeyguardIndicationController.-get19(KeyguardIndicationController.this)) {
        KeyguardIndicationController.-wrap1(KeyguardIndicationController.this);
      }
    }
  };
  private String mTransientIndication;
  private int mTransientTextColor;
  KeyguardUpdateMonitorCallback mUpdateMonitor = new KeyguardUpdateMonitorCallback()
  {
    public int mLastSuccessiveErrorMessage = -1;
    
    public void onFingerprintAuthFailed()
    {
      super.onFingerprintAuthFailed();
      this.mLastSuccessiveErrorMessage = -1;
    }
    
    public void onFingerprintAuthenticated(int paramAnonymousInt)
    {
      super.onFingerprintAuthenticated(paramAnonymousInt);
      this.mLastSuccessiveErrorMessage = -1;
    }
    
    public void onFingerprintError(int paramAnonymousInt, String paramAnonymousString)
    {
      KeyguardUpdateMonitor localKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(KeyguardIndicationController.-get5(KeyguardIndicationController.this));
      if ((!localKeyguardUpdateMonitor.isUnlockingWithFingerprintAllowed()) || (paramAnonymousInt == 5)) {
        return;
      }
      int i = KeyguardIndicationController.-get5(KeyguardIndicationController.this).getResources().getColor(2131492997, null);
      if (KeyguardIndicationController.-get17(KeyguardIndicationController.this).isBouncerShowing()) {
        if (this.mLastSuccessiveErrorMessage != paramAnonymousInt) {
          KeyguardIndicationController.-get17(KeyguardIndicationController.this).showBouncerMessage(paramAnonymousString, i);
        }
      }
      for (;;)
      {
        this.mLastSuccessiveErrorMessage = paramAnonymousInt;
        return;
        if (localKeyguardUpdateMonitor.isDeviceInteractive())
        {
          KeyguardIndicationController.this.showTransientIndication(paramAnonymousString, i);
          KeyguardIndicationController.-get9(KeyguardIndicationController.this).removeMessages(1);
          KeyguardIndicationController.this.hideTransientIndicationDelayed(5000L);
        }
        else
        {
          KeyguardIndicationController.-set4(KeyguardIndicationController.this, paramAnonymousString);
        }
      }
    }
    
    public void onFingerprintHelp(int paramAnonymousInt, String paramAnonymousString)
    {
      KeyguardUpdateMonitor localKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(KeyguardIndicationController.-get5(KeyguardIndicationController.this));
      if (!localKeyguardUpdateMonitor.isUnlockingWithFingerprintAllowed()) {
        return;
      }
      paramAnonymousInt = KeyguardIndicationController.-get5(KeyguardIndicationController.this).getResources().getColor(2131492997, null);
      if (KeyguardIndicationController.-get17(KeyguardIndicationController.this).isBouncerShowing()) {
        KeyguardIndicationController.-get17(KeyguardIndicationController.this).showBouncerMessage(paramAnonymousString, paramAnonymousInt);
      }
      for (;;)
      {
        this.mLastSuccessiveErrorMessage = -1;
        return;
        if (localKeyguardUpdateMonitor.isDeviceInteractive())
        {
          KeyguardIndicationController.-get12(KeyguardIndicationController.this).setTransientFpError(true);
          KeyguardIndicationController.this.showTransientIndication(paramAnonymousString, paramAnonymousInt);
          KeyguardIndicationController.-get9(KeyguardIndicationController.this).removeMessages(2);
          KeyguardIndicationController.-get9(KeyguardIndicationController.this).sendMessageDelayed(KeyguardIndicationController.-get9(KeyguardIndicationController.this).obtainMessage(2), 1300L);
        }
      }
    }
    
    public void onFingerprintRunningStateChanged(boolean paramAnonymousBoolean)
    {
      if (paramAnonymousBoolean) {
        KeyguardIndicationController.-set4(KeyguardIndicationController.this, null);
      }
    }
    
    public void onRefreshBatteryInfo(KeyguardUpdateMonitor.BatteryStatus paramAnonymousBatteryStatus)
    {
      boolean bool;
      if (paramAnonymousBatteryStatus.status != 2)
      {
        if (paramAnonymousBatteryStatus.status != 5) {
          break label330;
        }
        bool = true;
        KeyguardIndicationController localKeyguardIndicationController = KeyguardIndicationController.this;
        if (!paramAnonymousBatteryStatus.isPluggedIn()) {
          break label335;
        }
        label30:
        KeyguardIndicationController.-set6(localKeyguardIndicationController, bool);
        KeyguardIndicationController.-set5(KeyguardIndicationController.this, paramAnonymousBatteryStatus.isCharged());
        KeyguardIndicationController.-set2(KeyguardIndicationController.this, paramAnonymousBatteryStatus.maxChargingWattage);
        KeyguardIndicationController.-set3(KeyguardIndicationController.this, KeyguardIndicationController.-get4(KeyguardIndicationController.this));
        KeyguardIndicationController.-set1(KeyguardIndicationController.this, paramAnonymousBatteryStatus.getChargingSpeed(KeyguardIndicationController.-get16(KeyguardIndicationController.this), KeyguardIndicationController.-get8(KeyguardIndicationController.this)));
        KeyguardIndicationController.-set0(KeyguardIndicationController.this, paramAnonymousBatteryStatus.level);
        Log.d("KeyguardIndication", "onRefreshBatteryInfo: plugged:" + KeyguardIndicationController.-get15(KeyguardIndicationController.this) + ", charged:" + KeyguardIndicationController.-get14(KeyguardIndicationController.this) + ", level:" + KeyguardIndicationController.-get1(KeyguardIndicationController.this) + ", speed:" + KeyguardIndicationController.-get4(KeyguardIndicationController.this) + ", last speed:" + KeyguardIndicationController.-get10(KeyguardIndicationController.this) + ", visible:" + KeyguardIndicationController.-get19(KeyguardIndicationController.this));
        if (KeyguardIndicationController.-get11(KeyguardIndicationController.this) != null)
        {
          if (!KeyguardIndicationController.-get15(KeyguardIndicationController.this)) {
            break label340;
          }
          KeyguardIndicationController.-get11(KeyguardIndicationController.this).setVisibility(0);
          KeyguardIndicationController.-get11(KeyguardIndicationController.this).setText(String.valueOf(KeyguardIndicationController.-get1(KeyguardIndicationController.this)) + "%");
        }
      }
      for (;;)
      {
        KeyguardIndicationController.-wrap1(KeyguardIndicationController.this);
        if ((KeyguardIndicationController.-get7(KeyguardIndicationController.this) != null) && (KeyguardIndicationController.-get6(KeyguardIndicationController.this) != null)) {
          break label355;
        }
        Log.w("KeyguardIndication", "no dash view");
        return;
        bool = true;
        break;
        label330:
        bool = false;
        break;
        label335:
        bool = false;
        break label30;
        label340:
        KeyguardIndicationController.-get11(KeyguardIndicationController.this).setVisibility(8);
      }
      label355:
      if ((KeyguardIndicationController.-get15(KeyguardIndicationController.this)) && (KeyguardIndicationController.-get4(KeyguardIndicationController.this) == 2))
      {
        KeyguardIndicationController.-wrap0(KeyguardIndicationController.this);
        if (KeyguardIndicationController.-get10(KeyguardIndicationController.this) != KeyguardIndicationController.-get4(KeyguardIndicationController.this))
        {
          KeyguardIndicationController.-get7(KeyguardIndicationController.this).setImageResource(0);
          KeyguardIndicationController.-get6(KeyguardIndicationController.this).start();
          bool = KeyguardIndicationController.-get0(KeyguardIndicationController.this).isStreamMute(2);
          Log.d("KeyguardIndication", "play dash anim, " + bool);
          if ((!bool) && (KeyguardIndicationController.-get19(KeyguardIndicationController.this))) {
            KeyguardIndicationController.-get2(KeyguardIndicationController.this).play(KeyguardIndicationController.-get3(KeyguardIndicationController.this), 1.0F, 1.0F, 1, 0, 1.0F);
          }
        }
        for (;;)
        {
          KeyguardIndicationController.-get7(KeyguardIndicationController.this).setVisibility(0);
          return;
          KeyguardIndicationController.-get7(KeyguardIndicationController.this).setImageResource(2130837645);
        }
      }
      KeyguardIndicationController.-get7(KeyguardIndicationController.this).setVisibility(8);
    }
    
    public void onScreenTurnedOn()
    {
      if (KeyguardIndicationController.-get13(KeyguardIndicationController.this) != null)
      {
        int i = KeyguardIndicationController.-get5(KeyguardIndicationController.this).getResources().getColor(2131492997, null);
        KeyguardIndicationController.this.showTransientIndication(KeyguardIndicationController.-get13(KeyguardIndicationController.this), i);
        KeyguardIndicationController.-get9(KeyguardIndicationController.this).removeMessages(1);
        KeyguardIndicationController.this.hideTransientIndicationDelayed(5000L);
        KeyguardIndicationController.-set4(KeyguardIndicationController.this, null);
      }
    }
    
    public void onTimeChanged()
    {
      if (KeyguardIndicationController.-get19(KeyguardIndicationController.this)) {
        KeyguardIndicationController.-wrap1(KeyguardIndicationController.this);
      }
    }
    
    public void onUserUnlocked()
    {
      if (KeyguardIndicationController.-get19(KeyguardIndicationController.this)) {
        KeyguardIndicationController.-wrap1(KeyguardIndicationController.this);
      }
    }
  };
  private final UserManager mUserManager;
  private boolean mVisible;
  
  public KeyguardIndicationController(Context paramContext, KeyguardIndicationTextView paramKeyguardIndicationTextView, LockIcon paramLockIcon, ImageView paramImageView, TextView paramTextView, KeyguardBottomAreaView paramKeyguardBottomAreaView)
  {
    this.mContext = paramContext;
    this.mTextView = paramKeyguardIndicationTextView;
    this.mLockIcon = paramLockIcon;
    paramKeyguardIndicationTextView = paramContext.getResources();
    this.mSlowThreshold = paramKeyguardIndicationTextView.getInteger(2131624039);
    this.mFastThreshold = paramKeyguardIndicationTextView.getInteger(2131624040);
    this.mUserManager = ((UserManager)paramContext.getSystemService(UserManager.class));
    this.mBatteryInfo = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
    this.mDashView = paramImageView;
    this.mKeyguardBottomArea = paramKeyguardBottomAreaView;
    updateDashViews();
    this.mLevelView = paramTextView;
    this.mAudioManager = ((AudioManager)paramContext.getSystemService("audio"));
    this.mChargingSound = new SoundPool(1, 1, 0);
    this.mChargingSoundId = this.mChargingSound.load(paramContext, 2131361792, 1);
    KeyguardUpdateMonitor.getInstance(paramContext).registerCallback(this.mUpdateMonitor);
  }
  
  private String computePowerIndication()
  {
    if (this.mBatteryLevel >= 100) {
      return this.mContext.getResources().getString(2131690695);
    }
    int i;
    try
    {
      this.mBatteryInfo.computeChargeTimeRemaining();
      switch (this.mChargingSpeed)
      {
      case 1: 
      default: 
        if (0 != 0)
        {
          i = 2131690364;
          if (0 == 0) {
            break label152;
          }
          String str = Formatter.formatShortElapsedTimeRoundingUpToMinutes(this.mContext, 0L);
          return this.mContext.getResources().getString(i, new Object[] { str });
        }
        break;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e("KeyguardIndication", "Error calling IBatteryStats: ", localRemoteException);
        continue;
        i = 2131689930;
        continue;
        if (0 != 0)
        {
          i = 2131690366;
        }
        else
        {
          i = 2131690698;
          continue;
          i = 2131690696;
        }
      }
    }
    label152:
    return this.mContext.getResources().getString(i);
  }
  
  private void updateBottomMargins()
  {
    Object localObject = (TextView)this.mKeyguardBottomArea.getIndicationView();
    int i = 0;
    if (localObject != null) {
      i = ((TextView)localObject).getLineCount();
    }
    if ((this.mDashView.getParent() instanceof FrameLayout))
    {
      int j = 0;
      if (i > 1) {
        j = ((TextView)localObject).getLineHeight() * (i - 1);
      }
      localObject = (FrameLayout.LayoutParams)this.mDashView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).bottomMargin = (this.mContext.getResources().getDimensionPixelSize(2131755724) + j);
      if (Utils.DEBUG_ONEPLUS) {
        Log.d("KeyguardIndication", "update dashview, bottom:" + ((FrameLayout.LayoutParams)localObject).bottomMargin + ", " + i + ", " + j);
      }
    }
  }
  
  private void updateIndication()
  {
    int i;
    if (this.mVisible)
    {
      i = this.mStatusBarKeyguardViewManager.getSecurityPromptStringId();
      if (this.mUserManager.isUserUnlocked(ActivityManager.getCurrentUser())) {
        break label61;
      }
      this.mTextView.switchIndication(17040043);
      this.mTextView.setTextColor(-1);
    }
    for (;;)
    {
      if (this.mDashView.getVisibility() == 0) {
        updateBottomMargins();
      }
      return;
      label61:
      if (i != 0)
      {
        this.mTextView.switchIndication(i);
        this.mTextView.setTextColor(-1);
      }
      else if (!TextUtils.isEmpty(this.mTransientIndication))
      {
        this.mTextView.switchIndication(this.mTransientIndication);
        this.mTextView.setTextColor(this.mTransientTextColor);
      }
      else if (this.mPowerPluggedIn)
      {
        String str = computePowerIndication();
        this.mTextView.switchIndication(str);
        this.mTextView.setTextColor(-1);
      }
      else
      {
        this.mTextView.switchIndication(this.mRestingIndication);
        this.mTextView.setTextColor(-1);
      }
    }
  }
  
  public void hideTransientIndication()
  {
    if (this.mTransientIndication != null)
    {
      this.mTransientIndication = null;
      this.mHandler.removeMessages(1);
      updateIndication();
    }
  }
  
  public void hideTransientIndicationDelayed(long paramLong)
  {
    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1), paramLong);
  }
  
  public void notifyPreventModeChange(boolean paramBoolean)
  {
    Object localObject = KeyguardUpdateMonitor.getInstance(this.mContext);
    boolean bool1 = ((KeyguardUpdateMonitor)localObject).isFingerprintLockout();
    boolean bool2 = ((KeyguardUpdateMonitor)localObject).isUnlockingWithFingerprintAllowed();
    boolean bool3 = this.mStatusBarKeyguardViewManager.isBouncerShowing();
    boolean bool4 = ((KeyguardUpdateMonitor)localObject).isDeviceInteractive();
    if (Utils.DEBUG_ONEPLUS) {
      Log.d("KeyguardIndication", "notifyPreventModeChange, " + paramBoolean + ", " + this.mShowMsgWhenExiting + ", " + bool1 + ", " + bool2 + ", " + bool3 + ", " + bool4);
    }
    if (paramBoolean)
    {
      this.mShowMsgWhenExiting = true;
      return;
    }
    if ((this.mShowMsgWhenExiting) && (bool1))
    {
      localObject = this.mContext.getString(17039870);
      if (!bool2) {
        return;
      }
      int i = this.mContext.getResources().getColor(2131492997, null);
      if ((!bool3) && (bool4))
      {
        this.mLockIcon.setTransientFpError(false);
        showTransientIndication((String)localObject, i);
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        hideTransientIndicationDelayed(5000L);
      }
    }
    this.mShowMsgWhenExiting = false;
  }
  
  public void setStatusBarKeyguardViewManager(StatusBarKeyguardViewManager paramStatusBarKeyguardViewManager)
  {
    this.mStatusBarKeyguardViewManager = paramStatusBarKeyguardViewManager;
  }
  
  public void setVisible(boolean paramBoolean)
  {
    this.mVisible = paramBoolean;
    KeyguardIndicationTextView localKeyguardIndicationTextView = this.mTextView;
    if (paramBoolean) {}
    for (int i = 0;; i = 8)
    {
      localKeyguardIndicationTextView.setVisibility(i);
      if (paramBoolean)
      {
        hideTransientIndication();
        updateIndication();
      }
      return;
    }
  }
  
  public void showTransientIndication(int paramInt)
  {
    showTransientIndication(this.mContext.getResources().getString(paramInt));
  }
  
  public void showTransientIndication(String paramString)
  {
    showTransientIndication(paramString, -1);
  }
  
  public void showTransientIndication(String paramString, int paramInt)
  {
    this.mTransientIndication = paramString;
    this.mTransientTextColor = paramInt;
    this.mHandler.removeMessages(1);
    updateIndication();
  }
  
  public void updateDashViews()
  {
    this.mDashView.setImageResource(2130837645);
    this.mDashView.setBackground(this.mContext.getDrawable(2130837596));
    updateBottomMargins();
    this.mDashAnimation = ((AnimationDrawable)this.mDashView.getBackground());
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\KeyguardIndicationController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */