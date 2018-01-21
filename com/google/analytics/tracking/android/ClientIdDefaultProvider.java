package com.google.analytics.tracking.android;

import android.content.Context;
import com.google.android.gms.common.util.VisibleForTesting;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

class ClientIdDefaultProvider
  implements DefaultProvider
{
  private static ClientIdDefaultProvider sInstance;
  private static final Object sInstanceLock = new Object();
  private String mClientId;
  private boolean mClientIdLoaded = false;
  private final Object mClientIdLock = new Object();
  private final Context mContext;
  
  protected ClientIdDefaultProvider(Context paramContext)
  {
    this.mContext = paramContext;
    asyncInitializeClientId();
  }
  
  private void asyncInitializeClientId()
  {
    new Thread("client_id_fetcher")
    {
      public void run()
      {
        synchronized (ClientIdDefaultProvider.this.mClientIdLock)
        {
          ClientIdDefaultProvider.access$102(ClientIdDefaultProvider.this, ClientIdDefaultProvider.this.initializeClientId());
          ClientIdDefaultProvider.access$202(ClientIdDefaultProvider.this, true);
          ClientIdDefaultProvider.this.mClientIdLock.notifyAll();
          return;
        }
      }
    }.start();
  }
  
  private String blockingGetClientId()
  {
    if (this.mClientIdLoaded)
    {
      Log.v("Loaded clientId");
      return this.mClientId;
    }
    for (;;)
    {
      synchronized (this.mClientIdLock)
      {
        if (!this.mClientIdLoaded) {}
      }
      Log.v("Waiting for clientId to load");
      try
      {
        for (;;)
        {
          this.mClientIdLock.wait();
          if (this.mClientIdLoaded) {
            break;
          }
        }
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          Log.e("Exception while waiting for clientId: " + localInterruptedException);
        }
      }
    }
  }
  
  @VisibleForTesting
  static void dropInstance()
  {
    synchronized (sInstanceLock)
    {
      sInstance = null;
      return;
    }
  }
  
  public static ClientIdDefaultProvider getProvider()
  {
    synchronized (sInstanceLock)
    {
      ClientIdDefaultProvider localClientIdDefaultProvider = sInstance;
      return localClientIdDefaultProvider;
    }
  }
  
  public static void initializeProvider(Context paramContext)
  {
    synchronized (sInstanceLock)
    {
      if (sInstance != null) {
        return;
      }
      sInstance = new ClientIdDefaultProvider(paramContext);
    }
  }
  
  private boolean storeClientId(String paramString)
  {
    try
    {
      Log.v("Storing clientId.");
      FileOutputStream localFileOutputStream = this.mContext.openFileOutput("gaClientId", 0);
      localFileOutputStream.write(paramString.getBytes());
      localFileOutputStream.close();
      return true;
    }
    catch (FileNotFoundException paramString)
    {
      Log.e("Error creating clientId file.");
      return false;
    }
    catch (IOException paramString)
    {
      Log.e("Error writing to clientId file.");
    }
    return false;
  }
  
  protected String generateClientId()
  {
    String str = UUID.randomUUID().toString().toLowerCase();
    if (storeClientId(str)) {
      return str;
    }
    return "0";
  }
  
  public String getValue(String paramString)
  {
    if (!"&cid".equals(paramString)) {
      return null;
    }
    return blockingGetClientId();
  }
  
  /* Error */
  @VisibleForTesting
  String initializeClientId()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aconst_null
    //   6: astore_2
    //   7: aload_0
    //   8: getfield 34	com/google/analytics/tracking/android/ClientIdDefaultProvider:mContext	Landroid/content/Context;
    //   11: ldc 110
    //   13: invokevirtual 165	android/content/Context:openFileInput	(Ljava/lang/String;)Ljava/io/FileInputStream;
    //   16: astore 5
    //   18: sipush 128
    //   21: newarray <illegal type>
    //   23: astore 6
    //   25: aload 5
    //   27: aload 6
    //   29: iconst_0
    //   30: sipush 128
    //   33: invokevirtual 171	java/io/FileInputStream:read	([BII)I
    //   36: istore_1
    //   37: aload 5
    //   39: invokevirtual 175	java/io/FileInputStream:available	()I
    //   42: ifgt +30 -> 72
    //   45: iload_1
    //   46: ifle +49 -> 95
    //   49: new 118	java/lang/String
    //   52: dup
    //   53: aload 6
    //   55: iconst_0
    //   56: iload_1
    //   57: invokespecial 178	java/lang/String:<init>	([BII)V
    //   60: astore_2
    //   61: aload 5
    //   63: invokevirtual 179	java/io/FileInputStream:close	()V
    //   66: aload_2
    //   67: ifnull +72 -> 139
    //   70: aload_2
    //   71: areturn
    //   72: ldc -75
    //   74: invokestatic 90	com/google/analytics/tracking/android/Log:e	(Ljava/lang/String;)V
    //   77: aload 5
    //   79: invokevirtual 179	java/io/FileInputStream:close	()V
    //   82: aload_0
    //   83: getfield 34	com/google/analytics/tracking/android/ClientIdDefaultProvider:mContext	Landroid/content/Context;
    //   86: ldc 110
    //   88: invokevirtual 184	android/content/Context:deleteFile	(Ljava/lang/String;)Z
    //   91: pop
    //   92: goto -26 -> 66
    //   95: ldc -70
    //   97: invokestatic 90	com/google/analytics/tracking/android/Log:e	(Ljava/lang/String;)V
    //   100: aload 5
    //   102: invokevirtual 179	java/io/FileInputStream:close	()V
    //   105: aload_0
    //   106: getfield 34	com/google/analytics/tracking/android/ClientIdDefaultProvider:mContext	Landroid/content/Context;
    //   109: ldc 110
    //   111: invokevirtual 184	android/content/Context:deleteFile	(Ljava/lang/String;)Z
    //   114: pop
    //   115: goto -49 -> 66
    //   118: astore_2
    //   119: aload_3
    //   120: astore_2
    //   121: ldc -68
    //   123: invokestatic 90	com/google/analytics/tracking/android/Log:e	(Ljava/lang/String;)V
    //   126: aload_0
    //   127: getfield 34	com/google/analytics/tracking/android/ClientIdDefaultProvider:mContext	Landroid/content/Context;
    //   130: ldc 110
    //   132: invokevirtual 184	android/content/Context:deleteFile	(Ljava/lang/String;)Z
    //   135: pop
    //   136: goto -70 -> 66
    //   139: aload_0
    //   140: invokevirtual 190	com/google/analytics/tracking/android/ClientIdDefaultProvider:generateClientId	()Ljava/lang/String;
    //   143: areturn
    //   144: astore_3
    //   145: goto -24 -> 121
    //   148: astore_3
    //   149: goto +7 -> 156
    //   152: astore_2
    //   153: aload 4
    //   155: astore_2
    //   156: goto -90 -> 66
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	159	0	this	ClientIdDefaultProvider
    //   36	21	1	i	int
    //   6	65	2	str	String
    //   118	1	2	localIOException1	IOException
    //   120	1	2	localObject1	Object
    //   152	1	2	localFileNotFoundException1	FileNotFoundException
    //   155	1	2	localObject2	Object
    //   1	119	3	localObject3	Object
    //   144	1	3	localIOException2	IOException
    //   148	1	3	localFileNotFoundException2	FileNotFoundException
    //   3	151	4	localObject4	Object
    //   16	85	5	localFileInputStream	java.io.FileInputStream
    //   23	31	6	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   7	45	118	java/io/IOException
    //   49	61	118	java/io/IOException
    //   72	92	118	java/io/IOException
    //   95	115	118	java/io/IOException
    //   61	66	144	java/io/IOException
    //   61	66	148	java/io/FileNotFoundException
    //   7	45	152	java/io/FileNotFoundException
    //   49	61	152	java/io/FileNotFoundException
    //   72	92	152	java/io/FileNotFoundException
    //   95	115	152	java/io/FileNotFoundException
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\google\analytics\tracking\android\ClientIdDefaultProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */