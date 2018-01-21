package com.android.systemui.statusbar.phone;

import android.app.ActivityManager;
import android.app.IWallpaperManager;
import android.app.IWallpaperManager.Stub;
import android.app.IWallpaperManagerCallback.Stub;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.DrawableWrapper;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.plugin.LSState;
import libcore.io.IoUtils;

public class LockscreenWallpaper
  extends IWallpaperManagerCallback.Stub
  implements Runnable
{
  private final PhoneStatusBar mBar;
  private Bitmap mCache;
  private boolean mCached;
  private int mCurrentUserId;
  private final Handler mH;
  private AsyncTask<Void, Void, LoaderResult> mLoader;
  private UserHandle mSelectedUser;
  private final KeyguardUpdateMonitor mUpdateMonitor;
  private final WallpaperManager mWallpaperManager;
  
  public LockscreenWallpaper(Context paramContext, PhoneStatusBar paramPhoneStatusBar, Handler paramHandler)
  {
    this.mBar = paramPhoneStatusBar;
    this.mH = paramHandler;
    this.mWallpaperManager = ((WallpaperManager)paramContext.getSystemService("wallpaper"));
    this.mCurrentUserId = ActivityManager.getCurrentUser();
    this.mUpdateMonitor = KeyguardUpdateMonitor.getInstance(paramContext);
    paramContext = IWallpaperManager.Stub.asInterface(ServiceManager.getService("wallpaper"));
    try
    {
      paramContext.setLockWallpaperCallback(this);
      return;
    }
    catch (RemoteException paramContext)
    {
      Log.e("LockscreenWallpaper", "System dead?" + paramContext);
    }
  }
  
  private void postUpdateWallpaper()
  {
    this.mH.removeCallbacks(this);
    this.mH.post(this);
  }
  
  public Bitmap getBitmap()
  {
    boolean bool = true;
    if (this.mCached) {
      return this.mCache;
    }
    if (!this.mWallpaperManager.isWallpaperSupported())
    {
      this.mCached = true;
      this.mCache = null;
      return null;
    }
    LoaderResult localLoaderResult = loadBitmap(this.mCurrentUserId, this.mSelectedUser);
    KeyguardUpdateMonitor localKeyguardUpdateMonitor;
    if (localLoaderResult.success)
    {
      this.mCached = true;
      localKeyguardUpdateMonitor = this.mUpdateMonitor;
      if (localLoaderResult.bitmap == null) {
        break label91;
      }
    }
    for (;;)
    {
      localKeyguardUpdateMonitor.setHasLockscreenWallpaper(bool);
      this.mCache = localLoaderResult.bitmap;
      return this.mCache;
      label91:
      bool = false;
    }
  }
  
  public LoaderResult loadBitmap(int paramInt, UserHandle paramUserHandle)
  {
    if (paramUserHandle != null) {}
    for (int i = paramUserHandle.getIdentifier();; i = paramInt)
    {
      ParcelFileDescriptor localParcelFileDescriptor = this.mWallpaperManager.getWallpaperFile(2, i);
      if (localParcelFileDescriptor == null) {
        break;
      }
      try
      {
        paramUserHandle = new BitmapFactory.Options();
        paramUserHandle = LoaderResult.success(BitmapFactory.decodeFileDescriptor(localParcelFileDescriptor.getFileDescriptor(), null, paramUserHandle));
        return paramUserHandle;
      }
      catch (OutOfMemoryError paramUserHandle)
      {
        Log.w("LockscreenWallpaper", "Can't decode file", paramUserHandle);
        paramUserHandle = LoaderResult.fail();
        return paramUserHandle;
      }
      finally
      {
        IoUtils.closeQuietly(localParcelFileDescriptor);
      }
    }
    if ((paramUserHandle != null) && (paramUserHandle.getIdentifier() != paramInt)) {
      return LoaderResult.success(this.mWallpaperManager.getBitmapAsUser(paramUserHandle.getIdentifier()));
    }
    return LoaderResult.success(null);
  }
  
  public void onWallpaperChanged()
  {
    postUpdateWallpaper();
  }
  
  public void run()
  {
    if (this.mLoader != null) {
      this.mLoader.cancel(false);
    }
    this.mLoader = new AsyncTask()
    {
      protected LockscreenWallpaper.LoaderResult doInBackground(Void... paramAnonymousVarArgs)
      {
        return LockscreenWallpaper.this.loadBitmap(this.val$currentUser, this.val$selectedUser);
      }
      
      protected void onPostExecute(LockscreenWallpaper.LoaderResult paramAnonymousLoaderResult)
      {
        super.onPostExecute(paramAnonymousLoaderResult);
        if (isCancelled()) {
          return;
        }
        KeyguardUpdateMonitor localKeyguardUpdateMonitor;
        if (paramAnonymousLoaderResult.success)
        {
          LockscreenWallpaper.-set1(LockscreenWallpaper.this, true);
          LockscreenWallpaper.-set0(LockscreenWallpaper.this, paramAnonymousLoaderResult.bitmap);
          localKeyguardUpdateMonitor = LockscreenWallpaper.-get1(LockscreenWallpaper.this);
          if (paramAnonymousLoaderResult.bitmap == null) {
            break label98;
          }
        }
        label98:
        for (boolean bool = true;; bool = false)
        {
          localKeyguardUpdateMonitor.setHasLockscreenWallpaper(bool);
          LockscreenWallpaper.-get0(LockscreenWallpaper.this).updateMediaMetaData(true, true);
          LockscreenWallpaper.-set2(LockscreenWallpaper.this, null);
          LSState.getInstance().onWallpaperChange(LockscreenWallpaper.this.getBitmap());
          return;
        }
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }
  
  public void setCurrentUser(int paramInt)
  {
    if (paramInt != this.mCurrentUserId)
    {
      this.mCached = false;
      this.mCurrentUserId = paramInt;
      onWallpaperChanged();
    }
  }
  
  private static class LoaderResult
  {
    public final Bitmap bitmap;
    public final boolean success;
    
    LoaderResult(boolean paramBoolean, Bitmap paramBitmap)
    {
      this.success = paramBoolean;
      this.bitmap = paramBitmap;
    }
    
    static LoaderResult fail()
    {
      return new LoaderResult(false, null);
    }
    
    static LoaderResult success(Bitmap paramBitmap)
    {
      return new LoaderResult(true, paramBitmap);
    }
  }
  
  public static class WallpaperDrawable
    extends DrawableWrapper
  {
    private final ConstantState mState;
    private final Rect mTmpRect = new Rect();
    
    public WallpaperDrawable(Resources paramResources, Bitmap paramBitmap)
    {
      this(paramResources, new ConstantState(paramBitmap));
    }
    
    private WallpaperDrawable(Resources paramResources, ConstantState paramConstantState)
    {
      super();
      this.mState = paramConstantState;
    }
    
    public ConstantState getConstantState()
    {
      return this.mState;
    }
    
    public int getIntrinsicHeight()
    {
      return -1;
    }
    
    public int getIntrinsicWidth()
    {
      return -1;
    }
    
    protected void onBoundsChange(Rect paramRect)
    {
      int i = getBounds().width();
      int j = getBounds().height();
      int k = ConstantState.-get0(this.mState).getWidth();
      int m = ConstantState.-get0(this.mState).getHeight();
      if (k * j > i * m) {}
      for (float f1 = j / m;; f1 = i / k)
      {
        float f2 = f1;
        if (f1 <= 1.0F) {
          f2 = 1.0F;
        }
        f1 = (j - m * f2) * 0.5F;
        this.mTmpRect.set(paramRect.left, paramRect.top + Math.round(f1), paramRect.left + Math.round(k * f2), paramRect.top + Math.round(m * f2 + f1));
        super.onBoundsChange(this.mTmpRect);
        return;
      }
    }
    
    public void setXfermode(Xfermode paramXfermode)
    {
      getDrawable().setXfermode(paramXfermode);
    }
    
    static class ConstantState
      extends Drawable.ConstantState
    {
      private final Bitmap mBackground;
      
      ConstantState(Bitmap paramBitmap)
      {
        this.mBackground = paramBitmap;
      }
      
      public int getChangingConfigurations()
      {
        return 0;
      }
      
      public Drawable newDrawable()
      {
        return newDrawable(null);
      }
      
      public Drawable newDrawable(Resources paramResources)
      {
        return new LockscreenWallpaper.WallpaperDrawable(paramResources, this, null);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\LockscreenWallpaper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */