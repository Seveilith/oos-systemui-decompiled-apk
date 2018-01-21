package com.android.systemui.statusbar.notification;

import android.util.Pools.SimplePool;
import android.view.NotificationHeaderView;
import android.view.View;
import android.view.ViewPropertyAnimator;
import com.android.systemui.statusbar.CrossFadeHelper;

public class HeaderTransformState
  extends TransformState
{
  private static Pools.SimplePool<HeaderTransformState> sInstancePool = new Pools.SimplePool(40);
  private View mExpandButton;
  private View mWorkProfileIcon;
  private TransformState mWorkProfileState;
  
  public static HeaderTransformState obtain()
  {
    HeaderTransformState localHeaderTransformState = (HeaderTransformState)sInstancePool.acquire();
    if (localHeaderTransformState != null) {
      return localHeaderTransformState;
    }
    return new HeaderTransformState();
  }
  
  public void initFrom(View paramView)
  {
    super.initFrom(paramView);
    if ((paramView instanceof NotificationHeaderView))
    {
      paramView = (NotificationHeaderView)paramView;
      this.mExpandButton = paramView.getExpandButton();
      this.mWorkProfileState = TransformState.obtain();
      this.mWorkProfileIcon = paramView.getWorkProfileIcon();
      this.mWorkProfileState.initFrom(this.mWorkProfileIcon);
    }
  }
  
  public void prepareFadeIn()
  {
    super.prepareFadeIn();
    if (!(this.mTransformedView instanceof NotificationHeaderView)) {
      return;
    }
    NotificationHeaderView localNotificationHeaderView = (NotificationHeaderView)this.mTransformedView;
    int j = localNotificationHeaderView.getChildCount();
    int i = 0;
    if (i < j)
    {
      View localView = localNotificationHeaderView.getChildAt(i);
      if (localView.getVisibility() == 8) {}
      for (;;)
      {
        i += 1;
        break;
        localView.animate().cancel();
        localView.setVisibility(0);
        localView.setAlpha(1.0F);
        if (localView == this.mWorkProfileIcon)
        {
          localView.setTranslationX(0.0F);
          localView.setTranslationY(0.0F);
        }
      }
    }
  }
  
  public void recycle()
  {
    super.recycle();
    sInstancePool.release(this);
  }
  
  protected void reset()
  {
    super.reset();
    this.mExpandButton = null;
    this.mWorkProfileState = null;
    if (this.mWorkProfileState != null)
    {
      this.mWorkProfileState.recycle();
      this.mWorkProfileState = null;
    }
  }
  
  public void setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    super.setVisible(paramBoolean1, paramBoolean2);
    if (!(this.mTransformedView instanceof NotificationHeaderView)) {
      return;
    }
    NotificationHeaderView localNotificationHeaderView = (NotificationHeaderView)this.mTransformedView;
    int k = localNotificationHeaderView.getChildCount();
    int i = 0;
    while (i < k)
    {
      View localView = localNotificationHeaderView.getChildAt(i);
      if ((!paramBoolean2) && (localView.getVisibility() == 8))
      {
        i += 1;
      }
      else
      {
        localView.animate().cancel();
        int j;
        if (localView.getVisibility() != 8)
        {
          if (paramBoolean1)
          {
            j = 0;
            label100:
            localView.setVisibility(j);
          }
        }
        else if (localView == this.mExpandButton) {
          if (!paramBoolean1) {
            break label158;
          }
        }
        label158:
        for (float f = 1.0F;; f = 0.0F)
        {
          localView.setAlpha(f);
          if (localView != this.mWorkProfileIcon) {
            break;
          }
          localView.setTranslationX(0.0F);
          localView.setTranslationY(0.0F);
          break;
          j = 4;
          break label100;
        }
      }
    }
  }
  
  public void transformViewFrom(TransformState paramTransformState, float paramFloat)
  {
    if (!(this.mTransformedView instanceof NotificationHeaderView)) {
      return;
    }
    NotificationHeaderView localNotificationHeaderView = (NotificationHeaderView)this.mTransformedView;
    localNotificationHeaderView.setVisibility(0);
    localNotificationHeaderView.setAlpha(1.0F);
    int j = localNotificationHeaderView.getChildCount();
    int i = 0;
    if (i < j)
    {
      View localView = localNotificationHeaderView.getChildAt(i);
      if (localView.getVisibility() == 8) {}
      for (;;)
      {
        i += 1;
        break;
        if (localView == this.mExpandButton)
        {
          CrossFadeHelper.fadeIn(this.mExpandButton, paramFloat);
        }
        else
        {
          localView.setVisibility(0);
          if (localView == this.mWorkProfileIcon) {
            this.mWorkProfileState.transformViewFullyFrom(((HeaderTransformState)paramTransformState).mWorkProfileState, paramFloat);
          }
        }
      }
    }
  }
  
  public boolean transformViewTo(TransformState paramTransformState, float paramFloat)
  {
    if (!(this.mTransformedView instanceof NotificationHeaderView)) {
      return false;
    }
    paramTransformState = (NotificationHeaderView)this.mTransformedView;
    int j = paramTransformState.getChildCount();
    int i = 0;
    if (i < j)
    {
      View localView = paramTransformState.getChildAt(i);
      if (localView.getVisibility() == 8) {}
      for (;;)
      {
        i += 1;
        break;
        if (localView != this.mExpandButton) {
          localView.setVisibility(4);
        } else {
          CrossFadeHelper.fadeOut(this.mExpandButton, paramFloat);
        }
      }
    }
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\notification\HeaderTransformState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */