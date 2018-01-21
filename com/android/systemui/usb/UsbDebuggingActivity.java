package com.android.systemui.usb;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.IUsbManager.Stub;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController.AlertParams;

public class UsbDebuggingActivity
  extends AlertActivity
  implements DialogInterface.OnClickListener
{
  private CheckBox mAlwaysAllow;
  private UsbDisconnectedReceiver mDisconnectedReceiver;
  private String mKey;
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    boolean bool;
    if (paramInt == -1)
    {
      paramInt = 1;
      if (paramInt == 0) {
        break label53;
      }
      bool = this.mAlwaysAllow.isChecked();
    }
    for (;;)
    {
      try
      {
        paramDialogInterface = IUsbManager.Stub.asInterface(ServiceManager.getService("usb"));
        if (paramInt == 0) {
          continue;
        }
        paramDialogInterface.allowUsbDebugging(bool, this.mKey);
      }
      catch (Exception paramDialogInterface)
      {
        label53:
        Log.e("UsbDebuggingActivity", "Unable to notify Usb service", paramDialogInterface);
        continue;
      }
      finish();
      return;
      paramInt = 0;
      break;
      bool = false;
      continue;
      paramDialogInterface.denyUsbDebugging();
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (SystemProperties.getInt("service.adb.tcp.port", 0) == 0) {
      this.mDisconnectedReceiver = new UsbDisconnectedReceiver(this);
    }
    Object localObject = getIntent();
    paramBundle = ((Intent)localObject).getStringExtra("fingerprints");
    this.mKey = ((Intent)localObject).getStringExtra("key");
    if ((paramBundle == null) || (this.mKey == null))
    {
      finish();
      return;
    }
    localObject = this.mAlertParams;
    ((AlertController.AlertParams)localObject).mTitle = getString(2131690064);
    ((AlertController.AlertParams)localObject).mMessage = getString(2131690065, new Object[] { paramBundle });
    ((AlertController.AlertParams)localObject).mPositiveButtonText = getString(17039370);
    ((AlertController.AlertParams)localObject).mNegativeButtonText = getString(17039360);
    ((AlertController.AlertParams)localObject).mPositiveButtonListener = this;
    ((AlertController.AlertParams)localObject).mNegativeButtonListener = this;
    paramBundle = LayoutInflater.from(((AlertController.AlertParams)localObject).mContext).inflate(17367089, null);
    this.mAlwaysAllow = ((CheckBox)paramBundle.findViewById(16909109));
    this.mAlwaysAllow.setText(getString(2131690066));
    ((AlertController.AlertParams)localObject).mView = paramBundle;
    setupAlert();
  }
  
  public void onStart()
  {
    super.onStart();
    IntentFilter localIntentFilter = new IntentFilter("android.hardware.usb.action.USB_STATE");
    registerReceiver(this.mDisconnectedReceiver, localIntentFilter);
  }
  
  protected void onStop()
  {
    if (this.mDisconnectedReceiver != null) {
      unregisterReceiver(this.mDisconnectedReceiver);
    }
    super.onStop();
  }
  
  private class UsbDisconnectedReceiver
    extends BroadcastReceiver
  {
    private final Activity mActivity;
    
    public UsbDisconnectedReceiver(Activity paramActivity)
    {
      this.mActivity = paramActivity;
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (!"android.hardware.usb.action.USB_STATE".equals(paramIntent.getAction())) {
        return;
      }
      if (!paramIntent.getBooleanExtra("connected", false)) {
        this.mActivity.finish();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\usb\UsbDebuggingActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */