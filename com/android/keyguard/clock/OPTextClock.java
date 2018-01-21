package com.android.keyguard.clock;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Process;
import android.os.SystemClock;
import android.provider.Settings.System;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewHierarchyEncoder;
import android.widget.RemoteViews.RemoteView;
import android.widget.TextView;
import com.android.internal.R.styleable;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import java.util.Calendar;
import java.util.TimeZone;
import libcore.icu.LocaleData;

@RemoteViews.RemoteView
public class OPTextClock
  extends TextView
{
  public static final CharSequence DEFAULT_FORMAT_12_HOUR = "h:mm a";
  public static final CharSequence DEFAULT_FORMAT_24_HOUR = "H:mm";
  private boolean mAttached;
  private CharSequence mDescFormat;
  private CharSequence mDescFormat12;
  private CharSequence mDescFormat24;
  @ViewDebug.ExportedProperty
  private CharSequence mFormat;
  private CharSequence mFormat12;
  private CharSequence mFormat24;
  private ContentObserver mFormatChangeObserver;
  @ViewDebug.ExportedProperty
  private boolean mHasSeconds;
  private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ((OPTextClock.-get1(OPTextClock.this) == null) && ("android.intent.action.TIMEZONE_CHANGED".equals(paramAnonymousIntent.getAction())))
      {
        paramAnonymousContext = paramAnonymousIntent.getStringExtra("time-zone");
        OPTextClock.-wrap1(OPTextClock.this, paramAnonymousContext);
      }
      OPTextClock.-wrap2(OPTextClock.this);
    }
  };
  private KeyguardUpdateMonitorCallback mMonitorCallback = new KeyguardUpdateMonitorCallback()
  {
    public void onTimeChanged()
    {
      OPTextClock.-wrap2(OPTextClock.this);
    }
  };
  private boolean mShowCurrentUserTime;
  private final Runnable mTicker = new Runnable()
  {
    public void run()
    {
      OPTextClock.-wrap2(OPTextClock.this);
      long l = SystemClock.uptimeMillis();
      OPTextClock.this.getHandler().postAtTime(OPTextClock.-get0(OPTextClock.this), l + (1000L - l % 1000L));
    }
  };
  private Calendar mTime;
  private String mTimeZone;
  
  public OPTextClock(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public OPTextClock(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public OPTextClock(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public OPTextClock(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.TextClock, paramInt1, paramInt2);
    try
    {
      this.mFormat12 = paramContext.getText(0);
      this.mFormat24 = paramContext.getText(1);
      this.mTimeZone = paramContext.getString(2);
      paramContext.recycle();
      init();
      return;
    }
    finally
    {
      paramContext.recycle();
    }
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
  
  private void init()
  {
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
  }
  
  private void onTimeChanged()
  {
    this.mTime.setTimeInMillis(System.currentTimeMillis());
    setText(DateFormat.format(this.mFormat, this.mTime));
    setContentDescription(DateFormat.format(this.mDescFormat, this.mTime));
  }
  
  private void registerObserver()
  {
    ContentResolver localContentResolver;
    if (isAttachedToWindow())
    {
      if (this.mFormatChangeObserver == null) {
        this.mFormatChangeObserver = new FormatChangeObserver(getHandler());
      }
      localContentResolver = getContext().getContentResolver();
      if (this.mShowCurrentUserTime) {
        localContentResolver.registerContentObserver(Settings.System.CONTENT_URI, true, this.mFormatChangeObserver, -1);
      }
    }
    else
    {
      return;
    }
    localContentResolver.registerContentObserver(Settings.System.CONTENT_URI, true, this.mFormatChangeObserver);
  }
  
  private void registerReceiver()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
    getContext().registerReceiverAsUser(this.mIntentReceiver, Process.myUserHandle(), localIntentFilter, null, getHandler());
    KeyguardUpdateMonitor.getInstance(getContext()).registerCallback(this.mMonitorCallback);
  }
  
  private void unregisterObserver()
  {
    if (this.mFormatChangeObserver != null) {
      getContext().getContentResolver().unregisterContentObserver(this.mFormatChangeObserver);
    }
  }
  
  private void unregisterReceiver()
  {
    getContext().unregisterReceiver(this.mIntentReceiver);
    KeyguardUpdateMonitor.getInstance(getContext()).removeCallback(this.mMonitorCallback);
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
      if (this.mHasSeconds) {
        this.mTicker.run();
      }
    }
    else
    {
      return;
    }
    onTimeChanged();
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
  
  public void setContentDescriptionFormat12Hour(CharSequence paramCharSequence)
  {
    this.mDescFormat12 = paramCharSequence;
    chooseFormat();
    onTimeChanged();
  }
  
  public void setContentDescriptionFormat24Hour(CharSequence paramCharSequence)
  {
    this.mDescFormat24 = paramCharSequence;
    chooseFormat();
    onTimeChanged();
  }
  
  @RemotableViewMethod
  public void setFormat12Hour(CharSequence paramCharSequence)
  {
    this.mFormat12 = paramCharSequence;
    chooseFormat();
    onTimeChanged();
  }
  
  @RemotableViewMethod
  public void setFormat24Hour(CharSequence paramCharSequence)
  {
    this.mFormat24 = paramCharSequence;
    chooseFormat();
    onTimeChanged();
  }
  
  public void setShowCurrentUserTime(boolean paramBoolean)
  {
    this.mShowCurrentUserTime = paramBoolean;
    chooseFormat();
    onTimeChanged();
    unregisterObserver();
    registerObserver();
  }
  
  private class FormatChangeObserver
    extends ContentObserver
  {
    public FormatChangeObserver(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      OPTextClock.-wrap0(OPTextClock.this);
      OPTextClock.-wrap2(OPTextClock.this);
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      OPTextClock.-wrap0(OPTextClock.this);
      OPTextClock.-wrap2(OPTextClock.this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\clock\OPTextClock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */