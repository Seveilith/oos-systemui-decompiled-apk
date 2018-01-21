package com.android.systemui.keyguard;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.Trace;
import android.util.Log;
import com.android.internal.policy.IKeyguardDrawnCallback;
import com.android.internal.policy.IKeyguardExitCallback;
import com.android.internal.policy.IKeyguardService.Stub;
import com.android.internal.policy.IKeyguardStateCallback;
import com.android.systemui.SystemUIApplication;

public class KeyguardService
  extends Service
{
  private final IKeyguardService.Stub mBinder = new IKeyguardService.Stub()
  {
    public void addStateMonitorCallback(IKeyguardStateCallback paramAnonymousIKeyguardStateCallback)
    {
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).addStateMonitorCallback(paramAnonymousIKeyguardStateCallback);
    }
    
    public void dismiss(boolean paramAnonymousBoolean)
    {
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).dismiss(paramAnonymousBoolean);
    }
    
    public void doKeyguardTimeout(Bundle paramAnonymousBundle)
    {
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).doKeyguardTimeout(paramAnonymousBundle);
    }
    
    public void forceDismiss(boolean paramAnonymousBoolean)
    {
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).forceDismiss(paramAnonymousBoolean);
    }
    
    public void keyguardDone(boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
    {
      Trace.beginSection("KeyguardService.mBinder#keyguardDone");
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).keyguardDone(paramAnonymousBoolean1);
      Trace.endSection();
    }
    
    public void notifyLidSwitchChanged(boolean paramAnonymousBoolean)
    {
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).notifyLidSwitchChanged(paramAnonymousBoolean);
    }
    
    public void onActivityDrawn()
    {
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).onActivityDrawn();
    }
    
    public void onBootCompleted()
    {
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).onBootCompleted();
    }
    
    public void onDreamingStarted()
    {
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).onDreamingStarted();
    }
    
    public void onDreamingStopped()
    {
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).onDreamingStopped();
    }
    
    public void onFinishedGoingToSleep(int paramAnonymousInt, boolean paramAnonymousBoolean)
    {
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).onFinishedGoingToSleep(paramAnonymousInt, paramAnonymousBoolean);
    }
    
    public void onScreenTurnedOff()
    {
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).onScreenTurnedOff();
    }
    
    public void onScreenTurnedOn()
    {
      Trace.beginSection("KeyguardService.mBinder#onScreenTurningOn");
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).onScreenTurnedOn();
      Trace.endSection();
    }
    
    public void onScreenTurningOn(IKeyguardDrawnCallback paramAnonymousIKeyguardDrawnCallback)
    {
      Trace.beginSection("KeyguardService.mBinder#onScreenTurningOn");
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).onScreenTurningOn(paramAnonymousIKeyguardDrawnCallback);
      Trace.endSection();
    }
    
    public void onStartedGoingToSleep(int paramAnonymousInt)
    {
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).onStartedGoingToSleep(paramAnonymousInt);
    }
    
    public void onStartedWakingUp()
    {
      Trace.beginSection("KeyguardService.mBinder#onStartedWakingUp");
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).onStartedWakingUp();
      Trace.endSection();
    }
    
    public void onSystemReady()
    {
      Trace.beginSection("KeyguardService.mBinder#onSystemReady");
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).onSystemReady();
      Trace.endSection();
    }
    
    public void setCurrentUser(int paramAnonymousInt)
    {
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).setCurrentUser(paramAnonymousInt);
    }
    
    public void setKeyguardEnabled(boolean paramAnonymousBoolean)
    {
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).setKeyguardEnabled(paramAnonymousBoolean);
    }
    
    public void setOccluded(boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
    {
      Trace.beginSection("KeyguardService.mBinder#setOccluded");
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).setOccluded(paramAnonymousBoolean1, paramAnonymousBoolean2);
      Trace.endSection();
    }
    
    public void startKeyguardExitAnimation(long paramAnonymousLong1, long paramAnonymousLong2)
    {
      Trace.beginSection("KeyguardService.mBinder#startKeyguardExitAnimation");
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).startKeyguardExitAnimation(paramAnonymousLong1, paramAnonymousLong2);
      Trace.endSection();
    }
    
    public void verifyUnlock(IKeyguardExitCallback paramAnonymousIKeyguardExitCallback)
    {
      Trace.beginSection("KeyguardService.mBinder#verifyUnlock");
      KeyguardService.this.checkPermission();
      KeyguardService.-get0(KeyguardService.this).verifyUnlock(paramAnonymousIKeyguardExitCallback);
      Trace.endSection();
    }
  };
  private KeyguardViewMediator mKeyguardViewMediator;
  
  void checkPermission()
  {
    if (Binder.getCallingUid() == 1000) {
      return;
    }
    if (getBaseContext().checkCallingOrSelfPermission("android.permission.CONTROL_KEYGUARD") != 0)
    {
      Log.w("KeyguardService", "Caller needs permission 'android.permission.CONTROL_KEYGUARD' to call " + Debug.getCaller());
      throw new SecurityException("Access denied to process: " + Binder.getCallingPid() + ", must have permission " + "android.permission.CONTROL_KEYGUARD");
    }
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return this.mBinder;
  }
  
  public void onCreate()
  {
    ((SystemUIApplication)getApplication()).startServicesIfNeeded();
    this.mKeyguardViewMediator = ((KeyguardViewMediator)((SystemUIApplication)getApplication()).getComponent(KeyguardViewMediator.class));
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\keyguard\KeyguardService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */