package com.android.systemui.recents.views;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.android.systemui.statusbar.AlphaOptimizedImageView;

public class FixedSizeImageView
  extends AlphaOptimizedImageView
{
  private boolean mAllowInvalidate = true;
  private boolean mAllowRelayout = true;
  
  public FixedSizeImageView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public FixedSizeImageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public FixedSizeImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public FixedSizeImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public void invalidate()
  {
    if (this.mAllowInvalidate) {
      super.invalidate();
    }
  }
  
  public void requestLayout()
  {
    if (this.mAllowRelayout) {
      super.requestLayout();
    }
  }
  
  public void setImageDrawable(Drawable paramDrawable)
  {
    int i;
    if ((paramDrawable instanceof BitmapDrawable)) {
      if (((BitmapDrawable)paramDrawable).getBitmap() == null) {
        i = 1;
      }
    }
    for (;;)
    {
      if ((paramDrawable == null) || (i != 0))
      {
        this.mAllowRelayout = false;
        this.mAllowInvalidate = false;
      }
      super.setImageDrawable(paramDrawable);
      this.mAllowRelayout = true;
      this.mAllowInvalidate = true;
      return;
      i = 0;
      continue;
      i = 0;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\FixedSizeImageView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */