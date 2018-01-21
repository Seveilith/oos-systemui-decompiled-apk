package com.android.systemui.volume;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.IRemoteVolumeController;
import android.media.IRemoteVolumeController.Stub;
import android.media.MediaMetadata;
import android.media.session.ISessionController;
import android.media.session.MediaController;
import android.media.session.MediaController.Callback;
import android.media.session.MediaController.PlaybackInfo;
import android.media.session.MediaSession.QueueItem;
import android.media.session.MediaSession.Token;
import android.media.session.MediaSessionManager;
import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MediaSessions
{
  private static final String TAG = Util.logTag(MediaSessions.class);
  private final Callbacks mCallbacks;
  private final Context mContext;
  private final H mHandler;
  private boolean mInit;
  private final MediaSessionManager mMgr;
  private final Map<MediaSession.Token, MediaControllerRecord> mRecords = new HashMap();
  private final IRemoteVolumeController mRvc = new IRemoteVolumeController.Stub()
  {
    public void remoteVolumeChanged(ISessionController paramAnonymousISessionController, int paramAnonymousInt)
      throws RemoteException
    {
      MediaSessions.-get2(MediaSessions.this).obtainMessage(2, paramAnonymousInt, 0, paramAnonymousISessionController).sendToTarget();
    }
    
    public void updateRemoteController(ISessionController paramAnonymousISessionController)
      throws RemoteException
    {
      MediaSessions.-get2(MediaSessions.this).obtainMessage(3, paramAnonymousISessionController).sendToTarget();
    }
  };
  private final MediaSessionManager.OnActiveSessionsChangedListener mSessionsListener = new MediaSessionManager.OnActiveSessionsChangedListener()
  {
    public void onActiveSessionsChanged(List<MediaController> paramAnonymousList)
    {
      MediaSessions.this.onActiveSessionsUpdatedH(paramAnonymousList);
    }
  };
  
  public MediaSessions(Context paramContext, Looper paramLooper, Callbacks paramCallbacks)
  {
    this.mContext = paramContext;
    this.mHandler = new H(paramLooper, null);
    this.mMgr = ((MediaSessionManager)paramContext.getSystemService("media_session"));
    this.mCallbacks = paramCallbacks;
  }
  
  private static void dump(int paramInt, PrintWriter paramPrintWriter, MediaController paramMediaController)
  {
    paramPrintWriter.println("  Controller " + paramInt + ": " + paramMediaController.getPackageName());
    Object localObject1 = paramMediaController.getExtras();
    long l = paramMediaController.getFlags();
    Object localObject3 = paramMediaController.getMetadata();
    MediaController.PlaybackInfo localPlaybackInfo = paramMediaController.getPlaybackInfo();
    Object localObject4 = paramMediaController.getPlaybackState();
    Object localObject2 = paramMediaController.getQueue();
    CharSequence localCharSequence = paramMediaController.getQueueTitle();
    paramInt = paramMediaController.getRatingType();
    paramMediaController = paramMediaController.getSessionActivity();
    paramPrintWriter.println("    PlaybackState: " + Util.playbackStateToString((PlaybackState)localObject4));
    paramPrintWriter.println("    PlaybackInfo: " + Util.playbackInfoToString(localPlaybackInfo));
    if (localObject3 != null) {
      paramPrintWriter.println("  MediaMetadata.desc=" + ((MediaMetadata)localObject3).getDescription());
    }
    paramPrintWriter.println("    RatingType: " + paramInt);
    paramPrintWriter.println("    Flags: " + l);
    if (localObject1 != null)
    {
      paramPrintWriter.println("    Extras:");
      localObject3 = ((Bundle)localObject1).keySet().iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = (String)((Iterator)localObject3).next();
        paramPrintWriter.println("      " + (String)localObject4 + "=" + ((Bundle)localObject1).get((String)localObject4));
      }
    }
    if (localCharSequence != null) {
      paramPrintWriter.println("    QueueTitle: " + localCharSequence);
    }
    if ((localObject2 == null) || (((List)localObject2).isEmpty())) {}
    for (;;)
    {
      if (localPlaybackInfo != null) {
        paramPrintWriter.println("    sessionActivity: " + paramMediaController);
      }
      return;
      paramPrintWriter.println("    Queue:");
      localObject1 = ((Iterable)localObject2).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (MediaSession.QueueItem)((Iterator)localObject1).next();
        paramPrintWriter.println("      " + localObject2);
      }
    }
  }
  
  private static boolean isRemote(MediaController.PlaybackInfo paramPlaybackInfo)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramPlaybackInfo != null)
    {
      bool1 = bool2;
      if (paramPlaybackInfo.getPlaybackType() == 2) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private void onRemoteVolumeChangedH(ISessionController paramISessionController, int paramInt)
  {
    paramISessionController = new MediaController(this.mContext, paramISessionController);
    if (D.BUG) {
      Log.d(TAG, "remoteVolumeChangedH " + paramISessionController.getPackageName() + " " + Util.audioManagerFlagsToString(paramInt));
    }
    paramISessionController = paramISessionController.getSessionToken();
    if (this.mCallbacks != null) {
      this.mCallbacks.onRemoteVolumeChanged(paramISessionController, paramInt);
    }
  }
  
  private void onUpdateRemoteControllerH(ISessionController paramISessionController)
  {
    MediaController localMediaController = null;
    if (paramISessionController != null) {
      localMediaController = new MediaController(this.mContext, paramISessionController);
    }
    if (localMediaController != null) {}
    for (paramISessionController = localMediaController.getPackageName();; paramISessionController = null)
    {
      if (D.BUG) {
        Log.d(TAG, "updateRemoteControllerH " + paramISessionController);
      }
      postUpdateSessions();
      return;
    }
  }
  
  private void updateRemoteH(MediaSession.Token paramToken, String paramString, MediaController.PlaybackInfo paramPlaybackInfo)
  {
    if (this.mCallbacks != null) {
      this.mCallbacks.onRemoteUpdate(paramToken, paramString, paramPlaybackInfo);
    }
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println(getClass().getSimpleName() + " state:");
    paramPrintWriter.print("  mInit: ");
    paramPrintWriter.println(this.mInit);
    paramPrintWriter.print("  mRecords.size: ");
    paramPrintWriter.println(this.mRecords.size());
    int i = 0;
    Iterator localIterator = this.mRecords.values().iterator();
    while (localIterator.hasNext())
    {
      MediaControllerRecord localMediaControllerRecord = (MediaControllerRecord)localIterator.next();
      i += 1;
      dump(i, paramPrintWriter, MediaControllerRecord.-get0(localMediaControllerRecord));
    }
  }
  
  protected String getControllerName(MediaController paramMediaController)
  {
    Object localObject = this.mContext.getPackageManager();
    paramMediaController = paramMediaController.getPackageName();
    try
    {
      localObject = Objects.toString(((PackageManager)localObject).getApplicationInfo(paramMediaController, 0).loadLabel((PackageManager)localObject), "").trim();
      int i = ((String)localObject).length();
      if (i > 0) {
        return (String)localObject;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
    return paramMediaController;
  }
  
  public void init()
  {
    if (D.BUG) {
      Log.d(TAG, "init");
    }
    this.mMgr.addOnActiveSessionsChangedListener(this.mSessionsListener, null, this.mHandler);
    this.mInit = true;
    postUpdateSessions();
    this.mMgr.setRemoteVolumeController(this.mRvc);
  }
  
  protected void onActiveSessionsUpdatedH(List<MediaController> paramList)
  {
    if (D.BUG) {
      Log.d(TAG, "onActiveSessionsUpdatedH n=" + paramList.size());
    }
    Object localObject1 = new HashSet(this.mRecords.keySet());
    paramList = paramList.iterator();
    Object localObject2;
    while (paramList.hasNext())
    {
      Object localObject3 = (MediaController)paramList.next();
      localObject2 = ((MediaController)localObject3).getSessionToken();
      MediaController.PlaybackInfo localPlaybackInfo = ((MediaController)localObject3).getPlaybackInfo();
      ((Set)localObject1).remove(localObject2);
      if (!this.mRecords.containsKey(localObject2))
      {
        MediaControllerRecord localMediaControllerRecord = new MediaControllerRecord((MediaController)localObject3, null);
        MediaControllerRecord.-set0(localMediaControllerRecord, getControllerName((MediaController)localObject3));
        this.mRecords.put(localObject2, localMediaControllerRecord);
        ((MediaController)localObject3).registerCallback(localMediaControllerRecord, this.mHandler);
      }
      localObject3 = (MediaControllerRecord)this.mRecords.get(localObject2);
      if (isRemote(localPlaybackInfo))
      {
        updateRemoteH((MediaSession.Token)localObject2, MediaControllerRecord.-get1((MediaControllerRecord)localObject3), localPlaybackInfo);
        MediaControllerRecord.-set1((MediaControllerRecord)localObject3, true);
      }
    }
    paramList = ((Iterable)localObject1).iterator();
    while (paramList.hasNext())
    {
      localObject1 = (MediaSession.Token)paramList.next();
      localObject2 = (MediaControllerRecord)this.mRecords.get(localObject1);
      MediaControllerRecord.-get0((MediaControllerRecord)localObject2).unregisterCallback((MediaController.Callback)localObject2);
      this.mRecords.remove(localObject1);
      if (D.BUG) {
        Log.d(TAG, "Removing " + MediaControllerRecord.-get1((MediaControllerRecord)localObject2) + " sentRemote=" + MediaControllerRecord.-get2((MediaControllerRecord)localObject2));
      }
      if (MediaControllerRecord.-get2((MediaControllerRecord)localObject2))
      {
        if (this.mCallbacks != null) {
          this.mCallbacks.onRemoteRemoved((MediaSession.Token)localObject1);
        }
        MediaControllerRecord.-set1((MediaControllerRecord)localObject2, false);
      }
    }
  }
  
  protected void postUpdateSessions()
  {
    if (!this.mInit) {
      return;
    }
    this.mHandler.sendEmptyMessage(1);
  }
  
  public void setVolume(MediaSession.Token paramToken, int paramInt)
  {
    MediaControllerRecord localMediaControllerRecord = (MediaControllerRecord)this.mRecords.get(paramToken);
    if (localMediaControllerRecord == null)
    {
      Log.w(TAG, "setVolume: No record found for token " + paramToken);
      return;
    }
    if (D.BUG) {
      Log.d(TAG, "Setting level to " + paramInt);
    }
    MediaControllerRecord.-get0(localMediaControllerRecord).setVolumeTo(paramInt, 0);
  }
  
  public static abstract interface Callbacks
  {
    public abstract void onRemoteRemoved(MediaSession.Token paramToken);
    
    public abstract void onRemoteUpdate(MediaSession.Token paramToken, String paramString, MediaController.PlaybackInfo paramPlaybackInfo);
    
    public abstract void onRemoteVolumeChanged(MediaSession.Token paramToken, int paramInt);
  }
  
  private final class H
    extends Handler
  {
    private H(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        MediaSessions.this.onActiveSessionsUpdatedH(MediaSessions.-get3(MediaSessions.this).getActiveSessions(null));
        return;
      case 2: 
        MediaSessions.-wrap1(MediaSessions.this, (ISessionController)paramMessage.obj, paramMessage.arg1);
        return;
      }
      MediaSessions.-wrap2(MediaSessions.this, (ISessionController)paramMessage.obj);
    }
  }
  
  private final class MediaControllerRecord
    extends MediaController.Callback
  {
    private final MediaController controller;
    private String name;
    private boolean sentRemote;
    
    private MediaControllerRecord(MediaController paramMediaController)
    {
      this.controller = paramMediaController;
    }
    
    private String cb(String paramString)
    {
      return paramString + " " + this.controller.getPackageName() + " ";
    }
    
    public void onAudioInfoChanged(MediaController.PlaybackInfo paramPlaybackInfo)
    {
      if (D.BUG) {
        Log.d(MediaSessions.-get0(), cb("onAudioInfoChanged") + Util.playbackInfoToString(paramPlaybackInfo) + " sentRemote=" + this.sentRemote);
      }
      boolean bool = MediaSessions.-wrap0(paramPlaybackInfo);
      if ((!bool) && (this.sentRemote))
      {
        if (MediaSessions.-get1(MediaSessions.this) != null) {
          MediaSessions.-get1(MediaSessions.this).onRemoteRemoved(this.controller.getSessionToken());
        }
        this.sentRemote = false;
      }
      while (!bool) {
        return;
      }
      MediaSessions.-wrap3(MediaSessions.this, this.controller.getSessionToken(), this.name, paramPlaybackInfo);
      this.sentRemote = true;
    }
    
    public void onExtrasChanged(Bundle paramBundle)
    {
      if (D.BUG) {
        Log.d(MediaSessions.-get0(), cb("onExtrasChanged") + paramBundle);
      }
    }
    
    public void onMetadataChanged(MediaMetadata paramMediaMetadata)
    {
      if (D.BUG) {
        Log.d(MediaSessions.-get0(), cb("onMetadataChanged") + Util.mediaMetadataToString(paramMediaMetadata));
      }
    }
    
    public void onPlaybackStateChanged(PlaybackState paramPlaybackState)
    {
      if (D.BUG) {
        Log.d(MediaSessions.-get0(), cb("onPlaybackStateChanged") + Util.playbackStateToString(paramPlaybackState));
      }
    }
    
    public void onQueueChanged(List<MediaSession.QueueItem> paramList)
    {
      if (D.BUG) {
        Log.d(MediaSessions.-get0(), cb("onQueueChanged") + paramList);
      }
    }
    
    public void onQueueTitleChanged(CharSequence paramCharSequence)
    {
      if (D.BUG) {
        Log.d(MediaSessions.-get0(), cb("onQueueTitleChanged") + paramCharSequence);
      }
    }
    
    public void onSessionDestroyed()
    {
      if (D.BUG) {
        Log.d(MediaSessions.-get0(), cb("onSessionDestroyed"));
      }
    }
    
    public void onSessionEvent(String paramString, Bundle paramBundle)
    {
      if (D.BUG) {
        Log.d(MediaSessions.-get0(), cb("onSessionEvent") + "event=" + paramString + " extras=" + paramBundle);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\volume\MediaSessions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */