package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Resources;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.util.MathUtils;
import android.util.SparseBooleanArray;
import java.io.PrintWriter;

public class DozeParameters
{
  private static final boolean DEBUG = Log.isLoggable("DozeParameters", 3);
  private static IntInOutMatcher sPickupSubtypePerformsProxMatcher;
  private final Context mContext;
  
  public DozeParameters(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private String dumpPickupSubtypePerformsProxCheck()
  {
    getPickupSubtypePerformsProxCheck(0);
    if (sPickupSubtypePerformsProxMatcher == null) {
      return "fallback: " + this.mContext.getResources().getBoolean(2131558434);
    }
    return "spec: " + sPickupSubtypePerformsProxMatcher.mSpec;
  }
  
  private boolean getBoolean(String paramString, int paramInt)
  {
    return SystemProperties.getBoolean(paramString, this.mContext.getResources().getBoolean(paramInt));
  }
  
  private int getInt(String paramString, int paramInt)
  {
    return MathUtils.constrain(SystemProperties.getInt(paramString, this.mContext.getResources().getInteger(paramInt)), 0, 30000);
  }
  
  private String getString(String paramString, int paramInt)
  {
    return SystemProperties.get(paramString, this.mContext.getString(paramInt));
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("  DozeParameters:");
    paramPrintWriter.print("    getDisplayStateSupported(): ");
    paramPrintWriter.println(getDisplayStateSupported());
    paramPrintWriter.print("    getPulseDuration(pickup=false): ");
    paramPrintWriter.println(getPulseDuration(false));
    paramPrintWriter.print("    getPulseDuration(pickup=true): ");
    paramPrintWriter.println(getPulseDuration(true));
    paramPrintWriter.print("    getPulseInDuration(pickup=false): ");
    paramPrintWriter.println(getPulseInDuration(false));
    paramPrintWriter.print("    getPulseInDuration(pickup=true): ");
    paramPrintWriter.println(getPulseInDuration(true));
    paramPrintWriter.print("    getPulseInVisibleDuration(): ");
    paramPrintWriter.println(getPulseVisibleDuration());
    paramPrintWriter.print("    getPulseOutDuration(): ");
    paramPrintWriter.println(getPulseOutDuration());
    paramPrintWriter.print("    getPulseOnSigMotion(): ");
    paramPrintWriter.println(getPulseOnSigMotion());
    paramPrintWriter.print("    getVibrateOnSigMotion(): ");
    paramPrintWriter.println(getVibrateOnSigMotion());
    paramPrintWriter.print("    getVibrateOnPickup(): ");
    paramPrintWriter.println(getVibrateOnPickup());
    paramPrintWriter.print("    getProxCheckBeforePulse(): ");
    paramPrintWriter.println(getProxCheckBeforePulse());
    paramPrintWriter.print("    getPickupVibrationThreshold(): ");
    paramPrintWriter.println(getPickupVibrationThreshold());
    paramPrintWriter.print("    getPickupSubtypePerformsProxCheck(): ");
    paramPrintWriter.println(dumpPickupSubtypePerformsProxCheck());
  }
  
  public boolean getDisplayStateSupported()
  {
    return getBoolean("doze.display.supported", 2131558450);
  }
  
  public boolean getPickupSubtypePerformsProxCheck(int paramInt)
  {
    String str = getString("doze.pickup.proxcheck", 2131689912);
    if (TextUtils.isEmpty(str)) {
      return this.mContext.getResources().getBoolean(2131558434);
    }
    if ((sPickupSubtypePerformsProxMatcher != null) && (TextUtils.equals(str, sPickupSubtypePerformsProxMatcher.mSpec))) {}
    for (;;)
    {
      return sPickupSubtypePerformsProxMatcher.isIn(paramInt);
      sPickupSubtypePerformsProxMatcher = new IntInOutMatcher(str);
    }
  }
  
  public int getPickupVibrationThreshold()
  {
    return getInt("doze.pickup.vibration.threshold", 2131624007);
  }
  
  public boolean getProxCheckBeforePulse()
  {
    return getBoolean("doze.pulse.proxcheck", 2131558432);
  }
  
  public int getPulseDuration(boolean paramBoolean)
  {
    return getPulseInDuration(paramBoolean) + getPulseVisibleDuration() + getPulseOutDuration();
  }
  
  public int getPulseInDuration(boolean paramBoolean)
  {
    if (paramBoolean) {
      return getInt("doze.pulse.duration.in.pickup", 2131624009);
    }
    return getInt("doze.pulse.duration.in", 2131624008);
  }
  
  public boolean getPulseOnSigMotion()
  {
    return getBoolean("doze.pulse.sigmotion", 2131558431);
  }
  
  public int getPulseOutDuration()
  {
    return getInt("doze.pulse.duration.out", 2131624011);
  }
  
  public int getPulseVisibleDuration()
  {
    return getInt("doze.pulse.duration.visible", 2131624010);
  }
  
  public int getPulseVisibleDuration(boolean paramBoolean)
  {
    if (paramBoolean) {
      return getInt("doze.pulse.duration.visible.p", 2131624032);
    }
    return getInt("doze.pulse.duration.visible", 2131624010);
  }
  
  public boolean getVibrateOnPickup()
  {
    return SystemProperties.getBoolean("doze.vibrate.pickup", false);
  }
  
  public boolean getVibrateOnSigMotion()
  {
    return SystemProperties.getBoolean("doze.vibrate.sigmotion", false);
  }
  
  public static class IntInOutMatcher
  {
    private final boolean mDefaultIsIn;
    private final SparseBooleanArray mIsIn;
    final String mSpec;
    
    public IntInOutMatcher(String paramString)
    {
      if (TextUtils.isEmpty(paramString)) {
        throw new IllegalArgumentException("Spec must not be empty");
      }
      boolean bool2 = false;
      int j = 0;
      this.mSpec = paramString;
      this.mIsIn = new SparseBooleanArray();
      String[] arrayOfString = paramString.split(",", -1);
      int k = arrayOfString.length;
      int i = 0;
      if (i < k)
      {
        String str2 = arrayOfString[i];
        if (str2.length() == 0) {
          throw new IllegalArgumentException("Illegal spec, must not have zero-length items: `" + paramString + "`");
        }
        boolean bool1;
        if (str2.charAt(0) != '!')
        {
          bool1 = true;
          if (!bool1) {
            break label179;
          }
        }
        label179:
        for (String str1 = str2;; str1 = str2.substring(1))
        {
          if (str2.length() != 0) {
            break label190;
          }
          throw new IllegalArgumentException("Illegal spec, must not have zero-length items: `" + paramString + "`");
          bool1 = false;
          break;
        }
        label190:
        if ("*".equals(str1))
        {
          if (j != 0) {
            throw new IllegalArgumentException("Illegal spec, `*` must not appear multiple times in `" + paramString + "`");
          }
          j = 1;
        }
        for (;;)
        {
          i += 1;
          bool2 = bool1;
          break;
          int m = Integer.parseInt(str1);
          if (this.mIsIn.indexOfKey(m) >= 0) {
            throw new IllegalArgumentException("Illegal spec, `" + m + "` must not appear multiple times in `" + paramString + "`");
          }
          this.mIsIn.put(m, bool1);
          bool1 = bool2;
        }
      }
      if (j == 0) {
        throw new IllegalArgumentException("Illegal spec, must specify either * or !*");
      }
      this.mDefaultIsIn = bool2;
    }
    
    public boolean isIn(int paramInt)
    {
      return this.mIsIn.get(paramInt, this.mDefaultIsIn);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\DozeParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */