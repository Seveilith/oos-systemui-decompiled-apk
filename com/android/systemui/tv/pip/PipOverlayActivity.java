package com.android.systemui.tv.pip;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class PipOverlayActivity
  extends Activity
  implements PipManager.Listener
{
  private static boolean sActivityCreated;
  private Animator mFadeInAnimation;
  private Animator mFadeOutAnimation;
  private View mGuideOverlayView;
  private final Handler mHandler = new Handler();
  private final Runnable mHideGuideOverlayRunnable = new Runnable()
  {
    public void run()
    {
      PipOverlayActivity.-get0(PipOverlayActivity.this).start();
    }
  };
  private final PipManager mPipManager = PipManager.getInstance();
  
  static void showPipOverlay(Context paramContext)
  {
    if (!sActivityCreated)
    {
      Intent localIntent = new Intent(paramContext, PipOverlayActivity.class);
      localIntent.setFlags(268435456);
      ActivityOptions localActivityOptions = ActivityOptions.makeBasic();
      localActivityOptions.setLaunchStackId(4);
      paramContext.startActivity(localIntent, localActivityOptions.toBundle());
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    sActivityCreated = true;
    setContentView(2130968838);
    this.mGuideOverlayView = findViewById(2131952328);
    this.mPipManager.addListener(this);
    this.mFadeInAnimation = AnimatorInflater.loadAnimator(this, 2131034322);
    this.mFadeInAnimation.setTarget(this.mGuideOverlayView);
    this.mFadeOutAnimation = AnimatorInflater.loadAnimator(this, 2131034323);
    this.mFadeOutAnimation.setTarget(this.mGuideOverlayView);
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    sActivityCreated = false;
    this.mHandler.removeCallbacksAndMessages(null);
    this.mPipManager.removeListener(this);
    this.mPipManager.resumePipResizing(2);
  }
  
  public void onMoveToFullscreen()
  {
    finish();
  }
  
  public void onPipActivityClosed()
  {
    finish();
  }
  
  public void onPipEntered() {}
  
  public void onPipResizeAboutToStart()
  {
    finish();
    this.mPipManager.suspendPipResizing(2);
  }
  
  protected void onResume()
  {
    super.onResume();
    this.mFadeInAnimation.start();
    this.mHandler.removeCallbacks(this.mHideGuideOverlayRunnable);
    this.mHandler.postDelayed(this.mHideGuideOverlayRunnable, 4000L);
  }
  
  public void onShowPipMenu()
  {
    finish();
  }
  
  protected void onStop()
  {
    super.onStop();
    this.mHandler.removeCallbacks(this.mHideGuideOverlayRunnable);
    finish();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tv\pip\PipOverlayActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */