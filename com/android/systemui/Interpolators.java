package com.android.systemui;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;

public class Interpolators
{
  public static final Interpolator ACCELERATE;
  public static final Interpolator ACCELERATE_DECELERATE;
  public static final Interpolator ALPHA_IN;
  public static final Interpolator ALPHA_OUT;
  public static final Interpolator CUSTOM_40_40 = new PathInterpolator(0.4F, 0.0F, 0.6F, 1.0F);
  public static final Interpolator DECELERATE_QUINT;
  public static final Interpolator FAST_OUT_LINEAR_IN;
  public static final Interpolator FAST_OUT_SLOW_IN = new PathInterpolator(0.4F, 0.0F, 0.2F, 1.0F);
  public static final Interpolator LINEAR;
  public static final Interpolator LINEAR_OUT_SLOW_IN;
  public static final Interpolator TOUCH_RESPONSE = new PathInterpolator(0.3F, 0.0F, 0.1F, 1.0F);
  
  static
  {
    FAST_OUT_LINEAR_IN = new PathInterpolator(0.4F, 0.0F, 1.0F, 1.0F);
    LINEAR_OUT_SLOW_IN = new PathInterpolator(0.0F, 0.0F, 0.2F, 1.0F);
    ALPHA_IN = new PathInterpolator(0.4F, 0.0F, 1.0F, 1.0F);
    ALPHA_OUT = new PathInterpolator(0.0F, 0.0F, 0.8F, 1.0F);
    LINEAR = new LinearInterpolator();
    ACCELERATE = new AccelerateInterpolator();
    ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();
    DECELERATE_QUINT = new DecelerateInterpolator(2.5F);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\Interpolators.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */