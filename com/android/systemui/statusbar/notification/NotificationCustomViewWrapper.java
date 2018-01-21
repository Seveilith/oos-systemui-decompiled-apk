package com.android.systemui.statusbar.notification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.service.notification.StatusBarNotification;
import android.support.v4.graphics.ColorUtils;
import android.view.View;
import com.android.systemui.ViewInvertHelper;
import com.android.systemui.statusbar.ExpandableNotificationRow;

public class NotificationCustomViewWrapper
  extends NotificationViewWrapper
{
  private int mBackgroundColor = 0;
  private final Paint mGreyPaint = new Paint();
  private final ViewInvertHelper mInvertHelper;
  private boolean mShouldInvertDark;
  private boolean mShowingLegacyBackground;
  
  protected NotificationCustomViewWrapper(View paramView, ExpandableNotificationRow paramExpandableNotificationRow)
  {
    super(paramView, paramExpandableNotificationRow);
    this.mInvertHelper = new ViewInvertHelper(paramView, 700L);
  }
  
  private boolean isColorLight(int paramInt)
  {
    return (Color.alpha(paramInt) == 0) || (ColorUtils.calculateLuminance(paramInt) > 0.5D);
  }
  
  protected void fadeGrayscale(final boolean paramBoolean, long paramLong)
  {
    startIntensityAnimation(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        NotificationCustomViewWrapper.this.updateGrayscaleMatrix(((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
        NotificationCustomViewWrapper.-get0(NotificationCustomViewWrapper.this).setColorFilter(new ColorMatrixColorFilter(NotificationCustomViewWrapper.this.mGrayscaleColorMatrix));
        NotificationCustomViewWrapper.this.mView.setLayerPaint(NotificationCustomViewWrapper.-get0(NotificationCustomViewWrapper.this));
      }
    }, paramBoolean, paramLong, new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if (!paramBoolean) {
          NotificationCustomViewWrapper.this.mView.setLayerType(0, null);
        }
      }
    });
  }
  
  public int getCustomBackgroundColor()
  {
    if (this.mRow.isSummaryWithChildren()) {
      return 0;
    }
    return this.mBackgroundColor;
  }
  
  public void notifyContentUpdated(StatusBarNotification paramStatusBarNotification)
  {
    super.notifyContentUpdated(paramStatusBarNotification);
    paramStatusBarNotification = this.mView.getBackground();
    this.mBackgroundColor = 0;
    if ((paramStatusBarNotification instanceof ColorDrawable))
    {
      this.mBackgroundColor = ((ColorDrawable)paramStatusBarNotification).getColor();
      this.mView.setBackground(null);
      this.mView.setTag(2131951676, Integer.valueOf(this.mBackgroundColor));
      if (this.mBackgroundColor == 0) {
        break label116;
      }
    }
    label116:
    for (boolean bool = isColorLight(this.mBackgroundColor);; bool = true)
    {
      this.mShouldInvertDark = bool;
      return;
      if (this.mView.getTag(2131951676) == null) {
        break;
      }
      this.mBackgroundColor = ((Integer)this.mView.getTag(2131951676)).intValue();
      break;
    }
  }
  
  public void setDark(boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    if ((paramBoolean1 == this.mDark) && (this.mDarkInitialized)) {
      return;
    }
    super.setDark(paramBoolean1, paramBoolean2, paramLong);
    if ((!this.mShowingLegacyBackground) && (this.mShouldInvertDark))
    {
      if (paramBoolean2)
      {
        this.mInvertHelper.fade(paramBoolean1, paramLong);
        return;
      }
      this.mInvertHelper.update(paramBoolean1);
      return;
    }
    View localView = this.mView;
    if (paramBoolean1) {}
    for (int i = 2;; i = 0)
    {
      localView.setLayerType(i, null);
      if (!paramBoolean2) {
        break;
      }
      fadeGrayscale(paramBoolean1, paramLong);
      return;
    }
    updateGrayscale(paramBoolean1);
  }
  
  public void setShowingLegacyBackground(boolean paramBoolean)
  {
    super.setShowingLegacyBackground(paramBoolean);
    this.mShowingLegacyBackground = paramBoolean;
  }
  
  public void setVisible(boolean paramBoolean)
  {
    super.setVisible(paramBoolean);
    View localView = this.mView;
    if (paramBoolean) {}
    for (float f = 1.0F;; f = 0.0F)
    {
      localView.setAlpha(f);
      return;
    }
  }
  
  protected void updateGrayscale(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      updateGrayscaleMatrix(1.0F);
      this.mGreyPaint.setColorFilter(new ColorMatrixColorFilter(this.mGrayscaleColorMatrix));
      this.mView.setLayerPaint(this.mGreyPaint);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\notification\NotificationCustomViewWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */