package com.android.systemui.tuner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;

public class PreviewNavInflater
  extends NavigationBarInflaterView
{
  public PreviewNavInflater(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private boolean isValidLayout(String paramString)
  {
    if (paramString == null) {
      return true;
    }
    int j = 0;
    int m = 0;
    int i = 0;
    while (i < paramString.length())
    {
      int n = m;
      int k = j;
      if (paramString.charAt(i) == ";".charAt(0))
      {
        if ((i == 0) || (i - m == 1)) {
          return false;
        }
        n = i;
        k = j + 1;
      }
      i += 1;
      m = n;
      j = k;
    }
    return (j == 2) && (paramString.length() - m != 1);
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    TunerService.get(getContext()).removeTunable(this);
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    return true;
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    if ("sysui_nav_bar".equals(paramString1))
    {
      if (isValidLayout(paramString2)) {
        super.onTuningChanged(paramString1, paramString2);
      }
      return;
    }
    super.onTuningChanged(paramString1, paramString2);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tuner\PreviewNavInflater.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */