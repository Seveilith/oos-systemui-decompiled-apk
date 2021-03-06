package android.support.v4.graphics.drawable;

import android.graphics.drawable.Drawable;
import android.util.Log;
import java.lang.reflect.Method;

class DrawableCompatJellybeanMr1
{
  private static Method sSetLayoutDirectionMethod;
  private static boolean sSetLayoutDirectionMethodFetched;
  
  public static boolean setLayoutDirection(Drawable paramDrawable, int paramInt)
  {
    if (!sSetLayoutDirectionMethodFetched) {}
    try
    {
      sSetLayoutDirectionMethod = Drawable.class.getDeclaredMethod("setLayoutDirection", new Class[] { Integer.TYPE });
      sSetLayoutDirectionMethod.setAccessible(true);
      sSetLayoutDirectionMethodFetched = true;
      if (sSetLayoutDirectionMethod == null) {}
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;)
      {
        try
        {
          sSetLayoutDirectionMethod.invoke(paramDrawable, new Object[] { Integer.valueOf(paramInt) });
          return true;
        }
        catch (Exception paramDrawable)
        {
          Log.i("DrawableCompatJellybeanMr1", "Failed to invoke setLayoutDirection(int) via reflection", paramDrawable);
          sSetLayoutDirectionMethod = null;
        }
        localNoSuchMethodException = localNoSuchMethodException;
        Log.i("DrawableCompatJellybeanMr1", "Failed to retrieve setLayoutDirection(int) method", localNoSuchMethodException);
      }
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\graphics\drawable\DrawableCompatJellybeanMr1.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */