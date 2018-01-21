package com.android.systemui.qs;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings.Secure;

public abstract class SecureSetting
  extends ContentObserver
{
  private final Context mContext;
  private boolean mListening;
  private int mObservedValue = 0;
  private final String mSettingName;
  private int mUserId;
  
  public SecureSetting(Context paramContext, Handler paramHandler, String paramString)
  {
    super(paramHandler);
    this.mContext = paramContext;
    this.mSettingName = paramString;
    this.mUserId = ActivityManager.getCurrentUser();
  }
  
  public int getValue()
  {
    return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), this.mSettingName, 0, this.mUserId);
  }
  
  protected abstract void handleValueChanged(int paramInt, boolean paramBoolean);
  
  public void onChange(boolean paramBoolean)
  {
    int i = getValue();
    if (i != this.mObservedValue) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      handleValueChanged(i, paramBoolean);
      this.mObservedValue = i;
      return;
    }
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (paramBoolean == this.mListening) {
      return;
    }
    this.mListening = paramBoolean;
    if (paramBoolean)
    {
      this.mObservedValue = getValue();
      this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(this.mSettingName), false, this, this.mUserId);
      return;
    }
    this.mContext.getContentResolver().unregisterContentObserver(this);
    this.mObservedValue = 0;
  }
  
  public void setUserId(int paramInt)
  {
    this.mUserId = paramInt;
    if (this.mListening)
    {
      setListening(false);
      setListening(true);
    }
  }
  
  public void setValue(int paramInt)
  {
    Settings.Secure.putIntForUser(this.mContext.getContentResolver(), this.mSettingName, paramInt, this.mUserId);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\SecureSetting.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */