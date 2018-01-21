package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.icu.text.DateTimePatternGenerator;
import android.icu.text.DisplayContext;
import android.icu.text.SimpleDateFormat;
import android.os.LocaleList;
import android.provider.Settings.System;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.R.styleable;
import java.util.Date;
import java.util.Locale;

public class DateView
  extends TextView
{
  private KeyguardUpdateMonitorCallback mCallback = new KeyguardUpdateMonitorCallback()
  {
    public void onTimeChanged()
    {
      DateView.this.updateClock();
      Log.i("DateView", "onTimeChanged");
    }
  };
  private final Date mCurrentTime = new Date();
  private android.icu.text.DateFormat mDateFormat;
  private String mDatePattern;
  private BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if (("android.intent.action.TIMEZONE_CHANGED".equals(paramAnonymousContext)) || ("android.intent.action.LOCALE_CHANGED".equals(paramAnonymousContext)))
      {
        if (("android.intent.action.LOCALE_CHANGED".equals(paramAnonymousContext)) || ("android.intent.action.TIMEZONE_CHANGED".equals(paramAnonymousContext))) {
          DateView.-set0(DateView.this, null);
        }
        DateView.this.updateClock();
      }
    }
  };
  private String mLastText;
  private LocaleList mLocaleList;
  private boolean mOverrideDateFormat;
  private String mOverrideDateString;
  private String mToBeOverrideDateString;
  
  public DateView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramAttributeSet = paramContext.getTheme().obtainStyledAttributes(paramAttributeSet, R.styleable.DateView, 0, 0);
    try
    {
      this.mDatePattern = paramAttributeSet.getString(0);
      paramAttributeSet.recycle();
      if (this.mDatePattern == null) {
        this.mDatePattern = getContext().getString(2131689920);
      }
      this.mOverrideDateFormat = getContext().getResources().getBoolean(2131558452);
      this.mOverrideDateString = getContext().getString(2131689927);
      this.mToBeOverrideDateString = getContext().getString(2131689926);
      this.mLocaleList = paramContext.getResources().getConfiguration().getLocales();
      return;
    }
    finally
    {
      paramAttributeSet.recycle();
    }
  }
  
  private String getDateFormat()
  {
    if (getContext().getResources().getBoolean(17957084)) {
      return android.text.format.DateFormat.format(Settings.System.getString(getContext().getContentResolver(), "date_format"), this.mCurrentTime).toString();
    }
    return this.mDateFormat.format(this.mCurrentTime);
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
    localIntentFilter.addAction("android.intent.action.LOCALE_CHANGED");
    getContext().registerReceiver(this.mIntentReceiver, localIntentFilter, null, null);
    KeyguardUpdateMonitor.getInstance(getContext()).registerCallback(this.mCallback);
    updateClock();
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    paramConfiguration = paramConfiguration.getLocales();
    if (paramConfiguration != this.mLocaleList)
    {
      this.mLocaleList = paramConfiguration;
      this.mOverrideDateFormat = getContext().getResources().getBoolean(2131558452);
      this.mOverrideDateString = getContext().getString(2131689927);
      this.mToBeOverrideDateString = getContext().getString(2131689926);
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.mDateFormat = null;
    getContext().unregisterReceiver(this.mIntentReceiver);
    KeyguardUpdateMonitor.getInstance(getContext()).removeCallback(this.mCallback);
  }
  
  protected void updateClock()
  {
    Object localObject;
    if (this.mDateFormat == null)
    {
      localObject = Locale.getDefault();
      if (this.mOverrideDateFormat) {
        break label76;
      }
      localObject = android.icu.text.DateFormat.getInstanceForSkeleton(this.mDatePattern, (Locale)localObject);
    }
    for (;;)
    {
      ((android.icu.text.DateFormat)localObject).setContext(DisplayContext.CAPITALIZATION_FOR_STANDALONE);
      this.mDateFormat = ((android.icu.text.DateFormat)localObject);
      this.mCurrentTime.setTime(System.currentTimeMillis());
      localObject = getDateFormat();
      if (!((String)localObject).equals(this.mLastText))
      {
        setText((CharSequence)localObject);
        this.mLastText = ((String)localObject);
      }
      return;
      label76:
      String str = DateTimePatternGenerator.getInstance((Locale)localObject).getBestPattern(this.mDatePattern);
      if (this.mToBeOverrideDateString.equals(str)) {
        localObject = new SimpleDateFormat(this.mOverrideDateString, (Locale)localObject);
      } else {
        localObject = android.icu.text.DateFormat.getInstanceForSkeleton(this.mDatePattern, (Locale)localObject);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\DateView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */