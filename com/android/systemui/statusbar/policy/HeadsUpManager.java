package com.android.systemui.statusbar.policy;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Region;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings.Global;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.util.Pools.Pool;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.InternalInsetsInfo;
import android.view.ViewTreeObserver.OnComputeInternalInsetsListener;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.NotificationData.Entry;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

public class HeadsUpManager
  implements ViewTreeObserver.OnComputeInternalInsetsListener
{
  private PhoneStatusBar mBar;
  private Clock mClock;
  private final Context mContext;
  private final int mDefaultSnoozeLengthMs;
  private HashSet<NotificationData.Entry> mEntriesToRemoveAfterExpand = new HashSet();
  private final Pools.Pool<HeadsUpEntry> mEntryPool = new Pools.Pool()
  {
    private Stack<HeadsUpManager.HeadsUpEntry> mPoolObjects = new Stack();
    
    public HeadsUpManager.HeadsUpEntry acquire()
    {
      if (!this.mPoolObjects.isEmpty()) {
        return (HeadsUpManager.HeadsUpEntry)this.mPoolObjects.pop();
      }
      return new HeadsUpManager.HeadsUpEntry(HeadsUpManager.this);
    }
    
    public boolean release(HeadsUpManager.HeadsUpEntry paramAnonymousHeadsUpEntry)
    {
      paramAnonymousHeadsUpEntry.reset();
      this.mPoolObjects.push(paramAnonymousHeadsUpEntry);
      return true;
    }
  };
  private final NotificationGroupManager mGroupManager;
  private final Handler mHandler = new Handler();
  private boolean mHasPinnedNotification;
  private HashMap<String, HeadsUpEntry> mHeadsUpEntries = new HashMap();
  private boolean mHeadsUpGoingAway;
  private final int mHeadsUpNotificationDecay;
  private boolean mIsExpanded;
  private boolean mIsObserving;
  private final HashSet<OnHeadsUpChangedListener> mListeners = new HashSet();
  private final int mMinimumDisplayTime;
  private boolean mReleaseOnExpandFinish;
  private ContentObserver mSettingsObserver;
  private int mSnoozeLengthMs;
  private final ArrayMap<String, Long> mSnoozedPackages;
  private final int mStatusBarHeight;
  private final View mStatusBarWindowView;
  private HashSet<String> mSwipedOutKeys = new HashSet();
  private int[] mTmpTwoArray = new int[2];
  private final int mTouchAcceptanceDelay;
  private boolean mTrackingHeadsUp;
  private int mUser;
  private boolean mWaitingOnCollapseWhenGoingAway;
  
  public HeadsUpManager(final Context paramContext, View paramView, NotificationGroupManager paramNotificationGroupManager)
  {
    this.mContext = paramContext;
    Resources localResources = this.mContext.getResources();
    this.mTouchAcceptanceDelay = localResources.getInteger(2131623990);
    this.mSnoozedPackages = new ArrayMap();
    this.mDefaultSnoozeLengthMs = localResources.getInteger(2131623988);
    this.mSnoozeLengthMs = this.mDefaultSnoozeLengthMs;
    this.mMinimumDisplayTime = localResources.getInteger(2131623989);
    this.mHeadsUpNotificationDecay = localResources.getInteger(2131623987);
    this.mClock = new Clock();
    this.mSnoozeLengthMs = Settings.Global.getInt(paramContext.getContentResolver(), "heads_up_snooze_length_ms", this.mDefaultSnoozeLengthMs);
    this.mSettingsObserver = new ContentObserver(this.mHandler)
    {
      public void onChange(boolean paramAnonymousBoolean)
      {
        int i = Settings.Global.getInt(paramContext.getContentResolver(), "heads_up_snooze_length_ms", -1);
        if ((i > -1) && (i != HeadsUpManager.-get5(HeadsUpManager.this))) {
          HeadsUpManager.-set0(HeadsUpManager.this, i);
        }
      }
    };
    paramContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("heads_up_snooze_length_ms"), false, this.mSettingsObserver);
    this.mStatusBarWindowView = paramView;
    this.mGroupManager = paramNotificationGroupManager;
    this.mStatusBarHeight = localResources.getDimensionPixelSize(17104921);
  }
  
  private void addHeadsUpEntry(NotificationData.Entry paramEntry)
  {
    Object localObject = (HeadsUpEntry)this.mEntryPool.acquire();
    ((HeadsUpEntry)localObject).setEntry(paramEntry);
    this.mHeadsUpEntries.put(paramEntry.key, localObject);
    paramEntry.row.setHeadsUp(true);
    setEntryPinned((HeadsUpEntry)localObject, shouldHeadsUpBecomePinned(paramEntry));
    localObject = this.mListeners.iterator();
    while (((Iterator)localObject).hasNext()) {
      ((OnHeadsUpChangedListener)((Iterator)localObject).next()).onHeadsUpStateChanged(paramEntry, true);
    }
    paramEntry.row.sendAccessibilityEvent(2048);
  }
  
  private HeadsUpEntry getHeadsUpEntry(String paramString)
  {
    return (HeadsUpEntry)this.mHeadsUpEntries.get(paramString);
  }
  
  private boolean hasFullScreenIntent(NotificationData.Entry paramEntry)
  {
    return paramEntry.notification.getNotification().fullScreenIntent != null;
  }
  
  private boolean hasPinnedNotificationInternal()
  {
    Iterator localIterator = this.mHeadsUpEntries.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (((HeadsUpEntry)this.mHeadsUpEntries.get(str)).entry.row.isPinned()) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isClickedHeadsUpNotification(View paramView)
  {
    paramView = (Boolean)paramView.getTag(2131951684);
    if (paramView != null) {
      return paramView.booleanValue();
    }
    return false;
  }
  
  private void removeHeadsUpEntry(NotificationData.Entry paramEntry)
  {
    HeadsUpEntry localHeadsUpEntry = (HeadsUpEntry)this.mHeadsUpEntries.remove(paramEntry.key);
    paramEntry.row.sendAccessibilityEvent(2048);
    paramEntry.row.setHeadsUp(false);
    setEntryPinned(localHeadsUpEntry, false);
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((OnHeadsUpChangedListener)localIterator.next()).onHeadsUpStateChanged(paramEntry, false);
    }
    this.mEntryPool.release(localHeadsUpEntry);
  }
  
  private void setEntryPinned(HeadsUpEntry paramHeadsUpEntry, boolean paramBoolean)
  {
    paramHeadsUpEntry = paramHeadsUpEntry.entry.row;
    if (paramHeadsUpEntry.isPinned() != paramBoolean)
    {
      paramHeadsUpEntry.setPinned(paramBoolean);
      updatePinnedMode();
      Iterator localIterator = this.mListeners.iterator();
      while (localIterator.hasNext())
      {
        OnHeadsUpChangedListener localOnHeadsUpChangedListener = (OnHeadsUpChangedListener)localIterator.next();
        if (paramBoolean) {
          localOnHeadsUpChangedListener.onHeadsUpPinned(paramHeadsUpEntry);
        } else {
          localOnHeadsUpChangedListener.onHeadsUpUnPinned(paramHeadsUpEntry);
        }
      }
    }
  }
  
  public static void setIsClickedNotification(View paramView, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (Boolean localBoolean = Boolean.valueOf(true);; localBoolean = null)
    {
      paramView.setTag(2131951684, localBoolean);
      return;
    }
  }
  
  private boolean shouldHeadsUpBecomePinned(NotificationData.Entry paramEntry)
  {
    if (this.mIsExpanded) {
      return hasFullScreenIntent(paramEntry);
    }
    return true;
  }
  
  private static String snoozeKey(String paramString, int paramInt)
  {
    return paramInt + "," + paramString;
  }
  
  private void updatePinnedMode()
  {
    boolean bool = hasPinnedNotificationInternal();
    if (bool == this.mHasPinnedNotification) {
      return;
    }
    this.mHasPinnedNotification = bool;
    if (this.mHasPinnedNotification) {
      MetricsLogger.count(this.mContext, "note_peek", 1);
    }
    updateTouchableRegionListener();
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((OnHeadsUpChangedListener)localIterator.next()).onHeadsUpPinnedModeChanged(bool);
    }
  }
  
  private void updateTouchableRegionListener()
  {
    if ((!this.mHasPinnedNotification) && (!this.mHeadsUpGoingAway)) {}
    for (boolean bool = this.mWaitingOnCollapseWhenGoingAway; bool == this.mIsObserving; bool = true) {
      return;
    }
    if (bool)
    {
      this.mStatusBarWindowView.getViewTreeObserver().addOnComputeInternalInsetsListener(this);
      this.mStatusBarWindowView.requestLayout();
    }
    for (;;)
    {
      this.mIsObserving = bool;
      return;
      this.mStatusBarWindowView.getViewTreeObserver().removeOnComputeInternalInsetsListener(this);
    }
  }
  
  private void waitForStatusBarLayout()
  {
    this.mWaitingOnCollapseWhenGoingAway = true;
    this.mStatusBarWindowView.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
    {
      public void onLayoutChange(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6, int paramAnonymousInt7, int paramAnonymousInt8)
      {
        if (HeadsUpManager.-get7(HeadsUpManager.this).getHeight() <= HeadsUpManager.-get6(HeadsUpManager.this))
        {
          HeadsUpManager.-get7(HeadsUpManager.this).removeOnLayoutChangeListener(this);
          HeadsUpManager.-set1(HeadsUpManager.this, false);
          HeadsUpManager.-wrap2(HeadsUpManager.this);
        }
      }
    });
  }
  
  private boolean wasShownLongEnough(String paramString)
  {
    HeadsUpEntry localHeadsUpEntry1 = getHeadsUpEntry(paramString);
    HeadsUpEntry localHeadsUpEntry2 = getTopEntry();
    if (this.mSwipedOutKeys.contains(paramString))
    {
      this.mSwipedOutKeys.remove(paramString);
      return true;
    }
    if (localHeadsUpEntry1 != localHeadsUpEntry2) {
      return true;
    }
    return localHeadsUpEntry1.wasShownLongEnough();
  }
  
  public void addListener(OnHeadsUpChangedListener paramOnHeadsUpChangedListener)
  {
    this.mListeners.add(paramOnHeadsUpChangedListener);
  }
  
  public void addSwipedOutNotification(String paramString)
  {
    this.mSwipedOutKeys.add(paramString);
  }
  
  public int compare(NotificationData.Entry paramEntry1, NotificationData.Entry paramEntry2)
  {
    paramEntry1 = getHeadsUpEntry(paramEntry1.key);
    paramEntry2 = getHeadsUpEntry(paramEntry2.key);
    if ((paramEntry1 == null) || (paramEntry2 == null))
    {
      if (paramEntry1 == null) {
        return 1;
      }
      return -1;
    }
    return paramEntry1.compareTo(paramEntry2);
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("HeadsUpManager state:");
    paramPrintWriter.print("  mTouchAcceptanceDelay=");
    paramPrintWriter.println(this.mTouchAcceptanceDelay);
    paramPrintWriter.print("  mSnoozeLengthMs=");
    paramPrintWriter.println(this.mSnoozeLengthMs);
    paramPrintWriter.print("  now=");
    paramPrintWriter.println(SystemClock.elapsedRealtime());
    paramPrintWriter.print("  mUser=");
    paramPrintWriter.println(this.mUser);
    paramFileDescriptor = this.mHeadsUpEntries.values().iterator();
    while (paramFileDescriptor.hasNext())
    {
      paramArrayOfString = (HeadsUpEntry)paramFileDescriptor.next();
      paramPrintWriter.print("  HeadsUpEntry=");
      paramPrintWriter.println(paramArrayOfString.entry);
    }
    int j = this.mSnoozedPackages.size();
    paramPrintWriter.println("  snoozed packages: " + j);
    int i = 0;
    while (i < j)
    {
      paramPrintWriter.print("    ");
      paramPrintWriter.print(this.mSnoozedPackages.valueAt(i));
      paramPrintWriter.print(", ");
      paramPrintWriter.println((String)this.mSnoozedPackages.keyAt(i));
      i += 1;
    }
  }
  
  public Collection<HeadsUpEntry> getAllEntries()
  {
    return this.mHeadsUpEntries.values();
  }
  
  public NotificationData.Entry getEntry(String paramString)
  {
    return ((HeadsUpEntry)this.mHeadsUpEntries.get(paramString)).entry;
  }
  
  public HeadsUpEntry getTopEntry()
  {
    if (this.mHeadsUpEntries.isEmpty()) {
      return null;
    }
    Object localObject = null;
    Iterator localIterator = this.mHeadsUpEntries.values().iterator();
    while (localIterator.hasNext())
    {
      HeadsUpEntry localHeadsUpEntry = (HeadsUpEntry)localIterator.next();
      if ((localObject == null) || (localHeadsUpEntry.compareTo((HeadsUpEntry)localObject) == -1)) {
        localObject = localHeadsUpEntry;
      }
    }
    return (HeadsUpEntry)localObject;
  }
  
  public int getTopHeadsUpPinnedHeight()
  {
    Object localObject = getTopEntry();
    if ((localObject == null) || (((HeadsUpEntry)localObject).entry == null)) {
      return 0;
    }
    ExpandableNotificationRow localExpandableNotificationRow1 = ((HeadsUpEntry)localObject).entry.row;
    localObject = localExpandableNotificationRow1;
    if (localExpandableNotificationRow1.isChildInGroup())
    {
      ExpandableNotificationRow localExpandableNotificationRow2 = this.mGroupManager.getGroupSummary(localExpandableNotificationRow1.getStatusBarNotification());
      localObject = localExpandableNotificationRow1;
      if (localExpandableNotificationRow2 != null) {
        localObject = localExpandableNotificationRow2;
      }
    }
    return ((ExpandableNotificationRow)localObject).getPinnedHeadsUpHeight(true);
  }
  
  public boolean hasPinnedHeadsUp()
  {
    return this.mHasPinnedNotification;
  }
  
  public boolean isHeadsUp(String paramString)
  {
    return this.mHeadsUpEntries.containsKey(paramString);
  }
  
  public boolean isSnoozed(String paramString)
  {
    Object localObject = snoozeKey(paramString, this.mUser);
    localObject = (Long)this.mSnoozedPackages.get(localObject);
    if (localObject != null)
    {
      if (((Long)localObject).longValue() > SystemClock.elapsedRealtime()) {
        return true;
      }
      this.mSnoozedPackages.remove(paramString);
    }
    return false;
  }
  
  public boolean isTrackingHeadsUp()
  {
    return this.mTrackingHeadsUp;
  }
  
  public void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo paramInternalInsetsInfo)
  {
    if ((this.mIsExpanded) || (this.mBar.isBouncerShowing())) {
      return;
    }
    if (this.mHasPinnedNotification)
    {
      localExpandableNotificationRow1 = getTopEntry().entry.row;
      localObject = localExpandableNotificationRow1;
      if (localExpandableNotificationRow1.isChildInGroup())
      {
        localExpandableNotificationRow2 = this.mGroupManager.getGroupSummary(localExpandableNotificationRow1.getStatusBarNotification());
        localObject = localExpandableNotificationRow1;
        if (localExpandableNotificationRow2 != null) {
          localObject = localExpandableNotificationRow2;
        }
      }
      ((ExpandableNotificationRow)localObject).getLocationOnScreen(this.mTmpTwoArray);
      i = this.mTmpTwoArray[0];
      j = this.mTmpTwoArray[0];
      k = ((ExpandableNotificationRow)localObject).getWidth();
      m = ((ExpandableNotificationRow)localObject).getIntrinsicHeight();
      paramInternalInsetsInfo.setTouchableInsets(3);
      paramInternalInsetsInfo.touchableRegion.set(i, 0, j + k, m);
    }
    while ((!this.mHeadsUpGoingAway) && (!this.mWaitingOnCollapseWhenGoingAway))
    {
      ExpandableNotificationRow localExpandableNotificationRow1;
      Object localObject;
      ExpandableNotificationRow localExpandableNotificationRow2;
      int i;
      int j;
      int k;
      int m;
      return;
    }
    paramInternalInsetsInfo.setTouchableInsets(3);
    paramInternalInsetsInfo.touchableRegion.set(0, 0, this.mStatusBarWindowView.getWidth(), this.mStatusBarHeight);
  }
  
  public void onExpandingFinished()
  {
    if (this.mReleaseOnExpandFinish)
    {
      releaseAllImmediately();
      this.mReleaseOnExpandFinish = false;
    }
    for (;;)
    {
      this.mEntriesToRemoveAfterExpand.clear();
      return;
      Iterator localIterator = this.mEntriesToRemoveAfterExpand.iterator();
      while (localIterator.hasNext())
      {
        NotificationData.Entry localEntry = (NotificationData.Entry)localIterator.next();
        if (isHeadsUp(localEntry.key)) {
          removeHeadsUpEntry(localEntry);
        }
      }
    }
  }
  
  public void releaseAllImmediately()
  {
    Iterator localIterator = new ArrayList(this.mHeadsUpEntries.keySet()).iterator();
    while (localIterator.hasNext()) {
      releaseImmediately((String)localIterator.next());
    }
  }
  
  public void releaseImmediately(String paramString)
  {
    paramString = getHeadsUpEntry(paramString);
    if (paramString == null) {
      return;
    }
    removeHeadsUpEntry(paramString.entry);
  }
  
  public boolean removeNotification(String paramString, boolean paramBoolean)
  {
    if ((wasShownLongEnough(paramString)) || (paramBoolean))
    {
      releaseImmediately(paramString);
      return true;
    }
    getHeadsUpEntry(paramString).removeAsSoonAsPossible();
    return false;
  }
  
  public void setBar(PhoneStatusBar paramPhoneStatusBar)
  {
    this.mBar = paramPhoneStatusBar;
  }
  
  public void setExpanded(NotificationData.Entry paramEntry, boolean paramBoolean)
  {
    paramEntry = (HeadsUpEntry)this.mHeadsUpEntries.get(paramEntry.key);
    if ((paramEntry != null) && (paramEntry.expanded != paramBoolean))
    {
      paramEntry.expanded = paramBoolean;
      if (paramBoolean) {
        paramEntry.removeAutoRemovalCallbacks();
      }
    }
    else
    {
      return;
    }
    paramEntry.updateEntry(false);
  }
  
  public void setHeadsUpGoingAway(boolean paramBoolean)
  {
    if (paramBoolean != this.mHeadsUpGoingAway)
    {
      this.mHeadsUpGoingAway = paramBoolean;
      if (!paramBoolean) {
        waitForStatusBarLayout();
      }
      updateTouchableRegionListener();
    }
  }
  
  public void setIsExpanded(boolean paramBoolean)
  {
    if (paramBoolean != this.mIsExpanded)
    {
      this.mIsExpanded = paramBoolean;
      if (paramBoolean)
      {
        this.mWaitingOnCollapseWhenGoingAway = false;
        this.mHeadsUpGoingAway = false;
        updateTouchableRegionListener();
      }
    }
  }
  
  public void setRemoteInputActive(NotificationData.Entry paramEntry, boolean paramBoolean)
  {
    paramEntry = (HeadsUpEntry)this.mHeadsUpEntries.get(paramEntry.key);
    if ((paramEntry != null) && (paramEntry.remoteInputActive != paramBoolean))
    {
      paramEntry.remoteInputActive = paramBoolean;
      if (paramBoolean) {
        paramEntry.removeAutoRemovalCallbacks();
      }
    }
    else
    {
      return;
    }
    paramEntry.updateEntry(false);
  }
  
  public void setTrackingHeadsUp(boolean paramBoolean)
  {
    this.mTrackingHeadsUp = paramBoolean;
  }
  
  public void setUser(int paramInt)
  {
    this.mUser = paramInt;
  }
  
  public boolean shouldSwallowClick(String paramString)
  {
    paramString = (HeadsUpEntry)this.mHeadsUpEntries.get(paramString);
    return (paramString != null) && (this.mClock.currentTimeMillis() < paramString.postTime);
  }
  
  public void showNotification(NotificationData.Entry paramEntry)
  {
    addHeadsUpEntry(paramEntry);
    updateNotification(paramEntry, true);
    paramEntry.setInterruption();
  }
  
  public void snooze()
  {
    Iterator localIterator = this.mHeadsUpEntries.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (String)localIterator.next();
      localObject = (HeadsUpEntry)this.mHeadsUpEntries.get(localObject);
      String str = ((HeadsUpEntry)localObject).entry.notification.getPackageName();
      this.mSnoozedPackages.put(snoozeKey(str, this.mUser), Long.valueOf(SystemClock.elapsedRealtime() + this.mSnoozeLengthMs));
      localObject = ((HeadsUpEntry)localObject).entry.notification.getNotification().getSwipeUpHeadsUpIntent();
      if (localObject != null) {
        try
        {
          ((PendingIntent)localObject).send();
        }
        catch (PendingIntent.CanceledException localCanceledException)
        {
          localCanceledException.printStackTrace();
        }
      }
    }
    this.mReleaseOnExpandFinish = true;
  }
  
  public void unpinAll()
  {
    Iterator localIterator = this.mHeadsUpEntries.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (String)localIterator.next();
      localObject = (HeadsUpEntry)this.mHeadsUpEntries.get(localObject);
      setEntryPinned((HeadsUpEntry)localObject, false);
      ((HeadsUpEntry)localObject).updateEntry(false);
    }
  }
  
  public void updateNotification(NotificationData.Entry paramEntry, boolean paramBoolean)
  {
    paramEntry.row.sendAccessibilityEvent(2048);
    if (paramBoolean)
    {
      HeadsUpEntry localHeadsUpEntry = (HeadsUpEntry)this.mHeadsUpEntries.get(paramEntry.key);
      if (localHeadsUpEntry == null) {
        return;
      }
      localHeadsUpEntry.updateEntry();
      setEntryPinned(localHeadsUpEntry, shouldHeadsUpBecomePinned(paramEntry));
    }
  }
  
  public static class Clock
  {
    public long currentTimeMillis()
    {
      return SystemClock.elapsedRealtime();
    }
  }
  
  public class HeadsUpEntry
    implements Comparable<HeadsUpEntry>
  {
    public long earliestRemovaltime;
    public NotificationData.Entry entry;
    public boolean expanded;
    private Runnable mRemoveHeadsUpRunnable;
    public long postTime;
    public boolean remoteInputActive;
    
    public HeadsUpEntry() {}
    
    private boolean isSticky()
    {
      if (((!this.entry.row.isPinned()) || (!this.expanded)) && (!this.remoteInputActive)) {
        return HeadsUpManager.-wrap0(HeadsUpManager.this, this.entry);
      }
      return true;
    }
    
    public int compareTo(HeadsUpEntry paramHeadsUpEntry)
    {
      boolean bool1 = this.entry.row.isPinned();
      boolean bool2 = paramHeadsUpEntry.entry.row.isPinned();
      if ((!bool1) || (bool2))
      {
        if ((!bool1) && (bool2)) {
          return 1;
        }
      }
      else {
        return -1;
      }
      bool1 = HeadsUpManager.-wrap0(HeadsUpManager.this, this.entry);
      bool2 = HeadsUpManager.-wrap0(HeadsUpManager.this, paramHeadsUpEntry.entry);
      if ((!bool1) || (bool2))
      {
        if ((!bool1) && (bool2)) {
          return 1;
        }
      }
      else {
        return -1;
      }
      if ((!this.remoteInputActive) || (paramHeadsUpEntry.remoteInputActive))
      {
        if ((!this.remoteInputActive) && (paramHeadsUpEntry.remoteInputActive)) {
          return 1;
        }
      }
      else {
        return -1;
      }
      if (this.postTime < paramHeadsUpEntry.postTime) {
        return 1;
      }
      if (this.postTime == paramHeadsUpEntry.postTime) {
        return this.entry.key.compareTo(paramHeadsUpEntry.entry.key);
      }
      return -1;
    }
    
    public void removeAsSoonAsPossible()
    {
      removeAutoRemovalCallbacks();
      HeadsUpManager.-get2(HeadsUpManager.this).postDelayed(this.mRemoveHeadsUpRunnable, this.earliestRemovaltime - HeadsUpManager.-get0(HeadsUpManager.this).currentTimeMillis());
    }
    
    public void removeAutoRemovalCallbacks()
    {
      HeadsUpManager.-get2(HeadsUpManager.this).removeCallbacks(this.mRemoveHeadsUpRunnable);
    }
    
    public void reset()
    {
      removeAutoRemovalCallbacks();
      this.entry = null;
      this.mRemoveHeadsUpRunnable = null;
      this.expanded = false;
      this.remoteInputActive = false;
    }
    
    public void setEntry(final NotificationData.Entry paramEntry)
    {
      this.entry = paramEntry;
      this.postTime = (HeadsUpManager.-get0(HeadsUpManager.this).currentTimeMillis() + HeadsUpManager.-get8(HeadsUpManager.this));
      this.mRemoveHeadsUpRunnable = new Runnable()
      {
        public void run()
        {
          if (!HeadsUpManager.-get9(HeadsUpManager.this))
          {
            HeadsUpManager.-wrap1(HeadsUpManager.this, paramEntry);
            return;
          }
          HeadsUpManager.-get1(HeadsUpManager.this).add(paramEntry);
        }
      };
      updateEntry();
    }
    
    public void updateEntry()
    {
      updateEntry(true);
    }
    
    public void updateEntry(boolean paramBoolean)
    {
      long l = HeadsUpManager.-get0(HeadsUpManager.this).currentTimeMillis();
      this.earliestRemovaltime = (HeadsUpManager.-get4(HeadsUpManager.this) + l);
      if (paramBoolean) {
        this.postTime = Math.max(this.postTime, l);
      }
      removeAutoRemovalCallbacks();
      if (HeadsUpManager.-get1(HeadsUpManager.this).contains(this.entry)) {
        HeadsUpManager.-get1(HeadsUpManager.this).remove(this.entry);
      }
      if (!isSticky())
      {
        l = Math.max(this.postTime + HeadsUpManager.-get3(HeadsUpManager.this) - l, HeadsUpManager.-get4(HeadsUpManager.this));
        HeadsUpManager.-get2(HeadsUpManager.this).postDelayed(this.mRemoveHeadsUpRunnable, l);
      }
    }
    
    public boolean wasShownLongEnough()
    {
      return this.earliestRemovaltime < HeadsUpManager.-get0(HeadsUpManager.this).currentTimeMillis();
    }
  }
  
  public static abstract interface OnHeadsUpChangedListener
  {
    public abstract void onHeadsUpPinned(ExpandableNotificationRow paramExpandableNotificationRow);
    
    public abstract void onHeadsUpPinnedModeChanged(boolean paramBoolean);
    
    public abstract void onHeadsUpStateChanged(NotificationData.Entry paramEntry, boolean paramBoolean);
    
    public abstract void onHeadsUpUnPinned(ExpandableNotificationRow paramExpandableNotificationRow);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\HeadsUpManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */