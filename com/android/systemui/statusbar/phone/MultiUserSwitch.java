package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.os.UserManager;
import android.provider.ContactsContract.Profile;
import android.provider.ContactsContract.QuickContact;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import com.android.systemui.qs.QSPanel;
import com.android.systemui.statusbar.policy.KeyguardUserSwitcher;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.policy.UserSwitcherController.BaseUserAdapter;
import com.android.systemui.util.MdmLogger;

public class MultiUserSwitch
  extends FrameLayout
  implements View.OnClickListener
{
  private boolean mKeyguardMode;
  private KeyguardUserSwitcher mKeyguardUserSwitcher;
  private QSPanel mQsPanel;
  private final int[] mTmpInt2 = new int[2];
  private UserSwitcherController.BaseUserAdapter mUserListener;
  final UserManager mUserManager = UserManager.get(getContext());
  private UserSwitcherController mUserSwitcherController;
  
  public MultiUserSwitch(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void refreshContentDescription()
  {
    String str2 = null;
    String str1 = str2;
    if (this.mUserManager.isUserSwitcherEnabled())
    {
      str1 = str2;
      if (this.mUserSwitcherController != null) {
        str1 = this.mUserSwitcherController.getCurrentUserName(this.mContext);
      }
    }
    str2 = null;
    if (!TextUtils.isEmpty(str1)) {
      str2 = this.mContext.getString(2131690191, new Object[] { str1 });
    }
    if (!TextUtils.equals(getContentDescription(), str2)) {
      setContentDescription(str2);
    }
  }
  
  private void registerListener()
  {
    if ((this.mUserManager.isUserSwitcherEnabled()) && (this.mUserListener == null))
    {
      UserSwitcherController localUserSwitcherController = this.mUserSwitcherController;
      if (localUserSwitcherController != null)
      {
        this.mUserListener = new UserSwitcherController.BaseUserAdapter(localUserSwitcherController)
        {
          public View getView(int paramAnonymousInt, View paramAnonymousView, ViewGroup paramAnonymousViewGroup)
          {
            return null;
          }
          
          public void notifyDataSetChanged()
          {
            MultiUserSwitch.-wrap0(MultiUserSwitch.this);
          }
        };
        refreshContentDescription();
      }
    }
  }
  
  public boolean hasMultipleUsers()
  {
    boolean bool = false;
    if (this.mUserListener == null) {
      return false;
    }
    if (this.mUserListener.getCount() != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public void onClick(View paramView)
  {
    MdmLogger.log("quick_user", "icon", "1");
    if (this.mUserManager.isUserSwitcherEnabled()) {
      if (this.mKeyguardMode) {
        if (this.mKeyguardUserSwitcher != null) {
          this.mKeyguardUserSwitcher.show(true);
        }
      }
    }
    while (this.mQsPanel == null)
    {
      do
      {
        return;
      } while ((this.mQsPanel == null) || (this.mUserSwitcherController == null));
      if (getChildCount() > 0) {}
      for (paramView = getChildAt(0);; paramView = this)
      {
        paramView.getLocationInWindow(this.mTmpInt2);
        int[] arrayOfInt = this.mTmpInt2;
        arrayOfInt[0] += paramView.getWidth() / 2;
        arrayOfInt = this.mTmpInt2;
        arrayOfInt[1] += paramView.getHeight() / 2;
        this.mQsPanel.showDetailAdapter(true, this.mUserSwitcherController.userDetailAdapter, this.mTmpInt2);
        return;
      }
    }
    paramView = ContactsContract.QuickContact.composeQuickContactsIntent(getContext(), paramView, ContactsContract.Profile.CONTENT_URI, 3, null);
    this.mQsPanel.getHost().startActivityDismissingKeyguard(paramView);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    setOnClickListener(this);
    refreshContentDescription();
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    paramAccessibilityEvent.setClassName(Button.class.getName());
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
    paramAccessibilityNodeInfo.setClassName(Button.class.getName());
  }
  
  public void setClickable(boolean paramBoolean)
  {
    super.setClickable(paramBoolean);
    refreshContentDescription();
  }
  
  public void setKeyguardMode(boolean paramBoolean)
  {
    this.mKeyguardMode = paramBoolean;
    registerListener();
  }
  
  public void setKeyguardUserSwitcher(KeyguardUserSwitcher paramKeyguardUserSwitcher)
  {
    this.mKeyguardUserSwitcher = paramKeyguardUserSwitcher;
  }
  
  public void setQsPanel(QSPanel paramQSPanel)
  {
    this.mQsPanel = paramQSPanel;
    setUserSwitcherController(paramQSPanel.getHost().getUserSwitcherController());
  }
  
  public void setUserSwitcherController(UserSwitcherController paramUserSwitcherController)
  {
    this.mUserSwitcherController = paramUserSwitcherController;
    registerListener();
    refreshContentDescription();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\MultiUserSwitch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */