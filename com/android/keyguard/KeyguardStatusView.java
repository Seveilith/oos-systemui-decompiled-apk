package com.android.keyguard;

import android.app.AlarmManager;
import android.app.AlarmManager.AlarmClockInfo;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.GridLayout;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.clock.OPTextClock;
import java.util.Locale;

public class KeyguardStatusView
  extends GridLayout
{
  private final String SETTGINS_CLOCK_KEY = "keyguard_clock_widget";
  private final AlarmManager mAlarmManager;
  private TextView mAlarmStatusView;
  private OPTextClock mClockView;
  private Context mContext;
  private OPTextClock mDateView;
  private KeyguardUpdateMonitorCallback mInfoCallback = new KeyguardUpdateMonitorCallback()
  {
    public void onFinishedGoingToSleep(int paramAnonymousInt)
    {
      KeyguardStatusView.-wrap1(KeyguardStatusView.this, false);
    }
    
    public void onKeyguardVisibilityChanged(boolean paramAnonymousBoolean)
    {
      if (paramAnonymousBoolean)
      {
        KeyguardStatusView.-wrap0(KeyguardStatusView.this);
        KeyguardStatusView.-wrap2(KeyguardStatusView.this);
      }
    }
    
    public void onStartedWakingUp()
    {
      KeyguardStatusView.-wrap1(KeyguardStatusView.this, true);
    }
    
    public void onTimeChanged()
    {
      KeyguardStatusView.-wrap0(KeyguardStatusView.this);
    }
    
    public void onUserSwitchComplete(int paramAnonymousInt)
    {
      KeyguardStatusView.-wrap0(KeyguardStatusView.this);
      KeyguardStatusView.-wrap2(KeyguardStatusView.this);
    }
  };
  private final LockPatternUtils mLockPatternUtils;
  private TextView mOwnerInfo;
  
  public KeyguardStatusView(Context paramContext)
  {
    this(paramContext, null, 0);
  }
  
  public KeyguardStatusView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public KeyguardStatusView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mAlarmManager = ((AlarmManager)paramContext.getSystemService("alarm"));
    this.mLockPatternUtils = new LockPatternUtils(getContext());
    this.mContext = paramContext;
  }
  
  public static String formatNextAlarm(Context paramContext, AlarmManager.AlarmClockInfo paramAlarmClockInfo)
  {
    if (paramAlarmClockInfo == null) {
      return "";
    }
    if (DateFormat.is24HourFormat(paramContext, KeyguardUpdateMonitor.getCurrentUser())) {}
    for (paramContext = "EHm";; paramContext = "Ehma") {
      return DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), paramContext), paramAlarmClockInfo.getTriggerTime()).toString();
    }
  }
  
  private String getOwnerInfo()
  {
    String str = null;
    if (this.mLockPatternUtils.isDeviceOwnerInfoEnabled()) {
      str = this.mLockPatternUtils.getDeviceOwnerInfo();
    }
    while (!this.mLockPatternUtils.isOwnerInfoEnabled(KeyguardUpdateMonitor.getCurrentUser())) {
      return str;
    }
    return this.mLockPatternUtils.getOwnerInfo(KeyguardUpdateMonitor.getCurrentUser());
  }
  
  private void refresh()
  {
    Patterns.update(this.mContext, false);
    refreshTime();
  }
  
  private void setEnableMarquee(boolean paramBoolean)
  {
    if (this.mAlarmStatusView != null) {
      this.mAlarmStatusView.setSelected(paramBoolean);
    }
    if (this.mOwnerInfo != null) {
      this.mOwnerInfo.setSelected(paramBoolean);
    }
  }
  
  private void updateOwnerInfo()
  {
    if (this.mOwnerInfo == null) {
      return;
    }
    String str = getOwnerInfo();
    if (!TextUtils.isEmpty(str))
    {
      this.mOwnerInfo.setVisibility(0);
      this.mOwnerInfo.setText(str);
      return;
    }
    this.mOwnerInfo.setVisibility(8);
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    KeyguardUpdateMonitor.getInstance(this.mContext).registerCallback(this.mInfoCallback);
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    this.mClockView.setTextSize(0, getResources().getDimensionPixelSize(R.dimen.widget_big_font_size));
    paramConfiguration = (ViewGroup.MarginLayoutParams)this.mClockView.getLayoutParams();
    paramConfiguration.bottomMargin = getResources().getDimensionPixelSize(R.dimen.bottom_text_spacing_digital);
    this.mClockView.setLayoutParams(paramConfiguration);
    this.mDateView.setTextSize(0, getResources().getDimensionPixelSize(R.dimen.widget_label_font_size));
    if (this.mOwnerInfo != null) {
      this.mOwnerInfo.setTextSize(0, getResources().getDimensionPixelSize(R.dimen.widget_label_font_size));
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    KeyguardUpdateMonitor.getInstance(this.mContext).removeCallback(this.mInfoCallback);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mDateView = ((OPTextClock)findViewById(R.id.date_view));
    this.mClockView = ((OPTextClock)findViewById(R.id.clock_view));
    this.mDateView.setShowCurrentUserTime(true);
    this.mClockView.setShowCurrentUserTime(true);
    this.mOwnerInfo = ((TextView)findViewById(R.id.owner_info));
    setEnableMarquee(KeyguardUpdateMonitor.getInstance(this.mContext).isDeviceInteractive());
    refresh();
    updateOwnerInfo();
    this.mClockView.setElegantTextHeight(false);
  }
  
  public void refreshTime()
  {
    this.mDateView.setFormat24Hour(Patterns.dateView);
    this.mDateView.setFormat12Hour(Patterns.dateView);
    this.mClockView.setFormat12Hour(Patterns.clockView12);
    this.mClockView.setFormat24Hour(Patterns.clockView24);
  }
  
  public static final class Patterns
  {
    public static String cacheKey;
    public static String clockView12;
    public static String clockView24;
    public static String dateView;
    
    public static void update(Context paramContext, boolean paramBoolean)
    {
      Locale localLocale = Locale.getDefault();
      Resources localResources = paramContext.getResources();
      if (paramBoolean) {}
      String str1;
      String str2;
      String str3;
      String str4;
      for (int i = R.string.abbrev_wday_month_day_no_year_alarm;; i = R.string.abbrev_wday_month_day_no_year)
      {
        str1 = localResources.getString(i);
        str2 = localResources.getString(R.string.clock_12hr_format);
        str3 = localResources.getString(R.string.clock_24hr_format);
        str4 = localLocale.toString() + str1 + str2 + str3;
        if (!str4.equals(cacheKey)) {
          break;
        }
        return;
      }
      if (localResources.getBoolean(17957084)) {}
      for (dateView = Settings.System.getString(paramContext.getContentResolver(), "date_format");; dateView = DateFormat.getBestDateTimePattern(localLocale, str1))
      {
        clockView12 = DateFormat.getBestDateTimePattern(localLocale, str2);
        if ((!paramContext.getResources().getBoolean(R.bool.config_showAmpm)) && (!str2.contains("a"))) {
          clockView12 = clockView12.replaceAll("a", "").trim();
        }
        clockView24 = DateFormat.getBestDateTimePattern(localLocale, str3);
        clockView24 = clockView24.replace(':', 60929);
        clockView12 = clockView12.replace(':', 60929);
        cacheKey = str4;
        return;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\KeyguardStatusView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */