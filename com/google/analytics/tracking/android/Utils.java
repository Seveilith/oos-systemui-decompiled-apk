package com.google.analytics.tracking.android;

import android.text.TextUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class Utils
{
  private static final char[] HEXBYTES = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  
  public static String filterCampaign(String paramString)
  {
    Object localObject1;
    Object localObject2;
    int i;
    if (!TextUtils.isEmpty(paramString))
    {
      localObject1 = paramString;
      if (paramString.contains("?")) {
        break label120;
      }
      paramString = (String)localObject1;
      if (paramString.contains("%3D")) {
        break label142;
      }
      if (!paramString.contains("=")) {
        break label155;
      }
      paramString = parseURLParameters(paramString);
      localObject1 = new String[9];
      localObject1[0] = "dclid";
      localObject1[1] = "utm_source";
      localObject1[2] = "gclid";
      localObject1[3] = "utm_campaign";
      localObject1[4] = "utm_medium";
      localObject1[5] = "utm_term";
      localObject1[6] = "utm_content";
      localObject1[7] = "utm_id";
      localObject1[8] = "gmob_t";
      localObject2 = new StringBuilder();
      i = 0;
    }
    for (;;)
    {
      if (i >= localObject1.length)
      {
        return ((StringBuilder)localObject2).toString();
        return null;
        label120:
        localObject2 = paramString.split("[\\?]");
        paramString = (String)localObject1;
        if (localObject2.length <= 1) {
          break;
        }
        paramString = localObject2[1];
        break;
        try
        {
          label142:
          paramString = URLDecoder.decode(paramString, "UTF-8");
        }
        catch (UnsupportedEncodingException paramString)
        {
          return null;
        }
        label155:
        return null;
      }
      if (!TextUtils.isEmpty((CharSequence)paramString.get(localObject1[i]))) {
        break label182;
      }
      i += 1;
    }
    label182:
    if (((StringBuilder)localObject2).length() <= 0) {}
    for (;;)
    {
      ((StringBuilder)localObject2).append(localObject1[i]).append("=").append((String)paramString.get(localObject1[i]));
      break;
      ((StringBuilder)localObject2).append("&");
    }
  }
  
  static String getLanguage(Locale paramLocale)
  {
    StringBuilder localStringBuilder;
    if (paramLocale != null)
    {
      if (TextUtils.isEmpty(paramLocale.getLanguage())) {
        break label51;
      }
      localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramLocale.getLanguage().toLowerCase());
      if (!TextUtils.isEmpty(paramLocale.getCountry())) {
        break label53;
      }
    }
    for (;;)
    {
      return localStringBuilder.toString();
      return null;
      label51:
      return null;
      label53:
      localStringBuilder.append("-").append(paramLocale.getCountry().toLowerCase());
    }
  }
  
  public static Map<String, String> parseURLParameters(String paramString)
  {
    HashMap localHashMap = new HashMap();
    paramString = paramString.split("&");
    int j = paramString.length;
    int i = 0;
    if (i >= j) {
      return localHashMap;
    }
    String[] arrayOfString = paramString[i].split("=");
    if (arrayOfString.length <= 1) {
      if (arrayOfString.length == 1) {
        break label76;
      }
    }
    for (;;)
    {
      i += 1;
      break;
      localHashMap.put(arrayOfString[0], arrayOfString[1]);
      continue;
      label76:
      if (arrayOfString[0].length() != 0) {
        localHashMap.put(arrayOfString[0], null);
      }
    }
  }
  
  public static void putIfAbsent(Map<String, String> paramMap, String paramString1, String paramString2)
  {
    if (paramMap.containsKey(paramString1)) {
      return;
    }
    paramMap.put(paramString1, paramString2);
  }
  
  public static boolean safeParseBoolean(String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      return paramBoolean;
    }
    if (paramString.equalsIgnoreCase("true")) {}
    while ((paramString.equalsIgnoreCase("yes")) || (paramString.equalsIgnoreCase("1"))) {
      return true;
    }
    if (paramString.equalsIgnoreCase("false")) {}
    while ((paramString.equalsIgnoreCase("no")) || (paramString.equalsIgnoreCase("0"))) {
      return false;
    }
    return paramBoolean;
  }
  
  public static double safeParseDouble(String paramString, double paramDouble)
  {
    if (paramString != null) {}
    try
    {
      double d = Double.parseDouble(paramString);
      return d;
    }
    catch (NumberFormatException paramString) {}
    return paramDouble;
    return paramDouble;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\google\analytics\tracking\android\Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */