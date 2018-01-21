package com.android.systemui.statusbar.notification;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.service.notification.StatusBarNotification;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.systemui.ViewInvertHelper;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.statusbar.ViewTransformationHelper;
import com.android.systemui.statusbar.ViewTransformationHelper.CustomTransformation;

public class NotificationTemplateViewWrapper
  extends NotificationHeaderViewWrapper
{
  private View mActionsContainer;
  private int mContentHeight;
  private int mMinHeightHint;
  protected ImageView mPicture;
  private ProgressBar mProgressBar;
  private TextView mText;
  private TextView mTitle;
  
  protected NotificationTemplateViewWrapper(Context paramContext, View paramView, ExpandableNotificationRow paramExpandableNotificationRow)
  {
    super(paramContext, paramView, paramExpandableNotificationRow);
    this.mTransformationHelper.setCustomTransformation(new ViewTransformationHelper.CustomTransformation()
    {
      private float getTransformationY(TransformState paramAnonymousTransformState1, TransformState paramAnonymousTransformState2)
      {
        int[] arrayOfInt = paramAnonymousTransformState2.getLaidOutLocationOnScreen();
        paramAnonymousTransformState1 = paramAnonymousTransformState1.getLaidOutLocationOnScreen();
        return (arrayOfInt[1] + paramAnonymousTransformState2.getTransformedView().getHeight() - paramAnonymousTransformState1[1]) * 0.33F;
      }
      
      public boolean customTransformTarget(TransformState paramAnonymousTransformState1, TransformState paramAnonymousTransformState2)
      {
        paramAnonymousTransformState1.setTransformationEndY(getTransformationY(paramAnonymousTransformState1, paramAnonymousTransformState2));
        return true;
      }
      
      public boolean initTransformation(TransformState paramAnonymousTransformState1, TransformState paramAnonymousTransformState2)
      {
        paramAnonymousTransformState1.setTransformationStartY(getTransformationY(paramAnonymousTransformState1, paramAnonymousTransformState2));
        return true;
      }
      
      public boolean transformFrom(TransformState paramAnonymousTransformState, TransformableView paramAnonymousTransformableView, float paramAnonymousFloat)
      {
        if (!(paramAnonymousTransformableView instanceof HybridNotificationView)) {
          return false;
        }
        paramAnonymousTransformableView = paramAnonymousTransformableView.getCurrentState(1);
        CrossFadeHelper.fadeIn(paramAnonymousTransformState.getTransformedView(), paramAnonymousFloat);
        if (paramAnonymousTransformableView != null)
        {
          paramAnonymousTransformState.transformViewVerticalFrom(paramAnonymousTransformableView, this, paramAnonymousFloat);
          paramAnonymousTransformableView.recycle();
        }
        return true;
      }
      
      public boolean transformTo(TransformState paramAnonymousTransformState, TransformableView paramAnonymousTransformableView, float paramAnonymousFloat)
      {
        if (!(paramAnonymousTransformableView instanceof HybridNotificationView)) {
          return false;
        }
        paramAnonymousTransformableView = paramAnonymousTransformableView.getCurrentState(1);
        CrossFadeHelper.fadeOut(paramAnonymousTransformState.getTransformedView(), paramAnonymousFloat);
        if (paramAnonymousTransformableView != null)
        {
          paramAnonymousTransformState.transformViewVerticalTo(paramAnonymousTransformableView, this, paramAnonymousFloat);
          paramAnonymousTransformableView.recycle();
        }
        return true;
      }
    }, 2);
  }
  
  private void fadeProgressDark(final ProgressBar paramProgressBar, boolean paramBoolean, long paramLong)
  {
    startIntensityAnimation(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        float f = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        NotificationTemplateViewWrapper.-wrap0(NotificationTemplateViewWrapper.this, paramProgressBar, f);
      }
    }, paramBoolean, paramLong, null);
  }
  
  private static int interpolateColor(int paramInt1, int paramInt2, float paramFloat)
  {
    int i = Color.alpha(paramInt1);
    int j = Color.red(paramInt1);
    int k = Color.green(paramInt1);
    paramInt1 = Color.blue(paramInt1);
    int m = Color.alpha(paramInt2);
    int n = Color.red(paramInt2);
    int i1 = Color.green(paramInt2);
    paramInt2 = Color.blue(paramInt2);
    return Color.argb((int)(i * (1.0F - paramFloat) + m * paramFloat), (int)(j * (1.0F - paramFloat) + n * paramFloat), (int)(k * (1.0F - paramFloat) + i1 * paramFloat), (int)(paramInt1 * (1.0F - paramFloat) + paramInt2 * paramFloat));
  }
  
  private void resolveTemplateViews(StatusBarNotification paramStatusBarNotification)
  {
    this.mPicture = ((ImageView)this.mView.findViewById(16908356));
    this.mPicture.setTag(2131951682, paramStatusBarNotification.getNotification().getLargeIcon());
    this.mTitle = ((TextView)this.mView.findViewById(16908310));
    this.mText = ((TextView)this.mView.findViewById(16908414));
    paramStatusBarNotification = this.mView.findViewById(16908301);
    if ((paramStatusBarNotification instanceof ProgressBar)) {}
    for (this.mProgressBar = ((ProgressBar)paramStatusBarNotification);; this.mProgressBar = null)
    {
      this.mActionsContainer = this.mView.findViewById(16909228);
      return;
    }
  }
  
  private void setProgressBarDark(boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    if (this.mProgressBar != null)
    {
      if (paramBoolean2) {
        fadeProgressDark(this.mProgressBar, paramBoolean1, paramLong);
      }
    }
    else {
      return;
    }
    updateProgressDark(this.mProgressBar, paramBoolean1);
  }
  
  private void updateActionOffset()
  {
    if (this.mActionsContainer != null)
    {
      int i = Math.max(this.mContentHeight, this.mMinHeightHint);
      this.mActionsContainer.setTranslationY(i - this.mView.getHeight());
    }
  }
  
  private void updateProgressDark(ProgressBar paramProgressBar, float paramFloat)
  {
    int i = interpolateColor(this.mColor, -1, paramFloat);
    paramProgressBar.getIndeterminateDrawable().mutate().setTint(i);
    paramProgressBar.getProgressDrawable().mutate().setTint(i);
  }
  
  private void updateProgressDark(ProgressBar paramProgressBar, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (float f = 1.0F;; f = 0.0F)
    {
      updateProgressDark(paramProgressBar, f);
      return;
    }
  }
  
  public void notifyContentUpdated(StatusBarNotification paramStatusBarNotification)
  {
    resolveTemplateViews(paramStatusBarNotification);
    super.notifyContentUpdated(paramStatusBarNotification);
  }
  
  public void setContentHeight(int paramInt1, int paramInt2)
  {
    super.setContentHeight(paramInt1, paramInt2);
    this.mContentHeight = paramInt1;
    this.mMinHeightHint = paramInt2;
    updateActionOffset();
  }
  
  public void setDark(boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    if ((paramBoolean1 == this.mDark) && (this.mDarkInitialized)) {
      return;
    }
    super.setDark(paramBoolean1, paramBoolean2, paramLong);
    setPictureGrayscale(paramBoolean1, paramBoolean2, paramLong);
    setProgressBarDark(paramBoolean1, paramBoolean2, paramLong);
  }
  
  protected void setPictureGrayscale(boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    if (this.mPicture != null)
    {
      if (paramBoolean2) {
        fadeGrayscale(this.mPicture, paramBoolean1, paramLong);
      }
    }
    else {
      return;
    }
    updateGrayscale(this.mPicture, paramBoolean1);
  }
  
  protected void updateInvertHelper()
  {
    super.updateInvertHelper();
    View localView = this.mView.findViewById(16909243);
    if (localView != null) {
      this.mInvertHelper.addTarget(localView);
    }
  }
  
  protected void updateTransformedTypes()
  {
    super.updateTransformedTypes();
    if (this.mTitle != null) {
      this.mTransformationHelper.addTransformedView(1, this.mTitle);
    }
    if (this.mText != null) {
      this.mTransformationHelper.addTransformedView(2, this.mText);
    }
    if (this.mPicture != null) {
      this.mTransformationHelper.addTransformedView(3, this.mPicture);
    }
    if (this.mProgressBar != null) {
      this.mTransformationHelper.addTransformedView(4, this.mProgressBar);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\notification\NotificationTemplateViewWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */