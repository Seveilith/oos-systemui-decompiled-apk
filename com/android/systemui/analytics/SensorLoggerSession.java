package com.android.systemui.analytics;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Build;
import android.view.MotionEvent;
import com.android.systemui.statusbar.phone.TouchAnalyticsProto.Session;
import com.android.systemui.statusbar.phone.TouchAnalyticsProto.Session.PhoneEvent;
import com.android.systemui.statusbar.phone.TouchAnalyticsProto.Session.SensorEvent;
import com.android.systemui.statusbar.phone.TouchAnalyticsProto.Session.TouchEvent;
import com.android.systemui.statusbar.phone.TouchAnalyticsProto.Session.TouchEvent.Pointer;
import java.util.ArrayList;

public class SensorLoggerSession
{
  private long mEndTimestampMillis;
  private ArrayList<TouchAnalyticsProto.Session.TouchEvent> mMotionEvents = new ArrayList();
  private ArrayList<TouchAnalyticsProto.Session.PhoneEvent> mPhoneEvents = new ArrayList();
  private int mResult = 2;
  private ArrayList<TouchAnalyticsProto.Session.SensorEvent> mSensorEvents = new ArrayList();
  private final long mStartSystemTimeNanos;
  private final long mStartTimestampMillis;
  private int mTouchAreaHeight;
  private int mTouchAreaWidth;
  private int mType;
  
  public SensorLoggerSession(long paramLong1, long paramLong2)
  {
    this.mStartTimestampMillis = paramLong1;
    this.mStartSystemTimeNanos = paramLong2;
    this.mType = 3;
  }
  
  private TouchAnalyticsProto.Session.TouchEvent motionEventToProto(MotionEvent paramMotionEvent)
  {
    int j = paramMotionEvent.getPointerCount();
    TouchAnalyticsProto.Session.TouchEvent localTouchEvent = new TouchAnalyticsProto.Session.TouchEvent();
    localTouchEvent.setTimeOffsetNanos(paramMotionEvent.getEventTimeNano() - this.mStartSystemTimeNanos);
    localTouchEvent.setAction(paramMotionEvent.getActionMasked());
    localTouchEvent.setActionIndex(paramMotionEvent.getActionIndex());
    localTouchEvent.pointers = new TouchAnalyticsProto.Session.TouchEvent.Pointer[j];
    int i = 0;
    while (i < j)
    {
      TouchAnalyticsProto.Session.TouchEvent.Pointer localPointer = new TouchAnalyticsProto.Session.TouchEvent.Pointer();
      localPointer.setX(paramMotionEvent.getX(i));
      localPointer.setY(paramMotionEvent.getY(i));
      localPointer.setSize(paramMotionEvent.getSize(i));
      localPointer.setPressure(paramMotionEvent.getPressure(i));
      localPointer.setId(paramMotionEvent.getPointerId(i));
      localTouchEvent.pointers[i] = localPointer;
      i += 1;
    }
    return localTouchEvent;
  }
  
  private TouchAnalyticsProto.Session.PhoneEvent phoneEventToProto(int paramInt, long paramLong)
  {
    TouchAnalyticsProto.Session.PhoneEvent localPhoneEvent = new TouchAnalyticsProto.Session.PhoneEvent();
    localPhoneEvent.setType(paramInt);
    localPhoneEvent.setTimeOffsetNanos(paramLong - this.mStartSystemTimeNanos);
    return localPhoneEvent;
  }
  
  private TouchAnalyticsProto.Session.SensorEvent sensorEventToProto(SensorEvent paramSensorEvent, long paramLong)
  {
    TouchAnalyticsProto.Session.SensorEvent localSensorEvent = new TouchAnalyticsProto.Session.SensorEvent();
    localSensorEvent.setType(paramSensorEvent.sensor.getType());
    localSensorEvent.setTimeOffsetNanos(paramLong - this.mStartSystemTimeNanos);
    localSensorEvent.setTimestamp(paramSensorEvent.timestamp);
    localSensorEvent.values = ((float[])paramSensorEvent.values.clone());
    return localSensorEvent;
  }
  
  public void addMotionEvent(MotionEvent paramMotionEvent)
  {
    paramMotionEvent = motionEventToProto(paramMotionEvent);
    this.mMotionEvents.add(paramMotionEvent);
  }
  
  public void addPhoneEvent(int paramInt, long paramLong)
  {
    TouchAnalyticsProto.Session.PhoneEvent localPhoneEvent = phoneEventToProto(paramInt, paramLong);
    this.mPhoneEvents.add(localPhoneEvent);
  }
  
  public void addSensorEvent(SensorEvent paramSensorEvent, long paramLong)
  {
    paramSensorEvent = sensorEventToProto(paramSensorEvent, paramLong);
    this.mSensorEvents.add(paramSensorEvent);
  }
  
  public void end(long paramLong, int paramInt)
  {
    this.mResult = paramInt;
    this.mEndTimestampMillis = paramLong;
  }
  
  public int getResult()
  {
    return this.mResult;
  }
  
  public long getStartTimestampMillis()
  {
    return this.mStartTimestampMillis;
  }
  
  public void setTouchArea(int paramInt1, int paramInt2)
  {
    this.mTouchAreaWidth = paramInt1;
    this.mTouchAreaHeight = paramInt2;
  }
  
  public void setType(int paramInt)
  {
    this.mType = paramInt;
  }
  
  public TouchAnalyticsProto.Session toProto()
  {
    TouchAnalyticsProto.Session localSession = new TouchAnalyticsProto.Session();
    localSession.setStartTimestampMillis(this.mStartTimestampMillis);
    localSession.setDurationMillis(this.mEndTimestampMillis - this.mStartTimestampMillis);
    localSession.setBuild(Build.FINGERPRINT);
    localSession.setResult(this.mResult);
    localSession.setType(this.mType);
    localSession.sensorEvents = ((TouchAnalyticsProto.Session.SensorEvent[])this.mSensorEvents.toArray(localSession.sensorEvents));
    localSession.touchEvents = ((TouchAnalyticsProto.Session.TouchEvent[])this.mMotionEvents.toArray(localSession.touchEvents));
    localSession.phoneEvents = ((TouchAnalyticsProto.Session.PhoneEvent[])this.mPhoneEvents.toArray(localSession.phoneEvents));
    localSession.setTouchAreaWidth(this.mTouchAreaWidth);
    localSession.setTouchAreaHeight(this.mTouchAreaHeight);
    return localSession;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("Session{");
    localStringBuilder.append("mStartTimestampMillis=").append(this.mStartTimestampMillis);
    localStringBuilder.append(", mStartSystemTimeNanos=").append(this.mStartSystemTimeNanos);
    localStringBuilder.append(", mEndTimestampMillis=").append(this.mEndTimestampMillis);
    localStringBuilder.append(", mResult=").append(this.mResult);
    localStringBuilder.append(", mTouchAreaHeight=").append(this.mTouchAreaHeight);
    localStringBuilder.append(", mTouchAreaWidth=").append(this.mTouchAreaWidth);
    localStringBuilder.append(", mMotionEvents=[size=").append(this.mMotionEvents.size()).append("]");
    localStringBuilder.append(", mSensorEvents=[size=").append(this.mSensorEvents.size()).append("]");
    localStringBuilder.append(", mPhoneEvents=[size=").append(this.mPhoneEvents.size()).append("]");
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\analytics\SensorLoggerSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */