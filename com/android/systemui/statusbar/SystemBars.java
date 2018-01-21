package com.android.systemui.statusbar;

import android.content.res.Configuration;
import android.util.Log;
import com.android.systemui.SystemUI;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class SystemBars
  extends SystemUI
  implements ServiceMonitor.Callbacks
{
  private ServiceMonitor mServiceMonitor;
  private BaseStatusBar mStatusBar;
  
  private RuntimeException andLog(String paramString, Throwable paramThrowable)
  {
    Log.w("SystemBars", paramString, paramThrowable);
    throw new RuntimeException(paramString, paramThrowable);
  }
  
  /* Error */
  private void createStatusBarFromConfig()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 37	com/android/systemui/statusbar/SystemBars:mContext	Landroid/content/Context;
    //   4: ldc 38
    //   6: invokevirtual 44	android/content/Context:getString	(I)Ljava/lang/String;
    //   9: astore_1
    //   10: aload_1
    //   11: ifnull +10 -> 21
    //   14: aload_1
    //   15: invokevirtual 50	java/lang/String:length	()I
    //   18: ifne +11 -> 29
    //   21: aload_0
    //   22: ldc 52
    //   24: aconst_null
    //   25: invokespecial 54	com/android/systemui/statusbar/SystemBars:andLog	(Ljava/lang/String;Ljava/lang/Throwable;)Ljava/lang/RuntimeException;
    //   28: athrow
    //   29: aload_0
    //   30: getfield 37	com/android/systemui/statusbar/SystemBars:mContext	Landroid/content/Context;
    //   33: invokevirtual 58	android/content/Context:getClassLoader	()Ljava/lang/ClassLoader;
    //   36: aload_1
    //   37: invokevirtual 64	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   40: astore_2
    //   41: aload_0
    //   42: aload_2
    //   43: invokevirtual 70	java/lang/Class:newInstance	()Ljava/lang/Object;
    //   46: checkcast 72	com/android/systemui/statusbar/BaseStatusBar
    //   49: putfield 74	com/android/systemui/statusbar/SystemBars:mStatusBar	Lcom/android/systemui/statusbar/BaseStatusBar;
    //   52: aload_0
    //   53: getfield 74	com/android/systemui/statusbar/SystemBars:mStatusBar	Lcom/android/systemui/statusbar/BaseStatusBar;
    //   56: aload_0
    //   57: getfield 37	com/android/systemui/statusbar/SystemBars:mContext	Landroid/content/Context;
    //   60: putfield 75	com/android/systemui/statusbar/BaseStatusBar:mContext	Landroid/content/Context;
    //   63: aload_0
    //   64: getfield 74	com/android/systemui/statusbar/SystemBars:mStatusBar	Lcom/android/systemui/statusbar/BaseStatusBar;
    //   67: aload_0
    //   68: getfield 79	com/android/systemui/statusbar/SystemBars:mComponents	Ljava/util/Map;
    //   71: putfield 80	com/android/systemui/statusbar/BaseStatusBar:mComponents	Ljava/util/Map;
    //   74: aload_0
    //   75: getfield 74	com/android/systemui/statusbar/SystemBars:mStatusBar	Lcom/android/systemui/statusbar/BaseStatusBar;
    //   78: invokevirtual 83	com/android/systemui/statusbar/BaseStatusBar:start	()V
    //   81: return
    //   82: astore_2
    //   83: aload_0
    //   84: new 85	java/lang/StringBuilder
    //   87: dup
    //   88: invokespecial 86	java/lang/StringBuilder:<init>	()V
    //   91: ldc 88
    //   93: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   96: aload_1
    //   97: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   100: invokevirtual 96	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   103: aload_2
    //   104: invokespecial 54	com/android/systemui/statusbar/SystemBars:andLog	(Ljava/lang/String;Ljava/lang/Throwable;)Ljava/lang/RuntimeException;
    //   107: athrow
    //   108: astore_2
    //   109: aload_0
    //   110: new 85	java/lang/StringBuilder
    //   113: dup
    //   114: invokespecial 86	java/lang/StringBuilder:<init>	()V
    //   117: ldc 98
    //   119: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   122: aload_1
    //   123: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   126: invokevirtual 96	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   129: aload_2
    //   130: invokespecial 54	com/android/systemui/statusbar/SystemBars:andLog	(Ljava/lang/String;Ljava/lang/Throwable;)Ljava/lang/RuntimeException;
    //   133: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	134	0	this	SystemBars
    //   9	114	1	str	String
    //   40	3	2	localClass	Class
    //   82	22	2	localThrowable1	Throwable
    //   108	22	2	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   29	41	82	java/lang/Throwable
    //   41	52	108	java/lang/Throwable
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mStatusBar != null) {
      this.mStatusBar.dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    if (this.mStatusBar != null) {
      this.mStatusBar.onConfigurationChanged(paramConfiguration);
    }
  }
  
  public void onNoService()
  {
    createStatusBarFromConfig();
  }
  
  public long onServiceStartAttempt()
  {
    if (this.mStatusBar != null)
    {
      this.mStatusBar.destroy();
      this.mStatusBar = null;
      return 500L;
    }
    return 0L;
  }
  
  public void start()
  {
    this.mServiceMonitor = new ServiceMonitor("SystemBars", false, this.mContext, "bar_service_component", this);
    this.mServiceMonitor.start();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\SystemBars.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */