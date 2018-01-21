package com.android.systemui;

import android.content.Context;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

public class SysUIToast
{
  public static Toast makeText(Context paramContext, CharSequence paramCharSequence, int paramInt)
  {
    paramContext = Toast.makeText(paramContext, paramCharSequence, paramInt);
    paramCharSequence = paramContext.getWindowParams();
    paramCharSequence.privateFlags |= 0x10;
    paramContext.getWindowParams().type = 2006;
    return paramContext;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\SysUIToast.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */