package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

public class KeyguardIndicationTextView
  extends TextView
{
  public KeyguardIndicationTextView(Context paramContext)
  {
    super(paramContext);
  }
  
  public KeyguardIndicationTextView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public KeyguardIndicationTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public KeyguardIndicationTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public void switchIndication(int paramInt)
  {
    switchIndication(getResources().getText(paramInt));
  }
  
  public void switchIndication(CharSequence paramCharSequence)
  {
    if (TextUtils.isEmpty(paramCharSequence))
    {
      setVisibility(4);
      return;
    }
    setVisibility(0);
    setText(paramCharSequence);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\KeyguardIndicationTextView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */