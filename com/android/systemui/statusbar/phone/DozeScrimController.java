package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Interpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.doze.DozeHost.PulseCallback;
import com.android.systemui.doze.DozeLog;

public class DozeScrimController
{
  private static final boolean DEBUG = Log.isLoggable("DozeScrimController", 3);
  private Animator mBehindAnimator;
  private float mBehindTarget;
  private final DozeParameters mDozeParameters;
  private boolean mDozing;
  private final Handler mHandler = new Handler();
  private Animator mInFrontAnimator;
  private float mInFrontTarget;
  private DozeHost.PulseCallback mPulseCallback;
  private final Runnable mPulseIn = new Runnable()
  {
    public void run()
    {
      if (DozeScrimController.-get0()) {
        Log.d("DozeScrimController", "Pulse in, mDozing=" + DozeScrimController.-get2(DozeScrimController.this) + " mPulseReason=" + DozeLog.pulseReasonToString(DozeScrimController.-get6(DozeScrimController.this)));
      }
      if (!DozeScrimController.-get2(DozeScrimController.this)) {
        return;
      }
      DozeLog.tracePulseStart(DozeScrimController.-get6(DozeScrimController.this));
      DozeScrimController.-wrap1(DozeScrimController.this);
    }
  };
  private final Runnable mPulseInFinished = new Runnable()
  {
    public void run()
    {
      if (DozeScrimController.-get0()) {
        Log.d("DozeScrimController", "Pulse in finished, mDozing=" + DozeScrimController.-get2(DozeScrimController.this));
      }
      if (!DozeScrimController.-get2(DozeScrimController.this)) {
        return;
      }
      if (DozeScrimController.-get6(DozeScrimController.this) == 1)
      {
        DozeScrimController.-get3(DozeScrimController.this).postDelayed(DozeScrimController.-get4(DozeScrimController.this), DozeScrimController.-get1(DozeScrimController.this).getPulseVisibleDuration(false));
        return;
      }
      DozeScrimController.-get3(DozeScrimController.this).postDelayed(DozeScrimController.-get4(DozeScrimController.this), DozeScrimController.-get1(DozeScrimController.this).getPulseVisibleDuration(true));
    }
  };
  private final Runnable mPulseOut = new Runnable()
  {
    public void run()
    {
      if (DozeScrimController.-get0()) {
        Log.d("DozeScrimController", "Pulse out, mDozing=" + DozeScrimController.-get2(DozeScrimController.this));
      }
      if (!DozeScrimController.-get2(DozeScrimController.this)) {
        return;
      }
      DozeScrimController.-wrap4(DozeScrimController.this, true, 1.0F, DozeScrimController.-get1(DozeScrimController.this).getPulseOutDuration(), Interpolators.ALPHA_IN, DozeScrimController.-get5(DozeScrimController.this));
    }
  };
  private final Runnable mPulseOutFinished = new Runnable()
  {
    public void run()
    {
      if (DozeScrimController.-get0()) {
        Log.d("DozeScrimController", "Pulse out finished");
      }
      DozeLog.tracePulseFinish();
      DozeScrimController.-wrap0(DozeScrimController.this);
    }
  };
  private int mPulseReason;
  private final ScrimController mScrimController;
  
  public DozeScrimController(ScrimController paramScrimController, Context paramContext)
  {
    this.mScrimController = paramScrimController;
    this.mDozeParameters = new DozeParameters(paramContext);
  }
  
  private void abortAnimations()
  {
    if (this.mInFrontAnimator != null) {
      this.mInFrontAnimator.cancel();
    }
    if (this.mBehindAnimator != null) {
      this.mBehindAnimator.cancel();
    }
  }
  
  private void cancelPulsing()
  {
    Log.d("DozeScrimController", "Cancel pulsing: " + this.mPulseCallback);
    if (this.mPulseCallback != null)
    {
      this.mHandler.removeCallbacks(this.mPulseIn);
      this.mHandler.removeCallbacks(this.mPulseOut);
      pulseFinished();
    }
  }
  
  private Animator getCurrentAnimator(boolean paramBoolean)
  {
    if (paramBoolean) {
      return this.mInFrontAnimator;
    }
    return this.mBehindAnimator;
  }
  
  private float getCurrentTarget(boolean paramBoolean)
  {
    if (paramBoolean) {
      return this.mInFrontTarget;
    }
    return this.mBehindTarget;
  }
  
  private float getDozeAlpha(boolean paramBoolean)
  {
    if (paramBoolean) {
      return this.mScrimController.getDozeInFrontAlpha();
    }
    return this.mScrimController.getDozeBehindAlpha();
  }
  
  private void pulseFinished()
  {
    if (this.mPulseCallback != null)
    {
      this.mPulseCallback.onPulseFinished();
      this.mPulseCallback = null;
    }
  }
  
  private void pulseStarted()
  {
    if (this.mPulseCallback != null) {
      this.mPulseCallback.onPulseStarted();
    }
  }
  
  private void setCurrentAnimator(boolean paramBoolean, Animator paramAnimator)
  {
    if (paramBoolean)
    {
      this.mInFrontAnimator = paramAnimator;
      return;
    }
    this.mBehindAnimator = paramAnimator;
  }
  
  private void setCurrentTarget(boolean paramBoolean, float paramFloat)
  {
    if (paramBoolean)
    {
      this.mInFrontTarget = paramFloat;
      return;
    }
    this.mBehindTarget = paramFloat;
  }
  
  private void setDozeAlpha(boolean paramBoolean, float paramFloat)
  {
    if (paramBoolean)
    {
      this.mScrimController.setDozeInFrontAlpha(paramFloat);
      return;
    }
    this.mScrimController.setDozeBehindAlpha(paramFloat);
  }
  
  private void startScrimAnimation(boolean paramBoolean, float paramFloat, long paramLong, Interpolator paramInterpolator)
  {
    startScrimAnimation(paramBoolean, paramFloat, paramLong, paramInterpolator, null);
  }
  
  private void startScrimAnimation(final boolean paramBoolean, float paramFloat, long paramLong, Interpolator paramInterpolator, final Runnable paramRunnable)
  {
    Object localObject = getCurrentAnimator(paramBoolean);
    if (localObject != null)
    {
      if (getCurrentTarget(paramBoolean) == paramFloat) {
        return;
      }
      ((Animator)localObject).cancel();
    }
    localObject = ValueAnimator.ofFloat(new float[] { getDozeAlpha(paramBoolean), paramFloat });
    ((ValueAnimator)localObject).addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        float f = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        DozeScrimController.-wrap3(DozeScrimController.this, paramBoolean, f);
      }
    });
    ((ValueAnimator)localObject).setInterpolator(paramInterpolator);
    ((ValueAnimator)localObject).setDuration(paramLong);
    ((ValueAnimator)localObject).addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        DozeScrimController.-wrap2(DozeScrimController.this, paramBoolean, null);
        if (paramRunnable != null) {
          paramRunnable.run();
        }
      }
    });
    ((ValueAnimator)localObject).start();
    setCurrentAnimator(paramBoolean, (Animator)localObject);
    setCurrentTarget(paramBoolean, paramFloat);
  }
  
  public void abortPulsing()
  {
    Log.d("DozeScrimController", "abortPulsing");
    cancelPulsing();
    if (this.mDozing)
    {
      this.mScrimController.setDozeBehindAlpha(1.0F);
      this.mScrimController.setDozeInFrontAlpha(1.0F);
    }
  }
  
  public boolean isPulsing()
  {
    return this.mPulseCallback != null;
  }
  
  public void onScreenTurnedOn()
  {
    int i;
    if (isPulsing())
    {
      if (this.mPulseReason == 3) {
        break label46;
      }
      if (this.mPulseReason != 4) {
        break label51;
      }
      i = 1;
      if (i == 0) {
        break label56;
      }
    }
    label46:
    label51:
    label56:
    for (Interpolator localInterpolator = Interpolators.LINEAR_OUT_SLOW_IN;; localInterpolator = Interpolators.ALPHA_OUT)
    {
      startScrimAnimation(true, 0.0F, 0L, localInterpolator, this.mPulseInFinished);
      return;
      i = 1;
      break;
      i = 0;
      break;
    }
  }
  
  public void pulse(DozeHost.PulseCallback paramPulseCallback, int paramInt)
  {
    if (paramPulseCallback == null) {
      throw new IllegalArgumentException("callback must not be null");
    }
    if ((!this.mDozing) || (this.mPulseCallback != null))
    {
      paramPulseCallback.onPulseFinished();
      return;
    }
    this.mPulseCallback = paramPulseCallback;
    this.mPulseReason = paramInt;
    this.mHandler.removeCallbacks(this.mPulseOut);
    this.mHandler.post(this.mPulseIn);
  }
  
  public void setDozing(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mDozing == paramBoolean1) {
      return;
    }
    Log.d("DozeScrimController", "setDozing: " + paramBoolean1);
    this.mDozing = paramBoolean1;
    if (this.mDozing)
    {
      abortAnimations();
      this.mScrimController.setDozeBehindAlpha(1.0F);
      this.mScrimController.setDozeInFrontAlpha(1.0F);
      return;
    }
    cancelPulsing();
    if (paramBoolean2)
    {
      startScrimAnimation(false, 0.0F, 700L, Interpolators.LINEAR_OUT_SLOW_IN);
      startScrimAnimation(true, 0.0F, 700L, Interpolators.LINEAR_OUT_SLOW_IN);
      return;
    }
    abortAnimations();
    this.mScrimController.setDozeBehindAlpha(0.0F);
    this.mScrimController.setDozeInFrontAlpha(0.0F);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\statusbar\phone\DozeScrimController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */