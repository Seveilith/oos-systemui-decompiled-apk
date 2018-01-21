package com.android.systemui.statusbar.phone;

import android.view.animation.Interpolator;

public class BounceInterpolator
  implements Interpolator
{
  public float getInterpolation(float paramFloat)
  {
    paramFloat *= 1.1F;
    if (paramFloat < 0.36363637F) {
      return 7.5625F * paramFloat * paramFloat;
    }
    if (paramFloat < 0.72727275F)
    {
      paramFloat -= 0.54545456F;
      return 7.5625F * paramFloat * paramFloat + 0.75F;
    }
    if (paramFloat < 0.90909094F)
    {
      paramFloat -= 0.8181818F;
      return 7.5625F * paramFloat * paramFloat + 0.9375F;
    }
    return 1.0F;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\BounceInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */