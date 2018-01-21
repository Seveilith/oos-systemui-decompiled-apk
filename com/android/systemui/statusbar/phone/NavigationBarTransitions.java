package com.android.systemui.statusbar.phone;

import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewPropertyAnimator;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.IStatusBarService.Stub;

public final class NavigationBarTransitions
  extends BarTransitions
{
  private final IStatusBarService mBarService;
  private boolean mLightsOut;
  private final View.OnTouchListener mLightsOutListener = new View.OnTouchListener()
  {
    public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
    {
      if (paramAnonymousMotionEvent.getAction() == 0) {
        NavigationBarTransitions.-wrap0(NavigationBarTransitions.this, false, false, false);
      }
      try
      {
        NavigationBarTransitions.-get0(NavigationBarTransitions.this).setSystemUiVisibility(0, 1, "LightsOutListener");
        return false;
      }
      catch (RemoteException paramAnonymousView) {}
      return false;
    }
  };
  private final NavigationBarView mView;
  
  public NavigationBarTransitions(NavigationBarView paramNavigationBarView)
  {
    super(paramNavigationBarView, 2130838059);
    this.mView = paramNavigationBarView;
    this.mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
  }
  
  private void applyLightsOut(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if ((!paramBoolean3) && (paramBoolean1 == this.mLightsOut)) {
      return;
    }
    this.mLightsOut = paramBoolean1;
    View localView = this.mView.getCurrentView().findViewById(2131951802);
    localView.animate().cancel();
    if (paramBoolean1) {}
    for (float f = 0.5F; !paramBoolean2; f = 1.0F)
    {
      localView.setAlpha(f);
      return;
    }
    if (paramBoolean1) {}
    for (int i = 750;; i = 250)
    {
      localView.animate().alpha(f).setDuration(i).start();
      return;
    }
  }
  
  private void applyMode(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    Log.d("NavigationBarTransitions", "applyMode mode: " + paramInt);
    applyLightsOut(isLightsOut(paramInt), paramBoolean1, paramBoolean2);
  }
  
  public void init()
  {
    applyModeBackground(-1, getMode(), false);
    applyMode(getMode(), false, true);
  }
  
  protected void onTransition(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super.onTransition(paramInt1, paramInt2, paramBoolean);
    applyMode(paramInt2, paramBoolean, false);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\NavigationBarTransitions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */