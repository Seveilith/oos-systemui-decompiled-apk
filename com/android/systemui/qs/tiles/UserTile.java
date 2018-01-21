package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import com.android.systemui.qs.QSTile;
import com.android.systemui.qs.QSTile.DetailAdapter;
import com.android.systemui.qs.QSTile.Host;
import com.android.systemui.qs.QSTile.Icon;
import com.android.systemui.qs.QSTile.State;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.UserInfoController.OnUserInfoChangedListener;
import com.android.systemui.statusbar.policy.UserSwitcherController;

public class UserTile
  extends QSTile<QSTile.State>
  implements UserInfoController.OnUserInfoChangedListener
{
  private Pair<String, Drawable> mLastUpdate;
  private final UserInfoController mUserInfoController;
  private final UserSwitcherController mUserSwitcherController;
  
  public UserTile(QSTile.Host paramHost)
  {
    super(paramHost);
    this.mUserSwitcherController = paramHost.getUserSwitcherController();
    this.mUserInfoController = paramHost.getUserInfoController();
  }
  
  public QSTile.DetailAdapter getDetailAdapter()
  {
    return this.mUserSwitcherController.userDetailAdapter;
  }
  
  public Intent getLongClickIntent()
  {
    return new Intent("android.settings.USER_SETTINGS");
  }
  
  public int getMetricsCategory()
  {
    return 260;
  }
  
  public CharSequence getTileLabel()
  {
    return getState().label;
  }
  
  protected void handleClick()
  {
    showDetail(true);
  }
  
  protected void handleUpdateState(QSTile.State paramState, final Object paramObject)
  {
    if (paramObject != null) {}
    for (paramObject = (Pair)paramObject;; paramObject = this.mLastUpdate)
    {
      if (paramObject != null)
      {
        paramState.label = ((CharSequence)((Pair)paramObject).first);
        paramState.contentDescription = ((CharSequence)((Pair)paramObject).first);
        paramState.icon = new QSTile.Icon()
        {
          public Drawable getDrawable(Context paramAnonymousContext)
          {
            return (Drawable)paramObject.second;
          }
        };
      }
      return;
    }
  }
  
  public QSTile.State newTileState()
  {
    return new QSTile.State();
  }
  
  public void onUserInfoChanged(String paramString, Drawable paramDrawable)
  {
    this.mLastUpdate = new Pair(paramString, paramDrawable);
    refreshState(this.mLastUpdate);
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mUserInfoController.addListener(this);
      return;
    }
    this.mUserInfoController.remListener(this);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\UserTile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */