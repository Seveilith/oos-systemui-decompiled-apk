package com.android.systemui.tv.pip;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class PipOnboardingActivity
  extends Activity
  implements PipManager.Listener
{
  private AnimatorSet mEnterAnimator;
  private final PipManager mPipManager = PipManager.getInstance();
  
  private Animator loadAnimator(int paramInt1, int paramInt2)
  {
    Animator localAnimator = AnimatorInflater.loadAnimator(this, paramInt2);
    localAnimator.setTarget(findViewById(paramInt1));
    return localAnimator;
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968837);
    findViewById(2131951936).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PipOnboardingActivity.this.finish();
      }
    });
    this.mPipManager.addListener(this);
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    this.mPipManager.removeListener(this);
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (this.mEnterAnimator.isStarted()) {
      return true;
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if (this.mEnterAnimator.isStarted()) {
      return true;
    }
    return super.onKeyUp(paramInt, paramKeyEvent);
  }
  
  public void onMoveToFullscreen()
  {
    finish();
  }
  
  public void onPause()
  {
    super.onPause();
    finish();
  }
  
  public void onPipActivityClosed()
  {
    finish();
  }
  
  public void onPipEntered() {}
  
  public void onPipResizeAboutToStart() {}
  
  public void onResume()
  {
    super.onResume();
    this.mEnterAnimator = new AnimatorSet();
    this.mEnterAnimator.playTogether(new Animator[] { loadAnimator(2131952030, 2131034317), loadAnimator(2131952326, 2131034320), loadAnimator(2131952327, 2131034320), loadAnimator(2131951748, 2131034321), loadAnimator(2131951995, 2131034319), loadAnimator(2131951936, 2131034318) });
    this.mEnterAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        ((AnimationDrawable)((ImageView)PipOnboardingActivity.this.findViewById(2131952327)).getDrawable()).start();
      }
    });
    int i = getResources().getInteger(2131624030);
    this.mEnterAnimator.setStartDelay(i);
    this.mEnterAnimator.start();
  }
  
  public void onShowPipMenu()
  {
    finish();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\tv\pip\PipOnboardingActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */