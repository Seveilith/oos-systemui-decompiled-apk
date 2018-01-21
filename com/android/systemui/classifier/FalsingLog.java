package com.android.systemui.classifier;

import android.os.Build;
import android.os.SystemProperties;
import android.util.Log;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class FalsingLog
{
  public static final boolean ENABLED = SystemProperties.getBoolean("debug.falsing_log", Build.IS_DEBUGGABLE);
  private static final boolean LOGCAT = SystemProperties.getBoolean("debug.falsing_logcat", false);
  private static final int MAX_SIZE = SystemProperties.getInt("debug.falsing_log_size", 100);
  private static FalsingLog sInstance;
  private final SimpleDateFormat mFormat = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.US);
  private final ArrayDeque<String> mLog = new ArrayDeque(MAX_SIZE);
  
  public static void dump(PrintWriter paramPrintWriter)
  {
    try
    {
      paramPrintWriter.println("FALSING LOG:");
      if (!ENABLED)
      {
        paramPrintWriter.println("Disabled, to enable: setprop debug.falsing_log 1");
        paramPrintWriter.println();
        return;
      }
      if ((sInstance == null) || (sInstance.mLog.isEmpty()))
      {
        paramPrintWriter.println("<empty>");
        paramPrintWriter.println();
        return;
      }
      Iterator localIterator = sInstance.mLog.iterator();
      while (localIterator.hasNext()) {
        paramPrintWriter.println((String)localIterator.next());
      }
      paramPrintWriter.println();
    }
    finally {}
  }
  
  public static void e(String paramString1, String paramString2)
  {
    if (LOGCAT) {
      Log.e("FalsingLog", paramString1 + "\t" + paramString2);
    }
    log("E", paramString1, paramString2);
  }
  
  public static void i(String paramString1, String paramString2)
  {
    if (LOGCAT) {
      Log.i("FalsingLog", paramString1 + "\t" + paramString2);
    }
    log("I", paramString1, paramString2);
  }
  
  public static void log(String paramString1, String paramString2, String paramString3)
  {
    try
    {
      boolean bool = ENABLED;
      if (!bool) {
        return;
      }
      if (sInstance == null) {
        sInstance = new FalsingLog();
      }
      if (sInstance.mLog.size() >= MAX_SIZE) {
        sInstance.mLog.removeFirst();
      }
      paramString1 = sInstance.mFormat.format(new Date()) + " " + paramString1 + " " + paramString2 + " " + paramString3;
      sInstance.mLog.add(paramString1);
      return;
    }
    finally {}
  }
  
  /* Error */
  public static void wtf(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 33	com/android/systemui/classifier/FalsingLog:ENABLED	Z
    //   6: istore_2
    //   7: iload_2
    //   8: ifne +7 -> 15
    //   11: ldc 2
    //   13: monitorexit
    //   14: return
    //   15: aload_0
    //   16: aload_1
    //   17: invokestatic 168	com/android/systemui/classifier/FalsingLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   20: invokestatic 174	android/app/ActivityThread:currentApplication	()Landroid/app/Application;
    //   23: astore_3
    //   24: ldc -80
    //   26: astore 6
    //   28: getstatic 25	android/os/Build:IS_DEBUGGABLE	Z
    //   31: ifeq +228 -> 259
    //   34: aload_3
    //   35: ifnull +224 -> 259
    //   38: new 178	java/io/File
    //   41: dup
    //   42: aload_3
    //   43: invokevirtual 184	android/app/Application:getDataDir	()Ljava/io/File;
    //   46: new 116	java/lang/StringBuilder
    //   49: dup
    //   50: invokespecial 117	java/lang/StringBuilder:<init>	()V
    //   53: ldc -70
    //   55: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   58: new 58	java/text/SimpleDateFormat
    //   61: dup
    //   62: ldc -68
    //   64: invokespecial 190	java/text/SimpleDateFormat:<init>	(Ljava/lang/String;)V
    //   67: new 152	java/util/Date
    //   70: dup
    //   71: invokespecial 153	java/util/Date:<init>	()V
    //   74: invokevirtual 157	java/text/SimpleDateFormat:format	(Ljava/util/Date;)Ljava/lang/String;
    //   77: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   80: ldc -64
    //   82: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   85: invokevirtual 127	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   88: invokespecial 195	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   91: astore 7
    //   93: aconst_null
    //   94: astore_3
    //   95: aconst_null
    //   96: astore 5
    //   98: new 77	java/io/PrintWriter
    //   101: dup
    //   102: aload 7
    //   104: invokespecial 198	java/io/PrintWriter:<init>	(Ljava/io/File;)V
    //   107: astore 4
    //   109: aload 4
    //   111: invokestatic 200	com/android/systemui/classifier/FalsingLog:dump	(Ljava/io/PrintWriter;)V
    //   114: aload 4
    //   116: invokevirtual 203	java/io/PrintWriter:close	()V
    //   119: new 116	java/lang/StringBuilder
    //   122: dup
    //   123: invokespecial 117	java/lang/StringBuilder:<init>	()V
    //   126: ldc -51
    //   128: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   131: aload 7
    //   133: invokevirtual 208	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   136: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   139: invokevirtual 127	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   142: astore 5
    //   144: aload 5
    //   146: astore_3
    //   147: aload 4
    //   149: ifnull +11 -> 160
    //   152: aload 4
    //   154: invokevirtual 203	java/io/PrintWriter:close	()V
    //   157: aload 5
    //   159: astore_3
    //   160: ldc 114
    //   162: new 116	java/lang/StringBuilder
    //   165: dup
    //   166: invokespecial 117	java/lang/StringBuilder:<init>	()V
    //   169: aload_0
    //   170: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   173: ldc -97
    //   175: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   178: aload_1
    //   179: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   182: ldc -46
    //   184: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   187: aload_3
    //   188: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   191: invokevirtual 127	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   194: invokestatic 212	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/String;)I
    //   197: pop
    //   198: ldc 2
    //   200: monitorexit
    //   201: return
    //   202: astore_3
    //   203: aload 5
    //   205: astore 4
    //   207: aload_3
    //   208: astore 5
    //   210: aload 4
    //   212: astore_3
    //   213: ldc 114
    //   215: ldc -42
    //   217: aload 5
    //   219: invokestatic 217	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   222: pop
    //   223: aload 6
    //   225: astore_3
    //   226: aload 4
    //   228: ifnull -68 -> 160
    //   231: aload 4
    //   233: invokevirtual 203	java/io/PrintWriter:close	()V
    //   236: aload 6
    //   238: astore_3
    //   239: goto -79 -> 160
    //   242: astore_0
    //   243: ldc 2
    //   245: monitorexit
    //   246: aload_0
    //   247: athrow
    //   248: astore_0
    //   249: aload_3
    //   250: ifnull +7 -> 257
    //   253: aload_3
    //   254: invokevirtual 203	java/io/PrintWriter:close	()V
    //   257: aload_0
    //   258: athrow
    //   259: ldc 114
    //   261: ldc -37
    //   263: invokestatic 132	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   266: pop
    //   267: aload 6
    //   269: astore_3
    //   270: goto -110 -> 160
    //   273: astore_0
    //   274: aload 4
    //   276: astore_3
    //   277: goto -28 -> 249
    //   280: astore 5
    //   282: goto -72 -> 210
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	285	0	paramString1	String
    //   0	285	1	paramString2	String
    //   6	2	2	bool	boolean
    //   23	165	3	localObject1	Object
    //   202	6	3	localIOException1	java.io.IOException
    //   212	65	3	localObject2	Object
    //   107	168	4	localObject3	Object
    //   96	122	5	localObject4	Object
    //   280	1	5	localIOException2	java.io.IOException
    //   26	242	6	str	String
    //   91	41	7	localFile	java.io.File
    // Exception table:
    //   from	to	target	type
    //   98	109	202	java/io/IOException
    //   3	7	242	finally
    //   15	24	242	finally
    //   28	34	242	finally
    //   38	93	242	finally
    //   152	157	242	finally
    //   160	198	242	finally
    //   231	236	242	finally
    //   253	257	242	finally
    //   257	259	242	finally
    //   259	267	242	finally
    //   98	109	248	finally
    //   213	223	248	finally
    //   109	144	273	finally
    //   109	144	280	java/io/IOException
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\classifier\FalsingLog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */