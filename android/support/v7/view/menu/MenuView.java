package android.support.v7.view.menu;

import android.support.annotation.RestrictTo;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public abstract interface MenuView
{
  public static abstract interface ItemView
  {
    public abstract MenuItemImpl getItemData();
    
    public abstract void initialize(MenuItemImpl paramMenuItemImpl, int paramInt);
    
    public abstract boolean prefersCondensedTitle();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\view\menu\MenuView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */