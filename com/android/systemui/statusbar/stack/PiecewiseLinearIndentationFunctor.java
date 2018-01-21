package com.android.systemui.statusbar.stack;

import java.util.ArrayList;

public class PiecewiseLinearIndentationFunctor
  extends StackIndentationFunctor
{
  private final ArrayList<Float> mBaseValues;
  private final float mLinearPart;
  
  PiecewiseLinearIndentationFunctor(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    super(paramInt1, paramInt2, paramInt3);
    this.mBaseValues = new ArrayList(paramInt1 + 1);
    initBaseValues();
    this.mLinearPart = paramFloat;
  }
  
  private int getSumOfSquares(int paramInt)
  {
    return (paramInt + 1) * paramInt * (paramInt * 2 + 1) / 6;
  }
  
  private void initBaseValues()
  {
    int k = getSumOfSquares(this.mMaxItemsInStack - 1);
    int j = 0;
    this.mBaseValues.add(Float.valueOf(0.0F));
    int i = 0;
    while (i < this.mMaxItemsInStack - 1)
    {
      j += (this.mMaxItemsInStack - i - 1) * (this.mMaxItemsInStack - i - 1);
      this.mBaseValues.add(Float.valueOf(j / k));
      i += 1;
    }
  }
  
  public float getValue(float paramFloat)
  {
    float f1 = paramFloat;
    if (this.mStackStartsAtPeek) {
      f1 = paramFloat + 1.0F;
    }
    if (f1 < 0.0F) {
      return 0.0F;
    }
    if (f1 >= this.mMaxItemsInStack) {
      return this.mTotalTransitionDistance;
    }
    int i = (int)f1;
    paramFloat = f1 - i;
    if (i == 0) {
      return this.mDistanceToPeekStart * paramFloat;
    }
    float f2 = this.mDistanceToPeekStart;
    float f3 = ((Float)this.mBaseValues.get(i - 1)).floatValue();
    float f4 = ((Float)this.mBaseValues.get(i)).floatValue();
    return f2 + ((1.0F - this.mLinearPart) * ((1.0F - paramFloat) * f3 + f4 * paramFloat) + (f1 - 1.0F) / (this.mMaxItemsInStack - 1) * this.mLinearPart) * this.mPeekSize;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\stack\PiecewiseLinearIndentationFunctor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */