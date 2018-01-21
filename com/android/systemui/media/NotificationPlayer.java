package com.android.systemui.media;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;
import java.util.LinkedList;

public class NotificationPlayer
  implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener
{
  private AudioManager mAudioManagerWithAudioFocus;
  private LinkedList<Command> mCmdQueue = new LinkedList();
  private final Object mCompletionHandlingLock = new Object();
  private CreationAndCompletionThread mCompletionThread;
  private Looper mLooper;
  private MediaPlayer mPlayer;
  private final Object mQueueAudioFocusLock = new Object();
  private int mState = 2;
  private String mTag;
  private CmdThread mThread;
  private PowerManager.WakeLock mWakeLock;
  
  public NotificationPlayer(String paramString)
  {
    if (paramString != null)
    {
      this.mTag = paramString;
      return;
    }
    this.mTag = "NotificationPlayer";
  }
  
  private void acquireWakeLock()
  {
    if (this.mWakeLock != null) {
      this.mWakeLock.acquire();
    }
  }
  
  private void enqueueLocked(Command paramCommand)
  {
    this.mCmdQueue.add(paramCommand);
    if (this.mThread == null)
    {
      acquireWakeLock();
      this.mThread = new CmdThread();
      this.mThread.start();
    }
  }
  
  private void releaseWakeLock()
  {
    if (this.mWakeLock != null) {
      this.mWakeLock.release();
    }
  }
  
  private void startSound(Command paramCommand)
  {
    try
    {
      synchronized (this.mCompletionHandlingLock)
      {
        if ((this.mLooper != null) && (this.mLooper.getThread().getState() != Thread.State.TERMINATED)) {
          this.mLooper.quit();
        }
        this.mCompletionThread = new CreationAndCompletionThread(paramCommand);
        synchronized (this.mCompletionThread)
        {
          this.mCompletionThread.start();
          this.mCompletionThread.wait();
          long l = SystemClock.uptimeMillis() - paramCommand.requestTime;
          if (l > 1000L) {
            Log.w(this.mTag, "Notification sound delayed by " + l + "msecs");
          }
          return;
        }
      }
      return;
    }
    catch (Exception localException)
    {
      Log.w(this.mTag, "error loading sound for " + paramCommand.uri, localException);
    }
  }
  
  public void onCompletion(MediaPlayer arg1)
  {
    synchronized (this.mQueueAudioFocusLock)
    {
      if (this.mAudioManagerWithAudioFocus != null)
      {
        this.mAudioManagerWithAudioFocus.abandonAudioFocus(null);
        this.mAudioManagerWithAudioFocus = null;
      }
    }
    synchronized (this.mCmdQueue)
    {
      if (this.mCmdQueue.size() == 0) {}
      synchronized (this.mCompletionHandlingLock)
      {
        if (this.mLooper != null) {
          this.mLooper.quit();
        }
        this.mCompletionThread = null;
        return;
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
    }
  }
  
  public boolean onError(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2)
  {
    Log.e(this.mTag, "error " + paramInt1 + " (extra=" + paramInt2 + ") playing notification");
    onCompletion(paramMediaPlayer);
    return true;
  }
  
  public void play(Context arg1, Uri paramUri, boolean paramBoolean, AudioAttributes paramAudioAttributes)
  {
    Command localCommand = new Command(null);
    localCommand.requestTime = SystemClock.uptimeMillis();
    localCommand.code = 1;
    localCommand.context = ???;
    localCommand.uri = paramUri;
    localCommand.looping = paramBoolean;
    localCommand.attributes = paramAudioAttributes;
    synchronized (this.mCmdQueue)
    {
      enqueueLocked(localCommand);
      this.mState = 1;
      return;
    }
  }
  
  public void setUsesWakeLock(Context paramContext)
  {
    if ((this.mWakeLock != null) || (this.mThread != null)) {
      throw new RuntimeException("assertion failed mWakeLock=" + this.mWakeLock + " mThread=" + this.mThread);
    }
    this.mWakeLock = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(1, this.mTag);
  }
  
  public void stop()
  {
    synchronized (this.mCmdQueue)
    {
      if (this.mState != 2)
      {
        Command localCommand = new Command(null);
        localCommand.requestTime = SystemClock.uptimeMillis();
        localCommand.code = 2;
        enqueueLocked(localCommand);
        this.mState = 2;
      }
      return;
    }
  }
  
  private final class CmdThread
    extends Thread
  {
    CmdThread()
    {
      super();
    }
    
    public void run()
    {
      synchronized (NotificationPlayer.-get1(NotificationPlayer.this))
      {
        NotificationPlayer.Command localCommand1 = (NotificationPlayer.Command)NotificationPlayer.-get1(NotificationPlayer.this).removeFirst();
        switch (localCommand1.code)
        {
        }
      }
      synchronized (NotificationPlayer.-get1(NotificationPlayer.this))
      {
        while (NotificationPlayer.-get1(NotificationPlayer.this).size() == 0)
        {
          NotificationPlayer.-set3(NotificationPlayer.this, null);
          NotificationPlayer.-wrap0(NotificationPlayer.this);
          return;
          localCommand2 = finally;
          throw localCommand2;
          NotificationPlayer.-wrap1(NotificationPlayer.this, localCommand2);
        }
        if (NotificationPlayer.-get3(NotificationPlayer.this) != null)
        {
          long l = SystemClock.uptimeMillis() - localCommand2.requestTime;
          if (l > 1000L) {
            Log.w(NotificationPlayer.-get5(NotificationPlayer.this), "Notification stop delayed by " + l + "msecs");
          }
          NotificationPlayer.-get3(NotificationPlayer.this).stop();
          NotificationPlayer.-get3(NotificationPlayer.this).release();
          NotificationPlayer.-set2(NotificationPlayer.this, null);
        }
      }
    }
  }
  
  private static final class Command
  {
    AudioAttributes attributes;
    int code;
    Context context;
    boolean looping;
    long requestTime;
    Uri uri;
    
    public String toString()
    {
      return "{ code=" + this.code + " looping=" + this.looping + " attributes=" + this.attributes + " uri=" + this.uri + " }";
    }
  }
  
  private final class CreationAndCompletionThread
    extends Thread
  {
    public NotificationPlayer.Command mCmd;
    
    public CreationAndCompletionThread(NotificationPlayer.Command paramCommand)
    {
      this.mCmd = paramCommand;
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: invokestatic 29	android/os/Looper:prepare	()V
      //   3: aload_0
      //   4: getfield 15	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:this$0	Lcom/android/systemui/media/NotificationPlayer;
      //   7: invokestatic 33	android/os/Looper:myLooper	()Landroid/os/Looper;
      //   10: invokestatic 37	com/android/systemui/media/NotificationPlayer:-set1	(Lcom/android/systemui/media/NotificationPlayer;Landroid/os/Looper;)Landroid/os/Looper;
      //   13: pop
      //   14: aload_0
      //   15: monitorenter
      //   16: aload_0
      //   17: getfield 20	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:mCmd	Lcom/android/systemui/media/NotificationPlayer$Command;
      //   20: getfield 43	com/android/systemui/media/NotificationPlayer$Command:context	Landroid/content/Context;
      //   23: ldc 45
      //   25: invokevirtual 51	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
      //   28: checkcast 53	android/media/AudioManager
      //   31: astore_2
      //   32: new 55	android/media/MediaPlayer
      //   35: dup
      //   36: invokespecial 56	android/media/MediaPlayer:<init>	()V
      //   39: astore_3
      //   40: aload_3
      //   41: aload_0
      //   42: getfield 20	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:mCmd	Lcom/android/systemui/media/NotificationPlayer$Command;
      //   45: getfield 60	com/android/systemui/media/NotificationPlayer$Command:attributes	Landroid/media/AudioAttributes;
      //   48: invokevirtual 64	android/media/MediaPlayer:setAudioAttributes	(Landroid/media/AudioAttributes;)V
      //   51: aload_3
      //   52: aload_0
      //   53: getfield 20	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:mCmd	Lcom/android/systemui/media/NotificationPlayer$Command;
      //   56: getfield 43	com/android/systemui/media/NotificationPlayer$Command:context	Landroid/content/Context;
      //   59: aload_0
      //   60: getfield 20	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:mCmd	Lcom/android/systemui/media/NotificationPlayer$Command;
      //   63: getfield 68	com/android/systemui/media/NotificationPlayer$Command:uri	Landroid/net/Uri;
      //   66: invokevirtual 72	android/media/MediaPlayer:setDataSource	(Landroid/content/Context;Landroid/net/Uri;)V
      //   69: aload_3
      //   70: aload_0
      //   71: getfield 20	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:mCmd	Lcom/android/systemui/media/NotificationPlayer$Command;
      //   74: getfield 76	com/android/systemui/media/NotificationPlayer$Command:looping	Z
      //   77: invokevirtual 80	android/media/MediaPlayer:setLooping	(Z)V
      //   80: aload_3
      //   81: invokevirtual 81	android/media/MediaPlayer:prepare	()V
      //   84: aload_0
      //   85: getfield 20	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:mCmd	Lcom/android/systemui/media/NotificationPlayer$Command;
      //   88: getfield 68	com/android/systemui/media/NotificationPlayer$Command:uri	Landroid/net/Uri;
      //   91: ifnull +97 -> 188
      //   94: aload_0
      //   95: getfield 20	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:mCmd	Lcom/android/systemui/media/NotificationPlayer$Command;
      //   98: getfield 68	com/android/systemui/media/NotificationPlayer$Command:uri	Landroid/net/Uri;
      //   101: invokevirtual 87	android/net/Uri:getEncodedPath	()Ljava/lang/String;
      //   104: ifnull +84 -> 188
      //   107: aload_0
      //   108: getfield 20	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:mCmd	Lcom/android/systemui/media/NotificationPlayer$Command;
      //   111: getfield 68	com/android/systemui/media/NotificationPlayer$Command:uri	Landroid/net/Uri;
      //   114: invokevirtual 87	android/net/Uri:getEncodedPath	()Ljava/lang/String;
      //   117: invokevirtual 93	java/lang/String:length	()I
      //   120: ifle +68 -> 188
      //   123: aload_2
      //   124: invokevirtual 97	android/media/AudioManager:isMusicActiveRemotely	()Z
      //   127: ifne +61 -> 188
      //   130: aload_0
      //   131: getfield 15	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:this$0	Lcom/android/systemui/media/NotificationPlayer;
      //   134: invokestatic 101	com/android/systemui/media/NotificationPlayer:-get4	(Lcom/android/systemui/media/NotificationPlayer;)Ljava/lang/Object;
      //   137: astore_1
      //   138: aload_1
      //   139: monitorenter
      //   140: aload_0
      //   141: getfield 15	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:this$0	Lcom/android/systemui/media/NotificationPlayer;
      //   144: invokestatic 105	com/android/systemui/media/NotificationPlayer:-get0	(Lcom/android/systemui/media/NotificationPlayer;)Landroid/media/AudioManager;
      //   147: ifnonnull +39 -> 186
      //   150: aload_0
      //   151: getfield 20	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:mCmd	Lcom/android/systemui/media/NotificationPlayer$Command;
      //   154: getfield 76	com/android/systemui/media/NotificationPlayer$Command:looping	Z
      //   157: ifeq +90 -> 247
      //   160: aload_2
      //   161: aconst_null
      //   162: aload_0
      //   163: getfield 20	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:mCmd	Lcom/android/systemui/media/NotificationPlayer$Command;
      //   166: getfield 60	com/android/systemui/media/NotificationPlayer$Command:attributes	Landroid/media/AudioAttributes;
      //   169: invokestatic 111	android/media/AudioAttributes:toLegacyStreamType	(Landroid/media/AudioAttributes;)I
      //   172: iconst_1
      //   173: invokevirtual 115	android/media/AudioManager:requestAudioFocus	(Landroid/media/AudioManager$OnAudioFocusChangeListener;II)I
      //   176: pop
      //   177: aload_0
      //   178: getfield 15	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:this$0	Lcom/android/systemui/media/NotificationPlayer;
      //   181: aload_2
      //   182: invokestatic 119	com/android/systemui/media/NotificationPlayer:-set0	(Lcom/android/systemui/media/NotificationPlayer;Landroid/media/AudioManager;)Landroid/media/AudioManager;
      //   185: pop
      //   186: aload_1
      //   187: monitorexit
      //   188: aload_3
      //   189: aload_0
      //   190: getfield 15	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:this$0	Lcom/android/systemui/media/NotificationPlayer;
      //   193: invokevirtual 123	android/media/MediaPlayer:setOnCompletionListener	(Landroid/media/MediaPlayer$OnCompletionListener;)V
      //   196: aload_3
      //   197: aload_0
      //   198: getfield 15	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:this$0	Lcom/android/systemui/media/NotificationPlayer;
      //   201: invokevirtual 127	android/media/MediaPlayer:setOnErrorListener	(Landroid/media/MediaPlayer$OnErrorListener;)V
      //   204: aload_3
      //   205: invokevirtual 130	android/media/MediaPlayer:start	()V
      //   208: aload_0
      //   209: getfield 15	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:this$0	Lcom/android/systemui/media/NotificationPlayer;
      //   212: invokestatic 134	com/android/systemui/media/NotificationPlayer:-get3	(Lcom/android/systemui/media/NotificationPlayer;)Landroid/media/MediaPlayer;
      //   215: ifnull +13 -> 228
      //   218: aload_0
      //   219: getfield 15	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:this$0	Lcom/android/systemui/media/NotificationPlayer;
      //   222: invokestatic 134	com/android/systemui/media/NotificationPlayer:-get3	(Lcom/android/systemui/media/NotificationPlayer;)Landroid/media/MediaPlayer;
      //   225: invokevirtual 137	android/media/MediaPlayer:release	()V
      //   228: aload_0
      //   229: getfield 15	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:this$0	Lcom/android/systemui/media/NotificationPlayer;
      //   232: aload_3
      //   233: invokestatic 141	com/android/systemui/media/NotificationPlayer:-set2	(Lcom/android/systemui/media/NotificationPlayer;Landroid/media/MediaPlayer;)Landroid/media/MediaPlayer;
      //   236: pop
      //   237: aload_0
      //   238: invokevirtual 144	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:notify	()V
      //   241: aload_0
      //   242: monitorexit
      //   243: invokestatic 147	android/os/Looper:loop	()V
      //   246: return
      //   247: aload_2
      //   248: aconst_null
      //   249: aload_0
      //   250: getfield 20	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:mCmd	Lcom/android/systemui/media/NotificationPlayer$Command;
      //   253: getfield 60	com/android/systemui/media/NotificationPlayer$Command:attributes	Landroid/media/AudioAttributes;
      //   256: invokestatic 111	android/media/AudioAttributes:toLegacyStreamType	(Landroid/media/AudioAttributes;)I
      //   259: iconst_3
      //   260: invokevirtual 115	android/media/AudioManager:requestAudioFocus	(Landroid/media/AudioManager$OnAudioFocusChangeListener;II)I
      //   263: pop
      //   264: goto -87 -> 177
      //   267: astore_2
      //   268: aload_1
      //   269: monitorexit
      //   270: aload_2
      //   271: athrow
      //   272: astore_1
      //   273: aload_0
      //   274: getfield 15	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:this$0	Lcom/android/systemui/media/NotificationPlayer;
      //   277: invokestatic 151	com/android/systemui/media/NotificationPlayer:-get5	(Lcom/android/systemui/media/NotificationPlayer;)Ljava/lang/String;
      //   280: new 153	java/lang/StringBuilder
      //   283: dup
      //   284: invokespecial 154	java/lang/StringBuilder:<init>	()V
      //   287: ldc -100
      //   289: invokevirtual 160	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   292: aload_0
      //   293: getfield 20	com/android/systemui/media/NotificationPlayer$CreationAndCompletionThread:mCmd	Lcom/android/systemui/media/NotificationPlayer$Command;
      //   296: getfield 68	com/android/systemui/media/NotificationPlayer$Command:uri	Landroid/net/Uri;
      //   299: invokevirtual 163	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   302: invokevirtual 166	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   305: aload_1
      //   306: invokestatic 172	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   309: pop
      //   310: goto -73 -> 237
      //   313: astore_1
      //   314: aload_0
      //   315: monitorexit
      //   316: aload_1
      //   317: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	318	0	this	CreationAndCompletionThread
      //   272	34	1	localException	Exception
      //   313	4	1	localObject2	Object
      //   31	217	2	localAudioManager	AudioManager
      //   267	4	2	localObject3	Object
      //   39	194	3	localMediaPlayer	MediaPlayer
      // Exception table:
      //   from	to	target	type
      //   140	177	267	finally
      //   177	186	267	finally
      //   247	264	267	finally
      //   32	140	272	java/lang/Exception
      //   186	188	272	java/lang/Exception
      //   188	228	272	java/lang/Exception
      //   228	237	272	java/lang/Exception
      //   268	272	272	java/lang/Exception
      //   16	32	313	finally
      //   32	140	313	finally
      //   186	188	313	finally
      //   188	228	313	finally
      //   228	237	313	finally
      //   237	241	313	finally
      //   268	272	313	finally
      //   273	310	313	finally
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\media\NotificationPlayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */