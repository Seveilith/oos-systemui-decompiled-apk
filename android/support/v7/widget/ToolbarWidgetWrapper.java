package android.support.v7.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.drawable;
import android.support.v7.appcompat.R.string;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.view.menu.ActionMenuItem;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window.Callback;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class ToolbarWidgetWrapper
  implements DecorToolbar
{
  private View mCustomView;
  private int mDefaultNavigationContentDescription = 0;
  private Drawable mDefaultNavigationIcon;
  private int mDisplayOpts;
  private CharSequence mHomeDescription;
  private Drawable mIcon;
  private Drawable mLogo;
  boolean mMenuPrepared;
  private Drawable mNavIcon;
  private int mNavigationMode = 0;
  private CharSequence mSubtitle;
  CharSequence mTitle;
  private boolean mTitleSet;
  Toolbar mToolbar;
  Window.Callback mWindowCallback;
  
  public ToolbarWidgetWrapper(Toolbar paramToolbar, boolean paramBoolean)
  {
    this(paramToolbar, paramBoolean, R.string.abc_action_bar_up_description, R.drawable.abc_ic_ab_back_material);
  }
  
  public ToolbarWidgetWrapper(Toolbar paramToolbar, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    this.mToolbar = paramToolbar;
    this.mTitle = paramToolbar.getTitle();
    this.mSubtitle = paramToolbar.getSubtitle();
    boolean bool;
    if (this.mTitle != null)
    {
      bool = true;
      this.mTitleSet = bool;
      this.mNavIcon = paramToolbar.getNavigationIcon();
      paramToolbar = TintTypedArray.obtainStyledAttributes(paramToolbar.getContext(), null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
      this.mDefaultNavigationIcon = paramToolbar.getDrawable(R.styleable.ActionBar_homeAsUpIndicator);
      if (!paramBoolean) {
        break label477;
      }
      Object localObject = paramToolbar.getText(R.styleable.ActionBar_title);
      if (!TextUtils.isEmpty((CharSequence)localObject)) {
        setTitle((CharSequence)localObject);
      }
      localObject = paramToolbar.getText(R.styleable.ActionBar_subtitle);
      if (!TextUtils.isEmpty((CharSequence)localObject)) {
        setSubtitle((CharSequence)localObject);
      }
      localObject = paramToolbar.getDrawable(R.styleable.ActionBar_logo);
      if (localObject != null) {
        setLogo((Drawable)localObject);
      }
      localObject = paramToolbar.getDrawable(R.styleable.ActionBar_icon);
      if (localObject != null) {
        setIcon((Drawable)localObject);
      }
      if ((this.mNavIcon == null) && (this.mDefaultNavigationIcon != null)) {
        setNavigationIcon(this.mDefaultNavigationIcon);
      }
      setDisplayOptions(paramToolbar.getInt(R.styleable.ActionBar_displayOptions, 0));
      paramInt2 = paramToolbar.getResourceId(R.styleable.ActionBar_customNavigationLayout, 0);
      if (paramInt2 != 0)
      {
        setCustomView(LayoutInflater.from(this.mToolbar.getContext()).inflate(paramInt2, this.mToolbar, false));
        setDisplayOptions(this.mDisplayOpts | 0x10);
      }
      paramInt2 = paramToolbar.getLayoutDimension(R.styleable.ActionBar_height, 0);
      if (paramInt2 > 0)
      {
        localObject = this.mToolbar.getLayoutParams();
        ((ViewGroup.LayoutParams)localObject).height = paramInt2;
        this.mToolbar.setLayoutParams((ViewGroup.LayoutParams)localObject);
      }
      paramInt2 = paramToolbar.getDimensionPixelOffset(R.styleable.ActionBar_contentInsetStart, -1);
      int i = paramToolbar.getDimensionPixelOffset(R.styleable.ActionBar_contentInsetEnd, -1);
      if ((paramInt2 >= 0) || (i >= 0)) {
        this.mToolbar.setContentInsetsRelative(Math.max(paramInt2, 0), Math.max(i, 0));
      }
      paramInt2 = paramToolbar.getResourceId(R.styleable.ActionBar_titleTextStyle, 0);
      if (paramInt2 != 0) {
        this.mToolbar.setTitleTextAppearance(this.mToolbar.getContext(), paramInt2);
      }
      paramInt2 = paramToolbar.getResourceId(R.styleable.ActionBar_subtitleTextStyle, 0);
      if (paramInt2 != 0) {
        this.mToolbar.setSubtitleTextAppearance(this.mToolbar.getContext(), paramInt2);
      }
      paramInt2 = paramToolbar.getResourceId(R.styleable.ActionBar_popupTheme, 0);
      if (paramInt2 != 0) {
        this.mToolbar.setPopupTheme(paramInt2);
      }
    }
    for (;;)
    {
      paramToolbar.recycle();
      setDefaultNavigationContentDescription(paramInt1);
      this.mHomeDescription = this.mToolbar.getNavigationContentDescription();
      this.mToolbar.setNavigationOnClickListener(new View.OnClickListener()
      {
        final ActionMenuItem mNavItem = new ActionMenuItem(ToolbarWidgetWrapper.this.mToolbar.getContext(), 0, 16908332, 0, 0, ToolbarWidgetWrapper.this.mTitle);
        
        public void onClick(View paramAnonymousView)
        {
          if ((ToolbarWidgetWrapper.this.mWindowCallback != null) && (ToolbarWidgetWrapper.this.mMenuPrepared)) {
            ToolbarWidgetWrapper.this.mWindowCallback.onMenuItemSelected(0, this.mNavItem);
          }
        }
      });
      return;
      bool = false;
      break;
      label477:
      this.mDisplayOpts = detectDisplayOptions();
    }
  }
  
  private int detectDisplayOptions()
  {
    int i = 11;
    if (this.mToolbar.getNavigationIcon() != null)
    {
      i = 15;
      this.mDefaultNavigationIcon = this.mToolbar.getNavigationIcon();
    }
    return i;
  }
  
  private void setTitleInt(CharSequence paramCharSequence)
  {
    this.mTitle = paramCharSequence;
    if ((this.mDisplayOpts & 0x8) != 0) {
      this.mToolbar.setTitle(paramCharSequence);
    }
  }
  
  private void updateHomeAccessibility()
  {
    if ((this.mDisplayOpts & 0x4) != 0)
    {
      if (TextUtils.isEmpty(this.mHomeDescription)) {
        this.mToolbar.setNavigationContentDescription(this.mDefaultNavigationContentDescription);
      }
    }
    else {
      return;
    }
    this.mToolbar.setNavigationContentDescription(this.mHomeDescription);
  }
  
  private void updateNavigationIcon()
  {
    if ((this.mDisplayOpts & 0x4) != 0)
    {
      Toolbar localToolbar = this.mToolbar;
      if (this.mNavIcon != null) {}
      for (Drawable localDrawable = this.mNavIcon;; localDrawable = this.mDefaultNavigationIcon)
      {
        localToolbar.setNavigationIcon(localDrawable);
        return;
      }
    }
    this.mToolbar.setNavigationIcon(null);
  }
  
  private void updateToolbarLogo()
  {
    Drawable localDrawable = null;
    if ((this.mDisplayOpts & 0x2) != 0)
    {
      if ((this.mDisplayOpts & 0x1) == 0) {
        break label49;
      }
      if (this.mLogo == null) {
        break label41;
      }
      localDrawable = this.mLogo;
    }
    for (;;)
    {
      this.mToolbar.setLogo(localDrawable);
      return;
      label41:
      localDrawable = this.mIcon;
      continue;
      label49:
      localDrawable = this.mIcon;
    }
  }
  
  public Context getContext()
  {
    return this.mToolbar.getContext();
  }
  
  public void setCustomView(View paramView)
  {
    if ((this.mCustomView != null) && ((this.mDisplayOpts & 0x10) != 0)) {
      this.mToolbar.removeView(this.mCustomView);
    }
    this.mCustomView = paramView;
    if ((paramView != null) && ((this.mDisplayOpts & 0x10) != 0)) {
      this.mToolbar.addView(this.mCustomView);
    }
  }
  
  public void setDefaultNavigationContentDescription(int paramInt)
  {
    if (paramInt == this.mDefaultNavigationContentDescription) {
      return;
    }
    this.mDefaultNavigationContentDescription = paramInt;
    if (TextUtils.isEmpty(this.mToolbar.getNavigationContentDescription())) {
      setNavigationContentDescription(this.mDefaultNavigationContentDescription);
    }
  }
  
  public void setDisplayOptions(int paramInt)
  {
    int i = this.mDisplayOpts ^ paramInt;
    this.mDisplayOpts = paramInt;
    if (i != 0)
    {
      if ((i & 0x4) != 0)
      {
        if ((paramInt & 0x4) != 0) {
          updateHomeAccessibility();
        }
        updateNavigationIcon();
      }
      if ((i & 0x3) != 0) {
        updateToolbarLogo();
      }
      if ((i & 0x8) != 0)
      {
        if ((paramInt & 0x8) == 0) {
          break label115;
        }
        this.mToolbar.setTitle(this.mTitle);
        this.mToolbar.setSubtitle(this.mSubtitle);
      }
    }
    for (;;)
    {
      if (((i & 0x10) != 0) && (this.mCustomView != null))
      {
        if ((paramInt & 0x10) == 0) {
          break;
        }
        this.mToolbar.addView(this.mCustomView);
      }
      return;
      label115:
      this.mToolbar.setTitle(null);
      this.mToolbar.setSubtitle(null);
    }
    this.mToolbar.removeView(this.mCustomView);
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    this.mIcon = paramDrawable;
    updateToolbarLogo();
  }
  
  public void setLogo(Drawable paramDrawable)
  {
    this.mLogo = paramDrawable;
    updateToolbarLogo();
  }
  
  public void setNavigationContentDescription(int paramInt)
  {
    if (paramInt == 0) {}
    for (Object localObject = null;; localObject = getContext().getString(paramInt))
    {
      setNavigationContentDescription((CharSequence)localObject);
      return;
    }
  }
  
  public void setNavigationContentDescription(CharSequence paramCharSequence)
  {
    this.mHomeDescription = paramCharSequence;
    updateHomeAccessibility();
  }
  
  public void setNavigationIcon(Drawable paramDrawable)
  {
    this.mNavIcon = paramDrawable;
    updateNavigationIcon();
  }
  
  public void setSubtitle(CharSequence paramCharSequence)
  {
    this.mSubtitle = paramCharSequence;
    if ((this.mDisplayOpts & 0x8) != 0) {
      this.mToolbar.setSubtitle(paramCharSequence);
    }
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.mTitleSet = true;
    setTitleInt(paramCharSequence);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\ToolbarWidgetWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */