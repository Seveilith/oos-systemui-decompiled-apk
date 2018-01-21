package com.android.systemui.media;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.projection.IMediaProjection;
import android.media.projection.IMediaProjectionManager;
import android.media.projection.IMediaProjectionManager.Stub;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.BidiFormatter;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.android.systemui.util.Utils;

public class MediaProjectionPermissionActivity
  extends Activity
  implements DialogInterface.OnClickListener, CompoundButton.OnCheckedChangeListener, DialogInterface.OnCancelListener
{
  private AlertDialog mDialog;
  private String mPackageName;
  private boolean mPermanentGrant;
  private IMediaProjectionManager mService;
  private int mUid;
  
  private Intent getMediaProjectionIntent(int paramInt, String paramString, boolean paramBoolean)
    throws RemoteException
  {
    paramString = this.mService.createProjection(paramInt, paramString, 0, paramBoolean);
    Intent localIntent = new Intent();
    localIntent.putExtra("android.media.projection.extra.EXTRA_MEDIA_PROJECTION", paramString.asBinder());
    return localIntent;
  }
  
  public void onCancel(DialogInterface paramDialogInterface)
  {
    finish();
  }
  
  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    this.mPermanentGrant = paramBoolean;
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (paramInt == -1) {}
    try
    {
      setResult(-1, getMediaProjectionIntent(this.mUid, this.mPackageName, this.mPermanentGrant));
      return;
    }
    catch (RemoteException paramDialogInterface)
    {
      Log.e("MediaProjectionPermissionActivity", "Error granting projection permission", paramDialogInterface);
      setResult(0);
      return;
    }
    finally
    {
      if (this.mDialog != null) {
        this.mDialog.dismiss();
      }
      finish();
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mPackageName = getCallingPackage();
    this.mService = IMediaProjectionManager.Stub.asInterface(ServiceManager.getService("media_projection"));
    if (this.mPackageName == null)
    {
      finish();
      return;
    }
    PackageManager localPackageManager = getPackageManager();
    ApplicationInfo localApplicationInfo;
    try
    {
      localApplicationInfo = localPackageManager.getApplicationInfo(this.mPackageName, 0);
      this.mUid = localApplicationInfo.uid;
      localObject2 = new TextPaint();
    }
    catch (PackageManager.NameNotFoundException paramBundle)
    {
      try
      {
        if (!this.mService.hasProjectionPermission(this.mUid, this.mPackageName)) {
          break label137;
        }
        setResult(-1, getMediaProjectionIntent(this.mUid, this.mPackageName, false));
        finish();
        return;
      }
      catch (RemoteException paramBundle)
      {
        Log.e("MediaProjectionPermissionActivity", "Error checking projection permissions", paramBundle);
        finish();
        return;
      }
      paramBundle = paramBundle;
      Log.e("MediaProjectionPermissionActivity", "unable to look up package name", paramBundle);
      finish();
      return;
    }
    label137:
    Object localObject2;
    ((TextPaint)localObject2).setTextSize(42.0F);
    Object localObject1 = localApplicationInfo.loadLabel(localPackageManager).toString();
    int j = ((String)localObject1).length();
    int i = 0;
    for (;;)
    {
      paramBundle = (Bundle)localObject1;
      int k;
      int m;
      if (i < j)
      {
        k = ((String)localObject1).codePointAt(i);
        m = Character.getType(k);
        if ((m != 13) && (m != 15)) {
          break label457;
        }
      }
      label457:
      while (m == 14)
      {
        paramBundle = ((String)localObject1).substring(0, i) + "…";
        localObject1 = paramBundle;
        if (paramBundle.isEmpty()) {
          localObject1 = this.mPackageName;
        }
        paramBundle = TextUtils.ellipsize((CharSequence)localObject1, (TextPaint)localObject2, 500.0F, TextUtils.TruncateAt.END).toString();
        paramBundle = BidiFormatter.getInstance().unicodeWrap(paramBundle);
        localObject1 = getString(2131690397, new Object[] { paramBundle });
        localObject2 = new SpannableString((CharSequence)localObject1);
        i = ((String)localObject1).indexOf(paramBundle);
        if (i >= 0) {
          ((SpannableString)localObject2).setSpan(new StyleSpan(1), i, paramBundle.length() + i, 0);
        }
        if (Utils.checkSystemDialogPermission(this))
        {
          this.mDialog = new AlertDialog.Builder(this).setIcon(localApplicationInfo.loadIcon(localPackageManager)).setMessage((CharSequence)localObject2).setPositiveButton(2131690400, this).setNegativeButton(17039360, this).setView(2130968801).setOnCancelListener(this).create();
          this.mDialog.create();
          this.mDialog.getButton(-1).setFilterTouchesWhenObscured(true);
          ((CheckBox)this.mDialog.findViewById(2131952211)).setOnCheckedChangeListener(this);
          this.mDialog.getWindow().setType(2003);
          this.mDialog.show();
        }
        return;
      }
      i += Character.charCount(k);
    }
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    if (this.mDialog != null) {
      this.mDialog.dismiss();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\media\MediaProjectionPermissionActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */