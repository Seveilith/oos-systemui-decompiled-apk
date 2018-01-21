package com.android.systemui.recents;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings.System;
import android.util.Log;
import com.android.systemui.recents.misc.SystemServicesProxy;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class LockStateController
{
  private static LockStateController sInstance = null;
  private final String TAG = "LockStateController";
  private final String TASK_LOCK_LIST_KEY = "task_lock_list";
  private final String TASK_LOCK_LIST_KEY_WITH_USERID = "task_lock_list_with_userid";
  private final String TASK_LOCK_STATE = "tasklockstate";
  private Context mContext;
  private Handler mHandler;
  private Set<String> mLockedList;
  private Set<String> mLockedListWithUserId;
  private List<String> mLockedPackageNameList;
  private List<String> mLockedPackageNameListWithUserId;
  private SharedPreferences mSp;
  
  private LockStateController(Context paramContext)
  {
    this.mContext = paramContext;
    this.mHandler = new Handler();
    this.mSp = paramContext.getSharedPreferences("tasklockstate", 0);
    this.mLockedList = this.mSp.getStringSet("task_lock_list", new HashSet());
    this.mLockedListWithUserId = this.mSp.getStringSet("task_lock_list_with_userid", new HashSet());
    initPackageNameList();
  }
  
  private String appendUserWithBrace(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      return null;
    }
    return paramString1.replace("}", "") + "#" + paramString2 + "}";
  }
  
  private String appendUserWithoutBrace(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      return null;
    }
    return paramString1.replace("{", "") + "#" + paramString2;
  }
  
  public static LockStateController getInstance(Context paramContext)
  {
    if (sInstance == null) {
      sInstance = new LockStateController(paramContext);
    }
    return sInstance;
  }
  
  private void initPackageNameList()
  {
    this.mLockedPackageNameList = new ArrayList();
    this.mLockedPackageNameListWithUserId = new ArrayList();
    Object localObject1 = this.mLockedList.iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = ((String)((Iterator)localObject1).next()).split("/");
      this.mLockedPackageNameList.add(localObject2[0].replace("{", ""));
    }
    Log.d("LockStateController", "init userid tasklock list: " + this.mLockedListWithUserId.isEmpty());
    Object localObject3;
    if (this.mLockedListWithUserId.isEmpty())
    {
      localObject1 = String.valueOf(Recents.getSystemServices().getCurrentUser());
      localObject2 = this.mLockedList.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (String)((Iterator)localObject2).next();
        this.mLockedListWithUserId.add(appendUserWithBrace((String)localObject3, (String)localObject1));
        localObject3 = ((String)localObject3).split("/");
        this.mLockedPackageNameListWithUserId.add(appendUserWithoutBrace(localObject3[0], (String)localObject1));
      }
    }
    localObject1 = this.mLockedListWithUserId.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject3 = (String)((Iterator)localObject1).next();
      localObject2 = ((String)localObject3).split("/");
      localObject3 = ((String)localObject3).substring(((String)localObject3).lastIndexOf("#") + 1);
      localObject3 = ((String)localObject3).substring(0, ((String)localObject3).length() - 1);
      this.mLockedPackageNameListWithUserId.add(appendUserWithoutBrace(localObject2[0], (String)localObject3));
    }
  }
  
  private String removeUserWithBrace(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return paramString.substring(0, paramString.lastIndexOf("#")) + "}";
  }
  
  private void writeToProvider()
  {
    int i = 0;
    if (this.mLockedPackageNameListWithUserId != null) {
      i = this.mLockedPackageNameListWithUserId.size();
    }
    Object localObject = new StringBuilder();
    int j = 0;
    while (j < i)
    {
      ((StringBuilder)localObject).append((String)this.mLockedPackageNameListWithUserId.get(j)).append(",");
      j += 1;
    }
    String str = ((StringBuilder)localObject).toString();
    localObject = str;
    if (i > 0) {
      localObject = str.substring(0, str.length() - 1).replace("#", "");
    }
    Recents.getSystemServices().writeLockedListToProvider((String)localObject);
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    Object localObject = (String[])this.mLockedList.toArray(new String[this.mLockedList.size()]);
    paramPrintWriter.println();
    paramPrintWriter.println("LOCKED RECENT APP list: " + localObject.length);
    int i = 0;
    while (i < localObject.length)
    {
      paramPrintWriter.print("  ");
      paramPrintWriter.println(localObject[i]);
      i += 1;
    }
    localObject = (String[])this.mLockedListWithUserId.toArray(new String[this.mLockedListWithUserId.size()]);
    paramPrintWriter.println();
    paramPrintWriter.println("with userId: " + localObject.length);
    i = 0;
    while (i < localObject.length)
    {
      paramPrintWriter.print("  ");
      paramPrintWriter.println(localObject[i]);
      i += 1;
    }
    paramPrintWriter.println();
    paramPrintWriter.println("pkg name list: " + this.mLockedPackageNameList.size());
    i = 0;
    while (i < this.mLockedPackageNameList.size())
    {
      paramPrintWriter.print("  ");
      paramPrintWriter.println((String)this.mLockedPackageNameList.get(i));
      i += 1;
    }
    paramPrintWriter.println();
    paramPrintWriter.println("with userId: " + this.mLockedPackageNameListWithUserId.size());
    i = 0;
    while (i < this.mLockedPackageNameListWithUserId.size())
    {
      paramPrintWriter.print("  ");
      paramPrintWriter.println((String)this.mLockedPackageNameListWithUserId.get(i));
      i += 1;
    }
    paramPrintWriter.println();
    localObject = Recents.getSystemServices();
    paramPrintWriter.println("RECENT_TASK_LOCKED_LIST: " + Settings.System.getStringForUser(this.mContext.getContentResolver(), "com_oneplus_systemui_recent_task_lockd_list", ((SystemServicesProxy)localObject).getCurrentUser()));
    paramPrintWriter.println();
  }
  
  public boolean getLockState(String paramString, int paramInt)
  {
    boolean bool = false;
    if (this.mLockedListWithUserId != null) {
      bool = this.mLockedListWithUserId.contains(appendUserWithBrace(paramString, String.valueOf(paramInt)));
    }
    return bool;
  }
  
  public List<String> getLockedPackageList()
  {
    if (this.mLockedList == null) {
      return null;
    }
    return new ArrayList(this.mLockedList);
  }
  
  public boolean isLocked(String paramString)
  {
    return this.mLockedPackageNameListWithUserId.contains(paramString);
  }
  
  public void removeLockState(final String paramString, final int paramInt)
  {
    if (paramInt == -1) {
      return;
    }
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        int j = UserHandle.getUserId(paramInt);
        String str1 = "{" + paramString + "/";
        Log.d("LockStateController", "uninstall Lock task , " + paramString + ", " + j);
        ArrayList localArrayList = new ArrayList(LockStateController.-get0(LockStateController.this));
        int k = localArrayList.size();
        int i = 0;
        while (i < k)
        {
          String str2 = (String)localArrayList.get(i);
          if ((str2.startsWith(str1)) && (str2.endsWith(String.valueOf(j) + "}"))) {
            LockStateController.this.setLockState(LockStateController.-wrap0(LockStateController.this, str2), false, j);
          }
          i += 1;
        }
      }
    });
  }
  
  public void setLockState(String paramString, boolean paramBoolean, int paramInt)
  {
    String[] arrayOfString = paramString.split("/");
    String str1 = String.valueOf(paramInt);
    String str2 = appendUserWithBrace(paramString, str1);
    if (arrayOfString[0] == null) {
      return;
    }
    if (paramBoolean)
    {
      this.mLockedList.add(paramString);
      this.mLockedPackageNameList.add(arrayOfString[0].replace("{", ""));
      this.mLockedListWithUserId.add(str2);
      this.mLockedPackageNameListWithUserId.add(appendUserWithoutBrace(arrayOfString[0], str1));
    }
    for (;;)
    {
      paramString = this.mSp.edit();
      paramString.clear();
      paramString.putStringSet("task_lock_list", this.mLockedList);
      paramString.putStringSet("task_lock_list_with_userid", this.mLockedListWithUserId);
      paramString.apply();
      writeToProvider();
      return;
      this.mLockedList.remove(paramString);
      this.mLockedPackageNameList.remove(arrayOfString[0].replace("{", ""));
      this.mLockedListWithUserId.remove(str2);
      this.mLockedPackageNameListWithUserId.remove(appendUserWithoutBrace(arrayOfString[0], str1));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\LockStateController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */