package com.android.systemui.recents.views;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewPropertyAnimator;
import com.android.systemui.Interpolators;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsActivity;
import com.android.systemui.recents.events.activity.ConfigurationChangedEvent;
import com.android.systemui.recents.events.activity.DismissRecentsToHomeAnimationStarted;
import com.android.systemui.recents.events.activity.EnterRecentsWindowAnimationCompletedEvent;
import com.android.systemui.recents.events.activity.MultiWindowStateChangedEvent;
import com.android.systemui.recents.events.ui.DismissAllTaskViewsEvent;
import com.android.systemui.recents.events.ui.dragndrop.DragEndCancelledEvent;
import com.android.systemui.recents.events.ui.dragndrop.DragEndEvent;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.model.TaskStack;
import com.android.systemui.recents.model.TaskStack.DockState;

public class SystemBarScrimViews
{
  private Context mContext;
  private boolean mHasDockedTasks;
  private boolean mHasNavBarScrim;
  private boolean mHasTransposedNavBar;
  private int mNavBarScrimEnterDuration;
  private View mNavBarScrimView;
  private boolean mShouldAnimateNavBarScrim;
  
  public SystemBarScrimViews(RecentsActivity paramRecentsActivity)
  {
    this.mContext = paramRecentsActivity;
    this.mNavBarScrimView = paramRecentsActivity.findViewById(2131952185);
    this.mNavBarScrimView.forceHasOverlappingRendering(false);
    this.mNavBarScrimEnterDuration = paramRecentsActivity.getResources().getInteger(2131623995);
    this.mHasNavBarScrim = Recents.getSystemServices().hasTransposedNavigationBar();
    this.mHasDockedTasks = Recents.getSystemServices().hasDockedTask();
  }
  
  private void animateNavBarScrimVisibility(boolean paramBoolean, AnimationProps paramAnimationProps)
  {
    int i = 0;
    if (paramBoolean)
    {
      this.mNavBarScrimView.setVisibility(0);
      this.mNavBarScrimView.setTranslationY(this.mNavBarScrimView.getMeasuredHeight());
    }
    while (paramAnimationProps != AnimationProps.IMMEDIATE)
    {
      this.mNavBarScrimView.animate().translationY(i).setDuration(paramAnimationProps.getDuration(6)).setInterpolator(paramAnimationProps.getInterpolator(6)).start();
      return;
      i = this.mNavBarScrimView.getMeasuredHeight();
    }
    this.mNavBarScrimView.setTranslationY(i);
  }
  
  private void animateScrimToCurrentNavBarState(boolean paramBoolean)
  {
    paramBoolean = isNavBarScrimRequired(paramBoolean);
    if (this.mHasNavBarScrim != paramBoolean) {
      if (!paramBoolean) {
        break label38;
      }
    }
    label38:
    for (AnimationProps localAnimationProps = createBoundsAnimation(150);; localAnimationProps = AnimationProps.IMMEDIATE)
    {
      animateNavBarScrimVisibility(paramBoolean, localAnimationProps);
      this.mHasNavBarScrim = paramBoolean;
      return;
    }
  }
  
  private AnimationProps createBoundsAnimation(int paramInt)
  {
    return new AnimationProps().setDuration(6, paramInt).setInterpolator(6, Interpolators.FAST_OUT_SLOW_IN);
  }
  
  private boolean isNavBarScrimRequired(boolean paramBoolean)
  {
    if ((!paramBoolean) || (this.mHasTransposedNavBar)) {}
    while (this.mHasDockedTasks) {
      return false;
    }
    return true;
  }
  
  private void prepareEnterRecentsAnimation(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mHasNavBarScrim = paramBoolean1;
    this.mShouldAnimateNavBarScrim = paramBoolean2;
    View localView = this.mNavBarScrimView;
    if ((!this.mHasNavBarScrim) || (this.mShouldAnimateNavBarScrim)) {}
    for (int i = 4;; i = 0)
    {
      localView.setVisibility(i);
      return;
    }
  }
  
  public final void onBusEvent(ConfigurationChangedEvent paramConfigurationChangedEvent)
  {
    if (paramConfigurationChangedEvent.fromDeviceOrientationChange) {
      this.mHasNavBarScrim = Recents.getSystemServices().hasTransposedNavigationBar();
    }
    animateScrimToCurrentNavBarState(paramConfigurationChangedEvent.hasStackTasks);
  }
  
  public final void onBusEvent(DismissRecentsToHomeAnimationStarted paramDismissRecentsToHomeAnimationStarted)
  {
    if (this.mHasNavBarScrim) {
      animateNavBarScrimVisibility(false, createBoundsAnimation(200));
    }
  }
  
  public final void onBusEvent(EnterRecentsWindowAnimationCompletedEvent paramEnterRecentsWindowAnimationCompletedEvent)
  {
    if (this.mHasNavBarScrim) {
      if (!this.mShouldAnimateNavBarScrim) {
        break label46;
      }
    }
    label46:
    for (paramEnterRecentsWindowAnimationCompletedEvent = new AnimationProps().setDuration(6, this.mNavBarScrimEnterDuration).setInterpolator(6, Interpolators.DECELERATE_QUINT);; paramEnterRecentsWindowAnimationCompletedEvent = AnimationProps.IMMEDIATE)
    {
      animateNavBarScrimVisibility(true, paramEnterRecentsWindowAnimationCompletedEvent);
      return;
    }
  }
  
  public final void onBusEvent(MultiWindowStateChangedEvent paramMultiWindowStateChangedEvent)
  {
    boolean bool = false;
    this.mHasDockedTasks = paramMultiWindowStateChangedEvent.inMultiWindow;
    if (paramMultiWindowStateChangedEvent.stack.getStackTaskCount() > 0) {
      bool = true;
    }
    animateScrimToCurrentNavBarState(bool);
  }
  
  public final void onBusEvent(DismissAllTaskViewsEvent paramDismissAllTaskViewsEvent)
  {
    if (this.mHasNavBarScrim) {
      animateNavBarScrimVisibility(false, createBoundsAnimation(200));
    }
  }
  
  public final void onBusEvent(DragEndCancelledEvent paramDragEndCancelledEvent)
  {
    boolean bool = false;
    if (paramDragEndCancelledEvent.stack.getStackTaskCount() > 0) {
      bool = true;
    }
    animateScrimToCurrentNavBarState(bool);
  }
  
  public final void onBusEvent(DragEndEvent paramDragEndEvent)
  {
    if ((paramDragEndEvent.dropTarget instanceof TaskStack.DockState)) {
      animateScrimToCurrentNavBarState(false);
    }
  }
  
  public void updateNavBarScrim(boolean paramBoolean1, boolean paramBoolean2, AnimationProps paramAnimationProps)
  {
    prepareEnterRecentsAnimation(isNavBarScrimRequired(paramBoolean2), paramBoolean1);
    if ((paramBoolean1) && (paramAnimationProps != null)) {
      animateNavBarScrimVisibility(true, paramAnimationProps);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\SystemBarScrimViews.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */