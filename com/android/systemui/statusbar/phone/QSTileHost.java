package com.android.systemui.statusbar.phone;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.service.quicksettings.Tile;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.Host.Callback;
import com.android.systemui.qs.external.CustomTile;
import com.android.systemui.qs.external.TileLifecycleManager;
import com.android.systemui.qs.external.TileServices;
import com.android.systemui.qs.tiles.AirplaneModeTile;
import com.android.systemui.qs.tiles.BatteryTile;
import com.android.systemui.qs.tiles.BluetoothTile;
import com.android.systemui.qs.tiles.CastTile;
import com.android.systemui.qs.tiles.CellularTile;
import com.android.systemui.qs.tiles.ColorInversionTile;
import com.android.systemui.qs.tiles.DataSaverTile;
import com.android.systemui.qs.tiles.DndTile;
import com.android.systemui.qs.tiles.FlashlightTile;
import com.android.systemui.qs.tiles.GameModeTile;
import com.android.systemui.qs.tiles.HotspotTile;
import com.android.systemui.qs.tiles.IntentTile;
import com.android.systemui.qs.tiles.LocationTile;
import com.android.systemui.qs.tiles.NfcTile;
import com.android.systemui.qs.tiles.NightDisplayTile;
import com.android.systemui.qs.tiles.OtgTile;
import com.android.systemui.qs.tiles.ReadModeTile;
import com.android.systemui.qs.tiles.RotationLockTile;
import com.android.systemui.qs.tiles.UserTile;
import com.android.systemui.qs.tiles.VPNTile;
import com.android.systemui.qs.tiles.WifiTile;
import com.android.systemui.qs.tiles.WorkModeTile;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BluetoothController;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.FlashlightController;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.statusbar.policy.SecurityController;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerService.Tunable;
import com.android.systemui.util.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public class QSTileHost
  implements QSTile.Host, TunerService.Tunable
{
  private static final boolean DEBUG = Log.isLoggable("QSTileHost", 3);
  private final AutoTileManager mAutoTiles;
  private final BatteryController mBattery;
  private final BluetoothController mBluetooth;
  private final List<QSTile.Host.Callback> mCallbacks = new ArrayList();
  private final CastController mCast;
  private final Context mContext;
  private int mCurrentUser;
  private final FlashlightController mFlashlight;
  private View mHeader;
  private final HotspotController mHotspot;
  private final StatusBarIconController mIconController;
  private final KeyguardMonitor mKeyguard;
  private final LocationController mLocation;
  private final Looper mLooper;
  private final NetworkController mNetwork;
  private final NextAlarmController mNextAlarmController;
  private final ManagedProfileController mProfileController;
  private final RotationLockController mRotation;
  private final SecurityController mSecurity;
  private final TileServices mServices;
  private final PhoneStatusBar mStatusBar;
  protected final ArrayList<String> mTileSpecs = new ArrayList();
  private final LinkedHashMap<String, QSTile<?>> mTiles = new LinkedHashMap();
  private final UserInfoController mUserInfoController;
  private final UserSwitcherController mUserSwitcherController;
  private final ZenModeController mZen;
  
  public QSTileHost(Context paramContext, PhoneStatusBar paramPhoneStatusBar, BluetoothController paramBluetoothController, LocationController paramLocationController, RotationLockController paramRotationLockController, NetworkController paramNetworkController, ZenModeController paramZenModeController, HotspotController paramHotspotController, CastController paramCastController, FlashlightController paramFlashlightController, UserSwitcherController paramUserSwitcherController, UserInfoController paramUserInfoController, KeyguardMonitor paramKeyguardMonitor, SecurityController paramSecurityController, BatteryController paramBatteryController, StatusBarIconController paramStatusBarIconController, NextAlarmController paramNextAlarmController)
  {
    this.mContext = paramContext;
    this.mStatusBar = paramPhoneStatusBar;
    this.mBluetooth = paramBluetoothController;
    this.mLocation = paramLocationController;
    this.mRotation = paramRotationLockController;
    this.mNetwork = paramNetworkController;
    this.mZen = paramZenModeController;
    this.mHotspot = paramHotspotController;
    this.mCast = paramCastController;
    this.mFlashlight = paramFlashlightController;
    this.mUserSwitcherController = paramUserSwitcherController;
    this.mUserInfoController = paramUserInfoController;
    this.mKeyguard = paramKeyguardMonitor;
    this.mSecurity = paramSecurityController;
    this.mBattery = paramBatteryController;
    this.mIconController = paramStatusBarIconController;
    this.mNextAlarmController = paramNextAlarmController;
    this.mProfileController = new ManagedProfileController(this);
    paramPhoneStatusBar = new HandlerThread(QSTileHost.class.getSimpleName(), 10);
    paramPhoneStatusBar.start();
    this.mLooper = paramPhoneStatusBar.getLooper();
    this.mServices = new TileServices(this, this.mLooper);
    TunerService.get(this.mContext).addTunable(this, new String[] { "sysui_qs_tiles" });
    this.mAutoTiles = new AutoTileManager(paramContext, this);
  }
  
  public void addCallback(QSTile.Host.Callback paramCallback)
  {
    this.mCallbacks.add(paramCallback);
  }
  
  public void addTile(ComponentName paramComponentName)
  {
    ArrayList localArrayList = new ArrayList(this.mTileSpecs);
    localArrayList.add(0, CustomTile.toSpec(paramComponentName));
    changeTiles(this.mTileSpecs, localArrayList);
  }
  
  public void addTile(String paramString)
  {
    Object localObject = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "sysui_qs_tiles", ActivityManager.getCurrentUser());
    localObject = loadTileSpecs(this.mContext, (String)localObject);
    if (((List)localObject).contains(paramString)) {
      return;
    }
    ((List)localObject).add(paramString);
    Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "sysui_qs_tiles", TextUtils.join(",", (Iterable)localObject), ActivityManager.getCurrentUser());
  }
  
  public void animateToggleQSExpansion()
  {
    this.mHeader.callOnClick();
  }
  
  public void changeTiles(List<String> paramList1, List<String> paramList2)
  {
    int j = paramList1.size();
    paramList2.size();
    int i = 0;
    if (i < j)
    {
      Object localObject1 = (String)paramList1.get(i);
      if (!((String)localObject1).startsWith("custom(")) {}
      for (;;)
      {
        i += 1;
        break;
        if (!paramList2.contains(localObject1))
        {
          localObject1 = CustomTile.getComponentFromSpec((String)localObject1);
          Object localObject2 = new Intent().setComponent((ComponentName)localObject1);
          localObject2 = new TileLifecycleManager(new Handler(), this.mContext, this.mServices, new Tile(), (Intent)localObject2, new UserHandle(ActivityManager.getCurrentUser()));
          ((TileLifecycleManager)localObject2).onStopListening();
          ((TileLifecycleManager)localObject2).onTileRemoved();
          TileLifecycleManager.setTileAdded(this.mContext, (ComponentName)localObject1, false);
          ((TileLifecycleManager)localObject2).flushMessagesAndUnbind();
        }
      }
    }
    boolean bool = DEBUG;
    Log.d("QSTileHost", "saveCurrentTiles " + paramList2);
    Settings.Secure.putStringForUser(getContext().getContentResolver(), "sysui_qs_tiles", TextUtils.join(",", paramList2), ActivityManager.getCurrentUser());
  }
  
  public void collapsePanels()
  {
    this.mStatusBar.postAnimateCollapsePanels();
  }
  
  public QSTile<?> createTile(String paramString)
  {
    if (paramString.equals("wifi")) {
      return new WifiTile(this);
    }
    if (paramString.equals("bt")) {
      return new BluetoothTile(this);
    }
    if (paramString.equals("cell")) {
      return new CellularTile(this);
    }
    if (paramString.equals("dnd")) {
      return new DndTile(this);
    }
    if (paramString.equals("inversion")) {
      return new ColorInversionTile(this);
    }
    if (paramString.equals("airplane")) {
      return new AirplaneModeTile(this);
    }
    if (paramString.equals("work")) {
      return new WorkModeTile(this);
    }
    if (paramString.equals("rotation")) {
      return new RotationLockTile(this);
    }
    if (paramString.equals("flashlight")) {
      return new FlashlightTile(this);
    }
    if (paramString.equals("location")) {
      return new LocationTile(this);
    }
    if (paramString.equals("cast")) {
      return new CastTile(this);
    }
    if (paramString.equals("hotspot")) {
      return new HotspotTile(this);
    }
    if (paramString.equals("user")) {
      return new UserTile(this);
    }
    if (paramString.equals("battery")) {
      return new BatteryTile(this);
    }
    if (paramString.equals("powersaving")) {
      return new BatteryTile(this);
    }
    if (paramString.equals("saver")) {
      return new DataSaverTile(this);
    }
    if (paramString.equals("night")) {
      return new NightDisplayTile(this);
    }
    if (paramString.equals("vpn")) {
      return new VPNTile(this);
    }
    if (paramString.equals("nfc")) {
      return new NfcTile(this);
    }
    if (paramString.equals("game")) {
      return new GameModeTile(this);
    }
    if (paramString.equals("read")) {
      return new ReadModeTile(this);
    }
    if (paramString.equals("otg")) {
      return new OtgTile(this);
    }
    if (paramString.startsWith("intent(")) {
      return IntentTile.create(this, paramString);
    }
    if (paramString.startsWith("custom(")) {
      return CustomTile.create(this, paramString);
    }
    Log.w("QSTileHost", "Bad tile spec: " + paramString);
    return null;
  }
  
  public void destroy()
  {
    this.mAutoTiles.destroy();
    TunerService.get(this.mContext).removeTunable(this);
  }
  
  public BatteryController getBatteryController()
  {
    return this.mBattery;
  }
  
  public BluetoothController getBluetoothController()
  {
    return this.mBluetooth;
  }
  
  public CastController getCastController()
  {
    return this.mCast;
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  public FlashlightController getFlashlightController()
  {
    return this.mFlashlight;
  }
  
  public HotspotController getHotspotController()
  {
    return this.mHotspot;
  }
  
  public StatusBarIconController getIconController()
  {
    return this.mIconController;
  }
  
  public KeyguardMonitor getKeyguardMonitor()
  {
    return this.mKeyguard;
  }
  
  public LocationController getLocationController()
  {
    return this.mLocation;
  }
  
  public Looper getLooper()
  {
    return this.mLooper;
  }
  
  public ManagedProfileController getManagedProfileController()
  {
    return this.mProfileController;
  }
  
  public NetworkController getNetworkController()
  {
    return this.mNetwork;
  }
  
  public NextAlarmController getNextAlarmController()
  {
    return this.mNextAlarmController;
  }
  
  public PhoneStatusBar getPhoneStatusBar()
  {
    return this.mStatusBar;
  }
  
  public RotationLockController getRotationLockController()
  {
    return this.mRotation;
  }
  
  public SecurityController getSecurityController()
  {
    return this.mSecurity;
  }
  
  public TileServices getTileServices()
  {
    return this.mServices;
  }
  
  public Collection<QSTile<?>> getTiles()
  {
    return this.mTiles.values();
  }
  
  public UserInfoController getUserInfoController()
  {
    return this.mUserInfoController;
  }
  
  public UserSwitcherController getUserSwitcherController()
  {
    return this.mUserSwitcherController;
  }
  
  public ZenModeController getZenModeController()
  {
    return this.mZen;
  }
  
  protected List<String> loadTileSpecs(Context paramContext, String paramString)
  {
    int j = 0;
    paramContext = paramContext.getResources();
    String str1 = paramContext.getString(2131689907);
    int k;
    int i;
    label100:
    Object localObject;
    if (Utils.isCurrentGuest(getContext()))
    {
      paramString = paramContext.getString(2131689928);
      paramContext = paramString;
      if (DEBUG)
      {
        Log.d("QSTileHost", "Loaded tile specs of guest from config: " + paramString);
        paramContext = paramString;
      }
      paramString = new ArrayList();
      k = 0;
      paramContext = paramContext.split(",");
      int m = paramContext.length;
      i = j;
      j = k;
      if (i >= m) {
        return paramString;
      }
      localObject = paramContext[i].trim();
      if (!((String)localObject).isEmpty()) {
        break label310;
      }
      k = j;
    }
    for (;;)
    {
      i += 1;
      j = k;
      break label100;
      if (paramString == null)
      {
        paramString = paramContext.getString(2131689909);
        paramContext = paramString;
        if (!DEBUG) {
          break;
        }
        Log.d("QSTileHost", "Loaded tile specs from config: " + paramString);
        paramContext = paramString;
        break;
      }
      localObject = paramContext.getString(2131689909).split(",");
      k = localObject.length;
      i = 0;
      if (i < k)
      {
        String str2 = localObject[i].trim();
        if (str2.isEmpty()) {
          paramContext = paramString;
        }
        for (;;)
        {
          i += 1;
          paramString = paramContext;
          break;
          paramContext = paramString;
          if (str2.startsWith("intent(")) {
            paramContext = paramString.concat(",").concat(str2);
          }
        }
      }
      paramContext = paramString;
      if (!DEBUG) {
        break;
      }
      Log.d("QSTileHost", "Loaded tile specs from setting: " + paramString);
      paramContext = paramString;
      break;
      label310:
      if (((String)localObject).equals("default"))
      {
        k = j;
        if (j == 0)
        {
          paramString.addAll(Arrays.asList(str1.split(",")));
          k = 1;
        }
      }
      else
      {
        paramString.add(localObject);
        k = j;
      }
    }
    return paramString;
  }
  
  public void onTuningChanged(String paramString1, String paramString2)
  {
    if (!"sysui_qs_tiles".equals(paramString1)) {
      return;
    }
    if (DEBUG) {
      Log.d("QSTileHost", "Recreating tiles");
    }
    paramString1 = paramString2;
    if (paramString2 == null)
    {
      paramString1 = paramString2;
      if (UserManager.isDeviceInDemoMode(this.mContext)) {
        paramString1 = this.mContext.getResources().getString(2131689910);
      }
    }
    paramString1 = loadTileSpecs(this.mContext, paramString1);
    int i = ActivityManager.getCurrentUser();
    if ((paramString1.equals(this.mTileSpecs)) && (i == this.mCurrentUser)) {
      return;
    }
    paramString2 = this.mTiles.entrySet().iterator();
    while (paramString2.hasNext())
    {
      localObject = (Map.Entry)paramString2.next();
      if (!paramString1.contains(((Map.Entry)localObject).getKey()))
      {
        if (DEBUG) {
          Log.d("QSTileHost", "Destroying tile: " + (String)((Map.Entry)localObject).getKey());
        }
        ((QSTile)((Map.Entry)localObject).getValue()).destroy();
      }
    }
    paramString2 = new LinkedHashMap();
    Object localObject = paramString1.iterator();
    while (((Iterator)localObject).hasNext())
    {
      String str = (String)((Iterator)localObject).next();
      QSTile localQSTile = (QSTile)this.mTiles.get(str);
      if ((localQSTile != null) && ((!(localQSTile instanceof CustomTile)) || (((CustomTile)localQSTile).getUser() == i)))
      {
        if (localQSTile.isAvailable())
        {
          if (DEBUG) {
            Log.d("QSTileHost", "Adding " + localQSTile);
          }
          localQSTile.removeCallbacks();
          if ((!(localQSTile instanceof CustomTile)) && (this.mCurrentUser != i)) {
            localQSTile.userSwitch(i);
          }
          paramString2.put(str, localQSTile);
        }
        else
        {
          localQSTile.destroy();
        }
      }
      else
      {
        if (DEBUG) {
          Log.d("QSTileHost", "Creating tile: " + str);
        }
        try
        {
          localQSTile = createTile(str);
          if (localQSTile == null) {
            continue;
          }
          if (!localQSTile.isAvailable()) {
            break label471;
          }
          localQSTile.setTileSpec(str);
          paramString2.put(str, localQSTile);
        }
        catch (Throwable localThrowable)
        {
          Log.w("QSTileHost", "Error creating tile for spec: " + str, localThrowable);
        }
        continue;
        label471:
        localThrowable.destroy();
      }
    }
    this.mCurrentUser = i;
    this.mTileSpecs.clear();
    this.mTileSpecs.addAll(paramString1);
    this.mTiles.clear();
    this.mTiles.putAll(paramString2);
    i = 0;
    while (i < this.mCallbacks.size())
    {
      ((QSTile.Host.Callback)this.mCallbacks.get(i)).onTilesChanged();
      i += 1;
    }
  }
  
  public void removeCallback(QSTile.Host.Callback paramCallback)
  {
    this.mCallbacks.remove(paramCallback);
  }
  
  public void removeTile(ComponentName paramComponentName)
  {
    ArrayList localArrayList = new ArrayList(this.mTileSpecs);
    localArrayList.remove(CustomTile.toSpec(paramComponentName));
    changeTiles(this.mTileSpecs, localArrayList);
  }
  
  public void removeTile(String paramString)
  {
    ArrayList localArrayList = new ArrayList(this.mTileSpecs);
    localArrayList.remove(paramString);
    Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "sysui_qs_tiles", TextUtils.join(",", localArrayList), ActivityManager.getCurrentUser());
  }
  
  public void setHeaderView(View paramView)
  {
    this.mHeader = paramView;
  }
  
  public void startActivityDismissingKeyguard(PendingIntent paramPendingIntent)
  {
    this.mStatusBar.postStartActivityDismissingKeyguard(paramPendingIntent);
  }
  
  public void startActivityDismissingKeyguard(Intent paramIntent)
  {
    this.mStatusBar.postStartActivityDismissingKeyguard(paramIntent, 0);
  }
  
  public void startRunnableDismissingKeyguard(Runnable paramRunnable)
  {
    this.mStatusBar.postQSRunnableDismissingKeyguard(paramRunnable);
  }
  
  public void warn(String paramString, Throwable paramThrowable) {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\QSTileHost.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */