package com.android.systemui;

import android.animation.ArgbEvaluator;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.Op;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings.System;
import android.util.Log;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback;
import com.android.systemui.util.Utils;

public class BatteryMeterDrawable
  extends Drawable
  implements BatteryController.BatteryStateChangeCallback
{
  public static final String TAG = BatteryMeterDrawable.class.getSimpleName();
  private static int mAnimOffset;
  private BatteryController mBatteryController;
  private final Paint mBatteryPaint;
  private int mBatteryStyle = 0;
  private final RectF mBoltFrame = new RectF();
  private final Paint mBoltPaint;
  private final Path mBoltPath = new Path();
  private final float[] mBoltPoints;
  private final RectF mButtonFrame = new RectF();
  private float mButtonHeightFraction;
  private int mChargeColor;
  private boolean mCharging;
  private Paint mCircleBackPaint;
  private Paint mCircleChargingPaint;
  private Paint mCircleFrontPaint;
  private final RectF mCircleRect = new RectF();
  private int mCircleSize;
  private final Path mClipPath = new Path();
  private final int[] mColors;
  private final Context mContext;
  private final int mCriticalLevel;
  private int mCustomBackgroundColor;
  private int mCustomColor;
  private int mDarkModeBackgroundColor;
  private int mDarkModeFillColor;
  private final RectF mFrame = new RectF();
  private final Paint mFramePaint;
  private final Handler mHandler;
  private int mHeight;
  private int mIconTint = -1;
  private boolean mInStatusBar = false;
  private final int mIntrinsicHeight;
  private final int mIntrinsicWidth;
  private final Runnable mInvalidate = new Runnable()
  {
    public void run()
    {
      BatteryMeterDrawable.this.invalidateSelf();
    }
  };
  private int mLastHeight = 0;
  private int mLastWidth = 0;
  private int mLevel = -1;
  private int mLightModeBackgroundColor;
  private int mLightModeFillColor;
  private boolean mListening;
  private boolean mLog = false;
  private float mOldDarkIntensity = 0.0F;
  private boolean mPluggedIn;
  private final RectF mPlusFrame = new RectF();
  private final Paint mPlusPaint;
  private final Path mPlusPath = new Path();
  private final float[] mPlusPoints;
  private boolean mPowerSaveEnabled;
  private final SettingObserver mSettingObserver = new SettingObserver();
  private final Path mShapePath = new Path();
  private boolean mShowPercent;
  private float mSubpixelSmoothingLeft;
  private float mSubpixelSmoothingRight;
  private final Paint mTextPaint;
  private final Path mTextPath = new Path();
  private boolean mUseCustomColor;
  private String mWarningString;
  private float mWarningTextHeight;
  private final Paint mWarningTextPaint;
  private int mWidth;
  private int resetUITimes = 0;
  
  public BatteryMeterDrawable(Context paramContext, Handler paramHandler, int paramInt)
  {
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    paramHandler = paramContext.getResources();
    TypedArray localTypedArray1 = paramHandler.obtainTypedArray(2131427371);
    TypedArray localTypedArray2 = paramHandler.obtainTypedArray(2131427372);
    int j = localTypedArray1.length();
    this.mColors = new int[j * 2];
    int i = 0;
    while (i < j)
    {
      this.mColors[(i * 2)] = localTypedArray1.getInt(i, 0);
      this.mColors[(i * 2 + 1)] = localTypedArray2.getColor(i, 0);
      i += 1;
    }
    localTypedArray1.recycle();
    localTypedArray2.recycle();
    updateShowPercent();
    this.mWarningString = paramContext.getString(2131690341);
    this.mCriticalLevel = this.mContext.getResources().getInteger(17694806);
    this.mButtonHeightFraction = paramContext.getResources().getFraction(2131886092, 1, 1);
    this.mSubpixelSmoothingLeft = paramContext.getResources().getFraction(2131886093, 1, 1);
    this.mSubpixelSmoothingRight = paramContext.getResources().getFraction(2131886094, 1, 1);
    this.mFramePaint = new Paint(1);
    this.mFramePaint.setColor(paramInt);
    this.mFramePaint.setDither(true);
    this.mFramePaint.setStrokeWidth(0.0F);
    this.mFramePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    this.mBatteryPaint = new Paint(1);
    this.mBatteryPaint.setDither(true);
    this.mBatteryPaint.setStrokeWidth(0.0F);
    this.mBatteryPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    this.mTextPaint = new Paint(1);
    this.mTextPaint.setTextAlign(Paint.Align.CENTER);
    this.mWarningTextPaint = new Paint(1);
    this.mWarningTextPaint.setColor(this.mColors[1]);
    this.mWarningTextPaint.setTextAlign(Paint.Align.CENTER);
    this.mChargeColor = paramContext.getColor(2131492991);
    this.mBoltPaint = new Paint(1);
    this.mBoltPaint.setColor(paramContext.getColor(2131492992));
    this.mBoltPoints = loadBoltPoints(paramHandler);
    this.mPlusPaint = new Paint(this.mBoltPaint);
    this.mPlusPoints = loadPlusPoints(paramHandler);
    this.mDarkModeBackgroundColor = paramContext.getColor(2131493046);
    this.mDarkModeFillColor = paramContext.getColor(2131493047);
    this.mLightModeBackgroundColor = paramContext.getColor(2131493049);
    this.mLightModeFillColor = paramContext.getColor(2131493050);
    this.mIntrinsicWidth = paramContext.getResources().getDimensionPixelSize(2131755581);
    this.mIntrinsicHeight = paramContext.getResources().getDimensionPixelSize(2131755580);
    this.mCircleBackPaint = new Paint(1);
    this.mCircleBackPaint.setColor(paramInt);
    this.mCircleBackPaint.setStrokeCap(Paint.Cap.BUTT);
    this.mCircleBackPaint.setDither(true);
    this.mCircleBackPaint.setStrokeWidth(0.0F);
    this.mCircleBackPaint.setStyle(Paint.Style.STROKE);
    this.mCircleFrontPaint = new Paint(1);
    this.mCircleFrontPaint.setStrokeCap(Paint.Cap.BUTT);
    this.mCircleFrontPaint.setDither(true);
    this.mCircleFrontPaint.setStrokeWidth(0.0F);
    this.mCircleFrontPaint.setStyle(Paint.Style.STROKE);
    this.mCircleChargingPaint = new Paint(1);
    this.mCircleChargingPaint.setStyle(Paint.Style.FILL);
  }
  
  private void drawCircle(Canvas paramCanvas, int paramInt1, int paramInt2)
  {
    initCircleSize(paramInt2, paramInt1);
    Paint localPaint = this.mCircleFrontPaint;
    if (this.mPluggedIn)
    {
      paramInt1 = this.mChargeColor;
      localPaint.setColor(paramInt1);
      localPaint = this.mCircleChargingPaint;
      if (!this.mPluggedIn) {
        break label163;
      }
    }
    label163:
    for (paramInt1 = this.mChargeColor;; paramInt1 = getColorForLevel(this.mLevel))
    {
      localPaint.setColor(paramInt1);
      paramCanvas.drawArc(this.mCircleRect, 270.0F, 360.0F, false, this.mCircleBackPaint);
      paramCanvas.drawArc(this.mCircleRect, 270.0F, 3.6F * this.mLevel, false, this.mCircleFrontPaint);
      float f = (this.mCircleRect.right - this.mCircleRect.left) / 4.0F;
      if (this.mPluggedIn) {
        paramCanvas.drawCircle(this.mCircleRect.centerX(), this.mCircleRect.centerY(), f, this.mCircleChargingPaint);
      }
      return;
      paramInt1 = getColorForLevel(this.mLevel);
      break;
    }
  }
  
  private int getBackgroundColor(float paramFloat)
  {
    if (this.mUseCustomColor) {
      return this.mCustomBackgroundColor;
    }
    return getColorForDarkIntensity(paramFloat, this.mLightModeBackgroundColor, this.mDarkModeBackgroundColor);
  }
  
  private int getColorForDarkIntensity(float paramFloat, int paramInt1, int paramInt2)
  {
    return ((Integer)ArgbEvaluator.getInstance().evaluate(paramFloat, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2))).intValue();
  }
  
  private int getColorForLevel(int paramInt)
  {
    if (this.mUseCustomColor) {
      return this.mCustomColor;
    }
    int j = 0;
    int i = 0;
    while (i < this.mColors.length)
    {
      int k = this.mColors[i];
      j = this.mColors[(i + 1)];
      if (paramInt <= k)
      {
        if (i == this.mColors.length - 2) {
          return this.mIconTint;
        }
        return j;
      }
      i += 2;
    }
    return j;
  }
  
  private int getFillColor(float paramFloat)
  {
    if (this.mUseCustomColor) {
      return this.mCustomColor;
    }
    return getColorForDarkIntensity(paramFloat, this.mLightModeFillColor, this.mDarkModeFillColor);
  }
  
  private void initCircleSize(int paramInt1, int paramInt2)
  {
    this.mCircleSize = Math.max(paramInt1, paramInt2);
    float f6 = this.mCircleSize / 6.5F;
    this.mCircleBackPaint.setStrokeWidth(f6);
    this.mCircleFrontPaint.setStrokeWidth(f6);
    float f1 = f6 / 2.0F;
    float f2 = f6 / 2.0F;
    float f3 = this.mCircleSize;
    float f4 = f6 / 2.0F;
    float f5 = this.mCircleSize;
    f6 /= 2.0F;
    this.mCircleRect.set(0.0F + f1, f2, f3 - f4 + 0.0F, f5 - f6);
  }
  
  private static float[] loadBoltPoints(Resources paramResources)
  {
    paramResources = paramResources.getIntArray(2131427373);
    int j = 0;
    int i = 0;
    int k = 0;
    while (k < paramResources.length)
    {
      j = Math.max(j, paramResources[k]);
      i = Math.max(i, paramResources[(k + 1)]);
      k += 2;
    }
    float[] arrayOfFloat = new float[paramResources.length];
    k = 0;
    while (k < paramResources.length)
    {
      arrayOfFloat[k] = (paramResources[k] / j);
      arrayOfFloat[(k + 1)] = (paramResources[(k + 1)] / i);
      k += 2;
    }
    return arrayOfFloat;
  }
  
  private static float[] loadPlusPoints(Resources paramResources)
  {
    paramResources = paramResources.getIntArray(2131427374);
    int j = 0;
    int i = 0;
    int k = 0;
    while (k < paramResources.length)
    {
      j = Math.max(j, paramResources[k]);
      i = Math.max(i, paramResources[(k + 1)]);
      k += 2;
    }
    float[] arrayOfFloat = new float[paramResources.length];
    k = 0;
    while (k < paramResources.length)
    {
      arrayOfFloat[k] = (paramResources[k] / j);
      arrayOfFloat[(k + 1)] = (paramResources[(k + 1)] / i);
      k += 2;
    }
    return arrayOfFloat;
  }
  
  private void postInvalidate()
  {
    this.mHandler.post(this.mInvalidate);
  }
  
  private void postInvalidate(int paramInt)
  {
    this.mHandler.postDelayed(this.mInvalidate, paramInt);
  }
  
  private int updateChargingAnimLevel()
  {
    int i = this.mLevel;
    if ((!this.mCharging) || (this.mBatteryStyle != 0))
    {
      mAnimOffset = 0;
      this.mHandler.removeCallbacks(this.mInvalidate);
      return i;
    }
    i += mAnimOffset;
    if (i >= 96) {
      i = 100;
    }
    for (mAnimOffset = 0;; mAnimOffset += 10)
    {
      this.mHandler.removeCallbacks(this.mInvalidate);
      this.mHandler.postDelayed(this.mInvalidate, 500L);
      return i;
    }
  }
  
  private void updateShowPercent()
  {
    boolean bool = false;
    if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "status_bar_show_battery_percent", 0, KeyguardUpdateMonitor.getCurrentUser()) != 0) {
      bool = true;
    }
    this.mShowPercent = bool;
  }
  
  public void disableShowPercent()
  {
    this.mShowPercent = false;
    postInvalidate();
  }
  
  public void draw(Canvas paramCanvas)
  {
    int j = 0;
    Object localObject = paramCanvas.getClipBounds();
    int k = ((Rect)localObject).left;
    int m = ((Rect)localObject).top;
    int i = j;
    if (this.mInStatusBar) {
      if (k == 0)
      {
        i = j;
        if (m == 0) {}
      }
      else
      {
        i = j;
        if (this.resetUITimes < 10)
        {
          Log.i(TAG, "re-draw since have padding");
          this.resetUITimes += 1;
          i = 1;
          this.mHandler.removeCallbacks(this.mInvalidate);
          postInvalidate();
        }
      }
    }
    if ((!this.mContext.getResources().getBoolean(2131558442)) || (i != 0))
    {
      i = 0;
      if (i == 0) {
        break label161;
      }
      i = updateChargingAnimLevel();
      label134:
      if ((this.mLog) && (!this.mPluggedIn)) {
        break label170;
      }
    }
    for (;;)
    {
      if (i != -1) {
        break label287;
      }
      return;
      i = 1;
      break;
      label161:
      i = this.mLevel;
      break label134;
      label170:
      if (Utils.DEBUG_ONEPLUS) {
        Log.d(TAG, "draw, " + this.mBatteryStyle + ", " + this.mPluggedIn + ", " + this.mCharging + ", " + this.mShowPercent + ", " + this.mListening + ", " + this.mLevel + ", " + this.mPowerSaveEnabled);
      }
    }
    label287:
    float f1 = i / 100.0F;
    j = this.mHeight;
    k = (int)(this.mHeight * 0.6551724F);
    m = (this.mWidth - k) / 2;
    int n = (int)(j * this.mButtonHeightFraction);
    this.mFrame.set(0.0F, 0.0F, k, j);
    this.mFrame.offset(m, 0.0F);
    label653:
    label667:
    float f2;
    float f3;
    float f4;
    float f5;
    switch (this.mBatteryStyle)
    {
    default: 
      this.mButtonFrame.set(this.mFrame.left + Math.round(k * 0.25F), this.mFrame.top, this.mFrame.right - Math.round(k * 0.25F), this.mFrame.top + n);
      localObject = this.mButtonFrame;
      ((RectF)localObject).top += this.mSubpixelSmoothingLeft;
      localObject = this.mButtonFrame;
      ((RectF)localObject).left += this.mSubpixelSmoothingLeft;
      localObject = this.mButtonFrame;
      ((RectF)localObject).right -= this.mSubpixelSmoothingRight;
      localObject = this.mFrame;
      ((RectF)localObject).top += n;
      localObject = this.mFrame;
      ((RectF)localObject).left += this.mSubpixelSmoothingLeft;
      localObject = this.mFrame;
      ((RectF)localObject).top += this.mSubpixelSmoothingLeft;
      localObject = this.mFrame;
      ((RectF)localObject).right -= this.mSubpixelSmoothingRight;
      localObject = this.mFrame;
      ((RectF)localObject).bottom -= this.mSubpixelSmoothingRight;
      localObject = this.mBatteryPaint;
      if (this.mPluggedIn)
      {
        j = this.mChargeColor;
        ((Paint)localObject).setColor(j);
        if (i < 96) {
          break label1149;
        }
        f1 = 1.0F;
        if (f1 != 1.0F) {
          break label1163;
        }
        f1 = this.mButtonFrame.top;
        this.mShapePath.reset();
        this.mShapePath.moveTo(this.mButtonFrame.left, this.mButtonFrame.top);
        this.mShapePath.lineTo(this.mButtonFrame.right, this.mButtonFrame.top);
        this.mShapePath.lineTo(this.mButtonFrame.right, this.mFrame.top);
        this.mShapePath.lineTo(this.mFrame.right, this.mFrame.top);
        this.mShapePath.lineTo(this.mFrame.right, this.mFrame.bottom);
        this.mShapePath.lineTo(this.mFrame.left, this.mFrame.bottom);
        this.mShapePath.lineTo(this.mFrame.left, this.mFrame.top);
        this.mShapePath.lineTo(this.mButtonFrame.left, this.mFrame.top);
        this.mShapePath.lineTo(this.mButtonFrame.left, this.mButtonFrame.top);
        if (!this.mPluggedIn) {
          break label1227;
        }
        f2 = this.mFrame.left + this.mFrame.width() / 4.0F;
        f3 = this.mFrame.top + this.mFrame.height() / 6.0F;
        f4 = this.mFrame.right - this.mFrame.width() / 4.0F;
        f5 = this.mFrame.bottom - this.mFrame.height() / 10.0F;
        if ((this.mBoltFrame.left == f2) && (this.mBoltFrame.top == f3)) {
          break label1186;
        }
      }
      break;
    }
    label1149:
    label1163:
    label1186:
    while ((this.mBoltFrame.right != f4) || (this.mBoltFrame.bottom != f5))
    {
      this.mBoltFrame.set(f2, f3, f4, f5);
      this.mBoltPath.reset();
      this.mBoltPath.moveTo(this.mBoltFrame.left + this.mBoltPoints[0] * this.mBoltFrame.width(), this.mBoltFrame.top + this.mBoltPoints[1] * this.mBoltFrame.height());
      j = 2;
      while (j < this.mBoltPoints.length)
      {
        this.mBoltPath.lineTo(this.mBoltFrame.left + this.mBoltPoints[j] * this.mBoltFrame.width(), this.mBoltFrame.top + this.mBoltPoints[(j + 1)] * this.mBoltFrame.height());
        j += 2;
      }
      drawCircle(paramCanvas, k, j);
      return;
      return;
      j = getColorForLevel(i);
      break;
      if (i > this.mCriticalLevel) {
        break label653;
      }
      f1 = 0.0F;
      break label653;
      f1 = this.mFrame.top + this.mFrame.height() * (1.0F - f1);
      break label667;
    }
    this.mShapePath.op(this.mBoltPath, Path.Op.DIFFERENCE);
    label1227:
    paramCanvas.drawPath(this.mShapePath, this.mFramePaint);
    this.mFrame.top = f1;
    this.mClipPath.reset();
    this.mClipPath.addRect(this.mFrame, Path.Direction.CCW);
    this.mShapePath.op(this.mClipPath, Path.Op.INTERSECT);
    paramCanvas.drawPath(this.mShapePath, this.mBatteryPaint);
    if ((this.mPluggedIn) || (this.mPowerSaveEnabled)) {}
    do
    {
      return;
      this.mBoltPath.lineTo(this.mBoltFrame.left + this.mBoltPoints[0] * this.mBoltFrame.width(), this.mBoltFrame.top + this.mBoltPoints[1] * this.mBoltFrame.height());
      break;
      if (i <= this.mCriticalLevel)
      {
        f1 = this.mWidth;
        f2 = this.mHeight;
        f3 = this.mWarningTextHeight;
        paramCanvas.drawText(this.mWarningString, f1 * 0.5F, (f2 + f3) * 0.48F, this.mWarningTextPaint);
        return;
      }
    } while (0 == 0);
    paramCanvas.drawText(null, 0.0F, 0.0F, this.mTextPaint);
  }
  
  public void enableLog(boolean paramBoolean)
  {
    this.mLog = paramBoolean;
  }
  
  public int getIntrinsicHeight()
  {
    return this.mIntrinsicHeight;
  }
  
  public int getIntrinsicWidth()
  {
    if (this.mBatteryStyle == 1) {
      return this.mIntrinsicHeight;
    }
    return this.mIntrinsicWidth;
  }
  
  public int getOpacity()
  {
    return 0;
  }
  
  public void onBatteryLevelChanged(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mLevel = paramInt;
    this.mPluggedIn = paramBoolean1;
    this.mCharging = paramBoolean2;
    postInvalidate();
  }
  
  public void onBatteryPercentShowChange(boolean paramBoolean) {}
  
  public void onBatteryStyleChanged(int paramInt)
  {
    this.mBatteryStyle = paramInt;
    this.resetUITimes = 0;
    postInvalidate();
  }
  
  public void onFastChargeChanged(boolean paramBoolean) {}
  
  public void onPowerSaveChanged(boolean paramBoolean)
  {
    this.mPowerSaveEnabled = paramBoolean;
    if ((this.mLog) && (Utils.DEBUG_ONEPLUS)) {
      Log.d(TAG, "onPowerSaveChanged , " + paramBoolean);
    }
    invalidateSelf();
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setBatteryController(BatteryController paramBatteryController)
  {
    this.mBatteryController = paramBatteryController;
    this.mPowerSaveEnabled = this.mBatteryController.isPowerSave();
  }
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    this.mHeight = (paramInt4 - paramInt2);
    this.mWidth = (paramInt3 - paramInt1);
    this.mWarningTextPaint.setTextSize(this.mHeight * 0.75F);
    this.mWarningTextHeight = (-this.mWarningTextPaint.getFontMetrics().ascent);
    if ((this.mLastHeight != this.mHeight) || (this.mLastWidth != this.mWidth))
    {
      this.mLastHeight = this.mHeight;
      this.mLastWidth = this.mWidth;
      postInvalidate(20);
    }
  }
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
  public void setCustomColor(int paramInt1, int paramInt2)
  {
    this.mUseCustomColor = true;
    this.mCustomColor = paramInt1;
    this.mCustomBackgroundColor = paramInt2;
    this.mFramePaint.setColor(paramInt2);
    this.mChargeColor = paramInt1;
  }
  
  public void setDarkIntensity(float paramFloat)
  {
    if (paramFloat == this.mOldDarkIntensity) {
      return;
    }
    int i = getBackgroundColor(paramFloat);
    int j = getFillColor(paramFloat);
    this.mIconTint = j;
    this.mFramePaint.setColor(i);
    this.mBoltPaint.setColor(j);
    this.mChargeColor = j;
    this.mCircleBackPaint.setColor(i);
    this.mCircleFrontPaint.setColor(j);
    this.mCircleChargingPaint.setColor(j);
    invalidateSelf();
    this.mOldDarkIntensity = paramFloat;
  }
  
  public void setIconPlace(boolean paramBoolean)
  {
    this.mInStatusBar = paramBoolean;
  }
  
  public void startListening()
  {
    this.mListening = true;
    this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("status_bar_show_battery_percent"), false, this.mSettingObserver);
    updateShowPercent();
    this.mBatteryController.addStateChangedCallback(this);
  }
  
  public void stopListening()
  {
    this.mListening = false;
    this.mContext.getContentResolver().unregisterContentObserver(this.mSettingObserver);
    this.mBatteryController.removeStateChangedCallback(this);
  }
  
  private final class SettingObserver
    extends ContentObserver
  {
    public SettingObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      super.onChange(paramBoolean, paramUri);
      BatteryMeterDrawable.-wrap1(BatteryMeterDrawable.this);
      BatteryMeterDrawable.-wrap0(BatteryMeterDrawable.this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\BatteryMeterDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */