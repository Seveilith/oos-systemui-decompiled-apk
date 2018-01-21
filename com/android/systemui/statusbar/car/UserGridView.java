package com.android.systemui.statusbar.car;

import android.content.Context;
import android.content.pm.UserInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.android.systemui.statusbar.UserUtil;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.policy.UserSwitcherController.BaseUserAdapter;
import com.android.systemui.statusbar.policy.UserSwitcherController.UserRecord;

public class UserGridView
  extends GridView
{
  private Adapter mAdapter;
  private int mPendingUserId = 55536;
  private PhoneStatusBar mStatusBar;
  private UserSwitcherController mUserSwitcherController;
  
  public UserGridView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void showOfflineAuthUi()
  {
    this.mStatusBar.executeRunnableDismissingKeyguard(null, null, true, true, true);
  }
  
  public void init(PhoneStatusBar paramPhoneStatusBar, UserSwitcherController paramUserSwitcherController)
  {
    this.mStatusBar = paramPhoneStatusBar;
    this.mUserSwitcherController = paramUserSwitcherController;
    this.mAdapter = new Adapter(this.mUserSwitcherController);
    setAdapter(this.mAdapter);
    setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        UserGridView.-set0(UserGridView.this, 55536);
        paramAnonymousAdapterView = UserGridView.-get0(UserGridView.this).getItem(paramAnonymousInt);
        if (paramAnonymousAdapterView == null) {
          return;
        }
        if ((paramAnonymousAdapterView.isGuest) || (paramAnonymousAdapterView.isAddUser))
        {
          UserGridView.-get1(UserGridView.this).switchTo(paramAnonymousAdapterView);
          return;
        }
        if (paramAnonymousAdapterView.isCurrent)
        {
          UserGridView.-wrap0(UserGridView.this);
          return;
        }
        UserGridView.-set0(UserGridView.this, paramAnonymousAdapterView.info.id);
        UserGridView.-get1(UserGridView.this).switchTo(paramAnonymousAdapterView);
      }
    });
    setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
    {
      public boolean onItemLongClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        paramAnonymousAdapterView = UserGridView.-get0(UserGridView.this).getItem(paramAnonymousInt);
        if ((paramAnonymousAdapterView == null) || (paramAnonymousAdapterView.isAddUser)) {
          return false;
        }
        if (paramAnonymousAdapterView.isGuest)
        {
          if (paramAnonymousAdapterView.isCurrent) {
            UserGridView.-get1(UserGridView.this).switchTo(paramAnonymousAdapterView);
          }
          return true;
        }
        UserUtil.deleteUserWithPrompt(UserGridView.this.getContext(), paramAnonymousAdapterView.info.id, UserGridView.-get1(UserGridView.this));
        return true;
      }
    });
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getMode(paramInt1);
    int j = View.MeasureSpec.getSize(paramInt1);
    if (i == 0)
    {
      setNumColumns(-1);
      super.onMeasure(paramInt1, paramInt2);
      return;
    }
    int k = Math.max(1, getRequestedColumnWidth());
    if (getAdapter() == null) {}
    for (i = 0;; i = getAdapter().getCount())
    {
      setNumColumns(Math.max(1, Math.min(i, j / k)));
      break;
    }
  }
  
  public void onUserSwitched(int paramInt)
  {
    if (this.mPendingUserId == paramInt) {
      post(new Runnable()
      {
        public void run()
        {
          UserGridView.-wrap0(UserGridView.this);
        }
      });
    }
    this.mPendingUserId = 55536;
  }
  
  private final class Adapter
    extends UserSwitcherController.BaseUserAdapter
  {
    public Adapter(UserSwitcherController paramUserSwitcherController)
    {
      super();
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      paramViewGroup = paramView;
      if (paramView == null) {
        paramViewGroup = ((LayoutInflater)UserGridView.this.getContext().getSystemService("layout_inflater")).inflate(2130968607, null);
      }
      paramView = getItem(paramInt);
      Object localObject = (TextView)paramViewGroup.findViewById(2131951800);
      if (paramView != null)
      {
        ((TextView)localObject).setText(getName(UserGridView.this.getContext(), paramView));
        paramViewGroup.setActivated(paramView.isCurrent);
      }
      for (;;)
      {
        localObject = (ImageView)paramViewGroup.findViewById(2131951799);
        if ((paramView != null) && (paramView.picture != null)) {
          break;
        }
        ((ImageView)localObject).setImageDrawable(getDrawable(UserGridView.this.getContext(), paramView));
        return paramViewGroup;
        ((TextView)localObject).setText("Unknown");
      }
      ((ImageView)localObject).setImageBitmap(paramView.picture);
      return paramViewGroup;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\car\UserGridView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */