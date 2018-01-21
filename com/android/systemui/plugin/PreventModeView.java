package com.android.systemui.plugin;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.util.Utils;

public class PreventModeView
  extends RelativeLayout
{
  private final String TAG = "PreventModeView";
  private Configuration mConfig;
  private ImageView mPhone;
  private RippleView mRippleView;
  private TextView mTag;
  private TextView mTag2;
  private TextView mTagNum1;
  private TextView mTagNum2;
  private TextView mTitle;
  private TextView mTitleCancel;
  
  public PreventModeView(Context paramContext)
  {
    super(paramContext);
  }
  
  public PreventModeView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public PreventModeView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public PreventModeView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  private void playRippleAniamor()
  {
    this.mRippleView.prepare();
    this.mRippleView.startRipple();
  }
  
  public void init()
  {
    this.mTitle = ((TextView)findViewById(2131951899));
    this.mPhone = ((ImageView)findViewById(2131951900));
    this.mTitleCancel = ((TextView)findViewById(2131951901));
    this.mTag = ((TextView)findViewById(2131951904));
    this.mTag2 = ((TextView)findViewById(2131951907));
    this.mTagNum1 = ((TextView)findViewById(2131951903));
    this.mTagNum2 = ((TextView)findViewById(2131951906));
    this.mRippleView = ((RippleView)findViewById(2131951908));
    this.mConfig = new Configuration(this.mContext.getResources().getConfiguration());
    if (Utils.getDeviceTag() == "17801") {
      this.mPhone.setImageResource(2130838090);
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    this.mTitle.setText(2131689978);
    this.mTitleCancel.setText(2131689979);
    this.mTag.setText(2131689982);
    this.mTag2.setText(2131689983);
    if (paramConfiguration.fontScale != this.mConfig.fontScale)
    {
      FontSizeUtils.updateFontSize(this.mTitle, 2131755700);
      FontSizeUtils.updateFontSize(this.mTitleCancel, 2131755701);
      FontSizeUtils.updateFontSize(this.mTag, 2131755702);
      FontSizeUtils.updateFontSize(this.mTag2, 2131755703);
      FontSizeUtils.updateFontSize(this.mTagNum1, 2131755702);
      FontSizeUtils.updateFontSize(this.mTagNum2, 2131755703);
    }
    this.mConfig = new Configuration(paramConfiguration);
  }
  
  protected void onVisibilityChanged(View paramView, int paramInt)
  {
    super.onVisibilityChanged(paramView, paramInt);
    if ((this.mTitleCancel != null) && (this.mTitle != null) && (this.mTag != null))
    {
      if (paramInt != 0) {
        this.mRippleView.stopRipple();
      }
    }
    else {
      return;
    }
    playRippleAniamor();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\plugin\PreventModeView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */