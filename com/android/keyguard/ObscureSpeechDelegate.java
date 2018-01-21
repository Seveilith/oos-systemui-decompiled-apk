package com.android.keyguard;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

class ObscureSpeechDelegate
  extends View.AccessibilityDelegate
{
  static boolean sAnnouncedHeadset = false;
  private final AudioManager mAudioManager;
  private final ContentResolver mContentResolver;
  
  public ObscureSpeechDelegate(Context paramContext)
  {
    this.mContentResolver = paramContext.getContentResolver();
    this.mAudioManager = ((AudioManager)paramContext.getSystemService("audio"));
  }
  
  private boolean shouldObscureSpeech()
  {
    if (Settings.Secure.getIntForUser(this.mContentResolver, "speak_password", 0, -2) != 0) {
      return false;
    }
    return (!this.mAudioManager.isWiredHeadsetOn()) && (!this.mAudioManager.isBluetoothA2dpOn());
  }
  
  public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfo);
    if (shouldObscureSpeech())
    {
      paramView = paramView.getContext();
      paramAccessibilityNodeInfo.setText(null);
      paramAccessibilityNodeInfo.setContentDescription(paramView.getString(17040599));
    }
  }
  
  public void onPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    super.onPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
    if ((paramAccessibilityEvent.getEventType() != 16384) && (shouldObscureSpeech()))
    {
      paramAccessibilityEvent.getText().clear();
      paramAccessibilityEvent.setContentDescription(paramView.getContext().getString(17040599));
    }
  }
  
  public void sendAccessibilityEvent(View paramView, int paramInt)
  {
    super.sendAccessibilityEvent(paramView, paramInt);
    if ((paramInt != 32768) || (sAnnouncedHeadset)) {}
    while (!shouldObscureSpeech()) {
      return;
    }
    sAnnouncedHeadset = true;
    paramView.announceForAccessibility(paramView.getContext().getString(17040598));
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\keyguard\ObscureSpeechDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */