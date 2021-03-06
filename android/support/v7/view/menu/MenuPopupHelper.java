package android.support.v7.view.menu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.dimen;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow.OnDismissListener;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class MenuPopupHelper
{
  private View mAnchorView;
  private final Context mContext;
  private int mDropDownGravity = 8388611;
  private boolean mForceShowIcon;
  private final PopupWindow.OnDismissListener mInternalOnDismissListener = new PopupWindow.OnDismissListener()
  {
    public void onDismiss()
    {
      MenuPopupHelper.this.onDismiss();
    }
  };
  private final MenuBuilder mMenu;
  private PopupWindow.OnDismissListener mOnDismissListener;
  private final boolean mOverflowOnly;
  private MenuPopup mPopup;
  private final int mPopupStyleAttr;
  private final int mPopupStyleRes;
  private MenuPresenter.Callback mPresenterCallback;
  
  public MenuPopupHelper(@NonNull Context paramContext, @NonNull MenuBuilder paramMenuBuilder, @NonNull View paramView, boolean paramBoolean, @AttrRes int paramInt)
  {
    this(paramContext, paramMenuBuilder, paramView, paramBoolean, paramInt, 0);
  }
  
  public MenuPopupHelper(@NonNull Context paramContext, @NonNull MenuBuilder paramMenuBuilder, @NonNull View paramView, boolean paramBoolean, @AttrRes int paramInt1, @StyleRes int paramInt2)
  {
    this.mContext = paramContext;
    this.mMenu = paramMenuBuilder;
    this.mAnchorView = paramView;
    this.mOverflowOnly = paramBoolean;
    this.mPopupStyleAttr = paramInt1;
    this.mPopupStyleRes = paramInt2;
  }
  
  @NonNull
  private MenuPopup createPopup()
  {
    Object localObject = ((WindowManager)this.mContext.getSystemService("window")).getDefaultDisplay();
    Point localPoint = new Point();
    int i;
    if (Build.VERSION.SDK_INT >= 17)
    {
      ((Display)localObject).getRealSize(localPoint);
      if (Math.min(localPoint.x, localPoint.y) < this.mContext.getResources().getDimensionPixelSize(R.dimen.abc_cascading_menus_min_smallest_width)) {
        break label181;
      }
      i = 1;
      label68:
      if (i == 0) {
        break label186;
      }
    }
    label181:
    label186:
    for (localObject = new CascadingMenuPopup(this.mContext, this.mAnchorView, this.mPopupStyleAttr, this.mPopupStyleRes, this.mOverflowOnly);; localObject = new StandardMenuPopup(this.mContext, this.mMenu, this.mAnchorView, this.mPopupStyleAttr, this.mPopupStyleRes, this.mOverflowOnly))
    {
      ((MenuPopup)localObject).addMenu(this.mMenu);
      ((MenuPopup)localObject).setOnDismissListener(this.mInternalOnDismissListener);
      ((MenuPopup)localObject).setAnchorView(this.mAnchorView);
      ((MenuPopup)localObject).setCallback(this.mPresenterCallback);
      ((MenuPopup)localObject).setForceShowIcon(this.mForceShowIcon);
      ((MenuPopup)localObject).setGravity(this.mDropDownGravity);
      return (MenuPopup)localObject;
      if (Build.VERSION.SDK_INT >= 13)
      {
        ((Display)localObject).getSize(localPoint);
        break;
      }
      localPoint.set(((Display)localObject).getWidth(), ((Display)localObject).getHeight());
      break;
      i = 0;
      break label68;
    }
  }
  
  private void showPopup(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    MenuPopup localMenuPopup = getPopup();
    localMenuPopup.setShowTitle(paramBoolean2);
    if (paramBoolean1)
    {
      int i = paramInt1;
      if ((GravityCompat.getAbsoluteGravity(this.mDropDownGravity, ViewCompat.getLayoutDirection(this.mAnchorView)) & 0x7) == 5) {
        i = paramInt1 - this.mAnchorView.getWidth();
      }
      localMenuPopup.setHorizontalOffset(i);
      localMenuPopup.setVerticalOffset(paramInt2);
      paramInt1 = (int)(48.0F * this.mContext.getResources().getDisplayMetrics().density / 2.0F);
      localMenuPopup.setEpicenterBounds(new Rect(i - paramInt1, paramInt2 - paramInt1, i + paramInt1, paramInt2 + paramInt1));
    }
    localMenuPopup.show();
  }
  
  public void dismiss()
  {
    if (isShowing()) {
      this.mPopup.dismiss();
    }
  }
  
  @NonNull
  public MenuPopup getPopup()
  {
    if (this.mPopup == null) {
      this.mPopup = createPopup();
    }
    return this.mPopup;
  }
  
  public boolean isShowing()
  {
    if (this.mPopup != null) {
      return this.mPopup.isShowing();
    }
    return false;
  }
  
  protected void onDismiss()
  {
    this.mPopup = null;
    if (this.mOnDismissListener != null) {
      this.mOnDismissListener.onDismiss();
    }
  }
  
  public void setAnchorView(@NonNull View paramView)
  {
    this.mAnchorView = paramView;
  }
  
  public void setForceShowIcon(boolean paramBoolean)
  {
    this.mForceShowIcon = paramBoolean;
    if (this.mPopup != null) {
      this.mPopup.setForceShowIcon(paramBoolean);
    }
  }
  
  public void setGravity(int paramInt)
  {
    this.mDropDownGravity = paramInt;
  }
  
  public void setOnDismissListener(@Nullable PopupWindow.OnDismissListener paramOnDismissListener)
  {
    this.mOnDismissListener = paramOnDismissListener;
  }
  
  public void setPresenterCallback(@Nullable MenuPresenter.Callback paramCallback)
  {
    this.mPresenterCallback = paramCallback;
    if (this.mPopup != null) {
      this.mPopup.setCallback(paramCallback);
    }
  }
  
  public void show()
  {
    if (!tryShow()) {
      throw new IllegalStateException("MenuPopupHelper cannot be used without an anchor");
    }
  }
  
  public boolean tryShow()
  {
    if (isShowing()) {
      return true;
    }
    if (this.mAnchorView == null) {
      return false;
    }
    showPopup(0, 0, false, false);
    return true;
  }
  
  public boolean tryShow(int paramInt1, int paramInt2)
  {
    if (isShowing()) {
      return true;
    }
    if (this.mAnchorView == null) {
      return false;
    }
    showPopup(paramInt1, paramInt2, true, true);
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\view\menu\MenuPopupHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */