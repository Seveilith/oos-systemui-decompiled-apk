package android.support.v7.widget;

import android.content.res.Resources.Theme;
import android.support.annotation.Nullable;
import android.widget.SpinnerAdapter;

public abstract interface ThemedSpinnerAdapter
  extends SpinnerAdapter
{
  @Nullable
  public abstract Resources.Theme getDropDownViewTheme();
  
  public abstract void setDropDownViewTheme(@Nullable Resources.Theme paramTheme);
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\ThemedSpinnerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */