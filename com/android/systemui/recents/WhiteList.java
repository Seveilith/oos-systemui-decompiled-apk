package com.android.systemui.recents;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.oneplus.config.ConfigGrabber;
import com.oneplus.config.ConfigObserver;
import com.oneplus.config.ConfigObserver.ConfigUpdater;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WhiteList
{
  private static Context sContext;
  private static boolean sGetWhiteListFail;
  private static Handler sHandler;
  private static boolean sInit;
  private static ConfigObserver sOnlineConfigObserver;
  private static Set<String> sWhiteList = new HashSet();
  
  static
  {
    sInit = false;
    sGetWhiteListFail = true;
    sWhiteList.add("com.oppo.qetest");
    sWhiteList.add("com.oppo.qetest.remote");
    sWhiteList.add("com.oppo.qemonitor");
    sWhiteList.add("com.oppo.qemonitor.remote");
    sWhiteList.add("android");
    sWhiteList.add("system");
    sWhiteList.add("system:ui");
    sWhiteList.add("android.process.acore");
    sWhiteList.add("android.process.media");
    sWhiteList.add("com.android.systemui");
    sWhiteList.add("com.android.dialer");
    sWhiteList.add("com.android.vending");
    sWhiteList.add("com.android.nfc");
    sWhiteList.add("com.android.phone");
    sWhiteList.add("com.android.providers.telephony");
    sWhiteList.add("com.android.providers.media");
    sWhiteList.add("com.google.android.apps.inputmethod.zhuyin");
    sWhiteList.add("com.sohu.inputmethod.sogou");
    sWhiteList.add("com.baidu.input");
    sWhiteList.add("com.google.android.inputmethod.pinyin");
    sWhiteList.add("com.xinshuru.inputmethod");
    sWhiteList.add("com.cootek.smartinputv5");
    sWhiteList.add("com.tencent.qqpinyin");
    sWhiteList.add("com.touchtype.swiftkey");
    sWhiteList.add("com.syntellia.fleksy.keyboard");
    sWhiteList.add("com.baidu.input_huawei");
    sWhiteList.add("com.komoxo.octopusime");
    sWhiteList.add("com.bingime.ime");
    sWhiteList.add("com.nuance.swype.dtc");
    sWhiteList.add("com.iflytek.inputmethod");
    sWhiteList.add("com.emoji.keyboard.touchpal");
    sWhiteList.add("com.baidu.input_mi");
    sWhiteList.add("com.baidu.input_miv6");
    sWhiteList.add("com.baidu.input_yijia");
    sWhiteList.add("com.google.process.gapps");
    sWhiteList.add("com.google.android.deskclock");
    sWhiteList.add("com.google.android.gms.persistent");
    sWhiteList.add("com.google.android.gms");
    sWhiteList.add("com.google.android.gms.unstable");
    sWhiteList.add("com.google.android.apps.messaging");
    sWhiteList.add("com.google.android.gms.ui");
    sWhiteList.add("com.google.android.inputmethod.latin");
    sWhiteList.add("com.google.android.marvin.talkback");
    sWhiteList.add("com.google.android.tts");
    sWhiteList.add("com.google.android.googlequicksearchbox");
    sWhiteList.add("org.codeaurora.ims");
    sWhiteList.add("com.qualcomm.telephony");
    sWhiteList.add("com.qualcomm.qcrilmsgtunnel");
    sWhiteList.add("com.qualcomm.qti.telephonyservice");
    sWhiteList.add("com.qualcomm.qti.GBAHttpAuthentication.auth");
    sWhiteList.add("com.qti.qualcomm.datastatusnotification");
    sWhiteList.add("com.qualcomm.location.XT");
    sWhiteList.add("com.qualcomm.qti.tetherservice");
    sWhiteList.add("com.qualcomm.wfd.service");
    sWhiteList.add("com.nxp.spi");
    sWhiteList.add("com.oneplus.ota");
    sWhiteList.add("net.oneplus.deskclock");
    sWhiteList.add("com.oneplus.deskclock");
    sWhiteList.add("net.oneplus.odm.provider");
    sWhiteList.add("com.qq.tencent");
    sWhiteList.add("com.tencent.mobileqq");
    sWhiteList.add("com.wb.alert.sms");
    sWhiteList.add("com.zdworks.android.zdclock");
    sWhiteList.add("org.tpmkranz.notifyme");
    sWhiteList.add("mohammad.adib.sidebar");
    sWhiteList.add("com.tencent.mobileqqi");
    sWhiteList.add("com.cmcm.locker");
    sWhiteList.add("mobi.appplus.hilocker");
    sWhiteList.add("com.henry.app.optimizer.passcode.lockscreen");
    sWhiteList.add("com.solo.cm.go.locker");
    sWhiteList.add("com.jiubang.goscreenlock");
    sWhiteList.add("com.qti.service.colorservice");
    sWhiteList.add("com.android.bluetooth");
    sWhiteList.add("com.whatsapp");
    sWhiteList.add("jp.naver.line.android");
    sWhiteList.add("com.snapchat.android");
    sWhiteList.add("com.viber.voip");
    sWhiteList.add("com.bbm");
    sWhiteList.add("com.skype.raider");
    sWhiteList.add("com.skype.rover");
    sWhiteList.add("org.telegram.messenger");
    sWhiteList.add("com.facebook.katana");
    sWhiteList.add("com.linkedin.android");
    sWhiteList.add("com.facebook.orca");
    sWhiteList.add("com.tencent.mm");
    sWhiteList.add("cn.ledongli.ldl");
    sWhiteList.add("com.twitter.android");
    sWhiteList.add("com.sgiggle.production");
    sWhiteList.add("com.bsb.hike");
    sWhiteList.add("com.imo.android.imoim");
    sWhiteList.add("org.codeaurora.bluetooth");
    sWhiteList.add("com.google.android.apps.tachyon");
    sWhiteList.add("com.delcom.standard");
    sWhiteList.add("com.softsim.control");
    sWhiteList.add("com.UCMobile");
    sWhiteList.add("com.myfitnesspal.android");
    sWhiteList.add("com.amap.android.ams");
    sWhiteList.add("com.monefy.app.lite");
    sWhiteList.add("android.ext.services");
    sWhiteList.add("com.microsoft.teams");
    sWhiteList.add("com.oneplus.faceunlock");
    Log.d("WhiteList", "Raw WhiteList size=" + sWhiteList.size());
  }
  
  public static void dumpWhiteList(PrintWriter paramPrintWriter)
  {
    Object[] arrayOfObject = sWhiteList.toArray(new String[sWhiteList.size()]);
    if (paramPrintWriter != null)
    {
      paramPrintWriter.println("WHITE LIST APP:");
      paramPrintWriter.print("   ");
      i = 0;
      while (i < arrayOfObject.length)
      {
        paramPrintWriter.print(arrayOfObject[i]);
        paramPrintWriter.print(" , ");
        i += 1;
      }
      paramPrintWriter.println();
      return;
    }
    int i = 0;
    while (i < arrayOfObject.length)
    {
      Log.i("WhiteList", "idx=" + i + " pkg=" + arrayOfObject[i]);
      i += 1;
    }
  }
  
  private static void getWhiteList(JSONArray paramJSONArray)
  {
    int i;
    if (paramJSONArray != null) {
      i = 0;
    }
    for (;;)
    {
      try
      {
        if (i < paramJSONArray.length())
        {
          ??? = paramJSONArray.getJSONObject(i);
          if (!((JSONObject)???).getString("name").equals("do_not_kill_package_name")) {
            break label218;
          }
          JSONArray localJSONArray = ((JSONObject)???).getJSONArray("value");
          synchronized (sWhiteList)
          {
            sWhiteList.clear();
            int j = 0;
            if (j < localJSONArray.length())
            {
              sWhiteList.add(localJSONArray.getString(j));
              j += 1;
              continue;
            }
          }
        }
        Log.i("WhiteList", "jsonArray is null");
      }
      catch (JSONException paramJSONArray)
      {
        Log.e("WhiteList", "getWhiteList error. " + paramJSONArray);
        sGetWhiteListFail = true;
        return;
        sGetWhiteListFail = false;
        Log.v("WhiteList", "RecentAppWhiteList updated complete sWhiteList size=" + sWhiteList.size());
        return;
      }
      catch (Exception paramJSONArray)
      {
        Log.e("WhiteList", "getWhiteList error. " + paramJSONArray);
        sGetWhiteListFail = true;
        return;
      }
      sGetWhiteListFail = true;
      return;
      label218:
      i += 1;
    }
  }
  
  public static void init(Context paramContext, Handler paramHandler)
  {
    if (!sInit)
    {
      sInit = true;
      sContext = paramContext;
      sHandler = paramHandler;
      registerObserver();
    }
    if (sGetWhiteListFail) {
      sHandler.post(new Runnable()
      {
        public void run()
        {
          WhiteList.-wrap0(new ConfigGrabber(WhiteList.-get0().getApplicationContext(), "RecentAppWhiteList").grabConfig());
        }
      });
    }
  }
  
  public static boolean isInWhiteList(String paramString)
  {
    if ((!paramString.startsWith("com.android")) && (!paramString.contains("oneplus"))) {
      return sWhiteList.contains(paramString);
    }
    return true;
  }
  
  private static void registerObserver()
  {
    if (sOnlineConfigObserver == null)
    {
      OnlineConfigUpdater localOnlineConfigUpdater = new OnlineConfigUpdater(null);
      sOnlineConfigObserver = new ConfigObserver(sContext.getApplicationContext(), sHandler, localOnlineConfigUpdater, "RecentAppWhiteList");
      sOnlineConfigObserver.register();
    }
  }
  
  private static class OnlineConfigUpdater
    implements ConfigObserver.ConfigUpdater
  {
    public void updateConfig(JSONArray paramJSONArray)
    {
      Log.v("WhiteList", "Receive online config update");
      WhiteList.-wrap0(paramJSONArray);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\WhiteList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */