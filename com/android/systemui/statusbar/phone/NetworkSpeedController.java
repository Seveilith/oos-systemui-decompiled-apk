package com.android.systemui.statusbar.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.INetworkStatsService;
import android.net.INetworkStatsService.Stub;
import android.net.NetworkInfo;
import android.net.NetworkStats;
import android.net.NetworkStats.Entry;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.os.BackgroundThread;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Timer;

public class NetworkSpeedController
  extends BroadcastReceiver
  implements TunerService.Tunable
{
  private static boolean DEBUG;
  private static int ERTRY_POINT;
  private static int HANDRED;
  private static String TAG = "NetworkSpeedController";
  private static int TEN;
  private static int THOUSAND;
  private static String UNIT_GB = "G";
  private static String UNIT_KB;
  private static String UNIT_MB;
  private static int UPDATE_INTERVAL = 3;
  private int MSG_MAYBE_STOP_NETWORTSPEED = 1001;
  private int MSG_UPDATE_NETWORTSPEED = 1000;
  private int MSG_UPDATE_SHOW = 1002;
  private int MSG_UPDATE_SPEED_ON_BG = 2001;
  private long[] autoTestMatrx = { HANDRED, THOUSAND - 1, THOUSAND, ERTRY_POINT, ERTRY_POINT + 1, ERTRY_POINT * TEN - 1, ERTRY_POINT * TEN, ERTRY_POINT * TEN + 1, ERTRY_POINT * HANDRED - 1, ERTRY_POINT * HANDRED, ERTRY_POINT * HANDRED + 1, ERTRY_POINT * THOUSAND - 1, ERTRY_POINT * THOUSAND, ERTRY_POINT * ERTRY_POINT - 1, ERTRY_POINT * ERTRY_POINT, ERTRY_POINT * ERTRY_POINT + 1, ERTRY_POINT * ERTRY_POINT * TEN - 1, ERTRY_POINT * ERTRY_POINT * TEN, ERTRY_POINT * ERTRY_POINT * TEN + 1, ERTRY_POINT * ERTRY_POINT * HANDRED - 1, ERTRY_POINT * ERTRY_POINT * HANDRED, ERTRY_POINT * ERTRY_POINT * HANDRED + 1, ERTRY_POINT * ERTRY_POINT * THOUSAND - 1, ERTRY_POINT * ERTRY_POINT * THOUSAND, ERTRY_POINT * ERTRY_POINT * THOUSAND + 1, ERTRY_POINT * ERTRY_POINT * ERTRY_POINT - 1, ERTRY_POINT * ERTRY_POINT * ERTRY_POINT, ERTRY_POINT * ERTRY_POINT * ERTRY_POINT + 1 };
  private MyBackgroundHandler mBackgroundHandler = new MyBackgroundHandler(BackgroundThread.getHandler().getLooper());
  private boolean mBlockNetworkSpeed = true;
  private Context mContext;
  private MyHandler mHandler = new MyHandler(Looper.getMainLooper());
  private boolean mHotSpotEnable = false;
  private boolean mIsFirstLoad = true;
  private boolean mNetworkShowState = false;
  private final ArrayList<INetworkSpeedStateCallBack> mNetworkSpeedStateCallBack = new ArrayList();
  private String mSpeed = "";
  private MySpeedMachine mSpeedMachine = new MySpeedMachine();
  private INetworkStatsService mStatsService;
  private Timer mTimer;
  
  static
  {
    DEBUG = Build.DEBUG_ONEPLUS;
    TEN = 10;
    HANDRED = 100;
    THOUSAND = 1000;
    ERTRY_POINT = 1024;
    UNIT_KB = "K";
    UNIT_MB = "M";
  }
  
  public NetworkSpeedController(Context paramContext)
  {
    this.mContext = paramContext;
    this.mTimer = new Timer();
    paramContext = new IntentFilter();
    paramContext.addAction("android.intent.action.TIME_SET");
    paramContext.addAction("android.intent.action.TIMEZONE_CHANGED");
    paramContext.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
    this.mContext.registerReceiver(this, paramContext);
    TunerService.get(this.mContext).addTunable(this, new String[] { "icon_blacklist" });
    this.mStatsService = INetworkStatsService.Stub.asInterface(ServiceManager.getService("netstats"));
  }
  
  private String divToFractionDigits(long paramLong1, long paramLong2, int paramInt)
  {
    if (paramLong2 == 0L)
    {
      Log.i(TAG, "divisor shouldn't be 0");
      return "Error";
    }
    StringBuffer localStringBuffer = new StringBuffer();
    long l = paramLong1 / paramLong2;
    paramLong1 %= paramLong2;
    localStringBuffer.append(l);
    if (paramInt > 0)
    {
      localStringBuffer.append(".");
      int i = 0;
      while (i < paramInt)
      {
        paramLong1 *= 10L;
        l = paramLong1 / paramLong2;
        paramLong1 %= paramLong2;
        localStringBuffer.append(l);
        i += 1;
      }
    }
    return localStringBuffer.toString();
  }
  
  private String formateSpeed(long paramLong)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    Object localObject = UNIT_KB;
    long l1 = ERTRY_POINT;
    int j = 0;
    int i;
    if (paramLong < ERTRY_POINT)
    {
      l1 = ERTRY_POINT;
      i = 2;
      localObject = UNIT_KB;
    }
    for (;;)
    {
      localStringBuffer.append(divToFractionDigits(paramLong, l1, i)).append(" ").append((String)localObject).append("/s ");
      return localStringBuffer.toString();
      long l2;
      String str;
      if ((paramLong >= ERTRY_POINT) && (paramLong < ERTRY_POINT * THOUSAND))
      {
        l2 = ERTRY_POINT;
        str = UNIT_KB;
        if ((paramLong >= ERTRY_POINT) && (paramLong < ERTRY_POINT * TEN))
        {
          i = 2;
          l1 = l2;
          localObject = str;
        }
        else if ((paramLong >= ERTRY_POINT * TEN) && (paramLong < ERTRY_POINT * HANDRED))
        {
          i = 1;
          l1 = l2;
          localObject = str;
        }
        else
        {
          l1 = l2;
          i = j;
          localObject = str;
          if (paramLong >= ERTRY_POINT * HANDRED)
          {
            l1 = l2;
            i = j;
            localObject = str;
            if (paramLong < ERTRY_POINT * THOUSAND)
            {
              i = 0;
              l1 = l2;
              localObject = str;
            }
          }
        }
      }
      else if ((paramLong >= ERTRY_POINT * THOUSAND) && (paramLong < ERTRY_POINT * ERTRY_POINT * THOUSAND))
      {
        l2 = ERTRY_POINT * ERTRY_POINT;
        str = UNIT_MB;
        if ((paramLong >= ERTRY_POINT * THOUSAND) && (paramLong < TEN * l2))
        {
          i = 2;
          l1 = l2;
          localObject = str;
        }
        else if ((paramLong >= ERTRY_POINT * ERTRY_POINT * TEN) && (paramLong < HANDRED * l2))
        {
          i = 1;
          l1 = l2;
          localObject = str;
        }
        else
        {
          l1 = l2;
          i = j;
          localObject = str;
          if (paramLong >= ERTRY_POINT * ERTRY_POINT * HANDRED)
          {
            l1 = l2;
            i = j;
            localObject = str;
            if (paramLong < THOUSAND * l2)
            {
              i = 0;
              l1 = l2;
              localObject = str;
            }
          }
        }
      }
      else
      {
        l1 = ERTRY_POINT * ERTRY_POINT * ERTRY_POINT;
        localObject = UNIT_GB;
        if ((paramLong >= ERTRY_POINT * ERTRY_POINT * THOUSAND) && (paramLong < TEN * l1)) {
          i = 2;
        } else if ((paramLong >= ERTRY_POINT * ERTRY_POINT * ERTRY_POINT * TEN) && (paramLong < HANDRED * l1)) {
          i = 1;
        } else {
          i = 0;
        }
      }
    }
  }
  
  private long[] getTetherStats()
  {
    long[] arrayOfLong = new long[2];
    int i;
    int j;
    long l1;
    long l2;
    for (;;)
    {
      try
      {
        localNetworkStats = this.mStatsService.peekTetherStats();
        localEntry = null;
        if (localNetworkStats == null) {
          break label112;
        }
        i = localNetworkStats.size();
      }
      catch (RemoteException localRemoteException)
      {
        NetworkStats localNetworkStats;
        NetworkStats.Entry localEntry;
        Object localObject1;
        Object localObject2;
        Log.w(TAG, "Failed to fetch network stats.");
        return arrayOfLong;
      }
      if (j < i)
      {
        localEntry = localNetworkStats.getValues(j, localEntry);
        localObject1 += localEntry.rxBytes;
        localObject2 += localEntry.txBytes;
        j += 1;
      }
      else
      {
        arrayOfLong[0] = l1;
        arrayOfLong[1] = l2;
        return arrayOfLong;
      }
    }
    for (;;)
    {
      l1 = 0L;
      l2 = 0L;
      j = 0;
      break;
      label112:
      i = 0;
    }
  }
  
  private void onShowStateChange(boolean paramBoolean)
  {
    if (DEBUG) {
      Log.d(TAG, "onShowStateChange s:" + paramBoolean);
    }
    Iterator localIterator = this.mNetworkSpeedStateCallBack.iterator();
    while (localIterator.hasNext()) {
      ((INetworkSpeedStateCallBack)localIterator.next()).onSpeedShow(paramBoolean);
    }
  }
  
  private void onStartTraceSpeed()
  {
    if (DEBUG) {
      Log.d(TAG, "onStartTraceSpeed");
    }
    updateSpeed();
  }
  
  private void onStopTraceSpeed()
  {
    if (DEBUG) {
      Log.d(TAG, "onStopTraceSpeed");
    }
    this.mIsFirstLoad = true;
    stopSpeed();
    this.mSpeed = "";
  }
  
  private void refreshSpeed()
  {
    if (DEBUG) {
      Log.d(TAG, "refreshSpeed sp:" + this.mSpeed);
    }
    Iterator localIterator = this.mNetworkSpeedStateCallBack.iterator();
    while (localIterator.hasNext())
    {
      INetworkSpeedStateCallBack localINetworkSpeedStateCallBack = (INetworkSpeedStateCallBack)localIterator.next();
      if (localINetworkSpeedStateCallBack != null) {
        localINetworkSpeedStateCallBack.onSpeedChange(this.mSpeed);
      }
    }
  }
  
  private void scheduleNextUpdate()
  {
    long l1 = SystemClock.uptimeMillis();
    long l2 = UPDATE_INTERVAL * 1000;
    Message localMessage = new Message();
    localMessage.what = this.MSG_UPDATE_SPEED_ON_BG;
    this.mBackgroundHandler.sendMessageAtTime(localMessage, l1 + l2);
  }
  
  private void stopSpeed()
  {
    if (this.mSpeedMachine != null)
    {
      this.mSpeedMachine.reset();
      this.mSpeedMachine.setTurnOff();
    }
    this.mBackgroundHandler.removeMessages(this.MSG_UPDATE_SPEED_ON_BG);
  }
  
  private void updateSpeed()
  {
    this.mIsFirstLoad = true;
    if (DEBUG) {
      Log.d(TAG, "updateSpeed");
    }
    this.mSpeed = "";
    Message localMessage = this.mHandler.obtainMessage();
    localMessage.what = this.MSG_UPDATE_NETWORTSPEED;
    localMessage.obj = this.mSpeed;
    this.mHandler.sendMessage(localMessage);
    if (this.mSpeedMachine != null)
    {
      this.mSpeedMachine.reset();
      this.mSpeedMachine.setTurnOn();
    }
    this.mBackgroundHandler.removeMessages(this.MSG_UPDATE_SPEED_ON_BG);
    localMessage = new Message();
    localMessage.what = this.MSG_UPDATE_SPEED_ON_BG;
    this.mBackgroundHandler.sendMessage(localMessage);
  }
  
  private void updateState()
  {
    boolean bool = isNetworkSpeedShowing();
    if (DEBUG) {
      Log.d(TAG, "updateState showState:" + bool);
    }
    if (this.mNetworkShowState != bool)
    {
      this.mNetworkShowState = bool;
      if (!this.mNetworkShowState) {
        break label88;
      }
      onStartTraceSpeed();
    }
    for (;;)
    {
      Message localMessage = this.mHandler.obtainMessage();
      localMessage.what = this.MSG_UPDATE_SHOW;
      this.mHandler.sendMessage(localMessage);
      return;
      label88:
      onStopTraceSpeed();
    }
  }
  
  /* Error */
  public void addCallback(INetworkSpeedStateCallBack paramINetworkSpeedStateCallBack)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 207	com/android/systemui/statusbar/phone/NetworkSpeedController:mNetworkSpeedStateCallBack	Ljava/util/ArrayList;
    //   6: aload_1
    //   7: invokevirtual 439	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   10: pop
    //   11: aload_1
    //   12: aload_0
    //   13: getfield 111	com/android/systemui/statusbar/phone/NetworkSpeedController:mSpeed	Ljava/lang/String;
    //   16: invokeinterface 379 2 0
    //   21: aload_1
    //   22: aload_0
    //   23: getfield 73	com/android/systemui/statusbar/phone/NetworkSpeedController:mNetworkShowState	Z
    //   26: invokeinterface 364 2 0
    //   31: aload_0
    //   32: monitorexit
    //   33: return
    //   34: astore_1
    //   35: getstatic 90	com/android/systemui/statusbar/phone/NetworkSpeedController:TAG	Ljava/lang/String;
    //   38: ldc_w 441
    //   41: aload_1
    //   42: invokestatic 446	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   45: pop
    //   46: goto -15 -> 31
    //   49: astore_1
    //   50: aload_0
    //   51: monitorexit
    //   52: aload_1
    //   53: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	54	0	this	NetworkSpeedController
    //   0	54	1	paramINetworkSpeedStateCallBack	INetworkSpeedStateCallBack
    // Exception table:
    //   from	to	target	type
    //   11	31	34	java/lang/Exception
    //   2	11	49	finally
    //   11	31	49	finally
    //   35	46	49	finally
  }
  
  public boolean isNetworkConnected()
  {
    if (this.mContext == null) {
      return false;
    }
    NetworkInfo localNetworkInfo = ((ConnectivityManager)this.mContext.getSystemService("connectivity")).getActiveNetworkInfo();
    if (localNetworkInfo != null) {}
    for (boolean bool = localNetworkInfo.isAvailable();; bool = false)
    {
      if (DEBUG) {
        Log.v(TAG, "isNetworkConnected = " + bool);
      }
      return bool;
    }
  }
  
  public boolean isNetworkSpeedShowing()
  {
    return (isNetworkConnected()) && (!this.mBlockNetworkSpeed);
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramContext = paramIntent.getAction();
    if (paramContext == "android.intent.action.TIME_SET") {
      updateState();
    }
    do
    {
      return;
      if (paramContext == "android.intent.action.TIMEZONE_CHANGED")
      {
        updateState();
        return;
      }
    } while (paramContext != "android.net.wifi.WIFI_AP_STATE_CHANGED");
    if (paramIntent.getIntExtra("wifi_state", 14) == 13) {}
    for (boolean bool = true;; bool = false)
    {
      this.mHotSpotEnable = bool;
      if (DEBUG) {
        Log.i(TAG, "HotSpot enable:" + this.mHotSpotEnable);
      }
      updateState();
      return;
    }
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    if (!"icon_blacklist".equals(paramString1)) {
      return;
    }
    boolean bool = StatusBarIconController.getIconBlacklist(paramString2).contains("networkspeed");
    if (bool != this.mBlockNetworkSpeed)
    {
      Log.i(TAG, " onTuningChanged blocknetworkSpeed:" + bool);
      this.mBlockNetworkSpeed = bool;
      updateState();
    }
  }
  
  public void updateConnectivity(BitSet paramBitSet1, BitSet paramBitSet2)
  {
    if (DEBUG) {
      Log.d(TAG, "updateConnectivity connectedTransports:" + paramBitSet1 + " validatedTransports:" + paramBitSet2);
    }
    updateState();
  }
  
  public static abstract interface INetworkSpeedStateCallBack
  {
    public abstract void onSpeedChange(String paramString);
    
    public abstract void onSpeedShow(boolean paramBoolean);
  }
  
  private class MyBackgroundHandler
    extends Handler
  {
    public MyBackgroundHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (paramMessage.what == NetworkSpeedController.-get4(NetworkSpeedController.this))
      {
        NetworkSpeedController.-get7(NetworkSpeedController.this).removeMessages(NetworkSpeedController.-get4(NetworkSpeedController.this));
        if (NetworkSpeedController.-get12(NetworkSpeedController.this).isTurnOn())
        {
          NetworkSpeedController.MySpeedMachine.-wrap0(NetworkSpeedController.-get12(NetworkSpeedController.this));
          NetworkSpeedController.-wrap4(NetworkSpeedController.this);
        }
      }
    }
  }
  
  private class MyHandler
    extends Handler
  {
    public MyHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      int i = paramMessage.what;
      if (i == NetworkSpeedController.-get2(NetworkSpeedController.this)) {
        if ((paramMessage.obj instanceof String))
        {
          NetworkSpeedController.-set1(NetworkSpeedController.this, (String)paramMessage.obj);
          NetworkSpeedController.-wrap3(NetworkSpeedController.this);
        }
      }
      do
      {
        return;
        if (i == NetworkSpeedController.-get1(NetworkSpeedController.this))
        {
          NetworkSpeedController.-wrap5(NetworkSpeedController.this);
          return;
        }
      } while (i != NetworkSpeedController.-get3(NetworkSpeedController.this));
      NetworkSpeedController.-wrap2(NetworkSpeedController.this, NetworkSpeedController.-get11(NetworkSpeedController.this));
    }
  }
  
  private class MySpeedMachine
  {
    long incrementRxBytes = 0L;
    long incrementTxBytes = 0L;
    boolean isTurnOn = false;
    boolean mIsFirstLoadTether = true;
    long oldRxBytes = 0L;
    long oldTetherRxBytes = 0L;
    long oldTetherTxBytes = 0L;
    long oldTxBytes = 0L;
    
    public MySpeedMachine()
    {
      reset();
    }
    
    private void updateSpeedonBG()
    {
      if (NetworkSpeedController.this.isNetworkSpeedShowing())
      {
        long l5 = TrafficStats.getTotalTxBytes();
        long l6 = TrafficStats.getTotalRxBytes();
        this.incrementTxBytes = (l5 - this.oldTxBytes);
        this.incrementRxBytes = (l6 - this.oldRxBytes);
        this.oldTxBytes = l5;
        this.oldRxBytes = l6;
        long l3 = 0L;
        long l4 = 0L;
        if (!NetworkSpeedController.-get9(NetworkSpeedController.this))
        {
          this.oldTetherTxBytes = 0L;
          this.oldTetherRxBytes = 0L;
          this.mIsFirstLoadTether = true;
          if (NetworkSpeedController.-get10(NetworkSpeedController.this))
          {
            if (NetworkSpeedController.-get0()) {
              Log.d(NetworkSpeedController.-get5(), "NetWorkSpeed is first load.");
            }
            this.incrementTxBytes = 0L;
            this.incrementRxBytes = 0L;
            NetworkSpeedController.-set0(NetworkSpeedController.this, false);
          }
          if (this.incrementTxBytes < 0L) {
            this.incrementTxBytes = 0L;
          }
          if (this.incrementRxBytes < 0L) {
            this.incrementRxBytes = 0L;
          }
          if (this.incrementRxBytes <= this.incrementTxBytes) {
            break label523;
          }
        }
        label523:
        for (long l1 = this.incrementRxBytes;; l1 = this.incrementTxBytes)
        {
          long l2 = l1 / NetworkSpeedController.-get6();
          localObject = NetworkSpeedController.-wrap0(NetworkSpeedController.this, l2);
          if (NetworkSpeedController.-get0()) {
            Log.d(NetworkSpeedController.-get5(), "NetWorkSpeed refresh totalTxBytes=" + l5 + ", totalRxBytes=" + l6 + ", incrementPs=" + l2 + ", mSpeed=" + (String)localObject + " ,incrementBytes:" + l1);
          }
          Message localMessage = NetworkSpeedController.-get8(NetworkSpeedController.this).obtainMessage();
          localMessage.what = NetworkSpeedController.-get2(NetworkSpeedController.this);
          localMessage.obj = localObject;
          NetworkSpeedController.-get8(NetworkSpeedController.this).sendMessage(localMessage);
          return;
          localObject = NetworkSpeedController.-wrap1(NetworkSpeedController.this);
          l2 = l4;
          l1 = l3;
          if (localObject != null)
          {
            l2 = l4;
            l1 = l3;
            if (localObject.length == 2)
            {
              l3 = localObject[0];
              l4 = localObject[1];
              l1 = l4 - this.oldTetherTxBytes;
              l2 = l3 - this.oldTetherRxBytes;
              this.oldTetherTxBytes = l4;
              this.oldTetherRxBytes = l3;
            }
          }
          if (NetworkSpeedController.-get0()) {
            Log.d(NetworkSpeedController.-get5(), "NetWorkSpeed TetherTx:" + NetworkSpeedController.-wrap0(NetworkSpeedController.this, l1 / NetworkSpeedController.-get6()) + " tTetherRx:" + NetworkSpeedController.-wrap0(NetworkSpeedController.this, l2 / NetworkSpeedController.-get6()) + " systemTx:" + NetworkSpeedController.-wrap0(NetworkSpeedController.this, this.incrementTxBytes / NetworkSpeedController.-get6()) + " systemRx:" + NetworkSpeedController.-wrap0(NetworkSpeedController.this, this.incrementRxBytes / NetworkSpeedController.-get6()));
          }
          if (this.mIsFirstLoadTether)
          {
            this.mIsFirstLoadTether = false;
            break;
          }
          this.incrementTxBytes = (this.incrementTxBytes + l1 + l2);
          break;
        }
      }
      Object localObject = NetworkSpeedController.-get8(NetworkSpeedController.this).obtainMessage();
      ((Message)localObject).what = NetworkSpeedController.-get1(NetworkSpeedController.this);
      NetworkSpeedController.-get8(NetworkSpeedController.this).sendMessage((Message)localObject);
      Log.d(NetworkSpeedController.-get5(), "send MSG_CLOSE_NETWORTSPEED");
    }
    
    public boolean isTurnOn()
    {
      return this.isTurnOn;
    }
    
    public void reset()
    {
      this.oldTxBytes = 0L;
      this.incrementTxBytes = 0L;
      this.oldRxBytes = 0L;
      this.incrementRxBytes = 0L;
    }
    
    public void setTurnOff()
    {
      this.isTurnOn = false;
    }
    
    public void setTurnOn()
    {
      this.isTurnOn = true;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\NetworkSpeedController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */