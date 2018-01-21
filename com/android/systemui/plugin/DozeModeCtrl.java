package com.android.systemui.plugin;

import android.view.ViewGroup;
import android.widget.TextView;

public class DozeModeCtrl
  extends BaseCtrl
{
  private final String TAG = "DozeModeCtrl";
  TextView mClockView;
  
  public void onDozePulsing() {}
  
  public void onStartCtrl()
  {
    this.mClockView = ((TextView)LSState.getInstance().getContainer().findViewById(2131951787));
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\plugin\DozeModeCtrl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */