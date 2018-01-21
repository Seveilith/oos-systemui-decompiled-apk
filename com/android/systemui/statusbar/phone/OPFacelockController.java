package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.SystemSensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.IPowerManager;
import android.os.IPowerManager.Stub;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;
import android.view.IWindowManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import android.view.WindowManagerGlobal;
import android.widget.Button;
import com.android.internal.policy.IOPFaceSettingService;
import com.android.internal.policy.IOPFaceSettingService.Stub;
import com.android.internal.policy.IOPFacelockCallback;
import com.android.internal.policy.IOPFacelockCallback.Stub;
import com.android.internal.policy.IOPFacelockService;
import com.android.internal.policy.IOPFacelockService.Stub;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.plugin.LSState;
import com.android.systemui.plugin.PreventModeCtrl;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.util.MdmLogger;
import com.android.systemui.util.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class OPFacelockController
  extends KeyguardUpdateMonitorCallback
  implements View.OnClickListener
{
  private static final boolean DEBUG = Utils.DEBUG_ONEPLUS;
  private static String FACELOCK_PACKAGE_NAME = "com.oneplus.faceunlock";
  private static String FACELOCK_SERVICE_NAME = "com.oneplus.faceunlock.FaceUnlockService";
  private static String FACELOCK_SETTING_SERVICE_NAME = "com.oneplus.faceunlock.FaceSettingService";
  private int LIGHTING_MODE_BRIGHTNESS = 300;
  private int LIGHTING_MODE_SENSOR_THRESHOLD = 0;
  private final int MSG_CAMERA_ERROR = 10;
  private final int MSG_FAIL = 4;
  private final int MSG_NO_FACE = 5;
  private final int MSG_NO_PERMISSION = 11;
  private final int MSG_RESET_FACELOCK_PENDING = 8;
  private final int MSG_RESET_LOCKOUT = 6;
  private final int MSG_RESET_SCREEN_ON = 9;
  private final int MSG_SKIP_BOUNCER = 7;
  private final int MSG_START_FACELOCK = 1;
  private final int MSG_STOP_FACELOCK = 2;
  private final int MSG_UNLOCK = 3;
  private ValueAnimator mAlphaAnimator;
  private boolean mBinding = false;
  private boolean mBindingSetting = false;
  private boolean mBoundToService = false;
  private Button mButtonCloseLightView;
  private boolean mCameraLaunching = false;
  private ServiceConnection mConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      Log.d("OPFacelockController", "Connected to Facelock service");
      OPFacelockController.-set5(OPFacelockController.this, IOPFacelockService.Stub.asInterface(paramAnonymousIBinder));
      OPFacelockController.-set0(OPFacelockController.this, false);
      OPFacelockController.-set2(OPFacelockController.this, true);
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      Log.e("OPFacelockController", "disconnect from Facelock service");
      OPFacelockController.-set5(OPFacelockController.this, null);
      OPFacelockController.-set0(OPFacelockController.this, false);
      OPFacelockController.-set2(OPFacelockController.this, false);
    }
  };
  private Context mContext;
  private Runnable mEndStopFacelockRunnable;
  private boolean mFaceLockActive = false;
  private HandlerThread mFacelockThread;
  private int mFailAttempts;
  private Handler mHandler;
  private KeyguardIndicationController mIndicator;
  private boolean mIsGoingToSleep = false;
  private boolean mIsKeyguardShowing = false;
  private boolean mIsScreenOffUnlock = false;
  private boolean mIsScreenTurnedOn = false;
  private boolean mIsSleep = false;
  private KeyguardViewMediator mKeyguardViewMediator;
  private ViewMediatorCallback mKeyguardViewMediatorCallback;
  private final Sensor mLightSensor;
  private final SensorEventListener mLightSensorListener = new SensorEventListener()
  {
    public void onAccuracyChanged(Sensor paramAnonymousSensor, int paramAnonymousInt) {}
    
    public void onSensorChanged(SensorEvent paramAnonymousSensorEvent)
    {
      float f = paramAnonymousSensorEvent.values[0];
      if (OPFacelockController.-get0()) {
        Log.d("OPFacelockController", "light sensor: lux:" + f + ", already lighting:" + OPFacelockController.-get14(OPFacelockController.this) + ", threshold:" + OPFacelockController.-get1(OPFacelockController.this));
      }
      if ((f <= OPFacelockController.-get1(OPFacelockController.this)) && (!OPFacelockController.-get14(OPFacelockController.this))) {
        OPFacelockController.-wrap9(OPFacelockController.this, true);
      }
    }
  };
  private OPFacelockLightView mLightView;
  private boolean mLightingModeEnabled = false;
  private final LockIcon mLockIcon;
  private boolean mLockout = false;
  private boolean mNeedToPendingStopFacelock = false;
  private final IOPFacelockCallback mOPFacelockCallback = new IOPFacelockCallback.Stub()
  {
    public void onBeginRecognize(int paramAnonymousInt)
    {
      if (!OPFacelockController.-get4(OPFacelockController.this)) {
        return;
      }
      if (OPFacelockController.-get0()) {
        Log.d("OPFacelockController", "onBeginRecognize");
      }
    }
    
    public void onCompared(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5)
    {
      if ((paramAnonymousInt3 == 2) && (OPFacelockController.-get10(OPFacelockController.this)))
      {
        if (OPFacelockController.-get0()) {
          Log.d("OPFacelockController", "onCompared 2 to remove timeout");
        }
        OPFacelockController.-get6(OPFacelockController.this).removeMessages(9);
        OPFacelockController.-wrap11(OPFacelockController.this, 1, true);
      }
    }
    
    public void onEndRecognize(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
    {
      if (!OPFacelockController.-get4(OPFacelockController.this)) {
        return;
      }
      if (OPFacelockController.-get10(OPFacelockController.this)) {
        OPFacelockController.-get12(OPFacelockController.this).notifyScreenOffAuthenticate(false, KeyguardViewMediator.AUTHENTICATE_FACEUNLOCK);
      }
      OPFacelockController.-get6(OPFacelockController.this).removeMessages(8);
      OPFacelockController.-set4(OPFacelockController.this, false);
      boolean bool = OPFacelockController.-get20(OPFacelockController.this).isUnlockingWithFingerprintAllowed();
      Log.d("OPFacelockController", "onEndRecognize, result:" + paramAnonymousInt3 + ", keyguardShow:" + OPFacelockController.-get9(OPFacelockController.this) + ", allowed:" + bool + ", isSleep:" + OPFacelockController.-get11(OPFacelockController.this) + ", simpin:" + OPFacelockController.-get20(OPFacelockController.this).isSimPinSecure());
      OPFacelockController.-get12(OPFacelockController.this).userActivity();
      if (paramAnonymousInt3 == 0)
      {
        if ((!OPFacelockController.-get9(OPFacelockController.this)) || (!bool) || (OPFacelockController.-get11(OPFacelockController.this)) || (OPFacelockController.-get20(OPFacelockController.this).isSimPinSecure()))
        {
          Log.d("OPFacelockController", "not handle recognize");
          OPFacelockController.-get6(OPFacelockController.this).removeMessages(2);
          OPFacelockController.-get6(OPFacelockController.this).sendEmptyMessage(2);
          return;
        }
        MdmLogger.log("lock_unlock_success", "face", "1");
        if ((OPFacelockController.-get20(OPFacelockController.this).isAutoFacelockEnabled()) || (OPFacelockController.-get16(OPFacelockController.this).isBouncerShowing()))
        {
          Log.d("OPFacelockController", "onEndRecognize, result ok to unlock");
          OPFacelockController.-get6(OPFacelockController.this).removeMessages(9);
          OPFacelockController.-get6(OPFacelockController.this).sendEmptyMessage(3);
          return;
        }
        Log.d("OPFacelockController", "onEndRecognize, result ok to skip bouncer");
        OPFacelockController.-get6(OPFacelockController.this).sendEmptyMessage(7);
        return;
      }
      if (paramAnonymousInt3 == 2)
      {
        Log.d("OPFacelockController", "onEndRecognize: no face");
        OPFacelockController.-get6(OPFacelockController.this).sendEmptyMessage(5);
        return;
      }
      if (paramAnonymousInt3 == 3)
      {
        Log.d("OPFacelockController", "onEndRecognize: camera error");
        if (OPFacelockController.-get10(OPFacelockController.this))
        {
          OPFacelockController.-get6(OPFacelockController.this).removeMessages(9);
          OPFacelockController.-wrap11(OPFacelockController.this, 1, false);
        }
        OPFacelockController.-get6(OPFacelockController.this).sendEmptyMessage(10);
        return;
      }
      if (paramAnonymousInt3 == 4)
      {
        Log.d("OPFacelockController", "onEndRecognize: no permission");
        if (OPFacelockController.-get10(OPFacelockController.this))
        {
          OPFacelockController.-get6(OPFacelockController.this).removeMessages(9);
          OPFacelockController.-wrap11(OPFacelockController.this, 1, false);
        }
        OPFacelockController.-get6(OPFacelockController.this).sendEmptyMessage(11);
        return;
      }
      MdmLogger.log("lock_unlock_failed", "face", "1");
      Log.d("OPFacelockController", "onEndRecognize: fail " + (OPFacelockController.-get5(OPFacelockController.this) + 1) + " times");
      OPFacelockController.-get6(OPFacelockController.this).sendEmptyMessage(4);
    }
  };
  private boolean mPendingStopFacelock = false;
  private PhoneStatusBar mPhoneStatusBar;
  IPowerManager mPowerManager;
  private SensorManager mSensorManager;
  private int mSensorRate;
  private IOPFacelockService mService;
  private ServiceConnection mSettingConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      OPFacelockController.-set6(OPFacelockController.this, IOPFaceSettingService.Stub.asInterface(paramAnonymousIBinder));
      Log.d("OPFacelockController", "Connected to FaceSetting service, " + OPFacelockController.-get17(OPFacelockController.this));
      OPFacelockController.-wrap10(OPFacelockController.this);
      OPFacelockController.-set1(OPFacelockController.this, false);
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      Log.e("OPFacelockController", "disconnect from FaceSetting service");
      OPFacelockController.-set6(OPFacelockController.this, null);
      OPFacelockController.-get20(OPFacelockController.this).setIsFaceAdded(false);
      OPFacelockController.-set1(OPFacelockController.this, false);
    }
  };
  private IOPFaceSettingService mSettingService;
  private boolean mStartFacelockWhenScreenOn = false;
  private StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
  private StatusBarWindowManager mStatusBarWindowManager;
  private Handler mUIHandler;
  private KeyguardUpdateMonitor mUpdateMonitor;
  private IWindowManager mWM;
  
  public OPFacelockController(Context paramContext, KeyguardViewMediator paramKeyguardViewMediator, PhoneStatusBar paramPhoneStatusBar, KeyguardIndicationController paramKeyguardIndicationController, LockIcon paramLockIcon, StatusBarKeyguardViewManager paramStatusBarKeyguardViewManager, StatusBarWindowManager paramStatusBarWindowManager)
  {
    Log.d("OPFacelockController", "new facelock");
    this.mContext = paramContext;
    this.mUpdateMonitor = KeyguardUpdateMonitor.getInstance(paramContext);
    this.mUpdateMonitor.registerCallback(this);
    this.mKeyguardViewMediator = paramKeyguardViewMediator;
    this.mPhoneStatusBar = paramPhoneStatusBar;
    this.mIndicator = paramKeyguardIndicationController;
    this.mLockIcon = paramLockIcon;
    this.mKeyguardViewMediatorCallback = paramKeyguardViewMediator.getViewMediatorCallback();
    this.mStatusBarKeyguardViewManager = paramStatusBarKeyguardViewManager;
    this.mStatusBarWindowManager = paramStatusBarWindowManager;
    this.mFacelockThread = new HandlerThread("FacelockThread");
    this.mFacelockThread.start();
    this.mHandler = new FacelockHandler(this.mFacelockThread.getLooper());
    this.mUIHandler = new Handler();
    this.mWM = WindowManagerGlobal.getWindowManagerService();
    this.mLightView = ((OPFacelockLightView)LSState.getInstance().getContainer().findViewById(2131952135));
    this.mButtonCloseLightView = ((Button)LSState.getInstance().getContainer().findViewById(2131952141));
    this.mButtonCloseLightView.setOnClickListener(this);
    if (this.mLightView == null) {
      Log.d("OPFacelockController", "mLightView == null, " + LSState.getInstance().getContainer());
    }
    for (;;)
    {
      this.mSensorManager = new SystemSensorManager(this.mContext, this.mHandler.getLooper());
      this.mLightSensor = this.mSensorManager.getDefaultSensor(5);
      this.mSensorRate = this.mContext.getResources().getInteger(17694826);
      this.mPowerManager = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
      return;
      this.mLightView.init();
    }
  }
  
  private void bindFacelock()
  {
    if (this.mBinding) {
      return;
    }
    Intent localIntent = new Intent();
    localIntent.setComponent(new ComponentName(FACELOCK_PACKAGE_NAME, FACELOCK_SERVICE_NAME));
    try
    {
      if (this.mContext.bindServiceAsUser(localIntent, this.mConnection, 1, UserHandle.OWNER))
      {
        Log.d("OPFacelockController", "Binding ok");
        this.mBinding = true;
        return;
      }
      Log.d("OPFacelockController", "Binding fail");
      return;
    }
    catch (Exception localException)
    {
      Log.e("OPFacelockController", "bindFacelock fail, " + localException.getMessage());
    }
  }
  
  private void bindFacelockSetting()
  {
    if (this.mBindingSetting) {
      return;
    }
    Intent localIntent = new Intent();
    localIntent.setComponent(new ComponentName(FACELOCK_PACKAGE_NAME, FACELOCK_SETTING_SERVICE_NAME));
    try
    {
      if (this.mContext.bindServiceAsUser(localIntent, this.mSettingConnection, 1, UserHandle.OWNER))
      {
        Log.d("OPFacelockController", "Binding setting ok");
        this.mBindingSetting = true;
        return;
      }
      Log.d("OPFacelockController", "Binding setting fail");
      return;
    }
    catch (Exception localException)
    {
      Log.e("OPFacelockController", "bind setting fail, " + localException.getMessage());
    }
  }
  
  private void handleRecognizeFail()
  {
    this.mFailAttempts += 1;
    int k = 0;
    int i;
    if (this.mFailAttempts % 5 != 0)
    {
      i = 7;
      if (this.mFailAttempts < 3) {
        break label115;
      }
      j = k;
      if (!LSState.getInstance().getPreventModeCtrl().isPreventModeActive())
      {
        j = k;
        if (this.mPhoneStatusBar != null)
        {
          if (DEBUG) {
            Log.d("OPFacelockController", "enter Bouncer");
          }
          this.mUIHandler.post(new Runnable()
          {
            public void run()
            {
              OPFacelockController.-get16(OPFacelockController.this).animateCollapsePanels(0, true, false, 1.3F);
            }
          });
        }
      }
    }
    label115:
    for (int j = k;; j = 1)
    {
      if (j != 0) {
        playFacelockIndicationTextAnim();
      }
      updateRecognizedState(i, -65536);
      handleStopFacelock();
      return;
      i = 1;
      break;
    }
  }
  
  private void handleResetFacelockPending()
  {
    this.mNeedToPendingStopFacelock = false;
    if (DEBUG) {
      Log.d("OPFacelockController", "handleResetFacelockPending, " + this.mPendingStopFacelock);
    }
    if (this.mPendingStopFacelock) {
      handleStopFacelock();
    }
  }
  
  private void handleResetLockout()
  {
    this.mLockout = false;
    if ((this.mBoundToService) && (canLaunchFacelock()))
    {
      updateRecognizedState(5, -1);
      return;
    }
  }
  
  private void handleSkipBouncer()
  {
    if (DEBUG) {
      Log.d("OPFacelockController", "handleSkipBouncer");
    }
    this.mFailAttempts = 0;
    updateRecognizedState(2, -1);
    handleStopFacelock();
  }
  
  private void handleStartFacelock()
  {
    Log.d("OPFacelockController", "handle startFacelock, active:" + this.mFaceLockActive + ", pendingStop:" + this.mPendingStopFacelock);
    if (this.mService == null)
    {
      Log.d("OPFacelockController", "not start Facelock");
      return;
    }
    if (this.mFaceLockActive)
    {
      this.mPendingStopFacelock = false;
      updateRecognizedState(3, -1);
      return;
    }
    if ((!this.mIsScreenTurnedOn) && (this.mKeyguardViewMediator.isScreenOffAuthenticating()))
    {
      this.mStartFacelockWhenScreenOn = true;
      Log.d("OPFacelockController", "pending start to screen on");
      return;
    }
    this.mStartFacelockWhenScreenOn = false;
    updateRecognizedState(3, -1);
    this.mFaceLockActive = true;
    this.mNeedToPendingStopFacelock = true;
    registerLightSensor(true);
    if ((this.mIsScreenTurnedOn) || (this.mKeyguardViewMediator.isScreenOffAuthenticating())) {}
    for (;;)
    {
      try
      {
        this.mService.registerCallback(this.mOPFacelockCallback);
        this.mService.prepare();
        this.mService.startFaceUnlock(0);
        this.mHandler.removeMessages(8);
        this.mHandler.sendEmptyMessageDelayed(8, 500L);
        return;
        this.mIsScreenOffUnlock = true;
        updateKeyguardAlpha(0, true);
        this.mHandler.removeMessages(9);
        this.mHandler.sendEmptyMessageDelayed(9, 600L);
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.e("OPFacelockController", "startFacelock fail, " + localRemoteException.getMessage());
          handleStopFacelock();
        }
      }
      finally {}
    }
  }
  
  private void handleStopFacelock()
  {
    boolean bool = true;
    if ((this.mService != null) && (this.mFaceLockActive))
    {
      if (this.mNeedToPendingStopFacelock)
      {
        this.mPendingStopFacelock = true;
        if (DEBUG) {
          Log.d("OPFacelockController", "pending stop facelock");
        }
      }
    }
    else
    {
      Log.d("OPFacelockController", "not stop facelock, active:" + this.mFaceLockActive);
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder().append("handle stopFacelock, hasRunnable:");
    if (this.mEndStopFacelockRunnable != null) {}
    for (;;)
    {
      Log.d("OPFacelockController", bool);
      this.mHandler.removeMessages(8);
      this.mPendingStopFacelock = false;
      this.mFaceLockActive = false;
      registerLightSensor(false);
      updateFacelockLightMode(false);
      try
      {
        this.mService.unregisterCallback(this.mOPFacelockCallback);
        this.mService.stopFaceUnlock(0);
        this.mService.release();
        if (this.mEndStopFacelockRunnable != null)
        {
          this.mEndStopFacelockRunnable.run();
          this.mEndStopFacelockRunnable = null;
        }
        return;
        bool = false;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.e("OPFacelockController", "stopFacelock fail, " + localRemoteException.getMessage());
        }
      }
      finally {}
    }
  }
  
  private void playFacelockIndicationTextAnim()
  {
    if ((this.mPhoneStatusBar == null) || (this.mPhoneStatusBar.isBouncerShowing())) {
      return;
    }
    this.mUIHandler.post(new Runnable()
    {
      public void run()
      {
        OPFacelockController.-get16(OPFacelockController.this).startFacelockFailAnimation();
      }
    });
  }
  
  private void registerLightSensor(boolean paramBoolean)
  {
    if (!this.mUpdateMonitor.isFacelockLightingEnabled()) {
      return;
    }
    if (paramBoolean)
    {
      this.mSensorManager.registerListener(this.mLightSensorListener, this.mLightSensor, this.mSensorRate * 1000, this.mHandler);
      return;
    }
    this.mSensorManager.unregisterListener(this.mLightSensorListener);
  }
  
  private void startRootAnimation()
  {
    this.mLightingModeEnabled = true;
    LSState.getInstance().getStatusBarKeyguardViewManager().updateStates();
    registerLightSensor(false);
    this.mAlphaAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
    this.mAlphaAnimator.setDuration(1000L);
    this.mAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        float f = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        OPFacelockController.-get13(OPFacelockController.this).setAlpha(f);
      }
    });
    this.mAlphaAnimator.addListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator) {}
      
      public void onAnimationEnd(Animator paramAnonymousAnimator) {}
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        OPFacelockController.-get13(OPFacelockController.this).setVisibility(0);
      }
    });
    this.mAlphaAnimator.start();
  }
  
  private void stopFacelock()
  {
    this.mHandler.removeMessages(2);
    this.mHandler.removeMessages(1);
    this.mHandler.sendEmptyMessage(2);
  }
  
  private void unlockKeyguard()
  {
    Log.d("OPFacelockController", "unlockKeyguard, bouncer:" + this.mPhoneStatusBar.isBouncerShowing() + ", live wp:" + this.mStatusBarWindowManager.isShowingLiveWallpaper());
    this.mFailAttempts = 0;
    if ((this.mPhoneStatusBar.isBouncerShowing()) || (this.mStatusBarWindowManager.isShowingLiveWallpaper())) {}
    for (;;)
    {
      this.mUIHandler.post(new Runnable()
      {
        public void run()
        {
          OPFacelockController.-get18(OPFacelockController.this).notifyKeyguardAuthenticated(true);
          if ((OPFacelockController.this.isLighModeEnabled()) && (OPFacelockController.-get13(OPFacelockController.this) != null)) {
            OPFacelockController.-get13(OPFacelockController.this).setAlpha(0.0F);
          }
        }
      });
      this.mUpdateMonitor.notifyFacelockStateChanged(0);
      stopFacelock();
      return;
      this.mUpdateMonitor.onFacelockUnlocking(true);
      try
      {
        if (this.mWM != null) {
          this.mWM.keyguardGoingAway(2);
        }
        updateKeyguardAlpha(0, false);
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.e("OPFacelockController", "keyguardGoingAway fail, " + localRemoteException.getMessage());
        }
      }
    }
  }
  
  private void updateFacelockLightMode(final boolean paramBoolean)
  {
    if (this.mLightView == null) {
      return;
    }
    if ((paramBoolean) && (LSState.getInstance().getPreventModeCtrl().isPreventModeActive()))
    {
      if (DEBUG) {
        Log.e("OPFacelockController", "not enter when prevent mode");
      }
      return;
    }
    if (paramBoolean) {}
    try
    {
      this.mPowerManager.overrideScreenBrightnessRangeMinimum(this.LIGHTING_MODE_BRIGHTNESS);
      for (;;)
      {
        this.mUIHandler.post(new Runnable()
        {
          public void run()
          {
            if (OPFacelockController.-get14(OPFacelockController.this) != paramBoolean) {
              Log.d("OPFacelockController", "set light view to " + paramBoolean);
            }
            if (paramBoolean)
            {
              OPFacelockController.-wrap7(OPFacelockController.this);
              return;
            }
            OPFacelockController.-set3(OPFacelockController.this, false);
            LSState.getInstance().getStatusBarKeyguardViewManager().updateStates();
            OPFacelockController.-get13(OPFacelockController.this).setVisibility(8);
          }
        });
        return;
        this.mPowerManager.overrideScreenBrightnessRangeMinimum(0);
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e("OPFacelockController", "updateFacelockLightMode, overrideScreenBrightness:" + localRemoteException.getMessage());
      }
    }
  }
  
  private void updateIsFaceAdded()
  {
    if (this.mSettingService == null)
    {
      this.mUpdateMonitor.setIsFaceAdded(false);
      bindFacelockSetting();
      return;
    }
    int i = 1;
    try
    {
      int j = this.mSettingService.checkState(0);
      i = j;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.d("OPFacelockController", "updateIsFaceAdded fail: " + localException.getMessage());
        continue;
        boolean bool = false;
        continue;
        if (this.mStatusBarKeyguardViewManager.mBouncer != null)
        {
          this.mUpdateMonitor.setIsFaceAdded(bool);
          this.mStatusBarKeyguardViewManager.mBouncer.updateBouncerPromptReason();
          Log.d("OPFacelockController", "face is added and not allowed, update Prompt reason");
        }
      }
    }
    if (i == 0)
    {
      bool = true;
      if (DEBUG) {
        Log.d("OPFacelockController", "isFaceAdded:" + bool);
      }
      if ((!this.mUpdateMonitor.isFaceAdded()) && (bool) && (!this.mUpdateMonitor.isUnlockingWithFingerprintAllowed())) {
        break label148;
      }
      this.mUpdateMonitor.setIsFaceAdded(bool);
    }
  }
  
  private void updateKeyguardAlpha(final int paramInt, boolean paramBoolean)
  {
    Log.d("OPFacelockController", "update alpha:" + paramInt + ", " + this.mIsScreenOffUnlock + ", live wp:" + this.mStatusBarWindowManager.isShowingLiveWallpaper());
    if ((paramInt == 0) && (paramBoolean)) {
      this.mKeyguardViewMediator.notifyScreenOffAuthenticate(true, KeyguardViewMediator.AUTHENTICATE_FACEUNLOCK);
    }
    this.mUIHandler.post(new Runnable()
    {
      public void run()
      {
        if (OPFacelockController.-get19(OPFacelockController.this).isShowingLiveWallpaper()) {
          return;
        }
        OPFacelockController.-get12(OPFacelockController.this).changePanelAlpha(paramInt, KeyguardViewMediator.AUTHENTICATE_FACEUNLOCK);
        OPFacelockController.-get18(OPFacelockController.this).getViewRootImpl().setReportNextDraw();
      }
    });
    if (paramInt == 1)
    {
      this.mIsScreenOffUnlock = false;
      if (paramBoolean) {
        this.mKeyguardViewMediator.notifyScreenOffAuthenticate(false, KeyguardViewMediator.AUTHENTICATE_FACEUNLOCK);
      }
    }
  }
  
  private void updateNotifyMessage(final int paramInt1, final int paramInt2)
  {
    final int i = this.mUpdateMonitor.getFacelockNotifyMsgId(paramInt1);
    this.mUIHandler.post(new Runnable()
    {
      public void run()
      {
        if ((OPFacelockController.-get8(OPFacelockController.this)) && (paramInt1 == 0)) {
          OPFacelockController.-get15(OPFacelockController.this).setFacelockRunning(paramInt1, false);
        }
        while (paramInt1 == 3)
        {
          OPFacelockController.-get7(OPFacelockController.this).showTransientIndication(" ", paramInt2);
          return;
          OPFacelockController.-get15(OPFacelockController.this).setFacelockRunning(paramInt1, true);
        }
        if (paramInt1 == 2)
        {
          OPFacelockController.-get7(OPFacelockController.this).showTransientIndication(null);
          return;
        }
        if (i > 0) {
          OPFacelockController.-get7(OPFacelockController.this).showTransientIndication(OPFacelockController.-get3(OPFacelockController.this).getString(i), paramInt2);
        }
      }
    });
  }
  
  private void updateRecognizedState(int paramInt1, int paramInt2)
  {
    if (this.mLockout) {
      return;
    }
    this.mUpdateMonitor.notifyFacelockStateChanged(paramInt1);
    updateNotifyMessage(paramInt1, paramInt2);
    if (paramInt1 == 1) {
      this.mLockout = true;
    }
  }
  
  public boolean canLaunchFacelock()
  {
    if (this.mCameraLaunching)
    {
      Log.d("OPFacelockController", "not start when camera launching");
      return false;
    }
    if (!this.mUpdateMonitor.isFacelockAllowed())
    {
      if (DEBUG) {
        Log.d("OPFacelockController", "not allow to facelock");
      }
      return false;
    }
    if (isFacelockTimeout())
    {
      Log.d("OPFacelockController", "not allow to facelock");
      return false;
    }
    return true;
  }
  
  public void disPatchTouchEvent(MotionEvent paramMotionEvent)
  {
    this.mLightView.dispatchTouchEvent(paramMotionEvent);
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.print("  OPFacelockController: ");
    paramPrintWriter.println();
    paramPrintWriter.print("  mFailAttempts: ");
    paramPrintWriter.println(this.mFailAttempts);
    paramPrintWriter.print("  mLockout: ");
    paramPrintWriter.println(this.mLockout);
    paramPrintWriter.print("  mBinding: ");
    paramPrintWriter.println(this.mBinding);
    paramPrintWriter.print("  mCameraLaunching: ");
    paramPrintWriter.println(this.mCameraLaunching);
    paramPrintWriter.print("  mBoundToService: ");
    paramPrintWriter.println(this.mBoundToService);
    paramPrintWriter.print("  mFaceLockActive: ");
    paramPrintWriter.println(this.mFaceLockActive);
    paramPrintWriter.print("  mService: ");
    paramPrintWriter.println(this.mService);
    paramPrintWriter.print("  isFacelockEnabled: ");
    paramPrintWriter.println(this.mUpdateMonitor.isFacelockEnabled());
    paramPrintWriter.print("  isAutoFacelockEnabled: ");
    paramPrintWriter.println(this.mUpdateMonitor.isAutoFacelockEnabled());
    paramPrintWriter.print("  isFacelockLightingEnabled: ");
    paramPrintWriter.println(this.mUpdateMonitor.isFacelockLightingEnabled());
    paramPrintWriter.print("  FacelockRunningType: ");
    paramPrintWriter.println(this.mUpdateMonitor.getFacelockRunningType());
    paramPrintWriter.print("  isFacelockTimeout: ");
    paramPrintWriter.println(isFacelockTimeout());
    paramPrintWriter.print("  isFacelockAllowed: ");
    paramPrintWriter.println(this.mUpdateMonitor.isFacelockAllowed());
    paramPrintWriter.print("  mIsKeyguardShowing: ");
    paramPrintWriter.println(this.mIsKeyguardShowing);
    paramPrintWriter.print("  mIsScreenTurnedOn: ");
    paramPrintWriter.println(this.mIsScreenTurnedOn);
    paramPrintWriter.print("  mNeedToPendingStopFacelock: ");
    paramPrintWriter.println(this.mNeedToPendingStopFacelock);
    paramPrintWriter.print("  mPendingStopFacelock: ");
    paramPrintWriter.println(this.mPendingStopFacelock);
    paramPrintWriter.print("  hasRunnable: ");
    if (this.mEndStopFacelockRunnable != null) {}
    for (boolean bool = true;; bool = false)
    {
      paramPrintWriter.println(bool);
      paramPrintWriter.print("  mIsScreenOffUnlock: ");
      paramPrintWriter.println(this.mIsScreenOffUnlock);
      paramPrintWriter.print("  mStartFacelockWhenScreenOn: ");
      paramPrintWriter.println(this.mStartFacelockWhenScreenOn);
      paramPrintWriter.print("  mIsSleep: ");
      paramPrintWriter.println(this.mIsSleep);
      paramPrintWriter.print("  mLightingModeEnabled: ");
      paramPrintWriter.println(this.mLightingModeEnabled);
      paramPrintWriter.print("  LIGHTING_MODE_SENSOR_THRESHOLD: ");
      paramPrintWriter.println(this.LIGHTING_MODE_SENSOR_THRESHOLD);
      paramPrintWriter.print("  LIGHTING_MODE_BRIGHTNESS: ");
      paramPrintWriter.println(this.LIGHTING_MODE_BRIGHTNESS);
      return;
    }
  }
  
  public boolean isFacelockTimeout()
  {
    boolean bool2 = true;
    boolean bool3 = this.mUpdateMonitor.isUnlockingWithFingerprintAllowed();
    boolean bool1 = bool2;
    if (!this.mLockout)
    {
      bool1 = bool2;
      if (bool3) {
        bool1 = false;
      }
    }
    return bool1;
  }
  
  public boolean isLighModeEnabled()
  {
    return this.mLightingModeEnabled;
  }
  
  public void notifyCameraLaunching(boolean paramBoolean, Runnable paramRunnable)
  {
    if (this.mIsKeyguardShowing) {
      this.mCameraLaunching = paramBoolean;
    }
    if (paramRunnable != null) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      Log.d("OPFacelockController", "notifyCameraLaunching, hasRunnable:" + paramBoolean + ", facelockActive:" + this.mFaceLockActive + ", keyguard:" + this.mIsKeyguardShowing);
      if (this.mFaceLockActive) {
        break;
      }
      if (paramBoolean) {
        paramRunnable.run();
      }
      return;
    }
    if (paramBoolean) {
      this.mEndStopFacelockRunnable = paramRunnable;
    }
    stopFacelock();
  }
  
  public void onClearFailedFacelockAttempts()
  {
    if (DEBUG) {
      Log.d("OPFacelockController", "onClearFailedFacelockAttempts, failed:" + this.mFailAttempts + ", lockout:" + this.mLockout);
    }
    this.mFailAttempts = 0;
    this.mLockout = false;
  }
  
  public void onClick(View paramView)
  {
    if (DEBUG) {
      Log.d("OPFacelockController", "onClick to stop Lighting mode");
    }
    stopFacelockLightMode();
  }
  
  public void onDreamingStateChanged(boolean paramBoolean)
  {
    if (DEBUG) {
      Log.d("OPFacelockController", "onDreamingStateChanged, " + paramBoolean);
    }
  }
  
  public void onFinishedGoingToSleep(int paramInt)
  {
    if (DEBUG) {
      Log.d("OPFacelockController", "onFinishedGoingToSleep, " + paramInt);
    }
    this.mIsGoingToSleep = false;
    this.LIGHTING_MODE_SENSOR_THRESHOLD = SystemProperties.getInt("persist.sys.facelock.lsensor", 0);
    this.LIGHTING_MODE_BRIGHTNESS = SystemProperties.getInt("persist.sys.facelock.bright", 300);
  }
  
  public void onKeyguardBouncerChanged(boolean paramBoolean)
  {
    if (DEBUG) {
      Log.d("OPFacelockController", "onKeyguardBouncerChanged , bouncer:" + paramBoolean);
    }
  }
  
  public void onKeyguardReset()
  {
    if ((this.mBoundToService) && (canLaunchFacelock()))
    {
      if (this.mIsScreenTurnedOn)
      {
        if (DEBUG) {
          Log.d("OPFacelockController", "onKeyguardReset");
        }
        this.mHandler.removeMessages(2);
        this.mHandler.removeMessages(1);
        this.mHandler.sendEmptyMessage(1);
      }
      return;
    }
  }
  
  public void onKeyguardVisibilityChanged(boolean paramBoolean)
  {
    if (DEBUG) {
      Log.d("OPFacelockController", "onKeyguardVisibilityChanged, show:" + paramBoolean + ", bound:" + this.mBoundToService);
    }
    if (this.mIsKeyguardShowing == paramBoolean) {
      return;
    }
    if (!this.mBoundToService) {
      bindFacelock();
    }
    updateIsFaceAdded();
    if (paramBoolean) {
      if ((!this.mIsKeyguardShowing) && (this.mBoundToService) && (canLaunchFacelock()))
      {
        this.mHandler.removeMessages(2);
        this.mHandler.removeMessages(1);
        this.mHandler.sendEmptyMessage(1);
      }
    }
    for (;;)
    {
      this.mIsKeyguardShowing = paramBoolean;
      return;
      this.mStartFacelockWhenScreenOn = false;
      this.mCameraLaunching = false;
      this.mNeedToPendingStopFacelock = false;
      this.mHandler.removeMessages(1);
      this.mHandler.sendEmptyMessage(2);
    }
  }
  
  public void onPasswordLockout()
  {
    if (DEBUG) {
      Log.d("OPFacelockController", "onPasswordLockout");
    }
    stopFacelock();
  }
  
  public void onPreStartedGoingToSleep()
  {
    if (DEBUG) {
      Log.d("OPFacelockController", "onPreStartedGoingToSleep");
    }
    this.mIsSleep = true;
  }
  
  public void onPreStartedWakingUp()
  {
    Log.d("OPFacelockController", "onPreStartedWakingUp, bound:" + this.mBoundToService);
    this.mIsSleep = false;
    if ((this.mBoundToService) && (canLaunchFacelock()))
    {
      this.mHandler.removeMessages(2);
      this.mHandler.removeMessages(1);
      this.mHandler.sendEmptyMessage(1);
      return;
    }
  }
  
  public void onScreenTurnedOff()
  {
    if (DEBUG) {
      Log.d("OPFacelockController", "onScreenTurnedOff");
    }
    this.mIsScreenTurnedOn = false;
  }
  
  public void onScreenTurnedOn()
  {
    if (DEBUG) {
      Log.d("OPFacelockController", "onScreenTurnedOn, " + this.mStartFacelockWhenScreenOn + ", " + this.mIsSleep);
    }
    this.mIsScreenTurnedOn = true;
    if (this.mStartFacelockWhenScreenOn)
    {
      this.mStartFacelockWhenScreenOn = false;
      if (!canLaunchFacelock()) {
        return;
      }
      this.mIsSleep = false;
      if (this.mBoundToService)
      {
        this.mHandler.removeMessages(2);
        this.mHandler.removeMessages(1);
        this.mHandler.sendEmptyMessage(1);
      }
    }
  }
  
  public void onStartedGoingToSleep(int paramInt)
  {
    if (DEBUG) {
      Log.d("OPFacelockController", "onStartedGoingToSleep, " + paramInt + ", bound:" + this.mBoundToService);
    }
    this.mIsGoingToSleep = true;
    this.mStartFacelockWhenScreenOn = false;
    this.mCameraLaunching = false;
    this.mIsSleep = true;
    this.mHandler.removeMessages(2);
    this.mHandler.removeMessages(1);
    this.mHandler.sendEmptyMessage(2);
  }
  
  public void onStartedWakingUp()
  {
    Log.d("OPFacelockController", "onStartedWakingUp, bound:" + this.mBoundToService + ", lockout:" + this.mLockout);
    this.mIsSleep = false;
    if ((this.mBoundToService) && (canLaunchFacelock()))
    {
      this.mHandler.removeMessages(2);
      this.mHandler.removeMessages(1);
      this.mHandler.sendEmptyMessage(1);
      return;
    }
  }
  
  public void onSystemReady()
  {
    if (DEBUG) {
      Log.d("OPFacelockController", "onSystemReady");
    }
    bindFacelock();
    bindFacelockSetting();
  }
  
  public void resetFacelockPending()
  {
    this.mNeedToPendingStopFacelock = false;
    stopFacelock();
  }
  
  public void setClockY(float paramFloat)
  {
    if (this.mLightView != null) {
      this.mLightView.setClockY(paramFloat);
    }
  }
  
  public void stopFacelockLightMode()
  {
    registerLightSensor(false);
    updateFacelockLightMode(false);
  }
  
  public boolean tryToStartFaceLock()
  {
    Log.d("OPFacelockController", "tryToStartFaceLock, bound:" + this.mBoundToService);
    if (!canLaunchFacelock()) {
      return false;
    }
    if (this.mBoundToService)
    {
      this.mHandler.removeMessages(2);
      this.mHandler.removeMessages(1);
      this.mHandler.sendEmptyMessage(1);
    }
    return true;
  }
  
  public void tryToStartFaceLockAfterScreenOn()
  {
    if (DEBUG) {
      Log.d("OPFacelockController", "tryToStartFaceLockAfterScreenOn");
    }
    this.mStartFacelockWhenScreenOn = true;
  }
  
  private class FacelockHandler
    extends Handler
  {
    FacelockHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (OPFacelockController.-get0()) {
        Log.d("OPFacelockController", "handleMessage: what:" + paramMessage.what + ", bound:" + OPFacelockController.-get2(OPFacelockController.this) + ", active:" + OPFacelockController.-get4(OPFacelockController.this));
      }
      if (!OPFacelockController.-get2(OPFacelockController.this)) {
        return;
      }
      switch (paramMessage.what)
      {
      default: 
        Log.e("OPFacelockController", "Unhandled message");
      }
      for (;;)
      {
        if (OPFacelockController.-get0()) {
          Log.d("OPFacelockController", "handleMessage: done");
        }
        return;
        OPFacelockController.-wrap4(OPFacelockController.this);
        continue;
        OPFacelockController.-wrap12(OPFacelockController.this, 0, -1);
        OPFacelockController.-wrap5(OPFacelockController.this);
        continue;
        if (!OPFacelockController.-get4(OPFacelockController.this)) {
          return;
        }
        OPFacelockController.-wrap8(OPFacelockController.this);
        continue;
        if (!OPFacelockController.-get4(OPFacelockController.this)) {
          return;
        }
        OPFacelockController.-wrap0(OPFacelockController.this);
        continue;
        if (!OPFacelockController.-get4(OPFacelockController.this)) {
          return;
        }
        OPFacelockController.-wrap6(OPFacelockController.this);
        OPFacelockController.-wrap12(OPFacelockController.this, 6, -65536);
        OPFacelockController.-wrap5(OPFacelockController.this);
        continue;
        OPFacelockController.-wrap2(OPFacelockController.this);
        continue;
        if (!OPFacelockController.-get4(OPFacelockController.this)) {
          return;
        }
        OPFacelockController.-wrap3(OPFacelockController.this);
        continue;
        OPFacelockController.-wrap1(OPFacelockController.this);
        continue;
        Log.d("OPFacelockController", "reset screen on, offUnlock:" + OPFacelockController.-get10(OPFacelockController.this));
        if (!OPFacelockController.-get10(OPFacelockController.this)) {
          return;
        }
        OPFacelockController.-wrap11(OPFacelockController.this, 1, true);
        continue;
        if (!OPFacelockController.-get4(OPFacelockController.this)) {
          return;
        }
        OPFacelockController.-wrap6(OPFacelockController.this);
        OPFacelockController.-wrap12(OPFacelockController.this, 8, -65536);
        OPFacelockController.-wrap5(OPFacelockController.this);
        continue;
        if (!OPFacelockController.-get4(OPFacelockController.this)) {
          return;
        }
        OPFacelockController.-wrap6(OPFacelockController.this);
        OPFacelockController.-wrap12(OPFacelockController.this, 9, -65536);
        OPFacelockController.-wrap5(OPFacelockController.this);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\OPFacelockController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */