package com.android.systemui.usb;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.IUsbManager.Stub;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController.AlertParams;

public class UsbConfirmActivity
  extends AlertActivity
  implements DialogInterface.OnClickListener, CompoundButton.OnCheckedChangeListener
{
  private UsbAccessory mAccessory;
  private CheckBox mAlwaysUse;
  private TextView mClearDefaultHint;
  private UsbDevice mDevice;
  private UsbDisconnectedReceiver mDisconnectedReceiver;
  private ResolveInfo mResolveInfo;
  
  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    if (this.mClearDefaultHint == null) {
      return;
    }
    if (paramBoolean)
    {
      this.mClearDefaultHint.setVisibility(0);
      return;
    }
    this.mClearDefaultHint.setVisibility(8);
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (paramInt == -1) {}
    for (;;)
    {
      try
      {
        localIUsbManager = IUsbManager.Stub.asInterface(ServiceManager.getService("usb"));
        paramInt = this.mResolveInfo.activityInfo.applicationInfo.uid;
        i = UserHandle.myUserId();
        bool = this.mAlwaysUse.isChecked();
        paramDialogInterface = null;
        if (this.mDevice == null) {
          continue;
        }
        paramDialogInterface = new Intent("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        paramDialogInterface.putExtra("device", this.mDevice);
        localIUsbManager.grantDevicePermission(this.mDevice, paramInt);
        if (!bool) {
          continue;
        }
        localIUsbManager.setDevicePackage(this.mDevice, this.mResolveInfo.activityInfo.packageName, i);
      }
      catch (Exception paramDialogInterface)
      {
        IUsbManager localIUsbManager;
        int i;
        boolean bool;
        Log.e("UsbConfirmActivity", "Unable to start activity", paramDialogInterface);
        continue;
        if (this.mAccessory == null) {
          continue;
        }
        paramDialogInterface = new Intent("android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
        paramDialogInterface.putExtra("accessory", this.mAccessory);
        localIUsbManager.grantAccessoryPermission(this.mAccessory, paramInt);
        if (!bool) {
          continue;
        }
        localIUsbManager.setAccessoryPackage(this.mAccessory, this.mResolveInfo.activityInfo.packageName, i);
        continue;
        localIUsbManager.setAccessoryPackage(this.mAccessory, null, i);
        continue;
      }
      paramDialogInterface.addFlags(268435456);
      paramDialogInterface.setComponent(new ComponentName(this.mResolveInfo.activityInfo.packageName, this.mResolveInfo.activityInfo.name));
      startActivityAsUser(paramDialogInterface, new UserHandle(i));
      finish();
      return;
      localIUsbManager.setDevicePackage(this.mDevice, null, i);
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getIntent();
    this.mDevice = ((UsbDevice)paramBundle.getParcelableExtra("device"));
    this.mAccessory = ((UsbAccessory)paramBundle.getParcelableExtra("accessory"));
    this.mResolveInfo = ((ResolveInfo)paramBundle.getParcelableExtra("rinfo"));
    paramBundle = getPackageManager();
    String str = this.mResolveInfo.loadLabel(paramBundle).toString();
    AlertController.AlertParams localAlertParams = this.mAlertParams;
    localAlertParams.mIcon = this.mResolveInfo.loadIcon(paramBundle);
    localAlertParams.mTitle = str;
    if (this.mDevice == null)
    {
      localAlertParams.mMessage = getString(2131690058, new Object[] { str });
      this.mDisconnectedReceiver = new UsbDisconnectedReceiver(this, this.mAccessory);
      localAlertParams.mPositiveButtonText = getString(17039370);
      localAlertParams.mNegativeButtonText = getString(17039360);
      localAlertParams.mPositiveButtonListener = this;
      localAlertParams.mNegativeButtonListener = this;
      localAlertParams.mView = ((LayoutInflater)getSystemService("layout_inflater")).inflate(17367089, null);
      this.mAlwaysUse = ((CheckBox)localAlertParams.mView.findViewById(16909109));
      if (this.mDevice != null) {
        break label292;
      }
      this.mAlwaysUse.setText(2131690063);
    }
    for (;;)
    {
      this.mAlwaysUse.setOnCheckedChangeListener(this);
      this.mClearDefaultHint = ((TextView)localAlertParams.mView.findViewById(16909110));
      this.mClearDefaultHint.setVisibility(8);
      setupAlert();
      return;
      localAlertParams.mMessage = getString(2131690057, new Object[] { str });
      this.mDisconnectedReceiver = new UsbDisconnectedReceiver(this, this.mDevice);
      break;
      label292:
      this.mAlwaysUse.setText(2131690062);
    }
  }
  
  protected void onDestroy()
  {
    if (this.mDisconnectedReceiver != null) {
      unregisterReceiver(this.mDisconnectedReceiver);
    }
    super.onDestroy();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\usb\UsbConfirmActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */