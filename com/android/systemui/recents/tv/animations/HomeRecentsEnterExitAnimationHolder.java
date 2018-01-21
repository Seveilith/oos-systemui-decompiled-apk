package com.android.systemui.recents.tv.animations;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewPropertyAnimator;
import com.android.systemui.Interpolators;
import com.android.systemui.recents.events.activity.DismissRecentsToHomeAnimationStarted;
import com.android.systemui.recents.misc.ReferenceCountedTrigger;
import com.android.systemui.recents.tv.views.TaskCardView;
import com.android.systemui.recents.tv.views.TaskStackHorizontalGridView;

public class HomeRecentsEnterExitAnimationHolder
{
  private Context mContext;
  private long mDelay;
  private float mDimAlpha;
  private int mDuration;
  private TaskStackHorizontalGridView mGridView;
  private int mTranslationX;
  
  public HomeRecentsEnterExitAnimationHolder(Context paramContext, TaskStackHorizontalGridView paramTaskStackHorizontalGridView)
  {
    this.mContext = paramContext;
    this.mGridView = paramTaskStackHorizontalGridView;
    this.mDimAlpha = this.mContext.getResources().getFloat(2131755625);
    this.mTranslationX = this.mContext.getResources().getDimensionPixelSize(2131755668);
    this.mDelay = this.mContext.getResources().getInteger(2131624029);
    this.mDuration = this.mContext.getResources().getInteger(2131624028);
  }
  
  public void setEnterFromAppStartingAnimationValues(boolean paramBoolean)
  {
    int i = 0;
    if (i < this.mGridView.getChildCount())
    {
      TaskCardView localTaskCardView = (TaskCardView)this.mGridView.getChildAt(i);
      localTaskCardView.setTranslationX(0.0F);
      label41:
      View localView;
      if (paramBoolean)
      {
        f = this.mDimAlpha;
        localTaskCardView.setAlpha(f);
        localView = localTaskCardView.getInfoFieldView();
        if (!paramBoolean) {
          break label99;
        }
      }
      label99:
      for (float f = 0.0F;; f = 1.0F)
      {
        localView.setAlpha(f);
        if ((paramBoolean) && (localTaskCardView.hasFocus())) {
          localTaskCardView.getViewFocusAnimator().changeSize(false);
        }
        i += 1;
        break;
        f = 1.0F;
        break label41;
      }
    }
  }
  
  public void setEnterFromHomeStartingAnimationValues(boolean paramBoolean)
  {
    int i = 0;
    if (i < this.mGridView.getChildCount())
    {
      TaskCardView localTaskCardView = (TaskCardView)this.mGridView.getChildAt(i);
      localTaskCardView.setTranslationX(0.0F);
      localTaskCardView.setAlpha(0.0F);
      View localView = localTaskCardView.getInfoFieldView();
      if (paramBoolean) {}
      for (float f = 0.0F;; f = 1.0F)
      {
        localView.setAlpha(f);
        if ((paramBoolean) && (localTaskCardView.hasFocus())) {
          localTaskCardView.getViewFocusAnimator().changeSize(false);
        }
        i += 1;
        break;
      }
    }
  }
  
  public void startEnterAnimation(boolean paramBoolean)
  {
    int i = 0;
    if (i < this.mGridView.getChildCount())
    {
      Object localObject = (TaskCardView)this.mGridView.getChildAt(i);
      long l = Math.max(this.mDelay * i, 0L);
      ((TaskCardView)localObject).setTranslationX(-this.mTranslationX);
      localObject = ((TaskCardView)localObject).animate();
      if (paramBoolean) {}
      for (float f = this.mDimAlpha;; f = 1.0F)
      {
        ((ViewPropertyAnimator)localObject).alpha(f).translationX(0.0F).setDuration(this.mDuration).setStartDelay(l).setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        i += 1;
        break;
      }
    }
  }
  
  public void startExitAnimation(DismissRecentsToHomeAnimationStarted paramDismissRecentsToHomeAnimationStarted)
  {
    int i = this.mGridView.getChildCount() - 1;
    while (i >= 0)
    {
      TaskCardView localTaskCardView = (TaskCardView)this.mGridView.getChildAt(i);
      long l = Math.max(this.mDelay * (this.mGridView.getChildCount() - 1 - i), 0L);
      localTaskCardView.animate().alpha(0.0F).translationXBy(-this.mTranslationX).setDuration(this.mDuration).setStartDelay(l).setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
      if (i == 0)
      {
        localTaskCardView.animate().setListener(paramDismissRecentsToHomeAnimationStarted.getAnimationTrigger().decrementOnAnimationEnd());
        paramDismissRecentsToHomeAnimationStarted.getAnimationTrigger().increment();
      }
      i -= 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\tv\animations\HomeRecentsEnterExitAnimationHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */