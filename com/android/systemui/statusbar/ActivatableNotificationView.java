package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewConfiguration;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.classifier.FalsingManager;
import com.android.systemui.statusbar.notification.FakeShadowView;
import com.android.systemui.statusbar.notification.NotificationUtils;

public abstract class ActivatableNotificationView
  extends ExpandableOutlineView
{
  private static final Interpolator ACTIVATE_INVERSE_ALPHA_INTERPOLATOR = new PathInterpolator(0.0F, 0.0F, 0.5F, 1.0F);
  private static final Interpolator ACTIVATE_INVERSE_INTERPOLATOR = new PathInterpolator(0.6F, 0.0F, 0.5F, 1.0F);
  private boolean mActivated;
  private float mAnimationTranslationY;
  private float mAppearAnimationFraction = -1.0F;
  private RectF mAppearAnimationRect = new RectF();
  private float mAppearAnimationTranslation;
  private ValueAnimator mAppearAnimator;
  private ObjectAnimator mBackgroundAnimator;
  private ValueAnimator mBackgroundColorAnimator;
  private NotificationBackgroundView mBackgroundDimmed;
  private NotificationBackgroundView mBackgroundNormal;
  private ValueAnimator.AnimatorUpdateListener mBackgroundVisibilityUpdater = new ValueAnimator.AnimatorUpdateListener()
  {
    public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
    {
      ActivatableNotificationView.this.setNormalBackgroundVisibilityAmount(ActivatableNotificationView.-get1(ActivatableNotificationView.this).getAlpha());
      ActivatableNotificationView.-set3(ActivatableNotificationView.this, ActivatableNotificationView.-get0(ActivatableNotificationView.this).getAlpha());
    }
  };
  private float mBgAlpha = 1.0F;
  private int mBgTint = 0;
  private Interpolator mCurrentAlphaInterpolator;
  private Interpolator mCurrentAppearInterpolator;
  private int mCurrentBackgroundTint;
  private boolean mDark;
  private boolean mDimmed;
  private float mDimmedBackgroundFadeInAmount = -1.0F;
  private float mDownX;
  private float mDownY;
  private boolean mDrawingAppearAnimation;
  private AnimatorListenerAdapter mFadeInEndListener = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      super.onAnimationEnd(paramAnonymousAnimator);
      ActivatableNotificationView.-set4(ActivatableNotificationView.this, null);
      ActivatableNotificationView.-set3(ActivatableNotificationView.this, -1.0F);
      ActivatableNotificationView.this.updateBackground();
    }
  };
  private ValueAnimator mFadeInFromDarkAnimator;
  private FakeShadowView mFakeShadow;
  private FalsingManager mFalsingManager;
  private boolean mIsBelowSpeedBump;
  private final int mLegacyColor;
  private final int mLowPriorityColor;
  private final int mLowPriorityRippleColor;
  private float mNormalBackgroundVisibilityAmount;
  private final int mNormalColor;
  protected final int mNormalRippleColor;
  private OnActivatedListener mOnActivatedListener;
  private float mShadowAlpha = 1.0F;
  private boolean mShowingLegacyBackground;
  private final Interpolator mSlowOutFastInInterpolator;
  private final Interpolator mSlowOutLinearInInterpolator;
  private int mStartTint;
  private final Runnable mTapTimeoutRunnable = new Runnable()
  {
    public void run()
    {
      ActivatableNotificationView.this.makeInactive(true);
    }
  };
  private int mTargetTint;
  private final int mTintedRippleColor;
  private final float mTouchSlop;
  private boolean mTrackTouch;
  private ValueAnimator.AnimatorUpdateListener mUpdateOutlineListener = new ValueAnimator.AnimatorUpdateListener()
  {
    public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
    {
      ActivatableNotificationView.-wrap4(ActivatableNotificationView.this);
    }
  };
  
  public ActivatableNotificationView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mTouchSlop = ViewConfiguration.get(paramContext).getScaledTouchSlop();
    this.mSlowOutFastInInterpolator = new PathInterpolator(0.8F, 0.0F, 0.6F, 1.0F);
    this.mSlowOutLinearInInterpolator = new PathInterpolator(0.8F, 0.0F, 1.0F, 1.0F);
    setClipChildren(false);
    setClipToPadding(false);
    this.mLegacyColor = paramContext.getColor(2131493022);
    this.mNormalColor = paramContext.getColor(2131493023);
    this.mLowPriorityColor = paramContext.getColor(2131493025);
    this.mTintedRippleColor = paramContext.getColor(2131493029);
    this.mLowPriorityRippleColor = paramContext.getColor(2131493028);
    this.mNormalRippleColor = paramContext.getColor(2131493027);
    this.mFalsingManager = FalsingManager.getInstance(paramContext);
  }
  
  private int calculateBgColor(boolean paramBoolean)
  {
    if ((paramBoolean) && (this.mBgTint != 0)) {
      return this.mBgTint;
    }
    if (this.mShowingLegacyBackground) {
      return this.mLegacyColor;
    }
    if (this.mIsBelowSpeedBump) {
      return this.mLowPriorityColor;
    }
    return this.mNormalColor;
  }
  
  private void cancelAppearAnimation()
  {
    if (this.mAppearAnimator != null)
    {
      this.mAppearAnimator.cancel();
      this.mAppearAnimator = null;
    }
  }
  
  private void cancelFadeAnimations()
  {
    if (this.mBackgroundAnimator != null) {
      this.mBackgroundAnimator.cancel();
    }
    this.mBackgroundDimmed.animate().cancel();
    this.mBackgroundNormal.animate().cancel();
  }
  
  private void enableAppearDrawing(boolean paramBoolean)
  {
    if (paramBoolean != this.mDrawingAppearAnimation)
    {
      this.mDrawingAppearAnimation = paramBoolean;
      if (!paramBoolean)
      {
        setContentAlpha(1.0F);
        this.mAppearAnimationFraction = -1.0F;
        setOutlineRect(null);
      }
      invalidate();
    }
  }
  
  private void fadeDimmedBackground()
  {
    this.mBackgroundDimmed.animate().cancel();
    this.mBackgroundNormal.animate().cancel();
    if (this.mActivated)
    {
      updateBackground();
      return;
    }
    float f1;
    if (!shouldHideBackground())
    {
      if (this.mDimmed) {
        this.mBackgroundDimmed.setVisibility(0);
      }
    }
    else
    {
      if (!this.mDimmed) {
        break label145;
      }
      f1 = 1.0F;
      label63:
      if (!this.mDimmed) {
        break label150;
      }
    }
    int i;
    label145:
    label150:
    for (float f2 = 0.0F;; f2 = 1.0F)
    {
      i = 220;
      if (this.mBackgroundAnimator == null) {
        break label155;
      }
      f1 = ((Float)this.mBackgroundAnimator.getAnimatedValue()).floatValue();
      int j = (int)this.mBackgroundAnimator.getCurrentPlayTime();
      this.mBackgroundAnimator.removeAllListeners();
      this.mBackgroundAnimator.cancel();
      i = j;
      if (j > 0) {
        break label155;
      }
      updateBackground();
      return;
      this.mBackgroundNormal.setVisibility(0);
      break;
      f1 = 0.0F;
      break label63;
    }
    label155:
    this.mBackgroundNormal.setAlpha(f1);
    this.mBackgroundAnimator = ObjectAnimator.ofFloat(this.mBackgroundNormal, View.ALPHA, new float[] { f1, f2 });
    this.mBackgroundAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
    this.mBackgroundAnimator.setDuration(i);
    this.mBackgroundAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        ActivatableNotificationView.this.updateBackground();
        ActivatableNotificationView.-set1(ActivatableNotificationView.this, null);
        if (ActivatableNotificationView.-get2(ActivatableNotificationView.this) == null) {
          ActivatableNotificationView.-set3(ActivatableNotificationView.this, -1.0F);
        }
      }
    });
    this.mBackgroundAnimator.addUpdateListener(this.mBackgroundVisibilityUpdater);
    this.mBackgroundAnimator.start();
  }
  
  private void fadeInFromDark(long paramLong)
  {
    if (this.mDimmed) {}
    for (final NotificationBackgroundView localNotificationBackgroundView = this.mBackgroundDimmed;; localNotificationBackgroundView = this.mBackgroundNormal)
    {
      localNotificationBackgroundView.setAlpha(0.0F);
      this.mBackgroundVisibilityUpdater.onAnimationUpdate(null);
      localNotificationBackgroundView.setPivotX(this.mBackgroundDimmed.getWidth() / 2.0F);
      localNotificationBackgroundView.setPivotY(getActualHeight() / 2.0F);
      localNotificationBackgroundView.setScaleX(0.93F);
      localNotificationBackgroundView.setScaleY(0.93F);
      localNotificationBackgroundView.animate().alpha(1.0F).scaleX(1.0F).scaleY(1.0F).setDuration(170L).setStartDelay(paramLong).setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN).setListener(new AnimatorListenerAdapter()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          localNotificationBackgroundView.setScaleX(1.0F);
          localNotificationBackgroundView.setScaleY(1.0F);
          localNotificationBackgroundView.setAlpha(1.0F);
        }
      }).setUpdateListener(this.mBackgroundVisibilityUpdater).start();
      this.mFadeInFromDarkAnimator = TimeAnimator.ofFloat(new float[] { 0.0F, 1.0F });
      this.mFadeInFromDarkAnimator.setDuration(170L);
      this.mFadeInFromDarkAnimator.setStartDelay(paramLong);
      this.mFadeInFromDarkAnimator.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
      this.mFadeInFromDarkAnimator.addListener(this.mFadeInEndListener);
      this.mFadeInFromDarkAnimator.addUpdateListener(this.mUpdateOutlineListener);
      this.mFadeInFromDarkAnimator.start();
      return;
    }
  }
  
  private boolean handleTouchEventDimmed(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getActionMasked())
    {
    }
    for (;;)
    {
      return this.mTrackTouch;
      this.mDownX = paramMotionEvent.getX();
      this.mDownY = paramMotionEvent.getY();
      this.mTrackTouch = true;
      if (this.mDownY > getActualHeight())
      {
        this.mTrackTouch = false;
        continue;
        if (!isWithinTouchSlop(paramMotionEvent))
        {
          makeInactive(true);
          this.mTrackTouch = false;
          continue;
          if (isWithinTouchSlop(paramMotionEvent))
          {
            if (handleSlideBack()) {
              return true;
            }
            if (!this.mActivated)
            {
              makeActive();
              postDelayed(this.mTapTimeoutRunnable, 1200L);
            }
            else if (!performClick())
            {
              return false;
            }
          }
          else
          {
            makeInactive(true);
            this.mTrackTouch = false;
            continue;
            makeInactive(true);
            this.mTrackTouch = false;
          }
        }
      }
    }
  }
  
  private boolean isWithinTouchSlop(MotionEvent paramMotionEvent)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (Math.abs(paramMotionEvent.getX() - this.mDownX) < this.mTouchSlop)
    {
      bool1 = bool2;
      if (Math.abs(paramMotionEvent.getY() - this.mDownY) < this.mTouchSlop) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private void makeActive()
  {
    this.mFalsingManager.onNotificationActive();
    startActivateAnimation(false);
    this.mActivated = true;
    if (this.mOnActivatedListener != null) {
      this.mOnActivatedListener.onActivated(this);
    }
  }
  
  private void setBackgroundTintColor(int paramInt)
  {
    this.mCurrentBackgroundTint = paramInt;
    int i = paramInt;
    if (paramInt == this.mNormalColor) {
      i = 0;
    }
    this.mBackgroundDimmed.setTint(i);
    this.mBackgroundNormal.setTint(i);
  }
  
  private void setContentAlpha(float paramFloat)
  {
    View localView = getContentView();
    if (localView.hasOverlappingRendering()) {
      if ((paramFloat != 0.0F) && (paramFloat != 1.0F)) {
        break label46;
      }
    }
    label46:
    for (int i = 0;; i = 2)
    {
      if (localView.getLayerType() != i) {
        localView.setLayerType(i, null);
      }
      localView.setAlpha(paramFloat);
      return;
    }
  }
  
  private void startActivateAnimation(final boolean paramBoolean)
  {
    float f1 = 0.0F;
    if (!isAttachedToWindow()) {
      return;
    }
    int i = this.mBackgroundNormal.getWidth() / 2;
    int j = this.mBackgroundNormal.getActualHeight() / 2;
    float f2 = (float)Math.sqrt(i * i + j * j);
    Object localObject;
    Interpolator localInterpolator2;
    Interpolator localInterpolator1;
    if (paramBoolean)
    {
      localObject = ViewAnimationUtils.createCircularReveal(this.mBackgroundNormal, i, j, f2, 0.0F);
      this.mBackgroundNormal.setVisibility(0);
      if (paramBoolean) {
        break label198;
      }
      localInterpolator2 = Interpolators.LINEAR_OUT_SLOW_IN;
      localInterpolator1 = Interpolators.LINEAR_OUT_SLOW_IN;
      label90:
      ((Animator)localObject).setInterpolator(localInterpolator2);
      ((Animator)localObject).setDuration(220L);
      if (!paramBoolean) {
        break label211;
      }
      this.mBackgroundNormal.setAlpha(1.0F);
      ((Animator)localObject).addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          ActivatableNotificationView.this.updateBackground();
        }
      });
      ((Animator)localObject).start();
      label136:
      localObject = this.mBackgroundNormal.animate();
      if (!paramBoolean) {
        break label229;
      }
    }
    for (;;)
    {
      ((ViewPropertyAnimator)localObject).alpha(f1).setInterpolator(localInterpolator1).setUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          float f2 = paramAnonymousValueAnimator.getAnimatedFraction();
          float f1 = f2;
          if (paramBoolean) {
            f1 = 1.0F - f2;
          }
          ActivatableNotificationView.this.setNormalBackgroundVisibilityAmount(f1);
        }
      }).setDuration(220L);
      return;
      localObject = ViewAnimationUtils.createCircularReveal(this.mBackgroundNormal, i, j, 0.0F, f2);
      break;
      label198:
      localInterpolator2 = ACTIVATE_INVERSE_INTERPOLATOR;
      localInterpolator1 = ACTIVATE_INVERSE_ALPHA_INTERPOLATOR;
      break label90;
      label211:
      this.mBackgroundNormal.setAlpha(0.4F);
      ((Animator)localObject).start();
      break label136;
      label229:
      f1 = 1.0F;
    }
  }
  
  private void startAppearAnimation(final boolean paramBoolean, float paramFloat, long paramLong1, long paramLong2, final Runnable paramRunnable)
  {
    cancelAppearAnimation();
    this.mAnimationTranslationY = (getActualHeight() * paramFloat);
    if (this.mAppearAnimationFraction == -1.0F)
    {
      if (paramBoolean)
      {
        this.mAppearAnimationFraction = 0.0F;
        this.mAppearAnimationTranslation = this.mAnimationTranslationY;
      }
    }
    else
    {
      if (!paramBoolean) {
        break label192;
      }
      this.mCurrentAppearInterpolator = this.mSlowOutFastInInterpolator;
      this.mCurrentAlphaInterpolator = Interpolators.LINEAR_OUT_SLOW_IN;
    }
    for (paramFloat = 1.0F;; paramFloat = 0.0F)
    {
      this.mAppearAnimator = ValueAnimator.ofFloat(new float[] { this.mAppearAnimationFraction, paramFloat });
      this.mAppearAnimator.setInterpolator(Interpolators.LINEAR);
      this.mAppearAnimator.setDuration(((float)paramLong2 * Math.abs(this.mAppearAnimationFraction - paramFloat)));
      this.mAppearAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          ActivatableNotificationView.-set0(ActivatableNotificationView.this, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
          ActivatableNotificationView.-wrap2(ActivatableNotificationView.this);
          ActivatableNotificationView.-wrap3(ActivatableNotificationView.this);
          ActivatableNotificationView.this.invalidate();
        }
      });
      if (paramLong1 > 0L)
      {
        updateAppearAnimationAlpha();
        updateAppearRect();
        this.mAppearAnimator.setStartDelay(paramLong1);
      }
      this.mAppearAnimator.addListener(new AnimatorListenerAdapter()
      {
        private boolean mWasCancelled;
        
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          this.mWasCancelled = true;
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (paramRunnable != null) {
            paramRunnable.run();
          }
          if (!this.mWasCancelled)
          {
            ActivatableNotificationView.-wrap0(ActivatableNotificationView.this, false);
            ActivatableNotificationView.this.onAppearAnimationFinished(paramBoolean);
          }
        }
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          this.mWasCancelled = false;
        }
      });
      this.mAppearAnimator.start();
      return;
      this.mAppearAnimationFraction = 1.0F;
      this.mAppearAnimationTranslation = 0.0F;
      break;
      label192:
      this.mCurrentAppearInterpolator = Interpolators.FAST_OUT_SLOW_IN;
      this.mCurrentAlphaInterpolator = this.mSlowOutLinearInInterpolator;
    }
  }
  
  private void updateAppearAnimationAlpha()
  {
    float f = Math.min(1.0F, this.mAppearAnimationFraction / 1.0F);
    setContentAlpha(this.mCurrentAlphaInterpolator.getInterpolation(f));
  }
  
  private void updateAppearRect()
  {
    float f2 = 1.0F - this.mAppearAnimationFraction;
    float f1 = this.mCurrentAppearInterpolator.getInterpolation(f2) * this.mAnimationTranslationY;
    this.mAppearAnimationTranslation = f1;
    float f3 = Math.min(1.0F, Math.max(0.0F, (f2 - 0.0F) / 0.8F));
    f3 = this.mCurrentAppearInterpolator.getInterpolation(f3);
    f3 = getWidth() * 0.475F * f3;
    float f4 = getWidth() - f3;
    f2 = Math.max(0.0F, (f2 - 0.0F) / 1.0F);
    float f5 = this.mCurrentAppearInterpolator.getInterpolation(f2);
    int i = getActualHeight();
    if (this.mAnimationTranslationY > 0.0F)
    {
      f1 = i - this.mAnimationTranslationY * f5 * 0.1F - f1;
      f2 = f1 * f5;
    }
    for (;;)
    {
      this.mAppearAnimationRect.set(f3, f2, f4, f1);
      setOutlineRect(f3, this.mAppearAnimationTranslation + f2, f4, this.mAppearAnimationTranslation + f1);
      return;
      f2 = (i + this.mAnimationTranslationY) * f5 * 0.1F - f1;
      f1 = i * (1.0F - f5) + f2 * f5;
    }
  }
  
  private void updateBackgroundTint(boolean paramBoolean)
  {
    if (this.mBackgroundColorAnimator != null) {
      this.mBackgroundColorAnimator.cancel();
    }
    int i = getRippleColor();
    this.mBackgroundDimmed.setRippleColor(i);
    this.mBackgroundNormal.setRippleColor(i);
    i = calculateBgColor();
    if (!paramBoolean) {
      setBackgroundTintColor(i);
    }
    while (i == this.mCurrentBackgroundTint) {
      return;
    }
    this.mStartTint = this.mCurrentBackgroundTint;
    this.mTargetTint = i;
    this.mBackgroundColorAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
    this.mBackgroundColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        int i = NotificationUtils.interpolateColors(ActivatableNotificationView.-get3(ActivatableNotificationView.this), ActivatableNotificationView.-get4(ActivatableNotificationView.this), paramAnonymousValueAnimator.getAnimatedFraction());
        ActivatableNotificationView.-wrap1(ActivatableNotificationView.this, i);
      }
    });
    this.mBackgroundColorAnimator.setDuration(360L);
    this.mBackgroundColorAnimator.setInterpolator(Interpolators.LINEAR);
    this.mBackgroundColorAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        ActivatableNotificationView.-set2(ActivatableNotificationView.this, null);
      }
    });
    this.mBackgroundColorAnimator.start();
  }
  
  private void updateOutlineAlpha()
  {
    if (this.mDark)
    {
      setOutlineAlpha(0.0F);
      return;
    }
    float f2 = (0.7F + 0.3F * this.mNormalBackgroundVisibilityAmount) * this.mShadowAlpha;
    float f1 = f2;
    if (this.mFadeInFromDarkAnimator != null) {
      f1 = f2 * this.mFadeInFromDarkAnimator.getAnimatedFraction();
    }
    setOutlineAlpha(f1);
  }
  
  public int calculateBgColor()
  {
    return calculateBgColor(true);
  }
  
  public void cancelAppearDrawing()
  {
    cancelAppearAnimation();
    enableAppearDrawing(false);
  }
  
  protected boolean disallowSingleClick(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    if (this.mDrawingAppearAnimation)
    {
      paramCanvas.save();
      paramCanvas.translate(0.0F, this.mAppearAnimationTranslation);
    }
    super.dispatchDraw(paramCanvas);
    if (this.mDrawingAppearAnimation) {
      paramCanvas.restore();
    }
  }
  
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2)
  {
    if (!this.mDimmed) {
      this.mBackgroundNormal.drawableHotspotChanged(paramFloat1, paramFloat2);
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    if (this.mDimmed)
    {
      this.mBackgroundDimmed.setState(getDrawableState());
      return;
    }
    this.mBackgroundNormal.setState(getDrawableState());
  }
  
  public int getBackgroundColorWithoutTint()
  {
    return calculateBgColor(false);
  }
  
  protected abstract View getContentView();
  
  protected int getRippleColor()
  {
    if (this.mBgTint != 0) {
      return this.mTintedRippleColor;
    }
    if (this.mShowingLegacyBackground) {
      return this.mTintedRippleColor;
    }
    if (this.mIsBelowSpeedBump) {
      return this.mLowPriorityRippleColor;
    }
    return this.mNormalRippleColor;
  }
  
  public float getShadowAlpha()
  {
    return this.mShadowAlpha;
  }
  
  protected boolean handleSlideBack()
  {
    return false;
  }
  
  public void makeInactive(boolean paramBoolean)
  {
    if (this.mActivated)
    {
      this.mActivated = false;
      if (this.mDimmed)
      {
        if (!paramBoolean) {
          break label55;
        }
        startActivateAnimation(true);
      }
    }
    for (;;)
    {
      if (this.mOnActivatedListener != null) {
        this.mOnActivatedListener.onActivationReset(this);
      }
      removeCallbacks(this.mTapTimeoutRunnable);
      return;
      label55:
      updateBackground();
    }
  }
  
  protected void onAppearAnimationFinished(boolean paramBoolean) {}
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mBackgroundNormal = ((NotificationBackgroundView)findViewById(2131952283));
    this.mFakeShadow = ((FakeShadowView)findViewById(2131952288));
    this.mBackgroundDimmed = ((NotificationBackgroundView)findViewById(2131952284));
    this.mBackgroundNormal.setCustomBackground(2130838071);
    this.mBackgroundDimmed.setCustomBackground(2130838072);
    updateBackground();
    updateBackgroundTint();
    updateOutlineAlpha();
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((!this.mDimmed) || (this.mActivated)) {}
    while ((paramMotionEvent.getActionMasked() != 0) || (!disallowSingleClick(paramMotionEvent))) {
      return super.onInterceptTouchEvent(paramMotionEvent);
    }
    return true;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    setPivotX(getWidth() / 2);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mDimmed)
    {
      boolean bool1 = this.mActivated;
      boolean bool2 = handleTouchEventDimmed(paramMotionEvent);
      if ((bool1) && (bool2) && (paramMotionEvent.getAction() == 1))
      {
        this.mFalsingManager.onNotificationDoubleTap();
        removeCallbacks(this.mTapTimeoutRunnable);
      }
      return bool2;
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void performAddAnimation(long paramLong1, long paramLong2)
  {
    enableAppearDrawing(true);
    if (this.mDrawingAppearAnimation) {
      startAppearAnimation(true, -1.0F, paramLong1, paramLong2, null);
    }
  }
  
  public void performRemoveAnimation(long paramLong, float paramFloat, Runnable paramRunnable)
  {
    enableAppearDrawing(true);
    if (this.mDrawingAppearAnimation) {
      startAppearAnimation(false, paramFloat, 0L, paramLong, paramRunnable);
    }
    while (paramRunnable == null) {
      return;
    }
    paramRunnable.run();
  }
  
  public void reset()
  {
    setTintColor(0);
    resetBackgroundAlpha();
    setShowingLegacyBackground(false);
    setBelowSpeedBump(false);
  }
  
  protected void resetBackgroundAlpha()
  {
    updateBackgroundAlpha(0.0F);
  }
  
  public void setActualHeight(int paramInt, boolean paramBoolean)
  {
    super.setActualHeight(paramInt, paramBoolean);
    setPivotY(paramInt / 2);
    this.mBackgroundNormal.setActualHeight(paramInt);
    this.mBackgroundDimmed.setActualHeight(paramInt);
  }
  
  public void setBelowSpeedBump(boolean paramBoolean)
  {
    super.setBelowSpeedBump(paramBoolean);
    if (paramBoolean != this.mIsBelowSpeedBump)
    {
      this.mIsBelowSpeedBump = paramBoolean;
      updateBackgroundTint();
    }
  }
  
  public void setClipTopAmount(int paramInt)
  {
    super.setClipTopAmount(paramInt);
    this.mBackgroundNormal.setClipTopAmount(paramInt);
    this.mBackgroundDimmed.setClipTopAmount(paramInt);
  }
  
  public void setDark(boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    super.setDark(paramBoolean1, paramBoolean2, paramLong);
    if (this.mDark == paramBoolean1) {
      return;
    }
    this.mDark = paramBoolean1;
    updateBackground();
    if ((paramBoolean1) || (!paramBoolean2) || (shouldHideBackground())) {}
    for (;;)
    {
      updateOutlineAlpha();
      return;
      fadeInFromDark(paramLong);
    }
  }
  
  public void setDimmed(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mDimmed != paramBoolean1)
    {
      this.mDimmed = paramBoolean1;
      resetBackgroundAlpha();
      if (paramBoolean2) {
        fadeDimmedBackground();
      }
    }
    else
    {
      return;
    }
    updateBackground();
  }
  
  public void setFakeShadowIntensity(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2)
  {
    this.mFakeShadow.setFakeShadowTranslationZ((getTranslationZ() + 0.1F) * paramFloat1, paramFloat2, paramInt1, paramInt2);
  }
  
  public void setNormalBackgroundVisibilityAmount(float paramFloat)
  {
    this.mNormalBackgroundVisibilityAmount = paramFloat;
    updateOutlineAlpha();
  }
  
  public void setOnActivatedListener(OnActivatedListener paramOnActivatedListener)
  {
    this.mOnActivatedListener = paramOnActivatedListener;
  }
  
  public void setShadowAlpha(float paramFloat)
  {
    if (paramFloat != this.mShadowAlpha)
    {
      this.mShadowAlpha = paramFloat;
      updateOutlineAlpha();
    }
  }
  
  public void setShowingLegacyBackground(boolean paramBoolean)
  {
    this.mShowingLegacyBackground = paramBoolean;
    updateBackgroundTint();
  }
  
  public void setTintColor(int paramInt)
  {
    setTintColor(paramInt, false);
  }
  
  public void setTintColor(int paramInt, boolean paramBoolean)
  {
    this.mBgTint = paramInt;
    updateBackgroundTint(paramBoolean);
  }
  
  protected boolean shouldHideBackground()
  {
    return this.mDark;
  }
  
  protected void updateBackground()
  {
    int j = 4;
    cancelFadeAnimations();
    if (shouldHideBackground())
    {
      this.mBackgroundDimmed.setVisibility(4);
      this.mBackgroundNormal.setVisibility(4);
      if (this.mBackgroundNormal.getVisibility() != 0) {
        break label169;
      }
    }
    label67:
    label123:
    label169:
    for (float f = 1.0F;; f = 0.0F)
    {
      setNormalBackgroundVisibilityAmount(f);
      return;
      if (this.mDimmed)
      {
        boolean bool;
        NotificationBackgroundView localNotificationBackgroundView;
        if (isGroupExpansionChanging())
        {
          bool = isChildInGroup();
          localNotificationBackgroundView = this.mBackgroundDimmed;
          if (!bool) {
            break label123;
          }
        }
        for (int i = 4;; i = 0)
        {
          localNotificationBackgroundView.setVisibility(i);
          localNotificationBackgroundView = this.mBackgroundNormal;
          if (!this.mActivated)
          {
            i = j;
            if (!bool) {}
          }
          else
          {
            i = 0;
          }
          localNotificationBackgroundView.setVisibility(i);
          break;
          bool = false;
          break label67;
        }
      }
      this.mBackgroundDimmed.setVisibility(4);
      this.mBackgroundNormal.setVisibility(0);
      this.mBackgroundNormal.setAlpha(1.0F);
      removeCallbacks(this.mTapTimeoutRunnable);
      makeInactive(false);
      break;
    }
  }
  
  protected void updateBackgroundAlpha(float paramFloat)
  {
    if ((isChildInGroup()) && (this.mDimmed)) {}
    for (;;)
    {
      this.mBgAlpha = paramFloat;
      if (this.mDimmedBackgroundFadeInAmount != -1.0F) {
        this.mBgAlpha *= this.mDimmedBackgroundFadeInAmount;
      }
      this.mBackgroundDimmed.setAlpha(this.mBgAlpha);
      return;
      paramFloat = 1.0F;
    }
  }
  
  protected void updateBackgroundTint()
  {
    updateBackgroundTint(false);
  }
  
  public static abstract interface OnActivatedListener
  {
    public abstract void onActivated(ActivatableNotificationView paramActivatableNotificationView);
    
    public abstract void onActivationReset(ActivatableNotificationView paramActivatableNotificationView);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\ActivatableNotificationView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */