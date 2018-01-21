package com.android.systemui.shortcut;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.RemoteException;
import android.util.Log;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.android.internal.policy.DividerSnapAlgorithm;
import com.android.internal.policy.DividerSnapAlgorithm.SnapTarget;
import com.android.systemui.SystemUI;
import com.android.systemui.recents.Recents;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.stackdivider.DividerView;

public class ShortcutKeyDispatcher
  extends SystemUI
  implements ShortcutKeyServiceProxy.Callbacks
{
  protected final long ALT_MASK = 8589934592L;
  protected final long CTRL_MASK = 17592186044416L;
  protected final long META_MASK = 281474976710656L;
  protected final long SC_DOCK_LEFT = 281474976710727L;
  protected final long SC_DOCK_RIGHT = 281474976710728L;
  protected final long SHIFT_MASK = 4294967296L;
  private IActivityManager mActivityManager = ActivityManagerNative.getDefault();
  private ShortcutKeyServiceProxy mShortcutKeyServiceProxy = new ShortcutKeyServiceProxy(this);
  private IWindowManager mWindowManagerService = WindowManagerGlobal.getWindowManagerService();
  
  private void handleDockKey(long paramLong)
  {
    for (;;)
    {
      try
      {
        if (this.mWindowManagerService.getDockedStackSide() == -1)
        {
          localObject1 = (Recents)getComponent(Recents.class);
          if (paramLong != 281474976710727L) {
            break label138;
          }
          i = 0;
          ((Recents)localObject1).dockTopTask(-1, i, null, 352);
          return;
        }
        Object localObject1 = ((Divider)getComponent(Divider.class)).getView();
        Object localObject2 = ((DividerView)localObject1).getSnapAlgorithm();
        DividerSnapAlgorithm.SnapTarget localSnapTarget = ((DividerSnapAlgorithm)localObject2).calculateNonDismissingSnapTarget(((DividerView)localObject1).getCurrentPosition());
        if (paramLong == 281474976710727L)
        {
          i = -1;
          localObject2 = ((DividerSnapAlgorithm)localObject2).cycleNonDismissTarget(localSnapTarget, i);
          ((DividerView)localObject1).startDragging(true, false);
          ((DividerView)localObject1).stopDragging(((DividerSnapAlgorithm.SnapTarget)localObject2).position, 0.0F, true, true);
          return;
        }
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("ShortcutKeyDispatcher", "handleDockKey() failed.");
        return;
      }
      int i = 1;
      continue;
      label138:
      i = 1;
    }
  }
  
  public void onShortcutKeyPressed(long paramLong)
  {
    int i = this.mContext.getResources().getConfiguration().orientation;
    if (((paramLong == 281474976710727L) || (paramLong == 281474976710728L)) && (i == 2)) {
      handleDockKey(paramLong);
    }
  }
  
  public void registerShortcutKey(long paramLong)
  {
    try
    {
      this.mWindowManagerService.registerShortcutKey(paramLong, this.mShortcutKeyServiceProxy);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void start()
  {
    registerShortcutKey(281474976710727L);
    registerShortcutKey(281474976710728L);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\shortcut\ShortcutKeyDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */