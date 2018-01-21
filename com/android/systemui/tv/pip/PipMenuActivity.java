package com.android.systemui.tv.pip;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class PipMenuActivity
  extends Activity
  implements PipManager.Listener
{
  private Animator mFadeInAnimation;
  private Animator mFadeOutAnimation;
  private View mPipControlsView;
  private final PipManager mPipManager = PipManager.getInstance();
  private boolean mRestorePipSizeWhenClose;
  
  private void restorePipAndFinish()
  {
    if (this.mRestorePipSizeWhenClose) {
      this.mPipManager.resizePinnedStack(1);
    }
    finish();
  }
  
  public void onBackPressed()
  {
    restorePipAndFinish();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968836);
    this.mPipManager.addListener(this);
    this.mRestorePipSizeWhenClose = true;
    this.mPipControlsView = ((PipControlsView)findViewById(2131952324));
    this.mFadeInAnimation = AnimatorInflater.loadAnimator(this, 2131034315);
    this.mFadeInAnimation.setTarget(this.mPipControlsView);
    this.mFadeOutAnimation = AnimatorInflater.loadAnimator(this, 2131034316);
    this.mFadeOutAnimation.setTarget(this.mPipControlsView);
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    this.mPipManager.removeListener(this);
    this.mPipManager.resumePipResizing(1);
  }
  
  public void onMoveToFullscreen()
  {
    this.mRestorePipSizeWhenClose = false;
    finish();
  }
  
  public void onPause()
  {
    super.onPause();
    this.mFadeOutAnimation.start();
    restorePipAndFinish();
  }
  
  public void onPipActivityClosed()
  {
    finish();
  }
  
  public void onPipEntered() {}
  
  public void onPipResizeAboutToStart()
  {
    finish();
    this.mPipManager.suspendPipResizing(1);
  }
  
  public void onResume()
  {
    super.onResume();
    this.mFadeInAnimation.start();
  }
  
  public void onShowPipMenu() {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tv\pip\PipMenuActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */