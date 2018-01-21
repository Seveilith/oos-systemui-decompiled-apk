package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.DemoMode;
import com.android.systemui.statusbar.StatusBarIconView;

public class DemoStatusIcons
  extends LinearLayout
  implements DemoMode
{
  private boolean mDemoMode;
  private final int mIconSize;
  private final LinearLayout mStatusIcons;
  
  public DemoStatusIcons(LinearLayout paramLinearLayout, int paramInt)
  {
    super(paramLinearLayout.getContext());
    this.mStatusIcons = paramLinearLayout;
    this.mIconSize = paramInt;
    setLayoutParams(this.mStatusIcons.getLayoutParams());
    setOrientation(this.mStatusIcons.getOrientation());
    setGravity(16);
    paramLinearLayout = (ViewGroup)this.mStatusIcons.getParent();
    paramLinearLayout.addView(this, paramLinearLayout.indexOfChild(this.mStatusIcons));
  }
  
  private void updateSlot(String paramString1, String paramString2, int paramInt)
  {
    if (!this.mDemoMode) {
      return;
    }
    Object localObject = paramString2;
    if (paramString2 == null) {
      localObject = this.mContext.getPackageName();
    }
    int k = -1;
    int i = 0;
    for (;;)
    {
      int j = k;
      if (i < getChildCount())
      {
        paramString2 = (StatusBarIconView)getChildAt(i);
        if (!paramString1.equals(paramString2.getTag())) {
          break label120;
        }
        if (paramInt == 0) {
          j = i;
        }
      }
      else
      {
        if (paramInt != 0) {
          break;
        }
        if (j != -1) {
          removeViewAt(j);
        }
        return;
      }
      paramString1 = paramString2.getStatusBarIcon();
      paramString1.icon = Icon.createWithResource(paramString1.icon.getResPackage(), paramInt);
      paramString2.set(paramString1);
      paramString2.updateDrawable();
      return;
      label120:
      i += 1;
    }
    paramString2 = new StatusBarIcon((String)localObject, UserHandle.SYSTEM, paramInt, 0, 0, "Demo");
    localObject = new StatusBarIconView(getContext(), null, null);
    ((StatusBarIconView)localObject).setTag(paramString1);
    ((StatusBarIconView)localObject).set(paramString2);
    addView((View)localObject, 0, new LinearLayout.LayoutParams(this.mIconSize, this.mIconSize));
  }
  
  public void dispatchDemoCommand(String paramString, Bundle paramBundle)
  {
    if ((!this.mDemoMode) && (paramString.equals("enter")))
    {
      this.mDemoMode = true;
      this.mStatusIcons.setVisibility(8);
      setVisibility(0);
    }
    label142:
    label173:
    label204:
    label235:
    label266:
    label297:
    label328:
    label359:
    do
    {
      do
      {
        return;
        if ((this.mDemoMode) && (paramString.equals("exit")))
        {
          this.mDemoMode = false;
          this.mStatusIcons.setVisibility(0);
          setVisibility(8);
          return;
        }
      } while ((!this.mDemoMode) || (!paramString.equals("status")));
      paramString = paramBundle.getString("volume");
      if (paramString != null)
      {
        if (!paramString.equals("vibrate")) {
          break;
        }
        i = 2130838577;
        updateSlot("volume", null, i);
      }
      paramString = paramBundle.getString("zen");
      if (paramString != null)
      {
        if (!paramString.equals("important")) {
          break label404;
        }
        i = 2130839018;
        updateSlot("zen", null, i);
      }
      paramString = paramBundle.getString("bluetooth");
      if (paramString != null)
      {
        if (!paramString.equals("disconnected")) {
          break label424;
        }
        i = 2130838406;
        updateSlot("bluetooth", null, i);
      }
      paramString = paramBundle.getString("location");
      if (paramString != null)
      {
        if (!paramString.equals("show")) {
          break label444;
        }
        i = 2130838466;
        updateSlot("location", null, i);
      }
      paramString = paramBundle.getString("alarm");
      if (paramString != null)
      {
        if (!paramString.equals("show")) {
          break label449;
        }
        i = 2130838390;
        updateSlot("alarm_clock", null, i);
      }
      paramString = paramBundle.getString("tty");
      if (paramString != null)
      {
        if (!paramString.equals("show")) {
          break label454;
        }
        i = 2130839000;
        updateSlot("tty", null, i);
      }
      paramString = paramBundle.getString("mute");
      if (paramString != null)
      {
        if (!paramString.equals("show")) {
          break label459;
        }
        i = 17301622;
        updateSlot("mute", null, i);
      }
      paramString = paramBundle.getString("speakerphone");
      if (paramString != null)
      {
        if (!paramString.equals("show")) {
          break label464;
        }
        i = 17301639;
        updateSlot("speakerphone", null, i);
      }
      paramString = paramBundle.getString("cast");
      if (paramString != null)
      {
        if (!paramString.equals("show")) {
          break label469;
        }
        i = 2130838405;
        updateSlot("cast", null, i);
      }
      paramString = paramBundle.getString("hotspot");
    } while (paramString == null);
    if (paramString.equals("show")) {}
    for (int i = 2130838465;; i = 0)
    {
      updateSlot("hotspot", null, i);
      return;
      i = 0;
      break;
      label404:
      if (paramString.equals("none"))
      {
        i = 2130839019;
        break label142;
      }
      i = 0;
      break label142;
      label424:
      if (paramString.equals("connected"))
      {
        i = 2130838407;
        break label173;
      }
      i = 0;
      break label173;
      label444:
      i = 0;
      break label204;
      label449:
      i = 0;
      break label235;
      label454:
      i = 0;
      break label266;
      label459:
      i = 0;
      break label297;
      label464:
      i = 0;
      break label328;
      label469:
      i = 0;
      break label359;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\DemoStatusIcons.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */