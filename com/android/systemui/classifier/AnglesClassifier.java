package com.android.systemui.classifier;

import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnglesClassifier
  extends StrokeClassifier
{
  private HashMap<Stroke, Data> mStrokeMap = new HashMap();
  
  public AnglesClassifier(ClassifierData paramClassifierData)
  {
    this.mClassifierData = paramClassifierData;
  }
  
  public float getFalseTouchEvaluation(int paramInt, Stroke paramStroke)
  {
    paramStroke = (Data)this.mStrokeMap.get(paramStroke);
    return AnglesVarianceEvaluator.evaluate(paramStroke.getAnglesVariance()) + AnglesPercentageEvaluator.evaluate(paramStroke.getAnglesPercentage());
  }
  
  public String getTag()
  {
    return "ANG";
  }
  
  public void onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getActionMasked() == 0) {
      this.mStrokeMap.clear();
    }
    int i = 0;
    while (i < paramMotionEvent.getPointerCount())
    {
      Stroke localStroke = this.mClassifierData.getStroke(paramMotionEvent.getPointerId(i));
      if (this.mStrokeMap.get(localStroke) == null) {
        this.mStrokeMap.put(localStroke, new Data());
      }
      ((Data)this.mStrokeMap.get(localStroke)).addPoint((Point)localStroke.getPoints().get(localStroke.getPoints().size() - 1));
      i += 1;
    }
  }
  
  private static class Data
  {
    private final float ANGLE_DEVIATION = 0.15707964F;
    private float mAnglesCount = 0.0F;
    private float mBiggestAngle = 0.0F;
    private float mCount = 1.0F;
    private float mFirstAngleVariance = 0.0F;
    private float mFirstLength = 0.0F;
    private List<Point> mLastThreePoints = new ArrayList();
    private float mLeftAngles = 0.0F;
    private float mLength = 0.0F;
    private float mPreviousAngle = 3.1415927F;
    private float mRightAngles = 0.0F;
    private float mSecondCount = 1.0F;
    private float mSecondSum = 0.0F;
    private float mSecondSumSquares = 0.0F;
    private float mStraightAngles = 0.0F;
    private float mSum = 0.0F;
    private float mSumSquares = 0.0F;
    
    public void addPoint(Point paramPoint)
    {
      if ((!this.mLastThreePoints.isEmpty()) && (((Point)this.mLastThreePoints.get(this.mLastThreePoints.size() - 1)).equals(paramPoint))) {}
      do
      {
        return;
        if (!this.mLastThreePoints.isEmpty())
        {
          f1 = this.mLength;
          this.mLength = (((Point)this.mLastThreePoints.get(this.mLastThreePoints.size() - 1)).dist(paramPoint) + f1);
        }
        this.mLastThreePoints.add(paramPoint);
      } while (this.mLastThreePoints.size() != 4);
      this.mLastThreePoints.remove(0);
      float f1 = ((Point)this.mLastThreePoints.get(1)).getAngle((Point)this.mLastThreePoints.get(0), (Point)this.mLastThreePoints.get(2));
      this.mAnglesCount += 1.0F;
      float f2;
      if (f1 < 2.9845130165391645D)
      {
        this.mLeftAngles += 1.0F;
        f2 = f1 - this.mPreviousAngle;
        if (this.mBiggestAngle >= f1) {
          break label339;
        }
        this.mBiggestAngle = f1;
        this.mFirstLength = this.mLength;
        this.mFirstAngleVariance = getAnglesVariance(this.mSumSquares, this.mSum, this.mCount);
        this.mSecondSumSquares = 0.0F;
        this.mSecondSum = 0.0F;
      }
      for (this.mSecondCount = 1.0F;; this.mSecondCount = ((float)(this.mSecondCount + 1.0D)))
      {
        this.mSum += f2;
        this.mSumSquares += f2 * f2;
        this.mCount = ((float)(this.mCount + 1.0D));
        this.mPreviousAngle = f1;
        return;
        if (f1 <= 3.298672290640422D)
        {
          this.mStraightAngles += 1.0F;
          break;
        }
        this.mRightAngles += 1.0F;
        break;
        label339:
        this.mSecondSum += f2;
        this.mSecondSumSquares += f2 * f2;
      }
    }
    
    public float getAnglesPercentage()
    {
      if (this.mAnglesCount == 0.0F) {
        return 1.0F;
      }
      return (Math.max(this.mLeftAngles, this.mRightAngles) + this.mStraightAngles) / this.mAnglesCount;
    }
    
    public float getAnglesVariance()
    {
      float f2 = getAnglesVariance(this.mSumSquares, this.mSum, this.mCount);
      float f1 = f2;
      if (this.mFirstLength < this.mLength / 2.0F) {
        f1 = Math.min(f2, this.mFirstAngleVariance + getAnglesVariance(this.mSecondSumSquares, this.mSecondSum, this.mSecondCount));
      }
      return f1;
    }
    
    public float getAnglesVariance(float paramFloat1, float paramFloat2, float paramFloat3)
    {
      return paramFloat1 / paramFloat3 - paramFloat2 / paramFloat3 * (paramFloat2 / paramFloat3);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\AnglesClassifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */