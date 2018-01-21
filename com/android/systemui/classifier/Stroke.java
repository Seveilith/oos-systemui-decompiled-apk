package com.android.systemui.classifier;

import java.util.ArrayList;

public class Stroke
{
  private final float NANOS_TO_SECONDS = 1.0E9F;
  private final float mDpi;
  private long mEndTimeNano;
  private float mLength;
  private ArrayList<Point> mPoints = new ArrayList();
  private long mStartTimeNano;
  
  public Stroke(long paramLong, float paramFloat)
  {
    this.mDpi = paramFloat;
    this.mEndTimeNano = paramLong;
    this.mStartTimeNano = paramLong;
  }
  
  public void addPoint(float paramFloat1, float paramFloat2, long paramLong)
  {
    this.mEndTimeNano = paramLong;
    Point localPoint = new Point(paramFloat1 / this.mDpi, paramFloat2 / this.mDpi, paramLong - this.mStartTimeNano);
    if (!this.mPoints.isEmpty())
    {
      paramFloat1 = this.mLength;
      this.mLength = (((Point)this.mPoints.get(this.mPoints.size() - 1)).dist(localPoint) + paramFloat1);
    }
    this.mPoints.add(localPoint);
  }
  
  public int getCount()
  {
    return this.mPoints.size();
  }
  
  public long getDurationNanos()
  {
    return this.mEndTimeNano - this.mStartTimeNano;
  }
  
  public float getDurationSeconds()
  {
    return (float)getDurationNanos() / 1.0E9F;
  }
  
  public float getEndPointLength()
  {
    return ((Point)this.mPoints.get(0)).dist((Point)this.mPoints.get(this.mPoints.size() - 1));
  }
  
  public ArrayList<Point> getPoints()
  {
    return this.mPoints;
  }
  
  public float getTotalLength()
  {
    return this.mLength;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\Stroke.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */