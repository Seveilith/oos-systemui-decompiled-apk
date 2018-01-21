package com.android.systemui.statusbar.notification;

import android.content.Context;
import android.service.notification.StatusBarNotification;
import android.view.View;
import com.android.internal.widget.ImageFloatingTextView;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.ViewTransformationHelper;

public class NotificationBigTextTemplateViewWrapper
  extends NotificationTemplateViewWrapper
{
  private ImageFloatingTextView mBigtext;
  
  protected NotificationBigTextTemplateViewWrapper(Context paramContext, View paramView, ExpandableNotificationRow paramExpandableNotificationRow)
  {
    super(paramContext, paramView, paramExpandableNotificationRow);
  }
  
  private void resolveViews(StatusBarNotification paramStatusBarNotification)
  {
    this.mBigtext = ((ImageFloatingTextView)this.mView.findViewById(16909247));
  }
  
  public void notifyContentUpdated(StatusBarNotification paramStatusBarNotification)
  {
    resolveViews(paramStatusBarNotification);
    super.notifyContentUpdated(paramStatusBarNotification);
  }
  
  protected void updateTransformedTypes()
  {
    super.updateTransformedTypes();
    if (this.mBigtext != null) {
      this.mTransformationHelper.addTransformedView(2, this.mBigtext);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\notification\NotificationBigTextTemplateViewWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */