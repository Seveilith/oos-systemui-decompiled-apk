package com.android.systemui.tuner;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;

public class PowerNotificationControlsFragment
  extends Fragment
{
  private boolean isEnabled()
  {
    return Settings.Secure.getInt(getContext().getContentResolver(), "show_importance_slider", 0) == 1;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return paramLayoutInflater.inflate(2130968750, paramViewGroup, false);
  }
  
  public void onPause()
  {
    super.onPause();
    MetricsLogger.visibility(getContext(), 392, false);
  }
  
  public void onResume()
  {
    super.onResume();
    MetricsLogger.visibility(getContext(), 392, true);
  }
  
  public void onViewCreated(View paramView, final Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    paramView = paramView.findViewById(2131952306);
    paramBundle = (Switch)paramView.findViewById(16908352);
    final TextView localTextView = (TextView)paramView.findViewById(2131952307);
    paramBundle.setChecked(isEnabled());
    if (isEnabled()) {}
    for (paramView = getString(2131690582);; paramView = getString(2131690583))
    {
      localTextView.setText(paramView);
      paramBundle.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          boolean bool;
          int i;
          label43:
          TextView localTextView;
          if (PowerNotificationControlsFragment.-wrap0(PowerNotificationControlsFragment.this))
          {
            bool = false;
            MetricsLogger.action(PowerNotificationControlsFragment.this.getContext(), 393, bool);
            paramAnonymousView = PowerNotificationControlsFragment.this.getContext().getContentResolver();
            if (!bool) {
              break label91;
            }
            i = 1;
            Settings.Secure.putInt(paramAnonymousView, "show_importance_slider", i);
            paramBundle.setChecked(bool);
            localTextView = localTextView;
            if (!bool) {
              break label96;
            }
          }
          label91:
          label96:
          for (paramAnonymousView = PowerNotificationControlsFragment.this.getString(2131690582);; paramAnonymousView = PowerNotificationControlsFragment.this.getString(2131690583))
          {
            localTextView.setText(paramAnonymousView);
            return;
            bool = true;
            break;
            i = 0;
            break label43;
          }
        }
      });
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tuner\PowerNotificationControlsFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */