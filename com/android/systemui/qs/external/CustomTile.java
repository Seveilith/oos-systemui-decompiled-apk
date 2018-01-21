package com.android.systemui.qs.external;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.service.quicksettings.IQSTileService;
import android.service.quicksettings.Tile;
import android.util.Log;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.qs.QSIconView;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.DrawableIcon;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.State;
import com.android.systemui.statusbar.phone.QSTileHost;
import libcore.util.Objects;

public class CustomTile
  extends QSTile<QSTile.State>
  implements TileLifecycleManager.TileChangeListener
{
  private final ComponentName mComponent;
  private Icon mDefaultIcon;
  private boolean mIsShowingDialog;
  private boolean mIsTokenGranted;
  private boolean mListening;
  private final IQSTileService mService;
  private final TileServiceManager mServiceManager;
  private final Tile mTile;
  private final IBinder mToken = new Binder();
  private final int mUser;
  private final IWindowManager mWindowManager = WindowManagerGlobal.getWindowManagerService();
  
  private CustomTile(QSTileHost paramQSTileHost, String paramString)
  {
    super(paramQSTileHost);
    this.mComponent = ComponentName.unflattenFromString(paramString);
    this.mTile = new Tile();
    setTileIcon();
    this.mServiceManager = paramQSTileHost.getTileServices().getTileWrapper(this);
    this.mService = this.mServiceManager.getTileService();
    this.mServiceManager.setTileChangeListener(this);
    this.mUser = ActivityManager.getCurrentUser();
  }
  
  public static QSTile<?> create(QSTileHost paramQSTileHost, String paramString)
  {
    if ((paramString != null) && (paramString.startsWith("custom(")) && (paramString.endsWith(")")))
    {
      paramString = paramString.substring("custom(".length(), paramString.length() - 1);
      if (paramString.isEmpty()) {
        throw new IllegalArgumentException("Empty custom tile spec action");
      }
    }
    else
    {
      throw new IllegalArgumentException("Bad custom tile spec: " + paramString);
    }
    return new CustomTile(paramQSTileHost, paramString);
  }
  
  private static int getColor(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 0: 
    case 1: 
      return QSIconView.sCustomDisableIconColor;
    }
    return QSIconView.sIconColor;
  }
  
  public static ComponentName getComponentFromSpec(String paramString)
  {
    paramString = paramString.substring("custom(".length(), paramString.length() - 1);
    if (paramString.isEmpty()) {
      throw new IllegalArgumentException("Empty custom tile spec action");
    }
    return ComponentName.unflattenFromString(paramString);
  }
  
  private boolean iconEquals(Icon paramIcon1, Icon paramIcon2)
  {
    if (paramIcon1 == paramIcon2) {
      return true;
    }
    if ((paramIcon1 == null) || (paramIcon2 == null)) {
      return false;
    }
    if ((paramIcon1.getType() != 2) || (paramIcon2.getType() != 2)) {
      return false;
    }
    if (paramIcon1.getResId() != paramIcon2.getResId()) {
      return false;
    }
    return Objects.equal(paramIcon1.getResPackage(), paramIcon2.getResPackage());
  }
  
  private boolean isSystemApp(PackageManager paramPackageManager)
    throws PackageManager.NameNotFoundException
  {
    return paramPackageManager.getApplicationInfo(this.mComponent.getPackageName(), 0).isSystemApp();
  }
  
  private Intent resolveIntent(Intent paramIntent)
  {
    Object localObject = null;
    ResolveInfo localResolveInfo = this.mContext.getPackageManager().resolveActivityAsUser(paramIntent, 0, ActivityManager.getCurrentUser());
    paramIntent = (Intent)localObject;
    if (localResolveInfo != null) {
      paramIntent = new Intent("android.service.quicksettings.action.QS_TILE_PREFERENCES").setClassName(localResolveInfo.activityInfo.packageName, localResolveInfo.activityInfo.name);
    }
    return paramIntent;
  }
  
  private void setTileIcon()
  {
    try
    {
      PackageManager localPackageManager = this.mContext.getPackageManager();
      int i = 786432;
      if (isSystemApp(localPackageManager)) {
        i = 786944;
      }
      ServiceInfo localServiceInfo = localPackageManager.getServiceInfo(this.mComponent, i);
      boolean bool;
      if (localServiceInfo.icon != 0)
      {
        i = localServiceInfo.icon;
        if (this.mTile.getIcon() == null) {
          break label149;
        }
        bool = iconEquals(this.mTile.getIcon(), this.mDefaultIcon);
        label76:
        if (i == 0) {
          break label154;
        }
      }
      label149:
      label154:
      for (Icon localIcon = Icon.createWithResource(this.mComponent.getPackageName(), i);; localIcon = null)
      {
        this.mDefaultIcon = localIcon;
        if (bool) {
          this.mTile.setIcon(this.mDefaultIcon);
        }
        if (this.mTile.getLabel() != null) {
          return;
        }
        this.mTile.setLabel(localServiceInfo.loadLabel(localPackageManager));
        return;
        i = localServiceInfo.applicationInfo.icon;
        break;
        bool = true;
        break label76;
      }
      return;
    }
    catch (Exception localException)
    {
      this.mDefaultIcon = null;
    }
  }
  
  public static String toSpec(ComponentName paramComponentName)
  {
    return "custom(" + paramComponentName.flattenToShortString() + ")";
  }
  
  public ComponentName getComponent()
  {
    return this.mComponent;
  }
  
  public Intent getLongClickIntent()
  {
    Intent localIntent = new Intent("android.service.quicksettings.action.QS_TILE_PREFERENCES");
    localIntent.setPackage(this.mComponent.getPackageName());
    localIntent = resolveIntent(localIntent);
    if (localIntent != null) {
      return localIntent;
    }
    return new Intent("android.settings.APPLICATION_DETAILS_SETTINGS").setData(Uri.fromParts("package", this.mComponent.getPackageName(), null));
  }
  
  public int getMetricsCategory()
  {
    return 268;
  }
  
  public Tile getQsTile()
  {
    return this.mTile;
  }
  
  public CharSequence getTileLabel()
  {
    return getState().label;
  }
  
  public int getUser()
  {
    return this.mUser;
  }
  
  protected void handleClick()
  {
    if (this.mTile.getState() == 0) {
      return;
    }
    try
    {
      this.mWindowManager.addWindowToken(this.mToken, 2035);
      this.mIsTokenGranted = true;
      try
      {
        if (this.mServiceManager.isActiveTile())
        {
          this.mServiceManager.setBindRequested(true);
          this.mService.onStartListening();
        }
        this.mService.onClick(this.mToken);
      }
      catch (RemoteException localRemoteException1)
      {
        for (;;) {}
      }
      MetricsLogger.action(this.mContext, getMetricsCategory(), this.mComponent.getPackageName());
      return;
    }
    catch (RemoteException localRemoteException2)
    {
      for (;;) {}
    }
  }
  
  protected void handleDestroy()
  {
    super.handleDestroy();
    if (this.mIsTokenGranted) {}
    try
    {
      this.mWindowManager.removeWindowToken(this.mToken);
      this.mHost.getTileServices().freeService(this, this.mServiceManager);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  protected void handleUpdateState(QSTile.State paramState, Object paramObject)
  {
    int i = this.mTile.getState();
    if (this.mServiceManager.hasPendingBind()) {
      i = 0;
    }
    try
    {
      paramObject = this.mTile.getIcon().loadDrawable(this.mContext);
      ((Drawable)paramObject).setTint(getColor(i));
      paramState.icon = new QSTile.DrawableIcon((Drawable)paramObject);
      paramState.label = this.mTile.getLabel();
      if (this.mTile.getContentDescription() != null)
      {
        paramState.contentDescription = this.mTile.getContentDescription();
        return;
      }
    }
    catch (Exception paramObject)
    {
      for (;;)
      {
        Log.w(this.TAG, "Invalid icon, forcing into unavailable state");
        i = 0;
        paramObject = this.mDefaultIcon.loadDrawable(this.mContext);
      }
      paramState.contentDescription = paramState.label;
    }
  }
  
  public boolean isAvailable()
  {
    return this.mDefaultIcon != null;
  }
  
  public QSTile.State newTileState()
  {
    return new QSTile.State();
  }
  
  public void onDialogHidden()
  {
    this.mIsShowingDialog = false;
    try
    {
      this.mWindowManager.removeWindowToken(this.mToken);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void onDialogShown()
  {
    this.mIsShowingDialog = true;
  }
  
  public void onTileChanged(ComponentName paramComponentName)
  {
    setTileIcon();
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (this.mListening == paramBoolean) {
      return;
    }
    this.mListening = paramBoolean;
    if (paramBoolean) {}
    try
    {
      setTileIcon();
      refreshState();
      if (this.mServiceManager.isActiveTile()) {
        return;
      }
      this.mServiceManager.setBindRequested(true);
      this.mService.onStartListening();
      return;
    }
    catch (RemoteException localRemoteException1)
    {
      return;
    }
    this.mService.onStopListening();
    if ((!this.mIsTokenGranted) || (this.mIsShowingDialog)) {}
    for (;;)
    {
      this.mIsShowingDialog = false;
      this.mServiceManager.setBindRequested(false);
      return;
      try
      {
        this.mWindowManager.removeWindowToken(this.mToken);
        this.mIsTokenGranted = false;
      }
      catch (RemoteException localRemoteException2)
      {
        for (;;) {}
      }
    }
  }
  
  public void startUnlockAndRun()
  {
    this.mHost.startRunnableDismissingKeyguard(new Runnable()
    {
      public void run()
      {
        try
        {
          CustomTile.-get0(CustomTile.this).onUnlockComplete();
          return;
        }
        catch (RemoteException localRemoteException) {}
      }
    });
  }
  
  public void updateState(Tile paramTile)
  {
    this.mTile.setIcon(paramTile.getIcon());
    this.mTile.setLabel(paramTile.getLabel());
    this.mTile.setContentDescription(paramTile.getContentDescription());
    this.mTile.setState(paramTile.getState());
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\external\CustomTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */