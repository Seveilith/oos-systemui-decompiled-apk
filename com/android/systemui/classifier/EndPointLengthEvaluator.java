package com.android.systemui.classifier;

public class EndPointLengthEvaluator
{
  public static float evaluate(float paramFloat)
  {
    float f2 = 0.0F;
    if (paramFloat < 0.05D) {
      f2 = (float)2.0D;
    }
    float f1 = f2;
    if (paramFloat < 0.1D) {
      f1 = (float)(f2 + 2.0D);
    }
    f2 = f1;
    if (paramFloat < 0.2D) {
      f2 = (float)(f1 + 2.0D);
    }
    f1 = f2;
    if (paramFloat < 0.3D) {
      f1 = (float)(f2 + 2.0D);
    }
    f2 = f1;
    if (paramFloat < 0.4D) {
      f2 = (float)(f1 + 2.0D);
    }
    f1 = f2;
    if (paramFloat < 0.5D) {
      f1 = (float)(f2 + 2.0D);
    }
    return f1;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\EndPointLengthEvaluator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */