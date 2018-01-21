package com.android.systemui.qs;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.android.settingslib.Utils;

public class DataUsageGraph
  extends View
{
  private long mLimitLevel;
  private final int mMarkerWidth;
  private long mMaxLevel;
  private boolean mNoWarning;
  private final int mOverlimitColor;
  private final Paint mTmpPaint = new Paint();
  private final RectF mTmpRect = new RectF();
  private int mTrackColor;
  private int mUsageColor;
  private long mUsageLevel;
  private int mWarningColor;
  private long mWarningLevel;
  
  public DataUsageGraph(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramAttributeSet = paramContext.getResources();
    this.mTrackColor = paramContext.getColor(2131493005);
    this.mUsageColor = Utils.getColorAccent(paramContext);
    this.mOverlimitColor = paramContext.getColor(2131492997);
    this.mWarningColor = paramContext.getColor(2131493006);
    this.mMarkerWidth = paramAttributeSet.getDimensionPixelSize(2131755489);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    RectF localRectF = this.mTmpRect;
    Paint localPaint = this.mTmpPaint;
    int j = getWidth();
    int k = getHeight();
    int i;
    float f;
    if ((this.mLimitLevel > 0L) && (this.mUsageLevel > this.mLimitLevel))
    {
      i = 1;
      f = j * ((float)this.mUsageLevel / (float)this.mMaxLevel);
      if (i == 0) {
        break label267;
      }
      f = Math.min(Math.max(j * ((float)this.mLimitLevel / (float)this.mMaxLevel) - this.mMarkerWidth / 2, this.mMarkerWidth), j - this.mMarkerWidth * 2);
      localRectF.set(this.mMarkerWidth + f, 0.0F, j, k);
      localPaint.setColor(this.mOverlimitColor);
      paramCanvas.drawRect(localRectF, localPaint);
    }
    for (;;)
    {
      localRectF.set(0.0F, 0.0F, f, k);
      localPaint.setColor(this.mUsageColor);
      paramCanvas.drawRect(localRectF, localPaint);
      if (!this.mNoWarning)
      {
        f = Math.min(Math.max(j * ((float)this.mWarningLevel / (float)this.mMaxLevel) - this.mMarkerWidth / 2, 0.0F), j - this.mMarkerWidth);
        localRectF.set(f, 0.0F, this.mMarkerWidth + f, k);
        localPaint.setColor(this.mWarningColor);
        paramCanvas.drawRect(localRectF, localPaint);
      }
      return;
      i = 0;
      break;
      label267:
      localRectF.set(0.0F, 0.0F, j, k);
      localPaint.setColor(this.mTrackColor);
      paramCanvas.drawRect(localRectF, localPaint);
    }
  }
  
  public void setCustomColor(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mTrackColor = paramInt1;
    this.mUsageColor = paramInt2;
    this.mWarningColor = paramInt3;
  }
  
  public void setLevels(long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean)
  {
    this.mLimitLevel = Math.max(0L, paramLong1);
    this.mWarningLevel = Math.max(0L, paramLong2);
    this.mUsageLevel = Math.max(0L, paramLong3);
    this.mMaxLevel = Math.max(Math.max(Math.max(this.mLimitLevel, this.mWarningLevel), this.mUsageLevel), 1L);
    this.mNoWarning = paramBoolean;
    postInvalidate();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\DataUsageGraph.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */