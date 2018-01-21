package com.android.systemui.recents.views;

class Range
{
  float max;
  float min;
  float origin;
  final float relativeMax;
  final float relativeMin;
  
  public Range(float paramFloat1, float paramFloat2)
  {
    this.relativeMin = paramFloat1;
    this.min = paramFloat1;
    this.relativeMax = paramFloat2;
    this.max = paramFloat2;
  }
  
  public float getAbsoluteX(float paramFloat)
  {
    if (paramFloat < 0.5F) {
      return (paramFloat - 0.5F) / 0.5F * -this.relativeMin;
    }
    return (paramFloat - 0.5F) / 0.5F * this.relativeMax;
  }
  
  public float getNormalizedX(float paramFloat)
  {
    if (paramFloat < this.origin) {
      return (paramFloat - this.origin) * 0.5F / -this.relativeMin + 0.5F;
    }
    return (paramFloat - this.origin) * 0.5F / this.relativeMax + 0.5F;
  }
  
  public boolean isInRange(float paramFloat)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramFloat >= Math.floor(this.min))
    {
      bool1 = bool2;
      if (paramFloat <= Math.ceil(this.max)) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public void offset(float paramFloat)
  {
    this.origin = paramFloat;
    this.min = (this.relativeMin + paramFloat);
    this.max = (this.relativeMax + paramFloat);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\Range.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */