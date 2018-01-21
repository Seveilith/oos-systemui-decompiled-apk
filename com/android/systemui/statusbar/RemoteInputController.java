package com.android.systemui.statusbar;

import android.util.ArrayMap;
import android.util.Pair;
import com.android.internal.util.Preconditions;
import com.android.systemui.statusbar.phone.StatusBarWindowManager;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class RemoteInputController
{
  private final ArrayList<Callback> mCallbacks = new ArrayList(3);
  private final HeadsUpManager mHeadsUpManager;
  private final ArrayList<Pair<WeakReference<NotificationData.Entry>, Object>> mOpen = new ArrayList();
  private final ArrayMap<String, Object> mSpinning = new ArrayMap();
  
  public RemoteInputController(StatusBarWindowManager paramStatusBarWindowManager, HeadsUpManager paramHeadsUpManager)
  {
    addCallback(paramStatusBarWindowManager);
    this.mHeadsUpManager = paramHeadsUpManager;
  }
  
  private void apply(NotificationData.Entry paramEntry)
  {
    this.mHeadsUpManager.setRemoteInputActive(paramEntry, isRemoteInputActive(paramEntry));
    boolean bool = isRemoteInputActive();
    int j = this.mCallbacks.size();
    int i = 0;
    while (i < j)
    {
      ((Callback)this.mCallbacks.get(i)).onRemoteInputActive(bool);
      i += 1;
    }
  }
  
  private boolean pruneWeakThenRemoveAndContains(NotificationData.Entry paramEntry1, NotificationData.Entry paramEntry2, Object paramObject)
  {
    boolean bool1 = false;
    int i = this.mOpen.size() - 1;
    if (i >= 0)
    {
      NotificationData.Entry localEntry = (NotificationData.Entry)((WeakReference)((Pair)this.mOpen.get(i)).first).get();
      Object localObject = ((Pair)this.mOpen.get(i)).second;
      int j;
      label75:
      boolean bool2;
      if ((paramObject == null) || (localObject == paramObject))
      {
        j = 1;
        if ((localEntry != null) && ((localEntry != paramEntry2) || (j == 0))) {
          break label124;
        }
        this.mOpen.remove(i);
        bool2 = bool1;
      }
      for (;;)
      {
        i -= 1;
        bool1 = bool2;
        break;
        j = 0;
        break label75;
        label124:
        bool2 = bool1;
        if (localEntry == paramEntry1) {
          if ((paramObject != null) && (paramObject != localObject))
          {
            this.mOpen.remove(i);
            bool2 = bool1;
          }
          else
          {
            bool2 = true;
          }
        }
      }
    }
    return bool1;
  }
  
  public void addCallback(Callback paramCallback)
  {
    Preconditions.checkNotNull(paramCallback);
    this.mCallbacks.add(paramCallback);
  }
  
  public void addRemoteInput(NotificationData.Entry paramEntry, Object paramObject)
  {
    Preconditions.checkNotNull(paramEntry);
    Preconditions.checkNotNull(paramObject);
    if (!pruneWeakThenRemoveAndContains(paramEntry, null, paramObject)) {
      this.mOpen.add(new Pair(new WeakReference(paramEntry), paramObject));
    }
    apply(paramEntry);
  }
  
  public void addSpinning(String paramString, Object paramObject)
  {
    Preconditions.checkNotNull(paramString);
    Preconditions.checkNotNull(paramObject);
    this.mSpinning.put(paramString, paramObject);
  }
  
  public void closeRemoteInputs()
  {
    if (this.mOpen.size() == 0) {
      return;
    }
    ArrayList localArrayList = new ArrayList(this.mOpen.size());
    int i = this.mOpen.size() - 1;
    NotificationData.Entry localEntry;
    while (i >= 0)
    {
      localEntry = (NotificationData.Entry)((WeakReference)((Pair)this.mOpen.get(i)).first).get();
      if ((localEntry != null) && (localEntry.row != null)) {
        localArrayList.add(localEntry);
      }
      i -= 1;
    }
    i = localArrayList.size() - 1;
    while (i >= 0)
    {
      localEntry = (NotificationData.Entry)localArrayList.get(i);
      if (localEntry.row != null) {
        localEntry.row.closeRemoteInput();
      }
      i -= 1;
    }
  }
  
  public boolean isRemoteInputActive()
  {
    pruneWeakThenRemoveAndContains(null, null, null);
    return !this.mOpen.isEmpty();
  }
  
  public boolean isRemoteInputActive(NotificationData.Entry paramEntry)
  {
    return pruneWeakThenRemoveAndContains(paramEntry, null, null);
  }
  
  public boolean isSpinning(String paramString)
  {
    return this.mSpinning.containsKey(paramString);
  }
  
  public void remoteInputSent(NotificationData.Entry paramEntry)
  {
    int j = this.mCallbacks.size();
    int i = 0;
    while (i < j)
    {
      ((Callback)this.mCallbacks.get(i)).onRemoteInputSent(paramEntry);
      i += 1;
    }
  }
  
  public void removeRemoteInput(NotificationData.Entry paramEntry, Object paramObject)
  {
    Preconditions.checkNotNull(paramEntry);
    pruneWeakThenRemoveAndContains(null, paramEntry, paramObject);
    apply(paramEntry);
  }
  
  public void removeSpinning(String paramString, Object paramObject)
  {
    Preconditions.checkNotNull(paramString);
    if ((paramObject == null) || (this.mSpinning.get(paramString) == paramObject)) {
      this.mSpinning.remove(paramString);
    }
  }
  
  public static abstract interface Callback
  {
    public void onRemoteInputActive(boolean paramBoolean) {}
    
    public void onRemoteInputSent(NotificationData.Entry paramEntry) {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\RemoteInputController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */