package com.android.systemui.statusbar.car;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.util.SimpleArrayMap;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout.LayoutParams;
import com.android.systemui.statusbar.phone.ActivityStarter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CarNavigationBarController
{
  private ActivityStarter mActivityStarter;
  private Context mContext;
  private int mCurrentFacetIndex;
  private List<String[]> mFacetCategories = new ArrayList();
  private SimpleArrayMap<String, Integer> mFacetCategoryMap = new SimpleArrayMap();
  private SparseBooleanArray mFacetHasMultipleAppsCache = new SparseBooleanArray();
  private SimpleArrayMap<String, Integer> mFacetPackageMap = new SimpleArrayMap();
  private List<String[]> mFacetPackages = new ArrayList();
  private List<Intent> mIntents;
  private List<Intent> mLongPressIntents;
  private CarNavigationBarView mNavBar;
  private List<CarNavigationButton> mNavButtons = new ArrayList();
  
  public CarNavigationBarController(Context paramContext, CarNavigationBarView paramCarNavigationBarView, ActivityStarter paramActivityStarter)
  {
    this.mContext = paramContext;
    this.mNavBar = paramCarNavigationBarView;
    this.mActivityStarter = paramActivityStarter;
    bind();
  }
  
  private void bind()
  {
    Object localObject1 = this.mContext.getResources();
    TypedArray localTypedArray1 = ((Resources)localObject1).obtainTypedArray(2131427596);
    TypedArray localTypedArray2 = ((Resources)localObject1).obtainTypedArray(2131427597);
    TypedArray localTypedArray3 = ((Resources)localObject1).obtainTypedArray(2131427598);
    TypedArray localTypedArray4 = ((Resources)localObject1).obtainTypedArray(2131427599);
    localObject1 = ((Resources)localObject1).obtainTypedArray(2131427600);
    if ((localTypedArray1.length() != localTypedArray2.length()) || (localTypedArray1.length() != localTypedArray3.length())) {}
    while ((localTypedArray1.length() != localTypedArray4.length()) || (localTypedArray1.length() != ((TypedArray)localObject1).length())) {
      throw new RuntimeException("car_facet array lengths do not match");
    }
    this.mIntents = createEmptyIntentList(localTypedArray1.length());
    this.mLongPressIntents = createEmptyIntentList(localTypedArray1.length());
    int i = 0;
    while (i < localTypedArray1.length())
    {
      Drawable localDrawable = localTypedArray1.getDrawable(i);
      try
      {
        this.mIntents.set(i, Intent.parseUri(localTypedArray2.getString(i), 1));
        Object localObject2 = localTypedArray3.getString(i);
        if (((String)localObject2).isEmpty()) {}
        for (boolean bool = false;; bool = true)
        {
          if (bool) {
            this.mLongPressIntents.set(i, Intent.parseUri((String)localObject2, 1));
          }
          localObject2 = createNavButton(localDrawable, i, bool);
          this.mNavButtons.add(localObject2);
          this.mNavBar.addButton((CarNavigationButton)localObject2, createNavButton(localDrawable, i, bool));
          initFacetFilterMaps(i, localTypedArray4.getString(i).split(";"), ((TypedArray)localObject1).getString(i).split(";"));
          this.mFacetHasMultipleAppsCache.put(i, facetHasMultiplePackages(i));
          i += 1;
          break;
        }
        return;
      }
      catch (URISyntaxException localURISyntaxException)
      {
        throw new RuntimeException("Malformed intent uri", localURISyntaxException);
      }
    }
  }
  
  private List<Intent> createEmptyIntentList(int paramInt)
  {
    return Arrays.asList(new Intent[paramInt]);
  }
  
  private CarNavigationButton createNavButton(Drawable paramDrawable, final int paramInt, boolean paramBoolean)
  {
    CarNavigationButton localCarNavigationButton = (CarNavigationButton)View.inflate(this.mContext, 2130968610, null);
    localCarNavigationButton.setResources(paramDrawable);
    localCarNavigationButton.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1.0F));
    localCarNavigationButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        CarNavigationBarController.-wrap0(CarNavigationBarController.this, paramInt);
      }
    });
    if (paramBoolean)
    {
      localCarNavigationButton.setLongClickable(true);
      localCarNavigationButton.setOnLongClickListener(new View.OnLongClickListener()
      {
        public boolean onLongClick(View paramAnonymousView)
        {
          CarNavigationBarController.-wrap1(CarNavigationBarController.this, paramInt);
          return true;
        }
      });
      return localCarNavigationButton;
    }
    localCarNavigationButton.setLongClickable(false);
    return localCarNavigationButton;
  }
  
  private boolean facetHasMultiplePackages(int paramInt)
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    String[] arrayOfString = (String[])this.mFacetPackages.get(paramInt);
    if (arrayOfString.length > 1)
    {
      int j = 0;
      i = 0;
      while (i < arrayOfString.length)
      {
        if (localPackageManager.getLaunchIntentForPackage(arrayOfString[i]) != null) {}
        for (int k = 1;; k = 0)
        {
          j += k;
          if (j <= 1) {
            break;
          }
          return true;
        }
        i += 1;
      }
    }
    arrayOfString = (String[])this.mFacetCategories.get(paramInt);
    int i = 0;
    paramInt = 0;
    while (paramInt < arrayOfString.length)
    {
      String str = arrayOfString[paramInt];
      Intent localIntent = new Intent();
      localIntent.setAction("android.intent.action.MAIN");
      localIntent.addCategory(str);
      i += localPackageManager.queryIntentActivities(localIntent, 0).size();
      if (i > 1) {
        return true;
      }
      paramInt += 1;
    }
    return false;
  }
  
  private String getPackageCategory(String paramString)
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    int k = this.mFacetCategories.size();
    int i = 0;
    while (i < k)
    {
      String[] arrayOfString = (String[])this.mFacetCategories.get(i);
      int j = 0;
      while (j < arrayOfString.length)
      {
        String str = arrayOfString[j];
        Intent localIntent = new Intent();
        localIntent.setPackage(paramString);
        localIntent.setAction("android.intent.action.MAIN");
        localIntent.addCategory(str);
        if (localPackageManager.queryIntentActivities(localIntent, 0).size() > 0)
        {
          this.mFacetPackageMap.put(paramString, (Integer)this.mFacetCategoryMap.get(str));
          return str;
        }
        j += 1;
      }
      i += 1;
    }
    return null;
  }
  
  private void initFacetFilterMaps(int paramInt, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    this.mFacetCategories.add(paramArrayOfString2);
    int i = 0;
    while (i < paramArrayOfString2.length)
    {
      this.mFacetCategoryMap.put(paramArrayOfString2[i], Integer.valueOf(paramInt));
      i += 1;
    }
    this.mFacetPackages.add(paramArrayOfString1);
    i = 0;
    while (i < paramArrayOfString1.length)
    {
      this.mFacetPackageMap.put(paramArrayOfString1[i], Integer.valueOf(paramInt));
      i += 1;
    }
  }
  
  private void onFacetClicked(int paramInt)
  {
    Intent localIntent = (Intent)this.mIntents.get(paramInt);
    if (localIntent.getPackage() == null) {
      return;
    }
    localIntent.putExtra("categories", (String[])this.mFacetCategories.get(paramInt));
    localIntent.putExtra("packages", (String[])this.mFacetPackages.get(paramInt));
    localIntent.putExtra("filter_id", Integer.toString(paramInt));
    if (paramInt == this.mCurrentFacetIndex) {}
    for (boolean bool = true;; bool = false)
    {
      localIntent.putExtra("launch_picker", bool);
      setCurrentFacet(paramInt);
      startActivity(localIntent);
      return;
    }
  }
  
  private void onFacetLongClicked(int paramInt)
  {
    setCurrentFacet(paramInt);
    startActivity((Intent)this.mLongPressIntents.get(paramInt));
  }
  
  private void setCurrentFacet(int paramInt)
  {
    if (paramInt == this.mCurrentFacetIndex) {
      return;
    }
    if (this.mNavButtons.get(this.mCurrentFacetIndex) != null) {
      ((CarNavigationButton)this.mNavButtons.get(this.mCurrentFacetIndex)).setSelected(false, false);
    }
    if (this.mNavButtons.get(paramInt) != null) {
      ((CarNavigationButton)this.mNavButtons.get(paramInt)).setSelected(true, this.mFacetHasMultipleAppsCache.get(paramInt));
    }
    this.mCurrentFacetIndex = paramInt;
  }
  
  private void startActivity(Intent paramIntent)
  {
    if ((this.mActivityStarter != null) && (paramIntent != null)) {
      this.mActivityStarter.startActivity(paramIntent, false);
    }
  }
  
  public void onPackageChange(String paramString)
  {
    int i;
    if (this.mFacetPackageMap.containsKey(paramString))
    {
      i = ((Integer)this.mFacetPackageMap.get(paramString)).intValue();
      this.mFacetHasMultipleAppsCache.put(i, facetHasMultiplePackages(i));
      return;
    }
    paramString = getPackageCategory(paramString);
    if (this.mFacetCategoryMap.containsKey(paramString))
    {
      i = ((Integer)this.mFacetCategoryMap.get(paramString)).intValue();
      this.mFacetHasMultipleAppsCache.put(i, facetHasMultiplePackages(i));
    }
  }
  
  public void taskChanged(String paramString)
  {
    if (this.mFacetPackageMap.containsKey(paramString)) {
      setCurrentFacet(((Integer)this.mFacetPackageMap.get(paramString)).intValue());
    }
    paramString = getPackageCategory(paramString);
    if (paramString != null) {
      setCurrentFacet(((Integer)this.mFacetCategoryMap.get(paramString)).intValue());
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\car\CarNavigationBarController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */