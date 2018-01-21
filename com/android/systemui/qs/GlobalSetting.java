package com.android.systemui.qs;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings.Global;

public abstract class GlobalSetting
  extends ContentObserver
{
  private final Context mContext;
  private final String mSettingName;
  
  public GlobalSetting(Context paramContext, Handler paramHandler, String paramString)
  {
    super(paramHandler);
    this.mContext = paramContext;
    this.mSettingName = paramString;
  }
  
  public int getValue()
  {
    return Settings.Global.getInt(this.mContext.getContentResolver(), this.mSettingName, 0);
  }
  
  protected abstract void handleValueChanged(int paramInt);
  
  public void onChange(boolean paramBoolean)
  {
    handleValueChanged(getValue());
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor(this.mSettingName), false, this);
      return;
    }
    this.mContext.getContentResolver().unregisterContentObserver(this);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\GlobalSetting.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */