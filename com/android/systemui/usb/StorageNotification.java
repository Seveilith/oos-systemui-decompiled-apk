package com.android.systemui.usb;

import android.app.Notification;
import android.app.Notification.Action;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.MoveCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.storage.DiskInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseArray;
import com.android.systemui.SystemUI;
import java.util.Iterator;

public class StorageNotification
  extends SystemUI
{
  private final BroadcastReceiver mFinishReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      StorageNotification.-get1(StorageNotification.this).cancelAsUser(null, 1397575510, UserHandle.ALL);
    }
  };
  private final StorageEventListener mListener = new StorageEventListener()
  {
    public void onDiskDestroyed(DiskInfo paramAnonymousDiskInfo)
    {
      StorageNotification.-wrap0(StorageNotification.this, paramAnonymousDiskInfo);
    }
    
    public void onDiskScanned(DiskInfo paramAnonymousDiskInfo, int paramAnonymousInt)
    {
      StorageNotification.-wrap1(StorageNotification.this, paramAnonymousDiskInfo, paramAnonymousInt);
    }
    
    public void onVolumeForgotten(String paramAnonymousString)
    {
      StorageNotification.-get1(StorageNotification.this).cancelAsUser(paramAnonymousString, 1397772886, UserHandle.ALL);
    }
    
    public void onVolumeRecordChanged(VolumeRecord paramAnonymousVolumeRecord)
    {
      paramAnonymousVolumeRecord = StorageNotification.-get2(StorageNotification.this).findVolumeByUuid(paramAnonymousVolumeRecord.getFsUuid());
      if ((paramAnonymousVolumeRecord != null) && (paramAnonymousVolumeRecord.isMountedReadable())) {
        StorageNotification.-wrap4(StorageNotification.this, paramAnonymousVolumeRecord);
      }
    }
    
    public void onVolumeStateChanged(VolumeInfo paramAnonymousVolumeInfo, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      StorageNotification.-wrap4(StorageNotification.this, paramAnonymousVolumeInfo);
    }
  };
  private final PackageManager.MoveCallback mMoveCallback = new PackageManager.MoveCallback()
  {
    public void onCreated(int paramAnonymousInt, Bundle paramAnonymousBundle)
    {
      StorageNotification.MoveInfo localMoveInfo = new StorageNotification.MoveInfo(null);
      localMoveInfo.moveId = paramAnonymousInt;
      localMoveInfo.extras = paramAnonymousBundle;
      if (paramAnonymousBundle != null)
      {
        localMoveInfo.packageName = paramAnonymousBundle.getString("android.intent.extra.PACKAGE_NAME");
        localMoveInfo.label = paramAnonymousBundle.getString("android.intent.extra.TITLE");
        localMoveInfo.volumeUuid = paramAnonymousBundle.getString("android.os.storage.extra.FS_UUID");
      }
      StorageNotification.-get0(StorageNotification.this).put(paramAnonymousInt, localMoveInfo);
    }
    
    public void onStatusChanged(int paramAnonymousInt1, int paramAnonymousInt2, long paramAnonymousLong)
    {
      StorageNotification.MoveInfo localMoveInfo = (StorageNotification.MoveInfo)StorageNotification.-get0(StorageNotification.this).get(paramAnonymousInt1);
      if (localMoveInfo == null)
      {
        Log.w("StorageNotification", "Ignoring unknown move " + paramAnonymousInt1);
        return;
      }
      if (PackageManager.isMoveStatusFinished(paramAnonymousInt2))
      {
        StorageNotification.-wrap2(StorageNotification.this, localMoveInfo, paramAnonymousInt2);
        return;
      }
      StorageNotification.-wrap3(StorageNotification.this, localMoveInfo, paramAnonymousInt2, paramAnonymousLong);
    }
  };
  private final SparseArray<MoveInfo> mMoves = new SparseArray();
  private NotificationManager mNotificationManager;
  private final BroadcastReceiver mSnoozeReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getStringExtra("android.os.storage.extra.FS_UUID");
      StorageNotification.-get2(StorageNotification.this).setVolumeSnoozed(paramAnonymousContext, true);
    }
  };
  private StorageManager mStorageManager;
  
  private PendingIntent buildBrowsePendingIntent(VolumeInfo paramVolumeInfo)
  {
    Intent localIntent = paramVolumeInfo.buildBrowseIntent();
    int i = paramVolumeInfo.getId().hashCode();
    return PendingIntent.getActivityAsUser(this.mContext, i, localIntent, 268435456, null, UserHandle.CURRENT);
  }
  
  private PendingIntent buildForgetPendingIntent(VolumeRecord paramVolumeRecord)
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.settings", "com.android.settings.Settings$PrivateVolumeForgetActivity");
    localIntent.putExtra("android.os.storage.extra.FS_UUID", paramVolumeRecord.getFsUuid());
    int i = paramVolumeRecord.getFsUuid().hashCode();
    return PendingIntent.getActivityAsUser(this.mContext, i, localIntent, 268435456, null, UserHandle.CURRENT);
  }
  
  private PendingIntent buildInitPendingIntent(DiskInfo paramDiskInfo)
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.settings", "com.android.settings.deviceinfo.StorageWizardInit");
    localIntent.putExtra("android.os.storage.extra.DISK_ID", paramDiskInfo.getId());
    int i = paramDiskInfo.getId().hashCode();
    return PendingIntent.getActivityAsUser(this.mContext, i, localIntent, 268435456, null, UserHandle.CURRENT);
  }
  
  private PendingIntent buildInitPendingIntent(VolumeInfo paramVolumeInfo)
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.settings", "com.android.settings.deviceinfo.StorageWizardInit");
    localIntent.putExtra("android.os.storage.extra.VOLUME_ID", paramVolumeInfo.getId());
    int i = paramVolumeInfo.getId().hashCode();
    return PendingIntent.getActivityAsUser(this.mContext, i, localIntent, 268435456, null, UserHandle.CURRENT);
  }
  
  private Notification.Builder buildNotificationBuilder(VolumeInfo paramVolumeInfo, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    paramVolumeInfo = new Notification.Builder(this.mContext).setSmallIcon(getSmallIcon(paramVolumeInfo.getDisk(), paramVolumeInfo.getState())).setColor(this.mContext.getColor(17170523)).setContentTitle(paramCharSequence1).setContentText(paramCharSequence2).setStyle(new Notification.BigTextStyle().bigText(paramCharSequence2)).setVisibility(1).setLocalOnly(true);
    overrideNotificationAppName(this.mContext, paramVolumeInfo);
    return paramVolumeInfo;
  }
  
  private PendingIntent buildSnoozeIntent(String paramString)
  {
    Intent localIntent = new Intent("com.android.systemui.action.SNOOZE_VOLUME");
    localIntent.putExtra("android.os.storage.extra.FS_UUID", paramString);
    int i = paramString.hashCode();
    return PendingIntent.getBroadcastAsUser(this.mContext, i, localIntent, 268435456, UserHandle.CURRENT);
  }
  
  private PendingIntent buildUnmountPendingIntent(VolumeInfo paramVolumeInfo)
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.settings", "com.android.settings.deviceinfo.StorageUnmountReceiver");
    localIntent.putExtra("android.os.storage.extra.VOLUME_ID", paramVolumeInfo.getId());
    int i = paramVolumeInfo.getId().hashCode();
    return PendingIntent.getBroadcastAsUser(this.mContext, i, localIntent, 268435456, UserHandle.CURRENT);
  }
  
  private PendingIntent buildVolumeSettingsPendingIntent(VolumeInfo paramVolumeInfo)
  {
    Intent localIntent = new Intent();
    switch (paramVolumeInfo.getType())
    {
    default: 
      return null;
    case 1: 
      localIntent.setClassName("com.android.settings", "com.android.settings.Settings$PrivateVolumeSettingsActivity");
    }
    for (;;)
    {
      localIntent.putExtra("android.os.storage.extra.VOLUME_ID", paramVolumeInfo.getId());
      int i = paramVolumeInfo.getId().hashCode();
      return PendingIntent.getActivityAsUser(this.mContext, i, localIntent, 268435456, null, UserHandle.CURRENT);
      localIntent.setClassName("com.android.settings", "com.android.settings.Settings$PublicVolumeSettingsActivity");
    }
  }
  
  private PendingIntent buildWizardMigratePendingIntent(MoveInfo paramMoveInfo)
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.settings", "com.android.settings.deviceinfo.StorageWizardMigrateProgress");
    localIntent.putExtra("android.content.pm.extra.MOVE_ID", paramMoveInfo.moveId);
    VolumeInfo localVolumeInfo = this.mStorageManager.findVolumeByQualifiedUuid(paramMoveInfo.volumeUuid);
    if (localVolumeInfo != null) {
      localIntent.putExtra("android.os.storage.extra.VOLUME_ID", localVolumeInfo.getId());
    }
    return PendingIntent.getActivityAsUser(this.mContext, paramMoveInfo.moveId, localIntent, 268435456, null, UserHandle.CURRENT);
  }
  
  private PendingIntent buildWizardMovePendingIntent(MoveInfo paramMoveInfo)
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.settings", "com.android.settings.deviceinfo.StorageWizardMoveProgress");
    localIntent.putExtra("android.content.pm.extra.MOVE_ID", paramMoveInfo.moveId);
    return PendingIntent.getActivityAsUser(this.mContext, paramMoveInfo.moveId, localIntent, 268435456, null, UserHandle.CURRENT);
  }
  
  private PendingIntent buildWizardReadyPendingIntent(DiskInfo paramDiskInfo)
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.settings", "com.android.settings.deviceinfo.StorageWizardReady");
    localIntent.putExtra("android.os.storage.extra.DISK_ID", paramDiskInfo.getId());
    int i = paramDiskInfo.getId().hashCode();
    return PendingIntent.getActivityAsUser(this.mContext, i, localIntent, 268435456, null, UserHandle.CURRENT);
  }
  
  private int getSmallIcon(DiskInfo paramDiskInfo, int paramInt)
  {
    if (paramDiskInfo.isSd())
    {
      switch (paramInt)
      {
      default: 
        return 17302586;
      }
      return 17302586;
    }
    if (paramDiskInfo.isUsb()) {
      return 17302608;
    }
    return 17302586;
  }
  
  private void onDiskDestroyedInternal(DiskInfo paramDiskInfo)
  {
    this.mNotificationManager.cancelAsUser(paramDiskInfo.getId(), 1396986699, UserHandle.ALL);
  }
  
  private void onDiskScannedInternal(DiskInfo paramDiskInfo, int paramInt)
  {
    if ((paramInt == 0) && (paramDiskInfo.size > 0L))
    {
      Object localObject = this.mContext.getString(17040455, new Object[] { paramDiskInfo.getDescription() });
      String str = this.mContext.getString(17040456, new Object[] { paramDiskInfo.getDescription() });
      localObject = new Notification.Builder(this.mContext).setSmallIcon(getSmallIcon(paramDiskInfo, 6)).setColor(this.mContext.getColor(17170523)).setContentTitle((CharSequence)localObject).setContentText(str).setContentIntent(buildInitPendingIntent(paramDiskInfo)).setStyle(new Notification.BigTextStyle().bigText(str)).setVisibility(1).setLocalOnly(true).setCategory("err");
      SystemUI.overrideNotificationAppName(this.mContext, (Notification.Builder)localObject);
      this.mNotificationManager.notifyAsUser(paramDiskInfo.getId(), 1396986699, ((Notification.Builder)localObject).build(), UserHandle.ALL);
      return;
    }
    this.mNotificationManager.cancelAsUser(paramDiskInfo.getId(), 1396986699, UserHandle.ALL);
  }
  
  private void onMoveFinished(MoveInfo paramMoveInfo, int paramInt)
  {
    if (paramMoveInfo.packageName != null)
    {
      this.mNotificationManager.cancelAsUser(paramMoveInfo.packageName, 1397575510, UserHandle.ALL);
      return;
    }
    Object localObject = this.mContext.getPackageManager().getPrimaryStorageCurrentVolume();
    String str1 = this.mStorageManager.getBestVolumeDescription((VolumeInfo)localObject);
    String str2;
    if (paramInt == -100)
    {
      str2 = this.mContext.getString(17040470);
      str1 = this.mContext.getString(17040471, new Object[] { str1 });
      if ((localObject == null) || (((VolumeInfo)localObject).getDisk() == null)) {
        break label243;
      }
      localObject = buildWizardReadyPendingIntent(((VolumeInfo)localObject).getDisk());
    }
    for (;;)
    {
      localObject = new Notification.Builder(this.mContext).setSmallIcon(17302586).setColor(this.mContext.getColor(17170523)).setContentTitle(str2).setContentText(str1).setContentIntent((PendingIntent)localObject).setStyle(new Notification.BigTextStyle().bigText(str1)).setVisibility(1).setLocalOnly(true).setCategory("sys").setPriority(-1).setAutoCancel(true);
      SystemUI.overrideNotificationAppName(this.mContext, (Notification.Builder)localObject);
      this.mNotificationManager.notifyAsUser(paramMoveInfo.packageName, 1397575510, ((Notification.Builder)localObject).build(), UserHandle.ALL);
      return;
      str2 = this.mContext.getString(17040472);
      str1 = this.mContext.getString(17040473);
      break;
      label243:
      if (localObject != null) {
        localObject = buildVolumeSettingsPendingIntent((VolumeInfo)localObject);
      } else {
        localObject = null;
      }
    }
  }
  
  private void onMoveProgress(MoveInfo paramMoveInfo, int paramInt, long paramLong)
  {
    Object localObject;
    CharSequence localCharSequence;
    if (!TextUtils.isEmpty(paramMoveInfo.label))
    {
      localObject = this.mContext.getString(17040468, new Object[] { paramMoveInfo.label });
      if (paramLong >= 0L) {
        break label193;
      }
      localCharSequence = null;
      label42:
      if (paramMoveInfo.packageName == null) {
        break label202;
      }
    }
    label193:
    label202:
    for (PendingIntent localPendingIntent = buildWizardMovePendingIntent(paramMoveInfo);; localPendingIntent = buildWizardMigratePendingIntent(paramMoveInfo))
    {
      localObject = new Notification.Builder(this.mContext).setSmallIcon(17302586).setColor(this.mContext.getColor(17170523)).setContentTitle((CharSequence)localObject).setContentText(localCharSequence).setContentIntent(localPendingIntent).setStyle(new Notification.BigTextStyle().bigText(localCharSequence)).setVisibility(1).setLocalOnly(true).setCategory("progress").setPriority(-1).setProgress(100, paramInt, false).setOngoing(true);
      SystemUI.overrideNotificationAppName(this.mContext, (Notification.Builder)localObject);
      this.mNotificationManager.notifyAsUser(paramMoveInfo.packageName, 1397575510, ((Notification.Builder)localObject).build(), UserHandle.ALL);
      return;
      localObject = this.mContext.getString(17040469);
      break;
      localCharSequence = DateUtils.formatDuration(paramLong);
      break label42;
    }
  }
  
  private void onPrivateVolumeStateChangedInternal(VolumeInfo paramVolumeInfo)
  {
    Log.d("StorageNotification", "Notifying about private volume: " + paramVolumeInfo.toString());
    updateMissingPrivateVolumes();
  }
  
  private void onPublicVolumeStateChangedInternal(VolumeInfo paramVolumeInfo)
  {
    Log.d("StorageNotification", "Notifying about public volume: " + paramVolumeInfo.toString());
    Notification localNotification;
    switch (paramVolumeInfo.getState())
    {
    default: 
      localNotification = null;
    }
    while (localNotification != null)
    {
      this.mNotificationManager.notifyAsUser(paramVolumeInfo.getId(), 1397773634, localNotification, UserHandle.ALL);
      return;
      localNotification = onVolumeUnmounted(paramVolumeInfo);
      continue;
      localNotification = onVolumeChecking(paramVolumeInfo);
      continue;
      localNotification = onVolumeMounted(paramVolumeInfo);
      continue;
      localNotification = onVolumeFormatting(paramVolumeInfo);
      continue;
      localNotification = onVolumeEjecting(paramVolumeInfo);
      continue;
      localNotification = onVolumeUnmountable(paramVolumeInfo);
      continue;
      localNotification = onVolumeRemoved(paramVolumeInfo);
      continue;
      localNotification = onVolumeBadRemoval(paramVolumeInfo);
    }
    this.mNotificationManager.cancelAsUser(paramVolumeInfo.getId(), 1397773634, UserHandle.ALL);
  }
  
  private Notification onVolumeBadRemoval(VolumeInfo paramVolumeInfo)
  {
    if (!paramVolumeInfo.isPrimary()) {
      return null;
    }
    DiskInfo localDiskInfo = paramVolumeInfo.getDisk();
    return buildNotificationBuilder(paramVolumeInfo, this.mContext.getString(17040457, new Object[] { localDiskInfo.getDescription() }), this.mContext.getString(17040458, new Object[] { localDiskInfo.getDescription() })).setCategory("err").build();
  }
  
  private Notification onVolumeChecking(VolumeInfo paramVolumeInfo)
  {
    DiskInfo localDiskInfo = paramVolumeInfo.getDisk();
    return buildNotificationBuilder(paramVolumeInfo, this.mContext.getString(17040449, new Object[] { localDiskInfo.getDescription() }), this.mContext.getString(17040450, new Object[] { localDiskInfo.getDescription() })).setCategory("progress").setPriority(-1).setOngoing(true).build();
  }
  
  private Notification onVolumeEjecting(VolumeInfo paramVolumeInfo)
  {
    DiskInfo localDiskInfo = paramVolumeInfo.getDisk();
    return buildNotificationBuilder(paramVolumeInfo, this.mContext.getString(17040461, new Object[] { localDiskInfo.getDescription() }), this.mContext.getString(17040462, new Object[] { localDiskInfo.getDescription() })).setCategory("progress").setPriority(-1).setOngoing(true).build();
  }
  
  private Notification onVolumeFormatting(VolumeInfo paramVolumeInfo)
  {
    return null;
  }
  
  private Notification onVolumeMounted(VolumeInfo paramVolumeInfo)
  {
    Object localObject2 = this.mStorageManager.findRecordByUuid(paramVolumeInfo.getFsUuid());
    Object localObject1 = paramVolumeInfo.getDisk();
    if ((((VolumeRecord)localObject2).isSnoozed()) && (((DiskInfo)localObject1).isAdoptable())) {
      return null;
    }
    if ((!((DiskInfo)localObject1).isAdoptable()) || (((VolumeRecord)localObject2).isInited()))
    {
      localObject2 = ((DiskInfo)localObject1).getDescription();
      localObject3 = this.mContext.getString(17040452, new Object[] { ((DiskInfo)localObject1).getDescription() });
      PendingIntent localPendingIntent = buildBrowsePendingIntent(paramVolumeInfo);
      localObject2 = buildNotificationBuilder(paramVolumeInfo, (CharSequence)localObject2, (CharSequence)localObject3).addAction(new Notification.Action(17302361, this.mContext.getString(17040465), localPendingIntent)).addAction(new Notification.Action(17302346, this.mContext.getString(17040464), buildUnmountPendingIntent(paramVolumeInfo))).setContentIntent(localPendingIntent).setCategory("sys").setPriority(-1);
      if (((DiskInfo)localObject1).isAdoptable()) {
        ((Notification.Builder)localObject2).setDeleteIntent(buildSnoozeIntent(paramVolumeInfo.getFsUuid()));
      }
      return ((Notification.Builder)localObject2).build();
    }
    localObject2 = ((DiskInfo)localObject1).getDescription();
    localObject1 = this.mContext.getString(17040451, new Object[] { ((DiskInfo)localObject1).getDescription() });
    Object localObject3 = buildInitPendingIntent(paramVolumeInfo);
    return buildNotificationBuilder(paramVolumeInfo, (CharSequence)localObject2, (CharSequence)localObject1).addAction(new Notification.Action(17302592, this.mContext.getString(17040463), (PendingIntent)localObject3)).addAction(new Notification.Action(17302346, this.mContext.getString(17040464), buildUnmountPendingIntent(paramVolumeInfo))).setContentIntent((PendingIntent)localObject3).setDeleteIntent(buildSnoozeIntent(paramVolumeInfo.getFsUuid())).setCategory("sys").build();
  }
  
  private Notification onVolumeRemoved(VolumeInfo paramVolumeInfo)
  {
    if (!paramVolumeInfo.isPrimary()) {
      return null;
    }
    DiskInfo localDiskInfo = paramVolumeInfo.getDisk();
    return buildNotificationBuilder(paramVolumeInfo, this.mContext.getString(17040459, new Object[] { localDiskInfo.getDescription() }), this.mContext.getString(17040460, new Object[] { localDiskInfo.getDescription() })).setCategory("err").build();
  }
  
  private void onVolumeStateChangedInternal(VolumeInfo paramVolumeInfo)
  {
    switch (paramVolumeInfo.getType())
    {
    default: 
      return;
    case 1: 
      onPrivateVolumeStateChangedInternal(paramVolumeInfo);
      return;
    }
    onPublicVolumeStateChangedInternal(paramVolumeInfo);
  }
  
  private Notification onVolumeUnmountable(VolumeInfo paramVolumeInfo)
  {
    DiskInfo localDiskInfo = paramVolumeInfo.getDisk();
    return buildNotificationBuilder(paramVolumeInfo, this.mContext.getString(17040453, new Object[] { localDiskInfo.getDescription() }), this.mContext.getString(17040454, new Object[] { localDiskInfo.getDescription() })).setContentIntent(buildInitPendingIntent(paramVolumeInfo)).setCategory("err").build();
  }
  
  private Notification onVolumeUnmounted(VolumeInfo paramVolumeInfo)
  {
    return null;
  }
  
  private void updateMissingPrivateVolumes()
  {
    Iterator localIterator = this.mStorageManager.getVolumeRecords().iterator();
    while (localIterator.hasNext())
    {
      Object localObject1 = (VolumeRecord)localIterator.next();
      if (((VolumeRecord)localObject1).getType() == 1)
      {
        String str1 = ((VolumeRecord)localObject1).getFsUuid();
        Object localObject2 = this.mStorageManager.findVolumeByUuid(str1);
        if (((localObject2 != null) && (((VolumeInfo)localObject2).isMountedWritable())) || (((VolumeRecord)localObject1).isSnoozed()))
        {
          this.mNotificationManager.cancelAsUser(str1, 1397772886, UserHandle.ALL);
        }
        else
        {
          localObject2 = this.mContext.getString(17040466, new Object[] { ((VolumeRecord)localObject1).getNickname() });
          String str2 = this.mContext.getString(17040467);
          localObject1 = new Notification.Builder(this.mContext).setSmallIcon(17302586).setColor(this.mContext.getColor(17170523)).setContentTitle((CharSequence)localObject2).setContentText(str2).setContentIntent(buildForgetPendingIntent((VolumeRecord)localObject1)).setStyle(new Notification.BigTextStyle().bigText(str2)).setVisibility(1).setLocalOnly(true).setCategory("sys").setDeleteIntent(buildSnoozeIntent(str1));
          SystemUI.overrideNotificationAppName(this.mContext, (Notification.Builder)localObject1);
          this.mNotificationManager.notifyAsUser(str1, 1397772886, ((Notification.Builder)localObject1).build(), UserHandle.ALL);
        }
      }
    }
  }
  
  public void start()
  {
    this.mNotificationManager = ((NotificationManager)this.mContext.getSystemService(NotificationManager.class));
    this.mStorageManager = ((StorageManager)this.mContext.getSystemService(StorageManager.class));
    this.mStorageManager.registerListener(this.mListener);
    this.mContext.registerReceiver(this.mSnoozeReceiver, new IntentFilter("com.android.systemui.action.SNOOZE_VOLUME"), "android.permission.MOUNT_UNMOUNT_FILESYSTEMS", null);
    this.mContext.registerReceiver(this.mFinishReceiver, new IntentFilter("com.android.systemui.action.FINISH_WIZARD"), "android.permission.MOUNT_UNMOUNT_FILESYSTEMS", null);
    Iterator localIterator = this.mStorageManager.getDisks().iterator();
    while (localIterator.hasNext())
    {
      DiskInfo localDiskInfo = (DiskInfo)localIterator.next();
      onDiskScannedInternal(localDiskInfo, localDiskInfo.volumeCount);
    }
    localIterator = this.mStorageManager.getVolumes().iterator();
    while (localIterator.hasNext()) {
      onVolumeStateChangedInternal((VolumeInfo)localIterator.next());
    }
    this.mContext.getPackageManager().registerMoveCallback(this.mMoveCallback, new Handler());
    updateMissingPrivateVolumes();
  }
  
  private static class MoveInfo
  {
    public Bundle extras;
    public String label;
    public int moveId;
    public String packageName;
    public String volumeUuid;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\usb\StorageNotification.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */