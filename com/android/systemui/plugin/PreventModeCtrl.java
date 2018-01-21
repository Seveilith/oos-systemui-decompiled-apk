package com.android.systemui.plugin;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.SystemSensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.System;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.systemui.statusbar.phone.OPFacelockController;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.util.ImageUtils;
import com.android.systemui.util.Utils;

public class PreventModeCtrl
  extends BaseCtrl
{
  private static boolean mPreventModeActive = false;
  private static boolean mPreventModeNoBackground = false;
  private static boolean mProximitySensorEnabled = false;
  private final boolean DEBUG = true;
  private final String TAG = "PreventModeCtrl";
  private ValueAnimator mAlphaAnimator;
  ImageView mBackground;
  private boolean mBouncer = false;
  private boolean mDozing = false;
  private Handler mHandler;
  private int mKeyLockMode;
  private boolean mKeyguardIsVisible = false;
  private Object mObject = new Object();
  PreventModeView mPMView;
  SensorEventListener mProximityListener = new SensorEventListener()
  {
    private void finishWithResult(int paramAnonymousInt)
    {
      Log.d("PreventModeCtrl", "finishWithResult: result = " + paramAnonymousInt);
      if (paramAnonymousInt == 1) {
        PreventModeCtrl.-wrap2(PreventModeCtrl.this);
      }
      do
      {
        return;
        if ((paramAnonymousInt == 2) && (PreventModeCtrl.-get1()))
        {
          PreventModeCtrl.this.mPMView.setVisibility(8);
          PreventModeCtrl.-wrap0(PreventModeCtrl.this);
          return;
        }
      } while (paramAnonymousInt != 0);
      PreventModeCtrl.-wrap0(PreventModeCtrl.this);
    }
    
    public void onAccuracyChanged(Sensor paramAnonymousSensor, int paramAnonymousInt) {}
    
    public void onSensorChanged(SensorEvent paramAnonymousSensorEvent)
    {
      int j = 1;
      int i = 0;
      for (;;)
      {
        synchronized (PreventModeCtrl.-get0(PreventModeCtrl.this))
        {
          if (paramAnonymousSensorEvent.values.length == 0)
          {
            Log.d("PreventModeCtrl", "Event has no values!");
            finishWithResult(0);
            return;
          }
          Log.d("PreventModeCtrl", "Event: value=" + paramAnonymousSensorEvent.values[0] + " max=" + PreventModeCtrl.-get2(PreventModeCtrl.this).getMaximumRange());
          if (paramAnonymousSensorEvent.values[0] == 0.0F)
          {
            i = 1;
            break label124;
            finishWithResult(i);
          }
        }
        label124:
        while (i == 0)
        {
          i = 2;
          break;
        }
        i = j;
      }
    }
  };
  private Sensor mProximitySensor;
  private SensorManager mSensorManager;
  StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
  
  private void disableProximitySensorInternal()
  {
    long l;
    if (mProximitySensorEnabled)
    {
      Log.d("PreventModeCtrl", "disableProximitySensor, " + this.mKeyLockMode);
      l = Binder.clearCallingIdentity();
    }
    try
    {
      this.mSensorManager.unregisterListener(this.mProximityListener);
      this.mPMView.setVisibility(8);
      if ((mPreventModeNoBackground) && (LSState.getInstance().getPhoneStatusBar() != null))
      {
        LSState.getInstance().getPhoneStatusBar().setPanelViewAlpha(1.0F, true);
        Log.d("PreventModeCtrl", "panel alpha to 1");
      }
      mPreventModeNoBackground = false;
      mProximitySensorEnabled = false;
      if (LSState.getInstance().getPhoneStatusBar() != null) {
        LSState.getInstance().getPhoneStatusBar().notifyPreventModeChange(false);
      }
      mPreventModeActive = false;
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void enableProximitySensorInternal()
  {
    if (!mPreventModeActive)
    {
      this.mHandler.removeMessages(3);
      Message localMessage = this.mHandler.obtainMessage(3);
      this.mHandler.sendMessageDelayed(localMessage, 5000L);
    }
    long l;
    if (!mProximitySensorEnabled)
    {
      Log.d("PreventModeCtrl", "enableProximitySensor");
      l = Binder.clearCallingIdentity();
    }
    try
    {
      this.mSensorManager.registerListener(this.mProximityListener, this.mProximitySensor, 3);
      mProximitySensorEnabled = true;
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private boolean isPreventModeEnabled()
  {
    if (Utils.isPreventModeEnalbed(this.mContext)) {
      return this.mKeyguardIsVisible;
    }
    return false;
  }
  
  private void startRootAnimation()
  {
    if ((!mPreventModeActive) && (this.mKeyguardIsVisible))
    {
      this.mKeyLockMode = Settings.System.getInt(this.mContext.getContentResolver(), "oem_acc_key_lock_mode", -1);
      Log.d("PreventModeCtrl", "startRootAnimation, " + this.mKeyLockMode + ", " + this.mBackground.getDrawable());
      PhoneStatusBar localPhoneStatusBar = LSState.getInstance().getPhoneStatusBar();
      if ((this.mBackground.getDrawable() == null) && (localPhoneStatusBar != null))
      {
        localPhoneStatusBar.setPanelViewAlpha(0.0F, true);
        mPreventModeNoBackground = true;
        Log.d("PreventModeCtrl", "panel alpha to 0");
      }
      mPreventModeActive = true;
      if (localPhoneStatusBar != null)
      {
        if (localPhoneStatusBar.getFacelockController() != null) {
          localPhoneStatusBar.getFacelockController().stopFacelockLightMode();
        }
        localPhoneStatusBar.notifyPreventModeChange(true);
      }
      this.mHandler.removeMessages(3);
      this.mAlphaAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
      this.mAlphaAnimator.setDuration(0L);
      this.mAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          float f = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
          PreventModeCtrl.this.mPMView.setAlpha(f);
        }
      });
      this.mAlphaAnimator.addListener(new Animator.AnimatorListener()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator) {}
        
        public void onAnimationEnd(Animator paramAnonymousAnimator) {}
        
        public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          PreventModeCtrl.this.mPMView.setVisibility(0);
        }
      });
      this.mAlphaAnimator.start();
      return;
    }
  }
  
  public void disPatchTouchEvent(MotionEvent paramMotionEvent)
  {
    this.mPMView.dispatchTouchEvent(paramMotionEvent);
  }
  
  public void disableProximitySensor()
  {
    if (this.mHandler != null)
    {
      this.mHandler.removeMessages(1);
      this.mHandler.removeMessages(2);
      this.mHandler.removeMessages(3);
      this.mHandler.sendEmptyMessage(2);
    }
  }
  
  public boolean isPreventModeActive()
  {
    return mPreventModeActive;
  }
  
  public boolean isPreventModeNoBackground()
  {
    return mPreventModeNoBackground;
  }
  
  public void onDreamingStarted()
  {
    this.mDozing = true;
  }
  
  public void onDreamingStopped()
  {
    this.mDozing = false;
  }
  
  public void onFinishedGoingToSleep(int paramInt)
  {
    disableProximitySensor();
  }
  
  public void onKeyguardBouncerChanged(boolean paramBoolean)
  {
    this.mBouncer = paramBoolean;
  }
  
  public void onKeyguardVisibilityChanged(boolean paramBoolean)
  {
    this.mKeyguardIsVisible = paramBoolean;
    if (!paramBoolean) {
      disableProximitySensor();
    }
  }
  
  public void onScreenTurnedOn()
  {
    if ((this.mHandler == null) || (!isPreventModeEnabled()) || (this.mDozing)) {
      return;
    }
    this.mHandler.removeMessages(1);
    this.mHandler.removeMessages(2);
    this.mHandler.removeMessages(3);
    this.mHandler.sendEmptyMessage(1);
  }
  
  public void onStartCtrl()
  {
    Log.d("PreventModeCtrl", "onStartCtrl");
    this.mStatusBarKeyguardViewManager = LSState.getInstance().getStatusBarKeyguardViewManager();
    this.mPMView = ((PreventModeView)LSState.getInstance().getContainer().findViewById(2131951897));
    this.mBackground = ((ImageView)LSState.getInstance().getContainer().findViewById(2131951898));
    if (this.mPMView == null) {
      Log.d("PreventModeCtrl", "mPMView == null, " + LSState.getInstance().getContainer());
    }
    this.mPMView.init();
    this.mHandler = new ProximityHandler(null);
    this.mSensorManager = new SystemSensorManager(this.mContext, this.mHandler.getLooper());
    this.mProximitySensor = this.mSensorManager.getDefaultSensor(8, true);
  }
  
  public void onStartedWakingUp()
  {
    if ((this.mHandler != null) && (isPreventModeEnabled()))
    {
      this.mHandler.removeMessages(1);
      this.mHandler.removeMessages(2);
      this.mHandler.removeMessages(3);
      this.mHandler.sendEmptyMessage(1);
    }
  }
  
  public void onWallpaperChange(Bitmap paramBitmap)
  {
    StringBuilder localStringBuilder = new StringBuilder().append("onWallpaperChange: bitmap:");
    if (paramBitmap != null) {}
    for (boolean bool = true;; bool = false)
    {
      Log.d("PreventModeCtrl", bool);
      if (paramBitmap == null) {
        break;
      }
      paramBitmap = ImageUtils.computeCustomBackgroundBounds(this.mContext, paramBitmap);
      this.mBackground.setImageBitmap(paramBitmap);
      return;
    }
    this.mBackground.setImageDrawable(null);
  }
  
  private class ProximityHandler
    extends Handler
  {
    private ProximityHandler() {}
    
    public void handleMessage(Message arg1)
    {
      switch (???.what)
      {
      default: 
        return;
      }
      for (;;)
      {
        synchronized (PreventModeCtrl.-get0(PreventModeCtrl.this))
        {
          PreventModeCtrl.-wrap1(PreventModeCtrl.this);
          return;
        }
        synchronized (PreventModeCtrl.-get0(PreventModeCtrl.this))
        {
          PreventModeCtrl.-wrap0(PreventModeCtrl.this);
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\plugin\PreventModeCtrl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */