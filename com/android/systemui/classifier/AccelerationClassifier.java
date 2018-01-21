package com.android.systemui.classifier;

import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class AccelerationClassifier
  extends StrokeClassifier
{
  private final HashMap<Stroke, Data> mStrokeMap = new HashMap();
  
  public AccelerationClassifier(ClassifierData paramClassifierData)
  {
    this.mClassifierData = paramClassifierData;
  }
  
  public float getFalseTouchEvaluation(int paramInt, Stroke paramStroke)
  {
    return SpeedRatioEvaluator.evaluate(((Data)this.mStrokeMap.get(paramStroke)).maxSpeedRatio) * 2.0F;
  }
  
  public String getTag()
  {
    return "ACC";
  }
  
  public void onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getActionMasked() == 0) {
      this.mStrokeMap.clear();
    }
    int i = 0;
    if (i < paramMotionEvent.getPointerCount())
    {
      Stroke localStroke = this.mClassifierData.getStroke(paramMotionEvent.getPointerId(i));
      Point localPoint = (Point)localStroke.getPoints().get(localStroke.getPoints().size() - 1);
      if (this.mStrokeMap.get(localStroke) == null) {
        this.mStrokeMap.put(localStroke, new Data(localPoint));
      }
      for (;;)
      {
        i += 1;
        break;
        ((Data)this.mStrokeMap.get(localStroke)).addPoint(localPoint);
      }
    }
  }
  
  private static class Data
  {
    float maxSpeedRatio = 0.0F;
    Point previousPoint;
    float previousSpeed = 0.0F;
    
    public Data(Point paramPoint)
    {
      this.previousPoint = paramPoint;
    }
    
    public void addPoint(Point paramPoint)
    {
      float f2 = this.previousPoint.dist(paramPoint);
      float f1 = (float)(paramPoint.timeOffsetNano - this.previousPoint.timeOffsetNano + 1L);
      f2 /= f1;
      if ((f1 > 2.0E7F) || (f1 < 5000000.0F))
      {
        this.previousSpeed = 0.0F;
        this.previousPoint = paramPoint;
        return;
      }
      if (this.previousSpeed != 0.0F) {
        this.maxSpeedRatio = Math.max(this.maxSpeedRatio, f2 / this.previousSpeed);
      }
      this.previousSpeed = f2;
      this.previousPoint = paramPoint;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\AccelerationClassifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */