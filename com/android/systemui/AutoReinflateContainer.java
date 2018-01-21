package com.android.systemui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.LocaleList;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import com.android.systemui.qs.GameModeDemo;
import com.android.systemui.qs.QSDetailItems;
import com.android.systemui.qs.QSIconView;
import com.android.systemui.qs.QSTileView;
import com.android.systemui.util.ThemeColorUtils;
import com.android.systemui.util.Utils;
import java.util.ArrayList;
import java.util.List;

public class AutoReinflateContainer
  extends FrameLayout
{
  private int mBlackAccentColor;
  private int mDensity;
  private GameModeDemo mGameModeDemo;
  private final List<InflateListener> mInflateListeners = new ArrayList();
  private final int mLayout;
  private LocaleList mLocaleList;
  private ThemeChangeReceiver mReceiver;
  private boolean mSpecialTheme;
  private int mThemeColor;
  private int mWhiteAccentColor;
  
  public AutoReinflateContainer(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mDensity = paramContext.getResources().getConfiguration().densityDpi;
    this.mLocaleList = paramContext.getResources().getConfiguration().getLocales();
    this.mSpecialTheme = Utils.isSpecialTheme(paramContext);
    this.mThemeColor = Utils.getThemeColor(paramContext);
    this.mWhiteAccentColor = Utils.getThemeWhiteAccentColor(this.mContext, 2131493076);
    this.mBlackAccentColor = Utils.getThemeBlackAccentColor(this.mContext, 2131493076);
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.AutoReinflateContainer);
    if (!paramAttributeSet.hasValue(0)) {
      throw new IllegalArgumentException("AutoReinflateContainer must contain a layout");
    }
    this.mLayout = paramAttributeSet.getResourceId(0, 0);
    ThemeColorUtils.init(paramContext);
    this.mReceiver = new ThemeChangeReceiver(null);
    this.mReceiver.init();
    inflateLayout();
    this.mGameModeDemo = new GameModeDemo(paramContext);
  }
  
  private void inflateLayout()
  {
    updateThemeColor();
    removeAllViews();
    LayoutInflater.from(getContext()).inflate(this.mLayout, this);
    int j = this.mInflateListeners.size();
    int i = 0;
    while (i < j)
    {
      ((InflateListener)this.mInflateListeners.get(i)).onInflated(getChildAt(0));
      i += 1;
    }
  }
  
  private void updateThemeColor()
  {
    ThemeColorUtils.init(this.mContext);
    int i = ThemeColorUtils.getColor(ThemeColorUtils.QS_PRIMARY_TEXT);
    int j = ThemeColorUtils.getColor(ThemeColorUtils.QS_SECONDARY_TEXT);
    int k = ThemeColorUtils.getColor(ThemeColorUtils.QS_TILE_ICON);
    int m = ThemeColorUtils.getColor(ThemeColorUtils.QS_TILE_ICON_DISABLE);
    QSTileView.updateThemeColor(ThemeColorUtils.getColor(ThemeColorUtils.QS_TILE_LABEL));
    QSIconView.updateThemeColor(k, m);
    QSDetailItems.updateThemeColor(i, j, k);
  }
  
  public void addInflateListener(InflateListener paramInflateListener)
  {
    this.mInflateListeners.add(paramInflateListener);
    paramInflateListener.onInflated(getChildAt(0));
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    int i = 0;
    int j = paramConfiguration.densityDpi;
    this.mGameModeDemo.updateResources();
    if (j != this.mDensity)
    {
      this.mDensity = j;
      i = 1;
    }
    paramConfiguration = paramConfiguration.getLocales();
    if (paramConfiguration != this.mLocaleList)
    {
      this.mLocaleList = paramConfiguration;
      i = 1;
    }
    if (i != 0) {
      inflateLayout();
    }
  }
  
  public static abstract interface InflateListener
  {
    public abstract void onInflated(View paramView);
  }
  
  private final class ThemeChangeReceiver
    extends BroadcastReceiver
  {
    private ThemeChangeReceiver() {}
    
    public void init()
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
      AutoReinflateContainer.-get1(AutoReinflateContainer.this).registerReceiver(this, localIntentFilter);
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      int i = Utils.getThemeColor(AutoReinflateContainer.-get1(AutoReinflateContainer.this));
      boolean bool = Utils.isSpecialTheme(AutoReinflateContainer.-get1(AutoReinflateContainer.this));
      int j = Utils.getThemeWhiteAccentColor(AutoReinflateContainer.-get1(AutoReinflateContainer.this), 2131493076);
      int k = Utils.getThemeBlackAccentColor(AutoReinflateContainer.-get1(AutoReinflateContainer.this), 2131493076);
      Log.d("AutoReinflateContainer", String.format("mThemeColor=0x%x, themeColor=0x%x, mWhiteAccentColor=0x%x, whiteAccentColor=0x%x, mBlackAccentColor=0x%x, blackAccentColor=0x%x, mSpecialTheme=%b, specialTheme=%b", new Object[] { Integer.valueOf(AutoReinflateContainer.-get3(AutoReinflateContainer.this)), Integer.valueOf(i), Integer.valueOf(AutoReinflateContainer.-get4(AutoReinflateContainer.this)), Integer.valueOf(j), Integer.valueOf(AutoReinflateContainer.-get0(AutoReinflateContainer.this)), Integer.valueOf(k), Boolean.valueOf(AutoReinflateContainer.-get2(AutoReinflateContainer.this)), Boolean.valueOf(bool) }));
      if ((i != AutoReinflateContainer.-get3(AutoReinflateContainer.this)) || (AutoReinflateContainer.-get4(AutoReinflateContainer.this) != j)) {
        break label231;
      }
      for (;;)
      {
        AutoReinflateContainer.-set2(AutoReinflateContainer.this, i);
        AutoReinflateContainer.-set1(AutoReinflateContainer.this, bool);
        AutoReinflateContainer.-set3(AutoReinflateContainer.this, j);
        AutoReinflateContainer.-set0(AutoReinflateContainer.this, k);
        ThemeColorUtils.updateAccentColor(AutoReinflateContainer.-get1(AutoReinflateContainer.this));
        AutoReinflateContainer.-wrap0(AutoReinflateContainer.this);
        label231:
        return;
        if (AutoReinflateContainer.-get0(AutoReinflateContainer.this) == k) {
          if (AutoReinflateContainer.-get2(AutoReinflateContainer.this) == bool) {
            break;
          }
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\AutoReinflateContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */