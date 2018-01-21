package com.android.systemui.volume;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.VolumePolicy;
import android.os.Bundle;
import android.os.Handler;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.SystemUI;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.qs.tiles.DndTile;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class VolumeDialogComponent
  implements VolumeComponent, TunerService.Tunable
{
  private final Context mContext;
  private final VolumeDialogController mController;
  private final VolumeDialog mDialog;
  private final SystemUI mSysui;
  private final VolumeDialog.Callback mVolumeDialogCallback = new VolumeDialog.Callback()
  {
    public void onZenPrioritySettingsClicked()
    {
      VolumeDialogComponent.-wrap1(VolumeDialogComponent.this, ZenModePanel.ZEN_PRIORITY_SETTINGS);
    }
  };
  private VolumePolicy mVolumePolicy = new VolumePolicy(true, true, true, 400);
  private final ZenModeController mZenModeController;
  
  public VolumeDialogComponent(SystemUI paramSystemUI, Context paramContext, Handler paramHandler, ZenModeController paramZenModeController)
  {
    this.mSysui = paramSystemUI;
    this.mContext = paramContext;
    this.mController = new VolumeDialogController(paramContext, null)
    {
      protected void onUserActivityW()
      {
        VolumeDialogComponent.-wrap0(VolumeDialogComponent.this);
      }
    };
    this.mZenModeController = paramZenModeController;
    this.mDialog = new VolumeDialog(paramContext, 2020, this.mController, paramZenModeController, this.mVolumeDialogCallback, this.mSysui);
    applyConfiguration();
    TunerService.get(this.mContext).addTunable(this, new String[] { "sysui_volume_down_silent", "sysui_volume_up_silent", "sysui_do_not_disturb" });
  }
  
  private void applyConfiguration()
  {
    this.mDialog.setStreamImportant(4, true);
    this.mDialog.setStreamImportant(1, false);
    this.mDialog.setAutomute(true);
    this.mDialog.setSilentMode(false);
    this.mController.setVolumePolicy(this.mVolumePolicy);
    this.mController.showDndTile(true);
  }
  
  private void sendUserActivity()
  {
    KeyguardViewMediator localKeyguardViewMediator = (KeyguardViewMediator)this.mSysui.getComponent(KeyguardViewMediator.class);
    if (localKeyguardViewMediator != null) {
      localKeyguardViewMediator.userActivity();
    }
  }
  
  private void setVolumePolicy(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt)
  {
    this.mVolumePolicy = new VolumePolicy(paramBoolean1, paramBoolean2, paramBoolean3, paramInt);
    this.mController.setVolumePolicy(this.mVolumePolicy);
  }
  
  private void startSettings(Intent paramIntent)
  {
    ((PhoneStatusBar)this.mSysui.getComponent(PhoneStatusBar.class)).startActivityDismissingKeyguard(paramIntent, true, true);
  }
  
  public void dismissNow()
  {
    this.mController.dismiss();
  }
  
  public void dispatchDemoCommand(String paramString, Bundle paramBundle) {}
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    this.mController.dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    this.mDialog.dump(paramPrintWriter);
  }
  
  public ZenModeController getZenController()
  {
    return this.mZenModeController;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration) {}
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    boolean bool;
    if ("sysui_volume_down_silent".equals(paramString1)) {
      if (paramString2 != null) {
        if (Integer.parseInt(paramString2) != 0)
        {
          bool = true;
          setVolumePolicy(bool, this.mVolumePolicy.volumeUpToExitSilent, this.mVolumePolicy.doNotDisturbWhenSilent, this.mVolumePolicy.vibrateToSilentDebounce);
        }
      }
    }
    do
    {
      return;
      bool = false;
      break;
      bool = true;
      break;
      if ("sysui_volume_up_silent".equals(paramString1))
      {
        if (paramString2 != null) {
          if (Integer.parseInt(paramString2) != 0) {
            bool = true;
          }
        }
        for (;;)
        {
          setVolumePolicy(this.mVolumePolicy.volumeDownToEnterSilent, bool, this.mVolumePolicy.doNotDisturbWhenSilent, this.mVolumePolicy.vibrateToSilentDebounce);
          return;
          bool = false;
          continue;
          bool = true;
        }
      }
    } while (!"sysui_do_not_disturb".equals(paramString1));
    if (paramString2 != null) {
      if (Integer.parseInt(paramString2) != 0) {
        bool = true;
      }
    }
    for (;;)
    {
      setVolumePolicy(this.mVolumePolicy.volumeDownToEnterSilent, this.mVolumePolicy.volumeUpToExitSilent, bool, this.mVolumePolicy.vibrateToSilentDebounce);
      return;
      bool = false;
      continue;
      bool = true;
    }
  }
  
  public void register()
  {
    this.mController.register();
    DndTile.setCombinedIcon(this.mContext, true);
  }
  
  public void showVolumeDialogForTriKey()
  {
    if (!KeyguardUpdateMonitor.getInstance(this.mContext).isDreaming()) {
      this.mDialog.show(1);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\volume\VolumeDialogComponent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */