package com.android.systemui.qs.tiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.util.Log;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.BooleanState;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.ResourceIcon;
import com.android.systemui.util.Utils;

public class NfcTile
  extends QSTile<QSTile.BooleanState>
{
  private final String TAG = "NfcTile";
  private boolean mListening;
  private NfcAdapter mNfcAdapter = null;
  private final NfcBroadcastReceiver mReceiver = new NfcBroadcastReceiver(null);
  
  public NfcTile(QSTile.Host paramHost)
  {
    super(paramHost);
  }
  
  private void obtainNfcAdapter()
  {
    if ((this.mNfcAdapter == null) && (Utils.isSystemReady())) {}
    try
    {
      this.mNfcAdapter = NfcAdapter.getNfcAdapter(this.mContext);
      return;
    }
    catch (Exception localException)
    {
      Log.e("NfcTile", "Error while getting Nfc Adapter", localException);
    }
  }
  
  private void setEnable(boolean paramBoolean)
  {
    obtainNfcAdapter();
    if (this.mNfcAdapter != null)
    {
      if (!paramBoolean) {
        break label32;
      }
      this.mNfcAdapter.enable();
    }
    for (;;)
    {
      refreshState(Boolean.valueOf(paramBoolean));
      return;
      label32:
      this.mNfcAdapter.disable();
    }
  }
  
  private void updateNfcStateChanged(int paramInt)
  {
    switch (paramInt)
    {
    }
    for (;;)
    {
      refreshState(this.mReceiver.mNfcStatus);
      return;
      Log.d("NfcTile", "updateNfcStateChanged:On");
      this.mReceiver.mNfcStatus = Boolean.valueOf(true);
      continue;
      Log.d("NfcTile", "updateNfcStateChanged:Off");
      this.mReceiver.mNfcStatus = Boolean.valueOf(false);
    }
  }
  
  protected String composeChangeAnnouncement()
  {
    if (((QSTile.BooleanState)this.mState).value) {
      return this.mContext.getString(2131689941);
    }
    return this.mContext.getString(2131689940);
  }
  
  public Intent getLongClickIntent()
  {
    return new Intent("android.settings.NFC_SETTINGS");
  }
  
  public int getMetricsCategory()
  {
    return 412;
  }
  
  public CharSequence getTileLabel()
  {
    return this.mContext.getString(2131689922);
  }
  
  public void handleClick()
  {
    if (((QSTile.BooleanState)this.mState).value) {}
    for (boolean bool = false;; bool = true)
    {
      setEnable(bool);
      return;
    }
  }
  
  protected void handleUpdateState(QSTile.BooleanState paramBooleanState, Object paramObject)
  {
    Boolean localBoolean = (Boolean)paramObject;
    paramObject = localBoolean;
    if (localBoolean == null) {
      paramObject = this.mReceiver.mNfcStatus;
    }
    paramBooleanState.value = ((Boolean)paramObject).booleanValue();
    if (paramBooleanState.value)
    {
      paramBooleanState.contentDescription = this.mContext.getString(2131689941);
      if (!paramBooleanState.value) {
        break label98;
      }
    }
    label98:
    for (int i = 2130837800;; i = 2130837799)
    {
      paramBooleanState.icon = QSTile.ResourceIcon.get(i);
      paramBooleanState.label = this.mContext.getString(2131689922);
      return;
      paramBooleanState.contentDescription = this.mContext.getString(2131689940);
      break;
    }
  }
  
  public QSTile.BooleanState newTileState()
  {
    return new QSTile.BooleanState();
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (this.mListening == paramBoolean) {
      return;
    }
    this.mListening = paramBoolean;
    if (paramBoolean)
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.nfc.action.ADAPTER_STATE_CHANGED");
      this.mContext.registerReceiver(this.mReceiver, localIntentFilter);
      obtainNfcAdapter();
      if (this.mNfcAdapter != null) {
        updateNfcStateChanged(this.mNfcAdapter.getAdapterState());
      }
      return;
    }
    this.mContext.unregisterReceiver(this.mReceiver);
  }
  
  private final class NfcBroadcastReceiver
    extends BroadcastReceiver
  {
    Boolean mNfcStatus = Boolean.valueOf(false);
    
    private NfcBroadcastReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if ("android.nfc.action.ADAPTER_STATE_CHANGED".equals(paramIntent.getAction())) {
        NfcTile.-wrap0(NfcTile.this, paramIntent.getIntExtra("android.nfc.extra.ADAPTER_STATE", 1));
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\NfcTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */