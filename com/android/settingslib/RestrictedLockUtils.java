package com.android.settingslib;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import java.util.Iterator;

public class RestrictedLockUtils
{
  public static EnforcedAdmin checkIfRestrictionEnforced(Context paramContext, String paramString, int paramInt)
  {
    if ((DevicePolicyManager)paramContext.getSystemService("device_policy") == null) {
      return null;
    }
    int j = UserManager.get(paramContext).getUserRestrictionSource(paramString, UserHandle.of(paramInt));
    if ((j == 0) || (j == 1)) {
      return null;
    }
    int i;
    if ((j & 0x4) != 0)
    {
      i = 1;
      if ((j & 0x2) == 0) {
        break label75;
      }
    }
    label75:
    for (j = 1;; j = 0)
    {
      if (i == 0) {
        break label81;
      }
      return getProfileOwner(paramContext, paramInt);
      i = 0;
      break;
    }
    label81:
    if (j != 0)
    {
      paramContext = getDeviceOwner(paramContext);
      if (paramContext.userId == paramInt) {
        return paramContext;
      }
      return EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
    }
    return null;
  }
  
  public static EnforcedAdmin getDeviceOwner(Context paramContext)
  {
    paramContext = (DevicePolicyManager)paramContext.getSystemService("device_policy");
    if (paramContext == null) {
      return null;
    }
    ComponentName localComponentName = paramContext.getDeviceOwnerComponentOnAnyUser();
    if (localComponentName != null) {
      return new EnforcedAdmin(localComponentName, paramContext.getDeviceOwnerUserId());
    }
    return null;
  }
  
  private static EnforcedAdmin getProfileOwner(Context paramContext, int paramInt)
  {
    if (paramInt == 55536) {
      return null;
    }
    paramContext = (DevicePolicyManager)paramContext.getSystemService("device_policy");
    if (paramContext == null) {
      return null;
    }
    paramContext = paramContext.getProfileOwnerAsUser(paramInt);
    if (paramContext != null) {
      return new EnforcedAdmin(paramContext, paramInt);
    }
    return null;
  }
  
  public static Intent getShowAdminSupportDetailsIntent(Context paramContext, EnforcedAdmin paramEnforcedAdmin)
  {
    paramContext = new Intent("android.settings.SHOW_ADMIN_SUPPORT_DETAILS");
    if (paramEnforcedAdmin != null)
    {
      if (paramEnforcedAdmin.component != null) {
        paramContext.putExtra("android.app.extra.DEVICE_ADMIN", paramEnforcedAdmin.component);
      }
      int i = UserHandle.myUserId();
      if (paramEnforcedAdmin.userId != 55536) {
        i = paramEnforcedAdmin.userId;
      }
      paramContext.putExtra("android.intent.extra.USER_ID", i);
    }
    return paramContext;
  }
  
  public static boolean hasBaseUserRestriction(Context paramContext, String paramString, int paramInt)
  {
    return ((UserManager)paramContext.getSystemService("user")).hasBaseUserRestriction(paramString, UserHandle.of(paramInt));
  }
  
  public static boolean isCurrentUserOrProfile(Context paramContext, int paramInt)
  {
    paramContext = UserManager.get(paramContext).getProfiles(UserHandle.myUserId()).iterator();
    while (paramContext.hasNext()) {
      if (((UserInfo)paramContext.next()).id == paramInt) {
        return true;
      }
    }
    return false;
  }
  
  public static void sendShowAdminSupportDetailsIntent(Context paramContext, EnforcedAdmin paramEnforcedAdmin)
  {
    Intent localIntent = getShowAdminSupportDetailsIntent(paramContext, paramEnforcedAdmin);
    int j = UserHandle.myUserId();
    int i = j;
    if (paramEnforcedAdmin != null)
    {
      i = j;
      if (paramEnforcedAdmin.userId != 55536)
      {
        i = j;
        if (isCurrentUserOrProfile(paramContext, paramEnforcedAdmin.userId)) {
          i = paramEnforcedAdmin.userId;
        }
      }
    }
    paramContext.startActivityAsUser(localIntent, new UserHandle(i));
  }
  
  public static class EnforcedAdmin
  {
    public static final EnforcedAdmin MULTIPLE_ENFORCED_ADMIN = new EnforcedAdmin();
    public ComponentName component = null;
    public int userId = 55536;
    
    public EnforcedAdmin() {}
    
    public EnforcedAdmin(ComponentName paramComponentName, int paramInt)
    {
      this.component = paramComponentName;
      this.userId = paramInt;
    }
    
    public EnforcedAdmin(EnforcedAdmin paramEnforcedAdmin)
    {
      if (paramEnforcedAdmin == null) {
        throw new IllegalArgumentException();
      }
      this.component = paramEnforcedAdmin.component;
      this.userId = paramEnforcedAdmin.userId;
    }
    
    public void copyTo(EnforcedAdmin paramEnforcedAdmin)
    {
      if (paramEnforcedAdmin == null) {
        throw new IllegalArgumentException();
      }
      paramEnforcedAdmin.component = this.component;
      paramEnforcedAdmin.userId = this.userId;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if (!(paramObject instanceof EnforcedAdmin)) {
        return false;
      }
      paramObject = (EnforcedAdmin)paramObject;
      if (this.userId != ((EnforcedAdmin)paramObject).userId) {
        return false;
      }
      if ((this.component == null) && (((EnforcedAdmin)paramObject).component == null)) {}
      while ((this.component != null) && (this.component.equals(((EnforcedAdmin)paramObject).component))) {
        return true;
      }
      return false;
    }
    
    public String toString()
    {
      return "EnforcedAdmin{component=" + this.component + ",userId=" + this.userId + "}";
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\settingslib\RestrictedLockUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */