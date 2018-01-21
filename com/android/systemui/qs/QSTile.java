package com.android.systemui.qs;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.systemui.qs.external.TileServices;
import com.android.systemui.statusbar.phone.ManagedProfileController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BluetoothController;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.FlashlightController;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.util.MdmLogger;
import java.util.ArrayList;
import java.util.Objects;

public abstract class QSTile<TState extends State>
{
  protected static final boolean DEBUG = Log.isLoggable("Tile", 3);
  protected final String TAG = "Tile." + getClass().getSimpleName();
  private boolean mAnnounceNextStateChange;
  private final ArrayList<Callback> mCallbacks = new ArrayList();
  protected final Context mContext;
  protected final QSTile<TState>.H mHandler;
  protected final Host mHost;
  private final ArraySet<Object> mListeners = new ArraySet();
  protected TState mState = newTileState();
  private String mTileSpec;
  private TState mTmpState = newTileState();
  protected final Handler mUiHandler = new Handler(Looper.getMainLooper());
  
  protected QSTile(Host paramHost)
  {
    this.mHost = paramHost;
    this.mContext = paramHost.getContext();
    this.mHandler = new H(paramHost.getLooper(), null);
  }
  
  private void handleAddCallback(Callback paramCallback)
  {
    this.mCallbacks.add(paramCallback);
    paramCallback.onStateChanged(this.mState);
  }
  
  private void handleRemoveCallback(Callback paramCallback)
  {
    this.mCallbacks.remove(paramCallback);
  }
  
  private void handleRemoveCallbacks()
  {
    this.mCallbacks.clear();
  }
  
  private void handleScanStateChanged(boolean paramBoolean)
  {
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      ((Callback)this.mCallbacks.get(i)).onScanStateChanged(paramBoolean);
      i += 1;
    }
  }
  
  private void handleShowDetail(boolean paramBoolean)
  {
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      ((Callback)this.mCallbacks.get(i)).onShowDetail(paramBoolean);
      i += 1;
    }
  }
  
  private void handleStateChanged()
  {
    boolean bool = shouldAnnouncementBeDelayed();
    if (this.mCallbacks.size() != 0)
    {
      int i = 0;
      while (i < this.mCallbacks.size())
      {
        ((Callback)this.mCallbacks.get(i)).onStateChanged(this.mState);
        i += 1;
      }
      if ((this.mAnnounceNextStateChange) && (!bool)) {}
    }
    else
    {
      if (!this.mAnnounceNextStateChange) {
        break label108;
      }
    }
    for (;;)
    {
      this.mAnnounceNextStateChange = bool;
      return;
      String str = composeChangeAnnouncement();
      if (str == null) {
        break;
      }
      ((Callback)this.mCallbacks.get(0)).onAnnouncementRequested(str);
      break;
      label108:
      bool = false;
    }
  }
  
  private void handleToggleStateChanged(boolean paramBoolean)
  {
    int i = 0;
    while (i < this.mCallbacks.size())
    {
      ((Callback)this.mCallbacks.get(i)).onToggleStateChanged(paramBoolean);
      i += 1;
    }
  }
  
  public void addCallback(Callback paramCallback)
  {
    this.mHandler.obtainMessage(1, paramCallback).sendToTarget();
  }
  
  protected void checkIfRestrictionEnforcedByAdminOnly(State paramState, String paramString)
  {
    RestrictedLockUtils.EnforcedAdmin localEnforcedAdmin = RestrictedLockUtils.checkIfRestrictionEnforced(this.mContext, paramString, ActivityManager.getCurrentUser());
    if ((localEnforcedAdmin == null) || (RestrictedLockUtils.hasBaseUserRestriction(this.mContext, paramString, ActivityManager.getCurrentUser())))
    {
      paramState.disabledByPolicy = false;
      paramState.enforcedAdmin = null;
      return;
    }
    paramState.disabledByPolicy = true;
    paramState.enforcedAdmin = localEnforcedAdmin;
  }
  
  public final void clearState()
  {
    this.mHandler.sendEmptyMessage(11);
  }
  
  public void click()
  {
    this.mHandler.sendEmptyMessage(2);
  }
  
  protected String composeChangeAnnouncement()
  {
    return null;
  }
  
  public QSIconView createTileView(Context paramContext)
  {
    return new QSIconView(paramContext);
  }
  
  public void destroy()
  {
    this.mHandler.sendEmptyMessage(10);
  }
  
  public void fireScanStateChanged(boolean paramBoolean)
  {
    H localH = this.mHandler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localH.obtainMessage(9, i, 0).sendToTarget();
      return;
    }
  }
  
  public void fireToggleStateChanged(boolean paramBoolean)
  {
    H localH = this.mHandler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localH.obtainMessage(8, i, 0).sendToTarget();
      return;
    }
  }
  
  public DetailAdapter getDetailAdapter()
  {
    return null;
  }
  
  public Host getHost()
  {
    return this.mHost;
  }
  
  public abstract Intent getLongClickIntent();
  
  public abstract int getMetricsCategory();
  
  public TState getState()
  {
    return this.mState;
  }
  
  public abstract CharSequence getTileLabel();
  
  public String getTileSpec()
  {
    return this.mTileSpec;
  }
  
  protected void handleClearState()
  {
    this.mTmpState = newTileState();
    this.mState = newTileState();
  }
  
  protected abstract void handleClick();
  
  protected void handleDestroy()
  {
    setListening(false);
    this.mCallbacks.clear();
  }
  
  protected void handleLongClick()
  {
    MetricsLogger.action(this.mContext, 366, getTileSpec());
    this.mHost.startActivityDismissingKeyguard(getLongClickIntent());
  }
  
  protected void handleRefreshState(Object paramObject)
  {
    handleUpdateState(this.mTmpState, paramObject);
    if (this.mTmpState.copyTo(this.mState)) {
      handleStateChanged();
    }
  }
  
  protected void handleSecondaryClick()
  {
    handleClick();
  }
  
  protected abstract void handleUpdateState(TState paramTState, Object paramObject);
  
  protected void handleUserSwitch(int paramInt)
  {
    handleRefreshState(null);
  }
  
  public boolean isAvailable()
  {
    return true;
  }
  
  public void longClick()
  {
    this.mHandler.sendEmptyMessage(4);
  }
  
  public abstract TState newTileState();
  
  public final void refreshState()
  {
    refreshState(null);
  }
  
  protected final void refreshState(Object paramObject)
  {
    this.mHandler.obtainMessage(5, paramObject).sendToTarget();
  }
  
  public void removeCallback(Callback paramCallback)
  {
    this.mHandler.obtainMessage(13, paramCallback).sendToTarget();
  }
  
  public void removeCallbacks()
  {
    this.mHandler.sendEmptyMessage(12);
  }
  
  public void secondaryClick()
  {
    this.mHandler.sendEmptyMessage(3);
  }
  
  public void setDetailListening(boolean paramBoolean) {}
  
  public void setListening(Object paramObject, boolean paramBoolean)
  {
    if (paramBoolean) {
      if ((this.mListeners.add(paramObject)) && (this.mListeners.size() == 1))
      {
        if (DEBUG) {
          Log.d(this.TAG, "setListening true");
        }
        this.mHandler.obtainMessage(14, 1, 0).sendToTarget();
      }
    }
    while ((!this.mListeners.remove(paramObject)) || (this.mListeners.size() != 0)) {
      return;
    }
    if (DEBUG) {
      Log.d(this.TAG, "setListening false");
    }
    this.mHandler.obtainMessage(14, 0, 0).sendToTarget();
  }
  
  protected abstract void setListening(boolean paramBoolean);
  
  public void setTileSpec(String paramString)
  {
    this.mTileSpec = paramString;
  }
  
  protected boolean shouldAnnouncementBeDelayed()
  {
    return false;
  }
  
  public void showDetail(boolean paramBoolean)
  {
    H localH = this.mHandler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localH.obtainMessage(6, i, 0).sendToTarget();
      return;
    }
  }
  
  public void userSwitch(int paramInt)
  {
    this.mHandler.obtainMessage(7, paramInt, 0).sendToTarget();
  }
  
  public static class AirplaneBooleanState
    extends QSTile.BooleanState
  {
    public boolean isAirplaneMode;
    
    public boolean copyTo(QSTile.State paramState)
    {
      AirplaneBooleanState localAirplaneBooleanState = (AirplaneBooleanState)paramState;
      if ((super.copyTo(paramState)) || (localAirplaneBooleanState.isAirplaneMode != this.isAirplaneMode)) {}
      for (boolean bool = true;; bool = false)
      {
        localAirplaneBooleanState.isAirplaneMode = this.isAirplaneMode;
        return bool;
      }
    }
  }
  
  protected class AnimationIcon
    extends QSTile.ResourceIcon
  {
    private final int mAnimatedResId;
    
    public AnimationIcon(int paramInt1, int paramInt2)
    {
      super(null);
      this.mAnimatedResId = paramInt1;
    }
    
    public Drawable getDrawable(Context paramContext)
    {
      return paramContext.getDrawable(this.mAnimatedResId).getConstantState().newDrawable();
    }
  }
  
  public static class BooleanState
    extends QSTile.State
  {
    public boolean value;
    
    public boolean copyTo(QSTile.State paramState)
    {
      BooleanState localBooleanState = (BooleanState)paramState;
      if ((super.copyTo(paramState)) || (localBooleanState.value != this.value)) {}
      for (boolean bool = true;; bool = false)
      {
        localBooleanState.value = this.value;
        return bool;
      }
    }
    
    protected StringBuilder toStringBuilder()
    {
      StringBuilder localStringBuilder = super.toStringBuilder();
      localStringBuilder.insert(localStringBuilder.length() - 1, ",value=" + this.value);
      return localStringBuilder;
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void onAnnouncementRequested(CharSequence paramCharSequence);
    
    public abstract void onScanStateChanged(boolean paramBoolean);
    
    public abstract void onShowDetail(boolean paramBoolean);
    
    public abstract void onStateChanged(QSTile.State paramState);
    
    public abstract void onToggleStateChanged(boolean paramBoolean);
  }
  
  public static abstract interface DetailAdapter
  {
    public abstract View createDetailView(Context paramContext, View paramView, ViewGroup paramViewGroup);
    
    public abstract int getMetricsCategory();
    
    public abstract Intent getSettingsIntent();
    
    public abstract CharSequence getTitle();
    
    public boolean getToggleEnabled()
    {
      return true;
    }
    
    public abstract Boolean getToggleState();
    
    public abstract void setToggleState(boolean paramBoolean);
  }
  
  public static class DrawableIcon
    extends QSTile.Icon
  {
    protected final Drawable mDrawable;
    
    public DrawableIcon(Drawable paramDrawable)
    {
      this.mDrawable = paramDrawable;
    }
    
    public Drawable getDrawable(Context paramContext)
    {
      return this.mDrawable;
    }
    
    public Drawable getInvisibleDrawable(Context paramContext)
    {
      return this.mDrawable;
    }
  }
  
  protected final class H
    extends Handler
  {
    private H(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool2 = true;
      boolean bool3 = true;
      boolean bool4 = true;
      boolean bool1 = true;
      Object localObject2 = null;
      long l1 = SystemClock.elapsedRealtime();
      Object localObject1 = localObject2;
      long l2;
      label116:
      QSTile localQSTile;
      for (;;)
      {
        try
        {
          if (paramMessage.what != 1) {
            continue;
          }
          localObject2 = "handleAddCallback";
          localObject1 = localObject2;
          QSTile.-wrap0(QSTile.this, (QSTile.Callback)paramMessage.obj);
          localObject1 = localObject2;
        }
        catch (Throwable paramMessage)
        {
          localObject2 = "Error in " + (String)localObject1;
          Log.w(QSTile.this.TAG, (String)localObject2, paramMessage);
          QSTile.this.mHost.warn((String)localObject2, paramMessage);
          continue;
          localObject1 = localObject2;
          if (paramMessage.what != 13) {
            continue;
          }
          localObject2 = "handleRemoveCallback";
          localObject1 = localObject2;
          QSTile.-wrap1(QSTile.this, (QSTile.Callback)paramMessage.obj);
          localObject1 = localObject2;
          continue;
          localObject1 = localObject2;
          if (paramMessage.what != 2) {
            continue;
          }
          paramMessage = "handleClick";
          localObject1 = paramMessage;
          MdmLogger.logQsTile(QSTile.this.TAG, "full", "1");
          localObject1 = paramMessage;
          if (!QSTile.this.mState.disabledByPolicy) {
            continue;
          }
          localObject1 = paramMessage;
          localObject2 = RestrictedLockUtils.getShowAdminSupportDetailsIntent(QSTile.this.mContext, QSTile.this.mState.enforcedAdmin);
          localObject1 = paramMessage;
          QSTile.this.mHost.startActivityDismissingKeyguard((Intent)localObject2);
          localObject1 = paramMessage;
          continue;
          localObject1 = paramMessage;
          QSTile.-set0(QSTile.this, true);
          localObject1 = paramMessage;
          QSTile.this.handleClick();
          localObject1 = paramMessage;
          continue;
          localObject1 = localObject2;
          if (paramMessage.what != 3) {
            continue;
          }
          paramMessage = "handleSecondaryClick";
          localObject1 = paramMessage;
          MdmLogger.logQsTile(QSTile.this.TAG, "half", "1");
          localObject1 = paramMessage;
          QSTile.this.handleSecondaryClick();
          localObject1 = paramMessage;
          continue;
          localObject1 = localObject2;
          if (paramMessage.what != 4) {
            continue;
          }
          paramMessage = "handleLongClick";
          localObject1 = paramMessage;
          MdmLogger.logQsTile(QSTile.this.TAG, "long", "1");
          localObject1 = paramMessage;
          QSTile.this.handleLongClick();
          localObject1 = paramMessage;
          continue;
          localObject1 = localObject2;
          if (paramMessage.what != 5) {
            continue;
          }
          localObject2 = "handleRefreshState";
          localObject1 = localObject2;
          QSTile.this.handleRefreshState(paramMessage.obj);
          localObject1 = localObject2;
          continue;
          localObject1 = localObject2;
          if (paramMessage.what != 6) {
            break label559;
          }
        }
        l2 = SystemClock.elapsedRealtime();
        if (!Build.DEBUG_ONEPLUS) {
          break label869;
        }
        Log.d(QSTile.this.TAG, "Time cost in handleMessage: name=" + (String)localObject1 + ", time=" + (l2 - l1) + " ms");
        return;
        localObject1 = localObject2;
        if (paramMessage.what == 12)
        {
          paramMessage = "handleRemoveCallbacks";
          localObject1 = paramMessage;
          QSTile.-wrap2(QSTile.this);
          localObject1 = paramMessage;
        }
        else
        {
          localObject2 = "handleShowDetail";
          localObject1 = localObject2;
          localQSTile = QSTile.this;
          localObject1 = localObject2;
          if (paramMessage.arg1 == 0) {
            break label931;
          }
          label542:
          localObject1 = localObject2;
          QSTile.-wrap4(localQSTile, bool1);
          localObject1 = localObject2;
          continue;
          label559:
          localObject1 = localObject2;
          if (paramMessage.what == 7)
          {
            localObject2 = "handleUserSwitch";
            localObject1 = localObject2;
            QSTile.this.handleUserSwitch(paramMessage.arg1);
            localObject1 = localObject2;
          }
          else
          {
            localObject1 = localObject2;
            if (paramMessage.what == 8)
            {
              localObject2 = "handleToggleStateChanged";
              localObject1 = localObject2;
              localQSTile = QSTile.this;
              localObject1 = localObject2;
              if (paramMessage.arg1 == 0) {
                break label936;
              }
              bool1 = bool2;
              label638:
              localObject1 = localObject2;
              QSTile.-wrap5(localQSTile, bool1);
              localObject1 = localObject2;
            }
            else
            {
              localObject1 = localObject2;
              if (paramMessage.what == 9)
              {
                localObject2 = "handleScanStateChanged";
                localObject1 = localObject2;
                localQSTile = QSTile.this;
                localObject1 = localObject2;
                if (paramMessage.arg1 == 0) {
                  break label941;
                }
                bool1 = bool3;
                label696:
                localObject1 = localObject2;
                QSTile.-wrap3(localQSTile, bool1);
                localObject1 = localObject2;
              }
              else
              {
                localObject1 = localObject2;
                if (paramMessage.what == 10)
                {
                  paramMessage = "handleDestroy";
                  localObject1 = paramMessage;
                  QSTile.this.handleDestroy();
                  localObject1 = paramMessage;
                }
                else
                {
                  localObject1 = localObject2;
                  if (paramMessage.what != 11) {
                    break;
                  }
                  paramMessage = "handleClearState";
                  localObject1 = paramMessage;
                  QSTile.this.handleClearState();
                  localObject1 = paramMessage;
                }
              }
            }
          }
        }
      }
      localObject1 = localObject2;
      if (paramMessage.what == 14)
      {
        localObject2 = "setListening";
        localObject1 = localObject2;
        localQSTile = QSTile.this;
        localObject1 = localObject2;
        if (paramMessage.arg1 == 0) {
          break label946;
        }
      }
      label869:
      label931:
      label936:
      label941:
      label946:
      for (bool1 = bool4;; bool1 = false)
      {
        localObject1 = localObject2;
        localQSTile.setListening(bool1);
        localObject1 = localObject2;
        break;
        localObject1 = localObject2;
        throw new IllegalArgumentException("Unknown msg: " + paramMessage.what);
        if (l2 - l1 < 1000L) {
          break label116;
        }
        Log.d(QSTile.this.TAG, "Time cost in handleMessage: name=" + (String)localObject1 + ", time=" + (l2 - l1) + " ms");
        return;
        bool1 = false;
        break label542;
        bool1 = false;
        break label638;
        bool1 = false;
        break label696;
      }
    }
  }
  
  public static abstract interface Host
  {
    public abstract void collapsePanels();
    
    public abstract BatteryController getBatteryController();
    
    public abstract BluetoothController getBluetoothController();
    
    public abstract CastController getCastController();
    
    public abstract Context getContext();
    
    public abstract FlashlightController getFlashlightController();
    
    public abstract HotspotController getHotspotController();
    
    public abstract KeyguardMonitor getKeyguardMonitor();
    
    public abstract LocationController getLocationController();
    
    public abstract Looper getLooper();
    
    public abstract ManagedProfileController getManagedProfileController();
    
    public abstract NetworkController getNetworkController();
    
    public abstract RotationLockController getRotationLockController();
    
    public abstract TileServices getTileServices();
    
    public abstract UserInfoController getUserInfoController();
    
    public abstract UserSwitcherController getUserSwitcherController();
    
    public abstract ZenModeController getZenModeController();
    
    public abstract void removeTile(String paramString);
    
    public abstract void startActivityDismissingKeyguard(PendingIntent paramPendingIntent);
    
    public abstract void startActivityDismissingKeyguard(Intent paramIntent);
    
    public abstract void startRunnableDismissingKeyguard(Runnable paramRunnable);
    
    public abstract void warn(String paramString, Throwable paramThrowable);
    
    public static abstract interface Callback
    {
      public abstract void onTilesChanged();
    }
  }
  
  public static abstract class Icon
  {
    public abstract Drawable getDrawable(Context paramContext);
    
    public Drawable getInvisibleDrawable(Context paramContext)
    {
      return getDrawable(paramContext);
    }
    
    public int getPadding()
    {
      return 0;
    }
    
    public int hashCode()
    {
      return Icon.class.hashCode();
    }
  }
  
  public static class ResourceIcon
    extends QSTile.Icon
  {
    private static final SparseArray<QSTile.Icon> ICONS = new SparseArray();
    protected final int mResId;
    
    private ResourceIcon(int paramInt)
    {
      this.mResId = paramInt;
    }
    
    public static QSTile.Icon get(int paramInt)
    {
      QSTile.Icon localIcon = (QSTile.Icon)ICONS.get(paramInt);
      Object localObject = localIcon;
      if (localIcon == null)
      {
        localObject = new ResourceIcon(paramInt);
        ICONS.put(paramInt, localObject);
      }
      return (QSTile.Icon)localObject;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if ((paramObject instanceof ResourceIcon))
      {
        bool1 = bool2;
        if (((ResourceIcon)paramObject).mResId == this.mResId) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public Drawable getDrawable(Context paramContext)
    {
      return paramContext.getDrawable(this.mResId);
    }
    
    public Drawable getInvisibleDrawable(Context paramContext)
    {
      return paramContext.getDrawable(this.mResId);
    }
    
    public String toString()
    {
      return String.format("ResourceIcon[resId=0x%08x]", new Object[] { Integer.valueOf(this.mResId) });
    }
  }
  
  public static final class SignalState
    extends QSTile.BooleanState
  {
    public boolean activityIn;
    public boolean activityOut;
    public boolean colored;
    public boolean connected;
    public boolean filter;
    public boolean isOverlayIconWide;
    public boolean isShowRoaming;
    public int overlayIconId;
    public int subId;
    
    public boolean copyTo(QSTile.State paramState)
    {
      SignalState localSignalState = (SignalState)paramState;
      boolean bool;
      if ((localSignalState.connected != this.connected) || (localSignalState.activityIn != this.activityIn)) {
        bool = true;
      }
      for (;;)
      {
        localSignalState.connected = this.connected;
        localSignalState.activityIn = this.activityIn;
        localSignalState.activityOut = this.activityOut;
        localSignalState.overlayIconId = this.overlayIconId;
        localSignalState.filter = this.filter;
        localSignalState.isOverlayIconWide = this.isOverlayIconWide;
        localSignalState.isShowRoaming = this.isShowRoaming;
        localSignalState.subId = this.subId;
        localSignalState.colored = this.colored;
        if (super.copyTo(paramState)) {
          break label187;
        }
        return bool;
        if ((localSignalState.activityOut != this.activityOut) || (localSignalState.overlayIconId != this.overlayIconId) || (localSignalState.isOverlayIconWide != this.isOverlayIconWide) || (localSignalState.isShowRoaming != this.isShowRoaming) || (localSignalState.subId != this.subId)) {
          break;
        }
        if (localSignalState.colored != this.colored) {
          bool = true;
        } else {
          bool = false;
        }
      }
      label187:
      return true;
    }
    
    protected StringBuilder toStringBuilder()
    {
      StringBuilder localStringBuilder = super.toStringBuilder();
      localStringBuilder.insert(localStringBuilder.length() - 1, ",connected=" + this.connected);
      localStringBuilder.insert(localStringBuilder.length() - 1, ",activityIn=" + this.activityIn);
      localStringBuilder.insert(localStringBuilder.length() - 1, ",activityOut=" + this.activityOut);
      localStringBuilder.insert(localStringBuilder.length() - 1, ",overlayIconId=" + this.overlayIconId);
      localStringBuilder.insert(localStringBuilder.length() - 1, ",filter=" + this.filter);
      localStringBuilder.insert(localStringBuilder.length() - 1, ",wideOverlayIcon=" + this.isOverlayIconWide);
      localStringBuilder.insert(localStringBuilder.length() - 1, ",isShowRoaming=" + this.isShowRoaming);
      localStringBuilder.insert(localStringBuilder.length() - 1, ",subId=" + this.subId);
      localStringBuilder.insert(localStringBuilder.length() - 1, ",colored=" + this.colored);
      return localStringBuilder;
    }
  }
  
  public static class State
  {
    public boolean autoMirrorDrawable = true;
    public CharSequence contentDescription;
    public boolean disabledByPolicy;
    public CharSequence dualLabelContentDescription;
    public RestrictedLockUtils.EnforcedAdmin enforcedAdmin;
    public String expandedAccessibilityClassName;
    public QSTile.Icon icon;
    public CharSequence label;
    public String minimalAccessibilityClassName;
    public CharSequence minimalContentDescription;
    public boolean noDisableColor = false;
    
    public boolean copyTo(State paramState)
    {
      if (paramState == null) {
        throw new IllegalArgumentException();
      }
      if (!paramState.getClass().equals(getClass())) {
        throw new IllegalArgumentException();
      }
      boolean bool;
      if ((Objects.equals(paramState.icon, this.icon)) && (Objects.equals(paramState.label, this.label)) && (Objects.equals(paramState.contentDescription, this.contentDescription)) && (Objects.equals(Boolean.valueOf(paramState.autoMirrorDrawable), Boolean.valueOf(this.autoMirrorDrawable))) && (Objects.equals(paramState.dualLabelContentDescription, this.dualLabelContentDescription)) && (Objects.equals(paramState.minimalContentDescription, this.minimalContentDescription)) && (Objects.equals(paramState.minimalAccessibilityClassName, this.minimalAccessibilityClassName)) && (Objects.equals(paramState.expandedAccessibilityClassName, this.expandedAccessibilityClassName)) && (Objects.equals(Boolean.valueOf(paramState.disabledByPolicy), Boolean.valueOf(this.disabledByPolicy))) && (Objects.equals(Boolean.valueOf(paramState.noDisableColor), Boolean.valueOf(this.noDisableColor))))
      {
        if (!Objects.equals(paramState.enforcedAdmin, this.enforcedAdmin)) {
          break label307;
        }
        bool = false;
      }
      for (;;)
      {
        paramState.icon = this.icon;
        paramState.label = this.label;
        paramState.contentDescription = this.contentDescription;
        paramState.dualLabelContentDescription = this.dualLabelContentDescription;
        paramState.minimalContentDescription = this.minimalContentDescription;
        paramState.minimalAccessibilityClassName = this.minimalAccessibilityClassName;
        paramState.expandedAccessibilityClassName = this.expandedAccessibilityClassName;
        paramState.autoMirrorDrawable = this.autoMirrorDrawable;
        paramState.disabledByPolicy = this.disabledByPolicy;
        paramState.noDisableColor = this.noDisableColor;
        if (this.enforcedAdmin != null) {
          break;
        }
        paramState.enforcedAdmin = null;
        return bool;
        bool = true;
        continue;
        label307:
        bool = true;
      }
      if (paramState.enforcedAdmin == null)
      {
        paramState.enforcedAdmin = new RestrictedLockUtils.EnforcedAdmin(this.enforcedAdmin);
        return bool;
      }
      this.enforcedAdmin.copyTo(paramState.enforcedAdmin);
      return bool;
    }
    
    public String toString()
    {
      return toStringBuilder().toString();
    }
    
    protected StringBuilder toStringBuilder()
    {
      StringBuilder localStringBuilder = new StringBuilder(getClass().getSimpleName()).append('[');
      localStringBuilder.append(",icon=").append(this.icon);
      localStringBuilder.append(",label=").append(this.label);
      localStringBuilder.append(",contentDescription=").append(this.contentDescription);
      localStringBuilder.append(",dualLabelContentDescription=").append(this.dualLabelContentDescription);
      localStringBuilder.append(",minimalContentDescription=").append(this.minimalContentDescription);
      localStringBuilder.append(",minimalAccessibilityClassName=").append(this.minimalAccessibilityClassName);
      localStringBuilder.append(",expandedAccessibilityClassName=").append(this.expandedAccessibilityClassName);
      localStringBuilder.append(",autoMirrorDrawable=").append(this.autoMirrorDrawable);
      localStringBuilder.append(",disabledByPolicy=").append(this.disabledByPolicy);
      localStringBuilder.append(",enforcedAdmin=").append(this.enforcedAdmin);
      return localStringBuilder.append(']');
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\QSTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */