package com.android.systemui.volume;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.net.Uri;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.Transition.TransitionListener;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Interpolators;
import com.android.systemui.SystemUI;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import com.android.systemui.tuner.TunerZenModePanel;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VolumeDialog
  implements TunerService.Tunable
{
  private static final String TAG = Util.logTag(VolumeDialog.class);
  private final Accessibility mAccessibility = new Accessibility(null);
  private final AccessibilityManager mAccessibilityMgr;
  private ColorStateList mActiveSliderTint;
  private int mActiveStream;
  private final AudioManager mAudioManager;
  private boolean mAutomute = true;
  private ColorStateList mBackgroundSliderTint;
  private int mBlackAccentColor = 0;
  private Callback mCallback;
  private final View.OnClickListener mClickExpand = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (VolumeDialog.-get8(VolumeDialog.this)) {
        return;
      }
      if (VolumeDialog.-get9(VolumeDialog.this)) {}
      for (boolean bool = false;; bool = true)
      {
        Events.writeEvent(VolumeDialog.-get3(VolumeDialog.this), 3, new Object[] { Boolean.valueOf(bool) });
        VolumeDialog.-wrap12(VolumeDialog.this, bool, false);
        return;
      }
    }
  };
  private long mCollapseTime;
  private boolean mConfigurationChange;
  private final Context mContext;
  private final VolumeDialogController mController;
  private final VolumeDialogController.Callbacks mControllerCallbackH = new VolumeDialogController.Callbacks()
  {
    public void onConfigurationChanged()
    {
      Configuration localConfiguration = VolumeDialog.-get3(VolumeDialog.this).getResources().getConfiguration();
      int i = localConfiguration.densityDpi;
      boolean bool = false;
      float f = localConfiguration.fontScale;
      VolumeDialog.-set0(VolumeDialog.this, true);
      if ((i != VolumeDialog.-get5(VolumeDialog.this)) || (f != VolumeDialog.-get10(VolumeDialog.this)))
      {
        VolumeDialog.-set1(VolumeDialog.this, i);
        VolumeDialog.-set3(VolumeDialog.this, f);
        VolumeDialog.-get6(VolumeDialog.this).dismiss();
        VolumeDialog.-get21(VolumeDialog.this).cleanup();
        VolumeDialog.-wrap3(VolumeDialog.this);
        VolumeDialog.-wrap13(VolumeDialog.this);
        VolumeDialog.-wrap14(VolumeDialog.this, VolumeDialog.-wrap1(VolumeDialog.this));
        bool = true;
      }
      VolumeDialog.-wrap15(VolumeDialog.this);
      VolumeDialog.-get18(VolumeDialog.this).update();
      VolumeDialog.-get21(VolumeDialog.this).onConfigurationChanged();
      VolumeDialog.-wrap2(VolumeDialog.this, bool);
      VolumeDialog.-set0(VolumeDialog.this, false);
    }
    
    public void onDismissRequested(int paramAnonymousInt)
    {
      VolumeDialog.this.dismissH(paramAnonymousInt);
    }
    
    public void onLayoutDirectionChanged(int paramAnonymousInt)
    {
      VolumeDialog.-get7(VolumeDialog.this).setLayoutDirection(paramAnonymousInt);
    }
    
    public void onScreenOff()
    {
      VolumeDialog.this.dismissH(4);
    }
    
    public void onShowRequested(int paramAnonymousInt)
    {
      VolumeDialog.-wrap8(VolumeDialog.this, paramAnonymousInt);
    }
    
    public void onShowSafetyWarning(int paramAnonymousInt)
    {
      if (VolumeDialog.-wrap0(VolumeDialog.this)) {
        VolumeDialog.-wrap9(VolumeDialog.this, paramAnonymousInt);
      }
    }
    
    public void onShowSilentHint()
    {
      if (VolumeDialog.-get17(VolumeDialog.this)) {
        VolumeDialog.-get4(VolumeDialog.this).setRingerMode(2, false);
      }
    }
    
    public void onShowVibrateHint()
    {
      if (VolumeDialog.-get17(VolumeDialog.this)) {
        VolumeDialog.-get4(VolumeDialog.this).setRingerMode(0, false);
      }
    }
    
    public void onStateChanged(VolumeDialogController.State paramAnonymousState)
    {
      VolumeDialog.-wrap4(VolumeDialog.this, paramAnonymousState);
    }
  };
  private int mDensity;
  private CustomDialog mDialog;
  private ViewGroup mDialogContentView;
  private ViewGroup mDialogRowsView;
  private ViewGroup mDialogView;
  private final SparseBooleanArray mDynamic = new SparseBooleanArray();
  private ImageButton mExpandButton;
  private int mExpandButtonAnimationDuration;
  private boolean mExpandButtonAnimationRunning;
  private boolean mExpanded;
  private float mFontScale = 1.0F;
  private final H mHandler = new H();
  private boolean mHovering = false;
  private ColorStateList mInactiveSliderTint;
  private final KeyguardManager mKeyguard;
  private VolumeDialogMotion mMotion;
  private boolean mPendingRecheckAll;
  private boolean mPendingStateChanged;
  private final List<VolumeRow> mRows = new ArrayList();
  private SafetyWarningDialog mSafetyWarning;
  private final Object mSafetyWarningLock = new Object();
  private boolean mShowFullZen;
  private boolean mShowHeaders = true;
  private boolean mShowing;
  private boolean mSilentMode = true;
  private SpTexts mSpTexts;
  private VolumeDialogController.State mState;
  private SystemUI mSysui;
  private int mThemeColorExpandBtn = 0;
  private int mThemeColorFooterIcon = 0;
  private int mThemeColorFooterText = 0;
  private int mThemeColorIcon = 0;
  private int mThemeColorMode = 0;
  private int mThemeColorPrimary = 0;
  private int mThemeColorSeekbar = 0;
  private int mThemeColorSeekbarBackgroundColor = 0;
  private int mThemeColorSeekbarThumb = 0;
  private int mThemeColorText = 0;
  private int mWhiteAccentColor = 0;
  private Window mWindow;
  private final int mWindowType;
  private ZenFooter mZenFooter;
  private final ZenModeController mZenModeController;
  private TunerZenModePanel mZenPanel;
  private final ZenModePanel.Callback mZenPanelCallback = new ZenModePanel.Callback()
  {
    public void onExpanded(boolean paramAnonymousBoolean) {}
    
    public void onInteraction()
    {
      VolumeDialog.-get11(VolumeDialog.this).sendEmptyMessage(6);
    }
    
    public void onPrioritySettings()
    {
      VolumeDialog.-get2(VolumeDialog.this).onZenPrioritySettingsClicked();
    }
  };
  
  public VolumeDialog(Context paramContext, int paramInt, VolumeDialogController paramVolumeDialogController, ZenModeController paramZenModeController, Callback paramCallback, SystemUI paramSystemUI)
  {
    this.mContext = paramContext;
    this.mController = paramVolumeDialogController;
    this.mCallback = paramCallback;
    this.mSysui = paramSystemUI;
    this.mWindowType = paramInt;
    this.mZenModeController = paramZenModeController;
    this.mKeyguard = ((KeyguardManager)paramContext.getSystemService("keyguard"));
    this.mAudioManager = ((AudioManager)paramContext.getSystemService("audio"));
    this.mAccessibilityMgr = ((AccessibilityManager)this.mContext.getSystemService("accessibility"));
    this.mActiveSliderTint = ColorStateList.valueOf(com.android.settingslib.Utils.getColorAccent(this.mContext));
    this.mInactiveSliderTint = loadColorStateList(2131493053);
    this.mBackgroundSliderTint = loadColorStateList(2131493076, 1124073471);
    initDialog();
    this.mAccessibility.init();
    paramVolumeDialogController.addCallback(this.mControllerCallbackH, this.mHandler);
    paramVolumeDialogController.getState();
    TunerService.get(this.mContext).addTunable(this, new String[] { "sysui_show_full_zen" });
    paramContext = this.mContext.getResources().getConfiguration();
    this.mDensity = paramContext.densityDpi;
    this.mFontScale = paramContext.fontScale;
  }
  
  private void addExistingRows()
  {
    int j = this.mRows.size();
    int i = 0;
    while (i < j)
    {
      VolumeRow localVolumeRow = (VolumeRow)this.mRows.get(i);
      initRow(localVolumeRow, VolumeRow.-get14(localVolumeRow), VolumeRow.-get7(localVolumeRow), VolumeRow.-get6(localVolumeRow), VolumeRow.-get9(localVolumeRow));
      this.mDialogRowsView.addView(VolumeRow.-get17(localVolumeRow));
      i += 1;
    }
  }
  
  private void addRow(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    VolumeRow localVolumeRow = new VolumeRow(null);
    initRow(localVolumeRow, paramInt1, paramInt2, paramInt3, paramBoolean);
    this.mDialogRowsView.addView(VolumeRow.-get17(localVolumeRow));
    this.mRows.add(localVolumeRow);
  }
  
  private void applyAndroidTheme()
  {
    Resources localResources = this.mContext.getResources();
    this.mThemeColorPrimary = localResources.getColor(2131493101);
    this.mThemeColorText = localResources.getColor(2131493091);
    this.mThemeColorIcon = localResources.getColor(2131493092);
    this.mThemeColorSeekbar = localResources.getColor(2131493093);
    this.mThemeColorSeekbarThumb = localResources.getColor(2131493093);
    this.mThemeColorFooterIcon = localResources.getColor(2131493095);
    this.mThemeColorFooterText = localResources.getColor(2131493096);
    this.mThemeColorExpandBtn = localResources.getColor(2131493097);
  }
  
  private void applyBlackTheme()
  {
    Resources localResources = this.mContext.getResources();
    this.mThemeColorPrimary = localResources.getColor(2131493100);
    this.mThemeColorText = localResources.getColor(2131493084);
    this.mThemeColorIcon = localResources.getColor(2131493085);
    this.mThemeColorSeekbarThumb = localResources.getColor(2131493087);
    this.mThemeColorFooterIcon = localResources.getColor(2131493088);
    this.mThemeColorFooterText = localResources.getColor(2131493089);
    this.mThemeColorExpandBtn = localResources.getColor(2131493090);
    this.mThemeColorIcon = this.mBlackAccentColor;
    this.mThemeColorSeekbar = this.mBlackAccentColor;
    this.mThemeColorSeekbarThumb = this.mBlackAccentColor;
    this.mThemeColorExpandBtn = this.mBlackAccentColor;
  }
  
  private void applyColorTheme(boolean paramBoolean)
  {
    int i = com.android.systemui.util.Utils.getThemeColor(this.mContext);
    boolean bool = isAccentColorChanged();
    if ((this.mThemeColorMode != i) || (bool))
    {
      this.mThemeColorMode = i;
      switch (i)
      {
      default: 
        applyWhiteTheme();
      }
    }
    for (;;)
    {
      applyColors();
      return;
      if (paramBoolean) {
        break;
      }
      return;
      applyWhiteTheme();
      continue;
      applyBlackTheme();
      continue;
      applyAndroidTheme();
    }
  }
  
  private void applyColors()
  {
    this.mDialogView.setBackgroundColor(this.mThemeColorPrimary);
    this.mExpandButton.setColorFilter(this.mThemeColorExpandBtn);
    Iterator localIterator = this.mRows.iterator();
    while (localIterator.hasNext())
    {
      VolumeRow localVolumeRow = (VolumeRow)localIterator.next();
      VolumeRow.-get4(localVolumeRow).setTextColor(this.mThemeColorText);
      VolumeRow.-get5(localVolumeRow).setColorFilter(this.mThemeColorIcon);
      VolumeRow.-get12(localVolumeRow).setThumbTintList(ColorStateList.valueOf(this.mThemeColorSeekbarThumb));
      VolumeRow.-get12(localVolumeRow).setProgressTintList(ColorStateList.valueOf(this.mThemeColorSeekbar));
      VolumeRow.-get12(localVolumeRow).setProgressBackgroundTintList(ColorStateList.valueOf(this.mThemeColorSeekbarBackgroundColor));
    }
    ((ImageView)this.mDialog.findViewById(2131952350)).setColorFilter(this.mThemeColorFooterIcon);
    ((TextView)this.mDialog.findViewById(2131952351)).setTextColor(this.mThemeColorFooterText);
    ((ImageView)this.mDialog.findViewById(2131952352)).setColorFilter(this.mThemeColorFooterIcon);
  }
  
  private void applyWhiteTheme()
  {
    Resources localResources = this.mContext.getResources();
    this.mThemeColorPrimary = localResources.getColor(2131493099);
    this.mThemeColorText = localResources.getColor(2131493077);
    this.mThemeColorFooterIcon = localResources.getColor(2131493081);
    this.mThemeColorFooterText = localResources.getColor(2131493082);
    this.mThemeColorIcon = this.mWhiteAccentColor;
    this.mThemeColorSeekbar = this.mWhiteAccentColor;
    this.mThemeColorSeekbarThumb = this.mWhiteAccentColor;
    this.mThemeColorExpandBtn = this.mWhiteAccentColor;
  }
  
  private boolean checkShowSafeVolumeWarningPermission()
  {
    boolean bool = Settings.canDrawOverlays(this.mContext);
    if (!bool)
    {
      Intent localIntent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + this.mContext.getPackageName()));
      this.mContext.startActivity(localIntent);
    }
    return bool;
  }
  
  private int computeTimeoutH()
  {
    if (Accessibility.-get0(this.mAccessibility)) {
      return 20000;
    }
    if (this.mHovering) {
      return 16000;
    }
    if (this.mSafetyWarning != null) {
      return 5000;
    }
    if ((this.mExpanded) || (this.mExpandButtonAnimationRunning)) {
      return 5000;
    }
    if (this.mActiveStream == 3) {
      return 1500;
    }
    return 3000;
  }
  
  private VolumeRow findRow(int paramInt)
  {
    Iterator localIterator = this.mRows.iterator();
    while (localIterator.hasNext())
    {
      VolumeRow localVolumeRow = (VolumeRow)localIterator.next();
      if (VolumeRow.-get14(localVolumeRow) == paramInt) {
        return localVolumeRow;
      }
    }
    return null;
  }
  
  private VolumeRow getActiveRow()
  {
    Iterator localIterator = this.mRows.iterator();
    while (localIterator.hasNext())
    {
      VolumeRow localVolumeRow = (VolumeRow)localIterator.next();
      if (VolumeRow.-get14(localVolumeRow) == this.mActiveStream) {
        return localVolumeRow;
      }
    }
    return (VolumeRow)this.mRows.get(0);
  }
  
  private long getConservativeCollapseDuration()
  {
    return this.mExpandButtonAnimationDuration * 3;
  }
  
  private static int getImpliedLevel(SeekBar paramSeekBar, int paramInt)
  {
    int i = paramSeekBar.getMax();
    int j = i / 100;
    if (paramInt == 0) {
      return 0;
    }
    if (paramInt == i) {
      return i / 100;
    }
    return (int)(paramInt / i * (j - 1)) + 1;
  }
  
  private boolean hasTouchFeature()
  {
    return this.mContext.getPackageManager().hasSystemFeature("android.hardware.touchscreen");
  }
  
  private void initDialog()
  {
    this.mDialog = new CustomDialog(this.mContext);
    this.mSpTexts = new SpTexts(this.mContext);
    this.mHovering = false;
    this.mShowing = false;
    this.mWindow = this.mDialog.getWindow();
    this.mWindow.requestFeature(1);
    this.mWindow.setBackgroundDrawable(new ColorDrawable(0));
    this.mWindow.clearFlags(2);
    this.mWindow.addFlags(17563944);
    this.mDialog.setCanceledOnTouchOutside(true);
    Resources localResources = this.mContext.getResources();
    WindowManager.LayoutParams localLayoutParams = this.mWindow.getAttributes();
    localLayoutParams.type = this.mWindowType;
    localLayoutParams.format = -3;
    localLayoutParams.setTitle(VolumeDialog.class.getSimpleName());
    localLayoutParams.gravity = 49;
    localLayoutParams.y = localResources.getDimensionPixelSize(2131755555);
    localLayoutParams.gravity = 48;
    localLayoutParams.windowAnimations = -1;
    this.mWindow.setAttributes(localLayoutParams);
    this.mWindow.setSoftInputMode(48);
    this.mActiveSliderTint = loadColorStateList(2131493076);
    this.mInactiveSliderTint = loadColorStateList(2131493076);
    this.mBackgroundSliderTint = loadColorStateList(2131493076, 1124073471);
    this.mDialog.setContentView(2130968844);
    this.mDialogView = ((ViewGroup)this.mDialog.findViewById(2131952342));
    this.mDialogView.setOnHoverListener(new View.OnHoverListener()
    {
      public boolean onHover(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        int i = paramAnonymousMotionEvent.getActionMasked();
        paramAnonymousView = VolumeDialog.this;
        boolean bool;
        if (i != 9)
        {
          if (i != 7) {
            break label47;
          }
          bool = true;
        }
        for (;;)
        {
          VolumeDialog.-set4(paramAnonymousView, bool);
          VolumeDialog.this.rescheduleTimeoutH();
          return true;
          bool = true;
          continue;
          label47:
          bool = false;
        }
      }
    });
    this.mDialogContentView = ((ViewGroup)this.mDialog.findViewById(2131952343));
    this.mDialogRowsView = ((ViewGroup)this.mDialogContentView.findViewById(2131952344));
    this.mExpanded = false;
    this.mExpandButton = ((ImageButton)this.mDialogView.findViewById(2131952345));
    this.mExpandButton.setOnClickListener(this.mClickExpand);
    updateWindowWidthH();
    updateExpandButtonH();
    this.mMotion = new VolumeDialogMotion(this.mDialog, this.mDialogView, this.mDialogContentView, this.mExpandButton, new VolumeDialogMotion.Callback()
    {
      public void onAnimatingChanged(boolean paramAnonymousBoolean)
      {
        if (paramAnonymousBoolean) {
          return;
        }
        if (VolumeDialog.-get14(VolumeDialog.this))
        {
          VolumeDialog.-get11(VolumeDialog.this).sendEmptyMessage(7);
          VolumeDialog.-set6(VolumeDialog.this, false);
        }
        if (VolumeDialog.-get13(VolumeDialog.this))
        {
          VolumeDialog.-get11(VolumeDialog.this).sendEmptyMessage(4);
          VolumeDialog.-set5(VolumeDialog.this, false);
        }
      }
    });
    if (this.mRows.isEmpty())
    {
      addRow(2, 2130837953, 2130837954, true);
      addRow(3, 2130837947, 2130837950, true);
      addRow(4, 2130837936, 2130837937, false);
      addRow(0, 2130837959, 2130837959, false);
      addRow(6, 2130837938, 2130837938, false);
      addRow(1, 2130837957, 2130837958, false);
    }
    for (;;)
    {
      this.mExpandButtonAnimationDuration = localResources.getInteger(2131624018);
      this.mZenFooter = ((ZenFooter)this.mDialog.findViewById(2131952349));
      this.mZenFooter.init(this.mSysui, this, this.mZenModeController);
      this.mZenPanel = ((TunerZenModePanel)this.mDialog.findViewById(2131952316));
      this.mZenPanel.init(this.mZenModeController);
      this.mZenPanel.setCallback(this.mZenPanelCallback);
      this.mAccessibility.init();
      this.mWhiteAccentColor = com.android.systemui.util.Utils.getThemeWhiteAccentColor(this.mContext, 2131493079);
      this.mBlackAccentColor = com.android.systemui.util.Utils.getThemeBlackAccentColor(this.mContext, 2131493086);
      this.mThemeColorSeekbarBackgroundColor = localResources.getColor(2131493098);
      this.mController.addCallback(this.mControllerCallbackH, this.mHandler);
      this.mController.getState();
      return;
      addExistingRows();
    }
  }
  
  @SuppressLint({"InflateParams"})
  private void initRow(final VolumeRow paramVolumeRow, final int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    VolumeRow.-set15(paramVolumeRow, paramInt1);
    VolumeRow.-set8(paramVolumeRow, paramInt2);
    VolumeRow.-set7(paramVolumeRow, paramInt3);
    VolumeRow.-set10(paramVolumeRow, paramBoolean);
    VolumeRow.-set18(paramVolumeRow, this.mDialog.getLayoutInflater().inflate(2130968845, null));
    VolumeRow.-get17(paramVolumeRow).setId(VolumeRow.-get14(paramVolumeRow));
    VolumeRow.-get17(paramVolumeRow).setTag(paramVolumeRow);
    VolumeRow.-set5(paramVolumeRow, (TextView)VolumeRow.-get17(paramVolumeRow).findViewById(2131952346));
    VolumeRow.-get4(paramVolumeRow).setId(VolumeRow.-get14(paramVolumeRow) * 20);
    this.mSpTexts.add(VolumeRow.-get4(paramVolumeRow));
    VolumeRow.-set13(paramVolumeRow, (SeekBar)VolumeRow.-get17(paramVolumeRow).findViewById(2131952348));
    VolumeRow.-get12(paramVolumeRow).setOnSeekBarChangeListener(new VolumeSeekBarChangeListener(paramVolumeRow, null));
    VolumeRow.-set0(paramVolumeRow, null);
    VolumeRow.-set3(paramVolumeRow, true);
    VolumeRow.-get17(paramVolumeRow).setOnTouchListener(new View.OnTouchListener()
    {
      private boolean mDragging;
      private final Rect mSliderHitRect = new Rect();
      
      @SuppressLint({"ClickableViewAccessibility"})
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        VolumeDialog.VolumeRow.-get12(paramVolumeRow).getHitRect(this.mSliderHitRect);
        if ((!this.mDragging) && (paramAnonymousMotionEvent.getActionMasked() == 0) && (paramAnonymousMotionEvent.getY() < this.mSliderHitRect.top)) {
          this.mDragging = true;
        }
        if (this.mDragging)
        {
          paramAnonymousMotionEvent.offsetLocation(-this.mSliderHitRect.left, -this.mSliderHitRect.top);
          VolumeDialog.VolumeRow.-get12(paramVolumeRow).dispatchTouchEvent(paramAnonymousMotionEvent);
          if ((paramAnonymousMotionEvent.getActionMasked() == 1) || (paramAnonymousMotionEvent.getActionMasked() == 3)) {
            this.mDragging = false;
          }
          return true;
        }
        return false;
      }
    });
    VolumeRow.-set6(paramVolumeRow, (ImageButton)VolumeRow.-get17(paramVolumeRow).findViewById(2131952347));
    VolumeRow.-get5(paramVolumeRow).setImageResource(paramInt2);
    VolumeRow.-set2(paramVolumeRow, paramInt2);
    VolumeRow.-get5(paramVolumeRow).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        int j = 1;
        Events.writeEvent(VolumeDialog.-get3(VolumeDialog.this), 7, new Object[] { Integer.valueOf(VolumeDialog.VolumeRow.-get14(paramVolumeRow)), Integer.valueOf(VolumeDialog.VolumeRow.-get8(paramVolumeRow)) });
        VolumeDialog.-get4(VolumeDialog.this).setActiveStream(VolumeDialog.VolumeRow.-get14(paramVolumeRow));
        if (VolumeDialog.VolumeRow.-get14(paramVolumeRow) == 2)
        {
          if (VolumeDialog.VolumeRow.-get13(paramVolumeRow).level == 1) {}
          for (i = 1;; i = 0)
          {
            paramAnonymousView = VolumeDialog.-get4(VolumeDialog.this);
            int k = paramInt1;
            if (i != 0) {
              j = VolumeDialog.VolumeRow.-get10(paramVolumeRow);
            }
            paramAnonymousView.setStreamVolume(k, j);
            VolumeDialog.VolumeRow.-set17(paramVolumeRow, 0L);
            return;
          }
        }
        if (VolumeDialog.VolumeRow.-get13(paramVolumeRow).level == 0)
        {
          i = 1;
          label151:
          paramAnonymousView = VolumeDialog.-get4(VolumeDialog.this);
          j = paramInt1;
          if (i == 0) {
            break label190;
          }
        }
        label190:
        for (int i = VolumeDialog.VolumeRow.-get10(paramVolumeRow);; i = 0)
        {
          paramAnonymousView.setStreamVolume(j, i);
          break;
          i = 0;
          break label151;
        }
      }
    });
  }
  
  private boolean isAccentColorChanged()
  {
    int i = com.android.systemui.util.Utils.getThemeWhiteAccentColor(this.mContext, 2131493079);
    int j = com.android.systemui.util.Utils.getThemeBlackAccentColor(this.mContext, 2131493086);
    if ((this.mThemeColorMode == 0) && (this.mWhiteAccentColor != i))
    {
      this.mWhiteAccentColor = i;
      return true;
    }
    if ((this.mThemeColorMode == 1) && (this.mBlackAccentColor != j))
    {
      this.mBlackAccentColor = j;
      return true;
    }
    return false;
  }
  
  private boolean isAttached()
  {
    if (this.mDialogContentView != null) {
      return this.mDialogContentView.isAttachedToWindow();
    }
    return false;
  }
  
  private ColorStateList loadColorStateList(int paramInt)
  {
    return ColorStateList.valueOf(this.mContext.getColor(paramInt));
  }
  
  private ColorStateList loadColorStateList(int paramInt1, int paramInt2)
  {
    return ColorStateList.valueOf(this.mContext.getColor(paramInt1) & paramInt2);
  }
  
  private void onStateChangedH(VolumeDialogController.State paramState)
  {
    boolean bool = this.mMotion.isAnimating();
    if (D.BUG) {
      Log.d(TAG, "onStateChangedH animating=" + bool);
    }
    this.mState = paramState;
    if (bool)
    {
      this.mPendingStateChanged = true;
      return;
    }
    int m = 0;
    int i = m;
    int k;
    if (this.mDynamic.size() > 0)
    {
      j = 0;
      i = this.mRows.size() - 1;
      while (i >= 0)
      {
        k = j;
        if (this.mDynamic.equals(this.mRows.get(i))) {
          k = j + 1;
        }
        i -= 1;
        j = k;
      }
      i = m;
      if (j != this.mDynamic.size()) {
        i = 1;
      }
    }
    this.mDynamic.clear();
    int j = 0;
    if (j < paramState.states.size())
    {
      k = paramState.states.keyAt(j);
      if (!((VolumeDialogController.StreamState)paramState.states.valueAt(j)).dynamic) {}
      for (;;)
      {
        j += 1;
        break;
        Log.i(TAG, " onStateChangedH stream:" + k);
        this.mDynamic.put(k, true);
        if (findRow(k) == null) {
          addRow(k, 2130837951, 2130837952, true);
        }
      }
    }
    if (this.mActiveStream != paramState.activeStream)
    {
      this.mActiveStream = paramState.activeStream;
      updateRowsH(getActiveRow());
      rescheduleTimeoutH();
    }
    for (;;)
    {
      paramState = this.mRows.iterator();
      while (paramState.hasNext()) {
        updateVolumeRowH((VolumeRow)paramState.next());
      }
      if (i != 0) {
        updateRowsH(getActiveRow());
      }
    }
    updateFooterH();
  }
  
  private void prepareForCollapse()
  {
    this.mHandler.removeMessages(8);
    this.mCollapseTime = System.currentTimeMillis();
    updateDialogBottomMarginH();
    this.mHandler.sendEmptyMessageDelayed(8, getConservativeCollapseDuration());
  }
  
  private void recheckH(VolumeRow paramVolumeRow)
  {
    if (paramVolumeRow == null)
    {
      if (D.BUG) {
        Log.d(TAG, "recheckH ALL");
      }
      trimObsoleteH();
      paramVolumeRow = this.mRows.iterator();
      while (paramVolumeRow.hasNext()) {
        updateVolumeRowH((VolumeRow)paramVolumeRow.next());
      }
    }
    if (D.BUG) {
      Log.d(TAG, "recheckH " + VolumeRow.-get14(paramVolumeRow));
    }
    updateVolumeRowH(paramVolumeRow);
  }
  
  private void setStreamImportantH(int paramInt, boolean paramBoolean)
  {
    Iterator localIterator = this.mRows.iterator();
    while (localIterator.hasNext())
    {
      VolumeRow localVolumeRow = (VolumeRow)localIterator.next();
      if (VolumeRow.-get14(localVolumeRow) == paramInt)
      {
        Log.i(TAG, " setStreamImportantH stream:" + VolumeRow.-get14(localVolumeRow) + " important:" + paramBoolean);
        VolumeRow.-set10(localVolumeRow, paramBoolean);
        return;
      }
    }
  }
  
  private boolean shouldBeVisibleH(VolumeRow paramVolumeRow, boolean paramBoolean)
  {
    if ((this.mExpanded) && (VolumeRow.-get17(paramVolumeRow).getVisibility() == 0)) {
      paramBoolean = true;
    }
    do
    {
      return paramBoolean;
      if ((this.mExpanded) && ((VolumeRow.-get9(paramVolumeRow)) || (paramBoolean))) {
        break;
      }
    } while (!this.mExpanded);
    return false;
  }
  
  private void showH(int paramInt)
  {
    if (D.BUG) {
      Log.d(TAG, "showH r=" + Events.DISMISS_REASONS[paramInt]);
    }
    this.mHandler.removeMessages(1);
    this.mHandler.removeMessages(2);
    rescheduleTimeoutH();
    if ((this.mShowing) || (KeyguardUpdateMonitor.getInstance(this.mContext).isDreaming())) {
      return;
    }
    applyColorTheme(true);
    this.mShowing = true;
    this.mMotion.startShow();
    Events.writeEvent(this.mContext, 0, new Object[] { Integer.valueOf(paramInt), Boolean.valueOf(this.mKeyguard.isKeyguardLocked()) });
    this.mController.notifyVisible(true);
  }
  
  private void showSafetyWarningH(int paramInt)
  {
    if (((paramInt & 0x401) != 0) || (this.mShowing)) {}
    synchronized (this.mSafetyWarningLock)
    {
      SafetyWarningDialog localSafetyWarningDialog = this.mSafetyWarning;
      if (localSafetyWarningDialog != null) {
        return;
      }
      this.mSafetyWarning = new SafetyWarningDialog(this.mContext, this.mController.getAudioManager())
      {
        protected void cleanUp()
        {
          synchronized (VolumeDialog.-get15(VolumeDialog.this))
          {
            VolumeDialog.-set7(VolumeDialog.this, null);
            VolumeDialog.-wrap6(VolumeDialog.this, null);
            return;
          }
        }
      };
      this.mSafetyWarning.show();
      recheckH(null);
      rescheduleTimeoutH();
      return;
    }
  }
  
  private void trimObsoleteH()
  {
    if (D.BUG) {
      Log.d(TAG, "trimObsoleteH");
    }
    int i = this.mRows.size() - 1;
    while (i >= 0)
    {
      VolumeRow localVolumeRow = (VolumeRow)this.mRows.get(i);
      if ((VolumeRow.-get13(localVolumeRow) != null) && (VolumeRow.-get13(localVolumeRow).dynamic) && (!this.mDynamic.get(VolumeRow.-get14(localVolumeRow))))
      {
        this.mRows.remove(i);
        this.mDialogRowsView.removeView(VolumeRow.-get17(localVolumeRow));
      }
      i -= 1;
    }
  }
  
  private void updateDialogBottomMarginH()
  {
    long l1 = System.currentTimeMillis();
    long l2 = this.mCollapseTime;
    ViewGroup.MarginLayoutParams localMarginLayoutParams;
    if ((this.mCollapseTime != 0L) && (l1 - l2 < getConservativeCollapseDuration()))
    {
      i = 1;
      localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.mDialogView.getLayoutParams();
      if (i == 0) {
        break label134;
      }
    }
    label134:
    for (int i = this.mDialogContentView.getHeight();; i = this.mContext.getResources().getDimensionPixelSize(2131755557))
    {
      if (i != localMarginLayoutParams.bottomMargin)
      {
        if (D.BUG) {
          Log.d(TAG, "bottomMargin " + localMarginLayoutParams.bottomMargin + " -> " + i);
        }
        localMarginLayoutParams.bottomMargin = i;
        this.mDialogView.setLayoutParams(localMarginLayoutParams);
      }
      return;
      i = 0;
      break;
    }
  }
  
  private void updateExpandButtonH()
  {
    if (D.BUG) {
      Log.d(TAG, "updateExpandButtonH");
    }
    Object localObject = this.mExpandButton;
    boolean bool;
    label47:
    label62:
    label77:
    Context localContext;
    if (this.mExpandButtonAnimationRunning)
    {
      bool = false;
      ((ImageButton)localObject).setClickable(bool);
      if (!this.mExpandButtonAnimationRunning) {
        break label181;
      }
      bool = isAttached();
      if (!bool)
      {
        if (!this.mExpanded) {
          break label186;
        }
        i = 2130837940;
        if (!hasTouchFeature()) {
          break label193;
        }
        this.mExpandButton.setImageResource(i);
        localObject = this.mExpandButton;
        localContext = this.mContext;
        if (!this.mExpanded) {
          break label214;
        }
      }
    }
    label181:
    label186:
    label193:
    label214:
    for (int i = 2131690430;; i = 2131690429)
    {
      ((ImageButton)localObject).setContentDescription(localContext.getString(i));
      if (this.mExpandButtonAnimationRunning)
      {
        localObject = this.mExpandButton.getDrawable();
        if ((localObject instanceof AnimatedVectorDrawable))
        {
          localObject = (AnimatedVectorDrawable)((Drawable)localObject).getConstantState().newDrawable();
          this.mExpandButton.setImageDrawable((Drawable)localObject);
          ((AnimatedVectorDrawable)localObject).start();
          this.mHandler.postDelayed(new Runnable()
          {
            public void run()
            {
              VolumeDialog.-set2(VolumeDialog.this, false);
              VolumeDialog.-wrap11(VolumeDialog.this);
              VolumeDialog.this.rescheduleTimeoutH();
            }
          }, this.mExpandButtonAnimationDuration);
        }
      }
      return;
      bool = true;
      break;
      bool = false;
      break label47;
      i = 2130837942;
      break label62;
      this.mExpandButton.setImageResource(2130837953);
      this.mExpandButton.setBackgroundResource(0);
      break label77;
    }
  }
  
  private void updateExpandedH(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mExpanded == paramBoolean1) {
      return;
    }
    this.mExpanded = paramBoolean1;
    this.mExpandButtonAnimationRunning = isAttached();
    if (D.BUG) {
      Log.d(TAG, "updateExpandedH " + paramBoolean1);
    }
    updateExpandButtonH();
    updateFooterH();
    TransitionManager.endTransitions(this.mDialogView);
    VolumeRow localVolumeRow = getActiveRow();
    if (!paramBoolean2)
    {
      this.mWindow.setLayout(this.mWindow.getAttributes().width, -1);
      AutoTransition localAutoTransition = new AutoTransition();
      localAutoTransition.setDuration(this.mExpandButtonAnimationDuration);
      localAutoTransition.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
      localAutoTransition.addListener(new Transition.TransitionListener()
      {
        public void onTransitionCancel(Transition paramAnonymousTransition) {}
        
        public void onTransitionEnd(Transition paramAnonymousTransition)
        {
          VolumeDialog.-get20(VolumeDialog.this).setLayout(VolumeDialog.-get20(VolumeDialog.this).getAttributes().width, -2);
        }
        
        public void onTransitionPause(Transition paramAnonymousTransition)
        {
          VolumeDialog.-get20(VolumeDialog.this).setLayout(VolumeDialog.-get20(VolumeDialog.this).getAttributes().width, -2);
        }
        
        public void onTransitionResume(Transition paramAnonymousTransition) {}
        
        public void onTransitionStart(Transition paramAnonymousTransition) {}
      });
      TransitionManager.beginDelayedTransition(this.mDialogView, localAutoTransition);
    }
    updateRowsH(localVolumeRow);
    rescheduleTimeoutH();
  }
  
  private void updateFooterH()
  {
    if (D.BUG) {
      Log.d(TAG, "updateFooterH");
    }
    int i;
    if (this.mZenFooter.getVisibility() == 0)
    {
      i = 1;
      if (i == 0) {}
      Util.setVisOrGone(this.mZenFooter, true);
      this.mZenFooter.update();
      if (this.mZenPanel.getVisibility() != 0) {
        break label121;
      }
      i = 1;
      label59:
      if ((this.mShowFullZen) && ((i != 0) && (0 == 0))) {
        break label126;
      }
    }
    for (;;)
    {
      Util.setVisOrGone(this.mZenPanel, false);
      if (0 != 0)
      {
        this.mZenPanel.setZenState(this.mState.zenMode);
        this.mZenPanel.setDoneListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            VolumeDialog.-wrap5(VolumeDialog.this);
            VolumeDialog.-get11(VolumeDialog.this).sendEmptyMessage(9);
          }
        });
      }
      return;
      i = 0;
      break;
      label121:
      i = 0;
      break label59;
      label126:
      prepareForCollapse();
    }
  }
  
  private void updateRowsH(VolumeRow paramVolumeRow)
  {
    if (D.BUG) {
      Log.d(TAG, "updateRowsH");
    }
    if (!this.mShowing) {
      trimObsoleteH();
    }
    Util.setVisOrGone(this.mDialogRowsView.findViewById(2131951750), this.mExpanded);
    Iterator localIterator = this.mRows.iterator();
    if (localIterator.hasNext())
    {
      VolumeRow localVolumeRow = (VolumeRow)localIterator.next();
      if (localVolumeRow == paramVolumeRow) {}
      for (boolean bool1 = true;; bool1 = false)
      {
        boolean bool2 = shouldBeVisibleH(localVolumeRow, bool1);
        Util.setVisOrGone(VolumeRow.-get17(localVolumeRow), bool2);
        if (!VolumeRow.-get17(localVolumeRow).isShown()) {
          break;
        }
        updateVolumeRowHeaderVisibleH(localVolumeRow);
        updateVolumeRowSliderTintH(localVolumeRow, bool1);
        break;
      }
    }
  }
  
  private void updateVolumeRowH(VolumeRow paramVolumeRow)
  {
    if (D.BUG) {
      Log.d(TAG, "updateVolumeRowH s=" + VolumeRow.-get14(paramVolumeRow));
    }
    if (this.mState == null) {
      return;
    }
    VolumeDialogController.StreamState localStreamState = (VolumeDialogController.StreamState)this.mState.states.get(VolumeRow.-get14(paramVolumeRow));
    if (localStreamState == null) {
      return;
    }
    VolumeRow.-set14(paramVolumeRow, localStreamState);
    if ((localStreamState.level > 0) && (VolumeRow.-get14(paramVolumeRow) != 2)) {
      VolumeRow.-set11(paramVolumeRow, localStreamState.level);
    }
    if (localStreamState.level == VolumeRow.-get11(paramVolumeRow)) {
      VolumeRow.-set12(paramVolumeRow, -1);
    }
    boolean bool3;
    boolean bool2;
    label142:
    label152:
    boolean bool1;
    label163:
    int j;
    label182:
    int k;
    label200:
    boolean bool4;
    label214:
    int m;
    label228:
    label242:
    label261:
    boolean bool5;
    label327:
    float f;
    if (VolumeRow.-get14(paramVolumeRow) == 2)
    {
      bool3 = true;
      if (VolumeRow.-get14(paramVolumeRow) != 1) {
        break label642;
      }
      bool2 = true;
      if (VolumeRow.-get14(paramVolumeRow) != 4) {
        break label648;
      }
      i = 1;
      if (VolumeRow.-get14(paramVolumeRow) != 3) {
        break label653;
      }
      bool1 = true;
      if (!bool3) {
        break label665;
      }
      if (this.mState.ringerModeInternal != 1) {
        break label659;
      }
      j = 1;
      if (!bool3) {
        break label677;
      }
      if (this.mState.ringerModeInternal != 0) {
        break label671;
      }
      k = 1;
      if (this.mState.zenMode != 3) {
        break label683;
      }
      bool4 = true;
      if (this.mState.zenMode != 2) {
        break label689;
      }
      m = 1;
      if (!bool4) {
        break label701;
      }
      if (bool3) {
        break label695;
      }
      bool1 = bool2;
      if ((localStreamState.level > 1) && (bool3) && (!bool1)) {
        break label735;
      }
      i = localStreamState.levelMax;
      if (i != VolumeRow.-get12(paramVolumeRow).getMax()) {
        VolumeRow.-get12(paramVolumeRow).setMax(i);
      }
      updateVolumeRowHeaderVisibleH(paramVolumeRow);
      Util.setText(VolumeRow.-get4(paramVolumeRow), localStreamState.name);
      if (((this.mAutomute) || (localStreamState.muteSupported)) && (!bool1)) {
        break label748;
      }
      bool5 = false;
      VolumeRow.-get5(paramVolumeRow).setEnabled(bool5);
      ImageButton localImageButton = VolumeRow.-get5(paramVolumeRow);
      if (!bool5) {
        break label754;
      }
      f = 1.0F;
      label349:
      localImageButton.setAlpha(f);
      if (j == 0) {
        break label761;
      }
      i = 2130837955;
      label364:
      m = i;
      if (bool3)
      {
        m = i;
        if (k != 0) {
          m = 2130837953;
        }
      }
      if (m != VolumeRow.-get2(paramVolumeRow))
      {
        if ((VolumeRow.-get2(paramVolumeRow) != 0) && (j != 0) && (!this.mConfigurationChange)) {
          break label856;
        }
        label413:
        VolumeRow.-set2(paramVolumeRow, m);
        VolumeRow.-get5(paramVolumeRow).setImageResource(m);
      }
      if (m != 2130837955) {
        break label866;
      }
      i = 3;
      label439:
      VolumeRow.-set9(paramVolumeRow, i);
      if (!bool5) {
        break label1076;
      }
      if (!bool3) {
        break label989;
      }
      if (j == 0) {
        break label915;
      }
      VolumeRow.-get5(paramVolumeRow).setContentDescription(this.mContext.getString(2131690450, new Object[] { localStreamState.name }));
      label489:
      if (!bool1) {
        break label1091;
      }
      bool5 = false;
      label497:
      if ((VolumeRow.-get13(paramVolumeRow).muted) && ((j != 0) || ((!bool3) && (!bool1)))) {
        break label1097;
      }
    }
    label642:
    label648:
    label653:
    label659:
    label665:
    label671:
    label677:
    label683:
    label689:
    label695:
    label701:
    label735:
    label748:
    label754:
    label761:
    label856:
    label866:
    label915:
    label989:
    label1076:
    label1091:
    label1097:
    for (int i = VolumeRow.-get13(paramVolumeRow).level;; i = 0)
    {
      j = i;
      if (bool3) {
        if (!VolumeRow.-get13(paramVolumeRow).muted)
        {
          j = i;
          if (!bool4) {}
        }
        else
        {
          j = 0;
        }
      }
      if (D.BUG) {
        Log.d(TAG, "updateVolumeRowSliderH zenMuted:" + bool1 + " isZenAlarms:" + bool4 + " isRingStream:" + bool3 + " isSystemStream" + bool2);
      }
      updateVolumeRowSliderH(paramVolumeRow, bool5, j);
      return;
      bool3 = false;
      break;
      bool2 = false;
      break label142;
      i = 0;
      break label152;
      bool1 = false;
      break label163;
      j = 0;
      break label182;
      j = 0;
      break label182;
      k = 0;
      break label200;
      k = 0;
      break label200;
      bool4 = false;
      break label214;
      m = 0;
      break label228;
      bool1 = true;
      break label242;
      if (m != 0)
      {
        if ((!bool3) && (!bool2) && (i == 0)) {
          break label242;
        }
        bool1 = true;
        break label242;
      }
      bool1 = false;
      break label242;
      VolumeRow.-set11(paramVolumeRow, localStreamState.level);
      break label261;
      bool5 = true;
      break label327;
      f = 0.5F;
      break label349;
      if ((k != 0) || (bool1))
      {
        i = VolumeRow.-get2(paramVolumeRow);
        break label364;
      }
      if (localStreamState.routedToBluetooth)
      {
        if (localStreamState.muted)
        {
          i = 2130837949;
          break label364;
        }
        i = 2130837948;
        break label364;
      }
      if ((this.mAutomute) && (localStreamState.level == 0))
      {
        i = VolumeRow.-get6(paramVolumeRow);
        break label364;
      }
      if (localStreamState.muted)
      {
        i = VolumeRow.-get6(paramVolumeRow);
        break label364;
      }
      i = VolumeRow.-get7(paramVolumeRow);
      break label364;
      this.mController.vibrate();
      break label413;
      if ((m == 2130837949) || (m == VolumeRow.-get6(paramVolumeRow)))
      {
        i = 2;
        break label439;
      }
      if ((m == 2130837948) || (m == VolumeRow.-get7(paramVolumeRow)))
      {
        i = 1;
        break label439;
      }
      i = 0;
      break label439;
      if (this.mController.hasVibrator())
      {
        VolumeRow.-get5(paramVolumeRow).setContentDescription(this.mContext.getString(2131690451, new Object[] { localStreamState.name }));
        break label489;
      }
      VolumeRow.-get5(paramVolumeRow).setContentDescription(this.mContext.getString(2131690452, new Object[] { localStreamState.name }));
      break label489;
      if ((localStreamState.muted) || ((this.mAutomute) && (localStreamState.level == 0)))
      {
        VolumeRow.-get5(paramVolumeRow).setContentDescription(this.mContext.getString(2131690450, new Object[] { localStreamState.name }));
        break label489;
      }
      VolumeRow.-get5(paramVolumeRow).setContentDescription(this.mContext.getString(2131690452, new Object[] { localStreamState.name }));
      break label489;
      VolumeRow.-get5(paramVolumeRow).setContentDescription(localStreamState.name);
      break label489;
      bool5 = true;
      break label497;
    }
  }
  
  private void updateVolumeRowHeaderVisibleH(VolumeRow paramVolumeRow)
  {
    Util.setVisOrGone(VolumeRow.-get4(paramVolumeRow), false);
  }
  
  private void updateVolumeRowSliderH(VolumeRow paramVolumeRow, boolean paramBoolean, int paramInt)
  {
    VolumeRow.-get12(paramVolumeRow).setEnabled(paramBoolean);
    if (VolumeRow.-get14(paramVolumeRow) == this.mActiveStream) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      updateVolumeRowSliderTintH(paramVolumeRow, paramBoolean);
      if (!VolumeRow.-get15(paramVolumeRow)) {
        break;
      }
      return;
    }
    int k = VolumeRow.-get12(paramVolumeRow).getProgress();
    getImpliedLevel(VolumeRow.-get12(paramVolumeRow), k);
    int i;
    if (VolumeRow.-get17(paramVolumeRow).getVisibility() == 0)
    {
      i = 1;
      if (SystemClock.uptimeMillis() - VolumeRow.-get16(paramVolumeRow) >= 1000L) {
        break label164;
      }
    }
    label164:
    for (int j = 1;; j = 0)
    {
      this.mHandler.removeMessages(3, paramVolumeRow);
      if ((!this.mShowing) || (i == 0) || (j == 0)) {
        break label170;
      }
      if (D.BUG) {
        Log.d(TAG, "inGracePeriod");
      }
      this.mHandler.sendMessageAtTime(this.mHandler.obtainMessage(3, paramVolumeRow), VolumeRow.-get16(paramVolumeRow) + 1000L);
      return;
      i = 0;
      break;
    }
    label170:
    if ((paramInt == k) && (this.mShowing) && (i != 0)) {
      return;
    }
    if (k != paramInt)
    {
      if ((!this.mShowing) || (i == 0)) {
        break label335;
      }
      if ((VolumeRow.-get0(paramVolumeRow) != null) && (VolumeRow.-get0(paramVolumeRow).isRunning()) && (VolumeRow.-get1(paramVolumeRow) == paramInt)) {
        return;
      }
      if (VolumeRow.-get0(paramVolumeRow) != null) {
        break label306;
      }
      VolumeRow.-set0(paramVolumeRow, ObjectAnimator.ofInt(VolumeRow.-get12(paramVolumeRow), "progress", new int[] { k, paramInt }));
      VolumeRow.-get0(paramVolumeRow).setInterpolator(new DecelerateInterpolator());
    }
    for (;;)
    {
      VolumeRow.-set1(paramVolumeRow, paramInt);
      VolumeRow.-get0(paramVolumeRow).setDuration(80L);
      VolumeRow.-get0(paramVolumeRow).start();
      return;
      label306:
      VolumeRow.-get0(paramVolumeRow).cancel();
      VolumeRow.-get0(paramVolumeRow).setIntValues(new int[] { k, paramInt });
    }
    label335:
    if (VolumeRow.-get0(paramVolumeRow) != null) {
      VolumeRow.-get0(paramVolumeRow).cancel();
    }
    VolumeRow.-get12(paramVolumeRow).setProgress(paramInt);
  }
  
  private void updateVolumeRowSliderTintH(VolumeRow paramVolumeRow, boolean paramBoolean)
  {
    if ((paramBoolean) && (this.mExpanded)) {
      VolumeRow.-get12(paramVolumeRow).requestFocus();
    }
    if ((paramBoolean) && (VolumeRow.-get12(paramVolumeRow).isEnabled())) {}
    for (ColorStateList localColorStateList = this.mActiveSliderTint; localColorStateList == VolumeRow.-get3(paramVolumeRow); localColorStateList = this.mInactiveSliderTint) {
      return;
    }
    VolumeRow.-set4(paramVolumeRow, localColorStateList);
    VolumeRow.-get12(paramVolumeRow).setProgressTintList(localColorStateList);
    VolumeRow.-get12(paramVolumeRow).setThumbTintList(localColorStateList);
    VolumeRow.-get12(paramVolumeRow).setProgressBackgroundTintList(this.mBackgroundSliderTint);
  }
  
  private void updateWindowWidthH()
  {
    ViewGroup.LayoutParams localLayoutParams = this.mDialogView.getLayoutParams();
    this.mDialogContentView.getLayoutParams();
    DisplayMetrics localDisplayMetrics = this.mContext.getResources().getDisplayMetrics();
    if (D.BUG) {
      Log.d(TAG, "updateWindowWidth dm.w=" + localDisplayMetrics.widthPixels);
    }
    int j = localDisplayMetrics.widthPixels;
    localLayoutParams.width = j;
    this.mDialogView.setLayoutParams(localLayoutParams);
    int k = this.mContext.getResources().getDimensionPixelSize(2131755402);
    int i = j;
    if (j > k) {
      i = k;
    }
    localLayoutParams.width = i;
    this.mDialogView.setLayoutParams(localLayoutParams);
  }
  
  protected void dismissH(int paramInt)
  {
    if (this.mMotion.isAnimating()) {
      return;
    }
    this.mHandler.removeMessages(2);
    this.mHandler.removeMessages(1);
    if (!this.mShowing) {
      return;
    }
    this.mShowing = false;
    this.mMotion.startDismiss(new Runnable()
    {
      public void run()
      {
        VolumeDialog.-wrap12(VolumeDialog.this, false, true);
      }
    });
    if (this.mAccessibilityMgr.isEnabled())
    {
      ??? = AccessibilityEvent.obtain(32);
      ((AccessibilityEvent)???).setPackageName(this.mContext.getPackageName());
      ((AccessibilityEvent)???).setClassName(CustomDialog.class.getSuperclass().getName());
      ((AccessibilityEvent)???).getText().add(this.mContext.getString(2131690454));
      this.mAccessibilityMgr.sendAccessibilityEvent((AccessibilityEvent)???);
    }
    Events.writeEvent(this.mContext, 1, new Object[] { Integer.valueOf(paramInt) });
    this.mController.notifyVisible(false);
    synchronized (this.mSafetyWarningLock)
    {
      if (this.mSafetyWarning != null)
      {
        if (D.BUG) {
          Log.d(TAG, "SafetyWarning dismissed");
        }
        this.mSafetyWarning.dismiss();
      }
      return;
    }
  }
  
  public void dismissWaitForRipple(final int paramInt)
  {
    this.mHandler.postDelayed(new Runnable()
    {
      public void run()
      {
        VolumeDialog.-get11(VolumeDialog.this).obtainMessage(2, paramInt, 0).sendToTarget();
      }
    }, 200L);
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println(VolumeDialog.class.getSimpleName() + " state:");
    paramPrintWriter.print("  mShowing: ");
    paramPrintWriter.println(this.mShowing);
    paramPrintWriter.print("  mExpanded: ");
    paramPrintWriter.println(this.mExpanded);
    paramPrintWriter.print("  mExpandButtonAnimationRunning: ");
    paramPrintWriter.println(this.mExpandButtonAnimationRunning);
    paramPrintWriter.print("  mActiveStream: ");
    paramPrintWriter.println(this.mActiveStream);
    paramPrintWriter.print("  mDynamic: ");
    paramPrintWriter.println(this.mDynamic);
    paramPrintWriter.print("  mShowHeaders: ");
    paramPrintWriter.println(this.mShowHeaders);
    paramPrintWriter.print("  mAutomute: ");
    paramPrintWriter.println(this.mAutomute);
    paramPrintWriter.print("  mSilentMode: ");
    paramPrintWriter.println(this.mSilentMode);
    paramPrintWriter.print("  mCollapseTime: ");
    paramPrintWriter.println(this.mCollapseTime);
    paramPrintWriter.print("  mAccessibility.mFeedbackEnabled: ");
    paramPrintWriter.println(Accessibility.-get0(this.mAccessibility));
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    boolean bool2 = false;
    if ("sysui_show_full_zen".equals(paramString1))
    {
      boolean bool1 = bool2;
      if (paramString2 != null)
      {
        bool1 = bool2;
        if (Integer.parseInt(paramString2) != 0) {
          bool1 = true;
        }
      }
      this.mShowFullZen = bool1;
    }
  }
  
  protected void rescheduleTimeoutH()
  {
    this.mHandler.removeMessages(2);
    int i = computeTimeoutH();
    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(2, 3, 0), i);
    if (D.BUG) {
      Log.d(TAG, "rescheduleTimeout " + i + " " + Debug.getCaller());
    }
    this.mController.userActivity();
  }
  
  public void setAutomute(boolean paramBoolean)
  {
    if (this.mAutomute == paramBoolean) {
      return;
    }
    this.mAutomute = paramBoolean;
    this.mHandler.sendEmptyMessage(4);
  }
  
  public void setSilentMode(boolean paramBoolean)
  {
    if (this.mSilentMode == paramBoolean) {
      return;
    }
    this.mSilentMode = paramBoolean;
    this.mHandler.sendEmptyMessage(4);
  }
  
  public void setStreamImportant(int paramInt, boolean paramBoolean)
  {
    H localH = this.mHandler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localH.obtainMessage(5, paramInt, i).sendToTarget();
      return;
    }
  }
  
  public void show(int paramInt)
  {
    this.mHandler.obtainMessage(1, paramInt, 0).sendToTarget();
  }
  
  private final class Accessibility
    extends View.AccessibilityDelegate
  {
    private boolean mFeedbackEnabled;
    
    private Accessibility() {}
    
    private boolean computeFeedbackEnabled()
    {
      Iterator localIterator = VolumeDialog.-get1(VolumeDialog.this).getEnabledAccessibilityServiceList(-1).iterator();
      while (localIterator.hasNext())
      {
        AccessibilityServiceInfo localAccessibilityServiceInfo = (AccessibilityServiceInfo)localIterator.next();
        if ((localAccessibilityServiceInfo != null) && (localAccessibilityServiceInfo.feedbackType != 0) && (localAccessibilityServiceInfo.feedbackType != 16)) {
          return true;
        }
      }
      return false;
    }
    
    private void updateFeedbackEnabled()
    {
      this.mFeedbackEnabled = computeFeedbackEnabled();
    }
    
    public void init()
    {
      VolumeDialog.-get7(VolumeDialog.this).addOnAttachStateChangeListener(new View.OnAttachStateChangeListener()
      {
        public void onViewAttachedToWindow(View paramAnonymousView)
        {
          if (D.BUG) {
            Log.d(VolumeDialog.-get0(), "onViewAttachedToWindow");
          }
          VolumeDialog.Accessibility.-wrap0(VolumeDialog.Accessibility.this);
        }
        
        public void onViewDetachedFromWindow(View paramAnonymousView)
        {
          if (D.BUG) {
            Log.d(VolumeDialog.-get0(), "onViewDetachedFromWindow");
          }
        }
      });
      VolumeDialog.-get7(VolumeDialog.this).setAccessibilityDelegate(this);
      VolumeDialog.-get1(VolumeDialog.this).addAccessibilityStateChangeListener(new AccessibilityManager.AccessibilityStateChangeListener()
      {
        public void onAccessibilityStateChanged(boolean paramAnonymousBoolean)
        {
          VolumeDialog.Accessibility.-wrap0(VolumeDialog.Accessibility.this);
        }
      });
      updateFeedbackEnabled();
    }
    
    public boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      VolumeDialog.this.rescheduleTimeoutH();
      return super.onRequestSendAccessibilityEvent(paramViewGroup, paramView, paramAccessibilityEvent);
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void onZenPrioritySettingsClicked();
  }
  
  private final class CustomDialog
    extends Dialog
  {
    public CustomDialog(Context paramContext)
    {
      super();
    }
    
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
    {
      paramAccessibilityEvent.setClassName(getClass().getSuperclass().getName());
      paramAccessibilityEvent.setPackageName(VolumeDialog.-get3(VolumeDialog.this).getPackageName());
      WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
      boolean bool;
      if (localLayoutParams.width == -1) {
        if (localLayoutParams.height == -1) {
          bool = true;
        }
      }
      for (;;)
      {
        paramAccessibilityEvent.setFullScreen(bool);
        if ((paramAccessibilityEvent.getEventType() != 32) || (!VolumeDialog.-get16(VolumeDialog.this))) {
          break;
        }
        paramAccessibilityEvent.getText().add(VolumeDialog.-get3(VolumeDialog.this).getString(2131690453, new Object[] { VolumeDialog.VolumeRow.-get13(VolumeDialog.-wrap1(VolumeDialog.this)).name }));
        return true;
        bool = false;
        continue;
        bool = false;
      }
      return false;
    }
    
    public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
    {
      VolumeDialog.this.rescheduleTimeoutH();
      return super.dispatchTouchEvent(paramMotionEvent);
    }
    
    protected void onStop()
    {
      super.onStop();
      boolean bool = VolumeDialog.-get12(VolumeDialog.this).isAnimating();
      if (D.BUG) {
        Log.d(VolumeDialog.-get0(), "onStop animating=" + bool);
      }
      if (bool)
      {
        VolumeDialog.-set5(VolumeDialog.this, true);
        return;
      }
      VolumeDialog.-get11(VolumeDialog.this).sendEmptyMessage(4);
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      if ((isShowing()) && (paramMotionEvent.getAction() == 4))
      {
        VolumeDialog.this.dismissH(1);
        return true;
      }
      return false;
    }
  }
  
  private final class H
    extends Handler
  {
    public H()
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool = false;
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        VolumeDialog.-wrap8(VolumeDialog.this, paramMessage.arg1);
        return;
      case 2: 
        VolumeDialog.this.dismissH(paramMessage.arg1);
        return;
      case 3: 
        VolumeDialog.-wrap6(VolumeDialog.this, (VolumeDialog.VolumeRow)paramMessage.obj);
        return;
      case 4: 
        VolumeDialog.-wrap6(VolumeDialog.this, null);
        return;
      case 5: 
        VolumeDialog localVolumeDialog = VolumeDialog.this;
        int i = paramMessage.arg1;
        if (paramMessage.arg2 != 0) {
          bool = true;
        }
        VolumeDialog.-wrap7(localVolumeDialog, i, bool);
        return;
      case 6: 
        VolumeDialog.this.rescheduleTimeoutH();
        return;
      case 7: 
        VolumeDialog.-wrap4(VolumeDialog.this, VolumeDialog.-get19(VolumeDialog.this));
        return;
      case 8: 
        VolumeDialog.-wrap10(VolumeDialog.this);
        return;
      }
      VolumeDialog.-wrap13(VolumeDialog.this);
    }
  }
  
  private static class VolumeRow
  {
    private ObjectAnimator anim;
    private int animTargetProgress;
    private int cachedIconRes;
    private boolean cachedShowHeaders = true;
    private ColorStateList cachedSliderTint;
    private TextView header;
    private ImageButton icon;
    private int iconMuteRes;
    private int iconRes;
    private int iconState;
    private boolean important;
    private int lastAudibleLevel = 1;
    private int requestedLevel = -1;
    private SeekBar slider;
    private VolumeDialogController.StreamState ss;
    private int stream;
    private boolean tracking;
    private long userAttempt;
    private View view;
  }
  
  private final class VolumeSeekBarChangeListener
    implements SeekBar.OnSeekBarChangeListener
  {
    private final VolumeDialog.VolumeRow mRow;
    
    private VolumeSeekBarChangeListener(VolumeDialog.VolumeRow paramVolumeRow)
    {
      this.mRow = paramVolumeRow;
    }
    
    public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
    {
      if (VolumeDialog.VolumeRow.-get13(this.mRow) == null) {
        return;
      }
      if (D.BUG) {
        Log.d(VolumeDialog.-get0(), AudioSystem.streamToString(VolumeDialog.VolumeRow.-get14(this.mRow)) + " onProgressChanged " + paramInt + " fromUser=" + paramBoolean);
      }
      if (!paramBoolean) {
        return;
      }
      int i = paramInt;
      if (VolumeDialog.VolumeRow.-get14(this.mRow) == 2)
      {
        i = paramInt;
        if (paramInt < 1)
        {
          i = 1;
          paramSeekBar.setProgress(1);
        }
      }
      paramInt = i;
      if (VolumeDialog.VolumeRow.-get13(this.mRow).levelMin > 0)
      {
        int j = VolumeDialog.VolumeRow.-get13(this.mRow).levelMin;
        paramInt = i;
        if (i < j)
        {
          paramSeekBar.setProgress(j);
          paramInt = j;
        }
      }
      if ((VolumeDialog.VolumeRow.-get13(this.mRow).level != paramInt) || ((VolumeDialog.VolumeRow.-get13(this.mRow).muted) && (paramInt > 0)))
      {
        VolumeDialog.VolumeRow.-set17(this.mRow, SystemClock.uptimeMillis());
        if (VolumeDialog.VolumeRow.-get11(this.mRow) != paramInt)
        {
          VolumeDialog.-get4(VolumeDialog.this).setStreamVolume(VolumeDialog.VolumeRow.-get14(this.mRow), paramInt);
          VolumeDialog.VolumeRow.-set12(this.mRow, paramInt);
          Events.writeEvent(VolumeDialog.-get3(VolumeDialog.this), 9, new Object[] { Integer.valueOf(VolumeDialog.VolumeRow.-get14(this.mRow)), Integer.valueOf(paramInt) });
        }
      }
    }
    
    public void onStartTrackingTouch(SeekBar paramSeekBar)
    {
      if (D.BUG) {
        Log.d(VolumeDialog.-get0(), "onStartTrackingTouch " + VolumeDialog.VolumeRow.-get14(this.mRow));
      }
      VolumeDialog.-get4(VolumeDialog.this).setActiveStream(VolumeDialog.VolumeRow.-get14(this.mRow));
      VolumeDialog.VolumeRow.-set16(this.mRow, true);
    }
    
    public void onStopTrackingTouch(SeekBar paramSeekBar)
    {
      if (D.BUG) {
        Log.d(VolumeDialog.-get0(), "onStopTrackingTouch " + VolumeDialog.VolumeRow.-get14(this.mRow));
      }
      VolumeDialog.VolumeRow.-set16(this.mRow, false);
      VolumeDialog.VolumeRow.-set17(this.mRow, SystemClock.uptimeMillis());
      int i = paramSeekBar.getProgress();
      Events.writeEvent(VolumeDialog.-get3(VolumeDialog.this), 16, new Object[] { Integer.valueOf(VolumeDialog.VolumeRow.-get14(this.mRow)), Integer.valueOf(i) });
      if (VolumeDialog.VolumeRow.-get13(this.mRow).level != i) {
        VolumeDialog.-get11(VolumeDialog.this).sendMessageDelayed(VolumeDialog.-get11(VolumeDialog.this).obtainMessage(3, this.mRow), 1000L);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\volume\VolumeDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */