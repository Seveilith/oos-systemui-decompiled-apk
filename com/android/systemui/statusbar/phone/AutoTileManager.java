package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.Prefs;
import com.android.systemui.qs.SecureSetting;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.DataSaverController.Listener;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.HotspotController.Callback;
import com.android.systemui.statusbar.policy.NetworkController;

public class AutoTileManager
{
  private SecureSetting mColorsSetting;
  private final Context mContext;
  private final DataSaverController.Listener mDataSaverListener = new DataSaverController.Listener()
  {
    public void onDataSaverChanged(boolean paramAnonymousBoolean)
    {
      if (paramAnonymousBoolean)
      {
        AutoTileManager.-get4(AutoTileManager.this).addTile("saver");
        Prefs.putBoolean(AutoTileManager.-get1(AutoTileManager.this), "QsDataSaverAdded", true);
        AutoTileManager.-get3(AutoTileManager.this).post(new Runnable()
        {
          public void run()
          {
            AutoTileManager.-get4(AutoTileManager.this).getNetworkController().getDataSaverController().remListener(AutoTileManager.-get2(AutoTileManager.this));
          }
        });
      }
    }
  };
  private final Handler mHandler;
  private final QSTileHost mHost;
  private final HotspotController.Callback mHotspotCallback = new HotspotController.Callback()
  {
    public void onHotspotChanged(boolean paramAnonymousBoolean)
    {
      if (paramAnonymousBoolean)
      {
        AutoTileManager.-get4(AutoTileManager.this).addTile("hotspot");
        Prefs.putBoolean(AutoTileManager.-get1(AutoTileManager.this), "QsHotspotAdded", true);
        AutoTileManager.-get3(AutoTileManager.this).post(new Runnable()
        {
          public void run()
          {
            AutoTileManager.-get4(AutoTileManager.this).getHotspotController().removeCallback(AutoTileManager.-get5(AutoTileManager.this));
          }
        });
      }
    }
  };
  private final ManagedProfileController.Callback mProfileCallback = new ManagedProfileController.Callback()
  {
    public void onManagedProfileChanged()
    {
      if (AutoTileManager.-get4(AutoTileManager.this).getManagedProfileController().hasActiveProfile())
      {
        AutoTileManager.-get4(AutoTileManager.this).addTile("work");
        Prefs.putBoolean(AutoTileManager.-get1(AutoTileManager.this), "QsWorkAdded", true);
      }
    }
    
    public void onManagedProfileRemoved()
    {
      if (!AutoTileManager.-get4(AutoTileManager.this).getManagedProfileController().hasActiveProfile())
      {
        AutoTileManager.-get4(AutoTileManager.this).removeTile("work");
        Prefs.putBoolean(AutoTileManager.-get1(AutoTileManager.this), "QsWorkAdded", false);
      }
    }
  };
  
  public AutoTileManager(Context paramContext, QSTileHost paramQSTileHost)
  {
    this.mContext = paramContext;
    this.mHost = paramQSTileHost;
    this.mHandler = new Handler(this.mHost.getLooper());
    if (!Prefs.getBoolean(paramContext, "QsHotspotAdded", false)) {
      paramQSTileHost.getHotspotController().addCallback(this.mHotspotCallback);
    }
    if (!Prefs.getBoolean(paramContext, "QsDataSaverAdded", false)) {
      paramQSTileHost.getNetworkController().getDataSaverController().addListener(this.mDataSaverListener);
    }
    if (!Prefs.getBoolean(paramContext, "QsInvertColorsAdded", false))
    {
      this.mColorsSetting = new SecureSetting(this.mContext, this.mHandler, "accessibility_display_inversion_enabled")
      {
        protected void handleValueChanged(int paramAnonymousInt, boolean paramAnonymousBoolean)
        {
          if (paramAnonymousInt != 0)
          {
            AutoTileManager.-get4(AutoTileManager.this).addTile("inversion");
            Prefs.putBoolean(AutoTileManager.-get1(AutoTileManager.this), "QsInvertColorsAdded", true);
            AutoTileManager.-get3(AutoTileManager.this).post(new Runnable()
            {
              public void run()
              {
                AutoTileManager.-get0(AutoTileManager.this).setListening(false);
              }
            });
          }
        }
      };
      this.mColorsSetting.setListening(true);
    }
    paramQSTileHost.getManagedProfileController().addCallback(this.mProfileCallback);
  }
  
  public void destroy() {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\AutoTileManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */