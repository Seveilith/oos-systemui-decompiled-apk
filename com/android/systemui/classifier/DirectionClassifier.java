package com.android.systemui.classifier;

import java.util.ArrayList;

public class DirectionClassifier
  extends StrokeClassifier
{
  public DirectionClassifier(ClassifierData paramClassifierData) {}
  
  public float getFalseTouchEvaluation(int paramInt, Stroke paramStroke)
  {
    Point localPoint = (Point)paramStroke.getPoints().get(0);
    paramStroke = (Point)paramStroke.getPoints().get(paramStroke.getPoints().size() - 1);
    return DirectionEvaluator.evaluate(paramStroke.x - localPoint.x, paramStroke.y - localPoint.y, paramInt);
  }
  
  public String getTag()
  {
    return "DIR";
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\DirectionClassifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */