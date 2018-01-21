package com.android.systemui.usb;

import android.content.Intent;
import android.content.res.Resources;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import com.android.internal.app.ResolverActivity;
import java.util.List;

public class UsbResolverActivity
  extends ResolverActivity
{
  private UsbAccessory mAccessory;
  private UsbDevice mDevice;
  private UsbDisconnectedReceiver mDisconnectedReceiver;
  
  protected void onCreate(Bundle paramBundle)
  {
    Object localObject2 = getIntent();
    Object localObject1 = ((Intent)localObject2).getParcelableExtra("android.intent.extra.INTENT");
    if (!(localObject1 instanceof Intent))
    {
      Log.w("UsbResolverActivity", "Target is not an intent: " + localObject1);
      finish();
      return;
    }
    localObject1 = (Intent)localObject1;
    localObject2 = ((Intent)localObject2).getParcelableArrayListExtra("rlist");
    super.onCreate(paramBundle, (Intent)localObject1, getResources().getText(17040290), null, (List)localObject2, true);
    paramBundle = (CheckBox)findViewById(16909109);
    if (paramBundle != null)
    {
      if (this.mDevice != null) {
        break label143;
      }
      paramBundle.setText(2131690063);
    }
    for (;;)
    {
      this.mDevice = ((UsbDevice)((Intent)localObject1).getParcelableExtra("device"));
      if (this.mDevice == null) {
        break;
      }
      this.mDisconnectedReceiver = new UsbDisconnectedReceiver(this, this.mDevice);
      return;
      label143:
      paramBundle.setText(2131690062);
    }
    this.mAccessory = ((UsbAccessory)((Intent)localObject1).getParcelableExtra("accessory"));
    if (this.mAccessory == null)
    {
      Log.e("UsbResolverActivity", "no device or accessory");
      finish();
      return;
    }
    this.mDisconnectedReceiver = new UsbDisconnectedReceiver(this, this.mAccessory);
  }
  
  protected void onDestroy()
  {
    if (this.mDisconnectedReceiver != null) {
      unregisterReceiver(this.mDisconnectedReceiver);
    }
    super.onDestroy();
  }
  
  /* Error */
  protected boolean onTargetSelected(com.android.internal.app.ResolverActivity.TargetInfo paramTargetInfo, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokeinterface 135 1 0
    //   6: astore 5
    //   8: ldc -119
    //   10: invokestatic 143	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   13: invokestatic 149	android/hardware/usb/IUsbManager$Stub:asInterface	(Landroid/os/IBinder;)Landroid/hardware/usb/IUsbManager;
    //   16: astore 6
    //   18: aload 5
    //   20: getfield 155	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   23: getfield 161	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   26: getfield 167	android/content/pm/ApplicationInfo:uid	I
    //   29: istore_3
    //   30: invokestatic 173	android/os/UserHandle:myUserId	()I
    //   33: istore 4
    //   35: aload_0
    //   36: getfield 85	com/android/systemui/usb/UsbResolverActivity:mDevice	Landroid/hardware/usb/UsbDevice;
    //   39: ifnull +90 -> 129
    //   42: aload 6
    //   44: aload_0
    //   45: getfield 85	com/android/systemui/usb/UsbResolverActivity:mDevice	Landroid/hardware/usb/UsbDevice;
    //   48: iload_3
    //   49: invokeinterface 179 3 0
    //   54: iload_2
    //   55: ifeq +44 -> 99
    //   58: aload 6
    //   60: aload_0
    //   61: getfield 85	com/android/systemui/usb/UsbResolverActivity:mDevice	Landroid/hardware/usb/UsbDevice;
    //   64: aload 5
    //   66: getfield 155	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   69: getfield 183	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   72: iload 4
    //   74: invokeinterface 187 4 0
    //   79: aload_1
    //   80: aload_0
    //   81: aconst_null
    //   82: new 169	android/os/UserHandle
    //   85: dup
    //   86: iload 4
    //   88: invokespecial 189	android/os/UserHandle:<init>	(I)V
    //   91: invokeinterface 193 4 0
    //   96: pop
    //   97: iconst_1
    //   98: ireturn
    //   99: aload 6
    //   101: aload_0
    //   102: getfield 85	com/android/systemui/usb/UsbResolverActivity:mDevice	Landroid/hardware/usb/UsbDevice;
    //   105: aconst_null
    //   106: iload 4
    //   108: invokeinterface 187 4 0
    //   113: goto -34 -> 79
    //   116: astore_1
    //   117: ldc 31
    //   119: ldc -61
    //   121: aload_1
    //   122: invokestatic 198	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   125: pop
    //   126: goto -29 -> 97
    //   129: aload_0
    //   130: getfield 108	com/android/systemui/usb/UsbResolverActivity:mAccessory	Landroid/hardware/usb/UsbAccessory;
    //   133: ifnull -54 -> 79
    //   136: aload 6
    //   138: aload_0
    //   139: getfield 108	com/android/systemui/usb/UsbResolverActivity:mAccessory	Landroid/hardware/usb/UsbAccessory;
    //   142: iload_3
    //   143: invokeinterface 202 3 0
    //   148: iload_2
    //   149: ifeq +27 -> 176
    //   152: aload 6
    //   154: aload_0
    //   155: getfield 108	com/android/systemui/usb/UsbResolverActivity:mAccessory	Landroid/hardware/usb/UsbAccessory;
    //   158: aload 5
    //   160: getfield 155	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   163: getfield 183	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   166: iload 4
    //   168: invokeinterface 206 4 0
    //   173: goto -94 -> 79
    //   176: aload 6
    //   178: aload_0
    //   179: getfield 108	com/android/systemui/usb/UsbResolverActivity:mAccessory	Landroid/hardware/usb/UsbAccessory;
    //   182: aconst_null
    //   183: iload 4
    //   185: invokeinterface 206 4 0
    //   190: goto -111 -> 79
    //   193: astore_1
    //   194: ldc 31
    //   196: ldc -48
    //   198: aload_1
    //   199: invokestatic 198	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   202: pop
    //   203: goto -106 -> 97
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	206	0	this	UsbResolverActivity
    //   0	206	1	paramTargetInfo	com.android.internal.app.ResolverActivity.TargetInfo
    //   0	206	2	paramBoolean	boolean
    //   29	114	3	i	int
    //   33	151	4	j	int
    //   6	153	5	localResolveInfo	android.content.pm.ResolveInfo
    //   16	161	6	localIUsbManager	android.hardware.usb.IUsbManager
    // Exception table:
    //   from	to	target	type
    //   8	54	116	android/os/RemoteException
    //   58	79	116	android/os/RemoteException
    //   79	97	116	android/os/RemoteException
    //   99	113	116	android/os/RemoteException
    //   129	148	116	android/os/RemoteException
    //   152	173	116	android/os/RemoteException
    //   176	190	116	android/os/RemoteException
    //   194	203	116	android/os/RemoteException
    //   79	97	193	android/content/ActivityNotFoundException
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\usb\UsbResolverActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */