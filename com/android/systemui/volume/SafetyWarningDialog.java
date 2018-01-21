package com.android.systemui.volume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import com.android.systemui.statusbar.phone.SystemUIDialog;

public abstract class SafetyWarningDialog
  extends SystemUIDialog
  implements DialogInterface.OnDismissListener, DialogInterface.OnClickListener
{
  private static final String TAG = Util.logTag(SafetyWarningDialog.class);
  private final AudioManager mAudioManager;
  private final Context mContext;
  private boolean mNewVolumeUp;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(paramAnonymousIntent.getAction()))
      {
        if (D.BUG) {
          Log.d(SafetyWarningDialog.-get0(), "Received ACTION_CLOSE_SYSTEM_DIALOGS");
        }
        SafetyWarningDialog.this.cancel();
        SafetyWarningDialog.this.cleanUp();
      }
    }
  };
  private long mShowTime;
  
  public SafetyWarningDialog(Context paramContext, AudioManager paramAudioManager)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mAudioManager = paramAudioManager;
    getWindow().setType(2010);
    setShowForAllUsers(true);
    setMessage(this.mContext.getString(17040706));
    setButton(-1, this.mContext.getString(17039379), this);
    setButton(-2, this.mContext.getString(17039369), (DialogInterface.OnClickListener)null);
    setOnDismissListener(this);
    paramAudioManager = new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS");
    paramContext.registerReceiver(this.mReceiver, paramAudioManager);
  }
  
  protected abstract void cleanUp();
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    this.mAudioManager.disableSafeMediaVolume();
  }
  
  public void onDismiss(DialogInterface paramDialogInterface)
  {
    this.mContext.unregisterReceiver(this.mReceiver);
    cleanUp();
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramInt == 24) && (paramKeyEvent.getRepeatCount() == 0)) {
      this.mNewVolumeUp = true;
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramInt == 24) && (this.mNewVolumeUp) && (System.currentTimeMillis() - this.mShowTime > 1000L))
    {
      if (D.BUG) {
        Log.d(TAG, "Confirmed warning via VOLUME_UP");
      }
      this.mAudioManager.disableSafeMediaVolume();
      dismiss();
    }
    return super.onKeyUp(paramInt, paramKeyEvent);
  }
  
  protected void onStart()
  {
    super.onStart();
    this.mShowTime = System.currentTimeMillis();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\volume\SafetyWarningDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */