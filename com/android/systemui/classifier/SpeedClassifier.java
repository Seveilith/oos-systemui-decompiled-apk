package com.android.systemui.classifier;

public class SpeedClassifier
  extends StrokeClassifier
{
  private final float NANOS_TO_SECONDS = 1.0E9F;
  
  public SpeedClassifier(ClassifierData paramClassifierData) {}
  
  public float getFalseTouchEvaluation(int paramInt, Stroke paramStroke)
  {
    float f = (float)paramStroke.getDurationNanos() / 1.0E9F;
    if (f == 0.0F) {
      return SpeedEvaluator.evaluate(0.0F);
    }
    return SpeedEvaluator.evaluate(paramStroke.getTotalLength() / f);
  }
  
  public String getTag()
  {
    return "SPD";
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\SpeedClassifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */