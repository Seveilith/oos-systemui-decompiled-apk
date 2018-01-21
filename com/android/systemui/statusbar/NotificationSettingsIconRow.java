package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import com.android.systemui.Interpolators;

public class NotificationSettingsIconRow
  extends FrameLayout
  implements View.OnClickListener
{
  private boolean mAnimating = false;
  private boolean mDismissing = false;
  private ValueAnimator mFadeAnimator;
  private AlphaOptimizedImageView mGearIcon;
  private int[] mGearLocation = new int[2];
  private float mHorizSpaceForGear;
  private boolean mIconPlaced = false;
  private SettingsIconRowListener mListener;
  private boolean mOnLeft = true;
  private ExpandableNotificationRow mParent;
  private int[] mParentLocation = new int[2];
  private boolean mSettingsFadedIn = false;
  private boolean mSnapping = false;
  private int mVertSpaceForGear;
  
  public NotificationSettingsIconRow(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public NotificationSettingsIconRow(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public NotificationSettingsIconRow(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public NotificationSettingsIconRow(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public void cancelFadeAnimator()
  {
    if (this.mFadeAnimator != null) {
      this.mFadeAnimator.cancel();
    }
  }
  
  public void fadeInSettings(final boolean paramBoolean, final float paramFloat1, final float paramFloat2)
  {
    if ((this.mDismissing) || (this.mAnimating)) {
      return;
    }
    if (isIconLocationChange(paramFloat1)) {
      setGearAlpha(0.0F);
    }
    if (paramFloat1 > 0.0F) {}
    for (boolean bool = true;; bool = false)
    {
      setIconLocation(bool);
      this.mFadeAnimator = ValueAnimator.ofFloat(new float[] { this.mGearIcon.getAlpha(), 1.0F });
      this.mFadeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          int i = 1;
          float f = Math.abs(paramFloat1);
          if ((paramBoolean) && (paramFloat1 <= paramFloat2)) {}
          while ((i == 0) || (NotificationSettingsIconRow.-get1(NotificationSettingsIconRow.this)))
          {
            return;
            if ((paramBoolean) || (f > paramFloat2)) {
              i = 0;
            }
          }
          NotificationSettingsIconRow.this.setGearAlpha(((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
        }
      });
      this.mFadeAnimator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          NotificationSettingsIconRow.-get0(NotificationSettingsIconRow.this).setAlpha(0.0F);
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          boolean bool = false;
          NotificationSettingsIconRow.-set0(NotificationSettingsIconRow.this, false);
          paramAnonymousAnimator = NotificationSettingsIconRow.this;
          if (NotificationSettingsIconRow.-get0(NotificationSettingsIconRow.this).getAlpha() == 1.0F) {
            bool = true;
          }
          NotificationSettingsIconRow.-set1(paramAnonymousAnimator, bool);
        }
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          NotificationSettingsIconRow.-set0(NotificationSettingsIconRow.this, true);
        }
      });
      this.mFadeAnimator.setInterpolator(Interpolators.ALPHA_IN);
      this.mFadeAnimator.setDuration(200L);
      this.mFadeAnimator.start();
      return;
    }
  }
  
  public float getSpaceForGear()
  {
    return this.mHorizSpaceForGear;
  }
  
  public boolean isIconLocationChange(float paramFloat)
  {
    int i;
    if (paramFloat > this.mGearIcon.getPaddingStart())
    {
      i = 1;
      if (paramFloat >= -this.mGearIcon.getPaddingStart()) {
        break label60;
      }
    }
    label60:
    for (int j = 1;; j = 0)
    {
      if (((!this.mOnLeft) || (j == 0)) && ((this.mOnLeft) || (i == 0))) {
        break label65;
      }
      return true;
      i = 0;
      break;
    }
    label65:
    return false;
  }
  
  public boolean isIconOnLeft()
  {
    return this.mOnLeft;
  }
  
  public boolean isVisible()
  {
    return this.mGearIcon.getAlpha() > 0.0F;
  }
  
  public void onClick(View paramView)
  {
    if ((paramView.getId() == 2131952117) && (this.mListener != null))
    {
      this.mGearIcon.getLocationOnScreen(this.mGearLocation);
      this.mParent.getLocationOnScreen(this.mParentLocation);
      int i = (int)(this.mHorizSpaceForGear / 2.0F);
      int j = (int)(this.mGearIcon.getTranslationY() * 2.0F + this.mGearIcon.getHeight()) / 2;
      int k = this.mGearLocation[0];
      int m = this.mParentLocation[0];
      int n = this.mGearLocation[1];
      int i1 = this.mParentLocation[1];
      this.mListener.onGearTouched(this.mParent, k - m + i, n - i1 + j);
    }
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mGearIcon = ((AlphaOptimizedImageView)findViewById(2131952117));
    this.mGearIcon.setOnClickListener(this);
    setOnClickListener(this);
    this.mHorizSpaceForGear = getResources().getDimensionPixelOffset(2131755376);
    this.mVertSpaceForGear = getResources().getDimensionPixelOffset(2131755369);
    resetState();
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    setIconLocation(this.mOnLeft);
  }
  
  public void resetState()
  {
    setGearAlpha(0.0F);
    this.mIconPlaced = false;
    this.mSettingsFadedIn = false;
    this.mAnimating = false;
    this.mSnapping = false;
    this.mDismissing = false;
    setIconLocation(true);
    if (this.mListener != null) {
      this.mListener.onSettingsIconRowReset(this.mParent);
    }
  }
  
  public void setAppName(String paramString)
  {
    paramString = String.format(getResources().getString(2131690523), new Object[] { paramString });
    this.mGearIcon.setContentDescription(paramString);
  }
  
  public void setGearAlpha(float paramFloat)
  {
    if (paramFloat == 0.0F)
    {
      this.mSettingsFadedIn = false;
      setVisibility(4);
    }
    for (;;)
    {
      this.mGearIcon.setAlpha(paramFloat);
      return;
      setVisibility(0);
    }
  }
  
  public void setGearListener(SettingsIconRowListener paramSettingsIconRowListener)
  {
    this.mListener = paramSettingsIconRowListener;
  }
  
  public void setIconLocation(boolean paramBoolean)
  {
    float f2 = 0.0F;
    if ((this.mIconPlaced) && (paramBoolean == this.mOnLeft)) {}
    while ((this.mSnapping) || (this.mParent == null) || (this.mGearIcon.getWidth() == 0)) {
      return;
    }
    boolean bool = this.mParent.isLayoutRtl();
    float f1;
    label76:
    float f3;
    if (bool)
    {
      f1 = -(this.mParent.getWidth() - this.mHorizSpaceForGear);
      if (!bool) {
        break label123;
      }
      f3 = (this.mHorizSpaceForGear - this.mGearIcon.getWidth()) / 2.0F;
      if (!paramBoolean) {
        break label140;
      }
    }
    label123:
    label140:
    for (f1 += f3;; f1 = f2 + f3)
    {
      setTranslationX(f1);
      this.mOnLeft = paramBoolean;
      this.mIconPlaced = true;
      return;
      f1 = 0.0F;
      break;
      f2 = this.mParent.getWidth() - this.mHorizSpaceForGear;
      break label76;
    }
  }
  
  public void setNotificationRowParent(ExpandableNotificationRow paramExpandableNotificationRow)
  {
    this.mParent = paramExpandableNotificationRow;
    setIconLocation(this.mOnLeft);
  }
  
  public void setSnapping(boolean paramBoolean)
  {
    this.mSnapping = paramBoolean;
  }
  
  public void updateSettingsIcons(float paramFloat1, float paramFloat2)
  {
    float f;
    if ((!this.mAnimating) && (this.mSettingsFadedIn))
    {
      f = paramFloat2 * 0.3F;
      paramFloat1 = Math.abs(paramFloat1);
      if (paramFloat1 != 0.0F) {
        break label39;
      }
      paramFloat1 = 0.0F;
    }
    for (;;)
    {
      setGearAlpha(paramFloat1);
      return;
      return;
      label39:
      if (paramFloat1 <= f) {
        paramFloat1 = 1.0F;
      } else {
        paramFloat1 = 1.0F - (paramFloat1 - f) / (paramFloat2 - f);
      }
    }
  }
  
  public void updateVerticalLocation()
  {
    if (this.mParent == null) {
      return;
    }
    int i = this.mParent.getCollapsedHeight();
    if (i < this.mVertSpaceForGear)
    {
      this.mGearIcon.setTranslationY(i / 2 - this.mGearIcon.getHeight() / 2);
      return;
    }
    this.mGearIcon.setTranslationY((this.mVertSpaceForGear - this.mGearIcon.getHeight()) / 2);
  }
  
  public static abstract interface SettingsIconRowListener
  {
    public abstract void onGearTouched(ExpandableNotificationRow paramExpandableNotificationRow, int paramInt1, int paramInt2);
    
    public abstract void onSettingsIconRowReset(ExpandableNotificationRow paramExpandableNotificationRow);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\NotificationSettingsIconRow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */