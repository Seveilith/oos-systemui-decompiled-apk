package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnLayoutChangeListener;

public class KeyguardUserSwitcherScrim
  extends Drawable
  implements View.OnLayoutChangeListener
{
  private int mAlpha = 255;
  private int mDarkColor;
  private int mLayoutWidth;
  private Paint mRadialGradientPaint = new Paint();
  private int mTop;
  
  public KeyguardUserSwitcherScrim(Context paramContext)
  {
    this.mDarkColor = paramContext.getColor(2131493037);
  }
  
  private void updatePaint()
  {
    if (this.mLayoutWidth == 0) {
      return;
    }
    float f1 = this.mLayoutWidth * 2.5F;
    Paint localPaint;
    if (getLayoutDirection() == 0)
    {
      i = 1;
      localPaint = this.mRadialGradientPaint;
      if (i == 0) {
        break label141;
      }
    }
    label141:
    for (int i = this.mLayoutWidth;; i = 0)
    {
      float f2 = i;
      i = Color.argb((int)(Color.alpha(this.mDarkColor) * this.mAlpha / 255.0F), 0, 0, 0);
      float f3 = Math.max(0.0F, this.mLayoutWidth * 0.75F / f1);
      Shader.TileMode localTileMode = Shader.TileMode.CLAMP;
      localPaint.setShader(new RadialGradient(f2, 0.0F, f1, new int[] { i, 0 }, new float[] { f3, 1.0F }, localTileMode));
      return;
      i = 0;
      break;
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    int i;
    Rect localRect;
    float f3;
    float f1;
    if (getLayoutDirection() == 0)
    {
      i = 1;
      localRect = getBounds();
      f3 = localRect.width() * 2.5F;
      f1 = this.mTop + localRect.height();
      paramCanvas.translate(0.0F, -this.mTop);
      paramCanvas.scale(1.0F, f1 * 2.5F / f3);
      if (i == 0) {
        break label109;
      }
      f1 = localRect.right - f3;
      label77:
      if (i == 0) {
        break label114;
      }
    }
    label109:
    label114:
    for (float f2 = localRect.right;; f2 = localRect.left + f3)
    {
      paramCanvas.drawRect(f1, 0.0F, f2, f3, this.mRadialGradientPaint);
      return;
      i = 0;
      break;
      f1 = 0.0F;
      break label77;
    }
  }
  
  public int getAlpha()
  {
    return this.mAlpha;
  }
  
  public int getOpacity()
  {
    return -3;
  }
  
  public void onLayoutChange(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
  {
    if ((paramInt1 != paramInt5) || (paramInt2 != paramInt6)) {
      break label29;
    }
    for (;;)
    {
      this.mLayoutWidth = (paramInt3 - paramInt1);
      this.mTop = paramInt2;
      updatePaint();
      label29:
      return;
      if (paramInt3 == paramInt7) {
        if (paramInt4 == paramInt8) {
          break;
        }
      }
    }
  }
  
  public void setAlpha(int paramInt)
  {
    this.mAlpha = paramInt;
    updatePaint();
    invalidateSelf();
  }
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\KeyguardUserSwitcherScrim.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */