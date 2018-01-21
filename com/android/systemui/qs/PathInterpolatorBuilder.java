package com.android.systemui.qs;

import android.graphics.Path;
import android.view.animation.BaseInterpolator;
import android.view.animation.Interpolator;

public class PathInterpolatorBuilder
{
  private float[] mDist;
  private float[] mX;
  private float[] mY;
  
  public PathInterpolatorBuilder(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    initCubic(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
  }
  
  private void initCubic(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    Path localPath = new Path();
    localPath.moveTo(0.0F, 0.0F);
    localPath.cubicTo(paramFloat1, paramFloat2, paramFloat3, paramFloat4, 1.0F, 1.0F);
    initPath(localPath);
  }
  
  private void initPath(Path paramPath)
  {
    paramPath = paramPath.approximate(0.002F);
    int k = paramPath.length / 3;
    if ((paramPath[1] != 0.0F) || (paramPath[2] != 0.0F)) {}
    while ((paramPath[(paramPath.length - 2)] != 1.0F) || (paramPath[(paramPath.length - 1)] != 1.0F)) {
      throw new IllegalArgumentException("The Path must start at (0,0) and end at (1,1)");
    }
    this.mX = new float[k];
    this.mY = new float[k];
    this.mDist = new float[k];
    float f2 = 0.0F;
    float f1 = 0.0F;
    int i = 0;
    int j = 0;
    while (i < k)
    {
      int m = j + 1;
      float f3 = paramPath[j];
      j = m + 1;
      float f4 = paramPath[m];
      float f5 = paramPath[j];
      if ((f3 == f1) && (f4 != f2)) {
        throw new IllegalArgumentException("The Path cannot have discontinuity in the X axis.");
      }
      if (f4 < f2) {
        throw new IllegalArgumentException("The Path cannot loop back on itself.");
      }
      this.mX[i] = f4;
      this.mY[i] = f5;
      if (i > 0)
      {
        f1 = this.mX[i] - this.mX[(i - 1)];
        f2 = this.mY[i] - this.mY[(i - 1)];
        f1 = (float)Math.sqrt(f1 * f1 + f2 * f2);
        this.mDist[i] = (this.mDist[(i - 1)] + f1);
      }
      f2 = f4;
      f1 = f3;
      i += 1;
      j += 1;
    }
    f1 = this.mDist[(this.mDist.length - 1)];
    i = 0;
    while (i < k)
    {
      paramPath = this.mDist;
      paramPath[i] /= f1;
      i += 1;
    }
  }
  
  public Interpolator getXInterpolator()
  {
    return new PathInterpolator(this.mDist, this.mX, null);
  }
  
  public Interpolator getYInterpolator()
  {
    return new PathInterpolator(this.mDist, this.mY, null);
  }
  
  private static class PathInterpolator
    extends BaseInterpolator
  {
    private final float[] mX;
    private final float[] mY;
    
    private PathInterpolator(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
    {
      this.mX = paramArrayOfFloat1;
      this.mY = paramArrayOfFloat2;
    }
    
    public float getInterpolation(float paramFloat)
    {
      if (paramFloat <= 0.0F) {
        return 0.0F;
      }
      if (paramFloat >= 1.0F) {
        return 1.0F;
      }
      int i = 0;
      int j = this.mX.length - 1;
      while (j - i > 1)
      {
        int k = (i + j) / 2;
        if (paramFloat < this.mX[k]) {
          j = k;
        } else {
          i = k;
        }
      }
      float f = this.mX[j] - this.mX[i];
      if (f == 0.0F) {
        return this.mY[i];
      }
      paramFloat = (paramFloat - this.mX[i]) / f;
      f = this.mY[i];
      return (this.mY[j] - f) * paramFloat + f;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\PathInterpolatorBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */