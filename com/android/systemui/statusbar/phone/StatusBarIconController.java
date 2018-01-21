package com.android.systemui.statusbar.phone;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.BatteryDashChargeView;
import com.android.systemui.BatteryMeterView;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.Interpolators;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.plugin.LSState;
import com.android.systemui.statusbar.NetworkSpeedView;
import com.android.systemui.statusbar.NotificationData;
import com.android.systemui.statusbar.SignalClusterView;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.policy.Clock;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import com.android.systemui.util.Utils;
import java.io.PrintWriter;
import java.util.ArrayList;

public class StatusBarIconController
  extends StatusBarIconList
  implements TunerService.Tunable
{
  private static final boolean DEBUG;
  private static int PFLAG_FORCE_LAYOUT = Utils.getIntField(null, "android.view.View", "PFLAG_FORCE_LAYOUT", 0);
  private static String TAG = "StatusBarIconController";
  private static Context mContext;
  private static final int[] sTmpInt2;
  private static final Rect sTmpRect;
  private BatteryDashChargeView mBatteryDashChargeView;
  private TextView mBatteryLevelView;
  private BatteryMeterView mBatteryMeterView;
  private BatteryMeterView mBatteryMeterViewKeyguard;
  private TextView mClock;
  private float mDarkIntensity;
  private int mDarkModeIconColorSingleTone;
  private DemoStatusIcons mDemoStatusIcons;
  private final Handler mHandler;
  private final ArraySet<String> mIconBlacklist = new ArraySet();
  private int mIconHPadding;
  private int mIconSize;
  private int mIconTint = -1;
  private int mLightModeIconColorSingleTone;
  private NotificationIconAreaController mNotificationIconAreaController;
  private View mNotificationIconAreaInner;
  private float mPendingDarkIntensity;
  private PhoneStatusBar mPhoneStatusBar;
  private Runnable mReApplyIconTint = new Runnable()
  {
    public void run()
    {
      StatusBarIconController localStatusBarIconController = StatusBarIconController.this;
      StatusBarIconController.-set0(localStatusBarIconController, StatusBarIconController.-get1(localStatusBarIconController) + 1);
      StatusBarIconController.-wrap0(StatusBarIconController.this);
    }
  };
  private int mSetTintRetryTimes = 0;
  private SignalClusterView mSignalCluster;
  private LinearLayout mStatusIcons;
  private LinearLayout mStatusIconsKeyguard;
  private NetworkSpeedView mStatusbarSpeedLayout;
  private LinearLayout mSystemIconArea;
  private ValueAnimator mTintAnimator;
  private final Rect mTintArea = new Rect();
  private boolean mTintChangePending;
  private View mTraceView;
  private boolean mTransitionDeferring;
  private final Runnable mTransitionDeferringDoneRunnable = new Runnable()
  {
    public void run()
    {
      StatusBarIconController.-set1(StatusBarIconController.this, false);
    }
  };
  private long mTransitionDeferringDuration;
  private long mTransitionDeferringStartTime;
  private boolean mTransitionPending;
  private NetworkSpeedView mkeyguardSpeedLayout;
  
  static
  {
    DEBUG = Build.DEBUG_ONEPLUS;
    sTmpRect = new Rect();
    sTmpInt2 = new int[2];
  }
  
  public StatusBarIconController(Context paramContext, View paramView1, View paramView2, PhoneStatusBar paramPhoneStatusBar)
  {
    super(paramContext.getResources().getStringArray(17235984));
    mContext = paramContext;
    this.mPhoneStatusBar = paramPhoneStatusBar;
    this.mSystemIconArea = ((LinearLayout)paramView1.findViewById(2131952267));
    this.mStatusIcons = ((LinearLayout)paramView1.findViewById(2131952311));
    this.mSignalCluster = ((SignalClusterView)paramView1.findViewById(2131952234));
    this.mNotificationIconAreaController = SystemUIFactory.getInstance().createNotificationIconAreaController(paramContext, paramPhoneStatusBar);
    this.mNotificationIconAreaInner = this.mNotificationIconAreaController.getNotificationInnerAreaView();
    ((ViewGroup)paramView1.findViewById(2131952266)).addView(this.mNotificationIconAreaInner);
    this.mStatusIconsKeyguard = ((LinearLayout)paramView2.findViewById(2131952311));
    this.mBatteryMeterView = ((BatteryMeterView)paramView1.findViewById(2131952313));
    this.mBatteryMeterViewKeyguard = ((BatteryMeterView)paramView2.findViewById(2131952313));
    scaleBatteryMeterViews(paramContext);
    this.mClock = ((TextView)paramView1.findViewById(2131951896));
    this.mDarkModeIconColorSingleTone = paramContext.getColor(2131493045);
    this.mLightModeIconColorSingleTone = paramContext.getColor(2131493048);
    this.mHandler = new Handler();
    loadDimens();
    TunerService.get(mContext).addTunable(this, new String[] { "icon_blacklist" });
    this.mBatteryDashChargeView = ((BatteryDashChargeView)paramView1.findViewById(2131952314));
    this.mBatteryLevelView = ((TextView)paramView1.findViewById(2131952312));
    this.mStatusbarSpeedLayout = ((NetworkSpeedView)paramView1.findViewById(2131952309));
    this.mkeyguardSpeedLayout = ((NetworkSpeedView)paramView2.findViewById(2131952309));
    setupNetworkSpeed();
    this.mTraceView = paramView1.findViewById(2131952268);
  }
  
  public static ArraySet<String> addBlackSlotsForHighlightHint(ArraySet<String> paramArraySet)
  {
    if (mContext != null) {
      return defineSlots(paramArraySet, mContext.getResources().getStringArray(17235984));
    }
    return paramArraySet;
  }
  
  private void addSystemIcon(int paramInt, StatusBarIcon paramStatusBarIcon)
  {
    Object localObject = getSlot(paramInt);
    paramInt = getViewIndex(paramInt);
    boolean bool = this.mIconBlacklist.contains(localObject);
    StatusBarIconView localStatusBarIconView = new StatusBarIconView(mContext, (String)localObject, null, bool);
    localStatusBarIconView.set(paramStatusBarIcon);
    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-2, this.mIconSize);
    localLayoutParams.setMargins(this.mIconHPadding, 0, this.mIconHPadding, 0);
    this.mStatusIcons.addView(localStatusBarIconView, paramInt, localLayoutParams);
    localObject = new StatusBarIconView(mContext, (String)localObject, null, bool);
    ((StatusBarIconView)localObject).set(paramStatusBarIcon);
    this.mStatusIconsKeyguard.addView((View)localObject, paramInt, new LinearLayout.LayoutParams(-2, this.mIconSize));
    applyIconTint();
  }
  
  private void animateHide(final View paramView, boolean paramBoolean)
  {
    paramView.animate().cancel();
    if (!paramBoolean)
    {
      paramView.setAlpha(0.0F);
      paramView.setVisibility(4);
      return;
    }
    paramView.animate().alpha(0.0F).setDuration(160L).setStartDelay(0L).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(new Runnable()
    {
      public void run()
      {
        paramView.setVisibility(4);
      }
    });
  }
  
  private void animateIconTint(float paramFloat, long paramLong1, long paramLong2)
  {
    if (this.mTintAnimator != null) {
      this.mTintAnimator.cancel();
    }
    if (this.mDarkIntensity == paramFloat) {
      return;
    }
    this.mTintAnimator = ValueAnimator.ofFloat(new float[] { this.mDarkIntensity, paramFloat });
    this.mTintAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        StatusBarIconController.-wrap1(StatusBarIconController.this, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
      }
    });
    this.mTintAnimator.setDuration(paramLong2);
    this.mTintAnimator.setStartDelay(paramLong1);
    this.mTintAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
    this.mTintAnimator.start();
  }
  
  private void animateShow(View paramView, boolean paramBoolean)
  {
    paramView.animate().cancel();
    paramView.setVisibility(0);
    if (!paramBoolean)
    {
      paramView.setAlpha(1.0F);
      return;
    }
    paramView.animate().alpha(1.0F).setDuration(320L).setInterpolator(Interpolators.ALPHA_IN).setStartDelay(50L).withEndAction(null);
    if (this.mPhoneStatusBar.isKeyguardFadingAway()) {
      paramView.animate().setDuration(this.mPhoneStatusBar.getKeyguardFadingAwayDuration()).setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN).setStartDelay(this.mPhoneStatusBar.getKeyguardFadingAwayDelay()).start();
    }
  }
  
  private void applyIconTint()
  {
    if ((isLayoutReadyBeforeApplyIconTint()) && (this.mSetTintRetryTimes > 1))
    {
      this.mHandler.removeCallbacks(this.mReApplyIconTint);
      this.mSetTintRetryTimes = 0;
      int i = 0;
      while (i < this.mStatusIcons.getChildCount())
      {
        localObject = (StatusBarIconView)this.mStatusIcons.getChildAt(i);
        ((StatusBarIconView)localObject).setImageTintList(ColorStateList.valueOf(getTint(this.mTintArea, (View)localObject, this.mIconTint)));
        i += 1;
      }
      this.mSignalCluster.setIconTint(this.mIconTint, this.mDarkIntensity, this.mTintArea);
      Object localObject = this.mBatteryMeterView;
      if (isInArea(this.mTintArea, this.mBatteryMeterView)) {}
      for (float f = this.mDarkIntensity;; f = 0.0F)
      {
        ((BatteryMeterView)localObject).setDarkIntensity(f);
        this.mClock.setTextColor(getTint(this.mTintArea, this.mClock, this.mIconTint));
        this.mBatteryLevelView.setTextColor(getTint(this.mTintArea, this.mBatteryLevelView, this.mIconTint));
        if (this.mBatteryDashChargeView != null) {
          this.mBatteryDashChargeView.setIconTint(this.mIconTint, this.mDarkIntensity, this.mTintArea);
        }
        setupNetworkSpeed();
        return;
      }
    }
    this.mHandler.postDelayed(this.mReApplyIconTint, 100L);
  }
  
  public static ArraySet<String> defineSlots(ArraySet<String> paramArraySet, String[] paramArrayOfString)
  {
    int j = paramArrayOfString.length;
    int i = 0;
    if (i < j)
    {
      if ((paramArraySet.contains(paramArrayOfString[i])) || ("phone_signal".equals(paramArrayOfString[i]))) {}
      for (;;)
      {
        if (DEBUG) {
          Log.i(TAG, " defineSlots i" + i + " slots:" + paramArrayOfString[i]);
        }
        i += 1;
        break;
        if ((!"battery".equals(paramArrayOfString[i])) && (!"clock".equals(paramArrayOfString[i]))) {
          paramArraySet.add(paramArrayOfString[i]);
        }
      }
    }
    return paramArraySet;
  }
  
  public static float getDarkIntensity(Rect paramRect, View paramView, float paramFloat)
  {
    if (isInArea(paramRect, paramView)) {
      return paramFloat;
    }
    return 0.0F;
  }
  
  public static ArraySet<String> getIconBlacklist(String paramString)
  {
    ArraySet localArraySet = new ArraySet();
    String str = paramString;
    if (paramString == null) {
      str = "rotate,networkspeed";
    }
    paramString = str.split(",");
    int i = 0;
    int j = paramString.length;
    while (i < j)
    {
      str = paramString[i];
      if (!TextUtils.isEmpty(str)) {
        localArraySet.add(str);
      }
      i += 1;
    }
    paramString = localArraySet;
    if (LSState.getInstance().isHighHintShow()) {
      paramString = addBlackSlotsForHighlightHint(localArraySet);
    }
    return paramString;
  }
  
  public static int getTint(Rect paramRect, View paramView, int paramInt)
  {
    if (isInArea(paramRect, paramView)) {
      return paramInt;
    }
    return -1;
  }
  
  private void handleSet(int paramInt, StatusBarIcon paramStatusBarIcon)
  {
    paramInt = getViewIndex(paramInt);
    ((StatusBarIconView)this.mStatusIcons.getChildAt(paramInt)).set(paramStatusBarIcon);
    ((StatusBarIconView)this.mStatusIconsKeyguard.getChildAt(paramInt)).set(paramStatusBarIcon);
    applyIconTint();
  }
  
  private static boolean isInArea(Rect paramRect, View paramView)
  {
    if (paramRect.isEmpty()) {
      return true;
    }
    sTmpRect.set(paramRect);
    paramView.getLocationOnScreen(sTmpInt2);
    int i = sTmpInt2[0];
    int j = Math.max(i, paramRect.left);
    j = Math.max(0, Math.min(paramView.getWidth() + i, paramRect.right) - j);
    boolean bool;
    if (paramRect.top <= 0)
    {
      bool = true;
      if (paramView.getWidth() <= 0) {
        break label105;
      }
      if (j * 2 < paramView.getWidth()) {
        break label100;
      }
      i = 1;
    }
    for (;;)
    {
      if (i == 0) {
        break label131;
      }
      return bool;
      bool = false;
      break;
      label100:
      i = 0;
      continue;
      label105:
      if ((i >= paramRect.left) && (i <= paramRect.right)) {
        i = 1;
      } else {
        i = 0;
      }
    }
    label131:
    return false;
  }
  
  private boolean isLayoutReadyBeforeApplyIconTint()
  {
    if (this.mTraceView == null) {
      return false;
    }
    int i = Utils.getIntField(this.mTraceView, "android.view.View", "mPrivateFlags", 0);
    return (PFLAG_FORCE_LAYOUT & i) == 0;
  }
  
  private void loadDimens()
  {
    this.mIconSize = mContext.getResources().getDimensionPixelSize(17104928);
    this.mIconHPadding = mContext.getResources().getDimensionPixelSize(2131755380);
  }
  
  private void scaleBatteryMeterViews(Context paramContext)
  {
    paramContext = paramContext.getResources();
    TypedValue localTypedValue = new TypedValue();
    paramContext.getValue(2131755367, localTypedValue, true);
    float f = localTypedValue.getFloat();
    int i = paramContext.getDimensionPixelSize(2131755360);
    int j = paramContext.getDimensionPixelSize(2131755361);
    int k = paramContext.getDimensionPixelSize(2131755530);
    paramContext = new LinearLayout.LayoutParams((int)(j * f), (int)(i * f));
    paramContext.setMarginsRelative(0, 0, 0, k);
    this.mBatteryMeterView.setLayoutParams(paramContext);
    this.mBatteryMeterViewKeyguard.setLayoutParams(paramContext);
  }
  
  private void setHeightAndCenter(ImageView paramImageView, int paramInt)
  {
    ViewGroup.LayoutParams localLayoutParams = paramImageView.getLayoutParams();
    localLayoutParams.height = paramInt;
    if ((localLayoutParams instanceof LinearLayout.LayoutParams)) {
      ((LinearLayout.LayoutParams)localLayoutParams).gravity = 16;
    }
    paramImageView.setLayoutParams(localLayoutParams);
  }
  
  private void setIconTintInternal(float paramFloat)
  {
    if (DEBUG) {
      Log.i("StatusBarIconController", "setIconTintInternal:" + paramFloat);
    }
    this.mDarkIntensity = paramFloat;
    this.mIconTint = ((Integer)ArgbEvaluator.getInstance().evaluate(paramFloat, Integer.valueOf(this.mLightModeIconColorSingleTone), Integer.valueOf(this.mDarkModeIconColorSingleTone))).intValue();
    this.mNotificationIconAreaController.setIconTint(this.mIconTint);
    applyIconTint();
  }
  
  private void setupNetworkSpeed()
  {
    if (this.mStatusbarSpeedLayout != null) {
      this.mStatusbarSpeedLayout.setIconTint(this.mIconTint, this.mDarkIntensity, this.mTintArea);
    }
    if (this.mkeyguardSpeedLayout != null) {
      this.mkeyguardSpeedLayout.setIconTint(this.mIconTint, this.mDarkIntensity, this.mTintArea);
    }
  }
  
  private void updateClock()
  {
    FontSizeUtils.updateFontSize(this.mClock, 2131755362);
    this.mClock.setPaddingRelative(0, 0, mContext.getResources().getDimensionPixelSize(2131755364), 0);
  }
  
  public void appTransitionCancelled()
  {
    if ((this.mTransitionPending) && (this.mTintChangePending))
    {
      this.mTintChangePending = false;
      animateIconTint(this.mPendingDarkIntensity, 0L, 120L);
    }
    this.mTransitionPending = false;
  }
  
  public void appTransitionPending()
  {
    this.mTransitionPending = true;
  }
  
  public void appTransitionStarting(long paramLong1, long paramLong2)
  {
    if ((this.mTransitionPending) && (this.mTintChangePending))
    {
      this.mTintChangePending = false;
      animateIconTint(this.mPendingDarkIntensity, Math.max(0L, paramLong1 - SystemClock.uptimeMillis()), paramLong2);
    }
    for (;;)
    {
      this.mTransitionPending = false;
      return;
      if (this.mTransitionPending)
      {
        this.mTransitionDeferring = true;
        this.mTransitionDeferringStartTime = paramLong1;
        this.mTransitionDeferringDuration = paramLong2;
        this.mHandler.removeCallbacks(this.mTransitionDeferringDoneRunnable);
        this.mHandler.postAtTime(this.mTransitionDeferringDoneRunnable, paramLong1);
      }
    }
  }
  
  public void dispatchDemoCommand(String paramString, Bundle paramBundle)
  {
    if (this.mDemoStatusIcons == null) {
      this.mDemoStatusIcons = new DemoStatusIcons(this.mStatusIcons, this.mIconSize);
    }
    this.mDemoStatusIcons.dispatchDemoCommand(paramString, paramBundle);
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    int j = this.mStatusIcons.getChildCount();
    paramPrintWriter.println("  icon views: " + j);
    int i = 0;
    while (i < j)
    {
      StatusBarIconView localStatusBarIconView = (StatusBarIconView)this.mStatusIcons.getChildAt(i);
      paramPrintWriter.println("    [" + i + "] icon=" + localStatusBarIconView + " visibility =" + localStatusBarIconView.getVisibility());
      i += 1;
    }
    super.dump(paramPrintWriter);
  }
  
  public int getSystemIconAreaWidth()
  {
    int i = mContext.getResources().getDimensionPixelSize(2131755725);
    return this.mSystemIconArea.getWidth() + i;
  }
  
  public void hideNotificationIconArea(boolean paramBoolean)
  {
    animateHide(this.mNotificationIconAreaInner, paramBoolean);
  }
  
  public void hideSystemIconArea(boolean paramBoolean)
  {
    animateHide(this.mSystemIconArea, paramBoolean);
  }
  
  public void onDensityOrFontScaleChanged()
  {
    loadDimens();
    this.mNotificationIconAreaController.onDensityOrFontScaleChanged(mContext);
    updateClock();
    int i = 0;
    while (i < this.mStatusIcons.getChildCount())
    {
      View localView = this.mStatusIcons.getChildAt(i);
      LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-2, this.mIconSize);
      localLayoutParams.setMargins(this.mIconHPadding, 0, this.mIconHPadding, 0);
      localView.setLayoutParams(localLayoutParams);
      i += 1;
    }
    i = 0;
    while (i < this.mStatusIconsKeyguard.getChildCount())
    {
      this.mStatusIconsKeyguard.getChildAt(i).setLayoutParams(new LinearLayout.LayoutParams(-2, this.mIconSize));
      i += 1;
    }
    scaleBatteryMeterViews(mContext);
    setupNetworkSpeed();
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    if (!"icon_blacklist".equals(paramString1)) {
      return;
    }
    this.mIconBlacklist.clear();
    this.mIconBlacklist.addAll(getIconBlacklist(paramString2));
    paramString1 = new ArrayList();
    int i = 0;
    while (i < this.mStatusIcons.getChildCount())
    {
      paramString1.add((StatusBarIconView)this.mStatusIcons.getChildAt(i));
      i += 1;
    }
    i = paramString1.size() - 1;
    while (i >= 0)
    {
      removeIcon(((StatusBarIconView)paramString1.get(i)).getSlot());
      i -= 1;
    }
    i = 0;
    while (i < paramString1.size())
    {
      setIcon(((StatusBarIconView)paramString1.get(i)).getSlot(), ((StatusBarIconView)paramString1.get(i)).getStatusBarIcon());
      i += 1;
    }
    this.mHandler.postDelayed(new Runnable()
    {
      public void run()
      {
        StatusBarIconController.-get0(StatusBarIconController.this).resetHighlightHintLayout();
      }
    }, 100L);
  }
  
  public void removeIcon(int paramInt)
  {
    if (getIcon(paramInt) == null) {
      return;
    }
    super.removeIcon(paramInt);
    paramInt = getViewIndex(paramInt);
    this.mStatusIcons.removeViewAt(paramInt);
    this.mStatusIconsKeyguard.removeViewAt(paramInt);
  }
  
  public void removeIcon(String paramString)
  {
    removeIcon(getSlotIndex(paramString));
  }
  
  public void setClockVisibility(boolean paramBoolean)
  {
    TextView localTextView = this.mClock;
    if (paramBoolean) {}
    for (int i = 0;; i = 8)
    {
      localTextView.setVisibility(i);
      return;
    }
  }
  
  public void setExternalIcon(String paramString)
  {
    int i = getViewIndex(getSlotIndex(paramString));
    int j = mContext.getResources().getDimensionPixelSize(2131755378);
    paramString = (ImageView)this.mStatusIcons.getChildAt(i);
    paramString.setScaleType(ImageView.ScaleType.FIT_CENTER);
    paramString.setAdjustViewBounds(true);
    setHeightAndCenter(paramString, j);
    paramString = (ImageView)this.mStatusIconsKeyguard.getChildAt(i);
    paramString.setScaleType(ImageView.ScaleType.FIT_CENTER);
    paramString.setAdjustViewBounds(true);
    setHeightAndCenter(paramString, j);
  }
  
  public void setIcon(int paramInt, StatusBarIcon paramStatusBarIcon)
  {
    if (paramStatusBarIcon == null)
    {
      removeIcon(paramInt);
      return;
    }
    if (getIcon(paramInt) == null) {}
    for (int i = 1;; i = 0)
    {
      super.setIcon(paramInt, paramStatusBarIcon);
      if (i == 0) {
        break;
      }
      addSystemIcon(paramInt, paramStatusBarIcon);
      return;
    }
    handleSet(paramInt, paramStatusBarIcon);
  }
  
  public void setIcon(String paramString, int paramInt, CharSequence paramCharSequence)
  {
    int i = getSlotIndex(paramString);
    StatusBarIcon localStatusBarIcon = getIcon(i);
    if (localStatusBarIcon == null)
    {
      setIcon(paramString, new StatusBarIcon(UserHandle.SYSTEM, mContext.getPackageName(), Icon.createWithResource(mContext, paramInt), 0, 0, paramCharSequence));
      return;
    }
    localStatusBarIcon.icon = Icon.createWithResource(mContext, paramInt);
    localStatusBarIcon.contentDescription = paramCharSequence;
    handleSet(i, localStatusBarIcon);
  }
  
  public void setIcon(String paramString, StatusBarIcon paramStatusBarIcon)
  {
    setIcon(getSlotIndex(paramString), paramStatusBarIcon);
  }
  
  public void setIconVisibility(String paramString, boolean paramBoolean)
  {
    int i = getSlotIndex(paramString);
    StatusBarIcon localStatusBarIcon = getIcon(i);
    if ((localStatusBarIcon == null) || (localStatusBarIcon.visible == paramBoolean)) {
      return;
    }
    Log.i(TAG, " setIconVisibility slot:" + paramString + " visibility:" + paramBoolean);
    localStatusBarIcon.visible = paramBoolean;
    handleSet(i, localStatusBarIcon);
    this.mHandler.postDelayed(new Runnable()
    {
      public void run()
      {
        StatusBarIconController.-get0(StatusBarIconController.this).resetHighlightHintLayout();
      }
    }, 100L);
  }
  
  public void setIconsDark(boolean paramBoolean1, boolean paramBoolean2)
  {
    float f = 1.0F;
    boolean bool;
    if (DEBUG)
    {
      StringBuilder localStringBuilder = new StringBuilder().append("setIconsDark dark:").append(paramBoolean1).append(" animate:").append(paramBoolean2).append(" currentDark:");
      if (this.mDarkIntensity != 0.0F)
      {
        bool = true;
        Log.i("StatusBarIconController", bool + " mTransitionPending:" + this.mTransitionPending + " mTransitionDeferring:" + this.mTransitionDeferring + " mPendingDarkIntensity:" + this.mPendingDarkIntensity);
      }
    }
    else
    {
      if (paramBoolean2) {
        break label151;
      }
      if (this.mTintAnimator != null) {
        this.mTintAnimator.cancel();
      }
      if (!paramBoolean1) {
        break label146;
      }
    }
    label146:
    for (f = 1.0F;; f = 0.0F)
    {
      setIconTintInternal(f);
      return;
      bool = false;
      break;
    }
    label151:
    if (paramBoolean1) {}
    for (;;)
    {
      animateIconTint(f, 0L, 120L);
      return;
      f = 0.0F;
    }
  }
  
  public void setIconsDarkArea(Rect paramRect)
  {
    if ((paramRect == null) && (this.mTintArea.isEmpty())) {
      return;
    }
    if (paramRect == null) {
      this.mTintArea.setEmpty();
    }
    for (;;)
    {
      applyIconTint();
      this.mNotificationIconAreaController.setTintArea(paramRect);
      return;
      this.mTintArea.set(paramRect);
    }
  }
  
  public void setNotificationIconAreaVisiable(boolean paramBoolean)
  {
    this.mNotificationIconAreaController.setNotificationIconAreaVisiable(paramBoolean);
    if ((this.mClock != null) && ((this.mClock instanceof Clock))) {
      ((Clock)this.mClock).updateShowSeconds();
    }
  }
  
  public void setSignalCluster(SignalClusterView paramSignalClusterView)
  {
    this.mSignalCluster = paramSignalClusterView;
  }
  
  public void showNotificationIconArea(boolean paramBoolean)
  {
    animateShow(this.mNotificationIconAreaInner, paramBoolean);
  }
  
  public void showSystemIconArea(boolean paramBoolean)
  {
    animateShow(this.mSystemIconArea, paramBoolean);
  }
  
  public void updateNotificationIcons(NotificationData paramNotificationData)
  {
    this.mNotificationIconAreaController.updateNotificationIcons(paramNotificationData);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\StatusBarIconController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */