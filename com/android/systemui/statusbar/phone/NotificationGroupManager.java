package com.android.systemui.statusbar.phone;

import android.app.Notification;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.NotificationData.Entry;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.HeadsUpManager.OnHeadsUpChangedListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class NotificationGroupManager
  implements HeadsUpManager.OnHeadsUpChangedListener
{
  private int mBarState = -1;
  private final HashMap<String, NotificationGroup> mGroupMap = new HashMap();
  private HeadsUpManager mHeadsUpManager;
  private boolean mIsUpdatingUnchangedGroup;
  private HashMap<String, StatusBarNotification> mIsolatedEntries = new HashMap();
  private OnGroupChangeListener mListener;
  
  private String getGroupKey(StatusBarNotification paramStatusBarNotification)
  {
    if (isIsolated(paramStatusBarNotification)) {
      return paramStatusBarNotification.getKey();
    }
    return paramStatusBarNotification.getGroupKey();
  }
  
  @Nullable
  private ExpandableNotificationRow getGroupSummary(String paramString)
  {
    paramString = (NotificationGroup)this.mGroupMap.get(paramString);
    if (paramString == null) {}
    while (paramString.summary == null) {
      return null;
    }
    return paramString.summary.row;
  }
  
  private NotificationData.Entry getIsolatedChild(String paramString)
  {
    Iterator localIterator = this.mIsolatedEntries.values().iterator();
    while (localIterator.hasNext())
    {
      StatusBarNotification localStatusBarNotification = (StatusBarNotification)localIterator.next();
      if ((localStatusBarNotification.getGroupKey().equals(paramString)) && (isIsolated(localStatusBarNotification))) {
        return ((NotificationGroup)this.mGroupMap.get(localStatusBarNotification.getKey())).summary;
      }
    }
    return null;
  }
  
  private int getNumberOfIsolatedChildren(String paramString)
  {
    int i = 0;
    Iterator localIterator = this.mIsolatedEntries.values().iterator();
    while (localIterator.hasNext())
    {
      StatusBarNotification localStatusBarNotification = (StatusBarNotification)localIterator.next();
      if ((localStatusBarNotification.getGroupKey().equals(paramString)) && (isIsolated(localStatusBarNotification))) {
        i += 1;
      }
    }
    return i;
  }
  
  private int getTotalNumberOfChildren(StatusBarNotification paramStatusBarNotification)
  {
    int j = getNumberOfIsolatedChildren(paramStatusBarNotification.getGroupKey());
    paramStatusBarNotification = (NotificationGroup)this.mGroupMap.get(paramStatusBarNotification.getGroupKey());
    if (paramStatusBarNotification != null) {}
    for (int i = paramStatusBarNotification.children.size();; i = 0) {
      return j + i;
    }
  }
  
  private void handleSuppressedSummaryHeadsUpped(NotificationData.Entry paramEntry)
  {
    NotificationData.Entry localEntry = null;
    StatusBarNotification localStatusBarNotification = paramEntry.notification;
    Object localObject;
    if ((isGroupSuppressed(localStatusBarNotification.getGroupKey())) && (localStatusBarNotification.getNotification().isGroupSummary()) && (paramEntry.row.isHeadsUp()))
    {
      localObject = (NotificationGroup)this.mGroupMap.get(localStatusBarNotification.getGroupKey());
      if (localObject != null)
      {
        localObject = ((NotificationGroup)localObject).children.iterator();
        if (((Iterator)localObject).hasNext()) {
          localEntry = (NotificationData.Entry)((Iterator)localObject).next();
        }
        localObject = localEntry;
        if (localEntry == null) {
          localObject = getIsolatedChild(localStatusBarNotification.getGroupKey());
        }
        if (localObject != null)
        {
          if (!this.mHeadsUpManager.isHeadsUp(((NotificationData.Entry)localObject).key)) {
            break label144;
          }
          this.mHeadsUpManager.updateNotification((NotificationData.Entry)localObject, true);
        }
      }
    }
    for (;;)
    {
      this.mHeadsUpManager.releaseImmediately(paramEntry.key);
      return;
      return;
      label144:
      this.mHeadsUpManager.showNotification((NotificationData.Entry)localObject);
    }
  }
  
  private boolean hasIsolatedChildren(NotificationGroup paramNotificationGroup)
  {
    boolean bool = false;
    if (getNumberOfIsolatedChildren(paramNotificationGroup.summary.notification.getGroupKey()) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean isGroupChild(StatusBarNotification paramStatusBarNotification)
  {
    if (isIsolated(paramStatusBarNotification)) {
      return false;
    }
    return (paramStatusBarNotification.isGroup()) && (!paramStatusBarNotification.getNotification().isGroupSummary());
  }
  
  private boolean isGroupNotFullyVisible(NotificationGroup paramNotificationGroup)
  {
    if ((paramNotificationGroup.summary == null) || (paramNotificationGroup.summary.row.getClipTopAmount() > 0)) {}
    while (paramNotificationGroup.summary.row.getTranslationY() < 0.0F) {
      return true;
    }
    return false;
  }
  
  private boolean isGroupSummary(StatusBarNotification paramStatusBarNotification)
  {
    if (isIsolated(paramStatusBarNotification)) {
      return true;
    }
    return paramStatusBarNotification.getNotification().isGroupSummary();
  }
  
  private boolean isGroupSuppressed(String paramString)
  {
    paramString = (NotificationGroup)this.mGroupMap.get(paramString);
    if (paramString != null) {
      return paramString.suppressed;
    }
    return false;
  }
  
  private boolean isIsolated(StatusBarNotification paramStatusBarNotification)
  {
    return this.mIsolatedEntries.containsKey(paramStatusBarNotification.getKey());
  }
  
  private boolean isOnlyChild(StatusBarNotification paramStatusBarNotification)
  {
    if (!paramStatusBarNotification.getNotification().isGroupSummary()) {
      return getTotalNumberOfChildren(paramStatusBarNotification) == 1;
    }
    return false;
  }
  
  private void onEntryBecomingChild(NotificationData.Entry paramEntry)
  {
    if (paramEntry.row.isHeadsUp()) {
      onHeadsUpStateChanged(paramEntry, true);
    }
  }
  
  private void onEntryRemovedInternal(NotificationData.Entry paramEntry, StatusBarNotification paramStatusBarNotification)
  {
    String str = getGroupKey(paramStatusBarNotification);
    NotificationGroup localNotificationGroup = (NotificationGroup)this.mGroupMap.get(str);
    if (localNotificationGroup == null) {
      return;
    }
    if (isGroupChild(paramStatusBarNotification)) {
      localNotificationGroup.children.remove(paramEntry);
    }
    for (;;)
    {
      updateSuppression(localNotificationGroup);
      if ((localNotificationGroup.children.isEmpty()) && (localNotificationGroup.summary == null)) {
        this.mGroupMap.remove(str);
      }
      return;
      localNotificationGroup.summary = null;
    }
  }
  
  private void setGroupExpanded(NotificationGroup paramNotificationGroup, boolean paramBoolean)
  {
    paramNotificationGroup.expanded = paramBoolean;
    if (paramNotificationGroup.summary != null) {
      this.mListener.onGroupExpansionChanged(paramNotificationGroup.summary.row, paramBoolean);
    }
  }
  
  private boolean shouldIsolate(StatusBarNotification paramStatusBarNotification)
  {
    NotificationGroup localNotificationGroup = (NotificationGroup)this.mGroupMap.get(paramStatusBarNotification.getGroupKey());
    if ((!paramStatusBarNotification.isGroup()) || (paramStatusBarNotification.getNotification().isGroupSummary())) {
      return false;
    }
    if ((paramStatusBarNotification.getNotification().fullScreenIntent != null) || (localNotificationGroup == null)) {}
    while (!localNotificationGroup.expanded) {
      return true;
    }
    return isGroupNotFullyVisible(localNotificationGroup);
  }
  
  private void updateSuppression(NotificationGroup paramNotificationGroup)
  {
    boolean bool1 = true;
    if (paramNotificationGroup == null) {
      return;
    }
    boolean bool2 = paramNotificationGroup.suppressed;
    if ((paramNotificationGroup.summary == null) || (paramNotificationGroup.expanded)) {
      bool1 = false;
    }
    for (;;)
    {
      paramNotificationGroup.suppressed = bool1;
      if (bool2 != paramNotificationGroup.suppressed)
      {
        if (paramNotificationGroup.suppressed) {
          handleSuppressedSummaryHeadsUpped(paramNotificationGroup.summary);
        }
        if (!this.mIsUpdatingUnchangedGroup) {
          this.mListener.onGroupsChanged();
        }
      }
      return;
      if (paramNotificationGroup.children.size() != 1) {
        if ((paramNotificationGroup.children.size() == 0) && (paramNotificationGroup.summary.notification.getNotification().isGroupSummary())) {
          bool1 = hasIsolatedChildren(paramNotificationGroup);
        } else {
          bool1 = false;
        }
      }
    }
  }
  
  public void collapseAllGroups()
  {
    ArrayList localArrayList = new ArrayList(this.mGroupMap.values());
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      NotificationGroup localNotificationGroup = (NotificationGroup)localArrayList.get(i);
      if (localNotificationGroup.expanded) {
        setGroupExpanded(localNotificationGroup, false);
      }
      updateSuppression(localNotificationGroup);
      i += 1;
    }
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("GroupManager state:");
    paramPrintWriter.println("  number of groups: " + this.mGroupMap.size());
    paramFileDescriptor = this.mGroupMap.entrySet().iterator();
    while (paramFileDescriptor.hasNext())
    {
      paramArrayOfString = (Map.Entry)paramFileDescriptor.next();
      paramPrintWriter.println("\n    key: " + (String)paramArrayOfString.getKey());
      paramPrintWriter.println(paramArrayOfString.getValue());
    }
    paramPrintWriter.println("\n    isolated entries: " + this.mIsolatedEntries.size());
    paramFileDescriptor = this.mIsolatedEntries.entrySet().iterator();
    while (paramFileDescriptor.hasNext())
    {
      paramArrayOfString = (Map.Entry)paramFileDescriptor.next();
      paramPrintWriter.print("      ");
      paramPrintWriter.print((String)paramArrayOfString.getKey());
      paramPrintWriter.print(", ");
      paramPrintWriter.println(paramArrayOfString.getValue());
    }
  }
  
  public int getCurrentNumberOfChildren(StatusBarNotification paramStatusBarNotification)
  {
    paramStatusBarNotification = (NotificationGroup)this.mGroupMap.get(paramStatusBarNotification.getGroupKey());
    if (paramStatusBarNotification != null) {
      return paramStatusBarNotification.children.size();
    }
    return 0;
  }
  
  public ExpandableNotificationRow getGroupSummary(StatusBarNotification paramStatusBarNotification)
  {
    return getGroupSummary(getGroupKey(paramStatusBarNotification));
  }
  
  public ExpandableNotificationRow getLogicalGroupSummary(StatusBarNotification paramStatusBarNotification)
  {
    return getGroupSummary(paramStatusBarNotification.getGroupKey());
  }
  
  public boolean isChildInGroupWithSummary(StatusBarNotification paramStatusBarNotification)
  {
    if (!isGroupChild(paramStatusBarNotification)) {
      return false;
    }
    paramStatusBarNotification = (NotificationGroup)this.mGroupMap.get(getGroupKey(paramStatusBarNotification));
    if ((paramStatusBarNotification == null) || (paramStatusBarNotification.summary == null)) {}
    while (paramStatusBarNotification.suppressed) {
      return false;
    }
    return !paramStatusBarNotification.children.isEmpty();
  }
  
  public boolean isGroupExpanded(StatusBarNotification paramStatusBarNotification)
  {
    paramStatusBarNotification = (NotificationGroup)this.mGroupMap.get(getGroupKey(paramStatusBarNotification));
    if (paramStatusBarNotification == null) {
      return false;
    }
    return paramStatusBarNotification.expanded;
  }
  
  public boolean isOnlyChildInGroup(StatusBarNotification paramStatusBarNotification)
  {
    if (!isOnlyChild(paramStatusBarNotification)) {
      return false;
    }
    ExpandableNotificationRow localExpandableNotificationRow = getLogicalGroupSummary(paramStatusBarNotification);
    return (localExpandableNotificationRow != null) && (!localExpandableNotificationRow.getStatusBarNotification().equals(paramStatusBarNotification));
  }
  
  public boolean isSummaryOfGroup(StatusBarNotification paramStatusBarNotification)
  {
    if (!isGroupSummary(paramStatusBarNotification)) {
      return false;
    }
    paramStatusBarNotification = (NotificationGroup)this.mGroupMap.get(getGroupKey(paramStatusBarNotification));
    if (paramStatusBarNotification == null) {
      return false;
    }
    return !paramStatusBarNotification.children.isEmpty();
  }
  
  public boolean isSummaryOfSuppressedGroup(StatusBarNotification paramStatusBarNotification)
  {
    if (isGroupSuppressed(getGroupKey(paramStatusBarNotification))) {
      return paramStatusBarNotification.getNotification().isGroupSummary();
    }
    return false;
  }
  
  public void onEntryAdded(NotificationData.Entry paramEntry)
  {
    Object localObject = paramEntry.notification;
    boolean bool = isGroupChild((StatusBarNotification)localObject);
    String str = getGroupKey((StatusBarNotification)localObject);
    NotificationGroup localNotificationGroup = (NotificationGroup)this.mGroupMap.get(str);
    localObject = localNotificationGroup;
    if (localNotificationGroup == null)
    {
      localObject = new NotificationGroup();
      this.mGroupMap.put(str, localObject);
    }
    if (bool)
    {
      ((NotificationGroup)localObject).children.add(paramEntry);
      updateSuppression((NotificationGroup)localObject);
    }
    do
    {
      return;
      ((NotificationGroup)localObject).summary = paramEntry;
      ((NotificationGroup)localObject).expanded = paramEntry.row.areChildrenExpanded();
      updateSuppression((NotificationGroup)localObject);
    } while (((NotificationGroup)localObject).children.isEmpty());
    paramEntry = ((HashSet)((NotificationGroup)localObject).children.clone()).iterator();
    while (paramEntry.hasNext()) {
      onEntryBecomingChild((NotificationData.Entry)paramEntry.next());
    }
    this.mListener.onGroupCreatedFromChildren((NotificationGroup)localObject);
  }
  
  public void onEntryRemoved(NotificationData.Entry paramEntry)
  {
    onEntryRemovedInternal(paramEntry, paramEntry.notification);
    this.mIsolatedEntries.remove(paramEntry.key);
  }
  
  public void onEntryUpdated(NotificationData.Entry paramEntry, StatusBarNotification paramStatusBarNotification)
  {
    String str1 = paramStatusBarNotification.getGroupKey();
    String str2 = paramEntry.notification.getGroupKey();
    int i;
    boolean bool2;
    boolean bool3;
    boolean bool1;
    if (str1.equals(str2))
    {
      i = 0;
      bool2 = isGroupChild(paramStatusBarNotification);
      bool3 = isGroupChild(paramEntry.notification);
      if ((i != 0) || (bool2 != bool3)) {
        break label164;
      }
      bool1 = true;
      label58:
      this.mIsUpdatingUnchangedGroup = bool1;
      if (this.mGroupMap.get(getGroupKey(paramStatusBarNotification)) != null) {
        onEntryRemovedInternal(paramEntry, paramStatusBarNotification);
      }
      onEntryAdded(paramEntry);
      this.mIsUpdatingUnchangedGroup = false;
      if (!isIsolated(paramEntry.notification)) {
        break label170;
      }
      this.mIsolatedEntries.put(paramEntry.key, paramEntry.notification);
      if (i != 0)
      {
        updateSuppression((NotificationGroup)this.mGroupMap.get(str1));
        updateSuppression((NotificationGroup)this.mGroupMap.get(str2));
      }
    }
    label164:
    label170:
    while ((bool2) || (!bool3))
    {
      return;
      i = 1;
      break;
      bool1 = false;
      break label58;
    }
    onEntryBecomingChild(paramEntry);
  }
  
  public void onHeadsUpPinned(ExpandableNotificationRow paramExpandableNotificationRow) {}
  
  public void onHeadsUpPinnedModeChanged(boolean paramBoolean) {}
  
  public void onHeadsUpStateChanged(NotificationData.Entry paramEntry, boolean paramBoolean)
  {
    StatusBarNotification localStatusBarNotification = paramEntry.notification;
    if (paramEntry.row.isHeadsUp()) {
      if (shouldIsolate(localStatusBarNotification))
      {
        onEntryRemovedInternal(paramEntry, paramEntry.notification);
        this.mIsolatedEntries.put(localStatusBarNotification.getKey(), localStatusBarNotification);
        onEntryAdded(paramEntry);
        updateSuppression((NotificationGroup)this.mGroupMap.get(paramEntry.notification.getGroupKey()));
        this.mListener.onGroupsChanged();
      }
    }
    while (!this.mIsolatedEntries.containsKey(localStatusBarNotification.getKey()))
    {
      return;
      handleSuppressedSummaryHeadsUpped(paramEntry);
      return;
    }
    onEntryRemovedInternal(paramEntry, paramEntry.notification);
    this.mIsolatedEntries.remove(localStatusBarNotification.getKey());
    onEntryAdded(paramEntry);
    this.mListener.onGroupsChanged();
  }
  
  public void onHeadsUpUnPinned(ExpandableNotificationRow paramExpandableNotificationRow) {}
  
  public void setGroupExpanded(StatusBarNotification paramStatusBarNotification, boolean paramBoolean)
  {
    paramStatusBarNotification = (NotificationGroup)this.mGroupMap.get(getGroupKey(paramStatusBarNotification));
    if (paramStatusBarNotification == null) {
      return;
    }
    setGroupExpanded(paramStatusBarNotification, paramBoolean);
  }
  
  public void setHeadsUpManager(HeadsUpManager paramHeadsUpManager)
  {
    this.mHeadsUpManager = paramHeadsUpManager;
  }
  
  public void setOnGroupChangeListener(OnGroupChangeListener paramOnGroupChangeListener)
  {
    this.mListener = paramOnGroupChangeListener;
  }
  
  public void setStatusBarState(int paramInt)
  {
    if (this.mBarState == paramInt) {
      return;
    }
    this.mBarState = paramInt;
    if (this.mBarState == 1) {
      collapseAllGroups();
    }
  }
  
  public boolean toggleGroupExpansion(StatusBarNotification paramStatusBarNotification)
  {
    boolean bool = false;
    paramStatusBarNotification = (NotificationGroup)this.mGroupMap.get(getGroupKey(paramStatusBarNotification));
    if (paramStatusBarNotification == null) {
      return false;
    }
    if (paramStatusBarNotification.expanded) {}
    for (;;)
    {
      setGroupExpanded(paramStatusBarNotification, bool);
      return paramStatusBarNotification.expanded;
      bool = true;
    }
  }
  
  public static class NotificationGroup
  {
    public final HashSet<NotificationData.Entry> children = new HashSet();
    public boolean expanded;
    public NotificationData.Entry summary;
    public boolean suppressed;
    
    public String toString()
    {
      Object localObject2 = new StringBuilder().append("    summary:\n      ");
      if (this.summary != null) {}
      for (Object localObject1 = this.summary.notification;; localObject1 = "null")
      {
        localObject1 = localObject1;
        localObject1 = (String)localObject1 + "\n    children size: " + this.children.size();
        localObject2 = this.children.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          NotificationData.Entry localEntry = (NotificationData.Entry)((Iterator)localObject2).next();
          localObject1 = (String)localObject1 + "\n      " + localEntry.notification;
        }
      }
      return (String)localObject1;
    }
  }
  
  public static abstract interface OnGroupChangeListener
  {
    public abstract void onGroupCreatedFromChildren(NotificationGroupManager.NotificationGroup paramNotificationGroup);
    
    public abstract void onGroupExpansionChanged(ExpandableNotificationRow paramExpandableNotificationRow, boolean paramBoolean);
    
    public abstract void onGroupsChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\NotificationGroupManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */