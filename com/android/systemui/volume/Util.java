package com.android.systemui.volume;

import android.content.Context;
import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.session.MediaController.PlaybackInfo;
import android.media.session.PlaybackState;
import android.provider.Settings.Global;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.oem.os.ThreeKeyManager;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class Util
{
  private static int[] AUDIO_MANAGER_FLAGS = { 1, 16, 4, 2, 8, 2048, 128, 4096, 1024 };
  private static String[] AUDIO_MANAGER_FLAG_NAMES = { "SHOW_UI", "VIBRATE", "PLAY_SOUND", "ALLOW_RINGER_MODES", "REMOVE_SOUND_AND_VIBRATE", "SHOW_VIBRATE_HINT", "SHOW_SILENT_HINT", "FROM_KEY", "SHOW_UI_WARNINGS" };
  private static final SimpleDateFormat HMMAA = new SimpleDateFormat("h:mm aa", Locale.US);
  
  public static String audioManagerFlagsToString(int paramInt)
  {
    return bitFieldToString(paramInt, AUDIO_MANAGER_FLAGS, AUDIO_MANAGER_FLAG_NAMES);
  }
  
  private static String bitFieldToString(int paramInt, int[] paramArrayOfInt, String[] paramArrayOfString)
  {
    if (paramInt == 0) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int j = 0;
    int i = paramInt;
    paramInt = j;
    while (paramInt < paramArrayOfInt.length)
    {
      if ((paramArrayOfInt[paramInt] & i) != 0)
      {
        if (localStringBuilder.length() > 0) {
          localStringBuilder.append(',');
        }
        localStringBuilder.append(paramArrayOfString[paramInt]);
      }
      i &= paramArrayOfInt[paramInt];
      paramInt += 1;
    }
    if (i != 0)
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(',');
      }
      localStringBuilder.append("UNKNOWN_").append(i);
    }
    return localStringBuilder.toString();
  }
  
  private static CharSequence emptyToNull(CharSequence paramCharSequence)
  {
    CharSequence localCharSequence;
    if (paramCharSequence != null)
    {
      localCharSequence = paramCharSequence;
      if (paramCharSequence.length() != 0) {}
    }
    else
    {
      localCharSequence = null;
    }
    return localCharSequence;
  }
  
  public static int getCorrectZenMode(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt3 > 0) && (paramInt2 == 1) && (paramInt1 == 0)) {
      return 3;
    }
    return paramInt1;
  }
  
  public static int getThreeKeyStatus(Context paramContext)
  {
    int j = -1;
    if (paramContext == null)
    {
      Log.e("Volume.Util", "getThreeKeyStatus error, context is null");
      return -1;
    }
    ThreeKeyManager localThreeKeyManager = (ThreeKeyManager)paramContext.getSystemService("threekey");
    int i = j;
    if (localThreeKeyManager != null) {}
    try
    {
      i = localThreeKeyManager.getThreeKeyStatus();
      j = i;
      if (i == -1) {
        j = Settings.Global.getInt(paramContext.getContentResolver(), "three_Key_mode", -1);
      }
      return j;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.e("Volume.Util", "Exception occurs, Three Key Service may not ready", localException);
        i = j;
      }
    }
  }
  
  public static boolean isVoiceCapable(Context paramContext)
  {
    paramContext = (TelephonyManager)paramContext.getSystemService("phone");
    if (paramContext != null) {
      return paramContext.isVoiceCapable();
    }
    return false;
  }
  
  public static String logTag(Class<?> paramClass)
  {
    paramClass = "vol." + paramClass.getSimpleName();
    if (paramClass.length() < 23) {
      return paramClass;
    }
    return paramClass.substring(0, 23);
  }
  
  public static String mediaMetadataToString(MediaMetadata paramMediaMetadata)
  {
    return paramMediaMetadata.getDescription().toString();
  }
  
  public static String playbackInfoToString(MediaController.PlaybackInfo paramPlaybackInfo)
  {
    if (paramPlaybackInfo == null) {
      return null;
    }
    String str1 = playbackInfoTypeToString(paramPlaybackInfo.getPlaybackType());
    String str2 = volumeProviderControlToString(paramPlaybackInfo.getVolumeControl());
    return String.format("PlaybackInfo[vol=%s,max=%s,type=%s,vc=%s],atts=%s", new Object[] { Integer.valueOf(paramPlaybackInfo.getCurrentVolume()), Integer.valueOf(paramPlaybackInfo.getMaxVolume()), str1, str2, paramPlaybackInfo.getAudioAttributes() });
  }
  
  public static String playbackInfoTypeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN_" + paramInt;
    case 1: 
      return "LOCAL";
    }
    return "REMOTE";
  }
  
  public static String playbackStateStateToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN_" + paramInt;
    case 0: 
      return "STATE_NONE";
    case 1: 
      return "STATE_STOPPED";
    case 2: 
      return "STATE_PAUSED";
    }
    return "STATE_PLAYING";
  }
  
  public static String playbackStateToString(PlaybackState paramPlaybackState)
  {
    if (paramPlaybackState == null) {
      return null;
    }
    return playbackStateStateToString(paramPlaybackState.getState()) + " " + paramPlaybackState;
  }
  
  public static String ringerModeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "RINGER_MODE_UNKNOWN_" + paramInt;
    case 0: 
      return "RINGER_MODE_SILENT";
    case 1: 
      return "RINGER_MODE_VIBRATE";
    }
    return "RINGER_MODE_NORMAL";
  }
  
  public static boolean setText(TextView paramTextView, CharSequence paramCharSequence)
  {
    if (Objects.equals(emptyToNull(paramTextView.getText()), emptyToNull(paramCharSequence))) {
      return false;
    }
    paramTextView.setText(paramCharSequence);
    return true;
  }
  
  public static final void setVisOrGone(View paramView, boolean paramBoolean)
  {
    int i = 0;
    if (paramView != null) {
      if (paramView.getVisibility() != 0) {
        break label21;
      }
    }
    label21:
    for (boolean bool = true; bool == paramBoolean; bool = false) {
      return;
    }
    if (paramBoolean) {}
    for (;;)
    {
      paramView.setVisibility(i);
      return;
      i = 8;
    }
  }
  
  public static String volumeProviderControlToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "VOLUME_CONTROL_UNKNOWN_" + paramInt;
    case 2: 
      return "VOLUME_CONTROL_ABSOLUTE";
    case 0: 
      return "VOLUME_CONTROL_FIXED";
    }
    return "VOLUME_CONTROL_RELATIVE";
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\volume\Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */