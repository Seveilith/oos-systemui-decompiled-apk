package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.pm.UserInfo;
import android.os.Environment;
import android.os.StatFs;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.RestrictedLockUtils;
import com.android.systemui.SysUIToast;
import com.android.systemui.qs.PseudoGridView;
import com.android.systemui.qs.PseudoGridView.ViewGroupAdapterBridge;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.policy.UserSwitcherController.BaseUserAdapter;
import com.android.systemui.statusbar.policy.UserSwitcherController.UserRecord;
import com.android.systemui.util.MdmLogger;
import java.io.File;

public class UserDetailView
  extends PseudoGridView
{
  private Adapter mAdapter;
  
  public UserDetailView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public static long getAvailableInternalMemorySize()
  {
    StatFs localStatFs = new StatFs(Environment.getDataDirectory().getPath());
    long l = localStatFs.getBlockSize();
    return localStatFs.getAvailableBlocks() * l;
  }
  
  public static UserDetailView inflate(Context paramContext, ViewGroup paramViewGroup, boolean paramBoolean)
  {
    return (UserDetailView)LayoutInflater.from(paramContext).inflate(2130968782, paramViewGroup, paramBoolean);
  }
  
  public void createAndSetAdapter(UserSwitcherController paramUserSwitcherController)
  {
    this.mAdapter = new Adapter(this.mContext, paramUserSwitcherController);
    PseudoGridView.ViewGroupAdapterBridge.link(this, this.mAdapter);
  }
  
  public void refreshAdapter()
  {
    this.mAdapter.refresh();
  }
  
  public static class Adapter
    extends UserSwitcherController.BaseUserAdapter
    implements View.OnClickListener
  {
    private final Context mContext;
    private final UserSwitcherController mController;
    
    public Adapter(Context paramContext, UserSwitcherController paramUserSwitcherController)
    {
      super();
      this.mContext = paramContext;
      this.mController = paramUserSwitcherController;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      UserSwitcherController.UserRecord localUserRecord = getItem(paramInt);
      paramViewGroup = UserDetailItemView.convertOrInflate(this.mContext, paramView, paramViewGroup);
      if (paramViewGroup != paramView) {
        paramViewGroup.setOnClickListener(this);
      }
      paramView = getName(this.mContext, localUserRecord);
      if (localUserRecord.picture == null)
      {
        paramViewGroup.bind(paramView, getDrawable(this.mContext, localUserRecord), localUserRecord.resolveId());
        paramViewGroup.updateThemeColor(localUserRecord.isAddUser);
        paramViewGroup.setActivated(localUserRecord.isCurrent);
        paramViewGroup.setDisabledByAdmin(localUserRecord.isDisabledByAdmin);
        localUserRecord.isStorageInsufficient = false;
        if (localUserRecord.isSwitchToEnabled) {
          break label141;
        }
        paramViewGroup.setEnabled(false);
      }
      for (;;)
      {
        paramViewGroup.setTag(localUserRecord);
        return paramViewGroup;
        paramViewGroup.bind(paramView, localUserRecord.picture, localUserRecord.info.id);
        break;
        label141:
        if ((localUserRecord.isAddUser) || ((localUserRecord.isGuest) && (localUserRecord.info == null)))
        {
          long l = UserDetailView.getAvailableInternalMemorySize();
          Log.d("UserDetailView", "Available storage size=" + l + " bytes");
          if (l < 209715200L)
          {
            Log.d("UserDetailView", "Storage size is too small, disable add user function");
            paramViewGroup.setEnabled(false);
            localUserRecord.isSwitchToEnabled = false;
            localUserRecord.isStorageInsufficient = true;
          }
        }
      }
    }
    
    public void onClick(View paramView)
    {
      paramView = (UserSwitcherController.UserRecord)paramView.getTag();
      if (paramView.isDisabledByAdmin)
      {
        paramView = RestrictedLockUtils.getShowAdminSupportDetailsIntent(this.mContext, paramView.enforcedAdmin);
        this.mController.startActivity(paramView);
      }
      do
      {
        return;
        if (paramView.isSwitchToEnabled)
        {
          MetricsLogger.action(this.mContext, 156);
          if (paramView.isGuest) {
            MdmLogger.log("quick_user", "guest", "1");
          }
          for (;;)
          {
            switchTo(paramView);
            return;
            if (!paramView.isAddUser) {
              MdmLogger.log("quick_user", "switch", "1");
            }
          }
        }
      } while (!paramView.isStorageInsufficient);
      SysUIToast.makeText(this.mContext, this.mContext.getString(2131690013), 0).show();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\UserDetailView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */