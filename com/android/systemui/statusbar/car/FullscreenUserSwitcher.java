package com.android.systemui.statusbar.car;

import android.view.View;
import android.view.ViewStub;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import com.android.systemui.statusbar.policy.UserSwitcherController;

public class FullscreenUserSwitcher
{
  private View mContainer;
  private UserGridView mUserGridView;
  private UserSwitcherController mUserSwitcherController;
  
  public FullscreenUserSwitcher(PhoneStatusBar paramPhoneStatusBar, UserSwitcherController paramUserSwitcherController, ViewStub paramViewStub)
  {
    this.mUserSwitcherController = paramUserSwitcherController;
    this.mContainer = paramViewStub.inflate();
    this.mUserGridView = ((UserGridView)this.mContainer.findViewById(2131951801));
    this.mUserGridView.init(paramPhoneStatusBar, this.mUserSwitcherController);
  }
  
  public void hide()
  {
    this.mContainer.setVisibility(8);
  }
  
  public void onUserSwitched(int paramInt)
  {
    this.mUserGridView.onUserSwitched(paramInt);
  }
  
  public void show()
  {
    this.mContainer.setVisibility(0);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\car\FullscreenUserSwitcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */