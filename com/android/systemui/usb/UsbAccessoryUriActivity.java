package com.android.systemui.usb;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController.AlertParams;

public class UsbAccessoryUriActivity
  extends AlertActivity
  implements DialogInterface.OnClickListener
{
  private UsbAccessory mAccessory;
  private Uri mUri;
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (paramInt == -1)
    {
      paramDialogInterface = new Intent("android.intent.action.VIEW", this.mUri);
      paramDialogInterface.addCategory("android.intent.category.BROWSABLE");
      paramDialogInterface.addFlags(268435456);
    }
    try
    {
      startActivityAsUser(paramDialogInterface, UserHandle.CURRENT);
      finish();
      return;
    }
    catch (ActivityNotFoundException paramDialogInterface)
    {
      for (;;)
      {
        Log.e("UsbAccessoryUriActivity", "startActivity failed for " + this.mUri);
      }
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getIntent();
    this.mAccessory = ((UsbAccessory)paramBundle.getParcelableExtra("accessory"));
    String str = paramBundle.getStringExtra("uri");
    if (str == null) {}
    for (paramBundle = null;; paramBundle = Uri.parse(str))
    {
      this.mUri = paramBundle;
      if (this.mUri != null) {
        break;
      }
      Log.e("UsbAccessoryUriActivity", "could not parse Uri " + str);
      finish();
      return;
    }
    paramBundle = this.mUri.getScheme();
    if (("http".equals(paramBundle)) || ("https".equals(paramBundle)))
    {
      paramBundle = this.mAlertParams;
      paramBundle.mTitle = this.mAccessory.getDescription();
      if ((paramBundle.mTitle == null) || (paramBundle.mTitle.length() == 0)) {
        paramBundle.mTitle = getString(2131690060);
      }
      paramBundle.mMessage = getString(2131690059, new Object[] { this.mUri });
      paramBundle.mPositiveButtonText = getString(2131690061);
      paramBundle.mNegativeButtonText = getString(17039360);
      paramBundle.mPositiveButtonListener = this;
      paramBundle.mNegativeButtonListener = this;
      setupAlert();
      return;
    }
    Log.e("UsbAccessoryUriActivity", "Uri not http or https: " + this.mUri);
    finish();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\usb\UsbAccessoryUriActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */