package com.android.systemui.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.SeekBar;

public class ToggleSeekBar
  extends SeekBar
{
  private String mAccessibilityLabel;
  
  public ToggleSeekBar(Context paramContext)
  {
    super(paramContext);
  }
  
  public ToggleSeekBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public ToggleSeekBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
    if (this.mAccessibilityLabel != null) {
      paramAccessibilityNodeInfo.setText(this.mAccessibilityLabel);
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!isEnabled()) {
      setEnabled(true);
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void setAccessibilityLabel(String paramString)
  {
    this.mAccessibilityLabel = paramString;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\settings\ToggleSeekBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */