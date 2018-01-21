package com.android.keyguard;

import android.app.ActivityManagerNative;
import android.app.ActivityOptions;
import android.app.IActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.telecom.TelecomManager;
import android.telephony.ServiceState;
import android.util.AttributeSet;
import android.util.Slog;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.policy.EmergencyAffordanceManager;
import com.android.internal.telephony.IccCardConstants.State;
import com.android.internal.widget.LockPatternUtils;

public class EmergencyButton
  extends Button
{
  private static final Intent INTENT_EMERGENCY_DIAL = new Intent().setAction("com.android.phone.EmergencyDialer.DIAL").setPackage("com.android.phone").setFlags(343932928);
  private int mDownX;
  private int mDownY;
  private final EmergencyAffordanceManager mEmergencyAffordanceManager;
  private EmergencyButtonCallback mEmergencyButtonCallback;
  private final boolean mEnableEmergencyCallWhileSimLocked;
  KeyguardUpdateMonitorCallback mInfoCallback = new KeyguardUpdateMonitorCallback()
  {
    public void onPhoneStateChanged(int paramAnonymousInt)
    {
      EmergencyButton.this.updateEmergencyCallButton();
    }
    
    public void onServiceStateChanged(int paramAnonymousInt, ServiceState paramAnonymousServiceState)
    {
      EmergencyButton.this.updateEmergencyCallButton();
    }
    
    public void onSimStateChanged(int paramAnonymousInt1, int paramAnonymousInt2, IccCardConstants.State paramAnonymousState)
    {
      EmergencyButton.this.updateEmergencyCallButton();
    }
  };
  private final boolean mIsCarrierSupported = isCarrierOneSupported();
  private final boolean mIsVoiceCapable;
  private LockPatternUtils mLockPatternUtils;
  private boolean mLongPressWasDragged;
  private PowerManager mPowerManager;
  
  public EmergencyButton(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public EmergencyButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mIsVoiceCapable = paramContext.getResources().getBoolean(17956957);
    this.mEnableEmergencyCallWhileSimLocked = this.mContext.getResources().getBoolean(17956941);
    this.mEmergencyAffordanceManager = new EmergencyAffordanceManager(paramContext);
  }
  
  private TelecomManager getTelecommManager()
  {
    return (TelecomManager)this.mContext.getSystemService("telecom");
  }
  
  public static boolean isCarrierOneSupported()
  {
    return "405854".equals(SystemProperties.get("persist.radio.atel.carrier"));
  }
  
  private boolean isInCall()
  {
    return getTelecommManager().isInCall();
  }
  
  private void resumeCall()
  {
    getTelecommManager().showInCallScreen(false);
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    KeyguardUpdateMonitor.getInstance(this.mContext).registerCallback(this.mInfoCallback);
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    updateEmergencyCallButton();
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    KeyguardUpdateMonitor.getInstance(this.mContext).removeCallback(this.mInfoCallback);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mLockPatternUtils = new LockPatternUtils(this.mContext);
    this.mPowerManager = ((PowerManager)this.mContext.getSystemService("power"));
    setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if ((!KeyguardUpdateMonitor.getInstance(EmergencyButton.-get0(EmergencyButton.this)).isKeyguardVisible()) && (EmergencyButton.-get2(EmergencyButton.this) != null)) {
          EmergencyButton.-get2(EmergencyButton.this).onEmergencyButtonClickedWhenInCall();
        }
        EmergencyButton.this.takeEmergencyCallAction();
      }
    });
    setOnLongClickListener(new View.OnLongClickListener()
    {
      public boolean onLongClick(View paramAnonymousView)
      {
        if ((!EmergencyButton.-get3(EmergencyButton.this)) && (EmergencyButton.-get1(EmergencyButton.this).needsEmergencyAffordance()))
        {
          EmergencyButton.-get1(EmergencyButton.this).performEmergencyCall();
          return true;
        }
        return false;
      }
    });
    updateEmergencyCallButton();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    if (paramMotionEvent.getActionMasked() == 0)
    {
      this.mDownX = i;
      this.mDownY = j;
    }
    for (this.mLongPressWasDragged = false;; this.mLongPressWasDragged = true)
    {
      int k;
      do
      {
        return super.onTouchEvent(paramMotionEvent);
        i = Math.abs(i - this.mDownX);
        j = Math.abs(j - this.mDownY);
        k = ViewConfiguration.get(this.mContext).getScaledTouchSlop();
      } while ((Math.abs(j) <= k) && (Math.abs(i) <= k));
    }
  }
  
  public boolean performLongClick()
  {
    return super.performLongClick();
  }
  
  public void setCallback(EmergencyButtonCallback paramEmergencyButtonCallback)
  {
    this.mEmergencyButtonCallback = paramEmergencyButtonCallback;
  }
  
  public void takeEmergencyCallAction()
  {
    MetricsLogger.action(this.mContext, 200);
    this.mPowerManager.userActivity(SystemClock.uptimeMillis(), true);
    try
    {
      ActivityManagerNative.getDefault().stopSystemLockTaskMode();
      if (isInCall())
      {
        resumeCall();
        if (this.mEmergencyButtonCallback != null) {
          this.mEmergencyButtonCallback.onEmergencyButtonClickedWhenInCall();
        }
        return;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.w("EmergencyButton", "Failed to stop app pinning");
      }
      KeyguardUpdateMonitor.getInstance(this.mContext).reportEmergencyCallAction(true);
      getContext().startActivityAsUser(INTENT_EMERGENCY_DIAL, ActivityOptions.makeCustomAnimation(getContext(), 0, 0).toBundle(), new UserHandle(KeyguardUpdateMonitor.getCurrentUser()));
    }
  }
  
  public void updateEmergencyCallButton()
  {
    int j = 0;
    int i;
    if (this.mIsVoiceCapable)
    {
      if (isInCall()) {
        j = 1;
      }
    }
    else
    {
      if (j == 0) {
        break label167;
      }
      setVisibility(0);
      if (!isInCall()) {
        break label146;
      }
      i = 17040039;
    }
    for (;;)
    {
      setText(i);
      return;
      boolean bool;
      if (KeyguardUpdateMonitor.getInstance(this.mContext).isSimPinVoiceSecure()) {
        bool = this.mEnableEmergencyCallWhileSimLocked;
      }
      for (;;)
      {
        j = bool;
        if (!this.mContext.getResources().getBoolean(R.bool.kg_hide_emgcy_btn_when_oos)) {
          break;
        }
        KeyguardUpdateMonitor localKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
        if ((bool) && (!localKeyguardUpdateMonitor.isOOS())) {
          break label141;
        }
        j = 0;
        break;
        if (!this.mLockPatternUtils.isSecure(KeyguardUpdateMonitor.getCurrentUser())) {
          bool = this.mContext.getResources().getBoolean(R.bool.config_showEmergencyButton);
        } else {
          bool = true;
        }
      }
      label141:
      j = 1;
      break;
      label146:
      if (this.mIsCarrierSupported) {
        i = R.string.button_lockscreen_emergency_call;
      } else {
        i = 17040038;
      }
    }
    label167:
    setVisibility(8);
  }
  
  public static abstract interface EmergencyButtonCallback
  {
    public abstract void onEmergencyButtonClickedWhenInCall();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\EmergencyButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */