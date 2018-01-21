package com.android.systemui.statusbar.phone;

import android.app.ActivityManagerNative;
import android.app.ActivityOptions;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.UserHandle;
import android.telecom.TelecomManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.EmergencyButton;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.EventLogTags;
import com.android.systemui.Interpolators;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.statusbar.KeyguardAffordanceView;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.policy.AccessibilityController;
import com.android.systemui.statusbar.policy.AccessibilityController.AccessibilityStateChangedCallback;
import com.android.systemui.statusbar.policy.FlashlightController;
import com.android.systemui.statusbar.policy.PreviewInflater;
import com.android.systemui.util.MdmLogger;

public class KeyguardBottomAreaView
  extends FrameLayout
  implements View.OnClickListener, UnlockMethodCache.OnUnlockMethodChangedListener, AccessibilityController.AccessibilityStateChangedCallback, View.OnLongClickListener
{
  public static final Intent INSECURE_CAMERA_INTENT = new Intent("android.media.action.STILL_IMAGE_CAMERA");
  private static final Intent PHONE_INTENT = new Intent("android.intent.action.DIAL");
  private static final Intent SECURE_CAMERA_INTENT = new Intent("android.media.action.STILL_IMAGE_CAMERA_SECURE").addFlags(8388608);
  private AccessibilityController mAccessibilityController;
  private View.AccessibilityDelegate mAccessibilityDelegate = new View.AccessibilityDelegate()
  {
    public void onInitializeAccessibilityNodeInfo(View paramAnonymousView, AccessibilityNodeInfo paramAnonymousAccessibilityNodeInfo)
    {
      super.onInitializeAccessibilityNodeInfo(paramAnonymousView, paramAnonymousAccessibilityNodeInfo);
      String str = null;
      if (paramAnonymousView == KeyguardBottomAreaView.-get5(KeyguardBottomAreaView.this)) {
        str = KeyguardBottomAreaView.this.getResources().getString(2131690095);
      }
      for (;;)
      {
        paramAnonymousAccessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, str));
        return;
        if (paramAnonymousView == KeyguardBottomAreaView.-get2(KeyguardBottomAreaView.this)) {
          str = KeyguardBottomAreaView.this.getResources().getString(2131690098);
        } else if (paramAnonymousView == KeyguardBottomAreaView.-get3(KeyguardBottomAreaView.this)) {
          if (KeyguardBottomAreaView.-get4(KeyguardBottomAreaView.this)) {
            str = KeyguardBottomAreaView.this.getResources().getString(2131690097);
          } else {
            str = KeyguardBottomAreaView.this.getResources().getString(2131690096);
          }
        }
      }
    }
    
    public boolean performAccessibilityAction(View paramAnonymousView, int paramAnonymousInt, Bundle paramAnonymousBundle)
    {
      if (paramAnonymousInt == 16)
      {
        if (paramAnonymousView == KeyguardBottomAreaView.-get5(KeyguardBottomAreaView.this))
        {
          KeyguardBottomAreaView.-get6(KeyguardBottomAreaView.this).animateCollapsePanels(2, true);
          return true;
        }
        if (paramAnonymousView == KeyguardBottomAreaView.-get2(KeyguardBottomAreaView.this))
        {
          KeyguardBottomAreaView.this.launchCamera("lockscreen_affordance");
          return true;
        }
        if (paramAnonymousView == KeyguardBottomAreaView.-get3(KeyguardBottomAreaView.this))
        {
          KeyguardBottomAreaView.this.launchLeftAffordance();
          return true;
        }
      }
      return super.performAccessibilityAction(paramAnonymousView, paramAnonymousInt, paramAnonymousBundle);
    }
  };
  private ActivityStarter mActivityStarter;
  private KeyguardAffordanceHelper mAffordanceHelper;
  private AssistManager mAssistManager;
  private KeyguardAffordanceView mCameraImageView;
  private View mCameraPreview;
  private ImageView mDashView;
  private final BroadcastReceiver mDevicePolicyReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      KeyguardBottomAreaView.this.post(new Runnable()
      {
        public void run()
        {
          KeyguardBottomAreaView.-wrap2(KeyguardBottomAreaView.this);
        }
      });
    }
  };
  private EmergencyButton mEmergencyButton;
  private FlashlightController mFlashlightController;
  private KeyguardIndicationController mIndicationController;
  private View mIndicationHost;
  private TextView mIndicationLevelText;
  private TextView mIndicationText;
  private KeyguardAffordanceView mLeftAffordanceView;
  private boolean mLeftIsVoiceAssist;
  private View mLeftPreview;
  private LockIcon mLockIcon;
  private LockPatternUtils mLockPatternUtils;
  private PhoneStatusBar mPhoneStatusBar;
  private ViewGroup mPreviewContainer;
  private PreviewInflater mPreviewInflater;
  private boolean mPrewarmBound;
  private final ServiceConnection mPrewarmConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      KeyguardBottomAreaView.-set0(KeyguardBottomAreaView.this, new Messenger(paramAnonymousIBinder));
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      KeyguardBottomAreaView.-set0(KeyguardBottomAreaView.this, null);
    }
  };
  private Messenger mPrewarmMessenger;
  private UnlockMethodCache mUnlockMethodCache;
  private final KeyguardUpdateMonitorCallback mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback()
  {
    public void onFingerprintRunningStateChanged(boolean paramAnonymousBoolean)
    {
      KeyguardBottomAreaView.-get5(KeyguardBottomAreaView.this).update();
    }
    
    public void onFinishedGoingToSleep(int paramAnonymousInt)
    {
      KeyguardBottomAreaView.-get5(KeyguardBottomAreaView.this).setDeviceInteractive(false);
    }
    
    public void onKeyguardVisibilityChanged(boolean paramAnonymousBoolean)
    {
      KeyguardBottomAreaView.-get5(KeyguardBottomAreaView.this).update();
    }
    
    public void onScreenTurnedOff()
    {
      KeyguardBottomAreaView.-get5(KeyguardBottomAreaView.this).setScreenOn(false);
    }
    
    public void onScreenTurnedOn()
    {
      KeyguardBottomAreaView.-get5(KeyguardBottomAreaView.this).setScreenOn(true);
    }
    
    public void onStartedWakingUp()
    {
      KeyguardBottomAreaView.-get5(KeyguardBottomAreaView.this).setDeviceInteractive(true);
    }
    
    public void onStrongAuthStateChanged(int paramAnonymousInt)
    {
      KeyguardBottomAreaView.-get5(KeyguardBottomAreaView.this).update();
    }
    
    public void onUserSwitchComplete(int paramAnonymousInt)
    {
      KeyguardBottomAreaView.-wrap2(KeyguardBottomAreaView.this);
    }
    
    public void onUserUnlocked()
    {
      KeyguardBottomAreaView.-wrap1(KeyguardBottomAreaView.this);
      KeyguardBottomAreaView.-wrap2(KeyguardBottomAreaView.this);
      KeyguardBottomAreaView.this.updateLeftAffordance();
    }
  };
  private boolean mUserSetupComplete;
  
  public KeyguardBottomAreaView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public KeyguardBottomAreaView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public KeyguardBottomAreaView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public KeyguardBottomAreaView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  private boolean canLaunchVoiceAssist()
  {
    return this.mAssistManager.canVoiceAssistBeLaunchedFromKeyguard();
  }
  
  private Intent getCameraIntent()
  {
    boolean bool = KeyguardUpdateMonitor.getInstance(this.mContext).getUserCanSkipBouncer(KeyguardUpdateMonitor.getCurrentUser());
    if ((!this.mLockPatternUtils.isSecure(KeyguardUpdateMonitor.getCurrentUser())) || (bool)) {
      return INSECURE_CAMERA_INTENT;
    }
    return SECURE_CAMERA_INTENT;
  }
  
  private void handleTrustCircleClick()
  {
    EventLogTags.writeSysuiLockscreenGesture(6, 0, 0);
    if ((this.mPhoneStatusBar != null) && (this.mPhoneStatusBar.getFacelockController() != null) && (this.mPhoneStatusBar.getFacelockController().tryToStartFaceLock())) {
      return;
    }
    this.mIndicationController.showTransientIndication(2131690422);
    this.mLockPatternUtils.requireCredentialEntry(KeyguardUpdateMonitor.getCurrentUser());
  }
  
  private void inflateCameraPreview()
  {
    int j = 0;
    View localView = this.mCameraPreview;
    int i = 0;
    if (localView != null)
    {
      this.mPreviewContainer.removeView(localView);
      if (localView.getVisibility() == 0) {
        i = 1;
      }
    }
    else
    {
      this.mCameraPreview = this.mPreviewInflater.inflatePreview(getCameraIntent());
      if (this.mCameraPreview != null)
      {
        this.mPreviewContainer.addView(this.mCameraPreview);
        localView = this.mCameraPreview;
        if (i == 0) {
          break label99;
        }
      }
    }
    label99:
    for (i = j;; i = 4)
    {
      localView.setVisibility(i);
      if (this.mAffordanceHelper != null) {
        this.mAffordanceHelper.updatePreviews();
      }
      return;
      i = 0;
      break;
    }
  }
  
  private void initAccessibility()
  {
    this.mLockIcon.setAccessibilityDelegate(this.mAccessibilityDelegate);
    this.mLeftAffordanceView.setAccessibilityDelegate(this.mAccessibilityDelegate);
    this.mCameraImageView.setAccessibilityDelegate(this.mAccessibilityDelegate);
  }
  
  private boolean isPhoneVisible()
  {
    boolean bool2 = false;
    PackageManager localPackageManager = this.mContext.getPackageManager();
    boolean bool1 = bool2;
    if (localPackageManager.hasSystemFeature("android.hardware.telephony"))
    {
      bool1 = bool2;
      if (localPackageManager.resolveActivity(PHONE_INTENT, 0) != null) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private static boolean isSuccessfulLaunch(int paramInt)
  {
    if ((paramInt == 0) || (paramInt == 3)) {}
    while (paramInt == 2) {
      return true;
    }
    return false;
  }
  
  private void launchPhone()
  {
    final TelecomManager localTelecomManager = TelecomManager.from(this.mContext);
    if (localTelecomManager.isInCall())
    {
      AsyncTask.execute(new Runnable()
      {
        public void run()
        {
          localTelecomManager.showInCallScreen(false);
        }
      });
      return;
    }
    this.mActivityStarter.startActivity(PHONE_INTENT, false);
  }
  
  private void launchVoiceAssist()
  {
    Runnable local7 = new Runnable()
    {
      public void run()
      {
        KeyguardBottomAreaView.-get1(KeyguardBottomAreaView.this).launchVoiceAssistFromKeyguard();
        KeyguardBottomAreaView.-get0(KeyguardBottomAreaView.this).preventNextAnimation();
      }
    };
    if (this.mPhoneStatusBar.isKeyguardCurrentlySecure())
    {
      AsyncTask.execute(local7);
      return;
    }
    this.mPhoneStatusBar.executeRunnableDismissingKeyguard(local7, null, false, false, true);
  }
  
  private void startFinishDozeAnimationElement(View paramView, long paramLong)
  {
    paramView.setAlpha(0.0F);
    paramView.setTranslationY(paramView.getHeight() / 2);
    paramView.animate().alpha(1.0F).translationY(0.0F).setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN).setStartDelay(paramLong).setDuration(250L);
  }
  
  private void updateCameraVisibility()
  {
    int j = 0;
    if (this.mCameraImageView == null) {
      return;
    }
    Object localObject = resolveCameraIntent();
    boolean bool;
    if ((this.mPhoneStatusBar == null) || (this.mPhoneStatusBar.isCameraAllowedByAdmin()))
    {
      i = 0;
      if ((i != 0) || (localObject == null) || (!getResources().getBoolean(2131558415))) {
        break label86;
      }
      bool = this.mUserSetupComplete;
      label62:
      localObject = this.mCameraImageView;
      if (!bool) {
        break label91;
      }
    }
    label86:
    label91:
    for (int i = j;; i = 8)
    {
      ((KeyguardAffordanceView)localObject).setVisibility(i);
      return;
      i = 1;
      break;
      bool = false;
      break label62;
    }
  }
  
  private void updateEmergencyButton()
  {
    if ((this.mContext.getResources().getBoolean(2131558461)) && (this.mEmergencyButton != null)) {
      this.mEmergencyButton.updateEmergencyCallButton();
    }
  }
  
  private void updateLeftAffordanceIcon()
  {
    this.mLeftIsVoiceAssist = canLaunchVoiceAssist();
    boolean bool = this.mUserSetupComplete;
    int j;
    int i;
    KeyguardAffordanceView localKeyguardAffordanceView;
    if (this.mLeftIsVoiceAssist)
    {
      j = 2130837755;
      i = 2131690091;
      localKeyguardAffordanceView = this.mLeftAffordanceView;
      if (!bool) {
        break label99;
      }
    }
    label99:
    for (int k = 0;; k = 8)
    {
      localKeyguardAffordanceView.setVisibility(k);
      this.mLeftAffordanceView.setImageDrawable(this.mContext.getDrawable(j));
      this.mLeftAffordanceView.setContentDescription(this.mContext.getString(i));
      return;
      bool &= isPhoneVisible();
      j = 2130837764;
      i = 2131690090;
      break;
    }
  }
  
  private void updateLeftPreview()
  {
    View localView = this.mLeftPreview;
    if (localView != null) {
      this.mPreviewContainer.removeView(localView);
    }
    if (this.mLeftIsVoiceAssist) {}
    for (this.mLeftPreview = this.mPreviewInflater.inflatePreviewFromService(this.mAssistManager.getVoiceInteractorComponentName());; this.mLeftPreview = this.mPreviewInflater.inflatePreview(PHONE_INTENT))
    {
      if (this.mLeftPreview != null)
      {
        this.mPreviewContainer.addView(this.mLeftPreview);
        this.mLeftPreview.setVisibility(4);
      }
      if (this.mAffordanceHelper != null) {
        this.mAffordanceHelper.updatePreviews();
      }
      return;
    }
  }
  
  private void watchForCameraPolicyChanges()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
    getContext().registerReceiverAsUser(this.mDevicePolicyReceiver, UserHandle.ALL, localIntentFilter, null, null);
    KeyguardUpdateMonitor.getInstance(this.mContext).registerCallback(this.mUpdateMonitorCallback);
  }
  
  public void bindCameraPrewarmService()
  {
    Object localObject = getCameraIntent();
    localObject = PreviewInflater.getTargetActivityInfo(this.mContext, (Intent)localObject, KeyguardUpdateMonitor.getCurrentUser(), true);
    String str;
    Intent localIntent;
    if ((localObject != null) && (((ActivityInfo)localObject).metaData != null))
    {
      str = ((ActivityInfo)localObject).metaData.getString("android.media.still_image_camera_preview_service");
      if (str != null)
      {
        localIntent = new Intent();
        localIntent.setClassName(((ActivityInfo)localObject).packageName, str);
        localIntent.setAction("android.service.media.CameraPrewarmService.ACTION_PREWARM");
      }
    }
    try
    {
      if (getContext().bindServiceAsUser(localIntent, this.mPrewarmConnection, 67108865, new UserHandle(-2))) {
        this.mPrewarmBound = true;
      }
      return;
    }
    catch (SecurityException localSecurityException)
    {
      Log.w("PhoneStatusBar/KeyguardBottomAreaView", "Unable to bind to prewarm service package=" + ((ActivityInfo)localObject).packageName + " class=" + str, localSecurityException);
    }
  }
  
  public View getDashView()
  {
    return this.mDashView;
  }
  
  public View getIndicationView()
  {
    return this.mIndicationText;
  }
  
  public View getIndicationViewHost()
  {
    return this.mIndicationHost;
  }
  
  public View getLeftPreview()
  {
    return this.mLeftPreview;
  }
  
  public KeyguardAffordanceView getLeftView()
  {
    return this.mLeftAffordanceView;
  }
  
  public LockIcon getLockIcon()
  {
    return this.mLockIcon;
  }
  
  public View getRightPreview()
  {
    return this.mCameraPreview;
  }
  
  public KeyguardAffordanceView getRightView()
  {
    return this.mCameraImageView;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public boolean isLeftVoiceAssist()
  {
    return this.mLeftIsVoiceAssist;
  }
  
  public void launchCamera(String paramString)
  {
    Log.i("PhoneStatusBar/KeyguardBottomAreaView", "launchCamera, " + paramString);
    final Intent localIntent = getCameraIntent();
    PackageManager localPackageManager = this.mContext.getPackageManager();
    try
    {
      localPackageManager.getPackageInfo("com.oneplus.camera", 1);
      localIntent.setComponent(new ComponentName("com.oneplus.camera", "com.oneplus.camera.MainActivity"));
      localIntent.putExtra("com.android.systemui.camera_launch_source", paramString);
      localIntent.putExtra("com.android.systemui.camera_launch_source_gesture", NotificationPanelView.mLastCameraGestureLaunchSource);
      NotificationPanelView.mLastCameraGestureLaunchSource = 0;
      boolean bool = PreviewInflater.wouldLaunchResolverActivity(this.mContext, localIntent, KeyguardUpdateMonitor.getCurrentUser());
      if ((localIntent != SECURE_CAMERA_INTENT) || (bool))
      {
        this.mPhoneStatusBar.notifyCameraLaunching(new Runnable()
        {
          public void run()
          {
            Log.d("PhoneStatusBar/KeyguardBottomAreaView", "handle launchCamera");
            KeyguardBottomAreaView.-get0(KeyguardBottomAreaView.this).startActivity(localIntent, false, new ActivityStarter.Callback()
            {
              public void onActivityStarted(int paramAnonymous2Int)
              {
                KeyguardBottomAreaView.this.unbindCameraPrewarmService(KeyguardBottomAreaView.-wrap0(paramAnonymous2Int));
              }
            });
          }
        });
        return;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        Log.i("PhoneStatusBar/KeyguardBottomAreaView", "no op camera");
        localIntent.setComponent(new ComponentName("com.android.camera2", "com.android.camera.CameraActivity"));
      }
      this.mPhoneStatusBar.notifyCameraLaunching(new Runnable()
      {
        public void run()
        {
          AsyncTask.execute(new Runnable()
          {
            public void run()
            {
              Log.d("PhoneStatusBar/KeyguardBottomAreaView", "handle launchCamera secure");
              int i = -6;
              ActivityOptions localActivityOptions = ActivityOptions.makeBasic();
              localActivityOptions.setRotationAnimationHint(3);
              try
              {
                int j = ActivityManagerNative.getDefault().startActivityAsUser(null, KeyguardBottomAreaView.this.getContext().getBasePackageName(), this.val$intent, this.val$intent.resolveTypeIfNeeded(KeyguardBottomAreaView.this.getContext().getContentResolver()), null, null, 0, 268435456, null, localActivityOptions.toBundle(), UserHandle.CURRENT.getIdentifier());
                i = j;
              }
              catch (RemoteException localRemoteException)
              {
                for (;;)
                {
                  final boolean bool;
                  Log.w("PhoneStatusBar/KeyguardBottomAreaView", "Unable to start camera activity", localRemoteException);
                }
              }
              KeyguardBottomAreaView.-get0(KeyguardBottomAreaView.this).preventNextAnimation();
              bool = KeyguardBottomAreaView.-wrap0(i);
              KeyguardBottomAreaView.this.post(new Runnable()
              {
                public void run()
                {
                  KeyguardBottomAreaView.this.unbindCameraPrewarmService(bool);
                }
              });
            }
          });
        }
      });
    }
  }
  
  public void launchLeftAffordance()
  {
    if (this.mLeftIsVoiceAssist)
    {
      launchVoiceAssist();
      MdmLogger.log("lock_voice", "", "1");
      return;
    }
    launchPhone();
    MdmLogger.log("lock_phone", "", "1");
  }
  
  public void onClick(View paramView)
  {
    if (paramView == this.mCameraImageView) {
      launchCamera("lockscreen_affordance");
    }
    for (;;)
    {
      if (paramView == this.mLockIcon)
      {
        if (this.mAccessibilityController.isAccessibilityEnabled()) {
          break;
        }
        handleTrustCircleClick();
      }
      return;
      if (paramView == this.mLeftAffordanceView) {
        launchLeftAffordance();
      }
    }
    this.mPhoneStatusBar.animateCollapsePanels(0, true);
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    int i = getResources().getDimensionPixelSize(2131755523);
    paramConfiguration = (ViewGroup.MarginLayoutParams)this.mIndicationText.getLayoutParams();
    if (paramConfiguration.bottomMargin != i)
    {
      paramConfiguration.bottomMargin = i;
      this.mIndicationText.setLayoutParams(paramConfiguration);
    }
    i = getResources().getDimensionPixelSize(17105173);
    this.mIndicationText.setTextSize(0, i);
    this.mIndicationLevelText.setTextSize(0, i);
    this.mIndicationController.updateDashViews();
    paramConfiguration = this.mCameraImageView.getLayoutParams();
    paramConfiguration.width = getResources().getDimensionPixelSize(2131755520);
    paramConfiguration.height = getResources().getDimensionPixelSize(2131755519);
    this.mCameraImageView.setLayoutParams(paramConfiguration);
    this.mCameraImageView.setImageDrawable(this.mContext.getDrawable(2130837698));
    paramConfiguration = this.mLockIcon.getLayoutParams();
    paramConfiguration.width = getResources().getDimensionPixelSize(2131755520);
    paramConfiguration.height = getResources().getDimensionPixelSize(2131755519);
    this.mLockIcon.setLayoutParams(paramConfiguration);
    this.mLockIcon.update(true);
    paramConfiguration = this.mLeftAffordanceView.getLayoutParams();
    paramConfiguration.width = getResources().getDimensionPixelSize(2131755520);
    paramConfiguration.height = getResources().getDimensionPixelSize(2131755519);
    this.mLeftAffordanceView.setLayoutParams(paramConfiguration);
    updateLeftAffordanceIcon();
    updateEmergencyButton();
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mLockPatternUtils = new LockPatternUtils(this.mContext);
    this.mPreviewContainer = ((ViewGroup)findViewById(2131951855));
    this.mEmergencyButton = ((EmergencyButton)findViewById(2131951860));
    this.mCameraImageView = ((KeyguardAffordanceView)findViewById(2131951856));
    this.mLeftAffordanceView = ((KeyguardAffordanceView)findViewById(2131951857));
    this.mLockIcon = ((LockIcon)findViewById(2131951858));
    this.mIndicationText = ((TextView)findViewById(2131951853));
    this.mIndicationHost = findViewById(2131951851);
    this.mIndicationLevelText = ((TextView)findViewById(2131951852));
    this.mDashView = ((ImageView)findViewById(2131951850));
    watchForCameraPolicyChanges();
    updateCameraVisibility();
    this.mUnlockMethodCache = UnlockMethodCache.getInstance(getContext());
    this.mUnlockMethodCache.addListener(this);
    this.mLockIcon.update();
    updateEmergencyButton();
    setClipChildren(false);
    setClipToPadding(false);
    this.mPreviewInflater = new PreviewInflater(this.mContext, new LockPatternUtils(this.mContext));
    inflateCameraPreview();
    this.mLockIcon.setOnClickListener(this);
    this.mLockIcon.setOnLongClickListener(this);
    this.mCameraImageView.setOnClickListener(this);
    this.mLeftAffordanceView.setOnClickListener(this);
    initAccessibility();
  }
  
  public void onKeyguardShowingChanged()
  {
    updateLeftAffordance();
    inflateCameraPreview();
  }
  
  public boolean onLongClick(View paramView)
  {
    handleTrustCircleClick();
    return true;
  }
  
  public void onStateChanged(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mCameraImageView.setClickable(paramBoolean2);
    this.mLeftAffordanceView.setClickable(paramBoolean2);
    this.mCameraImageView.setFocusable(paramBoolean1);
    this.mLeftAffordanceView.setFocusable(paramBoolean1);
    this.mLockIcon.update();
  }
  
  public void onUnlockMethodStateChanged()
  {
    this.mLockIcon.update();
    updateCameraVisibility();
  }
  
  protected void onVisibilityChanged(View paramView, int paramInt)
  {
    super.onVisibilityChanged(paramView, paramInt);
    if ((paramView == this) && (paramInt == 0))
    {
      this.mLockIcon.update();
      updateCameraVisibility();
    }
  }
  
  public ResolveInfo resolveCameraIntent()
  {
    return this.mContext.getPackageManager().resolveActivityAsUser(getCameraIntent(), 65536, KeyguardUpdateMonitor.getCurrentUser());
  }
  
  public void setAccessibilityController(AccessibilityController paramAccessibilityController)
  {
    this.mAccessibilityController = paramAccessibilityController;
    this.mLockIcon.setAccessibilityController(paramAccessibilityController);
    paramAccessibilityController.addStateChangedCallback(this);
  }
  
  public void setActivityStarter(ActivityStarter paramActivityStarter)
  {
    this.mActivityStarter = paramActivityStarter;
  }
  
  public void setAffordanceHelper(KeyguardAffordanceHelper paramKeyguardAffordanceHelper)
  {
    this.mAffordanceHelper = paramKeyguardAffordanceHelper;
  }
  
  public void setAssistManager(AssistManager paramAssistManager)
  {
    this.mAssistManager = paramAssistManager;
    updateLeftAffordance();
  }
  
  public void setFlashlightController(FlashlightController paramFlashlightController)
  {
    this.mFlashlightController = paramFlashlightController;
  }
  
  public void setKeyguardIndicationController(KeyguardIndicationController paramKeyguardIndicationController)
  {
    this.mIndicationController = paramKeyguardIndicationController;
  }
  
  public void setPhoneStatusBar(PhoneStatusBar paramPhoneStatusBar)
  {
    this.mPhoneStatusBar = paramPhoneStatusBar;
    updateCameraVisibility();
  }
  
  public void setUserSetupComplete(boolean paramBoolean)
  {
    this.mUserSetupComplete = paramBoolean;
    updateCameraVisibility();
    updateLeftAffordanceIcon();
  }
  
  public void startFinishDozeAnimation()
  {
    long l = 0L;
    if (this.mLeftAffordanceView.getVisibility() == 0)
    {
      startFinishDozeAnimationElement(this.mLeftAffordanceView, 0L);
      l = 48L;
    }
    startFinishDozeAnimationElement(this.mLockIcon, l);
    if (this.mCameraImageView.getVisibility() == 0) {
      startFinishDozeAnimationElement(this.mCameraImageView, l + 48L);
    }
    this.mIndicationText.setAlpha(0.0F);
    this.mIndicationText.animate().alpha(1.0F).setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN).setDuration(700L);
  }
  
  public void unbindCameraPrewarmService(boolean paramBoolean)
  {
    if ((!this.mPrewarmBound) || ((this.mPrewarmMessenger != null) && (paramBoolean))) {}
    try
    {
      this.mPrewarmMessenger.send(Message.obtain(null, 1));
      this.mContext.unbindService(this.mPrewarmConnection);
      this.mPrewarmBound = false;
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.w("PhoneStatusBar/KeyguardBottomAreaView", "Error sending camera fired message", localRemoteException);
      }
    }
  }
  
  public void updateLeftAffordance()
  {
    updateLeftAffordanceIcon();
    updateLeftPreview();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\KeyguardBottomAreaView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */