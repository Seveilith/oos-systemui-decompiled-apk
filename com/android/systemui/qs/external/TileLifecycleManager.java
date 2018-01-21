package com.android.systemui.qs.external;

import android.app.AppGlobals;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.quicksettings.IQSService;
import android.service.quicksettings.IQSTileService;
import android.service.quicksettings.IQSTileService.Stub;
import android.service.quicksettings.Tile;
import android.support.annotation.VisibleForTesting;
import android.util.ArraySet;
import android.util.Log;
import java.util.Set;
import libcore.util.Objects;

public class TileLifecycleManager
  extends BroadcastReceiver
  implements IQSTileService, ServiceConnection, IBinder.DeathRecipient
{
  private int mBindTryCount;
  private boolean mBound;
  private TileChangeListener mChangeListener;
  private IBinder mClickBinder;
  private final Context mContext;
  private final Handler mHandler;
  private final Intent mIntent;
  private boolean mIsBound;
  private boolean mListening;
  private Set<Integer> mQueuedMessages = new ArraySet();
  @VisibleForTesting
  boolean mReceiverRegistered;
  private final IBinder mToken = new Binder();
  private boolean mUnbindImmediate;
  private final UserHandle mUser;
  private QSTileServiceWrapper mWrapper;
  
  public TileLifecycleManager(Handler paramHandler, Context paramContext, IQSService paramIQSService, Tile paramTile, Intent paramIntent, UserHandle paramUserHandle)
  {
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    this.mIntent = paramIntent;
    this.mIntent.putExtra("service", paramIQSService.asBinder());
    this.mIntent.putExtra("token", this.mToken);
    this.mUser = paramUserHandle;
  }
  
  private boolean checkComponentState()
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    if ((isPackageAvailable(localPackageManager)) && (isComponentAvailable(localPackageManager))) {
      return true;
    }
    startPackageListening();
    return false;
  }
  
  private void handleDeath()
  {
    if (this.mWrapper == null) {
      return;
    }
    this.mWrapper = null;
    if (!this.mBound) {
      return;
    }
    if (checkComponentState()) {
      this.mHandler.postDelayed(new Runnable()
      {
        public void run()
        {
          if (TileLifecycleManager.-get0(TileLifecycleManager.this)) {
            TileLifecycleManager.this.setBindService(true);
          }
        }
      }, 1000L);
    }
  }
  
  private void handlePendingMessages()
  {
    for (;;)
    {
      synchronized (this.mQueuedMessages)
      {
        ArraySet localArraySet = new ArraySet(this.mQueuedMessages);
        this.mQueuedMessages.clear();
        if (localArraySet.contains(Integer.valueOf(0))) {
          onTileAdded();
        }
        if (this.mListening) {
          onStartListening();
        }
        if (localArraySet.contains(Integer.valueOf(2)))
        {
          if (!this.mListening) {
            Log.w("TileLifecycleManager", "Managed to get click on non-listening state...");
          }
        }
        else
        {
          if (localArraySet.contains(Integer.valueOf(3)))
          {
            if (this.mListening) {
              break label176;
            }
            Log.w("TileLifecycleManager", "Managed to get unlock on non-listening state...");
          }
          if (localArraySet.contains(Integer.valueOf(1)))
          {
            if (this.mListening)
            {
              Log.w("TileLifecycleManager", "Managed to get remove in listening state...");
              onStopListening();
            }
            onTileRemoved();
          }
          if (this.mUnbindImmediate)
          {
            this.mUnbindImmediate = false;
            setBindService(false);
          }
          return;
        }
      }
      onClick(this.mClickBinder);
      continue;
      label176:
      onUnlockComplete();
    }
  }
  
  private boolean isComponentAvailable(PackageManager paramPackageManager)
  {
    boolean bool = false;
    this.mIntent.getComponent().getPackageName();
    try
    {
      paramPackageManager = AppGlobals.getPackageManager().getServiceInfo(this.mIntent.getComponent(), 0, this.mUser.getIdentifier());
      if (paramPackageManager != null) {
        bool = true;
      }
      return bool;
    }
    catch (RemoteException paramPackageManager) {}
    return false;
  }
  
  private boolean isPackageAvailable(PackageManager paramPackageManager)
  {
    String str = this.mIntent.getComponent().getPackageName();
    try
    {
      paramPackageManager.getPackageInfoAsUser(str, 0, this.mUser.getIdentifier());
      return true;
    }
    catch (PackageManager.NameNotFoundException paramPackageManager)
    {
      Log.d("TileLifecycleManager", "Package not available: " + str);
    }
    return false;
  }
  
  public static boolean isTileAdded(Context paramContext, ComponentName paramComponentName)
  {
    return paramContext.getSharedPreferences("tiles_prefs", 0).getBoolean(paramComponentName.flattenToString(), false);
  }
  
  private void queueMessage(int paramInt)
  {
    synchronized (this.mQueuedMessages)
    {
      this.mQueuedMessages.add(Integer.valueOf(paramInt));
      return;
    }
  }
  
  public static void setTileAdded(Context paramContext, ComponentName paramComponentName, boolean paramBoolean)
  {
    paramContext.getSharedPreferences("tiles_prefs", 0).edit().putBoolean(paramComponentName.flattenToString(), paramBoolean).commit();
  }
  
  private void startPackageListening()
  {
    IntentFilter localIntentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
    localIntentFilter.addDataScheme("package");
    this.mContext.registerReceiverAsUser(this, this.mUser, localIntentFilter, null, this.mHandler);
    localIntentFilter = new IntentFilter("android.intent.action.USER_UNLOCKED");
    this.mContext.registerReceiverAsUser(this, this.mUser, localIntentFilter, null, this.mHandler);
    this.mReceiverRegistered = true;
  }
  
  private void stopPackageListening()
  {
    this.mContext.unregisterReceiver(this);
    this.mReceiverRegistered = false;
  }
  
  public IBinder asBinder()
  {
    IBinder localIBinder = null;
    if (this.mWrapper != null) {
      localIBinder = this.mWrapper.asBinder();
    }
    return localIBinder;
  }
  
  public void binderDied()
  {
    handleDeath();
  }
  
  public void flushMessagesAndUnbind()
  {
    this.mUnbindImmediate = true;
    setBindService(true);
  }
  
  public ComponentName getComponent()
  {
    return this.mIntent.getComponent();
  }
  
  public IBinder getToken()
  {
    return this.mToken;
  }
  
  public void handleDestroy()
  {
    if (this.mReceiverRegistered) {
      stopPackageListening();
    }
  }
  
  public boolean hasPendingClick()
  {
    synchronized (this.mQueuedMessages)
    {
      boolean bool = this.mQueuedMessages.contains(Integer.valueOf(2));
      return bool;
    }
  }
  
  public boolean isActiveTile()
  {
    boolean bool = false;
    try
    {
      ServiceInfo localServiceInfo = this.mContext.getPackageManager().getServiceInfo(this.mIntent.getComponent(), 8320);
      if (localServiceInfo.metaData != null) {
        bool = localServiceInfo.metaData.getBoolean("android.service.quicksettings.ACTIVE_TILE", false);
      }
      return bool;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
    return false;
  }
  
  public void onClick(IBinder paramIBinder)
  {
    if ((this.mWrapper != null) && (this.mWrapper.onClick(paramIBinder))) {
      return;
    }
    this.mClickBinder = paramIBinder;
    queueMessage(2);
    handleDeath();
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if ((!"android.intent.action.USER_UNLOCKED".equals(paramIntent.getAction())) && (!Objects.equal(paramIntent.getData().getEncodedSchemeSpecificPart(), this.mIntent.getComponent().getPackageName()))) {
      return;
    }
    if (("android.intent.action.PACKAGE_CHANGED".equals(paramIntent.getAction())) && (this.mChangeListener != null)) {
      this.mChangeListener.onTileChanged(this.mIntent.getComponent());
    }
    stopPackageListening();
    if (this.mBound) {
      setBindService(true);
    }
  }
  
  public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
  {
    this.mBindTryCount = 0;
    paramComponentName = new QSTileServiceWrapper(IQSTileService.Stub.asInterface(paramIBinder));
    try
    {
      paramIBinder.linkToDeath(this, 0);
      this.mWrapper = paramComponentName;
      handlePendingMessages();
      return;
    }
    catch (RemoteException paramIBinder)
    {
      for (;;) {}
    }
  }
  
  public void onServiceDisconnected(ComponentName paramComponentName)
  {
    handleDeath();
  }
  
  public void onStartListening()
  {
    this.mListening = true;
    if ((this.mWrapper == null) || (this.mWrapper.onStartListening())) {
      return;
    }
    handleDeath();
  }
  
  public void onStopListening()
  {
    this.mListening = false;
    if ((this.mWrapper == null) || (this.mWrapper.onStopListening())) {
      return;
    }
    handleDeath();
  }
  
  public void onTileAdded()
  {
    if ((this.mWrapper != null) && (this.mWrapper.onTileAdded())) {
      return;
    }
    queueMessage(0);
    handleDeath();
  }
  
  public void onTileRemoved()
  {
    if ((this.mWrapper != null) && (this.mWrapper.onTileRemoved())) {
      return;
    }
    queueMessage(1);
    handleDeath();
  }
  
  public void onUnlockComplete()
  {
    if ((this.mWrapper != null) && (this.mWrapper.onUnlockComplete())) {
      return;
    }
    queueMessage(3);
    handleDeath();
  }
  
  public void setBindService(boolean paramBoolean)
  {
    if ((this.mBound) && (this.mUnbindImmediate))
    {
      this.mUnbindImmediate = false;
      return;
    }
    this.mBound = paramBoolean;
    if (paramBoolean)
    {
      if (this.mBindTryCount == 5)
      {
        startPackageListening();
        return;
      }
      if (!checkComponentState()) {
        return;
      }
      this.mBindTryCount += 1;
    }
    do
    {
      try
      {
        this.mIsBound = this.mContext.bindServiceAsUser(this.mIntent, this, 33554433, this.mUser);
        return;
      }
      catch (SecurityException localSecurityException)
      {
        Log.e("TileLifecycleManager", "Failed to bind to service", localSecurityException);
        this.mIsBound = false;
        return;
      }
      this.mBindTryCount = 0;
      this.mWrapper = null;
    } while (!this.mIsBound);
    this.mContext.unbindService(this);
    this.mIsBound = false;
  }
  
  public void setTileChangeListener(TileChangeListener paramTileChangeListener)
  {
    this.mChangeListener = paramTileChangeListener;
  }
  
  public static abstract interface TileChangeListener
  {
    public abstract void onTileChanged(ComponentName paramComponentName);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\external\TileLifecycleManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */