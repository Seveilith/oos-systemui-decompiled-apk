package com.android.systemui.statusbar.phone;

import android.app.ActivityManager;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ManagedProfileController
{
  private final List<Callback> mCallbacks = new ArrayList();
  private final Context mContext;
  private int mCurrentUser;
  private boolean mListening;
  private final LinkedList<UserInfo> mProfiles;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      ManagedProfileController.-wrap0(ManagedProfileController.this);
      paramAnonymousContext = ManagedProfileController.-get0(ManagedProfileController.this).iterator();
      while (paramAnonymousContext.hasNext()) {
        ((ManagedProfileController.Callback)paramAnonymousContext.next()).onManagedProfileChanged();
      }
    }
  };
  private final UserManager mUserManager;
  
  public ManagedProfileController(QSTileHost paramQSTileHost)
  {
    this.mContext = paramQSTileHost.getContext();
    this.mUserManager = UserManager.get(this.mContext);
    this.mProfiles = new LinkedList();
  }
  
  private void reloadManagedProfiles()
  {
    int i;
    int j;
    for (;;)
    {
      synchronized (this.mProfiles)
      {
        if (this.mProfiles.size() > 0)
        {
          i = 1;
          j = ActivityManager.getCurrentUser();
          this.mProfiles.clear();
          Iterator localIterator1 = this.mUserManager.getEnabledProfiles(j).iterator();
          if (!localIterator1.hasNext()) {
            break;
          }
          UserInfo localUserInfo = (UserInfo)localIterator1.next();
          if ((!localUserInfo.isManagedProfile()) || (localUserInfo.id == 999)) {
            continue;
          }
          this.mProfiles.add(localUserInfo);
        }
      }
      i = 0;
    }
    if ((this.mProfiles.size() == 0) && (i != 0) && (j == this.mCurrentUser))
    {
      Iterator localIterator2 = this.mCallbacks.iterator();
      while (localIterator2.hasNext()) {
        ((Callback)localIterator2.next()).onManagedProfileRemoved();
      }
    }
    this.mCurrentUser = j;
  }
  
  private void setListening(boolean paramBoolean)
  {
    this.mListening = paramBoolean;
    if (paramBoolean)
    {
      reloadManagedProfiles();
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.USER_SWITCHED");
      localIntentFilter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
      localIntentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
      localIntentFilter.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
      localIntentFilter.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
      this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, localIntentFilter, null, null);
      return;
    }
    this.mContext.unregisterReceiver(this.mReceiver);
  }
  
  public void addCallback(Callback paramCallback)
  {
    this.mCallbacks.add(paramCallback);
    if (this.mCallbacks.size() == 1) {
      setListening(true);
    }
    paramCallback.onManagedProfileChanged();
  }
  
  public boolean hasActiveProfile()
  {
    boolean bool = false;
    if (!this.mListening) {
      reloadManagedProfiles();
    }
    synchronized (this.mProfiles)
    {
      int i = this.mProfiles.size();
      if (i > 0) {
        bool = true;
      }
      return bool;
    }
  }
  
  public boolean isWorkModeEnabled()
  {
    if (!this.mListening) {
      reloadManagedProfiles();
    }
    synchronized (this.mProfiles)
    {
      Iterator localIterator = this.mProfiles.iterator();
      while (localIterator.hasNext())
      {
        boolean bool = ((UserInfo)localIterator.next()).isQuietModeEnabled();
        if (bool) {
          return false;
        }
      }
      return true;
    }
  }
  
  public void removeCallback(Callback paramCallback)
  {
    if ((this.mCallbacks.remove(paramCallback)) && (this.mCallbacks.size() == 0)) {
      setListening(false);
    }
  }
  
  public void setWorkModeEnabled(boolean paramBoolean)
  {
    for (;;)
    {
      UserInfo localUserInfo;
      synchronized (this.mProfiles)
      {
        Iterator localIterator = this.mProfiles.iterator();
        if (!localIterator.hasNext()) {
          break;
        }
        localUserInfo = (UserInfo)localIterator.next();
        if (paramBoolean)
        {
          if (this.mUserManager.trySetQuietModeDisabled(localUserInfo.id, null)) {
            continue;
          }
          ((StatusBarManager)this.mContext.getSystemService("statusbar")).collapsePanels();
        }
      }
      this.mUserManager.setQuietModeEnabled(localUserInfo.id, true);
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void onManagedProfileChanged();
    
    public abstract void onManagedProfileRemoved();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\ManagedProfileController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */