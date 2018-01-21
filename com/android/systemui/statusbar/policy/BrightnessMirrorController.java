package com.android.systemui.statusbar.policy;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout.LayoutParams;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.ScrimView;
import com.android.systemui.statusbar.phone.StatusBarWindowView;
import com.android.systemui.statusbar.stack.NotificationStackScrollLayout;
import com.android.systemui.util.MdmLogger;

public class BrightnessMirrorController
{
  public long TRANSITION_DURATION_IN = 200L;
  public long TRANSITION_DURATION_OUT = 150L;
  private View mBrightnessMirror;
  private final int[] mInt2Cache = new int[2];
  private final View mNotificationPanel;
  private final ScrimView mScrimBehind;
  private final NotificationStackScrollLayout mStackScroller;
  private final StatusBarWindowView mStatusBarWindow;
  
  public BrightnessMirrorController(StatusBarWindowView paramStatusBarWindowView)
  {
    this.mStatusBarWindow = paramStatusBarWindowView;
    this.mScrimBehind = ((ScrimView)paramStatusBarWindowView.findViewById(2131952302));
    this.mBrightnessMirror = paramStatusBarWindowView.findViewById(2131951797);
    this.mNotificationPanel = paramStatusBarWindowView.findViewById(2131952273);
    this.mStackScroller = ((NotificationStackScrollLayout)paramStatusBarWindowView.findViewById(2131952277));
  }
  
  private ViewPropertyAnimator inAnimation(ViewPropertyAnimator paramViewPropertyAnimator)
  {
    return paramViewPropertyAnimator.alpha(1.0F).setDuration(this.TRANSITION_DURATION_IN).setInterpolator(Interpolators.ALPHA_IN);
  }
  
  private ViewPropertyAnimator outAnimation(ViewPropertyAnimator paramViewPropertyAnimator)
  {
    return paramViewPropertyAnimator.alpha(0.0F).setDuration(this.TRANSITION_DURATION_OUT).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(null);
  }
  
  public View getMirror()
  {
    return this.mBrightnessMirror;
  }
  
  public void hideMirror()
  {
    this.mScrimBehind.animateViewAlpha(1.0F, this.TRANSITION_DURATION_IN, Interpolators.ALPHA_IN);
    inAnimation(this.mNotificationPanel.animate()).withLayer().withEndAction(new Runnable()
    {
      public void run()
      {
        BrightnessMirrorController.-get0(BrightnessMirrorController.this).setVisibility(4);
        BrightnessMirrorController.-get1(BrightnessMirrorController.this).setFadingOut(false);
      }
    });
  }
  
  public void onDensityOrFontScaleChanged()
  {
    int i = this.mStatusBarWindow.indexOfChild(this.mBrightnessMirror);
    this.mStatusBarWindow.removeView(this.mBrightnessMirror);
    this.mBrightnessMirror = LayoutInflater.from(this.mBrightnessMirror.getContext()).inflate(2130968606, this.mStatusBarWindow, false);
    this.mStatusBarWindow.addView(this.mBrightnessMirror, i);
  }
  
  public void setLocation(View paramView)
  {
    paramView.getLocationInWindow(this.mInt2Cache);
    int i = this.mInt2Cache[0];
    int j = paramView.getWidth() / 2;
    int k = this.mInt2Cache[1];
    int m = paramView.getHeight() / 2;
    this.mBrightnessMirror.setTranslationX(0.0F);
    this.mBrightnessMirror.setTranslationY(0.0F);
    this.mBrightnessMirror.getLocationInWindow(this.mInt2Cache);
    int n = this.mInt2Cache[0];
    int i1 = this.mBrightnessMirror.getWidth() / 2;
    int i2 = this.mInt2Cache[1];
    int i3 = this.mBrightnessMirror.getHeight() / 2;
    this.mBrightnessMirror.setTranslationX(i + j - (n + i1));
    this.mBrightnessMirror.setTranslationY(k + m - (i2 + i3));
  }
  
  public void showMirror()
  {
    MdmLogger.log("quick_bright", "manual", "1");
    this.mBrightnessMirror.setVisibility(0);
    this.mStackScroller.setFadingOut(true);
    this.mScrimBehind.animateViewAlpha(0.0F, this.TRANSITION_DURATION_OUT, Interpolators.ALPHA_OUT);
    outAnimation(this.mNotificationPanel.animate()).withLayer();
  }
  
  public void updateResources()
  {
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.mBrightnessMirror.getLayoutParams();
    localLayoutParams.width = this.mBrightnessMirror.getResources().getDimensionPixelSize(2131755401);
    localLayoutParams.gravity = this.mBrightnessMirror.getResources().getInteger(2131624023);
    this.mBrightnessMirror.setLayoutParams(localLayoutParams);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\policy\BrightnessMirrorController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */