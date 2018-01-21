package com.android.systemui.stackdivider;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

public class MinimizedDockShadow
  extends View
{
  private int mDockSide = -1;
  private final Paint mShadowPaint = new Paint();
  
  public MinimizedDockShadow(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void updatePaint(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = this.mContext.getResources().getColor(2131493056, null);
    int j = this.mContext.getResources().getColor(2131493057, null);
    int k = Color.argb((Color.alpha(i) + Color.alpha(j)) / 2, 0, 0, 0);
    int m = Color.argb((int)(Color.alpha(i) * 0.25F + Color.alpha(j) * 0.75F), 0, 0, 0);
    if (this.mDockSide == 2)
    {
      localPaint = this.mShadowPaint;
      f = paramInt4 - paramInt2;
      localTileMode = Shader.TileMode.CLAMP;
      localPaint.setShader(new LinearGradient(0.0F, 0.0F, 0.0F, f, new int[] { i, k, m, j }, new float[] { 0.0F, 0.35F, 0.6F, 1.0F }, localTileMode));
    }
    do
    {
      return;
      if (this.mDockSide == 1)
      {
        localPaint = this.mShadowPaint;
        f = paramInt3 - paramInt1;
        localTileMode = Shader.TileMode.CLAMP;
        localPaint.setShader(new LinearGradient(0.0F, 0.0F, f, 0.0F, new int[] { i, k, m, j }, new float[] { 0.0F, 0.35F, 0.6F, 1.0F }, localTileMode));
        return;
      }
    } while (this.mDockSide != 3);
    Paint localPaint = this.mShadowPaint;
    float f = paramInt3 - paramInt1;
    Shader.TileMode localTileMode = Shader.TileMode.CLAMP;
    localPaint.setShader(new LinearGradient(f, 0.0F, 0.0F, 0.0F, new int[] { i, k, m, j }, new float[] { 0.0F, 0.35F, 0.6F, 1.0F }, localTileMode));
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    paramCanvas.drawRect(0.0F, 0.0F, getWidth(), getHeight(), this.mShadowPaint);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramBoolean)
    {
      updatePaint(paramInt1, paramInt2, paramInt3, paramInt4);
      invalidate();
    }
  }
  
  public void setDockSide(int paramInt)
  {
    if (paramInt != this.mDockSide)
    {
      this.mDockSide = paramInt;
      updatePaint(getLeft(), getTop(), getRight(), getBottom());
      invalidate();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\stackdivider\MinimizedDockShadow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */