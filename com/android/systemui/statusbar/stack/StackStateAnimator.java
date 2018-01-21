package com.android.systemui.statusbar.stack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.animation.Interpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.ExpandableView;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

public class StackStateAnimator
{
  private AnimationFilter mAnimationFilter = new AnimationFilter();
  private Stack<AnimatorListenerAdapter> mAnimationListenerPool = new Stack();
  private HashSet<Animator> mAnimatorSet = new HashSet();
  private ValueAnimator mBottomOverScrollAnimator;
  private ArrayList<View> mChildrenToClearFromOverlay = new ArrayList();
  private long mCurrentAdditionalDelay;
  private int mCurrentLastNotAddedIndex;
  private long mCurrentLength;
  private final int mGoToFullShadeAppearingTranslation;
  private HashSet<View> mHeadsUpAppearChildren = new HashSet();
  private int mHeadsUpAppearHeightBottom;
  private final Interpolator mHeadsUpAppearInterpolator;
  private HashSet<View> mHeadsUpDisappearChildren = new HashSet();
  public NotificationStackScrollLayout mHostLayout;
  private ArrayList<View> mNewAddChildren = new ArrayList();
  private ArrayList<NotificationStackScrollLayout.AnimationEvent> mNewEvents = new ArrayList();
  private boolean mShadeExpanded;
  private final StackViewState mTmpState = new StackViewState();
  private ValueAnimator mTopOverScrollAnimator;
  
  public StackStateAnimator(NotificationStackScrollLayout paramNotificationStackScrollLayout)
  {
    this.mHostLayout = paramNotificationStackScrollLayout;
    this.mGoToFullShadeAppearingTranslation = paramNotificationStackScrollLayout.getContext().getResources().getDimensionPixelSize(2131755512);
    this.mHeadsUpAppearInterpolator = new HeadsUpAppearInterpolator();
  }
  
  private void abortAnimation(View paramView, int paramInt)
  {
    paramView = (Animator)getChildTag(paramView, paramInt);
    if (paramView != null) {
      paramView.cancel();
    }
  }
  
  private boolean applyWithoutAnimation(ExpandableView paramExpandableView, StackViewState paramStackViewState, StackScrollState paramStackScrollState)
  {
    if (this.mShadeExpanded) {
      return false;
    }
    if (getChildTag(paramExpandableView, 2131951644) != null) {
      return false;
    }
    if ((this.mHeadsUpDisappearChildren.contains(paramExpandableView)) || (this.mHeadsUpAppearChildren.contains(paramExpandableView))) {
      return false;
    }
    if (NotificationStackScrollLayout.isPinnedHeadsUp(paramExpandableView)) {
      return false;
    }
    paramStackScrollState.applyState(paramExpandableView, paramStackViewState);
    return true;
  }
  
  private long calculateChildAnimationDelay(StackViewState paramStackViewState, StackScrollState paramStackScrollState)
  {
    if (this.mAnimationFilter.hasDarkEvent) {
      return calculateDelayDark(paramStackViewState);
    }
    if (this.mAnimationFilter.hasGoToFullShadeEvent) {
      return calculateDelayGoToFullShade(paramStackViewState);
    }
    if (this.mAnimationFilter.hasHeadsUpDisappearClickEvent) {
      return 120L;
    }
    long l1 = 0L;
    Iterator localIterator = this.mNewEvents.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (NotificationStackScrollLayout.AnimationEvent)localIterator.next();
      long l2 = 80L;
      switch (((NotificationStackScrollLayout.AnimationEvent)localObject).animationType)
      {
      default: 
        break;
      case 0: 
        l1 = Math.max((2 - Math.max(0, Math.min(2, Math.abs(paramStackViewState.notGoneIndex - paramStackScrollState.getViewStateForView(((NotificationStackScrollLayout.AnimationEvent)localObject).changingView).notGoneIndex) - 1))) * 80L, l1);
        break;
      case 2: 
        l2 = 32L;
      case 1: 
        int j = paramStackViewState.notGoneIndex;
        int i;
        if (((NotificationStackScrollLayout.AnimationEvent)localObject).viewAfterChangingView == null)
        {
          i = 1;
          label191:
          if (i == 0) {
            break label266;
          }
        }
        label266:
        for (localObject = this.mHostLayout.getLastChildNotGone();; localObject = ((NotificationStackScrollLayout.AnimationEvent)localObject).viewAfterChangingView)
        {
          int k = paramStackScrollState.getViewStateForView((View)localObject).notGoneIndex;
          i = j;
          if (j >= k) {
            i = j + 1;
          }
          l1 = Math.max(Math.max(0, Math.min(2, Math.abs(i - k) - 1)) * l2, l1);
          break;
          i = 0;
          break label191;
        }
      }
    }
    return l1;
  }
  
  private long calculateDelayDark(StackViewState paramStackViewState)
  {
    int i;
    if (this.mAnimationFilter.darkAnimationOriginIndex == -1) {
      i = 0;
    }
    for (;;)
    {
      return Math.abs(i - paramStackViewState.notGoneIndex) * 24;
      if (this.mAnimationFilter.darkAnimationOriginIndex == -2) {
        i = this.mHostLayout.getNotGoneChildCount() - 1;
      } else {
        i = this.mAnimationFilter.darkAnimationOriginIndex;
      }
    }
  }
  
  private long calculateDelayGoToFullShade(StackViewState paramStackViewState)
  {
    return (48.0F * (float)Math.pow(paramStackViewState.notGoneIndex, 0.699999988079071D));
  }
  
  private long cancelAnimatorAndGetNewDuration(long paramLong, ValueAnimator paramValueAnimator)
  {
    long l = paramLong;
    if (paramValueAnimator != null)
    {
      l = Math.max(paramValueAnimator.getDuration() - paramValueAnimator.getCurrentPlayTime(), paramLong);
      paramValueAnimator.cancel();
    }
    return l;
  }
  
  private int findLastNotAddedIndex(StackScrollState paramStackScrollState)
  {
    int i = this.mHostLayout.getChildCount() - 1;
    if (i >= 0)
    {
      ExpandableView localExpandableView = (ExpandableView)this.mHostLayout.getChildAt(i);
      StackViewState localStackViewState = paramStackScrollState.getViewStateForView(localExpandableView);
      if ((localStackViewState == null) || (localExpandableView.getVisibility() == 8)) {}
      while (this.mNewAddChildren.contains(localExpandableView))
      {
        i -= 1;
        break;
      }
      return localStackViewState.notGoneIndex;
    }
    return -1;
  }
  
  public static <T> T getChildTag(View paramView, int paramInt)
  {
    return (T)paramView.getTag(paramInt);
  }
  
  public static int getFinalActualHeight(ExpandableView paramExpandableView)
  {
    if (paramExpandableView == null) {
      return 0;
    }
    if ((ValueAnimator)getChildTag(paramExpandableView, 2131951648) == null) {
      return paramExpandableView.getActualHeight();
    }
    return ((Integer)getChildTag(paramExpandableView, 2131951654)).intValue();
  }
  
  public static float getFinalTranslationY(View paramView)
  {
    if (paramView == null) {
      return 0.0F;
    }
    if ((ValueAnimator)getChildTag(paramView, 2131951644) == null) {
      return paramView.getTranslationY();
    }
    return ((Float)getChildTag(paramView, 2131951650)).floatValue();
  }
  
  private AnimatorListenerAdapter getGlobalAnimationFinishedListener()
  {
    if (!this.mAnimationListenerPool.empty()) {
      return (AnimatorListenerAdapter)this.mAnimationListenerPool.pop();
    }
    new AnimatorListenerAdapter()
    {
      private boolean mWasCancelled;
      
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        this.mWasCancelled = true;
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        StackStateAnimator.-get1(StackStateAnimator.this).remove(paramAnonymousAnimator);
        if ((!StackStateAnimator.-get1(StackStateAnimator.this).isEmpty()) || (this.mWasCancelled)) {}
        for (;;)
        {
          StackStateAnimator.-get0(StackStateAnimator.this).push(this);
          return;
          StackStateAnimator.-wrap0(StackStateAnimator.this);
        }
      }
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        this.mWasCancelled = false;
      }
    };
  }
  
  private void onAnimationFinished()
  {
    this.mHostLayout.onChildAnimationFinished();
    Iterator localIterator = this.mChildrenToClearFromOverlay.iterator();
    while (localIterator.hasNext()) {
      removeFromOverlay((View)localIterator.next());
    }
    this.mChildrenToClearFromOverlay.clear();
  }
  
  private void processAnimationEvents(ArrayList<NotificationStackScrollLayout.AnimationEvent> paramArrayList, StackScrollState paramStackScrollState)
  {
    paramArrayList = paramArrayList.iterator();
    while (paramArrayList.hasNext())
    {
      NotificationStackScrollLayout.AnimationEvent localAnimationEvent = (NotificationStackScrollLayout.AnimationEvent)paramArrayList.next();
      final ExpandableView localExpandableView = (ExpandableView)localAnimationEvent.changingView;
      StackViewState localStackViewState;
      if (localAnimationEvent.animationType == 0)
      {
        localStackViewState = paramStackScrollState.getViewStateForView(localExpandableView);
        if (localStackViewState != null)
        {
          paramStackScrollState.applyState(localExpandableView, localStackViewState);
          this.mNewAddChildren.add(localExpandableView);
        }
      }
      else
      {
        label285:
        do
        {
          do
          {
            for (;;)
            {
              this.mNewEvents.add(localAnimationEvent);
              break;
              if (localAnimationEvent.animationType == 1)
              {
                if (localExpandableView.getVisibility() == 8)
                {
                  removeFromOverlay(localExpandableView);
                  break;
                }
                localStackViewState = paramStackScrollState.getViewStateForView(localAnimationEvent.viewAfterChangingView);
                i = localExpandableView.getActualHeight();
                float f = -1.0F;
                if (localStackViewState != null) {
                  f = Math.max(Math.min((localStackViewState.yTranslation - (localExpandableView.getTranslationY() + i / 2.0F)) * 2.0F / i, 1.0F), -1.0F);
                }
                localExpandableView.performRemoveAnimation(464L, f, new Runnable()
                {
                  public void run()
                  {
                    StackStateAnimator.removeFromOverlay(localExpandableView);
                  }
                });
                continue;
              }
              if (localAnimationEvent.animationType == 2)
              {
                this.mHostLayout.getOverlay().remove(localExpandableView);
                if ((Math.abs(localExpandableView.getTranslation()) == localExpandableView.getWidth()) && (localExpandableView.getTransientContainer() != null)) {
                  localExpandableView.getTransientContainer().removeTransientView(localExpandableView);
                }
              }
              else
              {
                if (localAnimationEvent.animationType != 13) {
                  break label285;
                }
                ((ExpandableNotificationRow)localAnimationEvent.changingView).prepareExpansionChanged(paramStackScrollState);
              }
            }
            if (localAnimationEvent.animationType == 14)
            {
              localStackViewState = paramStackScrollState.getViewStateForView(localExpandableView);
              this.mTmpState.copyFrom(localStackViewState);
              if (localAnimationEvent.headsUpFromBottom) {}
              for (this.mTmpState.yTranslation = this.mHeadsUpAppearHeightBottom;; this.mTmpState.yTranslation = (-this.mTmpState.height))
              {
                this.mHeadsUpAppearChildren.add(localExpandableView);
                paramStackScrollState.applyState(localExpandableView, this.mTmpState);
                break;
              }
            }
          } while ((localAnimationEvent.animationType != 15) && (localAnimationEvent.animationType != 16));
          this.mHeadsUpDisappearChildren.add(localExpandableView);
        } while (localExpandableView.getParent() != null);
        this.mHostLayout.getOverlay().add(localExpandableView);
        this.mTmpState.initFrom(localExpandableView);
        this.mTmpState.yTranslation = (-localExpandableView.getActualHeight());
        this.mAnimationFilter.animateY = true;
        localStackViewState = this.mTmpState;
        if (localAnimationEvent.animationType == 16) {}
        for (int i = 120;; i = 0)
        {
          startViewAnimations(localExpandableView, localStackViewState, i, 230L);
          this.mChildrenToClearFromOverlay.add(localExpandableView);
          break;
        }
      }
    }
  }
  
  public static void removeFromOverlay(View paramView)
  {
    ViewGroup localViewGroup = (ViewGroup)paramView.getParent();
    if (localViewGroup != null) {
      localViewGroup.removeView(paramView);
    }
  }
  
  private void startAlphaAnimation(final View paramView, ViewState paramViewState, long paramLong1, long paramLong2)
  {
    Object localObject = (Float)getChildTag(paramView, 2131951658);
    Float localFloat = (Float)getChildTag(paramView, 2131951652);
    final float f1 = paramViewState.alpha;
    if ((localFloat != null) && (localFloat.floatValue() == f1)) {
      return;
    }
    paramViewState = (ObjectAnimator)getChildTag(paramView, 2131951646);
    if (!this.mAnimationFilter.animateAlpha)
    {
      if (paramViewState != null)
      {
        PropertyValuesHolder[] arrayOfPropertyValuesHolder = paramViewState.getValues();
        float f2 = localFloat.floatValue();
        f2 = ((Float)localObject).floatValue() + (f1 - f2);
        arrayOfPropertyValuesHolder[0].setFloatValues(new float[] { f2, f1 });
        paramView.setTag(2131951658, Float.valueOf(f2));
        paramView.setTag(2131951652, Float.valueOf(f1));
        paramViewState.setCurrentPlayTime(paramViewState.getCurrentPlayTime());
        return;
      }
      paramView.setAlpha(f1);
      if (f1 == 0.0F) {
        paramView.setVisibility(4);
      }
    }
    localObject = ObjectAnimator.ofFloat(paramView, View.ALPHA, new float[] { paramView.getAlpha(), f1 });
    ((ObjectAnimator)localObject).setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
    paramView.setLayerType(2, null);
    ((ObjectAnimator)localObject).addListener(new AnimatorListenerAdapter()
    {
      public boolean mWasCancelled;
      
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        this.mWasCancelled = true;
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        paramView.setLayerType(0, null);
        if ((f1 != 0.0F) || (this.mWasCancelled)) {}
        for (;;)
        {
          paramView.setTag(2131951646, null);
          paramView.setTag(2131951658, null);
          paramView.setTag(2131951652, null);
          return;
          paramView.setVisibility(4);
        }
      }
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        this.mWasCancelled = false;
      }
    });
    ((ObjectAnimator)localObject).setDuration(cancelAnimatorAndGetNewDuration(paramLong1, paramViewState));
    if ((paramLong2 > 0L) && ((paramViewState == null) || (paramViewState.getAnimatedFraction() == 0.0F))) {
      ((ObjectAnimator)localObject).setStartDelay(paramLong2);
    }
    ((ObjectAnimator)localObject).addListener(getGlobalAnimationFinishedListener());
    startAnimator((ValueAnimator)localObject);
    paramView.setTag(2131951646, localObject);
    paramView.setTag(2131951658, Float.valueOf(paramView.getAlpha()));
    paramView.setTag(2131951652, Float.valueOf(f1));
  }
  
  private void startAnimator(ValueAnimator paramValueAnimator)
  {
    this.mAnimatorSet.add(paramValueAnimator);
    paramValueAnimator.start();
  }
  
  private void startHeightAnimation(final ExpandableView paramExpandableView, StackViewState paramStackViewState, long paramLong1, long paramLong2)
  {
    Object localObject = (Integer)getChildTag(paramExpandableView, 2131951660);
    Integer localInteger = (Integer)getChildTag(paramExpandableView, 2131951654);
    int i = paramStackViewState.height;
    if ((localInteger != null) && (localInteger.intValue() == i)) {
      return;
    }
    paramStackViewState = (ValueAnimator)getChildTag(paramExpandableView, 2131951648);
    if (!this.mAnimationFilter.animateHeight)
    {
      if (paramStackViewState != null)
      {
        PropertyValuesHolder[] arrayOfPropertyValuesHolder = paramStackViewState.getValues();
        int j = localInteger.intValue();
        j = ((Integer)localObject).intValue() + (i - j);
        arrayOfPropertyValuesHolder[0].setIntValues(new int[] { j, i });
        paramExpandableView.setTag(2131951660, Integer.valueOf(j));
        paramExpandableView.setTag(2131951654, Integer.valueOf(i));
        paramStackViewState.setCurrentPlayTime(paramStackViewState.getCurrentPlayTime());
        return;
      }
      paramExpandableView.setActualHeight(i, false);
      return;
    }
    localObject = ValueAnimator.ofInt(new int[] { paramExpandableView.getActualHeight(), i });
    ((ValueAnimator)localObject).addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        paramExpandableView.setActualHeight(((Integer)paramAnonymousValueAnimator.getAnimatedValue()).intValue(), false);
      }
    });
    ((ValueAnimator)localObject).setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
    ((ValueAnimator)localObject).setDuration(cancelAnimatorAndGetNewDuration(paramLong1, paramStackViewState));
    if ((paramLong2 > 0L) && ((paramStackViewState == null) || (paramStackViewState.getAnimatedFraction() == 0.0F))) {
      ((ValueAnimator)localObject).setStartDelay(paramLong2);
    }
    ((ValueAnimator)localObject).addListener(getGlobalAnimationFinishedListener());
    ((ValueAnimator)localObject).addListener(new AnimatorListenerAdapter()
    {
      boolean mWasCancelled;
      
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        this.mWasCancelled = true;
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        paramExpandableView.setTag(2131951648, null);
        paramExpandableView.setTag(2131951660, null);
        paramExpandableView.setTag(2131951654, null);
        paramExpandableView.setActualHeightAnimating(false);
        if ((!this.mWasCancelled) && ((paramExpandableView instanceof ExpandableNotificationRow))) {
          ((ExpandableNotificationRow)paramExpandableView).setGroupExpansionChanging(false);
        }
      }
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        this.mWasCancelled = false;
      }
    });
    startAnimator((ValueAnimator)localObject);
    paramExpandableView.setTag(2131951648, localObject);
    paramExpandableView.setTag(2131951660, Integer.valueOf(paramExpandableView.getActualHeight()));
    paramExpandableView.setTag(2131951654, Integer.valueOf(i));
    paramExpandableView.setActualHeightAnimating(true);
  }
  
  private void startInsetAnimation(final ExpandableView paramExpandableView, StackViewState paramStackViewState, long paramLong1, long paramLong2)
  {
    Object localObject = (Integer)getChildTag(paramExpandableView, 2131951659);
    Integer localInteger = (Integer)getChildTag(paramExpandableView, 2131951653);
    int i = paramStackViewState.clipTopAmount;
    if ((localInteger != null) && (localInteger.intValue() == i)) {
      return;
    }
    paramStackViewState = (ValueAnimator)getChildTag(paramExpandableView, 2131951647);
    if (!this.mAnimationFilter.animateTopInset)
    {
      if (paramStackViewState != null)
      {
        PropertyValuesHolder[] arrayOfPropertyValuesHolder = paramStackViewState.getValues();
        int j = localInteger.intValue();
        j = ((Integer)localObject).intValue() + (i - j);
        arrayOfPropertyValuesHolder[0].setIntValues(new int[] { j, i });
        paramExpandableView.setTag(2131951659, Integer.valueOf(j));
        paramExpandableView.setTag(2131951653, Integer.valueOf(i));
        paramStackViewState.setCurrentPlayTime(paramStackViewState.getCurrentPlayTime());
        return;
      }
      paramExpandableView.setClipTopAmount(i);
      return;
    }
    localObject = ValueAnimator.ofInt(new int[] { paramExpandableView.getClipTopAmount(), i });
    ((ValueAnimator)localObject).addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        paramExpandableView.setClipTopAmount(((Integer)paramAnonymousValueAnimator.getAnimatedValue()).intValue());
      }
    });
    ((ValueAnimator)localObject).setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
    ((ValueAnimator)localObject).setDuration(cancelAnimatorAndGetNewDuration(paramLong1, paramStackViewState));
    if ((paramLong2 > 0L) && ((paramStackViewState == null) || (paramStackViewState.getAnimatedFraction() == 0.0F))) {
      ((ValueAnimator)localObject).setStartDelay(paramLong2);
    }
    ((ValueAnimator)localObject).addListener(getGlobalAnimationFinishedListener());
    ((ValueAnimator)localObject).addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        paramExpandableView.setTag(2131951647, null);
        paramExpandableView.setTag(2131951659, null);
        paramExpandableView.setTag(2131951653, null);
      }
    });
    startAnimator((ValueAnimator)localObject);
    paramExpandableView.setTag(2131951647, localObject);
    paramExpandableView.setTag(2131951659, Integer.valueOf(paramExpandableView.getClipTopAmount()));
    paramExpandableView.setTag(2131951653, Integer.valueOf(i));
  }
  
  private void startShadowAlphaAnimation(final ExpandableView paramExpandableView, StackViewState paramStackViewState, long paramLong1, long paramLong2)
  {
    Object localObject = (Float)getChildTag(paramExpandableView, 2131951661);
    Float localFloat = (Float)getChildTag(paramExpandableView, 2131951655);
    float f1 = paramStackViewState.shadowAlpha;
    if ((localFloat != null) && (localFloat.floatValue() == f1)) {
      return;
    }
    paramStackViewState = (ValueAnimator)getChildTag(paramExpandableView, 2131951649);
    if (!this.mAnimationFilter.animateShadowAlpha)
    {
      if (paramStackViewState != null)
      {
        PropertyValuesHolder[] arrayOfPropertyValuesHolder = paramStackViewState.getValues();
        float f2 = localFloat.floatValue();
        f2 = ((Float)localObject).floatValue() + (f1 - f2);
        arrayOfPropertyValuesHolder[0].setFloatValues(new float[] { f2, f1 });
        paramExpandableView.setTag(2131951661, Float.valueOf(f2));
        paramExpandableView.setTag(2131951655, Float.valueOf(f1));
        paramStackViewState.setCurrentPlayTime(paramStackViewState.getCurrentPlayTime());
        return;
      }
      paramExpandableView.setShadowAlpha(f1);
      return;
    }
    localObject = ValueAnimator.ofFloat(new float[] { paramExpandableView.getShadowAlpha(), f1 });
    ((ValueAnimator)localObject).addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        paramExpandableView.setShadowAlpha(((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
      }
    });
    ((ValueAnimator)localObject).setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
    ((ValueAnimator)localObject).setDuration(cancelAnimatorAndGetNewDuration(paramLong1, paramStackViewState));
    if ((paramLong2 > 0L) && ((paramStackViewState == null) || (paramStackViewState.getAnimatedFraction() == 0.0F))) {
      ((ValueAnimator)localObject).setStartDelay(paramLong2);
    }
    ((ValueAnimator)localObject).addListener(getGlobalAnimationFinishedListener());
    ((ValueAnimator)localObject).addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        paramExpandableView.setTag(2131951649, null);
        paramExpandableView.setTag(2131951661, null);
        paramExpandableView.setTag(2131951655, null);
      }
    });
    startAnimator((ValueAnimator)localObject);
    paramExpandableView.setTag(2131951649, localObject);
    paramExpandableView.setTag(2131951661, Float.valueOf(paramExpandableView.getShadowAlpha()));
    paramExpandableView.setTag(2131951655, Float.valueOf(f1));
  }
  
  private void startYTranslationAnimation(final View paramView, ViewState paramViewState, long paramLong1, long paramLong2)
  {
    Object localObject = (Float)getChildTag(paramView, 2131951656);
    Float localFloat = (Float)getChildTag(paramView, 2131951650);
    float f1 = paramViewState.yTranslation;
    if ((localFloat != null) && (localFloat.floatValue() == f1)) {
      return;
    }
    ObjectAnimator localObjectAnimator = (ObjectAnimator)getChildTag(paramView, 2131951644);
    if (!this.mAnimationFilter.animateY)
    {
      if (localObjectAnimator != null)
      {
        paramViewState = localObjectAnimator.getValues();
        float f2 = localFloat.floatValue();
        f2 = ((Float)localObject).floatValue() + (f1 - f2);
        paramViewState[0].setFloatValues(new float[] { f2, f1 });
        paramView.setTag(2131951656, Float.valueOf(f2));
        paramView.setTag(2131951650, Float.valueOf(f1));
        localObjectAnimator.setCurrentPlayTime(localObjectAnimator.getCurrentPlayTime());
        return;
      }
      paramView.setTranslationY(f1);
      return;
    }
    localObject = ObjectAnimator.ofFloat(paramView, View.TRANSLATION_Y, new float[] { paramView.getTranslationY(), f1 });
    if (this.mHeadsUpAppearChildren.contains(paramView)) {}
    for (paramViewState = this.mHeadsUpAppearInterpolator;; paramViewState = Interpolators.FAST_OUT_SLOW_IN)
    {
      ((ObjectAnimator)localObject).setInterpolator(paramViewState);
      ((ObjectAnimator)localObject).setDuration(cancelAnimatorAndGetNewDuration(paramLong1, localObjectAnimator));
      if ((paramLong2 > 0L) && ((localObjectAnimator == null) || (localObjectAnimator.getAnimatedFraction() == 0.0F))) {
        ((ObjectAnimator)localObject).setStartDelay(paramLong2);
      }
      ((ObjectAnimator)localObject).addListener(getGlobalAnimationFinishedListener());
      ((ObjectAnimator)localObject).addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          HeadsUpManager.setIsClickedNotification(paramView, false);
          paramView.setTag(2131951644, null);
          paramView.setTag(2131951656, null);
          paramView.setTag(2131951650, null);
          if (this.val$isHeadsUpDisappear) {
            ((ExpandableNotificationRow)paramView).setHeadsupDisappearRunning(false);
          }
        }
      });
      startAnimator((ValueAnimator)localObject);
      paramView.setTag(2131951644, localObject);
      paramView.setTag(2131951656, Float.valueOf(paramView.getTranslationY()));
      paramView.setTag(2131951650, Float.valueOf(f1));
      return;
    }
  }
  
  private void startZTranslationAnimation(final View paramView, ViewState paramViewState, long paramLong1, long paramLong2)
  {
    Object localObject = (Float)getChildTag(paramView, 2131951657);
    Float localFloat = (Float)getChildTag(paramView, 2131951651);
    float f1 = paramViewState.zTranslation;
    if ((localFloat != null) && (localFloat.floatValue() == f1)) {
      return;
    }
    paramViewState = (ObjectAnimator)getChildTag(paramView, 2131951645);
    if (!this.mAnimationFilter.animateZ)
    {
      if (paramViewState != null)
      {
        PropertyValuesHolder[] arrayOfPropertyValuesHolder = paramViewState.getValues();
        float f2 = localFloat.floatValue();
        f2 = ((Float)localObject).floatValue() + (f1 - f2);
        arrayOfPropertyValuesHolder[0].setFloatValues(new float[] { f2, f1 });
        paramView.setTag(2131951657, Float.valueOf(f2));
        paramView.setTag(2131951651, Float.valueOf(f1));
        paramViewState.setCurrentPlayTime(paramViewState.getCurrentPlayTime());
        return;
      }
      paramView.setTranslationZ(f1);
    }
    localObject = ObjectAnimator.ofFloat(paramView, View.TRANSLATION_Z, new float[] { paramView.getTranslationZ(), f1 });
    ((ObjectAnimator)localObject).setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
    ((ObjectAnimator)localObject).setDuration(cancelAnimatorAndGetNewDuration(paramLong1, paramViewState));
    if ((paramLong2 > 0L) && ((paramViewState == null) || (paramViewState.getAnimatedFraction() == 0.0F))) {
      ((ObjectAnimator)localObject).setStartDelay(paramLong2);
    }
    ((ObjectAnimator)localObject).addListener(getGlobalAnimationFinishedListener());
    ((ObjectAnimator)localObject).addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        paramView.setTag(2131951645, null);
        paramView.setTag(2131951657, null);
        paramView.setTag(2131951651, null);
      }
    });
    startAnimator((ValueAnimator)localObject);
    paramView.setTag(2131951645, localObject);
    paramView.setTag(2131951657, Float.valueOf(paramView.getTranslationZ()));
    paramView.setTag(2131951651, Float.valueOf(f1));
  }
  
  public void animateOverScrollToAmount(float paramFloat, final boolean paramBoolean1, final boolean paramBoolean2)
  {
    float f = this.mHostLayout.getCurrentOverScrollAmount(paramBoolean1);
    if (paramFloat == f) {
      return;
    }
    cancelOverScrollAnimators(paramBoolean1);
    ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { f, paramFloat });
    localValueAnimator.setDuration(360L);
    localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        float f = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        StackStateAnimator.this.mHostLayout.setOverScrollAmount(f, paramBoolean1, false, false, paramBoolean2);
      }
    });
    localValueAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
    localValueAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if (paramBoolean1)
        {
          StackStateAnimator.-set1(StackStateAnimator.this, null);
          return;
        }
        StackStateAnimator.-set0(StackStateAnimator.this, null);
      }
    });
    localValueAnimator.start();
    if (paramBoolean1)
    {
      this.mTopOverScrollAnimator = localValueAnimator;
      return;
    }
    this.mBottomOverScrollAnimator = localValueAnimator;
  }
  
  public void cancelOverScrollAnimators(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (ValueAnimator localValueAnimator = this.mTopOverScrollAnimator;; localValueAnimator = this.mBottomOverScrollAnimator)
    {
      if (localValueAnimator != null) {
        localValueAnimator.cancel();
      }
      return;
    }
  }
  
  public boolean isOverScrollAnimatorsRunning(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (ValueAnimator localValueAnimator = this.mTopOverScrollAnimator; localValueAnimator != null; localValueAnimator = this.mBottomOverScrollAnimator) {
      return localValueAnimator.isRunning();
    }
    return false;
  }
  
  public boolean isRunning()
  {
    return !this.mAnimatorSet.isEmpty();
  }
  
  public void setHeadsUpAppearHeightBottom(int paramInt)
  {
    this.mHeadsUpAppearHeightBottom = paramInt;
  }
  
  public void setShadeExpanded(boolean paramBoolean)
  {
    this.mShadeExpanded = paramBoolean;
  }
  
  public void startAnimationForEvents(ArrayList<NotificationStackScrollLayout.AnimationEvent> paramArrayList, StackScrollState paramStackScrollState, long paramLong)
  {
    processAnimationEvents(paramArrayList, paramStackScrollState);
    int j = this.mHostLayout.getChildCount();
    this.mAnimationFilter.applyCombination(this.mNewEvents);
    this.mCurrentAdditionalDelay = paramLong;
    this.mCurrentLength = NotificationStackScrollLayout.AnimationEvent.combineLength(this.mNewEvents);
    this.mCurrentLastNotAddedIndex = findLastNotAddedIndex(paramStackScrollState);
    int i = 0;
    if (i < j)
    {
      paramArrayList = (ExpandableView)this.mHostLayout.getChildAt(i);
      StackViewState localStackViewState = paramStackScrollState.getViewStateForView(paramArrayList);
      if ((localStackViewState == null) || (paramArrayList.getVisibility() == 8)) {}
      for (;;)
      {
        i += 1;
        break;
        if (!applyWithoutAnimation(paramArrayList, localStackViewState, paramStackScrollState)) {
          startStackAnimations(paramArrayList, localStackViewState, paramStackScrollState, i, -1L);
        }
      }
    }
    if (!isRunning()) {
      onAnimationFinished();
    }
    this.mHeadsUpAppearChildren.clear();
    this.mHeadsUpDisappearChildren.clear();
    this.mNewEvents.clear();
    this.mNewAddChildren.clear();
  }
  
  public void startStackAnimations(ExpandableView paramExpandableView, StackViewState paramStackViewState, StackScrollState paramStackScrollState, int paramInt, long paramLong)
  {
    boolean bool1 = this.mNewAddChildren.contains(paramExpandableView);
    long l2 = this.mCurrentLength;
    long l1 = l2;
    if (bool1)
    {
      l1 = l2;
      if (this.mAnimationFilter.hasGoToFullShadeEvent)
      {
        paramExpandableView.setTranslationY(paramExpandableView.getTranslationY() + this.mGoToFullShadeAppearingTranslation);
        l1 = 514L + (100.0F * (float)Math.pow(paramStackViewState.notGoneIndex - this.mCurrentLastNotAddedIndex, 0.699999988079071D));
      }
    }
    int i;
    int j;
    label112:
    int m;
    label127:
    int k;
    label141:
    label156:
    int i1;
    label170:
    int n;
    label184:
    boolean bool2;
    if (paramExpandableView.getTranslationY() != paramStackViewState.yTranslation)
    {
      i = 1;
      if (paramExpandableView.getTranslationZ() == paramStackViewState.zTranslation) {
        break label395;
      }
      j = 1;
      if (paramStackViewState.alpha == paramExpandableView.getAlpha()) {
        break label401;
      }
      m = 1;
      if (paramStackViewState.height == paramExpandableView.getActualHeight()) {
        break label407;
      }
      k = 1;
      if (paramStackViewState.shadowAlpha == paramExpandableView.getShadowAlpha()) {
        break label413;
      }
      paramInt = 1;
      if (paramStackViewState.dark == paramExpandableView.isDark()) {
        break label419;
      }
      i1 = 1;
      if (paramStackViewState.clipTopAmount == paramExpandableView.getClipTopAmount()) {
        break label425;
      }
      n = 1;
      bool2 = this.mAnimationFilter.hasDelays;
      if ((i != 0) || (j != 0) || (m != 0) || (k != 0) || (n != 0) || (i1 != 0)) {
        break label431;
      }
      i = paramInt;
      label227:
      l2 = 0L;
      if (paramLong == -1L) {
        break label437;
      }
      label239:
      startViewAnimations(paramExpandableView, paramStackViewState, paramLong, l1);
      if (k == 0) {
        break label472;
      }
      startHeightAnimation(paramExpandableView, paramStackViewState, l1, paramLong);
      label264:
      if (paramInt == 0) {
        break label483;
      }
      startShadowAlphaAnimation(paramExpandableView, paramStackViewState, l1, paramLong);
      label279:
      if (n == 0) {
        break label494;
      }
      startInsetAnimation(paramExpandableView, paramStackViewState, l1, paramLong);
    }
    for (;;)
    {
      paramExpandableView.setDimmed(paramStackViewState.dimmed, this.mAnimationFilter.animateDimmed);
      paramExpandableView.setBelowSpeedBump(paramStackViewState.belowSpeedBump);
      paramExpandableView.setHideSensitive(paramStackViewState.hideSensitive, this.mAnimationFilter.animateHideSensitive, paramLong, l1);
      paramExpandableView.setDark(paramStackViewState.dark, this.mAnimationFilter.animateDark, paramLong);
      if (bool1) {
        paramExpandableView.performAddAnimation(paramLong, this.mCurrentLength);
      }
      if ((paramExpandableView instanceof ExpandableNotificationRow)) {
        ((ExpandableNotificationRow)paramExpandableView).startChildAnimation(paramStackScrollState, this, paramLong, l1);
      }
      return;
      i = 0;
      break;
      label395:
      j = 0;
      break label112;
      label401:
      m = 0;
      break label127;
      label407:
      k = 0;
      break label141;
      label413:
      paramInt = 0;
      break label156;
      label419:
      i1 = 0;
      break label170;
      label425:
      n = 0;
      break label184;
      label431:
      i = 1;
      break label227;
      label437:
      if ((!bool2) || (i == 0))
      {
        paramLong = l2;
        if (!bool1) {
          break label239;
        }
      }
      paramLong = this.mCurrentAdditionalDelay + calculateChildAnimationDelay(paramStackViewState, paramStackScrollState);
      break label239;
      label472:
      abortAnimation(paramExpandableView, 2131951648);
      break label264;
      label483:
      abortAnimation(paramExpandableView, 2131951649);
      break label279;
      label494:
      abortAnimation(paramExpandableView, 2131951647);
    }
  }
  
  public void startViewAnimations(View paramView, ViewState paramViewState, long paramLong1, long paramLong2)
  {
    int i;
    label44:
    int j;
    label59:
    int k;
    label74:
    label93:
    int m;
    if (paramView.getVisibility() == 0)
    {
      i = 1;
      float f = paramViewState.alpha;
      if ((i == 0) && ((f != 0.0F) || (paramView.getAlpha() != 0.0F)) && (!paramViewState.gone)) {
        break label182;
      }
      if (paramView.getTranslationY() == paramViewState.yTranslation) {
        break label197;
      }
      j = 1;
      if (paramView.getTranslationZ() == paramViewState.zTranslation) {
        break label203;
      }
      k = 1;
      f = paramView.getAlpha();
      if (paramViewState.alpha == f) {
        break label209;
      }
      i = 1;
      m = i;
      if ((paramView instanceof ExpandableView))
      {
        if (!((ExpandableView)paramView).willBeGone()) {
          break label215;
        }
        m = 0;
        label117:
        m = i & m;
      }
      if (j == 0) {
        break label221;
      }
      startYTranslationAnimation(paramView, paramViewState, paramLong2, paramLong1);
      label138:
      if (k == 0) {
        break label231;
      }
      startZTranslationAnimation(paramView, paramViewState, paramLong2, paramLong1);
    }
    for (;;)
    {
      if ((m == 0) || (paramView.getTranslationX() != 0.0F)) {
        break label242;
      }
      startAlphaAnimation(paramView, paramViewState, paramLong2, paramLong1);
      return;
      i = 0;
      break;
      label182:
      if (paramViewState.hidden) {
        break label44;
      }
      paramView.setVisibility(0);
      break label44;
      label197:
      j = 0;
      break label59;
      label203:
      k = 0;
      break label74;
      label209:
      i = 0;
      break label93;
      label215:
      m = 1;
      break label117;
      label221:
      abortAnimation(paramView, 2131951644);
      break label138;
      label231:
      abortAnimation(paramView, 2131951645);
    }
    label242:
    abortAnimation(paramView, 2131951646);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\stack\StackStateAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */