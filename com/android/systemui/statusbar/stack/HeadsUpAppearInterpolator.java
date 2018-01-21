package com.android.systemui.statusbar.stack;

import android.graphics.Path;
import android.view.animation.PathInterpolator;

public class HeadsUpAppearInterpolator
  extends PathInterpolator
{
  public HeadsUpAppearInterpolator()
  {
    super(getAppearPath());
  }
  
  private static Path getAppearPath()
  {
    Path localPath = new Path();
    localPath.moveTo(0.0F, 0.0F);
    float f = 400.0F + 100.0F;
    localPath.cubicTo(225.0F / f, 0.0F, 200.0F / f, 1.125F, 250.0F / f, 1.125F);
    localPath.cubicTo((60.0F + 250.0F) / f, 1.125F, (30.0F + 250.0F) / f, 0.975F, 400.0F / f, 0.975F);
    localPath.cubicTo((40.0F + 400.0F) / f, 0.975F, (20.0F + 400.0F) / f, 1.0F, 1.0F, 1.0F);
    return localPath;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\stack\HeadsUpAppearInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */