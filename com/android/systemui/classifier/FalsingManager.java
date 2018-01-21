package com.android.systemui.classifier;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings.Secure;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityManager;
import com.android.systemui.analytics.DataCollector;
import com.android.systemui.statusbar.StatusBarState;
import java.io.PrintWriter;

public class FalsingManager
  implements SensorEventListener
{
  private static final int[] CLASSIFIER_SENSORS = { 8 };
  private static final int[] COLLECTOR_SENSORS = { 1, 4, 8, 5, 11 };
  private static FalsingManager sInstance = null;
  private final AccessibilityManager mAccessibilityManager;
  private boolean mBouncerOn = false;
  private final Context mContext;
  private final DataCollector mDataCollector;
  private boolean mEnforceBouncer = false;
  private final Handler mHandler = new Handler();
  private final HumanInteractionClassifier mHumanInteractionClassifier;
  private boolean mScreenOn;
  private final SensorManager mSensorManager;
  private boolean mSessionActive = false;
  protected final ContentObserver mSettingsObserver = new ContentObserver(this.mHandler)
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      FalsingManager.-wrap0(FalsingManager.this);
    }
  };
  private int mState = 0;
  
  private FalsingManager(Context paramContext)
  {
    this.mContext = paramContext;
    this.mSensorManager = ((SensorManager)this.mContext.getSystemService(SensorManager.class));
    this.mAccessibilityManager = ((AccessibilityManager)paramContext.getSystemService(AccessibilityManager.class));
    this.mDataCollector = DataCollector.getInstance(this.mContext);
    this.mHumanInteractionClassifier = HumanInteractionClassifier.getInstance(this.mContext);
    this.mScreenOn = ((PowerManager)paramContext.getSystemService(PowerManager.class)).isInteractive();
    this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("falsing_manager_enforce_bouncer"), false, this.mSettingsObserver, -1);
    updateConfiguration();
  }
  
  public static FalsingManager getInstance(Context paramContext)
  {
    if (sInstance == null) {
      sInstance = new FalsingManager(paramContext);
    }
    return sInstance;
  }
  
  private boolean isEnabled()
  {
    if (!this.mHumanInteractionClassifier.isEnabled()) {
      return this.mDataCollector.isEnabled();
    }
    return true;
  }
  
  private void onSessionStart()
  {
    if (FalsingLog.ENABLED) {
      FalsingLog.i("onSessionStart", "classifierEnabled=" + isClassiferEnabled());
    }
    this.mBouncerOn = false;
    this.mSessionActive = true;
    if (this.mHumanInteractionClassifier.isEnabled()) {
      registerSensors(CLASSIFIER_SENSORS);
    }
    if (this.mDataCollector.isEnabledFull()) {
      registerSensors(COLLECTOR_SENSORS);
    }
  }
  
  private void registerSensors(int[] paramArrayOfInt)
  {
    int i = 0;
    int j = paramArrayOfInt.length;
    while (i < j)
    {
      int k = paramArrayOfInt[i];
      Sensor localSensor = this.mSensorManager.getDefaultSensor(k);
      if (localSensor != null) {
        this.mSensorManager.registerListener(this, localSensor, 1);
      }
      i += 1;
    }
  }
  
  private boolean sessionEntrypoint()
  {
    if ((!this.mSessionActive) && (shouldSessionBeActive()))
    {
      onSessionStart();
      return true;
    }
    return false;
  }
  
  private void sessionExitpoint(boolean paramBoolean)
  {
    if ((!this.mSessionActive) || ((!paramBoolean) && (shouldSessionBeActive()))) {
      return;
    }
    this.mSessionActive = false;
    this.mSensorManager.unregisterListener(this);
  }
  
  private boolean shouldSessionBeActive()
  {
    return (!FalsingLog.ENABLED) || ((isEnabled()) && (this.mScreenOn) && (this.mState == 1));
  }
  
  private void updateConfiguration()
  {
    boolean bool = false;
    if (Settings.Secure.getInt(this.mContext.getContentResolver(), "falsing_manager_enforce_bouncer", 0) != 0) {
      bool = true;
    }
    this.mEnforceBouncer = bool;
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    int j = 1;
    paramPrintWriter.println("FALSING MANAGER");
    paramPrintWriter.print("classifierEnabled=");
    if (isClassiferEnabled())
    {
      i = 1;
      paramPrintWriter.println(i);
      paramPrintWriter.print("mSessionActive=");
      if (!this.mSessionActive) {
        break label115;
      }
      i = 1;
      label43:
      paramPrintWriter.println(i);
      paramPrintWriter.print("mBouncerOn=");
      if (!this.mSessionActive) {
        break label120;
      }
      i = 1;
      label63:
      paramPrintWriter.println(i);
      paramPrintWriter.print("mState=");
      paramPrintWriter.println(StatusBarState.toShortString(this.mState));
      paramPrintWriter.print("mScreenOn=");
      if (!this.mScreenOn) {
        break label125;
      }
    }
    label115:
    label120:
    label125:
    for (int i = j;; i = 0)
    {
      paramPrintWriter.println(i);
      paramPrintWriter.println();
      return;
      i = 0;
      break;
      i = 0;
      break label43;
      i = 0;
      break label63;
    }
  }
  
  public boolean isClassiferEnabled()
  {
    return this.mHumanInteractionClassifier.isEnabled();
  }
  
  public boolean isFalseTouch()
  {
    int j = 1;
    StringBuilder localStringBuilder;
    if ((FalsingLog.ENABLED) && (!this.mSessionActive) && (((PowerManager)this.mContext.getSystemService(PowerManager.class)).isInteractive()))
    {
      localStringBuilder = new StringBuilder().append("Session is not active, yet there's a query for a false touch.").append(" enabled=");
      if (!isEnabled()) {
        break label120;
      }
      i = 1;
      localStringBuilder = localStringBuilder.append(i).append(" mScreenOn=");
      if (!this.mScreenOn) {
        break label125;
      }
    }
    label120:
    label125:
    for (int i = j;; i = 0)
    {
      FalsingLog.wtf("isFalseTouch", i + " mState=" + StatusBarState.toShortString(this.mState));
      if (!this.mAccessibilityManager.isTouchExplorationEnabled()) {
        break label130;
      }
      return false;
      i = 0;
      break;
    }
    label130:
    return this.mHumanInteractionClassifier.isFalseTouch();
  }
  
  public boolean isReportingEnabled()
  {
    return this.mDataCollector.isReportingEnabled();
  }
  
  public void onAccuracyChanged(Sensor paramSensor, int paramInt)
  {
    this.mDataCollector.onAccuracyChanged(paramSensor, paramInt);
  }
  
  public void onAffordanceSwipingAborted()
  {
    this.mDataCollector.onAffordanceSwipingAborted();
  }
  
  public void onAffordanceSwipingStarted(boolean paramBoolean)
  {
    if (FalsingLog.ENABLED) {
      FalsingLog.i("onAffordanceSwipingStarted", "");
    }
    if (paramBoolean) {
      this.mHumanInteractionClassifier.setType(6);
    }
    for (;;)
    {
      this.mDataCollector.onAffordanceSwipingStarted(paramBoolean);
      return;
      this.mHumanInteractionClassifier.setType(5);
    }
  }
  
  public void onBouncerHidden()
  {
    StringBuilder localStringBuilder;
    if (FalsingLog.ENABLED)
    {
      localStringBuilder = new StringBuilder().append("from=");
      if (!this.mBouncerOn) {
        break label63;
      }
    }
    label63:
    for (int i = 1;; i = 0)
    {
      FalsingLog.i("onBouncerHidden", i);
      if (this.mBouncerOn)
      {
        this.mBouncerOn = false;
        this.mDataCollector.onBouncerHidden();
      }
      return;
    }
  }
  
  public void onBouncerShown()
  {
    StringBuilder localStringBuilder;
    if (FalsingLog.ENABLED)
    {
      localStringBuilder = new StringBuilder().append("from=");
      if (!this.mBouncerOn) {
        break label63;
      }
    }
    label63:
    for (int i = 1;; i = 0)
    {
      FalsingLog.i("onBouncerShown", i);
      if (!this.mBouncerOn)
      {
        this.mBouncerOn = true;
        this.mDataCollector.onBouncerShown();
      }
      return;
    }
  }
  
  public void onCameraHintStarted()
  {
    this.mDataCollector.onCameraHintStarted();
  }
  
  public void onCameraOn()
  {
    this.mDataCollector.onCameraOn();
  }
  
  public void onLeftAffordanceHintStarted()
  {
    this.mDataCollector.onLeftAffordanceHintStarted();
  }
  
  public void onLeftAffordanceOn()
  {
    this.mDataCollector.onLeftAffordanceOn();
  }
  
  public void onNotificationActive()
  {
    this.mDataCollector.onNotificationActive();
  }
  
  public void onNotificationDismissed()
  {
    this.mDataCollector.onNotificationDismissed();
  }
  
  public void onNotificationDoubleTap()
  {
    this.mDataCollector.onNotificationDoubleTap();
  }
  
  public void onNotificatonStartDismissing()
  {
    if (FalsingLog.ENABLED) {
      FalsingLog.i("onNotificatonStartDismissing", "");
    }
    this.mHumanInteractionClassifier.setType(1);
    this.mDataCollector.onNotificatonStartDismissing();
  }
  
  public void onNotificatonStartDraggingDown()
  {
    if (FalsingLog.ENABLED) {
      FalsingLog.i("onNotificatonStartDraggingDown", "");
    }
    this.mHumanInteractionClassifier.setType(2);
    this.mDataCollector.onNotificatonStartDraggingDown();
  }
  
  public void onNotificatonStopDismissing()
  {
    this.mDataCollector.onNotificatonStopDismissing();
  }
  
  public void onNotificatonStopDraggingDown()
  {
    this.mDataCollector.onNotificatonStopDraggingDown();
  }
  
  public void onQsDown()
  {
    if (FalsingLog.ENABLED) {
      FalsingLog.i("onQsDown", "");
    }
    this.mHumanInteractionClassifier.setType(0);
    this.mDataCollector.onQsDown();
  }
  
  public void onScreenOff()
  {
    StringBuilder localStringBuilder;
    if (FalsingLog.ENABLED)
    {
      localStringBuilder = new StringBuilder().append("from=");
      if (!this.mScreenOn) {
        break label61;
      }
    }
    label61:
    for (int i = 1;; i = 0)
    {
      FalsingLog.i("onScreenOff", i);
      this.mDataCollector.onScreenOff();
      this.mScreenOn = false;
      sessionExitpoint(false);
      return;
    }
  }
  
  public void onScreenOnFromTouch()
  {
    StringBuilder localStringBuilder;
    if (FalsingLog.ENABLED)
    {
      localStringBuilder = new StringBuilder().append("from=");
      if (!this.mScreenOn) {
        break label63;
      }
    }
    label63:
    for (int i = 1;; i = 0)
    {
      FalsingLog.i("onScreenOnFromTouch", i);
      this.mScreenOn = true;
      if (sessionEntrypoint()) {
        this.mDataCollector.onScreenOnFromTouch();
      }
      return;
    }
  }
  
  public void onScreenTurningOn()
  {
    StringBuilder localStringBuilder;
    if (FalsingLog.ENABLED)
    {
      localStringBuilder = new StringBuilder().append("from=");
      if (!this.mScreenOn) {
        break label63;
      }
    }
    label63:
    for (int i = 1;; i = 0)
    {
      FalsingLog.i("onScreenTurningOn", i);
      this.mScreenOn = true;
      if (sessionEntrypoint()) {
        this.mDataCollector.onScreenTurningOn();
      }
      return;
    }
  }
  
  public void onSensorChanged(SensorEvent paramSensorEvent)
  {
    try
    {
      this.mDataCollector.onSensorChanged(paramSensorEvent);
      this.mHumanInteractionClassifier.onSensorChanged(paramSensorEvent);
      return;
    }
    finally
    {
      paramSensorEvent = finally;
      throw paramSensorEvent;
    }
  }
  
  public void onSucccessfulUnlock()
  {
    if (FalsingLog.ENABLED) {
      FalsingLog.i("onSucccessfulUnlock", "");
    }
    this.mDataCollector.onSucccessfulUnlock();
  }
  
  public void onTouchEvent(MotionEvent paramMotionEvent, int paramInt1, int paramInt2)
  {
    if ((!this.mSessionActive) || (this.mBouncerOn)) {
      return;
    }
    this.mDataCollector.onTouchEvent(paramMotionEvent, paramInt1, paramInt2);
    this.mHumanInteractionClassifier.onTouchEvent(paramMotionEvent);
  }
  
  public void onTrackingStarted()
  {
    if (FalsingLog.ENABLED) {
      FalsingLog.i("onTrackingStarted", "");
    }
    this.mHumanInteractionClassifier.setType(4);
    this.mDataCollector.onTrackingStarted();
  }
  
  public void onTrackingStopped()
  {
    this.mDataCollector.onTrackingStopped();
  }
  
  public void onUnlockHintStarted()
  {
    this.mDataCollector.onUnlockHintStarted();
  }
  
  public Uri reportRejectedTouch()
  {
    if (this.mDataCollector.isEnabled()) {
      return this.mDataCollector.reportRejectedTouch();
    }
    return null;
  }
  
  public void setNotificationExpanded()
  {
    this.mDataCollector.setNotificationExpanded();
  }
  
  public void setQsExpanded(boolean paramBoolean)
  {
    this.mDataCollector.setQsExpanded(paramBoolean);
  }
  
  public void setStatusBarState(int paramInt)
  {
    if (FalsingLog.ENABLED) {
      FalsingLog.i("setStatusBarState", "from=" + StatusBarState.toShortString(this.mState) + " to=" + StatusBarState.toShortString(paramInt));
    }
    this.mState = paramInt;
    if (shouldSessionBeActive())
    {
      sessionEntrypoint();
      return;
    }
    sessionExitpoint(false);
  }
  
  public boolean shouldEnforceBouncer()
  {
    return this.mEnforceBouncer;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\FalsingManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */