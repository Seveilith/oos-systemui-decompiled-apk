package com.android.systemui.statusbar.phone;

import android.app.Notification;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import com.android.internal.util.NotificationColorUtil;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.NotificationData;
import com.android.systemui.statusbar.NotificationData.Entry;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.notification.NotificationUtils;
import java.util.ArrayList;

public class NotificationIconAreaController
{
  private int mIconHPadding;
  private int mIconSize;
  private int mIconTint = -1;
  private ImageView mMoreIcon;
  private final NotificationColorUtil mNotificationColorUtil;
  protected View mNotificationIconArea;
  private IconMerger mNotificationIcons;
  private PhoneStatusBar mPhoneStatusBar;
  private final Rect mTintArea = new Rect();
  
  public NotificationIconAreaController(Context paramContext, PhoneStatusBar paramPhoneStatusBar)
  {
    this.mPhoneStatusBar = paramPhoneStatusBar;
    this.mNotificationColorUtil = NotificationColorUtil.getInstance(paramContext);
    initializeNotificationAreaViews(paramContext);
  }
  
  private void applyNotificationIconsTint()
  {
    int i = 0;
    while (i < this.mNotificationIcons.getChildCount())
    {
      StatusBarIconView localStatusBarIconView = (StatusBarIconView)this.mNotificationIcons.getChildAt(i);
      Boolean.TRUE.equals(localStatusBarIconView.getTag(2131951677));
      if (NotificationUtils.isGrayscale(localStatusBarIconView, this.mNotificationColorUtil)) {
        localStatusBarIconView.setImageTintList(ColorStateList.valueOf(StatusBarIconController.getTint(this.mTintArea, localStatusBarIconView, this.mIconTint)));
      }
      i += 1;
    }
  }
  
  @NonNull
  private LinearLayout.LayoutParams generateIconLayoutParams()
  {
    return new LinearLayout.LayoutParams(this.mIconSize + this.mIconHPadding * 2, getHeight());
  }
  
  private void reloadDimens(Context paramContext)
  {
    paramContext = paramContext.getResources();
    this.mIconSize = paramContext.getDimensionPixelSize(17104928);
    this.mIconHPadding = paramContext.getDimensionPixelSize(2131755380);
  }
  
  protected int getHeight()
  {
    return this.mPhoneStatusBar.getStatusBarHeight();
  }
  
  public View getNotificationInnerAreaView()
  {
    return this.mNotificationIconArea;
  }
  
  protected View inflateIconArea(LayoutInflater paramLayoutInflater)
  {
    return paramLayoutInflater.inflate(2130968734, null);
  }
  
  protected void initializeNotificationAreaViews(Context paramContext)
  {
    reloadDimens(paramContext);
    this.mNotificationIconArea = inflateIconArea(LayoutInflater.from(paramContext));
    this.mNotificationIcons = ((IconMerger)this.mNotificationIconArea.findViewById(2131952114));
    this.mMoreIcon = ((ImageView)this.mNotificationIconArea.findViewById(2131952113));
    if (this.mMoreIcon != null)
    {
      this.mMoreIcon.setImageTintList(ColorStateList.valueOf(this.mIconTint));
      this.mNotificationIcons.setOverflowIndicator(this.mMoreIcon);
    }
  }
  
  public void onDensityOrFontScaleChanged(Context paramContext)
  {
    reloadDimens(paramContext);
    paramContext = generateIconLayoutParams();
    int i = 0;
    while (i < this.mNotificationIcons.getChildCount())
    {
      this.mNotificationIcons.getChildAt(i).setLayoutParams(paramContext);
      i += 1;
    }
  }
  
  public void setIconTint(int paramInt)
  {
    this.mIconTint = paramInt;
    if (this.mMoreIcon != null) {
      this.mMoreIcon.setImageTintList(ColorStateList.valueOf(this.mIconTint));
    }
    applyNotificationIconsTint();
  }
  
  public void setNotificationIconAreaVisiable(boolean paramBoolean)
  {
    View localView = this.mNotificationIconArea;
    if (paramBoolean) {}
    for (int i = 0;; i = 4)
    {
      localView.setVisibility(i);
      return;
    }
  }
  
  public void setTintArea(Rect paramRect)
  {
    if (paramRect == null) {
      this.mTintArea.setEmpty();
    }
    for (;;)
    {
      applyNotificationIconsTint();
      return;
      this.mTintArea.set(paramRect);
    }
  }
  
  protected boolean shouldShowNotification(NotificationData.Entry paramEntry, NotificationData paramNotificationData)
  {
    Bundle localBundle = null;
    if (paramEntry.notification.getNotification() != null) {
      localBundle = paramEntry.notification.getNotification().extras;
    }
    if ((localBundle != null) && (localBundle.getBoolean("hide_icon", false))) {
      return false;
    }
    if ((!paramNotificationData.isAmbient(paramEntry.key)) || (NotificationData.showNotificationEvenIfUnprovisioned(paramEntry.notification)))
    {
      if (!PhoneStatusBar.isTopLevelChild(paramEntry)) {
        return false;
      }
    }
    else {
      return false;
    }
    return paramEntry.row.getVisibility() != 8;
  }
  
  public void updateNotificationIcons(NotificationData paramNotificationData)
  {
    Object localObject1 = generateIconLayoutParams();
    Object localObject2 = paramNotificationData.getActiveNotifications();
    int j = ((ArrayList)localObject2).size();
    ArrayList localArrayList = new ArrayList(j);
    int i = 0;
    while (i < j)
    {
      NotificationData.Entry localEntry = (NotificationData.Entry)((ArrayList)localObject2).get(i);
      if (shouldShowNotification(localEntry, paramNotificationData)) {
        localArrayList.add(localEntry.icon);
      }
      i += 1;
    }
    paramNotificationData = new ArrayList();
    i = 0;
    while (i < this.mNotificationIcons.getChildCount())
    {
      localObject2 = this.mNotificationIcons.getChildAt(i);
      if (!localArrayList.contains(localObject2)) {
        paramNotificationData.add(localObject2);
      }
      i += 1;
    }
    j = paramNotificationData.size();
    i = 0;
    while (i < j)
    {
      this.mNotificationIcons.removeView((View)paramNotificationData.get(i));
      i += 1;
    }
    i = 0;
    while (i < localArrayList.size())
    {
      paramNotificationData = (View)localArrayList.get(i);
      if (paramNotificationData.getParent() == null) {
        this.mNotificationIcons.addView(paramNotificationData, i, (ViewGroup.LayoutParams)localObject1);
      }
      i += 1;
    }
    j = this.mNotificationIcons.getChildCount();
    i = 0;
    if (i < j)
    {
      paramNotificationData = this.mNotificationIcons.getChildAt(i);
      localObject1 = (StatusBarIconView)localArrayList.get(i);
      if (paramNotificationData == localObject1) {}
      for (;;)
      {
        i += 1;
        break;
        this.mNotificationIcons.removeView((View)localObject1);
        this.mNotificationIcons.addView((View)localObject1, i);
      }
    }
    applyNotificationIconsTint();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\NotificationIconAreaController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */