package com.android.systemui.classifier;

public class DurationCountClassifier
  extends StrokeClassifier
{
  public DurationCountClassifier(ClassifierData paramClassifierData) {}
  
  public float getFalseTouchEvaluation(int paramInt, Stroke paramStroke)
  {
    return DurationCountEvaluator.evaluate(paramStroke.getDurationSeconds() / paramStroke.getCount());
  }
  
  public String getTag()
  {
    return "DUR";
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\DurationCountClassifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */