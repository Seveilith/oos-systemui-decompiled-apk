package com.android.systemui.statusbar.notification;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class HybridGroupManager
{
  private final Context mContext;
  private int mOverflowNumberColor;
  private ViewGroup mParent;
  
  public HybridGroupManager(Context paramContext, ViewGroup paramViewGroup)
  {
    this.mContext = paramContext;
    this.mParent = paramViewGroup;
  }
  
  private HybridNotificationView inflateHybridView()
  {
    HybridNotificationView localHybridNotificationView = (HybridNotificationView)((LayoutInflater)this.mContext.getSystemService(LayoutInflater.class)).inflate(2130968631, this.mParent, false);
    this.mParent.addView(localHybridNotificationView);
    return localHybridNotificationView;
  }
  
  private TextView inflateOverflowNumber()
  {
    TextView localTextView = (TextView)((LayoutInflater)this.mContext.getSystemService(LayoutInflater.class)).inflate(2130968632, this.mParent, false);
    this.mParent.addView(localTextView);
    updateOverFlowNumberColor(localTextView);
    return localTextView;
  }
  
  private CharSequence resolveText(Notification paramNotification)
  {
    CharSequence localCharSequence2 = paramNotification.extras.getCharSequence("android.text");
    CharSequence localCharSequence1 = localCharSequence2;
    if (localCharSequence2 == null) {
      localCharSequence1 = paramNotification.extras.getCharSequence("android.bigText");
    }
    return localCharSequence1;
  }
  
  private CharSequence resolveTitle(Notification paramNotification)
  {
    CharSequence localCharSequence2 = paramNotification.extras.getCharSequence("android.title");
    CharSequence localCharSequence1 = localCharSequence2;
    if (localCharSequence2 == null) {
      localCharSequence1 = paramNotification.extras.getCharSequence("android.title.big");
    }
    return localCharSequence1;
  }
  
  private void updateOverFlowNumberColor(TextView paramTextView)
  {
    paramTextView.setTextColor(this.mOverflowNumberColor);
  }
  
  public HybridNotificationView bindFromNotification(HybridNotificationView paramHybridNotificationView, Notification paramNotification)
  {
    HybridNotificationView localHybridNotificationView = paramHybridNotificationView;
    if (paramHybridNotificationView == null) {
      localHybridNotificationView = inflateHybridView();
    }
    localHybridNotificationView.bind(resolveTitle(paramNotification), resolveText(paramNotification));
    return localHybridNotificationView;
  }
  
  public TextView bindOverflowNumber(TextView paramTextView, int paramInt)
  {
    TextView localTextView = paramTextView;
    if (paramTextView == null) {
      localTextView = inflateOverflowNumber();
    }
    paramTextView = this.mContext.getResources().getString(2131690252, new Object[] { Integer.valueOf(paramInt) });
    if (!paramTextView.equals(localTextView.getText())) {
      localTextView.setText(paramTextView);
    }
    localTextView.setContentDescription(String.format(this.mContext.getResources().getQuantityString(2132017153, paramInt), new Object[] { Integer.valueOf(paramInt) }));
    return localTextView;
  }
  
  public void setOverflowNumberColor(TextView paramTextView, int paramInt)
  {
    this.mOverflowNumberColor = paramInt;
    if (paramTextView != null) {
      updateOverFlowNumberColor(paramTextView);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\notification\HybridGroupManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */