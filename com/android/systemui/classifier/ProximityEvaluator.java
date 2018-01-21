package com.android.systemui.classifier;

public class ProximityEvaluator
{
  public static float evaluate(float paramFloat, int paramInt)
  {
    float f2 = 0.0F;
    float f1 = 0.1F;
    if (paramInt == 0) {
      f1 = 1.0F;
    }
    if (paramFloat >= f1) {
      f2 = (float)2.0D;
    }
    return f2;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\ProximityEvaluator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */