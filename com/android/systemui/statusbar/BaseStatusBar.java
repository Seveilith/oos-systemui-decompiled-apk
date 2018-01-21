package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManagerNative;
import android.app.ActivityOptions;
import android.app.IActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.Notification.Action;
import android.app.Notification.Builder;
import android.app.Notification.WearableExtender;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.RemoteInput;
import android.app.TaskStackBuilder;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.service.dreams.IDreamManager;
import android.service.dreams.IDreamManager.Stub;
import android.service.notification.NotificationListenerService;
import android.service.notification.NotificationListenerService.RankingMap;
import android.service.notification.StatusBarNotification;
import android.service.vr.IVrManager;
import android.service.vr.IVrManager.Stub;
import android.service.vr.IVrStateCallbacks;
import android.service.vr.IVrStateCallbacks.Stub;
import android.text.Layout;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.IWindowManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RemoteViews;
import android.widget.RemoteViews.OnClickHandler;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.IStatusBarService.Stub;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardHostView.OnDismissAction;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.DejankUtils;
import com.android.systemui.Interpolators;
import com.android.systemui.RecentsComponent;
import com.android.systemui.SwipeHelper.LongPressListener;
import com.android.systemui.SystemUI;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.recents.Recents;
import com.android.systemui.statusbar.phone.NavigationBarView;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.PreviewInflater;
import com.android.systemui.statusbar.policy.RemoteInputView;
import com.android.systemui.statusbar.stack.NotificationStackScrollLayout;
import com.android.systemui.util.MdmLogger;
import com.android.systemui.util.Utils;
import com.android.systemui.volume.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public abstract class BaseStatusBar
  extends SystemUI
  implements CommandQueue.Callbacks, ActivatableNotificationView.OnActivatedListener, ExpandableNotificationRow.ExpansionLogger, NotificationData.Environment, ExpandableNotificationRow.OnExpandClickListener, NotificationGuts.OnGutsClosedListener
{
  protected static final boolean DEBUG_ONEPLUS = Build.DEBUG_ONEPLUS;
  public static final boolean ENABLE_CHILD_NOTIFICATIONS;
  private static boolean ENABLE_LOCK_SCREEN_ALLOW_REMOTE_INPUT;
  public static final boolean ENABLE_REMOTE_INPUT = SystemProperties.getBoolean("debug.enable_remote_input", true);
  public static final boolean FORCE_REMOTE_INPUT_HISTORY;
  protected AccessibilityManager mAccessibilityManager;
  private final BroadcastReceiver mAllUsersReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(paramAnonymousIntent.getAction())) && (BaseStatusBar.this.isCurrentProfile(getSendingUserId())))
      {
        BaseStatusBar.-get5(BaseStatusBar.this).clear();
        BaseStatusBar.-wrap7(BaseStatusBar.this);
        BaseStatusBar.this.mIsSecure = BaseStatusBar.this.isKeyguardSecure();
        BaseStatusBar.this.updateNotifications();
      }
    }
  };
  protected boolean mAllowLockscreenRemoteInput;
  protected AssistManager mAssistManager;
  protected IStatusBarService mBarService;
  protected boolean mBouncerShowing;
  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if ("android.intent.action.USER_SWITCHED".equals(paramAnonymousContext))
      {
        BaseStatusBar.this.mCurrentUserId = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", -1);
        BaseStatusBar.-wrap5(BaseStatusBar.this);
        Log.v("StatusBar", "userId " + BaseStatusBar.this.mCurrentUserId + " is in the house");
        BaseStatusBar.-wrap7(BaseStatusBar.this);
        BaseStatusBar.this.userSwitched(BaseStatusBar.this.mCurrentUserId);
        BaseStatusBar.this.mUserSwitched = true;
      }
      do
      {
        do
        {
          do
          {
            do
            {
              return;
              if ("android.intent.action.USER_ADDED".equals(paramAnonymousContext))
              {
                BaseStatusBar.-wrap5(BaseStatusBar.this);
                return;
              }
              if (!"android.intent.action.USER_PRESENT".equals(paramAnonymousContext)) {
                break;
              }
              paramAnonymousContext = null;
              try
              {
                paramAnonymousIntent = ActivityManagerNative.getDefault().getRecentTasks(1, 5, BaseStatusBar.this.mCurrentUserId).getList();
                paramAnonymousContext = paramAnonymousIntent;
              }
              catch (RemoteException paramAnonymousIntent)
              {
                for (;;) {}
              }
            } while ((paramAnonymousContext == null) || (paramAnonymousContext.size() <= 0));
            paramAnonymousContext = BaseStatusBar.-get3(BaseStatusBar.this).getUserInfo(((ActivityManager.RecentTaskInfo)paramAnonymousContext.get(0)).userId);
          } while ((paramAnonymousContext == null) || (!paramAnonymousContext.isManagedProfile()) || (paramAnonymousContext.id == 999));
          paramAnonymousContext = Toast.makeText(BaseStatusBar.this.mContext, 2131690443, 0);
          paramAnonymousIntent = (TextView)paramAnonymousContext.getView().findViewById(16908299);
          paramAnonymousIntent.setCompoundDrawablesRelativeWithIntrinsicBounds(2130838469, 0, 0, 0);
          paramAnonymousIntent.setCompoundDrawablePadding(BaseStatusBar.this.mContext.getResources().getDimensionPixelSize(2131755570));
          paramAnonymousContext.show();
          return;
          if ((!"com.android.systemui.statusbar.banner_action_cancel".equals(paramAnonymousContext)) && (!"com.android.systemui.statusbar.banner_action_setup".equals(paramAnonymousContext))) {
            break;
          }
          ((NotificationManager)BaseStatusBar.this.mContext.getSystemService("notification")).cancel(2131951670);
          Settings.Secure.putInt(BaseStatusBar.this.mContext.getContentResolver(), "show_note_about_notification_hiding", 0);
        } while (!"com.android.systemui.statusbar.banner_action_setup".equals(paramAnonymousContext));
        BaseStatusBar.this.animateCollapsePanels(2, true);
        BaseStatusBar.this.mContext.startActivity(new Intent("android.settings.ACTION_APP_NOTIFICATION_REDACTION").addFlags(268435456));
        return;
      } while (!"com.android.systemui.statusbar.work_challenge_unlocked_notification_action".equals(paramAnonymousContext));
      paramAnonymousContext = (IntentSender)paramAnonymousIntent.getParcelableExtra("android.intent.extra.INTENT");
      paramAnonymousIntent = paramAnonymousIntent.getStringExtra("android.intent.extra.INDEX");
      if (paramAnonymousContext != null) {}
      try
      {
        BaseStatusBar.this.mContext.startIntentSender(paramAnonymousContext, null, 0, 0, 0);
        if (paramAnonymousIntent != null) {}
        try
        {
          BaseStatusBar.this.mBarService.onNotificationClick(paramAnonymousIntent);
          BaseStatusBar.this.onWorkChallengeUnlocked();
          return;
        }
        catch (RemoteException paramAnonymousContext)
        {
          for (;;) {}
        }
      }
      catch (IntentSender.SendIntentException paramAnonymousContext)
      {
        for (;;) {}
      }
    }
  };
  protected CommandQueue mCommandQueue;
  protected final SparseArray<UserInfo> mCurrentProfiles = new SparseArray();
  protected int mCurrentUserId = 0;
  private int mDensity;
  protected boolean mDeviceInteractive;
  protected DevicePolicyManager mDevicePolicyManager;
  private boolean mDeviceProvisioned = false;
  protected boolean mDisableNotificationAlerts = false;
  protected DismissView mDismissView;
  protected Display mDisplay;
  protected IDreamManager mDreamManager;
  protected EmptyShadeView mEmptyShadeView;
  private float mFontScale;
  private boolean mGameModeBlockNotification = false;
  private final ContentObserver mGameModeObserver = new ContentObserver(this.mHandler)
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      BaseStatusBar.-wrap6(BaseStatusBar.this);
    }
  };
  private boolean mGameModeStatus = false;
  protected NotificationGroupManager mGroupManager = new NotificationGroupManager();
  protected H mHandler = createHandler();
  protected ArraySet<NotificationData.Entry> mHeadsUpEntriesToRemoveOnSwitch = new ArraySet();
  protected HeadsUpManager mHeadsUpManager;
  protected boolean mHeadsUpTicker = false;
  private int mIMEPickerID = 0;
  protected boolean mIsSecure = false;
  protected NotificationOverflowContainer mKeyguardIconOverflowContainer;
  private KeyguardManager mKeyguardManager;
  protected ArraySet<String> mKeysKeptForRemoteInput = new ArraySet();
  protected int mLayoutDirection = -1;
  private Locale mLocale;
  private LockPatternUtils mLockPatternUtils;
  private boolean mLockscreenPublicMode = false;
  private final ContentObserver mLockscreenSettingsObserver = new ContentObserver(this.mHandler)
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      BaseStatusBar.-get5(BaseStatusBar.this).clear();
      BaseStatusBar.-get4(BaseStatusBar.this).clear();
      BaseStatusBar.this.updateNotifications();
    }
  };
  protected NavigationBarView mNavigationBarView = null;
  private Set<String> mNonBlockablePkgs;
  private NotificationClicker mNotificationClicker = new NotificationClicker(null);
  protected NotificationData mNotificationData;
  private NotificationGuts mNotificationGutsExposed;
  private final NotificationListenerService mNotificationListener = new NotificationListenerService()
  {
    public void onListenerConnected()
    {
      final StatusBarNotification[] arrayOfStatusBarNotification = getActiveNotifications();
      if (arrayOfStatusBarNotification == null) {
        return;
      }
      final NotificationListenerService.RankingMap localRankingMap = getCurrentRanking();
      BaseStatusBar.this.mHandler.post(new Runnable()
      {
        public void run()
        {
          StatusBarNotification[] arrayOfStatusBarNotification = arrayOfStatusBarNotification;
          int i = 0;
          int j = arrayOfStatusBarNotification.length;
          while (i < j)
          {
            StatusBarNotification localStatusBarNotification = arrayOfStatusBarNotification[i];
            BaseStatusBar.this.addNotification(localStatusBarNotification, localRankingMap, null);
            i += 1;
          }
        }
      });
    }
    
    public void onNotificationPosted(final StatusBarNotification paramAnonymousStatusBarNotification, final NotificationListenerService.RankingMap paramAnonymousRankingMap)
    {
      if (paramAnonymousStatusBarNotification != null) {
        BaseStatusBar.this.mHandler.post(new Runnable()
        {
          public void run()
          {
            BaseStatusBar.-wrap2(BaseStatusBar.this, paramAnonymousStatusBarNotification.getNotification());
            String str = paramAnonymousStatusBarNotification.getKey();
            BaseStatusBar.this.mKeysKeptForRemoteInput.remove(str);
            int i;
            if (BaseStatusBar.this.mNotificationData.get(str) != null) {
              i = 1;
            }
            while ((!BaseStatusBar.ENABLE_CHILD_NOTIFICATIONS) && (BaseStatusBar.this.mGroupManager.isChildInGroupWithSummary(paramAnonymousStatusBarNotification))) {
              if (i != 0)
              {
                BaseStatusBar.this.removeNotification(str, paramAnonymousRankingMap);
                return;
                i = 0;
              }
              else
              {
                BaseStatusBar.this.mNotificationData.updateRanking(paramAnonymousRankingMap);
                return;
              }
            }
            if (i != 0)
            {
              BaseStatusBar.this.updateNotification(paramAnonymousStatusBarNotification, paramAnonymousRankingMap);
              return;
            }
            BaseStatusBar.this.addNotification(paramAnonymousStatusBarNotification, paramAnonymousRankingMap, null);
          }
        });
      }
    }
    
    public void onNotificationRankingUpdate(final NotificationListenerService.RankingMap paramAnonymousRankingMap)
    {
      if (paramAnonymousRankingMap != null) {
        BaseStatusBar.this.mHandler.post(new Runnable()
        {
          public void run()
          {
            BaseStatusBar.this.updateNotificationRanking(paramAnonymousRankingMap);
          }
        });
      }
    }
    
    public void onNotificationRemoved(final StatusBarNotification paramAnonymousStatusBarNotification, final NotificationListenerService.RankingMap paramAnonymousRankingMap)
    {
      if (paramAnonymousStatusBarNotification != null)
      {
        paramAnonymousStatusBarNotification = paramAnonymousStatusBarNotification.getKey();
        BaseStatusBar.this.mHandler.post(new Runnable()
        {
          public void run()
          {
            BaseStatusBar.this.removeNotification(paramAnonymousStatusBarNotification, paramAnonymousRankingMap);
          }
        });
      }
    }
  };
  private RemoteViews.OnClickHandler mOnClickHandler = new RemoteViews.OnClickHandler()
  {
    private String getNotificationKeyForParent(ViewParent paramAnonymousViewParent)
    {
      while (paramAnonymousViewParent != null)
      {
        if ((paramAnonymousViewParent instanceof ExpandableNotificationRow)) {
          return ((ExpandableNotificationRow)paramAnonymousViewParent).getStatusBarNotification().getKey();
        }
        paramAnonymousViewParent = paramAnonymousViewParent.getParent();
      }
      return null;
    }
    
    private boolean handleRemoteInput(View paramAnonymousView, PendingIntent paramAnonymousPendingIntent, Intent paramAnonymousIntent)
    {
      paramAnonymousIntent = paramAnonymousView.getTag(16908380);
      RemoteInput[] arrayOfRemoteInput = null;
      if ((paramAnonymousIntent instanceof RemoteInput[])) {
        arrayOfRemoteInput = (RemoteInput[])paramAnonymousIntent;
      }
      if (arrayOfRemoteInput == null) {
        return false;
      }
      Intent localIntent = null;
      int i = 0;
      int j = arrayOfRemoteInput.length;
      while (i < j)
      {
        paramAnonymousIntent = arrayOfRemoteInput[i];
        if (paramAnonymousIntent.getAllowFreeFormInput()) {
          localIntent = paramAnonymousIntent;
        }
        i += 1;
      }
      if (localIntent == null) {
        return false;
      }
      paramAnonymousIntent = paramAnonymousView.getParent();
      Object localObject2 = null;
      Object localObject1 = localObject2;
      Object localObject3;
      if (paramAnonymousIntent != null)
      {
        if ((paramAnonymousIntent instanceof View))
        {
          localObject1 = (View)paramAnonymousIntent;
          if (((View)localObject1).isRootNamespace()) {
            localObject1 = (RemoteInputView)((View)localObject1).findViewWithTag(RemoteInputView.VIEW_TAG);
          }
        }
      }
      else {
        localObject3 = null;
      }
      for (;;)
      {
        localObject2 = localObject3;
        if (paramAnonymousIntent != null)
        {
          if ((paramAnonymousIntent instanceof ExpandableNotificationRow)) {
            localObject2 = (ExpandableNotificationRow)paramAnonymousIntent;
          }
        }
        else
        {
          if ((localObject1 != null) && (localObject2 != null)) {
            break label186;
          }
          return false;
          paramAnonymousIntent = paramAnonymousIntent.getParent();
          break;
        }
        paramAnonymousIntent = paramAnonymousIntent.getParent();
      }
      label186:
      ((ExpandableNotificationRow)localObject2).setUserExpanded(true);
      if (!BaseStatusBar.this.mAllowLockscreenRemoteInput)
      {
        if (BaseStatusBar.this.isLockscreenPublicMode())
        {
          BaseStatusBar.this.onLockedRemoteInput((ExpandableNotificationRow)localObject2, paramAnonymousView);
          return true;
        }
        i = paramAnonymousPendingIntent.getCreatorUserHandle().getIdentifier();
        if ((BaseStatusBar.-get3(BaseStatusBar.this).getUserInfo(i).isManagedProfile()) && (BaseStatusBar.-get1(BaseStatusBar.this).isDeviceLocked(i)))
        {
          BaseStatusBar.this.onLockedWorkRemoteInput(i, (ExpandableNotificationRow)localObject2, paramAnonymousView);
          return true;
        }
      }
      j = paramAnonymousView.getWidth();
      i = j;
      if ((paramAnonymousView instanceof TextView))
      {
        paramAnonymousIntent = (TextView)paramAnonymousView;
        i = j;
        if (paramAnonymousIntent.getLayout() != null) {
          i = Math.min(j, (int)paramAnonymousIntent.getLayout().getLineWidth(0) + (paramAnonymousIntent.getCompoundPaddingLeft() + paramAnonymousIntent.getCompoundPaddingRight()));
        }
      }
      i = paramAnonymousView.getLeft() + i / 2;
      j = paramAnonymousView.getTop() + paramAnonymousView.getHeight() / 2;
      int k = ((RemoteInputView)localObject1).getWidth();
      int m = ((RemoteInputView)localObject1).getHeight();
      ((RemoteInputView)localObject1).setRevealParameters(i, j, Math.max(Math.max(i + j, m - j + i), Math.max(k - i + j, k - i + (m - j))));
      ((RemoteInputView)localObject1).setPendingIntent(paramAnonymousPendingIntent);
      ((RemoteInputView)localObject1).setRemoteInput(arrayOfRemoteInput, localIntent);
      ((RemoteInputView)localObject1).focusAnimated();
      return true;
    }
    
    private void logActionClick(View paramAnonymousView)
    {
      ViewParent localViewParent = paramAnonymousView.getParent();
      String str = getNotificationKeyForParent(localViewParent);
      if (str == null)
      {
        Log.w("StatusBar", "Couldn't determine notification for click.");
        return;
      }
      int j = -1;
      int i = j;
      if (paramAnonymousView.getId() == 16909224)
      {
        i = j;
        if (localViewParent != null)
        {
          i = j;
          if ((localViewParent instanceof ViewGroup)) {
            i = ((ViewGroup)localViewParent).indexOfChild(paramAnonymousView);
          }
        }
      }
      Log.d("StatusBar", "Clicked on button " + i + " for " + str);
      try
      {
        BaseStatusBar.this.mBarService.onNotificationActionClick(str, i);
        return;
      }
      catch (RemoteException paramAnonymousView) {}
    }
    
    private boolean superOnClickHandler(View paramAnonymousView, PendingIntent paramAnonymousPendingIntent, Intent paramAnonymousIntent)
    {
      return super.onClickHandler(paramAnonymousView, paramAnonymousPendingIntent, paramAnonymousIntent, 1);
    }
    
    public boolean onClickHandler(final View paramAnonymousView, final PendingIntent paramAnonymousPendingIntent, final Intent paramAnonymousIntent)
    {
      if (handleRemoteInput(paramAnonymousView, paramAnonymousPendingIntent, paramAnonymousIntent)) {
        return true;
      }
      logActionClick(paramAnonymousView);
      try
      {
        ActivityManagerNative.getDefault().resumeAppSwitches();
        if (paramAnonymousPendingIntent.isActivity())
        {
          final boolean bool1 = BaseStatusBar.this.mStatusBarKeyguardViewManager.isShowing();
          final boolean bool2 = PreviewInflater.wouldLaunchResolverActivity(BaseStatusBar.this.mContext, paramAnonymousPendingIntent.getIntent(), BaseStatusBar.this.mCurrentUserId);
          BaseStatusBar.this.dismissKeyguardThenExecute(new KeyguardHostView.OnDismissAction()
          {
            public boolean onDismiss()
            {
              boolean bool2;
              BaseStatusBar localBaseStatusBar;
              if ((!bool1) || (bool2))
              {
                bool2 = BaseStatusBar.5.-wrap0(BaseStatusBar.5.this, paramAnonymousView, paramAnonymousPendingIntent, paramAnonymousIntent);
                localBaseStatusBar = BaseStatusBar.this;
                if ((bool1) && (!bool2)) {
                  break label128;
                }
              }
              label128:
              for (boolean bool1 = false;; bool1 = true)
              {
                localBaseStatusBar.overrideActivityPendingAppTransition(bool1);
                if (bool2)
                {
                  BaseStatusBar.this.animateCollapsePanels(2, true);
                  BaseStatusBar.this.visibilityChanged(false);
                  BaseStatusBar.this.mAssistManager.hideAssist();
                }
                return bool2;
                try
                {
                  ActivityManagerNative.getDefault().keyguardWaitingForActivityDrawn();
                  ActivityManagerNative.getDefault().resumeAppSwitches();
                }
                catch (RemoteException localRemoteException) {}
                break;
              }
            }
          }, bool2);
          return true;
        }
        return superOnClickHandler(paramAnonymousView, paramAnonymousPendingIntent, paramAnonymousIntent);
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
  };
  protected PowerManager mPowerManager;
  protected RecentsComponent mRecents;
  protected View.OnTouchListener mRecentsPreloadOnTouchListener = new View.OnTouchListener()
  {
    public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
    {
      int i = paramAnonymousMotionEvent.getAction() & 0xFF;
      if (i == 0) {
        BaseStatusBar.this.preloadRecents();
      }
      do
      {
        return false;
        if (i == 3)
        {
          BaseStatusBar.this.cancelPreloadingRecents();
          return false;
        }
      } while ((i != 1) || (paramAnonymousView.isPressed()));
      BaseStatusBar.this.cancelPreloadingRecents();
      return false;
    }
  };
  protected RemoteInputController mRemoteInputController;
  protected ArraySet<NotificationData.Entry> mRemoteInputEntriesToRemoveOnCollapse = new ArraySet();
  protected final ContentObserver mSettingsObserver = new ContentObserver(this.mHandler)
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      if (Settings.Global.getInt(BaseStatusBar.this.mContext.getContentResolver(), "device_provisioned", 0) != 0) {}
      for (paramAnonymousBoolean = true;; paramAnonymousBoolean = false)
      {
        if (paramAnonymousBoolean != BaseStatusBar.-get0(BaseStatusBar.this))
        {
          BaseStatusBar.-set0(BaseStatusBar.this, paramAnonymousBoolean);
          BaseStatusBar.this.updateNotifications();
        }
        int i = Util.getCorrectZenMode(Settings.Global.getInt(BaseStatusBar.this.mContext.getContentResolver(), "zen_mode", 0), Util.getThreeKeyStatus(BaseStatusBar.this.mContext), Settings.System.getIntForUser(BaseStatusBar.this.mContext.getContentResolver(), "oem_vibrate_under_silent", 0, KeyguardUpdateMonitor.getCurrentUser()));
        BaseStatusBar.this.setZenMode(i);
        BaseStatusBar.-get4(BaseStatusBar.this).clear();
        BaseStatusBar.-wrap7(BaseStatusBar.this);
        return;
      }
    }
  };
  protected boolean mShowLockscreenNotifications;
  protected NotificationStackScrollLayout mStackScroller;
  protected int mState;
  protected StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
  protected boolean mUseHeadsUp = false;
  private UserManager mUserManager;
  protected boolean mUserSwitched = false;
  private final SparseBooleanArray mUsersAllowingNotifications = new SparseBooleanArray();
  private final SparseBooleanArray mUsersAllowingPrivateNotifications = new SparseBooleanArray();
  protected boolean mVisible;
  private boolean mVisibleToUser;
  protected boolean mVrMode;
  private final IVrStateCallbacks mVrStateCallbacks = new IVrStateCallbacks.Stub()
  {
    public void onVrStateChanged(boolean paramAnonymousBoolean)
    {
      BaseStatusBar.this.mVrMode = paramAnonymousBoolean;
    }
  };
  protected WindowManager mWindowManager;
  protected IWindowManager mWindowManagerService;
  protected int mZenMode;
  
  static
  {
    ENABLE_CHILD_NOTIFICATIONS = SystemProperties.getBoolean("debug.child_notifs", true);
    FORCE_REMOTE_INPUT_HISTORY = SystemProperties.getBoolean("debug.force_remoteinput_history", false);
    ENABLE_LOCK_SCREEN_ALLOW_REMOTE_INPUT = false;
  }
  
  private boolean adminAllowsUnredactedNotifications(int paramInt)
  {
    if (paramInt == -1) {
      return true;
    }
    return (this.mDevicePolicyManager.getKeyguardDisabledFeatures(null, paramInt) & 0x8) == 0;
  }
  
  private boolean alertAgain(NotificationData.Entry paramEntry, Notification paramNotification)
  {
    return (paramEntry == null) || (!paramEntry.hasInterrupted()) || ((paramNotification.flags & 0x8) == 0);
  }
  
  private void bindGuts(final ExpandableNotificationRow paramExpandableNotificationRow)
  {
    paramExpandableNotificationRow.inflateGuts();
    final StatusBarNotification localStatusBarNotification = paramExpandableNotificationRow.getStatusBarNotification();
    localPackageManager = getPackageManagerForUser(this.mContext, localStatusBarNotification.getUser().getIdentifier());
    paramExpandableNotificationRow.setTag(localStatusBarNotification.getPackageName());
    final NotificationGuts localNotificationGuts = paramExpandableNotificationRow.getGuts();
    localNotificationGuts.setClosedListener(this);
    final String str2 = localStatusBarNotification.getPackageName();
    String str1 = str2;
    localDrawable = null;
    j = -1;
    localObject = str1;
    try
    {
      ApplicationInfo localApplicationInfo = localPackageManager.getApplicationInfo(str2, 8704);
      i = j;
      localObject = str1;
      if (localApplicationInfo != null)
      {
        localObject = str1;
        str1 = String.valueOf(localPackageManager.getApplicationLabel(localApplicationInfo));
        localObject = str1;
        localDrawable = localPackageManager.getApplicationIcon(localApplicationInfo);
        localObject = str1;
        i = localApplicationInfo.uid;
        localObject = str1;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        localDrawable = localPackageManager.getDefaultActivityIcon();
        final int i = j;
        continue;
        ((TextView)localObject).setVisibility(8);
      }
    }
    ((ImageView)localNotificationGuts.findViewById(2131952099)).setImageDrawable(localDrawable);
    ((TextView)localNotificationGuts.findViewById(2131952100)).setText((CharSequence)localObject);
    localObject = (TextView)localNotificationGuts.findViewById(2131952110);
    if (i >= 0)
    {
      ((TextView)localObject).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          MetricsLogger.action(BaseStatusBar.this.mContext, 205);
          localNotificationGuts.resetFalsingCheck();
          BaseStatusBar.-wrap4(BaseStatusBar.this, str2, i);
        }
      });
      ((TextView)localObject).setText(2131690521);
      localNotificationGuts.bindImportance(localPackageManager, localStatusBarNotification, this.mNonBlockablePkgs, this.mNotificationData.getImportance(localStatusBarNotification.getKey()));
      localObject = (TextView)localNotificationGuts.findViewById(2131952111);
      ((TextView)localObject).setText(2131690522);
      ((TextView)localObject).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(final View paramAnonymousView)
        {
          if ((localNotificationGuts.hasImportanceChanged()) && (BaseStatusBar.this.isLockscreenPublicMode()) && ((BaseStatusBar.this.mState == 1) || (BaseStatusBar.this.mState == 2)))
          {
            paramAnonymousView = new KeyguardHostView.OnDismissAction()
            {
              public boolean onDismiss()
              {
                BaseStatusBar.-wrap3(BaseStatusBar.this, this.val$sbn, this.val$row, this.val$guts, paramAnonymousView);
                return true;
              }
            };
            BaseStatusBar.this.onLockedNotificationImportanceChange(paramAnonymousView);
            return;
          }
          BaseStatusBar.-wrap3(BaseStatusBar.this, localStatusBarNotification, paramExpandableNotificationRow, localNotificationGuts, paramAnonymousView);
        }
      });
      return;
    }
  }
  
  private boolean containsSpecialNotification()
  {
    boolean bool = false;
    ArrayList localArrayList = this.mNotificationData.getActiveNotifications();
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      if (Utils.bypassClearNotificationEffectPackage(((NotificationData.Entry)localArrayList.get(i)).notification.getPackageName())) {
        bool = true;
      }
      i += 1;
    }
    return bool;
  }
  
  private void dismissPopups(int paramInt1, int paramInt2)
  {
    dismissPopups(paramInt1, paramInt2, true, false);
  }
  
  public static PackageManager getPackageManagerForUser(Context paramContext, int paramInt)
  {
    Context localContext1 = paramContext;
    Context localContext2 = localContext1;
    if (paramInt >= 0) {}
    try
    {
      localContext2 = paramContext.createPackageContextAsUser(paramContext.getPackageName(), 4, new UserHandle(paramInt));
      return localContext2.getPackageManager();
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;)
      {
        localContext2 = localContext1;
      }
    }
  }
  
  private void processForRemoteInput(Notification paramNotification)
  {
    if (!ENABLE_REMOTE_INPUT) {
      return;
    }
    Object localObject1;
    Object localObject2;
    Notification.Action localAction;
    Object localObject3;
    RemoteInput[] arrayOfRemoteInput;
    int m;
    int j;
    if ((paramNotification.extras != null) && (paramNotification.extras.containsKey("android.wearable.EXTENSIONS")) && ((paramNotification.actions == null) || (paramNotification.actions.length == 0)))
    {
      localObject1 = null;
      List localList = new Notification.WearableExtender(paramNotification).getActions();
      int k = localList.size();
      int i = 0;
      localObject2 = localObject1;
      if (i < k)
      {
        localAction = (Notification.Action)localList.get(i);
        if (localAction == null) {
          localObject3 = localObject1;
        }
        do
        {
          i += 1;
          localObject1 = localObject3;
          break;
          arrayOfRemoteInput = localAction.getRemoteInputs();
          localObject3 = localObject1;
        } while (arrayOfRemoteInput == null);
        m = arrayOfRemoteInput.length;
        j = 0;
      }
    }
    for (;;)
    {
      localObject2 = localObject1;
      if (j < m)
      {
        if (arrayOfRemoteInput[j].getAllowFreeFormInput()) {
          localObject2 = localAction;
        }
      }
      else
      {
        localObject3 = localObject2;
        if (localObject2 == null) {
          break;
        }
        if (localObject2 != null)
        {
          paramNotification = Notification.Builder.recoverBuilder(this.mContext, paramNotification);
          paramNotification.setActions(new Notification.Action[] { localObject2 });
          paramNotification.build();
        }
        return;
      }
      j += 1;
    }
  }
  
  private void saveImportanceCloseControls(StatusBarNotification paramStatusBarNotification, ExpandableNotificationRow paramExpandableNotificationRow, NotificationGuts paramNotificationGuts, View paramView)
  {
    paramNotificationGuts.resetFalsingCheck();
    paramNotificationGuts.saveImportance(paramStatusBarNotification);
    paramStatusBarNotification = new int[2];
    paramNotificationGuts = new int[2];
    paramExpandableNotificationRow.getLocationOnScreen(paramStatusBarNotification);
    paramView.getLocationOnScreen(paramNotificationGuts);
    int i = paramView.getWidth() / 2;
    int j = paramView.getHeight() / 2;
    dismissPopups(paramNotificationGuts[0] - paramStatusBarNotification[0] + i, paramNotificationGuts[1] - paramStatusBarNotification[1] + j);
  }
  
  private boolean shouldPeekInGameMode(StatusBarNotification paramStatusBarNotification)
  {
    boolean bool = false;
    paramStatusBarNotification = paramStatusBarNotification.getNotification().extras;
    if (paramStatusBarNotification != null) {
      bool = paramStatusBarNotification.getBoolean("oneplus.shouldPeekInGameMode", false);
    }
    return bool;
  }
  
  private void startAppNotificationSettingsActivity(String paramString, int paramInt)
  {
    Intent localIntent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS");
    localIntent.putExtra("app_package", paramString);
    localIntent.putExtra("app_uid", paramInt);
    startNotificationGutsIntent(localIntent, paramInt);
  }
  
  private void startNotificationGutsIntent(final Intent paramIntent, final int paramInt)
  {
    dismissKeyguardThenExecute(new KeyguardHostView.OnDismissAction()
    {
      public boolean onDismiss()
      {
        AsyncTask.execute(new Runnable()
        {
          public void run()
          {
            try
            {
              if (this.val$keyguardShowing) {
                ActivityManagerNative.getDefault().keyguardWaitingForActivityDrawn();
              }
              TaskStackBuilder.create(BaseStatusBar.this.mContext).addNextIntentWithParentStack(this.val$intent).startActivities(BaseStatusBar.this.getActivityOptions(), new UserHandle(UserHandle.getUserId(this.val$appUid)));
              BaseStatusBar.this.overrideActivityPendingAppTransition(this.val$keyguardShowing);
              return;
            }
            catch (RemoteException localRemoteException) {}
          }
        });
        BaseStatusBar.this.animateCollapsePanels(2, true);
        return true;
      }
    }, false);
  }
  
  private void updateCurrentProfilesCache()
  {
    synchronized (this.mCurrentProfiles)
    {
      this.mCurrentProfiles.clear();
      if (this.mUserManager != null)
      {
        Iterator localIterator = this.mUserManager.getProfiles(this.mCurrentUserId).iterator();
        if (localIterator.hasNext())
        {
          UserInfo localUserInfo = (UserInfo)localIterator.next();
          this.mCurrentProfiles.put(localUserInfo.id, localUserInfo);
        }
      }
    }
  }
  
  private void updateGameModeSetting()
  {
    this.mGameModeStatus = "1".equals(Settings.System.getStringForUser(this.mContext.getContentResolver(), "game_mode_status", -2));
    this.mGameModeBlockNotification = "1".equals(Settings.System.getStringForUser(this.mContext.getContentResolver(), "game_mode_block_notification", -2));
  }
  
  private void updateLockscreenNotificationSetting()
  {
    int i;
    boolean bool;
    if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_show_notifications", 1, this.mCurrentUserId) != 0)
    {
      i = 1;
      int j = this.mDevicePolicyManager.getKeyguardDisabledFeatures(null, this.mCurrentUserId);
      if ((j & 0x4) != 0) {
        break label106;
      }
      bool = true;
      label44:
      if (i == 0) {
        break label111;
      }
      label48:
      setShowLockscreenNotifications(bool);
      if (!ENABLE_LOCK_SCREEN_ALLOW_REMOTE_INPUT) {
        break label131;
      }
      if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_allow_remote_input", 0, this.mCurrentUserId) == 0) {
        break label116;
      }
      i = 1;
      label82:
      if ((j & 0x40) != 0) {
        break label121;
      }
      bool = true;
      label91:
      if (i == 0) {
        break label126;
      }
    }
    for (;;)
    {
      setLockScreenAllowRemoteInput(bool);
      return;
      i = 0;
      break;
      label106:
      bool = false;
      break label44;
      label111:
      bool = false;
      break label48;
      label116:
      i = 0;
      break label82;
      label121:
      bool = false;
      break label91;
      label126:
      bool = false;
    }
    label131:
    setLockScreenAllowRemoteInput(false);
  }
  
  private void updateNotificationViews(NotificationData.Entry paramEntry, StatusBarNotification paramStatusBarNotification)
  {
    Object localObject = paramEntry.cachedContentView;
    RemoteViews localRemoteViews3 = paramEntry.cachedBigContentView;
    RemoteViews localRemoteViews1 = paramEntry.cachedHeadsUpContentView;
    RemoteViews localRemoteViews2 = paramEntry.cachedPublicContentView;
    ((RemoteViews)localObject).reapply(this.mContext, paramEntry.getContentView(), this.mOnClickHandler);
    if ((localRemoteViews3 != null) && (paramEntry.getExpandedContentView() != null)) {
      localRemoteViews3.reapply(paramStatusBarNotification.getPackageContext(this.mContext), paramEntry.getExpandedContentView(), this.mOnClickHandler);
    }
    localObject = paramEntry.getHeadsUpContentView();
    if ((localRemoteViews1 != null) && (localObject != null)) {
      localRemoteViews1.reapply(paramStatusBarNotification.getPackageContext(this.mContext), (View)localObject, this.mOnClickHandler);
    }
    if ((localRemoteViews2 != null) && (paramEntry.getPublicContentView() != null)) {
      localRemoteViews2.reapply(paramStatusBarNotification.getPackageContext(this.mContext), paramEntry.getPublicContentView(), this.mOnClickHandler);
    }
    this.mNotificationClicker.register(paramEntry.row, paramStatusBarNotification);
    paramEntry.row.onNotificationUpdated(paramEntry);
    paramEntry.row.resetHeight();
  }
  
  public abstract void addNotification(StatusBarNotification paramStatusBarNotification, NotificationListenerService.RankingMap paramRankingMap, NotificationData.Entry paramEntry);
  
  protected void addNotificationViews(NotificationData.Entry paramEntry, NotificationListenerService.RankingMap paramRankingMap)
  {
    if (paramEntry == null) {
      return;
    }
    this.mNotificationData.add(paramEntry, paramRankingMap);
    updateNotifications();
  }
  
  public void addPostCollapseAction(Runnable paramRunnable) {}
  
  public void animateCollapsePanels(int paramInt, boolean paramBoolean) {}
  
  public void animateCollapsePanels(int paramInt, boolean paramBoolean1, boolean paramBoolean2) {}
  
  protected void applyColorsAndBackgrounds(StatusBarNotification paramStatusBarNotification, NotificationData.Entry paramEntry)
  {
    boolean bool = true;
    if ((paramEntry.getContentView().getId() != 16909242) && (paramEntry.targetSdk >= 9) && (paramEntry.targetSdk < 21))
    {
      paramEntry.row.setShowingLegacyBackground(true);
      paramEntry.legacy = true;
    }
    if (paramEntry.icon != null)
    {
      paramStatusBarNotification = paramEntry.icon;
      if (paramEntry.targetSdk >= 21) {
        break label79;
      }
    }
    for (;;)
    {
      paramStatusBarNotification.setTag(2131951677, Boolean.valueOf(bool));
      return;
      label79:
      bool = false;
    }
  }
  
  protected void bindDismissListener(final ExpandableNotificationRow paramExpandableNotificationRow)
  {
    paramExpandableNotificationRow.setOnDismissListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramAnonymousView.announceForAccessibility(BaseStatusBar.this.mContext.getString(2131690184));
        BaseStatusBar.this.performRemoveNotification(paramExpandableNotificationRow.getStatusBarNotification(), false);
      }
    });
  }
  
  public void cancelPreloadRecentApps()
  {
    this.mHandler.removeMessages(1023);
    this.mHandler.sendEmptyMessage(1023);
  }
  
  protected void cancelPreloadingRecents()
  {
    if (this.mRecents != null) {
      this.mRecents.cancelPreloadingRecents();
    }
  }
  
  public void clearNotificationEffects()
  {
    if (!containsSpecialNotification()) {}
    try
    {
      this.mBarService.clearNotificationEffects();
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  protected abstract void createAndAddWindows();
  
  protected H createHandler()
  {
    return new H();
  }
  
  public StatusBarIconView createIcon(StatusBarNotification paramStatusBarNotification)
  {
    Object localObject = paramStatusBarNotification.getNotification();
    StatusBarIconView localStatusBarIconView = new StatusBarIconView(this.mContext, paramStatusBarNotification.getPackageName() + "/0x" + Integer.toHexString(paramStatusBarNotification.getId()), (Notification)localObject);
    localStatusBarIconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    Icon localIcon = ((Notification)localObject).getSmallIcon();
    if (localIcon == null)
    {
      handleNotificationError(paramStatusBarNotification, "No small icon in notification from " + paramStatusBarNotification.getPackageName());
      return null;
    }
    localObject = new StatusBarIcon(paramStatusBarNotification.getUser(), paramStatusBarNotification.getPackageName(), localIcon, ((Notification)localObject).iconLevel, ((Notification)localObject).number, StatusBarIconView.contentDescForNotification(this.mContext, (Notification)localObject));
    if (!localStatusBarIconView.set((StatusBarIcon)localObject))
    {
      handleNotificationError(paramStatusBarNotification, "Couldn't create icon: " + localObject);
      return null;
    }
    return localStatusBarIconView;
  }
  
  protected NotificationData.Entry createNotificationViews(StatusBarNotification paramStatusBarNotification)
  {
    Object localObject = createIcon(paramStatusBarNotification);
    if (localObject == null) {
      return null;
    }
    localObject = new NotificationData.Entry(paramStatusBarNotification, (StatusBarIconView)localObject);
    if (!inflateViews((NotificationData.Entry)localObject, this.mStackScroller))
    {
      handleNotificationError(paramStatusBarNotification, "Couldn't expand RemoteViews for: " + paramStatusBarNotification);
      return null;
    }
    return (NotificationData.Entry)localObject;
  }
  
  public void destroy()
  {
    this.mContext.unregisterReceiver(this.mBroadcastReceiver);
    try
    {
      this.mNotificationListener.unregisterAsSystemService();
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  protected void dismissKeyboardShortcuts() {}
  
  public void dismissKeyboardShortcutsMenu()
  {
    this.mHandler.removeMessages(1027);
    this.mHandler.sendEmptyMessage(1027);
  }
  
  protected void dismissKeyguardThenExecute(KeyguardHostView.OnDismissAction paramOnDismissAction, boolean paramBoolean)
  {
    paramOnDismissAction.onDismiss();
  }
  
  public void dismissPopups()
  {
    dismissPopups(-1, -1, true, false);
  }
  
  public void dismissPopups(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mNotificationGutsExposed != null) {
      this.mNotificationGutsExposed.closeControls(paramInt1, paramInt2, true);
    }
    if (paramBoolean1) {
      this.mStackScroller.resetExposedGearView(paramBoolean2, true);
    }
  }
  
  protected Bundle getActivityOptions()
  {
    ActivityOptions localActivityOptions = ActivityOptions.makeBasic();
    localActivityOptions.setLaunchStackId(1);
    return localActivityOptions.toBundle();
  }
  
  public String getCurrentMediaNotificationKey()
  {
    return null;
  }
  
  public NotificationGuts getExposedGuts()
  {
    return this.mNotificationGutsExposed;
  }
  
  public NotificationGroupManager getGroupManager()
  {
    return this.mGroupManager;
  }
  
  public List<String> getLockedPackageList()
  {
    if (this.mRecents != null) {
      return this.mRecents.getLockedPackageList();
    }
    return null;
  }
  
  protected abstract int getMaxKeyguardNotifications(boolean paramBoolean);
  
  protected SwipeHelper.LongPressListener getNotificationLongClicker()
  {
    new SwipeHelper.LongPressListener()
    {
      public boolean onLongPress(final View paramAnonymousView, final int paramAnonymousInt1, final int paramAnonymousInt2)
      {
        if (!(paramAnonymousView instanceof ExpandableNotificationRow)) {
          return false;
        }
        if (paramAnonymousView.getWindowToken() == null)
        {
          Log.e("StatusBar", "Trying to show notification guts, but not attached to window");
          return false;
        }
        paramAnonymousView = (ExpandableNotificationRow)paramAnonymousView;
        BaseStatusBar.-wrap0(BaseStatusBar.this, paramAnonymousView);
        final NotificationGuts localNotificationGuts = paramAnonymousView.getGuts();
        if (localNotificationGuts == null) {
          return false;
        }
        if (localNotificationGuts.getVisibility() == 0)
        {
          BaseStatusBar.-wrap1(BaseStatusBar.this, paramAnonymousInt1, paramAnonymousInt2);
          return false;
        }
        MetricsLogger.action(BaseStatusBar.this.mContext, 204);
        if (MdmLogger.sTouchGear) {
          MdmLogger.log("notification_level", "setting_slide", "1");
        }
        for (;;)
        {
          MdmLogger.sTouchGear = false;
          localNotificationGuts.setVisibility(4);
          localNotificationGuts.post(new Runnable()
          {
            public void run()
            {
              boolean bool = false;
              if (!localNotificationGuts.isAttachedToWindow()) {
                return;
              }
              BaseStatusBar.this.dismissPopups(-1, -1, false, false);
              localNotificationGuts.setVisibility(0);
              float f = (float)Math.hypot(Math.max(localNotificationGuts.getWidth() - paramAnonymousInt1, paramAnonymousInt1), Math.max(localNotificationGuts.getHeight() - paramAnonymousInt2, paramAnonymousInt2));
              Object localObject = ViewAnimationUtils.createCircularReveal(localNotificationGuts, paramAnonymousInt1, paramAnonymousInt2, 0.0F, f);
              ((Animator)localObject).setDuration(360L);
              ((Animator)localObject).setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
              ((Animator)localObject).addListener(new AnimatorListenerAdapter()
              {
                public void onAnimationEnd(Animator paramAnonymous3Animator)
                {
                  super.onAnimationEnd(paramAnonymous3Animator);
                  this.val$row.resetTranslation();
                }
              });
              ((Animator)localObject).start();
              localObject = localNotificationGuts;
              if (BaseStatusBar.this.mState == 1) {
                bool = true;
              }
              ((NotificationGuts)localObject).setExposed(true, bool);
              paramAnonymousView.closeRemoteInput();
              BaseStatusBar.this.mStackScroller.onHeightChanged(paramAnonymousView, true);
              BaseStatusBar.-set1(BaseStatusBar.this, localNotificationGuts);
            }
          });
          return true;
          MdmLogger.log("notification_level", "setting_long press", "1");
        }
      }
    };
  }
  
  void handleNotificationError(StatusBarNotification paramStatusBarNotification, String paramString)
  {
    removeNotification(paramStatusBarNotification.getKey(), null);
    try
    {
      this.mBarService.onNotificationError(paramStatusBarNotification.getPackageName(), paramStatusBarNotification.getTag(), paramStatusBarNotification.getId(), paramStatusBarNotification.getUid(), paramStatusBarNotification.getInitialPid(), paramString, paramStatusBarNotification.getUserId());
      return;
    }
    catch (RemoteException paramStatusBarNotification) {}
  }
  
  protected void handleVisibleToUserChanged(boolean paramBoolean)
  {
    if (paramBoolean) {}
    try
    {
      boolean bool = this.mHeadsUpManager.hasPinnedHeadsUp();
      if ((!isPanelFullyCollapsed()) && ((this.mState == 0) || (this.mState == 2))) {
        if (containsSpecialNotification()) {
          paramBoolean = false;
        }
      }
      for (;;)
      {
        int i = this.mNotificationData.getActiveNotifications().size();
        if ((bool) && (isPanelFullyCollapsed())) {
          i = 1;
        }
        for (;;)
        {
          this.mBarService.onPanelRevealed(paramBoolean, i);
          return;
          MetricsLogger.histogram(this.mContext, "note_load", i);
        }
        this.mBarService.onPanelHidden();
        return;
        paramBoolean = true;
        continue;
        paramBoolean = false;
      }
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void hideRecentApps(boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = 1;
    this.mHandler.removeMessages(1020);
    H localH = this.mHandler;
    int i;
    if (paramBoolean1)
    {
      i = 1;
      if (!paramBoolean2) {
        break label49;
      }
    }
    for (;;)
    {
      localH.obtainMessage(1020, i, j).sendToTarget();
      return;
      i = 0;
      break;
      label49:
      j = 0;
    }
  }
  
  protected void hideRecents(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mRecents != null) {
      this.mRecents.hideRecents(paramBoolean1, paramBoolean2);
    }
  }
  
  /* Error */
  protected boolean inflateViews(NotificationData.Entry paramEntry, ViewGroup paramViewGroup)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 427	com/android/systemui/statusbar/BaseStatusBar:mContext	Landroid/content/Context;
    //   4: aload_1
    //   5: getfield 563	com/android/systemui/statusbar/NotificationData$Entry:notification	Landroid/service/notification/StatusBarNotification;
    //   8: invokevirtual 433	android/service/notification/StatusBarNotification:getUser	()Landroid/os/UserHandle;
    //   11: invokevirtual 439	android/os/UserHandle:getIdentifier	()I
    //   14: invokestatic 443	com/android/systemui/statusbar/BaseStatusBar:getPackageManagerForUser	(Landroid/content/Context;I)Landroid/content/pm/PackageManager;
    //   17: astore 16
    //   19: aload_1
    //   20: getfield 563	com/android/systemui/statusbar/NotificationData$Entry:notification	Landroid/service/notification/StatusBarNotification;
    //   23: astore 15
    //   25: aload_1
    //   26: aload_0
    //   27: getfield 427	com/android/systemui/statusbar/BaseStatusBar:mContext	Landroid/content/Context;
    //   30: aconst_null
    //   31: invokevirtual 1111	com/android/systemui/statusbar/NotificationData$Entry:cacheContentViews	(Landroid/content/Context;Landroid/app/Notification;)Z
    //   34: pop
    //   35: aload_1
    //   36: getfield 777	com/android/systemui/statusbar/NotificationData$Entry:cachedContentView	Landroid/widget/RemoteViews;
    //   39: astore 20
    //   41: aload_1
    //   42: getfield 780	com/android/systemui/statusbar/NotificationData$Entry:cachedBigContentView	Landroid/widget/RemoteViews;
    //   45: astore 19
    //   47: aload_1
    //   48: getfield 783	com/android/systemui/statusbar/NotificationData$Entry:cachedHeadsUpContentView	Landroid/widget/RemoteViews;
    //   51: astore 18
    //   53: aload_1
    //   54: getfield 786	com/android/systemui/statusbar/NotificationData$Entry:cachedPublicContentView	Landroid/widget/RemoteViews;
    //   57: astore 17
    //   59: aload 20
    //   61: ifnonnull +50 -> 111
    //   64: ldc_w 1113
    //   67: new 905	java/lang/StringBuilder
    //   70: dup
    //   71: invokespecial 906	java/lang/StringBuilder:<init>	()V
    //   74: ldc_w 1115
    //   77: invokevirtual 910	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   80: aload 15
    //   82: invokevirtual 662	android/service/notification/StatusBarNotification:getNotification	()Landroid/app/Notification;
    //   85: invokevirtual 969	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   88: invokevirtual 922	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   91: invokestatic 1121	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   94: pop
    //   95: iconst_0
    //   96: ireturn
    //   97: astore_1
    //   98: ldc_w 1113
    //   101: ldc_w 1123
    //   104: aload_1
    //   105: invokestatic 1127	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   108: pop
    //   109: iconst_0
    //   110: ireturn
    //   111: iconst_0
    //   112: istore 6
    //   114: iconst_0
    //   115: istore 7
    //   117: iconst_0
    //   118: istore 8
    //   120: aload_1
    //   121: getfield 813	com/android/systemui/statusbar/NotificationData$Entry:row	Lcom/android/systemui/statusbar/ExpandableNotificationRow;
    //   124: ifnull +552 -> 676
    //   127: aload_1
    //   128: getfield 813	com/android/systemui/statusbar/NotificationData$Entry:row	Lcom/android/systemui/statusbar/ExpandableNotificationRow;
    //   131: astore 12
    //   133: aload 12
    //   135: invokevirtual 1130	com/android/systemui/statusbar/ExpandableNotificationRow:hasUserChangedExpansion	()Z
    //   138: istore 9
    //   140: aload 12
    //   142: invokevirtual 1133	com/android/systemui/statusbar/ExpandableNotificationRow:isUserExpanded	()Z
    //   145: istore 10
    //   147: aload 12
    //   149: invokevirtual 1136	com/android/systemui/statusbar/ExpandableNotificationRow:isUserLocked	()Z
    //   152: istore 11
    //   154: aload_1
    //   155: invokevirtual 1139	com/android/systemui/statusbar/NotificationData$Entry:reset	()V
    //   158: iload 9
    //   160: istore 6
    //   162: aload 12
    //   164: astore_2
    //   165: iload 10
    //   167: istore 7
    //   169: iload 11
    //   171: istore 8
    //   173: iload 9
    //   175: ifeq +25 -> 200
    //   178: aload 12
    //   180: iload 10
    //   182: invokevirtual 1142	com/android/systemui/statusbar/ExpandableNotificationRow:setUserExpanded	(Z)V
    //   185: iload 11
    //   187: istore 8
    //   189: iload 10
    //   191: istore 7
    //   193: aload 12
    //   195: astore_2
    //   196: iload 9
    //   198: istore 6
    //   200: aload_0
    //   201: aload_2
    //   202: invokevirtual 1146	com/android/systemui/statusbar/BaseStatusBar:workAroundBadLayerDrawableOpacity	(Landroid/view/View;)V
    //   205: aload_0
    //   206: aload_2
    //   207: invokevirtual 1148	com/android/systemui/statusbar/BaseStatusBar:bindDismissListener	(Lcom/android/systemui/statusbar/ExpandableNotificationRow;)V
    //   210: aload_2
    //   211: invokevirtual 1152	com/android/systemui/statusbar/ExpandableNotificationRow:getPrivateLayout	()Lcom/android/systemui/statusbar/NotificationContentView;
    //   214: astore 21
    //   216: aload_2
    //   217: invokevirtual 1155	com/android/systemui/statusbar/ExpandableNotificationRow:getPublicLayout	()Lcom/android/systemui/statusbar/NotificationContentView;
    //   220: astore 22
    //   222: aload_2
    //   223: ldc_w 1156
    //   226: invokevirtual 1159	com/android/systemui/statusbar/ExpandableNotificationRow:setDescendantFocusability	(I)V
    //   229: getstatic 284	com/android/systemui/statusbar/BaseStatusBar:ENABLE_REMOTE_INPUT	Z
    //   232: ifeq +10 -> 242
    //   235: aload_2
    //   236: ldc_w 1160
    //   239: invokevirtual 1159	com/android/systemui/statusbar/ExpandableNotificationRow:setDescendantFocusability	(I)V
    //   242: aload_0
    //   243: getfield 349	com/android/systemui/statusbar/BaseStatusBar:mNotificationClicker	Lcom/android/systemui/statusbar/BaseStatusBar$NotificationClicker;
    //   246: aload_2
    //   247: aload 15
    //   249: invokevirtual 817	com/android/systemui/statusbar/BaseStatusBar$NotificationClicker:register	(Lcom/android/systemui/statusbar/ExpandableNotificationRow;Landroid/service/notification/StatusBarNotification;)V
    //   252: aconst_null
    //   253: astore 12
    //   255: aconst_null
    //   256: astore 13
    //   258: aconst_null
    //   259: astore 14
    //   261: aload 20
    //   263: aload 15
    //   265: aload_0
    //   266: getfield 427	com/android/systemui/statusbar/BaseStatusBar:mContext	Landroid/content/Context;
    //   269: invokevirtual 803	android/service/notification/StatusBarNotification:getPackageContext	(Landroid/content/Context;)Landroid/content/Context;
    //   272: aload 21
    //   274: aload_0
    //   275: getfield 377	com/android/systemui/statusbar/BaseStatusBar:mOnClickHandler	Landroid/widget/RemoteViews$OnClickHandler;
    //   278: invokevirtual 1164	android/widget/RemoteViews:apply	(Landroid/content/Context;Landroid/view/ViewGroup;Landroid/widget/RemoteViews$OnClickHandler;)Landroid/view/View;
    //   281: astore 20
    //   283: aload 19
    //   285: ifnull +25 -> 310
    //   288: aload 19
    //   290: aload 15
    //   292: aload_0
    //   293: getfield 427	com/android/systemui/statusbar/BaseStatusBar:mContext	Landroid/content/Context;
    //   296: invokevirtual 803	android/service/notification/StatusBarNotification:getPackageContext	(Landroid/content/Context;)Landroid/content/Context;
    //   299: aload 21
    //   301: aload_0
    //   302: getfield 377	com/android/systemui/statusbar/BaseStatusBar:mOnClickHandler	Landroid/widget/RemoteViews$OnClickHandler;
    //   305: invokevirtual 1164	android/widget/RemoteViews:apply	(Landroid/content/Context;Landroid/view/ViewGroup;Landroid/widget/RemoteViews$OnClickHandler;)Landroid/view/View;
    //   308: astore 12
    //   310: aload 18
    //   312: ifnull +25 -> 337
    //   315: aload 18
    //   317: aload 15
    //   319: aload_0
    //   320: getfield 427	com/android/systemui/statusbar/BaseStatusBar:mContext	Landroid/content/Context;
    //   323: invokevirtual 803	android/service/notification/StatusBarNotification:getPackageContext	(Landroid/content/Context;)Landroid/content/Context;
    //   326: aload 21
    //   328: aload_0
    //   329: getfield 377	com/android/systemui/statusbar/BaseStatusBar:mOnClickHandler	Landroid/widget/RemoteViews$OnClickHandler;
    //   332: invokevirtual 1164	android/widget/RemoteViews:apply	(Landroid/content/Context;Landroid/view/ViewGroup;Landroid/widget/RemoteViews$OnClickHandler;)Landroid/view/View;
    //   335: astore 13
    //   337: aload 17
    //   339: ifnull +25 -> 364
    //   342: aload 17
    //   344: aload 15
    //   346: aload_0
    //   347: getfield 427	com/android/systemui/statusbar/BaseStatusBar:mContext	Landroid/content/Context;
    //   350: invokevirtual 803	android/service/notification/StatusBarNotification:getPackageContext	(Landroid/content/Context;)Landroid/content/Context;
    //   353: aload 22
    //   355: aload_0
    //   356: getfield 377	com/android/systemui/statusbar/BaseStatusBar:mOnClickHandler	Landroid/widget/RemoteViews$OnClickHandler;
    //   359: invokevirtual 1164	android/widget/RemoteViews:apply	(Landroid/content/Context;Landroid/view/ViewGroup;Landroid/widget/RemoteViews$OnClickHandler;)Landroid/view/View;
    //   362: astore 14
    //   364: aload 20
    //   366: ifnull +16 -> 382
    //   369: aload 20
    //   371: iconst_1
    //   372: invokevirtual 1167	android/view/View:setIsRootNamespace	(Z)V
    //   375: aload 21
    //   377: aload 20
    //   379: invokevirtual 1172	com/android/systemui/statusbar/NotificationContentView:setContractedChild	(Landroid/view/View;)V
    //   382: aload 12
    //   384: ifnull +16 -> 400
    //   387: aload 12
    //   389: iconst_1
    //   390: invokevirtual 1167	android/view/View:setIsRootNamespace	(Z)V
    //   393: aload 21
    //   395: aload 12
    //   397: invokevirtual 1175	com/android/systemui/statusbar/NotificationContentView:setExpandedChild	(Landroid/view/View;)V
    //   400: aload 13
    //   402: ifnull +16 -> 418
    //   405: aload 13
    //   407: iconst_1
    //   408: invokevirtual 1167	android/view/View:setIsRootNamespace	(Z)V
    //   411: aload 21
    //   413: aload 13
    //   415: invokevirtual 1178	com/android/systemui/statusbar/NotificationContentView:setHeadsUpChild	(Landroid/view/View;)V
    //   418: aload 14
    //   420: ifnull +16 -> 436
    //   423: aload 14
    //   425: iconst_1
    //   426: invokevirtual 1167	android/view/View:setIsRootNamespace	(Z)V
    //   429: aload 22
    //   431: aload 14
    //   433: invokevirtual 1172	com/android/systemui/statusbar/NotificationContentView:setContractedChild	(Landroid/view/View;)V
    //   436: aload_1
    //   437: aload 16
    //   439: aload 15
    //   441: invokevirtual 447	android/service/notification/StatusBarNotification:getPackageName	()Ljava/lang/String;
    //   444: iconst_0
    //   445: invokevirtual 467	android/content/pm/PackageManager:getApplicationInfo	(Ljava/lang/String;I)Landroid/content/pm/ApplicationInfo;
    //   448: getfield 1181	android/content/pm/ApplicationInfo:targetSdkVersion	I
    //   451: putfield 848	com/android/systemui/statusbar/NotificationData$Entry:targetSdk	I
    //   454: aload_1
    //   455: getfield 563	com/android/systemui/statusbar/NotificationData$Entry:notification	Landroid/service/notification/StatusBarNotification;
    //   458: invokevirtual 662	android/service/notification/StatusBarNotification:getNotification	()Landroid/app/Notification;
    //   461: getfield 1185	android/app/Notification:publicVersion	Landroid/app/Notification;
    //   464: ifnonnull +444 -> 908
    //   467: iconst_1
    //   468: istore 9
    //   470: aload_1
    //   471: iload 9
    //   473: putfield 1188	com/android/systemui/statusbar/NotificationData$Entry:autoRedacted	Z
    //   476: aload_1
    //   477: aload_2
    //   478: putfield 813	com/android/systemui/statusbar/NotificationData$Entry:row	Lcom/android/systemui/statusbar/ExpandableNotificationRow;
    //   481: aload_1
    //   482: getfield 813	com/android/systemui/statusbar/NotificationData$Entry:row	Lcom/android/systemui/statusbar/ExpandableNotificationRow;
    //   485: aload_0
    //   486: invokevirtual 1192	com/android/systemui/statusbar/ExpandableNotificationRow:setOnActivatedListener	(Lcom/android/systemui/statusbar/ActivatableNotificationView$OnActivatedListener;)V
    //   489: aload_1
    //   490: getfield 813	com/android/systemui/statusbar/NotificationData$Entry:row	Lcom/android/systemui/statusbar/ExpandableNotificationRow;
    //   493: astore 13
    //   495: aload 12
    //   497: ifnull +417 -> 914
    //   500: iconst_1
    //   501: istore 9
    //   503: aload 13
    //   505: iload 9
    //   507: invokevirtual 1195	com/android/systemui/statusbar/ExpandableNotificationRow:setExpandable	(Z)V
    //   510: aload_0
    //   511: aload 15
    //   513: aload_1
    //   514: invokevirtual 1197	com/android/systemui/statusbar/BaseStatusBar:applyColorsAndBackgrounds	(Landroid/service/notification/StatusBarNotification;Lcom/android/systemui/statusbar/NotificationData$Entry;)V
    //   517: iload 6
    //   519: ifeq +9 -> 528
    //   522: aload_2
    //   523: iload 7
    //   525: invokevirtual 1142	com/android/systemui/statusbar/ExpandableNotificationRow:setUserExpanded	(Z)V
    //   528: aload_2
    //   529: iload 8
    //   531: invokevirtual 1200	com/android/systemui/statusbar/ExpandableNotificationRow:setUserLocked	(Z)V
    //   534: aload_2
    //   535: aload_1
    //   536: invokevirtual 821	com/android/systemui/statusbar/ExpandableNotificationRow:onNotificationUpdated	(Lcom/android/systemui/statusbar/NotificationData$Entry;)V
    //   539: aload_1
    //   540: getfield 563	com/android/systemui/statusbar/NotificationData$Entry:notification	Landroid/service/notification/StatusBarNotification;
    //   543: invokevirtual 662	android/service/notification/StatusBarNotification:getNotification	()Landroid/app/Notification;
    //   546: getfield 1203	android/app/Notification:visibility	I
    //   549: istore_3
    //   550: aload_0
    //   551: getfield 357	com/android/systemui/statusbar/BaseStatusBar:mIsSecure	Z
    //   554: ifeq +366 -> 920
    //   557: aload_0
    //   558: getfield 520	com/android/systemui/statusbar/BaseStatusBar:mNotificationData	Lcom/android/systemui/statusbar/NotificationData;
    //   561: aload_1
    //   562: getfield 1207	com/android/systemui/statusbar/NotificationData$Entry:key	Ljava/lang/String;
    //   565: invokevirtual 1210	com/android/systemui/statusbar/NotificationData:isLock	(Ljava/lang/String;)Z
    //   568: istore 6
    //   570: aload_0
    //   571: aload_1
    //   572: getfield 563	com/android/systemui/statusbar/NotificationData$Entry:notification	Landroid/service/notification/StatusBarNotification;
    //   575: invokevirtual 1060	android/service/notification/StatusBarNotification:getUserId	()I
    //   578: invokevirtual 1213	com/android/systemui/statusbar/BaseStatusBar:userAllowsPrivateNotificationsInPublic	(I)Z
    //   581: ifeq +345 -> 926
    //   584: iconst_0
    //   585: istore 4
    //   587: iload_3
    //   588: ifne +344 -> 932
    //   591: iconst_1
    //   592: istore 5
    //   594: aload_0
    //   595: getfield 520	com/android/systemui/statusbar/BaseStatusBar:mNotificationData	Lcom/android/systemui/statusbar/NotificationData;
    //   598: aload_1
    //   599: getfield 563	com/android/systemui/statusbar/NotificationData$Entry:notification	Landroid/service/notification/StatusBarNotification;
    //   602: invokevirtual 523	android/service/notification/StatusBarNotification:getKey	()Ljava/lang/String;
    //   605: invokevirtual 1216	com/android/systemui/statusbar/NotificationData:getVisibilityOverride	(Ljava/lang/String;)I
    //   608: ifne +330 -> 938
    //   611: iconst_1
    //   612: istore_3
    //   613: iload 5
    //   615: ifeq +8 -> 623
    //   618: iload 4
    //   620: ifne +323 -> 943
    //   623: iload_3
    //   624: ifeq +10 -> 634
    //   627: aload_0
    //   628: invokevirtual 1219	com/android/systemui/statusbar/BaseStatusBar:isLockscreenPublicMode	()Z
    //   631: ifne +317 -> 948
    //   634: iload 6
    //   636: istore 7
    //   638: iload 7
    //   640: ifeq +13 -> 653
    //   643: aload_0
    //   644: aload_1
    //   645: aload_1
    //   646: getfield 563	com/android/systemui/statusbar/NotificationData$Entry:notification	Landroid/service/notification/StatusBarNotification;
    //   649: iconst_1
    //   650: invokevirtual 1223	com/android/systemui/statusbar/BaseStatusBar:updatePublicContentView	(Lcom/android/systemui/statusbar/NotificationData$Entry;Landroid/service/notification/StatusBarNotification;I)V
    //   653: iload_3
    //   654: ifne +300 -> 954
    //   657: iload 6
    //   659: istore 7
    //   661: iload 4
    //   663: ifne +297 -> 960
    //   666: aload_2
    //   667: iload 7
    //   669: iload 6
    //   671: invokevirtual 1226	com/android/systemui/statusbar/ExpandableNotificationRow:setSensitive	(ZZ)V
    //   674: iconst_1
    //   675: ireturn
    //   676: aload_0
    //   677: getfield 427	com/android/systemui/statusbar/BaseStatusBar:mContext	Landroid/content/Context;
    //   680: ldc_w 1228
    //   683: invokevirtual 1232	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   686: checkcast 1234	android/view/LayoutInflater
    //   689: ldc_w 1235
    //   692: aload_2
    //   693: iconst_0
    //   694: invokevirtual 1239	android/view/LayoutInflater:inflate	(ILandroid/view/ViewGroup;Z)Landroid/view/View;
    //   697: checkcast 416	com/android/systemui/statusbar/ExpandableNotificationRow
    //   700: astore 13
    //   702: aload 13
    //   704: aload_0
    //   705: aload_1
    //   706: getfield 563	com/android/systemui/statusbar/NotificationData$Entry:notification	Landroid/service/notification/StatusBarNotification;
    //   709: invokevirtual 523	android/service/notification/StatusBarNotification:getKey	()Ljava/lang/String;
    //   712: invokevirtual 1243	com/android/systemui/statusbar/ExpandableNotificationRow:setExpansionLogger	(Lcom/android/systemui/statusbar/ExpandableNotificationRow$ExpansionLogger;Ljava/lang/String;)V
    //   715: aload 13
    //   717: aload_0
    //   718: getfield 313	com/android/systemui/statusbar/BaseStatusBar:mGroupManager	Lcom/android/systemui/statusbar/phone/NotificationGroupManager;
    //   721: invokevirtual 1247	com/android/systemui/statusbar/ExpandableNotificationRow:setGroupManager	(Lcom/android/systemui/statusbar/phone/NotificationGroupManager;)V
    //   724: aload 13
    //   726: aload_0
    //   727: getfield 1067	com/android/systemui/statusbar/BaseStatusBar:mHeadsUpManager	Lcom/android/systemui/statusbar/policy/HeadsUpManager;
    //   730: invokevirtual 1251	com/android/systemui/statusbar/ExpandableNotificationRow:setHeadsUpManager	(Lcom/android/systemui/statusbar/policy/HeadsUpManager;)V
    //   733: aload 13
    //   735: aload_0
    //   736: getfield 1253	com/android/systemui/statusbar/BaseStatusBar:mRemoteInputController	Lcom/android/systemui/statusbar/RemoteInputController;
    //   739: invokevirtual 1257	com/android/systemui/statusbar/ExpandableNotificationRow:setRemoteInputController	(Lcom/android/systemui/statusbar/RemoteInputController;)V
    //   742: aload 13
    //   744: aload_0
    //   745: invokevirtual 1261	com/android/systemui/statusbar/ExpandableNotificationRow:setOnExpandClickListener	(Lcom/android/systemui/statusbar/ExpandableNotificationRow$OnExpandClickListener;)V
    //   748: aload 15
    //   750: invokevirtual 447	android/service/notification/StatusBarNotification:getPackageName	()Ljava/lang/String;
    //   753: astore 12
    //   755: aload 12
    //   757: astore_2
    //   758: aload 16
    //   760: aload 12
    //   762: sipush 8704
    //   765: invokevirtual 467	android/content/pm/PackageManager:getApplicationInfo	(Ljava/lang/String;I)Landroid/content/pm/ApplicationInfo;
    //   768: astore 14
    //   770: aload_2
    //   771: astore 12
    //   773: aload 14
    //   775: ifnull +15 -> 790
    //   778: aload 16
    //   780: aload 14
    //   782: invokevirtual 471	android/content/pm/PackageManager:getApplicationLabel	(Landroid/content/pm/ApplicationInfo;)Ljava/lang/CharSequence;
    //   785: invokestatic 477	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   788: astore 12
    //   790: aload 13
    //   792: aload 12
    //   794: invokevirtual 1264	com/android/systemui/statusbar/ExpandableNotificationRow:setAppName	(Ljava/lang/String;)V
    //   797: aload 13
    //   799: astore_2
    //   800: goto -600 -> 200
    //   803: astore_1
    //   804: new 905	java/lang/StringBuilder
    //   807: dup
    //   808: invokespecial 906	java/lang/StringBuilder:<init>	()V
    //   811: aload 15
    //   813: invokevirtual 447	android/service/notification/StatusBarNotification:getPackageName	()Ljava/lang/String;
    //   816: invokevirtual 910	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   819: ldc_w 912
    //   822: invokevirtual 910	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   825: aload 15
    //   827: invokevirtual 913	android/service/notification/StatusBarNotification:getId	()I
    //   830: invokestatic 919	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   833: invokevirtual 910	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   836: invokevirtual 922	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   839: astore_2
    //   840: ldc_w 1113
    //   843: new 905	java/lang/StringBuilder
    //   846: dup
    //   847: invokespecial 906	java/lang/StringBuilder:<init>	()V
    //   850: ldc_w 1266
    //   853: invokevirtual 910	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   856: aload_2
    //   857: invokevirtual 910	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   860: invokevirtual 922	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   863: aload_1
    //   864: invokestatic 1127	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   867: pop
    //   868: iconst_0
    //   869: ireturn
    //   870: astore 13
    //   872: ldc_w 1113
    //   875: new 905	java/lang/StringBuilder
    //   878: dup
    //   879: invokespecial 906	java/lang/StringBuilder:<init>	()V
    //   882: ldc_w 1268
    //   885: invokevirtual 910	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   888: aload 15
    //   890: invokevirtual 447	android/service/notification/StatusBarNotification:getPackageName	()Ljava/lang/String;
    //   893: invokevirtual 910	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   896: invokevirtual 922	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   899: aload 13
    //   901: invokestatic 1127	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   904: pop
    //   905: goto -451 -> 454
    //   908: iconst_0
    //   909: istore 9
    //   911: goto -441 -> 470
    //   914: iconst_0
    //   915: istore 9
    //   917: goto -414 -> 503
    //   920: iconst_0
    //   921: istore 6
    //   923: goto -353 -> 570
    //   926: iconst_1
    //   927: istore 4
    //   929: goto -342 -> 587
    //   932: iconst_0
    //   933: istore 5
    //   935: goto -341 -> 594
    //   938: iconst_0
    //   939: istore_3
    //   940: goto -327 -> 613
    //   943: iconst_1
    //   944: istore_3
    //   945: goto -322 -> 623
    //   948: iconst_1
    //   949: istore 7
    //   951: goto -313 -> 638
    //   954: iconst_1
    //   955: istore 7
    //   957: goto -296 -> 661
    //   960: iconst_1
    //   961: istore 6
    //   963: goto -297 -> 666
    //   966: astore 12
    //   968: aload_2
    //   969: astore 12
    //   971: goto -181 -> 790
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	974	0	this	BaseStatusBar
    //   0	974	1	paramEntry	NotificationData.Entry
    //   0	974	2	paramViewGroup	ViewGroup
    //   549	396	3	i	int
    //   585	343	4	j	int
    //   592	342	5	k	int
    //   112	850	6	bool1	boolean
    //   115	841	7	bool2	boolean
    //   118	412	8	bool3	boolean
    //   138	778	9	bool4	boolean
    //   145	45	10	bool5	boolean
    //   152	34	11	bool6	boolean
    //   131	662	12	localObject1	Object
    //   966	1	12	localNameNotFoundException1	PackageManager.NameNotFoundException
    //   969	1	12	localViewGroup	ViewGroup
    //   256	542	13	localObject2	Object
    //   870	30	13	localNameNotFoundException2	PackageManager.NameNotFoundException
    //   259	522	14	localObject3	Object
    //   23	866	15	localStatusBarNotification	StatusBarNotification
    //   17	762	16	localPackageManager	PackageManager
    //   57	286	17	localRemoteViews1	RemoteViews
    //   51	265	18	localRemoteViews2	RemoteViews
    //   45	244	19	localRemoteViews3	RemoteViews
    //   39	339	20	localObject4	Object
    //   214	198	21	localNotificationContentView1	NotificationContentView
    //   220	210	22	localNotificationContentView2	NotificationContentView
    // Exception table:
    //   from	to	target	type
    //   25	35	97	java/lang/RuntimeException
    //   261	283	803	java/lang/RuntimeException
    //   288	310	803	java/lang/RuntimeException
    //   315	337	803	java/lang/RuntimeException
    //   342	364	803	java/lang/RuntimeException
    //   369	382	803	java/lang/RuntimeException
    //   387	400	803	java/lang/RuntimeException
    //   405	418	803	java/lang/RuntimeException
    //   423	436	803	java/lang/RuntimeException
    //   436	454	870	android/content/pm/PackageManager$NameNotFoundException
    //   758	770	966	android/content/pm/PackageManager$NameNotFoundException
    //   778	790	966	android/content/pm/PackageManager$NameNotFoundException
  }
  
  public boolean isBouncerShowing()
  {
    return this.mBouncerShowing;
  }
  
  public boolean isCameraAllowedByAdmin()
  {
    if (this.mDevicePolicyManager.getCameraDisabled(null, this.mCurrentUserId)) {
      return false;
    }
    if ((isKeyguardShowing()) && (isKeyguardSecure())) {
      return (this.mDevicePolicyManager.getKeyguardDisabledFeatures(null, this.mCurrentUserId) & 0x2) == 0;
    }
    return true;
  }
  
  public boolean isCollapsing()
  {
    return false;
  }
  
  /* Error */
  protected boolean isCurrentProfile(int paramInt)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_3
    //   2: aload_0
    //   3: getfield 320	com/android/systemui/statusbar/BaseStatusBar:mCurrentProfiles	Landroid/util/SparseArray;
    //   6: astore 4
    //   8: aload 4
    //   10: monitorenter
    //   11: iload_3
    //   12: istore_2
    //   13: iload_1
    //   14: iconst_m1
    //   15: if_icmpeq +20 -> 35
    //   18: aload_0
    //   19: getfield 320	com/android/systemui/statusbar/BaseStatusBar:mCurrentProfiles	Landroid/util/SparseArray;
    //   22: iload_1
    //   23: invokevirtual 1285	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   26: astore 5
    //   28: aload 5
    //   30: ifnull +10 -> 40
    //   33: iload_3
    //   34: istore_2
    //   35: aload 4
    //   37: monitorexit
    //   38: iload_2
    //   39: ireturn
    //   40: iconst_0
    //   41: istore_2
    //   42: goto -7 -> 35
    //   45: astore 5
    //   47: aload 4
    //   49: monitorexit
    //   50: aload 5
    //   52: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	53	0	this	BaseStatusBar
    //   0	53	1	paramInt	int
    //   12	30	2	bool1	boolean
    //   1	33	3	bool2	boolean
    //   6	42	4	localSparseArray	SparseArray
    //   26	3	5	localObject1	Object
    //   45	6	5	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   18	28	45	finally
  }
  
  public boolean isDeviceInVrMode()
  {
    return this.mVrMode;
  }
  
  public boolean isDeviceProvisioned()
  {
    return this.mDeviceProvisioned;
  }
  
  protected boolean isInGameMode()
  {
    if (this.mGameModeStatus) {
      return this.mGameModeBlockNotification;
    }
    return false;
  }
  
  public boolean isKeyguardSecure()
  {
    if (this.mStatusBarKeyguardViewManager == null)
    {
      Slog.w("StatusBar", "isKeyguardSecure() called before startKeyguard(), returning false", new Throwable());
      return false;
    }
    return this.mStatusBarKeyguardViewManager.isSecure();
  }
  
  public boolean isKeyguardShowing()
  {
    if (this.mStatusBarKeyguardViewManager == null)
    {
      Slog.i("StatusBar", "isKeyguardShowing() called before startKeyguard(), returning true");
      return true;
    }
    return this.mStatusBarKeyguardViewManager.isShowing();
  }
  
  public boolean isLockscreenPublicMode()
  {
    return this.mLockscreenPublicMode;
  }
  
  public boolean isMediaNotification(NotificationData.Entry paramEntry)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramEntry.getExpandedContentView() != null)
    {
      bool1 = bool2;
      if (paramEntry.getExpandedContentView().findViewById(16909245) != null) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean isNotificationForCurrentProfiles(StatusBarNotification paramStatusBarNotification)
  {
    int i = this.mCurrentUserId;
    return isCurrentProfile(paramStatusBarNotification.getUserId());
  }
  
  public abstract boolean isPanelFullyCollapsed();
  
  protected abstract boolean isSnoozedPackage(StatusBarNotification paramStatusBarNotification);
  
  public void logNotificationExpansion(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    try
    {
      this.mBarService.onNotificationExpansionChanged(paramString, paramBoolean1, paramBoolean2);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public abstract void maybeEscalateHeadsUp();
  
  protected void notifyHeadsUpScreenOff()
  {
    maybeEscalateHeadsUp();
  }
  
  protected void notifyUserAboutHiddenNotifications()
  {
    if (Settings.Secure.getInt(this.mContext.getContentResolver(), "show_note_about_notification_hiding", 1) != 0)
    {
      Log.d("StatusBar", "user hasn't seen notification about hidden notifications");
      if (!this.mLockPatternUtils.isSecure(KeyguardUpdateMonitor.getCurrentUser()))
      {
        Log.d("StatusBar", "insecure lockscreen, skipping notification");
        Settings.Secure.putInt(this.mContext.getContentResolver(), "show_note_about_notification_hiding", 0);
        return;
      }
      Log.d("StatusBar", "disabling lockecreen notifications and alerting the user");
      Settings.Secure.putInt(this.mContext.getContentResolver(), "lock_screen_show_notifications", 0);
      Settings.Secure.putInt(this.mContext.getContentResolver(), "lock_screen_allow_private_notifications", 0);
      Object localObject2 = this.mContext.getPackageName();
      Object localObject1 = PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.android.systemui.statusbar.banner_action_cancel").setPackage((String)localObject2), 268435456);
      localObject2 = PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.android.systemui.statusbar.banner_action_setup").setPackage((String)localObject2), 268435456);
      localObject1 = new Notification.Builder(this.mContext).setSmallIcon(2130837684).setContentTitle(this.mContext.getString(2131690423)).setContentText(this.mContext.getString(2131690424)).setPriority(1).setOngoing(true).setColor(this.mContext.getColor(17170523)).setContentIntent((PendingIntent)localObject2).addAction(2130837701, this.mContext.getString(2131690425), (PendingIntent)localObject1).addAction(2130837865, this.mContext.getString(2131690426), (PendingIntent)localObject2);
      overrideNotificationAppName(this.mContext, (Notification.Builder)localObject1);
      ((NotificationManager)this.mContext.getSystemService("notification")).notify(2131951670, ((Notification.Builder)localObject1).build());
    }
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    Locale localLocale = this.mContext.getResources().getConfiguration().locale;
    int i = TextUtils.getLayoutDirectionFromLocale(localLocale);
    float f = paramConfiguration.fontScale;
    int j = paramConfiguration.densityDpi;
    if ((j != this.mDensity) || (this.mFontScale != f))
    {
      onDensityOrFontScaleChanged();
      this.mDensity = j;
      this.mFontScale = f;
    }
    if ((!localLocale.equals(this.mLocale)) || (i != this.mLayoutDirection))
    {
      this.mLocale = localLocale;
      this.mLayoutDirection = i;
      refreshLayout(i);
    }
  }
  
  protected void onDensityOrFontScaleChanged()
  {
    ArrayList localArrayList = this.mNotificationData.getActiveNotifications();
    int i = 0;
    if (i < localArrayList.size())
    {
      NotificationData.Entry localEntry = (NotificationData.Entry)localArrayList.get(i);
      if (localEntry.row.getGuts() == this.mNotificationGutsExposed) {}
      for (int j = 1;; j = 0)
      {
        localEntry.row.reInflateViews();
        if (j != 0)
        {
          this.mNotificationGutsExposed = localEntry.row.getGuts();
          bindGuts(localEntry.row);
        }
        inflateViews(localEntry, this.mStackScroller);
        i += 1;
        break;
      }
    }
  }
  
  public void onExpandClicked(NotificationData.Entry paramEntry, boolean paramBoolean) {}
  
  public void onGutsClosed(NotificationGuts paramNotificationGuts)
  {
    this.mStackScroller.onHeightChanged(null, true);
    this.mNotificationGutsExposed = null;
  }
  
  protected void onLockedNotificationImportanceChange(KeyguardHostView.OnDismissAction paramOnDismissAction) {}
  
  protected void onLockedRemoteInput(ExpandableNotificationRow paramExpandableNotificationRow, View paramView) {}
  
  protected void onLockedWorkRemoteInput(int paramInt, ExpandableNotificationRow paramExpandableNotificationRow, View paramView) {}
  
  public void onPanelLaidOut()
  {
    if ((this.mState == 1) && (getMaxKeyguardNotifications(false) != getMaxKeyguardNotifications(true))) {
      updateRowStates();
    }
  }
  
  public boolean onSecureLockScreen()
  {
    return isLockscreenPublicMode();
  }
  
  protected void onWorkChallengeUnlocked() {}
  
  public void overrideActivityPendingAppTransition(boolean paramBoolean)
  {
    if (paramBoolean) {}
    try
    {
      this.mWindowManagerService.overridePendingAppTransition(null, 0, 0, null);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("StatusBar", "Error overriding app transition: " + localRemoteException);
    }
  }
  
  protected void performRemoveNotification(StatusBarNotification paramStatusBarNotification, boolean paramBoolean)
  {
    String str1 = paramStatusBarNotification.getPackageName();
    String str2 = paramStatusBarNotification.getTag();
    int i = paramStatusBarNotification.getId();
    int j = paramStatusBarNotification.getUserId();
    try
    {
      this.mBarService.onNotificationClear(str1, str2, i, j);
      boolean bool = paramBoolean;
      if (FORCE_REMOTE_INPUT_HISTORY)
      {
        bool = paramBoolean;
        if (this.mKeysKeptForRemoteInput.contains(paramStatusBarNotification.getKey()))
        {
          this.mKeysKeptForRemoteInput.remove(paramStatusBarNotification.getKey());
          bool = true;
        }
      }
      if (this.mRemoteInputEntriesToRemoveOnCollapse.remove(this.mNotificationData.get(paramStatusBarNotification.getKey()))) {
        bool = true;
      }
      if (bool) {
        removeNotification(paramStatusBarNotification.getKey(), null);
      }
      return;
    }
    catch (RemoteException paramStatusBarNotification) {}
  }
  
  public void preloadRecentApps()
  {
    this.mHandler.removeMessages(1022);
    this.mHandler.sendEmptyMessage(1022);
  }
  
  protected void preloadRecents()
  {
    if (this.mRecents != null) {
      this.mRecents.preloadRecents();
    }
  }
  
  protected abstract void refreshLayout(int paramInt);
  
  public abstract void removeNotification(String paramString, NotificationListenerService.RankingMap paramRankingMap);
  
  protected StatusBarNotification removeNotificationViews(String paramString, NotificationListenerService.RankingMap paramRankingMap)
  {
    paramRankingMap = this.mNotificationData.remove(paramString, paramRankingMap);
    if (paramRankingMap == null)
    {
      Log.w("StatusBar", "removeNotification for unknown key: " + paramString);
      return null;
    }
    updateNotifications();
    return paramRankingMap.notification;
  }
  
  protected void sendCloseSystemWindows(String paramString)
  {
    if (ActivityManagerNative.isSystemReady()) {}
    try
    {
      ActivityManagerNative.getDefault().closeSystemDialogs(paramString);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  protected abstract void setAreThereNotifications();
  
  public void setBouncerShowing(boolean paramBoolean)
  {
    this.mBouncerShowing = paramBoolean;
  }
  
  protected abstract void setHeadsUpUser(int paramInt);
  
  protected void setLockScreenAllowRemoteInput(boolean paramBoolean)
  {
    this.mAllowLockscreenRemoteInput = paramBoolean;
  }
  
  public void setLockscreenPublicMode(boolean paramBoolean)
  {
    this.mLockscreenPublicMode = paramBoolean;
  }
  
  protected void setNotificationShown(StatusBarNotification paramStatusBarNotification)
  {
    setNotificationsShown(new String[] { paramStatusBarNotification.getKey() });
  }
  
  protected void setNotificationsShown(String[] paramArrayOfString)
  {
    try
    {
      this.mNotificationListener.setNotificationsShown(paramArrayOfString);
      return;
    }
    catch (RuntimeException paramArrayOfString)
    {
      Log.d("StatusBar", "failed setNotificationsShown: ", paramArrayOfString);
    }
  }
  
  protected void setShowLockscreenNotifications(boolean paramBoolean)
  {
    this.mShowLockscreenNotifications = paramBoolean;
  }
  
  protected void setZenMode(int paramInt)
  {
    if (!isDeviceProvisioned()) {
      return;
    }
    this.mZenMode = paramInt;
    updateNotifications();
  }
  
  public boolean shouldHideNotifications(int paramInt)
  {
    return (isLockscreenPublicMode()) && (!userAllowsNotificationsInPublic(paramInt));
  }
  
  public boolean shouldHideNotifications(String paramString)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (isLockscreenPublicMode())
    {
      bool1 = bool2;
      if (this.mNotificationData.getVisibilityOverride(paramString) == -1) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  protected boolean shouldPeek(NotificationData.Entry paramEntry)
  {
    return shouldPeek(paramEntry, paramEntry.notification);
  }
  
  protected boolean shouldPeek(NotificationData.Entry paramEntry, StatusBarNotification paramStatusBarNotification)
  {
    if ((!this.mUseHeadsUp) || (isDeviceInVrMode())) {
      return false;
    }
    if (this.mNotificationData.shouldFilterOut(paramStatusBarNotification))
    {
      if (Utils.DEBUG_ONEPLUS) {
        Log.d("StatusBar", "No peeking: filtered notification: " + paramStatusBarNotification.getKey());
      }
      return false;
    }
    boolean bool1;
    if (this.mPowerManager.isScreenOn()) {
      if (this.mStatusBarKeyguardViewManager.isShowing())
      {
        bool1 = this.mStatusBarKeyguardViewManager.isOccluded();
        if (!bool1) {}
      }
    }
    for (;;)
    {
      try
      {
        boolean bool2 = this.mDreamManager.isDreaming();
        if (!bool2) {
          continue;
        }
        bool1 = false;
      }
      catch (RemoteException localRemoteException)
      {
        Log.d("StatusBar", "failed to query dream manager", localRemoteException);
        continue;
        if (!this.mNotificationData.shouldSuppressScreenOn(paramStatusBarNotification.getKey())) {
          continue;
        }
        if (!Utils.DEBUG_ONEPLUS) {
          continue;
        }
        Log.d("StatusBar", "No peeking: suppressed by DND: " + paramStatusBarNotification.getKey());
        return false;
        if (!paramEntry.hasJustLaunchedFullScreenIntent()) {
          continue;
        }
        if (!Utils.DEBUG_ONEPLUS) {
          continue;
        }
        Log.d("StatusBar", "No peeking: recent fullscreen: " + paramStatusBarNotification.getKey());
        return false;
        if (!isSnoozedPackage(paramStatusBarNotification)) {
          continue;
        }
        if (!isInGameMode()) {
          continue;
        }
        if (paramStatusBarNotification.getNotification().fullScreenIntent != null) {
          continue;
        }
        if (!Utils.DEBUG_ONEPLUS) {
          continue;
        }
        Log.d("StatusBar", "No peeking: in game mode and without full-screen intent: snoozed package: " + paramStatusBarNotification.getKey());
        return false;
        if (!Utils.DEBUG_ONEPLUS) {
          continue;
        }
        Log.d("StatusBar", "No peeking: not in game mode: snoozed package: " + paramStatusBarNotification.getKey());
        return false;
        if (this.mNotificationData.getImportance(paramStatusBarNotification.getKey()) >= 4) {
          continue;
        }
        if (!Utils.DEBUG_ONEPLUS) {
          continue;
        }
        Log.d("StatusBar", "No peeking: unimportant notification: " + paramStatusBarNotification.getKey());
        return false;
        if ((!Utils.showHeadsUpInGameModePkg(paramStatusBarNotification.getPackageName())) && (isInGameMode()) && (!shouldPeekInGameMode(paramStatusBarNotification))) {
          continue;
        }
        if (paramStatusBarNotification.getNotification().fullScreenIntent == null) {
          break label589;
        }
        if (!this.mAccessibilityManager.isTouchExplorationEnabled()) {
          break label587;
        }
        if (!Utils.DEBUG_ONEPLUS) {
          continue;
        }
        Log.d("StatusBar", "No peeking: accessible fullscreen: " + paramStatusBarNotification.getKey());
        return false;
        if (!Utils.DEBUG_ONEPLUS) {
          continue;
        }
        Log.d("StatusBar", "No peeking: game mode status " + this.mGameModeStatus + ", game mode block notification " + this.mGameModeBlockNotification + " : " + paramStatusBarNotification.getKey());
        return false;
      }
      if (bool1) {
        continue;
      }
      if (Utils.DEBUG_ONEPLUS) {
        Log.d("StatusBar", "No peeking: not in use: " + paramStatusBarNotification.getKey());
      }
      return false;
      bool1 = true;
      break;
      bool1 = false;
      break;
      bool1 = true;
    }
    label587:
    return true;
    label589:
    return true;
  }
  
  public boolean shouldShowOnKeyguard(StatusBarNotification paramStatusBarNotification)
  {
    int i;
    if (paramStatusBarNotification.getId() == this.mIMEPickerID)
    {
      i = 1;
      if ((this.mShowLockscreenNotifications) && (!this.mNotificationData.isAmbient(paramStatusBarNotification.getKey()))) {
        break label41;
      }
    }
    label41:
    while (i != 0)
    {
      return false;
      i = 0;
      break;
    }
    return true;
  }
  
  public void showAssistDisclosure()
  {
    if (this.mAssistManager != null) {
      this.mAssistManager.showDisclosure();
    }
  }
  
  public void showRecentApps(boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = 1;
    this.mHandler.removeMessages(1019);
    H localH = this.mHandler;
    int i;
    if (paramBoolean1)
    {
      i = 1;
      if (!paramBoolean2) {
        break label49;
      }
    }
    for (;;)
    {
      localH.obtainMessage(1019, i, j).sendToTarget();
      return;
      i = 0;
      break;
      label49:
      j = 0;
    }
  }
  
  protected void showRecents(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mRecents != null)
    {
      sendCloseSystemWindows("recentapps");
      this.mRecents.showRecents(paramBoolean1, paramBoolean2);
    }
  }
  
  protected void showRecentsNextAffiliatedTask()
  {
    if (this.mRecents != null) {
      this.mRecents.showNextAffiliatedTask();
    }
  }
  
  protected void showRecentsPreviousAffiliatedTask()
  {
    if (this.mRecents != null) {
      this.mRecents.showPrevAffiliatedTask();
    }
  }
  
  public void start()
  {
    this.mWindowManager = ((WindowManager)this.mContext.getSystemService("window"));
    this.mWindowManagerService = WindowManagerGlobal.getWindowManagerService();
    this.mDisplay = this.mWindowManager.getDefaultDisplay();
    this.mDevicePolicyManager = ((DevicePolicyManager)this.mContext.getSystemService("device_policy"));
    this.mNotificationData = new NotificationData(this);
    this.mAccessibilityManager = ((AccessibilityManager)this.mContext.getSystemService("accessibility"));
    this.mDreamManager = IDreamManager.Stub.asInterface(ServiceManager.checkService("dreams"));
    this.mPowerManager = ((PowerManager)this.mContext.getSystemService("power"));
    this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("device_provisioned"), true, this.mSettingsObserver);
    this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("zen_mode"), false, this.mSettingsObserver);
    this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("three_Key_mode"), false, this.mSettingsObserver);
    this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("lock_screen_show_notifications"), false, this.mSettingsObserver, -1);
    if (ENABLE_LOCK_SCREEN_ALLOW_REMOTE_INPUT) {
      this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("lock_screen_allow_remote_input"), false, this.mSettingsObserver, -1);
    }
    this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("lock_screen_allow_private_notifications"), true, this.mLockscreenSettingsObserver, -1);
    this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("game_mode_status"), false, this.mGameModeObserver, -1);
    this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("game_mode_block_notification"), false, this.mGameModeObserver, -1);
    updateGameModeSetting();
    this.mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
    this.mRecents = ((RecentsComponent)getComponent(Recents.class));
    Object localObject1 = this.mContext.getResources().getConfiguration();
    this.mLocale = ((Configuration)localObject1).locale;
    this.mLayoutDirection = TextUtils.getLayoutDirectionFromLocale(this.mLocale);
    this.mFontScale = ((Configuration)localObject1).fontScale;
    this.mDensity = ((Configuration)localObject1).densityDpi;
    this.mUserManager = ((UserManager)this.mContext.getSystemService("user"));
    this.mKeyguardManager = ((KeyguardManager)this.mContext.getSystemService("keyguard"));
    this.mLockPatternUtils = new LockPatternUtils(this.mContext);
    this.mCommandQueue = new CommandQueue(this);
    localObject1 = new int[9];
    Object localObject2 = new ArrayList();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    Rect localRect1 = new Rect();
    Rect localRect2 = new Rect();
    try
    {
      this.mBarService.registerStatusBar(this.mCommandQueue, localArrayList1, localArrayList2, (int[])localObject1, (List)localObject2, localRect1, localRect2);
      createAndAddWindows();
      this.mSettingsObserver.onChange(false);
      disable(localObject1[0], localObject1[6], false);
      setSystemUiVisibility(localObject1[1], localObject1[7], localObject1[8], -1, localRect1, localRect2);
      int i;
      int j;
      if (localObject1[2] != 0)
      {
        bool = true;
        topAppWindowChanged(bool);
        localObject2 = (IBinder)((ArrayList)localObject2).get(0);
        i = localObject1[3];
        j = localObject1[4];
        if (localObject1[5] == 0) {
          break label659;
        }
      }
      label659:
      for (boolean bool = true;; bool = false)
      {
        setImeWindowStatus((IBinder)localObject2, i, j, bool);
        j = localArrayList1.size();
        i = 0;
        while (i < j)
        {
          setIcon((String)localArrayList1.get(i), (StatusBarIcon)localArrayList2.get(i));
          i += 1;
        }
        bool = false;
        break;
      }
      try
      {
        this.mNotificationListener.registerAsSystemService(this.mContext, new ComponentName(this.mContext.getPackageName(), getClass().getCanonicalName()), -1);
        this.mCurrentUserId = ActivityManager.getCurrentUser();
        setHeadsUpUser(this.mCurrentUserId);
        localObject1 = new IntentFilter();
        ((IntentFilter)localObject1).addAction("android.intent.action.USER_SWITCHED");
        ((IntentFilter)localObject1).addAction("android.intent.action.USER_ADDED");
        ((IntentFilter)localObject1).addAction("android.intent.action.USER_PRESENT");
        this.mContext.registerReceiver(this.mBroadcastReceiver, (IntentFilter)localObject1);
        localObject1 = new IntentFilter();
        ((IntentFilter)localObject1).addAction("com.android.systemui.statusbar.work_challenge_unlocked_notification_action");
        ((IntentFilter)localObject1).addAction("com.android.systemui.statusbar.banner_action_cancel");
        ((IntentFilter)localObject1).addAction("com.android.systemui.statusbar.banner_action_setup");
        this.mContext.registerReceiver(this.mBroadcastReceiver, (IntentFilter)localObject1, "com.android.systemui.permission.SELF", null);
        localObject1 = new IntentFilter();
        ((IntentFilter)localObject1).addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
        this.mContext.registerReceiverAsUser(this.mAllUsersReceiver, UserHandle.ALL, (IntentFilter)localObject1, null, null);
        updateCurrentProfilesCache();
        localObject1 = IVrManager.Stub.asInterface(ServiceManager.getService("vrmanager"));
      }
      catch (RemoteException localRemoteException1)
      {
        try
        {
          ((IVrManager)localObject1).registerListener(this.mVrStateCallbacks);
          this.mNonBlockablePkgs = new ArraySet();
          Collections.addAll(this.mNonBlockablePkgs, this.mContext.getResources().getStringArray(2131427608));
          this.mIMEPickerID = 17040442;
          return;
          localRemoteException1 = localRemoteException1;
          Log.e("StatusBar", "Unable to register notification listener", localRemoteException1);
        }
        catch (RemoteException localRemoteException2)
        {
          for (;;)
          {
            Slog.e("StatusBar", "Failed to register VR mode state listener: " + localRemoteException2);
          }
        }
      }
    }
    catch (RemoteException localRemoteException3)
    {
      for (;;) {}
    }
  }
  
  public void startAssist(Bundle paramBundle)
  {
    if (this.mAssistManager != null) {
      this.mAssistManager.startAssist(paramBundle);
    }
  }
  
  public void startPendingIntentDismissingKeyguard(final PendingIntent paramPendingIntent)
  {
    if (!isDeviceProvisioned()) {
      return;
    }
    final boolean bool2 = this.mStatusBarKeyguardViewManager.isShowing();
    if (paramPendingIntent.isActivity()) {}
    for (final boolean bool1 = PreviewInflater.wouldLaunchResolverActivity(this.mContext, paramPendingIntent.getIntent(), this.mCurrentUserId);; bool1 = false)
    {
      dismissKeyguardThenExecute(new KeyguardHostView.OnDismissAction()
      {
        public boolean onDismiss()
        {
          new Thread()
          {
            public void run()
            {
              try
              {
                if ((!this.val$keyguardShowing) || (this.val$afterKeyguardGone)) {
                  ActivityManagerNative.getDefault().resumeAppSwitches();
                }
              }
              catch (RemoteException localRemoteException)
              {
                try
                {
                  for (;;)
                  {
                    this.val$intent.send(null, 0, null, null, null, null, BaseStatusBar.this.getActivityOptions());
                    if (this.val$intent.isActivity())
                    {
                      BaseStatusBar.this.mAssistManager.hideAssist();
                      BaseStatusBar localBaseStatusBar = BaseStatusBar.this;
                      if (!this.val$keyguardShowing) {
                        break label147;
                      }
                      if (!this.val$afterKeyguardGone) {
                        break;
                      }
                      bool = false;
                      localBaseStatusBar.overrideActivityPendingAppTransition(bool);
                    }
                    return;
                    ActivityManagerNative.getDefault().keyguardWaitingForActivityDrawn();
                    continue;
                    localRemoteException = localRemoteException;
                  }
                }
                catch (PendingIntent.CanceledException localCanceledException)
                {
                  for (;;)
                  {
                    Log.w("StatusBar", "Sending intent failed: " + localCanceledException);
                    continue;
                    boolean bool = true;
                    continue;
                    label147:
                    bool = false;
                  }
                }
              }
            }
          }.start();
          BaseStatusBar.this.animateCollapsePanels(2, true, true);
          BaseStatusBar.this.visibilityChanged(false);
          return true;
        }
      }, bool1);
      return;
    }
  }
  
  protected boolean startWorkChallengeIfNecessary(int paramInt, IntentSender paramIntentSender, String paramString)
  {
    Intent localIntent1 = this.mKeyguardManager.createConfirmDeviceCredentialIntent(null, null, paramInt);
    if (localIntent1 == null) {
      return false;
    }
    Intent localIntent2 = new Intent("com.android.systemui.statusbar.work_challenge_unlocked_notification_action");
    localIntent2.putExtra("android.intent.extra.INTENT", paramIntentSender);
    localIntent2.putExtra("android.intent.extra.INDEX", paramString);
    localIntent2.setPackage(this.mContext.getPackageName());
    localIntent1.putExtra("android.intent.extra.INTENT", PendingIntent.getBroadcast(this.mContext, 0, localIntent2, 1409286144).getIntentSender());
    try
    {
      ActivityManagerNative.getDefault().startConfirmDeviceCredentialIntent(localIntent1);
      return true;
    }
    catch (RemoteException paramIntentSender)
    {
      for (;;) {}
    }
  }
  
  protected void toggleKeyboardShortcuts(int paramInt)
  {
    KeyboardShortcuts.toggle(this.mContext, paramInt);
  }
  
  public void toggleKeyboardShortcutsMenu(int paramInt)
  {
    this.mHandler.removeMessages(1026);
    this.mHandler.obtainMessage(1026, paramInt, 0).sendToTarget();
  }
  
  public void toggleRecentApps()
  {
    toggleRecents();
  }
  
  protected void toggleRecents()
  {
    if (this.mRecents != null) {
      this.mRecents.toggleRecents(this.mDisplay);
    }
  }
  
  public void toggleSplitScreen()
  {
    toggleSplitScreenMode(-1, -1);
  }
  
  protected abstract void toggleSplitScreenMode(int paramInt1, int paramInt2);
  
  protected abstract void updateHeadsUp(String paramString, NotificationData.Entry paramEntry, boolean paramBoolean1, boolean paramBoolean2);
  
  /* Error */
  public void updateNotification(StatusBarNotification paramStatusBarNotification, NotificationListenerService.RankingMap paramRankingMap)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 523	android/service/notification/StatusBarNotification:getKey	()Ljava/lang/String;
    //   4: astore 9
    //   6: aload_0
    //   7: getfield 520	com/android/systemui/statusbar/BaseStatusBar:mNotificationData	Lcom/android/systemui/statusbar/NotificationData;
    //   10: aload 9
    //   12: invokevirtual 1527	com/android/systemui/statusbar/NotificationData:get	(Ljava/lang/String;)Lcom/android/systemui/statusbar/NotificationData$Entry;
    //   15: astore 10
    //   17: aload 10
    //   19: ifnonnull +4 -> 23
    //   22: return
    //   23: aload_0
    //   24: getfield 329	com/android/systemui/statusbar/BaseStatusBar:mHeadsUpEntriesToRemoveOnSwitch	Landroid/util/ArraySet;
    //   27: aload 10
    //   29: invokevirtual 1524	android/util/ArraySet:remove	(Ljava/lang/Object;)Z
    //   32: pop
    //   33: aload_0
    //   34: getfield 331	com/android/systemui/statusbar/BaseStatusBar:mRemoteInputEntriesToRemoveOnCollapse	Landroid/util/ArraySet;
    //   37: aload 10
    //   39: invokevirtual 1524	android/util/ArraySet:remove	(Ljava/lang/Object;)Z
    //   42: pop
    //   43: aload_1
    //   44: invokevirtual 662	android/service/notification/StatusBarNotification:getNotification	()Landroid/app/Notification;
    //   47: astore 11
    //   49: aload_0
    //   50: getfield 520	com/android/systemui/statusbar/BaseStatusBar:mNotificationData	Lcom/android/systemui/statusbar/NotificationData;
    //   53: aload_2
    //   54: invokevirtual 1973	com/android/systemui/statusbar/NotificationData:updateRanking	(Landroid/service/notification/NotificationListenerService$RankingMap;)V
    //   57: aload 10
    //   59: aload_0
    //   60: getfield 427	com/android/systemui/statusbar/BaseStatusBar:mContext	Landroid/content/Context;
    //   63: aload_1
    //   64: invokevirtual 662	android/service/notification/StatusBarNotification:getNotification	()Landroid/app/Notification;
    //   67: invokevirtual 1111	com/android/systemui/statusbar/NotificationData$Entry:cacheContentViews	(Landroid/content/Context;Landroid/app/Notification;)Z
    //   70: istore 5
    //   72: aload_0
    //   73: aload 10
    //   75: aload_1
    //   76: invokevirtual 1584	com/android/systemui/statusbar/BaseStatusBar:shouldPeek	(Lcom/android/systemui/statusbar/NotificationData$Entry;Landroid/service/notification/StatusBarNotification;)Z
    //   79: istore 6
    //   81: aload_0
    //   82: aload 10
    //   84: aload 11
    //   86: invokespecial 1975	com/android/systemui/statusbar/BaseStatusBar:alertAgain	(Lcom/android/systemui/statusbar/NotificationData$Entry;Landroid/app/Notification;)Z
    //   89: istore 7
    //   91: aload_1
    //   92: invokevirtual 662	android/service/notification/StatusBarNotification:getNotification	()Landroid/app/Notification;
    //   95: invokevirtual 1978	android/app/Notification:ShowChronometerOnStatusBar	()Z
    //   98: istore 8
    //   100: aload 10
    //   102: getfield 563	com/android/systemui/statusbar/NotificationData$Entry:notification	Landroid/service/notification/StatusBarNotification;
    //   105: astore_2
    //   106: aload 10
    //   108: aload_1
    //   109: putfield 563	com/android/systemui/statusbar/NotificationData$Entry:notification	Landroid/service/notification/StatusBarNotification;
    //   112: aload_0
    //   113: getfield 313	com/android/systemui/statusbar/BaseStatusBar:mGroupManager	Lcom/android/systemui/statusbar/phone/NotificationGroupManager;
    //   116: aload 10
    //   118: aload_2
    //   119: invokevirtual 1981	com/android/systemui/statusbar/phone/NotificationGroupManager:onEntryUpdated	(Lcom/android/systemui/statusbar/NotificationData$Entry;Landroid/service/notification/StatusBarNotification;)V
    //   122: iconst_0
    //   123: istore 4
    //   125: iload 4
    //   127: istore_3
    //   128: iload 5
    //   130: ifeq +126 -> 256
    //   133: aload 10
    //   135: getfield 858	com/android/systemui/statusbar/NotificationData$Entry:icon	Lcom/android/systemui/statusbar/StatusBarIconView;
    //   138: ifnull +109 -> 247
    //   141: new 947	com/android/internal/statusbar/StatusBarIcon
    //   144: dup
    //   145: aload_1
    //   146: invokevirtual 433	android/service/notification/StatusBarNotification:getUser	()Landroid/os/UserHandle;
    //   149: aload_1
    //   150: invokevirtual 447	android/service/notification/StatusBarNotification:getPackageName	()Ljava/lang/String;
    //   153: aload 11
    //   155: invokevirtual 939	android/app/Notification:getSmallIcon	()Landroid/graphics/drawable/Icon;
    //   158: aload 11
    //   160: getfield 950	android/app/Notification:iconLevel	I
    //   163: aload 11
    //   165: getfield 953	android/app/Notification:number	I
    //   168: aload_0
    //   169: getfield 427	com/android/systemui/statusbar/BaseStatusBar:mContext	Landroid/content/Context;
    //   172: aload 11
    //   174: invokestatic 957	com/android/systemui/statusbar/StatusBarIconView:contentDescForNotification	(Landroid/content/Context;Landroid/app/Notification;)Ljava/lang/String;
    //   177: invokespecial 960	com/android/internal/statusbar/StatusBarIcon:<init>	(Landroid/os/UserHandle;Ljava/lang/String;Landroid/graphics/drawable/Icon;IILjava/lang/CharSequence;)V
    //   180: astore_2
    //   181: aload 10
    //   183: getfield 858	com/android/systemui/statusbar/NotificationData$Entry:icon	Lcom/android/systemui/statusbar/StatusBarIconView;
    //   186: aload 11
    //   188: invokevirtual 1984	com/android/systemui/statusbar/StatusBarIconView:setNotification	(Landroid/app/Notification;)V
    //   191: aload 10
    //   193: getfield 858	com/android/systemui/statusbar/NotificationData$Entry:icon	Lcom/android/systemui/statusbar/StatusBarIconView;
    //   196: aload_2
    //   197: invokevirtual 964	com/android/systemui/statusbar/StatusBarIconView:set	(Lcom/android/internal/statusbar/StatusBarIcon;)Z
    //   200: ifne +47 -> 247
    //   203: aload_0
    //   204: aload_1
    //   205: new 905	java/lang/StringBuilder
    //   208: dup
    //   209: invokespecial 906	java/lang/StringBuilder:<init>	()V
    //   212: ldc_w 1986
    //   215: invokevirtual 910	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   218: aload_2
    //   219: invokevirtual 969	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   222: invokevirtual 922	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   225: invokevirtual 945	com/android/systemui/statusbar/BaseStatusBar:handleNotificationError	(Landroid/service/notification/StatusBarNotification;Ljava/lang/String;)V
    //   228: return
    //   229: astore_2
    //   230: ldc_w 1113
    //   233: ldc_w 1123
    //   236: aload_2
    //   237: invokestatic 1127	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   240: pop
    //   241: iconst_0
    //   242: istore 5
    //   244: goto -172 -> 72
    //   247: aload_0
    //   248: aload 10
    //   250: aload_1
    //   251: invokespecial 1988	com/android/systemui/statusbar/BaseStatusBar:updateNotificationViews	(Lcom/android/systemui/statusbar/NotificationData$Entry;Landroid/service/notification/StatusBarNotification;)V
    //   254: iconst_1
    //   255: istore_3
    //   256: iload_3
    //   257: ifne +101 -> 358
    //   260: new 947	com/android/internal/statusbar/StatusBarIcon
    //   263: dup
    //   264: aload_1
    //   265: invokevirtual 433	android/service/notification/StatusBarNotification:getUser	()Landroid/os/UserHandle;
    //   268: aload_1
    //   269: invokevirtual 447	android/service/notification/StatusBarNotification:getPackageName	()Ljava/lang/String;
    //   272: aload 11
    //   274: invokevirtual 939	android/app/Notification:getSmallIcon	()Landroid/graphics/drawable/Icon;
    //   277: aload 11
    //   279: getfield 950	android/app/Notification:iconLevel	I
    //   282: aload 11
    //   284: getfield 953	android/app/Notification:number	I
    //   287: aload_0
    //   288: getfield 427	com/android/systemui/statusbar/BaseStatusBar:mContext	Landroid/content/Context;
    //   291: aload 11
    //   293: invokestatic 957	com/android/systemui/statusbar/StatusBarIconView:contentDescForNotification	(Landroid/content/Context;Landroid/app/Notification;)Ljava/lang/String;
    //   296: invokespecial 960	com/android/internal/statusbar/StatusBarIcon:<init>	(Landroid/os/UserHandle;Ljava/lang/String;Landroid/graphics/drawable/Icon;IILjava/lang/CharSequence;)V
    //   299: astore_2
    //   300: aload 10
    //   302: getfield 858	com/android/systemui/statusbar/NotificationData$Entry:icon	Lcom/android/systemui/statusbar/StatusBarIconView;
    //   305: aload 11
    //   307: invokevirtual 1984	com/android/systemui/statusbar/StatusBarIconView:setNotification	(Landroid/app/Notification;)V
    //   310: aload 10
    //   312: getfield 858	com/android/systemui/statusbar/NotificationData$Entry:icon	Lcom/android/systemui/statusbar/StatusBarIconView;
    //   315: aload_2
    //   316: invokevirtual 964	com/android/systemui/statusbar/StatusBarIconView:set	(Lcom/android/internal/statusbar/StatusBarIcon;)Z
    //   319: pop
    //   320: aload_0
    //   321: aload 10
    //   323: aload_0
    //   324: getfield 978	com/android/systemui/statusbar/BaseStatusBar:mStackScroller	Lcom/android/systemui/statusbar/stack/NotificationStackScrollLayout;
    //   327: invokevirtual 982	com/android/systemui/statusbar/BaseStatusBar:inflateViews	(Lcom/android/systemui/statusbar/NotificationData$Entry;Landroid/view/ViewGroup;)Z
    //   330: ifne +28 -> 358
    //   333: aload_0
    //   334: aload_1
    //   335: new 905	java/lang/StringBuilder
    //   338: dup
    //   339: invokespecial 906	java/lang/StringBuilder:<init>	()V
    //   342: ldc_w 1990
    //   345: invokevirtual 910	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   348: aload_1
    //   349: invokevirtual 969	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   352: invokevirtual 922	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   355: invokevirtual 945	com/android/systemui/statusbar/BaseStatusBar:handleNotificationError	(Landroid/service/notification/StatusBarNotification;Ljava/lang/String;)V
    //   358: iload 8
    //   360: ifeq +90 -> 450
    //   363: aload 10
    //   365: aload_0
    //   366: getfield 427	com/android/systemui/statusbar/BaseStatusBar:mContext	Landroid/content/Context;
    //   369: invokevirtual 1993	com/android/systemui/statusbar/NotificationData$Entry:createChronometer	(Landroid/content/Context;)V
    //   372: aload_0
    //   373: aload 9
    //   375: aload 10
    //   377: iload 6
    //   379: iload 7
    //   381: invokevirtual 1995	com/android/systemui/statusbar/BaseStatusBar:updateHeadsUp	(Ljava/lang/String;Lcom/android/systemui/statusbar/NotificationData$Entry;ZZ)V
    //   384: aload_0
    //   385: invokevirtual 834	com/android/systemui/statusbar/BaseStatusBar:updateNotifications	()V
    //   388: aload_1
    //   389: invokevirtual 1998	android/service/notification/StatusBarNotification:isClearable	()Z
    //   392: ifne +15 -> 407
    //   395: aload_0
    //   396: getfield 978	com/android/systemui/statusbar/BaseStatusBar:mStackScroller	Lcom/android/systemui/statusbar/stack/NotificationStackScrollLayout;
    //   399: aload 10
    //   401: getfield 813	com/android/systemui/statusbar/NotificationData$Entry:row	Lcom/android/systemui/statusbar/ExpandableNotificationRow;
    //   404: invokevirtual 2001	com/android/systemui/statusbar/stack/NotificationStackScrollLayout:snapViewIfNeeded	(Lcom/android/systemui/statusbar/ExpandableNotificationRow;)V
    //   407: aload_0
    //   408: invokevirtual 2003	com/android/systemui/statusbar/BaseStatusBar:setAreThereNotifications	()V
    //   411: return
    //   412: astore_2
    //   413: ldc_w 1113
    //   416: new 905	java/lang/StringBuilder
    //   419: dup
    //   420: invokespecial 906	java/lang/StringBuilder:<init>	()V
    //   423: ldc_w 2005
    //   426: invokevirtual 910	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   429: aload_1
    //   430: invokevirtual 447	android/service/notification/StatusBarNotification:getPackageName	()Ljava/lang/String;
    //   433: invokevirtual 910	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   436: invokevirtual 922	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   439: aload_2
    //   440: invokestatic 2006	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   443: pop
    //   444: iload 4
    //   446: istore_3
    //   447: goto -191 -> 256
    //   450: aload 10
    //   452: aconst_null
    //   453: putfield 2010	com/android/systemui/statusbar/NotificationData$Entry:statusBarChronometer	Landroid/widget/Chronometer;
    //   456: aload 10
    //   458: aconst_null
    //   459: putfield 2013	com/android/systemui/statusbar/NotificationData$Entry:keyguardChronometer	Landroid/widget/Chronometer;
    //   462: goto -90 -> 372
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	465	0	this	BaseStatusBar
    //   0	465	1	paramStatusBarNotification	StatusBarNotification
    //   0	465	2	paramRankingMap	NotificationListenerService.RankingMap
    //   127	320	3	i	int
    //   123	322	4	j	int
    //   70	173	5	bool1	boolean
    //   79	299	6	bool2	boolean
    //   89	291	7	bool3	boolean
    //   98	261	8	bool4	boolean
    //   4	370	9	str	String
    //   15	442	10	localEntry	NotificationData.Entry
    //   47	259	11	localNotification	Notification
    // Exception table:
    //   from	to	target	type
    //   57	72	229	java/lang/RuntimeException
    //   133	228	412	java/lang/RuntimeException
    //   247	254	412	java/lang/RuntimeException
  }
  
  protected abstract void updateNotificationRanking(NotificationListenerService.RankingMap paramRankingMap);
  
  protected abstract void updateNotifications();
  
  protected void updatePublicContentView(NotificationData.Entry paramEntry, StatusBarNotification paramStatusBarNotification, int paramInt)
  {
    RemoteViews localRemoteViews = paramEntry.cachedPublicContentView;
    View localView = paramEntry.getPublicContentView();
    if ((paramEntry.autoRedacted) && (localRemoteViews != null) && (localView != null)) {
      if (!adminAllowsUnredactedNotifications(paramEntry.notification.getUserId())) {
        break label107;
      }
    }
    String str;
    label107:
    for (;;)
    {
      str = this.mContext.getResources().getQuantityString(84672512, paramInt, new Object[] { Integer.valueOf(paramInt) });
      TextView localTextView = (TextView)localView.findViewById(16908310);
      if ((localTextView != null) && (!localTextView.getText().toString().equals(str))) {
        break;
      }
      return;
    }
    localRemoteViews.setTextViewText(16908310, str);
    localRemoteViews.reapply(paramStatusBarNotification.getPackageContext(this.mContext), localView, this.mOnClickHandler);
    paramEntry.row.onNotificationUpdated(paramEntry);
  }
  
  protected void updateRowStates()
  {
    this.mKeyguardIconOverflowContainer.getIconsView().removeAllViews();
    Object localObject1 = this.mNotificationData.getActiveNotifications();
    int i2 = ((ArrayList)localObject1).size();
    int i1 = 0;
    int k;
    int m;
    int n;
    label56:
    Object localObject2;
    boolean bool2;
    label113:
    label159:
    int j;
    if (this.mState == 1)
    {
      k = 1;
      m = 0;
      if (k != 0) {
        m = getMaxKeyguardNotifications(true);
      }
      n = 0;
      if (n >= i2) {
        break label478;
      }
      localObject2 = (NotificationData.Entry)((ArrayList)localObject1).get(n);
      bool2 = this.mGroupManager.isChildInGroupWithSummary(((NotificationData.Entry)localObject2).notification);
      if (!this.mIsSecure) {
        break label284;
      }
      bool1 = this.mNotificationData.isLock(((NotificationData.Entry)localObject2).notification.getKey());
      if ((k == 0) && (!bool1)) {
        break label290;
      }
      ((NotificationData.Entry)localObject2).row.setOnKeyguard(true);
      if (!this.mGroupManager.isSummaryOfSuppressedGroup(((NotificationData.Entry)localObject2).notification)) {
        break label340;
      }
      if (!((NotificationData.Entry)localObject2).row.isRemoved()) {
        break label335;
      }
      i = 0;
      if (!bool2) {
        break label350;
      }
      if (this.mGroupManager.getGroupSummary(((NotificationData.Entry)localObject2).notification).getVisibility() != 0) {
        break label345;
      }
      j = 1;
      label184:
      bool1 = shouldShowOnKeyguard(((NotificationData.Entry)localObject2).notification);
      if ((i != 0) || ((isLockscreenPublicMode()) && (!this.mShowLockscreenNotifications))) {
        break label367;
      }
      if ((k != 0) && (j == 0)) {
        break label355;
      }
      label221:
      if (((NotificationData.Entry)localObject2).row.getVisibility() != 8) {
        break label425;
      }
    }
    label284:
    label290:
    Object localObject3;
    label335:
    label340:
    label345:
    label350:
    label355:
    label367:
    label425:
    for (int i = 1;; i = 0)
    {
      ((NotificationData.Entry)localObject2).row.setVisibility(0);
      j = i1;
      if (!bool2)
      {
        if (!((NotificationData.Entry)localObject2).row.isRemoved()) {
          break label430;
        }
        j = i1;
      }
      for (;;)
      {
        n += 1;
        i1 = j;
        break label56;
        k = 0;
        break;
        bool1 = false;
        break label113;
        ((NotificationData.Entry)localObject2).row.setOnKeyguard(false);
        localObject3 = ((NotificationData.Entry)localObject2).row;
        if ((i1 != 0) || (bool2)) {}
        for (bool1 = false;; bool1 = true)
        {
          ((ExpandableNotificationRow)localObject3).setSystemExpanded(bool1);
          break;
        }
        i = 1;
        break label159;
        i = 0;
        break label159;
        j = 0;
        break label184;
        j = 0;
        break label184;
        if ((i1 < m) && (bool1)) {
          break label221;
        }
        ((NotificationData.Entry)localObject2).row.setVisibility(8);
        j = i1;
        if (k != 0)
        {
          j = i1;
          if (bool1)
          {
            j = i1;
            if (!bool2)
            {
              j = i1;
              if (i == 0)
              {
                this.mKeyguardIconOverflowContainer.getIconsView().addNotification((NotificationData.Entry)localObject2);
                j = i1;
              }
            }
          }
        }
      }
    }
    label430:
    if (i != 0)
    {
      localObject3 = this.mStackScroller;
      localObject2 = ((NotificationData.Entry)localObject2).row;
      if (!bool1) {
        break label472;
      }
    }
    label472:
    for (boolean bool1 = false;; bool1 = true)
    {
      ((NotificationStackScrollLayout)localObject3).generateAddAnimation((View)localObject2, bool1);
      j = i1 + 1;
      break;
    }
    label478:
    localObject1 = this.mStackScroller;
    if (k != 0) {
      if (this.mKeyguardIconOverflowContainer.getIconsView().getChildCount() > 0) {
        bool1 = true;
      }
    }
    for (;;)
    {
      ((NotificationStackScrollLayout)localObject1).updateOverflowContainerVisibility(bool1);
      this.mStackScroller.changeViewPosition(this.mDismissView, this.mStackScroller.getChildCount() - 1);
      this.mStackScroller.changeViewPosition(this.mEmptyShadeView, this.mStackScroller.getChildCount() - 2);
      this.mStackScroller.changeViewPosition(this.mKeyguardIconOverflowContainer, this.mStackScroller.getChildCount() - 3);
      return;
      bool1 = false;
      continue;
      bool1 = false;
    }
  }
  
  protected void updateVisibleToUser()
  {
    boolean bool2 = this.mVisibleToUser;
    if (this.mVisible) {}
    for (boolean bool1 = this.mDeviceInteractive;; bool1 = false)
    {
      this.mVisibleToUser = bool1;
      if (bool2 != this.mVisibleToUser) {
        handleVisibleToUserChanged(this.mVisibleToUser);
      }
      return;
    }
  }
  
  public boolean userAllowsNotificationsInPublic(int paramInt)
  {
    if (paramInt == -1) {
      return true;
    }
    if (this.mUsersAllowingNotifications.indexOfKey(paramInt) < 0)
    {
      if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_show_notifications", 0, paramInt) != 0) {}
      for (boolean bool = true;; bool = false)
      {
        this.mUsersAllowingNotifications.append(paramInt, bool);
        return bool;
      }
    }
    return this.mUsersAllowingNotifications.get(paramInt);
  }
  
  public boolean userAllowsPrivateNotificationsInPublic(int paramInt)
  {
    if (paramInt == -1) {
      return true;
    }
    if (this.mUsersAllowingPrivateNotifications.indexOfKey(paramInt) < 0)
    {
      int i;
      boolean bool;
      if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_allow_private_notifications", 0, paramInt) != 0)
      {
        i = 1;
        bool = adminAllowsUnredactedNotifications(paramInt);
        if (i == 0) {
          break label64;
        }
      }
      for (;;)
      {
        this.mUsersAllowingPrivateNotifications.append(paramInt, bool);
        return bool;
        i = 0;
        break;
        label64:
        bool = false;
      }
    }
    return this.mUsersAllowingPrivateNotifications.get(paramInt);
  }
  
  public void userSwitched(int paramInt)
  {
    setHeadsUpUser(paramInt);
  }
  
  protected void visibilityChanged(boolean paramBoolean)
  {
    if (this.mVisible != paramBoolean)
    {
      this.mVisible = paramBoolean;
      if (!paramBoolean) {
        dismissPopups();
      }
    }
    updateVisibleToUser();
  }
  
  protected void workAroundBadLayerDrawableOpacity(View paramView) {}
  
  protected class H
    extends Handler
  {
    protected H() {}
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool3 = true;
      boolean bool2 = true;
      BaseStatusBar localBaseStatusBar;
      boolean bool1;
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1019: 
        localBaseStatusBar = BaseStatusBar.this;
        if (paramMessage.arg1 > 0)
        {
          bool1 = true;
          if (paramMessage.arg2 == 0) {
            break label96;
          }
        }
        for (;;)
        {
          localBaseStatusBar.showRecents(bool1, bool2);
          return;
          bool1 = false;
          break;
          bool2 = false;
        }
      case 1020: 
        localBaseStatusBar = BaseStatusBar.this;
        if (paramMessage.arg1 > 0)
        {
          bool1 = true;
          if (paramMessage.arg2 <= 0) {
            break label139;
          }
        }
        for (bool2 = bool3;; bool2 = false)
        {
          localBaseStatusBar.hideRecents(bool1, bool2);
          return;
          bool1 = false;
          break;
        }
      case 1021: 
        BaseStatusBar.this.toggleRecents();
        return;
      case 1022: 
        BaseStatusBar.this.preloadRecents();
        return;
      case 1023: 
        BaseStatusBar.this.cancelPreloadingRecents();
        return;
      case 1024: 
        BaseStatusBar.this.showRecentsNextAffiliatedTask();
        return;
      case 1025: 
        BaseStatusBar.this.showRecentsPreviousAffiliatedTask();
        return;
      case 1026: 
        label96:
        label139:
        BaseStatusBar.this.toggleKeyboardShortcuts(paramMessage.arg1);
        return;
      }
      BaseStatusBar.this.dismissKeyboardShortcuts();
    }
  }
  
  private final class NotificationClicker
    implements View.OnClickListener
  {
    private NotificationClicker() {}
    
    private boolean shouldAutoCancel(StatusBarNotification paramStatusBarNotification)
    {
      int i = paramStatusBarNotification.getNotification().flags;
      if ((i & 0x10) != 16) {
        return false;
      }
      return (i & 0x40) == 0;
    }
    
    public void onClick(final View paramView)
    {
      if (!(paramView instanceof ExpandableNotificationRow))
      {
        Log.e("StatusBar", "NotificationClicker called on a view that is not a notification row.");
        return;
      }
      final ExpandableNotificationRow localExpandableNotificationRow = (ExpandableNotificationRow)paramView;
      final StatusBarNotification localStatusBarNotification = localExpandableNotificationRow.getStatusBarNotification();
      if (localStatusBarNotification == null)
      {
        Log.e("StatusBar", "NotificationClicker called on an unclickable notification,");
        return;
      }
      if ((localExpandableNotificationRow.getSettingsRow() != null) && (localExpandableNotificationRow.getSettingsRow().isVisible()))
      {
        localExpandableNotificationRow.animateTranslateNotification(0.0F);
        return;
      }
      paramView = localStatusBarNotification.getNotification();
      final String str;
      final boolean bool2;
      if (paramView.contentIntent != null)
      {
        paramView = paramView.contentIntent;
        str = localStatusBarNotification.getKey();
        localExpandableNotificationRow.setJustClicked(true);
        DejankUtils.postAfterTraversal(new Runnable()
        {
          public void run()
          {
            localExpandableNotificationRow.setJustClicked(false);
          }
        });
        Log.d("StatusBar", "Clicked on content of " + str + " , " + paramView.toString() + " , " + paramView.getIntent());
        bool2 = BaseStatusBar.this.mStatusBarKeyguardViewManager.isShowing();
        if (!paramView.isActivity()) {
          break label237;
        }
      }
      label237:
      for (final boolean bool1 = PreviewInflater.wouldLaunchResolverActivity(BaseStatusBar.this.mContext, paramView.getIntent(), BaseStatusBar.this.mCurrentUserId);; bool1 = false)
      {
        BaseStatusBar.this.dismissKeyguardThenExecute(new KeyguardHostView.OnDismissAction()
        {
          public boolean onDismiss()
          {
            if ((BaseStatusBar.this.mHeadsUpManager != null) && (BaseStatusBar.this.mHeadsUpManager.isHeadsUp(str)))
            {
              if (BaseStatusBar.this.isPanelFullyCollapsed()) {
                HeadsUpManager.setIsClickedNotification(localExpandableNotificationRow, true);
              }
              BaseStatusBar.this.mHeadsUpManager.releaseImmediately(str);
            }
            Object localObject2 = null;
            final Object localObject1 = localObject2;
            if (BaseStatusBar.NotificationClicker.-wrap0(BaseStatusBar.NotificationClicker.this, localStatusBarNotification))
            {
              localObject1 = localObject2;
              if (BaseStatusBar.this.mGroupManager.isOnlyChildInGroup(localStatusBarNotification))
              {
                StatusBarNotification localStatusBarNotification = BaseStatusBar.this.mGroupManager.getLogicalGroupSummary(localStatusBarNotification).getStatusBarNotification();
                localObject1 = localObject2;
                if (BaseStatusBar.NotificationClicker.-wrap0(BaseStatusBar.NotificationClicker.this, localStatusBarNotification)) {
                  localObject1 = localStatusBarNotification;
                }
              }
            }
            new Thread()
            {
              public void run()
              {
                for (;;)
                {
                  try
                  {
                    if ((this.val$keyguardShowing) && (!this.val$afterKeyguardGone)) {
                      continue;
                    }
                    ActivityManagerNative.getDefault().resumeAppSwitches();
                  }
                  catch (RemoteException localRemoteException1)
                  {
                    int i;
                    boolean bool1;
                    continue;
                    try
                    {
                      Log.d("StatusBar", "send intent, " + this.val$intent.toString() + " , " + this.val$intent.getIntent());
                      this.val$intent.send(null, 0, null, null, null, null, BaseStatusBar.this.getActivityOptions());
                      if (!this.val$intent.isActivity()) {
                        continue;
                      }
                      BaseStatusBar.this.mAssistManager.hideAssist();
                      localBaseStatusBar = BaseStatusBar.this;
                      if (!this.val$keyguardShowing) {
                        continue;
                      }
                      if (!this.val$afterKeyguardGone) {
                        continue;
                      }
                      bool1 = false;
                      localBaseStatusBar.overrideActivityPendingAppTransition(bool1);
                    }
                    catch (PendingIntent.CanceledException localCanceledException)
                    {
                      try
                      {
                        BaseStatusBar.this.mBarService.onNotificationClick(this.val$notificationKey);
                        if (localObject1 == null) {
                          continue;
                        }
                        BaseStatusBar.this.mHandler.post(new Runnable()
                        {
                          public void run()
                          {
                            Runnable local1 = new Runnable()
                            {
                              public void run()
                              {
                                BaseStatusBar.this.performRemoveNotification(this.val$parentToCancelFinal, true);
                              }
                            };
                            if (BaseStatusBar.this.isCollapsing())
                            {
                              BaseStatusBar.this.addPostCollapseAction(local1);
                              return;
                            }
                            local1.run();
                          }
                        });
                        return;
                        localCanceledException = localCanceledException;
                        Log.w("StatusBar", "Sending contentIntent failed: " + localCanceledException);
                        continue;
                        bool1 = true;
                        continue;
                        bool1 = false;
                      }
                      catch (RemoteException localRemoteException2)
                      {
                        continue;
                      }
                    }
                  }
                  if (this.val$intent == null) {
                    continue;
                  }
                  if (!this.val$intent.isActivity()) {
                    continue;
                  }
                  i = this.val$intent.getCreatorUserHandle().getIdentifier();
                  if ((!BaseStatusBar.-get2(BaseStatusBar.this).isSeparateProfileChallengeEnabled(i)) || (!BaseStatusBar.-get1(BaseStatusBar.this).isDeviceLocked(i))) {
                    continue;
                  }
                  bool1 = false;
                  try
                  {
                    boolean bool2 = ActivityManagerNative.getDefault().canBypassWorkChallenge(this.val$intent);
                    bool1 = bool2;
                  }
                  catch (RemoteException localRemoteException3)
                  {
                    BaseStatusBar localBaseStatusBar;
                    continue;
                  }
                  if ((bool1) || (!BaseStatusBar.this.startWorkChallengeIfNecessary(i, this.val$intent.getIntentSender(), this.val$notificationKey))) {
                    continue;
                  }
                  return;
                  ActivityManagerNative.getDefault().keyguardWaitingForActivityDrawn();
                }
              }
            }.start();
            BaseStatusBar.this.animateCollapsePanels(2, true, true);
            BaseStatusBar.this.visibilityChanged(false);
            return true;
          }
        }, bool1);
        return;
        paramView = paramView.fullScreenIntent;
        break;
      }
    }
    
    public void register(ExpandableNotificationRow paramExpandableNotificationRow, StatusBarNotification paramStatusBarNotification)
    {
      paramStatusBarNotification = paramStatusBarNotification.getNotification();
      if ((paramStatusBarNotification.contentIntent != null) || (paramStatusBarNotification.fullScreenIntent != null))
      {
        paramExpandableNotificationRow.setOnClickListener(this);
        return;
      }
      paramExpandableNotificationRow.setOnClickListener(null);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\BaseStatusBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */