package com.android.systemui.recents.model;

import android.content.Context;
import android.os.UserHandle;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import com.android.systemui.recents.LockStateController;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.PackagesChangedEvent;

public class RecentsPackageMonitor
  extends PackageMonitor
{
  private Context mContext;
  
  public boolean onPackageChanged(String paramString, int paramInt, String[] paramArrayOfString)
  {
    onPackageModified(paramString);
    return true;
  }
  
  public void onPackageModified(String paramString)
  {
    int i = getChangingUserId();
    EventBus.getDefault().post(new PackagesChangedEvent(this, paramString, i));
  }
  
  public void onPackageRemoved(String paramString, int paramInt)
  {
    int i = getChangingUserId();
    EventBus.getDefault().post(new PackagesChangedEvent(this, paramString, i));
    LockStateController.getInstance(this.mContext).removeLockState(paramString, paramInt);
  }
  
  public void register(Context paramContext)
  {
    this.mContext = paramContext;
    try
    {
      register(paramContext, BackgroundThread.get().getLooper(), UserHandle.ALL, true);
      return;
    }
    catch (IllegalStateException paramContext)
    {
      paramContext.printStackTrace();
    }
  }
  
  public void unregister()
  {
    try
    {
      super.unregister();
      return;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      localIllegalStateException.printStackTrace();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\model\RecentsPackageMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */