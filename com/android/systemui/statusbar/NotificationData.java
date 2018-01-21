package com.android.systemui.statusbar;

import android.app.Notification;
import android.app.Notification.Builder;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService.Ranking;
import android.service.notification.NotificationListenerService.RankingMap;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Chronometer;
import android.widget.RemoteViews;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

public class NotificationData
{
  private final String TAG = "NotificationData";
  private final ArrayMap<String, Entry> mEntries = new ArrayMap();
  private final Environment mEnvironment;
  private NotificationGroupManager mGroupManager;
  private HeadsUpManager mHeadsUpManager;
  private StatusBarNotification mHighlightHintNotification;
  private final Comparator<Entry> mRankingComparator = new Comparator()
  {
    private final NotificationListenerService.Ranking mRankingA = new NotificationListenerService.Ranking();
    private final NotificationListenerService.Ranking mRankingB = new NotificationListenerService.Ranking();
    
    public int compare(NotificationData.Entry paramAnonymousEntry1, NotificationData.Entry paramAnonymousEntry2)
    {
      StatusBarNotification localStatusBarNotification1 = paramAnonymousEntry1.notification;
      StatusBarNotification localStatusBarNotification2 = paramAnonymousEntry2.notification;
      int k = 3;
      int n = 3;
      int m = 0;
      int i1 = 0;
      if (NotificationData.-get2(NotificationData.this) != null)
      {
        NotificationData.-get2(NotificationData.this).getRanking(paramAnonymousEntry1.key, this.mRankingA);
        NotificationData.-get2(NotificationData.this).getRanking(paramAnonymousEntry2.key, this.mRankingB);
        k = this.mRankingA.getImportance();
        n = this.mRankingB.getImportance();
        m = this.mRankingA.getRank();
        i1 = this.mRankingB.getRank();
      }
      String str = NotificationData.-get0(NotificationData.this).getCurrentMediaNotificationKey();
      int i;
      int j;
      label163:
      boolean bool1;
      if (paramAnonymousEntry1.key.equals(str)) {
        if (k > 1)
        {
          i = 1;
          if (!paramAnonymousEntry2.key.equals(str)) {
            break label233;
          }
          if (n <= 1) {
            break label227;
          }
          j = 1;
          if (k < 5) {
            break label239;
          }
          bool1 = NotificationData.-wrap0(localStatusBarNotification1);
          label176:
          if (n < 5) {
            break label245;
          }
        }
      }
      boolean bool3;
      label227:
      label233:
      label239:
      label245:
      for (boolean bool2 = NotificationData.-wrap0(localStatusBarNotification2);; bool2 = false)
      {
        bool3 = paramAnonymousEntry1.row.isHeadsUp();
        if (bool3 == paramAnonymousEntry2.row.isHeadsUp()) {
          break label253;
        }
        if (!bool3) {
          break label251;
        }
        return -1;
        i = 0;
        break;
        i = 0;
        break;
        j = 0;
        break label163;
        j = 0;
        break label163;
        bool1 = false;
        break label176;
      }
      label251:
      return 1;
      label253:
      if (bool3) {
        return NotificationData.-get1(NotificationData.this).compare(paramAnonymousEntry1, paramAnonymousEntry2);
      }
      if (i != j)
      {
        if (i != 0) {
          return -1;
        }
        return 1;
      }
      if (bool1 != bool2)
      {
        if (bool1) {
          return -1;
        }
        return 1;
      }
      if (m != i1) {
        return m - i1;
      }
      return (int)(localStatusBarNotification2.getNotification().when - localStatusBarNotification1.getNotification().when);
    }
  };
  private NotificationListenerService.RankingMap mRankingMap;
  private boolean mShowHighlightNotification;
  private final ArrayList<Entry> mSortedAndFiltered = new ArrayList();
  private final NotificationListenerService.Ranking mTmpRanking = new NotificationListenerService.Ranking();
  
  public NotificationData(Environment paramEnvironment)
  {
    this.mEnvironment = paramEnvironment;
    this.mGroupManager = paramEnvironment.getGroupManager();
  }
  
  private void dumpEntry(PrintWriter paramPrintWriter, String paramString, int paramInt, Entry paramEntry)
  {
    this.mRankingMap.getRanking(paramEntry.key, this.mTmpRanking);
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("  [" + paramInt + "] key=" + paramEntry.key + " icon=" + paramEntry.icon);
    paramEntry = paramEntry.notification;
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("      pkg=" + paramEntry.getPackageName() + " id=" + paramEntry.getId() + " importance=" + this.mTmpRanking.getImportance());
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("      notification=" + paramEntry.getNotification());
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("      tickerText=\"" + paramEntry.getNotification().tickerText + "\"");
  }
  
  private static boolean isSystemNotification(StatusBarNotification paramStatusBarNotification)
  {
    paramStatusBarNotification = paramStatusBarNotification.getPackageName();
    if (!"android".equals(paramStatusBarNotification)) {
      return "com.android.systemui".equals(paramStatusBarNotification);
    }
    return true;
  }
  
  public static boolean showNotificationEvenIfUnprovisioned(StatusBarNotification paramStatusBarNotification)
  {
    if ("android".equals(paramStatusBarNotification.getPackageName())) {
      return paramStatusBarNotification.getNotification().extras.getBoolean("android.allowDuringSetup");
    }
    return false;
  }
  
  private void updateRankingAndSort(NotificationListenerService.RankingMap arg1)
  {
    if (??? != null) {
      this.mRankingMap = ???;
    }
    synchronized (this.mEntries)
    {
      int j = this.mEntries.size();
      int i = 0;
      while (i < j)
      {
        Entry localEntry = (Entry)this.mEntries.valueAt(i);
        StatusBarNotification localStatusBarNotification = localEntry.notification.clone();
        String str = getOverrideGroupKey(localEntry.key);
        if (!Objects.equals(localStatusBarNotification.getOverrideGroupKey(), str))
        {
          localEntry.notification.setOverrideGroupKey(str);
          this.mGroupManager.onEntryUpdated(localEntry, localStatusBarNotification);
        }
        i += 1;
      }
      filterAndSort();
      return;
    }
  }
  
  public void add(Entry paramEntry, NotificationListenerService.RankingMap paramRankingMap)
  {
    synchronized (this.mEntries)
    {
      this.mEntries.put(paramEntry.notification.getKey(), paramEntry);
      this.mGroupManager.onEntryAdded(paramEntry);
      updateRankingAndSort(paramRankingMap);
      return;
    }
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    int j = this.mSortedAndFiltered.size();
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("active notifications: " + j);
    int i = 0;
    while (i < j)
    {
      dumpEntry(paramPrintWriter, paramString, i, (Entry)this.mSortedAndFiltered.get(i));
      i += 1;
    }
    synchronized (this.mEntries)
    {
      int m = this.mEntries.size();
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("inactive notifications: " + (m - i));
      j = 0;
      i = 0;
      while (i < m)
      {
        Entry localEntry = (Entry)this.mEntries.valueAt(i);
        int k = j;
        if (!this.mSortedAndFiltered.contains(localEntry))
        {
          dumpEntry(paramPrintWriter, paramString, j, localEntry);
          k = j + 1;
        }
        i += 1;
        j = k;
      }
      return;
    }
  }
  
  public boolean filterAndSort()
  {
    this.mSortedAndFiltered.clear();
    this.mShowHighlightNotification = false;
    this.mHighlightHintNotification = null;
    for (;;)
    {
      int i;
      int k;
      synchronized (this.mEntries)
      {
        int n = this.mEntries.size();
        j = 0;
        i = 0;
        if (i < n)
        {
          Entry localEntry = (Entry)this.mEntries.valueAt(i);
          StatusBarNotification localStatusBarNotification = localEntry.notification;
          Notification localNotification = localStatusBarNotification.getNotification();
          k = j;
          if (localNotification.showOnStatusBar())
          {
            int m = localNotification.getPriorityOnStatusBar();
            k = j;
            if (m > j)
            {
              this.mShowHighlightNotification = true;
              this.mHighlightHintNotification = localStatusBarNotification;
              k = m;
            }
          }
          if (shouldFilterOut(localStatusBarNotification)) {
            break label160;
          }
          this.mSortedAndFiltered.add(localEntry);
        }
      }
      Collections.sort(this.mSortedAndFiltered, this.mRankingComparator);
      return false;
      label160:
      i += 1;
      int j = k;
    }
  }
  
  public Entry get(String paramString)
  {
    return (Entry)this.mEntries.get(paramString);
  }
  
  public ArrayList<Entry> getActiveNotifications()
  {
    return this.mSortedAndFiltered;
  }
  
  public StatusBarNotification getHighlightHintNotification()
  {
    return this.mHighlightHintNotification;
  }
  
  public int getImportance(String paramString)
  {
    if (this.mRankingMap != null)
    {
      this.mRankingMap.getRanking(paramString, this.mTmpRanking);
      return this.mTmpRanking.getImportance();
    }
    return 64536;
  }
  
  public Chronometer getKeyguardChronometer()
  {
    if (this.mHighlightHintNotification == null) {
      return null;
    }
    return get(this.mHighlightHintNotification.getKey()).keyguardChronometer;
  }
  
  public String getOverrideGroupKey(String paramString)
  {
    if (this.mRankingMap != null)
    {
      this.mRankingMap.getRanking(paramString, this.mTmpRanking);
      return this.mTmpRanking.getOverrideGroupKey();
    }
    return null;
  }
  
  public Chronometer getStatusBarChronometer()
  {
    if (this.mHighlightHintNotification == null) {
      return null;
    }
    return get(this.mHighlightHintNotification.getKey()).statusBarChronometer;
  }
  
  public int getVisibilityOverride(String paramString)
  {
    if (this.mRankingMap != null)
    {
      this.mRankingMap.getRanking(paramString, this.mTmpRanking);
      return this.mTmpRanking.getVisibilityOverride();
    }
    return 64536;
  }
  
  public boolean hasActiveClearableNotifications()
  {
    Iterator localIterator = this.mSortedAndFiltered.iterator();
    while (localIterator.hasNext())
    {
      Entry localEntry = (Entry)localIterator.next();
      if ((localEntry.getContentView() != null) && (localEntry.notification.isClearable())) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isAmbient(String paramString)
  {
    if (this.mRankingMap != null)
    {
      this.mRankingMap.getRanking(paramString, this.mTmpRanking);
      return this.mTmpRanking.isAmbient();
    }
    return false;
  }
  
  public boolean isLock(String paramString)
  {
    if (this.mRankingMap != null)
    {
      this.mRankingMap.getRanking(paramString, this.mTmpRanking);
      return this.mTmpRanking.isLock();
    }
    return false;
  }
  
  public Entry remove(String paramString, NotificationListenerService.RankingMap paramRankingMap)
  {
    synchronized (this.mEntries)
    {
      paramString = (Entry)this.mEntries.remove(paramString);
      if (paramString == null) {
        return null;
      }
    }
    this.mGroupManager.onEntryRemoved(paramString);
    updateRankingAndSort(paramRankingMap);
    return paramString;
  }
  
  public void setHeadsUpManager(HeadsUpManager paramHeadsUpManager)
  {
    this.mHeadsUpManager = paramHeadsUpManager;
  }
  
  boolean shouldFilterOut(StatusBarNotification paramStatusBarNotification)
  {
    if (!this.mEnvironment.isDeviceProvisioned()) {}
    for (boolean bool = showNotificationEvenIfUnprovisioned(paramStatusBarNotification); !bool; bool = true) {
      return true;
    }
    if (!this.mEnvironment.isNotificationForCurrentProfiles(paramStatusBarNotification)) {
      return true;
    }
    if ((this.mEnvironment.onSecureLockScreen()) && ((paramStatusBarNotification.getNotification().visibility == -1) || (this.mEnvironment.shouldHideNotifications(paramStatusBarNotification.getUserId())) || (this.mEnvironment.shouldHideNotifications(paramStatusBarNotification.getKey())))) {
      return true;
    }
    return (!BaseStatusBar.ENABLE_CHILD_NOTIFICATIONS) && (this.mGroupManager.isChildInGroupWithSummary(paramStatusBarNotification));
  }
  
  public boolean shouldSuppressScreenOff(String paramString)
  {
    boolean bool = false;
    if (this.mRankingMap != null)
    {
      this.mRankingMap.getRanking(paramString, this.mTmpRanking);
      if ((this.mTmpRanking.getSuppressedVisualEffects() & 0x1) != 0) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  public boolean shouldSuppressScreenOn(String paramString)
  {
    boolean bool = false;
    if (this.mRankingMap != null)
    {
      this.mRankingMap.getRanking(paramString, this.mTmpRanking);
      if ((this.mTmpRanking.getSuppressedVisualEffects() & 0x2) != 0) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  public boolean showNotification()
  {
    return this.mShowHighlightNotification;
  }
  
  public void updateRanking(NotificationListenerService.RankingMap paramRankingMap)
  {
    updateRankingAndSort(paramRankingMap);
  }
  
  public static final class Entry
  {
    public boolean autoRedacted;
    public RemoteViews cachedBigContentView;
    public RemoteViews cachedContentView;
    public RemoteViews cachedHeadsUpContentView;
    public RemoteViews cachedPublicContentView;
    public StatusBarIconView icon;
    private boolean interruption;
    public String key;
    public Chronometer keyguardChronometer = null;
    private long lastFullScreenIntentLaunchTime = -2000L;
    public boolean legacy;
    public StatusBarNotification notification;
    public CharSequence remoteInputText;
    public ExpandableNotificationRow row;
    public Chronometer statusBarChronometer = null;
    public int targetSdk;
    
    public Entry(StatusBarNotification paramStatusBarNotification, StatusBarIconView paramStatusBarIconView)
    {
      this.key = paramStatusBarNotification.getKey();
      this.notification = paramStatusBarNotification;
      this.icon = paramStatusBarIconView;
    }
    
    private boolean compareRemoteViews(RemoteViews paramRemoteViews1, RemoteViews paramRemoteViews2)
    {
      if ((paramRemoteViews1 == null) && (paramRemoteViews2 == null)) {}
      do
      {
        return true;
        if ((paramRemoteViews1 == null) || (paramRemoteViews2 == null) || (paramRemoteViews2.getPackage() == null) || (paramRemoteViews1.getPackage() == null) || (!paramRemoteViews1.getPackage().equals(paramRemoteViews2.getPackage()))) {
          break;
        }
      } while (paramRemoteViews1.getLayoutId() == paramRemoteViews2.getLayoutId());
      return false;
      return false;
    }
    
    private void initChronometers(Context paramContext, Notification paramNotification)
    {
      this.statusBarChronometer = new Chronometer(paramContext);
      this.statusBarChronometer.setBase(paramNotification.getChronometerBase());
      this.keyguardChronometer = new Chronometer(paramContext);
      this.keyguardChronometer.setBase(paramNotification.getChronometerBase());
    }
    
    public boolean cacheContentViews(Context paramContext, Notification paramNotification)
    {
      if (paramNotification != null)
      {
        Object localObject = Notification.Builder.recoverBuilder(paramContext, paramNotification);
        paramContext = ((Notification.Builder)localObject).createContentView();
        RemoteViews localRemoteViews1 = ((Notification.Builder)localObject).createBigContentView();
        RemoteViews localRemoteViews2 = ((Notification.Builder)localObject).createHeadsUpContentView();
        localObject = ((Notification.Builder)localObject).makePublicContentView();
        boolean bool = Objects.equals(Boolean.valueOf(this.notification.getNotification().extras.getBoolean("android.contains.customView")), Boolean.valueOf(paramNotification.extras.getBoolean("android.contains.customView")));
        if ((compareRemoteViews(this.cachedContentView, paramContext)) && (compareRemoteViews(this.cachedBigContentView, localRemoteViews1)) && (compareRemoteViews(this.cachedHeadsUpContentView, localRemoteViews2)) && (compareRemoteViews(this.cachedPublicContentView, (RemoteViews)localObject))) {}
        for (;;)
        {
          this.cachedPublicContentView = ((RemoteViews)localObject);
          this.cachedHeadsUpContentView = localRemoteViews2;
          this.cachedBigContentView = localRemoteViews1;
          this.cachedContentView = paramContext;
          return bool;
          bool = false;
        }
      }
      paramContext = Notification.Builder.recoverBuilder(paramContext, this.notification.getNotification());
      this.cachedContentView = paramContext.createContentView();
      this.cachedBigContentView = paramContext.createBigContentView();
      this.cachedHeadsUpContentView = paramContext.createHeadsUpContentView();
      this.cachedPublicContentView = paramContext.makePublicContentView();
      return false;
    }
    
    public void createChronometer(Context paramContext)
    {
      Notification localNotification = this.notification.getNotification();
      if (localNotification.getChronometerState() == 0) {
        initChronometers(paramContext, localNotification);
      }
      while (localNotification.getChronometerState() == 0)
      {
        this.statusBarChronometer.start();
        this.keyguardChronometer.start();
        return;
        if (this.statusBarChronometer == null)
        {
          initChronometers(paramContext, localNotification);
        }
        else
        {
          this.statusBarChronometer.setBase(localNotification.getChronometerBase());
          this.keyguardChronometer.setBase(localNotification.getChronometerBase());
        }
      }
      this.statusBarChronometer.stop();
      this.keyguardChronometer.stop();
    }
    
    public View getContentView()
    {
      return this.row.getPrivateLayout().getContractedChild();
    }
    
    public View getExpandedContentView()
    {
      return this.row.getPrivateLayout().getExpandedChild();
    }
    
    public View getHeadsUpContentView()
    {
      return this.row.getPrivateLayout().getHeadsUpChild();
    }
    
    public View getPublicContentView()
    {
      return this.row.getPublicLayout().getContractedChild();
    }
    
    public boolean hasInterrupted()
    {
      return this.interruption;
    }
    
    public boolean hasJustLaunchedFullScreenIntent()
    {
      return SystemClock.elapsedRealtime() < this.lastFullScreenIntentLaunchTime + 2000L;
    }
    
    public void notifyFullScreenIntentLaunched()
    {
      this.lastFullScreenIntentLaunchTime = SystemClock.elapsedRealtime();
    }
    
    public void reset()
    {
      this.autoRedacted = false;
      this.legacy = false;
      this.lastFullScreenIntentLaunchTime = -2000L;
      if (this.row != null) {
        this.row.reset();
      }
    }
    
    public void setInterruption()
    {
      this.interruption = true;
    }
  }
  
  public static abstract interface Environment
  {
    public abstract String getCurrentMediaNotificationKey();
    
    public abstract NotificationGroupManager getGroupManager();
    
    public abstract boolean isDeviceProvisioned();
    
    public abstract boolean isNotificationForCurrentProfiles(StatusBarNotification paramStatusBarNotification);
    
    public abstract boolean onSecureLockScreen();
    
    public abstract boolean shouldHideNotifications(int paramInt);
    
    public abstract boolean shouldHideNotifications(String paramString);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\NotificationData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */