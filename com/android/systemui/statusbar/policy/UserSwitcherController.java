package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.Dialog;
import android.app.IActivityManager;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.settingslib.drawable.UserIcons;
import com.android.systemui.GuestResumeSessionReceiver;
import com.android.systemui.SystemUI;
import com.android.systemui.SystemUISecondaryUserService;
import com.android.systemui.qs.QSTile.DetailAdapter;
import com.android.systemui.qs.tiles.UserDetailView;
import com.android.systemui.statusbar.phone.ActivityStarter;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.util.ThemeColorUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserSwitcherController
{
  private final ActivityStarter mActivityStarter;
  private final ArrayList<WeakReference<BaseUserAdapter>> mAdapters = new ArrayList();
  private Dialog mAddUserDialog;
  private boolean mAddUsersWhenLocked;
  private final KeyguardMonitor.Callback mCallback = new KeyguardMonitor.Callback()
  {
    public void onKeyguardChanged()
    {
      UserSwitcherController.-wrap2(UserSwitcherController.this);
    }
  };
  private final Context mContext;
  private Dialog mExitGuestDialog;
  private SparseBooleanArray mForcePictureLoadForUserId = new SparseBooleanArray(2);
  private final GuestResumeSessionReceiver mGuestResumeSessionReceiver = new GuestResumeSessionReceiver();
  private final Handler mHandler;
  private final KeyguardMonitor mKeyguardMonitor;
  private int mLastNonGuestUser = 0;
  private boolean mPauseRefreshUsers;
  private final PhoneStateListener mPhoneStateListener = new PhoneStateListener()
  {
    private int mCallState;
    
    public void onCallStateChanged(int paramAnonymousInt, String paramAnonymousString)
    {
      if (this.mCallState == paramAnonymousInt) {
        return;
      }
      this.mCallState = paramAnonymousInt;
      paramAnonymousInt = ActivityManager.getCurrentUser();
      paramAnonymousString = UserSwitcherController.-get9(UserSwitcherController.this).getUserInfo(paramAnonymousInt);
      if ((paramAnonymousString != null) && (paramAnonymousString.isGuest())) {
        UserSwitcherController.-wrap5(UserSwitcherController.this, paramAnonymousInt);
      }
      UserSwitcherController.-wrap3(UserSwitcherController.this, 55536);
    }
  };
  private BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    private void showLogoutNotification(int paramAnonymousInt)
    {
      Object localObject = PendingIntent.getBroadcastAsUser(UserSwitcherController.-get1(UserSwitcherController.this), 0, new Intent("com.android.systemui.LOGOUT_USER"), 0, UserHandle.SYSTEM);
      localObject = new Notification.Builder(UserSwitcherController.-get1(UserSwitcherController.this)).setVisibility(-1).setPriority(-2).setSmallIcon(2130837763).setContentTitle(UserSwitcherController.-get1(UserSwitcherController.this).getString(2131690386)).setContentText(UserSwitcherController.-get1(UserSwitcherController.this).getString(2131690387)).setContentIntent((PendingIntent)localObject).setOngoing(true).setShowWhen(false).addAction(2130837705, UserSwitcherController.-get1(UserSwitcherController.this).getString(2131690388), (PendingIntent)localObject);
      SystemUI.overrideNotificationAppName(UserSwitcherController.-get1(UserSwitcherController.this), (Notification.Builder)localObject);
      NotificationManager.from(UserSwitcherController.-get1(UserSwitcherController.this)).notifyAsUser("logout_user", 1011, ((Notification.Builder)localObject).build(), new UserHandle(paramAnonymousInt));
    }
    
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      int m = 0;
      int k = 55536;
      int i;
      if ("com.android.systemui.REMOVE_GUEST".equals(paramAnonymousIntent.getAction()))
      {
        i = ActivityManager.getCurrentUser();
        paramAnonymousContext = UserSwitcherController.-get9(UserSwitcherController.this).getUserInfo(i);
        if ((paramAnonymousContext != null) && (paramAnonymousContext.isGuest())) {
          UserSwitcherController.-wrap4(UserSwitcherController.this, i);
        }
        return;
      }
      int j;
      if ("com.android.systemui.LOGOUT_USER".equals(paramAnonymousIntent.getAction()))
      {
        UserSwitcherController.this.logoutCurrentUser();
        j = m;
        i = k;
      }
      label255:
      label348:
      label431:
      label511:
      do
      {
        do
        {
          for (;;)
          {
            UserSwitcherController.-wrap3(UserSwitcherController.this, i);
            if (j != 0) {
              UserSwitcherController.-get8(UserSwitcherController.this).run();
            }
            return;
            if ("android.intent.action.USER_SWITCHED".equals(paramAnonymousIntent.getAction()))
            {
              if ((UserSwitcherController.-get2(UserSwitcherController.this) != null) && (UserSwitcherController.-get2(UserSwitcherController.this).isShowing()))
              {
                UserSwitcherController.-get2(UserSwitcherController.this).cancel();
                UserSwitcherController.-set1(UserSwitcherController.this, null);
              }
              m = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", -1);
              paramAnonymousIntent = UserSwitcherController.-get9(UserSwitcherController.this).getUserInfo(m);
              int n = UserSwitcherController.-get10(UserSwitcherController.this).size();
              i = 0;
              while (i < n)
              {
                UserSwitcherController.UserRecord localUserRecord = (UserSwitcherController.UserRecord)UserSwitcherController.-get10(UserSwitcherController.this).get(i);
                if (localUserRecord.info == null)
                {
                  j = i;
                  i = j + 1;
                }
                else
                {
                  boolean bool;
                  if (localUserRecord.info.id == m)
                  {
                    bool = true;
                    if (localUserRecord.isCurrent != bool) {
                      UserSwitcherController.-get10(UserSwitcherController.this).set(i, localUserRecord.copyWithIsCurrent(bool));
                    }
                    if ((bool) && (!localUserRecord.isGuest)) {
                      break label348;
                    }
                  }
                  for (;;)
                  {
                    if (paramAnonymousIntent != null)
                    {
                      j = i;
                      if (paramAnonymousIntent.isAdmin()) {
                        break;
                      }
                    }
                    j = i;
                    if (!localUserRecord.isRestricted) {
                      break;
                    }
                    UserSwitcherController.-get10(UserSwitcherController.this).remove(i);
                    j = i - 1;
                    break;
                    bool = false;
                    break label255;
                    UserSwitcherController.-set2(UserSwitcherController.this, localUserRecord.info.id);
                  }
                }
              }
              UserSwitcherController.-wrap2(UserSwitcherController.this);
              if (UserSwitcherController.-get5(UserSwitcherController.this) != 55536)
              {
                paramAnonymousContext.stopServiceAsUser(UserSwitcherController.-get6(UserSwitcherController.this), UserHandle.of(UserSwitcherController.-get5(UserSwitcherController.this)));
                UserSwitcherController.-set4(UserSwitcherController.this, 55536);
              }
              if ((paramAnonymousIntent == null) || (paramAnonymousIntent.isPrimary())) {
                if ((UserManager.isSplitSystemUser()) && (paramAnonymousIntent != null) && (!paramAnonymousIntent.isGuest())) {
                  break label511;
                }
              }
              for (;;)
              {
                if ((paramAnonymousIntent != null) && (paramAnonymousIntent.isGuest())) {
                  UserSwitcherController.-wrap5(UserSwitcherController.this, m);
                }
                j = 1;
                i = k;
                break;
                paramAnonymousContext.startServiceAsUser(UserSwitcherController.-get6(UserSwitcherController.this), UserHandle.of(paramAnonymousIntent.id));
                UserSwitcherController.-set4(UserSwitcherController.this, paramAnonymousIntent.id);
                break label431;
                if (paramAnonymousIntent.id != 0) {
                  showLogoutNotification(m);
                }
              }
            }
            if (!"android.intent.action.USER_INFO_CHANGED".equals(paramAnonymousIntent.getAction())) {
              break;
            }
            i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 55536);
            j = m;
          }
          i = k;
          j = m;
        } while (!"android.intent.action.USER_UNLOCKED".equals(paramAnonymousIntent.getAction()));
        i = k;
        j = m;
      } while (paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 55536) == 0);
    }
  };
  private int mSecondaryUser = 55536;
  private Intent mSecondaryUserServiceIntent;
  private final ContentObserver mSettingsObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      boolean bool = true;
      UserSwitcherController localUserSwitcherController = UserSwitcherController.this;
      if (Settings.Global.getInt(UserSwitcherController.-get1(UserSwitcherController.this).getContentResolver(), "lockscreenSimpleUserSwitcher", 0) != 0)
      {
        paramAnonymousBoolean = true;
        UserSwitcherController.-set5(localUserSwitcherController, paramAnonymousBoolean);
        localUserSwitcherController = UserSwitcherController.this;
        if (Settings.Global.getInt(UserSwitcherController.-get1(UserSwitcherController.this).getContentResolver(), "add_users_when_locked", 0) == 0) {
          break label82;
        }
      }
      label82:
      for (paramAnonymousBoolean = bool;; paramAnonymousBoolean = false)
      {
        UserSwitcherController.-set0(localUserSwitcherController, paramAnonymousBoolean);
        UserSwitcherController.-wrap3(UserSwitcherController.this, 55536);
        return;
        paramAnonymousBoolean = false;
        break;
      }
    }
  };
  private boolean mSimpleUserSwitcher;
  private final Runnable mUnpauseRefreshUsers = new Runnable()
  {
    public void run()
    {
      UserSwitcherController.-get3(UserSwitcherController.this).removeCallbacks(this);
      UserSwitcherController.-set3(UserSwitcherController.this, false);
      UserSwitcherController.-wrap3(UserSwitcherController.this, 55536);
    }
  };
  private final UserManager mUserManager;
  private ArrayList<UserRecord> mUsers = new ArrayList();
  public final QSTile.DetailAdapter userDetailAdapter = new QSTile.DetailAdapter()
  {
    private final Intent USER_SETTINGS_INTENT = new Intent("android.settings.USER_SETTINGS");
    
    public View createDetailView(Context paramAnonymousContext, View paramAnonymousView, ViewGroup paramAnonymousViewGroup)
    {
      if (!(paramAnonymousView instanceof UserDetailView))
      {
        paramAnonymousContext = UserDetailView.inflate(paramAnonymousContext, paramAnonymousViewGroup, false);
        paramAnonymousContext.createAndSetAdapter(UserSwitcherController.this);
      }
      for (;;)
      {
        paramAnonymousContext.refreshAdapter();
        return paramAnonymousContext;
        paramAnonymousContext = (UserDetailView)paramAnonymousView;
      }
    }
    
    public int getMetricsCategory()
    {
      return 125;
    }
    
    public Intent getSettingsIntent()
    {
      return this.USER_SETTINGS_INTENT;
    }
    
    public CharSequence getTitle()
    {
      return UserSwitcherController.-get1(UserSwitcherController.this).getString(2131690288);
    }
    
    public Boolean getToggleState()
    {
      return null;
    }
    
    public void setToggleState(boolean paramAnonymousBoolean) {}
  };
  
  public UserSwitcherController(Context paramContext, KeyguardMonitor paramKeyguardMonitor, Handler paramHandler, ActivityStarter paramActivityStarter)
  {
    this.mContext = paramContext;
    this.mGuestResumeSessionReceiver.register(paramContext);
    this.mKeyguardMonitor = paramKeyguardMonitor;
    this.mHandler = paramHandler;
    this.mActivityStarter = paramActivityStarter;
    this.mUserManager = UserManager.get(paramContext);
    paramHandler = new IntentFilter();
    paramHandler.addAction("android.intent.action.USER_ADDED");
    paramHandler.addAction("android.intent.action.USER_REMOVED");
    paramHandler.addAction("android.intent.action.USER_INFO_CHANGED");
    paramHandler.addAction("android.intent.action.USER_SWITCHED");
    paramHandler.addAction("android.intent.action.USER_STOPPED");
    paramHandler.addAction("android.intent.action.USER_UNLOCKED");
    this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.SYSTEM, paramHandler, null, null);
    this.mSecondaryUserServiceIntent = new Intent(paramContext, SystemUISecondaryUserService.class);
    paramContext = new IntentFilter();
    paramContext.addAction("com.android.systemui.REMOVE_GUEST");
    paramContext.addAction("com.android.systemui.LOGOUT_USER");
    this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.SYSTEM, paramContext, "com.android.systemui.permission.SELF", null);
    this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("lockscreenSimpleUserSwitcher"), true, this.mSettingsObserver);
    this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("add_users_when_locked"), true, this.mSettingsObserver);
    this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("allow_user_switching_when_system_user_locked"), true, this.mSettingsObserver);
    this.mSettingsObserver.onChange(false);
    paramKeyguardMonitor.addCallback(this.mCallback);
    listenForCallState();
    refreshUsers(55536);
  }
  
  private void checkIfAddUserDisallowedByAdminOnly(UserRecord paramUserRecord)
  {
    RestrictedLockUtils.EnforcedAdmin localEnforcedAdmin = RestrictedLockUtils.checkIfRestrictionEnforced(this.mContext, "no_add_user", ActivityManager.getCurrentUser());
    if ((localEnforcedAdmin == null) || (RestrictedLockUtils.hasBaseUserRestriction(this.mContext, "no_add_user", ActivityManager.getCurrentUser())))
    {
      paramUserRecord.isDisabledByAdmin = false;
      paramUserRecord.enforcedAdmin = null;
      return;
    }
    paramUserRecord.isDisabledByAdmin = true;
    paramUserRecord.enforcedAdmin = localEnforcedAdmin;
  }
  
  private void exitGuest(int paramInt)
  {
    int j = 0;
    int i = j;
    if (this.mLastNonGuestUser != 0)
    {
      UserInfo localUserInfo = this.mUserManager.getUserInfo(this.mLastNonGuestUser);
      i = j;
      if (localUserInfo != null)
      {
        i = j;
        if (localUserInfo.isEnabled())
        {
          i = j;
          if (localUserInfo.supportsSwitchToByUser()) {
            i = localUserInfo.id;
          }
        }
      }
    }
    switchToUserId(i);
    this.mUserManager.removeUser(paramInt);
  }
  
  private void listenForCallState()
  {
    TelephonyManager.from(this.mContext).listen(this.mPhoneStateListener, 32);
  }
  
  private void notifyAdapters()
  {
    int i = this.mAdapters.size() - 1;
    if (i >= 0)
    {
      BaseUserAdapter localBaseUserAdapter = (BaseUserAdapter)((WeakReference)this.mAdapters.get(i)).get();
      if (localBaseUserAdapter != null) {
        localBaseUserAdapter.notifyDataSetChanged();
      }
      for (;;)
      {
        i -= 1;
        break;
        this.mAdapters.remove(i);
      }
    }
  }
  
  private void pauseRefreshUsers()
  {
    if (!this.mPauseRefreshUsers)
    {
      this.mHandler.postDelayed(this.mUnpauseRefreshUsers, 3000L);
      this.mPauseRefreshUsers = true;
    }
  }
  
  private void refreshUsers(int paramInt)
  {
    if (paramInt != 55536) {
      this.mForcePictureLoadForUserId.put(paramInt, true);
    }
    if (this.mPauseRefreshUsers) {
      return;
    }
    boolean bool = this.mForcePictureLoadForUserId.get(-1);
    SparseArray localSparseArray = new SparseArray(this.mUsers.size());
    int i = this.mUsers.size();
    paramInt = 0;
    if (paramInt < i)
    {
      UserRecord localUserRecord = (UserRecord)this.mUsers.get(paramInt);
      if ((localUserRecord == null) || (localUserRecord.picture == null)) {}
      for (;;)
      {
        paramInt += 1;
        break;
        if ((localUserRecord.info != null) && (!bool) && (!this.mForcePictureLoadForUserId.get(localUserRecord.info.id))) {
          localSparseArray.put(localUserRecord.info.id, localUserRecord.picture);
        }
      }
    }
    this.mForcePictureLoadForUserId.clear();
    new AsyncTask()
    {
      protected ArrayList<UserSwitcherController.UserRecord> doInBackground(SparseArray<Bitmap>... paramAnonymousVarArgs)
      {
        SparseArray<Bitmap> localSparseArray = paramAnonymousVarArgs[0];
        Object localObject = UserSwitcherController.-get9(UserSwitcherController.this).getUsers(true);
        if (localObject == null) {
          return null;
        }
        ArrayList localArrayList = new ArrayList(((List)localObject).size());
        int j = ActivityManager.getCurrentUser();
        boolean bool3 = UserSwitcherController.-get9(UserSwitcherController.this).canSwitchUsers();
        paramAnonymousVarArgs = null;
        UserSwitcherController.UserRecord localUserRecord = null;
        Iterator localIterator = ((Iterable)localObject).iterator();
        boolean bool1;
        while (localIterator.hasNext())
        {
          UserInfo localUserInfo = (UserInfo)localIterator.next();
          if (j == localUserInfo.id)
          {
            bool1 = true;
            label105:
            localObject = paramAnonymousVarArgs;
            if (bool1) {
              localObject = localUserInfo;
            }
            if (bool3) {
              break label176;
            }
          }
          label176:
          for (bool2 = bool1;; bool2 = true)
          {
            paramAnonymousVarArgs = (SparseArray<Bitmap>[])localObject;
            if (!localUserInfo.isEnabled()) {
              break;
            }
            if (!localUserInfo.isGuest()) {
              break label182;
            }
            localUserRecord = new UserSwitcherController.UserRecord(localUserInfo, null, true, bool1, false, false, bool3);
            paramAnonymousVarArgs = (SparseArray<Bitmap>[])localObject;
            break;
            bool1 = false;
            break label105;
          }
          label182:
          paramAnonymousVarArgs = (SparseArray<Bitmap>[])localObject;
          if (localUserInfo.supportsSwitchToByUser())
          {
            Bitmap localBitmap = (Bitmap)localSparseArray.get(localUserInfo.id);
            paramAnonymousVarArgs = localBitmap;
            if (localBitmap == null)
            {
              localBitmap = UserSwitcherController.-get9(UserSwitcherController.this).getUserIcon(localUserInfo.id);
              paramAnonymousVarArgs = localBitmap;
              if (localBitmap != null)
              {
                i = UserSwitcherController.-get1(UserSwitcherController.this).getResources().getDimensionPixelSize(2131755506);
                paramAnonymousVarArgs = Bitmap.createScaledBitmap(localBitmap, i, i, true);
              }
            }
            if (bool1) {}
            for (i = 0;; i = localArrayList.size())
            {
              localArrayList.add(i, new UserSwitcherController.UserRecord(localUserInfo, paramAnonymousVarArgs, false, bool1, false, false, bool2));
              paramAnonymousVarArgs = (SparseArray<Bitmap>[])localObject;
              break;
            }
          }
        }
        if (UserSwitcherController.-get9(UserSwitcherController.this).hasBaseUserRestriction("no_add_user", UserHandle.SYSTEM))
        {
          i = 0;
          if ((paramAnonymousVarArgs == null) || ((!paramAnonymousVarArgs.isAdmin()) && (paramAnonymousVarArgs.id != 0))) {
            break label514;
          }
          j = i;
          label351:
          if (i == 0) {
            break label519;
          }
          bool1 = this.val$addUsersWhenLocked;
          label361:
          if ((j == 0) && (!bool1)) {
            break label530;
          }
          if (localUserRecord != null) {
            break label525;
          }
          i = 1;
          label377:
          if ((j == 0) && (!bool1)) {
            break label535;
          }
          bool1 = UserSwitcherController.-get9(UserSwitcherController.this).canAddMoreUsers();
          label398:
          if (!this.val$addUsersWhenLocked) {
            break label541;
          }
        }
        label514:
        label519:
        label525:
        label530:
        label535:
        label541:
        for (boolean bool2 = false;; bool2 = true)
        {
          if (!UserSwitcherController.-get7(UserSwitcherController.this))
          {
            if (localUserRecord != null) {
              break label547;
            }
            if (i != 0)
            {
              paramAnonymousVarArgs = new UserSwitcherController.UserRecord(null, null, true, false, false, bool2, bool3);
              UserSwitcherController.-wrap0(UserSwitcherController.this, paramAnonymousVarArgs);
              localArrayList.add(paramAnonymousVarArgs);
            }
          }
          if ((!UserSwitcherController.-get7(UserSwitcherController.this)) && (bool1))
          {
            paramAnonymousVarArgs = new UserSwitcherController.UserRecord(null, null, false, false, true, bool2, bool3);
            UserSwitcherController.-wrap0(UserSwitcherController.this, paramAnonymousVarArgs);
            localArrayList.add(paramAnonymousVarArgs);
          }
          return localArrayList;
          i = 1;
          break;
          j = 0;
          break label351;
          bool1 = false;
          break label361;
          i = 0;
          break label377;
          i = 0;
          break label377;
          bool1 = false;
          break label398;
        }
        label547:
        if (localUserRecord.isCurrent) {}
        for (int i = 0;; i = localArrayList.size())
        {
          localArrayList.add(i, localUserRecord);
          break;
        }
      }
      
      protected void onPostExecute(ArrayList<UserSwitcherController.UserRecord> paramAnonymousArrayList)
      {
        if (paramAnonymousArrayList != null)
        {
          UserSwitcherController.-set6(UserSwitcherController.this, paramAnonymousArrayList);
          UserSwitcherController.-wrap2(UserSwitcherController.this);
        }
      }
    }.execute(new SparseArray[] { localSparseArray });
  }
  
  private void showAddUserDialog()
  {
    if ((this.mAddUserDialog != null) && (this.mAddUserDialog.isShowing())) {
      this.mAddUserDialog.cancel();
    }
    this.mAddUserDialog = new AddUserDialog(this.mContext);
    this.mAddUserDialog.show();
  }
  
  private void showExitGuestDialog(int paramInt)
  {
    if ((this.mExitGuestDialog != null) && (this.mExitGuestDialog.isShowing())) {
      this.mExitGuestDialog.cancel();
    }
    this.mExitGuestDialog = new ExitGuestDialog(this.mContext, paramInt);
    this.mExitGuestDialog.show();
  }
  
  private void showGuestNotification(int paramInt)
  {
    if (this.mUserManager.canSwitchUsers()) {}
    for (Object localObject = PendingIntent.getBroadcastAsUser(this.mContext, 0, new Intent("com.android.systemui.REMOVE_GUEST"), 0, UserHandle.SYSTEM);; localObject = null)
    {
      localObject = new Notification.Builder(this.mContext).setVisibility(-1).setPriority(-2).setSmallIcon(2130837763).setContentTitle(this.mContext.getString(2131690383)).setContentText(this.mContext.getString(2131690384)).setContentIntent((PendingIntent)localObject).setShowWhen(false).addAction(2130837705, this.mContext.getString(2131690385), (PendingIntent)localObject);
      SystemUI.overrideNotificationAppName(this.mContext, (Notification.Builder)localObject);
      NotificationManager.from(this.mContext).notifyAsUser("remove_guest", 1010, ((Notification.Builder)localObject).build(), new UserHandle(paramInt));
      return;
    }
  }
  
  private void switchToUserId(int paramInt)
  {
    try
    {
      pauseRefreshUsers();
      ActivityManagerNative.getDefault().switchUser(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("UserSwitcherController", "Couldn't switch user.", localRemoteException);
    }
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("UserSwitcherController state:");
    paramPrintWriter.println("  mLastNonGuestUser=" + this.mLastNonGuestUser);
    paramPrintWriter.print("  mUsers.size=");
    paramPrintWriter.println(this.mUsers.size());
    int i = 0;
    while (i < this.mUsers.size())
    {
      paramFileDescriptor = (UserRecord)this.mUsers.get(i);
      paramPrintWriter.print("    ");
      paramPrintWriter.println(paramFileDescriptor.toString());
      i += 1;
    }
  }
  
  public String getCurrentUserName(Context paramContext)
  {
    if (this.mUsers.isEmpty()) {
      return null;
    }
    UserRecord localUserRecord = (UserRecord)this.mUsers.get(0);
    if ((localUserRecord == null) || (localUserRecord.info == null)) {
      return null;
    }
    if (localUserRecord.isGuest) {
      return paramContext.getString(2131690373);
    }
    return localUserRecord.info.name;
  }
  
  public int getSwitchableUserCount()
  {
    int j = 0;
    int m = this.mUsers.size();
    int i = 0;
    while (i < m)
    {
      UserRecord localUserRecord = (UserRecord)this.mUsers.get(i);
      int k = j;
      if (localUserRecord.info != null)
      {
        k = j;
        if (localUserRecord.info.supportsSwitchTo()) {
          k = j + 1;
        }
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  public boolean isSimpleUserSwitcher()
  {
    return this.mSimpleUserSwitcher;
  }
  
  public void logoutCurrentUser()
  {
    if (ActivityManager.getCurrentUser() != 0)
    {
      pauseRefreshUsers();
      ActivityManager.logoutCurrentUser();
    }
  }
  
  public void onDensityOrFontScaleChanged()
  {
    refreshUsers(-1);
  }
  
  public void removeUserId(int paramInt)
  {
    if (paramInt == 0)
    {
      Log.w("UserSwitcherController", "User " + paramInt + " could not removed.");
      return;
    }
    if (ActivityManager.getCurrentUser() == paramInt) {
      switchToUserId(0);
    }
    if (this.mUserManager.removeUser(paramInt)) {
      refreshUsers(55536);
    }
  }
  
  public void startActivity(Intent paramIntent)
  {
    this.mActivityStarter.startActivity(paramIntent, true);
  }
  
  public void switchTo(UserRecord paramUserRecord)
  {
    UserInfo localUserInfo;
    if ((paramUserRecord.isGuest) && (paramUserRecord.info == null))
    {
      localUserInfo = this.mUserManager.createGuest(this.mContext, this.mContext.getString(2131690373));
      if (localUserInfo == null) {
        return;
      }
    }
    for (int i = localUserInfo.id; ActivityManager.getCurrentUser() == i; i = paramUserRecord.info.id)
    {
      if (paramUserRecord.isGuest) {
        showExitGuestDialog(i);
      }
      return;
      if (paramUserRecord.isAddUser)
      {
        showAddUserDialog();
        return;
      }
    }
    switchToUserId(i);
  }
  
  public boolean useFullscreenUserSwitcher()
  {
    boolean bool = false;
    int i = Settings.System.getInt(this.mContext.getContentResolver(), "enable_fullscreen_user_switcher", -1);
    if (i != -1)
    {
      if (i != 0) {
        bool = true;
      }
      return bool;
    }
    return this.mContext.getResources().getBoolean(2131558436);
  }
  
  private final class AddUserDialog
    extends SystemUIDialog
    implements DialogInterface.OnClickListener
  {
    public AddUserDialog(Context paramContext)
    {
      super();
      int i = ThemeColorUtils.getColor(ThemeColorUtils.QS_DIALOG_TEXT);
      paramContext = new SpannableStringBuilder().append(UserSwitcherController.-get1(UserSwitcherController.this).getString(2131690389), new ForegroundColorSpan(i), 18);
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder().append(UserSwitcherController.-get1(UserSwitcherController.this).getString(2131690390), new ForegroundColorSpan(i), 18);
      setTitle(paramContext);
      setMessage(localSpannableStringBuilder);
      setButton(-2, UserSwitcherController.-get1(UserSwitcherController.this).getString(17039360), this);
      setButton(-1, UserSwitcherController.-get1(UserSwitcherController.this).getString(17039370), this);
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (paramInt == -2)
      {
        cancel();
        return;
      }
      dismiss();
      if (ActivityManager.isUserAMonkey()) {
        return;
      }
      paramDialogInterface = UserSwitcherController.-get9(UserSwitcherController.this).createUser(UserSwitcherController.-get1(UserSwitcherController.this).getString(2131690372), 0);
      if (paramDialogInterface == null) {
        return;
      }
      paramInt = paramDialogInterface.id;
      paramDialogInterface = UserIcons.convertToBitmap(UserIcons.getDefaultUserIcon(paramInt, false));
      UserSwitcherController.-get9(UserSwitcherController.this).setUserIcon(paramInt, paramDialogInterface);
      UserSwitcherController.-wrap6(UserSwitcherController.this, paramInt);
    }
    
    public void show()
    {
      super.show();
      int i = ThemeColorUtils.getColor(ThemeColorUtils.PROGRESS);
      int j = ThemeColorUtils.getColor(ThemeColorUtils.QS_DIALOG_BACKGROUND);
      Button localButton1 = getButton(-2);
      Button localButton2 = getButton(-1);
      ColorDrawable localColorDrawable = new ColorDrawable(j);
      getWindow().setBackgroundDrawable(localColorDrawable);
      if (localButton1 != null) {
        localButton1.setTextColor(i);
      }
      if (localButton2 != null) {
        localButton2.setTextColor(i);
      }
    }
  }
  
  public static abstract class BaseUserAdapter
    extends BaseAdapter
  {
    final UserSwitcherController mController;
    
    protected BaseUserAdapter(UserSwitcherController paramUserSwitcherController)
    {
      this.mController = paramUserSwitcherController;
      UserSwitcherController.-get0(paramUserSwitcherController).add(new WeakReference(this));
    }
    
    public int getCount()
    {
      int j = 0;
      int i = j;
      if (UserSwitcherController.-get4(this.mController).isShowing())
      {
        i = j;
        if (UserSwitcherController.-get4(this.mController).isSecure()) {
          if (!UserSwitcherController.-get4(this.mController).canSkipBouncer()) {
            break label62;
          }
        }
      }
      label62:
      for (i = j; i == 0; i = 1) {
        return UserSwitcherController.-get10(this.mController).size();
      }
      int k = UserSwitcherController.-get10(this.mController).size();
      j = 0;
      i = 0;
      for (;;)
      {
        if ((i >= k) || (((UserSwitcherController.UserRecord)UserSwitcherController.-get10(this.mController).get(i)).isRestricted)) {
          return j;
        }
        j += 1;
        i += 1;
      }
    }
    
    public Drawable getDrawable(Context paramContext, UserSwitcherController.UserRecord paramUserRecord)
    {
      if (paramUserRecord.isAddUser) {
        return paramContext.getDrawable(2130837683);
      }
      return UserIcons.getDefaultUserIcon(paramUserRecord.resolveId(), false);
    }
    
    public UserSwitcherController.UserRecord getItem(int paramInt)
    {
      return (UserSwitcherController.UserRecord)UserSwitcherController.-get10(this.mController).get(paramInt);
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public String getName(Context paramContext, UserSwitcherController.UserRecord paramUserRecord)
    {
      if (paramUserRecord.isGuest)
      {
        if (paramUserRecord.isCurrent) {
          return paramContext.getString(2131690375);
        }
        if (paramUserRecord.info == null) {}
        for (int i = 2131690374;; i = 2131690373) {
          return paramContext.getString(i);
        }
      }
      if (paramUserRecord.isAddUser) {
        return paramContext.getString(2131690371);
      }
      return paramUserRecord.info.name;
    }
    
    public void refresh()
    {
      UserSwitcherController.-wrap3(this.mController, 55536);
    }
    
    public void switchTo(UserSwitcherController.UserRecord paramUserRecord)
    {
      this.mController.switchTo(paramUserRecord);
    }
  }
  
  private final class ExitGuestDialog
    extends SystemUIDialog
    implements DialogInterface.OnClickListener
  {
    private final int mGuestId;
    
    public ExitGuestDialog(Context paramContext, int paramInt)
    {
      super();
      setTitle(2131690376);
      setMessage(paramContext.getString(2131690377));
      setButton(-2, paramContext.getString(17039360), this);
      setButton(-1, paramContext.getString(2131690378), this);
      setCanceledOnTouchOutside(false);
      this.mGuestId = paramInt;
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (paramInt == -2)
      {
        cancel();
        return;
      }
      dismiss();
      UserSwitcherController.-wrap1(UserSwitcherController.this, this.mGuestId);
    }
  }
  
  public static final class UserRecord
  {
    public RestrictedLockUtils.EnforcedAdmin enforcedAdmin;
    public final UserInfo info;
    public final boolean isAddUser;
    public final boolean isCurrent;
    public boolean isDisabledByAdmin;
    public final boolean isGuest;
    public final boolean isRestricted;
    public boolean isStorageInsufficient;
    public boolean isSwitchToEnabled;
    public final Bitmap picture;
    
    public UserRecord(UserInfo paramUserInfo, Bitmap paramBitmap, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
    {
      this.info = paramUserInfo;
      this.picture = paramBitmap;
      this.isGuest = paramBoolean1;
      this.isCurrent = paramBoolean2;
      this.isAddUser = paramBoolean3;
      this.isRestricted = paramBoolean4;
      this.isSwitchToEnabled = paramBoolean5;
    }
    
    public UserRecord copyWithIsCurrent(boolean paramBoolean)
    {
      return new UserRecord(this.info, this.picture, this.isGuest, paramBoolean, this.isAddUser, this.isRestricted, this.isSwitchToEnabled);
    }
    
    public int resolveId()
    {
      if ((this.isGuest) || (this.info == null)) {
        return 55536;
      }
      return this.info.id;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("UserRecord(");
      if (this.info != null) {
        localStringBuilder.append("name=\"").append(this.info.name).append("\" id=").append(this.info.id);
      }
      for (;;)
      {
        if (this.isGuest) {
          localStringBuilder.append(" <isGuest>");
        }
        if (this.isAddUser) {
          localStringBuilder.append(" <isAddUser>");
        }
        if (this.isCurrent) {
          localStringBuilder.append(" <isCurrent>");
        }
        if (this.picture != null) {
          localStringBuilder.append(" <hasPicture>");
        }
        if (this.isRestricted) {
          localStringBuilder.append(" <isRestricted>");
        }
        if (this.isDisabledByAdmin)
        {
          localStringBuilder.append(" <isDisabledByAdmin>");
          localStringBuilder.append(" enforcedAdmin=").append(this.enforcedAdmin);
        }
        if (this.isSwitchToEnabled) {
          localStringBuilder.append(" <isSwitchToEnabled>");
        }
        localStringBuilder.append(')');
        return localStringBuilder.toString();
        if (this.isGuest) {
          localStringBuilder.append("<add guest placeholder>");
        } else if (this.isAddUser) {
          localStringBuilder.append("<add user placeholder>");
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\UserSwitcherController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */