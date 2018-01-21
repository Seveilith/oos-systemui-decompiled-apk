package com.android.systemui.statusbar.notification;

import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.service.notification.StatusBarNotification;
import android.view.NotificationHeaderView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewPropertyAnimator;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.TransformableView;

public abstract class NotificationViewWrapper
  implements TransformableView
{
  protected boolean mDark;
  protected boolean mDarkInitialized = false;
  protected final ColorMatrix mGrayscaleColorMatrix = new ColorMatrix();
  protected final ExpandableNotificationRow mRow;
  protected final View mView;
  
  protected NotificationViewWrapper(View paramView, ExpandableNotificationRow paramExpandableNotificationRow)
  {
    this.mView = paramView;
    this.mRow = paramExpandableNotificationRow;
  }
  
  public static NotificationViewWrapper wrap(Context paramContext, View paramView, ExpandableNotificationRow paramExpandableNotificationRow)
  {
    if (paramView.getId() == 16909242)
    {
      if ("bigPicture".equals(paramView.getTag())) {
        return new NotificationBigPictureTemplateViewWrapper(paramContext, paramView, paramExpandableNotificationRow);
      }
      if ("bigText".equals(paramView.getTag())) {
        return new NotificationBigTextTemplateViewWrapper(paramContext, paramView, paramExpandableNotificationRow);
      }
      if (("media".equals(paramView.getTag())) || ("bigMediaNarrow".equals(paramView.getTag()))) {
        return new NotificationMediaTemplateViewWrapper(paramContext, paramView, paramExpandableNotificationRow);
      }
      if ("messaging".equals(paramView.getTag())) {
        return new NotificationMessagingTemplateViewWrapper(paramContext, paramView, paramExpandableNotificationRow);
      }
      return new NotificationTemplateViewWrapper(paramContext, paramView, paramExpandableNotificationRow);
    }
    if ((paramView instanceof NotificationHeaderView)) {
      return new NotificationHeaderViewWrapper(paramContext, paramView, paramExpandableNotificationRow);
    }
    return new NotificationCustomViewWrapper(paramView, paramExpandableNotificationRow);
  }
  
  public TransformState getCurrentState(int paramInt)
  {
    return null;
  }
  
  public int getCustomBackgroundColor()
  {
    return 0;
  }
  
  public NotificationHeaderView getNotificationHeader()
  {
    return null;
  }
  
  public void notifyContentUpdated(StatusBarNotification paramStatusBarNotification)
  {
    this.mDarkInitialized = false;
  }
  
  public void setContentHeight(int paramInt1, int paramInt2) {}
  
  public void setDark(boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    this.mDark = paramBoolean1;
    this.mDarkInitialized = true;
  }
  
  public void setShowingLegacyBackground(boolean paramBoolean) {}
  
  public void setVisible(boolean paramBoolean)
  {
    this.mView.animate().cancel();
    View localView = this.mView;
    if (paramBoolean) {}
    for (int i = 0;; i = 4)
    {
      localView.setVisibility(i);
      return;
    }
  }
  
  protected void startIntensityAnimation(ValueAnimator.AnimatorUpdateListener paramAnimatorUpdateListener, boolean paramBoolean, long paramLong, Animator.AnimatorListener paramAnimatorListener)
  {
    float f1;
    if (paramBoolean)
    {
      f1 = 0.0F;
      if (!paramBoolean) {
        break label85;
      }
    }
    label85:
    for (float f2 = 1.0F;; f2 = 0.0F)
    {
      ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { f1, f2 });
      localValueAnimator.addUpdateListener(paramAnimatorUpdateListener);
      localValueAnimator.setDuration(700L);
      localValueAnimator.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
      localValueAnimator.setStartDelay(paramLong);
      if (paramAnimatorListener != null) {
        localValueAnimator.addListener(paramAnimatorListener);
      }
      localValueAnimator.start();
      return;
      f1 = 1.0F;
      break;
    }
  }
  
  public void transformFrom(TransformableView paramTransformableView)
  {
    CrossFadeHelper.fadeIn(this.mView);
  }
  
  public void transformFrom(TransformableView paramTransformableView, float paramFloat)
  {
    CrossFadeHelper.fadeIn(this.mView, paramFloat);
  }
  
  public void transformTo(TransformableView paramTransformableView, float paramFloat)
  {
    CrossFadeHelper.fadeOut(this.mView, paramFloat);
  }
  
  public void transformTo(TransformableView paramTransformableView, Runnable paramRunnable)
  {
    CrossFadeHelper.fadeOut(this.mView, paramRunnable);
  }
  
  public void updateExpandability(boolean paramBoolean, View.OnClickListener paramOnClickListener) {}
  
  protected void updateGrayscaleMatrix(float paramFloat)
  {
    this.mGrayscaleColorMatrix.setSaturation(1.0F - paramFloat);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\notification\NotificationViewWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */