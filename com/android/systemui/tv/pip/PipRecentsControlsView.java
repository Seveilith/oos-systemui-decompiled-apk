package com.android.systemui.tv.pip;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

public class PipRecentsControlsView
  extends FrameLayout
{
  private Animator mFocusGainAnimator;
  private AnimatorSet mFocusLossAnimatorSet;
  private PipControlsView mPipControlsView;
  private final PipManager mPipManager = PipManager.getInstance();
  private View mScrim;
  
  public PipRecentsControlsView(Context paramContext)
  {
    this(paramContext, null, 0, 0);
  }
  
  public PipRecentsControlsView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0, 0);
  }
  
  public PipRecentsControlsView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public PipRecentsControlsView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  private static void cancelAnimator(Animator paramAnimator)
  {
    if (paramAnimator.isStarted()) {
      paramAnimator.cancel();
    }
  }
  
  private Animator loadAnimator(View paramView, int paramInt)
  {
    Animator localAnimator = AnimatorInflater.loadAnimator(getContext(), paramInt);
    localAnimator.setTarget(paramView);
    return localAnimator;
  }
  
  private static void startAnimator(Animator paramAnimator1, Animator paramAnimator2)
  {
    cancelAnimator(paramAnimator2);
    if (!paramAnimator1.isStarted()) {
      paramAnimator1.start();
    }
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if (!paramKeyEvent.isCanceled())
    {
      if ((paramKeyEvent.getKeyCode() == 4) && (paramKeyEvent.getAction() == 1))
      {
        if (this.mPipControlsView.mListener != null) {
          ((Listener)this.mPipControlsView.mListener).onBackPressed();
        }
        return true;
      }
      if (paramKeyEvent.getKeyCode() == 20)
      {
        if (paramKeyEvent.getAction() == 0) {
          this.mPipManager.getPipRecentsOverlayManager().clearFocus();
        }
        return true;
      }
    }
    return super.dispatchKeyEvent(paramKeyEvent);
  }
  
  public void onFinishInflate()
  {
    super.onFinishInflate();
    this.mPipControlsView = ((PipControlsView)findViewById(2131952329));
    this.mScrim = findViewById(2131951664);
    this.mFocusGainAnimator = loadAnimator(this.mPipControlsView, 2131034312);
    this.mFocusLossAnimatorSet = new AnimatorSet();
    this.mFocusLossAnimatorSet.playSequentially(new Animator[] { loadAnimator(this.mPipControlsView, 2131034313), loadAnimator(this.mScrim, 2131034314) });
    setPadding(0, this.mPipManager.getRecentsFocusedPipBounds().bottom, 0, 0);
  }
  
  public void reset()
  {
    cancelAnimator(this.mFocusGainAnimator);
    cancelAnimator(this.mFocusLossAnimatorSet);
    this.mScrim.setAlpha(0.0F);
    this.mPipControlsView.setTranslationY(0.0F);
    this.mPipControlsView.setScaleX(1.0F);
    this.mPipControlsView.setScaleY(1.0F);
    this.mPipControlsView.reset();
  }
  
  public void setListener(Listener paramListener)
  {
    this.mPipControlsView.setListener(paramListener);
  }
  
  public void startFocusGainAnimation()
  {
    this.mScrim.setAlpha(0.0F);
    PipControlButtonView localPipControlButtonView = this.mPipControlsView.getFocusedButton();
    if (localPipControlButtonView != null) {
      localPipControlButtonView.startFocusGainAnimation();
    }
    startAnimator(this.mFocusGainAnimator, this.mFocusLossAnimatorSet);
  }
  
  public void startFocusLossAnimation()
  {
    PipControlButtonView localPipControlButtonView = this.mPipControlsView.getFocusedButton();
    if (localPipControlButtonView != null) {
      localPipControlButtonView.startFocusLossAnimation();
    }
    startAnimator(this.mFocusLossAnimatorSet, this.mFocusGainAnimator);
  }
  
  public static abstract interface Listener
    extends PipControlsView.Listener
  {
    public abstract void onBackPressed();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tv\pip\PipRecentsControlsView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */