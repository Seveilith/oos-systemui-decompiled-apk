package android.support.v17.leanback.widget;

class WindowAlignment
{
  public final Axis horizontal = new Axis("horizontal");
  private Axis mMainAxis = this.horizontal;
  private int mOrientation = 0;
  private Axis mSecondAxis = this.vertical;
  public final Axis vertical = new Axis("vertical");
  
  public final Axis mainAxis()
  {
    return this.mMainAxis;
  }
  
  public final void reset()
  {
    mainAxis().reset();
  }
  
  public final Axis secondAxis()
  {
    return this.mSecondAxis;
  }
  
  public final void setOrientation(int paramInt)
  {
    this.mOrientation = paramInt;
    if (this.mOrientation == 0)
    {
      this.mMainAxis = this.horizontal;
      this.mSecondAxis = this.vertical;
      return;
    }
    this.mMainAxis = this.vertical;
    this.mSecondAxis = this.horizontal;
  }
  
  public String toString()
  {
    return "horizontal=" + this.horizontal.toString() + "; vertical=" + this.vertical.toString();
  }
  
  public static class Axis
  {
    private int mMaxEdge;
    private int mMaxScroll;
    private int mMinEdge;
    private int mMinScroll;
    private String mName;
    private int mPaddingHigh;
    private int mPaddingLow;
    private boolean mReversedFlow;
    private float mScrollCenter;
    private int mSize;
    private int mWindowAlignment = 3;
    private int mWindowAlignmentOffset = 0;
    private float mWindowAlignmentOffsetPercent = 50.0F;
    
    public Axis(String paramString)
    {
      reset();
      this.mName = paramString;
    }
    
    public final int getClientSize()
    {
      return this.mSize - this.mPaddingLow - this.mPaddingHigh;
    }
    
    public final int getMaxEdge()
    {
      return this.mMaxEdge;
    }
    
    public final int getMaxScroll()
    {
      return this.mMaxScroll;
    }
    
    public final int getMinEdge()
    {
      return this.mMinEdge;
    }
    
    public final int getMinScroll()
    {
      return this.mMinScroll;
    }
    
    public final int getPaddingHigh()
    {
      return this.mPaddingHigh;
    }
    
    public final int getPaddingLow()
    {
      return this.mPaddingLow;
    }
    
    public final int getSize()
    {
      return this.mSize;
    }
    
    public final int getSystemScrollPos(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      int j;
      int i;
      boolean bool2;
      if (!this.mReversedFlow) {
        if (this.mWindowAlignmentOffset >= 0)
        {
          j = this.mWindowAlignmentOffset - this.mPaddingLow;
          i = j;
          if (this.mWindowAlignmentOffsetPercent != -1.0F) {
            i = j + (int)(this.mSize * this.mWindowAlignmentOffsetPercent / 100.0F);
          }
          j = getClientSize();
          boolean bool1 = isMinUnknown();
          bool2 = isMaxUnknown();
          if ((!bool1) && (!bool2)) {
            break label225;
          }
          label86:
          if (bool1) {
            break label288;
          }
          if (this.mReversedFlow) {
            break label279;
          }
          if ((this.mWindowAlignment & 0x1) == 0) {
            break label288;
          }
        }
      }
      label225:
      label279:
      while ((this.mWindowAlignment & 0x2) != 0)
      {
        if ((!paramBoolean1) && (paramInt - this.mMinEdge > i)) {
          break label288;
        }
        return this.mMinEdge - this.mPaddingLow;
        j = this.mSize + this.mWindowAlignmentOffset - this.mPaddingLow;
        break;
        if (this.mWindowAlignmentOffset >= 0) {}
        for (j = this.mSize - this.mWindowAlignmentOffset - this.mPaddingLow;; j = -this.mWindowAlignmentOffset - this.mPaddingLow)
        {
          i = j;
          if (this.mWindowAlignmentOffsetPercent == -1.0F) {
            break;
          }
          i = j - (int)(this.mSize * this.mWindowAlignmentOffsetPercent / 100.0F);
          break;
        }
        if (((this.mWindowAlignment & 0x3) != 3) || (this.mMaxEdge - this.mMinEdge > j)) {
          break label86;
        }
        if (this.mReversedFlow) {
          return this.mMaxEdge - this.mPaddingLow - j;
        }
        return this.mMinEdge - this.mPaddingLow;
      }
      label288:
      if (!bool2) {
        if (!this.mReversedFlow)
        {
          if ((this.mWindowAlignment & 0x2) == 0) {}
        }
        else {
          while ((this.mWindowAlignment & 0x1) != 0)
          {
            if ((!paramBoolean2) && (this.mMaxEdge - paramInt > j - i)) {
              break;
            }
            return this.mMaxEdge - this.mPaddingLow - j;
          }
        }
      }
      return paramInt - i - this.mPaddingLow;
    }
    
    public final void invalidateScrollMax()
    {
      this.mMaxEdge = Integer.MAX_VALUE;
      this.mMaxScroll = Integer.MAX_VALUE;
    }
    
    public final void invalidateScrollMin()
    {
      this.mMinEdge = Integer.MIN_VALUE;
      this.mMinScroll = Integer.MIN_VALUE;
    }
    
    public final boolean isMaxUnknown()
    {
      return this.mMaxEdge == Integer.MAX_VALUE;
    }
    
    public final boolean isMinUnknown()
    {
      return this.mMinEdge == Integer.MIN_VALUE;
    }
    
    void reset()
    {
      this.mScrollCenter = -2.14748365E9F;
      this.mMinEdge = Integer.MIN_VALUE;
      this.mMaxEdge = Integer.MAX_VALUE;
    }
    
    public final void setMaxEdge(int paramInt)
    {
      this.mMaxEdge = paramInt;
    }
    
    public final void setMaxScroll(int paramInt)
    {
      this.mMaxScroll = paramInt;
    }
    
    public final void setMinEdge(int paramInt)
    {
      this.mMinEdge = paramInt;
    }
    
    public final void setMinScroll(int paramInt)
    {
      this.mMinScroll = paramInt;
    }
    
    public final void setPadding(int paramInt1, int paramInt2)
    {
      this.mPaddingLow = paramInt1;
      this.mPaddingHigh = paramInt2;
    }
    
    public final void setReversedFlow(boolean paramBoolean)
    {
      this.mReversedFlow = paramBoolean;
    }
    
    public final void setSize(int paramInt)
    {
      this.mSize = paramInt;
    }
    
    public final void setWindowAlignment(int paramInt)
    {
      this.mWindowAlignment = paramInt;
    }
    
    public String toString()
    {
      return "center: " + this.mScrollCenter + " min:" + this.mMinEdge + " max:" + this.mMaxEdge;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\WindowAlignment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */