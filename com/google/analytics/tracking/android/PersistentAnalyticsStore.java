package com.google.analytics.tracking.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build.VERSION;
import android.text.TextUtils;
import com.google.android.gms.analytics.internal.Command;
import com.google.android.gms.common.util.VisibleForTesting;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.http.impl.client.DefaultHttpClient;

class PersistentAnalyticsStore
  implements AnalyticsStore
{
  private static final String CREATE_HITS_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s ( '%s' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, '%s' INTEGER NOT NULL, '%s' TEXT NOT NULL, '%s' TEXT NOT NULL, '%s' INTEGER);", new Object[] { "hits2", "hit_id", "hit_time", "hit_url", "hit_string", "hit_app_id" });
  @VisibleForTesting
  static final String HITS_TABLE = "hits2";
  @VisibleForTesting
  static final String HIT_APP_ID = "hit_app_id";
  @VisibleForTesting
  static final String HIT_ID = "hit_id";
  @VisibleForTesting
  static final String HIT_STRING = "hit_string";
  @VisibleForTesting
  static final String HIT_TIME = "hit_time";
  @VisibleForTesting
  static final String HIT_URL = "hit_url";
  private Clock mClock;
  private final Context mContext;
  private final String mDatabaseName;
  private final AnalyticsDatabaseHelper mDbHelper;
  private volatile Dispatcher mDispatcher;
  private long mLastDeleteStaleHitsTime;
  private final AnalyticsStoreStateListener mListener;
  
  PersistentAnalyticsStore(AnalyticsStoreStateListener paramAnalyticsStoreStateListener, Context paramContext)
  {
    this(paramAnalyticsStoreStateListener, paramContext, "google_analytics_v2.db");
  }
  
  @VisibleForTesting
  PersistentAnalyticsStore(AnalyticsStoreStateListener paramAnalyticsStoreStateListener, Context paramContext, String paramString)
  {
    this.mContext = paramContext.getApplicationContext();
    this.mDatabaseName = paramString;
    this.mListener = paramAnalyticsStoreStateListener;
    this.mClock = new Clock()
    {
      public long currentTimeMillis()
      {
        return System.currentTimeMillis();
      }
    };
    this.mDbHelper = new AnalyticsDatabaseHelper(this.mContext, this.mDatabaseName);
    this.mDispatcher = new SimpleNetworkDispatcher(new DefaultHttpClient(), this.mContext);
    this.mLastDeleteStaleHitsTime = 0L;
  }
  
  private void fillVersionParameter(Map<String, String> paramMap, Collection<Command> paramCollection)
  {
    String str = "&_v".substring(1);
    if (paramCollection == null) {}
    Command localCommand;
    do
    {
      return;
      while (!paramCollection.hasNext()) {
        paramCollection = paramCollection.iterator();
      }
      localCommand = (Command)paramCollection.next();
    } while (!"appendVersion".equals(localCommand.getId()));
    paramMap.put(str, localCommand.getValue());
  }
  
  static String generateHitString(Map<String, String> paramMap)
  {
    ArrayList localArrayList = new ArrayList(paramMap.size());
    paramMap = paramMap.entrySet().iterator();
    for (;;)
    {
      if (!paramMap.hasNext()) {
        return TextUtils.join("&", localArrayList);
      }
      Map.Entry localEntry = (Map.Entry)paramMap.next();
      localArrayList.add(HitBuilder.encode((String)localEntry.getKey()) + "=" + HitBuilder.encode((String)localEntry.getValue()));
    }
  }
  
  private SQLiteDatabase getWritableDatabase(String paramString)
  {
    try
    {
      SQLiteDatabase localSQLiteDatabase = this.mDbHelper.getWritableDatabase();
      return localSQLiteDatabase;
    }
    catch (SQLiteException localSQLiteException)
    {
      Log.w(paramString);
    }
    return null;
  }
  
  private void removeOldHitIfFull()
  {
    int i = getNumStoredHits() - 2000 + 1;
    if (i <= 0) {
      return;
    }
    List localList = peekHitIds(i);
    Log.v("Store full, deleting " + localList.size() + " hits to make room.");
    deleteHits((String[])localList.toArray(new String[0]));
  }
  
  private void writeHitToDatabase(Map<String, String> paramMap, long paramLong, String paramString)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase("Error opening database for putHit");
    ContentValues localContentValues;
    if (localSQLiteDatabase != null)
    {
      localContentValues = new ContentValues();
      localContentValues.put("hit_string", generateHitString(paramMap));
      localContentValues.put("hit_time", Long.valueOf(paramLong));
      paramLong = 0L;
      if (paramMap.containsKey("AppUID")) {
        break label115;
      }
    }
    for (;;)
    {
      localContentValues.put("hit_app_id", Long.valueOf(paramLong));
      if (paramString != null) {}
      for (;;)
      {
        if (paramString.length() == 0) {
          break label146;
        }
        localContentValues.put("hit_url", paramString);
        try
        {
          localSQLiteDatabase.insert("hits2", null, localContentValues);
          this.mListener.reportStoreIsEmpty(false);
          return;
        }
        catch (SQLiteException paramMap)
        {
          label115:
          Log.w("Error storing hit");
          return;
        }
        return;
        try
        {
          long l = Long.parseLong((String)paramMap.get("AppUID"));
          paramLong = l;
        }
        catch (NumberFormatException paramMap) {}
        paramString = "http://www.google-analytics.com/collect";
      }
      label146:
      Log.w("Empty path: not sending hit");
      return;
    }
  }
  
  public void clearHits(long paramLong)
  {
    boolean bool = false;
    Object localObject = getWritableDatabase("Error opening database for clearHits");
    if (localObject == null) {
      return;
    }
    if (paramLong == 0L)
    {
      ((SQLiteDatabase)localObject).delete("hits2", null, null);
      localObject = this.mListener;
      if (getNumStoredHits() == 0) {
        break label83;
      }
    }
    for (;;)
    {
      ((AnalyticsStoreStateListener)localObject).reportStoreIsEmpty(bool);
      return;
      ((SQLiteDatabase)localObject).delete("hits2", "hit_app_id = ?", new String[] { Long.valueOf(paramLong).toString() });
      break;
      label83:
      bool = true;
    }
  }
  
  @Deprecated
  void deleteHits(Collection<Hit> paramCollection)
  {
    if (paramCollection == null) {}
    while (paramCollection.isEmpty())
    {
      Log.w("Empty/Null collection passed to deleteHits.");
      return;
    }
    String[] arrayOfString = new String[paramCollection.size()];
    int i = 0;
    paramCollection = paramCollection.iterator();
    for (;;)
    {
      if (!paramCollection.hasNext())
      {
        deleteHits(arrayOfString);
        return;
      }
      arrayOfString[i] = String.valueOf(((Hit)paramCollection.next()).getHitId());
      i += 1;
    }
  }
  
  void deleteHits(String[] paramArrayOfString)
  {
    boolean bool = false;
    if (paramArrayOfString == null) {}
    while (paramArrayOfString.length == 0)
    {
      Log.w("Empty hitIds passed to deleteHits.");
      return;
    }
    Object localObject = getWritableDatabase("Error opening database for deleteHits.");
    String str;
    if (localObject != null) {
      str = String.format("HIT_ID in (%s)", new Object[] { TextUtils.join(",", Collections.nCopies(paramArrayOfString.length, "?")) });
    }
    try
    {
      ((SQLiteDatabase)localObject).delete("hits2", str, paramArrayOfString);
      localObject = this.mListener;
      if (getNumStoredHits() != 0) {}
      for (;;)
      {
        ((AnalyticsStoreStateListener)localObject).reportStoreIsEmpty(bool);
        return;
        return;
        bool = true;
      }
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      Log.w("Error deleting hits " + paramArrayOfString);
    }
  }
  
  int deleteStaleHits()
  {
    boolean bool = false;
    long l = this.mClock.currentTimeMillis();
    if (l > this.mLastDeleteStaleHitsTime + 86400000L) {}
    for (int i = 1; i == 0; i = 0) {
      return 0;
    }
    this.mLastDeleteStaleHitsTime = l;
    Object localObject = getWritableDatabase("Error opening database for deleteStaleHits.");
    if (localObject != null)
    {
      i = ((SQLiteDatabase)localObject).delete("hits2", "HIT_TIME < ?", new String[] { Long.toString(this.mClock.currentTimeMillis() - 2592000000L) });
      localObject = this.mListener;
      if (getNumStoredHits() == 0) {
        break label116;
      }
    }
    for (;;)
    {
      ((AnalyticsStoreStateListener)localObject).reportStoreIsEmpty(bool);
      return i;
      return 0;
      label116:
      bool = true;
    }
  }
  
  public void dispatch()
  {
    Log.v("Dispatch running...");
    if (this.mDispatcher.okToDispatch())
    {
      List localList = peekHits(40);
      if (localList.isEmpty()) {
        break label122;
      }
      int i = this.mDispatcher.dispatchHits(localList);
      Log.v("sent " + i + " of " + localList.size() + " hits");
      deleteHits(localList.subList(0, Math.min(i, localList.size())));
      if (i == localList.size()) {
        break label139;
      }
    }
    label122:
    label139:
    while (getNumStoredHits() <= 0)
    {
      return;
      return;
      Log.v("...nothing to dispatch");
      this.mListener.reportStoreIsEmpty(true);
      return;
    }
    GAServiceManager.getInstance().dispatchLocalHits();
  }
  
  @VisibleForTesting
  public AnalyticsDatabaseHelper getDbHelper()
  {
    return this.mDbHelper;
  }
  
  public Dispatcher getDispatcher()
  {
    return this.mDispatcher;
  }
  
  @VisibleForTesting
  AnalyticsDatabaseHelper getHelper()
  {
    return this.mDbHelper;
  }
  
  int getNumStoredHits()
  {
    int j = 0;
    int i = 0;
    Object localObject5 = getWritableDatabase("Error opening database for getNumStoredHits.");
    if (localObject5 != null)
    {
      Object localObject3 = null;
      Object localObject1 = null;
      try
      {
        localObject5 = ((SQLiteDatabase)localObject5).rawQuery("SELECT COUNT(*) from hits2", null);
        localObject1 = localObject5;
        localObject3 = localObject5;
        if (!((Cursor)localObject5).moveToFirst()) {
          break label136;
        }
        localObject1 = localObject5;
        localObject3 = localObject5;
        long l = ((Cursor)localObject5).getLong(0);
        i = (int)l;
      }
      catch (SQLiteException localSQLiteException)
      {
        localObject4 = localObject1;
        Log.w("Error getting numStoredHits");
        i = j;
        if (localObject1 == null) {
          break label141;
        }
        ((Cursor)localObject1).close();
        return 0;
      }
      finally
      {
        label136:
        do
        {
          Object localObject4;
          if (localObject4 == null) {}
          for (;;)
          {
            throw ((Throwable)localObject2);
            ((Cursor)localObject4).close();
          }
        } while (localObject5 != null);
      }
      ((Cursor)localObject5).close();
      return i;
      label141:
      return i;
    }
    return 0;
  }
  
  List<String> peekHitIds(int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    SQLiteDatabase localSQLiteDatabase;
    Object localObject3;
    Cursor localCursor2;
    Cursor localCursor1;
    Object localObject1;
    if (paramInt > 0)
    {
      localSQLiteDatabase = getWritableDatabase("Error opening database for peekHitIds.");
      if (localSQLiteDatabase == null) {
        break label139;
      }
      localObject3 = null;
      localCursor2 = null;
      localCursor1 = localCursor2;
      localObject1 = localObject3;
    }
    try
    {
      String str1 = String.format("%s ASC", new Object[] { "hit_id" });
      localCursor1 = localCursor2;
      localObject1 = localObject3;
      String str2 = Integer.toString(paramInt);
      localCursor1 = localCursor2;
      localObject1 = localObject3;
      localCursor2 = localSQLiteDatabase.query("hits2", new String[] { "hit_id" }, null, null, null, null, str1, str2);
      localCursor1 = localCursor2;
      localObject1 = localCursor2;
      bool = localCursor2.moveToFirst();
      if (bool) {
        break label142;
      }
    }
    catch (SQLiteException localSQLiteException)
    {
      label139:
      label142:
      do
      {
        boolean bool;
        localObject1 = localCursor1;
        Log.w("Error in peekHits fetching hitIds: " + localSQLiteException.getMessage());
      } while (localCursor1 == null);
      localCursor1.close();
      return localArrayList;
    }
    finally
    {
      if (localObject1 != null) {
        break label256;
      }
    }
    return localArrayList;
    Log.w("Invalid maxHits specified. Skipping");
    return localArrayList;
    return localArrayList;
    for (;;)
    {
      localCursor1 = localCursor2;
      localObject1 = localCursor2;
      localArrayList.add(String.valueOf(localCursor2.getLong(0)));
      localCursor1 = localCursor2;
      localObject1 = localCursor2;
      bool = localCursor2.moveToNext();
      if (!bool) {
        break;
      }
    }
    localCursor2.close();
    return localArrayList;
    for (;;)
    {
      throw ((Throwable)localObject2);
      label256:
      ((Cursor)localObject1).close();
    }
  }
  
  /* Error */
  public List<Hit> peekHits(int paramInt)
  {
    // Byte code:
    //   0: new 160	java/util/ArrayList
    //   3: dup
    //   4: invokespecial 455	java/util/ArrayList:<init>	()V
    //   7: astore 7
    //   9: aload_0
    //   10: ldc_w 480
    //   13: invokespecial 263	com/google/analytics/tracking/android/PersistentAnalyticsStore:getWritableDatabase	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteDatabase;
    //   16: astore 9
    //   18: aload 9
    //   20: ifnull +236 -> 256
    //   23: aconst_null
    //   24: astore 8
    //   26: aconst_null
    //   27: astore 4
    //   29: aload 4
    //   31: astore 6
    //   33: aload 8
    //   35: astore 5
    //   37: ldc_w 459
    //   40: iconst_1
    //   41: anewarray 4	java/lang/Object
    //   44: dup
    //   45: iconst_0
    //   46: ldc 23
    //   48: aastore
    //   49: invokestatic 55	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   52: astore 10
    //   54: aload 4
    //   56: astore 6
    //   58: aload 8
    //   60: astore 5
    //   62: iload_1
    //   63: invokestatic 463	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   66: astore 11
    //   68: aload 4
    //   70: astore 6
    //   72: aload 8
    //   74: astore 5
    //   76: aload 9
    //   78: ldc 16
    //   80: iconst_2
    //   81: anewarray 51	java/lang/String
    //   84: dup
    //   85: iconst_0
    //   86: ldc 23
    //   88: aastore
    //   89: dup
    //   90: iconst_1
    //   91: ldc 29
    //   93: aastore
    //   94: aconst_null
    //   95: aconst_null
    //   96: aconst_null
    //   97: aconst_null
    //   98: aload 10
    //   100: aload 11
    //   102: invokevirtual 467	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   105: astore 4
    //   107: aload 4
    //   109: astore 6
    //   111: aload 4
    //   113: astore 5
    //   115: new 160	java/util/ArrayList
    //   118: dup
    //   119: invokespecial 455	java/util/ArrayList:<init>	()V
    //   122: astore 8
    //   124: aload 4
    //   126: invokeinterface 445 1 0
    //   131: istore_3
    //   132: iload_3
    //   133: ifne +126 -> 259
    //   136: aload 4
    //   138: ifnonnull +168 -> 306
    //   141: iconst_0
    //   142: istore_2
    //   143: aload 4
    //   145: astore 6
    //   147: aload 4
    //   149: astore 5
    //   151: ldc_w 459
    //   154: iconst_1
    //   155: anewarray 4	java/lang/Object
    //   158: dup
    //   159: iconst_0
    //   160: ldc 23
    //   162: aastore
    //   163: invokestatic 55	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   166: astore 7
    //   168: aload 4
    //   170: astore 6
    //   172: aload 4
    //   174: astore 5
    //   176: iload_1
    //   177: invokestatic 463	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   180: astore 10
    //   182: aload 4
    //   184: astore 6
    //   186: aload 4
    //   188: astore 5
    //   190: aload 9
    //   192: ldc 16
    //   194: iconst_3
    //   195: anewarray 51	java/lang/String
    //   198: dup
    //   199: iconst_0
    //   200: ldc 23
    //   202: aastore
    //   203: dup
    //   204: iconst_1
    //   205: ldc 26
    //   207: aastore
    //   208: dup
    //   209: iconst_2
    //   210: ldc 32
    //   212: aastore
    //   213: aconst_null
    //   214: aconst_null
    //   215: aconst_null
    //   216: aconst_null
    //   217: aload 7
    //   219: aload 10
    //   221: invokevirtual 467	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   224: astore 4
    //   226: aload 4
    //   228: astore 6
    //   230: aload 4
    //   232: astore 5
    //   234: aload 4
    //   236: invokeinterface 445 1 0
    //   241: istore_3
    //   242: iload_2
    //   243: istore_1
    //   244: iload_3
    //   245: ifne +244 -> 489
    //   248: aload 4
    //   250: ifnonnull +390 -> 640
    //   253: aload 8
    //   255: areturn
    //   256: aload 7
    //   258: areturn
    //   259: aload 8
    //   261: new 336	com/google/analytics/tracking/android/Hit
    //   264: dup
    //   265: aconst_null
    //   266: aload 4
    //   268: iconst_0
    //   269: invokeinterface 449 2 0
    //   274: aload 4
    //   276: iconst_1
    //   277: invokeinterface 449 2 0
    //   282: invokespecial 483	com/google/analytics/tracking/android/Hit:<init>	(Ljava/lang/String;JJ)V
    //   285: invokeinterface 212 2 0
    //   290: pop
    //   291: aload 4
    //   293: invokeinterface 472 1 0
    //   298: istore_3
    //   299: iload_3
    //   300: ifeq -164 -> 136
    //   303: goto -44 -> 259
    //   306: aload 4
    //   308: invokeinterface 452 1 0
    //   313: goto -172 -> 141
    //   316: astore 4
    //   318: aload 7
    //   320: astore 8
    //   322: aload 4
    //   324: astore 7
    //   326: aload 6
    //   328: astore 4
    //   330: aload 4
    //   332: astore 5
    //   334: new 186	java/lang/StringBuilder
    //   337: dup
    //   338: invokespecial 187	java/lang/StringBuilder:<init>	()V
    //   341: ldc_w 474
    //   344: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   347: aload 7
    //   349: invokevirtual 477	android/database/sqlite/SQLiteException:getMessage	()Ljava/lang/String;
    //   352: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   355: invokevirtual 207	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   358: invokestatic 226	com/google/analytics/tracking/android/Log:w	(Ljava/lang/String;)V
    //   361: aload 4
    //   363: ifnonnull +6 -> 369
    //   366: aload 8
    //   368: areturn
    //   369: aload 4
    //   371: invokeinterface 452 1 0
    //   376: aload 8
    //   378: areturn
    //   379: astore 6
    //   381: aload 5
    //   383: astore 4
    //   385: aload 6
    //   387: astore 5
    //   389: aload 4
    //   391: ifnonnull +6 -> 397
    //   394: aload 5
    //   396: athrow
    //   397: aload 4
    //   399: invokeinterface 452 1 0
    //   404: goto -10 -> 394
    //   407: aload 4
    //   409: astore 6
    //   411: aload 4
    //   413: astore 5
    //   415: aload 8
    //   417: iload_1
    //   418: invokeinterface 486 2 0
    //   423: checkcast 336	com/google/analytics/tracking/android/Hit
    //   426: aload 4
    //   428: iconst_1
    //   429: invokeinterface 489 2 0
    //   434: invokevirtual 492	com/google/analytics/tracking/android/Hit:setHitString	(Ljava/lang/String;)V
    //   437: aload 4
    //   439: astore 6
    //   441: aload 4
    //   443: astore 5
    //   445: aload 8
    //   447: iload_1
    //   448: invokeinterface 486 2 0
    //   453: checkcast 336	com/google/analytics/tracking/android/Hit
    //   456: aload 4
    //   458: iconst_2
    //   459: invokeinterface 489 2 0
    //   464: invokevirtual 495	com/google/analytics/tracking/android/Hit:setHitUrl	(Ljava/lang/String;)V
    //   467: iload_1
    //   468: iconst_1
    //   469: iadd
    //   470: istore_1
    //   471: aload 4
    //   473: astore 6
    //   475: aload 4
    //   477: astore 5
    //   479: aload 4
    //   481: invokeinterface 472 1 0
    //   486: ifeq -238 -> 248
    //   489: aload 4
    //   491: astore 6
    //   493: aload 4
    //   495: astore 5
    //   497: aload 4
    //   499: checkcast 497	android/database/sqlite/SQLiteCursor
    //   502: invokevirtual 501	android/database/sqlite/SQLiteCursor:getWindow	()Landroid/database/CursorWindow;
    //   505: invokevirtual 506	android/database/CursorWindow:getNumRows	()I
    //   508: ifgt -101 -> 407
    //   511: aload 4
    //   513: astore 6
    //   515: aload 4
    //   517: astore 5
    //   519: ldc_w 508
    //   522: iconst_1
    //   523: anewarray 4	java/lang/Object
    //   526: dup
    //   527: iconst_0
    //   528: aload 8
    //   530: iload_1
    //   531: invokeinterface 486 2 0
    //   536: checkcast 336	com/google/analytics/tracking/android/Hit
    //   539: invokevirtual 340	com/google/analytics/tracking/android/Hit:getHitId	()J
    //   542: invokestatic 277	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   545: aastore
    //   546: invokestatic 55	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   549: invokestatic 226	com/google/analytics/tracking/android/Log:w	(Ljava/lang/String;)V
    //   552: goto -85 -> 467
    //   555: astore 4
    //   557: aload 6
    //   559: astore 5
    //   561: new 186	java/lang/StringBuilder
    //   564: dup
    //   565: invokespecial 187	java/lang/StringBuilder:<init>	()V
    //   568: ldc_w 510
    //   571: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   574: aload 4
    //   576: invokevirtual 477	android/database/sqlite/SQLiteException:getMessage	()Ljava/lang/String;
    //   579: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   582: invokevirtual 207	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   585: invokestatic 226	com/google/analytics/tracking/android/Log:w	(Ljava/lang/String;)V
    //   588: aload 6
    //   590: astore 5
    //   592: new 160	java/util/ArrayList
    //   595: dup
    //   596: invokespecial 455	java/util/ArrayList:<init>	()V
    //   599: astore 4
    //   601: iconst_0
    //   602: istore_1
    //   603: aload 6
    //   605: astore 5
    //   607: aload 8
    //   609: invokeinterface 511 1 0
    //   614: astore 7
    //   616: aload 6
    //   618: astore 5
    //   620: aload 7
    //   622: invokeinterface 130 1 0
    //   627: istore_3
    //   628: iload_3
    //   629: ifne +21 -> 650
    //   632: aload 6
    //   634: ifnonnull +83 -> 717
    //   637: aload 4
    //   639: areturn
    //   640: aload 4
    //   642: invokeinterface 452 1 0
    //   647: aload 8
    //   649: areturn
    //   650: aload 6
    //   652: astore 5
    //   654: aload 7
    //   656: invokeinterface 134 1 0
    //   661: checkcast 336	com/google/analytics/tracking/android/Hit
    //   664: astore 8
    //   666: aload 6
    //   668: astore 5
    //   670: aload 8
    //   672: invokevirtual 514	com/google/analytics/tracking/android/Hit:getHitParams	()Ljava/lang/String;
    //   675: invokestatic 517	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   678: ifne +30 -> 708
    //   681: aload 6
    //   683: astore 5
    //   685: aload 4
    //   687: aload 8
    //   689: invokeinterface 212 2 0
    //   694: pop
    //   695: goto -79 -> 616
    //   698: astore 4
    //   700: aload 5
    //   702: ifnonnull +25 -> 727
    //   705: aload 4
    //   707: athrow
    //   708: iload_1
    //   709: ifne -77 -> 632
    //   712: iconst_1
    //   713: istore_1
    //   714: goto -33 -> 681
    //   717: aload 6
    //   719: invokeinterface 452 1 0
    //   724: aload 4
    //   726: areturn
    //   727: aload 5
    //   729: invokeinterface 452 1 0
    //   734: goto -29 -> 705
    //   737: astore 5
    //   739: goto -350 -> 389
    //   742: astore 7
    //   744: goto -414 -> 330
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	747	0	this	PersistentAnalyticsStore
    //   0	747	1	paramInt	int
    //   142	101	2	i	int
    //   131	498	3	bool	boolean
    //   27	280	4	localCursor1	Cursor
    //   316	7	4	localSQLiteException1	SQLiteException
    //   328	188	4	localObject1	Object
    //   555	20	4	localSQLiteException2	SQLiteException
    //   599	87	4	localArrayList	ArrayList
    //   698	27	4	localList	List<Hit>
    //   35	693	5	localObject2	Object
    //   737	1	5	localObject3	Object
    //   31	296	6	localCursor2	Cursor
    //   379	7	6	localObject4	Object
    //   409	309	6	localObject5	Object
    //   7	648	7	localObject6	Object
    //   742	1	7	localSQLiteException3	SQLiteException
    //   24	664	8	localObject7	Object
    //   16	175	9	localSQLiteDatabase	SQLiteDatabase
    //   52	168	10	str1	String
    //   66	35	11	str2	String
    // Exception table:
    //   from	to	target	type
    //   37	54	316	android/database/sqlite/SQLiteException
    //   62	68	316	android/database/sqlite/SQLiteException
    //   76	107	316	android/database/sqlite/SQLiteException
    //   115	124	316	android/database/sqlite/SQLiteException
    //   37	54	379	finally
    //   62	68	379	finally
    //   76	107	379	finally
    //   115	124	379	finally
    //   334	361	379	finally
    //   151	168	555	android/database/sqlite/SQLiteException
    //   176	182	555	android/database/sqlite/SQLiteException
    //   190	226	555	android/database/sqlite/SQLiteException
    //   234	242	555	android/database/sqlite/SQLiteException
    //   415	437	555	android/database/sqlite/SQLiteException
    //   445	467	555	android/database/sqlite/SQLiteException
    //   479	489	555	android/database/sqlite/SQLiteException
    //   497	511	555	android/database/sqlite/SQLiteException
    //   519	552	555	android/database/sqlite/SQLiteException
    //   151	168	698	finally
    //   176	182	698	finally
    //   190	226	698	finally
    //   234	242	698	finally
    //   415	437	698	finally
    //   445	467	698	finally
    //   479	489	698	finally
    //   497	511	698	finally
    //   519	552	698	finally
    //   561	588	698	finally
    //   592	601	698	finally
    //   607	616	698	finally
    //   620	628	698	finally
    //   654	666	698	finally
    //   670	681	698	finally
    //   685	695	698	finally
    //   124	132	737	finally
    //   259	299	737	finally
    //   124	132	742	android/database/sqlite/SQLiteException
    //   259	299	742	android/database/sqlite/SQLiteException
  }
  
  public void putHit(Map<String, String> paramMap, long paramLong, String paramString, Collection<Command> paramCollection)
  {
    deleteStaleHits();
    removeOldHitIfFull();
    fillVersionParameter(paramMap, paramCollection);
    writeHitToDatabase(paramMap, paramLong, paramString);
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
  class AnalyticsDatabaseHelper
    extends SQLiteOpenHelper
  {
    private boolean mBadDatabase;
    private long mLastDatabaseCheckTime = 0L;
    
    AnalyticsDatabaseHelper(Context paramContext, String paramString)
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
      int j = 0;
      Cursor localCursor = paramSQLiteDatabase.rawQuery("SELECT * FROM hits2 WHERE 0", null);
      HashSet localHashSet = new HashSet();
      do
      {
        try
        {
          String[] arrayOfString = localCursor.getColumnNames();
          i = 0;
          int k = arrayOfString.length;
          if (i >= k)
          {
            localCursor.close();
            if (localHashSet.remove("hit_id")) {
              continue;
            }
          }
          while (!localHashSet.remove("hit_url"))
          {
            throw new SQLiteException("Database column missing");
            localHashSet.add(arrayOfString[i]);
            i += 1;
            break;
          }
        }
        finally
        {
          localCursor.close();
        }
      } while ((!localHashSet.remove("hit_string")) || (!localHashSet.remove("hit_time")));
      if (localHashSet.remove("hit_app_id")) {}
      for (int i = j; localHashSet.isEmpty(); i = 1)
      {
        if (i != 0) {
          break label180;
        }
        return;
      }
      throw new SQLiteException("Database has extra columns");
      label180:
      paramSQLiteDatabase.execSQL("ALTER TABLE hits2 ADD COLUMN hit_app_id");
    }
    
    public SQLiteDatabase getWritableDatabase()
    {
      if (!this.mBadDatabase) {}
      for (;;)
      {
        localObject = null;
        this.mBadDatabase = true;
        this.mLastDatabaseCheckTime = PersistentAnalyticsStore.this.mClock.currentTimeMillis();
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
            PersistentAnalyticsStore.this.mContext.getDatabasePath(PersistentAnalyticsStore.this.mDatabaseName).delete();
            continue;
            localObject = super.getWritableDatabase();
          }
        }
        if (localObject == null) {
          break;
        }
        this.mBadDatabase = false;
        return (SQLiteDatabase)localObject;
        if (this.mLastDatabaseCheckTime + 3600000L <= PersistentAnalyticsStore.this.mClock.currentTimeMillis()) {}
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
        if (tablePresent("hits2", paramSQLiteDatabase))
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


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\google\analytics\tracking\android\PersistentAnalyticsStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */