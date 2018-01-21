package android.support.v4.content;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.os.BuildCompat;
import android.util.TypedValue;

public class ContextCompat
{
  private static final Object sLock = new Object();
  private static TypedValue sTempValue;
  
  public static Context createDeviceProtectedStorageContext(Context paramContext)
  {
    if (BuildCompat.isAtLeastN()) {
      return ContextCompatApi24.createDeviceProtectedStorageContext(paramContext);
    }
    return null;
  }
  
  public static final ColorStateList getColorStateList(Context paramContext, @ColorRes int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      return ContextCompatApi23.getColorStateList(paramContext, paramInt);
    }
    return paramContext.getResources().getColorStateList(paramInt);
  }
  
  public static final Drawable getDrawable(Context paramContext, @DrawableRes int paramInt)
  {
    int i = Build.VERSION.SDK_INT;
    if (i >= 21) {
      return ContextCompatApi21.getDrawable(paramContext, paramInt);
    }
    if (i >= 16) {
      return paramContext.getResources().getDrawable(paramInt);
    }
    synchronized (sLock)
    {
      if (sTempValue == null) {
        sTempValue = new TypedValue();
      }
      paramContext.getResources().getValue(paramInt, sTempValue, true);
      paramInt = sTempValue.resourceId;
      return paramContext.getResources().getDrawable(paramInt);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\content\ContextCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */