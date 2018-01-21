package com.android.systemui.statusbar;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class EmptyShadeView
  extends StackScrollerDecorView
{
  public EmptyShadeView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  protected View findContentView()
  {
    return findViewById(2131952281);
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    ((TextView)findViewById(2131952281)).setText(2131690401);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\EmptyShadeView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */