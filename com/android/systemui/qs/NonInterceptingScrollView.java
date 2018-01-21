package com.android.systemui.qs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class NonInterceptingScrollView
  extends ScrollView
{
  public NonInterceptingScrollView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getActionMasked())
    {
    }
    for (;;)
    {
      return super.onTouchEvent(paramMotionEvent);
      if (canScrollVertically(1)) {
        requestDisallowInterceptTouchEvent(true);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\NonInterceptingScrollView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */