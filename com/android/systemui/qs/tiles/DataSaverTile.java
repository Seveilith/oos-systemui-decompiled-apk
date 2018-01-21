package com.android.systemui.qs.tiles;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Prefs;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.BooleanState;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.ResourceIcon;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.DataSaverController.Listener;
import com.android.systemui.statusbar.policy.NetworkController;

public class DataSaverTile
  extends QSTile<QSTile.BooleanState>
  implements DataSaverController.Listener
{
  private static final Intent DATA_SAVER_SETTINGS = new Intent("com.oneplus.action.DATAUSAGE_SAVER");
  private final DataSaverController mDataSaverController;
  
  public DataSaverTile(QSTile.Host paramHost)
  {
    super(paramHost);
    this.mDataSaverController = paramHost.getNetworkController().getDataSaverController();
  }
  
  private void toggleDataSaver()
  {
    QSTile.BooleanState localBooleanState = (QSTile.BooleanState)this.mState;
    if (this.mDataSaverController.isDataSaverEnabled()) {}
    for (boolean bool = false;; bool = true)
    {
      localBooleanState.value = bool;
      MetricsLogger.action(this.mContext, getMetricsCategory(), ((QSTile.BooleanState)this.mState).value);
      this.mDataSaverController.setDataSaverEnabled(((QSTile.BooleanState)this.mState).value);
      refreshState(Boolean.valueOf(((QSTile.BooleanState)this.mState).value));
      return;
    }
  }
  
  protected String composeChangeAnnouncement()
  {
    if (((QSTile.BooleanState)this.mState).value) {
      return this.mContext.getString(2131690238);
    }
    return this.mContext.getString(2131690237);
  }
  
  public Intent getLongClickIntent()
  {
    return DATA_SAVER_SETTINGS;
  }
  
  public int getMetricsCategory()
  {
    return 284;
  }
  
  public CharSequence getTileLabel()
  {
    return this.mContext.getString(2131690579);
  }
  
  protected void handleClick()
  {
    if ((((QSTile.BooleanState)this.mState).value) || (Prefs.getBoolean(this.mContext, "QsDataSaverDialogShown", false)))
    {
      toggleDataSaver();
      return;
    }
    SystemUIDialog localSystemUIDialog = new SystemUIDialog(this.mContext);
    localSystemUIDialog.setTitle(17040847);
    localSystemUIDialog.setMessage(17040846);
    localSystemUIDialog.setPositiveButton(17040848, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        DataSaverTile.-wrap0(DataSaverTile.this);
      }
    });
    localSystemUIDialog.setNegativeButton(17039360, null);
    localSystemUIDialog.setShowForAllUsers(true);
    localSystemUIDialog.show();
    Prefs.putBoolean(this.mContext, "QsDataSaverDialogShown", true);
  }
  
  protected void handleUpdateState(QSTile.BooleanState paramBooleanState, Object paramObject)
  {
    boolean bool;
    if ((paramObject instanceof Boolean))
    {
      bool = ((Boolean)paramObject).booleanValue();
      paramBooleanState.value = bool;
      paramBooleanState.label = this.mContext.getString(2131690579);
      paramBooleanState.contentDescription = paramBooleanState.label;
      if (!paramBooleanState.value) {
        break label90;
      }
    }
    label90:
    for (int i = 2130837703;; i = 2130837704)
    {
      paramBooleanState.icon = QSTile.ResourceIcon.get(i);
      paramObject = Switch.class.getName();
      paramBooleanState.expandedAccessibilityClassName = ((String)paramObject);
      paramBooleanState.minimalAccessibilityClassName = ((String)paramObject);
      return;
      bool = this.mDataSaverController.isDataSaverEnabled();
      break;
    }
  }
  
  public boolean isAvailable()
  {
    boolean bool = false;
    if (ActivityManager.getCurrentUser() == 0) {
      bool = true;
    }
    return bool;
  }
  
  public QSTile.BooleanState newTileState()
  {
    return new QSTile.BooleanState();
  }
  
  public void onDataSaverChanged(boolean paramBoolean)
  {
    refreshState(Boolean.valueOf(paramBoolean));
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mDataSaverController.addListener(this);
      return;
    }
    this.mDataSaverController.remListener(this);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\DataSaverTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */