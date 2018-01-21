package com.android.systemui.usb;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.IUsbManager.Stub;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.RemoteException;
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

public class UsbPermissionActivity
  extends AlertActivity
  implements DialogInterface.OnClickListener, CompoundButton.OnCheckedChangeListener
{
  private UsbAccessory mAccessory;
  private CheckBox mAlwaysUse;
  private TextView mClearDefaultHint;
  private UsbDevice mDevice;
  private UsbDisconnectedReceiver mDisconnectedReceiver;
  private String mPackageName;
  private PendingIntent mPendingIntent;
  private boolean mPermissionGranted;
  private int mUid;
  
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
    if (paramInt == -1) {
      this.mPermissionGranted = true;
    }
    finish();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getIntent();
    this.mDevice = ((UsbDevice)paramBundle.getParcelableExtra("device"));
    this.mAccessory = ((UsbAccessory)paramBundle.getParcelableExtra("accessory"));
    this.mPendingIntent = ((PendingIntent)paramBundle.getParcelableExtra("android.intent.extra.INTENT"));
    this.mUid = paramBundle.getIntExtra("android.intent.extra.UID", -1);
    this.mPackageName = paramBundle.getStringExtra("package");
    paramBundle = getPackageManager();
    for (;;)
    {
      String str;
      AlertController.AlertParams localAlertParams;
      try
      {
        ApplicationInfo localApplicationInfo = paramBundle.getApplicationInfo(this.mPackageName, 0);
        str = localApplicationInfo.loadLabel(paramBundle).toString();
        localAlertParams = this.mAlertParams;
        localAlertParams.mIcon = localApplicationInfo.loadIcon(paramBundle);
        localAlertParams.mTitle = str;
        if (this.mDevice == null)
        {
          localAlertParams.mMessage = getString(2131690056, new Object[] { str });
          this.mDisconnectedReceiver = new UsbDisconnectedReceiver(this, this.mAccessory);
          localAlertParams.mPositiveButtonText = getString(17039370);
          localAlertParams.mNegativeButtonText = getString(17039360);
          localAlertParams.mPositiveButtonListener = this;
          localAlertParams.mNegativeButtonListener = this;
          localAlertParams.mView = ((LayoutInflater)getSystemService("layout_inflater")).inflate(17367089, null);
          this.mAlwaysUse = ((CheckBox)localAlertParams.mView.findViewById(16909109));
          if (this.mDevice != null) {
            break label339;
          }
          this.mAlwaysUse.setText(2131690063);
          this.mAlwaysUse.setOnCheckedChangeListener(this);
          this.mClearDefaultHint = ((TextView)localAlertParams.mView.findViewById(16909110));
          this.mClearDefaultHint.setVisibility(8);
          setupAlert();
          return;
        }
      }
      catch (PackageManager.NameNotFoundException paramBundle)
      {
        Log.e("UsbPermissionActivity", "unable to look up package name", paramBundle);
        finish();
        return;
      }
      localAlertParams.mMessage = getString(2131690055, new Object[] { str });
      this.mDisconnectedReceiver = new UsbDisconnectedReceiver(this, this.mDevice);
      continue;
      label339:
      this.mAlwaysUse.setText(2131690062);
    }
  }
  
  public void onDestroy()
  {
    IUsbManager localIUsbManager = IUsbManager.Stub.asInterface(ServiceManager.getService("usb"));
    Intent localIntent = new Intent();
    try
    {
      int i;
      if (this.mDevice != null)
      {
        localIntent.putExtra("device", this.mDevice);
        if (this.mPermissionGranted)
        {
          localIUsbManager.grantDevicePermission(this.mDevice, this.mUid);
          if (this.mAlwaysUse.isChecked())
          {
            i = UserHandle.getUserId(this.mUid);
            localIUsbManager.setDevicePackage(this.mDevice, this.mPackageName, i);
          }
        }
      }
      if (this.mAccessory != null)
      {
        localIntent.putExtra("accessory", this.mAccessory);
        if (this.mPermissionGranted)
        {
          localIUsbManager.grantAccessoryPermission(this.mAccessory, this.mUid);
          if (this.mAlwaysUse.isChecked())
          {
            i = UserHandle.getUserId(this.mUid);
            localIUsbManager.setAccessoryPackage(this.mAccessory, this.mPackageName, i);
          }
        }
      }
      localIntent.putExtra("permission", this.mPermissionGranted);
      this.mPendingIntent.send(this, 0, localIntent);
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e("UsbPermissionActivity", "IUsbService connection failed", localRemoteException);
      }
    }
    catch (PendingIntent.CanceledException localCanceledException)
    {
      for (;;)
      {
        Log.w("UsbPermissionActivity", "PendingIntent was cancelled");
      }
    }
    if (this.mDisconnectedReceiver != null) {
      unregisterReceiver(this.mDisconnectedReceiver);
    }
    super.onDestroy();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\usb\UsbPermissionActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */