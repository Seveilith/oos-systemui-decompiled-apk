package com.android.settingslib.drawer;

import android.R.styleable;
import android.animation.ArgbEvaluator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toolbar;
import com.android.settingslib.R.drawable;
import com.android.settingslib.R.id;
import com.android.settingslib.R.layout;
import com.android.settingslib.R.string;
import com.android.settingslib.R.style;
import com.android.settingslib.applications.InterestingConfigChanges;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingsDrawerActivity
  extends Activity
  implements DrawerLayout.DrawerListener
{
  private static final boolean DEBUG = Log.isLoggable("SettingsDrawerActivity", 3);
  private static final int STATUS_BAR_OVERLAY_COLOR = Color.parseColor("#00000000");
  private static final int STATUS_BAR_OVERLAY_TRANSLUCENT_COLOR = Color.parseColor("#15000000");
  private static InterestingConfigChanges sConfigTracker;
  private static List<DashboardCategory> sDashboardCategories;
  private static ArraySet<ComponentName> sTileBlacklist = new ArraySet();
  private static HashMap<Pair<String, String>, Tile> sTileCache;
  private ArgbEvaluator mArgbEvaluator;
  private final List<CategoryListener> mCategoryListeners = new ArrayList();
  private FrameLayout mContentHeaderContainer;
  private SettingsDrawerAdapter mDrawerAdapter;
  private DrawerLayout mDrawerLayout;
  private DrawerLayout.DrawerListener mDrawerListener;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 0: 
        SettingsDrawerActivity.-wrap0(SettingsDrawerActivity.this);
        return;
      }
      new SettingsDrawerActivity.CategoriesUpdater(SettingsDrawerActivity.this, null).execute(new Void[0]);
    }
  };
  private final PackageReceiver mPackageReceiver = new PackageReceiver(null);
  private boolean mShowingMenu;
  private UserManager mUserManager;
  private Window mWindow;
  
  private void delayInitDrawer()
  {
    ListView localListView = (ListView)findViewById(R.id.left_drawer);
    localListView.setAdapter(this.mDrawerAdapter);
    localListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        SettingsDrawerActivity.this.onTileClicked(SettingsDrawerActivity.-get0(SettingsDrawerActivity.this).getTile(paramAnonymousInt));
      }
    });
  }
  
  private void updateUserHandlesIfNeeded(Tile paramTile)
  {
    paramTile = paramTile.userHandle;
    int i = paramTile.size() - 1;
    while (i >= 0)
    {
      if (this.mUserManager.getUserInfo(((UserHandle)paramTile.get(i)).getIdentifier()) == null)
      {
        if (DEBUG) {
          Log.d("SettingsDrawerActivity", "Delete the user: " + ((UserHandle)paramTile.get(i)).getIdentifier());
        }
        paramTile.remove(i);
      }
      i -= 1;
    }
  }
  
  public List<DashboardCategory> getDashboardCategories()
  {
    if (sDashboardCategories == null)
    {
      sTileCache = new HashMap();
      sConfigTracker = new InterestingConfigChanges();
      sConfigTracker.applyNewConfig(getResources());
      sDashboardCategories = TileUtils.getCategories(this, sTileCache);
    }
    return sDashboardCategories;
  }
  
  protected void onCategoriesChanged()
  {
    updateDrawer();
    int j = this.mCategoryListeners.size();
    int i = 0;
    while (i < j)
    {
      ((CategoryListener)this.mCategoryListeners.get(i)).onCategoriesChanged();
      i += 1;
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    System.currentTimeMillis();
    paramBundle = getTheme().obtainStyledAttributes(R.styleable.Theme);
    this.mWindow = getWindow();
    if (!paramBundle.getBoolean(38, false)) {
      requestWindowFeature(1);
    }
    super.setContentView(R.layout.settings_with_drawer);
    this.mContentHeaderContainer = ((FrameLayout)findViewById(R.id.content_header_container));
    this.mDrawerLayout = ((DrawerLayout)findViewById(R.id.drawer_layout));
    if (this.mDrawerLayout == null) {
      return;
    }
    this.mDrawerLayout.setDrawerListener(this);
    this.mArgbEvaluator = new ArgbEvaluator();
    Toolbar localToolbar = (Toolbar)findViewById(R.id.action_bar);
    if (paramBundle.getBoolean(38, false))
    {
      localToolbar.setVisibility(8);
      this.mDrawerLayout.setDrawerLockMode(1);
      this.mDrawerLayout = null;
      return;
    }
    localToolbar.setTitleTextAppearance(this, R.style.Settings_TextAppearance_Material_Widget_ActionBar_Title);
    setActionBar(localToolbar);
    this.mUserManager = UserManager.get(this);
    this.mDrawerAdapter = new SettingsDrawerAdapter(this);
    this.mHandler.sendEmptyMessageDelayed(0, 100L);
  }
  
  public void onDrawerClosed(View paramView)
  {
    if (this.mDrawerListener != null) {
      this.mDrawerListener.onDrawerClosed(paramView);
    }
  }
  
  public void onDrawerOpened(View paramView)
  {
    if (this.mDrawerListener != null) {
      this.mDrawerListener.onDrawerOpened(paramView);
    }
  }
  
  public void onDrawerSlide(View paramView, float paramFloat) {}
  
  public void onDrawerStateChanged(int paramInt)
  {
    if (this.mDrawerListener != null) {
      this.mDrawerListener.onDrawerStateChanged(paramInt);
    }
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if ((this.mShowingMenu) && (this.mDrawerLayout != null) && (paramMenuItem.getItemId() == 16908332) && (this.mDrawerAdapter.getCount() != 0))
    {
      openDrawer();
      return true;
    }
    return super.onOptionsItemSelected(paramMenuItem);
  }
  
  protected void onPause()
  {
    if (this.mDrawerLayout != null) {
      unregisterReceiver(this.mPackageReceiver);
    }
    super.onPause();
  }
  
  public void onProfileTileOpen()
  {
    finish();
  }
  
  protected void onResume()
  {
    super.onResume();
    if (this.mDrawerLayout != null)
    {
      IntentFilter localIntentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
      localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
      localIntentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
      localIntentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
      localIntentFilter.addDataScheme("package");
      registerReceiver(this.mPackageReceiver, localIntentFilter);
      if (sDashboardCategories != null) {
        break label104;
      }
      this.mHandler.sendEmptyMessageDelayed(1, 100L);
    }
    for (;;)
    {
      if ((getIntent() != null) && (getIntent().getBooleanExtra("show_drawer_menu", false))) {
        showMenuIcon();
      }
      return;
      label104:
      new CategoriesUpdater(null).execute(new Void[0]);
    }
  }
  
  protected void onTileClicked(Tile paramTile)
  {
    if (openTile(paramTile)) {
      finish();
    }
  }
  
  public void openDrawer()
  {
    if (this.mDrawerLayout != null) {
      this.mDrawerLayout.openDrawer(8388611);
    }
  }
  
  public boolean openTile(Tile paramTile)
  {
    new Handler().postDelayed(new Runnable()
    {
      public void run()
      {
        SettingsDrawerActivity.-get1(SettingsDrawerActivity.this).closeDrawer(8388611, false);
      }
    }, 300L);
    if (paramTile == null)
    {
      startActivity(new Intent("android.settings.SETTINGS").addFlags(32768));
      return true;
    }
    try
    {
      updateUserHandlesIfNeeded(paramTile);
      int i = paramTile.userHandle.size();
      if (i > 1)
      {
        ProfileSelectDialog.show(getFragmentManager(), paramTile);
        return false;
      }
      if (i == 1)
      {
        paramTile.intent.putExtra("show_drawer_menu", true);
        paramTile.intent.addFlags(32768);
        startActivityAsUser(paramTile.intent, (UserHandle)paramTile.userHandle.get(0));
        return true;
      }
      paramTile.intent.putExtra("show_drawer_menu", true);
      paramTile.intent.addFlags(32768);
      startActivity(paramTile.intent);
      return true;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.w("SettingsDrawerActivity", "Couldn't find tile " + paramTile.intent, localActivityNotFoundException);
    }
    return true;
  }
  
  public void setContentView(int paramInt)
  {
    ViewGroup localViewGroup = (ViewGroup)findViewById(R.id.content_frame);
    if (localViewGroup != null) {
      localViewGroup.removeAllViews();
    }
    LayoutInflater.from(this).inflate(paramInt, localViewGroup);
  }
  
  public void setContentView(View paramView)
  {
    ((ViewGroup)findViewById(R.id.content_frame)).addView(paramView);
  }
  
  public void setContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    ((ViewGroup)findViewById(R.id.content_frame)).addView(paramView, paramLayoutParams);
  }
  
  public void showMenuIcon()
  {
    this.mShowingMenu = true;
    if (getActionBar() != null)
    {
      getActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
      getActionBar().setHomeActionContentDescription(R.string.content_description_menu_button);
      getActionBar().setDisplayHomeAsUpEnabled(true);
    }
  }
  
  public void updateDrawer()
  {
    if ((this.mDrawerLayout == null) || (this.mDrawerAdapter == null)) {
      return;
    }
    this.mDrawerAdapter.updateCategories();
    if (this.mDrawerAdapter.getCount() != 0)
    {
      this.mDrawerLayout.setDrawerLockMode(0);
      return;
    }
    this.mDrawerLayout.setDrawerLockMode(1);
  }
  
  private class CategoriesUpdater
    extends AsyncTask<Void, Void, List<DashboardCategory>>
  {
    private CategoriesUpdater() {}
    
    protected List<DashboardCategory> doInBackground(Void... paramVarArgs)
    {
      if (SettingsDrawerActivity.-get2().applyNewConfig(SettingsDrawerActivity.this.getResources())) {
        SettingsDrawerActivity.-get4().clear();
      }
      return TileUtils.getCategories(SettingsDrawerActivity.this, SettingsDrawerActivity.-get4());
    }
    
    protected void onPostExecute(List<DashboardCategory> paramList)
    {
      int j = 0;
      while (j < paramList.size())
      {
        DashboardCategory localDashboardCategory = (DashboardCategory)paramList.get(j);
        int k;
        for (int i = 0; i < localDashboardCategory.tiles.size(); i = k + 1)
        {
          Tile localTile = (Tile)localDashboardCategory.tiles.get(i);
          k = i;
          if (SettingsDrawerActivity.-get3().contains(localTile.intent.getComponent()))
          {
            localDashboardCategory.tiles.remove(i);
            k = i - 1;
          }
        }
        j += 1;
      }
      SettingsDrawerActivity.-set0(paramList);
      SettingsDrawerActivity.this.onCategoriesChanged();
    }
    
    protected void onPreExecute()
    {
      if ((SettingsDrawerActivity.-get2() == null) || (SettingsDrawerActivity.-get4() == null)) {
        SettingsDrawerActivity.this.getDashboardCategories();
      }
    }
  }
  
  public static abstract interface CategoryListener
  {
    public abstract void onCategoriesChanged();
  }
  
  private class PackageReceiver
    extends BroadcastReceiver
  {
    private PackageReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      new SettingsDrawerActivity.CategoriesUpdater(SettingsDrawerActivity.this, null).execute(new Void[0]);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\settingslib\drawer\SettingsDrawerActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */