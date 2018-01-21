package com.android.systemui.statusbar.phone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

public class SystemUIDialog
  extends AlertDialog
{
  private final Context mContext;
  
  public SystemUIDialog(Context paramContext)
  {
    this(paramContext, 2131821083);
  }
  
  public SystemUIDialog(Context paramContext, int paramInt)
  {
    super(paramContext, paramInt);
    this.mContext = paramContext;
    applyFlags(this);
    paramContext = getWindow().getAttributes();
    paramContext.setTitle(getClass().getSimpleName());
    getWindow().setAttributes(paramContext);
  }
  
  public static void applyFlags(AlertDialog paramAlertDialog)
  {
    paramAlertDialog.getWindow().setType(2014);
    paramAlertDialog.getWindow().addFlags(655360);
  }
  
  public static void setShowForAllUsers(AlertDialog paramAlertDialog, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramAlertDialog = paramAlertDialog.getWindow().getAttributes();
      paramAlertDialog.privateFlags |= 0x10;
      return;
    }
    paramAlertDialog = paramAlertDialog.getWindow().getAttributes();
    paramAlertDialog.privateFlags &= 0xFFFFFFEF;
  }
  
  public void setMessage(int paramInt)
  {
    setMessage(this.mContext.getString(paramInt));
  }
  
  public void setNegativeButton(int paramInt, DialogInterface.OnClickListener paramOnClickListener)
  {
    setButton(-2, this.mContext.getString(paramInt), paramOnClickListener);
  }
  
  public void setPositiveButton(int paramInt, DialogInterface.OnClickListener paramOnClickListener)
  {
    setButton(-1, this.mContext.getString(paramInt), paramOnClickListener);
  }
  
  public void setShowForAllUsers(boolean paramBoolean)
  {
    setShowForAllUsers(this, paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\SystemUIDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */