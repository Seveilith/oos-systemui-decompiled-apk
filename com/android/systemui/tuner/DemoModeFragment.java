package com.android.systemui.tuner;

import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Global;
import android.support.v14.preference.PreferenceFragment;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.view.MenuItem;
import com.android.internal.logging.MetricsLogger;

public class DemoModeFragment
  extends PreferenceFragment
  implements Preference.OnPreferenceChangeListener
{
  private static final String[] STATUS_ICONS = { "volume", "bluetooth", "location", "alarm", "zen", "sync", "tty", "eri", "mute", "speakerphone", "managed_profile" };
  private final ContentObserver mDemoModeObserver = new ContentObserver(new Handler(Looper.getMainLooper()))
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      DemoModeFragment.-wrap0(DemoModeFragment.this);
      DemoModeFragment.-wrap1(DemoModeFragment.this);
    }
  };
  private SwitchPreference mEnabledSwitch;
  private SwitchPreference mOnSwitch;
  
  private void setGlobal(String paramString, int paramInt)
  {
    Settings.Global.putInt(getContext().getContentResolver(), paramString, paramInt);
  }
  
  private void startDemoMode()
  {
    Intent localIntent = new Intent("com.android.systemui.demo");
    localIntent.putExtra("command", "enter");
    getContext().sendBroadcast(localIntent);
    localIntent.putExtra("command", "clock");
    localIntent.putExtra("hhmm", "0700");
    getContext().sendBroadcast(localIntent);
    localIntent.putExtra("command", "network");
    localIntent.putExtra("wifi", "show");
    localIntent.putExtra("mobile", "show");
    localIntent.putExtra("sims", "1");
    localIntent.putExtra("nosim", "false");
    localIntent.putExtra("level", "4");
    localIntent.putExtra("datatypel", "");
    getContext().sendBroadcast(localIntent);
    localIntent.putExtra("fully", "true");
    getContext().sendBroadcast(localIntent);
    localIntent.putExtra("command", "battery");
    localIntent.putExtra("level", "100");
    localIntent.putExtra("plugged", "false");
    getContext().sendBroadcast(localIntent);
    localIntent.putExtra("command", "status");
    String[] arrayOfString = STATUS_ICONS;
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      localIntent.putExtra(arrayOfString[i], "hide");
      i += 1;
    }
    getContext().sendBroadcast(localIntent);
    localIntent.putExtra("command", "notifications");
    localIntent.putExtra("visible", "false");
    getContext().sendBroadcast(localIntent);
    setGlobal("sysui_tuner_demo_on", 1);
  }
  
  private void stopDemoMode()
  {
    Intent localIntent = new Intent("com.android.systemui.demo");
    localIntent.putExtra("command", "exit");
    getContext().sendBroadcast(localIntent);
    setGlobal("sysui_tuner_demo_on", 0);
  }
  
  private void updateDemoModeEnabled()
  {
    if (Settings.Global.getInt(getContext().getContentResolver(), "sysui_demo_allowed", 0) != 0) {}
    for (boolean bool = true;; bool = false)
    {
      this.mEnabledSwitch.setChecked(bool);
      this.mOnSwitch.setEnabled(bool);
      return;
    }
  }
  
  private void updateDemoModeOn()
  {
    if (Settings.Global.getInt(getContext().getContentResolver(), "sysui_tuner_demo_on", 0) != 0) {}
    for (boolean bool = true;; bool = false)
    {
      this.mOnSwitch.setChecked(bool);
      return;
    }
  }
  
  public void onCreatePreferences(Bundle paramBundle, String paramString)
  {
    paramBundle = getContext();
    this.mEnabledSwitch = new SwitchPreference(paramBundle);
    this.mEnabledSwitch.setTitle(2131690462);
    this.mEnabledSwitch.setOnPreferenceChangeListener(this);
    this.mOnSwitch = new SwitchPreference(paramBundle);
    this.mOnSwitch.setTitle(2131690463);
    this.mOnSwitch.setEnabled(false);
    this.mOnSwitch.setOnPreferenceChangeListener(this);
    paramBundle = getPreferenceManager().createPreferenceScreen(paramBundle);
    paramBundle.addPreference(this.mEnabledSwitch);
    paramBundle.addPreference(this.mOnSwitch);
    setPreferenceScreen(paramBundle);
    updateDemoModeEnabled();
    updateDemoModeOn();
    paramBundle = getContext().getContentResolver();
    paramBundle.registerContentObserver(Settings.Global.getUriFor("sysui_demo_allowed"), false, this.mDemoModeObserver);
    paramBundle.registerContentObserver(Settings.Global.getUriFor("sysui_tuner_demo_on"), false, this.mDemoModeObserver);
    setHasOptionsMenu(true);
  }
  
  public void onDestroy()
  {
    getContext().getContentResolver().unregisterContentObserver(this.mDemoModeObserver);
    super.onDestroy();
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    }
    for (;;)
    {
      return super.onOptionsItemSelected(paramMenuItem);
      getFragmentManager().popBackStack();
    }
  }
  
  public void onPause()
  {
    super.onPause();
    MetricsLogger.visibility(getContext(), 229, false);
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    int i = 0;
    if (paramObject == Boolean.TRUE) {}
    for (boolean bool = true; paramPreference == this.mEnabledSwitch; bool = false)
    {
      if (!bool)
      {
        this.mOnSwitch.setChecked(false);
        stopDemoMode();
      }
      MetricsLogger.action(getContext(), 235, bool);
      if (bool) {
        i = 1;
      }
      setGlobal("sysui_demo_allowed", i);
      return true;
    }
    if (paramPreference == this.mOnSwitch)
    {
      MetricsLogger.action(getContext(), 236, bool);
      if (bool)
      {
        startDemoMode();
        return true;
      }
      stopDemoMode();
      return true;
    }
    return false;
  }
  
  public void onResume()
  {
    super.onResume();
    MetricsLogger.visibility(getContext(), 229, true);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tuner\DemoModeFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */