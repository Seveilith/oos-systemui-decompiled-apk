package android.support.v7.app;

import android.support.annotation.RestrictTo;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class AppCompatDelegate
{
  private static boolean sCompatVectorFromResourcesEnabled = false;
  private static int sDefaultNightMode = -1;
  
  public static boolean isCompatVectorFromResourcesEnabled()
  {
    return sCompatVectorFromResourcesEnabled;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public static @interface NightMode {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\app\AppCompatDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */