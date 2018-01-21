package com.android.systemui.util;

import android.content.Context;
import android.os.SystemProperties;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Logger;
import com.google.analytics.tracking.android.Logger.LogLevel;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

public class GoogleAnalyticsHelper
{
  private static String TAG = GoogleAnalyticsHelper.class.getSimpleName();
  private static Tracker mTracker;
  private static GoogleAnalyticsHelper sInstance;
  
  public static GoogleAnalyticsHelper getInstance(Context paramContext)
  {
    if ((sInstance == null) && (isBetaRom()))
    {
      sInstance = new GoogleAnalyticsHelper();
      paramContext = GoogleAnalytics.getInstance(paramContext);
      paramContext.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
      mTracker = paramContext.getTracker("UA-92966593-4");
    }
    return sInstance;
  }
  
  private static boolean isBetaRom()
  {
    String str1 = SystemProperties.get("ro.build.beta");
    String str2 = SystemProperties.get("persist.op.ga");
    return ("1".equals(str1)) || ("1".equals(str2));
  }
  
  public void send(String paramString1, String paramString2, String paramString3)
  {
    if (mTracker != null) {
      mTracker.send(MapBuilder.createEvent(paramString1, paramString3, paramString2, null).build());
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\util\GoogleAnalyticsHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */