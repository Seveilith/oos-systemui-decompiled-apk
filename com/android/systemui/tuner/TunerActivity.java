package com.android.systemui.tuner;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v14.preference.PreferenceFragment.OnPreferenceStartFragmentCallback;
import android.support.v14.preference.PreferenceFragment.OnPreferenceStartScreenCallback;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import com.android.settingslib.drawer.SettingsDrawerActivity;

public class TunerActivity
  extends SettingsDrawerActivity
  implements PreferenceFragment.OnPreferenceStartFragmentCallback, PreferenceFragment.OnPreferenceStartScreenCallback
{
  public void onBackPressed()
  {
    if (!getFragmentManager().popBackStackImmediate()) {
      super.onBackPressed();
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    boolean bool;
    if (getFragmentManager().findFragmentByTag("tuner") == null)
    {
      paramBundle = getIntent().getAction();
      if (paramBundle == null) {
        break label68;
      }
      bool = paramBundle.equals("com.android.settings.action.DEMO_MODE");
      if (!bool) {
        break label73;
      }
    }
    label68:
    label73:
    for (paramBundle = new DemoModeFragment();; paramBundle = new TunerFragment())
    {
      getFragmentManager().beginTransaction().replace(2131951980, paramBundle, "tuner").commit();
      return;
      bool = false;
      break;
    }
  }
  
  public boolean onPreferenceStartFragment(PreferenceFragment paramPreferenceFragment, Preference paramPreference)
  {
    try
    {
      paramPreferenceFragment = (Fragment)Class.forName(paramPreference.getFragment()).newInstance();
      FragmentTransaction localFragmentTransaction = getFragmentManager().beginTransaction();
      setTitle(paramPreference.getTitle());
      localFragmentTransaction.replace(2131951980, paramPreferenceFragment);
      localFragmentTransaction.addToBackStack("PreferenceFragment");
      localFragmentTransaction.commit();
      return true;
    }
    catch (ClassNotFoundException|InstantiationException|IllegalAccessException paramPreferenceFragment)
    {
      Log.d("TunerActivity", "Problem launching fragment", paramPreferenceFragment);
    }
    return false;
  }
  
  public boolean onPreferenceStartScreen(PreferenceFragment paramPreferenceFragment, PreferenceScreen paramPreferenceScreen)
  {
    FragmentTransaction localFragmentTransaction = getFragmentManager().beginTransaction();
    SubSettingsFragment localSubSettingsFragment = new SubSettingsFragment();
    Bundle localBundle = new Bundle(1);
    localBundle.putString("android.support.v7.preference.PreferenceFragmentCompat.PREFERENCE_ROOT", paramPreferenceScreen.getKey());
    localSubSettingsFragment.setArguments(localBundle);
    localSubSettingsFragment.setTargetFragment(paramPreferenceFragment, 0);
    localFragmentTransaction.replace(2131951980, localSubSettingsFragment);
    localFragmentTransaction.addToBackStack("PreferenceFragment");
    localFragmentTransaction.commit();
    return true;
  }
  
  public static class SubSettingsFragment
    extends PreferenceFragment
  {
    public void onCreatePreferences(Bundle paramBundle, String paramString)
    {
      setPreferenceScreen((PreferenceScreen)((PreferenceFragment)getTargetFragment()).getPreferenceScreen().findPreference(paramString));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tuner\TunerActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */