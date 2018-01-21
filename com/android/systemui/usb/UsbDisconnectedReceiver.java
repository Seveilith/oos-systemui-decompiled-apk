package com.android.systemui.usb;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;

class UsbDisconnectedReceiver
  extends BroadcastReceiver
{
  private UsbAccessory mAccessory;
  private final Activity mActivity;
  private UsbDevice mDevice;
  
  public UsbDisconnectedReceiver(Activity paramActivity, UsbAccessory paramUsbAccessory)
  {
    this.mActivity = paramActivity;
    this.mAccessory = paramUsbAccessory;
    paramActivity.registerReceiver(this, new IntentFilter("android.hardware.usb.action.USB_ACCESSORY_DETACHED"));
  }
  
  public UsbDisconnectedReceiver(Activity paramActivity, UsbDevice paramUsbDevice)
  {
    this.mActivity = paramActivity;
    this.mDevice = paramUsbDevice;
    paramActivity.registerReceiver(this, new IntentFilter("android.hardware.usb.action.USB_DEVICE_DETACHED"));
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramContext = paramIntent.getAction();
    if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(paramContext))
    {
      paramContext = (UsbDevice)paramIntent.getParcelableExtra("device");
      if ((paramContext != null) && (paramContext.equals(this.mDevice))) {
        this.mActivity.finish();
      }
    }
    do
    {
      do
      {
        return;
      } while (!"android.hardware.usb.action.USB_ACCESSORY_DETACHED".equals(paramContext));
      paramContext = (UsbAccessory)paramIntent.getParcelableExtra("accessory");
    } while ((paramContext == null) || (!paramContext.equals(this.mAccessory)));
    this.mActivity.finish();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\usb\UsbDisconnectedReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */