package com.android.systemui.stackdivider;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class DividerWindowManager
{
  private WindowManager.LayoutParams mLp;
  private View mView;
  private final WindowManager mWindowManager;
  
  public DividerWindowManager(Context paramContext)
  {
    this.mWindowManager = ((WindowManager)paramContext.getSystemService(WindowManager.class));
  }
  
  public void add(View paramView, int paramInt1, int paramInt2)
  {
    this.mLp = new WindowManager.LayoutParams(paramInt1, paramInt2, 2034, 545521704, -3);
    this.mLp.setTitle("DockedStackDivider");
    WindowManager.LayoutParams localLayoutParams = this.mLp;
    localLayoutParams.privateFlags |= 0x40;
    paramView.setSystemUiVisibility(1792);
    this.mWindowManager.addView(paramView, this.mLp);
    this.mView = paramView;
  }
  
  public void remove()
  {
    if (this.mView != null) {
      this.mWindowManager.removeView(this.mView);
    }
    this.mView = null;
  }
  
  public void setSlippery(boolean paramBoolean)
  {
    int j = 0;
    WindowManager.LayoutParams localLayoutParams;
    int i;
    if ((paramBoolean) && ((this.mLp.flags & 0x20000000) == 0))
    {
      localLayoutParams = this.mLp;
      localLayoutParams.flags |= 0x20000000;
      i = 1;
    }
    for (;;)
    {
      if (i != 0) {
        this.mWindowManager.updateViewLayout(this.mView, this.mLp);
      }
      return;
      i = j;
      if (!paramBoolean)
      {
        i = j;
        if ((this.mLp.flags & 0x20000000) != 0)
        {
          localLayoutParams = this.mLp;
          localLayoutParams.flags &= 0xDFFFFFFF;
          i = 1;
        }
      }
    }
  }
  
  public void setTouchable(boolean paramBoolean)
  {
    int j = 0;
    WindowManager.LayoutParams localLayoutParams;
    int i;
    if ((!paramBoolean) && ((this.mLp.flags & 0x10) == 0))
    {
      localLayoutParams = this.mLp;
      localLayoutParams.flags |= 0x10;
      i = 1;
    }
    for (;;)
    {
      if (i != 0) {
        this.mWindowManager.updateViewLayout(this.mView, this.mLp);
      }
      return;
      i = j;
      if (paramBoolean)
      {
        i = j;
        if ((this.mLp.flags & 0x10) != 0)
        {
          localLayoutParams = this.mLp;
          localLayoutParams.flags &= 0xFFFFFFEF;
          i = 1;
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\stackdivider\DividerWindowManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */