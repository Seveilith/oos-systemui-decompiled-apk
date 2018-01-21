package com.android.systemui.volume;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.PathInterpolator;

public class VolumeDialogMotion
{
  private static final String TAG = Util.logTag(VolumeDialogMotion.class);
  private boolean mAnimating;
  private final Callback mCallback;
  private final View mChevron;
  private ValueAnimator mChevronPositionAnimator;
  private final ViewGroup mContents;
  private ValueAnimator mContentsPositionAnimator;
  private final Dialog mDialog;
  private final View mDialogView;
  private boolean mDismissing;
  private final Handler mHandler = new Handler();
  private boolean mShowing;
  
  public VolumeDialogMotion(Dialog paramDialog, View paramView1, ViewGroup paramViewGroup, View paramView2, Callback paramCallback)
  {
    this.mDialog = paramDialog;
    this.mDialogView = paramView1;
    this.mContents = paramViewGroup;
    this.mChevron = paramView2;
    this.mCallback = paramCallback;
    this.mDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
    {
      public void onDismiss(DialogInterface paramAnonymousDialogInterface)
      {
        if (D.BUG) {
          Log.d(VolumeDialogMotion.-get0(), "mDialog.onDismiss");
        }
      }
    });
    this.mDialog.setOnShowListener(new DialogInterface.OnShowListener()
    {
      public void onShow(DialogInterface paramAnonymousDialogInterface)
      {
        if (D.BUG) {
          Log.d(VolumeDialogMotion.-get0(), "mDialog.onShow");
        }
        int i = VolumeDialogMotion.-get5(VolumeDialogMotion.this).getHeight();
        VolumeDialogMotion.-get5(VolumeDialogMotion.this).setTranslationY(-i);
        VolumeDialogMotion.-wrap3(VolumeDialogMotion.this);
      }
    });
  }
  
  private int chevronDistance()
  {
    return this.mChevron.getHeight() / 6;
  }
  
  private int chevronPosY()
  {
    Object localObject = null;
    if (this.mChevron == null) {}
    while (localObject == null)
    {
      return 0;
      localObject = this.mChevron.getTag();
    }
    return ((Integer)localObject).intValue();
  }
  
  private static int scaledDuration(int paramInt)
  {
    return (int)(paramInt * 1.0F);
  }
  
  private void setDismissing(boolean paramBoolean)
  {
    if (paramBoolean == this.mDismissing) {
      return;
    }
    this.mDismissing = paramBoolean;
    if (D.BUG) {
      Log.d(TAG, "mDismissing = " + this.mDismissing);
    }
    updateAnimating();
  }
  
  private void setShowing(boolean paramBoolean)
  {
    if (paramBoolean == this.mShowing) {
      return;
    }
    this.mShowing = paramBoolean;
    if (D.BUG) {
      Log.d(TAG, "mShowing = " + this.mShowing);
    }
    updateAnimating();
  }
  
  private void startShowAnimation()
  {
    if (D.BUG) {
      Log.d(TAG, "startShowAnimation");
    }
    this.mDialogView.animate().translationY(0.0F).setDuration(scaledDuration(300)).setInterpolator(new LogDecelerateInterpolator(null)).setListener(null).setUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        if (VolumeDialogMotion.-get2(VolumeDialogMotion.this) == null) {
          return;
        }
        float f = ((Float)VolumeDialogMotion.-get2(VolumeDialogMotion.this).getAnimatedValue()).floatValue();
        int i = VolumeDialogMotion.-wrap0(VolumeDialogMotion.this);
        VolumeDialogMotion.-get1(VolumeDialogMotion.this).setTranslationY(i + f + -VolumeDialogMotion.-get5(VolumeDialogMotion.this).getTranslationY());
      }
    }).start();
    this.mContentsPositionAnimator = ValueAnimator.ofFloat(new float[] { -chevronDistance(), 0.0F }).setDuration(scaledDuration(400));
    this.mContentsPositionAnimator.addListener(new AnimatorListenerAdapter()
    {
      private boolean mCancelled;
      
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        if (D.BUG) {
          Log.d(VolumeDialogMotion.-get0(), "show.onAnimationCancel");
        }
        this.mCancelled = true;
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if (this.mCancelled) {
          return;
        }
        if (D.BUG) {
          Log.d(VolumeDialogMotion.-get0(), "show.onAnimationEnd");
        }
        VolumeDialogMotion.-wrap2(VolumeDialogMotion.this, false);
      }
    });
    this.mContentsPositionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        float f = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        VolumeDialogMotion.-get3(VolumeDialogMotion.this).setTranslationY(-VolumeDialogMotion.-get5(VolumeDialogMotion.this).getTranslationY() + f);
      }
    });
    this.mContentsPositionAnimator.setInterpolator(new LogDecelerateInterpolator(null));
    this.mContentsPositionAnimator.start();
    this.mContents.setAlpha(0.0F);
    this.mContents.animate().alpha(1.0F).setDuration(scaledDuration(150)).setInterpolator(new PathInterpolator(0.0F, 0.0F, 0.2F, 1.0F)).start();
    this.mChevronPositionAnimator = ValueAnimator.ofFloat(new float[] { -chevronDistance(), 0.0F }).setDuration(scaledDuration(250));
    this.mChevronPositionAnimator.setInterpolator(new PathInterpolator(0.4F, 0.0F, 0.2F, 1.0F));
    this.mChevronPositionAnimator.start();
    this.mChevron.setAlpha(0.0F);
    this.mChevron.animate().alpha(1.0F).setStartDelay(scaledDuration(50)).setDuration(scaledDuration(150)).setInterpolator(new PathInterpolator(0.4F, 0.0F, 1.0F, 1.0F)).start();
  }
  
  private void updateAnimating()
  {
    if (!this.mShowing) {}
    for (boolean bool = this.mDismissing; bool == this.mAnimating; bool = true) {
      return;
    }
    this.mAnimating = bool;
    if (D.BUG) {
      Log.d(TAG, "mAnimating = " + this.mAnimating);
    }
    if (this.mCallback != null) {
      this.mCallback.onAnimatingChanged(this.mAnimating);
    }
  }
  
  public boolean isAnimating()
  {
    return this.mAnimating;
  }
  
  public void startDismiss(final Runnable paramRunnable)
  {
    if (D.BUG) {
      Log.d(TAG, "startDismiss");
    }
    if (this.mDismissing) {
      return;
    }
    setDismissing(true);
    if (this.mShowing)
    {
      this.mDialogView.animate().cancel();
      if (this.mContentsPositionAnimator != null) {
        this.mContentsPositionAnimator.cancel();
      }
      this.mContents.animate().cancel();
      if (this.mChevronPositionAnimator != null) {
        this.mChevronPositionAnimator.cancel();
      }
      this.mChevron.animate().cancel();
      setShowing(false);
    }
    this.mDialogView.animate().translationY(-this.mDialogView.getHeight()).setDuration(scaledDuration(250)).setInterpolator(new LogAccelerateInterpolator(null)).setUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        VolumeDialogMotion.-get3(VolumeDialogMotion.this).setTranslationY(-VolumeDialogMotion.-get5(VolumeDialogMotion.this).getTranslationY());
        int i = VolumeDialogMotion.-wrap0(VolumeDialogMotion.this);
        VolumeDialogMotion.-get1(VolumeDialogMotion.this).setTranslationY(i + -VolumeDialogMotion.-get5(VolumeDialogMotion.this).getTranslationY());
      }
    }).setListener(new AnimatorListenerAdapter()
    {
      private boolean mCancelled;
      
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        if (D.BUG) {
          Log.d(VolumeDialogMotion.-get0(), "dismiss.onAnimationCancel");
        }
        this.mCancelled = true;
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if (this.mCancelled) {
          return;
        }
        if (D.BUG) {
          Log.d(VolumeDialogMotion.-get0(), "dismiss.onAnimationEnd");
        }
        VolumeDialogMotion.-get6(VolumeDialogMotion.this).postDelayed(new Runnable()
        {
          public void run()
          {
            if (D.BUG) {
              Log.d(VolumeDialogMotion.-get0(), "mDialog.dismiss()");
            }
            VolumeDialogMotion.-get4(VolumeDialogMotion.this).dismiss();
            this.val$onComplete.run();
            VolumeDialogMotion.-wrap1(VolumeDialogMotion.this, false);
          }
        }, 50L);
      }
    }).start();
  }
  
  public void startShow()
  {
    if (D.BUG) {
      Log.d(TAG, "startShow");
    }
    if (this.mShowing) {
      return;
    }
    setShowing(true);
    if (this.mDismissing)
    {
      this.mDialogView.animate().cancel();
      setDismissing(false);
      startShowAnimation();
      return;
    }
    if (D.BUG) {
      Log.d(TAG, "mDialog.show()");
    }
    this.mDialog.show();
  }
  
  public static abstract interface Callback
  {
    public abstract void onAnimatingChanged(boolean paramBoolean);
  }
  
  private static final class LogAccelerateInterpolator
    implements TimeInterpolator
  {
    private final int mBase;
    private final int mDrift;
    private final float mLogScale;
    
    private LogAccelerateInterpolator()
    {
      this(100, 0);
    }
    
    private LogAccelerateInterpolator(int paramInt1, int paramInt2)
    {
      this.mBase = paramInt1;
      this.mDrift = paramInt2;
      this.mLogScale = (1.0F / computeLog(1.0F, this.mBase, this.mDrift));
    }
    
    private static float computeLog(float paramFloat, int paramInt1, int paramInt2)
    {
      return (float)-Math.pow(paramInt1, -paramFloat) + 1.0F + paramInt2 * paramFloat;
    }
    
    public float getInterpolation(float paramFloat)
    {
      return 1.0F - computeLog(1.0F - paramFloat, this.mBase, this.mDrift) * this.mLogScale;
    }
  }
  
  private static final class LogDecelerateInterpolator
    implements TimeInterpolator
  {
    private final float mBase;
    private final float mDrift;
    private final float mOutputScale;
    private final float mTimeScale;
    
    private LogDecelerateInterpolator()
    {
      this(400.0F, 1.4F, 0.0F);
    }
    
    private LogDecelerateInterpolator(float paramFloat1, float paramFloat2, float paramFloat3)
    {
      this.mBase = paramFloat1;
      this.mDrift = paramFloat3;
      this.mTimeScale = (1.0F / paramFloat2);
      this.mOutputScale = (1.0F / computeLog(1.0F));
    }
    
    private float computeLog(float paramFloat)
    {
      return 1.0F - (float)Math.pow(this.mBase, -paramFloat * this.mTimeScale) + this.mDrift * paramFloat;
    }
    
    public float getInterpolation(float paramFloat)
    {
      return computeLog(paramFloat) * this.mOutputScale;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\volume\VolumeDialogMotion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */