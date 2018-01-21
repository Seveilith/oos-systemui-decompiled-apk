package com.android.systemui.qs.external;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Icon;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.quicksettings.IQSService.Stub;
import android.service.quicksettings.IQSTileService;
import android.service.quicksettings.Tile;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.statusbar.phone.QSTileHost;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TileServices
  extends IQSService.Stub
{
  private static final Comparator<TileServiceManager> SERVICE_SORT = new Comparator()
  {
    public int compare(TileServiceManager paramAnonymousTileServiceManager1, TileServiceManager paramAnonymousTileServiceManager2)
    {
      return -Integer.compare(paramAnonymousTileServiceManager1.getBindPriority(), paramAnonymousTileServiceManager2.getBindPriority());
    }
  };
  private final Context mContext;
  private final Handler mHandler;
  private final QSTileHost mHost;
  private final Handler mMainHandler;
  private int mMaxBound = 3;
  private final BroadcastReceiver mRequestListeningReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.service.quicksettings.action.REQUEST_LISTENING".equals(paramAnonymousIntent.getAction())) {
        TileServices.-wrap0(TileServices.this, (ComponentName)paramAnonymousIntent.getParcelableExtra("android.service.quicksettings.extra.COMPONENT"));
      }
    }
  };
  private final ArrayMap<CustomTile, TileServiceManager> mServices = new ArrayMap();
  private final ArrayMap<ComponentName, CustomTile> mTiles = new ArrayMap();
  private final ArrayMap<IBinder, CustomTile> mTokenMap = new ArrayMap();
  
  public TileServices(QSTileHost paramQSTileHost, Looper paramLooper)
  {
    this.mHost = paramQSTileHost;
    this.mContext = this.mHost.getContext();
    this.mContext.registerReceiver(this.mRequestListeningReceiver, new IntentFilter("android.service.quicksettings.action.REQUEST_LISTENING"));
    this.mHandler = new Handler(paramLooper);
    this.mMainHandler = new Handler(Looper.getMainLooper());
  }
  
  private CustomTile getTileForComponent(ComponentName paramComponentName)
  {
    synchronized (this.mServices)
    {
      paramComponentName = (CustomTile)this.mTiles.get(paramComponentName);
      return paramComponentName;
    }
  }
  
  private CustomTile getTileForToken(IBinder paramIBinder)
  {
    synchronized (this.mServices)
    {
      paramIBinder = (CustomTile)this.mTokenMap.get(paramIBinder);
      return paramIBinder;
    }
  }
  
  private void requestListening(ComponentName paramComponentName)
  {
    synchronized (this.mServices)
    {
      CustomTile localCustomTile = getTileForComponent(paramComponentName);
      if (localCustomTile == null)
      {
        Log.d("TileServices", "Couldn't find tile for " + paramComponentName);
        return;
      }
      paramComponentName = (TileServiceManager)this.mServices.get(localCustomTile);
      boolean bool = paramComponentName.isActiveTile();
      if (!bool) {
        return;
      }
      paramComponentName.setBindRequested(true);
    }
    try
    {
      paramComponentName.getTileService().onStartListening();
      return;
      paramComponentName = finally;
      throw paramComponentName;
    }
    catch (RemoteException paramComponentName)
    {
      for (;;) {}
    }
  }
  
  private void verifyCaller(CustomTile paramCustomTile)
  {
    try
    {
      paramCustomTile = paramCustomTile.getComponent().getPackageName();
      int i = this.mContext.getPackageManager().getPackageUidAsUser(paramCustomTile, Binder.getCallingUserHandle().getIdentifier());
      if (Binder.getCallingUid() != i) {
        throw new SecurityException("Component outside caller's uid");
      }
    }
    catch (PackageManager.NameNotFoundException paramCustomTile)
    {
      throw new SecurityException(paramCustomTile);
    }
  }
  
  public void freeService(final CustomTile paramCustomTile, TileServiceManager paramTileServiceManager)
  {
    synchronized (this.mServices)
    {
      paramTileServiceManager.setBindAllowed(false);
      paramTileServiceManager.handleDestroy();
      this.mServices.remove(paramCustomTile);
      this.mTokenMap.remove(paramTileServiceManager.getToken());
      this.mTiles.remove(paramCustomTile.getComponent());
      paramCustomTile = paramCustomTile.getComponent().getClassName();
      this.mMainHandler.post(new Runnable()
      {
        public void run()
        {
          TileServices.-get0(TileServices.this).getIconController().removeIcon(paramCustomTile);
        }
      });
      return;
    }
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  public QSTileHost getHost()
  {
    return this.mHost;
  }
  
  public Tile getTile(IBinder paramIBinder)
  {
    paramIBinder = getTileForToken(paramIBinder);
    if (paramIBinder != null)
    {
      verifyCaller(paramIBinder);
      return paramIBinder.getQsTile();
    }
    return null;
  }
  
  public TileServiceManager getTileWrapper(CustomTile paramCustomTile)
  {
    ComponentName localComponentName = paramCustomTile.getComponent();
    TileServiceManager localTileServiceManager = onCreateTileService(localComponentName, paramCustomTile.getQsTile());
    synchronized (this.mServices)
    {
      this.mServices.put(paramCustomTile, localTileServiceManager);
      this.mTiles.put(localComponentName, paramCustomTile);
      this.mTokenMap.put(localTileServiceManager.getToken(), paramCustomTile);
      return localTileServiceManager;
    }
  }
  
  public boolean isLocked()
  {
    return this.mHost.getKeyguardMonitor().isShowing();
  }
  
  public boolean isSecure()
  {
    KeyguardMonitor localKeyguardMonitor = this.mHost.getKeyguardMonitor();
    if (localKeyguardMonitor.isSecure()) {
      return localKeyguardMonitor.isShowing();
    }
    return false;
  }
  
  protected TileServiceManager onCreateTileService(ComponentName paramComponentName, Tile paramTile)
  {
    return new TileServiceManager(this, this.mHandler, paramComponentName, paramTile);
  }
  
  public void onDialogHidden(IBinder paramIBinder)
  {
    paramIBinder = getTileForToken(paramIBinder);
    if (paramIBinder != null)
    {
      verifyCaller(paramIBinder);
      ((TileServiceManager)this.mServices.get(paramIBinder)).setShowingDialog(false);
      paramIBinder.onDialogHidden();
    }
  }
  
  public void onShowDialog(IBinder paramIBinder)
  {
    paramIBinder = getTileForToken(paramIBinder);
    if (paramIBinder != null)
    {
      verifyCaller(paramIBinder);
      paramIBinder.onDialogShown();
      this.mHost.collapsePanels();
      ((TileServiceManager)this.mServices.get(paramIBinder)).setShowingDialog(true);
    }
  }
  
  public void onStartActivity(IBinder paramIBinder)
  {
    paramIBinder = getTileForToken(paramIBinder);
    if (paramIBinder != null)
    {
      verifyCaller(paramIBinder);
      this.mHost.collapsePanels();
    }
  }
  
  public void onStartSuccessful(IBinder arg1)
  {
    CustomTile localCustomTile = getTileForToken(???);
    if (localCustomTile != null) {
      verifyCaller(localCustomTile);
    }
    synchronized (this.mServices)
    {
      ((TileServiceManager)this.mServices.get(localCustomTile)).clearPendingBind();
      localCustomTile.refreshState();
      return;
    }
  }
  
  public void recalculateBindAllowance()
  {
    int k;
    synchronized (this.mServices)
    {
      ArrayList localArrayList = new ArrayList(this.mServices.values());
      k = localArrayList.size();
      if (k > this.mMaxBound)
      {
        long l = System.currentTimeMillis();
        i = 0;
        if (i < k)
        {
          ((TileServiceManager)localArrayList.get(i)).calculateBindPriority(l);
          i += 1;
        }
      }
    }
    int i = 0;
    int j;
    for (;;)
    {
      j = i;
      if (i >= this.mMaxBound) {
        break;
      }
      j = i;
      if (i >= k) {
        break;
      }
      ((TileServiceManager)localList.get(i)).setBindAllowed(true);
      i += 1;
    }
    while (j < k)
    {
      ((TileServiceManager)localList.get(j)).setBindAllowed(false);
      j += 1;
    }
  }
  
  public void startUnlockAndRun(IBinder paramIBinder)
  {
    paramIBinder = getTileForToken(paramIBinder);
    if (paramIBinder != null)
    {
      verifyCaller(paramIBinder);
      paramIBinder.startUnlockAndRun();
    }
  }
  
  public void updateQsTile(Tile paramTile, IBinder arg2)
  {
    CustomTile localCustomTile = getTileForToken(???);
    if (localCustomTile != null) {
      verifyCaller(localCustomTile);
    }
    synchronized (this.mServices)
    {
      TileServiceManager localTileServiceManager = (TileServiceManager)this.mServices.get(localCustomTile);
      localTileServiceManager.clearPendingBind();
      localTileServiceManager.setLastUpdate(System.currentTimeMillis());
      localCustomTile.updateState(paramTile);
      localCustomTile.refreshState();
      return;
    }
  }
  
  public void updateStatusIcon(final IBinder paramIBinder, Icon paramIcon, String paramString)
  {
    paramIBinder = getTileForToken(paramIBinder);
    if (paramIBinder != null) {
      verifyCaller(paramIBinder);
    }
    try
    {
      final ComponentName localComponentName = paramIBinder.getComponent();
      paramIBinder = localComponentName.getPackageName();
      UserHandle localUserHandle = getCallingUserHandle();
      if (this.mContext.getPackageManager().getPackageInfoAsUser(paramIBinder, 0, localUserHandle.getIdentifier()).applicationInfo.isSystemApp()) {
        if (paramIcon == null) {
          break label97;
        }
      }
      label97:
      for (paramIBinder = new StatusBarIcon(localUserHandle, paramIBinder, paramIcon, 0, 0, paramString);; paramIBinder = null)
      {
        this.mMainHandler.post(new Runnable()
        {
          public void run()
          {
            StatusBarIconController localStatusBarIconController = TileServices.-get0(TileServices.this).getIconController();
            localStatusBarIconController.setIcon(localComponentName.getClassName(), paramIBinder);
            localStatusBarIconController.setExternalIcon(localComponentName.getClassName());
          }
        });
        return;
      }
      return;
    }
    catch (PackageManager.NameNotFoundException paramIBinder) {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\external\TileServices.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */