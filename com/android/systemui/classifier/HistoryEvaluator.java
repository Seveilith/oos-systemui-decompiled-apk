package com.android.systemui.classifier;

import android.os.SystemClock;
import java.util.ArrayList;

public class HistoryEvaluator
{
  private final ArrayList<Data> mGestureWeights = new ArrayList();
  private long mLastUpdate = SystemClock.elapsedRealtime();
  private final ArrayList<Data> mStrokes = new ArrayList();
  
  private void decayValue()
  {
    long l = SystemClock.elapsedRealtime();
    if (l <= this.mLastUpdate) {
      return;
    }
    float f = (float)Math.pow(0.8999999761581421D, (float)(l - this.mLastUpdate) / 50.0F);
    decayValue(this.mStrokes, f);
    decayValue(this.mGestureWeights, f);
    this.mLastUpdate = l;
  }
  
  private void decayValue(ArrayList<Data> paramArrayList, float paramFloat)
  {
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      Data localData = (Data)paramArrayList.get(i);
      localData.weight *= paramFloat;
      i += 1;
    }
    while ((!paramArrayList.isEmpty()) && (isZero(((Data)paramArrayList.get(0)).weight))) {
      paramArrayList.remove(0);
    }
  }
  
  private boolean isZero(float paramFloat)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramFloat <= 1.0E-5F)
    {
      bool1 = bool2;
      if (paramFloat >= -1.0E-5F) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private float weightedAverage(ArrayList<Data> paramArrayList)
  {
    float f2 = 0.0F;
    float f1 = 0.0F;
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      Data localData = (Data)paramArrayList.get(i);
      f2 += localData.evaluation * localData.weight;
      f1 += localData.weight;
      i += 1;
    }
    if (f1 == 0.0F) {
      return 0.0F;
    }
    return f2 / f1;
  }
  
  public void addGesture(float paramFloat)
  {
    decayValue();
    this.mGestureWeights.add(new Data(paramFloat));
  }
  
  public void addStroke(float paramFloat)
  {
    decayValue();
    this.mStrokes.add(new Data(paramFloat));
  }
  
  public float getEvaluation()
  {
    return weightedAverage(this.mStrokes) + weightedAverage(this.mGestureWeights);
  }
  
  private static class Data
  {
    public float evaluation;
    public float weight;
    
    public Data(float paramFloat)
    {
      this.evaluation = paramFloat;
      this.weight = 1.0F;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\HistoryEvaluator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */