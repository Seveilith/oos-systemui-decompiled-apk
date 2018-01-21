package com.android.systemui.classifier;

import android.view.MotionEvent;

public class PointerCountClassifier
  extends GestureClassifier
{
  private int mCount = 0;
  
  public PointerCountClassifier(ClassifierData paramClassifierData) {}
  
  public float getFalseTouchEvaluation(int paramInt)
  {
    return PointerCountEvaluator.evaluate(this.mCount);
  }
  
  public String getTag()
  {
    return "PTR_CNT";
  }
  
  public void onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getActionMasked();
    if (i == 0) {
      this.mCount = 1;
    }
    if (i == 5) {
      this.mCount += 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\PointerCountClassifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */