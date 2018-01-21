package com.android.systemui.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.util.Log;
import android.util.OpFeatures;
import com.android.keyguard.KeyguardUpdateMonitor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class Utils
{
  public static final boolean DEBUG_ONEPLUS;
  private static int mDisplayLongLength;
  private static int mDisplayShortLength;
  private static boolean mIsHomeApp;
  private static boolean mIsScreenCompat;
  private static boolean mIsSystemUI;
  private static String sDeviceTag = "";
  private static Set<String> sFPNotResumeList;
  public static boolean sSystemReady;
  
  static
  {
    DEBUG_ONEPLUS = Build.DEBUG_ONEPLUS;
    sSystemReady = false;
    sFPNotResumeList = new HashSet();
    mDisplayLongLength = -1;
    mDisplayShortLength = -1;
    mIsHomeApp = false;
    mIsSystemUI = false;
    mIsScreenCompat = false;
    sFPNotResumeList.add("com.qiyi.video");
    sFPNotResumeList.add("com.tencent.qqlive");
    sFPNotResumeList.add("com.sina.weibo");
    sFPNotResumeList.add("com.netease.cloudmusic");
    sFPNotResumeList.add("com.sina.weibo");
    sFPNotResumeList.add("com.youku.phone");
    sFPNotResumeList.add("com.tudou.android");
    sFPNotResumeList.add("com.letv.android.client");
    sFPNotResumeList.add("com.sohu.sohuvideo");
    sFPNotResumeList.add("com.baidu.video");
    sFPNotResumeList.add("com.ifeng.newvideo");
    sFPNotResumeList.add("com.zhongduomei.rrmj.society");
    sFPNotResumeList.add("com.telecom.video.ikan4g");
    sFPNotResumeList.add("com.Android56");
    sFPNotResumeList.add("com.hunantv.imgo.activity");
    sFPNotResumeList.add("tv.acfundanmaku.video");
    sFPNotResumeList.add("tv.danmaku.bili");
    sFPNotResumeList.add("com.oneplus.camera");
  }
  
  public static boolean bypassClearNotificationEffectPackage(String paramString)
  {
    if (!"com.android.server.telecom".equals(paramString)) {
      return "com.android.dialer".equals(paramString);
    }
    return true;
  }
  
  public static boolean checkSystemDialogPermission(Context paramContext)
  {
    if (paramContext == null) {
      return false;
    }
    boolean bool = Settings.canDrawOverlays(paramContext);
    if (!bool) {
      paramContext.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + paramContext.getPackageName())));
    }
    return bool;
  }
  
  public static String getDeviceTag()
  {
    if (!sDeviceTag.equals("")) {
      return sDeviceTag;
    }
    try
    {
      String str = Build.DEVICE;
      if (str.equals("ONEPLUS")) {
        return "14001";
      }
      if (str.equals("OnePlus2")) {
        return "14049";
      }
      if ((!str.equals("SUM")) && (!str.equals("OnePlus")))
      {
        if (str.equals("OnePlus3")) {
          return "15801";
        }
        if (str.equals("OnePlus3T")) {
          return "15811";
        }
        if ((str.equals("P6859")) || (str.equals("OnePlus5"))) {
          break label168;
        }
        if ((str.equals("P7801")) || (str.equals("OnePlus5T"))) {
          break label171;
        }
        return "15801";
      }
    }
    catch (Exception localException)
    {
      Log.w("Utils", "Exception e = " + localException.toString());
      return "";
    }
    return "15055";
    label168:
    return "16859";
    label171:
    return "17801";
  }
  
  public static int getIntField(Object paramObject, String paramString1, String paramString2, int paramInt)
  {
    try
    {
      Object localObject = Class.forName(paramString1);
      if (localObject != null)
      {
        localObject = ((Class)localObject).getDeclaredField(paramString2);
        if (localObject != null)
        {
          ((Field)localObject).setAccessible(true);
          return ((Integer)((Field)localObject).get(paramObject)).intValue();
        }
        Log.e("Utils", paramString1 + ":" + paramString2 + " in setIntField not found");
      }
      return paramInt;
    }
    catch (Exception paramObject)
    {
      Log.e("Utils", "getIntField function Exception:", (Throwable)paramObject);
    }
    return paramInt;
  }
  
  private static int getThemeAccentColor(Context paramContext, int paramInt, String paramString)
  {
    paramString = Settings.System.getString(paramContext.getContentResolver(), paramString);
    if (paramString == null) {
      return paramContext.getResources().getColor(paramInt);
    }
    return Color.parseColor(paramString);
  }
  
  public static int getThemeBlackAccentColor(Context paramContext, int paramInt)
  {
    return getThemeAccentColor(paramContext, paramInt, "oem_black_mode_accent_color");
  }
  
  public static int getThemeColor(Context paramContext)
  {
    return Settings.System.getInt(paramContext.getContentResolver(), "oem_black_mode", 2);
  }
  
  public static int getThemeWhiteAccentColor(Context paramContext, int paramInt)
  {
    return getThemeAccentColor(paramContext, paramInt, "oem_white_mode_accent_color");
  }
  
  public static boolean hasCtaFeature(Context paramContext)
  {
    return paramContext.getPackageManager().hasSystemFeature("oem.ctaSwitch.support");
  }
  
  public static boolean is17801Device()
  {
    return "17801".equals(SystemProperties.get("ro.boot.project_name"));
  }
  
  public static boolean is8998Device()
  {
    if (is17801Device()) {
      return true;
    }
    return ("P6859".equals(Build.DEVICE)) || ("OnePlus5".equals(Build.DEVICE));
  }
  
  public static boolean isBackKeyRight(Context paramContext)
  {
    return ((isGlobalROM(paramContext)) && (isKeySwapped(paramContext))) || ((!isGlobalROM(paramContext)) && (!isKeySwapped(paramContext)));
  }
  
  public static boolean isCurrentGuest(Context paramContext)
  {
    paramContext = ((UserManager)paramContext.getSystemService("user")).getUserInfo(ActivityManager.getCurrentUser());
    if (paramContext == null) {
      return false;
    }
    return paramContext.isGuest();
  }
  
  public static boolean isDeepCleanEnable(Context paramContext)
  {
    return Settings.System.getInt(paramContext.getContentResolver(), "oem_clear_way", 0) == 1;
  }
  
  public static boolean isGlobalROM(Context paramContext)
  {
    return OpFeatures.isSupport(new int[] { 1 });
  }
  
  public static boolean isHomeApp()
  {
    return mIsHomeApp;
  }
  
  public static boolean isInFPNotResumeList(String paramString)
  {
    return sFPNotResumeList.contains(paramString);
  }
  
  public static boolean isKeySwapped(Context paramContext)
  {
    try
    {
      int i = Settings.System.getInt(paramContext.getContentResolver(), "oem_acc_key_define");
      return i == 1;
    }
    catch (Settings.SettingNotFoundException paramContext) {}
    return true;
  }
  
  public static boolean isParallelApp(int paramInt)
  {
    UserHandle localUserHandle = UserHandle.CURRENT;
    return UserHandle.getUserId(paramInt) == 999;
  }
  
  public static boolean isPreventModeEnalbed(Context paramContext)
  {
    boolean bool = false;
    if (KeyguardUpdateMonitor.getCurrentUser() != 0) {
      return false;
    }
    try
    {
      int i = Settings.System.getInt(paramContext.getContentResolver(), "oem_acc_anti_misoperation_screen");
      if (i != 0) {
        bool = true;
      }
      return bool;
    }
    catch (Settings.SettingNotFoundException paramContext) {}
    return false;
  }
  
  public static boolean isProximityDozeEnable(Context paramContext)
  {
    boolean bool = false;
    if (Settings.System.getIntForUser(paramContext.getContentResolver(), "prox_wake_enabled", 0, ActivityManager.getCurrentUser()) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isScreenCompat()
  {
    return mIsScreenCompat;
  }
  
  public static boolean isSpecialTheme(Context paramContext)
  {
    return Settings.System.getInt(paramContext.getContentResolver(), "oem_special_theme", 0) == 1;
  }
  
  public static boolean isSupportHideNavBar()
  {
    return "17801".equals(SystemProperties.get("ro.boot.project_name"));
  }
  
  public static boolean isSystemReady()
  {
    return sSystemReady;
  }
  
  public static boolean isSystemUI()
  {
    return mIsSystemUI;
  }
  
  public static void setSystemReady()
  {
    sSystemReady = true;
  }
  
  public static boolean showHeadsUpInGameModePkg(String paramString)
  {
    if (!"com.android.incallui".equals(paramString)) {
      return "com.oneplus.deskclock".equals(paramString);
    }
    return true;
  }
  
  /* Error */
  public static void updateTopPackage(Context paramContext)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_2
    //   2: aload_0
    //   3: ldc_w 399
    //   6: invokevirtual 317	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   9: checkcast 321	android/app/ActivityManager
    //   12: astore_3
    //   13: aload_3
    //   14: iconst_1
    //   15: invokevirtual 403	android/app/ActivityManager:getRunningTasks	(I)Ljava/util/List;
    //   18: iconst_0
    //   19: invokeinterface 408 2 0
    //   24: checkcast 410	android/app/ActivityManager$RunningTaskInfo
    //   27: getfield 414	android/app/ActivityManager$RunningTaskInfo:topActivity	Landroid/content/ComponentName;
    //   30: invokevirtual 417	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   33: astore_3
    //   34: new 110	android/content/Intent
    //   37: dup
    //   38: ldc_w 419
    //   41: invokespecial 422	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   44: astore 4
    //   46: aload 4
    //   48: ldc_w 424
    //   51: invokevirtual 428	android/content/Intent:addCategory	(Ljava/lang/String;)Landroid/content/Intent;
    //   54: pop
    //   55: aload_0
    //   56: invokevirtual 284	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   59: aload 4
    //   61: ldc_w 429
    //   64: invokevirtual 433	android/content/pm/PackageManager:resolveActivity	(Landroid/content/Intent;I)Landroid/content/pm/ResolveInfo;
    //   67: getfield 439	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   70: getfield 444	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   73: astore 4
    //   75: aload 4
    //   77: ifnull +113 -> 190
    //   80: aload_3
    //   81: ifnull +109 -> 190
    //   84: aload_3
    //   85: ldc_w 446
    //   88: invokevirtual 99	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   91: ifne +94 -> 185
    //   94: aload_3
    //   95: ldc_w 448
    //   98: invokevirtual 99	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   101: ifne +84 -> 185
    //   104: aload_3
    //   105: aload 4
    //   107: invokevirtual 99	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   110: istore_1
    //   111: iload_1
    //   112: putstatic 44	com/android/systemui/util/Utils:mIsHomeApp	Z
    //   115: aload_3
    //   116: ifnull +81 -> 197
    //   119: aload_3
    //   120: ldc_w 450
    //   123: invokevirtual 99	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   126: putstatic 46	com/android/systemui/util/Utils:mIsSystemUI	Z
    //   129: aload_0
    //   130: ldc_w 452
    //   133: invokevirtual 317	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   136: checkcast 454	android/app/AppOpsManager
    //   139: astore 4
    //   141: aload_3
    //   142: ifnull +67 -> 209
    //   145: aload 4
    //   147: bipush 70
    //   149: aload_0
    //   150: invokevirtual 284	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   153: aload_3
    //   154: iconst_1
    //   155: invokevirtual 458	android/content/pm/PackageManager:getApplicationInfo	(Ljava/lang/String;I)Landroid/content/pm/ApplicationInfo;
    //   158: getfield 463	android/content/pm/ApplicationInfo:uid	I
    //   161: aload_3
    //   162: invokevirtual 467	android/app/AppOpsManager:checkOpNoThrow	(IILjava/lang/String;)I
    //   165: ifne +39 -> 204
    //   168: iload_2
    //   169: istore_1
    //   170: iload_1
    //   171: putstatic 48	com/android/systemui/util/Utils:mIsScreenCompat	Z
    //   174: return
    //   175: astore_3
    //   176: aload_3
    //   177: invokevirtual 470	java/lang/IndexOutOfBoundsException:printStackTrace	()V
    //   180: aconst_null
    //   181: astore_3
    //   182: goto -148 -> 34
    //   185: iconst_1
    //   186: istore_1
    //   187: goto -76 -> 111
    //   190: iconst_0
    //   191: putstatic 44	com/android/systemui/util/Utils:mIsHomeApp	Z
    //   194: goto -79 -> 115
    //   197: iconst_0
    //   198: putstatic 46	com/android/systemui/util/Utils:mIsSystemUI	Z
    //   201: goto -72 -> 129
    //   204: iconst_0
    //   205: istore_1
    //   206: goto -36 -> 170
    //   209: iconst_0
    //   210: putstatic 48	com/android/systemui/util/Utils:mIsScreenCompat	Z
    //   213: return
    //   214: astore_0
    //   215: aload_0
    //   216: invokevirtual 471	android/content/pm/PackageManager$NameNotFoundException:printStackTrace	()V
    //   219: iconst_0
    //   220: putstatic 48	com/android/systemui/util/Utils:mIsScreenCompat	Z
    //   223: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	224	0	paramContext	Context
    //   110	96	1	bool1	boolean
    //   1	168	2	bool2	boolean
    //   12	150	3	localObject1	Object
    //   175	2	3	localIndexOutOfBoundsException	IndexOutOfBoundsException
    //   181	1	3	localObject2	Object
    //   44	102	4	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   13	34	175	java/lang/IndexOutOfBoundsException
    //   145	168	214	android/content/pm/PackageManager$NameNotFoundException
    //   170	174	214	android/content/pm/PackageManager$NameNotFoundException
    //   209	213	214	android/content/pm/PackageManager$NameNotFoundException
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\util\Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */