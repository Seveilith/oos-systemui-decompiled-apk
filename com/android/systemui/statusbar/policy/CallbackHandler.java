package com.android.systemui.statusbar.policy;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.SubscriptionInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CallbackHandler
  extends Handler
  implements NetworkController.EmergencyListener, NetworkController.SignalCallbackExtended
{
  private final ArrayList<NetworkController.EmergencyListener> mEmergencyListeners = new ArrayList();
  private final ArrayList<NetworkController.SignalCallback> mSignalCallbacks = new ArrayList();
  
  public CallbackHandler() {}
  
  CallbackHandler(Looper paramLooper)
  {
    super(paramLooper);
  }
  
  public void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
    case 0: 
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
      Iterator localIterator;
      do
      {
        for (;;)
        {
          return;
          localIterator = this.mEmergencyListeners.iterator();
          if (localIterator.hasNext())
          {
            localObject = (NetworkController.EmergencyListener)localIterator.next();
            if (paramMessage.arg1 == 0) {
              break label103;
            }
          }
          for (bool = true;; bool = false)
          {
            ((NetworkController.EmergencyListener)localObject).setEmergencyCallsOnly(bool);
            break label63;
            break;
          }
          localIterator = this.mSignalCallbacks.iterator();
          while (localIterator.hasNext()) {
            ((NetworkController.SignalCallback)localIterator.next()).setSubs((List)paramMessage.obj);
          }
          continue;
          localIterator = this.mSignalCallbacks.iterator();
          if (localIterator.hasNext())
          {
            localObject = (NetworkController.SignalCallback)localIterator.next();
            if (paramMessage.arg1 == 0) {
              break label201;
            }
          }
          for (bool = true;; bool = false)
          {
            ((NetworkController.SignalCallback)localObject).setNoSims(bool);
            break label161;
            break;
          }
          localIterator = this.mSignalCallbacks.iterator();
          while (localIterator.hasNext()) {
            ((NetworkController.SignalCallback)localIterator.next()).setEthernetIndicators((NetworkController.IconState)paramMessage.obj);
          }
          continue;
          localIterator = this.mSignalCallbacks.iterator();
          while (localIterator.hasNext()) {
            ((NetworkController.SignalCallback)localIterator.next()).setIsAirplaneMode((NetworkController.IconState)paramMessage.obj);
          }
        }
        localIterator = this.mSignalCallbacks.iterator();
      } while (!localIterator.hasNext());
      Object localObject = (NetworkController.SignalCallback)localIterator.next();
      if (paramMessage.arg1 != 0) {}
      for (boolean bool = true;; bool = false)
      {
        ((NetworkController.SignalCallback)localObject).setMobileDataEnabled(bool);
        break;
      }
    case 6: 
      label63:
      label103:
      label161:
      label201:
      if (paramMessage.arg1 != 0)
      {
        this.mEmergencyListeners.add((NetworkController.EmergencyListener)paramMessage.obj);
        return;
      }
      this.mEmergencyListeners.remove((NetworkController.EmergencyListener)paramMessage.obj);
      return;
    }
    if (paramMessage.arg1 != 0)
    {
      this.mSignalCallbacks.add((NetworkController.SignalCallback)paramMessage.obj);
      return;
    }
    this.mSignalCallbacks.remove((NetworkController.SignalCallback)paramMessage.obj);
  }
  
  public void setEmergencyCallsOnly(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      obtainMessage(0, i, 0).sendToTarget();
      return;
    }
  }
  
  public void setEthernetIndicators(NetworkController.IconState paramIconState)
  {
    obtainMessage(3, paramIconState).sendToTarget();
  }
  
  public void setIsAirplaneMode(NetworkController.IconState paramIconState)
  {
    obtainMessage(4, paramIconState).sendToTarget();
  }
  
  public void setListening(NetworkController.EmergencyListener paramEmergencyListener, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      obtainMessage(6, i, 0, paramEmergencyListener).sendToTarget();
      return;
    }
  }
  
  public void setListening(NetworkController.SignalCallback paramSignalCallback, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      obtainMessage(7, i, 0, paramSignalCallback).sendToTarget();
      return;
    }
  }
  
  public void setMobileDataEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      obtainMessage(5, i, 0).sendToTarget();
      return;
    }
  }
  
  public void setMobileDataIndicators(final NetworkController.IconState paramIconState1, final NetworkController.IconState paramIconState2, final int paramInt1, final int paramInt2, final boolean paramBoolean1, final boolean paramBoolean2, final int paramInt3, final int paramInt4, final int paramInt5, final int paramInt6, final String paramString1, final String paramString2, final boolean paramBoolean3, final int paramInt7, final int paramInt8, final int paramInt9, final int paramInt10, final boolean paramBoolean4, final boolean paramBoolean5, final boolean paramBoolean6)
  {
    post(new Runnable()
    {
      public void run()
      {
        Iterator localIterator = CallbackHandler.-get0(CallbackHandler.this).iterator();
        while (localIterator.hasNext())
        {
          NetworkController.SignalCallback localSignalCallback = (NetworkController.SignalCallback)localIterator.next();
          if ((localSignalCallback instanceof NetworkController.SignalCallbackExtended)) {
            ((NetworkController.SignalCallbackExtended)localSignalCallback).setMobileDataIndicators(paramIconState1, paramIconState2, paramInt1, paramInt2, paramBoolean1, paramBoolean2, paramInt3, paramInt4, paramInt5, paramInt6, paramString1, paramString2, paramBoolean3, paramInt7, paramInt8, paramInt9, paramInt10, paramBoolean4, paramBoolean5, paramBoolean6);
          } else {
            localSignalCallback.setMobileDataIndicators(paramIconState1, paramIconState2, paramInt1, paramInt2, paramBoolean1, paramBoolean2, paramInt3, paramInt4, paramInt5, paramInt6, paramString1, paramString2, paramBoolean3, paramInt7, paramBoolean5, paramBoolean6);
          }
        }
      }
    });
  }
  
  public void setMobileDataIndicators(final NetworkController.IconState paramIconState1, final NetworkController.IconState paramIconState2, final int paramInt1, final int paramInt2, final boolean paramBoolean1, final boolean paramBoolean2, final int paramInt3, final int paramInt4, final int paramInt5, final int paramInt6, final String paramString1, final String paramString2, final boolean paramBoolean3, final int paramInt7, final boolean paramBoolean4, final boolean paramBoolean5)
  {
    post(new Runnable()
    {
      public void run()
      {
        Iterator localIterator = CallbackHandler.-get0(CallbackHandler.this).iterator();
        while (localIterator.hasNext()) {
          ((NetworkController.SignalCallback)localIterator.next()).setMobileDataIndicators(paramIconState1, paramIconState2, paramInt1, paramInt2, paramBoolean1, paramBoolean2, paramInt3, paramInt4, paramInt5, paramInt6, paramString1, paramString2, paramBoolean3, paramInt7, paramBoolean4, paramBoolean5);
        }
      }
    });
  }
  
  public void setNoSims(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      obtainMessage(2, i, 0).sendToTarget();
      return;
    }
  }
  
  public void setSubs(List<SubscriptionInfo> paramList)
  {
    obtainMessage(1, paramList).sendToTarget();
  }
  
  public void setWifiIndicators(final boolean paramBoolean1, final NetworkController.IconState paramIconState1, final NetworkController.IconState paramIconState2, final boolean paramBoolean2, final boolean paramBoolean3, final String paramString)
  {
    post(new Runnable()
    {
      public void run()
      {
        Iterator localIterator = CallbackHandler.-get0(CallbackHandler.this).iterator();
        while (localIterator.hasNext()) {
          ((NetworkController.SignalCallback)localIterator.next()).setWifiIndicators(paramBoolean1, paramIconState1, paramIconState2, paramBoolean2, paramBoolean3, paramString);
        }
      }
    });
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\CallbackHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */