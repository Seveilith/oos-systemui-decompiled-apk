package com.android.keyguard;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.ITelephony.Stub;
import com.android.internal.telephony.IccCardConstants.State;

public class KeyguardSimPinView
  extends KeyguardPinBasedInputView
{
  private CheckSimPin mCheckSimPinThread;
  private int mRemainingAttempts = -1;
  private AlertDialog mRemainingAttemptsDialog;
  private int mResult = 1;
  private int mRetryCount = 0;
  private boolean mShowDefaultMessage = true;
  private ImageView mSimImageView;
  private ProgressDialog mSimUnlockProgressDialog = null;
  private int mSlotId;
  private int mSubId = -1;
  KeyguardUpdateMonitorCallback mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback()
  {
    public void onSimStateChanged(int paramAnonymousInt1, int paramAnonymousInt2, IccCardConstants.State paramAnonymousState)
    {
      Log.v("KeyguardSimPinView", "onSimStateChanged(subId=" + paramAnonymousInt1 + ",slotId" + paramAnonymousInt2 + ",state=" + paramAnonymousState + ")");
      if (paramAnonymousInt1 != KeyguardSimPinView.-get3(KeyguardSimPinView.this))
      {
        if (paramAnonymousInt2 == KeyguardSimPinView.-get2(KeyguardSimPinView.this)) {
          KeyguardSimPinView.-set4(KeyguardSimPinView.this, true);
        }
      }
      else {
        switch (-getcom-android-internal-telephony-IccCardConstants$StateSwitchesValues()[paramAnonymousState.ordinal()])
        {
        }
      }
      for (;;)
      {
        KeyguardSimPinView.this.resetState();
        return;
        return;
        KeyguardSimPinView.-wrap2(KeyguardSimPinView.this);
        continue;
        KeyguardSimPinView.-set4(KeyguardSimPinView.this, true);
      }
    }
  };
  
  public KeyguardSimPinView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public KeyguardSimPinView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void closeKeyGuard()
  {
    Log.d("KeyguardSimPinView", "closeKeyGuard");
    this.mRemainingAttempts = -1;
    if (this.mCallback != null)
    {
      this.mCallback.userActivity();
      this.mCallback.reset();
    }
    this.mShowDefaultMessage = true;
    this.mRetryCount = 0;
  }
  
  private String getPinPasswordErrorMessage(int paramInt, boolean paramBoolean)
  {
    String str;
    if (paramInt == 0)
    {
      str = getContext().getString(R.string.kg_password_wrong_pin_code_pukked);
      Log.d("KeyguardSimPinView", "getPinPasswordErrorMessage: attemptsRemaining=" + paramInt + " displayMessage=" + str);
      return str;
    }
    if (paramInt > 0)
    {
      if (paramBoolean) {}
      for (i = R.plurals.kg_password_default_pin_message;; i = R.plurals.kg_password_wrong_pin_code)
      {
        str = getContext().getResources().getQuantityString(i, paramInt, new Object[] { Integer.valueOf(paramInt) });
        break;
      }
    }
    if (paramBoolean) {}
    for (int i = R.string.kg_sim_pin_instructions;; i = R.string.kg_password_pin_failed)
    {
      str = getContext().getString(i);
      break;
    }
  }
  
  private Dialog getSimRemainingAttemptsDialog(int paramInt)
  {
    String str = getPinPasswordErrorMessage(paramInt, false);
    if (this.mRemainingAttemptsDialog == null)
    {
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(this.mContext);
      localBuilder.setMessage(str);
      localBuilder.setCancelable(false);
      localBuilder.setNeutralButton(R.string.ok, null);
      this.mRemainingAttemptsDialog = localBuilder.create();
      this.mRemainingAttemptsDialog.getWindow().setType(2009);
    }
    for (;;)
    {
      return this.mRemainingAttemptsDialog;
      this.mRemainingAttemptsDialog.setMessage(str);
    }
  }
  
  private Dialog getSimUnlockProgressDialog()
  {
    if (this.mSimUnlockProgressDialog == null)
    {
      this.mSimUnlockProgressDialog = new ProgressDialog(this.mContext);
      this.mSimUnlockProgressDialog.setMessage(this.mContext.getString(R.string.kg_sim_unlock_progress_dialog_message));
      this.mSimUnlockProgressDialog.setIndeterminate(true);
      this.mSimUnlockProgressDialog.setCancelable(false);
      this.mSimUnlockProgressDialog.getWindow().setType(2009);
    }
    return this.mSimUnlockProgressDialog;
  }
  
  private void handleSubInfoChangeIfNeeded()
  {
    int i = KeyguardUpdateMonitor.getInstance(this.mContext).getNextSubIdForState(IccCardConstants.State.PIN_REQUIRED);
    if ((i != this.mSubId) && (SubscriptionManager.isValidSubscriptionId(i)))
    {
      this.mSubId = i;
      this.mShowDefaultMessage = true;
      this.mRemainingAttempts = -1;
      Log.d("KeyguardSimPinView", "subinfo change subId to " + i);
    }
  }
  
  private void showDefaultMessage(boolean paramBoolean)
  {
    Object localObject1 = KeyguardUpdateMonitor.getInstance(this.mContext);
    if (!((KeyguardUpdateMonitor)localObject1).isSimPinSecure())
    {
      Log.d("KeyguardSimPinView", "return when no simpin");
      return;
    }
    int i = ((KeyguardUpdateMonitor)localObject1).getNextSubIdForState(IccCardConstants.State.PIN_REQUIRED);
    boolean bool = paramBoolean;
    if (this.mSubId != i)
    {
      bool = paramBoolean;
      if (SubscriptionManager.isValidSubscriptionId(i))
      {
        bool = true;
        this.mSubId = i;
        Log.d("KeyguardSimPinView", "change subId to " + i);
      }
    }
    TextView localTextView = (TextView)findViewById(R.id.slot_id_name);
    if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
      return;
    }
    Object localObject2;
    if ((this.mRemainingAttempts < 0) || (bool))
    {
      this.mSlotId = SubscriptionManager.getSlotId(this.mSubId);
      if (this.mSlotId == -1)
      {
        Log.w("KeyguardSimPinView", "get invalid slot index");
        this.mSlotId = KeyguardUpdateMonitor.getInstance(this.mContext).getSimSlotId(this.mSubId);
      }
      int j = TelephonyManager.getDefault().getSimCount();
      localObject2 = getResources();
      i = -1;
      if (j < 2)
      {
        localObject1 = ((Resources)localObject2).getString(R.string.kg_sim_pin_instructions);
        localTextView.setText(this.mContext.getString(R.string.kg_slot_name, new Object[] { Integer.valueOf(this.mSlotId + 1) }));
        localTextView.setVisibility(0);
        this.mSecurityMessageDisplay.setMessage((CharSequence)localObject1, true);
        this.mSimImageView.setImageTintList(ColorStateList.valueOf(i));
        Log.d("KeyguardSimPinView", "mSubId=" + this.mSubId + " , slot=" + this.mSlotId);
        new CheckSimPin(this, "", this.mSubId)
        {
          void onSimCheckResponse(int paramAnonymousInt1, int paramAnonymousInt2)
          {
            if ((paramAnonymousInt1 == 2) && (paramAnonymousInt2 == 0) && (KeyguardSimPinView.-get0(jdField_this) <= 10))
            {
              Log.w("KeyguardSimPinView", "PIN_GENERAL_FAILURE, retry again, " + KeyguardSimPinView.-get0(jdField_this));
              KeyguardSimPinView localKeyguardSimPinView = jdField_this;
              KeyguardSimPinView.-set3(localKeyguardSimPinView, KeyguardSimPinView.-get0(localKeyguardSimPinView) + 1);
              jdField_this.postDelayed(new Runnable()
              {
                public void run()
                {
                  KeyguardSimPinView.-wrap3(KeyguardSimPinView.3.this.this$0, true);
                }
              }, 100L);
            }
            Log.d("KeyguardSimPinView", "onSimCheckResponse  dummy One result" + paramAnonymousInt1 + " attemptsRemaining=" + paramAnonymousInt2);
            if (paramAnonymousInt2 >= 0)
            {
              KeyguardSimPinView.-set1(jdField_this, paramAnonymousInt2);
              KeyguardSimPinView.-set2(jdField_this, paramAnonymousInt1);
              jdField_this.mSecurityMessageDisplay.setMessage(KeyguardSimPinView.-wrap1(jdField_this, paramAnonymousInt2, true), true);
            }
          }
        }.start();
      }
    }
    else
    {
      this.mSecurityMessageDisplay.setMessage(getPinPasswordErrorMessage(this.mRemainingAttempts, true), true);
      return;
    }
    SubscriptionInfo localSubscriptionInfo = KeyguardUpdateMonitor.getInstance(this.mContext).getSubscriptionInfoForSubId(this.mSubId);
    if (localSubscriptionInfo != null) {}
    for (localObject1 = localSubscriptionInfo.getDisplayName();; localObject1 = "")
    {
      localObject2 = ((Resources)localObject2).getString(R.string.kg_sim_pin_instructions_multi, new Object[] { localObject1 });
      localObject1 = localObject2;
      if (localSubscriptionInfo == null) {
        break;
      }
      i = localSubscriptionInfo.getIconTint();
      localObject1 = localObject2;
      break;
    }
  }
  
  protected int getPasswordTextViewId()
  {
    return R.id.simPinEntry;
  }
  
  protected int getPromtReasonStringRes(int paramInt)
  {
    return 0;
  }
  
  protected void onAttachedToWindow()
  {
    this.mRetryCount = 0;
    super.onAttachedToWindow();
    if (this.mShowDefaultMessage) {
      showDefaultMessage(false);
    }
    KeyguardUpdateMonitor.getInstance(this.mContext).registerCallback(this.mUpdateMonitorCallback);
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    resetState();
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    KeyguardUpdateMonitor.getInstance(this.mContext).removeCallback(this.mUpdateMonitorCallback);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mSecurityMessageDisplay.setTimeout(0);
    if ((this.mEcaView instanceof EmergencyCarrierArea)) {
      ((EmergencyCarrierArea)this.mEcaView).setCarrierTextVisible(true);
    }
    this.mSimImageView = ((ImageView)findViewById(R.id.keyguard_sim));
  }
  
  public void onPause()
  {
    if (this.mSimUnlockProgressDialog != null)
    {
      this.mSimUnlockProgressDialog.dismiss();
      this.mSimUnlockProgressDialog = null;
    }
  }
  
  public void resetState()
  {
    super.resetState();
    Log.v("KeyguardSimPinView", "Resetting state, default=" + this.mShowDefaultMessage);
    handleSubInfoChangeIfNeeded();
    if (this.mShowDefaultMessage)
    {
      this.mRetryCount = 0;
      showDefaultMessage(false);
    }
  }
  
  protected boolean shouldLockout(long paramLong)
  {
    return false;
  }
  
  public void startAppearAnimation() {}
  
  public boolean startDisappearAnimation(Runnable paramRunnable)
  {
    return false;
  }
  
  protected void verifyPasswordAndUnlock()
  {
    if (this.mPasswordEntry.getText().length() < 4)
    {
      this.mSecurityMessageDisplay.setMessage(R.string.kg_invalid_sim_pin_hint, true);
      resetPasswordText(true, true);
      if (this.mCallback != null) {
        this.mCallback.userActivity();
      }
      return;
    }
    getSimUnlockProgressDialog().show();
    if (this.mCheckSimPinThread == null)
    {
      Log.d("KeyguardSimPinView", "begin verifyPasswordAndUnlock");
      this.mCheckSimPinThread = new CheckSimPin(this, this.mPasswordEntry.getText(), this.mSubId)
      {
        void onSimCheckResponse(final int paramAnonymousInt1, final int paramAnonymousInt2)
        {
          jdField_this.post(new Runnable()
          {
            public void run()
            {
              KeyguardSimPinView.-set1(KeyguardSimPinView.2.this.this$0, paramAnonymousInt2);
              KeyguardSimPinView.-set2(KeyguardSimPinView.2.this.this$0, paramAnonymousInt1);
              if (KeyguardSimPinView.-get1(KeyguardSimPinView.2.this.this$0) != null) {
                KeyguardSimPinView.-get1(KeyguardSimPinView.2.this.this$0).hide();
              }
              KeyguardSimPinView localKeyguardSimPinView = KeyguardSimPinView.2.this.this$0;
              if (paramAnonymousInt1 != 0) {}
              for (boolean bool = true;; bool = false)
              {
                localKeyguardSimPinView.resetPasswordText(true, bool);
                if (paramAnonymousInt1 != 0) {
                  break;
                }
                KeyguardSimPinView.-set2(KeyguardSimPinView.2.this.this$0, 1);
                KeyguardSimPinView.-set1(KeyguardSimPinView.2.this.this$0, -1);
                KeyguardSimPinView.-set3(KeyguardSimPinView.2.this.this$0, 0);
                KeyguardSimPinView.-set4(KeyguardSimPinView.2.this.this$0, true);
                KeyguardUpdateMonitor.getInstance(KeyguardSimPinView.2.this.this$0.getContext()).reportSimUnlocked(KeyguardSimPinView.-get3(KeyguardSimPinView.2.this.this$0));
                if (KeyguardSimPinView.2.this.this$0.mCallback != null) {
                  KeyguardSimPinView.2.this.this$0.mCallback.dismiss(true);
                }
                if (KeyguardSimPinView.2.this.this$0.mCallback != null) {
                  KeyguardSimPinView.2.this.this$0.mCallback.userActivity();
                }
                KeyguardSimPinView.-set0(KeyguardSimPinView.2.this.this$0, null);
                return;
              }
              KeyguardSimPinView.-set4(KeyguardSimPinView.2.this.this$0, false);
              if (paramAnonymousInt1 == 1)
              {
                KeyguardSimPinView.2.this.this$0.mSecurityMessageDisplay.setMessage(KeyguardSimPinView.-wrap1(KeyguardSimPinView.2.this.this$0, paramAnonymousInt2, false), true);
                if (paramAnonymousInt2 <= 2) {
                  KeyguardSimPinView.-wrap0(KeyguardSimPinView.2.this.this$0, paramAnonymousInt2).show();
                }
              }
              for (;;)
              {
                Log.d("KeyguardSimPinView", "verifyPasswordAndUnlock  CheckSimPin.onSimCheckResponse: " + paramAnonymousInt1 + " attemptsRemaining=" + paramAnonymousInt2);
                break;
                KeyguardSimPinView.2.this.this$0.mSecurityMessageDisplay.setMessage(KeyguardSimPinView.2.this.this$0.getContext().getString(R.string.kg_password_pin_failed), true);
              }
            }
          });
        }
      };
      this.mCheckSimPinThread.start();
    }
  }
  
  private abstract class CheckSimPin
    extends Thread
  {
    private final String mPin;
    private int mSubId;
    
    protected CheckSimPin(String paramString, int paramInt)
    {
      this.mPin = paramString;
      this.mSubId = paramInt;
    }
    
    abstract void onSimCheckResponse(int paramInt1, int paramInt2);
    
    public void run()
    {
      try
      {
        Log.v("KeyguardSimPinView", "call supplyPinReportResultForSubscriber(subid=" + this.mSubId + ")");
        final int[] arrayOfInt = ITelephony.Stub.asInterface(ServiceManager.checkService("phone")).supplyPinReportResultForSubscriber(this.mSubId, this.mPin);
        Log.v("KeyguardSimPinView", "supplyPinReportResult returned: " + arrayOfInt[0] + " " + arrayOfInt[1]);
        KeyguardSimPinView.this.post(new Runnable()
        {
          public void run()
          {
            KeyguardSimPinView.CheckSimPin.this.onSimCheckResponse(arrayOfInt[0], arrayOfInt[1]);
          }
        });
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("KeyguardSimPinView", "RemoteException for supplyPinReportResult:", localRemoteException);
        KeyguardSimPinView.this.post(new Runnable()
        {
          public void run()
          {
            KeyguardSimPinView.CheckSimPin.this.onSimCheckResponse(2, -1);
          }
        });
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\KeyguardSimPinView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */