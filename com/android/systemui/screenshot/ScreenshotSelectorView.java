package com.android.systemui.screenshot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class ScreenshotSelectorView
  extends View
{
  private final Paint mPaintBackground = new Paint(-16777216);
  private final Paint mPaintSelection;
  private Rect mSelectionRect;
  private Point mStartPoint;
  
  public ScreenshotSelectorView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ScreenshotSelectorView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mPaintBackground.setAlpha(160);
    this.mPaintSelection = new Paint(0);
    this.mPaintSelection.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
  }
  
  public void draw(Canvas paramCanvas)
  {
    paramCanvas.drawRect(this.mLeft, this.mTop, this.mRight, this.mBottom, this.mPaintBackground);
    if (this.mSelectionRect != null) {
      paramCanvas.drawRect(this.mSelectionRect, this.mPaintSelection);
    }
  }
  
  public Rect getSelectionRect()
  {
    return this.mSelectionRect;
  }
  
  public void startSelection(int paramInt1, int paramInt2)
  {
    this.mStartPoint = new Point(paramInt1, paramInt2);
    this.mSelectionRect = new Rect(paramInt1, paramInt2, paramInt1, paramInt2);
  }
  
  public void stopSelection()
  {
    this.mStartPoint = null;
    this.mSelectionRect = null;
  }
  
  public void updateSelection(int paramInt1, int paramInt2)
  {
    if (this.mSelectionRect != null)
    {
      this.mSelectionRect.left = Math.min(this.mStartPoint.x, paramInt1);
      this.mSelectionRect.right = Math.max(this.mStartPoint.x, paramInt1);
      this.mSelectionRect.top = Math.min(this.mStartPoint.y, paramInt2);
      this.mSelectionRect.bottom = Math.max(this.mStartPoint.y, paramInt2);
      invalidate();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\screenshot\ScreenshotSelectorView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */