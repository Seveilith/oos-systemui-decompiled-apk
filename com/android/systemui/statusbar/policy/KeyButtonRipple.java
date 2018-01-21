package com.android.systemui.statusbar.policy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.CanvasProperty;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.DisplayListCanvas;
import android.view.RenderNodeAnimator;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.systemui.Interpolators;
import java.util.ArrayList;
import java.util.HashSet;

public class KeyButtonRipple
  extends Drawable
{
  private final AnimatorListenerAdapter mAnimatorListener = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      KeyButtonRipple.-get1(KeyButtonRipple.this).remove(paramAnonymousAnimator);
      if ((!KeyButtonRipple.-get1(KeyButtonRipple.this).isEmpty()) || (KeyButtonRipple.-get0(KeyButtonRipple.this))) {
        return;
      }
      KeyButtonRipple.-set0(KeyButtonRipple.this, false);
      KeyButtonRipple.this.invalidateSelf();
    }
  };
  private CanvasProperty<Float> mBottomProp;
  private boolean mDrawingHardwareGlow;
  private float mGlowAlpha = 0.0F;
  private float mGlowScale = 1.0F;
  private final Interpolator mInterpolator = new LogInterpolator(null);
  private CanvasProperty<Float> mLeftProp;
  private int mMaxWidth;
  private CanvasProperty<Paint> mPaintProp;
  private boolean mPressed;
  private CanvasProperty<Float> mRightProp;
  private Paint mRipplePaint;
  private final HashSet<Animator> mRunningAnimations = new HashSet();
  private CanvasProperty<Float> mRxProp;
  private CanvasProperty<Float> mRyProp;
  private boolean mSupportHardware;
  private final View mTargetView;
  private final ArrayList<Animator> mTmpArray = new ArrayList();
  private CanvasProperty<Float> mTopProp;
  
  public KeyButtonRipple(Context paramContext, View paramView)
  {
    this.mMaxWidth = paramContext.getResources().getDimensionPixelSize(2131755536);
    this.mTargetView = paramView;
  }
  
  private void cancelAnimations()
  {
    this.mTmpArray.addAll(this.mRunningAnimations);
    int j = this.mTmpArray.size();
    int i = 0;
    while (i < j)
    {
      ((Animator)this.mTmpArray.get(i)).cancel();
      i += 1;
    }
    this.mTmpArray.clear();
    this.mRunningAnimations.clear();
  }
  
  private void drawHardware(DisplayListCanvas paramDisplayListCanvas)
  {
    if (this.mDrawingHardwareGlow) {
      paramDisplayListCanvas.drawRoundRect(this.mLeftProp, this.mTopProp, this.mRightProp, this.mBottomProp, this.mRxProp, this.mRyProp, this.mPaintProp);
    }
  }
  
  private void drawSoftware(Canvas paramCanvas)
  {
    Paint localPaint;
    float f1;
    float f2;
    int i;
    float f4;
    float f3;
    if (this.mGlowAlpha > 0.0F)
    {
      localPaint = getRipplePaint();
      localPaint.setAlpha((int)(this.mGlowAlpha * 255.0F));
      f1 = getBounds().width();
      f2 = getBounds().height();
      if (f1 <= f2) {
        break label132;
      }
      i = 1;
      f4 = getRippleSize() * this.mGlowScale * 0.5F;
      f1 *= 0.5F;
      f2 *= 0.5F;
      if (i == 0) {
        break label138;
      }
      f3 = f4;
      label89:
      if (i == 0) {
        break label144;
      }
      f4 = f2;
      label97:
      if (i == 0) {
        break label147;
      }
    }
    label132:
    label138:
    label144:
    label147:
    for (float f5 = f2;; f5 = f1)
    {
      paramCanvas.drawRoundRect(f1 - f3, f2 - f4, f1 + f3, f2 + f4, f5, f5, localPaint);
      return;
      i = 0;
      break;
      f3 = f1;
      break label89;
      break label97;
    }
  }
  
  private void enterHardware()
  {
    cancelAnimations();
    this.mDrawingHardwareGlow = true;
    setExtendStart(CanvasProperty.createFloat(getExtendSize() / 2));
    RenderNodeAnimator localRenderNodeAnimator1 = new RenderNodeAnimator(getExtendStart(), getExtendSize() / 2 - getRippleSize() * 1.35F / 2.0F);
    localRenderNodeAnimator1.setDuration(350L);
    localRenderNodeAnimator1.setInterpolator(this.mInterpolator);
    localRenderNodeAnimator1.addListener(this.mAnimatorListener);
    localRenderNodeAnimator1.setTarget(this.mTargetView);
    setExtendEnd(CanvasProperty.createFloat(getExtendSize() / 2));
    RenderNodeAnimator localRenderNodeAnimator2 = new RenderNodeAnimator(getExtendEnd(), getExtendSize() / 2 + getRippleSize() * 1.35F / 2.0F);
    localRenderNodeAnimator2.setDuration(350L);
    localRenderNodeAnimator2.setInterpolator(this.mInterpolator);
    localRenderNodeAnimator2.addListener(this.mAnimatorListener);
    localRenderNodeAnimator2.setTarget(this.mTargetView);
    if (isHorizontal())
    {
      this.mTopProp = CanvasProperty.createFloat(0.0F);
      this.mBottomProp = CanvasProperty.createFloat(getBounds().height());
      this.mRxProp = CanvasProperty.createFloat(getBounds().height() / 2);
    }
    for (this.mRyProp = CanvasProperty.createFloat(getBounds().height() / 2);; this.mRyProp = CanvasProperty.createFloat(getBounds().width() / 2))
    {
      this.mGlowScale = 1.35F;
      this.mGlowAlpha = 0.2F;
      this.mRipplePaint = getRipplePaint();
      this.mRipplePaint.setAlpha((int)(this.mGlowAlpha * 255.0F));
      this.mPaintProp = CanvasProperty.createPaint(this.mRipplePaint);
      localRenderNodeAnimator1.start();
      localRenderNodeAnimator2.start();
      this.mRunningAnimations.add(localRenderNodeAnimator1);
      this.mRunningAnimations.add(localRenderNodeAnimator2);
      invalidateSelf();
      return;
      this.mLeftProp = CanvasProperty.createFloat(0.0F);
      this.mRightProp = CanvasProperty.createFloat(getBounds().width());
      this.mRxProp = CanvasProperty.createFloat(getBounds().width() / 2);
    }
  }
  
  private void enterSoftware()
  {
    cancelAnimations();
    this.mGlowAlpha = 0.2F;
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(this, "glowScale", new float[] { 0.0F, 1.35F });
    localObjectAnimator.setInterpolator(this.mInterpolator);
    localObjectAnimator.setDuration(350L);
    localObjectAnimator.addListener(this.mAnimatorListener);
    localObjectAnimator.start();
    this.mRunningAnimations.add(localObjectAnimator);
  }
  
  private void exitHardware()
  {
    this.mPaintProp = CanvasProperty.createPaint(getRipplePaint());
    RenderNodeAnimator localRenderNodeAnimator = new RenderNodeAnimator(this.mPaintProp, 1, 0.0F);
    localRenderNodeAnimator.setDuration(450L);
    localRenderNodeAnimator.setInterpolator(Interpolators.ALPHA_OUT);
    localRenderNodeAnimator.addListener(this.mAnimatorListener);
    localRenderNodeAnimator.setTarget(this.mTargetView);
    localRenderNodeAnimator.start();
    this.mRunningAnimations.add(localRenderNodeAnimator);
    invalidateSelf();
  }
  
  private void exitSoftware()
  {
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(this, "glowAlpha", new float[] { this.mGlowAlpha, 0.0F });
    localObjectAnimator.setInterpolator(Interpolators.ALPHA_OUT);
    localObjectAnimator.setDuration(450L);
    localObjectAnimator.addListener(this.mAnimatorListener);
    localObjectAnimator.start();
    this.mRunningAnimations.add(localObjectAnimator);
  }
  
  private CanvasProperty<Float> getExtendEnd()
  {
    if (isHorizontal()) {
      return this.mRightProp;
    }
    return this.mBottomProp;
  }
  
  private int getExtendSize()
  {
    if (isHorizontal()) {
      return getBounds().width();
    }
    return getBounds().height();
  }
  
  private CanvasProperty<Float> getExtendStart()
  {
    if (isHorizontal()) {
      return this.mLeftProp;
    }
    return this.mTopProp;
  }
  
  private Paint getRipplePaint()
  {
    if (this.mRipplePaint == null)
    {
      this.mRipplePaint = new Paint();
      this.mRipplePaint.setAntiAlias(true);
      this.mRipplePaint.setColor(-1);
    }
    return this.mRipplePaint;
  }
  
  private int getRippleSize()
  {
    if (isHorizontal()) {}
    for (int i = getBounds().width();; i = getBounds().height()) {
      return Math.min(i, this.mMaxWidth);
    }
  }
  
  private boolean isHorizontal()
  {
    return getBounds().width() > getBounds().height();
  }
  
  private void setExtendEnd(CanvasProperty<Float> paramCanvasProperty)
  {
    if (isHorizontal())
    {
      this.mRightProp = paramCanvasProperty;
      return;
    }
    this.mBottomProp = paramCanvasProperty;
  }
  
  private void setExtendStart(CanvasProperty<Float> paramCanvasProperty)
  {
    if (isHorizontal())
    {
      this.mLeftProp = paramCanvasProperty;
      return;
    }
    this.mTopProp = paramCanvasProperty;
  }
  
  private void setPressedHardware(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      enterHardware();
      return;
    }
    exitHardware();
  }
  
  private void setPressedSoftware(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      enterSoftware();
      return;
    }
    exitSoftware();
  }
  
  public void draw(Canvas paramCanvas)
  {
    this.mSupportHardware = paramCanvas.isHardwareAccelerated();
    if (this.mSupportHardware)
    {
      drawHardware((DisplayListCanvas)paramCanvas);
      return;
    }
    drawSoftware(paramCanvas);
  }
  
  public float getGlowAlpha()
  {
    return this.mGlowAlpha;
  }
  
  public float getGlowScale()
  {
    return this.mGlowScale;
  }
  
  public int getOpacity()
  {
    return -3;
  }
  
  public boolean isStateful()
  {
    return true;
  }
  
  public void jumpToCurrentState()
  {
    cancelAnimations();
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    boolean bool2 = false;
    int i = 0;
    for (;;)
    {
      boolean bool1 = bool2;
      if (i < paramArrayOfInt.length)
      {
        if (paramArrayOfInt[i] == 16842919) {
          bool1 = true;
        }
      }
      else
      {
        if (bool1 == this.mPressed) {
          break;
        }
        setPressed(bool1);
        this.mPressed = bool1;
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setColor(int paramInt)
  {
    if (this.mRipplePaint != null) {
      this.mRipplePaint.setColor(paramInt);
    }
  }
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
  public void setGlowAlpha(float paramFloat)
  {
    this.mGlowAlpha = paramFloat;
    invalidateSelf();
  }
  
  public void setGlowScale(float paramFloat)
  {
    this.mGlowScale = paramFloat;
    invalidateSelf();
  }
  
  public void setPressed(boolean paramBoolean)
  {
    if (this.mSupportHardware)
    {
      setPressedHardware(paramBoolean);
      return;
    }
    setPressedSoftware(paramBoolean);
  }
  
  private static final class LogInterpolator
    implements Interpolator
  {
    public float getInterpolation(float paramFloat)
    {
      return 1.0F - (float)Math.pow(400.0D, -paramFloat * 1.4D);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\KeyButtonRipple.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */