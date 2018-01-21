package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.android.settingslib.drawable.UserIconDrawable;
import com.android.systemui.R.styleable;

public class UserAvatarView
  extends View
{
  private final UserIconDrawable mDrawable = new UserIconDrawable();
  
  public UserAvatarView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public UserAvatarView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public UserAvatarView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public UserAvatarView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.UserAvatarView, paramInt1, paramInt2);
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
        setAvatarPadding(paramContext.getDimension(i, 0.0F));
        continue;
        setFrameWidth(paramContext.getDimension(i, 0.0F));
        continue;
        setFramePadding(paramContext.getDimension(i, 0.0F));
        continue;
        setFrameColor(paramContext.getColorStateList(i));
        continue;
        setBadgeDiameter(paramContext.getDimension(i, 0.0F));
        continue;
        setBadgeMargin(paramContext.getDimension(i, 0.0F));
      }
    }
    paramContext.recycle();
    setBackground(this.mDrawable);
  }
  
  public void setAvatarPadding(float paramFloat)
  {
    this.mDrawable.setPadding(paramFloat);
  }
  
  public void setAvatarWithBadge(Bitmap paramBitmap, int paramInt)
  {
    this.mDrawable.setIcon(paramBitmap);
    this.mDrawable.setBadgeIfManagedUser(getContext(), paramInt);
  }
  
  public void setBadgeDiameter(float paramFloat)
  {
    this.mDrawable.setBadgeRadius(0.5F * paramFloat);
  }
  
  public void setBadgeMargin(float paramFloat)
  {
    this.mDrawable.setBadgeMargin(paramFloat);
  }
  
  public void setDrawableWithBadge(Drawable paramDrawable, int paramInt)
  {
    if ((paramDrawable instanceof UserIconDrawable)) {
      throw new RuntimeException("Recursively adding UserIconDrawable");
    }
    this.mDrawable.setIconDrawable(paramDrawable);
    this.mDrawable.setBadgeIfManagedUser(getContext(), paramInt);
  }
  
  public void setFrameColor(ColorStateList paramColorStateList)
  {
    this.mDrawable.setFrameColor(paramColorStateList);
  }
  
  public void setFramePadding(float paramFloat)
  {
    this.mDrawable.setFramePadding(paramFloat);
  }
  
  public void setFrameWidth(float paramFloat)
  {
    this.mDrawable.setFrameWidth(paramFloat);
  }
  
  public void setTintList(ColorStateList paramColorStateList)
  {
    this.mDrawable.setTintList(paramColorStateList);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\UserAvatarView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */