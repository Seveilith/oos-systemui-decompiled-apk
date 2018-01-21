package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.util.ArrayUtils;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.R.styleable;
import com.android.systemui.qs.QSIconView;
import com.android.systemui.qs.QSTileView;
import com.android.systemui.statusbar.phone.UserAvatarView;

public class UserDetailItemView
  extends LinearLayout
{
  private Typeface mActivatedTypeface;
  private UserAvatarView mAvatar;
  private TextView mName;
  private Typeface mRegularTypeface;
  private View mRestrictedPadlock;
  
  public UserDetailItemView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public UserDetailItemView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public UserDetailItemView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public UserDetailItemView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.UserDetailItemView, paramInt1, paramInt2);
    paramInt2 = paramContext.getIndexCount();
    paramInt1 = 0;
    if (paramInt1 < paramInt2)
    {
      int i = paramContext.getIndex(paramInt1);
      switch (i)
      {
      }
      for (;;)
      {
        paramInt1 += 1;
        break;
        this.mRegularTypeface = Typeface.create(paramContext.getString(i), 0);
        continue;
        this.mActivatedTypeface = Typeface.create(paramContext.getString(i), 0);
      }
    }
    paramContext.recycle();
  }
  
  public static UserDetailItemView convertOrInflate(Context paramContext, View paramView, ViewGroup paramViewGroup)
  {
    View localView = paramView;
    if (!(paramView instanceof UserDetailItemView)) {
      localView = LayoutInflater.from(paramContext).inflate(2130968783, paramViewGroup, false);
    }
    return (UserDetailItemView)localView;
  }
  
  private void updateTypeface()
  {
    boolean bool = ArrayUtils.contains(getDrawableState(), 16843518);
    TextView localTextView = this.mName;
    if (bool) {}
    for (Typeface localTypeface = this.mActivatedTypeface;; localTypeface = this.mRegularTypeface)
    {
      localTextView.setTypeface(localTypeface);
      return;
    }
  }
  
  public void bind(String paramString, Bitmap paramBitmap, int paramInt)
  {
    this.mName.setText(paramString);
    this.mAvatar.setAvatarWithBadge(paramBitmap, paramInt);
  }
  
  public void bind(String paramString, Drawable paramDrawable, int paramInt)
  {
    this.mName.setText(paramString);
    this.mAvatar.setDrawableWithBadge(paramDrawable, paramInt);
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    updateTypeface();
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    FontSizeUtils.updateFontSize(this.mName, 2131755718);
  }
  
  protected void onFinishInflate()
  {
    this.mAvatar = ((UserAvatarView)findViewById(2131951927));
    this.mName = ((TextView)findViewById(2131951800));
    if (this.mRegularTypeface == null) {
      this.mRegularTypeface = this.mName.getTypeface();
    }
    if (this.mActivatedTypeface == null) {
      this.mActivatedTypeface = this.mName.getTypeface();
    }
    updateTypeface();
    this.mRestrictedPadlock = findViewById(2131952161);
  }
  
  public void setAvatarEnabled(boolean paramBoolean)
  {
    this.mAvatar.setEnabled(paramBoolean);
  }
  
  public void setDisabledByAdmin(boolean paramBoolean)
  {
    boolean bool2 = false;
    Object localObject = this.mRestrictedPadlock;
    int i;
    boolean bool1;
    if (paramBoolean)
    {
      i = 0;
      ((View)localObject).setVisibility(i);
      localObject = this.mName;
      if (!paramBoolean) {
        break label65;
      }
      bool1 = false;
      label33:
      ((TextView)localObject).setEnabled(bool1);
      localObject = this.mAvatar;
      if (!paramBoolean) {
        break label70;
      }
    }
    label65:
    label70:
    for (paramBoolean = bool2;; paramBoolean = true)
    {
      ((UserAvatarView)localObject).setEnabled(paramBoolean);
      return;
      i = 8;
      break;
      bool1 = true;
      break label33;
    }
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    this.mName.setEnabled(paramBoolean);
    this.mAvatar.setEnabled(paramBoolean);
  }
  
  public void updateThemeColor(boolean paramBoolean)
  {
    this.mName.setTextColor(QSTileView.sTextColor);
    if (paramBoolean)
    {
      this.mAvatar.setTintList(ColorStateList.valueOf(QSIconView.sIconColor));
      return;
    }
    this.mAvatar.setTintList(null);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\qs\tiles\UserDetailItemView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */