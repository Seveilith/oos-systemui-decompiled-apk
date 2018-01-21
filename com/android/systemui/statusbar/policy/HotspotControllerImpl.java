package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.OnStartTetheringCallback;
import android.os.UserManager;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class HotspotControllerImpl
  implements HotspotController
{
  private static final boolean DEBUG = Log.isLoggable("HotspotController", 3);
  private final ArrayList<HotspotController.Callback> mCallbacks = new ArrayList();
  private final ConnectivityManager mConnectivityManager;
  private final Context mContext;
  private int mHotspotState;
  private final Receiver mReceiver = new Receiver(null);
  
  public HotspotControllerImpl(Context paramContext)
  {
    this.mContext = paramContext;
    this.mConnectivityManager = ((ConnectivityManager)paramContext.getSystemService("connectivity"));
  }
  
  private void fireCallback(boolean paramBoolean)
  {
    synchronized (this.mCallbacks)
    {
      Iterator localIterator = this.mCallbacks.iterator();
      if (localIterator.hasNext()) {
        ((HotspotController.Callback)localIterator.next()).onHotspotChanged(paramBoolean);
      }
    }
  }
  
  private static String stateToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 11: 
      return "DISABLED";
    case 10: 
      return "DISABLING";
    case 13: 
      return "ENABLED";
    case 12: 
      return "ENABLING";
    }
    return "FAILED";
  }
  
  /* Error */
  public void addCallback(HotspotController.Callback paramCallback)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 65	com/android/systemui/statusbar/policy/HotspotControllerImpl:mCallbacks	Ljava/util/ArrayList;
    //   4: astore_3
    //   5: aload_3
    //   6: monitorenter
    //   7: aload_1
    //   8: ifnull +16 -> 24
    //   11: aload_0
    //   12: getfield 65	com/android/systemui/statusbar/policy/HotspotControllerImpl:mCallbacks	Ljava/util/ArrayList;
    //   15: aload_1
    //   16: invokevirtual 120	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
    //   19: istore_2
    //   20: iload_2
    //   21: ifeq +6 -> 27
    //   24: aload_3
    //   25: monitorexit
    //   26: return
    //   27: getstatic 29	com/android/systemui/statusbar/policy/HotspotControllerImpl:DEBUG	Z
    //   30: ifeq +28 -> 58
    //   33: ldc 50
    //   35: new 122	java/lang/StringBuilder
    //   38: dup
    //   39: invokespecial 123	java/lang/StringBuilder:<init>	()V
    //   42: ldc 125
    //   44: invokevirtual 129	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   47: aload_1
    //   48: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   51: invokevirtual 136	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   54: invokestatic 140	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   57: pop
    //   58: aload_0
    //   59: getfield 65	com/android/systemui/statusbar/policy/HotspotControllerImpl:mCallbacks	Ljava/util/ArrayList;
    //   62: aload_1
    //   63: invokevirtual 143	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   66: pop
    //   67: aload_0
    //   68: getfield 70	com/android/systemui/statusbar/policy/HotspotControllerImpl:mReceiver	Lcom/android/systemui/statusbar/policy/HotspotControllerImpl$Receiver;
    //   71: astore_1
    //   72: aload_0
    //   73: getfield 65	com/android/systemui/statusbar/policy/HotspotControllerImpl:mCallbacks	Ljava/util/ArrayList;
    //   76: invokevirtual 146	java/util/ArrayList:isEmpty	()Z
    //   79: ifeq +13 -> 92
    //   82: iconst_0
    //   83: istore_2
    //   84: aload_1
    //   85: iload_2
    //   86: invokevirtual 149	com/android/systemui/statusbar/policy/HotspotControllerImpl$Receiver:setListening	(Z)V
    //   89: aload_3
    //   90: monitorexit
    //   91: return
    //   92: iconst_1
    //   93: istore_2
    //   94: goto -10 -> 84
    //   97: astore_1
    //   98: aload_3
    //   99: monitorexit
    //   100: aload_1
    //   101: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	102	0	this	HotspotControllerImpl
    //   0	102	1	paramCallback	HotspotController.Callback
    //   19	75	2	bool	boolean
    //   4	95	3	localArrayList	ArrayList
    // Exception table:
    //   from	to	target	type
    //   11	20	97	finally
    //   27	58	97	finally
    //   58	82	97	finally
    //   84	89	97	finally
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("HotspotController state:");
    paramPrintWriter.print("  mHotspotEnabled=");
    paramPrintWriter.println(stateToString(this.mHotspotState));
  }
  
  public boolean isHotspotEnabled()
  {
    return this.mHotspotState == 13;
  }
  
  public boolean isHotspotSupported()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mConnectivityManager.isTetheringSupported())
    {
      bool1 = bool2;
      if (this.mConnectivityManager.getTetherableWifiRegexs().length != 0) {
        bool1 = UserManager.get(this.mContext).isUserAdmin(ActivityManager.getCurrentUser());
      }
    }
    return bool1;
  }
  
  public void removeCallback(HotspotController.Callback paramCallback)
  {
    if (paramCallback == null) {
      return;
    }
    if (DEBUG) {
      Log.d("HotspotController", "removeCallback " + paramCallback);
    }
    synchronized (this.mCallbacks)
    {
      this.mCallbacks.remove(paramCallback);
      paramCallback = this.mReceiver;
      if (this.mCallbacks.isEmpty())
      {
        bool = false;
        paramCallback.setListening(bool);
        return;
      }
      boolean bool = true;
    }
  }
  
  public void setHotspotEnabled(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      OnStartTetheringCallback localOnStartTetheringCallback = new OnStartTetheringCallback();
      this.mConnectivityManager.startTethering(0, false, localOnStartTetheringCallback);
      return;
    }
    this.mConnectivityManager.stopTethering(0);
  }
  
  static final class OnStartTetheringCallback
    extends ConnectivityManager.OnStartTetheringCallback
  {
    public void onTetheringFailed() {}
    
    public void onTetheringStarted() {}
  }
  
  private final class Receiver
    extends BroadcastReceiver
  {
    private boolean mRegistered;
    
    private Receiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (HotspotControllerImpl.-get0()) {
        Log.d("HotspotController", "onReceive " + paramIntent.getAction());
      }
      int i = paramIntent.getIntExtra("wifi_state", 14);
      HotspotControllerImpl.-set0(HotspotControllerImpl.this, i);
      paramContext = HotspotControllerImpl.this;
      if (HotspotControllerImpl.-get2(HotspotControllerImpl.this) == 13) {}
      for (boolean bool = true;; bool = false)
      {
        HotspotControllerImpl.-wrap0(paramContext, bool);
        return;
      }
    }
    
    public void setListening(boolean paramBoolean)
    {
      if ((!paramBoolean) || (this.mRegistered))
      {
        if ((!paramBoolean) && (this.mRegistered))
        {
          if (HotspotControllerImpl.-get0()) {
            Log.d("HotspotController", "Unregistering receiver");
          }
          HotspotControllerImpl.-get1(HotspotControllerImpl.this).unregisterReceiver(this);
          this.mRegistered = false;
        }
        return;
      }
      if (HotspotControllerImpl.-get0()) {
        Log.d("HotspotController", "Registering receiver");
      }
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
      HotspotControllerImpl.-get1(HotspotControllerImpl.this).registerReceiver(this, localIntentFilter);
      this.mRegistered = true;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\HotspotControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */