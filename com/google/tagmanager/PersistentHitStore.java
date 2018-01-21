package com.google.tagmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build.VERSION;
import com.google.android.gms.common.util.VisibleForTesting;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.impl.client.DefaultHttpClient;

class PersistentHitStore
{
  private static final String CREATE_HITS_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s ( '%s' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, '%s' INTEGER NOT NULL, '%s' TEXT NOT NULL,'%s' INTEGER NOT NULL);", new Object[] { "gtm_hits", "hit_id", "hit_time", "hit_url", "hit_first_send_time" });
  @VisibleForTesting
  static final String HITS_TABLE = "gtm_hits";
  @VisibleForTesting
  static final String HIT_FIRST_DISPATCH_TIME = "hit_first_send_time";
  @VisibleForTesting
  static final String HIT_ID = "hit_id";
  @VisibleForTesting
  static final String HIT_TIME = "hit_time";
  @VisibleForTesting
  static final String HIT_URL = "hit_url";
  private Clock mClock;
  private final Context mContext;
  private final String mDatabaseName;
  private final UrlDatabaseHelper mDbHelper;
  private volatile Dispatcher mDispatcher;
  private long mLastDeleteStaleHitsTime;
  private final HitStoreStateListener mListener;
  
  @VisibleForTesting
  PersistentHitStore(HitStoreStateListener paramHitStoreStateListener, Context paramContext, String paramString)
  {
    this.mContext = paramContext.getApplicationContext();
    this.mDatabaseName = paramString;
    this.mListener = paramHitStoreStateListener;
    this.mClock = new Clock()
    {
      public long currentTimeMillis()
      {
        return System.currentTimeMillis();
      }
    };
    this.mDbHelper = new UrlDatabaseHelper(this.mContext, this.mDatabaseName);
    this.mDispatcher = new SimpleNetworkDispatcher(new DefaultHttpClient(), this.mContext, new StoreDispatchListener());
    this.mLastDeleteStaleHitsTime = 0L;
  }
  
  @VisibleForTesting
  public UrlDatabaseHelper getDbHelper()
  {
    return this.mDbHelper;
  }
  
  @VisibleForTesting
  UrlDatabaseHelper getHelper()
  {
    return this.mDbHelper;
  }
  
  @VisibleForTesting
  public void setClock(Clock paramClock)
  {
    this.mClock = paramClock;
  }
  
  @VisibleForTesting
  void setDispatcher(Dispatcher paramDispatcher)
  {
    this.mDispatcher = paramDispatcher;
  }
  
  @VisibleForTesting
  void setLastDeleteStaleHitsTime(long paramLong)
  {
    this.mLastDeleteStaleHitsTime = paramLong;
  }
  
  @VisibleForTesting
  class StoreDispatchListener
    implements SimpleNetworkDispatcher.DispatchListener
  {
    StoreDispatchListener() {}
  }
  
  @VisibleForTesting
  class UrlDatabaseHelper
    extends SQLiteOpenHelper
  {
    private boolean mBadDatabase;
    private long mLastDatabaseCheckTime = 0L;
    
    UrlDatabaseHelper(Context paramContext, String paramString)
    {
      super(paramString, null, 1);
    }
    
    private boolean tablePresent(String paramString, SQLiteDatabase paramSQLiteDatabase)
    {
      Object localObject = null;
      SQLiteDatabase localSQLiteDatabase = null;
      try
      {
        paramSQLiteDatabase = paramSQLiteDatabase.query("SQLITE_MASTER", new String[] { "name" }, "name=?", new String[] { paramString }, null, null, null);
        localSQLiteDatabase = paramSQLiteDatabase;
        localObject = paramSQLiteDatabase;
        boolean bool = paramSQLiteDatabase.moveToFirst();
        return bool;
        paramSQLiteDatabase.close();
        return bool;
      }
      catch (SQLiteException paramSQLiteDatabase)
      {
        localObject = localSQLiteDatabase;
        Log.w("Error querying for table " + paramString);
        return false;
        localSQLiteDatabase.close();
        return false;
      }
      finally
      {
        if (localObject != null) {}
      }
      for (;;)
      {
        throw paramString;
        ((Cursor)localObject).close();
      }
    }
    
    private void validateColumnsPresent(SQLiteDatabase paramSQLiteDatabase)
    {
      paramSQLiteDatabase = paramSQLiteDatabase.rawQuery("SELECT * FROM gtm_hits WHERE 0", null);
      HashSet localHashSet = new HashSet();
      do
      {
        try
        {
          String[] arrayOfString = paramSQLiteDatabase.getColumnNames();
          int i = 0;
          int j = arrayOfString.length;
          if (i >= j)
          {
            paramSQLiteDatabase.close();
            if (localHashSet.remove("hit_id")) {
              continue;
            }
          }
          while (!((Set)localObject).remove("hit_url"))
          {
            throw new SQLiteException("Database column missing");
            localHashSet.add(arrayOfString[i]);
            i += 1;
            break;
          }
        }
        finally
        {
          paramSQLiteDatabase.close();
        }
      } while ((!((Set)localObject).remove("hit_time")) || (!((Set)localObject).remove("hit_first_send_time")));
      if (((Set)localObject).isEmpty()) {
        return;
      }
      throw new SQLiteException("Database has extra columns");
    }
    
    public SQLiteDatabase getWritableDatabase()
    {
      if (!this.mBadDatabase) {}
      for (;;)
      {
        localObject = null;
        this.mBadDatabase = true;
        this.mLastDatabaseCheckTime = PersistentHitStore.this.mClock.currentTimeMillis();
        try
        {
          SQLiteDatabase localSQLiteDatabase = super.getWritableDatabase();
          localObject = localSQLiteDatabase;
        }
        catch (SQLiteException localSQLiteException)
        {
          for (;;)
          {
            int i;
            PersistentHitStore.this.mContext.getDatabasePath(PersistentHitStore.this.mDatabaseName).delete();
            continue;
            localObject = super.getWritableDatabase();
          }
        }
        if (localObject == null) {
          break;
        }
        this.mBadDatabase = false;
        return (SQLiteDatabase)localObject;
        if (this.mLastDatabaseCheckTime + 3600000L <= PersistentHitStore.this.mClock.currentTimeMillis()) {}
        for (i = 1; i == 0; i = 0) {
          throw new SQLiteException("Database creation failed");
        }
      }
    }
    
    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      FutureApis.setOwnerOnlyReadWrite(paramSQLiteDatabase.getPath());
    }
    
    public void onOpen(SQLiteDatabase paramSQLiteDatabase)
    {
      if (Build.VERSION.SDK_INT >= 15) {}
      for (;;)
      {
        Cursor localCursor;
        if (tablePresent("gtm_hits", paramSQLiteDatabase))
        {
          validateColumnsPresent(paramSQLiteDatabase);
          return;
          localCursor = paramSQLiteDatabase.rawQuery("PRAGMA journal_mode=memory", null);
        }
        try
        {
          localCursor.moveToFirst();
          localCursor.close();
        }
        finally
        {
          localCursor.close();
        }
      }
    }
    
    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\google\tagmanager\PersistentHitStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */