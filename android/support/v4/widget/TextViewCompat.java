package android.support.v4.widget;

import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.widget.TextView;

public final class TextViewCompat
{
  static final TextViewCompatImpl IMPL = new BaseTextViewCompatImpl();
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    if (i >= 23)
    {
      IMPL = new Api23TextViewCompatImpl();
      return;
    }
    if (i >= 18)
    {
      IMPL = new JbMr2TextViewCompatImpl();
      return;
    }
    if (i >= 17)
    {
      IMPL = new JbMr1TextViewCompatImpl();
      return;
    }
    if (i >= 16)
    {
      IMPL = new JbTextViewCompatImpl();
      return;
    }
  }
  
  public static void setTextAppearance(@NonNull TextView paramTextView, @StyleRes int paramInt)
  {
    IMPL.setTextAppearance(paramTextView, paramInt);
  }
  
  static class Api23TextViewCompatImpl
    extends TextViewCompat.JbMr2TextViewCompatImpl
  {
    public void setTextAppearance(@NonNull TextView paramTextView, @StyleRes int paramInt)
    {
      TextViewCompatApi23.setTextAppearance(paramTextView, paramInt);
    }
  }
  
  static class BaseTextViewCompatImpl
    implements TextViewCompat.TextViewCompatImpl
  {
    public void setTextAppearance(TextView paramTextView, @StyleRes int paramInt)
    {
      TextViewCompatGingerbread.setTextAppearance(paramTextView, paramInt);
    }
  }
  
  static class JbMr1TextViewCompatImpl
    extends TextViewCompat.JbTextViewCompatImpl
  {}
  
  static class JbMr2TextViewCompatImpl
    extends TextViewCompat.JbMr1TextViewCompatImpl
  {}
  
  static class JbTextViewCompatImpl
    extends TextViewCompat.BaseTextViewCompatImpl
  {}
  
  static abstract interface TextViewCompatImpl
  {
    public abstract void setTextAppearance(@NonNull TextView paramTextView, @StyleRes int paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\widget\TextViewCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */