package com.android.systemui;

import android.app.Notification.Builder;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Map;

public abstract class SystemUI
{
  public Map<Class<?>, Object> mComponents;
  public Context mContext;
  
  public static void overrideNotificationAppName(Context paramContext, Notification.Builder paramBuilder)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("android.substName", paramContext.getString(17039702));
    paramBuilder.addExtras(localBundle);
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {}
  
  public <T> T getComponent(Class<T> paramClass)
  {
    Object localObject = null;
    if (this.mComponents != null) {
      localObject = this.mComponents.get(paramClass);
    }
    return (T)localObject;
  }
  
  protected void onBootCompleted() {}
  
  protected void onConfigurationChanged(Configuration paramConfiguration) {}
  
  public <T, C extends T> void putComponent(Class<T> paramClass, C paramC)
  {
    if (this.mComponents != null) {
      this.mComponents.put(paramClass, paramC);
    }
  }
  
  public abstract void start();
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\SystemUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */