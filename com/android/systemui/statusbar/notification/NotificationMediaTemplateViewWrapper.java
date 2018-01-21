package com.android.systemui.statusbar.notification;

import android.content.Context;
import android.service.notification.StatusBarNotification;
import android.view.View;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.ViewTransformationHelper;

public class NotificationMediaTemplateViewWrapper
  extends NotificationTemplateViewWrapper
{
  View mActions;
  
  protected NotificationMediaTemplateViewWrapper(Context paramContext, View paramView, ExpandableNotificationRow paramExpandableNotificationRow)
  {
    super(paramContext, paramView, paramExpandableNotificationRow);
  }
  
  private void resolveViews(StatusBarNotification paramStatusBarNotification)
  {
    this.mActions = this.mView.findViewById(16909245);
  }
  
  public void notifyContentUpdated(StatusBarNotification paramStatusBarNotification)
  {
    resolveViews(paramStatusBarNotification);
    super.notifyContentUpdated(paramStatusBarNotification);
  }
  
  protected void updateTransformedTypes()
  {
    super.updateTransformedTypes();
    if (this.mActions != null) {
      this.mTransformationHelper.addTransformedView(5, this.mActions);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\notification\NotificationMediaTemplateViewWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */