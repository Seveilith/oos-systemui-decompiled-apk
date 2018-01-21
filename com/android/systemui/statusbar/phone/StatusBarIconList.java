package com.android.systemui.statusbar.phone;

import com.android.internal.statusbar.StatusBarIcon;
import java.io.PrintWriter;
import java.util.ArrayList;

public class StatusBarIconList
{
  private static String TAG = "StatusBarIconList";
  private ArrayList<StatusBarIcon> mIcons = new ArrayList();
  private ArrayList<String> mSlots = new ArrayList();
  
  public StatusBarIconList(String[] paramArrayOfString)
  {
    int j = paramArrayOfString.length;
    int i = 0;
    while (i < j)
    {
      this.mSlots.add(paramArrayOfString[i]);
      this.mIcons.add(null);
      i += 1;
    }
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    int j = this.mSlots.size();
    paramPrintWriter.println("  icon slots: " + j);
    int i = 0;
    while (i < j)
    {
      paramPrintWriter.printf("    %2d: (%s) %s\n", new Object[] { Integer.valueOf(i), this.mSlots.get(i), this.mIcons.get(i) });
      i += 1;
    }
  }
  
  public StatusBarIcon getIcon(int paramInt)
  {
    return (StatusBarIcon)this.mIcons.get(paramInt);
  }
  
  public String getSlot(int paramInt)
  {
    return (String)this.mSlots.get(paramInt);
  }
  
  public int getSlotIndex(String paramString)
  {
    int j = this.mSlots.size();
    int i = 0;
    while (i < j)
    {
      if (paramString.equals(this.mSlots.get(i))) {
        return i;
      }
      i += 1;
    }
    this.mSlots.add(paramString);
    this.mIcons.add(null);
    return j;
  }
  
  public int getViewIndex(int paramInt)
  {
    int j = 0;
    int i = 0;
    while (i < paramInt)
    {
      int k = j;
      if (this.mIcons.get(i) != null) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  public void removeIcon(int paramInt)
  {
    this.mIcons.set(paramInt, null);
  }
  
  public void setIcon(int paramInt, StatusBarIcon paramStatusBarIcon)
  {
    this.mIcons.set(paramInt, paramStatusBarIcon);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\StatusBarIconList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */