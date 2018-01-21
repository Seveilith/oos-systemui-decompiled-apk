package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.android.keyguard.KeyguardStatusView.Patterns;
import com.android.keyguard.clock.OPTextClock;

public class OPFacelockLightView
  extends RelativeLayout
{
  private final String TAG = "OPFacelockLightView";
  private Button mButton;
  private OPTextClock mClockView;
  private Configuration mConfig;
  private Context mContext;
  private OPTextClock mDateView;
  private LinearLayout mInnerView;
  private TextView mNotify;
  private TextView mNotifyView;
  
  public OPFacelockLightView(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
  }
  
  public OPFacelockLightView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
  }
  
  public OPFacelockLightView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mContext = paramContext;
  }
  
  public OPFacelockLightView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mContext = paramContext;
  }
  
  private void refresh()
  {
    KeyguardStatusView.Patterns.update(this.mContext, false);
    refreshTime();
  }
  
  private void relayout()
  {
    int i = this.mContext.getResources().getDimensionPixelSize(2131755732);
    RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams)this.mInnerView.getLayoutParams();
    localLayoutParams.topMargin = ((int)(i * 0.125D));
    localLayoutParams.bottomMargin = ((int)(i * 0.174D));
    this.mInnerView.setLayoutParams(localLayoutParams);
  }
  
  public void init()
  {
    this.mInnerView = ((LinearLayout)findViewById(2131952137));
    this.mNotify = ((TextView)findViewById(2131952140));
    this.mConfig = new Configuration(this.mContext.getResources().getConfiguration());
    this.mClockView = ((OPTextClock)findViewById(2131952138));
    this.mDateView = ((OPTextClock)findViewById(2131952139));
    this.mNotifyView = ((TextView)findViewById(2131952140));
    this.mButton = ((Button)findViewById(2131952141));
    Log.d("OPFacelockLightView", "init");
    relayout();
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    this.mNotify.setText(2131690025);
    this.mConfig = new Configuration(paramConfiguration);
    this.mClockView.setTextSize(0, getResources().getDimensionPixelSize(2131755744));
    paramConfiguration = (ViewGroup.MarginLayoutParams)this.mClockView.getLayoutParams();
    paramConfiguration.bottomMargin = getResources().getDimensionPixelSize(2131755742);
    this.mClockView.setLayoutParams(paramConfiguration);
    this.mDateView.setTextSize(0, getResources().getDimensionPixelSize(2131755743));
    this.mNotifyView.setTextSize(0, getResources().getDimensionPixelSize(2131755734));
    this.mButton.setTextSize(0, getResources().getDimensionPixelSize(2131755735));
  }
  
  protected void onVisibilityChanged(View paramView, int paramInt)
  {
    super.onVisibilityChanged(paramView, paramInt);
    if (paramInt == 0) {
      refresh();
    }
  }
  
  public void refreshTime()
  {
    this.mDateView.setFormat24Hour(KeyguardStatusView.Patterns.dateView);
    this.mDateView.setFormat12Hour(KeyguardStatusView.Patterns.dateView);
    this.mClockView.setFormat12Hour(KeyguardStatusView.Patterns.clockView12);
    this.mClockView.setFormat24Hour(KeyguardStatusView.Patterns.clockView24);
  }
  
  public void setClockY(float paramFloat)
  {
    this.mInnerView.setY(paramFloat);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\OPFacelockLightView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */