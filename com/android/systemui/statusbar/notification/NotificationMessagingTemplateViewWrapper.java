package com.android.systemui.statusbar.notification;

import android.content.Context;
import android.service.notification.StatusBarNotification;
import android.view.View;
import com.android.internal.widget.MessagingLinearLayout;
import com.android.systemui.statusbar.ExpandableNotificationRow;
import com.android.systemui.statusbar.ViewTransformationHelper;

public class NotificationMessagingTemplateViewWrapper
  extends NotificationTemplateViewWrapper
{
  private View mContractedMessage;
  
  protected NotificationMessagingTemplateViewWrapper(Context paramContext, View paramView, ExpandableNotificationRow paramExpandableNotificationRow)
  {
    super(paramContext, paramView, paramExpandableNotificationRow);
  }
  
  private void resolveViews()
  {
    this.mContractedMessage = null;
    Object localObject = this.mView.findViewById(16909256);
    if (((localObject instanceof MessagingLinearLayout)) && (((MessagingLinearLayout)localObject).getChildCount() > 0))
    {
      localObject = (MessagingLinearLayout)localObject;
      View localView = ((MessagingLinearLayout)localObject).getChildAt(0);
      if (localView.getId() == ((MessagingLinearLayout)localObject).getContractedChildId()) {
        this.mContractedMessage = localView;
      }
    }
  }
  
  public void notifyContentUpdated(StatusBarNotification paramStatusBarNotification)
  {
    resolveViews();
    super.notifyContentUpdated(paramStatusBarNotification);
  }
  
  protected void updateTransformedTypes()
  {
    super.updateTransformedTypes();
    if (this.mContractedMessage != null) {
      this.mTransformationHelper.addTransformedView(2, this.mContractedMessage);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\notification\NotificationMessagingTemplateViewWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */