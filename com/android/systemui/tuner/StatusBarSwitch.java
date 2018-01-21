package com.android.systemui.tuner;

import android.app.ActivityManager;
import android.content.Context;
import android.provider.Settings.Secure;
import android.support.v14.preference.SwitchPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import java.util.Set;

public class StatusBarSwitch
  extends SwitchPreference
  implements TunerService.Tunable
{
  private Set<String> mBlacklist;
  
  public StatusBarSwitch(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void setList(Set<String> paramSet)
  {
    Settings.Secure.putStringForUser(getContext().getContentResolver(), "icon_blacklist", TextUtils.join(",", paramSet), ActivityManager.getCurrentUser());
  }
  
  public void onAttached()
  {
    super.onAttached();
    TunerService.get(getContext()).addTunable(this, new String[] { "icon_blacklist" });
  }
  
  public void onDetached()
  {
    TunerService.get(getContext()).removeTunable(this);
    super.onDetached();
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    if (!"icon_blacklist".equals(paramString1)) {
      return;
    }
    this.mBlacklist = StatusBarIconController.getIconBlacklist(paramString2);
    if (this.mBlacklist.contains(getKey())) {}
    for (boolean bool = false;; bool = true)
    {
      setChecked(bool);
      return;
    }
  }
  
  protected boolean persistBoolean(boolean paramBoolean)
  {
    if (!paramBoolean) {
      if (!this.mBlacklist.contains(getKey()))
      {
        MetricsLogger.action(getContext(), 234, getKey());
        this.mBlacklist.add(getKey());
        setList(this.mBlacklist);
      }
    }
    for (;;)
    {
      return true;
      if (this.mBlacklist.remove(getKey()))
      {
        MetricsLogger.action(getContext(), 233, getKey());
        setList(this.mBlacklist);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tuner\StatusBarSwitch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */