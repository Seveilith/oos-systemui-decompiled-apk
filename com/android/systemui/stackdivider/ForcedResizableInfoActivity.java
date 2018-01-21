package com.android.systemui.stackdivider;

import android.app.Activity;
import android.app.ActivityManager.TaskDescription;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.TextView;

public class ForcedResizableInfoActivity
  extends Activity
  implements View.OnTouchListener
{
  private final Runnable mFinishRunnable = new Runnable()
  {
    public void run()
    {
      ForcedResizableInfoActivity.this.finish();
    }
  };
  
  public void finish()
  {
    super.finish();
    overridePendingTransition(0, 2131034144);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968625);
    ((TextView)findViewById(16908299)).setText(2131690629);
    getWindow().setTitle(getString(2131690629));
    getWindow().getDecorView().setOnTouchListener(this);
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    finish();
    return true;
  }
  
  protected void onStart()
  {
    super.onStart();
    getWindow().getDecorView().postDelayed(this.mFinishRunnable, 2500L);
  }
  
  protected void onStop()
  {
    super.onStop();
    finish();
  }
  
  public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
  {
    finish();
    return true;
  }
  
  public void setTaskDescription(ActivityManager.TaskDescription paramTaskDescription) {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\stackdivider\ForcedResizableInfoActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */