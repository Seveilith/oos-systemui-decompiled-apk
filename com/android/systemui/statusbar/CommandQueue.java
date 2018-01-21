package com.android.systemui.statusbar;

import android.content.ComponentName;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Pair;
import com.android.internal.os.SomeArgs;
import com.android.internal.statusbar.IStatusBar.Stub;
import com.android.internal.statusbar.StatusBarIcon;
import java.util.List;

public class CommandQueue
  extends IStatusBar.Stub
{
  private Callbacks mCallbacks;
  private Handler mHandler = new H(null);
  private final Object mLock = new Object();
  
  public CommandQueue(Callbacks paramCallbacks)
  {
    this.mCallbacks = paramCallbacks;
  }
  
  public void addQsTile(ComponentName paramComponentName)
  {
    synchronized (this.mLock)
    {
      this.mHandler.obtainMessage(1769472, paramComponentName).sendToTarget();
      return;
    }
  }
  
  public void animateCollapsePanels()
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(262144);
      this.mHandler.sendEmptyMessage(262144);
      return;
    }
  }
  
  public void animateExpandNotificationsPanel(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(196608);
      this.mHandler.obtainMessage(196608, paramInt, 0, null).sendToTarget();
      return;
    }
  }
  
  public void animateExpandSettingsPanel(String paramString)
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(327680);
      this.mHandler.obtainMessage(327680, paramString).sendToTarget();
      return;
    }
  }
  
  public void appTransitionCancelled()
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(1245184);
      this.mHandler.sendEmptyMessage(1245184);
      return;
    }
  }
  
  public void appTransitionFinished()
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(2031616);
      this.mHandler.sendEmptyMessage(2031616);
      return;
    }
  }
  
  public void appTransitionPending()
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(1245184);
      this.mHandler.sendEmptyMessage(1245184);
      return;
    }
  }
  
  public void appTransitionStarting(long paramLong1, long paramLong2)
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(1376256);
      this.mHandler.obtainMessage(1376256, Pair.create(Long.valueOf(paramLong1), Long.valueOf(paramLong2))).sendToTarget();
      return;
    }
  }
  
  public void buzzBeepBlinked()
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(983040);
      this.mHandler.sendEmptyMessage(983040);
      return;
    }
  }
  
  public void cancelPreloadRecentApps()
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(720896);
      this.mHandler.obtainMessage(720896, 0, 0, null).sendToTarget();
      return;
    }
  }
  
  public void clickQsTile(ComponentName paramComponentName)
  {
    synchronized (this.mLock)
    {
      this.mHandler.obtainMessage(1900544, paramComponentName).sendToTarget();
      return;
    }
  }
  
  public void disable(int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(131072);
      this.mHandler.obtainMessage(131072, paramInt1, paramInt2, null).sendToTarget();
      return;
    }
  }
  
  public void dismissKeyboardShortcutsMenu()
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(2097152);
      this.mHandler.obtainMessage(2097152).sendToTarget();
      return;
    }
  }
  
  public List<String> getLockedPackageList()
  {
    return this.mCallbacks.getLockedPackageList();
  }
  
  public void handleSystemNavigationKey(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mHandler.obtainMessage(2162688, paramInt, 0).sendToTarget();
      return;
    }
  }
  
  public void hideRecentApps(boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = 1;
    for (;;)
    {
      synchronized (this.mLock)
      {
        this.mHandler.removeMessages(917504);
        Handler localHandler = this.mHandler;
        int i;
        if (paramBoolean1)
        {
          i = 1;
          break label73;
          localHandler.obtainMessage(917504, i, j, null).sendToTarget();
        }
        else
        {
          i = 0;
          break label73;
          j = 0;
        }
      }
      label73:
      if (!paramBoolean2) {}
    }
  }
  
  public void notificationLightOff()
  {
    synchronized (this.mLock)
    {
      this.mHandler.sendEmptyMessage(1048576);
      return;
    }
  }
  
  public void notificationLightPulse(int paramInt1, int paramInt2, int paramInt3)
  {
    synchronized (this.mLock)
    {
      this.mHandler.obtainMessage(1114112, paramInt2, paramInt3, Integer.valueOf(paramInt1)).sendToTarget();
      return;
    }
  }
  
  public void notifyNavBarColorChanged(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(2228224);
      this.mHandler.obtainMessage(2228224, paramInt, 0, null).sendToTarget();
      return;
    }
  }
  
  public void onCameraLaunchGestureDetected(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(1572864);
      this.mHandler.obtainMessage(1572864, paramInt, 0).sendToTarget();
      return;
    }
  }
  
  public void preloadRecentApps()
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(655360);
      this.mHandler.obtainMessage(655360, 0, 0, null).sendToTarget();
      return;
    }
  }
  
  public void remQsTile(ComponentName paramComponentName)
  {
    synchronized (this.mLock)
    {
      this.mHandler.obtainMessage(1835008, paramComponentName).sendToTarget();
      return;
    }
  }
  
  public void removeIcon(String paramString)
  {
    synchronized (this.mLock)
    {
      this.mHandler.obtainMessage(65536, 2, 0, paramString).sendToTarget();
      return;
    }
  }
  
  public void setIcon(String paramString, StatusBarIcon paramStatusBarIcon)
  {
    synchronized (this.mLock)
    {
      this.mHandler.obtainMessage(65536, 1, 0, new Pair(paramString, paramStatusBarIcon)).sendToTarget();
      return;
    }
  }
  
  public void setImeWindowStatus(IBinder paramIBinder, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(524288);
      paramIBinder = this.mHandler.obtainMessage(524288, paramInt1, paramInt2, paramIBinder);
      paramIBinder.getData().putBoolean("showImeSwitcherKey", paramBoolean);
      paramIBinder.sendToTarget();
      return;
    }
  }
  
  public void setSystemUiVisibility(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rect paramRect1, Rect paramRect2)
  {
    synchronized (this.mLock)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.argi1 = paramInt1;
      localSomeArgs.argi2 = paramInt2;
      localSomeArgs.argi3 = paramInt3;
      localSomeArgs.argi4 = paramInt4;
      localSomeArgs.arg1 = paramRect1;
      localSomeArgs.arg2 = paramRect2;
      this.mHandler.obtainMessage(393216, localSomeArgs).sendToTarget();
      return;
    }
  }
  
  public void setWindowState(int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      this.mHandler.obtainMessage(786432, paramInt1, paramInt2, null).sendToTarget();
      return;
    }
  }
  
  public void showAssistDisclosure()
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(1441792);
      this.mHandler.obtainMessage(1441792).sendToTarget();
      return;
    }
  }
  
  public void showRecentApps(boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = 1;
    for (;;)
    {
      synchronized (this.mLock)
      {
        this.mHandler.removeMessages(851968);
        Handler localHandler = this.mHandler;
        int i;
        if (paramBoolean1)
        {
          i = 1;
          break label73;
          localHandler.obtainMessage(851968, i, j, null).sendToTarget();
        }
        else
        {
          i = 0;
          break label73;
          j = 0;
        }
      }
      label73:
      if (!paramBoolean2) {}
    }
  }
  
  public void showScreenPinningRequest(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mHandler.obtainMessage(1179648, paramInt, 0, null).sendToTarget();
      return;
    }
  }
  
  public void showTvPictureInPictureMenu()
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(1703936);
      this.mHandler.obtainMessage(1703936).sendToTarget();
      return;
    }
  }
  
  public void startAssist(Bundle paramBundle)
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(1507328);
      this.mHandler.obtainMessage(1507328, paramBundle).sendToTarget();
      return;
    }
  }
  
  public void toggleKeyboardShortcutsMenu(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(1638400);
      this.mHandler.obtainMessage(1638400, paramInt, 0).sendToTarget();
      return;
    }
  }
  
  public void toggleRecentApps()
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(589824);
      this.mHandler.obtainMessage(589824, 0, 0, null).sendToTarget();
      return;
    }
  }
  
  public void toggleSplitScreen()
  {
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(1966080);
      this.mHandler.obtainMessage(1966080, 0, 0, null).sendToTarget();
      return;
    }
  }
  
  public void topAppWindowChanged(boolean paramBoolean)
  {
    int i = 0;
    synchronized (this.mLock)
    {
      this.mHandler.removeMessages(458752);
      Handler localHandler = this.mHandler;
      if (paramBoolean) {
        i = 1;
      }
      localHandler.obtainMessage(458752, i, 0, null).sendToTarget();
      return;
    }
  }
  
  public static abstract interface Callbacks
  {
    public abstract void addQsTile(ComponentName paramComponentName);
    
    public abstract void animateCollapsePanels(int paramInt);
    
    public abstract void animateExpandNotificationsPanel(int paramInt);
    
    public abstract void animateExpandSettingsPanel(String paramString);
    
    public abstract void appTransitionCancelled();
    
    public abstract void appTransitionFinished();
    
    public abstract void appTransitionPending();
    
    public abstract void appTransitionStarting(long paramLong1, long paramLong2);
    
    public abstract void buzzBeepBlinked();
    
    public abstract void cancelPreloadRecentApps();
    
    public abstract void clickTile(ComponentName paramComponentName);
    
    public abstract void disable(int paramInt1, int paramInt2, boolean paramBoolean);
    
    public abstract void dismissKeyboardShortcutsMenu();
    
    public abstract List<String> getLockedPackageList();
    
    public abstract void handleSystemNavigationKey(int paramInt);
    
    public abstract void hideRecentApps(boolean paramBoolean1, boolean paramBoolean2);
    
    public abstract void notificationLightOff();
    
    public abstract void notificationLightPulse(int paramInt1, int paramInt2, int paramInt3);
    
    public void notifyNavBarColorChanged(int paramInt) {}
    
    public abstract void onCameraLaunchGestureDetected(int paramInt);
    
    public abstract void preloadRecentApps();
    
    public abstract void remQsTile(ComponentName paramComponentName);
    
    public abstract void removeIcon(String paramString);
    
    public abstract void setIcon(String paramString, StatusBarIcon paramStatusBarIcon);
    
    public abstract void setImeWindowStatus(IBinder paramIBinder, int paramInt1, int paramInt2, boolean paramBoolean);
    
    public abstract void setSystemUiVisibility(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rect paramRect1, Rect paramRect2);
    
    public abstract void setWindowState(int paramInt1, int paramInt2);
    
    public abstract void showAssistDisclosure();
    
    public abstract void showRecentApps(boolean paramBoolean1, boolean paramBoolean2);
    
    public abstract void showScreenPinningRequest(int paramInt);
    
    public abstract void showTvPictureInPictureMenu();
    
    public abstract void startAssist(Bundle paramBundle);
    
    public abstract void toggleKeyboardShortcutsMenu(int paramInt);
    
    public abstract void toggleRecentApps();
    
    public abstract void toggleSplitScreen();
    
    public abstract void topAppWindowChanged(boolean paramBoolean);
  }
  
  private final class H
    extends Handler
  {
    private H() {}
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool2 = true;
      boolean bool3 = true;
      boolean bool1 = true;
      CommandQueue.Callbacks localCallbacks;
      switch (paramMessage.what & 0xFFFF0000)
      {
      default: 
        return;
      case 65536: 
        switch (paramMessage.arg1)
        {
        default: 
          return;
        case 1: 
          paramMessage = (Pair)paramMessage.obj;
          CommandQueue.-get0(CommandQueue.this).setIcon((String)paramMessage.first, (StatusBarIcon)paramMessage.second);
          return;
        }
        CommandQueue.-get0(CommandQueue.this).removeIcon((String)paramMessage.obj);
        return;
      case 131072: 
        CommandQueue.-get0(CommandQueue.this).disable(paramMessage.arg1, paramMessage.arg2, true);
        return;
      case 196608: 
        CommandQueue.-get0(CommandQueue.this).animateExpandNotificationsPanel(paramMessage.arg1);
        return;
      case 262144: 
        CommandQueue.-get0(CommandQueue.this).animateCollapsePanels(0);
        return;
      case 327680: 
        CommandQueue.-get0(CommandQueue.this).animateExpandSettingsPanel((String)paramMessage.obj);
        return;
      case 393216: 
        paramMessage = (SomeArgs)paramMessage.obj;
        CommandQueue.-get0(CommandQueue.this).setSystemUiVisibility(paramMessage.argi1, paramMessage.argi2, paramMessage.argi3, paramMessage.argi4, (Rect)paramMessage.arg1, (Rect)paramMessage.arg2);
        paramMessage.recycle();
        return;
      case 458752: 
        localCallbacks = CommandQueue.-get0(CommandQueue.this);
        if (paramMessage.arg1 != 0) {}
        for (;;)
        {
          localCallbacks.topAppWindowChanged(bool1);
          return;
          bool1 = false;
        }
      case 524288: 
        CommandQueue.-get0(CommandQueue.this).setImeWindowStatus((IBinder)paramMessage.obj, paramMessage.arg1, paramMessage.arg2, paramMessage.getData().getBoolean("showImeSwitcherKey", false));
        return;
      case 851968: 
        localCallbacks = CommandQueue.-get0(CommandQueue.this);
        if (paramMessage.arg1 != 0)
        {
          bool1 = true;
          if (paramMessage.arg2 == 0) {
            break label616;
          }
        }
        for (;;)
        {
          localCallbacks.showRecentApps(bool1, bool2);
          return;
          bool1 = false;
          break;
          bool2 = false;
        }
      case 917504: 
        localCallbacks = CommandQueue.-get0(CommandQueue.this);
        if (paramMessage.arg1 != 0)
        {
          bool1 = true;
          if (paramMessage.arg2 == 0) {
            break label664;
          }
        }
        for (bool2 = bool3;; bool2 = false)
        {
          localCallbacks.hideRecentApps(bool1, bool2);
          return;
          bool1 = false;
          break;
        }
      case 589824: 
        CommandQueue.-get0(CommandQueue.this).toggleRecentApps();
        return;
      case 655360: 
        CommandQueue.-get0(CommandQueue.this).preloadRecentApps();
        return;
      case 720896: 
        CommandQueue.-get0(CommandQueue.this).cancelPreloadRecentApps();
        return;
      case 2097152: 
        CommandQueue.-get0(CommandQueue.this).dismissKeyboardShortcutsMenu();
        return;
      case 1638400: 
        CommandQueue.-get0(CommandQueue.this).toggleKeyboardShortcutsMenu(paramMessage.arg1);
        return;
      case 786432: 
        CommandQueue.-get0(CommandQueue.this).setWindowState(paramMessage.arg1, paramMessage.arg2);
        return;
      case 983040: 
        CommandQueue.-get0(CommandQueue.this).buzzBeepBlinked();
        return;
      case 1048576: 
        CommandQueue.-get0(CommandQueue.this).notificationLightOff();
        return;
      case 1114112: 
        CommandQueue.-get0(CommandQueue.this).notificationLightPulse(((Integer)paramMessage.obj).intValue(), paramMessage.arg1, paramMessage.arg2);
        return;
      case 1179648: 
        CommandQueue.-get0(CommandQueue.this).showScreenPinningRequest(paramMessage.arg1);
        return;
      case 1245184: 
        CommandQueue.-get0(CommandQueue.this).appTransitionPending();
        return;
      case 1310720: 
        CommandQueue.-get0(CommandQueue.this).appTransitionCancelled();
        return;
      case 1376256: 
        paramMessage = (Pair)paramMessage.obj;
        CommandQueue.-get0(CommandQueue.this).appTransitionStarting(((Long)paramMessage.first).longValue(), ((Long)paramMessage.second).longValue());
        return;
      case 2031616: 
        CommandQueue.-get0(CommandQueue.this).appTransitionFinished();
        return;
      case 1441792: 
        CommandQueue.-get0(CommandQueue.this).showAssistDisclosure();
        return;
      case 1507328: 
        CommandQueue.-get0(CommandQueue.this).startAssist((Bundle)paramMessage.obj);
        return;
      case 1572864: 
        CommandQueue.-get0(CommandQueue.this).onCameraLaunchGestureDetected(paramMessage.arg1);
        return;
      case 1703936: 
        CommandQueue.-get0(CommandQueue.this).showTvPictureInPictureMenu();
        return;
      case 1769472: 
        CommandQueue.-get0(CommandQueue.this).addQsTile((ComponentName)paramMessage.obj);
        return;
      case 1835008: 
        CommandQueue.-get0(CommandQueue.this).remQsTile((ComponentName)paramMessage.obj);
        return;
      case 1900544: 
        CommandQueue.-get0(CommandQueue.this).clickTile((ComponentName)paramMessage.obj);
        return;
      case 1966080: 
        CommandQueue.-get0(CommandQueue.this).toggleSplitScreen();
        return;
      case 2162688: 
        label616:
        label664:
        CommandQueue.-get0(CommandQueue.this).handleSystemNavigationKey(paramMessage.arg1);
        return;
      }
      CommandQueue.-get0(CommandQueue.this).notifyNavBarColorChanged(paramMessage.arg1);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\CommandQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */