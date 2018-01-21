package com.android.systemui.recents.misc;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityManager.StackInfo;
import android.app.ActivityManager.TaskDescription;
import android.app.ActivityManager.TaskThumbnail;
import android.app.ActivityManagerNative;
import android.app.ActivityOptions;
import android.app.AppGlobals;
import android.app.IActivityManager;
import android.app.ITaskStackListener.Stub;
import android.app.UiModeManager;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IRemoteCallback;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.util.ArraySet;
import android.util.Log;
import android.util.MutableBoolean;
import android.view.Display;
import android.view.IAppTransitionAnimationSpecsFuture;
import android.view.IDockedStackListener;
import android.view.IWindowManager;
import android.view.WindowManager;
import android.view.WindowManager.KeyboardShortcutsReceiver;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.app.AssistUtils;
import com.android.internal.os.BackgroundThread;
import com.android.systemui.recents.LockStateController;
import com.android.systemui.recents.WhiteList;
import com.android.systemui.recents.model.Task.TaskKey;
import com.android.systemui.recents.model.ThumbnailData;
import com.android.systemui.util.SpecialCheck;
import com.android.systemui.util.Utils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SystemServicesProxy
{
  private static boolean mDeepCleaning;
  static final HandlerThread sBgThread;
  static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
  private static String sCurrentDockedPackageName = null;
  private static String sCurrentInputMethodPackageName;
  private static String sCurrentLauncherPackageName;
  private static String sCurrentWallpaperPackageName;
  static final List<String> sRecentsBlacklist;
  private static SystemServicesProxy sSystemServicesProxy;
  AccessibilityManager mAccm;
  ActivityManager mAm;
  AppWidgetManager mAppWidgetManager;
  ComponentName mAssistComponent;
  AssistUtils mAssistUtils;
  Canvas mBgProtectionCanvas;
  Paint mBgProtectionPaint;
  Handler mBgThreadHandler;
  Context mContext;
  Display mDisplay;
  int mDummyThumbnailHeight;
  int mDummyThumbnailWidth;
  private final Handler mHandler = new H(null);
  boolean mHasFreeformWorkspaceSupport;
  IActivityManager mIam;
  IPackageManager mIpm;
  boolean mIsSafeMode;
  IWindowManager mIwm;
  PackageManager mPm;
  String mRecentsPackage;
  private ITaskStackListener.Stub mTaskStackListener = new ITaskStackListener.Stub()
  {
    public void onActivityDismissingDockedStack()
      throws RemoteException
    {
      SystemServicesProxy.-get1(SystemServicesProxy.this).sendEmptyMessage(6);
    }
    
    public void onActivityForcedResizable(String paramAnonymousString, int paramAnonymousInt)
      throws RemoteException
    {
      SystemServicesProxy.-get1(SystemServicesProxy.this).obtainMessage(5, paramAnonymousInt, 0, paramAnonymousString).sendToTarget();
    }
    
    public void onActivityPinned()
      throws RemoteException
    {
      SystemServicesProxy.-get1(SystemServicesProxy.this).removeMessages(2);
      SystemServicesProxy.-get1(SystemServicesProxy.this).sendEmptyMessage(2);
    }
    
    public void onPinnedActivityRestartAttempt()
      throws RemoteException
    {
      SystemServicesProxy.-get1(SystemServicesProxy.this).removeMessages(3);
      SystemServicesProxy.-get1(SystemServicesProxy.this).sendEmptyMessage(3);
    }
    
    public void onPinnedStackAnimationEnded()
      throws RemoteException
    {
      SystemServicesProxy.-get1(SystemServicesProxy.this).removeMessages(4);
      SystemServicesProxy.-get1(SystemServicesProxy.this).sendEmptyMessage(4);
    }
    
    public void onTaskStackChanged()
      throws RemoteException
    {
      SystemServicesProxy.-get1(SystemServicesProxy.this).removeMessages(1);
      SystemServicesProxy.-get1(SystemServicesProxy.this).sendEmptyMessage(1);
    }
  };
  private List<TaskStackListener> mTaskStackListeners = new ArrayList();
  UserManager mUm;
  private List<String> mWidgetPkgList;
  WindowManager mWm;
  
  static
  {
    sBitmapOptions.inMutable = true;
    sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
    sRecentsBlacklist = new ArrayList();
    sRecentsBlacklist.add("com.android.systemui.tv.pip.PipOnboardingActivity");
    sRecentsBlacklist.add("com.android.systemui.tv.pip.PipMenuActivity");
    mDeepCleaning = false;
    sBgThread = new HandlerThread("Recents-SystemServicesProxy", 10);
    sBgThread.start();
    sCurrentInputMethodPackageName = null;
    sCurrentLauncherPackageName = null;
    sCurrentWallpaperPackageName = null;
  }
  
  private SystemServicesProxy(Context paramContext)
  {
    this.mContext = paramContext;
    this.mBgThreadHandler = new Handler(sBgThread.getLooper());
    this.mAccm = AccessibilityManager.getInstance(paramContext);
    this.mAm = ((ActivityManager)paramContext.getSystemService("activity"));
    this.mIam = ActivityManagerNative.getDefault();
    this.mPm = paramContext.getPackageManager();
    this.mIpm = AppGlobals.getPackageManager();
    this.mAssistUtils = new AssistUtils(paramContext);
    this.mWm = ((WindowManager)paramContext.getSystemService("window"));
    this.mIwm = WindowManagerGlobal.getWindowManagerService();
    this.mUm = UserManager.get(paramContext);
    this.mDisplay = this.mWm.getDefaultDisplay();
    this.mRecentsPackage = paramContext.getPackageName();
    boolean bool;
    if (!this.mPm.hasSystemFeature("android.software.freeform_window_management"))
    {
      if (Settings.Global.getInt(paramContext.getContentResolver(), "enable_freeform_support", 0) == 0) {
        break label354;
      }
      bool = true;
    }
    Resources localResources;
    for (;;)
    {
      this.mHasFreeformWorkspaceSupport = bool;
      this.mIsSafeMode = this.mPm.isSafeMode();
      this.mAppWidgetManager = ((AppWidgetManager)this.mContext.getSystemService(AppWidgetManager.class));
      localResources = paramContext.getResources();
      this.mDummyThumbnailWidth = localResources.getDimensionPixelSize(17104898);
      this.mDummyThumbnailHeight = localResources.getDimensionPixelSize(17104897);
      this.mBgProtectionPaint = new Paint();
      this.mBgProtectionPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
      this.mBgProtectionPaint.setColor(-1);
      this.mBgProtectionCanvas = new Canvas();
      this.mAssistComponent = this.mAssistUtils.getAssistComponentForUser(UserHandle.myUserId());
      if (((UiModeManager)paramContext.getSystemService("uimode")).getCurrentModeType() != 4) {
        break;
      }
      Collections.addAll(sRecentsBlacklist, localResources.getStringArray(2131427613));
      return;
      bool = true;
      continue;
      label354:
      bool = false;
    }
    Collections.addAll(sRecentsBlacklist, localResources.getStringArray(2131427609));
  }
  
  private boolean canThisProcessBeKilled(String paramString)
  {
    if (sCurrentInputMethodPackageName == null) {
      sCurrentInputMethodPackageName = getCurrentInputMethod();
    }
    if (sCurrentLauncherPackageName == null) {
      sCurrentLauncherPackageName = getCurrentLauncher();
    }
    if (sCurrentWallpaperPackageName == null) {
      sCurrentWallpaperPackageName = getWallpaperPackageNameIfAvailable();
    }
    if (WhiteList.isInWhiteList(paramString)) {
      return false;
    }
    if ((sCurrentWallpaperPackageName != null) && (sCurrentWallpaperPackageName.equals(paramString)))
    {
      Log.d("SystemServicesProxy", "wallpaper: " + paramString);
      return false;
    }
    if ((sCurrentInputMethodPackageName != null) && (sCurrentInputMethodPackageName.equals(paramString)))
    {
      Log.d("SystemServicesProxy", "IME: " + paramString);
      return false;
    }
    if ((sCurrentLauncherPackageName != null) && (sCurrentLauncherPackageName.equals(paramString)))
    {
      Log.d("SystemServicesProxy", "launcher: " + paramString);
      return false;
    }
    if ((sCurrentDockedPackageName != null) && (sCurrentDockedPackageName.equals(paramString)))
    {
      Log.d("SystemServicesProxy", "docked: " + paramString);
      return false;
    }
    if ((this.mWidgetPkgList != null) && (this.mWidgetPkgList.contains(paramString)))
    {
      Log.d("SystemServicesProxy", "widget: " + paramString);
      return false;
    }
    return true;
  }
  
  private void forceStopPackage(String paramString, int paramInt)
  {
    Log.d("SystemServicesProxy", "Task is killed:" + paramString + ", " + paramInt);
    this.mAm.forceStopPackageAsUser(paramString, paramInt);
  }
  
  private Drawable getBadgedIcon(Drawable paramDrawable, int paramInt)
  {
    Drawable localDrawable = paramDrawable;
    if (paramInt != UserHandle.myUserId()) {
      localDrawable = this.mPm.getUserBadgedIcon(paramDrawable, new UserHandle(paramInt));
    }
    return localDrawable;
  }
  
  private String getBadgedLabel(String paramString, int paramInt)
  {
    String str = paramString;
    if (paramInt != UserHandle.myUserId()) {
      str = this.mPm.getUserBadgedLabel(paramString, new UserHandle(paramInt)).toString();
    }
    return str;
  }
  
  private String getCurrentInputMethod()
  {
    Object localObject1 = (InputMethodManager)this.mContext.getSystemService("input_method");
    if (localObject1 != null)
    {
      Object localObject2 = ((InputMethodManager)localObject1).getInputMethodList();
      if (((List)localObject2).size() > 0)
      {
        localObject1 = Settings.Secure.getString(this.mContext.getContentResolver(), "default_input_method");
        localObject2 = ((Iterable)localObject2).iterator();
        while (((Iterator)localObject2).hasNext())
        {
          InputMethodInfo localInputMethodInfo = (InputMethodInfo)((Iterator)localObject2).next();
          if (localInputMethodInfo.getId().equals(localObject1)) {
            return localInputMethodInfo.getPackageName();
          }
        }
      }
    }
    return null;
  }
  
  private String getCurrentLauncher()
  {
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.addCategory("android.intent.category.HOME");
    return this.mContext.getPackageManager().resolveActivity(localIntent, 65536).activityInfo.packageName;
  }
  
  public static SystemServicesProxy getInstance(Context paramContext)
  {
    if (!Looper.getMainLooper().isCurrentThread()) {
      throw new RuntimeException("Must be called on the UI thread");
    }
    if (sSystemServicesProxy == null) {
      sSystemServicesProxy = new SystemServicesProxy(paramContext);
    }
    return sSystemServicesProxy;
  }
  
  private String getWallpaperPackageNameIfAvailable()
  {
    String str = null;
    try
    {
      WallpaperManager localWallpaperManager = WallpaperManager.getInstance(this.mContext);
      if (localWallpaperManager.getWallpaperInfo() != null) {
        str = localWallpaperManager.getWallpaperInfo().getPackageName();
      }
      return str;
    }
    catch (Exception localException)
    {
      Log.e("SystemServicesProxy", "Couldn't get wallpaper info package name." + localException);
    }
    return null;
  }
  
  public static boolean isFreeformStack(int paramInt)
  {
    return paramInt == 2;
  }
  
  public static boolean isHomeStack(int paramInt)
  {
    boolean bool = false;
    if (paramInt == 0) {
      bool = true;
    }
    return bool;
  }
  
  private void preloadKillProcessLists()
  {
    sCurrentInputMethodPackageName = getCurrentInputMethod();
    sCurrentLauncherPackageName = getCurrentLauncher();
    sCurrentWallpaperPackageName = getWallpaperPackageNameIfAvailable();
    sCurrentDockedPackageName = null;
    Iterator localIterator = this.mAm.getRunningTasks(Integer.MAX_VALUE).iterator();
    while (localIterator.hasNext())
    {
      ActivityManager.RunningTaskInfo localRunningTaskInfo = (ActivityManager.RunningTaskInfo)localIterator.next();
      if (localRunningTaskInfo.stackId == 3) {
        sCurrentDockedPackageName = localRunningTaskInfo.baseActivity.getPackageName();
      }
    }
  }
  
  public void cancelThumbnailTransition(int paramInt)
  {
    if (this.mWm == null) {
      return;
    }
    try
    {
      WindowManagerGlobal.getWindowManagerService().cancelTaskThumbnailTransition(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.printStackTrace();
    }
  }
  
  public void cancelWindowTransition(int paramInt)
  {
    if (this.mWm == null) {
      return;
    }
    try
    {
      WindowManagerGlobal.getWindowManagerService().cancelTaskWindowTransition(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.printStackTrace();
    }
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    preloadKillProcessLists();
    initWidgetPkgList();
    paramPrintWriter.print("  isVisible=");
    paramPrintWriter.println(isRecentsActivityVisible());
    paramPrintWriter.print("  isDeepCleanEnable=");
    paramPrintWriter.println(Utils.isDeepCleanEnable(this.mContext));
    paramPrintWriter.print("  wallpaper pkg=");
    paramPrintWriter.println(sCurrentWallpaperPackageName);
    paramPrintWriter.print("  ime pkg=");
    paramPrintWriter.println(sCurrentInputMethodPackageName);
    paramPrintWriter.print("  launcher pkg=");
    paramPrintWriter.println(sCurrentLauncherPackageName);
    paramPrintWriter.print("  docked pkg=");
    paramPrintWriter.println(sCurrentDockedPackageName);
    if (this.mWidgetPkgList != null)
    {
      paramPrintWriter.println("  widget pkg=");
      int i = 0;
      while (i < this.mWidgetPkgList.size())
      {
        paramPrintWriter.print("    ");
        paramPrintWriter.println((String)this.mWidgetPkgList.get(i));
        i += 1;
      }
    }
    paramString = LockStateController.getInstance(this.mContext);
    if (paramString != null) {
      paramString.dump(paramPrintWriter);
    }
    WhiteList.dumpWhiteList(paramPrintWriter);
  }
  
  public void endProlongedAnimations()
  {
    if (this.mWm == null) {
      return;
    }
    try
    {
      WindowManagerGlobal.getWindowManagerService().endProlongedAnimations();
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public ActivityInfo getActivityInfo(ComponentName paramComponentName, int paramInt)
  {
    if (this.mIpm == null) {
      return null;
    }
    try
    {
      paramComponentName = this.mIpm.getActivityInfo(paramComponentName, 128, paramInt);
      return paramComponentName;
    }
    catch (RemoteException paramComponentName)
    {
      paramComponentName.printStackTrace();
    }
    return null;
  }
  
  public Drawable getBadgedActivityIcon(ActivityInfo paramActivityInfo, int paramInt)
  {
    if (this.mPm == null) {
      return null;
    }
    return getBadgedIcon(paramActivityInfo.loadIcon(this.mPm), paramInt);
  }
  
  public String getBadgedActivityLabel(ActivityInfo paramActivityInfo, int paramInt)
  {
    if (this.mPm == null) {
      return null;
    }
    return getBadgedLabel(paramActivityInfo.loadLabel(this.mPm).toString(), paramInt);
  }
  
  public Drawable getBadgedApplicationIcon(ApplicationInfo paramApplicationInfo, int paramInt)
  {
    if (this.mPm == null) {
      return null;
    }
    return getBadgedIcon(paramApplicationInfo.loadIcon(this.mPm), paramInt);
  }
  
  public String getBadgedApplicationLabel(ApplicationInfo paramApplicationInfo, int paramInt)
  {
    if (this.mPm == null) {
      return null;
    }
    return getBadgedLabel(paramApplicationInfo.loadLabel(this.mPm).toString(), paramInt);
  }
  
  public String getBadgedContentDescription(ActivityInfo paramActivityInfo, int paramInt, Resources paramResources)
  {
    String str1 = paramActivityInfo.loadLabel(this.mPm).toString();
    paramActivityInfo = paramActivityInfo.applicationInfo.loadLabel(this.mPm).toString();
    String str2 = getBadgedLabel(paramActivityInfo, paramInt);
    if (paramActivityInfo.equals(str1)) {
      return str2;
    }
    return paramResources.getString(2131690183, new Object[] { str2, str1 });
  }
  
  public Drawable getBadgedTaskDescriptionIcon(ActivityManager.TaskDescription paramTaskDescription, int paramInt, Resources paramResources)
  {
    Bitmap localBitmap2 = paramTaskDescription.getInMemoryIcon();
    Bitmap localBitmap1 = localBitmap2;
    if (localBitmap2 == null) {
      localBitmap1 = ActivityManager.TaskDescription.loadTaskDescriptionIcon(paramTaskDescription.getIconFilename(), paramInt);
    }
    if (localBitmap1 != null) {
      return getBadgedIcon(new BitmapDrawable(paramResources, localBitmap1), paramInt);
    }
    return null;
  }
  
  public int getCurrentUser()
  {
    if (this.mAm == null) {
      return 0;
    }
    ActivityManager localActivityManager = this.mAm;
    return ActivityManager.getCurrentUser();
  }
  
  public int getDeviceSmallestWidth()
  {
    if (this.mDisplay == null) {
      return 0;
    }
    Point localPoint1 = new Point();
    Point localPoint2 = new Point();
    this.mDisplay.getCurrentSizeRange(localPoint1, localPoint2);
    return localPoint1.x;
  }
  
  public Rect getDisplayRect()
  {
    Rect localRect = new Rect();
    if (this.mDisplay == null) {
      return localRect;
    }
    Point localPoint = new Point();
    this.mDisplay.getRealSize(localPoint);
    localRect.set(0, 0, localPoint.x, localPoint.y);
    return localRect;
  }
  
  public int getDockedDividerSize(Context paramContext)
  {
    paramContext = paramContext.getResources();
    return paramContext.getDimensionPixelSize(17104931) - paramContext.getDimensionPixelSize(17104932) * 2;
  }
  
  public int getProcessUser()
  {
    if (this.mUm == null) {
      return 0;
    }
    return this.mUm.getUserHandle();
  }
  
  public List<ActivityManager.RecentTaskInfo> getRecentTasks(int paramInt1, int paramInt2, boolean paramBoolean, ArraySet<Integer> paramArraySet)
  {
    if (this.mAm == null) {
      return null;
    }
    int j = Math.max(10, paramInt1);
    int i = 62;
    if (paramBoolean) {
      i = 63;
    }
    Object localObject = null;
    try
    {
      List localList = this.mAm.getRecentTasksForUser(j, i, paramInt2);
      localObject = localList;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.e("SystemServicesProxy", "Failed to get recent tasks", localException);
      }
      paramInt2 = 1;
      Iterator localIterator = ((List)localObject).iterator();
      ActivityManager.RecentTaskInfo localRecentTaskInfo;
      for (;;)
      {
        if (!localIterator.hasNext()) {
          break label387;
        }
        localRecentTaskInfo = (ActivityManager.RecentTaskInfo)localIterator.next();
        if ((!sRecentsBlacklist.contains(localRecentTaskInfo.realActivity.getClassName())) && (!sRecentsBlacklist.contains(localRecentTaskInfo.realActivity.getPackageName()))) {
          break;
        }
        localIterator.remove();
      }
      if ((localRecentTaskInfo.baseIntent.getFlags() & 0x800000) != 8388608) {
        break label381;
      }
      label381:
      for (i = 1;; i = 0)
      {
        int k = paramArraySet.contains(Integer.valueOf(localRecentTaskInfo.userId));
        i |= k;
        if (SpecialCheck.shouldNotExcludeTask(localRecentTaskInfo.baseIntent.getComponent())) {
          i = 0;
        }
        String str = localRecentTaskInfo.baseIntent.getComponent().getPackageName();
        if (Build.DEBUG_ONEPLUS) {
          Log.d("SystemServicesProxy", "getRecentTasks: " + str + ", " + localRecentTaskInfo.id + ", " + k + ", " + localRecentTaskInfo.userId);
        }
        if (((i != 0) && ((paramInt2 == 0) || (!paramBoolean))) || ("com.oneplus.applocker".equals(str)))
        {
          Log.d("SystemServicesProxy", "exclude " + str);
          localIterator.remove();
        }
        paramInt2 = 0;
        break;
      }
    }
    if (localObject == null)
    {
      Log.d("SystemServicesProxy", "no tasks");
      return new ArrayList();
    }
    label387:
    return ((List)localObject).subList(0, Math.min(((List)localObject).size(), paramInt1));
  }
  
  public ActivityManager.RunningTaskInfo getRunningTask()
  {
    List localList = this.mAm.getRunningTasks(1);
    if ((localList == null) || (localList.isEmpty())) {
      return null;
    }
    return (ActivityManager.RunningTaskInfo)localList.get(0);
  }
  
  public void getStableInsets(Rect paramRect)
  {
    if (this.mWm == null) {
      return;
    }
    try
    {
      WindowManagerGlobal.getWindowManagerService().getStableInsets(paramRect);
      return;
    }
    catch (Exception paramRect)
    {
      paramRect.printStackTrace();
    }
  }
  
  public int getSystemSetting(Context paramContext, String paramString)
  {
    return Settings.System.getInt(paramContext.getContentResolver(), paramString, 0);
  }
  
  public ThumbnailData getTaskThumbnail(int paramInt)
  {
    return getTaskThumbnail(paramInt, false);
  }
  
  public ThumbnailData getTaskThumbnail(int paramInt, boolean paramBoolean)
  {
    if (this.mAm == null) {
      return null;
    }
    ThumbnailData localThumbnailData = new ThumbnailData();
    getThumbnail(paramInt, localThumbnailData);
    if (paramBoolean) {
      localThumbnailData.thumbnail = null;
    }
    if (localThumbnailData.thumbnail != null)
    {
      localThumbnailData.thumbnail.setHasAlpha(false);
      if (Color.alpha(localThumbnailData.thumbnail.getPixel(0, 0)) == 0)
      {
        this.mBgProtectionCanvas.setBitmap(localThumbnailData.thumbnail);
        this.mBgProtectionCanvas.drawRect(0.0F, 0.0F, localThumbnailData.thumbnail.getWidth(), localThumbnailData.thumbnail.getHeight(), this.mBgProtectionPaint);
        this.mBgProtectionCanvas.setBitmap(null);
        Log.e("SystemServicesProxy", "Invalid screenshot detected from getTaskThumbnail()");
      }
    }
    return localThumbnailData;
  }
  
  public void getThumbnail(int paramInt, ThumbnailData paramThumbnailData)
  {
    if (this.mAm == null) {
      return;
    }
    ActivityManager.TaskThumbnail localTaskThumbnail = this.mAm.getTaskThumbnail(paramInt);
    if (localTaskThumbnail == null) {
      return;
    }
    Bitmap localBitmap2 = localTaskThumbnail.mainThumbnail;
    ParcelFileDescriptor localParcelFileDescriptor = localTaskThumbnail.thumbnailFileDescriptor;
    Bitmap localBitmap1 = localBitmap2;
    if (localBitmap2 == null)
    {
      localBitmap1 = localBitmap2;
      if (localParcelFileDescriptor != null) {
        localBitmap1 = BitmapFactory.decodeFileDescriptor(localParcelFileDescriptor.getFileDescriptor(), null, sBitmapOptions);
      }
    }
    if (localParcelFileDescriptor != null) {}
    try
    {
      localParcelFileDescriptor.close();
      paramThumbnailData.thumbnail = localBitmap1;
      paramThumbnailData.thumbnailInfo = localTaskThumbnail.thumbnailInfo;
      return;
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
  }
  
  public Rect getWindowRect()
  {
    Rect localRect = new Rect();
    if (this.mIam == null) {
      return localRect;
    }
    try
    {
      ActivityManager.StackInfo localStackInfo = this.mIam.getStackInfo(0);
      if (localStackInfo != null) {
        localRect.set(localStackInfo.bounds);
      }
      return localRect;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException = localRemoteException;
      localRemoteException.printStackTrace();
      return localRect;
    }
    finally {}
    return localRect;
  }
  
  public boolean hasDockedTask()
  {
    if (this.mIam == null) {
      return false;
    }
    Object localObject = null;
    try
    {
      ActivityManager.StackInfo localStackInfo = this.mIam.getStackInfo(3);
      localObject = localStackInfo;
    }
    catch (RemoteException localRemoteException)
    {
      int j;
      int i;
      for (;;)
      {
        localRemoteException.printStackTrace();
      }
      if (localObject.taskUserIds[i] != j) {
        break label89;
      }
      label89:
      for (boolean bool = true;; bool = false)
      {
        i -= 1;
        break;
      }
    }
    if (localObject != null)
    {
      j = getCurrentUser();
      bool = false;
      i = ((ActivityManager.StackInfo)localObject).taskUserIds.length - 1;
      if ((i < 0) || (bool)) {
        return bool;
      }
    }
    return false;
  }
  
  public boolean hasFreeformWorkspaceSupport()
  {
    return this.mHasFreeformWorkspaceSupport;
  }
  
  public boolean hasSoftNavigationBar()
  {
    try
    {
      boolean bool = WindowManagerGlobal.getWindowManagerService().hasNavigationBar();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.printStackTrace();
    }
    return false;
  }
  
  public boolean hasTransposedNavigationBar()
  {
    boolean bool = false;
    Rect localRect = new Rect();
    getStableInsets(localRect);
    if (localRect.right > 0) {
      bool = true;
    }
    return bool;
  }
  
  public void initWidgetPkgList()
  {
    if (this.mAppWidgetManager == null) {
      return;
    }
    BackgroundThread.getHandler().post(new Runnable()
    {
      public void run()
      {
        try
        {
          SystemServicesProxy.-set0(SystemServicesProxy.this, SystemServicesProxy.this.mAppWidgetManager.getAppWidgetPackageList());
          if (Utils.DEBUG_ONEPLUS) {
            Log.d("SystemServicesProxy", "initWigetPkgList");
          }
          return;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
      }
    });
  }
  
  public boolean isBlackListedActivity(String paramString)
  {
    return sRecentsBlacklist.contains(paramString);
  }
  
  public boolean isDeepCleaning()
  {
    return mDeepCleaning;
  }
  
  public boolean isInSafeMode()
  {
    return this.mIsSafeMode;
  }
  
  public boolean isRecentsActivityVisible()
  {
    return isRecentsActivityVisible(null);
  }
  
  public boolean isRecentsActivityVisible(MutableBoolean paramMutableBoolean)
  {
    boolean bool3 = true;
    if (this.mIam == null) {
      return false;
    }
    for (;;)
    {
      boolean bool4;
      boolean bool2;
      boolean bool1;
      try
      {
        ActivityManager.StackInfo localStackInfo1 = this.mIam.getStackInfo(0);
        ActivityManager.StackInfo localStackInfo2 = this.mIam.getStackInfo(1);
        ComponentName localComponentName = localStackInfo1.topActivity;
        bool4 = localStackInfo1.visible;
        bool2 = bool4;
        if (localStackInfo2 != null)
        {
          if (!localStackInfo2.visible) {
            continue;
          }
          if (localStackInfo2.position > localStackInfo1.position)
          {
            bool1 = true;
            break label171;
          }
        }
        else
        {
          if (paramMutableBoolean != null) {
            paramMutableBoolean.value = bool2;
          }
          if ((!bool2) || (localComponentName == null) || (!localComponentName.getPackageName().equals("com.android.systemui"))) {
            continue;
          }
          bool2 = bool3;
          if (!localComponentName.getClassName().equals("com.android.systemui.recents.RecentsActivity")) {
            bool2 = localComponentName.getClassName().equals("com.android.systemui.recents.tv.RecentsTvActivity");
          }
          return bool2;
        }
        bool1 = false;
        break label171;
        bool1 = false;
        break label171;
        bool1 = true;
        break label177;
        return false;
      }
      catch (RemoteException paramMutableBoolean)
      {
        paramMutableBoolean.printStackTrace();
        return false;
      }
      label171:
      if (bool1)
      {
        bool1 = false;
        label177:
        bool2 = bool4 & bool1;
      }
    }
  }
  
  public boolean isScreenPinningActive()
  {
    if (this.mIam == null) {
      return false;
    }
    try
    {
      boolean bool = this.mIam.isInLockTaskMode();
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public boolean isSystemUser(int paramInt)
  {
    boolean bool = false;
    if (paramInt == 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isTouchExplorationEnabled()
  {
    boolean bool = false;
    if (this.mAccm == null) {
      return false;
    }
    if (this.mAccm.isEnabled()) {
      bool = this.mAccm.isTouchExplorationEnabled();
    }
    return bool;
  }
  
  public void killAllRunningProcess()
  {
    final LockStateController localLockStateController = LockStateController.getInstance(this.mContext);
    final List localList = this.mAm.getRunningAppProcesses();
    BackgroundThread.getHandler().post(new Runnable()
    {
      public void run()
      {
        SystemServicesProxy.-wrap2(SystemServicesProxy.this);
        int k = SystemServicesProxy.this.getCurrentUser();
        int i;
        label32:
        int m;
        int j;
        label78:
        String str1;
        if (k == 0)
        {
          i = 1;
          Iterator localIterator = localList.iterator();
          if (!localIterator.hasNext()) {
            return;
          }
          Object localObject = (ActivityManager.RunningAppProcessInfo)localIterator.next();
          m = UserHandle.getUserId(((ActivityManager.RunningAppProcessInfo)localObject).uid);
          localObject = ((ActivityManager.RunningAppProcessInfo)localObject).pkgList;
          int n = localObject.length;
          j = 0;
          if (j < n)
          {
            str1 = localObject[j];
            if (((k == m) || ((i != 0) && (m == 999))) && (SystemServicesProxy.-wrap0(SystemServicesProxy.this, str1))) {
              break label132;
            }
          }
        }
        for (;;)
        {
          j += 1;
          break label78;
          break label32;
          i = 0;
          break;
          label132:
          String str2 = str1 + "#" + m;
          if (localLockStateController.isLocked(str2)) {
            Log.d("SystemServicesProxy", "Package is locked:" + str2);
          } else {
            SystemServicesProxy.-wrap1(SystemServicesProxy.this, str1, m);
          }
        }
      }
    });
  }
  
  public boolean moveTaskToDockedStack(int paramInt1, int paramInt2, Rect paramRect)
  {
    if (this.mIam == null) {
      return false;
    }
    try
    {
      boolean bool = this.mIam.moveTaskToDockedStack(paramInt1, paramInt2, true, false, paramRect, true);
      return bool;
    }
    catch (RemoteException paramRect)
    {
      paramRect.printStackTrace();
    }
    return false;
  }
  
  public void moveTaskToStack(int paramInt1, int paramInt2)
  {
    if (this.mIam == null) {
      return;
    }
    try
    {
      this.mIam.positionTaskInStack(paramInt1, paramInt2, 0);
      return;
    }
    catch (RemoteException|IllegalArgumentException localRemoteException)
    {
      localRemoteException.printStackTrace();
    }
  }
  
  public void overridePendingAppTransitionMultiThumbFuture(IAppTransitionAnimationSpecsFuture paramIAppTransitionAnimationSpecsFuture, IRemoteCallback paramIRemoteCallback, boolean paramBoolean)
  {
    try
    {
      WindowManagerGlobal.getWindowManagerService().overridePendingAppTransitionMultiThumbFuture(paramIAppTransitionAnimationSpecsFuture, paramIRemoteCallback, paramBoolean);
      return;
    }
    catch (RemoteException paramIAppTransitionAnimationSpecsFuture)
    {
      Log.w("SystemServicesProxy", "Failed to override transition: " + paramIAppTransitionAnimationSpecsFuture);
    }
  }
  
  public void registerDockedStackListener(IDockedStackListener paramIDockedStackListener)
  {
    if (this.mWm == null) {
      return;
    }
    try
    {
      WindowManagerGlobal.getWindowManagerService().registerDockedStackListener(paramIDockedStackListener);
      return;
    }
    catch (Exception paramIDockedStackListener)
    {
      paramIDockedStackListener.printStackTrace();
    }
  }
  
  public void registerTaskStackListener(TaskStackListener paramTaskStackListener)
  {
    if (this.mIam == null) {
      return;
    }
    this.mTaskStackListeners.add(paramTaskStackListener);
    if (this.mTaskStackListeners.size() == 1) {}
    try
    {
      this.mIam.registerTaskStackListener(this.mTaskStackListener);
      return;
    }
    catch (Exception paramTaskStackListener)
    {
      Log.w("SystemServicesProxy", "Failed to call registerTaskStackListener", paramTaskStackListener);
    }
  }
  
  public void removeTask(final int paramInt1, final String paramString, final int paramInt2)
  {
    if (this.mAm == null) {
      return;
    }
    BackgroundThread.getHandler().post(new Runnable()
    {
      public void run()
      {
        if (Utils.DEBUG_ONEPLUS) {
          Log.d("SystemServicesProxy", "Task is removed: id: " + paramInt1 + ", pkg: " + paramString + ", user:" + paramInt2);
        }
        SystemServicesProxy.this.mAm.removeTask(paramInt1);
        if ((!Utils.isDeepCleanEnable(SystemServicesProxy.this.mContext)) || (SystemServicesProxy.-get0())) {}
        do
        {
          return;
          SystemServicesProxy.-wrap2(SystemServicesProxy.this);
        } while (!SystemServicesProxy.-wrap0(SystemServicesProxy.this, paramString));
        SystemServicesProxy.-wrap1(SystemServicesProxy.this, paramString, paramInt2);
      }
    });
  }
  
  public void requestKeyboardShortcuts(Context paramContext, WindowManager.KeyboardShortcutsReceiver paramKeyboardShortcutsReceiver, int paramInt)
  {
    this.mWm.requestAppKeyboardShortcuts(paramKeyboardShortcutsReceiver, paramInt);
  }
  
  public void sendCloseSystemWindows(String paramString)
  {
    if (ActivityManagerNative.isSystemReady()) {}
    try
    {
      this.mIam.closeSystemDialogs(paramString);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void setDeepCleaning(boolean paramBoolean)
  {
    mDeepCleaning = paramBoolean;
  }
  
  public void setRecentsVisibility(boolean paramBoolean)
  {
    try
    {
      this.mIwm.setRecentsVisibility(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("SystemServicesProxy", "Unable to reach window manager", localRemoteException);
    }
  }
  
  public void setTvPipVisibility(boolean paramBoolean)
  {
    try
    {
      this.mIwm.setTvPipVisibility(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("SystemServicesProxy", "Unable to reach window manager", localRemoteException);
    }
  }
  
  public boolean startActivityFromRecents(Context paramContext, Task.TaskKey paramTaskKey, String paramString, ActivityOptions paramActivityOptions)
  {
    Object localObject = null;
    if (this.mIam != null)
    {
      ActivityOptions localActivityOptions = paramActivityOptions;
      try
      {
        if (paramTaskKey.stackId == 3)
        {
          localActivityOptions = paramActivityOptions;
          if (paramActivityOptions == null) {
            localActivityOptions = ActivityOptions.makeBasic();
          }
          localActivityOptions.setLaunchStackId(1);
        }
        Log.d("SystemServicesProxy", "start " + paramTaskKey.id + ", " + paramTaskKey.getComponent().toString());
        paramActivityOptions = this.mIam;
        int i = paramTaskKey.id;
        if (localActivityOptions == null) {}
        for (paramTaskKey = (Task.TaskKey)localObject;; paramTaskKey = localActivityOptions.toBundle())
        {
          paramActivityOptions.startActivityFromRecents(i, paramTaskKey);
          return true;
        }
        return false;
      }
      catch (Exception paramTaskKey)
      {
        Log.e("SystemServicesProxy", paramContext.getString(2131690329, new Object[] { paramString }), paramTaskKey);
      }
    }
  }
  
  public void startInPlaceAnimationOnFrontMostApplication(ActivityOptions paramActivityOptions)
  {
    if (this.mIam == null) {
      return;
    }
    try
    {
      this.mIam.startInPlaceAnimationOnFrontMostApplication(paramActivityOptions);
      return;
    }
    catch (Exception paramActivityOptions)
    {
      paramActivityOptions.printStackTrace();
    }
  }
  
  public boolean startTaskInDockedMode(int paramInt1, int paramInt2)
  {
    if (this.mIam == null) {
      return false;
    }
    try
    {
      ActivityOptions localActivityOptions = ActivityOptions.makeBasic();
      localActivityOptions.setDockCreateMode(paramInt2);
      localActivityOptions.setLaunchStackId(3);
      this.mIam.startActivityFromRecents(paramInt1, localActivityOptions.toBundle());
      return true;
    }
    catch (Exception localException)
    {
      Log.e("SystemServicesProxy", "Failed to dock task: " + paramInt1 + " with createMode: " + paramInt2, localException);
    }
    return false;
  }
  
  public void writeLockedListToProvider(final String paramString)
  {
    this.mBgThreadHandler.post(new Runnable()
    {
      public void run()
      {
        Settings.System.putStringForUser(SystemServicesProxy.this.mContext.getContentResolver(), "com_oneplus_systemui_recent_task_lockd_list", paramString, SystemServicesProxy.this.getCurrentUser());
      }
    });
  }
  
  private final class H
    extends Handler
  {
    private H() {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      }
      for (;;)
      {
        return;
        int i = SystemServicesProxy.-get2(SystemServicesProxy.this).size() - 1;
        while (i >= 0)
        {
          ((SystemServicesProxy.TaskStackListener)SystemServicesProxy.-get2(SystemServicesProxy.this).get(i)).onTaskStackChanged();
          i -= 1;
        }
        continue;
        i = SystemServicesProxy.-get2(SystemServicesProxy.this).size() - 1;
        while (i >= 0)
        {
          ((SystemServicesProxy.TaskStackListener)SystemServicesProxy.-get2(SystemServicesProxy.this).get(i)).onActivityPinned();
          i -= 1;
        }
        continue;
        i = SystemServicesProxy.-get2(SystemServicesProxy.this).size() - 1;
        while (i >= 0)
        {
          ((SystemServicesProxy.TaskStackListener)SystemServicesProxy.-get2(SystemServicesProxy.this).get(i)).onPinnedActivityRestartAttempt();
          i -= 1;
        }
        continue;
        i = SystemServicesProxy.-get2(SystemServicesProxy.this).size() - 1;
        while (i >= 0)
        {
          ((SystemServicesProxy.TaskStackListener)SystemServicesProxy.-get2(SystemServicesProxy.this).get(i)).onPinnedStackAnimationEnded();
          i -= 1;
        }
        continue;
        i = SystemServicesProxy.-get2(SystemServicesProxy.this).size() - 1;
        while (i >= 0)
        {
          ((SystemServicesProxy.TaskStackListener)SystemServicesProxy.-get2(SystemServicesProxy.this).get(i)).onActivityForcedResizable((String)paramMessage.obj, paramMessage.arg1);
          i -= 1;
        }
        continue;
        i = SystemServicesProxy.-get2(SystemServicesProxy.this).size() - 1;
        while (i >= 0)
        {
          ((SystemServicesProxy.TaskStackListener)SystemServicesProxy.-get2(SystemServicesProxy.this).get(i)).onActivityDismissingDockedStack();
          i -= 1;
        }
      }
    }
  }
  
  public static abstract class TaskStackListener
  {
    public void onActivityDismissingDockedStack() {}
    
    public void onActivityForcedResizable(String paramString, int paramInt) {}
    
    public void onActivityPinned() {}
    
    public void onPinnedActivityRestartAttempt() {}
    
    public void onPinnedStackAnimationEnded() {}
    
    public void onTaskStackChanged() {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\misc\SystemServicesProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */