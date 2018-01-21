package com.android.systemui.volume;

import android.content.Context;
import android.media.AudioSystem;
import android.util.Log;
import com.android.internal.logging.MetricsLogger;
import java.util.Arrays;

public class Events
{
  public static final String[] DISMISS_REASONS = { "unknown", "touch_outside", "volume_controller", "timeout", "screen_off", "settings_clicked", "done_clicked" };
  private static final String[] EVENT_TAGS;
  public static final String[] SHOW_REASONS = { "unknown", "volume_changed", "remote_volume_changed" };
  private static final String TAG = Util.logTag(Events.class);
  public static Callback sCallback;
  
  static
  {
    EVENT_TAGS = new String[] { "show_dialog", "dismiss_dialog", "active_stream_changed", "expand", "key", "collection_started", "collection_stopped", "icon_click", "settings_click", "touch_level_changed", "level_changed", "internal_ringer_mode_changed", "external_ringer_mode_changed", "zen_mode_changed", "suppressor_changed", "mute_changed", "touch_level_done", "status_bar_icon_changed" };
  }
  
  private static String iconStateToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "unknown_state_" + paramInt;
    case 1: 
      return "unmute";
    case 2: 
      return "mute";
    }
    return "vibrate";
  }
  
  private static String ringerModeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "unknown";
    case 0: 
      return "silent";
    case 1: 
      return "vibrate";
    }
    return "normal";
  }
  
  public static void writeEvent(Context paramContext, int paramInt, Object... paramVarArgs)
  {
    long l = System.currentTimeMillis();
    StringBuilder localStringBuilder = new StringBuilder("writeEvent ").append(EVENT_TAGS[paramInt]);
    if ((paramVarArgs != null) && (paramVarArgs.length > 0))
    {
      localStringBuilder.append(" ");
      switch (paramInt)
      {
      case 5: 
      case 6: 
      case 8: 
      default: 
        localStringBuilder.append(Arrays.asList(paramVarArgs));
      }
    }
    for (;;)
    {
      Log.i(TAG, localStringBuilder.toString());
      if (sCallback != null) {
        sCallback.writeEvent(l, paramInt, paramVarArgs);
      }
      return;
      MetricsLogger.visible(paramContext, 207);
      if (((Boolean)paramVarArgs[1]).booleanValue()) {}
      for (int i = 1;; i = 0)
      {
        MetricsLogger.histogram(paramContext, "volume_from_keyguard", i);
        localStringBuilder.append(SHOW_REASONS[((Integer)paramVarArgs[0]).intValue()]).append(" keyguard=").append(paramVarArgs[1]);
        break;
      }
      MetricsLogger.visibility(paramContext, 208, ((Boolean)paramVarArgs[0]).booleanValue());
      localStringBuilder.append(paramVarArgs[0]);
      continue;
      MetricsLogger.hidden(paramContext, 207);
      localStringBuilder.append(DISMISS_REASONS[((Integer)paramVarArgs[0]).intValue()]);
      continue;
      MetricsLogger.action(paramContext, 210, ((Integer)paramVarArgs[0]).intValue());
      localStringBuilder.append(AudioSystem.streamToString(((Integer)paramVarArgs[0]).intValue()));
      continue;
      MetricsLogger.action(paramContext, 212, ((Integer)paramVarArgs[1]).intValue());
      localStringBuilder.append(AudioSystem.streamToString(((Integer)paramVarArgs[0]).intValue())).append(' ').append(iconStateToString(((Integer)paramVarArgs[1]).intValue()));
      continue;
      MetricsLogger.action(paramContext, 209, ((Integer)paramVarArgs[1]).intValue());
      localStringBuilder.append(AudioSystem.streamToString(((Integer)paramVarArgs[0]).intValue())).append(' ').append(paramVarArgs[1]);
      continue;
      MetricsLogger.action(paramContext, 211, ((Integer)paramVarArgs[1]).intValue());
      localStringBuilder.append(AudioSystem.streamToString(((Integer)paramVarArgs[0]).intValue())).append(' ').append(paramVarArgs[1]);
      continue;
      MetricsLogger.action(paramContext, 213, ((Integer)paramVarArgs[0]).intValue());
      localStringBuilder.append(ringerModeToString(((Integer)paramVarArgs[0]).intValue()));
      continue;
      localStringBuilder.append(zenModeToString(((Integer)paramVarArgs[0]).intValue()));
      continue;
      localStringBuilder.append(paramVarArgs[0]).append(' ').append(paramVarArgs[1]);
      continue;
      localStringBuilder.append(paramVarArgs[0]);
    }
  }
  
  public static void writeState(long paramLong, VolumeDialogController.State paramState)
  {
    if (sCallback != null) {
      sCallback.writeState(paramLong, paramState);
    }
  }
  
  private static String zenModeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "unknown";
    case 0: 
      return "off";
    case 1: 
      return "important_interruptions";
    case 3: 
      return "alarms";
    }
    return "no_interruptions";
  }
  
  public static abstract interface Callback
  {
    public abstract void writeEvent(long paramLong, int paramInt, Object[] paramArrayOfObject);
    
    public abstract void writeState(long paramLong, VolumeDialogController.State paramState);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\volume\Events.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */