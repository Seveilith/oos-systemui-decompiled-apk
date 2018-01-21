package com.android.systemui.statusbar.policy;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.ContactsContract.Profile;
import android.util.Log;
import android.util.Pair;
import com.android.settingslib.drawable.UserIconDrawable;
import com.android.settingslib.drawable.UserIcons;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class UserInfoController
{
  private final ArrayList<OnUserInfoChangedListener> mCallbacks = new ArrayList();
  private final Context mContext;
  private final BroadcastReceiver mProfileReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if (("android.provider.Contacts.PROFILE_CHANGED".equals(paramAnonymousContext)) || ("android.intent.action.USER_INFO_CHANGED".equals(paramAnonymousContext))) {}
      try
      {
        int i = ActivityManagerNative.getDefault().getCurrentUser().id;
        if (paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", getSendingUserId()) == i) {
          UserInfoController.this.reloadUserInfo();
        }
        return;
      }
      catch (RemoteException paramAnonymousContext)
      {
        Log.e("UserInfoController", "Couldn't get current user id for profile change", paramAnonymousContext);
      }
    }
  };
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.intent.action.USER_SWITCHED".equals(paramAnonymousIntent.getAction())) {
        UserInfoController.this.reloadUserInfo();
      }
    }
  };
  private Drawable mUserDrawable;
  private AsyncTask<Void, Void, Pair<String, Drawable>> mUserInfoTask;
  private String mUserName;
  
  public UserInfoController(Context paramContext)
  {
    this.mContext = paramContext;
    paramContext = new IntentFilter();
    paramContext.addAction("android.intent.action.USER_SWITCHED");
    this.mContext.registerReceiver(this.mReceiver, paramContext);
    paramContext = new IntentFilter();
    paramContext.addAction("android.provider.Contacts.PROFILE_CHANGED");
    paramContext.addAction("android.intent.action.USER_INFO_CHANGED");
    this.mContext.registerReceiverAsUser(this.mProfileReceiver, UserHandle.ALL, paramContext, null, null);
  }
  
  private void notifyChanged()
  {
    Iterator localIterator = this.mCallbacks.iterator();
    while (localIterator.hasNext()) {
      ((OnUserInfoChangedListener)localIterator.next()).onUserInfoChanged(this.mUserName, this.mUserDrawable);
    }
  }
  
  private void queryForUserInformation()
  {
    try
    {
      final Object localObject = ActivityManagerNative.getDefault().getCurrentUser();
      final Context localContext = this.mContext.createPackageContextAsUser("android", 0, new UserHandle(((UserInfo)localObject).id));
      final int i = ((UserInfo)localObject).id;
      final boolean bool = ((UserInfo)localObject).isGuest();
      localObject = ((UserInfo)localObject).name;
      Resources localResources = this.mContext.getResources();
      this.mUserInfoTask = new AsyncTask()
      {
        protected Pair<String, Drawable> doInBackground(Void... paramAnonymousVarArgs)
        {
          localObject2 = UserManager.get(UserInfoController.-get0(UserInfoController.this));
          paramAnonymousVarArgs = localObject;
          Object localObject1 = ((UserManager)localObject2).getUserIcon(i);
          Void[] arrayOfVoid;
          if (localObject1 != null)
          {
            localObject1 = new UserIconDrawable(this.val$avatarSize).setIcon((Bitmap)localObject1).setBadgeIfManagedUser(UserInfoController.-get0(UserInfoController.this), i).bake();
            arrayOfVoid = paramAnonymousVarArgs;
            if (((UserManager)localObject2).getUsers().size() <= 1)
            {
              localObject2 = localContext.getContentResolver().query(ContactsContract.Profile.CONTENT_URI, new String[] { "_id", "display_name" }, null, null, null);
              arrayOfVoid = paramAnonymousVarArgs;
              if (localObject2 == null) {}
            }
          }
          try
          {
            if (((Cursor)localObject2).moveToFirst()) {
              paramAnonymousVarArgs = ((Cursor)localObject2).getString(((Cursor)localObject2).getColumnIndex("display_name"));
            }
            ((Cursor)localObject2).close();
            return new Pair(arrayOfVoid, localObject1);
          }
          finally
          {
            int i;
            ((Cursor)localObject2).close();
          }
          if (bool) {}
          for (i = 55536;; i = i)
          {
            localObject1 = UserIcons.getDefaultUserIcon(i, false);
            break;
          }
        }
        
        protected void onPostExecute(Pair<String, Drawable> paramAnonymousPair)
        {
          UserInfoController.-set2(UserInfoController.this, (String)paramAnonymousPair.first);
          UserInfoController.-set0(UserInfoController.this, (Drawable)paramAnonymousPair.second);
          UserInfoController.-set1(UserInfoController.this, null);
          UserInfoController.-wrap0(UserInfoController.this);
        }
      };
      this.mUserInfoTask.execute(new Void[0]);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("UserInfoController", "Couldn't get user info", localRemoteException);
      throw new RuntimeException(localRemoteException);
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.e("UserInfoController", "Couldn't create user context", localNameNotFoundException);
      throw new RuntimeException(localNameNotFoundException);
    }
  }
  
  public void addListener(OnUserInfoChangedListener paramOnUserInfoChangedListener)
  {
    this.mCallbacks.add(paramOnUserInfoChangedListener);
    paramOnUserInfoChangedListener.onUserInfoChanged(this.mUserName, this.mUserDrawable);
  }
  
  public void onDensityOrFontScaleChanged()
  {
    reloadUserInfo();
  }
  
  public void reloadUserInfo()
  {
    if (this.mUserInfoTask != null)
    {
      this.mUserInfoTask.cancel(false);
      this.mUserInfoTask = null;
    }
    queryForUserInformation();
  }
  
  public void remListener(OnUserInfoChangedListener paramOnUserInfoChangedListener)
  {
    this.mCallbacks.remove(paramOnUserInfoChangedListener);
  }
  
  public static abstract interface OnUserInfoChangedListener
  {
    public abstract void onUserInfoChanged(String paramString, Drawable paramDrawable);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\UserInfoController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */