package com.android.systemui.volume;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.media.IVolumeController.Stub;
import android.media.VolumePolicy;
import android.media.session.MediaController.PlaybackInfo;
import android.media.session.MediaSession.Token;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.Vibrator;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.service.notification.Condition;
import android.service.notification.ZenModeConfig;
import android.util.Log;
import android.util.SparseArray;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.qs.tiles.DndTile;
import com.android.systemui.util.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;

public class VolumeDialogController
{
  private static final int[] STREAMS = { 4, 6, 8, 3, 5, 2, 1, 7, 9, 0 };
  private static final String TAG = Util.logTag(VolumeDialogController.class);
  private final AudioManager mAudio;
  private final C mCallbacks = new C(null);
  private final ComponentName mComponent;
  private final Context mContext;
  private boolean mDestroyed;
  private boolean mEnabled;
  private final boolean mHasVibrator;
  private final MediaSessions mMediaSessions;
  private final MediaSessionsCallbacks mMediaSessionsCallbacksW = new MediaSessionsCallbacks(null);
  private KeyguardUpdateMonitorCallback mMonitorCallback = new KeyguardUpdateMonitorCallback()
  {
    public void onSystemReady()
    {
      VolumeDialogController.-set1(VolumeDialogController.this, Settings.System.getIntForUser(VolumeDialogController.-get3(VolumeDialogController.this).getContentResolver(), "oem_vibrate_under_silent", 0, KeyguardUpdateMonitor.getCurrentUser()));
      if (VolumeDialogController.-wrap9(VolumeDialogController.this)) {
        VolumeDialogController.-get1(VolumeDialogController.this).onStateChanged(VolumeDialogController.-get9(VolumeDialogController.this));
      }
    }
  };
  private final NotificationManager mNoMan;
  private final SettingObserver mObserver;
  private final Receiver mReceiver = new Receiver(null);
  private boolean mShowDndTile = true;
  private final State mState = new State();
  private final String[] mStreamTitles;
  private int mThreeKeySatus = -1;
  private int mVibrateWhenMute = 0;
  private final Vibrator mVibrator;
  private final VC mVolumeController = new VC(null);
  private VolumePolicy mVolumePolicy;
  private final W mWorker;
  private final HandlerThread mWorkerThread;
  private int mZenMode = 0;
  
  public VolumeDialogController(Context paramContext, ComponentName paramComponentName)
  {
    this.mContext = paramContext.getApplicationContext();
    Events.writeEvent(this.mContext, 5, new Object[0]);
    this.mComponent = paramComponentName;
    this.mWorkerThread = new HandlerThread(VolumeDialogController.class.getSimpleName());
    this.mWorkerThread.start();
    this.mWorker = new W(this.mWorkerThread.getLooper());
    this.mMediaSessions = createMediaSessions(this.mContext, this.mWorkerThread.getLooper(), this.mMediaSessionsCallbacksW);
    this.mAudio = ((AudioManager)this.mContext.getSystemService("audio"));
    this.mNoMan = ((NotificationManager)this.mContext.getSystemService("notification"));
    this.mObserver = new SettingObserver(this.mWorker);
    this.mObserver.init();
    this.mReceiver.init();
    this.mStreamTitles = this.mContext.getResources().getStringArray(2131427610);
    this.mVibrator = ((Vibrator)this.mContext.getSystemService("vibrator"));
    if (this.mVibrator != null) {}
    for (boolean bool = this.mVibrator.hasVibrator();; bool = false)
    {
      this.mHasVibrator = bool;
      return;
    }
  }
  
  private boolean checkRoutedToBluetoothW(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = false;
    if (paramInt == 3)
    {
      bool1 = bool2;
      if ((this.mAudio.getDevicesForStream(3) & 0x380) != 0) {
        bool1 = true;
      }
      bool1 = updateStreamRoutedToBluetoothW(paramInt, bool1);
    }
    return bool1;
  }
  
  private static String getApplicationName(Context paramContext, ComponentName paramComponentName)
  {
    if (paramComponentName == null) {
      return null;
    }
    PackageManager localPackageManager = paramContext.getPackageManager();
    paramContext = paramComponentName.getPackageName();
    try
    {
      paramComponentName = Objects.toString(localPackageManager.getApplicationInfo(paramContext, 0).loadLabel(localPackageManager), "").trim();
      int i = paramComponentName.length();
      if (i > 0) {
        return paramComponentName;
      }
    }
    catch (PackageManager.NameNotFoundException paramComponentName) {}
    return paramContext;
  }
  
  private static boolean isLogWorthy(int paramInt)
  {
    switch (paramInt)
    {
    case 5: 
    default: 
      return false;
    }
    return true;
  }
  
  private static boolean isRinger(int paramInt)
  {
    return (paramInt == 2) || (paramInt == 5);
  }
  
  private void onDismissRequestedW(int paramInt)
  {
    this.mCallbacks.onDismissRequested(paramInt);
  }
  
  private void onGetStateW()
  {
    int[] arrayOfInt = STREAMS;
    int i = 0;
    int j = arrayOfInt.length;
    while (i < j)
    {
      int k = arrayOfInt[i];
      updateStreamLevelW(k, this.mAudio.getLastAudibleStreamVolume(k));
      streamStateW(k).levelMin = this.mAudio.getStreamMinVolume(k);
      streamStateW(k).levelMax = this.mAudio.getStreamMaxVolume(k);
      updateStreamMuteW(k, this.mAudio.isStreamMute(k));
      StreamState localStreamState = streamStateW(k);
      localStreamState.muteSupported = this.mAudio.isStreamAffectedByMute(k);
      localStreamState.name = this.mStreamTitles[k];
      checkRoutedToBluetoothW(k);
      i += 1;
    }
    updateRingerModeExternalW(this.mAudio.getRingerMode());
    updateZenModeW();
    updateEffectsSuppressorW(this.mNoMan.getEffectsSuppressor());
    updateZenModeConfigW();
    this.mCallbacks.onStateChanged(this.mState);
  }
  
  private void onNotifyVisibleW(boolean paramBoolean)
  {
    if (this.mDestroyed) {
      return;
    }
    this.mAudio.notifyVolumeControllerVisible(this.mVolumeController, paramBoolean);
    if ((!paramBoolean) && (updateActiveStreamW(-1))) {
      this.mCallbacks.onStateChanged(this.mState);
    }
  }
  
  private void onSetActiveStreamW(int paramInt)
  {
    if (updateActiveStreamW(paramInt)) {
      this.mCallbacks.onStateChanged(this.mState);
    }
  }
  
  private void onSetExitConditionW(Condition paramCondition)
  {
    Uri localUri = null;
    NotificationManager localNotificationManager = this.mNoMan;
    int i = this.mState.zenMode;
    if (paramCondition != null) {
      localUri = paramCondition.id;
    }
    localNotificationManager.setZenMode(i, localUri, TAG);
  }
  
  private void onSetRingerModeW(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mAudio.setRingerMode(paramInt);
      return;
    }
    this.mAudio.setRingerModeInternal(paramInt);
  }
  
  private void onSetStreamMuteW(int paramInt, boolean paramBoolean)
  {
    AudioManager localAudioManager = this.mAudio;
    if (paramBoolean) {}
    for (int i = -100;; i = 100)
    {
      localAudioManager.adjustStreamVolume(paramInt, i, 0);
      return;
    }
  }
  
  private void onSetStreamVolumeW(int paramInt1, int paramInt2)
  {
    if (D.BUG) {
      Log.d(TAG, "onSetStreamVolume " + paramInt1 + " level=" + paramInt2);
    }
    if (paramInt1 >= 100)
    {
      this.mMediaSessionsCallbacksW.setStreamVolume(paramInt1, paramInt2);
      return;
    }
    this.mAudio.setStreamVolume(paramInt1, paramInt2, 0);
  }
  
  private void onSetZenModeW(int paramInt)
  {
    if (D.BUG) {
      Log.d(TAG, "onSetZenModeW " + paramInt);
    }
    this.mNoMan.setZenMode(paramInt, null, TAG);
  }
  
  private void onShowSafetyWarningW(int paramInt)
  {
    this.mCallbacks.onShowSafetyWarning(paramInt);
  }
  
  private boolean onVolumeChangedW(int paramInt1, int paramInt2)
  {
    int i;
    int j;
    label19:
    int k;
    label30:
    label40:
    boolean bool1;
    int n;
    boolean bool2;
    if ((paramInt2 & 0x1) != 0)
    {
      i = 1;
      if ((paramInt2 & 0x1000) == 0) {
        break label190;
      }
      j = 1;
      if ((paramInt2 & 0x800) == 0) {
        break label196;
      }
      k = 1;
      if ((paramInt2 & 0x80) == 0) {
        break label202;
      }
      paramInt2 = 1;
      bool1 = false;
      if (i != 0) {
        bool1 = updateActiveStreamW(paramInt1);
      }
      n = this.mAudio.getLastAudibleStreamVolume(paramInt1);
      bool2 = updateStreamLevelW(paramInt1, n);
      if (i == 0) {
        break label207;
      }
    }
    label190:
    label196:
    label202:
    label207:
    for (int m = 3;; m = paramInt1)
    {
      bool1 = bool1 | bool2 | checkRoutedToBluetoothW(m);
      if (bool1) {
        this.mCallbacks.onStateChanged(this.mState);
      }
      if (i != 0) {
        this.mCallbacks.onShowRequested(1);
      }
      if (k != 0) {
        this.mCallbacks.onShowVibrateHint();
      }
      if (paramInt2 != 0) {
        this.mCallbacks.onShowSilentHint();
      }
      if ((bool1) && (j != 0)) {
        Events.writeEvent(this.mContext, 4, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(n) });
      }
      return bool1;
      i = 0;
      break;
      j = 0;
      break label19;
      k = 0;
      break label30;
      paramInt2 = 0;
      break label40;
    }
  }
  
  private StreamState streamStateW(int paramInt)
  {
    StreamState localStreamState2 = (StreamState)this.mState.states.get(paramInt);
    StreamState localStreamState1 = localStreamState2;
    if (localStreamState2 == null)
    {
      localStreamState1 = new StreamState();
      this.mState.states.put(paramInt, localStreamState1);
    }
    return localStreamState1;
  }
  
  private boolean updateActiveStreamW(int paramInt)
  {
    if (paramInt == this.mState.activeStream) {
      return false;
    }
    this.mState.activeStream = paramInt;
    Events.writeEvent(this.mContext, 2, new Object[] { Integer.valueOf(paramInt) });
    if (D.BUG) {
      Log.d(TAG, "updateActiveStreamW " + paramInt);
    }
    if (paramInt < 100) {}
    for (;;)
    {
      if (D.BUG) {
        Log.d(TAG, "forceVolumeControlStream " + paramInt);
      }
      this.mAudio.forceVolumeControlStream(paramInt);
      return true;
      paramInt = -1;
    }
  }
  
  private boolean updateEffectsSuppressorW(ComponentName paramComponentName)
  {
    if (Objects.equals(this.mState.effectsSuppressor, paramComponentName)) {
      return false;
    }
    this.mState.effectsSuppressor = paramComponentName;
    this.mState.effectsSuppressorName = getApplicationName(this.mContext, this.mState.effectsSuppressor);
    Events.writeEvent(this.mContext, 14, new Object[] { this.mState.effectsSuppressor, this.mState.effectsSuppressorName });
    return true;
  }
  
  private boolean updateRingerModeExternalW(int paramInt)
  {
    if (paramInt == this.mState.ringerModeExternal) {
      return false;
    }
    this.mState.ringerModeExternal = paramInt;
    Events.writeEvent(this.mContext, 12, new Object[] { Integer.valueOf(paramInt) });
    return true;
  }
  
  private boolean updateRingerModeInternalW(int paramInt)
  {
    if (paramInt == this.mState.ringerModeInternal) {
      return false;
    }
    this.mState.ringerModeInternal = paramInt;
    Events.writeEvent(this.mContext, 11, new Object[] { Integer.valueOf(paramInt) });
    return true;
  }
  
  private boolean updateStreamLevelW(int paramInt1, int paramInt2)
  {
    StreamState localStreamState = streamStateW(paramInt1);
    if (localStreamState.level == paramInt2) {
      return false;
    }
    localStreamState.level = paramInt2;
    if (isLogWorthy(paramInt1)) {
      Events.writeEvent(this.mContext, 10, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
    }
    return true;
  }
  
  private boolean updateStreamMuteW(int paramInt, boolean paramBoolean)
  {
    StreamState localStreamState = streamStateW(paramInt);
    if (localStreamState.muted == paramBoolean) {
      return false;
    }
    localStreamState.muted = paramBoolean;
    if (isLogWorthy(paramInt)) {
      Events.writeEvent(this.mContext, 15, new Object[] { Integer.valueOf(paramInt), Boolean.valueOf(paramBoolean) });
    }
    if ((paramBoolean) && (isRinger(paramInt))) {
      updateRingerModeInternalW(this.mAudio.getRingerModeInternal());
    }
    return true;
  }
  
  private boolean updateStreamRoutedToBluetoothW(int paramInt, boolean paramBoolean)
  {
    StreamState localStreamState = streamStateW(paramInt);
    if (localStreamState.routedToBluetooth == paramBoolean) {
      return false;
    }
    localStreamState.routedToBluetooth = paramBoolean;
    if (D.BUG) {
      Log.d(TAG, "updateStreamRoutedToBluetoothW stream=" + paramInt + " routedToBluetooth=" + paramBoolean);
    }
    return true;
  }
  
  private boolean updateZenModeConfigW()
  {
    ZenModeConfig localZenModeConfig = getZenModeConfig();
    if (Objects.equals(this.mState.zenModeConfig, localZenModeConfig)) {
      return false;
    }
    this.mState.zenModeConfig = localZenModeConfig;
    return true;
  }
  
  private boolean updateZenModeW()
  {
    this.mThreeKeySatus = Util.getThreeKeyStatus(this.mContext);
    int i = Settings.Global.getInt(this.mContext.getContentResolver(), "zen_mode", 0);
    Log.i(TAG, "s updateZenModeW zen:" + i + " threeKeySatus:" + this.mThreeKeySatus + " mVibrateWhenMute:" + this.mVibrateWhenMute);
    i = Util.getCorrectZenMode(i, this.mThreeKeySatus, this.mVibrateWhenMute);
    Log.i(TAG, "e updateZenModeW zen:" + i);
    if (this.mState.zenMode == i) {
      return false;
    }
    this.mState.zenMode = i;
    Events.writeEvent(this.mContext, 13, new Object[] { Integer.valueOf(i) });
    return true;
  }
  
  public void addCallback(Callbacks paramCallbacks, Handler paramHandler)
  {
    this.mCallbacks.add(paramCallbacks, paramHandler);
    if (paramCallbacks != null) {
      paramCallbacks.onStateChanged(this.mState);
    }
  }
  
  protected MediaSessions createMediaSessions(Context paramContext, Looper paramLooper, MediaSessions.Callbacks paramCallbacks)
  {
    return new MediaSessions(paramContext, paramLooper, paramCallbacks);
  }
  
  public void dismiss()
  {
    this.mCallbacks.onDismissRequested(2);
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println(VolumeDialogController.class.getSimpleName() + " state:");
    paramPrintWriter.print("  mEnabled: ");
    paramPrintWriter.println(this.mEnabled);
    paramPrintWriter.print("  mDestroyed: ");
    paramPrintWriter.println(this.mDestroyed);
    paramPrintWriter.print("  mVolumePolicy: ");
    paramPrintWriter.println(this.mVolumePolicy);
    paramPrintWriter.print("  mState: ");
    paramPrintWriter.println(this.mState.toString(4));
    paramPrintWriter.print("  mShowDndTile: ");
    paramPrintWriter.println(this.mShowDndTile);
    paramPrintWriter.print("  mHasVibrator: ");
    paramPrintWriter.println(this.mHasVibrator);
    paramPrintWriter.print("  mRemoteStreams: ");
    paramPrintWriter.println(MediaSessionsCallbacks.-get0(this.mMediaSessionsCallbacksW).values());
    paramPrintWriter.println();
    this.mMediaSessions.dump(paramPrintWriter);
  }
  
  public AudioManager getAudioManager()
  {
    return this.mAudio;
  }
  
  public void getState()
  {
    if (this.mDestroyed) {
      return;
    }
    this.mWorker.sendEmptyMessage(3);
  }
  
  public ZenModeConfig getZenModeConfig()
  {
    return this.mNoMan.getZenModeConfig();
  }
  
  public boolean hasVibrator()
  {
    return this.mHasVibrator;
  }
  
  public void notifyVisible(boolean paramBoolean)
  {
    if (this.mDestroyed) {
      return;
    }
    W localW = this.mWorker;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localW.obtainMessage(12, i, 0).sendToTarget();
      return;
    }
  }
  
  protected void onUserActivityW() {}
  
  /* Error */
  public void register()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 343	com/android/systemui/volume/VolumeDialogController:mAudio	Landroid/media/AudioManager;
    //   4: aload_0
    //   5: getfield 278	com/android/systemui/volume/VolumeDialogController:mVolumeController	Lcom/android/systemui/volume/VolumeDialogController$VC;
    //   8: invokevirtual 748	android/media/AudioManager:setVolumeController	(Landroid/media/IVolumeController;)V
    //   11: aload_0
    //   12: aload_0
    //   13: getfield 695	com/android/systemui/volume/VolumeDialogController:mVolumePolicy	Landroid/media/VolumePolicy;
    //   16: invokevirtual 752	com/android/systemui/volume/VolumeDialogController:setVolumePolicy	(Landroid/media/VolumePolicy;)V
    //   19: aload_0
    //   20: aload_0
    //   21: getfield 289	com/android/systemui/volume/VolumeDialogController:mShowDndTile	Z
    //   24: invokevirtual 755	com/android/systemui/volume/VolumeDialogController:showDndTile	(Z)V
    //   27: aload_0
    //   28: getfield 130	com/android/systemui/volume/VolumeDialogController:mMediaSessions	Lcom/android/systemui/volume/MediaSessions;
    //   31: invokevirtual 756	com/android/systemui/volume/MediaSessions:init	()V
    //   34: return
    //   35: astore_1
    //   36: getstatic 102	com/android/systemui/volume/VolumeDialogController:TAG	Ljava/lang/String;
    //   39: ldc_w 758
    //   42: aload_1
    //   43: invokestatic 762	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   46: pop
    //   47: return
    //   48: astore_1
    //   49: getstatic 102	com/android/systemui/volume/VolumeDialogController:TAG	Ljava/lang/String;
    //   52: ldc_w 764
    //   55: aload_1
    //   56: invokestatic 762	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   59: pop
    //   60: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	61	0	this	VolumeDialogController
    //   35	8	1	localSecurityException1	SecurityException
    //   48	8	1	localSecurityException2	SecurityException
    // Exception table:
    //   from	to	target	type
    //   0	11	35	java/lang/SecurityException
    //   27	34	48	java/lang/SecurityException
  }
  
  public void setActiveStream(int paramInt)
  {
    if (this.mDestroyed) {
      return;
    }
    this.mWorker.obtainMessage(11, paramInt, 0).sendToTarget();
  }
  
  public void setRingerMode(int paramInt, boolean paramBoolean)
  {
    if (this.mDestroyed) {
      return;
    }
    W localW = this.mWorker;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localW.obtainMessage(4, paramInt, i).sendToTarget();
      return;
    }
  }
  
  public void setStreamVolume(int paramInt1, int paramInt2)
  {
    if (this.mDestroyed) {
      return;
    }
    this.mWorker.obtainMessage(10, paramInt1, paramInt2).sendToTarget();
  }
  
  public void setVolumePolicy(VolumePolicy paramVolumePolicy)
  {
    this.mVolumePolicy = paramVolumePolicy;
    if (this.mVolumePolicy == null) {
      return;
    }
    try
    {
      this.mAudio.setVolumePolicy(this.mVolumePolicy);
      return;
    }
    catch (NoSuchMethodError paramVolumePolicy)
    {
      Log.w(TAG, "No volume policy api");
    }
  }
  
  public void showDndTile(boolean paramBoolean)
  {
    if (D.BUG) {
      Log.d(TAG, "showDndTile");
    }
    DndTile.setVisible(this.mContext, paramBoolean);
  }
  
  public void userActivity()
  {
    if (this.mDestroyed) {
      return;
    }
    this.mWorker.removeMessages(13);
    this.mWorker.sendEmptyMessage(13);
  }
  
  public void vibrate()
  {
    if (this.mHasVibrator) {
      this.mVibrator.vibrate(50L);
    }
  }
  
  private final class C
    implements VolumeDialogController.Callbacks
  {
    private final HashMap<VolumeDialogController.Callbacks, Handler> mCallbackMap = new HashMap();
    
    private C() {}
    
    public void add(VolumeDialogController.Callbacks paramCallbacks, Handler paramHandler)
    {
      if ((paramCallbacks == null) || (paramHandler == null)) {
        throw new IllegalArgumentException();
      }
      this.mCallbackMap.put(paramCallbacks, paramHandler);
    }
    
    public void onConfigurationChanged()
    {
      Iterator localIterator = this.mCallbackMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        final Map.Entry localEntry = (Map.Entry)localIterator.next();
        ((Handler)localEntry.getValue()).post(new Runnable()
        {
          public void run()
          {
            ((VolumeDialogController.Callbacks)localEntry.getKey()).onConfigurationChanged();
          }
        });
      }
    }
    
    public void onDismissRequested(final int paramInt)
    {
      Iterator localIterator = this.mCallbackMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        final Map.Entry localEntry = (Map.Entry)localIterator.next();
        ((Handler)localEntry.getValue()).post(new Runnable()
        {
          public void run()
          {
            ((VolumeDialogController.Callbacks)localEntry.getKey()).onDismissRequested(paramInt);
          }
        });
      }
    }
    
    public void onLayoutDirectionChanged(final int paramInt)
    {
      Iterator localIterator = this.mCallbackMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        final Map.Entry localEntry = (Map.Entry)localIterator.next();
        ((Handler)localEntry.getValue()).post(new Runnable()
        {
          public void run()
          {
            ((VolumeDialogController.Callbacks)localEntry.getKey()).onLayoutDirectionChanged(paramInt);
          }
        });
      }
    }
    
    public void onScreenOff()
    {
      Iterator localIterator = this.mCallbackMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        final Map.Entry localEntry = (Map.Entry)localIterator.next();
        ((Handler)localEntry.getValue()).post(new Runnable()
        {
          public void run()
          {
            ((VolumeDialogController.Callbacks)localEntry.getKey()).onScreenOff();
          }
        });
      }
    }
    
    public void onShowRequested(final int paramInt)
    {
      Iterator localIterator = this.mCallbackMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        final Map.Entry localEntry = (Map.Entry)localIterator.next();
        ((Handler)localEntry.getValue()).post(new Runnable()
        {
          public void run()
          {
            ((VolumeDialogController.Callbacks)localEntry.getKey()).onShowRequested(paramInt);
          }
        });
      }
    }
    
    public void onShowSafetyWarning(final int paramInt)
    {
      Iterator localIterator = this.mCallbackMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        final Map.Entry localEntry = (Map.Entry)localIterator.next();
        ((Handler)localEntry.getValue()).post(new Runnable()
        {
          public void run()
          {
            ((VolumeDialogController.Callbacks)localEntry.getKey()).onShowSafetyWarning(paramInt);
          }
        });
      }
    }
    
    public void onShowSilentHint()
    {
      Iterator localIterator = this.mCallbackMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        final Map.Entry localEntry = (Map.Entry)localIterator.next();
        ((Handler)localEntry.getValue()).post(new Runnable()
        {
          public void run()
          {
            ((VolumeDialogController.Callbacks)localEntry.getKey()).onShowSilentHint();
          }
        });
      }
    }
    
    public void onShowVibrateHint()
    {
      Iterator localIterator = this.mCallbackMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        final Map.Entry localEntry = (Map.Entry)localIterator.next();
        ((Handler)localEntry.getValue()).post(new Runnable()
        {
          public void run()
          {
            ((VolumeDialogController.Callbacks)localEntry.getKey()).onShowVibrateHint();
          }
        });
      }
    }
    
    public void onStateChanged(final VolumeDialogController.State paramState)
    {
      long l = System.currentTimeMillis();
      paramState = paramState.copy();
      Iterator localIterator = this.mCallbackMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        final Map.Entry localEntry = (Map.Entry)localIterator.next();
        ((Handler)localEntry.getValue()).post(new Runnable()
        {
          public void run()
          {
            ((VolumeDialogController.Callbacks)localEntry.getKey()).onStateChanged(paramState);
          }
        });
      }
      Events.writeState(l, paramState);
    }
  }
  
  public static abstract interface Callbacks
  {
    public abstract void onConfigurationChanged();
    
    public abstract void onDismissRequested(int paramInt);
    
    public abstract void onLayoutDirectionChanged(int paramInt);
    
    public abstract void onScreenOff();
    
    public abstract void onShowRequested(int paramInt);
    
    public abstract void onShowSafetyWarning(int paramInt);
    
    public abstract void onShowSilentHint();
    
    public abstract void onShowVibrateHint();
    
    public abstract void onStateChanged(VolumeDialogController.State paramState);
  }
  
  private final class MediaSessionsCallbacks
    implements MediaSessions.Callbacks
  {
    private int mNextStream = 100;
    private final HashMap<MediaSession.Token, Integer> mRemoteStreams = new HashMap();
    
    private MediaSessionsCallbacks() {}
    
    private MediaSession.Token findToken(int paramInt)
    {
      Iterator localIterator = this.mRemoteStreams.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        if (((Integer)localEntry.getValue()).equals(Integer.valueOf(paramInt))) {
          return (MediaSession.Token)localEntry.getKey();
        }
      }
      return null;
    }
    
    public void onRemoteRemoved(MediaSession.Token paramToken)
    {
      int i = ((Integer)this.mRemoteStreams.get(paramToken)).intValue();
      if (Utils.DEBUG_ONEPLUS) {
        Log.d(VolumeDialogController.-get0(), "onRemoteRemoved stream: " + i);
      }
      VolumeDialogController.-get9(VolumeDialogController.this).states.remove(i);
      if (VolumeDialogController.-get9(VolumeDialogController.this).activeStream == i) {
        VolumeDialogController.-wrap2(VolumeDialogController.this, -1);
      }
      VolumeDialogController.-get1(VolumeDialogController.this).onStateChanged(VolumeDialogController.-get9(VolumeDialogController.this));
    }
    
    public void onRemoteUpdate(MediaSession.Token paramToken, String paramString, MediaController.PlaybackInfo paramPlaybackInfo)
    {
      if (!this.mRemoteStreams.containsKey(paramToken))
      {
        this.mRemoteStreams.put(paramToken, Integer.valueOf(this.mNextStream));
        if (Utils.DEBUG_ONEPLUS) {
          Log.d(VolumeDialogController.-get0(), "onRemoteUpdate: " + paramString + " is stream " + this.mNextStream);
        }
        this.mNextStream += 1;
      }
      int j = ((Integer)this.mRemoteStreams.get(paramToken)).intValue();
      if (VolumeDialogController.-get9(VolumeDialogController.this).states.indexOfKey(j) < 0) {}
      for (int i = 1;; i = 0)
      {
        paramToken = VolumeDialogController.-wrap10(VolumeDialogController.this, j);
        paramToken.dynamic = true;
        paramToken.levelMin = 0;
        paramToken.levelMax = paramPlaybackInfo.getMaxVolume();
        if (paramToken.level != paramPlaybackInfo.getCurrentVolume())
        {
          paramToken.level = paramPlaybackInfo.getCurrentVolume();
          i = 1;
        }
        if (!Objects.equals(paramToken.name, paramString))
        {
          paramToken.name = paramString;
          i = 1;
        }
        if (i != 0)
        {
          if (Utils.DEBUG_ONEPLUS) {
            Log.d(VolumeDialogController.-get0(), "onRemoteUpdate: " + paramString + ": " + paramToken.level + " of " + paramToken.levelMax);
          }
          VolumeDialogController.-get1(VolumeDialogController.this).onStateChanged(VolumeDialogController.-get9(VolumeDialogController.this));
        }
        return;
      }
    }
    
    public void onRemoteVolumeChanged(MediaSession.Token paramToken, int paramInt)
    {
      if (this.mRemoteStreams == null) {
        return;
      }
      int i = ((Integer)this.mRemoteStreams.get(paramToken)).intValue();
      if (Utils.DEBUG_ONEPLUS) {
        Log.d(VolumeDialogController.-get0(), "onRemoteVolumeChanged stream: " + i);
      }
      if ((paramInt & 0x1) != 0) {}
      for (paramInt = 1;; paramInt = 0)
      {
        boolean bool2 = VolumeDialogController.-wrap2(VolumeDialogController.this, i);
        boolean bool1 = bool2;
        if (paramInt != 0) {
          bool1 = bool2 | VolumeDialogController.-wrap0(VolumeDialogController.this, 3);
        }
        if (bool1) {
          VolumeDialogController.-get1(VolumeDialogController.this).onStateChanged(VolumeDialogController.-get9(VolumeDialogController.this));
        }
        if (paramInt != 0) {
          VolumeDialogController.-get1(VolumeDialogController.this).onShowRequested(2);
        }
        return;
      }
    }
    
    public void setStreamVolume(int paramInt1, int paramInt2)
    {
      MediaSession.Token localToken = findToken(paramInt1);
      if (localToken == null)
      {
        Log.w(VolumeDialogController.-get0(), "setStreamVolume: No token found for stream: " + paramInt1);
        return;
      }
      VolumeDialogController.-get6(VolumeDialogController.this).setVolume(localToken, paramInt2);
    }
  }
  
  private final class Receiver
    extends BroadcastReceiver
  {
    private Receiver() {}
    
    public void init()
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
      localIntentFilter.addAction("android.media.STREAM_DEVICES_CHANGED_ACTION");
      localIntentFilter.addAction("android.media.RINGER_MODE_CHANGED");
      localIntentFilter.addAction("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION");
      localIntentFilter.addAction("android.media.STREAM_MUTE_CHANGED_ACTION");
      localIntentFilter.addAction("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED");
      localIntentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
      localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
      localIntentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
      VolumeDialogController.-get3(VolumeDialogController.this).registerReceiver(this, localIntentFilter, null, VolumeDialogController.-get10(VolumeDialogController.this));
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = paramIntent.getAction();
      boolean bool2 = false;
      int i;
      int j;
      int k;
      boolean bool1;
      if (paramContext.equals("android.media.VOLUME_CHANGED_ACTION"))
      {
        i = paramIntent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
        j = paramIntent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", -1);
        k = paramIntent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", -1);
        if (D.BUG) {
          Log.d(VolumeDialogController.-get0(), "onReceive VOLUME_CHANGED_ACTION stream=" + i + " level=" + j + " oldLevel=" + k);
        }
        bool1 = VolumeDialogController.-wrap6(VolumeDialogController.this, i, j);
      }
      for (;;)
      {
        if (bool1) {
          VolumeDialogController.-get1(VolumeDialogController.this).onStateChanged(VolumeDialogController.-get9(VolumeDialogController.this));
        }
        return;
        if (paramContext.equals("android.media.STREAM_DEVICES_CHANGED_ACTION"))
        {
          i = paramIntent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
          j = paramIntent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_DEVICES", -1);
          k = paramIntent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_DEVICES", -1);
          if (D.BUG) {
            Log.d(VolumeDialogController.-get0(), "onReceive STREAM_DEVICES_CHANGED_ACTION stream=" + i + " devices=" + j + " oldDevices=" + k);
          }
          bool1 = VolumeDialogController.-wrap0(VolumeDialogController.this, i) | VolumeDialogController.-wrap1(VolumeDialogController.this, i, 0);
        }
        else if (paramContext.equals("android.media.RINGER_MODE_CHANGED"))
        {
          i = paramIntent.getIntExtra("android.media.EXTRA_RINGER_MODE", -1);
          if (D.BUG) {
            Log.d(VolumeDialogController.-get0(), "onReceive RINGER_MODE_CHANGED_ACTION rm=" + Util.ringerModeToString(i));
          }
          bool1 = VolumeDialogController.-wrap4(VolumeDialogController.this, i);
        }
        else if (paramContext.equals("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION"))
        {
          i = paramIntent.getIntExtra("android.media.EXTRA_RINGER_MODE", -1);
          if (D.BUG) {
            Log.d(VolumeDialogController.-get0(), "onReceive INTERNAL_RINGER_MODE_CHANGED_ACTION rm=" + Util.ringerModeToString(i));
          }
          bool1 = VolumeDialogController.-wrap5(VolumeDialogController.this, i);
        }
        else if (paramContext.equals("android.media.STREAM_MUTE_CHANGED_ACTION"))
        {
          i = paramIntent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
          bool1 = paramIntent.getBooleanExtra("android.media.EXTRA_STREAM_VOLUME_MUTED", false);
          if (D.BUG) {
            Log.d(VolumeDialogController.-get0(), "onReceive STREAM_MUTE_CHANGED_ACTION stream=" + i + " muted=" + bool1);
          }
          bool1 = VolumeDialogController.-wrap7(VolumeDialogController.this, i, bool1);
        }
        else if (paramContext.equals("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED"))
        {
          if (D.BUG) {
            Log.d(VolumeDialogController.-get0(), "onReceive ACTION_EFFECTS_SUPPRESSOR_CHANGED");
          }
          bool1 = VolumeDialogController.-wrap3(VolumeDialogController.this, VolumeDialogController.-get8(VolumeDialogController.this).getEffectsSuppressor());
        }
        else if (paramContext.equals("android.intent.action.CONFIGURATION_CHANGED"))
        {
          if (D.BUG) {
            Log.d(VolumeDialogController.-get0(), "onReceive ACTION_CONFIGURATION_CHANGED");
          }
          VolumeDialogController.-get1(VolumeDialogController.this).onConfigurationChanged();
          bool1 = bool2;
        }
        else if (paramContext.equals("android.intent.action.SCREEN_OFF"))
        {
          if (D.BUG) {
            Log.d(VolumeDialogController.-get0(), "onReceive ACTION_SCREEN_OFF");
          }
          VolumeDialogController.-get1(VolumeDialogController.this).onScreenOff();
          bool1 = bool2;
        }
        else
        {
          bool1 = bool2;
          if (paramContext.equals("android.intent.action.CLOSE_SYSTEM_DIALOGS"))
          {
            if (D.BUG) {
              Log.d(VolumeDialogController.-get0(), "onReceive ACTION_CLOSE_SYSTEM_DIALOGS");
            }
            VolumeDialogController.this.dismiss();
            bool1 = bool2;
          }
        }
      }
    }
  }
  
  private final class SettingObserver
    extends ContentObserver
  {
    private final Uri SERVICE_URI = Settings.Secure.getUriFor("volume_controller_service_component");
    private final Uri THREEKEY_MODE_URI = Settings.Global.getUriFor("three_Key_mode");
    private final Uri VIBRATE_WHEN_MUTE = Settings.Global.getUriFor("oem_vibrate_under_silent");
    private final Uri ZEN_MODE_CONFIG_URI = Settings.Global.getUriFor("zen_mode_config_etag");
    private final Uri ZEN_MODE_URI = Settings.Global.getUriFor("zen_mode");
    
    public SettingObserver(Handler paramHandler)
    {
      super();
    }
    
    public void init()
    {
      VolumeDialogController.-get3(VolumeDialogController.this).getContentResolver().registerContentObserver(this.SERVICE_URI, false, this);
      VolumeDialogController.-get3(VolumeDialogController.this).getContentResolver().registerContentObserver(this.ZEN_MODE_URI, false, this);
      VolumeDialogController.-get3(VolumeDialogController.this).getContentResolver().registerContentObserver(this.ZEN_MODE_CONFIG_URI, false, this);
      VolumeDialogController.-get3(VolumeDialogController.this).getContentResolver().registerContentObserver(this.THREEKEY_MODE_URI, false, this);
      VolumeDialogController.-get3(VolumeDialogController.this).getContentResolver().registerContentObserver(this.VIBRATE_WHEN_MUTE, false, this);
      KeyguardUpdateMonitor.getInstance(VolumeDialogController.-get3(VolumeDialogController.this)).registerCallback(VolumeDialogController.-get7(VolumeDialogController.this));
      onChange(true, this.SERVICE_URI);
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      boolean bool = false;
      if (this.SERVICE_URI.equals(paramUri))
      {
        String str = Settings.Secure.getString(VolumeDialogController.-get3(VolumeDialogController.this).getContentResolver(), "volume_controller_service_component");
        if ((str != null) && (VolumeDialogController.-get2(VolumeDialogController.this) != null)) {}
        for (paramBoolean = VolumeDialogController.-get2(VolumeDialogController.this).equals(ComponentName.unflattenFromString(str)); paramBoolean == VolumeDialogController.-get5(VolumeDialogController.this); paramBoolean = false) {
          return;
        }
        if (paramBoolean) {
          VolumeDialogController.this.register();
        }
        VolumeDialogController.-set0(VolumeDialogController.this, paramBoolean);
      }
      if ((!this.ZEN_MODE_URI.equals(paramUri)) && (!this.VIBRATE_WHEN_MUTE.equals(paramUri)))
      {
        paramBoolean = bool;
        if (!this.THREEKEY_MODE_URI.equals(paramUri)) {}
      }
      else
      {
        VolumeDialogController.-set1(VolumeDialogController.this, Settings.System.getIntForUser(VolumeDialogController.-get3(VolumeDialogController.this).getContentResolver(), "oem_vibrate_under_silent", 0, KeyguardUpdateMonitor.getCurrentUser()));
        paramBoolean = VolumeDialogController.-wrap9(VolumeDialogController.this);
      }
      if (this.ZEN_MODE_CONFIG_URI.equals(paramUri)) {
        paramBoolean = VolumeDialogController.-wrap8(VolumeDialogController.this);
      }
      if (paramBoolean) {
        VolumeDialogController.-get1(VolumeDialogController.this).onStateChanged(VolumeDialogController.-get9(VolumeDialogController.this));
      }
    }
  }
  
  public static final class State
  {
    public static int NO_ACTIVE_STREAM = -1;
    public int activeStream = NO_ACTIVE_STREAM;
    public ComponentName effectsSuppressor;
    public String effectsSuppressorName;
    public int ringerModeExternal;
    public int ringerModeInternal;
    public final SparseArray<VolumeDialogController.StreamState> states = new SparseArray();
    public int zenMode;
    public ZenModeConfig zenModeConfig;
    
    private static void sep(StringBuilder paramStringBuilder, int paramInt)
    {
      if (paramInt > 0)
      {
        paramStringBuilder.append('\n');
        int i = 0;
        while (i < paramInt)
        {
          paramStringBuilder.append(' ');
          i += 1;
        }
      }
      paramStringBuilder.append(',');
    }
    
    public State copy()
    {
      State localState = new State();
      int i = 0;
      while (i < this.states.size())
      {
        localState.states.put(this.states.keyAt(i), ((VolumeDialogController.StreamState)this.states.valueAt(i)).copy());
        i += 1;
      }
      localState.ringerModeExternal = this.ringerModeExternal;
      localState.ringerModeInternal = this.ringerModeInternal;
      localState.zenMode = this.zenMode;
      if (this.effectsSuppressor != null) {
        localState.effectsSuppressor = this.effectsSuppressor.clone();
      }
      localState.effectsSuppressorName = this.effectsSuppressorName;
      if (this.zenModeConfig != null) {
        localState.zenModeConfig = this.zenModeConfig.copy();
      }
      localState.activeStream = this.activeStream;
      return localState;
    }
    
    public String toString()
    {
      return toString(0);
    }
    
    public String toString(int paramInt)
    {
      StringBuilder localStringBuilder = new StringBuilder("{");
      if (paramInt > 0) {
        sep(localStringBuilder, paramInt);
      }
      int i = 0;
      while (i < this.states.size())
      {
        if (i > 0) {
          sep(localStringBuilder, paramInt);
        }
        int j = this.states.keyAt(i);
        VolumeDialogController.StreamState localStreamState = (VolumeDialogController.StreamState)this.states.valueAt(i);
        localStringBuilder.append(AudioSystem.streamToString(j)).append(":").append(localStreamState.level).append('[').append(localStreamState.levelMin).append("..").append(localStreamState.levelMax).append(']');
        if (localStreamState.muted) {
          localStringBuilder.append(" [MUTED]");
        }
        i += 1;
      }
      sep(localStringBuilder, paramInt);
      localStringBuilder.append("ringerModeExternal:").append(this.ringerModeExternal);
      sep(localStringBuilder, paramInt);
      localStringBuilder.append("ringerModeInternal:").append(this.ringerModeInternal);
      sep(localStringBuilder, paramInt);
      localStringBuilder.append("zenMode:").append(this.zenMode);
      sep(localStringBuilder, paramInt);
      localStringBuilder.append("effectsSuppressor:").append(this.effectsSuppressor);
      sep(localStringBuilder, paramInt);
      localStringBuilder.append("effectsSuppressorName:").append(this.effectsSuppressorName);
      sep(localStringBuilder, paramInt);
      localStringBuilder.append("zenModeConfig:").append(this.zenModeConfig);
      sep(localStringBuilder, paramInt);
      localStringBuilder.append("activeStream:").append(this.activeStream);
      if (paramInt > 0) {
        sep(localStringBuilder, paramInt);
      }
      return '}';
    }
  }
  
  public static final class StreamState
  {
    public boolean dynamic;
    public int level;
    public int levelMax;
    public int levelMin;
    public boolean muteSupported;
    public boolean muted;
    public String name;
    public boolean routedToBluetooth;
    
    public StreamState copy()
    {
      StreamState localStreamState = new StreamState();
      localStreamState.dynamic = this.dynamic;
      localStreamState.level = this.level;
      localStreamState.levelMin = this.levelMin;
      localStreamState.levelMax = this.levelMax;
      localStreamState.muted = this.muted;
      localStreamState.muteSupported = this.muteSupported;
      localStreamState.name = this.name;
      localStreamState.routedToBluetooth = this.routedToBluetooth;
      return localStreamState;
    }
  }
  
  private final class VC
    extends IVolumeController.Stub
  {
    private final String TAG = VolumeDialogController.-get0() + ".VC";
    
    private VC() {}
    
    public void dismiss()
      throws RemoteException
    {
      if (D.BUG) {
        Log.d(this.TAG, "dismiss requested");
      }
      if (VolumeDialogController.-get4(VolumeDialogController.this)) {
        return;
      }
      VolumeDialogController.-get10(VolumeDialogController.this).obtainMessage(2, 2, 0).sendToTarget();
      VolumeDialogController.-get10(VolumeDialogController.this).sendEmptyMessage(2);
    }
    
    public void displaySafeVolumeWarning(int paramInt)
      throws RemoteException
    {
      if (D.BUG) {
        Log.d(this.TAG, "displaySafeVolumeWarning " + Util.audioManagerFlagsToString(paramInt));
      }
      if (VolumeDialogController.-get4(VolumeDialogController.this)) {
        return;
      }
      VolumeDialogController.-get10(VolumeDialogController.this).obtainMessage(14, paramInt, 0).sendToTarget();
    }
    
    public void masterMuteChanged(int paramInt)
      throws RemoteException
    {
      if (D.BUG) {
        Log.d(this.TAG, "masterMuteChanged");
      }
    }
    
    public void setLayoutDirection(int paramInt)
      throws RemoteException
    {
      if (D.BUG) {
        Log.d(this.TAG, "setLayoutDirection");
      }
      if (VolumeDialogController.-get4(VolumeDialogController.this)) {
        return;
      }
      VolumeDialogController.-get10(VolumeDialogController.this).obtainMessage(8, paramInt, 0).sendToTarget();
    }
    
    public void volumeChanged(int paramInt1, int paramInt2)
      throws RemoteException
    {
      if (D.BUG) {
        Log.d(this.TAG, "volumeChanged " + AudioSystem.streamToString(paramInt1) + " " + Util.audioManagerFlagsToString(paramInt2));
      }
      if (VolumeDialogController.-get4(VolumeDialogController.this)) {
        return;
      }
      VolumeDialogController.-get10(VolumeDialogController.this).obtainMessage(1, paramInt1, paramInt2).sendToTarget();
    }
  }
  
  private final class W
    extends Handler
  {
    W(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool2 = true;
      boolean bool3 = true;
      boolean bool1 = true;
      VolumeDialogController localVolumeDialogController;
      int i;
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        VolumeDialogController.-wrap1(VolumeDialogController.this, paramMessage.arg1, paramMessage.arg2);
        return;
      case 2: 
        VolumeDialogController.-wrap11(VolumeDialogController.this, paramMessage.arg1);
        return;
      case 3: 
        VolumeDialogController.-wrap12(VolumeDialogController.this);
        return;
      case 4: 
        localVolumeDialogController = VolumeDialogController.this;
        i = paramMessage.arg1;
        if (paramMessage.arg2 != 0) {}
        for (;;)
        {
          VolumeDialogController.-wrap16(localVolumeDialogController, i, bool1);
          return;
          bool1 = false;
        }
      case 5: 
        VolumeDialogController.-wrap19(VolumeDialogController.this, paramMessage.arg1);
        return;
      case 6: 
        VolumeDialogController.-wrap15(VolumeDialogController.this, (Condition)paramMessage.obj);
        return;
      case 7: 
        localVolumeDialogController = VolumeDialogController.this;
        i = paramMessage.arg1;
        if (paramMessage.arg2 != 0) {}
        for (bool1 = bool2;; bool1 = false)
        {
          VolumeDialogController.-wrap17(localVolumeDialogController, i, bool1);
          return;
        }
      case 8: 
        VolumeDialogController.-get1(VolumeDialogController.this).onLayoutDirectionChanged(paramMessage.arg1);
        return;
      case 9: 
        VolumeDialogController.-get1(VolumeDialogController.this).onConfigurationChanged();
        return;
      case 10: 
        VolumeDialogController.-wrap18(VolumeDialogController.this, paramMessage.arg1, paramMessage.arg2);
        return;
      case 11: 
        VolumeDialogController.-wrap14(VolumeDialogController.this, paramMessage.arg1);
        return;
      case 12: 
        localVolumeDialogController = VolumeDialogController.this;
        if (paramMessage.arg1 != 0) {}
        for (bool1 = bool3;; bool1 = false)
        {
          VolumeDialogController.-wrap13(localVolumeDialogController, bool1);
          return;
        }
      case 13: 
        VolumeDialogController.this.onUserActivityW();
        return;
      }
      VolumeDialogController.-wrap20(VolumeDialogController.this, paramMessage.arg1);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\volume\VolumeDialogController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */