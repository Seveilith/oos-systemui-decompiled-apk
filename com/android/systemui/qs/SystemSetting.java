package com.android.systemui.qs;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings.System;

public abstract class SystemSetting
  extends ContentObserver
{
  private final Context mContext;
  private boolean mCurrentUserOnly;
  private final String mSettingName;
  
  public SystemSetting(Context paramContext, Handler paramHandler, String paramString)
  {
    this(paramContext, paramHandler, paramString, false);
  }
  
  public SystemSetting(Context paramContext, Handler paramHandler, String paramString, boolean paramBoolean)
  {
    super(paramHandler);
    this.mContext = paramContext;
    this.mSettingName = paramString;
    this.mCurrentUserOnly = paramBoolean;
  }
  
  public int getValue()
  {
    return getValue(0);
  }
  
  public int getValue(int paramInt)
  {
    if (this.mCurrentUserOnly) {
      return Settings.System.getIntForUser(this.mContext.getContentResolver(), this.mSettingName, paramInt, -2);
    }
    return Settings.System.getInt(this.mContext.getContentResolver(), this.mSettingName, paramInt);
  }
  
  protected abstract void handleValueChanged(int paramInt, boolean paramBoolean);
  
  public void onChange(boolean paramBoolean)
  {
    handleValueChanged(getValue(), paramBoolean);
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (this.mCurrentUserOnly)
      {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(this.mSettingName), false, this, -2);
        return;
      }
      this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(this.mSettingName), false, this);
      return;
    }
    this.mContext.getContentResolver().unregisterContentObserver(this);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\SystemSetting.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */