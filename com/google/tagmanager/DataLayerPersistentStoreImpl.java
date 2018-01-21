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
import java.util.concurrent.Executor;

class DataLayerPersistentStoreImpl
{
  private static final String CREATE_MAPS_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s ( '%s' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, '%s' STRING NOT NULL, '%s' BLOB NOT NULL, '%s' INTEGER NOT NULL);", new Object[] { "datalayer", "ID", "key", "value", "expires" });
  private Clock mClock;
  private final Context mContext;
  private DatabaseHelper mDbHelper;
  private final Executor mExecutor;
  private int mMaxNumStoredItems;
  
  @VisibleForTesting
  DataLayerPersistentStoreImpl(Context paramContext, Clock paramClock, String paramString, int paramInt, Executor paramExecutor)
  {
    this.mContext = paramContext;
    this.mClock = paramClock;
    this.mMaxNumStoredItems = paramInt;
    this.mExecutor = paramExecutor;
    this.mDbHelper = new DatabaseHelper(this.mContext, paramString);
  }
  
  @VisibleForTesting
  class DatabaseHelper
    extends SQLiteOpenHelper
  {
    DatabaseHelper(Context paramContext, String paramString)
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
      paramSQLiteDatabase = paramSQLiteDatabase.rawQuery("SELECT * FROM datalayer WHERE 0", null);
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
            if (localHashSet.remove("key")) {
              continue;
            }
          }
          while (!((Set)localObject).remove("value"))
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
      } while ((!((Set)localObject).remove("ID")) || (!((Set)localObject).remove("expires")));
      if (((Set)localObject).isEmpty()) {
        return;
      }
      throw new SQLiteException("Database has extra columns");
    }
    
    public SQLiteDatabase getWritableDatabase()
    {
      Object localObject = null;
      try
      {
        SQLiteDatabase localSQLiteDatabase = super.getWritableDatabase();
        localObject = localSQLiteDatabase;
      }
      catch (SQLiteException localSQLiteException)
      {
        for (;;)
        {
          DataLayerPersistentStoreImpl.this.mContext.getDatabasePath("google_tagmanager.db").delete();
        }
      }
      if (localObject != null) {
        return (SQLiteDatabase)localObject;
      }
      return super.getWritableDatabase();
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
        if (tablePresent("datalayer", paramSQLiteDatabase))
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


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\google\tagmanager\DataLayerPersistentStoreImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */