package com.android.systemui.keyboard;

import android.content.Context;
import android.view.Window;
import com.android.systemui.statusbar.phone.SystemUIDialog;

public class BluetoothDialog
  extends SystemUIDialog
{
  public BluetoothDialog(Context paramContext)
  {
    super(paramContext);
    getWindow().setType(2008);
    setShowForAllUsers(true);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\keyboard\BluetoothDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */