package com.android.systemui.recents.tv.animations;

import android.animation.Animator.AnimatorListener;
import android.content.res.Resources;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.systemui.Interpolators;
import com.android.systemui.recents.tv.views.TaskCardView;

public class DismissAnimationsHolder
{
  private ImageView mCardDismissIcon;
  private TransitionDrawable mDismissDrawable;
  private int mDismissEnterYDelta;
  private float mDismissIconNotInDismissStateAlpha;
  private int mDismissStartYDelta;
  private TextView mDismissText;
  private LinearLayout mInfoField;
  private long mLongDuration;
  private long mShortDuration;
  private View mThumbnailView;
  
  public DismissAnimationsHolder(TaskCardView paramTaskCardView)
  {
    this.mInfoField = ((LinearLayout)paramTaskCardView.findViewById(2131952204));
    this.mThumbnailView = paramTaskCardView.findViewById(2131952207);
    this.mCardDismissIcon = ((ImageView)paramTaskCardView.findViewById(2131952209));
    this.mDismissDrawable = ((TransitionDrawable)this.mCardDismissIcon.getDrawable());
    this.mDismissDrawable.setCrossFadeEnabled(true);
    this.mDismissText = ((TextView)paramTaskCardView.findViewById(2131952210));
    paramTaskCardView = paramTaskCardView.getResources();
    this.mDismissEnterYDelta = paramTaskCardView.getDimensionPixelOffset(2131755661);
    this.mDismissStartYDelta = (this.mDismissEnterYDelta * 2);
    this.mShortDuration = paramTaskCardView.getInteger(2131624025);
    this.mLongDuration = paramTaskCardView.getInteger(2131624026);
    this.mDismissIconNotInDismissStateAlpha = paramTaskCardView.getFloat(2131624037);
  }
  
  public void reset()
  {
    this.mInfoField.setAlpha(1.0F);
    this.mInfoField.setTranslationY(0.0F);
    this.mInfoField.animate().setListener(null);
    this.mThumbnailView.setAlpha(1.0F);
    this.mThumbnailView.setTranslationY(0.0F);
    this.mCardDismissIcon.setAlpha(0.0F);
    this.mDismissText.setAlpha(0.0F);
  }
  
  public void startDismissAnimation(Animator.AnimatorListener paramAnimatorListener)
  {
    this.mCardDismissIcon.animate().setDuration(this.mShortDuration).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).alpha(0.0F).withEndAction(new Runnable()
    {
      public void run()
      {
        DismissAnimationsHolder.-get0(DismissAnimationsHolder.this).reverseTransition(0);
      }
    });
    this.mDismissText.animate().setDuration(this.mShortDuration).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).alpha(0.0F);
    this.mInfoField.animate().setDuration(this.mLongDuration).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).translationY(this.mDismissStartYDelta).alpha(0.0F).setListener(paramAnimatorListener);
    this.mThumbnailView.animate().setDuration(this.mLongDuration).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).translationY(this.mDismissStartYDelta).alpha(0.0F);
  }
  
  public void startEnterAnimation()
  {
    this.mCardDismissIcon.animate().setDuration(this.mShortDuration).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).alpha(1.0F).withStartAction(new Runnable()
    {
      public void run()
      {
        DismissAnimationsHolder.-get0(DismissAnimationsHolder.this).startTransition(0);
      }
    });
    this.mDismissText.animate().setDuration(this.mShortDuration).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).alpha(1.0F);
    this.mInfoField.animate().setDuration(this.mShortDuration).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).translationY(this.mDismissEnterYDelta).alpha(0.5F);
    this.mThumbnailView.animate().setDuration(this.mShortDuration).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).translationY(this.mDismissEnterYDelta).alpha(0.5F);
  }
  
  public void startExitAnimation()
  {
    this.mCardDismissIcon.animate().setDuration(this.mShortDuration).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).alpha(this.mDismissIconNotInDismissStateAlpha).withEndAction(new Runnable()
    {
      public void run()
      {
        DismissAnimationsHolder.-get0(DismissAnimationsHolder.this).reverseTransition(0);
      }
    });
    this.mDismissText.animate().setDuration(this.mShortDuration).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).alpha(0.0F);
    this.mInfoField.animate().setDuration(this.mShortDuration).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).translationY(0.0F).alpha(1.0F);
    this.mThumbnailView.animate().setDuration(this.mShortDuration).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).translationY(0.0F).alpha(1.0F);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\tv\animations\DismissAnimationsHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */