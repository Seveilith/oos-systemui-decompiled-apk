package com.android.systemui.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionManager.OnSubscriptionsChangedListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.systemui.qs.QSPanel;
import com.android.systemui.statusbar.phone.QSTileHost;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.codeaurora.internal.IExtTelephony;
import org.codeaurora.internal.IExtTelephony.Stub;

public class SimSwitchController
  implements View.OnClickListener, View.OnLongClickListener
{
  private static final boolean DEBUG = Log.isLoggable("SimSwitchController", 3);
  private final Context mContext;
  private final SubscriptionManager.OnSubscriptionsChangedListener mOnSubscriptionsChangeListener = new SubscriptionManager.OnSubscriptionsChangedListener()
  {
    public void onSubscriptionsChanged()
    {
      SimSwitchController.-wrap1(SimSwitchController.this, "onSubscriptionsChanged:");
      SimSwitchController.-wrap2(SimSwitchController.this);
    }
  };
  private final int mPhoneCount = TelephonyManager.getDefault().getPhoneCount();
  private QSPanel mQSPanel = null;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      Log.d("SimSwitchController", "Intent received: " + paramAnonymousContext);
      if ("codeaurora.intent.action.DEFAULT_PHONE_ACCOUNT_CHANGED".equals(paramAnonymousContext))
      {
        int i = SimSwitchController.-wrap0(SimSwitchController.this);
        SimSwitchController.this.updateViews(i);
      }
    }
  };
  private ImageView mSimSlot1Icon;
  private ImageView mSimSlot2Icon;
  private final View mSimSwitcherView;
  private LinearLayout mSlot1Layout;
  private TextView mSlot1Name;
  private LinearLayout mSlot2Layout;
  private TextView mSlot2Name;
  private List<SubscriptionInfo> mSubInfoList = null;
  private SubscriptionManager mSubscriptionManager;
  private int[] mUiccProvisionStatus = new int[this.mPhoneCount];
  
  public SimSwitchController(Context paramContext, View paramView, QSPanel paramQSPanel)
  {
    this.mContext = paramContext;
    this.mSimSwitcherView = paramView;
    this.mQSPanel = paramQSPanel;
    this.mSlot1Name = ((TextView)this.mSimSwitcherView.findViewById(2131952254));
    this.mSlot2Name = ((TextView)this.mSimSwitcherView.findViewById(2131952258));
    this.mSimSlot1Icon = ((ImageView)this.mSimSwitcherView.findViewById(2131952252));
    this.mSimSlot2Icon = ((ImageView)this.mSimSwitcherView.findViewById(2131952256));
    this.mSlot1Layout = ((LinearLayout)this.mSimSwitcherView.findViewById(2131952251));
    this.mSlot2Layout = ((LinearLayout)this.mSimSwitcherView.findViewById(2131952255));
    this.mSlot1Layout.setOnClickListener(this);
    this.mSlot2Layout.setOnClickListener(this);
    this.mSlot1Layout.setOnLongClickListener(this);
    this.mSlot2Layout.setOnLongClickListener(this);
    this.mSubscriptionManager = SubscriptionManager.from(this.mContext);
    this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mOnSubscriptionsChangeListener);
    logd(" In constructor ");
    updateSubscriptions();
    paramContext = new IntentFilter("codeaurora.intent.action.DEFAULT_PHONE_ACCOUNT_CHANGED");
    this.mContext.registerReceiver(this.mReceiver, paramContext);
  }
  
  private int getProvisionCount()
  {
    int j = 0;
    int i = 0;
    while (i < this.mPhoneCount)
    {
      int k = j;
      if (isSubProvisioned(i)) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  private int getVoicePrefSlot()
  {
    Object localObject2 = TelecomManager.from(this.mContext);
    PhoneAccountHandle localPhoneAccountHandle = ((TelecomManager)localObject2).getUserSelectedOutgoingPhoneAccount();
    Object localObject1 = TelephonyManager.from(this.mContext);
    int k = -1;
    if (localPhoneAccountHandle == null)
    {
      logd("Get voice pref slotId " + -1);
      return -1;
    }
    localObject2 = ((TelecomManager)localObject2).getPhoneAccount(localPhoneAccountHandle);
    int j = k;
    int m;
    int n;
    int i;
    if (localObject2 != null)
    {
      j = k;
      if (this.mSubInfoList != null)
      {
        m = ((TelephonyManager)localObject1).getSubIdForPhoneAccount((PhoneAccount)localObject2);
        n = this.mSubInfoList.size();
        i = 0;
      }
    }
    for (;;)
    {
      j = k;
      if (i < n)
      {
        localObject1 = (SubscriptionInfo)this.mSubInfoList.get(i);
        if ((localObject1 != null) && (((SubscriptionInfo)localObject1).getSubscriptionId() == m)) {
          j = ((SubscriptionInfo)localObject1).getSimSlotIndex();
        }
      }
      else
      {
        logd("Get voice pref slotId " + j);
        return j;
      }
      i += 1;
    }
  }
  
  private boolean isSubProvisioned(int paramInt)
  {
    boolean bool = false;
    if (this.mUiccProvisionStatus[paramInt] == 1) {
      bool = true;
    }
    return bool;
  }
  
  private void logd(String paramString)
  {
    Log.d("SimSwitchController", paramString);
  }
  
  private void setVoicePref(int paramInt)
  {
    TelecomManager localTelecomManager = TelecomManager.from(this.mContext);
    logd("Setting voice pref slotId " + paramInt);
    int j;
    int i;
    if (this.mSubInfoList != null)
    {
      j = this.mSubInfoList.size();
      i = 0;
    }
    for (;;)
    {
      if (i < j)
      {
        SubscriptionInfo localSubscriptionInfo = (SubscriptionInfo)this.mSubInfoList.get(i);
        if ((localSubscriptionInfo != null) && (localSubscriptionInfo.getSimSlotIndex() == paramInt))
        {
          i = localSubscriptionInfo.getSubscriptionId();
          logd("Setting voice pref slotId " + paramInt + " subId " + i);
          localTelecomManager.setUserSelectedOutgoingPhoneAccount(subscriptionIdToPhoneAccountHandle(i));
        }
      }
      else
      {
        updateViews(paramInt);
        return;
      }
      i += 1;
    }
  }
  
  private PhoneAccountHandle subscriptionIdToPhoneAccountHandle(int paramInt)
  {
    TelecomManager localTelecomManager = TelecomManager.from(this.mContext);
    TelephonyManager localTelephonyManager = TelephonyManager.from(this.mContext);
    ListIterator localListIterator = localTelecomManager.getCallCapablePhoneAccounts().listIterator();
    while (localListIterator.hasNext())
    {
      PhoneAccountHandle localPhoneAccountHandle = (PhoneAccountHandle)localListIterator.next();
      if (paramInt == localTelephonyManager.getSubIdForPhoneAccount(localTelecomManager.getPhoneAccount(localPhoneAccountHandle))) {
        return localPhoneAccountHandle;
      }
    }
    return null;
  }
  
  private void updateSubscriptions()
  {
    int i = -1;
    IExtTelephony localIExtTelephony = IExtTelephony.Stub.asInterface(ServiceManager.getService("extphone"));
    this.mSubInfoList = this.mSubscriptionManager.getActiveSubscriptionInfoList();
    if (this.mSubInfoList != null)
    {
      Iterator localIterator = this.mSubInfoList.iterator();
      while (localIterator.hasNext())
      {
        SubscriptionInfo localSubscriptionInfo = (SubscriptionInfo)localIterator.next();
        i = localSubscriptionInfo.getSimSlotIndex();
        if (SubscriptionManager.isValidSlotId(i)) {
          try
          {
            this.mUiccProvisionStatus[i] = localIExtTelephony.getCurrentUiccCardProvisioningStatus(i);
          }
          catch (RemoteException localRemoteException)
          {
            logd("Activate  sub failed  phoneId " + localSubscriptionInfo.getSimSlotIndex());
          }
          catch (NullPointerException localNullPointerException)
          {
            logd("Failed to activate sub Exception: " + localNullPointerException);
          }
        }
      }
      i = getVoicePrefSlot();
    }
    updateViews(i);
  }
  
  public void onClick(View paramView)
  {
    int i = paramView.getId();
    logd(" OnClick slotId id = " + i);
    switch (i)
    {
    default: 
      Log.w("SimSwitchController", "Invalid switch case " + i);
      return;
    case 2131952251: 
      setVoicePref(0);
      return;
    }
    setVoicePref(1);
  }
  
  public boolean onLongClick(View paramView)
  {
    paramView = this.mQSPanel.getHost();
    logd("launching SimSettings activity " + paramView);
    if (paramView != null)
    {
      Intent localIntent = new Intent("com.android.settings.sim.SIM_SUB_INFO_SETTINGS");
      localIntent.setFlags(8388608);
      paramView.startActivityDismissingKeyguard(localIntent);
    }
    return true;
  }
  
  public void updateViews(int paramInt)
  {
    for (;;)
    {
      SubscriptionInfo localSubscriptionInfo;
      int i;
      try
      {
        logd("voice preferred slot " + paramInt);
        if ((this.mSubInfoList == null) || (this.mSubInfoList.size() <= 1))
        {
          logd("There are not subscription present or only one present ");
          this.mSimSwitcherView.setVisibility(8);
          return;
        }
        if (getProvisionCount() <= 1) {
          continue;
        }
        this.mSimSwitcherView.setVisibility(0);
        Iterator localIterator = this.mSubInfoList.iterator();
        if (!localIterator.hasNext()) {
          break label371;
        }
        localSubscriptionInfo = (SubscriptionInfo)localIterator.next();
        if (localSubscriptionInfo == null) {
          continue;
        }
        i = localSubscriptionInfo.getIconTint();
        switch (localSubscriptionInfo.getSimSlotIndex())
        {
        case 0: 
          logd("Update, slotId " + localSubscriptionInfo.getSimSlotIndex() + " display name " + localSubscriptionInfo.getDisplayName());
          continue;
          this.mSlot1Name.setText(localSubscriptionInfo.getDisplayName().toString());
        }
      }
      finally {}
      this.mSimSlot1Icon.setImageBitmap(localSubscriptionInfo.createIconBitmap(this.mContext));
      GradientDrawable localGradientDrawable = (GradientDrawable)this.mSlot1Layout.getBackground();
      if (paramInt == 0)
      {
        localGradientDrawable.setColor(Color.argb(180, Color.red(i), Color.green(i), Color.blue(i)));
      }
      else
      {
        localGradientDrawable.setColor(-7829368);
        continue;
        this.mSlot2Name.setText(localSubscriptionInfo.getDisplayName().toString());
        this.mSimSlot2Icon.setImageBitmap(localSubscriptionInfo.createIconBitmap(this.mContext));
        localGradientDrawable = (GradientDrawable)this.mSlot2Layout.getBackground();
        if (paramInt == 1)
        {
          localGradientDrawable.setColor(Color.argb(180, Color.red(i), Color.green(i), Color.blue(i)));
        }
        else
        {
          localGradientDrawable.setColor(-7829368);
          continue;
          label371:
          return;
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\settings\SimSwitchController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */