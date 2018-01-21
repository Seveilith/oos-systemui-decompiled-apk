package com.android.systemui.recents.model;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsConfiguration;
import com.android.systemui.recents.misc.SystemServicesProxy;

class BackgroundTaskLoader
  implements Runnable
{
  static boolean DEBUG = false;
  static String TAG = "TaskResourceLoader";
  boolean mCancelled;
  Context mContext;
  BitmapDrawable mDefaultIcon;
  Bitmap mDefaultThumbnail;
  TaskKeyLruCache<Drawable> mIconCache;
  TaskResourceLoadQueue mLoadQueue;
  HandlerThread mLoadThread;
  Handler mLoadThreadHandler;
  Handler mMainThreadHandler;
  TaskKeyLruCache<ThumbnailData> mThumbnailCache;
  boolean mWaitingOnLoadQueue;
  
  public BackgroundTaskLoader(TaskResourceLoadQueue paramTaskResourceLoadQueue, TaskKeyLruCache<Drawable> paramTaskKeyLruCache, TaskKeyLruCache<ThumbnailData> paramTaskKeyLruCache1, Bitmap paramBitmap, BitmapDrawable paramBitmapDrawable)
  {
    this.mLoadQueue = paramTaskResourceLoadQueue;
    this.mIconCache = paramTaskKeyLruCache;
    this.mThumbnailCache = paramTaskKeyLruCache1;
    this.mDefaultThumbnail = paramBitmap;
    this.mDefaultIcon = paramBitmapDrawable;
    this.mMainThreadHandler = new Handler();
    this.mLoadThread = new HandlerThread("Recents-TaskResourceLoader", 10);
    this.mLoadThread.start();
    this.mLoadThreadHandler = new Handler(this.mLoadThread.getLooper());
    this.mLoadThreadHandler.post(this);
  }
  
  public void run()
  {
    for (;;)
    {
      if (this.mCancelled)
      {
        this.mContext = null;
        synchronized (this.mLoadThread)
        {
          try
          {
            this.mLoadThread.wait();
          }
          catch (InterruptedException localInterruptedException1)
          {
            for (;;)
            {
              localInterruptedException1.printStackTrace();
            }
          }
        }
      }
      RecentsConfiguration localRecentsConfiguration = Recents.getConfiguration();
      SystemServicesProxy localSystemServicesProxy = Recents.getSystemServices();
      if (localSystemServicesProxy != null)
      {
        final Task localTask = this.mLoadQueue.nextTask();
        if (localTask != null)
        {
          ??? = (Drawable)this.mIconCache.get(localTask.key);
          ThumbnailData localThumbnailData = (ThumbnailData)this.mThumbnailCache.get(localTask.key);
          final Object localObject3 = ???;
          if (??? == null)
          {
            localObject3 = localSystemServicesProxy.getBadgedTaskDescriptionIcon(localTask.taskDescription, localTask.key.userId, this.mContext.getResources());
            ??? = localObject3;
            if (localObject3 == null)
            {
              localObject5 = localSystemServicesProxy.getActivityInfo(localTask.key.getComponent(), localTask.key.userId);
              ??? = localObject3;
              if (localObject5 != null)
              {
                if (DEBUG) {
                  Log.d(TAG, "Loading icon: " + localTask.key);
                }
                ??? = localSystemServicesProxy.getBadgedActivityIcon((ActivityInfo)localObject5, localTask.key.userId);
              }
            }
            localObject3 = ???;
            if (??? == null) {
              localObject3 = this.mDefaultIcon;
            }
            this.mIconCache.put(localTask.key, localObject3);
          }
          final Object localObject5 = localThumbnailData;
          if (localThumbnailData == null)
          {
            ??? = localThumbnailData;
            if (localRecentsConfiguration.svelteLevel < 3)
            {
              if (DEBUG) {
                Log.d(TAG, "Loading thumbnail: " + localTask.key);
              }
              ??? = localSystemServicesProxy.getTaskThumbnail(localTask.key.id, localTask.key.isTopAppLocked);
            }
            if (((ThumbnailData)???).thumbnail != null) {
              break label455;
            }
            ((ThumbnailData)???).thumbnail = this.mDefaultThumbnail;
            label340:
            localObject5 = ???;
            if (localRecentsConfiguration.svelteLevel < 1)
            {
              this.mThumbnailCache.put(localTask.key, ???);
              localObject5 = ???;
            }
          }
          if (!this.mCancelled) {
            this.mMainThreadHandler.post(new Runnable()
            {
              public void run()
              {
                localTask.notifyTaskDataLoaded(localObject5.thumbnail, localObject3, localObject5.thumbnailInfo);
              }
            });
          }
        }
      }
      if ((this.mCancelled) || (!this.mLoadQueue.isEmpty())) {
        continue;
      }
      synchronized (this.mLoadQueue)
      {
        try
        {
          this.mWaitingOnLoadQueue = true;
          while (this.mLoadQueue.isEmpty()) {
            this.mLoadQueue.wait();
          }
        }
        catch (InterruptedException localInterruptedException2)
        {
          localInterruptedException2.printStackTrace();
        }
        continue;
        label455:
        ((ThumbnailData)???).thumbnail.prepareToDraw();
        break label340;
        this.mWaitingOnLoadQueue = false;
      }
    }
  }
  
  void start(Context arg1)
  {
    this.mContext = ???;
    this.mCancelled = false;
    synchronized (this.mLoadThread)
    {
      this.mLoadThread.notifyAll();
      return;
    }
  }
  
  void stop()
  {
    this.mCancelled = true;
    if (this.mWaitingOnLoadQueue) {
      this.mContext = null;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\model\BackgroundTaskLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */