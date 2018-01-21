package com.android.systemui.classifier;

public class EndPointRatioClassifier
  extends StrokeClassifier
{
  public EndPointRatioClassifier(ClassifierData paramClassifierData)
  {
    this.mClassifierData = paramClassifierData;
  }
  
  public float getFalseTouchEvaluation(int paramInt, Stroke paramStroke)
  {
    if (paramStroke.getTotalLength() == 0.0F) {}
    for (float f = 1.0F;; f = paramStroke.getEndPointLength() / paramStroke.getTotalLength()) {
      return EndPointRatioEvaluator.evaluate(f);
    }
  }
  
  public String getTag()
  {
    return "END_RTIO";
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\EndPointRatioClassifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */