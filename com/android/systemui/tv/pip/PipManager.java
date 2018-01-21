package com.android.systemui.tv.pip;

import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityManager.StackInfo;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Rect;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.android.systemui.Prefs;
import com.android.systemui.recents.misc.SystemServicesProxy;
import com.android.systemui.recents.misc.SystemServicesProxy.TaskStackListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PipManager
{
  private static final boolean DEBUG_FORCE_ONBOARDING = SystemProperties.getBoolean("debug.tv.pip_force_onboarding", false);
  private static PipManager sPipManager;
  private static final List<Pair<String, String>> sSettingsPackageAndClassNamePairList = new ArrayList();
  private final MediaSessionManager.OnActiveSessionsChangedListener mActiveMediaSessionListener = new MediaSessionManager.OnActiveSessionsChangedListener()
  {
    public void onActiveSessionsChanged(List<MediaController> paramAnonymousList)
    {
      PipManager.-wrap7(PipManager.this, paramAnonymousList);
    }
  };
  private IActivityManager mActivityManager;
  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.intent.action.MEDIA_RESOURCE_GRANTED".equals(paramAnonymousIntent.getAction()))
      {
        paramAnonymousContext = paramAnonymousIntent.getStringArrayExtra("android.intent.extra.PACKAGES");
        int i = paramAnonymousIntent.getIntExtra("android.intent.extra.MEDIA_RESOURCE_TYPE", -1);
        if ((paramAnonymousContext != null) && (paramAnonymousContext.length > 0) && (i == 0)) {
          PipManager.-wrap3(PipManager.this, paramAnonymousContext);
        }
      }
    }
  };
  private final Runnable mClosePipRunnable = new Runnable()
  {
    public void run()
    {
      PipManager.this.closePip();
    }
  };
  private Context mContext;
  private Rect mCurrentPipBounds;
  private Rect mDefaultPipBounds;
  private final Handler mHandler = new Handler();
  private boolean mInitialized;
  private String[] mLastPackagesResourceGranted;
  private List<Listener> mListeners = new ArrayList();
  private List<MediaListener> mMediaListeners = new ArrayList();
  private MediaSessionManager mMediaSessionManager;
  private Rect mMenuModePipBounds;
  private boolean mOnboardingShown;
  private Rect mPipBounds;
  private ComponentName mPipComponentName;
  private MediaController mPipMediaController;
  private PipRecentsOverlayManager mPipRecentsOverlayManager;
  private int mPipTaskId = -1;
  private int mRecentsFocusChangedAnimationDurationMs;
  private Rect mRecentsFocusedPipBounds;
  private Rect mRecentsPipBounds;
  private final Runnable mResizePinnedStackRunnable = new Runnable()
  {
    public void run()
    {
      PipManager.this.resizePinnedStack(PipManager.-get8(PipManager.this));
    }
  };
  private Rect mSettingsPipBounds;
  private int mState = 0;
  private int mSuspendPipResizingReason;
  private SystemServicesProxy.TaskStackListener mTaskStackListener = new SystemServicesProxy.TaskStackListener()
  {
    public void onActivityPinned()
    {
      ActivityManager.StackInfo localStackInfo = PipManager.-wrap0(PipManager.this);
      if (localStackInfo == null)
      {
        Log.w("PipManager", "Cannot find pinned stack");
        return;
      }
      PipManager.-set3(PipManager.this, localStackInfo.taskIds[(localStackInfo.taskIds.length - 1)]);
      PipManager.-set2(PipManager.this, ComponentName.unflattenFromString(localStackInfo.taskNames[(localStackInfo.taskNames.length - 1)]));
      PipManager.-set4(PipManager.this, 1);
      PipManager.-set0(PipManager.this, PipManager.-get4(PipManager.this));
      PipManager.-wrap4(PipManager.this);
      PipManager.-get3(PipManager.this).addOnActiveSessionsChangedListener(PipManager.-get0(PipManager.this), null);
      PipManager.-wrap7(PipManager.this, PipManager.-get3(PipManager.this).getActiveSessions(null));
      if (PipManager.-get5(PipManager.this).isRecentsShown()) {
        PipManager.this.resizePinnedStack(3);
      }
      int i = PipManager.-get2(PipManager.this).size() - 1;
      while (i >= 0)
      {
        ((PipManager.Listener)PipManager.-get2(PipManager.this).get(i)).onPipEntered();
        i -= 1;
      }
      PipManager.-wrap8(PipManager.this, true);
    }
    
    public void onPinnedActivityRestartAttempt()
    {
      PipManager.this.movePipToFullscreen();
    }
    
    public void onPinnedStackAnimationEnded()
    {
      switch (PipManager.-get8(PipManager.this))
      {
      default: 
        return;
      case 1: 
        if (!PipManager.-get5(PipManager.this).isRecentsShown())
        {
          PipManager.-wrap6(PipManager.this);
          return;
        }
        PipManager.this.resizePinnedStack(PipManager.-get8(PipManager.this));
        return;
      case 3: 
      case 4: 
        PipManager.-get5(PipManager.this).addPipRecentsOverlayView();
        return;
      }
      PipManager.-wrap5(PipManager.this);
    }
    
    public void onTaskStackChanged()
    {
      if (PipManager.-get8(PipManager.this) != 0)
      {
        int k = 0;
        localObject = PipManager.-wrap0(PipManager.this);
        if ((localObject == null) || (((ActivityManager.StackInfo)localObject).taskIds == null))
        {
          Log.w("PipManager", "There is nothing in pinned stack");
          PipManager.-wrap2(PipManager.this, false);
          return;
        }
        int i = ((ActivityManager.StackInfo)localObject).taskIds.length - 1;
        for (;;)
        {
          int j = k;
          if (i >= 0)
          {
            if (localObject.taskIds[i] == PipManager.-get6(PipManager.this)) {
              j = 1;
            }
          }
          else
          {
            if (j != 0) {
              break;
            }
            PipManager.-wrap2(PipManager.this, true);
            return;
          }
          i -= 1;
        }
      }
      if (PipManager.-get8(PipManager.this) == 1) {
        if (!PipManager.-wrap1(PipManager.this)) {
          break label166;
        }
      }
      label166:
      for (Object localObject = PipManager.-get7(PipManager.this);; localObject = PipManager.-get1(PipManager.this))
      {
        if (PipManager.-get4(PipManager.this) != localObject)
        {
          PipManager.-set1(PipManager.this, (Rect)localObject);
          PipManager.this.resizePinnedStack(1);
        }
        return;
      }
    }
  };
  
  static
  {
    sSettingsPackageAndClassNamePairList.add(new Pair("com.android.tv.settings", null));
    sSettingsPackageAndClassNamePairList.add(new Pair("com.google.android.leanbacklauncher", "com.google.android.leanbacklauncher.settings.HomeScreenSettingsActivity"));
    sSettingsPackageAndClassNamePairList.add(new Pair("com.google.android.apps.mediashell", "com.google.android.apps.mediashell.settings.CastSettingsActivity"));
    sSettingsPackageAndClassNamePairList.add(new Pair("com.google.android.katniss", "com.google.android.katniss.setting.SpeechSettingsActivity"));
    sSettingsPackageAndClassNamePairList.add(new Pair("com.google.android.katniss", "com.google.android.katniss.setting.SearchSettingsActivity"));
    sSettingsPackageAndClassNamePairList.add(new Pair("com.google.android.gsf.notouch", "com.google.android.gsf.notouch.UsageDiagnosticsSettingActivity"));
  }
  
  private void closePipInternal(boolean paramBoolean)
  {
    this.mState = 0;
    this.mPipTaskId = -1;
    this.mPipMediaController = null;
    this.mMediaSessionManager.removeOnActiveSessionsChangedListener(this.mActiveMediaSessionListener);
    if (paramBoolean) {}
    try
    {
      this.mActivityManager.removeStack(4);
      int i = this.mListeners.size() - 1;
      while (i >= 0)
      {
        ((Listener)this.mListeners.get(i)).onPipActivityClosed();
        i -= 1;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e("PipManager", "removeStack failed", localRemoteException);
      }
      this.mHandler.removeCallbacks(this.mClosePipRunnable);
      updatePipVisibility(false);
    }
  }
  
  public static PipManager getInstance()
  {
    if (sPipManager == null) {
      sPipManager = new PipManager();
    }
    return sPipManager;
  }
  
  private ActivityManager.StackInfo getPinnedStackInfo()
  {
    try
    {
      ActivityManager.StackInfo localStackInfo = this.mActivityManager.getStackInfo(4);
      return localStackInfo;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("PipManager", "getStackInfo failed", localRemoteException);
    }
    return null;
  }
  
  private void handleMediaResourceGranted(String[] paramArrayOfString)
  {
    if (this.mState == 0) {
      this.mLastPackagesResourceGranted = paramArrayOfString;
    }
    int k;
    do
    {
      return;
      k = 0;
      int i = 0;
      if (this.mLastPackagesResourceGranted != null)
      {
        String[] arrayOfString = this.mLastPackagesResourceGranted;
        int n = arrayOfString.length;
        int j = 0;
        k = i;
        if (j < n)
        {
          String str = arrayOfString[j];
          int i1 = paramArrayOfString.length;
          int m = 0;
          for (;;)
          {
            k = i;
            if (m < i1)
            {
              if (TextUtils.equals(paramArrayOfString[m], str)) {
                k = 1;
              }
            }
            else
            {
              j += 1;
              i = k;
              break;
            }
            m += 1;
          }
        }
      }
      this.mLastPackagesResourceGranted = paramArrayOfString;
    } while (k != 0);
    closePip();
  }
  
  private boolean isSettingsShown()
  {
    try
    {
      List localList = this.mActivityManager.getTasks(1, 0);
      if (localList != null)
      {
        int i = localList.size();
        if (i != 0) {}
      }
      else
      {
        return false;
      }
    }
    catch (RemoteException localRemoteException)
    {
      Log.d("PipManager", "Failed to detect top activity", localRemoteException);
      return false;
    }
    ComponentName localComponentName = ((ActivityManager.RunningTaskInfo)localRemoteException.get(0)).topActivity;
    Iterator localIterator = sSettingsPackageAndClassNamePairList.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (Pair)localIterator.next();
      String str = (String)((Pair)localObject).first;
      if (localComponentName.getPackageName().equals(str))
      {
        localObject = (String)((Pair)localObject).second;
        if ((localObject == null) || (localComponentName.getClassName().equals(localObject))) {
          return true;
        }
      }
    }
    return false;
  }
  
  private void launchPipOnboardingActivityIfNeeded()
  {
    if ((!DEBUG_FORCE_ONBOARDING) && (this.mOnboardingShown)) {
      return;
    }
    this.mOnboardingShown = true;
    Prefs.putBoolean(this.mContext, "TvPictureInPictureOnboardingShown", true);
    Intent localIntent = new Intent(this.mContext, PipOnboardingActivity.class);
    localIntent.setFlags(268435456);
    this.mContext.startActivity(localIntent);
  }
  
  private void loadConfigurationsAndApply()
  {
    Object localObject = this.mContext.getResources();
    this.mDefaultPipBounds = Rect.unflattenFromString(((Resources)localObject).getString(17039472));
    this.mSettingsPipBounds = Rect.unflattenFromString(((Resources)localObject).getString(2131689916));
    this.mMenuModePipBounds = Rect.unflattenFromString(((Resources)localObject).getString(2131689917));
    this.mRecentsPipBounds = Rect.unflattenFromString(((Resources)localObject).getString(2131689918));
    this.mRecentsFocusedPipBounds = Rect.unflattenFromString(((Resources)localObject).getString(2131689919));
    this.mRecentsFocusChangedAnimationDurationMs = ((Resources)localObject).getInteger(2131624027);
    if (isSettingsShown())
    {
      localObject = this.mSettingsPipBounds;
      this.mPipBounds = ((Rect)localObject);
      if (getPinnedStackInfo() != null) {
        break label129;
      }
    }
    label129:
    for (int i = 0;; i = 1)
    {
      resizePinnedStack(i);
      return;
      localObject = this.mDefaultPipBounds;
      break;
    }
  }
  
  private void showPipMenu()
  {
    if (this.mPipRecentsOverlayManager.isRecentsShown()) {
      return;
    }
    this.mState = 2;
    int i = this.mListeners.size() - 1;
    while (i >= 0)
    {
      ((Listener)this.mListeners.get(i)).onShowPipMenu();
      i -= 1;
    }
    Intent localIntent = new Intent(this.mContext, PipMenuActivity.class);
    localIntent.setFlags(268435456);
    this.mContext.startActivity(localIntent);
  }
  
  private void showPipOverlay()
  {
    PipOverlayActivity.showPipOverlay(this.mContext);
  }
  
  private void updateMediaController(List<MediaController> paramList)
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    int i;
    if (paramList != null)
    {
      localObject1 = localObject2;
      if (this.mState != 0)
      {
        localObject1 = localObject2;
        if (this.mPipComponentName != null) {
          i = paramList.size() - 1;
        }
      }
    }
    for (;;)
    {
      localObject1 = localObject2;
      if (i >= 0)
      {
        localObject1 = (MediaController)paramList.get(i);
        if (!((MediaController)localObject1).getPackageName().equals(this.mPipComponentName.getPackageName())) {}
      }
      else
      {
        if (this.mPipMediaController == localObject1) {
          break label157;
        }
        this.mPipMediaController = ((MediaController)localObject1);
        i = this.mMediaListeners.size() - 1;
        while (i >= 0)
        {
          ((MediaListener)this.mMediaListeners.get(i)).onMediaControllerChanged();
          i -= 1;
        }
      }
      i -= 1;
    }
    if (this.mPipMediaController == null)
    {
      this.mHandler.postDelayed(this.mClosePipRunnable, 3000L);
      label157:
      return;
    }
    this.mHandler.removeCallbacks(this.mClosePipRunnable);
  }
  
  private void updatePipVisibility(boolean paramBoolean)
  {
    SystemServicesProxy.getInstance(this.mContext).setTvPipVisibility(paramBoolean);
  }
  
  public void addListener(Listener paramListener)
  {
    this.mListeners.add(paramListener);
  }
  
  public void addMediaListener(MediaListener paramMediaListener)
  {
    this.mMediaListeners.add(paramMediaListener);
  }
  
  public void closePip()
  {
    closePipInternal(true);
  }
  
  MediaController getMediaController()
  {
    return this.mPipMediaController;
  }
  
  public PipRecentsOverlayManager getPipRecentsOverlayManager()
  {
    return this.mPipRecentsOverlayManager;
  }
  
  int getPlaybackState()
  {
    if ((this.mPipMediaController == null) || (this.mPipMediaController.getPlaybackState() == null)) {
      return 2;
    }
    int i = this.mPipMediaController.getPlaybackState().getState();
    if ((i == 6) || (i == 8)) {
      i = 1;
    }
    long l;
    for (;;)
    {
      l = this.mPipMediaController.getPlaybackState().getActions();
      if ((i != 0) || ((0x4 & l) == 0L)) {
        break label108;
      }
      return 1;
      if ((i == 3) || (i == 4) || (i == 5) || (i == 9)) {
        break;
      }
      if (i == 10) {
        i = 1;
      } else {
        i = 0;
      }
    }
    label108:
    if ((i != 0) && ((0x2 & l) != 0L)) {
      return 0;
    }
    return 2;
  }
  
  public Rect getRecentsFocusedPipBounds()
  {
    return this.mRecentsFocusedPipBounds;
  }
  
  public void initialize(Context paramContext)
  {
    if (this.mInitialized) {
      return;
    }
    this.mInitialized = true;
    this.mContext = paramContext;
    this.mActivityManager = ActivityManagerNative.getDefault();
    SystemServicesProxy.getInstance(paramContext).registerTaskStackListener(this.mTaskStackListener);
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.MEDIA_RESOURCE_GRANTED");
    this.mContext.registerReceiver(this.mBroadcastReceiver, localIntentFilter);
    this.mOnboardingShown = Prefs.getBoolean(this.mContext, "TvPictureInPictureOnboardingShown", false);
    loadConfigurationsAndApply();
    this.mPipRecentsOverlayManager = new PipRecentsOverlayManager(paramContext);
    this.mMediaSessionManager = ((MediaSessionManager)this.mContext.getSystemService("media_session"));
  }
  
  public boolean isPipShown()
  {
    boolean bool = false;
    if (this.mState != 0) {
      bool = true;
    }
    return bool;
  }
  
  void movePipToFullscreen()
  {
    this.mState = 0;
    this.mPipTaskId = -1;
    int i = this.mListeners.size() - 1;
    while (i >= 0)
    {
      ((Listener)this.mListeners.get(i)).onMoveToFullscreen();
      i -= 1;
    }
    resizePinnedStack(this.mState);
  }
  
  void onConfigurationChanged()
  {
    loadConfigurationsAndApply();
    this.mPipRecentsOverlayManager.onConfigurationChanged(this.mContext);
  }
  
  public void removeListener(Listener paramListener)
  {
    this.mListeners.remove(paramListener);
  }
  
  public void removeMediaListener(MediaListener paramMediaListener)
  {
    this.mMediaListeners.remove(paramMediaListener);
  }
  
  void resizePinnedStack(int paramInt)
  {
    if ((this.mState == 3) || (this.mState == 4)) {}
    for (int i = 1;; i = 0)
    {
      this.mState = paramInt;
      paramInt = this.mListeners.size() - 1;
      while (paramInt >= 0)
      {
        ((Listener)this.mListeners.get(paramInt)).onPipResizeAboutToStart();
        paramInt -= 1;
      }
    }
    if (this.mSuspendPipResizingReason != 0) {
      return;
    }
    switch (this.mState)
    {
    default: 
      this.mCurrentPipBounds = this.mPipBounds;
    }
    for (;;)
    {
      int j = -1;
      paramInt = j;
      if (i != 0) {}
      try
      {
        if (this.mState != 3)
        {
          paramInt = j;
          if (this.mState != 4) {}
        }
        else
        {
          paramInt = this.mRecentsFocusChangedAnimationDurationMs;
        }
        this.mActivityManager.resizeStack(4, this.mCurrentPipBounds, true, true, true, paramInt);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("PipManager", "resizeStack failed", localRemoteException);
      }
      this.mCurrentPipBounds = null;
      continue;
      this.mCurrentPipBounds = this.mMenuModePipBounds;
      continue;
      this.mCurrentPipBounds = this.mPipBounds;
      continue;
      this.mCurrentPipBounds = this.mRecentsPipBounds;
      continue;
      this.mCurrentPipBounds = this.mRecentsFocusedPipBounds;
    }
  }
  
  public void resumePipResizing(int paramInt)
  {
    if ((this.mSuspendPipResizingReason & paramInt) == 0) {
      return;
    }
    this.mSuspendPipResizingReason &= paramInt;
    this.mHandler.post(this.mResizePinnedStackRunnable);
  }
  
  public void showTvPictureInPictureMenu()
  {
    if (this.mState == 1) {
      resizePinnedStack(2);
    }
  }
  
  public void suspendPipResizing(int paramInt)
  {
    this.mSuspendPipResizingReason |= paramInt;
  }
  
  public static abstract interface Listener
  {
    public abstract void onMoveToFullscreen();
    
    public abstract void onPipActivityClosed();
    
    public abstract void onPipEntered();
    
    public abstract void onPipResizeAboutToStart();
    
    public abstract void onShowPipMenu();
  }
  
  public static abstract interface MediaListener
  {
    public abstract void onMediaControllerChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tv\pip\PipManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */