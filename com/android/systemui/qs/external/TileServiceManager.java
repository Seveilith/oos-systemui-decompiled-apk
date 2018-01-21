package com.android.systemui.qs.external;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.UserHandle;
import android.service.quicksettings.IQSTileService;
import android.service.quicksettings.Tile;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.android.systemui.statusbar.phone.QSTileHost;
import java.util.Iterator;
import libcore.util.Objects;

public class TileServiceManager
{
  @VisibleForTesting
  static final String PREFS_FILE = "CustomTileModes";
  private boolean mBindAllowed;
  private boolean mBindRequested;
  private boolean mBound;
  private final Handler mHandler;
  private boolean mJustBound;
  @VisibleForTesting
  final Runnable mJustBoundOver = new Runnable()
  {
    public void run()
    {
      TileServiceManager.-set0(TileServiceManager.this, false);
      TileServiceManager.-get2(TileServiceManager.this).recalculateBindAllowance();
    }
  };
  private long mLastUpdate;
  private boolean mPendingBind = true;
  private int mPriority;
  private final TileServices mServices;
  private boolean mShowingDialog;
  private final TileLifecycleManager mStateManager;
  private final Runnable mUnbind = new Runnable()
  {
    public void run()
    {
      if ((!TileServiceManager.-get1(TileServiceManager.this)) || (TileServiceManager.-get0(TileServiceManager.this))) {
        return;
      }
      TileServiceManager.-wrap0(TileServiceManager.this);
    }
  };
  private final BroadcastReceiver mUninstallReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (!"android.intent.action.PACKAGE_REMOVED".equals(paramAnonymousIntent.getAction())) {
        return;
      }
      String str = paramAnonymousIntent.getData().getEncodedSchemeSpecificPart();
      ComponentName localComponentName = TileServiceManager.-get3(TileServiceManager.this).getComponent();
      if (!Objects.equal(str, localComponentName.getPackageName())) {
        return;
      }
      if (paramAnonymousIntent.getBooleanExtra("android.intent.extra.REPLACING", false))
      {
        paramAnonymousIntent = new Intent("android.service.quicksettings.action.QS_TILE");
        paramAnonymousIntent.setPackage(str);
        paramAnonymousContext = paramAnonymousContext.getPackageManager().queryIntentServicesAsUser(paramAnonymousIntent, 0, ActivityManager.getCurrentUser()).iterator();
        while (paramAnonymousContext.hasNext())
        {
          paramAnonymousIntent = (ResolveInfo)paramAnonymousContext.next();
          if ((Objects.equal(paramAnonymousIntent.serviceInfo.packageName, localComponentName.getPackageName())) && (Objects.equal(paramAnonymousIntent.serviceInfo.name, localComponentName.getClassName()))) {
            return;
          }
        }
      }
      TileServiceManager.-get2(TileServiceManager.this).getHost().removeTile(localComponentName);
    }
  };
  
  TileServiceManager(TileServices paramTileServices, Handler paramHandler, ComponentName paramComponentName, Tile paramTile)
  {
    this(paramTileServices, paramHandler, new TileLifecycleManager(paramHandler, paramTileServices.getContext(), paramTileServices, paramTile, new Intent().setComponent(paramComponentName), new UserHandle(ActivityManager.getCurrentUser())));
  }
  
  @VisibleForTesting
  TileServiceManager(TileServices paramTileServices, Handler paramHandler, TileLifecycleManager paramTileLifecycleManager)
  {
    this.mServices = paramTileServices;
    this.mHandler = paramHandler;
    this.mStateManager = paramTileLifecycleManager;
    paramHandler = new IntentFilter();
    paramHandler.addAction("android.intent.action.PACKAGE_REMOVED");
    paramHandler.addDataScheme("package");
    paramTileServices = this.mServices.getContext();
    paramTileServices.registerReceiverAsUser(this.mUninstallReceiver, new UserHandle(ActivityManager.getCurrentUser()), paramHandler, null, this.mHandler);
    paramHandler = paramTileLifecycleManager.getComponent();
    if (!TileLifecycleManager.isTileAdded(paramTileServices, paramHandler))
    {
      TileLifecycleManager.setTileAdded(paramTileServices, paramHandler, true);
      this.mStateManager.onTileAdded();
      this.mStateManager.flushMessagesAndUnbind();
    }
  }
  
  private void bindService()
  {
    if (this.mBound)
    {
      Log.e("TileServiceManager", "Service already bound");
      return;
    }
    this.mPendingBind = true;
    this.mBound = true;
    this.mJustBound = true;
    this.mHandler.postDelayed(this.mJustBoundOver, 5000L);
    this.mStateManager.setBindService(true);
  }
  
  private void unbindService()
  {
    if (!this.mBound)
    {
      Log.e("TileServiceManager", "Service not bound");
      return;
    }
    this.mBound = false;
    this.mJustBound = false;
    this.mStateManager.setBindService(false);
  }
  
  public void calculateBindPriority(long paramLong)
  {
    if (this.mStateManager.hasPendingClick())
    {
      this.mPriority = Integer.MAX_VALUE;
      return;
    }
    if (this.mShowingDialog)
    {
      this.mPriority = 2147483646;
      return;
    }
    if (this.mJustBound)
    {
      this.mPriority = 2147483645;
      return;
    }
    if (!this.mBindRequested)
    {
      this.mPriority = Integer.MIN_VALUE;
      return;
    }
    paramLong -= this.mLastUpdate;
    if (paramLong > 2147483644L)
    {
      this.mPriority = 2147483644;
      return;
    }
    this.mPriority = ((int)paramLong);
  }
  
  public void clearPendingBind()
  {
    this.mPendingBind = false;
  }
  
  public int getBindPriority()
  {
    return this.mPriority;
  }
  
  public IQSTileService getTileService()
  {
    return this.mStateManager;
  }
  
  public IBinder getToken()
  {
    return this.mStateManager.getToken();
  }
  
  public void handleDestroy()
  {
    this.mServices.getContext().unregisterReceiver(this.mUninstallReceiver);
    this.mStateManager.handleDestroy();
  }
  
  public boolean hasPendingBind()
  {
    return this.mPendingBind;
  }
  
  public boolean isActiveTile()
  {
    return this.mStateManager.isActiveTile();
  }
  
  public void setBindAllowed(boolean paramBoolean)
  {
    if (this.mBindAllowed == paramBoolean) {
      return;
    }
    this.mBindAllowed = paramBoolean;
    if ((!this.mBindAllowed) && (this.mBound)) {
      unbindService();
    }
    while ((!this.mBindAllowed) || (!this.mBindRequested) || (this.mBound)) {
      return;
    }
    bindService();
  }
  
  public void setBindRequested(boolean paramBoolean)
  {
    if (this.mBindRequested == paramBoolean) {
      return;
    }
    this.mBindRequested = paramBoolean;
    if ((!this.mBindAllowed) || (!this.mBindRequested) || (this.mBound)) {
      this.mServices.recalculateBindAllowance();
    }
    while ((!this.mBound) || (this.mBindRequested))
    {
      return;
      this.mHandler.removeCallbacks(this.mUnbind);
      bindService();
    }
    this.mHandler.postDelayed(this.mUnbind, 30000L);
  }
  
  public void setLastUpdate(long paramLong)
  {
    this.mLastUpdate = paramLong;
    if ((this.mBound) && (isActiveTile()))
    {
      this.mStateManager.onStopListening();
      setBindRequested(false);
    }
    this.mServices.recalculateBindAllowance();
  }
  
  public void setShowingDialog(boolean paramBoolean)
  {
    this.mShowingDialog = paramBoolean;
  }
  
  public void setTileChangeListener(TileLifecycleManager.TileChangeListener paramTileChangeListener)
  {
    this.mStateManager.setTileChangeListener(paramTileChangeListener);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\external\TileServiceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */