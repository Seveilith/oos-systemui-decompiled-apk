package com.android.systemui.media;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.IAudioService;
import android.media.IAudioService.Stub;
import android.media.IRingtonePlayer;
import android.media.IRingtonePlayer.Stub;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import com.android.systemui.SystemUI;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;

public class RingtonePlayer
  extends SystemUI
{
  private final NotificationPlayer mAsyncPlayer = new NotificationPlayer("RingtonePlayer");
  private IAudioService mAudioService;
  private IRingtonePlayer mCallback = new IRingtonePlayer.Stub()
  {
    public String getTitle(Uri paramAnonymousUri)
    {
      UserHandle localUserHandle = Binder.getCallingUserHandle();
      return Ringtone.getTitle(RingtonePlayer.-wrap0(RingtonePlayer.this, localUserHandle), paramAnonymousUri, false, false);
    }
    
    public boolean isPlaying(IBinder paramAnonymousIBinder)
    {
      synchronized (RingtonePlayer.-get1(RingtonePlayer.this))
      {
        paramAnonymousIBinder = (RingtonePlayer.Client)RingtonePlayer.-get1(RingtonePlayer.this).get(paramAnonymousIBinder);
        if (paramAnonymousIBinder != null) {
          return RingtonePlayer.Client.-get0(paramAnonymousIBinder).isPlaying();
        }
      }
      return false;
    }
    
    public ParcelFileDescriptor openRingtone(Uri paramAnonymousUri)
    {
      ParcelFileDescriptor localParcelFileDescriptor = null;
      Object localObject6 = null;
      localObject1 = Binder.getCallingUserHandle();
      ContentResolver localContentResolver = RingtonePlayer.-wrap0(RingtonePlayer.this, (UserHandle)localObject1).getContentResolver();
      if (paramAnonymousUri.toString().startsWith(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()))
      {
        localObject3 = null;
        localObject1 = null;
        for (;;)
        {
          try
          {
            Cursor localCursor = localContentResolver.query(paramAnonymousUri, new String[] { "is_ringtone", "is_alarm", "is_notification" }, null, null, null);
            localObject1 = localCursor;
            localObject3 = localCursor;
            int i;
            if (localCursor.moveToFirst())
            {
              localObject1 = localCursor;
              localObject3 = localCursor;
              if (localCursor.getInt(0) == 0)
              {
                localObject1 = localCursor;
                localObject3 = localCursor;
                i = localCursor.getInt(1);
                if (i == 0) {}
              }
              else
              {
                localObject1 = localCursor;
                localObject3 = localCursor;
              }
            }
            try
            {
              localParcelFileDescriptor = localContentResolver.openFileDescriptor(paramAnonymousUri, "r");
              paramAnonymousUri = (Uri)localObject6;
              if (localCursor != null) {}
              try
              {
                localCursor.close();
                paramAnonymousUri = (Uri)localObject6;
              }
              catch (Throwable paramAnonymousUri)
              {
                continue;
              }
              if (paramAnonymousUri != null)
              {
                throw paramAnonymousUri;
                localObject1 = localCursor;
                localObject3 = localCursor;
                i = localCursor.getInt(2);
                if (i != 0) {
                  continue;
                }
                localObject1 = localParcelFileDescriptor;
                if (localCursor == null) {}
              }
            }
            catch (IOException paramAnonymousUri)
            {
              localObject1 = localCursor;
              localObject3 = localCursor;
              throw new SecurityException(paramAnonymousUri);
            }
          }
          catch (Throwable paramAnonymousUri)
          {
            try
            {
              throw paramAnonymousUri;
            }
            finally
            {
              localObject3 = localObject1;
            }
            Object localObject5 = paramAnonymousUri;
            if (localObject3 != null) {}
            try
            {
              ((Cursor)localObject3).close();
              localObject5 = paramAnonymousUri;
            }
            catch (Throwable localThrowable2)
            {
              if (paramAnonymousUri != null) {
                continue;
              }
              localObject5 = localThrowable2;
              continue;
              localObject5 = paramAnonymousUri;
              if (paramAnonymousUri == localThrowable2) {
                continue;
              }
              paramAnonymousUri.addSuppressed(localThrowable2);
              localObject5 = paramAnonymousUri;
              continue;
            }
            if (localObject5 != null) {
              throw ((Throwable)localObject5);
            }
            throw new SecurityException("Uri is not ringtone, alarm, or notification: " + paramAnonymousUri);
          }
          finally
          {
            paramAnonymousUri = null;
            continue;
          }
          try
          {
            localCursor.close();
            localObject1 = localParcelFileDescriptor;
          }
          catch (Throwable localThrowable1)
          {
            continue;
            throw localThrowable1;
          }
        }
        if (localObject1 != null)
        {
          throw ((Throwable)localObject1);
          return localParcelFileDescriptor;
        }
      }
    }
    
    public void play(IBinder paramAnonymousIBinder, Uri paramAnonymousUri, AudioAttributes paramAnonymousAudioAttributes, float paramAnonymousFloat, boolean paramAnonymousBoolean)
      throws RemoteException
    {
      synchronized (RingtonePlayer.-get1(RingtonePlayer.this))
      {
        RingtonePlayer.Client localClient = (RingtonePlayer.Client)RingtonePlayer.-get1(RingtonePlayer.this).get(paramAnonymousIBinder);
        Object localObject = localClient;
        if (localClient == null)
        {
          localObject = Binder.getCallingUserHandle();
          localObject = new RingtonePlayer.Client(RingtonePlayer.this, paramAnonymousIBinder, paramAnonymousUri, (UserHandle)localObject, paramAnonymousAudioAttributes);
          paramAnonymousIBinder.linkToDeath((IBinder.DeathRecipient)localObject, 0);
          RingtonePlayer.-get1(RingtonePlayer.this).put(paramAnonymousIBinder, localObject);
        }
        RingtonePlayer.Client.-get0((RingtonePlayer.Client)localObject).setLooping(paramAnonymousBoolean);
        RingtonePlayer.Client.-get0((RingtonePlayer.Client)localObject).setVolume(paramAnonymousFloat);
        RingtonePlayer.Client.-get0((RingtonePlayer.Client)localObject).play();
        return;
      }
    }
    
    public void playAsync(Uri paramAnonymousUri, UserHandle paramAnonymousUserHandle, boolean paramAnonymousBoolean, AudioAttributes paramAnonymousAudioAttributes)
    {
      if (Binder.getCallingUid() != 1000) {
        throw new SecurityException("Async playback only available from system UID.");
      }
      UserHandle localUserHandle;
      if (!UserHandle.ALL.equals(paramAnonymousUserHandle))
      {
        localUserHandle = paramAnonymousUserHandle;
        if (!UserHandle.PARALLEL.equals(paramAnonymousUserHandle)) {}
      }
      else
      {
        localUserHandle = UserHandle.SYSTEM;
      }
      RingtonePlayer.-get0(RingtonePlayer.this).play(RingtonePlayer.-wrap0(RingtonePlayer.this, localUserHandle), paramAnonymousUri, paramAnonymousBoolean, paramAnonymousAudioAttributes);
    }
    
    public void setPlaybackProperties(IBinder paramAnonymousIBinder, float paramAnonymousFloat, boolean paramAnonymousBoolean)
    {
      synchronized (RingtonePlayer.-get1(RingtonePlayer.this))
      {
        paramAnonymousIBinder = (RingtonePlayer.Client)RingtonePlayer.-get1(RingtonePlayer.this).get(paramAnonymousIBinder);
        if (paramAnonymousIBinder != null)
        {
          RingtonePlayer.Client.-get0(paramAnonymousIBinder).setVolume(paramAnonymousFloat);
          RingtonePlayer.Client.-get0(paramAnonymousIBinder).setLooping(paramAnonymousBoolean);
        }
        return;
      }
    }
    
    public void stop(IBinder paramAnonymousIBinder)
    {
      synchronized (RingtonePlayer.-get1(RingtonePlayer.this))
      {
        paramAnonymousIBinder = (RingtonePlayer.Client)RingtonePlayer.-get1(RingtonePlayer.this).remove(paramAnonymousIBinder);
        if (paramAnonymousIBinder != null)
        {
          RingtonePlayer.Client.-get1(paramAnonymousIBinder).unlinkToDeath(paramAnonymousIBinder, 0);
          RingtonePlayer.Client.-get0(paramAnonymousIBinder).stop();
        }
        return;
      }
    }
    
    public void stopAsync()
    {
      if (Binder.getCallingUid() != 1000) {
        throw new SecurityException("Async playback only available from system UID.");
      }
      RingtonePlayer.-get0(RingtonePlayer.this).stop();
    }
  };
  private final HashMap<IBinder, Client> mClients = new HashMap();
  
  private Context getContextForUser(UserHandle paramUserHandle)
  {
    try
    {
      paramUserHandle = this.mContext.createPackageContextAsUser(this.mContext.getPackageName(), 0, paramUserHandle);
      return paramUserHandle;
    }
    catch (PackageManager.NameNotFoundException paramUserHandle)
    {
      throw new RuntimeException(paramUserHandle);
    }
  }
  
  public void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("Clients:");
    synchronized (this.mClients)
    {
      paramArrayOfString = this.mClients.values().iterator();
      if (paramArrayOfString.hasNext())
      {
        Client localClient = (Client)paramArrayOfString.next();
        paramPrintWriter.print("  mToken=");
        paramPrintWriter.print(Client.-get1(localClient));
        paramPrintWriter.print(" mUri=");
        paramPrintWriter.println(Client.-get0(localClient).getUri());
      }
    }
  }
  
  public void start()
  {
    this.mAsyncPlayer.setUsesWakeLock(this.mContext);
    this.mAudioService = IAudioService.Stub.asInterface(ServiceManager.getService("audio"));
    try
    {
      this.mAudioService.setRingtonePlayer(this.mCallback);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("RingtonePlayer", "Problem registering RingtonePlayer: " + localRemoteException);
    }
  }
  
  private class Client
    implements IBinder.DeathRecipient
  {
    private final Ringtone mRingtone;
    private final IBinder mToken;
    
    public Client(IBinder paramIBinder, Uri paramUri, UserHandle paramUserHandle, AudioAttributes paramAudioAttributes)
    {
      this.mToken = paramIBinder;
      this.mRingtone = new Ringtone(RingtonePlayer.-wrap0(RingtonePlayer.this, paramUserHandle), false);
      this.mRingtone.setAudioAttributes(paramAudioAttributes);
      this.mRingtone.setUri(paramUri);
    }
    
    public void binderDied()
    {
      synchronized (RingtonePlayer.-get1(RingtonePlayer.this))
      {
        RingtonePlayer.-get1(RingtonePlayer.this).remove(this.mToken);
        this.mRingtone.stop();
        return;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\media\RingtonePlayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */