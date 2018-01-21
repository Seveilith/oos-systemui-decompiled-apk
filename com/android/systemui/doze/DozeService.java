package com.android.systemui.doze;

import android.app.ActivityManager;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.service.dreams.DreamService;
import android.util.Log;
import com.android.internal.hardware.AmbientDisplayConfiguration;
import com.android.systemui.SystemUIApplication;
import com.android.systemui.plugin.LSState;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.util.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DozeService
  extends DreamService
{
  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("com.android.systemui.doze.pulse".equals(paramAnonymousIntent.getAction()))
      {
        Log.d(DozeService.-get10(DozeService.this), "Received pulse intent");
        DozeService.-wrap6(DozeService.this, 0);
      }
      if (UiModeManager.ACTION_ENTER_CAR_MODE.equals(paramAnonymousIntent.getAction()))
      {
        Log.d(DozeService.-get10(DozeService.this), "Received UIMode intent ");
        DozeService.-set0(DozeService.this, true);
        if ((DozeService.-get0(DozeService.this)) && (DozeService.-get3(DozeService.this))) {
          DozeService.-wrap2(DozeService.this);
        }
      }
      if ("android.intent.action.USER_SWITCHED".equals(paramAnonymousIntent.getAction())) {}
    }
  };
  private boolean mBroadcastReceiverRegistered;
  private boolean mCarMode;
  private AmbientDisplayConfiguration mConfig;
  private final Context mContext = this;
  private boolean mDisplayStateSupported;
  private final DozeParameters mDozeParameters = new DozeParameters(this.mContext);
  private boolean mDreaming;
  private final Handler mHandler = new Handler();
  private DozeHost mHost;
  private final DozeHost.Callback mHostCallback = new DozeHost.Callback()
  {
    public void onBuzzBeepBlinked()
    {
      Log.d(DozeService.-get10(DozeService.this), "onBuzzBeepBlinked");
    }
    
    public void onNewNotifications()
    {
      Log.d(DozeService.-get10(DozeService.this), "onNewNotifications (noop)");
      DozeService.-wrap4(DozeService.this);
    }
    
    public void onNotificationLight(boolean paramAnonymousBoolean)
    {
      Log.d(DozeService.-get10(DozeService.this), "onNotificationLight (noop) on=" + paramAnonymousBoolean);
    }
    
    public void onPowerSaveChanged(boolean paramAnonymousBoolean)
    {
      DozeService.-set1(DozeService.this, paramAnonymousBoolean);
      if ((DozeService.-get7(DozeService.this)) && (DozeService.-get3(DozeService.this))) {
        DozeService.-wrap3(DozeService.this);
      }
    }
  };
  private MotionCheck mMotion;
  private long mNotificationPulseTime;
  private PowerManager mPowerManager;
  private boolean mPowerSaveActive;
  private boolean mPulsing;
  private SensorManager mSensorManager;
  private final ContentObserver mSettingsObserver = new ContentObserver(this.mHandler)
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri, int paramAnonymousInt)
    {
      if (paramAnonymousInt != ActivityManager.getCurrentUser()) {}
    }
  };
  private final String mTag = String.format("DozeService.%08x", new Object[] { Integer.valueOf(hashCode()) });
  private UiModeManager mUiModeManager;
  private PowerManager.WakeLock mWakeLock;
  
  public DozeService()
  {
    Log.d(this.mTag, "new DozeService()");
    setDebug(true);
  }
  
  private void continuePulsing(int paramInt)
  {
    if (this.mHost.isPulsingBlocked())
    {
      this.mPulsing = false;
      this.mWakeLock.release();
      return;
    }
    this.mHost.pulseWhileDozing(new DozeHost.PulseCallback()
    {
      public void onPulseFinished()
      {
        Log.d("DozeService", "onPulseFinished, " + DozeService.-get8(DozeService.this) + " , " + DozeService.-get3(DozeService.this));
        if ((DozeService.-get8(DozeService.this)) && (DozeService.-get3(DozeService.this)))
        {
          DozeService.-set2(DozeService.this, false);
          DozeService.-wrap7(DozeService.this);
          DozeService.-wrap8(DozeService.this);
        }
        DozeService.-get11(DozeService.this).release();
      }
      
      public void onPulseStarted()
      {
        Log.d("DozeService", "onPulseStarted, " + DozeService.-get8(DozeService.this) + " , " + DozeService.-get3(DozeService.this));
        if ((DozeService.-get8(DozeService.this)) && (DozeService.-get3(DozeService.this))) {
          DozeService.-wrap9(DozeService.this);
        }
      }
    }, paramInt);
  }
  
  private void finishForCarMode()
  {
    Log.w(this.mTag, "Exiting ambient mode, not allowed in car mode");
    finish();
  }
  
  private void finishToSavePower()
  {
    Log.w(this.mTag, "Exiting ambient mode due to low power battery saver");
    finish();
  }
  
  private void listenForBroadcasts(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      IntentFilter localIntentFilter = new IntentFilter("com.android.systemui.doze.pulse");
      localIntentFilter.addAction(UiModeManager.ACTION_ENTER_CAR_MODE);
      localIntentFilter.addAction("android.intent.action.USER_SWITCHED");
      this.mContext.registerReceiver(this.mBroadcastReceiver, localIntentFilter);
      this.mBroadcastReceiverRegistered = true;
      return;
    }
    if (this.mBroadcastReceiverRegistered) {
      this.mContext.unregisterReceiver(this.mBroadcastReceiver);
    }
    this.mBroadcastReceiverRegistered = false;
  }
  
  private void listenForNotifications(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mHost.addCallback(this.mHostCallback);
      return;
    }
    this.mHost.removeCallback(this.mHostCallback);
  }
  
  private void listenForPulseSignals(boolean paramBoolean)
  {
    Log.d(this.mTag, "listenForPulseSignals: " + paramBoolean);
    if (Utils.isProximityDozeEnable(this.mContext)) {
      this.mMotion.setListening(paramBoolean);
    }
    listenForBroadcasts(paramBoolean);
    listenForNotifications(paramBoolean);
  }
  
  private void requestNotificationPulse()
  {
    Log.d(this.mTag, "requestNotificationPulse");
    if (!this.mConfig.pulseOnNotificationEnabled(-2)) {
      return;
    }
    this.mNotificationPulseTime = SystemClock.elapsedRealtime();
    requestPulse(1);
  }
  
  private void requestPulse(int paramInt)
  {
    requestPulse(paramInt, false);
  }
  
  private void requestPulse(int paramInt, final boolean paramBoolean)
  {
    Log.d("DozeService", "requestPulseL: mHost = " + this.mHost + ", mDreaming = " + this.mDreaming + ", mPulsing = " + this.mPulsing + ", reason=" + paramInt + ", check=" + paramBoolean);
    if ((this.mHost == null) || (!this.mDreaming) || (this.mPulsing)) {
      return;
    }
    this.mWakeLock.acquire();
    this.mPulsing = true;
    if (!this.mDozeParameters.getProxCheckBeforePulse())
    {
      LSState.getInstance().onDozePulsing();
      continuePulsing(paramInt);
      return;
    }
    final long l = SystemClock.uptimeMillis();
    if (paramBoolean)
    {
      LSState.getInstance().onDozePulsing();
      continuePulsing(paramInt);
    }
    new ProximityCheck(this, l)
    {
      public void onProximityResult(int paramAnonymousInt)
      {
        if (paramAnonymousInt == 1) {}
        for (boolean bool = true;; bool = false)
        {
          long l = SystemClock.uptimeMillis();
          DozeLog.traceProximityResult(DozeService.-get1(jdField_this), bool, l - l, paramBoolean);
          if (!this.val$performedProxCheck) {
            break;
          }
          return;
        }
        if (bool)
        {
          DozeService.-set2(jdField_this, false);
          DozeService.-get11(jdField_this).release();
          return;
        }
        LSState.getInstance().onDozePulsing();
        DozeService.-wrap1(jdField_this, paramBoolean);
      }
    }.check();
  }
  
  private void reregisterAllSensors() {}
  
  private void turnDisplayOff()
  {
    Log.d(this.mTag, "Display off");
    setDozeScreenState(1);
  }
  
  private void turnDisplayOn()
  {
    Log.d(this.mTag, "Display on");
    if (this.mDisplayStateSupported) {}
    for (int i = 3;; i = 2)
    {
      setDozeScreenState(i);
      return;
    }
  }
  
  protected void dumpOnHandler(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    super.dumpOnHandler(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    paramPrintWriter.print("  mDreaming: ");
    paramPrintWriter.println(this.mDreaming);
    paramPrintWriter.print("  mPulsing: ");
    paramPrintWriter.println(this.mPulsing);
    paramPrintWriter.print("  mWakeLock: held=");
    paramPrintWriter.println(this.mWakeLock.isHeld());
    paramPrintWriter.print("  mHost: ");
    paramPrintWriter.println(this.mHost);
    paramPrintWriter.print("  mBroadcastReceiverRegistered: ");
    paramPrintWriter.println(this.mBroadcastReceiverRegistered);
    paramPrintWriter.print("  mMotion:");
    paramPrintWriter.println(this.mMotion);
    paramPrintWriter.print("  mDisplayStateSupported: ");
    paramPrintWriter.println(this.mDisplayStateSupported);
    paramPrintWriter.print("  mPowerSaveActive: ");
    paramPrintWriter.println(this.mPowerSaveActive);
    paramPrintWriter.print("  mCarMode: ");
    paramPrintWriter.println(this.mCarMode);
    paramPrintWriter.print("  mNotificationPulseTime: ");
    paramPrintWriter.println(DozeLog.FORMAT.format(new Date(this.mNotificationPulseTime - SystemClock.elapsedRealtime() + System.currentTimeMillis())));
    this.mDozeParameters.dump(paramPrintWriter);
  }
  
  public void onAttachedToWindow()
  {
    Log.d(this.mTag, "onAttachedToWindow");
    super.onAttachedToWindow();
  }
  
  public void onCreate()
  {
    Log.d(this.mTag, "onCreate");
    super.onCreate();
    if ((getApplication() instanceof SystemUIApplication)) {
      this.mHost = ((DozeHost)((SystemUIApplication)getApplication()).getComponent(DozeHost.class));
    }
    if (this.mHost == null) {
      Log.w("DozeService", "No doze service host found.");
    }
    setWindowless(true);
    this.mSensorManager = ((SensorManager)this.mContext.getSystemService("sensor"));
    this.mConfig = new AmbientDisplayConfiguration(this.mContext);
    this.mMotion = new MotionCheck();
    this.mPowerManager = ((PowerManager)this.mContext.getSystemService("power"));
    this.mWakeLock = this.mPowerManager.newWakeLock(1, "DozeService");
    this.mWakeLock.setReferenceCounted(true);
    this.mDisplayStateSupported = this.mDozeParameters.getDisplayStateSupported();
    this.mUiModeManager = ((UiModeManager)this.mContext.getSystemService("uimode"));
    turnDisplayOff();
  }
  
  public void onDreamingStarted()
  {
    super.onDreamingStarted();
    if (this.mHost == null)
    {
      finish();
      return;
    }
    this.mPowerSaveActive = this.mHost.isPowerSaveActive();
    if (this.mUiModeManager.getCurrentModeType() == 3) {}
    for (boolean bool = true;; bool = false)
    {
      this.mCarMode = bool;
      Log.d(this.mTag, "onDreamingStarted canDoze=" + canDoze() + " mPowerSaveActive=" + this.mPowerSaveActive + " mCarMode=" + this.mCarMode);
      if (!this.mPowerSaveActive) {
        break;
      }
      finishToSavePower();
      return;
    }
    if (this.mCarMode)
    {
      finishForCarMode();
      return;
    }
    this.mDreaming = true;
    listenForPulseSignals(true);
    LSState.getInstance().onDreamingStarted();
    this.mHost.startDozing(this.mWakeLock.wrap(new -void_onDreamingStarted__LambdaImpl0()));
  }
  
  public void onDreamingStopped()
  {
    Log.d(this.mTag, "onDreamingStopped isDozing=" + isDozing());
    super.onDreamingStopped();
    if (this.mHost == null) {
      return;
    }
    this.mDreaming = false;
    listenForPulseSignals(false);
    LSState.getInstance().onDreamingStopped();
    this.mHost.stopDozing();
  }
  
  private class MotionCheck
    implements SensorEventListener, Runnable
  {
    private int mCurrentState;
    private boolean mFinished = false;
    private float mMaxRange;
    private boolean mProximityChecking;
    private boolean mRegistered;
    private int mSensorType = 33171026;
    private final String mTag = DozeService.-get10(DozeService.this) + ".MotionCheck";
    
    public MotionCheck()
    {
      Log.d("DozeService", "choose sensor: " + "TYPE_PICK_UP");
    }
    
    private void finishWithResult(int paramInt)
    {
      if (!this.mRegistered) {
        return;
      }
      if ((this.mCurrentState != 0) && (paramInt == 1)) {
        DozeService.-wrap6(DozeService.this, 3);
      }
      this.mCurrentState = paramInt;
    }
    
    private void release()
    {
      if (!this.mRegistered) {
        return;
      }
      if (DozeService.-get9(DozeService.this) != null)
      {
        Log.d(this.mTag, "Unregister P Sensor");
        DozeService.-get9(DozeService.this).unregisterListener(this);
        this.mRegistered = false;
      }
    }
    
    public void check()
    {
      if ((this.mFinished) || (this.mRegistered)) {
        return;
      }
      Sensor localSensor = DozeService.-get9(DozeService.this).getDefaultSensor(this.mSensorType);
      if (localSensor == null)
      {
        Log.d(this.mTag, "No sensor found");
        finishWithResult(0);
        return;
      }
      Log.d(this.mTag, "sensor registered");
      this.mMaxRange = localSensor.getMaximumRange();
      DozeService.-get9(DozeService.this).registerListener(this, localSensor, 3, 0, DozeService.-get4(DozeService.this));
      DozeService.-get4(DozeService.this).postDelayed(this, 500L);
      this.mRegistered = true;
    }
    
    public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}
    
    public void onSensorChanged(SensorEvent paramSensorEvent)
    {
      int j = 1;
      int i = 0;
      if (this.mProximityChecking) {
        return;
      }
      if (paramSensorEvent.values.length == 0)
      {
        Log.d(this.mTag, "Event has no values!");
        finishWithResult(0);
        return;
      }
      Log.d(this.mTag, "Event: value=" + paramSensorEvent.values[0] + " max=" + this.mMaxRange);
      if (paramSensorEvent.values[0] == 1.0F) {
        i = 1;
      }
      if (i != 0) {}
      for (i = j;; i = 2)
      {
        finishWithResult(i);
        return;
      }
    }
    
    public void run() {}
    
    public void setListening(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        check();
        return;
      }
      release();
    }
    
    public void setProximityChecking(boolean paramBoolean)
    {
      if (this.mProximityChecking == paramBoolean) {
        return;
      }
      this.mProximityChecking = paramBoolean;
    }
  }
  
  private abstract class ProximityCheck
    implements SensorEventListener, Runnable
  {
    private boolean mFinished;
    private float mMaxRange;
    private boolean mRegistered;
    private final String mTag = DozeService.-get10(DozeService.this) + ".ProximityCheck";
    
    private ProximityCheck() {}
    
    private void finishWithResult(int paramInt)
    {
      if (this.mFinished) {
        return;
      }
      if (this.mRegistered)
      {
        DozeService.-get4(DozeService.this).removeCallbacks(this);
        DozeService.-get9(DozeService.this).unregisterListener(this);
        DozeService.-get5(DozeService.this).setProximityChecking(false);
        this.mRegistered = false;
      }
      onProximityResult(paramInt);
      this.mFinished = true;
    }
    
    public void check()
    {
      if ((this.mFinished) || (this.mRegistered)) {
        return;
      }
      Sensor localSensor = DozeService.-get9(DozeService.this).getDefaultSensor(8);
      if (localSensor == null)
      {
        Log.d(this.mTag, "No sensor found");
        finishWithResult(0);
        return;
      }
      DozeService.-get5(DozeService.this).setProximityChecking(true);
      this.mMaxRange = localSensor.getMaximumRange();
      DozeService.-get9(DozeService.this).registerListener(this, localSensor, 3, 0, DozeService.-get4(DozeService.this));
      DozeService.-get4(DozeService.this).postDelayed(this, 500L);
      this.mRegistered = true;
    }
    
    public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}
    
    public abstract void onProximityResult(int paramInt);
    
    public void onSensorChanged(SensorEvent paramSensorEvent)
    {
      int j = 1;
      int i = 0;
      if (paramSensorEvent.values.length == 0)
      {
        Log.d(this.mTag, "Event has no values!");
        finishWithResult(0);
        return;
      }
      Log.d(this.mTag, "Event: value=" + paramSensorEvent.values[0] + " max=" + this.mMaxRange);
      if (paramSensorEvent.values[0] < this.mMaxRange) {
        i = 1;
      }
      if (i != 0) {}
      for (i = j;; i = 2)
      {
        finishWithResult(i);
        return;
      }
    }
    
    public void run()
    {
      Log.d(this.mTag, "No event received before timeout");
      finishWithResult(0);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\doze\DozeService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */