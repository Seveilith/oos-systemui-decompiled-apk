package com.android.keyguard.clock;

import android.content.Context;
import android.util.AttributeSet;
import com.android.keyguard.R.id;

public class DigitalClockHorizontal
  extends KeyguardClockWidgetView
{
  private OPTextClock mTextClock;
  
  public DigitalClockHorizontal(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public DigitalClockHorizontal(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  public DigitalClockHorizontal(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init();
  }
  
  private void init() {}
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mTextClock = ((OPTextClock)findViewById(R.id.text_clock));
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\clock\DigitalClockHorizontal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */