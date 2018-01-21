package com.android.systemui.statusbar.notification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import android.view.NotificationHeaderView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.ViewInvertHelper;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.statusbar.ViewTransformationHelper;
import com.android.systemui.util.Utils;
import java.util.Stack;

public class NotificationHeaderViewWrapper
  extends NotificationViewWrapper
{
  protected int mColor;
  private Context mContext;
  private ImageView mExpandButton;
  private ImageView mIcon;
  private final PorterDuffColorFilter mIconColorFilter = new PorterDuffColorFilter(0, PorterDuff.Mode.SRC_ATOP);
  private final int mIconDarkAlpha;
  private final int mIconDarkColor = -1;
  protected final ViewInvertHelper mInvertHelper;
  private StatusBarNotification mNotification;
  private NotificationHeaderView mNotificationHeader;
  protected final ViewTransformationHelper mTransformationHelper;
  
  protected NotificationHeaderViewWrapper(Context paramContext, View paramView, ExpandableNotificationRow paramExpandableNotificationRow)
  {
    super(paramView, paramExpandableNotificationRow);
    this.mIconDarkAlpha = paramContext.getResources().getInteger(2131624012);
    this.mInvertHelper = new ViewInvertHelper(paramContext, 700L);
    this.mTransformationHelper = new ViewTransformationHelper();
    this.mContext = paramContext;
    this.mNotification = paramExpandableNotificationRow.getStatusBarNotification();
    resolveHeaderViews();
    updateInvertHelper();
  }
  
  private void addRemainingTransformTypes()
  {
    this.mTransformationHelper.addRemainingTransformTypes(this.mView);
  }
  
  private void fadeIconAlpha(final ImageView paramImageView, boolean paramBoolean, long paramLong)
  {
    startIntensityAnimation(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        float f = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        paramImageView.setImageAlpha((int)((1.0F - f) * 255.0F + NotificationHeaderViewWrapper.-get0(NotificationHeaderViewWrapper.this) * f));
      }
    }, paramBoolean, paramLong, null);
  }
  
  private void fadeIconColorFilter(final ImageView paramImageView, boolean paramBoolean, long paramLong)
  {
    startIntensityAnimation(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        NotificationHeaderViewWrapper.-wrap0(NotificationHeaderViewWrapper.this, paramImageView, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
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
  
  private int resolveColor(ImageView paramImageView)
  {
    if ((paramImageView != null) && (paramImageView.getDrawable() != null))
    {
      paramImageView = paramImageView.getDrawable().getColorFilter();
      if ((paramImageView instanceof PorterDuffColorFilter)) {
        return ((PorterDuffColorFilter)paramImageView).getColor();
      }
    }
    return 0;
  }
  
  private void updateCropToPaddingForImageViews()
  {
    Stack localStack = new Stack();
    localStack.push(this.mView);
    while (!localStack.isEmpty())
    {
      Object localObject = (View)localStack.pop();
      if ((localObject instanceof ImageView))
      {
        ((ImageView)localObject).setCropToPadding(true);
      }
      else if ((localObject instanceof ViewGroup))
      {
        localObject = (ViewGroup)localObject;
        int i = 0;
        while (i < ((ViewGroup)localObject).getChildCount())
        {
          localStack.push(((ViewGroup)localObject).getChildAt(i));
          i += 1;
        }
      }
    }
  }
  
  private void updateIconAlpha(ImageView paramImageView, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = this.mIconDarkAlpha;; i = 255)
    {
      paramImageView.setImageAlpha(i);
      return;
    }
  }
  
  private void updateIconColorFilter(ImageView paramImageView, float paramFloat)
  {
    int i = interpolateColor(this.mColor, -1, paramFloat);
    this.mIconColorFilter.setColor(i);
    paramImageView = paramImageView.getDrawable();
    if (paramImageView != null)
    {
      paramImageView = paramImageView.mutate();
      paramImageView.setColorFilter(null);
      paramImageView.setColorFilter(this.mIconColorFilter);
    }
  }
  
  private void updateIconColorFilter(ImageView paramImageView, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (float f = 1.0F;; f = 0.0F)
    {
      updateIconColorFilter(paramImageView, f);
      return;
    }
  }
  
  protected void fadeGrayscale(final ImageView paramImageView, final boolean paramBoolean, long paramLong)
  {
    startIntensityAnimation(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        NotificationHeaderViewWrapper.this.updateGrayscaleMatrix(((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
        paramImageView.setColorFilter(new ColorMatrixColorFilter(NotificationHeaderViewWrapper.this.mGrayscaleColorMatrix));
      }
    }, paramBoolean, paramLong, new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if (!paramBoolean) {
          paramImageView.setColorFilter(null);
        }
      }
    });
  }
  
  public TransformState getCurrentState(int paramInt)
  {
    return this.mTransformationHelper.getCurrentState(paramInt);
  }
  
  public NotificationHeaderView getNotificationHeader()
  {
    return this.mNotificationHeader;
  }
  
  public void notifyContentUpdated(StatusBarNotification paramStatusBarNotification)
  {
    super.notifyContentUpdated(paramStatusBarNotification);
    ArraySet localArraySet = this.mTransformationHelper.getAllTransformingViews();
    this.mNotification = paramStatusBarNotification;
    resolveHeaderViews();
    updateInvertHelper();
    updateTransformedTypes();
    addRemainingTransformTypes();
    updateCropToPaddingForImageViews();
    paramStatusBarNotification = this.mTransformationHelper.getAllTransformingViews();
    int i = 0;
    while (i < localArraySet.size())
    {
      View localView = (View)localArraySet.valueAt(i);
      if (!paramStatusBarNotification.contains(localView)) {
        this.mTransformationHelper.resetTransformedView(localView);
      }
      i += 1;
    }
  }
  
  protected void resolveHeaderViews()
  {
    this.mIcon = ((ImageView)this.mView.findViewById(16908294));
    this.mExpandButton = ((ImageView)this.mView.findViewById(16909240));
    this.mColor = resolveColor(this.mExpandButton);
    this.mNotificationHeader = ((NotificationHeaderView)this.mView.findViewById(16909234));
    TextView localTextView;
    if (this.mNotification != null)
    {
      localTextView = (TextView)this.mNotificationHeader.findViewById(16909235);
      if (localTextView.getText() == null) {
        break label146;
      }
    }
    label146:
    for (String str = localTextView.getText().toString();; str = "")
    {
      boolean bool = Utils.isParallelApp(this.mNotification.getUid());
      if ((localTextView != null) && (bool)) {
        localTextView.setText(this.mContext.getResources().getString(84607041, new Object[] { str }));
      }
      return;
    }
  }
  
  public void setDark(boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    if ((paramBoolean1 == this.mDark) && (this.mDarkInitialized)) {
      return;
    }
    super.setDark(paramBoolean1, paramBoolean2, paramLong);
    if (paramBoolean2) {
      this.mInvertHelper.fade(paramBoolean1, paramLong);
    }
    while ((this.mIcon == null) || (this.mRow.isChildInGroup()))
    {
      return;
      this.mInvertHelper.update(paramBoolean1);
    }
    int i;
    if (this.mNotificationHeader.getOriginalIconColor() != -1) {
      i = 1;
    }
    while (paramBoolean2) {
      if (i != 0)
      {
        fadeIconColorFilter(this.mIcon, paramBoolean1, paramLong);
        fadeIconAlpha(this.mIcon, paramBoolean1, paramLong);
        return;
        i = 0;
      }
      else
      {
        fadeGrayscale(this.mIcon, paramBoolean1, paramLong);
        return;
      }
    }
    if (i != 0)
    {
      updateIconColorFilter(this.mIcon, paramBoolean1);
      updateIconAlpha(this.mIcon, paramBoolean1);
      return;
    }
    updateGrayscale(this.mIcon, paramBoolean1);
  }
  
  public void setVisible(boolean paramBoolean)
  {
    super.setVisible(paramBoolean);
    this.mTransformationHelper.setVisible(paramBoolean);
  }
  
  public void transformFrom(TransformableView paramTransformableView)
  {
    this.mTransformationHelper.transformFrom(paramTransformableView);
  }
  
  public void transformFrom(TransformableView paramTransformableView, float paramFloat)
  {
    this.mTransformationHelper.transformFrom(paramTransformableView, paramFloat);
  }
  
  public void transformTo(TransformableView paramTransformableView, float paramFloat)
  {
    this.mTransformationHelper.transformTo(paramTransformableView, paramFloat);
  }
  
  public void transformTo(TransformableView paramTransformableView, Runnable paramRunnable)
  {
    this.mTransformationHelper.transformTo(paramTransformableView, paramRunnable);
  }
  
  public void updateExpandability(boolean paramBoolean, View.OnClickListener paramOnClickListener)
  {
    Object localObject = this.mExpandButton;
    int i;
    if (paramBoolean)
    {
      i = 0;
      ((ImageView)localObject).setVisibility(i);
      localObject = this.mNotificationHeader;
      if (!paramBoolean) {
        break label41;
      }
    }
    for (;;)
    {
      ((NotificationHeaderView)localObject).setOnClickListener(paramOnClickListener);
      return;
      i = 8;
      break;
      label41:
      paramOnClickListener = null;
    }
  }
  
  protected void updateGrayscale(ImageView paramImageView, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      updateGrayscaleMatrix(1.0F);
      paramImageView.setColorFilter(new ColorMatrixColorFilter(this.mGrayscaleColorMatrix));
      return;
    }
    paramImageView.setColorFilter(null);
  }
  
  protected void updateInvertHelper()
  {
    this.mInvertHelper.clearTargets();
    int i = 0;
    while (i < this.mNotificationHeader.getChildCount())
    {
      View localView = this.mNotificationHeader.getChildAt(i);
      if (localView != this.mIcon) {
        this.mInvertHelper.addTarget(localView);
      }
      i += 1;
    }
  }
  
  protected void updateTransformedTypes()
  {
    this.mTransformationHelper.reset();
    this.mTransformationHelper.addTransformedView(0, this.mNotificationHeader);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\notification\NotificationHeaderViewWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */