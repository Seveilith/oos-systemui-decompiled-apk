package com.android.systemui.statusbar.phone;

import android.content.res.Resources;
import android.graphics.Path;
import android.util.DisplayMetrics;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.PathInterpolator;

public class KeyguardClockPositionAlgorithm
{
  private static final PathInterpolator sSlowDownInterpolator;
  private AccelerateInterpolator mAccelerateInterpolator = new AccelerateInterpolator();
  private int mClockNotificationsMarginMax;
  private int mClockNotificationsMarginMin;
  private float mClockYFractionMax;
  private float mClockYFractionMin;
  private float mDensity;
  private float mEmptyDragAmount;
  private float mExpandedHeight;
  private int mHeight;
  private int mKeyguardStatusHeight;
  private int mMaxKeyguardNotifications;
  private int mMaxPanelHeight;
  private float mMoreCardNotificationAmount;
  private int mNotificationCount;
  
  static
  {
    Path localPath = new Path();
    localPath.moveTo(0.0F, 0.0F);
    localPath.cubicTo(0.3F, 0.875F, 0.6F, 1.0F, 1.0F, 1.0F);
    sSlowDownInterpolator = new PathInterpolator(localPath);
  }
  
  private float getClockAlpha(float paramFloat)
  {
    if (getNotificationAmountT() == 0.0F) {}
    for (float f = 0.5F;; f = 0.75F) {
      return Math.max(0.0F, Math.min(1.0F, (paramFloat - f) / (0.95F - f)));
    }
  }
  
  private int getClockNotificationsPadding()
  {
    float f = Math.min(getNotificationAmountT(), 1.0F);
    return (int)(this.mClockNotificationsMarginMin * f + (1.0F - f) * this.mClockNotificationsMarginMax);
  }
  
  private float getClockScale(int paramInt1, int paramInt2, int paramInt3)
  {
    if (getNotificationAmountT() == 0.0F) {}
    for (float f = 6.0F;; f = 5.0F)
    {
      f = paramInt2 - this.mKeyguardStatusHeight * f;
      f = Math.max(0.0F, Math.min((paramInt1 - f) / (paramInt3 - f), 1.0F));
      return (float)(this.mAccelerateInterpolator.getInterpolation(f) * Math.pow(this.mEmptyDragAmount / this.mDensity / 300.0F + 1.0F, 0.30000001192092896D));
    }
  }
  
  private int getClockY()
  {
    return (int)(getClockYFraction() * this.mHeight);
  }
  
  private float getClockYExpansionAdjustment()
  {
    float f1 = getClockYExpansionRubberbandFactor() * (this.mMaxPanelHeight - this.mExpandedHeight);
    float f2 = f1 / this.mMaxPanelHeight;
    f2 = -sSlowDownInterpolator.getInterpolation(f2) * 0.4F * this.mMaxPanelHeight;
    if (this.mNotificationCount == 0) {
      return (-2.0F * f1 + f2) / 3.0F;
    }
    return f2;
  }
  
  private float getClockYExpansionRubberbandFactor()
  {
    float f = (float)Math.pow(Math.min(getNotificationAmountT(), 1.0F), 0.30000001192092896D);
    return (1.0F - f) * 0.8F + 0.08F * f;
  }
  
  private float getClockYFraction()
  {
    float f = Math.min(getNotificationAmountT(), 1.0F);
    return (1.0F - f) * this.mClockYFractionMax + this.mClockYFractionMin * f;
  }
  
  private float getNotificationAmountT()
  {
    return this.mNotificationCount / (this.mMaxKeyguardNotifications + this.mMoreCardNotificationAmount);
  }
  
  private float getTopPaddingAdjMultiplier()
  {
    float f = Math.min(getNotificationAmountT(), 1.0F);
    return (1.0F - f) * 1.4F + 3.2F * f;
  }
  
  public float getMinStackScrollerPadding(int paramInt1, int paramInt2)
  {
    return this.mClockYFractionMin * paramInt1 + paramInt2 / 2 + this.mClockNotificationsMarginMin;
  }
  
  public void loadDimens(Resources paramResources)
  {
    this.mClockNotificationsMarginMin = paramResources.getDimensionPixelSize(2131755470);
    this.mClockNotificationsMarginMax = paramResources.getDimensionPixelSize(2131755471);
    this.mClockYFractionMin = paramResources.getFraction(2131886091, 1, 1);
    this.mClockYFractionMax = paramResources.getFraction(2131886090, 1, 1);
    this.mMoreCardNotificationAmount = (paramResources.getDimensionPixelSize(2131755374) / paramResources.getDimensionPixelSize(2131755369));
    this.mDensity = paramResources.getDisplayMetrics().density;
  }
  
  public void run(Result paramResult)
  {
    int i = getClockY() - this.mKeyguardStatusHeight / 2;
    paramResult.stackScrollerPaddingAdjustment = ((int)(getClockYExpansionAdjustment() * getTopPaddingAdjMultiplier()));
    int j = getClockNotificationsPadding();
    int k = paramResult.stackScrollerPaddingAdjustment;
    paramResult.clockY = i;
    paramResult.stackScrollerPadding = (this.mKeyguardStatusHeight + (i + (j + k)));
    paramResult.clockScale = getClockScale(paramResult.stackScrollerPadding, paramResult.clockY, getClockNotificationsPadding() + i + this.mKeyguardStatusHeight);
    paramResult.clockAlpha = getClockAlpha(paramResult.clockScale);
  }
  
  public void setup(int paramInt1, int paramInt2, float paramFloat1, int paramInt3, int paramInt4, int paramInt5, float paramFloat2)
  {
    this.mMaxKeyguardNotifications = paramInt1;
    this.mMaxPanelHeight = paramInt2;
    this.mExpandedHeight = paramFloat1;
    this.mNotificationCount = paramInt3;
    this.mHeight = paramInt4;
    this.mKeyguardStatusHeight = paramInt5;
    this.mEmptyDragAmount = paramFloat2;
  }
  
  public static class Result
  {
    public float clockAlpha;
    public float clockScale;
    public int clockY;
    public int stackScrollerPadding;
    public int stackScrollerPaddingAdjustment;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\KeyguardClockPositionAlgorithm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */