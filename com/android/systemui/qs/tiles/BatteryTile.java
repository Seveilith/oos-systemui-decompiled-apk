package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.settingslib.BatteryInfo;
import com.android.settingslib.BatteryInfo.BatteryDataParser;
import com.android.settingslib.BatteryInfo.Callback;
import com.android.settingslib.graph.UsageView;
import com.android.systemui.BatteryMeterDrawable;
import com.android.systemui.qs.QSIconView;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.DetailAdapter;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.Icon;
import com.android.systemui.qs.QSTile.State;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback;
import com.android.systemui.util.MdmLogger;
import com.android.systemui.util.ThemeColorUtils;
import com.android.systemui.util.Utils;
import java.text.NumberFormat;

public class BatteryTile
  extends QSTile<QSTile.State>
  implements BatteryController.BatteryStateChangeCallback
{
  private final BatteryController mBatteryController;
  private final BatteryDetail mBatteryDetail = new BatteryDetail(null);
  private BatteryManager mBatteryManager;
  private boolean mCharging;
  private boolean mDetailShown;
  private int mLevel;
  private boolean mPluggedIn;
  private boolean mPowerSave;
  
  public BatteryTile(QSTile.Host paramHost)
  {
    super(paramHost);
    this.mBatteryController = paramHost.getBatteryController();
    this.mBatteryManager = ((BatteryManager)paramHost.getContext().getSystemService("batterymanager"));
    if (this.mBatteryManager != null) {
      this.mLevel = this.mBatteryManager.getIntProperty(4);
    }
  }
  
  public QSTile.DetailAdapter getDetailAdapter()
  {
    return this.mBatteryDetail;
  }
  
  public Intent getLongClickIntent()
  {
    return new Intent("android.intent.action.POWER_USAGE_SUMMARY");
  }
  
  public int getMetricsCategory()
  {
    return 261;
  }
  
  public CharSequence getTileLabel()
  {
    return this.mContext.getString(2131690574);
  }
  
  protected void handleClick()
  {
    if (Utils.DEBUG_ONEPLUS) {
      Log.d(this.TAG, "handleClick");
    }
    showDetail(true);
  }
  
  protected void handleUpdateState(QSTile.State paramState, Object paramObject)
  {
    int i;
    StringBuilder localStringBuilder;
    if (paramObject != null)
    {
      i = ((Integer)paramObject).intValue();
      paramObject = NumberFormat.getPercentInstance().format(i / 100.0D);
      if (Utils.DEBUG_ONEPLUS) {
        Log.d(this.TAG, "update, " + this.mPowerSave);
      }
      paramState.icon = new QSTile.Icon()
      {
        public Drawable getDrawable(Context paramAnonymousContext)
        {
          paramAnonymousContext = new BatteryMeterDrawable(paramAnonymousContext, new Handler(Looper.getMainLooper()), paramAnonymousContext.getColor(2131492990));
          paramAnonymousContext.enableLog(true);
          paramAnonymousContext.onBatteryLevelChanged(BatteryTile.-get6(BatteryTile.this), BatteryTile.-get7(BatteryTile.this), BatteryTile.-get2(BatteryTile.this));
          paramAnonymousContext.onPowerSaveChanged(BatteryTile.-get8(BatteryTile.this));
          paramAnonymousContext.setCustomColor(QSIconView.sIconColor, QSIconView.sCustomDisableIconColor);
          return paramAnonymousContext;
        }
        
        public int getPadding()
        {
          return BatteryTile.-get5(BatteryTile.this).getContext().getResources().getDimensionPixelSize(2131755444);
        }
      };
      paramState.label = ((CharSequence)paramObject);
      localStringBuilder = new StringBuilder().append(this.mContext.getString(2131690196, new Object[] { paramObject })).append(",");
      if (!this.mPowerSave) {
        break label184;
      }
      paramObject = this.mContext.getString(2131690394);
    }
    for (;;)
    {
      paramState.contentDescription = ((String)paramObject + "," + this.mContext.getString(2131690165));
      paramObject = Button.class.getName();
      paramState.expandedAccessibilityClassName = ((String)paramObject);
      paramState.minimalAccessibilityClassName = ((String)paramObject);
      return;
      i = this.mLevel;
      break;
      label184:
      if (this.mCharging) {
        paramObject = this.mContext.getString(2131690338);
      } else {
        paramObject = "";
      }
    }
  }
  
  public QSTile.State newTileState()
  {
    return new QSTile.State();
  }
  
  public void onBatteryLevelChanged(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mLevel = paramInt;
    this.mPluggedIn = paramBoolean1;
    this.mCharging = paramBoolean2;
    refreshState(Integer.valueOf(paramInt));
    if (this.mDetailShown) {
      BatteryDetail.-wrap2(this.mBatteryDetail);
    }
  }
  
  public void onBatteryPercentShowChange(boolean paramBoolean) {}
  
  public void onBatteryStyleChanged(int paramInt) {}
  
  public void onFastChargeChanged(boolean paramBoolean) {}
  
  public void onPowerSaveChanged(boolean paramBoolean)
  {
    Log.d(this.TAG, "onPowerSaveChanged , " + paramBoolean + ", detail:" + this.mDetailShown);
    this.mPowerSave = paramBoolean;
    refreshState(null);
    if (this.mDetailShown) {
      BatteryDetail.-wrap2(this.mBatteryDetail);
    }
  }
  
  public void setDetailListening(boolean paramBoolean)
  {
    super.setDetailListening(paramBoolean);
    if (!paramBoolean) {
      BatteryDetail.-set0(this.mBatteryDetail, null);
    }
  }
  
  public void setListening(boolean paramBoolean)
  {
    Log.d(this.TAG, "listen: " + paramBoolean);
    if (paramBoolean)
    {
      this.mBatteryController.addStateChangedCallback(this);
      return;
    }
    this.mBatteryController.removeStateChangedCallback(this);
  }
  
  private final class BatteryDetail
    implements QSTile.DetailAdapter, View.OnClickListener, View.OnAttachStateChangeListener
  {
    private final KeyguardUpdateMonitorCallback mCallback = new KeyguardUpdateMonitorCallback()
    {
      public void onTimeChanged()
      {
        Log.i(BatteryTile.-get0(BatteryTile.this), "onTimeChanged");
        BatteryTile.BatteryDetail.-wrap2(BatteryTile.BatteryDetail.this);
      }
    };
    private View mCurrentView;
    private final BatteryMeterDrawable mDrawable = new BatteryMeterDrawable(BatteryTile.-get5(BatteryTile.this).getContext(), new Handler(), BatteryTile.-get5(BatteryTile.this).getContext().getColor(2131492990));
    
    private BatteryDetail() {}
    
    private void bindBatteryInfo(final BatteryInfo paramBatteryInfo)
    {
      final Object localObject = new SpannableStringBuilder();
      ((SpannableStringBuilder)localObject).append(paramBatteryInfo.batteryPercentString, new RelativeSizeSpan(2.6F), 17);
      if (paramBatteryInfo.remainingLabel != null)
      {
        if (!BatteryTile.-get3(BatteryTile.this).getResources().getBoolean(2131558418)) {
          break label247;
        }
        ((SpannableStringBuilder)localObject).append(' ');
      }
      for (;;)
      {
        ((SpannableStringBuilder)localObject).append(paramBatteryInfo.remainingLabel);
        ThemeColorUtils.getColor(ThemeColorUtils.QS_TILE_ICON);
        int i = ThemeColorUtils.getColor(ThemeColorUtils.QS_TILE_ICON);
        int j = ThemeColorUtils.getColor(ThemeColorUtils.PROGRESS);
        ((TextView)this.mCurrentView.findViewById(2131951794)).setText((CharSequence)localObject);
        ((TextView)this.mCurrentView.findViewById(2131951794)).setTextColor(j);
        localObject = (UsageView)this.mCurrentView.findViewById(2131951795);
        if (this.mCurrentView != null) {
          this.mCurrentView.postDelayed(new Runnable()
          {
            public void run()
            {
              if (Utils.DEBUG_ONEPLUS) {
                Log.d(BatteryTile.-get0(BatteryTile.this), "bindHistory b, " + BatteryTile.-get4(BatteryTile.this));
              }
              if ((!BatteryTile.-get4(BatteryTile.this)) || (paramBatteryInfo == null)) {
                return;
              }
              paramBatteryInfo.bindHistory(localObject, new BatteryInfo.BatteryDataParser[0]);
              if (Utils.DEBUG_ONEPLUS) {
                Log.d(BatteryTile.-get0(BatteryTile.this), "bindHistory e");
              }
            }
          }, 300L);
        }
        ((TextView)((UsageView)localObject).findViewById(2131952337)).setTextColor(i);
        ((TextView)((UsageView)localObject).findViewById(2131952335)).setTextColor(i);
        ((TextView)((UsageView)localObject).findViewById(2131952333)).setTextColor(i);
        ((TextView)((UsageView)localObject).findViewById(2131952340)).setTextColor(i);
        ((TextView)((UsageView)localObject).findViewById(2131952341)).setTextColor(i);
        ((UsageView)localObject).setAccentColor(j);
        return;
        label247:
        ((SpannableStringBuilder)localObject).append('\n');
      }
    }
    
    private void bindView()
    {
      if (this.mCurrentView == null) {
        return;
      }
      this.mDrawable.onBatteryLevelChanged(100, false, false);
      this.mDrawable.onPowerSaveChanged(true);
      this.mDrawable.disableShowPercent();
      ((ImageView)this.mCurrentView.findViewById(16908294)).setImageDrawable(this.mDrawable);
      ((Checkable)this.mCurrentView.findViewById(16908311)).setChecked(BatteryTile.-get8(BatteryTile.this));
      BatteryInfo.getBatteryInfo(BatteryTile.-get3(BatteryTile.this), new BatteryInfo.Callback()
      {
        public void onBatteryInfoLoaded(BatteryInfo paramAnonymousBatteryInfo)
        {
          if (BatteryTile.BatteryDetail.-get0(BatteryTile.BatteryDetail.this) != null) {
            BatteryTile.BatteryDetail.-wrap0(BatteryTile.BatteryDetail.this, paramAnonymousBatteryInfo);
          }
        }
      });
      TextView localTextView1 = (TextView)this.mCurrentView.findViewById(16908310);
      TextView localTextView2 = (TextView)this.mCurrentView.findViewById(16908304);
      Switch localSwitch = (Switch)this.mCurrentView.findViewById(16908311);
      int i = ThemeColorUtils.getColor(ThemeColorUtils.QS_TILE_ICON);
      int j = ThemeColorUtils.getColor(ThemeColorUtils.PROGRESS);
      int k = ThemeColorUtils.getColor(ThemeColorUtils.QS_TILE_ICON_DISABLE);
      ColorStateList localColorStateList = new ColorStateList(new int[][] { { 16842912 }, new int[0] }, new int[] { j, k });
      localTextView1.setTextColor(i);
      localTextView2.setTextColor(i);
      localSwitch.setThumbTintList(localColorStateList);
      localSwitch.setTrackTintList(localColorStateList);
      this.mDrawable.setCustomColor(i, k);
      if (BatteryTile.-get2(BatteryTile.this))
      {
        this.mCurrentView.findViewById(2131951796).setAlpha(0.7F);
        localTextView1.setTextSize(2, 14.0F);
        localTextView1.setText(2131690525);
        this.mCurrentView.findViewById(16908311).setVisibility(8);
        this.mCurrentView.findViewById(2131951796).setClickable(false);
        return;
      }
      this.mCurrentView.findViewById(2131951796).setAlpha(1.0F);
      localTextView1.setTextSize(2, 16.0F);
      localTextView1.setText(2131690526);
      localTextView2.setText(2131690527);
      this.mCurrentView.findViewById(16908311).setVisibility(0);
      this.mCurrentView.findViewById(2131951796).setClickable(true);
      this.mCurrentView.findViewById(2131951796).setOnClickListener(this);
    }
    
    private void postBindView()
    {
      if (this.mCurrentView == null) {
        return;
      }
      this.mCurrentView.post(new Runnable()
      {
        public void run()
        {
          BatteryTile.BatteryDetail.-wrap1(BatteryTile.BatteryDetail.this);
        }
      });
    }
    
    public View createDetailView(Context paramContext, View paramView, ViewGroup paramViewGroup)
    {
      if (Utils.DEBUG_ONEPLUS) {
        Log.d(BatteryTile.-get0(BatteryTile.this), "createDetailView");
      }
      paramContext = paramView;
      if (paramView == null) {
        paramContext = LayoutInflater.from(BatteryTile.-get3(BatteryTile.this)).inflate(2130968605, paramViewGroup, false);
      }
      this.mCurrentView = paramContext;
      this.mCurrentView.addOnAttachStateChangeListener(this);
      bindView();
      return paramContext;
    }
    
    public int getMetricsCategory()
    {
      return 274;
    }
    
    public Intent getSettingsIntent()
    {
      return new Intent("android.intent.action.POWER_USAGE_SUMMARY");
    }
    
    public CharSequence getTitle()
    {
      return BatteryTile.-get3(BatteryTile.this).getString(2131690524, new Object[] { Integer.valueOf(BatteryTile.-get6(BatteryTile.this)) });
    }
    
    public Boolean getToggleState()
    {
      return null;
    }
    
    public void onClick(View paramView)
    {
      MdmLogger.logQsTile(BatteryTile.-get0(BatteryTile.this), "full_switch", "1");
      paramView = BatteryTile.-get1(BatteryTile.this);
      if (BatteryTile.-get8(BatteryTile.this)) {}
      for (boolean bool = false;; bool = true)
      {
        paramView.setPowerSaveMode(bool);
        return;
      }
    }
    
    public void onViewAttachedToWindow(View paramView)
    {
      if (!BatteryTile.-get4(BatteryTile.this))
      {
        BatteryTile.-set0(BatteryTile.this, true);
        KeyguardUpdateMonitor.getInstance(BatteryTile.-get3(BatteryTile.this)).registerCallback(this.mCallback);
      }
    }
    
    public void onViewDetachedFromWindow(View paramView)
    {
      if (BatteryTile.-get4(BatteryTile.this))
      {
        BatteryTile.-set0(BatteryTile.this, false);
        KeyguardUpdateMonitor.getInstance(BatteryTile.-get3(BatteryTile.this)).removeCallback(this.mCallback);
      }
    }
    
    public void setToggleState(boolean paramBoolean) {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\BatteryTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */