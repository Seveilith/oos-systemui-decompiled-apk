package com.android.systemui.statusbar;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class DismissViewButton
  extends AlphaOptimizedButton
{
  public DismissViewButton(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public DismissViewButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public DismissViewButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public DismissViewButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public void getDrawingRect(Rect paramRect)
  {
    super.getDrawingRect(paramRect);
    float f1 = ((ViewGroup)this.mParent).getTranslationX();
    float f2 = ((ViewGroup)this.mParent).getTranslationY();
    paramRect.left = ((int)(paramRect.left + f1));
    paramRect.right = ((int)(paramRect.right + f1));
    paramRect.top = ((int)(paramRect.top + f2));
    paramRect.bottom = ((int)(paramRect.bottom + f2));
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\DismissViewButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */