package com.android.systemui.statusbar.phone;

import android.animation.LayoutTransition;
import android.animation.LayoutTransition.TransitionListener;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings.System;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.IDockedStackListener.Stub;
import android.view.IWindowManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerGlobal;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.RecentsComponent;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.statusbar.policy.DeadZone;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.KeyButtonView;
import com.android.systemui.util.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class NavigationBarView
  extends LinearLayout
{
  private final int COLOR_BACKGROUND_DARK;
  private final int COLOR_BACKGROUND_LIGHT;
  private final int COLOR_BACKGROUND_TRANSPARENT;
  private final int COLOR_KEY_DARK;
  private final int COLOR_KEY_LIGHT;
  private final int COLOR_KEY_TRANSPARENT;
  private final int WALLPAPER_NORMAL = 0;
  private final int WALLPAPER_SUPER_DARK = 2;
  private final int WALLPAPER_SUPER_LIGHT = 1;
  private boolean isHideNavBarOn = false;
  private Drawable mBackAltCarModeIcon;
  private Drawable mBackAltIcon;
  private Drawable mBackAltLandCarModeIcon;
  private Drawable mBackAltLandIcon;
  private Drawable mBackCarModeIcon;
  private Drawable mBackIcon;
  private Drawable mBackLandCarModeIcon;
  private Drawable mBackLandIcon;
  private int mBackgroundColor = 0;
  int mBarSize;
  private final NavigationBarTransitions mBarTransitions;
  private final SparseArray<ButtonDispatcher> mButtonDisatchers = new SparseArray();
  private Configuration mConfiguration;
  private int mCurrentRotation = -1;
  View mCurrentView = null;
  private DeadZone mDeadZone;
  int mDisabledFlags = 0;
  final Display mDisplay;
  private Drawable mDockedIcon;
  private boolean mDockedStackExists;
  private NavigationBarGestureHelper mGestureHelper;
  private H mHandler = new H(null);
  private HeadsUpManager mHeadsUpManager;
  private Drawable mHomeCarModeIcon;
  private Drawable mHomeDefaultIcon;
  private Drawable mImeIcon;
  private boolean mImeShow = false;
  private final View.OnClickListener mImeSwitcherClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      ((InputMethodManager)NavigationBarView.-get0(NavigationBarView.this).getSystemService(InputMethodManager.class)).showInputMethodPicker(true);
    }
  };
  private boolean mInCarMode = false;
  private boolean mIsImmersiveSticky = false;
  private boolean mKeyguardShow = false;
  private int mLastButtonColor = 0;
  private int mLastRippleColor = 0;
  private boolean mLayoutTransitionsEnabled = true;
  private Drawable mMenuIcon;
  int mNavigationIconHints = 0;
  private NavigationBarInflaterView mNavigationInflaterView;
  private OnVerticalChangedListener mOnVerticalChangedListener;
  private Drawable mRecentIcon;
  View[] mRotatedViews = new View[4];
  boolean mScreenOn;
  boolean mShowMenu;
  private boolean mShowNavKey = false;
  private boolean mStatusBarExpanded = false;
  private final NavTransitionListener mTransitionListener = new NavTransitionListener(null);
  private boolean mUseCarModeUi = false;
  boolean mVertical;
  private boolean mWakeAndUnlocking;
  private int mWallpaperState = 0;
  
  public NavigationBarView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mDisplay = ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay();
    this.mBarSize = paramContext.getResources().getDimensionPixelSize(2131755355);
    this.mVertical = false;
    this.mShowMenu = false;
    this.mGestureHelper = new NavigationBarGestureHelper(paramContext);
    this.mConfiguration = new Configuration();
    this.mConfiguration.updateFrom(paramContext.getResources().getConfiguration());
    updateIcons(paramContext, Configuration.EMPTY, this.mConfiguration);
    this.mBarTransitions = new NavigationBarTransitions(this);
    this.mButtonDisatchers.put(2131951793, new ButtonDispatcher(2131951793));
    this.mButtonDisatchers.put(2131951635, new ButtonDispatcher(2131951635));
    this.mButtonDisatchers.put(2131952180, new ButtonDispatcher(2131952180));
    this.mButtonDisatchers.put(2131952055, new ButtonDispatcher(2131952055));
    this.mButtonDisatchers.put(2131952056, new ButtonDispatcher(2131952056));
    if (Utils.isSupportHideNavBar()) {
      this.mButtonDisatchers.put(2131952081, new ButtonDispatcher(2131952081));
    }
    this.COLOR_BACKGROUND_LIGHT = paramContext.getColor(2131493129);
    this.COLOR_BACKGROUND_DARK = paramContext.getColor(2131493130);
    this.COLOR_BACKGROUND_TRANSPARENT = paramContext.getColor(2131493131);
    this.COLOR_KEY_LIGHT = paramContext.getColor(2131493132);
    this.COLOR_KEY_DARK = paramContext.getColor(2131493133);
    this.COLOR_KEY_TRANSPARENT = paramContext.getColor(2131493134);
  }
  
  private void applyAppCustomColor()
  {
    if ((Utils.isHomeApp()) && (this.mWallpaperState != 0))
    {
      if (this.mWallpaperState == 1) {
        updateButtonColor(this.COLOR_KEY_LIGHT, -16777216);
      }
      while (this.mWallpaperState != 2) {
        return;
      }
      updateButtonColor(this.COLOR_KEY_DARK, -1);
      return;
    }
    if (this.mBackgroundColor == 0)
    {
      updateButtonColor(this.COLOR_KEY_TRANSPARENT, -1);
      return;
    }
    if (isLightColor(this.mBackgroundColor))
    {
      updateButtonColor(this.COLOR_KEY_LIGHT, -16777216);
      return;
    }
    if (isDarkColor(this.mBackgroundColor))
    {
      updateButtonColor(this.COLOR_KEY_DARK, -1);
      return;
    }
    updateButtonColor(this.COLOR_KEY_TRANSPARENT, -1);
  }
  
  private static void dumpButton(PrintWriter paramPrintWriter, String paramString, ButtonDispatcher paramButtonDispatcher)
  {
    paramPrintWriter.print("      " + paramString + ": ");
    if (paramButtonDispatcher == null) {
      paramPrintWriter.print("null");
    }
    for (;;)
    {
      paramPrintWriter.println();
      return;
      paramPrintWriter.print(visibilityToString(paramButtonDispatcher.getVisibility()) + " alpha=" + paramButtonDispatcher.getAlpha());
    }
  }
  
  private Drawable getBackIcon(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean2)
    {
      if (paramBoolean1) {
        return this.mBackLandCarModeIcon;
      }
      return this.mBackLandIcon;
    }
    if (paramBoolean1) {
      return this.mBackCarModeIcon;
    }
    return this.mBackIcon;
  }
  
  private Drawable getBackIconWithAlt(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean2)
    {
      if (paramBoolean1) {
        return this.mBackAltLandCarModeIcon;
      }
      return this.mBackAltLandIcon;
    }
    if (paramBoolean1) {
      return this.mBackAltCarModeIcon;
    }
    return this.mBackAltIcon;
  }
  
  private String getResourceName(int paramInt)
  {
    if (paramInt != 0)
    {
      Object localObject = getContext().getResources();
      try
      {
        localObject = ((Resources)localObject).getResourceName(paramInt);
        return (String)localObject;
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        return "(unknown)";
      }
    }
    return "(null)";
  }
  
  private boolean hasPinnedHeadsUp()
  {
    if (this.mHeadsUpManager != null) {
      return this.mHeadsUpManager.hasPinnedHeadsUp();
    }
    return false;
  }
  
  private boolean inLockTask()
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isInLockTaskMode();
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  private boolean isDarkColor(int paramInt)
  {
    return !isLegible(-16777216, paramInt);
  }
  
  private boolean isLegible(int paramInt1, int paramInt2)
  {
    return ColorUtils.calculateContrast(paramInt1, ColorUtils.setAlphaComponent(paramInt2, 255)) >= 2.0D;
  }
  
  private boolean isLightColor(int paramInt)
  {
    return !isLegible(-1, paramInt);
  }
  
  private void notifyNavBarColorChange(int paramInt, boolean paramBoolean)
  {
    int i = 0;
    ButtonDispatcher localButtonDispatcher = getNavButton();
    if (showNavKey()) {}
    for (;;)
    {
      localButtonDispatcher.setVisibility(i);
      i = this.mBarTransitions.getMode();
      if (Build.DEBUG_ONEPLUS) {
        Log.d("StatusBar/NavBarView", "barMode =" + i + ", mImeShow =" + this.mImeShow + ", mKeyguardShow =" + this.mKeyguardShow + ", expanded=" + paramBoolean + ",hasPinnedHeadsUp()=" + hasPinnedHeadsUp() + "mDockedStackExists =" + this.mDockedStackExists + ", color =0x" + Integer.toHexString(paramInt));
      }
      if (!this.mImeShow) {
        break label182;
      }
      if (i != 2) {
        break;
      }
      updateButtonColor(this.COLOR_KEY_TRANSPARENT, -1);
      return;
      i = 4;
    }
    updateButtonColor(this.mContext.getColor(2131493132), -16777216);
    return;
    label182:
    if (!this.mKeyguardShow) {
      if ((paramBoolean) && (!hasPinnedHeadsUp())) {}
    }
    while ((isScreenSaverOn()) || (i == 0)) {
      do
      {
        this.mBackgroundColor = paramInt;
        if (!this.mDockedStackExists) {
          break;
        }
        updateButtonColor(this.COLOR_KEY_LIGHT, -16777216);
        return;
      } while (this.mDockedStackExists);
    }
    updateButtonColor(this.COLOR_KEY_TRANSPARENT, -1);
    return;
    if ((i == 1) || (i == 2))
    {
      updateButtonColor(this.COLOR_KEY_TRANSPARENT, -1);
      return;
    }
    if (i == 0)
    {
      if (Utils.isScreenCompat())
      {
        updateButtonColor(this.COLOR_KEY_DARK, -1);
        return;
      }
      updateButtonColor(this.COLOR_KEY_LIGHT, -16777216);
      return;
    }
    if ((i == 3) && (isScreenSaverOn()))
    {
      updateButtonColor(this.COLOR_KEY_LIGHT, -16777216);
      return;
    }
    if (this.isHideNavBarOn)
    {
      if (Utils.isHomeApp())
      {
        applyAppCustomColor();
        return;
      }
      updateButtonColor(this.COLOR_KEY_TRANSPARENT, -1);
      return;
    }
    if ((Utils.isHomeApp()) && (this.mWallpaperState != 0))
    {
      applyAppCustomColor();
      return;
    }
    if (Utils.isScreenCompat())
    {
      updateButtonColor(this.COLOR_KEY_DARK, -1);
      return;
    }
    if (this.mBackgroundColor == 0)
    {
      updateButtonColor(this.COLOR_KEY_TRANSPARENT, -1);
      return;
    }
    if (this.mBackgroundColor == this.COLOR_BACKGROUND_LIGHT)
    {
      updateButtonColor(this.COLOR_KEY_LIGHT, -16777216);
      return;
    }
    if ((this.mBackgroundColor == this.COLOR_BACKGROUND_DARK) || (this.mBackgroundColor == -16777216))
    {
      updateButtonColor(this.COLOR_KEY_DARK, -1);
      return;
    }
    applyAppCustomColor();
  }
  
  private void notifyVerticalChangedListener(boolean paramBoolean)
  {
    if (this.mOnVerticalChangedListener != null) {
      this.mOnVerticalChangedListener.onVerticalChanged(paramBoolean);
    }
  }
  
  private void postCheckForInvalidLayout(String paramString)
  {
    this.mHandler.obtainMessage(8686, 0, 0, paramString).sendToTarget();
  }
  
  private void setUseFadingAnimations(boolean paramBoolean)
  {
    WindowManager.LayoutParams localLayoutParams = (WindowManager.LayoutParams)getLayoutParams();
    int i;
    if (localLayoutParams != null)
    {
      if (localLayoutParams.windowAnimations == 0) {
        break label56;
      }
      i = 1;
      if ((i != 0) || (!paramBoolean)) {
        break label61;
      }
    }
    for (localLayoutParams.windowAnimations = 2131821079;; localLayoutParams.windowAnimations = 0)
    {
      ((WindowManager)getContext().getSystemService("window")).updateViewLayout(this, localLayoutParams);
      return;
      label56:
      i = 0;
      break;
      label61:
      if ((i == 0) || (paramBoolean)) {
        return;
      }
    }
  }
  
  private boolean showNavKey()
  {
    if ((Utils.isHomeApp()) || (!this.mShowNavKey) || (Utils.isScreenCompat())) {}
    while ((this.mImeShow) || (this.mKeyguardShow) || (this.mStatusBarExpanded) || (isScreenSaverOn()) || (this.mIsImmersiveSticky) || (this.mDockedStackExists) || (Utils.isSystemUI())) {
      return false;
    }
    return true;
  }
  
  private void updateButtonColor(int paramInt1, int paramInt2)
  {
    updateButtonColor(paramInt1, paramInt2, false);
  }
  
  private void updateButtonColor(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (Build.DEBUG_ONEPLUS) {
      Log.d("StatusBar/NavBarView", "updateButtonColor buttonColor: " + Integer.toHexString(paramInt1) + ", caller =" + Debug.getCallers(5));
    }
    int i;
    if ((paramInt1 != this.mLastButtonColor) || (paramInt2 != this.mLastRippleColor) || (paramBoolean))
    {
      this.mLastButtonColor = paramInt1;
      this.mLastRippleColor = paramInt2;
      i = 0;
    }
    while (i < this.mButtonDisatchers.size())
    {
      ArrayList localArrayList = ((ButtonDispatcher)this.mButtonDisatchers.valueAt(i)).getViews();
      int k = localArrayList.size();
      int j = 0;
      while (j < k)
      {
        KeyButtonView localKeyButtonView = (KeyButtonView)localArrayList.get(j);
        localKeyButtonView.updateThemeColor(paramInt1);
        localKeyButtonView.setRippleColor(paramInt2);
        j += 1;
        continue;
        return;
      }
      i += 1;
    }
    postInvalidate();
  }
  
  private boolean updateCarMode(Configuration paramConfiguration)
  {
    if (paramConfiguration != null) {
      if ((paramConfiguration.uiMode & 0xF) != 3) {
        break label45;
      }
    }
    label45:
    for (boolean bool = true;; bool = false)
    {
      if (bool != this.mInCarMode)
      {
        this.mInCarMode = bool;
        getHomeButton().setCarMode(bool);
        this.mUseCarModeUi = false;
      }
      return false;
    }
  }
  
  private void updateCurrentView()
  {
    boolean bool = true;
    int j = this.mDisplay.getRotation();
    int i = 0;
    while (i < 4)
    {
      this.mRotatedViews[i].setVisibility(8);
      i += 1;
    }
    this.mCurrentView = this.mRotatedViews[j];
    this.mCurrentView.setVisibility(0);
    NavigationBarInflaterView localNavigationBarInflaterView = this.mNavigationInflaterView;
    if (j == 1) {}
    for (;;)
    {
      localNavigationBarInflaterView.setAlternativeOrder(bool);
      i = 0;
      while (i < this.mButtonDisatchers.size())
      {
        ((ButtonDispatcher)this.mButtonDisatchers.valueAt(i)).setCurrentView(this.mCurrentView);
        i += 1;
      }
      bool = false;
    }
    updateLayoutTransitionsEnabled();
    this.mCurrentRotation = j;
  }
  
  private void updateIcons(Context paramContext, Configuration paramConfiguration1, Configuration paramConfiguration2)
  {
    if ((paramConfiguration1.orientation != paramConfiguration2.orientation) || (paramConfiguration1.densityDpi != paramConfiguration2.densityDpi))
    {
      if (Utils.isSupportHideNavBar()) {
        this.mDockedIcon = paramContext.getDrawable(2130837924);
      }
    }
    else if (paramConfiguration1.densityDpi != paramConfiguration2.densityDpi)
    {
      if (!Utils.isSupportHideNavBar()) {
        break label153;
      }
      this.mBackIcon = paramContext.getDrawable(2130837918);
      this.mBackAltIcon = paramContext.getDrawable(2130837921);
      this.mHomeDefaultIcon = paramContext.getDrawable(2130837926);
      this.mRecentIcon = paramContext.getDrawable(2130837935);
    }
    for (this.mMenuIcon = paramContext.getDrawable(2130837931);; this.mMenuIcon = paramContext.getDrawable(2130837930))
    {
      this.mBackLandIcon = this.mBackIcon;
      this.mBackAltLandIcon = this.mBackAltIcon;
      this.mImeIcon = paramContext.getDrawable(2130837732);
      return;
      this.mDockedIcon = paramContext.getDrawable(2130837923);
      break;
      label153:
      this.mBackIcon = paramContext.getDrawable(2130837917);
      this.mBackAltIcon = paramContext.getDrawable(2130837920);
      this.mHomeDefaultIcon = paramContext.getDrawable(2130837925);
      this.mRecentIcon = paramContext.getDrawable(2130837934);
    }
  }
  
  private void updateLayoutTransitionsEnabled()
  {
    if (!this.mWakeAndUnlocking) {}
    LayoutTransition localLayoutTransition;
    for (boolean bool = this.mLayoutTransitionsEnabled;; bool = false)
    {
      localLayoutTransition = ((ViewGroup)getCurrentView().findViewById(2131951802)).getLayoutTransition();
      if (localLayoutTransition != null)
      {
        if (!bool) {
          break;
        }
        localLayoutTransition.enableTransitionType(2);
        localLayoutTransition.enableTransitionType(3);
        localLayoutTransition.enableTransitionType(0);
        localLayoutTransition.enableTransitionType(1);
      }
      return;
    }
    localLayoutTransition.disableTransitionType(2);
    localLayoutTransition.disableTransitionType(3);
    localLayoutTransition.disableTransitionType(0);
    localLayoutTransition.disableTransitionType(1);
  }
  
  private void updateRecentsIcon()
  {
    ButtonDispatcher localButtonDispatcher = getRecentsButton();
    if (this.mDockedStackExists) {}
    for (Drawable localDrawable = this.mDockedIcon;; localDrawable = this.mRecentIcon)
    {
      localButtonDispatcher.setImageDrawable(localDrawable);
      return;
    }
  }
  
  private void updateTaskSwitchHelper()
  {
    if (getLayoutDirection() == 1) {}
    for (boolean bool = true;; bool = false)
    {
      this.mGestureHelper.setBarState(this.mVertical, bool);
      return;
    }
  }
  
  private static String visibilityToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "VISIBLE";
    case 4: 
      return "INVISIBLE";
    }
    return "GONE";
  }
  
  public void abortCurrentGesture()
  {
    getHomeButton().abortCurrentGesture();
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("NavigationBarView {");
    paramFileDescriptor = new Rect();
    paramArrayOfString = new Point();
    this.mDisplay.getRealSize(paramArrayOfString);
    paramPrintWriter.println(String.format("      this: " + PhoneStatusBar.viewInfo(this) + " " + visibilityToString(getVisibility()), new Object[0]));
    getWindowVisibleDisplayFrame(paramFileDescriptor);
    int i;
    if ((paramFileDescriptor.right > paramArrayOfString.x) || (paramFileDescriptor.bottom > paramArrayOfString.y))
    {
      i = 1;
      paramArrayOfString = new StringBuilder().append("      window: ").append(paramFileDescriptor.toShortString()).append(" ").append(visibilityToString(getWindowVisibility()));
      if (i == 0) {
        break label352;
      }
      paramFileDescriptor = " OFFSCREEN!";
      label157:
      paramPrintWriter.println(paramFileDescriptor);
      paramPrintWriter.println(String.format("      mCurrentView: id=%s (%dx%d) %s", new Object[] { getResourceName(getCurrentView().getId()), Integer.valueOf(getCurrentView().getWidth()), Integer.valueOf(getCurrentView().getHeight()), visibilityToString(getCurrentView().getVisibility()) }));
      i = this.mDisabledFlags;
      if (!this.mVertical) {
        break label359;
      }
      paramFileDescriptor = "true";
      label253:
      if (!this.mShowMenu) {
        break label366;
      }
    }
    label352:
    label359:
    label366:
    for (paramArrayOfString = "true";; paramArrayOfString = "false")
    {
      paramPrintWriter.println(String.format("      disabled=0x%08x vertical=%s menu=%s", new Object[] { Integer.valueOf(i), paramFileDescriptor, paramArrayOfString }));
      dumpButton(paramPrintWriter, "back", getBackButton());
      dumpButton(paramPrintWriter, "home", getHomeButton());
      dumpButton(paramPrintWriter, "rcnt", getRecentsButton());
      dumpButton(paramPrintWriter, "menu", getMenuButton());
      paramPrintWriter.println("    }");
      return;
      i = 0;
      break;
      paramFileDescriptor = "";
      break label157;
      paramFileDescriptor = "false";
      break label253;
    }
  }
  
  public ButtonDispatcher getBackButton()
  {
    return (ButtonDispatcher)this.mButtonDisatchers.get(2131951793);
  }
  
  public BarTransitions getBarTransitions()
  {
    return this.mBarTransitions;
  }
  
  public View getCurrentView()
  {
    return this.mCurrentView;
  }
  
  public ButtonDispatcher getHomeButton()
  {
    return (ButtonDispatcher)this.mButtonDisatchers.get(2131951635);
  }
  
  public ButtonDispatcher getImeSwitchButton()
  {
    return (ButtonDispatcher)this.mButtonDisatchers.get(2131952056);
  }
  
  public ButtonDispatcher getMenuButton()
  {
    return (ButtonDispatcher)this.mButtonDisatchers.get(2131952055);
  }
  
  public ButtonDispatcher getNavButton()
  {
    return (ButtonDispatcher)this.mButtonDisatchers.get(2131952081);
  }
  
  public ButtonDispatcher getRecentsButton()
  {
    return (ButtonDispatcher)this.mButtonDisatchers.get(2131952180);
  }
  
  public boolean isScreenSaverOn()
  {
    if (KeyguardUpdateMonitor.getInstance(this.mContext).isDreaming()) {
      return KeyguardUpdateMonitor.getInstance(this.mContext).isScreenOn();
    }
    return false;
  }
  
  public boolean isVertical()
  {
    return this.mVertical;
  }
  
  public boolean needsReorient(int paramInt)
  {
    return this.mCurrentRotation != paramInt;
  }
  
  public void notifyNavBarColorChange(int paramInt)
  {
    notifyNavBarColorChange(paramInt, false);
  }
  
  public void notifyScreenOn(boolean paramBoolean)
  {
    this.mScreenOn = paramBoolean;
    setDisabledFlags(this.mDisabledFlags, true);
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    boolean bool = updateCarMode(paramConfiguration);
    updateTaskSwitchHelper();
    updateIcons(getContext(), this.mConfiguration, paramConfiguration);
    updateRecentsIcon();
    if ((bool) || (this.mConfiguration.densityDpi != paramConfiguration.densityDpi)) {
      setNavigationIconHints(this.mNavigationIconHints, true);
    }
    this.mConfiguration.updateFrom(paramConfiguration);
  }
  
  public void onExpandChanged(boolean paramBoolean)
  {
    this.mStatusBarExpanded = paramBoolean;
    if ((!paramBoolean) || (hasPinnedHeadsUp()))
    {
      notifyNavBarColorChange(this.mBackgroundColor);
      return;
    }
    notifyNavBarColorChange(0, true);
  }
  
  public void onFinishInflate()
  {
    int i = 0;
    this.mNavigationInflaterView = ((NavigationBarInflaterView)findViewById(2131952089));
    updateRotatedViews();
    this.mNavigationInflaterView.setButtonDispatchers(this.mButtonDisatchers);
    getImeSwitchButton().setOnClickListener(this.mImeSwitcherClickListener);
    try
    {
      WindowManagerGlobal.getWindowManagerService().registerDockedStackListener(new IDockedStackListener.Stub()
      {
        public void onAdjustedForImeChanged(boolean paramAnonymousBoolean, long paramAnonymousLong)
          throws RemoteException
        {}
        
        public void onDividerVisibilityChanged(boolean paramAnonymousBoolean)
          throws RemoteException
        {}
        
        public void onDockSideChanged(int paramAnonymousInt)
          throws RemoteException
        {}
        
        public void onDockedStackExistsChanged(final boolean paramAnonymousBoolean)
          throws RemoteException
        {
          NavigationBarView.-get1(NavigationBarView.this).post(new Runnable()
          {
            public void run()
            {
              NavigationBarView.-set0(NavigationBarView.this, paramAnonymousBoolean);
              NavigationBarView.-wrap0(NavigationBarView.this);
            }
          });
        }
        
        public void onDockedStackMinimizedChanged(boolean paramAnonymousBoolean, long paramAnonymousLong)
          throws RemoteException
        {}
      });
      if (Utils.isSupportHideNavBar())
      {
        if (Settings.System.getInt(this.mContext.getContentResolver(), "oneplus_navigationbar_hide_button", 0) == 1)
        {
          bool = true;
          this.mShowNavKey = bool;
          ButtonDispatcher localButtonDispatcher = getNavButton();
          if (!showNavKey()) {
            break label127;
          }
          localButtonDispatcher.setVisibility(i);
        }
      }
      else {
        return;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e("StatusBar/NavBarView", "Failed registering docked stack exists listener", localRemoteException);
        continue;
        boolean bool = false;
        continue;
        label127:
        i = 4;
      }
    }
  }
  
  public void onImmersiveSticky(boolean paramBoolean)
  {
    if (Build.DEBUG_ONEPLUS) {
      Log.d("StatusBar/NavBarView", "onImmersiveSticky " + paramBoolean);
    }
    this.mIsImmersiveSticky = paramBoolean;
    ButtonDispatcher localButtonDispatcher = getNavButton();
    if (showNavKey()) {}
    for (int i = 0;; i = 4)
    {
      localButtonDispatcher.setVisibility(i);
      return;
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    return this.mGestureHelper.onInterceptTouchEvent(paramMotionEvent);
  }
  
  public void onShowKeyguard(boolean paramBoolean)
  {
    if (this.mKeyguardShow == paramBoolean) {
      return;
    }
    this.mKeyguardShow = paramBoolean;
    boolean bool;
    if (this.mKeyguardShow)
    {
      bool = isScreenSaverOn();
      if (!paramBoolean) {
        break label47;
      }
    }
    label47:
    for (int i = 0;; i = this.mBackgroundColor)
    {
      if (!bool) {
        notifyNavBarColorChange(i);
      }
      return;
      bool = false;
      break;
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt1 > 0) && (paramInt2 > paramInt1)) {}
    for (boolean bool = true;; bool = false)
    {
      if (bool != this.mVertical)
      {
        this.mVertical = bool;
        reorient();
        notifyVerticalChangedListener(bool);
      }
      postCheckForInvalidLayout("sizeChanged");
      super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mGestureHelper.onTouchEvent(paramMotionEvent)) {
      return true;
    }
    if ((this.mDeadZone != null) && (paramMotionEvent.getAction() == 4)) {
      this.mDeadZone.poke(paramMotionEvent);
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void refreshButtonColor()
  {
    updateButtonColor(this.mLastButtonColor, this.mLastRippleColor, true);
  }
  
  public void reorient()
  {
    updateCurrentView();
    getImeSwitchButton().setOnClickListener(this.mImeSwitcherClickListener);
    this.mDeadZone = ((DeadZone)this.mCurrentView.findViewById(2131952092));
    this.mBarTransitions.init();
    setDisabledFlags(this.mDisabledFlags, true);
    setMenuVisibility(this.mShowMenu, true);
    ButtonDispatcher localButtonDispatcher;
    if (Utils.isSupportHideNavBar())
    {
      localButtonDispatcher = getNavButton();
      if (!showNavKey()) {
        break label96;
      }
    }
    label96:
    for (int i = 0;; i = 4)
    {
      localButtonDispatcher.setVisibility(i);
      updateTaskSwitchHelper();
      setNavigationIconHints(this.mNavigationIconHints, true);
      return;
    }
  }
  
  public void setComponents(RecentsComponent paramRecentsComponent, Divider paramDivider)
  {
    this.mGestureHelper.setComponents(paramRecentsComponent, paramDivider, this);
  }
  
  public void setDisabledFlags(int paramInt)
  {
    setDisabledFlags(paramInt, false);
  }
  
  public void setDisabledFlags(int paramInt, boolean paramBoolean)
  {
    int m = 4;
    if ((!paramBoolean) && (this.mDisabledFlags == paramInt)) {
      return;
    }
    this.mDisabledFlags = paramInt;
    int k;
    int i;
    label49:
    int j;
    label69:
    label77:
    Object localObject;
    if ((0x200000 & paramInt) != 0)
    {
      k = 1;
      if (this.mUseCarModeUi) {
        break label221;
      }
      if ((0x1000000 & paramInt) == 0) {
        break label226;
      }
      i = 1;
      if ((0x400000 & paramInt) == 0) {
        break label237;
      }
      if ((this.mNavigationIconHints & 0x1) != 0) {
        break label231;
      }
      j = 1;
      if ((0x2000000 & paramInt) == 0) {
        break label243;
      }
      localObject = (ViewGroup)getCurrentView().findViewById(2131951802);
      if (localObject != null)
      {
        localObject = ((ViewGroup)localObject).getLayoutTransition();
        if ((localObject != null) && (!((LayoutTransition)localObject).getTransitionListeners().contains(this.mTransitionListener))) {
          ((LayoutTransition)localObject).addTransitionListener(this.mTransitionListener);
        }
      }
      paramInt = i;
      if (inLockTask())
      {
        paramInt = i;
        if (i != 0)
        {
          if (k == 0) {
            break label246;
          }
          paramInt = i;
        }
      }
      label157:
      localObject = getBackButton();
      if (j == 0) {
        break label251;
      }
      i = 4;
      label170:
      ((ButtonDispatcher)localObject).setVisibility(i);
      localObject = getHomeButton();
      if (k == 0) {
        break label256;
      }
      i = 4;
      label189:
      ((ButtonDispatcher)localObject).setVisibility(i);
      localObject = getRecentsButton();
      if (paramInt == 0) {
        break label261;
      }
    }
    label221:
    label226:
    label231:
    label237:
    label243:
    label246:
    label251:
    label256:
    label261:
    for (paramInt = m;; paramInt = 0)
    {
      ((ButtonDispatcher)localObject).setVisibility(paramInt);
      return;
      k = 0;
      break;
      i = 1;
      break label49;
      i = 0;
      break label49;
      j = 0;
      break label69;
      j = 0;
      break label69;
      break label77;
      paramInt = 0;
      break label157;
      i = 0;
      break label170;
      i = 0;
      break label189;
    }
  }
  
  public void setHeadsUpManager(HeadsUpManager paramHeadsUpManager)
  {
    this.mHeadsUpManager = paramHeadsUpManager;
  }
  
  public void setHideNavBarOn(boolean paramBoolean)
  {
    this.isHideNavBarOn = paramBoolean;
    if (this.isHideNavBarOn)
    {
      updateButtonColor(this.COLOR_KEY_TRANSPARENT, -1);
      return;
    }
    notifyNavBarColorChange(this.mBackgroundColor);
  }
  
  public void setLayoutDirection(int paramInt)
  {
    updateIcons(getContext(), Configuration.EMPTY, this.mConfiguration);
    int i = paramInt;
    if (Utils.isBackKeyRight(this.mContext)) {
      if (paramInt != 0) {
        break label45;
      }
    }
    label45:
    for (i = 1; Utils.isSupportHideNavBar(); i = 0)
    {
      swapkey(i);
      return;
    }
    super.setLayoutDirection(i);
  }
  
  public void setLayoutTransitionsEnabled(boolean paramBoolean)
  {
    this.mLayoutTransitionsEnabled = paramBoolean;
    updateLayoutTransitionsEnabled();
  }
  
  public void setMenuVisibility(boolean paramBoolean)
  {
    setMenuVisibility(paramBoolean, false);
  }
  
  public void setMenuVisibility(boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = 0;
    if ((!paramBoolean2) && (this.mShowMenu == paramBoolean1)) {
      return;
    }
    this.mShowMenu = paramBoolean1;
    ButtonDispatcher localButtonDispatcher;
    if (this.mShowMenu) {
      if ((this.mNavigationIconHints & 0x2) == 0)
      {
        i = 1;
        localButtonDispatcher = getMenuButton();
        if (i == 0) {
          break label69;
        }
      }
    }
    label69:
    for (int i = j;; i = 4)
    {
      localButtonDispatcher.setVisibility(i);
      return;
      i = 0;
      break;
      i = 0;
      break;
    }
  }
  
  public void setNavigationIconHints(int paramInt)
  {
    setNavigationIconHints(paramInt, false);
  }
  
  public void setNavigationIconHints(int paramInt, boolean paramBoolean)
  {
    int i = 0;
    if ((!paramBoolean) && (paramInt == this.mNavigationIconHints)) {
      return;
    }
    label36:
    Object localObject;
    if ((paramInt & 0x1) != 0)
    {
      paramBoolean = true;
      if (((this.mNavigationIconHints & 0x1) != 0) && (!paramBoolean)) {
        break label185;
      }
      this.mNavigationIconHints = paramInt;
      if (!paramBoolean) {
        break label195;
      }
      localObject = getBackIconWithAlt(this.mUseCarModeUi, this.mVertical);
      label59:
      getBackButton().setImageDrawable((Drawable)localObject);
      updateRecentsIcon();
      if (!this.mUseCarModeUi) {
        break label212;
      }
      getHomeButton().setImageDrawable(this.mHomeCarModeIcon);
      label90:
      if ((paramInt & 0x2) == 0) {
        break label226;
      }
      paramInt = 1;
      label98:
      localObject = getImeSwitchButton();
      if (paramInt == 0) {
        break label231;
      }
      paramInt = i;
      label110:
      ((ButtonDispatcher)localObject).setVisibility(paramInt);
      getImeSwitchButton().setImageDrawable(this.mImeIcon);
      setMenuVisibility(this.mShowMenu, true);
      getMenuButton().setImageDrawable(this.mMenuIcon);
      if (Utils.isSupportHideNavBar())
      {
        this.mImeShow = paramBoolean;
        if (!paramBoolean) {
          break label236;
        }
        notifyNavBarColorChange(this.COLOR_KEY_LIGHT);
      }
    }
    for (;;)
    {
      setDisabledFlags(this.mDisabledFlags, true);
      return;
      paramBoolean = false;
      break;
      label185:
      this.mTransitionListener.onBackAltCleared();
      break label36;
      label195:
      localObject = getBackIcon(this.mUseCarModeUi, this.mVertical);
      break label59;
      label212:
      getHomeButton().setImageDrawable(this.mHomeDefaultIcon);
      break label90;
      label226:
      paramInt = 0;
      break label98;
      label231:
      paramInt = 4;
      break label110;
      label236:
      notifyNavBarColorChange(this.mBackgroundColor);
    }
  }
  
  public void setOnVerticalChangedListener(OnVerticalChangedListener paramOnVerticalChangedListener)
  {
    this.mOnVerticalChangedListener = paramOnVerticalChangedListener;
    notifyVerticalChangedListener(this.mVertical);
  }
  
  public void setWakeAndUnlocking(boolean paramBoolean)
  {
    setUseFadingAnimations(paramBoolean);
    this.mWakeAndUnlocking = paramBoolean;
    updateLayoutTransitionsEnabled();
  }
  
  public void swapkey(int paramInt)
  {
    if (paramInt == 1)
    {
      this.mNavigationInflaterView.onTuningChanged("sysui_nav_bar", this.mContext.getString(2131689915));
      return;
    }
    this.mNavigationInflaterView.onTuningChanged("sysui_nav_bar", this.mContext.getString(2131689914));
  }
  
  public void updateNavButtonState(boolean paramBoolean)
  {
    this.mShowNavKey = paramBoolean;
    ButtonDispatcher localButtonDispatcher = getNavButton();
    if (showNavKey()) {}
    for (int i = 0;; i = 4)
    {
      localButtonDispatcher.setVisibility(i);
      return;
    }
  }
  
  void updateRotatedViews()
  {
    View[] arrayOfView = this.mRotatedViews;
    View localView = findViewById(2131952083);
    this.mRotatedViews[2] = localView;
    arrayOfView[0] = localView;
    arrayOfView = this.mRotatedViews;
    localView = findViewById(2131952084);
    this.mRotatedViews[1] = localView;
    arrayOfView[3] = localView;
    updateCurrentView();
  }
  
  public void updateWallpaperState(int paramInt)
  {
    this.mWallpaperState = paramInt;
    if (Build.DEBUG_ONEPLUS) {
      Log.d("StatusBar/NavBarView", "updateWallpaperState mWallpaperState " + this.mWallpaperState);
    }
  }
  
  private class H
    extends Handler
  {
    private H() {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      }
      int i;
      int j;
      int k;
      int m;
      do
      {
        return;
        paramMessage = "" + paramMessage.obj;
        i = NavigationBarView.this.getWidth();
        j = NavigationBarView.this.getHeight();
        k = NavigationBarView.this.getCurrentView().getWidth();
        m = NavigationBarView.this.getCurrentView().getHeight();
      } while ((j == m) && (i == k));
      Log.w("StatusBar/NavBarView", String.format("*** Invalid layout in navigation bar (%s this=%dx%d cur=%dx%d)", new Object[] { paramMessage, Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(m) }));
      NavigationBarView.this.requestLayout();
    }
  }
  
  private class NavTransitionListener
    implements LayoutTransition.TransitionListener
  {
    private boolean mBackTransitioning;
    private long mDuration;
    private boolean mHomeAppearing;
    private TimeInterpolator mInterpolator;
    private long mStartDelay;
    
    private NavTransitionListener() {}
    
    public void endTransition(LayoutTransition paramLayoutTransition, ViewGroup paramViewGroup, View paramView, int paramInt)
    {
      if (paramView.getId() == 2131951793) {
        this.mBackTransitioning = false;
      }
      while ((paramView.getId() != 2131951635) || (paramInt != 2)) {
        return;
      }
      this.mHomeAppearing = false;
    }
    
    public void onBackAltCleared()
    {
      Object localObject = NavigationBarView.this.getBackButton();
      if ((!this.mBackTransitioning) && (((ButtonDispatcher)localObject).getVisibility() == 0) && (this.mHomeAppearing) && (NavigationBarView.this.getHomeButton().getAlpha() == 0.0F))
      {
        NavigationBarView.this.getBackButton().setAlpha(0);
        localObject = ObjectAnimator.ofFloat(localObject, "alpha", new float[] { 0.0F, 1.0F });
        ((ValueAnimator)localObject).setStartDelay(this.mStartDelay);
        ((ValueAnimator)localObject).setDuration(this.mDuration);
        ((ValueAnimator)localObject).setInterpolator(this.mInterpolator);
        ((ValueAnimator)localObject).start();
      }
    }
    
    public void startTransition(LayoutTransition paramLayoutTransition, ViewGroup paramViewGroup, View paramView, int paramInt)
    {
      if (paramView.getId() == 2131951793) {
        this.mBackTransitioning = true;
      }
      while ((paramView.getId() != 2131951635) || (paramInt != 2)) {
        return;
      }
      this.mHomeAppearing = true;
      this.mStartDelay = paramLayoutTransition.getStartDelay(paramInt);
      this.mDuration = paramLayoutTransition.getDuration(paramInt);
      this.mInterpolator = paramLayoutTransition.getInterpolator(paramInt);
    }
  }
  
  public static abstract interface OnVerticalChangedListener
  {
    public abstract void onVerticalChanged(boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\NavigationBarView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */