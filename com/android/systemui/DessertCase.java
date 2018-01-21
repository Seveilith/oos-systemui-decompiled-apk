package com.android.systemui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.util.Slog;
import android.view.View;

public class DessertCase
  extends Activity
{
  DessertCaseView mView;
  
  public void onPause()
  {
    super.onPause();
    this.mView.stop();
  }
  
  public void onResume()
  {
    super.onResume();
    this.mView.postDelayed(new Runnable()
    {
      public void run()
      {
        DessertCase.this.mView.start();
      }
    }, 1000L);
  }
  
  public void onStart()
  {
    super.onStart();
    Object localObject = getPackageManager();
    ComponentName localComponentName = new ComponentName(this, DessertCaseDream.class);
    if (((PackageManager)localObject).getComponentEnabledSetting(localComponentName) != 1)
    {
      Slog.v("DessertCase", "ACHIEVEMENT UNLOCKED");
      ((PackageManager)localObject).setComponentEnabledSetting(localComponentName, 1, 1);
    }
    this.mView = new DessertCaseView(this);
    localObject = new DessertCaseView.RescalingContainer(this);
    ((DessertCaseView.RescalingContainer)localObject).setView(this.mView);
    setContentView((View)localObject);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\DessertCase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */