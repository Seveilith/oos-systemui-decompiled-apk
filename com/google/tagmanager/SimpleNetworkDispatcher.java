package com.google.tagmanager;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import com.google.android.gms.common.util.VisibleForTesting;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import org.apache.http.client.HttpClient;

class SimpleNetworkDispatcher
  implements Dispatcher
{
  private final Context ctx;
  private DispatchListener dispatchListener;
  private final HttpClient httpClient;
  private final String userAgent;
  
  @VisibleForTesting
  SimpleNetworkDispatcher(HttpClient paramHttpClient, Context paramContext, DispatchListener paramDispatchListener)
  {
    this.ctx = paramContext.getApplicationContext();
    this.userAgent = createUserAgentString("GoogleTagManager", "3.02", Build.VERSION.RELEASE, getUserAgentLanguage(Locale.getDefault()), Build.MODEL, Build.ID);
    this.httpClient = paramHttpClient;
    this.dispatchListener = paramDispatchListener;
  }
  
  static String getUserAgentLanguage(Locale paramLocale)
  {
    if (paramLocale != null) {
      if (paramLocale.getLanguage() != null) {
        break label15;
      }
    }
    label15:
    while (paramLocale.getLanguage().length() == 0)
    {
      return null;
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramLocale.getLanguage().toLowerCase());
    if (paramLocale.getCountry() == null) {}
    for (;;)
    {
      return localStringBuilder.toString();
      if (paramLocale.getCountry().length() != 0) {
        localStringBuilder.append("-").append(paramLocale.getCountry().toLowerCase());
      }
    }
  }
  
  String createUserAgentString(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
  {
    return String.format("%s/%s (Linux; U; Android %s; %s; %s Build/%s)", new Object[] { paramString1, paramString2, paramString3, paramString4, paramString5, paramString6 });
  }
  
  @VisibleForTesting
  URL getUrl(Hit paramHit)
  {
    paramHit = paramHit.getHitUrl();
    try
    {
      paramHit = new URL(paramHit);
      return paramHit;
    }
    catch (MalformedURLException paramHit)
    {
      Log.e("Error trying to parse the GTM url.");
    }
    return null;
  }
  
  public static abstract interface DispatchListener {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\google\tagmanager\SimpleNetworkDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */