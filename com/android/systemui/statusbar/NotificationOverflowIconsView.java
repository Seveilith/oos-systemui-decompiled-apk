package com.android.systemui.statusbar;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff.Mode;
import android.service.notification.StatusBarNotification;
import android.util.AttributeSet;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.android.internal.util.NotificationColorUtil;
import com.android.systemui.statusbar.phone.IconMerger;

public class NotificationOverflowIconsView
  extends IconMerger
{
  private int mIconSize;
  private TextView mMoreText;
  private NotificationColorUtil mNotificationColorUtil;
  private int mTintColor;
  
  public NotificationOverflowIconsView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void applyColor(Notification paramNotification, StatusBarIconView paramStatusBarIconView)
  {
    paramStatusBarIconView.setColorFilter(this.mTintColor, PorterDuff.Mode.MULTIPLY);
  }
  
  private void updateMoreText()
  {
    this.mMoreText.setText(getResources().getString(2131690350, new Object[] { Integer.valueOf(getChildCount()) }));
  }
  
  public void addNotification(NotificationData.Entry paramEntry)
  {
    StatusBarIconView localStatusBarIconView = new StatusBarIconView(getContext(), "", paramEntry.notification.getNotification());
    localStatusBarIconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    addView(localStatusBarIconView, this.mIconSize, this.mIconSize);
    localStatusBarIconView.set(paramEntry.icon.getStatusBarIcon());
    applyColor(paramEntry.notification.getNotification(), localStatusBarIconView);
    updateMoreText();
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mNotificationColorUtil = NotificationColorUtil.getInstance(getContext());
    this.mTintColor = getContext().getColor(2131493010);
    this.mIconSize = getResources().getDimensionPixelSize(17104928);
  }
  
  public void setMoreText(TextView paramTextView)
  {
    this.mMoreText = paramTextView;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\NotificationOverflowIconsView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */