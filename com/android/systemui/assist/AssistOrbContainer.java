package com.android.systemui.assist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import com.android.systemui.Interpolators;

public class AssistOrbContainer
  extends FrameLayout
{
  private boolean mAnimatingOut;
  private View mNavbarScrim;
  private AssistOrbView mOrb;
  private View mScrim;
  
  public AssistOrbContainer(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AssistOrbContainer(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public AssistOrbContainer(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  private void reset()
  {
    this.mAnimatingOut = false;
    this.mOrb.reset();
    this.mScrim.setAlpha(1.0F);
    this.mNavbarScrim.setAlpha(1.0F);
  }
  
  private void startEnterAnimation()
  {
    if (this.mAnimatingOut) {
      return;
    }
    this.mOrb.startEnterAnimation();
    this.mScrim.setAlpha(0.0F);
    this.mNavbarScrim.setAlpha(0.0F);
    post(new Runnable()
    {
      public void run()
      {
        AssistOrbContainer.-get1(AssistOrbContainer.this).animate().alpha(1.0F).setDuration(300L).setStartDelay(0L).setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
        AssistOrbContainer.-get0(AssistOrbContainer.this).animate().alpha(1.0F).setDuration(300L).setStartDelay(0L).setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
      }
    });
  }
  
  private void startExitAnimation(Runnable paramRunnable)
  {
    if (this.mAnimatingOut)
    {
      if (paramRunnable != null) {
        paramRunnable.run();
      }
      return;
    }
    this.mAnimatingOut = true;
    this.mOrb.startExitAnimation(150L);
    this.mScrim.animate().alpha(0.0F).setDuration(250L).setStartDelay(150L).setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
    this.mNavbarScrim.animate().alpha(0.0F).setDuration(250L).setStartDelay(150L).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).withEndAction(paramRunnable);
  }
  
  public AssistOrbView getOrb()
  {
    return this.mOrb;
  }
  
  public boolean isShowing()
  {
    return (getVisibility() == 0) && (!this.mAnimatingOut);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mScrim = findViewById(2131951789);
    this.mNavbarScrim = findViewById(2131951792);
    this.mOrb = ((AssistOrbView)findViewById(2131951790));
  }
  
  public void show(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      if (getVisibility() != 0)
      {
        setVisibility(0);
        if (paramBoolean2) {
          startEnterAnimation();
        }
      }
      else
      {
        return;
      }
      reset();
      return;
    }
    if (paramBoolean2)
    {
      startExitAnimation(new Runnable()
      {
        public void run()
        {
          AssistOrbContainer.-set0(AssistOrbContainer.this, false);
          AssistOrbContainer.this.setVisibility(8);
        }
      });
      return;
    }
    setVisibility(8);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\assist\AssistOrbContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */