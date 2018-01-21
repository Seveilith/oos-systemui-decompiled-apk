package android.support.v7.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.DataSetObservable;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.text.TextUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ActivityChooserModel
  extends DataSetObservable
{
  static final String LOG_TAG = ActivityChooserModel.class.getSimpleName();
  private static final Map<String, ActivityChooserModel> sDataModelRegistry = new HashMap();
  private static final Object sRegistryLock = new Object();
  private final List<ActivityResolveInfo> mActivities;
  private OnChooseActivityListener mActivityChoserModelPolicy;
  private ActivitySorter mActivitySorter;
  boolean mCanReadHistoricalData;
  final Context mContext;
  private final List<HistoricalRecord> mHistoricalRecords;
  private boolean mHistoricalRecordsChanged;
  final String mHistoryFileName;
  private int mHistoryMaxSize;
  private final Object mInstanceLock;
  private Intent mIntent;
  private boolean mReadShareHistoryCalled;
  private boolean mReloadActivities;
  
  private boolean addHistoricalRecord(HistoricalRecord paramHistoricalRecord)
  {
    boolean bool = this.mHistoricalRecords.add(paramHistoricalRecord);
    if (bool)
    {
      this.mHistoricalRecordsChanged = true;
      pruneExcessiveHistoricalRecordsIfNeeded();
      persistHistoricalDataIfNeeded();
      sortActivitiesIfNeeded();
      notifyChanged();
    }
    return bool;
  }
  
  private void ensureConsistentState()
  {
    boolean bool1 = loadActivitiesIfNeeded();
    boolean bool2 = readHistoricalDataIfNeeded();
    pruneExcessiveHistoricalRecordsIfNeeded();
    if ((bool1 | bool2))
    {
      sortActivitiesIfNeeded();
      notifyChanged();
    }
  }
  
  private boolean loadActivitiesIfNeeded()
  {
    if ((this.mReloadActivities) && (this.mIntent != null))
    {
      this.mReloadActivities = false;
      this.mActivities.clear();
      List localList = this.mContext.getPackageManager().queryIntentActivities(this.mIntent, 0);
      int j = localList.size();
      int i = 0;
      while (i < j)
      {
        ResolveInfo localResolveInfo = (ResolveInfo)localList.get(i);
        this.mActivities.add(new ActivityResolveInfo(localResolveInfo));
        i += 1;
      }
      return true;
    }
    return false;
  }
  
  private void persistHistoricalDataIfNeeded()
  {
    if (!this.mReadShareHistoryCalled) {
      throw new IllegalStateException("No preceding call to #readHistoricalData");
    }
    if (!this.mHistoricalRecordsChanged) {
      return;
    }
    this.mHistoricalRecordsChanged = false;
    if (!TextUtils.isEmpty(this.mHistoryFileName)) {
      AsyncTaskCompat.executeParallel(new PersistHistoryAsyncTask(), new Object[] { new ArrayList(this.mHistoricalRecords), this.mHistoryFileName });
    }
  }
  
  private void pruneExcessiveHistoricalRecordsIfNeeded()
  {
    int j = this.mHistoricalRecords.size() - this.mHistoryMaxSize;
    if (j <= 0) {
      return;
    }
    this.mHistoricalRecordsChanged = true;
    int i = 0;
    while (i < j)
    {
      HistoricalRecord localHistoricalRecord = (HistoricalRecord)this.mHistoricalRecords.remove(0);
      i += 1;
    }
  }
  
  private boolean readHistoricalDataIfNeeded()
  {
    if ((!this.mCanReadHistoricalData) || (!this.mHistoricalRecordsChanged) || (TextUtils.isEmpty(this.mHistoryFileName))) {
      return false;
    }
    this.mCanReadHistoricalData = false;
    this.mReadShareHistoryCalled = true;
    readHistoricalDataImpl();
    return true;
  }
  
  /* Error */
  private void readHistoricalDataImpl()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 114	android/support/v7/widget/ActivityChooserModel:mContext	Landroid/content/Context;
    //   4: aload_0
    //   5: getfield 150	android/support/v7/widget/ActivityChooserModel:mHistoryFileName	Ljava/lang/String;
    //   8: invokevirtual 190	android/content/Context:openFileInput	(Ljava/lang/String;)Ljava/io/FileInputStream;
    //   11: astore_2
    //   12: invokestatic 196	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   15: astore_3
    //   16: aload_3
    //   17: aload_2
    //   18: ldc -58
    //   20: invokeinterface 204 3 0
    //   25: iconst_0
    //   26: istore_1
    //   27: iload_1
    //   28: iconst_1
    //   29: if_icmpeq +18 -> 47
    //   32: iload_1
    //   33: iconst_2
    //   34: if_icmpeq +13 -> 47
    //   37: aload_3
    //   38: invokeinterface 207 1 0
    //   43: istore_1
    //   44: goto -17 -> 27
    //   47: ldc -47
    //   49: aload_3
    //   50: invokeinterface 212 1 0
    //   55: invokevirtual 217	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   58: ifne +53 -> 111
    //   61: new 184	org/xmlpull/v1/XmlPullParserException
    //   64: dup
    //   65: ldc -37
    //   67: invokespecial 220	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;)V
    //   70: athrow
    //   71: astore_3
    //   72: getstatic 58	android/support/v7/widget/ActivityChooserModel:LOG_TAG	Ljava/lang/String;
    //   75: new 222	java/lang/StringBuilder
    //   78: dup
    //   79: invokespecial 223	java/lang/StringBuilder:<init>	()V
    //   82: ldc -31
    //   84: invokevirtual 229	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   87: aload_0
    //   88: getfield 150	android/support/v7/widget/ActivityChooserModel:mHistoryFileName	Ljava/lang/String;
    //   91: invokevirtual 229	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   94: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   97: aload_3
    //   98: invokestatic 238	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   101: pop
    //   102: aload_2
    //   103: ifnull +7 -> 110
    //   106: aload_2
    //   107: invokevirtual 243	java/io/FileInputStream:close	()V
    //   110: return
    //   111: aload_0
    //   112: getfield 75	android/support/v7/widget/ActivityChooserModel:mHistoricalRecords	Ljava/util/List;
    //   115: astore 4
    //   117: aload 4
    //   119: invokeinterface 112 1 0
    //   124: aload_3
    //   125: invokeinterface 207 1 0
    //   130: istore_1
    //   131: iload_1
    //   132: iconst_1
    //   133: if_icmpne +14 -> 147
    //   136: aload_2
    //   137: ifnull -27 -> 110
    //   140: aload_2
    //   141: invokevirtual 243	java/io/FileInputStream:close	()V
    //   144: return
    //   145: astore_2
    //   146: return
    //   147: iload_1
    //   148: iconst_3
    //   149: if_icmpeq -25 -> 124
    //   152: iload_1
    //   153: iconst_4
    //   154: if_icmpeq -30 -> 124
    //   157: ldc -11
    //   159: aload_3
    //   160: invokeinterface 212 1 0
    //   165: invokevirtual 217	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   168: ifne +55 -> 223
    //   171: new 184	org/xmlpull/v1/XmlPullParserException
    //   174: dup
    //   175: ldc -9
    //   177: invokespecial 220	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;)V
    //   180: athrow
    //   181: astore_3
    //   182: getstatic 58	android/support/v7/widget/ActivityChooserModel:LOG_TAG	Ljava/lang/String;
    //   185: new 222	java/lang/StringBuilder
    //   188: dup
    //   189: invokespecial 223	java/lang/StringBuilder:<init>	()V
    //   192: ldc -31
    //   194: invokevirtual 229	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   197: aload_0
    //   198: getfield 150	android/support/v7/widget/ActivityChooserModel:mHistoryFileName	Ljava/lang/String;
    //   201: invokevirtual 229	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   204: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   207: aload_3
    //   208: invokestatic 238	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   211: pop
    //   212: aload_2
    //   213: ifnull -103 -> 110
    //   216: aload_2
    //   217: invokevirtual 243	java/io/FileInputStream:close	()V
    //   220: return
    //   221: astore_2
    //   222: return
    //   223: aload 4
    //   225: new 12	android/support/v7/widget/ActivityChooserModel$HistoricalRecord
    //   228: dup
    //   229: aload_3
    //   230: aconst_null
    //   231: ldc -7
    //   233: invokeinterface 253 3 0
    //   238: aload_3
    //   239: aconst_null
    //   240: ldc -1
    //   242: invokeinterface 253 3 0
    //   247: invokestatic 261	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   250: aload_3
    //   251: aconst_null
    //   252: ldc_w 263
    //   255: invokeinterface 253 3 0
    //   260: invokestatic 269	java/lang/Float:parseFloat	(Ljava/lang/String;)F
    //   263: invokespecial 272	android/support/v7/widget/ActivityChooserModel$HistoricalRecord:<init>	(Ljava/lang/String;JF)V
    //   266: invokeinterface 81 2 0
    //   271: pop
    //   272: goto -148 -> 124
    //   275: astore_3
    //   276: aload_2
    //   277: ifnull +7 -> 284
    //   280: aload_2
    //   281: invokevirtual 243	java/io/FileInputStream:close	()V
    //   284: aload_3
    //   285: athrow
    //   286: astore_2
    //   287: return
    //   288: astore_2
    //   289: goto -5 -> 284
    //   292: astore_2
    //   293: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	294	0	this	ActivityChooserModel
    //   26	129	1	i	int
    //   11	130	2	localFileInputStream	java.io.FileInputStream
    //   145	72	2	localIOException1	java.io.IOException
    //   221	60	2	localIOException2	java.io.IOException
    //   286	1	2	localIOException3	java.io.IOException
    //   288	1	2	localIOException4	java.io.IOException
    //   292	1	2	localFileNotFoundException	java.io.FileNotFoundException
    //   15	35	3	localXmlPullParser	org.xmlpull.v1.XmlPullParser
    //   71	89	3	localXmlPullParserException	org.xmlpull.v1.XmlPullParserException
    //   181	70	3	localIOException5	java.io.IOException
    //   275	10	3	localObject	Object
    //   115	109	4	localList	List
    // Exception table:
    //   from	to	target	type
    //   12	25	71	org/xmlpull/v1/XmlPullParserException
    //   37	44	71	org/xmlpull/v1/XmlPullParserException
    //   47	71	71	org/xmlpull/v1/XmlPullParserException
    //   111	124	71	org/xmlpull/v1/XmlPullParserException
    //   124	131	71	org/xmlpull/v1/XmlPullParserException
    //   157	181	71	org/xmlpull/v1/XmlPullParserException
    //   223	272	71	org/xmlpull/v1/XmlPullParserException
    //   140	144	145	java/io/IOException
    //   12	25	181	java/io/IOException
    //   37	44	181	java/io/IOException
    //   47	71	181	java/io/IOException
    //   111	124	181	java/io/IOException
    //   124	131	181	java/io/IOException
    //   157	181	181	java/io/IOException
    //   223	272	181	java/io/IOException
    //   216	220	221	java/io/IOException
    //   12	25	275	finally
    //   37	44	275	finally
    //   47	71	275	finally
    //   72	102	275	finally
    //   111	124	275	finally
    //   124	131	275	finally
    //   157	181	275	finally
    //   182	212	275	finally
    //   223	272	275	finally
    //   106	110	286	java/io/IOException
    //   280	284	288	java/io/IOException
    //   0	12	292	java/io/FileNotFoundException
  }
  
  private boolean sortActivitiesIfNeeded()
  {
    if ((this.mActivitySorter == null) || (this.mIntent == null) || (this.mActivities.isEmpty())) {}
    while (this.mHistoricalRecords.isEmpty()) {
      return false;
    }
    this.mActivitySorter.sort(this.mIntent, this.mActivities, Collections.unmodifiableList(this.mHistoricalRecords));
    return true;
  }
  
  public Intent chooseActivity(int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      Object localObject2 = this.mIntent;
      if (localObject2 == null) {
        return null;
      }
      ensureConsistentState();
      localObject2 = (ActivityResolveInfo)this.mActivities.get(paramInt);
      localObject2 = new ComponentName(((ActivityResolveInfo)localObject2).resolveInfo.activityInfo.packageName, ((ActivityResolveInfo)localObject2).resolveInfo.activityInfo.name);
      Intent localIntent1 = new Intent(this.mIntent);
      localIntent1.setComponent((ComponentName)localObject2);
      if (this.mActivityChoserModelPolicy != null)
      {
        Intent localIntent2 = new Intent(localIntent1);
        boolean bool = this.mActivityChoserModelPolicy.onChooseActivity(this, localIntent2);
        if (bool) {
          return null;
        }
      }
      addHistoricalRecord(new HistoricalRecord((ComponentName)localObject2, System.currentTimeMillis(), 1.0F));
      return localIntent1;
    }
  }
  
  public ResolveInfo getActivity(int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      ResolveInfo localResolveInfo = ((ActivityResolveInfo)this.mActivities.get(paramInt)).resolveInfo;
      return localResolveInfo;
    }
  }
  
  public int getActivityCount()
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      int i = this.mActivities.size();
      return i;
    }
  }
  
  public int getActivityIndex(ResolveInfo paramResolveInfo)
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      List localList = this.mActivities;
      int j = localList.size();
      int i = 0;
      while (i < j)
      {
        ResolveInfo localResolveInfo = ((ActivityResolveInfo)localList.get(i)).resolveInfo;
        if (localResolveInfo == paramResolveInfo) {
          return i;
        }
        i += 1;
      }
      return -1;
    }
  }
  
  public ResolveInfo getDefaultActivity()
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      if (!this.mActivities.isEmpty())
      {
        ResolveInfo localResolveInfo = ((ActivityResolveInfo)this.mActivities.get(0)).resolveInfo;
        return localResolveInfo;
      }
      return null;
    }
  }
  
  public int getHistorySize()
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      int i = this.mHistoricalRecords.size();
      return i;
    }
  }
  
  public void setDefaultActivity(int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      ActivityResolveInfo localActivityResolveInfo1 = (ActivityResolveInfo)this.mActivities.get(paramInt);
      ActivityResolveInfo localActivityResolveInfo2 = (ActivityResolveInfo)this.mActivities.get(0);
      if (localActivityResolveInfo2 != null)
      {
        f = localActivityResolveInfo2.weight - localActivityResolveInfo1.weight + 5.0F;
        addHistoricalRecord(new HistoricalRecord(new ComponentName(localActivityResolveInfo1.resolveInfo.activityInfo.packageName, localActivityResolveInfo1.resolveInfo.activityInfo.name), System.currentTimeMillis(), f));
        return;
      }
      float f = 1.0F;
    }
  }
  
  public final class ActivityResolveInfo
    implements Comparable<ActivityResolveInfo>
  {
    public final ResolveInfo resolveInfo;
    public float weight;
    
    public ActivityResolveInfo(ResolveInfo paramResolveInfo)
    {
      this.resolveInfo = paramResolveInfo;
    }
    
    public int compareTo(ActivityResolveInfo paramActivityResolveInfo)
    {
      return Float.floatToIntBits(paramActivityResolveInfo.weight) - Float.floatToIntBits(this.weight);
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (ActivityResolveInfo)paramObject;
      return Float.floatToIntBits(this.weight) == Float.floatToIntBits(((ActivityResolveInfo)paramObject).weight);
    }
    
    public int hashCode()
    {
      return Float.floatToIntBits(this.weight) + 31;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[");
      localStringBuilder.append("resolveInfo:").append(this.resolveInfo.toString());
      localStringBuilder.append("; weight:").append(new BigDecimal(this.weight));
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  public static abstract interface ActivitySorter
  {
    public abstract void sort(Intent paramIntent, List<ActivityChooserModel.ActivityResolveInfo> paramList, List<ActivityChooserModel.HistoricalRecord> paramList1);
  }
  
  public static final class HistoricalRecord
  {
    public final ComponentName activity;
    public final long time;
    public final float weight;
    
    public HistoricalRecord(ComponentName paramComponentName, long paramLong, float paramFloat)
    {
      this.activity = paramComponentName;
      this.time = paramLong;
      this.weight = paramFloat;
    }
    
    public HistoricalRecord(String paramString, long paramLong, float paramFloat)
    {
      this(ComponentName.unflattenFromString(paramString), paramLong, paramFloat);
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (HistoricalRecord)paramObject;
      if (this.activity == null)
      {
        if (((HistoricalRecord)paramObject).activity != null) {
          return false;
        }
      }
      else if (!this.activity.equals(((HistoricalRecord)paramObject).activity)) {
        return false;
      }
      if (this.time != ((HistoricalRecord)paramObject).time) {
        return false;
      }
      return Float.floatToIntBits(this.weight) == Float.floatToIntBits(((HistoricalRecord)paramObject).weight);
    }
    
    public int hashCode()
    {
      if (this.activity == null) {}
      for (int i = 0;; i = this.activity.hashCode()) {
        return ((i + 31) * 31 + (int)(this.time ^ this.time >>> 32)) * 31 + Float.floatToIntBits(this.weight);
      }
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[");
      localStringBuilder.append("; activity:").append(this.activity);
      localStringBuilder.append("; time:").append(this.time);
      localStringBuilder.append("; weight:").append(new BigDecimal(this.weight));
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  public static abstract interface OnChooseActivityListener
  {
    public abstract boolean onChooseActivity(ActivityChooserModel paramActivityChooserModel, Intent paramIntent);
  }
  
  private final class PersistHistoryAsyncTask
    extends AsyncTask<Object, Void, Void>
  {
    PersistHistoryAsyncTask() {}
    
    /* Error */
    public Void doInBackground(Object... paramVarArgs)
    {
      // Byte code:
      //   0: aload_1
      //   1: iconst_0
      //   2: aaload
      //   3: checkcast 33	java/util/List
      //   6: astore 4
      //   8: aload_1
      //   9: iconst_1
      //   10: aaload
      //   11: checkcast 35	java/lang/String
      //   14: astore 5
      //   16: aload_0
      //   17: getfield 14	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   20: getfield 39	android/support/v7/widget/ActivityChooserModel:mContext	Landroid/content/Context;
      //   23: aload 5
      //   25: iconst_0
      //   26: invokevirtual 45	android/content/Context:openFileOutput	(Ljava/lang/String;I)Ljava/io/FileOutputStream;
      //   29: astore_1
      //   30: invokestatic 51	android/util/Xml:newSerializer	()Lorg/xmlpull/v1/XmlSerializer;
      //   33: astore 5
      //   35: aload 5
      //   37: aload_1
      //   38: aconst_null
      //   39: invokeinterface 57 3 0
      //   44: aload 5
      //   46: ldc 59
      //   48: iconst_1
      //   49: invokestatic 65	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
      //   52: invokeinterface 69 3 0
      //   57: aload 5
      //   59: aconst_null
      //   60: ldc 71
      //   62: invokeinterface 75 3 0
      //   67: pop
      //   68: aload 4
      //   70: invokeinterface 79 1 0
      //   75: istore_3
      //   76: iconst_0
      //   77: istore_2
      //   78: iload_2
      //   79: iload_3
      //   80: if_icmpge +133 -> 213
      //   83: aload 4
      //   85: iconst_0
      //   86: invokeinterface 83 2 0
      //   91: checkcast 85	android/support/v7/widget/ActivityChooserModel$HistoricalRecord
      //   94: astore 6
      //   96: aload 5
      //   98: aconst_null
      //   99: ldc 87
      //   101: invokeinterface 75 3 0
      //   106: pop
      //   107: aload 5
      //   109: aconst_null
      //   110: ldc 89
      //   112: aload 6
      //   114: getfield 92	android/support/v7/widget/ActivityChooserModel$HistoricalRecord:activity	Landroid/content/ComponentName;
      //   117: invokevirtual 98	android/content/ComponentName:flattenToString	()Ljava/lang/String;
      //   120: invokeinterface 102 4 0
      //   125: pop
      //   126: aload 5
      //   128: aconst_null
      //   129: ldc 104
      //   131: aload 6
      //   133: getfield 107	android/support/v7/widget/ActivityChooserModel$HistoricalRecord:time	J
      //   136: invokestatic 110	java/lang/String:valueOf	(J)Ljava/lang/String;
      //   139: invokeinterface 102 4 0
      //   144: pop
      //   145: aload 5
      //   147: aconst_null
      //   148: ldc 112
      //   150: aload 6
      //   152: getfield 115	android/support/v7/widget/ActivityChooserModel$HistoricalRecord:weight	F
      //   155: invokestatic 118	java/lang/String:valueOf	(F)Ljava/lang/String;
      //   158: invokeinterface 102 4 0
      //   163: pop
      //   164: aload 5
      //   166: aconst_null
      //   167: ldc 87
      //   169: invokeinterface 121 3 0
      //   174: pop
      //   175: iload_2
      //   176: iconst_1
      //   177: iadd
      //   178: istore_2
      //   179: goto -101 -> 78
      //   182: astore_1
      //   183: getstatic 125	android/support/v7/widget/ActivityChooserModel:LOG_TAG	Ljava/lang/String;
      //   186: new 127	java/lang/StringBuilder
      //   189: dup
      //   190: invokespecial 128	java/lang/StringBuilder:<init>	()V
      //   193: ldc -126
      //   195: invokevirtual 134	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   198: aload 5
      //   200: invokevirtual 134	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   203: invokevirtual 137	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   206: aload_1
      //   207: invokestatic 143	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   210: pop
      //   211: aconst_null
      //   212: areturn
      //   213: aload 5
      //   215: aconst_null
      //   216: ldc 71
      //   218: invokeinterface 121 3 0
      //   223: pop
      //   224: aload 5
      //   226: invokeinterface 146 1 0
      //   231: aload_0
      //   232: getfield 14	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   235: iconst_1
      //   236: putfield 150	android/support/v7/widget/ActivityChooserModel:mCanReadHistoricalData	Z
      //   239: aload_1
      //   240: ifnull +7 -> 247
      //   243: aload_1
      //   244: invokevirtual 155	java/io/FileOutputStream:close	()V
      //   247: aconst_null
      //   248: areturn
      //   249: astore_1
      //   250: goto -3 -> 247
      //   253: astore 4
      //   255: getstatic 125	android/support/v7/widget/ActivityChooserModel:LOG_TAG	Ljava/lang/String;
      //   258: new 127	java/lang/StringBuilder
      //   261: dup
      //   262: invokespecial 128	java/lang/StringBuilder:<init>	()V
      //   265: ldc -126
      //   267: invokevirtual 134	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   270: aload_0
      //   271: getfield 14	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   274: getfield 158	android/support/v7/widget/ActivityChooserModel:mHistoryFileName	Ljava/lang/String;
      //   277: invokevirtual 134	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   280: invokevirtual 137	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   283: aload 4
      //   285: invokestatic 143	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   288: pop
      //   289: aload_0
      //   290: getfield 14	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   293: iconst_1
      //   294: putfield 150	android/support/v7/widget/ActivityChooserModel:mCanReadHistoricalData	Z
      //   297: aload_1
      //   298: ifnull -51 -> 247
      //   301: aload_1
      //   302: invokevirtual 155	java/io/FileOutputStream:close	()V
      //   305: goto -58 -> 247
      //   308: astore_1
      //   309: goto -62 -> 247
      //   312: astore 4
      //   314: getstatic 125	android/support/v7/widget/ActivityChooserModel:LOG_TAG	Ljava/lang/String;
      //   317: new 127	java/lang/StringBuilder
      //   320: dup
      //   321: invokespecial 128	java/lang/StringBuilder:<init>	()V
      //   324: ldc -126
      //   326: invokevirtual 134	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   329: aload_0
      //   330: getfield 14	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   333: getfield 158	android/support/v7/widget/ActivityChooserModel:mHistoryFileName	Ljava/lang/String;
      //   336: invokevirtual 134	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   339: invokevirtual 137	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   342: aload 4
      //   344: invokestatic 143	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   347: pop
      //   348: aload_0
      //   349: getfield 14	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   352: iconst_1
      //   353: putfield 150	android/support/v7/widget/ActivityChooserModel:mCanReadHistoricalData	Z
      //   356: aload_1
      //   357: ifnull -110 -> 247
      //   360: aload_1
      //   361: invokevirtual 155	java/io/FileOutputStream:close	()V
      //   364: goto -117 -> 247
      //   367: astore_1
      //   368: goto -121 -> 247
      //   371: astore 4
      //   373: getstatic 125	android/support/v7/widget/ActivityChooserModel:LOG_TAG	Ljava/lang/String;
      //   376: new 127	java/lang/StringBuilder
      //   379: dup
      //   380: invokespecial 128	java/lang/StringBuilder:<init>	()V
      //   383: ldc -126
      //   385: invokevirtual 134	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   388: aload_0
      //   389: getfield 14	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   392: getfield 158	android/support/v7/widget/ActivityChooserModel:mHistoryFileName	Ljava/lang/String;
      //   395: invokevirtual 134	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   398: invokevirtual 137	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   401: aload 4
      //   403: invokestatic 143	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   406: pop
      //   407: aload_0
      //   408: getfield 14	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   411: iconst_1
      //   412: putfield 150	android/support/v7/widget/ActivityChooserModel:mCanReadHistoricalData	Z
      //   415: aload_1
      //   416: ifnull -169 -> 247
      //   419: aload_1
      //   420: invokevirtual 155	java/io/FileOutputStream:close	()V
      //   423: goto -176 -> 247
      //   426: astore_1
      //   427: goto -180 -> 247
      //   430: astore 4
      //   432: aload_0
      //   433: getfield 14	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   436: iconst_1
      //   437: putfield 150	android/support/v7/widget/ActivityChooserModel:mCanReadHistoricalData	Z
      //   440: aload_1
      //   441: ifnull +7 -> 448
      //   444: aload_1
      //   445: invokevirtual 155	java/io/FileOutputStream:close	()V
      //   448: aload 4
      //   450: athrow
      //   451: astore_1
      //   452: goto -4 -> 448
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	455	0	this	PersistHistoryAsyncTask
      //   0	455	1	paramVarArgs	Object[]
      //   77	102	2	i	int
      //   75	6	3	j	int
      //   6	78	4	localList	List
      //   253	31	4	localIOException	java.io.IOException
      //   312	31	4	localIllegalStateException	IllegalStateException
      //   371	31	4	localIllegalArgumentException	IllegalArgumentException
      //   430	19	4	localObject1	Object
      //   14	211	5	localObject2	Object
      //   94	57	6	localHistoricalRecord	ActivityChooserModel.HistoricalRecord
      // Exception table:
      //   from	to	target	type
      //   16	30	182	java/io/FileNotFoundException
      //   243	247	249	java/io/IOException
      //   35	76	253	java/io/IOException
      //   83	175	253	java/io/IOException
      //   213	231	253	java/io/IOException
      //   301	305	308	java/io/IOException
      //   35	76	312	java/lang/IllegalStateException
      //   83	175	312	java/lang/IllegalStateException
      //   213	231	312	java/lang/IllegalStateException
      //   360	364	367	java/io/IOException
      //   35	76	371	java/lang/IllegalArgumentException
      //   83	175	371	java/lang/IllegalArgumentException
      //   213	231	371	java/lang/IllegalArgumentException
      //   419	423	426	java/io/IOException
      //   35	76	430	finally
      //   83	175	430	finally
      //   213	231	430	finally
      //   255	289	430	finally
      //   314	348	430	finally
      //   373	407	430	finally
      //   444	448	451	java/io/IOException
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\ActivityChooserModel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */