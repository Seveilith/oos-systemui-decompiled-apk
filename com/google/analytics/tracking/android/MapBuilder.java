package com.google.analytics.tracking.android;

import android.text.TextUtils;
import java.util.HashMap;
import java.util.Map;

public class MapBuilder
{
  private Map<String, String> map = new HashMap();
  
  public static MapBuilder createEvent(String paramString1, String paramString2, String paramString3, Long paramLong)
  {
    Object localObject = null;
    GAUsage.getInstance().setUsage(GAUsage.Field.CONSTRUCT_EVENT);
    MapBuilder localMapBuilder = new MapBuilder();
    localMapBuilder.set("&t", "event");
    localMapBuilder.set("&ec", paramString1);
    localMapBuilder.set("&ea", paramString2);
    localMapBuilder.set("&el", paramString3);
    paramString1 = (String)localObject;
    if (paramLong != null) {
      paramString1 = Long.toString(paramLong.longValue());
    }
    localMapBuilder.set("&ev", paramString1);
    return localMapBuilder;
  }
  
  public Map<String, String> build()
  {
    return new HashMap(this.map);
  }
  
  public MapBuilder set(String paramString1, String paramString2)
  {
    GAUsage.getInstance().setUsage(GAUsage.Field.MAP_BUILDER_SET);
    if (paramString1 == null)
    {
      Log.w(" MapBuilder.set() called with a null paramName.");
      return this;
    }
    this.map.put(paramString1, paramString2);
    return this;
  }
  
  public MapBuilder setCampaignParamsFromUrl(String paramString)
  {
    GAUsage.getInstance().setUsage(GAUsage.Field.MAP_BUILDER_SET_CAMPAIGN_PARAMS);
    paramString = Utils.filterCampaign(paramString);
    if (!TextUtils.isEmpty(paramString))
    {
      paramString = Utils.parseURLParameters(paramString);
      set("&cc", (String)paramString.get("utm_content"));
      set("&cm", (String)paramString.get("utm_medium"));
      set("&cn", (String)paramString.get("utm_campaign"));
      set("&cs", (String)paramString.get("utm_source"));
      set("&ck", (String)paramString.get("utm_term"));
      set("&ci", (String)paramString.get("utm_id"));
      set("&gclid", (String)paramString.get("gclid"));
      set("&dclid", (String)paramString.get("dclid"));
      set("&gmob_t", (String)paramString.get("gmob_t"));
      return this;
    }
    return this;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\google\analytics\tracking\android\MapBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */