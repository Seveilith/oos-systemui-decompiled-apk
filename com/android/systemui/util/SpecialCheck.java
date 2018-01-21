package com.android.systemui.util;

import android.content.ComponentName;
import java.util.HashSet;
import java.util.Set;

public class SpecialCheck
{
  private static String TAG = "SpecialCheck";
  private static Set<ComponentName> sTaskNotExcludeSet = new HashSet();
  
  static
  {
    sTaskNotExcludeSet.add(new ComponentName("com.kingsoft", "com.kingsoft.Main"));
    sTaskNotExcludeSet.add(new ComponentName("com.tencent.mobileqq", ".activity.SplashActivity"));
    sTaskNotExcludeSet.add(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
  }
  
  public static boolean shouldNotExcludeTask(ComponentName paramComponentName)
  {
    return sTaskNotExcludeSet.contains(paramComponentName);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\util\SpecialCheck.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */