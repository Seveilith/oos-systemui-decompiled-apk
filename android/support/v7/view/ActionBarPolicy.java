package android.support.v7.view;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.ConfigurationHelper;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.DisplayMetrics;
import android.view.ViewConfiguration;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class ActionBarPolicy
{
  private Context mContext;
  
  private ActionBarPolicy(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public static ActionBarPolicy get(Context paramContext)
  {
    return new ActionBarPolicy(paramContext);
  }
  
  public int getEmbeddedMenuWidthLimit()
  {
    return this.mContext.getResources().getDisplayMetrics().widthPixels / 2;
  }
  
  public int getMaxActionButtons()
  {
    Resources localResources = this.mContext.getResources();
    int i = ConfigurationHelper.getScreenWidthDp(localResources);
    int j = ConfigurationHelper.getScreenHeightDp(localResources);
    if ((ConfigurationHelper.getSmallestScreenWidthDp(localResources) > 600) || (i > 600)) {}
    while (((i > 960) && (j > 720)) || ((i > 720) && (j > 960))) {
      return 5;
    }
    if ((i >= 500) || ((i > 640) && (j > 480))) {}
    while ((i > 480) && (j > 640)) {
      return 4;
    }
    if (i >= 360) {
      return 3;
    }
    return 2;
  }
  
  public boolean showsOverflowMenuButton()
  {
    boolean bool = true;
    if (Build.VERSION.SDK_INT >= 19) {
      return true;
    }
    if (ViewConfigurationCompat.hasPermanentMenuKey(ViewConfiguration.get(this.mContext))) {
      bool = false;
    }
    return bool;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\view\ActionBarPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */