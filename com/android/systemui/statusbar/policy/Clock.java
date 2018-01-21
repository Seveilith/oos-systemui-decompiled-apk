package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.SpannableStringBuilder;
import android.text.format.DateFormat;
import android.text.style.RelativeSizeSpan;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.widget.TextView;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.DemoMode;
import com.android.systemui.R.styleable;
import com.android.systemui.plugin.LSState;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import libcore.icu.LocaleData;

public class Clock
  extends TextView
  implements DemoMode, TunerService.Tunable
{
  private static final String TAG = Clock.class.getSimpleName();
  private final int mAmPmStyle;
  private boolean mAttached;
  private Calendar mCalendar;
  private KeyguardUpdateMonitorCallback mCallback = new KeyguardUpdateMonitorCallback()
  {
    public void onTimeChanged()
    {
      if (Clock.-get1(Clock.this) != null) {
        Clock.this.updateClock();
      }
      Log.i(Clock.-get0(), "onTimeChanged");
    }
  };
  private SimpleDateFormat mClockFormat;
  private String mClockFormatString;
  private SimpleDateFormat mContentDescriptionFormat;
  private boolean mDemoMode;
  private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if (paramAnonymousContext.equals("android.intent.action.TIMEZONE_CHANGED"))
      {
        paramAnonymousContext = paramAnonymousIntent.getStringExtra("time-zone");
        Clock.-set0(Clock.this, Calendar.getInstance(TimeZone.getTimeZone(paramAnonymousContext)));
        if (Clock.-get2(Clock.this) != null) {
          Clock.-get2(Clock.this).setTimeZone(Clock.-get1(Clock.this).getTimeZone());
        }
      }
      for (;;)
      {
        Clock.this.updateClock();
        return;
        if (paramAnonymousContext.equals("android.intent.action.CONFIGURATION_CHANGED"))
        {
          paramAnonymousContext = Clock.this.getResources().getConfiguration().locale;
          if (!paramAnonymousContext.equals(Clock.-get3(Clock.this)))
          {
            Clock.-set2(Clock.this, paramAnonymousContext);
            Clock.-set1(Clock.this, "");
          }
        }
      }
    }
  };
  private Locale mLocale;
  private final BroadcastReceiver mScreenReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if ("android.intent.action.SCREEN_OFF".equals(paramAnonymousContext)) {
        if (Clock.-get5(Clock.this) != null) {
          Clock.-get5(Clock.this).removeCallbacks(Clock.-get4(Clock.this));
        }
      }
      while ((!"android.intent.action.SCREEN_ON".equals(paramAnonymousContext)) || (Clock.-get5(Clock.this) == null)) {
        return;
      }
      Clock.-get5(Clock.this).postAtTime(Clock.-get4(Clock.this), SystemClock.uptimeMillis() / 1000L * 1000L + 1000L);
    }
  };
  private final Runnable mSecondTick = new Runnable()
  {
    public void run()
    {
      if (Clock.-get1(Clock.this) != null) {
        Clock.this.updateClock();
      }
      Clock.-get5(Clock.this).postAtTime(this, SystemClock.uptimeMillis() / 1000L * 1000L + 1000L);
    }
  };
  private Handler mSecondsHandler;
  private boolean mShowSeconds;
  
  public Clock(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public Clock(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public Clock(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    paramContext = paramContext.getTheme().obtainStyledAttributes(paramAttributeSet, R.styleable.Clock, 0, 0);
    try
    {
      this.mAmPmStyle = paramContext.getInt(0, 2);
      return;
    }
    finally
    {
      paramContext.recycle();
    }
  }
  
  private final CharSequence getSmallTime()
  {
    Object localObject1 = getContext();
    KeyguardUpdateMonitor.getInstance(this.mContext);
    boolean bool = DateFormat.is24HourFormat((Context)localObject1, KeyguardUpdateMonitor.getCurrentUser());
    localObject1 = LocaleData.get(((Context)localObject1).getResources().getConfiguration().locale);
    int m;
    int k;
    int i;
    if (showSeconds()) {
      if (bool)
      {
        localObject1 = ((LocaleData)localObject1).timeFormat_Hms;
        if (((String)localObject1).equals(this.mClockFormatString)) {
          break label393;
        }
        this.mContentDescriptionFormat = new SimpleDateFormat((String)localObject1);
        localObject2 = localObject1;
        if (this.mAmPmStyle == 0) {
          break label292;
        }
        m = -1;
        k = 0;
        i = 0;
      }
    }
    int j;
    for (;;)
    {
      j = m;
      int n;
      if (i < ((String)localObject1).length())
      {
        n = ((String)localObject1).charAt(i);
        j = k;
        if (n == 39) {
          if (k == 0) {
            break label220;
          }
        }
      }
      label220:
      for (j = 0;; j = 1)
      {
        if ((j != 0) || (n != 97)) {
          break label225;
        }
        j = i;
        localObject2 = localObject1;
        if (j < 0) {
          break label292;
        }
        i = j;
        while ((i > 0) && (Character.isWhitespace(((String)localObject1).charAt(i - 1)))) {
          i -= 1;
        }
        localObject1 = ((LocaleData)localObject1).timeFormat_hms;
        break;
        if (bool)
        {
          localObject1 = ((LocaleData)localObject1).timeFormat_Hm;
          break;
        }
        localObject1 = ((LocaleData)localObject1).timeFormat_hm;
        break;
      }
      label225:
      i += 1;
      k = j;
    }
    Object localObject2 = ((String)localObject1).substring(0, i) + 61184 + ((String)localObject1).substring(i, j) + "a" + 61185 + ((String)localObject1).substring(j + 1);
    label292:
    localObject1 = new SimpleDateFormat((String)localObject2);
    this.mClockFormat = ((SimpleDateFormat)localObject1);
    this.mClockFormatString = ((String)localObject2);
    for (;;)
    {
      localObject1 = ((SimpleDateFormat)localObject1).format(this.mCalendar.getTime());
      if (this.mAmPmStyle == 0) {
        break label452;
      }
      i = ((String)localObject1).indexOf(61184);
      j = ((String)localObject1).indexOf(61185);
      if ((i < 0) || (j <= i)) {
        break label452;
      }
      localObject1 = new SpannableStringBuilder((CharSequence)localObject1);
      if (this.mAmPmStyle != 2) {
        break;
      }
      ((SpannableStringBuilder)localObject1).delete(i, j + 1);
      return (CharSequence)localObject1;
      label393:
      localObject1 = this.mClockFormat;
    }
    if (this.mAmPmStyle == 1) {
      ((SpannableStringBuilder)localObject1).setSpan(new RelativeSizeSpan(0.7F), i, j, 34);
    }
    ((SpannableStringBuilder)localObject1).delete(j, j + 1);
    ((SpannableStringBuilder)localObject1).delete(i, i + 1);
    return (CharSequence)localObject1;
    label452:
    return (CharSequence)localObject1;
  }
  
  private boolean showSeconds()
  {
    return (this.mShowSeconds) && (!LSState.getInstance().isHighHintShow());
  }
  
  public void dispatchDemoCommand(String paramString, Bundle paramBundle)
  {
    if ((!this.mDemoMode) && (paramString.equals("enter"))) {
      this.mDemoMode = true;
    }
    do
    {
      return;
      if ((this.mDemoMode) && (paramString.equals("exit")))
      {
        this.mDemoMode = false;
        updateClock();
        return;
      }
    } while ((!this.mDemoMode) || (!paramString.equals("clock")));
    paramString = paramBundle.getString("millis");
    paramBundle = paramBundle.getString("hhmm");
    if (paramString != null) {
      this.mCalendar.setTimeInMillis(Long.parseLong(paramString));
    }
    while ((paramBundle == null) || (paramBundle.length() != 4))
    {
      setText(getSmallTime());
      setContentDescription(this.mContentDescriptionFormat.format(this.mCalendar.getTime()));
      return;
    }
    int i = Integer.parseInt(paramBundle.substring(0, 2));
    int j = Integer.parseInt(paramBundle.substring(2));
    if (DateFormat.is24HourFormat(getContext(), ActivityManager.getCurrentUser())) {
      this.mCalendar.set(11, i);
    }
    for (;;)
    {
      this.mCalendar.set(12, j);
      break;
      this.mCalendar.set(10, i);
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (!this.mAttached)
    {
      this.mAttached = true;
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
      localIntentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
      localIntentFilter.addAction("android.intent.action.USER_SWITCHED");
      getContext().registerReceiverAsUser(this.mIntentReceiver, UserHandle.ALL, localIntentFilter, null, getHandler());
      TunerService.get(getContext()).addTunable(this, new String[] { "clock_seconds", "icon_blacklist" });
    }
    this.mCalendar = Calendar.getInstance(TimeZone.getDefault());
    updateClock();
    updateShowSeconds();
    KeyguardUpdateMonitor.getInstance(getContext()).registerCallback(this.mCallback);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.mAttached)
    {
      getContext().unregisterReceiver(this.mIntentReceiver);
      this.mAttached = false;
      TunerService.get(getContext()).removeTunable(this);
      KeyguardUpdateMonitor.getInstance(getContext()).removeCallback(this.mCallback);
    }
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    int i = 0;
    boolean bool2 = false;
    if ("clock_seconds".equals(paramString1))
    {
      bool1 = bool2;
      if (paramString2 != null)
      {
        bool1 = bool2;
        if (Integer.parseInt(paramString2) != 0) {
          bool1 = true;
        }
      }
      this.mShowSeconds = bool1;
      updateShowSeconds();
    }
    while (!"icon_blacklist".equals(paramString1))
    {
      boolean bool1;
      return;
    }
    if (StatusBarIconController.getIconBlacklist(paramString2).contains("clock")) {
      i = 8;
    }
    setVisibility(i);
  }
  
  final void updateClock()
  {
    if (this.mDemoMode) {
      return;
    }
    this.mCalendar.setTimeInMillis(System.currentTimeMillis());
    setText(getSmallTime());
    setContentDescription(this.mContentDescriptionFormat.format(this.mCalendar.getTime()));
  }
  
  public void updateShowSeconds()
  {
    if (showSeconds()) {
      if ((this.mSecondsHandler == null) && (getDisplay() != null))
      {
        this.mSecondsHandler = new Handler();
        if (getDisplay().getState() == 2) {
          this.mSecondsHandler.postAtTime(this.mSecondTick, SystemClock.uptimeMillis() / 1000L * 1000L + 1000L);
        }
        localIntentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
        localIntentFilter.addAction("android.intent.action.SCREEN_ON");
        this.mContext.registerReceiver(this.mScreenReceiver, localIntentFilter);
      }
    }
    while (this.mSecondsHandler == null)
    {
      IntentFilter localIntentFilter;
      return;
    }
    this.mContext.unregisterReceiver(this.mScreenReceiver);
    this.mSecondsHandler.removeCallbacks(this.mSecondTick);
    this.mSecondsHandler = null;
    updateClock();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\Clock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */