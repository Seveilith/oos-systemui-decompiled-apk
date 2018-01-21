package com.android.systemui.recents.views;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.android.systemui.recents.RecentsConfiguration;

class FakeShadowDrawable
  extends Drawable
{
  static final double COS_45 = Math.cos(Math.toRadians(45.0D));
  private boolean mAddPaddingForCorners = true;
  final RectF mCardBounds;
  float mCornerRadius;
  Paint mCornerShadowPaint;
  Path mCornerShadowPath;
  private boolean mDirty = true;
  Paint mEdgeShadowPaint;
  final float mInsetShadow;
  float mMaxShadowSize;
  private boolean mPrintedShadowClipWarning = false;
  float mRawMaxShadowSize;
  float mRawShadowSize;
  private final int mShadowEndColor;
  float mShadowSize;
  private final int mShadowStartColor;
  
  public FakeShadowDrawable(Resources paramResources, RecentsConfiguration paramRecentsConfiguration)
  {
    this.mShadowStartColor = paramResources.getColor(2131493039);
    this.mShadowEndColor = paramResources.getColor(2131493040);
    this.mInsetShadow = paramResources.getDimension(2131755537);
    setShadowSize(paramResources.getDimensionPixelSize(2131755538), paramResources.getDimensionPixelSize(2131755538));
    this.mCornerShadowPaint = new Paint(5);
    this.mCornerShadowPaint.setStyle(Paint.Style.FILL);
    this.mCornerShadowPaint.setDither(true);
    this.mCornerRadius = paramResources.getDimensionPixelSize(2131755614);
    this.mCardBounds = new RectF();
    this.mEdgeShadowPaint = new Paint(this.mCornerShadowPaint);
  }
  
  private void buildComponents(Rect paramRect)
  {
    float f = this.mMaxShadowSize * 1.5F;
    this.mCardBounds.set(paramRect.left + this.mMaxShadowSize, paramRect.top + f, paramRect.right - this.mMaxShadowSize, paramRect.bottom - f);
    buildShadowCorners();
  }
  
  private void buildShadowCorners()
  {
    Object localObject1 = new RectF(-this.mCornerRadius, -this.mCornerRadius, this.mCornerRadius, this.mCornerRadius);
    Object localObject2 = new RectF((RectF)localObject1);
    ((RectF)localObject2).inset(-this.mShadowSize, -this.mShadowSize);
    if (this.mCornerShadowPath == null) {
      this.mCornerShadowPath = new Path();
    }
    for (;;)
    {
      this.mCornerShadowPath.setFillType(Path.FillType.EVEN_ODD);
      this.mCornerShadowPath.moveTo(-this.mCornerRadius, 0.0F);
      this.mCornerShadowPath.rLineTo(-this.mShadowSize, 0.0F);
      this.mCornerShadowPath.arcTo((RectF)localObject2, 180.0F, 90.0F, false);
      this.mCornerShadowPath.arcTo((RectF)localObject1, 270.0F, -90.0F, false);
      this.mCornerShadowPath.close();
      float f1 = this.mCornerRadius / (this.mCornerRadius + this.mShadowSize);
      localObject1 = this.mCornerShadowPaint;
      float f2 = this.mCornerRadius;
      float f3 = this.mShadowSize;
      int i = this.mShadowStartColor;
      int j = this.mShadowStartColor;
      int k = this.mShadowEndColor;
      localObject2 = Shader.TileMode.CLAMP;
      ((Paint)localObject1).setShader(new RadialGradient(0.0F, 0.0F, f2 + f3, new int[] { i, j, k }, new float[] { 0.0F, f1, 1.0F }, (Shader.TileMode)localObject2));
      localObject1 = this.mEdgeShadowPaint;
      f1 = -this.mCornerRadius;
      f2 = this.mShadowSize;
      f3 = -this.mCornerRadius;
      float f4 = this.mShadowSize;
      i = this.mShadowStartColor;
      j = this.mShadowStartColor;
      k = this.mShadowEndColor;
      localObject2 = Shader.TileMode.CLAMP;
      ((Paint)localObject1).setShader(new LinearGradient(0.0F, f1 + f2, 0.0F, f3 - f4, new int[] { i, j, k }, new float[] { 0.0F, 0.5F, 1.0F }, (Shader.TileMode)localObject2));
      return;
      this.mCornerShadowPath.reset();
    }
  }
  
  static float calculateHorizontalPadding(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    if (paramBoolean) {
      return (float)(paramFloat1 + (1.0D - COS_45) * paramFloat2);
    }
    return paramFloat1;
  }
  
  static float calculateVerticalPadding(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    if (paramBoolean) {
      return (float)(1.5F * paramFloat1 + (1.0D - COS_45) * paramFloat2);
    }
    return 1.5F * paramFloat1;
  }
  
  private void drawShadow(Canvas paramCanvas)
  {
    float f1 = -this.mCornerRadius - this.mShadowSize;
    float f2 = this.mCornerRadius + this.mInsetShadow + this.mRawShadowSize / 2.0F;
    int i;
    if (this.mCardBounds.width() - 2.0F * f2 > 0.0F)
    {
      i = 1;
      if (this.mCardBounds.height() - 2.0F * f2 <= 0.0F) {
        break label412;
      }
    }
    label412:
    for (int j = 1;; j = 0)
    {
      int k = paramCanvas.save();
      paramCanvas.translate(this.mCardBounds.left + f2, this.mCardBounds.top + f2);
      paramCanvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
      if (i != 0) {
        paramCanvas.drawRect(0.0F, f1, this.mCardBounds.width() - 2.0F * f2, -this.mCornerRadius, this.mEdgeShadowPaint);
      }
      paramCanvas.restoreToCount(k);
      k = paramCanvas.save();
      paramCanvas.translate(this.mCardBounds.right - f2, this.mCardBounds.bottom - f2);
      paramCanvas.rotate(180.0F);
      paramCanvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
      if (i != 0)
      {
        float f3 = this.mCardBounds.width();
        float f4 = -this.mCornerRadius;
        paramCanvas.drawRect(0.0F, f1, f3 - 2.0F * f2, this.mShadowSize + f4, this.mEdgeShadowPaint);
      }
      paramCanvas.restoreToCount(k);
      i = paramCanvas.save();
      paramCanvas.translate(this.mCardBounds.left + f2, this.mCardBounds.bottom - f2);
      paramCanvas.rotate(270.0F);
      paramCanvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
      if (j != 0) {
        paramCanvas.drawRect(0.0F, f1, this.mCardBounds.height() - 2.0F * f2, -this.mCornerRadius, this.mEdgeShadowPaint);
      }
      paramCanvas.restoreToCount(i);
      i = paramCanvas.save();
      paramCanvas.translate(this.mCardBounds.right - f2, this.mCardBounds.top + f2);
      paramCanvas.rotate(90.0F);
      paramCanvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
      if (j != 0) {
        paramCanvas.drawRect(0.0F, f1, this.mCardBounds.height() - 2.0F * f2, -this.mCornerRadius, this.mEdgeShadowPaint);
      }
      paramCanvas.restoreToCount(i);
      return;
      i = 0;
      break;
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.mDirty)
    {
      buildComponents(getBounds());
      this.mDirty = false;
    }
    paramCanvas.translate(0.0F, this.mRawShadowSize / 4.0F);
    drawShadow(paramCanvas);
    paramCanvas.translate(0.0F, -this.mRawShadowSize / 4.0F);
  }
  
  public int getOpacity()
  {
    return -1;
  }
  
  public boolean getPadding(Rect paramRect)
  {
    int i = (int)Math.ceil(calculateVerticalPadding(this.mRawMaxShadowSize, this.mCornerRadius, this.mAddPaddingForCorners));
    int j = (int)Math.ceil(calculateHorizontalPadding(this.mRawMaxShadowSize, this.mCornerRadius, this.mAddPaddingForCorners));
    paramRect.set(j, i, j, i);
    return true;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    super.onBoundsChange(paramRect);
    this.mDirty = true;
  }
  
  public void setAlpha(int paramInt)
  {
    this.mCornerShadowPaint.setAlpha(paramInt);
    this.mEdgeShadowPaint.setAlpha(paramInt);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.mCornerShadowPaint.setColorFilter(paramColorFilter);
    this.mEdgeShadowPaint.setColorFilter(paramColorFilter);
  }
  
  void setShadowSize(float paramFloat1, float paramFloat2)
  {
    if ((paramFloat1 < 0.0F) || (paramFloat2 < 0.0F)) {
      throw new IllegalArgumentException("invalid shadow size");
    }
    float f = paramFloat1;
    if (paramFloat1 > paramFloat2)
    {
      paramFloat1 = paramFloat2;
      f = paramFloat1;
      if (!this.mPrintedShadowClipWarning)
      {
        Log.w("CardView", "Shadow size is being clipped by the max shadow size. See {CardView#setMaxCardElevation}.");
        this.mPrintedShadowClipWarning = true;
        f = paramFloat1;
      }
    }
    if ((this.mRawShadowSize == f) && (this.mRawMaxShadowSize == paramFloat2)) {
      return;
    }
    this.mRawShadowSize = f;
    this.mRawMaxShadowSize = paramFloat2;
    this.mShadowSize = (1.5F * f + this.mInsetShadow);
    this.mMaxShadowSize = (this.mInsetShadow + paramFloat2);
    this.mDirty = true;
    invalidateSelf();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\FakeShadowDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */