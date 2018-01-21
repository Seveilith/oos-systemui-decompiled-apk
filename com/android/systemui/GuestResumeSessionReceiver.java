package com.android.systemui;

import android.app.ActivityManagerNative;
import android.app.Dialog;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.System;
import android.util.Log;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.android.systemui.statusbar.phone.SystemUIDialog;

public class GuestResumeSessionReceiver
  extends BroadcastReceiver
{
  private Dialog mNewSessionDialog;
  
  private void cancelDialog()
  {
    if ((this.mNewSessionDialog != null) && (this.mNewSessionDialog.isShowing()))
    {
      this.mNewSessionDialog.cancel();
      this.mNewSessionDialog = null;
    }
  }
  
  private static void wipeGuestSession(Context paramContext, int paramInt)
  {
    UserManager localUserManager = (UserManager)paramContext.getSystemService("user");
    UserInfo localUserInfo;
    try
    {
      localUserInfo = ActivityManagerNative.getDefault().getCurrentUser();
      if (localUserInfo.id != paramInt)
      {
        Log.w("GuestResumeSessionReceiver", "User requesting to start a new session (" + paramInt + ")" + " is not current user (" + localUserInfo.id + ")");
        return;
      }
    }
    catch (RemoteException paramContext)
    {
      Log.e("GuestResumeSessionReceiver", "Couldn't wipe session because ActivityManager is dead");
      return;
    }
    if (!localUserInfo.isGuest())
    {
      Log.w("GuestResumeSessionReceiver", "User requesting to start a new session (" + paramInt + ")" + " is not a guest");
      return;
    }
    if (!localUserManager.markGuestForDeletion(localUserInfo.id))
    {
      Log.w("GuestResumeSessionReceiver", "Couldn't mark the guest for deletion for user " + paramInt);
      return;
    }
    paramContext = localUserManager.createGuest(paramContext, localUserInfo.name);
    if (paramContext == null) {}
    try
    {
      Log.e("GuestResumeSessionReceiver", "Could not create new guest, switching back to system user");
      ActivityManagerNative.getDefault().switchUser(0);
      localUserManager.removeUser(localUserInfo.id);
      WindowManagerGlobal.getWindowManagerService().lockNow(null);
      return;
    }
    catch (RemoteException paramContext)
    {
      Log.e("GuestResumeSessionReceiver", "Couldn't wipe session because ActivityManager or WindowManager is dead");
    }
    ActivityManagerNative.getDefault().switchUser(paramContext.id);
    localUserManager.removeUser(localUserInfo.id);
    return;
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    int i;
    if ("android.intent.action.USER_SWITCHED".equals(paramIntent.getAction()))
    {
      cancelDialog();
      i = paramIntent.getIntExtra("android.intent.extra.user_handle", 55536);
      if (i == 55536)
      {
        Log.e("GuestResumeSessionReceiver", paramIntent + " sent to " + "GuestResumeSessionReceiver" + " without EXTRA_USER_HANDLE");
        return;
      }
      try
      {
        paramIntent = ActivityManagerNative.getDefault().getCurrentUser();
        if (!paramIntent.isGuest()) {
          return;
        }
      }
      catch (RemoteException paramContext)
      {
        return;
      }
      paramIntent = paramContext.getContentResolver();
      if (Settings.System.getIntForUser(paramIntent, "systemui.guest_has_logged_in", 0, i) != 0)
      {
        this.mNewSessionDialog = new ResetSessionDialog(paramContext, i);
        this.mNewSessionDialog.show();
      }
    }
    else
    {
      return;
    }
    Settings.System.putIntForUser(paramIntent, "systemui.guest_has_logged_in", 1, i);
  }
  
  public void register(Context paramContext)
  {
    IntentFilter localIntentFilter = new IntentFilter("android.intent.action.USER_SWITCHED");
    paramContext.registerReceiverAsUser(this, UserHandle.SYSTEM, localIntentFilter, null, null);
  }
  
  private static class ResetSessionDialog
    extends SystemUIDialog
    implements DialogInterface.OnClickListener
  {
    private final int mUserId;
    
    public ResetSessionDialog(Context paramContext, int paramInt)
    {
      super();
      setTitle(paramContext.getString(2131690379));
      setMessage(paramContext.getString(2131690380));
      setCanceledOnTouchOutside(false);
      setButton(-2, paramContext.getString(2131690381), this);
      setButton(-1, paramContext.getString(2131690382), this);
      this.mUserId = paramInt;
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (paramInt == -2)
      {
        GuestResumeSessionReceiver.-wrap0(getContext(), this.mUserId);
        dismiss();
      }
      while (paramInt != -1) {
        return;
      }
      cancel();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\GuestResumeSessionReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */