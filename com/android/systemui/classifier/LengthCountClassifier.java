package com.android.systemui.classifier;

public class LengthCountClassifier
  extends StrokeClassifier
{
  public LengthCountClassifier(ClassifierData paramClassifierData) {}
  
  public float getFalseTouchEvaluation(int paramInt, Stroke paramStroke)
  {
    return LengthCountEvaluator.evaluate(paramStroke.getTotalLength() / Math.max(1.0F, paramStroke.getCount() - 2));
  }
  
  public String getTag()
  {
    return "LEN_CNT";
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\LengthCountClassifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */