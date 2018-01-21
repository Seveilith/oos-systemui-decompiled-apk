package com.android.systemui.tuner;

import android.content.Context;
import android.support.v7.preference.DropDownPreference;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AttributeSet;
import com.android.systemui.statusbar.phone.StatusBarIconController;

public class ClockPreference
  extends DropDownPreference
  implements TunerService.Tunable
{
  private ArraySet<String> mBlacklist;
  private final String mClock;
  private boolean mClockEnabled;
  private boolean mHasSeconds;
  private boolean mHasSetValue;
  private boolean mReceivedClock;
  private boolean mReceivedSeconds;
  
  public ClockPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mClock = paramContext.getString(17039410);
    setEntryValues(new CharSequence[] { "seconds", "default", "disabled" });
  }
  
  public void onAttached()
  {
    super.onAttached();
    TunerService.get(getContext()).addTunable(this, new String[] { "icon_blacklist", "clock_seconds" });
  }
  
  public void onDetached()
  {
    TunerService.get(getContext()).removeTunable(this);
    super.onDetached();
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    boolean bool2 = false;
    boolean bool1 = false;
    if ("icon_blacklist".equals(paramString1))
    {
      this.mReceivedClock = true;
      this.mBlacklist = StatusBarIconController.getIconBlacklist(paramString2);
      if (this.mBlacklist.contains(this.mClock)) {
        this.mClockEnabled = bool1;
      }
    }
    for (;;)
    {
      if ((!this.mHasSetValue) && (this.mReceivedClock) && (this.mReceivedSeconds))
      {
        this.mHasSetValue = true;
        if ((!this.mClockEnabled) || (!this.mHasSeconds)) {
          break label139;
        }
        setValue("seconds");
      }
      return;
      bool1 = true;
      break;
      if ("clock_seconds".equals(paramString1))
      {
        this.mReceivedSeconds = true;
        bool1 = bool2;
        if (paramString2 != null)
        {
          bool1 = bool2;
          if (Integer.parseInt(paramString2) != 0) {
            bool1 = true;
          }
        }
        this.mHasSeconds = bool1;
      }
    }
    label139:
    if (this.mClockEnabled)
    {
      setValue("default");
      return;
    }
    setValue("disabled");
  }
  
  protected boolean persistString(String paramString)
  {
    TunerService localTunerService = TunerService.get(getContext());
    int i;
    if ("seconds".equals(paramString))
    {
      i = 1;
      localTunerService.setValue("clock_seconds", i);
      if (!"disabled".equals(paramString)) {
        break label75;
      }
      this.mBlacklist.add(this.mClock);
    }
    for (;;)
    {
      TunerService.get(getContext()).setValue("icon_blacklist", TextUtils.join(",", this.mBlacklist));
      return true;
      i = 0;
      break;
      label75:
      this.mBlacklist.remove(this.mClock);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tuner\ClockPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */