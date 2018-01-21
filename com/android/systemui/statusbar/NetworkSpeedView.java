package com.android.systemui.statusbar;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.systemui.statusbar.phone.NetworkSpeedController;
import com.android.systemui.statusbar.phone.NetworkSpeedController.INetworkSpeedStateCallBack;
import com.android.systemui.statusbar.phone.StatusBarIconController;

public class NetworkSpeedView
  extends LinearLayout
  implements NetworkSpeedController.INetworkSpeedStateCallBack
{
  private String TAG = "NetworkSpeedView";
  private float mDarkIntensity;
  private int mDensity;
  private float mFontScale = 1.0F;
  private int mIconTint = -1;
  private NetworkSpeedController mNetworkSpeedController;
  private TextView mSpeedView;
  private final Rect mTintArea = new Rect();
  
  public NetworkSpeedView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public NetworkSpeedView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public NetworkSpeedView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  private void updateSpeedViewSize()
  {
    float f1 = this.mContext.getResources().getDimensionPixelSize(2131755722);
    float f2 = this.mFontScale;
    float f3 = this.mContext.getResources().getDimensionPixelSize(2131755723);
    f3 = this.mFontScale;
    if (this.mSpeedView != null) {
      this.mSpeedView.setTextSize(0, f1 * f2);
    }
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    float f = this.mContext.getResources().getConfiguration().fontScale;
    int i = paramConfiguration.densityDpi;
    if ((this.mFontScale != f) || (this.mDensity != i))
    {
      this.mFontScale = f;
      this.mDensity = i;
      updateSpeedViewSize();
    }
  }
  
  protected void onFinishInflate()
  {
    this.mSpeedView = ((TextView)findViewById(2131952310));
    Configuration localConfiguration = this.mContext.getResources().getConfiguration();
    this.mFontScale = localConfiguration.fontScale;
    this.mDensity = localConfiguration.densityDpi;
    updateSpeedViewSize();
  }
  
  public void onSpeedChange(String paramString)
  {
    paramString = paramString.split(" ");
    if ((paramString.length == 2) && (this.mSpeedView != null)) {
      this.mSpeedView.setText(paramString[0] + " " + paramString[1]);
    }
  }
  
  public void onSpeedShow(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 0;; i = 8)
    {
      setVisibility(i);
      return;
    }
  }
  
  public void onTintUpdate()
  {
    if (this.mSpeedView != null) {
      this.mSpeedView.setTextColor(StatusBarIconController.getTint(this.mTintArea, this.mSpeedView, this.mIconTint));
    }
  }
  
  public void setIconTint(int paramInt, float paramFloat, Rect paramRect)
  {
    int i;
    if ((paramInt != this.mIconTint) || (paramFloat != this.mDarkIntensity)) {
      i = 1;
    }
    for (;;)
    {
      this.mIconTint = paramInt;
      this.mDarkIntensity = paramFloat;
      this.mTintArea.set(paramRect);
      if ((i != 0) && (isAttachedToWindow())) {
        onTintUpdate();
      }
      return;
      if (this.mTintArea.equals(paramRect)) {
        i = 0;
      } else {
        i = 1;
      }
    }
  }
  
  public void setNetworkSpeedController(NetworkSpeedController paramNetworkSpeedController)
  {
    this.mNetworkSpeedController = paramNetworkSpeedController;
    this.mNetworkSpeedController.addCallback(this);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\NetworkSpeedView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */