package com.android.systemui.tuner;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v14.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.internal.logging.MetricsLogger;

public class TunerFragment
  extends PreferenceFragment
{
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setHasOptionsMenu(true);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    paramMenu.add(0, 2, 0, 2131690483);
  }
  
  public void onCreatePreferences(Bundle paramBundle, String paramString)
  {
    addPreferencesFromResource(2131296258);
    if ((Settings.Secure.getInt(getContext().getContentResolver(), "seen_tuner_warning", 0) == 0) && (getFragmentManager().findFragmentByTag("tuner_warning") == null)) {
      new TunerWarningFragment().show(getFragmentManager(), "tuner_warning");
    }
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    case 16908332: 
      getActivity().finish();
      return true;
    }
    TunerService.showResetRequest(getContext(), new Runnable()
    {
      public void run()
      {
        TunerFragment.this.getActivity().finish();
      }
    });
    return true;
  }
  
  public void onPause()
  {
    super.onPause();
    MetricsLogger.visibility(getContext(), 227, false);
  }
  
  public void onResume()
  {
    super.onResume();
    getActivity().setTitle(2131690455);
    MetricsLogger.visibility(getContext(), 227, true);
  }
  
  public static class TunerWarningFragment
    extends DialogFragment
  {
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      new AlertDialog.Builder(getContext()).setTitle(2131690478).setMessage(2131690479).setPositiveButton(2131690481, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          Settings.Secure.putInt(TunerFragment.TunerWarningFragment.this.getContext().getContentResolver(), "seen_tuner_warning", 1);
        }
      }).show();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tuner\TunerFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */