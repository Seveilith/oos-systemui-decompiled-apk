package com.android.systemui.statusbar.notification;

import android.app.Notification;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.view.View;
import android.widget.ImageView;
import com.android.systemui.statusbar.ExpandableNotificationRow;

public class NotificationBigPictureTemplateViewWrapper
  extends NotificationTemplateViewWrapper
{
  protected NotificationBigPictureTemplateViewWrapper(Context paramContext, View paramView, ExpandableNotificationRow paramExpandableNotificationRow)
  {
    super(paramContext, paramView, paramExpandableNotificationRow);
  }
  
  private void updateImageTag(StatusBarNotification paramStatusBarNotification)
  {
    paramStatusBarNotification = (Icon)paramStatusBarNotification.getNotification().extras.getParcelable("android.largeIcon.big");
    if (paramStatusBarNotification != null) {
      this.mPicture.setTag(2131951682, paramStatusBarNotification);
    }
  }
  
  public void notifyContentUpdated(StatusBarNotification paramStatusBarNotification)
  {
    super.notifyContentUpdated(paramStatusBarNotification);
    updateImageTag(paramStatusBarNotification);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\notification\NotificationBigPictureTemplateViewWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */