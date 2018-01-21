package com.android.systemui.doze;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.TimeUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DozeLog
{
  private static final boolean DEBUG = Log.isLoggable("DozeLog", 3);
  static final SimpleDateFormat FORMAT;
  private static final int SIZE;
  private static int sCount;
  private static SummaryStats sEmergencyCallStats;
  private static final KeyguardUpdateMonitorCallback sKeyguardCallback;
  private static String[] sMessages;
  private static SummaryStats sNotificationPulseStats;
  private static SummaryStats sPickupPulseNearVibrationStats;
  private static SummaryStats sPickupPulseNotNearVibrationStats;
  private static int sPosition;
  private static SummaryStats[][] sProxStats;
  private static boolean sPulsing;
  private static SummaryStats sScreenOnNotPulsingStats;
  private static SummaryStats sScreenOnPulsingStats;
  private static long sSince;
  private static long[] sTimes;
  
  static
  {
    if (Build.IS_DEBUGGABLE) {}
    for (int i = 400;; i = 50)
    {
      SIZE = i;
      FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
      sKeyguardCallback = new KeyguardUpdateMonitorCallback()
      {
        public void onEmergencyCallAction() {}
        
        public void onFinishedGoingToSleep(int paramAnonymousInt)
        {
          DozeLog.traceScreenOff(paramAnonymousInt);
        }
        
        public void onKeyguardBouncerChanged(boolean paramAnonymousBoolean)
        {
          DozeLog.traceKeyguardBouncerChanged(paramAnonymousBoolean);
        }
        
        public void onKeyguardVisibilityChanged(boolean paramAnonymousBoolean)
        {
          DozeLog.traceKeyguard(paramAnonymousBoolean);
        }
        
        public void onStartedWakingUp() {}
      };
      return;
    }
  }
  
  public static void dump(PrintWriter paramPrintWriter)
  {
    try
    {
      Object localObject = sMessages;
      if (localObject == null) {
        return;
      }
      paramPrintWriter.println("  Doze log:");
      int j = sPosition;
      int k = sCount;
      int m = SIZE;
      int n = SIZE;
      int i = 0;
      while (i < sCount)
      {
        int i1 = ((j - k + m) % n + i) % SIZE;
        paramPrintWriter.print("    ");
        paramPrintWriter.print(FORMAT.format(new Date(sTimes[i1])));
        paramPrintWriter.print(' ');
        paramPrintWriter.println(sMessages[i1]);
        i += 1;
      }
      paramPrintWriter.print("  Doze summary stats (for ");
      TimeUtils.formatDuration(System.currentTimeMillis() - sSince, paramPrintWriter);
      paramPrintWriter.println("):");
      sPickupPulseNearVibrationStats.dump(paramPrintWriter, "Pickup pulse (near vibration)");
      sPickupPulseNotNearVibrationStats.dump(paramPrintWriter, "Pickup pulse (not near vibration)");
      sNotificationPulseStats.dump(paramPrintWriter, "Notification pulse");
      sScreenOnPulsingStats.dump(paramPrintWriter, "Screen on (pulsing)");
      sScreenOnNotPulsingStats.dump(paramPrintWriter, "Screen on (not pulsing)");
      sEmergencyCallStats.dump(paramPrintWriter, "Emergency call");
      i = 0;
      while (i < 6)
      {
        localObject = pulseReasonToString(i);
        sProxStats[i][0].dump(paramPrintWriter, "Proximity near (" + (String)localObject + ")");
        sProxStats[i][1].dump(paramPrintWriter, "Proximity far (" + (String)localObject + ")");
        i += 1;
      }
      return;
    }
    finally {}
  }
  
  private static void init(Context paramContext)
  {
    try
    {
      if (sMessages == null)
      {
        sTimes = new long[SIZE];
        sMessages = new String[SIZE];
        sSince = System.currentTimeMillis();
        sPickupPulseNearVibrationStats = new SummaryStats(null);
        sPickupPulseNotNearVibrationStats = new SummaryStats(null);
        sNotificationPulseStats = new SummaryStats(null);
        sScreenOnPulsingStats = new SummaryStats(null);
        sScreenOnNotPulsingStats = new SummaryStats(null);
        sEmergencyCallStats = new SummaryStats(null);
        sProxStats = (SummaryStats[][])Array.newInstance(SummaryStats.class, new int[] { 6, 2 });
        int i = 0;
        while (i < 6)
        {
          sProxStats[i][0] = new SummaryStats(null);
          sProxStats[i][1] = new SummaryStats(null);
          i += 1;
        }
        log("init");
        KeyguardUpdateMonitor.getInstance(paramContext).registerCallback(sKeyguardCallback);
      }
      return;
    }
    finally {}
  }
  
  private static void log(String paramString)
  {
    try
    {
      String[] arrayOfString = sMessages;
      if (arrayOfString == null) {
        return;
      }
      sTimes[sPosition] = System.currentTimeMillis();
      sMessages[sPosition] = paramString;
      sPosition = (sPosition + 1) % SIZE;
      sCount = Math.min(sCount + 1, SIZE);
      if (DEBUG) {
        Log.d("DozeLog", paramString);
      }
      return;
    }
    finally {}
  }
  
  public static String pulseReasonToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("bad reason: " + paramInt);
    case 0: 
      return "intent";
    case 1: 
      return "notification";
    case 2: 
      return "sigmotion";
    case 3: 
      return "pickup";
    case 4: 
      return "doubletap";
    }
    return "proximity";
  }
  
  public static void traceDozing(Context paramContext, boolean paramBoolean)
  {
    sPulsing = false;
    init(paramContext);
    log("dozing " + paramBoolean);
  }
  
  public static void traceEmergencyCall()
  {
    log("emergencyCall");
    sEmergencyCallStats.append();
  }
  
  public static void traceFling(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    log("fling expand=" + paramBoolean1 + " aboveThreshold=" + paramBoolean2 + " thresholdNeeded=" + paramBoolean3 + " screenOnFromTouch=" + paramBoolean4);
  }
  
  public static void traceKeyguard(boolean paramBoolean)
  {
    log("keyguard " + paramBoolean);
    if (!paramBoolean) {
      sPulsing = false;
    }
  }
  
  public static void traceKeyguardBouncerChanged(boolean paramBoolean)
  {
    log("bouncer " + paramBoolean);
  }
  
  public static void traceProximityResult(Context paramContext, boolean paramBoolean, long paramLong, int paramInt)
  {
    init(paramContext);
    log("proximityResult reason=" + pulseReasonToString(paramInt) + " near=" + paramBoolean + " millis=" + paramLong);
    paramContext = sProxStats[paramInt];
    if (paramBoolean) {}
    for (paramInt = 0;; paramInt = 1)
    {
      paramContext[paramInt].append();
      return;
    }
  }
  
  public static void tracePulseFinish()
  {
    sPulsing = false;
    log("pulseFinish");
  }
  
  public static void tracePulseStart(int paramInt)
  {
    sPulsing = true;
    log("pulseStart reason=" + pulseReasonToString(paramInt));
  }
  
  public static void traceScreenOff(int paramInt)
  {
    log("screenOff why=" + paramInt);
  }
  
  public static void traceScreenOn()
  {
    log("screenOn pulsing=" + sPulsing);
    if (sPulsing) {}
    for (SummaryStats localSummaryStats = sScreenOnPulsingStats;; localSummaryStats = sScreenOnNotPulsingStats)
    {
      localSummaryStats.append();
      sPulsing = false;
      return;
    }
  }
  
  private static class SummaryStats
  {
    private int mCount;
    
    public void append()
    {
      this.mCount += 1;
    }
    
    public void dump(PrintWriter paramPrintWriter, String paramString)
    {
      if (this.mCount == 0) {
        return;
      }
      paramPrintWriter.print("    ");
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(": n=");
      paramPrintWriter.print(this.mCount);
      paramPrintWriter.print(" (");
      paramPrintWriter.print(this.mCount / (System.currentTimeMillis() - DozeLog.-get0()) * 1000.0D * 60.0D * 60.0D);
      paramPrintWriter.print("/hr)");
      paramPrintWriter.println();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\doze\DozeLog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */