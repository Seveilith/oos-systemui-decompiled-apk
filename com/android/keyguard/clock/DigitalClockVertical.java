package com.android.keyguard.clock;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings.System;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewHierarchyEncoder;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.R.dimen;
import com.android.keyguard.R.id;
import java.util.Calendar;
import java.util.TimeZone;
import libcore.icu.LocaleData;

public class DigitalClockVertical
  extends KeyguardClockWidgetView
{
  public static final CharSequence DEFAULT_FORMAT_12_HOUR = "hh";
  public static final CharSequence DEFAULT_FORMAT_24_HOUR = "HH";
  public static final CharSequence DEFAULT_FORMAT_MIN = "mm";
  private boolean mAttached;
  private CharSequence mDescFormat;
  private CharSequence mDescFormat12;
  private CharSequence mDescFormat24;
  @ViewDebug.ExportedProperty
  private CharSequence mFormat;
  private CharSequence mFormat12;
  private CharSequence mFormat24;
  private final ContentObserver mFormatChangeObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      DigitalClockVertical.-wrap0(DigitalClockVertical.this);
      DigitalClockVertical.-wrap2(DigitalClockVertical.this);
    }
    
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      DigitalClockVertical.-wrap0(DigitalClockVertical.this);
      DigitalClockVertical.-wrap2(DigitalClockVertical.this);
    }
  };
  @ViewDebug.ExportedProperty
  private boolean mHasSeconds;
  private TextView mHourView;
  private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ((DigitalClockVertical.-get1(DigitalClockVertical.this) == null) && ("android.intent.action.TIMEZONE_CHANGED".equals(paramAnonymousIntent.getAction())))
      {
        paramAnonymousContext = paramAnonymousIntent.getStringExtra("time-zone");
        DigitalClockVertical.-wrap1(DigitalClockVertical.this, paramAnonymousContext);
      }
      DigitalClockVertical.-wrap2(DigitalClockVertical.this);
    }
  };
  private KeyguardUpdateMonitor mKeyguardUpdateMonitor;
  private TextView mMinView;
  private boolean mShowCurrentUserTime;
  private final Runnable mTicker = new Runnable()
  {
    public void run()
    {
      DigitalClockVertical.-wrap2(DigitalClockVertical.this);
      long l = SystemClock.uptimeMillis();
      DigitalClockVertical.this.getHandler().postAtTime(DigitalClockVertical.-get0(DigitalClockVertical.this), l + (1000L - l % 1000L));
    }
  };
  private Calendar mTime;
  private String mTimeZone;
  
  public DigitalClockVertical(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public DigitalClockVertical(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  public DigitalClockVertical(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init();
  }
  
  private static CharSequence abc(CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3)
  {
    if (paramCharSequence1 == null)
    {
      if (paramCharSequence2 == null) {
        return paramCharSequence3;
      }
      return paramCharSequence2;
    }
    return paramCharSequence1;
  }
  
  private void checkViewState()
  {
    if (this.mHourView == null) {
      this.mHourView = ((TextView)findViewById(R.id.text_hour));
    }
    if (this.mMinView == null) {
      this.mMinView = ((TextView)findViewById(R.id.text_minute));
    }
  }
  
  private void chooseFormat()
  {
    chooseFormat(true);
  }
  
  private void chooseFormat(boolean paramBoolean)
  {
    boolean bool = is24HourModeEnabled();
    LocaleData localLocaleData = LocaleData.get(getContext().getResources().getConfiguration().locale);
    if (bool) {
      this.mFormat = abc(this.mFormat24, this.mFormat12, localLocaleData.timeFormat_Hm);
    }
    for (this.mDescFormat = abc(this.mDescFormat24, this.mDescFormat12, this.mFormat);; this.mDescFormat = abc(this.mDescFormat12, this.mDescFormat24, this.mFormat))
    {
      bool = this.mHasSeconds;
      this.mHasSeconds = DateFormat.hasSeconds(this.mFormat);
      if ((paramBoolean) && (this.mAttached) && (bool != this.mHasSeconds))
      {
        if (!bool) {
          break;
        }
        getHandler().removeCallbacks(this.mTicker);
      }
      return;
      this.mFormat = abc(this.mFormat12, this.mFormat24, localLocaleData.timeFormat_hm);
    }
    this.mTicker.run();
  }
  
  private void createTime(String paramString)
  {
    if (paramString != null)
    {
      this.mTime = Calendar.getInstance(TimeZone.getTimeZone(paramString));
      return;
    }
    this.mTime = Calendar.getInstance();
  }
  
  private int getFixedDimension(Resources paramResources, float paramFloat, int paramInt)
  {
    return (int)(paramResources.getDimension(paramInt) / paramFloat) * 3;
  }
  
  private void init()
  {
    Log.d("KeyguardClockWidget2", "init");
    this.mHourView = ((TextView)findViewById(R.id.text_hour));
    this.mMinView = ((TextView)findViewById(R.id.text_minute));
    if ((this.mFormat12 == null) || (this.mFormat24 == null))
    {
      LocaleData localLocaleData = LocaleData.get(getContext().getResources().getConfiguration().locale);
      if (this.mFormat12 == null) {
        this.mFormat12 = localLocaleData.timeFormat_hm;
      }
      if (this.mFormat24 == null) {
        this.mFormat24 = localLocaleData.timeFormat_Hm;
      }
    }
    createTime(this.mTimeZone);
    chooseFormat(false);
    this.mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(getContext());
  }
  
  private void onTimeChanged()
  {
    Log.d("KeyguardClockWidget2", "onTimeChanged");
    this.mTime.setTimeInMillis(System.currentTimeMillis());
    setText();
    setContentDescription(DateFormat.format(this.mDescFormat, this.mTime));
  }
  
  private void registerObserver()
  {
    ContentResolver localContentResolver = getContext().getContentResolver();
    if (this.mShowCurrentUserTime)
    {
      localContentResolver.registerContentObserver(Settings.System.CONTENT_URI, true, this.mFormatChangeObserver, -1);
      return;
    }
    localContentResolver.registerContentObserver(Settings.System.CONTENT_URI, true, this.mFormatChangeObserver);
  }
  
  private void registerReceiver()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.TIME_SET");
    localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
    getContext().registerReceiver(this.mIntentReceiver, localIntentFilter, null, getHandler());
  }
  
  private void setText()
  {
    checkViewState();
    TextView localTextView;
    if (this.mHourView != null)
    {
      localTextView = this.mHourView;
      if (this.mFormat != this.mFormat12) {
        break label68;
      }
    }
    label68:
    for (CharSequence localCharSequence = DEFAULT_FORMAT_12_HOUR;; localCharSequence = DEFAULT_FORMAT_24_HOUR)
    {
      localTextView.setText(DateFormat.format(localCharSequence, this.mTime));
      if (this.mMinView != null) {
        this.mMinView.setText(DateFormat.format(DEFAULT_FORMAT_MIN, this.mTime));
      }
      return;
    }
  }
  
  private void unregisterObserver()
  {
    getContext().getContentResolver().unregisterContentObserver(this.mFormatChangeObserver);
  }
  
  private void unregisterReceiver()
  {
    getContext().unregisterReceiver(this.mIntentReceiver);
  }
  
  protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
  {
    Object localObject2 = null;
    super.encodeProperties(paramViewHierarchyEncoder);
    Object localObject1 = getFormat12Hour();
    if (localObject1 == null)
    {
      localObject1 = null;
      paramViewHierarchyEncoder.addProperty("format12Hour", (String)localObject1);
      localObject1 = getFormat24Hour();
      if (localObject1 != null) {
        break label84;
      }
      localObject1 = null;
      label37:
      paramViewHierarchyEncoder.addProperty("format24Hour", (String)localObject1);
      if (this.mFormat != null) {
        break label94;
      }
    }
    label84:
    label94:
    for (localObject1 = localObject2;; localObject1 = this.mFormat.toString())
    {
      paramViewHierarchyEncoder.addProperty("format", (String)localObject1);
      paramViewHierarchyEncoder.addProperty("hasSeconds", this.mHasSeconds);
      return;
      localObject1 = ((CharSequence)localObject1).toString();
      break;
      localObject1 = ((CharSequence)localObject1).toString();
      break label37;
    }
  }
  
  @ViewDebug.ExportedProperty
  public CharSequence getFormat12Hour()
  {
    return this.mFormat12;
  }
  
  @ViewDebug.ExportedProperty
  public CharSequence getFormat24Hour()
  {
    return this.mFormat24;
  }
  
  public boolean is24HourModeEnabled()
  {
    if (this.mShowCurrentUserTime)
    {
      Context localContext = getContext();
      KeyguardUpdateMonitor.getInstance(getContext());
      return DateFormat.is24HourFormat(localContext, KeyguardUpdateMonitor.getCurrentUser());
    }
    return DateFormat.is24HourFormat(getContext());
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (!this.mAttached)
    {
      this.mAttached = true;
      registerReceiver();
      registerObserver();
      createTime(this.mTimeZone);
      if (!this.mHasSeconds) {
        break label203;
      }
      this.mTicker.run();
    }
    for (;;)
    {
      Resources localResources = this.mContext.getResources();
      float f = localResources.getDisplayMetrics().density;
      setPaddingRelative(getFixedDimension(localResources, f, R.dimen.clock_widget_2_maringLeft), 0, 0, 0);
      ((LinearLayout.LayoutParams)this.mMinView.getLayoutParams()).bottomMargin = getFixedDimension(localResources, f, R.dimen.clock_widget_2_text_min_margin_bottom);
      LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)findViewById(R.id.widget_divider).getLayoutParams();
      localLayoutParams.width = getFixedDimension(localResources, f, R.dimen.clock_widget_2_divider_width);
      localLayoutParams.height = getFixedDimension(localResources, f, R.dimen.clock_widget_2_divider_height);
      localLayoutParams.bottomMargin = getFixedDimension(localResources, f, R.dimen.clock_widget_2_divider_margin_bottom);
      ((LinearLayout.LayoutParams)this.mHourView.getLayoutParams()).bottomMargin = getFixedDimension(localResources, f, R.dimen.clock_widget_2_text_hour_margin_bottom);
      ((LinearLayout.LayoutParams)findViewById(R.id.owner_info).getLayoutParams()).topMargin = getFixedDimension(localResources, f, R.dimen.op_date_owner_info_margin);
      return;
      label203:
      onTimeChanged();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.mAttached)
    {
      unregisterReceiver();
      unregisterObserver();
      getHandler().removeCallbacks(this.mTicker);
      this.mAttached = false;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\clock\DigitalClockVertical.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */