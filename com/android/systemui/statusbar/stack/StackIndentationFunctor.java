package com.android.systemui.statusbar.stack;

public abstract class StackIndentationFunctor
{
  protected int mDistanceToPeekStart;
  protected int mMaxItemsInStack;
  protected int mPeekSize;
  protected boolean mStackStartsAtPeek;
  protected int mTotalTransitionDistance;
  
  StackIndentationFunctor(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mDistanceToPeekStart = paramInt3;
    if (this.mDistanceToPeekStart == 0) {
      bool = true;
    }
    this.mStackStartsAtPeek = bool;
    this.mMaxItemsInStack = paramInt1;
    this.mPeekSize = paramInt2;
    updateTotalTransitionDistance();
  }
  
  private void updateTotalTransitionDistance()
  {
    this.mTotalTransitionDistance = (this.mDistanceToPeekStart + this.mPeekSize);
  }
  
  public abstract float getValue(float paramFloat);
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\stack\StackIndentationFunctor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */