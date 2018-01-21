package com.android.systemui.statusbar.notification;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import com.android.internal.util.NotificationColorUtil;

public class NotificationUtils
{
  private static final int[] sLocationBase = new int[2];
  private static final int[] sLocationOffset = new int[2];
  
  public static float getRelativeYOffset(View paramView1, View paramView2)
  {
    paramView2.getLocationOnScreen(sLocationBase);
    paramView1.getLocationOnScreen(sLocationOffset);
    return sLocationOffset[1] - sLocationBase[1];
  }
  
  public static float interpolate(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return (1.0F - paramFloat3) * paramFloat1 + paramFloat2 * paramFloat3;
  }
  
  public static int interpolateColors(int paramInt1, int paramInt2, float paramFloat)
  {
    return Color.argb((int)interpolate(Color.alpha(paramInt1), Color.alpha(paramInt2), paramFloat), (int)interpolate(Color.red(paramInt1), Color.red(paramInt2), paramFloat), (int)interpolate(Color.green(paramInt1), Color.green(paramInt2), paramFloat), (int)interpolate(Color.blue(paramInt1), Color.blue(paramInt2), paramFloat));
  }
  
  public static boolean isGrayscale(ImageView paramImageView, NotificationColorUtil paramNotificationColorUtil)
  {
    Object localObject = paramImageView.getTag(2131951678);
    if (localObject != null) {
      return Boolean.TRUE.equals(localObject);
    }
    boolean bool = paramNotificationColorUtil.isGrayscaleIcon(paramImageView.getDrawable());
    paramImageView.setTag(2131951678, Boolean.valueOf(bool));
    return bool;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\notification\NotificationUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */