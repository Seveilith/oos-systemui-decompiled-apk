package com.android.settingslib.wifi;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Looper;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.settingslib.R.attr;
import com.android.settingslib.R.string;

public class AccessPointPreference
  extends Preference
{
  private static final int[] STATE_NONE;
  private static final int[] STATE_SECURED = { R.attr.state_encrypted };
  static final int[] WIFI_CONNECTION_STRENGTH = { R.string.accessibility_wifi_one_bar, R.string.accessibility_wifi_two_bars, R.string.accessibility_wifi_three_bars, R.string.accessibility_wifi_signal_full };
  private static int[] wifi_signal_attributes;
  private AccessPoint mAccessPoint;
  private Drawable mBadge;
  private final UserBadgeCache mBadgeCache = null;
  private final int mBadgePadding = 0;
  private CharSequence mContentDescription;
  private boolean mForSavedNetworks = false;
  private int mLevel;
  private final Runnable mNotifyChanged = new Runnable()
  {
    public void run()
    {
      AccessPointPreference.this.notifyChanged();
    }
  };
  private TextView mTitleView;
  private final StateListDrawable mWifiSld = null;
  
  static
  {
    STATE_NONE = new int[0];
    wifi_signal_attributes = new int[] { R.attr.wifi_signal };
  }
  
  public AccessPointPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void postNotifyChanged()
  {
    try
    {
      if (this.mTitleView != null) {
        this.mTitleView.post(this.mNotifyChanged);
      }
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  protected void notifyChanged()
  {
    if (Looper.getMainLooper() != Looper.myLooper())
    {
      postNotifyChanged();
      return;
    }
    super.notifyChanged();
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    if (this.mAccessPoint == null) {
      return;
    }
    Drawable localDrawable = getIcon();
    if (localDrawable != null) {
      localDrawable.setLevel(this.mLevel);
    }
    this.mTitleView = ((TextView)paramPreferenceViewHolder.findViewById(16908310));
    if (this.mTitleView != null)
    {
      this.mTitleView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, this.mBadge, null);
      this.mTitleView.setCompoundDrawablePadding(this.mBadgePadding);
    }
    paramPreferenceViewHolder.itemView.setContentDescription(this.mContentDescription);
  }
  
  public static class UserBadgeCache {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\settingslib\wifi\AccessPointPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */