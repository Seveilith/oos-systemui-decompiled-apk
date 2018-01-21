package android.support.v4.app;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;

public abstract class FragmentTransaction
{
  public abstract FragmentTransaction add(@IdRes int paramInt, Fragment paramFragment, @Nullable String paramString);
  
  public abstract FragmentTransaction attach(Fragment paramFragment);
  
  public abstract int commit();
  
  public abstract FragmentTransaction detach(Fragment paramFragment);
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\app\FragmentTransaction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */