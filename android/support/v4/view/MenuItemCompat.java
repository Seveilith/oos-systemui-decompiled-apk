package android.support.v4.view;

import android.os.Build.VERSION;
import android.support.v4.internal.view.SupportMenuItem;
import android.view.MenuItem;

public final class MenuItemCompat
{
  static final MenuVersionImpl IMPL = new BaseMenuVersionImpl();
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    if (i >= 14)
    {
      IMPL = new IcsMenuVersionImpl();
      return;
    }
    if (i >= 11)
    {
      IMPL = new HoneycombMenuVersionImpl();
      return;
    }
  }
  
  public static boolean expandActionView(MenuItem paramMenuItem)
  {
    if ((paramMenuItem instanceof SupportMenuItem)) {
      return ((SupportMenuItem)paramMenuItem).expandActionView();
    }
    return IMPL.expandActionView(paramMenuItem);
  }
  
  static class BaseMenuVersionImpl
    implements MenuItemCompat.MenuVersionImpl
  {
    public boolean expandActionView(MenuItem paramMenuItem)
    {
      return false;
    }
  }
  
  static class HoneycombMenuVersionImpl
    implements MenuItemCompat.MenuVersionImpl
  {
    public boolean expandActionView(MenuItem paramMenuItem)
    {
      return false;
    }
  }
  
  static class IcsMenuVersionImpl
    extends MenuItemCompat.HoneycombMenuVersionImpl
  {
    public boolean expandActionView(MenuItem paramMenuItem)
    {
      return MenuItemCompatIcs.expandActionView(paramMenuItem);
    }
  }
  
  static abstract interface MenuVersionImpl
  {
    public abstract boolean expandActionView(MenuItem paramMenuItem);
  }
  
  public static abstract interface OnActionExpandListener
  {
    public abstract boolean onMenuItemActionCollapse(MenuItem paramMenuItem);
    
    public abstract boolean onMenuItemActionExpand(MenuItem paramMenuItem);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\view\MenuItemCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */