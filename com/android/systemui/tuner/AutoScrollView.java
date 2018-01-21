package com.android.systemui.tuner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.widget.ScrollView;

public class AutoScrollView
  extends ScrollView
{
  public AutoScrollView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public boolean onDragEvent(DragEvent paramDragEvent)
  {
    switch (paramDragEvent.getAction())
    {
    }
    int i;
    int j;
    int k;
    do
    {
      return false;
      i = (int)paramDragEvent.getY();
      j = getHeight();
      k = (int)(j * 0.1F);
      if (i < k)
      {
        scrollBy(0, i - k);
        return false;
      }
    } while (i <= j - k);
    scrollBy(0, i - j + k);
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tuner\AutoScrollView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */