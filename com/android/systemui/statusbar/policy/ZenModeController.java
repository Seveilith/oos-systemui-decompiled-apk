package com.android.systemui.statusbar.policy;

import android.net.Uri;
import android.service.notification.Condition;
import android.service.notification.ZenModeConfig;
import android.service.notification.ZenModeConfig.ZenRule;

public abstract interface ZenModeController
{
  public abstract void addCallback(Callback paramCallback);
  
  public abstract ZenModeConfig getConfig();
  
  public abstract ZenModeConfig.ZenRule getManualRule();
  
  public abstract long getNextAlarm();
  
  public abstract int getZen();
  
  public abstract boolean isCountdownConditionSupported();
  
  public abstract boolean isVolumeRestricted();
  
  public abstract void removeCallback(Callback paramCallback);
  
  public abstract void setUserId(int paramInt);
  
  public abstract void setZen(int paramInt, Uri paramUri, String paramString);
  
  public static class Callback
  {
    public void onConditionsChanged(Condition[] paramArrayOfCondition) {}
    
    public void onConfigChanged(ZenModeConfig paramZenModeConfig) {}
    
    public void onEffectsSupressorChanged() {}
    
    public void onManualRuleChanged(ZenModeConfig.ZenRule paramZenRule) {}
    
    public void onNextAlarmChanged() {}
    
    public void onZenAvailableChanged(boolean paramBoolean) {}
    
    public void onZenChanged(int paramInt) {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\ZenModeController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */