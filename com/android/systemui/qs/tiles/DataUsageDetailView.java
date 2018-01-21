package com.android.systemui.qs.tiles;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.android.settingslib.net.DataUsageController.DataUsageInfo;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.qs.DataUsageGraph;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.util.ThemeColorUtils;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Formatter;
import java.util.Locale;
import org.codeaurora.internal.IExtTelephony;
import org.codeaurora.internal.IExtTelephony.Stub;

public class DataUsageDetailView
  extends LinearLayout
{
  private static int BYTES_TYPE_NUMBER = 1;
  private static int BYTES_TYPE_UNIT = 2;
  private static int BYTES_TYPE_WHOLE = 0;
  private final DecimalFormat FORMAT = new DecimalFormat("#.##");
  private final String TAG = "DataUsageDetailView";
  private DataUsageBuilder mBuilder;
  private IExtTelephony mExtTelephony = IExtTelephony.Stub.asInterface(ServiceManager.getService("extphone"));
  private MyCallStateListener mPhoneStateListener;
  private boolean mRegistered = false;
  private int mSelectedDataSim = 0;
  private Intent mSettingsIntent = CellularTile.CELLULAR_SETTINGS;
  private int mSimCount = 0;
  private View mSimLayout = null;
  BroadcastReceiver mSimReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      Log.d("DataUsageDetailView", "mSimReceiver:onReceive");
      DataUsageDetailView.this.bindData();
    }
  };
  private Spinner mSimSelector = null;
  private SubscriptionManager mSubscriptionManager;
  private TelephonyManager mTelephonyManager;
  private Handler mUiHandler;
  
  public DataUsageDetailView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mSubscriptionManager = SubscriptionManager.from(paramContext);
    this.mTelephonyManager = ((TelephonyManager)paramContext.getSystemService("phone"));
    this.mPhoneStateListener = new MyCallStateListener();
    this.mUiHandler = new Handler();
  }
  
  private void bindDataInternal()
  {
    final DataUsageController.DataUsageInfo localDataUsageInfo = this.mBuilder.buildOneplusDataUsage(getDefaultDataSimIndex(), false);
    this.mUiHandler.post(new Runnable()
    {
      public void run()
      {
        DataUsageDetailView.this.bind(localDataUsageInfo);
      }
    });
  }
  
  private String formatBytes(long paramLong)
  {
    return formatBytes(paramLong, BYTES_TYPE_WHOLE);
  }
  
  private String formatBytes(long paramLong, int paramInt)
  {
    long l = Math.abs(paramLong);
    double d;
    Object localObject;
    StringBuilder localStringBuilder;
    DecimalFormat localDecimalFormat;
    if (l > 1.073741824E9D)
    {
      d = l / 1.073741824E9D;
      localObject = "GB";
      if (paramInt != BYTES_TYPE_WHOLE) {
        break label135;
      }
      localStringBuilder = new StringBuilder();
      localDecimalFormat = this.FORMAT;
      if (paramLong >= 0L) {
        break label130;
      }
    }
    label130:
    for (paramInt = -1;; paramInt = 1)
    {
      return localDecimalFormat.format(paramInt * d) + " " + (String)localObject;
      if (l > 1048576.0D)
      {
        d = l / 1048576.0D;
        localObject = "MB";
        break;
      }
      d = l / 1024.0D;
      localObject = "KB";
      break;
    }
    label135:
    if (paramInt == BYTES_TYPE_NUMBER)
    {
      localObject = this.FORMAT;
      if (paramLong < 0L) {}
      for (paramInt = -1;; paramInt = 1) {
        return ((DecimalFormat)localObject).format(paramInt * d);
      }
    }
    return (String)localObject;
  }
  
  private int getDefaultDataSimIndex()
  {
    return this.mSubscriptionManager.getDefaultDataPhoneId();
  }
  
  private boolean isProvisioned(int paramInt)
  {
    if (this.mExtTelephony == null) {
      this.mExtTelephony = IExtTelephony.Stub.asInterface(ServiceManager.getService("extphone"));
    }
    try
    {
      i = this.mExtTelephony.getCurrentUiccCardProvisioningStatus(paramInt);
      paramInt = i;
    }
    catch (NullPointerException localNullPointerException)
    {
      for (;;)
      {
        i = -1;
        this.mExtTelephony = null;
        Log.e("DataUsageDetailView", "Failed to get pref, phoneId: " + paramInt + " Exception: " + localNullPointerException);
        paramInt = i;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        int i = -1;
        this.mExtTelephony = null;
        Log.e("DataUsageDetailView", "Failed to get pref, phoneId: " + paramInt + " Exception: " + localRemoteException);
        paramInt = i;
      }
    }
    return paramInt == 1;
  }
  
  private void setDefaultDataSimIndex(int paramInt)
  {
    try
    {
      if (!isProvisioned(paramInt))
      {
        Log.d("DataUsageDetailView", "return oemDdsSwitch when not provision:" + paramInt);
        return;
      }
      Log.d("DataUsageDetailView", "oemDdsSwitch:phoneId=" + paramInt);
      Method localMethod = this.mExtTelephony.getClass().getDeclaredMethod("oemDdsSwitch", new Class[] { Integer.TYPE });
      localMethod.setAccessible(true);
      localMethod.invoke(this.mExtTelephony, new Object[] { Integer.valueOf(paramInt) });
      return;
    }
    catch (Exception localException)
    {
      Log.d("DataUsageDetailView", "setDefaultDataSimId", localException);
    }
  }
  
  private void updateSimCount()
  {
    Object localObject = SystemProperties.get("gsm.sim.state");
    Log.d("DataUsageDetailView", "updateSimCount:simState=" + (String)localObject);
    this.mSimCount = 0;
    for (;;)
    {
      int i;
      try
      {
        localObject = TextUtils.split((String)localObject, ",");
        i = 0;
        if (i < localObject.length)
        {
          if ((localObject[i].isEmpty()) || (localObject[i].equalsIgnoreCase("ABSENT")) || (localObject[i].equalsIgnoreCase("NOT_READY"))) {
            break label179;
          }
          this.mSimCount += 1;
        }
      }
      catch (Exception localException)
      {
        Log.e("DataUsageDetailView", "Error to parse sim state");
        Log.d("DataUsageDetailView", "updateSimCount:mSimCount=" + this.mSimCount);
        if (this.mSimCount > 1)
        {
          this.mSettingsIntent.putExtra("select_tab", this.mSelectedDataSim);
          return;
        }
        this.mSettingsIntent.putExtra("select_tab", 0);
        return;
      }
      label179:
      i += 1;
    }
  }
  
  private void updateSimSelector()
  {
    if (this.mSimCount == 2)
    {
      this.mSimLayout.setVisibility(0);
      this.mSimSelector.setSelection(this.mSelectedDataSim);
      return;
    }
    this.mSimLayout.setVisibility(8);
  }
  
  public void bind(DataUsageController.DataUsageInfo paramDataUsageInfo)
  {
    Object localObject3 = this.mContext.getResources();
    Object localObject2 = null;
    Object localObject1 = paramDataUsageInfo;
    if (paramDataUsageInfo == null) {
      localObject1 = new DataUsageController.DataUsageInfo();
    }
    updateSimCount();
    int i;
    long l1;
    TextView localTextView2;
    TextView localTextView3;
    label113:
    DataUsageGraph localDataUsageGraph;
    TextView localTextView4;
    if (this.mSimCount == 0)
    {
      i = 2131690314;
      l1 = 0L;
      paramDataUsageInfo = null;
      TextView localTextView1 = (TextView)findViewById(16908310);
      localTextView1.setText(i);
      localTextView2 = (TextView)findViewById(2131951809);
      localTextView3 = (TextView)findViewById(2131951810);
      if (this.mSimCount != 0) {
        break label678;
      }
      localTextView2.setText("-");
      localTextView3.setText("");
      localDataUsageGraph = (DataUsageGraph)findViewById(2131951811);
      if ((((DataUsageController.DataUsageInfo)localObject1).limitLevel > 0L) || (((DataUsageController.DataUsageInfo)localObject1).warningLevel > 0L)) {
        break label709;
      }
      localDataUsageGraph.setVisibility(8);
      localTextView4 = (TextView)findViewById(2131951812);
      if (this.mSimCount <= 0) {
        break label781;
      }
      localTextView4.setText(((DataUsageController.DataUsageInfo)localObject1).carrier);
      label181:
      TextView localTextView5 = (TextView)findViewById(2131951814);
      localTextView5.setText(((DataUsageController.DataUsageInfo)localObject1).period);
      localObject1 = (TextView)findViewById(2131951813);
      if (paramDataUsageInfo == null) {
        break label792;
      }
      i = 0;
      label221:
      ((TextView)localObject1).setVisibility(i);
      ((TextView)localObject1).setText(paramDataUsageInfo);
      paramDataUsageInfo = (TextView)findViewById(2131951815);
      if (localObject2 == null) {
        break label798;
      }
      i = 0;
      label251:
      paramDataUsageInfo.setVisibility(i);
      paramDataUsageInfo.setText((CharSequence)localObject2);
      localObject2 = (TextView)findViewById(2131951807);
      localObject3 = ((Resources)localObject3).getStringArray(2131427603);
      localObject3 = new ArrayAdapter(this.mContext, ThemeColorUtils.getSpinnerLayout(), (String[])localObject3)
      {
        public View getDropDownView(int paramAnonymousInt, View paramAnonymousView, ViewGroup paramAnonymousViewGroup)
        {
          paramAnonymousView = super.getDropDownView(paramAnonymousInt, paramAnonymousView, paramAnonymousViewGroup);
          ((TextView)paramAnonymousView).setGravity(17);
          return paramAnonymousView;
        }
        
        public View getView(int paramAnonymousInt, View paramAnonymousView, ViewGroup paramAnonymousViewGroup)
        {
          paramAnonymousView = super.getView(paramAnonymousInt, paramAnonymousView, paramAnonymousViewGroup);
          ((TextView)paramAnonymousView).setTextSize(16.0F);
          return paramAnonymousView;
        }
      };
      this.mSimSelector.setAdapter((SpinnerAdapter)localObject3);
      this.mSelectedDataSim = getDefaultDataSimIndex();
      this.mSimSelector.setOnItemSelectedListener(new SimOnItemSelectedListener());
      updateSimSelector();
      ThemeColorUtils.getColor(ThemeColorUtils.QS_SYSTEM_PRIMARY);
      i = ThemeColorUtils.getColor(ThemeColorUtils.QS_PRIMARY_TEXT);
      int j = ThemeColorUtils.getColor(ThemeColorUtils.QS_SECONDARY_TEXT);
      int k = ThemeColorUtils.getColor(ThemeColorUtils.PROGRESS);
      int m = ThemeColorUtils.getColor(ThemeColorUtils.QS_TILE_ICON_DISABLE);
      int n = ThemeColorUtils.getColor(ThemeColorUtils.QS_POPUP_BACKGROUND);
      localTextView1.setTextColor(i);
      localTextView2.setTextColor(k);
      localTextView3.setTextColor(k);
      localDataUsageGraph.setCustomColor(m & 0x4CFFFFFF, k, i);
      localTextView4.setTextColor(i);
      localTextView5.setTextColor(j);
      ((TextView)localObject1).setTextColor(i);
      paramDataUsageInfo.setTextColor(i);
      ((TextView)localObject2).setTextColor(i);
      this.mSimSelector.setBackgroundTintList(ColorStateList.valueOf(j));
      paramDataUsageInfo = this.mContext.getResources().getDrawable(2130838374);
      paramDataUsageInfo.setTint(n);
      this.mSimSelector.setPopupBackgroundDrawable(paramDataUsageInfo);
      Log.d("DataUsageDetailView", "bind:call state=" + this.mTelephonyManager.getCallState());
      paramDataUsageInfo = this.mSimSelector;
      if (this.mTelephonyManager.getCallState() != 0) {
        break label804;
      }
    }
    label678:
    label709:
    label781:
    label792:
    label798:
    label804:
    for (boolean bool = true;; bool = false)
    {
      paramDataUsageInfo.setEnabled(bool);
      return;
      if (((DataUsageController.DataUsageInfo)localObject1).limitLevel > 0L)
      {
        i = 2131690315;
        l1 = ((DataUsageController.DataUsageInfo)localObject1).limitLevel - ((DataUsageController.DataUsageInfo)localObject1).usageLevel;
        paramDataUsageInfo = ((Resources)localObject3).getString(2131690317, new Object[] { formatBytes(((DataUsageController.DataUsageInfo)localObject1).usageLevel) });
        localObject2 = ((Resources)localObject3).getString(2131690318, new Object[] { formatBytes(((DataUsageController.DataUsageInfo)localObject1).limitLevel) });
        break;
      }
      i = 2131690314;
      l1 = ((DataUsageController.DataUsageInfo)localObject1).usageLevel;
      if (((DataUsageController.DataUsageInfo)localObject1).warningLevel > 0L)
      {
        paramDataUsageInfo = ((Resources)localObject3).getString(2131690319, new Object[] { formatBytes(((DataUsageController.DataUsageInfo)localObject1).warningLevel) });
        break;
      }
      paramDataUsageInfo = null;
      break;
      localTextView2.setText(formatBytes(l1, BYTES_TYPE_NUMBER));
      localTextView3.setText(formatBytes(l1, BYTES_TYPE_UNIT));
      break label113;
      localDataUsageGraph.setVisibility(0);
      l1 = ((DataUsageController.DataUsageInfo)localObject1).limitLevel;
      long l2 = ((DataUsageController.DataUsageInfo)localObject1).warningLevel;
      long l3 = ((DataUsageController.DataUsageInfo)localObject1).usageLevel;
      if ((((DataUsageController.DataUsageInfo)localObject1).warningLevel <= 0L) || (((DataUsageController.DataUsageInfo)localObject1).limitLevel <= 0L)) {}
      for (bool = true;; bool = false)
      {
        localDataUsageGraph.setLevels(l1, l2, l3, bool);
        break;
      }
      localTextView4.setText("");
      break label181;
      i = 8;
      break label221;
      i = 8;
      break label251;
    }
  }
  
  public void bindData()
  {
    new Thread()
    {
      public void run()
      {
        DataUsageDetailView.-wrap0(DataUsageDetailView.this);
      }
    }.start();
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (!this.mRegistered)
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
      this.mContext.registerReceiver(this.mSimReceiver, localIntentFilter);
      this.mTelephonyManager.listen(this.mPhoneStateListener, 32);
      this.mRegistered = true;
    }
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    FontSizeUtils.updateFontSize(this, 16908310, 2131755441);
    FontSizeUtils.updateFontSize(this, 2131951809, 2131755442);
    FontSizeUtils.updateFontSize(this, 2131951812, 2131755441);
    FontSizeUtils.updateFontSize(this, 2131951813, 2131755441);
    FontSizeUtils.updateFontSize(this, 2131951814, 2131755441);
    FontSizeUtils.updateFontSize(this, 2131951815, 2131755441);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.mRegistered)
    {
      this.mContext.unregisterReceiver(this.mSimReceiver);
      this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
      this.mRegistered = false;
    }
  }
  
  public void onFinishInflate()
  {
    super.onFinishInflate();
    this.mSimLayout = findViewById(2131951806);
    this.mSimSelector = ((Spinner)findViewById(2131951808));
  }
  
  public void setNetworkController(NetworkController paramNetworkController)
  {
    if (this.mBuilder == null) {
      this.mBuilder = new DataUsageBuilder(this.mContext, paramNetworkController);
    }
  }
  
  public static class DataUsageBuilder
  {
    private final StringBuilder PERIOD_BUILDER = new StringBuilder(50);
    private final Formatter PERIOD_FORMATTER = new Formatter(this.PERIOD_BUILDER, Locale.getDefault());
    private final String TAG = "DataUsageBuilder";
    private Context mContext;
    private NetworkController mDataController;
    
    public DataUsageBuilder(Context paramContext, NetworkController paramNetworkController)
    {
      this.mContext = paramContext;
      this.mDataController = paramNetworkController;
    }
    
    private String formatDateRange(long paramLong1, long paramLong2)
    {
      synchronized (this.PERIOD_BUILDER)
      {
        this.PERIOD_BUILDER.setLength(0);
        String str = DateUtils.formatDateRange(this.mContext, this.PERIOD_FORMATTER, paramLong1, paramLong2, 65552, null).toString();
        return str;
      }
    }
    
    public DataUsageController.DataUsageInfo buildOneplusDataUsage(int paramInt, boolean paramBoolean)
    {
      DataUsageController.DataUsageInfo localDataUsageInfo = new DataUsageController.DataUsageInfo();
      localDataUsageInfo.carrier = this.mDataController.getCarrierName();
      if (paramInt < 0) {
        return localDataUsageInfo;
      }
      Bundle localBundle = new Bundle();
      localBundle.putInt("oneplus_datausage_slotid", paramInt);
      localBundle.putBoolean("oneplus_datausage_cache", paramBoolean);
      try
      {
        localBundle = this.mContext.getContentResolver().call(Uri.parse("content://com.oneplus.security.database.SafeProvider"), "method_query_oneplus_datausage", null, localBundle);
        if (localBundle != null)
        {
          paramInt = localBundle.getInt("oneplus_datausage_error_code");
          int i = localBundle.getInt("oneplus_datausage_accountday");
          long l1 = localBundle.getLong("oneplus_datausage_time_start");
          long l2 = localBundle.getLong("oneplus_datausage_time_end");
          long l3 = localBundle.getLong("oneplus_datausage_total");
          long l4 = localBundle.getLong("oneplus_datausage_used");
          paramBoolean = localBundle.getBoolean("oneplus_datausage_warn_state");
          long l5 = localBundle.getLong("oneplus_datausage_warn_value");
          Log.d("DataUsageBuilder", "errorCode=" + paramInt + ",\n accountDay=" + i + ",\n startTime=" + l1 + ",\n endTime=" + l2 + ",\ntotal=" + l3 + ",\n used=" + l4 + ",\nwarnState=" + paramBoolean + ",\n warnValue=" + l5);
          localDataUsageInfo.limitLevel = l3;
          localDataUsageInfo.usageLevel = l4;
          localDataUsageInfo.warningLevel = l5;
          localDataUsageInfo.startDate = i;
          if ((l1 != 0L) && (l2 != 0L)) {
            localDataUsageInfo.period = formatDateRange(l1, l2);
          }
        }
        return localDataUsageInfo;
      }
      catch (Exception localException)
      {
        Log.e("DataUsageBuilder", "Error while fetch info from content://com.oneplus.security.database.SafeProvider", localException);
      }
      return localDataUsageInfo;
    }
  }
  
  class MyCallStateListener
    extends PhoneStateListener
  {
    MyCallStateListener() {}
    
    public void onCallStateChanged(int paramInt, String paramString)
    {
      boolean bool = false;
      if (DataUsageDetailView.-get2(DataUsageDetailView.this) != null)
      {
        paramString = DataUsageDetailView.-get2(DataUsageDetailView.this);
        if (DataUsageDetailView.-get3(DataUsageDetailView.this).getCallState() == 0) {
          bool = true;
        }
        paramString.setEnabled(bool);
        Log.d("DataUsageDetailView", "onCallStateChanged:state=" + DataUsageDetailView.-get3(DataUsageDetailView.this).getCallState());
      }
    }
  }
  
  public class SimOnItemSelectedListener
    implements AdapterView.OnItemSelectedListener
  {
    public SimOnItemSelectedListener() {}
    
    public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    {
      if (DataUsageDetailView.-get0(DataUsageDetailView.this) == paramInt) {
        return;
      }
      Log.d("DataUsageDetailView", "onItemSelected:pos=" + paramInt);
      DataUsageDetailView.-set0(DataUsageDetailView.this, paramInt);
      DataUsageDetailView.-wrap1(DataUsageDetailView.this, paramInt);
      DataUsageDetailView.this.bindData();
      DataUsageDetailView.-get1(DataUsageDetailView.this).putExtra("select_tab", DataUsageDetailView.-get0(DataUsageDetailView.this));
    }
    
    public void onNothingSelected(AdapterView paramAdapterView)
    {
      Log.d("DataUsageDetailView", "onNothingSelected");
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\DataUsageDetailView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */