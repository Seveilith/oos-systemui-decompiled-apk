package android.support.v4.internal.view;

import android.support.annotation.RestrictTo;
import android.view.MenuItem;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public abstract interface SupportMenuItem
  extends MenuItem
{
  public abstract boolean expandActionView();
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\internal\view\SupportMenuItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */