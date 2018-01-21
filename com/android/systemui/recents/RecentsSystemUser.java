package com.android.systemui.recents;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.EventLog;
import android.util.Log;
import android.util.SparseArray;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.DockedTopTaskEvent;
import com.android.systemui.recents.events.activity.RecentsActivityStartingEvent;
import com.android.systemui.recents.events.ui.RecentsDrawnEvent;
import com.android.systemui.recents.misc.ForegroundThread;

public class RecentsSystemUser
  extends IRecentsSystemUserCallbacks.Stub
{
  private Context mContext;
  private RecentsImpl mImpl;
  private final SparseArray<IRecentsNonSystemUserCallbacks> mNonSystemUserRecents = new SparseArray();
  
  public RecentsSystemUser(Context paramContext, RecentsImpl paramRecentsImpl)
  {
    this.mContext = paramContext;
    this.mImpl = paramRecentsImpl;
  }
  
  public IRecentsNonSystemUserCallbacks getNonSystemUserRecentsForUser(int paramInt)
  {
    return (IRecentsNonSystemUserCallbacks)this.mNonSystemUserRecents.get(paramInt);
  }
  
  public void registerNonSystemUserCallbacks(IBinder paramIBinder, final int paramInt)
  {
    try
    {
      final IRecentsNonSystemUserCallbacks localIRecentsNonSystemUserCallbacks = IRecentsNonSystemUserCallbacks.Stub.asInterface(paramIBinder);
      paramIBinder.linkToDeath(new IBinder.DeathRecipient()
      {
        public void binderDied()
        {
          RecentsSystemUser.-get0(RecentsSystemUser.this).removeAt(RecentsSystemUser.-get0(RecentsSystemUser.this).indexOfValue(localIRecentsNonSystemUserCallbacks));
          EventLog.writeEvent(36060, new Object[] { Integer.valueOf(5), Integer.valueOf(paramInt) });
        }
      }, 0);
      this.mNonSystemUserRecents.put(paramInt, localIRecentsNonSystemUserCallbacks);
      EventLog.writeEvent(36060, new Object[] { Integer.valueOf(4), Integer.valueOf(paramInt) });
      return;
    }
    catch (RemoteException paramIBinder)
    {
      Log.e("RecentsSystemUser", "Failed to register NonSystemUserCallbacks", paramIBinder);
    }
  }
  
  public void sendDockingTopTaskEvent(int paramInt, Rect paramRect)
    throws RemoteException
  {
    EventBus.getDefault().post(new DockedTopTaskEvent(paramInt, paramRect));
  }
  
  public void sendLaunchRecentsEvent()
    throws RemoteException
  {
    EventBus.getDefault().post(new RecentsActivityStartingEvent());
  }
  
  public void sendRecentsDrawnEvent()
  {
    EventBus.getDefault().post(new RecentsDrawnEvent());
  }
  
  public void startScreenPinning(int paramInt)
  {
    ForegroundThread.getHandler().post(new -void_startScreenPinning_int_taskId_LambdaImpl0(paramInt));
  }
  
  public void updateRecentsVisibility(boolean paramBoolean)
  {
    ForegroundThread.getHandler().post(new -void_updateRecentsVisibility_boolean_visible_LambdaImpl0(paramBoolean));
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\RecentsSystemUser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */