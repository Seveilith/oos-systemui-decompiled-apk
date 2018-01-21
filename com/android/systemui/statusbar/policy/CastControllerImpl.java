package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaRouter;
import android.media.MediaRouter.RouteInfo;
import android.media.MediaRouter.SimpleCallback;
import android.media.projection.MediaProjectionInfo;
import android.media.projection.MediaProjectionManager;
import android.media.projection.MediaProjectionManager.Callback;
import android.os.Handler;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class CastControllerImpl
  implements CastController
{
  private static final boolean DEBUG = Log.isLoggable("CastController", 3);
  private boolean mCallbackRegistered;
  private final ArrayList<CastController.Callback> mCallbacks = new ArrayList();
  private final Context mContext;
  private boolean mDiscovering;
  private final Object mDiscoveringLock = new Object();
  private final MediaRouter.SimpleCallback mMediaCallback = new MediaRouter.SimpleCallback()
  {
    public void onRouteAdded(MediaRouter paramAnonymousMediaRouter, MediaRouter.RouteInfo paramAnonymousRouteInfo)
    {
      if (CastControllerImpl.-get0()) {
        Log.d("CastController", "onRouteAdded: " + CastControllerImpl.-wrap0(paramAnonymousRouteInfo));
      }
      CastControllerImpl.-wrap2(CastControllerImpl.this);
    }
    
    public void onRouteChanged(MediaRouter paramAnonymousMediaRouter, MediaRouter.RouteInfo paramAnonymousRouteInfo)
    {
      if (CastControllerImpl.-get0()) {
        Log.d("CastController", "onRouteChanged: " + CastControllerImpl.-wrap0(paramAnonymousRouteInfo));
      }
      CastControllerImpl.-wrap2(CastControllerImpl.this);
    }
    
    public void onRouteRemoved(MediaRouter paramAnonymousMediaRouter, MediaRouter.RouteInfo paramAnonymousRouteInfo)
    {
      if (CastControllerImpl.-get0()) {
        Log.d("CastController", "onRouteRemoved: " + CastControllerImpl.-wrap0(paramAnonymousRouteInfo));
      }
      CastControllerImpl.-wrap2(CastControllerImpl.this);
    }
    
    public void onRouteSelected(MediaRouter paramAnonymousMediaRouter, int paramAnonymousInt, MediaRouter.RouteInfo paramAnonymousRouteInfo)
    {
      if (CastControllerImpl.-get0()) {
        Log.d("CastController", "onRouteSelected(" + paramAnonymousInt + "): " + CastControllerImpl.-wrap0(paramAnonymousRouteInfo));
      }
      CastControllerImpl.-wrap2(CastControllerImpl.this);
    }
    
    public void onRouteUnselected(MediaRouter paramAnonymousMediaRouter, int paramAnonymousInt, MediaRouter.RouteInfo paramAnonymousRouteInfo)
    {
      if (CastControllerImpl.-get0()) {
        Log.d("CastController", "onRouteUnselected(" + paramAnonymousInt + "): " + CastControllerImpl.-wrap0(paramAnonymousRouteInfo));
      }
      CastControllerImpl.-wrap2(CastControllerImpl.this);
    }
  };
  private final MediaRouter mMediaRouter;
  private MediaProjectionInfo mProjection;
  private final MediaProjectionManager.Callback mProjectionCallback = new MediaProjectionManager.Callback()
  {
    public void onStart(MediaProjectionInfo paramAnonymousMediaProjectionInfo)
    {
      CastControllerImpl.-wrap1(CastControllerImpl.this, paramAnonymousMediaProjectionInfo, true);
    }
    
    public void onStop(MediaProjectionInfo paramAnonymousMediaProjectionInfo)
    {
      CastControllerImpl.-wrap1(CastControllerImpl.this, paramAnonymousMediaProjectionInfo, false);
    }
  };
  private final Object mProjectionLock = new Object();
  private final MediaProjectionManager mProjectionManager;
  private final ArrayMap<String, MediaRouter.RouteInfo> mRoutes = new ArrayMap();
  
  public CastControllerImpl(Context paramContext)
  {
    this.mContext = paramContext;
    this.mMediaRouter = ((MediaRouter)paramContext.getSystemService("media_router"));
    this.mProjectionManager = ((MediaProjectionManager)paramContext.getSystemService("media_projection"));
    this.mProjection = this.mProjectionManager.getActiveProjectionInfo();
    this.mProjectionManager.addCallback(this.mProjectionCallback, new Handler());
    if (DEBUG) {
      Log.d("CastController", "new CastController()");
    }
  }
  
  private void ensureTagExists(MediaRouter.RouteInfo paramRouteInfo)
  {
    if (paramRouteInfo.getTag() == null) {
      paramRouteInfo.setTag(UUID.randomUUID().toString());
    }
  }
  
  private void fireOnCastDevicesChanged()
  {
    Iterator localIterator = this.mCallbacks.iterator();
    while (localIterator.hasNext()) {
      fireOnCastDevicesChanged((CastController.Callback)localIterator.next());
    }
  }
  
  private void fireOnCastDevicesChanged(CastController.Callback paramCallback)
  {
    paramCallback.onCastDevicesChanged();
  }
  
  private String getAppName(String paramString)
  {
    Object localObject = this.mContext.getPackageManager();
    try
    {
      ApplicationInfo localApplicationInfo = ((PackageManager)localObject).getApplicationInfo(paramString, 0);
      if (localApplicationInfo != null)
      {
        localObject = localApplicationInfo.loadLabel((PackageManager)localObject);
        if (!TextUtils.isEmpty((CharSequence)localObject)) {
          return ((CharSequence)localObject).toString();
        }
      }
      Log.w("CastController", "No label found for package: " + paramString);
      return paramString;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.w("CastController", "Error getting appName for package: " + paramString, localNameNotFoundException);
    }
    return paramString;
  }
  
  private void handleDiscoveryChangeLocked()
  {
    if (this.mCallbackRegistered)
    {
      this.mMediaRouter.removeCallback(this.mMediaCallback);
      this.mCallbackRegistered = false;
    }
    if (this.mDiscovering)
    {
      this.mMediaRouter.addCallback(4, this.mMediaCallback, 1);
      this.mCallbackRegistered = true;
    }
    while (this.mCallbacks.size() == 0) {
      return;
    }
    this.mMediaRouter.addCallback(4, this.mMediaCallback, 8);
    this.mCallbackRegistered = true;
  }
  
  private static String routeToString(MediaRouter.RouteInfo paramRouteInfo)
  {
    if (paramRouteInfo == null) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder().append(paramRouteInfo.getName()).append('/').append(paramRouteInfo.getDescription()).append('@').append(paramRouteInfo.getDeviceAddress()).append(",status=").append(paramRouteInfo.getStatus());
    if (paramRouteInfo.isDefault()) {
      localStringBuilder.append(",default");
    }
    if (paramRouteInfo.isEnabled()) {
      localStringBuilder.append(",enabled");
    }
    if (paramRouteInfo.isConnecting()) {
      localStringBuilder.append(",connecting");
    }
    if (paramRouteInfo.isSelected()) {
      localStringBuilder.append(",selected");
    }
    return ",id=" + paramRouteInfo.getTag();
  }
  
  private void setProjection(MediaProjectionInfo paramMediaProjectionInfo, boolean paramBoolean)
  {
    int j = 0;
    MediaProjectionInfo localMediaProjectionInfo = this.mProjection;
    synchronized (this.mProjectionLock)
    {
      boolean bool = Objects.equals(paramMediaProjectionInfo, this.mProjection);
      if ((!paramBoolean) || (bool))
      {
        i = j;
        if (!paramBoolean)
        {
          i = j;
          if (bool)
          {
            this.mProjection = null;
            i = 1;
          }
        }
        if (i != 0)
        {
          if (DEBUG) {
            Log.d("CastController", "setProjection: " + localMediaProjectionInfo + " -> " + this.mProjection);
          }
          fireOnCastDevicesChanged();
        }
        return;
      }
      this.mProjection = paramMediaProjectionInfo;
      int i = 1;
    }
  }
  
  private void updateRemoteDisplays()
  {
    for (;;)
    {
      int i;
      synchronized (this.mRoutes)
      {
        this.mRoutes.clear();
        int j = this.mMediaRouter.getRouteCount();
        i = 0;
        if (i < j)
        {
          MediaRouter.RouteInfo localRouteInfo1 = this.mMediaRouter.getRouteAt(i);
          if ((!localRouteInfo1.isEnabled()) || (!localRouteInfo1.matchesTypes(4))) {
            break label156;
          }
          ensureTagExists(localRouteInfo1);
          this.mRoutes.put(localRouteInfo1.getTag().toString(), localRouteInfo1);
        }
      }
      MediaRouter.RouteInfo localRouteInfo2 = this.mMediaRouter.getSelectedRoute(4);
      if (localRouteInfo2 != null)
      {
        boolean bool = localRouteInfo2.isDefault();
        if (!bool) {
          break label129;
        }
      }
      for (;;)
      {
        fireOnCastDevicesChanged();
        return;
        label129:
        ensureTagExists(localRouteInfo2);
        this.mRoutes.put(localRouteInfo2.getTag().toString(), localRouteInfo2);
      }
      label156:
      i += 1;
    }
  }
  
  public void addCallback(CastController.Callback arg1)
  {
    this.mCallbacks.add(???);
    fireOnCastDevicesChanged(???);
    synchronized (this.mDiscoveringLock)
    {
      handleDiscoveryChangeLocked();
      return;
    }
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("CastController state:");
    paramPrintWriter.print("  mDiscovering=");
    paramPrintWriter.println(this.mDiscovering);
    paramPrintWriter.print("  mCallbackRegistered=");
    paramPrintWriter.println(this.mCallbackRegistered);
    paramPrintWriter.print("  mCallbacks.size=");
    paramPrintWriter.println(this.mCallbacks.size());
    paramPrintWriter.print("  mRoutes.size=");
    paramPrintWriter.println(this.mRoutes.size());
    int i = 0;
    while (i < this.mRoutes.size())
    {
      paramFileDescriptor = (MediaRouter.RouteInfo)this.mRoutes.valueAt(i);
      paramPrintWriter.print("    ");
      paramPrintWriter.println(routeToString(paramFileDescriptor));
      i += 1;
    }
    paramPrintWriter.print("  mProjection=");
    paramPrintWriter.println(this.mProjection);
  }
  
  public Set<CastController.CastDevice> getCastDevices()
  {
    ArraySet localArraySet = new ArraySet();
    for (;;)
    {
      MediaRouter.RouteInfo localRouteInfo;
      int i;
      synchronized (this.mProjectionLock)
      {
        if (this.mProjection != null)
        {
          ??? = new CastController.CastDevice();
          ((CastController.CastDevice)???).id = this.mProjection.getPackageName();
          ((CastController.CastDevice)???).name = getAppName(this.mProjection.getPackageName());
          ((CastController.CastDevice)???).description = this.mContext.getString(2131690297);
          ((CastController.CastDevice)???).state = 2;
          ((CastController.CastDevice)???).tag = this.mProjection;
          localArraySet.add(???);
          return localArraySet;
        }
        synchronized (this.mRoutes)
        {
          Iterator localIterator = this.mRoutes.values().iterator();
          if (!localIterator.hasNext()) {
            break;
          }
          localRouteInfo = (MediaRouter.RouteInfo)localIterator.next();
          CastController.CastDevice localCastDevice = new CastController.CastDevice();
          localCastDevice.id = localRouteInfo.getTag().toString();
          ??? = localRouteInfo.getName(this.mContext);
          if (??? != null)
          {
            ??? = ((CharSequence)???).toString();
            localCastDevice.name = ((String)???);
            ??? = localRouteInfo.getDescription();
            if (??? == null) {
              break label274;
            }
            ??? = ((CharSequence)???).toString();
            localCastDevice.description = ((String)???);
            if (!localRouteInfo.isConnecting()) {
              break label279;
            }
            i = 1;
            localCastDevice.state = i;
            localCastDevice.tag = localRouteInfo;
            localArraySet.add(localCastDevice);
          }
        }
      }
      Object localObject3 = null;
      continue;
      label274:
      localObject3 = null;
      continue;
      label279:
      boolean bool = localRouteInfo.isSelected();
      if (bool) {
        i = 2;
      } else {
        i = 0;
      }
    }
    return localSet;
  }
  
  public void removeCallback(CastController.Callback arg1)
  {
    this.mCallbacks.remove(???);
    synchronized (this.mDiscoveringLock)
    {
      handleDiscoveryChangeLocked();
      return;
    }
  }
  
  public void setCurrentUserId(int paramInt)
  {
    this.mMediaRouter.rebindAsUser(paramInt);
  }
  
  public void setDiscovering(boolean paramBoolean)
  {
    synchronized (this.mDiscoveringLock)
    {
      boolean bool = this.mDiscovering;
      if (bool == paramBoolean) {
        return;
      }
      this.mDiscovering = paramBoolean;
      if (DEBUG) {
        Log.d("CastController", "setDiscovering: " + paramBoolean);
      }
      handleDiscoveryChangeLocked();
      return;
    }
  }
  
  public void startCasting(CastController.CastDevice paramCastDevice)
  {
    if ((paramCastDevice != null) && ((paramCastDevice.tag instanceof MediaRouter.RouteInfo)))
    {
      paramCastDevice = (MediaRouter.RouteInfo)paramCastDevice.tag;
      if (DEBUG) {
        Log.d("CastController", "startCasting: " + routeToString(paramCastDevice));
      }
      this.mMediaRouter.selectRoute(4, paramCastDevice);
      return;
    }
  }
  
  public void stopCasting(CastController.CastDevice paramCastDevice)
  {
    boolean bool = paramCastDevice.tag instanceof MediaProjectionInfo;
    if (DEBUG) {
      Log.d("CastController", "stopCasting isProjection=" + bool);
    }
    if (bool)
    {
      paramCastDevice = (MediaProjectionInfo)paramCastDevice.tag;
      if (Objects.equals(this.mProjectionManager.getActiveProjectionInfo(), paramCastDevice))
      {
        this.mProjectionManager.stopActiveProjection();
        return;
      }
      Log.w("CastController", "Projection is no longer active: " + paramCastDevice);
      return;
    }
    this.mMediaRouter.getDefaultRoute().select();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\CastControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */