package com.android.systemui.util;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import net.oneplus.odm.insight.tracker.AppTracker;

public class MdmLogger
{
  private static GoogleAnalyticsHelper gaInstance;
  private static AppTracker mAppTracker;
  private static Handler sHandler;
  private static HandlerThread sHandlerThread;
  private static MdmLogger sInstance = null;
  private static Map<String, String> sTagMap;
  public static boolean sTouchGear;
  
  static
  {
    gaInstance = null;
    mAppTracker = null;
    sTouchGear = false;
    sTagMap = new HashMap();
    sTagMap.put("Tile.AirplaneModeTile", "quick_airplane");
    sTagMap.put("Tile.BatteryTile", "quick_battery");
    sTagMap.put("Tile.BluetoothTile", "quick_bt");
    sTagMap.put("Tile.CastTile", "quick_cast");
    sTagMap.put("Tile.CellularTile", "quick_mobile");
    sTagMap.put("Tile.ColorInversionTile", "quick_invert");
    sTagMap.put("Tile.DataSaverTile", "quick_ds");
    sTagMap.put("Tile.FlashlightTile", "quick_fl");
    sTagMap.put("Tile.GameModeTile", "quick_game");
    sTagMap.put("Tile.HotspotTile", "quick_hot");
    sTagMap.put("Tile.LocationTile", "quick_location");
    sTagMap.put("Tile.NfcTile", "quick_nfc");
    sTagMap.put("Tile.NightDisplayTile", "quick_night");
    sTagMap.put("Tile.ReadModeTile", "quick_read");
    sTagMap.put("Tile.RotationLockTile", "quick_ar");
    sTagMap.put("Tile.VPNTile", "quick_vpn");
    sTagMap.put("Tile.WifiTile", "quick_wifi");
    sTagMap.put("Tile.WorkModeTile", "quick_work");
    sTagMap.put("Tile.OtgTile", "quick_otg");
  }
  
  private MdmLogger(Context paramContext)
  {
    mAppTracker = new AppTracker(paramContext);
    if (sHandlerThread == null)
    {
      sHandlerThread = new HandlerThread("MdmLogger", 10);
      sHandlerThread.start();
      sHandler = new Handler(sHandlerThread.getLooper());
    }
  }
  
  public static void init(Context paramContext)
  {
    if (sInstance == null) {
      sInstance = new MdmLogger(paramContext);
    }
    if (gaInstance == null) {
      gaInstance = GoogleAnalyticsHelper.getInstance(paramContext);
    }
    Log.d("MdmLogger", "MdmLogger is initialized");
  }
  
  public static void log(final String paramString1, String paramString2, final String paramString3)
  {
    sHandler.post(new Runnable()
    {
      public void run()
      {
        HashMap localHashMap = new HashMap();
        localHashMap.put(this.val$label, paramString3);
        MdmLogger.-get1().onEvent(paramString1, localHashMap);
        if (MdmLogger.-get0() != null) {
          MdmLogger.-get0().send(paramString1, this.val$label, paramString3);
        }
      }
    });
  }
  
  public static void logQsTile(String paramString1, String paramString2, String paramString3)
  {
    String str = (String)sTagMap.get(paramString1);
    if (str != null)
    {
      log(str, paramString2, paramString3);
      return;
    }
    Log.e("MdmLogger", "Cannot get tag from tileTag : " + paramString1);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\util\MdmLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */