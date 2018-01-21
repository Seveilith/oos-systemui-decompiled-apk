package android.support.v4.graphics;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

public final class ColorUtils
{
  private static final ThreadLocal<double[]> TEMP_ARRAY = new ThreadLocal();
  
  @ColorInt
  public static int HSLToColor(@NonNull float[] paramArrayOfFloat)
  {
    float f1 = paramArrayOfFloat[0];
    float f2 = paramArrayOfFloat[1];
    float f3 = paramArrayOfFloat[2];
    f2 = (1.0F - Math.abs(2.0F * f3 - 1.0F)) * f2;
    f3 -= 0.5F * f2;
    float f4 = f2 * (1.0F - Math.abs(f1 / 60.0F % 2.0F - 1.0F));
    int m = (int)f1 / 60;
    int k = 0;
    int j = 0;
    int i = 0;
    switch (m)
    {
    }
    for (;;)
    {
      return Color.rgb(constrain(k, 0, 255), constrain(j, 0, 255), constrain(i, 0, 255));
      k = Math.round((f2 + f3) * 255.0F);
      j = Math.round((f4 + f3) * 255.0F);
      i = Math.round(255.0F * f3);
      continue;
      k = Math.round((f4 + f3) * 255.0F);
      j = Math.round((f2 + f3) * 255.0F);
      i = Math.round(255.0F * f3);
      continue;
      k = Math.round(255.0F * f3);
      j = Math.round((f2 + f3) * 255.0F);
      i = Math.round((f4 + f3) * 255.0F);
      continue;
      k = Math.round(255.0F * f3);
      j = Math.round((f4 + f3) * 255.0F);
      i = Math.round((f2 + f3) * 255.0F);
      continue;
      k = Math.round((f4 + f3) * 255.0F);
      j = Math.round(255.0F * f3);
      i = Math.round((f2 + f3) * 255.0F);
      continue;
      k = Math.round((f2 + f3) * 255.0F);
      j = Math.round(255.0F * f3);
      i = Math.round((f4 + f3) * 255.0F);
    }
  }
  
  public static void RGBToHSL(@IntRange(from=0L, to=255L) int paramInt1, @IntRange(from=0L, to=255L) int paramInt2, @IntRange(from=0L, to=255L) int paramInt3, @NonNull float[] paramArrayOfFloat)
  {
    float f1 = paramInt1 / 255.0F;
    float f3 = paramInt2 / 255.0F;
    float f5 = paramInt3 / 255.0F;
    float f6 = Math.max(f1, Math.max(f3, f5));
    float f7 = Math.min(f1, Math.min(f3, f5));
    float f2 = f6 - f7;
    float f4 = (f6 + f7) / 2.0F;
    if (f6 == f7)
    {
      f1 = 0.0F;
      f2 = 0.0F;
      f3 = 60.0F * f2 % 360.0F;
      f2 = f3;
      if (f3 < 0.0F) {
        f2 = f3 + 360.0F;
      }
      paramArrayOfFloat[0] = constrain(f2, 0.0F, 360.0F);
      paramArrayOfFloat[1] = constrain(f1, 0.0F, 1.0F);
      paramArrayOfFloat[2] = constrain(f4, 0.0F, 1.0F);
      return;
    }
    if (f6 == f1) {
      f1 = (f3 - f5) / f2 % 6.0F;
    }
    for (;;)
    {
      f3 = f2 / (1.0F - Math.abs(2.0F * f4 - 1.0F));
      f2 = f1;
      f1 = f3;
      break;
      if (f6 == f3) {
        f1 = (f5 - f1) / f2 + 2.0F;
      } else {
        f1 = (f1 - f3) / f2 + 4.0F;
      }
    }
  }
  
  public static void RGBToXYZ(@IntRange(from=0L, to=255L) int paramInt1, @IntRange(from=0L, to=255L) int paramInt2, @IntRange(from=0L, to=255L) int paramInt3, @NonNull double[] paramArrayOfDouble)
  {
    if (paramArrayOfDouble.length != 3) {
      throw new IllegalArgumentException("outXyz must have a length of 3.");
    }
    double d1 = paramInt1 / 255.0D;
    double d2;
    label66:
    double d3;
    if (d1 < 0.04045D)
    {
      d1 /= 12.92D;
      d2 = paramInt2 / 255.0D;
      if (d2 >= 0.04045D) {
        break label194;
      }
      d2 /= 12.92D;
      d3 = paramInt3 / 255.0D;
      if (d3 >= 0.04045D) {
        break label215;
      }
    }
    label194:
    label215:
    for (d3 /= 12.92D;; d3 = Math.pow((0.055D + d3) / 1.055D, 2.4D))
    {
      paramArrayOfDouble[0] = ((0.4124D * d1 + 0.3576D * d2 + 0.1805D * d3) * 100.0D);
      paramArrayOfDouble[1] = ((0.2126D * d1 + 0.7152D * d2 + 0.0722D * d3) * 100.0D);
      paramArrayOfDouble[2] = ((0.0193D * d1 + 0.1192D * d2 + 0.9505D * d3) * 100.0D);
      return;
      d1 = Math.pow((0.055D + d1) / 1.055D, 2.4D);
      break;
      d2 = Math.pow((0.055D + d2) / 1.055D, 2.4D);
      break label66;
    }
  }
  
  public static double calculateContrast(@ColorInt int paramInt1, @ColorInt int paramInt2)
  {
    if (Color.alpha(paramInt2) != 255) {
      throw new IllegalArgumentException("background can not be translucent: #" + Integer.toHexString(paramInt2));
    }
    int i = paramInt1;
    if (Color.alpha(paramInt1) < 255) {
      i = compositeColors(paramInt1, paramInt2);
    }
    double d1 = calculateLuminance(i) + 0.05D;
    double d2 = calculateLuminance(paramInt2) + 0.05D;
    return Math.max(d1, d2) / Math.min(d1, d2);
  }
  
  @FloatRange(from=0.0D, to=1.0D)
  public static double calculateLuminance(@ColorInt int paramInt)
  {
    double[] arrayOfDouble = getTempDouble3Array();
    colorToXYZ(paramInt, arrayOfDouble);
    return arrayOfDouble[1] / 100.0D;
  }
  
  @VisibleForTesting
  static float circularInterpolate(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    float f1 = paramFloat1;
    float f2 = paramFloat2;
    if (Math.abs(paramFloat2 - paramFloat1) > 180.0F)
    {
      if (paramFloat2 <= paramFloat1) {
        break label43;
      }
      f1 = paramFloat1 + 360.0F;
      f2 = paramFloat2;
    }
    for (;;)
    {
      return ((f2 - f1) * paramFloat3 + f1) % 360.0F;
      label43:
      f2 = paramFloat2 + 360.0F;
      f1 = paramFloat1;
    }
  }
  
  public static void colorToHSL(@ColorInt int paramInt, @NonNull float[] paramArrayOfFloat)
  {
    RGBToHSL(Color.red(paramInt), Color.green(paramInt), Color.blue(paramInt), paramArrayOfFloat);
  }
  
  public static void colorToXYZ(@ColorInt int paramInt, @NonNull double[] paramArrayOfDouble)
  {
    RGBToXYZ(Color.red(paramInt), Color.green(paramInt), Color.blue(paramInt), paramArrayOfDouble);
  }
  
  private static int compositeAlpha(int paramInt1, int paramInt2)
  {
    return 255 - (255 - paramInt2) * (255 - paramInt1) / 255;
  }
  
  public static int compositeColors(@ColorInt int paramInt1, @ColorInt int paramInt2)
  {
    int i = Color.alpha(paramInt2);
    int j = Color.alpha(paramInt1);
    int k = compositeAlpha(j, i);
    return Color.argb(k, compositeComponent(Color.red(paramInt1), j, Color.red(paramInt2), i, k), compositeComponent(Color.green(paramInt1), j, Color.green(paramInt2), i, k), compositeComponent(Color.blue(paramInt1), j, Color.blue(paramInt2), i, k));
  }
  
  private static int compositeComponent(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (paramInt5 == 0) {
      return 0;
    }
    return (paramInt1 * 255 * paramInt2 + paramInt3 * paramInt4 * (255 - paramInt2)) / (paramInt5 * 255);
  }
  
  private static float constrain(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramFloat1 < paramFloat2) {
      return paramFloat2;
    }
    if (paramFloat1 > paramFloat3) {
      return paramFloat3;
    }
    return paramFloat1;
  }
  
  private static int constrain(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 < paramInt2) {
      return paramInt2;
    }
    if (paramInt1 > paramInt3) {
      return paramInt3;
    }
    return paramInt1;
  }
  
  private static double[] getTempDouble3Array()
  {
    double[] arrayOfDouble2 = (double[])TEMP_ARRAY.get();
    double[] arrayOfDouble1 = arrayOfDouble2;
    if (arrayOfDouble2 == null)
    {
      arrayOfDouble1 = new double[3];
      TEMP_ARRAY.set(arrayOfDouble1);
    }
    return arrayOfDouble1;
  }
  
  @ColorInt
  public static int setAlphaComponent(@ColorInt int paramInt1, @IntRange(from=0L, to=255L) int paramInt2)
  {
    if ((paramInt2 < 0) || (paramInt2 > 255)) {
      throw new IllegalArgumentException("alpha must be between 0 and 255.");
    }
    return 0xFFFFFF & paramInt1 | paramInt2 << 24;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\graphics\ColorUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */