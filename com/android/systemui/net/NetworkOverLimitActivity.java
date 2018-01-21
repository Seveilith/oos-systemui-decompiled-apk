package com.android.systemui.net;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.INetworkPolicyManager;
import android.net.INetworkPolicyManager.Stub;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.Window;
import com.android.systemui.util.Utils;

public class NetworkOverLimitActivity
  extends Activity
{
  private static int getLimitedDialogTitleForTemplate(NetworkTemplate paramNetworkTemplate)
  {
    switch (paramNetworkTemplate.getMatchRule())
    {
    default: 
      return 2131690243;
    case 2: 
      return 2131690240;
    case 3: 
      return 2131690241;
    }
    return 2131690242;
  }
  
  private void snoozePolicy(NetworkTemplate paramNetworkTemplate)
  {
    INetworkPolicyManager localINetworkPolicyManager = INetworkPolicyManager.Stub.asInterface(ServiceManager.getService("netpolicy"));
    try
    {
      localINetworkPolicyManager.snoozeLimit(paramNetworkTemplate);
      return;
    }
    catch (RemoteException paramNetworkTemplate)
    {
      Log.w("NetworkOverLimitActivity", "problem snoozing network policy", paramNetworkTemplate);
    }
  }
  
  public void onCreate(final Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (Utils.checkSystemDialogPermission(this))
    {
      paramBundle = (NetworkTemplate)getIntent().getParcelableExtra("android.net.NETWORK_TEMPLATE");
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
      localBuilder.setTitle(getLimitedDialogTitleForTemplate(paramBundle));
      localBuilder.setMessage(2131690244);
      localBuilder.setPositiveButton(17039370, null);
      localBuilder.setNegativeButton(2131690245, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          NetworkOverLimitActivity.-wrap0(NetworkOverLimitActivity.this, paramBundle);
        }
      });
      paramBundle = localBuilder.create();
      paramBundle.getWindow().setType(2003);
      paramBundle.setOnDismissListener(new DialogInterface.OnDismissListener()
      {
        public void onDismiss(DialogInterface paramAnonymousDialogInterface)
        {
          NetworkOverLimitActivity.this.finish();
        }
      });
      paramBundle.show();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\net\NetworkOverLimitActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */