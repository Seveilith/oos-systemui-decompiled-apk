package com.android.systemui.classifier;

import android.util.SparseArray;
import android.view.MotionEvent;
import java.util.ArrayList;

public class ClassifierData
{
  private SparseArray<Stroke> mCurrentStrokes = new SparseArray();
  private final float mDpi;
  private ArrayList<Stroke> mEndingStrokes = new ArrayList();
  
  public ClassifierData(float paramFloat)
  {
    this.mDpi = paramFloat;
  }
  
  public void cleanUp(MotionEvent paramMotionEvent)
  {
    this.mEndingStrokes.clear();
    int j = paramMotionEvent.getActionMasked();
    int i = 0;
    if (i < paramMotionEvent.getPointerCount())
    {
      int k = paramMotionEvent.getPointerId(i);
      if ((j == 1) || (j == 3)) {}
      for (;;)
      {
        this.mCurrentStrokes.remove(k);
        do
        {
          i += 1;
          break;
        } while ((j != 6) || (i != paramMotionEvent.getActionIndex()));
      }
    }
  }
  
  public ArrayList<Stroke> getEndingStrokes()
  {
    return this.mEndingStrokes;
  }
  
  public Stroke getStroke(int paramInt)
  {
    return (Stroke)this.mCurrentStrokes.get(paramInt);
  }
  
  public void update(MotionEvent paramMotionEvent)
  {
    this.mEndingStrokes.clear();
    int j = paramMotionEvent.getActionMasked();
    if (j == 0) {
      this.mCurrentStrokes.clear();
    }
    int i = 0;
    if (i < paramMotionEvent.getPointerCount())
    {
      int k = paramMotionEvent.getPointerId(i);
      if (this.mCurrentStrokes.get(k) == null) {
        this.mCurrentStrokes.put(k, new Stroke(paramMotionEvent.getEventTimeNano(), this.mDpi));
      }
      ((Stroke)this.mCurrentStrokes.get(k)).addPoint(paramMotionEvent.getX(i), paramMotionEvent.getY(i), paramMotionEvent.getEventTimeNano());
      if ((j == 1) || (j == 3)) {}
      for (;;)
      {
        this.mEndingStrokes.add(getStroke(k));
        do
        {
          i += 1;
          break;
        } while ((j != 6) || (i != paramMotionEvent.getActionIndex()));
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\ClassifierData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */