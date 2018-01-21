package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Resources;

public class VelocityTrackerFactory
{
  public static VelocityTrackerInterface obtain(Context paramContext)
  {
    paramContext = paramContext.getResources().getString(2131689911);
    if (paramContext.equals("noisy")) {
      return NoisyVelocityTracker.obtain();
    }
    if (paramContext.equals("platform")) {
      return PlatformVelocityTracker.obtain();
    }
    throw new IllegalStateException("Invalid tracker: " + paramContext);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\VelocityTrackerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */