package com.android.systemui.classifier;

import android.hardware.SensorEvent;
import android.view.MotionEvent;

public abstract class Classifier
{
  protected ClassifierData mClassifierData;
  
  public abstract String getTag();
  
  public void onSensorChanged(SensorEvent paramSensorEvent) {}
  
  public void onTouchEvent(MotionEvent paramMotionEvent) {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\Classifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */