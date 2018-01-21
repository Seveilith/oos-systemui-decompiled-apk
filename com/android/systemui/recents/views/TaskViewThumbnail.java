package com.android.systemui.recents.views;

import android.app.ActivityManager.TaskThumbnailInfo;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import com.android.systemui.recents.model.Task;

public class TaskViewThumbnail
  extends View
{
  private static final ColorMatrix TMP_BRIGHTNESS_COLOR_MATRIX = new ColorMatrix();
  private static final ColorMatrix TMP_FILTER_COLOR_MATRIX = new ColorMatrix();
  private Paint mBgFillPaint = new Paint();
  private BitmapShader mBitmapShader;
  private int mCornerRadius;
  @ViewDebug.ExportedProperty(category="recents")
  private float mDimAlpha;
  @ViewDebug.ExportedProperty(category="recents")
  private boolean mDisabledInSafeMode;
  private int mDisplayOrientation = 0;
  private Rect mDisplayRect = new Rect();
  private Paint mDrawPaint = new Paint();
  private float mFullscreenThumbnailScale;
  @ViewDebug.ExportedProperty(category="recents")
  private boolean mInvisible;
  private LightingColorFilter mLightingColorFilter = new LightingColorFilter(-1, 0);
  private Matrix mScaleMatrix = new Matrix();
  private Task mTask;
  private View mTaskBar;
  @ViewDebug.ExportedProperty(category="recents")
  private Rect mTaskViewRect = new Rect();
  private ActivityManager.TaskThumbnailInfo mThumbnailInfo;
  @ViewDebug.ExportedProperty(category="recents")
  private Rect mThumbnailRect = new Rect();
  @ViewDebug.ExportedProperty(category="recents")
  private float mThumbnailScale;
  
  public TaskViewThumbnail(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TaskViewThumbnail(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public TaskViewThumbnail(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public TaskViewThumbnail(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mDrawPaint.setColorFilter(this.mLightingColorFilter);
    this.mDrawPaint.setFilterBitmap(true);
    this.mDrawPaint.setAntiAlias(true);
    this.mCornerRadius = getResources().getDimensionPixelSize(2131755614);
    this.mBgFillPaint.setColor(-1);
    this.mFullscreenThumbnailScale = paramContext.getResources().getFraction(18022404, 1, 1);
  }
  
  void bindToTask(Task paramTask, boolean paramBoolean, int paramInt, Rect paramRect)
  {
    this.mTask = paramTask;
    this.mDisabledInSafeMode = paramBoolean;
    this.mDisplayOrientation = paramInt;
    this.mDisplayRect.set(paramRect);
    if (paramTask.colorBackground != 0) {
      this.mBgFillPaint.setColor(paramTask.colorBackground);
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.mInvisible) {
      return;
    }
    int j = this.mTaskViewRect.width();
    int k = this.mTaskViewRect.height();
    int m = Math.min(j, (int)(this.mThumbnailRect.width() * this.mThumbnailScale));
    int n = Math.min(k, (int)(this.mThumbnailRect.height() * this.mThumbnailScale));
    if ((this.mBitmapShader != null) && (m > 0) && (n > 0))
    {
      if (this.mTaskBar != null) {}
      for (int i = this.mTaskBar.getHeight() - this.mCornerRadius;; i = 0)
      {
        if (m < j) {
          paramCanvas.drawRoundRect(Math.max(0, m - this.mCornerRadius), i, j, k, this.mCornerRadius, this.mCornerRadius, this.mBgFillPaint);
        }
        if (n < k) {
          paramCanvas.drawRoundRect(0.0F, Math.max(i, n - this.mCornerRadius), j, k, this.mCornerRadius, this.mCornerRadius, this.mBgFillPaint);
        }
        paramCanvas.drawRoundRect(0.0F, i, m, n, this.mCornerRadius, this.mCornerRadius, this.mDrawPaint);
        return;
      }
    }
    paramCanvas.drawRoundRect(0.0F, 0.0F, j, k, this.mCornerRadius, this.mCornerRadius, this.mBgFillPaint);
  }
  
  void onTaskDataLoaded(ActivityManager.TaskThumbnailInfo paramTaskThumbnailInfo)
  {
    if (this.mTask.thumbnail != null)
    {
      setThumbnail(this.mTask.thumbnail, paramTaskThumbnailInfo);
      return;
    }
    setThumbnail(null, null);
  }
  
  public void onTaskViewSizeChanged(int paramInt1, int paramInt2)
  {
    if ((this.mTaskViewRect.width() == paramInt1) && (this.mTaskViewRect.height() == paramInt2)) {
      return;
    }
    this.mTaskViewRect.set(0, 0, paramInt1, paramInt2);
    setLeftTopRightBottom(0, 0, paramInt1, paramInt2);
    updateThumbnailScale();
  }
  
  public void setDimAlpha(float paramFloat)
  {
    this.mDimAlpha = paramFloat;
    updateThumbnailPaintFilter();
  }
  
  void setThumbnail(Bitmap paramBitmap, ActivityManager.TaskThumbnailInfo paramTaskThumbnailInfo)
  {
    if (paramBitmap != null)
    {
      paramBitmap.prepareToDraw();
      this.mBitmapShader = new BitmapShader(paramBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      this.mDrawPaint.setShader(this.mBitmapShader);
      this.mThumbnailRect.set(0, 0, paramBitmap.getWidth(), paramBitmap.getHeight());
      this.mThumbnailInfo = paramTaskThumbnailInfo;
      updateThumbnailScale();
      return;
    }
    this.mBitmapShader = null;
    this.mDrawPaint.setShader(null);
    this.mThumbnailRect.setEmpty();
    this.mThumbnailInfo = null;
  }
  
  void unbindFromTask()
  {
    this.mTask = null;
    setThumbnail(null, null);
  }
  
  void updateClipToTaskBar(View paramView)
  {
    this.mTaskBar = paramView;
    invalidate();
  }
  
  void updateThumbnailPaintFilter()
  {
    if (this.mInvisible) {
      return;
    }
    int i = (int)((1.0F - this.mDimAlpha) * 255.0F);
    if (this.mBitmapShader != null) {
      if (this.mDisabledInSafeMode)
      {
        TMP_FILTER_COLOR_MATRIX.setSaturation(0.0F);
        float f = 1.0F - this.mDimAlpha;
        Object localObject = TMP_BRIGHTNESS_COLOR_MATRIX.getArray();
        localObject[0] = f;
        localObject[6] = f;
        localObject[12] = f;
        localObject[4] = (this.mDimAlpha * 255.0F);
        localObject[9] = (this.mDimAlpha * 255.0F);
        localObject[14] = (this.mDimAlpha * 255.0F);
        TMP_FILTER_COLOR_MATRIX.preConcat(TMP_BRIGHTNESS_COLOR_MATRIX);
        localObject = new ColorMatrixColorFilter(TMP_FILTER_COLOR_MATRIX);
        this.mDrawPaint.setColorFilter((ColorFilter)localObject);
        this.mBgFillPaint.setColorFilter((ColorFilter)localObject);
      }
    }
    for (;;)
    {
      if (!this.mInvisible) {
        invalidate();
      }
      return;
      this.mLightingColorFilter.setColorMultiply(Color.argb(255, i, i, i));
      this.mDrawPaint.setColorFilter(this.mLightingColorFilter);
      this.mDrawPaint.setColor(-1);
      this.mBgFillPaint.setColorFilter(this.mLightingColorFilter);
      continue;
      this.mDrawPaint.setColorFilter(null);
      this.mDrawPaint.setColor(Color.argb(255, i, i, i));
    }
  }
  
  public void updateThumbnailScale()
  {
    this.mThumbnailScale = 1.0F;
    int i;
    if (this.mBitmapShader != null)
    {
      if ((this.mTask.isFreeformTask()) && (this.mTask.bounds != null)) {
        break label94;
      }
      i = 1;
      if ((!this.mTaskViewRect.isEmpty()) && (this.mThumbnailInfo != null)) {
        break label99;
      }
      label51:
      this.mThumbnailScale = 0.0F;
    }
    for (;;)
    {
      this.mScaleMatrix.setScale(this.mThumbnailScale, this.mThumbnailScale);
      this.mBitmapShader.setLocalMatrix(this.mScaleMatrix);
      if (!this.mInvisible) {
        invalidate();
      }
      return;
      label94:
      i = 0;
      break;
      label99:
      if ((this.mThumbnailInfo.taskWidth == 0) || (this.mThumbnailInfo.taskHeight == 0)) {
        break label51;
      }
      if (i != 0)
      {
        float f = 1.0F / this.mFullscreenThumbnailScale;
        if (this.mDisplayOrientation == 1)
        {
          if (this.mThumbnailInfo.screenOrientation == 1) {
            this.mThumbnailScale = (this.mTaskViewRect.width() / this.mThumbnailRect.width());
          } else {
            this.mThumbnailScale = (this.mTaskViewRect.width() / this.mDisplayRect.width() * f);
          }
        }
        else {
          this.mThumbnailScale = f;
        }
      }
      else
      {
        this.mThumbnailScale = Math.min(this.mTaskViewRect.width() / this.mThumbnailRect.width(), this.mTaskViewRect.height() / this.mThumbnailRect.height());
      }
    }
  }
  
  void updateThumbnailVisibility(int paramInt)
  {
    if ((this.mTaskBar != null) && (getHeight() - paramInt <= this.mTaskBar.getHeight())) {}
    for (boolean bool = true;; bool = false)
    {
      if (bool != this.mInvisible)
      {
        this.mInvisible = bool;
        if (!this.mInvisible) {
          updateThumbnailPaintFilter();
        }
      }
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\TaskViewThumbnail.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */