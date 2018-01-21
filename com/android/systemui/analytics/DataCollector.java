package com.android.systemui.analytics;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.view.MotionEvent;
import android.widget.Toast;
import com.android.systemui.statusbar.phone.TouchAnalyticsProto.Session;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DataCollector
  implements SensorEventListener
{
  private static DataCollector sInstance = null;
  private boolean mAllowReportRejectedTouch = false;
  private boolean mCollectBadTouches = false;
  private final Context mContext;
  private boolean mCornerSwiping = false;
  private SensorLoggerSession mCurrentSession = null;
  private boolean mEnableCollector = false;
  private final Handler mHandler = new Handler();
  protected final ContentObserver mSettingsObserver = new ContentObserver(this.mHandler)
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      DataCollector.-wrap0(DataCollector.this);
    }
  };
  private boolean mTimeoutActive = false;
  private boolean mTrackingStarted = false;
  
  private DataCollector(Context paramContext)
  {
    this.mContext = paramContext;
    this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("data_collector_enable"), false, this.mSettingsObserver, -1);
    this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("data_collector_collect_bad_touches"), false, this.mSettingsObserver, -1);
    this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("data_collector_allow_rejected_touch_reports"), false, this.mSettingsObserver, -1);
    updateConfiguration();
  }
  
  private void addEvent(int paramInt)
  {
    if ((isEnabled()) && (this.mCurrentSession != null)) {
      this.mCurrentSession.addPhoneEvent(paramInt, System.nanoTime());
    }
  }
  
  private void enforceTimeout()
  {
    if ((this.mTimeoutActive) && (System.currentTimeMillis() - this.mCurrentSession.getStartTimestampMillis() > 11000L)) {
      onSessionEnd(2);
    }
  }
  
  public static DataCollector getInstance(Context paramContext)
  {
    if (sInstance == null) {
      sInstance = new DataCollector(paramContext);
    }
    return sInstance;
  }
  
  private void onSessionEnd(int paramInt)
  {
    SensorLoggerSession localSensorLoggerSession = this.mCurrentSession;
    this.mCurrentSession = null;
    if (this.mEnableCollector)
    {
      localSensorLoggerSession.end(System.currentTimeMillis(), paramInt);
      queueSession(localSensorLoggerSession);
    }
  }
  
  private void onSessionStart()
  {
    this.mCornerSwiping = false;
    this.mTrackingStarted = false;
    this.mCurrentSession = new SensorLoggerSession(System.currentTimeMillis(), System.nanoTime());
  }
  
  private void queueSession(final SensorLoggerSession paramSensorLoggerSession)
  {
    AsyncTask.execute(new Runnable()
    {
      public void run()
      {
        byte[] arrayOfByte = TouchAnalyticsProto.Session.toByteArray(paramSensorLoggerSession.toProto());
        Object localObject = DataCollector.-get1(DataCollector.this).getFilesDir().getAbsolutePath();
        if (paramSensorLoggerSession.getResult() != 1) {
          if (!DataCollector.-get0(DataCollector.this)) {
            return;
          }
        }
        for (localObject = (String)localObject + "/bad_touches";; localObject = (String)localObject + "/good_touches")
        {
          localObject = new File((String)localObject);
          ((File)localObject).mkdir();
          localObject = new File((File)localObject, "trace_" + System.currentTimeMillis());
          try
          {
            new FileOutputStream((File)localObject).write(arrayOfByte);
            return;
          }
          catch (IOException localIOException)
          {
            throw new RuntimeException(localIOException);
          }
        }
      }
    });
  }
  
  private boolean sessionEntrypoint()
  {
    if ((isEnabled()) && (this.mCurrentSession == null))
    {
      onSessionStart();
      return true;
    }
    return false;
  }
  
  private void sessionExitpoint(int paramInt)
  {
    if (this.mCurrentSession != null) {
      onSessionEnd(paramInt);
    }
  }
  
  private void updateConfiguration()
  {
    boolean bool2 = false;
    if ((Build.IS_DEBUGGABLE) && (Settings.Secure.getInt(this.mContext.getContentResolver(), "data_collector_enable", 0) != 0))
    {
      bool1 = true;
      this.mEnableCollector = bool1;
      if ((!this.mEnableCollector) || (Settings.Secure.getInt(this.mContext.getContentResolver(), "data_collector_collect_bad_touches", 0) == 0)) {
        break label100;
      }
    }
    label100:
    for (boolean bool1 = true;; bool1 = false)
    {
      this.mCollectBadTouches = bool1;
      bool1 = bool2;
      if (Build.IS_DEBUGGABLE)
      {
        bool1 = bool2;
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), "data_collector_allow_rejected_touch_reports", 0) != 0) {
          bool1 = true;
        }
      }
      this.mAllowReportRejectedTouch = bool1;
      return;
      bool1 = false;
      break;
    }
  }
  
  public boolean isEnabled()
  {
    if (!this.mEnableCollector) {
      return this.mAllowReportRejectedTouch;
    }
    return true;
  }
  
  public boolean isEnabledFull()
  {
    return this.mEnableCollector;
  }
  
  public boolean isReportingEnabled()
  {
    return this.mAllowReportRejectedTouch;
  }
  
  public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}
  
  public void onAffordanceSwipingAborted()
  {
    if (this.mCornerSwiping)
    {
      this.mCornerSwiping = false;
      addEvent(23);
    }
  }
  
  public void onAffordanceSwipingStarted(boolean paramBoolean)
  {
    this.mCornerSwiping = true;
    if (paramBoolean)
    {
      addEvent(21);
      return;
    }
    addEvent(22);
  }
  
  public void onBouncerHidden()
  {
    addEvent(5);
  }
  
  public void onBouncerShown()
  {
    addEvent(4);
  }
  
  public void onCameraHintStarted()
  {
    addEvent(27);
  }
  
  public void onCameraOn()
  {
    addEvent(24);
  }
  
  public void onLeftAffordanceHintStarted()
  {
    addEvent(28);
  }
  
  public void onLeftAffordanceOn()
  {
    addEvent(25);
  }
  
  public void onNotificationActive()
  {
    addEvent(11);
  }
  
  public void onNotificationDismissed()
  {
    addEvent(18);
  }
  
  public void onNotificationDoubleTap()
  {
    addEvent(13);
  }
  
  public void onNotificatonStartDismissing()
  {
    addEvent(19);
  }
  
  public void onNotificatonStartDraggingDown()
  {
    addEvent(16);
  }
  
  public void onNotificatonStopDismissing()
  {
    addEvent(20);
  }
  
  public void onNotificatonStopDraggingDown()
  {
    addEvent(17);
  }
  
  public void onQsDown()
  {
    addEvent(6);
  }
  
  public void onScreenOff()
  {
    addEvent(2);
    sessionExitpoint(0);
  }
  
  public void onScreenOnFromTouch()
  {
    if (sessionEntrypoint()) {
      addEvent(1);
    }
  }
  
  public void onScreenTurningOn()
  {
    if (sessionEntrypoint()) {
      addEvent(0);
    }
  }
  
  public void onSensorChanged(SensorEvent paramSensorEvent)
  {
    try
    {
      if ((isEnabled()) && (this.mCurrentSession != null))
      {
        this.mCurrentSession.addSensorEvent(paramSensorEvent, System.nanoTime());
        enforceTimeout();
      }
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
    addEvent(3);
    sessionExitpoint(1);
  }
  
  public void onTouchEvent(MotionEvent paramMotionEvent, int paramInt1, int paramInt2)
  {
    if (this.mCurrentSession != null)
    {
      this.mCurrentSession.addMotionEvent(paramMotionEvent);
      this.mCurrentSession.setTouchArea(paramInt1, paramInt2);
      enforceTimeout();
    }
  }
  
  public void onTrackingStarted()
  {
    this.mTrackingStarted = true;
    addEvent(9);
  }
  
  public void onTrackingStopped()
  {
    if (this.mTrackingStarted)
    {
      this.mTrackingStarted = false;
      addEvent(10);
    }
  }
  
  public void onUnlockHintStarted()
  {
    addEvent(26);
  }
  
  public Uri reportRejectedTouch()
  {
    if (this.mCurrentSession == null)
    {
      Toast.makeText(this.mContext, "Generating rejected touch report failed: session timed out.", 1).show();
      return null;
    }
    Object localObject = this.mCurrentSession;
    ((SensorLoggerSession)localObject).setType(4);
    ((SensorLoggerSession)localObject).end(System.currentTimeMillis(), 1);
    localObject = TouchAnalyticsProto.Session.toByteArray(((SensorLoggerSession)localObject).toProto());
    File localFile = new File(this.mContext.getExternalCacheDir(), "rejected_touch_reports");
    localFile.mkdir();
    localFile = new File(localFile, "rejected_touch_report_" + System.currentTimeMillis());
    try
    {
      new FileOutputStream(localFile).write((byte[])localObject);
      return Uri.fromFile(localFile);
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
  
  public void setNotificationExpanded()
  {
    addEvent(14);
  }
  
  public void setQsExpanded(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      addEvent(7);
      return;
    }
    addEvent(8);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\analytics\DataCollector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */