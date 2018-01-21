package com.android.systemui.statusbar;

import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import com.android.systemui.Interpolators;

public class CrossFadeHelper
{
  public static void fadeIn(View paramView)
  {
    paramView.animate().cancel();
    if (paramView.getVisibility() == 4)
    {
      paramView.setAlpha(0.0F);
      paramView.setVisibility(0);
    }
    paramView.animate().alpha(1.0F).setDuration(210L).setInterpolator(Interpolators.ALPHA_IN).withEndAction(null);
    if (paramView.hasOverlappingRendering()) {
      paramView.animate().withLayer();
    }
  }
  
  public static void fadeIn(View paramView, float paramFloat)
  {
    fadeIn(paramView, paramFloat, true);
  }
  
  public static void fadeIn(View paramView, float paramFloat, boolean paramBoolean)
  {
    paramView.animate().cancel();
    if (paramView.getVisibility() == 4) {
      paramView.setVisibility(0);
    }
    float f = paramFloat;
    if (paramBoolean) {
      f = mapToFadeDuration(paramFloat);
    }
    paramFloat = Interpolators.ALPHA_IN.getInterpolation(f);
    paramView.setAlpha(paramFloat);
    updateLayerType(paramView, paramFloat);
  }
  
  public static void fadeOut(View paramView, float paramFloat)
  {
    fadeOut(paramView, paramFloat, true);
  }
  
  public static void fadeOut(View paramView, float paramFloat, boolean paramBoolean)
  {
    paramView.animate().cancel();
    if (paramFloat == 1.0F) {
      paramView.setVisibility(4);
    }
    for (;;)
    {
      float f = paramFloat;
      if (paramBoolean) {
        f = mapToFadeDuration(paramFloat);
      }
      paramFloat = Interpolators.ALPHA_OUT.getInterpolation(1.0F - f);
      paramView.setAlpha(paramFloat);
      updateLayerType(paramView, paramFloat);
      return;
      if (paramView.getVisibility() == 4) {
        paramView.setVisibility(0);
      }
    }
  }
  
  public static void fadeOut(final View paramView, Runnable paramRunnable)
  {
    paramView.animate().cancel();
    paramView.animate().alpha(0.0F).setDuration(210L).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(new Runnable()
    {
      public void run()
      {
        if (this.val$endRunnable != null) {
          this.val$endRunnable.run();
        }
        paramView.setVisibility(4);
      }
    });
    if (paramView.hasOverlappingRendering()) {
      paramView.animate().withLayer();
    }
  }
  
  private static float mapToFadeDuration(float paramFloat)
  {
    return Math.min(paramFloat / 0.5833333F, 1.0F);
  }
  
  private static void updateLayerType(View paramView, float paramFloat)
  {
    if ((paramView.hasOverlappingRendering()) && (paramFloat > 0.0F) && (paramFloat < 1.0F)) {
      paramView.setLayerType(2, null);
    }
    while (paramView.getLayerType() != 2) {
      return;
    }
    paramView.setLayerType(0, null);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\CrossFadeHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */