package com.android.systemui.statusbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.systemui.ViewInvertHelper;

public class NotificationOverflowContainer
  extends ActivatableNotificationView
{
  private View mContent;
  private boolean mDark;
  private NotificationOverflowIconsView mIconsView;
  private ViewInvertHelper mViewInvertHelper;
  
  public NotificationOverflowContainer(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  protected View getContentView()
  {
    return this.mContent;
  }
  
  public NotificationOverflowIconsView getIconsView()
  {
    return this.mIconsView;
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mIconsView = ((NotificationOverflowIconsView)findViewById(2131952287));
    this.mIconsView.setMoreText((TextView)findViewById(2131952285));
    this.mIconsView.setOverflowIndicator(findViewById(2131952286));
    this.mContent = findViewById(2131951832);
    this.mViewInvertHelper = new ViewInvertHelper(this.mContent, 700L);
  }
  
  public void setDark(boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    super.setDark(paramBoolean1, paramBoolean2, paramLong);
    if (this.mDark == paramBoolean1) {
      return;
    }
    this.mDark = paramBoolean1;
    if (paramBoolean2)
    {
      this.mViewInvertHelper.fade(paramBoolean1, paramLong);
      return;
    }
    this.mViewInvertHelper.update(paramBoolean1);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\NotificationOverflowContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */