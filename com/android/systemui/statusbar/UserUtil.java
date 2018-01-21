package com.android.systemui.statusbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.UserSwitcherController;

public class UserUtil
{
  public static void deleteUserWithPrompt(Context paramContext, int paramInt, UserSwitcherController paramUserSwitcherController)
  {
    new RemoveUserDialog(paramContext, paramInt, paramUserSwitcherController).show();
  }
  
  private static final class RemoveUserDialog
    extends SystemUIDialog
    implements DialogInterface.OnClickListener
  {
    private final int mUserId;
    private final UserSwitcherController mUserSwitcherController;
    
    public RemoveUserDialog(Context paramContext, int paramInt, UserSwitcherController paramUserSwitcherController)
    {
      super();
      setTitle(2131690391);
      setMessage(paramContext.getString(2131690392));
      setButton(-2, paramContext.getString(17039360), this);
      setButton(-1, paramContext.getString(2131690393), this);
      setCanceledOnTouchOutside(false);
      this.mUserId = paramInt;
      this.mUserSwitcherController = paramUserSwitcherController;
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (paramInt == -2)
      {
        cancel();
        return;
      }
      dismiss();
      this.mUserSwitcherController.removeUserId(this.mUserId);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\UserUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */