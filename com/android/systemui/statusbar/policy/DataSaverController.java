package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.net.INetworkPolicyListener;
import android.net.INetworkPolicyListener.Stub;
import android.net.NetworkPolicyManager;
import android.os.Handler;
import android.os.RemoteException;
import java.util.ArrayList;

public class DataSaverController
{
  private final Handler mHandler = new Handler();
  private final ArrayList<Listener> mListeners = new ArrayList();
  private final INetworkPolicyListener mPolicyListener = new INetworkPolicyListener.Stub()
  {
    public void onMeteredIfacesChanged(String[] paramAnonymousArrayOfString)
      throws RemoteException
    {}
    
    public void onRestrictBackgroundBlacklistChanged(int paramAnonymousInt, boolean paramAnonymousBoolean) {}
    
    public void onRestrictBackgroundChanged(final boolean paramAnonymousBoolean)
      throws RemoteException
    {
      DataSaverController.-get0(DataSaverController.this).post(new Runnable()
      {
        public void run()
        {
          DataSaverController.-wrap0(DataSaverController.this, paramAnonymousBoolean);
        }
      });
    }
    
    public void onRestrictBackgroundWhitelistChanged(int paramAnonymousInt, boolean paramAnonymousBoolean) {}
    
    public void onUidRulesChanged(int paramAnonymousInt1, int paramAnonymousInt2)
      throws RemoteException
    {}
  };
  private final NetworkPolicyManager mPolicyManager;
  
  public DataSaverController(Context paramContext)
  {
    this.mPolicyManager = NetworkPolicyManager.from(paramContext);
  }
  
  private void handleRestrictBackgroundChanged(boolean paramBoolean)
  {
    ArrayList localArrayList = this.mListeners;
    int i = 0;
    try
    {
      while (i < this.mListeners.size())
      {
        ((Listener)this.mListeners.get(i)).onDataSaverChanged(paramBoolean);
        i += 1;
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void addListener(Listener paramListener)
  {
    synchronized (this.mListeners)
    {
      this.mListeners.add(paramListener);
      if (this.mListeners.size() == 1) {
        this.mPolicyManager.registerListener(this.mPolicyListener);
      }
      paramListener.onDataSaverChanged(isDataSaverEnabled());
      return;
    }
  }
  
  public boolean isDataSaverEnabled()
  {
    return this.mPolicyManager.getRestrictBackground();
  }
  
  public void remListener(Listener paramListener)
  {
    synchronized (this.mListeners)
    {
      this.mListeners.remove(paramListener);
      if (this.mListeners.size() == 0) {
        this.mPolicyManager.unregisterListener(this.mPolicyListener);
      }
      return;
    }
  }
  
  public void setDataSaverEnabled(boolean paramBoolean)
  {
    this.mPolicyManager.setRestrictBackground(paramBoolean);
    try
    {
      this.mPolicyListener.onRestrictBackgroundChanged(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public static abstract interface Listener
  {
    public abstract void onDataSaverChanged(boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\DataSaverController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */