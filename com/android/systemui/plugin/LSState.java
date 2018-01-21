package com.android.systemui.plugin;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.statusbar.phone.FingerprintUnlockController;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;

public class LSState
  implements BaseCtrl.ControlCallback
{
  private static LSState sInstance;
  private final boolean DEBUG = true;
  private final String TAG = "LSState";
  private final int WHAT_UI_INIT = 1;
  private ViewGroup mContainer;
  private Context mContext;
  public final BaseCtrl[] mControls = { this.mPreventModeCtrl, this.mDozeModeCtrl };
  private final DozeModeCtrl mDozeModeCtrl = new DozeModeCtrl();
  private FingerprintUnlockController mFingerprintUnlockControl;
  private boolean mInit = false;
  private boolean mIsFinishedScreenTuredOn = false;
  private KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback = new KeyguardUpdateMonitorCallback()
  {
    public void onFinishedGoingToSleep(int paramAnonymousInt)
    {
      BaseCtrl[] arrayOfBaseCtrl = LSState.this.mControls;
      int i = 0;
      int j = arrayOfBaseCtrl.length;
      while (i < j)
      {
        BaseCtrl localBaseCtrl = arrayOfBaseCtrl[i];
        if ((localBaseCtrl != null) && (localBaseCtrl.isEnable())) {
          localBaseCtrl.onFinishedGoingToSleep(paramAnonymousInt);
        }
        i += 1;
      }
    }
    
    public void onKeyguardBouncerChanged(boolean paramAnonymousBoolean)
    {
      BaseCtrl[] arrayOfBaseCtrl = LSState.this.mControls;
      int i = 0;
      int j = arrayOfBaseCtrl.length;
      while (i < j)
      {
        BaseCtrl localBaseCtrl = arrayOfBaseCtrl[i];
        if ((localBaseCtrl != null) && (localBaseCtrl.isEnable())) {
          localBaseCtrl.onKeyguardBouncerChanged(paramAnonymousBoolean);
        }
        i += 1;
      }
    }
    
    public void onKeyguardVisibilityChanged(boolean paramAnonymousBoolean)
    {
      BaseCtrl[] arrayOfBaseCtrl = LSState.this.mControls;
      int i = 0;
      int j = arrayOfBaseCtrl.length;
      while (i < j)
      {
        BaseCtrl localBaseCtrl = arrayOfBaseCtrl[i];
        if ((localBaseCtrl != null) && (localBaseCtrl.isEnable())) {
          localBaseCtrl.onKeyguardVisibilityChanged(paramAnonymousBoolean);
        }
        i += 1;
      }
    }
    
    public void onScreenTurnedOff()
    {
      int i = 0;
      LSState.-set0(LSState.this, false);
      BaseCtrl[] arrayOfBaseCtrl = LSState.this.mControls;
      int j = arrayOfBaseCtrl.length;
      while (i < j)
      {
        BaseCtrl localBaseCtrl = arrayOfBaseCtrl[i];
        if ((localBaseCtrl != null) && (localBaseCtrl.isEnable())) {
          localBaseCtrl.onScreenTurnedOff();
        }
        i += 1;
      }
    }
    
    public void onStartedGoingToSleep(int paramAnonymousInt)
    {
      int i = 0;
      LSState.-set0(LSState.this, false);
      BaseCtrl[] arrayOfBaseCtrl = LSState.this.mControls;
      int j = arrayOfBaseCtrl.length;
      while (i < j)
      {
        BaseCtrl localBaseCtrl = arrayOfBaseCtrl[i];
        if ((localBaseCtrl != null) && (localBaseCtrl.isEnable())) {
          localBaseCtrl.onStartedGoingToSleep(paramAnonymousInt);
        }
        i += 1;
      }
    }
    
    public void onStartedWakingUp()
    {
      LSState.-set0(LSState.this, true);
      BaseCtrl[] arrayOfBaseCtrl = LSState.this.mControls;
      int i = 0;
      int j = arrayOfBaseCtrl.length;
      while (i < j)
      {
        BaseCtrl localBaseCtrl = arrayOfBaseCtrl[i];
        if ((localBaseCtrl != null) && (localBaseCtrl.isEnable())) {
          localBaseCtrl.onStartedWakingUp();
        }
        i += 1;
      }
    }
  };
  private Looper mNonUiLooper;
  private PhoneStatusBar mPhonstatusBar;
  private final PreventModeCtrl mPreventModeCtrl = new PreventModeCtrl();
  private StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
  private Handler mUIHandler = new MyUIHandler(null);
  private KeyguardUpdateMonitor mUpdateMonitor;
  
  public static LSState getInstance()
  {
    try
    {
      if (sInstance == null) {
        sInstance = new LSState();
      }
      LSState localLSState = sInstance;
      return localLSState;
    }
    finally {}
  }
  
  public ViewGroup getContainer()
  {
    return this.mContainer;
  }
  
  public FingerprintUnlockController getFingerprintUnlockControl()
  {
    return this.mFingerprintUnlockControl;
  }
  
  public Looper getNonUILooper()
  {
    try
    {
      if (this.mNonUiLooper == null)
      {
        localObject1 = new HandlerThread("LSState thread");
        ((HandlerThread)localObject1).start();
        this.mNonUiLooper = ((HandlerThread)localObject1).getLooper();
      }
      Object localObject1 = this.mNonUiLooper;
      return (Looper)localObject1;
    }
    finally {}
  }
  
  public PhoneStatusBar getPhoneStatusBar()
  {
    return this.mPhonstatusBar;
  }
  
  public PreventModeCtrl getPreventModeCtrl()
  {
    return this.mPreventModeCtrl;
  }
  
  public StatusBarKeyguardViewManager getStatusBarKeyguardViewManager()
  {
    return this.mStatusBarKeyguardViewManager;
  }
  
  public void init(Context paramContext, ViewGroup paramViewGroup, PhoneStatusBar paramPhoneStatusBar)
  {
    for (;;)
    {
      int i;
      try
      {
        if (!this.mInit)
        {
          Log.d("LSState", "init");
          this.mContainer = paramViewGroup;
          this.mUpdateMonitor = KeyguardUpdateMonitor.getInstance(paramContext);
          this.mPhonstatusBar = paramPhoneStatusBar;
          this.mUpdateMonitor.hasBootCompleted();
          this.mInit = true;
          this.mContext = paramContext;
          getNonUILooper();
          paramViewGroup = this.mControls;
          i = 0;
          int j = paramViewGroup.length;
          if (i < j)
          {
            paramPhoneStatusBar = paramViewGroup[i];
            if (paramPhoneStatusBar != null)
            {
              paramPhoneStatusBar.setCallback(this);
              paramPhoneStatusBar.init(paramContext);
              paramPhoneStatusBar.startCtrl();
            }
          }
          else
          {
            this.mUpdateMonitor.registerCallback(this.mKeyguardUpdateMonitorCallback);
          }
        }
        else
        {
          return;
        }
      }
      finally {}
      i += 1;
    }
  }
  
  public boolean isFinishedScreenTuredOn()
  {
    return this.mIsFinishedScreenTuredOn;
  }
  
  public boolean isHighHintShow()
  {
    if (this.mPhonstatusBar != null) {
      return this.mPhonstatusBar.isHighlightHintViewShowing();
    }
    return false;
  }
  
  public void onBackKeyDown()
  {
    BaseCtrl[] arrayOfBaseCtrl = this.mControls;
    int i = 0;
    int j = arrayOfBaseCtrl.length;
    while (i < j)
    {
      BaseCtrl localBaseCtrl = arrayOfBaseCtrl[i];
      if ((localBaseCtrl != null) && (localBaseCtrl.isEnable())) {
        localBaseCtrl.onBackKeyDown();
      }
      i += 1;
    }
  }
  
  public void onBackPressed()
  {
    BaseCtrl[] arrayOfBaseCtrl = this.mControls;
    int i = 0;
    int j = arrayOfBaseCtrl.length;
    while (i < j)
    {
      BaseCtrl localBaseCtrl = arrayOfBaseCtrl[i];
      if ((localBaseCtrl != null) && (localBaseCtrl.isEnable())) {
        localBaseCtrl.onBackPressed();
      }
      i += 1;
    }
  }
  
  public void onDozePulsing()
  {
    BaseCtrl[] arrayOfBaseCtrl = this.mControls;
    int i = 0;
    int j = arrayOfBaseCtrl.length;
    while (i < j)
    {
      BaseCtrl localBaseCtrl = arrayOfBaseCtrl[i];
      if ((localBaseCtrl != null) && (localBaseCtrl.isEnable())) {
        localBaseCtrl.onDozePulsing();
      }
      i += 1;
    }
  }
  
  public void onDreamingStarted()
  {
    BaseCtrl[] arrayOfBaseCtrl = this.mControls;
    int i = 0;
    int j = arrayOfBaseCtrl.length;
    while (i < j)
    {
      BaseCtrl localBaseCtrl = arrayOfBaseCtrl[i];
      if ((localBaseCtrl != null) && (localBaseCtrl.isEnable())) {
        localBaseCtrl.onDreamingStarted();
      }
      i += 1;
    }
  }
  
  public void onDreamingStopped()
  {
    BaseCtrl[] arrayOfBaseCtrl = this.mControls;
    int i = 0;
    int j = arrayOfBaseCtrl.length;
    while (i < j)
    {
      BaseCtrl localBaseCtrl = arrayOfBaseCtrl[i];
      if ((localBaseCtrl != null) && (localBaseCtrl.isEnable())) {
        localBaseCtrl.onDreamingStopped();
      }
      i += 1;
    }
  }
  
  public void onFingerprintStartedGoingToSleep()
  {
    this.mIsFinishedScreenTuredOn = false;
  }
  
  public void onKeyguardDone(boolean paramBoolean)
  {
    BaseCtrl[] arrayOfBaseCtrl = this.mControls;
    int i = 0;
    int j = arrayOfBaseCtrl.length;
    while (i < j)
    {
      BaseCtrl localBaseCtrl = arrayOfBaseCtrl[i];
      if ((localBaseCtrl != null) && (localBaseCtrl.isEnable())) {
        localBaseCtrl.onKeyguardDone(paramBoolean);
      }
      i += 1;
    }
  }
  
  public void onScreenTurnedOn()
  {
    this.mIsFinishedScreenTuredOn = true;
    BaseCtrl[] arrayOfBaseCtrl = this.mControls;
    int i = 0;
    int j = arrayOfBaseCtrl.length;
    while (i < j)
    {
      BaseCtrl localBaseCtrl = arrayOfBaseCtrl[i];
      if ((localBaseCtrl != null) && (localBaseCtrl.isEnable())) {
        localBaseCtrl.onScreenTurnedOn();
      }
      i += 1;
    }
  }
  
  public void onVolumeKeyPressed(KeyEvent paramKeyEvent)
  {
    BaseCtrl[] arrayOfBaseCtrl = this.mControls;
    int i = 0;
    int j = arrayOfBaseCtrl.length;
    while (i < j)
    {
      BaseCtrl localBaseCtrl = arrayOfBaseCtrl[i];
      if ((localBaseCtrl != null) && (localBaseCtrl.isEnable())) {
        localBaseCtrl.onVolumeKeyPressed(paramKeyEvent);
      }
      i += 1;
    }
  }
  
  public void onWallpaperChange(Bitmap paramBitmap)
  {
    BaseCtrl[] arrayOfBaseCtrl = this.mControls;
    int i = 0;
    int j = arrayOfBaseCtrl.length;
    while (i < j)
    {
      BaseCtrl localBaseCtrl = arrayOfBaseCtrl[i];
      if ((localBaseCtrl != null) && (localBaseCtrl.isEnable())) {
        localBaseCtrl.onWallpaperChange(paramBitmap);
      }
      i += 1;
    }
    this.mPhonstatusBar.onWallpaperChange(paramBitmap);
  }
  
  public void setFingerprintUnlockControl(FingerprintUnlockController paramFingerprintUnlockController)
  {
    this.mFingerprintUnlockControl = paramFingerprintUnlockController;
  }
  
  public void setStatusBarKeyguardViewManager(StatusBarKeyguardViewManager paramStatusBarKeyguardViewManager)
  {
    this.mStatusBarKeyguardViewManager = paramStatusBarKeyguardViewManager;
  }
  
  private class MyUIHandler
    extends Handler
  {
    private MyUIHandler() {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      paramMessage = LSState.this;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\plugin\LSState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */