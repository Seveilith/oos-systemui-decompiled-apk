package com.android.systemui.recents.model;

import android.app.ActivityManager;
import android.app.ActivityManager.TaskDescription;
import android.app.ActivityManager.TaskThumbnailInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsConfiguration;
import com.android.systemui.recents.events.activity.PackagesChangedEvent;
import com.android.systemui.recents.misc.SystemServicesProxy;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

public class RecentsTaskLoader
{
  private final LruCache<ComponentName, ActivityInfo> mActivityInfoCache;
  private final TaskKeyLruCache<String> mActivityLabelCache;
  private TaskKeyLruCache.EvictionCallback mClearActivityInfoOnEviction = new TaskKeyLruCache.EvictionCallback()
  {
    public void onEntryEvicted(Task.TaskKey paramAnonymousTaskKey)
    {
      if (paramAnonymousTaskKey != null) {
        RecentsTaskLoader.-get0(RecentsTaskLoader.this).remove(paramAnonymousTaskKey.getComponent());
      }
    }
  };
  private final TaskKeyLruCache<String> mContentDescriptionCache;
  BitmapDrawable mDefaultIcon;
  int mDefaultTaskBarBackgroundColor;
  int mDefaultTaskViewBackgroundColor;
  Bitmap mDefaultThumbnail;
  private final TaskKeyLruCache<Drawable> mIconCache;
  private final TaskResourceLoadQueue mLoadQueue;
  private final BackgroundTaskLoader mLoader;
  private final int mMaxIconCacheSize;
  private final int mMaxThumbnailCacheSize;
  private int mNumVisibleTasksLoaded;
  private int mNumVisibleThumbnailsLoaded;
  private final TaskKeyLruCache<ThumbnailData> mThumbnailCache;
  
  public RecentsTaskLoader(Context paramContext)
  {
    Object localObject = paramContext.getResources();
    this.mDefaultTaskBarBackgroundColor = paramContext.getColor(2131493012);
    this.mDefaultTaskViewBackgroundColor = paramContext.getColor(2131493013);
    this.mMaxThumbnailCacheSize = ((Resources)localObject).getInteger(2131623973);
    this.mMaxIconCacheSize = ((Resources)localObject).getInteger(2131623974);
    int i = this.mMaxIconCacheSize;
    int j = this.mMaxThumbnailCacheSize;
    localObject = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
    ((Bitmap)localObject).eraseColor(0);
    this.mDefaultThumbnail = Bitmap.createBitmap(100, 171, Bitmap.Config.ARGB_8888);
    this.mDefaultThumbnail.setHasAlpha(false);
    this.mDefaultThumbnail.eraseColor(-1);
    this.mDefaultIcon = new BitmapDrawable(paramContext.getResources(), (Bitmap)localObject);
    int k = ActivityManager.getMaxRecentTasksStatic();
    this.mLoadQueue = new TaskResourceLoadQueue();
    this.mIconCache = new TaskKeyLruCache(i, this.mClearActivityInfoOnEviction);
    this.mThumbnailCache = new TaskKeyLruCache(j);
    this.mActivityLabelCache = new TaskKeyLruCache(k, this.mClearActivityInfoOnEviction);
    this.mContentDescriptionCache = new TaskKeyLruCache(k, this.mClearActivityInfoOnEviction);
    this.mActivityInfoCache = new LruCache(k);
    this.mLoader = new BackgroundTaskLoader(this.mLoadQueue, this.mIconCache, this.mThumbnailCache, this.mDefaultThumbnail, this.mDefaultIcon);
  }
  
  private void stopLoader()
  {
    this.mLoader.stop();
    this.mLoadQueue.clearTasks();
  }
  
  public RecentsTaskLoadPlan createLoadPlan(Context paramContext)
  {
    return new RecentsTaskLoadPlan(paramContext);
  }
  
  public void deleteTaskData(Task paramTask, boolean paramBoolean)
  {
    this.mLoadQueue.removeTask(paramTask);
    this.mThumbnailCache.remove(paramTask.key);
    this.mIconCache.remove(paramTask.key);
    this.mActivityLabelCache.remove(paramTask.key);
    this.mContentDescriptionCache.remove(paramTask.key);
    if (paramBoolean) {
      paramTask.notifyTaskDataUnloaded(null, this.mDefaultIcon);
    }
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    String str = paramString + "  ";
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("RecentsTaskLoader");
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("Icon Cache");
    this.mIconCache.dump(str, paramPrintWriter);
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("Thumbnail Cache");
    this.mThumbnailCache.dump(str, paramPrintWriter);
  }
  
  int getActivityBackgroundColor(ActivityManager.TaskDescription paramTaskDescription)
  {
    if ((paramTaskDescription != null) && (paramTaskDescription.getBackgroundColor() != 0)) {
      return paramTaskDescription.getBackgroundColor();
    }
    return this.mDefaultTaskViewBackgroundColor;
  }
  
  int getActivityPrimaryColor(ActivityManager.TaskDescription paramTaskDescription)
  {
    if ((paramTaskDescription != null) && (paramTaskDescription.getPrimaryColor() != 0)) {
      return paramTaskDescription.getPrimaryColor();
    }
    return this.mDefaultTaskBarBackgroundColor;
  }
  
  Drawable getAndUpdateActivityIcon(Task.TaskKey paramTaskKey, ActivityManager.TaskDescription paramTaskDescription, Resources paramResources, boolean paramBoolean)
  {
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    Drawable localDrawable = (Drawable)this.mIconCache.getAndInvalidateIfModified(paramTaskKey);
    if (localDrawable != null) {
      return localDrawable;
    }
    if (paramBoolean)
    {
      paramTaskDescription = localSystemServicesProxy.getBadgedTaskDescriptionIcon(paramTaskDescription, paramTaskKey.userId, paramResources);
      if (paramTaskDescription != null)
      {
        this.mIconCache.put(paramTaskKey, paramTaskDescription);
        return paramTaskDescription;
      }
      paramTaskDescription = getAndUpdateActivityInfo(paramTaskKey);
      if (paramTaskDescription != null)
      {
        paramTaskDescription = localSystemServicesProxy.getBadgedActivityIcon(paramTaskDescription, paramTaskKey.userId);
        if (paramTaskDescription != null)
        {
          this.mIconCache.put(paramTaskKey, paramTaskDescription);
          return paramTaskDescription;
        }
      }
    }
    return null;
  }
  
  ActivityInfo getAndUpdateActivityInfo(Task.TaskKey paramTaskKey)
  {
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    ComponentName localComponentName = paramTaskKey.getComponent();
    ActivityInfo localActivityInfo2 = (ActivityInfo)this.mActivityInfoCache.get(localComponentName);
    ActivityInfo localActivityInfo1 = localActivityInfo2;
    if (localActivityInfo2 == null)
    {
      localActivityInfo1 = localSystemServicesProxy.getActivityInfo(localComponentName, paramTaskKey.userId);
      if ((localComponentName == null) || (localActivityInfo1 == null))
      {
        Log.e("RecentsTaskLoader", "Unexpected null component name or activity info: " + localComponentName + ", " + localActivityInfo1);
        return null;
      }
      this.mActivityInfoCache.put(localComponentName, localActivityInfo1);
    }
    return localActivityInfo1;
  }
  
  String getAndUpdateActivityTitle(Task.TaskKey paramTaskKey, ActivityManager.TaskDescription paramTaskDescription)
  {
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    if ((paramTaskDescription != null) && (paramTaskDescription.getLabel() != null)) {
      return paramTaskDescription.getLabel();
    }
    paramTaskDescription = (String)this.mActivityLabelCache.getAndInvalidateIfModified(paramTaskKey);
    if (paramTaskDescription != null) {
      return paramTaskDescription;
    }
    paramTaskDescription = getAndUpdateActivityInfo(paramTaskKey);
    if (paramTaskDescription != null)
    {
      paramTaskDescription = localSystemServicesProxy.getBadgedActivityLabel(paramTaskDescription, paramTaskKey.userId);
      if (Build.DEBUG_ONEPLUS) {
        Log.d("RecentsTaskLoader", "label =" + paramTaskDescription);
      }
      this.mActivityLabelCache.put(paramTaskKey, paramTaskDescription);
      return paramTaskDescription;
    }
    return "";
  }
  
  String getAndUpdateContentDescription(Task.TaskKey paramTaskKey, Resources paramResources)
  {
    SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
    Object localObject = (String)this.mContentDescriptionCache.getAndInvalidateIfModified(paramTaskKey);
    if (localObject != null) {
      return (String)localObject;
    }
    localObject = getAndUpdateActivityInfo(paramTaskKey);
    if (localObject != null)
    {
      paramResources = localSystemServicesProxy.getBadgedContentDescription((ActivityInfo)localObject, paramTaskKey.userId, paramResources);
      this.mContentDescriptionCache.put(paramTaskKey, paramResources);
      return paramResources;
    }
    return "";
  }
  
  Bitmap getAndUpdateThumbnail(Task.TaskKey paramTaskKey, boolean paramBoolean)
  {
    Object localObject = Recents.getSystemServices();
    ThumbnailData localThumbnailData = (ThumbnailData)this.mThumbnailCache.getAndInvalidateIfModified(paramTaskKey);
    if (localThumbnailData != null) {
      return localThumbnailData.thumbnail;
    }
    if ((paramBoolean) && (Recents.getConfiguration().svelteLevel < 3))
    {
      localObject = ((SystemServicesProxy)localObject).getTaskThumbnail(paramTaskKey.id, paramTaskKey.isTopAppLocked);
      if (((ThumbnailData)localObject).thumbnail != null)
      {
        this.mThumbnailCache.put(paramTaskKey, localObject);
        return ((ThumbnailData)localObject).thumbnail;
      }
    }
    return null;
  }
  
  public Bitmap getDefaultThumbnail()
  {
    return this.mDefaultThumbnail;
  }
  
  public int getIconCacheSize()
  {
    return this.mMaxIconCacheSize;
  }
  
  public int getThumbnailCacheSize()
  {
    return this.mMaxThumbnailCacheSize;
  }
  
  public void loadTaskData(Task paramTask)
  {
    Object localObject1 = (Drawable)this.mIconCache.getAndInvalidateIfModified(paramTask.key);
    Bitmap localBitmap = null;
    ActivityManager.TaskThumbnailInfo localTaskThumbnailInfo = null;
    Object localObject2 = (ThumbnailData)this.mThumbnailCache.getAndInvalidateIfModified(paramTask.key);
    if (localObject2 != null)
    {
      localBitmap = ((ThumbnailData)localObject2).thumbnail;
      localTaskThumbnailInfo = ((ThumbnailData)localObject2).thumbnailInfo;
    }
    int i;
    if ((localObject1 == null) || (localBitmap == null))
    {
      i = 1;
      if (localObject1 == null) {
        break label113;
      }
    }
    for (;;)
    {
      if (i != 0) {
        this.mLoadQueue.addTask(paramTask);
      }
      localObject2 = localBitmap;
      if (localBitmap == this.mDefaultThumbnail) {
        localObject2 = null;
      }
      paramTask.notifyTaskDataLoaded((Bitmap)localObject2, (Drawable)localObject1, localTaskThumbnailInfo);
      return;
      i = 0;
      break;
      label113:
      localObject1 = this.mDefaultIcon;
    }
  }
  
  public void loadTasks(Context paramContext, RecentsTaskLoadPlan paramRecentsTaskLoadPlan, RecentsTaskLoadPlan.Options paramOptions)
  {
    if (paramOptions == null) {
      throw new RuntimeException("Requires load options");
    }
    paramRecentsTaskLoadPlan.executePlan(paramOptions, this, this.mLoadQueue);
    if (!paramOptions.onlyLoadForCache)
    {
      this.mNumVisibleTasksLoaded = paramOptions.numVisibleTasks;
      this.mNumVisibleThumbnailsLoaded = paramOptions.numVisibleTaskThumbnails;
      this.mLoader.start(paramContext);
    }
  }
  
  public final void onBusEvent(PackagesChangedEvent paramPackagesChangedEvent)
  {
    Iterator localIterator = this.mActivityInfoCache.snapshot().keySet().iterator();
    while (localIterator.hasNext())
    {
      ComponentName localComponentName = (ComponentName)localIterator.next();
      if (localComponentName.getPackageName().equals(paramPackagesChangedEvent.packageName)) {
        this.mActivityInfoCache.remove(localComponentName);
      }
    }
  }
  
  public void onTrimMemory(int paramInt)
  {
    RecentsConfiguration localRecentsConfiguration = Recents.getConfiguration();
    switch (paramInt)
    {
    default: 
      return;
    case 20: 
      stopLoader();
      if (localRecentsConfiguration.svelteLevel == 0) {
        this.mThumbnailCache.trimToSize(Math.max(this.mNumVisibleTasksLoaded, this.mMaxThumbnailCacheSize / 2));
      }
      for (;;)
      {
        this.mIconCache.trimToSize(Math.max(this.mNumVisibleTasksLoaded, this.mMaxIconCacheSize / 2));
        return;
        if (localRecentsConfiguration.svelteLevel == 1) {
          this.mThumbnailCache.trimToSize(this.mNumVisibleThumbnailsLoaded);
        } else if (localRecentsConfiguration.svelteLevel >= 2) {
          this.mThumbnailCache.evictAll();
        }
      }
    case 5: 
    case 40: 
      this.mThumbnailCache.trimToSize(Math.max(1, this.mMaxThumbnailCacheSize / 2));
      this.mIconCache.trimToSize(Math.max(1, this.mMaxIconCacheSize / 2));
      this.mActivityInfoCache.trimToSize(Math.max(1, ActivityManager.getMaxRecentTasksStatic() / 2));
      return;
    case 10: 
    case 60: 
      this.mThumbnailCache.trimToSize(Math.max(1, this.mMaxThumbnailCacheSize / 4));
      this.mIconCache.trimToSize(Math.max(1, this.mMaxIconCacheSize / 4));
      this.mActivityInfoCache.trimToSize(Math.max(1, ActivityManager.getMaxRecentTasksStatic() / 4));
      return;
    }
    this.mThumbnailCache.evictAll();
    this.mIconCache.evictAll();
    this.mActivityInfoCache.evictAll();
    this.mActivityLabelCache.evictAll();
    this.mContentDescriptionCache.evictAll();
  }
  
  public void preloadTasks(RecentsTaskLoadPlan paramRecentsTaskLoadPlan, int paramInt, boolean paramBoolean)
  {
    paramRecentsTaskLoadPlan.preloadPlan(this, paramInt, paramBoolean);
  }
  
  public void unloadTaskData(Task paramTask)
  {
    this.mLoadQueue.removeTask(paramTask);
    paramTask.notifyTaskDataUnloaded(null, this.mDefaultIcon);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\model\RecentsTaskLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */