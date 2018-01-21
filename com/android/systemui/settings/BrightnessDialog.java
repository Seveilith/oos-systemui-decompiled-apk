package com.android.systemui.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ImageView;
import com.android.internal.logging.MetricsLogger;

public class BrightnessDialog
  extends Activity
{
  private BrightnessController mBrightnessController;
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getWindow();
    paramBundle.setGravity(48);
    paramBundle.clearFlags(2);
    paramBundle.requestFeature(1);
    setContentView(2130968784);
    this.mBrightnessController = new BrightnessController(this, (ImageView)findViewById(2131952165), (ToggleSlider)findViewById(2131952163));
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramInt == 25) || (paramInt == 24)) {}
    for (;;)
    {
      finish();
      do
      {
        return super.onKeyDown(paramInt, paramKeyEvent);
      } while (paramInt != 164);
    }
  }
  
  protected void onStart()
  {
    super.onStart();
    this.mBrightnessController.registerCallbacks();
    MetricsLogger.visible(this, 220);
  }
  
  protected void onStop()
  {
    super.onStop();
    MetricsLogger.hidden(this, 220);
    this.mBrightnessController.unregisterCallbacks();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\settings\BrightnessDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */