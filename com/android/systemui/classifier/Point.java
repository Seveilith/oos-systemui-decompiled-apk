package com.android.systemui.classifier;

public class Point
{
  public long timeOffsetNano;
  public float x;
  public float y;
  
  public Point(float paramFloat1, float paramFloat2)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
    this.timeOffsetNano = 0L;
  }
  
  public Point(float paramFloat1, float paramFloat2, long paramLong)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
    this.timeOffsetNano = paramLong;
  }
  
  public float crossProduct(Point paramPoint1, Point paramPoint2)
  {
    return (paramPoint1.x - this.x) * (paramPoint2.y - this.y) - (paramPoint1.y - this.y) * (paramPoint2.x - this.x);
  }
  
  public float dist(Point paramPoint)
  {
    return (float)Math.hypot(paramPoint.x - this.x, paramPoint.y - this.y);
  }
  
  public float dotProduct(Point paramPoint1, Point paramPoint2)
  {
    return (paramPoint1.x - this.x) * (paramPoint2.x - this.x) + (paramPoint1.y - this.y) * (paramPoint2.y - this.y);
  }
  
  public boolean equals(Point paramPoint)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.x == paramPoint.x)
    {
      bool1 = bool2;
      if (this.y == paramPoint.y) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public float getAngle(Point paramPoint1, Point paramPoint2)
  {
    float f1 = dist(paramPoint1);
    float f2 = dist(paramPoint2);
    if ((f1 == 0.0F) || (f2 == 0.0F)) {
      return 0.0F;
    }
    float f3 = crossProduct(paramPoint1, paramPoint2);
    f2 = (float)Math.acos(Math.min(1.0F, Math.max(-1.0F, dotProduct(paramPoint1, paramPoint2) / f1 / f2)));
    f1 = f2;
    if (f3 < 0.0D) {
      f1 = 6.2831855F - f2;
    }
    return f1;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\Point.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */