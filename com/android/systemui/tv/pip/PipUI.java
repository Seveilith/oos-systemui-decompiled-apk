package com.android.systemui.tv.pip;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import com.android.systemui.SystemUI;

public class PipUI
  extends SystemUI
{
  private boolean mSupportPip;
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (!this.mSupportPip) {
      return;
    }
    PipManager.getInstance().onConfigurationChanged();
  }
  
  public void start()
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    if (localPackageManager.hasSystemFeature("android.software.picture_in_picture")) {}
    for (boolean bool = localPackageManager.hasSystemFeature("android.software.leanback");; bool = false)
    {
      this.mSupportPip = bool;
      if (this.mSupportPip) {
        break;
      }
      return;
    }
    PipManager.getInstance().initialize(this.mContext);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tv\pip\PipUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */