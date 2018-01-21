package com.android.systemui.classifier;

import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpeedAnglesClassifier
  extends StrokeClassifier
{
  private HashMap<Stroke, Data> mStrokeMap = new HashMap();
  
  public SpeedAnglesClassifier(ClassifierData paramClassifierData)
  {
    this.mClassifierData = paramClassifierData;
  }
  
  public float getFalseTouchEvaluation(int paramInt, Stroke paramStroke)
  {
    paramStroke = (Data)this.mStrokeMap.get(paramStroke);
    return SpeedVarianceEvaluator.evaluate(paramStroke.getAnglesVariance()) + SpeedAnglesPercentageEvaluator.evaluate(paramStroke.getAnglesPercentage());
  }
  
  public String getTag()
  {
    return "SPD_ANG";
  }
  
  public void onTouchEvent(MotionEvent paramMotionEvent)
  {
    int j = paramMotionEvent.getActionMasked();
    if (j == 0) {
      this.mStrokeMap.clear();
    }
    int i = 0;
    while (i < paramMotionEvent.getPointerCount())
    {
      Stroke localStroke = this.mClassifierData.getStroke(paramMotionEvent.getPointerId(i));
      if (this.mStrokeMap.get(localStroke) == null) {
        this.mStrokeMap.put(localStroke, new Data());
      }
      if ((j != 1) && (j != 3) && ((j != 6) || (i != paramMotionEvent.getActionIndex()))) {
        ((Data)this.mStrokeMap.get(localStroke)).addPoint((Point)localStroke.getPoints().get(localStroke.getPoints().size() - 1));
      }
      i += 1;
    }
  }
  
  private static class Data
  {
    private final float ANGLE_DEVIATION = 0.31415927F;
    private final float DURATION_SCALE = 1.0E8F;
    private final float LENGTH_SCALE = 1.0F;
    private float mAcceleratingAngles = 0.0F;
    private float mAnglesCount = 0.0F;
    private float mCount = 1.0F;
    private float mDist = 0.0F;
    private List<Point> mLastThreePoints = new ArrayList();
    private float mPreviousAngle = 3.1415927F;
    private Point mPreviousPoint = null;
    private float mSum = 0.0F;
    private float mSumSquares = 0.0F;
    
    public void addPoint(Point paramPoint)
    {
      if (this.mPreviousPoint != null) {
        this.mDist += this.mPreviousPoint.dist(paramPoint);
      }
      this.mPreviousPoint = paramPoint;
      paramPoint = new Point((float)paramPoint.timeOffsetNano / 1.0E8F, this.mDist / 1.0F);
      if ((!this.mLastThreePoints.isEmpty()) && (((Point)this.mLastThreePoints.get(this.mLastThreePoints.size() - 1)).equals(paramPoint))) {}
      do
      {
        return;
        this.mLastThreePoints.add(paramPoint);
      } while (this.mLastThreePoints.size() != 4);
      this.mLastThreePoints.remove(0);
      float f1 = ((Point)this.mLastThreePoints.get(1)).getAngle((Point)this.mLastThreePoints.get(0), (Point)this.mLastThreePoints.get(2));
      this.mAnglesCount += 1.0F;
      if (f1 >= 2.8274336F) {
        this.mAcceleratingAngles += 1.0F;
      }
      float f2 = f1 - this.mPreviousAngle;
      this.mSum += f2;
      this.mSumSquares += f2 * f2;
      this.mCount = ((float)(this.mCount + 1.0D));
      this.mPreviousAngle = f1;
    }
    
    public float getAnglesPercentage()
    {
      if (this.mAnglesCount == 0.0F) {
        return 1.0F;
      }
      return this.mAcceleratingAngles / this.mAnglesCount;
    }
    
    public float getAnglesVariance()
    {
      return this.mSumSquares / this.mCount - this.mSum / this.mCount * (this.mSum / this.mCount);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\SpeedAnglesClassifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */