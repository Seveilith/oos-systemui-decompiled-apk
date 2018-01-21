package android.support.v4.content.res;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

class ConfigurationHelperGingerbread
{
  static int getScreenHeightDp(@NonNull Resources paramResources)
  {
    paramResources = paramResources.getDisplayMetrics();
    return (int)(paramResources.heightPixels / paramResources.density);
  }
  
  static int getScreenWidthDp(@NonNull Resources paramResources)
  {
    paramResources = paramResources.getDisplayMetrics();
    return (int)(paramResources.widthPixels / paramResources.density);
  }
  
  static int getSmallestScreenWidthDp(@NonNull Resources paramResources)
  {
    return Math.min(getScreenWidthDp(paramResources), getScreenHeightDp(paramResources));
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\content\res\ConfigurationHelperGingerbread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */