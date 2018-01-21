package com.android.keyguard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
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

public class KeyguardSimPukView
  extends KeyguardPinBasedInputView
{
  private CheckSimPuk mCheckSimPukThread;
  private String mPinText;
  private String mPukText;
  private int mRemainingAttempts = -1;
  private AlertDialog mRemainingAttemptsDialog;
  private boolean mShowDefaultMessage = true;
  private ImageView mSimImageView;
  private ProgressDialog mSimUnlockProgressDialog = null;
  private int mSlotId;
  private StateMachine mStateMachine = new StateMachine(null);
  private int mSubId = -1;
  KeyguardUpdateMonitorCallback mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback()
  {
    public void onSimStateChanged(int paramAnonymousInt1, int paramAnonymousInt2, IccCardConstants.State paramAnonymousState)
    {
      Log.v("KeyguardSimPukView", "onSimStateChanged(subId=" + paramAnonymousInt1 + ",slotId" + paramAnonymousInt2 + ",state=" + paramAnonymousState + ")");
      if (paramAnonymousInt1 != KeyguardSimPukView.-get6(KeyguardSimPukView.this))
      {
        if (paramAnonymousInt2 == KeyguardSimPukView.-get4(KeyguardSimPukView.this)) {
          KeyguardSimPukView.-set4(KeyguardSimPukView.this, true);
        }
      }
      else {
        switch (-getcom-android-internal-telephony-IccCardConstants$StateSwitchesValues()[paramAnonymousState.ordinal()])
        {
        }
      }
      for (;;)
      {
        KeyguardSimPukView.this.resetState();
        return;
        return;
        KeyguardSimPukView.-wrap4(KeyguardSimPukView.this);
      }
    }
  };
  
  public KeyguardSimPukView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public KeyguardSimPukView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private boolean checkPin()
  {
    int i = this.mPasswordEntry.getText().length();
    if ((i >= 4) && (i <= 8))
    {
      this.mPinText = this.mPasswordEntry.getText();
      return true;
    }
    return false;
  }
  
  private boolean checkPuk()
  {
    if (this.mPasswordEntry.getText().length() == 8)
    {
      this.mPukText = this.mPasswordEntry.getText();
      return true;
    }
    return false;
  }
  
  private void closeKeyGuard()
  {
    Log.d("KeyguardSimPukView", "closeKeyGuard");
    if (this.mCallback != null)
    {
      this.mCallback.userActivity();
      this.mCallback.reset();
    }
    this.mShowDefaultMessage = true;
  }
  
  private String getPukPasswordErrorMessage(int paramInt, boolean paramBoolean)
  {
    String str;
    if (paramInt == 0)
    {
      str = getContext().getString(R.string.kg_password_wrong_puk_code_dead);
      Log.d("KeyguardSimPukView", "getPukPasswordErrorMessage: attemptsRemaining=" + paramInt + " displayMessage=" + str);
      return str;
    }
    if (paramInt > 0)
    {
      if (paramBoolean) {}
      for (i = R.plurals.kg_password_default_puk_message;; i = R.plurals.kg_password_wrong_puk_code)
      {
        str = getContext().getResources().getQuantityString(i, paramInt, new Object[] { Integer.valueOf(paramInt) });
        break;
      }
    }
    if (paramBoolean) {}
    for (int i = R.string.kg_puk_enter_puk_hint;; i = R.string.kg_password_puk_failed)
    {
      str = getContext().getString(i);
      break;
    }
  }
  
  private Dialog getPukRemainingAttemptsDialog(int paramInt)
  {
    String str = getPukPasswordErrorMessage(paramInt, false);
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
      if (!(this.mContext instanceof Activity)) {
        this.mSimUnlockProgressDialog.getWindow().setType(2009);
      }
    }
    return this.mSimUnlockProgressDialog;
  }
  
  private void updateSim()
  {
    getSimUnlockProgressDialog().show();
    if (this.mCheckSimPukThread == null)
    {
      Log.d("KeyguardSimPukView", "begin verifyPasswordAndUnlock , mSubId = " + this.mSubId + ", slot=" + this.mSlotId);
      this.mCheckSimPukThread = new CheckSimPuk(this, this.mPukText, this.mPinText, this.mSubId)
      {
        void onSimLockChangedResponse(final int paramAnonymousInt1, final int paramAnonymousInt2)
        {
          jdField_this.post(new Runnable()
          {
            public void run()
            {
              if (KeyguardSimPukView.-get3(KeyguardSimPukView.2.this.this$0) != null) {
                KeyguardSimPukView.-get3(KeyguardSimPukView.2.this.this$0).hide();
              }
              KeyguardSimPukView localKeyguardSimPukView = KeyguardSimPukView.2.this.this$0;
              if (paramAnonymousInt1 != 0) {}
              for (boolean bool = true;; bool = false)
              {
                localKeyguardSimPukView.resetPasswordText(true, bool);
                if (paramAnonymousInt1 != 0) {
                  break;
                }
                KeyguardUpdateMonitor.getInstance(KeyguardSimPukView.2.this.this$0.getContext()).reportSimUnlocked(KeyguardSimPukView.-get6(KeyguardSimPukView.2.this.this$0));
                KeyguardSimPukView.-set3(KeyguardSimPukView.2.this.this$0, -1);
                KeyguardSimPukView.-set4(KeyguardSimPukView.2.this.this$0, true);
                if (KeyguardSimPukView.2.this.this$0.mCallback != null) {
                  KeyguardSimPukView.2.this.this$0.mCallback.dismiss(true);
                }
                KeyguardSimPukView.-set0(KeyguardSimPukView.2.this.this$0, null);
                return;
              }
              KeyguardSimPukView.-set4(KeyguardSimPukView.2.this.this$0, false);
              if (paramAnonymousInt1 == 1)
              {
                KeyguardSimPukView.2.this.this$0.mSecurityMessageDisplay.setMessage(KeyguardSimPukView.-wrap3(KeyguardSimPukView.2.this.this$0, paramAnonymousInt2, false), true);
                if (paramAnonymousInt2 <= 2) {
                  KeyguardSimPukView.-wrap0(KeyguardSimPukView.2.this.this$0, paramAnonymousInt2).show();
                }
              }
              for (;;)
              {
                Log.d("KeyguardSimPukView", "verifyPasswordAndUnlock  UpdateSim.onSimCheckResponse:  attemptsRemaining=" + paramAnonymousInt2);
                KeyguardSimPukView.-get5(KeyguardSimPukView.2.this.this$0).reset();
                break;
                KeyguardSimPukView.2.this.this$0.mSecurityMessageDisplay.setMessage(KeyguardSimPukView.2.this.this$0.getContext().getString(R.string.kg_password_puk_failed), true);
              }
            }
          });
        }
      };
      this.mCheckSimPukThread.start();
    }
  }
  
  public boolean confirmPin()
  {
    return this.mPinText.equals(this.mPasswordEntry.getText());
  }
  
  protected int getPasswordTextViewId()
  {
    return R.id.pukEntry;
  }
  
  protected int getPromtReasonStringRes(int paramInt)
  {
    return 0;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    KeyguardUpdateMonitor.getInstance(this.mContext).registerCallback(this.mUpdateMonitorCallback);
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
    this.mStateMachine.reset();
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
    this.mStateMachine.next();
  }
  
  private abstract class CheckSimPuk
    extends Thread
  {
    private final String mPin;
    private final String mPuk;
    private final int mSubId;
    
    protected CheckSimPuk(String paramString1, String paramString2, int paramInt)
    {
      this.mPuk = paramString1;
      this.mPin = paramString2;
      this.mSubId = paramInt;
    }
    
    abstract void onSimLockChangedResponse(int paramInt1, int paramInt2);
    
    public void run()
    {
      try
      {
        Log.v("KeyguardSimPukView", "call supplyPukReportResult() , mSubId = " + this.mSubId);
        final int[] arrayOfInt = ITelephony.Stub.asInterface(ServiceManager.checkService("phone")).supplyPukReportResultForSubscriber(this.mSubId, this.mPuk, this.mPin);
        Log.v("KeyguardSimPukView", "supplyPukReportResult returned: " + arrayOfInt[0] + " " + arrayOfInt[1]);
        KeyguardSimPukView.this.post(new Runnable()
        {
          public void run()
          {
            KeyguardSimPukView.CheckSimPuk.this.onSimLockChangedResponse(arrayOfInt[0], arrayOfInt[1]);
          }
        });
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("KeyguardSimPukView", "RemoteException for supplyPukReportResult:", localRemoteException);
        KeyguardSimPukView.this.post(new Runnable()
        {
          public void run()
          {
            KeyguardSimPukView.CheckSimPuk.this.onSimLockChangedResponse(2, -1);
          }
        });
      }
    }
  }
  
  private class StateMachine
  {
    final int CONFIRM_PIN = 2;
    final int DONE = 3;
    final int ENTER_PIN = 1;
    final int ENTER_PUK = 0;
    private int state = 0;
    
    private StateMachine() {}
    
    public void next()
    {
      int i = 0;
      if (this.state == 0) {
        if (KeyguardSimPukView.-wrap2(KeyguardSimPukView.this))
        {
          this.state = 1;
          i = R.string.kg_puk_enter_pin_hint;
        }
      }
      for (;;)
      {
        KeyguardSimPukView.this.resetPasswordText(true, true);
        if (i != 0) {
          KeyguardSimPukView.this.mSecurityMessageDisplay.setMessage(i, true);
        }
        return;
        i = R.string.kg_invalid_sim_puk_hint;
        continue;
        if (this.state == 1)
        {
          if (KeyguardSimPukView.-wrap1(KeyguardSimPukView.this))
          {
            this.state = 2;
            i = R.string.kg_enter_confirm_pin_hint;
          }
          else
          {
            i = R.string.kg_invalid_sim_pin_hint;
          }
        }
        else if (this.state == 2) {
          if (KeyguardSimPukView.this.confirmPin())
          {
            this.state = 3;
            i = R.string.keyguard_sim_unlock_progress_dialog_message;
            KeyguardSimPukView.-wrap5(KeyguardSimPukView.this);
          }
          else
          {
            this.state = 1;
            i = R.string.kg_invalid_confirm_pin_hint;
          }
        }
      }
    }
    
    void reset()
    {
      KeyguardSimPukView.-set1(KeyguardSimPukView.this, "");
      KeyguardSimPukView.-set2(KeyguardSimPukView.this, "");
      this.state = 0;
      Object localObject2 = KeyguardUpdateMonitor.getInstance(KeyguardSimPukView.-get0(KeyguardSimPukView.this));
      TextView localTextView = (TextView)KeyguardSimPukView.this.findViewById(R.id.slot_id_name);
      KeyguardSimPukView.-set6(KeyguardSimPukView.this, ((KeyguardUpdateMonitor)localObject2).getNextSubIdForState(IccCardConstants.State.PUK_REQUIRED));
      String str;
      Object localObject1;
      int i;
      if (SubscriptionManager.isValidSubscriptionId(KeyguardSimPukView.-get6(KeyguardSimPukView.this)))
      {
        str = KeyguardSimPukView.-get0(KeyguardSimPukView.this).getString(R.string.kg_slot_name, new Object[] { Integer.valueOf(SubscriptionManager.getSlotId(KeyguardSimPukView.-get6(KeyguardSimPukView.this)) + 1) });
        KeyguardSimPukView.-set5(KeyguardSimPukView.this, SubscriptionManager.getSlotId(KeyguardSimPukView.-get6(KeyguardSimPukView.this)));
        int j = TelephonyManager.getDefault().getSimCount();
        localObject1 = KeyguardSimPukView.this.getResources();
        i = -1;
        if (j < 2)
        {
          localObject1 = ((Resources)localObject1).getString(R.string.kg_puk_enter_puk_hint);
          if (KeyguardSimPukView.-get1(KeyguardSimPukView.this)) {
            KeyguardSimPukView.this.mSecurityMessageDisplay.setMessage((CharSequence)localObject1, true);
          }
          KeyguardSimPukView.-set4(KeyguardSimPukView.this, true);
          KeyguardSimPukView.-get2(KeyguardSimPukView.this).setImageTintList(ColorStateList.valueOf(i));
          localTextView.setText(str);
          localTextView.setVisibility(0);
        }
      }
      else
      {
        KeyguardSimPukView.this.mPasswordEntry.requestFocus();
        return;
      }
      SubscriptionInfo localSubscriptionInfo = ((KeyguardUpdateMonitor)localObject2).getSubscriptionInfoForSubId(KeyguardSimPukView.-get6(KeyguardSimPukView.this));
      if (localSubscriptionInfo != null) {
        localSubscriptionInfo.getDisplayName();
      }
      for (;;)
      {
        localObject2 = ((Resources)localObject1).getString(R.string.kg_puk_enter_puk_hint_multi, new Object[] { str });
        localObject1 = localObject2;
        if (localSubscriptionInfo == null) {
          break;
        }
        i = localSubscriptionInfo.getIconTint();
        localObject1 = localObject2;
        break;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\KeyguardSimPukView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */