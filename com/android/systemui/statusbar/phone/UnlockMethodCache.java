package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.os.Trace;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import java.util.ArrayList;
import java.util.Iterator;

public class UnlockMethodCache
{
  private static UnlockMethodCache sInstance;
  private final KeyguardUpdateMonitorCallback mCallback = new KeyguardUpdateMonitorCallback()
  {
    public void onFaceUnlockStateChanged(boolean paramAnonymousBoolean, int paramAnonymousInt)
    {
      UnlockMethodCache.-wrap0(UnlockMethodCache.this, false);
    }
    
    public void onFingerprintAuthenticated(int paramAnonymousInt)
    {
      Trace.beginSection("KeyguardUpdateMonitorCallback#onFingerprintAuthenticated");
      if (!UnlockMethodCache.-get0(UnlockMethodCache.this).isUnlockingWithFingerprintAllowed())
      {
        Trace.endSection();
        return;
      }
      UnlockMethodCache.-wrap0(UnlockMethodCache.this, false);
      Trace.endSection();
    }
    
    public void onKeyguardVisibilityChangedRaw(boolean paramAnonymousBoolean)
    {
      UnlockMethodCache.-wrap0(UnlockMethodCache.this, false);
    }
    
    public void onStartedWakingUp()
    {
      UnlockMethodCache.-wrap0(UnlockMethodCache.this, false);
    }
    
    public void onStrongAuthStateChanged(int paramAnonymousInt)
    {
      UnlockMethodCache.-wrap0(UnlockMethodCache.this, false);
    }
    
    public void onTrustChanged(int paramAnonymousInt)
    {
      UnlockMethodCache.-wrap0(UnlockMethodCache.this, false);
    }
    
    public void onTrustManagedChanged(int paramAnonymousInt)
    {
      UnlockMethodCache.-wrap0(UnlockMethodCache.this, false);
    }
    
    public void onUserSwitchComplete(int paramAnonymousInt)
    {
      UnlockMethodCache.-wrap0(UnlockMethodCache.this, false);
    }
  };
  private boolean mCanSkipBouncer;
  private boolean mFaceUnlockRunning;
  private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
  private final ArrayList<OnUnlockMethodChangedListener> mListeners = new ArrayList();
  private final LockPatternUtils mLockPatternUtils;
  private boolean mSecure;
  private boolean mTrustManaged;
  private boolean mTrusted;
  
  private UnlockMethodCache(Context paramContext)
  {
    this.mLockPatternUtils = new LockPatternUtils(paramContext);
    this.mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(paramContext);
    KeyguardUpdateMonitor.getInstance(paramContext).registerCallback(this.mCallback);
    update(true);
  }
  
  public static UnlockMethodCache getInstance(Context paramContext)
  {
    if (sInstance == null) {
      sInstance = new UnlockMethodCache(paramContext);
    }
    return sInstance;
  }
  
  private void notifyListeners()
  {
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((OnUnlockMethodChangedListener)localIterator.next()).onUnlockMethodStateChanged();
    }
  }
  
  private void update(boolean paramBoolean)
  {
    int j = 1;
    Trace.beginSection("UnlockMethodCache#update");
    int i = KeyguardUpdateMonitor.getCurrentUser();
    boolean bool4 = this.mLockPatternUtils.isSecure(i);
    boolean bool1;
    boolean bool3;
    boolean bool5;
    boolean bool2;
    if (bool4)
    {
      bool1 = this.mKeyguardUpdateMonitor.getUserCanSkipBouncer(i);
      bool3 = this.mKeyguardUpdateMonitor.getUserTrustIsManaged(i);
      bool5 = this.mKeyguardUpdateMonitor.getUserHasTrust(i);
      if (!this.mKeyguardUpdateMonitor.isFaceUnlockRunning(i)) {
        break label145;
      }
      bool2 = bool3;
      label71:
      i = j;
      if (bool4 == this.mSecure)
      {
        if (bool1 == this.mCanSkipBouncer) {
          break label151;
        }
        i = j;
      }
    }
    for (;;)
    {
      if ((i != 0) || (paramBoolean))
      {
        this.mSecure = bool4;
        this.mCanSkipBouncer = bool1;
        this.mTrusted = bool5;
        this.mTrustManaged = bool3;
        this.mFaceUnlockRunning = bool2;
        notifyListeners();
      }
      Trace.endSection();
      return;
      bool1 = true;
      break;
      label145:
      bool2 = false;
      break label71;
      label151:
      i = j;
      if (bool3 == this.mTrustManaged)
      {
        i = j;
        if (bool2 == this.mFaceUnlockRunning) {
          i = 0;
        }
      }
    }
  }
  
  public void addListener(OnUnlockMethodChangedListener paramOnUnlockMethodChangedListener)
  {
    this.mListeners.add(paramOnUnlockMethodChangedListener);
  }
  
  public boolean canSkipBouncer()
  {
    return this.mCanSkipBouncer;
  }
  
  public boolean isFaceUnlockRunning()
  {
    return this.mFaceUnlockRunning;
  }
  
  public boolean isMethodSecure()
  {
    return this.mSecure;
  }
  
  public boolean isTrustManaged()
  {
    return this.mTrustManaged;
  }
  
  public boolean isTrusted()
  {
    return this.mTrusted;
  }
  
  public static abstract interface OnUnlockMethodChangedListener
  {
    public abstract void onUnlockMethodStateChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\UnlockMethodCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */