package com.android.systemui.classifier;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.view.MotionEvent;

public class ProximityClassifier
  extends GestureClassifier
{
  private float mAverageNear;
  private long mGestureStartTimeNano;
  private boolean mNear;
  private long mNearDuration;
  private long mNearStartTimeNano;
  
  public ProximityClassifier(ClassifierData paramClassifierData) {}
  
  private void update(boolean paramBoolean, long paramLong)
  {
    if (paramLong > this.mNearStartTimeNano)
    {
      if (this.mNear) {
        this.mNearDuration += paramLong - this.mNearStartTimeNano;
      }
      if (paramBoolean) {
        this.mNearStartTimeNano = paramLong;
      }
    }
    this.mNear = paramBoolean;
  }
  
  public float getFalseTouchEvaluation(int paramInt)
  {
    return ProximityEvaluator.evaluate(this.mAverageNear, paramInt);
  }
  
  public String getTag()
  {
    return "PROX";
  }
  
  public void onSensorChanged(SensorEvent paramSensorEvent)
  {
    boolean bool = false;
    if (paramSensorEvent.sensor.getType() == 8)
    {
      if (paramSensorEvent.values[0] < paramSensorEvent.sensor.getMaximumRange()) {
        bool = true;
      }
      update(bool, paramSensorEvent.timestamp);
    }
  }
  
  public void onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getActionMasked();
    if (i == 0)
    {
      this.mGestureStartTimeNano = paramMotionEvent.getEventTimeNano();
      this.mNearStartTimeNano = paramMotionEvent.getEventTimeNano();
      this.mNearDuration = 0L;
    }
    long l;
    if ((i == 1) || (i == 3))
    {
      update(this.mNear, paramMotionEvent.getEventTimeNano());
      l = paramMotionEvent.getEventTimeNano() - this.mGestureStartTimeNano;
      if (l != 0L) {
        break label90;
      }
      if (!this.mNear) {
        break label85;
      }
    }
    label85:
    for (float f = 1.0F;; f = 0.0F)
    {
      this.mAverageNear = f;
      return;
    }
    label90:
    this.mAverageNear = ((float)this.mNearDuration / (float)l);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\ProximityClassifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */