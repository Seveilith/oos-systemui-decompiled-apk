package android.support.v17.leanback.widget.picker;

public class PickerColumn
{
  private int mCurrentValue;
  private String mLabelFormat;
  private int mMaxValue;
  private int mMinValue;
  private CharSequence[] mStaticLabels;
  
  public int getCount()
  {
    return this.mMaxValue - this.mMinValue + 1;
  }
  
  public int getCurrentValue()
  {
    return this.mCurrentValue;
  }
  
  public CharSequence getLabelFor(int paramInt)
  {
    if (this.mStaticLabels == null) {
      return String.format(this.mLabelFormat, new Object[] { Integer.valueOf(paramInt) });
    }
    return this.mStaticLabels[paramInt];
  }
  
  public int getMaxValue()
  {
    return this.mMaxValue;
  }
  
  public int getMinValue()
  {
    return this.mMinValue;
  }
  
  public void setCurrentValue(int paramInt)
  {
    this.mCurrentValue = paramInt;
  }
  
  public void setLabelFormat(String paramString)
  {
    this.mLabelFormat = paramString;
  }
  
  public void setMaxValue(int paramInt)
  {
    this.mMaxValue = paramInt;
  }
  
  public void setMinValue(int paramInt)
  {
    this.mMinValue = paramInt;
  }
  
  public void setStaticLabels(CharSequence[] paramArrayOfCharSequence)
  {
    this.mStaticLabels = paramArrayOfCharSequence;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\picker\PickerColumn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */