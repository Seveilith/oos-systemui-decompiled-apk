package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.android.keyguard.clock.OPTextClock;

public class SplitClockView
  extends LinearLayout
{
  private OPTextClock mAmPmView;
  private BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if (("android.intent.action.TIME_SET".equals(paramAnonymousContext)) || ("android.intent.action.TIMEZONE_CHANGED".equals(paramAnonymousContext)) || ("android.intent.action.LOCALE_CHANGED".equals(paramAnonymousContext)) || ("android.intent.action.CONFIGURATION_CHANGED".equals(paramAnonymousContext)) || ("android.intent.action.USER_SWITCHED".equals(paramAnonymousContext))) {
        SplitClockView.-wrap0(SplitClockView.this);
      }
    }
  };
  private OPTextClock mTimeView;
  
  public SplitClockView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private static int getAmPmPartEndIndex(String paramString)
  {
    int i = -1;
    int k = 0;
    int n = paramString.length();
    int j = n - 1;
    if (j >= 0)
    {
      char c = paramString.charAt(j);
      if (c == 'a') {}
      for (int m = 1;; m = 0)
      {
        boolean bool = Character.isWhitespace(c);
        if (m != 0) {
          k = 1;
        }
        if ((m == 0) && (!bool)) {
          break label72;
        }
        j -= 1;
        break;
      }
      label72:
      if (j == n - 1) {
        return -1;
      }
      if (k != 0) {
        i = j + 1;
      }
      return i;
    }
    if (k != 0) {
      i = 0;
    }
    return i;
  }
  
  private void updatePatterns()
  {
    String str3 = DateFormat.getTimeFormatString(getContext(), ActivityManager.getCurrentUser());
    int i = getAmPmPartEndIndex(str3);
    String str2;
    if (i == -1) {
      str2 = str3;
    }
    for (String str1 = "";; str1 = str3.substring(i))
    {
      this.mTimeView.setFormat12Hour(str2);
      this.mTimeView.setFormat24Hour(str2);
      this.mTimeView.setContentDescriptionFormat12Hour(str3);
      this.mTimeView.setContentDescriptionFormat24Hour(str3);
      this.mAmPmView.setFormat12Hour(str1);
      this.mAmPmView.setFormat24Hour(str1);
      return;
      str2 = str3.substring(0, i);
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.TIME_SET");
    localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
    localIntentFilter.addAction("android.intent.action.LOCALE_CHANGED");
    localIntentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
    localIntentFilter.addAction("android.intent.action.USER_SWITCHED");
    getContext().registerReceiverAsUser(this.mIntentReceiver, UserHandle.ALL, localIntentFilter, null, null);
    updatePatterns();
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    getContext().unregisterReceiver(this.mIntentReceiver);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mTimeView = ((OPTextClock)findViewById(2131952259));
    this.mAmPmView = ((OPTextClock)findViewById(2131952260));
    this.mTimeView.setShowCurrentUserTime(true);
    this.mAmPmView.setShowCurrentUserTime(true);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\SplitClockView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */