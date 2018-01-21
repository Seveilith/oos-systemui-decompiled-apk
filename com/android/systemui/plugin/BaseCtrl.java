package com.android.systemui.plugin;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.KeyEvent;

public abstract class BaseCtrl
{
  private ControlCallback mCallback;
  protected Context mContext;
  private boolean mStarted = false;
  
  public void init(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public boolean isEnable()
  {
    return this.mStarted;
  }
  
  public void onBackKeyDown() {}
  
  public void onBackPressed() {}
  
  public void onDozePulsing() {}
  
  public void onDreamingStarted() {}
  
  public void onDreamingStopped() {}
  
  public void onFinishedGoingToSleep(int paramInt) {}
  
  public void onKeyguardBouncerChanged(boolean paramBoolean) {}
  
  public void onKeyguardDone(boolean paramBoolean) {}
  
  public void onKeyguardVisibilityChanged(boolean paramBoolean) {}
  
  public void onScreenTurnedOff() {}
  
  public void onScreenTurnedOn() {}
  
  public abstract void onStartCtrl();
  
  public void onStartedGoingToSleep(int paramInt) {}
  
  public void onStartedWakingUp() {}
  
  public void onVolumeKeyPressed(KeyEvent paramKeyEvent) {}
  
  public void onWallpaperChange(Bitmap paramBitmap) {}
  
  public void setCallback(ControlCallback paramControlCallback)
  {
    this.mCallback = paramControlCallback;
  }
  
  public void startCtrl()
  {
    this.mStarted = true;
    onStartCtrl();
  }
  
  public static abstract interface ControlCallback {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\plugin\BaseCtrl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */