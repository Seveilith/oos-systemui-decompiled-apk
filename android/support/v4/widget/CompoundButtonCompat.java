package android.support.v4.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.CompoundButton;

public final class CompoundButtonCompat
{
  private static final CompoundButtonCompatImpl IMPL = new BaseCompoundButtonCompat();
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    if (i >= 23)
    {
      IMPL = new Api23CompoundButtonImpl();
      return;
    }
    if (i >= 21)
    {
      IMPL = new LollipopCompoundButtonImpl();
      return;
    }
  }
  
  @Nullable
  public static Drawable getButtonDrawable(@NonNull CompoundButton paramCompoundButton)
  {
    return IMPL.getButtonDrawable(paramCompoundButton);
  }
  
  public static void setButtonTintList(@NonNull CompoundButton paramCompoundButton, @Nullable ColorStateList paramColorStateList)
  {
    IMPL.setButtonTintList(paramCompoundButton, paramColorStateList);
  }
  
  public static void setButtonTintMode(@NonNull CompoundButton paramCompoundButton, @Nullable PorterDuff.Mode paramMode)
  {
    IMPL.setButtonTintMode(paramCompoundButton, paramMode);
  }
  
  static class Api23CompoundButtonImpl
    extends CompoundButtonCompat.LollipopCompoundButtonImpl
  {
    public Drawable getButtonDrawable(CompoundButton paramCompoundButton)
    {
      return CompoundButtonCompatApi23.getButtonDrawable(paramCompoundButton);
    }
  }
  
  static class BaseCompoundButtonCompat
    implements CompoundButtonCompat.CompoundButtonCompatImpl
  {
    public Drawable getButtonDrawable(CompoundButton paramCompoundButton)
    {
      return CompoundButtonCompatGingerbread.getButtonDrawable(paramCompoundButton);
    }
    
    public void setButtonTintList(CompoundButton paramCompoundButton, ColorStateList paramColorStateList)
    {
      CompoundButtonCompatGingerbread.setButtonTintList(paramCompoundButton, paramColorStateList);
    }
    
    public void setButtonTintMode(CompoundButton paramCompoundButton, PorterDuff.Mode paramMode)
    {
      CompoundButtonCompatGingerbread.setButtonTintMode(paramCompoundButton, paramMode);
    }
  }
  
  static abstract interface CompoundButtonCompatImpl
  {
    public abstract Drawable getButtonDrawable(CompoundButton paramCompoundButton);
    
    public abstract void setButtonTintList(CompoundButton paramCompoundButton, ColorStateList paramColorStateList);
    
    public abstract void setButtonTintMode(CompoundButton paramCompoundButton, PorterDuff.Mode paramMode);
  }
  
  static class LollipopCompoundButtonImpl
    extends CompoundButtonCompat.BaseCompoundButtonCompat
  {
    public void setButtonTintList(CompoundButton paramCompoundButton, ColorStateList paramColorStateList)
    {
      CompoundButtonCompatLollipop.setButtonTintList(paramCompoundButton, paramColorStateList);
    }
    
    public void setButtonTintMode(CompoundButton paramCompoundButton, PorterDuff.Mode paramMode)
    {
      CompoundButtonCompatLollipop.setButtonTintMode(paramCompoundButton, paramMode);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\widget\CompoundButtonCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */