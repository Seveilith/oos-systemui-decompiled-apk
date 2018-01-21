package com.android.systemui.statusbar.phone;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.stackdivider.WindowManagerProxy;
import com.android.systemui.util.Utils;

public class BarTransitions
{
  public static final boolean HIGH_END = ;
  private boolean mAlwaysOpaque = false;
  private final BarBackgroundDrawable mBarBackground;
  private int mMode;
  private final String mTag;
  private final View mView;
  
  public BarTransitions(View paramView, int paramInt)
  {
    this.mTag = ("BarTransitions." + paramView.getClass().getSimpleName());
    this.mView = paramView;
    this.mBarBackground = new BarBackgroundDrawable(this.mView.getContext(), paramInt, paramView);
    if (HIGH_END) {
      this.mView.setBackground(this.mBarBackground);
    }
  }
  
  public static String modeToString(int paramInt)
  {
    if (paramInt == 0) {
      return "MODE_OPAQUE";
    }
    if (paramInt == 1) {
      return "MODE_SEMI_TRANSPARENT";
    }
    if (paramInt == 2) {
      return "MODE_TRANSLUCENT";
    }
    if (paramInt == 3) {
      return "MODE_LIGHTS_OUT";
    }
    if (paramInt == 4) {
      return "MODE_TRANSPARENT";
    }
    if (paramInt == 5) {
      return "MODE_WARNING";
    }
    if (paramInt == 6) {
      return "MODE_LIGHTS_OUT_TRANSPARENT";
    }
    if (paramInt == 7) {
      return "MODE_HIGHLIGHT_HINT";
    }
    throw new IllegalArgumentException("Unknown mode " + paramInt);
  }
  
  protected void applyModeBackground(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.mBarBackground.applyModeBackground(paramInt1, paramInt2, paramBoolean);
  }
  
  public void finishAnimations()
  {
    this.mBarBackground.finishAnimation();
  }
  
  public int getMode()
  {
    return this.mMode;
  }
  
  public boolean isAlwaysOpaque()
  {
    if (HIGH_END) {
      return this.mAlwaysOpaque;
    }
    return true;
  }
  
  protected boolean isLightsOut(int paramInt)
  {
    return (paramInt == 3) || (paramInt == 6);
  }
  
  protected void onTransition(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (HIGH_END) {
      applyModeBackground(paramInt1, paramInt2, paramBoolean);
    }
  }
  
  public void transitionTo(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramInt1 != 7)
    {
      transitionTo(paramInt1, paramBoolean);
      return;
    }
    paramInt1 = this.mMode;
    this.mMode = 7;
    BarBackgroundDrawable.-set0(this.mBarBackground, paramInt2);
    onTransition(paramInt1, this.mMode, paramBoolean);
  }
  
  public void transitionTo(int paramInt, boolean paramBoolean)
  {
    int i = paramInt;
    if (isAlwaysOpaque()) {
      if ((paramInt != 1) && (paramInt != 2)) {
        break label49;
      }
    }
    for (;;)
    {
      i = 0;
      label49:
      do
      {
        paramInt = i;
        if (isAlwaysOpaque())
        {
          paramInt = i;
          if (i == 6) {
            paramInt = 3;
          }
        }
        if (this.mMode != paramInt) {
          break;
        }
        return;
        i = paramInt;
      } while (paramInt != 4);
    }
    i = this.mMode;
    this.mMode = paramInt;
    onTransition(i, this.mMode, paramBoolean);
  }
  
  private static class BarBackgroundDrawable
    extends Drawable
  {
    private boolean mAnimating;
    private int mColor;
    private int mColorStart;
    private Context mContext = null;
    private long mEndTime;
    private final Drawable mGradient;
    private int mGradientAlpha;
    private int mGradientAlphaStart;
    private int mHighlightColor;
    private int mMode = -1;
    private final int mOpaque;
    private Paint mPaint = new Paint();
    private final int mSemiTransparent;
    private long mStartTime;
    private PorterDuffColorFilter mTintFilter;
    private final int mTransparent;
    private final int mWarning;
    
    public BarBackgroundDrawable(Context paramContext, int paramInt, View paramView)
    {
      this.mContext = paramContext;
      paramContext.getResources();
      if ((Utils.isSupportHideNavBar()) && (paramView.getClass().getSimpleName().equals("NavigationBarView"))) {
        this.mOpaque = paramContext.getColor(2131493129);
      }
      for (this.mSemiTransparent = paramContext.getColor(2131493131);; this.mSemiTransparent = paramContext.getColor(17170548))
      {
        this.mTransparent = paramContext.getColor(2131492987);
        this.mWarning = paramContext.getColor(17170524);
        this.mGradient = paramContext.getDrawable(paramInt);
        return;
        this.mOpaque = paramContext.getColor(2131492986);
      }
    }
    
    public void applyModeBackground(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this.mMode = paramInt2;
      this.mAnimating = paramBoolean;
      if (paramBoolean)
      {
        long l = SystemClock.elapsedRealtime();
        this.mStartTime = l;
        this.mEndTime = (200L + l);
        this.mGradientAlphaStart = this.mGradientAlpha;
        this.mColorStart = this.mColor;
      }
      invalidateSelf();
    }
    
    public void draw(Canvas paramCanvas)
    {
      int i;
      if (this.mMode == 5)
      {
        i = this.mWarning;
        if (this.mAnimating) {
          break label233;
        }
        this.mColor = i;
        this.mGradientAlpha = 0;
      }
      for (;;)
      {
        if (this.mGradientAlpha > 0)
        {
          this.mGradient.setAlpha(this.mGradientAlpha);
          this.mGradient.draw(paramCanvas);
        }
        if (Color.alpha(this.mColor) > 0)
        {
          this.mPaint.setColor(this.mColor);
          if (this.mTintFilter != null) {
            this.mPaint.setColorFilter(this.mTintFilter);
          }
          paramCanvas.drawPaint(this.mPaint);
        }
        if (this.mAnimating) {
          invalidateSelf();
        }
        return;
        if (this.mMode == 2)
        {
          i = this.mSemiTransparent;
          break;
        }
        if (this.mMode == 1)
        {
          i = this.mSemiTransparent;
          break;
        }
        if ((this.mMode == 4) || (this.mMode == 6))
        {
          i = this.mTransparent;
          break;
        }
        if (this.mMode == 7)
        {
          i = this.mHighlightColor;
          break;
        }
        if ((Utils.isSupportHideNavBar()) && (this.mContext != null) && (Utils.isScreenCompat()) && (WindowManagerProxy.getInstance().getDockSide() == -1))
        {
          i = -16777216;
          break;
        }
        i = this.mOpaque;
        break;
        label233:
        long l = SystemClock.elapsedRealtime();
        if (l >= this.mEndTime)
        {
          this.mAnimating = false;
          this.mColor = i;
          this.mGradientAlpha = 0;
        }
        else
        {
          float f = (float)(l - this.mStartTime) / (float)(this.mEndTime - this.mStartTime);
          f = Math.max(0.0F, Math.min(Interpolators.LINEAR.getInterpolation(f), 1.0F));
          this.mGradientAlpha = ((int)(f * 0.0F + this.mGradientAlphaStart * (1.0F - f)));
          if ((Utils.isSupportHideNavBar()) && (i == this.mTransparent)) {
            this.mColor = Color.argb((int)(Color.alpha(i) * f + Color.alpha(this.mColorStart) * (1.0F - f)), Color.red(this.mColorStart), Color.green(this.mColorStart), Color.blue(this.mColorStart));
          } else {
            this.mColor = Color.argb((int)(Color.alpha(i) * f + Color.alpha(this.mColorStart) * (1.0F - f)), (int)(Color.red(i) * f + Color.red(this.mColorStart) * (1.0F - f)), (int)(Color.green(i) * f + Color.green(this.mColorStart) * (1.0F - f)), (int)(Color.blue(i) * f + Color.blue(this.mColorStart) * (1.0F - f)));
          }
        }
      }
    }
    
    public void finishAnimation()
    {
      if (this.mAnimating)
      {
        this.mAnimating = false;
        invalidateSelf();
      }
    }
    
    public int getOpacity()
    {
      return -3;
    }
    
    protected void onBoundsChange(Rect paramRect)
    {
      super.onBoundsChange(paramRect);
      this.mGradient.setBounds(paramRect);
    }
    
    public void setAlpha(int paramInt) {}
    
    public void setColorFilter(ColorFilter paramColorFilter) {}
    
    public void setTint(int paramInt)
    {
      if (this.mTintFilter == null) {
        this.mTintFilter = new PorterDuffColorFilter(paramInt, PorterDuff.Mode.SRC_IN);
      }
      for (;;)
      {
        invalidateSelf();
        return;
        this.mTintFilter.setColor(paramInt);
      }
    }
    
    public void setTintMode(PorterDuff.Mode paramMode)
    {
      if (this.mTintFilter == null) {
        this.mTintFilter = new PorterDuffColorFilter(0, paramMode);
      }
      for (;;)
      {
        invalidateSelf();
        return;
        this.mTintFilter.setMode(paramMode);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\BarTransitions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */