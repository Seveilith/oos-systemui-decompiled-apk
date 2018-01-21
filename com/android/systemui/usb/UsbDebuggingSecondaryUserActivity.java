package com.android.systemui.usb;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemProperties;
import com.android.internal.app.AlertActivity;

public class UsbDebuggingSecondaryUserActivity
  extends AlertActivity
  implements DialogInterface.OnClickListener
{
  private UsbDisconnectedReceiver mDisconnectedReceiver;
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    finish();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (SystemProperties.getInt("service.adb.tcp.port", 0) == 0) {
      this.mDisconnectedReceiver = new UsbDisconnectedReceiver(this);
    }
    paramBundle = this.mAlertParams;
    paramBundle.mTitle = getString(2131690067);
    paramBundle.mMessage = getString(2131690068);
    paramBundle.mPositiveButtonText = getString(17039370);
    paramBundle.mPositiveButtonListener = this;
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
      if (("android.hardware.usb.action.USB_STATE".equals(paramIntent.getAction())) && (!paramIntent.getBooleanExtra("connected", false))) {
        this.mActivity.finish();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\usb\UsbDebuggingSecondaryUserActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */