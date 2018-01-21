package com.android.systemui.classifier;

public class EndPointLengthClassifier
  extends StrokeClassifier
{
  public EndPointLengthClassifier(ClassifierData paramClassifierData) {}
  
  public float getFalseTouchEvaluation(int paramInt, Stroke paramStroke)
  {
    return EndPointLengthEvaluator.evaluate(paramStroke.getEndPointLength());
  }
  
  public String getTag()
  {
    return "END_LNGTH";
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\EndPointLengthClassifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */