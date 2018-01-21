package com.android.systemui.recents.views;

import android.graphics.Outline;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewOutlineProvider;
import com.android.systemui.recents.misc.Utilities;

public class AnimateableViewBounds
  extends ViewOutlineProvider
{
  @ViewDebug.ExportedProperty(category="recents")
  float mAlpha = 1.0F;
  @ViewDebug.ExportedProperty(category="recents")
  Rect mClipBounds = new Rect();
  @ViewDebug.ExportedProperty(category="recents")
  Rect mClipRect = new Rect();
  @ViewDebug.ExportedProperty(category="recents")
  int mCornerRadius;
  @ViewDebug.ExportedProperty(category="recents")
  Rect mLastClipBounds = new Rect();
  View mSourceView;
  
  public AnimateableViewBounds(View paramView, int paramInt)
  {
    this.mSourceView = paramView;
    this.mCornerRadius = paramInt;
  }
  
  private void updateClipBounds()
  {
    this.mClipBounds.set(Math.max(0, this.mClipRect.left), Math.max(0, this.mClipRect.top), this.mSourceView.getWidth() - Math.max(0, this.mClipRect.right), this.mSourceView.getHeight() - Math.max(0, this.mClipRect.bottom));
    if (!this.mLastClipBounds.equals(this.mClipBounds))
    {
      this.mSourceView.setClipBounds(this.mClipBounds);
      this.mSourceView.invalidateOutline();
      this.mLastClipBounds.set(this.mClipBounds);
    }
  }
  
  public float getAlpha()
  {
    return this.mAlpha;
  }
  
  public void getOutline(View paramView, Outline paramOutline)
  {
    paramOutline.setAlpha(Utilities.mapRange(this.mAlpha, 0.1F, 0.8F));
    if (this.mCornerRadius > 0)
    {
      paramOutline.setRoundRect(this.mClipRect.left, this.mClipRect.top, this.mSourceView.getWidth() - this.mClipRect.right, this.mSourceView.getHeight() - this.mClipRect.bottom, this.mCornerRadius);
      return;
    }
    paramOutline.setRect(this.mClipRect.left, this.mClipRect.top, this.mSourceView.getWidth() - this.mClipRect.right, this.mSourceView.getHeight() - this.mClipRect.bottom);
  }
  
  public void reset()
  {
    this.mClipRect.set(-1, -1, -1, -1);
    updateClipBounds();
  }
  
  void setAlpha(float paramFloat)
  {
    if (Float.compare(paramFloat, this.mAlpha) != 0)
    {
      this.mAlpha = paramFloat;
      this.mSourceView.invalidateOutline();
    }
  }
  
  public void setClipBottom(int paramInt)
  {
    this.mClipRect.bottom = paramInt;
    updateClipBounds();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\AnimateableViewBounds.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */