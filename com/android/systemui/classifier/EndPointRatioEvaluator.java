package com.android.systemui.classifier;

public class EndPointRatioEvaluator
{
  public static float evaluate(float paramFloat)
  {
    float f2 = 0.0F;
    if (paramFloat < 0.85D) {
      f2 = 1.0F;
    }
    float f1 = f2;
    if (paramFloat < 0.75D) {
      f1 = f2 + 1.0F;
    }
    f2 = f1;
    if (paramFloat < 0.65D) {
      f2 = f1 + 1.0F;
    }
    f1 = f2;
    if (paramFloat < 0.55D) {
      f1 = f2 + 1.0F;
    }
    f2 = f1;
    if (paramFloat < 0.45D) {
      f2 = f1 + 1.0F;
    }
    f1 = f2;
    if (paramFloat < 0.35D) {
      f1 = f2 + 1.0F;
    }
    return f1;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\EndPointRatioEvaluator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */