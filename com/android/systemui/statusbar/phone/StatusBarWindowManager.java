package com.android.systemui.statusbar.phone;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import com.android.systemui.statusbar.BaseStatusBar;
import com.android.systemui.statusbar.RemoteInputController.Callback;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Field;

public class StatusBarWindowManager
  implements RemoteInputController.Callback
{
  private final IActivityManager mActivityManager;
  private int mBarHeight;
  private final Context mContext;
  private final State mCurrentState = new State(null);
  private boolean mHasTopUi;
  private boolean mHasTopUiChanged;
  private final boolean mKeyguardScreenRotation;
  private WindowManager.LayoutParams mLp;
  private WindowManager.LayoutParams mLpChanged;
  private final float mScreenBrightnessDoze;
  private View mStatusBarView;
  private final WindowManager mWindowManager;
  
  public StatusBarWindowManager(Context paramContext)
  {
    this.mContext = paramContext;
    this.mWindowManager = ((WindowManager)paramContext.getSystemService("window"));
    this.mActivityManager = ActivityManagerNative.getDefault();
    this.mKeyguardScreenRotation = shouldEnableKeyguardScreenRotation();
    this.mScreenBrightnessDoze = (this.mContext.getResources().getInteger(17694823) / 255.0F);
  }
  
  private void adjustScreenOrientation(State paramState)
  {
    if (State.-wrap0(paramState))
    {
      if (this.mKeyguardScreenRotation)
      {
        this.mLpChanged.screenOrientation = 2;
        return;
      }
      this.mLpChanged.screenOrientation = 5;
      return;
    }
    this.mLpChanged.screenOrientation = -1;
  }
  
  private void apply(State paramState)
  {
    applyKeyguardFlags(paramState);
    applyForceStatusBarVisibleFlag(paramState);
    applyFocusableFlag(paramState);
    adjustScreenOrientation(paramState);
    applyHeight(paramState);
    applyUserActivityTimeout(paramState);
    applyInputFeatures(paramState);
    applyFitsSystemWindows(paramState);
    applyModalFlag(paramState);
    applyBrightness(paramState);
    applyHasTopUi(paramState);
    if (this.mLp.copyFrom(this.mLpChanged) != 0) {
      this.mWindowManager.updateViewLayout(this.mStatusBarView, this.mLp);
    }
    if (this.mHasTopUi != this.mHasTopUiChanged) {}
    try
    {
      this.mActivityManager.setHasTopUi(this.mHasTopUiChanged);
      this.mHasTopUi = this.mHasTopUiChanged;
      return;
    }
    catch (RemoteException paramState)
    {
      for (;;)
      {
        Log.e("StatusBarWindowManager", "Failed to call setHasTopUi", paramState);
      }
    }
  }
  
  private void applyBrightness(State paramState)
  {
    if (paramState.forceDozeBrightness)
    {
      this.mLpChanged.screenBrightness = this.mScreenBrightnessDoze;
      return;
    }
    this.mLpChanged.screenBrightness = -1.0F;
  }
  
  private void applyFitsSystemWindows(State paramState)
  {
    if (State.-wrap0(paramState)) {}
    for (boolean bool = false;; bool = true)
    {
      if (this.mStatusBarView.getFitsSystemWindows() != bool)
      {
        this.mStatusBarView.setFitsSystemWindows(bool);
        this.mStatusBarView.requestApplyInsets();
      }
      return;
    }
  }
  
  private void applyFocusableFlag(State paramState)
  {
    boolean bool;
    WindowManager.LayoutParams localLayoutParams;
    if (paramState.statusBarFocusable)
    {
      bool = paramState.panelExpanded;
      if (((!paramState.keyguardShowing) || (!paramState.keyguardNeedsInput) || (!paramState.bouncerShowing)) && ((!BaseStatusBar.ENABLE_REMOTE_INPUT) || (!paramState.remoteInputActive))) {
        break label163;
      }
      localLayoutParams = this.mLpChanged;
      localLayoutParams.flags &= 0xFFFFFFF7;
      localLayoutParams = this.mLpChanged;
      localLayoutParams.flags &= 0xFFFDFFFF;
    }
    for (;;)
    {
      Log.d("StatusBarWindowManager", "applyFocusableFlag: " + paramState.statusBarFocusable + ", " + paramState.panelExpanded + ", " + State.-wrap0(paramState) + ", " + Integer.toHexString(this.mLpChanged.flags));
      this.mLpChanged.softInputMode = 16;
      return;
      bool = false;
      break;
      label163:
      if ((State.-wrap0(paramState)) || (bool))
      {
        localLayoutParams = this.mLpChanged;
        localLayoutParams.flags &= 0xFFFFFFF7;
        localLayoutParams = this.mLpChanged;
        localLayoutParams.flags |= 0x20000;
      }
      else
      {
        localLayoutParams = this.mLpChanged;
        localLayoutParams.flags |= 0x8;
        localLayoutParams = this.mLpChanged;
        localLayoutParams.flags &= 0xFFFDFFFF;
      }
    }
  }
  
  private void applyForceStatusBarVisibleFlag(State paramState)
  {
    if (paramState.forceStatusBarVisible)
    {
      paramState = this.mLpChanged;
      paramState.privateFlags |= 0x1000;
      return;
    }
    paramState = this.mLpChanged;
    paramState.privateFlags &= 0xEFFF;
  }
  
  private void applyHasTopUi(State paramState)
  {
    this.mHasTopUiChanged = isExpanded(paramState);
  }
  
  private void applyHeight(State paramState)
  {
    if (isExpanded(paramState))
    {
      this.mLpChanged.height = -1;
      return;
    }
    this.mLpChanged.height = this.mBarHeight;
  }
  
  private void applyInputFeatures(State paramState)
  {
    if ((!State.-wrap0(paramState)) || (paramState.statusBarState != 1) || (paramState.qsExpanded)) {}
    while (paramState.forceUserActivity)
    {
      paramState = this.mLpChanged;
      paramState.inputFeatures &= 0xFFFFFFFB;
      return;
    }
    paramState = this.mLpChanged;
    paramState.inputFeatures |= 0x4;
  }
  
  private void applyKeyguardFlags(State paramState)
  {
    WindowManager.LayoutParams localLayoutParams;
    if (paramState.keyguardShowing)
    {
      localLayoutParams = this.mLpChanged;
      localLayoutParams.privateFlags |= 0x400;
      if ((paramState.keyguardShowing) && (!paramState.backdropShowing)) {
        break label76;
      }
    }
    label76:
    while (paramState.hasWallpaper)
    {
      paramState = this.mLpChanged;
      paramState.flags &= 0xFFEFFFFF;
      return;
      localLayoutParams = this.mLpChanged;
      localLayoutParams.privateFlags &= 0xFBFF;
      break;
    }
    paramState = this.mLpChanged;
    paramState.flags |= 0x100000;
  }
  
  private void applyModalFlag(State paramState)
  {
    if (paramState.headsUpShowing)
    {
      paramState = this.mLpChanged;
      paramState.flags |= 0x20;
      return;
    }
    paramState = this.mLpChanged;
    paramState.flags &= 0xFFFFFFDF;
  }
  
  private void applyUserActivityTimeout(State paramState)
  {
    if ((!State.-wrap0(paramState)) || (paramState.statusBarState != 1) || (paramState.qsExpanded))
    {
      this.mLpChanged.userActivityTimeout = -1L;
      return;
    }
    this.mLpChanged.userActivityTimeout = 10000L;
  }
  
  private boolean isExpanded(State paramState)
  {
    if (!paramState.forceCollapsed)
    {
      if ((!State.-wrap0(paramState)) && (!paramState.panelVisible) && (!paramState.keyguardFadingAway) && (!paramState.bouncerShowing)) {
        return paramState.headsUpShowing;
      }
      return true;
    }
    return false;
  }
  
  private boolean shouldEnableKeyguardScreenRotation()
  {
    Resources localResources = this.mContext.getResources();
    if (!SystemProperties.getBoolean("lockscreen.rot_override", false)) {
      return localResources.getBoolean(2131558453);
    }
    return true;
  }
  
  public void add(View paramView, int paramInt)
  {
    this.mLp = new WindowManager.LayoutParams(-1, paramInt, 2000, -2138832824, -3);
    WindowManager.LayoutParams localLayoutParams = this.mLp;
    localLayoutParams.flags |= 0x1000000;
    this.mLp.gravity = 48;
    this.mLp.softInputMode = 16;
    this.mLp.setTitle("StatusBar");
    this.mLp.packageName = this.mContext.getPackageName();
    this.mStatusBarView = paramView;
    this.mBarHeight = paramInt;
    this.mWindowManager.addView(this.mStatusBarView, this.mLp);
    this.mLpChanged = new WindowManager.LayoutParams();
    this.mLpChanged.copyFrom(this.mLp);
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("StatusBarWindowManager state:");
    paramPrintWriter.println(this.mCurrentState);
  }
  
  public boolean isShowingLiveWallpaper()
  {
    return !this.mCurrentState.hasWallpaper;
  }
  
  public boolean isShowingWallpaper()
  {
    return !this.mCurrentState.backdropShowing;
  }
  
  public void onRemoteInputActive(boolean paramBoolean)
  {
    this.mCurrentState.remoteInputActive = paramBoolean;
    apply(this.mCurrentState);
  }
  
  public void setBackdropShowing(boolean paramBoolean)
  {
    this.mCurrentState.backdropShowing = paramBoolean;
    apply(this.mCurrentState);
  }
  
  public void setBarHeight(int paramInt)
  {
    this.mBarHeight = paramInt;
    apply(this.mCurrentState);
  }
  
  public void setBouncerShowing(boolean paramBoolean)
  {
    this.mCurrentState.bouncerShowing = paramBoolean;
    apply(this.mCurrentState);
  }
  
  public void setForceDozeBrightness(boolean paramBoolean)
  {
    this.mCurrentState.forceDozeBrightness = paramBoolean;
    apply(this.mCurrentState);
  }
  
  public void setForceStatusBarVisible(boolean paramBoolean)
  {
    this.mCurrentState.forceStatusBarVisible = paramBoolean;
    apply(this.mCurrentState);
  }
  
  public void setForceWindowCollapsed(boolean paramBoolean)
  {
    this.mCurrentState.forceCollapsed = paramBoolean;
    apply(this.mCurrentState);
  }
  
  public void setHeadsUpShowing(boolean paramBoolean)
  {
    this.mCurrentState.headsUpShowing = paramBoolean;
    apply(this.mCurrentState);
  }
  
  public void setKeyguardFadingAway(boolean paramBoolean)
  {
    this.mCurrentState.keyguardFadingAway = paramBoolean;
    apply(this.mCurrentState);
  }
  
  public void setKeyguardNeedsInput(boolean paramBoolean)
  {
    this.mCurrentState.keyguardNeedsInput = paramBoolean;
    apply(this.mCurrentState);
  }
  
  public void setKeyguardOccluded(boolean paramBoolean)
  {
    this.mCurrentState.keyguardOccluded = paramBoolean;
    apply(this.mCurrentState);
  }
  
  public void setKeyguardShowing(boolean paramBoolean)
  {
    this.mCurrentState.keyguardShowing = paramBoolean;
    apply(this.mCurrentState);
  }
  
  public void setLockscreenWallpaper(boolean paramBoolean)
  {
    Log.d("StatusBarWindowManager", "LockscreenWP: " + paramBoolean);
    this.mCurrentState.hasWallpaper = paramBoolean;
    apply(this.mCurrentState);
  }
  
  public void setPanelExpanded(boolean paramBoolean)
  {
    this.mCurrentState.panelExpanded = paramBoolean;
    apply(this.mCurrentState);
  }
  
  public void setPanelVisible(boolean paramBoolean)
  {
    this.mCurrentState.panelVisible = paramBoolean;
    this.mCurrentState.statusBarFocusable = paramBoolean;
    apply(this.mCurrentState);
  }
  
  public void setQsExpanded(boolean paramBoolean)
  {
    this.mCurrentState.qsExpanded = paramBoolean;
    apply(this.mCurrentState);
  }
  
  public void setStatusBarFocusable(boolean paramBoolean)
  {
    this.mCurrentState.statusBarFocusable = paramBoolean;
    apply(this.mCurrentState);
  }
  
  public void setStatusBarState(int paramInt)
  {
    this.mCurrentState.statusBarState = paramInt;
    apply(this.mCurrentState);
  }
  
  private static class State
  {
    boolean backdropShowing;
    boolean bouncerShowing;
    boolean forceCollapsed;
    boolean forceDozeBrightness;
    boolean forceStatusBarVisible;
    boolean forceUserActivity;
    boolean hasWallpaper;
    boolean headsUpShowing;
    boolean keyguardFadingAway;
    boolean keyguardNeedsInput;
    boolean keyguardOccluded;
    boolean keyguardShowing;
    boolean panelExpanded;
    boolean panelVisible;
    boolean qsExpanded;
    boolean remoteInputActive;
    boolean statusBarFocusable;
    int statusBarState;
    
    private boolean isKeyguardShowingAndNotOccluded()
    {
      return (this.keyguardShowing) && (!this.keyguardOccluded);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Window State {");
      localStringBuilder.append("\n");
      Field[] arrayOfField = getClass().getDeclaredFields();
      int i = 0;
      int j = arrayOfField.length;
      for (;;)
      {
        Field localField;
        if (i < j)
        {
          localField = arrayOfField[i];
          localStringBuilder.append("  ");
        }
        try
        {
          localStringBuilder.append(localField.getName());
          localStringBuilder.append(": ");
          localStringBuilder.append(localField.get(this));
          localStringBuilder.append("\n");
          i += 1;
          continue;
          localStringBuilder.append("}");
          return localStringBuilder.toString();
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          for (;;) {}
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\StatusBarWindowManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */