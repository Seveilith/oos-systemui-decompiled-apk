package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.settings.CurrentUserTracker;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public final class KeyguardMonitor
  extends KeyguardUpdateMonitorCallback
{
  private final ArrayList<Callback> mCallbacks = new ArrayList();
  private boolean mCanSkipBouncer;
  private final Context mContext;
  private int mCurrentUser;
  private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
  private boolean mListening;
  private boolean mOccluded;
  private boolean mSecure;
  private boolean mShowing;
  private final CurrentUserTracker mUserTracker;
  
  public KeyguardMonitor(Context paramContext)
  {
    this.mContext = paramContext;
    this.mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
    this.mUserTracker = new CurrentUserTracker(this.mContext)
    {
      public void onUserSwitched(int paramAnonymousInt)
      {
        KeyguardMonitor.-set0(KeyguardMonitor.this, paramAnonymousInt);
        KeyguardMonitor.-wrap0(KeyguardMonitor.this);
      }
    };
  }
  
  private void notifyKeyguardChanged()
  {
    try
    {
      Iterator localIterator = this.mCallbacks.iterator();
      while (localIterator.hasNext()) {
        ((Callback)localIterator.next()).onKeyguardChanged();
      }
      return;
    }
    catch (ConcurrentModificationException localConcurrentModificationException)
    {
      Log.w("KeyguardMonitor", "abnormal operation", localConcurrentModificationException);
    }
  }
  
  private void updateCanSkipBouncerState()
  {
    this.mCanSkipBouncer = this.mKeyguardUpdateMonitor.getUserCanSkipBouncer(this.mCurrentUser);
  }
  
  public void addCallback(Callback paramCallback)
  {
    this.mCallbacks.add(paramCallback);
    if ((this.mCallbacks.size() == 0) || (this.mListening)) {
      return;
    }
    this.mListening = true;
    this.mCurrentUser = ActivityManager.getCurrentUser();
    updateCanSkipBouncerState();
    this.mKeyguardUpdateMonitor.registerCallback(this);
    this.mUserTracker.startTracking();
  }
  
  public boolean canSkipBouncer()
  {
    return this.mCanSkipBouncer;
  }
  
  public boolean isDeviceInteractive()
  {
    return this.mKeyguardUpdateMonitor.isDeviceInteractive();
  }
  
  public boolean isOccluded()
  {
    return this.mOccluded;
  }
  
  public boolean isSecure()
  {
    return this.mSecure;
  }
  
  public boolean isShowing()
  {
    return this.mShowing;
  }
  
  public void notifyKeyguardState(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if ((this.mShowing == paramBoolean1) && (this.mSecure == paramBoolean2) && (this.mOccluded == paramBoolean3)) {
      return;
    }
    this.mShowing = paramBoolean1;
    this.mSecure = paramBoolean2;
    this.mOccluded = paramBoolean3;
    notifyKeyguardChanged();
  }
  
  public void onTrustChanged(int paramInt)
  {
    updateCanSkipBouncerState();
    notifyKeyguardChanged();
  }
  
  public void removeCallback(Callback paramCallback)
  {
    if ((this.mCallbacks.remove(paramCallback)) && (this.mCallbacks.size() == 0) && (this.mListening))
    {
      this.mListening = false;
      this.mKeyguardUpdateMonitor.removeCallback(this);
      this.mUserTracker.stopTracking();
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void onKeyguardChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\KeyguardMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */