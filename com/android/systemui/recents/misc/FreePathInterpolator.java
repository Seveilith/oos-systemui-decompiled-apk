package com.android.systemui.recents.misc;

import android.graphics.Path;
import android.view.animation.BaseInterpolator;

public class FreePathInterpolator
  extends BaseInterpolator
{
  private float mArcLength;
  private float[] mX;
  private float[] mY;
  
  public FreePathInterpolator(Path paramPath)
  {
    initPath(paramPath);
  }
  
  private void initPath(Path paramPath)
  {
    paramPath = paramPath.approximate(0.002F);
    int k = paramPath.length / 3;
    this.mX = new float[k];
    this.mY = new float[k];
    this.mArcLength = 0.0F;
    float f2 = 0.0F;
    float f3 = 0.0F;
    float f1 = 0.0F;
    int i = 0;
    int j = 0;
    while (i < k)
    {
      int m = j + 1;
      float f4 = paramPath[j];
      j = m + 1;
      float f6 = paramPath[m];
      float f5 = paramPath[j];
      if ((f4 == f1) && (f6 != f2)) {
        throw new IllegalArgumentException("The Path cannot have discontinuity in the X axis.");
      }
      if (f6 < f2) {
        throw new IllegalArgumentException("The Path cannot loop back on itself.");
      }
      this.mX[i] = f6;
      this.mY[i] = f5;
      this.mArcLength = ((float)(this.mArcLength + Math.hypot(f6 - f2, f5 - f3)));
      f2 = f6;
      f3 = f5;
      f1 = f4;
      i += 1;
      j += 1;
    }
  }
  
  public float getArcLength()
  {
    return this.mArcLength;
  }
  
  public float getInterpolation(float paramFloat)
  {
    int j = 0;
    int k = this.mX.length - 1;
    if (paramFloat <= 0.0F) {
      return this.mY[0];
    }
    int i = k;
    if (paramFloat >= 1.0F) {
      return this.mY[k];
    }
    while (i - j > 1)
    {
      k = (j + i) / 2;
      if (paramFloat < this.mX[k]) {
        i = k;
      } else {
        j = k;
      }
    }
    float f = this.mX[i] - this.mX[j];
    if (f == 0.0F) {
      return this.mY[j];
    }
    paramFloat = (paramFloat - this.mX[j]) / f;
    f = this.mY[j];
    return (this.mY[i] - f) * paramFloat + f;
  }
  
  public float getX(float paramFloat)
  {
    int j = 0;
    int i = this.mY.length - 1;
    if (paramFloat <= 0.0F) {
      return this.mX[i];
    }
    if (paramFloat >= 1.0F) {
      return this.mX[0];
    }
    while (i - j > 1)
    {
      int k = (j + i) / 2;
      if (paramFloat < this.mY[k]) {
        j = k;
      } else {
        i = k;
      }
    }
    float f = this.mY[i] - this.mY[j];
    if (f == 0.0F) {
      return this.mX[j];
    }
    paramFloat = (paramFloat - this.mY[j]) / f;
    f = this.mX[j];
    return (this.mX[i] - f) * paramFloat + f;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\misc\FreePathInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */