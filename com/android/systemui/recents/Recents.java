package com.android.systemui.recents;

import android.app.ActivityManager.RunningTaskInfo;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.util.EventLog;
import android.util.Log;
import android.view.Display;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.RecentsComponent;
import com.android.systemui.SystemUI;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.ConfigurationChangedEvent;
import com.android.systemui.recents.events.activity.DockedTopTaskEvent;
import com.android.systemui.recents.events.activity.RecentsActivityStartingEvent;
import com.android.systemui.recents.events.component.RecentsVisibilityChangedEvent;
import com.android.systemui.recents.events.component.ScreenPinningRequestEvent;
import com.android.systemui.recents.events.component.ShowUserToastEvent;
import com.android.systemui.recents.events.ui.RecentsDrawnEvent;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.model.RecentsTaskLoader;
import com.android.systemui.recents.tv.RecentsTvImpl;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.stackdivider.DividerView;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Recents
  extends SystemUI
  implements RecentsComponent
{
  private static RecentsConfiguration sConfiguration;
  private static RecentsDebugFlags sDebugFlags;
  private static SystemServicesProxy sSystemServicesProxy;
  private static RecentsTaskLoader sTaskLoader;
  private int mDraggingInRecentsCurrentUser;
  private Handler mHandler;
  private RecentsImpl mImpl;
  private final ArrayList<Runnable> mOnConnectRunnables = new ArrayList();
  private String mOverrideRecentsPackageName;
  private RecentsSystemUser mSystemToUserCallbacks;
  private IRecentsSystemUserCallbacks mUserToSystemCallbacks;
  private final IBinder.DeathRecipient mUserToSystemCallbacksDeathRcpt = new IBinder.DeathRecipient()
  {
    public void binderDied()
    {
      Recents.-set0(Recents.this, null);
      EventLog.writeEvent(36060, new Object[] { Integer.valueOf(3), Integer.valueOf(Recents.-get4().getProcessUser()) });
      Recents.-get0(Recents.this).postDelayed(new Runnable()
      {
        public void run()
        {
          Recents.-wrap0(Recents.this);
        }
      }, 5000L);
    }
  };
  private final ServiceConnection mUserToSystemServiceConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      if (paramAnonymousIBinder != null)
      {
        Recents.-set0(Recents.this, IRecentsSystemUserCallbacks.Stub.asInterface(paramAnonymousIBinder));
        EventLog.writeEvent(36060, new Object[] { Integer.valueOf(2), Integer.valueOf(Recents.-get4().getProcessUser()) });
      }
      try
      {
        paramAnonymousIBinder.linkToDeath(Recents.-get3(Recents.this), 0);
        Recents.-wrap1(Recents.this);
        Recents.this.mContext.unbindService(this);
        return;
      }
      catch (RemoteException paramAnonymousComponentName)
      {
        for (;;)
        {
          Log.e("Recents", "Lost connection to (System) SystemUI", paramAnonymousComponentName);
        }
      }
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName) {}
  };
  
  public static RecentsConfiguration getConfiguration()
  {
    return sConfiguration;
  }
  
  public static RecentsDebugFlags getDebugFlags()
  {
    return sDebugFlags;
  }
  
  private static String getMetricsCounterForResizeMode(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "window_enter_incompatible";
    case 4: 
      return "window_enter_unsupported";
    }
    return "window_enter_supported";
  }
  
  public static SystemServicesProxy getSystemServices()
  {
    return sSystemServicesProxy;
  }
  
  public static RecentsTaskLoader getTaskLoader()
  {
    return sTaskLoader;
  }
  
  private boolean isUserSetup()
  {
    boolean bool2 = false;
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    boolean bool1 = bool2;
    if (Settings.Global.getInt(localContentResolver, "device_provisioned", 0) != 0)
    {
      bool1 = bool2;
      if (Settings.Secure.getInt(localContentResolver, "user_setup_complete", 0) != 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static void logDockAttempt(Context paramContext, ComponentName paramComponentName, int paramInt)
  {
    if (paramInt == 0) {
      MetricsLogger.action(paramContext, 391, paramComponentName.flattenToShortString());
    }
    MetricsLogger.count(paramContext, getMetricsCounterForResizeMode(paramInt), 1);
  }
  
  private void postToSystemUser(Runnable paramRunnable)
  {
    this.mOnConnectRunnables.add(paramRunnable);
    if (this.mUserToSystemCallbacks == null)
    {
      paramRunnable = new Intent();
      paramRunnable.setClass(this.mContext, RecentsSystemUserService.class);
      boolean bool = this.mContext.bindServiceAsUser(paramRunnable, this.mUserToSystemServiceConnection, 1, UserHandle.SYSTEM);
      EventLog.writeEvent(36060, new Object[] { Integer.valueOf(1), Integer.valueOf(sSystemServicesProxy.getProcessUser()) });
      if (!bool) {
        this.mHandler.postDelayed(new Runnable()
        {
          public void run()
          {
            Recents.-wrap0(Recents.this);
          }
        }, 5000L);
      }
      return;
    }
    runAndFlushOnConnectRunnables();
  }
  
  private boolean proxyToOverridePackage(String paramString)
  {
    if (this.mOverrideRecentsPackageName != null)
    {
      paramString = new Intent(paramString);
      paramString.setPackage(this.mOverrideRecentsPackageName);
      paramString.addFlags(268435456);
      this.mContext.sendBroadcast(paramString);
      return true;
    }
    return false;
  }
  
  private void registerWithSystemUser()
  {
    postToSystemUser(new Runnable()
    {
      public void run()
      {
        try
        {
          Log.d("Recents", "run: processUser =" + this.val$processUser);
          Recents.-get2(Recents.this).registerNonSystemUserCallbacks(new RecentsImplProxy(Recents.-get1(Recents.this)), this.val$processUser);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          Log.e("Recents", "Failed to register", localRemoteException);
        }
      }
    });
  }
  
  private void runAndFlushOnConnectRunnables()
  {
    Iterator localIterator = this.mOnConnectRunnables.iterator();
    while (localIterator.hasNext()) {
      ((Runnable)localIterator.next()).run();
    }
    this.mOnConnectRunnables.clear();
  }
  
  public void cancelPreloadingRecents()
  {
    if (!isUserSetup()) {
      return;
    }
    int i = sSystemServicesProxy.getCurrentUser();
    if (sSystemServicesProxy.isSystemUser(i)) {
      this.mImpl.cancelPreloadingRecents();
    }
    while (this.mSystemToUserCallbacks == null) {
      return;
    }
    IRecentsNonSystemUserCallbacks localIRecentsNonSystemUserCallbacks = this.mSystemToUserCallbacks.getNonSystemUserRecentsForUser(i);
    if (localIRecentsNonSystemUserCallbacks != null) {
      try
      {
        localIRecentsNonSystemUserCallbacks.cancelPreloadingRecents();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("Recents", "Callback failed", localRemoteException);
        return;
      }
    }
    Log.e("Recents", "No SystemUI callbacks found for user: " + i);
  }
  
  public boolean dockTopTask(int paramInt1, int paramInt2, Rect paramRect, int paramInt3)
  {
    if (!isUserSetup()) {
      return false;
    }
    Object localObject = new Point();
    Rect localRect = paramRect;
    if (paramRect == null)
    {
      ((DisplayManager)this.mContext.getSystemService(DisplayManager.class)).getDisplay(0).getRealSize((Point)localObject);
      localRect = new Rect(0, 0, ((Point)localObject).x, ((Point)localObject).y);
    }
    int i = sSystemServicesProxy.getCurrentUser();
    localObject = getSystemServices();
    paramRect = ((SystemServicesProxy)localObject).getRunningTask();
    boolean bool2 = ((SystemServicesProxy)localObject).isScreenPinningActive();
    boolean bool1;
    if (paramRect != null)
    {
      bool1 = SystemServicesProxy.isHomeStack(paramRect.stackId);
      if ((paramRect != null) && (!bool1)) {
        break label124;
      }
    }
    label124:
    while (bool2)
    {
      return false;
      bool1 = false;
      break;
    }
    logDockAttempt(this.mContext, paramRect.topActivity, paramRect.resizeMode);
    if (paramRect.isDockable)
    {
      if (paramInt3 != -1) {
        MetricsLogger.action(this.mContext, paramInt3, paramRect.topActivity.flattenToShortString());
      }
      if (sSystemServicesProxy.isSystemUser(i)) {
        this.mImpl.dockTopTask(paramRect.id, paramInt1, paramInt2, localRect);
      }
      for (;;)
      {
        this.mDraggingInRecentsCurrentUser = i;
        return true;
        if (this.mSystemToUserCallbacks != null)
        {
          localObject = this.mSystemToUserCallbacks.getNonSystemUserRecentsForUser(i);
          if (localObject != null) {
            try
            {
              ((IRecentsNonSystemUserCallbacks)localObject).dockTopTask(paramRect.id, paramInt1, paramInt2, localRect);
            }
            catch (RemoteException paramRect)
            {
              Log.e("Recents", "Callback failed", paramRect);
            }
          } else {
            Log.e("Recents", "No SystemUI callbacks found for user: " + i);
          }
        }
      }
    }
    EventBus.getDefault().send(new ShowUserToastEvent(2131690332, 0));
    return false;
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    sSystemServicesProxy.dump("Recents", paramPrintWriter);
    paramPrintWriter.println();
  }
  
  public List<String> getLockedPackageList()
  {
    return LockStateController.getInstance(this.mContext).getLockedPackageList();
  }
  
  public IBinder getSystemUserCallbacks()
  {
    return this.mSystemToUserCallbacks;
  }
  
  public void hideRecents(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!isUserSetup()) {
      return;
    }
    if (proxyToOverridePackage("com.android.systemui.recents.ACTION_HIDE")) {
      return;
    }
    int i = sSystemServicesProxy.getCurrentUser();
    if (sSystemServicesProxy.isSystemUser(i)) {
      this.mImpl.hideRecents(paramBoolean1, paramBoolean2);
    }
    while (this.mSystemToUserCallbacks == null) {
      return;
    }
    IRecentsNonSystemUserCallbacks localIRecentsNonSystemUserCallbacks = this.mSystemToUserCallbacks.getNonSystemUserRecentsForUser(i);
    if (localIRecentsNonSystemUserCallbacks != null) {
      try
      {
        localIRecentsNonSystemUserCallbacks.hideRecents(paramBoolean1, paramBoolean2);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("Recents", "Callback failed", localRemoteException);
        return;
      }
    }
    Log.e("Recents", "No SystemUI callbacks found for user: " + i);
  }
  
  public void onBootCompleted()
  {
    this.mImpl.onBootCompleted();
    WhiteList.init(this.mContext, this.mHandler);
  }
  
  public final void onBusEvent(ConfigurationChangedEvent paramConfigurationChangedEvent)
  {
    this.mImpl.onConfigurationChanged();
  }
  
  public final void onBusEvent(final DockedTopTaskEvent paramDockedTopTaskEvent)
  {
    int i = sSystemServicesProxy.getProcessUser();
    if (!sSystemServicesProxy.isSystemUser(i)) {
      postToSystemUser(new Runnable()
      {
        public void run()
        {
          try
          {
            Recents.-get2(Recents.this).sendDockingTopTaskEvent(paramDockedTopTaskEvent.dragMode, paramDockedTopTaskEvent.initialRect);
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.e("Recents", "Callback failed", localRemoteException);
          }
        }
      });
    }
  }
  
  public final void onBusEvent(RecentsActivityStartingEvent paramRecentsActivityStartingEvent)
  {
    int i = sSystemServicesProxy.getProcessUser();
    if (!sSystemServicesProxy.isSystemUser(i)) {
      postToSystemUser(new Runnable()
      {
        public void run()
        {
          try
          {
            Recents.-get2(Recents.this).sendLaunchRecentsEvent();
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.e("Recents", "Callback failed", localRemoteException);
          }
        }
      });
    }
    WhiteList.init(this.mContext, this.mHandler);
  }
  
  public final void onBusEvent(final RecentsVisibilityChangedEvent paramRecentsVisibilityChangedEvent)
  {
    SystemServicesProxy localSystemServicesProxy = getSystemServices();
    if (localSystemServicesProxy.isSystemUser(localSystemServicesProxy.getProcessUser()))
    {
      this.mImpl.onVisibilityChanged(paramRecentsVisibilityChangedEvent.applicationContext, paramRecentsVisibilityChangedEvent.visible);
      return;
    }
    postToSystemUser(new Runnable()
    {
      public void run()
      {
        try
        {
          Recents.-get2(Recents.this).updateRecentsVisibility(paramRecentsVisibilityChangedEvent.visible);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          Log.e("Recents", "Callback failed", localRemoteException);
        }
      }
    });
  }
  
  public final void onBusEvent(final ScreenPinningRequestEvent paramScreenPinningRequestEvent)
  {
    int i = sSystemServicesProxy.getProcessUser();
    if (sSystemServicesProxy.isSystemUser(i))
    {
      this.mImpl.onStartScreenPinning(paramScreenPinningRequestEvent.applicationContext, paramScreenPinningRequestEvent.taskId);
      return;
    }
    postToSystemUser(new Runnable()
    {
      public void run()
      {
        try
        {
          Recents.-get2(Recents.this).startScreenPinning(paramScreenPinningRequestEvent.taskId);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          Log.e("Recents", "Callback failed", localRemoteException);
        }
      }
    });
  }
  
  public final void onBusEvent(ShowUserToastEvent paramShowUserToastEvent)
  {
    int i = sSystemServicesProxy.getCurrentUser();
    if (sSystemServicesProxy.isSystemUser(i)) {
      this.mImpl.onShowCurrentUserToast(paramShowUserToastEvent.msgResId, paramShowUserToastEvent.msgLength);
    }
    while (this.mSystemToUserCallbacks == null) {
      return;
    }
    IRecentsNonSystemUserCallbacks localIRecentsNonSystemUserCallbacks = this.mSystemToUserCallbacks.getNonSystemUserRecentsForUser(i);
    if (localIRecentsNonSystemUserCallbacks != null) {
      try
      {
        localIRecentsNonSystemUserCallbacks.showCurrentUserToast(paramShowUserToastEvent.msgResId, paramShowUserToastEvent.msgLength);
        return;
      }
      catch (RemoteException paramShowUserToastEvent)
      {
        Log.e("Recents", "Callback failed", paramShowUserToastEvent);
        return;
      }
    }
    Log.e("Recents", "No SystemUI callbacks found for user: " + i);
  }
  
  public final void onBusEvent(RecentsDrawnEvent paramRecentsDrawnEvent)
  {
    int i = sSystemServicesProxy.getProcessUser();
    if (!sSystemServicesProxy.isSystemUser(i)) {
      postToSystemUser(new Runnable()
      {
        public void run()
        {
          try
          {
            Recents.-get2(Recents.this).sendRecentsDrawnEvent();
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.e("Recents", "Callback failed", localRemoteException);
          }
        }
      });
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    int i = sSystemServicesProxy.getCurrentUser();
    if (sSystemServicesProxy.isSystemUser(i)) {
      this.mImpl.onConfigurationChanged();
    }
    while (this.mSystemToUserCallbacks == null) {
      return;
    }
    paramConfiguration = this.mSystemToUserCallbacks.getNonSystemUserRecentsForUser(i);
    if (paramConfiguration != null) {
      try
      {
        paramConfiguration.onConfigurationChanged();
        return;
      }
      catch (RemoteException paramConfiguration)
      {
        Log.e("Recents", "Callback failed", paramConfiguration);
        return;
      }
    }
    Log.e("Recents", "No SystemUI callbacks found for user: " + i);
  }
  
  public void onDraggingInRecents(float paramFloat)
  {
    if (sSystemServicesProxy.isSystemUser(this.mDraggingInRecentsCurrentUser)) {
      this.mImpl.onDraggingInRecents(paramFloat);
    }
    while (this.mSystemToUserCallbacks == null) {
      return;
    }
    IRecentsNonSystemUserCallbacks localIRecentsNonSystemUserCallbacks = this.mSystemToUserCallbacks.getNonSystemUserRecentsForUser(this.mDraggingInRecentsCurrentUser);
    if (localIRecentsNonSystemUserCallbacks != null) {
      try
      {
        localIRecentsNonSystemUserCallbacks.onDraggingInRecents(paramFloat);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("Recents", "Callback failed", localRemoteException);
        return;
      }
    }
    Log.e("Recents", "No SystemUI callbacks found for user: " + this.mDraggingInRecentsCurrentUser);
  }
  
  public void onDraggingInRecentsEnded(float paramFloat)
  {
    if (sSystemServicesProxy.isSystemUser(this.mDraggingInRecentsCurrentUser)) {
      this.mImpl.onDraggingInRecentsEnded(paramFloat);
    }
    while (this.mSystemToUserCallbacks == null) {
      return;
    }
    IRecentsNonSystemUserCallbacks localIRecentsNonSystemUserCallbacks = this.mSystemToUserCallbacks.getNonSystemUserRecentsForUser(this.mDraggingInRecentsCurrentUser);
    if (localIRecentsNonSystemUserCallbacks != null) {
      try
      {
        localIRecentsNonSystemUserCallbacks.onDraggingInRecentsEnded(paramFloat);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("Recents", "Callback failed", localRemoteException);
        return;
      }
    }
    Log.e("Recents", "No SystemUI callbacks found for user: " + this.mDraggingInRecentsCurrentUser);
  }
  
  public void preloadRecents()
  {
    if (!isUserSetup()) {
      return;
    }
    int i = sSystemServicesProxy.getCurrentUser();
    if (sSystemServicesProxy.isSystemUser(i)) {
      this.mImpl.preloadRecents();
    }
    while (this.mSystemToUserCallbacks == null) {
      return;
    }
    IRecentsNonSystemUserCallbacks localIRecentsNonSystemUserCallbacks = this.mSystemToUserCallbacks.getNonSystemUserRecentsForUser(i);
    if (localIRecentsNonSystemUserCallbacks != null) {
      try
      {
        localIRecentsNonSystemUserCallbacks.preloadRecents();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("Recents", "Callback failed", localRemoteException);
        return;
      }
    }
    Log.e("Recents", "No SystemUI callbacks found for user: " + i);
  }
  
  public void showNextAffiliatedTask()
  {
    if (!isUserSetup()) {
      return;
    }
    this.mImpl.showNextAffiliatedTask();
  }
  
  public void showPrevAffiliatedTask()
  {
    if (!isUserSetup()) {
      return;
    }
    this.mImpl.showPrevAffiliatedTask();
  }
  
  public void showRecents(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!isUserSetup()) {
      return;
    }
    if (proxyToOverridePackage("com.android.systemui.recents.ACTION_SHOW")) {
      return;
    }
    int i = ((Divider)getComponent(Divider.class)).getView().growsRecents();
    int j = sSystemServicesProxy.getCurrentUser();
    if (sSystemServicesProxy.isSystemUser(j)) {
      this.mImpl.showRecents(paramBoolean1, false, true, false, paramBoolean2, i);
    }
    while (this.mSystemToUserCallbacks == null) {
      return;
    }
    IRecentsNonSystemUserCallbacks localIRecentsNonSystemUserCallbacks = this.mSystemToUserCallbacks.getNonSystemUserRecentsForUser(j);
    if (localIRecentsNonSystemUserCallbacks != null) {
      try
      {
        localIRecentsNonSystemUserCallbacks.showRecents(paramBoolean1, false, true, false, paramBoolean2, i);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("Recents", "Callback failed", localRemoteException);
        return;
      }
    }
    Log.e("Recents", "No SystemUI callbacks found for user: " + j);
  }
  
  public void start()
  {
    sDebugFlags = new RecentsDebugFlags(this.mContext);
    sSystemServicesProxy = SystemServicesProxy.getInstance(this.mContext);
    sTaskLoader = new RecentsTaskLoader(this.mContext);
    sConfiguration = new RecentsConfiguration(this.mContext);
    this.mHandler = new Handler();
    Object localObject;
    if (((UiModeManager)this.mContext.getSystemService("uimode")).getCurrentModeType() == 4)
    {
      this.mImpl = new RecentsTvImpl(this.mContext);
      if (("userdebug".equals(Build.TYPE)) || ("eng".equals(Build.TYPE)))
      {
        localObject = SystemProperties.get("persist.recents_override_pkg");
        if (!((String)localObject).isEmpty()) {
          this.mOverrideRecentsPackageName = ((String)localObject);
        }
      }
      EventBus.getDefault().register(this, 1);
      EventBus.getDefault().register(sSystemServicesProxy, 1);
      EventBus.getDefault().register(sTaskLoader, 1);
      int i = sSystemServicesProxy.getProcessUser();
      if (!sSystemServicesProxy.isSystemUser(i)) {
        break label275;
      }
      this.mSystemToUserCallbacks = new RecentsSystemUser(this.mContext, this.mImpl);
    }
    for (;;)
    {
      putComponent(Recents.class, this);
      localObject = new IntentFilter();
      ((IntentFilter)localObject).addAction("android.intent.action.USER_FOREGROUND");
      this.mContext.getApplicationContext().registerReceiverAsUser(new UserSwitchReceiver(null), UserHandle.CURRENT, (IntentFilter)localObject, null, this.mHandler);
      return;
      this.mImpl = new RecentsImpl(this.mContext);
      break;
      label275:
      registerWithSystemUser();
    }
  }
  
  public void toggleRecents(Display paramDisplay)
  {
    if (!isUserSetup()) {
      return;
    }
    if (proxyToOverridePackage("com.android.systemui.recents.ACTION_TOGGLE")) {
      return;
    }
    int i = ((Divider)getComponent(Divider.class)).getView().growsRecents();
    int j = sSystemServicesProxy.getCurrentUser();
    if (sSystemServicesProxy.isSystemUser(j)) {
      this.mImpl.toggleRecents(i);
    }
    while (this.mSystemToUserCallbacks == null) {
      return;
    }
    paramDisplay = this.mSystemToUserCallbacks.getNonSystemUserRecentsForUser(j);
    if (paramDisplay != null) {
      try
      {
        paramDisplay.toggleRecents(i);
        return;
      }
      catch (RemoteException paramDisplay)
      {
        Log.e("Recents", "Callback failed", paramDisplay);
        return;
      }
    }
    Log.e("Recents", "No SystemUI callbacks found for user: " + j);
  }
  
  private class UserSwitchReceiver
    extends BroadcastReceiver
  {
    private UserSwitchReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (paramIntent.getAction().equals("android.intent.action.USER_FOREGROUND")) {
        WhiteList.init(Recents.this.mContext, Recents.-get0(Recents.this));
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\Recents.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */