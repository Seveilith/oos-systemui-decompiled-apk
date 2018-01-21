package com.android.systemui.statusbar.phone;

import android.app.AlarmManager.AlarmClockInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.UserManager;
import android.text.TextUtils.TruncateAt;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.keyguard.CarrierText;
import com.android.keyguard.KeyguardStatusView;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.qs.QSPanel;
import com.android.systemui.qs.QSPanel.Callback;
import com.android.systemui.qs.QuickQSPanel;
import com.android.systemui.qs.TouchAnimator;
import com.android.systemui.qs.TouchAnimator.Builder;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.DateView;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.NextAlarmController.NextAlarmChangeCallback;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.UserInfoController.OnUserInfoChangedListener;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.MdmLogger;
import com.android.systemui.util.ThemeColorUtils;
import com.android.systemui.util.Utils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class QuickStatusBarHeader
  extends BaseStatusBarHeader
  implements NextAlarmController.NextAlarmChangeCallback, View.OnClickListener, UserInfoController.OnUserInfoChangedListener
{
  private ActivityStarter mActivityStarter;
  private boolean mAlarmShowing;
  private TextView mAlarmStatus;
  private ImageView mAlarmStatusCollapsed;
  private TouchAnimator mAlarmTranslation;
  private CarrierText mCarrierText;
  private float mDateScaleFactor;
  private ViewGroup mDateTimeAlarmGroup;
  private ViewGroup mDateTimeGroup;
  private float mDateTimeTranslationX;
  private float mDateTimeTranslationY;
  private DateView mDateView;
  private ImageButton mEditButton;
  protected ExpandableIndicator mExpandIndicator;
  private boolean mExpanded;
  private float mExpansionAmount;
  private TouchAnimator mFirstHalfAnimator;
  protected float mGearTranslation;
  private QuickQSPanel mHeaderQsPanel;
  private QSTileHost mHost;
  private boolean mListening;
  private ImageView mMultiUserAvatar;
  protected MultiUserSwitch mMultiUserSwitch;
  private AlarmManager.AlarmClockInfo mNextAlarm;
  private NextAlarmController mNextAlarmController;
  private float mNoAlarmOffset;
  private QSPanel mQsPanel;
  private TouchAnimator mSecondHalfAnimator;
  protected TouchAnimator mSettingsAlpha;
  private SettingsButton mSettingsButton;
  protected View mSettingsContainer;
  private boolean mShowEmergencyCallsOnly;
  private boolean mShowFullAlarm;
  private ImageView mSmallAlarm;
  
  public QuickStatusBarHeader(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private String getNextAlarmDate(AlarmManager.AlarmClockInfo paramAlarmClockInfo)
  {
    Object localObject = null;
    if (paramAlarmClockInfo != null)
    {
      long l = paramAlarmClockInfo.getTriggerTime();
      localObject = Calendar.getInstance();
      ((Calendar)localObject).setTimeInMillis(l);
      Calendar localCalendar = Calendar.getInstance();
      localCalendar.add(6, 7);
      if (((Calendar)localObject).after(localCalendar))
      {
        Locale localLocale;
        if (DateFormat.is24HourFormat(getContext()))
        {
          paramAlarmClockInfo = "Hm";
          localLocale = Locale.getDefault();
          if (((Calendar)localObject).get(1) != localCalendar.get(1)) {
            break label130;
          }
        }
        label130:
        for (paramAlarmClockInfo = "EMMMMd" + paramAlarmClockInfo;; paramAlarmClockInfo = "EYYYYMMMMd" + paramAlarmClockInfo)
        {
          return new SimpleDateFormat(DateFormat.getBestDateTimePattern(localLocale, paramAlarmClockInfo), localLocale).format(((Calendar)localObject).getTime());
          paramAlarmClockInfo = "hma";
          break;
        }
      }
      localObject = KeyguardStatusView.formatNextAlarm(getContext(), paramAlarmClockInfo);
    }
    return (String)localObject;
  }
  
  private void startSettingsActivity()
  {
    this.mActivityStarter.startActivity(new Intent("android.settings.SETTINGS"), true);
  }
  
  private void updateAlarmVisibilities()
  {
    int j = 0;
    Object localObject = this.mAlarmStatus;
    if ((this.mAlarmShowing) && (this.mShowFullAlarm))
    {
      i = 0;
      ((TextView)localObject).setVisibility(i);
      localObject = this.mAlarmStatusCollapsed;
      if (!this.mAlarmShowing) {
        break label53;
      }
    }
    label53:
    for (int i = j;; i = 4)
    {
      ((ImageView)localObject).setVisibility(i);
      return;
      i = 4;
      break;
    }
  }
  
  private void updateDateTimePosition()
  {
    boolean bool = isLayoutRtl();
    Object localObject1 = new TouchAnimator.Builder();
    Object localObject2 = this.mCarrierText;
    if (bool)
    {
      f = -this.mDateTimeTranslationX;
      localObject1 = ((TouchAnimator.Builder)localObject1).addFloat(localObject2, "translationX", new float[] { 0.0F, f });
      localObject2 = this.mDateTimeAlarmGroup;
      if (!bool) {
        break label190;
      }
      f = -this.mDateTimeTranslationX;
      label65:
      localObject1 = ((TouchAnimator.Builder)localObject1).addFloat(localObject2, "translationX", new float[] { 0.0F, f });
      localObject2 = this.mDateTimeAlarmGroup;
      if ((this.mAlarmShowing) || (!this.mShowFullAlarm)) {
        break label198;
      }
      f = this.mDateTimeTranslationY + this.mNoAlarmOffset;
      label115:
      this.mAlarmTranslation = ((TouchAnimator.Builder)localObject1).addFloat(localObject2, "translationY", new float[] { 0.0F, f }).build();
      this.mAlarmTranslation.setPosition(this.mExpansionAmount);
      localObject1 = this.mCarrierText;
      if ((this.mAlarmShowing) || (!this.mShowFullAlarm)) {
        break label206;
      }
    }
    label190:
    label198:
    label206:
    for (float f = this.mNoAlarmOffset;; f = 0.0F)
    {
      ((CarrierText)localObject1).setTranslationY(f);
      return;
      f = this.mDateTimeTranslationX;
      break;
      f = this.mDateTimeTranslationX;
      break label65;
      f = this.mDateTimeTranslationY;
      break label115;
    }
  }
  
  private void updateListeners()
  {
    if (this.mListening)
    {
      this.mNextAlarmController.addStateChangedCallback(this);
      return;
    }
    this.mNextAlarmController.removeStateChangedCallback(this);
  }
  
  private void updateResources()
  {
    FontSizeUtils.updateFontSize(this.mAlarmStatus, 2131755502);
    FontSizeUtils.updateFontSize(this.mCarrierText, 2131755501);
    this.mGearTranslation = this.mContext.getResources().getDimension(2131755417);
    this.mDateTimeTranslationX = this.mContext.getResources().getDimension(2131755709);
    this.mDateTimeTranslationY = this.mContext.getResources().getDimension(2131755710);
    this.mNoAlarmOffset = this.mContext.getResources().getDimension(2131755711);
    float f = this.mContext.getResources().getDimension(2131755415);
    this.mDateScaleFactor = (this.mContext.getResources().getDimension(2131755416) / f);
    updateDateTimePosition();
    TouchAnimator.Builder localBuilder = new TouchAnimator.Builder().addFloat(this.mCarrierText, "alpha", new float[] { 0.0F, 1.0F });
    if (this.mShowFullAlarm)
    {
      localObject = this.mAlarmStatus;
      localBuilder = localBuilder.addFloat(localObject, "alpha", new float[] { 0.0F, 1.0F });
      if (!this.mShowFullAlarm) {
        break label284;
      }
    }
    label284:
    for (Object localObject = this.mAlarmStatus;; localObject = this.mSmallAlarm)
    {
      this.mSecondHalfAnimator = localBuilder.addFloat(localObject, "alpha", new float[] { 0.0F, 1.0F }).setStartDelay(0.5F).build();
      this.mFirstHalfAnimator = new TouchAnimator.Builder().addFloat(this.mAlarmStatusCollapsed, "alpha", new float[] { 1.0F, 0.0F }).setEndDelay(0.5F).build();
      updateSettingsAnimator();
      return;
      localObject = this.mDateView;
      break;
    }
  }
  
  public int getCollapsedHeight()
  {
    return getHeight();
  }
  
  public void onClick(View paramView)
  {
    int i;
    if (paramView == this.mSettingsButton)
    {
      paramView = this.mContext;
      if (this.mExpanded)
      {
        i = 406;
        MetricsLogger.action(paramView, i);
        MdmLogger.log("quick_setting", "", "1");
        if (!this.mSettingsButton.isTunerClick()) {
          break label74;
        }
        this.mHost.startRunnableDismissingKeyguard(new -void_onClick_android_view_View_v_LambdaImpl0());
      }
    }
    label74:
    do
    {
      do
      {
        return;
        i = 490;
        break;
        startSettingsActivity();
        return;
      } while ((paramView != this.mAlarmStatus) || (this.mNextAlarm == null));
      paramView = this.mNextAlarm.getShowIntent();
    } while (paramView == null);
    this.mActivityStarter.startPendingIntentDismissingKeyguard(paramView);
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    updateResources();
  }
  
  protected void onDetachedFromWindow()
  {
    setListening(false);
    this.mHost.getUserInfoController().remListener(this);
    this.mHost.getNetworkController().removeEmergencyListener(this);
    super.onDetachedFromWindow();
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mCarrierText = ((CarrierText)findViewById(2131952175));
    this.mDateTimeAlarmGroup = ((ViewGroup)findViewById(2131952176));
    this.mDateTimeAlarmGroup.findViewById(2131952261).setVisibility(8);
    this.mDateTimeGroup = ((ViewGroup)findViewById(2131952178));
    this.mDateTimeGroup.setPivotX(0.0F);
    this.mDateTimeGroup.setPivotY(0.0F);
    this.mShowFullAlarm = getResources().getBoolean(2131558437);
    this.mExpandIndicator = ((ExpandableIndicator)findViewById(2131952174));
    this.mDateView = ((DateView)findViewById(2131952270));
    this.mSmallAlarm = ((ImageView)findViewById(2131952271));
    this.mHeaderQsPanel = ((QuickQSPanel)findViewById(2131952177));
    this.mSettingsButton = ((SettingsButton)findViewById(2131952170));
    this.mSettingsContainer = findViewById(2131952169);
    this.mSettingsButton.setOnClickListener(this);
    this.mEditButton = ((ImageButton)findViewById(2131952173));
    this.mEditButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(final View paramAnonymousView)
      {
        QuickStatusBarHeader.-get2(QuickStatusBarHeader.this).startRunnableDismissingKeyguard(new Runnable()
        {
          public void run()
          {
            if (QuickStatusBarHeader.-get1(QuickStatusBarHeader.this) == 1.0F)
            {
              MdmLogger.log("quick_edit", "", "1");
              QuickStatusBarHeader.-get3(QuickStatusBarHeader.this).showEdit(paramAnonymousView);
              return;
            }
            Log.d("QuickStatusBarHeader", "Not fully expanded, skip this click");
          }
        });
      }
    });
    this.mAlarmStatusCollapsed = ((ImageView)findViewById(2131952269));
    this.mAlarmStatus = ((TextView)findViewById(2131952272));
    this.mAlarmStatus.setOnClickListener(this);
    this.mMultiUserSwitch = ((MultiUserSwitch)findViewById(2131951919));
    this.mMultiUserAvatar = ((ImageView)this.mMultiUserSwitch.findViewById(2131951920));
    ((RippleDrawable)this.mSettingsButton.getBackground()).setForceSoftware(true);
    ((RippleDrawable)this.mExpandIndicator.getBackground()).setForceSoftware(true);
    updateResources();
    updateThemeColor();
  }
  
  public void onNextAlarmChanged(AlarmManager.AlarmClockInfo paramAlarmClockInfo)
  {
    boolean bool2 = true;
    int i = 0;
    this.mNextAlarm = paramAlarmClockInfo;
    if (paramAlarmClockInfo != null)
    {
      String str = getNextAlarmDate(paramAlarmClockInfo);
      this.mAlarmStatus.setText(str);
      this.mAlarmStatus.setContentDescription(this.mContext.getString(2131690219, new Object[] { str }));
      this.mAlarmStatusCollapsed.setContentDescription(this.mContext.getString(2131690219, new Object[] { str }));
    }
    boolean bool3 = this.mAlarmShowing;
    boolean bool1;
    if (paramAlarmClockInfo != null)
    {
      bool1 = true;
      if (bool3 != bool1)
      {
        if (paramAlarmClockInfo == null) {
          break label153;
        }
        bool1 = bool2;
        label107:
        this.mAlarmShowing = bool1;
        updateEverything();
      }
      if ((!this.mShowFullAlarm) && (this.mSmallAlarm != null))
      {
        paramAlarmClockInfo = this.mSmallAlarm;
        if (!this.mAlarmShowing) {
          break label158;
        }
      }
    }
    for (;;)
    {
      paramAlarmClockInfo.setVisibility(i);
      return;
      bool1 = false;
      break;
      label153:
      bool1 = false;
      break label107;
      label158:
      i = 4;
    }
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    super.onRtlPropertiesChanged(paramInt);
    updateResources();
  }
  
  public void onUserInfoChanged(String paramString, Drawable paramDrawable)
  {
    this.mMultiUserAvatar.setImageDrawable(paramDrawable);
  }
  
  public void setActivityStarter(ActivityStarter paramActivityStarter)
  {
    this.mActivityStarter = paramActivityStarter;
  }
  
  public void setBatteryController(BatteryController paramBatteryController) {}
  
  public void setCallback(QSPanel.Callback paramCallback)
  {
    this.mHeaderQsPanel.setCallback(paramCallback);
  }
  
  public void setEmergencyCallsOnly(boolean paramBoolean)
  {
    if (paramBoolean != this.mShowEmergencyCallsOnly) {}
    for (int i = 1;; i = 0)
    {
      if (i != 0)
      {
        this.mShowEmergencyCallsOnly = paramBoolean;
        if (this.mExpanded) {
          updateEverything();
        }
      }
      return;
    }
  }
  
  public void setExpanded(boolean paramBoolean)
  {
    if (this.mExpanded == paramBoolean) {
      return;
    }
    this.mExpanded = paramBoolean;
    this.mHeaderQsPanel.setExpanded(paramBoolean);
    updateEverything();
  }
  
  public void setExpansion(float paramFloat)
  {
    this.mExpansionAmount = paramFloat;
    this.mSecondHalfAnimator.setPosition(paramFloat);
    this.mFirstHalfAnimator.setPosition(paramFloat);
    this.mAlarmTranslation.setPosition(paramFloat);
    this.mSettingsAlpha.setPosition(paramFloat);
    updateAlarmVisibilities();
    ExpandableIndicator localExpandableIndicator = this.mExpandIndicator;
    if (paramFloat > 0.93F) {}
    for (boolean bool = true;; bool = false)
    {
      localExpandableIndicator.setExpanded(bool);
      return;
    }
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (paramBoolean == this.mListening) {
      return;
    }
    this.mHeaderQsPanel.setListening(paramBoolean);
    this.mListening = paramBoolean;
    updateListeners();
  }
  
  public void setNextAlarmController(NextAlarmController paramNextAlarmController)
  {
    this.mNextAlarmController = paramNextAlarmController;
  }
  
  public void setQSPanel(QSPanel paramQSPanel)
  {
    this.mQsPanel = paramQSPanel;
    setupHost(paramQSPanel.getHost());
    if (this.mQsPanel != null) {
      this.mMultiUserSwitch.setQsPanel(paramQSPanel);
    }
  }
  
  public void setUserInfoController(UserInfoController paramUserInfoController)
  {
    paramUserInfoController.addListener(this);
  }
  
  public void setupHost(QSTileHost paramQSTileHost)
  {
    this.mHost = paramQSTileHost;
    paramQSTileHost.setHeaderView(this.mExpandIndicator);
    this.mHeaderQsPanel.setQSPanelAndHeader(this.mQsPanel, this);
    this.mHeaderQsPanel.setHost(paramQSTileHost, null);
    setUserInfoController(paramQSTileHost.getUserInfoController());
    setBatteryController(paramQSTileHost.getBatteryController());
    setNextAlarmController(paramQSTileHost.getNextAlarmController());
    if (this.mHost.getNetworkController().hasVoiceCallingFeature()) {
      this.mHost.getNetworkController().addEmergencyListener(this);
    }
  }
  
  public void updateEverything()
  {
    post(new -void_updateEverything__LambdaImpl0());
  }
  
  protected void updateSettingsAnimator()
  {
    boolean bool = isLayoutRtl();
    Object localObject = new TouchAnimator.Builder().addFloat(this.mSettingsContainer, "translationY", new float[] { -this.mGearTranslation, 0.0F });
    MultiUserSwitch localMultiUserSwitch = this.mMultiUserSwitch;
    if (bool) {}
    for (float f = this.mGearTranslation;; f = -this.mGearTranslation)
    {
      this.mSettingsAlpha = ((TouchAnimator.Builder)localObject).addFloat(localMultiUserSwitch, "translationX", new float[] { f, 0.0F }).addFloat(this.mSettingsButton, "rotation", new float[] { -90.0F, 0.0F }).addFloat(this.mSettingsContainer, "alpha", new float[] { 0.0F, 1.0F }).addFloat(this.mMultiUserSwitch, "alpha", new float[] { 0.0F, 1.0F }).addFloat(this.mEditButton, "alpha", new float[] { 0.0F, 1.0F }).setStartDelay(0.7F).build();
      if ((!bool) || (this.mDateTimeGroup.getWidth() != 0)) {
        break;
      }
      this.mDateTimeGroup.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
      {
        public void onLayoutChange(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6, int paramAnonymousInt7, int paramAnonymousInt8)
        {
          QuickStatusBarHeader.-get0(QuickStatusBarHeader.this).setPivotX(QuickStatusBarHeader.this.getWidth());
          QuickStatusBarHeader.-get0(QuickStatusBarHeader.this).removeOnLayoutChangeListener(this);
        }
      });
      return;
    }
    localObject = this.mDateTimeGroup;
    if (bool) {}
    for (int i = this.mDateTimeGroup.getWidth();; i = 0)
    {
      ((ViewGroup)localObject).setPivotX(i);
      return;
    }
  }
  
  protected void updateThemeColor()
  {
    int i = ThemeColorUtils.getColor(ThemeColorUtils.QS_SYSTEM_PRIMARY);
    int j = ThemeColorUtils.getColor(ThemeColorUtils.QS_ICON_ACTIVE);
    int k = ThemeColorUtils.getColor(ThemeColorUtils.QS_TILE_LABEL_LIGHT);
    int m = ThemeColorUtils.getColor(ThemeColorUtils.QS_TILE_LABEL);
    setBackgroundTintList(ColorStateList.valueOf(i));
    this.mDateView.setTextColor(m);
    this.mDateView.setCompoundDrawableTintList(ColorStateList.valueOf(m));
    ((TextView)findViewById(2131952259)).setTextColor(m);
    ((TextView)findViewById(2131952260)).setTextColor(m);
    this.mCarrierText.setTextColor(k);
    this.mEditButton.setImageTintList(ColorStateList.valueOf(j));
    this.mSettingsButton.setImageTintList(ColorStateList.valueOf(j));
    this.mAlarmStatus.setCompoundDrawableTintList(ColorStateList.valueOf(k));
    this.mAlarmStatus.setTextColor(k);
    this.mAlarmStatusCollapsed.setImageTintList(ColorStateList.valueOf(k));
    ((ImageView)findViewById(2131952171)).setImageTintList(ColorStateList.valueOf(k));
    if (this.mSmallAlarm != null) {
      this.mSmallAlarm.setImageTintList(ColorStateList.valueOf(k));
    }
  }
  
  protected void updateVisibilities()
  {
    int j = 0;
    updateAlarmVisibilities();
    updateDateTimePosition();
    Object localObject = this.mCarrierText;
    label49:
    label71:
    label92:
    label122:
    label149:
    boolean bool;
    if (this.mExpanded)
    {
      i = -1;
      ((CarrierText)localObject).setMarqueeRepeatLimit(i);
      CarrierText localCarrierText = this.mCarrierText;
      if (!this.mExpanded) {
        break label236;
      }
      localObject = TextUtils.TruncateAt.MARQUEE;
      localCarrierText.setEllipsize((TextUtils.TruncateAt)localObject);
      localObject = this.mCarrierText;
      if (!this.mExpanded) {
        break label244;
      }
      i = 0;
      ((CarrierText)localObject).setVisibility(i);
      localObject = this.mSettingsContainer;
      if (!this.mExpanded) {
        break label249;
      }
      i = 0;
      ((View)localObject).setVisibility(i);
      localObject = this.mSettingsContainer.findViewById(2131952171);
      if (!TunerService.isTunerEnabled(this.mContext)) {
        break label254;
      }
      i = 0;
      ((View)localObject).setVisibility(i);
      localObject = findViewById(2131952172);
      if (!TunerService.isTunerEnabled(this.mContext)) {
        break label259;
      }
      i = 4;
      ((View)localObject).setVisibility(i);
      bool = UserManager.isDeviceInDemoMode(this.mContext);
      localObject = this.mMultiUserSwitch;
      if ((this.mExpanded) && (this.mMultiUserSwitch.hasMultipleUsers()) && (!bool)) {
        break label265;
      }
    }
    label236:
    label244:
    label249:
    label254:
    label259:
    label265:
    for (int i = 4;; i = 0)
    {
      ((MultiUserSwitch)localObject).setVisibility(i);
      if ((this.mHost.getPhoneStatusBar().getBarState() == 0) && (!Utils.isCurrentGuest(getContext()))) {
        break label270;
      }
      this.mEditButton.setVisibility(8);
      return;
      i = 0;
      break;
      localObject = TextUtils.TruncateAt.END;
      break label49;
      i = 4;
      break label71;
      i = 4;
      break label92;
      i = 4;
      break label122;
      i = 8;
      break label149;
    }
    label270:
    localObject = this.mEditButton;
    if ((!bool) && (this.mExpanded)) {}
    for (i = j;; i = 4)
    {
      ((ImageButton)localObject).setVisibility(i);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\QuickStatusBarHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */