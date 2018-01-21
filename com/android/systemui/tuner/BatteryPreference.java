package com.android.systemui.tuner;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings.System;
import android.support.v7.preference.DropDownPreference;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AttributeSet;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.statusbar.phone.StatusBarIconController;

public class BatteryPreference
  extends DropDownPreference
  implements TunerService.Tunable
{
  private final String mBattery;
  private boolean mBatteryEnabled;
  private ArraySet<String> mBlacklist;
  private boolean mHasPercentage;
  private boolean mHasSetValue;
  
  public BatteryPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mBattery = paramContext.getString(17039407);
    setEntryValues(new CharSequence[] { "percent", "default", "disabled" });
  }
  
  public void onAttached()
  {
    super.onAttached();
    if (Settings.System.getInt(getContext().getContentResolver(), "status_bar_show_battery_percent", 0) != 0) {}
    for (boolean bool = true;; bool = false)
    {
      this.mHasPercentage = bool;
      TunerService.get(getContext()).addTunable(this, new String[] { "icon_blacklist" });
      return;
    }
  }
  
  public void onDetached()
  {
    TunerService.get(getContext()).removeTunable(this);
    super.onDetached();
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    if ("icon_blacklist".equals(paramString1))
    {
      this.mBlacklist = StatusBarIconController.getIconBlacklist(paramString2);
      if (!this.mBlacklist.contains(this.mBattery)) {
        break label71;
      }
    }
    label71:
    for (boolean bool = false;; bool = true)
    {
      this.mBatteryEnabled = bool;
      if (!this.mHasSetValue)
      {
        this.mHasSetValue = true;
        if ((!this.mBatteryEnabled) || (!this.mHasPercentage)) {
          break;
        }
        setValue("percent");
      }
      return;
    }
    if (this.mBatteryEnabled)
    {
      setValue("default");
      return;
    }
    setValue("disabled");
  }
  
  protected boolean persistString(String paramString)
  {
    boolean bool = "percent".equals(paramString);
    MetricsLogger.action(getContext(), 237, bool);
    ContentResolver localContentResolver = getContext().getContentResolver();
    int i;
    if (bool)
    {
      i = 1;
      Settings.System.putInt(localContentResolver, "status_bar_show_battery_percent", i);
      if (!"disabled".equals(paramString)) {
        break label91;
      }
      this.mBlacklist.add(this.mBattery);
    }
    for (;;)
    {
      TunerService.get(getContext()).setValue("icon_blacklist", TextUtils.join(",", this.mBlacklist));
      return true;
      i = 0;
      break;
      label91:
      this.mBlacklist.remove(this.mBattery);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tuner\BatteryPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */