package com.android.systemui.recents.views;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.util.SparseArray;
import android.util.SparseLongArray;
import android.view.animation.Interpolator;
import com.android.systemui.Interpolators;
import java.util.List;

public class AnimationProps
{
  public static final AnimationProps IMMEDIATE = new AnimationProps(0, Interpolators.LINEAR);
  private Animator.AnimatorListener mListener;
  private SparseLongArray mPropDuration;
  private SparseLongArray mPropInitialPlayTime;
  private SparseArray<Interpolator> mPropInterpolators;
  private SparseLongArray mPropStartDelay;
  
  public AnimationProps() {}
  
  public AnimationProps(int paramInt1, int paramInt2, Interpolator paramInterpolator)
  {
    this(paramInt1, paramInt2, paramInterpolator, null);
  }
  
  public AnimationProps(int paramInt1, int paramInt2, Interpolator paramInterpolator, Animator.AnimatorListener paramAnimatorListener)
  {
    setStartDelay(0, paramInt1);
    setDuration(0, paramInt2);
    setInterpolator(0, paramInterpolator);
    setListener(paramAnimatorListener);
  }
  
  public AnimationProps(int paramInt, Interpolator paramInterpolator)
  {
    this(0, paramInt, paramInterpolator, null);
  }
  
  public AnimationProps(int paramInt, Interpolator paramInterpolator, Animator.AnimatorListener paramAnimatorListener)
  {
    this(0, paramInt, paramInterpolator, paramAnimatorListener);
  }
  
  public <T extends ValueAnimator> T apply(int paramInt, T paramT)
  {
    paramT.setStartDelay(getStartDelay(paramInt));
    paramT.setDuration(getDuration(paramInt));
    paramT.setInterpolator(getInterpolator(paramInt));
    long l = getInitialPlayTime(paramInt);
    if (l != 0L) {
      paramT.setCurrentPlayTime(l);
    }
    return paramT;
  }
  
  public AnimatorSet createAnimator(List<Animator> paramList)
  {
    AnimatorSet localAnimatorSet = new AnimatorSet();
    if (this.mListener != null) {
      localAnimatorSet.addListener(this.mListener);
    }
    localAnimatorSet.playTogether(paramList);
    return localAnimatorSet;
  }
  
  public long getDuration(int paramInt)
  {
    if (this.mPropDuration != null)
    {
      long l = this.mPropDuration.get(paramInt, -1L);
      if (l != -1L) {
        return l;
      }
      return this.mPropDuration.get(0, 0L);
    }
    return 0L;
  }
  
  public long getInitialPlayTime(int paramInt)
  {
    if (this.mPropInitialPlayTime != null)
    {
      if (this.mPropInitialPlayTime.indexOfKey(paramInt) != -1) {
        return this.mPropInitialPlayTime.get(paramInt);
      }
      return this.mPropInitialPlayTime.get(0, 0L);
    }
    return 0L;
  }
  
  public Interpolator getInterpolator(int paramInt)
  {
    if (this.mPropInterpolators != null)
    {
      Interpolator localInterpolator = (Interpolator)this.mPropInterpolators.get(paramInt);
      if (localInterpolator != null) {
        return localInterpolator;
      }
      return (Interpolator)this.mPropInterpolators.get(0, Interpolators.LINEAR);
    }
    return Interpolators.LINEAR;
  }
  
  public Animator.AnimatorListener getListener()
  {
    return this.mListener;
  }
  
  public long getStartDelay(int paramInt)
  {
    if (this.mPropStartDelay != null)
    {
      long l = this.mPropStartDelay.get(paramInt, -1L);
      if (l != -1L) {
        return l;
      }
      return this.mPropStartDelay.get(0, 0L);
    }
    return 0L;
  }
  
  public boolean isImmediate()
  {
    int j = this.mPropDuration.size();
    int i = 0;
    while (i < j)
    {
      if (this.mPropDuration.valueAt(i) > 0L) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public AnimationProps setDuration(int paramInt1, int paramInt2)
  {
    if (this.mPropDuration == null) {
      this.mPropDuration = new SparseLongArray();
    }
    this.mPropDuration.append(paramInt1, paramInt2);
    return this;
  }
  
  public AnimationProps setInitialPlayTime(int paramInt1, int paramInt2)
  {
    if (this.mPropInitialPlayTime == null) {
      this.mPropInitialPlayTime = new SparseLongArray();
    }
    this.mPropInitialPlayTime.append(paramInt1, paramInt2);
    return this;
  }
  
  public AnimationProps setInterpolator(int paramInt, Interpolator paramInterpolator)
  {
    if (this.mPropInterpolators == null) {
      this.mPropInterpolators = new SparseArray();
    }
    this.mPropInterpolators.append(paramInt, paramInterpolator);
    return this;
  }
  
  public AnimationProps setListener(Animator.AnimatorListener paramAnimatorListener)
  {
    this.mListener = paramAnimatorListener;
    return this;
  }
  
  public AnimationProps setStartDelay(int paramInt1, int paramInt2)
  {
    if (this.mPropStartDelay == null) {
      this.mPropStartDelay = new SparseLongArray();
    }
    this.mPropStartDelay.append(paramInt1, paramInt2);
    return this;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\views\AnimationProps.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */