package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v17.leanback.R.styleable;
import android.util.AttributeSet;
import android.view.View;

public class HorizontalGridView
  extends BaseGridView
{
  private boolean mFadingHighEdge;
  private boolean mFadingLowEdge;
  private LinearGradient mHighFadeShader;
  private int mHighFadeShaderLength;
  private int mHighFadeShaderOffset;
  private LinearGradient mLowFadeShader;
  private int mLowFadeShaderLength;
  private int mLowFadeShaderOffset;
  private Bitmap mTempBitmapHigh;
  private Bitmap mTempBitmapLow;
  private Paint mTempPaint = new Paint();
  private Rect mTempRect = new Rect();
  
  public HorizontalGridView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public HorizontalGridView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public HorizontalGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mLayoutManager.setOrientation(0);
    initAttributes(paramContext, paramAttributeSet);
  }
  
  private Bitmap getTempBitmapHigh()
  {
    if ((this.mTempBitmapHigh == null) || (this.mTempBitmapHigh.getWidth() != this.mHighFadeShaderLength)) {}
    for (;;)
    {
      this.mTempBitmapHigh = Bitmap.createBitmap(this.mHighFadeShaderLength, getHeight(), Bitmap.Config.ARGB_8888);
      do
      {
        return this.mTempBitmapHigh;
      } while (this.mTempBitmapHigh.getHeight() == getHeight());
    }
  }
  
  private Bitmap getTempBitmapLow()
  {
    if ((this.mTempBitmapLow == null) || (this.mTempBitmapLow.getWidth() != this.mLowFadeShaderLength)) {}
    for (;;)
    {
      this.mTempBitmapLow = Bitmap.createBitmap(this.mLowFadeShaderLength, getHeight(), Bitmap.Config.ARGB_8888);
      do
      {
        return this.mTempBitmapLow;
      } while (this.mTempBitmapLow.getHeight() == getHeight());
    }
  }
  
  private boolean needsFadingHighEdge()
  {
    if (!this.mFadingHighEdge) {
      return false;
    }
    int i = getChildCount() - 1;
    while (i >= 0)
    {
      View localView = getChildAt(i);
      if (this.mLayoutManager.getOpticalRight(localView) > getWidth() - getPaddingRight() + this.mHighFadeShaderOffset) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  private boolean needsFadingLowEdge()
  {
    if (!this.mFadingLowEdge) {
      return false;
    }
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      View localView = getChildAt(i);
      if (this.mLayoutManager.getOpticalLeft(localView) < getPaddingLeft() - this.mLowFadeShaderOffset) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void updateLayerType()
  {
    if ((this.mFadingLowEdge) || (this.mFadingHighEdge))
    {
      setLayerType(2, null);
      setWillNotDraw(false);
      return;
    }
    setLayerType(0, null);
    setWillNotDraw(true);
  }
  
  public void draw(Canvas paramCanvas)
  {
    boolean bool1 = needsFadingLowEdge();
    boolean bool2 = needsFadingHighEdge();
    if (!bool1) {
      this.mTempBitmapLow = null;
    }
    if (!bool2) {
      this.mTempBitmapHigh = null;
    }
    int i;
    int j;
    label91:
    int n;
    int k;
    if ((bool1) || (bool2))
    {
      if (!this.mFadingLowEdge) {
        break label516;
      }
      i = getPaddingLeft() - this.mLowFadeShaderOffset - this.mLowFadeShaderLength;
      if (!this.mFadingHighEdge) {
        break label521;
      }
      j = getWidth() - getPaddingRight() + this.mHighFadeShaderOffset + this.mHighFadeShaderLength;
      n = paramCanvas.save();
      if (!this.mFadingLowEdge) {
        break label529;
      }
      k = this.mLowFadeShaderLength;
      label110:
      if (!this.mFadingHighEdge) {
        break label535;
      }
    }
    label516:
    label521:
    label529:
    label535:
    for (int m = this.mHighFadeShaderLength;; m = 0)
    {
      paramCanvas.clipRect(i + k, 0, j - m, getHeight());
      super.draw(paramCanvas);
      paramCanvas.restoreToCount(n);
      Canvas localCanvas = new Canvas();
      this.mTempRect.top = 0;
      this.mTempRect.bottom = getHeight();
      Bitmap localBitmap;
      if ((bool1) && (this.mLowFadeShaderLength > 0))
      {
        localBitmap = getTempBitmapLow();
        localBitmap.eraseColor(0);
        localCanvas.setBitmap(localBitmap);
        k = localCanvas.save();
        localCanvas.clipRect(0, 0, this.mLowFadeShaderLength, getHeight());
        localCanvas.translate(-i, 0.0F);
        super.draw(localCanvas);
        localCanvas.restoreToCount(k);
        this.mTempPaint.setShader(this.mLowFadeShader);
        localCanvas.drawRect(0.0F, 0.0F, this.mLowFadeShaderLength, getHeight(), this.mTempPaint);
        this.mTempRect.left = 0;
        this.mTempRect.right = this.mLowFadeShaderLength;
        paramCanvas.translate(i, 0.0F);
        paramCanvas.drawBitmap(localBitmap, this.mTempRect, this.mTempRect, null);
        paramCanvas.translate(-i, 0.0F);
      }
      if ((bool2) && (this.mHighFadeShaderLength > 0))
      {
        localBitmap = getTempBitmapHigh();
        localBitmap.eraseColor(0);
        localCanvas.setBitmap(localBitmap);
        i = localCanvas.save();
        localCanvas.clipRect(0, 0, this.mHighFadeShaderLength, getHeight());
        localCanvas.translate(-(j - this.mHighFadeShaderLength), 0.0F);
        super.draw(localCanvas);
        localCanvas.restoreToCount(i);
        this.mTempPaint.setShader(this.mHighFadeShader);
        localCanvas.drawRect(0.0F, 0.0F, this.mHighFadeShaderLength, getHeight(), this.mTempPaint);
        this.mTempRect.left = 0;
        this.mTempRect.right = this.mHighFadeShaderLength;
        paramCanvas.translate(j - this.mHighFadeShaderLength, 0.0F);
        paramCanvas.drawBitmap(localBitmap, this.mTempRect, this.mTempRect, null);
        paramCanvas.translate(-(j - this.mHighFadeShaderLength), 0.0F);
      }
      return;
      super.draw(paramCanvas);
      return;
      i = 0;
      break;
      j = getWidth();
      break label91;
      k = 0;
      break label110;
    }
  }
  
  protected void initAttributes(Context paramContext, AttributeSet paramAttributeSet)
  {
    initBaseGridViewAttributes(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.lbHorizontalGridView);
    setRowHeight(paramContext);
    setNumRows(paramContext.getInt(R.styleable.lbHorizontalGridView_numberOfRows, 1));
    paramContext.recycle();
    updateLayerType();
    this.mTempPaint = new Paint();
    this.mTempPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
  }
  
  public void setNumRows(int paramInt)
  {
    this.mLayoutManager.setNumRows(paramInt);
    requestLayout();
  }
  
  public void setRowHeight(int paramInt)
  {
    this.mLayoutManager.setRowHeight(paramInt);
    requestLayout();
  }
  
  void setRowHeight(TypedArray paramTypedArray)
  {
    if (paramTypedArray.peekValue(R.styleable.lbHorizontalGridView_rowHeight) != null) {
      setRowHeight(paramTypedArray.getLayoutDimension(R.styleable.lbHorizontalGridView_rowHeight, 0));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v17\leanback\widget\HorizontalGridView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */